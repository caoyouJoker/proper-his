package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.ekt.EKTIO;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SingleExeOptCheckControl  extends TControl{

    public SingleExeOptCheckControl() {
    }

    public void onInit(){
        super.onInit();
    }

    public void onReadCard(){
//        String userID = EKTIO.getInstance().readUserID();
//        setValue("ID",userID);
    }

   public void onConfirm(){
       if(getValueString("ID").length() == 0){
           messageBox("Çë¶Á¿¨");
           return;
       }
       setReturnValue(getValueString("ID"));
       closeWindow();
   }
}
