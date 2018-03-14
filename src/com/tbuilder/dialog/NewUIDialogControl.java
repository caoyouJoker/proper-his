package com.tbuilder.dialog;

import com.dongyang.config.TRegistry;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;

public class NewUIDialogControl
  extends TControl
{
  String path;
  
  public void onInit()
  {
    super.onInit();
    this.path = ((String)getParameter());
    if (this.path == null) {
      this.path = "";
    }
    setValue("PATH", "目录位置为:" + this.path);
    setValue("TYPE", "TFrame");
    setValue("DATE", StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy.MM.dd"));
    String[] templates = TIOM_AppServer.listFile("%ROOT%\\config\\tbuilder\\template");
    TComboBox combo = (TComboBox)getComponent("TEMPLATE");
    combo.setVectorData(templates);
    String userID = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\TBuilder\\AUTHOR");
    setValue("AUTHOR", userID);
  }
  
  public void onOK()
  {
    if (this.path.length() == 0)
    {
      messageBox_("目录为空!");
      return;
    }
    String fileName = getValueString("FILE_NAME");
    if (fileName.length() == 0)
    {
      messageBox_("请输入文件名!");
      grabFocus("FILE_NAME");
      return;
    }
    if (fileName.indexOf("\\") > 0)
    {
      messageBox_("文件名不合法!");
      grabFocus("FILE_NAME");
      return;
    }
    if (!fileName.toUpperCase().endsWith(".X")) {
      fileName = fileName + ".x";
    }
    String type = getValueString("TYPE");
    if (type.length() == 0)
    {
      messageBox_("请选择类型!");
      grabFocus("TYPE");
      return;
    }
    TParm result = TIOM_AppServer.fileInf(this.path + "\\" + fileName);
    if (result.getErrCode() == 0)
    {
      messageBox_(fileName + "文件名已存在!");
      return;
    }
    String template = getValueString("TEMPLATE");
    String descrption = getValueString("DESCRPTION");
    String author = getValueString("AUTHOR");
    String date = getValueString("DATE");
    String co = getValueString("CO");
    StringBuffer sb = new StringBuffer();
    sb.append("#\n");
    sb.append("# TBuilder Config File \n");
    sb.append("#\n");
    String[] s = StringTool.parseLine(descrption, "\n");
    sb.append("# Title:");
    if (s.length > 0) {
      sb.append(s[0]);
    }
    sb.append("\n");
    for (int i = 1; i < s.length; i++)
    {
      sb.append("#       ");
      sb.append(s[i]);
      sb.append("\n");
    }
    sb.append("#\n");
    sb.append("# Company:");
    sb.append(co);
    sb.append("\n");
    sb.append("#\n");
    sb.append("# Author:");
    sb.append(author);
    sb.append(" ");
    sb.append(date);
    sb.append("\n");
    sb.append("#\n");
    sb.append("# version 1.0\n");
    sb.append("#\n");
    sb.append("\n");
    if (template.length() == 0) {
      sb.append(getNULL(type));
    } else {
      sb.append(getTemplate(template));
    }
    if (!TIOM_AppServer.writeFile(this.path + "\\" + fileName, sb.toString().getBytes()))
    {
      messageBox_("新建失败!");
      return;
    }
    TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\TBuilder\\AUTHOR", author);
    setReturnValue(fileName);
    closeWindow();
  }
  
  public String getTemplate(String fileName)
  {
    return new String(TIOM_AppServer.readFile("%ROOT%\\config\\tbuilder\\template\\" + fileName));
  }
  
  public String getNULL(String type)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<Type=");
    sb.append(type);
    sb.append(">\n");
    sb.append("UI.Title=\n");
    sb.append("UI.MenuConfig=\n");
    sb.append("UI.Width=1024\n");
    sb.append("UI.Height=748\n");
    sb.append("UI.toolbar=Y\n");
    sb.append("UI.controlclassname=\n");
    sb.append("UI.item=\n");
    sb.append("UI.layout=null\n");
    return sb.toString();
  }
  
  public void onCancel()
  {
    closeWindow();
  }
}
