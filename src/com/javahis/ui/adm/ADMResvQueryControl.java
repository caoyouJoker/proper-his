package com.javahis.ui.adm;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.sys.SYSRegionTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import com.dongyang.control.TControl;

/**
 * <p>
 * Title: סԺ֤ͳ��
 * </p>
 * 
 * 
 * <p>
 * Description: סԺ֤ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangp 20130707
 * @version 1.0
 */




public class ADMResvQueryControl extends TControl {
	private TTable table;
	private static final String URL = "%ROOT%\\config\\prt\\ADM\\ADMResvQuery.jhw";
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
		// Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}


	public ADMResvQueryControl() {
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String date_s = getValueString("DATE_S");
		String date_e = getValueString("DATE_E");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
//		if (null != this.getValueString("REGION_CODE")
//				&& this.getValueString("REGION_CODE").length() > 0) {
//			region = " AND A.REGION_CODE = '" + getValueString("REGION_CODE") + "'";
//		}
		String sql = 
			" SELECT   A.MR_NO, B.PAT_NAME, A.APP_DATE, A.RESV_DATE, A.ADM_DAYS, A.URG_FLG," +
			" A.ADM_SOURCE, A.OPD_DEPT_CODE, A.OPD_DR_CODE, A.DEPT_CODE," +
			" A.STATION_CODE, A.DR_CODE," +
			" C.IN_DEPT_CODE,C.IN_DATE " +//add by guoy 20151022
			" FROM ADM_RESV A, SYS_PATINFO B," +
			" ADM_INP C " +//add by guoy 20151022
			" WHERE A.MR_NO = B.MR_NO" +
			" AND A.IN_CASE_NO = C.CASE_NO " +// add by guoy 20151022
			" AND C.CANCEL_FLG = 'N' " +//add by huzc 20160303
			" AND A.APP_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')";
		if (null != this.getValueString("URG_FLG")
				&& this.getValueString("URG_FLG").length() > 0) {
			sql += " AND A.URG_FLG = '" + getValueString("URG_FLG") + "'";
		}
		if (null != this.getValueString("ADM_SOURCE")
				&& this.getValueString("ADM_SOURCE").length() > 0) {
			sql += " AND A.ADM_SOURCE = '" + getValueString("ADM_SOURCE") + "'";
		}
		if (null != this.getValueString("OPD_DEPT_CODE")
				&& this.getValueString("OPD_DEPT_CODE").length() > 0) {
			sql += " AND A.OPD_DEPT_CODE = '" + getValueString("OPD_DEPT_CODE") + "'";
		}
		if (null != this.getValueString("OPD_DR_CODE")
				&& this.getValueString("OPD_DR_CODE").length() > 0) {
			sql += " AND A.OPD_DR_CODE = '" + getValueString("OPD_DR_CODE") + "'";
		}
		if (null != this.getValueString("DEPT_CODE")
				&& this.getValueString("DEPT_CODE").length() > 0) {
			sql += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		}
		if (null != this.getValueString("STATION_CODE")
				&& this.getValueString("STATION_CODE").length() > 0) {
			sql += " AND A.STATION_CODE = '" + getValueString("STATION_CODE") + "'";
		}
		if (null != this.getValueString("DR_CODE")
				&& this.getValueString("DR_CODE").length() > 0) {
			sql += " AND A.DR_CODE = '" + getValueString("DR_CODE") + "'";
		}
		if (null != this.getValueString("MR_NO")
				&& this.getValueString("MR_NO").length() > 0) {
			sql += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		}
//			" AND A.RESV_DATE BETWEEN TO_DATE ('', 'YYYYMMDDHH24MISS')" +
//			" AND TO_DATE ('', 'YYYYMMDDHH24MISS')" +
		sql +=
			" ORDER BY A.APP_DATE," +
			" A.RESV_DATE," +
			" A.URG_FLG," +
			" A.ADM_SOURCE," +
			" A.OPD_DEPT_CODE," +
			" A.OPD_DR_CODE," +
			" A.DEPT_CODE," +
			" A.STATION_CODE," +
			" A.DR_CODE," +
			" A.MR_NO";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(result);
		table.setParmValue(result);
		setValue("SUM", result.getCount() == -1 ? 0 : result.getCount());
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ����
		this.setValue("DATE_E",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("DATE_S", date.toString().substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// �󶨿ؼ��¼�
		callFunction("UI|MR_NO|addEventListener", "MR_NO->"
				+ TKeyListener.KEY_RELEASED, this, "onKeyReleased");

	}

	public void onKeyReleased(KeyEvent e) {
		if (e.getKeyCode() != 10) {
			return;
		}
		TTextField mrNO = (TTextField) this.getComponent("MR_NO");
		mrNO.setValue(PatTool.getInstance().checkMrno(mrNO.getValue()));
		mrNO.setFocusable(true);
		this.onQuery();
	}


	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		table.acceptText();
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ӡ������");
			return;
		}
		TParm parm1 = new TParm();
		String[] parmMaps = table.getParmMap().split(";");
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			for (int j = 0; j < parmMaps.length; j++) {
				parm1.addData(parmMaps[j], parm.getValue(parmMaps[j], i));
			}
		}
		for (int i = 0; i < parmMaps.length; i++) {
			parm1.addData("SYSTEM", "COLUMNS", parmMaps[i]);
		}
		parm1.setCount(parm.getCount("MR_NO"));
		TParm result = new TParm();
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("DATE_S")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("DATE_E")), "yyyy/MM/dd");
		result.setData("DATE", "TEXT", sDate + " �� " + eDate);// ͳ��
		Timestamp date =StringTool.getTimestamp(new Date());
		result.setData("PRINT_DATE", "TEXT", date.toString().substring(0, 10).replace('-', '/'));
		result.setData("TODATE", "TEXT", date.toString().substring(0, 10).replace('-', '/'));
		result.setData("T1", parm1.getData());
		result.setData(
				"TITLE",
				"TEXT",
				 "סԺ֤ͳ��");
		result.setData(
				"TITLE1",
				"TEXT",
				(null != Operator.getHospitalCHNFullName() ? Operator.getHospitalCHNShortName() : "����Ժ��"));
		// ¬�������Ʊ���
		// ��β
		result.setData("OPT_USER", "TEXT", Operator.getName());
		this.openPrintWindow(URL, result);

		// ========================================================================================
	}

	/**
	 * ���
	 */
	public void onClear() {
		clearValue("MR_NO;PAT_NAME;URG_FLG;ADM_SOURCE;OPD_DEPT_CODE;OPD_DR_CODE;DEPT_CODE;STATION_CODE;DR_CODE;SUM");
		initPage();
		table.removeRowAll();
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getParmValue();
//		int parmc = parm.getCount("MR_NO");
//		int parmN = parm.getCount("PAT_NAME");
//		int parmm = parm.getCount("OTHER");
//		int parmCC = parm.getCount();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
//		TTable table1 = (TTable)this.getComponent("TABLE");
		ExportExcelUtil.getInstance().exportExcel(table, "סԺ֤ͳ�Ʊ�");
		
	
	}

	/**
	 * ��ѯ������
	 */
	public void onQueryMrno() {
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());

		}		
		setValue("MR_NO", pat.getMrNo());
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		setValue("PAT_NAME", pat.getName());
		this.onQuery();
	}

}
