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
 * Title: �豸ʹ����ͳ�Ʊ�
 * Description:�豸ʹ����ͳ�Ʊ�
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STAEquipmentStatisticsControl extends TControl {
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
    	sql =" SELECT A.ORDER_CODE, A.ORDER_CHN_DESC,SUM(A.DOSAGE_QTY ) AS DOSAGE_QTY"+
    		" FROM IBS_ORDD A,SYS_FEE B"+
    		" WHERE  A.ORDER_CODE = B.ORDER_CODE"+
    		" AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
    		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    		" AND A.ORDER_CODE = 'L0000015'"+
    		" GROUP BY A.ORDER_CODE,A.ORDER_CHN_DESC ";		
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
    	String title ="�豸ʹ����ͳ�Ʊ�";	
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
