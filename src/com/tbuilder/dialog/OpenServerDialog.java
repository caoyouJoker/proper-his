package com.tbuilder.dialog;

import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
//import com.tbuilder.ui.ULine;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OpenServerDialog
  extends JDialog
{
  public OpenServerDialog()
  {
    setModal(true);
    try
    {
      jbInit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void open()
  {
    setLocation(300, 150);
    setSize(500, 420);
    this.jLabel6.setVisible(false);
    setVisible(true);
  }
  
  private void jbInit()
    throws Exception
  {
    setTitle("Login AppServer Dialog");
    this.jPanel2.setLayout(this.flowLayout2);
    this.flowLayout2.setAlignment(2);
    this.jLabel1.setIcon(createImageIcon("p1.JPG"));
    //this.uLine1.setText("uLine1");
    this.jPanel3.setLayout(this.flowLayout1);
    this.flowLayout1.setHgap(10);
    this.flowLayout1.setVgap(10);
    this.jPanel4.setLayout(null);
    this.jLabel2.setHorizontalAlignment(2);
    this.jLabel2.setText("AppServer IP:");
    this.jLabel2.setBounds(new Rectangle(7, 67, 100, 20));
    this.jTextField1.setText("localhost");
    this.jTextField1.setBounds(new Rectangle(128, 67, 155, 20));
    this.jLabel3.setText("Login Appserver info");
    this.jLabel3.setBounds(new Rectangle(30, 32, 184, 15));
    this.jLabel4.setHorizontalAlignment(2);
    this.jLabel4.setText("AppServer Port:");
    this.jLabel4.setBounds(new Rectangle(7, 96, 100, 20));
    this.jTextField2.setText("8090");
    this.jTextField2.setBounds(new Rectangle(128, 96, 155, 20));
    this.jLabel5.setHorizontalAlignment(2);
    this.jLabel5.setText("AppServer Web:");
    this.jLabel5.setBounds(new Rectangle(7, 125, 100, 20));
    this.jTextField3.setText("webserver");
    this.jTextField3.setBounds(new Rectangle(128, 125, 155, 20));
    this.jButton2.addActionListener(new OpenServerDialog_jButton2_actionAdapter(this));
    this.jButton1.addActionListener(new OpenServerDialog_jButton1_actionAdapter(this));
    this.jLabel6.setForeground(Color.red);
    this.jLabel6.setText("Err:AppServer no start");
    this.jLabel6.setBounds(new Rectangle(84, 174, 155, 15));
    getContentPane().add(this.jPanel2, "South");
    this.jButton2.setText("Cancel");
    this.jPanel1.setLayout(this.borderLayout1);
    this.jPanel2.add(this.jButton1);
    this.jPanel2.add(this.jButton2);
    this.jButton1.setText("OK");
    getContentPane().add(this.jPanel1, "Center");
    this.jPanel3.add(this.jLabel1);
    this.jPanel1.add(this.jPanel4, "Center");
    this.jPanel4.add(this.jTextField1);
    this.jPanel4.add(this.jLabel2);
    this.jPanel4.add(this.jLabel3);
    this.jPanel4.add(this.jTextField2);
    this.jPanel4.add(this.jLabel4);
    this.jPanel4.add(this.jTextField3);
    this.jPanel4.add(this.jLabel5);
    this.jPanel4.add(this.jLabel6);
    this.jPanel1.add(this.jPanel3, "West");
    //this.jPanel1.add(this.uLine1, "South");
  }
  
  private ImageIcon createImageIcon(String filename)
  {
    String path = "/images/" + filename;
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(getClass().getResource(path));
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }
    return icon;
  }
  
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  FlowLayout flowLayout2 = new FlowLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  //ULine uLine1 = new ULine();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField jTextField2 = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField jTextField3 = new JTextField();
  JLabel jLabel6 = new JLabel();
  
  public void jButton2_actionPerformed(ActionEvent e)
  {
    dispose();
  }
  
  public void jButton1_actionPerformed(ActionEvent e)
  {
    TSocket socket = new TSocket(this.jTextField1.getText(), StringTool.getInt(this.jTextField2.getText()), this.jTextField3.getText());
    if (!TIOM_AppServer.appIsRun(socket))
    {
      this.jLabel6.setVisible(true);
      return;
    }
    this.jLabel6.setVisible(false);
    TIOM_AppServer.SOCKET = socket;
    dispose();
  }
}
