package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 健康检查合同变更控制类</p>
 *
 * <p>Description: 健康检查合同变更控制类</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */

public class HRMChangeContractControl extends TControl{

	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		//初始化数据
		initData();
	}
	TParm showParm=null;
	TParm dataStoreParm=null;
	/**
	 * 初始化数据
	 */
	private void initData() {
		showParm=(TParm)((TParm)this.getParameter()).getData("showParm");
		dataStoreParm=(TParm)((TParm)this.getParameter()).getData("dataStoreParm");
		((TTextField)this.getComponent("MR_NO")).setText(showParm.getValue("MR_NO"));
		((TTextField)this.getComponent("PAT_NAME")).setText(showParm.getValue("PAT_NAME"));
		((TTextField)this.getComponent("SEX_CODE")).setText(showParm.getValue("SEX_CODE"));
		((TTextField)this.getComponent("BIRTHDAY")).setText(showParm.getValue("BIRTHDAY"));
		((TTextField)this.getComponent("STAFF_NO")).setText(showParm.getValue("STAFF_NO"));
		((TTextField)this.getComponent("PAT_DEPT")).setText(showParm.getValue("PAT_DEPT"));
		((TTextField)this.getComponent("CONTRACT_DESC")).setText(showParm.getValue("CONTRACT_DESC"));
		
	
//		mrTab.setDataStore(contractD);
	}
	public void onSave() {
		String value=(String)((TTextFormat)this.getComponent("CONTRACT")).getValue();
		if(value.equals(dataStoreParm.getValue("CONTRACT_CODE"))){
			this.messageBox("变更前后的合同名称不能相同。");
			return ;
			
		}
		TParm parm=new TParm();
		parm.setData("COMPANY_CODE",((TTextFormat)this.getComponent("COMPANY_CODE")).getValue());
		parm.setData("CONTRACT_CODE",  ((TTextFormat)this.getComponent("CONTRACT")).getValue());
		parm.setData("CONTRACT_DESC", ((TTextFormat)this.getComponent("CONTRACT")).getText());
		this.setReturnValue(parm);
		this.closeWindow();
	}
}
