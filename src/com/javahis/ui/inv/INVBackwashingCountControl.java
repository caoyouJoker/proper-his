package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVBackwashingTool;
import jdo.inv.INVOrgTool;
import jdo.inv.INVPublicTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 返洗率统计
 * </p>
 * 
 * <p>
 * Description: 返洗率统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.8.10
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author sdr
 * @version 1.0
 */
public class INVBackwashingCountControl extends TControl {

	private TTable tableBS;

	

	/**
	 * 初始化方法
	 */
	public void onInit() {
		this.tableBS = this.getTable("tableBS");
		
		TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // 定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        

		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("BACKWASHING_DATE_START", date);
		setValue("BACKWASHING_DATE_END", date);
	}


	/**
	 * 清空表格
	 */
	public void onClear() {
		tableBS.removeRowAll();
		this.clearControls();
	}
	
	private void clearControls(){
		setValue("PACK_CODE", "");
		setValue("PACK_DESC", "");
		//为日期控件赋当前日期值
		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("BACKWASHING_DATE_START", date);
		setValue("BACKWASHING_DATE_END", date);
	}

	/**
	 * 手术包选择返回数据处理
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		// 包号
		setValue("PACK_CODE", parm.getValue("PACK_CODE"));
		// 包名
		setValue("PACK_DESC", parm.getValue("PACK_DESC"));
	}

	/**
	 * 表格单击事件 用来判断做保存操作还是更新操作
	 */
	public void onTableClicked() {
//		int row = tableBS.getSelectedRow();
//		if (row >= 0) {
//			isSave = false;
//		}
//		TParm selParm = tableBS.getParmValue().getRow(row);
//
//		setValue("PACK_CODE", selParm.getData("PACK_CODE"));
//		setValue("PACK_CHN_DESC", selParm.getData("PACK_CHN_DESC"));
//		setValue("BACKWASHING_REASON", selParm.getData("BACKWASHING_REASON"));
//		setValue("BACKWASHING_DATE", selParm.getValue("BACKWASHING_DATE").toString().substring(0,19).replaceAll("-", "/"));
//		setValue("POT_SEQ", selParm.getData("POT_SEQ"));
//		setValue("PROGRAM", selParm.getData("PROGRAM"));
//		setValue("OPERATIONSTAFF", selParm.getData("OPERATIONSTAFF"));
//		setValue("REMARK", selParm.getData("REMARK"));
//		setValue("ORG_CODE", selParm.getData("ORG_CODE"));
//		setValue("SCREAM", selParm.getData("BARCODE"));
//		
//		this.changeStatus(false);
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		
//		if (null == this.getValueString("BACKWASHING_DATE_START")
//				|| this.getValueString("BACKWASHING_DATE_START").length() == 0 || null == this.getValueString("BACKWASHING_DATE_END") || this.getValueString("BACKWASHING_DATE_END").length() == 0) {
//			messageBox("返洗日期不能为空！");
//			return;
//		}
		if(!checkConditions()){
			return;
		}
		
		TParm conditions = new TParm();
		
		String str = getValueString("BACKWASHING_DATE_BEGIN").toString();
		
		conditions.setData("BACKWASHING_DATE_START", getValueString("BACKWASHING_DATE_START").toString().substring(0,10) + " 00:00:00");
		conditions.setData("BACKWASHING_DATE_END", getValueString("BACKWASHING_DATE_END").toString().substring(0,10) + " 23:59:59");
		
		if(getValueString("PACK_CODE") != "" && getValueString("PACK_CODE").length() != 0){
			conditions.setData("PACK_CODE", getValueString("PACK_CODE"));
		}

		TParm result = INVBackwashingTool.getInstance().queryBSCount(conditions);
		tableBS.setParmValue(result);
		
	}
	

	/**
	 * 校验数据
	 * 
	 * @return
	 */
	private boolean checkConditions() {
		
		if (null == this.getValueString("BACKWASHING_DATE_START")
				|| this.getValueString("BACKWASHING_DATE_START").length() == 0) {
			messageBox("返洗日期不能为空！");
			return false;
		}
		if (null == this.getValueString("BACKWASHING_DATE_END")
				|| this.getValueString("BACKWASHING_DATE_END").length() == 0) {
			messageBox("返洗日期不能为空！");
			return false;
		}
		return true;
	}
	
	/**
	 * 获取表格
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	/**
	 * 得到TextField
	 * 
	 * @param tag
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}
	
}
