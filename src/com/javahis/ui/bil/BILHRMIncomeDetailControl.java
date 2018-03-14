package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 
 * </p>
 * 
 * <p>
 * Description: 健检开单执行科室收入统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-7-25下午16:59:55
 * @version 1.0
 */
public class BILHRMIncomeDetailControl extends TControl {
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
        String sql =
                "SELECT A.BILL_DATE, A.DEPT_CODE, A.EXEC_DEPT_CODE, B.DEPT_CHN_DESC AS DEPT, "
                        + "       C.DEPT_CHN_DESC AS DEPT_CHN_DESC, A.REXP_CODE, D.CHN_DESC, SUM(A.AR_AMT) AS AR_AMT "
                        + "  FROM (SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CODE, B.EXEC_DEPT_CODE, B.REXP_CODE, B.AR_AMT "
                        + "          FROM BIL_OPB_RECP A, HRM_ORDER B "
                        + "         WHERE A.ADM_TYPE = 'H' "
                        + "           AND A.RESET_RECEIPT_NO IS NULL "//正常收费票据
                        + "           AND A.BILL_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') AND TO_DATE( '@', 'YYYYMMDDHH24MISS') "
                        + "           AND A.RECEIPT_NO = B.RECEIPT_NO "
//                        + "           AND A.CASE_NO = B.CONTRACT_CODE "
                        + "        UNION ALL "
                        + "        SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CODE, B.EXEC_DEPT_CODE, B.REXP_CODE, B.AR_AMT "
                        + "          FROM BIL_OPB_RECP A, HRM_ORDER_HISTORY B "
                        + "         WHERE A.ADM_TYPE = 'H' "
                        + "           AND A.RESET_RECEIPT_NO IS NOT NULL "//退费票据对应的正常收费票据
                        + "           AND A.BILL_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') AND TO_DATE( '@', 'YYYYMMDDHH24MISS') "
                        + "           AND A.RECEIPT_NO = B.RECEIPT_NO "
//                        + "           AND A.CASE_NO = B.CONTRACT_CODE "
                        + "        UNION ALL "
                        + "        SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, C.DEPT_CODE, C.EXEC_DEPT_CODE, C.REXP_CODE, -1 * C.AR_AMT AS AR_AMT "
                        + "          FROM BIL_OPB_RECP A, BIL_OPB_RECP B, HRM_ORDER_HISTORY C "
                        + "         WHERE A.ADM_TYPE = 'H' "
                        + "           AND A.AR_AMT < 0 "
                        + "           AND A.BILL_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') AND TO_DATE( '@', 'YYYYMMDDHH24MISS') "
                        + "           AND A.ADM_TYPE = B.ADM_TYPE "
                        + "           AND A.RECEIPT_NO = B.RESET_RECEIPT_NO "//自连接，查出退费票据
                        + "           AND B.RECEIPT_NO = C.RECEIPT_NO "
//                        + "           AND B.CASE_NO = C.CONTRACT_CODE "
                        + "       ) A, SYS_DEPT B, SYS_DEPT C, SYS_DICTIONARY D "
                        + " WHERE A.DEPT_CODE = B.DEPT_CODE "
                        + "   AND A.EXEC_DEPT_CODE = C.DEPT_CODE "
                        + "   AND A.REXP_CODE = D.ID "
                        + "   AND D.GROUP_ID = 'SYS_CHARGE' "
                        + "GROUP BY A.BILL_DATE, A.DEPT_CODE, A.EXEC_DEPT_CODE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, A.REXP_CODE, D.CHN_DESC "
                        + "ORDER BY A.BILL_DATE";
        sql = sql.replaceAll("#", date_s);
        sql = sql.replaceAll("@", date_e);
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return;
        }
        for (int i = result.getCount() - 1; i >= 0; i--) {
            if (result.getDouble("AR_AMT", i) == 0) {
                result.removeRow(i);
            }
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
