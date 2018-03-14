package com.javahis.ui.opb;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 门诊对账查询</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangp 2012.3.29
 * @version 1.0
 */
public class OPBUnAccountDaily extends TControl{
	TTable table ;
	/**
     * 初始化
     */
    public void onInit() {
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	setValue("REGION_CODE",Operator.getRegion());
    	setValue("CASH_CODE",Operator.getID());
    	setValue("ADM_TYPE","O");
    	table = (TTable) getComponent("TABLE");
    }
    /**
     * 查询
     */
    public void onQuery(){
    	table.removeRowAll();
    	String startDate = getValueString("START_DATE")+getValueString("S_TIME");
    	String endDate = getValueString("END_DATE")+getValueString("E_TIME");
    	if(startDate.equals("")||endDate.equals("")){
    		messageBox("请选择时间段");
    		return;
    	}else{
    		startDate = startDate.substring(0, 4)+startDate.substring(5, 7)+startDate.substring(8, 10)
    		+startDate.substring(11, 13)+startDate.substring(14, 16)+startDate.substring(17, 19);
    		endDate = endDate.substring(0, 4)+endDate.substring(5, 7)+endDate.substring(8, 10)
    		+endDate.substring(11, 13)+endDate.substring(14, 16)+endDate.substring(17, 19);
    	}
    	String sql = getSql(startDate, endDate);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("查无数据");
    		return;
    	}
    	result = getTableParm(result);
    	table.setParmValue(result);
//    	USER_NAME;PRINT_NO;RESET_TYPE;ADM_TYPE;AR_AMT;
//    	PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;PAY_INS_CARD;
//    	OTHER_FEE1;PRINT_DATE;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USER;
//    	PAT_NAME
    }
    /**
     * 获得sql
     * @param startDate
     * @param endDate
     * @return
     */
    public String getSql(String startDate,String endDate){
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT B.USER_NAME,A.MR_NO, A.PRINT_NO," +
    			" CASE" +
    			" WHEN A.RESET_RECEIPT_NO IS NULL" +
    			" THEN '未作废'" +
    			" WHEN A.RESET_RECEIPT_NO IS NOT NULL" +
    			" THEN '作废'" +
    			" END AS RESET_TYPE," +
    			" CASE" +
    			" WHEN A.ADM_TYPE = 'O'" +
    			" THEN '门诊'" +
    			" WHEN A.ADM_TYPE = 'E'" +
    			" THEN '急诊'" +
    			" END AS ADM_TYPE," +
    			" A.AR_AMT, A.PAY_CASH, A.PAY_BANK_CARD, A.PAY_CHECK, A.PAY_MEDICAL_CARD," +
    			" A.PAY_INS_CARD, A.PAY_OTHER1 AS OTHER_FEE1, A.PRINT_DATE,A.ACCOUNT_SEQ,A.ACCOUNT_DATE," +
    			" A.ACCOUNT_USER,C.PAT_NAME" +
    			" FROM BIL_OPB_RECP A, SYS_OPERATOR B, SYS_PATINFO C" +
    			" WHERE A.CASHIER_CODE = B.USER_ID AND A.MR_NO = C.MR_NO ");
    	if(!getValueString("REGION_CODE").equals("")){
    		sql.append(" AND A.REGION_CODE = '"+getValueString("REGION_CODE")+"' ");
    	}
    	if(!getValueString("CASH_CODE").equals("")){
    		sql.append(" AND A.CASHIER_CODE = '"+getValueString("CASH_CODE")+"' ");
    	}
    	if(!getValueString("ADM_TYPE").equals("")){
    		sql.append(" AND A.ADM_TYPE = '"+getValueString("ADM_TYPE")+"' ");
    	}
    	if(!startDate.equals("")&&!endDate.equals("")){
    		sql.append(" AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')");
    	}
    	sql.append(" ORDER BY A.BILL_DATE DESC");
    	return sql.toString();
    }
    /**
     * 清空
     */
    public void onClear(){
    	table.removeRowAll();
    	setValue("REGION_CODE",Operator.getRegion());
    	setValue("CASH_CODE",Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	setValue("ADM_TYPE","O");
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "门诊对账表");
    }
    /**
     * 加总计
     * @param parm
     * @return
     */
    public TParm getTableParm(TParm parm){
    	double ar_amt_tot = 0;
    	double cash_tot = 0;
    	double bank_tot = 0;
    	double check_tot = 0;
    	double ekt_tot = 0;
    	double ins_tot = 0;
    	double other_tot = 0;
    	for (int i = 0; i < parm.getCount(); i++) {
//    			AR_AMT;PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;PAY_INS_CARD;OTHER_FEE1
    			ar_amt_tot += parm.getDouble("AR_AMT", i);
    			cash_tot += parm.getDouble("PAY_CASH", i);
    			bank_tot += parm.getDouble("PAY_BANK_CARD", i);
    			check_tot += parm.getDouble("PAY_CHECK", i);
    			ekt_tot += parm.getDouble("PAY_MEDICAL_CARD", i);
    			ins_tot += parm.getDouble("PAY_INS_CARD", i);
    			other_tot += parm.getDouble("OTHER_FEE1", i);
		}
//    	USER_NAME;PRINT_NO;MR_NO;PAT_NAME;RESET_TYPE;ADM_TYPE;AR_AMT;PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;
//    	PAY_INS_CARD;OTHER_FEE1;PRINT_DATE;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USER
    	parm.addData("USER_NAME", "总计");
    	parm.addData("PRINT_NO", "");
    	parm.addData("MR_NO", "");
    	parm.addData("PAT_NAME", "");
    	parm.addData("RESET_TYPE", "");
    	parm.addData("ADM_TYPE", "");
    	parm.addData("AR_AMT", ar_amt_tot);
    	parm.addData("PAY_CASH", cash_tot);
    	parm.addData("PAY_BANK_CARD", bank_tot);
    	parm.addData("PAY_CHECK", check_tot);
    	parm.addData("PAY_MEDICAL_CARD", ekt_tot);
    	parm.addData("PAY_INS_CARD", ins_tot);
    	parm.addData("OTHER_FEE1", other_tot);
    	parm.addData("PRINT_DATE", "");
    	parm.addData("ACCOUNT_SEQ", "");
    	parm.addData("ACCOUNT_DATE", "");
    	parm.addData("ACCOUNT_USER", "");
    	return parm;
    }
}
