package com.javahis.ui.bil;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.control.TControl;

import jdo.bil.BILComparator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>
 * Title:患者预交金余额报表
 * </p>
 * 
 * <p>
 * Description:患者预交金余额报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author yanmm 2017/12
 * @version 1.0
 */
public class BILInHoopTotPayControl extends TControl {
	private BILComparator compare = new BILComparator();

	private boolean ascending = false;
	private int sortColumn = -1;
	TParm endParm;
	private static TTable table;
	private static TTable table1;

	public void onInit() {
		super.onInit();
		this.initPage();
	}

	/**
	 * 得到TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		Timestamp yesterday = StringTool.rollDate(date, -15);
		this.setValue("QUERY_DATE", date);
		String dsTimeS = StringTool.getString(yesterday, "yyyy/MM/dd");
		String dsTimeE = StringTool.getString(date, "yyyy/MM/dd");
		setValue("START_DATE", dsTimeS);
		setValue("END_DATE", dsTimeE);
		table = (TTable) getComponent("TABLE");
		table1 = (TTable) getComponent("TABLE1");
		// 排序监听
		addListener(table);
		addListener(table1);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
		table.setParmValue(new TParm());
		table1.setParmValue(new TParm());
		TParm bilParm = new TParm();
		TParm tableParm = new TParm();
		String eTime = this.getText("QUERY_DATE").replaceAll("/", "");
		String timeS = this.getText("START_DATE").replaceAll("/", "");
		String timeE = this.getText("END_DATE").replaceAll("/", "");
		double y = 0.00;
		double z = 0.00;
		double q = 0.00;
		String sql = "";
		int row = 0;
		int num = 0;
		switch (selTTabbendPane) {
		case 0:
			sql = "SELECT CASE_NO,DEPT_CHN_DESC,STATION_DESC,USER_NAME, "
					+ "BED_NO,BED_NO_DESC,MR_NO,PAT_NAME,CTZ_DESC,MES_COUNT,PRE_AMT,TOT_AMT,DISCOUNT_RATE, "
					+ "(PRE_AMT - (TOT_AMT * DISCOUNT_RATE)) TOT_PAY " + "FROM "
					+ "(  SELECT A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME, "
					+ "A.BED_NO, A.MR_NO,P.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT,G.BED_NO_DESC, "
					+ "CASE WHEN C.PRE_AMT IS NULL THEN 0.00 ELSE C.PRE_AMT END PRE_AMT, "
					+ "SUM (B.TOT_AMT) TOT_AMT,CASE WHEN E.DISCOUNT_RATE IS NULL THEN 1 ELSE E.DISCOUNT_RATE END DISCOUNT_RATE "
					+ "FROM "
					+ "ADM_INP A,SYS_DEPT D,SYS_OPERATOR O,SYS_PATINFO P,SYS_STATION S,SYS_CTZ_REBATE E,SYS_CTZ Z,IBS_ORDD B,SYS_BED G, "
					+ "(SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT " + "FROM "
					+ "BIL_PAY C WHERE C.REFUND_FLG = 'N' AND C.TRANSACT_TYPE IN ('01', '03', '04') "
					+ "AND CHARGE_DATE <= TO_DATE ('" + eTime + "','YYYY/MM/DD HH24:MI:SS') " + "GROUP BY CASE_NO) C "
					+ "WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.BED_NO=G.BED_NO "
					+ "AND A.VS_DR_CODE = O.USER_ID AND A.CTZ1_CODE = Z.CTZ_CODE "
					+ "AND A.MR_NO = P.MR_NO AND A.STATION_CODE = S.STATION_CODE " + "AND A.CASE_NO = C.CASE_NO(+) AND "
					+ "B.BILL_DATE <= TO_DATE ('" + eTime + "', 'YYYY/MM/DD HH24:MI:SS') "
					+ "AND A.DS_DATE IS NULL AND A.CTZ1_CODE = E.CTZ_CODE(+) " + getWhereStr(0)
					+ "GROUP BY C.PRE_AMT,A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME,G.BED_NO_DESC, "
					+ "A.BED_NO,A.MR_NO,P.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT,E.DISCOUNT_RATE "
					+ "ORDER BY D.DEPT_CHN_DESC)";
			// System.out.println("sql========="+sql);
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			// 科室,150;病区,150;经治医生,120;身份,100;床号,120;病案号,120;姓名,100;预交金总额,150;在院费用总额,150;欠费金额,150;短信通知次数,100
			// DEPT_CHN_DESC;STATION_DESC;USER_NAME;CTZ_DESC;BED_NO_DESC;MR_NO;PAT_NAME;PRE_AMT;TOT_AMT;TOT_PAY;MES_COUNT
			if (result1.getCount() > 0) {
				for (int i = 0; i < result1.getCount(); i++) {
					if (result1.getDouble("TOT_PAY", i) < 0) {
						bilParm.setData("DEPT_CHN_DESC", i, result1.getValue("DEPT_CHN_DESC", i));
						bilParm.setData("STATION_DESC", i, result1.getValue("STATION_DESC", i));
						bilParm.setData("USER_NAME", i, result1.getValue("USER_NAME", i));
						bilParm.setData("CTZ_DESC", i, result1.getValue("CTZ_DESC", i));
						bilParm.setData("BED_NO_DESC", i, result1.getValue("BED_NO_DESC", i));
						bilParm.setData("MR_NO", i, result1.getValue("MR_NO", i));
						bilParm.setData("PAT_NAME", i, result1.getValue("PAT_NAME", i));
						bilParm.setData("PRE_AMT", i, result1.getDouble("PRE_AMT", i));
						y += result1.getDouble("PRE_AMT", i);
						bilParm.setData("TOT_AMT", i, result1.getDouble("TOT_AMT", i));
						z += result1.getDouble("TOT_AMT", i);
						bilParm.setData("TOT_PAY", i, result1.getDouble("TOT_PAY", i));
						q += result1.getDouble("TOT_PAY", i);
						bilParm.setData("MES_COUNT", i, result1.getValue("MES_COUNT", i));
						num++;
					}
				}
			} else {
				tableParm.addRowData(bilParm, row);
			}
			bilParm.addData("DEPT_CHN_DESC", "合计:");
			bilParm.addData("STATION_DESC", " ");
			bilParm.addData("USER_NAME", num);
			bilParm.addData("CTZ_DESC", " ");
			bilParm.addData("BED_NO_DESC", " ");
			bilParm.addData("MR_NO", " ");
			bilParm.addData("PAT_NAME", " ");
			bilParm.addData("PRE_AMT", y);
			bilParm.addData("TOT_AMT", z);
			bilParm.addData("TOT_PAY", q);
			bilParm.addData("MES_COUNT", " ");
			bilParm.setCount(result1.getCount() + 1);
			table.setParmValue(bilParm);
			if (result1.getCount("CASE_NO") < 0) {
				messageBox("查无数据");
				return;
			}
			break;
		case 1:
			sql = "SELECT CASE_NO,DEPT_CHN_DESC,STATION_DESC,USER_NAME, "
					+ "BED_NO,BED_NO_DESC,MR_NO,PAT_NAME,CTZ_DESC,MES_COUNT,PRE_AMT,TOT_AMT,DISCOUNT_RATE, "
					+ "(PRE_AMT - (TOT_AMT * DISCOUNT_RATE)) TOT_PAY " + "FROM "
					+ "(  SELECT A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME, "
					+ "A.BED_NO, A.MR_NO,P.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT,G.BED_NO_DESC, "
					+ "CASE WHEN C.PRE_AMT IS NULL THEN 0.00 ELSE C.PRE_AMT END PRE_AMT, "
					+ "SUM (B.TOT_AMT) TOT_AMT,CASE WHEN E.DISCOUNT_RATE IS NULL THEN 1 ELSE E.DISCOUNT_RATE END DISCOUNT_RATE "
					+ "FROM "
					+ "ADM_INP A,SYS_DEPT D,SYS_OPERATOR O,SYS_PATINFO P,SYS_STATION S,SYS_CTZ_REBATE E,SYS_CTZ Z,IBS_ORDD B,SYS_BED G, "
					+ "(SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT " + "FROM "
					+ "BIL_PAY C WHERE C.REFUND_FLG = 'N' AND C.TRANSACT_TYPE IN ('01', '03', '04') "
					+ " GROUP BY CASE_NO) C ,IBS_BILLM M "
					+ "WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.BED_NO=G.BED_NO "
					+ "AND A.VS_DR_CODE = O.USER_ID AND A.CTZ1_CODE = Z.CTZ_CODE "
					+ "AND A.MR_NO = P.MR_NO AND A.STATION_CODE = S.STATION_CODE " + "AND A.CASE_NO = C.CASE_NO(+) AND "
					+ "A.DS_DATE BETWEEN TO_DATE ('" + timeS + "', 'YYYY/MM/DD') " + "AND TO_DATE ('" + timeE
					+ "','YYYY/MM/DD') " + "AND A.DS_DATE IS NOT NULL AND A.CTZ1_CODE = E.CTZ_CODE(+) "
					+ "AND B.BILL_NO IS NOT NULL AND B.BILL_NO=M.BILL_NO AND M.RECEIPT_NO IS NULL " + getWhereStr(1)
					+ "GROUP BY C.PRE_AMT,A.CASE_NO,D.DEPT_CHN_DESC,S.STATION_DESC,O.USER_NAME,G.BED_NO_DESC, "
					+ "A.BED_NO,A.MR_NO,P.PAT_NAME,Z.CTZ_DESC,A.MES_COUNT,E.DISCOUNT_RATE "
					+ "ORDER BY D.DEPT_CHN_DESC)";
			// System.out.println("sql2----" + sql);
			TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));

			if (result2.getCount() > 0) {
				for (int i = 0; i < result2.getCount(); i++) {
					if (result2.getDouble("TOT_PAY", i) < 0) {
						bilParm.setData("DEPT_CHN_DESC", i, result2.getValue("DEPT_CHN_DESC", i));
						bilParm.setData("STATION_DESC", i, result2.getValue("STATION_DESC", i));
						bilParm.setData("USER_NAME", i, result2.getValue("USER_NAME", i));
						bilParm.setData("CTZ_DESC", i, result2.getValue("CTZ_DESC", i));
						bilParm.setData("BED_NO_DESC", i, result2.getValue("BED_NO_DESC", i));
						bilParm.setData("MR_NO", i, result2.getValue("MR_NO", i));
						bilParm.setData("PAT_NAME", i, result2.getValue("PAT_NAME", i));
						bilParm.setData("PRE_AMT", i, result2.getDouble("PRE_AMT", i));
						y += result2.getDouble("PRE_AMT", i);
						bilParm.setData("TOT_AMT", i, result2.getDouble("TOT_AMT", i));
						z += result2.getDouble("TOT_AMT", i);
						bilParm.setData("TOT_PAY", i, result2.getDouble("TOT_PAY", i));
						q += result2.getDouble("TOT_PAY", i);
						bilParm.setData("MES_COUNT", i, result2.getValue("MES_COUNT", i));
						num++;
					}
				}
			} else {
				tableParm.addRowData(bilParm, row);
			}

			bilParm.addData("DEPT_CHN_DESC", "合计:");
			bilParm.addData("STATION_DESC", " ");
			bilParm.addData("USER_NAME", num);
			bilParm.addData("CTZ_DESC", " ");
			bilParm.addData("BED_NO_DESC", " ");
			bilParm.addData("MR_NO", " ");
			bilParm.addData("PAT_NAME", " ");
			bilParm.addData("PRE_AMT", y);
			bilParm.addData("TOT_AMT", z);
			bilParm.addData("TOT_PAY", q);
			bilParm.addData("MES_COUNT", " ");
			bilParm.setCount(result2.getCount() + 1);
			table1.setParmValue(bilParm);

			if (result2.getCount("CASE_NO") < 0) {
				messageBox("查无数据");
				return;
			}

			break;
		}
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
	 * 得到条件
	 * 
	 * @param tableIndex
	 * @return String
	 */
	public String getWhereStr(int tableIndex) {
		String whereStr = "";
		switch (tableIndex) {
		case 0:
			String mrNo1 = this.getValueString("MR_NO1");
			String deptCode1 = this.getValueString("DEPT_CODE1");
			String stationCode1 = this.getValueString("STATION_CODE1");
			// 病案号
			if (!"".equals(mrNo1)) {
				whereStr += " AND A.MR_NO = '" + mrNo1 + "'";
			}
			// 科室
			if (!"".equals(deptCode1)) {
				whereStr += " AND A.DEPT_CODE = '" + deptCode1 + "'";
			}
			// 病区
			if (!"".equals(stationCode1)) {
				whereStr += " AND A.STATION_CODE = '" + stationCode1 + "'";
			}
			break;
		case 1:
			String mrNo2 = this.getValueString("MR_NO2");
			String deptCode2 = this.getValueString("DEPT_CODE2");
			String stationCode2 = this.getValueString("STATION_CODE2");
			// 病案号
			if (!"".equals(mrNo2)) {
				whereStr += " AND A.MR_NO = '" + mrNo2 + "'";
			}
			// 科室
			if (!"".equals(deptCode2)) {
				whereStr += " AND A.DEPT_CODE = '" + deptCode2 + "'";
			}
			// 病区
			if (!"".equals(stationCode2)) {
				whereStr += " AND A.STATION_CODE = '" + stationCode2 + "'";
			}
			break;
		}
		return whereStr;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
		switch (selTTabbendPane) {
		case 0:
			this.clearValue("DEPT_CODE1;STATION_CODE1;ALLPERSON;MR_NO1;PAT_NAME");
			// 当前时间
			Timestamp today = SystemTool.getInstance().getDate();
			this.setValue("QUERY_DATE", today);
			table.removeRowAll();
			break;
		case 1:
			this.clearValue("DEPT_CODE2;STATION_CODE2");
			Timestamp date = SystemTool.getInstance().getDate();
			Timestamp yesterday = StringTool.rollDate(date, -15);
			this.setValue("QUERY_DATE", date);
			String dsTimeS = StringTool.getString(yesterday, "yyyy/MM/dd");
			String dsTimeE = StringTool.getString(date, "yyyy/MM/dd");
			this.setValue("START_DATE", dsTimeS);
			this.setValue("END_DATE", dsTimeE);
			table1.removeRowAll();
			break;
		}
	}

