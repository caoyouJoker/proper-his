package com.tbuilder.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ProjectPropertiesDialog_jButton2_actionAdapter
  implements ActionListener
{
  private ProjectPropertiesDialog adaptee;
  
  ProjectPropertiesDialog_jButton2_actionAdapter(ProjectPropertiesDialog adaptee)
  {
    this.adaptee = adaptee;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    this.adaptee.jButton2_actionPerformed(e);
  }
}
