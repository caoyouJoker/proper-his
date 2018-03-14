package com.javahis.ui.mro;

import jdo.mro.EMRScopeTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class EMRScopeControl extends TControl {

	
	TTable table; 
	/**
     * 初始化
     */ 
    public void onInit() { 
    	table = (TTable)this.getComponent("TABLE");
    	//this.onQuery();
    }
    /**
     * 查询条件，将条件加入到parm中
     */
    public void onQuery(){
    	TParm parm = new TParm();
    	if(this.getValue("EMR_SCOPE_CODE").toString().length()>0){
    		parm.setData("EMR_SCOPE_CODE", this.getValue("EMR_SCOPE_CODE").toString());
    	}
    	//将页面中的PRG_ID加入到parm中
    	if(this.getValue("IN_HOP_DEPT_DOC").toString().length()>0){
    		parm.setData("IN_HOP_DEPT_DOC", this.getValue("IN_HOP_DEPT_DOC").toString());
    	}
    	//将页面中的OPT_USER加入到parm中
    	if(this.getValue("EMR_SCOPE_NAME").toString().length()>0){
    		parm.setData("EMR_SCOPE_NAME", this.getValue("EMR_SCOPE_NAME").toString());
    	}
    	
    	TParm result = EMRScopeTool.getInstance().onQuery(parm);
    	System.out.println("rsul::::"+result);
    	
    	table.setParmValue(result);
    	
    	//this.messageBox("查询成功");
    }
    /**
     * 保存/修改
     */
    public void onSave(){
    	
    	//获取页面中的 TTextField 控件
		TTextField tt=(TTextField) this.getComponent("EMR_SCOPE_CODE");
		//保存
		if(tt.isEnabled()){
			TParm parm = this.writeParm();
	    	Boolean flag = EMRScopeTool.getInstance().onSave(parm);
	    	if(flag){ 
	    		this.messageBox("新增成功");
	    		this.onClear();
	    		this.onQuery();
	    		return;
	    	}
	    	this.messageBox("新增失败");	 
	    	return;
		}
		//更改
		TParm parm = this.writeParm();
		Boolean flag = EMRScopeTool.getInstance().onUpdata(parm);
		if(flag){ 
    		this.messageBox("修改成功");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("修改失败");		
    	return;
    	
    }
    
    /**
     * 获取页面中选框的内容
     * @return
     */
    public TParm writeParm(){
    	
    	TParm parm = new TParm();
    	
    	if("".equals(this.getValueString("EMR_SCOPE_CODE"))){
    		this.messageBox("编码范围不能为空");
    		return parm;
    	}
    	if("".equals(this.getValueString("IN_HOP_DEPT_DOC"))){
    		this.messageBox("范围不能为空");
    		return parm;
    	}
    	if("".equals(this.getValueString("EMR_SCOPE_NAME"))){
    		this.messageBox("范围名称不能为空");
    		return parm;
    	}
    	parm.setData("EMR_SCOPE_NAME", this.getValueString("EMR_SCOPE_NAME"));
    	parm.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", this.getValueString("IN_HOP_DEPT_DOC"));
    	parm.setData("MEMO", this.getValueString("MEMO"));
    	
		return parm;
    	
    	
    	
    }
    
    /**
     * 清空方法
     * 将页面中控件的内容清空
     */
    public void onClear() {
    	
        // 清空画面内容
        String clearString =
        		"EMR_SCOPE_NAME;EMR_SCOPE_CODE;IN_HOP_DEPT_DOC;MEMO";
        clearValue(clearString);
      
        //设置页面中的控件EMR_SCOPE_CODE为可编辑状态
        setTextEnabled(true);
        //getComboBox("EMR_SCOPE_CODE").setEnabled(true);
       
        
    } 
 
    /**
     * 界面上不可编辑的控件
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
    	callFunction("UI|EMR_SCOPE_CODE|setEnabled", boo);
    }
    /**
     * 鼠标单击事件
     */
    public void clickMouse(){
    	TParm parm = new TParm();   	
    	parm.setData("EMR_SCOPE_NAME", (String)table.getItemData(table.getSelectedRow(),"EMR_SCOPE_NAME"));   	
    	parm.setData("EMR_SCOPE_CODE", (String)table.getItemData(table.getSelectedRow(),"EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", (String)table.getItemData(table.getSelectedRow(),"IN_HOP_DEPT_DOC"));   	
    	parm.setData("MEMO", (String)table.getItemData(table.getSelectedRow(),"MEMO"));
    	//调用数据上翻方法
    	this.setTextValue(parm);
    	setTextEnabled(false);
    }
    
    /**
     * 数据上翻
     * @param parm TParm
     * @param row int
     */
    public void setTextValue(TParm parm) {
        setValueForParm("EMR_SCOPE_NAME;EMR_SCOPE_CODE;IN_HOP_DEPT_DOC;MEMO", parm);
    }
    /**
     * 新增功能
     */
    public void onAdd(){
    	
    	TParm parm = new TParm();
    	
    	if("".equals(this.getValueString("EMR_SCOPE_CODE"))){
    		this.messageBox("编码范围不能为空");
    		return;
    	}
    	if("".equals(this.getValueString("IN_HOP_DEPT_DOC"))){
    		this.messageBox("范围不能为空");
    		return;
    	}
    	if("".equals(this.getValueString("EMR_SCOPE_NAME"))){
    		this.messageBox("范围名称不能为空");
    		return;
    	}
    	String sql ="SELECT MAX(ID) MAX FROM EMR_SCOPE";
    	TParm result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	String s = result.getValue("MAX",0);  	
    	String id = StringTool.addString(s);
    	//this.messageBox(id);
    	parm.setData("ID", id);
    	parm.setData("EMR_SCOPE_NAME", this.getValueString("EMR_SCOPE_NAME"));
    	parm.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", this.getValueString("IN_HOP_DEPT_DOC"));
    	parm.setData("MEMO", this.getValueString("MEMO"));
    	Boolean flag = EMRScopeTool.getInstance().onSave(parm);
    	if(flag){ 
    		this.messageBox("新增成功");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("新增失败");	 
    }
    /**
     * 删除方法
     * 通过ID删除
     */
    public void onDelete(){
    	
    	TParm parm = new TParm();
    	String id = table.getParmValue().getValue("EMR_SCOPE_CODE",table.getSelectedRow());
    	
//    	this.messageBox(id);
    	parm.setData("EMR_SCOPE_CODE", id);
    	Boolean flag = EMRScopeTool.getInstance().onDelete(parm);
    	if(flag){
    		this.messageBox("删除成功");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("删除失败");
    }
}
                                                  