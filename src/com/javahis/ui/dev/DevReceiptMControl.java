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
 * Title: 验收管理
 * </p>
 * 
 * <p>
 * Description: 验收管理（验收单）
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
public class DevReceiptMControl extends TControl {
	/**
	 * 动作类名称
	 */
	private String actionName = "action.dev.DevAction";
	/**
	 * 主
	 */
	private  String TABLE1 = "TABLE1";
	/**
	 * 细
	 */
	private  String TABLE2 = "TABLE2";
	/**
	 * 请购进度
	 */ 
	private String rateofproCode = "";

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
		// 科室、人员
		this.setValue("QRECEIPT_DEPT", Operator.getDept());
		this.setValue("QRECEIPT_USER", Operator.getID());
		this.setValue("RECEIPT_DEPT", Operator.getDept());
		this.setValue("RECEIPT_USER", Operator.getID());
		// 起日
		Timestamp startDate = StringTool.getTimestamp(StringTool.getString(
				StringTool.getTimestamp(new Date()), "yyyy/MM/dd"),
				"yyyy/MM/dd");
		// 迄日
		Timestamp endDate = StringTool.getTimestamp("9999/12/31", "yyyy/MM/dd");
		// 查询验收日期起日
		this.setValue("QSTART_DATE", startDate);
		// 查询验收日期迄日
		this.setValue("QEND_DATE", endDate);
		// 验收日期
		this.setValue("RECEIPT_DATE", startDate);
		// 预定交货日起日
		this.setValue("QUSERSTART_DATE", startDate);
		// 预定交货日迄日
		this.setValue("QUSEEND_DATE", endDate);
		// 预定交货日
		this.setValue("INVOICE_DATE", startDate);
		// 初始化TABLE1
		this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
		this.getTTable(TABLE1).setDSValue();
		// 初始化TABLE2
		this.getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
		this.getTTable(TABLE2).setDSValue();
		// 添加一行
		insertRow(TABLE2);
		this.getTTable(TABLE2).setDSValue();
	}

	/**
	 * 当TABLE创建编辑控件时临时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onChange(Component com, int row, int column) {
		// 设备属性
		String receiptQty = this.getTTable(TABLE2).getDataStore()
				.getItemString(row, "RECEIPT_QTY");
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE2, column);
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();

		TParm parm = new TParm();
		parm.setData("RECEIPT_QTY", receiptQty);

	}

	/**
	 * 添加一行
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	public int insertRow(String tag) {
		int rowNum = -1;
		boolean falg = true;
		TTable table = this.getTTable(tag);
		TDataStore tabDataStore = table.getDataStore();
		int rowCount = tabDataStore.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!tabDataStore.isActive(i)) {
				falg = false;
				break;
			}
		}
		if (falg) {
			rowNum = tabDataStore.insertRow();
			tabDataStore.setActive(rowNum, false);
		}
		return rowNum;
	}

	/**
	 * 加入设备出库明细表格空行
	 */
	public void addDRow() {
		String column = "DEVPRO_CODE;DEV_CHN_DESC;QTY;BRAND;SPECIFICATION;MODEL;SUM_QTY;"
				+ "RECEIPT_QTY;UNIT_PRICE;XJ;REMARK";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			if (stringMap[i].equals("DEVPRO_CODE"))
				tableDParm.setData(stringMap[i], "A");
			else
				tableDParm.setData(stringMap[i], "");
		}
		((TTable) getComponent("TABLE2")).addRow(tableDParm);
	}

	/**
	 * 事件初始化
	 */
	public void initEven() {
		// TABLE2表事件
		getTTable("TABLE2").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponent");
		// 主项TABLE1单击事件
		callFunction("UI|" + TABLE1 + "|addEventListener", TABLE1 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
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
		          
//		getTTable("TABLE2").addEventListener(
//				"TABLE2->" + TTableEvent.DOUBLE_CLICKED, this,
//				"onDoubleClicked");
	}

	/**
	 * 当TABLE创建编辑控件时临时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// 设备属性
		String devProCode = this.getTTable(TABLE2).getDataStore()
				.getItemString(row, "DEVPRO_CODE");
		// 状态条显示
		callFunction("UI|setSysStatus", "");
		// 拿到列名
		String columnName = this.getFactColumnName(TABLE2, column);
		if (!"DEV_CHN_DESC".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("DEVPRO_CODE", devProCode);
		parm.setData("ACTIVE", "Y");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("DEVBASE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\DEVBASEPopupUI.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm action = (TParm) obj;
		callFunction("UI|setSysStatus", action.getValue("DEV_CODE") + ":"
				+ action.getValue("DEV_CHN_DESC")
				+ action.getValue("SPECIFICATION"));

		this.getTTable(TABLE2).acceptText();
		int rowNum = this.getTTable(TABLE2).getSelectedRow();
		// 判断是否可以添加新的项目
		if ((rateofproCode.equals("C") || rateofproCode.equals("D") || rateofproCode
				.equals("E"))) {
			this.getTTable(TABLE2).getDataStore().setItem(rowNum,
					"DEV_CHN_DESC", "");
			this.getTTable(TABLE2).setDSValue(rowNum);
			return;
		}
		// fux modify 20131101 去掉判断重复项DEV_CODE
		// 判断是否有重复项
		// if(this.isRepeatItem(action.getValue("DEV_CODE"),rowNum)){
		// this.messageBox("不可输入相同设备请在已有设备中修改数量！");
		// this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
		// this.getTTable(TABLE2).setDSValue(rowNum);
		// return;
		// }
		String columnArr[] = this.getTTable(TABLE2).getDataStore().getColumns();
		for (String temp : columnArr) {
			if (action.getValue(temp).length() == 0)
				continue;
			if ("OPT_DATE".equals(temp))
				continue;
			if ("OPT_USER".equals(temp))
				continue;
			if ("OPT_TERM".equals(temp))
				continue;
			this.getTTable(TABLE2).getDataStore().setItem(rowNum, temp,
					action.getData(temp));
		}
		this.getTTable(TABLE2).getDataStore().setActive(rowNum, true);
		// fux modify 20130728 加入空行
		String devCode = action.getValue("DEV_CODE");
		TParm devBase = onDevBase(devCode);
		if (devBase.getCount() <= 0) {
			this.insertRow(TABLE2);
			this.getTTable(TABLE2).setDSValue();
			this.getTTable(TABLE2).getTable().grabFocus();
			this.getTTable(TABLE2).setSelectedRow(rowNum);
			this.getTTable(TABLE2).setSelectedColumn(4);
		} else {
			this.insertRow(TABLE2);
			this.getTTable(TABLE2).setDSValue();
			this.getTTable(TABLE2).getTable().grabFocus();
			this.getTTable(TABLE2).setSelectedRow(rowNum);
			this.getTTable(TABLE2).setSelectedColumn(4);
			// 附件关联主件parm
			TParm tableParmSet = new TParm();
			for (int i = 0; i < devBase.getCount(); i++) {
				// addData是数组 setData是字符串
				// parmStockM =
				// getDevStock(devSetCode,this.getValueString("REQUESTOUT_DEPT"));
				// DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;SUM_QTY;RECEIPT_QTY;UNIT_PRICE;XJ;REMARK
				tableParmSet.setData("DEVPRO_CODE", devBase.getData(
						"DEVPRO_CODE", i));
				tableParmSet
						.setData("DEV_CODE", devBase.getData("DEV_CODE", i));
				tableParmSet.setData("DEV_CHN_DESC", devBase.getData(
						"DEV_CHN_DESC", i));
				// BRAND;SPECIFICATION;MODEL
				tableParmSet.setData("BRAND", "");
				tableParmSet.setData("SPECIFICATION", devBase.getData(
						"SPECIFICATION", i));
				tableParmSet.setData("MODEL", devBase.getData("MODEL", i));
				tableParmSet.setData("QTY", "0");
				tableParmSet.setData("SUM_QTY", "0");
				tableParmSet.setData("RECEIPT_QTY", "0");
				tableParmSet.setData("UNIT_PRICE", devBase.getData(
						"UNIT_PRICE", i));
				tableParmSet.setData("XJ", 0);
				tableParmSet.setData("REMARK", devBase
						.getData("DESCRIPTION", i));
				// System.out.println("tableParmSet"+tableParmSet);
				for (String temp : columnArr) {
					if (tableParmSet.getValue(temp).length() == 0)
						continue;
					if ("OPT_DATE".equals(temp))
						continue;
					if ("OPT_USER".equals(temp))
						continue;
					if ("OPT_TERM".equals(temp))
						continue;
					this.getTTable(TABLE2).getDataStore().setItem(
							rowNum + i + 1, temp, tableParmSet.getData(temp));
				}
				this.getTTable(TABLE2).getDataStore().setActive(rowNum + i + 1,
						true);
				this.insertRow(TABLE2);
				this.getTTable(TABLE2).setDSValue();
				this.getTTable(TABLE2).getTable().grabFocus();
				this.getTTable(TABLE2).setSelectedRow(rowNum + i + 1);
				this.getTTable(TABLE2).setSelectedColumn(4);
			}
		}

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
	 * 是否有重复项
	 * 
	 * @return boolean
	 */
	public boolean isRepeatItem(String devOrder, int selCount) {
		boolean falg = false;
		TDataStore dataStore = this.getTTable(TABLE2).getDataStore();
		int rowCount = dataStore.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!dataStore.isActive(i))
				continue;
			if (i == selCount)
				continue;
			if (devOrder.equals(dataStore.getItemString(i, "DEV_CODE")))
				falg = true;
		}
		return falg;
	}

	/**
	 * 临时医嘱修改事件监听
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		if ("UNIT_PRICE".equals(columnName)) {
			node.getTable().getDataStore().setItem(node.getRow(), "UNIT_PRICE",
					node.getValue());
			double XJAmt = this.getXJAmt(node.getTable().getDataStore());
			node.getTable().getDataStore().setItem(node.getRow(), "XJ", XJAmt);
			node.getTable().setDSValue(node.getRow());
		}
		// fux need modify 20130808
		if ("QTY".equals(columnName)) {
			node.getTable().getDataStore().setItem(node.getRow(), "QTY",
					node.getValue());
			double XJAmt = this.getXJAmt(node.getTable().getDataStore());
			node.getTable().getDataStore().setItem(node.getRow(), "XJ", XJAmt);
			node.getTable().setDSValue(node.getRow());
		}
		// 去掉验收数小于订购数的校验，直接可以验收
		if ("RECEIPT_QTY".equals(columnName)) {
			TParm rowParm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// 验收数>订购数，则报错
			// if(Integer.parseInt(node.getValue().toString())>rowParm.getInt("QTY")-rowParm.getInt("SUM_QTY")||
			// Integer.parseInt(node.getValue().toString())<0){
			if (Integer.parseInt(node.getValue().toString()) > rowParm
					.getInt("QTY")
					|| Integer.parseInt(node.getValue().toString()) < 0) {
				this.messageBox("验收数输入错误请重新输入！");
				return true;
			}
			node.getTable().getDataStore().setItem(node.getRow(),
					"RECEIPT_QTY", node.getValue());
			node.getTable().setDSValue(node.getRow());
		}
		double totAmt = this.getXJAmt(node.getTable().getDataStore());
		this.setValue("INVOICE_AMT", totAmt);
		return false;
	}

	/**
	 * 点击事件
	 */
	public void onTableClicked(int row) {
		TParm parm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
		callFunction("UI|setSysStatus", parm.getValue("DEV_CODE") + ":"
				+ parm.getValue("DEV_CHN_DESC")
				+ parm.getValue("SPECIFICATION"));
		String purorderNo = parm.getValue("PURORDER_NO");
		TParm purParm = new TParm(this.getDBTool().select(
				"SELECT RATEOFPRO_CODE,PAYMENT_TERMS FROM DEV_PURORDERM WHERE PURORDER_NO='"
						+ purorderNo + "'"));
		// 请购进度
		rateofproCode = purParm.getValue("RATEOFPRO_CODE", 0);
		this.setValue("PURORDER_NO", parm.getData("PURORDER_NO"));
		this.setValue("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		this.setValue("RECEIPT_DEPT", parm.getData("RECEIPT_DEPT"));
		this.setValue("RECEIPT_USER", parm.getData("RECEIPT_USER"));
		this.setValue("PURORDER_DATE", parm.getData("PURORDER_DATE"));
		this.setValue("RECEIPT_DATE", parm.getData("RECEIPT_DATE"));
		this.setValue("RES_DELIVERY_DATE", parm.getData("RES_DELIVERY_DATE"));
		this.setValue("INVOICE_AMT", parm.getData("INVOICE_AMT"));
		this.setValue("SUP_CODE", parm.getData("SUP_CODE"));
		this.setValue("INVOICE_NO", parm.getData("INVOICE_NO"));
		this.setValue("INVOICE_DATE", parm.getData("INVOICE_DATE"));
		this.setValue("RECEIPT_MINUTE", parm.getData("RECEIPT_MINUTE"));
		this.setValue("PAYMENT_TERMS", purParm.getValue("PAYMENT_TERMS", 0));
		this.setValue("REMARK", parm.getData("REMARK"));
		// 判断是否可以添加新的项目
		if (rateofproCode.equals("E")) {
			this.getTTextFormat("RECEIPT_DEPT").setEnabled(false);
			this.getTTextFormat("RECEIPT_USER").setEnabled(false);
			this.getTTextFormat("RECEIPT_DATE").setEnabled(false);
			this.getTNumberTextField("INVOICE_AMT").setEnabled(false);
			this.getTTextFormat("SUP_CODE").setEnabled(false);
			this.getTTextArea("RECEIPT_MINUTE").setEnabled(false);
			this.getTTextField("PAYMENT_TERMS").setEnabled(false);
			this.getTNumberTextField("INVOICE_AMT").setEnabled(false);
			this.getTTextField("INVOICE_NO").setEnabled(false);
			this.getTTextFormat("INVOICE_DATE").setEnabled(false);
		} else {
			this.getTTextFormat("RECEIPT_DEPT").setEnabled(true);
			this.getTTextFormat("RECEIPT_USER").setEnabled(true);
			this.getTTextFormat("RECEIPT_DATE").setEnabled(true);
			this.getTNumberTextField("INVOICE_AMT").setEnabled(true);
			this.getTTextFormat("SUP_CODE").setEnabled(true);
			this.getTTextArea("RECEIPT_MINUTE").setEnabled(true);
			this.getTTextField("PAYMENT_TERMS").setEnabled(true);
			this.getTNumberTextField("INVOICE_AMT").setEnabled(true);
			this.getTTextField("INVOICE_NO").setEnabled(true);
			this.getTTextFormat("INVOICE_DATE").setEnabled(true);
		}
		// 初始化TABLE2
		this.getTTable(TABLE2).setDataStore(
				getRequestDData(parm.getValue("RECEIPT_NO")));
		this.getTTable(TABLE2).setDSValue();
		if (rateofproCode.equals("E")) {
			this.getTTable(TABLE2).setLockColumns("all");
		} 
		else {
			this.getTTable(TABLE2).setLockColumns("0,3,9");  
		}    
	}

	/**
	 * 扫描赋值事件 fux modify
	 * 
	 * @param tag
	 *            String
	 * @return void
	 */
	public void onRead() {
		TParm parm = new TParm();// 从扫描单上得到参数
		this.setValue("RECEIPT_NO", parm.getValue("RECEIPT_NO"));// 供应厂商
		this.setValue("RECEIPT_DATE", parm.getValue("RECEIPT_DATE"));// 验收日期
		this.setValue("PURORDER_NO", parm.getValue("PURORDER_NO"));// 订购单号
		this.setValue("SUP_CODE", parm.getValue("SUP_CODE"));// 供应厂商
		this.setValue("SUP_SALES1", parm.getValue("SUP_SALES1"));// 联络人员
		this.setValue("SUP_SALES1_TEL", parm.getValue("SUP_SALES1_TEL"));// 联络电话
		this.setValue("PAYMENT_TERMS", parm.getValue("PAYMENT_TERMS"));// 付款条件
		this.setValue("REMARK", parm.getValue("REMARK"));// 备注
		this.setValue("RECEIPT_DEPT", parm.getValue("RECEIPT_DEPT"));// 验收科室
		this.setValue("RECEIPT_USER", parm.getValue("RECEIPT_USER"));// 验收人员
		this.setValue("INVOICE_AMT", parm.getValue("INVOICE_AMT"));// 发票金额
		this.setValue("INVOICE_NO", parm.getValue("INVOICE_NO"));// 发票号码
		this.setValue("RECEIPT_MINUTE", parm.getValue("RECEIPT_MINUTE"));// 验收记录
		this.setValue("INVOICE_DATE", parm.getValue("INVOICE_DATE"));// 发票日期
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
	 * 保存
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		// 保存一次累计验收数+String receiptQty =
		// this.getTTable(TABLE2).getDataStore().getItemString(row,"RECEIPT_QTY");
		// 检核细表
		TParm checkParm = isCheckMItem();
		if (checkParm.getErrCode() < 0) {
			this.messageBox(checkParm.getErrText());
			return false;
		}   
		// 新增
		if (this.getValueString("RECEIPT_NO").length() == 0) {
			// 供应厂商可以为空
			// if(this.getValueString("SUP_CODE").length()==0){
			// this.messageBox("请填写供应产商！");
			// return false;
			// }
			// 验收单号
			String receiptNo = SystemTool.getInstance().getNo("ALL", "DEV",
					"RECEIPT_NO", "RECEIPT_NO");
			// 订购主表
			TParm purorderMParm = this.getPurOrderM(receiptNo);
			String sqlRequestM[] = new String[] { this.creatRequestSQL(
					purorderMParm, "INSERT") };
			for (String temp : sqlRequestM) {   
				//System.out.println("temp:" + temp);
			}
			// 订购细表
			Timestamp timestamp = StringTool.getTimestamp(new Date());
			TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
			int rowCount = dateStore.rowCount();
			int seqNo = 1;                      
			// DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;RECEIPT_QTY;SUM_QTY;QTY;
			// UNIT_PRICE;XJ;REMARK
			// fux modify 20130710 加入SUM_QTY累计验收量计算
			for (int i = 0; i < rowCount; i++) {                  
				String Qty = this.getTTable(TABLE2).getDataStore()
						.getItemString(i, "QTY");
				String sumQty = this.getTTable(TABLE2).getDataStore()
						.getItemString(i, "SUM_QTY");
				if (!dateStore.isActive(i))  
					continue;
				dateStore.setItem(i, "RECEIPT_NO", receiptNo);  
				dateStore.setItem(i, "SUM_QTY", Qty);
				dateStore.setItem(i, "SEQ_NO", seqNo);
				dateStore.setItem(i, "OPT_USER", Operator.getID());
				dateStore.setItem(i, "OPT_DATE", timestamp);
				dateStore.setItem(i, "OPT_TERM", Operator.getIP());
				// 未完成
				dateStore.setItem(i, "FINAL_FLG", "0");
				seqNo++;
			}        
			//dateStore.getUpdateSQL() D表                                              
			//sqlRequestM              M表                    
			sqlRequestM = StringUtil.getInstance().copyArray(
					dateStore.getUpdateSQL(), sqlRequestM);   
			for (String sql : sqlRequestM) {
				 System.out.println("sqlMMMM:" + sql);             
			}    
			// TParm otherParm =
			// this.getOtherData(this.getValueString("PURORDER_NO"));
			// String[] sqlPurOrder = new
			// String[]{"UPDATE DEV_PURORDERM SET RATEOFPRO_CODE='D',CHK_USER='"+Operator.getID()+"',CHK_DATE=SYSDATE WHERE PURORDER_NO='"+this.getValueString("PURORDER_NO")+"'"};
			// sqlRequestM =
			// StringUtil.getInstance().copyArray(sqlPurOrder,sqlRequestM);
			// if(!otherParm.getValue("REQUEST_NO").equals("FALSE")){
			// String[] sqlRequest = new
			// String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='D' WHERE REQUEST_NO='"+otherParm.getValue("REQUEST_NO")+"'"};
			// sqlRequestM =
			// StringUtil.getInstance().copyArray(sqlRequest,sqlRequestM);
			// }
			TParm sqlParm = new TParm();
			sqlParm.setData("SQL", sqlRequestM);
			TParm actionParm = TIOM_AppServer.executeAction(actionName,
					"saveDevRequest", sqlParm);
			//messageBox("sqlParm:"+sqlParm);
			if (actionParm.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return false;
			}   
			this.messageBox("保存成功！");
			this.setValue("RECEIPT_NO", receiptNo);
		} else {
			// 保存更新
			if ((rateofproCode.equals("E"))) {
				this.messageBox("进度状态不可以修改！");
				return false;
			}
			this.getTTable(TABLE2).acceptText();
			// 请购主表
			TParm requestMParm = this.getPurOrderM(this
					.getValueString("RECEIPT_NO"));
			String sqlRequestM[] = new String[] { this.creatRequestSQL(
					requestMParm, "UPDATE") };
			for (String temp : sqlRequestM) {
				// System.out.println("temp:"+temp);
			}
			// 请购细表
			Timestamp timestamp = StringTool.getTimestamp(new Date());
			TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
			int newRow[] = dateStore.getNewRows();
			int rowCount = dateStore.rowCount();
			int seqNo = dateStore.rowCount() - newRow.length;
			for (int row : newRow) {
				String receiptQty = this.getTTable(TABLE2).getDataStore()
						.getItemString(row, "RECEIPT_QTY");
				String sumQty = this.getTTable(TABLE2).getDataStore()
						.getItemString(row, "SUM_QTY");
				if (!dateStore.isActive(row))
					continue;
				dateStore.setItem(row, "RECEIPT_NO", this
						.getValueString("RECEIPT_NO"));
				dateStore.setItem(row, "SUM_QTY", receiptQty);
				dateStore.setItem(row, "SEQ_NO", seqNo);
				dateStore.setItem(row, "OPT_USER", Operator.getID());
				dateStore.setItem(row, "OPT_DATE", timestamp);
				dateStore.setItem(row, "OPT_TERM", Operator.getIP());
				seqNo++;

			}             
			// 删除
			for (int i = rowCount - 1; i >= 0; i--) {
				if (!dateStore.isActive(i)&& dateStore.getItemString(i, "RECEIPT_NO").length() != 0) {
					dateStore.setActive(i, true);
					dateStore.deleteRow(i);
				}
			}
			sqlRequestM = StringUtil.getInstance().copyArray(
					dateStore.getUpdateSQL(), sqlRequestM); 
			for (String sql : sqlRequestM) {
				// System.out.println("sql:"+sql);
			}
			TParm sqlParm = new TParm();
			sqlParm.setData("SQL", sqlRequestM);
			TParm actionParm = TIOM_AppServer.executeAction(actionName,
					"saveDevRequest", sqlParm);
			if (actionParm.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return false;
			}
			this.messageBox("保存成功！");
		}
		this.onPrint();
		this.onClear();
		// this.onQuery();
		// 添加一行
		return true;
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
	 * 生成DEV_PURCHASEM表插入语句
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String creatRequestSQL(TParm parm, String type) {
		String sql = "";
		// fux modify 20130806 添加验收完成标记
		if ("INSERT".equals(type)) {
			sql = "INSERT INTO DEV_RECEIPTM (RECEIPT_NO,PURORDER_NO,RECEIPT_DATE,SUP_CODE,REMARK,RECEIPT_DEPT,RECEIPT_USER," +
					"INVOICE_NO,INVOICE_DATE,INVOICE_AMT,RECEIPT_MINUTE,PURORDER_DATE,RES_DELIVERY_DATE,OPT_USER," +
					"OPT_DATE,OPT_TERM,FINAL_FLG,CONTRACT_DATE ) VALUES ('"
					+ parm.getValue("RECEIPT_NO")
					+ "','"
					+ parm.getValue("PURORDER_NO")
					+ "',TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("RECEIPT_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),'"
					+ parm.getValue("SUP_CODE")
					+ "','"
					+ parm.getValue("REMARK")
					+ "','"  
					+ parm.getValue("RECEIPT_DEPT")
					+ "','"
					+ parm.getValue("RECEIPT_USER")
					+ "','"
					+ parm.getValue("INVOICE_NO")
					+ "',TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("INVOICE_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),'"
					+ parm.getValue("INVOICE_AMT")
					+ "','"
					+ parm.getValue("RECEIPT_MINUTE")
					+ "',TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("PURORDER_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),TO_DATE('"
					+ StringTool.getString(parm
							.getTimestamp("RES_DELIVERY_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),'"  
					+ Operator.getID()
					+ "',SYSDATE,'"    
					+ Operator.getIP()
					+ "','" + parm.getValue("FINAL_FLG") + "',TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("CONTRACT_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'))";
		} else {
			sql = "UPDATE DEV_RECEIPTM SET RECEIPT_NO='"
					+ parm.getValue("RECEIPT_NO")
					+ "',PURORDER_NO='"
					+ parm.getValue("PURORDER_NO")
					+ "',RECEIPT_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("RECEIPT_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),SUP_CODE='"
					+ parm.getValue("SUP_CODE")
					+ "',REMARK='"
					+ parm.getValue("REMARK")
					+ "',RECEIPT_DEPT='"
					+ parm.getValue("RECEIPT_DEPT")
					+ "',RECEIPT_USER='"
					+ parm.getValue("RECEIPT_USER")
					+ "',INVOICE_NO='"
					+ parm.getValue("INVOICE_NO")
					+ "',INVOICE_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("INVOICE_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS')" 
					+ "',CONTRACT_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("CONTRACT_DATE"),
							"yyyyMMddHHmmss")  
					+ "','YYYYMMDDHH24MISS')" 
					+ ",INVOICE_AMT='"
					+ parm.getValue("INVOICE_AMT")
					+ "',RECEIPT_MINUTE='"
					+ parm.getValue("RECEIPT_MINUTE")  
					+ "',PURORDER_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("PURORDER_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),RES_DELIVERY_DATE=TO_DATE('"
					+ StringTool.getString(parm
							.getTimestamp("RES_DELIVERY_DATE"),
							"yyyyMMddHHmmss")
					+ "','YYYYMMDDHH24MISS'),OPT_USER='" + Operator.getID()
					+ "',OPT_DATE=SYSDATE,OPT_TERM='" + Operator.getIP()
					+ "' WHERE RECEIPT_NO='" + parm.getValue("RECEIPT_NO")
					+ "'";
		}
		return sql;
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
			TParm queryParm = this.getTable1QueryParm();
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
	 * 拿到TABLE1的查询条件
	 * 
	 * @return TParm
	 */
	public TParm getTable1QueryParm() {
		TParm result = new TParm();
		String startDate = StringTool.getString((Timestamp) this
				.getValue("QSTART_DATE"), "yyyyMMdd");
		String endDate = StringTool.getString((Timestamp) this
				.getValue("QEND_DATE"), "yyyyMMdd");
		String deptCode = this.getValueString("QRECEIPT_DEPT");
		String operator = this.getValueString("QRECEIPT_USER");
		String requestNo = this.getValueString("QPURORDER_NO");
		String receiptNo = this.getValueString("QRECEIPT_NO");
		String supCode = this.getValueString("QSUP_CODE");
		String ydDateStart = StringTool.getString((Timestamp) this
				.getValue("QUSERSTART_DATE"), "yyyyMMdd");
		String ydDateEnd = StringTool.getString((Timestamp) this
				.getValue("QUSEEND_DATE"), "yyyyMMdd");
		if (startDate.length() != 0)
			result.setData("QSTART_DATE", startDate);
		if (endDate.length() != 0)
			result.setData("QEND_DATE", endDate);
		// if(ydDateStart.length()!=0)
		// result.setData("YSTART_DATE",ydDateStart);
		if (ydDateEnd.length() != 0)
			result.setData("YEND_DATE", ydDateEnd);
		if (deptCode.length() != 0)
			result.setData("RECEIPT_DEPT", deptCode);
		if (operator.length() != 0)
			result.setData("RECEIPT_USER", operator);
		if (receiptNo.length() != 0)
			result.setData("RECEIPT_NO", receiptNo);
		if (requestNo.length() != 0)
			result.setData("PURORDER_NO", requestNo);
		if (supCode.length() != 0)
			result.setData("SUP_CODE", supCode);
		return result;
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
		this.getTTable(TABLE2).setLockColumns("3,9,10");        
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		this
				.clearValue("PURORDER_NO;RECEIPT_NO;INVOICE_AMT;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;INVOICE_NO;RECEIPT_MINUTE;REMARK;CONTRACT_DATE");
		// 初始化TABLE1
		this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
		this.getTTable(TABLE1).setDSValue();
		// 初始化TABLE2
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

	/**
	 * 删除
	 */
	public void onDelete() {
		int row = this.getTTable(TABLE1).getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据！");
			return;
		}
		if ((rateofproCode.equals("E"))) {
			this.messageBox("进度状态不可以删除！");
			return;
		}
		if (messageBox("提示信息", "是否删除?", this.YES_NO_OPTION) != 0)
			return;
		this.getTTable(TABLE1).getDataStore().deleteRow(row);
		int rowCount = this.getTTable(TABLE2).getDataStore().rowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			if (!this.getTTable(TABLE2).getDataStore().isActive(i)
					&& this.getTTable(TABLE2).getDataStore().getItemString(i,
							"RECEIPT_NO").length() == 0) {
				continue; 
			}
			this.getTTable(TABLE2).getDataStore().deleteRow(i);
		}
		this.getTTable(TABLE1).setDSValue();
		this.getTTable(TABLE2).setDSValue();
		String arraySqlTable1[] = this.getTTable(TABLE1).getUpdateSQL();
		String arraySqlTable2[] = this.getTTable(TABLE2).getUpdateSQL(); 
		String arraySql[] = StringUtil.getInstance().copyArray(arraySqlTable1,
				arraySqlTable2);  
//		TParm requestParm = this.getTTable(TABLE1).getDataStore().getRowParm(
//				row);
//		String purOrderNo = requestParm.getValue("PURORDER_NO");
//		TParm otherParm = this.getOtherData(purOrderNo);
	/*	String[] sqlPurOrder = new String[] { "UPDATE DEV_PURORDERM SET RATEOFPRO_CODE='C',CHK_USER='"
				+ Operator.getID()
				+ "',CHK_DATE=SYSDATE WHERE PURORDER_NO='"
				+ this.getValueString("PURORDER_NO") + "'" };
		arraySql = StringUtil.getInstance().copyArray(sqlPurOrder, arraySql);
		if (!otherParm.getValue("REQUEST_NO").equals("FALSE")) {
			String[] sqlRequest = new String[] { "UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='C' WHERE REQUEST_NO='"
					+ otherParm.getValue("REQUEST_NO") + "'" };
			arraySql = StringUtil.getInstance().copyArray(sqlRequest, arraySql);
		}*/
		TParm sqlParm = new TParm();
		sqlParm.setData("SQL", arraySql);
		TParm actionParm = TIOM_AppServer.executeAction(actionName,
				"saveDevRequest", sqlParm);            
		if (actionParm.getErrCode() < 0) {
			this.messageBox("删除失败！");
			return;        
		}
		this.messageBox("删除成功！");
		this.onClear();
	}

	
	/**
	 * 打印验收单
	 */
	public void onPrint() {                  
		TParm parm = getPurOrderM(this.getValueString("RECEIPT_NO"));
		parm.setData("TITLE_NAME", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()));
		parm.setData("FORMS_NAME", "TEXT", "医疗设备验收单");   
		   
		
		TParm tableParm = this.getTTable(TABLE2).getDataStore().getBuffer(
				TDataStore.PRIMARY);
		// System.out.println("tableParm"+tableParm);
		//同一设备编号进行一张单据打印
		int rowCount = tableParm.getCount();      
		for (int i = 0; i < rowCount; i++) {     
			TParm printDataParm = new TParm();
			if (!tableParm.getBoolean("#ACTIVE#", i))  
				continue;  
			//第i行数据
			TParm tableParmRow = this.getTTable(TABLE2).getDataStore().getBuffer(
					TDataStore.PRIMARY).getRow(i);
			//messageBox("第i行数据:"+tableParmRow);  
//			printDataParm
//					.addRowData(tableParm,i,"DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;RECEIPT_QTY;REMARK;QTY;XJ");
			printDataParm.setRowData(i, tableParmRow);
//			printDataParm
//			.addRowData(tableParmRow,i,"DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;RECEIPT_QTY;REMARK;QTY;XJ");
			printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");    
			printDataParm.addData("SYSTEM", "COLUMNS", "QTY");
			printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE"); 
			printDataParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			// 验收数           
			printDataParm.addData("SYSTEM", "COLUMNS", "QTY");  
			//Double tot_amt = 0.00;                  
			printDataParm.addData("SYSTEM", "COLUMNS", "XJ");               
			printDataParm.setCount(printDataParm.getCount("DEV_CODE"));        
			  
			parm.setData("DEV_CHN_DESC", "TEXT", printDataParm.getData("DEV_CHN_DESC", 0));
			parm.setData("SPECIFICATION", "TEXT", printDataParm.getData("SPECIFICATION", 0));
			parm.setData("UNIT_PRICE", "TEXT", printDataParm.getData("UNIT_PRICE", 0));
			parm.setData("MODEL", "TEXT", printDataParm.getData("MAN_CODE", 0));
			//parm.setData("BRAND", "TEXT", printDataParm.getData("BRAND", 0));          
			parm.setData("QTY", "TEXT", printDataParm.getData("QTY", 0));                  
			parm.setData("CONTRACT_DATE", "TEXT",this.getValueString("CONTRACT_DATE").length()> 0 ? 
					this.getValueString("CONTRACT_DATE").substring(0,10):"");  
			parm.setData("SUP_CODE", "TEXT", this.getValueString("SUP_CODE"));   
			parm.setData("UNIT", "TEXT", getUnitDesc(printDataParm.getValue("DEV_CODE", 0))); 
			parm.setData("TITLE", "TEXT", Manager.getOrganization() 
					.getHospitalCHNFullName(Operator.getRegion())
					+ "验收单");   
			parm.setData("TABLE", printDataParm.getData());
			this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevReceiptForms.jhw",
					parm, false);
		}

	}

	
	/**  
	 * 查询单位中文
	 */
	private String getUnitDesc(String data) {
		String sql = " SELECT UNIT_CHN_DESC FROM SYS_UNIT A,DEV_BASE B " +
				" WHERE A.UNIT_CODE = B.UNIT_CODE" +
				" AND B.DEV_CODE = '"+data+"' ";  
		//messageBox("sql:"+sql);  
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String result = parm.getValue("UNIT_CHN_DESC", 0);
		return result;
	}

	/**  
	 * 生成订购单
	 */
	public void onCreatPuro() {

		TParm parm = new TParm();

		Object obj = this.openDialog("%ROOT%\\config\\dev\\ReceiptMUI.x");
		if (obj == null) {
			return;
		}          
		TParm action = (TParm) obj;
		this.setValue("RECEIPT_NO", "");
		this.setValue("PURORDER_NO", action.getValue("PURORDER_NO"));
		this.setValue("SUP_CODE", action.getValue("SUP_CODE"));
		this.setValue("SUP_SALES1", action.getValue("SUP_SALES1"));
		this.setValue("SUP_SALES1_TEL", action.getValue("SUP_SALES1_TEL"));
		this.setValue("PAYMENT_TERMS", action.getValue("PAYMENT_TERMS")); 
		this.getTTextFormat("SUP_CODE").setEnabled(false);
		TParm purOrderDParm = new TParm(this.getDBTool().select(
				"SELECT * FROM DEV_PURORDERD WHERE PURORDER_NO='"
						+ action.getValue("PURORDER_NO") + "'"));          
		int rowCount = purOrderDParm.getCount();         
		for (int i = 0; i < rowCount; i++) {
			TParm temp = purOrderDParm.getRow(i);    
			this.popReturn(temp);
		}   
		this.getTTable(TABLE2).setDSValue();
		double totAmt = this.getTotAmt(this.getTTable(TABLE2).getDataStore());
		if (action.getDouble("INVOICE_AMT") != totAmt) {
			if (this.messageBox("提示信息", "报价格与订单总价格不相等是否取代！", this.YES_NO_OPTION) != 0) {
				this.setValue("INVOICE_AMT", totAmt);
			} else {
				this.setValue("INVOICE_AMT", action.getValue("TOT_AMT"));
			}
		}
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(TParm action) {
		this.getTTable(TABLE2).acceptText();
		int rowNum = this.getTTable(TABLE2).getDataStore().insertRow();
		// 判断是否可以添加新的项目
		if ((rateofproCode.equals("E"))) {
			this.messageBox("进度状态不可以添加");
			this.getTTable(TABLE2).getDataStore().setItem(rowNum,
					"DEV_CHN_DESC", "");
			this.getTTable(TABLE2).setDSValue(rowNum);
			return;
		}
		String columnArr[] = this.getTTable(TABLE2).getDataStore().getColumns();
		for (String temp : columnArr) {
			if (action.getValue(temp).length() == 0
					&& !"SET_PURORDER_NO".equals(temp) 
					&& !"SET_SEQ_NO".equals(temp))      
				continue;
			if ("OPT_DATE".equals(temp))  
				continue;
			if ("OPT_USER".equals(temp))
				continue;
			if ("OPT_TERM".equals(temp)) 
				continue;
			if ("SET_PURORDER_NO".equals(temp)) {
				this.getTTable(TABLE2).getDataStore().setItem(rowNum, temp,
						action.getData("PURORDER_NO"));
				continue;
			}
			if ("SET_SEQ_NO".equals(temp)) {
				this.getTTable(TABLE2).getDataStore().setItem(rowNum, temp,
						action.getData("SEQ_NO"));
				continue;
			}
			// fux modify
			this.getTTable(TABLE2).getDataStore().setItem(rowNum, temp,
					action.getData(temp));
		}
	}
	  
//	/**
//	 * 附件明细(打开附件明细维护)  
//	 */
//	public void onDoubleClicked(int row) {          
//        row = this.getTTable(TABLE2).getClickedRow();
//		TParm exportParm = new TParm();   
//		exportParm.setData("ROW",row);
//		TParm parmDiag = (TParm) openDialog(       
//		"%ROOT%\\config\\dev\\DEVFujianDetail.x", exportParm);  
//		//附件  SET_SEQ_NO 累计
//		this.getTTable(TABLE2).acceptText();
//		int rowNum = this.getTTable(TABLE2).getDataStore().insertRow();
//		String columnArr[] = this.getTTable(TABLE2).getDataStore().getColumns();
//		for (String temp : columnArr) { 
//			if ("REMARK".equals(temp)) {  
//				this.getTTable(TABLE2).getDataStore().setItem(rowNum, temp,
//						exportParm.getData());   
//				continue;  
//			}
//		}
//		
//	}
	
	

}
