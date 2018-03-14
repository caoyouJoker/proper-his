package com.javahis.ui.ami;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class AMIRecordTool {
	
	private static AMIRecordTool instanceObject;
	public AMIRecordTool(){
		
	}
	public static synchronized AMIRecordTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new AMIRecordTool();
        }
        return instanceObject;
    }
	//AMI总表
	 public String[] getErdLevelTemplet_0(){
	    	
	        String subClassCode = TConfig.getSystemValue("REGDataLabelSUBCLASSCODE");
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
	 //获取初始化病患信息
	 public TParm getInitValue(){
		 TParm parm = new TParm();
		 String sql = "SELECT TRIAGE_NO,CASE_NO,MR_NO,LEVEL_CODE,PAT_NAME,SEX_CODE,"
		 		+ " TO_CHAR( COME_TIME,'syyyy-mm-dd hh24:mi:ss') AS COME_TIME"
		 		+ " FROM ERD_EVALUTION";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取胸痛中心院前信息
	 public TParm getAmiPreerdInfo(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT *FROM AMI_PREERD_INFO WHERE TRIAGE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取胸痛中心急诊护士记录
	 public TParm getAmiErdNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_ERD_NS_RECORD WHERE TRIAGE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取胸痛中心急诊医生记录
	 public TParm getAmiErdDrRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_ERD_DR_RECROD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取彩超室胸痛数据记录
	 public TParm getAmiUtRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_UT_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取CT室胸痛中心数据记录
	 public TParm getAmiCtRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_CT_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取手术室护士-胸痛中心数据记录
	 public TParm getAmiSurNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_SUR_NS_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //获取手术室医生-胸痛中心数据记录
	 public TParm getAmiSurDrRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_SUR_DR_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //介入室护士-胸痛中心数据记录
	 public TParm getAmiIntNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_INT_NS_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //肺动脉栓塞ATPE死亡危险分层
	 public TParm getAmiApteRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_APTE_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //住院医生站-胸痛中心记录
	 public TParm getAmiAdmRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMIADM_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 
	 
	 
	
	
}
