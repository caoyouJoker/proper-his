package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sta.STAZeroInHosTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title:0点在院病人明细表
 * </p>
 * 
 * <p>
 * Description:0点在院病人明细表
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
 * @author wukai 2016-09-01
 * @version JavaHis 1.0
 */
public class STAZeroInHosControl extends TControl{
	
	private static final String TABLE= "TABLE";
	private String dailyDate;
	private String dept;
	private String station;
	
	public void onInit(){
		super.onInit();
		onClear();
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery() { 
		TParm parm = new TParm();
		String date = this.getValueString("DAILY_DATE");
		if(StringUtils.isEmpty(date)) {
			this.messageBox("请输入查询日期");
			return;
		}
		this.setDailyDate(date.substring(0, 10));
		this.setDept(this.getText("DEPT_CODE"));
		this.setStation(this.getText("STATION_CODE"));
//		parm.setData("CTZ_CODE", this.getValue("CTZ1_CODE"));
		parm.setData("DAILY_DATE", StringTool.getTimestamp(date.substring(0, 10) + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		if(!StringUtils.isEmpty(this.getValueString("DEPT_CODE"))) {
			parm.setData("IN_DEPT_CODE", this.getValueString("DEPT_CODE"));
		}
		if(!StringUtils.isEmpty(this.getValueString("STATION_CODE"))) {
			parm.setData("IN_STATION_CODE", this.getValueString("STATION_CODE"));
		}
		TParm result = STAZeroInHosTool.getNewInstance().selectData(parm);
		if(result.getErrCode() < 0) {
			this.getTTable(TABLE).setParmValue(new TParm());
			this.messageBox("获取0点在院病人失败！");
			this.setValue("ZEROCOUNT", "0");
			return;
		}
		if(result.getCount() <= 0) {
			this.setValue("ZEROCOUNT", "0");
			this.messageBox("当天0点无在院病人！");
		} else {
			this.setValue("ZEROCOUNT", String.valueOf(result.getCount()));
		}
		
		
		this.filterTParmData(result);
		
		this.getTTable(TABLE).setParmValue(result);
	}
	/**
	 * 过滤查询到的病患基本信息数据住院用于计算年龄和住院天数来放入到查询出来的TParm中
	 * 
	 * @param parm
	 *            TParm 需要过滤的数据
	 * @return TParm
	 */
	public TParm filterTParmData(TParm parm) {
		// System.out.println("过滤TABLE资料"+parm);
		/*
		 * 利用循环来计算此数据中的生日算年龄，入院日期算住院天数
		 * (生日字段:SYS_PATINFO.BIRTH_DATE对应KEY(AGE),入院日期字段
		 * :ADM_INP.IN_DATE对应KEY(DAYNUM))
		 */
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp;
		Timestamp tp;
		// 计算年龄
		String age = "0";
		// 返回的行数
		int rowCount = parm.getCount("PAT_NAME");
		for (int i = 0; i < rowCount; i++) {
			if(parm.getTimestamp("BIRTH_DATE", i) != null){
				temp = parm.getTimestamp("BIRTH_DATE", i) == null ? sysDate
						: parm.getTimestamp("BIRTH_DATE", i);
				if (parm.getTimestamp("IN_DATE", i) != null){
					age = OdiUtil.showAge(temp,
							parm.getTimestamp("IN_DATE", i));
				}else{
					age = "";
				}
			} else {
				age = "";
			}
			parm.addData("AGE", age);
			// 计算住院天数
			tp = parm.getTimestamp("DS_DATE", i);
			if (tp == null) {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
							"00:00:00"), StringTool.setTime(parm.getTimestamp(
							"IN_DATE", i), "00:00:00"));
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			} else {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					// ===============modify by chenxi 20120703 start
					days = StringTool.getDateDiffer(StringTool.setTime(parm
							.getTimestamp("DS_DATE", i), "00:00:00"),
							StringTool.setTime(parm.getTimestamp("IN_DATE", i),
									"00:00:00"));
					// =========== modify by chenxi 20120703 stop
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			}
		}
		return parm;
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		Timestamp date = StringTool.getTimestamp(new Date());
		this.clearValue("DEPT_CODE;STATION_CODE");
		this.setValue("DAILY_DATE", date);
		this.setValue("ZEROCOUNT", "0");
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("无可打印数据！");
			return;
		}
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", "0点在院病人明细表");
		data.setData("DAILY_DATE", "TEXT", this.getDailyDate());
		data.setData("ZEROCOUNT", "TEXT", this.getValueString("ZEROCOUNT") + "人");
		data.setData("DEPT_CODE", "TEXT", this.getDept());
		data.setData("STATION_CODE", "TEXT", this.getStation());
		
		//表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i < table.getRowCount(); i++) {
			parm.addData("BED_NO_DESC", tableParm.getData("BED_NO_DESC",i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME",i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
//			parm.addData("SEX_CODE", "1".equals(tableParm.getData("SEX_CODE",i)) ? "男" : "女");
			parm.addData("SEX_CODE",tableParm.getData("SEX_CODE",i));
			parm.addData("AGE", tableParm.getData("AGE",i));
			parm.addData("IN_DATE",  tableParm.getData("IN_DATE",i).toString().substring(0, 9));
			parm.addData("DAYNUM",  tableParm.getData("DAYNUM",i));
			parm.addData("VS_DR_CODE",  tableParm.getData("VS_DR_CODE",i));
			parm.addData("MAINDIAG",  tableParm.getData("MAINDIAG",i));
			parm.addData("IN_DEPT_CODE",  tableParm.getData("IN_DEPT_CODE",i));
			parm.addData("IN_STATION_CODE",  tableParm.getData("IN_STATION_CODE",i));
			parm.addData("CTZ1_CODE",  tableParm.getData("CTZ1_CODE",i));
			parm.addData("CTZ2_CODE",  tableParm.getData("CTZ2_CODE",i));
			parm.addData("CUR_AMT",  tableParm.getData("CUR_AMT",i));
			parm.addData("CLNCPATH_CODE",  tableParm.getData("CLNCPATH_CODE",i));
			parm.addData("SCHD_CODE",  tableParm.getData("SCHD_CODE",i));
			parm.addData("DISE_CODE",  tableParm.getData("DISE_CODE",i));
			

//			parm.addData("IN_DEPT_CODE", tableParm.getData("IN_DEPT_CODE", i));
//			parm.addData("IN_STATION_CODE", tableParm.getData("IN_STATION_CODE", i));
//			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
//			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
//			parm.addData("IN_DATE", tableParm.getData("IN_DATE", i));
//			parm.addData("VS_DR_CODE", tableParm.getData("VS_DR_CODE", i));
		}
//		parm.setCount(parm.getCount("MR_NO"));
//		parm.addData("SYSTEM","COLUMNS","IN_DEPT_CODE");
//		parm.addData("SYSTEM","COLUMNS","IN_STATION_CODE");
//		parm.addData("SYSTEM","COLUMNS","MR_NO");
//		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
//		parm.addData("SYSTEM","COLUMNS","IN_DATE");
//		parm.addData("SYSTEM","COLUMNS","VS_DR_CODE");
		
		parm.setCount(parm.getCount("MR_NO"));
		parm.addData("SYSTEM","COLUMNS","BED_NO_DESC");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","SEX_CODE");
		parm.addData("SYSTEM","COLUMNS","AGE");
		parm.addData("SYSTEM","COLUMNS","IN_DATE");
		parm.addData("SYSTEM","COLUMNS","DAYNUM");
		parm.addData("SYSTEM","COLUMNS","VS_DR_CODE");
		parm.addData("SYSTEM","COLUMNS","MAINDIAG");
		parm.addData("SYSTEM","COLUMNS","IN_DEPT_CODE");
		parm.addData("SYSTEM","COLUMNS","IN_STATION_CODE");
		parm.addData("SYSTEM","COLUMNS","CTZ1_CODE");
		parm.addData("SYSTEM","COLUMNS","CTZ2_CODE");
		parm.addData("SYSTEM","COLUMNS","CUR_AMT");
		parm.addData("SYSTEM","COLUMNS","CLNCPATH_CODE");
		parm.addData("SYSTEM","COLUMNS","SCHD_CODE");
		parm.addData("SYSTEM","COLUMNS","DISE_CODE");
		
		data.setData("TABLE", parm.getData());
		
		data.setData("OPT_USER","TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\sta\\STAZeroInHos.jhw", data);
		
	}
	
	/**
	 * 导出Excel
	 */
	public void onExport() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("无导出Excel数据！");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "0点在院病人明细表");
	}

	public String getDailyDate() {
		return dailyDate;
	}

	public void setDailyDate(String dailyDate) {
		this.dailyDate = dailyDate;
	}
	
	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	
	
}
