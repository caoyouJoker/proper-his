package jdo.reg;

import jdo.sys.Operator;

import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>胸痛中心急诊护士记录</p>
 * @author Eric
 *
 */
public class RegPreedTool {
	private static RegPreedTool instanceObject;
	public RegPreedTool(){
		
	}
	public static synchronized RegPreedTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new RegPreedTool();
        }
        return instanceObject;
    }
	
	  /**
     * 拿到模板
     * @return
     */
    public String[] getErdLevelTemplet(){ 	
        String subClassCode = TConfig.getSystemValue("AMI_PRE_SUBCLASSCODE");
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
     * 新建保存急诊检伤
     * @param mrNo String
     * @param caseNo String
     * @return String[]
     */
    public TParm saveELFile(String caseNo,String subclassCode,String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        //String classCode = TConfig.getSystemValue("ERDLevelCLASSCODE");
        String classCode = "EMR020006";
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String mrNo="";
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(2,4)+"\\"+caseNo.substring(4,6)+"\\"+caseNo;
        //String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+caseNo;
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
     * 拿到之前保存的病历
     * @param caseNo String
     * @return String[]
     */
    public String[] getPreedFile(String caseNo)
    {
        String classCode = TConfig.getSystemValue("AMI_PRE_CLASSCODE");
        String subclassCode = TConfig.getSystemValue("AMI_PRE_SUBCLASSCODE");
        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

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
     * 获取院前信息
     * @param triagoNo
     * @return
     */
    public TParm getPreFile(String triagoNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_PREERD_INFO WHERE TRIAGE_NO='"+triagoNo+"'"));
        return preeParm;    
    }
    
    
    
    /**
     * 删除
     * @return 
     */
    public TParm onDeletePreedRecord(String TRIAGE_NO)
    {

		String deletesql =
      	      " DELETE FROM AMI_PREERD_INFO WHERE TRIAGE_NO = '" +
      	    		TRIAGE_NO + "' ";
        TParm delParm = new TParm(TJDODBTool.getInstance().update(
      	   deletesql));
        if(delParm.getErrCode()<0)
            return delParm;
        else
        	return null;

    }
    /**
	 * 插入 AMI_PREERD_INFO
	 * */
	public TParm onInsertPreedRecord(TParm dataParm) {
    	
        String sql = "INSERT INTO AMI_PREERD_INFO (PREERD_NO,TRIAGE_NO,CASE_NO,MR_NO,PAT_LOG_TIME,START_ADD,START_TIME,AT_HOME,CALL_HELP,CALL_HELP_TIME,"
        		+ "CNST_CHEST_PAIN,INTER_CHEST_PAIN,CHEST_PAIN_BETTER,ABDOMINAL_PAIN,HARD_BREATHE,SHOCK,CARDIAC_FAILURE,MALIGNANT_ARRHYTHMIA,CARDIOPULMONARY_RESUSCITAT,"
        		+ "BLEED,ERD_TYPE,AMBULANCE_120,AMBULANCE_IN,AMBULANCE_OUT,DR_ATTEN_TIME,DOOR_TIME,IN_ADMIT_TIME,TRANSFER_NET,TRANSFER_HP_NAME,TRANSFER_DOOR_TIME_OUT,TRANSFER_DECICE_TIME,"
        		+ "TRANSFER_AMBULANCE_TIME,TRANSFER_LEAVE_TIME,TRANSFER_DOOR_TIME_IN,TRANSFER_IN_ADMIT_TIME,TRANSFER_BY_ERD,TRANSFER_TO_CCR,TRANSFER_ARRIVE_TIME,SELF_DOOR_TIME,SELF_ADMIT_TIME,"
        		+ "SELF_BY_CCU,SELF_CUU_ARRIVE_TIME,IN_HP_COME_DEPT,IN_HP_CONS_TIME,FIRST_MEDICAL_ORG,FIRST_DR,FIRST_TIME,FIRST_OUT_ECG_TIME,OPT_USER,OPT_DATE,OPT_TERM,NATIVE_REGION_120,"
        		+ "OTHER_REGION_120,NATIVE_MEDICAL_INSTITUTION,OTHER_ASSESSMENT,ASSESSMENT_LOG,IN_HP_LEAVE_DEPT_TIME)"
 		       + "VALUES ("
 		       +"'1'"
 		       + ",'" + dataParm.getValue("TRIAGE_NO") + "'"
 		       + ",'" + dataParm.getValue("CASE_NO") + "'"
 		       + ",'" + dataParm.getValue("MR_NO") + "'"
 		       + ",to_date('" + dataParm.getValue("PAT_LOG_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("START_ADD") + "'"
 		       + ",to_date('" + dataParm.getValue("START_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("AT_HOME") + "'"
 		       + ",'" + dataParm.getValue("CALL_HELP") + "'"
 		       + ",to_date('" + dataParm.getValue("CALL_HELP_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("CNST_CHEST_PAIN") + "'"
 		       + ",'" + dataParm.getValue("INTER_CHEST_PAIN") + "'"
 		       + ",'" + dataParm.getValue("CHEST_PAIN_BETTER") + "'"
 		       + ",'" + dataParm.getValue("ABDOMINAL_PAIN") + "'"
 		       + ",'" + dataParm.getValue("HARD_BREATHE") + "'"
 		       + ",'" + dataParm.getValue("SHOCK") + "'"
 		       + ",'" + dataParm.getValue("CARDIAC_FAILURE") + "'"
 		       + ",'" + dataParm.getValue("MALIGNANT_ARRHYTHMIA") + "'"
 		       + ",'" + dataParm.getValue("CARDIOPULMONARY_RESUSCITAT") + "'"
 		       + ",'" + dataParm.getValue("BLEED") + "'"
 		       + ",'" + dataParm.getValue("ERD_TYPE") + "'"
 		       + ",'" + dataParm.getValue("AMBULANCE_120") + "'"
 		       + ",'" + dataParm.getValue("AMBULANCE_IN") + "'"
 		       + ",'" + dataParm.getValue("AMBULANCE_OUT") + "'"
 		       + ",to_date('" + dataParm.getValue("DR_ATTEN_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("DOOR_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("IN_ADMIT_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("TRANSFER_NET") + "'"
 		       + ",'" + dataParm.getValue("TRANSFER_HP_NAME") + "'"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_DOOR_TIME_OUT") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_DECICE_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_AMBULANCE_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_LEAVE_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_DOOR_TIME_IN") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_IN_ADMIT_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("TRANSFER_BY_ERD") + "'"
 		       + ",'" + dataParm.getValue("TRANSFER_TO_CCR") + "'"
 		       + ",to_date('" + dataParm.getValue("TRANSFER_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("SELF_DOOR_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("SELF_ADMIT_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("SELF_BY_CCU") + "'"
 		       + ",to_date('" + dataParm.getValue("SELF_CUU_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("IN_HP_COME_DEPT") + "'"
 		       + ",to_date('" + dataParm.getValue("IN_HP_CONS_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + dataParm.getValue("FIRST_MEDICAL_ORG") + "'"
 		       + ",'" + dataParm.getValue("FIRST_DR") + "'"
 		       + ",to_date('" + dataParm.getValue("FIRST_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",to_date('" + dataParm.getValue("FIRST_OUT_ECG_TIME") + "','yyyy/MM/dd HH24:MI')"
 		       + ",'" + Operator.getID() + "'"
		       + ",sysdate"
		       + ",'" + Operator.getIP() + "'"
		       + ",'"+dataParm.getValue("NATIVE_REGION_120")+"'"// 本区域120
		       + ",'"+dataParm.getValue("OTHER_REGION_120")+"'"// 外区120
		       + ",'"+dataParm.getValue("NATIVE_MEDICAL_INSTITUTION")+"'"// 当地医疗机构
		       + ",'"+dataParm.getValue("OTHER_ASSESSMENT")+"'"// 病情评估,其他
		       + ",'"+dataParm.getValue("ASSESSMENT_LOG")+"'"// 病情评估,备注
		       + ",to_date('" + dataParm.getValue("IN_HP_LEAVE_DEPT_TIME") + "','yyyy/MM/dd HH24:MI')"// 如果院内发病，离开科室时间       
		       + ")";
      
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;

    }
    
    
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
