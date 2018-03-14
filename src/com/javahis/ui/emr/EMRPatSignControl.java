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
 * PDF����ǩ������
 * 
 * @author wangqing 20171018
 *
 */
public class EMRPatSignControl extends TControl {
	private static String CLOSE_ACTION = "CLOSE_ACTION";
	private static String OPEN_URL_ACTION = "OPEN_URL_ACTION";
	/**
	 * ������ʾPDF
	 */
	private TPanel pdfPanel;
	private Thread0 thread0;

	/**
	 * ϵͳ����
	 */
	private TParm sysParm;

	/**
	 * �Ƿ����ǩ��
	 */
	private boolean canSign;
	/**
	 * ǩ��ͼƬ
	 */
	private String imgFile;
	/**
	 * ǩ������
	 */
	private String patientSignContent;
	/**
	 * PDF�ļ�
	 */
	private String pdfFile;
	
	/**
	 * ��ʶ�ļ�����ʶǩ��ͼƬ�Ƿ��Ѿ���ȫ���ɣ�
	 */
	private String flgFile;
	

	/**
	 * ����ǩ��jar·��
	 */
	private final String jarFile = "C:\\JavaHisFile\\sign\\signature.jar";

	/**
	 * �Ƿ���Թر� true�����ԣ�false��������
	 */
	private boolean closeFlg = false;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();	
		// ��ʼ��ϵͳ����
		Object obj = this.getParameter();
		if(obj != null && obj instanceof TParm){
			sysParm = (TParm) obj;
			sysParm.addListener(CLOSE_ACTION, this, "onCloseAction");
			sysParm.addListener(OPEN_URL_ACTION, this, "onOpenUrlAction");
			initSysParm();
		}else{
			this.messageBox("ϵͳ�������󣡣���");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
//		pdfFile = "http://www.baidu.com";
		// �����ʼ��
		pdfPanel = (TPanel) this.getComponent("PDF_SIGN_PANEL");
		pdfPanel.setLayout(new BorderLayout());// ���ò��ַ�ʽΪBorderLayout��Ϊ��Ƕ��SWT�ؼ�
		final TFrame frame = (TFrame) this.getComponent();
		// ��Ӵ��ڼ���
		frame.addWindowListener(new WindowAdapter(){
			//���񴰿ڹر��¼�
			public void windowClosing(WindowEvent e){      	  
				//���ڹر�ʱ����Ӧ�������
//				int exi = JOptionPane.showConfirmDialog (null, "Ҫ�رմ˴�����", "������ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); 
//				if (exi == JOptionPane.YES_OPTION){ 
//					frame.dispose();
//				} else{ 
//					
//				}
			}
			//���񴰿���С���¼�
			public void windowIconified(WindowEvent e){				
				//������С��ʱ����Ӧ�������
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
//				frame.setResizable(false);  // ��������
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ĭ�����
				thread0 = new Thread0(pdfFile, canvas);
				thread0.start();
			}
		});	

	}

	/**
	 * ǩ��
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
	 * SwingǶ��SWT
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
			shell = SWT_AWT.new_Shell(display, canvas);// SwingǶ��SWT
			shell.setLayout( new FillLayout() );
			shell.open();
			// Ƕ�������
			browser = new Browser(shell, SWT.NONE);
			browser.setLayoutData(BorderLayout.CENTER);
			browser.setUrl(url);
			// 	SWT�¼�ѭ��			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())// �����¼�
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
