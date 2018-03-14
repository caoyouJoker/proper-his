package com.javahis.ui.bil;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: Qҽ���񱨱�</p>
 *
 * <p>Description: Qҽ���񱨱�</p>
 *
 *
 * @author yyn 2017-11
 * @version 1.0
 */
public class BILQeAppStatisticsControl extends TControl {
	String type = "";
	/**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //table���������¼�
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        TTable table = (TTable)this.getComponent("Table");
        //table����checkBox�¼�
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
    }
    
    /**
     * ��ʼ������
     */
    public void initPage() {
    	 //��ʼ��Ժ��
        setValue("REGION_CODE", Operator.getRegion());
        setValue("TYPE","");
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("S_DATE", today);
        setValue("E_DATE", today);
        setValue("S_TIME", "00:00:00");
        setValue("E_TIME", "23:59:59");
        setValue("CASHIER_CODE", Operator.getID());
        setValue("DEPT", Operator.getDept());  	
    }
    
    /**
     * ���
     */
    public void onClear() {
    	initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	String startDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
    	String endDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
    	startDate = startDate +"000000";
    	endDate = endDate +"235959";
    	TParm result = new TParm();
    	if(this.getValue("TYPE").equals("")){
    		this.messageBox("Ʊ����𲻿�Ϊ��");
    		return;
    	}
    	if(this.getValue("TYPE").equals("R")){//Ʊ����𣺹Һ�
    		String sql = "SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD HH24:MI:SS ')  AS TIME,A.OPT_USER,C.USER_NAME,A.PRINT_NO,A.AR_AMT "
    			+" FROM BIL_REG_RECP A, BIL_INVRCP B, SYS_OPERATOR C"
    			+" WHERE A.PRINT_NO = B.INV_NO AND A.CASH_CODE <> B.CASHIER_CODE AND"
    			+" A.CASH_CODE = 'QeApp' AND B.CASHIER_CODE = C.USER_ID"
    			+" AND BILL_DATE BETWEEN TO_DATE('"+startDate +"','YYYYMMDDHH24MISS')"
    			+" AND TO_DATE ('"+endDate +"','YYYYMMDDHH24MISS')";
    		if(! this.getValue("CASHIER_CODE").equals("")){
    			sql += " AND A.OPT_USER = '"+getValue("CASHIER_CODE")+"'";
    		}
    			sql+= " ORDER BY TIME,A.OPT_USER";
    		//System.out.println("r ="+sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getCount() == 0)
                this.messageBox("û��Ҫ��ѯ������");
            if(result.getErrCode() < 0){
            	System.out.println("error1");
    			return;
            }
            this.callFunction("UI|Table|setParmValue", result);
    		type = "�Һ�";
    	}
    	if(this.getValue("TYPE").equals("O")){//Ʊ������շ�
    		String sql = "SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD HH24:MI:SS')  AS TIME,A.OPT_USER,C.USER_NAME,A.PRINT_NO,A.AR_AMT"
    				+" FROM BIL_OPB_RECP A, BIL_INVRCP B, SYS_OPERATOR C"
    				+" WHERE A.PRINT_NO = B.INV_NO AND A.CASHIER_CODE <> B.CASHIER_CODE AND"
    				+" A.CASHIER_CODE = 'QeApp' AND B.CASHIER_CODE = C.USER_ID"
    				+" AND BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"
        			+" AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS')";
        	if(! this.getValue("CASHIER_CODE").equals("")){
            	sql += " AND A.OPT_USER = '"+getValue("CASHIER_CODE")+"'";
            }
        			sql += " ORDER BY TIME,A.OPT_USER";
    		//System.out.println("o ="+sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getCount() == 0)
                this.messageBox("û��Ҫ��ѯ������");
            if(result.getErrCode() < 0){
            	System.out.println("error2");
    			return;
            }
            this.callFunction("UI|Table|setParmValue", result);
    		type = "�շ�";
    	}
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
    	TTable table = (TTable)this.getComponent("Table");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "Qҽ"+type+"ռƱ����ͳ�Ʊ�");
    }

}
