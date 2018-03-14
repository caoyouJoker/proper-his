package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.TableModel;

import jdo.sta.STAOutRecallTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title:��Ժ�ٻز�ѯ��
 * </p>
 * 
 * <p>
 * Description:��Ժ�ٻز�ѯ��
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
public class STAOutRecallControl extends TControl{
	
	private static final String TABLE= "TABLE";
	
	private String startDate;
	private String endDate;
	
	//********���������� start*********
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	//********���������� end*********
	
	public void onInit(){
		super.onInit();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		this.setValue("END_DATE", date);
		this.setValue("START_DATE", StringTool.rollDate(date, -7));
		this.setValue("RECALLNUM", "0");
		addListener(getTTable(TABLE));
		//System.out.println("start date :::::::::  " + StringTool.rollDate(date, -7));
	}
	
	/**
	 * Ϊ������������
	 * @param tTable :���
	 */
	private void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				//STAOutRecallControl.this.messageBox(sortColumn + "");
				// table.getModel().sort(ascending, sortColumn);
				if(sortColumn == 1 || sortColumn == 5){
					return;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				 //System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String startDate = this.getValueString("START_DATE");
		this.setStartDate(startDate.substring(0, 10));
		if(startDate != null && startDate.length() > 0) {
			startDate = startDate.substring(0, 10).replaceAll("-", "") + "000000";
		}
		String endDate = this.getValueString("END_DATE");
		this.setEndDate(endDate.substring(0, 10));
		if(endDate != null && endDate.length() > 0) {
			endDate = endDate.substring(0,10).replaceAll("-", "") + "235959";
		}
		//System.out.println("Date ::::::::::  " +startDate  +"  *****    :::::: " + endDate);
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		String sql = STAOutRecallTool.getNewInstance().getOutRecallSQL(parm);
		TParm reultParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable(TABLE).setParmValue(reultParm);
		if(reultParm.getCount() <= 0) {
			this.setValue("RECALLNUM", "0");
		} else {
			this.setValue("RECALLNUM", String.valueOf(reultParm.getCount()));
		}
		//System.out.println("result :::::::::  " + new TParm(TJDODBTool.getInstance().select(sql)));
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		this.setValue("END_DATE", date);
		this.setValue("START_DATE", StringTool.rollDate(date, -7));
		this.setValue("RECALLNUM", "0");
	}
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("�޵���Excel����");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table,"��Ժ�ٻ�ͳ�Ʊ�");
	}
	
	
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TTable table = this.getTTable(TABLE);
		if(table.getRowCount() <= 0){
			this.messageBox("�޿ɴ�ӡ����");
			return;
		}
		
		TParm data = new TParm();
		String zhi = "";
		if(StringUtils.isEmpty(getStartDate()) && StringUtils.isEmpty(getEndDate())) {
			zhi = "";
		} else {
			zhi = " �� ";
		}
		data.setData("STAT_DATE", "TEXT", this.getStartDate() + zhi + this.getEndDate());
		data.setData("RECALLNUM", "TEXT", this.getValueString("RECALLNUM"));
		data.setData("TITLE", "TEXT", "��Ժ�ٻ�ͳ�Ʊ�");
		
		//�������
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i < table.getRowCount(); i++) {
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("LAST_DS_DATE", tableParm.getData("LAST_DS_DATE", i));
			parm.addData("DS_DEPT_CODE", tableParm.getData("DS_DEPT_CODE", i));
			parm.addData("OPT_DATE", tableParm.getData("OPT_DATE", i));
			parm.addData("OPT_USER", tableParm.getData("OPT_USER", i));
		}
		parm.setCount(parm.getCount("MR_NO"));
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","LAST_DS_DATE");
		parm.addData("SYSTEM","COLUMNS","DS_DEPT_CODE");
		parm.addData("SYSTEM","COLUMNS","OPT_DATE");
		parm.addData("SYSTEM","COLUMNS","OPT_USER");
		//System.out.println("parm :::::::::::::  " + parm);
		data.setData("RECALLTABLE", parm.getData());
		
		data.setData("OPT_USER","TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\sta\\STAOutRecall.jhw", data);
	}
	
	/**
	 * ����TTable
	 * @param tag
	 * @return
	 */
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
