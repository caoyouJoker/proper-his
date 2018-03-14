package jdo.spc.reqinf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

public class SpcPatInfoTool extends TJDODBTool{

	 /** 实例*/
    public static SpcPatInfoTool instanceObject;

    /**得到实例*/
    public static SpcPatInfoTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SpcPatInfoTool();
        return instanceObject;
    }
    
	/**更新方法*/
	public TParm updatePat(TParm parm) {
				
		String sql = "UPDATE SYS_PATINFO SET " +
		
						"SEX_CODE='"+parm.getData("SEX_CODE")+"' ," +
						"IPD_NO='"+parm.getData("IPD_NO")+"' ," +
						"DELETE_FLG='"+parm.getData("DELETE_FLG")+"' ," +
						"MERGE_FLG='"+parm.getData("MERGE_FLG")+"' ," +

						"MOTHER_MRNO='"+parm.getData("MOTHER_MRNO")+"' ," +
						"PAT_NAME='"+parm.getData("PAT_NAME")+"' ," +
						"PAT_NAME1='"+parm.getData("PAT_NAME1")+"' ," +
						"PY1='"+parm.getData("PY1")+"' ," +
						"PY2='"+parm.getData("PY2")+"' ," +
						
						"FOREIGNER_FLG='"+parm.getData("FOREIGNER_FLG")+"' ," +
						"IDNO='"+parm.getData("IDNO")+"' ," +
						"BIRTH_DATE = TO_DATE('"+parm.getData("BIRTH_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"CTZ1_CODE='"+parm.getData("CTZ1_CODE")+"' ," +
						"CTZ2_CODE='"+parm.getData("CTZ2_CODE")+"' ," +

						"CTZ3_CODE='"+parm.getData("CTZ3_CODE")+"' ," +
						"TEL_COMPANY='"+parm.getData("TEL_COMPANY")+"' ," +			
						"TEL_HOME='"+parm.getData("TEL_HOME")+"' ," +
						"CELL_PHONE='"+parm.getData("CELL_PHONE")+"' ," +
						"COMPANY_DESC='"+parm.getData("COMPANY_DESC")+"' ," +

						"E_MAIL='"+parm.getData("E_MAIL")+"' ," +
						"BLOOD_TYPE='"+parm.getData("BLOOD_TYPE")+"' ," +
						"BLOOD_RH_TYPE='"+parm.getData("BLOOD_RH_TYPE")+"' ," +
						"MARRIAGE_CODE='"+parm.getData("MARRIAGE_CODE")+"' ," +
						"POST_CODE='"+parm.getData("POST_CODE")+"' ," +

						"ADDRESS='"+parm.getData("ADDRESS")+"' ," +
						"RESID_POST_CODE='"+parm.getData("RESID_POST_CODE")+"' ," +
						"RESID_ADDRESS='"+parm.getData("RESID_ADDRESS")+"' ," +
						"CONTACTS_NAME='"+parm.getData("CONTACTS_NAME")+"' ," +
						"RELATION_CODE='"+parm.getData("RELATION_CODE")+"' ," +

						"CONTACTS_TEL='"+parm.getData("CONTACTS_TEL")+"' ," +
						"CONTACTS_ADDRESS='"+parm.getData("CONTACTS_ADDRESS")+"' ," +
						"SPOUSE_IDNO='"+parm.getData("SPOUSE_IDNO")+"' ," +
						"FATHER_IDNO='"+parm.getData("FATHER_IDNO")+"' ," +
						"MOTHER_IDNO='"+parm.getData("MOTHER_IDNO")+"' ," +

						"RELIGION_CODE='"+parm.getData("RELIGION_CODE")+"' ," +
						"EDUCATION_CODE='"+parm.getData("EDUCATION_CODE")+"' ," +
						"OCC_CODE='"+parm.getData("OCC_CODE")+"' ," +
						"NATION_CODE='"+parm.getData("NATION_CODE")+"' ," +
						"SPECIES_CODE='"+parm.getData("SPECIES_CODE")+"' ," +
	
						"FIRST_ADM_DATE = TO_DATE('"+parm.getData("FIRST_ADM_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"RCNT_OPD_DATE = TO_DATE('"+parm.getData("RCNT_OPD_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"RCNT_OPD_DEPT='"+parm.getData("RCNT_OPD_DEPT")+"' ," +
						"RCNT_IPD_DATE = TO_DATE('"+parm.getData("RCNT_IPD_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"RCNT_IPD_DEPT='"+parm.getData("RCNT_IPD_DEPT")+"' ," +
	
						"RCNT_EMG_DATE = TO_DATE('"+parm.getData("RCNT_EMG_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"RCNT_EMG_DEPT='"+parm.getData("RCNT_EMG_DEPT")+"' ," +
						"RCNT_MISS_DATE = TO_DATE('"+parm.getData("RCNT_MISS_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"RCNT_MISS_DEPT='"+parm.getData("RCNT_MISS_DEPT")+"' ," +
						"KID_EXAM_RCNT_DATE = TO_DATE('"+parm.getData("KID_EXAM_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						
						"KID_INJ_RCNT_DATE = TO_DATE('"+parm.getData("KID_INJ_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"ADULT_EXAM_DATE = TO_DATE('"+parm.getData("ADULT_EXAM_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"SMEAR_RCNT_DATE = TO_DATE('"+parm.getData("SMEAR_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"DEAD_DATE = TO_DATE('"+parm.getData("DEAD_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"HEIGHT="+parm.getData("HEIGHT")+" ," +//double
			
						"WEIGHT="+parm.getData("WEIGHT")+" ," +//double
						"DESCRIPTION='"+parm.getData("DESCRIPTION")+"' ," +
						"BORNIN_FLG='"+parm.getData("BORNIN_FLG")+"' ," +
						"NEWBORN_SEQ="+parm.getData("NEWBORN_SEQ")+" ," +//integer
						"PREMATURE_FLG='"+parm.getData("PREMATURE_FLG")+"' ," +
			
						"HANDICAP_FLG='"+parm.getData("HANDICAP_FLG")+"' ," +
						"BLACK_FLG='"+parm.getData("BLACK_FLG")+"' ," +
						"NAME_INVISIBLE_FLG='"+parm.getData("NAME_INVISIBLE_FLG")+"' ," +
						"LAW_PROTECT_FLG='"+parm.getData("LAW_PROTECT_FLG")+"' ," +
						"LMP_DATE = TO_DATE('"+parm.getData("LMP_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
				
						"PREGNANT_DATE = TO_DATE('"+parm.getData("PREGNANT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"BREASTFEED_STARTDATE = TO_DATE('"+parm.getData("BREASTFEED_STARTDATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"BREASTFEED_ENDDATE = TO_DATE('"+parm.getData("BREASTFEED_ENDDATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
						"PAT1_CODE='"+parm.getData("PAT1_CODE")+"' ," +
						"PAT2_CODE='"+parm.getData("PAT2_CODE")+"' ," +
				
						"PAT3_CODE='"+parm.getData("PAT3_CODE")+"' ," +
						"MERGE_TOMRNO='"+parm.getData("MERGE_TOMRNO")+"' ," +
						"HOMEPLACE_CODE='"+parm.getData("HOMEPLACE_CODE")+"' ," +
						"FAMILY_HISTORY='"+parm.getData("FAMILY_HISTORY")+"' ," +
						"HANDLE_FLG='"+parm.getData("HANDLE_FLG")+"' ," +
						
						"NHI_NO='"+parm.getData("NHI_NO")+"' ," +						
						"ADDRESS_COMPANY='"+parm.getData("ADDRESS_COMPANY")+"' ," +
						"POST_COMPANY='"+parm.getData("POST_COMPANY")+"' ," +
						"BIRTHPLACE='"+parm.getData("BIRTHPLACE")+"' ," +
						"CCB_PERSON_NO='"+parm.getData("CCB_PERSON_NO")+"' ," +
												
						"NHICARD_NO='"+parm.getData("NHICARD_NO")+"' ," +
						"OPT_USER='"+parm.getData("OPT_USER")+"' ," +
						"OPT_TERM='"+parm.getData("OPT_TERM")+"' ," +						
						"OPT_DATE=SYSDATE " +
												
					"WHERE MR_NO='"+parm.getData("MR_NO")+"'";

		TParm result = new TParm(this.update(sql));
		
		return result;
	}
	
	/**保存方法*/
	public TParm newPat(TParm parm) {
			
		String sql = "INSERT INTO SYS_PATINFO " +
							"(" +
//							MR_NO                 VARCHAR2(20 BYTE)       NOT NULL,
//							SEX_CODE              VARCHAR2(20 BYTE)       NOT NULL,
//							  IPD_NO                VARCHAR2(20 BYTE),
//							  DELETE_FLG            VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  MERGE_FLG             VARCHAR2(1 BYTE)        DEFAULT 'N',
							 "MR_NO, SEX_CODE, IPD_NO, DELETE_FLG ,MERGE_FLG, " +
							 
								//		  MOTHER_MRNO           VARCHAR2(20 BYTE),
//							  PAT_NAME              VARCHAR2(50 BYTE),
//							  PAT_NAME1             VARCHAR2(50 BYTE),
//							  PY1                   VARCHAR2(50 BYTE),
//							  PY2                   VARCHAR2(20 BYTE),
							 "MOTHER_MRNO, PAT_NAME, PAT_NAME1, PY1, PY2, " +
							 
								//		  FOREIGNER_FLG         VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  IDNO                  VARCHAR2(20 BYTE),
//							  BIRTH_DATE            DATE,
//							  CTZ1_CODE             VARCHAR2(20 BYTE),
//							  CTZ2_CODE             VARCHAR2(20 BYTE),
							 "FOREIGNER_FLG , IDNO, BIRTH_DATE, CTZ1_CODE, CTZ2_CODE, " +
							 
								//		  CTZ3_CODE             VARCHAR2(20 BYTE),
//							  TEL_COMPANY           VARCHAR2(20 BYTE),
//							  TEL_HOME              VARCHAR2(20 BYTE),
//							  CELL_PHONE            VARCHAR2(20 BYTE),
//							  COMPANY_DESC          VARCHAR2(50 BYTE),
							 "CTZ3_CODE, TEL_COMPANY, TEL_HOME, CELL_PHONE, COMPANY_DESC, " +

								//		  E_MAIL                VARCHAR2(100 BYTE),
//							  BLOOD_TYPE            VARCHAR2(20 BYTE),
//							  BLOOD_RH_TYPE         VARCHAR2(1 BYTE)        DEFAULT '+',
//							  MARRIAGE_CODE         VARCHAR2(20 BYTE),
//							  POST_CODE             VARCHAR2(20 BYTE),
							 "E_MAIL, BLOOD_TYPE, BLOOD_RH_TYPE, MARRIAGE_CODE, POST_CODE, " +

								//		  ADDRESS               VARCHAR2(100 BYTE),
//							  RESID_POST_CODE       VARCHAR2(20 BYTE),
//							  RESID_ADDRESS         VARCHAR2(100 BYTE),
//							  CONTACTS_NAME         VARCHAR2(50 BYTE),
//							  RELATION_CODE         VARCHAR2(20 BYTE),
							 "ADDRESS, RESID_POST_CODE, RESID_ADDRESS, CONTACTS_NAME, RELATION_CODE, " +

								//		  CONTACTS_TEL          VARCHAR2(20 BYTE),
//							  CONTACTS_ADDRESS      VARCHAR2(100 BYTE),
//							  SPOUSE_IDNO           VARCHAR2(20 BYTE),
//							  FATHER_IDNO           VARCHAR2(20 BYTE),
//							  MOTHER_IDNO           VARCHAR2(20 BYTE),
							 "CONTACTS_TEL, CONTACTS_ADDRESS, SPOUSE_IDNO, FATHER_IDNO, MOTHER_IDNO, " +
							 
								//		  RELIGION_CODE         VARCHAR2(20 BYTE),
//							  EDUCATION_CODE        VARCHAR2(20 BYTE),
//							  OCC_CODE              VARCHAR2(20 BYTE),
//							  NATION_CODE           VARCHAR2(20 BYTE),
//							  SPECIES_CODE          VARCHAR2(20 BYTE),
							 "RELIGION_CODE, EDUCATION_CODE, OCC_CODE, NATION_CODE, SPECIES_CODE, " +
							 
								//		  FIRST_ADM_DATE        DATE,
//							  RCNT_OPD_DATE         DATE,
//							  RCNT_OPD_DEPT         VARCHAR2(20 BYTE),
//							  RCNT_IPD_DATE         DATE,
//							  RCNT_IPD_DEPT         VARCHAR2(20 BYTE),
							 "FIRST_ADM_DATE, RCNT_OPD_DATE, RCNT_OPD_DEPT, RCNT_IPD_DATE, RCNT_IPD_DEPT, " +
							 
								//		  RCNT_EMG_DATE         DATE,
//							  RCNT_EMG_DEPT         VARCHAR2(20 BYTE),
//							  RCNT_MISS_DATE        DATE,
//							  RCNT_MISS_DEPT        VARCHAR2(20 BYTE),
//							  KID_EXAM_RCNT_DATE    DATE,
							 "RCNT_EMG_DATE, RCNT_EMG_DEPT, RCNT_MISS_DATE, RCNT_MISS_DEPT, KID_EXAM_RCNT_DATE, " +
							 
								//		  KID_INJ_RCNT_DATE     DATE,
//							  ADULT_EXAM_DATE       DATE,
//							  SMEAR_RCNT_DATE       DATE,
//							  DEAD_DATE             DATE,
//							  HEIGHT                NUMBER(6,3),
							 "KID_INJ_RCNT_DATE, ADULT_EXAM_DATE, SMEAR_RCNT_DATE, DEAD_DATE, HEIGHT, " +
							
								//		  WEIGHT                NUMBER(6,3),
//							  DESCRIPTION           VARCHAR2(200 BYTE),
//							  BORNIN_FLG            VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  NEWBORN_SEQ           NUMBER(3),
//							  PREMATURE_FLG         VARCHAR2(1 BYTE)        DEFAULT 'N',
							 "WEIGHT, DESCRIPTION, BORNIN_FLG, NEWBORN_SEQ, PREMATURE_FLG, " +
							 
								//		  HANDICAP_FLG          VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  BLACK_FLG             VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  NAME_INVISIBLE_FLG    VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  LAW_PROTECT_FLG       VARCHAR2(1 BYTE)        DEFAULT 'N',
//							  LMP_DATE              DATE,
							 "HANDICAP_FLG, BLACK_FLG, NAME_INVISIBLE_FLG, LAW_PROTECT_FLG, LMP_DATE, " +
							 
								//		  PREGNANT_DATE         DATE,
//							  BREASTFEED_STARTDATE  DATE,
//							  BREASTFEED_ENDDATE    DATE,
//							  PAT1_CODE             VARCHAR2(20 BYTE),
//							  PAT2_CODE             VARCHAR2(20 BYTE),
							 "PREGNANT_DATE, BREASTFEED_STARTDATE, BREASTFEED_ENDDATE, PAT1_CODE, PAT2_CODE, " +
							 
								//		  PAT3_CODE             VARCHAR2(20 BYTE),
//							  MERGE_TOMRNO          VARCHAR2(20 BYTE),
//							  HOMEPLACE_CODE        VARCHAR2(20 BYTE),
//							  FAMILY_HISTORY        VARCHAR2(4000 CHAR),
//							  HANDLE_FLG            VARCHAR2(1 BYTE),
							 "PAT3_CODE, MERGE_TOMRNO, HOMEPLACE_CODE, FAMILY_HISTORY, HANDLE_FLG, " +
							 
								//		  NHI_NO                VARCHAR2(20 BYTE),
//							  ADDRESS_COMPANY       VARCHAR2(200 BYTE),
//							  POST_COMPANY          VARCHAR2(200 BYTE),
//							  BIRTHPLACE            VARCHAR2(200 BYTE),
//							  CCB_PERSON_NO         VARCHAR2(20 BYTE),
							 "NHI_NO, ADDRESS_COMPANY, POST_COMPANY, BIRTHPLACE, CCB_PERSON_NO, " +
							 
								//		  NHICARD_NO            VARCHAR2(20 BYTE)
//							  OPT_USER              VARCHAR2(20 BYTE)       NOT NULL,		  
//							  OPT_TERM              VARCHAR2(20 BYTE)       NOT NULL,
//							  OPT_DATE              DATE                    NOT NULL,
							 "NHICARD_NO, OPT_USER, OPT_TERM, OPT_DATE " +
							 
							") " +
					"VALUES(" +
//					"MR_NO, SEX_CODE, IPD_NO, DELETE_FLG ,MERGE_FLG, " +
								"'"+ parm.getData("MR_NO") +"'," +
								"'"+ parm.getData("SEX_CODE") +"'," +
								"'"+ parm.getData("IPD_NO") +"'," +
								"'"+ parm.getData("DELETE_FLG") +"'," +
								"'"+ parm.getData("MERGE_FLG") +"'," +

//								 "MOTHER_MRNO, PAT_NAME, PAT_NAME1, PY1, PY2, " +
								"'"+ parm.getData("MOTHER_MRNO") +"'," +
								"'"+ parm.getData("PAT_NAME") +"'," +
								"'"+ parm.getData("PAT_NAME1") +"'," +
								"'"+ parm.getData("PY1") +"'," +
								"'"+ parm.getData("PY2") +"'," +

//								"FOREIGNER_FLG , IDNO, BIRTH_DATE, CTZ1_CODE, CTZ2_CODE, " +
								  "'"+ parm.getData("FOREIGNER_FLG") +"'," +
								  "'"+ parm.getData("IDNO") +"'," +
								  "TO_DATE('"+parm.getData("BIRTH_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("CTZ1_CODE") +"'," +
								  "'"+ parm.getData("CTZ2_CODE") +"'," +

//								  "CTZ3_CODE, TEL_COMPANY, TEL_HOME, CELL_PHONE, COMPANY_DESC, " +
								  "'"+ parm.getData("CTZ3_CODE") +"'," +
								  "'"+ parm.getData("TEL_COMPANY") +"'," +
								  "'"+ parm.getData("TEL_HOME") +"'," +
								  "'"+ parm.getData("CELL_PHONE") +"'," +
								  "'"+ parm.getData("COMPANY_DESC") +"'," +

//								  "E_MAIL, BLOOD_TYPE, BLOOD_RH_TYPE, MARRIAGE_CODE, POST_CODE, " +
								  "'"+ parm.getData("E_MAIL") +"'," +
								  "'"+ parm.getData("BLOOD_TYPE") +"'," +
								  "'"+ parm.getData("BLOOD_RH_TYPE") +"'," +
								  "'"+ parm.getData("MARRIAGE_CODE") +"'," +
								  "'"+ parm.getData("POST_CODE") +"'," +

//								  "ADDRESS, RESID_POST_CODE, RESID_ADDRESS, CONTACTS_NAME, RELATION_CODE, " +
								  "'"+ parm.getData("ADDRESS") +"'," +
								  "'"+ parm.getData("RESID_POST_CODE") +"'," +
								  "'"+ parm.getData("RESID_ADDRESS") +"'," +
								  "'"+ parm.getData("CONTACTS_NAME") +"'," +
								  "'"+ parm.getData("RELATION_CODE") +"'," +

//								  "CONTACTS_TEL, CONTACTS_ADDRESS, SPOUSE_IDNO, FATHER_IDNO, MOTHER_IDNO, " +
								  "'"+ parm.getData("CONTACTS_TEL") +"'," +
								  "'"+ parm.getData("CONTACTS_ADDRESS") +"'," +
								  "'"+ parm.getData("SPOUSE_IDNO") +"'," +
								  "'"+ parm.getData("FATHER_IDNO") +"'," +
								  "'"+ parm.getData("MOTHER_IDNO") +"'," +

//								  "RELIGION_CODE, EDUCATION_CODE, OCC_CODE, NATION_CODE, SPECIES_CODE, " +
								  "'"+ parm.getData("RELIGION_CODE") +"'," +
								  "'"+ parm.getData("EDUCATION_CODE") +"'," +
								  "'"+ parm.getData("OCC_CODE") +"'," +
								  "'"+ parm.getData("NATION_CODE") +"'," +
								  "'"+ parm.getData("SPECIES_CODE") +"'," +

//								  "FIRST_ADM_DATE, RCNT_OPD_DATE, RCNT_OPD_DEPT, RCNT_IPD_DATE, RCNT_IPD_DEPT, " +
								  "TO_DATE('"+parm.getData("FIRST_ADM_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("RCNT_OPD_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("RCNT_OPD_DEPT") +"'," +
								  "TO_DATE('"+parm.getData("RCNT_IPD_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("RCNT_IPD_DEPT") +"'," +

//								  "RCNT_EMG_DATE, RCNT_EMG_DEPT, RCNT_MISS_DATE, RCNT_MISS_DEPT, KID_EXAM_RCNT_DATE, " +
								  "TO_DATE('"+parm.getData("RCNT_EMG_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("RCNT_EMG_DEPT") +"'," +
								  "TO_DATE('"+parm.getData("RCNT_MISS_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("RCNT_MISS_DEPT") +"'," +
								  "TO_DATE('"+parm.getData("KID_EXAM_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//

								  
//								  "KID_INJ_RCNT_DATE, ADULT_EXAM_DATE, SMEAR_RCNT_DATE, DEAD_DATE, HEIGHT, " +
								  "TO_DATE('"+parm.getData("KID_INJ_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("ADULT_EXAM_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("SMEAR_RCNT_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("DEAD_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  ""+ parm.getData("HEIGHT") +"," +//double

//								  "WEIGHT, DESCRIPTION, BORNIN_FLG, NEWBORN_SEQ, PREMATURE_FLG, " +
								  ""+ parm.getData("WEIGHT") +"," +//double
								  "'"+ parm.getData("DESCRIPTION") +"'," +
								  "'"+ parm.getData("BORNIN_FLG") +"'," +
								  ""+ parm.getData("NEWBORN_SEQ") +"," +//integer
								  "'"+ parm.getData("PREMATURE_FLG") +"'," +

//								  "HANDICAP_FLG, BLACK_FLG, NAME_INVISIBLE_FLG, LAW_PROTECT_FLG, LMP_DATE, " +
								  "'"+ parm.getData("HANDICAP_FLG") +"'," +
								  "'"+ parm.getData("BLACK_FLG") +"'," +
								  "'"+ parm.getData("NAME_INVISIBLE_FLG") +"'," +
								  "'"+ parm.getData("LAW_PROTECT_FLG") +"'," +
								  "TO_DATE('"+parm.getData("LMP_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//

//								  "PREGNANT_DATE, BREASTFEED_STARTDATE, BREASTFEED_ENDDATE, PAT1_CODE, PAT2_CODE, " +
								  "TO_DATE('"+parm.getData("PREGNANT_DATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("BREASTFEED_STARTDATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "TO_DATE('"+parm.getData("BREASTFEED_ENDDATE")+"','yyyy-mm-dd hh24:mi:ss')," +//
								  "'"+ parm.getData("PAT1_CODE") +"'," +
								  "'"+ parm.getData("PAT2_CODE") +"'," +

//								  "PAT3_CODE, MERGE_TOMRNO, HOMEPLACE_CODE, FAMILY_HISTORY, HANDLE_FLG, " +
								  "'"+ parm.getData("PAT3_CODE") +"'," +
								  "'"+ parm.getData("MERGE_TOMRNO") +"'," +
								  "'"+ parm.getData("HOMEPLACE_CODE") +"'," +
								  "'"+ parm.getData("FAMILY_HISTORY") +"'," +
								  "'"+ parm.getData("HANDLE_FLG") +"'," +

//								  "NHI_NO, ADDRESS_COMPANY, POST_COMPANY, BIRTHPLACE, CCB_PERSON_NO, " +
								  "'"+ parm.getData("NHI_NO") +"'," +
								  "'"+ parm.getData("ADDRESS_COMPANY") +"'," +
								  "'"+ parm.getData("POST_COMPANY") +"'," +
								  "'"+ parm.getData("BIRTHPLACE") +"'," +
								  "'"+ parm.getData("CCB_PERSON_NO") +"'," +

//								  "NHICARD_NO, OPT_USER, OPT_TERM, OPT_DATE " +
								"'"+ parm.getData("NHICARD_NO") +"'," +
								"'"+ parm.getData("OPT_USER") +"'," +
								"'"+ parm.getData("OPT_TERM") +"'," +
								"SYSDATE" + ")";

		TParm result = new TParm(this.update(sql));
		
		return result;
	}
	
	/**记录日志*/
	public boolean saveLog(TParm parm) {
		
		String sql = "INSERT INTO IND_BATCH_LOG " +
							"(BATCH_CODE,START_TIME,END_TIME,RESULT_DESC) " +
					"VALUES('"+parm.getData("BATCH_CODE")+"','"+
								parm.getData("START_TIME")+"','"+
								parm.getData("END_TIME")+"','"+
								parm.getData("RESULT_DESC")+"')";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
}
