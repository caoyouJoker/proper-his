package com.javahis.ui.nss;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>
 * Title: 肠内营养容器字典
 * </p>
 * 
 * <p>
 * Description: 肠内营养容器字典
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
public class NSSEnteralNutritionContainerDictControl extends TControl {
    public NSSEnteralNutritionContainerDictControl() {
        super();
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        table = getTable("TABLE");
        onQuery();
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
    	TParm parm = new TParm();
    	parm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    	TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataRQ(parm);
    	table.setParmValue(resultParm);
    }
    
    /**
     * 保存操作
     * add by lich
     */
    public void onSave(){
    	TTextField ContainerCode = (TTextField) getComponent("CONTAINER_CODE");
    	if(ContainerCode.isEnabled()){
    		messageBox("如新增数据，请点新增按钮");
    		return;
    	}
    	TParm parm = new TParm();
    	parm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    	TParm codeFlg = NSSEnteralNutritionTool.getInstance().isExistRQ(parm);
    	
    	//新增操作
    	if(0 == Integer.parseInt(codeFlg.getValue("COUNT", 0))){
    		TParm insertParm = new TParm();
    		//医嘱代码
    		insertParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    		//医嘱中文描述
    		String chnDesc = this.getValueString("CONTAINER_DESC");
    		if(null == chnDesc || "".equals(chnDesc)){
    			messageBox("请填写营养成分中文名称");
    			return;
    		}else{    			
    			insertParm.setData("CONTAINER_DESC", chnDesc);
    		}
    		//容量
    		Double capacity = this.getValueDouble("CAPACITY");
    		if(0 == capacity){
    			messageBox("请填写容量");
    			return;
    		}else{    			
    			insertParm.setData("CAPACITY",capacity );
    		}
    		//单位
    		String capacityUnit = this.getValueString("CAPACITY_UNIT");
    		if(null == capacityUnit||"".equals(capacityUnit)){
    			messageBox("请填写单位");
    			return;
    		}else{    			
    			insertParm.setData("CAPACITY_UNIT", capacityUnit);
    		}
    		insertParm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
    		insertParm.setData("OPT_USER", Operator.getID());
    		insertParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		insertParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().insertDataRQ(insertParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("保存失败！");
    			return;
    		}else{
    			this.messageBox("保存成功！");
    		}
    		
    		//修改操作	
    	}else{
    		TParm updateParm = new TParm();
    		updateParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    		updateParm.setData("CONTAINER_DESC", this.getValueString("CONTAINER_DESC"));
    		updateParm.setData("CAPACITY", this.getValueDouble("CAPACITY"));
    		updateParm.setData("CAPACITY_UNIT", this.getValueString("CAPACITY_UNIT"));
    		updateParm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
    		
    		updateParm.setData("OPT_USER", Operator.getID());
    		updateParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		updateParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().updateDataRQ(updateParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("保存失败！");
    			return;
    		}else{
    			this.messageBox("保存成功！");
    		}
    	}
    	onClear();
    }
    
    
    
    
    
    /**
     * 删除操作
     * add by lich
     */
    public void onDelete(){
    	TParm delParm = new TParm();
    	
    	if (StringUtils.isEmpty(this.getValueString("CONTAINER_CODE"))) {
    		this.messageBox("请选择要删除数据行");
    		return;
    	}
    	
    	if (JOptionPane.showConfirmDialog(null, "是否删除选中数据？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {
    		delParm.setData("CONTAINER_CODE", this.getValue("CONTAINER_CODE"));
			TParm result =  NSSEnteralNutritionTool.getInstance().deleteDataRQ(delParm);;
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
     * 清空操作
     */
    public void onClear(){
    	this.clearValue("CONTAINER_CODE;CONTAINER_DESC;CAPACITY;CAPACITY_UNIT;ACTIVE_FLG ");
    	callFunction("UI|CONTAINER_CODE|setEnabled", true);
    	onInit();
    }
    
    
    
    /**
     * 单击表格数据后，带入基本信息
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
        this.setValue("CONTAINER_CODE", table.getItemData(row,"CONTAINER_CODE"));
        this.setValue("CONTAINER_DESC",  table.getItemData(row,"CONTAINER_DESC"));
        this.setValue("CAPACITY", table.getItemData(row,"CAPACITY"));
        this.setValue("CAPACITY_UNIT",  table.getItemData(row,"CAPACITY_UNIT"));
        this.setValue("ACTIVE_FLG", table.getItemData(row,"ACTIVE_FLG"));
       
        callFunction("UI|CONTAINER_CODE|setEnabled", false);
        callFunction("UI|CONTAINER_DESC|setEnabled", true);
    	
    }
    
    /**
     * 生成新的成分代码主键
     */
    public void onNewContainerCode(){
    	TParm maxCode = NSSEnteralNutritionTool.getInstance().getMaxContainerCode();
    	String newCode = getNewCode(maxCode.getValue("MAX", 0));
    	this.setValue("CONTAINER_CODE", newCode);
    	callFunction("UI|CONTAINER_CODE|setEnabled", false);
    }
    
    /**
     * 获取新增最大主键号NUTRITION_CODE
     * @param NutritionCode
     * @return
     * add by lich
     */
    private String getNewCode(String Code){
    	
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     * add by lich
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
