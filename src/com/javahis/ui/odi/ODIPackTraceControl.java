package com.javahis.ui.odi;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.TypeTool;



/**
 * Title: 器械包追踪查询
 * Description:器械包追踪查询
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class ODIPackTraceControl extends TControl {
	private TTable table;
	private Pat pat; // 病患对象	
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");      
        onClear();
    }
    /**
     * 查询
     */
    public void onQuery() {
    	onTableAdd();	
		
  }
     	
    /**
     * 清空
     */
    public void onClear() {
 	    this.setValue("BAR_CODE","");
 	    this.setValue("MR_NO","");
 	    this.setValue("PAT_NAME","");
 	    this.setValue("SEX_CODE","");
 	    this.setValue("BARCODE", "Y");
 	    TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    /**
     * 条码扫描事件
     */
    public void onExeQuery() {
    	onTableAdd();
    }
    /**
     * 条码扫描回车事件
     */
    public void onScream() {
    	onTableAdd();
    }
    /**
     * 病案号回车事件
     */
    public void onMrno() {
    	onTableAdd();
    }
    public void onTableAdd() {
    	String SQL = "";
    	TParm parmTable = new TParm();
    	if(((TRadioButton) this.getComponent("BARCODE")).isSelected()){
    		//数据检核
        	if(this.getValue("BAR_CODE").equals("")){
        		this.messageBox("条码不能为空");
        		return;
        	}
        	String barcode = getValue("BAR_CODE").toString();
        	 SQL =" AND C.BARCODE ='" + barcode + "'";
    	}else if(((TRadioButton) this.getComponent("MRNO")).isSelected()){
    		if(this.getValue("MR_NO").equals("")){
        		this.messageBox("病案号不能为空");
        		return;
        	}
    		pat = Pat.onQueryByMrNo(PatTool.getInstance().checkMrno(
    				TypeTool.getString(getValue("MR_NO"))));
    		setValue("MR_NO", PatTool.getInstance().checkMrno(
    				TypeTool.getString(getValue("MR_NO"))));
    		setValue("PAT_NAME", pat.getName().trim());
    		setValue("SEX_CODE",pat.getSexString());
    		String mrno = PatTool.getInstance().checkMrno(
    				TypeTool.getString(getValue("MR_NO")));
    		 SQL =" AND G.MR_NO ='" + mrno + "'";      		
    	}
    	//查询数据
   	      parmTable = onRefresh(SQL); 
//   	 System.out.println("parmTable=========="+parmTable);
   	      if(parmTable.getCount()<=0){
   		      this.messageBox("查无数据"); 
   		      table.removeRowAll();
   		      return; 
   	 }
    	 table.setParmValue(parmTable);      
    }
    /**
     * 查询（共用）
     */
    public TParm onRefresh(String SQL) {
    	//出库、接收、使用
    	String sql =" SELECT B.USER_NAME AS DISINFECTION_USER,A.DISINFECTION_DATE ," +
    			" D.USER_NAME AS REPACK_USER,C.REPACK_DATE,F.USER_NAME AS STERILLZATION_USER," +
    			" E.STERILLZATION_DATE,E.STERILIZATION_POTSEQ,E.STERILIZATION_NUM," +
    			" E.STERILLZATION_PROGRAM,J.USER_NAME AS OUTSTOCK_USER,G.OPT_DATE AS OUTSTOCK_DATE," +
    			" K.USER_NAME AS RECEIVE_USER,G.RECEIVE_DATE,I.DEPT_CHN_DESC AS RECEIVE_DEPT," +
    			" L.USER_NAME AS CHECK_USER,G.CHECK_DATE,G.MR_NO,H.PAT_NAME,C.BARCODE,M.PACK_DESC,C.QTY" +
    			" FROM INV_DISINFECTION A,SYS_OPERATOR B,INV_REPACK C, SYS_OPERATOR D," +
    			" INV_STERILIZATION E,SYS_OPERATOR F,INV_SUP_DISPENSED G,SYS_PATINFO H," +
    			" SYS_DEPT I,SYS_OPERATOR J, SYS_OPERATOR K,SYS_OPERATOR L,INV_PACKM M" +
    			" WHERE  A.DISINFECTION_USER = B.USER_ID(+)" +
    			" AND  C.REPACK_USER = D.USER_ID(+)" +
    			" AND  E.STERILLZATION_USER = F.USER_ID(+)" +
    			" AND C.BARCODE = A.BARCODE(+)" +
    			" AND C.BARCODE = E.BARCODE(+)" +
    			" AND C.BARCODE = G.BARCODE(+)" +
    			" AND G.MR_NO = H.MR_NO(+)" +
    			" AND G.RECEIVE_DEPT = I.DEPT_CODE(+)" +
    			" AND G.OPT_USER = J.USER_ID(+)" +
    			" AND G.RECEIVE_USER = K.USER_ID(+)" +
    			" AND G.CHECK_USER =L.USER_ID(+)" + 
    			" AND C.PACK_CODE =M.PACK_CODE" +SQL;
//    	System.out.println("sql=========="+sql);
    	TParm parmTable = new TParm(TJDODBTool.getInstance().select(sql));
        return parmTable;    	
    }
    /**
     * 选择事件
     */
    public void onSel(){
    	if(((TRadioButton) this.getComponent("BARCODE")).isSelected()){
    		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
    		bar.grabFocus();
    		this.setValue("MR_NO","");
    	 	this.setValue("PAT_NAME","");
    	 	this.setValue("SEX_CODE","");
    	}else if(((TRadioButton) this.getComponent("MRNO")).isSelected()){
    		TTextField mrno = ((TTextField) getComponent("MR_NO"));
    		mrno.grabFocus();
    		this.setValue("BAR_CODE","");
    	}
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    /**
     * 查询（共用）
     */
    public void onData(String barcode,int row,TParm parm) {  	
    	String code = ""; 
    	 for (int i = 0; i < row; i++) {
    		 if(!barcode.equals("")){
        		 code= barcode;
        		 } 
    		 if(barcode.equals("")){
        		 code= parm.getValue("BARCODE", row);
        		 }
    		 //回收
    		 String sql1 =" SELECT B.USER_NAME AS DISINFECTION_USER,A.DISINFECTION_DATE"+ 
    			 " FROM INV_DISINFECTION A,SYS_OPERATOR B"+
    			 " WHERE  A.DISINFECTION_USER = B.USER_ID(+)"+
    			 " AND A.BARCODE = '" + code + "' ";
    		 TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    		 //打包
    		 String sql2 =" SELECT B.USER_NAME AS REPACK_USER,A.REPACK_DATE"+ 
    		     " FROM INV_REPACK A,SYS_OPERATOR B"+ 
    		     " WHERE A.REPACK_USER = B.USER_ID(+)"+ 
    		     " AND A.BARCODE = '" + code + "' ";
    		 TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		 //灭菌
    		 String sql3 =" SELECT B.USER_NAME AS STERILLZATION_USER,A.STERILLZATION_DATE,"+
    			 " A.STERILIZATION_POTSEQ,A.STERILIZATION_NUM,A.STERILLZATION_PROGRAM"+
    			 " FROM INV_STERILIZATION A,SYS_OPERATOR B"+
    			 " WHERE A.STERILLZATION_USER = B.USER_ID(+)"+
    			 " AND A.BARCODE = '" + code + "' ";
    		 TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3)); 
    		 if(parm==null&&result1==null&&result2==null&&result3==null){
    			 this.messageBox("查无数据");
    			 table.removeRowAll();
        		 return; 
    		 }else if(parm!=null||result1!=null||result2!=null||result3!=null){
                 String packcode = code.substring(0,6);
                 System.out.println("packcode=========="+packcode);
        		 parm.addData("BAR_CODE", code);
        		 String sql = " SELECT A.PACK_DESC FROM INV_PACKM A"+
        		              " WHERE A.PACK_CODE = '" + packcode + "'";
        		 TParm result = new TParm(TJDODBTool.getInstance().select(sql)); 
        		 parm.addData("PACK_DESC", result.getValue("PACK_DESC",0));
    		 }
	         
    	 }
    	 table.setParmValue(parm);	 
    }
}
