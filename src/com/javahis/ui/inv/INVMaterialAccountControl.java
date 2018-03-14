package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVMaterialAccountControl extends TControl{
	private TTable table ;
	private String date  ; //统计区间
	
	/**
	 * 初始化
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
//callFunction("UI|PRINT|setEnabled", false);至灰
//	String mrNo = PatTool.getInstance().checkMrno//(this.getValueString("MR_NO")) ;病案号回车补零
	}
	/**
	 * 初始化界面
	 */
	public void onInitPage(){  
		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("S_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //开始时间
		this.setValue("E_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //结束时间
		table = (TTable)this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		if(this.check()){
			return ;
		}
		String sql = "" ;
		System.out.println("sql========="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(parm.getErrCode()<0){
			this.messageBox("查无数据") ;
			return  ;
		}
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			return ;
		}
		table.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("S_DATE"),
		"yyyy/MM/dd ")
		+ " 至 "
		+ StringTool.getString((Timestamp) this.getValue("E_DATE"),
				"yyyy/MM/dd ");

	}
	/**
	 * 校验
	 * @return
	 */
	public boolean check(){
		if(this.getValueString("")==null || this.getValueString("").length()<0){
			this.messageBox("") ;
			return false ;
		}
		return true ;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("") ;
		table.removeAll() ;
	}
	
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("")<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount("");i++){
			result.addData("", tableParm.getValue("", i)); //赋值 
			result.addData("", tableParm.getValue("", i)); 
			result.addData("", tableParm.getValue("", i)); 
		}
		result.setCount(tableParm.getCount("")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "");//排序
		result.addData("SYSTEM", "COLUMNS", "");
		result.addData("SYSTEM", "COLUMNS", "");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//制表时间
		printParm.setData("TITLE", "TEXT","表名") ;
		printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILItemFeeDetail.jhw",
				printParm);
	}
	
	
	 /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount("") <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "");
    }	
}
