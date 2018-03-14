package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sta.STAStationReportTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:����ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Description:����ͳ�Ʊ���
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
 * @author wukai 2016-7-15
 * @version JavaHis 1.0
 */
public class STAStationReportControl extends TControl{
	
	
	private String date1 = "";
	private String date2 = "";
	
	@Override
	public void onInit() {
		super.onInit();
		oninit();
	}
	
	private void oninit() {
		Timestamp time = SystemTool.getInstance().getDate();
		this.clearValue("STATION_CODE1;STAT_DATE1");
		this.setValue("STAT_DATE1", StringTool.rollDate(time, -1));
		this.callFunction("UI|TABLE_DAY|setParmValue", new TParm());
		this.clearValue("STATION_CODE2;STAT_DATE2");
		this.setValue("STAT_DATE2", STATool.getInstance().getLastMonth());
		this.callFunction("UI|TABLE_MONTH|setParmValue", new TParm());
	}

	/**
	 * ��ȡtable
	 * @param tag
	 * @return
	 */
	public  TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}
	
	/**
	 * ��ȡTextFormat
	 * @param tag
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TTabbedPane tp = (TTabbedPane) this.getComponent("tTabbedPane");
		TTable table = null;
		if(tp.getSelectedIndex() == 0) {   //�Ո�
			table = this.getTTable("TABLE_DAY");
			String station_code = this.getValueString("STATION_CODE1");
			String stat_date = this.getValueString("STAT_DATE1");
			setDate1(stat_date);
			this.TableBind(table, station_code, stat_date,0);
		} else {   //��
			table = this.getTTable("TABLE_MONTH");
			String station_code = this.getValueString("STATION_CODE2");
			String stat_date = this.getValueString("STAT_DATE2");
			setDate2(stat_date);
			this.TableBind(table, station_code, stat_date,1);
		}
		
	}
	
	/**
	 * ���ݰ�
	 */
	private void TableBind(TTable table,String station_code,String stat_date,int type) {
		
		String sql = STAStationReportTool.getNewInstance().getUnionReportSQL(station_code, stat_date,type); 
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm == null || parm.getCount() <=0) {
	         this.messageBox("û�в�ѯ����");
	         table.setParmValue(new TParm());
	         return;
		}
		table.setParmValue(parm);
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		TTabbedPane tp = (TTabbedPane) this.getComponent("tTabbedPane");
		Timestamp time = SystemTool.getInstance().getDate();
		if(tp.getSelectedIndex() == 0) {
			this.clearValue("STATION_CODE1;STAT_DATE1");
			this.setValue("STAT_DATE1", StringTool.rollDate(time, -1));
			this.callFunction("UI|TABLE_DAY|setParmValue", new TParm());
		} else {
			this.clearValue("STATION_CODE2;STAT_DATE2");
			this.setValue("STAT_DATE2", STATool.getInstance().getLastMonth());
			this.callFunction("UI|TABLE_MONTH|setParmValue", new TParm());
		}
	}
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		TTabbedPane tp = (TTabbedPane) this.getComponent("tTabbedPane");
		TTable table = null;
		String stat_type = "";
		String date = "";
		if(tp.getSelectedIndex() == 0) {
			table = this.getTTable("TABLE_DAY");
			stat_type = "�ձ�";
		} else {
			table = this.getTTable("TABLE_MONTH");
			stat_type = "�±�";
		}
		
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿�������ݣ�");
			return;
		}
		if(tp.getSelectedIndex() == 0) {
			if(getDate1() == null || getDate1().length() <= 0) {
				date ="����ʱ��";
			} else {
				date = getDate1().substring(0, 10);
			}
		} else {
			if(getDate2() == null || getDate2().length() <= 0) {
				date ="����ʱ��";
			} else {
				date = getDate2().substring(0, 7);
			}
		}
		ExportExcelUtil.getInstance().exportExcel(table,"�ʿ�ͳ�Ʊ���-" + stat_type +  " ͳ��ʱ�䣺" + date + " ");
		
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TTabbedPane tp = (TTabbedPane) this.getComponent("tTabbedPane");
		TTable table = null;
		String stat_type = "";
		String date = "";
		if(tp.getSelectedIndex() == 0) {  //�ձ���
			table = this.getTTable("TABLE_DAY");
			stat_type = "�ձ�";
		} else {  //�±���
			table = this.getTTable("TABLE_MONTH");
			stat_type = "�±�";
		}
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿ɴ�ӡ���ݣ�");
			return;
		}
		
		TParm data = new TParm();
		if(tp.getSelectedIndex() == 0) {
			if(getDate1() == null || getDate1().length() <= 0) {
				date ="����ʱ��";
			} else {
				date = getDate1().substring(0, 10);
			}
		} else {
			if(getDate2() == null || getDate2().length() <= 0) {
				date ="����ʱ��";
			} else {
				date = getDate2().substring(0, 7);
			}
		}
		
		
		//��ͷ����
		data.setData("Title", "TEXT", "�ʿ�ͳ�Ʊ���");
		data.setData("STAT_DATE", "TEXT", date);
		data.setData("STAT_TYPE", "TEXT", stat_type);
		
		//�������
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i< table.getRowCount() ;i ++) {
			parm.addData("STATION_CODE",  tableParm.getData("STATION_CODE", i));
			parm.addData("DATA_07",  tableParm.getData("DATA_07", i));
			parm.addData("DATA_08",  tableParm.getData("DATA_08", i));
			parm.addData("DATA_08_1",  tableParm.getData("DATA_08_1", i));
			parm.addData("DATA_09",  tableParm.getData("DATA_09", i));
			parm.addData("DATA_11",  tableParm.getData("DATA_11", i));
			parm.addData("DATA_12",  tableParm.getData("DATA_12", i));
			parm.addData("DATA_13",  tableParm.getData("DATA_13", i));
			parm.addData("DATA_14",  tableParm.getData("DATA_14", i));
			parm.addData("DATA_15",  tableParm.getData("DATA_15", i));
			parm.addData("DATA_15_1", tableParm.getData("DATA_15_1", i));
			parm.addData("DATA_16",  tableParm.getData("DATA_16", i));
			parm.addData("DATA_77", tableParm.getData("DATA_77", i));
			parm.addData("DATA_77_1", tableParm.getData("DATA_77_1", i));
			parm.addData("DATA_19", tableParm.getData("DATA_19", i));
			parm.addData("DATA_19_AVG", tableParm.getData("DATA_19_AVG", i));
		}
		
		parm.setCount(parm.getCount("STATION_CODE"));
		parm.addData("SYSTEM","COLUMNS","STATION_CODE");
		parm.addData("SYSTEM","COLUMNS","DATA_07");
		parm.addData("SYSTEM","COLUMNS","DATA_08");
		parm.addData("SYSTEM","COLUMNS","DATA_08_1");
		parm.addData("SYSTEM","COLUMNS","DATA_09");
		parm.addData("SYSTEM","COLUMNS","DATA_11");
		parm.addData("SYSTEM","COLUMNS","DATA_12");
		parm.addData("SYSTEM","COLUMNS","DATA_13");
		parm.addData("SYSTEM","COLUMNS","DATA_14");
		parm.addData("SYSTEM","COLUMNS","DATA_15");
		parm.addData("SYSTEM","COLUMNS","DATA_15_1");
		parm.addData("SYSTEM","COLUMNS","DATA_16");
		parm.addData("SYSTEM","COLUMNS","DATA_77");
		parm.addData("SYSTEM","COLUMNS","DATA_77_1");
		parm.addData("SYSTEM","COLUMNS","DATA_19");
		parm.addData("SYSTEM","COLUMNS","DATA_19_AVG");
		
		data.setData("TABLE", parm.getData());
		
		data.setData("OPT_USER","TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		this.openPrintDialog("%ROOT%\\config\\prt\\sta\\STAStationReport.jhw", data);
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date) {
		this.date1 = date;
	}
	
	public String getDate2() {
		return date2;
	}

	public void setDate2(String date) {
		this.date2 = date;
	}

	
}
