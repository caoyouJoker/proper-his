package com.javahis.ui.sta;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: STA_IN_12急诊抢救明细报表
 * </p>
 * 
 * <p>
 * Description: 急诊抢救明细报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl 2012.06.21
 * @version 1.0
 */
public class STAIn_12Control extends TControl {
	private TParm tableParm; // 单档表格的TParm

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initUI();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * 初始化界面
	 */
	public void initUI() {
		this.setValue("DATE_S", SystemTool.getInstance().getDate());
		this.setValue("DATE_E", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("ORDER_CODE", "J0000008");
		//医嘱名称的sql
		String sqlDesc = "SELECT ORDER_DESC FROM SYS_FEE WHERE "
				+ " ORDER_CODE = 'J0000008'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlDesc));
		this.setValue("ORDER_DESC",result.getValue("ORDER_DESC", 0));
		//初始化医嘱的textField
		TParm parm = new TParm();
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x",parm);
        //textfield接收回传值
		callFunction("UI|ORDER_CODE|addEventListener" ,
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	 public void popReturn(String tag, Object obj) {
	        TParm parmrtn = (TParm) obj;
	        this.setValue("ORDER_CODE", parmrtn.getValue("ORDER_CODE"));
	        this.setValue("ORDER_DESC", parmrtn.getValue("ORDER_DESC"));
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initUI();
		this.setValue("SUN_TOT", "0");
		this.callFunction("UI|TABLE|removeRowAll");// 清空表格
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (this.getValueString("DEPT_CODE").equals("")) {
			this.messageBox("科室不能为空");
			return;
		}
		String startDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_S")), "yyyyMMdd");
		String endDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_E")), "yyyyMMdd");
		if(startDate.compareTo(endDate)>0){
			this.messageBox("起始日期大于结束日期");
			return;
		}
		String regionCode = "";// 区域ID
		if (!this.getValue("REGION_CODE").equals("")) {
			regionCode = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		}
		String dept_code = ""; // 科室
		if (!this.getValue("DEPT_CODE").equals("")) {
			dept_code = " AND A.DEPT_CODE= '" + this.getValueString("DEPT_CODE")
					+ "' ";
		}
		String dateStr = "";
		// 取开立时间
		if (!startDate.equals("") && !endDate.equals("")) {
			dateStr = " AND A.ORDER_DATE BETWEEN TO_DATE('" + startDate
					+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ endDate + "235959','yyyyMMddHH24miss') ";
		}
		//医嘱
		String order_code = ""; 
		if (!this.getValue("ORDER_CODE").equals("")) {
			order_code = " AND A.ORDER_CODE= '" + this.getValueString("ORDER_CODE")
					+ "' ";
		}
		String sql = " SELECT A.MR_NO, B.PAT_NAME, A.ORDER_DESC, A.ORDER_DATE,E.DEPT_CHN_DESC AS DEPT_CODE,"
				+ " F.USER_NAME AS DR_CODE, D.ICD_CHN_DESC,'' AS BED_NO,'' AS CODE_STATUS "
				+ " FROM OPD_ORDER A, SYS_PATINFO B, OPD_DIAGREC C, SYS_DIAGNOSIS D,SYS_DEPT E,SYS_OPERATOR F,SYS_FEE G "
				+ " WHERE A.MR_NO = B.MR_NO "
				+ "  AND C.MAIN_DIAG_FLG ='Y' "
				+ "  AND A.CASE_NO = C.CASE_NO "
				+ " AND C.ICD_CODE = D.ICD_CODE "
				+ "  AND C.ICD_TYPE = D.ICD_TYPE "
				+ "  AND A.DEPT_CODE = E.DEPT_CODE "
				+ "  AND A.DR_CODE = F.USER_ID "
				+ "  AND A.ORDER_CODE = G.ORDER_CODE "
				+ regionCode
				+ dept_code
				+ dateStr+order_code + " ORDER BY A.ORDER_DATE";
		tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = 0;
		if (tableParm.getCount() <= 0) {
			this.messageBox("查无数据");
			this.callFunction("UI|TABLE|removeRowAll");// 清空表格
			// 合计笔数
			this.setValue("SUM_TOT", count + "");
			return;
		}
		count = tableParm.getCount();
		// 合计笔数
		this.setValue("SUM_TOT", count + "");
		// 表格赋值
		this.callFunction("UI|TABLE|setParmValue", tableParm);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "急诊抢救明细统计报表");
		}
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		TTable table = ((TTable) this.getComponent("TABLE"));
		table.acceptText();
		TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		if (rowCount < 1) {
			this.messageBox("请先查询数据!");
			return;
		}
		TParm T1 = new TParm(); // 表格数据
		for (int i = 0; i < rowCount; i++) {
			parm.setData("ORDER_DATE", i, StringTool.getString(
					parm.getTimestamp("ORDER_DATE", i), "yyyy/MM/dd HH:mm"));
			T1.addRowData(parm, i);
		}
		T1.setCount(rowCount);
		String[] chage = table.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_S")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_E")), "yyyy/MM/dd");
		TParm printParm = new TParm();// 给报表传参的TParm
		printParm.setData("START_DATE", "TEXT", sDate);
		printParm.setData("END_DATE", "TEXT", eDate);
		printParm.setData("OPT_USER", "TEXT", "制表人:" + Operator.getName());
		printParm.setData("TABLE", T1.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_12.jhw",
				printParm);
	}
}
