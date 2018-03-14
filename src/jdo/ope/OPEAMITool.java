package jdo.ope;

import jdo.sys.Operator;

import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class OPEAMITool {
	private static OPEAMITool instanceObject;
	public OPEAMITool(){
		
	}
	public static synchronized OPEAMITool getInstance() {
        if (instanceObject == null) {
            instanceObject = new OPEAMITool();
        }
        return instanceObject;
    }
	
	
	
	
	  /**
     * 取得AMI
     * @return
     */
    public String[] getErdLevelTemplet(){
    	
        //String subClassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String subClassCode = "EMR02000616";
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
     * 新建保存AMI
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
     * 拿到之前保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getAMIFile(String caseNo)
    {
        //String classCode = TConfig.getSystemValue("ERDLevelCLASSCODE");
        //String subclassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String classCode = "EMR020006";
        String subclassCode = "EMR02000616";
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
    public TParm getPreFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_PREERD_INFO WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(介入室护士)
     * @param caseNo String
     * @return TParm
     */
    public TParm getINTNSFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_INT_NS_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 拿到之前保存的文件(手术室护士)
     * @param caseNo String
     * @return TParm
     */
    public TParm getSURNSFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_SUR_NS_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(手术室医生)
     * @param caseNo String
     * @return TParm
     */
    public TParm getSURDRFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_SUR_DR_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(急诊护士)
     * @param caseNo String
     * @return TParm
     */
    public TParm getERDNSFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_ERD_NS_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 拿到之前保存的文件(CT)
     * @param caseNo String
     * @return TParm
     */
    public TParm getCTFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_CT_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 拿到之前保存的文件(超声)
     * @param caseNo String
     * @return TParm
     */
    public TParm getUTFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_UT_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 拿到之前保存的文件(肺栓塞)
     * @param caseNo String
     * @return TParm
     */
    public TParm getAPTEFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_APTE_RECORD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(急诊医生)
     * @param caseNo String
     * @return TParm
     */
    public TParm getERDDRFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_ERD_DR_RECROD WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    /**
     * 拿到之前保存的文件(GRACE)
     * @param caseNo String
     * @return TParm
     */
    public TParm getGRACEFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_ERD_DR_GRACE WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(掛号)
     * @param caseNo String
     * @return TParm
     */
    public TParm getREGPATADMFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'"));
        if(preeParm.getCount()>0){
        	 return preeParm;
        }else{
        	return null;
        }
       

      
    }
    
    
    /**
     * 拿到之前保存的文件(住院医生)
     * @param caseNo String
     * @return TParm
     */
    public TParm getAMIADMFile(String caseNo)
    {
        
        TParm preeParm = new TParm(this.getDBTool().select("SELECT * FROM AMI_ADM_RECORD WHERE CASE_NO='"+caseNo+"'"));
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
