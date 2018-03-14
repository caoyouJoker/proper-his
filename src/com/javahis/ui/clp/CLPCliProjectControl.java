package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

import jdo.clp.CLPCliProjectTool;
import jdo.sys.Operator;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:临床研究项目单档
 * </p>
 * 
 * <p>
 * Description:临床研究项目单档
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wukai 2016.5.23
 * @version 1.0
 */
public class CLPCliProjectControl extends TControl {
	public CLPCliProjectControl() {
	}

	private static final String TABLE = "TABLE";
	private TTable table;
	private CLPCliProjectTool tool;

	@Override
	public void onInit() {
		super.onInit();
		tool = CLPCliProjectTool.getNewInstance();
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		table = getTable(TABLE);
		TParm parm = new TParm();
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_TIME", start);
		this.setValue("END_TIME", end);
		parm.setData("END_TIME", end);
		parm.setData("START_TIME", start);
		table.setParmValue(tool.onQuery(parm));
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 项目查询
	 */
	public void onQuery() {
		// TODO
		TParm parm = new TParm();
		String code = getText("CLIPRO_CODE");
		if (!StringUtils.isEmpty(code)) {
			parm.setData("CLIPRO_CODE", "%" + code + "%");
		}
		String desc = getText("CLIPRO_DESC");
		if (!StringUtils.isEmpty(desc)) {
			parm.setData("CLIPRO_DESC", "%" + desc + "%");
		}
		String charger = getText("CLIPRO_CHARGER");
		if (!StringUtils.isEmpty(charger)) {
			parm.setData("CLIPRO_CHARGER", "%" + charger + "%");
		}

		Object classify = this.getValue("CLASSIFY_CODE");
		if (classify != null) {
			parm.setData("CLASSIFY_CODE", "%" + classify + "%");
		}

		Object startTime = this.getValue("START_TIME");
		if (startTime != null) {
			parm.setData(
					"START_TIME",
					Timestamp.valueOf(startTime.toString().substring(0, 10)
							+ " 00:00:00"));
		}

		Object endTime = this.getValue("END_TIME");
		if (endTime != null) {
			parm.setData(
					"END_TIME",
					Timestamp.valueOf(endTime.toString().substring(0, 10)
							+ " 23:59:59"));
		}

		String exp = this.getText("CLIPRO_EXP");
		if (!StringUtils.isEmpty(exp)) {
			parm.setData("CLIPRO_EXP", "%" + exp + "%");
		}

		TParm res = tool.onQuery(parm);
		//System.out.println("query result:" + res);
		if(res == null || res.getCount() <= 0) {
			this.messageBox("查询暂无此数据!");
		}
		getTable(TABLE).setParmValue(res);
	}

	/**
	 * 删除一个项目
	 */
	public void onDelete() {
		boolean flg = getTextField("CLIPRO_CODE").isEnabled();
		if (!flg) { // 选中一个可以删除
			int res = this.messageBox("提示", "确认删除？", YES_NO_OPTION);
			if(res == YES_OPTION) {
				int row = table.getSelectedRow();
				if (row != -1) {
					String code = table.getParmValue().getValue("CLIPRO_CODE", row);
					TParm parm = new TParm();
					parm.setData("CLIPRO_CODE", code);
					if(tool.onDelete(parm)) {
						this.messageBox("删除成功");
						table.removeRow(row);
						onClear();
					}
				}
			}
		} else {
			this.messageBox("请选择一条进行删除");
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		int row = 0;
		Timestamp start = (Timestamp) this.getValue("START_TIME");
		Timestamp end = (Timestamp) this.getValue("END_TIME");
		if (start != null && end != null) {
			if (start.getTime() > end.getTime()) {
				this.messageBox("时间范围不正确，请重新选择");
				return;
			}
		}
		if (start != null) {
			start = Timestamp.valueOf(start.toString().substring(0, 10)
					+ " 00:00:00");
		}
		if (end != null) {
			end = Timestamp.valueOf(end.toString().substring(0, 10)
					+ " 23:59:59");
		}
		TTextField code = getTextField("CLIPRO_CODE");
		boolean flg = code.isEnabled(); // 能够被获取到
		TParm parm = new TParm();
		parm.setData("CLIPRO_CODE", getValueString("CLIPRO_CODE"));
		parm.setData("CLIPRO_DESC", getValueString("CLIPRO_DESC"));
		parm.setData("CLIPRO_CHARGER", getText("CLIPRO_CHARGER"));
		parm.setData("START_TIME", (start != null) ? start : "");
		parm.setData("END_TIME", (end != null) ? end : "");
		parm.setData("CLIPRO_EXP", getText("CLIPRO_EXP"));
		parm.setData("CLASSIFY_CODE", getValue("CLASSIFY_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TIME", StringTool.getTimestamp(new Date()));
		parm.setData("OPT_TERM", Operator.getIP());
		// this.messageBox(parm.toString());
		if (!flg) { // 更新数据
			if (!checkData()) {
				return;
			}
			if (!tool.onUpdate(parm)) {
				this.messageBox("更新失败，请检查数据的合理性");
				return;
			}
			row = table.getSelectedRow();
		} else { // 保存新数据
			if (!checkData()) {
				return;
			}
			if(!checkCode(getValueString("CLIPRO_CODE"))) {
				return;
			}
			if (!tool.onSave(parm)) {
				this.messageBox("保存失败，请检查数据的合理性");
				return;
			}
			row = table.addRow();
		}
		table.setItem(row, "CLIPRO_CODE", parm.getData("CLIPRO_CODE"));
		table.setItem(row, "CLIPRO_DESC", parm.getData("CLIPRO_DESC"));
		table.setItem(row, "CLIPRO_CHARGER", parm.getData("CLIPRO_CHARGER"));
		table.setItem(row, "START_TIME", parm.getData("START_TIME"));
		table.setItem(row, "END_TIME", parm.getData("END_TIME"));
		table.setItem(row, "CLIPRO_EXP", parm.getData("CLIPRO_EXP"));
		table.setItem(row, "CLASSIFY_CODE", parm.getData("CLASSIFY_CODE"));
		table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
		table.setItem(row, "OPT_TIME", parm.getData("OPT_TIME"));
		table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
		this.messageBox("保存成功");
		onClear();
	}

	/**
	 * 清空区域内容，删除置灰
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "CLIPRO_CODE;CLIPRO_DESC;CLIPRO_CHARGER;CLIPRO_EXP;START_TIME;END_TIME;CLASSIFY_CODE";
		clearValue(clearString);
		table.setSelectionMode(0);
		getTextField("CLIPRO_CODE").setEnabled(true);
		getTextField("CLIPRO_DESC").setEnabled(true);
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_TIME", start);
		this.setValue("END_TIME", end);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 点击表格区域内容，将点击的列的值 删除激活
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row >= 0) {
			String linkedTag = "CLIPRO_CODE;CLIPRO_DESC;CLIPRO_CHARGER;START_TIME;END_TIME;CLASSIFY_CODE;CLIPRO_EXP";
			TParm parm = getTable(TABLE).getParmValue().getRow(row);
			this.setValueForParm(linkedTag, parm);

			getTextField("CLIPRO_CODE").setEnabled(false);
			getTextField("CLIPRO_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
		}
	}

	/**
	 * 获取TTextField
	 * 
	 * @param tag
	 *            : 标签
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * 获取页面表格
	 * 
	 * @param table
	 * @return
	 */
	private TTable getTable(String table) {
		return (TTable) getComponent(table);
	}

	/**
	 * 检查数据是否为空
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (StringUtils.isEmpty(getValueString("CLIPRO_CODE"))) {
			this.messageBox("编码不能为空");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("CLIPRO_DESC"))) {
			this.messageBox("编码名称不能为空");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("CLIPRO_CHARGER"))) {
			this.messageBox("负责人不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 检查当前的code时候已经存在
	 * 
	 * @return boolean false 表示存在此主健，不可使用此编号
	 */
	private boolean checkCode(String code) {
		TParm parm = tool.onQuery(new TParm());
		if (parm != null) {
			for(int i=0;i<parm.getCount();i++) {
				if(code.equals(parm.getData("CLIPRO_CODE", i))) {
					this.messageBox("编码已存在，请更改编码!");
					return false;
				}
			}
		}
		return true;
	}
}
