package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import jdo.bil.BILSysParmTool;
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
 * <p>
 * Title: 科室收入统计
 * </p>
 * 
 * <p>
 * Description: 科室收入统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) ProperSoft
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author caowl 2012.10.30
 * @version 1.0
 */
public class BILExaDeptPrintWordControl extends TControl {

	private TTable table;
	
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化界面
	 * 
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("S_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("E_DATE", rollDay);
		// 初始化执行科室和病区为空
		this.setValue("DEPT_CODE", "");
		this.setValue("STATION_CODE", "");

		// 移除表中所有数据
		this.callFunction("UI|Table|removeRowAll");
		//得到表
		table =(TTable) this.getComponent("Table");
		//设置表头
		table.setHeader("执行科室,100;外一科/7病区,100,double,########0.00;内一科/13病区,100,double,########0.00;内二科/9病区,100,double,########0.00;内一科/国际,100,double,########0.00;内三科/11病区,100,double,########0.00;内四科/11病区,100,double,########0.00;ICU,100,double,########0.00;CCU,100,double,########0.00;国际医疗部,100,double,########0.00;儿童心脏病内科/7病区,150,double,########0.00;代谢科/13病区,100,double,########0.00;总计,100,double,########0.00");//caowl add 儿童心脏病内科和代谢科 20130407		
		//设置parmMap
		table.setParmMap("COST_CENTER_CHN_DESC;SUM1;SUM2;SUM3;SUM4;SUM5;SUM6;SUM7;SUM8;SUM9;SUM10;SUM11;SUM");//caowl add sum10,sum11 20130407

	}
	/**
	 * 查询
	 * */
	public void onQuery() {
		
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");

		//外一科    七病区
		String sql1 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030201' AND STATION_CODE = 'S07'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
	
		TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		
		//内一科 十三病区
		String sql2 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030101' AND STATION_CODE = 'S13'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
	
		
		TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		
		//内二科 九病区
		String sql3 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030102' AND STATION_CODE = 'S09'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		
		//内一科/国际
		String sql4 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030101' AND STATION_CODE = 'V01'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm4 = new TParm(TJDODBTool.getInstance().select(sql4));
		
		//内三科   十一病区
		String sql5 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030103' AND STATION_CODE = 'S11'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm5 = new TParm(TJDODBTool.getInstance().select(sql5));
		
		//内四科 十一病区
		String sql6 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030104' AND STATION_CODE = 'S11'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm6 = new TParm(TJDODBTool.getInstance().select(sql6));
		
		//ICU
		String sql7 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '0303' AND STATION_CODE = 'I01'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm7 = new TParm(TJDODBTool.getInstance().select(sql7));
		
		//CCU
		String sql8 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '0304' AND STATION_CODE = 'C02'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm8 = new TParm(TJDODBTool.getInstance().select(sql8));
		
		//国际诊疗部
		String sql9 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
						" AND DEPT_CODE = '030901' AND STATION_CODE = 'V01'"+
						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
						" ORDER BY EXE_DEPT_CODE";
		
		TParm selParm9 = new TParm(TJDODBTool.getInstance().select(sql9));
		
		//caowl 20130407 start
		//儿童心脏病内科/7病区
		String sql10 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
		" FROM IBS_ORDD A,SYS_COST_CENTER B"+
		" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
		" AND DEPT_CODE = '030106' AND STATION_CODE = 'S07'"+
		" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
		" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
		" ORDER BY EXE_DEPT_CODE";
		TParm selParm10 = new TParm(TJDODBTool.getInstance().select(sql10));
		
		//代谢科/13病区
		String sql11 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
		" FROM IBS_ORDD A,SYS_COST_CENTER B"+
		" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
		" AND DEPT_CODE = '030105' AND STATION_CODE = 'S13'"+
		" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
		" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
		" ORDER BY EXE_DEPT_CODE";
		TParm selParm11 = new TParm(TJDODBTool.getInstance().select(sql11));
		//caowl 20130407 end
		
		//查询所有的执行科室
		String sql = "SELECT COST_CENTER_CODE AS EXE_DEPT_CODE ,COST_CENTER_CHN_DESC FROM SYS_COST_CENTER ";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));									
		TParm parmValue = new TParm();
	
		double sum1=0.00;
		double sum2=0.00;
		double sum3=0.00;
		double sum4=0.00;
		double sum5=0.00;
		double sum6=0.00;
		double sum7=0.00;
		double sum8=0.00;
		double sum9=0.00;
		double sum10=0.00;//caowl add 20130407
		double sum11 = 0.00;//caowl add 20130407
		double sums = 0.00;
		for(int i = 0;i<selParm.getCount();i++){
			String exe_dept_code =selParm.getValue("EXE_DEPT_CODE",i);
			
			parmValue.setData("COST_CENTER_CHN_DESC",selParm.getValue("COST_CENTER_CHN_DESC",i));
			//1111111111111111
			double sum = 0.00;
			if(selParm1.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm1.getCount();j++){				
					if(selParm1.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){						
						parmValue.setData("SUM1",selParm1.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm1.getDouble("SUM(A.TOT_AMT)",j);
						sum1 += selParm1.getDouble("SUM(A.TOT_AMT)",j);;
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM1",0.00);
				sum += 0.00;
				sum1 += 0.00;
			}
			//222222222222222222
			if(selParm2.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm2.getCount();j++){
					if(selParm2.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM2",selParm2.getValue("SUM(A.TOT_AMT)",j));
						sum +=   selParm2.getDouble("SUM(A.TOT_AMT)",j);
						sum2 += selParm2.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM2",0.00);
				sum += 0.00;
				sum2 += 0.00;
			}
			//333333333333333333
			if(selParm3.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm3.getCount();j++){
					if(selParm3.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM3",selParm3.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm3.getDouble("SUM(A.TOT_AMT)",j);
						sum3 += selParm3.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					
					}
				}
			}
			
			else{
				parmValue.setData("SUM3",0.00);
				sum += 0.00;
				sum3 += 0.00;
			}
			//4444444444444444444
			if(selParm4.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm4.getCount();j++){
					if(selParm4.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM4",selParm4.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm4.getDouble("SUM(A.TOT_AMT)",j);
						sum4 += selParm4.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM4",0.00);
				sum += 0.00;
				sum4 +=0.00;
			}
			//5555555555555555555
			if(selParm5.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm5.getCount();j++){
					if(selParm5.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM5",selParm5.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm5.getDouble("SUM(A.TOT_AMT)",j);
						sum5 += selParm5.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM5",0.00);
				sum += 0.00;
				sum5 += 0.00;
			}
			//66666666666666666
			if(selParm6.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm6.getCount();j++){
					if(selParm6.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM6",selParm6.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm6.getDouble("SUM(A.TOT_AMT)",j);
						sum6 += selParm6.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM6",0.00);
				sum += 0.00;
				sum6 += 0.00;
			}
			//77777777777777
			if(selParm7.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm7.getCount();j++){
					if(selParm7.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM7",selParm7.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm7.getDouble("SUM(A.TOT_AMT)",j);
						sum7 += selParm7.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM7",0.00);
				sum += 0.00;
				sum7 += 0.00;
			}
			//88888888888888
			if(selParm8.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm8.getCount();j++){
					if(selParm8.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM8",selParm8.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm8.getDouble("SUM(A.TOT_AMT)",j);
						sum8 += selParm8.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM8",0.00);
				sum += 0.00;
				sum8 += 0.00;
			}
			//999999999999999
			if(selParm9.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm9.getCount();j++){
					if(selParm9.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM9",selParm9.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm9.getDouble("SUM(A.TOT_AMT)",j);
						sum9 += selParm9.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM9",0.00);
				sum += 0.00;
				sum9 += 0.00;
			}
			//caowl 20130407 start
			//第十个
			if(selParm10.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm10.getCount();j++){
					if(selParm10.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM10",selParm10.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm10.getDouble("SUM(A.TOT_AMT)",j);
						sum10 += selParm10.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM10",0.00);
				sum += 0.00;
				sum10 += 0.00;
			}
			
			//第十一个
			if(selParm11.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
				for(int j = 0 ;j<selParm11.getCount();j++){
					if(selParm11.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
						parmValue.setData("SUM11",selParm11.getValue("SUM(A.TOT_AMT)",j));
						sum += selParm11.getDouble("SUM(A.TOT_AMT)",j);
						sum11 += selParm11.getDouble("SUM(A.TOT_AMT)",j);
					}
					else{
						continue;
					}
				}
			}
			else{
				parmValue.setData("SUM11",0.00);
				sum += 0.00;
				sum11 += 0.00;
			}
			//caowl 20130407 end
			
			parmValue.setData("SUM",sum);
			sums += sum;
			
			if(sum != 0){
				table.addRow(parmValue);				
			}								
		}
	   
		TParm parm = new TParm();
		parm.setData("COST_CENTER_CHN_DESC", "合计");
		parm.setData("SUM1", sum1);
		parm.setData("SUM2", sum2);
		parm.setData("SUM3", sum3);
		parm.setData("SUM4", sum4);
		parm.setData("SUM5", sum5);
		parm.setData("SUM6", sum6);
		parm.setData("SUM7", sum7);
		parm.setData("SUM8", sum8);
		parm.setData("SUM9", sum9);
		//caowl 20130407 start
		parm.setData("SUM10",sum10);
		parm.setData("SUM11",sum11);
		//caowl 20130407 end
		parm.setData("SUM", sums);
		
		table.addRow(parm);

		if (table.getRowCount() < 1) {
			// 查无数据
			this.messageBox("E0008");
			this.initPage();			
		}			
		
	}

	/**
	 * 清空
	 * */
	public void onClear() {
		initPage();		
	}



	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "科室收入统计表");
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
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

}
