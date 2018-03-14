package com.javahis.ui.inv;


import jdo.inv.InvKindQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 物资分类查询</p>
 *
 * <p>Description: 物资分类查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author lij 2016.11.16
 * @version 1.0
 */
public class INVKindQueryControl extends TControl{
	private TTable table;
	public INVKindQueryControl(){
		
	}
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		table = (TTable)this.getComponent("TABLE");
	}
		
	
    /**
	 * 查询
	 */
    public void onQuery(){
    	String sql = " SELECT ID,CHN_DESC,OPT_USER,OPT_TERM,OPT_DATE FROM SYS_DICTIONARY WHERE GROUP_ID='INV_BASE_KIND' ";
		String inv_id = this.getValueString("ID");
		String inv_chn_desc = this.getValueString("CHN_DESC");
		if(!inv_id.equals("")){
			sql += " AND ID LIKE '%"+inv_id+"%' ";
		}
		
		if(!inv_chn_desc.equals("")){
			sql += " AND CHN_DESC LIKE '%"+inv_chn_desc+"%' ";
		}
		sql += " ORDER BY SEQ,ID ";
		System.out.println("sql>>>>>>>>"+sql);
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getErrCode() < 0 ){
    		this.messageBox(mParm.getErrText());
    		return;
    	}
    	if(mParm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		return;
    	}
    	
    	table.setParmValue(mParm);
    }
    /**
	 * 保存
	 */
	public void onSave(){
		TParm parm = new TParm();
		String inv_id = this.getValueString("ID");
		String inv_chn_desc = this.getValueString("CHN_DESC");
		if(inv_id.equals("")){
			this.messageBox("分类编号不能为空!");
			return;
		}
		if(inv_chn_desc.equals("")){
			this.messageBox("分类名称不能为空!");
			return;
		}
		parm.setData("GROUP_ID","INV_BASE_KIND");
        parm.setData("ID",this.getValueString("ID"));
        parm.setData("CHN_DESC",this.getValueString("CHN_DESC"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("ENG_DESC","");
        parm.setData("PY1",this.onKindAction());
        parm.setData("PY2","");
		parm.setData("SEQ",this.getMaxSeq());
        parm.setData("DESCRIPTION","");
        parm.setData("TYPE","");
        parm.setData("PARENT_ID","");
        parm.setData("STATE","");
        parm.setData("DATA","");
        parm.setData("STA1_CODE","");
        parm.setData("STA2_CODE","");
        parm.setData("STA3_CODE","");
        parm.setData("FLG","N");
        System.out.println(parm);
        TParm result = new TParm();
        TParm result1 = new TParm();
        String sql = " SELECT ID,CHN_DESC,OPT_USER,OPT_TERM,OPT_DATE FROM SYS_DICTIONARY WHERE GROUP_ID='INV_BASE_KIND' AND ID='"+inv_id+"'";
        if (table.getSelectedRow() < 0 ) {
            // 新增方法
        	result1 = new TParm( TJDODBTool.getInstance().select(sql));
//        	System.out.println("sql:"+sql);
//        	System.out.println("++++++++++"+result1.getCount());
        	if(result1.getCount() > 0 ){
        		this.messageBox("物资分类编号已存在！请重新输入");
        	}else{
        		result = InvKindQueryTool.getInstance().onInsert(parm);
        		this.messageBox("保存成功");
        		onQuery();
        	}
        }
//        else{
//            // 更新方法
//        	result = InvKindQueryTool.getInstance().onUpdate(parm);
//        	result1 = new TParm( TJDODBTool.getInstance().select(sql));
////        	System.out.println("================"+result1.getCount());
//        	if(result1.getCount() > 0){
//        		this.messageBox("保存成功");
//        		onQuery();
//        	}else{
//        		this.messageBox("物资分类编号无法更改");
//        	}
//        }
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
            return;
        }
	}
	/**
     * 清空
     */
	 public void onClear(){
	    	this.clearValue("ID;CHN_DESC");
	    	TTable tableM = this.getTable("TABLE");
	    	tableM.removeRowAll();
	    	callFunction("UI|ID|setEnabled", true);
	        callFunction("UI|CHN_DESC|setEnabled", true);
	    }
	 /**
	  * 删除
	  */
    public void onDelete(){
    	TParm result = new  TParm();
    	TParm result1 = new TParm();
    	String id = null;
    	 if (table.getSelectedRow() < 0) {
             this.messageBox("请选择删除行");
             return;
         }
    	 String sql = " SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID='INV_BASE_KIND' AND ID IN (SELECT DISTINCT INV_KIND FROM INV_BASE ) ";
    	 result1 = new TParm( TJDODBTool.getInstance().select(sql));
         TParm parm = table.getParmValue().getRow(table.getSelectedRow());
         String flg = null;
         for(int i = 0 ; i<result1.getCount();i++){
//        	 System.out.println("??????????????????:"+result1.getData("ID",i));
        	 id = result1.getData("ID",i).toString();
//        	 System.out.println("1111111111111111111:"+parm.getData("ID"));
        	 if(parm.getData("ID").equals(id)){
        		 flg = "N";
        		 break;
        	 }
        	 else{
       		     flg = "Y";
        	 }
         }
         if(flg.equals("N")){
        	 this.messageBox("物资字典正在使用中,不允许删除！");
        	 return;
         }else{
        	 result = InvKindQueryTool.getInstance().onDelete(parm);
        	 table.removeRow(table.getSelectedRow());
        	 this.messageBox("删除成功");
         }
         if (result.getErrCode() < 0) {
             this.messageBox("删除失败");
             return;
         }
    }
    /**
     * onKindAction回车事件
     * @return 
     */
    public String onKindAction() {
        String py = TMessage.getPy(this.getValueString("CHN_DESC"));
        setValue("PY1", py);
        return py;
//        System.out.println("py>>>>>>"+py);
    }
    
    /**
     * 表格单击事件
     */
    public void onTableClick() {
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        this.setValueForParm("ID;CHN_DESC", parm);
     // 改变输入框状态
        callFunction("UI|ID|setEnabled", false);
        callFunction("UI|CHN_DESC|setEnabled", false);
        
    }
   
    /**
     * 得到Table对象
     * @param tagName
     * @return
     */
    private TTable getTable(String tagName){
    	return (TTable) this.getComponent(tagName);
    }
    /**
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq() {
    	TParm result = InvKindQueryTool.getInstance().onfindMaxSeq();
    	int max = TypeTool.getInt(result.getData("SEQ", 0));
    	max++;
    	return max;
    }
}


