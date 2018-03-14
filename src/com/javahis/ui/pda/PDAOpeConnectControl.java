package com.javahis.ui.pda;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.dongyang.ui.TFrame;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;

public class PDAOpeConnectControl extends TControl{
	private TParm Parameter;// 存放参数
	private String opbookSeq;
	private TWord word;
	private String[] saveFiles;
	private boolean update = false;
	private String fromUser;//交班人
	private String toUser;//接班人
	TParm result = new TParm();
	Timestamp sysDate = SystemTool.getInstance().getDate();
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	public void onInit(){
		super.onInit();
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) { // 判断是否是TParm
				Parameter = (TParm) obj;
			} else {
				this.closeWindow();
			}
		} else {
			this.closeWindow();
		}
		word = (TWord) this.getComponent("TWORD");
		openJhw();
	}
	/**
	 * 打开病例
	 */
	private void openJhw() {
		opbookSeq = Parameter.getValue("OPBOOK_SEQ");
		String sql2 = "SELECT A.CASE_NO,A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ " A.HANDOVER_USER,A.TRANSFER_USER,A.HANDOVER_DATE, "
				+ " A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ " A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ " A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ " (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE   B.GROUP_ID = 'OPE_OPROOM' "
				+ " AND A.ROOM_NO = B.ID(+)) AS  OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ " F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ " J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ " M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,X.DEPT_CHN_DESC,Y.STATION_DESC,"
				+ " (SELECT Z.CHN_DESC FROM SYS_DICTIONARY Z  WHERE   Z.GROUP_ID = 'OPE_SITE'  AND A.PART_CODE = Z.ID(+)) AS PART_CODE,"
				+ " A.GDVAS_CODE,A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,O.ALLERGY "  
				+ " FROM OPE_OPBOOK A,SYS_PATINFO C,SYS_DEPT X,SYS_STATION Y,SYS_DICTIONARY F,"  
				+ " SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"    
				+ " SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N ,ADM_INP O "
				+ " WHERE  A.MR_NO = C.MR_NO (+) "
				+ " AND A.OP_DEPT_CODE=X.DEPT_CODE "
				+ " AND A.OP_STATION_CODE=Y.STATION_CODE "
				+ " AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "
				+ " AND A.DIAG_CODE1 = G.ICD_CODE(+) "
				+ " AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ " AND A.MAIN_SURGEON = I.USER_ID(+) "
				+ " AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ " AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ " AND A.CIRCULE_USER2 = L.USER_ID(+) "  
				+ " AND A.ANA_USER1 = M.USER_ID(+) "
				+ " AND A.EXTRA_USER1 = N.USER_ID(+) " 
				+ " AND A.CASE_NO = O.CASE_NO(+) "
				+ " AND A.OPBOOK_SEQ='"
				+ opbookSeq
				+ "' "
				//根据iproom 还要判断不同手术类型 
				+ " AND A.CANCEL_FLG <> 'Y' " 
				+ " ORDER BY OPBOOK_SEQ DESC ";
	        
//		System.out.println("sql2:"+sql2);
        TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));     
			
        String sql3 =" SELECT  A.AGE,A.BED,A.FROM_DEPT AS FROM_DEPT1 ,A.TO_DEPT AS TO_DEPT1,B.DEPT_CHN_DESC AS TO_DEPT," 
        		+ " C.DEPT_CHN_DESC AS FROM_DEPT,TO_CHAR(A.TRANSFER_DATE,'YYYY-MM-DD HH24:MI:SS') AS TRANSFER_DATE,A.TEMPERATURE,PULSE," 
        		+ " A.RESPIRE,A.SBP AS SYSTOLICPRESSURE, A.DBP AS DIASTOLICPRESSURE,A.ACTIVE_TOOTH_FLG,A.FALSE_TOOTH_FLG," 
        		+ " A.GENERAL_MARK,A.ALLERGIC_FLG,A.ALLERGIC_MARK,A.INFECT_FLG,A.WEIGHT AS WEIGHT_MON,A.SKIN_BREAK_FLG," 
        		+ " A.SKIN_BREAK_POSITION,A.BLOOD_TYPE,A.RHPOSITIVE_FLG,A.CROSS_MATCHUY AS CROSS_MATCH ,A.OPE_PRE_MARK," 
        		+ " A.OPE_INFORM_FLG,A.ANA_SINFORM_FLG,A.BLOOD_INFORM_FLG,A.SKIN_PREPARATION_FLG,A.CROSSMATCH_FLG," 
        		+ " A.SKIN_TEST_FLG,A.PREPARE_EDUCATION_FLG,A.BOWEL_PREPARATION_FLG,A.DENTAL_CARE_FLG,A.NASAL_CARE_FLG," 
        		+ " A.INFECT_SCR_RESULT_CONT,A.RHPOSITIVE_FLG,A.ALLERGIC_MARK " 
        		+ " FROM  INW_TransferSheet_WO A,SYS_DEPT B,SYS_DEPT C "
