package com.javahis.ui.dev;

import java.util.Vector;

import jdo.dev.DevDepTool;
import jdo.dev.DevMMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: �豸�½�
 * </p>
 * 
 * <p>
 * Description:�豸�½�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 20130721
 * </p>
 * 
 * <p>
 * Company: BLUECORE
 * </p>
 * 
 * @author fux
 * 
 * @version 4.0
 */
public class DevAccountMonthControl extends TControl {
	TParm parm = new TParm();

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("DATE", StringTool.getTimestamp(now, "yyyyMMdd"));
		this.clearValue("DEPT_CODE");
		this.clearValue("DEV_CLASS");
		this.clearValue("DEVKIND_CODE");
		this.clearValue("DEVPRO_CODE");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		System.out.println("��ӡ");
		// ͳ���·�
		parm.setData("DATE", this.getValueString("DATE"));
		// �豸����
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// �豸����
		parm.setData("DEV_CLASS", this.getValueString("DEV_CLASS"));
		// �豸����
		parm.setData("DEVKIND_CODE", this.getValueString("DEVKIND_CODE"));
		// �豸����
		parm.setData("DEVPRO_CODE", this.getValueString("DEVPRO_CODE"));
		TParm selParm = DevMMTool.getInstance().queryDevMMStock(parm);
		TParm PrintParm = new TParm();
		// ���ñ�ͷ��Ϣ
		TParm printParm = new TParm();
		printParm.setData("HOSP_NAME_T", Operator.getHospitalCHNFullName());
		// ת��ʱ�����ڸ�ʽ
		// REGION_CODE
		// YYYYMM
		// DEPT_CODE
		// DEV_CODE
		// BATCH_SEQ
		// MM_IN_QTY
		// MM_IN_SCRAPAMT
		// MM_OUT_QTY
		// MM_OUT_SCRAPAMT
		// MM_CHECKMODI_QTY
		// MM_CHECKMODI_SCRAPAMT
		// MM_STOCK_QTY
		// MM_STOCK_SCRAPAMT
		// LAST_MM_STOCK_QTY
		// LAST_MM_STOCK_SCRAPAMT
		// MM_INWAREHOUSE_QTY
		// MM_INWAREHOUSE_SCRAPAMT
		// MM_REGRESSGOODS_QTY
		// MM_REGRESSGOODS_SCRAPAMT
		// MM_GIFTIN_QTY
		// MM_GIFTIN_SCRAPAMT
		// MM_GIFTOUT_QTY
		// MM_GIFTOUT_SCRAPAMT
		// MM_WASTE_QTY
		// MM_WASTE_SCRAPAMT
		// MM_SCRAP_VALUE
		int count = selParm.getCount();
		for (int i = 0; i < count; i++) {
			PrintParm.setRowData(count, selParm, i);
			PrintParm.addData("REGION_CODE", selParm.getData("REGION_CODE", i));
			PrintParm.addData("YYYYMM", selParm.getData("YYYYMM", i));
			PrintParm.addData("DEPT_CODE", selParm.getData("DEPT_CODE", i));
			PrintParm.addData("DEV_CODE", selParm.getData("DEV_CODE", i));
			PrintParm.addData("BATCH_SEQ", selParm.getData("BATCH_SEQ", i));
			PrintParm.addData("MM_IN_QTY", selParm.getData("MM_IN_QTY", i));
			PrintParm.addData("MM_IN_SCRAPAMT",
					selParm.getData("MM_IN_SCRAPAMT", i));
			PrintParm.addData("MM_OUT_QTY", selParm.getData("MM_OUT_QTY", i));
			PrintParm.addData("MM_OUT_SCRAPAMT",
					selParm.getData("MM_OUT_SCRAPAMT", i));
			PrintParm.addData("MM_CHECKMODI_QTY",
					selParm.getData("MM_CHECKMODI_QTY", i));
			PrintParm.addData("MM_CHECKMODI_SCRAPAMT",
					selParm.getData("MM_CHECKMODI_SCRAPAMT", i));
			PrintParm.addData("MM_STOCK_QTY",
					selParm.getData("MM_STOCK_QTY", i));
			PrintParm.addData("MM_STOCK_SCRAPAMT",
					selParm.getData("MM_STOCK_SCRAPAMT", i));
			PrintParm.addData("LAST_MM_STOCK_QTY",
					selParm.getData("LAST_MM_STOCK_QTY", i));
			PrintParm.addData("LAST_MM_STOCK_SCRAPAMT",
					selParm.getData("LAST_MM_STOCK_SCRAPAMT", i));
			PrintParm.addData("MM_INWAREHOUSE_QTY",
					selParm.getData("MM_INWAREHOUSE_QTY", i));
			PrintParm.addData("MM_INWAREHOUSE_SCRAPAMT",
					selParm.getData("MM_INWAREHOUSE_SCRAPAMT", i));
			PrintParm.addData("MM_REGRESSGOODS_QTY",
					selParm.getData("MM_REGRESSGOODS_QTY", i));
			PrintParm.addData("MM_REGRESSGOODS_SCRAPAMT",
					selParm.getData("MM_REGRESSGOODS_SCRAPAMT", i));
			PrintParm.addData("MM_GIFTIN_QTY",
					selParm.getData("MM_GIFTIN_QTY", i));
			PrintParm.addData("MM_GIFTIN_SCRAPAMT",
					selParm.getData("MM_GIFTIN_SCRAPAMT", i));
			PrintParm.addData("MM_GIFTOUT_QTY",
					selParm.getData("MM_GIFTOUT_QTY", i));
			PrintParm.addData("MM_GIFTOUT_SCRAPAMT",
					selParm.getData("MM_GIFTOUT_SCRAPAMT", i));
			PrintParm.addData("MM_WASTE_QTY",
					selParm.getData("MM_WASTE_QTY", i));
			PrintParm.addData("MM_WASTE_SCRAPAMT",
					selParm.getData("MM_WASTE_SCRAPAMT", i));
			PrintParm.addData("MM_SCRAP_VALUE",
					selParm.getData("MM_SCRAP_VALUE", i));
		}
		PrintParm.setCount(count);
		// ������ⵥ�����Ϣ
		PrintParm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "YYYYMM");
		PrintParm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
		PrintParm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_IN_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_IN_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_OUT_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_OUT_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_CHECKMODI_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_CHECKMODI_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_STOCK_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_STOCK_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "LAST_MM_STOCK_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "LAST_MM_STOCK_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_INWAREHOUSE_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_INWAREHOUSE_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_REGRESSGOODS_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_REGRESSGOODS_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTIN_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTIN_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTOUT_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_GIFTOUT_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_WASTE_QTY");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_WASTE_SCRAPAMT");
		PrintParm.addData("SYSTEM", "COLUMNS", "MM_SCRAP_VALUE");
		TParm MMParm = new TParm(); 
		MMParm.setData("TABLE", PrintParm.getData());
		openPrintWindow("%ROOT%\\config\\prt\\dev\\DevMMStock.jhw", printParm);
	}

	/**
	 * ���
	 */
	public void onClear() {
		System.out.println("���");
		this.initPage();
	}

	/**
	 * �۾ɼ���
	 */
	public void onDep() {
		// ԭֵ�����۾�ֵ���ۼ��۾�ֵ����ֵ(���޸�)
		String sql = " SELECT A.DEV_CODE,A.DEPT_CODE,A.UNIT_PRICE,A.MDEP_PRICE,"
				+ " B.DEV_CLASS,B.DEVKIND_CODE,B.DEVPRO_CODE,A.INWAREHOUSE_DATE,"
				+ " A.DEVSEQ_NO,A.REGION_CODE"
				+ " FROM DEV_STOCKDD A,DEV_BASE B "
				+ " WHERE A.DEV_CODE = B.DEV_CODE ";
		// ����
		String deptcode = this.getValueString("DEPT_CODE");
		// �豸����
		String devclass = this.getValueString("DEV_CLASS");
		// �豸����
		String devkind = this.getValueString("DEVKIND_CODE");
		// �豸����
		String devtype = this.getValueString("DEVPRO_CODE");
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);
		// ����
		if (!deptcode.equals("")) {
			SQL.append("AND A.DEPT_CODE='" + deptcode + "'");
		}
		// �豸����
		if (!devtype.equals("")) {
			SQL.append("AND B.DEVPRO_CODE='" + devtype + "'");
		}
		// �豸����
		if (!devclass.equals("")) {
			SQL.append("AND B.DEV_CLASS='" + devclass + "'");
		}
		// �豸����
		if (!devkind.equals("")) {
			SQL.append("AND B.DEVKIND_CODE='" + devkind + "'");
		}
		SQL.append("ORDER BY  A.DEV_CODE,A.DEVSEQ_NO,A.DEPT_CODE,B.DEVPRO_CODE,B.DEV_CLASS,B.DEVKIND_CODE");
		TParm result = new TParm(this.getDBTool().select(SQL.toString()));
		if (result.getCount() <= 0) {
			messageBox("���۾����ݣ�");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			String inWarehouseDate = result.getValue("INWAREHOUSE_DATE", i)
					.toString();
			TParm parmDep = DevDepTool.getInstance().selectSeqDevInf(
					getDevBase(result.getValue("DEV_CODE", i)),
					result.getDouble("UNIT_PRICE", i), inWarehouseDate);
			//System.out.println("parmDep" + parmDep);
			Double depPrice = Double.parseDouble(parmDep.getData("DEP_PRICE")
					.toString());
			Double currPrice = Double.parseDouble(parmDep.getData("CURR_PRICE")
					.toString());
			TParm stockDDParm = new TParm();
			// �ۼ��۾�ֵ
			stockDDParm.addData("DEP_PRICE", depPrice); 
			// ��ֵ
			stockDDParm.addData("CURR_PRICE", currPrice);
			// �豸����
			stockDDParm.addData("DEV_CODE", result.getValue("DEV_CODE", i));
			// DD��
			stockDDParm.addData("DEVSEQ_NO", result.getDouble("DEVSEQ_NO", i));
			// ����
			stockDDParm.addData("REGION_CODE",
					result.getValue("REGION_CODE", i));
			//System.out.println("stockDDParm===" + stockDDParm);
			TParm parm = DevDepTool.getInstance().UpdateMonthDep(stockDDParm);
			if (parm.getErrCode() < 0) {
				messageBox("�۾ɼ���ʧ��");
				//�������
				err(parm.getErrText());
				return;             
			} 
		}
		messageBox("�۾ɼ���ɹ�");
	}

	/**
	 * �½��ת
	 */
	public void onMonth() {
		System.out.println("�½��ת");
		// ͳ���·�
		parm.setData("DATE", this.getValueString("DATE"));
		// �豸����
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// �豸����
		parm.setData("DEV_CLASS", this.getValueString("DEV_CLASS"));
		// �豸����
		parm.setData("DEVKIND_CODE", this.getValueString("DEVKIND_CODE"));
		// �豸����
		parm.setData("DEVPRO_CODE", this.getValueString("DEVPRO_CODE")); 
		//System.out.println("parm" + parm);     
		TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\DEVMonth.x");  
	}            

	/**
	 * �õ��豸����������Ϣ
	 * 
	 * @param devCode
	 *            String
	 * @return TParm
	 */
	public TParm getDevBase(String devCode) {
		String SQL = "SELECT * FROM DEV_BASE WHERE DEV_CODE = '" + devCode
				+ "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		return parm;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ����½�����
	 * 
	 * @return String
	 */
	public String getMonthStartDate(String monthData) {
		if (monthData.trim().length() <= 0) {
			return "";
		}
		if (monthData.substring(4, 6).equals("01")) {
			return this.valiDataMonthDate(monthData);
		}
		int month = Integer.parseInt(monthData.substring(4, 6)) - 1;
		String m = String.valueOf(month);
		if (m.length() == 1) {
			m = "0" + m;
		}
		String result = monthData.substring(0, 4) + m;
		return result;
	}

	/**
	 * ��֤�½�����
	 * 
	 * @param monthData
	 *            String
	 * @return String
	 */
	public String valiDataMonthDate(String monthData) {
		if (monthData.trim().length() <= 0) {
			return "";
		}
		int year = Integer.parseInt(monthData.substring(0, 4)) - 1;
		String result = String.valueOf(year) + "12";
		return result;
	}

	/**
	 * ����½�����
	 * 
	 * @return String
	 */
	public String getMonthEndDate(String monthData) {
		return monthData;
	}

	/**
	 * �༭ͳ���·�
	 * 
	 * @param dateStr
	 *            String
	 * @return String
	 */
	public String getDataValue(String dateStr) {
		if (dateStr.trim().length() <= 0) {
			return "";
		}
		String[] str = dateStr.split("\\/");
		String strDate = "";
		for (int i = 0; i < str.length; i++) {
			strDate += str[i];
		}
		return strDate;
	}
}