package com.javahis.ui.emr;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.caigen.a.i;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>病患知情同意书签名</p>
 * 
 * @author WangQing 20170502
 *
 */
public class EMRInformedConsentFormControl extends TControl {
	/**
	 * 签名病历列表
	 */
	private TTable table;
	/**
	 * 用来显示pdf
	 */
	private TPanel pdfPanel;	
	/**
	 * 监听线程（监听pdf是否完成合并）
	 */
	PDFThread3 thread3;
	/**
	 * swt线程（医生）
	 */
	SWTThread swThread;
	/**
	 * swt线程（病患）
	 */
	SWTThread2 swThread2;

	/**
	 * 虚拟合并线程 add by wangqing 20171018
	 */
	private PDFThread4 thread4;

	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 住院就诊号
	 */
	private String caseNo;
	/**
	 * pdf缓存路径1
	 */
	private static String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	/**
	 * pdf缓存路径2（签名专用）
	 */
	private static String signTempPath = "C:\\JavaHisFile\\sign\\temp\\pdf";
	/**
	 * 签名图片缓存路径
	 */
	private static String picTempPath = "C:\\JavaHisFile\\sign\\temp\\picture";
	
	/**
	 * 标识文件路径（标识签名图片是否已经完全生成）
	 */
	private static String txtTempPath = "C:\\JavaHisFile\\sign\\temp\\txt";
	
	/**
	 * 签名密匙
	 */
	private static String keyFile = "C:\\JavaHisFile\\sign\\demo.p12";
	/**
	 * 本地签名jar路径
	 */
	private static String jarFile = "C:\\JavaHisFile\\sign\\signature.jar";
	/**
	 * table选中行
	 */
	private int selectRow = -1;
	/**
	 * 动作类名字
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * 用来关闭监听线程的死循环
	 */
	private boolean closeFlg  = false;

	/**
	 * 首次签名关键字
	 */
	final static String KEY_WORD = "注明与病患的关系";// add by wangqing 20170926 新增二次签名功能
	/**
	 * 二次签名关键字
	 */
	final static String KEY_WORD_TWO = "注明和病患的关系";// add by wangqing 20170926 新增二次签名功能

	private static String CLOSE_ACTION = "CLOSE_ACTION";

	private static String OPEN_URL_ACTION = "OPEN_URL_ACTION";

	/**
	 * 为患者签名界面传参数
	 */
	private TParm patSignParm;

	/**
	 * 患者界面是否已经打开
	 */
	private boolean isOpen = false;

