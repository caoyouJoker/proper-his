package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.bil.BilInvoice;
import jdo.hrm.HRMCompany;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMPrePay;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查预交金</p>
 * 
 * <p>Description: 健康检查预交金</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMPrePayControl extends TControl {
	//由外部调用时传入的团体代码
	private String companyCode;
	//TABLE
	private TTable table;
	//团体COMBO
	private TComboBox comCombo;
	
	private TTextFormat format;
	//数据对象
	private HRMPrePay pay;
	//团体数据对象
	private HRMCompany company;
	//票据对象
	private BilInvoice bilInvoice;
	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		Object obj=this.getParameter();
		if(obj!=null){
			companyCode=(String)obj;
		}
		initComponent();
		initData();
	}
	/**
	 * 初始化控件
	 */
	public void initComponent(){
		table=(TTable)this.getComponent("TABLE");
		table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onTabValueChanged");
//		comCombo=(TComboBox)this.getComponent("COMPANY_DESC");
		format=(TTextFormat)this.getComponent("COMPANY_DESC");
		TParm companyComboParm=HRMCompanyTool.getInstance().getCompanyComboParm();
//		System.out.println("===companyComboParm===="+companyComboParm);
		
		if(companyComboParm.getErrCode()<0){
			this.messageBox_("取得套餐数据失败");
			return;
		}
		TParm parm=new TParm();
		int count =companyComboParm.getCount();
		for(int i=0;i<count;i++){
			parm.addData("COMPANY_CODE", companyComboParm.getValue("ID",i));
			parm.addData("COMPANY_DESC", companyComboParm.getValue("NAME",i));	
			parm.addData("PY1", companyComboParm.getValue("PY1",i));
		}
		

		parm.addData("SYSTEM", "COLUMNS","COMPANY_CODE");
		parm.addData("SYSTEM", "COLUMNS","COMPANY_DESC");
		parm.addData("SYSTEM", "COLUMNS","PY1");
		
		parm.setCount(count);
		
		format.setPopupMenuData(parm);		
		
		TComboBox com=new TComboBox();

		
		com.setParmMap("id:ID;name:NAME");
		com.setParmValue(companyComboParm);
		com.setTableShowList("name");
		com.setShowID(false);
		com.setShowName(true);
		table.addItem("COMPANY", com);
	}
	/**
	 * 初始化数据
	 */
	public void initData(){
		pay=new HRMPrePay();
		pay.onQuery();
		company=new HRMCompany();
		company.onQuery();
		if(!StringUtil.isNullString(companyCode)){
			pay.filt(companyCode);
			company.filt(companyCode);
		}
		table.setDataStore(pay);
		int row=pay.insertRow();
		pay.setActive(row,false);
		table.setDSValue();
		
		bilInvoice=new BilInvoice("OPB");
		this.setValue("RECEIPT_NO", bilInvoice.getUpdateNo());
		if(bilInvoice.getUpdateNo().equalsIgnoreCase(bilInvoice.getEndInvno())){
			this.messageBox_("无可打印票据");
			return;
		}
		if(StringTool.addString(bilInvoice.getUpdateNo()).equalsIgnoreCase(bilInvoice.getEndInvno())){
			this.messageBox_("仅剩余一张票据");
		}
	}
	/**
	 * TABLE单击事件，显示团体信息
	 */
	public void onTableClicked(){
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		String companyCode=pay.getItemString(row, "COMPANY_CODE");
		if(StringUtil.isNullString(companyCode)){
			return;
		}
		if(!company.filt(companyCode)){
			return ;
		}
		this.setValue("TEL", company.getItemString(0, "TEL"));
		this.setValue("CONTACTS_NAME", company.getItemString(0, "CONTACTS_NAME"));
		this.setValue("CONTACTS_TEL", company.getItemString(0, "CONTACTS_TEL"));
		this.setValue("ALL_PAY", pay.getAllPay(companyCode));
		this.setValue("SUM_PAY", pay.getSumPay(companyCode));
	}
	/**
	 * TABLE值改变事件
	 * @param tNode
	 * @return
	 */
	public boolean onTabValueChanged(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=table.getParmMap(column);
		table.acceptText();
		if("PRE_AMT".equalsIgnoreCase(colName)){
			//this.messageBox("==PRE_AMT=="+tNode.getValue());
//			System.out.println();
			double value=(Double)tNode.getValue();
			double oldValue=(Double)tNode.getOldValue();
			if(value<=0||value==oldValue){
				return true;
			}
			if(StringUtil.isNullString(pay.getItemString(row, "RECEIPT_NO"))){
				pay.insertRow("",value);
				table.setDSValue();
				return false;
			}
		}
		//COMPANY_CODE;CHARGE_DATE;RECEIPT_NO;TRANSACT_TYPE;PAY_TYPE;PRE_AMT;CHECK_NO;CASHIER_CODE;REFUND_FLG;REFUND_DATE;REFUND_CODE
		if("COMPANY_CODE".equalsIgnoreCase(colName)){
			Timestamp now=pay.getDBTime();
			pay.setItem(row, "CHARGE_DATE", now);
			pay.setItem(row, "RECEIPT_NO",bilInvoice.getUpdateNo());
			pay.setItem(row, "OPT_USER", Operator.getID());
			pay.setItem(row, "OPT_TERM", Operator.getIP());
			pay.setItem(row, "OPT_DATE", now);
			pay.setActive(row,true);
			return false;
		}
		if("TRANSACT_TYPE".equalsIgnoreCase(colName)){
			// System.out.println("before ");
			//pay.showDebug();
			pay.setItem(row, "TRANSACT_TYPE", tNode.getValue());
			// System.out.println("pay");
			//pay.showDebug();
			return false;
		}
		return false;
	}
	/**
	 * 团体代码查询事件
	 */
	public void onComCode(){
		String companyCode=this.getValueString("COMPANY_DESC");
		company.filt(companyCode);
		TParm companyParm=company.getRowParm(0);
		this.setValueForParm("TEL;CONTACTS_NAME;CONTACTS_TEL", companyParm);
		pay.onQuery(companyCode);
		int row=pay.insertRow(companyCode);
		pay.setItem(row, "RECEIPT_NO",bilInvoice.getUpdateNo());
		pay.setActive(row,true);
		table.setDataStore(pay);
		table.setDSValue();
		this.setValue("ALL_PAY", pay.getAllPay(companyCode));
		this.setValue("SUM_PAY", pay.getSumPay(companyCode));
	}
	/**
	 * 保存事件
	 */
	public void onCharge(){
		if(pay==null){
			return;
		}
		int row=pay.rowCount()-1;
		if(row<0){
			return;
		}
		if(StringUtil.isNullString(pay.getItemString(row, "COMPANY_CODE"))){
			this.messageBox_("团体信息不能为空");
			return;
		}
		if(StringUtil.isNullString(pay.getItemString(row, "RECEIPT_NO"))){
			this.messageBox_("票据号不能为空");
			return;
		}
		if(StringUtil.isNullString(pay.getItemString(row, "TRANSACT_TYPE"))){
			this.messageBox_("操作类型不能为空");
			return;
		}
		TParm result=pay.onSave();
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
		}else{
			this.messageBox("P0001");
		}
		
	}
	/**
	 * 清空事件
	 */
	public void onClear(){
		this.clearValue("COMPANY_DESC;ALL_PAY;TEL;CONTACTS_NAME;CONTACTS_TEL;PRE_AMT;PAY_TYPE;CHECK_NO");
	}
	/**
	 *   退费操作
	 */
	public void onRefund(){
		this.messageBox_("开发中");
		//清空   业务类型02     退费
		
		//退费日期
		
		//退费人员
	}
}
