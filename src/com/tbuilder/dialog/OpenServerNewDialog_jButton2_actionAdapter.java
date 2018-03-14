package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OpenServerNewDialog_jButton2_actionAdapter
  implements ActionListener
{
  private OpenServerNewDialog adaptee;
  
  OpenServerNewDialog_jButton2_actionAdapter(OpenServerNewDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent actionEvent)
  {
    this.adaptee.jButton2_actionPerformed(actionEvent);
  }
}
