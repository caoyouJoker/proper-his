package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p> Title:  </p>
 * 
 * <p> Description: 门诊收入明细查询 </p>
 * 
 * <p> Copyright: Copyright (c) 2009 </p>
 * 
 * <p> Company: </p>
 * 
 * @author wu 2012-7-25下午16:59:55
 * @version 1.0
 */
public class OPBIncomeDetailControl extends TControl {
	private TTable table;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * 初始化页面
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");	
		// 初始化查询区间
		this.setValue("E_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
        String sql =//wanglong modify 20150114
                "SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT, "
                        + "       C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT "
                        + "  FROM OPD_ORDER A, SYS_DEPT B, SYS_DEPT C, "
                        + "       (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE') D "
                        + " WHERE A.RECEIPT_NO IN "
                        + "           (SELECT A.RECEIPT_NO "
                        + "              FROM BIL_OPB_RECP A "
                        + "             WHERE A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "               AND A.ADM_TYPE <> 'H' "
                        + "               AND A.RESET_RECEIPT_NO IS NULL "
                        + "               AND A.AR_AMT > 0) "
                        + "   AND A.DEPT_CODE = B.DEPT_CODE "
                        + "   AND A.EXEC_DEPT_CODE = C.DEPT_CODE "
                        + "   AND A.REXP_CODE = D.ID "
                        + "GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC "
                        + "UNION ALL "
                        + "SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT, "
                        + "       C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT "
                        + "  FROM OPD_ORDER_HISTORY_NEW A, SYS_DEPT B, SYS_DEPT C, "
                        + "       (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE') D "
                        // ====start===== add by kangy 20170308
                        + ",(SELECT A.RECEIPT_NO,A.CASE_NO,A.MR_NO, "
                        + "B.ACCOUNT_DATE_SUM AS ACCOUNT_DATE,B.ACCOUNT_SEQ,B.CASHIER_CODE "
                        +" FROM BIL_OPB_RECP A, "
                        +" (SELECT RECEIPT_NO,ACCOUNT_SEQ, "
                         +" TO_CHAR (ACCOUNT_DATE, 'YYYYMMDD') ACCOUNT_DATE, "
                         +" ACCOUNT_DATE AS ACCOUNT_DATE_SUM, CASHIER_CODE "
                         +"   FROM BIL_OPB_RECP "
                         +"  WHERE ACCOUNT_DATE BETWEEN TO_DATE ('@','YYYYMMDDHH24MISS') "
                         +"                            AND TO_DATE ('#','YYYYMMDDHH24MISS') "
                         +"        AND TOT_AMT < 0) B "
                +"   WHERE     A.RESET_RECEIPT_NO = B.RECEIPT_NO "
                  +"       AND A.RESET_RECEIPT_NO IS NOT NULL) Q" 
               // ====end===== add by kangy 20170308
                        + " WHERE A.RECEIPT_NO IN "
                        + "           (SELECT A.RECEIPT_NO  "
                        + "              FROM BIL_OPB_RECP A "
                        + "             WHERE A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "               AND A.ADM_TYPE <> 'H' "
                        + "               AND A.RESET_RECEIPT_NO IS NOT NULL "
                        + "               AND A.AR_AMT > 0) "
                        + "   AND A.DEPT_CODE = B.DEPT_CODE "
                        + "   AND A.EXEC_DEPT_CODE = C.DEPT_CODE "
                        + "   AND A.REXP_CODE = D.ID "
                        // ====start===== add by kangy 20170308
                        +" AND A.CASE_NO=Q.CASE_NO(+) "
                        +" AND A.RECEIPT_NO=Q.RECEIPT_NO(+) "
               			// ====end===== add by kangy 20170308
                        + "GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC "
                        + "UNION ALL "
                        + "SELECT TO_CHAR( B.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, D.DEPT_CHN_DESC DEPT, "
                        + "       E.DEPT_CHN_DESC EXE_DEPT, F.CHN_DESC, SUM(-B.AR_AMT) AR_AMT "
                        + "  FROM BIL_OPB_RECP A, OPD_ORDER_HISTORY_NEW B, BIL_OPB_RECP C, SYS_DEPT D, SYS_DEPT E, "
                        + "       (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE') F "
                     // ====start===== add by kangy 20170308
                     + ",(SELECT A.RECEIPT_NO,A.CASE_NO,A.MR_NO, "
                     + "B.ACCOUNT_DATE_SUM AS ACCOUNT_DATE,B.ACCOUNT_SEQ,B.CASHIER_CODE "
                     +" FROM BIL_OPB_RECP A, "
                     +" (SELECT RECEIPT_NO,ACCOUNT_SEQ, "
                      +" TO_CHAR (ACCOUNT_DATE, 'YYYYMMDD') ACCOUNT_DATE, "
                      +" ACCOUNT_DATE AS ACCOUNT_DATE_SUM, CASHIER_CODE "
                      +"   FROM BIL_OPB_RECP "
                      +"  WHERE ACCOUNT_DATE BETWEEN TO_DATE ('@','YYYYMMDDHH24MISS') "
                      +"                            AND TO_DATE ('#','YYYYMMDDHH24MISS') "
                      +"        AND TOT_AMT < 0) B "
             +"   WHERE     A.RESET_RECEIPT_NO = B.RECEIPT_NO "
               +"       AND A.RESET_RECEIPT_NO IS NOT NULL) Q " 
            // ====end===== add by kangy 20170308"
                        + " WHERE A.RESET_RECEIPT_NO IS NULL "
                        + "   AND A.AR_AMT < 0 "
                        + "   AND A.RECEIPT_NO = C.RESET_RECEIPT_NO "
                        + "   AND A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "   AND C.RECEIPT_NO = B.RECEIPT_NO "
                        + "   AND B.DEPT_CODE = D.DEPT_CODE "
                        + "   AND B.EXEC_DEPT_CODE = E.DEPT_CODE "
                        + "   AND B.REXP_CODE = F.ID "
                        // ====start===== add by kangy 20170308"
                        + " AND B.CASE_NO=Q.CASE_NO(+) "
                        + " AND B.RECEIPT_NO=Q.RECEIPT_NO(+) "
                        // ====end===== add by kangy 20170308"
                        + "GROUP BY B.BILL_DATE, D.DEPT_CHN_DESC, E.DEPT_CHN_DESC, F.CHN_DESC "
                        + "UNION ALL "
                        + "SELECT A.PRINT_DATE BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC, '挂号费' CHN_DESC, "
                        + "       SUM(A.REG_FEE_REAL) AR_AMT "
                        + "  FROM SYS_DEPT B, REG_PATADM C, "
                        + "       (SELECT TO_CHAR( PRINT_DATE, 'YYYY-MM-DD') PRINT_DATE, CASE_NO, "
                        + "               SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL "
                        + "          FROM BIL_REG_RECP "
                        + "         WHERE ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "        GROUP BY PRINT_DATE, CASE_NO) A "
                        + " WHERE A.CASE_NO = C.CASE_NO "
                        + "   AND C.REALDEPT_CODE = B.DEPT_CODE "
                        + "GROUP BY A.PRINT_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC "
                        + "UNION ALL "
                        + "SELECT A.PRINT_DATE BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC, '诊察费' CHN_DESC, "
                        + "       SUM(A.CLINIC_FEE_REAL) AR_AMT "
                        + "  FROM SYS_DEPT B, REG_PATADM C, "
                        + "       (SELECT TO_CHAR( PRINT_DATE, 'YYYY-MM-DD') PRINT_DATE, CASE_NO, "
                        + "               SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL "
                        + "          FROM BIL_REG_RECP "
                        + "         WHERE ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "        GROUP BY PRINT_DATE, CASE_NO) A "
                        + " WHERE A.CASE_NO = C.CASE_NO " + "   AND C.REALDEPT_CODE = B.DEPT_CODE "
                        + "GROUP BY A.PRINT_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC "
                        + "ORDER BY BILL_DATE";
        sql = sql.replaceAll("@", date_s);//wanglong modify 20150114
        sql = sql.replaceAll("#", date_e);
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		System.out.println(sql);
		if(result.getErrCode() < 0){
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门诊收入明细表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}
}
