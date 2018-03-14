package com.javahis.device;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.dongyang.ui.TPanel;

public class JMCamera extends JFrame {
  String filename;
  JMStudio studio;
  Image image;
  BufferedImage image1;
  BorderLayout borderLayout1 = new BorderLayout();
  P jPanel1 = new P();
  JPanel dyPanel1 = new JPanel();
  JButton dyButton1 = new JButton();
  JButton dyButton2 = new JButton();
  JLabel jLabel1 = new JLabel();
  Border border1;
  JTextField jTextField1 = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField2 = new JTextField();
  JLabel jLabel3 = new JLabel();
  boolean move;
  int x0;
  int y0;
  W jLabel4 = new W();
  Border border2;
  public JMCamera(Image image,JMStudio studio,String filename) {
    this.image = image;
//    System.out.println("image"+(image==null));
    this.studio = studio;
//    System.out.println("image"+(studio==null));
    this.filename = filename;
//    System.out.println("image"+(filename==null));
    jbInit();
  }

  public JMCamera() {
      jbInit();
  }

  private void jbInit(){
    border1 = BorderFactory.createLineBorder(Color.blue,1);
    border2 = BorderFactory.createLineBorder(Color.blue,1);
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
    dyPanel1.setPreferredSize(new Dimension(100, 1));
//    dyPanel1.setText("dyPanel1");
//    dyPanel1.setLayout(null);
    dyButton1.setBounds(new Rectangle(19, 5, 70, 30));
    dyButton1.setText("确定");
    dyButton1.addActionListener(new JMCamera_dyButton1_actionAdapter(this));
    jPanel1.setLayout(null);
    dyButton2.setBounds(new Rectangle(19, 47, 70, 30));
    dyButton2.setText("取消");
    dyButton2.addActionListener(new JMCamera_dyButton2_actionAdapter(this));
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 20));
    jLabel1.setForeground(Color.white);
    jLabel1.setBorder(border1);
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("+");
    jLabel1.setBounds(new Rectangle(19, 47, 300, 400));
    jLabel1.addMouseMotionListener(new JMCamera_jLabel1_mouseMotionAdapter(this));
    jLabel1.addMouseListener(new JMCamera_jLabel1_mouseAdapter(this));
    jTextField1.setFont(new java.awt.Font("Dialog", 0, 12));
    jTextField1.setText("300");
    jTextField1.setBounds(new Rectangle(20, 106, 62, 21));
    jTextField1.addActionListener(new JMCamera_jTextField1_actionAdapter(this));
    jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel2.setText("图片宽度");
    jLabel2.setBounds(new Rectangle(19, 88, 70, 16));
    jTextField2.setText("400");
    jTextField2.setBounds(new Rectangle(20, 151, 62, 21));
    jTextField2.addActionListener(new JMCamera_jTextField2_actionAdapter(this));
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel3.setText("图片高度");
    jLabel3.setBounds(new Rectangle(20, 134, 61, 16));
    jLabel4.setBorder(border2);
    jLabel4.setBounds(new Rectangle(22, 187, 60, 80));
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(dyPanel1, BorderLayout.EAST);
    dyPanel1.add(dyButton1, null);
    dyPanel1.add(dyButton2, null);
    dyPanel1.add(jTextField1, null);
    dyPanel1.add(jLabel2, null);
    dyPanel1.add(jTextField2, null);
    dyPanel1.add(jLabel3, null);
    dyPanel1.add(jLabel4, null);
    jPanel1.add(jLabel1);
    this.pack();
  }
  public void cutPic()
  {
    image1 = new BufferedImage(jLabel1.getWidth(),
                                          jLabel1.getHeight(),
                                          BufferedImage.TYPE_INT_RGB);
    Graphics g1 = image1.getGraphics();
    g1.drawImage(image, jLabel1.getX() * -1, jLabel1.getY() * -1, null);
    g1.dispose();
    jLabel4.repaint();
  }
  public Image getImage(int w,int h,BufferedImage src)
  {
    Image image = null;
    if(src == null)
      return null;
    try {
      int width = src.getWidth(); // 得到源图宽
      int height = src.getHeight(); // 得到源图长
      int wp = w;
      int hp = (int) ( (double) height * (double)w/(double)width);
      if(hp > h)
      {
        hp = h;
        wp = (int) ( (double) width * (double)h/(double)height);
      }
      image = src.getScaledInstance(wp,hp,Image.SCALE_DEFAULT);
    }catch(Exception e)
    {
    }
    return image;
  }
  class P extends JPanel {

    public void paint(Graphics g) {
      g.drawImage(image, 0, 0, this);
      jLabel1.paint(g.create(jLabel1.getX(),jLabel1.getY(),jLabel1.getWidth(),jLabel1.getHeight()));
      cutPic();
    }

    public void update(Graphics g) {
      paint(g);
    }
  }
  class W extends JLabel{
    public void paint(Graphics g) {
      if(image1 != null)
        g.drawImage(image1, 0, 0, this.getWidth(),this.getHeight(),this);
    }
    public void update(Graphics g) {
      paint(g);
    }
  }
  void jTextField1_actionPerformed(ActionEvent e) {
    jLabel1.setSize(Integer.parseInt(jTextField1.getText()),Integer.parseInt(jTextField2.getText()));
    jLabel4.setSize(Integer.parseInt(jTextField1.getText())/5,Integer.parseInt(jTextField2.getText())/5);
    jPanel1.repaint();
  }

  void jTextField2_actionPerformed(ActionEvent e) {
    jLabel1.setSize(Integer.parseInt(jTextField1.getText()),Integer.parseInt(jTextField2.getText()));
    jLabel4.setSize(Integer.parseInt(jTextField1.getText())/5,Integer.parseInt(jTextField2.getText())/5);
    jPanel1.repaint();
  }

  void jLabel1_mousePressed(MouseEvent e) {
    move = true;
    x0 = e.getPoint().x;
    y0 = e.getPoint().y;
  }
  void jLabel1_mouseReleased(MouseEvent e) {
    move = false;
  }

  void jLabel1_mouseDragged(MouseEvent e) {
    if(!move)
      return;
    jLabel1.setLocation(e.getPoint().x - x0 + jLabel1.getX(),e.getPoint().y - y0 + jLabel1.getY());
    jPanel1.repaint();
    cutPic();
  }

  void dyButton2_actionPerformed(ActionEvent e) {
    dispose();
  }

  void dyButton1_actionPerformed(ActionEvent e) {
    try {
//        System.out.println("filename="
//                +filename);
      ImageIO.write(image1, "JPEG", new File(filename)); // 输出到文件流
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    dispose();
    if(studio != null)
      studio.camera(image1);
  }
}

