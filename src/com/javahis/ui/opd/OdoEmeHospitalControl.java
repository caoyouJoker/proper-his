package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JOptionPane;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.mro.MROQueueTool;
import jdo.mro.MROTool;
import jdo.opd.ODOEmeHospitalTool;
import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;
/**
 * 紧急住院
 * @author Administrator
 *
 */
public class OdoEmeHospitalControl extends TControl{
	
	Pat pat;
	private TParm recptParm;
	String OPD_CASE_NO;//就诊号
	String resvNo = "";//预约单号
	String caseNo = "";//住院就诊号
	String IPD_NO = "";//住院号
	 String preTreatNO="";
	 String bedNo="";
	int check;
	String BED_NO_OLD;
	String PRETREAT_NO;
	public void onInit(){
		
	//	super.onInit();
		this.recptParm = new TParm();
		Object obj = this.getParameter();
		if(obj.toString().length()!=0 || obj!=null ){
			//==start==add by kangy 20160919======锁定界面所有控件
			callFunction("UI|ADM_SOURCE|setEnabled", false);
			callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
			callFunction("UI|OPD_DR_CODE|setEnabled", false);
			callFunction("UI|SERVICE_LEVEL|setEnabled", false);
			callFunction("UI|CTZ1_CODE|setEnabled", false);
			callFunction("UI|PATIENT_CONDITION|setEnabled", false);
			callFunction("UI|DIAG_CODE|setEnabled", false);
			callFunction("UI|DEPT_CODE|setEnabled", false);
			callFunction("UI|STATION_CODE|setEnabled", false);
			callFunction("UI|DR_CODE|setEnabled", false);
			callFunction("UI|RESV_DATE|setEnabled", false);
			//==end==add by kangy 20160919======锁定界面所有控件
			this.recptParm = (TParm) obj;
			resvNo=recptParm.getValue("RESV_NO",0);
			//==start==modify by kangy  20160919
			OPD_CASE_NO = (String) recptParm.getData("CASE_NO");
			//this.setValue("CASE_NO", recptParm.getData("CASE_NO",0));
			this.setValue("MR_NO", recptParm.getData("MR_NO",0));
			//pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());
			pat = Pat.onQueryByMrNo(recptParm.getData("MR_NO",0).toString().trim());
			this.setValue("MR_NO", recptParm.getData("MR_NO",0));
			this.setValue("NAME", recptParm.getData("NAME"));
			this.setValue("SEX_CODE",recptParm.getData("SEX_CODE",0));
			this.setValue("AGE",  OdoUtil.showAge(pat.getBirthday(),
					SystemTool.getInstance().getDate()));
		/*	if ("O".equalsIgnoreCase((String) recptParm.getData("ADM_TYPE",0))) {
				this.setValue("ADM_SOURCE", "01");
			} else if ("E".equalsIgnoreCase((String) recptParm.getData("ADM_TYPE",0))) {
				this.setValue("ADM_SOURCE", "02");
			}*/
			this.setValue("ADM_SOURCE", recptParm.getData("ADM_SOURCE",0));
			this.setValue("OPD_DEPT_CODE", recptParm.getData("OPD_DEPT_CODE",0));
			//System.out.println("zhaolingling+++++"+recptParm.getData("OPD_DEPT_CODE",0));
			this.setValue("OPD_DR_CODE", recptParm.getData("OPD_DR_CODE",0));
			//==start==modify by kangy  20160919
			this.setValue("SERVICE_LEVEL", "1");
			this.setValue("CTZ1_CODE", "99");
			//==start===add by kangy===20160919
			this.setValue("PATIENT_CONDITION", recptParm.getData("PATIENT_CONDITION",0));
			this.setValue("DEPT_CODE", recptParm.getData("DEPT_CODE",0));
			this.setValue("STATION_CODE", recptParm.getData("STATION_CODE",0));
			this.setValue("DR_CODE", recptParm.getData("DR_CODE",0));
			this.setValue("DIAG_CODE", recptParm.getData("DIAG_CODE",0));
			this.setValue("DIAG_DESC", recptParm.getData("DIAG_DESC"));
			//==end===add by kangy===20160919
//			System.out.println("预约住院床位号：：："+recptParm.getData("BED_NO",0));
			
			this.setValue("BED_NO", getBedDesc(recptParm.getValue("BED_NO",0)));
//			this.setValue("BED_DESC", recptParm.getData("BED_NO",0));
			this.setValue("RESV_DATE", SystemTool.getInstance().getDate());
//			System.out.println("床位号：：："+getValue("BED_DESC"));
			BED_NO_OLD = recptParm.getValue("BED_NO",0);
			PRETREAT_NO = (String) recptParm.getData("PRETREAT_NO",0);
			
		}
		if(checkAdmInp(pat.getMrNo())){
			this.messageBox("此病患住院中！");
			callFunction("UI|save|setEnabled", false); // 保存
			//callFunction("UI|print|setEnabled", false); 
		}

		callFunction("UI||setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");
		// textfield接受回传值
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
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
           // this.grabFocus("DIAG_REMARK");
        }
    }
    
    /**
	 * 床位检索
	 */
	public void onBedNo() {
		TParm sendParm = new TParm();
		if (getValue("DEPT_CODE") == null || "".equals(getValue("DEPT_CODE"))) {
			this.messageBox("请选择科室！");
			return;
		}
		if (getValue("STATION_CODE") == null
				|| "".equals(getValue("STATION_CODE"))) {
			this.messageBox("请选择病区！");
			return;
		}
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("TYPE", "RESV"); // ===== chenxi modify 20130301
											// 预约床时，占床也可预约
//		sendParm.setData("HAVEBEDNO",haveBedNo);
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
		if (reParm != null) {
			this.setValue("BED_NO", getBedDesc(reParm.getValue("BED_NO", 0)));
//			this.setValue("YELLOW_SIGN", reParm.getData("YELLOW_SIGN", 0));
//			this.setValue("RED_SIGN", reParm.getData("RED_SIGN", 0));
		}
	}
	
	/**
	 * 根据床位code
	 * 
	 * @param Bed_Code
	 *            String
	 * @return String
	 */
	private String getBedDesc(String Bed_Code) {
		this.setValue("BED_DESC", Bed_Code);
		TComboBox combo = (TComboBox) this.getComponent("BED_DESC");
		return combo.getSelectedName();
	}
	
    
    public TParm readData(){
    	TParm parm = new TParm();
    	parm.setData("OPD_CASE_NO", OPD_CASE_NO);//就诊号
    	parm.setData("MR_NO", getValue("MR_NO"));//病案号
    	parm.setData("NAME", getValue("NAME"));//病患名称
    	parm.setData("SEX_CODE", getValue("SEX_CODE"));//性别
    	parm.setData("AGE", getValue("AGE"));//年龄
    	parm.setData("ADM_SOURCE", getValue("ADM_SOURCE"));//病患来源
    	parm.setData("OPD_DEPT_CODE", getValue("OPD_DEPT_CODE"));//门急诊科别
    	parm.setData("OPD_DR_CODE", getValue("OPD_DR_CODE"));//门急诊医生
    	parm.setData("SERVICE_LEVEL", getValue("SERVICE_LEVEL"));//服务等级
    	parm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));//付款方式
    	parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION"));//病人状况
    	parm.setData("DIAG_CODE", getValue("DIAG_CODE"));//主诊断
    	parm.setData("DEPT_CODE", getValue("DEPT_CODE"));//住院科室
    	parm.setData("STATION_CODE", getValue("STATION_CODE"));//住院病区
    	parm.setData("DR_CODE", getValue("DR_CODE"));//住院医生
    	parm.setData("RESV_DATE", SystemTool.getInstance().getDateReplace(getValueString("RESV_DATE"), true));//住院日期
    	parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		
		parm.setData("MAINDIAG", getValue("DIAG_CODE"));//主诊断
		parm.setData("NEW_BORN_FLG", "N"); // 新生儿注记
		parm.setData("TOTAL_BILPAY", ""); // 预交金
		parm.setData("CTZ2_CODE", ""); // 2
		parm.setData("CTZ3_CODE",""); // 3
		parm.setData("IN_COUNT", ""); // 入院次数
		//parm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(getValueString("RESV_DATE"), true)); // 3入院日期
		parm.setData("IN_DATE", getValue("RESV_DATE"));
		parm.setData("BED_NO", getValue("BED_DESC")); // 床位号
		parm.setData("VS_DR_CODE", getValue("DR_CODE"));
		parm.setData("ATTEND_DR_CODE", getValue("DR_CODE")); // 主治医师
		parm.setData("ADM_DATE", SystemTool.getInstance().getDateReplace(getValueString("RESV_DATE"), true)); // 登记日期
		parm.setData("RED_SIGN",""); // 红色警戒
		parm.setData("YELLOW_SIGN", ""); // 黄色警戒
		parm.setData("AGN_CODE","");
		parm.setData("AGN_INTENTION", "");
		parm.setData("M_CASE_NO", "");
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("IN_DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("STATION_CODE"));
		parm.setData("ADM_CLERK", Operator.getID()); // 住院登记作业员
	    //  带入历次过敏史记录  machao  start
		String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND "
        		+ " MR_NO = '"+parm.getValue("MR_NO")+"' AND"
        		+ " DRUG_TYPE is not null AND "
        		+ " DRUG_TYPE <> 'N' ";
		TParm res = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
        
        parm.setData("ALLERGY", res.getCount("MR_NO")>0?"Y":"N");
        //带入历次过敏史记录  machao  end
    	return parm;
    }
	
    
    /**
	 * 检查是否住院中 false 未住院 true 住院中
	 * 
	 * @param MrNo
	 *            String
	 * @return boolean
	 */
	public boolean checkAdmInp(String MrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", MrNo);
		TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
		if (result.checkEmpty("IPD_NO", result))
			return false;
		caseNo = result.getData("CASE_NO", 0).toString();
		return true;
	}
	
	
	/**
	 * 界面数据检查
	 * 
	 * @return Boolean
	 */
	public Boolean checkData() {
		if ("".equals(this.getValueString("ADM_SOURCE"))) {
			this.messageBox_("请输入病患来源");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DEPT_CODE"))) {
			this.messageBox_("请输入门急诊科室");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DR_CODE"))) {
			this.messageBox_("请输入门急诊医生");
			return false;
		}
		if ("".equals(this.getValueString("SERVICE_LEVEL"))) {
			this.messageBox_("请选择服务等级");
			return false;
		}
		if ("".equals(this.getValueString("CTZ1_CODE"))) {
			this.messageBox_("请输入付款方式");
			return false;
		}
		if ("".equals(this.getValueString("PATIENT_CONDITION"))) {
			this.messageBox_("请选择病患状态");
			return false;
		}
		if ("".equals(this.getValueString("DIAG_CODE"))){
			this.messageBox_("请输入主诊断");
			return false;
		}
		if ("".equals(this.getValueString("DEPT_CODE"))) {
			this.messageBox_("请输入住院科别");
			return false;
		}
		if ("".equals(this.getValueString("STATION_CODE"))) {
			this.messageBox_("请输入住院病区");
			return false;
		}
		if ("".equals(this.getValueString("DR_CODE"))) {
			this.messageBox_("请输入经治医师");
			return false;
		}
		if ("".equals(this.getValueString("RESV_DATE"))) {
			this.messageBox_("请输入住院日期");
			return false;
		}
		if("".equals(this.getValueString("BED_DESC"))){
			this.messageBox_("请选择床位");
			return false;
		}
		return true;
	}
    
	
	/*//住院证打印
	public void onPrint(){
		TParm parm = new TParm();
		if (!this.checkData()) // 检查数据
			return;
		//保存ADM_RESV表
		resvNo = new String();
		resvNo = SystemTool.getInstance().getNo("ALL", "ADM", "RESV_NO",
         										"RESV_NO"); //调用取号原则
		//parm.setData("RESV_NO", resvNo); //预约号
//		parm.setData("CASE_NO", resvNo); //就诊号
//		parm.setData("IPD_NO", IPD_NO);
//		
//		parm.setData("MR_NO", getValue("MR_NO"));
//	    parm.setData("PAT_NAME", getValue("NAME"));//病患名称
//	    parm.setData("SEX", getValue("SEX_CODE"));//性别
//	    parm.setData("AGE", getValue("AGE"));//年龄
//        Timestamp ts = SystemTool.getInstance().getDate();
//        parm.setData("ADM_TYPE", "O");
//        parm.setData("DEPT_CODE", getValue("DEPT_CODE"));//住院科室
//    	parm.setData("STATION_CODE", getValue("STATION_CODE"));//住院病区
//        parm.setData("ADM_DATE", ts);
//        parm.setData("STYLETYPE", "1");
//        parm.setData("RULETYPE", "3");
//        parm.setData("SYSTEM_TYPE", "ODO");
//		
//		
//		String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
//	    String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
//		
//	    String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
//        sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";
//
//        //System.out.println("===sql===" + sql);
//        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
//        String filePath = result1.getValue("FILE_PATH", 0);
//        String fileName = result1.getValue("FILE_NAME", 0);
//	    
//		TParm emrFileData = new TParm();
//		emrFileData.setData("FILE_PATH", filePath);
//		emrFileData.setData("FILE_NAME", fileName);
//		emrFileData.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
//		emrFileData.setData("SUBCLASS_CODE", subClassCode);
//		emrFileData.setData("CLASS_CODE", classCode);
//		emrFileData.setData("FLG", true);
//		parm.setData("EMR_FILE_DATA", emrFileData);
		
		
		
		
		
		
		TParm actionParm = new TParm();
        actionParm.setData("MR_NO", pat.getMrNo());
        actionParm.setData("IPD_NO", pat.getIpdNo());
        actionParm.setData("PAT_NAME", pat.getName());
        actionParm.setData("SEX", pat.getSexString());
        actionParm.setData("AGE", getValue("AGE")); //年龄
        Timestamp ts = SystemTool.getInstance().getDate();
        //actionParm.setData("CASE_NO", caseNo);
        actionParm.setData("CASE_NO", resvNo); //duzhw add
        actionParm.setData("ADM_TYPE", "O");
        actionParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
        actionParm.setData("STATION_CODE", getValue("STATION_CODE"));
        actionParm.setData("ADM_DATE", ts);
        actionParm.setData("STYLETYPE", "1");
        actionParm.setData("RULETYPE", "3");
        actionParm.setData("SYSTEM_TYPE", "ODO");
        TParm emrFileData = new TParm();
        String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
        String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        emrFileData.setData("TEMPLET_PATH", path);
        emrFileData.setData("EMT_FILENAME", fileName);
        emrFileData.setData("SUBCLASS_CODE", subClassCode);
        emrFileData.setData("CLASS_CODE", classCode);
        actionParm.setData("EMR_FILE_DATA", emrFileData);
		this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
	}*/
	

	
	public void onSave(){
		if (!this.checkData()) // 检查数据
			return;
//		if (!this.checkHospCard()) {
//			this.messageBox("请点击\"住院证打印\"按钮，并保存住院证");
//			return;
//		}
		TParm parm = this.readData();
		if(checkAdmInp((String)parm.getData("MR_NO"))){
			this.messageBox("此病患住院中！");
			callFunction("UI|save|setEnabled", false); 
			//callFunction("UI|print|setEnabled", false); 
			return;
		}
		if (!this.checkHospCard()) {
			this.messageBox("请先保存住院证");
			onPrint();
			return;
		}
//		resvNo = SystemTool.getInstance().getNo("ALL", "ADM", "RESV_NO",
//			"RESV_NO"); //调用取号原则

		if (resvNo == null || "".equals(resvNo)) {
			this.messageBox("E0122");
			return;
		}
		
		TParm bed = new TParm();
		bed.setData("BED_NO", parm.getData("BED_NO"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bed);
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")) {
				this.messageBox("此床已占用,请重新选择床位");
				return;
			}
		}
		
		//保存ADM_INP表
		caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
												"CASE_NO"); // 调用取号原则

