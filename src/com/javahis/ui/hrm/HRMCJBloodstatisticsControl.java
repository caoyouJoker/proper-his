package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class HRMCJBloodstatisticsControl extends TControl
{
	public TTable getTTable(String tag) {
		return (TTable)this.getComponent(tag);
	}
	private String start_Date;
	private String end_Date;
	private String ORDER_DEPT_CODE;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("end_Date",
				date.toString().substring(0, 10).replace('-', '/'));
		this.setValue("start_Date",
				StringTool.rollDate(date, -7).toString().substring(0, 10).
				replace('-', '/'));
		setValue("PURORDER_DATE", date);
	}

	/**
	 *  查询方法
	 */
	public void onQuery() {
		start_Date=this.getValue("start_Date").toString().substring(0, 10).replace('-', '/') + " 00:00:00";
		end_Date=this.getValue("end_Date").toString().substring(0, 10).replace('-', '/') + " 23:59:59";
		ORDER_DEPT_CODE=this.getValueString("ORDER_DEPT_CODE");
		String sql=" SELECT   COUNT (DISTINCT MA.APPLICATION_NO) AS COUNT,SD.DEPT_CHN_DESC AS DES "+
		"   FROM   MED_APPLY MA,SYS_DEPT SD"+
		" WHERE  MA.CAT1_TYPE = 'LIS'"+
		" AND SD.DEPT_CODE=MA.DEPT_CODE"+
		" AND  MA.ORDER_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')";
		if(!"".equals(ORDER_DEPT_CODE))
//		{
//			sql+= "AND SD.DEPT_CODE = '"+ORDER_DEPT_CODE+"'";
//		}
//		else
		{
			sql+= "AND MA.DEPT_CODE = '"+ORDER_DEPT_CODE+"'";
		}
		sql+=" AND OPTITEM_CHN_DESC LIKE '%血%' GROUP BY   SD.DEPT_CHN_DESC";
		TParm selParm = new TParm();
                // System.out.println("sql==="+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable("tbl1").setParmValue(selParm);
		if(getTTable("tbl1").getRowCount()<1)
			this.messageBox("E0008");//弹出提示对话框（“没有数据”）
	}

	/**
	 *  打印方法
	 */
	public void onPrint() {
		int ATM = 0;
		TTable table_d = getTTable("tbl1");
		if (table_d.getRowCount() > 0) {
			// 打印数据OPT_USER;COUNT(CASE_NO)
			TParm date = new TParm();

			// 表头数据
			date.setData("Title", "TEXT",
					Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
			"采血工作量统计报表");
			date.setData("createDate", "TEXT",
					"制表日期: " +
					SystemTool.getInstance().getDate().toString().
					substring(0, 10).replace('-', '/'));
			date.setData("start_Date", "TEXT",
					"开始时间: " +
					start_Date.
					substring(0, 10).replace('-', '/'));
			date.setData("end_Date", "TEXT",
					"结束时间: " +
					end_Date.
					substring(0, 10).replace('-', '/'));
			// 表格数据 B.CHN_DESC, COUNT (A.CASE_NO)
			TParm parm = new TParm();
			TParm tableParm = table_d.getParmValue();
			// System.out.println("tableParm====" + tableParm);
			int count = tableParm.getCount();

			for (int i = 0; i < count; i++) {
				parm.addData("DES",
						tableParm.getValue("DES", i));
				parm.addData("COUNT",
						tableParm.getValue("COUNT", i));
				ATM +=
					StringTool.getInt(tableParm.getValue("COUNT", i));
			}
			parm.setCount(parm.getCount("COUNT"));
			// System.out.println("parm====" + parm);
			parm.addData("SYSTEM", "COLUMNS", "DES");
			parm.addData("SYSTEM", "COLUMNS", "COUNT");
			date.setData("tbl1", parm.getData());
			// 表尾数据
			date.setData("createUser", "TEXT", "制表人: " + Operator.getName());
			date.setData("pass", "TEXT", "审核人: ");
			date.setData("ATM", "TEXT",
					"合计检验人次：" + ATM);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMCJBloodstatistics.jhw",
					date);
		}
		else {
			this.messageBox("E0010");//弹出提示对话框（“没打印有数据”）
			return;
		}
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		//得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table_d = getTTable("tbl1");
		ExportExcelUtil.getInstance().exportExcel(table_d, "检验检查人数统计报表");
	}
	/**
	 * 清空
	 */
	public void onClear()
	{
		this.clearValue("ORDER_DEPT_CODE");
		this.getTTable("tbl1").setSelectionMode(0);
		this.getTTable("tbl1").removeRowAll();
		onInit();
	}

}
