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
 * �������תͳ��
 * @author wangqing 20180108
 *
 */
public class STAStationNewUIControl extends TControl {

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		Timestamp time = SystemTool.getInstance().getDate();
		this.setValue("STA_DATE1", StringTool.rollDate(time, -1));// ǰһ��
		this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());// ���ó�ʼʱ�䣨�ϸ��£�
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
		if (t.getSelectedIndex() == 0) { // �ձ�
			this.onQueryDay();
		} else if (t.getSelectedIndex() == 1) { // �±�
			this.onQueryMonth();		
		}
	}

	/**
	 * �ձ���ѯ
	 */
	public void onQueryDay(){	
		TTable table = (TTable) this.getComponent("TableDay");
		table.removeRowAll();
		Timestamp queryDate = (Timestamp) this.getValue("STA_DATE1");
		if(queryDate==null || queryDate.toString().trim().length()<=0){
			this.messageBox("��ѯ���ڲ���Ϊ��");
			return;
		}
		Timestamp firstDay = StringTool.rollDate(queryDate, 1);
		String queryDateStr = StringTool.getString(queryDate, "yyyyMMdd");
		String firstDayStr = StringTool.getString(firstDay, "yyyyMMdd");
		//		System.out.println("{queryDateStr:"+queryDateStr+";lastDayStr:"+lastDayStr+"}");
		String stationCode = this.getValueString("STATION_CODE1");
		// �벡������
		String sql1 = "SELECT IN_STATION_CODE AS STATION_CODE, COUNT(CASE_NO) AS IN_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE substr(IN_DATE, 0, 8)='"+queryDateStr+"' @ GROUP BY IN_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql1 = sql1.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql1 = sql1.replace("@", "");
		}
		System.out.println("{�벡������sql1:"+sql1+"}");
		// ����������
		String sql2 = "SELECT OUT_STATION_CODE as STATION_CODE, COUNT(CASE_NO) AS OUT_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE to_char(OUT_DATE, 'yyyyMMdd')='"+queryDateStr+"' @ GROUP BY OUT_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql2 = sql2.replace("@", " AND OUT_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql2 = sql2.replace("@", "");
		}
		System.out.println("{����������sql2:"+sql2+"}");
		// �ڳ�ʵ������
		String sql3 = "SELECT distinct CASE_NO, IN_STATION_CODE as STATION_CODE, DS_DATE "
				+ "FROM ADM_DAILY_ZERO_INP "
				+ "WHERE DS_DATE IS NULL AND to_char(DAILY_DATE, 'yyyyMMdd')='"+queryDateStr+"' @ ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql3 = sql3.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql3 = sql3.replace("@", "");
		}
		sql3 = "SELECT STATION_CODE, COUNT(CASE_NO) AS BEGIN_REAL_NO FROM ("+sql3+") GROUP BY STATION_CODE ";		
		System.out.println("{�ڳ�ʵ������sql3:"+sql3+"}");
		// ��������������ĩʵ��������			
		String sql4 = "SELECT distinct CASE_NO, IN_STATION_CODE as STATION_CODE, DAILY_DATE "
				+ "FROM ADM_DAILY_ZERO_INP "
				+ "WHERE DS_DATE IS NULL AND to_char(DAILY_DATE, 'yyyyMMdd')='"+firstDayStr+"' @ ";	
		if(stationCode!=null && stationCode.trim().length()>0){
			sql4 = sql4.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql4 = sql4.replace("@", "");
		}
		sql4 = "SELECT STATION_CODE, COUNT(CASE_NO) AS END_REAL_NO FROM ("+sql4+") GROUP BY STATION_CODE ";
		System.out.println("{��������������ĩʵ��������sql4:"+sql4+"}");
		// ����
		String sql5 = "SELECT STATION_CODE, STATION_DESC FROM SYS_STATION @ ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql5 = sql5.replace("@", " WHERE STATION_CODE ='"+stationCode+"' ");
		}else{	
			sql5 = sql5.replace("@", "");
		}
		System.out.println("{����sql5:"+sql5+"}");
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
		System.out.println("{��ѯ���ת����sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		//		System.out.println("{result:"+result+"}");
		table.setParmValue(result);	
	}

	/**
	 * �±���ѯ
	 */
	public void onQueryMonth(){
		TTable table = (TTable) this.getComponent("TableMouth");
		table.removeRowAll();
		Timestamp queryDate = (Timestamp) this.getValue("STA_DATE2");
		if(queryDate==null || queryDate.toString().trim().length()<=0){
			this.messageBox("��ѯ���ڲ���Ϊ��");
			return;
		}
		String queryDateStr = StringTool.getString(queryDate, "yyyyMM");
		String StartDate = queryDateStr + "01";// ÿ�µ�һ��
		String EndDate = StringTool.getString(STATool.getInstance().getLastDayOfMonth(queryDateStr), "yyyyMMdd");// ��ȡ���·ݵ����һ��
		//		System.out.println("{StartDate:"+StartDate+";EndDate:"+EndDate+"}");
		String stationCode = this.getValueString("STATION_CODE2");
		// �벡������
		String sql1 = "SELECT IN_STATION_CODE AS STATION_CODE, COUNT(CASE_NO) AS IN_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE substr(IN_DATE, 0, 8)>='"+StartDate+"' AND substr(IN_DATE, 0, 8)<='"+EndDate+"' @ GROUP BY IN_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql1 = sql1.replace("@", " AND IN_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql1 = sql1.replace("@", "");
		}
		System.out.println("{�벡������sql1:"+sql1+"}");
		// ����������
		String sql2 = "SELECT OUT_STATION_CODE as STATION_CODE, COUNT(CASE_NO) AS OUT_STATION_NO "
				+ "FROM ADM_TRANS_LOG "
				+ "WHERE to_char(OUT_DATE, 'yyyyMMdd')>='"+StartDate+"' AND substr(OUT_DATE, 0, 8)<='"+EndDate+"' @ GROUP BY OUT_STATION_CODE ";
		if(stationCode!=null && stationCode.trim().length()>0){
			sql2 = sql2.replace("@", " AND OUT_STATION_CODE ='"+stationCode+"' ");
		}else{
			sql2 = sql2.replace("@", "");
		}
		System.out.println("{����������sql2:"+sql2+"}");
		// ��������������ĩʵ��������			
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
		System.out.println("{ʵ��ռ���ܴ�������sql4:"+sql4+"}");
		// ����
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
		System.out.println("{��ѯ���ת����sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		//		System.out.println("{result:"+result+"}");
		table.setParmValue(result);	

	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
		TTable table;
		if (t.getSelectedIndex() == 0) { // �ձ�
			table = (TTable) this.getComponent("TableDay");
			if (table.getRowCount()<1) {
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "�������תͳ���ձ�");
		} else if (t.getSelectedIndex() == 1) { // �±�
			table = (TTable) this.getComponent("TableMouth");	
			if (table.getRowCount()<1) {
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "�������תͳ���±�");
		}		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
