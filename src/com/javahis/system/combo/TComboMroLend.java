package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.config.TConfigParse.TObject;

/**
 * <p>Title: 病案借阅原因</p>
 *
 * <p>Description: 病案借阅原因</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangk 2009-5-6
 * @version 1.0
 */
public class TComboMroLend
    extends TComboBox {
    /**
      * 新建对象的初始值
      * @param object TObject
      */
     public void createInit(TObject object) {
         if (object == null)
             return;
         object.setValue("Width", "81");
         object.setValue("Height", "23");
         object.setValue("Text", "TButton");
         object.setValue("showID", "Y");
         object.setValue("showName", "Y");
         object.setValue("showText", "N");
         object.setValue("showValue", "N");
         object.setValue("showPy1", "Y");
         object.setValue("showPy2", "Y");
         object.setValue("Editable", "Y");
         object.setValue("Tip", "病案借阅原因");
         object.setValue("TableShowList", "id,name");
         object.setValue("ModuleParmString", "");
         object.setValue("ModuleParmTag", "");
     }

     public String getModuleName() {
         return "sys\\SYSMROComboModule.x";
     }

     public String getModuleMethodName() {
         return "getMroLend";
     }

     public String getParmMap() {
        return "id:LEND_CODE;name:LEND_DESC;enname:LEND_DESC;Py1:PY1;py2:PY2";
     }

}
