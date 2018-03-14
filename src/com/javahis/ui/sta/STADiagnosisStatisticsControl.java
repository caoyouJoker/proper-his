package com.javahis.ui.sta;

import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: ��Ⱦ�����ͳ�Ʊ�
 * Description:��Ⱦ�����ͳ�Ʊ�
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STADiagnosisStatisticsControl extends TControl {
	private TTable table;

    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
	 * ���ݼ��
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("��ʼ���ڲ���Ϊ��");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("�������ڲ���Ϊ��");
    		return true;
    	}
	    return false; 
	}
    /**
     * ��ѯ
     */
    public void onQuery() {
    	//���ݼ��
    	if(checkdata())
		    return;   	
    	String sql ="";  	
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//��ѯ����
    	sql =" SELECT FDD.MR_NO, FDD.CASE_NO, FDD.ICD_CODE," +
    		" FDD.ICD_DESC, FZ.DEPT_CHN_DESC, FTT.OUT_DATE" +
    		" FROM MRO_RECORD_DIAG FDD," +
    		" MRO_RECORD FTT," +
    		" JAVAHIS.SYS_DEPT FZ" +
    		" WHERE FDD.IO_TYPE = 'Q'" +
    		" AND FDD.CASE_NO = FTT.CASE_NO" +
    		" AND FTT.OUT_DEPT = FZ.DEPT_CODE" +
    		" AND FTT.OUT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
    		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
    		" ORDER BY  FDD.MR_NO, FDD.CASE_NO, FDD.IO_TYPE ";		
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//��������
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}	
    	((TTable) getComponent("TABLE")).setParmValue(result);    	
		
  }
    
    /**
     * ���
     */
    public void onExport() {
    	String title ="��Ⱦ���ͳ�Ʊ�";	
    	    if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);
    	
    }  	
    /**
     * ���
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
}
