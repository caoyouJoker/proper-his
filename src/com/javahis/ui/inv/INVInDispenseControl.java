package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:物资入库单报表
 *
 * <p>Description: 物资入库单报表
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVInDispenseControl extends TControl{
	private TTable table ;
	private String date  ; //统计区间     
	
	/**
	 * 初始化
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}
	/**
	 * 初始化界面
	 */
	public void onInitPage(){  
		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("START_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //结束时间
		table = (TTable)this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		TParm parm =INVsettlementTool.getInstance().queryInDispense(getSearchParm()) ;
		System.out.println("parm======"+parm);
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			this.onClear();
			return ;
		}
		double money = 0.00 ;
		for(int i=0;i<parm.getCount();i++){
			money +=parm.getDouble("AMT", i) ;
		}
		parm.addData("ORG_DESC", "合计") ;
		parm.addData("INV_CODE", "") ;
		parm.addData("INV_CHN_DESC", "") ;
		parm.addData("DESCRIPTION", "") ;
		parm.addData("QTY", "") ;
		parm.addData("UNIT_CHN_DESC", "") ;
		parm.addData("COST_PRICE", "") ;
		parm.addData("AMT", StringTool.round(money,2)) ;
		parm.setCount(parm.getCount("INV_CODE")) ;
		table.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("START_DATE"),
		"yyyy/MM/dd ")
		+ " 至 "
		+ StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy/MM/dd ");

	}
	  /**
     * 获取查询条件数据
     * @return
     * */
 	private TParm getSearchParm() {
 		TParm searchParm = new TParm();
 		String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "");
 		String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "");
 		searchParm.setData("START_DATE",startDate+"000000"); 
 		searchParm.setData("END_DATE",endDate+"235959");  
 		if(this.getValueString("ORG_CODE").length()>0)
 			searchParm.setData("TO_ORG_CODE", this.getValueString("ORG_CODE")) ;
 		if(this.getValueString("REQUEST_TYPE").length()>0)
 			searchParm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE")) ;
 		return searchParm;
 	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("ORG_CODE;REQUEST_TYPE") ;
		TParm clearParm = new TParm() ;
		table.setParmValue(clearParm) ;
	}
	
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i)); //赋值 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i)); 
			result.addData("QTY", tableParm.getValue("QTY", i)); 
			result.addData("COST_PRICE", tableParm.getValue("COST_PRICE", i)); 
			result.addData("AMT", tableParm.getValue("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//排序
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//制表时间
		String orgDesc = this.getValueString("ORG_CODE").length()>0?tableParm.getValue("ORG_DESC", 0):"全部" ;
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"全部" ;
		printParm.setData("TITLE", "TEXT","专够品入库单") ;
		printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		printParm.setData("ORG_DESC", "TEXT", "部门: " + orgDesc);
		printParm.setData("REQUEST_TYPE", "TEXT", "请领类别: " + requestType);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInDispense.jhw",
				printParm);
	}
	
	
	 /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "物资入库单报表");
    }	
}
