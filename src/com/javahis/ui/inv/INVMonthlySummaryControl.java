package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: �����»���Control
 * </p>
 * 
 * <p>
 * Description: �����»���Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>                                     
 *   
 * @author zhangh 2013.12.9
 * @version 1.0
 */

public class INVMonthlySummaryControl extends TControl {

	public INVMonthlySummaryControl() {
	}

	private TTable table;

	/*
	 * �����ʼ��
	 * 
	 */
	public void onInit() {
		initUI();
	}

	/*
	 * ��ʼ������
	 */    
	private void initUI() { 
		Timestamp date = SystemTool.getInstance().getDate();
        String startDate = date.toString().substring(0, 10).replace('-', '/');
		this.setValue("YEAR", startDate);
		table = (TTable) this.getComponent("TABLE");
	}

	/*
	 * ��ѯ����
	 */
	public void onQuery() {
		query();
	}  

	private void query() {
		
		if (this.getValueString("YEAR").length() <= 0 ) {
			this.messageBox("����д��ѯʱ�䣡");
			return;
		}
		
		String strDate = this.getValueString("YEAR");
		String str = strDate.substring(0, 4)+strDate.substring(5, 7);
		
//		TParm subjectCodeTP = new TParm(TJDODBTool.getInstance().select(this.getAccSubjectCodeSql()));		//���ȫ�������Ʒ���
		TParm result = new TParm(TJDODBTool.getInstance().select(this.getMonthlyInfo(str)));		//��ѯ�½��ܱ���Ϣ
		
		TParm tableTP = new TParm();
		
		if (result.getCount() <= 0) {
			this.messageBox("�޲�ѯ���");
			table.removeRowAll();
			return;
		}
		
		//��װ������ʽ
		tableTP.addData("CATEGORY_CHN_DESC", result.getRow(0).getData("CATEGORY_CHN_DESC"));
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", "");
		tableTP.addData("MONTHEXPENDITURE", "");
		tableTP.addData("RESEARCHFUNDINGEXP", "");
		tableTP.addData("CLINICALEXP", "");
		tableTP.addData("LABORATORYEXP", "");
		tableTP.addData("MEDICALSERVICEEXP", "");
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", "");
		
		double totd1 = 0.00;
		double totd2 = 0.00;
		double totd3 = 0.00;
		double totd4 = 0.00;
		double totd5 = 0.00;
		double totd6 = 0.00;
		double totd7 = 0.00;
		
		double d1 = 0.00;	//MONTHINCOME
		double d2 = 0.00;	//MONTHEXPENDITURE
		double d3 = 0.00;	//RESEARCHFUNDINGEXP
		double d4 = 0.00;	//CLINICALEXP
		double d5 = 0.00;	//LABORATORYEXP
		double d6 = 0.00;	//MEDICALSERVICEEXP
		double d7 = 0.00;	//MONTHBALANCE
		for(int i=1;i<=7;i++){
			tableTP.addData("CATEGORY_CHN_DESC", result.getRow(i).getData("CATEGORY_CHN_DESC"));
			tableTP.addData("LASTMONTHBALANCE", result.getRow(i).getData("LASTMONTHBALANCE"));
			tableTP.addData("MONTHINCOME", result.getRow(i).getData("MONTHINCOME"));
			tableTP.addData("MONTHEXPENDITURE", result.getRow(i).getData("MONTHEXPENDITURE"));
			tableTP.addData("RESEARCHFUNDINGEXP", result.getRow(i).getData("RESEARCHFUNDINGEXP"));
			tableTP.addData("CLINICALEXP", result.getRow(i).getData("CLINICALEXP"));
			tableTP.addData("LABORATORYEXP", result.getRow(i).getData("LABORATORYEXP"));
			tableTP.addData("MEDICALSERVICEEXP", result.getRow(i).getData("MEDICALSERVICEEXP"));
			tableTP.addData("DONATIONEXP", result.getRow(i).getData("DONATIONEXP"));
			tableTP.addData("MONTHBALANCE", result.getRow(i).getData("MONTHBALANCE"));
			
			d1 = d1 + result.getRow(i).getDouble("MONTHINCOME");
			d2 = d2 + result.getRow(i).getDouble("MONTHEXPENDITURE");
			d3 = d3 + result.getRow(i).getDouble("RESEARCHFUNDINGEXP");
			d4 = d4 + result.getRow(i).getDouble("CLINICALEXP");
			d5 = d5 + result.getRow(i).getDouble("LABORATORYEXP");
			d6 = d6 + result.getRow(i).getDouble("MEDICALSERVICEEXP");
			d7 = d7 + result.getRow(i).getDouble("MONTHBALANCE");
		}
		
		tableTP.addData("CATEGORY_CHN_DESC", "�ϼƣ�");
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", d1);
		tableTP.addData("MONTHEXPENDITURE", d2);
		tableTP.addData("RESEARCHFUNDINGEXP", d3);
		tableTP.addData("CLINICALEXP", d4);
		tableTP.addData("LABORATORYEXP", d5);
		tableTP.addData("MEDICALSERVICEEXP", d6);
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", d7);
		
		totd1 = totd1 + d1;
		totd2 = totd2 + d2;
		totd3 = totd3 + d3;
		totd4 = totd4 + d4;
		totd5 = totd5 + d5;
		totd6 = totd6 + d6;
		totd7 = totd7 + d7;
		
		
		//�ڶ���
		tableTP.addData("CATEGORY_CHN_DESC", result.getRow(8).getData("CATEGORY_CHN_DESC"));
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", "");
		tableTP.addData("MONTHEXPENDITURE", "");
		tableTP.addData("RESEARCHFUNDINGEXP", "");
		tableTP.addData("CLINICALEXP", "");
		tableTP.addData("LABORATORYEXP", "");
		tableTP.addData("MEDICALSERVICEEXP", "");
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", "");
		
		d1 = 0.00;	//MONTHINCOME
		d2 = 0.00;	//MONTHEXPENDITURE
		d3 = 0.00;	//RESEARCHFUNDINGEXP
		d4 = 0.00;	//CLINICALEXP
		d5 = 0.00;	//LABORATORYEXP
		d6 = 0.00;	//MEDICALSERVICEEXP
		d7 = 0.00;	//MONTHBALANCE
		for(int i=9;i<=12;i++){
			tableTP.addData("CATEGORY_CHN_DESC", result.getRow(i).getData("CATEGORY_CHN_DESC"));
			tableTP.addData("LASTMONTHBALANCE", result.getRow(i).getData("LASTMONTHBALANCE"));
			tableTP.addData("MONTHINCOME", result.getRow(i).getData("MONTHINCOME"));
			tableTP.addData("MONTHEXPENDITURE", result.getRow(i).getData("MONTHEXPENDITURE"));
			tableTP.addData("RESEARCHFUNDINGEXP", result.getRow(i).getData("RESEARCHFUNDINGEXP"));
			tableTP.addData("CLINICALEXP", result.getRow(i).getData("CLINICALEXP"));
			tableTP.addData("LABORATORYEXP", result.getRow(i).getData("LABORATORYEXP"));
			tableTP.addData("MEDICALSERVICEEXP", result.getRow(i).getData("MEDICALSERVICEEXP"));
			tableTP.addData("DONATIONEXP", result.getRow(i).getData("DONATIONEXP"));
			tableTP.addData("MONTHBALANCE", result.getRow(i).getData("MONTHBALANCE"));
			
			d1 = d1 + result.getRow(i).getDouble("MONTHINCOME");
			d2 = d2 + result.getRow(i).getDouble("MONTHEXPENDITURE");
			d3 = d3 + result.getRow(i).getDouble("RESEARCHFUNDINGEXP");
			d4 = d4 + result.getRow(i).getDouble("CLINICALEXP");
			d5 = d5 + result.getRow(i).getDouble("LABORATORYEXP");
			d6 = d6 + result.getRow(i).getDouble("MEDICALSERVICEEXP");
			d7 = d7 + result.getRow(i).getDouble("MONTHBALANCE");
		}
		
		tableTP.addData("CATEGORY_CHN_DESC", "�ϼƣ�");
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", d1);
		tableTP.addData("MONTHEXPENDITURE", d2);
		tableTP.addData("RESEARCHFUNDINGEXP", d3);
		tableTP.addData("CLINICALEXP", d4);
		tableTP.addData("LABORATORYEXP", d5);
		tableTP.addData("MEDICALSERVICEEXP", d6);
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", d7);
		
		totd1 = totd1 + d1;
		totd2 = totd2 + d2;
		totd3 = totd3 + d3;
		totd4 = totd4 + d4;
		totd5 = totd5 + d5;
		totd6 = totd6 + d6;
		totd7 = totd7 + d7;
		
		
		//������
		tableTP.addData("CATEGORY_CHN_DESC", result.getRow(13).getData("CATEGORY_CHN_DESC"));
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", "");
		tableTP.addData("MONTHEXPENDITURE", "");
		tableTP.addData("RESEARCHFUNDINGEXP", "");
		tableTP.addData("CLINICALEXP", "");
		tableTP.addData("LABORATORYEXP", "");
		tableTP.addData("MEDICALSERVICEEXP", "");
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", "");
		
		d1 = 0.00;	//MONTHINCOME
		d2 = 0.00;	//MONTHEXPENDITURE
		d3 = 0.00;	//RESEARCHFUNDINGEXP
		d4 = 0.00;	//CLINICALEXP
		d5 = 0.00;	//LABORATORYEXP
		d6 = 0.00;	//MEDICALSERVICEEXP
		d7 = 0.00;	//MONTHBALANCE
		for(int i=14;i<=16;i++){
			tableTP.addData("CATEGORY_CHN_DESC", result.getRow(i).getData("CATEGORY_CHN_DESC"));
			tableTP.addData("LASTMONTHBALANCE", result.getRow(i).getData("LASTMONTHBALANCE"));
			tableTP.addData("MONTHINCOME", result.getRow(i).getData("MONTHINCOME"));
			tableTP.addData("MONTHEXPENDITURE", result.getRow(i).getData("MONTHEXPENDITURE"));
			tableTP.addData("RESEARCHFUNDINGEXP", result.getRow(i).getData("RESEARCHFUNDINGEXP"));
			tableTP.addData("CLINICALEXP", result.getRow(i).getData("CLINICALEXP"));
			tableTP.addData("LABORATORYEXP", result.getRow(i).getData("LABORATORYEXP"));
			tableTP.addData("MEDICALSERVICEEXP", result.getRow(i).getData("MEDICALSERVICEEXP"));
			tableTP.addData("DONATIONEXP", result.getRow(i).getData("DONATIONEXP"));
			tableTP.addData("MONTHBALANCE", result.getRow(i).getData("MONTHBALANCE"));
			
			d1 = d1 + result.getRow(i).getDouble("MONTHINCOME");
			d2 = d2 + result.getRow(i).getDouble("MONTHEXPENDITURE");
			d3 = d3 + result.getRow(i).getDouble("RESEARCHFUNDINGEXP");
			d4 = d4 + result.getRow(i).getDouble("CLINICALEXP");
			d5 = d5 + result.getRow(i).getDouble("LABORATORYEXP");
			d6 = d6 + result.getRow(i).getDouble("MEDICALSERVICEEXP");
			d7 = d7 + result.getRow(i).getDouble("MONTHBALANCE");
		}
		
		tableTP.addData("CATEGORY_CHN_DESC", "�ϼƣ�");
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", d1);
		tableTP.addData("MONTHEXPENDITURE", d2);
		tableTP.addData("RESEARCHFUNDINGEXP", d3);
		tableTP.addData("CLINICALEXP", d4);
		tableTP.addData("LABORATORYEXP", d5);
		tableTP.addData("MEDICALSERVICEEXP", d6);
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", d7);
		
		totd1 = totd1 + d1;
		totd2 = totd2 + d2;
		totd3 = totd3 + d3;
		totd4 = totd4 + d4;
		totd5 = totd5 + d5;
		totd6 = totd6 + d6;
		totd7 = totd7 + d7;
		
		//������
		tableTP.addData("CATEGORY_CHN_DESC", result.getRow(17).getData("CATEGORY_CHN_DESC"));
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", "");
		tableTP.addData("MONTHEXPENDITURE", "");
		tableTP.addData("RESEARCHFUNDINGEXP", "");
		tableTP.addData("CLINICALEXP", "");
		tableTP.addData("LABORATORYEXP", "");
		tableTP.addData("MEDICALSERVICEEXP", "");
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", "");
		
		d1 = 0.00;	//MONTHINCOME
		d2 = 0.00;	//MONTHEXPENDITURE
		d3 = 0.00;	//RESEARCHFUNDINGEXP
		d4 = 0.00;	//CLINICALEXP
		d5 = 0.00;	//LABORATORYEXP
		d6 = 0.00;	//MEDICALSERVICEEXP
		d7 = 0.00;	//MONTHBALANCE
		for(int i=17;i<=25;i++){
			tableTP.addData("CATEGORY_CHN_DESC", result.getRow(i).getData("CATEGORY_CHN_DESC"));
			tableTP.addData("LASTMONTHBALANCE", result.getRow(i).getData("LASTMONTHBALANCE"));
			tableTP.addData("MONTHINCOME", result.getRow(i).getData("MONTHINCOME"));
			tableTP.addData("MONTHEXPENDITURE", result.getRow(i).getData("MONTHEXPENDITURE"));
			tableTP.addData("RESEARCHFUNDINGEXP", result.getRow(i).getData("RESEARCHFUNDINGEXP"));
			tableTP.addData("CLINICALEXP", result.getRow(i).getData("CLINICALEXP"));
			tableTP.addData("LABORATORYEXP", result.getRow(i).getData("LABORATORYEXP"));
			tableTP.addData("MEDICALSERVICEEXP", result.getRow(i).getData("MEDICALSERVICEEXP"));
			tableTP.addData("DONATIONEXP", result.getRow(i).getData("DONATIONEXP"));
			tableTP.addData("MONTHBALANCE", result.getRow(i).getData("MONTHBALANCE"));
			
			d1 = d1 + result.getRow(i).getDouble("MONTHINCOME");
			d2 = d2 + result.getRow(i).getDouble("MONTHEXPENDITURE");
			d3 = d3 + result.getRow(i).getDouble("RESEARCHFUNDINGEXP");
			d4 = d4 + result.getRow(i).getDouble("CLINICALEXP");
			d5 = d5 + result.getRow(i).getDouble("LABORATORYEXP");
			d6 = d6 + result.getRow(i).getDouble("MEDICALSERVICEEXP");
			d7 = d7 + result.getRow(i).getDouble("MONTHBALANCE");
		}
		
		tableTP.addData("CATEGORY_CHN_DESC", "�ϼƣ�");
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", d1);
		tableTP.addData("MONTHEXPENDITURE", d2);
		tableTP.addData("RESEARCHFUNDINGEXP", d3);
		tableTP.addData("CLINICALEXP", d4);
		tableTP.addData("LABORATORYEXP", d5);
		tableTP.addData("MEDICALSERVICEEXP", d6);
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", d7);
		
		totd1 = totd1 + d1;
		totd2 = totd2 + d2;
		totd3 = totd3 + d3;
		totd4 = totd4 + d4;
		totd5 = totd5 + d5;
		totd6 = totd6 + d6;
		totd7 = totd7 + d7;
		
		
		//���һ�л���
		tableTP.addData("CATEGORY_CHN_DESC", "�ܼƣ�");
		tableTP.addData("LASTMONTHBALANCE", "");
		tableTP.addData("MONTHINCOME", totd1);
		tableTP.addData("MONTHEXPENDITURE", totd2);
		tableTP.addData("RESEARCHFUNDINGEXP", totd3);
		tableTP.addData("CLINICALEXP", totd4);
		tableTP.addData("LABORATORYEXP", totd5);
		tableTP.addData("MEDICALSERVICEEXP", totd6);
		tableTP.addData("DONATIONEXP", "");
		tableTP.addData("MONTHBALANCE", totd7);
		
		
		table.setParmValue(tableTP);
	}

