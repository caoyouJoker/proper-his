package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.ope.OPEToPreventTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 手术预防使用率统计</p>
 *
 * <p>Description:手术预防使用率统计</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.10.28
 * @version 1.0
 */
public class OPEToPreventControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * 初始化
   */
	public void onInit() {
		table=this.getTable("TABLE");
		 initPage();
		
   }
	 /**
	 * 查询
	 */
	public void onQuery() {
		TParm parm=new TParm();
		DecimalFormat dec=new DecimalFormat("##.##%");
		
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
		//科室
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		}
		//病区
		if(this.getValueString("SESSION_CODE").length()>0){
			parm.setData("STATION_CODE",this.getValueString("SESSION_CODE"));
		}
		//医生
		if(this.getValueString("VS_DR_CODE").length()>0){
			parm.setData("VS_DR_CODE",this.getValueString("VS_DR_CODE"));
		}
		//抗菌药限制类型
		if(this.getValueString("PHA_PREVENCODE").length()>0){
			parm.setData("PHA_PREVENCODE",this.getValueString("PHA_PREVENCODE"));
		}
		TParm result=OPEToPreventTool.getInstance().selectdata(parm);
		
		TParm mresult=OPEToPreventTool.getInstance().selectdataM(parm);
		if(result.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有查询数据");
			return;
		}
		double cout=0;
		double totnum=0;
		if(mresult.getCount()>0){
			for(int i=0;i<result.getCount();i++){
				 for(int j=0;j<mresult.getCount();j++){
					 if(result.getValue("REGION_CODE",i).equals(mresult.getValue("REGION_CODE",j)) &&
							  result.getValue("DEPT_CODE",i).equals(mresult.getValue("DEPT_CODE",j)) &&	
							  result.getValue("STATION_CODE",i).equals(mresult.getValue("STATION_CODE",j)) &&
							  result.getValue("VS_DR_CODE",i).equals(mresult.getValue("VS_DR_CODE",j)) 
						   ){
						     result.addData("PHA_PREVENCODE", mresult.getValue("PHA_PREVENCODE",j));
						     result.addData("USER_NUM",mresult.getValue("USER_NUM",j));//联合用药人数
						  
						   }else{
							   result.addData("JOINT_NUM","0");
							   result.addData("PHA_PREVENCODE","");
						   }
				   }
				   totnum=result.getDouble("TOTAL_NUM",i);
				   if(totnum!=0){
				      cout=result.getDouble("USER_NUM",i)/totnum;
				   }
				   result.addData("APPLICATION_SCALE", dec.format(cout)); 
			}
			
			
		  
		}else{
			for(int i=0;i<result.getCount();i++){
				   result.addData("PHA_PREVENCODE","");
				   result.addData("USER_NUM","0");
				   totnum=result.getDouble("TOTAL_NUM",i);
				   if(totnum!=0){
				      cout=result.getDouble("USER_NUM",i)/totnum;
				   }
				    result.addData("APPLICATION_SCALE", dec.format(cout));
			   }
		}
		    table.setParmValue(result);
		
    }
	
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1天
		// 初始化查询区间
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "手术预防使用率统计");
    }
    
    /**
	 * 清空内容
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;SESSION_CODE;VS_DR_CODE;PHA_PREVENCODE";
		table.removeRowAll();
		clearValue(clearString);
		initPage();
	}
}