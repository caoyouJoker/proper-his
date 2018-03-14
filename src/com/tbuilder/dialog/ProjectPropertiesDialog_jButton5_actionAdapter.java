package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ProjectPropertiesDialog_jButton5_actionAdapter
  implements ActionListener
{
  private ProjectPropertiesDialog adaptee;
  
  ProjectPropertiesDialog_jButton5_actionAdapter(ProjectPropertiesDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    this.adaptee.jButton5_actionPerformed(e);
  }
}
