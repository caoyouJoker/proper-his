package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.util.Date;

import jdo.iva.IVADeploymentTool;
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
import com.javahis.util.mainServer;

/**
 * <p>
 * Title: 静配中心配液Control
 * </p>
 * 
 * <p>
 * Description: 静配中心配液Control
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
public class IVADeploymentControl extends TControl {
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

	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	public void onInit() {
		super.onInit();
		TParm parm = new TParm();
		TParm result = IVADeploymentTool.getInstance().onqueryPatch(parm);
		String values = "[id,name]";
		for (int i = 0; i < result.getCount(); i++) {
			values += ",[" + result.getValue("BATCH_CODE", i) + ","
					+ result.getValue("BATCH_CHN_DESC", i) + "]";
		}
		String endvalues = "[" + values + "]";
		getTComboBox("BATCH_CODE").setStringData(endvalues);

		initPage();
	}

	/*
	 * 初始化时间和调配人员
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
//		this.setValue("IVA_DEPLOY_USER", Operator.getID());
		
	}

	/*
	 * 查询已调配药品or未调配药品信息
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
		// 批次代码
		String batch_code = this.getValueString("BATCH_CODE");

		String mr_no = this.getValueString("MR_NO");
		String iva_deploy_user = this.getValueString("IVA_DEPLOY_USER");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// 判断前台界面得到的值是否为空
		if (batch_code != null && !"".equals(batch_code)) {
			parm.setData("BATCH_CODE", batch_code);
		}else{
			this.messageBox("请输入批次!");
			return;
		}
		if (barcode != null && !"".equals(barcode)) {
			parm.setData("BAR_CODE", barcode);
		}
		if (station_code != null && !"".equals(station_code)) {
			parm.setData("STATION_CODE", station_code);
		}
		if (mr_no != null && !"".equals(mr_no)) {
			parm.setData("MR_NO", mr_no);
		}

		if (start_time != null && !"".equals(start_time)) {
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间!");
			return;
		}
		if (end_time != null && !"".equals(end_time)) {
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
		}else{
			this.messageBox("请输入查询区间!");
			return;
		}
		TParm result = new TParm();
		// 判断单选按钮是否选中
		if (getRadioButton("BUTTON_M").isSelected()) {
			parm.setData("FLG", "N");
		}
		if (getRadioButton("BUTTON_D").isSelected()) {
			parm.setData("FLG", "Y");
			if (iva_deploy_user != null && !"".equals(iva_deploy_user)) {
				parm.setData("IVA_DEPLOY_USER", iva_deploy_user);
			}
		}
		result = IVADeploymentTool.getInstance().queryInfo(parm);
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
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		// 得到选中行索引
		int row = table_m.getSelectedRow();
		// 得到table的值
		TParm tparm = table_m.getParmValue();
		TParm parm = new TParm();
		if (row != -1) {
//			this.setValue("STATION_CODE", tparm.getValue("STATION_CODE", row));
//			this.setValue("MR_NO", tparm.getValue("MR_NO", row));
//			this.setValue("PAT_NAME", tparm.getValue("PAT_NAME", row));
			if (getRadioButton("BUTTON_M").isSelected()) {
				parm.setData("FLG", "N");
			}
			if (getRadioButton("BUTTON_D").isSelected()) {
				parm.setData("FLG", "Y");
//				this.setValue("IVA_DEPLOY_USER", tparm.getValue(
//						"IVA_DEPLOY_USER", row));
			}
			String barcode = this.getValueString("BAR_CODE");
			// 批次代码
			String batch_code = this.getValueString("BATCH_CODE");
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			parm.setData("BATCH_CODE", batch_code);
			parm.setData("BAR_CODE", barcode);
			parm.setData("STATION_CODE", tparm.getValue("STATION_CODE", row));
			parm.setData("MR_NO", tparm.getValue("MR_NO", row));
			parm.setData("IVA_DEPLOY_USER", tparm.getValue(
					"IVA_DEPLOY_USER", row));
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
			parm.setData("CASE_NO", tparm.getValue("CASE_NO", row));
			TParm result = IVADeploymentTool.getInstance().querydetail(parm);
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
		this.clearValue("STATION_CODE;IVA_DEPLOY_USER;BAR_CODE;MR_NO;PAT_NAME");

	}

	/*
	 * 单选按钮事件
	 */
	public void onRadion() {
		if (getRadioButton("BUTTON_D").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("IVA_DEPLOY_USER"))
					.setEnabled(true);
		}
		if (getRadioButton("BUTTON_M").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("IVA_DEPLOY_USER"))
					.setEnabled(false);
		}
		initPage();
		onClear();
	}

	/*
	 * 调配完之后保存
	 */
	public void onSave() {
		String check_user = this.getValueString("IVA_DEPLOY_USER");
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
			this.setValue("IVA_DEPLOY_USER", inParm.getValue("USER_ID"));
		}
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// 得到未选中的所有值
		TParm result = new TParm();
		int number = 0;
