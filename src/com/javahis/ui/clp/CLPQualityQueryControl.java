package com.javahis.ui.clp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.text.DecimalFormat;
import java.util.Date;

import jdo.clp.CLPQualityQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
//import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
//import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:临床路径患者治疗质量查询</p>
 *
 * <p>Description:临床路径患者治疗质量查询 </p>
 *
 * <p>Copyright: Copyright (c) blueCore</p>
 *
 * <p>Company: blueCore</p>
 *
 * @author  fux 20121228
 * @version 4.0
 */
public class CLPQualityQueryControl extends TControl{
	
	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 表格
	private TTable table;

	public CLPQualityQueryControl() {

	}
	/***
	 * 初始化
	 * */
	 public void onInit() {	       
		 super.onInit();
	     initPage();	       	      	      
	    }
	 /**
	  * 初始化页面
	  * */
	 public void initPage(){
		 this.callFunction("UI|Table|removeRowAll");
		 initControl();
	 }
	/**
	 * 初始化控件
	 * */ 
	 public void initControl(){
		    Timestamp date = StringTool.getTimestamp(new Date());
			// 初始化时间查询区间
			this.setValue("e_Date", date.toString().substring(0, 10).replace('-',
					'/')
					+ " 23:59:59");
			this.setValue("s_Date", StringTool.rollDate(date, -7).toString()
					.substring(0, 10).replace('-', '/')
					+ " 00:00:00");
			table = (TTable) this.getComponent("Table");
			start_date = (TTextFormat) this.getComponent("s_Date");
			end_date = (TTextFormat) this.getComponent("e_Date");			
	 }
	 private double getRate(double no,double num){
		 if(num==0)
			 return 0.00;
		 return no/num;
	 }
	 /**
	  * 查询
	  * */
	 public void onQuery(){
		// TParm selParm = new TParm();
		 if (!checkData()) {
			return ;
		}
		
		//出院科室
		 DecimalFormat df = new DecimalFormat("0.0");
		String deptSelect = "";
		String dept_code = this.getValueString("DEPT_CODE");
		if(dept_code != null && !dept_code.equals("")){
			deptSelect  = " AND M.OUT_DEPT = '"+dept_code+"'";
		}	
	    //得到出院日期查询条件
		String selectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND M.OUT_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}  			
		//1.查询所有的临床路径名称
		String sqlClpPathName = " SELECT  DISTINCT B.CLNCPATH_CHN_DESC,C.CLNCPATH_CODE "+
								" FROM CLP_MANAGEM C,CLP_BSCINFO B,MRO_RECORD M"+
								" WHERE C.CLNCPATH_CODE = B.CLNCPATH_CODE"+
								" AND C.CASE_NO = M.CASE_NO"+selectCondition+deptSelect;
		TParm result =  new TParm(TJDODBTool.getInstance().select(sqlClpPathName));
		if(result.getCount()<=0){
			this.messageBox("没有查询的数据");
			table.setParmValue(new TParm());
			return ;
		}
		for(int i = 0;i<result.getCount();i++){
			TParm parms = result.getRow(i);
			//入径人数			
			parms.setData("START_DATE", startDate);
			parms.setData("END_DATE", endDate);
			if(dept_code != null && !dept_code.equals("")){
				parms.setData("DS_DEPT_CODE",dept_code);
			}			
			TParm selectData1 = CLPQualityQueryTool.getInstance().selectData("querySum", parms);		
			result.setData("NOSUM",i,selectData1.getInt("NOSUM",0));
			//完成路径人数		
			TParm selectData2 = CLPQualityQueryTool.getInstance().selectData("queryNo1", parms);
		    result.setData("NO1",i,selectData2.getInt("NO1",0));
			//变异人数		
			//TParm selectData3 = CLPQualityQueryTool.getInstance().selectData("queryNo2", parms);
			result.setData("NO2",i,selectData2.getInt("NO1",0));
			//溢出人数			
			TParm selectData4 = CLPQualityQueryTool.getInstance().selectData("queryNo3", parms);
			result.setData("NO3",i,selectData4.getInt("NO3",0));
			
			//死亡人数		
			TParm selectData5 = CLPQualityQueryTool.getInstance().selectData("queryNo4", parms);
			result.setData("NO4",i,selectData5.getInt("NO4",0));
			
			//院内感染人数
			TParm selectData6 = CLPQualityQueryTool.getInstance().selectData("queryNo5", parms);
			result.setData("NO5",i,selectData6.getInt("NO5",0));
			
			//实际住院天数	出院者占床日数	
			TParm selectData7 = CLPQualityQueryTool.getInstance().selectData("queryNo6", parms);
			result.setData("NO6",i,selectData7.getInt("NO6",0));
			
			//术前平均住院日
			TParm selectData8 =  CLPQualityQueryTool.getInstance().selectData("queryNo8", parms);
			result.setData("NO8",i,selectData8.getDouble("NO8",0));
			
			//实际住院费						
			TParm selectData9 = CLPQualityQueryTool.getInstance().selectData("queryNo9", parms);
			result.setData("NO9",i,selectData9.getInt("NO9",0));
			
			//实际药品费 
			TParm selectData10 =CLPQualityQueryTool.getInstance().selectData("queryNo10", parms);
			result.setData("NO10",i,selectData10.getDouble("NO10",0));	
		}	

