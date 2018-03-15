package com.javahis.ui.clp;

import jdo.clp.CLPPackAgeTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 临床路径套餐字典 </p>
 *
 * <p>Description: 临床路径套餐字典</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author pangben 2015-8-10
 * @version 1.0
 */
public class CLPPackAgeControl extends TControl {
	TTable table;
	/**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
        onClear();
        onQuery();
    }
    /**
     * 
    * @Title: initPage
    * @Description: TODO(默认数据)
    * @author pangben
    * @throws
     */
    public void initPage(){
    	table=(TTable)this.getComponent("TABLE");
    	
    }
    /**
     * 
    * @Title: onClear
    * @Description: TODO(清空)
    * @author pangben
    * @throws
     */
    public void onClear(){
    	this.setValue("PACKAGE_CODE", "");
    	this.setValue("PACKAGE_DESC", "");
    	this.setValue("PY", "");
    	//String sql="SELECT MAX(SEQ_NO) SEQ_NO FROM CLP_PACKAGE ";
    	TParm result = CLPPackAgeTool.getInstance().getMaxSeqNo(new TParm());
    	if (result.getCount()>0&&result.getValue("SEQ_NO",0).length()>0) {
    		this.setValue("SEQ_NO", result.getInt("SEQ_NO",0)+1);
		}else{
			this.setValue("SEQ_NO", 0);
		}
    	TCheckBox chk=(TCheckBox)this.getComponent("CLP_PACK_FLG");
    	chk.setSelected(false);
    }
    /**
     * 
    * @Title: onQuery
    * @Description: TODO(查询)
    * @author pangben
    * @throws
     */
    public void onQuery(){
    	String packageCode=this.getConfigString("PACKAGE_CODE");
    	TParm parm=new TParm();
    	if (packageCode.length()>0) {
    		parm.setData("PACKAGE_CODE",packageCode);
		}
    	TParm result=CLPPackAgeTool.getInstance().queryPackAge(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("查询出错");
			return;
		}
    	if (result.getCount()<=0) {
			this.messageBox("没有需要查询的数据");
			table.setParmValue(new TParm());
			return;
		}else{
			table.setParmValue(result);
		}
    }
    /**
     * 
    * @Title: onSave
    * @Description: TODO(保存)
    * @author pangben
    * @throws
     */
    public void onSave(){
    	TParm parm=new TParm();
    	if (this.getValueString("PACKAGE_CODE").trim().length()<=0) {
			this.messageBox("套餐代码不可以为空");
			return;
		}
    	if (this.getValueString("PACKAGE_DESC").trim().length()<=0) {
			this.messageBox("套餐名称不可以为空");
			return;
		}
    	parm.setData("PACKAGE_CODE",this.getValueString("PACKAGE_CODE").trim());
    	parm.setData("PACKAGE_DESC",this.getValueString("PACKAGE_DESC").trim());
    	parm.setData("PY",this.getValueString("PY").trim());
    	parm.setData("CLP_PACK_FLG",this.getValueString("CLP_PACK_FLG"));
    	parm.setData("PACK_NOTE",this.getValueString("PACK_NOTE"));
    	parm.setData("SEQ_NO",this.getValueInt("SEQ_NO"));
    	parm.setData("OPT_TERM",Operator.getIP());
    	parm.setData("OPT_USER",Operator.getID());
    	//this.setValueForParm("PACKAGE_CODE;PACKAGE_DESC;PY;CLP_PACK_FLG;SEQ_NO;PACK_NOTE", parm);
    	//String packageCode=this.getConfigString("PACKAGE_CODE");
    	TParm result=CLPPackAgeTool.getInstance().queryPackAge(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("查询出现问题");
			return;
		}
    	TParm onExeParm=new TParm();
    	if (result.getCount()>0) {
    		onExeParm=CLPPackAgeTool.getInstance().onUpdate(parm);
		}else{
			onExeParm=CLPPackAgeTool.getInstance().onInsert(parm);
		}
    	if (onExeParm.getErrCode()<0) {
    		System.out.println("操作失败:"+onExeParm);
			this.messageBox("操作失败");
			return;
		}
    	if (result.getCount()>0) {
			this.messageBox("修改成功");
		}else{
			this.messageBox("添加成功");
		}
    	onClear();
    	onQuery();
    }
    /**
     * 
    * @Title: onPy
    * @Description: TODO(拼音)
    * @author pangben
    * @throws
     */
    public void onPy(){
    	this.setValue("PY", SYSHzpyTool.getInstance().charToCode(
			TypeTool.getString(getValue("PACKAGE_DESC"))));// 简拼
    }
    /**
     * 
    * @Title: onTableClicked
    * @Description: TODO(触发表格事件)
    * @author pangben
    * @throws
     */
    public void onTableClicked(){
    	int row=table.getSelectedRow();
    	if (row<0) {
			return;
		}
    	TParm tableParm=table.getParmValue();
    	this.setValueForParm("PACKAGE_CODE;PACKAGE_DESC;PY;CLP_PACK_FLG;SEQ_NO;PACK_NOTE",
    			tableParm, row);
    }
    /**
     * 
    * @Title: onDelete
    * @Description: TODO(删除操作)
    * @author pangben
    * @throws
     */
    public void onDelete(){
    	TParm parm=new TParm();
    	if (this.getValueString("PACKAGE_CODE").trim().length()<=0) {
			this.messageBox("套餐代码不可以为空");
			return;
		}
    	String sql="SELECT ORDER_CODE FROM CLP_PACK WHERE PACK_CODE='"+this.getValueString("PACKAGE_CODE").trim()+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount()>0) {
			this.messageBox("此套餐代码已使用，请修改路径医嘱");
			return;
		}
    	parm.setData("PACKAGE_CODE",this.getValueString("PACKAGE_CODE").trim());
    	result=CLPPackAgeTool.getInstance().onDelete(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("删除出现问题");
			return;
		}
    	this.messageBox("P0003");
    	onClear();
    	onQuery();
    }
}
