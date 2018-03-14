package jdo.reg;

import jdo.sys.Operator;

import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class RegSaveTool {
	private static RegSaveTool instanceObject;
	public RegSaveTool(){
		
	}
	public static synchronized RegSaveTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new RegSaveTool();
        }
        return instanceObject;
    }
	
	  /**
     * 取得急诊评分表
     * @return
     */
    public String[] getErdLevelTemplet(){
    	
        //String subClassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String subClassCode = "EMR02000605";
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
     * 删除
     * @return 
     */
    public TParm onDeleteNSRecord(String TRIAGE_NO)
    {

		String deletesql =
      	      " DELETE FROM AMI_ERD_NS_RECCORD WHERE TRIAGE_NO = '" +
      	    		TRIAGE_NO + "' ";
        TParm delParm = new TParm(TJDODBTool.getInstance().update(
      	   deletesql));
        if(delParm.getErrCode()<0)
            return delParm;
        else
        	return null;

    }
    /**
	 * 插入 AMI_ERD_NS_RECCORD
	 * */
	public TParm onInsertNSRecord(TParm parm) {
		
		//System.out.println("FIRST_IN_ECG_TIME---->"+parm.getData("FIRST_IN_ECG_TIME"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//進行轉換
		//Date date = sdf.parse(dateString);
		TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO AMI_ERD_NS_RECORD (TRIAGE_NO,FIRST_IN_ECG_TIME, CONSCIOUS, RESPIRATORY_RATE, PULSE, CARDIAC_RATE,DIASTOLIC_BLOOD_PRESSURE, SYSTOLIC_BLOOD_PRESSURE, KILLIP, TNI_BLOOD_DRAWING_TIME, REPORT_TIME, CTNL,BLOOD_CREATININE,OPT_USER, OPT_DATE, OPT_TERM) VALUES "+
                " ('"+parm.getData("TRIAGE_NO")+"', TO_DATE('"+parm.getData("FIRST_IN_ECG_TIME")+"','yyyy/MM/dd HH24:mi:ss'), '"+parm.getData("CONSCIOUS")+"', '"+parm.getData("RESPIRATORY_RATE")+"', "+
                " '"+parm.getData("PULSE")+"', '"+parm.getData("CARDIAC_RATE")+"', '"+parm.getData("DIASTOLIC_BLOOD_PRESSURE")+"', '"+parm.getData("SYSTOLIC_BLOOD_PRESSURE")+"', "+
                " '"+parm.getData("KILLIP")+"', TO_DATE('"+parm.getData("TNI_BLOOD_DRAWING_TIME")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("REPORT_TIME")+"','yyyy/MM/dd HH24:mi:ss'), '"+parm.getData("CTNL")+"', '"+parm.getData("BLOOD_CREATININE")+"', "+
                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"')"));

//			if(saveParm.getErrCode()<0)
			return saveParm;

	}
    /**
     * 拿到之前保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getELFile(String caseNo)
    {
        //String classCode = TConfig.getSystemValue("ERDLevelCLASSCODE");
        //String subclassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String classCode = "EMR020006";
        String subclassCode = "EMR02000605";
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
     * 拿到之前保存的文件
     * @param caseNo String
     * @return TParm
     */
    public TParm getPreFile(String triagoNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_PREERD_INFO WHERE TRIAGE_NO='"+triagoNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
