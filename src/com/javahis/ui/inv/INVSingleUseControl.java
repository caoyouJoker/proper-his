package com.javahis.ui.inv;

import java.awt.Component;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jdo.inv.INVRegressGoodTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * <p>
 * Title: �������ü�¼
 * </p>
 * 
 * <p>     
 * Description: �������ü�¼
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BlueCore 2013
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author zhangh
 * @version 1.0
 */
public class INVSingleUseControl extends TControl {

	TTable table;
	private boolean isHigh = true;//��ֵ��ֵ���ֱ��
	private String upSupCode = "";

	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		this.table = (TTable) this.getComponent("TABLE");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("USE_DATE", new Date());

		callFunction("UI|INV_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "getBarCode");
		// ע�ἤ��INDSupOrder�������¼�
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                 this, "onCreateEditComoponentUD");
        
        createNewRow();
	}
	
	
    /**
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
   
        TParm parm = new TParm();
        parm.setData("ORG_CODE", Operator.getDept());
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }
    
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table.acceptText();
        String inv_desc = parm.getValue("INV_CHN_DESC");
        String inv_code = parm.getValue("INV_CODE");
        String d = parm.getValue("DESCRIPTION");
        String ui = parm.getValue("STOCK_UNIT");
        if(ui!=null&&ui.length()>=1){
        String sqlString="select UNIT_CHN_DESC from sys_unit where unit_code='"+ui+"'";
    	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqlString));
    	ui=parm1.getData("UNIT_CHN_DESC", 0).toString();
        }
        int batch_seq = 100000000;
        int invseq_no = parm.getInt("INVSEQ_NO");
        String seqmain_flg = parm.getValue("SEQMAN_FLG");
        if (!StringUtil.isNullString(inv_desc)) {
            // ������Ź���������Ƿ��ظ�
//            if ("N".equals(seqmain_flg)) {
//                for (int i = 0; i < table.getRowCount(); i++) {
//                    if ("".equals(table.getParmValue().getValue("INV_CODE", i))) {
//                        continue;
//                    }
//                    else if (table.getSelectedRow() == i) {
//                        continue;
//                    }
//                    else {
//                        if (inv_code.equals(table.getParmValue().getValue(
//                            "INV_CODE", i)) &&
//                            batch_seq ==
//                            table.getParmValue().getInt(" ", i)) {
//                            this.messageBox("���������ظ�");
//                            return;
//                        }
//                    }
//                }
//            }
            table.setItem(table.getSelectedRow(), "INV_CODE", inv_code);
            table.setItem(table.getSelectedRow(), "INV_CHN_DESC", inv_desc);
            table.setItem(table.getSelectedRow(), "DESCRIPTION", d);
            table.setItem(table.getSelectedRow(), "QTY",1);
            table.setItem(table.getSelectedRow(), "UNIT",ui);
            if (table.getRowCount() == table.getSelectedRow() + 1) { 
                createNewRow();
            }
        }
    }
    
    /**
     * ����ϸ��������
     * @return int
     */
    private int createNewRow() {
        int row = table.addRow();
        return row;
    }
    
    /**
     * �򿪽���
     */
	public void onExport1(){
		
		  
          TParm parm = new TParm();
          Object result = openDialog("%ROOT%\\config\\inv\\INVSingleUseSelect.x", parm);
          if (result != null) {
        	  table.removeRow(table.getRowCount()-1);
              TParm addParm = (TParm) result;
              if (addParm == null) {
                  return;}
    		for (int i = 0; i < addParm.getCount("INV_CODE"); i++) {
    			 TParm mainParm = new TParm();
    			mainParm.setData("INV_CODE", addParm.getValue("INV_CODE", i));
    			mainParm.setData("UNIT", addParm.getValue("UNIT_DESC", i));
    			mainParm.setData("INV_CHN_DESC", addParm.getValue("INV_CHN_DESC", i));
    			mainParm.setData("DESCRIPTION", addParm.getValue("DESCRIPTION", i));
//    			mainParm.setData("BATCH_NO", parm1.getValue("BATCH_NO", i));
    			mainParm.setData("QTY", 1);
    			mainParm.setData("UP_SUP_CODE", addParm.getValue("UP_SUP_CODE", i));
    			mainParm.setData("MAN_CODE", addParm.getValue("MAN_CODE", i));
    			mainParm.setData("ORG_CODE", Operator.getDept());
    			table.addRow(mainParm);
    		}
    		createNewRow();
          }
    } 


	/**
	 * �õ������
	 * */
	public void getBarCode() {
		callFunction("UI|save|setEnabled", true);
		if (getValueString("INV_CODE") != null
				&& getValueString("INV_CODE").trim().length() > 0) {
			getTextField("INV_CODE").grabFocus();
		} else {
			return;
		}
		String barCode = this.getValueString("INV_CODE").toUpperCase();
		String sql1 = "";
		TParm parm1 = new TParm();
		int stockQty = 0;
			
			isHigh = false;
			sql1 = " SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,A.UP_SUP_CODE,A.MAN_CODE,'' AS QTY,d.UNIT_CHN_DESC UNIT  " +
				   " FROM INV_BASE A " +
				   " left join SYS_UNIT d on A.STOCK_UNIT= d.UNIT_CODE" +
				   " LEFT JOIN SYS_MANUFACTURER B ON B.MAN_CODE = A.MAN_CODE " +
			 	   " WHERE  A.INV_CODE='" + barCode +"'";
		//	System.out.println(sql1);
			parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		if (parm1.getCount("INV_CODE") <= 0) {
			messageBox("����ı�ţ�����ɨ��");
			return;
		}
		TParm mainParm = new TParm();
		for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
			mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
			mainParm.setData("UNIT", parm1.getValue("UNIT", i));
			mainParm.setData("INV_CHN_DESC", parm1.getValue("INV_CHN_DESC", i));
			mainParm.setData("DESCRIPTION", parm1.getValue("DESCRIPTION", i));
//			mainParm.setData("BATCH_NO", parm1.getValue("BATCH_NO", i));
				mainParm.setData("QTY", stockQty + 1);
			mainParm.setData("UP_SUP_CODE", parm1.getValue("UP_SUP_CODE", i));
			mainParm.setData("MAN_CODE", parm1.getValue("MAN_CODE", i));
			mainParm.setData("ORG_CODE", Operator.getDept());
		}
