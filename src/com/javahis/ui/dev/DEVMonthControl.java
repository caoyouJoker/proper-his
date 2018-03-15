package com.javahis.ui.dev;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;



import jdo.dev.DevMMTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 设备月结(月结插入数据)</p> 
 * 
 * <p>Description:设备月结(月结插入数据)</p>
 * 
 * <p>Copyright: Copyright (c) 20130808</p>
 * 
 * <p>Company: ProperSoft </p>
 *      
 * @author  fux 
 * 
 * @version 4.0     
 */
public class DEVMonthControl extends TControl{
	String startDate ="";
	String endDate ="";
	  /**
       * 初始化 
       */
	    public void onInit() { 
	       messageBox("初始化 ");
		   initPage(); 
	  } 
	  /**   
	   * 初始化页面
	   */       
	  public void initPage() {
	    	String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
	        this.setValue("DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));
	  }
	  /**
	   * 确定日结 
	   */
	  public void onMonth(){
		    messageBox("确定日结开始");
//			String date = StringTool.getString(TypeTool
//					.getTimestamp(getValue("MONTH_DATE")), "yyyy-MM");
			String date = getValue("MONTH_DATE").toString();
			messageBox("date"+date);  
			String yearAndMonth[] = date.split("-"); 
			this.messageBox("::::"+date.trim().length()); 
		    if(date.trim().length()<=0){
		        this.messageBox("月份不能为空！");
		        return;  
		      }         
            if (messageBox("确定要进行"+date.substring(0,4)+"年"+date.substring(4,6)+"月"+"的月结批次操作吗？","信息",this.YES_NO_OPTION) != 0)
                return; 
            if(this.getValueString("DEV_CLASS") == null && "".equals(this.getValueString("DEV_CLASS"))){
                this.messageBox("请选择至少一个设备类型");
                return;  
              }  
            //日期到天
            //获取某年某月的第一天【年：yearAndMonth[0]  月：yearAndMonth[1]】
		    startDate = this.getFirstDayOfMonth(Integer.parseInt(yearAndMonth[0]),
		    		Integer.parseInt(yearAndMonth[1])-1);
		    endDate = this.getLastDayOfMonth(Integer.parseInt(yearAndMonth[0]),
		    		Integer.parseInt(yearAndMonth[1])-1);
		    System.out.println("startDate=="+startDate);
		    System.out.println("endDate=="+endDate);
		    messageBox("startDate"+startDate);
		    messageBox("endDate"+endDate);  
		    //数字型日期YYYYMM
			int Mdate1 = Integer.parseInt(yearAndMonth[0]);
			int Mdate2 = Integer.parseInt(yearAndMonth[1])-1;
			System.out.println("Mdate1"+Mdate1);
			System.out.println("Mdate2"+Mdate2); 
            int Mdate = 201309;  
		    //结转月份         
		    TParm parm = new TParm();    
		    parm.setData("YYYYMM",Mdate);    
		    parm.setData("START_DATE",startDate); 
		    parm.setData("END_DATE",endDate);  
		    //parm.setData("DEV_CLASS",this.getValueString("DEV_CLASS"));
		    System.out.println("====parm===="+parm);  
		    if(this.saveMonthData(parm)){ 
		        this.messageBox("保存成功！");
		      }else{
		        this.messageBox("保存失败！");
		      }
	  } 
	  /**
	   * 保存月结数据方法
	   * @param parm TParm 
	   * @return boolean
	   */
	  public boolean saveMonthData(TParm parm){
		  messageBox("保存月结数据方法");
	    //查询入库数据(拆分SQL)
		TParm devData = this.getDevMonthRData(parm);
	    if(devData.getErrCode()<0){
	      return false; 
	    } 
	    System.out.println("result :"+devData);
	    return true;
	  }
	   
	  /**
	   * 获得月结报表初始化数据(拆分SQL)
	   * @return TParm 
	   */
	  public TParm getDevMonthRData(TParm parm){ 
	    TParm result = new TParm();  
	    System.out.println("queryDevMMStockCount"+DevMMTool.getInstance().queryDevMMStockCount(parm).getDouble("COUNT"));
	    if(DevMMTool.getInstance().queryDevMMStockCount(parm).getDouble("COUNT") > 0){
	      result.setErr(-1,"有月结数据");     
	      this.messageBox("本月已做完月结！"); 
	      return result; 
	    }  
	    //月结入库数据---------可以
	    TParm devRkData = DevMMTool.getInstance().queryDevMonthRKData(parm);
	    if(devRkData.getErrCode()<0){
	      result.setErr(-1,"月结入库数据-->>>参数错误！");
	      return result;  
	    }
	    System.out.println("入库初始化数据："+devRkData); 
	    //月结出库调入数据  
	    TParm devCKTRData = DevMMTool.getInstance().queryDevMonthCKTRData(parm);
	    if(devCKTRData.getErrCode()<0){
	      result.setErr(-1,"月结出库调入数据-->>>参数错误！");
	      return result;
	    }                           
	    System.out.println("出库调入初始化数据："+devCKTRData);
	    //月结出库调出数据
	    TParm devCKTCData = DevMMTool.getInstance().queryDevMonthCKTCData(parm);
	    if(devCKTCData.getErrCode()<0){
	      result.setErr(-1,"月结出库调出数据-->>>参数错误！");
	      return result; 
	    } 
	    System.out.println("出库调出初始化数据："+devCKTRData);
	    //月结退货数据-------------DEV_REGRESSGOODS
//	    TParm devTHData = DevMMTool.getInstance().queryDevMonthTHData(parm);
//	    if(devTHData.getErrCode()<0){
//	      result.setErr(-1,"月结退货数据-->>>参数错误！");
//	      return result; 
//	    }
//	    System.out.println("退货初始化数据："+devTHData);
//	    //损耗初始化---------------DEV_WASTE
//	    TParm devSHData = DevMMTool.getInstance().queryDevMonthSHData(parm);
//	    if (devSHData.getErrCode() < 0) {
//	      result.setErr( -1, "损耗初始化-->>>参数错误！");
//	      return result;
//	    }  
//	    System.out.println("损耗初始化数据：" + devSHData);
//	    //盘点初始化 -----------DEV_QTYCHECK
//	    TParm devPDData = DevMMTool.getInstance().queryDevMonthPDData(parm);
//	    if (devPDData.getErrCode() < 0) {
//	      result.setErr( -1, "盘点初始化-->>>参数错误！");
//	      return result;
//	    }
//	    System.out.println("盘点初始化数据：" + devPDData);
	    
	    TParm MMParm = new TParm(); 
	    //入库 
	    MMParm.setData("INWAREHOUSE",devRkData);
	    //调入 
	    MMParm.setData("GIFTIN",devCKTRData);      
	    //调出  
	    MMParm.setData("GIFTOUT",devCKTCData); 
	    System.out.println("MMParm"+MMParm);
//	    //退货 
//	    MMParm.setData("REGRESSGOODS",devTHData);
//	    //损耗
//	    MMParm.setData("WASTE",devSHData);
//	    //盘点 
//	    MMParm.setData("CHECKMODI",devPDData); 
	    //根据时间段(月初到月末)查询出stockd的数据量   
	    TParm stockDParm = new TParm(); 
	    stockDParm = ParmStockD();
	    System.out.println("stockDParm"+stockDParm); 
	    for(int i = 0;i<stockDParm.getCount("DEV_CODE"); i++){ 
	    	 //for循环插入数据    
	    	 System.out.println("i"+i);  
		     TParm  MMresult = DevMMTool.getInstance().insertMMStock(MMParm);
		     System.out.println("MMresult"+MMresult);  
	    }  
	    return result;  
	  } 
	  /**
	   * 查询出STOCKD的数据量    
	   * @return TParm  
	   */    
	  public TParm ParmStockD(){ 
		String sql = " SELECT * FROM DEV_STOCKD " +  
				     " WHERE INWAREHOUSE_DATE BETWEEN " +
				     " TO_DATE('"+startDate+"','YYYYMMDD') " +
				     " AND TO_DATE('"+endDate+"','YYYYMMDD') "; 
		System.out.println("sql=="+sql); 
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;	     
	  }  
	  /**  
	   * 获得月结启日
	   * @return String
	   */ 
	  public String getMonthStartDate(String monthData){  
	    if(monthData.trim().length()<=0){
	      return "";
	    }
	    if (monthData.substring(4, 6).equals("01")) {
	      return this.valiDataMonthDate(monthData);
	    }
	    //09 -01 = 08
	    int month = Integer.parseInt(monthData.substring(4, 6)) - 1;
	    String m = String.valueOf(month);
	    if (m.length() == 1) {
	      m = "0" + m;
	    }
	    String result = monthData.substring(0, 4) + m;
	    return result;
	  } 
	
	  /**
	   * 验证月结启日
	   * @param monthData String
	   * @return String
	   */
	  public String valiDataMonthDate(String monthData){
	    if(monthData.trim().length()<=0){
	      return "";
	    }
	    int year = Integer.parseInt(monthData.substring(0,4))-1;
	    String result = String.valueOf(year)+"12";
	    return result;
	  }
	  /**
	   * 获得月结讫日 
	   * @return String
	   */ 
	  public String getMonthEndDate(String monthData){
	    return monthData;
	  }
	  /**
	   * 编辑统计月份
	   * @param dateStr String
	   * @return String
	   */
	  public String getDataValue(String dateStr){ 
	    if(dateStr.trim().length()<=0){
	      return "";
	    }
	    String[] str = dateStr.split("\\/");
	    String strDate = "";
	    for(int i=0;i<str.length;i++){
	      strDate+=str[i];
	    }
	    return strDate;
	  }
	  /**
	   * 获取某年某月的最后一天
	   * @param dateStr String
	   * @return String
	   */
	    public static String getLastDayOfMonth(int year, int month) {     
	        Calendar cal = Calendar.getInstance();     
	        cal.set(Calendar.YEAR, year);     
	        cal.set(Calendar.MONTH, month);     
	        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE));  
	       return  new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
	    }    
		  /**
		   * 获取某年某月的第一天
		   * @param dateStr String
		   * @return String
		   */
	    public static String getFirstDayOfMonth(int year, int month) {     
	        Calendar cal = Calendar.getInstance();     
	        cal.set(Calendar.YEAR, year);     
	        cal.set(Calendar.MONTH, month);  
	        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));  
	       return   new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
	    }   
}