		// 总计需要变量		
		// 进入路径人数
		double inPathCount = 0;
		//完成路径人数
		double overPathCount = 0;
		//变异人数
		double varianceCount = 0;
		//变异率
		double varRate = 0;
		// 溢出路径人数
		double outPathCout = 0;
		//溢出率
		double outRate = 0;
		//死亡人数
		double deathCount = 0;
		//院内感染人数
		double congionCount = 0;
		//平均住院日
		double avgAdmDays = 0;
		//出院者占用总床
		double sumBed = 0;
		//术前平均住院日
		double preOperatorAdmDays = 0;
		//人均住院费
		double avgAdmCost = 0;
		//人均药品费
		double avgPhaCost = 0;
		
		int row = result.getCount();
	
		TParm selParm = new TParm();
		for(int i = 0;i<row;i++){
			selParm.addData("CLNCPATH_CHN_DESC", result.getValue("CLNCPATH_CHN_DESC",i));//CLNCPATH_CHN_DESC 临床路径名称
			selParm.addData("NOSUM",result.getValue("NOSUM",i));//NOSUM 入径人数
			inPathCount += result.getInt("NOSUM",i);
			selParm.addData("NO1",result.getValue("NO1",i));//NO1 完成路径人数
			overPathCount += result.getInt("NO1",i);
			selParm.addData("NO2", result.getValue("NO2",i));//NO2变异人数		
			
			double no2 = result.getDouble("NO2",i);	
			varianceCount += no2;
			double nosum = result.getDouble("NOSUM",i);				
			double rate1= 0;
			String variancerate = "";
			if (no2 <= 0) {
				variancerate = rate1 + "%" + "";
				selParm.addData("VARIANCERATE",variancerate );//VARIANCERATE 变异率
			} else {
				rate1 = getRate(no2,nosum);
				BigDecimal bdi = new BigDecimal(rate1);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rate1 = bdi.multiply(parmMuli).doubleValue();				
				variancerate = rate1 + "%" + "";
				selParm.addData("VARIANCERATE",variancerate );//VARIANCERATE 变异率
			}	
			varRate += rate1;
						
			selParm.addData("NO3", result.getValue("NO3",i));//NO3 溢出人数
			double no3 = result.getDouble("NO3",i);
			outPathCout += no3;
			double rate2 = 0;
			String overflowrate = "";
			if(no3 <=0){
				overflowrate = rate2 + "%" + "";
				selParm.addData("OVERFLOWRATE", overflowrate);//OVERFLOWRATE 溢出率
			}else{
				rate2 = getRate(no3,nosum);
				BigDecimal bdi = new BigDecimal(rate2);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rate2 = bdi.multiply(parmMuli).doubleValue();				
				overflowrate = rate2 + "%" + "";
				selParm.addData("OVERFLOWRATE", overflowrate);//OVERFLOWRATE 溢出率
			}
			outRate += rate2;			
		
			selParm.addData("NO4",result.getValue("NO4",i));//NO4 死亡人数
			deathCount += result.getInt("NO4",i);
			selParm.addData("NO5",result.getValue("NO5",i));//NO5 院内感染人数
			congionCount += result.getInt("NO5",i);
			double no6 = result.getDouble("NO6",i);
			double days = 0;
			if(no6<=0){
				selParm.addData("AVGADMDAYS", 0.0);//AVGADMDAYS 平均住院日
			}else{
				days = getRate(no6,nosum);
				//yanjing 20130723 注
//				BigDecimal bdi = new BigDecimal(days);
//				bdi = bdi.setScale(4, 4);
//				BigDecimal parmMuli = new BigDecimal(100);format
//				parmMuli = parmMuli.setScale(0);
//				days = bdi.multiply(parmMuli).doubleValue();
				selParm.addData("AVGADMDAYS",df.format(days));//AVGADMDAYS 平均住院日  add caoyong df.() 20131014
			}
			avgAdmDays += days;
//			selParm.addData("SUMBEDDAYS", result.getDouble("NO6",i));//SUMBEDDAYS 出院者占用总床日数      等于住院天数
			selParm.addData("SUMBEDDAYS", no6);//SUMBEDDAYS 出院者占用总床日数      等于住院天数
			sumBed += result.getInt("NO6",i);
			//格式
			double no8 = result.getDouble("NO8",i);
			double aveDays = 0;
			if(no8==0){
				selParm.addData("BEFOREOPERATORADMDAYS",0.0);//BEFOREOPERATORADMDAYS 术前平均住院日
			}else{
				aveDays =getRate(no8,nosum);
				selParm.addData("BEFOREOPERATORADMDAYS",df.format(aveDays) );//BEFOREOPERATORADMDAYS 术前平均住院日 add caoyong df.format() 20131014
				
			}
			preOperatorAdmDays += aveDays;
			double no9 = result.getDouble("NO9",i);
			double cost = 0;
			if(no9<0){
				selParm.addData("AVGCOST", 0.0);//AVGCOST 人均住院费
			}else{
				cost =getRate(no9,nosum);
//				BigDecimal bdi = new BigDecimal(cost);
//				bdi = bdi.setScale(4, 4);
//				BigDecimal parmMuli = new BigDecimal(100);
//				parmMuli = parmMuli.setScale(0);
//				cost = bdi.multiply(parmMuli).doubleValue();
				selParm.addData("AVGCOST",df.format(cost));//AVGCOST 人均住院费 add caoyong df.format() 20131014
			}
			avgAdmCost += cost;
			double no10 = result.getDouble("NO10",i);
			double phaCost = 0;
			if(no10<0){
				selParm.addData("AVGPHACOST",0.0);//AVGPHACOST 人均药品费		
			}else{
				phaCost =getRate(no10,nosum);
				selParm.addData("AVGPHACOST",df.format(phaCost));//AVGPHACOST 人均药品费	 add caoyong df.format() 20131014
			}
			avgPhaCost += phaCost;
			//selParm.addData("AVGPHACOST","");//AVGPHACOST 人均药品费			
		}
		//=================add caoyong 20131014 start=================
    	//   呼叫功能parm赋值    
		//临床路径名称,150;入径人数,70;完成路径人数,80;变异人数,70;变异率,70;溢出人数,70;溢出率,70;死亡人数,70;院内感染人数,100;平均住院日,70;出院者占用总床日数,120;术前平均住院日,100;人均住院费,80;人均药品费,80
		//CLNCPATH_CHN_DESC;NOSUM;NO1;NO2;VARIANCERATE;NO3;OVERFLOWRATE;NO4;NO5;AVGADMDAYS;SUMBEDDAYS;BEFOREOPERATORADMDAYS;AVGCOST;AVGPHACOST
		
