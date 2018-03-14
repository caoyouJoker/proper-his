package com.javahis.ui.iva;

import java.sql.Timestamp;

import jdo.iva.IVADeploymentTool;
import jdo.iva.IVADispensingTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 静配中心发药Control
 * </p>
 * 
 * <p>
 * Description: 静配中心发药Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2013.07.28
 * @version 1.0
 */

public class IVADispensingControl extends TControl {

	// 得到table控件
	private TTable table_d;
	private TTable table_m;
	private boolean save = false;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	// 得到checkbox控件
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	// 得到RadioButton控件
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	public void onInit() {
		super.onInit();
		String sql = "SELECT BATCH_CODE,BATCH_CHN_DESC FROM ODI_BATCHTIME";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String values = "[id,name]";
		values += ",['','']";
		for (int i = 0; i < result.getCount(); i++) {
			values += ",[" + result.getValue("BATCH_CODE", i) + ","
					+ result.getValue("BATCH_CHN_DESC", i) + "]";
		}
		String endvalues = "[" + values + "]";
		getTComboBox("BATCH_CODE").setStringData(endvalues);
		getTComboBox("BATCH_CODE").setEnabled(false);
		initPage();
	}

	/*
	 * 初始化时间和发药人员
	 */
	public void initPage() {

		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
//		Operator.getID();
//		this.setValue("IVA_DISPENSE_USER", Operator.getID());

	}

	/*
	 * 查询已发药or未发药信息
	 */
	public void onQuery() {

		// 得到TABLE_M控件
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		// 封装前台得到的值
		TParm parm = new TParm();
		// 得到前台控件值

		String station_code = this.getValueString("STATION_CODE");
		String barcode = this.getValueString("BAR_CODE");
		String mr_no = this.getValueString("MR_NO");
		String batch_code = this.getValueString("BATCH_CODE");
		String iva_dispense_user = this.getValueString("IVA_DISPENSE_USER");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// 判断前台界面得到的值是否为空
		if (barcode != null && !"".equals(barcode)) {
			parm.setData("BAR_CODE", barcode);
		}
		if (station_code != null && !"".equals(station_code)) {
			parm.setData("STATION_DESC", station_code);
		}
		if (mr_no != null && !"".equals(mr_no)) {
			parm.setData("MR_NO", mr_no);
		}
		if (batch_code != null && !"".equals(batch_code)) {
			parm.setData("BATCH_CODE", batch_code);
		}
		if (start_time != null && !"".equals(start_time)) {
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间");
			return;
		}
		if (end_time != null && !"".equals(end_time)) {
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间");
			return;
		}
		TParm result = new TParm();
		// 判断单选按钮是否选中
		if (getRadioButton("BUTTON_M").isSelected()) {
			parm.setData("FLG", "N");
		}
		if (getRadioButton("BUTTON_D").isSelected()) {
			parm.setData("FLG", "Y");
			if (iva_dispense_user != null && !"".equals(iva_dispense_user)) {
				parm.setData("IVA_DISPENSE_USER", iva_dispense_user);
			}
		}
		result = IVADispensingTool.getInstance().queryInfo(parm);
		if(!save){
			if(result.getCount() <= 0){
				this.messageBox("未查询到数据");
				return;
			}
		}
		save=false;
		table_m.setParmValue(result);
	}

