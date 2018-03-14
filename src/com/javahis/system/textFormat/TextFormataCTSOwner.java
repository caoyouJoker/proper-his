package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

public class TextFormataCTSOwner extends TTextFormat
{
  public String getPopupMenuSQL()
  {
    String sql = 
      " SELECT USER_ID AS ID,USER_NAME AS NAME FROM CTS_OWNER ORDER BY USER_ID ";
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
    object.setValue("PopupMenuHeader", "ID,100;NAME,100");
    object.setValue("PopupMenuWidth", "300");
    object.setValue("PopupMenuHeight", "300");
    object.setValue("PopupMenuFilter", "ID,1;NAME,1");

    object.setValue("FormatType", "combo");
    object.setValue("ShowDownButton", "Y");
    object.setValue("Tip", "œ¥“¬»À‘±");
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
    return "ID,100;NAME,200";
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