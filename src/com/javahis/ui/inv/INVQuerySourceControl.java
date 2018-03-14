package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 物联网物资溯源报表</p>
 *
 * <p>Description: 物联网物资溯源报表</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author duzhw
 * @version 1.0
 */
public class INVQuerySourceControl extends TControl {
	public INVQuerySourceControl(){}
	private static TTable Table_IN;
	private static TTable Table_OUT;
	
	/**
	 * 初始化
	 */
	public void init(){
		super.init();
		initPage();

	}
	/**
     * 初始画面数据
     */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		String currday = getFirstDayOfMonth(1);//本月1号
		// 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",
        		currday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        //获得TABLE对象
        Table_IN = (TTable) getComponent("Table_IN");
        Table_OUT = (TTable) getComponent("Table_OUT");
        TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	
	/**
	 * 得到TextField对象
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
	    TParm parm = (TParm) obj;
	    if (parm == null) {
	            return;
	    }
	    String order_code = parm.getValue("INV_CODE");
	      if (!StringUtil.isNullString(order_code))
	          getTextField("INV_CODE").setValue(order_code);
	      String order_desc = parm.getValue("INV_CHN_DESC");
	      if (!StringUtil.isNullString(order_desc))
	            getTextField("INV_DESC").setValue(order_desc);
	}
	
	 /**
     * 查询
     */
	public void onQuery(){
		//入库明细查询sql
		String sqlIn = "SELECT A.VERIFYIN_NO AS DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC," +
				" B.DESCRIPTION,A.VALID_DATE, A.IN_QTY AS  QTY " +
				" FROM INV_VERIFYIND A, INV_BASE B " +
				" WHERE A.INV_CODE = B.INV_CODE ";	
		//出库明细查询sql
		String sqlOut = "SELECT A.DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,E.ORG_DESC AS FROM_ORG_DESC, " +
				" F.ORG_DESC AS TO_ORG_DESC,D.DISPENSE_DATE AS VALID_DATE, A.QTY " +
				" FROM INV_DISPENSED A, INV_BASE B, INV_DISPENSEM D, INV_ORG E, INV_ORG F " +
				" WHERE A.INV_CODE = B.INV_CODE AND   A.DISPENSE_NO = D.DISPENSE_NO " +
				" AND  D.FROM_ORG_CODE = E.ORG_CODE AND D.TO_ORG_CODE = F.ORG_CODE AND A.IO_FLG ='2' ";		
		// 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "START_DATE")), "yyyyMMdd")+" 00:00:00";
            String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "END_DATE")), "yyyyMMdd")+" 23:59:59";
            //入库明细查询时间拼sql
            sqlIn += " AND A.OPT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
			+ "','yyyymmdd hh24:mi:ss')";
            //出库明细查询时间拼sql
            sqlOut += " AND D.DISPENSE_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
			+ "','yyyymmdd hh24:mi:ss')";
            
        }else{
        	this.messageBox("日期 不能为空");
        	return;
        }
       //物资
       if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
        	sqlIn +=" AND A.INV_CODE = '" + invCode + "'"; 
        	sqlOut +=" AND A.INV_CODE = '" + invCode + "'"; 
       }else {
        	sqlIn +=" AND A.INV_CODE LIKE '08%'";
        	sqlOut +=" AND A.INV_CODE LIKE '08%'";
	   }
       sqlIn += " GROUP BY A.INV_CODE,A.VERIFYIN_NO,B.INV_CHN_DESC,B.DESCRIPTION,A.VALID_DATE,A.IN_QTY ";
       sqlOut += " GROUP BY A.INV_CODE,A.DISPENSE_NO,B.INV_CHN_DESC,B.DESCRIPTION,E.ORG_DESC,F.ORG_DESC,D.DISPENSE_DATE,A.QTY ";
       //System.out.println("入库sql="+sqlIn);
       //System.out.println("出库sql="+sqlOut);
       TParm selInParm = new TParm(TJDODBTool.getInstance().select(sqlIn));
       TParm selOutParm = new TParm(TJDODBTool.getInstance().select(sqlOut));
       if(selInParm.getCount()<0){
        	this.messageBox("入库信息没有要查询的数据");
        }
       if(selOutParm.getCount()<0){
    	   this.messageBox("出库信息没有要查询的数据");
       }
       //System.out.println("入库：selInParm="+selInParm);
       //System.out.println("出库：selOutParm="+selOutParm);
        //加载table上的数据
        Table_IN.setParmValue(selInParm);
        Table_OUT.setParmValue(selOutParm);
	}
	    
	    
	    
	/**
     * 打印方法
     */
    public void onPrint() {
    	TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
	    if(tab.getSelectedIndex() == 0) { //页签一：入库明细
	    	onPrint1();
		}
		if(tab.getSelectedIndex() == 1) { //页签二：出库明细
			onPrint2();
		}
    	
    }
	//打印入库明细
    public void onPrint1() {
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//物资编码
		TParm tableParm = Table_IN.getParmValue() ;
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
    public void onPrint2() {
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//物资编码
		TParm tableParm = Table_OUT.getParmValue() ;
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
     * 清空方法
     */
	
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		String currday = getFirstDayOfMonth(1);//本月1号
		// 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",
        		currday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.setValue("INV_CODE", "");
		
	}    
	    
	/**
	  * 导出excel
	  * */
	 public void onExcel(){
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
	    if(tab.getSelectedIndex() == 0) { //页签一：入库明细
	    	onExcel1();
		}
		if(tab.getSelectedIndex() == 1) { //页签二：出库明细
			onExcel2();
		}
	 }   
	 //导出入库明细
	 public void onExcel1(){
		 TTable table = getTable("Table_IN");
	    	if(table.getRowCount() > 0){
	    		ExportExcelUtil.getInstance().exportExcel(table, "入库明细表统计");
	    	}else {
	         this.messageBox("没有汇出数据");
	         return;
	     }
		 
	 } 
	 //导出出库明细
	 public void onExcel2(){
		 TTable table = getTable("Table_OUT");
	    	if(table.getRowCount() > 0){
	    		ExportExcelUtil.getInstance().exportExcel(table, "出库明细表统计");
	    	}else {
	         this.messageBox("没有汇出数据");
	         return;
	     }
		 
	 }   
	// 获取当月第一天
	public static String getFirstDayOfMonth(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// 设为当前月的n号
		str = sdf.format(lastDate.getTime());
		return str;
	}  
	    
	    
	 /**
	  * 得到Table对象
	  *
	  * @param tagName
	  *            元素TAG名称
	  * @return
	  */
	 private TTable getTable(String tagName) {
	     return (TTable) getComponent(tagName);
	 }

}