	/*
	 * 单击事件
	 */
	public void onClick() {
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		int row = table_m.getSelectedRow();
		TParm tparm = table_m.getParmValue();
		TParm parm = new TParm();
		if (row != -1) {
//			this.setValue("STATION_CODE", tparm.getValue("STATION_CODE", row));
//			this.setValue("MR_NO", tparm.getValue("MR_NO", row));
//			this.setValue("PAT_NAME", tparm.getValue("PAT_NAME", row));
			parm.setData("CASE_NO", tparm.getValue("CASE_NO", row));
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			String barcode = this.getValueString("BAR_CODE");
			String batch_code = this.getValueString("BATCH_CODE");
//			String iva_dispense_user = this.getValueString("IVA_DISPENSE_USER");
//			String station_code = this.getValueString("STATION_CODE");
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
			if (barcode != null && !"".equals(barcode)) {
				parm.setData("BAR_CODE", barcode);
			}
			if (batch_code != null && !"".equals(batch_code)) {
				parm.setData("BATCH_CODE", batch_code);
			}
			parm.setData("STATION_DESC", tparm.getValue("STATION_CODE", row));
			if (getRadioButton("BUTTON_M").isSelected()) {
				parm.setData("FLG", "N");
			}
			if (getRadioButton("BUTTON_D").isSelected()) {
//				this.setValue("IVA_DISPENSE_USER", tparm.getValue("IVA_DISPENSE_USER", row));
				parm.setData("IVA_DISPENSE_USER", tparm.getValue("IVA_DISPENSE_USER", row));
				parm.setData("FLG", "Y");
			}
			TParm result = IVADispensingTool.getInstance().querydetail(parm);
			for(int i=0; i<result.getCount("ORDER_CODE");i++){
				result.setData("EXEC_DATE", i, 
						result.getValue("EXEC_DATE", i).substring(0, 4)
						+"-"
						+result.getValue("EXEC_DATE", i).substring(4, 6)
						+"-"
						+result.getValue("EXEC_DATE", i).substring(6, 8)
						+" "
						+result.getValue("EXEC_DATE", i).substring(8, 10)
						+":"
						+result.getValue("EXEC_DATE", i).substring(10, 12));
			}
			table_d.setParmValue(result);
		}
		onTableCheckBoxClicked(this);

	}

	/*
	 * 清空
	 */

	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		getTComboBox("BATCH_CODE").setValue("");
		this.clearValue("STATION_CODE;MR_NO;PAT_NAME;BAR_CODE;IVA_DISPENSE_USER;TREAT_START_TIME;TREAT_END_TIME");
	}

	/*
	 * 单选按钮事件
	 */
	public void onRadion() {
		if (getRadioButton("BUTTON_D").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("IVA_DISPENSE_USER"))
					.setEnabled(true);
			getTComboBox("BATCH_CODE").setEnabled(true);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
			initPage();
			onClear();
		}
		if (getRadioButton("BUTTON_M").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("IVA_DISPENSE_USER"))
					.setEnabled(false);
			getTComboBox("BATCH_CODE").setEnabled(false);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			initPage();
			onClear();
		}

	}

	/*
	 * 调配完之后保存
	 */
	public void onSave() {
		String check_user = this.getValueString("IVA_DISPENSE_USER");
		table_d = this.getTTable("TABLE_D");
		table_m = this.getTTable("TABLE_M");
		TParm tparm = table_m.getParmValue();
		int num = 0;
		for(int n=0;n<tparm.getCount("CASE_NO");n++){
			if(tparm.getValue("SELECT_AVG", n).equals("Y")){
				num++;
			}
		}
		if(num == 0){
			this.messageBox("没有要保存的数据");
			return;
		}
		if(check_user.equals("")){
			String type = "singleExe";
			TParm inParm = (TParm) this.openDialog(
					"%ROOT%\\config\\inw\\passWordCheck.x", type);
			String OK = inParm.getValue("RESULT");
			if (!OK.equals("OK")) {
				return;
			}
			this.setValue("IVA_DISPENSE_USER", inParm.getValue("USER_ID"));
			check_user = this.getValueString("IVA_DISPENSE_USER");
		}
		// 得到未选中的所有值
		TParm result = new TParm();
		int number = 0;
		for (int i = 0; i < tparm.getCount(); i++) {
			TParm rowParm = tparm.getRow(i);
			String flg = rowParm.getValue("SELECT_AVG");
			// 判断是否选中
			if ("Y".equals(flg)) {// flg.equals("Y")
				rowParm.setData("IVA_DISPENSE_USER", check_user);
				result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
						"onUpdateDispensing", rowParm);
				if (result.getErrCode() < 0) {
//					temParm.setRowData(i, rowParm);
					number++;
				}
			} 
//			else {
//				temParm.setRowData(i, rowParm);
//			}
		}
		if(number <= 0){
			this.messageBox("保存成功");
		}else{
			this.messageBox("保存失败");
		}
		table_d.removeRowAll();
		table_m.removeRowAll();
		this.clearValue("MR_NO;PAT_NAME;BAR_CODE");
		save=true;
		this.onQuery();
