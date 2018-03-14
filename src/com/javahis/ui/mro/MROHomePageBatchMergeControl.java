package com.javahis.ui.mro;

import java.awt.Color;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import jdo.mro.MROTool;
import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.javahis.util.FileUtils;

/**
 * <p>
 * Title: ������ҳ�����ϲ�
 * </p>
 * 
 * <p>
 * Description: ������ҳ�����ϲ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.6.29
 * @version 1.0
 */
public class MROHomePageBatchMergeControl extends TControl {
	
	/** ������� */
	private TParm parameter;
	private String tempPath ="C:\\JavaHisFile\\temp\\pdf";
	private TParm homePageParm;// �Ѵ�ӡ������ҳ���ݼ���
	private TButton uploadButton;
	private TLabel label;
	private List<String> errorCaseNoList;

	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		if (obj != null && obj instanceof TParm) {
			parameter = (TParm) obj;
			uploadButton = (TButton) this.getComponent("UPLOAD_BUTTON");
			label = (TLabel) this.getComponent("LABEL");
			errorCaseNoList = new ArrayList<String>();
			
			File fDir = new File(tempPath);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			
			// ɾ����ʱ�ļ����ļ�
			FileUtils.getInstance().delAllFile(tempPath);
			
			int count = parameter.getCount();
			if (count < 1) {
				this.closeWindow();
				return;
			}
			label.setValue("�ܼƣ�" + count);
			String caseNo = "";
			
			for (int i = 0; i < count; i++) {
				caseNo = caseNo + "'" + parameter.getValue("CASE_NO", i) + "'";
				if (i < count - 1) {
					caseNo = caseNo + ",";
				}
			}
			
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO_LIST", caseNo);
			// ��ѯ������ҳ����
			homePageParm = MROTool.getInstance().queryHomePageInfo(queryParm);
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					// ������������ҳ
					batchDealWithEMRFile();
					label.setValue("�ܼƣ�" + parameter.getCount() + "  ʧ�ܣ�"
							+ errorCaseNoList.size());
				}
			});

			JPanel contentPane = (JPanel)this.getComponent("PANEL");
			contentPane.setBorder(new EmptyBorder(20, 5, 20, 30));
			contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
			final JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setForeground(Color.BLUE);
			
			new Thread() {
				public void run() {
					int count = homePageParm.getCount();
					int totalCount = 100;
					boolean flg = false;
					File file;
					for (int i = 0; i < count; i++) {
						try {
							flg = false;
							while(!flg) {
								file = new File(tempPath + "\\"
										+ homePageParm.getValue("CASE_NO", i) + ".pdf");
								if (file.exists() || errorCaseNoList.contains(homePageParm.getValue("CASE_NO", i))) {
									flg = true;
									progressBar.setValue((totalCount/count) * (i+1));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (count > 0) {
						progressBar.setValue(totalCount);
						progressBar.setString("�ϲ���ɣ�");
						uploadButton.setEnabled(true);
					}
				}
			}.start();
			contentPane.add(progressBar);
		} else {
			return;
		}
	}
	
	/**
	 * ����������
	 */
	private void batchDealWithEMRFile() {
//		int count = parameter.getCount();
		int homePageCount = homePageParm.getCount();
		List<String> caseNoList = new ArrayList<String>();
		StringBuffer errMsg = new StringBuffer();
		
		/*// δ��ӡ������ҳ�����ݸ�����ʾ
		boolean homePagePrintFlg = false;
		for (int i = 0; i < count; i++) {
			homePagePrintFlg = false;
			for (int n = 0; n < homePageCount; n++) {
				if (parameter.getValue("CASE_NO", i).equals(homePageParm.getValue("CASE_NO", n))) {
					homePagePrintFlg = true;
					break;
				}
			}
			
			if (!homePagePrintFlg) {
				errMsg.append("�����š�" + parameter.getValue("MR_NO", i) + "����δ��ӡ������ҳ\r\n");
				if (!errorCaseNoList.contains(parameter.getValue("CASE_NO", i))) {
					errorCaseNoList.add(parameter.getValue("CASE_NO", i));
				}
			}
		}*/
		
		// ������ʽ����
		String downLoadMsg = onDonwLoadPDF();
		if (StringUtils.isNotEmpty(downLoadMsg)) {
			errMsg.append(downLoadMsg);
		}
		
		Date beginDate;
		Date endDate;
		// Ĭ�ϳ�ʱʱ��Ϊ60s
		int timeOut = 60;
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		File file;
		String uploadMsg = "";
		
		// ��������ҳjhwת����pdf
		for (int j = 0 ; j < homePageCount; j++) {
			if (!caseNoList.contains(homePageParm.getValue("CASE_NO", j))) {
				caseNoList.add(homePageParm.getValue("CASE_NO", j));
				try {
					// ��ҳ��ӡ������ҳ
					onPrintIndex(homePageParm.getRow(j));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				// �ϴ�����
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
						errMsg.append("�����š�"+ homePageParm.getValue("MR_NO", j) +"���Ĳ�����ҳ�ļ����ɴ����ļ������³�ʱ��PDF�洢·�����ô���\r\n");
						if (!errorCaseNoList.contains(homePageParm.getValue("CASE_NO", j))) {
							errorCaseNoList.add(homePageParm.getValue("CASE_NO", j));
						}
						break;
					}
					file = new File(tempPath + "\\" + "0"
							+ homePageParm.getValue("CASE_NO", j) + ".pdf");
					if (file.exists()) {
						completeFlg = true;
						// �ϴ�������ҳ����
						uploadMsg = onUpLoadHomePagePdf(homePageParm.getRow(j));
						if (StringUtils.isNotEmpty(uploadMsg)) {
							errMsg.append(uploadMsg);
						}
					}
				} while (!completeFlg);
			}
		}
		
		if (StringUtils.isNotEmpty(errMsg.toString())) {
			this.messageBox(errMsg.toString());
		}
		
		// �ϲ�����
		this.mergeAllPdf();
	}
	
	/**
	 * ��ҳ��ӡ������ҳ
	 * 
	 * @param parm
	 */
	private void onPrintIndex(TParm parm) {
		TWord word = new TWord();
		word.onOpen(parm.getValue("FILE_PATH"), parm
				.getValue("FILE_NAME")
				+ ".jhw", 3, true);
		word.getPageManager().setOrientation(1);
		word.getPageManager().print(PrinterJob.getPrinterJob(),
				"0" + parm.getValue("FILE_NAME").split("_")[0]);
	}
	
	/**
	 * �ϴ�������ҳ����
	 * 
	 * @param parm
	 */
	private String onUpLoadHomePagePdf(TParm parm) {
		String severRoot = TConfig.getSystemValue("FileServer.Main.Root");
		String msg = "";
		
		String fileName = parm.getValue("FILE_NAME");
		String filePath = severRoot + "\\" + TConfig.getSystemValue("EmrData")
				+ "\\"
				+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF")
				+ "\\" + fileName + ".pdf";
		try {
			TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
					filePath);
			File file = new File(tempPath + File.separator + "0" + parm.getValue("CASE_NO") + ".pdf");
			byte[] data = FileTool.getByte(file);
			if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					filePath, data)) {
				msg = "�����š�" + parm.getValue("MR_NO") + "��������ҳ�ϴ�ʧ��\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * ������ʽ����
	 * 
	 * @return ��ʾ��Ϣ
	 */
	private String onDonwLoadPDF() {
		int count = homePageParm.getCount();
		String severRoot = TConfig.getSystemValue("FileServer.Main.Root");
		String bigFilePath = "";
		byte[] data = null;
		StringBuffer errMsg = new StringBuffer();

		for (int i = 0; i < count; i++) {
			try {
				bigFilePath = severRoot + "\\��ʽ����\\"
						+ homePageParm.getValue("MR_NO", i).substring(0, 7) + "\\"
						+ homePageParm.getValue("MR_NO", i) + "\\"
						+ homePageParm.getValue("CASE_NO", i) + ".pdf";

				data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
						bigFilePath);
				if (null == data) {
					errMsg.append("�����š�" + homePageParm.getValue("MR_NO", i)
							+ "����δ�ύ��������PDF\r\n");
					if (!errorCaseNoList.contains(homePageParm.getValue("CASE_NO", i))) {
						errorCaseNoList.add(homePageParm.getValue("CASE_NO", i));
					}
				} else {
					FileTool.setByte(tempPath + "\\" + 1
							+ homePageParm.getValue("CASE_NO", i) + ".pdf", data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return errMsg.toString();
	}
	
	/**
	 * �ϲ�����
	 */
	private void mergeAllPdf() {
		File file = new File(tempPath + "");

		if (!file.exists()) {
			file.mkdirs();
		}
		// ����ִ���ļ�
		byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox("��������û���ҵ��ļ� " + PdfTool.getInstance().getRoot()
					+ "\\pdftk");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\pdftk.exe", data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		int count = homePageParm.getCount();
		for (int i = 0; i < count; i++) {
			sb.append("pdftk.exe ");
			sb.append(0 + homePageParm.getValue("CASE_NO", i) + ".pdf ");
			sb.append(1 + homePageParm.getValue("CASE_NO", i) + ".pdf ");
			sb.append(" cat output ");
			sb.append(homePageParm.getValue("CASE_NO", i) + ".pdf \r\n");
		}
		// �����������ļ�
		String s = tempPath.substring(0, 2) + "\r\n" + "cd " + tempPath
				+ "\r\n" + sb.toString() + " \r\n exit";
		try {
			FileTool.setByte(tempPath + "\\pdf.bat", s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// ִ���������ļ�
		PDFODITool tool = new PDFODITool();
		TParm p = new TParm(tool.exec(tempPath + "\\pdf.bat"));
		if (p.getErrCode() != 0) {
			messageBox(p.getErrText());
			System.out.println(p.getErrText());
			return;
		}
	}
	
	/**
	 * �ϴ��ϲ�����
	 */
	public void onUpload() {
		String severRoot = TConfig.getSystemValue("FileServer.Main.Root");
		String[] fileArray = TIOM_FileServer.listFile(tempPath);
		int fileListCount = fileArray.length;
		int count = homePageParm.getCount();
		File file;
		byte[] data;
		String bigFilePath = "";
		StringBuffer sbMsg = new StringBuffer();
		TParm updateParm = new TParm();
		String optId = Operator.getID();
		String optIp = Operator.getIP();
		List<String> uploadCaseNolist = new ArrayList<String>();

		for (int i = 0; i < fileListCount; i++) {
			for (int j = 0; j < count; j++) {
				if (fileArray[i].equals(homePageParm.getValue("CASE_NO", j)
						+ ".pdf")) {
					try {
						file = new File(tempPath + File.separator
								+ fileArray[i]);
						data = FileTool.getByte(file);
						if (null != data) {
							bigFilePath = severRoot
									+ "\\��ʽ����\\"
									+ homePageParm.getValue("MR_NO", j)
											.substring(0, 7) + "\\"
									+ homePageParm.getValue("MR_NO", j) + "\\"
									+ homePageParm.getValue("CASE_NO", j)
									+ ".pdf";
							TIOM_FileServer.deleteFile(TIOM_FileServer
									.getSocket(), bigFilePath);
							if (!TIOM_FileServer.writeFile(TIOM_FileServer
									.getSocket(), bigFilePath, data)) {
								sbMsg.append("�����š�"
										+ homePageParm.getValue("MR_NO", j)
										+ "���ϲ������ϴ�ʧ��\r\n");
							} else {
								updateParm = new TParm();
								updateParm.setData("CASE_NO", homePageParm
										.getValue("CASE_NO", j));
								updateParm.setData("HP_MERGE_CODE", optId);
								updateParm.setData("HP_MERGE_DATE", "SYSDATE");
								updateParm.setData("OPT_USER", optId);
								updateParm.setData("OPT_TERM", optIp);
								// ���ºϲ�������ҳ��Ϣ
								updateParm = MROTool.getInstance()
										.updateMergeHomePageInfo(updateParm);

								if (updateParm.getErrCode() < 0) {
									sbMsg.append("�����š�"
											+ homePageParm.getValue("MR_NO", j)
											+ "�����ºϲ�������ҳ��Ϣʧ��\r\n");
								} else {
									uploadCaseNolist.add(homePageParm.getValue("MR_NO", j));
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
		TParm result = new TParm();
		if (uploadCaseNolist.size() == 0) {
			this.messageBox("���β����޲�����ҳ�ϲ��ɹ��������ϴ�ʧ��");
			this.closeWindow();
			return;
		} else if (uploadCaseNolist.size() > 0) {
			result.setData("FLG", "SUCCESS");
			this.setReturnValue(result);
			this.messageBox("�ϴ��ɹ�");
			this.closeWindow();
			return;
		}
	}
}
