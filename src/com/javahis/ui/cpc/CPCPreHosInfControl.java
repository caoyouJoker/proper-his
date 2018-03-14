package com.javahis.ui.cpc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

/**
 * ��ʹ���ġ�����ʾ�ṹ������
 * @author WangQing
 *
 */
public class CPCPreHosInfControl extends TControl {
	/**
	 * �ṹ������
	 */
	private TWord word;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		word = (TWord) this.getComponent("TWORD");
		this.onNew();	
	}
	
	/**
	 * ��������
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
			word.setMicroField("����", "");
			word.setMicroField("�Ա�", "");
			word.setMicroField("����", "");
		
			word.setWordParameter(allParm);
			word.setCanEdit(false);
			word.update();
		}
		
		
	}

}
