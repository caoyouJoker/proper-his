package com.javahis.ui.adm;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.adm.ADMResvTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.adm.ADMXMLTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SYSBedTool;
import jdo.adm.ADMSQLTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.inw.InwForOutSideTool;
import jdo.sys.SystemTool;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: 入出转管理
 * </p>
 *
 * <p>
 * Description: 入出转管理    
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008  
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMWaitTransControl extends TControl {
	TParm patInfo = null;// 待转入的病患信息
	TParm admPat = null;// 在院的病患信息
	TParm admInfo;// 记录病区床位状态
	TTable in;//在院病患信息表
	TTable inTable;//预计转入表
	TTable outTable;//预计转出表	
	public void onInit() {
		super.onInit();
		this.setValue("PRE_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), 1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59");
		inTable=(TTable) this.getComponent("TABLE_IN");
		outTable=(TTable) this.getComponent("TABLE_OUT");
		in=(TTable) this.getComponent("in");
		pageInit();
	}

	/**
	 * 页面初始化
	 */
	private void pageInit() {
		//============add  by  chenxi
		callFunction("UI|WAIT_IN|addEventListener",
                "WAIT_IN->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		onQuery();
		onInit_Dept_Station();
		initInStation();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					chose();
				} catch (Exception e) {
				}
			}
		});
	}
	//====================chenxi add  
	public void onTABLEClicked(int row ){
		if (row < 0)
            return;
		TTable table = (TTable)this.getComponent("WAIT_IN") ;
		TTable inTable = (TTable)this.getComponent("in");
		int selectRow = 0 ;
		String bedNo= table.getValueAt(row, 4).toString().trim() ;
		if(!bedNo.equals("") || bedNo.length()<0){
		
			int check =	this.messageBox("消息", "此病患有预约"+bedNo+"床号,是否对号入住?", 0) ;
		    if(check!=0){
		    	String updatesql = "UPDATE SYS_BED SET APPT_FLG = 'N'  WHERE BED_NO_DESC = '"+bedNo+"'"  ;
				TParm bedParm = new TParm(TJDODBTool.getInstance().update(updatesql)) ;
				if (bedParm.getErrCode() < 0) { 
					this.messageBox("E0005");
					return;
				} 
				return ;
		    }
		    	
		    else {
		    	for(int i=0 ;i<inTable.getRowCount();i++){
		    		if(inTable.getValueAt(i, 3).equals(bedNo))//2-->3
		    			selectRow = i ;
		    	}
		    	this.onCheckin(selectRow) ;
		    }
		    	
		}
	
	}

	/**
	 * 安排病患入床
	 */
	public void onCheckin(int selectRow) {
		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到待转入DS
		TDataStore ds = waitIn.getDataStore();
		// 得到在院病患table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		checkIn.setSelectedRow(selectRow);
		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {
			this.messageBox_("请选择要入住的病患!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// 在院病患列表选中行号
		if (checkIndex < 0) {
			this.messageBox_("请选择要入住的床位!");
			return;
		}
		
		String mrNo=ds.getItemString(waitIndex, "MR_NO");
		String sql="SELECT EXEC_FLG FROM ADM_PRETREAT  WHERE MR_NO = '"+mrNo+"'";
		TParm data=new TParm(TJDODBTool.getInstance().select(sql));
		sql ="SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm data1=new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(data.getValue("EXEC_FLG",0))){
			if(!mrNo.equals(in.getParmValue().getValue("PRE_MRNO",in.getSelectedRow()))){
				if(JOptionPane.showConfirmDialog(null, "该床位不是预约床位是否继续？", "信息",
	    				JOptionPane.YES_NO_OPTION) == 0){//选择的床位与预约的床位不相同，则 提示
				}else{
					return;
				}
			}
		}
		String selectSql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
		" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",in.getSelectedRow())+"' " +
		" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(selectSql));
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				if(!data1.getValue("SEX_CODE",0).equals(parm.getValue("SEX_CODE",i))){
					if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
		    				JOptionPane.YES_NO_OPTION) == 0){
						
					}else{
						return;
					}
				}
			}
		}
		
		//刷新检核
		if(!check()){// shibl 20130117 add 
			return;
		}
		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}
		// 得到待转入选中行的数据
		TParm updata = new TParm();
		// 床位号
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));
		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// 待转入就诊号
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// 待转入住院号
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// 占床注记
		updata.setData("BED_STATUS", "1");
		// 床位所在病区
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// 床位号
		updata.setData("BED_NO",checkIn.getParmValue().getValue("BED_NO",checkIn.getSelectedRow()));
				//checkIn.getValueAt(checkIn.getValue(), 1));
		// 科室
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 5))//3-->5
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 3)//2-->3
									.toString())) {
				int check = this.messageBox("消息",
						"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
			} else {
				updata.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
			}
		} else {
			updata.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
	    String caseNo = ds.getItemString(waitIndex, "CASE_NO"); // wanglong add 20140731
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // 入床保存
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		}
		else {
			this.messageBox("P0005");
			
			//更新床位状态
			String upsql="UPDATE SYS_BED SET APPT_FLG='N' ,BED_STATUS='1'," +
					" PRETREAT_DATE='',PRE_MRNO=''," +
					" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO=''" +
					" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			upsql="UPDATE ADM_PRETREAT SET EXEC_FLG ='Y' WHERE MR_NO='"+mrNo+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			initInStation();
            // 床旁接口[A01(入院)、A02(转床)、A03(出院)] wanglong add 20140731
            TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A02");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
            }
            // 电视屏接口 wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
			sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}
	/**
	 * 更新所有信息
	 */
	public void onReload() {
		pageInit();
	}

	/**
	 * 查询病患的姓名，性别，出生日期
	 */
	public void onQuery() {
		TParm parm = new TParm();
		// =============pangben modify 20110512 start 添加区域查询
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// ===pangben modify 添加参数
		patInfo = ADMWaitTransTool.getInstance().selpatInfo(parm); // 待转入的病患信息
		admPat = ADMWaitTransTool.getInstance().selAdmPat(parm); // 在院的病患信息
	}

	/**
	 * 待转入转出 科室combo 点选事件
	 */
	public void chose() {
		this.onSelectIn();
		this.onSelectOut();
	}

	/**
	 * 待转入转出TABLE 显示
	 *
	 * @param tag
	 *            String
	 */
	public void creatDataStore(String tag) {
		Pat pat = null;
		TParm parm = new TParm();
		TParm result = new TParm();
		if (patInfo == null)
			return;
		TTable table = (TTable) this.callFunction("UI|" + tag + "|getThis");
		String mrNo = "";//
		String caseNo = "";//
		//System.out.println("row count===="+table.getRowCount());
		Timestamp date=SystemTool.getInstance().getDate() ;   //=======  chenxi modify 20130228
		/**
		 * 循环table 显示病患姓名，性别，年龄
		 */
		for (int i = 0; i < table.getRowCount(); i++) {
			// 拿到table中的值
			mrNo = table.getValueAt(i, 0).toString().trim();
			caseNo = table.getValueAt(i, 1).toString().trim();
			parm = new TParm();
			result = new TParm();
			pat = new Pat();
			// 得到pat对象拿到生日
			pat = pat.onQueryByMrNo(mrNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			result = ADMInpTool.getInstance().selectBedNo(parm) ;
			// 得到病患年龄
			String[] AGE = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			//=================  chenxi modify 20130228
			// 向table赋值
			if(tag.equals("WAIT_IN")){
			table.setValueAt(pat.getName(), i, 2);
			table.setValueAt(pat.getSexCode(), i, 3);
			table.setValueAt(result.getValue("BED_NO_DESC", 0), i, 4);   //=====预约床号
			table.setValueAt(AGE[0], i, 5);
			}
			else {
			table.setValueAt(pat.getName(), i, 3);
			table.setValueAt(pat.getSexCode(), i, 4);
			table.setValueAt(AGE[0], i, 5);}

		}
	}

	/**
	 * 在院病患点选事件
	 */
	public void onInStation() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("请选择病患！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// 配参
		sendParm.setData("ADM", "ADM");
		// 病案号
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// 住院号
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// 就诊号
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// 姓名
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// 性别
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// 年龄
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// 床号
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// 科室
		sendParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// 病区
		sendParm.setData("STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// 经治医师
		sendParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", selectRow));
		// 主治医师
		sendParm.setData("ATTEND_DR_CODE",
				parm.getData("ATTEND_DR_CODE", selectRow));
		// 科主任
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// 主管护士
		sendParm.setData("VS_NURSE_CODE",
				parm.getData("VS_NURSE_CODE", selectRow));
		// 入院状态
		sendParm.setData("PATIENT_CONDITION",
				parm.getData("PATIENT_CONDITION", selectRow));
		// 科主任
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG",
				parm.getData("BED_OCCU_FLG", selectRow));
		// 保存按钮状态
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		
		sendParm.setData("PRETREAT_OUT_NO", in.getParmValue().getValue("PRETREAT_OUT_NO",in.getSelectedRow()));//预转号
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm); 
		initInStation();
		
	}
	
	/**
	 * 在院病患信息查询
	 */
	public void initInStation() {
		TParm parm = new TParm();
		parm.setData("STATION_CODE", getValue("STATION_CODE").toString()==null?"":getValue("STATION_CODE").toString());
		//==================shibl
//		parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString()==null?"":getValue("DEPT_CODE").toString());
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
//		System.out.println("-----------parm-----"+parm);
		admInfo = ADMInpTool.getInstance().queryInStation(parm);
//		System.out.println("-------admInfo--1------------"+admInfo);
		if (admInfo.getErrCode() < 0) {
			this.messageBox_(admInfo.getErrText());
			return;
		}
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		// 如果病床没有住人 把年龄的0去掉
		for (int i = 0; i < admInfo.getCount(); i++) {

			if (admInfo.getInt("AGE", i) == 0) {
				// 如果住有病患并且此病床不是包床 年龄为0岁 那么自动加一
				if (admInfo.getValue("MR_NO", i).length() > 0
						&& !admInfo.getValue("BED_STATUS", i).equals("3"))
					admInfo.setData("AGE", i, "1");
				else
					// 没有病患在床将0改为空
					admInfo.setData("AGE", i, "");
			} else {
				// 得到病患年龄========pangb 2011-11-18 获得年龄一致
				String[] AGE = StringTool.CountAgeByTimestamp(
						admInfo.getTimestamp("BIRTH_DATE", i),
						admInfo.getTimestamp("IN_DATE", i));
				admInfo.setData("AGE", i, AGE[0]);
			}
			if (admInfo.getData("IN_DATE", i) != null
					&& admInfo.getValue("MR_NO", i).length() > 0
					&& !admInfo.getValue("BED_STATUS", i).equals("3")) {
				int days = StringTool.getDateDiffer(SystemTool.getInstance()
						.getDate(), admInfo.getTimestamp("IN_DATE", i));
				if (days > 0) {
					admInfo.setData("DAYNUM", i, days);
				} else {
					admInfo.setData("DAYNUM", i, "1");
				}
			} else
				admInfo.setData("DAYNUM", i, "");

		}
//		System.out.println("-------admInfo--------------"+admInfo);
		//table.setParmValue(admInfo);
		tableColor(admInfo);
		//统计床位
		int nullBed=0;
		int notNullBed=0;
		for(int i=0;i<admInfo.getCount();i++){
			if("0".equals(admInfo.getValue("BED_STATUS",i))){
				nullBed++;
			}else{
				notNullBed++;
			}
		}
		
		this.setValue("FILL_NUM", notNullBed+"");//占床
		this.setValue("NULL_NUM", nullBed+"");//空床
		initPre();
	}
	/**
	 * 预约数据查询
	 */
	public void initPre(){
		String date=SystemTool.getInstance().getDate().toString().substring(0,19).replaceAll("-", "/");
		TParm inParam=new TParm();
		TParm outParm=new TParm();
		if (this.getValue("STATION_CODE") != null
				&& !"".equals(this.getValue("STATION_CODE"))) {
			inParam.setData("PRETREAT_IN_STATION", this
					.getValue("STATION_CODE"));
			outParm.setData("PRETREAT_OUT_STATION", this
					.getValue("STATION_CODE"));
		}
		
		if (this.getValue("PRE_DATE") != null
				&& !"".equals(this.getValue("PRE_DATE"))) {
			inParam.setData("START_DATE", date);
			inParam.setData("END_DATE", this.getValue("PRE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			outParm.setData("START_DATE", date);
			outParm.setData("END_DATE", this.getValue("PRE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			
		}
		//inParam.setData("EXEC_FLG","N");
		initPreIn(inParam);
		initPreOut(outParm);
	}
	/**
	 * 预约数据查询
	 */
	public void initPreIn(TParm parm){
		TParm result = ADMResvTool.getInstance().queryPretreat(parm);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		
		String sql="SELECT MR_NO  FROM ADM_WAIT_TRANS WHERE IN_STATION_CODE='"+this.getValueString("STATION_CODE")+"'";
		TParm countParm=new TParm(TJDODBTool.getInstance().select(sql));
		int inNum=(result.getCount()>0?result.getCount():0)+(countParm.getCount()>0?countParm.getCount():0);
		this.setValue("PRE_IN_NUM", inNum+"");
		inTable.setParmValue(result);
	}
	/**
	 * 预转数据查询
	 */
	public void initPreOut(TParm parm){
		TParm result = ADMResvTool.getInstance().queryPretreat(parm);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		outTable.setParmValue(result);
		
		if(result.getCount()>0){
			this.setValue("PRE_OUT_NUM", result.getCount()+"");//待转出
		}else{
			this.setValue("PRE_OUT_NUM", 0+"");//待转出
		}
	}
	/**
	 * 待转入科室COMBO点选事件
	 */
	public void onSelectIn() {
		//=========modify lim 20120323 begin
//		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
//		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", ""));
//		WAIT_IN.retrieve();		
		//=========modify lim 20120323 end
		
		String filter = "";
		if (this.getValueString("IN_STATION_CODE").length() > 0)
			filter += " IN_STATION_CODE ='" + this.getValueString("IN_STATION_CODE")
					+ "'";
		if (this.getValueString("IN_DEPT_CODE").length() > 0)
			filter += " AND IN_DEPT_CODE ='"
					+ this.getValueString("IN_DEPT_CODE") + "'";
		TTable table = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		table.getDataStore().showDebug();
		creatDataStore("WAIT_IN");

	}

	/**
	 * 待转出科室COMBO点选事件
	 */
	public void onSelectOut() {
		String filter = "";
		if (this.getValueString("OUT_STATION_CODE").length() > 0)
			filter += " OUT_STATION_CODE ='"
					+ this.getValueString("OUT_STATION_CODE") + "'";
		if (this.getValueString("OUT_DEPT_CODE").length() > 0)
			filter += " AND OUT_DEPT_CODE ='"
					+ this.getValueString("OUT_DEPT_CODE") + "'";

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		creatDataStore("WAIT_OUT");
	}

	/**
	 * 安排病患入床
	 */
	public void onCheckin() {
		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到待转入DS
		TDataStore ds = waitIn.getDataStore();
		// 得到在院病患table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {
			this.messageBox_("请选择要入住的病患!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// 在院病患列表选中行号
		if (checkIndex < 0) {
			this.messageBox_("请选择要入住的床位!");
			return;
		}
		
		String mrNo=ds.getItemString(waitIndex, "MR_NO");
		String sql="SELECT EXEC_FLG FROM ADM_PRETREAT  WHERE MR_NO = '"+mrNo+"'";
		TParm data=new TParm(TJDODBTool.getInstance().select(sql));
		sql ="SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm data1=new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(data.getValue("EXEC_FLG",0))){
			if(!mrNo.equals(in.getParmValue().getValue("PRE_MRNO",in.getSelectedRow()))){
				if(JOptionPane.showConfirmDialog(null, "该床位不是预约床位是否继续？", "信息",
	    				JOptionPane.YES_NO_OPTION) == 0){//选择的床位与预约的床位不相同，则 提示
				}else{
					return;
				}
			}
		}
		String selectSql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
		" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",in.getSelectedRow())+"' " +
		" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(selectSql));
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				if(!data1.getValue("SEX_CODE",0).equals(parm.getValue("SEX_CODE",i))){
					if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
		    				JOptionPane.YES_NO_OPTION) == 0){
						break;
					}else{
						return;
					}
					
				}
			}
		}
		
		
		
		//刷新检核
		if(!check()){// shibl 20130117 add 
			return;
		}
		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}
		//========================  chenxi modify 20130228 
