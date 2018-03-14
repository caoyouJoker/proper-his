package com.javahis.ui.nss;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title: 营养成分字典
 * </p>
 * 
 * <p>
 * Description: 营养成分字典
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2015.3.13
 * @version 1.0
 */
public class NSSNutrientsDictControl extends TControl {
	public NSSNutrientsDictControl() {
		super();
	}

	private TTable table;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		table = getTable("TABLE");
		onQueryInit();
	}

	/**
	 * 初始化查询
	 */
	public void onQueryInit() {
		TParm result = NSSEnteralNutritionTool.getInstance().selectDataCF(new TParm());
		if (result.getErrCode() < 0) {
			this.messageBox("初始化失败！");
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * 生成新的成分代码主键
	 */
	public void onNewNutritionCode() {
		TParm maxCode = NSSEnteralNutritionTool.getInstance()
				.getMaxNutritionCode();
		String newCode = getNewCode(maxCode.getValue("MAX", 0));
		this.setValue("NUTRITION_CODE", newCode);
		callFunction("UI|NUTRITION_CODE|setEnabled", false);
	}

	/**
	 * 保存操作 add by lich
	 */
	public void onSave() {
		TTextField NutritionCode = (TTextField) getComponent("NUTRITION_CODE");
		if (NutritionCode.isEnabled()) {
			messageBox("如新增数据，请点新增按钮");
			return;
		}
		TParm parm = new TParm();
		parm.setData("NUTRITION_CODE", this.getValueString("NUTRITION_CODE"));
		TParm codeFlg = NSSEnteralNutritionTool.getInstance().isExistCF(parm);

		// 新增操作
		if (0 == Integer.parseInt(codeFlg.getValue("COUNT", 0))) {
			TParm insertParm = new TParm();
			// 医嘱代码
			insertParm.setData("NUTRITION_CODE", this
					.getValueString("NUTRITION_CODE"));
			// 医嘱中文描述
			String chnDesc = this.getValueString("NUTRITION_CHN_DESC");
			if (null == chnDesc || "".equals(chnDesc)) {
				messageBox("请填写营养成分中文名称");
				return;
			} else {
				insertParm.setData("NUTRITION_CHN_DESC", chnDesc);
			}
			//
			insertParm.setData("NUTRITION_ENG_DESC", this
					.getValueString("NUTRITION_ENG_DESC"));
			//
			insertParm.setData("PY1", this.getValueString("PY1"));
			insertParm.setData("PY2", this.getValueString("PY2"));
			// 营养素参考值
			insertParm.setData("NRV", this.getValueDouble("NRV"));
			// 医嘱中文描述
			String unitCode = this.getValueString("UNIT_CODE");
			if (null == unitCode || "".equals(unitCode)) {
				messageBox("请填写单位");
				return;
			} else {
				insertParm.setData("UNIT_CODE", unitCode);
			}
			insertParm.setData("OPT_USER", Operator.getID());
			insertParm.setData("OPT_TERM", Operator.getIP());
			String date = SystemTool.getInstance().getDate().toString();
			insertParm
					.setData("OPT_DATE", date.substring(0, date.length() - 2));

			TParm resultParm = NSSEnteralNutritionTool.getInstance()
					.insertDataCF(insertParm);
			if (resultParm.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return;
			} else {
				this.messageBox("保存成功！");
			}

			// 修改操作
		} else {
			TParm updateParm = new TParm();
			updateParm.setData("NUTRITION_CODE", this
					.getValueString("NUTRITION_CODE"));
			updateParm.setData("NUTRITION_CHN_DESC", this
					.getValueString("NUTRITION_CHN_DESC"));
			updateParm.setData("NUTRITION_ENG_DESC", this
					.getValueString("NUTRITION_ENG_DESC"));
			updateParm.setData("PY1", this.getValueString("PY1"));
			updateParm.setData("PY2", this.getValueString("PY2"));
			updateParm.setData("NRV", this.getValueDouble("NRV"));
			updateParm.setData("UNIT_CODE", this.getValueString("UNIT_CODE"));

			updateParm.setData("OPT_USER", Operator.getID());
			updateParm.setData("OPT_TERM", Operator.getIP());
			String date = SystemTool.getInstance().getDate().toString();
			updateParm
					.setData("OPT_DATE", date.substring(0, date.length() - 2));
			TParm resultParm = NSSEnteralNutritionTool.getInstance()
					.updateDataCF(updateParm);
			if (resultParm.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return;
			} else {
				this.messageBox("保存成功！");
			}
		}
		onClear();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("NUTRITION_CODE", this.getValueString("NUTRITION_CODE"));
		parm.setData("NUTRITION_CHN_DESC", this
				.getValueString("NUTRITION_CHN_DESC"));
		TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataCF(
				parm);
		table.setParmValue(resultParm);
	}

	/**
	 * 单击表格数据后，带入基本信息
	 */
	public void onTableClick() {
		TTable table = getTable("TABLE");
		int row = table.getSelectedRow();

		this.setValue("NUTRITION_CODE", table
				.getItemData(row, "NUTRITION_CODE"));
		this.setValue("NUTRITION_CHN_DESC", table.getItemData(row,
				"NUTRITION_CHN_DESC"));
		this.setValue("NUTRITION_ENG_DESC", table.getItemData(row,
				"NUTRITION_ENG_DESC"));
		this.setValue("PY1", table.getItemData(row, "PY1"));
		this.setValue("PY2", table.getItemData(row, "PY2"));
		this.setValue("NRV", table.getItemData(row, "NRV"));
		this.setValue("UNIT_CODE", table.getItemData(row, "UNIT_CODE"));
		callFunction("UI|NUTRITION_CODE|setEnabled", false);
		callFunction("UI|NUTRITION_CHN_DESC|setEnabled", true);
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		this
				.clearValue("NUTRITION_CODE;NUTRITION_CHN_DESC;NUTRITION_ENG_DESC;PY1;"
						+ "PY2;NRV;UNIT_CODE");
		callFunction("UI|NUTRITION_CODE|setEnabled", true);
		onInit();
	}

	/**
	 * 删除操作 add by lich
	 */
	public void onDelete() {
		TParm delParm = new TParm();

		if (JOptionPane.showConfirmDialog(null, "是否删除选中数据？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {
			delParm.setData("NUTRITION_CODE", this.getValue("NUTRITION_CODE"));
			TParm result = NSSEnteralNutritionTool.getInstance().deleteDataCF(
					delParm);
			if (result.getErrCode() < 0) {
				this.messageBox("删除失败！");
			} else {
				this.messageBox("删除成功！");
				onInit();
				onClear();
			}
		}
		onInit();
	}

	/**
	 * 获取新增最大主键号
	 * 
	 * @param NutritionCode
	 * @return add by lich
	 */
	private String getNewCode(String Code) {
		DecimalFormat df = new DecimalFormat("0000");
		double code = 1;
		try {
			code = Double.parseDouble(Code);
			code += 1;
			return df.format(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return df.format(code);
	}

	/**
	 * NUTRITION_CHN_DESC回车事件
	 */
	public void onUserNameAction() {
		String py = TMessage.getPy(this.getValueString("NUTRITION_CHN_DESC"));
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return add by lich
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
