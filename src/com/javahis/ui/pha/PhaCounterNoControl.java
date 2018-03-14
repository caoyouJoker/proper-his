package com.javahis.ui.pha;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title: 药房领药号设置</p>
 *
 * <p>Description:药房领药号设置</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 2010.03.15
 * @version 1.0
 */
public class PhaCounterNoControl extends TControl {
	//数据对象
	private TDataStore tds;
	//TABLE
	private TTable table;
	//数据使用SQL
	private String INIT_SQL="SELECT * FROM PHA_COUNTERNO ORDER BY ORG_CODE,COUNTER_NO";
	 /**
     * 初始化方法
     */
	public void onInit() {
		onInitComponent();
		onClear();
	}
	/**
	 * 初始化控件
	 */
	public void onInitComponent(){
		table=(TTable)this.getComponent("TABLE");
		table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,"onValueChanged");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onBoxClicked");
		tds=new TDataStore();
		tds.setSQL(INIT_SQL);
		tds.retrieve();
	}
	/**
	 * 清空事件
	 */
	public void onClear(){
		this.setValue("ORG_CODE", Operator.getDept());
		onQuery();
	}
	/**
	 * 查询事件
	 */
	public void onQuery(){
		onQueryForData();
	}
	/**
	 * 查询事件，为TTextFormat使用
	 */
	public void onQueryForData(){
		if(StringUtil.isNullString(this.getValueString("ORG_CODE"))){
			return;
		}
		tds.setFilter("ORG_CODE='" +this.getValueString("ORG_CODE")+
				"'");
		tds.filter();
		if(tds.rowCount()<10){
			insert10();
		}
		table.setDataStore(tds);
		table.setDSValue();

	}
	/**
	 * TABLE值改变事件
	 * @param tNode
	 * @return
	 */
	public boolean onValueChanged(TTableNode tNode){
		if(table==null){
			return true;
		}
		int column=tNode.getColumn();
		int row=tNode.getRow();
		String colName=table.getParmMap(column);
		if("COUNTER_DESC".equalsIgnoreCase(colName)){
			String py=StringUtil.onCode(tNode.getValue()+"");
			tds.setItem(row, "PY1", py);
		}
		return false;
	}
	/**
	 * checkBox事件
	 * @param obj
	 * @return
	 */
	public boolean onBoxClicked(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		return false;
	}
	/**
	 * 插入固定10条。
	 * @return
	 */
	private boolean insert10(){
		if(tds==null){
			return false;
		}
		String orgCode=this.getValueString("ORG_CODE");
		if(StringUtil.isNullString(orgCode)){
			return false;
		}
		int count=tds.rowCount();
		for(int i=count;i<10-count;i++){
			int row=tds.insertRow();
			tds.setItem(row, "ORG_CODE", orgCode);
			tds.setItem(row, "COUNTER_NO", i+1);
			tds.setItem(row, "COMMON_FLG", "Y");
			tds.setItem(row, "CTRL_FLG", "N");
			tds.setItem(row, "OPT_USER", Operator.getID());
			tds.setItem(row, "OPT_DATE", tds.getDBTime());
			tds.setItem(row, "OPT_TERM", Operator.getIP());
			tds.setActive(row,true);
		}
		tds.setFilter("ORG_CODE='" +this.getValueString("ORG_CODE")+
				"'");
		tds.filter();
		return true;
	}
	/**
	 * 保存事件
	 */
	public void onSave(){
		if(tds==null||tds.rowCount()<=0){
			return;
		}
		String[] sql=tds.getUpdateSQL();
		if(sql==null||sql.length<=0){
			this.messageBox_("没有数据");
			return;
		}
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		tds.resetModify();
		onClear();
	}
}
