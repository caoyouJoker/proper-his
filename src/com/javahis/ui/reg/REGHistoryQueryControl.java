package com.javahis.ui.reg;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: 挂号历史查询
 * </p>
 * 
 * <p>
 * Description: 挂号历史查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl 2009.08.28
 * @version 1.0
 */
public class REGHistoryQueryControl extends TControl {
	public void onInit() {
		super.onInit();
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		initPage();
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

	}

	/**
	 * 行单击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		setPageValue();
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {

		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("CLINICTYPE_CODE", "");
		setValue("DEPT_CODE", "");
		// 默认区域
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DR_CODE", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		this.callFunction("UI|Table|removeRowAll");
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		print();
	}

	/**
	 * 调用报表打印预览界面
	 */
	private void print() {
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 1) {
			this.messageBox("先查询数据!");
			return;
		}
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd hh:mm:ss");
		TParm printData = this.getPrintDate(startTime, endTime);
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("S_TIME");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("E_TIME");
		TParm parm = new TParm();
		// ========pangben modify 20110329 start,fuxin modify 20120306
		String region = ((TTable) this.getComponent("Table")).getParmValue()
				.getRow(0).getValue("REGION_CHN_ABN");
		parm.setData("TITLE", "TEXT", (this.getValue("REGION_CODE") != null
				&& !this.getValue("REGION_CODE").equals("") ? region : "所有医院")
				+ "挂号历史信息报表");
		// ========pangben modify 20110329 stop
		parm.setData("S_DATE", "TEXT", sDate);
		parm.setData("E_DATE", "TEXT", eDate);
		parm.setData("OPT_USER", "TEXT",Operator.getName());
		parm.setData("OPT_DATE", "TEXT", sysDate);
		parm.setData("historyQuerytable", printData.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGHistoryQuery.jhw",
				parm);

	}

	/**
	 * 整理打印数据
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @return TParm
	 */
	private TParm getPrintDate(String startTime, String endTime) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String clinicTypeCodeWhere = "";
		if (getValue("CLINICTYPE_CODE").toString().length() != 0)
			clinicTypeCodeWhere = " AND A.CLINICTYPE_CODE = '"
					+ getValue("CLINICTYPE_CODE") + "'  ";
		String deptCodeWhere = "";
		if (getValue("DEPT_CODE").toString().length() != 0)
			deptCodeWhere = " AND A.REALDEPT_CODE = '" + getValue("DEPT_CODE")
					+ "'  ";
		String drCodeWhere = "";
		if (getValue("DR_CODE").toString().length() != 0)
			drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE")
					+ "'  ";
		String mrNoWhere = "";
		if (getValue("MR_NO").toString().trim().length() != 0)
			mrNoWhere = " AND A.MR_NO = '" + getValue("MR_NO") + "'  ";
		// ================pangben modify 20110408 start
		String reqion = "";
		if (this.getValueString("REGION_CODE").length() != 0)
			reqion = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		// ================pangben modify 20110408 stop,fuxin modify 20120306
		//=================yanjing modify 20130624 添加退挂人、退挂日期字段
		String sql = " SELECT G.USER_NAME AS REGCAN_USER,A.REGCAN_DATE, H.REGION_CHN_ABN,A.ADM_DATE, A.MR_NO,C.PAT_NAME, A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE,"
				+ "        A.QUE_NO, A.CTZ1_CODE, B.AR_AMT, D.CLINICTYPE_DESC,"
				+ "        E.USER_NAME, F.DEPT_ABS_DESC,G.CTZ_DESC,A.REGION_CODE,"
				+ "		   I.CHN_DESC AS SEX,C.BIRTH_DATE,C.TEL_HOME," +
						" CASE " +
						" WHEN " +
						" A.SESSION_CODE = '01' " +
						" THEN " +
						" '上午' " +
						" WHEN " +
						" A.SESSION_CODE = '02' " +
						" THEN " +
						" '下午' " +
						" END SESSION_CODE "
				+ "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,REG_CLINICTYPE D,"
				+ "        SYS_OPERATOR E,SYS_DEPT F,SYS_CTZ G,SYS_REGION H,SYS_DICTIONARY I,SYS_OPERATOR G "
				+ "  WHERE B.BILL_DATE BETWEEN TO_DATE ('"  //modify by huangtt 20141226 adm_date 改为bill_date
				+ startTime
				+ "000000"
				+ "', 'yyyyMMddHH24miss') "
				+ "                       AND TO_DATE ('"
				+ endTime
				+ "235959"
				+ "', 'yyyyMMddHH24miss') "
				+ "    AND A.REGCAN_USER = G.USER_ID(+) "
				+ "    AND A.MR_NO = C.MR_NO "
				+ "    AND A.CASE_NO = B.CASE_NO(+) "
				+ "    AND A.REALDEPT_CODE = F.DEPT_CODE "
				+ "    AND A.REGION_CODE = H.REGION_CODE(+) "
				+ // =========pangben modify 20110408
				"    AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE "
				+ clinicTypeCodeWhere
				+ deptCodeWhere
				+ drCodeWhere
				+ mrNoWhere
				+ reqion
				+ // ======pangben modify 20110325
				"    AND A.REALDR_CODE = E.USER_ID   AND I.GROUP_ID = 'SYS_SEX'  AND I.ID = C.SEX_CODE"
				+ "    AND A.CTZ1_CODE = G.CTZ_CODE ORDER BY H.REGION_CHN_ABN,A.ADM_DATE DESC"; // =====fuxin
																								// modify
																								// 20120306
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (selParm.getCount("MR_NO") < 1) {
			this.messageBox("查无数据");
			this.initPage();
			return selParm;
		}
		// ==========pangben modify 20110425 start 累计
		double sumAramt = 0.00;
		// ==========pangben modify 20110425 stop
		TParm endDate = new TParm();
		int count = selParm.getCount("MR_NO");
		// ADM_DATE;MR_NO;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		for (int i = 0; i < count; i++) {
			// =============pangben modify 20110408 start 在打印的报表中添加区域
			String reqionTemp = selParm.getValue("REGION_CHN_ABN", i); // ====fuxin
																		// modify
																		// 20120306
			// =============pangben modify 20110408 stop
			Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
			String admDateStr = StringTool.getString(admDate, "yyyy/MM/dd");
			String mrNo = selParm.getValue("MR_NO", i);
			String patName = selParm.getValue("PAT_NAME", i);
			String sex_code = selParm.getValue("SEX", i);
			String tel_home = selParm.getValue("TEL_HOME", i);
			Timestamp birth_date = selParm.getTimestamp("BIRTH_DATE", i);
			String birth_datestr = StringTool.getString(birth_date,
					"yyyy/MM/dd");
			String clinicDesc = selParm.getValue("CLINICTYPE_DESC", i);
			String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
			String useName = selParm.getValue("USER_NAME", i);
			String session_code = selParm.getValue("SESSION_CODE", i);
			int queNo = selParm.getInt("QUE_NO", i);
			String ctzDesc = selParm.getValue("CTZ_DESC", i);
			double ar_amt = selParm.getDouble("AR_AMT", i);
			//yanjing 20130705 添加退挂人、退挂日期
			String regcan_user = selParm.getValue("REGCAN_USER", i);
			String regcan_date = selParm.getValue("REGCAN_DATE", i).toString().replace("-", "/");
			if(!regcan_date.equals("")){
				 regcan_date = regcan_date.substring(0, 10);
			}
		//yanjing end
			// ==========pangben modify 20110425 start
			sumAramt += StringTool.round(ar_amt, 2);
			// ==========pangben modify 20110425 stop
			endDate.addData("REGION_CHN_ABN", reqionTemp);// =============pangben
															// modify
															// 20110408,fuxin
															// modify 20120306
			endDate.addData("ADM_DATE", admDateStr);
			endDate.addData("MR_NO", mrNo);
			endDate.addData("PAT_NAME", patName);
			endDate.addData("SEX", sex_code);
			endDate.addData("TEL_HOME", tel_home);
			endDate.addData("BIRTH_DATE", birth_datestr);
			endDate.addData("CLINICTYPE_DESC", clinicDesc);
			endDate.addData("DEPT_ABS_DESC", deptDesc);
			endDate.addData("USER_NAME", useName);
			endDate.addData("QUE_NO", queNo);
			endDate.addData("CTZ_DESC", ctzDesc);
			endDate.addData("SESSION_CODE", session_code);
			endDate.addData("AR_AMT", df.format(ar_amt));
			endDate.addData("sumAramt", df.format(ar_amt));
			endDate.addData("REGCAN_USER",regcan_user);//yanjing 20130624添加退挂人员
			endDate.addData("REGCAN_DATE",regcan_date);//yanjing 20130624添加退挂日期
		}

		// =============pangben modify 20110408 start
		endDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN"); // fuxin modify
																// 20120306
		// =============pangben modify 20110408 stop
		endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "MR_NO");
		endDate.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "SEX");
		endDate.addData("SYSTEM", "COLUMNS", "TEL_HOME");
		endDate.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "QUE_NO");
		endDate.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		//yanjing 20130705 添加退挂人、退挂日期
		endDate.addData("SYSTEM", "COLUMNS", "REGCAN_USER");
		endDate.addData("SYSTEM", "COLUMNS", "REGCAN_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
		
		// ==========pangben modify 20110425 start
		endDate.setData("REGION_CHN_ABN", count, "总计:");
		endDate.setData("ADM_DATE", count, "");
		endDate.setData("MR_NO", count, "");
		endDate.setData("PAT_NAME", count, "");
		endDate.setData("SEX", count, "");
		endDate.setData("TEL_HOME", count, "");
		endDate.setData("BIRTH_DATE", count, "");
		endDate.setData("CLINICTYPE_DESC", count, "");
		endDate.setData("DEPT_ABS_DESC", count, "");
		endDate.setData("USER_NAME", count, "");
		endDate.setData("QUE_NO", count, "");
		endDate.setData("CTZ_DESC", count, "");
		endDate.setData("SESSION_CODE", count, "");
		endDate.setData("AR_AMT", count, df.format(sumAramt));
		endDate.setData("OPT_USER", count, Operator.getName());
		endDate.setCount(endDate.getCount("MR_NO"));
		// ==========pangben modify 20110425 start
		this.callFunction("UI|Table|setParmValue", endDate);
		return endDate;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		TParm printData = this.getPrintDate(startTime, endTime);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "挂号历史数据报表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();

	}

	/**
	 * 点选grid数据给界面翻值
	 */
	public void setPageValue() {
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
		}
		TParm tableParm = table.getParmValue();
		String clinicTypeCode = tableParm.getValue("CLINICTYPE_CODE", selRow);
		String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
		String drCode = tableParm.getValue("REALDR_CODE", selRow);
		String mrNo = tableParm.getValue("MR_NO", selRow);
		String patName = tableParm.getValue("PAT_NAME", selRow);
		String region_code = tableParm.getValue("REGION_CODE", selRow);// ==pangben
																		// modify
																		// 20110413
		setValue("CLINICTYPE_CODE", clinicTypeCode);
		setValue("DEPT_CODE", deptCode);
		setValue("DR_CODE", drCode);
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", patName);
		setValue("REGION_CODE", region_code);// ==pangben modify 20110413
	}

	/**
	 * 查询病案号 ===zhangp 20120326
	 */
	public void onQueryMrNo() {
		String mrNo = getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
		setValue("MR_NO", mrNo);
	}

}
