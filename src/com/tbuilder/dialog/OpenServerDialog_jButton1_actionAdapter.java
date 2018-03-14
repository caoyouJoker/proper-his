package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerDialog_jButton1_actionAdapter
  implements ActionListener
{
  private OpenServerDialog adaptee;
  
  OpenServerDialog_jButton1_actionAdapter(OpenServerDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    this.adaptee.jButton1_actionPerformed(e);
  }
}
