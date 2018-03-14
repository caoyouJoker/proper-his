package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.ind.INDEAntibacterialsTool;
import jdo.ind.INDRadStatisticsTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * 
 * <p>
 * Title: 院内急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Description: 院内急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangm 2013.3.18
 * @version 1.0
 */
public class INDEAntibacterialsControl extends TControl{
	private TTable table = new TTable();
	private String header = ""; // 存放表头
	private String parmMap = ""; // 存放表头Map
	private String align = "";//列数据对齐方式
	private String reportFlg = ""; // 报表类型标记

	private StringBuffer startTime = new StringBuffer(); // 开始日期
	private StringBuffer endTime = new StringBuffer(); // 截止日期
	
	
	/**
	 * 界面初始化
	 */
	public void onInit() {
		super.onInit();
		this.resetDate(); // 初始化时间控件
		this.initTable(); // 初始化table
		// this.initPopeDem(); //初始化权限
	}

	/**
	 * 权限初始化
	 */
	// private void initPopeDem() {
	// // 组长权限
	// if (this.getPopedem("LEADER")) {
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// // 全院权限
	// if (this.getPopedem("ALL")) {
	// // callFunction("UI|DEPT_CODE|setEnabled",true);
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// }

	/**
	 * table初始化
	 */
	private void initTable() {
		table = (TTable) this.getComponent("tab_Statistics");
		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());
	}

	// table填充方法
	private void fillTable(TParm parm) {
		table = new TTable();
		table = (TTable) this.getComponent("tab_Statistics");
		table.removeRowAll();

		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());

		TParm result = new TParm();
		result = INDEAntibacterialsTool.getInstance().selectReportData(parm);

		if (result.getCount() < 0) {
			this.messageBox("没有符合条件的数据！");
			return;
		}
		table.setParmValue(result);
	}

	// 查询按钮触发事件
	public void onQuery() {
		if (!this.checkConditions()) {
			return;
		}
		TParm parm = new TParm();
		parm = this.encParameter(); // 获得查询条件
		fillTable(parm);
	}

	// 报表导出按钮触发事件
	public void onExport() {
		TTable expTable = (TTable) callFunction("UI|tab_Statistics|getThis");
		if (expTable.getRowCount() <= 0) {
			messageBox("无导出资料");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(expTable, reportFlg);
	}

	// 清空按钮触发事件
	public void onClear() {
		this.resetDate();
	}

	// 重置时间控件
	private void resetDate() {
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("txt_StartDate", now);
		this.setValue("txt_StartTime", StringTool.getTimestamp("00:00:00",
				"HH:mm:ss"));
		this.setValue("txt_EndDate", now);
		this.setValue("txt_EndTime", now);
	}

	// 获得表头
	private Map getTableHeader() {
		header = "日期,90,BILL_DATE;病案号,100,MR_NO;姓名,80,PAT_NAME;药品编码,100,ORDER_CODE;药品名称,180,ORDER_DESC;规格,80,SPECIFICATION;单位,50,UNIT_CHN_DESC;零售价,80,double,#########0.00,OWN_PRICE;数量,80,double,#########0,DOSAGE_QTY;零售金额,80,double,#########0.00,OWN_AMT;下达医生,80,USER_NAME";
		parmMap = "BILL_DATE;MR_NO;PAT_NAME;ORDER_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;OWN_PRICE;DOSAGE_QTY;OWN_AMT;USER_NAME";
		align = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,left";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("header", header);
		map.put("parmMap", parmMap);
		map.put("align", align);
		return map;
	}

	private boolean checkConditions() {
		if (getValueString("txt_StartDate").equals("")
				|| getValueString("txt_EndDate").equals("")) {
			this.messageBox("请选择起讫日期！");
			return false;
		}
		return true;
	}

	// 封装查询参数
	private TParm encParameter() {
		TParm parm = new TParm(); // 参数列表

		startTime = new StringBuffer();
		endTime = new StringBuffer();

		String str = getValueString("txt_StartDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		startTime.append(str);
		startTime.append(" ");

		if (getValueString("txt_StartTime").equals("")) {
			startTime.append("00:00:00");
		} else {
			startTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_StartTime")), "HH:mm:ss"));
		}

		str = getValueString("txt_EndDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		endTime.append(str);
		endTime.append(" ");

		if (getValueString("txt_EndTime").equals("")) {
			endTime.append("23:59:59");
		} else {
			endTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_EndTime")), "HH:mm:ss"));
		}

		parm.setData("DATE_START", startTime.toString()); // 开始时间
		parm.setData("DATE_END", endTime.toString()); // 终止时间

		reportFlg = "selectEAnti"; // 报表类型标记

		parm.setData("REPORTFLG", reportFlg);
		return parm;
	}
	
	
	
}
