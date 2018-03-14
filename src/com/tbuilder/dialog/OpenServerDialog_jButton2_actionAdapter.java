package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerDialog_jButton2_actionAdapter
  implements ActionListener
{
  private OpenServerDialog adaptee;
  
  OpenServerDialog_jButton2_actionAdapter(OpenServerDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    this.adaptee.jButton2_actionPerformed(e);
  }
}
