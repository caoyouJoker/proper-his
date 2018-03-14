package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:物资出库单报表
 *
 * <p>Description: 物资出库单报表
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVOutDispenseControl extends TControl{
	private TTable table_in_all ;
	private TTable table_in_detail ;
	private TTable table_out_detail ;
//	private TTable table ;
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
		table_in_all = (TTable)this.getComponent("Table_IN_ALL");
		table_in_detail = (TTable)this.getComponent("Table_IN_DETAIL");
		table_out_detail = (TTable)this.getComponent("Table_OUT_DETAIL");
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		//TParm parm =INVsettlementTool.getInstance().queryOutDispense(getSearchParm()) ;
		TParm parm =INVsettlementTool.getInstance().queryInAllDispense(getSearchParm());
		//System.out.println("parm====="+parm);
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			this.onClear();
			return ;
		}
		//double money = 0.00 ;
		int inQty = 0;
		for(int i=0;i<parm.getCount();i++){
			inQty += parm.getInt("IN_QTY", i);
		}
		parm.addData("INV_CODE", "合计") ;
		parm.addData("INV_CHN_DESC", "") ;
		parm.addData("DESCRIPTION", "") ;
		parm.addData("IN_QTY", inQty) ;

		table_in_all.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("START_DATE"),
		"yyyy/MM/dd ")
		+ " 至 "
		+ StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy/MM/dd ");

	}
	 /**
     * 操作者信息表格(TABLE)单击事件
     */
    public void onTableClicked() {
    	
    	// 得到被选中行的数据
        //TParm checkparm = table_in_all.getDataStore().getRowParm(table_in_all.getSelectedRow());
        int row = table_in_all.getTable().getTable().getSelectedRow();
        if((row+1) != table_in_all.getRowCount()){
        	//获取对应INV_CODE
            String invCode  = table_in_all.getItemString(row, "INV_CODE");
            this.setValue("INV_CODE", invCode);
            //查询对应入库明细到第二页签中
            TParm inParm = INVsettlementTool.getInstance().queryInDetailDispense(getInOutSearchParm(invCode));
            table_in_detail.setParmValue(inParm) ;
            //查询对应出库明细到第三页签中
            TParm outParm = INVsettlementTool.getInstance().queryOutDetailDispense(getInOutSearchParm(invCode));
            table_out_detail.setParmValue(outParm) ;
        }else{
        	//System.out.println("选中的行是合计行！");
        }
        
        
    }
    /**
     * 页签单击事件
     */
    public void onTablePaneClicked(){
    	TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
    	if(tab.getSelectedIndex() == 0) { //页签一：验证入库汇总
    		this.callFunction("UI|query|setEnabled", true);
    		this.callFunction("UI|export|setEnabled", true);
			}
		if(tab.getSelectedIndex() == 1) { //页签二：入库明细
			this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
			}
		if(tab.getSelectedIndex() == 2) { //页签三：出库明细
			this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
		}
    	
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
 		return searchParm;
 	}
 	  /**
     * 获取出入库查询条件数据
     * @return
     * */
 	private TParm getInOutSearchParm(String invCode) {
 		TParm inOutSearchParm = new TParm();
 		String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "");
 		String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "");
 		inOutSearchParm.setData("START_DATE",startDate+"000000"); 
 		inOutSearchParm.setData("END_DATE",endDate+"235959"); 
 		inOutSearchParm.setData("INV_CODE", invCode);
 		return inOutSearchParm;
 	}
	/**
	 * 清空
	 */
	public void onClear(){
//		this.clearValue("ORG_CODE;REQUEST_TYPE") ;
//		TParm clearParm = new TParm() ;
//		table.setParmValue(clearParm) ;
	}
	
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
    	if(tab.getSelectedIndex() == 0) { //页签一：验证入库汇总
    		onPrint1();
			}
		if(tab.getSelectedIndex() == 1) { //页签二：入库明细
			onPrint2();
			}
		if(tab.getSelectedIndex() == 2) { //页签三：出库明细
			onPrint3();
		}

	}
	//打印验证入库汇总
	public void onPrint1(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		TParm tableParm = table_in_all.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//打印数据
		TParm data = new TParm();
		//表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"验收入库汇总报表");
		data.setData("S_DATE", "TEXT", "开始时间：" + startDate);
		data.setData("E_DATE", "TEXT", "结束时间：" + endDate);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //赋值 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("IN_QTY", tableParm.getValue("IN_QTY", i)); 
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "IN_QTY");

		data.setData("TABLE", result.getData()) ; 
		//表尾数据
		data.setData("OPT_DATE", "TEXT", "制作时间："+now);
		data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
		
		//out日志输出data信息-用于调试
		System.out.println("data=="+data);
		
		//调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInAllDispense.jhw", data);
		
	}
	
	//打印入库明细
	public void onPrint2(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//物资编码
		TParm tableParm = table_in_detail.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//打印数据
		TParm data = new TParm();
		//表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"入库明细报表");
		data.setData("S_DATE", "TEXT", "开始时间：" + startDate);
		data.setData("E_DATE", "TEXT", "结束时间：" + endDate);
		data.setData("INV_CODE", "TEXT", "物资编码：" + invCode);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //赋值 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i)); 
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));
			result.addData("QTY", tableParm.getValue("QTY", i));
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		result.addData("SYSTEM", "COLUMNS", "QTY");

		data.setData("TABLE", result.getData()) ; 
		//表尾数据
		data.setData("OPT_DATE", "TEXT", "制作时间："+now);
		data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
		
		//out日志输出data信息-用于调试
		System.out.println("data=="+data);
		
		//调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInDetailDispense.jhw", data);
	}
	
	//打印出库明细
	public void onPrint3(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//物资编码
		TParm tableParm = table_out_detail.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//打印数据
		TParm data = new TParm();
		//表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"出库明细报表");
		data.setData("S_DATE", "TEXT", "开始时间：" + startDate);
		data.setData("E_DATE", "TEXT", "结束时间：" + endDate);
		data.setData("INV_CODE", "TEXT", "物资编码：" + invCode);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //赋值 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i)); 
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));
			result.addData("QTY", tableParm.getValue("QTY", i));
			result.addData("FROM_ORG_DESC", tableParm.getValue("FROM_ORG_DESC", i));
			result.addData("TO_ORG_DESC", tableParm.getValue("TO_ORG_DESC", i));
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "FROM_ORG_DESC");
		result.addData("SYSTEM", "COLUMNS", "TO_ORG_DESC");

		data.setData("TABLE", result.getData()) ; 
		//表尾数据
		data.setData("OPT_DATE", "TEXT", "制作时间："+now);
		data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
		
		//out日志输出data信息-用于调试
		System.out.println("data=="+data);
		
		//调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVOutDetailDispense.jhw", data);
	}
	
	 /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table_in_all.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table_in_all, "物资验收入库汇总报表");
    }	
}
