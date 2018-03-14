package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 住院医疗费拒付明细表
 * </p>
 * 
 * <p>
 * Description:住院医疗费拒付明细表
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
 * @author lim
 * @version 1.0
 */
public class INSMedRefuseDetailControl extends TControl {

	private String nhi_no;

	private TTable tTable;

	private TTabbedPane tablePanel;

	/**
	 * 初始化
	 */
	public void onInit() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		// 入院开始时间
		setValue("START_DATE", yesterday);
		// 入院结束时间
		setValue("END_DATE", SystemTool.getInstance().getDate());
		this.tTable = (TTable) getComponent("TTABLE");
		this.tablePanel = ((TTabbedPane) getComponent("TTABBEDPANEL"));
		TParm result2 = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
		this.nhi_no = result2.getValue("NHI_NO", 0);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String beginTime = this.getValueString("START_DATE");
		String endTime = this.getValueString("END_DATE");
		String type = this.getValueString("TYPE");

		if (!checkInput(beginTime, endTime, type)) {
			return;
		}

		String[] beginTimeArray = beginTime.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

		String[] endTimeArray = endTime.split("-");
		String newEndTime = endTimeArray[0].concat(endTimeArray[1]);

		if (Integer.parseInt(newBeginTime) > Integer.parseInt(newEndTime)) {
			messageBox("查询日期不符");
			return;
		}

		if ("CJ".equals(type)) {// 城居
			this.tTable.setParmMap("CODE");
			TParm parm = new TParm();
			parm.setData("PIPELINE", "DataDown_czyd");
			parm.setData("PLOT_TYPE", "F");

			parm.addData("HOSP_NHI_NO", this.nhi_no);
			parm.addData("START_CODE", newBeginTime);
			parm.addData("END_CODE", newEndTime);
			parm.addData("PARM_COUNT", 3);

			TParm resultParm = InsManager.getInstance().safe(parm, null);
			if (resultParm == null) {
				this.callFunction("UI|TTABLE|setParmValue", new TParm());
				return;
			}

			int count = resultParm.getCount("CODE");
			if (count <= 0) {
				messageBox("无下载资料!");
				this.callFunction("UI|TTABLE|setParmValue", new TParm());
				return;
			}
			this.callFunction("UI|TTABLE|setParmValue", resultParm);

		} else if ("CZ".equals(type)) {// 城职
			this.tTable.setParmMap("REPORT_CODE");
			TParm parm = new TParm();
			parm.setData("PIPELINE", "DataDown_rs");
			parm.setData("PLOT_TYPE", "J");

			parm.addData("HOSP_NHI_NO", this.nhi_no);
			parm.addData("PAYMENT_START_CODE", newBeginTime);
			parm.addData("PAYMENT_END_CODE", newEndTime);
			parm.addData("PARM_COUNT", 3);

			TParm resultParm = InsManager.getInstance().safe(parm, null);

			if (resultParm == null) {
				this.callFunction("UI|TTABLE|setParmValue", new TParm());
				return;
			}
			int count = resultParm.getCount("REPORT_CODE");
			if (count <= 0) {
				messageBox("无下载资料!");
				this.callFunction("UI|TTABLE|setParmValue", new TParm());
				return;
			}
			this.callFunction("UI|TTABLE|setParmValue", resultParm);
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.setValue("START_DATE", "");
		this.setValue("END_DATE", "");
		this.setValue("TYPE", "");

		this.callFunction("UI|TTABLE2|setParmValue", new TParm());
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
		this.tablePanel.setSelectedIndex(0);

	}

	/**
	 * 汇出
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TTABLE2");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "住院医疗费拒付明细表");
	}

	/**
	 * 页签切换动作
	 */
	public void onChange() {
		int selectedRow = this.tTable.getSelectedRow();
		int selectedIndex = this.tablePanel.getSelectedIndex();
		if (selectedRow < 0 && selectedIndex == 1) {
			messageBox("请选择一个报盘批号.");
			this.tablePanel.setSelectedIndex(0);
			return;
		}
		if (selectedIndex == 1) {// 定位到拒付明细.
			String reportCode = (String) this.tTable
					.getValueAt_(selectedRow, 0);
			//String type=this.getValueString("TYPE");
			StringBuffer sbuilder=new StringBuffer();
//			if("CZ".equals(type)){
//				sbuilder.append(" AND O.CTZ1_CODE IN ('11','12','13')");
//			}else if("CJ".equals(type)){
//				sbuilder.append(" AND O.CTZ1_CODE IN ('21','22','23')");
//			}else{
//				sbuilder.append(" AND O.CTZ1_CODE IN ('11','12','13','21','22','23')");
//			}
			sbuilder.append(" AND O.CTZ1_CODE IN ('11','12','13','21','22','23')");
			String sql = "SELECT  O.DEPT_DESC, O.CASE_NO,O.MR_NO, O.PAT_NAME, O.IDNO, S.CTZ_DESC,"
					+ " D.ORDER_DESC, D.REFUSE_AMT, D.REFUSE_REASON_NOTE, D.SEQ_NO"
					+ " FROM INS_IBSORDER_DOWNLOAD D, INS_ADM_CONFIRM O , SYS_CTZ S  "
					+ " WHERE D.ADM_SEQ=O.ADM_SEQ"
					+ " AND O.HIS_CTZ_CODE = S.CTZ_CODE"
					+ " AND D.REPORT_CODE= '"
						+ reportCode
						+ "' "
					//+ " AND I.PHA_NHI_AMT+I.EXM_NHI_AMT+I.OP_NHI_AMT+I.TREAT_NHI_AMT+I.MATERIAL_NHI_AMT+I.OTHER_NHI_AMT+I.BLOODALL_NHI_AMT+I.BLOOD_NHI_AMT+I.BED_NHI_AMT > O.START_STANDARD_AMT"
					+ " AND D.REFUSE_AMT <> 0.00 AND (D.NHI_ORDER_CODE IS NULL OR D.NHI_ORDER_CODE<>'***018') "+sbuilder
					+ " ORDER BY O.DEPT_DESC DESC";
			//System.out.println("sql:::::"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));

			if (result.getErrCode() < 0) {  
				messageBox(result.getErrText());
				return;
			}
			if (result.getCount() <= 0) {
				messageBox("查无数据");
				this.callFunction("UI|TTABLE2|setParmValue", result);
				return;
			}
			this.callFunction("UI|TTABLE2|setParmValue", result);
		}

	}

	private boolean checkInput(String startDate, String endDate, String type) {
		if ("".equals(startDate)) {
			messageBox("请输入开始期号.");
			return false;
		}
		if ("".equals(endDate)) {
			messageBox("请输入结束期号.");
			return false;
		}
		if ("".equals(type)) {
			messageBox("请选择医保类别.");
			return false;
		}
		return true;
	}

	public String getNhi_no() {
		return nhi_no;
	}

	public void setNhi_no(String nhiNo) {
		nhi_no = nhiNo;
	}

	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}

	public TTabbedPane getTablePanel() {
		return tablePanel;
	}

	public void setTablePanel(TTabbedPane tablePanel) {
		this.tablePanel = tablePanel;
	}
}