		int nosum=0;//入径人数总人数
		int no1=0;//完成路径人数
		double no2=0;//变异人数
		double no3=0;//溢出人数
		int no4=0;//死亡人数
		int no5=0;//院内感染人数
		int sumbeddays=0;//出院者占用总床日数
		double no6=0;
		double no8=0;
		double no9=0;
		double no10=0;
		double avgadmdays=0;//平均住院日 
		double beforeoperatoradmdays=0;//术前平均住院日
		double avgcost=0;//人均住院费
		double avgphacost=0;//人均药品费
		double variancerate=0;
		double overflowrate=0;
		for (int j = 0; j < selParm.getCount("CLNCPATH_CHN_DESC"); j++) {
			nosum+=selParm.getInt("NOSUM",j);
			no1+=selParm.getInt("NO1",j);
			no2+=selParm.getDouble("NO2",j);
			no3+=selParm.getDouble("NO3",j);
			no4+=selParm.getInt("NO4",j);
			no5+=selParm.getInt("NO5",j);
			no9+=result.getDouble("NO9",j);
			no10+=result.getDouble("NO10",j);
			no6+=result.getDouble("NO6",j);
			no8+=result.getDouble("NO8",j);
			sumbeddays+=selParm.getInt("SUMBEDDAYS",j);
		}
		overflowrate=getRate(no3,no1);
		//（溢出人数/完成路径人数）
		variancerate=getRate(no2,no1);;//（变异人数/完成路径人数）
		avgadmdays=getRate(no6,no1);;//（出院者占用总床日数/完成路径人数）、
		beforeoperatoradmdays=getRate(no8,no1);;//（术前住院日总和/完成路径人数）
		avgcost=getRate(no9,no1);;//（术前住院日总和/完成路径人数）
		avgphacost=getRate(no10,no1);;//（完成路径患者药品费总和/完成路径人数）
		
