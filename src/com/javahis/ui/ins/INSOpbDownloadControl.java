package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 医保门诊下载
 * </p>
 * 
 * <p>
 * Description: 医保门诊下载
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
 * @author zhangp 20120207
 * @version 1.0
 */
public class INSOpbDownloadControl extends TControl {
	int panel = 1;
	TTabbedPane tab;
	String pipeLine = "DataDown_yb";
	String plot_type = "E";
	String renyuan = "";
	String regType = "";
	String confirm = "";
	boolean selectAll = true;// 全选
	private TParm sumParm;
	 /**
     * 执行Module动作
     * @return String
     */
    private String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='INS_MTLBA' ORDER BY SEQ,ID ";
        return sql;
    }
	public void onInit() {
		tab = (TTabbedPane) getComponent("tTabbedPane_0");
		setValue("RENYUAN", 1);
		setValue("REG_TYPE", 1);
		tab.setEnabled(false);
		renyuan = getValueString("RENYUAN");
		regType = getValueString("REG_TYPE");
		sumParm=new TParm(TJDODBTool.getInstance().select(getPopupMenuSQL()));
		this.callFunction("UI|END_DATE|setValue", SystemTool.getInstance()
				.getDate());
		this.callFunction("UI|START_DATE|setValue", SystemTool.getInstance()
				.getDate());
		this.callFunction("UI|download2|setEnabled", false);
		this.callFunction("UI|download3|setEnabled", false);
		this.callFunction("UI|download4|setEnabled", false);
		TNumberTextField downloadCount = (TNumberTextField) getComponent("DOWNLOAD_COUNT");
		downloadCount.setEnabled(false);
		TLabel label1 = (TLabel) getComponent("tLabel_63");
		TLabel label2 = (TLabel) getComponent("tLabel_64");
		label1.setEnabled(false);
		label2.setEnabled(false);
	}

	/**
	 * 下载
	 * 
	 * @param s
	 */
	public void onDownload(int s) {
		
		String startDate = getValueString("START_DATE");// 开始时间
		String endDate = getValueString("END_DATE");// 结束时间
		if (startDate.equals("") || endDate.equals("")) {
			messageBox("请选择查询日期");
			return;
		}
		this.callFunction("UI|TABLE4|setParmValue", "");
		TParm result = null;
		if (s == 1) {
			tab.setSelectedIndex(0);
			panel = 1;
			if (renyuan.equals("1") && regType.equals("1")
					|| renyuan.equals("1") && regType.equals("2")) {// 城职普通/门特
				pipeLine = "DataDown_zjks";
				plot_type = "B";
			}
			if (renyuan.equals("2") && regType.equals("2")) {
				pipeLine = "DataDown_cjks";
				plot_type = "B";
			}

			result = download1();
			if (result == null) {
				return;
			}
			this.callFunction("UI|TABLE1|setParmValue", result);
			this.callFunction("UI|download2|setEnabled", true);
			this.callFunction("UI|download3|setEnabled", true);
			this.callFunction("UI|download4|setEnabled", true);
		}
		if (s == 2) {
			tab.setSelectedIndex(1);
			panel = 2;
			pipeLine = "DataDown_yb";
			plot_type = "F";
			result = download2();
			if (result == null) {
				return;
			}
			this.callFunction("UI|TABLE2|setParmValue", result);
		}
		if (s == 3) {
			tab.setSelectedIndex(2);
			panel = 3;
			pipeLine = "DataDown_yb";
			plot_type = "G";
			result = download3();
			if (result == null) {
				return;
			}
			this.callFunction("UI|TABLE3|setParmValue", result);
		}
		if (s == 4) {
			tab.setSelectedIndex(3);
			panel = 4;
			if (renyuan.equals("1") && regType.equals("1")
					|| renyuan.equals("1") && regType.equals("2")) {// 城职普通/门特
				pipeLine = "DataDown_zjkd";
				plot_type = "C";
			}
			if (renyuan.equals("2") && regType.equals("2")) {
				pipeLine = "DataDown_cjkd";
				plot_type = "C";
			}
			result = download4();
			if (result == null) {
				return;
			}
			this.callFunction("UI|TABLE4|setParmValue", result);
		}

	}

	/**
	 * 查询
	 * 
	 * @param confirmNo
	 * @return
	 */
	public TParm onQuery(String confirmNo) {
		String sql =
		// "SELECT a.pat_name, a.mr_no, b.dept_chn_desc, c.user_name ,d.SEQ_NO"
		// +
		"SELECT a.pat_name, a.mr_no, b.dept_chn_desc, c.user_name "
				+ " FROM sys_patinfo a, sys_dept b, sys_operator c , ins_opd d ,reg_patadm e "
				+ " WHERE a.mr_no = d.mr_no and a.mr_no = e.mr_no and D.CASE_NO = e.case_no and "
				+ " c.user_id = E.REALDR_CODE  and b.dept_code = e.dept_code and d.confirm_no = '"
				+ confirmNo + "'";
		// System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return result;
		}
		// System.out.println("query======"+result);
		return result;
	}

	/**
	 * comboBox 监听器
	 */
	public void onSelect() {
		renyuan = getValueString("RENYUAN");
		TComboBox regCombo = (TComboBox) getComponent("REG_TYPE");
		if (renyuan.equals("2")) {
			setValue("REG_TYPE", 2);
			regCombo.setEnabled(false);
		} else {
			setValue("REG_TYPE", 1);
			regCombo.setEnabled(true);
			TTabbedPane si = (TTabbedPane) getComponent("tTabbedPane_0");
			si.setEnabled(false);
		}
		regType = getValueString("REG_TYPE");
	}

	/**
	 * 拒付信息下载
	 */
	public TParm download1() {
		TParm parm = new TParm();
		String startDate = getValueString("START_DATE");// 开始时间
		String endDate = getValueString("END_DATE");// 结束时间
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO

		parm.addData("HOSP_NHI_NO", hospital);
		// DateFormat df1 = new SimpleDateFormat("yyyyMM");
		parm.addData("ADM_SEQ", startDate.replace("-", "").substring(0, 6));
		parm.setData("PIPELINE", pipeLine);
		parm.setData("PLOT_TYPE", plot_type);
		parm.addData("PARM_COUNT", 2);
		TParm result = InsManager.getInstance().safe(parm, "");// 医保卡接口方法（复数）
																// 返回集合
		if (result.getInt("PROGRAM_STATE", 0) == -1) {
			this.messageBox(result.getValue("PROGRAM_MESSAGE", 0));
			return null;
		}
		confirm = result.getData("SUM_LOT_NUMBER", 0).toString();
		String[] confirms = confirm.split(",");
		TParm tableParm = new TParm();// 最终的tparm
		TParm parm1 = null;
		int count = 0;// 循环次数
		if (selectAll) {
			count = confirms.length;
		} else {
			count = getValueInt("DOWNLOAD_COUNT");
		}
		if (count > confirms.length) {
			count = confirms.length;
		}

		for (int i = 0; i < count; i++) {
			parm1 = onQuery(confirms[i]);
			// a.pat_name, a.mr_no, b.dept_chn_desc, c.user_name
			tableParm.addData("PAT_NAME", parm1.getData("PAT_NAME", 0));
			tableParm.addData("MR_NO", parm1.getData("MR_NO", 0));
			tableParm.addData("DEPT_CHN_DESC", parm1
					.getData("DEPT_CHN_DESC", 0));
			tableParm.addData("USER_NAME", parm1.getData("USER_NAME", 0));
			tableParm.addData("CONFIRM_NO", confirms[i]);// 汇总期号
			tableParm.addData("HOSP_PRO_AMT", result.getDouble(
					"HOSP_PRO_AMT", i));// 医院拒付金额
			tableParm.addData("DR_PRO_AMT", result.getDouble("DR_PRO_AMT", i));// 医师拒付金额
			tableParm
					.addData("THE_PRO_AMT", result.getDouble("THE_PRO_AMT", i));// 本期实际拒付
			tableParm.addData("SCROLL_PRO_AMT", result.getDouble(
					"SCROLL_PRO_AMT", i));// 滚存拒付
			tableParm.addData("APPLY_PAY_AMT", result.getData("APPLY_PAY_AMT",
					i));// 申请支付
			tableParm.addData("SUM_LOT_NUMBER", result.getData(
					"SUM_LOT_NUMBER", i));// 汇总批号
			tableParm.addData("PROGRAM_STATE", result.getData("PROGRAM_STATE",
					i));// 程序执行状态
			tableParm.addData("PROGRAM_MESSAGE", result.getData(
					"PROGRAM_MESSAGE", i));// 程序执行信息
		}
		return tableParm;
	}

	/**
	 * 缓支信息下载
	 */
	public TParm download2() {
		TParm parm = new TParm();
		String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		parm.addData("HOSP_NHI_NO", hospital);
		parm.addData("START_DATE", startDate);
		parm.addData("END_DATE", endDate);
		parm.setData("PIPELINE", pipeLine);
		parm.setData("PLOT_TYPE", plot_type);
		parm.addData("PARM_COUNT", 3);
		TParm result = InsManager.getInstance().safe(parm, "");
		String[] confirms = confirm.split(",");
		TParm tableParm = new TParm();// 最终的tparm
		TParm parm1 = null;
		for (int i = 0; i < confirms.length; i++) {
			parm1 = onQuery(confirms[i]);
			tableParm.addData("PAT_NAME", parm1.getData("PAT_NAME", 0));
			tableParm.addData("MR_NO", parm1.getData("MR_NO", 0));
			tableParm.addData("DEPT_CHN_DESC", parm1
					.getData("DEPT_CHN_DESC", 0));
			tableParm.addData("USER_NAME", parm1.getData("USER_NAME", 0));
			tableParm.addData("PROGRAM_STATE", result.getData("PROGRAM_STATE",
					0));// 程序执行状态
			tableParm.addData("PROGRAM_MESSAGE", result.getData(
					"PROGRAM_MESSAGE", 0));// 程序执行信息
			tableParm.addData("SLOW_DATE", result.getData("SLOW_DATE", 0));// 缓支时间
			tableParm.addData("SLOW_REASON_CODE", result.getData(
					"SLOW_REASON_CODE", 0));// 违规原因编码
			tableParm.addData("ADM_SEQ", result.getData("ADM_SEQ", 0));// 就诊顺序号
		}
		return tableParm;
	}

	/**
	 * 缓支给付下载
	 */
	public TParm download3() {
		TParm parm = new TParm();
		String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		parm.addData("HOSP_NHI_NO", hospital);
		parm.addData("START_DATE", startDate);
		parm.addData("END_DATE", endDate);
		parm.setData("PIPELINE", pipeLine);
		parm.setData("PLOT_TYPE", plot_type);
		parm.addData("PARM_COUNT", 3);
		TParm result = InsManager.getInstance().safe(parm, "");
		String[] confirms = confirm.split(",");
		TParm tableParm = new TParm();// 最终的tparm
		TParm parm1 = null;
		for (int i = 0; i < confirms.length; i++) {
			parm1 = onQuery(confirms[i]);
			tableParm.addData("PAT_NAME", parm1.getData("PAT_NAME", 0));
			tableParm.addData("MR_NO", parm1.getData("MR_NO", 0));
			tableParm.addData("DEPT_CHN_DESC", parm1
					.getData("DEPT_CHN_DESC", 0));
			tableParm.addData("USER_NAME", parm1.getData("USER_NAME", 0));
			tableParm.addData("PROGRAM_STATE", result.getData("PROGRAM_STATE",
					i));// 程序执行状态
			tableParm.addData("PROGRAM_MESSAGE", result.getData(
					"PROGRAM_MESSAGE", i));// 程序执行信息
			tableParm.addData("SLOW_PAY_DATE", result.getData("SLOW_PAY_DATE",
					i));// 缓支支付时间
			tableParm.addData("ADM_SEQ", result.getData("ADM_SEQ", i));// 就诊顺序号
		}
		return tableParm;
	}

	/**
	 * 拒付明细下载
	 */
	public TParm download4() {
		if (confirm.equals("")) {
			messageBox("请先下载拒付信息");
			return null;
		}
		TParm parm = null;
		String[] confirms = confirm.split(",");
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		TParm tableParm = new TParm();// 最终的tparm
		TParm parm1 = null;
		for (int i = 0; i < confirms.length; i++) {
			parm = new TParm();
			parm.addData("HOSP_NHI_NO", hospital);
			parm.addData("SUM_LOT_NUMBER", confirms[i]);
			parm.setData("PIPELINE", pipeLine);
			parm.setData("PLOT_TYPE", plot_type);
			parm.addData("PARM_COUNT", 2);
			TParm result = InsManager.getInstance().safe(parm, "");
			parm1 = onQuery(confirms[i]);
			for (int j = 0; j < result.getCount("MEDIAL_CODE"); j++) {
				tableParm.addData("PAT_NAME", parm1.getData("PAT_NAME", 0));
				tableParm.addData("MR_NO", parm1.getData("MR_NO", 0));
				tableParm.addData("DEPT_CHN_DESC", parm1.getData(
						"DEPT_CHN_DESC", 0));
				tableParm.addData("USER_NAME", parm1.getData("USER_NAME", 0));
				tableParm.addData("SOURCE", getSource(result.getValue("SOURCE", j)));// 来源
				tableParm.addData("DISEASE_CODE", getDiseaseDesc(result.getValue(
						"DISEASE_CODE", j)));// 病种名称
				tableParm.addData("DRUG_DAY_AVG", result.getDouble(
						"DRUG_DAY_AVG", j));// 药日均
				tableParm.addData("PEOPLE_DAYDRUG_AMT", result.getDouble(
						"PEOPLE_DAYDRUG_AMT", j));// 人日均药品费
				tableParm.addData("RICH_DRUG_RATE", result.getDouble(
						"RICH_DRUG_RATE", j));// 贵重药品比例
				tableParm.addData("REFUSE_AMT", result.getDouble("REFUSE_AMT",
						j));// 拒付金额
				tableParm.addData("REFUSE_CODE", result.getData("REFUSE_CODE",
						j));// 拒付编码
				tableParm.addData("REFUSE_REASON", result.getData(
						"REFUSE_REASON", j));// 拒付原因
				tableParm.addData("MEDIAL_CODE", result.getData("MEDIAL_CODE",
						j));// 医疗机构编码（或执业医师编码或执业药师编码）
				tableParm.addData("MEDIAL_DESC", result.getData("MEDIAL_DESC",
						j));// 医疗机构名称（或执业医师姓名或执业药师姓名）
				tableParm.addData("CONFIRM_NO", confirms[i]);
			}			
		}
		return tableParm;
	}
	/**
	 * 获得病种名称
	 * @param code
	 * @return
	 */
	private String getDiseaseDesc(String code){
		for (int i = 0; i < sumParm.getCount(); i++) {
			if (sumParm.getValue("ID",i).equals(code)) {
				return sumParm.getValue("NAME",i);
			}
		}
		return "";
	}
	private String getSource(String source){
		if ("01".equals(source)) {
			return "城职、城乡门特治疗机构";
		}else if("02".equals(source)){
			return "城乡民政优抚治疗机构";
		}else if ("03".equals(source)) {
			return "城乡非民政优抚治疗机构";
		}
		return "";
	}
	/**
	 * radio监听器
	 * 
	 * @param s
	 */
	public void onRadioCheck(int s) {
		TCheckBox allSelect = (TCheckBox) getComponent("ALLSELECT");
		TNumberTextField downloadCount = (TNumberTextField) getComponent("DOWNLOAD_COUNT");
		TLabel label1 = (TLabel) getComponent("tLabel_63");
		TLabel label2 = (TLabel) getComponent("tLabel_64");
		if (s == 1) {
			allSelect.setEnabled(true);
			downloadCount.setEnabled(false);
			label1.setEnabled(false);
			label2.setEnabled(false);
			clearValue("DOWNLOAD_COUNT");

		}
		if (s == 2) {
			allSelect.setEnabled(false);
			downloadCount.setEnabled(true);
			label1.setEnabled(true);
			label2.setEnabled(true);
			clearValue("DOWNLOAD_COUNT");
		}

	}

	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("START_DATE;END_DATE");
		onRadioCheck(1);
		this.callFunction("UI|download2|setEnabled", false);
		this.callFunction("UI|download3|setEnabled", false);
		this.callFunction("UI|download4|setEnabled", false);
		panel = 1;
		tab.setSelectedIndex(0);
		this.callFunction("UI|END_DATE|setValue", SystemTool.getInstance()
				.getDate());
		this.callFunction("UI|START_DATE|setValue", SystemTool.getInstance()
				.getDate());
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = null;
		if (panel == 1) {
			table = (TTable) this.getComponent("TABLE1");
			if (table.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "拒付信息");
		}
		if (panel == 2) {
			table = (TTable) this.getComponent("TABLE2");
			if (table.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "缓支信息");
		}
		if (panel == 3) {
			table = (TTable) this.getComponent("TABLE3");
			if (table.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "缓支给付信息");
		}
		if (panel == 4) {
			table = (TTable) this.getComponent("TABLE4");
			if (table.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "拒付明细");
		}

	}

	/**
	 * 
	 * 查询
	 */
	public void onPrint() {
		if (panel == 1) {
			TTable table = (TTable) this.getComponent("TABLE1");
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				messageBox("请选择要打印的数据!");
				return;
			}

			TParm parm = table.getShowParmValue();
			String sumLotNumber = parm.getValue("SUM_LOT_NUMBER", selectedRow);// 汇总批号
			double hospProAmt = parm.getDouble("HOSP_PRO_AMT", selectedRow);// 医院拒付金额
			double drProAmt = parm.getDouble("DR_PRO_AMT", selectedRow);// 医师拒付金额
			double theProAmt = parm.getDouble("theProAmt", selectedRow);// 本期实际拒付
			double scrollProAmt = parm.getDouble("SCROLL_PRO_AMT", selectedRow);// 滚存拒付
			double applyPayAmt = parm.getDouble("APPLY_PAY_AMT", selectedRow);// 申请支付

			String sql = "SELECT NHI_NO,REGION_CHN_DESC FROM SYS_REGION WHERE REGION_CODE='"
					+ Operator.getRegion() + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() <= 0) {
				messageBox("没有找到医院相关信息.");
				return;
			}

			DecimalFormat df = new DecimalFormat("##########0.00");
			double sum = hospProAmt + drProAmt + theProAmt + scrollProAmt
					+ applyPayAmt;

			TParm exportData = new TParm();
			exportData
					.setData("HOSPCODE", "TEXT", result.getValue("NHI_NO", 0)); // 医院/药店编码
			exportData.setData("HOSPNAME", "TEXT", result.getValue(
					"REGION_CHN_DESC", 0)); // 医院/药店名称
			exportData.setData("WEEKNUM", "TEXT", sumLotNumber); // 汇总期号

			exportData.setData("HOSPREJECT", "TEXT", df.format(hospProAmt)); // 医院拒付
			exportData.setData("DOCREJECT", "TEXT", df.format(drProAmt)); // 医师拒付
			exportData.setData("REALREJECT", "TEXT", df.format(theProAmt)); // 本期实际拒付
			exportData.setData("ROLLREJECT", "TEXT", df.format(scrollProAmt)); // 滚存拒付
			exportData.setData("APPPAY", "TEXT", df.format(applyPayAmt)); // 申请支付
			exportData.setData("ALLSUM", "TEXT", df.format(sum)); // 小计

			exportData.setData("SIMAMT", "TEXT", df.format(sum)); // 小写
			exportData.setData("CHNAMT", "TEXT", StringUtil.getInstance()
					.numberToWord(sum)); // 大写

			this.openPrintWindow(
					"%ROOT%\\config\\prt\\INS\\INSRejectionSheet.jhw",
					exportData);
		} else if (panel == 4) {
			TTable table = (TTable) this.getComponent("TABLE4");
			TParm parm = table.getShowParmValue();
			if (parm.getCount() <= 0) {
				messageBox("没有打印数据!");
				return;
			}
			String weekDate = "";
			String startDate = this.getValueString("START_DATE");

			if (!"".equals(startDate)) {
				String[] date = startDate.split("-");
				weekDate = date[0] + date[1];
			}

			DecimalFormat df = new DecimalFormat("##########0.00");

			String sql = "SELECT NHI_NO,REGION_CHN_DESC FROM SYS_REGION WHERE REGION_CODE='"
					+ Operator.getRegion() + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() <= 0) {
				messageBox("没有找到医院相关信息.");
				return;
			}
			TParm exportData = new TParm();
			exportData
					.setData("HOSPCODE", "TEXT", result.getValue("NHI_NO", 0));
			exportData.setData("HOSPNAME", "TEXT", result.getValue(
					"REGION_CHN_DESC", 0));
			exportData.setData("WEEKNO", "TEXT", weekDate);
			TParm data = new TParm();
			double sum1 = 0;
			double sum2 = 0;
			double sum3 = 0;
			double sum4 = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				data.addData("NUM", i + 1);
				data.addData("SOURCE", parm.getData("SOURCE", i));
				data.addData("DISEASE_CODE", parm.getData("DISEASE_CODE", i));
				data.addData("DRUG_DAY_AVG", parm.getData("DRUG_DAY_AVG", i));
				data.addData("PEOPLE_DAYDRUG_AMT", parm.getData(
						"PEOPLE_DAYDRUG_AMT", i));
				data.addData("RICH_DRUG_RATE", parm
						.getData("RICH_DRUG_RATE", i));
				data.addData("REFUSE_AMT", parm.getData("REFUSE_AMT", i));
				data.addData("REFUSE_CODE", parm.getData("REFUSE_CODE", i));
				data.addData("REFUSE_REASON", parm.getData("REFUSE_REASON", i));
				sum1 += parm.getDouble("PEOPLE_DAYDRUG_AMT", i);
				sum2 += parm.getDouble("RICH_DRUG_RATE", i);
				sum3 += parm.getDouble("REFUSE_AMT", i);
				// sum4+= parm.getDouble("PEOPLE_DAYDRUG_AMT", i) ;
			}
			data.setCount(data.getCount("SOURCE"));
			data.addData("SYSTEM", "COLUMNS", "NUM");
			data.addData("SYSTEM", "COLUMNS", "SOURCE");
			data.addData("SYSTEM", "COLUMNS", "DISEASE_CODE");
			data.addData("SYSTEM", "COLUMNS", "DRUG_DAY_AVG");
			data.addData("SYSTEM", "COLUMNS", "PEOPLE_DAYDRUG_AMT");
			data.addData("SYSTEM", "COLUMNS", "RICH_DRUG_RATE");
			data.addData("SYSTEM", "COLUMNS", "REFUSE_AMT");
			data.addData("SYSTEM", "COLUMNS", "REFUSE_CODE");
			data.addData("SYSTEM", "COLUMNS", "REFUSE_REASON");
			exportData.setData("TABLE", data.getData());
			exportData.setData("SUM1", "TEXT", df.format(sum1));// 人日均药品费合计
			exportData.setData("SUM2", "TEXT", df.format(sum2));// 贵重药品比例合计
			exportData.setData("SUM3", "TEXT", df.format(sum3));// 拒付金额合计
			// exportData.setData("SUM4","TEXT", sum4) ;//拒付编码合计
			exportData.setData("VS", "TEXT", Operator.getID());// 经办人
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\INS\\InsRejectionDetailSheet.jhw",
					exportData);
		}
	}

}
