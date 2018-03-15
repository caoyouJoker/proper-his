package com.javahis.ui.bms;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ѪƷ״̬��ѯ(��ѯ����գ��ر�)
 * </p>
 * 
 * <p>
 * Description: ѪƷ״̬��ѯ
 * </p> 
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author chenx 2013.03.07
 * @version 4.0
 */
public class BMSBloodStatusControl extends TControl{
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit() ;
		callFunction("UI|delete|setEnabled", false);//����
	}
	/**
	 * ��ѯ
	 */
	
	 public  void onQuery(){
		 String bloodno = this.getValueString("BLOOD_NO");
		 
		 if(bloodno == null || "".equals(bloodno)){
			 this.messageBox("�����������") ;
			 return ;
		 }
		 String sqlStaus = "SELECT A.BLD_CODE,A.SUBCAT_CODE,A.APPLY_NO,A.BLD_TYPE,A.SHIT_FLG,A.CROSS_MATCH_L, "+
				 		   "A.CROSS_MATCH_S,A.ANTI_A,A.ANTI_B,A.RESULT,A.TEST_DATE,A.TEST_USER, "+
				 		   "A.STATE_CODE,A.OUT_USER,A.BLOOD_NO "+
				 		   "FROM BMS_BLOOD A "+
				 		   " WHERE BLOOD_NO = '"+bloodno+"' ";
		 
		 TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStaus));
		 if(StringUtil.isNullString(parm.getValue("STATE_CODE",0))){
			 return;
		 }
		 if(!StringUtil.isNullString(parm.getValue("STATE_CODE",0)) && "0".equals(parm.getValue("STATE_CODE",0))){
			 callFunction("UI|save|setEnabled", false);
			 ((TTextFormat)this.getComponent("TRAN_DATE")).setEnabled(false) ;
			 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(false) ;
			 this.onClear() ;
			 this.setValue("BLOOD_NO", parm.getValue("BLOOD_NO",0)) ;
			 this.setValue("BLD_CODE", parm.getValue("BLD_CODE",0)) ;
		     this.setValue("SUBCAT_CODE", parm.getValue("SUBCAT_CODE",0)) ;
		     this.setValue("APPLY_NO", parm.getValue("APPLY_NO",0)) ;
		     this.setValue("SHIT_FLG", parm.getValue("SHIT_FLG",0)) ;
		     this.setValue("CROSS_MATCH_L", parm.getValue("CROSS_MATCH_L",0)) ;
		     this.setValue("CROSS_MATCH_S", parm.getValue("CROSS_MATCH_S",0)) ;
		     this.setValue("ANTI_A", parm.getValue("ANTI_A",0)) ;
		     this.setValue("ANTI_B", parm.getValue("ANTI_B",0)) ;
		     this.setValue("RESULT", parm.getValue("RESULT",0)) ;
		     String testDate = parm.getValue("TEST_DATE",0) ;
		     String testDateStr = "" ;//��������
		     if(null != testDate && !"".equals(testDate)){
		    	 String[] testDateArray = testDate.split(" ") ;
		    	 testDateStr = testDateArray[0].replace("-", "/") ;
		     }
		    
		     this.setValue("TEST_DATE", testDateStr) ;
		     this.setValue("TEST_USER", parm.getValue("TEST_USER",0)) ;
		     this.setValue("BLD_TYPE", parm.getValue("BLD_TYPE",0)) ;

		     this.setValue("STATE_CODE", parm.getValue("STATE_CODE",0)) ;//ѪҺ״̬
		     
			 return ;
		 }
		 callFunction("UI|save|setEnabled", true);
		 ((TTextFormat)this.getComponent("TRAN_DATE")).setEnabled(true) ;
		 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(true) ;
		 String sql = " SELECT A.BLD_CODE,A.SUBCAT_CODE,A.APPLY_NO,A.BLD_TYPE,A.SHIT_FLG,A.CROSS_MATCH_L," +
		 		      " A.CROSS_MATCH_S,A.ANTI_A,A.ANTI_B,A.RESULT,A.TEST_DATE,A.TEST_USER,A.OUT_NO,A.MR_NO," +
		 		      " B.PAT_NAME,B.IDNO,A.STATE_CODE,A.TRAN_DATE,C.USE_DATE, " +
		 		      
		 		      //add by yangjj 20150527
		 		      " A.RECHECK_USER,A.RECHECK_TIME,A.OUT_USER,A.OUT_DATE "+
		 		      
		 		      " FROM  BMS_BLOOD A,SYS_PATINFO B,BMS_APPLYM C " +
		 		      " WHERE BLOOD_NO = '"+bloodno+"' " +
		 		      " AND A.MR_NO = B.MR_NO(+) " +
		 		      " AND A.APPLY_NO = C.APPLY_NO "; 
		 System.out.println("sql=============="+sql);
		 TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;

		 
	     //   ��ô�����Ϣ��Ϣ
	     if (sqlParm.getErrCode() < 0) {
	         messageBox(sqlParm.getErrText());
	    	 return;      
	     }
	     if (sqlParm.getCount() <= 0) {
	            messageBox("��������");
	            return;
	     }  
	     
	     this.setValue("BLD_CODE", sqlParm.getValue("BLD_CODE",0)) ;
	     this.setValue("SUBCAT_CODE", sqlParm.getValue("SUBCAT_CODE",0)) ;
	     this.setValue("APPLY_NO", sqlParm.getValue("APPLY_NO",0)) ;
	     this.setValue("SHIT_FLG", sqlParm.getValue("SHIT_FLG",0)) ;
	     this.setValue("CROSS_MATCH_L", sqlParm.getValue("CROSS_MATCH_L",0)) ;
	     this.setValue("CROSS_MATCH_S", sqlParm.getValue("CROSS_MATCH_S",0)) ;
	     this.setValue("ANTI_A", sqlParm.getValue("ANTI_A",0)) ;
	     this.setValue("ANTI_B", sqlParm.getValue("ANTI_B",0)) ;
	     this.setValue("RESULT", sqlParm.getValue("RESULT",0)) ;
	     String testDate = sqlParm.getValue("TEST_DATE",0) ;
	     String testDateStr = "" ;//��������
	     if(null != testDate && !"".equals(testDate)){
	    	 String[] testDateArray = testDate.split(" ") ;
	    	 testDateStr = testDateArray[0].replace("-", "/") ;
	     }
	    
	     this.setValue("TEST_DATE", testDateStr) ;
	     this.setValue("TEST_USER", sqlParm.getValue("TEST_USER",0)) ;
	     this.setValue("BLD_TYPE", sqlParm.getValue("BLD_TYPE",0)) ;
	     
	     this.setValue("OUT_NO", sqlParm.getValue("OUT_NO",0)) ;//���ⵥ��
	     this.setValue("MR_NO", sqlParm.getValue("MR_NO",0)) ;//������
	     this.setValue("PAT_NAME", sqlParm.getValue("PAT_NAME",0)) ;//��������
	     this.setValue("ID_NO", sqlParm.getValue("IDNO",0)) ;//����֤��
	     
	     String useDate = sqlParm.getValue("USE_DATE",0).toString().substring(0, 10); //��Ѫ���� 
	     //this.messageBox(sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,19)+"");
	     //this.setValue("USE_DATE",  useDate.replace("-", "/")) ;//��Ѫ����
	     this.setValue("USE_DATE",  sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,10)) ;//��Ѫ���ڱ��Ϊ��Ѫ����
	     this.setValue("STATE_CODE", sqlParm.getValue("STATE_CODE",0)) ;//ѪҺ״̬
	     if("2".equals(sqlParm.getValue("STATE_CODE",0))){
	    	 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(false) ;
	     }else{
	    	 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(true) ;
	     }
