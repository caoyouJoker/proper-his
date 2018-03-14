package com.tbuilder.dialog;

//import com.tbuilder.ui.ULine;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OpenServerNewDialog
  extends JDialog
{
  private String projectName;
  private String rename;
  
  public OpenServerNewDialog()
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
  
  public OpenServerNewDialog(String rename)
  {
    setModal(true);
    this.rename = rename;
    this.projectName = rename;
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
    setLocation(400, 200);
    setSize(550, 450);
    setVisible(true);
  }
  
  private void jbInit()
    throws Exception
  {
    getContentPane().setLayout(this.borderLayout1);
    //this.uLine1.setText("uLine1");
    this.jPanel1.setLayout(this.borderLayout2);
    this.jPanel3.setLayout(this.borderLayout3);
    this.borderLayout2.setHgap(5);
    this.borderLayout2.setVgap(5);
    this.borderLayout1.setHgap(5);
    this.borderLayout1.setVgap(5);
    this.jLabel1.setMaximumSize(new Dimension(100, 300));
    this.jLabel1.setMinimumSize(new Dimension(100, 300));
    this.jLabel1.setPreferredSize(new Dimension(155, 300));
    this.jLabel1.setIcon(createImageIcon("p1.JPG"));
    this.jPanel3.setMinimumSize(new Dimension(100, 300));
    this.jPanel3.setPreferredSize(new Dimension(100, 300));
    this.jPanel5.setLayout(null);
    this.jLabel2.setText("Name:");
    this.jLabel2.setBounds(new Rectangle(7, 87, 78, 27));
    setTitle("New Project");
    this.jTextField1.setBounds(new Rectangle(85, 91, 281, 20));
    this.jButton1.addActionListener(new OpenServerNewDialog_jButton1_actionAdapter(this));
    this.jButton2.addActionListener(new OpenServerNewDialog_jButton2_actionAdapter(this));
    getContentPane().add(this.jPanel1, "Center");
    this.jButton1.setText("Canecl");
    this.jPanel2.setLayout(this.flowLayout1);
    getContentPane().add(this.jPanel2, "South");
    this.jButton2.setText("Ok");
    this.flowLayout1.setAlignment(2);
    this.jPanel2.add(this.jButton2);
    this.jPanel2.add(this.jButton1);
    //this.jPanel1.add(this.uLine1, "South");
    this.jPanel1.add(this.jPanel3, "Center");
    this.jPanel1.add(this.jPanel4, "West");
    this.jPanel3.add(this.jLabel1, "West");
    this.jPanel3.add(this.jPanel5, "Center");
    this.jPanel5.add(this.jLabel2);
    this.jPanel5.add(this.jTextField1);
    setjTextField();
  }
  
  public void setjTextField()
  {
    if ((this.rename == null) || (this.rename.length() == 0)) {
      return;
    }
    this.jTextField1.setText(this.rename);
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
  
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  //ULine uLine1 = new ULine();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();
  
  public static void main(String[] args)
  {
    new OpenServerNewDialog().open();
  }
  
  public String getProjectName()
  {
    return this.projectName;
  }
  
  public String getRename()
  {
    return this.rename;
  }
  
  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }
  
  public void setRename(String rename)
  {
    this.rename = rename;
  }
  
  public void jButton1_actionPerformed(ActionEvent actionEvent)
  {
    dispose();
  }
  
  public void jButton2_actionPerformed(ActionEvent actionEvent)
  {
    this.projectName = this.jTextField1.getText();
    dispose();
  }
}
