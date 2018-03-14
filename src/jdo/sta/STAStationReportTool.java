package jdo.sta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 病区统计报表
 * </p>
 * 
 * <p>
 * Description: 病区统计报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wukai 20160718
 * @version 1.0
 */
public class STAStationReportTool extends TJDOTool {
	
	
	private static STAStationReportTool mInstance;
	
	public static STAStationReportTool getNewInstance(){
		if(mInstance == null) {
			mInstance = new STAStationReportTool();
		}
		return mInstance;
	}

	public STAStationReportTool() {
		//setModuleName("sta\\STAWorkLogModule.x");
		//onInit();
	}
	
	/**
	 * 获取日工作报表
	 * @param station_code
	 * @param stat_date
	 * @return
	 */
	public String getStationReportSQL(String station_code,String stat_date,int type) {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(" SELECT STATION_CODE, SUM(DATA_07) AS DATA_07, SUM(DATA_08) AS DATA_08, SUM(DATA_08_1) AS DATA_08_1, SUM(DATA_09) AS DATA_09,");
		sbuilder.append(" SUM(DATA_11) AS DATA_11, SUM(DATA_12) AS DATA_12, SUM(DATA_13) AS DATA_13, SUM(DATA_14) AS DATA_14, SUM(DATA_15) AS DATA_15,");
		sbuilder.append(" SUM(DATA_15_1) AS DATA_15_1, SUM(DATA_16) AS DATA_16 , SUM(DATA_16) AS DATA_77,SUM(DATA_19) AS DATA_19,");
		sbuilder.append(" (CASE WHEN SUM(DATA_09)=0 THEN 0 ELSE SUM(DATA_19)/SUM(DATA_09) END) AS DATA_19_AVG ");
		sbuilder.append(" FROM STA_DAILY_01 WHERE 1=1 ");
		
		if(station_code != null && station_code.length() > 0) {  
			sbuilder.append(" AND STATION_CODE = '" + station_code + "'");
		}
		
		if(stat_date != null && stat_date.length() > 0) {
			System.out.println("stat_date:::::" + stat_date);
			if(type == 0) {   //日报
				stat_date = stat_date.replaceAll("-", "").substring(0, 8);
				sbuilder.append(" AND TO_CHAR(STA_DATE) = '" + stat_date + "'");
			} else {    //月报
				int days = getMonthDays(stat_date);
				stat_date = stat_date.replaceAll("-", "").substring(0, 6);
				sbuilder.append(" AND TO_CHAR(STA_DATE) >= '" + stat_date + "01' AND "
								+ " TO_CHAR(STA_DATE) <='" + stat_date + days +"'"
								);
			}
		}
		String stationSql =
	            " SELECT DISTINCT (A.STATION_CODE) AS STATION_CODE FROM SYS_STATION A ,SYS_STADEP_LIST B WHERE A.STATION_CODE = B.STATION_CODE(+) ";
		sbuilder.append(" AND STATION_CODE IN ( " + stationSql + " ) ");
		sbuilder.append(" GROUP BY STATION_CODE ");
		sbuilder.append(" ORDER BY STATION_CODE ");
		System.out.println("sbuilder ::::::: =  " + sbuilder.toString());
		return sbuilder.toString();
	}
	
