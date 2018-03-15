package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.udd.UDDOpePreventionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 抗菌药品手术预防药品使用报表</p>
 *
 * <p>Description:抗菌药品手术预防药品使用报表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author 2014.02.12
 * @version 1.0
 */
public class UDDOpePreventionControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * 初始化
   */
	public void onInit() {
		callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");
		table=this.getTable("TABLE");
		this.onClear();
		 initPage();
		
   }
	 /**
	 * 查询
	 */
	public void onQuery() {
		TParm parm=new TParm();
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
		//科室
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		}
		//医生
		if(this.getValueString("VS_DR_CODE").length()>0){
			parm.setData("VS_DR_CODE",this.getValueString("VS_DR_CODE"));
		}
		
		TParm result=UDDOpePreventionTool.getInstance().getselectInum(parm);
		
		for(int i=0;i<result.getCount();i++){
			String time=dateDiff(result.getValue("ORDER_DATE", i).replaceAll("/","-"),result.getValue("OP_DATE", i).replaceAll("/","-"),"yyyy-MM-dd HH:mm:ss");
			this.messageBox("xxxx"+time);
			result.addData("DATE_DEFFEREBCE", time);
		}
		if(result.getCount()<0){
			this.messageBox("没有查询数据");
			table.removeRowAll();
			return;
		}
		
		if(result.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * 初始化
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1天
		// 初始化查询区间
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "联合应用比例统计");
    }
    
    /**
	 * 清空内容
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;VS_DR_CODE";
		table.removeRowAll();
		clearValue(clearString);
		initPage();
	}
	/**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			return;
		}
		TParm tparm= new TParm();
        tparm = table.getParmValue().getRow(row);
        this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
        this.setValue("SESSION_CODE", tparm.getValue("STATION_CODE"));
        this.setValue("VS_DR_CODE", tparm.getValue("VS_DR_CODE"));
			    
		  
	}
	
	public String dateDiff(String startTime, String endTime, String format) {
		//按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数
		long diff;
		String datatime="";
		try {
		//获得两个时间的毫秒时间差异
		diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		long min = diff%nd%nh/nm;//计算差多少分钟
		long sec = diff%nd%nh%nm/ns;//计算差多少秒
		//输出结果
		if(day!=0){
			datatime=day+"天"+hour+"小时"+min+"分钟"+sec+"秒";
		}else if(hour!=0){
			datatime= hour+"小时"+min+"分钟"+sec+"秒";
		}else if(min!=0){
			datatime=min+"分钟"+sec+"秒";
		}else{
			datatime=sec+"秒";
		}
		 return datatime;
		} catch (Exception e) {
		e.printStackTrace();
		}
		return datatime;
	}
	
}