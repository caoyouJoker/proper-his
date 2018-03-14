package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.OPDSysParmTool;
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
 * Title: 中药Panel
 * </p>
 *
 * <p>
 * Description:中药Panel
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
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDMainChnMedicControl extends TControl {
	ODO odo;
	int selectRow=-1;
	int orignalrows=0;
	int selectcolumn=-1;
	String nullStr="";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLECHNMEDIC|addEventListener","TABLECHNMEDIC->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLECHNMEDIC->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		//table专用的监听
        callFunction("UI|TABLECHNMEDIC|addEventListener",TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
        TParm parm=OPDSysParmTool.getInstance().selectAll();
        this.setValue("DCT_TAKE_DAYS", StringTool.getInt(parm.getValue("DCT_TAKE_DAYS",0)));
        this.setValue("DCT_TAKE_QTY", StringTool.getInt(parm.getValue("DCT_TAKE_QTY",0)));
		initChnMedic(0);
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
		  selectcolumn=column;
	        if(column != 0&&column!=3&&column!=6&&column!=9){
	        	return;
	        }

	        if(!(com instanceof TTextField)){
	        	return;
	        }

	        TTextField textfield = (TTextField)com;
	        textfield.onInit();
	        //给table上的新text增加sys_fee弹出窗口
	        textfield.setPopupMenuParameter("bbb",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"));
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
	        this.callFunction("UI|TABLECHNMEDIC|acceptText");
	        OrderList ol=odo.getPrescriptionList().getOrderList("3", this.getValueInt("RX_NO")-1);
			if(ol==null||ol.size()<1||ol.size()-1<selectRow)
				return;
	        Order o= ol.getOrder(OdoUtil.calculateOrderIndexForChnMed(selectRow, selectcolumn));
//	        o.initParm(parm);
	        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
	        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
	        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
	        String[] ctz=new String[]{
					odo.getReg().getCtz1Code(),odo.getReg().getCtz2Code(),odo.getReg().getCtz3Code()
			};
			OdoUtil.fillOrder(o,parm,ctz);
			this.callFunction("UI|TABLECHNMEDIC|setValueAt", o.getOrderDesc(),selectRow,selectcolumn);
			this.callFunction("UI|TABLECHNMEDIC|setValueAt", o.getMediQty(),selectRow,selectcolumn+1);
			if(selectcolumn==9){
	        	onNew();
	        }
	    }
	/**
	 * 初始化界面
	 * @param  int indexOfPrescription TParm的下标
	 */
	public void initChnMedic(int indexOfPrescription){
		if(odo== null)
			return;
		if(odo.getPrescriptionList().getOrderList("3", indexOfPrescription)==null){
			OrderList ol=odo.getPrescriptionList().newOrderList("3");
			for(int i=0;i<4;i++){
				Order o=ol.newOrder();
				o.modifyOrderDesc("");
				o.modifyOrderCode("");
				o.modifyDctexcepCode("");
				o.modifyMediQty(0.0);
			}
		}
		//备,30,boolean;连,30,boolean;组号,60;医嘱,100;用量,100;开药单位,80;频次,80;途径,80;日份,100;单价,100;总量,80;
		//发药单位,80;盒,30,boolean;总价,200;折扣金额,200;应付金额,200;
		//医生备注,200;护士备注,200;执行科室,80;急作,30,boolean;给付类别,80;收费时间,200;配药时间,200;医嘱确认,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "3");
		if (rxparm == null)
			return;
		this.callFunction("UI|RX_NO|setParmValue", rxparm);
		OrderList ol=odo.getPrescriptionList().getOrderList("3",
				indexOfPrescription);
		if(ol==null||ol.size()<1)
			return;
		String rborderDeptCode = ol.getOrder(0).getExecDeptCode();
		this.setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm 下标换算成"RX_NO"的下标
		this.setValue("RX_NO", indexOfPrescription + 1);
		Order o=ol.getOrder(0);
		this.setValue("DCT_TAKE_DAYS", o.getTakeDays());
		this.setValue("DCT_TAKE_QTY", o.getDctTakeQty());
		this.setValue("FREQ_CODE", o.getFreqCode());
		this.setValue("ROUTE_CODE", o.getRouteCode());
		this.setValue("DCTAGENT_CODE",o.getDctagentCode());
		this.setValue("DESCRIPTION", o.getDrNote());
		this.setValue("URGENT_FLG",o.getUrgentFlg());
		this.setValue("RELEASE_FLG", o.getReleaseFlg());
		this.setValue("TOTQTY", ol.getTotGram());
	}
	/**
	 * 初始化TABLE
	 * @param rxNo TParm 的下标，即RX_NO-1
	 */
	public void initTable(int rxNo){
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		if(odo==null){
			return;
		}
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("3", rxNo);
//		// System.out.println("6");
		if(ol==null||ol.size()<1)
			return;
		int differ=4-ol.size()%4;
		if(differ==4)differ=0;
//		// System.out.println("7:"+ol.size());
//		// System.out.println("7:"+differ);
		for(int i=0;i<differ;i++){
			Order o=ol.newOrder();
			o.modifyOrderCode("");
			o.modifyOrderDesc("");
			o.modifyMediQty(0.0);
			o.modifyDctexcepCode("");
		}
		TParm parm=ol.getParm(OrderList.PRIMARY);
//		// System.out.println("8:ol.parm"+parm);
		TParm parmtable=new TParm();
		parmtable=OdoUtil.chnMedicReArrange(parm);
		parm=null;
//		// System.out.println("chinese==="+parmtable);
		callFunction("UI|TABLECHNMEDIC|setParmValue",parmtable);

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
	 *
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode) {
		TParm parm = new TParm();
		TTableNode mytableNode = (TTableNode) tableNode;
		String value = TCM_Transform.getString(mytableNode.getValue());
		// 第几张处方
		int i = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		// TABLE上第几行
		int c = mytableNode.getRow();
		// 第几列
		int column = mytableNode.getColumn();
		int rxNo=OdoUtil.calculateOrderIndexForChnMed(selectRow,column);
//		this.messageBox_(rxNo);
		OrderList ol = odo.getPrescriptionList().getOrderList("3", i);
		// 取得一个Order
		if(ol==null||ol.size()<1){
			this.messageBox_("数据装载失败");
			return;
		}
		Order o = ol.getOrder(rxNo);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		TTable table = (TTable) this
				.callFunction("UI|TABLECHNMEDIC|getThis");
		String columnName = table.getParmMap(column);
		// 用量
		if (!StringUtil.isNullString(columnName)&&columnName.contains("TAKE_QTY")) {
//			this.messageBox_("here"+o.getOrderCode());
			o.modifyMediQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuQty(o).getParm();
			return;
		}
		// 特殊煎法
		if (!StringUtil.isNullString(columnName)&&columnName.contains("DCTEXCEP_CODE")) {
			o.modifyDctexcepCode(value);
			return;
		}
	}
	/**
	 * 处方号combo值改变事件
	 */
	public void onRxNoChanged(){
		initTable(TCM_Transform.getInt(this.getValue("RX_NO"))-1);
	}
	/**
	 * TABLE新增一行数据
	 */
	public void onNew(){
		if (odo == null){
			return;
		}

		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", index);
		if(ol==null|ol.size()<1){
//			this.messageBox_("ol is null");
			return;
		}
		//中药饮片一行数据就有4条医嘱
		TParm parm=new TParm();

		for(int i=0;i<4;i++){
			Order o=ol.newOrder();
			parm.setData("ORDER_CODE" + (i+1), "");
			parm.setData("TAKE_QTY" + (i+1), 0.0);
			parm.setData("DCTEXCEP_CODE" + (i+1), "");
		}


		this.callFunction("UI|TABLECHNMEDIC|addRow",parm);
	}
	/**
	 * TABLE删除一行数据
	 */
	public void onDelete(){
//		this.messageBox_("in delete");
		if(selectRow==-1)
			return;
		int column=TCM_Transform.getInt(this.callFunction("UI|TABLECHNMEDIC|getSelectedColumn"));
		int rxNo=OdoUtil.calculateOrderIndexForChnMed(selectRow,column);
//		this.messageBox_(rxNo);
		OrderList ol=odo.getPrescriptionList().getOrderList("3", TCM_Transform.getInt(this.getValue("RX_NO"))-1);
		ol.removeData(rxNo);
//		this.messageBox_(ol.size());

		initTable(TCM_Transform.getInt(this.getValue("RX_NO"))-1);

//		for(int i=rxNo;i<rxNo+3;i++){
//			this.callFunction("UI|TABLECHNMEDIC|setValueAt", null,selectRow,column);
//		}
////		this.callFunction("UI|TABLECHNMEDIC|removeRow", selectRow);
//		odo.getPrescriptionList().getOrderList("3", TCM_Transform.getInt(this.getValue("RX_NO"))-1).removeData(rxNo);
	}
	/**
	 * 新增一张处方签
	 * 功能：1.清空现在的TABLE中的数据
	 *      2.新建同处方类型的处方签
	 *      3.TABLE中新增空行
	 */
	public void onNewOrderList(){
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		OrderList ol=odo.getPrescriptionList().newOrderList("3");
//		for(int i=0;i<4;i++){
//			ol.newOrder();
//		}

		initChnMedic(odo.getPrescriptionList().getGroupPrsptSize("3")-1);
	}
	/**
	 * 删除一张处方签
	 */
	public void onDeleteOrderList(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		initChnMedic(0);
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
		initChnMedic(0);
	}
	/**
	 * 日份回車事件
	 */
	public void onTakeDays(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyTakeDays(this.getValueInt("DCT_TAKE_DAYS"));

		}
	}
	/**
	 * 飲片計量回車事件
	 */
	public void onDctTakeQty(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDctTakeQty(this.getValueInt("DCT_TAKE_QTY"));
		}
	}
	/**
	 * 頻次回車事件
	 */
	public void onFreq(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyFreqCode(TCM_Transform.getString(this.getValue("FREQ_CODE")));
		}

	}
	/**
	 * 用法回車事件
	 */
	public void onRoute(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyRouteCode(TCM_Transform.getString(this.getValue("ROUTE_CODE")));
		}
	}
	/**
	 * 煎藥方式回車事件
	 */
	public void onDctAgent(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDctagentCode(TCM_Transform.getString(this.getValue("DCTAGENT_CODE")));
		}

	}
	/**
	 * 備注失去焦點事件
	 */
	public void onDesc(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDrNote(TCM_Transform.getString(this.getValue("DESCRIPTION")));
		}
	}
	/**
	 * 急作點擊事件
	 */
	public void onUrgent(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyUrgentFlg(TCM_Transform.getString(this.getValue("URGENT_FLG")));
		}
	}
	/**
	 * 自備點擊事件
	 */
	public void onRelease(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyReleaseFlg(TCM_Transform.getString(this.getValue("RELEASE_FLG")));
		}

	}
	/**
	 * 清空界面
	 */
	public void onClear(){
//		this.setValue("RBORDER_DEPT_CODE", nullStr);
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("DCT_TAKE_DAYS", nullStr);
		this.setValue("DCT_TAKE_QTY", nullStr);
		this.setValue("FREQ_CODE", nullStr);
		this.setValue("ROUTE_CODE", nullStr);
		this.setValue("DCTAGENT_CODE", nullStr);
		this.setValue("DESCRIPTION", nullStr);
		this.setValue("TOTQTY", nullStr);
		this.setValue("URGENT_FLG", false);
		this.setValue("RELEASE_FLG", false);
		this.setValue("TOT_PAY", nullStr);
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}
}
