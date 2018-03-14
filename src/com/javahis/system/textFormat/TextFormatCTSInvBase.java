package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

public class TextFormatCTSInvBase extends TTextFormat
{
  public String getPopupMenuSQL()
  {
    String sql = 
      " SELECT INV_CODE ID,INV_CHN_DESC AS NAME,DESCRIPTION,PY1 FROM INV_BASE WHERE INV_KIND = '08' ORDER BY INV_CODE,INV_CHN_DESC";

    return sql;
  }

  public void createInit(TObject object)
  {
    if (object == null)
      return;
    object.setValue("Width", "81");
    object.setValue("Height", "23");
    object.setValue("Text", "");
    object.setValue("HorizontalAlignment", "2");
    object.setValue("PopupMenuHeader", "代码,100;名称,150;规格,100");
    object.setValue("PopupMenuWidth", "370");
    object.setValue("PopupMenuHeight", "350");
    object.setValue("PopupMenuFilter", "ID,1;NAME,1;DESCRIPTION,1;PY1,1");

    object.setValue("FormatType", "combo");
    object.setValue("ShowDownButton", "Y");
    object.setValue("Tip", "布服种类");
    object.setValue("ShowColumnList", "ID;NAME");
  }

  public void onInit()
  {
    super.onInit();
    setPopupMenuFilter("ID,1;NAME,3;DESCRIPTION,1;PY1,1");
    setLanguageMap("NAME");
    setPopupMenuEnHeader("Code;Name");
  }

  public String getPopupMenuHeader()
  {
    return "代码,100;名称,150;规格,100";
  }

  public void getEnlargeAttributes(TAttributeList data)
  {
    data.add(new TAttributeList.TAttribute("ShowColumnList", "String", "NAME", "Left"));
    data.add(new TAttributeList.TAttribute("ValueColumn", "String", "ID", "Left"));
    data.add(new TAttributeList.TAttribute("HisOneNullRow", "boolean", "N", "Center"));
  }

  public void setAttribute(String name, String value)
  {
    super.setAttribute(name, value);
  }
}