	/*
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
        String startDate = date.toString().substring(0, 10).replace('-', '/');
		table.removeRowAll();
		this.setValue("YEAR", startDate);
	}
	/*
	 * ����
	 */
	public void onCreate(){ 
		  
		String strDate = this.getValueString("YEAR");
		String str = strDate.substring(0, 4)+strDate.substring(5, 7);
		System.out.println("str"+str);
		TParm result = new TParm(TJDODBTool.getInstance().select(this.getAccMonthSql(str)));

		if (result == null || result.getCount() <= 0) {//����
			TParm tp = new TParm();  
			tp.setData("YEAR", 0, strDate.substring(0, 4));
			tp.setData("MONTH", 0, strDate.substring(5, 7));
			TParm rs = TIOM_AppServer.executeAction("action.inv.INVMonthlyAction",
		            "onSave", tp); 
			if (rs.getErrCode() < 0) {   
				err("ERR:" + rs.getErrCode() + rs.getErrText()
						+ rs.getErrName());
				messageBox("����ʧ�ܣ�");  
			}else{
				messageBox("���ɳɹ���");
			}
			return;
		}else{//������
			messageBox("�������������ɣ�"); 
			return;
		}
		
	}
	
	
	
	
	/**
	 * 	��ѯ�½������Ƿ����ɹ�SQL
	 * 
	 * 
	 * */
	private String getAccMonthSql(String strDate){
		
		String sql = "";
		sql = " SELECT ACC_DATE FROM INV_DDACC WHERE ACC_DATE = '" + strDate + "' ";
		return sql;
	}
	
