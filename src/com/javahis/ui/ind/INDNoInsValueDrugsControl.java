package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.ind.INDNoInsValueDrugsTool;
import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��ҽ������ҩƷ�ص���Ʒ�ֱ���
 * </p>
 * 
 * <p>
 * Description:��ҽ������ҩƷ�ص���Ʒ�ֱ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2016-10-26
 * @version JavaHis 1.0
 */
public class INDNoInsValueDrugsControl extends TControl {

	private TTable table;
	private String startDate; //��ʼʱ��
	private String endDate; //����ʱ��
	private String medType; //ͳ������
	
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		TParm parm = new TParm();
		//parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		onClear();
	}

	public void onQuery() {
		table.removeRowAll();
        this.setValue("TOTAL_AMT", "0");
        String type = this.getValueString("MED_TYPE");
        if(StringUtils.isEmpty(type)) {
        	this.messageBox("��ѡ���ѯ���");
        	return;
        }
        this.medType = type;
        TParm parm = new TParm();
        
        String startDate = this.getValueString("START_DATE");
        if(!StringUtils.isEmpty(startDate)) {
        	this.startDate = startDate.substring(0, 10);
        	startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
        } else {
        	this.startDate = "";
        }
        parm.setData("START_DATE", startDate);
        
        String endDate = this.getValueString("END_DATE");
        if(!StringUtils.isEmpty(endDate)) {
        	this.endDate = endDate.substring(0, 10);
        	endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
        } else {
        	this.endDate = "";
        }
       	parm.setData("END_DATE", endDate);
        
        parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        parm.setData("DR_CODE", this.getValueString("DR_CODE"));
        parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        
       // System.out.println("query parm ::::::   " + parm);
        
        TParm res = new TParm();
        if("1".equals(type)) {  //������ҩ
        	res = INDNoInsValueDrugsTool.getNewInstance().onQueryNoInsMedA(parm);
        } else if("2".equals(type)) {
        	res = INDNoInsValueDrugsTool.getNewInstance().onQueryNoInsMedB(parm);
        } else if("3".equals(type)) {
        	res = INDNoInsValueDrugsTool.getNewInstance().onQueryNoInsMedC(parm);
        }
        //System.out.println("res ::::::::::::  " + res);
        if(res == null || res.getCount("PAT_NAME") <= 0 || res.getErrCode() < 0) {
        	this.messageBox("�˲�ѯ������������ҩ���ݣ�");
        	table.setParmValue(new TParm());
        	return;
        }
        double total = 0;
        for(int i = 0; i < res.getCount(); i++) {
        	String tot = String.valueOf(res.getDouble("TOT_AMT", i));
        	if(StringUtils.isEmpty(tot) || "null".equals(tot)) {
        		continue;
        	}
        	total += Double.parseDouble(tot);
        }
        this.setText("TOTAL_AMT", new DecimalFormat("#########0.00").format(total));
        table.setParmValue(res);
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿ɴ�ӡ���ݣ�");
			return;
		}
		String cc = "";
		if("1".equals(this.medType)) {
			cc = "��������ҩ��";
		} if("2".equals(this.medType)) {
			cc = "��������ҩ��";
		} if("3".equals(this.medType)) {
			cc = "��סԺ��ҩ��";
		}
		
		String staDate = "";
		if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
			staDate = "ͳ��ʱ�䣺" + startDate + " ~ " + endDate;
		} else if(StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
			staDate = "ͳ��ʱ�䣺###" + " ~ " + endDate;
		} else if(!StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
			staDate = "ͳ��ʱ�䣺" + startDate + " ~ ###" ;
		} else {
			staDate = "ͳ��ʱ�䣺";
		}
		String title = "��ҽ������ҩƷ�ص���Ʒ��ͳ��" + cc;
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", title);
		data.setData("STA_DATE", "TEXT", staDate);
		data.setData("TOT_AMT", "TEXT",  "�ܼƣ�" + this.getValueString("TOTAL_AMT") + "Ԫ");
		
		//�������
		//PAT_NAME;ORDER_CODE;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;TOT_AMT;USER_NAME;DEPT_CHN_DESC;BILL_DATE
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i < table.getRowCount(); i++) {
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("ORDER_CODE", tableParm.getData("ORDER_CODE", i));
			parm.addData("ORDER_DESC", tableParm.getData("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm.getData("SPECIFICATION", i));
			parm.addData("DOSAGE_QTY", tableParm.getData("DOSAGE_QTY", i));
			parm.addData("UNIT_CHN_DESC", tableParm.getData("UNIT_CHN_DESC", i));
			parm.addData("OWN_PRICE", tableParm.getData("OWN_PRICE", i));
			parm.addData("TOT_AMT", tableParm.getData("TOT_AMT", i));
			parm.addData("USER_NAME", tableParm.getData("USER_NAME", i));
			parm.addData("DEPT_CHN_DESC", tableParm.getData("DEPT_CHN_DESC", i));
			parm.addData("BILL_DATE", tableParm.getData("BILL_DATE", i));
		}
		parm.setCount(parm.getCount("PAT_NAME"));
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","ORDER_CODE");
		parm.addData("SYSTEM","COLUMNS","ORDER_DESC");
		parm.addData("SYSTEM","COLUMNS","SPECIFICATION");
		parm.addData("SYSTEM","COLUMNS","DOSAGE_QTY");
		parm.addData("SYSTEM","COLUMNS","UNIT_CHN_DESC");
		parm.addData("SYSTEM","COLUMNS","OWN_PRICE");
		parm.addData("SYSTEM","COLUMNS","TOT_AMT");
		parm.addData("SYSTEM","COLUMNS","USER_NAME");
		parm.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
		parm.addData("SYSTEM","COLUMNS","BILL_DATE");
		data.setData("TABLESUM", parm.getData());
		
		data.setData("OPT_USER","TEXT", "�����ˣ�" + Operator.getName());
		data.setData("OPT_DATE", "TEXT", "����ʱ�䣺" + SystemTool.getInstance().getDate().toString().substring(0, 10));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\ind\\INDNoInsValueDrugs.jhw", data);
	}

	public void onExport() {
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿ɵ������ݣ�");
			return;
		}
		String cc = "";
		if("1".equals(this.medType)) {
			cc = "(������ҩ)";
		} if("2".equals(this.medType)) {
			cc = "(������ҩ)";
		} if("3".equals(this.medType)) {
			cc = "(סԺ��ҩ)";
		}
		String name = "��ҽ������ҩƷ�ص����" + cc;
		ExportExcelUtil.getInstance().exportExcel(table, name);
	}

	/**
	 * ���
	 */
	public void onClear() {
		String clearStr = "DEPT_CODE;DR_CODE;ORDER_DESC;STOP_FLG;TO_ORG_CODE;ORDER_CODE;ORDER_DESC;MED_TYPE";
		this.clearValue(clearStr);
		this.setValue("TOTAL_AMT", "0");
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table.removeRowAll();
	}
	
	 /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }
	
	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

}