//		ipdNo = SystemTool.getInstance().getNo("ALL", "OPE", "IPD_NO", 
//												"IPD_NO");//调用取号原则
		bedNo = parm.getData("BED_NO").toString();
		if ("Y".equals(parm.getValue("NEW_BORN_FLG"))) {
			IPD_NO = parm.getValue("IPD_NO").toString();
		} else {
			IPD_NO = pat.getIpdNo(); // 判断该病患是否住过院
			if ("".equals(IPD_NO))
				IPD_NO = SystemTool.getInstance().getIpdNo();

			if ("".equals(IPD_NO)) {
				this.messageBox_("住院号取参错误！");
				return;
			}
		}
		parm.setData("RESV_NO", resvNo); //预约号
		parm.setData("CASE_NO", caseNo); //就诊号
		parm.setData("URG_FLG", "Y");
		parm.setData("IPD_NO", IPD_NO);
		TParm result = new TParm();
	    parm.setData("BED_NO", bedNo);
		TParm operParm=ODOEmeHospitalTool.getInstance().getMessage(parm);
		if(operParm.getCount()>0){
			if(null!=operParm.getValue("STATE",0) && "1".equals(operParm.getValue("STATE",0))){
				check = this.messageBox("消息", "急诊手术已经排程，是否继续操作？", 0);
				if(check!=0){
					return;
				}
			}
		}else{
			//parm.setData("STATE_FLG", "1");
			check = this.messageBox("消息", "不存在急诊手术，是否继续操作？", 0);
			if(check!=0){
				return;
			}
		}
		String fileServerMainRoot = TIOM_FileServer
				.getPath("FileServer.Main.Root");
		String emrData = TIOM_FileServer.getPath("EmrData");
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='" + resvNo
		+ "'  ORDER BY OPT_DATE DESC ";
		 System.out.println("======sql===###################===="+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {
		
		} else {
			// 移动JHW文件并改名称.
			String oldFileName = result1.getValue("FILE_NAME", 0);
			String oldFilePath = result1.getValue("FILE_PATH", 0);
			String seq = result1.getValue("FILE_SEQ", 0);
			System.out.println(fileServerMainRoot + emrData + oldFilePath
					+ "\\" + oldFileName + ".jhw");
		
			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					fileServerMainRoot + emrData + oldFilePath + "\\"
							+ oldFileName + ".jhw");
		
			Timestamp ts = SystemTool.getInstance().getDate();
			String dateStr = StringTool.getString(ts, "yyyyMMdd");
			// 获得新的文件路径
			StringBuilder filePathSb = new StringBuilder();
			filePathSb.append("JHW\\").append(dateStr.substring(2, 4))
					.append("\\").append(dateStr.substring(4, 6)).append("\\")
					.append(parm.getValue("MR_NO"));
			String newFilePath = filePathSb.toString();
		
			// 获得新的文件名称
			String[] oldFileNameArray = oldFileName.split("_");
		
			StringBuilder sb = new StringBuilder(caseNo);
			sb.append("_").append(oldFileNameArray[1]).append("_")
					.append(oldFileNameArray[2]);
			String newFileName = sb.toString();
		
			try {
				// 移动文件
				TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						fileServerMainRoot + emrData + newFilePath + "\\"
								+ newFileName + ".jhw", data);
				// this.messageBox("=====resvNo====="+resvNo);
				TParm action = new TParm(
						this.getDBTool()
								.select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
										+ caseNo + "'"));
				int index = action.getInt("MAXFILENO", 0);
				// 更新数据库
				String sql1 = "UPDATE EMR_FILE_INDEX SET CASE_NO='" + caseNo
						+ "',FILE_PATH='" + newFilePath + "',FILE_NAME='"
						+ newFileName + "',FILE_SEQ='" + index
						+ "' WHERE CASE_NO='" + resvNo + "' AND FILE_SEQ='"
						+ seq + "'";
		
				// System.out.println("======sql11111========"+sql1);
				TParm result2 = new TParm(TJDODBTool.getInstance().update(sql1));
				if (result2.getErrCode() < 0) {
					err(result2.getErrName() + "" + result2.getErrText());
					messageBox("更新失败!");
					return;
				}
				// 删除老文件；
				boolean delFlg = TIOM_FileServer.deleteFile(
						TIOM_FileServer.getSocket(), fileServerMainRoot
								+ emrData + oldFilePath + "\\" + oldFileName
								+ ".jhw");
				if (!delFlg) {
					this.messageBox("删除原文件失败!");
					return;
				}
			} catch (Exception e) {
				this.messageBox("移动文件失败!");
			}
		}	
		
		
		//FILE_SEQ
		TParm action = new TParm(
				this.getDBTool()
						.select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
								+ resvNo + "'"));
		int fileSeq = action.getInt("MAXFILENO", 0);
		//FILE_PATH
		Timestamp ts = SystemTool.getInstance().getDate();
		String dateStr = StringTool.getString(ts, "yyyyMMdd");
		String date = StringTool.getString(ts, "yyyy/MM/dd hh:mm:ss");
		StringBuilder filePathSb = new StringBuilder();
		filePathSb.append("JHW\\").append(dateStr.substring(2, 4))
				.append("\\").append(dateStr.substring(4, 6)).append("\\")
				.append(parm.getValue("MR_NO"));
		String filePath = filePathSb.toString();
		//FILE_NAME
		String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
	    String designName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
	    StringBuilder sb = new StringBuilder(resvNo);
		sb.append("_").append(designName).append("_")
				.append(fileSeq);
		String fileName = sb.toString();
		//DESIGN_NAME
		StringBuilder sb2 = new StringBuilder(designName);
		sb2.append("(").append(date).append(")");
		String DesignName = sb2.toString();
	    String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
	    String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
	    parm.setData("FILE_SEQ", fileSeq);
	    parm.setData("FILE_NAME", fileName);
	    parm.setData("FILE_PATH", filePath);
	    parm.setData("DESIGN_NAME", DesignName);
	    parm.setData("CLASS_CODE", classCode);
	    parm.setData("SUBCLASS_CODE", subClassCode);
	    parm.setData("DISPOSAC_FLG", "N");
	    parm.setData("CANPRINT_FLG", "Y");
	    parm.setData("MODIFY_FLG", "Y");
	    parm.setData("REPORT_FLG", "N");
	    parm.setData("DAY_OPE_FLG","N");	//   2017/4/23   yanmm    紧急住院无需日间手术  写死
	    
	  //检查性别是否相同
	    TParm room = ADMInpTool.getInstance().selectRoomInfo(
				parm.getValue("BED_NO"));
	    String sexsql ="SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
		TParm data1=new TParm(TJDODBTool.getInstance().select(sexsql));
	    String selectSql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
		" A.ROOM_CODE='"+room.getValue("ROOM_CODE", 0)+"' " +
		" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
		TParm Sexparm=new TParm(TJDODBTool.getInstance().select(selectSql));
