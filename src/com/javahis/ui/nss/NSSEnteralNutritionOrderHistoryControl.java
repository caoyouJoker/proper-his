package com.javahis.ui.nss;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 肠内营养定制历史记录</p>
 *
 * <p>Description: 肠内营养定制历史记录</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.4.20
 * @version 1.0
 */
public class NSSEnteralNutritionOrderHistoryControl extends TControl {
	
	private TTable tableM;
	private TTable tableD;
	private TParm parameterParm;
	
    public NSSEnteralNutritionOrderHistoryControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
		this.onInitPage();
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		} else {
			this.messageBox("无传参数据");
			return;
		}
    	
		// 病患信息表格数据点击事件
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
		// 查询指定病患本次住院的所有定制配方数据
		TParm result = this.queryData();
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询历史数据错误");
			return;
		}
		
		tableM.setParmValue(result);
	}
	
	/**
	 * 查询指定病患本次住院的所有定制配方数据
	 */
	private TParm queryData() {
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", parameterParm.getValue("CASE_NO"));
		queryParm.setData("ORDER_DATE_SORT", "Y");
		// 根据选中的住院医生开立的饮食医嘱数据行查询对应的营养师医嘱主项数据
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(queryParm);
		
		int count = result.getCount();
		// 去除新建医嘱本身
		for (int i = count - 1; i > -1; i--) {
			if (StringUtils.equals(parameterParm.getValue("EN_ORDER_NO"),
					result.getValue("EN_ORDER_NO", i))) {
				result.removeRow(i);
				break;
			}
		}
		return result;
	}
	
	/**
	 * 添加对TABLE_M的监听事件
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm parm = tableM.getParmValue().getRow(row);
		// 根据营养师医嘱主项查询对应的配方明细SQL
		String sql = NSSEnteralNutritionTool.getInstance().queryENOrderDSql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询配方明细错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("FLG", i, "N");
		}
		
		tableD.setParmValue(result);
	}
	
	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		if (tableD.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableD.getRowCount(); i++) {
			tableD.setItem(i, "FLG", flg);
		}
	}
	
    /**
     * 传回方法
     */
    public void onReturn() {
    	// 强制失去编辑焦点
		if (tableD.getTable().isEditing()) {
			tableD.getTable().getCellEditor().stopCellEditing();
		}
    	
        if (tableD.getRowCount() <= 0) {
        	this.messageBox("请勾选配方明细");
            return;
        }
        TParm parm = tableD.getParmValue();
        boolean checkFlg = false;
        
        for (int i = 0; i < parm.getCount(); i++) {
        	if (parm.getBoolean("FLG", i)) {
        		checkFlg = true;
        		break;
        	}
        }
        
        if (!checkFlg) {
        	this.messageBox("请勾选配方明细");
        	return;
        }
        
        setReturnValue(parm);
        this.closeWindow();
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
	 * 得到getCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