	/**
	 * ��û�Ʒ���code
	 * 
	 */
//	private String getAccSubjectCodeSql(){
//		
//		return " SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE = 'INV_ACC' ORDER BY CATEGORY_CODE ASC";
//	}
	
	private String getMonthlyInfo(String strDate){
		//ԭ��
		//return " SELECT * FROM  SYS_CATEGORY S LEFT JOIN INV_DDACC D ON D.ACC_SUBJECT_CODE = S.CATEGORY_CODE WHERE S.RULE_TYPE = 'INV_ACC' AND D.ACC_DATE = '" + strDate + "' ORDER BY S.CATEGORY_CODE ASC ";
		//����
		//return " SELECT * FROM  SYS_CATEGORY S LEFT JOIN INV_DDACC D ON D.ACC_SUBJECT_CODE = S.CATEGORY_CODE WHERE S.RULE_TYPE = 'INV_ACC_M' AND D.ACC_DATE = '" + strDate + "' ORDER BY S.CATEGORY_CODE ASC ";
		//С��
		return " SELECT * FROM  SYS_CATEGORY S LEFT JOIN INV_DDACC D ON D.ACC_SUBJECT_CODE = S.CATEGORY_CODE WHERE S.RULE_TYPE IN ('INV_ACC_D','INV_ACC_M')    AND D.ACC_DATE = '" + strDate + "' ORDER BY S.CATEGORY_CODE ASC ";
	}
	   
	   
	/*  
	 * ��ӡ����
	 */
	public void onPrint() {
		print();
	}   

