package com.javahis.ui.adm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.adm.ADMResvTool;
import jdo.bil.BILComparator;
import jdo.sys.SystemTool;

/**
 * <p>Title: 预约病患统计 </p>
 *
 * <p>Description: 预约病患统计 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author WangLong 20120921
 * @version 1.0
 */
public class ADMResvStatisticsControl extends TControl {
	
	// =================排序辅助==============
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;
	
	   /**
     * 初始化方法
     */
    public void onInit(){
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("START_DATE",StringTool.getTimestamp(dateStr+" 00:00:00","yyyyMMdd hh:mm:ss"));
        this.setValue("END_DATE",sysDate);
		TTable table = (TTable) this.getComponent("TABLE");
		addSortListener(table);//加排序
    }
    /**
     * 查询
     */
    public void onQuery(){
		String admSource = this.getValueString("ADM_SOURCE");//病患来源
		Timestamp startDate = (Timestamp)this.getValue("START_DATE");// 开始时间
		Timestamp endDate = (Timestamp)this.getValue("END_DATE");// 结束日期
		String deptCode = this.getValueString("DEPT_CODE");// 科室
		String stationCode = this.getValueString("STATION_CODE");// 病区
		String drCode = this.getValueString("DR_CODE");// 医生

		TParm parm = new TParm();
		if (!StringUtil.isNullString(admSource)) {
			parm.setData("ADM_SOURCE", admSource);
		}

		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);

		
		if (!StringUtil.isNullString(deptCode)) {
			parm.setData("DEPT_CODE", deptCode);
		}
		if (!StringUtil.isNullString(stationCode)) {
			parm.setData("STATION_CODE", stationCode);
		}
		if (!StringUtil.isNullString(drCode)) {
			parm.setData("DR_CODE", drCode);
		}
		
		TParm admResvStatistics = ADMResvTool.getInstance().queryADMResvStatistics(parm);
		if (admResvStatistics.getErrCode() < 0) {
			messageBox(admResvStatistics.getErrText());
			return;
		}
		if (admResvStatistics.getCount() <= 0) {
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			this.clearValue("COUNT");
			return;
		}
		this.clearValue("TABLE");
		((TTextField) getComponent("COUNT")).setValue(admResvStatistics.getCount()+"");
		this.callFunction("UI|TABLE|setParmValue", admResvStatistics);
    }

    /**
     * 科室选择事件
     */
    public void onDEPT_CODE() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }
    
    /**
     * 导出
     */
    public void onExecl(){
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),"统计病人预约住院情况");
    }

    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("ADM_SOURCE;DEPT_CODE;STATION_CODE;DR_CODE");
        ((TTextField) getComponent("COUNT")).setValue("");
        onInit();
        this.getTTable("TABLE").setDSValue();
    }
    
	// ====================排序功能begin======================
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
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
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
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
	// ====================排序功能end======================
}
