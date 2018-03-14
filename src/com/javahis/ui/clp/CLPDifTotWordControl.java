package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.Date;
import java.util.Vector;

import com.javahis.util.ExportExcelUtil;

public class CLPDifTotWordControl extends TControl {
	public CLPDifTotWordControl() {

	}

	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 表格
	private TTable table;

	public void onInit() {
		super.onInit();
		initControl();
	}

	/**
	 * 初始化控件
	 */
	public void initControl() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1周
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table = (TTable) this.getComponent("CLPTABLE");
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (this.table.getRowCount() <= 0) {
			this.messageBox("没有要打印的数据");
			return;
		}
		TParm prtParm = new TParm();
		// 表头
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径差异总表");
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", startDate); // 制表时间start
		prtParm.setData("END_DATE", "TEXT", endDate); // 制表时间end
		TParm prtTableParm = this.getSelectTParm();
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CODE");// wangzhilei 20120724 添加
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "MONCAT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "VARIANCE_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		prtTableParm.addData("SYSTEM", "COLUMNS", "VAR_COUNT");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// 表尾
		prtParm.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPDifTotWordPrt.jhw",
				prtParm);
	}

	public void onQuery() {
		TParm parm = this.getSelectTParm();
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		TParm tableparm = new TParm();
		// 得到时间查询条件
		String startdate = this.getValueString("START_DATE");
		startdate = startdate.substring(0, 4) + startdate.substring(5, 7)
				+ startdate.substring(8, 10) + startdate.substring(11, 13)
				+ startdate.substring(14, 16) + startdate.substring(17, 19);
		String enddate = this.getValueString("END_DATE");
		enddate = enddate.substring(0, 4) + enddate.substring(5, 7)
				+ enddate.substring(8, 10) + enddate.substring(11, 13)
				+ enddate.substring(14, 16) + enddate.substring(17, 19);
		// 得到差异总表信息
		String Sql = "SELECT D.REGION_CODE AS REGION_CODE,B.CLNCPATH_CHN_DESC,T.MONCAT_CHN_DESC,V.VARIANCE_CHN_DESC,DE.DEPT_CHN_DESC,R.USER_NAME,COUNT(D.CASE_NO) VAR_COUNT FROM "
				+ "CLP_MANAGED D, CLP_VARMONCAT T,CLP_VARIANCE V,CLP_BSCINFO B ,SYS_DEPT DE ,SYS_OPERATOR R ,ADM_INP P,MRO_RECORD MR  "
				+ "WHERE MR.CASE_NO=D.CASE_NO AND D.MEDICAL_MONCAT IS NOT NULL AND  T.MONCAT_CODE(+) = D.MEDICAL_MONCAT  AND D.CASE_NO=P.CASE_NO(+) AND D.REGION_CODE='"
				+ Operator.getRegion()
				+ "' AND MR.OUT_DATE>TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND MR.OUT_DATE<TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS') "
				+ "AND V.VARIANCE_CODE(+) = D.MEDICAL_VARIANCE "
				+ "AND  B.CLNCPATH_CODE(+)=D.CLNCPATH_CODE "
				+ "AND  D.R_DEPT_CODE = DE.DEPT_CODE(+) "
				+ "AND  D.R_USER = R.USER_ID(+) "
				+ "GROUP BY B.CLNCPATH_CHN_DESC, T.MONCAT_CHN_DESC, V.VARIANCE_CHN_DESC, DE.DEPT_CHN_DESC, R.USER_NAME,D.REGION_CODE ";
		//System.out.println("执行sql:" + Sql);
		tableparm = new TParm(TJDODBTool.getInstance().select(Sql));
		TParm prtTableParm = new TParm();
		// 处理查询出来的parm
		if (tableparm == null || tableparm.getCount() <= 0) {
			this.messageBox("没有查询数据");
			return tableparm;
		}
		// 总计信息变量
		// 变异数
		int totalCount = 0;
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");// wangzhilei 20120724 添加
		// 统计信息end
		for (int i = 0; i < tableparm.getCount(); i++) {
			TParm rowParm = tableparm.getRow(i);
			// wangzhilei 20120724 添加
			String rn = tableparm.getValue("REGION_CODE", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			// wangzhilei 20120724 添加
			prtTableParm.addData("REGION_CODE", rn);// wangzhilei 20120724 添加
			prtTableParm.addData("CLNCPATH_CHN_DESC", rowParm
					.getValue("CLNCPATH_CHN_DESC"));
			prtTableParm.addData("MONCAT_CHN_DESC", rowParm
					.getValue("MONCAT_CHN_DESC"));
			prtTableParm.addData("VARIANCE_CHN_DESC", rowParm
					.getValue("VARIANCE_CHN_DESC"));
			prtTableParm.addData("DEPT_CHN_DESC", rowParm
					.getValue("DEPT_CHN_DESC"));
			prtTableParm.addData("USER_NAME", rowParm.getValue("USER_NAME"));
			prtTableParm.addData("VAR_COUNT", rowParm.getValue("VAR_COUNT"));
			// 统计变异数
			totalCount += rowParm.getInt("VAR_COUNT");
		}
		// 加入统计信息
		prtTableParm.addData("REGION_CODE", "总计:");// wangzhilei 20120724 添加
		prtTableParm.addData("MONCAT_CHN_DESC", "");
		prtTableParm.addData("VARIANCE_CHN_DESC", "");
		prtTableParm.addData("DEPT_CHN_DESC", "");
		prtTableParm.addData("USER_NAME", "");
		prtTableParm.addData("VAR_COUNT", totalCount);
		prtTableParm.setCount(prtTableParm.getCount("CLNCPATH_CHN_DESC"));
		return prtTableParm;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径差异总表");
		}
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

}
