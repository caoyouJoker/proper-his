package com.javahis.ui.sys;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Operator;
import jdo.sys.SYSPumpTypeTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.orm.tools.PinyinTool;

/**
 * <p>
 * title:泵入方式字典
 * </p>
 * <p>
 * ContentDiscription:泵入方式字典
 * </p>
 * <p>
 * Company:javahis
 * </p>
 * <p>
 * Time:20160526
 * </p>
 * 
 * @author wukai
 * 
 */
public class SYSPumpTypeControl extends TControl {

	private TTable table;
	private SYSPumpTypeTool tool;

	public void onInit() {
		super.onInit();
		tool = SYSPumpTypeTool.getNewInstance();
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		table.setParmValue(tool.onQuery(new TParm()));
		TDataStore tds = new TDataStore();
		tds.setSQL("SELECT PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, "
                + "SEQ, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_PUMPTYPE ORDER BY PUMP_CODE");
		tds.retrieve();
		// 初始化顺序号，想获取表中最大的顺序号+1 delete置灰
		int seq = getMaxSeq(tds, "SEQ", tds.isFilter() ? tds.FILTER
				: tds.PRIMARY);
		this.setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String code = getText("PUMP_CODE");
		if (!StringUtils.isEmpty(code)) {
			parm.setData("PUMP_CODE", "%" + code + "%");
		}
		String desc = getText("PUMP_DESC");
		if (!StringUtils.isEmpty(desc)) {
			parm.setData("PUMP_DESC", "%" + desc + "%");
		}
		String py1 = getText("PY1");
		if (!StringUtils.isEmpty(py1)) {
			parm.setData("PY1", "%" + py1 + "%");
		}
		String py2 = getText("PY2");
		if (!StringUtils.isEmpty(py2)) {
			parm.setData("PY2", "%" + py2 + "%");
		}
		String description = getText("DESCRIPTION");
		if (!StringUtils.isEmpty(description)) {
			parm.setData("DESCRIPTION", "%" + description + "%");
		}
		TParm res = tool.onQuery(parm);
		if(res == null || res.getCount() <= 0) {
			this.messageBox("此查询条件暂无数据!");
		}
		table.setParmValue(res);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		int row = 0;
		TTextField code = getTextField("PUMP_CODE");
		boolean flg = code.isEnabled(); // 能够被获取到
		TParm parm = new TParm();
		parm.setData("PUMP_CODE", getValueString("PUMP_CODE"));
		parm.setData("PUMP_DESC", getValueString("PUMP_DESC"));
		parm.setData("PY1", this.getText("PY1"));
		parm.setData("PY2", this.getText("PY2"));
		parm.setData("SEQ", this.getText("SEQ"));
		parm.setData("DESCRIPTION", this.getText("DESCRIPTION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		parm.setData("OPT_TERM", Operator.getIP());
		if (!flg) { // 更新数据
			if (!checkData()) {
				return;
			}
			if (!tool.onUpdate(parm)) {
				this.messageBox("更新失败，请检查数据合理性");
				return;
			}
			row = table.getSelectedRow();
		} else { // 保存新数据
			if (!checkData()) {
				return;
			}
			if (!tool.onSave(parm)) {
				this.messageBox("保存失败!");
				return;
			}
			row = table.addRow();
		}
		table.setItem(row, "PUMP_CODE", parm.getData("PUMP_CODE"));
		table.setItem(row, "PUMP_DESC", parm.getData("PUMP_DESC"));
		table.setItem(row, "PY1", parm.getData("PY1"));
		table.setItem(row, "PY2", parm.getData("PY2"));
		table.setItem(row, "SEQ", parm.getData("SEQ"));
		table.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
		table.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
		table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
		this.messageBox("保存成功");
		onClear();
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		boolean flg = getTextField("PUMP_CODE").isEnabled();
		if (!flg) { // 选中一个可以删除
			int row = table.getSelectedRow();
			if (row != -1) {
				String code = table.getParmValue().getValue("PUMP_CODE", row);
				TParm parm = new TParm();
				parm.setData("PUMP_CODE", code);
				if (tool.onDelete(parm)) {
					table.removeRow(row);
				} else {
					this.messageBox("删除失败!");
				}
				onClear();
			}
		} else {
			this.messageBox("请选择一条进行删除");
		}
	}

	/**
	 * 清空画面
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "PUMP_CODE;PUMP_DESC;PY1;" + "PY2;SEQ;DESCRIPTION";
		clearValue(clearString);
		// 产生序号,重新获取所有的数据
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL("SELECT PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, "
                + "SEQ, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_PUMPTYPE ORDER BY PUMP_CODE");
		dataStore.retrieve();
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);

		table.setSelectionMode(0);
		getTextField("PUMP_CODE").setEnabled(true);
		getTextField("PUMP_DESC").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 获取TextField
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 检查数据是否为空
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (StringUtils.isEmpty(getValueString("PUMP_CODE"))) {
			this.messageBox("编码不能为空");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("PUMP_DESC"))) {
			this.messageBox("编码名称不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 得到最大的编号 +1
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// 保存数据量
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// 保存最大号
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// 保存最大值
			if (max < value) {
				max = value;
				continue;
			}
		}
		// 最大号加1
		max++;
		return max;
	}
	
	/**
	 * Table点击事件 将所选表格内容填充到界面中
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String linkNames = "PUMP_CODE;PUMP_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION";
			this.setValueForParm(linkNames, parm);
			getTextField("PUMP_CODE").setEnabled(false);
			getTextField("PUMP_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
		}
	}
	
	/**
	 * 编码名称输入完后
	 */
	public void onDescFinish() {
		// this.messageBox("name finish");
		String desc = this.getValueString("PUMP_DESC");
		if (desc == null) {
			return;
		} else {
			String py1 = PinyinTool.getPinyin(desc);
			this.setValue("PY1", py1);
		}
	}
}
