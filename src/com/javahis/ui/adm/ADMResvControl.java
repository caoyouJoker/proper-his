package com.javahis.ui.adm;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.data.TParm;

import jdo.sys.SystemTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Operator;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;

import jdo.adm.ADMInpTool;

import com.dongyang.data.TNull;

import java.sql.Timestamp;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.javahis.util.StringUtil;
import com.dongyang.util.StringTool;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.javahis.system.textFormat.TextFormatCLPBscInfo;

import jdo.clp.BscInfoTool;
import jdo.clp.CLPSingleDiseTool;

 

/**
 * <p>Title: 预约住院</p>
 *
 * <p>Description: 预约住院</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMResvControl
    extends TControl {
    Pat pat;
    TParm recParm = new TParm(); //接参
    String type = "NEW"; //保存，更新控制使用
    String resvNo = ""; //预约单号
    String BED_NO = ""; //预约床号
    String McaseNo = ""; //母亲就诊序号
    String adm_type_zyz = "";//打印住院证时，门急住级别，yanj，20130820
    String bedNo="";
    boolean save = true;
    String preTreatNO="";
    String hrmCaseNo = "";
    private String dayOpeFlg;
    
    
    public String getDayOpeFlg() {							//   2017/3/24   	by  yanmm   增加日间手术勾选				
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {			//   2017/3/24   	by  yanmm   增加日间手术勾选		
		this.dayOpeFlg = dayOpeFlg;
	}

	public void onInit() {
    	
        init();
        //只有text有这个方法，调用ICD10弹出框
        callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //textfield接受回传值
        callFunction("UI|DIAG_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        setValue("APP_DATE", SystemTool.getInstance().getDate()); //预定日期
                        setValue("RESV_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(),1)); //预约住院日
                    }
                    catch (Exception e) {
                    }
                }
            });
        this.setMenu(false);
        //接收参数
        //参数：MR_NO --病案号;
        //     ADM_SOURCE --病患来源
        //     DEPT_CODE --门急诊部门
        //     DR_CODE --门急诊医师
        //     ICD_CODE --诊断CODE
        //     DESCRIPTION --诊断备注
        // 测试参数
//        TParm parm = new TParm();
//        parm.setData("MR_NO","000000000092");
//        parm.setData("ADM_SOURCE","01");
//        parm.setData("DEPT_CODE","10101");
//        parm.setData("DR_CODE","D001");
//        parm.setData("ICD_CODE","Q44");
//        parm.setData("DESCRIPTION","备注");
//        Object obj = parm;
        Object obj = this.getParameter();
        TParm recptParm = new TParm();
        if (obj instanceof TParm) {
            recptParm = (TParm) obj;
            this.initUI(recptParm);
        }
        
        if (!this.getPopedem("PIC")) {
        	this.callFunction("UI|linkHrm|Visible", false);
        	this.callFunction("UI|l_RECRUIT_NO|Visible",false);
        	this.callFunction("UI|RECRUIT_NO|Visible",false);
        }
    }

    /**
     * 其它程序调用初始化
     * MR_NO,ADM_SOURCE,OPD_DEPT_CODE,OPD_DR_CODE,DIAG_CODE
     * @param parm TParm
     */
    public void initUI(TParm parm) {
    	adm_type_zyz = parm.getValue("ADM_TYPE_ZYZ");//yanj,20130820初始化
    
    	
        setValue("MR_NO", parm.getData("MR_NO"));
        onMRNO();
        if (parm.getValue("ADM_SOURCE").length() > 0) {
            setValue("ADM_SOURCE", parm.getValue("ADM_SOURCE"));
            callFunction("UI|ADM_SOURCE|setEnabled", false);
        }
        if (parm.getValue("ADM_SOURCE").equals("02")) {// 急诊 add by wanglong 20121025
            TParm param = new TParm();
            param.setData("CASE_NO", parm.getValue("CASE_NO"));
            TParm diseCode = CLPSingleDiseTool.getInstance().queryREGAdmSDInfo(param);
            if (diseCode.getErrCode() < 0) {
                messageBox("单病种信息获取出错：" + diseCode.getErrText());
                return;
            } else {
                setValue("DISE_CODE", diseCode.getValue("DISE_CODE", 0));
            }
        }
        if (!parm.checkEmpty("DEPT_CODE", parm)) { // 门急诊部门
            setValue("OPD_DEPT_CODE", parm.getValue("DEPT_CODE"));
            callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
        }
        if (!parm.checkEmpty("DR_CODE", parm)) { // 门急诊医师
            setValue("OPD_DR_CODE", parm.getValue("DR_CODE"));
            callFunction("UI|OPD_DR_CODE|setEnabled", false);
        }
        // 诊断备注
        setValue("DIAG_REMARK", parm.getValue("DESCRIPTION"));
        // 门急诊诊断
        setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
        setValue("DIAG_DESC", this.getName(parm.getValue("ICD_CODE")));
        // callFunction("UI|save|setEnabled", true); //保存 chenxi
    }

    /**
     * Menu 流程控制
     */
    public void setMenu(boolean check) {
        if (check) {
            callFunction("UI|save|setEnabled", true); // 保存
            callFunction("UI|notify|setEnabled", true); // 预约通知
            callFunction("UI|stop|setEnabled", true); // 取消预约
            callFunction("UI|child|setEnabled", true); // 新生儿
            callFunction("UI|linkHrm|setEnabled", true);// 关联体检信息
        } else {
            callFunction("UI|save|setEnabled", false); // 保存
            callFunction("UI|notify|setEnabled", false); // 预约通知
            callFunction("UI|stop|setEnabled", false); // 取消预约
            callFunction("UI|child|setEnabled", false); // 新生儿
            callFunction("UI|linkHrm|setEnabled", false);// 关联体检信息
        }
    }

    /**
     * 得到诊断desc
     * @param code String
     * @return String
     */
    public String getName(String code) {
        if (code == null) return code;
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        if (dataStore == null) return code;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector v = (Vector) parm.getData("ICD_CODE");
        Vector d = (Vector) parm.getData("ICD_CHN_DESC");
        Vector e = (Vector) parm.getData("ICD_ENG_DESC");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(v.get(i))) {
                if ("en".equals(this.getLanguage())) {
                    return "" + e.get(i);
                } else {
                    return "" + d.get(i);
                }
            }
        }
        return code;
    }

    /**
     * 诊断事件
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm==null){
            this.setValue("DIAG_CODE", "");
            this.setValue("DIAG_DESC", "");
        }else{
            this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
            this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
            this.grabFocus("DIAG_REMARK");
        }
    }


    /**
     * 查询
     */
    public void onQuery() {
        this.onMRNO();
    }

    /**
     * 病案号回车查询事件
     * 如果没有住院号，通过取号原则取得住院号
     */
    public void onMRNO() {
        String mrNo = getValueString("MR_NO").trim();
        if (!this.queryPat(mrNo)) return;
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        //检查病患是否住院中
        if (this.checkAdmInp()) {
            this.messageBox("E0121");
            this.setMenu(false);
            return;
        }
        // 根据病案号 查询该病人的所有预约信息
        TParm result = ADMResvTool.getInstance().selectAll(parm);
        // 判断是否已经预约 有预约单号并且没有入院没有产生IN_CASE_NO就是预约中的病人
        if (!"".equals(result.getValue("RESV_NO", 0))
                && "".equals(result.getValue("IN_CASE_NO", 0))) {
        	//新增一个带出信息，RECRUIT_NO受试者编号，反正不是PIC也看不到 by guangl 20160719
            this.setValueForParm("RESV_NO;TEL;TEL_NO1;CTZ1_CODE;ADM_SOURCE;OPD_DEPT_CODE;OPD_DR_CODE;PATIENT_CONDITION;DIAG_CODE;DIAG_REMARK;"
                                             + "CLNCPATH_CODE;ADM_DAYS;URG_FLG;BED_CLASS_CODE;BILPAY;OPER_DESC;OPER_DATE;REMARK;RECRUIT_NO;DAY_OPE_FLG",
                                     result, 0);
            TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(parm);// add by wanglong 20121025
            this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong 20121025
            if (result.getData("RESV_DATE", 0) != null) {
                this.setValue("RESV_DATE", result.getTimestamp("RESV_DATE", 0));
            }
            this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
            this.setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
            this.setValue("DR_CODE", result.getValue("DR_CODE", 0));
            BED_NO = result.getValue("BED_NO", 0);
            if (result.getValue("BED_NO", 0).length() > 0) {
                this.setValue("BED_NO", StringUtil.getDesc("SYS_BED", "BED_NO_DESC", "BED_NO='"
                        + result.getValue("BED_NO", 0) + "'"));
            }
            // 得到诊断名称
            String desc = this.getName(getValue("DIAG_CODE").toString());
            this.setValue("DIAG_DESC", desc);
            resvNo = result.getValue("RESV_NO", 0);
            preTreatNO=result.getValue("PRETREAT_NO",0);
            this.setValue("NEW_BORN_FLG", result.getBoolean("NEW_BORN_FLG", 0));// 新生儿注记
            McaseNo = result.getValue("M_CASE_NO", 0);// 母亲的case_no
            int check;
            if ("en".equals(this.getLanguage())) check =
                    this.messageBox("Message", "The patient has an appointment，Whether to amend？", 0);
            else check = this.messageBox("消息", "此病患已预约，是否重新建立？", 0);
            if (check == 0) {
                type = "UPDATE";
                this.setMenu(true);
            } else {
                this.setMenu(false);
                this.callFunction("UI|NEW_BORN_FLG|setEnable", false);
                return;
            }
        } else {
            type = "NEW";
            pat = Pat.onQueryByMrNo(mrNo);
            this.setValue("CTZ1_CODE", pat.getCtz1Code());
            // 如果勾选了 新生儿注记 则调用母婴对应界面
            if (this.getValueBoolean("NEW_BORN_FLG")) {
                this.onChild();
            }
            callFunction("UI|save|setEnabled", true); // 保存
        }
    }
 
    /**
     * 查询病患信息
     */
    public boolean queryPat(String mrNo) {
        this.setMenu(false); //MENU 显示控制
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.setMenu(false); //MENU 显示控制
            this.messageBox("E0081");
            return false;
        }
        //modifiy by liming begin.添加病案号合并提示信息.
        String allMrNo = PatTool.getInstance().checkMrno(mrNo) ;
        if(mrNo!=null && !allMrNo.equals(pat.getMrNo())){
        	//============xueyf modify 20120307 start
        	messageBox("病案号"+allMrNo+" 已合并至"+pat.getMrNo()) ;
        	//============xueyf modify 20120307 stop
        }
        //modify by liming end.
        setValue("MR_NO", pat.getMrNo());
        setValue("IPD_NO", pat.getIpdNo());

        setValue("SEX_CODE", pat.getSexCode());
        setValue("CTZ1_CODE", pat.getCtz1Code());
        setValue("TEL", pat.getTelHome());
        setValue("TEL_NO1", pat.getTelCompany());
        if("en".equals(this.getLanguage())){
            setValue("PAT_NAME", pat.getName1());
            if (pat.getBirthday() != null)
                setValue("AGE",
                         StringTool.CountAgeByTimestamp(pat.getBirthday(),
                    SystemTool.getInstance().getDate())[0] + " Y");
        }else{
            setValue("PAT_NAME", pat.getName());
            if (pat.getBirthday() != null)
                setValue("AGE",
                         com.javahis.util.StringUtil.showAge(pat.getBirthday(),
                    SystemTool.getInstance().getDate()));
        }
        callFunction("UI|save|setEnable", true); //保存
        callFunction("UI|MR_NO|setEnabled", false); //病案号
        return true;
    }

    /**
     * 调用预约管理界面
     * 传回数据
     */
    public void onManage() {
//        TParm parm = pat.getParm();
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\customquery\\QueryView.x",
            new Object[] {"ADMRESV_VIEW"});
        if (reParm == null) {
            return;
        }
        setValue("MR_NO", reParm.getData("MR_NO")); //门急诊科别
        this.onMRNO();
    }

    /**
     * 判断updata ， insert
     * @param parm TParm
     * @return boolean
     */
    public void onSave() {
        if ("NEW".equals(type))
            this.insertData();
        else {
            TParm parm = pat.getParm();
            TParm result = ADMResvTool.getInstance().selectAll(parm);
            this.upDate(result);
            //modify by lim 2012/05/31 begin
          
            //modify by lim 2012/05/31 end
            updateAdmPretreatData();
        }
        
        hrmCaseNo = "";
    }


    /**
     * 新增
     */
    public void insertData() {
        if (!checkData())
            return;
        TParm parm = this.readData(); //得到保存信息
        resvNo = new String();
        resvNo = SystemTool.getInstance().getNo("ALL", "ADM", "RESV_NO",
                                                "RESV_NO"); //调用取号原则
        if (resvNo == null || "".equals(resvNo)) {
            this.messageBox("E0122");
            return;
        }
        parm.setData("RESV_NO", resvNo); //预约号
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMResvAction",
            "insertdata", parm); // 住院登记保存
        if(result.getErrCode()<0){
        	this.messageBox("保存失败！"+result.getErrText());
        	return;
        }
        //判断是否符合临床路径
        TParm inBscParm = new TParm();
        inBscParm.setData("ICD_CODE", this.getValueString("DIAG_CODE"));
        TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
        int clpCount = bscParm.getCount("CLNCPATH_CODE");
        String clpPath = "";
        if ("".equals(this.getValueString("CLNCPATH_CODE"))) {
            if (clpCount > 0) {
            clpPath = bscParm.getValue("CLNCPATH_CODE");
            this.messageBox("建议进入" + clpPath + "临床路径");
            }
        }
        else {
            if (clpCount > 0) {
                clpPath = bscParm.getValue("CLNCPATH_CODE");
                if (!this.getValueString("CLNCPATH_CODE").equals(clpPath))
                    this.messageBox("与诊断对应临床路径不一致");
            }
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        else {
            if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by wanglong 20121025
                updateADMResvSDInfo();//插入单病种信息
            }
            type = "UPDATE";
            setValue("RESV_NO", resvNo); //预约单号为
            this.setMenu(true);
            this.messageBox("P0005");
            onPrint1();
        }
        inserAdmPretreatData();//插入数据到 预登记 表huangjw
    }
    
    /**
     * 插入数据到 预登记 表add  by huangjw 20150612
     */
    public void inserAdmPretreatData(){
    	preTreatNO=SystemTool.getInstance().getNo("ALL", "ADM", "PRETREAT_NO",
        "PRETREAT_NO"); //调用取号原则
    	TParm parm=getData();
    	TParm bedParm=new TParm();
    	if(!"".equals(this.getValue("BED_NO")) && this.getValue("BED_NO")!=null){
    		bedParm.setData("BED_NO",bedNo);
    		bedParm.setData("PRE_PATNAME",this.getValue("PAT_NAME"));
        	bedParm.setData("PRE_SEX",this.getValue("SEX_CODE"));
    	}
    	TParm param=new TParm();
    	param.setData("PARM",parm.getData());
    	param.setData("BEDPARM",bedParm.getData());
    	TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMResvAction",
                "insertPretreat", param); // 预登记数据保存
    	if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
    	String sql=" UPDATE ADM_RESV SET PRETREAT_NO='"+preTreatNO+"' WHERE RESV_NO='"+resvNo+"' ";
    	new TParm(TJDODBTool.getInstance().update(sql));
    }
    /**
     * 插入数据到 预登记 表add  by huangjw 20150612
     */
    public void updateAdmPretreatData(){
    	TParm parm=getData();
    	TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMResvAction",
                "updatePretreat", parm); // 预登记数据更新
    	if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
    	
    }
    /**
     * 获得数据
     * @return
     */
    public TParm getData(){
    	TParm parm=new TParm();
    	parm.setData("PRETREAT_NO",preTreatNO);
    	parm.setData("MR_NO",this.getValue("MR_NO"));
//    	parm.setData("IPD_NO","");
    	parm.setData("PRETREAT_IN_DEPT",getValue("DEPT_CODE"));
    	parm.setData("PRETREAT_IN_STATION",getValue("STATION_CODE"));
    	//判断上午与下午
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(SystemTool.getInstance().getDate().getTime());
		calendar.get(Calendar.AM_PM);
		switch (calendar.get(Calendar.AM_PM)) {
		case Calendar.AM:
			parm.setData("PRETREAT_DATE",getValue("RESV_DATE").toString().substring(0,10).replaceAll("-", "/")+" 12:00:00");
			break;
		case Calendar.PM:
			parm.setData("PRETREAT_DATE",getValue("RESV_DATE").toString().substring(0,10).replaceAll("-", "/")+" 23:59:59");
			break;

		}
		//================
    	parm.setData("PRETREAT_TYPE","1");
    	parm.setData("PATIENT_CONDITION",getValue("PATIENT_CONDITION"));
    	if(!"".equals(this.getValue("BED_NO")) && this.getValue("BED_NO")!=null){
    		parm.setData("EXEC_FLG","Y");
    	}else{
    		parm.setData("EXEC_FLG","N");
    	}
    	parm.setData("OPT_TREAM",Operator.getIP());
    	parm.setData("OPT_USER",Operator.getID());
    	parm.setData("OPT_DATE",SystemTool.getInstance().getDate().toString().substring(0,19).replaceAll("-", "/"));
    	return parm;
    }
    /**
     * 住院证打印
     * @param parm TParm
     */
    public void onPrint1() {
        if (resvNo.length() <= 0)
            return;
        TParm p = new TParm();
        p.setData("RESV_NO", resvNo);
        TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p);
        TParm actionParm = new TParm();
       
        this.setDayOpeFlg("Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术":"");			//   2017/3/24   	by  yanmm   增加日间手术勾选		
        actionParm.setData("DAY_OPE_FLG","Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术":"");
        
        actionParm.setData("MR_NO", pat.getMrNo());
        actionParm.setData("IPD_NO", pat.getIpdNo());
        actionParm.setData("PAT_NAME", pat.getName());
        actionParm.setData("SEX", pat.getSexString());
        actionParm.setData("AGE", StringUtil.showAge(pat.getBirthday(), resvPrint
                .getTimestamp("APP_DATE", 0))); // 年龄
        actionParm.setData("REMARK", resvPrint.getValue("REMARK", 0)); // 特殊事项
        Timestamp ts = SystemTool.getInstance().getDate();
        actionParm.setData("CASE_NO", resvNo);
        actionParm.setData("ADM_TYPE", "O");
        actionParm.setData("DEPT_CODE", resvPrint.getValue("DEPT_CODE", 0));
        actionParm.setData("STATION_CODE", resvPrint.getValue("STATION_CODE", 0));
        actionParm.setData("ADM_DATE", ts);
        actionParm.setData("STYLETYPE", "1");
        actionParm.setData("RULETYPE", "3");
        actionParm.setData("SYSTEM_TYPE", "ODO");  
        TTextFormat diseDesc = (TTextFormat) this.getComponent("DISE_CODE");// add by wanglong 20121025
        actionParm.setData("DISE_DESC", diseDesc.getText());// add by wanglong 20121025
        actionParm.setData("BED_NO", BED_NO);// add by chenxi 20130308
        TParm emrFileData = new TParm();
        String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
        String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        emrFileData.setData("TEMPLET_PATH", path);
        emrFileData.setData("EMT_FILENAME", fileName);
        emrFileData.setData("SUBCLASS_CODE", subClassCode);
        emrFileData.setData("CLASS_CODE", classCode);
        emrFileData.setData("RESV_NO", resvNo);
        actionParm.setData("ADM_TYPE_ZYZ",adm_type_zyz);//20130820,yanj
        actionParm.setData("EMR_FILE_DATA", emrFileData);
        actionParm.addListener("EMR_LISTENER",this,"emrListener");			//   2017/3/24   	by  yanmm   增加日间手术勾选		
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
      //yanj,20130820,添加当为门急诊时打印完自动关闭该界面
		if(adm_type_zyz.equals("O")||adm_type_zyz.equals("E")){
			this.closeWindow();
			return;
		}
    }    
    /**
     * 事件
     * @param parm TParm
     */
    public void emrListener(TParm parm)					//   2017/3/24   	by  yanmm   增加日间手术勾选		
    {
        parm.runListener("setCaptureValue","DAY_OPE_FLG",this.getDayOpeFlg());
        
    }
    /**
     * 修改预约信息
     */
    public void upDate(TParm parm) { 
        if (!checkData())
            return;
        TParm data = this.readData(); //得到保存信息
        TParm resv = ADMResvTool.getInstance().selectAll(data);
        data.setData("RESV_NO", resv.getData("RESV_NO", 0));
        data.setData("OPT_USER", Operator.getID());
        data.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMResvAction",
            "upDate", data); // 住院登记保存
        if (result.getErrCode() < 0)
            this.messageBox("E0005");
        else
            this.messageBox("P0005");
        if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by wanglong 20121025
            updateADMResvSDInfo();//插入单病种信息
        }
        onPrint1() ;
    }

    /**
     * 住院证打印
     * @param parm TParm
     */
    public void onPrint() {
    	String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='"+resvNo+"' ORDER BY OPT_DATE DESC " ;
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql)) ;	
    	if(result1.getErrCode() < 0){
            this.messageBox("E0005");
            return;    		
    	}
    	if(result1.getCount() < 0){
    		this.onPrint1() ;
    	}else{
    		String filePath = result1.getValue("FILE_PATH",0) ;
    		String fileName = result1.getValue("FILE_NAME",0) ;
    		TParm p = new TParm();
            p.setData("RESV_NO",resvNo);
            TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p); 
    		TParm parm = new TParm() ;
    		parm.setData("MR_NO",pat.getMrNo()) ;
    		parm.setData("IPD_NO", pat.getIpdNo());
    		parm.setData("PAT_NAME",pat.getName()) ;
    		parm.setData("SEX", pat.getSexString());
    		parm.setData("AGE",StringUtil.showAge(pat.getBirthday(),
                                       resvPrint.getTimestamp("APP_DATE",0))); //年龄
    		parm.setData("CASE_NO",resvNo) ;
    		Timestamp ts = SystemTool.getInstance().getDate() ;
    		parm.setData("ADM_TYPE","O") ;
    		parm.setData("DEPT_CODE",resvPrint.getValue("DEPT_CODE",0)) ;
    		parm.setData("STATION_CODE",resvPrint.getValue("STATION_CODE",0)) ;
    		parm.setData("ADM_DATE", ts);
    		parm.setData("STYLETYPE", "1");
    		parm.setData("RULETYPE", "3");
    		parm.setData("SYSTEM_TYPE","ODO") ;
    		this.setDayOpeFlg("Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术": "");			//   2017/3/24   	by  yanmm   增加日间手术勾选		
    		parm.setData("DAY_OPE_FLG","Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "日间手术": "") ;
    		String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE") ;
    		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE") ;    		
    		TParm emrFileData = new TParm() ;
    		emrFileData.setData("FILE_PATH",filePath)  ;
    		emrFileData.setData("FILE_NAME",fileName) ;
    		emrFileData.setData("FILE_SEQ",result1.getValue("FILE_SEQ",0)) ;   		
    		emrFileData.setData("SUBCLASS_CODE",subClassCode) ;
    		emrFileData.setData("CLASS_CODE",classCode) ;
    		
    		emrFileData.setData("FLG",true) ;
    		
    		parm.setData("ADM_TYPE_ZYZ",adm_type_zyz);//20130820,yanj
    		
    		parm.setData("EMR_FILE_DATA", emrFileData);
    		parm.addListener("EMR_LISTENER",this,"emrListener");		//   2017/3/24   	by  yanmm   增加日间手术勾选		
    		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    		//yanj,20130820,添加当为门急诊时打印完自动关闭该界面
    		if(adm_type_zyz.equals("O")||adm_type_zyz.equals("E")){
    			this.closeWindow();
    			return;
    		}
    	}
    }
    
