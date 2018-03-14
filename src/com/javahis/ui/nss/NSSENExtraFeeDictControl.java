package com.javahis.ui.nss;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: 肠内营养附加费用</p>
 *
 * <p>Description: 肠内营养附加费用</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2016.3.4
 * @version 1.0
 */
public class NSSENExtraFeeDictControl extends TControl {
	
    private TTable table;
    private TTextField orderCode;
	
    /**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		TParm parm = new TParm();
		orderCode = (TTextField) this.getComponent("ORDER_CODE");
		orderCode.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		orderCode.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
		
		this.onInitPage();
	}
	
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		callFunction("UI|CATEGORY_CODE|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", true);
        callFunction("UI|ORDER_DESC|setEnabled", true);
        this.clearValue("CATEGORY_CODE;ORDER_CODE;ORDER_DESC;CHARGE_TYPE");
    	getCheckBox("ACTIVE_FLG").setSelected(true);
		this.onQuery();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm queryParm = this
				.getParmForTag("CATEGORY_CODE;ORDER_CODE;CHARGE_TYPE");
		// 查询肠内营养附加费用
		TParm result = NSSEnteralNutritionTool.getInstance().queryENExtraFee(
				queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询肠内营养附加费用错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		} else {
			table.setParmValue(result);
		}
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 数据合理性校验
		if (this.validateData()) {
			TParm result = new TParm();
			TParm parm = getParmForTag("CATEGORY_CODE;ORDER_CODE;CHARGE_TYPE");
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			
			// 更新
			if (table.getSelectedRow() >= 0) {
				parm.setData("CHARGE_TYPE", this.getValue("CHARGE_TYPE"));
				if (getCheckBox("ACTIVE_FLG").isSelected()) {
					parm.setData("ACTIVE_FLG", "Y");
				} else {
					parm.setData("ACTIVE_FLG", "N");
				}
				
				// 更新肠内营养附加费用字典
				result = NSSEnteralNutritionTool.getInstance().updateENExtraFee(parm);
				
				// 插入
			} else {
				
				if (getCheckBox("ACTIVE_FLG").isSelected()) {
					parm.setData("ACTIVE_FLG", "Y");
				} else {
					parm.setData("ACTIVE_FLG", "N");
				}
				
				// 新增肠内营养附加费用字典
				result = NSSEnteralNutritionTool.getInstance().insertENExtraFee(parm);
			}
			
			if (result.getErrCode() < 0) {
				this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
			} else {
				this.messageBox("P0001");
				this.onClear();
			}
		}
	}
	
	/**
	 * 删除方法
	 */
	public void onDelete() {
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选中要删除的数据行");
			return;
		}
		
		if (this.messageBox("删除", "是否删除选中的数据", 2) == 0) {
			TParm parm = new TParm();
			parm = getParmForTag("CATEGORY_CODE;ORDER_CODE");
			
			// 删除肠内营养附加费用字典
			TParm result = NSSEnteralNutritionTool.getInstance().deleteENExtraFee(parm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("删除失败");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
			} else {
				this.messageBox("删除成功");
				this.onClear();
			}
		}
	}
	
	/**
	 * 数据校验
	 */
	private boolean validateData() {
		if (StringUtils.isEmpty(this.getValueString("CATEGORY_CODE"))) {
			this.messageBox("肠内营养分类不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("ORDER_CODE"))) {
			this.messageBox("附加费用编码不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("ORDER_DESC"))) {
			this.messageBox("附加费用名称不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("CHARGE_TYPE"))) {
			this.messageBox("计费单位不能为空");
			return false;
		}
		
		return true;
	}
	
	/**
     * 单击表格数据后，带入基本信息
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
    	TParm parm = table.getParmValue().getRow(row);
		this.setValueForParm("CATEGORY_CODE;ORDER_CODE;ORDER_DESC;CHARGE_TYPE",
				parm);
		if ("Y".equals(parm.getValue("ACTIVE_FLG"))) {
			getCheckBox("ACTIVE_FLG").setSelected(true);
		} else {
			getCheckBox("ACTIVE_FLG").setSelected(false);
		}
		
		callFunction("UI|CATEGORY_CODE|setEnabled", false);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
    }
    
    /**
	 * 接受返回值方法
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (StringUtils.isNotEmpty(order_code)) {
			this.setValue("ORDER_CODE", order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (StringUtils.isNotEmpty(order_desc)) {
			this.setValue("ORDER_DESC", order_desc);
		}
	}
    
    /**
     * 清空方法
     */
    public void onClear() {
    	this.onInitPage();
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
     * 得到TCheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
}
