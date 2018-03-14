package com.javahis.ui.sys;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WebBrowser extends JFrame{
    JPanel contentPane;
 BorderLayout borderLayoutAll=new BorderLayout();
 JLabel jLabelPrompt=new JLabel(); //状态提示框
 BorderLayout borderLayoutMain=new BorderLayout();
 JPanel jPanelMain=new JPanel();
 JTextField textFieldURL=new JTextField(); //URL输入框
 JEditorPane jEditorPane=new JEditorPane(); //显示网页容器

 /**
  * 构造函数
  * @param args
  */
 public WebBrowser(){

   try {
    Init();
   } catch (Exception e) {
    // TODO 自动生成 catch 块
    e.printStackTrace();
   }

 }
 private void Init() throws Exception{
  contentPane=(JPanel) getContentPane();
  contentPane.setLayout(borderLayoutAll);
  jPanelMain.setLayout(borderLayoutMain);
  jLabelPrompt.setText("请输入URL");
  textFieldURL.setText("");
  textFieldURL.addActionListener(new java.awt.event.ActionListener(){
   public void actionPerformed(ActionEvent e){
    textFieldURL_actionPerformed(e);
   }
  });
  jEditorPane.setEditable(false);
  jEditorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener(){
   public void hyperlinkUpdate(HyperlinkEvent e){
    jEditorPane_hyperlinkUpdate(e);
   }
  });
  JScrollPane scrollPane=new JScrollPane();
  scrollPane.getViewport().add(jEditorPane);
  jPanelMain.add(textFieldURL,"North");
  jPanelMain.add(scrollPane,"Center");
  contentPane.add(jLabelPrompt,"North");
  contentPane.add(jPanelMain,"Center");
  enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  this.setSize(new Dimension(600,500));
  this.setTitle("迷你IE");
  this.setVisible(true);
 }
 void textFieldURL_actionPerformed(ActionEvent e){
  try {
   jEditorPane.setPage(textFieldURL.getText());//显示URL
  } catch (IOException e1) {
   JOptionPane msg=new JOptionPane();
   JOptionPane.showMessageDialog(this, "URL地址不正确："+textFieldURL.getText(),"输入不正确",0);
  }

 }
 void jEditorPane_hyperlinkUpdate(HyperlinkEvent e){
  if(e.getEventType()==javax.swing.event.HyperlinkEvent.EventType.ACTIVATED){
   try {
    URL url=e.getURL();
    jEditorPane.setPage(url);
    textFieldURL.setText(url.toString());
   } catch (Exception io) {
    // TODO: handle exception
    JOptionPane msg=new JOptionPane();
    JOptionPane.showMessageDialog(this,"打开该链接失败！","输入不正确",0);
   }

  }
 }
 protected void processWindowEvent (WindowEvent e){
  super.processWindowEvent(e);
  if(e.getID()==WindowEvent.WINDOW_CLOSING){
   System.exit(0);
  }
 }
 public static void main(String[] args) {

  new WebBrowser();
 }

}
