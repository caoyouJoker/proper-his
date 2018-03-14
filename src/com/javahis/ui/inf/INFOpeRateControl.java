package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.sta.STATool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

public class INFOpeRateControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}

	private void initPage() {
		this.clearValue("START_DATE;END_DATE;DEPT_CODE");
		Timestamp startime = STATool.getInstance().getLastMonth();
		Timestamp endtime = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", startime);
		this.setValue("END_DATE", endtime);
		this.callFunction("UI|TABLE|setParmValue", new TParm());

		// ��������
		this.setValue("REGION_CODE", "H01");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String regionCode = this.getValueString("REGION_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String startDate = this.getValueString("START_DATE").substring(0, 10);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp endtime = SystemTool.getInstance().getDate();
		String endDate = sdf.format(endtime).substring(0, 10);
		//  ���ݿ��ҡ��пڷ������ ��ѯ
		String sql = " SELECT DEPT_CHN_DESC, "
				+ " CHN_DESC,"
				+ " WM_CONCAT (INF_OPESITE) AS INF_OPESITE ,"
				+ " SUM(AMOUNT1) AS AMOUNT1 ,"
				+ " SUM(AMOUNT2) AS AMOUNT2 "
				+ " FROM (SELECT D.DEPT_CHN_DESC,"
				+ "  E.CHN_DESC,"
				+ "  '' AS INF_OPESITE,"
				+ "  COUNT (A.OPBOOK_SEQ) AS AMOUNT1,"
				+ "  0 AS AMOUNT2"
				+ "  FROM OPE_OPBOOK A,"
				+ "  ADM_INP B,"
				+ "  SYS_OPERATIONICD C,"
				+ "  SYS_DEPT D,"
				+ "  SYS_DICTIONARY E"
				+ "   WHERE     A.CASE_NO = B.CASE_NO"
				+ "   AND A.OP_CODE1 = C.OPERATION_ICD"
				+ "   AND A.OP_DEPT_CODE = D.DEPT_CODE"
				+ "   AND C.OPE_INCISION = E.ID"
				+ "   AND E.GROUP_ID = 'OPE_INCISION'"
				+ "   AND C.OPE_INCISION IS NOT NULL  ";
		if(!"".equals(regionCode)){
			sql += " AND B.REGION_CODE = '"+ regionCode +"' ";
				}
			if(!"".equals(deptCode)){
				sql += " AND A.OP_DEPT_CODE = '"+ deptCode +"' "; 
			}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND B.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
			}		
				sql += " GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC"
				+ " UNION"
				+ " SELECT D.DEPT_CHN_DESC,"
				+ "       E.CHN_DESC,"
				+ "       WM_CONCAT (A.INF_OPESITE) AS INF_OPESITE,"
				+ "       0 AS AMOUNT1,"
				+ "       COUNT (B.REPORT_NO) AS AMOUNT2"
				+ "  FROM SYS_OPERATIONICD A,"
				+ "       INF_CASE B,"
				+ "       ADM_INP C,"
				+ "       SYS_DEPT D,"
				+ "       SYS_DICTIONARY E"
				+ " WHERE     A.OPERATION_ICD = B.OP_CODE"
				+ "       AND B.CASE_NO = C.CASE_NO"
				+ "       AND B.DEPT_CODE = D.DEPT_CODE"
				+ "       AND A.OPE_INCISION = E.ID"
				+ "       AND E.GROUP_ID = 'OPE_INCISION'"
				+ "       AND A.OPE_INCISION IS NOT NULL"
				+ "       AND B.OPBOOK_SEQ IS NOT NULL";
		if(!"".equals(regionCode)){
			sql += " AND C.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sql += " AND B.DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND C.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sql += "	GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC" + " )"
				+ " GROUP BY DEPT_CHN_DESC, CHN_DESC"
				+ " ORDER BY  DEPT_CHN_DESC, CHN_DESC";
		System.out.println("sql:" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//���ݿ��ҷ����ѯ
		String sqlDept = " SELECT DEPT_CHN_DESC, "
				+ " WM_CONCAT (INF_OPESITE) AS INF_OPESITE, "
				+ " SUM(AMOUNT1) AS  AMOUNT1 ,"
				+ " SUM(AMOUNT2) AS  AMOUNT2"
				+ " FROM (SELECT D.DEPT_CHN_DESC,"
				+ "  E.CHN_DESC,"
				+ "  '' AS INF_OPESITE,"
				+ "  COUNT (A.OPBOOK_SEQ) AS AMOUNT1,"
				+ "  0 AS AMOUNT2"
				+ "  FROM OPE_OPBOOK A,"
				+ "  ADM_INP B,"
				+ "  SYS_OPERATIONICD C,"
				+ "  SYS_DEPT D,"
				+ "  SYS_DICTIONARY E"
				+ "   WHERE     A.CASE_NO = B.CASE_NO"
				+ "   AND A.OP_CODE1 = C.OPERATION_ICD"
				+ "   AND A.OP_DEPT_CODE = D.DEPT_CODE"
				+ "   AND C.OPE_INCISION = E.ID"
				+ "   AND E.GROUP_ID = 'OPE_INCISION'"
				+ "   AND C.OPE_INCISION IS NOT NULL  ";
		if(!"".equals(regionCode)){
			sql += " AND B.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sqlDept += " AND A.OP_DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sqlDept += " AND B.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
						+ endDate
						+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sqlDept += " GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC"
				+ " UNION"
				+ " SELECT D.DEPT_CHN_DESC,"
				+ "       E.CHN_DESC,"
				+ "       WM_CONCAT (A.INF_OPESITE) AS INF_OPESITE,"
				+ "       0 AS AMOUNT1,"
				+ "       COUNT (B.REPORT_NO) AS AMOUNT2"
				+ "  FROM SYS_OPERATIONICD A,"
				+ "       INF_CASE B,"
				+ "       ADM_INP C,"
				+ "       SYS_DEPT D,"
				+ "       SYS_DICTIONARY E"
				+ " WHERE     A.OPERATION_ICD = B.OP_CODE"
				+ "       AND B.CASE_NO = C.CASE_NO"
				+ "       AND B.DEPT_CODE = D.DEPT_CODE"
				+ "       AND A.OPE_INCISION = E.ID"
				+ "       AND E.GROUP_ID = 'OPE_INCISION'"
				+ "       AND A.OPE_INCISION IS NOT NULL"
				+ "       AND B.OPBOOK_SEQ IS NOT NULL";
		if(!"".equals(regionCode)){
			sql += " AND C.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sqlDept += " AND B.DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sqlDept += " AND C.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sqlDept += "	GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC" + " )"
				+ " GROUP BY DEPT_CHN_DESC" + " ORDER BY DEPT_CHN_DESC";
//		System.out.println("sql:" + sql);
		TParm parmDept = new TParm(TJDODBTool.getInstance().select(sqlDept));
		
		TParm parmShow = new TParm();
		
		double Sum1 = 0;// �ϼ�ͳ����������
		double Sum2 = 0;// �ϼ�ͳ�Ƹ�Ⱦ����
		double a = 0;//��������ϸ��
		double b = 0;//��Ⱦ����ϸ��
		double rate = 0;//���θ�Ⱦ��ϸ��
		
		int bCount = 0;//������ǳ�п�
		int sCount = 0;//������п�
		int qCount = 0;//��������/ǻ϶
		int b1Count = 0;//�����ǳ�п�
		int s1Count = 0;//������п�
		int q1Count = 0;//��������/ǻ϶
		int b2Count = 0;//�����ǳ�п�
		int s2Count = 0;//������п�
		int q2Count = 0;//��������/ǻ϶
		int b3Count = 0;//III���ǳ�п�
		int s3Count = 0;//III����п�
		int q3Count = 0;//III������/ǻ϶
		int b4Count = 0;//IV���ǳ�п�
		int s4Count = 0;//IV����п�
		int q4Count = 0;//IV������/ǻ϶
		
		int opesite11 =0;//������������
		int opesite12 =0;//�����Ⱦ����
		int opesite21 =0;//������������
		int opesite22 =0;//�����Ⱦ����
		int opesite31 =0;//III����������
		int opesite32 =0;//III���Ⱦ����
		int opesite41 =0;//IV����������
		int opesite42 =0;//IV���Ⱦ����
		// ������ѭ��
		for (int i = 0; i < parm.getCount(); i++) {
			String dept = parm.getValue("DEPT_CHN_DESC", i);
			String opeInsion = parm.getValue("CHN_DESC", i);
			String infOpesite = parm.getValue("INF_OPESITE", i);
			parmShow.addData("DEPT_CHN_DESC", dept);
			parmShow.addData("CHN_DESC", opeInsion);
			parmShow.addData("AMOUNT1", parm.getValue("AMOUNT1", i));
			parmShow.addData("AMOUNT2", parm.getValue("AMOUNT2", i));
			a = Double.parseDouble(parm.getValue("AMOUNT1", i));
			b = Double.parseDouble(parm.getValue("AMOUNT2", i));
			rate = b / a * 100;
			parmShow.addData("RATE", rate);
			Sum1 = Sum1 + a;
			Sum2 = Sum2 + b;
			//�����Ⱦ������λ����ǳ�пڡ���пڡ�����/ǻ϶��������
			if ("".equals(infOpesite)) {
				parmShow.addData("B_AMOUNT", 0);
				parmShow.addData("S_AMOUNT", 0);
				parmShow.addData("Q_AMOUNT", 0);
			}
			if (!infOpesite.contains(",")) {

				if ("01".equals(infOpesite)) {
					parmShow.addData("B_AMOUNT", 1);
					parmShow.addData("S_AMOUNT", 0);
					parmShow.addData("Q_AMOUNT", 0);
				} else if ("02".equals(infOpesite)) {
					parmShow.addData("S_AMOUNT", 1);
					parmShow.addData("B_AMOUNT", 0);
					parmShow.addData("Q_AMOUNT", 0);
				} else if ("03".equals(infOpesite)) {
					parmShow.addData("Q_AMOUNT", 1);
					parmShow.addData("B_AMOUNT", 0);
					parmShow.addData("S_AMOUNT", 0);
				}
			} else {
				String[] str = infOpesite.split(",");
				TParm result = getString(str);
				parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
				parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
				parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
			}
			
			
			//�пڷ��� ͳ�ƺϼ�
			if("���ࣨ��ࣩ�п�".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite11 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite12 +=amount2;
			}
			if("���ࣨ���-��Ⱦ���п�".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite21 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite22 +=amount2;
			}
			if("���ࣨ��Ⱦ���п�".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite31 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite32 +=amount2;
			}
			if("���ࣨ�ۻ�-��Ⱦ���п�".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite41 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite42 +=amount2;
			}
			
			// ����С��ѭ��
			for (int j = 0; j < parmDept.getCount(); j++) {
				String deptJ = parmDept.getValue("DEPT_CHN_DESC", j);
				if (i < parm.getCount() - 1) {
					String deptNew = parm.getValue("DEPT_CHN_DESC", i + 1);
					//ѭ����ǰ���Һ���һ�����ұȽϣ������һ����������С�ƣ�
					if (!deptNew.equals(dept) && dept.equals(deptJ)) {
						parmShow.addData("DEPT_CHN_DESC", deptJ);
						parmShow.addData("CHN_DESC", "С��");
						parmShow.addData("AMOUNT1", parmDept.getValue(
								"AMOUNT1", j));
						parmShow.addData("AMOUNT2", parmDept.getValue(
								"AMOUNT2", j));
						a = Double.parseDouble(parmDept.getValue("AMOUNT1", j));
						b = Double.parseDouble(parmDept.getValue("AMOUNT2", j));
						rate = b / a * 100;
						parmShow.addData("RATE", rate);
						
						String infOpesite1 = parmDept.getValue("INF_OPESITE", j);
						
						if ("".equals(infOpesite1)) {
							parmShow.addData("B_AMOUNT", 0);
							parmShow.addData("S_AMOUNT", 0);
							parmShow.addData("Q_AMOUNT", 0);
						}
						if (!infOpesite1.contains(",")) {

							if ("01".equals(infOpesite1)) {
								parmShow.addData("B_AMOUNT", 1);
								parmShow.addData("S_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("02".equals(infOpesite1)) {
								parmShow.addData("S_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("03".equals(infOpesite1)) {
								parmShow.addData("Q_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("S_AMOUNT", 0);
							}
							
						} else {
							String[] str = infOpesite1.split(",");
							TParm result = getString(str);
							parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
							parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
							parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
						}
					}
				}
				//����С�����һ��
				if (i == parm.getCount() - 1) {
					if (dept.equals(deptJ)) {
						parmShow.addData("DEPT_CHN_DESC", deptJ);
						parmShow.addData("CHN_DESC", "С��");
						parmShow.addData("AMOUNT1", parmDept.getValue(
								"AMOUNT1", j));
						parmShow.addData("AMOUNT2", parmDept.getValue(
								"AMOUNT2", j));
						a = Double.parseDouble(parmDept.getValue("AMOUNT1", j));
						b = Double.parseDouble(parmDept.getValue("AMOUNT2", j));
						rate = b / a * 100;
						parmShow.addData("RATE", rate);
						
						String infOpesite1 = parmDept.getValue("INF_OPESITE", j);
						if ("".equals(infOpesite1)) {
							parmShow.addData("B_AMOUNT", 0);
							parmShow.addData("S_AMOUNT", 0);
							parmShow.addData("Q_AMOUNT", 0);
						}
						if (!infOpesite1.contains(",")) {

							if ("01".equals(infOpesite1)) {
								parmShow.addData("B_AMOUNT", 1);
								parmShow.addData("S_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("02".equals(infOpesite1)) {
								parmShow.addData("S_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("03".equals(infOpesite1)) {
								parmShow.addData("Q_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("S_AMOUNT", 0);
							}

						} else {
							String[] str = infOpesite1.split(",");
							TParm result = getString(str);
							parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
							parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
							parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
						}
					}
				}

			}
			
		}
//		System.out.println("parm:"+parm);
		
		//�ϼ�
		int bAmount = 0;//��ǳ�п�������
		int sAmount = 0;//��п�������
		int qAmount = 0;//����/ǻ϶������
		//��Ⱦ������λ ������
		for(int i=0;i<parmShow.getCount("B_AMOUNT");i++){
			bAmount = Integer.parseInt(parmShow.getValue("B_AMOUNT",i));
			sAmount = Integer.parseInt(parmShow.getValue("S_AMOUNT",i));
			qAmount = Integer.parseInt(parmShow.getValue("Q_AMOUNT",i));
			//�����пڷ��� ��Ⱦ������λ  ͳ��
			if("���ࣨ��ࣩ�п�".equals(parmShow.getValue("CHN_DESC",i))){
//				messageBox_(parmShow.getValue("CHN_DESC",i));
				b1Count += bAmount;
				s1Count += sAmount;
				q1Count += qAmount;
			}
			if("���ࣨ���-��Ⱦ���п�".equals(parmShow.getValue("CHN_DESC",i))){
				b2Count += bAmount;
				s2Count += sAmount;
				q2Count += qAmount;
			}
			if("���ࣨ��Ⱦ���п�".equals(parmShow.getValue("CHN_DESC",i))){
				b3Count += bAmount;
				s3Count += sAmount;
				q3Count += qAmount;
			}
			if("���ࣨ�ۻ�-��Ⱦ���п�".equals(parmShow.getValue("CHN_DESC",i))){
				b4Count += bAmount;
				s4Count += sAmount;
				q4Count += qAmount;
			}
			bCount += bAmount;
			sCount += sAmount;
			qCount += qAmount;
		}
//		messageBox_(parmDept.getCount("DEPT_CHN_DESC"));
		if(parmDept.getCount("DEPT_CHN_DESC") > 1){
			//"I��(���)�п� ͳ��
			double opesite11l = (int) opesite11;
			double opesite12l = (int) opesite12;
			parmShow.addData("DEPT_CHN_DESC", "ȫԺ�ϼ�");
			parmShow.addData("CHN_DESC", "I��(���)�п�");
			parmShow.addData("AMOUNT1", opesite11);
			parmShow.addData("AMOUNT2", opesite12);
			if(!Double.isNaN(opesite12l / opesite11l * 100)){
				parmShow.addData("RATE", opesite12l / opesite11l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b1Count);
			parmShow.addData("S_AMOUNT", s1Count);
			parmShow.addData("Q_AMOUNT", q1Count);
			//II��(���-��Ⱦ)�п� ͳ��
			double opesite21l = (int) opesite21;
			double opesite22l = (int) opesite22;
			parmShow.addData("DEPT_CHN_DESC", "ȫԺ�ϼ�");
			parmShow.addData("CHN_DESC", "II��(���-��Ⱦ)�п�");
			parmShow.addData("AMOUNT1", opesite21);
			parmShow.addData("AMOUNT2", opesite22);
			if(!Double.isNaN(opesite22l / opesite21l * 100)){
				parmShow.addData("RATE", opesite22l / opesite21l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b2Count);
			parmShow.addData("S_AMOUNT", s2Count);
			parmShow.addData("Q_AMOUNT", q2Count);
			//III��(��Ⱦ)�п� ͳ��
			double opesite31l = (int) opesite31;
			double opesite32l = (int) opesite32;
			parmShow.addData("DEPT_CHN_DESC", "ȫԺ�ϼ�");
			parmShow.addData("CHN_DESC", "III��(��Ⱦ)�п�");
			parmShow.addData("AMOUNT1", opesite31);
			parmShow.addData("AMOUNT2", opesite32);
			if(!Double.isNaN(opesite32l / opesite31l * 100)){
				parmShow.addData("RATE", opesite32l / opesite31l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b3Count);
			parmShow.addData("S_AMOUNT", s3Count);
			parmShow.addData("Q_AMOUNT", q3Count);
			//IV��(�ۻ�-��Ⱦ)�п� ͳ��
			double opesite41l = (int) opesite41;
			double opesite42l = (int) opesite42;
			parmShow.addData("DEPT_CHN_DESC", "ȫԺ�ϼ�");
			parmShow.addData("CHN_DESC", "IV��(�ۻ�-��Ⱦ)�п�");
			parmShow.addData("AMOUNT1", opesite41);
			parmShow.addData("AMOUNT2", opesite42);
			if(!Double.isNaN(opesite42l / opesite41l * 100) ){
				parmShow.addData("RATE", opesite42l / opesite41l * 100);
			}else{
				parmShow.addData("RATE", 0.00);
			}
			parmShow.addData("B_AMOUNT", b4Count);
			parmShow.addData("S_AMOUNT", s4Count);
			parmShow.addData("Q_AMOUNT", q4Count);
			
			//ȫԺ�ϼ� 
			int sum1 = (int) Sum1;
			int sum2 = (int) Sum2;
			parmShow.addData("DEPT_CHN_DESC", "ȫԺ�ϼ�");
			parmShow.addData("CHN_DESC", "����");
			parmShow.addData("AMOUNT1", sum1);
			parmShow.addData("AMOUNT2", sum2);
			parmShow.addData("RATE", Sum2 / Sum1 * 100);
			parmShow.addData("B_AMOUNT", bCount/2);
			parmShow.addData("S_AMOUNT", sCount/2);
			parmShow.addData("Q_AMOUNT", qCount/2);
		}
		
//		System.out.println("parmShow:"+parmShow);
		this.getTable("TABLE").setParmValue(parmShow);
		if (this.getTable("TABLE").getRowCount() < 1) {
			// ��������
			this.messageBox("��������");
		}
	}

	/**
	 * �����Ⱦ������λ ��Ӧ����
	 * @param s
	 * @return
	 */
	public TParm getString(String[] s) {
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		for (int i = 0; i < s.length; i++) {
			if ("01".equals(s[i])) {
				num1++;
			} else if ("02".equals(s[i])) {
				num2++;
			} else if ("03".equals(s[i])) {
				num3++;
			}
		}
		TParm p = new TParm();
		p.setData("B_AMOUNT", num1);
		p.setData("S_AMOUNT", num2);
		p.setData("Q_AMOUNT", num3);
		return p;

	}

	/**
	 * ����excel
	 */
	public void onExport() {
		if (getTable("TABLE").getRowCount() <= 0) {
			this.messageBox("û�е�������");
			return;
		}
		INFOpeRateUtil.getInstance().exportExcel(getTable("TABLE"),
				"������λ��Ⱦ��ͳ�Ʊ�");
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
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
}
