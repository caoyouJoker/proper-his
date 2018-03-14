package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 门诊收费交款界面</p>
 *
 * <p>Description:门诊收费交款界面</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim
 * @version 1.0
 */
public class EKTTredeReportControl extends TControl {
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	setValue("REGION_CODE", Operator.getRegion());
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("SEARCH_DATE", today);
    }	
	
    /**
     * 
     * 查询方法.
     */
	public void onQuery()
	{
		String sql = this.getSQL() ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<=0){
    		this.callFunction("UI|TTABLE|setParmValue",new TParm());
    		messageBox("查无数据");
    		return;
    	}
    	
    	double refound = 0 ;
    	double realExchangeRate = 0 ;
    	double greenBalance = 0 ;
    	for (int i = 0; i < result.getCount(); i++) {
    		double amt = result.getDouble("BUSINESS_AMT", i) ;
    		if(amt < 0)
    		{//统计退卡款.
    			refound += amt ;
    		}else
    		{//统计实交金额.
    			realExchangeRate += amt ;
    		}
    		//统计院内支付.
    		greenBalance += result.getDouble("GREEN_BALANCE", i) ;
		}
    	refound = (refound<0) ? -1*refound : refound;
    	//统计售卡款.
    	double vending = refound + realExchangeRate ;
    	
    	TParm parm = new TParm();
    	parm.addData("PROJECT", "售卡款") ;
    	parm.addData("PROJECT", "退卡款") ;
    	parm.addData("PROJECT", "实交金额") ;
    	parm.addData("CASH", vending) ;
    	parm.addData("CASH", refound) ;
    	parm.addData("CASH", realExchangeRate) ;
    	parm.addData("SUM", vending) ;
    	parm.addData("SUM", refound) ;
    	parm.addData("SUM", realExchangeRate) ;
    	
    	this.callFunction("UI|TTABLE|setParmValue",parm);
	}
	
	/**
	 * 
	 * 打印方法.
	 */
	public void onPrint()
	{
    	TTable table = (TTable) this.getComponent("TTABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		String sql = this.getSQL() ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	if(result.getCount()<=0){
    		this.messageBox("没有打印数据");
    		return;
    	}	
    	double refound = 0.00 ;
    	double realExchangeRate = 0.00 ;
    	double greenBalance = 0.00 ;
    	String beginTredeNo = "" ;
    	String endTredeNo = "" ;
    	for (int i = 0; i < result.getCount(); i++) {
    		if(i==0)
    		{//起始收据号
    			beginTredeNo = result.getValue("BUSINESS_NO",i) ;
    		}
    		if(i== result.getCount()-1)
    		{
    			endTredeNo = result.getValue("BUSINESS_NO",i) ;
    		}
    		
    		double amt = result.getDouble("BUSINESS_AMT", i) ;
    		if(amt < 0)
    		{//统计退卡款.
    			refound += amt ;
    		}else
    		{//统计实交金额.
    			realExchangeRate += amt ;
    		}
    		//统计院内支付.
    		greenBalance += result.getDouble("GREEN_BALANCE", i) ;
		}
    	refound = (refound<0) ? -1*refound : refound;
    	//统计售卡款.
    	double vending = refound + realExchangeRate ;   	
    	//医疗卡收据号.
    	String receiptNo = beginTredeNo+" ~ "+endTredeNo ;
    	DecimalFormat df = new DecimalFormat("##########0.00");

    	TParm data = new TParm();// 打印的数据
    	data.setData("VENDINGCASH","TEXT",df.format(vending)) ;
    	data.setData("REFOUNDCASH","TEXT",df.format(refound)) ;
    	data.setData("REALEXCHANGERATE","TEXT",df.format(realExchangeRate)) ;
    	data.setData("VENDINGCHECK","TEXT", "0.00") ;
    	data.setData("REFOUNDCHECK","TEXT", "0.00") ;
    	data.setData("REALEXCCHECK","TEXT", "0.00") ;
    	data.setData("VENDINGSUM","TEXT",df.format(vending)) ;
    	data.setData("REFOUNDSUM","TEXT",df.format(refound)) ;
    	data.setData("REALEXCSUM","TEXT",df.format(realExchangeRate)) ;
    	data.setData("NUMTOCHINESE","TEXT", StringUtil.getInstance().numberToWord(realExchangeRate)) ;
    	data.setData("RECEIPTNO","TEXT", receiptNo) ;
    	data.setData("GREENBLANCE","TEXT",df.format(greenBalance)) ;
    	String date = SystemTool.getInstance().getDate().toString();
    	data.setData("DATE","TEXT", date.substring(0, 4)+"/"+date.substring(5, 7)+"/"+date.substring(8, 10)) ;
    	
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTredeReport.jhw",data);
	}

	/**
	 * 
	 * 清空方法.
	 */
	public void onClear()
	{
		this.setValue("REGION_CODE", "") ;
		this.setValue("SEARCH_DATE", "") ;
		this.callFunction("UI|TTABLE|setParmValue",new TParm());
	}
	
	/**
	 * 
	 * 构造查询SQL文.
	 */
	private String getSQL()
	{
		String regionCode = this.getValueString("REGION_CODE") ;
		String searchDate = this.getValueString("SEARCH_DATE") ;	
		if (!"".equals(searchDate)){
			searchDate = searchDate.substring(0, 19);
			searchDate = searchDate.substring(0, 4) + searchDate.substring(5, 7) +
			searchDate.substring(8, 10);
		}
//		StringBuilder sBuilder = new StringBuilder("SELECT EKT_TREDE.TREDE_NO,EKT_TREDE.AMT,REG_PATADM.GREEN_BALANCE " +
//				                                   "FROM EKT_TREDE,REG_PATADM " +
//				                                   "WHERE EKT_TREDE.CASE_NO = REG_PATADM.CASE_NO ") ;
		//===zhangp 20120312 start
		StringBuilder sBuilder = new StringBuilder("SELECT A.BUSINESS_NO,A.BUSINESS_AMT,B.GREEN_BALANCE " +
				                                   "FROM EKT_ACCNTDETAIL A,REG_PATADM B  " +
				                                   "WHERE A.CASE_NO = B.CASE_NO ") ;
		if(regionCode!=null && !"".equals(regionCode))
		{
			sBuilder.append("AND B.REGION_CODE = '").append(regionCode).append("' ") ;
		}
		if(searchDate!=null && !"".equals(searchDate))
		{
			sBuilder.append("AND B.REG_DATE BETWEEN TO_DATE('"+searchDate+"000000','YYYYMMDDHH24MISS') AND TO_DATE('"+searchDate+"235959','YYYYMMDDHH24MISS')") ;
		}
		sBuilder.append("AND A.CHARGE_FLG<>6 ") ;
		sBuilder.append("ORDER BY A.BUSINESS_NO ASC") ;
		//===zhangp 201203012 end
		return sBuilder.toString() ;
	}
}
