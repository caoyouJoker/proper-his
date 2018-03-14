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
 * Title:0����Ժ������ϸ��
 * </p>
 * 
 * <p>
 * Description:0����Ժ������ϸ��
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
	 * ��ѯ
	 */
	public void onQuery() { 
		TParm parm = new TParm();
		String date = this.getValueString("DAILY_DATE");
		if(StringUtils.isEmpty(date)) {
			this.messageBox("�������ѯ����");
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
			this.messageBox("��ȡ0����Ժ����ʧ�ܣ�");
			this.setValue("ZEROCOUNT", "0");
			return;
		}
		if(result.getCount() <= 0) {
			this.setValue("ZEROCOUNT", "0");
			this.messageBox("����0������Ժ���ˣ�");
		} else {
			this.setValue("ZEROCOUNT", String.valueOf(result.getCount()));
		}
		
		
		this.filterTParmData(result);
		
		this.getTTable(TABLE).setParmValue(result);
	}
	/**
	 * ���˲�ѯ���Ĳ���������Ϣ����סԺ���ڼ��������סԺ���������뵽��ѯ������TParm��
	 * 
	 * @param parm
	 *            TParm ��Ҫ���˵�����
	 * @return TParm
	 */
	public TParm filterTParmData(TParm parm) {
		// System.out.println("����TABLE����"+parm);
		/*
		 * ����ѭ��������������е����������䣬��Ժ������סԺ����
		 * (�����ֶ�:SYS_PATINFO.BIRTH_DATE��ӦKEY(AGE),��Ժ�����ֶ�
		 * :ADM_INP.IN_DATE��ӦKEY(DAYNUM))
		 */
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp;
		Timestamp tp;
		// ��������
		String age = "0";
		// ���ص�����
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
			// ����סԺ����
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
	 * ���
	 */
	public void onClear() {
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		Timestamp date = StringTool.getTimestamp(new Date());
		this.clearValue("DEPT_CODE;STATION_CODE");
		this.setValue("DAILY_DATE", date);
		this.setValue("ZEROCOUNT", "0");
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("�޿ɴ�ӡ���ݣ�");
			return;
		}
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", "0����Ժ������ϸ��");
		data.setData("DAILY_DATE", "TEXT", this.getDailyDate());
		data.setData("ZEROCOUNT", "TEXT", this.getValueString("ZEROCOUNT") + "��");
		data.setData("DEPT_CODE", "TEXT", this.getDept());
		data.setData("STATION_CODE", "TEXT", this.getStation());
		
		//�������
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i < table.getRowCount(); i++) {
			parm.addData("BED_NO_DESC", tableParm.getData("BED_NO_DESC",i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME",i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
//			parm.addData("SEX_CODE", "1".equals(tableParm.getData("SEX_CODE",i)) ? "��" : "Ů");
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
	 * ����Excel
	 */
	public void onExport() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("�޵���Excel���ݣ�");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "0����Ժ������ϸ��");
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
