package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerConfigDialog_jButton1_actionAdapter
  implements ActionListener
{
  private OpenServerConfigDialog adaptee;
  
  OpenServerConfigDialog_jButton1_actionAdapter(OpenServerConfigDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent actionEvent)
  {
    this.adaptee.jButton1_actionPerformed(actionEvent);
  }
}
