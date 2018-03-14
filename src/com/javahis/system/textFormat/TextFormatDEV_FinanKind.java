package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

public class TextFormatDEV_FinanKind extends TTextFormat
{
  public String getPopupMenuSQL()
  {
    String sql = 
      " SELECT  FINAN_KIND AS ID,FINAN_DESC AS NAME FROM DEV_FINANKIND ORDER BY CLASSIFY,FINAN_KIND ";
    return sql;
  }

  public void createInit(TConfigParse.TObject object)
  {
    if (object == null)
      return;
    object.setValue("Width", "81");
    object.setValue("Height", "23");
    object.setValue("Text", "");
    object.setValue("HorizontalAlignment", "2");
    object.setValue("PopupMenuHeader", "代码,100;名称,100");
    object.setValue("PopupMenuWidth", "300");
    object.setValue("PopupMenuHeight", "300");
    object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
    object.setValue("FormatType", "combo");
    object.setValue("ShowDownButton", "Y");
    object.setValue("Tip", "资金来源");
    object.setValue("ShowColumnList", "ID;NAME");
  }
  public void onInit() {
    super.onInit();
    setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
    setLanguageMap("NAME|ENNAME");
    setPopupMenuEnHeader("Code;Name");
  }

  public String getPopupMenuHeader()
  {
    return "代码,100;名称,200";
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