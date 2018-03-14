package com.javahis.ui.dev;

import com.dongyang.control.*;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import java.util.Date;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;

import java.awt.Component;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;

import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import jdo.dev.DevReceiptDDataStore;
import com.dongyang.ui.TTextArea;

/**
 * <p>
 * Title: ���չ���(����)
 * </p>
 * 
 * <p>
 * Description: ���չ��������� 
 * </p>
 * insert
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fux
 * @version 1.0
 * 
 */
public class DEVFujianDetailControl extends TControl {
	/**
	 * ϸ
	 */
	private  String TABLE2 = "TABLE";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		/**
		 * ��ʼ��ҳ��
		 */
		onInitPage();
		/**
		 * ��ʼ���¼�
		 */
		initEven();
	}  

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
	addDRow();
	}

	/**
	 * �����豸������ϸ������
	 */
	public void addDRow() {  
		String column = "DEVPRO_CODE;DEV_CHN_DESC;QTY;UNIT_PRICE;MAN_CODE;SPECIFICATION;MODEL;BRAND;XJ;REMARK";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();              
		for (int i = 0; i < stringMap.length; i++) {
			if (stringMap[i].equals("DEVPRO_CODE"))  
				//�����Ǹ���                  
				tableDParm.setData(stringMap[i], "B");  
		}
		((TTable) getComponent(TABLE2)).addRow(tableDParm);
	}

	/**
	 * �¼���ʼ��
	 */
	public void initEven() {
		// ϸ��TABLE2ֵ�ı����
		addEventListener(TABLE2 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// TABLE2�س��¼�
		callFunction("UI|RECEIPT_QTY|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onChange");      
		// ϸ��TABLE2�����¼�         
		getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "");
		getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
	}



	/**
	 * ��ѯ��������DEV_BASE
	 * 
	 * @return boolean
	 */
	public TParm onDevBase(String devCode) {
		String sql = " SELECT * FROM DEV_BASE" + " WHERE SETDEV_CODE = '"
				+ devCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}


    /**
     * ֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //�������༭�¼�
        if(onTableQty(node)) 
            return true;
        return false; 
   }
    /**
     * �豸�������༭�¼�  
     * @param node TTableNode
     * @return boolean
     */
    public boolean onTableQty(TTableNode node){
 	   //fux need modify 
        if(node.getColumn() != 5)
             return false;   
        TParm parm = ((TTable)getComponent(TABLE2)).getParmValue();
        //System.out.println("д��DD��parm"+parm);
        TTable tableD = ((TTable)getComponent(TABLE2));     
         if(Integer.parseInt(node.getValue() + "") == 0){
             messageBox("������������");
             return true; 
         } 
         if(Integer.parseInt(node.getValue() + "") > Integer.parseInt("" +tableD.getValueAt(node.getRow(),6))){
             messageBox("���������ɴ��ڿ����");  
             return true;  
         }           
         return false;
    }

	/**
	 * �õ����ֿؼ�
	 * 
	 * @param tag
	 *            String
	 * @return TNumberTextField
	 */
	public TNumberTextField getTNumberTextField(String tag) {
		return (TNumberTextField) this.getComponent(tag);
	}

	/**
	 * �õ�����֮ǰ���к�
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column) {
		return this.getTTable(TABLE2).getColumnModel().getColumnIndex(column);
	}

	/**
	 * ����ʵ������
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}

	/** 
	 * ����(����parm)
	 * 
	 * @return boolean 
	 */
   public void  onSave(){         
	   TParm parm = this.getTTable(TABLE2).getParmValue();
	   //����parm  
       if (parm.getErrCode()<0) {   
           this.messageBox("E0005!");  
           return;   
       }   
       else     
       {         	      
          this.messageBox("P0005");
		  this.setReturnValue(parm);  
		  this.closeWindow(); 
       }
   } 
	
	
	/**
	 * ��Ӧ���������¼�
	 */
	public void onSupCodeChick() {
		String supCode = this.getValueString("SUP_CODE");
		TParm parm = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='"
										+ supCode + "'"));
		if (parm.getCount() < 0)
			return;
		this.setValue("SUP_SALES1", parm.getData("SUP_SALES1", 0));
		this.setValue("SUP_SALES1_TEL", parm.getData("SUP_SALES1_TEL", 0));
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ��빺��������
	 * 
	 * @return TParm
	 */
	public TParm getPurOrderM(String receiptNo) {
		TParm result = new TParm();
		// ���յ���
		result.setData("RECEIPT_NO", receiptNo);
		// ��������
		result.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
		// ��������
		result.setData("RECEIPT_DATE", this.getValue("RECEIPT_DATE"));
		// ���տ���
		result.setData("RECEIPT_DEPT", this.getValue("RECEIPT_DEPT"));
		// ������Ա
		result.setData("RECEIPT_USER", this.getValue("RECEIPT_USER"));
		// ��Ʊ����
		result.setData("INVOICE_DATE", this.getValue("INVOICE_DATE"));
		// ��Ʊ���
		result.setData("INVOICE_AMT", this.getValue("INVOICE_AMT"));
		// ��Ʊ����
		result.setData("INVOICE_NO", this.getValue("INVOICE_NO"));
		// ��Ӧ����
		result.setData("SUP_CODE", this.getValue("SUP_CODE"));
		// ���ռ�¼
		result.setData("RECEIPT_MINUTE", this.getValue("RECEIPT_MINUTE"));
		// //��������
		// TParm temp = getOtherData(this.getValueString("PURORDER_NO"));
		// result.setData("PURORDER_DATE",temp.getData("PURORDER_DATE"));
		// //��������
		// result.setData("RES_DELIVERY_DATE",temp.getData("RES_DELIVERY_DATE"));
		// ��ע
		result.setData("REMARK", this.getValue("REMARK"));
		result.setData("FINAL_FLG", "0");
		result.setData("CONTRACT_DATE", this.getValue("CONTRACT_DATE"));
		return result;
	}

	/**
	 * �õ��������ںͽ�������
	 * 
	 * @param purOrderNo
	 *            String
	 * @return TParm
	 */
	public TParm getOtherData(String purOrderNo) {
		TParm result = new TParm(this.getDBTool().select(
				"SELECT * FROM DEV_PURORDERM WHERE PURORDER_NO = '"
						+ purOrderNo + "'"));
		return result.getRow(0);
	}

	/**
	 * �õ��빺������
	 * 
	 * @param purOrderNo
	 *            String
	 * @return TParm
	 */
	public TParm getRequestData(String requestNo) {
		TParm result = new TParm(this.getDBTool().select(
				"SELECT * FROM DEV_PURCHASEM WHERE REQUEST_NO = '" + requestNo
						+ "'"));
		return result.getRow(0);
	}


	/**
	 * ���ϸ���
	 * 
	 * @return TParm
	 */
	public TParm isCheckMItem() {
		TParm result = new TParm();
		TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
		int rowCount = dateStore.rowCount();
		if (rowCount <= 0) {
			result.setErrCode(-1);
			result.setErrText("����д�豸��ϸ���ϣ�");
			return result;
		}
		for (int i = 0; i < rowCount; i++) {
			if (!dateStore.isActive(i))
				continue;
			if (dateStore.getItemDouble(i, "UNIT_PRICE") <= 0) {
				result.setErrCode(-2);
				result.setErrText("�豸��Ϊ:"
						+ dateStore.getItemString(i, "DEV_CHN_DESC")
						+ "����Ŀ����д�ο��۸�");
				return result;
			}
			if (dateStore.getItemInt(i, "QTY") <= 0) {
				result.setErrCode(-3);
				result.setErrText("�豸��Ϊ:"
						+ dateStore.getItemString(i, "DEV_CHN_DESC")
						+ "����Ŀ����д����������");
				return result;
			}
		}
		return result;
	}

	/**
	 * ����TABLE������
	 * 
	 * @param tag
	 *            String
	 * @param queryParm
	 *            TParm
	 * @return TDataStore
	 */
	public TDataStore getTableTDataStore(String tag) {
		TDataStore dateStore = new TDataStore();
		if (tag.equals("TABLE1")) {
			String sql = "SELECT * FROM DEV_RECEIPTM";
			String buffer = "";
			TParm queryParm = dateStore.getBuffer(buffer);
			// ��ȡ��ѯ�ֶ���
			String columnName[] = queryParm.getNames();
			if (columnName.length > 0)
				sql += " WHERE ";
			int count = 0;
			// �������ֵ�Ὺѭ����ʾ
			for (String temp : columnName) {
				if (temp.equals("QEND_DATE"))
					continue;
				if (temp.equals("YEND_DATE"))
					continue;
				// ��������
				if (temp.equals("QSTART_DATE")) {
					if (count > 0)
						sql += " AND ";
					sql += " RECEIPT_DATE BETWEEN TO_DATE('"
							+ queryParm.getValue("QSTART_DATE")
							+ "','YYYYMMDD') " + " AND TO_DATE('"
							+ queryParm.getValue("QEND_DATE") + "','YYYYMMDD')";
					count++;
					continue;
				}
				// //Ԥ����������(��Ʊ����)
				// if(temp.equals("YSTART_DATE")){
				// if(count>0)
				// sql+=" AND ";
				// sql+=" RES_DELIVERY_DATE BETWEEN TO_DATE('"+queryParm.getValue("YSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("YEND_DATE")+"','YYYYMMDD')";
				// count++;
				// continue;
				// }
				if (count > 0)
					sql += " AND ";
				sql += temp + "='" + queryParm.getValue(temp) + "' ";
				count++;
			}
			// System.out.println("sql:"+sql);
			dateStore.setSQL(sql);
			dateStore.retrieve();
		}
		if (tag.equals("TABLE2")) {
			String receiptNo = this.getValueString("QRECEIPT_NO");
			DevReceiptDDataStore devReceiptDataStore = new DevReceiptDDataStore();
			devReceiptDataStore.setReceiptNo(receiptNo);
			devReceiptDataStore.onQuery();
			// �ο��۸��ܼ۸�
			double totAmt = getTotAmt(devReceiptDataStore);
			this.setValue("INVOICE_AMT", totAmt);
			return devReceiptDataStore;
		}
		return dateStore;
	}

	/**
	 * �õ���ϸ������
	 * 
	 * @param requestNo
	 *            String
	 * @return DevBaseDataStore
	 */
	public DevReceiptDDataStore getRequestDData(String receiptNo) {
		DevReceiptDDataStore devReceiptDataStore = new DevReceiptDDataStore();
		devReceiptDataStore.setReceiptNo(receiptNo);
		devReceiptDataStore.onQuery();
		// �ο��۸��ܼ۸�
		double totAmt = getTotAmt(devReceiptDataStore);
		if (this.getValueDouble("INVOICE_AMT") == 0) {  
			this.setValue("INVOICE_AMT", totAmt);
		}
		return devReceiptDataStore;
	}

	/**
	 * ���㷢Ʊ�ܼ۸�SUM_QTYȡ����
	 * 
	 * @param devBaseDataStore
	 *            TDataStore
	 * @return double
	 */
	public double getTotAmt(TDataStore devBaseDataStore) {
		int rowCount = devBaseDataStore.rowCount();
		double totAmt = 0;
		for (int i = 0; i < rowCount; i++) {
			if (!devBaseDataStore.isActive(i)
					&& !(Boolean) devBaseDataStore.getItemData(i, "#NEW#"))
				continue;
			totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")
					* devBaseDataStore.getItemDouble(i, "SUM_QTY");
		}
		return totAmt;
	}

	/**
	 * ���㵥���ܼ�
	 * 
	 * @param devBaseDataStore
	 *            TDataStore
	 * @return double 
	 */
	public double getXJAmt(TDataStore devBaseDataStore) {
		int rowCount = devBaseDataStore.rowCount();
//		DataStore data = new DataStore();
//		int G = data.columnCount();
//		if ( data.getErrCode()< 0) {
//			
//		}
		double totAmt = 0;
		for (int i = 0; i < rowCount; i++) {
			if (!devBaseDataStore.isActive(i)
					&& !(Boolean) devBaseDataStore.getItemData(i, "#NEW#"))
				continue;
			totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")
					* devBaseDataStore.getItemDouble(i, "QTY");
		}
		return totAmt;
	}


	/**
	 * ���
	 */
	public void onClear() {
		// �ж��Ƿ񱣴�
		// ���
		this
				.clearValue("QRECEIPT_NO;QPURORDER_NO;QSUP_CODE;PURORDER_NO;RECEIPT_NO;INVOICE_AMT;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;INVOICE_NO;RECEIPT_MINUTE;REMARK;CONTRACT_DATE");
		/**
		 * ��ʼ��ҳ��
		 */  
		onInitPage();
		this.getTTable(TABLE2).setLockColumns("0,2,4,5,7");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
  		// ��ʼ��TABLE2       
		// ���Ĳ�  
		this.getTTable(TABLE2).setDataStore(getRequestDData(""));
		this.getTTable(TABLE2).setDSValue();
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// �ж��Ƿ񱣴�
		return true;
	}

	/**
	 * �õ�TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * �õ�TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * �õ�TTextArea
	 * 
	 * @param tag
	 *            String
	 * @return TTextArea
	 */
	public TTextArea getTTextArea(String tag) {
		return (TTextArea) this.getComponent(tag);
	}

	/**
	 * �õ�TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	
	

}
