package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.MV;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.div.MVList;

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
public class MVTableDialogControl extends TControl{
     private MV mv;
     private MVList mvList;

    public MVTableDialogControl() {
    }
    /**
    * 初始化
    */
   public void onInit()
   {
       //System.out.println("============MVTableDialogControl comd in ===================");
       Object obj=this.getParameter();
       TParm parm = (TParm)obj;
       //this.messageBox("=====in parm======"+parm);
       mv=(MV)parm.getData("MV");
       mvList=(MVList)parm.getData("MV_LIST");

       //System.out.println("==============mv name==="+mv.getName());

       //this.messageBox("mv name==="+mv.getName());
       setValue("MV_NAME",mv.getName());

   }

   /**
    * 设置名称
    */
   public void onMVName() {
       mv.setName(getText("MV_NAME"));
       mvList.update();
   }



}
