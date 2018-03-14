package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerNewDialog_jButton1_actionAdapter
  implements ActionListener
{
  private OpenServerNewDialog adaptee;
  
  OpenServerNewDialog_jButton1_actionAdapter(OpenServerNewDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent actionEvent)
  {
    this.adaptee.jButton1_actionPerformed(actionEvent);
  }
}
