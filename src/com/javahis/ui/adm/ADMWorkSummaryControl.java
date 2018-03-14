package com.javahis.ui.adm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

import java.text.DecimalFormat;
/**
 * <p>
 * Title: סԺ��������ͳ��
 * </p>
 * 
 * <p>
 * Description:סԺ��������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author yyn 201710
 * @version 1.0
 */

public class ADMWorkSummaryControl extends TControl{
	private TTable table;
	DecimalFormat df1 = new DecimalFormat("########0.00");
	public ADMWorkSummaryControl(){
		
	}
	
	/*
	 * ��ʼ��
	 */
	public void onInit(){
		initPage();
	}
	
	/*
	 * ��ʼ������
	 */
	private void initPage(){
		table = (TTable) this.getComponent("TABLE");
		
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
			"yyyyMMddHHmmss"));// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
			"yyyyMMddHHmmss"));// ����ʱ��
		this.setValue("USER_ID", Operator.getID());
		table.removeRowAll();		
	}
	
	/**
     * ���
     */
    public void onClear(){
    	initPage();
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "סԺ��������ͳ�Ʊ�");
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
    	TParm returnParm = new TParm();
    	StringBuffer sql=new StringBuffer();
    	StringBuffer sql1=new StringBuffer();
    	if (this.getValueString("START_DATE").length() == 0) {
			messageBox("��ʼʱ�䲻��ȷ!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("����ʱ�䲻��ȷ!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		String id = "";
		
		//Ԥ�������ݲ�ѯSQL���
		sql.append("SELECT N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,SUM(Y.PRE_AMT) AS PRE_AMT,SUM(Y.NUM) AS NUM,Y.PAY_TYPE"
				+ " FROM "
				+ "(SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT"
				+ " FROM ADM_INP A,SYS_OPERATOR B"
				+ " WHERE A.ADM_CLERK=B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS') GROUP BY A.ADM_CLERK,B.USER_NAME) N,"
				+ "(SELECT BIL_PAY.TRANSACT_TYPE,"
				+ " CASE WHEN TRANSACT_TYPE ='01' THEN SUM(BIL_PAY.PRE_AMT) WHEN TRANSACT_TYPE ='02' THEN SUM(BIL_PAY.PRE_AMT) END AS PRE_AMT,"
				+ " CASE WHEN TRANSACT_TYPE ='01' THEN COUNT(BIL_PAY.MR_NO) WHEN TRANSACT_TYPE ='02' THEN COUNT(BIL_PAY.MR_NO) END AS NUM,"
				+ " BIL_PAY.PAY_TYPE,BIL_PAY.RECEIPT_NO,BIL_PAY.CASHIER_CODE"
				+ " FROM BIL_PAY"
				+ " WHERE BIL_PAY.CHARGE_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')" 
				+ " GROUP BY BIL_PAY.TRANSACT_TYPE,BIL_PAY.PAY_TYPE,BIL_PAY.RECEIPT_NO,BIL_PAY.CASHIER_CODE"
				+ " ORDER BY NUM) Y"
				+ " WHERE Y.CASHIER_CODE = N.ADM_CLERK(+)");
				
		id = this.getValueString("USER_ID");		
		if (id.length() > 0) {//�շ�Ա��ѡ��
			sql.append(" AND N.ADM_CLERK = '" + id +"' ");
		}				
		sql.append(" GROUP BY N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,Y.PAY_TYPE"
				+ " ORDER BY N.ADM_CLERK");
		TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (bilPayParm.getErrCode() < 0) {
			System.out.println("error");
			return;
		}
		
		//�շ�Ա��ѯSQL���
		sql1.append("SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT "
				+ " FROM ADM_INP A,SYS_OPERATOR B"
				+ " WHERE A.ADM_CLERK = B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')");				
		id = this.getValueString("USER_ID");		
		if (id.length() > 0) {//�շ�Ա��ѡ��
			sql1.append(" AND A.ADM_CLERK = '" + id +"' ");
		}
				
		sql1.append(" GROUP BY A.ADM_CLERK,B.USER_NAME");
		TParm nameParm = new TParm(TJDODBTool.getInstance().select(sql1.toString()));
		if (nameParm.getErrCode() < 0) {
			System.out.println("error1");
			return;
		}
		
		//��Ժ����SQL���
		StringBuffer sql2=new StringBuffer();
    	sql2.append("SELECT N.ADM_CLERK,N.PAT_NAME,N.PAT_COUNT,Y.TRANSACT_TYPE,Y.PAY_CASH,Y.PAY_CHECK,Y.PAY_BANK_CARD,Y.PRE_AMT,Y.MR_NO,A.CTZ1_CODE "
    				+ " FROM "
    				+ " (SELECT A.ADM_CLERK,B.USER_NAME AS PAT_NAME,COUNT(A.CASE_NO) AS PAT_COUNT"
    				+ " FROM ADM_INP A,SYS_OPERATOR B"
    				+ " WHERE A.ADM_CLERK = B.USER_ID(+) AND A.IN_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')"
    				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')"
    				+ " GROUP BY A.ADM_CLERK,B.USER_NAME) N,"
    				+ " (SELECT "
    				+ " CASE WHEN AR_AMT-PAY_BILPAY>0 THEN '03' ELSE '04' END AS TRANSACT_TYPE,"
    				+ " PAY_CASH,PAY_CHECK,PAY_BANK_CARD, AR_AMT-PAY_BILPAY AS PRE_AMT,BIL_IBS_RECPM.CASHIER_CODE,MR_NO"
    				+ " FROM BIL_IBS_RECPM"
    				+ " WHERE CHARGE_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS') "
    				+ " AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS') ) Y,SYS_PATINFO A"
    				+ " WHERE Y.CASHIER_CODE = N.ADM_CLERK(+) and Y.MR_NO = A.MR_NO");
    	id = this.getValueString("USER_ID");		
 		if (id.length() > 0) {//�շ�Ա��ѡ��
    		sql2.append(" AND N.ADM_CLERK = '" + id +"' ");
    	}		  			
 		sql2.append(" ORDER BY N.ADM_CLERK");
 		TParm bilPayParm1 = new TParm(TJDODBTool.getInstance().select(sql2.toString()));
		if (bilPayParm1.getErrCode() < 0) {
			System.out.println("error2");
			return;
		}
				
		double payCash = 0.00;
		double payUCash = 0.00;
		double payCard = 0.00;
		double payUCard = 0.00;
		double payRemark = 0.00;
		double payURemark = 0.00;
		int payCashCount = 0;
		int payUCashCount = 0;
		int payCardCount = 0;
		int payUCardCount = 0;
		int payRemarkCount = 0;
		int payURemarkCount = 0;
		int count = 0;
		int h1 = 0;//���ľ����ж�������
		int h2 = 0;//����ϣ��������
		int h3 = 0;//�ɸ�̩�Ļ�����
		int h4 = 0;//����ƻ�������
		
		for(int j = 0; j < nameParm.getCount();j++){
			for(int i = 0;i < bilPayParm.getCount();i++){
				if(bilPayParm.getValue("ADM_CLERK",i).equals(nameParm.getValue("ADM_CLERK", j))){//ÿλ�շ�Ա
					if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")
							|| bilPayParm.getValue("TRANSACT_TYPE", i).equals("02")) {// Ԥ����
						if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")) {//�տ�
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// �ֽ����
								payCash += bilPayParm.getDouble("PRE_AMT", i);
								payCashCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// ֧Ʊ
								payRemark += bilPayParm.getDouble("PRE_AMT", i);
								payRemarkCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// ˢ��
								payCard += bilPayParm.getDouble("PRE_AMT", i);
								payCardCount += bilPayParm.getInt("NUM", i);
							}
						} else {//�˿�
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// �ֽ����
								payUCash += bilPayParm.getDouble("PRE_AMT", i);
								payUCashCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// ֧Ʊ
								payURemark += bilPayParm.getDouble("PRE_AMT", i);
								payURemarkCount += bilPayParm.getInt("NUM", i);
							}
							if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// ˢ��
								payUCard += bilPayParm.getDouble("PRE_AMT", i);
								payUCardCount += bilPayParm.getInt("NUM", i);
							}

						}//�˿�end
					}//Ԥ����end
				}//ÿλ�շ�Աend
			}
			
			count = payCashCount+payRemarkCount+payCardCount+payUCashCount+payURemarkCount+payUCardCount;//Ԥ�������

			returnParm.addData("USER_ID", nameParm.getValue("ADM_CLERK", j));			
			returnParm.addData("USER_NAME", nameParm.getValue("PAT_NAME", j));
			returnParm.addData("PAT_COUNT", nameParm.getValue("PAT_COUNT", j));
			returnParm.addData("COUNT1", count);
			returnParm.addData("CASH_AMT1", df1.format(payCash));
			returnParm.addData("CARD_AMT1", df1.format(payCard));
			returnParm.addData("REMARK_AMT1", df1.format(payRemark));
						
			payCash = 0.00;
			payUCash = 0.00;
			payCard = 0.00;
			payUCard = 0.00;
			payRemark = 0.00;
			payURemark = 0.00;
			payCashCount = 0;
			payUCashCount = 0;
			payCardCount = 0;
			payUCardCount = 0;
			payRemarkCount = 0;
			payURemarkCount = 0;
			count = 0;

			for(int i = 0;i < bilPayParm1.getCount();i++){
				if (bilPayParm1.getDouble("PRE_AMT", i) == 0) {// ���ݳ�����ȥ�ۼ�
					continue;
				}
				if(bilPayParm1.getValue("ADM_CLERK", i).equals(nameParm.getValue("ADM_CLERK", j))){//ÿλ�շ�Ա						
					if (bilPayParm1.getValue("TRANSACT_TYPE", i).equals("03")) {// ����
						if (bilPayParm1.getDouble("PAY_CASH", i)!=0) {// �ֽ�
							payCash += bilPayParm1.getDouble("PRE_AMT", i);
							payCashCount++;
						}
						if (bilPayParm1.getDouble("PAY_CHECK", i)!=0) {// ֧Ʊ
							payRemark += bilPayParm1.getDouble("PRE_AMT", i);
							payRemarkCount++;
						}
						if (bilPayParm1.getDouble("PAY_BANK_CARD", i)!=0) {// ˢ��
							payCard += bilPayParm1.getDouble("PRE_AMT", i);
							payCardCount++;
						}
					}//����end
					else {// �˿�
						if (bilPayParm1.getDouble("PAY_CASH", i)!=0) {// �ֽ�
							payUCash += bilPayParm1.getDouble("PRE_AMT", i);
							payUCashCount++;
						}
						if (bilPayParm1.getDouble("PAY_CHECK", i)!=0) {// ֧Ʊ
							payURemark += bilPayParm1.getDouble("PRE_AMT", i);
							payURemarkCount++;
						}
						if (bilPayParm1.getDouble("PAY_BANK_CARD", i)!=0) {// ˢ��
							payUCard += bilPayParm1.getDouble("PRE_AMT", i);
							payUCardCount++;
						}
					}//�˿�end
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("35")){//���ľ����ж�
						h1++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("30")){//����ϣ��
						h2++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("67")){//�ɸ�̩��
						h3++;
					}
					if(bilPayParm1.getValue("CTZ1_CODE", i).equals("44")){//����ƻ�
						h4++;
					}
				}//ÿλ�շ�Աend
			}
			count = payCashCount+payRemarkCount+payCardCount+payUCashCount+payURemarkCount+payUCardCount;
			returnParm.addData("COUNT2", count);
			returnParm.addData("CASH_AMT2", df1.format(payCash));
			returnParm.addData("CARD_AMT2", df1.format(payCard));
			returnParm.addData("REMARK_AMT2", df1.format(payRemark));
			returnParm.addData("CASH_AMT3", df1.format(payUCash));
			returnParm.addData("CARD_AMT3", df1.format(payUCard));
			returnParm.addData("REMARK_AMT3", df1.format(payURemark));
			returnParm.addData("AXJZXD", h1);
			returnParm.addData("AXXW", h2);
			returnParm.addData("HFTX", h3);
			returnParm.addData("MTJH", h4);
			
			payCash = 0.00;
			payUCash = 0.00;
			payCard = 0.00;
			payUCard = 0.00;
			payRemark = 0.00;
			payURemark = 0.00;
			payCashCount = 0;
			payUCashCount = 0;
			payCardCount = 0;
			payUCardCount = 0;
			payRemarkCount = 0;
			payURemarkCount = 0;
			count = 0;
			h1 = 0;
			h2 = 0;
			h3 = 0;
			h4 = 0;
			
		}		
		table.setParmValue(returnParm);		
    }
              
}

