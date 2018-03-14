package com.tbuilder.dialog;

import com.dongyang.control.TControl;

public class NewDirDialogControl
  extends TControl
{
  public void onInit()
  {
    super.onInit();
    String path = (String)getParameter();
    if (path == null) {
      path = "";
    }
    setValue("PATH", "Ŀ¼λ��Ϊ:" + path);
  }
  
  public void onOK()
  {
    setReturnValue(getValue("DIR"));
    closeWindow();
  }
  
  public void onCancel()
  {
    closeWindow();
  }
}