	/**
	 * 获取相应病区出院孩子的数量
	 * @param station_code
	 * @param stat_date
	 * @param type
	 * @return
	 */
	public String getChildNumSQL(String station_code,String stat_date,int type) {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(" SELECT A.DS_STATION_CODE AS STATION_CODE, COUNT(A.MR_NO) AS DATA_77_1 FROM ADM_INP A, SYS_PATINFO B ");
		sbuilder.append(" WHERE A.MR_NO = B.MR_NO ");
		sbuilder.append(" AND (EXTRACT(year FROM A.IN_DATE) - EXTRACT(year FROM B.BIRTH_DATE)) < 7");
		String stationSql =
	            " SELECT DISTINCT (C.STATION_CODE) AS STATION_CODE FROM SYS_STATION C ,SYS_STADEP_LIST D WHERE C.STATION_CODE = D.STATION_CODE(+) ";
		sbuilder.append(" AND A.DS_STATION_CODE IN ( " + stationSql + " ) ");
		if(station_code != null && station_code.length() > 0) {
			sbuilder.append(" AND A.DS_STATION_CODE = '" + station_code +"'");
		}
		
		if(stat_date != null && stat_date.length() > 0 ) {
			if(type == 0) {  //日报
				stat_date = stat_date.replaceAll("-", "").substring(0, 8);
				sbuilder.append(" AND A.IN_DATE <= TO_DATE('" + stat_date + "','yyyy-MM-dd hh24:mi:ss') " +
						" AND A.DS_DATE >= TO_DATE('" + stat_date + "','yyyy-MM-dd hh24:mi:ss') ");
			} else {   //月
				stat_date = stat_date.replaceAll("-", "").substring(0, 6);
				sbuilder.append(" AND A.DS_DATE BETWEEN TO_DATE('" + setMonMaxDay(stat_date,"00") + "','yyyy-MM-dd hh24:mi:ss') ");
				sbuilder.append(" AND TO_DATE('" + getMonthMaxDay(stat_date) + "','yyyy-MM-dd hh24:mi:ss')");
			}
		}
		
		sbuilder.append(" GROUP BY A.DS_STATION_CODE");
		sbuilder.append(" ORDER BY A.DS_STATION_CODE");
		System.out.println(" child sbuilder::::::::: " + sbuilder.toString());
		return sbuilder.toString();
	}
	
