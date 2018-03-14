package com.javahis.ui.adm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

import java.text.DecimalFormat;
/**
 * <p>
 * Title: 住院处工作量统计
 * </p>
 * 
 * <p>
 * Description:住院处工作量统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author yyn 201710
 * @version 1.0
 */

public class ADMWorkSummaryControl extends TControl{
	private TTable table;
	DecimalFormat df1 = new DecimalFormat("########0.00");
	public ADMWorkSummaryControl(){
		
	}
	
	/*
	 * 初始化
	 */
	public void onInit(){
		initPage();
	}
	
	/*
	 * 初始化界面
	 */
	private void initPage(){
		table = (TTable) this.getComponent("TABLE");
		
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
			"yyyyMMddHHmmss"));// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
			"yyyyMMddHHmmss"));// 结束时间
		this.setValue("USER_ID", Operator.getID());
		table.removeRowAll();		
	}
	
	/**
     * 清空
     */
    public void onClear(){
    	initPage();
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "住院处工作量统计表");
    }
    
    /**
     * 查询
     */
    public void onQuery(){
    	TParm returnParm = new TParm();
    	StringBuffer sql=new StringBuffer();
    	StringBuffer sql1=new StringBuffer();
    	if (this.getValueString("START_DATE").length() == 0) {
			messageBox("开始时间不正确!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("结束时间不正确!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		String id = "";
		
		//预交金数据查询SQL语句
		sql.append("SELECT N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,SUM(Y.PRE_AMT) AS PRE_AMT,SUM(Y.NUM) AS NUM,Y.PAY_TYPE"
				+ " FROM "
				+ "(SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT"
				+ " FROM ADM_INP A,SYS_OPERATOR B"
				+ " WHERE A.ADM_CLERK=B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS') GROUP BY A.ADM_CLERK,B.USER_NAME) N,"
				+ "(SELECT BIL_PAY.TRANSACT_TYPE,"
				+ " CASE WHEN TRANSACT_TYPE ='01' THEN SUM(BIL_PAY.PRE_AMT) WHEN TRANSACT_TYPE ='02' THEN SUM(BIL_PAY.PRE_AMT) END AS PRE_AMT,"
				+ " CASE WHEN TRANSACT_TYPE ='01' THEN COUNT(BIL_PAY.MR_NO) WHEN TRANSACT_TYPE ='02' THEN COUNT(BIL_PAY.MR_NO) END AS NUM,"
				+ " BIL_PAY.PAY_TYPE,BIL_PAY.RECEIPT_NO,BIL_PAY.CASHIER_CODE"
				+ " FROM BIL_PAY"
				+ " WHERE BIL_PAY.CHARGE_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')" 
				+ " GROUP BY BIL_PAY.TRANSACT_TYPE,BIL_PAY.PAY_TYPE,BIL_PAY.RECEIPT_NO,BIL_PAY.CASHIER_CODE"
				+ " ORDER BY NUM) Y"
				+ " WHERE Y.CASHIER_CODE = N.ADM_CLERK(+)");
				
		id = this.getValueString("USER_ID");		
		if (id.length() > 0) {//收费员已选择
			sql.append(" AND N.ADM_CLERK = '" + id +"' ");
		}				
		sql.append(" GROUP BY N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,Y.PAY_TYPE"
				+ " ORDER BY N.ADM_CLERK");
		TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (bilPayParm.getErrCode() < 0) {
			System.out.println("error");
			return;
		}
		
		//收费员查询SQL语句
		sql1.append("SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT "
				+ " FROM ADM_INP A,SYS_OPERATOR B"
				+ " WHERE A.ADM_CLERK = B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')");				
		id = this.getValueString("USER_ID");		
		if (id.length() > 0) {//收费员已选择
			sql1.append(" AND A.ADM_CLERK = '" + id +"' ");
		}
				
		sql1.append(" GROUP BY A.ADM_CLERK,B.USER_NAME");
		TParm nameParm = new TParm(TJDODBTool.getInstance().select(sql1.toString()));
		if (nameParm.getErrCode() < 0) {
			System.out.println("error1");
			return;
		}
		
		//出院结算SQL语句
		StringBuffer sql2=new StringBuffer();
    	sql2.append("SELECT N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,Y.PAY_CASH,Y.PAY_CHECK,Y.PAY_BANK_CARD,Y.PRE_AMT,Y.MR_NO,A.CTZ1_CODE "
    				+ " FROM "
    				+ " (SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT"
    				+ " FROM ADM_INP A,SYS_OPERATOR B"
    				+ " WHERE A.ADM_CLERK = B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
    				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')"
    				+ " GROUP BY A.ADM_CLERK,B.USER_NAME) N,"
    				+ " (SELECT "
    				+ " CASE WHEN AR_AMT-PAY_BILPAY>0 THEN '03' ELSE '04' END AS TRANSACT_TYPE,"
    				+ " PAY_CASH,PAY_CHECK,PAY_BANK_CARD, AR_AMT-PAY_BILPAY AS PRE_AMT,BIL_IBS_RECPM.CASHIER_CODE,MR_NO"
    				+ " FROM BIL_IBS_RECPM"
    				+ " WHERE CHARGE_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') "
    				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS') ) Y,SYS_PATINFO A"
    				+ " WHERE Y.CASHIER_CODE = N.ADM_CLERK(+) and Y.MR_NO = A.MR_NO");
    	id = this.getValueString("USER_ID");		
 		if (id.length() > 0) {//收费员已选择
    		sql2.append(" AND N.ADM_CLERK = '" + id +"' ");
    	}		  			
 		sql2.append(" ORDER BY N.ADM_CLERK");
 		TParm bilPayParm1 = new TParm(TJDODBTool.getInstance().select(sql2.toString()));
		if (bilPayParm1.getErrCode() < 0) {
			System.out.println("error2");
			return;
		}
				
		double payCash = 0.00;
		double payUCash = 0.00;
		double payCard = 0.00;
		double payUCard = 0.00;
		double payRemark = 0.00;
		double payURemark = 0.00;
		int payCashCount = 0;
		int payUCashCount = 0;
		int payCardCount = 0;
		int payUCardCount = 0;
		int payRemarkCount = 0;
		int payURemarkCount = 0;
		int count = 0;
		int h1 = 0;//爱心救助行动患者数
		int h2 = 0;//爱心希望患者数
		int h3 = 0;//荷福泰心患者数
		int h4 = 0;//明天计划患者数
		
		for(int j = 0; j < nameParm.getCount();j++){
			for(int i = 0;i < bilPayParm.getCount();i++){
				if(bilPayParm.getValue("ADM_CLERK",i).equals(nameParm.getValue("ADM_CLERK", j))){//每位收费员
					if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")
							|| bilPayParm.getValue("TRANSACT_TYPE", i).equals("02")) {// 预交金
						if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")) {//收款
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// 现金操作
								payCash += bilPayParm.getDouble("PRE_AMT", i);
								payCashCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// 支票
								payRemark += bilPayParm.getDouble("PRE_AMT", i);
								payRemarkCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// 刷卡
								payCard += bilPayParm.getDouble("PRE_AMT", i);
								payCardCount += bilPayParm.getInt("NUM", i);
							}
						} else {//退款
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// 现金操作
								payUCash += bilPayParm.getDouble("PRE_AMT", i);
								payUCashCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// 支票
								payURemark += bilPayParm.getDouble("PRE_AMT", i);
								payURemarkCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// 刷卡
								payUCard += bilPayParm.getDouble("PRE_AMT", i);
								payUCardCount += bilPayParm.getInt("NUM", i);
							}

						}//退款end
					}//预交金end
				}//每位收费员end
			}
			
			count = payCashCount+payRemarkCount+payCardCount+payUCashCount+payURemarkCount+payUCardCount;//预交金笔数

			returnParm.addData("USER_ID", nameParm.getValue("ADM_CLERK", j));			
			returnParm.addData("USER_NAME", nameParm.getValue("PAT_NAME", j));
			returnParm.addData("PAT_COUNT", nameParm.getValue("PAT_COUNT", j));
			returnParm.addData("COUNT1", count);
			returnParm.addData("CASH_AMT1", df1.format(payCash));
			returnParm.addData("CARD_AMT1", df1.format(payCard));
			returnParm.addData("REMARK_AMT1", df1.format(payRemark));
						
			payCash = 0.00;
			payUCash = 0.00;
			payCard = 0.00;
			payUCard = 0.00;
			payRemark = 0.00;
			payURemark = 0.00;
			payCashCount = 0;
			payUCashCount = 0;
			payCardCount = 0;
			payUCardCount = 0;
			payRemarkCount = 0;
			payURemarkCount = 0;
			count = 0;

			for(int i = 0;i < bilPayParm1.getCount();i++){
				if (bilPayParm1.getDouble("PRE_AMT", i) == 0) {// 数据冲销不去累计
					continue;
				}
				if(bilPayParm1.getValue("ADM_CLERK", i).equals(nameParm.getValue("ADM_CLERK", j))){//每位收费员						
					if (bilPayParm1.getValue("TRANSACT_TYPE", i).equals("03")) {// 补款
						if (bilPayParm1.getDouble("PAY_CASH", i)!=0) {// 现金
							payCash += bilPayParm1.getDouble("PRE_AMT", i);
							payCashCount++;
						}
						if (bilPayParm1.getDouble("PAY_CHECK", i)!=0) {// 支票
							payRemark += bilPayParm1.getDouble("PRE_AMT", i);
							payRemarkCount++;
						}
						if (bilPayParm1.getDouble("PAY_BANK_CARD", i)!=0) {// 刷卡
							payCard += bilPayParm1.getDouble("PRE_AMT", i);
							payCardCount++;
						}
					}//补款end
					else {// 退款
						if (bilPayParm1.getDouble("PAY_CASH", i)!=0) {// 现金
							payUCash += bilPayParm1.getDouble("PRE_AMT", i);
							payUCashCount++;
						}
						if (bilPayParm1.getDouble("PAY_CHECK", i)!=0) {// 支票
							payURemark += bilPayParm1.getDouble("PRE_AMT", i);
							payURemarkCount++;
						}
						if (bilPayParm1.getDouble("PAY_BANK_CARD", i)!=0) {// 刷卡
							payUCard += bilPayParm1.getDouble("PRE_AMT", i);
							payUCardCount++;
						}
					}//退款end
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("35")){//爱心救助行动
						h1++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("30")){//爱心希望
						h2++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("67")){//荷福泰心
						h3++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("44")){//明天计划
						h4++;
					}
				}//每位收费员end
			}
			count = payCashCount+payRemarkCount+payCardCount+payUCashCount+payURemarkCount+payUCardCount;
			returnParm.addData("COUNT2", count);
			returnParm.addData("CASH_AMT2", df1.format(payCash));
			returnParm.addData("CARD_AMT2", df1.format(payCard));
			returnParm.addData("REMARK_AMT2", df1.format(payRemark));
			returnParm.addData("CASH_AMT3", df1.format(payUCash));
			returnParm.addData("CARD_AMT3", df1.format(payUCard));
			returnParm.addData("REMARK_AMT3", df1.format(payURemark));
			returnParm.addData("AXJZXD", h1);
			returnParm.addData("AXXW", h2);
			returnParm.addData("HFTX", h3);
			returnParm.addData("MTJH", h4);
			
			payCash = 0.00;
			payUCash = 0.00;
			payCard = 0.00;
			payUCard = 0.00;
			payRemark = 0.00;
			payURemark = 0.00;
			payCashCount = 0;
			payUCashCount = 0;
			payCardCount = 0;
			payUCardCount = 0;
			payRemarkCount = 0;
			payURemarkCount = 0;
			count = 0;
			h1 = 0;
			h2 = 0;
			h3 = 0;
			h4 = 0;
			
		}		
		table.setParmValue(returnParm);		
    }
              
}