//		table.removeRowAll();
		table.removeRow(table.getRowCount()-1);
		table.addRow(mainParm);
		
		
		//setQtyIsLock(isHigh);
		this.setValue("INV_CODE", "");
		createNewRow();
	}

	//����Ǹ�ֵ����ѱ����QTY����ס��������ǣ���ſ�
	private void setQtyIsLock(boolean isHigh) {
		if(isHigh == true){
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, true);
			}
		}else{
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, false);
			}
		}
	}
	Map<String,String> map=new HashMap<String,String>();

	/**
	 * ����
	 * */
	public void onSave() {
		 map=new HashMap<String,String>();
		table.acceptText();
//		TParm parm = new TParm();// �õ�¼������Ϊ����������
		TParm invRegressParm = new TParm();// �����˻�����
		TParm invStockParm = new TParm();// �ۿ����
		
		TParm tableParm = table.getShowParmValue();
		int count = tableParm.getCount("INV_CODE");
		if(count <= 0){
			this.messageBox("�ޱ������ݣ�");
			return;
		}
		String returnNo = SystemTool.getInstance().getNo("ALL",
				"INV", "INV_REGRESSGOOD", "No");
//		HashMap<String, Double> invMap = new HashMap<String, Double>();
		
		for (int i = 0; i < count; i++) {
			TParm parmV = tableParm.getRow(i);
//			TParm returnTparm = INVRegressGoodTool.getInstance().selInvReturnMaxSeq();
			//����ȡ��ԭ��õ��˻����
			if ("".equals(parmV.getValue("INV_CODE"))||null==parmV.getValue("INV_CODE")||parmV.getValue("INV_CODE").length()<=0) {
				continue;
				
			}
			invRegressParm.addData("USE_NO", returnNo);
			invRegressParm.addData("SEQ", i + 1);
			invRegressParm.addData("INV_CODE", parmV.getValue("INV_CODE"));
			invRegressParm.addData("RFID","");
			invRegressParm.addData("USE_USER", Operator.getID());
			invRegressParm.addData("USE_DEPT", Operator.getDept());
			invRegressParm.addData("SUP_CODE", "19");
			invRegressParm.addData("REASON", "");
			invRegressParm.addData("OPT_USER", Operator.getID());
			invRegressParm.addData("OPT_TERM", Operator.getIP());
			invRegressParm.addData("QTY", parmV.getInt("QTY"));
		
				// �ۿ�������
				// INV_STOCKM
				if (map.containsKey(parmV.getValue("INV_CODE"))) {
					String c=map.get(parmV.getValue("INV_CODE"));
					int s=Integer.parseInt(c)+parmV.getInt("QTY");
					map.put(parmV.getValue("INV_CODE"), ""+s);
				}else {
					map.put(parmV.getValue("INV_CODE"), ""+parmV.getInt("QTY"));
				}
			}
		for (String s : map.keySet()) {
			invStockParm.addData("INV_CODE", s);
			invStockParm.addData("QTY", map.get(s));
			invStockParm.addData("ORG_CODE", Operator.getDept());
			invStockParm.addData("RFID", "");
			invStockParm.addData("OPT_USER", Operator.getID());
			invStockParm.addData("OPT_TERM", Operator.getIP());
			invStockParm.addData("FLG", "LOW");
		}
//		invStockParm.setData("MERGE", invMap);
		TParm Main = new TParm();
		Main.setData("INVRegressGood", invRegressParm.getData());// �˻���ϸ�����
		Main.setData("INVStock", invStockParm.getData());// �ۿ����
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVRegressGoodAction", "onSaveForSingleUse", Main);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("����ɹ���");
		this.setValue("RETURN_NO", returnNo);
		callFunction("UI|save|setEnabled", false);
		onClear();
	}
	
	/**
	 * ���
	 * */
	public void onClear() {
		table.clearSelection();
		table.removeRowAll();
		callFunction("UI|save|setEnabled", true);
		//((TTextFormat)this.getComponent("INV_CODE")).setEnabled(true);
		this.clearValue("INV_CODE;INV_CHN_DESC");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("USE_DATE", new Date());
		
		//this.addDefaultRowForTable("TABLE");
		this.setValue("INV_CODE", "");
		this.setValue("INV_CHN_DESC", "");
		//table.removeRowAll();
		//this.addDefaultRowForTable("TABLE");
		createNewRow();
	}

	/**
	 * ����Ĭ����
	 * 
	 * @param tableName
	 *            String
	 */
	private void addDefaultRowForTable(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (table.equals(tableName)) {
			if (tableParm.getCount("RFID") > 0
					&& "".equals(tableParm.getValue("RFID",
							(tableParm.getCount("RFID") - 1)).trim())) {
				return;
			}
			// Ĭ�ϼ���һ��������
			tableParm.addData("INV_CODE", "");
			tableParm.addData("INV_CHN_DESC", "");
			tableParm.addData("RFID", "");
			tableParm.addData("DESCRIPTION", "");
			tableParm.addData("QTY", "");
			tableParm.addData("SUP_CODE", "");
			tableParm.addData("ORG_CODE", "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * �õ�TTextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	
	/*
     * ����excel����
     */
    public void onExcel(){
    	if(table.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "�����˻�");
    }
    
    /*
     * ɾ������
     */
    public void onDelete(){
    	TParm parm = table.getShowParmValue();
		if (parm.getCount("INV_CODE") <= 0) {
			this.messageBox("û��Ҫɾ�������ݣ���ѡ��Ҫɾ�����У�");
			return;
		}
		table.removeRow(table.getSelectedRow());
    }
    
    /*
     * ��ӡ����
     */
    public void onPrint(){
		if (table.getRowCount() <= 0) {
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = table.getShowParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("RFID", tableData.getData("RFID", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("UP_SUP_CODE", tableData.getData("UP_SUP_CODE", i));
			printData.addData("MAN_CODE", tableData.getData("MAN_CODE", i));
			printData.addData("ORG_CODE", tableData.getData("ORG_CODE", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "RFID");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "UP_SUP_CODE");
		printData.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		printData.addData("SYSTEM", "COLUMNS", "ORG_CODE");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "������ϸ");
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGood.jhw",
				printParm);
    }
    
    /**
     * ��ѯ���˻��Żس��¼�
     */
    public void onQuery(){
    	String returnNo = this.getValueString("RETURN_NO");
    	String supCode = this.getValueString("UP_SUP_CODE");
    	TParm parm = new TParm();
    	if(returnNo != null && !"".equals(returnNo.trim())){
    		parm.setData("RETURN_NO", returnNo);	
    	}
    	if(supCode != null && !"".equals(supCode.trim())){
    		parm.setData("SUP_CODE", supCode);
    	}
    	TParm result = INVRegressGoodTool.getInstance().onQuery(parm);
    	if(result.getCount("RETURN_NO") <= 0){
    		this.messageBox("��ѯʧ�ܣ�");
    		return;
    	}
    	table.setParmValue(result);
    	callFunction("UI|save|setEnabled", false);
    	((TTextFormat)this.getComponent("INV_CODE")).setEnabled(false);
    }
}
