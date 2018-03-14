package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bms.BMSDeptBillTool;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ���ҷ��ò�ѯ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class BMSDeptBillControl
    extends TControl {

    private TTable table;
    private String caseNo ;
    
    //add by yangjj 20160708
    private TTextField QUERY;
    private TTextField QUERY_NAME;
    
    //add by wukai 20160811
    private String dept;  //����
    private String station; //����
    private String billDate; //ͳ��ʱ��
    private String billUser ; //ͳ����Ա
    
    public BMSDeptBillControl() {
    }
  
	/**
     * ��ʼ������
     */
    public void onInit() {
        table = getTable("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("DEPT_CODE",Operator.getDept()) ;
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).setDeptCode(this.getValueString("DEPT_CODE"));
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).onQuery();
        
        
        //add by yangjj 20160708
        TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "");
        QUERY = (TTextField) this.getComponent("QUERY");
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
		"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        QUERY_NAME = (TTextField) this.getComponent("QUERY_NAME");
    }

    //add by yangjj 20160708
    public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			QUERY.setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			QUERY_NAME.setValue(order_desc);
	}
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	double totle = 0.00 ;
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("EXE_DEPT_CODE", this.getValueString("DEPT_CODE"));
            this.dept = this.getText("DEPT_CODE");
        } else {
        	this.dept = "";
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("EXE_STATION_CODE", this.getValueString("STATION_CODE"));
            this.station = this.getText("STATION_CODE");
        } else {
        	this.station = "";
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("CASE_NO", caseNo);
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        }
        if (!"".equals(this.getValueString("OPT_USER"))) {
            parm.setData("OPT_USER", this.getValueString("OPT_USER"));
            this.billUser = this.getText("OPT_USER");
        } else {
        	this.billUser = "";
        }
        parm.setData("START_DATE", this.getValue("START_DATE").toString().substring(0, 10).replace("-", "")+"000000");
        parm.setData("END_DATE", this.getValue("END_DATE").toString().substring(0, 10).replace("-", "")+"235959");
        this.billDate =  this.getValue("START_DATE").toString().substring(0, 10) + " �� " + this.getValue("END_DATE").toString().substring(0, 10);
        //add by yangjj 20160708
        parm.setData("ORDER_CODE", this.getValueString("QUERY"));
        
        TParm result = BMSDeptBillTool.getInstance().onQuery(parm);
        if (result== null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            this.onClear() ;
            return;
        }
        for(int i=0;i<result.getCount();i++){
        	totle +=result.getDouble("AMT", i) ;
        }
        this.setValue("TOTLE", totle) ;
        table.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "DEPT_CODE;STATION_CODE;MR_NO;IPD_NO;OPT_USER;"
            + "START_DATE;END_DATE;TOTLE;QUERY;QUERY_NAME";
        this.clearValue(clearStr);
        table.removeRowAll();
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("DEPT_CODE", Operator.getDept()) ;
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).setDeptCode(this.getValueString("DEPT_CODE"));
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).onQuery();
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNoAction() {
//        String mr_no = this.getValueString("MR_NO");
//        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength()));//========= chenxi
    	
    	// modify by huangtt 20160928 EMPI���߲�����ʾ start
        String mr_no = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no);  //  chenxi modify  20121023
        Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);
		}
		// modify by huangtt 20160928 EMPI���߲�����ʾ end
        
        caseNo =this.onQueryByMrNo(this.getValueString("MR_NO")) ;
        this.onQuery();
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

    /**
     * ��ѯ���˻�ȡcase_no
     */
    public String onQueryByMrNo(String mrNo){
    	String sql ="SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO = '"+mrNo+"'"   ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		caseNo = parm.getValue("CASE_NO", 0).toString() ;
    	return caseNo ; 	
    }
    
    /**
     * ��ӡ
     */
    public void onPrint() {
    	if(table.getRowCount() <= 0) {
    		this.messageBox("�޿ɴ�ӡ���ݣ�");
    		return;
    	}
    	
    	//��ͷ����
    	TParm data = new TParm();
    	data.setData("TITLE", "TEXT", "���ҷ���ͳ�Ʊ�");
    	data.setData("DEPT_CODE","TEXT", this.dept);
    	data.setData("STATION_CODE","TEXT", this.station);
    	data.setData("BILL_USER", "TEXT", this.billUser);
    	DecimalFormat    df   = new DecimalFormat("######0.00");   
    	data.setData("TOTAL","TEXT", df.format(Double.parseDouble(this.getValueString("TOTLE"))));
    	data.setData("BILL_DATE", "TEXT", this.billDate);
    	
    	//�������DEPT_CHN_DESC;STATION_DESC;IPD_NO;MR_NO;PAT_NAME;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;OWN_PRICE;DOSAGE_QTY;AMT
    	TParm parm = new TParm();
    	TParm tableParm = table.getShowParmValue();
    	for(int i = 0; i < table.getRowCount(); i++) {
    		parm.addData("DEPT_CHN_DESC", tableParm.getData("DEPT_CHN_DESC", i));
    		parm.addData("STATION_DESC", tableParm.getData("STATION_DESC", i));
    		parm.addData("IPD_NO", tableParm.getData("IPD_NO", i));
    		parm.addData("MR_NO", tableParm.getData("MR_NO", i));
    		parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
    		parm.addData("ORDER_DESC", tableParm.getData("ORDER_DESC", i));
    		parm.addData("SPECIFICATION", tableParm.getData("SPECIFICATION", i));
    		parm.addData("UNIT_CHN_DESC", tableParm.getData("UNIT_CHN_DESC", i));
    		parm.addData("OWN_PRICE", tableParm.getData("OWN_PRICE", i));
    		parm.addData("DOSAGE_QTY", tableParm.getData("DOSAGE_QTY", i));
    		parm.addData("AMT", tableParm.getData("AMT", i));
    		
    	}
    	parm.setCount(parm.getCount("DEPT_CHN_DESC"));
    	parm.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
    	parm.addData("SYSTEM","COLUMNS","STATION_DESC");
    	parm.addData("SYSTEM","COLUMNS","IPD_NO");
    	parm.addData("SYSTEM","COLUMNS","MR_NO");
    	parm.addData("SYSTEM","COLUMNS","PAT_NAME");
    	parm.addData("SYSTEM","COLUMNS","ORDER_DESC");
    	parm.addData("SYSTEM","COLUMNS","SPECIFICATION");
    	parm.addData("SYSTEM","COLUMNS","UNIT_CHN_DESC");
    	parm.addData("SYSTEM","COLUMNS","OWN_PRICE");
    	parm.addData("SYSTEM","COLUMNS","DOSAGE_QTY");
    	parm.addData("SYSTEM","COLUMNS","AMT");
    	data.setData("TABLE", parm.getData());
    	
    	//��β����
    	data.setData("OPT_USER", "TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		//System.out.println( "print data :::::::   " + data);
		this.openPrintDialog("%ROOT%\\config\\prt\\bms\\BMSDeptBill.jhw", data);
    	
    }
    
    /**
     * ����Excel
     */
    public void onExport() {
    	if(table.getRowCount() <= 0) {
    		this.messageBox("�޿ɵ���Excel���ݣ�");
    		return;
    	}
    	ExportExcelUtil.getInstance().exportExcel(table, "���ҷ���ͳ�Ʊ�");
    	
    }
    
}
