package com.tbuilder.dialog;

import com.dongyang.config.TConfig;
import com.tbuilder.main.Tik;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectPropertiesDialog
  extends JDialog
{
  public ProjectPropertiesDialog()
  {
    setModal(true);
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void open()
  {
    setLocation(300, 150);
    setSize(600, 400);
    setVisible(true);
    onInitValue();
  }
  
  public void onInitValue()
  {
    TConfig projectConfig = Tik.getTik().getProjectConfig();
    if (projectConfig == null) {
      return;
    }
    this.jTextField2.setText(projectConfig.getString("", "path.configPath"));
    this.jTextField3.setText(projectConfig.getString("", "path.commonPath"));
  }
  
  private void jbInit()
    throws Exception
  {
    setTitle("Project Properties");
    this.jTree1 = new JTree(getTreeTop());
    this.jTree1.expandRow(0);
    this.jTree1.setSelectionRow(1);
    this.jButton1.addActionListener(new ProjectPropertiesDialog_jButton1_actionAdapter(this));
    
    this.jButton2.addActionListener(new ProjectPropertiesDialog_jButton2_actionAdapter(this));
    
    this.jPanel2.setLayout(this.borderLayout1);
    this.jPanel2.setPreferredSize(new Dimension(150, 2));
    this.jPanel3.setLayout(this.cardLayout1);
    this.jPanel6.setLayout(null);
    this.jLabel1.setText("Paths");
    this.jLabel1.setBounds(new Rectangle(17, 10, 42, 15));
    this.jLabel2.setText("Project Path:");
    this.jLabel2.setBounds(new Rectangle(17, 41, 93, 20));
    this.jTextField1.setText(Tik.PROJECT_FILE_NAME);
    this.jTextField1.setBounds(new Rectangle(115, 41, 281, 20));
    this.jButton3.setBounds(new Rectangle(400, 41, 37, 20));
    this.jButton3.setText("...");
    this.jButton3.addActionListener(new ProjectPropertiesDialog_jButton3_actionAdapter(this));
    
    this.jLabel3.setForeground(Color.red);
    this.jLabel3.setPreferredSize(new Dimension(300, 15));
    this.jLabel4.setText("Config  Path:");
    this.jLabel4.setBounds(new Rectangle(17, 84, 93, 15));
    this.jLabel5.setText("Common  Path:");
    this.jLabel5.setBounds(new Rectangle(17, 127, 93, 15));
    this.jTextField2.setBounds(new Rectangle(115, 86, 281, 20));
    this.jTextField3.setBounds(new Rectangle(115, 127, 281, 20));
    this.jButton4.setBounds(new Rectangle(400, 84, 37, 20));
    this.jButton4.setText("jButton4");
    this.jButton4.addActionListener(new ProjectPropertiesDialog_jButton4_actionAdapter(this));
    
    this.jButton5.setBounds(new Rectangle(400, 126, 37, 20));
    this.jButton5.setText("jButton5");
    this.jButton5.addActionListener(new ProjectPropertiesDialog_jButton5_actionAdapter(this));
    
    getContentPane().add(this.jPanel1, "South");
    this.jButton2.setText("Cancel");
    this.jPanel1.setLayout(this.flowLayout1);
    this.flowLayout1.setAlignment(2);
    this.jPanel1.add(this.jLabel3);
    this.jPanel1.add(this.jButton1);
    this.jPanel1.add(this.jButton2);
    getContentPane().add(this.jPanel2, "West");
    this.jPanel2.add(this.jScrollPane1, "Center");
    getContentPane().add(this.jPanel3, "Center");
    this.jPanel3.add(this.jPanel6, "jPanel6");
    this.jPanel6.add(this.jLabel1);
    this.jPanel6.add(this.jLabel2);
    this.jPanel6.add(this.jTextField1);
    this.jPanel6.add(this.jButton3);
    this.jPanel6.add(this.jLabel4);
    this.jPanel6.add(this.jLabel5);
    this.jPanel6.add(this.jTextField3);
    this.jPanel6.add(this.jTextField2);
    this.jTextField2.setText("");
    this.jTextField3.setText("");
    this.jPanel6.add(this.jButton4);
    this.jPanel6.add(this.jButton5);
    this.jScrollPane1.getViewport().add(this.jTree1);
    this.jPanel2.add(this.jPanel4, "North");
    this.jPanel2.add(this.jPanel5, "West");
    this.jButton1.setText("OK");
    onInitValue();
  }
  
  public void messageBox(String text)
  {
    JOptionPane.showMessageDialog(null, text);
  }
  
  public DefaultMutableTreeNode getTreeTop()
  {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Properties");
    DefaultMutableTreeNode d = new DefaultMutableTreeNode("Paths");
    top.add(d);
    return top;
  }
  
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  BorderLayout borderLayout1 = new BorderLayout();
  JTree jTree1;
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  CardLayout cardLayout1 = new CardLayout();
  JPanel jPanel6 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JButton jButton3 = new JButton();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JTextField jTextField2 = new JTextField();
  JTextField jTextField3 = new JTextField();
  JButton jButton4 = new JButton();
  JButton jButton5 = new JButton();
  
  public void jButton1_actionPerformed(ActionEvent e)
  {
    if (!Tik.getTik().onInitProjectConfig(this.jTextField1.getText()))
    {
      this.jLabel3.setText("Err:Project Path load file err.");
      return;
    }
    dispose();
  }
  
  public void jButton2_actionPerformed(ActionEvent e)
  {
    dispose();
  }
  
  public void jButton4_actionPerformed(ActionEvent e)
  {
    OpenServerFileDialog serverFileDialog1 = new OpenServerFileDialog(this.jTextField2.getText(), ".x");
    serverFileDialog1.open();
    if (serverFileDialog1.getpath().length() == 0) {
      return;
    }
    this.jTextField2.setText(serverFileDialog1.getpath());
  }
  
  public void jButton5_actionPerformed(ActionEvent e)
  {
    OpenServerFileDialog serverFileDialog2 = new OpenServerFileDialog(this.jTextField3.getText(), ".x");
    serverFileDialog2.open();
    if (serverFileDialog2.getpath().length() == 0) {
      return;
    }
    this.jTextField3.setText(serverFileDialog2.getpath());
  }
  
  public void jButton3_actionPerformed(ActionEvent e)
  {
    OpenServerFileDialog serverFileDialog3 = new OpenServerFileDialog(this.jTextField1.getText(), ".tpx");
    serverFileDialog3.open();
    if (serverFileDialog3.getpath().length() == 0) {
      return;
    }
    this.jTextField1.setText(serverFileDialog3.getpath());
  }
}
