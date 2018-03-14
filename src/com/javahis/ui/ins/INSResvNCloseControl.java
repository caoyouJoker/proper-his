package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import jdo.adm.ADMResvTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ԤԼδ�᰸
 * </p>
 * 
 * <p>
 * Description: �ʸ�ȷ����ԤԼδ�᰸
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111128
 * @version 1.0
 */
public class INSResvNCloseControl extends TControl {
	private int selectrow = -1;// ѡ�����
	SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private String flg;// ���ֲ�ѯ����

	// ����
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	
	public void onInit() {
		super.onInit();
		
		TTable table = (TTable) this.getComponent("TABLE");
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = (TParm) getParameter();
		setValueForParm("REGION_CODE;DR_CODE;DEPT_CODE", recptype, -1);
		flg = recptype.getValue("FLG");// �߷�֧
		DateFormat df = new SimpleDateFormat("yyyy");
		if (flg.equals("Y")) {// ԤԼδ�᰸
			// Ԥ�����ʱ���
			String date = df.format(SystemTool.getInstance().getDate())
					+ "-01-01";
			this.callFunction("UI|STARTTIME|setValue", StringTool.getTimestamp(
					date, "yyyy-MM-dd"));
			this.callFunction("UI|ENDTIME|setValue", SystemTool.getInstance()
					.getDate());
		} else {// ����Ȳ�ѯ
			// TLabel A= new TLabel();
			this.setTitle("�����ҽ�����߲�ѯ");
			((TLabel) this.getComponent("LBL")).setValue("����Ȳ�ѯ:");
			callFunction("UI|ENDTIME|setVisible", false);
			callFunction("UI|tButton_1|setVisible", false);
			callFunction("UI|LBL1|setVisible", false);
			callFunction("UI|STARTTIME|setEnabled", false);
			String year = df.format(SystemTool.getInstance().getDate());
			int yearTemp = Integer.parseInt(year) - 1;// �������

			this.callFunction("UI|STARTTIME|setValue", StringTool.getTimestamp(
					yearTemp + "-12-31", "yyyy-MM-dd"));
		}
		// table1�ĵ��������¼�
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// table1�ĵ��������¼�
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");
		
		addListener(table);
		onQuery();
		

	}

	/**
	 *���Ӷ�Table�ļ���
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// ���������¼�
		this.callFunction("UI|TABLE|acceptText");
		// TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		selectrow = row;
	}

	public void onTableDoubleClicked(int row) {
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		this.setReturnValue(data.getRow(row));
		this.callFunction("UI|onClose");
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
		}
		if (this.getValueString("DR_CODE").length() > 0) {
			parm.setData("DR_CODE", this.getValue("DR_CODE"));
		}
		if (this.getValueString("DEPT_CODE").length() > 0) {
			parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		if (this.getValue("STARTTIME").toString().length() > 0) {
			parm.setData("START_DATE", df1.format(this.getValue("STARTTIME"))+"000000");
		}
		//����
		if (this.getValueString("STATION_CODE").length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE"));	
		}
		TParm result=null;
		System.out.println();
		if (flg.equals("Y")) {//ԤԼδ�᰸
			if (this.getValue("ENDTIME").toString().length() > 0) {
				parm.setData("END_DATE", df1.format(this.getValue("ENDTIME"))+"235959");
			}
			result = ADMResvTool.getInstance().queryResvNClose(parm);
		}else{//�����
			parm.setData("START_DATE", df1.format(this.getValue("STARTTIME"))+"235959");
			result = ADMResvTool.getInstance().overYearNHIPatInfo(parm);
		}
	   
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			err(result.getErrText() + ":" + result.getErrName());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("E0008");
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			table.removeRowAll();
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}

	/**
    *
    */
	public void onOK() {
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		this.setReturnValue(data.getRow(selectrow));
		this.callFunction("UI|onClose");
	}
	
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// �����parmֵһ��
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames,table);
			}
		});
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
			String columnNames,TTable table) {
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
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);
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
}
