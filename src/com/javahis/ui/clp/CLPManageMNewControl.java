package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.clp.CLPCauseHistoryTool;
import jdo.clp.CLPManagemTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatCLPEvlStandm;
/**
 * <p>
 * Title: 临床路径准进准出
 * </p>
 * 
 * <p>
 * Description: 住院医生站第一次进入操作临床路径准入
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 2015-8-11
 * @version 1.0
 */
public class CLPManageMNewControl extends TControl{
	/**
	 * 就诊序号
	 */
	private String case_no;
	private String mr_no;
	private String optUser;
	private String optTerm;
	private String regionCode;
	private String clpNewFlg;
	/**
	 * 页面初始化方法
	 */
	public void onInit() {
		super.onInit();
		// 去掉菜单栏
		TDialog F = (TDialog) this.getComponent("UI");
		F.setUndecorated(true);
		initPage();
	}
	/**
	 * 初始化
	 */
	private void initPage() {
		TParm inParm = (TParm) this.getParameter();
		case_no = inParm.getValue("CASE_NO");
		mr_no = inParm.getValue("MR_NO");
		optUser=inParm.getValue("OPT_USER");
		optTerm=inParm.getValue("OPT_TERM");
		regionCode=inParm.getValue("REGION_CODE");
		clpNewFlg=inParm.getValue("CLP_NEW_FLG");//第一次进入医生站操作，更新转科表ADM_TRANS_LOG
		//界面赋值病人信息
		String patName=inParm.getValue("PAT_NAME");
		String deptCode=inParm.getValue("DEPT_CODE");//病人所在科室
		String ctzCode=inParm.getValue("CTZ_CODE");//身份
		String diagCode=inParm.getValue("DIAG_CODE");//诊断
		String diagDesc=inParm.getValue("DIAG_DESC");
		this.setValue("PAT_NAME", patName);
		this.setValue("DEPT_CODE", deptCode);
		this.setValue("CTZ_CODE", ctzCode);
		this.setValue("DIAG_CODE", diagCode+diagDesc);
		this.setValue("MR_NO", mr_no);
		this.setValue("CLNCPATH_CODE", inParm.getValue("CLNCPATH_CODE"));
		onCheckQuery();
		
		//调用webService 查询默认路径代码
	}
	/**
	 * 
	* @Title: onUnEnter
	* @Description: TODO(不进入)
	* @author pangben
	* @throws
	 */
	public void onUnEnter(){
		String causeCode=this.getValueString("CAUSE_CODE");
		if (causeCode.length()<=0) {
			this.messageBox("不进入原因不能为空");
			this.grabFocus("CAUSE_CODE");
			return;
		}
		TParm parm=new TParm();
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		//查询最大序号
		TParm getMaxSeqNoParm=CLPCauseHistoryTool.getInstance().queryMaxSeqNo(parm);
		if (getMaxSeqNoParm.getCount()>0&&getMaxSeqNoParm.getValue("SEQ_NO",0).length()>0) {
			parm.setData("SEQ_NO",getMaxSeqNoParm.getInt("SEQ_NO",0)+1);
		}else{
			parm.setData("SEQ_NO",0);
		}
		parm.setData("CAUSE_CODE",causeCode);
		parm.setData("CAUSE_USER",optUser);
		parm.setData("OPT_USER",optUser);
		parm.setData("OPT_TERM",optTerm);
		parm.setData("CLP_NEW_FLG", clpNewFlg);//第一次进入医生站操作，更新转科表ADM_TRANS_LOG
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertCauseHistory", parm);
		if (result.getErrCode()< 0) {
			this.messageBox("E0002");
		} else {
			this.messageBox("P0002");
			this.closeWindow();
		}
	}
	/**
	 * 
	* @Title: onSave
	* @Description: TODO(保存)
	* @author pangben
	* @throws
	 */
	public void onSave(){
		TParm parm = new TParm();
		parm.setData("MR_NO", this.mr_no);
		parm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(parm);
		TParm result = CLPManagemTool.getInstance().getPatientInfo(parm);
		if (result.getCount() <= 0) {
			this.messageBox("请先查询住院病人信息");
			return;
		}
		// 调用查询方法
		TParm resultTParm = CLPManagemTool.getInstance().selectData(parm);
		if (result.getCount() <= 0) {
			this.messageBox("请先查询住院病人信息");
			return;
		}
		if (resultTParm.getCount() > 0) {
			this.messageBox("该病人临床路径信息已经存在!");
			return;
		}
		if (!validData()) {
			return;
		}
		parm.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));//临床路径代码
		parm.setData("SCHD_CODE", this.getValue("SCHD_CODE"));//时程
		parm.setData("EVL_CODE", this.getValue("EVL_CODE"));//评估代码
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		putBasicSysInfoIntoParm(parm);
		String sql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND END_DTTM IS NOT NULL";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selparm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (selparm.getCount("CASE_NO")>0) {
			this.messageBox("此病患的临床路径项目已经溢出,不可以添加");
			return;
		}
		//====yanjing 20140710 查询该路径是否存在作废的数据 start
		String cancleSql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' " +
				"AND CLNCPATH_CODE = '"+parm.getValue("CLNCPATH_CODE")+"'";
		TParm cancleParm = new TParm(TJDODBTool.getInstance().select(cancleSql));
		if (cancleParm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (cancleParm.getCount("CASE_NO")>0) {
			this.messageBox("此病患存在作废的该临床路径项目");
			return;
		}
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("IN_DATE", datestr);
		parm.setData("START_DTTM", getCurrentDateTimeStr());
		parm.setData("CLP_NEW_FLG", clpNewFlg);//第一次进入医生站操作，更新转科表ADM_TRANS_LOG
		TParm resultParam = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertManagem", parm);
		if (resultParam.getErrCode()< 0) {
			this.messageBox("E0002");
		} else {
			this.messageBox("P0002");
			this.closeWindow();
		}
		
	}
	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		//int total = parm.getCount();
		//System.out.println("total" + total);
		parm.setData("REGION_CODE",regionCode );
		parm.setData("OPT_USER", optUser);
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", optTerm);
	}
	/**
	 * 数据验证方法
	 * 
	 * @return boolean
	 */
	private boolean validData() {
		boolean flag = true;
		if (this.getValueString("CLNCPATH_CODE") == null
				|| this.getValueString("CLNCPATH_CODE").length() <= 0) {
			this.messageBox("请选择临床路径");
			return false;
		}
		if (this.getValueString("EVL_CODE") == null
				|| this.getValueString("EVL_CODE").length() <= 0) {
			this.messageBox("请选择评估代码");
			return false;
		}
		return flag;
	}
	/**
	 * 得到当前的时间，年月日时分秒
	 * 
	 * @return String
	 */
	private String getCurrentDateTimeStr() {
		return getCurrentDateTimeStr("yyyyMMddHHmmss");
	}

	/**
	 * 得到当前时间，年月日时分秒
	 * 
	 * @param formatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateTimeStr(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String dateStr = format.format(new Date());
		return dateStr;
	}
	/**
	 * 
	* @Title: onCheckQuery
	* @Description: TODO(临床路径下拉列表事件)
	* @author pangben
	* @throws
	 */
	public void onCheckQuery(){
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_schd.onQuery();
        String sql="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+
        this.getValueString("CLNCPATH_CODE")+"' ORDER BY SEQ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        TextFormatCLPEvlStandm combo_evl=  (TextFormatCLPEvlStandm)this.getComponent("EVL_CODE");
        combo_evl.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_evl.onQuery();
        if (result.getCount()>0) {
        	this.setValue("SCHD_CODE", result.getValue("SCHD_CODE",0));
		}
        sql="SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE = '" + 
        this.getValueString("CLNCPATH_CODE") + "' ORDER BY EVL_CODE,SEQ ";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount()>0) {
       	    this.setValue("EVL_CODE", result.getValue("EVL_CODE",0));
		}
	}
}
