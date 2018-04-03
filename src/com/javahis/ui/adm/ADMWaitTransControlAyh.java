package com.javahis.ui.adm;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMSQLTool;
import jdo.adm.ADMWaitTransTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

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
public class ADMWaitTransControlAyh extends TControl {
	TParm patInfo = null;// 待转入的病患信息
	TParm admPat = null;// 在院的病患信息
	TParm admInfo;// 记录病区床位状态

	public void onInit() {
		super.onInit();
		pageInit();
		this.messageBox("爱育华转入");
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
		    		if(inTable.getValueAt(i, 2).equals(bedNo))
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
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 1));
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
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
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

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
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
		table.setParmValue(admInfo);
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
        updata.setData("BED_NO", checkIn.getValueAt(checkIn.getSelectedRow(), 1));
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
        // 病患转出病区 wanglong add 20140728
        updata.setData("OUT_STATION_CODE", ds.getItemString(waitIndex, "OUT_STATION_CODE"));
		// 床位所在病区
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
        // 病患转出科室 wanglong add 20140728
        updata.setData("OUT_DEPT_CODE", ds.getItemString(waitIndex, "OUT_DEPT_CODE"));
		// 床位所在科室
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
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
		String bedMr = checkIn.getValueAt(checkIn.getSelectedRow(), 3) == null ? ""
				: checkIn.getValueAt(checkIn.getSelectedRow(), 3).toString();
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
		if (table.getValueAt(selectRow, 3) == null) {
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

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
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

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
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
		//血糖
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");	
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		if (resultParm.getErrCode() < 0)
		  messageBox(resultParm.getErrText());
	} 
	  //$$==========liuf==========$$//
	

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
              
              // add by wangbin 20141017 取消住院删除临时表对应的数据 START
              this.cancelMroRegAppointment(caseNo);
              // add by wangbin 20141017 取消住院删除临时表对应的数据 END
              
              // add by wangbin 20141017 针对已出库后再取消住院的数据进行归还日期设定 START
              this.updateRtnDateByCaseNo(caseNo);
              // add by wangbin 20141017 针对已出库后再取消住院的数据进行归还日期设定 END
              
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

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
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
	 * 病历待出库取消
	 * 
	 * @param caseNo
	 *            就诊号
	 * @author wangbin 2014/10/17
	 */
	private void cancelMroRegAppointment(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(
				parm, null);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
	
	/**
	 * 针对已出库后再取消住院的数据进行归还日期设定
	 * 
	 * @param caseNo
	 *            就诊号
	 * @author wangbin 2014/10/17
	 */
	private void updateRtnDateByCaseNo(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);

		TParm result = MROQueueTool.getInstance().updateRtnDateByCaseNo(parm);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
}
