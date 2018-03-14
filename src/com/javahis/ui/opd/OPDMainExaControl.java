package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TPopupMenu;
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
 * Title: 检验检查Panel
 * </p>
 *
 * <p>
 * Description:检验检查Panel
 * </p>
 *
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class OPDMainExaControl extends TControl {
	ODO odo;
	int selectRow=-1;
	int orignalrows=0;
	int linkno=0;
	boolean linkFlg=false;
	int linkOrder=-1;
	String nullStr="";
	double totpay=0.0;
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEEXA|addEventListener","TABLEEXA->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLEEXA->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		//table专用的监听
        callFunction("UI|TABLEEXA|addEventListener",TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
        TTable table=(TTable)this.callFunction("UI|TABLEEXA|getThis");
//        table.addEventListener(table.getTag()+"->"+TTableEvent.RIGHT_CLICKED,this, "showOrderSet");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this, "onMainFlg");
		initExa(0);
	}
	/**
	 * 右击事件
	 * @param row 右击行号
	 * @param x 鼠标位置
	 * @param y 鼠标位置
	 */
	public void showOrderSet(){
		TTable table=(TTable)this.callFunction("UI|TABLEEXA|getThis");
		selectRow=table.getClickedRow();
		int x=table.getMouseX();
		int y=table.getMouseY();
		TPopupMenu popup = new TPopupMenu();
		popup.setTag("POPUPMENU");
		popup.setParentComponent(table);
		popup.setLoadTag("UI");

		popup.init(getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSPOPUPMENU.x"));
		popup.onInit();
		popup.show(table,x,y);
	}
	public void onOrderSetShow(){
////		this.messageBox_(!"Y".equalsIgnoreCase(o.getSetmainFlg()));
////		this.messageBox_(o.getSetmainFlg());
//		if(o==null||StringUtil.isNullString(o.getOrderCode())||!"Y".equalsIgnoreCase(o.getSetmainFlg())){
//			this.messageBox_("该医嘱非集合医嘱");
//			return;
//		}
		TParm parm=new TParm();
		 OrderList ol=odo.getPrescriptionList().getOrderList("5", this.getValueInt("RX_NO")-1);
	        if(ol==null){
	        	this.messageBox_("数据装载失败");
	        	return;
	        }
		parm.setData("OL",ol);
		parm.setData("ORDER",ol.getOrder(selectRow));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x",parm);

	}
	/**
	 * 主连接的checkbox的点击事件
	 * @param o Object
	 */
	public void onMainFlg(Object obj){

		TTable table=(TTable)obj;
		table.acceptText();
	}
	 /**
	   *
	   * @param com
	   * @param row
	   * @param column
	   */
	  public void onCreateEditComponent(Component com,int row,int column)
	    {
		  selectRow=row;
	        if(column != 0)
	            return;
	        if(!(com instanceof TTextField))
	            return;

	        TTextField textfield = (TTextField)com;
	        textfield.onInit();
			TParm rxType=new TParm();
			rxType.setData("RX_TYPE",5);
	        //给table上的新text增加sys_fee弹出窗口
	        textfield.setPopupMenuParameter("bbb",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),rxType);
	        //给新text增加接受sys_fee弹出窗口的回传值
	        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");

	    }
	  /**
	   *
	   * @param tag
	   * @param obj
	   */
	  public void popReturn(String tag,Object obj)
	    {
	        TParm parm=(TParm )obj;
	        this.callFunction("UI|TABLEEXA|acceptText");
	        OrderList ol=odo.getPrescriptionList().getOrderList("5", this.getValueInt("RX_NO")-1);
			if(ol==null||ol.size()<1||ol.size()-1<selectRow)
				return;
	        int olindex=ol.size()-1;
	        Order o= ol.getOrder(olindex);
	        if(!StringUtil.isNullString(o.getOrderCode())){
	        	this.messageBox_("已开立医嘱不能变更，请先删除");
	        	return;
	        }


//	        this.messageBox_(ol.size()+"ol.size");
	        o=OdoUtil.initExaOrder(odo.getReg(),parm,o,odo.isChild());
	        double own_amt=0.0;
	        double ar_amt=0.0;
	        double own_price=0.0;
	        String setFlg=parm.getValue("ORDERSET_FLG");

			TParm parmtable=new TParm();
	        if(!StringUtil.isNullString(setFlg)&&"Y".equalsIgnoreCase(setFlg)){
//	        	this.messageBox_(o.getOrderDesc());
	        	OrderList oltemp=OdoUtil.addOrder(odo.getReg(), ol, olindex,odo.isChild());
	        	if(oltemp==null)
	        	{
	        		this.messageBox_("获取医嘱数据错误");
	        		return ;
	        	}

	        	o= ol.getOrder(olindex);
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
//		        this.messageBox_(o.getOrderDesc());

		        parm.setData("OWN_PRICE", own_price);
		        parm.setData("OWN_AMT",own_amt);
		        parm.setData("AR_AMT",ar_amt);

//		        // System.out.println("my parm=="+parm);
		        parmtable=new TParm();
				parmtable=OdoUtil.calculatePayAmount(parm);
//				// System.out.println("my last parm==="+parmtable);
	        }
//	        // System.out.println("my last parm==="+parmtable);
//	        this.setValue("TOTPAY", ol.getSumFee()+own_amt);
	        this.callFunction("UI|TABLEEXA|setRowParmValue",selectRow, new TParm());
			this.callFunction("UI|TABLEEXA|setRowParmValue",selectRow, parmtable);
//			this.messageBox_(ol.size());
			onNew();
	    }

	/**
	 * 初始化界面
	 * @param  int indexOfPrescription TParm的下标
	 */
	public void initExa(int indexOfPrescription){
		if(odo== null)
			return;
		if(odo.getPrescriptionList().getOrderList("5",
				indexOfPrescription)==null){
			OrderList ol=odo.getPrescriptionList().newOrderList("5");
			ol.newOrder();
		}else{
			OrderList ol=odo.getPrescriptionList().getOrderList("5", indexOfPrescription);
			if(!StringUtil.isNullString(ol.getOrder(ol.size()-1).getOrderCode())){
				ol.newOrder();
			}
		}
		//备,30,boolean;连,30,boolean;组号,60;医嘱,100;用量,100;开药单位,80;频次,80;途径,80;日份,100;单价,100;总量,80;
		//发药单位,80;盒,30,boolean;总价,200;折扣金额,200;应付金额,200;
		//医生备注,200;护士备注,200;执行科室,80;急作,30,boolean;给付类别,80;收费时间,200;配药时间,200;医嘱确认,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "5");
		if (rxparm == null){
			callFunction("UI|RX_NO|setParmValue", new TParm());
			setValue("RX_NO", "");
			return;
		}

		callFunction("UI|RX_NO|setParmValue", rxparm);
//			setValue("RBORDER_DEPT_CODE", rborderDeptCode);
			setValue("RX_NO", odo.getPrescriptionList().getOrderList("5",
					indexOfPrescription).getPresrtNo());

//		setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm 下标换算成"RX_NO"的下标

//		initTable(indexOfPrescription);
	}

	/**
	 * 初始化TABLE
	 * @param rxNo TParm 的下标，即RX_NO-1
	 */
	public void initTable(int rxNo){
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		if (rxNo < 0 || rxNo >= odo.getPrescriptionList().getGroupPrsptSize("5"))
            return;
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("5", rxNo);
		if(ol==null||ol.size()<1)
			return;
		this.setValue("TOTPAY", ol.getSumFee());
		TParm parm=odo.getPrescriptionList().getOrderList("5", rxNo).getTParmForTable();
		TParm oldpamr=odo.getPrescriptionList().getOrderList("5", rxNo).getParm(ol.PRIMARY);
//		// System.out.println("old parm"+oldpamr);
//		// System.out.println("exa parm"+parm);
		if(parm.getCount() <= 0)
			return;
		if (parm.getData("PAYAMOUNT",0) == null) {
			// 计算应付金额，用总金额-折扣金额
			OdoUtil.calculatePayAmount(parm);

		}
//		// System.out.println("after method"+parm);


		callFunction("UI|TABLEEXA|setParmValue", parm);

	}
	/**
	 * TABLE点击事件
	 * @param row
	 */
	public void onTableClicked(int row){
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow=row;
	}

	/**
	 * TABLE值改变事件
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode){
		TTableNode	mytableNode=(TTableNode)tableNode;
		//该CELL的值
		String value=TCM_Transform.getString(mytableNode.getValue());
		//TABLE上第几行
		int c=mytableNode.getRow();
		//第几列
		int column=mytableNode.getColumn();
		int seq_no=TCM_Transform.getInt(this.getValue("RX_NO"))-1;
		TTable table = (TTable) this.callFunction("UI|TABLEORDER|getThis");
		String columnName = table.getParmMap(column);
		Order o=odo.getPrescriptionList().getOrderList("5", seq_no).getOrder(c);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		/* 医嘱,100;
		 * 检体,100;数量,80,double;单位,80;单价,100,double;
		 * 总价,200,double;折扣金额,200,double;应付金额,200,double;
		 * 医生备注,200;护士备注,200;执行科室,80;急作,40,boolean;
		 * 给付类别,80;收费时间,200;执行时间,200;医嘱确认,200
		 */

		if("OPTITEM_CODE".equalsIgnoreCase(columnName)){
			o.modifyOptitemCode(value);
			return;
		}
		if("DR_NOTE".equalsIgnoreCase(columnName)){
			o.modifyDrNote(value);
			return;
		}
		if("URGENT_FLG".equalsIgnoreCase(columnName)){
			o.modifyUrgentFlg(value);
			return;
		}
	}
	/**
	 * 处方号combo值改变事件
	 */
	public void onRxNoChanged(){
		if(this.getValue("RX_NO")!=null&&!"".equals(((String)this.getValue("RX_NO")))){
			initTable(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
		}
	}
	/**
	 * TABLE新增一行数据
	 */
	public void onNew(){
		if (odo == null)
			return;
		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("5", index);
		Order o = ol.newOrder();

//		if(linkFlg){
//			Order orderMain=ol.getOrder(linkOrder);
//			OdoUtil.linkOrder(o, orderMain,linkno);
//		}
		TParm parm = o.getParm();
		TParm parmtable=OdoUtil.calculatePayAmount(parm);
		this.callFunction("UI|TABLEEXA|addRow", parmtable);
	}

	/**
	 * TABLE删除一行数据
	 */
	public void onDelete(){
		callFunction("UI|TABLEEXA|removeRow", selectRow);
		if (selectRow == -1)
			return;
		OrderList ol = odo.getPrescriptionList().getOrderList("5",
				TCM_Transform.getInt(this.getValue("RX_NO")) - 1);

		Order o = ol.getOrder(selectRow);
		if (!OdoUtil.isChargedOrder(o)) {
			OdoUtil.deleteOrderSet(ol,o);
			initExa(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
//			callFunction("UI|TABLEEXA|setParmValue",new TParm());
//			callFunction("UI|TABLEEXA|removeRowAll");
//			TParm parmtable=OdoUtil.calculatePayAmount(ol.getTableParm());
//			callFunction("UI|TABLEEXA|setParmValue",parmtable);
//			onNew();
		}

	}
	/**
	 * 新增一张处方签
	 * 功能：1.清空现在的TABLE中的数据
	 *      2.新建同处方类型的处方签
	 *      3.TABLE中新增空行
	 */
	public void onNewOrderList(){
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		odo.getPrescriptionList().newOrderList("5");
		initExa(odo.getPrescriptionList().getGroupPrsptSize("5") - 1);

	}
	/**
	 * 删除一张处方签
	 */
	public void onDeleteOrderList(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("5", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		initExa(rxNo-1);
	}
	/**
	 * 设置ODO对象
	 * @param odo
	 */
	public void setOdo(Object odo)
	{
		if(odo==null)
			return;
		this.odo=(ODO)odo;
		initExa(0);
	}
	/**
	 * 清空界面
	 */
	public void onClear(){
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("SHOW_GOODS", false);
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}

}
