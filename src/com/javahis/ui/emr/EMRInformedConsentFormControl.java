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
 * <p>����֪��ͬ����ǩ��</p>
 * 
 * @author WangQing 20170502
 *
 */
public class EMRInformedConsentFormControl extends TControl {
	/**
	 * ǩ�������б�
	 */
	private TTable table;
	/**
	 * ������ʾpdf
	 */
	private TPanel pdfPanel;	
	/**
	 * �����̣߳�����pdf�Ƿ���ɺϲ���
	 */
	PDFThread3 thread3;
	/**
	 * swt�̣߳�ҽ����
	 */
	SWTThread swThread;
	/**
	 * swt�̣߳�������
	 */
	SWTThread2 swThread2;

	/**
	 * ����ϲ��߳� add by wangqing 20171018
	 */
	private PDFThread4 thread4;

	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * סԺ�����
	 */
	private String caseNo;
	/**
	 * pdf����·��1
	 */
	private static String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	/**
	 * pdf����·��2��ǩ��ר�ã�
	 */
	private static String signTempPath = "C:\\JavaHisFile\\sign\\temp\\pdf";
	/**
	 * ǩ��ͼƬ����·��
	 */
	private static String picTempPath = "C:\\JavaHisFile\\sign\\temp\\picture";
	
	/**
	 * ��ʶ�ļ�·������ʶǩ��ͼƬ�Ƿ��Ѿ���ȫ���ɣ�
	 */
	private static String txtTempPath = "C:\\JavaHisFile\\sign\\temp\\txt";
	
	/**
	 * ǩ���ܳ�
	 */
	private static String keyFile = "C:\\JavaHisFile\\sign\\demo.p12";
	/**
	 * ����ǩ��jar·��
	 */
	private static String jarFile = "C:\\JavaHisFile\\sign\\signature.jar";
	/**
	 * tableѡ����
	 */
	private int selectRow = -1;
	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * �����رռ����̵߳���ѭ��
	 */
	private boolean closeFlg  = false;

	/**
	 * �״�ǩ���ؼ���
	 */
	final static String KEY_WORD = "ע���벡���Ĺ�ϵ";// add by wangqing 20170926 ��������ǩ������
	/**
	 * ����ǩ���ؼ���
	 */
	final static String KEY_WORD_TWO = "ע���Ͳ����Ĺ�ϵ";// add by wangqing 20170926 ��������ǩ������

	private static String CLOSE_ACTION = "CLOSE_ACTION";

	private static String OPEN_URL_ACTION = "OPEN_URL_ACTION";

	/**
	 * Ϊ����ǩ�����洫����
	 */
	private TParm patSignParm;

	/**
	 * ���߽����Ƿ��Ѿ���
	 */
	private boolean isOpen = false;

