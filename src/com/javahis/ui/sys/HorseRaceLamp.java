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
 * <p>�����</p>
 * @author WangQing 20170313
 *
 */
public class HorseRaceLamp extends JWindow {

	/**
	 * �϶���λ��x
	 */
	private int otherX;
	/**
	 * �϶���λ��y
	 */
	private int otherY;

	/**
	 * �ؼ������Ŀ�����
	 */
	public TControl control;

	/**
	 * �вι�����
	 */
	public HorseRaceLamp(Frame f){
		super(f);
		this.initParagramers();
		this.addMouseListener(new MouseAdapter() {
			//����
			public void mousePressed(MouseEvent e) {
				otherX = e.getX();
				otherY = e.getY();
			}
			public void mouseClicked(MouseEvent e) {
				//˫��
				if (e.getClickCount() == 2) {
					doubleClickMsg(e);
				}
			}
		});
		//����¼�
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//����϶�
			public void mouseDragged(MouseEvent e) {
				mouseDraggedEvent(e);
			}
		});
	}

	/**
	 * �޲ι�����
	 */
	public HorseRaceLamp() {
		super();
		this.initParagramers();
		this.addMouseListener(new MouseAdapter() {
			//����
			public void mousePressed(MouseEvent e) {
				otherX = e.getX();
				otherY = e.getY();
			}
			public void mouseClicked(MouseEvent e) {
				//˫��
				if (e.getClickCount() == 2) {
					doubleClickMsg(e);
				}
			}
		});
		//����¼�
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//����϶�
			public void mouseDragged(MouseEvent e) {
				mouseDraggedEvent(e);
			}
		});
	}

	/**
	 * ��ʼ������
	 */
	public void initParagramers(){
		this.setSize(400, 40);
		this.setLocation(20, 30);
		this.setBackground(new Color(0,51,102));
		this.setFont(new Font("����_GB2312", Font.BOLD, 10));
	}

	/**
	 * ˫����Ϣ���
	 * @param e MouseEvent
	 */
	private void doubleClickMsg(MouseEvent e) {
		//���˫��
		if(e.getButton()==1){
			//	          System.out.println("˫��");
			//˫���¼�
			doubleClick();
		}
	}

	/**
	 * ˫���¼�
	 */
	public void doubleClick(){
		System.out.println("˫���¼�!!!");
		if(control != null){
			TParm p = new TParm();
			this.control.callFunction("doubleClickHRL", new TParm[]{p});
		}
	}

	/**
	 * ����϶��¼�
	 * @param e MouseEvent
	 */
	private void mouseDraggedEvent(MouseEvent e) {
		int x = e.getX()-otherX+this.getX();
		int y = e.getY()-otherY+this.getY();
		this.setLocation(x,y);
	}

	/**
	 * �����������ʾ����
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
		hrl.addMessage("��ʹ���Ĳ���֪ͨ������");
		hrl.setVisible(true);
	}
}
