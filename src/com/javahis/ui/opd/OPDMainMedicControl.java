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
 * Title: 药嘱Panel
 * </p>
 *
 * <p>
 * Description:药嘱Panel
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
public class OPDMainMedicControl extends TControl {
	ODO odo;
	int selectRow = -1;
	int orignalrows = 0;
	int linkno = 0;
	int linkOrder=-1;
	boolean linkFlg=false;
	String nullStr="";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEORDER|addEventListener", "TABLEORDER->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.addEventListener("TABLEORDER->" + TTableEvent.CHANGE_VALUE,
				"onTableOrderChangeValue");
		// table专用的监听
		callFunction("UI|TABLEORDER|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		TTable table = (TTable) this.callFunction("UI|TABLEORDER|getThis");
		table.addEventListener(TTableEvent.RIGHT_CLICKED, this, "showOrderSet");
		table
				.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
						"onMainFlg");
		this.setValue("W", "Y");
		initMedic(0);
	}

	/**
	 * 主连接的checkbox的点击事件
	 *
	 * @param o
	 *            Object
	 */
	public void onMainFlg(Object obj) {

		TTable table = (TTable) obj;
		table.acceptText();

	}

	/**
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		selectRow = row;
		if (column != 3)
			return;
		if (!(com instanceof TTextField))
			return;

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm rxType=new TParm();
		rxType.setData("RX_TYPE",1);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("bbb", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"),rxType);
		// 给新text增加接受sys_fee弹出窗口的回传值
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
		this.callFunction("UI|TABLEORDER|acceptText");
		OrderList ol = odo.getPrescriptionList().getOrderList("1",
				this.getValueInt("RX_NO") - 1);
		if(ol==null||ol.size()<1||ol.size()-1<selectRow)
			return;
//		this.messageBox_(ol.size());
//		// System.out.println("ol parm==="+ol.getParm());
//		this.messageBox_(selectRow);
		Order o = ol.getOrder(selectRow);
		String[] ctz=new String[]{
				odo.getReg().getCtz1Code(),odo.getReg().getCtz2Code(),odo.getReg().getCtz3Code()
		};
//		// System.out.println("parm===================="+parm);
		OdoUtil.fillOrder(o,parm,ctz);
//		this.messageBox_("ordersize"+ol.size());
		TParm parmOrder=o.getParm();
//		// System.out.println("parmOrder===="+parmOrder);
		TParm parmtable = OdoUtil.calculatePayAmount(parmOrder);
//		// System.out.println("parmtable"+parmtable);
		this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow,
				parmtable);
		onNew();

	}

	/**
	 * 初始化界面
	 *
	 * @param int
	 *            indexOfPrescription TParm的下标
	 */
	public void initMedic(int indexOfPrescription) {
		if (odo == null) {
			return;
		}
		OrderList ol=odo.getPrescriptionList().getOrderList("1", indexOfPrescription);
		Order o;
		if (ol == null||ol.size()<1) {
//			// System.out.println("orderlist is null");
			ol=odo.getPrescriptionList().newOrderList("1");
			o=ol.newOrder();
		}else{
			ol=odo.getPrescriptionList().getOrderList("1", indexOfPrescription);
			if(!StringUtil.isNullString(ol.getOrder(ol.size()-1).getOrderCode())){
				o=ol.newOrder();
//				this.messageBox_("init");
			}else{
				o=ol.getOrder(0);
			}
		}
		// 备,30,boolean;连,30,boolean;组号,60;医嘱,100;用量,100;开药单位,80;频次,80;途径,80;日份,100;单价,100;总量,80;
		// 发药单位,80;盒,30,boolean;总价,200;折扣金额,200;应付金额,200;
		// 医生备注,200;护士备注,200;执行科室,80;急作,30,boolean;给付类别,80;收费时间,200;配药时间,200;医嘱确认,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "1");

		if (rxparm == null) {
			callFunction("UI|RX_NO|setParmValue", new TParm());
			setValue("RBORDER_DEPT_CODE", o.getExecDeptCode());
			setValue("RX_NO", "");
			return;
		}

		callFunction("UI|RX_NO|setParmValue", rxparm);
		if (odo.getPrescriptionList().getOrderList("1", indexOfPrescription) == null) {
			setValue("RBORDER_DEPT_CODE", o.getExecDeptCode());
			setValue("RX_NO", "");
		} else {
			setValue("RBORDER_DEPT_CODE", o.getExecDeptCode());
			setValue("RX_NO", odo.getPrescriptionList().getOrderList("1",
					indexOfPrescription).getPresrtNo());
		}
		// setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm 下标换算成"RX_NO"的下标

//		initTable(indexOfPrescription);
	}

	/**
	 * 给定处方号在TABLE上显示该处方签的每条医嘱
	 *
	 * @param rxNo
	 */
	public void initTable(int rxNo) {

		callFunction("UI|TABLEORDER|setParmValue",new TParm());
		callFunction("UI|TABLEORDER|removeRowAll");
		if (rxNo < 0 || rxNo >= odo.getPrescriptionList().getGroupPrsptSize("1"))
            return;
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("1", rxNo);
		if(ol==null||ol.size()<1)
			return;
		TParm parm=odo.getPrescriptionList().getOrderList("1", rxNo).getParm(ol.PRIMARY);

		if (parm.getData("PAYAMOUNT",0) == null) {
			// 计算应付金额，用总金额-折扣金额
			OdoUtil.calculatePayAmount(parm);

		}
		if(parm.getCount() <= 0)
			return;


		callFunction("UI|TABLEORDER|setParmValue", parm);


		String phaType = odo.getPrescriptionList().getOrderList("1", rxNo)
				.getOrder(0).getPhaType();
		if (phaType == null || phaType.length() == 0)
			return;
		if ("W".equals(phaType))
			this.setValue("W", "Y");
		else if ("C".equals(phaType))
			this.setValue("C", "Y");
		else
			this.setValue("W", "Y");
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

	public void clearTableSelection() {
		TTable table = (TTable) this.callFunction("UI|TABLEORDER|getThis");
		table.clearSelection();
	}

	/**
	 * TABLE值改变事件
	 *
	 * @param ttableNode
	 */
	public void onTableOrderChangeValue(Object ttableNode) {
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
		OrderList ol=odo.getPrescriptionList().getOrderList("1", i);
//		this.messageBox_("ol.size"+ol.size());
//		this.messageBox_("c"+c);
		if(c>=ol.size()){
			return;
		}
		Order o =ol .getOrder(c);
		TTable table = (TTable) this.callFunction("UI|TABLEORDER|getThis");
		String columnName = table.getParmMap(column);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
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
		// 连
		if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) {
			o.modifyLinkmainFlg(TCM_Transform.getString(value));
			linkOrder=c;
			if ("Y".equalsIgnoreCase(TCM_Transform.getString(value))) {
				linkno++;
				linkFlg=true;
			} else {
				linkno--;
				linkFlg=false;
			}
			o.modifyLinkNo(TCM_Transform.getString(linkno));
			callFunction("UI|TABLEORDER|setValueAt", linkno, c, column + 1);
			return;
		}
		// 组号
		if ("LINK_NO".equalsIgnoreCase(columnName)) {

			int tempNo=TCM_Transform.getInt(value);
			if(tempNo>linkno)
			{
				this.messageBox_("该连接号没有主项");
				return;
			}
			if(tempNo==0){
				linkFlg=false;
			}
			o.modifyLinkNo(TCM_Transform.getString(tempNo));
			return;
		}

		// 盒药
		if ("GIVEBOX_FLG".equalsIgnoreCase(columnName)) {

			// the value is original value, not the last 1,so the real value is
			// opposite to itself
//			this.messageBox_(value);
			o.modifyGiveboxFlg(TCM_Transform.getString(value));
			OdoUtil.calcuQty(o);
			TParm boxparm = o.getParm();

			boxparm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			boxparm.setData("DOSAGE_QTY", o.getDispenseQty());
			this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow,
					boxparm);
			return;
		}
		// 急作
		if ("URGENT_FLG".equalsIgnoreCase(columnName)) {
			o.modifyUrgentFlg(TCM_Transform.getString(value));
			return;
		}
		// 用量
		if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getDouble(value)==o.getMediQty()){
				return;
			}
			o.modifyMediQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow, parm);

			return;
		}
		// 频次
		if ("FREQ_CODE".equalsIgnoreCase(columnName)) {
			if(value.equalsIgnoreCase(o.getFreqCode())){
				return;
			}
//			this.messageBox_(value);
			o.modifyFreqCode(value);
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow, parm);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){
				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){

					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
//						this.messageBox_(oTemp.getSeqNo());
						oTemp.modifyFreqCode(o.getFreqCode());
						parm = OdoUtil.calcuQty(o).getParm();
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLEORDER|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}
			}
			return;
		}
		// 途径
		if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			if(value.equalsIgnoreCase(o.getRouteCode())){
				return;
			}
			o.modifyRouteCode(value);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){

				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){

					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
						oTemp.modifyFreqCode(o.getFreqCode());
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLEORDER|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}

			}
			return;
		}
		// 日份
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getInt(value)==o.getTakeDays()){
				return;
			}
			o.modifyTakeDays(TCM_Transform.getInt(value));
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow, parm);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){

				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){

					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
//						this.messageBox_(oTemp.getSeqNo());
						oTemp.modifyFreqCode(o.getFreqCode());
						parm = OdoUtil.calcuQty(o).getParm();
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLEORDER|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}

			}
			return;
		}
		// 总量
		if ("DISPENSE_QTY".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getDouble(value)==o.getDispenseQty()){
				return;
			}
			if("Y".equalsIgnoreCase(o.getGiveboxFlg())){
				return;
			}
			o.modifyDispenseQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuMediQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLEORDER|setRowParmValue", selectRow, parm);
			return;
		}
		// 备注
		if ("DR_NOTE".equalsIgnoreCase(columnName)) {
			o.modifyDrNote(value);
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
		OrderList ol=odo.getPrescriptionList().getOrderList("1", index);
		Order o = ol.newOrder();

		if(linkFlg){
			Order orderMain=ol.getOrder(linkOrder);
			OdoUtil.linkOrder(o, orderMain,linkno);
		}
		TParm parm = o.getParm();
		TParm parmtable = OdoUtil.calculatePayAmount(parm);
		this.callFunction("UI|TABLEORDER|addRow", parmtable);
	}

	/**
	 * TABLE删除一行数据
	 */
	public void onDelete() {
		callFunction("UI|TABLEORDER|removeRow", selectRow);
		if (selectRow == -1)
			return;
		OrderList ol = odo.getPrescriptionList().getOrderList("1",
				TCM_Transform.getInt(this.getValue("RX_NO")) - 1);

		Order o = ol.getOrder(selectRow);
		if (!OdoUtil.isChargedOrder(o)) {
			ol.removeData(o);
			return;
		}
		onNew();
	}

	/**
	 * 新增一张处方签 功能：1.清空现在的TABLE中的数据 2.新建同处方类型的处方签 3.TABLE中新增空行
	 */
	public void onNewOrderList() {
		callFunction("UI|TABLEORDER|setParmValue",new TParm());
		callFunction("UI|TABLEORDER|removeRowAll");
		odo.getPrescriptionList().newOrderList("1");
		initMedic(odo.getPrescriptionList().getGroupPrsptSize("1") - 1);
	}

	/**
	 * 删除一张处方签
	 */
	public void onDeleteOrderList() {
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol = odo.getPrescriptionList().getOrderList("1", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLEORDER|setParmValue",new TParm());
		callFunction("UI|TABLEORDER|removeRowAll");
		initMedic(rxNo - 1);
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
		initMedic(0);
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
		callFunction("UI|TABLEORDER|setParmValue",new TParm());
		callFunction("UI|TABLEORDER|removeRowAll");
	}
	public Object callMessage(String message, Object parm) {
		return super.callMessage(message, parm);
	}

}
