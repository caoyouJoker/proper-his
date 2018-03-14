package com.javahis.ui.bil;

import java.sql.Timestamp;

import com.dongyang.control.TControl;

import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 在院患者预交金欠费情况统计表
 * </p>
 * 
 * <p>
 * Description: 在院患者预交金欠费情况统计表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author yanmm 2017/7/12
 * @version 1.0
 */
public class BILInHoopTotPayArrearsControl extends TControl {
	private static TTable mainTable;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		mainTable = (TTable) getComponent("TABLE");
		super.onInit();
		initUI();
	}

	public void initUI() {
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("START_DATE", StringTool.rollDate(date, -15).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/')
				+ " 23:59:59");

	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// mainTable.removeRowAll();// 清除主表
		mainTable.setParmValue(new TParm());
		TParm bilParm = new TParm();
		TParm tableParm = new TParm();
		String sTime = this.getText("START_DATE").replaceAll("/", "");
		String eTime = this.getText("END_DATE").replaceAll("/", "");
		double p = 0.00;
		String sql = "SELECT A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME,"
				+ "A.BED_NO,A.MR_NO,B.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT, "
				+ "CASE WHEN C.PRE_AMT IS NULL THEN 0.00 ELSE C.PRE_AMT END - SUM (B.TOT_AMT) TOT_AMT "
				+ "FROM ADM_INP A,SYS_DEPT D,SYS_OPERATOR O,SYS_PATINFO B,SYS_STATION S,SYS_CTZ Z, IBS_ORDD B,"
				+ "(  SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT FROM BIL_PAY C WHERE C.REFUND_FLG = 'N' "
				+ "AND C.TRANSACT_TYPE IN ('01', '03', '04') AND CHARGE_DATE BETWEEN TO_DATE ('"
				+ sTime
				+ "','YYYY/MM/DD HH24:MI:SS') "
				+ "AND TO_DATE ('"
				+ eTime
				+ "','YYYY/MM/DD HH24:MI:SS') GROUP BY CASE_NO) C  "
				+ "WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE  AND A.VS_DR_CODE = O.USER_ID  "
				+ "AND A.CTZ1_CODE = Z.CTZ_CODE  AND A.MR_NO = B.MR_NO  AND A.STATION_CODE = S.STATION_CODE  "
				+ "AND A.CASE_NO = C.CASE_NO(+) AND A.IN_DATE BETWEEN TO_DATE ('"
				+ sTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') "
				+ "AND TO_DATE ('"
				+ eTime
				+ "','YYYY/MM/DD HH24:MI:SS') "
				+ " AND A.DS_DATE IS NULL "
				+ "GROUP BY C.PRE_AMT, A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME,A.BED_NO,A.MR_NO,"
				+ "B.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT ORDER BY D.DEPT_CHN_DESC ";
	//	System.out.println("sql---------" + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int row = 0;
		int num = 0;
		if (result.getCount() >= 1) {
			for (int i = 0; i < result.getCount(); i++) {
				if(result.getDouble("TOT_AMT", i)<0){
				bilParm.setData("DEPT_CHN_DESC",i,result.getValue("DEPT_CHN_DESC", i));
				bilParm.setData("STATION_DESC",i,result.getValue("STATION_DESC", i));
				bilParm.setData("USER_NAME",i,result.getValue("USER_NAME", i));
				bilParm.setData("CTZ_DESC",i,result.getValue("CTZ_DESC", i));
				bilParm.setData("BED_NO",i,result.getValue("BED_NO", i));
				bilParm.setData("MR_NO",i,result.getValue("MR_NO", i));
				bilParm.setData("PAT_NAME",i, result.getValue("PAT_NAME", i));
				bilParm.setData("TOT_AMT",i,result.getDouble("TOT_AMT", i));// 欠费金额
				p += result.getDouble("TOT_AMT", i);
				bilParm.setData("MES_COUNT",i, result.getValue("MES_COUNT", i));
				num ++;
				}
			}
		} else {
			tableParm.addRowData(bilParm, row);
		}
		// 合计  DEPT_CHN_DESC;STATION_DESC;USER_NAME;CTZ_DESC;BED_NO;MR_NO;PAT_NAME;TOT_AMT;MES_COUNT
		bilParm.addData("DEPT_CHN_DESC","合计:");
		bilParm.addData("STATION_DESC",  " ");
		bilParm.addData("USER_NAME"," ");
		bilParm.addData("CTZ_DESC", " ");
		bilParm.addData("BED_NO", " ");
		bilParm.addData("MR_NO", " ");
		bilParm.addData("PAT_NAME",  num);
		bilParm.addData("TOT_AMT",  p);
		bilParm.addData("MES_COUNT",  " ");
		bilParm.setCount(result.getCount() + 1);
		if (result.getCount() < 0) {
			this.messageBox("没有要查询的数据");
			return;
		}
		mainTable.setParmValue(bilParm);
	}

	/**
	 * 导出Excel表格
	 */
	public void onExcel() {
		TTable mainTable = getTable("TABLE");
		if (mainTable.getRowCount() <= 0) {
			messageBox("无导出资料");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(mainTable, "在院患者预交金欠费情况统计表");
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		getTable("TABLE").removeRowAll();
		onInit();
	}

	/**
	 * 取得Table控件
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}

}