class JMCamera_jTextField1_actionAdapter implements java.awt.event.ActionListener {
  JMCamera adaptee;

  JMCamera_jTextField1_actionAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jTextField1_actionPerformed(e);
  }
}

class JMCamera_jTextField2_actionAdapter implements java.awt.event.ActionListener {
  JMCamera adaptee;

  JMCamera_jTextField2_actionAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jTextField2_actionPerformed(e);
  }
}

class JMCamera_jLabel1_mouseAdapter extends java.awt.event.MouseAdapter {
  JMCamera adaptee;

  JMCamera_jLabel1_mouseAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.jLabel1_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.jLabel1_mouseReleased(e);
  }
}

class JMCamera_jLabel1_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
  JMCamera adaptee;

  JMCamera_jLabel1_mouseMotionAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseDragged(MouseEvent e) {
    adaptee.jLabel1_mouseDragged(e);
  }
}

class JMCamera_dyButton2_actionAdapter implements java.awt.event.ActionListener {
  JMCamera adaptee;

  JMCamera_dyButton2_actionAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dyButton2_actionPerformed(e);
  }
}

class JMCamera_dyButton1_actionAdapter implements java.awt.event.ActionListener {
  JMCamera adaptee;

  JMCamera_dyButton1_actionAdapter(JMCamera adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dyButton1_actionPerformed(e);
  }
}