	private void print() {
		if (table.getRowCount() <= 0) {
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		String strDate = this.getValueString("YEAR");
		TParm printParm = new TParm();
		TParm tableData = table.getParmValue();
		TParm printData = new TParm();
		
		for (int i = 0; i < tableData.getCount("CATEGORY_CHN_DESC"); i++) {
			printData.addData("CATEGORY_CHN_DESC", tableData.getData("CATEGORY_CHN_DESC", i));
			printData.addData("LASTMONTHBALANCE", tableData.getData("LASTMONTHBALANCE", i));
			printData.addData("MONTHINCOME", tableData.getData("MONTHINCOME", i));
			printData.addData("MONTHEXPENDITURE", tableData.getData("MONTHEXPENDITURE", i));
			printData.addData("RESEARCHFUNDINGEXP", tableData.getData("RESEARCHFUNDINGEXP", i));
			printData.addData("CLINICALEXP", tableData.getData("CLINICALEXP", i));
			printData.addData("LABORATORYEXP", tableData.getData("LABORATORYEXP", i));
			printData.addData("MEDICALSERVICEEXP", tableData.getData("MEDICALSERVICEEXP", i));
			printData.addData("DONATIONEXP", tableData.getData("DONATIONEXP", i));
			printData.addData("MONTHBALANCE", tableData.getData("MONTHBALANCE", i));
		}
		printData.setCount(tableData.getCount("CATEGORY_CHN_DESC"));
		printData.addData("SYSTEM", "COLUMNS", "CATEGORY_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "LASTMONTHBALANCE");
		printData.addData("SYSTEM", "COLUMNS", "MONTHINCOME");
		printData.addData("SYSTEM", "COLUMNS", "MONTHEXPENDITURE");
		printData.addData("SYSTEM", "COLUMNS", "RESEARCHFUNDINGEXP");
		printData.addData("SYSTEM", "COLUMNS", "CLINICALEXP");
		printData.addData("SYSTEM", "COLUMNS", "LABORATORYEXP");
		printData.addData("SYSTEM", "COLUMNS", "MEDICALSERVICEEXP");
		printData.addData("SYSTEM", "COLUMNS", "DONATIONEXP");
		printData.addData("SYSTEM", "COLUMNS", "MONTHBALANCE");
		
		printParm.setData("TABLE", printData.getData());

		printParm.setData("TITLE", "TEXT", "̩�������Ѫ�ܲ�ҽԺ" + strDate.substring(0, 4) + "��" + strDate.substring(5, 7) + "�²��Ϻ�����ܱ�");
		printParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());

		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVMonthlySummaryReport.jhw",
				printParm);
	}

	 
}