		selParm.addData("CLNCPATH_CHN_DESC", "全院合计");
		selParm.addData("NOSUM", nosum);//合计入径人数总人数
		selParm.addData("NO1",no1);//合计完成路径人数
		selParm.addData("NO2", no2);//合计变异人数
		selParm.addData("VARIANCERATE", df.format(variancerate*100)+"%");// 合计变异率
		selParm.addData("NO3", no3);//合计溢出人数
		selParm.addData("OVERFLOWRATE",df.format(overflowrate*100)+"%");//溢出率
		selParm.addData("NO4",no4);//合计死亡人数
		selParm.addData("NO5",no5);//合计院内感染人数
		selParm.addData("AVGADMDAYS", df.format(avgadmdays));//合计平均住院日
		selParm.addData("SUMBEDDAYS", df.format(sumbeddays));//合计出院者占用总床日数
		selParm.addData("BEFOREOPERATORADMDAYS", df.format(beforeoperatoradmdays));//合计术前平均住院日
		selParm.addData("AVGCOST", df.format(avgcost));//合计人均住院费
		selParm.addData("AVGPHACOST", df.format(avgphacost));//合计人均药品费
		for (int i = 0; i < selParm.getCount("NO1"); i++) {
			selParm.setData("NO1",i,selParm.getDouble("NO1",i)-selParm.getDouble("NO3",i));
		}
		//=================add caoyong 20131014 end=================
		table.setParmValue(selParm);
	 }
    
	 /**
	  * 打印 
	  */
	public void onPrint() {     
		if (this.table.getRowCount() <= 0) {
			this.messageBox("没有要打印的数据");
			return;
		}	
        TParm tableParm = table.getParmValue();
        TParm PrtParm = new TParm();//打印数据
        TParm data = new TParm();
        int count = table.getRowCount();
		for(int i = 0 ;i < count;i++){
			//CLNCPATH_CHN_DESC;CLNCPATH_NUMBER;END_CLNCPATH_NUMBER;VARIANCE_NUMBER;
			//VARIANCE_PERCENTAGE;OVERFLOW_NUMBER;OVERFLOW_PERCENTAGE;DEATH_NUMBER;
			//INF_NUNBER;AVG_INDAY;OUT_DAY;AVG_OPINDAY;AVG_AMT;AVG_PHAAMT
			String Clincpath = tableParm.getValue("CLNCPATH_CHN_DESC", i);//临床路径名称
			String ClinicNumber = tableParm.getValue("CLNCPATH_NUMBER", i);//入径人数
			String EndClinicNumber = tableParm.getValue("END_CLNCPATH_NUMBER", i);//完成路径人数
			String VarianceNumber = tableParm.getValue("VARIANCE_NUMBER", i);//变异人数
			String VariancePercentage = tableParm.getValue("VARIANCE_PERCENTAGE", i);//变异率
			String OverflowNumber = tableParm.getValue("OVERFLOW_NUMBER", i);//溢出人数
			String OverflowPercentage = tableParm.getValue("OVERFLOW_PERCENTAGE", i);//溢出率
			String DeathNumber = tableParm.getValue("DEATH_NUMBER", i);//死亡人数
			String InfNumber = tableParm.getValue("INF_NUNBER", i);//院内感染人数
			String AvgInday = tableParm.getValue("AVG_INDAY", i);//平均住院日
			String Out_Day = tableParm.getValue("OUT_DAY", i);//出院者占用总床日数
			String Avg_OpInDay = tableParm.getValue("AVG_OPINDAY", i);//术前平均住院日
			String Avg_Amt = tableParm.getValue("AVG_AMT", i);//人均住院费
			String Avg_PhaAmt = tableParm.getValue("AVG_PHAAMT", i);//人均药品费
			PrtParm.addData("CLNCPATH_CHN_DESC", Clincpath);
			PrtParm.addData("CLNCPATH_NUMBER", ClinicNumber);
			PrtParm.addData("END_CLNCPATH_NUMBER", EndClinicNumber);
			PrtParm.addData("VARIANCE_NUMBER", VarianceNumber);
			PrtParm.addData("VARIANCE_PERCENTAGE", VariancePercentage);
			PrtParm.addData("OVERFLOW_NUMBER", OverflowNumber);
			PrtParm.addData("OVERFLOW_PERCENTAGE", OverflowPercentage);
			PrtParm.addData("DEATH_NUMBER", DeathNumber);
			PrtParm.addData("INF_NUNBER", InfNumber);
			PrtParm.addData("AVG_INDAY", AvgInday);
			PrtParm.addData("OUT_DAY", Out_Day);
			PrtParm.addData("AVG_OPINDAY", Avg_OpInDay);
			PrtParm.addData("AVG_AMT", Avg_Amt);
			PrtParm.addData("AVG_PHAAMT", Avg_PhaAmt);
		}
		PrtParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		PrtParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "END_CLNCPATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "VARIANCE_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "VARIANCE_PERCENTAGE");
		PrtParm.addData("SYSTEM", "COLUMNS", "OVERFLOW_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "OVERFLOW_PERCENTAGE");
		PrtParm.addData("SYSTEM", "COLUMNS", "DEATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "INF_NUNBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_INDAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "OUT_DAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_OPINDAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_AMT");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_PHAAMT");
        //表格数据汇总放入报表中
		data.setData("TABLE", PrtParm.getData());
		data.setData("NAME", "TEXT", "制作人：" + Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPQualityQuery.jhw", data);
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
		this.initPage();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径患者治疗质量统计报表");
		}
	}
	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("s_Date");
		if (start == null || start.length() <= 0) {
			this.messageBox("开始时间不能为空");
			return false;
		}
		
		String end = this.getValueString("e_Date");
		if (end == null || end.length() <= 0) {
			this.messageBox("结束时间不能为空");
			return false;
		}
		return true;
	}
	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
	 
}  
