package com.javahis.ui.ope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title:���ú�����ϸ��
 * </p>
 *
 * <p>
 * Description:���ú�����ϸ��
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
 * @author duzhw 2013.08.16
 * @version 1.0
 */
public class OPECostAccountListControl extends TControl{
	
	private TTable table;
	
	public OPECostAccountListControl(){
		super();
	}
	/**
	 * ��ʼ������
	 */
	public void onInit(){
		initPage();
	}
	private void initPage(){
		table = (TTable) this.getComponent("TABLE");
		String preday = getPreviousMonthDay(26);//����26��
		String currday = getFirstDayOfMonth(25);//����25��
		this.setValue("START_DATE", StringTool.getTimestamp(preday + "000000",
		"yyyyMMddHHmmss"));// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(currday + "235959",
		"yyyyMMddHHmmss"));// ����ʱ��
	}
	
	/**
	 * ��ѯ����
	 */
	public void onQuery(){
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss"); 	//��ʼʱ��
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss"); 	//����ʱ��
		String deptCode = getValueString("DEPT_CODE");						//����
		if(startDate.length() == 0){
			messageBox("��ʼʱ�䲻��ȷ!");
			return;
		}
		if(endDate.length() == 0){
			messageBox("����ʱ�䲻��ȷ!");
			return;
		}
	
		String pattern ="yyyy-MM-dd hh:mm:ss";
		try {
			SimpleDateFormat sf = new SimpleDateFormat(pattern);
			 Date d1 = sf.parse(startDate);
			 Date d2 = sf.parse(endDate);
			 if(d1.getTime() > d2.getTime()){
				 messageBox("��ʼʱ�䲻�����ڽ���ʱ��!");
					return;
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT T.SEQ,C.DEPT_CHN_DESC AS DEPT_CODE_DESC,T.DEPT_CODE,T.BILL_DATE AS INDATE,D.PAT_NAME AS PNAME,")
			.append("T.MR_NO AS CASE_NO,T.OWN_PRICE,T.ORDER_CODE,T.ORDER_DESC,B.HEXP_CODE,")
			.append("T.AR_AMT ")
			.append(" FROM SPC_INV_RECORD T,IBS_ORDD B, SYS_DEPT C, SYS_PATINFO D")
			.append(" WHERE T.CASE_NO = B.CASE_NO AND T.CASE_NO_SEQ = B.CASE_NO_SEQ AND T.SEQ_NO = B.SEQ_NO")
			.append(" AND T.DEPT_CODE = C.DEPT_CODE AND T.MR_NO= D.MR_NO");
		
		if(deptCode.toString().length() > 0){//�������������Ҳ�Ϊ��
			sql.append(" AND T.DEPT_CODE = '" + deptCode + "'");
		}
		//��ʼ������ʱ�����
		sql.append(" AND T.BILL_DATE BETWEEN TO_DATE ('").append(startDate).append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(endDate).append("', 'YYYYMMDDHH24MISS')");
		sql.append(" ORDER BY T.DEPT_CODE,T.MR_NO,T.ORDER_CODE ");
		//��ӡsql
		//System.out.println("sql��䣺������"+sql.toString());
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString()));
		
		String hexpCode = "";
		String deptCode1 = "";
		String deptCode2 = "";
		String caseNo1 = "";
		String caseNo2 = "";
		String orderCode1 = "";
		String orderCode2 = "";
		double arAmt = 0.00;				//����
		double material_price = 0.00;		//���Ϸ�
		double operstion_price = 0.00;		//������
		double drug_price = 0.00;			//ҩƷ��
		double all_material_price = 0.00;	//�ܼ�-���Ϸ�
		double all_operstion_price = 0.00;	//�ܼ�-������
		double all_drug_price = 0.00;		//�ܼ�-ҩƷ��
		TParm newparm = new TParm();
		int j = 0;
		for (int i = 0; i < returnParm.getCount(); i++) {
			hexpCode = returnParm.getValue("HEXP_CODE", i);
			deptCode1 = returnParm.getValue("DEPT_CODE", i);
			caseNo1 = returnParm.getValue("CASE_NO", i);
			orderCode1 = returnParm.getValue("ORDER_CODE", i);
			arAmt = returnParm.getDouble("AR_AMT", i);
			
			deptCode2 = returnParm.getValue("DEPT_CODE", i+1);
			caseNo2 = returnParm.getValue("CASE_NO", i+1);
			orderCode2 = returnParm.getValue("ORDER_CODE", i+1);
			if(deptCode1.equals(deptCode2)){
				if(caseNo1.equals(caseNo2)){
					if(orderCode1.equals(orderCode2)){//��������Ƿ�һ��
						if(hexpCode.substring(0, 2).equals("2E")){//ҩƷ��
							drug_price += arAmt;
						}else if(hexpCode.equals("250")){//������
							operstion_price += arAmt;
						}else if(hexpCode.substring(0, 2).equals("2C")){//���Ϸ�
							material_price += arAmt;
						}
					}else {
						if(hexpCode.substring(0, 2).equals("2E")){//ҩƷ��
							drug_price += arAmt;
						}else if(hexpCode.equals("250")){//������
							operstion_price += arAmt;
						}else if(hexpCode.substring(0, 2).equals("2C")){//���Ϸ�
							material_price += arAmt;
						}
						newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	//
						newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
						newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					//
						newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					//
						newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
						newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
						newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
						newparm.setData("MATERIAL_PRICE", j, material_price);					//���Ϸ�
						newparm.setData("OPERATION_PRICE", j, operstion_price);					//������
						newparm.setData("DRUG_PRICE", j, drug_price);							//ҩƷ��
						newparm.setData("ALL_PRICE", j, material_price 
								+ operstion_price + drug_price);								//�ϼ�=���Ϸ�+������+ҩƷ��
						
						material_price = 0.00;	//���Ϸ���0
						operstion_price = 0.00;	//��������0
						drug_price = 0.00;		//ҩƷ����0
						
						j++;
					}
				}else {
					newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	
					newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
					newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					
					newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					
					newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
					newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
					newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
					newparm.setData("MATERIAL_PRICE", j, material_price);					//���Ϸ�
					newparm.setData("OPERATION_PRICE", j, operstion_price);					//������
					newparm.setData("DRUG_PRICE", j, drug_price);							//ҩƷ��
					newparm.setData("ALL_PRICE", j, material_price 
							+ operstion_price + drug_price);								//�ϼ�=���Ϸ�+������+ҩƷ��
					
					material_price = 0.00;//���Ϸ���0
					operstion_price = 0.00;//��������0
					drug_price = 0.00;//ҩƷ����0
					
					j++;
				}
			}else {
				if(hexpCode.substring(0, 2).equals("2E")){//ҩƷ��  ----
					drug_price += arAmt;
				}else if(hexpCode.equals("250")){//������
					operstion_price += arAmt;
				}else if(hexpCode.substring(0, 2).equals("2C")){//���Ϸ�
					material_price += arAmt;
				}
				newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	
				newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
				newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					
				newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					
				newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
				newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
				newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
				newparm.setData("MATERIAL_PRICE", j, material_price);					//���Ϸ�
				newparm.setData("OPERATION_PRICE", j, operstion_price);					//������
				newparm.setData("DRUG_PRICE", j, drug_price);							//ҩƷ��
				newparm.setData("ALL_PRICE", j, material_price 
						+ operstion_price + drug_price);								//�ϼ�=���Ϸ�+������+ҩƷ��
				
				material_price = 0.00;//���Ϸ���0
				operstion_price = 0.00;//��������0
				drug_price = 0.00;//ҩƷ����0
				
				j++;
			}
			
		}
		//�ϼ��������
		for (int k = 0; k < newparm.getCount("SEQ"); k++) {
			if(k==0){
				double material_price_k = newparm.getDouble("MATERIAL_PRICE", k);
				double operstion_price_k = newparm.getDouble("OPERATION_PRICE", k);
				double drug_price_k = newparm.getDouble("DRUG_PRICE", k);
				all_material_price += material_price_k;
				all_operstion_price += operstion_price_k;
				all_drug_price += drug_price_k;
			}else{
				double material_price_k1 = newparm.getDouble("MATERIAL_PRICE", k);
				double operstion_price_k1 = newparm.getDouble("OPERATION_PRICE", k);
				double drug_price_k1 = newparm.getDouble("DRUG_PRICE", k);
				all_material_price += material_price_k1;
				all_operstion_price += operstion_price_k1;
				all_drug_price += drug_price_k1;
			}
		}
		//���"�ϼ�"
		newparm.setData("SEQ", j, "�ϼ�:");
		newparm.setData("DEPT_CODE_DESC", j, "");									
		newparm.setData("INDATE", j, "");					
		newparm.setData("PNAME", j, "");					
		newparm.setData("CASE_NO", j, "");	
		newparm.setData("OWN_PRICE", j, "");
		newparm.setData("ORDER_DESC", j, "");
		newparm.setData("MATERIAL_PRICE", j, all_material_price);
		newparm.setData("OPERATION_PRICE", j, all_operstion_price);
		newparm.setData("DRUG_PRICE", j, all_drug_price);
		newparm.setData("ALL_PRICE", j, all_material_price+all_operstion_price+all_drug_price);
		
		if(newparm.getCount("SEQ") < 0){
			messageBox("�������ݣ�");
			TParm resultparm = new TParm();
			this.table.setParmValue(resultparm);
			return;
		}
		//������װ��TParm���ݷŵ�table�ؼ�����ʾ
		this.table.setParmValue(newparm);
		
	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy-MM-dd");	//��ʼʱ��
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy-MM-dd");	//����ʱ��
//		TTextFormat turnPoint = (TTextFormat) getComponent("DEPT_CODE");
//		String deptCode = turnPoint.getText();	//����
//		System.out.println("�������="+turnPoint.getText());
		String deptCode = getValueString("DEPT_CODE");				//����
		System.out.println("999988---0=���ң�"+deptCode);
		if(deptCode.length() > 0){
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '" + deptCode + "'";
			TParm Parm = new TParm(TJDODBTool.getInstance().select(
					 sql.toString()));
			deptCode = Parm.getValue("DEPT_CHN_DESC", 0);
		}else{
			deptCode = "";
		}
		TTable table = getTable("TABLE");
		
		if(table.getRowCount() > 0){
			TParm tableParm = table.getParmValue();
			//out("tableParm.getCount():" + tableParm.getCount());
			//��ӡ����
			TParm data = new TParm();
			//��ͷ����
			data.setData("TITLE", "TEXT", Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
					"���ú�����ϸ����");
			data.setData("DEPT_CODE","TEXT", "����:" + deptCode);
			data.setData("START_DATE", "TEXT", "��ʼʱ�䣺" + startDate);
			data.setData("END_DATE", "TEXT", "����ʱ�䣺" + endDate);
			//�������
			TParm parm = new TParm();
			
			if(tableParm.getCount("SEQ") <= 0){
				this.messageBox("�����ݣ�");
			}else{
			//���������Ԫ��
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("SEQ", tableParm.getValue("SEQ", i));								//���
				parm.addData("DEPT_CODE_DESC", tableParm.getValue("DEPT_CODE_DESC", i));		//����
				String getdate = tableParm.getValue("INDATE", i);
				String c[] = getdate.split(" ");
//				String getdate = StringTool.getString(TypeTool
//						.getTimestamp(tableParm.getValue("INDATE", i)), "yyyy-MM-dd");
//				parm.addData("INDATE", getdate);												//����
				parm.addData("INDATE", c[0]);													//����
				parm.addData("PNAME", tableParm.getValue("PNAME", i));							//��������
				parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));						//������
				parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));					//����
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));				//��������
				parm.addData("MATERIAL_PRICE", tableParm.getValue("MATERIAL_PRICE", i));		//���Ϸ�
				parm.addData("OPERATION_PRICE", tableParm.getValue("OPERATION_PRICE", i));		//������
				parm.addData("DRUG_PRICE", tableParm.getValue("DRUG_PRICE", i));				//ҩƷ��
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));					//�ܼ�
				
			}
			
			//������
			parm.setCount(parm.getCount("SEQ"));
			parm.addData("SYSTEM", "COLUMNS", "SEQ");
			parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE_DESC");
			parm.addData("SYSTEM", "COLUMNS", "INDATE");
			parm.addData("SYSTEM", "COLUMNS", "PNAME");
			parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MATERIAL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OPERATION_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "DRUG_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			
			//�����ŵ�������
			data.setData("TABLE", parm.getData());
			//��β����
			data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
			
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\OPE\\OPECostAccountList.jhw", data);
			}

		}else {
			this.messageBox("û�д�ӡ����");
            return;
		}
		
	}
	
	/**
     * ����EXCEL
     */
    public void onExport() {
    	TTable table_e = getTable("TABLE");
    	if(table_e.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(table_e, "���ú�����ϸ��ͳ��");
    	}else {
         this.messageBox("û�л������");
         return;
     }
    }
    /**
     *�����ϸ�����һ�죨���ٺţ�
     */
    public String getPreviousMonthDay(int no) {
    	String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		lastDate.add(Calendar.MONTH, -1);// ��һ���£���Ϊ���µ�1��
		// lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
    }
	// ��ȡ���µ�һ��
	public static String getFirstDayOfMonth(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		str = sdf.format(lastDate.getTime());
		return str;
	}
    /**
     * ��շ���
     */
    public void onClear() {//��ʼ��ʱ��
    	String preday = getPreviousMonthDay(26);	//����26��
		String currday = getFirstDayOfMonth(25);	//����25��
		this.setValue("START_DATE", StringTool.getTimestamp(preday + "000000",
		"yyyyMMddHHmmss"));							// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(currday + "235959",
		"yyyyMMddHHmmss"));							// ����ʱ��
		
		this.setValue("DEPT_CODE", "");				//����
    }
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
