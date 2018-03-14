package com.javahis.ui.sys;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.dongyang.data.TParm;
import com.dongyang.root.client.SocketLink;
import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
import com.dongyang.manager.TIOM_AppServer;
import java.sql.Timestamp;
import com.javahis.ui.odi.ODIMainControl;
import com.javahis.ui.reg.REGCTTriageControl;

/**
 * 监听SOKET窗口
 * <p>Title: 监听SOKET窗口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author not attributable
 * @version add by Ningjf 20080117
 */
public class LEDUI_NEW extends JWindow{
	private int otherX;
	private int otherY;
	private int textX;
	/**
	 * 消息列表
	 */
	TParm action = new TParm();
	/**
	 * 消息类表字符串
	 */
	StringBuffer magess = new StringBuffer();
	/**
	 * Socket传送护士站工具
	 */
	private SocketLink client;
	/**
	 * URL
	 */
	private URL url ;
	/**
	 * 调用声音对象
	 */
	private AudioClip audio ;
	/**
	 * 颜色闪动
	 */
	Thread colorThread;
	/**
	 * 闪动状态
	 */
	boolean colorRowState;
	/**
	 * 文字滚动
	 */
	Thread textThread;
	/**
	 * 控制类
	 */
	//   ODIMainControl control;
	REGCTTriageControl control;
	/**
	 * 主窗体宽
	 */
	private int mainWidth;
	/**
	 * 病区
	 */
	private String stationCode="";
	/**
	 * 构造器
	 * @param owner Frame
	 */
	public LEDUI_NEW(Frame owner,Object control,Object parm) {
		super(owner);
		initTemp(owner,control,parm);
		jbInit();
	}
	
	/**
	 * 加载窗体监听
	 * @param owner Frame
	 * @param parm Object
	 */
	private void initTemp(Frame owner,Object control, Object parm) {
		this.mainWidth = owner.getWidth();
		//        System.out.println("主窗体宽度:" + this.mainWidth);
		if (control instanceof REGCTTriageControl) {
			this.control = (REGCTTriageControl) control;
		}
		if (parm != null) {
	
		}

	}
	
	/**
	 * 构造器
	 * 护士站执行操作
	 * @param owner Frame
	 * ========pangben 2011-11-10
	 */
	public LEDUI_NEW(Frame owner,Object control,Object parm,boolean flg) {
		super(owner);
		initTemp(owner,control,parm);
		jbInitOne();
	}
	
	/**
	 * 护士站执行操作
	 */
	private void jbInitOne(){
		//启动监听
		initNbwServerOne();
		mouseListenter();
	}
	/**
	 * 鼠标操作
	 */
	private void mouseListenter(){
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
		this.setLocation(this.mainWidth-500,30);
		//      this.setLocation(880,20);
		this.setBackground(new Color(51,51,153));
		//             this.setBackground(new Color(255, 255, 255));
		this.setFont(new Font("楷体_GB2312",Font.BOLD,14));
		this.setSize(400, 40);
		//             System.out.println("7");

	}
	/**
	 * LED监听方法
	 * @param stationCode String
	 */
	public void onListenerLed(String stationCode){
		this.stationCode = stationCode;
		//        System.out.println("病区:"+this.stationCode);
	}
	public LEDUI_NEW() {
		jbInit();
	}
	
