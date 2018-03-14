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
 * Title: 验收管理(附件)
 * </p>
 * 
 * <p>
 * Description: 验收管理（附件） 
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
	 * 细
	 */
	private  String TABLE2 = "TABLE";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		/**
		 * 初始化页面
		 */
		onInitPage();
		/**
		 * 初始化事件
		 */
		initEven();
	}  

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
	addDRow();
	}

	/**
	 * 加入设备出库明细表格空行
	 */
	public void addDRow() {  
		String column = "DEVPRO_CODE;DEV_CHN_DESC;QTY;UNIT_PRICE;MAN_CODE;SPECIFICATION;MODEL;BRAND;XJ;REMARK";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();              
		for (int i = 0; i < stringMap.length; i++) {
			if (stringMap[i].equals("DEVPRO_CODE"))  
				//必须是附件                  
				tableDParm.setData(stringMap[i], "B");  
		}
		((TTable) getComponent(TABLE2)).addRow(tableDParm);
	}

	/**
	 * 事件初始化
	 */
	public void initEven() {
		// 细项TABLE2值改变监听
		addEventListener(TABLE2 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// TABLE2回车事件
		callFunction("UI|RECEIPT_QTY|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onChange");      
		// 细项TABLE2监听事件         
		getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "");
		getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
	}



	/**
	 * 查询基础属性DEV_BASE
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
     * 值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //出库量编辑事件
        if(onTableQty(node)) 
            return true;
        return false; 
   }
    /**
     * 设备出库量编辑事件  
     * @param node TTableNode
     * @return boolean
     */
    public boolean onTableQty(TTableNode node){
 	   //fux need modify 
        if(node.getColumn() != 5)
             return false;   
        TParm parm = ((TTable)getComponent(TABLE2)).getParmValue();
        //System.out.println("写入DD表parm"+parm);
        TTable tableD = ((TTable)getComponent(TABLE2));     
         if(Integer.parseInt(node.getValue() + "") == 0){
             messageBox("出库量不可零");
             return true; 
         } 
         if(Integer.parseInt(node.getValue() + "") > Integer.parseInt("" +tableD.getValueAt(node.getRow(),6))){
             messageBox("出库量不可大于库存量");  
             return true;  
         }           
         return false;
    }

	/**
	 * 得到数字控件
	 * 
	 * @param tag
	 *            String
	 * @return TNumberTextField
	 */
	public TNumberTextField getTNumberTextField(String tag) {
		return (TNumberTextField) this.getComponent(tag);
	}

	/**
	 * 拿到更变之前的列号
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column) {
		return this.getTTable(TABLE2).getColumnModel().getColumnIndex(column);
	}

	/**
	 * 返回实际列名
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
	 * 保存(传回parm)
	 * 
	 * @return boolean 
	 */
   public void  onSave(){         
	   TParm parm = this.getTTable(TABLE2).getParmValue();
	   //传回parm  
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
	 * 供应厂商下拉事件
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
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 拿到请购主档数据
	 * 
	 * @return TParm
	 */
	public TParm getPurOrderM(String receiptNo) {
		TParm result = new TParm();
		// 验收单号
		result.setData("RECEIPT_NO", receiptNo);
		// 订购单号
		result.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
		// 验收日期
		result.setData("RECEIPT_DATE", this.getValue("RECEIPT_DATE"));
		// 验收科室
		result.setData("RECEIPT_DEPT", this.getValue("RECEIPT_DEPT"));
		// 验收人员
		result.setData("RECEIPT_USER", this.getValue("RECEIPT_USER"));
		// 发票日期
		result.setData("INVOICE_DATE", this.getValue("INVOICE_DATE"));
		// 发票金额
		result.setData("INVOICE_AMT", this.getValue("INVOICE_AMT"));
		// 发票号码
		result.setData("INVOICE_NO", this.getValue("INVOICE_NO"));
		// 供应厂商
		result.setData("SUP_CODE", this.getValue("SUP_CODE"));
		// 验收记录
		result.setData("RECEIPT_MINUTE", this.getValue("RECEIPT_MINUTE"));
		// //订购日期
		// TParm temp = getOtherData(this.getValueString("PURORDER_NO"));
		// result.setData("PURORDER_DATE",temp.getData("PURORDER_DATE"));
		// //交货日期
		// result.setData("RES_DELIVERY_DATE",temp.getData("RES_DELIVERY_DATE"));
		// 备注
		result.setData("REMARK", this.getValue("REMARK"));
		result.setData("FINAL_FLG", "0");
		result.setData("CONTRACT_DATE", this.getValue("CONTRACT_DATE"));
		return result;
	}

	/**
	 * 拿到订购日期和交货日期
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
	 * 拿到请购表数据
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
	 * 检核细项表
	 * 
	 * @return TParm
	 */
	public TParm isCheckMItem() {
		TParm result = new TParm();
		TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
		int rowCount = dateStore.rowCount();
		if (rowCount <= 0) {
			result.setErrCode(-1);
			result.setErrText("请填写设备明细资料！");
			return result;
		}
		for (int i = 0; i < rowCount; i++) {
			if (!dateStore.isActive(i))
				continue;
			if (dateStore.getItemDouble(i, "UNIT_PRICE") <= 0) {
				result.setErrCode(-2);
				result.setErrText("设备名为:"
						+ dateStore.getItemString(i, "DEV_CHN_DESC")
						+ "的项目请填写参考价格！");
				return result;
			}
			if (dateStore.getItemInt(i, "QTY") <= 0) {
				result.setErrCode(-3);
				result.setErrText("设备名为:"
						+ dateStore.getItemString(i, "DEV_CHN_DESC")
						+ "的项目请填写验收数量！");
				return result;
			}
		}
		return result;
	}

	/**
	 * 返回TABLE的数据
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
			// 获取查询字段名
			String columnName[] = queryParm.getNames();
			if (columnName.length > 0)
				sql += " WHERE ";
			int count = 0;
			// 数组与键值会开循环显示
			for (String temp : columnName) {
				if (temp.equals("QEND_DATE"))
					continue;
				if (temp.equals("YEND_DATE"))
					continue;
				// 验收日期
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
				// //预定交货日期(发票日期)
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
			// 参考价格总价格
			double totAmt = getTotAmt(devReceiptDataStore);
			this.setValue("INVOICE_AMT", totAmt);
			return devReceiptDataStore;
		}
		return dateStore;
	}

	/**
	 * 拿到明细表数据
	 * 
	 * @param requestNo
	 *            String
	 * @return DevBaseDataStore
	 */
	public DevReceiptDDataStore getRequestDData(String receiptNo) {
		DevReceiptDDataStore devReceiptDataStore = new DevReceiptDDataStore();
		devReceiptDataStore.setReceiptNo(receiptNo);
		devReceiptDataStore.onQuery();
		// 参考价格总价格
		double totAmt = getTotAmt(devReceiptDataStore);
		if (this.getValueDouble("INVOICE_AMT") == 0) {  
			this.setValue("INVOICE_AMT", totAmt);
		}
		return devReceiptDataStore;
	}

	/**
	 * 计算发票总价格（SUM_QTY取出）
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
	 * 计算单次总价
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
	 * 清空
	 */
	public void onClear() {
		// 判断是否保存
		// 清空
		this
				.clearValue("QRECEIPT_NO;QPURORDER_NO;QSUP_CODE;PURORDER_NO;RECEIPT_NO;INVOICE_AMT;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;INVOICE_NO;RECEIPT_MINUTE;REMARK;CONTRACT_DATE");
		/**
		 * 初始化页面
		 */  
		onInitPage();
		this.getTTable(TABLE2).setLockColumns("0,2,4,5,7");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
  		// 初始化TABLE2       
		// 从哪查  
		this.getTTable(TABLE2).setDataStore(getRequestDData(""));
		this.getTTable(TABLE2).setDSValue();
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// 判断是否保存
		return true;
	}

	/**
	 * 拿到TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * 拿到TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 拿到TTextArea
	 * 
	 * @param tag
	 *            String
	 * @return TTextArea
	 */
	public TTextArea getTTextArea(String tag) {
		return (TTextArea) this.getComponent(tag);
	}

	/**
	 * 拿到TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * 拿到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * 拿到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	
	

}
