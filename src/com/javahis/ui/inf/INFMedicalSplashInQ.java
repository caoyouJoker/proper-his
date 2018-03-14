package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractButton;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 5.2.11.4.ҽ����Ա�罦�Ǽ��б�
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
 * @author yanmm 2017/5/8
 * @version 1.0
 */
public class INFMedicalSplashInQ extends TControl {

	private static TTable mainTable;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ���TABLE����
		mainTable = (TTable) getComponent("TABLE");
		this.initPage();

	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		setValue("START_TIME", StringTool.rollDate(date, -15).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		onTimeQ();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		mainTable.removeRowAll();// �������
		String startTime = this.getText("START_TIME");
		String endTime = this.getText("END_TIME");
		String user_id = getValueString("USER_ID");
		String user_name = getValueString("USER_NAME");
		String infYN = getValueString("INF_Y_N");
		String sql1 = "";

		// A.USER_ID,A.USER_NAME,A.SEX_CODE,A.DEPT_CODE,A.TEL1,"
		// +
		// "A.WORKING_YEARS,A.HAPPEN_PLACE,A.HAPE_DATE,A.EXPOSE_PLACE,A.INSPECTION_RESULTS,A.REPORT_DATE,
		// ����
		if (!"".equals(user_id)) {
			sql1 += " AND B.USER_ID = '" + user_id + "'";
		}
		// ����
		if (!"".equals(user_name)) {
			sql1 += " AND B.USER_NAME = '" + user_name + "'";
		}
		// ʱ��
		if (this.getCheckBox("TIME_Q").isSelected()) {
			startTime = startTime.substring(0, 10).replaceAll("/", "").trim();
			endTime = endTime.substring(0, 10).replaceAll("/", "").trim();
			sql1 += " AND B.REPORT_DATE BETWEEN TO_DATE('" + startTime
					+ "000000" + "','YYYYMMDDHH24MISS') " + " AND TO_DATE('"
					+ endTime + "235959" + "','YYYYMMDDHH24MISS')";
		}
		// �Ƿ��Ⱦ
		
		if ("1".equals(infYN)) {
			sql1 += "AND B.INFECTION = 'Y'";
		}else if("2".equals(infYN)){
			sql1 += "AND B.INFECTION = 'N'";
		}
		String sql = "SELECT B.USER_ID,B.USER_NAME,B.SEX_CODE,B.DEPT_CODE,B.TEL1"
				+ " ,B.WORKING_YEARS,B.SPLASH_NO,B.HAPE_DATE,B.INFECTION,B.EXPOSE_PLACE "
				+ "	,B.REPORT_DATE ,B.HAPPEN_PLACE,"
				+ "NVL(A.ROLE_ID,'99') AS ROLE_ID "
				+ "FROM INF_SPLASH B LEFT JOIN SYS_OPERATOR A ON A.USER_ID = B.USER_ID "
				+ " WHERE 1=1 " + sql1;

		// System.out.println("1:::::" + sql);
		// this.messageBox("SQL:::::" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
	//	this.messageBox("a"+resultParm);
		if (resultParm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}
		// this.messageBox(resultParm + "");
		mainTable.setParmValue(resultParm);

	}

	/**
	 * getCheckBox
	 * 
	 * @param string
	 * @return
	 */
	private AbstractButton getCheckBox(String string) {
		return (TCheckBox) getComponent(string);
	}

	/**
	 * ʱ���ѯ�ؼ�
	 */
	public void onTimeQ() {
		if (getValueString("TIME_Q").equals("Y")) {
			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
		} else {
			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
		}
	}

	/**
	 * ����
	 */
	public void onRegDetail() {
		// this.messageBox("����");
		int rowIndex = ((TTable) this.getComponent("TABLE")).getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ������");
			return;
		}
		TParm parm = new TParm();
		TParm patInfo = (TParm) callFunction("UI|TABLE|getParmValue");
		parm.setData("SPLASH_NO", patInfo.getValue("SPLASH_NO", rowIndex));
		 this.openWindow(
				"%ROOT%\\config\\inf\\INFMedicalInUI.x", parm);
		/*if ("".equals(this.getValueString("SPLASH_NO"))) {
			this.setValue("SPLASH_NO", result.getValue("SPLASH_NO"));
			this.onQuery();
		}*/

	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.clearValue("START_TIME;END_TIME;PAT_NAME;USER_ID;TimeQ;INF_Y_N");
		mainTable.removeRowAll();
		this.onInit();
	}

}
