package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.clp.CLPBillTool;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;

import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 临床路径费用分析
 * </p>
 * 
 * <p>
 * Description: 临床路径费用分析
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
 * @author not attributable
 * @version 1.0
 */
public class CLPBillControl extends TControl {
	public CLPBillControl() {

	}

	// 标准总费用
	private double totalStandardFee = 0;
	// 实际总费用
	private double totalFactFee = 0;
	// 病人住院号
	private String case_no;

	/**
	 * 页面初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		TParm sendParm = (TParm) this.getParameter();
		case_no = sendParm.getValue("CASE_NO");
		this.setValue("CLNCPATH_CODE", sendParm.getValue("CLNCPATH_CODE"));
		// =========pangben 2012-6-4 start
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("SCHD_CODE");
		combo_schd.setCaseNo(case_no);
		combo_schd.setClncpathCode(sendParm.getValue("CLNCPATH_CODE"));
		combo_schd.onQuery();
		this.setValue("SCHD_CODE", sendParm.getValue("DURATION_CODE"));// 当前时程
		// System.out.println("-------------case_no"+case_no);
		initBasicInfo();
		// 初始化治疗天数
		initSchdDay();
		onQuery();
	}

	/**
	 * 初始化病人信息
	 */
	private void initBasicInfo() {
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		TParm result = CLPBillTool.getInstance().selectPatientData(selectParm);
		// System.out.println("查询出的基础数据:" + result);
		this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
		this.setValue("BED_NO", result.getValue("BED_NO", 0));
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		// this.setValue("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));
		// this.setValue("SCHD_CODE", result.getValue("SCHD_CODE", 0));
		this.setValue("AVERAGECOST", result.getValue("AVERAGECOST", 0));
	}

