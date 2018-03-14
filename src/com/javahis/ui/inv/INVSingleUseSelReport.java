package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
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
 * Title: �������ü�¼ͳ��
 * </p>
 * 
 * <p>
 * Description: �������ü�¼ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author fux 20140717
 * @version 1.0
 */
public class INVSingleUseSelReport extends TControl {

	public INVSingleUseSelReport() {

	}

	private static TTable mainTable;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.init();
		initPage();
        
		this.setValue("USE_DEPT", Operator.getDept());

		// if (this.getPopedem("ALL")) {
		// this.callFunction("UI|ORG_CODE|setEnabled", false);
		// }else{
		// this.callFunction("UI|ORG_CODE|setEnabled", true);
		// }

	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ���TABLE����
		mainTable = (TTable) getComponent("TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		TParm parm = new TParm();
		// ���õ����˵�
		getTextField("INV_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE,
				this, "popReturn");
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		mainTable.removeRowAll();// �������
		TParm parm = new TParm();
		String org_code = "";
		String inv_code = "";
		String startTime = "";
		String endTime = "";
		// START_TIME END_TIME
		// ���Ŵ���
		org_code = getValueString("USE_DEPT");
		// ���ʱ���
		inv_code = getValueString("INV_CODE");
		// ��Ӧ��
		String sup_code = getValueString("SUP_CODE");

		// ��ѯsql
		// ���ʱ���,150;��������,200;ʹ������,100;����ͺ�,180;���(SN),200;��Ʒ����,100;����,100;��Ӧ����,200
		// INV_CODE;INV_CHN_DESC;BILL_DATE;DESCRIPTION;BARCODE;BATCH_NO;ORG_CODE;SUP_CODE
		// ������˵���Ⱥ��Ҳ�����м�¼���ᱻ��ʾ
		// ������˵���Ⱥ��������м�¼���ᱻ��ʾ
		String sql = "		SELECT  A.USE_NO,A.SEQ,A.INV_CODE,A.QTY,A.USE_USER,A.USE_DATE,"
				+ "           A.USE_DEPT,A.SUP_CODE,A.REASON,B.INV_CHN_DESC     "
				+ "			FROM 	INV_USE     A,								"
				+ "					INV_BASE     B								"
				+ "			WHERE 	A.INV_CODE = B.INV_CODE					    ";

		// ʹ������
		if (!"".equals(startTime) && !"".equals(endTime)) {
			startTime = this.getValueString("START_DATE");
			endTime = this.getValueString("END_DATE");
			sql += " AND A.USE_DATE BETWEEN TO_DATE('" + startTime
					+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "','YYYYMMDDHH24MISS')";
		}

		// ����
		if (!"".equals(org_code)) {
			sql += " AND A.USE_DEPT = '" + org_code + "'";
		}
		// ���ʱ���
		if (!"".equals(inv_code)) {
			sql += " AND A.INV_CODE = '" + inv_code + "'";
		}
		// // ��Ӧ��
		// if (!"".equals(sup_code)) {
		// sql += " AND A.SUP_CODE = '" + sup_code + "'";
		// }
		sql += " ORDER BY A.USE_NO,A.SEQ,A.INV_CODE ";
		System.out.println("sql--->" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (resultParm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}
		mainTable.setParmValue(resultParm);
	}

	/**
	 * ÿ��λ�Ӷ��Ŵ���
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// ��"."���ָ�

		str1 = new StringBuilder(s[0].toString()).reverse().toString(); // �Ƚ��ַ����ߵ�˳��
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// ����ٽ�˳��ת����
		String str3 = new StringBuilder(str2).reverse().toString();
		// ����С��������
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * �������
	 */
	public void onExcel() {
		// TTable tTable = getTable("tTable");
		if (mainTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(mainTable, "�������ü�¼ͳ�Ʊ���");
		} else {
			this.messageBox("û�л������");
			return;
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// fux modify 20140313
		this
				.clearValue("USE_NO;START_DATE;END_DATE;USE_USER;USE_DEPT;INV_CODE;INV_DESC");
		mainTable.removeRowAll();

	}

	/**
	 * �õ�TextField����
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
	 * 
	 * @param Month
	 *            String �ƶ��·� ��ʽ:yyyyMM
	 * @param Day
	 *            String �ƶ��·� ��ʽ:dd
	 * @param num
	 *            String �Ӽ������� ����Ϊ��λ
	 * @return String
	 */
	public String rollMonth(String Month, String Day, int num) {
		if (Month.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(Month, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�num
		cal.add(cal.MONTH, num);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		Timestamp month = new Timestamp(cal.getTimeInMillis());
		String result = StringTool.getString(month, "yyyyMM");
		String lastDayOfMonth = getLastDayOfMonth(result);
		if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
			result += lastDayOfMonth;
		} else {
			result += Day;
		}
		return result;
	}

	/**
	 * ��ȡָ���·ݵ����һ�������
	 * 
	 * @param date
	 *            String ��ʽ YYYYMM
	 * @return Timestamp
	 */
	public String getLastDayOfMonth(String date) {
		if (date.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�1�����¸���
		cal.add(cal.MONTH, 1);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		// �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
		cal.add(cal.DATE, -1);
		Timestamp result = new Timestamp(cal.getTimeInMillis());
		return StringTool.getString(result, "dd");
	}

	/**
	 * ��ȡָ��n����ǰ������
	 */
	public String getMonthDay(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		// lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		lastDate.add(Calendar.MONTH, +no);// ��n���£���Ϊ���µ�1��
		// lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			return;
		}
		String order_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("INV_CODE").setValue(order_code);
		String order_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("INV_DESC").setValue(order_desc);
	}

}
