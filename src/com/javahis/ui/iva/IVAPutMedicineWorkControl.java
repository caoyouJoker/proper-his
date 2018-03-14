package com.javahis.ui.iva;

import java.sql.Timestamp;

import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.iva.IVADeploymentTool;
import jdo.iva.IVAPutMedicineWorkTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
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
 * Title: 病区排药Control
 * </p>
 * 
 * <p>
 * Description: 病区排药Control
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
 * @author zhangy 2013.07.21
 * @version 1.0
 */

public class IVAPutMedicineWorkControl extends TControl {
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

	// 初始化
	public void onInit() {
		super.onInit();
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		initPage();
	}

	// 初始化排药人员
	public void initPage() {
		this.setValue("PREPARE_USER", Operator.getID());
	}

	// 查询已排药or排药信息
	public void onQuery() {
		// 得到TABLE_M控件
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		table_m.removeRowAll();
		table_d.removeRowAll();
		// 封装前台得到的值
		TParm parm = new TParm();
		// 得到前台控件值
		String station_desc = this.getValueString("STATION_DESC");
		String mr_no = this.getValueString("MR_NO");
		String prepare_user = this.getValueString("PREPARE_USER");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// 判断前台界面得到的值是否为空
		if (station_desc != null && !"".equals(station_desc)) {
			parm.setData("STATION_DESC", station_desc);
		}
		if (mr_no != null && !"".equals(mr_no)) {
			parm.setData("MR_NO", mr_no);
		}
		if (start_time != null && !"".equals(start_time)) {
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间！");
			return;
		}
		if (end_time != null && !"".equals(end_time)) {
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间！");
			return;
		}
		TParm result = new TParm();
		// 判断单选按钮是否选中
		if (getRadioButton("BUTTON_M").isSelected()) {
			parm.setData("FLG", "N");
		}
		if (getRadioButton("BUTTON_D").isSelected()) {
			parm.setData("FLG", "Y");
			if (prepare_user != null && !"".equals(prepare_user)) {
				parm.setData("PREPARE_USER", prepare_user);
			}
		}
		result = IVAPutMedicineWorkTool.getInstance().queryInfo(parm);
		if(!save){
			if(result.getCount() <= 0){
				this.messageBox("未查询到数据");
				return;
			}
		}
		save = false;
		table_m.setParmValue(result);
	}

