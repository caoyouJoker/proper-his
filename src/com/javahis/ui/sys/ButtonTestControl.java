package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.util.TSystem;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ButtonTestControl extends TControl{
    /**
    * 语种
    */
    private String language;

    public ButtonTestControl() {
    }

    public void onInit() {
        super.onInit();
        //this.messageBox("ButtonTestControl come in");
    }
    public void clearText1() {
       //this.messageBox("button class test.");
//       System.out.println("ButtonTestControl test22222================");
       this.messageBox(getValue("PAT_NAME") + "有预约信息");
       this.setValue("FeeY",555);

    }

    public String getLanguage()
    {
        language=(String) TSystem.getObject("Language");
        /**System.out.println("==ButtonTestControl Language==" +
                           (String) TSystem.getObject("Language"));**/
        return language;
    }
}
