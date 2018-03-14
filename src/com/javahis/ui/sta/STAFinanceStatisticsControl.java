package com.javahis.ui.sta;

import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: 财务统计表
 * Description:财务统计表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STAFinanceStatisticsControl extends TControl {
	private TTable table1;
	private TTable table2;
	// 页签
	private TTabbedPane tabbedPane;
    public void onInit() {
        super.onInit();
        table1 = (TTable) this.getComponent("TABLE1");
        table2 = (TTable) this.getComponent("TABLE2");
    	tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
        onClear();
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}
	    return false; 
	}
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;   	
    	String sql ="";  	
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//查询未生成PDF病历医嘱数据
		if(tabbedPane.getSelectedIndex()==0){
    	sql =" SELECT A.MR_NO, A.PAT_NAME, C.IN_DATE, C.DS_DATE, A.ORDER_DESC,"+
    		" (SELECT DEPT_CHN_DESC FROM SYS_DEPT D"+
    		" WHERE D.DEPT_CODE = C.IN_DEPT_CODE) AS IN_DEPT_DESC,"+
    		" (SELECT DEPT_CHN_DESC FROM SYS_DEPT D"+
    		" WHERE D.DEPT_CODE = C.DS_DEPT_CODE) AS DS_DEPT_DESC, B.EFF_DATE,F.USER_NAME"+
    		" FROM MED_APPLY A,ODI_ORDER B,ADM_INP C,SYS_OPERATOR F"+
    		" WHERE A.CASE_NO = B.CASE_NO"+
    		" AND A.ORDER_NO = B.ORDER_NO"+
    		" AND A.SEQ_NO = B.ORDER_SEQ"+
    		" AND F.USER_ID = B.ORDER_DR_CODE"+
    		" AND A.CASE_NO = C.CASE_NO"+
    		" AND (A.PDFRE_FLG = 'N' OR A.PDFRE_FLG IS NULL)"+
    		" AND A.ORDER_CAT1_CODE IN ('LIS', 'ULT', 'RIS')"+
    		" AND C.CANCEL_FLG = 'N'"+
    		" AND C.DS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
    		" AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    		" ORDER BY C.DS_DATE ASC";		
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE1")).removeRowAll();
			return;
		}	
    	((TTable) getComponent("TABLE1")).setParmValue(result);    	
	}
		//查询手术工作量数据
	else if(tabbedPane.getSelectedIndex()==1){
		sql =" SELECT A.BILL_DATE, A.ORDER_CODE , B.ORDER_DESC, " +
		    " A.OWN_PRICE, A.DOSAGE_QTY , A.TOT_AMT" +
			" FROM IBS_ORDD A,SYS_FEE B" +
			" WHERE  A.ORDER_CODE = B.ORDER_CODE" +
		    " AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
		    " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		    " AND A.ORDER_CODE LIKE 'M%'" +
		    " AND A.ORDER_CODE NOT IN ('M1400060','M1400061','M1400062'," +
		    " 'M1400063','M1400064','M1400065'," +
		    " 'M1400067','M1400068','M1400069','M1400070','M1400071'," +
		    " 'M1400072','M1400073','M1400074')" +
		    " AND A.EXE_DEPT_CODE NOT IN ('0306')" +
		    " ORDER BY A.ORDER_CODE";		
//			 System.out.println("sql========="+sql);
			result = new TParm(TJDODBTool.getInstance().select(sql));
//			 System.out.println("result========="+result);   		 
			// 判断错误值
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				messageBox("E0005");//执行失败
				return;
			}
			if (result.getCount()<= 0) {
				messageBox("E0008");//查无资料
				((TTable) getComponent("TABLE2")).removeRowAll();
				return;
			}	
	    	((TTable) getComponent("TABLE2")).setParmValue(result);   	
		}
		
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	 if(tabbedPane.getSelectedIndex()==0){
    		 title ="未生成PDF病历医嘱统计表";	
    	    if (table1.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table1,title);
    	 }
    	else if(tabbedPane.getSelectedIndex()==1){
    		 title ="手术工作量统计表";	
    	     if (table2.getRowCount() > 0)   		
    		ExportExcelUtil.getInstance().exportExcel(table2,title);	
    	}
    }   	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE1|setParmValue", new TParm());
 	    this.callFunction("UI|TABLE2|setParmValue", new TParm());
    }
}
