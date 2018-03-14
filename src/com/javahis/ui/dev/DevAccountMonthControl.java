package com.javahis.ui.dev;

import java.util.Vector;

import jdo.dev.DevDepTool;
import jdo.dev.DevMMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 设备月结
 * </p>
 * 
 * <p>
 * Description:设备月结
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 20130721
 * </p>
 * 
 * <p>
 * Company: BLUECORE
 * </p>
 * 
 * @author fux
 * 
 * @version 4.0
 */
public class DevAccountMonthControl extends TControl {
	TParm parm = new TParm();

	/**
	 * 初始化
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("DATE", StringTool.getTimestamp(now, "yyyyMMdd"));
		this.clearValue("DEPT_CODE");
		this.clearValue("DEV_CLASS");
		this.clearValue("DEVKIND_CODE");
		this.clearValue("DEVPRO_CODE");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		System.out.println("打印");
		// 统计月份
		parm.setData("DATE", this.getValueString("DATE"));
		// 设备部门
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// 设备部门
		parm.setData("DEV_CLASS", this.getValueString("DEV_CLASS"));
		// 设备部门
		parm.setData("DEVKIND_CODE", this.getValueString("DEVKIND_CODE"));
		// 设备部门
		parm.setData("DEVPRO_CODE", this.getValueString("DEVPRO_CODE"));
		TParm selParm = DevMMTool.getInstance().queryDevMMStock(parm);
		TParm PrintParm = new TParm();
		// 设置表头信息
		TParm printParm = new TParm();
		printParm.setData("HOSP_NAME_T", Operator.getHospitalCHNFullName());
		// 转换时间日期格式
		// REGION_CODE
		// YYYYMM
		// DEPT_CODE
		// DEV_CODE
		// BATCH_SEQ
		// MM_IN_QTY
		// MM_IN_SCRAPAMT
		// MM_OUT_QTY
		// MM_OUT_SCRAPAMT
		// MM_CHECKMODI_QTY
		// MM_CHECKMODI_SCRAPAMT
		// MM_STOCK_QTY
		// MM_STOCK_SCRAPAMT
		// LAST_MM_STOCK_QTY
		// LAST_MM_STOCK_SCRAPAMT
		// MM_INWAREHOUSE_QTY
		// MM_INWAREHOUSE_SCRAPAMT
		// MM_REGRESSGOODS_QTY
		// MM_REGRESSGOODS_SCRAPAMT
		// MM_GIFTIN_QTY
		// MM_GIFTIN_SCRAPAMT
		// MM_GIFTOUT_QTY
		// MM_GIFTOUT_SCRAPAMT
		// MM_WASTE_QTY
		// MM_WASTE_SCRAPAMT
		// MM_SCRAP_VALUE
		int count = selParm.getCount();
		for (int i = 0; i < count; i++) {
			PrintParm.setRowData(count, selParm, i);
			PrintParm.addData("REGION_CODE", selParm.getData("REGION_CODE", i));
			PrintParm.addData("YYYYMM", selParm.getData("YYYYMM", i));
			PrintParm.addData("DEPT_CODE", selParm.getData("DEPT_CODE", i));
			PrintParm.addData("DEV_CODE", selParm.getData("DEV_CODE", i));
			PrintParm.addData("BATCH_SEQ", selParm.getData("BATCH_SEQ", i));
			PrintParm.addData("MM_IN_QTY", selParm.getData("MM_IN_QTY", i));
			PrintParm.addData("MM_IN_SCRAPAMT",
					selParm.getData("MM_IN_SCRAPAMT", i));
			PrintParm.addData("MM_OUT_QTY", selParm.getData("MM_OUT_QTY", i));
			PrintParm.addData("MM_OUT_SCRAPAMT",
					selParm.getData("MM_OUT_SCRAPAMT", i));
			PrintParm.addData("MM_CHECKMODI_QTY",
					selParm.getData("MM_CHECKMODI_QTY", i));
			PrintParm.addData("MM_CHECKMODI_SCRAPAMT",
					selParm.getData("MM_CHECKMODI_SCRAPAMT", i));
			PrintParm.addData("MM_STOCK_QTY",
					selParm.getData("MM_STOCK_QTY", i));
			PrintParm.addData("MM_STOCK_SCRAPAMT",
					selParm.getData("MM_STOCK_SCRAPAMT", i));
			PrintParm.addData("LAST_MM_STOCK_QTY",
					selParm.getData("LAST_MM_STOCK_QTY", i));
			PrintParm.addData("LAST_MM_STOCK_SCRAPAMT",
					selParm.getData("LAST_MM_STOCK_SCRAPAMT", i));
			PrintParm.addData("MM_INWAREHOUSE_QTY",
					selParm.getData("MM_INWAREHOUSE_QTY", i));
			PrintParm.addData("MM_INWAREHOUSE_SCRAPAMT",
					selParm.getData("MM_INWAREHOUSE_SCRAPAMT", i));
			PrintParm.addData("MM_REGRESSGOODS_QTY",
					selParm.getData("MM_REGRESSGOODS_QTY", i));
			PrintParm.addData("MM_REGRESSGOODS_SCRAPAMT",
					selParm.getData("MM_REGRESSGOODS_SCRAPAMT", i));
			PrintParm.addData("MM_GIFTIN_QTY",
					selParm.getData("MM_GIFTIN_QTY", i));
			PrintParm.addData("MM_GIFTIN_SCRAPAMT",
					selParm.getData("MM_GIFTIN_SCRAPAMT", i));
			PrintParm.addData("MM_GIFTOUT_QTY",
					selParm.getData("MM_GIFTOUT_QTY", i));
			PrintParm.addData("MM_GIFTOUT_SCRAPAMT",
					selParm.getData("MM_GIFTOUT_SCRAPAMT", i));
			PrintParm.addData("MM_WASTE_QTY",
					selParm.getData("MM_WASTE_QTY", i));
			PrintParm.addData("MM_WASTE_SCRAPAMT",
					selParm.getData("MM_WASTE_SCRAPAMT", i));
			PrintParm.addData("MM_SCRAP_VALUE",
					selParm.getData("MM_SCRAP_VALUE", i));
		}
		PrintParm.setCount(count);
		// 设置入库单表格信息
		PrintParm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "YYYYMM");
		PrintParm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_IN_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_IN_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_OUT_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_OUT_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_CHECKMODI_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_CHECKMODI_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_STOCK_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_STOCK_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "LAST_MM_STOCK_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "LAST_MM_STOCK_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_INWAREHOUSE_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_INWAREHOUSE_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_REGRESSGOODS_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_REGRESSGOODS_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTIN_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTIN_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTOUT_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTOUT_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_WASTE_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_WASTE_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_SCRAP_VALUE");
		TParm MMParm = new TParm(); 
		MMParm.setData("TABLE", PrintParm.getData());
		openPrintWindow("%ROOT%\\config\\prt\\dev\\DevMMStock.jhw", printParm);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		System.out.println("清空");
		this.initPage();
	}

	/**
	 * 折旧计算
	 */
	public void onDep() {
		// 原值，月折旧值，累计折旧值，现值(待修改)
		String sql = " SELECT A.DEV_CODE,A.DEPT_CODE,A.UNIT_PRICE,A.MDEP_PRICE,"
				+ " B.DEV_CLASS,B.DEVKIND_CODE,B.DEVPRO_CODE,A.INWAREHOUSE_DATE,"
				+ " A.DEVSEQ_NO,A.REGION_CODE"
				+ " FROM DEV_STOCKDD A,DEV_BASE B "
				+ " WHERE A.DEV_CODE = B.DEV_CODE ";
		// 科室
		String deptcode = this.getValueString("DEPT_CODE");
		// 设备分类
		String devclass = this.getValueString("DEV_CLASS");
		// 设备种类
		String devkind = this.getValueString("DEVKIND_CODE");
		// 设备属性
		String devtype = this.getValueString("DEVPRO_CODE");
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);
		// 科室
		if (!deptcode.equals("")) {
			SQL.append("AND A.DEPT_CODE='" + deptcode + "'");
		}
		// 设备属性
		if (!devtype.equals("")) {
			SQL.append("AND B.DEVPRO_CODE='" + devtype + "'");
		}
		// 设备分类
		if (!devclass.equals("")) {
			SQL.append("AND B.DEV_CLASS='" + devclass + "'");
		}
		// 设备种类
		if (!devkind.equals("")) {
			SQL.append("AND B.DEVKIND_CODE='" + devkind + "'");
		}
		SQL.append("ORDER BY  A.DEV_CODE,A.DEVSEQ_NO,A.DEPT_CODE,B.DEVPRO_CODE,B.DEV_CLASS,B.DEVKIND_CODE");
		TParm result = new TParm(this.getDBTool().select(SQL.toString()));
		if (result.getCount() <= 0) {
			messageBox("无折旧数据！");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			String inWarehouseDate = result.getValue("INWAREHOUSE_DATE", i)
					.toString();
			TParm parmDep = DevDepTool.getInstance().selectSeqDevInf(
					getDevBase(result.getValue("DEV_CODE", i)),
					result.getDouble("UNIT_PRICE", i), inWarehouseDate);
			//System.out.println("parmDep" + parmDep);
			Double depPrice = Double.parseDouble(parmDep.getData("DEP_PRICE")
					.toString());
			Double currPrice = Double.parseDouble(parmDep.getData("CURR_PRICE")
					.toString());
			TParm stockDDParm = new TParm();
			// 累计折旧值
			stockDDParm.addData("DEP_PRICE", depPrice); 
			// 现值
			stockDDParm.addData("CURR_PRICE", currPrice);
			// 设备编码
			stockDDParm.addData("DEV_CODE", result.getValue("DEV_CODE", i));
			// DD号
			stockDDParm.addData("DEVSEQ_NO", result.getDouble("DEVSEQ_NO", i));
			// 区域
			stockDDParm.addData("REGION_CODE",
					result.getValue("REGION_CODE", i));
			//System.out.println("stockDDParm===" + stockDDParm);
			TParm parm = DevDepTool.getInstance().UpdateMonthDep(stockDDParm);
			if (parm.getErrCode() < 0) {
				messageBox("折旧计算失败");
				//输出报错
				err(parm.getErrText());
				return;             
			} 
		}
		messageBox("折旧计算成功");
	}

	/**
	 * 月结结转
	 */
	public void onMonth() {
		System.out.println("月结结转");
		// 统计月份
		parm.setData("DATE", this.getValueString("DATE"));
		// 设备部门
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// 设备部门
		parm.setData("DEV_CLASS", this.getValueString("DEV_CLASS"));
		// 设备部门
		parm.setData("DEVKIND_CODE", this.getValueString("DEVKIND_CODE"));
		// 设备部门
		parm.setData("DEVPRO_CODE", this.getValueString("DEVPRO_CODE")); 
		//System.out.println("parm" + parm);     
		TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\DEVMonth.x");  
	}            

	/**
	 * 得到设备基本属性信息
	 * 
	 * @param devCode
	 *            String
	 * @return TParm
	 */
	public TParm getDevBase(String devCode) {
		String SQL = "SELECT * FROM DEV_BASE WHERE DEV_CODE = '" + devCode
				+ "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		return parm;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 获得月结启日
	 * 
	 * @return String
	 */
	public String getMonthStartDate(String monthData) {
		if (monthData.trim().length() <= 0) {
			return "";
		}
		if (monthData.substring(4, 6).equals("01")) {
			return this.valiDataMonthDate(monthData);
		}
		int month = Integer.parseInt(monthData.substring(4, 6)) - 1;
		String m = String.valueOf(month);
		if (m.length() == 1) {
			m = "0" + m;
		}
		String result = monthData.substring(0, 4) + m;
		return result;
	}

	/**
	 * 验证月结启日
	 * 
	 * @param monthData
	 *            String
	 * @return String
	 */
	public String valiDataMonthDate(String monthData) {
		if (monthData.trim().length() <= 0) {
			return "";
		}
		int year = Integer.parseInt(monthData.substring(0, 4)) - 1;
		String result = String.valueOf(year) + "12";
		return result;
	}

	/**
	 * 获得月结弃日
	 * 
	 * @return String
	 */
	public String getMonthEndDate(String monthData) {
		return monthData;
	}

	/**
	 * 编辑统计月份
	 * 
	 * @param dateStr
	 *            String
	 * @return String
	 */
	public String getDataValue(String dateStr) {
		if (dateStr.trim().length() <= 0) {
			return "";
		}
		String[] str = dateStr.split("\\/");
		String strDate = "";
		for (int i = 0; i < str.length; i++) {
			strDate += str[i];
		}
		return strDate;
	}
}