//    private String convertString(String str){
//    	int i = Integer.valueOf(str.substring(6,8))+1 ;
//    	if(String.valueOf(i).length() == 2){
//    		return String.valueOf(i) ;
//    	}else{
//    		return "0"+String.valueOf(i) ;
//    	}
//    }

    /**
     * 读取界面信息
     * @return TParm
     */
    public TParm readData() {
        TParm parm = new TParm();
        parm.setData("MR_NO", getValue("MR_NO")); //病案号
        parm.setData("SEX_CODE", getValue("SEX_CODE")); //性别
        parm.setData("ADM_SOURCE", getValue("ADM_SOURCE")); //病患来源
        parm.setData("APP_DATE", getValue("APP_DATE")); //预订日期
        parm.setData("BED_CLASS_CODE", getValue("BED_CLASS_CODE")); //病床等级
        parm.setData("RESV_DATE", getValue("RESV_DATE")); //预约住院日
        parm.setData("ADM_DAYS", getValue("ADM_DAYS")); //预约天数
        parm.setData("URG_FLG", getValue("URG_FLG")); //紧急注记
        parm.setData("BED_CLASS_CODE", getValue("BED_CLASS_CODE")); //病床等级
        parm.setData("DEPT_CODE", getValue("DEPT_CODE")); //科别代码
        parm.setData("STATION_CODE", getValue("STATION_CODE")); //住院病区
        parm.setData("DR_CODE", getValue("DR_CODE")); //医师代码
        parm.setData("BED_NO", BED_NO); //床位号
        parm.setData("DIAG_CODE", getValue("DIAG_CODE")); //主诊断
        parm.setData("DIAG_REMARK", getValue("DIAG_REMARK")); //主诊断备注
        parm.setData("OPD_DEPT_CODE", getValue("OPD_DEPT_CODE")==null?"":getValue("OPD_DEPT_CODE")); //门急诊科室
        parm.setData("OPD_DR_CODE", getValue("OPD_DR_CODE")==null?"":getValue("OPD_DR_CODE")); //门急诊科室
        TNull timeNull = new TNull(Timestamp.class);
        if (getValue("OPER_DATE") == null)
            parm.setData("OPER_DATE", timeNull); //预订手术日期
        else
            parm.setData("OPER_DATE", getValue("OPER_DATE")); //预订手术日期
        parm.setData("OPER_DESC", getValue("OPER_DESC")); //手术名称
        parm.setData("TEL", getValue("TEL")); //电话
        parm.setData("TEL_NO1", getValue("TEL_NO1")); //电话1
        parm.setData("BILPAY", getValueDouble("BILPAY")); //预交金
        parm.setData("CTZ1_CODE", getValue("CTZ1_CODE")); //身份别
        parm.setData("CLNCPATH_CODE", getValue("CLNCPATH_CODE")==null?new TNull(String.class ):getValue("CLNCPATH_CODE")); //临床路径代码DRG_CODE
        parm.setData("DRG_CODE", getValue("DRG_CODE")); //DRG_CODE
        parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION")); //病人状况
        parm.setData("REMARK", getValue("REMARK")); //体别注意事项 CONFIRM_NO
        parm.setData("CONFIRM_NO", getValue("CONFIRM_NO")); //资格证书编号
        parm.setData("NOTIFY_TIMES", getValueInt("NOTIFY_TIMES")); //通知次数
        parm.setData("NOTIFY_DATE", getValue("NOTIFY_DATE")); //最后一次通知日期
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("NEW_BORN_FLG",this.getValueString("NEW_BORN_FLG"));//新生儿注记
        parm.setData("M_CASE_NO",McaseNo);//母亲的病案号
        // add by wangb 2016/1/25 增加门急诊就诊号与住院信息关联
        String opdCaseNo = "";
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm recptParm = (TParm) obj;
            opdCaseNo = recptParm.getValue("CASE_NO");
        } else {
        	// add by wangb 2016/7/11 关联一期临床体检信息
        	if (StringUtils.isNotEmpty(hrmCaseNo)) {
        		opdCaseNo = hrmCaseNo;
        	}
        }
        parm.setData("OPD_CASE_NO", opdCaseNo);//门急诊预约住院就诊号
        
        parm.setData("RECRUIT_NO", this.getValue("RECRUIT_NO"));//受试者编号 add by guangl 20160719
        parm.setData("DAY_OPE_FLG", this.getValue("DAY_OPE_FLG"));//预约住院时标识日间手术病患 add by huangtt20161130

        return parm;
    }

    /**
     * 检核页面数据
     * @return boolean
     */
    public boolean checkData() {
        if(this.getValue("CTZ1_CODE")==null||
            "".equals(this.getValue("CTZ1_CODE"))){
            this.messageBox("E0123");
            this.grabFocus("CTZ1_CODE");
            return false;
        }
        if (this.getValue("ADM_SOURCE") == null ||
            "".equals(this.getValue("ADM_SOURCE"))) {
            this.messageBox("E0124");
            this.grabFocus("ADM_SOURCE");
            return false;
        }
        if (this.getValue("PATIENT_CONDITION") == null ||
            "".equals(this.getValue("PATIENT_CONDITION"))) {
            this.messageBox("E0125");
            this.grabFocus("PATIENT_CONDITION");
            return false;
        }
        if (this.getValue("DIAG_CODE") == null ||
            "".equals(this.getValue("DIAG_CODE"))) {
            this.messageBox("E0126");
            this.grabFocus("DIAG_CODE");
            return false;
        }
        if (this.getValue("RESV_DATE") == null ||
            "".equals(this.getValue("RESV_DATE"))) {
            this.messageBox("E0127");
            this.grabFocus("RESV_DATE");
            return false;
        }
        //begin modify by liming 2012/02/17
        if (this.getValue("DEPT_CODE") == null ||
            "".equals(this.getValue("DEPT_CODE"))) {
            //this.messageBox("E0128");
        	this.messageBox("请输入预订住院科室");
            this.grabFocus("DEPT_CODE");
            return false;
        }
        
       
        if (this.getValue("STATION_CODE") == null ||
                "".equals(this.getValue("STATION_CODE"))) {
                this.messageBox("请输入预订住院病区");
                this.grabFocus("STATION_CODE");
                return false;
            }  
        if (this.getValue("DR_CODE") == null ||
                "".equals(this.getValue("DR_CODE"))) {
                this.messageBox("请输入预订住院医师");
                this.grabFocus("DR_CODE");
                return false;
            }         
        //end
        Timestamp time = (Timestamp)this.getValue("RESV_DATE");
        Timestamp now = SystemTool.getInstance().getDate();
        int re = StringTool.getDateDiffer(time, now);
        if (re < 0) {
            this.messageBox("E0129");
            return false;
        }
        
        //PIC权限者 “受试者编号”为必填项 by guangl 20160719
        if(this.getPopedem("PIC") && "".equals(this.getValue("RECRUIT_NO"))){
        	this.messageBox("请输入受试者编号");
        	this.grabFocus("RECRUIT_NO");
        	return false;
        }
        return true;
    }

    /**
     * 检查是否住院中 true 住院中 false 未住院
     * @return boolean
     */
    public boolean checkAdmInp() {
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
        if (result.checkEmpty("IPD_NO", result))
            return false;
        return true;
    }

    /**
     * 清空
     */
    public void onClear() {
        resvNo = new String();
        pat = new Pat();
        McaseNo = "";
        this.setMenu(false);
        callFunction("UI|MR_NO|setEnabled", true); //保存
        //新增受试者编号编辑框清空 by guangl 2016-7-19
        clearValue("RESV_NO;MR_NO;IPD_NO;AGE;SEX_CODE;PAT_NAME;OPD_DEPT_CODE;OPD_DR_CODE;DIAG_CODE;DIAG_DESC;DIAG_REMARK;" +
                   "ADM_DAYS;STATION_CODE;DR_CODE;BED_CLASS_CODE;OPER_DATE;OPER_CODE;OPER_DESC;CAN_CLERK;CAN_DATE;BILPAY;" +
                   "CLNCPATH_CODE;REMARK;NOTIFY_TIMES;NOTIFY_DATE;CAN_REASON_CODE;ADM_SOURCE;CTZ1_CODE;PR_DEPT_CODE;TEL;"+
                   "TEL_NO1;PATIENT_CONDITION;URG_FLG;DEPT_CODE;BED_NO;DRG_CODE;CONFIRM_NO;OPER_DATE;"+
                   "NEW_BORN_FLG;CLNCPATH_DESC;RECRUIT_NO;DAY_OPE_FLG");
        this.grabFocus("MR_NO");
        setValue("APP_DATE", SystemTool.getInstance().getDate()); //预定日期
        setValue("RESV_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(),1)); //预约住院日
        this.callFunction("UI|NEW_BORN_FLG|setEnable",true);
        hrmCaseNo = "";
    }

    /**
     * 床位检索
     */
    public void onBedNo() {
        TParm sendParm = new TParm();
        sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
        sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
        sendParm.setData("PRETREAT_DATE", getValue("RESV_DATE").toString());//预约住院日期
        sendParm.setData("SEX_CODE", getValue("SEX_CODE").toString());//预约住院日期
        sendParm.setData("TYPE", "RESV") ;   //chenxi modify 20130301 
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMBedDetail.x", sendParm);
        if(reParm==null){
            return;
        }
        this.setValue("BED_NO", reParm.getValue("BED_NO_DESC", 0)); //显示床号名称
        bedNo=reParm.getValue("BED_NO",0);
        BED_NO = reParm.getValue("BED_NO", 0); //记录床号
    }

    /**
     * 通知
     */
    public void onNotify() {
        resvNo = getValue("RESV_NO").toString();
        if (resvNo == null || "".equals(resvNo)) {
            this.messageBox("E0130");
            return;
        }
        if (this.checkAdmInp()) {
            this.messageBox("E0131");
            this.setMenu(false);
            return;
        }
        TParm sendParm = new TParm();
        sendParm.setData("RESV_NO", resvNo);
        sendParm.setData("MR_NO", getValue("MR_NO"));
        sendParm.setData("PAT_NAME", getValue("PAT_NAME"));
        sendParm.setData("OPT_USER", Operator.getID());
        sendParm.setData("OPT_TERM", Operator.getIP());

        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMResvNotify.x", sendParm);
    }

    /**
     * 取消预约
     */
    public void onCanResv() {
        TParm sendParm = new TParm();
        sendParm.setData("RESV_NO", resvNo);
        sendParm.setData("MR_NO", getValue("MR_NO"));
        sendParm.setData("PAT_NAME", getValue("PAT_NAME"));
        sendParm.setData("OPT_USER", Operator.getID());
        sendParm.setData("OPT_TERM", Operator.getIP());
        sendParm.setData("BED_NO", BED_NO);
        if (this.checkAdmInp()) {
            this.messageBox("E0131");
            this.setMenu(false);
            return;
        }
        TParm can = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMCanResv.x", sendParm);
        if (can == null) {
            return;
        }
        if (can.getValue("CAN").equals("true")) { //如果返回值为“true” 已取消预约，清空所有信息
        	//===取消预约 时，删除
            TParm parm=new TParm();
            parm.setData("PRETREAT_NO",this.getValue("RESV_NO").toString());
            String sql=" DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO='"+this.getValue("RESV_NO")+"'";
            new TParm(TJDODBTool.getInstance().update(sql));
            if(!"".equals(this.getValue("BED_NO")) && this.getValue("BED_NO")!=null){
            	sql="UPDATE SYS_BED SET PRE_FLG='',PRETREAT_DATE='',PRE_MRNO=''," +
        		" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+bedNo+"'";
	        	new TParm(TJDODBTool.getInstance().update(sql));
            }
            this.onClear();
            
        }
    }

    /**
     * 科室Cobbo事件
     */
    public void onDeptCode() {
        //科室变化后病区combo会对应变化所以要清空病区combo的选中值和床位号
        this.clearValue("STATION_CODE;BED_NO;DR_CODE");
        BED_NO = "";
    }

    /**
     * 病区combo事件
     */
    public void onStation() {
        //清空床位
        this.clearValue("BED_NO");
        BED_NO = "";
    }

    /**
     * 主诊断码 为空时 主诊断中文不显示
     */
    public void onDiagLost() {
        if (this.getValueString("DIAG_CODE").trim().length() <= 0) {
            this.setValue("DIAG_DESC", "");
        }
    }
    /**
     * 门诊科别Combo事件
     */
    public void onOPD_DEPT_CODE(){
        //清空门诊医师的选中值
        this.clearValue("OPD_DR_CODE");
    }
    /**
     * 新生儿登记
     */
    public void onChild() {
        if (pat == null) {
            this.messageBox("没有病患信息！");
            return;
        }
        TParm sendParm = new TParm();
        sendParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMBabyFlg.x", sendParm);
        if (reParm == null){
            setValue("NEW_BORN_FLG", "N");
            return;
        }
        if (reParm.checkEmpty("IPD_NO", reParm)) {
            setValue("NEW_BORN_FLG", "N");
        }
        else {
            setValue("NEW_BORN_FLG", "Y");
            McaseNo = reParm.getData("M_CASE_NO").toString();
        }
    }
    /**
     * 新生儿注记事件
     */
    public void onNEW_BORN_FLG(){
        if(this.getValueBoolean("NEW_BORN_FLG")){
            if(!"".equals(this.getValueString("MR_NO"))){
                //判断是否已经查询病患,如果已查询那么直接调用母婴对应界面  负责先查询病患信息
                if (pat!=null&&!"".equals(pat.getMrNo()))
                    this.onChild();
                else
                    this.onMRNO();
            }
        }
    }
    /**
     * 调用病患信息界面
     */
    public void onPatInfo() {
        TParm parm = new TParm();
        parm.setData("RESV", "RESV");
        parm.setData("MR_NO", this.getValueString("MR_NO").trim());
        TParm result = (TParm)this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x",parm);
        if("".equals(this.getValueString("MR_NO"))){
            this.setValue("MR_NO",result.getValue("MR_NO"));
            this.onMRNO();
        }
    }
    /**
     * 设置临床路径
     */
    public void setClncpathDesc(){
        TextFormatCLPBscInfo clncPath = (TextFormatCLPBscInfo)this.getComponent("CLNCPATH_CODE");
//        System.out.println("临床路径"+clncPath.getValue());
//        System.out.println("临床路径1111"+this.getText("CLNCPATH_CODE"));
//        System.out.println("临床路径222"+clncPath.getPopupMenuData());
//        System.out.println("临床路径33333"+clncPath.getName());
//        System.out.println("临床路径4444444"+this.getName("CLNCPATH_CODE"));
//        System.out.println("临床路径55555"+clncPath.getSelectText());
//        System.out.println("临床路径666666"+clncPath.getShowColumnList());
    setValue("CLNCPATH_DESC",clncPath.getText());
    }
    
    /**
     * 插入单病种信息
     */
    public void updateADMResvSDInfo() {// add by wanglong 20121025
        TParm action = new TParm();
        action.setData("RESV_NO", resvNo);
        action.setData("DISE_CODE", this.getValue("DISE_CODE")+"");
        TParm result = CLPSingleDiseTool.getInstance().updateADMResvSDInfo(action);
        if (result.getErrCode() < 0) {
            messageBox("单病种信息保存失败");
            return;
        }
    }
    
    /**
     * 关联一期临床筛选期与基线期体检信息
     */
    public void linkHrmInfo() {
    	String mrNo = this.getValueString("MR_NO");
    	if (StringUtils.isEmpty(mrNo)) {
    		this.messageBox("病案号不能为空");
    		return;
    	}
    	
    	TParm parm = new TParm();
    	parm.setData("MR_NO", mrNo);
    	parm.setData("TYPE", type);
    	parm.setData("RESV_NO", resvNo);
    	
    	// 一期临床体检信息
		TParm selParm = (TParm) this.openDialog(
				"%ROOT%\\config\\hrm\\HRMPicInfo.x", parm);
		
		if (selParm == null) {
			return;
		}

		if (StringUtils.isNotEmpty(selParm.getValue("CASE_NO"))) {
			hrmCaseNo = selParm.getValue("CASE_NO").replace("[", "").replace(
					"]", "").replace(" ", "");
		}
    }
}