//		System.out.println("count====="+tparm.getCount());
		for (int i = 0; i < tparm.getCount(); i++) {
			TParm rowParm = tparm.getRow(i);
			String flg = rowParm.getValue("SELECT_AVG");
			// 判断是否选中
			if ("Y".equals(flg)) {// flg.equals("Y")
//				rowParm.setData("IVA_DEPLOY_USER", check_user);
//				rowParm.setData("BATCH_CODE", this.getValueString("BATCH_CODE"));
//				System.out.println("rowParm>>>>>>>>>>>"+rowParm);
				String sqlM = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
						+ " A.START_DTTM,A.END_DTTM,B.BATCH_CODE,B.BAR_CODE, "
						+ " B.ORDER_DATE,B.ORDER_DATETIME "
						+ " FROM ODI_DSPNM A,ODI_DSPND B "
						+ " WHERE A.PREPARE_CHECK_TIME BETWEEN TO_DATE('"
						+ start_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
						+ end_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS') "
						+ " AND A.IVA_FLG='Y' "
						+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
						+ " AND A.CASE_NO=B.CASE_NO "
						+ " AND A.ORDER_NO=B.ORDER_NO "
						+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
						+ " A.START_DTTM AND  A.END_DTTM "
						+ " AND B.CASE_NO='"+rowParm.getValue("CASE_NO")
						+ "' AND B.BATCH_CODE='"+this.getValueString("BATCH_CODE")
						+ "' AND (B.DC_DATE IS NULL OR SUBSTR( TO_CHAR(B.DC_DATE,'YYYYMMDDHH24MISS'),1,12) "
//						+ " >= B.ORDER_DATE || B.ORDER_DATETIME) "
						+ " >= (SELECT B.ORDER_DATE "
						+ " || G.DCCHECK_TIME FROM ODI_BATCHTIME G WHERE G.BATCH_CODE='"
						+this.getValueString("BATCH_CODE")+"')) "
						+ " AND B.IVA_FLG='Y' "
						+ " AND B.PREPARE_CHECK_USER IS NOT NULL "
						+ " AND B.IVA_DEPLOY_USER IS NULL "
						+ " AND B.IVA_RETN_USER IS NULL ";
//				System.out.println("sqlM============"+sqlM);
				TParm updateParm = new TParm(TJDODBTool.getInstance().select(sqlM));
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					updateParm.setData("IVA_DEPLOY_USER",j, this.getValueString("IVA_DEPLOY_USER"));
				}
//				System.out.println("updateParm="+updateParm);
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
							"onUpdateDep", updateParm.getRow(j));
					if (result.getErrCode() < 0) {
						number++;
					}
				}
			} 
		}
		if(number <= 0){
			this.messageBox("保存成功");
		}else{
			this.messageBox("保存失败");
		}
		table_d.removeRowAll();
		table_m.removeRowAll();
		this.clearValue("BAR_CODE;MR_NO;PAT_NAME");
		save=true;
		this.onQuery();
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

	public void onTcomBox() {
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		// 查询是否有未退药
		String batch_code = this.getValueString("BATCH_CODE");
		TParm result = IVADeploymentTool.getInstance().queryByBatchCode(batch_code);
		this.setValue("IVA_TIME", result.getValue("IVA_TIME", 0).substring(0, 2)
										+":"
										+result.getValue("IVA_TIME", 0).substring(2, 4));
		this.setValue("TREAT_START_TIME", result.getValue("TREAT_START_TIME", 0).substring(0, 2)
												+":"
												+result.getValue("TREAT_START_TIME", 0).substring(2, 4));
		this.setValue("TREAT_END_TIME", result.getValue("TREAT_END_TIME", 0).substring(0, 2)
												+":"
												+result.getValue("TREAT_END_TIME", 0).substring(2, 4));
		if (getRadioButton("BUTTON_M").isSelected()) {
			// 封装前台得到的值
			TParm parm = new TParm();
			// 得到前台控件值
			String station_code = this.getValueString("STATION_CODE");
			String barcode = this.getValueString("BAR_CODE");
			String mr_no = this.getValueString("MR_NO");
			String iva_deploy_user = this.getValueString("IVA_DEPLOY_USER");
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			// 判断前台界面得到的值是否为空
			if (batch_code != null && !"".equals(batch_code)) {
				parm.setData("BATCH_CODE", batch_code);
			}else{
				this.messageBox("请输入批次!");
				return;
			}
			if (barcode != null && !"".equals(barcode)) {
				parm.setData("BAR_CODE", barcode);
			}
			if (station_code != null && !"".equals(station_code)) {
				parm.setData("STATION_CODE", station_code);
			}
			if (mr_no != null && !"".equals(mr_no)) {
				parm.setData("MR_NO", mr_no);
			}
	
			if (start_time != null && !"".equals(start_time)) {
				parm.setData("START_TIME", start_time.toString().substring(0, 19));
			}else{
				this.messageBox("请输入查询区间!");
				return;
			}
			if (end_time != null && !"".equals(end_time)) {
				parm.setData("END_TIME", end_time.toString().substring(0, 19));
			}else{
				this.messageBox("请输入查询区间!");
				return;
			}
			
			TParm checkParm = this.checkDate(parm);
			if(checkParm.getInt("COUNTS", 0)>0){
	//			if (this.messageBox("提示信息 Tips",
	//					"医嘱已停用 , 请执行退药登记",
	//					this.YES_NO_OPTION) != 0){
	//				return;
	//			}
				this.messageBox("医嘱已停用 , 请执行退药登记！");
	//				((TMenuItem) getComponent("save")).setEnabled(false);
	//				return;
			}
		}
	}

	public void onActionText() {
		onQuery();
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
	
	public TParm checkDate(TParm parm){
		Timestamp date = StringTool.getTimestamp(new Date());
		String dateStr = date.toString();
		String dccheck_time = dateStr.substring(0,19);
		parm.setData("CHECK_DATE", dccheck_time);
		TParm result = IVADeploymentTool.getInstance().queryMDByDCTime(parm);
		return result;
	}

}
