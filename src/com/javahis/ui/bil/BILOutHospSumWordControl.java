package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>
 * Title: 出院患者医疗费用总表
 * </p>
 * 
 * <p>
 * Description: 出院患者医疗费用总表
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
 * @author caowl 2012.07.09
 * @version 1.0
 */
public class BILOutHospSumWordControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}

	String[] chargName = { "CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
			"CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08", "CHARGE09",
			"CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13", "CHARGE14",
			"CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18", "CHARGE19",
			"CHARGE20" };
	private TParm parmName; // 费用名称
	private TParm parmCode; // 费用代码
	// 切帐日期
	Timestamp startDate;
	Timestamp bill_date;

	/**
	 * 初始化界面
	 */
	public void initPage() {
		// 初始化时间
		startDate = SystemTool.getInstance().getDate();
		bill_date = getDateForInit(queryFirstDayOfLastMonth(StringTool.
                getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		setValue("S_DATE", startDate);
		setValue("BILL_DATE", bill_date);
		String sql = SYSSQL.getBillRecpparm(); // 获得费用代码
		sql += " WHERE ADM_TYPE='I'";
		parmCode = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();
		if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
			this.messageBox("设置费用字典有问题");
			return;
		}
		// 获得费用名称
		sql = "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		parmName = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(getHeard());
	}

	/**
	 * 添加表头
	 * 
	 * @return TParm
	 */
	private TParm getHeard() {
		TParm heardParm = new TParm();
		heardParm.addData("DEPT_CHN_DESC", "科室名称");
		heardParm.addData("STATION_DESC", "病区");
		heardParm.addData("TOT_AMT", "合计金额");
		for (int i = 0; i < chargName.length; i++) {
			heardParm.addData(chargName[i], getChargeName(parmName, parmCode
					.getValue(chargName[i], 0)));
		}
		heardParm.setCount(1);
		return heardParm;
	}

	/**
	 * 获得费用名称
	 * 
	 * @param parmName
	 *            TParm
	 * @param chargeCode
	 *            String
	 * @return String
	 */
	private String getChargeName(TParm parmName, String chargeCode) {
		for (int i = 0; i < parmName.getCount(); i++) {
			if (parmName.getValue("ID", i).equals(chargeCode)) {
				return parmName.getValue("CHN_DESC", i);
			}
		}
		return "";
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
			this.messageBox_("先查询数据!");
			return;
		}
		

		
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");

		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");//20140109 wangjingchun modify 683
		String billTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("BILL_DATE")), "yyyyMMdd");
		TParm selAllParm = new TParm();// 全部
		TParm selCheckedParm = new TParm();// 已结转
		TParm selParm = new TParm();// 未结转
		String billDay = billTime.substring(0, 8); // 月结时间点
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND E.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		// 全部（合计）
		String sqlAll = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selAllParm = new TParm(TJDODBTool.getInstance().select(sqlAll));
		BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
		TParm endAllParm = endData.getValue(selAllParm);
		System.out.println("全部：" + endAllParm);
		// 已结转
		String sqlChecked = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ " AND B.BILL_DATE < TO_DATE('"
				+ billDay
				+ "000000','yyyyMMddhh24miss')"
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selCheckedParm = new TParm(TJDODBTool.getInstance().select(sqlChecked));
		BILRecpChargeForPrint endData1 = new BILRecpChargeForPrint();
		TParm endCheckedParm = endData1.getValue(selCheckedParm);
		System.out.println("已结转：" + endCheckedParm);
		// 未结转
		String sql = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ " AND B.BILL_DATE > TO_DATE('"
				+ billDay
				+ "000000','yyyyMMddhh24miss')"
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));		
		BILRecpChargeForPrint endData2 = new BILRecpChargeForPrint();
		TParm endParm = endData2.getValue(selParm);
		System.out.println("未结转：" + endParm);
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd");

		// 赋值到报表中
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "出院患者医疗费用总表");
		parm.setData("DATE", "TEXT", sDate);
		parm.setData("OPT_USER", "TEXT", Operator.getName());
		parm.setData("printDate", "TEXT", sysDate);
		// 已结转
		String[] checkedName = { "CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
				"CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08", "CHARGE09",
				"CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13", "CHARGE14",
				"CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18", "CHARGE19",
				"CHARGE20" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(checkedName[i], "TEXT", endCheckedParm.getData(
					chargName[i], endCheckedParm.getCount() - 1));
		}		
		parm.setData("TOT_AMT", "TEXT", endCheckedParm.getData("TOT_AMT",
				endCheckedParm.getCount()-1));
		// 未结转
		String[] checkName = { "CHECKCHARGE01", "CHECKCHARGE02",
				"CHECKCHARGE03", "CHECKCHARGE04", "CHECKCHARGE05",
				"CHECKCHARGE06", "CHECKCHARGE07", "CHECKCHARGE08",
				"CHECKCHARGE09", "CHECKCHARGE10", "CHECKCHARGE11",
				"CHECKCHARGE12", "CHECKCHARGE13", "CHECKCHARGE14",
				"CHECKCHARGE15", "CHECKCHARGE16", "CHECKCHARGE17",
				"CHECKCHARGE18", "CHECKCHARGE19", "CHECKCHARGE20" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(checkName[i], "TEXT", endParm.getData(chargName[i],
					endParm.getCount() - 1));
		}
		parm.setData("CHECKTOT_AMT", "TEXT", endParm.getData("TOT_AMT", endParm.getCount() - 1 ));
		System.out.println("未结转总计："+endParm.getData("TOT_AMT", endParm.getCount() - 1 ));
		//全部
		String[] allChargName = { "ALLCHARGE01", "ALLCHARGE02", "ALLCHARGE03",
				"ALLCHARGE04", "ALLCHARGE05", "ALLCHARGE06", "ALLCHARGE07",
				"ALLCHARGE08", "ALLCHARGE09", "ALLCHARGE10", "ALLCHARGE11",
				"ALLCHARGE12", "ALLCHARGE13", "ALLCHARGE14", "ALLCHARGE15",
				"ALLCHARGE16", "ALLCHARGE17", "ALLCHARGE18", "ALLCHARGE19",
				"ALLCHARGE20" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(allChargName[i], "TEXT", endAllParm.getData(
					chargName[i], endAllParm.getCount() - 1));
		}
		parm.setData("ALLTOT_AMT", "TEXT", endAllParm.getData("TOT_AMT",
				endAllParm.getCount()-1));

		this.openPrintWindow(
				"%ROOT%\\config\\prt\\IBS\\BILOutHospPatSumFee.jhw", parm);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String billTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("BILL_DATE")), "yyyyMMdd");
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String deptWhere = "";
		String billDay = billTime.substring(0, 8); // 月结时间点
		// 已结转
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_4"))) {
			deptWhere += " AND B.BILL_DATE < TO_DATE('" + billDay
					+ "000000','yyyyMMddhh24miss')";
		}
		// 未结转
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_5"))) {
			deptWhere += " AND B.BILL_DATE > TO_DATE('" + billDay
					+ "000000','yyyyMMddhh24miss')";
		}
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND E.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		String sql = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ deptWhere
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		System.out.println("sql:" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("selParm" + selParm);
		if (selParm.getCount("EXE_DEPT_CODE") < 1) {
			// 查无数据
			this.messageBox("E0008");
			this.initPage();
			return;
		}
		BILRecpChargeForPrint endData = new BILRecpChargeForPrint();

		TParm endParm = endData.getValue(selParm);
		System.out.println("endParm:"+endParm);
		TParm resultParm = getHeard(); // 表头
		int count = resultParm.getCount();
		for (int i = 0; i < endParm.getCount(); i++) {
			resultParm.setRowData(count, endParm, i);
			count++;
		}
		resultParm.setCount(count);
		resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		for (int i = 0; i < chargName.length; i++) {
			resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
		}
		this.callFunction("UI|Table|setParmValue", resultParm);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "出院患者医疗费用总表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		this.clearValue("STATION_CODE;DEPT_CODE");
	}

	/**
	 * 单选按钮事件
	 * 
	 * @param type
	 *            String
	 */
	public void onRodio(String type) {
		if ("CTZ".equalsIgnoreCase(type)) {
			this.callFunction("UI|CTZ_CODE|setEnabled", true);
		} else {
			this.callFunction("UI|CTZ_CODE|setEnabled", false);
		}
	}
	 /**
     * 得到当前月
     * @param dateStr String
     * @return Timestamp
     */
    public Timestamp queryFirstDayOfLastMonth(String dateStr) {
        DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = defaultFormatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
//        cal.add(Calendar.MONTH, -1);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }

    /**
     * 初始化时间整理
     * @param date Timestamp
     * @return Timestamp
     */
    public Timestamp getDateForInit(Timestamp date) {
        String dateStr = StringTool.getString(date, "yyyyMMdd");
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
        String monThCycle = "" + monthM;
        dateStr = dateStr.substring(0, 6) + monThCycle;
        Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
        return result;
    }

}
