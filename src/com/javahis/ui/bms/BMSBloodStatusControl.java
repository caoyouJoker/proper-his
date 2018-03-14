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
 * Title: 血品状态查询(查询，清空，关闭)
 * </p>
 * 
 * <p>
 * Description: 血品状态查询
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
	 * 初始化
	 */
	public void onInit(){
		super.onInit() ;
		callFunction("UI|delete|setEnabled", false);//至灰
	}
	/**
	 * 查询
	 */
	
	 public  void onQuery(){
		 String bloodno = this.getValueString("BLOOD_NO");
		 
		 if(bloodno == null || "".equals(bloodno)){
			 this.messageBox("请输入条码号") ;
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
		     String testDateStr = "" ;//检验日期
		     if(null != testDate && !"".equals(testDate)){
		    	 String[] testDateArray = testDate.split(" ") ;
		    	 testDateStr = testDateArray[0].replace("-", "/") ;
		     }
		    
		     this.setValue("TEST_DATE", testDateStr) ;
		     this.setValue("TEST_USER", parm.getValue("TEST_USER",0)) ;
		     this.setValue("BLD_TYPE", parm.getValue("BLD_TYPE",0)) ;

		     this.setValue("STATE_CODE", parm.getValue("STATE_CODE",0)) ;//血液状态
		     
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

		 
	     //   获得错误信息消息
	     if (sqlParm.getErrCode() < 0) {
	         messageBox(sqlParm.getErrText());
	    	 return;      
	     }
	     if (sqlParm.getCount() <= 0) {
	            messageBox("查无数据");
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
	     String testDateStr = "" ;//检验日期
	     if(null != testDate && !"".equals(testDate)){
	    	 String[] testDateArray = testDate.split(" ") ;
	    	 testDateStr = testDateArray[0].replace("-", "/") ;
	     }
	    
	     this.setValue("TEST_DATE", testDateStr) ;
	     this.setValue("TEST_USER", sqlParm.getValue("TEST_USER",0)) ;
	     this.setValue("BLD_TYPE", sqlParm.getValue("BLD_TYPE",0)) ;
	     
	     this.setValue("OUT_NO", sqlParm.getValue("OUT_NO",0)) ;//出库单号
	     this.setValue("MR_NO", sqlParm.getValue("MR_NO",0)) ;//病案号
	     this.setValue("PAT_NAME", sqlParm.getValue("PAT_NAME",0)) ;//病患名称
	     this.setValue("ID_NO", sqlParm.getValue("IDNO",0)) ;//身份证号
	     
	     String useDate = sqlParm.getValue("USE_DATE",0).toString().substring(0, 10); //用血日期 
	     //this.messageBox(sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,19)+"");
	     //this.setValue("USE_DATE",  useDate.replace("-", "/")) ;//用血日期
	     this.setValue("USE_DATE",  sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,10)) ;//用血日期变更为发血日期
	     this.setValue("STATE_CODE", sqlParm.getValue("STATE_CODE",0)) ;//血液状态
	     if("2".equals(sqlParm.getValue("STATE_CODE",0))){
	    	 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(false) ;
	     }else{
	    	 ((TComboBox)this.getComponent("STATE_CODE")).setEnabled(true) ;
	     }
//	     String tranDate = sqlParm.getValue("TRAN_DATE",0).toString().substring(0, 10); ;//变更日期
//	     this.setValue("TRAN_DATE", StringTool.getTimestamp(SystemTool.getInstance().getDate().
//	    		 toString().substring(0, 10), "yyyyMMdd")) ;//变更日期
//	     this.setValue("TRAN_DATE", SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 10)) ;//变更日期
	     this.setValue("TRAN_DATE", sqlParm.getValue("OUT_DATE",0).replace("-", "/").substring(0,10)) ;//变更日期 变更为发血日期
		 
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
			 this.messageBox("请输入条码号") ;
			 return ;
		 }	
		 
		 String deleteSql = "DELETE FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodno+"'" ;
		 TParm sqlParm = new TParm(TJDODBTool.getInstance().update(deleteSql)) ;
	     //   获得错误信息消息
	     if (sqlParm.getErrCode() < 0) {
	         messageBox("删除失败");
	    	 return;      
	     }
	     messageBox("删除成功") ;
	     onClear() ;
	 }
	 
	 public void onSave(){
		 String bloodno = this.getValueString("BLOOD_NO");
		 
		 if(bloodno == null || "".equals(bloodno)){
			 this.messageBox("请输入条码号") ;
			 return ;
		 }
		 
		 String selectSql = "SELECT * FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodno+"'" ;
		 TParm sqlParm = new TParm(TJDODBTool.getInstance().select(selectSql)) ;
	     //   获得错误信息消息
	     if (sqlParm.getErrCode() < 0) {
	         messageBox(sqlParm.getErrText());
	    	 return;      
	     }
	     if (sqlParm.getCount() <= 0) {
	            messageBox("无法更新，没有该条记录");
	            return;
	     }
	     String stateCode = this.getValueString("STATE_CODE");
	     String tranDate = this.getValueString("TRAN_DATE");
	     
	     if(stateCode == null || "".equals(stateCode)){
	    	 messageBox("请选择血液状态!") ;
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
	     //   获得错误信息消息
	     if (updateParm.getErrCode() < 0) {
	         messageBox("更新失败");
	    	 return;      
	     }	     
	     messageBox("更新成功") ;
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