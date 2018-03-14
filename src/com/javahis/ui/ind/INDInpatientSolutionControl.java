package com.javahis.ui.ind;

import java.sql.Timestamp;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:住院配液查询
 * </p>
 * 
 * <p>
 * Description:住院配液查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author liuyl 2017.03.22
 * @version 1.0
 */
public class INDInpatientSolutionControl extends TControl {
	// 得到table控件
	private TTable table_d;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	TParm Parm = new TParm();

	// 初始化页面
	public void onInit() {
		super.onInit();
		table_d = (TTable) this.getComponent("TABLE_D");
		// 初始化时间默认值
		Timestamp today = SystemTool.getInstance().getDate();
		String startTime = StringTool.getString(today, "yyyy/MM/dd 00:00:00");
		String endTime = StringTool.getString(today, "yyyy/MM/dd 23:59:59");
		setValue("START_DATE", startTime);
		setValue("END_DATE", endTime);
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
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			setValue("MR_NO", pat.getMrNo());
		}
		setValue("PAT_NAME", pat.getName().trim());
		this.onQuery();
	}

	/**
	 * 扫描瓶签查询调配药品信息
	 */
	public void onQuery() {
		// 得到table控件
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		// 封装前台得到的值
		TParm parm = new TParm();
		String sDate = this.getValueString("START_DATE");
		String eDate = this.getValueString("END_DATE");
		String QueryDate = " ";
		sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		QueryDate += " AND B.OPT_DATE BETWEEN TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS')" + "AND TO_DATE ('" + eDate
				+ "','YYYYMMDDHH24MISS') ";
		String barcode = this.getValueString("BAR_CODE");
		if (barcode != null && !"".equals(barcode)) {
			parm.setData("BAR_CODE", barcode);
			String sql = "  SELECT A.LINKMAIN_FLG AS SELECT_FLG,trim(A.LINK_NO) AS LINK_NO,B.BAR_CODE,A.ORDER_CODE,A.DEPT_CODE,A.ORDER_DESC,A.MR_NO,"
					+ "A.SPECIFICATION,A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,A.STATION_CODE, "
					+ "B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY,A.FREQ_CODE,"
					+ "B.BATCH_CODE,B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,H.ROUTE_CHN_DESC, C.FREQ_CHN_DESC,"
					+ "D.ORDER_DESC AS IBS_ORDER_DESC,B.OWN_PRICE "
					+ "FROM ODI_DSPNM A,ODI_SOLUTION B,SYS_PHAFREQ C,SYS_UNIT E,SYS_PHAROUTE H,SYS_UNIT F,SYS_FEE G,SYS_FEE D "
					+ "WHERE A.IVA_FLG = 'Y' "
					+ " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
					+ "AND A.CASE_NO = B.CASE_NO "
					+ "AND A.ORDER_NO = B.ORDER_NO "
					+ "AND A.ORDER_SEQ = B.ORDER_SEQ "
					+ "AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM "
					+ "AND A.END_DTTM "
					+ "AND B.BAR_CODE = '"
					+ parm.getValue("BAR_CODE")
					+ "' "
					+ "AND A.FREQ_CODE = C.FREQ_CODE "
					+ "AND A.MEDI_UNIT = E.UNIT_CODE "
					+ "AND A.MEDI_UNIT = F.UNIT_CODE "
					+ "AND A.ROUTE_CODE = H.ROUTE_CODE "
					+ "AND G.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = G.ORDER_CODE "
					+ "AND B.IBS_ORDER_DESC = D.ORDER_DESC "
					+ QueryDate
					+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME,CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END ";
			Parm = new TParm(TJDODBTool.getInstance().select(sql));
			// messageBox_("Parm,:::"+Parm);
			String mr_no = Parm.getValue("MR_NO", 0).replace("[", "")
					.replace("]", "").replace(" ", "");
			String sqlPat = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO = "
					+ mr_no + " ";
			TParm PatParm = new TParm(TJDODBTool.getInstance().select(sqlPat));
			// System.out.println("sql::::"+sql);
			this.setValue("MR_NO", mr_no);
			this.setValue(
					"PAT_NAME",
					PatParm.getValue("PAT_NAME", 0).replace("[", "")
							.replace("]", "").replace(" ", ""));
			this.setValue("STATION_CODE", Parm.getValue("STATION_CODE", 0)
					.replace("[", "").replace("]", "").replace(" ", ""));
			this.setValue(
					"DEPT_CODE",
					Parm.getValue("DEPT_CODE", 0).replace("[", "")
							.replace("]", "").replace(" ", ""));
			TTable TABLE_D = (TTable) this.getComponent("TABLE_D");
			for (int i = 0, length = Parm.getCount("ORDER_CODE"); i < length; i++) {
				int row = TABLE_D.addRow(i);
				TABLE_D.setItem(
						row,
						0,
						Parm.getValue("SELECT_FLG").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						1,
						Parm.getValue("LINK_NO").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						2,
						Parm.getValue("BAR_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						3,
						Parm.getValue("ORDER_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						4,
						Parm.getValue("ORDER_DESC").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						5,
						Parm.getValue("SPECIFICATION").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						6,
						Parm.getValue("MEDI_QTY").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(row, 7, Parm.getValue("ROUTE_CHN_DESC")
						.replace("[", "").replace("]", "").replace(" ", "")
						.split(",")[i]);
				TABLE_D.setItem(
						row,
						8,
						Parm.getValue("FREQ_CHN_DESC").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						9,
						Parm.getValue("FREQ_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						10,
						Parm.getValue("EXEC_DATE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(row, 11, Parm.getValue("IBS_ORDER_DESC")
						.replace("[", "").replace("]", "").replace(" ", "")
						.split(",")[i]);
				TABLE_D.setItem(
						row,
						12,
						Parm.getValue("OWN_PRICE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				if ("N".equals(Parm.getValue("SELECT_FLG", i))
						&& !"".equals(Parm.getValue("LINK_NO", i))) {
					TABLE_D.setItem(row, 11, "");
					TABLE_D.setItem(row, 12, "");
				}				
			}			
			if (Parm.getCount() <= 0) {
				this.messageBox("未查询到数据");
				return;
			}
		} else if (this.getValue("MR_NO") != null
				&& !"".equals(this.getValue("MR_NO"))) {
			String mr_no = this.getValueString("MR_NO");
			parm.setData("MR_NO", mr_no);
			String sql = "  SELECT A.LINKMAIN_FLG AS SELECT_FLG,A.LINK_NO,B.BAR_CODE,A.ORDER_CODE,A.ORDER_DESC,A.MR_NO,"
					+ "A.SPECIFICATION,A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY, "
					+ "B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY,A.FREQ_CODE,"
					+ "B.BATCH_CODE,B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,H.ROUTE_CHN_DESC, C.FREQ_CHN_DESC,"
					+ "D.ORDER_DESC AS IBS_ORDER_DESC,B.OWN_PRICE "
					+ "FROM ODI_DSPNM A,ODI_SOLUTION B,SYS_PHAFREQ C,SYS_UNIT E,SYS_PHAROUTE H,SYS_UNIT F,SYS_FEE G,SYS_FEE D "
					+ "WHERE  A.IVA_FLG = 'Y' "
					+ " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
					+ "AND A.CASE_NO = B.CASE_NO "
					+ "AND A.ORDER_NO = B.ORDER_NO "
					+ "AND A.ORDER_SEQ = B.ORDER_SEQ "
					+ "AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM "
					+ "AND A.END_DTTM "
					+ "AND A.FREQ_CODE = C.FREQ_CODE "
					+ "AND A.MEDI_UNIT = E.UNIT_CODE "
					+ "AND A.MEDI_UNIT = F.UNIT_CODE "
					+ "AND A.ROUTE_CODE = H.ROUTE_CODE "
					+ "AND G.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = G.ORDER_CODE "
					+ "AND B.IBS_ORDER_DESC = D.ORDER_DESC "
					+ "AND A.MR_NO="
					+ mr_no
					+ ""
					+ QueryDate
					+ "ORDER BY B.ORDER_DATE, "
					+ "B.ORDER_DATETIME,CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END ";
			Parm = new TParm(TJDODBTool.getInstance().select(sql));
			TTable TABLE_D = (TTable) this.getComponent("TABLE_D");
			for (int i = 0, length = Parm.getCount("ORDER_CODE"); i < length; i++) {
				int row = TABLE_D.addRow();
				TABLE_D.setItem(
						row,
						0,
						Parm.getValue("SELECT_FLG").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						1,
						Parm.getValue("LINK_NO").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						2,
						Parm.getValue("BAR_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						3,
						Parm.getValue("ORDER_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						4,
						Parm.getValue("ORDER_DESC").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						5,
						Parm.getValue("SPECIFICATION").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						6,
						Parm.getValue("MEDI_QTY").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(row, 7, Parm.getValue("ROUTE_CHN_DESC")
						.replace("[", "").replace("]", "").replace(" ", "")
						.split(",")[i]);
				TABLE_D.setItem(
						row,
						8,
						Parm.getValue("FREQ_CHN_DESC").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						9,
						Parm.getValue("FREQ_CODE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(
						row,
						10,
						Parm.getValue("EXEC_DATE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				TABLE_D.setItem(row, 11, Parm.getValue("IBS_ORDER_DESC")
						.replace("[", "").replace("]", "").replace(" ", "")
						.split(",")[i]);
				TABLE_D.setItem(
						row,
						12,
						Parm.getValue("OWN_PRICE").replace("[", "")
								.replace("]", "").replace(" ", "").split(",")[i]);
				if ("N".equals(Parm.getValue("SELECT_FLG", i))
						&& !"".equals(Parm.getValue("LINK_NO", i))) {
					TABLE_D.setItem(row, 11, "");
					TABLE_D.setItem(row, 12, "");
				}
			}
			if (Parm.getCount() <= 0) {
				this.messageBox("未查询到数据");
				return;
			}
		} else {
			messageBox("请输入查询信息！");
			return;
		}
	}

	/**
	 * 扫描瓶签号查询信息
	 */
	public void onActionquery() {
		onQuery();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = this.getTTable("TABLE_D");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "住院配液查询");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this.clearValue("BAR_CODE;PAT_NAME;DEPT_CODE;STATION_CODE;MR_NO;START_DATE;END_DATE");
		((TTextField) this.getComponent("BAR_CODE")).requestFocus();
	}
}
