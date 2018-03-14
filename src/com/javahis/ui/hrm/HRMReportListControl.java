package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 健康检查队列设置控制类 </p>
 * 
 * <p> Description: 健康检查队列设置控制类  </p>
 * 
 * <p> Copyright: javahis 20100504 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMReportListControl extends TControl {
	//table
	private TTable table;
	//data
	private TDS tds;
	//SQL
	private final static String SQL="SELECT * FROM HRM_REPORTLIST ORDER BY LIST_CODE,DETAIL_SEQ";
	//检核主键是否重复
	private final static String CHECK_RESIST="SELECT COUNT(LIST_CODE) FROM HRM_REPORTLIST WHERE LIST_CODE='#' AND DETAIL_SEQ=#";
	 /**
     * 初始化
     */
    public void onInit() {
    	onInitComponent();
    	onInitData();
    }
    /**
     * 初始化控件
     */
    public void onInitComponent(){
    	table=(TTable)this.getComponent("TABLE");
		//套餐主项值改变事件
    	table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onValueChanged");
    }
    /**
     * 初始化数据
     */
    public void onInitData(){
    	tds=new TDS();
    	tds.setSQL(SQL);
    	tds.retrieve();
    	tds.insertRow();
    	table.setDataStore(tds);
    	
    }
    /**
     * 清空事件
     */
    public void onClear(){
    	table.setDSValue();
    }
    /**
     * 保存事件
     */
    public void onSave(){
    	TParm result=new TParm(TJDODBTool.getInstance().update(tds.getUpdateSQL()));
    	if(result.getErrCode()!=0){
    		this.messageBox("E0002");
    		return;
    	}
    	this.messageBox("P0002");
    	tds.resetModify();
    }
    /**
     * 删除事件
     */
    public void onDelete(){
    	if(table==null){
    		return;
    	}
    	int row=table.getSelectedRow();
    	if(row<0){
    		return;
    	}
    	tds.deleteRow(row);
    	onSave();
    }
    /**
     * 查询事件
     */
    public void onQuery(){
    	
    }
    /**
	 * 值改变事件
	 * @param tNode
	 * @return
	 */
	public boolean onValueChanged(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=tNode.getTable().getParmMap(column);
		tNode.getTable().acceptText();
		if("LIST_DESC".equalsIgnoreCase(colName)){
			String py1=StringUtil.getInstance().onCode(tNode.getValue()+"");
			table.getDataStore().setItem(row, "LIST_PY1", py1);
			table.setDSValue();
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			table.setSelectedColumn(column+1);
			return false;
		}
		if("DETAIL_SEQ".equalsIgnoreCase(colName)){
			int seq=StringTool.getInt(tNode.getValue()+"");
			String listCode=tds.getItemString(row, "LIST_CODE");
			String sql=CHECK_RESIST.replaceFirst("#", listCode).replaceFirst("#", seq+"");
			TParm result=new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()!=0){
				this.messageBox_("已存在相同的队列代码和细项序号");
				return true;
			}
		}
		if("DETAIL_DESC".equalsIgnoreCase(colName)){
			if(row==tds.rowCount()-1&&!StringUtil.isNullString(tds.getItemString(tds.rowCount()-1, "LIST_CODE"))){
				this.messageBox_("herer");
				tds.insertRow();
				table.setDSValue();
			}
			return false;
		}
		return false;
	}
}
