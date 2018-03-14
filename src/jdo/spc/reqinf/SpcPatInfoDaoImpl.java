package jdo.spc.reqinf;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

import jdo.spc.reqinf.dto.SysPatinfo;

public class SpcPatInfoDaoImpl extends TJDOTool {
		
	/**
	 * 实例
	 */
	public static SpcPatInfoDaoImpl instanceObject;
	
	/**
	 * 得到实例
	 * @return INDTool
	 */
	public static SpcPatInfoDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcPatInfoDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcPatInfoDaoImpl() {
		onInit();
	}
	
	public String onSaveSpcPatInfo(SysPatinfo patinfo) {
		
		///////////////////////////
		SimpleDateFormat logFormater = new SimpleDateFormat("yyMMdd HHmmss");
		TParm logParm = new TParm();	//日志
		
		logParm.setData("BATCH_CODE","PAT_INFO同步");
		logParm.setData("START_TIME",logFormater.format(new Date()));		
		logParm.setData("END_TIME",logFormater.format(new Date()));
		////////////////////////////
		
		
		String mrNo = patinfo.getMrNo();
		if(mrNo==null||"".equals(mrNo)) {
			
			////////////////////////////
			String resultDesc = "MR_NO IS NULL";
			logParm.setData("RESULT_DESC",resultDesc);
			SpcPatInfoTool.getInstance().saveLog(logParm);
			////////////////////////////
			
			return "MR_NO IS NULL";
		}
		
		String sql = "SELECT MR_NO FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
	            
				////////////////////////////
				String resultDesc = "QUERY PAT BY MR_NO FAILED :SELECT MR_NO FROM SYS_PATINFO WHERE MR_NO = " + mrNo;
				logParm.setData("RESULT_DESC",resultDesc);
				SpcPatInfoTool.getInstance().saveLog(logParm);
				////////////////////////////
				
	            return "QUERY PAT BY MR_NO FAILED" + result.getErrText();
	    }
		
		String checkResult = checkPatInf(patinfo);
		if(!"OK".equals(checkResult)){
			
			////////////////////////////
			String resultDesc = checkResult;
			logParm.setData("RESULT_DESC",resultDesc + "----mrNo:" + patinfo.getMrNo() );
			SpcPatInfoTool.getInstance().saveLog(logParm);
			////////////////////////////
			
			return checkResult;
		}
		
