package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 收费等级COMBO</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboINS_FEE_TYPECode extends TComboBox {
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
      object.setValue("Tip","收费等级");
      object.setValue("TableShowList","id,name");
      object.setValue("ModuleParmString","");
      object.setValue("ModuleParmTag","");
  }
  public String getModuleName()
  {
      return "sys\\SYSComboSQL.x";
  }
  public String getModuleMethodName()
  {
      return "getInsFeeTypeCode";
  }
  public String getParmMap()
  {
      return "id:FEE_TYPE;name:FEE_TYPE_DESC";
  }
  /**
   * 增加扩展属性
   * @param data TAttributeList
   */
  public void getEnlargeAttributes(TAttributeList data){
  }
}
