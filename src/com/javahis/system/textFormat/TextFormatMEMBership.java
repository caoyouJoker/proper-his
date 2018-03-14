package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
*
* <p>Title: 会员卡种类下拉区域</p>
*
* <p>Description: 会员卡种类下拉区域</p>

* <p>Company: BlueCore</p>
*
* @author huangtt
* @version 1.0
*/

public class TextFormatMEMBership extends TTextFormat
{


  public String getPopupMenuSQL()
  {
    
    String sql = 
      " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1 FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEMBERSHIP_CARD' ORDER BY SEQ ";
    return sql;
  }

  public void onInit() {
    super.onInit();
    setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
    setLanguageMap("NAME|ENNAME");
    setPopupMenuEnHeader("Code;Name");
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
    object.setValue("Tip", "会员卡种类下拉区域");
    object.setValue("ShowColumnList", "ID;NAME");
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