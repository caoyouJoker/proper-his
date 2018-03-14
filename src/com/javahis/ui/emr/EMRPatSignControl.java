package com.javahis.ui.emr;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TPanel;
/**
 * PDF病患签名界面
 * 
 * @author wangqing 20171018
 *
 */
public class EMRPatSignControl extends TControl {
	private static String CLOSE_ACTION = "CLOSE_ACTION";
	private static String OPEN_URL_ACTION = "OPEN_URL_ACTION";
	/**
	 * 用来显示PDF
	 */
	private TPanel pdfPanel;
	private Thread0 thread0;

	/**
	 * 系统参数
	 */
	private TParm sysParm;

	/**
	 * 是否可以签字
	 */
	private boolean canSign;
	/**
	 * 签名图片
	 */
	private String imgFile;
	/**
	 * 签字内容
	 */
	private String patientSignContent;
	/**
	 * PDF文件
	 */
	private String pdfFile;
	
	/**
	 * 标识文件（标识签名图片是否已经完全生成）
	 */
	private String flgFile;
	

	/**
	 * 本地签名jar路径
	 */
	private final String jarFile = "C:\\JavaHisFile\\sign\\signature.jar";

	/**
	 * 是否可以关闭 true，可以；false，不可以
	 */
	private boolean closeFlg = false;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();	
		// 初始化系统参数
		Object obj = this.getParameter();
		if(obj != null && obj instanceof TParm){
			sysParm = (TParm) obj;
			sysParm.addListener(CLOSE_ACTION, this, "onCloseAction");
			sysParm.addListener(OPEN_URL_ACTION, this, "onOpenUrlAction");
			initSysParm();
		}else{
			this.messageBox("系统参数错误！！！");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
//		pdfFile = "http://www.baidu.com";
		// 界面初始化
		pdfPanel = (TPanel) this.getComponent("PDF_SIGN_PANEL");
		pdfPanel.setLayout(new BorderLayout());// 设置布局方式为BorderLayout，为了嵌入SWT控件
		final TFrame frame = (TFrame) this.getComponent();
		// 添加窗口监听
		frame.addWindowListener(new WindowAdapter(){
			//捕获窗口关闭事件
			public void windowClosing(WindowEvent e){      	  
				//窗口关闭时的相应处理操作
//				int exi = JOptionPane.showConfirmDialog (null, "要关闭此窗口吗？", "友情提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); 
//				if (exi == JOptionPane.YES_OPTION){ 
//					frame.dispose();
//				} else{ 
//					
//				}
			}
			//捕获窗口最小化事件
			public void windowIconified(WindowEvent e){				
				//窗口最小化时的相应处理操作
			}
		});
		frame.setPreferredSize(new Dimension((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, 
				(int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
		final Canvas canvas = new Canvas();
		pdfPanel.add(canvas, BorderLayout.CENTER );			
		frame.pack();
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {			
//				frame.setPreferredSize(new Dimension(2000, 600));
//				frame.setResizable(false);  // 不可缩放
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // 默认最大化
				thread0 = new Thread0(pdfFile, canvas);
				thread0.start();
			}
		});	

	}

	/**
	 * 签名
	 */
	public void onSign(){
		try {
			String cmd = "cmd /c java -jar "+jarFile+" "+imgFile+" "+flgFile+" "+patientSignContent;
			System.out.println("===cmd:"+cmd);
			Runtime.getRuntime().exec(cmd);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void onCloseAction(String arg0){
		closeFlg = true;
		closeWindow();
	}

	public void onOpenUrlAction(String arg0){
		initSysParm();
		thread0.openUrl(pdfFile);
	}

	/**
	 * Swing嵌入SWT
	 * 
	 * @author wangqing
	 *
	 */
	private class Thread0 extends Thread{
		private Display display;
		private Canvas canvas;		
		private Shell shell;
		private Browser browser;
		private String url;

		public Thread0(String url, Canvas canvas){
			this.url  = url;		
			this.canvas = canvas;	
		}

		public void run(){		
			display = new Display();
			//			shell = new Shell(display);
			shell = SWT_AWT.new_Shell(display, canvas);// Swing嵌入SWT
			shell.setLayout( new FillLayout() );
			shell.open();
			// 嵌入浏览器
			browser = new Browser(shell, SWT.NONE);
			browser.setLayoutData(BorderLayout.CENTER);
			browser.setUrl(url);
			// 	SWT事件循环			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())// 处理事件
					display.sleep();
			}
			display.dispose();		
		}

		public void openUrl(final String url_){
			display.syncExec( new Runnable() {
				public void run() {
					url = url_;
					browser.setUrl(url_);
				}
			} );		
		}

		public void close(){
			if(display.isDisposed())return;
			display.syncExec( new Runnable() {
				public void run() {
					if(shell.isDisposed())return;
					shell.dispose();
				}
			} );		
		}

	}

	public boolean onClosing(){	
		if(!closeFlg){
			return false;
		}
		if(thread0 != null){
			thread0.close();
		}
		return true;
	}

	public void initSysParm(){
		if(sysParm == null){
			this.messageBox("sysParm is null");
			return;
		}
		imgFile = sysParm.getValue("imgFile");
		patientSignContent = sysParm.getValue("patientSignContent");
		pdfFile = sysParm.getValue("pdfFile");
		canSign = sysParm.getBoolean("canSign");
		flgFile = sysParm.getValue("flgFile");// add by wangqing 20171101
		if(!canSign){
//			this.callFunction("UI|sign|setEnabled", false);
			TButton button = (TButton) this.getComponent("sign");
			button.setEnabled(false);
		}else{
//			this.callFunction("UI|sign|setEnabled", true);
			TButton button = (TButton) this.getComponent("sign");
			button.setEnabled(true);
		}
	}

}