	/*
	 * 单击事件
	 */
	public void onClick() {
		// 得到table控件
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		// 得到选中行索引
		int row = table_m.getSelectedRow();
		// 得到table的值
		TParm tparm = table_m.getParmValue();
		// 得到查询细表的条件
		TParm parm = new TParm();
		if (row != -1) {
			String station_desc = this.getValueString("STATION_DESC");
			String mr_no = this.getValueString("MR_NO");
			String prepare_user = this.getValueString("PREPARE_USER");
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			// 给控件赋值
//			this.setValue("STATION_DESC", tparm.getValue("STATION_CODE", row));
//			this.setValue("MR_NO", tparm.getValue("MR_NO", row));
//			this.setValue("PAT_NAME", tparm.getValue("PAT_NAME", row));
			if (getRadioButton("BUTTON_M").isSelected()) {
				parm.setData("FLG", "N");
			}
			if (getRadioButton("BUTTON_D").isSelected()) {
				parm.setData("FLG", "Y");
				parm.setData("PREPARE_USER", tparm.getValue("PREPARE_USER",
						row));
//				this.setValue("PREPARE_USER", tparm.getValue("PREPARE_USER",
//						row));
			}
			// 给parm赋值来查询细表
			parm.setData("CASE_NO", tparm.getValue("CASE_NO", row));
			parm.setData("MR_NO", mr_no);
			parm.setData("STATION_DESC", station_desc);
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
//			System.out.println("CASE_NO====="+tparm.getValue("CASE_NO", row));
			TParm result = IVAPutMedicineWorkTool.getInstance().querydetail(
					parm);
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
		this.clearValue("STATION_DESC;MR_NO;PAT_NAME");
		this.onInit();
	}

	/*
	 * 单选按钮事件
	 */
	public void onRadion() {
		// 判断单选按钮是否选中
		if (getRadioButton("BUTTON_D").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("PREPARE_USER"))
					.setEnabled(true);
			onClear();
//			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
		}
		if (getRadioButton("BUTTON_M").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("PREPARE_USER"))
					.setEnabled(false);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			this.onClear();
			initPage();
		}

	}

	// /*
	// * 排药完之后保存
	// */
	// public void onSave() {
	// table_m.acceptText();
	// table_d = this.getTTable("TABLE_D");
	// table_m = this.getTTable("TABLE_M");
	// String check_user = this.getValueString("PREPARE_USER");
	// TParm tparm = table_m.getParmValue();
	// // 得到未选中的所有值
	// System.out.println("--------tparm---"+tparm);
	// TParm temParm = new TParm();
	// TParm result=new TParm();
	//		
	//		
	// System.out.println("----tparm.getCount-------**"+tparm.getCount());
	// for (int i = 0; i < tparm.getCount(); i++) {
	//		
	// TParm rowParm = tparm.getRow(i);
	// String flg = rowParm.getValue("SELECT_AVG");
	// // 判断是否选中
	// if ("Y".equals(flg)) {// flg.equals("Y")
	//
	// rowParm.setData("PREPARE_USER", check_user);
	// result = TIOM_AppServer.executeAction(
	// "action.spc.IVADsAciton", "onUpdatePut", rowParm);
	//
	// if (result.getErrCode() < 0) {
	// temParm.setRowData(i, rowParm);
	// }
	// } else {
	// temParm.setRowData(i, rowParm);
	// // temParm.setRowData(i, rowParm, i+1);
	//				
	//				
	// }
	// }
	//		
	// System.out.println("----tparm.getCount-------**"+tparm.getCount());
	// System.out.println(result);
	// System.out.println(temParm);
	// table_d.removeRowAll();
	// table_m.setParmValue(temParm);
	// }

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

	public void onSave() {
//		System.out.println("1111111111111111111111111111");
		table_d = this.getTTable("TABLE_D");
		table_m = this.getTTable("TABLE_M");
		String check_user = this.getValueString("PREPARE_USER");
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
		// 得到未选中的所有值
		TParm result = new TParm();
		int number = 0;
		for (int i = 0; i < tparm.getCount(); i++) {
			TParm rowParm = tparm.getRow(i);
//			System.out.println(rowParm);
			String flg = rowParm.getValue("SELECT_AVG");
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			// 判断是否选中
			if ("Y".equals(flg)) {// flg.equals("Y")
//				TParm  tabel_d_parm = IVAPutMedicineWorkTool.getInstance().querydetail(rowParm);
				rowParm.setData("PREPARE_USER", check_user);
				String sqlM = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
						+ " A.START_DTTM,A.END_DTTM,B.BAR_CODE,B.BATCH_CODE, "
						+ " B.ORDER_DATE,B.ORDER_DATETIME "
						+ " FROM ODI_DSPNM A,ODI_DSPND B "
						+ " WHERE A.CASE_NO='"+rowParm.getValue("CASE_NO")
						+ "' AND A.IVA_FLG='Y' "
						+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
						+ " AND A.CASE_NO=B.CASE_NO "
						+ " AND A.ORDER_NO=B.ORDER_NO "
						+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
						+ " A.START_DTTM AND  A.END_DTTM "
						+ " AND B.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ start_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
						+ end_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS') "
						+ " AND B.IVA_FLG='Y' "
						+ " AND B.PHA_DOSAGE_DATE IS NOT NULL "
						+ " AND B.PREPARE_USER IS NULL "
						+ " AND B.IVA_RETN_USER IS NULL ";
//				System.out.println("sqlM============"+sqlM);
				TParm updateParm = new TParm(TJDODBTool.getInstance().select(sqlM));
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					if(updateParm.getValue("BAR_CODE", j).equals("") 
							|| updateParm.getValue("BAR_CODE", j) == null){
						this.messageBox("药品未打印瓶签");
						return;
					}
					updateParm.setData("PREPARE_USER",j, check_user);
				}
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
							"onUpdatePut", updateParm.getRow(j));
					if (result.getErrCode() < 0) {
						number++;
					}
				}