	/**
	 * ϵͳ����
	 */
	private TParm sysParm;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		// ��ȡϵͳ����
		Object obj = this.getParameter();
		if(obj != null && obj instanceof TParm){
			sysParm= (TParm) obj;
		}else{
			this.messageBox("ϵͳ��������");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
		// ��ʼ������
		this.setValue("MR_NO", sysParm.getValue("MR_NO"));
		this.mrNo = sysParm.getValue("MR_NO");
		this.caseNo = sysParm.getValue("CASE_NO");
		// ��ȡ�ؼ�
		table = (TTable) this.getComponent("TABLE");
		pdfPanel = (TPanel) this.getComponent("tPanel_12");
		pdfPanel.setLayout(new BorderLayout());// ���ò��ַ�ʽΪBorderLayout��Ϊ��Ƕ��SWT�ؼ�
		
		patSignParm = new TParm();
		patSignParm.setData("pdfFile", "http://www.baidu.com");
		patSignParm.setData("canSign", false);
		patSignParm.setData("imgFile", "");
		patSignParm.setData("patientSignContent", "");	
		
		// ���Ӽ����¼�
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");// ˫��
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");// ����
		// ��һ�κϲ�PDF�Ƚ��������Գ�ʼ��ʱ������ϲ�һ��
		thread4 = new PDFThread4();
		thread4.start();
		
		final TFrame frame = (TFrame) getComponent();
		frame.setPreferredSize(new Dimension((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
				(int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
		final Canvas canvas = new Canvas();
		pdfPanel.add(canvas, BorderLayout.CENTER );			
		frame.pack();//Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
		
		// ��ѯ������Ϣ��ǩ��������Ϣ
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {	
				onMrNo();
				onQuery();
			}
		});	
		// ��ʼ��PDF����
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {							
				swThread = new SWTThread("http://www.baidu.com", canvas);
				swThread.start();	
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ĭ�����
			}
		});
		// �ر�С����
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
	 * �����Żس��¼�
	 */
	public void onMrNo(){
		// �ϲ�������start
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
				this.mrNo = pat.getMrNo();
			}
		}
		// �ϲ�������end
		String sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
				+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
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
			this.messageBox("�޴˲�����Ϣ");
			return;
		}
		System.out.println("--------result:"+result);
		setValueForParm("MR_NO;PAT_NAME;IPD_NO;SEX_CODE;DEPT_CODE;STATION_CODE;BED_NO_DESC;"
				+ "SERVICE_LEVEL;WEIGHT;VS_DR_CODE;IN_DATE;TOTAL_AMT;PAY_INS;TOTAL_BILPAY;GREENPATH_VALUE;CUR_AMT", result, 0);
	}

	/**
	 * ǩ��ͬ�����б�
	 */
	public void onQuery(){	
		String sql = "SELECT E.FILE_SEQ, E.CASE_NO, E.MR_NO, "
				+ " E.FILE_NAME,E.FILE_PATH, E.PDF_CREATOR_DATE AS CREATE_TIME, "
				+ " E.IS_PATIENT_SIGN_FLG AS SIGN_STATUS, F.PATIENT_SIGN_FLG "
				+ " ,E.IS_PATIENT_SIGN_TWO_FLG AS SIGN_TWO_STATUS, F.PATIENT_SIGN_TWO_FLG "// add by wangqing 20170926 ��������ǩ������
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
	 * table�����¼�
	 */
	public void onTableClicked(int row){
		this.selectRow = row;	
		TParm rowParm = table.getParmValue().getRow(selectRow);
		String fileName = rowParm.getValue("FILE_NAME");// �ļ���
		String filePath = rowParm.getValue("FILE_PATH");// �ļ�������·��
		System.out.println("======//////filePath="+filePath);
	}

	/**
	 * TABLE˫���¼�
	 * @param row
	 * @throws IOException
	 */
	public void onTableDoubled(int row) throws IOException{ 
		if(!isOpen){
			this.messageBox("�����ֶ��򿪻���ǩ������");
			return;
		}		
		this.selectRow = row;		
		TParm rowParm = table.getParmValue().getRow(row);
		String fileName = rowParm.getValue("FILE_NAME");// �ļ�����
		boolean hasFirstSign = rowParm.getValue("SIGN_STATUS").trim().equals("Y")?true:false;// �����״�ǩ��
		boolean canSignAgain = rowParm.getValue("PATIENT_SIGN_TWO_FLG").trim().equals("Y")?true:false;// ���߿ɷ���Զ���ǩ��
		boolean hasSecondSign = rowParm.getValue("SIGN_TWO_STATUS").trim().equals("Y")?true:false;// ���޶���ǩ��
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		String now = format.format(date);
		String inFile = signTempPath + "\\" + rowParm.getData("FILE_NAME") + "_" + now + ".pdf";
		String outFile = signTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now + ".pdf";
		String imgFile = picTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now +".png"; 		
		String flgFile = txtTempPath +"\\" + rowParm.getData("FILE_NAME") + "_sign_" + now +".txt";// ��ʶǩ��ͼƬ�Ƿ��Ѿ���ȫ����
		
		// FileServer�ļ�����·��
		String filePath = getEmrDataDir(fileName) + "\\"
				+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
				+ fileName + ".pdf";
		// ��ȡ�ļ�
		byte[] data = TIOM_FileServer.readFile(getFileServerAddress(fileName), filePath);
		if (data == null) {// ��������û���ҵ��ļ�
			if(hasFirstSign){// �Ѿ�ǩ����û���ҵ�ǩ���ļ�
				this.messageBox("û���ҵ�ǩ���ļ�������");
				return;
			}			
			// ����pdf
			TParm pdfParm = new TParm();// FILE_PATH;FILE_NAME;FILE_SEQ;MR_NO;CASE_NO
			pdfParm.setData("FILE_PATH", rowParm.getValue("FILE_PATH"));
			pdfParm.setData("FILE_NAME", rowParm.getValue("FILE_NAME"));
			pdfParm.setData("FILE_SEQ", rowParm.getValue("FILE_SEQ"));
			pdfParm.setData("MR_NO", rowParm.getValue("MR_NO"));
			pdfParm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
			// ִ�д�ӡ
			if(!onNewPdf(pdfParm)){ 
				return;
			}
			data = TIOM_FileServer.readFile(getFileServerAddress(fileName), filePath);
			onQuery();// ���´���pdfʱ��
		}	
		// ����������
		try {	
			File fDir = new File(signTempPath);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			FileTool.setByte(inFile, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ҽ�����������pdf
		swThread.openUrl(inFile);			
		// ǩ�ְ�����pdf
		// add by wangqing 20170926 start ��������ǩ������
		TWord word = new TWord();
		word.onOpen(rowParm.getValue("FILE_PATH"), rowParm.getValue("FILE_NAME") + ".jhw", 3, true);
		String patientSignContent = this.getEFixedValue(word, "PATIENT_SIGN_CONTENT");// �����״�ǩ���ճ�����
		String patientSignTwoContent = this.getEFixedValue(word, "PATIENT_SIGN_TWO_CONTENT");// ��������ǩ���ճ�����
		if(patientSignContent == null){
			patientSignContent = "";
		}
		if(patientSignTwoContent == null){
			patientSignTwoContent = "";
		}
		if(hasFirstSign){// ���״�ǩ��
			if(canSignAgain){// ���Զ���ǩ��
				if(hasSecondSign){// �ж���ǩ��
					patSignParm.setData("pdfFile", inFile);
					patSignParm.setData("canSign", false);	
					try {
						patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}else{// û�ж���ǩ��				
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
					// ��ȡǩ�������ҳ��		
					PDFCoordinate p = new PDFCoordinate();
					float[] f = p.getKeyWords(inFile, KEY_WORD_TWO);
					if(f==null){
						return;
					}
					// ��ʼ����
					thread3 = new PDFThread3(inFile, outFile, keyFile, imgFile, flgFile, f[0]+200, f[1]-100, f[0]+500, f[1], (int)f[2], false);
					thread3.start();
				}
			}else{// �����Զ���ǩ��
				patSignParm.setData("pdfFile", inFile);
				patSignParm.setData("canSign", false);	
				try {
					patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
				} catch (Exception ee) {
					ee.printStackTrace();
				}					
			}
		}else{// û���״�ǩ��
			patSignParm.setData("pdfFile", inFile);
			patSignParm.setData("canSign", true);
			patSignParm.setData("imgFile", imgFile);
			patSignParm.setData("flgFile", flgFile);// ��ʶ�ļ�
			patSignParm.setData("patientSignContent", patientSignContent);
			try {
				patSignParm.runListener(OPEN_URL_ACTION, new Object[]{"0"});
			} catch (Exception ee) {
				ee.printStackTrace();
			}				
			// ��ȡǩ�������ҳ��		
			PDFCoordinate p = new PDFCoordinate();
			float[] f = p.getKeyWords(inFile, KEY_WORD);
			if(f==null){
				return;
			}
			// ��ʼ����
			thread3 = new PDFThread3(inFile, outFile, keyFile, imgFile, flgFile, f[0]+200, f[1]-100, f[0]+500, f[1], (int)f[2], true);
			thread3.start();
		}
		// add by wangqing 20170926 end
	}

	/**
	 * SwingǶ��SWT
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
			shell = SWT_AWT.new_Shell(display, canvas);// SwingǶ��SWT
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

	/**
	 * ��SWT
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
		 * �Ƿ����ǩ��
		 */
		private boolean canSign;
		String outFile;
		String keyFile;
		String imgFile;
		String flgFile;
		private String patientSignContent;
		String keyWord;// add by wangqing 20170926 �������߶���ǩ������

		public SWTThread2(String url, String outFile, String keyFile, String imgFile, String flgFile, boolean canSign, String patientSignContent, String keyWord){
			this.url  = url;
			this.outFile = outFile;
			this.keyFile = keyFile;
			this.imgFile = imgFile;
			this.canSign = canSign;	
			this.patientSignContent = patientSignContent;
			this.keyWord = keyWord;// add by wangqing 20170926  �������߶���ǩ������
		}

		public void run(){		
			display = new Display();
			shell = new Shell(display);
			shell.setLayout( new FillLayout() );
			shell.setText("����ǩ��");
			shell.open();				
			shell.setMaximized(true);// ���
			shell.addShellListener(new ShellAdapter() {
				public void shellActivated(ShellEvent e) {
					System.out.println("Shell has been activated\n");
				}
				public void shellClosed(ShellEvent e) {
					System.out.println("Shell has been closed");
					//					MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					//					mb.setText("��ʾ");
					//					mb.setMessage("ȷ��Ҫ�ر���?");
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
			// ���Menu
			Menu menu = new Menu(shell, SWT.BAR);
			shell.setMenuBar(menu);
			item = new MenuItem(menu, SWT.PUSH);			
			item.setText("ǩ��");
			item.setEnabled(false);
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {					
					System.out.println("Action Performed");
					try {
						// modified by wangqing 20170926 �������߶���ǩ������
						String cmd = "cmd /c java -jar "+jarFile+" "+url+" "+outFile+" "+keyFile+" "+imgFile+" "+keyWord+" "+flgFile+" "+patientSignContent;
						System.out.println("===cmd:"+cmd);
						Runtime.getRuntime().exec(cmd);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			});
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
	 * <p>�����߳�</p>
	 * 
	 * <p>���������Ƿ����ǩ����������ǩ����ǩ��ͼƬ�Ѿ����ɣ����ϲ�pdf���ϴ�pdf</p>
	 * 
	 * @author wangqing
	 *
	 */
	private class PDFThread3 extends Thread{
		/**
		 * �ϲ�ǰPDF
		 */
		private String inFile;
		/**
		 * ǩ��ͼƬ
		 */
		private String imgFile;
		/**
		 * �ϲ���PDF
		 */
		private String outFile;
		/**
		 * ǩ���ܳ�
		 */
		private String keyFile;
		/**
		 * ��ʶ�ļ�
		 */
		private String flgFile;
		
		/**
		 * ǩ���������½�x����
		 */
		private float llx;
		/**
		 * ǩ���������½�y����
		 */
		private float lly;
		/**
		 * ǩ���������Ͻ�x����
		 */
		private float urx;
		/**
		 * ǩ���������Ͻ�y����
		 */
		private float ury;
		/**
		 * ǩ��ҳ��
		 */
		private int pageNo;
		/**
		 * �Ƿ��״�ǩ��
		 */
		private boolean isFirst = false;// add by wangqing 20170926 ��������ǩ������

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
			final File file = new File(flgFile);// ǩ��ͼƬ
			while(!file.exists() && !closeFlg);
			if(closeFlg)return;
			// �ϲ�PDF
			try {
				sign(inFile, outFile, keyFile, imgFile, llx, lly, urx, ury, pageNo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//			// ��ǰ�߳�˯0.5��
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			//�ϴ�PDF
			TParm rowParm = table.getParmValue().getRow(selectRow);
			String fileName = rowParm.getValue("FILE_NAME");// �ļ���
			String filePath = rowParm.getValue("FILE_PATH");// �ļ�������·��			
			String srcFilePath = outFile;// �����ļ�·��
			String outFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF") + "\\" + fileName + ".pdf";// �������ļ�·��	
			if(!onUpdate(srcFilePath, outFilePath)){
				messageBox("pdf�ϴ�ʧ�ܣ�����");
				return;
			}
			//			// ��ǰ�߳�˯0.5��
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			// �ϴ������ļ�
			if(isFirst){
				//				String outBackFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF") + "\\" + fileName + "_back.pdf";	
				String outBackFilePath = getEmrDataDir(fileName) + "\\" + filePath.replaceFirst("JHW", "PDF_BACK") + "\\" + fileName + "_back.pdf";
				if(!onUpdate(srcFilePath, outBackFilePath)){
					messageBox("����pdf�ϴ�ʧ�ܣ�����");
					return;
				}
			}	
			//			// ��ǰ�߳�˯0.5��
			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			// �ϴ��ɹ������EMR_FILE_INDEX��ǩ��״̬
			if(isFirst){
				//����EMR_FILE_INDEX.IS_PATIENT_SIGN_FLG
				String sql = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_FLG ='Y' WHERE CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"' AND FILE_NAME='"+fileName+"'";
				TParm result = new TParm(TJDODBTool.getInstance().update(sql));
				if(result.getErrCode()<0){
					return;
				}
			}else{
				//����EMR_FILE_INDEX.IS_PATIENT_SIGN_TWO_FLG
				String sql = "UPDATE EMR_FILE_INDEX SET IS_PATIENT_SIGN_TWO_FLG ='Y' WHERE CASE_NO='"+caseNo+"' AND MR_NO='"+mrNo+"' AND FILE_NAME='"+fileName+"'";
				TParm result = new TParm(TJDODBTool.getInstance().update(sql));
				if(result.getErrCode()<0){;
				return;
				}
			}
			// ��UI�߳�ˢ���б�������ִ��˫�����¼�		
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
	 * ����ϲ�PDF�߳�
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
	 * ��ǩ
	 */
	public void reSign(){
		if(table.getSelectedRow()<0){
			this.messageBox("��ѡ��һ����ǩ�����ݣ�����");
			return;
		}
		TParm rowParm = table.getParmValue().getRow(table.getSelectedRow());
		// add by wangqing 20170926 ��������ǩ������
		String fileName = rowParm.getValue("FILE_NAME");// �ļ�����
		String filePath = rowParm.getValue("FILE_PATH");
		boolean isSign = rowParm.getValue("SIGN_STATUS").trim().equals("Y")?true:false;// �����״�ǩ��
		boolean canSignTwo = rowParm.getValue("PATIENT_SIGN_TWO_FLG").trim().equals("Y")?true:false;// ���߿ɷ����ǩ��
		boolean isSignTwo = rowParm.getValue("SIGN_TWO_STATUS").trim().equals("Y")?true:false;// ���޶���ǩ��
		if(!isSign){
			this.messageBox("�˲�����δǩ��������");
			return;
		}
		if(isSignTwo){// �Ƿ��ж���ǩ��
			// FileServer�ļ�����·��
			//			String backFilePath = getEmrDataDir(fileName) + "\\"
			//					+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
			//					+ fileName + "_back.pdf";
			String backFilePath = getEmrDataDir(fileName) + "\\"
					+ rowParm.getValue("FILE_PATH").replaceFirst("JHW", "PDF_BACK") + "\\"
					+ fileName + "_back.pdf";
			byte[] data = TIOM_FileServer.readFile(getFileServerAddress(fileName), backFilePath);
			// ����������
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
			// �ϴ���FileServer������
			String outFilePath = getEmrDataDir(fileName) + "\\"
					+ filePath.replaceFirst("JHW", "PDF") + "\\"
					+ fileName + ".pdf";
			if(!onUpdate(backFile, outFilePath)){
				messageBox("pdf�ϴ�ʧ�ܣ�����");
				return;
			}
			//����EMR_FILE_INDEX.IS_PATIENT_SIGN_TWO_FLG
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
			// ִ�д�ӡ
			if(!onNewPdf(pdfParm)){ 
				this.messageBox("����pdfʧ��");
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
	 * ͨ���ļ�����ȷ���ļ�·��
	 * @param fileName
	 * @return
	 */
	public String getEmrDataDir(String fileName){
		//
		String strEmrDataDir=TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrData");
		// 1.�ļ������ǿմ������
		if (fileName != null && !fileName.equals("")) {
			// 2.����-�����
			if (fileName.indexOf("_") != -1) {
				// 3.ȡ��һ��caseNo�µ����ǰ2λ�����
				String sYear = fileName.substring(0, 2);
				//System.out.println("---sYear��---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
				String root = config.getString("","FileServer." + sYear + ".Root");
				if (root != null && !root.equals("")) {
					// 4.��ָ���������ļ����������
					strEmrDataDir = TIOM_FileServer.getRoot(sYear)+ TIOM_FileServer.getPath("EmrData");
				}

			}
		}

		return strEmrDataDir;
	}
	
	/**
	 * ͨ���ļ�����ȷ�Ϸ�������ַ
	 * 
	 * @param fileName
	 * @return
	 */
	public TSocket getFileServerAddress(String fileName) {
		//Ĭ���ļ�����
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// 1.�ļ������ǿմ������
		if (fileName != null && !fileName.equals("")) {
			// 2.����-�����
			if (fileName.indexOf("_") != -1) {
				// 3.ȡ��һ��caseNo�µ����ǰ2λ�����
				String sYear = fileName.substring(0, 2);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
				String ip = config.getString("","FileServer." + sYear + ".IP");
				if (ip != null && !ip.equals("")) {
					// 4.��ָ���������ļ����������
					tsocket = TIOM_FileServer.getSocket(sYear);
				}

			}
		}  	
		return tsocket;
	}

	/**
	 * �л���Ļ
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
	 * �ϴ��ļ�
	 * @param srcFilePath Դ�ļ�·��
	 * @param outFilePath �����ļ�·��
	 * @return true �ϴ��ɹ���false �ϴ�ʧ��
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
	 * ɾ���ļ����ļ����е��ļ�����ɾ���ļ��У�
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(File file) throws IOException{
		boolean flg = false;
		if(!file.exists()){
			flg = false;
		}
		// �ļ�
		if(file.isFile()){
			flg = file.delete();
		}
		// ·��
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
				flg = deleteFile(temp);// ʹ�õݹ���ɾ��
				if(!flg){
					break;
				}
			}
			//			if(flg){
			//				flg = file.delete();// ɾ�����ļ���
			//			}
		}
		return flg;
	}

	/**
	 * ����PDF
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
		// Ĭ�ϳ�ʱʱ��Ϊ30s
		int timeOut = 30;
		String pdfTransTimeOut = TConfig.getSystemValue("PDF_TRANS_TIME_OUT");
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		if (StringUtils.isNotEmpty(pdfTransTimeOut)) {
			timeOut = Integer.parseInt(pdfTransTimeOut);
		}
		parm.setData("TEMP_PATH", tempPath);//��ʱ·��
		TWord word = new TWord();
		word.onOpen(parm.getValue("FILE_PATH"), parm.getValue("FILE_NAME") + ".jhw", 3, true);
		//$$ =========add by lx 2012/08/10 ����һ�����¾�ʼ���� Start===============$$//
		TParm sexP = new TParm(TJDODBTool.getInstance().select(
				"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
						+ parm.getValue("MR_NO") + "'"));
		//System.out.println("===MR_NO==="+parm.getValue("MR_NO"));
		if (sexP.getInt("SEX_CODE", 0) == 9) {
			word.setSexControl(0);
		} else {
			word.setSexControl(sexP.getInt("SEX_CODE", 0));
		}				
		//$$ =========add by lx 2012/08/10 ����һ�����¾�ʼ���� end===============$$//
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
		// ��ʼ����PDF����ʼʱ��
		beginDate = SystemTool.getInstance().getDate();
		completeFlg = false;

		do {
			// ���ɹ����м�����ʱ������ָ��ʱ�����϶�Ϊ�ļ�Ŀ¼�趨������ļ�������������ѭ��
			endDate = SystemTool.getInstance().getDate();
			diff = endDate.getTime() - beginDate.getTime();
			// ʵʱ����ʱ����������30s��Ϊʧ��
			time = (int) Math.floor(diff / (1000));
			if (time > timeOut) {
				this.messageBox("�ļ���" + parm.getValue("FILE_NAME")
				+ "�����ɴ����ļ������³�ʱ��PDF�洢·�����ô���");
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
	 * ����EMR�ļ�
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
	 * ��ȡ�̶��ı�ֵ
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// �̶��ı�
		if(f == null){ 
			System.out.println("word--->name�ؼ�������");
			return null;	
		}
		return f.getText();
	}

	/**
	 * �ϲ�PDF
	 * @param inFile �ϲ�ǰPDF
	 * @param outFile �ϲ���PDF
	 * @param keyFile �ܳ��ļ�
	 * @param imgFile ǩ��ͼƬ
	 * @param llx ǩ���������½�x����
	 * @param lly ǩ���������½�y����
	 * @param urx ǩ���������Ͻ�x����
	 * @param ury ǩ���������Ͻ�y����
	 * @param pageNo ǩ��ҳ��
	 * @throws Exception
	 */
	public void sign(String inFile, String outFile, String keyFile, String imgFile, float llx, float lly, float urx, float ury, int pageNo) throws Exception{
		KeyStore ks = KeyStore.getInstance("pkcs12");
		ks.load(new FileInputStream(keyFile),"123456".toCharArray());
		String alias = (String) ks.aliases().nextElement();
		PrivateKey key = (PrivateKey) ks.getKey(alias, "123456".toCharArray());
		Certificate[] chain = ks.getCertificateChain(alias);
		PdfReader reader = new PdfReader(inFile); // ������
		FileOutputStream fout = new FileOutputStream(outFile);// �����
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
		//�������
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
		// ���巵��ֵ
		private  float[] resu = null;
		// ���巵��ҳ��
		private  int i = 1;

		/**
		 * <p>���عؼ������ڵ������ҳ�� float[0]��X��float[1]��Y��float[2]��page</p>
		 * <p>���ж�ȡ�������ְ��ֶ�ȡ</p>
		 */
		public float[] getKeyWords(String filePath, final String keyWord)
		{
			try
			{
				PdfReader pdfReader = new PdfReader(filePath);
				int pageNum = pdfReader.getNumberOfPages();
				PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
				// �±��1��ʼ
				for (i = 1; i <= pageNum; i++)
				{
					pdfReaderContentParser.processContent(i, new RenderListener()
					{
						@Override
						public void renderText(TextRenderInfo textRenderInfo)
						{
							// ���ж�ȡ�������֡����ŵ�����ȡ
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
			//����һ
			tsStr = sdf.format(ts);
			//������
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * �򿪻���ǩ������
	 */
	public void onPatSign(){
		if(this.isOpen){
			this.messageBox("����ǩ�������Ѿ���");
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