package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

public class SYSEmrViewControl extends TControl{
	
    // TWORD
    private TWord word;
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        word = (TWord) this.getComponent("WORD");
        // ��ʼ������
        TParm recptype = this.getInputParm();
		if (recptype != null) {
	        word.onOpen(recptype.getValue("FILE_PATH"), recptype.getValue("FILE_NAME"), 3, false);
		}
    }


}