		TParm parm = new TParm();
	
				
//		private String mrNo;
//		private String sexCode;
//		private String ipdNo;
//		private String deleteFlg;
//		private String mergeFlg;		
		parm.setData("MR_NO", patinfo.getMrNo());
		parm.setData("SEX_CODE",patinfo.getSexCode());
		parm.setData("IPD_NO", patinfo.getIpdNo());
		parm.setData("DELETE_FLG", patinfo.getDeleteFlg());
		parm.setData("MERGE_FLG", patinfo.getMergeFlg());
		
//		private String motherMrno;
//		private String patName;
//		private String patName1;
//		private String py1;
//		private String py2;
		parm.setData("MOTHER_MRNO",patinfo.getMotherMrno());
		parm.setData("PAT_NAME",patinfo.getPatName());		
		parm.setData("PAT_NAME1",patinfo.getPatName1());
		parm.setData("PY1",patinfo.getPy1());
		parm.setData("PY2",patinfo.getPy2());
		
//		private String foreignerFlg;
//		private String idno;
//		private String birthDate;
//		private String ctz1Code;
//		private String ctz2Code;
		parm.setData("FOREIGNER_FLG",patinfo.getForeignerFlg());
		parm.setData("IDNO",patinfo.getIdno());		
		parm.setData("BIRTH_DATE",patinfo.getBirthDate());
//		parm.setData("BIRTH_DATE",StringTool.getTimestamp(patinfo.getBirthDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("CTZ1_CODE",patinfo.getCtz1Code());
		parm.setData("CTZ2_CODE",patinfo.getCtz2Code());
		
//		private String ctz3Code;
//		private String telCompany;
//		private String telHome;
//		private String cellPhone;
//		private String companyDesc;		
		parm.setData("CTZ3_CODE",patinfo.getCtz3Code());		
		parm.setData("TEL_COMPANY",patinfo.getTelCompany());
		parm.setData("TEL_HOME",patinfo.getTelHome());
		parm.setData("CELL_PHONE",patinfo.getCellPhone());
		parm.setData("COMPANY_DESC",patinfo.getCompanyDesc());
		
//		private String EMail;
//		private String bloodType;
//		private String bloodRhType;	
//		private String marriageCode;
//		private String postCode;
		parm.setData("E_MAIL",patinfo.getEMail());		
		parm.setData("BLOOD_TYPE",patinfo.getBloodType());
		parm.setData("BLOOD_RH_TYPE",patinfo.getBloodRhType());		
		parm.setData("MARRIAGE_CODE",patinfo.getMarriageCode());
		parm.setData("POST_CODE",patinfo.getPostCode());
		
//		private String address;
//		private String residPostCode;
//		private String residAddress;
//		private String contactsName;
//		private String relationCode;
		parm.setData("ADDRESS",patinfo.getAddress());		
		parm.setData("RESID_POST_CODE",patinfo.getResidPostCode());
		parm.setData("RESID_ADDRESS",patinfo.getResidAddress());
		parm.setData("CONTACTS_NAME",patinfo.getContactsName());
		parm.setData("RELATION_CODE",patinfo.getRelationCode());
		
//		private String contactsTel;
//		private String contactsAddress;
//		private String spouseIdno;
//		private String fatherIdno;
//		private String motherIdno;
		parm.setData("CONTACTS_TEL",patinfo.getContactsTel());		
		parm.setData("CONTACTS_ADDRESS",patinfo.getContactsAddress());		
		parm.setData("SPOUSE_IDNO",patinfo.getSpouseIdno());
		parm.setData("FATHER_IDNO",patinfo.getFatherIdno());
		parm.setData("MOTHER_IDNO",patinfo.getMotherIdno());		
		
//		private String religionCode;
//		private String educationCode;
//		private String occCode;
//		private String nationCode;
//		private String speciesCode;
		parm.setData("RELIGION_CODE",patinfo.getReligionCode());
		parm.setData("EDUCATION_CODE",patinfo.getEducationCode());
		parm.setData("OCC_CODE",patinfo.getOccCode());
		parm.setData("NATION_CODE",patinfo.getNationCode());
		parm.setData("SPECIES_CODE",patinfo.getSpeciesCode());		
		
//		private String firstAdmDate;
//		private String rcntOpdDate;
//		private String rcntOpdDept;
//		private String rcntIpdDate;
//		private String rcntIpdDept;
		parm.setData("FIRST_ADM_DATE",patinfo.getFirstAdmDate());
//		parm.setData("FIRST_ADM_DATE",StringTool.getTimestamp(patinfo.getFirstAdmDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("RCNT_OPD_DATE",patinfo.getRcntOpdDate());
//		parm.setData("RCNT_OPD_DATE",StringTool.getTimestamp(patinfo.getRcntOpdDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("RCNT_OPD_DEPT",patinfo.getRcntOpdDept());
		parm.setData("RCNT_IPD_DATE",patinfo.getRcntIpdDate());
//		parm.setData("RCNT_IPD_DATE",StringTool.getTimestamp(patinfo.getRcntIpdDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("RCNT_IPD_DEPT",patinfo.getRcntIpdDept());		
		
//		private String rcntEmgDate;
//		private String rcntEmgDept;
//		private String rcntMissDate;
//		private String rcntMissDept;
//		private String kidExamRcntDate;
		parm.setData("RCNT_EMG_DATE",patinfo.getRcntEmgDate());
//		parm.setData("RCNT_EMG_DATE",StringTool.getTimestamp(patinfo.getRcntEmgDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("RCNT_EMG_DEPT",patinfo.getRcntEmgDept());
		parm.setData("RCNT_MISS_DATE",patinfo.getRcntMissDate());
//		parm.setData("RCNT_MISS_DATE",StringTool.getTimestamp(patinfo.getRcntMissDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("RCNT_MISS_DEPT",patinfo.getRcntMissDept());
		parm.setData("KID_EXAM_RCNT_DATE",patinfo.getKidExamRcntDate());
//		parm.setData("KID_EXAM_RCNT_DATE",StringTool.getTimestamp(patinfo.getKidInjRcntDate(), "yyyy-MM-dd HH:mm:ss"));
		
//		private String kidInjRcntDate;
//		private String adultExamDate;
//		private String smearRcntDate;
//		private String deadDate;
//		private Double height;
		parm.setData("KID_INJ_RCNT_DATE",patinfo.getKidInjRcntDate());
//		parm.setData("KID_INJ_RCNT_DATE",StringTool.getTimestamp(patinfo.getKidInjRcntDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("ADULT_EXAM_DATE",patinfo.getAdultExamDate());
//		parm.setData("ADULT_EXAM_DATE",StringTool.getTimestamp(patinfo.getAdultExamDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("SMEAR_RCNT_DATE",patinfo.getSmearRcntDate());
//		parm.setData("SMEAR_RCNT_DATE",StringTool.getTimestamp(patinfo.getSmearRcntDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("DEAD_DATE",patinfo.getDeadDate());
//		parm.setData("DEAD_DATE",StringTool.getTimestamp(patinfo.getDeadDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("HEIGHT",patinfo.getHeight());
		
//		private Double weight;
//		private String description;
//		private String borninFlg;
//		private Integer newbornSeq;
//		private String prematureFlg;
		parm.setData("WEIGHT",patinfo.getWeight());		
		parm.setData("DESCRIPTION",patinfo.getDescription());
		parm.setData("BORNIN_FLG",patinfo.getBorninFlg());
		parm.setData("NEWBORN_SEQ",patinfo.getNewbornSeq());
		parm.setData("PREMATURE_FLG",patinfo.getPrematureFlg());
		
//		private String handicapFlg;
//		private String blackFlg;
//		private String nameInvisibleFlg;
//		private String lawProtectFlg;
//		private String lmpDate;
		parm.setData("HANDICAP_FLG",patinfo.getHandicapFlg());		
		parm.setData("BLACK_FLG",patinfo.getBlackFlg());
		parm.setData("NAME_INVISIBLE_FLG",patinfo.getNameInvisibleFlg());
		parm.setData("LAW_PROTECT_FLG",patinfo.getLawProtectFlg());
		parm.setData("LMP_DATE",patinfo.getLmpDate());
//		parm.setData("LMP_DATE",StringTool.getTimestamp(patinfo.getLmpDate(), "yyyy-MM-dd HH:mm:ss"));
				
//		private String pregnantDate;
//		private String breastfeedStartdate;
//		private String breastfeedEnddate;
//		private String pat1Code;
//		private String pat2Code;		
		parm.setData("PREGNANT_DATE",patinfo.getPregnantDate());
//		parm.setData("PREGNANT_DATE",StringTool.getTimestamp(patinfo.getPregnantDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("BREASTFEED_STARTDATE",patinfo.getBreastfeedStartdate());
//		parm.setData("BREASTFEED_STARTDATE",StringTool.getTimestamp(patinfo.getBreastfeedStartdate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("BREASTFEED_ENDDATE",patinfo.getBreastfeedEnddate());
//		parm.setData("BREASTFEED_ENDDATE",StringTool.getTimestamp(patinfo.getBreastfeedEnddate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("PAT1_CODE",patinfo.getPat1Code());
		parm.setData("PAT2_CODE",patinfo.getPat2Code());
		
//		private String pat3Code;
//		private String mergeTomrno;	
//		private String homeplaceCode;
//		private String familyHistory;
//		private String handleFlg;
		parm.setData("PAT3_CODE",patinfo.getPat3Code());
		parm.setData("MERGE_TOMRNO",patinfo.getMergeTomrno());//
		parm.setData("HOMEPLACE_CODE",patinfo.getHomeplaceCode());
		parm.setData("FAMILY_HISTORY",patinfo.getFamilyHistory());//
		parm.setData("HANDLE_FLG",patinfo.getHandleFlg());//
				
//		private String nhiNo;
//		private String addressCompany;
//		private String postCompany;
//		private String birthplace;
//		private String ccbPersonNo;
		parm.setData("NHI_NO",patinfo.getNhiNo());
		parm.setData("ADDRESS_COMPANY",patinfo.getAddressCompany());
		parm.setData("POST_COMPANY",patinfo.getPostCompany());
		parm.setData("BIRTHPLACE",patinfo.getBirthplace());
		parm.setData("CCB_PERSON_NO",patinfo.getCcbPersonNo());//
		
//		private String nhiCardNo;
//		private String optUser;
//		private String optTerm;
//		private String optDate;
		parm.setData("NHICARD_NO",patinfo.getNhiCardNo());//
		parm.setData("OPT_USER",patinfo.getOptUser());
		parm.setData("OPT_TERM",patinfo.getOptTerm());
//		optDate
		
