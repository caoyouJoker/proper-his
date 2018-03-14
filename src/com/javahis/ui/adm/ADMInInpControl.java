package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSBillmTool;
import jdo.inw.InwForOutSideTool;
import jdo.mro.MROTool;
import jdo.odi.OdiOrderTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ESign;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;

/**
 * <p>
 * Title:转科管理
 * </p>
 * 
 * <p>
 * Description:转科管理
 * </p>
 *
 * <p> 
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMInInpControl extends TControl {
	TParm acceptData = new TParm(); // 接参
	Pat pat = new Pat();
	String preFlg="";
	String bedNo="";
	String preOutNo="";
	private TWord word;
	private static final String TWORD = "WORD";


	public void onInit() {
		super.onInit();
		initWord();
		acceptData = (TParm)this.getParameter();
		this.setValueForParm("BED_NO;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;",
				acceptData);
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		TParm admInp = ADMTool.getInstance().getADM_INFO(parm);
		this.setValue("OUT_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
		this.setValue("OUT_STATION_CODE", admInp.getValue("STATION_CODE", 0));
		// 入院日期
		((TTextFormat)this.getComponent("IN_DATE")).setValue(acceptData
				.getData("IN_DATE"));
		// 预定日期
		setValue("OUT_DATE", SystemTool.getInstance().getDate());

		preFlg=acceptData.getValue("PRE_FLG");//获取预转标记
		//bedNo=acceptData.getValue("BED_NO");//获取床号
		preOutNo=acceptData.getValue("PRETREAT_OUT_NO");
	}

	/**
	 * 保存事件
	 */
	public void onSave() {
		String out = getValue("OUT_INP").toString();
		if ("Y".equals(out))
			this.outAdm();
		else
			this.InOutDept();
	}

	/**
	 * 转科保存
	 */
	public void InOutDept() {
		if (!checkDate())
			return;
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("BED_NO", this.getValue("BED_NO"));
		parm.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
		parm.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		parm.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		parm.setData("OUT_DATE", this.getValue("OUT_DATE"));
		parm.setData("PSF_KIND", "INDP");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// ==========pangben modify 20110617 start
		parm.setData("REGION_CODE", Operator.getRegion());
		//        //add by chenhj 2017.5.18
		//        int i=onCheckSign(acceptData.getData("CASE_NO").toString());
		//        if(i==2){
		//        	return;
		//        }

		// modified by Eric 20170606 start
		// 规培医师写的病历如果没有上级医师签名，需要提示，但不强制签名
		// 查询患者所有病历
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+acceptData.getData("CASE_NO").toString()+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); // 没有规培医师签名
		List<String> list2=new ArrayList<String>();// 没有上级医师签名
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			// 判断是否是规培医师写的病历
			if(isPracticerWrite(word)){// 是规培医师写的病历
				// 判断是否有规培医师签名
				if(isDrSign(word.getFileAuthor(), this.getUseName(word.getFileAuthor()), word)){// 有规培医师签名
					// 查询此病人的上级医师
					TParm superResult = selectDrCodes(acceptData.getData("CASE_NO").toString());
					// 判断是否有经治医师签名
					if(!isDrSign(superResult.getValue("VS_DR_CODE", 0), superResult.getValue("VS_DR_NAME", 0), word)){// 没有经治医师签名
						// 判断是否有主治医师签名
						if(!isDrSign(superResult.getValue("ATTEND_DR_CODE", 0), superResult.getValue("ATTEND_DR_NAME", 0), word)){// 没有主治医师签名
							// 判断是否有科主任签名
							if(!isDrSign(superResult.getValue("DIRECTOR_DR_CODE", 0), superResult.getValue("DIRECTOR_DR_NAME", 0), word)){// 没有科主任签名
								list2.add(emrParm.getValue("FILE_NAME",i)+"病历没有上级医师签名"+"\r\n");
							}
						}
					}

				}else{// 没有规培医师签名，退出
					list.add(emrParm.getValue("FILE_NAME",i)+"病历没有规培医师签名"+"\r\n");
				}

			}
		}  
		if(!list.isEmpty()){
			//     			list.add("是否继续保存？？？"+"\r\n");
			this.messageBox(list.toString().replace("[", "").replace("]", "").replace(",", ""));
			return;
		}
		if(!list2.isEmpty()){
			list2.add("是否继续保存？？？"+"\r\n");
			// 取消：2；确定：0
			int flg = this.messageBox("", list2.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
			System.out.println("======flg="+flg);
			if(flg==2){// 不保存
				return;
			}
		}
		// modified by Eric 20170606 end

		/***********************liuf HL7 *******************************/
		//        TParm parmCIS = new TParm();
		//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
		//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
		//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
		//        parmCIS.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
		//        parmCIS.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
		//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		//        parmCIS.setData("OUT_DATE", this.getValue("OUT_DATE"));
		//        parmCIS.setData("PSF_KIND", "INDP");
		//        parmCIS.setData("OPT_USER", Operator.getID());
		//        parmCIS.setData("OPT_TERM", Operator.getIP());
		//        parmCIS.setData("REGION_CODE", Operator.getRegion());
		//        sendHL7Mes(parmCIS, "ADM_TRAN");             
		/***********************liuf HL7 *******************************/
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInOutSave", parm);
		if ("F".equals(result.getData("CHECK"))) {
			this.messageBox("次病患暂不能转科！");
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		} else {
			this.messageBox("P0005");
			isPre();//如果是预转状态这删除ADM_PRETREAT表中的信息
			/*********************** HL7 *******************************/
			sendHL7Mes(parm, "ADM_TRAN");
			this.closeWindow();
		}

		// add by wangb 2015/07/02 转科时向大屏发送消息以确保统计信息准确
		TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(acceptData.getValue("CASE_NO"));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
		}
		xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(acceptData.getValue("CASE_NO"));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
		}
	}
	/**
	 * 如果是预转状态
	 */
	public void isPre(){
		String sql="";
		if("Y".equals(preFlg)){
			sql="DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO = '"+preOutNo+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
		}
		sql="UPDATE SYS_BED SET PRE_FLG='N' WHERE PRETREAT_OUT_NO='"+preOutNo+"'";
		new TParm(TJDODBTool.getInstance().update(sql));

	}
	/**
	 * 出院保存
	 */
	public void outAdm() {
		if (!checkDate())
			return;
		// 判断病患是否是召回的病人 如果是那么需要选择出院日期
		TParm adm_case = new TParm();
		adm_case.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        //add by chenhj 2017.5.18
		//        int i=onCheckSign(acceptData.getData("CASE_NO").toString());
		//        if(i==2){
		//        	return;
		//        }

		// modified by wangqing 20170804 start
		// 规培医师写的病历如果没有上级医师签名，需要提示，但不强制签名
		// 查询患者所有病历
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+acceptData.getData("CASE_NO").toString()+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); // 没有规培医师签名
		List<String> list2=new ArrayList<String>();// 没有上级医师签名
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			// 判断是否是规培医师写的病历
			if(isPracticerWrite(word)){// 是规培医师写的病历
				// 判断是否有规培医师签名
				if(isDrSign(word.getFileAuthor(), this.getUseName(word.getFileAuthor()), word)){// 有规培医师签名
					// 查询此病人的上级医师
					TParm superResult = selectDrCodes(acceptData.getData("CASE_NO").toString());
					// 判断是否有经治医师签名
					if(!isDrSign(superResult.getValue("VS_DR_CODE", 0), superResult.getValue("VS_DR_NAME", 0), word)){// 没有经治医师签名
						// 判断是否有主治医师签名
						if(!isDrSign(superResult.getValue("ATTEND_DR_CODE", 0), superResult.getValue("ATTEND_DR_NAME", 0), word)){// 没有主治医师签名
							// 判断是否有科主任签名
							if(!isDrSign(superResult.getValue("DIRECTOR_DR_CODE", 0), superResult.getValue("DIRECTOR_DR_NAME", 0), word)){// 没有科主任签名
								list2.add(emrParm.getValue("FILE_NAME",i)+"病历没有上级医师签名"+"\r\n");
							}
						}
					}

				}else{// 没有规培医师签名，退出
					list.add(emrParm.getValue("FILE_NAME",i)+"病历没有规培医师签名"+"\r\n");
				}

			}
		}  
		if(!list.isEmpty()){
			//     			list.add("是否继续保存？？？"+"\r\n");
			this.messageBox(list.toString().replace("[", "").replace("]", "").replace(",", ""));
			return;
		}
		if(!list2.isEmpty()){
			list2.add("是否继续保存？？？"+"\r\n");
			// 取消：2；确定：0
			int flg = this.messageBox("", list2.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
			System.out.println("======flg="+flg);
			if(flg==2){// 不保存
				return;
			}
		}
		// modified by wangqing 20170804 end


		TParm check = ADMTool.getInstance().getADM_INFO(adm_case);
		Timestamp ds_date;
		String outDept=this.getValueString("OUT_DEPT_CODE");
		String outStation=this.getValueString("OUT_STATION_CODE");
		//===zhangp 20130105 start 召回重新入床的病人会因为床号变更查询不到费用导致状态为出院无账单
		TParm parm = new TParm();
		// 目前还没有召回功能 暂且注释掉
		if (check.getValue("LAST_DS_DATE", 0).length() > 0) { // 医疗出院日期不为空
			// 那么是召回的病人
			Object obj = this.openDialog("%ROOT%/config/adm/ADMChangeDsDate.x",
					check.getTimestamp("LAST_DS_DATE", 0));
			if (obj == null) {
				return;
			}
			ds_date = (Timestamp) obj;
			if(ds_date.equals(check.getTimestamp("LAST_DS_DATE", 0))){// SHIBL ADD 出院科室病区取上一次的记录
				outDept=check.getValue("DS_DEPT_CODE", 0).equals("")?outDept:check.getValue("DS_DEPT_CODE", 0);
				outStation=check.getValue("DS_STATION_CODE", 0).equals("")?outStation:check.getValue("DS_STATION_CODE", 0);
			}
			parm.setData("ADMS", "1");
			//===zhangp 20130105 end

		} else {
			//再次出院不改变出院日期
			if (check.getValue("DS_DATE", 0).length() > 0)
				ds_date = check.getTimestamp("DS_DATE", 0);
			else
				ds_date = SystemTool.getInstance().getDate();
		}
		// 计算住院天数
		int in_days = StringTool.getDateDiffer(
				(Timestamp)this.getValue("OUT_DATE"),
				acceptData.getTimestamp("IN_DATE"));
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("BED_NO", this.getValue("BED_NO"));
		parm.setData("OUT_DEPT_CODE", outDept);
		parm.setData("OUT_STATION_CODE", outStation);
		parm.setData("IN_DAYS", in_days);
		parm.setData("OUT_DATE", ds_date);
		parm.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
		parm.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
		parm.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
		parm.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// =========pangben modify 20110617
		parm.setData("REGION_CODE", Operator.getRegion());
		// System.out.println("再次出院入参" + parm);
		/***********************liuf HL7 *******************************/
		//        TParm parmCIS = new TParm();
		//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
		//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
		//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
		//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		//        parmCIS.setData("IN_DAYS", in_days);
		//        parmCIS.setData("OUT_DATE", ds_date);
		//        parmCIS.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
		//        parmCIS.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
		//        parmCIS.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
		//        parmCIS.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
		//        parmCIS.setData("OPT_USER", Operator.getID());
		//        parmCIS.setData("OPT_TERM", Operator.getIP());
		//        parmCIS.setData("REGION_CODE", Operator.getRegion());
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);
		//        sendHL7Mes(parmCIS, "ADM_OUT");
		/***********************liuf HL7 *******************************/

		//判断账单状态
		TParm backBillParm = IBSBillmTool.getInstance().selBackBill(parm);
		if (backBillParm.getErrCode() < 0) {
			err(backBillParm.getErrText());
		}
		int backBillCount = backBillParm.getCount("CASE_NO");


		Timestamp today = SystemTool.getInstance().getDate();
		Timestamp yesterday = StringTool.rollDate(today, -1);
		String yesDayStr = StringTool.getString(yesterday, "yyyyMMdd");
		String todayStr = StringTool.getString(today, "yyyyMMdd");
		String countDaySql = " SELECT SUM(D.TOT_AMT) AS TOT_AMT "
				+ "   FROM IBS_ORDM M,IBS_ORDD D " + "  WHERE D.CASE_NO = '"
				+ parm.getData("CASE_NO") + "' "
				+ "    AND M.CASE_NO = D.CASE_NO "
				+ "    AND M.DATA_TYPE = '0' "
				+ "    AND D.BILL_DATE BETWEEN TO_DATE('" + yesDayStr
				+ "','YYYYMMDD') " + "                      AND TO_DATE('"
				+ todayStr + "','YYYYMMDD') ";
		TParm countDayParm = new TParm(TJDODBTool.getInstance().select(
				countDaySql));  
		double autoFee = 0.00;     
		if (countDayParm.getCount() > 0)
			autoFee = countDayParm.getDouble("TOT_AMT", 0);
		if (parm.getInt("IN_DAYS") <= 0 && backBillCount <= 0 && autoFee == 0) {   
			//this.messageBox("提示", "已产生费用,是否重展", 2) == 0
			if (messageBox("提示信息", "是否计算当日入院当日出院产生的固定费用？", this.YES_NO_OPTION) == 0) {
				String todayFlg = "Y";           
				parm.setData("TODAY", todayFlg);      
			}
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "outAdmSave", parm);
		if (result.getErrCode() < 0)
			this.messageBox("E0005");
		else {
			this.messageBox("P0005");

			isPre();//如果是预转状态这删除ADM_PRETREAT表中的信息

			// wanglong add 20141103 出院时更新病案首页基本信息
			TParm mro = new TParm();
			mro.setData("MR_NO", this.getValue("MR_NO"));
			mro.setData("CASE_NO", acceptData.getData("CASE_NO"));
			mro.setData("IN_DATE", acceptData.getData("IN_DATE"));
			mro.setData("OPT_USER", Operator.getID());
			mro.setData("OPT_TERM", Operator.getIP());
			result = MROTool.getInstance().updateMROPatInfo(mro);
			if (result.getErrCode() < 0) {
				this.messageBox("更新病案首页信息失败 " + result.getErrText());
			}
			// 床旁接口 wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(acceptData.getValue("CASE_NO"), "A03");
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
			}
			xmlParm = ADMXMLTool.getInstance().creatXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("信息看板接口发送失败 " + xmlParm.getErrText());
			}
			// 电视屏接口 wanglong add 20141010
			xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
			}
			xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
			}
			/*********************** HL7 *******************************/
			sendHL7Mes(parm, "ADM_OUT");

			// add by wangb 2017/6/2 自动更新体温单出院数据 START
			int hour = DateUtil.getHour(ds_date);
			TParm updateParm = new TParm();
			updateParm.setData("ADM_TYPE", "I");
			updateParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
			updateParm.setData("EXAMINE_DATE", ds_date.toString().replace("-",
					"").substring(0, 8));
			updateParm.setData("EXAMINESESSION", hour / 4);
			updateParm.setData("PTMOVECATECODE", "05");
			updateParm.setData("PTMOVECATEDESC", DateUtil
					.transferHMToChinese(ds_date.toString()));

			// 更新病患动态信息
			updateParm = SUMVitalSignTool.getInstance().updatePatientTrends(
					updateParm);

			if (updateParm.getErrCode() < 0) {
				System.out.println("更新体温单出院时间失败：" + updateParm.getErrText());
			}
			// add by wangb 2017/6/2 自动更新体温单出院数据 END

			this.closeWindow();
		}
	}
	//add by chenhj start
	/**
	 * add by chenhj 各病历签字情况
	 * @param caseNo
	 */
	public int  onCheckSign(String caseNo) { 
		//查询患者主治医生
		String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
				+ " FROM ADM_INP " + " WHERE CASE_NO = '"
				+ caseNo + "'";
		TParm signParm=new TParm(TJDODBTool.getInstance().select(sql));
		//患者主治医生code
		String vsDrCode=signParm.getValue("VS_DR_CODE",0);
		//查询患者所有病历
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+caseNo+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); 
		list.add("以下尚未签名：\r\n");
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			boolean bb=isSign();
			if(!bb){
				String fileName=emrParm.getValue("FILE_NAME",i);
				String optDat=emrParm.getValue("OPT_DATE",i).substring(0,19);
				list.add(optDat+" "+fileName+"\r\n");
			}
		}
		int i=this.messageBox("未签名清单",list.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
		return i;
	}
	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	public void setWord(TWord word) {
		this.word = word;
	}
	public TWord getWord() {
		return this.word;
	}
	private boolean isSign(){
		boolean flg=false;   	
		//同名情况    2个相同签名
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
		ESign  sign2=(ESign)this.getWord().findObject(Operator.getID()+"_1", EComponent.SIGN_TYPE);
		if(sign1!=null){
			flg=true;
		}
		//
		//
		if(sign2!=null){
			flg=true;
		}
		return flg;
	}
	//add by chenhj end
	/**
	 * 出院点选事件
	 */
	public void onOUT_INP() {
		String check = getValue("OUT_INP").toString();
		if ("Y".equals(check)) {
			callFunction("UI|IN_DEPT_CODE|setEnabled", false);
			callFunction("UI|IN_STATION_CODE|setEnabled", false);
			TParm parm = new TParm();
			// 检查长期医嘱
			if (OdiOrderTool.getInstance().getUDOrder(
					acceptData.getValue("CASE_NO"))) {
				this.messageBox( "该病患有医生站未停用的长期医嘱，不允许出科。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			// 检查护士审核
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
				this.messageBox( "该病患有护士站未审核的医嘱，不允许出科。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  chenxi  add  20130321 出院病患检索药房未审核，药房未配药情况
			// 检查住院药房审核执行 true：有未审核,false:没有未配的药
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if(InwForOutSideTool.getInstance().checkDrug(parm)){
				this.messageBox( "住院药房有该病患未审核的医嘱，不允许出科。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			// 检查配药执行 true：有未配的药,false:没有未配的药
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if(InwForOutSideTool.getInstance().exeDrug(parm)){
				this.messageBox( "住院药房有该病患未完成的配药，不允许出科。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  chenxi  add  20130321 出院病患检索药房未审核，药房未配药情况
			// 检查护士执行
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
				this.messageBox( "该病患有护士站未执行的医嘱，不允许出科。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  duzhw  add  20130917 出院病患检索退药流程是否完成，未完成不许出院
			// 
			if (OdiOrderTool.getInstance().getRtnCfmM(
					acceptData.getValue("CASE_NO"))) {
				this.messageBox( "该病患有药房退药未确认的信息，不允许出院。");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//start 校验检验PDF报告，如果有报告未回传，增加提示 machao
			TParm result = onPdfParm();
			String orderDesc = "";
			for(int i = 0;i<result.getCount();i++){
				orderDesc = result.getValue("ORDER_DESC", i);
				this.messageBox(orderDesc+":医嘱报告未传回!");
			}
			//end
		} else {
			callFunction("UI|IN_DEPT_CODE|setEnabled", true);
			callFunction("UI|IN_STATION_CODE|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
		}
		// this.messageBox_(getValue("OUT_INP"));

	}

	public TParm onPdfParm(){
		TParm result = null;
		String sql = "SELECT * "+
					 "FROM MED_APPLY "+
					 "WHERE     MR_NO = '"+acceptData.getValue("MR_NO")+"' "+
					 "AND CASE_NO = '"+acceptData.getValue("CASE_NO")+"' "+
				     "AND CAT1_TYPE = 'LIS' "+   
				     "AND ADM_TYPE = 'I' "  + 
				     "AND (PDFRE_FLG = 'N' OR PDFRE_FLG IS NULL)"   ;
		//System.out.println("sql----- 1:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));		       
		return result;
	}
	/**
	 * 选择转入科室事件
	 */
	public void onIN_DEPT_CODE() {
		this.setValue("IN_STATION_CODE", "");
	}

	/**
	 * 检核转入病区
	 */
	public void onIN_STATION_CODE() {
		String inStation = this.getValue("IN_STATION_CODE").toString();
		String outStation = this.getValue("OUT_STATION_CODE").toString();
		if (inStation.equals(outStation)) {
			this.messageBox("转入转出病区相同！");
			this.setValue("IN_STATION_CODE", "");
			return;
		}
	}

	/**
	 * 检核数据
	 *
	 * @return boolean
	 */
	public boolean checkDate() {
		if (this.getValue("OUT_DEPT_CODE") == null
				|| this.getValue("OUT_DEPT_CODE").equals("")) {
			this.messageBox_("请选择转出科室！");
			return false;
		}
		if (this.getValue("OUT_STATION_CODE") == null
				|| this.getValue("OUT_STATION_CODE").equals("")) {
			this.messageBox_("请选择转出病区！");
			return false;
		}
		String out = getValue("OUT_INP").toString();
		if ("N".equals(out)) {
			if (this.getValue("IN_DEPT_CODE") == null
					|| this.getValue("IN_DEPT_CODE").equals("")) {
				this.grabFocus("IN_DEPT_CODE");
				this.messageBox_("请选择转入科室！");
				return false;
			}
			//shibl 20120524 add
			//            if(this.getValue("IN_DEPT_CODE").equals(this.getValue("OUT_DEPT_CODE"))){
			//            	this.grabFocus("IN_DEPT_CODE");
			//                this.messageBox_("转入科室与转出科室相同,请重新选择！");
			//                return false;
			//            }
			if (this.getValue("IN_STATION_CODE") == null
					|| this.getValue("IN_STATION_CODE").equals("")) {
				this.grabFocus("IN_STATION_CODE");
				this.messageBox_("请选择转入病区！");
				return false;
			}
		}
		TParm parm = new TParm();
		// 检查长期医嘱
		if (OdiOrderTool.getInstance().getUDOrder(
				acceptData.getValue("CASE_NO"))) {
			this.messageBox("该病患有医生站未停用的长期医嘱，不允许出科");
			return false;

		}
		// 检查护士审核
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
			this.messageBox("该病患有护士站未审核的医嘱,不允许出科");
			return false;

		}
		// 检查护士执行
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
			this.messageBox("该病患有护士站未执行的医嘱，不允许出科");
			return false;
		}
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().checkDrug(parm)){
			this.messageBox( "药房有该病患未审核的医嘱，不允许出科。");
			return  false;
		}
		// 检查配药执行 true：有未配的药,false:没有未配的药
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().exeDrug(parm)){
			this.messageBox( "药房有该病患未完成的配药，不允许出科。");
			return  false ;
		}
		if (OdiOrderTool.getInstance().getRtnCfmM(acceptData.getValue("CASE_NO"))) {
			this.messageBox( "该病患有药房退药未确认的信息，不允许出科。");
			return   false;
		}
		return true;
	}

	//$$==========liuf =============$$//
	/**
	 * hl7接口
	 * @param parm TParm
	 * @param type String
	 */
	private void sendHL7Mes(TParm parm, String type) {
		System.out.println("parm:"+parm);
		String caseNo = parm.getValue("CASE_NO");
		//转科
		if (type.equals("ADM_TRAN")) 
		{
			String InDeptCode = parm.getValue("IN_DEPT_CODE");
			boolean IsICU = parm.getBoolean("ICU_FLG");
			boolean IsCCU = parm.getBoolean("CCU_FLG");
			//CIS
			if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU) 
			{
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CIS");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
				if (resultParm.getErrCode() < 0)
					this.messageBox(resultParm.getErrText());
			}
			////////////////////////////////////////////zhangs add start
			//输液泵
			System.out.println("输液泵:"+this.checkIsCS5(caseNo));
			if (this.checkIsCS5(caseNo))
			{ 
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CS5");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			///////////////////////////////////////////////zhangs add end

			//血糖
			List list = new ArrayList();
			parm.setData("ADM_TYPE", "I");
			parm.setData("SEND_COMP", "NOVA");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0)
				this.messageBox(resultParm.getErrText());

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

			//add by lij 20170413 增加 NIS的HL7消息
			list = new ArrayList();
			parm.setData("SEND_COMP", "NIS");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}

		}
		//出院
		if (type.equals("ADM_OUT")) 
		{
			boolean IsICU = parm.getBoolean("ICU_FLG");
			boolean IsCCU = parm.getBoolean("CCU_FLG");
			//CIS
			if (IsICU||IsCCU) 
			{
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CIS");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			////////////////////////////////////////////zhangs add start
			//输液泵
			System.out.println("输液泵:"+this.checkIsCS5(caseNo));
			if (this.checkIsCS5(caseNo))
			{ 
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CS5");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			///////////////////////////////////////////////zhangs add end

			//血糖
			List list = new ArrayList();
			parm.setData("ADM_TYPE", "I");
			parm.setData("SEND_COMP", "NOVA");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0)
				this.messageBox(resultParm.getErrText());

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

			//add by lij 20170413 增加 NIS的HL7消息
			list = new ArrayList();
			parm.setData("SEND_COMP", "NIS");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}
		}
	}
	//$$==========liuf =============$$//
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
	 * <p>判断是否是规培医师写的病历</p>
	 * 
	 * @return
	 */
	public boolean isPracticerWrite(TWord word){
		String fileAuthor = word.getFileAuthor();
		String roleIdSql = " SELECT ROLE_ID FROM SYS_OPERATOR WHERE USER_ID ='"+fileAuthor+"' ";
		TParm roleIdResult = new TParm(TJDODBTool.getInstance().select(roleIdSql));
		String roleId = roleIdResult.getValue("ROLE_ID", 0);
		// 1、判断是否是规培医师
		if(roleId != null && roleId.equals("T")){// 规培医师
			return true;
		}
		return false;
	}

	/**
	 * <p>查询此病患的三级上级医师（经治医师、主治医师、科主任）</p>
	 * 
	 * @return
	 */
	public TParm selectDrCodes(String caseNo){
		String superSql= " SELECT A.VS_DR_CODE, A.ATTEND_DR_CODE, A.DIRECTOR_DR_CODE, "
				+ " B.USER_NAME AS VS_DR_NAME, C.USER_NAME AS ATTEND_DR_NAME, D.USER_NAME AS DIRECTOR_DR_NAME "
				+ " FROM ADM_INP A "
				+ " LEFT JOIN SYS_OPERATOR B ON A.VS_DR_CODE = B.USER_ID "
				+ " LEFT JOIN SYS_OPERATOR C ON A.ATTEND_DR_CODE = C.USER_ID "
				+ " LEFT JOIN SYS_OPERATOR D ON A.DIRECTOR_DR_CODE = D.USER_ID "
				+ " WHERE A.CASE_NO='"+caseNo+"' ";
		TParm superResult = new TParm(TJDODBTool.getInstance().select(superSql));
		return superResult;
	}
	
	/**
	 * <p>获取医生姓名</p>
	 * @param userId
	 * @return
	 */
	public String getUseName(String userId){
		String sql = " SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+userId+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			return result.getValue("USER_NAME", 0);
		}
		return "";
	}

	/**
	 * <p>判断病历是否有相应医师的签名</p>
	 * 
	 * @return
	 */
	public boolean isDrSign(String drCode, String drName, TWord word){
		
		// 1、签名控件 2、固定文本 3、宏（暂且没法获取值）
		if(drCode != null && drCode.length()>0){
			ESign sign=(ESign)word.findObject(drCode, EComponent.SIGN_TYPE);
			// modified by wangqing 20170804 start
			EFixed f1 = (EFixed)word.findObject("DR_SIGN", EComponent.FIXED_TYPE);
			EFixed f2 = (EFixed)word.findObject("SUPERIOR_SIGN", EComponent.FIXED_TYPE);
			if(sign!=null){
				return true;
			}
			if(f1 != null && f1.getText() != null && f1.getText().equals(drName)){
				return true;
			}
			if(f2 != null && f2.getText() != null && f2.getText().equals(drName)){
				return true;
			} 
			// modified by wangqing 20170804 end
		}
		return false;
	}


}