	/**
	 * 联合查询SQL
	 * @param station_code
	 * @param stat_date
	 * @param type
	 * @return
	 */
	public String getUnionReportSQL(String station_code,String stat_date,int type) {
		
		String sql = "SELECT A.STATION_CODE, A.DATA_07 AS DATA_07, A.DATA_08 AS DATA_08, " +
				 "    A.DATA_08_1 AS DATA_08_1, A.DATA_09 AS DATA_09, A.DATA_11 AS DATA_11, " +
				 "    A.DATA_12 AS DATA_12, A.DATA_13 AS DATA_13, A.DATA_14 AS DATA_14, " +
				 "    A.DATA_15 AS DATA_15, A.DATA_15_1 AS DATA_15_1, A.DATA_16 AS DATA_16, " +
				 "    A.DATA_77 AS DATA_77, A.DATA_19 AS DATA_19, A.DATA_19_AVG AS DATA_19_AVG, " +
				 "    B.MR_NO AS DATA_77_1 ";
		StringBuilder sbuilder = new StringBuilder(sql);
		
		
		String from1 = "( SELECT  STATION_CODE, SUM (DATA_07) AS DATA_07,SUM (DATA_08) AS DATA_08, " +
					   " SUM (DATA_08_1) AS DATA_08_1, SUM (DATA_09) AS DATA_09,SUM (DATA_11) AS DATA_11, " +
					   " SUM (DATA_12) AS DATA_12,SUM (DATA_13) AS DATA_13,SUM (DATA_14) AS DATA_14," +
					   " SUM (DATA_15) AS DATA_15,SUM (DATA_15_1) AS DATA_15_1,SUM (DATA_16) AS DATA_16, " +
					   " SUM (DATA_16) AS DATA_77, SUM (DATA_19) AS DATA_19," +
					   " (CASE WHEN SUM (DATA_09) = 0 THEN 0 ELSE SUM (DATA_19) / SUM (DATA_09) END) AS DATA_19_AVG " +
				       " FROM STA_DAILY_01 WHERE STATION_CODE IN (SELECT DISTINCT (A.STATION_CODE) AS STATION_CODE FROM SYS_STATION A, SYS_STADEP_LIST B WHERE A.STATION_CODE = B.STATION_CODE(+)) " ;
		StringBuilder sbuilderFrom1 = new StringBuilder(from1);
		
		String from2 = "( SELECT C.DS_STATION_CODE AS STATION_CODE, COUNT(C.MR_NO) AS MR_NO FROM ADM_INP C, SYS_PATINFO D WHERE C.MR_NO = D.MR_NO  AND (EXTRACT(year FROM C.IN_DATE) - EXTRACT(year FROM D.BIRTH_DATE)) < 7 ";
		StringBuilder sbuilderFrom2 = new StringBuilder(from2);
		
		if(station_code != null && station_code.length() > 0) {  
			sbuilderFrom1.append(" AND STATION_CODE = '" + station_code + "'");
			sbuilderFrom2.append(" AND C.DS_STATION_CODE = '" + station_code +"'");
		}
		if(stat_date != null && stat_date.length() > 0) {
			System.out.println("stat_date:::::" + stat_date);
			if(type == 0) {   //日报
				stat_date = stat_date.replaceAll("-", "").substring(0, 8).trim();
				sbuilderFrom1.append(" AND TO_CHAR(STA_DATE) = '" + stat_date + "'");
				sbuilderFrom2.append(" AND C.IN_DATE <= TO_DATE('" + stat_date + "','yyyy-MM-dd hh24:mi:ss') " +
						" AND C.DS_DATE >= TO_DATE('" + stat_date + "','yyyy-MM-dd hh24:mi:ss') ");
			} else {    //月报
				int days = getMonthDays(stat_date);
				stat_date = stat_date.substring(0, 10).trim();
				String d1 = stat_date.replaceAll("-", "").substring(0, 6);
				sbuilderFrom1.append(" AND TO_CHAR(STA_DATE) >= '" + d1 + "01' AND "
								+ " TO_CHAR(STA_DATE) <='" + d1 + days +"'"
								);
				sbuilderFrom2.append(" AND C.DS_DATE BETWEEN TO_DATE('" + setMonMaxDay(stat_date,"-01") + "','yyyy-MM-dd hh24:mi:ss') ");
				sbuilderFrom2.append(" AND TO_DATE('" + getMonthMaxDay(stat_date) + "','yyyy-MM-dd hh24:mi:ss')");
			}
		}
		
		sbuilderFrom1.append(" GROUP BY STATION_CODE ) A ");
		sbuilderFrom2.append(" GROUP BY C.DS_STATION_CODE ) B");
		
		//System.out.println("sbuilderFrom1 ::::: " + sbuilderFrom1);
		//System.out.println("sbuilderFrom2 ::::: " + sbuilderFrom2);
		
		sbuilder.append(" FROM " + sbuilderFrom1 +"," + sbuilderFrom2);
		sbuilder.append(" WHERE A.STATION_CODE = B.STATION_CODE(+) ORDER BY  A.STATION_CODE");
		
		//System.out.println("sbuild ::::: " + sbuilder);
		
		return sbuilder.toString();
		
	}
	
	
	
	/**
	 * 获取每年和每个月的天数
	 * @return
	 */
	public int getMonthDays(String time) {
		int days = 30;
		time = time.substring(0, 7);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(sdf.parse(time));
			days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}catch (ParseException e) {
			e.printStackTrace();
		}
		return days;
	}
   
	/**
	 * 获取某个月的最大日期
	 * @param time
	 * @return
	 */
	public String getMonthMaxDay(String time) {
		time = time.substring(0,7);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(sdf.parse(time));
			int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			return setMonMaxDay(time,days);
		}catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	private String setMonMaxDay(String time, int days) {
		time = time.substring(0,7)  + "-" + days;
		return time;
	}
	
	private String setMonMaxDay(String time, String days) {
		time = time.substring(0,7)  + days;
		return time;
	}

}
