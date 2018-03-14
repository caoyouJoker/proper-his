package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
/**
 * <p> Title: 健康检查个人项项目费用查询 </p>
 *
 * <p> Description: 健康检查个人项项目费用查询  </p>
 *
 * <p> Copyright: javahis 20120803 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class HRMCheckPersonFeeControl  extends TControl{
	TParm DATE ;
	/**
	 * 初始化
	 */
	public  void  onInit(){
		super.onInit() ;		
	}
	/**
	 * 查询
	 */
	public void onQuery (){
		if(this.getValueString("MR_NO").length() == 0){
			this.messageBox("请输入病案号") ;
			return ;
		}
		String mrno = this.getValueString("MR_NO");

		String sql = "SELECT  A.ORDER_DESC,A.SPECIFICATION,B.UNIT_CHN_DESC,A.OWN_PRICE,A.DISPENSE_QTY, " +
				     "A.OWN_AMT  " +
				     "FROM  HRM_ORDER A,SYS_UNIT B " +
				     "WHERE A.DISPENSE_UNIT= B.UNIT_CODE " +
				     "AND  MR_NO  LIKE '%"+mrno+"%'" ;
	  
		TParm  sqlparm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(sqlparm.getCount()<0){
			this.messageBox("查无数据") ;
			this.onClear() ;
			return ;
		}
		
		double total = 0 ;
		for(int i = 0 ,b=sqlparm.getCount(); i < b ; i++){
			total += sqlparm.getDouble("OWN_PRICE",i);
		}
		total = StringTool.round(total, 2);
		
		String namesql = "SELECT C.COMPANY_DESC,D.PAT_NAME,D.MR_NO  FROM HRM_COMPANY C,HRM_CONTRACTD D" +
				" WHERE C.COMPANY_CODE = D.COMPANY_CODE " +
				" AND D.MR_NO  LIKE '%"+mrno+"%' " ;

		DATE = new TParm(TJDODBTool.getInstance().select(namesql)) ;
		this.setValue("MR_NO", DATE.getData("MR_NO", 0)) ;
		this.setValue("PERSON_TOT", total);
		TTable table = (TTable)this.getComponent("TABLE") ;
		table.setParmValue(sqlparm) ;
		 
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("MR_NO;PAT_NAME;R_DATE") ;
		TTable table = (TTable)this.getComponent("TABLE") ;
		table.removeRowAll() ;
	}
	/*
	 * 打印
	 */
	public void onPrint(){
		
		TTable table = (TTable) this.getComponent("TABLE") ;
		TParm Parm =  table.getParmValue() ;
		if(Parm.getCount() < 0){
			this.messageBox("无打印数据");
			return ;
		}
		Timestamp p_date = SystemTool.getInstance().getDate() ; 
		String PDATE = StringTool.getString(p_date, "yyyy/MM/dd HH:mm:ss") ;
		double allMoney = 0.00 ;
		TParm  printParm = new TParm() ;
		for(int i = 0; i<table.getRowCount();i++){
			printParm.addData("ORDER_DESC", table.getItemData(i, "ORDER_DESC"));
			printParm.addData("SPECIFICATION", table.getItemData(i, "SPECIFICATION"));
			printParm.addData("UNIT_CHN_DESC", table.getItemData(i, "UNIT_CHN_DESC"));
			printParm.addData("OWN_PRICE", table.getItemData(i, "OWN_PRICE"));
			printParm.addData("DISPENSE_QTY", table.getItemData(i, "DISPENSE_QTY"));
			printParm.addData("OWN_AMT", table.getItemData(i, "OWN_AMT"));
			allMoney +=table.getItemDouble(i, "OWN_AMT") ;
		}
		allMoney = StringTool.round(allMoney, 2) ;
		printParm.setCount(table.getRowCount());
		printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		printParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		printParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		printParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
		printParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		TParm printdate = new TParm() ;
		printdate.setData("TABLE", printParm.getData()) ;
		printdate.setData("DATE","TEXT",PDATE);
		printdate.setData("USER","TEXT",Operator.getName());
		printdate.setData("MONEY", "TEXT", allMoney);
		printdate.setData("NAME", "TEXT", DATE.getValue("PAT_NAME", 0));
		printdate.setData("COMPANY", "TEXT", DATE.getValue("COMPANY_DESC", 0));
		printdate.setData("HOSP","TEXT",Operator.getHospitalCHNFullName()+"检查费用清单");//add by wanglong 20130730
		this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMCheckFee.jhw",printdate);
	}
	/**
	 * 回車事件
	 */ 
	public   void  onEnter(){
		this.onQuery() ;
	}
}