	/**
	 * 导出EXECL
	 */
	public void onExecl() {
		int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
		TTable table = null;
		try {
			table = selTTabbendPane == 0 ? table = (TTable) callFunction("UI|TABLE|getThis")
					: (TTable) callFunction("UI|TABLE1|getThis");
			if (selTTabbendPane == 0) {
				ExportExcelUtil.getInstance().exportExcel(table, "在院患者预交金欠费报表");
			} else if (selTTabbendPane == 1) {
				ExportExcelUtil.getInstance().exportExcel(table, "出院患者预交金欠费报表");
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("没有可导入的数据！");
			return;
		}
	}

	// ====================排序功能begin======================
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}

	// ====================排序功能end======================
	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		if (getValue("MR_NO1").equals("") || getValue("MR_NO1") == null) {
			this.setValue("PAT_NAME", "");
			((TTextField) this.getComponent("PAT_NAME")).setEnabled(true);
		}

		Pat pat1 = Pat.onQueryByMrNo(this.getValueString("MR_NO1").trim());
		String mrNo = PatTool.getInstance().checkMrno(TCM_Transform.getString(getValue("MR_NO1")));
		this.setValue("MR_NO1", mrNo);

		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat1.getMrNo())) {
			messageBox("病案号" + mrNo + " 已合并至 " + "" + pat1.getMrNo());
			setValue("MR_NO1", pat1.getMrNo());
		}
		this.setValue("PAT_NAME", pat1.getName());
		((TTextField) this.getComponent("PAT_NAME")).setEnabled(false);
	}

}
