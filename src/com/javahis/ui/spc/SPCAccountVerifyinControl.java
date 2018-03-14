package com.javahis.ui.spc;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.spc.SPCAccountVerifyinTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

public class SPCAccountVerifyinControl extends TControl {
	
	/* Table控件 */
	private TTable table_count;
	
    public SPCAccountVerifyinControl() {
        super();
    }
    
    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }
    
    /**
     * 初始画面数据
     */
    private void initPage() {
    	 Date d = new Date();  
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
         String dateNowStr = sdf.format(d);   
         dateNowStr = dateNowStr.substring(0, 7)+"-25 23:59:59";
         setValue("ACCOUNT_DATE", dateNowStr.replace('-', '/'));					
    }
    
    public void onQuery() {
    	 Date d = new Date();  
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
         String dateNowStr = sdf.format(d);  
         dateNowStr = (dateNowStr.substring(0, 7)+"-25").replaceAll("-", "");
         TParm parm = new TParm();
         parm.setData("CLOSE_DATE",dateNowStr);
       //  parm.setData("CLOSE_DATE","20131125");
         TParm result = SPCAccountVerifyinTool.getInstance().getSpcAccount(parm);
         double accountAmt = 0.0;
         double verifyinAmt = 0.0;
         double diffAmt = 0.0;
         for(int i=0;i<result.getCount();i++) {
        	 accountAmt = accountAmt+result.getDouble("ACCOUNT_AMT",i);
        	 TParm searchParm = new TParm();
        	 searchParm.setData("VERIFYIN_DATE",dateNowStr.replaceAll("-", "/")+" 23:59:59");
        	 String orderCode = result.getValue("ORDER_CODE", i);
        	 searchParm.setData("ORDER_CODE",orderCode);
        	 TParm searchresult = SPCAccountVerifyinTool.getInstance().getVerifyin(searchParm);
        	 Double diff = result.getDouble("ACCOUNT_AMT",i)-searchresult.getDouble("VERIFYIN_AMT",0);
        	 verifyinAmt = verifyinAmt +searchresult.getDouble("VERIFYIN_AMT",0);
        	 diffAmt = diffAmt + diff;				
        	 DecimalFormat df = new DecimalFormat("0.00");
				String hisAmt = String.valueOf(df.format(diff));
				diff = Double.valueOf(hisAmt);
				//diff = Math.abs(diff);
        	 if(searchresult.getCount()<=0) {
        		 result.addData("VERIFYIN_QTY", 0);
        		 result.addData("VERIFYIN_PRICE", 0);
        		 result.addData("VERIFYIN_AMT", 0);
        		 result.addData("DIFF", diff);
        	 }else {	
        		 result.addData("VERIFYIN_QTY", searchresult.getDouble("VERIFYIN_QTY",0));
        		 result.addData("VERIFYIN_PRICE", searchresult.getDouble("VERIFYIN_PRICE",0));
        		 result.addData("VERIFYIN_AMT", searchresult.getDouble("VERIFYIN_AMT",0));
        		 result.addData("DIFF", diff);
        	 }
        	 if(Math.abs(diff)>1) {
        		 this.getTTable("TABLE_ACCOUNT").setRowTextColor(i,
							new Color(255, 0, 0));
        	 }
         }		
         table_count = this.getTTable("TABLE_ACCOUNT");
         table_count.setParmValue(result);
         DecimalFormat df = new DecimalFormat("0.00");
			String aAmt = String.valueOf(df.format(accountAmt));
		//	accountAmt = Double.valueOf(aAmt);
			String vAmt = String.valueOf(df.format(verifyinAmt));
		//	verifyinAmt = Double.valueOf(vAmt);								
			String dAmt = String.valueOf(df.format(diffAmt));
		//	diffAmt = Double.valueOf(dAmt);
         this.setValue("accountAmt", aAmt);
         this.setValue("verifyinAmt", vAmt);
         this.setValue("diffAmt", dAmt);	  													
    }
    
	/**
	 * 获取TTable控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	} 
	
	public void onClear() {
		 table_count.removeRowAll();
		 this.setValue("accountAmt", "");
         this.setValue("verifyinAmt", "");
         this.setValue("diffAmt", "");	  		
	}

	
}