	/**
	 * 系统参数
	 */
	private TParm sysParm;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		// 获取系统参数
		Object obj = this.getParameter();
		if(obj != null && obj instanceof TParm){
			sysParm= (TParm) obj;
		}else{
			this.messageBox("系统参数错误");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
		// 初始化数据
		this.setValue("MR_NO", sysParm.getValue("MR_NO"));
		this.mrNo = sysParm.getValue("MR_NO");
		this.caseNo = sysParm.getValue("CASE_NO");
		// 获取控件
		table = (TTable) this.getComponent("TABLE");
		pdfPanel = (TPanel) this.getComponent("tPanel_12");
		pdfPanel.setLayout(new BorderLayout());// 设置布局方式为BorderLayout，为了嵌入SWT控件
		
		patSignParm = new TParm();
		patSignParm.setData("pdfFile", "http://www.baidu.com");
		patSignParm.setData("canSign", false);
		patSignParm.setData("imgFile", "");
		patSignParm.setData("patientSignContent", "");	
		
		// 增加监听事件
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");// 双击
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");// 单击
		// 第一次合并PDF比较慢，所以初始化时，虚拟合并一次
		thread4 = new PDFThread4();
		thread4.start();
		
		final TFrame frame = (TFrame) getComponent();
		frame.setPreferredSize(new Dimension((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
				(int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
		final Canvas canvas = new Canvas();
		pdfPanel.add(canvas, BorderLayout.CENTER );			
		frame.pack();//Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
		
		// 查询病患信息和签名病历信息
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {	
				onMrNo();
				onQuery();
			}
		});	
		// 初始化PDF界面
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {							
				swThread = new SWTThread("http://www.baidu.com", canvas);
				swThread.start();	
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // 默认最大化
			}
		});
		// 关闭小键盘
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {				
				int ii = 0;
				while(ii<50 && JNATestDll.instanceDll.myGetKeyState()==1){
					Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
					ii++;
				}
			}
		});	
	}

	/**
	 * 病案号回车事件
	 */
	public void onMrNo(){
		// 合并病案号start
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
				this.mrNo = pat.getMrNo();
			}
		}
		// 合并病案号end
		String sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
				+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
				+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
				+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
				//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
				+ " , A.DAY_OPE_FLG " //add by huangjw 20170322
				+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E "
				+ " WHERE A.BED_NO=B.BED_NO(+) "
				+ " AND A.CASE_NO=B.CASE_NO(+)"	
				+ " AND A.MR_NO=C.MR_NO(+) "
				+ " AND A.MAINDIAG = D.ICD_CODE(+) "
				+ " AND A.CASE_NO=E.CASE_NO(+) "
				+ " AND A.MR_NO='"+this.getValueString("MR_NO")+"' "
				+ " AND A.CASE_NO='"+caseNo+"' "
				+ " ORDER BY A.IPD_NO ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<0){
			this.messageBox("无此病患信息");
			return;
		}
		System.out.println("--------result:"+result);
		setValueForParm("MR_NO;PAT_NAME;IPD_NO;SEX_CODE;DEPT_CODE;STATION_CODE;BED_NO_DESC;"
				+ "SERVICE_LEVEL;WEIGHT;VS_DR_CODE;IN_DATE;TOTAL_AMT;PAY_INS;TOTAL_BILPAY;GREENPATH_VALUE;CUR_AMT", result, 0);
	}

	/**
	 * 签名同意书列表
	 */
	public void onQuery(){	
		String sql = "SELECT E.FILE_SEQ, E.CASE_NO, E.MR_NO, "
				+ " E.FILE_NAME,E.FILE_PATH, E.PDF_CREATOR_DATE AS CREATE_TIME, "
				+ " E.IS_PATIENT_SIGN_FLG AS SIGN_STATUS, F.PATIENT_SIGN_FLG "
				+ " ,E.IS_PATIENT_SIGN_TWO_FLG AS SIGN_TWO_STATUS, F.PATIENT_SIGN_TWO_FLG "// add by wangqing 20170926 新增二次签名功能
				+ " FROM JAVAHIS.EMR_FILE_INDEX E, EMR_TEMPLET F  "
				+ " WHERE E.SUBCLASS_CODE = F.SUBCLASS_CODE(+) "
				+ " AND E.CASE_NO ='" + caseNo
				+ "' AND E.MR_NO='" + mrNo 
				+ "' AND F.PATIENT_SIGN_FLG ='Y' "
				+ " ORDER BY FILE_SEQ ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable)this.getComponent("TABLE");
		table.setParmValue(parm);
	}

	/**
	 * table单击事件
	 */
	public void onTableClicked(int row){
		this.selectRow = row;	
		TParm rowParm = table.getParmValue().getRow(selectRow);
		String fileName = rowParm.getValue("FILE_NAME");// 文件名
		String filePath = rowParm.getValue("FILE_PATH");// 文件服务器路径
		System.out.println("======//////filePath="+filePath);
	}

	/**
	 * TABLE双击事件
	 * @param row
	 * @throws IOException
	 */
	public void onTableDoubled(int row) throws IOException{ 
		if(!isOpen){
			this.messageBox("请先手动打开患者签名界面");
			return;
		}		
		this.selectRow = row;		
		TParm rowParm = table.getParmValue().getRow(row);
		String fileName = rowParm.getValue("FILE_NAME");// 文件名称
		boolean hasFirstSign = rowParm.getValue("SIGN_STATUS").trim().equals("Y")?true:false;// 有无首次签名
		boolean canSignAgain = rowParm.getValue("PATIENT_SIGN_TWO_FLG").trim().equals("Y")?true:false;// 患者可否可以二次签名
		boolean hasSecondSign = rowParm.getValue("SIGN_TWO_STATUS").trim().equals("Y")?true:false;// 有无二次签名
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		String now = format.format(date);
		String inFile = signTempPath + "\\" + rowParm.getData("FILE_NAME") + "_" + now + ".pdf";
		String outFile = signTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now + ".pdf";
		String imgFile = picTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now +".png"; 		
		String flgFile = txtTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now +".txt";// 标识签名图片是否已经完全生成
		
		// FileServer文件绝对路径
		String filePath = getEmrDataDir(fileName) + "\\"
				+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
				+ fileName + ".pdf";
		// 读取文件
		byte[] data = TIOM_FileServer.readFile(getFileServerAddress(fileName), filePath);
		if (data == null) {// 服务器上没有找到文件
			if(hasFirstSign){// 已经签名，没有找到签名文件
				this.messageBox("没有找到签名文件！！！");
				return;
			}			
			// 生成pdf
			TParm pdfParm = new TParm();// FILE_PATH;FILE_NAME;FILE_SEQ;MR_NO;CASE_NO
			pdfParm.setData("FILE_PATH", rowParm.getValue("FILE_PATH"));
			pdfParm.setData("FILE_NAME", rowParm.getValue("FILE_NAME"));
			pdfParm.setData("FILE_SEQ", rowParm.getValue("FILE_SEQ"));
			pdfParm.setData("MR_NO", rowParm.getValue("MR_NO"));
			pdfParm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
			// 执行打印
			if(!onNewPdf(pdfParm)){ 
				return;
			}
			data = TIOM_FileServer.readFile(getFileServerAddress(fileName), filePath);
			onQuery();// 更新创建pdf时间
		}	
		// 拷贝到本地
		try {	
			File fDir = new File(signTempPath);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			FileTool.setByte(inFile, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 医生操作界面打开pdf
		swThread.openUrl(inFile);			
		// 签字板界面打开pdf
		// add by wangqing 20170926 start 新增二次签名功能
		TWord word = new TWord();
		word.onOpen(rowParm.getValue("FILE_PATH"), rowParm.getValue("FILE_NAME") + ".jhw", 3, true);
		String patientSignContent = this.getEFixedValue(word, "PATIENT_SIGN_CONTENT");// 病患首次签名照抄内容
		String patientSignTwoContent = this.getEFixedValue(word, "PATIENT_SIGN_TWO_CONTENT");// 病患二次签名照抄内容
		if(patientSignContent == null){
			patientSignContent = "";
		}
		if(patientSignTwoContent == null){
			patientSignTwoContent = "";
		}
		if(hasFirstSign){// 有首次签名
			if(canSignAgain){// 可以二次签名
				if(hasSecondSign){// 有二次签名
					patSignParm.setData("pdfFile", inFile);
					patSignParm.setData("canSign", false);	
					try {
						patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}else{// 没有二次签名				
					patSignParm.setData("pdfFile", inFile);
					patSignParm.setData("canSign", true);
					patSignParm.setData("imgFile", imgFile);
					patSignParm.setData("flgFile", flgFile);
					patSignParm.setData("patientSignContent", patientSignTwoContent);		
					try {
						patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
					} catch (Exception ee) {
						ee.printStackTrace();
					}	
					// 获取签名坐标和页码		
					PDFCoordinate p = new PDFCoordinate();
					float[] f = p.getKeyWords(inFile, KEY_WORD_TWO);
					if(f==null){
						return;
					}
					// 开始监听
					thread3 = new PDFThread3(inFile, outFile, keyFile, imgFile, flgFile, f[0]+200, f[1]-100, f[0]+500, f[1], (int)f[2], false);
					thread3.start();
				}
			}else{// 不可以二次签名
				patSignParm.setData("pdfFile", inFile);
				patSignParm.setData("canSign", false);	
				try {
					patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
				} catch (Exception ee) {
					ee.printStackTrace();
				}					
			}
		}else{// 没有首次签名
			patSignParm.setData("pdfFile", inFile);
			patSignParm.setData("canSign", true);
			patSignParm.setData("imgFile", imgFile);
			patSignParm.setData("flgFile", flgFile);// 标识文件
			patSignParm.setData("patientSignContent", patientSignContent);
			try {
				patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
			} catch (Exception ee) {
				ee.printStackTrace();
			}				
			// 获取签名坐标和页码		
			PDFCoordinate p = new PDFCoordinate();
			float[] f = p.getKeyWords(inFile, KEY_WORD);
			if(f==null){
				return;
			}
			// 开始监听
			thread3 = new PDFThread3(inFile, outFile, keyFile, imgFile, flgFile, f[0]+200, f[1]-100, f[0]+500, f[1], (int)f[2], true);
			thread3.start();
		}
		// add by wangqing 20170926 end
	}

	/**
	 * Swing嵌入SWT
	 * 
	 * @author wangqing
	 *
	 */
	private class SWTThread extends Thread{
		private Display display;
		private Canvas canvas;		
		private Shell shell;
		private Browser browser;
		private String url;

		public SWTThread(String url, Canvas canvas){
			this.url  = url;		
			this.canvas = canvas;	
		}

		public void run(){		
			display = new Display();
			//			shell = new Shell(display);
			shell = SWT_AWT.new_Shell(display, canvas);// Swing嵌入SWT
			shell.setLayout( new FillLayout() );
			shell.open();
			
			org.eclipse.swt.events.MouseListener mListener = new org.eclipse.swt.events.MouseListener(){

				@Override
				public void mouseDoubleClick(MouseEvent arg0) {
					System.out.println("//////mouseDoubleClick//////");
					// TODO Auto-generated method stub
					Robot robot = null;
					try {
						robot = new Robot();
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					robot.keyPress(KeyEvent.VK_F8);
					robot.keyRelease(KeyEvent.VK_F8); 					
				}

				@Override
				public void mouseDown(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseUp(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			};
			shell.addMouseListener(mListener);
			
			
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

	/**
	 * 纯SWT
	 * 
	 * @author wangqing
	 *
	 */
	private class SWTThread2 extends Thread{
		private Display display;		
		private Shell shell;
		private Browser browser;
		private MenuItem item;
		private String url;
		/**
		 * 是否可以签名
		 */
		private boolean canSign;
		String outFile;
		String keyFile;
		String imgFile;
		String flgFile;
		private String patientSignContent;
		String keyWord;// add by wangqing 20170926 新增患者二次签名功能

		public SWTThread2(String url, String outFile, String keyFile, String imgFile, String flgFile, boolean canSign, String patientSignContent, String keyWord){
			this.url  = url;
			this.outFile = outFile;
			this.keyFile = keyFile;
			this.imgFile = imgFile;
			this.canSign = canSign;	
			this.patientSignContent = patientSignContent;
			this.keyWord = keyWord;// add by wangqing 20170926  新增患者二次签名功能
		}

		public void run(){		
			display = new Display();
			shell = new Shell(display);
			shell.setLayout( new FillLayout() );
			shell.setText("患者签名");
			shell.open();				
			shell.setMaximized(true);// 最大化
			shell.addShellListener(new ShellAdapter() {
				public void shellActivated(ShellEvent e) {
					System.out.println("Shell has been activated\n");
				}
				public void shellClosed(ShellEvent e) {
					System.out.println("Shell has been closed");
					//					MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					//					mb.setText("提示");
					//					mb.setMessage("确定要关闭吗?");
					//					int rc = mb.open();
					//					e.doit = (rc == SWT.YES);
					e.doit = false;
				}
				public void shellDeactivated(ShellEvent e) {
					System.out.println("Shell has been deactivated\n");
				}
				public void shellDeiconified(ShellEvent e) {
					System.out.println("Shell has been deiconified\n");
				}
				public void shellIconified(ShellEvent e) {
					System.out.println("Shell has been iconified\n");
				}

			});
			// 添加Menu
			Menu menu = new Menu(shell, SWT.BAR);
			shell.setMenuBar(menu);
			item = new MenuItem(menu, SWT.PUSH);			
			item.setText("签名");
			item.setEnabled(false);
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {					
					System.out.println("Action Performed");
					try {
						// modified by wangqing 20170926 新增患者二次签名功能
						String cmd = "cmd /c java -jar "+jarFile+" "+url+" "+outFile+" "+keyFile+" "+imgFile+" "+keyWord+" "+flgFile+" "+patientSignContent;
						System.out.println("===cmd:"+cmd);
						Runtime.getRuntime().exec(cmd);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			});
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

		public void openUrl(final String url_, final String outFile_, final String keyFile_, final String imgFile_, final String flgFile_, final Boolean canSign_, final String patientSignContent_, final String keyWord_){
			display.syncExec( new Runnable() {
				public void run() {
					canSign = canSign_;
					if(canSign){
						item.setEnabled(true);
					}else{
						item.setEnabled(false);
					}
					url = url_;
					outFile = outFile_;
					keyFile = keyFile_;
					imgFile = imgFile_;
					flgFile = flgFile_;
					patientSignContent = patientSignContent_;
					keyWord = keyWord_;// add by wangqing 20170926
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

		public boolean isDisposed(){
			final boolean[] arr = {false};
			display.syncExec( new Runnable() {
				public void run() {
					arr[0] = shell.isDisposed();
				}
			} );
			return arr[0];
		}

	}

	/**
	 * <p>监听线程</p>
	 * 
	 * <p>监听病患是否完成签名，如果完成签名（签名图片已经生成），合并pdf、上传pdf</p>
	 * 
	 * @author wangqing
	 *
	 */
	private class PDFThread3 extends Thread{
		/**
		 * 合并前PDF
		 */
		private String inFile;
		/**
		 * 签名图片
		 */
		private String imgFile;
		/**
		 * 合并后PDF
		 */
		private String outFile;
		/**
		 * 签名密匙
		 */
		private String keyFile;
		/**
		 * 标识文件
		 */
		private String flgFile;
		
		/**
		 * 签名区域左下角x坐标
		 */
		private float llx;
		/**
		 * 签名区域左下角y坐标
		 */
		private float lly;
		/**
		 * 签名区域右上角x坐标
		 */
		private float urx;
		/**
		 * 签名区域右上角y坐标
		 */
		private float ury;
		/**
		 * 签名页码
		 */
		private int pageNo;
		/**
		 * 是否首次签名
		 */
		private boolean isFirst = false;// add by wangqing 20170926 新增二次签名功能

		public PDFThread3(String inFile, String outFile, String keyFile, String imgFile, String flgFile, float llx, float lly, float urx, float ury, int pageNo, boolean isFirst){
			super();
			this.inFile = inFile;
			this.outFile =outFile;
			this.keyFile = keyFile;
			this.imgFile = imgFile;
			this.flgFile = flgFile;
			this.llx = llx;
			this.lly = lly;
			this.urx = urx;
			this.ury = ury;
			this.pageNo = pageNo;
			this.isFirst = isFirst;
		}

		public void run(){
			final File file = new File(flgFile);// 签名图片
			while(!file.exists() && !closeFlg);
			if(closeFlg)return;
			// 合并PDF
			try {
				sign(inFile, outFile, keyFile, imgFile, llx, lly, urx, ury, pageNo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//			// 当前线程睡0.5秒
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			//上传PDF
			TParm rowParm = table.getParmValue().getRow(selectRow);
			String fileName = rowParm.getValue("FILE_NAME");// 文件名
			String filePath = rowParm.getValue("FILE_PATH");// 文件服务器路径			
			String srcFilePath = outFile;// 本地文件路径
			String outFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF") + "\\" + fileName + ".pdf";// 服务器文件路径	
			if(!onUpdate(srcFilePath, outFilePath)){
				messageBox("pdf上传失败！！！");
				return;
			}
			//			// 当前线程睡0.5秒
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			// 上传备份文件
			if(isFirst){
				//				String outBackFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF") + "\\" + fileName + "_back.pdf";	
				String outBackFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF_BACK") + "\\" + fileName + "_back.pdf";
				if(!onUpdate(srcFilePath, outBackFilePath)){
					messageBox("备份pdf上传失败！！！");
					return;
				}
			}	
			//			// 当前线程睡0.5秒
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			// 上传成功后更新EMR_FILE_INDEX的签名状态
			if(isFirst){
				//更新EMR_FILE_INDEX.IS_PATIENT_SIGN_FLG
				String sql = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_FLG ='Y' WHERE CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"' AND FILE_NAME='"+fileName+"'";
				TParm result = new TParm(TJDODBTool.getInstance().update(sql));
				if(result.getErrCode()<0){
					return;
				}
			}else{
				//更新EMR_FILE_INDEX.IS_PATIENT_SIGN_TWO_FLG
				String sql = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_TWO_FLG ='Y' WHERE CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"' AND FILE_NAME='"+fileName+"'";
				TParm result = new TParm(TJDODBTool.getInstance().update(sql));
				if(result.getErrCode()<0){;
				return;
				}
			}
			// 在UI线程刷新列表，并重新执行双击行事件		
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onQuery();
					try {
						onTableDoubled(selectRow);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 虚拟合并PDF线程
	 * 
	 * @author wangqing 20171018
	 *
	 */
	private class PDFThread4 extends Thread{
		public void run(){
			String inFile = "C:\\JavaHisFile\\sign\\merge\\merge_in.pdf";
			String outFile = "C:\\JavaHisFile\\sign\\merge\\merge_out.pdf";
			String imgFile = "C:\\JavaHisFile\\sign\\merge\\merge.png";
			float llx = 0;
			float lly = 0;
			float urx = 500;
			float ury = 500;
			int pageNo = 1;
			try {
				sign(inFile, outFile, keyFile, imgFile, llx, lly, urx, ury, pageNo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 重签
	 */
	public void reSign(){
		if(table.getSelectedRow()<0){
			this.messageBox("请选择一行已签名数据！！！");
			return;
		}
		TParm rowParm = table.getParmValue().getRow(table.getSelectedRow());
		// add by wangqing 20170926 新增二次签名功能
		String fileName = rowParm.getValue("FILE_NAME");// 文件名称
		String filePath = rowParm.getValue("FILE_PATH");
		boolean isSign = rowParm.getValue("SIGN_STATUS").trim().equals("Y")?true:false;// 有无首次签名
		boolean canSignTwo = rowParm.getValue("PATIENT_SIGN_TWO_FLG").trim().equals("Y")?true:false;// 患者可否二次签名
		boolean isSignTwo = rowParm.getValue("SIGN_TWO_STATUS").trim().equals("Y")?true:false;// 有无二次签名
		if(!isSign){
			this.messageBox("此病历尚未签名！！！");
			return;
		}
		if(isSignTwo){// 是否有二次签名
			// FileServer文件绝对路径
			//			String backFilePath = getEmrDataDir(fileName) + "\\"
			//					+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
			//					+ fileName + "_back.pdf";
			String backFilePath = getEmrDataDir(fileName) + "\\"
					+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF_BACK") + "\\"
					+ fileName + "_back.pdf";
			byte[] data = TIOM_FileServer.readFile(getFileServerAddress(fileName), backFilePath);
			// 拷贝到本地
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
			String now = format.format(date);
			String backFile = signTempPath + "\\" + rowParm.getData("FILE_NAME") + "_" + now + ".pdf";
			try {	
				File fDir = new File(signTempPath);
				if (!fDir.exists()) {
					fDir.mkdirs();
				}
				FileTool.setByte(backFile, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 上传到FileServer服务器
			String outFilePath = getEmrDataDir(fileName) + "\\"
					+ filePath.replaceFirst("JHW", "PDF") + "\\"
					+ fileName + ".pdf";
			if(!onUpdate(backFile, outFilePath)){
				messageBox("pdf上传失败！！！");
				return;
			}
			//更新EMR_FILE_INDEX.IS_PATIENT_SIGN_TWO_FLG
			String sql = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_TWO_FLG ='N' WHERE CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"' AND FILE_NAME='"+fileName+"'";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				return;
			}		
		}else{
			TParm pdfParm = new TParm();// FILE_PATH;FILE_NAME;FILE_SEQ;MR_NO;CASE_NO
			pdfParm.setData("FILE_PATH", rowParm.getValue("FILE_PATH"));
			pdfParm.setData("FILE_NAME", rowParm.getValue("FILE_NAME"));
			pdfParm.setData("FILE_SEQ", rowParm.getValue("FILE_SEQ"));
			pdfParm.setData("MR_NO", rowParm.getValue("MR_NO"));
			pdfParm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
			// 执行打印
			if(!onNewPdf(pdfParm)){ 
				this.messageBox("生成pdf失败");
				return;
			}
		}		
		onQuery();
		rowParm = table.getParmValue().getRow(selectRow);
		try {
			onTableDoubled(selectRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean onClosing(){
		if(swThread != null){
			swThread.close();
		}
		//		if(swThread2 != null){
		//			swThread2.close();
		//		}	
		try {
			if(patSignParm != null){
				patSignParm.runListener(CLOSE_ACTION, new Object[]{"0"});
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
//		try {
//			deleteFile(new File(signTempPath));
//			deleteFile(new File(picTempPath));
//			deleteFile(new File(txtTempPath));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return true;
	}

	/**
	 * 通过文件名，确认文件路径
	 * @param fileName
	 * @return
	 */
	public String getEmrDataDir(String fileName){
		//
		String strEmrDataDir=TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrData");
		// 1.文件名不是空串情况下
		if (fileName != null && !fileName.equals("")) {
			// 2.包含-的情况
			if (fileName.indexOf("_") != -1) {
				// 3.取第一个caseNo下的情况前2位的情况
				String sYear = fileName.substring(0, 2);
				//System.out.println("---sYear：---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
				String root = config.getString("","FileServer." + sYear + ".Root");
				if (root != null && !root.equals("")) {
					// 4.找指定的配置文件，如果有则：
					strEmrDataDir = TIOM_FileServer.getRoot(sYear)+ TIOM_FileServer.getPath("EmrData");
				}

			}
		}

		return strEmrDataDir;
	}
	
	/**
	 * 通过文件名，确认服务器地址
	 * 
	 * @param fileName
	 * @return
	 */
	public TSocket getFileServerAddress(String fileName) {
		//默认文件服器
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// 1.文件名不是空串情况下
		if (fileName != null && !fileName.equals("")) {
			// 2.包含-的情况
			if (fileName.indexOf("_") != -1) {
				// 3.取第一个caseNo下的情况前2位的情况
				String sYear = fileName.substring(0, 2);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
				String ip = config.getString("","FileServer." + sYear + ".IP");
				if (ip != null && !ip.equals("")) {
					// 4.找指定的配置文件，如果有则：
					tsocket = TIOM_FileServer.getSocket(sYear);
				}

			}
		}  	
		return tsocket;
	}

	/**
	 * 切换屏幕
	 */
	public void changeScreen(){	
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_RIGHT); 

		robot.keyRelease(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_SHIFT);	
		robot.keyRelease(KeyEvent.VK_WINDOWS); 
	}

	/**
	 * 上传文件
	 * @param srcFilePath 源文件路径
	 * @param outFilePath 生成文件路径
	 * @return true 上传成功；false 上传失败
	 */
	public boolean onUpdate(String srcFilePath, String outFilePath){
		File srcFile = new File(srcFilePath);
		try {
			TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
					outFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			byte data[] = FileTool.getByte(srcFile);
			if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					outFilePath, data)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除文件或文件夹中的文件（不删除文件夹）
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(File file) throws IOException{
		boolean flg = false;
		if(!file.exists()){
			flg = false;
		}
		// 文件
		if(file.isFile()){
			flg = file.delete();
		}
		// 路径
		if(file.isDirectory()){
			flg = true;
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (file.getPath().endsWith(File.separator)) {
					temp = new File(file.getPath() + tempList[i]);
				} else {
					temp = new File(file.getPath() + File.separator + tempList[i]);
				}
				flg = deleteFile(temp);// 使用递归来删除
				if(!flg){
					break;
				}
			}
			//			if(flg){
			//				flg = file.delete();// 删除空文件夹
			//			}
		}
		return flg;
	}

	/**
	 * 生成PDF
	 * @param parm {FILE_PATH;FILE_NAME;FILE_SEQ;MR_NO;CASE_NO}
	 * @return
	 */
	public boolean onNewPdf(TParm parm){
		boolean flg = true;
		File fDir = new File(tempPath);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		File file;
		Date beginDate;
		Date endDate;
		// 默认超时时间为30s
		int timeOut = 30;
		String pdfTransTimeOut = TConfig.getSystemValue("PDF_TRANS_TIME_OUT");
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		if (StringUtils.isNotEmpty(pdfTransTimeOut)) {
			timeOut = Integer.parseInt(pdfTransTimeOut);
		}
		parm.setData("TEMP_PATH", tempPath);//临时路径
		TWord word = new TWord();
		word.onOpen(parm.getValue("FILE_PATH"), parm.getValue("FILE_NAME") + ".jhw", 3, true);
		//$$ =========add by lx 2012/08/10 处理一下男月经始问题 Start===============$$//
		TParm sexP = new TParm(TJDODBTool.getInstance().select(
				"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
						+ parm.getValue("MR_NO") + "'"));
		//System.out.println("===MR_NO==="+parm.getValue("MR_NO"));
		if (sexP.getInt("SEX_CODE", 0) == 9) {
			word.setSexControl(0);
		} else {
			word.setSexControl(sexP.getInt("SEX_CODE", 0));
		}				
		//$$ =========add by lx 2012/08/10 处理一下男月经始问题 end===============$$//
		try {
			word.getPageManager().setOrientation(1);
			word.getPageManager().print(PrinterJob.getPrinterJob(), parm.getValue("FILE_NAME"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		flg = writePDFEmrFile(parm);
		if (!flg) {
			return false;
		}
		// 开始生成PDF的起始时间
		beginDate = SystemTool.getInstance().getDate();
		completeFlg = false;

		do {
			// 生成过程中计算用时，超过指定时间后可认定为文件目录设定错误或文件过大，跳过本次循环
			endDate = SystemTool.getInstance().getDate();
			diff = endDate.getTime() - beginDate.getTime();
			// 实时计算时间间隔，超过30s视为失败
			time = (int) Math.floor(diff / (1000));
			if (time > timeOut) {
				this.messageBox("文件【" + parm.getValue("FILE_NAME")
				+ "】生成错误，文件过大导致超时或PDF存储路径设置错误");
				return false;
			}
			file = new File(tempPath + "\\"
					+ parm.getValue("FILE_NAME") + ".pdf");
			if (file.exists()) {
				completeFlg = true;
			}
		} while (!completeFlg);

		fDir.deleteOnExit();
		String srcFilePath = tempPath + "\\" + parm.getValue("FILE_NAME") + ".pdf";
		String outFilePath = getEmrDataDir(parm.getValue("FILE_NAME")) + "\\"
				+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
				+ parm.getValue("FILE_NAME") + ".pdf";	
		flg = onUpdate(srcFilePath, outFilePath);
		return flg;
	}

	/**
	 * 保存EMR文件
	 * 
	 * @param parm
	 *            TParm {FILE_PATH;FILE_NAME;FILE_SEQ;MR_NO;CASE_NO}
	 */
	public boolean writePDFEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("PDF_CREATOR_USER", Operator.getID());
		parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parm.setData("PDF_CREATOR_DATE", TJDODBTool.getInstance().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CURRENT_USER", Operator.getID());

		// this.messageBox("type"+this.getOnlyEditType());
		TParm result = TIOM_AppServer.executeAction(actionName,
				"writePDFEmrFile", parm);
		if(result.getErrCode()<0){
			falg = false;
		}
		String sql11 = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_FLG='N' "
				+ "WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND FILE_SEQ='"+parm.getValue("FILE_SEQ")+"'";
		TParm result11= new TParm(TJDODBTool.getInstance().update(sql11));
		if(result11.getErrCode()<0){
			falg = false;
		}
		return falg;
	}

	/**
	 * 获取固定文本值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getEFixedValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return null;	
		}
		return f.getText();
	}

	/**
	 * 合并PDF
	 * @param inFile 合并前PDF
	 * @param outFile 合并后PDF
	 * @param keyFile 密匙文件
	 * @param imgFile 签名图片
	 * @param llx 签名区域左下角x坐标
	 * @param lly 签名区域左下角y坐标
	 * @param urx 签名区域右上角x坐标
	 * @param ury 签名区域右上角y坐标
	 * @param pageNo 签名页码
	 * @throws Exception
	 */
	public void sign(String inFile, String outFile, String keyFile, String imgFile, float llx, float lly, float urx, float ury, int pageNo) throws Exception{
		KeyStore ks = KeyStore.getInstance("pkcs12");
		ks.load(new FileInputStream(keyFile),"123456".toCharArray());
		String alias = (String) ks.aliases().nextElement();
		PrivateKey key = (PrivateKey) ks.getKey(alias, "123456".toCharArray());
		Certificate[] chain = ks.getCertificateChain(alias);
		PdfReader reader = new PdfReader(inFile); // 输入流
		FileOutputStream fout = new FileOutputStream(outFile);// 输出流
		PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
		//				PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
		PdfSignatureAppearance sap = stp.getSignatureAppearance();
		sap.setCrypto(key, chain, null, PdfSignatureAppearance.VERISIGN_SIGNED);
		sap.setReason("this is the reason");
		sap.setLocation("this is the location");
		sap.setContact("http://www.bluecore.com/");
		Image image = Image.getInstance(imgFile);
		sap.setSignatureGraphic(image);		
		//				sap.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
		sap.setAcro6Layers(true);
		sap.setRenderingMode(RenderingMode.GRAPHIC);				
		sap.setVisibleSignature(new Rectangle(llx, lly, urx, ury), pageNo, null);
		//		stp.getWriter().setCompressionLevel(5);
		//添加文字
		PdfContentByte overContent = stp.getOverContent(pageNo);
		BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		font = BaseFont.createFont("C:/Windows/Fonts/SIMHEI.TTF",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		overContent.beginText();
		overContent.setFontAndSize(font, 11);
		overContent.setTextMatrix(200, 200);
		String now = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm:ss");
		overContent.showTextAligned(Element.ALIGN_CENTER,now,llx-80,lly,0);
		overContent.endText();
		if (stp != null) {
			stp.close();
		}
		if (fout != null) {
			fout.close();
		}
		if (reader != null) {
			reader.close();
		}
	}

	public class PDFCoordinate
	{
		// 定义返回值
		private  float[] resu = null;
		// 定义返回页码
		private  int i = 1;

		/**
		 * <p>返回关键字所在的坐标和页数 float[0]：X；float[1]：Y；float[2]：page</p>
		 * <p>按行读取，粗体字按字读取</p>
		 */
		public float[] getKeyWords(String filePath, final String keyWord)
		{
			try
			{
				PdfReader pdfReader = new PdfReader(filePath);
				int pageNum = pdfReader.getNumberOfPages();
				PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
				// 下标从1开始
				for (i = 1; i <= pageNum; i++)
				{
					pdfReaderContentParser.processContent(i, new RenderListener()
					{
						@Override
						public void renderText(TextRenderInfo textRenderInfo)
						{
							// 按行读取，粗体字、符号单独读取
							String text=textRenderInfo.getText();
							if (null != text && text.contains(keyWord))
							{
								java.awt.geom.Rectangle2D.Float boundingRectange = textRenderInfo
										.getBaseline().getBoundingRectange();
								resu = new float[3];
								resu[0] = boundingRectange.x;
								resu[1] = boundingRectange.y;
								resu[2] = i;
								return;
							}
						}

						@Override
						public void renderImage(ImageRenderInfo arg0)
						{
							// TODO Auto-generated method stub

						}

						@Override
						public void endTextBlock()
						{
							// TODO Auto-generated method stub

						}

						@Override
						public void beginTextBlock()
						{
							// TODO Auto-generated method stub

						}
					});
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resu;
		}
	}

	/**
	 * <p>Timestamp->String</p>
	 * @param ts
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);// yyyy/MM/dd HH:mm:ss
		try {
			//方法一
			tsStr = sdf.format(ts);
			//方法二
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * 打开患者签名界面
	 */
	public void onPatSign(){
		if(this.isOpen){
			this.messageBox("患者签名界面已经打开");
			return;
		}
		if(patSignParm == null){
			return;
		}
		this.openWindow("%ROOT%\\config\\emr\\EMRPatSign.x", patSignParm);	
		isOpen = true;
		this.changeScreen();
		this.isOpen = true;	
	}



}  