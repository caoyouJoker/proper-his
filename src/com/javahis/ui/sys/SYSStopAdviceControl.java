package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSStopAdviceTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 长期医嘱停用通知
 * </p>
 * 
 * <p>
 * Description: 长期医嘱停用通知
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wu 2012-7-30下午05:08:44
 * @version 1.0
 */
public class SYSStopAdviceControl extends TControl {
	private TTable table;
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		this.table = (TTable) this.getComponent("TABLE");
		addListener(table);
	}
	/**
	 * 执行查询命令
	 */
	public void onQuery(){
		String all = this.getValueString("ALL");
		String medicine = this.getValueString("MEDICINE");
		String condition = "";
		if(!"Y".equals(all)){
			if("Y".equals(medicine)){
				condition = "A.CAT1_TYPE='PHA' AND ";
			}else{
				condition = "A.CAT1_TYPE<>'PHA' AND ";
			}
		}
		TParm parm = new TParm();
		parm.setData("condition", condition);
		TParm result = SYSStopAdviceTool.getInstance().onQuery(parm);
		if (result.getCount("ORDER_CODE") < 1) {
			this.table.removeRowAll();
			this.messageBox("E0008");
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * 把表格中的数据导出excel
	 */
	public void onExport(){
		TParm parm=table.getParmValue();
    	if (parm == null ||parm.getCount("ORDER_CODE")<=0) {
    		this.messageBox("没有需要导出的数据");
			return;
		}
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "长期医嘱停用通知");
	}
	/**
	 * 打印表格中显示的数据
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue();
		if (tableParm == null || tableParm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有要打印的数据");
            return;
        }
        TParm printData = new TParm();
        TParm T1 = new TParm();
        int tableCount = tableParm.getCount("ORDER_CODE");
        for (int i = 0; i < tableCount; i++) {
            T1.setRowData(i, tableParm.getRow(i));
            Timestamp data = (Timestamp) T1.getData("BILL_CREATE_DATE", i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(data);
            T1.setData("BILL_CREATE_DATE", i, date);
        }
        T1.setCount(tableCount);
        T1.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
        T1.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        T1.addData("SYSTEM", "COLUMNS", "MR_NO");
        T1.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BED_NO");
        T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        T1.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        T1.addData("SYSTEM", "COLUMNS", "TREAT_DOC_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BILL_CREATE_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BILL_CREATE_DATE");
        printData.setData("T1", T1.getData());
        printData.setData("printDate", "TEXT",
                          StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd"));
        printData.setData("printUser", "TEXT", Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\SYSStopAdvice.jhw",
                             printData);
	}
	public void onClear(){
		((TRadioButton)this.getComponent("ALL")).setSelected(true);
		((TRadioButton)this.getComponent("MEDICINE")).setSelected(false);
		((TRadioButton)this.getComponent("NOT_MEDICINE")).setSelected(false);
		this.table.removeRowAll();
		
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
		
	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * vectory转成param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
