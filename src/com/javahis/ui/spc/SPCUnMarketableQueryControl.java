package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.SPCMarketQueryTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 滞销药品查询
 * </p>
 * 
 * <p>
 * Description: 滞销药品查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author shendr 2013.10.18
 * @version 1.0
 */
public class SPCUnMarketableQueryControl extends TControl {

	/**
	 * TABLE控件
	 */
	private TTable tableA;// 滞销
	private TTable tableB;// 未尽销
	private TTable tableC;// 滞销

	/** 排序 */
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * 初始化
	 */
	public void onInit() {
		tableA = getTTable("TABLE_A");
		tableB = getTTable("TABLE_B");
		tableC = getTTable("TABLE_C");
		addListener(getTTable("TABLE_A"));
		addListener(getTTable("TABLE_B"));
		addListener(getTTable("TABLE_C"));
		initTime("A");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.setValue("ORG_CODE", Operator.getDept());
	}

	/**
	 * 初始化时间
	 * 
	 * @param type
	 */
	private void initTime(String type) {
		// 初始化统计区间(默认带3个月之前到今日的)
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		if ("A".equals(type)) {
			// 结束时间
			setValue("END_TIME", StringTool.getTimestamp(date.toString()
					.substring(0, 10)
					+ " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
			// 起始时间
			setValue("START_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
			// 入库时间
			setValue("IN_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		} else if ("B".equals(type)) {
			// 入库时间
			setValue("START_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		} else if ("C".equals(type)) {
			// 查询时间
			setValue("START_TIME", date);
		}
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (!queryCheck())
			return;
		String start_time = "";
		String end_time = "";
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		int day = this.getValueInt("DAY");
		String org_code = this.getValueString("ORG_CODE");
		if (getRadioButton("TYPEA").isSelected()) {
			start_time = this.getValueString("START_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
			end_time = this.getValueString("END_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
		} else {
			start_time = (StringTool.getTimestamp(
					StringTool.rollDate(date, -day)).toString().replaceAll("-",
					"").trim().substring(0, 8));
			end_time = this.getValueString("END_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
		}
		String in_time = this.getValueString("IN_TIME").replaceAll("-", "")
				.trim().substring(0, 8);
		String order_code = this.getValueString("ORDER_CODE");
		TParm parm = new TParm();
		parm.setData("ORG_CODE", org_code);
		parm.setData("START_TIME", start_time);
		parm.setData("END_TIME", end_time);
		parm.setData("IN_TIME", in_time);
		parm.setData("ORDER_CODE", order_code);
		TTable table = new TTable();
		TParm result = new TParm();
		if (tableA.isShowing()) {
			table = tableA;
			result = SPCMarketQueryTool.getInstance().queryByTypeA(parm);
		} else if (tableB.isShowing()) {
			table = tableB;
			result = SPCMarketQueryTool.getInstance().queryByTypeB(parm);
		} else if (tableC.isShowing()) {
			table = tableC;
			result = SPCMarketQueryTool.getInstance().queryByTypeC(parm);
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("E0008");
			onClear();
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * 查询前检查
	 */
	public boolean queryCheck() {
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String in_time = this.getValueString("IN_TIME");
		String day = this.getValueString("DAY");
		int iDay = this.getValueInt("DAY");
		if (tableA.isShowing()) {
			if (getRadioButton("TYPEA").isSelected()) {
				if (StringUtil.isNullString(start_time)
						|| StringUtil.isNullString(end_time)) {
					messageBox("请选择查询区间");
					return false;
				}
				if (StringUtil.isNullString(in_time)) {
					messageBox("请选择入库日期");
					return false;
				}
			} else {
				if (StringUtil.isNullString(day) || iDay <= 0) {
					messageBox("请选择查询天数");
					return false;
				}
			}
		} else if (tableB.isShowing()) {
			if (StringUtil.isNullString(start_time)) {
				messageBox("请选择入库日期");
				return false;
			}
		} else if (tableC.isShowing()) {
			if (StringUtil.isNullString(start_time)) {
				messageBox("请选择查询日期");
				return false;
			}
		}
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		String tags = "ORG_CODE;ORDER_CODE;ORDER_DESC;DAY";
		clearValue(tags);
		if (tableA.isShowing()) {
			initTime("A");
		} else if (tableB.isShowing()) {
			initTime("B");
		} else if (tableC.isShowing()) {
			initTime("C");
		}
		tableA.removeRowAll();
		tableB.removeRowAll();
		tableC.removeRowAll();
		onTableChanged();
		onRadioChanged();
	}

	/**
	 * 页签切换事件
	 */
	public void onTableChanged() {
		if (tableA.isShowing()) {
			getTLabel("LABEL_TIME").setText("查询区间:");
			getTLabel("LABEL").setVisible(true);
			getTLabel("LABEL_IN_TIME").setVisible(true);
			getTLabel("LABEL_DAY").setVisible(true);
			getTTextFormat("IN_TIME").setVisible(true);
			getTTextFormat("END_TIME").setVisible(true);
			getTextField("DAY").setVisible(true);
			getRadioButton("TYPEA").setVisible(true);
			getRadioButton("TYPEB").setVisible(true);
			initTime("A");
		} else if (tableB.isShowing()) {
			getTLabel("LABEL_TIME").setText("入库日期 <=");
			getTLabel("LABEL").setVisible(false);
			getTLabel("LABEL_IN_TIME").setVisible(false);
			getTLabel("LABEL_DAY").setVisible(false);
			getTTextFormat("IN_TIME").setVisible(false);
			getTTextFormat("END_TIME").setVisible(false);
			getTextField("DAY").setVisible(false);
			getRadioButton("TYPEA").setVisible(false);
			getRadioButton("TYPEB").setVisible(false);
			initTime("B");
		} else if (tableC.isShowing()) {
			getTLabel("LABEL_TIME").setText("查询日期:");
			getTLabel("LABEL").setVisible(false);
			getTLabel("LABEL_IN_TIME").setVisible(false);
			getTLabel("LABEL_DAY").setVisible(false);
			getTTextFormat("IN_TIME").setVisible(false);
			getTTextFormat("END_TIME").setVisible(false);
			getTextField("DAY").setVisible(false);
			getRadioButton("TYPEA").setVisible(false);
			getRadioButton("TYPEB").setVisible(false);
			initTime("C");
		}
	}

	/**
	 * 查询区间选项切换事件
	 */
	public void onRadioChanged() {
		if (getRadioButton("TYPEA").isSelected()) {
			getTextField("DAY").setEnabled(false);
			setValue("DAY", "");
		} else {
			getTextField("DAY").setEnabled(true);
		}
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (tableA.isShowing()) {
			if (tableA.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			} else {
				ExportExcelUtil.getInstance().exportExcel(tableA, "滞销药品查询");
			}
		} else if (tableB.isShowing()) {
			if (tableB.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			} else {
				ExportExcelUtil.getInstance().exportExcel(tableB, "未尽销药品查询");
			}
		} else if (tableC.isShowing()) {
			if (tableC.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			} else {
				ExportExcelUtil.getInstance().exportExcel(tableC, "滞销药品查询");
			}
		}
	}

	/**
	 * 获取TABLE控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获取TTextField控件
	 * 
	 * @param string
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * 获取TLabel控件
	 * 
	 * @param string
	 * @return
	 */
	public TLabel getTLabel(String tag) {
		return (TLabel) getComponent(tag);
	}

	/**
	 * 获取TTextFormat控件
	 * 
	 * @param string
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

	/**
	 * 获取TRadioButton控件
	 * 
	 * @param tag
	 * @return
	 */
	public TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	// =================================排序功能开始==================================
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断
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
				TParm totAmtRow = tableData.getRow(tableData
						.getCount("ORDER_DESC") - 1);// add by wanglong 20130108
				tableData.removeRow(tableData.getCount("ORDER_DESC") - 1);// add
				// by
				// wanglong
				// 20130108
				// System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
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
				TParm lastResultParm = new TParm();// 记录最终结果
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
				for (int k = 0; k < columnName.length; k++) {// add by wanglong
					// 20130108
					lastResultParm.addData(columnName[k], totAmtRow
							.getData(columnName[k]));
				}
				lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add
				// by
				// wanglong
				// 20130108
				table.setParmValue(lastResultParm);
			}
		});
	}

	/**
	 * 列名转列索引值
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
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * vectory转成param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		return parmTable;
	}
	// ================================排序功能结束==================================

}
