package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sta.STACancelHosInTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:取消入院查询表
 * </p>
 * 
 * <p>
 * Description:取消入院查询表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2016-08-30
 * @version JavaHis 1.0
 */
public class STACancelHosInControl extends TControl{
	
	private final String TABLE = "TABLE";
	private String startDate;
	private String endDate;
	
	public void onInit(){
		super.onInit();
		onClear();
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = getQueryParm(1);
//		String startDate =  this.getValueString("START_DATE");
//		String endDate = this.getValueString("END_DATE");
//		parm.setData("START_DATE", startDate.substring(0, 19));
//		parm.setData("END_DATE", endDate.substring(0, 19));
//		this.setStartDate(startDate.substring(0, 19));
//		this.setEndDate(endDate.substring(0, 19));
		String sql = STACancelHosInTool.getNewInstance().getCancelHosInSQL(parm);
		TParm data = new TParm( TJDODBTool.getInstance().select(sql));
		this.getTTable(TABLE).setParmValue(data);
		if(data.getCount() <= 0) {
			this.setValue("CANCELNUM", "0");
			this.messageBox("查询暂无数据");
			return;
		} else {
			this.setValue("CANCELNUM", String.valueOf(data.getCount()));
		}
		
	}
	
	
	public void onQueryForMrNo() {
		TParm parm = getQueryParm(0);
		String sql = STACancelHosInTool.getNewInstance().getCancelHosInSQL(parm);
		TParm data = new TParm( TJDODBTool.getInstance().select(sql));
		this.getTTable(TABLE).setParmValue(data);
		if(data.getCount() <= 0) {
			this.setValue("CANCELNUM", "0");
			return;
		} else {
			this.setValue("CANCELNUM", String.valueOf(data.getCount()));
		}
	}
	
	/**
	 * 
	 * @param type 0.病案号查询  1.普通查询
	 */
	private TParm getQueryParm(int type) {
		TParm parm = new TParm();
		String mrNo = this.getValueString("MR_NO");
		if(type == 0 && !StringUtils.isEmpty(mrNo)) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
		}
		parm.setData("MR_NO", mrNo);
		this.setValue("MR_NO", mrNo);
		String startDate =  this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		parm.setData("START_DATE", startDate.substring(0, 19));
		parm.setData("END_DATE", endDate.substring(0, 19));
		this.setStartDate(startDate.substring(0, 19));
		this.setEndDate(endDate.substring(0, 19));
		return parm;
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp timeEnd =StringTool.getTimestamp(date.toString().substring(0, 10).replaceAll("-", "/") + " 23:59:59", "yyyy/MM/dd HH:mm:ss") ;
		this.setValue("END_DATE", timeEnd);
		Timestamp timeStart =StringTool.getTimestamp( StringTool.rollDate(date, -7).toString().substring(0, 10).replaceAll("-", "/") + " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		this.setValue("START_DATE", timeStart);
		this.setValue("CANCELNUM", "0");
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("无可打印数据");
			return;
		}
		TParm data = new TParm();
		String zhi = "";
		if(StringUtils.isEmpty(getStartDate()) && StringUtils.isEmpty(getEndDate())) {
			zhi = "";
		} else {
			zhi = " ～ ";
		}
		data.setData("STAT_DATE", "TEXT", this.getStartDate() + zhi + this.getEndDate());
		data.setData("CANCELNUM", "TEXT", this.getValueString("CANCELNUM") + "人");
		data.setData("TITLE", "TEXT", "取消入院统计表");
		
		//表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i < table.getRowCount(); i++) {
			parm.addData("IN_DEPT_CODE", tableParm.getData("IN_DEPT_CODE", i));
			parm.addData("IN_STATION_CODE", tableParm.getData("IN_STATION_CODE", i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("IN_DATE", tableParm.getData("IN_DATE", i));
			parm.addData("OPT_DATE", tableParm.getData("OPT_DATE", i));
		}
		parm.setCount(parm.getCount("MR_NO"));
		parm.addData("SYSTEM","COLUMNS","IN_DEPT_CODE");
		parm.addData("SYSTEM","COLUMNS","IN_STATION_CODE");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","IN_DATE");
		parm.addData("SYSTEM","COLUMNS","OPT_DATE");
		data.setData("TABLE", parm.getData());
		
		data.setData("OPT_USER","TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\sta\\STACancelHosIn.jhw", data);
	}
	
	/**
	 * 导出Excel
	 */
	public void onExport() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("无导出Excel数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table,"取消入院统计表");
	}
	
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
	
}