//				System.out.println("rowParm=======>>>>>>>"+rowParm);
//				result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
//						"onUpdatePut", rowParm);
//				if (result.getErrCode() < 0) {
//					temParm.setRowData(i, rowParm);
//				}
			} 
//			else {
//				temParm.setRowData(i, rowParm);
//				// temParm.setRowData(i, rowParm, i+1);
//			}
		}
		table_d.removeRowAll();
		table_m.removeRowAll();
		if(number <= 0){
			this.messageBox("保存成功");
		}else{
			this.messageBox("保存失败");
		}
		this.clearValue("MR_NO;PAT_NAME");
		save = true;
		this.onQuery();
//		temParm.setCount(table_m.getRowCount());
//		System.out.println("----tparm.getCount-------**" + tparm.getCount());
//		table_m.setParmValue(temParm);
	}
	
	/**
	 * 打印报表
	 */
	public void onPrint(){
		// 判断是分批打印还是全部打印
		String select_all = this.getValueString("SELECT_ALL");
		if("Y".equals(select_all)){
			onPrintAll();
		} else {
			onPrintSingle();
		}
	}
	
	/**
	 * 全部打印报表
	 */
	public void onPrintAll(){
		table_m = getTTable("TABLE_M");
		if(table_m.getRowCount() <= 0){
			messageBox("没有要打印的数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("TITLE", "TEXT", "静配中心排药清单");
		data.setData("DATE", "TEXT", "统计时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// 表格数据
		TParm parm = new TParm();
		TParm result = IVADeploymentTool.getInstance().queryAllData();
		// 遍历表格中的元素
		for (int i = 0; i < result.getCount(); i++) {
			parm.addData("PAT_NAME", result.getValue("PAT_NAME", i));
			parm.addData("MR_NO", result.getValue("MR_NO", i));
			parm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
			parm.addData("BED_NO_DESC", result.getValue("BED_NO_DESC", i));
			parm.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", result.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", result.getValue("FREQ_CHN_DESC", i));
		}
		// 总行数
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// 将表格放到容器中
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}
	
	/**
	 * 分批打印报表
	 */
	public void onPrintSingle(){
		table_d = getTTable("TABLE_D");
		if(table_d.getRowCount() <= 0){
			messageBox("没有要打印的数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("TITLE", "TEXT", "静配中心排药清单");
		data.setData("DATE", "TEXT", "统计时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// 表格数据
		TParm parm = new TParm();
		int rowM = table_m.getSelectedRow();
		TParm tableParmM = table_m.getParmValue().getRow(rowM);
		TParm tableParmD = table_d.getParmValue();
		// 遍历表格中的元素
		for (int i = 0; i < table_d.getRowCount(); i++) {
			parm.addData("PAT_NAME", tableParmM.getValue("PAT_NAME"));
			parm.addData("MR_NO", tableParmM.getValue("MR_NO"));
			parm.addData("STATION_DESC", tableParmM.getValue("STATION_DESC"));
			parm.addData("BED_NO_DESC", tableParmM.getValue("BED_NO_DESC"));
			parm.addData("ORDER_DESC", tableParmD.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParmD.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", tableParmD.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", tableParmD.getValue("FREQ_CHN_DESC", i));
		}
		// 总行数
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// 将表格放到容器中
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}
	
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

}