		if(result.getCount("MR_NO")>0) {
			result = SpcPatInfoTool.getInstance().updatePat(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				
				////////////////////////////
				String resultDesc = "UPDATE PAT FAILED" + mrNo;
				logParm.setData("RESULT_DESC",resultDesc);
				SpcPatInfoTool.getInstance().saveLog(logParm);
				////////////////////////////
				
				return "UPDATE PAT FAILED" + result.getErrText();
			}
		}else {
			result = SpcPatInfoTool.getInstance().newPat(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				
				////////////////////////////
				String resultDesc = "NEW PAT FAILED" + mrNo;
				logParm.setData("RESULT_DESC",resultDesc);
				SpcPatInfoTool.getInstance().saveLog(logParm);
				////////////////////////////
				
				return "NEW PAT FAILED" + result.getErrText();
			}
		}
		
		return "OK";
	}
	
	/**校验类所有日期类型格式,非空校验*/
	private String checkPatInf(SysPatinfo patinfo){
		if(!checkDateStr(patinfo.getBirthDate())) return "BIRTH_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getFirstAdmDate())) return "FIRST_ADM_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getRcntOpdDate())) return "RCNT_OPD_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getRcntIpdDate())) return "RCNT_IPD_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getRcntEmgDate())) return "RCNT_EMG_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getRcntMissDate())) return "RCNT_MISS_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getKidExamRcntDate())) return "KID_EXAM_RCNT_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getKidInjRcntDate())) return "KID_INJ_RCNT_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getAdultExamDate())) return "ADULT_EXAM_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getSmearRcntDate())) return "SEMAR_RCNT_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getDeadDate())) return "DEAD_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getLmpDate())) return "LMP_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getPregnantDate())) return "PREGNANT_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getBreastfeedEnddate())) return "VREAST_FEED_END_DATE CHECK FAILED";
		if(!checkDateStr(patinfo.getBreastfeedStartdate())) return "VREAST_FEED_END_DATE CHECK FAILED";
		
		if(!checkNotNull(patinfo.getSexCode())) return "SEX_CODE IS NULL";
		if(!checkNotNull(patinfo.getOptUser())) return "OPT_USER IS NULL";
		if(!checkNotNull(patinfo.getOptTerm())) return "OPTTERM IS NULL";		
		
		return "OK";
	}
	
	/**非空校验*/
	private boolean checkNotNull(String str){
		if(null== str || "".equals(str.trim())){
			return false;
		}
		return true;
	}
	
	/**校验日期格式 yyyy-MM-dd HH:mm:ss */
	private boolean checkDateStr(String str){
		try{
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null != str && !"".equals(str.trim())){
//				str = str.substring(0, 19);
				try{
					formater.parse(str);
				}catch(Exception e){
					return false;
				}
			}
		}catch(Exception e){
			return false;
		}
		
		
		return true;
	}
		
}

