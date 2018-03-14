package com.javahis.ui.inv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
 
import javax.swing.JFrame;  
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JViewport; 
import javax.swing.Timer;
 
public class INV_LED extends JFrame {
  private Timer timer;
  private JLabel view;
  private JViewport window;
  
  public static void main(String[] args)
  {
    JFrame frm = new INV_LED("跑马灯");
    frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frm.pack();
    frm.setVisible(true);
  }
 
  public INV_LED(String title) throws HeadlessException
  {
    super(title);
    
    initComponents();
    
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e)
      {
        anchor = new Point();
        anchor.x = -window.getExtentSize().width;
        timer.start();
      }
    });
    
    timer = new Timer(100, new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        animate();
      }
    });
    timer.setInitialDelay(0);
  }
 
  private void initComponents()
  {
   String s = JOptionPane.showInputDialog(null, "请输入要实现效果的文字:");
    view = new JLabel(s);
    view.setFont(Font.decode("Dialog-BOLD-36"));
    view.setForeground(Color.BLUE);
    
    window = new JViewport();
    window.setView(view);
    getContentPane().add(window);
  }
  
  Point anchor;
  private void animate()
  {
    Dimension extSize = window.getExtentSize();
    Dimension viewSize = view.getPreferredSize();
    anchor.x += 5;//设置移动的速度
    window.setViewPosition(anchor);
    if (anchor.x > viewSize.width)
      anchor.x = -extSize.width;
  }
}