//		// 得到待转入入病患数据
//		if ("1".equals(checkIn.getValueAt(checkIn.getSelectedRow(), 0))) {
//			this.messageBox_("此床以占用！");
//			return;
//		}
//		// 判断选中的床号是否已经被预定
//		if (admInfo.getBoolean("APPT_FLG", checkIndex)) {
//			int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
//			if (check != 0) {
//				return;
//			}
//		}
//		// 判断选中的床号是否被包床
//		if (admInfo.getBoolean("BED_OCCU_FLG", checkIndex)) {
//			this.messageBox_("此床位已被包床，不可入住！");
//			return;
//		}
		// 得到待转入选中行的数据
		TParm updata = new TParm();
		// 床位号
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));//????
		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// 待转入就诊号
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// 待转入住院号
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// 占床注记
		updata.setData("BED_STATUS", "1");
		// 床位所在病区
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// 床位号
		updata.setData("BED_NO",checkIn.getParmValue().getValue("BED_NO",checkIn.getSelectedRow()));
				//checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		// 科室
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
			if (checkIn.getValueAt(checkIndex, 5) == null//3-->5
					|| "".equals(checkIn.getValueAt(checkIndex, 5))//3-->5
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 3)//2-->3
									.toString())) {
				int check = this.messageBox("消息",
						"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
			} else {
				updata.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
			}
		} else {
			updata.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
        String caseNo = ds.getItemString(waitIndex, "CASE_NO"); // wanglong add 20140731
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // 入床保存
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} 
		else {
			this.messageBox("P0005");
			
			
			
			//更新床位状态
			String upsql="UPDATE SYS_BED SET APPT_FLG='N' ,BED_STATUS='1'," +
					" PRETREAT_DATE='',PRE_MRNO=''," +
					" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO=''" +
					" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			upsql="UPDATE ADM_PRETREAT SET EXEC_FLG ='Y' WHERE MR_NO='"+mrNo+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			initInStation();
			
            // 床旁接口[A01(入院)、A02(转床)、A03(出院)] wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A02");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
            }
            // 电视屏接口 wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            sendHL7Mes(updata);
            initInStation();
			chose();
		}
	}
	
	/**
	 * 检查某一病患是否包床
	 *
	 * @param caseNo
	 *            String
	 * @return boolean true：包床 false：未包床
	 */
	public boolean checkOccu(String caseNo) {
		TParm qParm = new TParm();
		qParm.setData("CASE_NO", caseNo);
		TParm occu = SYSBedTool.getInstance().queryAll(qParm);
		int count = occu.getCount("BED_OCCU_FLG");
		String check = "N";
		for (int i = 0; i < count; i++) {
			if ("Y".equals(occu.getData("BED_OCCU_FLG", i))) {
				check = "Y";
			}
		}
		if ("Y".equals(check)) {
			return true;
		} else {
			return false;
		}
	}
	/**
     * 检核床位
     * @return boolean
     */
    public boolean check() {
        TTable table = (TTable)this.callFunction("UI|in|getThis");
        TTable waitTable = (TTable)this.callFunction("UI|WAIT_IN|getThis");  //chenxi modify 20130308
        if (table.getSelectedRow() < 0) {
            this.messageBox("未选床位");
            return false;
        }
        //=============shibl 20130106 add======多人点同一个床未刷新页面=============================
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        if (ALLO_FLG.equals("Y")) {
            this.messageBox("此床已占用");
            onReload();
            return false;
        }
        if (BED_STATUS.equals("1")) {
            this.messageBox("此床已被包床");
            onReload();
            return false;
        }
        //=================  chenxi modify 20130308
        if (APPT_FLG.equals("Y")) {
        	if(!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(parm.getValue("BED_NO_DESC"))){
        		int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
    			if (check != 0) {
    				onReload();
    				return  false;
    			}
                return true;
        	}
        	
        }
        return true;
    }
	/**
	 * 病患入住检核
	 *
	 * @return boolean
	 */
	public boolean checkSysBed() {
		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到在院病患table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		String waitMr = waitIn.getValueAt(waitIn.getSelectedRow(), 0)
				.toString();
		String bedMr = checkIn.getValueAt(checkIn.getSelectedRow(), 5) == null ? ""//3-->5
				: checkIn.getValueAt(checkIn.getSelectedRow(), 5).toString();//3-->5
		if (bedMr == null || "".equals(bedMr))
			return true;
		else if (waitMr.equals(bedMr)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转科管理
	 */
	public void onOutDept() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow == -1) {
			this.messageBox("请选择在院病患！");
			return;
		}
		if (table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox("请选择在院病患！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}

		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox("此病患未入住！");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// 病案号
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// 住院号
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// 就诊号
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// 姓名
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// 性别
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// 年龄
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// 床号
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// 科室
		sendParm.setData("OUT_DEPT_CODE", parm.getData("DEPT_CODE", selectRow));
		// 病区
		sendParm.setData("OUT_STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// 入院时间
		sendParm.setData("IN_DATE", parm.getData("IN_DATE", selectRow));
		sendParm.setData("PRE_FLG",parm.getData("PRE_FLG", selectRow));//传入预转标记
		//sendParm.setData("BED_NO",parm.getData("BED_NO", selectRow));//传入床号
		sendParm.setData("PRETREAT_OUT_NO",parm.getData("PRETREAT_OUT_NO", selectRow));//获取预转登记号时
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		//System.out.println("=======WAIT_OUT======"+outTable.getFilter());
		//$$=========add by lx===========$$//
		//outTable.retrieve();
		//onSelectOut();
		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(this.getValueString("OUT_DEPT_CODE"), this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();
		//$$==================$$//
		creatDataStore("WAIT_OUT");
	}

	/**
	 * 包床管理
	 */
	public void onBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("请选择病患！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		sendParm.setData("MR_NO", admInfo.getValue("MR_NO", selectRow));
		sendParm.setData("IPD_NO", admInfo.getValue("IPD_NO", selectRow));
		sendParm.setData("DEPT_CODE", admInfo.getValue("DEPT_CODE", selectRow));
		sendParm.setData("STATION_CODE",
				admInfo.getValue("STATION_CODE", selectRow));
		sendParm.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm bed = new TParm();
		bed.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = admInfo.getValue("CASE_NO", selectRow);
		int count = check.getCount("BED_NO");
		boolean flg = false ;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true ;
		}
			}
		if(flg==true){
			int checkFlg=	this.messageBox("消息","此病房已有其他病患!是否继续包床？",0);
			if(checkFlg!=0)
				return;
			}	
		
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
		initInStation();
		chose();
	}

	/**
	 * 初始化带入默认科室 诊区
	 */
	public void onInit_Dept_Station() {
		String userId = Operator.getID();
		String station = Operator.getStation();
		String dept = Operator.getDept();
		TComboBox admstation = (TComboBox) this.getComponent("STATION_CODE");
		TParm Station = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admstation.setParmValue(Station);
		admstation.onQuery();
		TComboBox admdept = (TComboBox) this.getComponent("DEPT_CODE");
		TParm Dept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admdept.setParmValue(Dept);
		admdept.onQuery();
		TComboBox in_station = (TComboBox) this.getComponent("IN_STATION_CODE");
		TParm inStation = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		in_station.setParmValue(inStation);
		in_station.onQuery();
		TComboBox out_station = (TComboBox) this.getComponent("OUT_STATION_CODE");
		//===========modify lim  begin
		//TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationList(userId)));
		TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationListForDynaSch()));
		//===========modify lim  end
		out_station.setParmValue(outStaion);
		out_station.onQuery();
		TComboBox in_dept = (TComboBox) this.getComponent("IN_DEPT_CODE");
		TParm inDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		in_dept.setParmValue(inDept);
		in_dept.onQuery();
		TComboBox out_dept = (TComboBox) this.getComponent("OUT_DEPT_CODE");
		TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptList(userId)));
		//TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptListForDynaSch()));
		out_dept.setParmValue(outDept);
		out_dept.onQuery();
		// 待转入和待转出 Grid 赋值
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();
		// 根据用户设置默认科室和病区
		setValue("IN_DEPT_CODE", "");
		setValue("DEPT_CODE", dept);
		setValue("OUT_DEPT_CODE", "");
		setValue("IN_STATION_CODE", station);
		setValue("STATION_CODE", station);
		setValue("OUT_STATION_CODE", station);
	}

	/**
	 * 取消包床
	 */
	public void onCancelBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("请选择病患！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		int re = this.messageBox("提示", "确认要取消该病患的包床吗？", 0);
		if (re != 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initInStation();
		chose();
	}

    //$$==========liuf==========$$//
	/**
	 * CIS，血糖病患入住发送
	 * @param parm
	 */
	private void sendHL7Mes(TParm parm) {
		System.out.println("sendHL7Mes()");
		// ICU、CCU注记
		String caseNO = parm.getValue("CASE_NO");		
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		String type="ADM_IN";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU||IsCCU)
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CIS");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		////////////////////////////////////////////zhangs add start
		//输液泵
		System.out.println("输液泵:"+this.checkIsCS5(caseNO));
		if (this.checkIsCS5(caseNO))
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CS5");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		///////////////////////////////////////////////zhangs add end
		//血糖
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");	
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		if (resultParm.getErrCode() < 0)
		  messageBox(resultParm.getErrText());

		// add by wangb 2017/3/24 医院同时启用诺瓦和强生两个血糖厂商接口，一份消息同时给两个厂商发送
		// 给强生血糖接口发送消息 START
		list = new ArrayList();
		parm.setData("SEND_COMP", "JNJ");
		list.add(parm);
		resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
		// 给强生血糖接口发送消息 END
		
		//add by lij 2017/04/11 增加 NIS的HL7消息
//		this.messageBox("1111");
		list = new ArrayList();
		parm.setData("SEND_COMP", "NIS");
		list.add(parm);
		resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
//		this.messageBox("2222");
	} 
	  //$$==========liuf==========$$//
