package com.javahis.ui.emr;

import com.dongyang.control.*;

/**
 * <p>Title: HIS医疗系统</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ModifControl extends TControl {
    /**
     * 初始化参数
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if(obj!=null){
            this.setValue("TextArea",obj.toString());
        }
    }
    /**
     * 传回
     */
    public void onSend(){
       this.setReturnValue(this.getValue("TextArea"));
       this.closeWindow();
    }
}
