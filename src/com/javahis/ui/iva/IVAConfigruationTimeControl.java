package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.util.Date;

import jdo.iva.IVAConfigruationTimeTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:静脉输液配置时间设定控制类
 * </p>
 * 
 * <p>
 * Description:静脉输液配置时间设定控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2013.6.4
 * @version 1.0
 */
public class IVAConfigruationTimeControl extends TControl {

	// 界面Table控件
	private TTable table;
	private String action = "save";

	/**
	 * 初始化方法
	 */

	public void onInit() {
		super.onInit();

		table = this.getTTable("TABLE");
	}

	/**
	 * 获得TTable控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获得TCheckBox控件
	 * 
	 * @param tag
	 * @return
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) getComponent(tag);
	}

	/**
	 * 获得TComboBox控件
	 * 
	 * @param tag
	 * @return
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) getComponent(tag);
	}

	/**
	 * 清空界面表格与控件的值
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "BATCH_CODE;BATCH_CHN_DESC;IVA_TIME;DCCHECK_TIME;"
				+ "BATCH_CHN_DESC;TREAT_START_TIME;"
				+ "TREAT_END_TIME;REMARK;FIRST_BATCH;LAST_BATCH;"
				+ "ST_FLG;F_FLG;UD_FLG;TPN_FLG;OP_FLG";//加入术中医嘱
		clearValue(tags);
	}

	/**
	 * 表格单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		// 获取table数据
		TParm selParm = table.getParmValue().getRow(row);
		// 控件赋值
		setValue("BATCH_CODE", selParm.getData("BATCH_CODE"));
		setValue("BATCH_CHN_DESC", selParm.getData("BATCH_CHN_DESC"));
		setValue("DCCHECK_TIME", selParm.getData("DCCHECK_TIME"));
		setValue("IVA_TIME", selParm.getData("IVA_TIME"));
		setValue("TREAT_START_TIME", selParm.getData("TREAT_START_TIME"));
		setValue("TREAT_END_TIME", selParm.getData("TREAT_END_TIME"));
		if(!selParm.getData("STAT_FLG").equals("")){
			this.setValue("ST_FLG", "Y");
		}else{
			this.setValue("ST_FLG", "N");
		}
		if(!selParm.getData("FIRST_FLG").equals("")){
			this.setValue("F_FLG", "Y");
		}else{
			this.setValue("F_FLG", "N");
		}
		if(!selParm.getData("UD_FLG").equals("")){
			this.setValue("UD_FLG", "Y");
		}else{
			this.setValue("UD_FLG", "N");
		}
		//20151110 wangjc add
		if(!selParm.getData("OP_FLG").equals("")){
			this.setValue("OP_FLG", "Y");
		}else{
			this.setValue("OP_FLG", "N");
		}
		setValue("TPN_FLG", selParm.getData("PN_FLG"));
		setValue("REMARK", selParm.getData("REMARK"));
		if (row == -1) {
			// 未选中则为保存
			action = "save";
		} else {
			action = "update";
		}

	}

	/**
	 * 数据校验
	 * 
	 * @return
	 */
	public boolean checkData() {
		if (getValueString("BATCH_CODE").length() == 0
				|| getValueString("BATCH_CODE") == "") {
			messageBox("请填写批次代码");
			return false;
		}
		if (getValueString("BATCH_CHN_DESC").length() == 0
				|| getValueString("BATCH_CHN_DESC") == "") {
			messageBox("请填写名称");
			return false;
		}
		if (getValueString("IVA_TIME").length() == 0
				|| getValueString("IVA_TIME") == "") {
			messageBox("请填写配液时间");
			return false;
		}
		if (getValueString("DCCHECK_TIME").length() == 0
				|| getValueString("DCCHECK_TIME") == "") {
			messageBox("请填写停配医嘱确认时间");
			return false;
		}
		if (getValueString("TREAT_START_TIME").length() == 0
				|| getValueString("TREAT_START_TIME") == "") {
			messageBox("请填写治疗区间");
			return false;
		}
		if (getValueString("TREAT_END_TIME").length() == 0
				|| getValueString("TREAT_END_TIME") == "") {
			messageBox("请填写治疗区间");
			return false;
		}
		if(this.getValueString("ST_FLG").equals("N") 
				&& this.getValueString("F_FLG").equals("N") 
				&& this.getValueString("UD_FLG").equals("N") 
				&& this.getValueString("OP_FLG").equals("N")){
			this.messageBox("临时，首日量和长期选项至少选择一项");
			return false;
		}
		return true;
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 数据校验
		if (!checkData())
			return;
		// 获取系统时间
		Timestamp date = StringTool.getTimestamp(new Date());
		// 获取界面参数
		TParm parm = new TParm();
		parm.setData("BATCH_CODE", getValueString("BATCH_CODE"));
		parm.setData("BATCH_CHN_DESC", getValueString("BATCH_CHN_DESC"));
		parm.setData("DCCHECK_TIME", getValueString("DCCHECK_TIME").substring(
				11, 16).replace(":", ""));
		parm.setData("IVA_TIME", getValueString("IVA_TIME").substring(11, 16).replace(":", ""));
		parm.setData("TREAT_START_TIME", getValueString("TREAT_START_TIME")
				.substring(11, 16).replace(":", ""));
		parm.setData("TREAT_END_TIME", getValueString("TREAT_END_TIME")
				.substring(11, 16).replace(":", ""));
		if(this.getValueString("ST_FLG").equals("Y")){
			parm.setData("STAT_FLG", "ST");
		}else{
			parm.setData("STAT_FLG", "");
		}
		if(this.getValueString("F_FLG").equals("Y")){
			parm.setData("FIRST_FLG", "F");
		}else{
			parm.setData("FIRST_FLG", "");
		}
		if(this.getValueString("UD_FLG").equals("Y")){
			parm.setData("UD_FLG", "UD");
		}else{
			parm.setData("UD_FLG", "");
		}
		//20151110 WANGJC ADD术中医嘱
		if(this.getValueString("OP_FLG").equals("Y")){
			parm.setData("OP_FLG", "OP");
		}else{
			parm.setData("OP_FLG", "");
		}
		parm.setData("PN_FLG", getValueString("TPN_FLG"));
		parm.setData("REMARK", getValueString("REMARK"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date.toString().substring(0, 19));
		parm.setData("OPT_TERM", Operator.getIP());
		// 判断table有没有选中行,来决定做保存还是更新操作
		if (table.getSelectedRow() >= 0) {
			TParm result = IVAConfigruationTimeTool.getInstance().updateInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0001");
				return;
			}
			messageBox("更新成功");
			// 保存到数据库中

		} else if (table.getSelectedRow() == -1) {
			// 更新到数据库中
			TParm result = IVAConfigruationTimeTool.getInstance().insertInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0002");
				return;
			}
			messageBox("P0002");
		}
		onClear();
		onQuery();
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要删除的数据");
			return;
		}
		int i = this.messageBox("提示", "是否删除", 2);
		if (i == 0) {
			TParm parm = new TParm();
			// 参数判断不为空时在传入
			String BATCH_CODE = table.getItemData(row, "BATCH_CODE").toString();
			if (!StringUtil.isNullString(BATCH_CODE)) {
				parm.setData("BATCH_CODE", BATCH_CODE);
			}
			TParm result = IVAConfigruationTimeTool.getInstance().deleteInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0003");
				return;
			}
			messageBox("P0003");
			onClear();
			onQuery();
		}
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// 清空table
		table.removeRowAll();
		TParm parm = new TParm();
		// 批次代码(查询条件)
		String BATCH_CODE = getValueString("BATCH_CODE");
		// 判断不为空时在传入
		if (!StringUtil.isNullString(BATCH_CODE)) {
			parm.setData("BATCH_CODE", BATCH_CODE);
		}
		TParm result = IVAConfigruationTimeTool.getInstance().queryInfo(parm);
		if(result.getCount() <= 0){
			table.removeRowAll();
			this.messageBox("未查询到数据");
			return;
		}
		for(int i=0;i<result.getCount("BATCH_CODE");i++){
			result.setData("IVA_TIME", i, result.getValue("IVA_TIME", i).substring(0, 2)
								+":"+result.getValue("IVA_TIME", i).substring(2, 4));
			result.setData("TREAT_START_TIME", i, result.getValue("TREAT_START_TIME", i).substring(0, 2)
					+":"+result.getValue("TREAT_START_TIME", i).substring(2, 4));
			result.setData("TREAT_END_TIME", i, result.getValue("TREAT_END_TIME", i).substring(0, 2)
					+":"+result.getValue("TREAT_END_TIME", i).substring(2, 4));
			result.setData("DCCHECK_TIME", i, result.getValue("DCCHECK_TIME", i).substring(0, 2)
					+":"+result.getValue("DCCHECK_TIME", i).substring(2, 4));
			result.setData("OPT_DATE", i, result.getValue("OPT_DATE", i).substring(0, 19));
		}
//		System.out.println("result===="+result);
		table.setParmValue(result);
	}

}
