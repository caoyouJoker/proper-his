package com.javahis.ui.emr;

import jdo.emr.EMRCdrTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/**
 * 麻醉用药
 * @author Administrator
 *
 */
public class EMROpeAnaControl extends TControl{
	TTable table;
	@Override
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * 初始化页面
	 */
	public void initPage(){
		table=(TTable) this.getComponent("TABLE");
		Object obj=this.getParameter();
		TParm parm=new TParm();
		if(obj instanceof TParm)
			parm=(TParm) obj;
		TParm param=new TParm();
		param.setData("ADM_TYPE",parm.getValue("ADM_TYPE"));
		param.setData("OPE_BOOK_NO",parm.getValue("APPLY_NO"));
		TParm result=EMRCdrTool.getInstance().getOpeAnaData(param);
		table.setParmValue(result);
	}
}