//		table_m.setParmValue(temParm);
	}

	/*
	 * 全选
	 */
	public void onSelectAll() {
		table_m = getTTable("TABLE_M");
		table_m.acceptText();
		if (table_m.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_AVG", getValueString("SELECT_ALL"));
		}
	}

	/*
	 * 复选框事件
	 */
	public void onTableCheckBoxClicked(Object obj) {
		if (getRadioButton("BUTTON_M").isSelected()) {
			table_m = this.getTTable("TABLE_M");
			int column = table_m.getSelectedRow();
			int row = table_m.getSelectedColumn();
			if ("N".equals(table_m.getItemString(column, "SELECT_AVG"))
					&& row == 0) {
				table_m.setItem(column, "SELECT_AVG", "Y");
			} else if ("Y".equals(table_m.getItemString(column, "SELECT_AVG"))
					&& row == 0){
				table_m.setItem(column, "SELECT_AVG", "N");
			}
		}

	}

	public void onActionText() {
		onQuery();
	}
	
	/**
	 * 打印报表
	 */
	public void onPrint(){
		// 判断是分批打印还是全部打印
//		String select_all = this.getValueString("SELECT_ALL");
//		if("Y".equals(select_all)){
//			onPrintAll();
//		} else {
//			onPrintSingle();
//		}
		String stationSql = "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION ORDER BY STATION_CODE ";
		TParm stationParm = new TParm(TJDODBTool.getInstance().select(stationSql));
		table_m = getTTable("TABLE_M");
		TParm tableMParm = table_m.getParmValue();
//		System.out.println(tableMParm);
		String statinDesc = ""; 
		String ivaDispenseUser = "";
		String batch_code = this.getValueString("BATCH_CODE");
		for(int s=0;s<stationParm.getCount("STATION_CODE");s++){
			TParm parm = new TParm();
			statinDesc = stationParm.getValue("STATION_DESC", s);
			for(int i=0; i<tableMParm.getCount("STATION_DESC"); i++){
				if(tableMParm.getRow(i).getValue("SELECT_AVG").equals("Y")
						&& tableMParm.getRow(i).getValue("STATION_CODE").equals(stationParm.getValue("STATION_CODE", s))){
					ivaDispenseUser = tableMParm.getRow(i).getValue("IVA_DISPENSE_USER");
					String sql = "SELECT  A.LINKMAIN_FLG AS SELECT_FLG,A.ORDER_CODE,A.ORDER_DESC || '  ' || A.SPECIFICATION AS ORDER_DESC,"
							+ " A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY, "
							+ "B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,"
							+ "C.FREQ_CHN_DESC,D.ROUTE_CHN_DESC,A.LINK_NO,B.BAR_CODE,B.IVA_DISPENSE_TIME "
							+ "FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_UNIT E,SYS_UNIT F "
							+ "WHERE  A.CASE_NO='"
							+ tableMParm.getRow(i).getValue("CASE_NO")
							+ "' AND A.STATION_CODE='" 
							+ tableMParm.getRow(i).getValue("STATION_CODE")
							+ "' AND A.MR_NO='" 
							+ tableMParm.getRow(i).getValue("MR_NO") 
							+ "' AND A.IVA_FLG = 'Y' "
							+ " AND A.CASE_NO=B.CASE_NO "
							+ " AND A.ORDER_NO=B.ORDER_NO "
							+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
							+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
							+" AND B.IVA_DISPENSE_TIME BETWEEN TO_DATE('"
							+ this.getValueString("START_TIME").toString().substring(0, 19)
							+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
							+ this.getValueString("END_TIME").toString().substring(0, 19)
							+ "','YYYY-MM-DD HH24:MI:SS') ";
					if (batch_code != null && !"".equals(batch_code)) {
						sql += " AND B.BATCH_CODE = '"+batch_code+"' "
								+ " AND (A.DSPN_KIND = 'ST' OR TO_DATE(B.ORDER_DATE || B.ORDER_DATETIME,"
								+ "'YYYY-MM-DD HH24:MI') BETWEEN TO_DATE('"
								+ this.getValueString("START_TIME").toString().substring(0, 16)
								+ "','YYYY-MM-DD HH24:MI')" + " AND TO_DATE('"
								+ this.getValueString("END_TIME").toString().substring(0, 16)
								+ "','YYYY-MM-DD HH24:MI')) ";
					}
					sql += " AND B.IVA_FLG = 'Y' "
							+ " AND B.IVA_CHECK_USER IS NOT NULL "
							+ " AND B.IVA_DISPENSE_USER='"
							+ tableMParm.getRow(i).getValue("IVA_DISPENSE_USER") 
							+ "' AND B.IVA_CHECK_USER IS NOT NULL "
							+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE "
							+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
							+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
//					System.out.println(sql);
					TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//					System.out.println("result====="+result);
					for(int j=0; j<result.getCount("ORDER_CODE");j++){
						parm.addData("BED_NO_DESC", tableMParm.getRow(i).getValue("BED_NO_DESC"));
						parm.addData("MR_NO", tableMParm.getRow(i).getValue("MR_NO"));
						parm.addData("PAT_NAME", tableMParm.getRow(i).getValue("PAT_NAME"));
						parm.addData("LINK_NO", result.getValue("LINK_NO",j));
						parm.addData("ORDER_DESC", result.getValue("ORDER_DESC",j));
						parm.addData("MEDI_QTY", result.getValue("MEDI_QTY",j));
						parm.addData("ROUTE_CHN_DESC", result.getValue("ROUTE_CHN_DESC",j));
						parm.addData("FREQ_CHN_DESC", result.getValue("FREQ_CHN_DESC",j));
						parm.addData("DOSAGE_QTY", result.getValue("DOSAGE_QTY",j));
					}
				}
			}
//			System.out.println(parm.getCount("BED_NO_DESC"));
			if(parm.getCount("BED_NO_DESC")<=0){
				continue;
			}
			TParm data = new TParm();
			// 总行数
			parm.setCount(parm.getCount("MR_NO"));
			parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "LINK_NO");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			parm.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			// 将表格放到容器中
			data.setData("TABLE", parm.getData());
			// 表尾/表尾数据
			data.setData("USER", "TEXT", Operator.getName());
	//		data.setData("IVA_DISPENSE_TIME", "TEXT", tableMParm.getRow(0).getValue("IVA_DISPENSE_TIME").toString()
	//						.substring(0, 19));
			String userSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
							+ivaDispenseUser+"' ";
			TParm userParm = new TParm(TJDODBTool.getInstance().select(userSql));
			data.setData("IVA_DISPENSE_USER", "TEXT", userParm.getValue("USER_NAME",0));
			data.setData("STATION_DESC", "TEXT", statinDesc);
			data.setData("DATE", "TEXT", SystemTool.getInstance().getDate().toString()
							.substring(0, 19));
//			System.out.println("data====>>>>>>>>"+data);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADispenseConfirmList.jhw", data);
		}
	}
	
	/**
	 * 全部打印报表
	 */