//		System.out.println("zhaolingling::::::"+Sexparm);
		if(Sexparm.getCount()>0){
			for(int i=0;i<Sexparm.getCount();i++){
				if(!data1.getValue("SEX_CODE",0).equals(Sexparm.getValue("SEX_CODE",i))){
					if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
		    				JOptionPane.YES_NO_OPTION) == 0){
						break;
					}else{
						return;
					}
					
				}
			}
		}
	    
	    
	    TParm qParm = new TParm();
	    qParm.setData("BED_NO", bedNo);
		TParm occu = SYSBedTool.getInstance().queryAll(qParm);
		String qMrNo = occu.getValue("MR_NO", 0);
	    
		// 检查病患是否包床
		if (checkOccu(caseNo)) {
			parm.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
			if (qMrNo == null
					|| "".equals(qMrNo)//3-->5
					|| parm.getData("MR_NO") != qMrNo) {
				int check = this.messageBox("消息",
						"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
				if (check != 0) {
					return;
				}
				parm.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
			} else {
				parm.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
			}
		} else {
			parm.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
		parm.setData("UPDATE", "");
		parm.setData("BED_NO_OLD", BED_NO_OLD);
//		System.out.println("预约床位：：："+BED_NO_OLD);
        parm.setData("PRETREAT_NO", PRETREAT_NO);
//        System.out.println("预约单号：：："+PRETREAT_NO);
		result = TIOM_AppServer.executeAction("action.opd.ODOEmeHospitalAction",
				"doSave", parm); 
//		inserAdmPretreatData();
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败:" + result.getErrText());
			return;
		}else{
			this.messageBox("P0001");

//			pat.modifyIpdNo(IPD_NO);
//			pat.modifyRcntIpdDate((Timestamp) getValue("RESV_DATE"));
//			System.out.println("++++++++++");
//			pat.modifyRcntIpdDept(parm.getValue("DEPT_CODE").toString());
//			pat.onSave();
		}
	}
	
	
	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
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
    		bedParm.setData("BED_NO","");
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
	 * 验证该病患是否已经生成住院证
	 * 
	 * @return 是否生成住院证
	 * @author wangb
	 */
	private boolean checkHospCard() {
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO = '#' AND CLASS_CODE='"
				+ classCode + "' AND SUBCLASS_CODE='" + subClassCode + "'";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.replaceFirst("#", resvNo)));
        
		if (result.getErrCode() < 0) {
			this.messageBox("查询住院证病历信息错误");
			err("ERR:" + result.getErrText());
			return false;
		}
		
		if (result.getCount() > 0) {
			return true;
		}
		
		return false;
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
     * 打印住院证
     */
    public void onPrint() {
		if (StringUtils.isEmpty(getValueString("MR_NO"))) {
			return;
		}
    	
        String caseNo = "";
        //this.messageBox("mrNo===="+pat.getMrNo());
        TParm resv = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
        //this.messageBox("======resv========"+resv);
        TParm mrParm = new TParm();
        mrParm.setData("MR_NO", pat.getMrNo());
        //TParm result = ADMInpTool.getInstance().checkAdmInp(mrParm);
        //this.messageBox("result============"+result);
        //if (result.getCount() < 0) {
        //    messageBox("该病患未入院！");
        //    return;
        //}
        //预约住院号
        caseNo = resv.getValue("RESV_NO", 0);        
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        
        // 已预约已入住
        if (checkAdmInp(pat.getMrNo())) {
        	caseNo = this.caseNo;
        }

        String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
        sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";

//        System.out.println("===sql===" + sql);
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
        if (result1.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        if (result1.getCount() < 0) {
            this.onPrint1();
        } else {
            String filePath = result1.getValue("FILE_PATH", 0);
            String fileName = result1.getValue("FILE_NAME", 0);
            TParm p = new TParm();
            p.setData("RESV_NO", caseNo);
            TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p);
            TParm parm = new TParm();
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("IPD_NO", pat.getIpdNo());
            parm.setData("PAT_NAME", pat.getName());
            parm.setData("SEX", pat.getSexString());
            parm.setData("AGE", StringUtil.showAge(pat.getBirthday(),
                    resvPrint.getTimestamp("APP_DATE", 0))); //年龄
            //parm.setData("CASE_NO", caseNo);
            parm.setData("CASE_NO", caseNo);//duzhw add
            Timestamp ts = SystemTool.getInstance().getDate();
            parm.setData("ADM_TYPE", "O");
            parm.setData("DEPT_CODE", resvPrint.getValue("DEPT_CODE", 0));
            parm.setData("STATION_CODE", resvPrint.getValue("STATION_CODE", 0)); 
            parm.setData("ADM_DATE", ts);
            parm.setData("STYLETYPE", "1");
            parm.setData("RULETYPE", "3");
            parm.setData("SYSTEM_TYPE", "ODO");
            TParm emrFileData = new TParm();
            emrFileData.setData("FILE_PATH", filePath);
            emrFileData.setData("FILE_NAME", fileName);
            emrFileData.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
            emrFileData.setData("SUBCLASS_CODE", subClassCode);
            emrFileData.setData("CLASS_CODE", classCode);
            emrFileData.setData("FLG", true);
            parm.setData("EMR_FILE_DATA", emrFileData);
            this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
        }
    }

    public void onPrint1() {
        //获取预约号
        String sql = " select * from adm_resv where mr_no = '"+this.getValue("MR_NO")+"' AND CAN_DATE IS NULL AND IN_CASE_NO IS NULL ORDER BY RESV_NO DESC ";
        TParm resvParm = new TParm(TJDODBTool.getInstance().select(sql));
        String resvNo = resvParm.getValue("RESV_NO", 0);
        TParm myParm = new TParm();
        myParm.setData("CASE_NO", caseNo);
        TParm casePrint = ADMInpTool.getInstance().selectall(myParm);
        TParm actionParm = new TParm();
        actionParm.setData("MR_NO", pat.getMrNo());
        actionParm.setData("IPD_NO", pat.getIpdNo());
        actionParm.setData("PAT_NAME", pat.getName());
        actionParm.setData("SEX", pat.getSexString());
        actionParm.setData("AGE", StringUtil.showAge(pat.getBirthday(), casePrint.getTimestamp("IN_DATE", 0))); //年龄
        Timestamp ts = SystemTool.getInstance().getDate();
        //actionParm.setData("CASE_NO", caseNo);
        actionParm.setData("CASE_NO", resvNo); //duzhw add
        actionParm.setData("ADM_TYPE", "O");
        actionParm.setData("DEPT_CODE", casePrint.getValue("DEPT_CODE", 0));
        actionParm.setData("STATION_CODE", casePrint.getValue("STATION_CODE", 0));
        actionParm.setData("ADM_DATE", ts);
        actionParm.setData("STYLETYPE", "1");
        actionParm.setData("RULETYPE", "3");
        actionParm.setData("SYSTEM_TYPE", "ODO");
        TParm emrFileData = new TParm();
        String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
        String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        emrFileData.setData("TEMPLET_PATH", path);
        emrFileData.setData("EMT_FILENAME", fileName);
        emrFileData.setData("SUBCLASS_CODE", subClassCode);
        emrFileData.setData("CLASS_CODE", classCode);
        actionParm.setData("EMR_FILE_DATA", emrFileData);
        actionParm.addListener("EMR_LISTENER",this,"emrListener");			
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
    }


}
