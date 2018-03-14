package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerConfigDialog_jButton2_actionAdapter
  implements ActionListener
{
  private OpenServerConfigDialog adaptee;
  
  OpenServerConfigDialog_jButton2_actionAdapter(OpenServerConfigDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent actionEvent)
  {
    this.adaptee.jButton2_actionPerformed(actionEvent);
  }
}