//	public void onPrintAll(){
//		table_m = getTTable("TABLE_M");
//		if(table_m.getRowCount() <= 0){
//			messageBox("没有要打印的数据");
//			return;
//		}
//		// 打印数据
//		TParm data = new TParm();
//		// 表头数据
//		data.setData("TITLE", "TEXT", "静配中心发药清单");
//		data.setData("DATE", "TEXT", "统计时间: "
//				+ SystemTool.getInstance().getDate().toString()
//						.substring(0, 10).replace('-', '/'));
//		// 表格数据
//		TParm parm = new TParm();
//		TParm result = IVADeploymentTool.getInstance().queryAllData();
//		// 遍历表格中的元素
//		for (int i = 0; i < result.getCount(); i++) {
//			parm.addData("PAT_NAME", result.getValue("PAT_NAME", i));
//			parm.addData("MR_NO", result.getValue("MR_NO", i));
//			parm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
//			parm.addData("BED_NO_DESC", result.getValue("BED_NO_DESC", i));
//			parm.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
//			parm.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
//			parm.addData("MEDI_QTY", result.getValue("MEDI_QTY", i));
//			parm.addData("FREQ_CHN_DESC", result.getValue("FREQ_CHN_DESC", i));
//		}
//		// 总行数
//		parm.setCount(parm.getCount("ORDER_DESC"));
//		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
//		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
//		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
//		// 将表格放到容器中
//		data.setData("TABLE", parm.getData());
//		// 表尾数据
//		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
//		// 调用打印方法
//		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADispenseConfirmList.jhw", data);
//	}
//	
//	/**
//	 * 分批打印报表
//	 */
//	public void onPrintSingle(){
//		table_d = getTTable("TABLE_D");
//		if(table_d.getRowCount() <= 0){
//			messageBox("没有要打印的数据");
//			return;
//		}
//		// 打印数据
//		TParm data = new TParm();
//		// 表头数据
//		data.setData("TITLE", "TEXT", "静配中心发药清单");
//		data.setData("DATE", "TEXT", "统计时间: "
//				+ SystemTool.getInstance().getDate().toString()
//						.substring(0, 10).replace('-', '/'));
//		// 表格数据
//		TParm parm = new TParm();
//		int rowM = table_m.getSelectedRow();
//		TParm tableParmM = table_m.getParmValue().getRow(rowM);
//		TParm tableParmD = table_d.getParmValue();
//		// 遍历表格中的元素
//		for (int i = 0; i < table_d.getRowCount(); i++) {
//			parm.addData("PAT_NAME", tableParmM.getValue("PAT_NAME"));
//			parm.addData("MR_NO", tableParmM.getValue("MR_NO"));
//			parm.addData("STATION_DESC", tableParmM.getValue("STATION_DESC"));
//			parm.addData("BED_NO_DESC", tableParmM.getValue("BED_NO_DESC"));
//			parm.addData("ORDER_DESC", tableParmD.getValue("ORDER_DESC", i));
//			parm.addData("SPECIFICATION", tableParmD.getValue("SPECIFICATION", i));
//			parm.addData("MEDI_QTY", tableParmD.getValue("MEDI_QTY", i));
//			parm.addData("FREQ_CHN_DESC", tableParmD.getValue("FREQ_CHN_DESC", i));
//		}
//		// 总行数
//		parm.setCount(parm.getCount("ORDER_DESC"));
//		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
//		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
//		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
//		// 将表格放到容器中
//		data.setData("TABLE", parm.getData());
//		// 表尾数据
//		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
//		// 调用打印方法
//		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADispenseConfirmList.jhw", data);
//	}
	
	/**
	 * 查询病患信息
	 */
	public void onQueryNO() {
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			clearValue("MR_NO;PAT_NAME;");
			this.messageBox("无此病案号!");
			return;
		}
		//modify by huangtt 20160930 EMPI患者查重提示  start
		String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
	            setValue("MR_NO", pat.getMrNo());
	    }
		//modify by huangtt 20160930 EMPI患者查重提示  end
		setValue("PAT_NAME", pat.getName().trim());
		this.onQuery();
	}
	
	public void onTcomBox() {
		// 查询是否有未退药
		String batch_code = this.getValueString("BATCH_CODE");
		if(batch_code.equals("")){
			this.clearValue("TREAT_START_TIME;TREAT_END_TIME");
		}
		TParm result = IVADeploymentTool.getInstance().queryByBatchCode(batch_code);
//		this.setValue("IVA_TIME", result.getValue("IVA_TIME", 0).substring(0, 2)
//										+":"
//										+result.getValue("IVA_TIME", 0).substring(2, 4));
		this.setValue("TREAT_START_TIME", result.getValue("TREAT_START_TIME", 0).substring(0, 2)
												+":"
												+result.getValue("TREAT_START_TIME", 0).substring(2, 4));
		this.setValue("TREAT_END_TIME", result.getValue("TREAT_END_TIME", 0).substring(0, 2)
												+":"
												+result.getValue("TREAT_END_TIME", 0).substring(2, 4));
	}
	
	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
	
}
