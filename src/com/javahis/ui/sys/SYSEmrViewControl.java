package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;

public class SYSEmrViewControl extends TControl{
	
    // TWORD
    private TWord word;
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        word = (TWord) this.getComponent("WORD");
        // 初始化数据
        TParm recptype = this.getInputParm();
		if (recptype != null) {
	        word.onOpen(recptype.getValue("FILE_PATH"), recptype.getValue("FILE_NAME"), 3, false);
		}
    }


}