	private void jbInit() {
		//启动监听
		initNbwServer();
		mouseListenter();
	}
	public static void main(String[] args) {
		com.javahis.util.JavaHisDebug.initClient();
		LEDUI ui = new LEDUI();
		ui.openWindow();

	}
	public void paint(Graphics g) {
		g.setColor(this.getBackground());
		g.fill3DRect(0,0,400,40,true);
		g.setColor(new Color(255,153,204));
		g.drawString(magess.toString(),textX,26);
	}
	/**
	 * 双击消息面板
	 * @param e MouseEvent
	 */
	private void doubleClickMsg(MouseEvent e) {
		//左键双击
		if(e.getButton()==1){
			//          System.out.println("双击");
			//双击事件
			doubleClick();
		}
	}
	/**
	 * 双击事件
	 */
	public void doubleClick(){
		//停止闪动颜色
		stopColorThread();
		//停止滚动消息
		stopMoveThread();
		//停止声音
		stopSound();
		this.control.test(action);
	}
	/**
	 * 关闭
	 */
	public void close(){
		this.stopColorThread();
		this.stopMoveThread();
		this.stopSound();
		this.closeSocket();
		this.setVisible(false);
	}
	/**
	 * 关闭Socket
	 */
	public void closeSocket(){
		if(!client.isClose())
			client.close();
	}
	/**
	 * 启动声音
	 */
	private void startSound(){
		//调用音乐
		try {
			url = new URL(TIOM_AppServer.SOCKET.getServletPath("wav/ring.wav"));
			audio = Applet.newAudioClip(url);
			//放音乐(循环)
			audio.loop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 停止音乐
	 */
	private void stopSound() {
		if(audio!=null){
			audio.stop();
		}
	}
	/**
	 * 打开窗口
	 */
	public void openWindow(){
		this.setVisible(true);
	}
	private void initNbwServerOne(){
		//$$ ============  Modified by lx 医生按部门,护士按病区收发消息2012/02/27 START==================$$//	
		//      System.out.println("1");
		//client = SocketLink.running("", "INWSTATION", "inw");
		client = SocketLink.running("", "CT", "CT");
		
		//$$ ============  Modified by lx 医生按部门,护士按病区收发消息2012/02/27 END==================$$//	
		//      System.out.println("2");
		if (client.isClose()) {
			return;
		}
		//      System.out.println("4");
		client.addEventListener("Message", this, "onMessageOne");
		//      System.out.println("5");
		client.listener();
		//      System.out.println("6");

	}
	/**
	 * 初始监听服务服务器
	 */
	private void initNbwServer() {
		//$$ ============  Modified by lx 医生按部门,护士按病区收发消息2012/02/27 START==================$$//	
		//      System.out.println("1");
		//client = SocketLink.running("", "INWSTATION", "inw");
		client = SocketLink.running("", "CT", "CT");

		//$$ ============  Modified by lx 医生按部门,护士按病区收发消息2012/02/27 END==================$$//	
		//      System.out.println("2");
		if (client.isClose()) {
			//          System.out.println("3");
			return;
		}
		//      System.out.println("4");
		client.addEventListener("Message", this, "onMessage");
		//      System.out.println("5");
		client.listener();
		//      System.out.println("6");
	}
	public void onMessageOne(Timestamp time,String id,String message){
		action=new TParm() ;
		//      System.out.println(time + " " + id + " " + message);
		String messages[] = message.split("\\|");
		//      aa:
		for (String temp : messages) {
			String mes[] = temp.split(":");
			//          if("CASE_NO".equals(mes[0])){
			//              int rowCount = action.getCount("CASE_NO");
			//              for(int i=0;i<rowCount;i++){
			//
			//                  if(action.getValue("CASE_NO",i).equals(mes[1]))
			//                      break aa;
			//              }
			//          }
			//
			//          if("STATION_CODE".equals(mes[0])){
			//              if (!this.stationCode.equals(mes[1]))
			//                  return;
			//          }
			//System.out.println("mes[1]:"+mes[1]);
			if(mes.length==1)
				action.addData(mes[0], "");
			else
				action.addData(mes[0], mes[1]);
		}
		//      System.out.println("action"+action);
		action.setData("FLG","Y");//护士站执行管控
		magess = new StringBuffer();
		int rowAction = action.getCount("CASE_NO");
		magess.append("患者姓名:");
		for(int i=0;i<rowAction;i++){
			magess.append(action.getValue("PAT_NAME",i)+" ");
		}
		magess.append(" 存在未执行的医嘱       ");
		//      System.out.println("消息:"+magess.toString());
		//启动颜色线程
		changeTableRowColor();
		//启用滚动文字
		startMoveThread();
		//启动音乐
		startSound();

	}
	/**
	 * 监听消息
	 * @param action String
	 */
	public void onMessage(Timestamp time,String id,String message){
		System.out.println("#######################"+time + " " + id + " " + message);
		String messages[] = message.split("\\|");
		aa:
			for (String temp : messages) {
				String mes[] = temp.split(":");
				if("CASE_NO".equals(mes[0])){
					int rowCount = action.getCount("CASE_NO");
					for(int i=0;i<rowCount;i++){
						if(action.getValue("CASE_NO",i).equals(mes[1]))
							break aa;
					}
				}
				action.addData(mes[0], mes[1]);
			}
		//      System.out.println("action"+action);
		magess = new StringBuffer();
		int rowAction = action.getCount("CASE_NO");
		for(int i=0;i<rowAction;i++){
			magess.append("患者姓名:");
			magess.append(action.getValue("PAT_NAME",i));
			magess.append("        ");
		}
		//      System.out.println("消息:"+magess.toString());
		//启动颜色线程
		changeTableRowColor();
		//启用滚动文字
		startMoveThread();
		//启动音乐
		startSound();
	}
	/**
	 * 删除消息队列
	 * @param parm TParm
	 */
	public void removeMessage(TParm parm){
		int rowCount = action.getCount("CASE_NO");
		String caseNo=parm.getValue("CASE_NO");
		for(int i=rowCount-1;i>=0;i--){
			if(caseNo.equals(action.getValue("CASE_NO",i))){
				action.removeRow(i);
			}
		}
	}
	/**
	 * 鼠标拖动
	 * @param e MouseEvent
	 */
	private void mouseDraggedEvent(MouseEvent e) {
		int x = e.getX()-otherX+this.getX();
		int y = e.getY()-otherY+this.getY();
		this.setLocation(x,y);
	}

	/**
	 * 启动颜色线程
	 */
	private void startColorThread() {
		if(colorThread!=null)
			return;
		colorThread = new Thread() {
			public void run() {
				while(colorThread!=null){
					try {
						Thread.sleep(500);
						workColor();
					}
					catch (InterruptedException ex) {
					}
				}
				setBackground(new Color(0,51,102));
				repaint();
			}
		};
		colorThread.start();
	}
	/**
	 * 开始工作
	 */
	public void workColor(){
		colorRowState=!colorRowState;
		this.setBackground(colorRowState?new Color(255,0,0):new Color(0,0,0));
		this.repaint();
	}

	/**
	 * 改变TABLE颜色
	 * @param row int
	 */
	public void changeTableRowColor(){
		startColorThread();
	}

	/**
	 * 停止颜色线程
	 */
	private void stopColorThread() {
		colorThread=null;
	}

	/**
	 * 启动滚动文字线程
	 */
	private void startMoveThread(){
		if(textThread!=null)
			return;
		textThread = new Thread() {
			public void run() {
				while(textThread!=null){
					try {
						Thread.sleep(500);
						workText();
					}
					catch (InterruptedException ex) {
					}
				}
				magess = new StringBuffer();
				repaint();
			}
		};
		textThread.start();
	}
	/**
	 * 开始工作
	 */
	public void workText(){
		if(textX>=this.getWidth())
			textX=0;
		textX+=10;
		this.repaint();
	}
	/**
	 * 停止滚动文字线程
	 */

	private void stopMoveThread() {
		textThread=null;
	}
}
