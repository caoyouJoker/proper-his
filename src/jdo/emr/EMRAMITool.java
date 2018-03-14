package jdo.emr;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.SysFeeUtil;

import jdo.sys.Operator;




public class EMRAMITool extends TJDOTool {
	
	private static EMRAMITool instanceObject;
	public EMRAMITool(){
		
	}
	public static synchronized EMRAMITool getInstance() {
        if (instanceObject == null) {
            instanceObject = new EMRAMITool();
        }
        return instanceObject;
    }
	
	/**
     * 新建保存结构化病历
	 * @param caseNo String
	 * @param mrNo String
	 * @param classCode TODO
     * @return String[]
     * 
     * Evan
     */
    public TParm saveJHWFile(String caseNo,String mrNo,String classCodeConfig,String subclassCode, String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue(classCodeConfig);
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+caseNo;
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    
    /**
     * 取得结构化病历
     * @return
     */
    public String[] getJHWTemplet(String subClassCodeConfig) {
    	
        String subClassCode = TConfig.getSystemValue(subClassCodeConfig);
    	
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
     * 拿到之前保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getSaveFile(String caseNo,String classCodeConfig,String subclassCodeConfig) {   	
    	String classCode = TConfig.getSystemValue(classCodeConfig);
        String subclassCode = TConfig.getSystemValue(subclassCodeConfig);

        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

        String dir="";
        String file="";
        String subClassCode = "";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
        }

        String s[] = {dir,file,subClassCode};
        return s;
    }
    
    /**
     * 新建胸疼急诊记录DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveErdDrJHWDataToDB(TParm dataParm) {
    	
        TParm result = new TParm();
        String sql = "INSERT INTO AMI_ERD_DR_RECROD ("
        		
        	   + "CASE_NO,"
        		
        	   + "FIRST_MEDICAL_ORG,FIRST_DR,FIRST_TIME,"
        	   
        	   + "sence,respire,pulse,heartRate,systolicBloodPressure,diastolicBloodPressure,"
        	   
        	   + "TNI_BLOOD_DRAWING_TIME,REPORT_TIME,cTnl,serum_creatinine,"
        	   
        	   + "REG_DATE,ecgDiagnosisTime,R_ECG_T_Y,R_ECG_T_N,TeleECGTime,"
        		      
        	   + "CCPC_AMI_HEART_UT,CCPC_AMI_NUCLIDE,CCPC_AMI_LVEF,"
        	   
        	   /*// 初步诊断
        	   + "PRI_DIG_DIAGNOSISING,PRI_DIG_STEMI,PRI_DIG_UA,"
        	   + "PRI_DIG_NSTEMI,PRI_DIG_DISSECTING_ANEURYSM,PRI_DIG_APTE,PRI_DIG_NACS,PRI_DIG_NCCP,PRI_DIG_UNKNOW,"
        	   + "PRI_DIG_TIME,PRI_DIG_DR,"
        	   
        	   // 急诊给药
        	   + "EMRADM_ASPIRIN_DOSE,EMRADM_FIRST_ADM_TIME,EMRADM_CLOPIDOGREL,EMRADM_TICAGRELOR,EMRADM_CLTIC_DOSE,"
        	   + "EMRADM_CLTIC_ADM_TIME,EMRADM_STATINS_DESC,EMRADM_STATINS_DOSE,EMRADM_STATINS_ADM_TIME,"
        	   // 院前给药
 		       + "PREADM_ASPIRIN_DOSE,PREADM_FIRST_ADM_TIME,PREADM_CLOPIDOGREL,PREADM_TICAGRELOR,PREADM_CLTIC_DOSE,"
 		       + "PREADM_CLTIC_ADM_TIME,PREADM_STATINS_DESC,PREADM_STATINS_DOSE,PREADM_STATINS_ADM_TIME,"
 		       //首次抗凝给药
 		       + "AC_MED,AC_MED_TIME,AC_MED_DESC,AC_MED_DOSE,AC_MED_DOSE_UNIT,"
 		       
 		       + "THRO_FIT,THRO_NOT_FIT,THROMBOLYTIC,THRO_IS_ARRIVE,THRO_NOT_ARRIVE,THRO_LOC,THRO_INFO_START,THRO_INFO_SIGN,"
 		       + "THRO_START_TIME,THRO_TIME,THRO_MEDICINE_1,THRO_MEDICINE_2,THRO_MEDICINE_3,THRO_DOSE_ALL,THRO_DOSE_HALF,THRO_IS,"
 		       + "THRO_AGN,"
 		       
 		       //24小时强化他汀治疗，β受体阻滞剂使用
 		       + "THRO_STATINS_24H,THRO_BETA_BLOCKERS"
 		       
 		       //TIMI评分# ： 分 
 		       + "TIMI_SCORE,"
 		       
 		       // GRACE
 		       + "GRACE_CARDIAC_ARREST,GRACE_ST_CHANGE,GRACE_MK_RICE,GRACE_SCORE,"
 		       //如果为非ACS
 		       + "NACS_ARRHYTHMIA,NACS_DCM,NACS_ICM,NACS_HCM,NACS_MYOCARDITIS,NACS_CHD,"
 		       + "NACS_VHD,NACS_OMI,NACS_ANGINA,NACS_PALPITATION,NACS_ATRIAL_FIBRILLATION,"
 		       + "NACS_HYPERTENSION,NACS_HEARTFAILURE,NACS_AF,NACS_R_ON_T,NACS_APB,"
 		       + "NACS_SVT,NACS_OTHER,NACS_OTHER_DESC,NACS_TREATMENT,NACS_TIME,NACS_PATIENT_W,NACS_DR,"
 		       
 		       // 如果为非心源性
 		       + "NONCARDIAC_RSD,NONCARDIAC_DSD,NONCARDIAC_NSD,NONCARDIAC_MENTALDISORDERS,NONCARDIAC_MUSCUL,NONCARDIAC_DOSS,"
 		       + "NONCARDIAC_OTHER,NONCARDIAC_OTHER_DESC,NONCARDIAC_TREATMENT,NONCARDIAC_TIME,NONCARDIAC_PATIENT_W,NONCARDIAC_DR,"
 		       
 		       //STEMI急诊PCI
 		       + "STEMI_CALL_TIME,STEMI_FIRST_DIG_TIME,STEMI_EMR_PCI,STEMI_THRO,STEMI_REMEDY,STEMI_GRAPHIC_ONLY"
 		       + ",STEMI_SEL_PCI,STEMI_SEL_GRAPHIC,STEMI_CABG,STEMI_NO_INFUSION ,STEMI_OTHER,STEMI_OTHER_DESC,"
 		       
 		       // NSTEMI急诊PCI	       
 		       + "NSTEMI_CALL_IN_TIME,NSTEMI_FIRST_MED_TIME,NSTEMI_EMD_TIME,NSTEMI_GRAPHIC_ONLY"
 		       + ",NSTEMI_INTER_24H,NSTEMI_INTER_72H,NSTEMI_GRAPHIC_ONLY_E,NSTEMI_INTER_SEL,"
 		       + "NSTEMI_GUARD,NSTEMI_CABG,NSTEMI_OTHER,NSTEMI_OTHER_DESC,"
 		       
 		       // 无再灌注原因
 		       + "NINF_NO_CHEST_PAIN,NINF_VITAL_SIGN_OK"
 		       + ",NINF_TIME_MISSED,NINF_BLEED,NINF_RHI,NINF_ECONOMIC,NINF_QUIT,NINF_OTHER,NINF_OTHER_DESC,"
 		       
 		       //肺栓塞危险分层
 		       + "PULM_HIGN,PULM_MIDDLE,PULM_LOW,PULM_IS,PULM_AC_START,"
 		       
 		       // 主动脉夹层处理
 		       + "AD_TYPE,AD_CONS_IN_TIME,AD_CONS_OUT_NOTICE,AD_CONS_OUT_TIME,"
 		       */
 		       + "OPT_USER,OPT_DATE,OPT_TERM) "
 		       
 		       + "VALUES ("
 		       
 		       + "'" + dataParm.getValue("CASE_NO") + "'"
 		       + "'" + dataParm.getValue("FIRST_MEDICAL_ORG") + "'"
 		       + ",'" + dataParm.getValue("FIRST_DR") + "'"
 		       + ",to_date('" + dataParm.getValue("FIRST_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       
 		       + ",'" + dataParm.getValue("sence") + "'"
 		       + ",'" + dataParm.getValue("respire") + "'"
 		       + ",'" + dataParm.getValue("pulse") + "'"
 		       + ",'" + dataParm.getValue("heartRate") + "'"
 		       + ",'" + dataParm.getValue("systolicBloodPressure") + "'"
 		       + ",'" + dataParm.getValue("diastolicBloodPressure") + "'"
 		
               + ",to_date('" + dataParm.getValue("TNI_BLOOD_DRAWING_TIME") + "','yyyy/MM/dd HH24:MI')"
               + ",to_date('" + dataParm.getValue("REPORT_TIME") + "','yyyy/MM/dd HH24:MI')"
               + ",'" + dataParm.getValue("cTnl") + "'"
               + ",'" + dataParm.getValue("serum_creatinine") + "'"

               + ",to_date('" + dataParm.getValue("REG_DATE") + "','yyyy/MM/dd HH24:MI')"
               + ",to_date('" + dataParm.getValue("ecgDiagnosisTime") + "','yyyy/MM/dd HH24:MI')"
               + ",'" + dataParm.getValue("R_ECG_T_Y") + "'"
               + ",'" + dataParm.getValue("R_ECG_T_N") + "'"
               + ",to_date('" + dataParm.getValue("TeleECGTime") + "','yyyy/MM/dd HH24:MI')"
   
 		       + ",'" + dataParm.getValue("CCPC_AMI_HEART_UT") + "'"
 		       + ",'" + dataParm.getValue("CCPC_AMI_NUCLIDE") + "'"
 		       + ",'" + dataParm.getValue("CCPC_AMI_LVEF") + "'"
 		       
 		       /*// 初步诊断
 		       + ",'" + dataParm.getValue("PRI_DIG_DIAGNOSISING") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_STEMI") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_UA") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_NSTEMI") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_DISSECTING_ANEURYSM") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_APTE") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_NACS") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_NCCP") + "'"
 		       + ",'" + dataParm.getValue("PRI_DIG_UNKNOW") + "'"
 		       + ",to_date('" + dataParm.getValue("PRI_DIG_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("PRI_DIG_DR") + "'"
 		       + ",'" + dataParm.getValue("EMRADM_ASPIRIN_DOSE") + "'"
 		       + ",to_date('" + dataParm.getValue("EMRADM_FIRST_ADM_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("EMRADM_CLOPIDOGREL") + "'"
 		       + ",'" + dataParm.getValue("EMRADM_TICAGRELOR") + "'"
 		       + ",'" + dataParm.getValue("EMRADM_CLTIC_DOSE") + "'"
 		       + ",to_date('" + dataParm.getValue("EMRADM_CLTIC_ADM_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("EMRADM_STATINS") + "'"
 		       + ",'" + dataParm.getValue("EMRADM_STATINS_DOSE") + "'"
 		       + ",to_date('" + dataParm.getValue("EMRADM_STATINS_ADM_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("EMRADM_ADM_DESC")+"'"
 		       + ",'" + dataParm.getValue("PREADM_ASPIRIN_DOSE") + "'"
 		       + ",to_date('" + dataParm.getValue("PREADM_FIRST_ADM_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("PREADM_ADM_DESC")+"'"
 		       + ",'" + dataParm.getValue("THROMBOLYTIC")+"'"
 		       + ",to_date('" + dataParm.getValue("THRO_ADM_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("THRO_LOC")+"'"
 		       + ",'" + dataParm.getValue("THRO_SCREEN")+"'"
 		       + ",to_date('" + dataParm.getValue("THRO_INFO_START") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("THRO_INFO_SIGN") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("THRO_START_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("THRO_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("THRO_MEDICINE") + "'"
 		       + ",'" + dataParm.getValue("THRO_DOSE") + "'"
 		       + ",'" + dataParm.getValue("THRO_AGN") + "'"
 		       + ",'" + dataParm.getValue("THRO_DESC") + "'"
 		       + ",'" + dataParm.getValue("THRO_STATINS_24H") + "'"
 		       + ",'" + dataParm.getValue("THRO_BETA_BLOCKERS") + "'"
 		       + ",'" + dataParm.getValue("TIMI_SCORE").trim() + "'"
 		       + ",'" + dataParm.getValue("GRACE_CARDIAC_ARREST") + "'"
 		       + ",'" + dataParm.getValue("GRACE_ST_CHANGE") + "'"
 		       + ",'" + dataParm.getValue("GRACE_MK_RICE") + "'"
 		       + ",'" + dataParm.getValue("GRACE_SCORE") + "'"
 		       + ",'" + dataParm.getValue("NACS_DIAGNOSTIC") + "'"
 		       + ",to_date('" + dataParm.getValue("NACS_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("NACS_VEST") + "'"
 		       + ",'" + dataParm.getValue("NACS_DR") + "'"
 		       + ",'" + dataParm.getValue("NACS_DESC") + "'"
 		       + ",to_date('" + dataParm.getValue("STEMI_CALL_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("STEMI_FIRST_DIG_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("STEMI_EMR_PCI") + "'"
 		       + ",'" + dataParm.getValue("STEMI_THRO") + "'"
 		       + ",'" + dataParm.getValue("STEMI_REMEDY") + "'"
 		       + ",'" + dataParm.getValue("STEMI_GRAPHIC_ONLY") + "'"
 		       + ",'" + dataParm.getValue("STEMI_SEL_PCI") + "'"
 		       + ",'" + dataParm.getValue("STEMI_SEL_GRAPHIC") + "'"
 		       + ",'" + dataParm.getValue("STEMI_CABG") + "'"
 		       + ",'" + dataParm.getValue("STEMI_NO_INFUSION ") + "'"
 		       + ",'" + dataParm.getValue("STEMI_OTHER") + "'"
 		       + ",to_date('" + dataParm.getValue("NSTEMI_CALL_IN_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("NSTEMI_FIRST_MED_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("NSTEMI_EMD_TIME") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_GRAPHIC_ONLY") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_INTER_24H") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_INTER_72H") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_GRAPHIC_ONLY_E") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_INTER_SEL") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_GUARD") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_CABG") + "'"
 		       + ",'" + dataParm.getValue("NSTEMI_OTHER") + "'"
 		       + ",'" + dataParm.getValue("NINF_NO_CHEST_PAIN") + "'"
 		       + ",'" + dataParm.getValue("NINF_VITAL_SIGN_OK") + "'"
 		       + ",'" + dataParm.getValue("NINF_TIME_MISSED") + "'"
 		       + ",'" + dataParm.getValue("NINF_BLEED") + "'"
 		       + ",'" + dataParm.getValue("NINF_RHI") + "'"
 		       + ",'" + dataParm.getValue("NINF_ECONOMIC") + "'"
 		       + ",'" + dataParm.getValue("NINF_QUIT") + "'"
 		       + ",'" + dataParm.getValue("NINF_OTHER") + "'"
 		       + ",'" + dataParm.getValue("PULM_MK_RICE") + "'"
 		       + ",to_date('" + dataParm.getValue("PULM_AC_START") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",'" + dataParm.getValue("AD_TYPE") + "'"
 		       + ",to_date('" + dataParm.getValue("AD_CONS_IN_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("AD_CONS_OUT_NOTICE") + "','yyyy/MM/dd HH24:MI:SS')"
 		       + ",to_date('" + dataParm.getValue("AD_CONS_OUT_TIME") + "','yyyy/MM/dd HH24:MI:SS')"*/
 		       
 		       
 		       + ",'" + dataParm.getValue("OPT_USER") + "'"
 		       + ",sysdate"
 		       + ",'" + dataParm.getValue("OPT_TERM") + "'"
 		       + ")";
 
        result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;

    }
    
    /**
     * 新建CT记录DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveAmiCTJHWDataToDB(TParm dataParm) {
    	TParm result = new TParm();
    	String sql = "INSERT INTO AMI_CT_RECORD (CASE_NO,CT_NOTICE_TIME,CT_RESP_TIME,CT_ARRIVE_TIME,PAT_ARRIVE_TIME,CT_START_TIME,CT_REPORT_TIME,OPT_USER,OPT_DATE,OPT_TERM) "
    			        + "VALUES ("
    	    			+ "'" + dataParm.getValue("CASE_NO") + "'"
    	    			+ ",to_date('" + dataParm.getValue("CT_NOTICE_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",to_date('" + dataParm.getValue("CT_RESP_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",to_date('" + dataParm.getValue("CT_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",to_date('" + dataParm.getValue("PAT_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",to_date('" + dataParm.getValue("CT_START_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",to_date('" + dataParm.getValue("CT_REPORT_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    	    			+ ",'" + dataParm.getValue("OPT_USER") + "'"
    	    			+ ",sysdate"
    	    			+ ",'" + dataParm.getValue("OPT_TERM") + "'"
    	    			+ ")";
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 新建UT记录DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveAmiUTJHWDataToDB(TParm dataParm) {
    	TParm result = new TParm();
    	String sql = "INSERT INTO AMI_UT_RECORD (CASE_NO,UT_NOTICE_TIME,UT_START_TIME,UT_REPORT_TIME,OPT_USER,OPT_DATE,OPT_TERM) "
    			        + "VALUES ("
    			        + "'" + dataParm.getValue("CASE_NO") + "'" 
    			        + ",to_date('" + dataParm.getValue("UT_NOTICE_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    			        + ",to_date('" + dataParm.getValue("UT_START_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    			        + ",to_date('" + dataParm.getValue("UT_REPORT_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
    			        + ",'" + dataParm.getValue("OPT_USER") + "'"
    			        + ",sysdate"
    			        + ",'" + dataParm.getValue("OPT_TERM") + "'"
    			        + ")";
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    
    /**
     * 新建急诊医生站-TIMI评分DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveTIMIJHWDataToDB(TParm dataParm) {
    	
        TParm result = new TParm();
        String sql = "INSERT INTO AMI_ERD_DR_TIMI (CASE_NO,TIMI_AGE,TIMI_HAS_HISTORY,TIMI_SYSTOLIC_BELOW_100,TIMI_CARDIOTACH_OVER_100,TIMI_KILLIP_OVER_I,TIMI_WEIGHT_BELOW_67,TIMI_AMI_LBBB,TIMI_INFUSION_OVER_4H,TIMI_TOTAL,TIML_LEVEL,OPT_USER,OPT_DATE,OPT_TERM)"
        		+ " VALUES ("
        		+ "'" + dataParm.getValue("CASE_NO") 
        		+ "'," + dataParm.getValue("TIMI_AGE")
        		+ "," + dataParm.getValue("TIMI_HAS_HISTORY")
        		+ "," + dataParm.getValue("TIMI_SYSTOLIC_BELOW_100") 
        		+ "," + dataParm.getValue("TIMI_CARDIOTACH_OVER_100") 
        		+ "," + dataParm.getValue("TIMI_KILLIP_OVER_I")
        		+ "," + dataParm.getValue("TIMI_WEIGHT_BELOW_67") 
        		+ "," + dataParm.getValue("TIMI_AMI_LBBB") 
        		+ "," + dataParm.getValue("TIMI_INFUSION_OVER_4H") 
        		+ ",'" + dataParm.getValue("TIMI_TOTAL").trim() + "'"
        		+ ",'" + dataParm.getValue("TIML_LEVEL") 
        		+ "','" + dataParm.getValue("OPT_USER") 
        		+ "',sysdate"
        		+ ",'" + dataParm.getValue("OPT_TERM")
        		+"')";
        
        result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;

    }
    
    
    /**
     * 新建急诊医生站-GRACE评分DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveGRACEJHWDataToDB(TParm dataParm) {
    	
        TParm result = new TParm();
        String sql = "INSERT INTO AMI_ERD_DR_GRACE (CASE_NO,VALUE_AGE,GRACE_AGE,VALUE_CARDIOTACH,GRACE_CARDIOTACH,VALUE_BP_SYSTOLIC,GRACE_BP_SYSTOLIC,VALUE_CK,GRACE_CK,VALUE_RISK,GRACE_RISK,VALUE_KILLIP,GRACE_KILLIP,OPT_USER,OPT_DATE,OPT_TERM) "
        		+ "VALUES ("
        		+ "'" + dataParm.getValue("CASE_NO") + "'"
        		+ ",'" + dataParm.getValue("VALUE_AGE") + "'"
        		+ ",'" + dataParm.getValue("GRACE_AGE").trim() + "'"
        		+ ",'" + dataParm.getValue("VALUE_CARDIOTACH") + "'"
        		+ ",'" + dataParm.getValue("GRACE_CARDIOTACH").trim() + "'"
        		+ ",'" + dataParm.getValue("VALUE_BP_SYSTOLIC") + "'"
        		+ ",'" + dataParm.getValue("GRACE_BP_SYSTOLIC").trim() + "'"
        		+ ",'" + dataParm.getValue("VALUE_CK") + "'"
        		+ ",'" + dataParm.getValue("GRACE_CK").trim() + "'"
        		+ ",'" + dataParm.getValue("VALUE_RISK") + "'"
        		+ ",'" + dataParm.getValue("GRACE_RISK").trim() + "'"
        		+ ",'" + dataParm.getValue("VALUE_KILLIP") + "'"
        		+ ",'" + dataParm.getValue("GRACE_KILLIP").trim() + "'"
        		+ ",'" + dataParm.getValue("OPT_USER") + "'"
        		+ ",sysdate"
        		+ ",'" + dataParm.getValue("OPT_TERM") + "'"
        		+ ")";
        
        result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;

    }
    
    /**
     * 新建肺动脉栓塞ATPE死亡危险分层DB资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm saveAPTEJHWDataToDB(TParm dataParm) {
    	
        TParm result = new TParm();
        String sql = "INSERT INTO AMI_APTE_RECORD (CASE_NO,ATPE_DR,RISK_OF_MORTALITY,SHOCK_OR_HYPOTENSION ,RIGHT_VEN_INSUFFICIENCY,MYOCARDIAL_INJURY,OPT_USER,OPT_DATE,OPT_TERM) "
        		+ "VALUES ("
        		+ "'" + dataParm.getValue("CASE_NO") + "'"
        		+ ",'" + dataParm.getValue("ATPE_DR") + "'"
        		+ ",'" + dataParm.getValue("RISK_OF_MORTALITY") + "'"
        		+ ",'" + dataParm.getValue("SHOCK_OR_HYPOTENSION") + "'"
        		+ ",'" + dataParm.getValue("RIGHT_VEN_INSUFFICIENCY") + "'"
        		+ ",'" + dataParm.getValue("MYOCARDIAL_INJURY") + "'"
        		+ ",'" + dataParm.getValue("OPT_USER") + "'"
        		+ ",sysdate"
        		+ ",'" + dataParm.getValue("OPT_TERM") + "'"
        		+ ")";        
        
        result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;

    }
    
    
    /**
     * 删除胸疼急诊记录DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteERDJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_ERD_DR_RECROD WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    /**
     * 删除CT DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteAmiCTJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_CT_RECORD WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    /**
     * 删除UT DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteAmiUTJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_UT_RECORD WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    
    
    /**
     * 删除急诊医生站-TIMI评分DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteTIMIJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_ERD_DR_TIMI WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    /**
     * 删除急诊医生站-GRACE评分DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteGRACEJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_ERD_DR_GRACE WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    /**
     * 删除肺动脉栓塞ATPE死亡危险分层DB资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm deleteAPTEJHWData(String caseNo) {
    	TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM AMI_APTE_RECORD WHERE CASE_NO='" + caseNo + "'");
        result = new TParm(this.getDBTool().update(sqlbf.toString()));
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        
        return result;
    }
    
    /**
     * 拿到急诊检伤资料
     * @param caseNo String
     * @return TParm
     */
    public TParm getErdEvalutionDataByCaseNo(String caseNo) {
    	String sql = "SELECT PAT_NAME,IDNO,SEX_CODE,TRIAGE_NO,CASE_NO,LEVEL_CODE," +
      			       "ADM_TYPE,ADM_DATE,DEPT_CODE,CLINICAREA_CODE," +
      			       "TRIAGE_USER,TO_CHAR (ADM_DATE, 'yyyy/MM/dd')||' '||TO_CHAR (COME_TIME, 'HH24:MI:SS') GATE_TIME FROM ERD_EVALUTION "
      			       + "WHERE CASE_NO='"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿到胸痛急诊护士记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getAmiErdNsReccordDataByTriageNo(String triageNo) {
    	String sql = "SELECT TRIAGE_NO,CASE_NO,FIRST_IN_ECG_TIME,CONSCIOUS,RESPIRATORY_RATE,PULSE,CARDIAC_RATE,DIASTOLIC_BLOOD_PRESSURE,SYSTOLIC_BLOOD_PRESSURE"
    			     + ",KILLIP,TNI_BLOOD_DRAWING_TIME,REPORT_TIME,CTNL,BLOOD_CREATININE,OPT_USER,OPT_DATE,OPT_TERM FROM AMI_ERD_NS_RECORD "
    			     + "WHERE TRIAGE_NO='"+triageNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 拿到CT室胸痛中心记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getAmiCTReccordDataByCaseNo(String caseNo) {
    	String sql = "SELECT CASE_NO,TO_CHAR (CT_NOTICE_TIME, 'yyyy/MM/dd HH24:MI:SS') AS CT_NOTICE_TIME"
    			          + ",TO_CHAR (CT_RESP_TIME, 'yyyy/MM/dd HH24:MI:SS') AS CT_RESP_TIME"
    			          + ",TO_CHAR (CT_ARRIVE_TIME, 'yyyy/MM/dd HH24:MI:SS') AS CT_ARRIVE_TIME"
    			          + ",TO_CHAR (PAT_ARRIVE_TIME, 'yyyy/MM/dd HH24:MI:SS') AS PAT_ARRIVE_TIME"
    			          + ",TO_CHAR (CT_START_TIME, 'yyyy/MM/dd HH24:MI:SS') AS CT_START_TIME"
    			          + ",TO_CHAR (CT_REPORT_TIME, 'yyyy/MM/dd HH24:MI:SS') AS CT_REPORT_TIME"
    			          + ",OPT_USER,OPT_DATE,OPT_TERM FROM AMI_CT_RECORD"
    			       + " WHERE CASE_NO='"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 拿到彩超室胸痛中心记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getAmiUTReccordDataByCaseNo(String caseNo) {
    	String sql = "SELECT CASE_NO,TO_CHAR (UT_NOTICE_TIME, 'yyyy/MM/dd HH24:MI:SS') AS UT_NOTICE_TIME"
    			         + ",TO_CHAR (UT_START_TIME, 'yyyy/MM/dd HH24:MI:SS') AS UT_START_TIME"
    			         + ",TO_CHAR (UT_REPORT_TIME, 'yyyy/MM/dd HH24:MI:SS') AS UT_REPORT_TIME"
    			         + ",OPT_USER,OPT_DATE,OPT_TERM FROM AMI_UT_RECORD"
    			       + " WHERE CASE_NO='"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 拿到急诊医生站-GRACE评分记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getAmiErdDrGraceDataByCaseNo(String caseNo) {
    	String sql = "SELECT CASE_NO,VALUE_AGE,GRACE_AGE,VALUE_CARDIOTACH,GRACE_CARDIOTACH,VALUE_BP_SYSTOLIC,GRACE_BP_SYSTOLIC,VALUE_CK,GRACE_CK,VALUE_RISK"
    			+ ",GRACE_RISK,VALUE_KILLIP,GRACE_KILLIP,OPT_USER,OPT_DATE,OPT_TERM "
    			+ "FROM AMI_ERD_DR_GRACE "
                + "WHERE CASE_NO='" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 根据就诊号更新入院路径与路径分类
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm updateEnterRouteAndPathKindToRegPatadm(TParm dataParm) {
    	TParm result = new TParm();
    	String sql = "UPDATE REG_PATADM "
    			        + "SET ENTER_ROUTE = '" + dataParm.getValue("ENTER_ROUTE") + "'"
    			        + ", PATH_KIND = '" + dataParm.getValue("PATH_KIND") + "'"
    			        + " WHERE CASE_NO = '"  + dataParm.getValue("CASE_NO") + "'";
    	    			
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 根据就诊号查询入院路径与路径分类
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm getEnterRouteAndPathKindByCaseNo(String caseNo) {
    	TParm result = new TParm();
    	String sql = "SELECT ENTER_ROUTE, PATH_KIND FROM REG_PATADM "
    			        + " WHERE CASE_NO = '"  + caseNo + "'";
    	    			
    	result = new TParm(TJDODBTool.getInstance().select(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 确认是否有虚拟医嘱
     * @param triageNo String
     * @return TParm
     */
    public TParm getVirtualOrderByTriageNo(String triageNo) {
    	
    	String fEcgOrder = TConfig.getSystemValue("FIRST_ECG_ORDER");//首次心电医嘱代码
    	
    	String sql = "SELECT CASE_NO FROM OPD_ORDER "
				+ " WHERE CASE_NO='" + triageNo + "'"
				+ " AND VIRTUAL_FLG='0' "
				+ " AND ORDER_CODE='"+fEcgOrder+"'";
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新虚拟医嘱状态
	 * @param String
     * @return TParm
     * 
     * Evan
     */
    public TParm updateVirtualFlgByTriageNo(String triageNo ,String virtualFlg) {
    	TParm result = new TParm();
    	String sql = "UPDATE OPD_ORDER SET VIRTUAL_FLG='" + virtualFlg + "'"
	    			+ " WHERE CASE_NO='" + triageNo + "'"
					+ " AND ORDER_CODE='Y040A001'";
    			        
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    
    /**
     * 确认是否有开心脏超声或核素的医嘱
     * @param caseNo String
     * @param orderCode String
     * @return TParm
     */
    public TParm getCheckOrderByCaseNoAndOrderCode(String caseNo,String orderCode) {
    	
    	String sql = "SELECT ORDER_CODE, ORDER_DESC, CASE_NO,ORDER_DATE FROM OPD_ORDER "
    				+ " WHERE CASE_NO='" + caseNo + "'"
    				+ " AND ORDER_CODE LIKE '" + orderCode + "%'";
  				
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 确认是否有开阿司匹林、氯吡格雷、替格瑞洛、他汀类药物、抗凝血药品、溶栓药品的医嘱
     * @param caseNo String
     * @return TParm
     */
    public TParm getSpecialOpdOrderByCaseNo(String caseNo) {
    	String sql = "SELECT T2.SYS_PHA_CLASS "
//    				+ " ,T1.OPT_DATE "
    				+ " ,T1.ORDER_DATE "
    			    + " ,CASE WHEN T1.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(T1.MEDI_QTY,'fm9999999990.000'),'0'),'.') END AS MEDI_QTY "					 
				    + " ,CASE WHEN T1.ROUTE_CODE='PS' THEN '' ELSE T4.UNIT_CHN_DESC END AS UNIT_CHN_DESC "
				    + " ,T1.ORDER_DESC "
    				+ "FROM OPD_ORDER T1 "
					+ "INNER JOIN "
					+ "	( "
					+ "		SELECT * FROM SYS_FEE_HISTORY "
					+ "		WHERE ACTIVE_FLG='Y' "
					+ "		AND SYS_PHA_CLASS IS NOT NULL "
					+ "		AND SYS_PHA_CLASS <> '0' "
					+ "	) T2 "
					+ "	ON (T1.ORDER_CODE=T2.ORDER_CODE AND T1.REGION_CODE=T2.REGION_CODE) "
					+ "LEFT JOIN SYS_UNIT T4 "
					+ " ON (T1.MEDI_UNIT=T4.UNIT_CODE) "
					+ "	WHERE T1.CASE_NO='" + caseNo + "'";
    	
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 确认是否有开主诊断
     * @param caseNo String
     * @return TParm
     */
    public TParm getOpdDiagrecByCaseNo(String caseNo) {
    	String sql = "SELECT T1.CASE_NO,T1.ORDER_DATE,T2.USER_NAME,T3.ICD_CHN_DESC FROM OPD_DIAGREC T1"
    			+ " LEFT JOIN SYS_OPERATOR T2 ON (T1.DR_CODE=T2.USER_ID)"
    			+ " LEFT JOIN SYS_DIAGNOSIS T3 ON (T1.ICD_TYPE=T3.ICD_TYPE AND T1.ICD_CODE=T3.ICD_CODE)"
    			+ " WHERE T1.CASE_NO='" + caseNo + "'";
    	
  				
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 拿到溶栓知情同意书保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getThrombolysisConsentSaveFile(String caseNo) {   	

        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE,TO_CHAR(OPT_DATE,'yyyy-MM-dd HH24:MI:SS') AS OPD_DATE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='EMR070016' AND SUBCLASS_CODE='EMR07001612' ORDER BY OPD_DATE"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

        String dir="";
        String file="";
        String subClassCode = "";
        String opdDate = "";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
            opdDate = emrParm.getValue("OPD_DATE",0);
        }

        String s[] = {dir,file,subClassCode,opdDate};
        return s;
    }
    
    /**
     * 确认是否有做手术申请
     * @param caseNo String
     * @return TParm
     */
    public TParm getOpeOPBookDataByCaseNo(String caseNo) {
    	String sql = "SELECT OPT_DATE FROM OPE_OPBOOK"   			
    			+ " WHERE CASE_NO='" + caseNo + "'";
    	
  				
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 判断是否有CT资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm getAmiCTDataByCaseNo(String caseNo) {
    	String sql = "SELECT CASE_NO FROM AMI_CT_RECORD"   			
    			+ " WHERE CASE_NO='" + caseNo + "'";   	
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 新建CT资料
	 * @param String
     * @return TParm
     * 
     * Evan
     */
    public TParm saveAmiCTData(String caseNo) {
    	TParm result = new TParm();
    	String sql = "INSERT INTO AMI_CT_RECORD (CASE_NO,CT_NOTICE_TIME,CT_RESP_TIME,CT_ARRIVE_TIME,PAT_ARRIVE_TIME,CT_START_TIME,CT_REPORT_TIME,OPT_USER,OPT_DATE,OPT_TERM) "
    			        + "VALUES ("
    	    			+ "'" + caseNo + "'"
    	    			+ ",sysdate"
    	    			+ ",null"
    	    			+ ",null"
    	    			+ ",null"
    	    			+ ",null"
    	    			+ ",null"
    	    			+ ",'" + Operator.getID() + "'"
    	    			+ ",sysdate"
    	    			+ ",'" + Operator.getIP() + "'"
    	    			+ ")";
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 更新CT资料
	 * @param String
     * @return TParm
     * 
     * Evan
     */
    public TParm updateAmiCTDataByCaseNo(String caseNo) {
    	TParm result = new TParm();
    	String sql = "UPDATE AMI_CT_RECORD SET CT_NOTICE_TIME=sysdate"
	    			+ " WHERE CASE_NO='" + caseNo + "'";
    			        
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    /**
     * 更新CT人员回覆时间
     * @param caseNo
     * @param ctRespTime
     * @return
     */
    public TParm updateAmiCTRespDataByCaseNo(String caseNo,String ctRespTime) {
    	TParm result = new TParm();
    	String sql = "UPDATE AMI_CT_RECORD SET "
	    			+ "CT_RESP_TIME=to_date('" + ctRespTime + "','yyyy/MM/dd HH24:MI:SS'),"
	    			+ "CT_ARRIVE_TIME=to_date('" + ctRespTime + "','yyyy/MM/dd HH24:MI:SS')"
	    			+ " WHERE CASE_NO='" + caseNo + "'";
    			        
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    /**
     * 更新CT患者到达时间
     * @param caseNo
     * @param patArriveTime
     * @return
     */
    public TParm updateAmiCTPatArriveByCaseNo(String caseNo,String patArriveTime) {
    	TParm result = new TParm();
    	String sql = "UPDATE AMI_CT_RECORD SET "
	    			+ "PAT_ARRIVE_TIME=to_date('" + patArriveTime + "','yyyy/MM/dd HH24:MI:SS')"
	    			+ " WHERE CASE_NO='" + caseNo + "'";
    			        
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 新建住院医生站胸痛资料
	 * @param String
     * @return TParm
     * 
     * Evan
     */
    public TParm insertAmiAdmRecordData(TParm dataParm) {
    	TParm result = new TParm();
    	 String sql = "INSERT INTO AMI_ADM_RECORD (CASE_NO,STATIN_24H,BETA_BLOCKERS_USED,OUT_DIG_STEMI,OUT_DIG_NSTEMI,OUT_DIG_UA,OUT_DIG_DISSECTING_ANEURYSM,OUT_DIG_APTE,OUT_DIG_NACS"
			 		+ ",OUT_DIG_NCCP,OUT_DIG_TIME,IN_LEFT_HEART_FAILURE,HOSPITAL_DAY,VEST_STATUS,TOTAL_FEE,OUT_STATUS,TRNS_TIME,TRNS_HP_NAME,DEAD_TIME,DEAD_CARDIAC,DEAD_NCARDIAC"
			 		+ ",DEAD_DESC,OUT_MED_DAPT,OUT_MED_ACEI_ARB,OUT_MED_STATIN,OUT_MED_BETA_BLOCKERS,OPT_USER,OPT_DATE,OPT_TERM) "
			 		+ "VALUES ("
			 		+ "'"+ dataParm.getValue("CASE_NO") + "'"
			 		+ ",'"+ dataParm.getValue("STATIN_24H") + "'"
			 		+ ",'"+ dataParm.getValue("BETA_BLOCKERS_USED") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_STEMI") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_NSTEMI") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_UA") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_DISSECTING_ANEURYSM") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_APTE") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_NACS") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_DIG_NCCP") + "'"
			 		+ ",to_date('" + dataParm.getValue("OUT_DIG_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			 		+ ",'"+ dataParm.getValue("IN_LEFT_HEART_FAILURE") + "'"
			 		+ ",'"+ dataParm.getValue("HOSPITAL_DAY") + "'"
			 		+ ",'"+ dataParm.getValue("VEST_STATUS") + "'"
			 		+ ",'"+ dataParm.getValue("TOTAL_FEE") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_STATUS") + "'"
			 		+ ",to_date('" + dataParm.getValue("TRNS_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			 		+ ",'"+ dataParm.getValue("TRNS_HP_NAME") + "'"
			 		+ ",to_date('" + dataParm.getValue("DEAD_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			 		+ ",'"+ dataParm.getValue("DEAD_CARDIAC") + "'"
			 		+ ",'"+ dataParm.getValue("DEAD_NCARDIAC") + "'"
			 		+ ",'"+ dataParm.getValue("DEAD_DESC") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_MED_DAPT") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_MED_ACEI_ARB") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_MED_STATIN") + "'"
			 		+ ",'"+ dataParm.getValue("OUT_MED_BETA_BLOCKERS") + "'"
			 		+ ",'" + dataParm.getValue("OPT_USER") + "'"
			 		+ ",sysdate"
			 		+ ",'" + dataParm.getValue("OPT_TERM") + "'"
	        		+ ")";  
    	 
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 更新住院医生站胸痛资料
	 * @param TParm
     * @return TParm
     * 
     * Evan
     */
    public TParm updateAmiAdmRecordData(TParm dataParm) {
    	TParm result = new TParm();
    	String sql = "UPDATE AMI_ADM_RECORD SET "
	    			+ "CASE_NO=" + "'" + dataParm.getValue("CASE_NO") + "'"
	    			+ ",STATIN_24H=" + "'"+ dataParm.getValue("STATIN_24H") + "'"
	    			+ ",BETA_BLOCKERS_USED=" + "'"+ dataParm.getValue("BETA_BLOCKERS_USED") + "'"
	    			+ ",OUT_DIG_STEMI=" + "'"+ dataParm.getValue("OUT_DIG_STEMI") + "'"
	    			+ ",OUT_DIG_NSTEMI=" + "'"+ dataParm.getValue("OUT_DIG_NSTEMI") + "'"
	    			+ ",OUT_DIG_UA=" + "'"+ dataParm.getValue("OUT_DIG_UA") + "'"
	    			+ ",OUT_DIG_DISSECTING_ANEURYSM=" + "'"+ dataParm.getValue("OUT_DIG_DISSECTING_ANEURYSM") + "'"
	    			+ ",OUT_DIG_APTE=" + "'"+ dataParm.getValue("OUT_DIG_APTE") + "'"
	    			+ ",OUT_DIG_NACS=" + "'"+ dataParm.getValue("OUT_DIG_NACS") + "'"
	    			+ ",OUT_DIG_NCCP=" + "'"+ dataParm.getValue("OUT_DIG_NCCP") + "'"
	    			+ ",OUT_DIG_TIME=" + "to_date('" + dataParm.getValue("OUT_DIG_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
	    			+ ",IN_LEFT_HEART_FAILURE=" + "'"+ dataParm.getValue("IN_LEFT_HEART_FAILURE") + "'"
	    			+ ",HOSPITAL_DAY=" + "'"+ dataParm.getValue("HOSPITAL_DAY") + "'"
	    			+ ",VEST_STATUS=" + "'"+ dataParm.getValue("VEST_STATUS") + "'"
	    			+ ",TOTAL_FEE=" + "'"+ dataParm.getValue("TOTAL_FEE") + "'"
	    			+ ",OUT_STATUS=" + "'"+ dataParm.getValue("OUT_STATUS") + "'"
	    			+ ",TRNS_TIME=" + "to_date('" + dataParm.getValue("TRNS_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
	    			+ ",TRNS_HP_NAME=" + "'"+ dataParm.getValue("TRNS_HP_NAME") + "'"
	    			+ ",DEAD_TIME=" + "to_date('" + dataParm.getValue("DEAD_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
	    			+ ",DEAD_CARDIAC=" + "'"+ dataParm.getValue("DEAD_CARDIAC") + "'"
	    			+ ",DEAD_NCARDIAC=" + "'"+ dataParm.getValue("DEAD_NCARDIAC") + "'"
	    			+ ",DEAD_DESC=" + "'"+ dataParm.getValue("DEAD_DESC") + "'"
	    			+ ",OUT_MED_DAPT=" + "'"+ dataParm.getValue("OUT_MED_DAPT") + "'"
	    			+ ",OUT_MED_ACEI_ARB=" + "'"+ dataParm.getValue("OUT_MED_ACEI_ARB") + "'"
	    			+ ",OUT_MED_STATIN=" + "'"+ dataParm.getValue("OUT_MED_STATIN") + "'"
	    			+ ",OUT_MED_BETA_BLOCKERS=" + "'"+ dataParm.getValue("OUT_MED_BETA_BLOCKERS") + "'"
	    			+ ",OPT_USER=" + "'" + dataParm.getValue("OPT_USER") + "'"
	    			+ ",OPT_DATE=" + "sysdate"
	    			+ ",OPT_TERM=" + "'" + dataParm.getValue("OPT_TERM") + "'"
	    			+ " WHERE CASE_NO='" + dataParm.getValue("CASE_NO") + "'";
    			        
    	result = new TParm(this.getDBTool().update(sql));

        
        if (result.getErrCode() < 0) {
//        	err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
        	return result;
        }
		return result;
    }
    
    /**
     * 判断是否有住院医生站胸痛资料
	 * @param caseNo String
     * @return TParm
     * 
     * Evan
     */
    public TParm getAmiAdmRecordDataByCaseNo(String caseNo) {
    	String sql = "SELECT CASE_NO FROM AMI_ADM_RECORD"   			
    			+ " WHERE CASE_NO='" + caseNo + "'";   	
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }

	/**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    /**
     * 确认是否有心电虚拟医嘱
     * 
     * @param caseNo 就诊号
     * @return result
     */
	public TParm getVirtualOrderByCaseNo(String caseNo) {
		// 从配置文件中取得胸痛中心心电医嘱
		String orderCode = TConfig.getSystemValue("CPC_ECG_ORDER");
		String sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO = '" + caseNo + "' AND ORDER_CAT1_CODE = 'ECC' ";
		if (StringUtils.isNotEmpty(orderCode)) {
			sql = sql + " AND ORDER_CODE IN ('" + orderCode.replace(",", "','")
					+ "')";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
    
    /**
     * 依照捡伤号新增首次心电虚拟医嘱
     * @param triageNo
     * @return
     */
    public TParm insertVirtualOrderByTriageNo(String triageNo) {
    	TParm result = new TParm();
    	String optUser = Operator.getID();
		String optTerm = Operator.getIP();
    	String fEcgOrder = TConfig.getSystemValue("FIRST_ECG_ORDER");//首次心电医嘱代码
    	TParm data = SysFeeUtil.getExaData(fEcgOrder);
    	String sql = " INSERT INTO OPD_ORDER"+
                " (CASE_NO,MR_NO,RX_NO,SEQ_NO,REGION_CODE,ADM_TYPE,ORDER_CODE,ORDER_DESC,"+
                " MEDI_QTY,TAKE_DAYS,DOSAGE_QTY,OWN_PRICE,OWN_AMT,AR_AMT," +
                " BILL_FLG,BILL_TYPE,BILL_USER,PRINT_FLG,EXEC_FLG,RECEIPT_FLG," +
                " ORDER_CAT1_CODE,CAT1_TYPE,REXP_CODE,HEXP_CODE," +
                " BILL_DATE,ORDER_DATE,OPT_USER,OPT_TERM,OPT_DATE,VIRTUAL_FLG)"+ 
                " VALUES ('"+ triageNo+ "','"+ triageNo+ "'," +
                " '1',1,'"+Operator.getRegion()+ "','E'," +
                " '"+ data.getValue("ORDER_CODE")+ "'," +
                " '"+ data.getValue("ORDER_DESC")+ "',0,1,1," +
                " "+ data.getDouble("OWN_PRICE")+ "," +
                " "+ data.getDouble("OWN_PRICE")+ "," +
                " "+ data.getDouble("OWN_PRICE")+ "," +
                " 'N',null,null,'N','N','N'," +
                " '"+ data.getValue("ORDER_CAT1_CODE")+ "'," +
                " '"+ data.getValue("CAT1_TYPE")+ "','"+ data.getValue("OPD_CHARGE_CODE")+ "'," +
                " '"+ data.getValue("CHARGE_HOSP_CODE",0)+ "'," +
                " null," +
                " SYSDATE,"+                 
                " '"+optUser+ "',"+
                " '"+optTerm+ "',SYSDATE,'0')";
    	        
    	result = new TParm(this.getDBTool().update(sql));
       
        if (result.getErrCode() < 0) {
        	return result;
        }
		return result;
    }
    
    /**
     * 更新虚拟心电医嘱状态
     * 
	 * @param caseNo 就诊号
	 * @param virtualFlg 医嘱状态
     * @return result
     */
	public TParm updateVirtualFlgByCaseNo(String caseNo, String virtualFlg) {
		// 从配置文件中取得胸痛中心心电医嘱
		String orderCode = TConfig.getSystemValue("CPC_ECG_ORDER");
		String sql = "UPDATE OPD_ORDER SET VIRTUAL_FLG = '" + virtualFlg
				+ "' WHERE CASE_NO = '" + caseNo + "' AND ORDER_CAT1_CODE = 'ECC' ";
		if (StringUtils.isNotEmpty(orderCode)) {
			sql = sql + " AND ORDER_CODE IN ('" + orderCode.replace(",", "','")
					+ "')";
		}
		TParm result = new TParm(this.getDBTool().update(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 向EMR_THRFILE_INDEX表插入数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertEmrThrFileIndex(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String querySql = "SELECT MAX(FILE_SEQ) AS FILE_SEQ "
				+ " FROM EMR_THRFILE_INDEX WHERE CASE_NO='" + caseNo + "'";
		TParm fileSeqParm = new TParm(TJDODBTool.getInstance().select(querySql));
		int fileSeq = fileSeqParm.getInt("FILE_SEQ", 0) + 1;
		String sql = " INSERT INTO EMR_THRFILE_INDEX(CASE_NO,FILE_SEQ,ADM_TYPE,"
				+ " MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,"
				+ " SUBCLASS_CODE,DISPOSAC_FLG,CREATOR_USER,CREATOR_DATE,"
				+ " PDF_CREATOR_USER,PDF_CREATOR_DATE,OPE_BOOK_NO,OPT_USER,OPT_DATE,OPT_TERM)"
				+ " VALUES ('"
				+ caseNo
				+ "',"
				+ fileSeq
				+ ",'"
				+ parm.getValue("ADM_TYPE")
				+ "','"
				+ parm.getValue("MR_NO")
				+ "',NULL,'"
				+ parm.getValue("FILE_PATH")
				+ "','"
				+ parm.getValue("FILE_NAME")
				+ "',NULL,'"
				+ parm.getValue("CLASS_CODE")
				+ "','"
				+ parm.getValue("SUBCLASS_CODE")
				+ "','N','"
				+ parm.getValue("CREATOR_USER")
				+ "',SYSDATE,'"
				+ parm.getValue("PDF_CREATOR_USER")
				+ "',SYSDATE,'"
				+ parm.getValue("OPE_BOOK_NO")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',SYSDATE,'"
				+ parm.getValue("OPT_TERM") + "')";

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
