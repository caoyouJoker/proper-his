package com.tbuilder.dialog;

import com.dongyang.control.TControl;

public class InputParmDialogControl
  extends TControl
{
  public void onInit()
  {
    super.onInit();
    String inputParm = (String)getParameter();
    if (inputParm == null) {
      inputParm = "";
    }
    setValue("NAME", inputParm);
  }
  
  public void onOK()
  {
    setReturnValue(getValue("NAME"));
    closeWindow();
  }
  
  public void onCancel()
  {
    closeWindow();
  }
}
