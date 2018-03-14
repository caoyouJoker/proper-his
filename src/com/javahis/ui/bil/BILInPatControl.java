package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

public class BILInPatControl extends TControl {
	TParm parm;
	String DATE;
	double sum = 0;

	public void onInit() {
		super.onInit();
		this.initPage();
	}

	/**
	 * 初始化
	 */
	private void initPage() {
		// table = (TTable) this.getComponent("TABLE");
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("DATE_S", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// 开始时间
		this.setValue("DATE_E", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// 结束时间

	}

	/**
	 * 查询
	 */
	public void OnQuery() {
		//===zhangp 20120618 start
		String sql =
				" SELECT   A.IPD_NO, C.MR_NO, E.STATION_DESC, B.PAT_NAME, C.DEPT_CODE," +
				" D.DEPT_ABS_DESC, SUM (PRE_AMT) AS PRE_AMT" +
				" FROM BIL_PAY A, SYS_PATINFO B, ADM_INP C, SYS_DEPT D, SYS_STATION E" +
				" WHERE A.CASE_NO = C.CASE_NO" +
				" AND C.STATION_CODE = E.STATION_CODE" +
				" AND A.MR_NO = C.MR_NO" +
				" AND B.MR_NO = C.MR_NO" +
				" AND C.DEPT_CODE = D.DEPT_CODE" +
				//===ZHANGP 20120625 START
//				" AND C.DS_DATE IS NULL" +
				" AND C.BILL_STATUS <> 4" +
				//===ZHANGP 20120625 END
				" GROUP BY A.IPD_NO," +
				" B.PAT_NAME," +
				" C.DEPT_CODE," +
				" D.DEPT_ABS_DESC," +
				" C.MR_NO," +
				" E.STATION_DESC" +
				" ORDER BY C.DEPT_CODE";
		System.out.println(sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
			return;
		}
		// table赋值IPD_NO;PAT_NAME;DEPT_CODE;TOTAL_BILPAY
		// this.callFunction("UI|TABLE|setParmValue", parm);
		double money1 = 0.00;
		TParm printData = new TParm();
		String deptcode = parm.getData("DEPT_ABS_DESC", 0).toString();
		DecimalFormat df = new DecimalFormat("########0.00");
		for (int i = 0; i < parm.getCount("IPD_NO"); i++) {
			if (parm.getData("DEPT_ABS_DESC", i).equals(deptcode)) {
				printData.addData("IPD_NO", parm.getValue("IPD_NO", i));
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				printData.addData("DEPT_ABS_DESC", parm.getValue(
						"DEPT_ABS_DESC", i));
				printData.addData("PRE_AMT", df.format(StringTool.round(parm.getDouble("PRE_AMT", i),2)));
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				printData.addData("STATION_CODE", parm.getValue("STATION_DESC", i));
				money1 += parm.getDouble("PRE_AMT", i);
//				if (i == parm.getCount("IPD_NO") - 1) {
//					printData.addData("IPD_NO", "");
//					printData.addData("PAT_NAME", "");
//					printData.addData("DEPT_ABS_DESC", "预交金小计");
//					printData.addData("PRE_AMT", df.format(money1));
//					printData.addData("MR_NO", "");
//					printData.addData("STATION_CODE", "");
//					sum += money1;
//				}
			} else {
				printData.addData("IPD_NO", "");
				printData.addData("PAT_NAME", "");
				printData.addData("DEPT_ABS_DESC", "预交金小计");
				printData.addData("PRE_AMT", df.format(money1));
				printData.addData("MR_NO", "");
				printData.addData("STATION_CODE", "");
				sum += money1;
				money1 = 0;
				printData.addData("IPD_NO", parm.getValue("IPD_NO", i));
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				printData.addData("DEPT_ABS_DESC", parm.getValue(
						"DEPT_ABS_DESC", i));
				printData.addData("PRE_AMT", df.format(StringTool.round(parm.getDouble("PRE_AMT", i),2)));
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				printData.addData("STATION_CODE", parm.getValue("STATION_DESC", i));
				money1 += parm.getDouble("PRE_AMT", i);
			}
			deptcode = parm.getData("DEPT_ABS_DESC", i).toString();
		}
		printData.addData("IPD_NO", "");
		printData.addData("PAT_NAME", "");
		printData.addData("DEPT_ABS_DESC", "预交金小计");
		printData.addData("PRE_AMT", df.format(money1));
		printData.addData("MR_NO", "");
		printData.addData("STATION_CODE", "");
		sum += money1;
		printData.addData("IPD_NO", "");
		printData.addData("PAT_NAME", "");
		printData.addData("DEPT_ABS_DESC", "总计");
		printData.addData("PRE_AMT", df.format(sum));
		printData.addData("MR_NO", "");
		printData.addData("STATION_CODE", "");
		//====zhangp 20120618 end
		printData.setCount(printData.getCount("IPD_NO"));
		this.callFunction("UI|TABLE|setParmValue", printData);
		DATE = StringTool.getString((Timestamp) this.getValue("DATE_S"),
				"yyyy/MM/dd ")
				+ " 至 "
				+ StringTool.getString((Timestamp) this.getValue("DATE_E"),
						"yyyy/MM/dd ");

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DEPT_ABS_DESC");
		TTable table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		//====zhangp 20120618 start
		parm = new TParm();
		DATE = "";
	    sum = 0;
		//====zhangp 20120618 end
	}

	/**
	 * 打印
	 */
	public void OnPrint() {
		//==========zhangp 20120618 start
		DecimalFormat df = new DecimalFormat("########0.00");
		TTable table = (TTable) getComponent("TABLE");
		TParm tableParm = table.getParmValue();
		tableParm.setCount(tableParm.getCount("IPD_NO"));
		tableParm.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "STATION_CODE");
		tableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "PRE_AMT");
		TParm printParm = new TParm();
		printParm.setData("TABLE", tableParm.getData());
//		printParm.setData("ALLMONEY", "TEXT", df.format(StringTool.round(sum,2)));
//		String date = SystemTool.getInstance().getDate().toString();
		String date = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");//20140109 wangjingchun modify 683
		printParm.setData("P_DATE", "TEXT", "制表时间: " + date);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		//==========zhangp 20120618 end
		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILInPat.jhw",
				printParm);
	}
	
    /**
     * 汇出Excel
     */
    public void onExport() {
    	TTable table = (TTable) getComponent("TABLE");
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "预交金明细表");
    }
}