//////////////////////////////////////////////zhangs add start
	/**
	 * 是否是CS5
	 * @param parm
	 * @return
	 */
	public boolean checkIsCS5(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean cs5Flg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query(inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        cs5Flg=result.getBoolean("CS5_FLG",0);
		return cs5Flg;
	}


	private TParm query(TParm inparm) {
		String Sql =" SELECT B.CS5_FLG "+
		" FROM ADM_INP A,SYS_DEPT B "+
		" WHERE A.CASE_NO='"+inparm.getValue("CASE_NO")+"' "+
		" AND B.DEPT_CODE=A.DEPT_CODE ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("CS5_FLG") < 0) {
			this.messageBox("没有查询到相应记录");
			return null;
		}
		return tabParm;
	}
//////////////////////////////////////////////////////zhangs add end
	/**
	 * 取消转科
	 * @param parm 
	 */	
	public void onCancelTrans(){ 
		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int selectRow = table.getSelectedRow();
		if(selectRow<0){
			this.messageBox("请选择要取消转科的病患.");
			return ;
		}
		String caseNo = (String)table.getValueAt(selectRow, 2) ;
		TParm parm = new TParm() ;
		parm.setData("OPT_USER",Operator.getID()) ;
		parm.setData("OPT_TERM",Operator.getIP()) ;
		parm.setData("DATE",SystemTool.getInstance().getDate()) ;
		parm.setData("CASE_NO", caseNo);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onUpdateTransAndLog", parm); 	
		if(result.getErrCode()<0){
			messageBox("取消转科失败.") ;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");			
			messageBox("取消转科成功.") ;
			
			// add by wangb 2015/11/27 取消转科发送大屏消息 START
            TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            // add by wangb 2015/11/27 取消转科发送大屏消息 END
		}
	}
	/**
	 * 取消住院         chenxi   modify  20130417
	 */
	public void onCancelInHospital(){
		 if (!checkDate())
	            return;
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		TParm result = new TParm();
		//=================执行取消住院操作
      int check = this.messageBox("消息", "是否取消？", 0);
      if (check == 0) {
          TParm parm = new TParm();
          parm.setData("CASE_NO", caseNo);
          parm.setData("PSF_KIND", "INC");
          parm.setData("PSF_HOSP", "");
          parm.setData("CANCEL_FLG", "Y");
          parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
          parm.setData("CANCEL_USER", Operator.getID());
          parm.setData("OPT_USER", Operator.getID());
          parm.setData("OPT_TERM", Operator.getIP());
          if (null != Operator.getRegion()
              && Operator.getRegion().length() > 0) {
              parm.setData("REGION_CODE", Operator.getRegion());
          }
         result = TIOM_AppServer.executeAction(
                  "action.adm.ADMInpAction", "ADMCanInp", parm); //
          if (result.getErrCode() < 0) {
              this.messageBox("E0005");
          } else {
              this.messageBox("P0005");
                // 床旁接口 wanglong add 20140731
                TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A04");
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
                }
                xmlParm = ADMXMLTool.getInstance().creatXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
                }
                // 电视屏接口 wanglong add 20141010
                xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
                }
                xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
                }
              initInStation();
              chose();
          }
	}
	}
	  /**
     * 检核数据
     *
     * @return boolean
     */
    public boolean checkDate() {
    	TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("请选择病患！");
			return false;
		}
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		//==============预交金未退,不可取消住院
		 TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName()) ;
            return false;
        }
        if (result.getDouble("PRE_AMT", 0) > 0) {
        	 this.messageBox("此病患还有预交金未退,不可取消住院");
            return false;
        }
        //==================以计费不可取消住院
        boolean checkflg =  IBSOrdermTool.getInstance().existFee(tableParm.getRow(selectRow));
        if(!checkflg){
      	  messageBox("已产生费用,不可取消住院") ;
      	  return false;
        }
        // 检查医生是否开立医嘱
     TParm    parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        if (this.checkOrderisEXIST(parm)) {    
        	this.messageBox( "该病患已开立医嘱，不可取消住院！");
        	  callFunction("UI|save|setEnabled", false);
        	  return false  ;     
        }
      
    	return true ;
    }
    /**
	 * 检查该病人是否开立医嘱
	 * 
	 * 
	 */
	public boolean checkOrderisEXIST(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND DC_DATE IS NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 取消入科
	 */
	public void  onCancleInDP(){
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("请选择病患！");
			return ;
		}
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ;
		TParm parm=new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(parm);
		if(tranLogDept.getCount()<=0){
			this.messageBox("查询转科记录错误");
			return;
		}
		if(!isEnableCancleInDP(tranLogDept.getRow(0))){
			return;
		}
		TParm trandParm=tableParm.getRow(selectRow);
		trandParm.setData("OPT_USER", Operator.getID());
		trandParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onCancleInDP", trandParm);
		if(result.getErrCode()<0){
			this.messageBox("执行失败");
			return;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");	
		    this.messageBox("执行成功");
		    
		    // add by wangb 2015/11/27 取消入科发送大屏消息 START
            TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
            }
            // add by wangb 2015/11/27 取消转科发送大屏消息 END
		}
		
	}
	/**
	 * 验证取消入科
	 * @param caseNo
	 * @return
	 */
	public  boolean  isEnableCancleInDP(TParm parm){
		if(InwForOutSideTool.getInstance().checkOrderisExistExec(parm)){
			this.messageBox("存在入科后已执行的医嘱,不能取消入科");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExistCheck(parm)){
			this.messageBox("存在入科后已审核的医嘱,不能取消入科");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExist(parm)){
			this.messageBox("存在入科后已开立的医嘱,不能取消入科");
			return false;
		}
		TParm parmfee=InwForOutSideTool.getInstance().checkOrderFee(parm);
		if(parmfee.getDouble("TOT_AMT", 0)!=0){
			this.messageBox("入科后的总费用为:"+parmfee.getDouble("TOT_AMT", 0)+",不能取消入科");
			return false;
		}
		return true;
	}
	/**
	 * 预登记
	 */
	public void onAdmPreInp(){
		int patRow=in.getSelectedRow();
		TParm tableParm=in.getParmValue();
		if(patRow<0){
			this.messageBox("请选择一条在院数据");
			return;
		}
		if("".equals(in.getParmValue().getValue("MR_NO",patRow))){
			this.messageBox("该床位没有在院病患信息");
			return;
		}
		if("Y".equals(tableParm.getValue("PRE_FLG",patRow))){
			this.messageBox("该病患已经预转");
			return;
		}
		TParm parm=new TParm();
		parm.setData("MR_NO",tableParm.getValue("MR_NO",patRow));
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO",patRow)); //add by huangtt 20170502 添加CASE_NO
		parm.setData("IPD_NO",tableParm.getValue("IPD_NO",patRow));
		parm.setData("PAT_NAME",tableParm.getValue("PAT_NAME",patRow));
		parm.setData("SEX_CODE",tableParm.getValue("SEX_CODE",patRow));
		parm.setData("AGE",tableParm.getValue("AGE",patRow));
		parm.setData("BED_NO",tableParm.getValue("BED_NO",patRow));
		parm.setData("PRETREAT_OUT_DEPT",this.getValue("DEPT_CODE"));
		parm.setData("PRETREAT_OUT_STATION",this.getValue("STATION_CODE"));
		parm.setData("NURSING_CLASS_CODE",tableParm.getValue("NURSING_CLASS",patRow));
		parm.setData("PATIENT_CONDITION",tableParm.getValue("PATIENT_CONDITION",patRow));
		this.openDialog("%ROOT%\\config\\adm\\ADMPreInp.x",parm);
		initInStation();
	}
	
	/**
	 * 取消预登记 
	 */
	public void onCancelPreInp(){
		int patRow=in.getSelectedRow();
		TParm tableParm=in.getParmValue();
		if(patRow<0){
			this.messageBox("请选择一条在院数据");
			return;
		}
		
		if("".equals(in.getParmValue().getValue("MR_NO",patRow))){
			this.messageBox("该床位没有在院病患信息");
			return;
		}
		if(!tableParm.getValue("PRE_FLG",patRow).equals("Y")){
			this.messageBox("该病患没有预转信息");
			return;
		}
		String sql="DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO='"+tableParm.getValue("PRETREAT_OUT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql="UPDATE SYS_BED SET PRE_FLG='N' , PRETREAT_OUT_NO='' WHERE PRETREAT_OUT_NO='"+tableParm.getValue("PRETREAT_OUT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("取消成功");
		// add by huangtt 20170502 start 发送病患基本信息
		TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(tableParm.getValue("CASE_NO",patRow));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
		}
		// add by huangtt 20170502 end 发送病患基本信息
		initInStation();
	}
	/**
	 * 计算年龄
	 * 
	 * @param date
	 * @return
	 */
	private String patAge(Timestamp date) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = date == null ? sysDate : date;
		String age = "0";
		age = OdiUtil.showAge(temp, sysDate);
		return age;
	}
	
	/**
	 * 预约 
	 */
	public void onPre(){
		int inRow=inTable.getSelectedRow();
		int patRow=in.getSelectedRow();
		if(inRow<0){
			this.messageBox("请选择预约数据");
			return;
		}
		if(patRow<0){
			this.messageBox("请选择床位数据");
			return ;
		}
		if(!"".equals(in.getParmValue().getValue("PRE_MRNO",patRow))){
			this.messageBox("该床已经被预约");
			return;
		}
		if("Y".equals(inTable.getParmValue().getValue("EXEC_FLG",inRow))){
			this.messageBox("此病患已经预约");
			return;
		}
		String patSex=in.getParmValue().getValue("SEX_CODE",patRow);
		String preSex=inTable.getParmValue().getValue("SEX_CODE",inRow);
		String date=SystemTool.getInstance().getDate().toString().replaceAll("-", "/").substring(0,19);
		TParm tableParm=inTable.getParmValue();
		if(!"".equals(in.getParmValue().getValue("MR_NO",patRow))){//该床已有病患
			
			if(!preSex.equals(patSex)){
				
				if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
	    				JOptionPane.YES_NO_OPTION) == 0){
					
				}else{
					return;
				}
			}
			String sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='Y' WHERE PRETREAT_NO='"+inTable.getParmValue().getValue("PRETREAT_NO",inTable.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			sql="UPDATE SYS_BED SET APPT_FLG='Y',PRETREAT_DATE=TO_DATE('"+inTable.getParmValue().
			getValue("PRETREAT_DATE",inTable.getSelectedRow()).toString().substring(0,19).replaceAll("-", "/")+"','yyyy/MM/dd HH24:mi:ss')," +
			" PRE_MRNO='"+tableParm.getValue("MR_NO",inRow)+"'," +
			" PRETREAT_TYPE='"+tableParm.getValue("PRETREAT_TYPE",inRow)+"'," +
			" PRE_PATNAME='"+tableParm.getValue("PAT_NAME",inRow)+"',PRE_SEX='"+tableParm.getValue("SEX_CODE",inRow)+"'," +
			" PRETREAT_NO='"+tableParm.getValue("PRETREAT_NO",inRow)+"' " +
			" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			this.messageBox("预约成功");
		}else{//该床没有病患,但是要保证同一房间里的病患性别相同
			String sql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
					" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",patRow)+"' " +
					" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
			TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				for(int i=0;i<parm.getCount();i++){
					if(!preSex.equals(parm.getValue("SEX_CODE",i))){
						if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
			    				JOptionPane.YES_NO_OPTION) == 0){
							break;
						}else{
							return;
						}
						
					}
				}
			}
			sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='Y' WHERE PRETREAT_NO='"+inTable.getParmValue().getValue("PRETREAT_NO",inTable.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			sql="UPDATE SYS_BED SET APPT_FLG='Y',PRETREAT_DATE=TO_DATE('"+inTable.getParmValue().
			getValue("PRETREAT_DATE",inTable.getSelectedRow()).toString().substring(0,19).replaceAll("-", "/")+"'," +
			"'yyyy/MM/dd HH24:mi:ss')," +
			" PRE_MRNO='"+tableParm.getValue("MR_NO",inRow)+"'," +
			" PRETREAT_TYPE='"+tableParm.getValue("PRETREAT_TYPE",inRow)+"'," +
			" PRE_PATNAME='"+tableParm.getValue("PAT_NAME",inRow)+"',PRE_SEX='"+tableParm.getValue("SEX_CODE",inRow)+"'," +
			" PRETREAT_NO='"+tableParm.getValue("PRETREAT_NO",inRow)+"' " +
			" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			this.messageBox("预约成功");
		}
		initInStation();
	}
	
	/**
	 * 取消预约
	 */
	public void onCancelPre(){
		int patRow=in.getSelectedRow();
		if(patRow<0){
			this.messageBox("请选择一条已预约的数据");
			return;
		}
		if("".equals(in.getParmValue().getValue("PRE_SEX_CODE",patRow))){
			this.messageBox("没有预约信息");
			return;
		}
		String sql="UPDATE SYS_BED SET APPT_FLG='N',PRETREAT_DATE='',PRE_MRNO=''," +
		" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='N' WHERE PRETREAT_NO='"+in.getParmValue().getValue("PRETREAT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("取消成功");
		initInStation();
	}
	/**
	 * 删除预约
	 */
	public void onDeletePre(){
		int patRow=in.getSelectedRow();
		if(patRow<0){
			this.messageBox("请选择一条已预约的数据");
			return;
		}
		if("".equals(in.getParmValue().getValue("PRE_SEX_CODE",patRow))){
			this.messageBox("没有预约信息");
			return;
		}
		String sql="UPDATE SYS_BED SET APPT_FLG='N',PRETREAT_DATE='',PRE_MRNO=''," +
		" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql=" DELETE ADM_PRETREAT WHERE  PRETREAT_NO='"+in.getParmValue().getValue("PRETREAT_NO",patRow)+"' ";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("删除成功");
		initInStation();
	}
	/**
	 * 刷新按钮
	 */
	public void onFresh(){
		initInStation();
	}
	/**
	 * 按照房间号 显示 白蓝色
	 * @param result
	 */
	public void tableColor(TParm result){
		in.setParmValue(result);
		in.setRowColor(0, Color.white);
		TParm tableParm=in.getParmValue();
		String romDesc=tableParm.getValue("ROOM_DESC",0);
		for(int i=1;i<result.getCount();i++){//按照房间号 间隔显示 白色 与蓝色
			if(tableParm.getValue("ROOM_DESC",i).equals(romDesc)){
				in.setRowColor(i, in.getRowColor(i-1));//房间号相同，则取上一行的颜色
			}else{
				if(in.getRowColor(i-1)==Color.white)//房间号 不同，则先判断上一行 的颜色是什么，若为白色，则下一行为蓝色
					in.setRowColor(i, Color.lightGray);
				else //若为蓝色，则下一行为白色
					in.setRowColor(i, Color.white);
				romDesc=tableParm.getValue("ROOM_DESC",i);
			}
		}
	}
	/**
	 * 生成交接单
	 */
	public void onCreate(){
		TParm action = new TParm();
		TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int row = waitOut.getSelectedRow();// 待转出表选中行号
		if (row < 0) {
			this.messageBox("请选择病患");
			return;	
		}
		TDataStore ds = waitOut.getDataStore();	
		action.setData("MR_NO", ds.getItemString(row, "MR_NO"));//病案号
		action.setData("CASE_NO", ds.getItemString(row,"CASE_NO"));//就诊号
		Pat pat = new Pat();
		pat = pat.onQueryByMrNo(ds.getItemString(row, "MR_NO"));		
		action.setData("PAT_NAME", pat.getName());//姓名
		action.setData("FROM_DEPT",ds.getItemString(row,"OUT_DEPT_CODE")); //转出科室
	    action.setData("TO_DEPT",ds.getItemString(row,"IN_DEPT_CODE")); //转入科室
	    // modify by wangb 2016/1/18 ICU生成ICU-病区交接单，其余生成病区-病区交接单
	    String transferClass = "";
	    String subClassCode = "";
		if ("I01,I02".contains(ds.getItemString(row, "OUT_STATION_CODE"))) {
			// ICU-病区
			transferClass = "IW";
			subClassCode = "EMR06030701";
		} else {
			// 病区-病区
			transferClass = "WW";
			subClassCode = "EMR0603011";
		}
	    action.setData("TRANSFER_CLASS",transferClass); //交接类型
		//查询模版信息
	    TParm actionParm = this.getEmrFilePath(subClassCode);
	    action.setData("TEMPLET_PATH",
	    		actionParm.getValue("TEMPLET_PATH",0));//交接单路径
	    action.setData("EMT_FILENAME",
	    		actionParm.getValue("EMT_FILENAME",0));//交接单名称
	    action.setData("FLG",false);//打开模版
//        System.out.println("---action----------------"+action);
	    //调用模版
	    this.openWindow("%ROOT%\\config\\emr\\EMRTransferWordUI.x", action);
	}
	/**
	 * 交接一览表
	 */
	public void onTransfer(){
		TParm parm = new TParm();
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {				
			TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			int row = waitOut.getSelectedRow();// 待转出表选中行号
			if (row < 0) {				
				TTable table = (TTable) this.callFunction("UI|in|getThis");
				int selectRow = table.getSelectedRow();
				if (selectRow < 0) {//出院病人或未选中病患
										
				}else{//已入住	
				TParm data = table.getParmValue();
			    parm.setData("MR_NO", data.getData("MR_NO", selectRow));// 病案号	
				parm.setData("CASE_NO", data.getData("CASE_NO", selectRow));// 就诊号
				}	
			}else{//带转出				
			TDataStore ds = waitOut.getDataStore();
			parm.setData("MR_NO", ds.getItemString(row, "MR_NO"));//病案号
			parm.setData("CASE_NO", ds.getItemString(row,"CASE_NO"));//就诊号
			}			
		}else {//带转入
			// 得到待转入DS
			TDataStore ds = waitIn.getDataStore();		
			parm.setData("MR_NO", ds.getItemString(waitIndex, "MR_NO"));//病案号
			parm.setData("CASE_NO", ds.getItemString(waitIndex,"CASE_NO"));//就诊号
		}
//		System.out.println("---parm----------------"+parm);	
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x",parm);
	}
	
	 /**
     * 得到EMR路径
     */
    public TParm getEmrFilePath(String subClassCode){
    	String sql=" SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE," +
    			   " A.TEMPLET_PATH FROM EMR_TEMPLET A"+
                   " WHERE A.SUBCLASS_CODE = '" + subClassCode + "'";
     	TParm result = new TParm();
    	result = new TParm(TJDODBTool.getInstance().select(sql)); 
//    	System.out.println("---result----------------getEmrFilePath"+result);
    	return result;
    }
}
