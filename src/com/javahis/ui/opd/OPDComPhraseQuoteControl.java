package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import com.dongyang.util.RunClass;
import com.dongyang.util.TypeTool;

/**
 * 
 * <p>Title: 片语Panel<p/>
 * <p>Description:片语Panel<p/>
 * <p>Copyright: Copyright (c) Liu dongyang 2008<p/>
 * <p>Company:Javahis<p/>
 * <p>@author ehui 20080924<p/>
 * <p>@version 1.0<p/>
 */
public class OPDComPhraseQuoteControl extends TControl {
	String sqlChangeDept=" SELECT * FROM OPD_COMPHRASE WHERE DEPT_OR_DR='1' ORDER BY DEPT_OR_DR,DEPTORDR_CODE,PHRASE_CODE ";
	String sqlChangeDr=" SELECT * FROM OPD_COMPHRASE WHERE DEPT_OR_DR='2' ORDER BY DEPT_OR_DR,DEPTORDR_CODE,PHRASE_CODE ";
	Object mainControl;
	String tag,value;
	TTable table;
	
	public void onInit() {
		super.onInit();
		table=(TTable)this.getComponent("TABLECOM");
		TParm parm=this.getInputParm();
		mainControl=(Object)parm.getData("subject");
		tag=(String)this.getInputParm().getData("TAG");

	}
	/**
	 * TABLE单击事件
	 */
	public void onTABLEPATClicked(){
		TDataStore tds=table.getDataStore();
		int row=table.getSelectedRow();
		String oldvalue=this.getValueString("CONTENT");
		this.setValue("CONTENT",oldvalue+tds.getItemData(row, "PHRASE_TEXT"));
	}
	/**
	 * TABLE双击事件
	 */
	public void onTABLEPATDoubleClicked(){
		TDataStore tds=table.getDataStore();
		int row=table.getSelectedRow();
		String oldvalue=tds.getItemString(row, "PHRASE_TEXT");
		this.setValue("CONTENT",oldvalue);
		TParm parm = new TParm();
		parm.setData("CONTENT",oldvalue);
		value=oldvalue;
		onFetchBack(oldvalue);
	}
	/**
	 * 部门或医师RADIO点击事件
	 */
	public void onChange(){
		TDataStore tds=table.getDataStore();
		if("Y".equalsIgnoreCase(getValue("DEPT").toString()) ){
			this.setText("LABEL", "科室");
			tds.setSQL(sqlChangeDept);
			tds.retrieve();
			
		}else{
			this.setText("LABEL", "医师");
			tds.setSQL(sqlChangeDr);
			tds.retrieve();
		}
			
	}
	/**
	 * 关闭
	 */
	public boolean onClosing(){
		this.setReturnValue(this.getValue("CONTENT"));
		return true;
	}
	/**
	 * 传回
	 */
	public void onFetchBack(){
		Object[] oldParam=new Object[]{
				tag
		};
		String oldvalue=TypeTool.getString(RunClass.runMethod(mainControl, "getValue", oldParam));
		Object[] parameters=new Object[]{
				tag, oldvalue+this.getValueString("CONTENT")
		};
		RunClass.runMethod(mainControl, "setValue", parameters);
	}
	/**
	 * 传回
	 * @param value
	 */
	public void onFetchBack(String value){
		Object[] oldParam=new Object[]{
				
		};
		String oldvalue=TypeTool.getString(RunClass.runMethod(mainControl, "getValue", oldParam))+value;
		String tagName=(String)RunClass.runMethod(mainControl, "getFocusTag", new Object[]{});
		if("FAMILY_HISTORY".equalsIgnoreCase(tag)){
			tagName=tag;
		}
		Object[] parameters=new Object[]{
				tagName, oldvalue
		};
		this.setValue("CONTENT",oldvalue);
		RunClass.runMethod(mainControl, "setValue", parameters);
	}
}
