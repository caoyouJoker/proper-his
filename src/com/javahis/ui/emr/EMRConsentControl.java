package com.javahis.ui.emr;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.io.File;
import java.sql.Timestamp;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.javahis.util.OdoUtil;
import jdo.sys.SystemTool;

/**
 * ����֪��ͬ�����б�
 * 
 * @author wangqing
 *
 */
public class EMRConsentControl extends TControl {
	/**
	 * ϵͳ����
	 */
	private TParm sysParm;
	/**
	 * סԺ�����
	 */
	private String caseNo;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ��������
	 */
	private String patName;
	/**
	 * �����Ա�
	 */
	private String patSex;
	/**
	 * ��������
	 */
	private String patAge;
	/**
	 * ֪��ͬ�����б�
	 */
	private TTable table;
	/**
	 * ������ʾpdf
	 */
	private TPanel pdfPanel;
	
	private static String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	
	private SWTThread swThread;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		//		this.messageBox("��ʼ��");
		Object object = this.getParameter();
		if(object != null && object instanceof TParm){
			sysParm = (TParm) object;
			caseNo = sysParm.getValue("CASE_NO");
			mrNo = sysParm.getValue("MR_NO");		
			ipdNo = sysParm.getValue("IPD_NO");
			System.out.println("//////sysParm="+sysParm);
			System.out.println("//////caseNo="+caseNo);
			System.out.println("//////mrNo="+mrNo);
			System.out.println("//////ipdNo="+ipdNo);
		}else{
			this.messageBox("ϵͳ��������");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
		table = (TTable) this.getComponent("TABLE");
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");// ˫��
		pdfPanel = (TPanel) this.getComponent("tPanel_12");
		pdfPanel.setLayout(new BorderLayout());// ���ò��ַ�ʽΪBorderLayout��Ϊ��Ƕ��SWT�ؼ�
		final TFrame frame = (TFrame) getComponent();
		frame.setPreferredSize(new Dimension((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, 
				(int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
		final Canvas canvas = new Canvas();
		pdfPanel.add(canvas, BorderLayout.CENTER );			
		frame.pack();//Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {	
				// ����������Ϣ
				String sql = " SELECT A.PAT_NAME, A.SEX_CODE AS PAT_SEX, A.BIRTH_DATE FROM SYS_PATINFO A WHERE A.MR_NO='"+mrNo+"' ";
				System.out.println("///sql="+sql);
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result==null || result.getErrCode()<0){
					messageBox("result==null || result.getErrCode()<0");
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							closeWindow();
						}
					});
					return;
				}
				if(result.getCount()<=0){
					messageBox("û�д˲���������");
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							closeWindow();
						}
					});
					return;
				}	
				patName = result.getValue("PAT_NAME", 0);
				patSex = result.getValue("PAT_SEX", 0);
				patAge = getAge(result.getTimestamp("BIRTH_DATE", 0));
				setValue("CASE_NO", caseNo);
				setValue("MR_NO", mrNo);
				setValue("IPD_NO", ipdNo);
				setValue("PAT_NAME", patName);
				setValue("PAT_SEX", patSex);
				setValue("PAT_AGE", patAge);
				// ֪��ͬ������Ϣ
				String sql1 = " SELECT A.CASE_NO, A.FILE_SEQ, A.MR_NO, A.IPD_NO, A.FILE_PATH, "
						+ "A.FILE_NAME, A.CLASS_CODE, A.SUBCLASS_CODE, A.IS_PATIENT_SIGN_FLG, A.IS_PATIENT_SIGN_TWO_FLG "
						+ "FROM EMR_FILE_INDEX A "
						+ "WHERE A.CASE_NO='"+caseNo
						+"' AND A.MR_NO='"+mrNo
						+"' AND A.IS_PATIENT_SIGN_FLG IS NOT NULL AND A.IS_PATIENT_SIGN_FLG='Y' ";
				TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
				if(result1==null || result1.getErrCode()<0){
					messageBox("result1==null || result1.getErrCode()<0");
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							closeWindow();
						}
					});
					return;
				}
				if(result1.getCount()<=0){
					messageBox("û��֪��ͬ��������");
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							closeWindow();
						}
					});
					return;
				}
				table.setParmValue(result1);	
				swThread = new SWTThread("http://www.baidu.com", canvas);
				swThread.start();	
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ĭ�����
			}
		});	
	}

	/**
	 * ��ȡ����
	 * @param birthDate
	 * @return
	 */
	public String getAge(Timestamp birthDate){
		//		Timestamp birthDate = TypeTool.getTimestamp(getValue("BIRTH_DATE"));
		String age = OdoUtil.showAge( birthDate,SystemTool.getInstance().getDate());
		return age;
	}

	/**
	 * ֪��ͬ�����б�˫���¼�
	 * @param row
	 */
	public void onTableDoubled(int row){
		//		this.messageBox("֪��ͬ�����б�˫���¼�");
		if(row<0){
			this.messageBox("û��ѡ����");
			return;
		}
		TParm parm = table.getParmValue();
		String fileName = parm.getValue("FILE_NAME", row);
		String filePath = parm.getValue("FILE_PATH", row);
		String fileAllPath = getEmrDataDir(fileName) + "\\"
				+ filePath.replaceFirst("JHW", "PDF") + "\\"
				+ fileName + ".pdf";
		byte[] data = TIOM_FileServer.readFile(getFileServerAddress(fileName), fileAllPath);
		if(data==null){
			this.messageBox("���������Ҳ����ļ�");
			return;
		}
		String inFile = tempPath + "\\" + fileName + ".pdf";
		// ����������
		try {	
			File fDir = new File(tempPath);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			FileTool.setByte(inFile, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		swThread.openUrl(inFile);			
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
		if(swThread != null){
			swThread.close();
		}
		return true;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
