package com.javahis.ui.mro;

import jdo.mro.EMRRusecopeTool;
import jdo.mro.EMRSortDicTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;

public class EMRRusecopeControl extends TControl{
	
	TTable table1; 
	TTable table2; 
	/**
     * 初始化
     */ 
    public void onInit() { 
    	table1 = (TTable)this.getComponent("TABLE1");
    	table2 = (TTable)this.getComponent("TABLE2");
    	//this.onQuery();
    	onClear1();
    	onClear2();
    	onQuery1();
    	callFunction("UI|ID1|setEnabled", false);
    	callFunction("UI|EMR_SORTDIC_NAME|setEnabled", false);
    }
    /**
     * 查询条件，将条件加入到parm中
     */
    public void onQuery1(){
    	TParm parm = new TParm();
    	//this.messageBox(this.getValue("EMR_RULE_CODE").toString());
    	//this.messageBox(this.getValue("SECURITY_CATEGORY").toString());
    	//this.messageBox(this.getValue("EMR_SCOPE_CODE").toString());
    	if(this.getValue("EMR_RULE_CODE").toString().length()>0){
    		parm.setData("EMR_RULE_CODE", this.getValue("EMR_RULE_CODE").toString());
    	}
    	if(this.getValue("SECURITY_CATEGORY_CODE").toString().length()>0){
    		parm.setData("SECURITY_CATEGORY_CODE", this.getValue("SECURITY_CATEGORY_CODE").toString());
    	}
    	if(this.getValue("EMR_SCOPE_CODE").toString().length()>0){
    		parm.setData("EMR_SCOPE_CODE", this.getValue("EMR_SCOPE_CODE").toString());
    	}
    	//this.messageBox(parm+"");
    	TParm result = EMRRusecopeTool.getInstance().onQuery(parm);
    	//System.out.println("rsul::::"+result);
    	table1.setParmValue(result);
    	
    	//this.messageBox("查询成功");
    }
    /**
     * 清空方法
     * 将页面中控件的内容清空
     */
    public void onClear1() {

        // 清空画面内容
        String clearString =
        		"ID;EMR_RULE_CODE;SECURITY_CATEGORY_CODE;EMR_SCOPE_CODE";
        clearValue(clearString);
      
        //设置页面中的控件EMR_SCOPE_CODE为可编辑状态
        setTextEnabled(true);
    } 
    /**
     * 数据上翻
     * @param parm TParm
     * @param row int
     */
    public void setTextValue(TParm parm) {
        setValueForParm("ID;EMR_RULE_CODE;SECURITY_CATEGORY_CODE;EMR_SCOPE_CODE", parm);
    }
    /**
     * 鼠标单击事件
     */
    public void clickMouse1(){
    	TParm parm = new TParm();   	
    	parm.setData("ID", (String)table1.getItemData(table1.getSelectedRow(),"ID"));   	
    	parm.setData("EMR_RULE_CODE", (String)table1.getItemData(table1.getSelectedRow(),"EMR_RULE_CODE"));
    	parm.setData("SECURITY_CATEGORY_CODE", (String)table1.getItemData(table1.getSelectedRow(),"SECURITY_CATEGORY_CODE"));   	
    	parm.setData("EMR_SCOPE_CODE", (String)table1.getItemData(table1.getSelectedRow(),"EMR_SCOPE_CODE"));
    	//调用数据上翻方法
    	this.setTextValue(parm);
    	setTextEnabled(false);
    	EMRQuery();
    }
    /**
     * 病历查询
     */
    public void EMRQuery(){
    	//String sql = "SELECT ROWNUM,EMR_CLASS_CODE FROM EMR_RULE WHERE";
    	TParm parm1 = new TParm();
//    	parm1.setData("EMR_RULE_CODE", (String)table1.getItemData(table1.getSelectedRow(),"EMR_RULE_CODE"));
//    	parm1.setData("SECURITY_CATEGORY_CODE", (String)table1.getItemData(table1.getSelectedRow(),"SECURITY_CATEGORY_CODE"));   	
//    	parm1.setData("EMR_SCOPE_CODE", (String)table1.getItemData(table1.getSelectedRow(),"EMR_SCOPE_CODE"));
    	
    	parm1.setData("EMR_RULE_CODE", this.getValueString("EMR_RULE_CODE"));
    	parm1.setData("SECURITY_CATEGORY_CODE", this.getValueString("SECURITY_CATEGORY_CODE"));
    	parm1.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	TParm result = EMRRusecopeTool.getInstance().onQueryEmr(parm1);
    	
    	//System.out.println("rsul::::"+result);
    	
    	table2.setParmValue(result);
    	
    	//this.messageBox("查询病历成功");
    }
    /**
     * 界面上不可编辑的控件
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
    	callFunction("UI|ID|setEnabled", boo);
    }
    /**
     * 保存与修改
     */
    public void onSave1(){
    	//获取页面中的 TTextField 控件
		TTextField tt=(TTextField) this.getComponent("ID");
		//保存
		if(tt.isEnabled()){
			//tt.getValueString("ID");
			//this.messageBox(tt.getValue());
			if(!StringUtil.isNullString(tt.getValue())){
				String sql ="SELECT ID FROM EMR_RUSECSCOPE WHERE ID='"+tt.getValue()+"'";
				TParm result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
				if(!StringUtil.isNullString(result.getValue("ID"))){
					this.messageBox("角色范围序号不能重复");
					return;
				}
			}
			
			TParm parm = this.writeParm();
			if(StringUtil.isNullString(parm.getValue("ID"))||StringUtil.isNullString(parm.getValue("EMR_RULE_CODE"))||
					StringUtil.isNullString(parm.getValue("SECURITY_CATEGORY_CODE"))||StringUtil.isNullString(parm.getValue("EMR_SCOPE_CODE"))){
				this.messageBox("新增失败");
				return;
			}
			
			Boolean flag = EMRRusecopeTool.getInstance().onSave(parm);
			if(flag){ 
	    		//this.messageBox("新增成功");
	    		this.onClear1();
	    		this.onQuery1();
	    		return;
	    	}
	    	this.messageBox("新增失败");	 
	    	return;
		}
		//更改
		TParm oldTParm = table1.getParmValue().getRow(table1.getSelectedRow());
		String oldemrRuleCode = oldTParm.getValue("EMR_RULE_CODE");
		String oldemrScopeCode = oldTParm.getValue("EMR_SCOPE_CODE");
		String oldSecutityCategoryCode = oldTParm.getValue("SECURITY_CATEGORY_CODE");
		//this.messageBox(oldemrRuleCode+"+"+oldemrScopeCode+"+"+oldSecutityCategoryCode+"+");
		TParm parm = this.writeParm();
		Boolean flag = EMRRusecopeTool.getInstance().onUpdata(parm);
		
		String sql = "SELECT ID FROM EMR_RULE_AUTHORITY WHERE EMR_RULE_CODE='"+oldemrRuleCode+
					 "' AND EMR_SCOPE_CODE='"+oldemrScopeCode+"' AND SECURITY_CATEGORY_CODE='"+oldSecutityCategoryCode+"'";
		//System.out.println("sss+"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
//		this.messageBox(parm.getValue("EMR_RULE_CODE"));
//		this.messageBox(parm.getValue("EMR_SCOPE_CODE"));
//		this.messageBox(parm.getValue("SECURITY_CATEGORY_CODE"));
		for(int i = 0;i<p.getCount();i++){
			String s = "UPDATE EMR_RULE_AUTHORITY "+
					   "SET EMR_RULE_CODE = '"+parm.getValue("EMR_RULE_CODE")+"', "+
					   "EMR_SCOPE_CODE = '"+parm.getValue("EMR_SCOPE_CODE")+"', "+
					   "SECURITY_CATEGORY_CODE = '"+parm.getValue("SECURITY_CATEGORY_CODE")+"' WHERE ID='"+p.getValue("ID",i)+"'";
			TJDODBTool.getInstance().update(s);

			//System.out.println("s:"+s);
		}
		if(flag){ 
    		//this.messageBox("修改成功");
    		this.onClear1();
    		this.onQuery1();
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
    	
    	if("".equals(this.getValueString("ID"))){
    		this.messageBox("序号不能为空");
    		return parm;
    	}
    	if("".equals(this.getValueString("EMR_RULE_CODE"))){
    		this.messageBox("角色不能为空");
    		return parm;
    	}
    	if("".equals(this.getValueString("SECURITY_CATEGORY_CODE"))){
    		this.messageBox("密级不能为空");
    		return parm;
    	}
    	if("".equals(this.getValueString("EMR_SCOPE_CODE"))){
    		this.messageBox("范围不能为空");
    		return parm;
    	}
    	parm.setData("ID", this.getValueString("ID"));
    	parm.setData("EMR_RULE_CODE", this.getValueString("EMR_RULE_CODE"));
    	parm.setData("SECURITY_CATEGORY_CODE", this.getValueString("SECURITY_CATEGORY_CODE"));
    	parm.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	
		return parm;
    }
    
    /**
     * 删除方法
     * 通过ID删除
     */
    public void onDelete1(){
    	TTextField tt = (TTextField)this.getComponent("ID");
    	if(tt.isEnabled()){
    		this.messageBox("删除失败");
    		return ;
    	}
    	TParm parm = new TParm();
    	String id = table1.getParmValue().getValue("ID",table1.getSelectedRow());
    	
    	//this.messageBox(id);
    	parm.setData("ID", id);
    	Boolean flag = EMRRusecopeTool.getInstance().onDelete(parm);
    	if(flag){
    		//this.messageBox("删除成功");
    		String emrRuleCode = table1.getParmValue().getValue("EMR_RULE_CODE",table1.getSelectedRow()); 
    		String securityCategoryCode = table1.getParmValue().getValue("SECURITY_CATEGORY_CODE",table1.getSelectedRow()); 
    		String emrScopeCode = table1.getParmValue().getValue("EMR_SCOPE_CODE",table1.getSelectedRow()); 
    		//this.messageBox(emrRuleCode+"\\"+securityCategoryCode+"\\"+emrScopeCode);
    		TParm parm1 = new TParm();
    		parm1.setData("EMR_RULE_CODE", emrRuleCode);
    		parm1.setData("EMR_SCOPE_CODE", emrScopeCode);
    		parm1.setData("SECURITY_CATEGORY_CODE", securityCategoryCode);
    		Boolean flag1 = EMRRusecopeTool.getInstance().onDelete1(parm1);
    		if(flag1){
    			this.onClear1();
        		this.onQuery1();
        		return;
    		}   		
    	}
    	this.messageBox("删除失败");
    }
    
    /**
     * 保存与修改
     */
    public void onSave2(){
    	//this.messageBox(this.getValueString("ID1"));
    	//this.messageBox(this.getValueString("EMR_CLASS_CODE"));
    	//this.messageBox(this.getValueString("EMR_SORTDIC_NAME"));
    	//String id = this.getValueString("ID1");
    	String sql ="SELECT MAX(CAST(ID AS INT)) MAX FROM EMR_RULE_AUTHORITY";
    	TParm result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	String id = result.getValue("MAX",0);
    	int i = Integer.parseInt(id);
    	i++;
    	//this.messageBox(i+"");
    	id = String.valueOf(i);
    	String emrRuleCode;
    	String emrScopeCode;
    	String securityCategoryCode;
    	String emrClassCode;
    	String emrDicName;
    	TTextField tt = (TTextField)this.getComponent("ID");
    	if(!tt.isEnabled()){
    		 emrRuleCode = table1.getParmValue().getValue("EMR_RULE_CODE",table1.getSelectedRow());
        	 emrScopeCode = table1.getParmValue().getValue("EMR_SCOPE_CODE",table1.getSelectedRow());
        	 securityCategoryCode = table1.getParmValue().getValue("SECURITY_CATEGORY_CODE",table1.getSelectedRow());
        	 emrClassCode = this.getValueString("EMR_CLASS_CODE");
        	 emrDicName = this.getValueString("EMR_SORTDIC_NAME");
    	}else{
    		this.messageBox("新增失败");
    		return;
    	}
    	if(StringUtil.isNullString(emrClassCode)){
    		this.messageBox("新增失败");
    		return;
    	}
    	//TTextFormat tt=(TTextFormat) this.getComponent("EMR_CLASS_CODE");
    	//this.messageBox(tt.getText());
    	//String s = tt.getText();
    	//this.messageBox(s.substring(0, s.indexOf(" ",s.indexOf("")+1)));
    	//this.messageBox(s.substring(s.indexOf(" ",s.indexOf("")+1)));
    	//emrClassCode = s.substring(0, s.indexOf(" ",s.indexOf("")+1));
    	//String emrDicName = s.substring(s.indexOf(" ",s.indexOf("")+1)+1);
    	String isRead = "1";
    	String memo = "";
    	
    	String sql1 = "SELECT ID FROM EMR_RULE_AUTHORITY WHERE EMR_RULE_CODE='"+emrRuleCode+
    				  "' AND EMR_SCOPE_CODE='"+emrScopeCode+
    				  "' AND SECURITY_CATEGORY_CODE='"+securityCategoryCode+
    				  "' AND EMR_CLASS_CODE='"+emrClassCode+"'";
    	TParm result1 =new TParm(TJDODBTool.getInstance().select(sql1.toString()));
    	//this.messageBox(result1+"");
    	if(result1.getCount()>0){
    		this.messageBox("新增失败");
    		return;
    	}
    	TParm parm = new TParm();
    	parm.setData("ID", id);
    	parm.setData("EMR_RULE_CODE", emrRuleCode);
    	parm.setData("EMR_SCOPE_CODE", emrScopeCode);
    	parm.setData("SECURITY_CATEGORY_CODE", securityCategoryCode);
    	parm.setData("EMR_CLASS_CODE", emrClassCode);
    	parm.setData("IS_READ", isRead);
    	parm.setData("MEMO", memo);
    	parm.setData("EMR_DIC_NAME", emrDicName);
    	Boolean flag = EMRRusecopeTool.getInstance().onSaveEmrRuleAuth(parm);
    	if(flag){ 
    		//this.messageBox("新增成功");
    		EMRQuery();
    		this.onClear2();
    		//this.onQuery1();
    		return;
    	}
    	this.messageBox("新增失败");	 
    	return;
    }
    /**
     * 清空方法
     * 将页面中控件的内容清空
     */
    public void onClear2() {

        // 清空画面内容
        String clearString =
        		"ID1;EMR_CLASS_CODE;EMR_SORTDIC_NAME";
        clearValue(clearString);
      
        //设置页面中的控件EMR_SCOPE_CODE为可编辑状态
       // callFunction("UI|ID1|EMR_SORTDIC_NAME|setEnabled", true);
    }
    
    /**
     * 删除方法
     * 通过ID删除
     */
    public void onDelete2(){
    	TTextField tt = (TTextField)this.getComponent("ID1");
    	if(StringUtil.isNullString(tt.getValue())){
    		this.messageBox("删除失败");
    		return ;
    	}
    	TParm parm = new TParm();
    	String id = table2.getParmValue().getValue("ID",table2.getSelectedRow());
    	parm.setData("ID", id);
    	Boolean flag = EMRRusecopeTool.getInstance().onDeleteEMR(parm);
    	if(flag){
    		//this.messageBox("删除成功");
    		this.EMRQuery();
    		this.onClear2();
    		return;
    	}
    	this.messageBox("删除失败");
    }
    public void clickMouse2(){
    	TParm parm = new TParm(); 
    	String emrClassCode = table2.getParmValue().getValue("EMR_CLASS_CODE",table2.getSelectedRow());
    	String emrDicName = table2.getParmValue().getValue("EMR_DIC_NAME",table2.getSelectedRow());
    	String id = table2.getParmValue().getValue("ROWNUM",table2.getSelectedRow());
    	
    	parm.setData("ID1", id);
    	parm.setData("EMR_CLASS_CODE", emrClassCode);
    	parm.setData("EMR_SORTDIC_NAME", emrDicName);
    	//调用数据上翻方法
    	setValueForParm("ID1;EMR_CLASS_CODE;EMR_SORTDIC_NAME", parm);
    	callFunction("UI|EMR_SORTDIC_NAME|setEnabled", false);
    	//setTextEnabled(false);
    }
    public void clickCom(){
    	String emrDesc = this.getValueString("EMR_CLASS_CODE").trim();
    	//this.messageBox(emrDesc+"");
    	String sql = "SELECT EMR_SORTDIC_NAME AS NAME FROM EMR_SORTDIC WHERE EMR_SORTDIC_CODE= '"+emrDesc+"'";
    	TParm result =new TParm(TJDODBTool.getInstance().select(sql));
    	//this.messageBox(result+"");
    	String s = result.getValue("NAME",0);
    	//this.messageBox(s);
    	setValue("EMR_SORTDIC_NAME", s);
    	//setValueForParm("EMR_SORTDIC_NAME", result);
    	callFunction("UI|EMR_SORTDIC_NAME|setEnabled", false);
    	//this.messageBox("12"); 
    }
}
