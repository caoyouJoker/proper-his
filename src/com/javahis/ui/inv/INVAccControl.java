package com.javahis.ui.inv;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
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
public class INVAccControl extends TControl{
	private TTable table ;
	private String date  ; //统计区间     
	private Map<String, String> inPriceMap;
	private Map<String, String> outPriceMap;
	private Map<String, String> inOutQtyMap;
	
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
		// 初始化查询区间
		this.setValue("START_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInitLast(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("END_DATE", rollDay);
		table = (TTable)this.getComponent("TABLE");
		inPriceMap=getInprice();
		outPriceMap=getOutprice();
		
	}

	/**
	 * 得到上个月
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	@SuppressWarnings("deprecation")
	public Timestamp getDateForInitLast(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		String dayCycle = sysParm.getValue("DAY_CYCLE",0);
		int hours = Integer.parseInt( dayCycle.substring(0,2));
		result.setHours(hours);
		int minutes = Integer.parseInt(dayCycle.substring(2,4));
		result.setMinutes(minutes);
		int seconds = Integer.parseInt(dayCycle.substring(4,6));
		result.setSeconds(seconds);
		return result;
	}
	//数字格式化
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");

	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
	private Map<String, String> getInprice(){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select a.inv_code,b.CONTRACT_PRICE from inv_base a left join inv_agent b on a.inv_code=b.inv_code ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i), parm.getValue("CONTRACT_PRICE", i));
		}
		return map;
		
	}
	
	private Map<String, String> getOutprice(){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select a.inv_code,b.OWN_PRICE from inv_base a left join sys_fee b on a.order_code=b.order_code ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i), parm.getValue("CONTRACT_PRICE", i));
		}
		return map;
	}
	
	private Map<String, String> getInOutQty(String s,String e){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select  ORG_CODE  , INV_CODE , sum(DD_IN_QTY)  as inq,  sum(DD_IN_QTY) as outq   from inv_ddstock";
		sql+=" where TRANDATE>'"+s+"' and TRANDATE<='"+e+"'";
		sql+="	group by   ORG_CODE  , INV_CODE     ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("=====1===="+sql);
		System.out.println("=====1===="+parm.getCount("INV_CODE"));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i)+"::"+parm.getValue("ORG_CODE", i), parm.getValue("INQ", i)+"::"+parm.getValue("OUTQ", i));
		}
		return map;
	}
	private Map<String, String> getQty(){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select a.inv_code,a.org_code,a.stock_qty from inv_stockm  a";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i)+"::"+parm.getValue("ORG_CODE", i), parm.getValue("STOCK_QTY", i));
		}
		return map;
	}
	
	
	/**
	 * 查询
	 */
	public void onQuery(){
			this.onClear();
		
		String s=getValueString("START_DATE").substring(0, 10).replaceAll("-", "");
		String e=getValueString("END_DATE").substring(0, 10).replaceAll("-", "");
		inOutQtyMap=getInOutQty(s, e);
		String sql="SELECT a.inv_code,a.stock_qty,b.INV_CHN_DESC,b.DESCRIPTION " +
				" from inv_stockm a " +
				" left join inv_base b on a.inv_code=b.inv_code where a.org_code='"+getValueString("ORG_CODE")+"'";
		
	 
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        TParm mParm=new TParm();   
        System.out.println("=========dddd========"+inOutQtyMap.get("08.04.0023009::0306"));
      System.out.println("11"+selParm.getCount("INV_CODE"));
        for (int i = 0; i < selParm.getCount("INV_CODE"); i++) {
        	String inString=inOutQtyMap.get(selParm.getValue("INV_CODE",i)+"::"+getValueString("ORG_CODE"));
        	String in="0";
        	String out="0";
        	if (inString!=null&&inString.length()>2) {
        		in=inString.split("::")[0];
        		out=inString.split("::")[1];
			}
        	
        	String inprice=inPriceMap.get(selParm.getValue("INV_CODE",i));
        	if (inprice==null) {
        		inprice="0";
			}
        	String outprice=outPriceMap.get(selParm.getValue("INV_CODE",i));
        	if (outprice==null||outprice.length()<1) {
        		outprice="0";
			}
        	mParm.setData("INV_CODE", i, selParm.getValue("INV_CODE",i));
        	mParm.setData("INV_CHN_DESC", i, selParm.getValue("INV_CHN_DESC",i));
        	mParm.setData("DESCRIPTION", i, selParm.getValue("DESCRIPTION",i));
        	mParm.setData("INQTY", i, in);
        	mParm.setData("INPRICE", i, inprice);
        	mParm.setData("INALL", i, new BigDecimal(in).multiply(new BigDecimal(inprice)).toString());
        	mParm.setData("OUTQTY", i, out);
        	mParm.setData("OUTPRICE", i, outprice);
        	mParm.setData("OUTALL", i, new BigDecimal(out).multiply(new BigDecimal(outprice)).toString());
        	
        	
        	mParm.setData("MQTY", i, selParm.getValue("STOCK_QTY",i));
        	mParm.setData("MPRICE", i, inprice);
        	mParm.setData("MALL", i, new BigDecimal(selParm.getValue("STOCK_QTY",i)).multiply(new BigDecimal(inprice)).toString());
        	
        	
        	
        	
        	
		}
        
        //加载table上的数据
	    this.callFunction("UI|TABLE|setParmValue", mParm);

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
 			searchParm.setData("FROM_ORG_CODE", this.getValueString("ORG_CODE")) ;
 		if(this.getValueString("REQUEST_TYPE").length()>0)
 			searchParm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE")) ;
 		return searchParm;
 	}
	/**
	 * 清空
	 */
	public void onClear(){
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
		printParm.setData("TITLE", "TEXT","专够品出库单") ;
		printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		printParm.setData("ORG_DESC", "TEXT", "部门: " + orgDesc);
		printParm.setData("REQUEST_TYPE", "TEXT", "请领类别: " + requestType);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVOutDispense.jhw",
				printParm);
	}
	
	
	 /**
     * 汇出Excel
     */
    public void onExport() {
       
        ExportExcelUtil.getInstance().exportExcel(table, "物资出库单报表");
    }	
}
