package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: ҩռ��ͳ��</p>
*
* <p>Description: ҩռ��ͳ��</p>
*
*
* @author yyn 2017-11
* @version 1.0
*/
public class STAMedicineproControl extends TControl {
	TTable table;

	/**
     * ��ʼ��
     */
	public void onInit(){
		super.onInit();
        table = (TTable)this.getComponent("TABLE");
        initPage();//��ʼ��ҳ����Ϣ
	}
	
	/**
     * ��ʼ��ҳ����Ϣ
     */
    public void initPage() {
    	Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);//ǰһ��ʱ��
    	setValue("S_DATE", yesterday);
    	setValue("E_DATE", yesterday);
        setValue("S_TIME", "00:00:00");
        setValue("E_TIME", "23:59:59");   	
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	String startDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
    	String endDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
    	startDate = startDate +"000000";
    	endDate = endDate +"235959";
    	double med_cost = 0.00;//ҩƷ����
    	double treat_cost = 0.00;//ҽ�Ʒ���
    	double med_pro = 0.00;//ҩռ��
    	TParm result = new TParm();
    	String sql = "SELECT TYPE,MED_COST,TREAT_COST,ROUND (MED_COST / TREAT_COST, 4) * 100 MED_PRO"
    			+ " FROM (SELECT '�ż���' AS TYPE,"
    			+ " (SELECT SUM (AR_AMT)"
    			+ " FROM OPD_ORDER"
    			+ " WHERE BILL_FLG = 'Y'"
    			+ " AND BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND CAT1_TYPE = 'PHA') MED_COST,"
    			+ " (SELECT SUM (AR_AMT)"
    			+ " FROM OPD_ORDER"
    			+ " WHERE BILL_FLG = 'Y'"
    			+ " AND BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')) TREAT_COST"
    			+ " FROM DUAL"
    			+ " UNION ALL"
    			+ " SELECT '���' AS TYPE,"
    			+ " (SELECT SUM (A.AR_AMT)"
    			+ " FROM HRM_ORDER A,BIL_OPB_RECP B"
    			+ " WHERE A.RECEIPT_NO IS NOT NULL "
    			+ " AND A.RECEIPT_NO = B.RECEIPT_NO"
    			+ " AND B.BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND B.BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND CAT1_TYPE = 'PHA') MED_COST,"
    			+ "(SELECT SUM (A.AR_AMT)"
    			+ " FROM HRM_ORDER A,BIL_OPB_RECP B"
    			+ " WHERE A.RECEIPT_NO IS NOT NULL"
    			+ " AND A.RECEIPT_NO = B.RECEIPT_NO"
    			+ " AND B.BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND B.BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')) TREAT_COST"
    			+ " FROM DUAL"
    			+ " UNION ALL"
    			+ " SELECT 'סԺ' AS TYPE,"
    			+ " (SELECT SUM (TOT_AMT)"
    			+ " FROM IBS_ORDD"
    			+ " WHERE BILL_FLG = 'Y'"
    			+ " AND BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND CAT1_TYPE = 'PHA') MED_COST,"
    			+ " (SELECT SUM (TOT_AMT)"
    			+ " FROM IBS_ORDD"
    			+ " WHERE BILL_FLG = 'Y'"
    			+ " AND BILL_DATE >= TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')"
    			+ " AND BILL_DATE <= TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')) TREAT_COST"
    			+ " FROM DUAL) T";
    	//System.out.println("sql = "+sql);
    	result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount() == 0)
            this.messageBox("û��Ҫ��ѯ������");
        if(result.getErrCode() < 0){
        	System.out.println("error1");
			return;
        }
        if(result.getCount() > 0){
        	for(int i = 0;i < result.getCount();i++){
        		med_cost += result.getDouble("MED_COST", i);//��ҩƷ����
        		treat_cost += result.getDouble("TREAT_COST", i);//��ҽ�Ʒ���
        	}
        	if(treat_cost != 0){
        		med_pro = (med_cost/treat_cost)*100;//��ҩռ��
        	}
        	else{
        		med_pro = 0;
        	}
        	result.addData("TYPE", "�ϼ�");
        	result.addData("MED_COST", med_cost);
        	result.addData("TREAT_COST", treat_cost);
        	result.addData("MED_PRO", med_pro); 
        }
        this.callFunction("UI|Table|setParmValue", result);   	
    }
    
    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("TABLE");
        table.removeRowAll();
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
    	TTable table = (TTable)this.getComponent("TABLE");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "ҩռ������ͳ�Ʊ�");
    }

}
