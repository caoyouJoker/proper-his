package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerConfigDialog_jButton3_actionAdapter
  implements ActionListener
{
  private OpenServerConfigDialog adaptee;
  
  OpenServerConfigDialog_jButton3_actionAdapter(OpenServerConfigDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    this.adaptee.jButton3_actionPerformed(e);
  }
}
