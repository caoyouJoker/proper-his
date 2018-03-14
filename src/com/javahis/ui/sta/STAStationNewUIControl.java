package com.javahis.ui.sta;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

import jdo.sta.STATool;
import jdo.sys.SystemTool;
/**
 * 病区入出转统计
 * @author wangqing 20180108
 *
 */
public class STAStationNewUIControl extends TControl {

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		Timestamp time = SystemTool.getInstance().getDate();
		this.setValue("STA_DATE1", StringTool.rollDate(time, -1));// 前一天
		this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());// 设置初始时间（上个月）
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
		if (t.getSelectedIndex() == 0) { // 日报
			this.onQueryDay();
		} else if (t.getSelectedIndex() == 1) { // 月报
			this.onQueryMonth();		
		}
	}

	/**
	 * 日报查询
	 */
	public void onQueryDay(){	
		TTable table = (TTable) this.getComponent("TableDay");
		table.removeRowAll();
		Timestamp queryDate = (Timestamp) this.getValue("STA_DATE1");
		if(queryDate==null || queryDate.toString().trim().length()<=0){
			this.messageBox("查询日期不能为空");
			return;
		}
		Timestamp firstDay = StringTool.rollDate(queryDate, 1);
		String queryDateStr = StringTool.getString(queryDate, "yyyyMMdd");
		String firstDayStr = StringTool.getString(firstDay, "yyyyMMdd");
		//		System.out.println("{queryDateStr:"+queryDateStr+";lastDayStr:"+lastDayStr+"}");
		String stationCode = this.getValueString("STATION_CODE1");
		// 入病区人数
		String sql1 = "SELECT IN_STATION_CODE AS STATION_CODE, COUNT(CASE_NO) AS IN_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE substr(IN_DATE, 0, 8)='"+queryDateStr+"' @ GROUP BY IN_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql1 = sql1.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql1 = sql1.replace("@", "");
		}
		System.out.println("{入病区人数sql1:"+sql1+"}");
		// 出病区人数
		String sql2 = "SELECT OUT_STATION_CODE as STATION_CODE, COUNT(CASE_NO) AS OUT_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE to_char(OUT_DATE, 'yyyyMMdd')='"+queryDateStr+"' @ GROUP BY OUT_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql2 = sql2.replace("@", " AND OUT_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql2 = sql2.replace("@", "");
		}
		System.out.println("{出病区人数sql2:"+sql2+"}");
		// 期初实有人数
		String sql3 = "SELECT distinct CASE_NO, IN_STATION_CODE as STATION_CODE, DS_DATE "
				+ "FROM ADM_DAILY_ZERO_INP "
				+ "WHERE DS_DATE IS NULL AND to_char(DAILY_DATE, 'yyyyMMdd')='"+queryDateStr+"' @ ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql3 = sql3.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql3 = sql3.replace("@", "");
		}
		sql3 = "SELECT STATION_CODE, COUNT(CASE_NO) AS BEGIN_REAL_NO FROM ("+sql3+") GROUP BY STATION_CODE ";		
		System.out.println("{期初实有人数sql3:"+sql3+"}");
		// 留病区人数（期末实有人数）			
		String sql4 = "SELECT distinct CASE_NO, IN_STATION_CODE as STATION_CODE, DAILY_DATE "
				+ "FROM ADM_DAILY_ZERO_INP "
				+ "WHERE DS_DATE IS NULL AND to_char(DAILY_DATE, 'yyyyMMdd')='"+firstDayStr+"' @ ";	
		if(stationCode!=null && stationCode.trim().length()>0){
			sql4 = sql4.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql4 = sql4.replace("@", "");
		}
		sql4 = "SELECT STATION_CODE, COUNT(CASE_NO) AS END_REAL_NO FROM ("+sql4+") GROUP BY STATION_CODE ";
		System.out.println("{留病区人数（期末实有人数）sql4:"+sql4+"}");
		// 病区
		String sql5 = "SELECT STATION_CODE, STATION_DESC FROM SYS_STATION @ ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql5 = sql5.replace("@", " WHERE STATION_CODE ='"+stationCode+"' ");
		}else{	
			sql5 = sql5.replace("@", "");
		}
		System.out.println("{病区sql5:"+sql5+"}");
		String sql = "with t1 as ("+sql1+"), "
				+ "t2 as ("+sql2+"), "
				+ "t3 as ("+sql3+"), "
				+ "t4 as ("+sql4+"), "
				+ "t5 as("+sql5+") "
				+ "SELECT t5.STATION_CODE, t1.IN_STATION_NO, t2.OUT_STATION_NO, t3.BEGIN_REAL_NO, t4.END_REAL_NO "
				+ "FROM t1, t2, t3, t4, t5 "
				+ "WHERE t5.STATION_CODE=t1.STATION_CODE(+) "
				+ "AND t5.STATION_CODE=t2.STATION_CODE(+) "
				+ "AND t5.STATION_CODE=t3.STATION_CODE(+) " 
				+ "AND t5.STATION_CODE=t4.STATION_CODE(+) "
				+ "ORDER BY STATION_CODE ";
		System.out.println("{查询入出转人数sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		//		System.out.println("{result:"+result+"}");
		table.setParmValue(result);	
	}

	/**
	 * 月报查询
	 */
	public void onQueryMonth(){
		TTable table = (TTable) this.getComponent("TableMouth");
		table.removeRowAll();
		Timestamp queryDate = (Timestamp) this.getValue("STA_DATE2");
		if(queryDate==null || queryDate.toString().trim().length()<=0){
			this.messageBox("查询日期不能为空");
			return;
		}
		String queryDateStr = StringTool.getString(queryDate, "yyyyMM");
		String StartDate = queryDateStr + "01";// 每月第一天
		String EndDate = StringTool.getString(STATool.getInstance().getLastDayOfMonth(queryDateStr), "yyyyMMdd");// 获取此月份的最后一天
		//		System.out.println("{StartDate:"+StartDate+";EndDate:"+EndDate+"}");
		String stationCode = this.getValueString("STATION_CODE2");
		// 入病区人数
		String sql1 = "SELECT IN_STATION_CODE AS STATION_CODE, COUNT(CASE_NO) AS IN_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE substr(IN_DATE, 0, 8)>='"+StartDate+"' AND substr(IN_DATE, 0, 8)<='"+EndDate+"' @ GROUP BY IN_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql1 = sql1.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql1 = sql1.replace("@", "");
		}
		System.out.println("{入病区人数sql1:"+sql1+"}");
		// 出病区人数
		String sql2 = "SELECT OUT_STATION_CODE as STATION_CODE, COUNT(CASE_NO) AS OUT_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE to_char(OUT_DATE, 'yyyyMMdd')>='"+StartDate+"' AND substr(OUT_DATE, 0, 8)<='"+EndDate+"' @ GROUP BY OUT_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql2 = sql2.replace("@", " AND OUT_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql2 = sql2.replace("@", "");
		}
		System.out.println("{出病区人数sql2:"+sql2+"}");
		// 留病区人数（期末实有人数）			
		String sql4 = "SELECT distinct CASE_NO, IN_STATION_CODE as STATION_CODE, DAILY_DATE "
				+ "FROM ADM_DAILY_ZERO_INP "
				+ "WHERE DS_DATE IS NULL "
				+ "AND to_char(DAILY_DATE, 'yyyyMMdd')>='"+StartDate+"' AND to_char(DAILY_DATE, 'yyyyMMdd')<='"+EndDate+"' @ ";	
		if(stationCode!=null && stationCode.trim().length()>0){
			sql4 = sql4.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql4 = sql4.replace("@", "");
		}
		sql4 = "SELECT STATION_CODE, COUNT(CASE_NO) as REAL_OCCUPY_BED_NO FROM ("+sql4+") GROUP BY STATION_CODE ";
		System.out.println("{实际占用总床日数）sql4:"+sql4+"}");
		// 病区
		String sql5 = "SELECT STATION_CODE, STATION_DESC FROM SYS_STATION @ ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql5 = sql5.replace("@", " WHERE STATION_CODE ='"+stationCode+"' ");
		}else{	
			sql5 = sql5.replace("@", "");
		}
		String sql = "with t1 as ("+sql1+"), "
				+ "t2 as ("+sql2+"), "
				+ "t4 as ("+sql4+"), "
				+ "t5 as("+sql5+") "
				+ "SELECT t5.STATION_CODE, t1.IN_STATION_NO, t2.OUT_STATION_NO, t4.REAL_OCCUPY_BED_NO "
				+ "FROM t1, t2, t4, t5 "
				+ "WHERE t5.STATION_CODE=t1.STATION_CODE(+) "
				+ "AND t5.STATION_CODE=t2.STATION_CODE(+) "
				+ "AND t5.STATION_CODE=t4.STATION_CODE(+) "
				+ "ORDER BY STATION_CODE";
		System.out.println("{查询入出转人数sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		//		System.out.println("{result:"+result+"}");
		table.setParmValue(result);	

	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
		TTable table;
		if (t.getSelectedIndex() == 0) { // 日报
			table = (TTable) this.getComponent("TableDay");
			if (table.getRowCount()<1) {
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "病区入出转统计日报");
		} else if (t.getSelectedIndex() == 1) { // 月报
			table = (TTable) this.getComponent("TableMouth");	
			if (table.getRowCount()<1) {
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "病区入出转统计月报");
		}		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