	/**
	 * 初始化治疗天数
	 */
	private void initSchdDay() {
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));
		selectParm.setData("SCHD_CODE", this.getValue("SCHD_CODE")+"%");
		TParm result = CLPBillTool.getInstance().selectDurationSchdDay(
				selectParm);
		String schdDay = "";
		if (result.getCount() > 0) {
			schdDay = result.getValue("SCHD_DAY", 0);
		}
		this.setValue("SCHD_DAY", schdDay);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 初始化总费用信息
		this.totalFactFee = 0;
		this.totalStandardFee = 0;// totalVariance
		// 费用分析
		queryFeeData();
		// 处理总费用和标准费用信息
		this.setValue("totalFactFee", StringTool.round(totalFactFee, 2) + "");
		this.setValue("totalStandardFee", StringTool.round(totalStandardFee, 2) + "");
		this.setValue("totalVariance", Math.abs(StringTool.round(totalFactFee
				- totalStandardFee, 2))
				+ "");
		// average charge
		TParm selectTParm = new TParm();
		selectTParm.setData("CLNCPATH_CODE", this
				.getValueString("CLNCPATH_CODE"));
		TParm avgParm = CLPBillTool.getInstance().getaverageCharge(selectTParm);
		this.setValue("AVERAGECOST", avgParm.getValue("AVERAGECOST", 0));
	}

	/**
	 * 导出excel
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有导出数据");
			return;
		}
		// System.out.println("表头"+table.getHeader());
		//
		// System.out.println("表数据"+table.getShowParmValue());
		ExportExcelUtil.getInstance().exportExcel(table, "临床路径费用分析统计表");
	}

	/**
	 * 费用分析
	 */
	private void queryFeeData() {
		TParm selectTParm = getSelectCondition();
		// System.out.println("查询数据:" + selectTParm);
		TParm result = CLPBillTool.getInstance().selectBillData(selectTParm);
		TParm tempParm=new TParm();
		// System.out.println("查询结果：" + result);
		TTable table = (TTable) this.getComponent("TABLE");
		String headerStr = "类别,100;时程,100,SCHD_CODE;总费用,100;"
				+ getChargeHeader();
		table.setHeader(headerStr);
		//table.setItem("SCHD_CODE");
		String parmStr = "SCHD_TYPE_DESC;DURATION_CHN_DESC;TOT;";
		for (int i = 1; i <= 9; i++) {
			parmStr += "REXP_0" + i + ";";
		}
		//=======pangben 2012-6-14
		// 20110701 luhai modify 将原有的30列改成20列，实际使用的费用列只有20列，多出的费用列不启用
		// 必须保证标题的数据列=实际数据列
		for (int i = 10; i <= 20; i++) {
			parmStr += "REXP_" + i + ";";
		}
		table.setParmMap(parmStr);
		// 设置列锁
		StringBuffer collockstr = new StringBuffer();
		for (int i = 0; i < 24; i++) {
			collockstr.append(i);
			if (i < 23) {
				collockstr.append(",");
			}
		}
		String sql=" SELECT DURATION_CODE,DURATION_CHN_DESC FROM  CLP_DURATION WHERE LEAF_FLG='N'";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		// 累计数据
		String shcdCode = "";
		if (null != result.getValue("SCHD_CODE", 0)) {
			shcdCode = result.getValue("SCHD_CODE", 0).substring(0,1);
			String []name= result.getNames();
			//获得一条空数据 累计操作时使用
			for (int i = 0; i < name.length; i++) {
				tempParm.addData(name[i], "");
			}
		}
		double standardFee=0.00;//累计每个时程的标准金额 实际金额
		double factFee=0.00; 
		tempParm.setCount(1);
		//===========pangben 2012-6-14 start 修改显示 累计时程获得每一个时程父节点的总金额
		TParm showParm=new TParm();
		int index=0;
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("SCHD_CODE", i).contains(shcdCode)) {
				if ("1".equals(result.getValue("SCHD_TYPE", i))) {
					standardFee+=result.getDouble("TOT", i);
					this.totalStandardFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "标准");
				} else if ("2".equals(result.getValue("SCHD_TYPE", i))) {
					factFee+=result.getDouble("TOT", i);
					this.totalFactFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "实际");
				}
				showParm.setRowData(index, result, i);
			}else{
				String name="";
				for (int j = 0; j < selparm.getCount(); j++) {
					if (selparm.getValue("DURATION_CODE",j).equals(shcdCode)) {
						name=selparm.getValue("DURATION_CHN_DESC",j);
						break;
					}
				}
				
				tempParm.setData("SCHD_TYPE_DESC",0,"累计标准");
				tempParm.setData("TOT",0,StringTool.round(standardFee, 2));//标准
				tempParm.setData("DURATION_CHN_DESC",0,name);//显示父节点数据
				showParm.setRowData(index, tempParm,0);
				index++;
				tempParm.setData("SCHD_TYPE_DESC",0,"累计实际");
				tempParm.setData("TOT",0,StringTool.round(factFee, 2));//标准
				tempParm.setData("DURATION_CHN_DESC",0,name);//显示父节点数据
				showParm.setRowData(index, tempParm,0);
				index++;
				standardFee=0.00;//充值获得金额
				factFee=0.00;
				shcdCode=result.getValue("SCHD_CODE", i).substring(0,1);
				if ("1".equals(result.getValue("SCHD_TYPE", i))) {
					standardFee+=result.getDouble("TOT", i);
					this.totalStandardFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "标准");
				} else if ("2".equals(result.getValue("SCHD_TYPE", i))) {
					factFee+=result.getDouble("TOT", i);
					this.totalFactFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "实际");
				}
				showParm.setRowData(index, result,i);
			}
			index++;
			if (i==result.getCount()-1) {
				shcdCode=result.getValue("SCHD_CODE", i).substring(0,1);
				String name="";
				for (int j = 0; j < selparm.getCount(); j++) {
					if (selparm.getValue("DURATION_CODE",j).equals(shcdCode)) {
						name=selparm.getValue("DURATION_CHN_DESC",j);
						break;
					}
				}
				tempParm.setData("SCHD_TYPE_DESC",0,"累计标准");
				tempParm.setData("TOT",0,StringTool.round(standardFee, 2));//标准
				tempParm.setData("DURATION_CHN_DESC",0,name);//显示父节点数据
				showParm.setRowData(index, tempParm,0);
				index++;
				tempParm.setData("SCHD_TYPE_DESC",0,"累计实际");
				tempParm.setData("TOT",0,StringTool.round(factFee, 2));//标准
				tempParm.setData("DURATION_CHN_DESC",0,name);//显示父节点数据
				showParm.setRowData(index, tempParm,0);
				index++;
			}
		}
		showParm.setCount(index);
		table.setLockColumns(collockstr.toString());
		// this.callFunction("UI|TABLE|setParmValue",
		// getTableParmWithSelectData(result));
		this.callFunction("UI|TABLE|setParmValue", showParm);
		// TTable table = (TTable)this.getComponent("TABLE");

	}

	/**
	 * 处理查询的table
	 * 
	 * @param parm
	 *            TParm
	 */
	private TParm getTableParmWithSelectData(TParm parm) {
		TParm returnparm = new TParm();
		int totalCount = 0;
		double tot = 0;
		double standard = 0;
		String schd_code = "";
		for (int i = 0; i < parm.getCount(); i++) {
			boolean isadd = false;
			TParm rowparm = parm.getRow(i);
			String totstr = rowparm.getValue("TOT");
			double rowtot = Double.parseDouble(totstr);
			returnparm.addData("SCHD_TYPE_DESC", rowparm
					.getValue("SCHD_TYPE_DESC"));
			returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
			returnparm.addData("TOT", rowtot);
			addChargeDetail(returnparm, rowparm);
			schd_code = rowparm.getValue("SCHD_CODE");
			if (i == 0) {
				tot = 0;
				standard = 0;

			}
			if (!parm.getValue("SCHD_CODE", (i + 1)).equals(schd_code)
					&& i != 0) {
				isadd = true;
			}
			if ("1".equals(rowparm.getValue("SCHD_TYPE"))) {
				// System.out.println("标准----------------");
				standard += rowtot;
				// 处理总标准费用
				totalStandardFee += rowtot;
			} else if ("2".equals(rowparm.getValue("SCHD_TYPE"))) {
				// System.out.println("实际----------------");
				tot += rowtot;
				// 处理实际总费用
				totalFactFee += rowtot;
			}
			// 到最后一行时也需要统计下标准和实际的总量
			if (i != 0 && i == (parm.getCount() - 1)) {
				isadd = true;
			}
			// 计算总条数
			totalCount++;
			if (isadd) {
				returnparm.addData("SCHD_TYPE_DESC", "累计标准");
				returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
				returnparm.addData("TOT", standard);
				addChargeDetail(returnparm);
				returnparm.addData("SCHD_TYPE_DESC", "累计实际");
				returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
				returnparm.addData("TOT", tot);
				addChargeDetail(returnparm);
				tot = 0;
				standard = 0;
				// 新加入数据也要计算总条数
				totalCount += 2;// 标准和实际总共两条数据
			}
		}
		returnparm.setCount(totalCount);
		return returnparm;
	}

	/**
	 * 添加费用明细，若没有添加空
	 * 
	 * @param chargeParm
	 *            TParm
	 */
	private void addChargeDetail(TParm toParm, TParm chargeParm) {
		String tmpcol = "REXP_0";
		for (int i = 1; i <= 9; i++) {
			toParm.addData((tmpcol + i), chargeParm.getValue(tmpcol + i));
		}
		tmpcol = "REXP_";
		for (int i = 10; i <= 30; i++) {
			toParm.addData((tmpcol + i), chargeParm.getValue(tmpcol + i));
		}
	}

	private void addChargeDetail(TParm toParm) {
		addChargeDetail(toParm, new TParm());
	}

	private String getChargeHeader() {
		StringBuffer header = new StringBuffer();
		TParm parm = CLPBillTool.getInstance().getChargeHeader(new TParm());
		TParm bilParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, "
										+ "CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, "
										+ "CHARGE16, CHARGE17, CHARGE18, CHARGE19, CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, "
										+ "CHARGE26, CHARGE27, CHARGE28, CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS' "));
		// ============pangben 2012-6-4 修改表头数据start
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (null != bilParm.getValue("CHARGE0" + i, 0)
						&& bilParm.getValue("CHARGE0" + i, 0).length() > 0) {
					for (int j = 0; j < parm.getCount(); j++) {
						if (bilParm.getValue("CHARGE0" + i, 0).equals(
								parm.getValue("ID", j))) {
							header.append(parm.getValue("CHN_DESC", j)
									+ ",100;");
							break;
						}

					}
				}
			} else {
				if (null != bilParm.getValue("CHARGE" + i, 0)
						&& bilParm.getValue("CHARGE" + i, 0).length() > 0) {
					for (int j = 0; j < parm.getCount(); j++) {
						if (bilParm.getValue("CHARGE" + i, 0).equals(
								parm.getValue("ID", j))) {
							header.append(parm.getValue("CHN_DESC", j)
									+ ",100;");
							break;
						}

					}
				}
			}
			// ============pangben 2012-6-4 stop
		}
		return header.toString().substring(0, header.toString().length() - 1);
	}

	private TParm getSelectCondition() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		this.putParamWithObjNameForQuery("CLNCPATH_CODE", parm);
		// this.putParamWithObjNameForQuery("SCHD_CODE", parm);
		String objstr = this.getValueString("SCHD_CODE");
		if (objstr != null && objstr.length() > 0) {
			// 参数值与控件名相同
			parm.setData("SCHD_CODE", objstr.trim() + "%");
		}
		this.putParamWithObjNameForQuery("SCHD_DAY", parm);
		parm.setData("REGION_CODE", this.getBasicOperatorMap().get(
				"REGION_CODE"));
		return parm;
	}

	public void onClear() {
		List<String> list = new ArrayList<String>();
		// list.add("CLNCPATH_CODE");
		list.add("SCHD_CODE");
		list.add("SCHD_DAY");
		// list.add("AVERAGECOST");
		// list.add("");
		// list.add("");
		// list.add("");
		// list.add("");
		this.clearInput(list);
	}

	/**
	 * 将表格的对应单元格设置成可写，其他的设置成不可写
	 * 
	 * @param tableName
	 *            String
	 * @param rowNum
	 *            int
	 * @param columnNum
	 *            int
	 */
	private void setTableEnabled(String tableName, int rowNum, int columnNum) {
		TTable table = (TTable) this.getComponent(tableName);
		int totalColumnMaxLength = table.getColumnCount();
		int totalRowMaxLength = table.getRowCount();
		// System.out.println("列总数：" + totalColumnMaxLength + "行总数:" +
		// totalRowMaxLength);
		// 锁列
		String lockColumnStr = "";
		for (int i = 0; i < totalColumnMaxLength; i++) {
			if (!(i + "").equals(columnNum + "")) {
				lockColumnStr += i + ",";
			}
		}
		lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
		// System.out.println("锁列串：" + lockColumnStr);
		table.setLockColumns(lockColumnStr);
		// 锁行
		String lockRowStr = "";
		for (int i = 0; i < totalRowMaxLength; i++) {
			if (!(i + "").equals(rowNum + "")) {
				lockRowStr += i + ",";
			}
		}
		// System.out.println("锁行串前：" + lockRowStr + "总行" + totalRowMaxLength);
		lockRowStr = lockRowStr.substring(0, ((lockRowStr.length() - 1) < 0 ? 0
				: (lockRowStr.length() - 1)));
		// System.out.println("锁行串：" + lockRowStr);
		if (lockRowStr.length() > 0) {
			table.setLockRows(lockRowStr);
		}

	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		objstr = objstr;
		parm.setData(paramName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		// System.out.println(objstr);
		objstr = objstr;
		// 参数值与控件名相同
		parm.setData(objName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamLikeWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			parm.setData(paramName, objstr);
		}

	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamLikeWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr.trim() + "%";
			// 参数值与控件名相同
			parm.setData(objName, objstr);
		}
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm) {
		putParamWithObjNameForQuery(objName, parm, objName);
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 * @param paramName
	 *            String
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			// 参数值与控件名相同
			parm.setData(paramName, objstr.trim());
		}
	}

	/**
	 * 检查控件是否为空
	 * 
	 * @param componentName
	 *            String
	 * @return boolean
	 */
	private boolean checkComponentNullOrEmpty(String componentName) {
		if (componentName == null || "".equals(componentName)) {
			return false;
		}
		String valueStr = this.getValueString(componentName);
		if (valueStr == null || "".equals(valueStr)) {
			return false;
		}
		return true;
	}

	/**
	 * 得到指定table的选中行
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * 数字验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		int total = parm.getCount();
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * 根据Operator得到map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getIP());
		return map;
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @param dataFormatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateStr(String dataFormatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataFormatStr);
		return datestr;
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @return String
	 */
	private String getCurrentDateStr() {
		return getCurrentDateStr("yyyyMMdd");
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 拷贝TParm
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		for (int i = 0; i < from.getNames().length; i++) {
			to.addData(from.getNames()[i], from.getValue(from.getNames()[i],
					row));
		}
	}

	/**
	 * 克隆对象
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm cloneTParm(TParm from) {
		TParm returnTParm = new TParm();
		for (int i = 0; i < from.getNames().length; i++) {
			returnTParm.setData(from.getNames()[i], from.getValue(from
					.getNames()[i]));
		}
		return returnTParm;
	}

	/**
	 * 处理TParm 里的null的方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNullVector(TParm parm, String keyStr, Class type) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getData(keyStr, i) == null) {
				// System.out.println("处理为空情况");
				TNull tnull = new TNull(type);
				parm.setData(keyStr, i, tnull);
			}
		}
	}

	/**
	 * 处理TParm 里null值方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNull(TParm parm, String keyStr, Class type) {
		if (parm.getData(keyStr) == null) {
			// System.out.println("处理为空情况");
			TNull tnull = new TNull(type);
			parm.setData(keyStr, tnull);
		}
	}

	/**
	 * 清空集合中对应的输入框的值
	 * 
	 * @param inputNames
	 *            List
	 */
	private void clearInput(List<String> inputNames) {
		for (String inputstr : inputNames) {
			this.setValue(inputstr, "");
		}
	}

}
