package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class BILQeReconciliationControl extends TControl{
	private static TTable table;
	
	/**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");

        initPage();
    }
    
    /**
     * 初始化界面数据
     */
    public void initPage() {

        Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
    	
    }
    public void onQuery(){
    	
    	String startDate = getValueString("START_DATE").replaceAll("/", "-").substring(0, 19);
		String endDate = getValueString("END_DATE").replaceAll("/", "-").substring(0, 19);

    	TParm parm = new TParm();
    	parm.setData("S_DATE", startDate);
    	parm.setData("E_DATE", endDate);
    	System.out.println(parm);
    
    	
    	TParm result = TIOM_AppServer.executeAction("action.reg.REGQeAppAction",
				"reconciliationQe", parm);
    	
    	System.out.println(result);
    	if(result.getCount() < 0){
    		this.messageBox("Q医没有返回数据");
    		table.removeRowAll();
    		return;
    	}
//    	String sql = "SELECT B.CASE_NO,C.PAT_NAME,A.*  FROM EKT_APP_TRADE A,EKT_TRADE B,SYS_PATINFO C" +
//    			" WHERE A.ORDER_TIME BETWEEN TO_DATE('"+startDate+"','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"+startDate+"','YYYY-MM-DD HH24:MI:SS')" +
//    			" AND A.TRADE_NO = B.TRADE_NO" +
//    			" AND A.MR_NO = C.MR_NO" +
//    			" ORDER BY A.ORDER_TIME";
    	
    	String sql = "SELECT A.CASE_NO,A.MR_NO,B.PAT_NAME,A.ORDER_NO,A.AR_AMT,A.PAY_MEDICAL_CARD," +
    			" A.PAY_INS_CARD,A.ALIPAY,A.RE_SOURCE,'挂号' SOURCE" +
    			" FROM BIL_REG_RECP A, SYS_PATINFO B" +
    			" WHERE A.MR_NO = B.MR_NO" +
    			" AND A.ORDER_NO IS NOT NULL" +
    			" AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"','YYYY-MM-DD HH24:MI:SS')" +
    			" AND TO_DATE ('"+endDate+"', 'YYYY-MM-DD HH24:MI:SS')" +
    			" UNION ALL " +
    			" SELECT A.CASE_NO,A.MR_NO,B.PAT_NAME,A.ORDER_NO,A.AR_AMT,A.PAY_MEDICAL_CARD," +
    			" A.PAY_INS_CARD,A.ALIPAY,A.RE_SOURCE,'缴费' SOURCE" +
    			" FROM BIL_OPB_RECP A, SYS_PATINFO B" +
    			" WHERE A.MR_NO = B.MR_NO" +
    			" AND A.ORDER_NO IS NOT NULL" +
    			" AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYY-MM-DD HH24:MI:SS')" +
    			" AND TO_DATE ('"+endDate+"', 'YYYY-MM-DD HH24:MI:SS')";
    	System.out.println(sql);
    	TParm treadParm = new TParm(TJDODBTool.getInstance().select(sql));
    	TParm tableParm = new TParm();
    	List<String> orderNos = new ArrayList<String>();
    	for (int i = 0; i < result.getCount(); i++) {
//    		tableParm.addRowData(result, i);
    		String orderNo = result.getValue("OrderNo", i);
    		boolean flg = false;
    		for (int j = 0; j < treadParm.getCount(); j++) {
    			if(orderNo.equals(treadParm.getValue("ORDER_NO", j))){
    				tableParm.addData("CASE_NO", treadParm.getValue("CASE_NO", j));
    				tableParm.addData("MR_NO", treadParm.getValue("MR_NO", j));
    				tableParm.addData("PAT_NAME", treadParm.getValue("PAT_NAME", j));
//    				tableParm.addData("ORDER_NO", treadParm.getValue("ORDER_NO", j));
    				tableParm.addData("AR_AMT", treadParm.getValue("AR_AMT", j));
    				tableParm.addData("PAY_MEDICAL_CARD", treadParm.getValue("PAY_MEDICAL_CARD", j));
    				tableParm.addData("ALIPAY", treadParm.getValue("ALIPAY", j));
    				tableParm.addData("PAY_INS_CARD", treadParm.getValue("PAY_INS_CARD", j));
    				flg = true;
    				break;
    			}
				
			}
    		if(!flg){
    			tableParm.addData("CASE_NO", "");
				tableParm.addData("MR_NO", "");
				tableParm.addData("PAT_NAME", "");
				
				tableParm.addData("AR_AMT", "");
				tableParm.addData("PAY_MEDICAL_CARD", "");
				tableParm.addData("ALIPAY", "");
				tableParm.addData("PAY_INS_CARD", "");
    		}
//    		CASE_NO;MR_NO;PAT_NAME;ORDER_NO;AR_AMT;PAY_MEDICAL_CARD;ALIPAY;PAY_MONEY;PAY_TYPE;BUSS_TYPE;SOURCE;ORDER_TIME;PAY_TIME;BUSS_TIME
    		
    		tableParm.addData("PAY_MONEY", result.getValue("PayMoney", i));
    		tableParm.addData("PAY_TYPE", result.getValue("PayType", i));
    		tableParm.addData("BUSS_TYPE", result.getValue("BussType", i));
    		tableParm.addData("BUSS_SN", result.getValue("BussSN", i));
    		tableParm.addData("SOURCE", result.getValue("Source", i));
    		tableParm.addData("ORDER_TIME", result.getValue("OrderTime", i));
    		tableParm.addData("PAY_TIME", result.getValue("PayTime", i));
    		tableParm.addData("BUSS_TIME", result.getValue("BussTime", i));
    		tableParm.addData("ORDER_NO", orderNo);
			
		}
    	
//    	if(treadParm.getCount() > result.getCount()){
//    		
//    	}
    	
    	table.setParmValue(tableParm);
    	
    }
    
    public void onClear(){
    	initPage();
    	table.removeRowAll();

    	
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "Q医对账报表");
	}
    


}
