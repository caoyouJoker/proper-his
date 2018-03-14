package com.javahis.ui.inw;

import org.apache.commons.lang.StringUtils;

import jdo.inw.INWAssessmentReadTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title: 评估基本档
 * </p>
 * 
 * <p>
 * Description: 评估基本档
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author huzc 2015.10.22
 * @version 1.0
 */

public class INWAssessmentReadControl extends TControl {
	// 声明TTable
	private TTable table;
	private TComboBox combobox;

	public INWAssessmentReadControl() {
	}

	/**
	 * 初始化
	 * 
	 * @author Huzc
	 */
	public void onInit() {
		// 获取Table的组件
		table = (TTable) this.getComponent("TABLE");
		combobox = (TComboBox) this.getComponent("LOGIC1");
		// 添加单击响应事件
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 默认查询所有数据
		onQuery();
	}

	/**
	 * 查询方法
	 * 
	 * @author huzc
	 */
	public void onQuery() {
		String sql = "SELECT * FROM SYS_EVALUTION_DICT ORDER BY EVALUTION_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(parm);
	}

	/**
	 * 名称回车事件（带出拼音）
	 * 
	 * @author Huzc
	 */
	public void onAssessmentReadChDescAction() {
		String py = TMessage.getPy(this.getValueString("EVALUTION_DESC"));
		setValue("PY", py);
	}

	/**
	 * 表格单击响应事件
	 * 
	 * @author Huzc
	 */
	public void onTableClicked(int row) {
		// 将取得的值赋给TParm类型的parm对象
		TParm parm = table.getParmValue().getRow(row);
		// 将Parm中的值传给每个控件
		if (StringUtils.isEmpty(parm.getValue("LOGIC1"))) {
			setValueForParm(
					"EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM",
					parm);
			combobox.setSelectedID(null);
		} else {
			setValueForParm(
					"EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;LOGIC1;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM",
					parm);
		}
		// 将评估单号设为不可编辑
		TTextField text = (TTextField) getComponent("EVALUTION_CODE");
		text.setEditable(false);
	}

	/**
	 * 清空方法
	 * 
	 * @author Huzc
	 */
	public void onClear() {
		// 清除控件上显示的数据
		this
				.clearValue("EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM");
		combobox.setSelectedID(null);
		// 清空表格
		table.removeRowAll();
		// 将评估单号控件设为可编辑
		TTextField text = (TTextField) getComponent("EVALUTION_CODE");
		text.setEditable(true);
		return;
	}

	/**
	 * 新增方法
	 * 
	 * @author huzc
	 */

	public void onNew() {
		String evalutioncode = getValueString("EVALUTION_CODE");
		String evalutiondesc = getValueString("EVALUTION_DESC");
		String score1 = getValueString("SCORE1");
		String logic1 = combobox.getSelectedName();

		if (StringUtils.isEmpty(evalutioncode)) {
			this.messageBox("评估单号不可为空！");
			return;
		} else if (StringUtils.isEmpty(evalutiondesc)) {
			this.messageBox("名称不可为空！");
			return;
		} else if (StringUtils.isEmpty(score1)) {
			this.messageBox("分数不可为空！");
			return;
		} else if (StringUtils.isEmpty(logic1)) {
			this.messageBox("逻辑不可为空！");
			return;
		} else {
			String shortdesc = getValueString("SHORT_DESC");
			String py = getValueString("PY");
			String evalutionclass = getValueString("EVALUTION_CLASS");
			String scoredesc = getValueString("SCORE_DESC");

			TParm parm = new TParm();

			parm.setData("EVALUTION_DESC", evalutiondesc);
			parm.setData("SHORT_DESC", shortdesc);
			parm.setData("PY", py);
			parm.setData("EVALUTION_CLASS", evalutionclass);
			parm.setData("LOGIC1", logic1);
			parm.setData("SCORE1", score1);
			parm.setData("SCORE_DESC", scoredesc);
			parm.setData("OPT_USER", Operator.getName());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());

			// 判断单号是否重复
			String sql = " SELECT * FROM SYS_EVALUTION_DICT WHERE EVALUTION_CODE = '"
					+ evalutioncode + "' ";
			TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm1.getCount() > 0) {
				this.messageBox("评估单号已存在，请重新输入！");
				return;
			} else {
				parm.setData("EVALUTION_CODE", evalutioncode);
			}

			TParm result = new TParm();
			result = INWAssessmentReadTool.getInstance()
					.insertINWAssessmentRead(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("添加出错");
				err("ERR:" + result.getErrCode() + result.getErrName()
						+ result.getErrText());
				return;
			}
			this.messageBox("添加成功");
		}
		onQuery();
	}

	/**
	 * 修改方法
	 * 
	 * @author Huzc
	 */
	public void onSave(int row) {
		String evalutiondesc = getValueString("EVALUTION_DESC");
		String score1 = getValueString("SCORE1");
		String logic1 = combobox.getSelectedName();

		if (table.getSelectedRow() < 0) {
			this.messageBox("没有选中需要修改的数据！");
			return;
		} else if (table.getParmValue() == null
				|| table.getParmValue().getCount() < 1) {
			this.messageBox("没有输入更新内容");
		} else if (StringUtils.isEmpty(evalutiondesc)) {
			this.messageBox("名称不可为空！");
			return;
		} else if (StringUtils.isEmpty(score1)) {
			this.messageBox("分数不可为空！");
			return;
		} else if (StringUtils.isEmpty(logic1)) {
			this.messageBox("逻辑不可为空！");
			return;
		} else {
			String evalutioncode = getValueString("EVALUTION_CODE");
			String shortdesc = getValueString("SHORT_DESC");
			String py = getValueString("PY");
			String evalutionclass = getValueString("EVALUTION_CLASS");
			String scoredesc = getValueString("SCORE_DESC");

			TParm parm = new TParm();

			parm.setData("EVALUTION_CODE", evalutioncode);
			parm.setData("EVALUTION_DESC", evalutiondesc);
			parm.setData("SHORT_DESC", shortdesc);
			parm.setData("PY", py);
			parm.setData("EVALUTION_CLASS", evalutionclass);
			parm.setData("LOGIC1", logic1);
			parm.setData("SCORE1", score1);
			parm.setData("SCORE_DESC", scoredesc);
			parm.setData("OPT_USER", Operator.getName());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate()
					.toString().substring(0, 18));

			INWAssessmentReadTool.getInstance().updateINWAssessmentRead(parm);

			if (parm.getErrCode() < 0) {
				this.messageBox("修改出错！");
				err("ERR:" + parm.getErrCode() + parm.getErrName()
						+ parm.getErrText());
				return;
			} else {
				this.messageBox("修改成功！");
			}
			onQuery();
		}

	}

	/**
	 * 删除方法
	 * 
	 * @author Huzc
	 */
	public void onDelete() {

		if (table.getSelectedRow() < 0) {
			this.messageBox("没有选中需要删除的数据！");
			return;
		} else {
			String evalutioncode = getValueString("EVALUTION_CODE");
			String sql = " DELETE  FROM SYS_EVALUTION_DICT WHERE EVALUTION_CODE = '"
					+ evalutioncode + "'";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0) {
				this.messageBox("删除失败！");
				err("ERR:" + result.getErrCode() + result.getErrName()
						+ result.getErrText());
				return;
			} else {
				this.messageBox("删除成功！");
			}
			onQuery();
		}
	}
}
