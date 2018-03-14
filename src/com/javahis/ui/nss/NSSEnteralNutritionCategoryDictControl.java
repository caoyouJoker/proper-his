package com.javahis.ui.nss;

import javax.swing.JOptionPane;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 肠内营养膳食分类字典
 * </p>
 * 
 * <p>
 * Description: 肠内营养膳食分类字典
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
public class NSSEnteralNutritionCategoryDictControl extends TControl {
    public NSSEnteralNutritionCategoryDictControl() {
        super();
    }

    private TTable table;
    private TTextField QUERY;
    
    /**
     * 初始化方法
     * add by lich
     */
    public void onInit() {
        table = getTable("TABLE");
    	TParm parm = new TParm();
    	onQuery();
    	QUERY = (TTextField) this.getComponent("ORDER_CODE");
    	QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
		"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
    	// 定义接受返回值方法
		QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }
    
    /**
	 * 接受返回值方法
	 * @param tag
	 * @param obj
	 * add by lich
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			QUERY.setValue("");
			this.setValue("ORDER_CODE",order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			this.setValue("ORDER_DESC",order_desc);
	}
	
    /**
     * 查询方法
     * add by lich
     */
    public void onQuery() {
    	TParm parm = new TParm();
    	parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataFL(parm);
        if(resultParm.getErrCode()<0){
			this.messageBox("查询失败！");
			return;
		}
        table.setParmValue(resultParm);
    }
    
    /**
     * 单击表格数据后，带入基本信息
     * add by lich
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
    	
        this.setValue("ORDER_CODE", table.getItemData(row,"ORDER_CODE"));
        this.setValue("ORDER_DESC",  table.getItemData(row,"ORDER_DESC"));
        this.setValue("VALID_PERIOD", table.getItemData(row,"VALID_PERIOD"));
        this.setValue("UNIT_CODE",  table.getItemData(row,"UNIT_CODE"));
        this.setValue("FREQ_PRINT_FLG", table.getItemData(row,"FREQ_PRINT_FLG"));
        this.setValue("TOTAL_PRINT_FLG", table.getItemData(row,"TOTAL_PRINT_FLG"));
        this.setValue("SHOW_NUTRITION_FLG",  table.getItemData(row,"SHOW_NUTRITION_FLG"));
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", true);
    }
    
    /**
     * 保存操作，包含增加和修改
     * add by lich
     */
    public void onSave(){
    	QUERY = (TTextField) this.getComponent("ORDER_CODE");
    	boolean flg = QUERY.isEnabled();
    	//新增操作
    	if(flg){		
    		TParm insertParm = new TParm();
    		//医嘱代码
    		insertParm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
    		//医嘱中文描述
    		insertParm.setData("ORDER_DESC", this.getValueString("ORDER_DESC"));
    		//有效期
    		double validPeriod = this.getValueDouble("VALID_PERIOD");
    		if(0 == validPeriod){
    			messageBox("请填写有效期");
    			return;
    		}else{    			
    			insertParm.setData("VALID_PERIOD", validPeriod);
    		}
    		//单位
    		String  unitCode = this.getValueString("UNIT_CODE");
    		if(null == unitCode || "".equals(unitCode)){
    			messageBox("请填写单位");
    			return;
    		}else{    			    			
        		insertParm.setData("UNIT_CODE",unitCode );
    		}
    		
    		TCheckBox freqPritnFlg = (TCheckBox) this.getComponent("FREQ_PRINT_FLG");
        	TCheckBox TotalPrintFlg = (TCheckBox) this.getComponent("TOTAL_PRINT_FLG");
        	if(freqPritnFlg.isSelected() && TotalPrintFlg.isSelected()){
        		messageBox("打印标签方式只能选择一种");
        		return;
        	}else if(!freqPritnFlg.isSelected()&& !TotalPrintFlg.isSelected()){
        		messageBox("请选择一种打印标签方式");
        		return;
        	}
    		insertParm.setData("FREQ_PRINT_FLG", this.getValueBoolean("FREQ_PRINT_FLG"));
    		insertParm.setData("TOTAL_PRINT_FLG", this.getValueBoolean("TOTAL_PRINT_FLG"));
    		insertParm.setData("SHOW_NUTRITION_FLG", this.getValueBoolean("SHOW_NUTRITION_FLG"));
   
    		insertParm.setData("OPT_USER", Operator.getID());
    		insertParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		insertParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().insertDataFL(insertParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("保存失败！");
    			return;
    		}else{
    			this.messageBox("保存成功！");
    		}
    		
    		//修改操作	
    	}else{
    		TParm updateParm = new TParm();
    		updateParm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
    		updateParm.setData("ORDER_DESC", this.getValueString("ORDER_DESC"));
    		updateParm.setData("VALID_PERIOD", this.getValueDouble("VALID_PERIOD"));
    		updateParm.setData("UNIT_CODE", this.getValueString("UNIT_CODE"));
    		updateParm.setData("FREQ_PRINT_FLG", this.getValueBoolean("FREQ_PRINT_FLG"));
    		updateParm.setData("TOTAL_PRINT_FLG", this.getValueBoolean("TOTAL_PRINT_FLG"));
    		updateParm.setData("SHOW_NUTRITION_FLG", this.getValueBoolean("SHOW_NUTRITION_FLG"));
    		
    		updateParm.setData("OPT_USER", Operator.getID());
    		updateParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		updateParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().updateDataFL(updateParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("保存失败！");
    			return;
    		}else{
    			this.messageBox("保存成功！");
    		}
    		
    	}
    	onInit();
    }
    
    /**
     * 清空操作
     * add by lich
     */
    public void onClear(){
    	this.clearValue("ORDER_CODE;ORDER_DESC;VALID_PERIOD;UNIT_CODE;" +
    			"FREQ_PRINT_FLG;TOTAL_PRINT_FLG;SHOW_NUTRITION_FLG");
    	callFunction("UI|ORDER_CODE|setEnabled", true);
    	onInit();
    }
    
    /**
     * 删除操作
     * add by lich
     */
    public void onDelete(){
    	TParm delParm = new TParm();
    	if (JOptionPane.showConfirmDialog(null, "是否删除选中数据？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {
    		delParm.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
			TParm result =  NSSEnteralNutritionTool.getInstance().deleteDataFL(delParm);;
			if(result.getErrCode()<0){
	    		this.messageBox("删除失败！");
	    	}else{
	    		this.messageBox("删除成功！");
	    		onInit();
	    		onClear();
	    	}
		}
    	onInit();
    }
    
    /**
     * 得到Table对象
     *
     * @param tagName
     *        元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