//        		+ " WHERE  A.OPBOOK_SEQ= '"+opbookSeq+"' "  //modify by wangjc 20171128
        		+ " WHERE  A.TRANSFER_CODE= '"+Parameter.getValue("TRANSFER_CODE")+"' "  
        		+ " AND A.TO_DEPT = B.DEPT_CODE "
        		+ " AND A.FROM_DEPT = C.DEPT_CODE "
        		+ " ORDER BY TRANSFER_DATE DESC";   
	                
//        System.out.println("sql3:"+sql3);
        TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
        String time = StringTool.getString(sysDate, "yyyyMMddHHmmss");
        
        result.setData("CASE_NO", result2.getValue("CASE_NO",0));
        result.setData("MR_NO", result2.getValue("MR_NO",0));
        result.setData("OP_ROOM", result2.getValue("OP_ROOM",0));
        result.setData("OPBOOK_SEQ", result2.getValue("OPBOOK_SEQ",0));
        result.setData("FROM_DEPT", result3.getValue("FROM_DEPT",0));
        result.setData("PAT_NAME", result2.getValue("PAT_NAME",0));
        result.setData("SEX", result2.getValue("SEX",0));
        result.setData("AGE", result3.getValue("AGE",0));
        result.setData("BED", result3.getValue("BED",0));
        
        result.setData("SKIN_PREPARATION_FLG", result3.getValue("SKIN_PREPARATION_FLG",0));
        result.setData("CROSSMATCH_FLG", result3.getValue("CROSSMATCH_FLG",0));
        result.setData("SKIN_TEST_FLG", result3.getValue("SKIN_TEST_FLG",0));
        result.setData("BOWEL_PREPARATION_FLG", result3.getValue("BOWEL_PREPARATION_FLG",0));
        result.setData("PREPARE_EDUCATION_FLG", result3.getValue("PREPARE_EDUCATION_FLG",0));
        result.setData("DENTAL_CARE_FLG", result3.getValue("DENTAL_CARE_FLG",0));
        result.setData("NASAL_CARE_FLG", result3.getValue("NASAL_CARE_FLG",0));
        
        result.setData("TO_DEPT", result3.getValue("TO_DEPT",0));
        result.setData("ICD_CHN_DESC", result2.getValue("ICD_CHN_DESC",0));
        result.setData("OPT_CHN_DESC", result2.getValue("OPT_CHN_DESC",0));
        
        result.setData("TEMPERATURE", result3.getValue("TEMPERATURE",0));
        result.setData("PULSE", result3.getValue("PULSE",0));
        result.setData("RESPIRE", result3.getValue("RESPIRE",0));
        result.setData("TOLICPRESSURE", result3.getValue("SYSTOLICPRESSURE",0)+"/"+result3.getValue("DIASTOLICPRESSURE",0));
        result.setData("GENERAL_MARK", result3.getValue("GENERAL_MARK",0));
        result.setData("INFECT_FLG", result3.getValue("INFECT_FLG",0));
        result.setData("ALLERGIC_FLG", result3.getValue("ALLERGIC_FLG",0));
        
        result.setData("WEIGHT_MON", result3.getValue("WEIGHT_MON",0));
        result.setData("SKIN_BREAK_FLG", result3.getValue("SKIN_BREAK_FLG",0));
        result.setData("SKIN_BREAK_POSITION", result3.getValue("SKIN_BREAK_POSITION",0));
        
        result.setData("BLOOD_TYPE", result3.getValue("BLOOD_TYPE",0));
        result.setData("RHPOSITIVE_FLG", result3.getValue("RHPOSITIVE_FLG",0));
        result.setData("OPE_PRE_MARK", result3.getValue("OPE_PRE_MARK",0));
        
        result.setData("OPE_INFORM_FLG", result3.getValue("OPE_INFORM_FLG",0));
        result.setData("ANA_SINFORM_FLG", result3.getValue("ANA_SINFORM_FLG",0));
        result.setData("BLOOD_INFORM_FLG", result3.getValue("BLOOD_INFORM_FLG",0));
        
        result.setData("ACTIVE_TOOTH_FLG", result3.getValue("ACTIVE_TOOTH_FLG",0));
        result.setData("FALSE_TOOTH_FLG", result3.getValue("FALSE_TOOTH_FLG",0));
        result.setData("DSTR", sysDate);
        result.setData("TRANSFER_DATE", time);

        result.setData("OPT_USER", Operator.getID());
        result.setData("OPT_TERM", Operator.getIP());
        result.setData("EMR_SAVE_MSG_FLG", "N");
        
        String sql4 = "SELECT * FROM INW_TRANSFERSHEET WHERE TRANSFER_CODE= '"+Parameter.getValue("TRANSFER_CODE")+"' ";
        TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));
        result.setData("HANDOVER_USER", this.getUserName(result4.getValue("FROM_USER",0)));
        result.setData("TRANSFER_USER", this.getUserName(result4.getValue("TO_USER",0)));
        if(StringUtils.isNotEmpty(result4.getValue("TRANSFER_DATE",0))){
        	result.setData("HANDOVER_DATE", result2.getValue("HANDOVER_DATE",0).substring(0, result2.getValue("HANDOVER_DATE",0).length()-2));
        	word.onOpen(result4.getValue("TRANSFER_FILE_PATH", 0), result4.getValue("TRANSFER_FILE_NAME", 0), 3, false);
        	
        }else{
        	result.setData("HANDOVER_DATE", "");
//        System.out.println("result:"+result);
        	
        	saveFiles = getPreedFile(result.getValue("MR_NO"), "EMR0603", "EMR0603081", opbookSeq);
        	if(saveFiles == null || saveFiles[0].trim().equals("")){
        		TParm parm = new TParm();
        		update = false;
        		saveFiles = getErdLevelTemplet("EMR0603081");
        		word.onOpen(saveFiles[0], saveFiles[1], 2, false);
        		parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", result.getValue("MR_NO"));
        		word.setWordParameter(parm);
        		word.setMicroField("病案号", result.getValue("MR_NO"));
        		word.setMicroField("术间", result.getValue("OP_ROOM"));
        		word.setMicroField("手术单号", result.getValue("OPBOOK_SEQ"));
        		word.setMicroField("科别", result.getValue("FROM_DEPT"));
        		word.setMicroField("姓名", result.getValue("PAT_NAME"));
        		word.setMicroField("性别", result.getValue("SEX"));
        		word.setMicroField("年龄", result.getValue("AGE"));
        		word.setMicroField("床号", result.getValue("BED"));
        		//皮肤准备
        		if("Y".equals(result.getValue("SKIN_PREPARATION_FLG"))){
        			setCheckBoxChooseChecked("SKIN_PREPARATION_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("SKIN_PREPARATION_FLG", word, false);
        		}
        		//交叉配血
        		if("Y".equals(result.getValue("CROSSMATCH_FLG"))){
        			setCheckBoxChooseChecked("CROSSMATCH_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("CROSSMATCH_FLG", word, false);
        		}
        		//皮试
        		if("Y".equals(result.getValue("SKIN_TEST_FLG"))){
        			setCheckBoxChooseChecked("SKIN_TEST_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("SKIN_TEST_FLG", word, false);
        		}
        		//肠道准备
        		if("Y".equals(result.getValue("BOWEL_PREPARATION_FLG"))){
        			setCheckBoxChooseChecked("BOWEL_PREPARATION_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("BOWEL_PREPARATION_FLG", word, false);
        		}
        		//术前宣教
        		if("Y".equals(result.getValue("PREPARE_EDUCATION_FLG"))){
        			setCheckBoxChooseChecked("PREPARE_EDUCATION_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("PREPARE_EDUCATION_FLG", word, false);
        		}
        		//口腔清洁
        		if("Y".equals(result.getValue("DENTAL_CARE_FLG"))){
        			setCheckBoxChooseChecked("DENTAL_CARE_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("DENTAL_CARE_FLG", word, false);
        		}
        		//鼻腔清洁
        		if("Y".equals(result.getValue("NASAL_CARE_FLG"))){
        			setCheckBoxChooseChecked("NASAL_CARE_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("NASAL_CARE_FLG", word, false);
        		}
        		//转入科室TO_DEPT
//			setCaptureValueArray(word, "TO_DEPT", result.getValue("TO_DEPT"));
        		this.setFixedValue("TO_DEPT", word, result.getValue("TO_DEPT"));
        		//入院诊断ICD_CHN_DESC
//			setCaptureValueArray(word, "ICD_CHN_DESC", result.getValue("ICD_CHN_DESC"));
        		this.setFixedValue("ICD_CHN_DESC", word, result.getValue("ICD_CHN_DESC"));
        		//拟行手术OPT_CHN_DESC
//			setCaptureValueArray(word, "OPT_CHN_DESC", result.getValue("OPT_CHN_DESC"));
        		this.setFixedValue("OPT_CHN_DESC", word, result.getValue("OPT_CHN_DESC"));
        		//T  TEMPERATURE
//			setCaptureValueArray(word, "TEMPERATURE", result.getValue("TEMPERATURE"));
        		this.setFixedValue("TEMPERATURE", word, result.getValue("TEMPERATURE")+" ℃");
        		//P  PULSE
//			setCaptureValueArray(word, "PULSE", result.getValue("PULSE"));
        		this.setFixedValue("PULSE", word, result.getValue("PULSE")+" 次/分");
        		//R  RESPIRE
//			setCaptureValueArray(word, "RESPIRE", result.getValue("RESPIRE"));
        		this.setFixedValue("RESPIRE", word, result.getValue("RESPIRE")+" 次/分");
        		//BP  TOLICPRESSURE
//			setCaptureValueArray(word, "TOLICPRESSURE", result.getValue("TOLICPRESSURE"));
        		this.setFixedValue("TOLICPRESSURE", word, result.getValue("TOLICPRESSURE")+" mmHg");
        		//备注GENERAL_MARK
//			setCaptureValueArray(word, "GENERAL_MARK", result.getValue("GENERAL_MARK"));
        		this.setFixedValue("GENERAL_MARK", word, result.getValue("GENERAL_MARK"));
        		//传染病INFECT_FLG
//			setCaptureValueArray(word, "INFECT_FLG", result.getValue("INFECT_FLG"));
        		this.setFixedValue("INFECT_FLG", word, result.getValue("INFECT_FLG"));
        		//过敏ALLERGIC_FLG
        		if("Y".equals(result.getValue("ALLERGIC_FLG"))){
        			setCheckBoxChooseChecked("ALLERGIC_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("ALLERGIC_FLG", word, false);
        		}
        		//书晨体重WEIGHT_MON
//			setCaptureValueArray(word, "WEIGHT_MON", result.getValue("WEIGHT_MON"));
        		this.setFixedValue("WEIGHT_MON", word, result.getValue("WEIGHT_MON")+"KG");
        		//皮肤破损SKIN_BREAK_FLG
        		if("Y".equals(result.getValue("SKIN_BREAK_FLG"))){
        			setCheckBoxChooseChecked("SKIN_BREAK_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("SKIN_BREAK_FLG", word, false);
        		}
        		//破损部位SKIN_BREAK_POSITION
//			setCaptureValueArray(word, "SKIN_BREAK_POSITION", result.getValue("SKIN_BREAK_POSITION"));
        		this.setFixedValue("SKIN_BREAK_POSITION", word, result.getValue("SKIN_BREAK_POSITION"));
        		//血型BLOOD_TYPE
        		if("T".equals(result.getValue("BLOOD_TYPE"))){
//				setCaptureValueArray(word, "BLOOD_TYPE", "不确定");
        			this.setFixedValue("BLOOD_TYPE", word, "不确定");
        		}else{
//				setCaptureValueArray(word, "BLOOD_TYPE", result.getValue("BLOOD_TYPE"));
        			this.setFixedValue("BLOOD_TYPE", word, result.getValue("BLOOD_TYPE"));
        		}
        		//HR  RHPOSITIVE_FLG
        		if("Y".equals(result.getValue("RHPOSITIVE_FLG"))){
        			//
        			setCheckBoxChooseChecked("RHPOSITIVE_FLG1", word, true);
        			setCheckBoxChooseChecked("RHPOSITIVE_FLG2", word, false);
        		} else {
        			setCheckBoxChooseChecked("RHPOSITIVE_FLG1", word, false);
        			setCheckBoxChooseChecked("RHPOSITIVE_FLG2", word, true);
        		}
        		//术前备注OPE_PRE_MARK
//			setCaptureValueArray(word, "OPE_PRE_MARK", result.getValue("OPE_PRE_MARK"));
        		this.setFixedValue("OPE_PRE_MARK", word, result.getValue("OPE_PRE_MARK"));
        		//手术同意书OPE_INFORM_FLG
        		if("Y".equals(result.getValue("OPE_INFORM_FLG"))){
        			setCheckBoxChooseChecked("OPE_INFORM_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("OPE_INFORM_FLG", word, false);
        		}
        		//麻醉同意书ANA_SINFORM_FLG
        		if("Y".equals(result.getValue("ANA_SINFORM_FLG"))){
        			setCheckBoxChooseChecked("ANA_SINFORM_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("ANA_SINFORM_FLG", word, false);
        		}
        		//输血同意书BLOOD_INFORM_FLG
        		if("Y".equals(result.getValue("BLOOD_INFORM_FLG"))){
        			setCheckBoxChooseChecked("BLOOD_INFORM_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("BLOOD_INFORM_FLG", word, false);
        		}
        		//活动牙齿ACTIVE_TOOTH_FLG
        		if("Y".equals(result.getValue("ACTIVE_TOOTH_FLG"))){
        			setCheckBoxChooseChecked("ACTIVE_TOOTH_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("ACTIVE_TOOTH_FLG", word, false);
        		}
        		//义齿FALSE_TOOTH_FLG
        		if("Y".equals(result.getValue("FALSE_TOOTH_FLG"))){
        			setCheckBoxChooseChecked("FALSE_TOOTH_FLG", word, true);
        		} else {
        			setCheckBoxChooseChecked("FALSE_TOOTH_FLG", word, false);
        		}
        		//交班人员
//			setCaptureValueArray(word, "HANDOVER_USER", result.getValue("HANDOVER_USER"));
        		this.setFixedValue("HANDOVER_USER", word, result.getValue("HANDOVER_USER"));
        		//接班人员
//			setCaptureValueArray(word, "TRANSFER_USER", result.getValue("TRANSFER_USER"));
        		this.setFixedValue("TRANSFER_USER", word, result.getValue("TRANSFER_USER"));
        		//交班时间
//			setCaptureValueArray(word, "HANDOVER_DATE", result.getValue("HANDOVER_DATE"));
        		this.setFixedValue("HANDOVER_DATE", word, result.getValue("HANDOVER_DATE"));
        	} else {
        		update = true;
        		word.onOpen(saveFiles[0], saveFiles[1], 3, false);
        	}
        }
			
			//交班人员HANDOVER_USER
//			if("Y".equals(result3.getValue("ICD_CHN_DESC",0))){
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, true);
//			} else {
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, false);
//			}
			//接班人员TRANSFER_USER
//			if("Y".equals(result3.getValue("ICD_CHN_DESC",0))){
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, true);
//			} else {
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, false);
//			}
			//交接时间TRANSFER_DATE
//			if("Y".equals(result3.getValue("ICD_CHN_DESC",0))){
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, true);
//			} else {
//				setCheckBoxChooseChecked("ICD_CHN_DESC", word, false);
//			}
	}
	/**
	 * 保存
	 */
	public boolean onSave(){
		boolean falg = true;
		String path ="";
		String fileName = "";
		String r = getECheckBoxChooseValue("RHPOSITIVE_FLG1", word);
		String h = getECheckBoxChooseValue("RHPOSITIVE_FLG2", word);
		
        
		if(("Y".equals(r) && "Y".equals(h)) || ("N".equals(r)) && "N".equals(h)){
			this.messageBox("血型请正确勾选！");
		} else {
			if(!isBlank(result.getValue("HANDOVER_USER")) && !isBlank(result.getValue("TRANSFER_USER")) ){
				this.messageBox("交接单已保存，无法再修改！");
			} else {
				if(!isBlank(getFixedValue("HANDOVER_USER", word)) && !isBlank(getFixedValue("TRANSFER_USER", word)) ){
					result.setData("HANDOVER_USER", getUserID(getFixedValue("HANDOVER_USER", word)));
			        result.setData("TRANSFER_USER", getUserID(getFixedValue("TRANSFER_USER", word)));
			        result.setData("dstrYMD", sysDate.toString().substring(0, 11));
			        result.setData("TRANSFER_CODE", this.Parameter.getValue("TRANSFER_CODE"));
					TParm resultSave = TIOM_AppServer.executeAction(
							"action.pda.PDAaction", "onSaveOpeConnect",
							this.result);
//					System.out.println("resultSave:"+resultSave);
					if (resultSave.getErrCode() < 0) {
						err(resultSave.getErrCode() + " " + resultSave.getErrText());
						this.messageBox("E0001");
					} else {
						//判断是否保存
						this.result.setData("EMR_SAVE_MSG_FLG", "N");
						//保存病例文件
						path = resultSave.getValue("PATH");
						fileName = resultSave.getValue("FILENAME");
						word.setMessageBoxSwitch(false);
						word.onSaveAs(path, fileName, 3);
//						if(this.update){//已有病例
//							path = saveFiles[0];
//							fileName = saveFiles[1];
//						} else {//新建
//						}
//					System.out.println("path:"+path+";;;;;"+fileName);
//						if(!isBlank(path) && !isBlank(fileName)){
//						}
						this.messageBox("保存成功");
					}
				}else{
					this.messageBox("交接人员请签字！");
				}
			}
		}
		return falg;
	}
	/**
	 * 打印
	 */
	public void onPrint() {
		result.setData("HANDOVER_USER", getUserID(getFixedValue("HANDOVER_USER", word)));
        result.setData("TRANSFER_USER", getUserID(getFixedValue("TRANSFER_USER", word)));
		if(isBlank(result.getValue("HANDOVER_USER")) || isBlank(result.getValue("TRANSFER_USER"))){
			this.messageBox("交接人员请签字！");
		} else {
			if (this.getWord().getFileOpenName() != null) {
				this.getWord().onPreviewWord();
				this.getWord().print();
//			this.closeWindow();
			} else {
				// 请选择病历
				this.messageBox("E0099");
			}
		}
	}
	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 退出时弹出提示
	 */
//	public boolean onClosing() {
//		if ("Y".equals(result3.getValue("EMR_SAVE_MSG_FLG"))) {
//			switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
//			case 0:
//				if (!onSave()) {
//					return false;
//				}
//				break;
//			case 1:
//				break;
//			case 2:
//				return false;
//			}
//			// add by wangb 2017/1/9  介入护理平台介入安全核查单关闭时自动调用清空按钮
////			((TParm) obj).runListener("CLEAR_LISTENER", new TParm());
//		} else {
//			if (this.messageBox("询问", "是否关闭？", 2) != 0) {
//				return false;
//			}
//		}
//		
//		super.onClosing();
////		this.setReturnValue(returnParm);
//		// 退出自动保存定时器
//		// this.cancel();
//		return true;
//	}
	 /**
     * 拿到模板
     * @return
     */
    public String[] getErdLevelTemplet(String subClassCode){ 	
        TParm result = new TParm();
        String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH," +
        		"SEQ,EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"+subClassCode+"'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        String s[] = null;
        if (result.getCount("CLASS_CODE") > 0) {
            s = new String[] {
                result.getValue("TEMPLET_PATH", 0),
                result.getValue("SUBCLASS_DESC", 0),
                result.getValue("SUBCLASS_CODE", 0)};
        }
        return s;     
    }
    /**
     * 拿到之前保存的病历
     * @param caseNo String
     * @return String[]
     */
	 public String[] getPreedFile(String caseNo, String classCode, String subclassCode, String opBookSeq){
//	        String classCode = TConfig.getSystemValue("AMI_PRE_CLASSCODE");
//	        String subclassCode = TConfig.getSystemValue("AMI_PRE_SUBCLASSCODE");
	        TParm emrParm = new TParm(TJDODBTool.getInstance().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"' AND OPBOOK_SEQ = '"+opBookSeq+"'"));
//	        System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'AND OPBOOK_SEQ = '"+opBookSeq+"'");

	        String dir="";
	        String file="";
	        String subClassCode = "";
	        if(emrParm.getCount()>0){
	            dir = emrParm.getValue("FILE_PATH",0);
	            file = emrParm.getValue("FILE_NAME",0);
	            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
	            String s[] = {dir,file,subClassCode};
	            return s;
	        }else{
	        	return null;
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
	 * 交班人签字
	 */
	public void onFromuser() {
		String type = "transfer";	
		TParm parm  = new TParm();
		parm.setData("TYPE",type);
		parm.addListener("onReturnfromuser", this,
				"onReturnfromuser");		
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);			
	} 
	/**
	 * 接班人签字
	 */
	public void onTouser() {
		String type = "transfer";
		TParm parm  = new TParm();
		parm.setData("TYPE",type);
		parm.addListener("onReturntouser", this,
				"onReturntouser");
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);	
	}
	/**
     * 交班人
     * @param inParm
     */
	public void onReturnfromuser(TParm inParm) {
		//		System.out.println("onReturnfromuser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		fromUser =inParm.getValue("USER_ID");
		//	      System.out.println("fromUser====="+fromUser);
//		setCaptureValueArray(word, "HANDOVER_USER", this.getUserName(fromUser));
		this.setFixedValue("HANDOVER_USER", word, this.getUserName(fromUser));
//		EComponent com = this.getWord().getPageManager().findObject(
//				"HANDOVER_USER", EComponent.FIXED_TYPE);
//		EFixed d =(EFixed) com;
//		//	     System.out.println("EFixed交班人====="+d);
//		if (d != null) {
//			d.setText(this.getUserName(fromUser));
//			this.getWord().update();	
//		}   
	}
	 /**
     * 接班人
     * @param inParm
     */
    public void onReturntouser(TParm inParm) {
		//		System.out.println("onReturntouser====="+inParm);
		String OK = inParm.getValue("RESULT");
		
		if (!OK.equals("OK")) {
			return;
		}				
		toUser =inParm.getValue("USER_ID");
//		setCaptureValueArray(word, "TRANSFER_USER", this.getUserName(toUser));
		this.setFixedValue("TRANSFER_USER", word, this.getUserName(toUser));
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
//		setCaptureValueArray(word, "HANDOVER_DATE", dateStr);
		this.setFixedValue("HANDOVER_DATE", word, dateStr);
		//	      System.out.println("toUser====="+toUser);
//		EComponent com = this.getWord().getPageManager().findObject(
//				"TRANSFER_USER", EComponent.FIXED_TYPE);
//		EFixed d =(EFixed) com;
//		//			System.out.println("EFixed接班人====="+d);
//		if (d != null) {
//			d.setText(this.getUserName(toUser));
//			this.getWord().update();
//		}
//		EComponent com1 = this.getWord().getPageManager().findObject(
//				"TRANSFER_DATE", EComponent.FIXED_TYPE);
//		EFixed d1 =(EFixed) com1;		
//		if (d1 != null) {
//			String dateStr = StringTool.getString(SystemTool.getInstance()
//					.getDate(), "yyyy/MM/dd HH:mm:ss");
//			d1.setText(dateStr);
//			this.getWord().update();
//		}
	}
    /**
  	 * 拿到交班人和接班人姓名
  	 */
  	public String getUserName(String userId) {
  		TParm parm = new TParm(this.getDBTool().select(
  				" SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
  						+ userId + "'"));
  		return parm.getValue("USER_NAME", 0);
  	}
  	/**
	 * 拿到交班人和接班人ID
	 */
	public String getUserID(String userName) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT USER_ID FROM SYS_OPERATOR WHERE USER_NAME ='"
						+ userName + "'"));
		return parm.getValue("USER_ID", 0);
	}
	/**
	 * 获取宏控件值
	 * @param name
	 * @param word
	 * @return
	 */
	public String getMicroFieldValue(String name, TWord word){
		EMicroField mf = (EMicroField) word.findObject(name, EComponent.MICRO_FIELD_TYPE);
		String value = mf.getText();
		return value;
	}
	
    /**
	 * 设置抓取框
	 * 
	 * @param name String
	 * @param value String
	 */
	public void setCaptureValueArray(TWord word, String name, String value) {
		ECapture ecap = word.findCapture(name);
		if (ecap == null) return;
		ecap.setFocusLast();
		ecap.clear();
		this.word.pasteString(value);
	}
	/**
	 * 获取抓取控件值
	 * @param name
	 * @param word
	 * @return
	 */
	public String getCaptureValue(String name, TWord word){
		//		ECapture sysTime = (ECapture) word.findObject(capture, EComponent.CAPTURE_TYPE);
		ECapture ecap = word.findCapture(name);
		String value = ecap.getValue();
		return value;
	}
	/**
	 * 获取单选框
	 * @param cbcName
	 * @param word
	 * @return
	 */
	public String getECheckBoxChooseValue(String cbcName, TWord word){
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(cbcName, EComponent.CHECK_BOX_CHOOSE_TYPE);
		String cbcValue = cbc.isChecked() ? "Y":"N";
		return cbcValue;
	}
	/**
	 * 设置选择框控件值
	 * @param name
	 * @param word
	 * @param value
	 */
	public void setCheckBoxChooseChecked(String name, TWord word, boolean value){
		ECheckBoxChoose sc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		sc.setChecked(value);
	}
	/**
	 * <p>获取固定文本值</p>
	 * @param name
	 * @param word
	 * @return
	 */
	public String getFixedValue(String name, TWord word){
		EFixed e = word.findFixed(name);
		return e.getText();
	}
	/**
	 * <p>设置固定文本值</p>
	 * @param name
	 * @param word
	 * @return
	 */
	public void setFixedValue(String name, TWord word, String value){
		EFixed e = word.findFixed(name);
		e.clearString();
		e.addString(value);
		this.word.update();
	}

}
