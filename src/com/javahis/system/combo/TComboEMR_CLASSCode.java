package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

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
public class TComboEMR_CLASSCode extends TComboBox {
    /**
      * 新建对象的初始值
      * @param object TObject
      */
     public void createInit(TObject object)
     {
         if(object == null)
             return;
         object.setValue("Width","81");
         object.setValue("Height","23");
         object.setValue("Text","TButton");
         object.setValue("showID","Y");
         object.setValue("showName","Y");
         object.setValue("showText","N");
         object.setValue("showValue","N");
         object.setValue("showPy1","Y");
         object.setValue("showPy2","Y");
         object.setValue("Editable","Y");
         object.setValue("Tip","病历类别");
         object.setValue("TableShowList","id,name");
         object.setValue("ModuleParmString","");
         object.setValue("ModuleParmTag","");
     }
     public String getModuleName()
     {
         return "sys\\SYSEmrClassModule.x";
     }
     public String getModuleMethodName()
     {
         return "qeuryEmrClass";
     }
     public String getParmMap()
     {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
     }
      /**
      * 得到属性列表
      * @return TAttributeList
      */
     public TAttributeList getAttributes()
     {
         TAttributeList data = new TAttributeList();
         data.add(new TAttribute("Tag","String","","Left"));
         data.add(new TAttribute("Visible","boolean","Y","Center"));
         data.add(new TAttribute("Enabled","boolean","Y","Center"));
         data.add(new TAttribute("Editable","boolean","Y","Center"));
         data.add(new TAttribute("Name","String","","Left"));
         data.add(new TAttribute("Text","String","","Left"));
         data.add(new TAttribute("Tip","String","","Left"));
         data.add(new TAttribute("controlClassName","String","","Left"));
         data.add(new TAttribute("ShowID","boolean","Y","Center"));
         data.add(new TAttribute("ShowName","boolean","N","Center"));
         data.add(new TAttribute("ShowText","boolean","Y","Center"));
         data.add(new TAttribute("ShowValue","boolean","N","Center"));
         data.add(new TAttribute("ShowPy1","boolean","N","Center"));
         data.add(new TAttribute("ShowPy2","boolean","N","Center"));
         data.add(new TAttribute("TableShowList","String","","Left"));
         data.add(new TAttribute("expandWidth","int","","Right"));
         data.add(new TAttribute("Action","String","","Left"));
         data.add(new TAttribute("SelectedAction","String","","Left"));
         data.add(new TAttribute("X","int","0","Right"));
         data.add(new TAttribute("Y","int","0","Right"));
         data.add(new TAttribute("Width","int","0","Right"));
         data.add(new TAttribute("Height","int","0","Right"));
         return data;
  }
}
