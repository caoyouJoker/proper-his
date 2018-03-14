package com.javahis.ui.odi;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.odi.ODIINFEEMedicinalTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 联合应用比例统计</p>
 *
 * <p>Description:联合应用比例统计</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.10.28
 * @version 1.0
 */
public class ODIINFEEMedicinalControl  extends TControl{
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
		TParm result=ODIINFEEMedicinalTool.getInstance().selectdata(parm);
		
		TParm mresult=ODIINFEEMedicinalTool.getInstance().selectdataM(parm);
		if(result.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		if(mresult.getCount()<=0){
			table.removeRowAll();
			this.messageBox("没有查询数据");
			return;
		}
		String sql="";
		TParm phaParm=new TParm();//联合用药统计
		TParm temp=new TParm();
		Timestamp time=null;
		String regionCode=result.getValue("REGION_CODE", 0);
		String deptCode= result.getValue("DEPT_CODE",0);
		String stationCode=result.getValue("STATION_CODE",0);
		String vsDrCode=result.getValue("VS_DR_CODE",0);
		int count=0;
		for (int i = 0; i < result.getCount(); i++) {
			if (regionCode.equals(result.getValue("REGION_CODE", i))
					&&deptCode.equals(result.getValue("DEPT_CODE", i))
							&&stationCode.equals(result.getValue("STATION_CODE", i))
									&&vsDrCode.equals(result.getValue("VS_DR_CODE", i))) {
				sql=" SELECT ORDER_CODE,ORDER_DATE,DC_DATE FROM ODI_ORDER WHERE CASE_NO='"+result.getValue("CASE_NO",i)+"' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND='UD' ORDER BY ORDER_DATE";
				temp=new TParm(TJDODBTool.getInstance().select(sql));
				time=temp.getTimestamp("DC_DATE",0);
				//比较时间
				for (int j = 1; j < temp.getCount(); j++) {
					if (time.after(temp.getTimestamp("ORDER_DATE",j))) {
						count++;
						break;
					}else{
						time=temp.getTimestamp("DC_DATE",j);
					}
				}
			}else{
				phaParm.addData("REGION_CODE", regionCode);
				phaParm.addData("DEPT_CODE", deptCode);
				phaParm.addData("STATION_CODE", stationCode);
				phaParm.addData("VS_DR_CODE", vsDrCode);
				phaParm.addData("JOINT_NUM", count);//累计联合用药人数
				regionCode=result.getValue("REGION_CODE", i);
				deptCode= result.getValue("DEPT_CODE",i);
				stationCode=result.getValue("STATION_CODE",i);
				vsDrCode=result.getValue("VS_DR_CODE",i);
				count=0;
			}
			if (i== result.getCount()-1) {
//				sql=" SELECT ORDER_CODE,ORDER_DATE,DC_DATE FROM ODI_ORDER WHERE CASE_NO='"+result.getValue("CASE_NO",i)+"' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND='UD' ORDER BY ORDER_DATE";
//				temp=new TParm(TJDODBTool.getInstance().select(sql));
//				time=temp.getTimestamp("DC_DATE",0);
//				for (int j = 1; j < temp.getCount(); j++) {
//					if (time.after(temp.getTimestamp("ORDER_DATE",j))) {
//						count++;
//						break;
//					}
//				}
				phaParm.addData("REGION_CODE", regionCode);
				phaParm.addData("DEPT_CODE", deptCode);
				phaParm.addData("STATION_CODE", stationCode);
				phaParm.addData("VS_DR_CODE", vsDrCode);
				phaParm.addData("JOINT_NUM", count);//累计联合用药人数
			}
		}
		phaParm.setCount(phaParm.getCount("VS_DR_CODE"));
		//存在联合用药
		if(mresult.getCount()>0){
			for(int i=0;i<mresult.getCount();i++){
				for (int j = 0; j < phaParm.getCount(); j++) {
					// 选择对应的联合用药人数
					if (mresult.getValue("REGION_CODE", i).equals(
							phaParm.getValue("REGION_CODE", j))
							&& mresult.getValue("DEPT_CODE", i).equals(
									phaParm.getValue("DEPT_CODE", j))
							&& mresult.getValue("STATION_CODE", i).equals(
									phaParm.getValue("STATION_CODE", j))
							&& mresult.getValue("VS_DR_CODE", i).equals(
									phaParm.getValue("VS_DR_CODE", j))) {
						mresult.setData("JOINT_NUM", i, phaParm.getInt("JOINT_NUM", j));
						mresult.setData("APPLICATION_SCALE", i, StringTool
								.round(phaParm.getDouble("JOINT_NUM", j)/ mresult.getDouble("TOTAL_NUM",i) * 100,2)+ "%");
					}
				}
			}
				  
		}
    //合计		
		double jnum = 0;// 联合人数
		double tnum = 0;
		double jsele = 0;
		for (int g = 0; g < mresult.getCount(); g++) {
			jnum += mresult.getDouble("JOINT_NUM", g);
			tnum += mresult.getDouble("TOTAL_NUM", g);
		}
		if (tnum != 0) {
			jsele = jnum / tnum ;
		}
		mresult.addData("REGION_CODE", "合计：");
		mresult.addData("STATION_CODE", "");
		mresult.addData("DEPT_CODE", "");
		mresult.addData("VS_DR_CODE", "");
		mresult.addData("JOINT_NUM", (int) jnum);
		mresult.addData("TOTAL_NUM", (int) tnum);
		mresult.addData("APPLICATION_SCALE", dec.format(jsele));
		mresult.setCount(mresult.getCount() + 1);
		table.setParmValue(mresult);
	}
	/**
	 * 初始化
	 */
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
        ExportExcelUtil.getInstance().exportExcel(table, "联合应用比例统计");
    }
    
    /**
	 * 清空内容
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;SESSION_CODE;VS_DR_CODE";
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
}