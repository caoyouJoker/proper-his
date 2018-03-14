package com.javahis.ui.inf;

import java.text.DecimalFormat;

import com.dongyang.control.TControl;

import jdo.inf.INFReportTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.INFHospitalAllRateUtil;

import jdo.sys.Operator;

/**
 * <p>
 * Title: ҽԺ��Ⱦ��ͳ�Ʊ�
 * </p>
 * 
 * <p>
 * Description: ҽԺ��Ⱦ����ͳ�Ʊ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author yanmm 2017/5/17
 * @version 1.0
 */
public class INFHospitalAllRateControl extends TControl {
	private static TTable mainTable;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		mainTable = (TTable) getComponent("TABLE");
		super.onInit();
		initUI();
	}

	/**
	 * ��ʼ������
	 */
	private TParm parm;

	public void initUI() {
		setValue("INF_DATE", SystemTool.getInstance().getDate());
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (getValueString("INF_DATE").length() == 0)
			return;
		parm = new TParm();
		parm.setData("INF_DATE", getValueString("INF_DATE").replace("-", "")
				.substring(0, 6));
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm parmRes = new TParm();
		parmRes = selestHospitalAllInf(parm); // ???????????
		// getTable("TABLE").removeRowAll();
		if (parmRes.getCount("DEPT_CHN_DESC") <= 0) {
			messageBox("��������");
			return;
		}
		mainTable.setParmValue(parmRes);
	}

	/**
	 * ����Excel���
	 */
	public void onExcel() {
		TTable mainTable = getTable("TABLE");
		if (mainTable.getRowCount() <= 0) {
			messageBox("�޵�������");
			return;
		}
		INFHospitalAllRateUtil.getInstance().exportExcel(mainTable, "ҽԺ��Ⱦ��ͳ�Ʊ�");
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		setValue("INF_DATE", "");
		getTable("TABLE").removeRowAll();
	}

	/**
	 * ȡ��Table�ؼ�
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selestHospitalAllInf(TParm parm) {
		// this.messageBox("����:::");
		TParm infParm = new TParm();
		String infTime = this.getText("INF_DATE").replaceAll("/", "");
		DecimalFormat df = new DecimalFormat("0.00");

		// ȫ�� ��ƿ�����
		String deptAllNameSql = "SELECT DEPT_CODE,DEPT_ABS_DESC,DEPT_CHN_DESC "
				+ "FROM SYS_DEPT " + "WHERE 1=1";
		TParm deptAllName = new TParm(TJDODBTool.getInstance().select(
				deptAllNameSql));
		int allOutM = 0; // ��Ժ
		int infPeopleM = 0; // ��Ⱦ
		int infCountM = 0; // ��Ⱦ��
		int etiolgexmCountM = 0; // �ͼ���
		double infRateM = 0.00; // ������Ⱦ��
		double countRateM = 0.00; // ���θ�Ⱦ��
		double etiolgexmRateM = 0.00; // �����ͼ���

		for (int i = 0; i < deptAllName.getCount(); i++) {
			// infParm.setData("DEPT_CODE",deptAllName.getValue("DEPT_CODE",i));
			infParm.setData("DEPT_ABS_DESC", i,
					deptAllName.getValue("DEPT_ABS_DESC", i));
			infParm.setData("DEPT_CHN_DESC", i,
					deptAllName.getValue("DEPT_CHN_DESC", i));
			// ���ҳ�Ժ������ COUNT
			String countSql = "SELECT COUNT(DISTINCT CASE_NO) COUNT "
					+ "FROM ADM_INP " + "WHERE TO_CHAR (DS_DATE,'YYYYMM') = '"
					+ infTime + "'" + " AND DS_DEPT_CODE = '"
					+ deptAllName.getValue("DEPT_CODE", i) + "'";
			// System.out.println(countSql);
			TParm count = new TParm(TJDODBTool.getInstance().select(countSql));
			infParm.setData("COUNT", i, count.getValue("COUNT", 0));
			allOutM += infParm.getInt("COUNT", i);
			// System.out.println("��Ժ����:::"+allOutM);
			// ���Ҹ�Ⱦ����INF_PAT_COUNT
			String infPatCountSql = "SELECT COUNT(DISTINCT A.CASE_NO) COUNT"
					+ " FROM INF_CASE A,ADM_INP B "
					+ "WHERE TO_CHAR (A.REPORT_DATE,'YYYYMM') = '" + infTime
					+ "' AND TO_CHAR(B.DS_DATE,'YYYYMM') ='" + infTime + "'"
					+ " AND A.REPORT_NO IS NOT NULL AND A.DEPT_CODE = '"
					+ deptAllName.getValue("DEPT_CODE", i)
					+ "' AND A.CASE_NO=B.CASE_NO";
			// System.out.println(infPatCountSql);
			TParm infPatCount = new TParm(TJDODBTool.getInstance().select(
					infPatCountSql));
			infParm.setData("INF_PAT_COUNT", i,
					infPatCount.getValue("COUNT", 0));
			infPeopleM += infParm.getInt("INF_PAT_COUNT", i);

			// System.out.println("���Ҹ�Ⱦ:::"+infPeopleM);
			// ���Ҹ�Ⱦ����INF_COUNT
			String infCountSql = "SELECT COUNT(A.CASE_NO) COUNT"
					+ " FROM INF_CASE A,ADM_INP B "
					+ "WHERE TO_CHAR (A.REPORT_DATE,'YYYYMM') = '" + infTime
					+ "' AND TO_CHAR(B.DS_DATE,'YYYYMM') ='" + infTime + "'"
					+ " AND A.REPORT_NO IS NOT NULL AND A.DEPT_CODE = '"
					+ deptAllName.getValue("DEPT_CODE", i)
					+ "' AND A.CASE_NO=B.CASE_NO";
			TParm infCount = new TParm(TJDODBTool.getInstance().select(
					infCountSql));
			infParm.setData("INF_COUNT", i, infCount.getValue("COUNT", 0));
			infCountM += infParm.getInt("INF_COUNT", i);
			// System.out.println("���Ҹ�Ⱦ����:::"+infCountM);
			// �ͼ����� ETIOLGEXM_COUNT
			String etiolgexmCountSql = "SELECT COUNT(A.CASE_NO) COUNT "
					+ "FROM INF_CASE A,ADM_INP B "
					+ "WHERE TO_CHAR (A.EXAM_DATE,'YYYYMM') =  '" + infTime
					+ "' AND TO_CHAR(B.DS_DATE,'YYYYMM') = '" + infTime + "' "
					+ "AND A.DEPT_CODE = '"
					+ deptAllName.getValue("DEPT_CODE", i)
					+ "' AND A.CASE_NO=B.CASE_NO AND A.ETIOLGEXM_FLG = 'Y'";
			TParm etiolgexmCount = new TParm(TJDODBTool.getInstance().select(
					etiolgexmCountSql));
			infParm.setData("ETIOLGEXM_COUNT", i,
					etiolgexmCount.getValue("COUNT", 0));
			etiolgexmCountM += infParm.getInt("ETIOLGEXM_COUNT", i);
			// System.out.println("�ͼ�����:::"+etiolgexmCountM);
			// ������Ⱦ�� INF_RATE
			if (count.getDouble("COUNT", 0) != 0) {
				infParm.setData(
						"INF_RATE",
						i,
						df.format(infPatCount.getDouble("COUNT", 0)
								/ count.getDouble("COUNT", 0) * 100));
			} else {
				infParm.setData("INF_RATE", i, "0.00");
			}
			infRateM += infParm.getDouble("INF_RATE", i);
			// System.out.println("������Ⱦ��:::"+infRateM);
			// ���θ�Ⱦ�� COUNT_RATE
			if (count.getDouble("COUNT", 0) != 0) {
				infParm.setData(
						"COUNT_RATE",
						i,
						df.format(infCount.getDouble("COUNT", 0)
								/ count.getDouble("COUNT", 0) * 100));
			} else {
				infParm.setData("COUNT_RATE", i, "0.00");
			}
			countRateM += infParm.getDouble("COUNT_RATE", i);
			// System.out.println("���θ�Ⱦ��:::"+countRateM);
			// �����ͼ��� ETIOLGEXM_RATE
			if (infCount.getDouble("INF_COUNT", 0) != 0) {
				infParm.setData(
						"ETIOLGEXM_RATE",
						i,
						df.format(etiolgexmCount.getDouble("COUNT", 0)
								/ infCount.getDouble("INF_COUNT", 0) * 100));
			} else {
				infParm.setData("ETIOLGEXM_RATE", i, "0.00");
			}
			etiolgexmRateM += infParm.getDouble("ETIOLGEXM_RATE", i);
			// System.out.println("�����ͼ���:::"+etiolgexmRateM);
		}
		// �ϼ�
		infParm.addData("DEPT_ABS_DESC", "�ϼ�:");
		infParm.addData("DEPT_CHN_DESC", " ");
		infParm.addData("COUNT", allOutM);
		infParm.addData("INF_PAT_COUNT", infPeopleM);
		infParm.addData("INF_COUNT", infCountM);
		infParm.addData("ETIOLGEXM_COUNT", etiolgexmCountM);
		infParm.addData("INF_RATE", infRateM);
		infParm.addData("COUNT_RATE", countRateM);
		infParm.addData("ETIOLGEXM_RATE", etiolgexmRateM);
		infParm.setCount(deptAllName.getCount() + 1);

		String icuOutSql = "SELECT COUNT(CASE_NO) COUNT"
				+ " FROM ADM_TRANS_LOG WHERE TO_CHAR (OUT_DATE,'YYYYMM') = '"
				+ infTime + "'"
				+ " AND OUT_DEPT_CODE = '0303' AND PSF_KIND='OUDP'";
		TParm countIcu = new TParm(TJDODBTool.getInstance().select(icuOutSql));
		// System.out.println("�ϼ�ICU::"countIcu);
		infParm.addData("DEPT_ABS_DESC", countIcu.getValue("COUNT", 0));
		infParm.addData("DEPT_CHN_DESC", "ICUת������:");
		infParm.addData("COUNT", " ");
		infParm.addData("INF_PAT_COUNT", " ");
		infParm.addData("INF_COUNT", " ");
		infParm.addData("ETIOLGEXM_COUNT", " ");
		infParm.addData("INF_RATE", " ");
		infParm.addData("COUNT_RATE", " ");
		infParm.addData("ETIOLGEXM_RATE", " ");
		infParm.setCount(deptAllName.getCount() + 2);

		String ccuOutSql = "SELECT COUNT(CASE_NO) COUNT"
				+ " FROM ADM_TRANS_LOG WHERE TO_CHAR (OUT_DATE,'YYYYMM') = '"
				+ infTime + "'"
				+ " AND OUT_DEPT_CODE = '0304' AND PSF_KIND='OUDP'";
		TParm countCcu = new TParm(TJDODBTool.getInstance().select(ccuOutSql));
		// System.out.println("�ϼ�CCU::"+countCcu);
		infParm.addData("DEPT_ABS_DESC", countCcu.getValue("COUNT", 0));
		infParm.addData("DEPT_CHN_DESC", "CCUת������:");
		infParm.addData("COUNT", " ");
		infParm.addData("INF_PAT_COUNT", " ");
		infParm.addData("INF_COUNT", " ");
		infParm.addData("ETIOLGEXM_COUNT", " ");
		infParm.addData("INF_RATE", " ");
		infParm.addData("COUNT_RATE", " ");
		infParm.addData("ETIOLGEXM_RATE", " ");
		infParm.setCount(deptAllName.getCount() + 3);

		// System.out.println("���:::::" + infParm);
		return infParm;
	}

}
