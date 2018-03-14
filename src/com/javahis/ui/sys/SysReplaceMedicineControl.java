/**
 * 
 */
package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import jdo.ind.INDSQL;
import jdo.sys.Operator;
import jdo.sys.ReplaceMedicineTool;
import jdo.sys.SysReplaceMedicineSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 替代药管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-6-13下午12:06:36
 * @version 1.0
 */
public class SysReplaceMedicineControl extends TControl {
	private TTable table;

	public void onInit() {
		super.init();
		// 给表格注册单击事件
		// callFunction("UI|TABLE|addEventListener",
		// "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
		this.table = this.getTable("TABLE");
		initPage();
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
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
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE1").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC1").setValue(order_desc);
	}

	/**
	 * 接受返回值方法2
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code2 = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code2))
			getTextField("ORDER_CODE2").setValue(order_code2);
		String ORDER_DESC2 = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(ORDER_DESC2))
			getTextField("ORDER_DESC2").setValue(ORDER_DESC2);
	}

	/**
	 * 查詢方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void onQuery() {
		TTable table = (TTable) getComponent("TABLE");
		TParm parm = new TParm();
		String orderCode1 = this.getTextField("ORDER_CODE1").getValue();
		if (!"".equals(orderCode1.trim())) {
			parm.setData("ORDER_CODE", orderCode1);
		}
		String orderCode2 = this.getTextField("ORDER_CODE2").getValue();
		if (!"".equals(orderCode2.trim())) {
			parm.setData("REPLACE_ORDER_CODE", orderCode2);
		}
		TParm result = ReplaceMedicineTool.getInstance().onQuery(parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
		table.setParmValue(result);
	}

	/**
	 * 保存方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void onSave() {
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parmQ = new TParm();
		parmQ.setData("ORDER_CODE", this.getTextField("ORDER_CODE1").getValue());
		parmQ.setData("REPLACE_ORDER_CODE", this.getTextField("ORDER_CODE2")
				.getValue());
		parmQ.setData("SEQ", this.getTextField("SEQ").getValue());
		parmQ.setData("DESCRIPTION", this.getTextField("NOTE").getValue());
		parmQ.setData("OPT_USER", Operator.getName());
		parmQ.setData("OPT_DATE", date);
		parmQ.setData("OPT_TERM", Operator.getIP());
		String sql = SysReplaceMedicineSQL.onQueryByCode(parmQ);
		System.out.println("sql: " + sql);
		TParm resultQ = new TParm(TJDODBTool.getInstance().select(sql));
		// TParm resultQ
		// =ReplaceMedicineTool.getInstance().onQueryByCode(parmQ);
		if (resultQ == null || resultQ.getCount() < 0) {
			TParm resultU = ReplaceMedicineTool.getInstance().onSave(parmQ);

			if (resultU == null || resultU.getErrCode() < 0) {
				this.messageBox("保存失敗！");
				this.getTextField("ORDER_CODE1").setEditable(true);
				return;
			}
			this.messageBox("P0001");
			this.onClear();
			this.onQuery();
			return;
		} else {
			TParm resultB = ReplaceMedicineTool.getInstance().onUpdateByCode(
					parmQ);
			if (resultB == null || resultB.getErrCode() < 0) {
				this.messageBox("保存失敗！");
				return;
			} else {
				this.messageBox("保存成功！");
				this.getTextField("ORDER_CODE1").setEditable(true);
				this.onClear();
				this.onQuery();
			}
		}

	}

	// public void saveNow() {
	// if (!CheckData())
	// return;
	// Timestamp date = StringTool.getTimestamp(new Date());
	// TParm parm = new TParm();
	// parm.setData("ORDER_CODE", this.getTextField("ORDER_CODE1").getValue());
	// parm.setData("REPLACE_ORDER_CODE", this.getTextField("ORDER_CODE2")
	// .getValue());
	// String seq = this.getTextField("SEQ").getValue();
	// if ("".equals(seq.trim())) {
	// Vector v = (Vector) ReplaceMedicineTool.getInstance().onQuerySEQ()
	// .getData("MAXSEQ");
	// Long m = (Long) v.get(0);
	// parm.setData("SEQ", m + 1);
	// } else {
	// parm.setData("SEQ", seq);
	// }
	// parm.setData("DESCRIPTION", this.getTextField("NOTE").getValue());
	// parm.setData("OPT_USER", Operator.getName());
	// parm.setData("OPT_DATE", date);
	// parm.setData("OPT_TERM", Operator.getIP());
	// TParm resultS = ReplaceMedicineTool.getInstance().onSave(parm);
	// if (resultS.getErrCode() < 0) {
	// super.messageBox(resultS.getErrText());
	// } else {
	// this.onClear();
	// this.onQuery();
	// }
	// }

	/**
	 * 清空
	 */
	public void onClear() {
		setValue("ORDER_CODE1", "");
		setValue("ORDER_CODE2", "");
		setValue("ORDER_DESC1", "");
		setValue("ORDER_DESC2", "");
		setValue("SEQ", "");
		setValue("NOTE", "");
		this.getTextField("ORDER_CODE1").setEditable(true);
		table.removeRowAll();
		initPage();
	}

	/**
	 * 删除选中行
	 */
	public void onDelete() {
		if (messageBox("提示", "确定删除吗？", YES_NO_OPTION) != 0)
			return;
		TParm parm = new TParm();
		this.table = (TTable) getComponent("TABLE");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0) {
			this.messageBox("请先选择要删除的行！");
			return;
		}
		TParm tableParm = table.getParmValue();
		String orderCode = tableParm.getValue("ORDER_CODE", selectRow);
		String replaceOrderCode = tableParm.getValue("REPLACE_ORDER_CODE",
				selectRow);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("REPLACE_ORDER_CODE", replaceOrderCode);
		TParm result = ReplaceMedicineTool.getInstance().onDelete(parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.onClear();
		table.removeRow(selectRow);
	}

	/**
	 * 检查数据
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("ORDER_CODE1").trim())) {
			this.messageBox("药品不能为空");
			return false;
		}
		if ("".equals(getValueString("ORDER_CODE2").trim())) {
			this.messageBox("替换药不能为空");
			return false;
		}

		String seq = (String) this.getValue("SEQ");
		if (!"".equals(seq.trim())) {
			Pattern pattern = Pattern.compile("[0-9]*");
			if (!pattern.matcher(seq).matches()) {
				this.messageBox("序号必须为数字");
				return false;
			}

		}
		return true;
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		TTable table = getTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("date", "TEXT", SystemTool.getInstance().getDate()
				.toString().substring(0, 10).replace('-', '/'));
		data.setData("user", "TEXT", Operator.getName());
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
			parm.addData("ORDER_DESC1", tableParm.getValue("ORDER_DESC1", i));
			parm.addData("REPLACE_ORDER_CODE",
					tableParm.getValue("REPLACE_ORDER_CODE", i));
			parm.addData("ORDER_DESC2", tableParm.getValue("ORDER_DESC2", i));
			parm.addData("SEQ", tableParm.getValue("SEQ", i));
			parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
			parm.addData("OPT_USER", tableParm.getValue("OPT_USER", i));
			parm.addData("OPT_DATE", tableParm.getValue("OPT_DATE", i)
					.toString().substring(0, 10).replace("-", "/"));
		}
		parm.setCount(parm.getCount("ORDER_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC1");
		parm.addData("SYSTEM", "COLUMNS", "REPLACE_ORDER_CODE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC2");
		parm.addData("SYSTEM", "COLUMNS", "SEQ");
		parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		parm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		data.setData("TABLE", parm.getData());
		// 调用打印方法
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\IND\\SysReplaceMedicine.jhw", data);
	}

	/**
	 * 表格单击事件
	 */
	public void onTableClicked() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			// TParm parm = table.getDataStore().getRowParm(selectedRow);
			// String names =
			// "ORDER_CODE;ORDER_DESC1;REPLACE_ORDER_CODE;ORDER_DESC2;SEQ;DESCRIPTION;";
			// this.setValueForParm(names, parm);
			this.setValue("ORDER_CODE1",
					table.getItemString(selectedRow, "ORDER_CODE"));
			this.getTextField("ORDER_CODE1").setEditable(false);
			this.setValue("ORDER_CODE2",
					table.getItemString(selectedRow, "REPLACE_ORDER_CODE"));
			this.setValue("ORDER_DESC1",
					table.getItemString(selectedRow, "ORDER_DESC1"));
			this.setValue("ORDER_DESC2",
					table.getItemString(selectedRow, "ORDER_DESC2"));
			this.setValue("SEQ", table.getItemString(selectedRow, "SEQ"));
			this.setValue("NOTE",
					table.getItemString(selectedRow, "DESCRIPTION"));
		}
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE1")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE1").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		getTextField("ORDER_CODE2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn2");
		this.onQuery();
	}

}
