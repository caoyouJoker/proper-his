package com.tbuilder.dialog;

import com.dongyang.manager.TIOM_AppServer;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class OpenServerFileDialog
  extends JDialog
{
  private String systemPath = "";
  private String path = "";
  private String fileFilter;
  
  public OpenServerFileDialog()
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
  
  public OpenServerFileDialog(String systemPath)
  {
    this.systemPath = systemPath;
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
  
  public OpenServerFileDialog(String systemPath, String fileFilter)
  {
    this.systemPath = systemPath;
    this.fileFilter = fileFilter;
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
  
  public String getpath()
  {
    return this.path;
  }
  
  public void setPath(String path)
  {
    this.path = path;
  }
  
  public String getFileFilter()
  {
    return this.fileFilter;
  }
  
  public void setFileFilter(String fileFilter)
  {
    this.fileFilter = fileFilter;
  }
  
  public void loadDir(String path, DefaultMutableTreeNode root)
  {
    String[] fileList = TIOM_AppServer.listDir(path);
    if (fileList == null) {
      return;
    }
    for (int i = 0; i < fileList.length; i++)
    {
      DefaultMutableTreeNode roots = new DefaultMutableTreeNode(fileList[i]);
      root.add(roots);
      loadDir(path + "\\" + fileList[i], roots);
    }
  }
  
  public void open()
  {
    setLocation(400, 200);
    setSize(500, 450);
    setVisible(true);
  }
  
  private void jbInit()
    throws Exception
  {
    this.jTextField1.setPreferredSize(new Dimension(410, 20));
    this.jTextField1.setText("%ROOT%");
    this.flowLayout1.setAlignment(0);
    this.jPanel7.setLayout(this.borderLayout2);
    this.jPanel6.setLayout(this.borderLayout3);
    this.jPanel4.setLayout(null);
    this.jPanel4.setBorder(BorderFactory.createEtchedBorder());
    this.jPanel4.setDebugGraphicsOptions(0);
    this.jPanel4.setMinimumSize(new Dimension(5, 5));
    this.jPanel4.setPreferredSize(new Dimension(100, 80));
    this.jPanel3.setLayout(this.flowLayout2);
    this.jButton3.setText("Canecl");
    this.jButton3.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        OpenServerFileDialog.this.jButton3_actionPerformed(e);
      }
    });
    this.jButton4.setText("OK");
    this.jButton4.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        OpenServerFileDialog.this.jButton4_actionPerformed(e);
      }
    });
    this.flowLayout2.setAlignment(2);
    this.borderLayout1.setHgap(1);
    this.borderLayout1.setVgap(1);
    this.jTree1.setPreferredSize(new Dimension(140, 64));
    this.jToggleButton1.setBorder(BorderFactory.createRaisedBevelBorder());
    this.jToggleButton1.setText("Home");
    this.jToggleButton1.setBounds(new Rectangle(4, 6, 92, 50));
    this.jToggleButton1.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        OpenServerFileDialog.this.jToggleButton1_actionPerformed(e);
      }
    });
    this.jToggleButton2.setBorder(BorderFactory.createRaisedBevelBorder());
    this.jToggleButton2.setText("Project");
    this.jToggleButton2.setBounds(new Rectangle(4, 60, 92, 51));
    this.jToggleButton2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        OpenServerFileDialog.this.jToggleButton2_actionPerformed(e);
      }
    });
    this.jToggleButton2.setSelected(true);
    getContentPane().add(this.jPanel2, "Center");
    this.jPanel2.setLayout(this.borderLayout1);
    this.jPanel1.setLayout(this.flowLayout1);
    this.jLabel1.setText("  Diractory:");
    this.jPanel2.add(this.jPanel4, "West");
    this.jPanel4.add(this.jToggleButton1);
    this.jPanel4.add(this.jToggleButton2);
    this.jPanel2.add(this.jScrollPane1, "Center");
    this.jScrollPane1.getViewport().add(this.jSplitPane1);
    this.jSplitPane1.add(this.jTree1, "left");
    this.jSplitPane1.add(this.jList1, "right");
    getContentPane().add(this.jPanel1, "North");
    this.jPanel1.add(this.jPanel6);
    this.jPanel1.add(this.jPanel7);
    this.jPanel7.add(this.jTextField1, "Center");
    getContentPane().add(this.jPanel3, "South");
    this.jPanel3.add(this.jButton4);
    this.jPanel3.add(this.jButton3);
    this.jPanel6.add(this.jLabel1, "Center");
    loadDir(this.jTextField1.getText(), this.projectTop);
    
    this.jTree1.getSelectionModel().setSelectionMode(1);
    this.jTree1.expandRow(0);
    this.jList1.setListData(TIOM_AppServer.listFile(this.jTextField1.getText(), this.fileFilter));
    
    this.jTree1.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent e)
      {
        TreePath[] treePath = OpenServerFileDialog.this.jTree1.getSelectionPaths();
        if (treePath == null) {
          return;
        }
        String str = treePath[0].toString();
        OpenServerFileDialog.this.jTextField1.setText(OpenServerFileDialog.this.creatStr(OpenServerFileDialog.this.jTree1.getSelectionPaths()[0].toString()));
        OpenServerFileDialog.this.jList1.setListData(TIOM_AppServer.listFile(OpenServerFileDialog.this.creatStr(str), OpenServerFileDialog.this.fileFilter));
      }
    });
    this.jList1.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting())
        {
          if (OpenServerFileDialog.this.jTree1.getSelectionPaths() == null)
          {
            OpenServerFileDialog.this.jTextField1.setText("%ROOT%");
            OpenServerFileDialog.this.jTextField1.setText(OpenServerFileDialog.this.jTextField1.getText() + "\\" + OpenServerFileDialog.this.jList1.getSelectedValue().toString());
            return;
          }
          OpenServerFileDialog.this.jTextField1.setText(OpenServerFileDialog.this.creatStr(OpenServerFileDialog.this.jTree1.getSelectionPaths()[0].toString()) + "\\" + OpenServerFileDialog.this.jList1.getSelectedValue().toString());
        }
      }
    });
  }
  
  public String creatStr(String path)
  {
    String[] strs = path.substring(1, path.length() - 1).split(",");
    String strPath = "";
    for (int j = 0; j < strs.length; j++) {
      strPath = strPath + strs[j].trim() + "\\";
    }
    return strPath.substring(0, strPath.length() - 1);
  }
  
  public static void main(String[] args)
  {
    new OpenServerFileDialog().open();
  }
  
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  JTextField jTextField1 = new JTextField();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JSplitPane jSplitPane1 = new JSplitPane();
  DefaultMutableTreeNode projectTop = new DefaultMutableTreeNode("%ROOT%");
  JTree jTree1 = new JTree(this.projectTop);
  JList jList1 = new JList();
  JToggleButton jToggleButton1 = new JToggleButton();
  JToggleButton jToggleButton2 = new JToggleButton();
  
  public void jButton3_actionPerformed(ActionEvent e)
  {
    dispose();
  }
  
  public void jToggleButton1_actionPerformed(ActionEvent e)
  {
    if (this.jToggleButton2.isSelected()) {
      this.jToggleButton2.setSelected(false);
    }
    this.jToggleButton1.setSelected(true);
  }
  
  public void jToggleButton2_actionPerformed(ActionEvent e)
  {
    if (this.jToggleButton1.isSelected()) {
      this.jToggleButton1.setSelected(false);
    }
    this.jToggleButton2.setSelected(true);
  }
  
  public void jButton4_actionPerformed(ActionEvent e)
  {
    setPath(this.jTextField1.getText());
    System.out.println("1" + this.jTextField1.getText());
    dispose();
  }
}
