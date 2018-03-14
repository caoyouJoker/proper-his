package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.bil.BILSysParmTool;
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
 * <p>Title:科室费用统计报表
 *
 * <p>Description: 科室费用统计报表
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVDeptFeePrintControl extends TControl{
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
	  TParm parm = INVsettlementTool.getInstance().queryDeptFee(getSearchParm());
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			this.onClear() ;
			return ;
		}
		double money = 0.00 ;
		for(int i=0;i<parm.getCount();i++){
			money +=parm.getDouble("AMT", i) ;
		}
		parm.addData("ORG_CODE", "") ;
		parm.addData("ORG_DESC", "合计") ;
		parm.addData("AMT", StringTool.round(money,2)) ;
		parm.setCount(parm.getCount("ORG_CODE")) ;
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
 			searchParm.setData("EXE_DEPT_CODE", this.getValueString("ORG_CODE")) ;
 		return searchParm;
 	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("ORG_CODE") ;
		table.removeRowAll();
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
			result.addData("SEQ", i+1); //赋值 
			result.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i)); 
			result.addData("AMT", tableParm.getDouble("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "SEQ");//排序
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		result.addData("SYSTEM", "COLUMNS", "AMT");
//		System.out.println("result====="+result);
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//制表时间
		printParm.setData("TITLE", "TEXT","物资部门费用统计报表") ;
		printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDeptFeePrint.jhw",
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
        ExportExcelUtil.getInstance().exportExcel(table, "科室费用统计报表");
    }	
}
