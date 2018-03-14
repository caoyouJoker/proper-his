package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.udd.UDDToPreventTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
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
public class UDDToPreventControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * 初始化
   */
	public void onInit() {
		callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//
		table=this.getTable("TABLE");
	    this.onClear();
		 initPage();
		
   }
	 /**
	 * 查询
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm parm=new TParm();
		boolean flg=false;
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
			flg=true;
		}
		TParm result=UDDToPreventTool.getInstance().selectdata(parm);//住院患者
		
		TParm mresult=UDDToPreventTool.getInstance().selectdataM(parm);//使用抗菌药品人数
		if(result.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有查询数据");
			return;
		}
		
		//住院患者所对应的使用抗菌药品的人数
		TParm sumParm=new TParm();
		double sum=0;//合计总人数
		double suma=0;//合计总人数
		double count=0;//联合医用比例
		double totnum=0;//每行的总人数
		double usernum=0;//每一行的使用人数
		double totunum=0;//合计使用人数
		double totunuma=0;//合计使用人数
		double tsale=0;//合计联合应用比例
		String phapreve="";
		if(result.getCount()>0){
			 boolean flag=false;
			 String region_code="";//区域
			 String dept_code="";//科室
			 String station_code="";//病区
			 String vs_dr_code="";//医生
			 
			 for(int i=0;i<result.getCount();i++){
				  region_code=result.getValue("REGION_CODE",i);
				  dept_code=result.getValue("DEPT_CODE",i);
				  station_code=result.getValue("STATION_CODE",i);
				  vs_dr_code=result.getValue("VS_DR_CODE",i);
				  flag=false;
				 for(int j=0;j<mresult.getCount();j++){
					 if(region_code.equals(mresult.getValue("REGION_CODE",j)) &&
						dept_code.equals(mresult.getValue("DEPT_CODE",j)) &&	
						station_code.equals(mresult.getValue("STATION_CODE",j)) &&
						vs_dr_code.equals(mresult.getValue("VS_DR_CODE",j)) 
						   ){
						//多条数据 
						 sumParm.addData("REGION_CODE", region_code);
						 sumParm.addData("DEPT_CODE", dept_code);
						 sumParm.addData("STATION_CODE", station_code);
						 sumParm.addData("VS_DR_CODE", vs_dr_code);
						 sumParm.addData("PHA_PREVENCODE",mresult.getValue("PHA_PREVENCODE",j));//类型
						 sumParm.addData("USER_NUM",mresult.getValue("USER_NUM",j));//使用人数
						 sumParm.addData("TOTAL_NUM",result.getValue("TOTAL_NUM",i));//总人数
						// sumParm.addData("APPLICATION_SCALE",dec.format(count));
						    flag=true;
						}
				   }
				   sum+=result.getDouble("TOTAL_NUM",i);
				 //使用抗菌药品人数向住院患者合并
				 if(!flag){//不存在数据
					 sumParm.addData("REGION_CODE", region_code);
					 sumParm.addData("PHA_PREVENCODE","");
					 sumParm.addData("DEPT_CODE", dept_code);
					 sumParm.addData("STATION_CODE", station_code);
					 sumParm.addData("VS_DR_CODE", vs_dr_code);
					 sumParm.addData("USER_NUM",0);
					 sumParm.addData("TOTAL_NUM",result.getValue("TOTAL_NUM",i));
					// sumParm.addData("APPLICATION_SCALE",dec.format(count));
				 }
				 
			}
		
		          sumParm.setCount(sumParm.getCount("REGION_CODE"));
		          
	          for(int i=0;i<sumParm.getCount();i++){
	        	  totnum=sumParm.getDouble("TOTAL_NUM",i);
	        	  usernum=sumParm.getDouble("USER_NUM",i);
				   if(totnum!=0){
				      count=usernum/totnum;
				   }
				   sumParm.addData("APPLICATION_SCALE", dec.format(count));//应用比例
				   
				   totunum+=sumParm.getDouble("USER_NUM",i);//总的使用人
	          }
	          
			
			if(sum!=0){
				tsale=totunum/sum;
			}
			//总计
			sumParm.addData("REGION_CODE", "合计：");
			sumParm.addData("PHA_PREVENCODE", "");
			sumParm.addData("DEPT_CODE", "");
			sumParm.addData("STATION_CODE", "");
			sumParm.addData("VS_DR_CODE", "");
			sumParm.addData("USER_NUM",(int)totunum);
			sumParm.addData("TOTAL_NUM", (int)sum);
			sumParm.addData("APPLICATION_SCALE", dec.format(tsale));
			if(!flg){//类型查询
				table.setParmValue(sumParm);
			}else{
				TParm phresult=new TParm();
				phapreve=getValueString("PHA_PREVENCODE");
				for(int g=0;g<sumParm.getCount();g++){
					if(phapreve.equals(sumParm.getValue("PHA_PREVENCODE",g))){
						phresult.addRowData(sumParm, g);
					}
				}
				if(phresult.getCount()<=0){
					this.messageBox("没有查询数据");
					return;
				}
				 for(int k=0;k<phresult.getCount();k++){
					   totunuma+=phresult.getDouble("USER_NUM",k);//总的使用人
					   suma+=phresult.getDouble("TOTAL_NUM",k);
					   
		          }
		          
				
				if(suma!=0){
					tsale=totunuma/suma;
				}
				phresult.addData("REGION_CODE", "合计：");
				phresult.addData("PHA_PREVENCODE", "");
				phresult.addData("DEPT_CODE", "");
				phresult.addData("STATION_CODE", "");
				phresult.addData("VS_DR_CODE", "");
				phresult.addData("USER_NUM",(int)totunuma);
				phresult.addData("TOTAL_NUM", (int)suma);
				phresult.addData("APPLICATION_SCALE", dec.format(tsale));
				table.setParmValue(phresult);
			}
			
		}
		    
		
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
        this.setValue("PHA_PREVENCODE", tparm.getValue("PHA_PREVENCODE"));
			    
		  
	}
}