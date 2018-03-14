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
	//AMI�ܱ�
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
	 //��ȡ��ʼ��������Ϣ
	 public TParm getInitValue(){
		 TParm parm = new TParm();
		 String sql = "SELECT TRIAGE_NO,CASE_NO,MR_NO,LEVEL_CODE,PAT_NAME,SEX_CODE,"
		 		+ " TO_CHAR( COME_TIME,'syyyy-mm-dd hh24:mi:ss') AS COME_TIME"
		 		+ " FROM ERD_EVALUTION";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ��ʹ����Ժǰ��Ϣ
	 public TParm getAmiPreerdInfo(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT *FROM AMI_PREERD_INFO WHERE TRIAGE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ��ʹ���ļ��ﻤʿ��¼
	 public TParm getAmiErdNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_ERD_NS_RECORD WHERE TRIAGE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ��ʹ���ļ���ҽ����¼
	 public TParm getAmiErdDrRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_ERD_DR_RECROD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ�ʳ�����ʹ���ݼ�¼
	 public TParm getAmiUtRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_UT_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡCT����ʹ�������ݼ�¼
	 public TParm getAmiCtRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_CT_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ�����һ�ʿ-��ʹ�������ݼ�¼
	 public TParm getAmiSurNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_SUR_NS_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //��ȡ������ҽ��-��ʹ�������ݼ�¼
	 public TParm getAmiSurDrRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_SUR_DR_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //�����һ�ʿ-��ʹ�������ݼ�¼
	 public TParm getAmiIntNsRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_INT_NS_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //�ζ���˨��ATPE����Σ�շֲ�
	 public TParm getAmiApteRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMI_APTE_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 //סԺҽ��վ-��ʹ���ļ�¼
	 public TParm getAmiAdmRecord(String str){
		 TParm parm = new TParm();
		 String sql = "SELECT * FROM AMIADM_RECORD WHERE CASE_NO = '"+str+"'";
		 parm = new TParm(TJDODBTool.getInstance().select(sql));
		 return parm;
	 }
	 
	 
	 
	
	
}
