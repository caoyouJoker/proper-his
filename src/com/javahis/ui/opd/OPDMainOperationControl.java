package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: 诊疗项目Panel
 * </p>
 *
 * <p>
 * Description:诊疗项目Panel
 * </p>
 *
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class OPDMainOperationControl extends TControl {
	ODO odo;
	int selectRow = -1;
	int orignalrows = 0;
	int linkno = 0;
	String nullStr="";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEOPERATION|addEventListener", "TABLEOPERATION->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.addEventListener("TABLEOPERATION->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		//table专用的监听
		callFunction("UI|TABLEOPERATION|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		TTable table = (TTable) this.callFunction("UI|TABLEOPERATION|getThis");
		table.addEventListener(TTableEvent.RIGHT_CLICKED, this, "showOrderSet");
		table
				.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
						"onMainFlg");
		initOperation(0);
	}

	/**
	 * 主连接的checkbox的点击事件
	 * @param o Object
	 */
	public void onMainFlg(Object obj) {

		TTable table = (TTable) obj;

		// 第几张处方
		int i = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		// TABLE上第几行
		int c = table.getSelectedRow();
		// 第几列
		int column = table.getSelectedColumn();
		Object value = table.getValueAt(c,column);
		// 取得一个Order
		Order o = odo.getPrescriptionList().getOrderList("4", i).getOrder(c);
		o.modifyLinkmainFlg(TCM_Transform.getString(value));
		if ("Y".equalsIgnoreCase(TCM_Transform.getString(value))) {
			linkno = ++linkno;
		} else {
			linkno = --linkno;
		}
		o.modifyLinkNo(TCM_Transform.getString(linkno));
		this
				.callFunction("UI|TABLEOPERATION|setValueAt", linkno, c,
						column + 1);
	}

	/**
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		selectRow = row;
		if (column != 2)
			return;
		if (!(com instanceof TTextField))
			return;

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm rxType=new TParm();
		rxType.setData("RX_TYPE",4);
		//给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("bbb", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"),rxType);
		//给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.callFunction("UI|TABLEOPERATION|acceptText");
		OrderList ol = odo.getPrescriptionList().getOrderList("4",
				this.getValueInt("RX_NO") - 1);
//		this.messageBox_(selectRow);
//		this.messageBox_(ol.size()-1);
		if(ol==null||ol.size()<1||ol.size()-1<selectRow)
			return;

		Order o = ol.getOrder(selectRow);
		String phaType=parm.getValue("ORDER_CAT1_CODE");
//		this.messageBox_(phaType);
		if(StringUtil.isNullString(phaType)){
			this.messageBox_("取得医嘱信息错误");
			return;
		}
        double own_amt=0.0;
        double ar_amt=0.0;
        double own_price=0.0;
        String setFlg=parm.getValue("ORDERSET_FLG");
//        // System.out.println("setFlg====="+setFlg);
        TParm  parmtable=new TParm();
		if(!StringUtil.isNullString(setFlg)&&("N".equalsIgnoreCase(setFlg)||"".equals(setFlg))){
			String[] ctz=new String[]{
					odo.getReg().getCtz1Code(),odo.getReg().getCtz2Code(),odo.getReg().getCtz3Code()
			};
//			// System.out.println("parm===================="+parm);
			OdoUtil.fillOrder(o,parm,ctz);
			TParm parmOrder=o.getParm();
//			// System.out.println("parmOrder===="+parmOrder);
			parmtable = OdoUtil.calculatePayAmount(parmOrder);
//			// System.out.println("parmtable"+parmtable);
			this.callFunction("UI|TABLEOPERATION|setRowParmValue", selectRow,
					parmtable);
			onNew();
		}else {
//				// System.out.println("start================");
				o=OdoUtil.initExaOrder(odo.getReg(),parm,o,odo.isChild());
//				// System.out.println(o.getParm()+" before===========");
	        	OrderList oltemp=OdoUtil.addOrder(odo.getReg(), ol, selectRow,odo.isChild());
	        	if(oltemp==null)
	        	{
	        		this.messageBox_("获取医嘱数据错误");
	        		return ;
	        	}

	        	o.modifyDosageQty(1.0);
	        	o.modifyDispenseQty(1.0);
	        	o.modifyMediQty(1.0);
	        	own_amt=o.getOwnAmt();
	        	o.modifyOwnAmt(0.0);
	        	ar_amt=o.getArAmt();
	        	o.modifyArAmt(0.0);
	        	own_price=o.getOwnPrice();
	        	o.modifyOwnPrice(0.0);
	        	o.modifySetmainFlg("Y");
		        parm = o.getParm();
		        parm.setData("OWN_PRICE", own_price);
		        parm.setData("OWN_AMT",own_amt);
		        parm.setData("AR_AMT",ar_amt);

				parmtable=OdoUtil.calculatePayAmount(parm);

//			// System.out.println(parmtable+" after===========");
//	        this.setValue("TOTPAY", ol.getSumFee()+own_amt);
			this.callFunction("UI|TABLEOPERATION|setRowParmValue",selectRow, parmtable);
			onNew();
		}
	}

	/**
	 * 初始化界面
	 *
	 * @param int
	 *            indexOfPrescription TParm的下标
	 */
	public void initOperation(int indexOfPrescription) {
		if (odo == null) {
			return;
		}
		if (odo.getPrescriptionList().getOrderList("4", indexOfPrescription) == null) {
			OrderList ol=odo.getPrescriptionList().newOrderList("4");
			ol.newOrder();
		}else{
			OrderList ol=odo.getPrescriptionList().getOrderList("4", indexOfPrescription);
//			this.messageBox_(ol.getOrder(ol.size()-1).getOrderCode()+"=order");
			if(!StringUtil.isNullString(ol.getOrder(ol.size()-1).getOrderCode())){
				ol.newOrder();
			}

//			// System.out.println("ol.getparm==="+ol.getParm());
		}
		/*
		 * 连,30,boolean;组号,60,int;医嘱,100;用量,100,double;
		 * 开药单位,80;频次,80;途径,80;日份,100,int;盒,30,boolean;
		 * 单价,100,double;总量,80,double;发药单位,80;总价,200,double;
		 * 折扣金额,200,double;应付金额,200,double;医生备注,200;护士备注,200;
		 * 执行科室,80;急作,40,boolean;给付类别,80;收费时间,200;配药时间,200;医嘱确认,200
		 */
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "4");
		if (rxparm == null) {
			callFunction("UI|RX_NO|setParmValue", new TParm());
			setValue("RX_NO", "");
			return;
		}

		callFunction("UI|RX_NO|setParmValue", rxparm);
		if (odo.getPrescriptionList().getOrderList("4", indexOfPrescription) == null) {
			setValue("RX_NO", "");
		} else {
			setValue("RX_NO", odo.getPrescriptionList().getOrderList("4",
					indexOfPrescription).getPresrtNo());
		}
		//		setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm 下标换算成"RX_NO"的下标

//		initTable(indexOfPrescription);
	}

	/**
	 * 给定处方号在TABLE上显示该处方签的每条医嘱
	 *
	 * @param rxNo
	 */
	public void initTable(int rxNo) {
		callFunction("UI|TABLEOPERATION|setParmValue",new TParm());
		callFunction("UI|TABLEOPERATION|removeRowAll");
		if (rxNo < 0 || rxNo >= odo.getPrescriptionList().getGroupPrsptSize("4"))
            return;
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("4", rxNo);
		if(ol==null||ol.size()<1)
			return;

		TParm parm=ol.getParm(ol.PRIMARY);
//		// System.out.println("inintTable"+parm);
//		// System.out.println("inintTable"+ol.getParm(ol.PRIMARY));
		if (parm.getData("PAYAMOUNT",0) == null) {
			// 计算应付金额，用总金额-折扣金额
			OdoUtil.calculatePayAmount(parm);

		}
		if(parm.getCount() <= 0)
			return;


		callFunction("UI|TABLEOPERATION|setParmValue", parm);


	}

	/**
	 * TABLE点击事件
	 *
	 * @param row
	 */
	public void onTableClicked(int row) {
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow = row;
	}

	/**
	 * TABLE值改变事件
	 *
	 * @param ttableNode
	 */
	public void onTableChangeValue(Object ttableNode) {
//
//		TParm parm = new TParm();
//		TTableNode mytableNode = (TTableNode) ttableNode;
//		// 该CELL的值
//		String value = TCM_Transform.getString(mytableNode.getValue());
//		// TABLE上第几行
//		int c = mytableNode.getRow();
//		// 第几列
//		int column = mytableNode.getColumn();
//		int seq_no = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
//		Order o = odo.getPrescriptionList().getOrderList("4", seq_no).getOrder(
//				c);
//		/*
//		 * 连,30,boolean;组号,60,int;医嘱,100;用量,100,double;
//		 * 开药单位,80,UNIT_CODE;频次,80,FREQ_CODE;日份,100,int;
//		 * 单价,100,double;总量,80,double;总价,200,double;
//		 * 折扣金额,200,double;应付金额,200,double;医生备注,200;
//		 * 护士备注,200;执行科室,80,RBORDER_DEPT_CODE;急作,40,boolean;
//		 * 给付类别,80,INSPAY_TYPE;收费时间,200;执行时间,200;医嘱确认,200
//		 */
//		switch (column) {
//		//		// 连
//		//		case 0:
//		//			o.modifyLinkmainFlg(value);
//		//			break;
//		// 组号
//		case 1:
//			o.modifyLinkNo(TCM_Transform.getString(value));
//			break;
//		//		// 医嘱
//		//		case 2:
//		//			o.modifyOrderCode(value);
//		//			break;
//		// 用量
//		case 3:
//			o.modifyMediQty(TCM_Transform.getDouble(value));
//			parm = OdoUtil.calcuQty(o).getParm();
//			this.callFunction("UI|TABLEOPERATION|setRowParmValue", selectRow,
//					parm);
//			break;
//		// 单位
//		case 4:
//			o.modifyDispenseUnit(value);
//			break;
//		// 频次
//		case 5:
//			o.modifyFreqCode(value);
//			break;
//		// 日份
//		case 6:
//			o.modifyTakeDays(TCM_Transform.getInt(value));
//			break;
//		// 单价
//		case 7:
//			o.modifyOwnPrice(TCM_Transform.getDouble(value));
//			break;
//		// 总量
//		case 8:
//			o.modifyDosageQty(TCM_Transform.getDouble(value));
//			break;
//		// 备注
//		case 10:
//			o.modifyDrNote(value);
//			break;
//		// 执行科室
//		case 12:
//			o.modifyExecDeptCode(value);
//			break;
//		// 急作
//		case 13:
//			o.modifyUrgentFlg(value);
//			break;
//		}
		TParm parm = new TParm();
		TTableNode mytableNode = (TTableNode) ttableNode;
		String value = TCM_Transform.getString(mytableNode.getValue());
		// 第几张处方
		int i = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		// TABLE上第几行
		int c = mytableNode.getRow();
		// 第几列
		int column = mytableNode.getColumn();
		// 取得一个Order
		OrderList ol=odo.getPrescriptionList().getOrderList("4", i);

		Order o =ol .getOrder(c);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		TTable table = (TTable) this.callFunction("UI|TABLEOPERATION|getThis");
		String columnName = table.getParmMap(column);
		/*
		 * RELEASE_FLG;LINKMAIN_FLG;LINK_NO;ORDER_CODE;MEDI_QTY;MEDI_UNIT;
		 * FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OPD_GIVEBOX_FLG;
		 * OWN_PRICE;DISPENSE_QTY;DISPENSE_UNIT;OWN_AMT;
		 * PAYAMOUNT;AR_AMT;DESCRIPTION;NS_NOTE;RBORDER_DEPT_CODE;
		 * URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE
		 */
		// 自备
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			o.modifyReleaseFlg(TCM_Transform.getString(value));
			return;
		}
	}

	/**
	 * 处方号combo值改变事件
	 */
	public void onRxNoChanged() {
		if (this.getValue("RX_NO") != null
				&& !"".equals(((String) this.getValue("RX_NO")))) {
			initTable(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
		}
	}

	/**
	 * TABLE新增一行数据
	 */
	public void onNew() {
		if (odo == null)
			return;
		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		Order o = odo.getPrescriptionList().getOrderList("4", index).newOrder();
		TParm parm = o.getParm();
		TParm parmtable = OdoUtil.calculatePayAmount(parm);
		this.callFunction("UI|TABLEOPERATION|addRow", parmtable);
	}

	/**
	 * TABLE删除一行数据
	 */
	public void onDelete() {
		callFunction("UI|TABLEOPERATION|removeRow", selectRow);
		if (selectRow == -1)
			return;
		OrderList ol = odo.getPrescriptionList().getOrderList("4",
				TCM_Transform.getInt(this.getValue("RX_NO")) - 1);

		Order o = ol.getOrder(selectRow);
		if (!OdoUtil.isChargedOrder(o)) {
			ol.removeData(o);
			initOperation(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
			return;
		}
	}

	/**
	 * 新增一张处方签 功能：1.清空现在的TABLE中的数据 2.新建同处方类型的处方签 3.TABLE中新增空行
	 */
	public void onNewOrderList() {
		callFunction("UI|TABLEOPERATION|setParmValue",new TParm());
		callFunction("UI|TABLEOPERATION|removeRowAll");
		odo.getPrescriptionList().newOrderList("4");
		initOperation(odo.getPrescriptionList().getGroupPrsptSize("4") - 1);

	}

	/**
	 * 删除一张处方签
	 */
	public void onDeleteOrderList() {
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol = odo.getPrescriptionList().getOrderList("4", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLEOPERATION|setParmValue",new TParm());
		callFunction("UI|TABLEOPERATION|removeRowAll");
		initOperation(rxNo - 1);
	}

	/**
	 * 设置ODO对象
	 *
	 * @param odo
	 */
	public void setOdo(Object odo) {

		if (odo == null)
			return;
		this.odo = (ODO) odo;
		initOperation(0);
	}
	/**
	 * 清空界面
	 */
	public void onClear(){
		this.setValue("RBORDER_DEPT_CODE", nullStr);
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("SHOW_GOODS", false);
		callFunction("UI|TABLEOPERATION|setParmValue",new TParm());
		callFunction("UI|TABLEOPERATION|removeRowAll");
	}
	public Object callMessage(String message, Object parm) {
		return super.callMessage(message, parm);
	}

}