//	     String tranDate = sqlParm.getValue("TRAN_DATE",0).toString().substring(0, 10); ;//�������
//	     this.setValue("TRAN_DATE", StringTool.getTimestamp(SystemTool.getInstance().getDate().
//	    		 toString().substring(0, 10), "yyyyMMdd")) ;//�������
//	     this.setValue("TRAN_DATE", SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 10)) ;//�������
	     this.setValue("TRAN_DATE", sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,10)) ;//������� ���Ϊ��Ѫ����
		 
	     //add by yangjj 20150527
	     this.setValue("RECHECK_USER", sqlParm.getValue("RECHECK_USER",0));
	     if(!StringUtil.isNullString(sqlParm.getValue("RECHECK_TIME",0))){
	    	 this.setValue("RECHECK_TIME", sqlParm.getValue("RECHECK_TIME",0).replace("-", "/").substring(0,19));
	     }
	     if(!StringUtil.isNullString(sqlParm.getValue("OUT_DATE",0))){
	    	 this.setValue("OUT_DATE", sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,19));
	     }
	     this.setValue("OUT_USER", sqlParm.getValue("OUT_USER",0));
	    
}
	 
	 public void onDelete(){
		 String bloodno = this.getValueString("BLOOD_NO");
		 
		 if(bloodno == null || "".equals(bloodno)){
			 this.messageBox("�����������") ;
			 return ;
		 }	
		 
		 String deleteSql = "DELETE FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodno+"'" ;
		 TParm sqlParm = new TParm(TJDODBTool.getInstance().update(deleteSql)) ;
	     //   ��ô�����Ϣ��Ϣ
	     if (sqlParm.getErrCode() < 0) {
	         messageBox("ɾ��ʧ��");
	    	 return;      
	     }
	     messageBox("ɾ���ɹ�") ;
	     onClear() ;
	 }
	 
	 public void onSave(){
		 String bloodno = this.getValueString("BLOOD_NO");
		 
		 if(bloodno == null || "".equals(bloodno)){
			 this.messageBox("�����������") ;
			 return ;
		 }
		 
		 String selectSql = "SELECT * FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodno+"'" ;
		 TParm sqlParm = new TParm(TJDODBTool.getInstance().select(selectSql)) ;
	     //   ��ô�����Ϣ��Ϣ
	     if (sqlParm.getErrCode() < 0) {
	         messageBox(sqlParm.getErrText());
	    	 return;      
	     }
	     if (sqlParm.getCount() <= 0) {
	            messageBox("�޷����£�û�и�����¼");
	            return;
	     }
	     String stateCode = this.getValueString("STATE_CODE");
	     String tranDate = this.getValueString("TRAN_DATE");
	     
	     if(stateCode == null || "".equals(stateCode)){
	    	 messageBox("��ѡ��ѪҺ״̬!") ;
	    	 return  ;
	     }
	     
	     String updateSql = "" ;
	     if("".equals(tranDate)){
	    	 updateSql = "UPDATE BMS_BLOOD SET STATE_CODE = '"+stateCode+"',TRAN_DATE=null WHERE BLOOD_NO = '"+bloodno+"'" ;
	     }else{
	    	 tranDate = tranDate.substring(0, 19) ;
	    	 String[] str = tranDate.split(" ") ;
	    	 String newTranDate = str[0].replace("-", "") ;
	    	 updateSql = "UPDATE BMS_BLOOD SET STATE_CODE = '"+stateCode+"',TRAN_DATE=TO_DATE('"+newTranDate+"','YYYYMMDD') WHERE BLOOD_NO = '"+bloodno+"'" ;
	     }
	     TParm updateParm = new TParm(TJDODBTool.getInstance().update(updateSql)) ;
	     //   ��ô�����Ϣ��Ϣ
	     if (updateParm.getErrCode() < 0) {
	         messageBox("����ʧ��");
	    	 return;      
	     }	     
	     messageBox("���³ɹ�") ;
	     this.onClear() ;
	 }
	 
	 /**
	  * 
	  */
	 public void onClear(){
		 clearValue("BLOOD_NO;BLD_CODE;SUBCAT_CODE;APPLY_NO;BLD_TYPE;SHIT_FLG;CROSS_MATCH_L;"
				      +"CROSS_MATCH_S;ANTI_A;ANTI_B;RESULT;TEST_DATE;TEST_USER;OUT_NO;MR_NO;PAT_NAME;ID_NO;USE_DATE;STATE_CODE;TRAN_DATE;RECHECK_USER;RECHECK_TIME;OUT_USER;OUT_DATE");
				 
	 }
	 
	 public void onEnterAction(){
		 onQuery() ;
	 }
}