package com.javahis.ui.sys;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.JWindow;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>跑马灯</p>
 * @author WangQing 20170313
 *
 */
public class HorseRaceLamp extends JWindow {

	/**
	 * 拖动至位置x
	 */
	private int otherX;
	/**
	 * 拖动至位置y
	 */
	private int otherY;

	/**
	 * 控件所属的控制器
	 */
	public TControl control;

	/**
	 * 有参构造器
	 */
	public HorseRaceLamp(Frame f){
		super(f);
		this.initParagramers();
		this.addMouseListener(new MouseAdapter() {
			//按下
			public void mousePressed(MouseEvent e) {
				otherX = e.getX();
				otherY = e.getY();
			}
			public void mouseClicked(MouseEvent e) {
				//双击
				if (e.getClickCount() == 2) {
					doubleClickMsg(e);
				}
			}
		});
		//鼠标事件
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//鼠标拖动
			public void mouseDragged(MouseEvent e) {
				mouseDraggedEvent(e);
			}
		});
	}

	/**
	 * 无参构造器
	 */
	public HorseRaceLamp() {
		super();
		this.initParagramers();
		this.addMouseListener(new MouseAdapter() {
			//按下
			public void mousePressed(MouseEvent e) {
				otherX = e.getX();
				otherY = e.getY();
			}
			public void mouseClicked(MouseEvent e) {
				//双击
				if (e.getClickCount() == 2) {
					doubleClickMsg(e);
				}
			}
		});
		//鼠标事件
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//鼠标拖动
			public void mouseDragged(MouseEvent e) {
				mouseDraggedEvent(e);
			}
		});
	}

	/**
	 * 初始化参数
	 */
	public void initParagramers(){
		this.setSize(400, 40);
		this.setLocation(20, 30);
		this.setBackground(new Color(0,51,102));
		this.setFont(new Font("楷体_GB2312", Font.BOLD, 10));
	}

	/**
	 * 双击消息面板
	 * @param e MouseEvent
	 */
	private void doubleClickMsg(MouseEvent e) {
		//左键双击
		if(e.getButton()==1){
			//	          System.out.println("双击");
			//双击事件
			doubleClick();
		}
	}

	/**
	 * 双击事件
	 */
	public void doubleClick(){
		System.out.println("双击事件!!!");
		if(control != null){
			TParm p = new TParm();
			this.control.callFunction("doubleClickHRL", new TParm[]{p});
		}
	}

	/**
	 * 鼠标拖动事件
	 * @param e MouseEvent
	 */
	private void mouseDraggedEvent(MouseEvent e) {
		int x = e.getX()-otherX+this.getX();
		int y = e.getY()-otherY+this.getY();
		this.setLocation(x,y);
	}

	/**
	 * 设置跑马灯显示数据
	 * @param mes
	 */
	public void addMessage(String mes){
		this.getContentPane().removeAll();
		JLabel view = new JLabel(mes);
		view.setFont(Font.decode("Dialog-BOLD-36"));
		view.setForeground(Color.BLUE);
		JViewport window = new JViewport();
		window.setView(view);
		this.getContentPane().add(window);	
	}

	public static void main(String[] args){
		HorseRaceLamp hrl = new HorseRaceLamp();
		hrl.addMessage("胸痛中心病患通知！！！");
		hrl.setVisible(true);
	}
}
