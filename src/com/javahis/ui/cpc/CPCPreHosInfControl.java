package com.javahis.ui.cpc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

/**
 * 胸痛中心——显示结构化病例
 * @author WangQing
 *
 */
public class CPCPreHosInfControl extends TControl {
	/**
	 * 结构化病历
	 */
	private TWord word;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		word = (TWord) this.getComponent("TWORD");
		this.onNew();	
	}
	
	/**
	 * 新增病历
	 */
	public void onNew(){
		Object o = this.getParameter();
		if(o !=null){
			TParm sysParm = (TParm)o;
			String templetPath = sysParm.getValue("TEMPLET_PATH");
			String templetName = sysParm.getValue("TEMPLET_NAME");
			TParm allParm = new TParm();
			word.onOpen(templetPath, templetName, 2, true);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", "11111");
			word.setMicroField("姓名", "");
			word.setMicroField("性别", "");
			word.setMicroField("年龄", "");
		
			word.setWordParameter(allParm);
			word.setCanEdit(false);
			word.update();
		}
		
		
	}

}
