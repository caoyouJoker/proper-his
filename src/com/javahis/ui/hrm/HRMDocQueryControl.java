package com.javahis.ui.hrm;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.pdf.jacobTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����ϵͳ������PDF���ӱ������
 * </p>
 * 
 * <p>
 * Description: ����ϵͳ������PDF���ӱ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.11.21
 * @version 1.0
 */
public class HRMDocQueryControl extends TControl {
	
	private String mrNo = ""; // ������
	private String caseNo = ""; // �����
	private TSocket socket;
	private String serverPath;
	private String fileServerRoot;
	private String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	private TParm docServerSortParm = null;
	private TTable docTable;
	/**
	 * �����
	 */
	private static int PDF_FLG_YSH = 2;
	/**
	 * ���ύ
	 */
	private static int PDF_FLG_YTJ = 1;
	/**
	 * δ�ύ
	 */
	private static int PDF_FLG_WTJ = -1;
	/**
	 * ����˻�
	 */
	private static int PDF_FLG_SHTH = -2;
	/**
	 * �鵵�˻�
	 */
	private static int PDF_FLG_GDTH = -3;
	/**
	 * �ѹ鵵
	 */
	private static int PDF_FLG_YGD = 3;
    private BILComparator compare = new BILComparator();//��������
    private boolean ascending = false;//�������� 
    private int sortColumn = -1;//�������� 
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		this.initPage();
		this.initTABLE();
		this.initServerDocTable();
	}
	
	/**
	 * ��ʼ��ҳ������
	 */
	private void initPage() {
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		TParm parm = (TParm) this.getParameter();
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("SEX", parm.getValue("SEX_CODE"));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));

		String sql = "SELECT E.CASE_NO, E.FILE_SEQ, E.MR_NO, "
				+ "   E.IPD_NO, E.FILE_PATH, E.FILE_NAME, "
				+ "   E.DESIGN_NAME, E.CLASS_CODE, E.SUBCLASS_CODE, "
				+ "   E.DISPOSAC_FLG, E.OPT_USER, E.OPT_DATE, "
				+ "   E.OPT_TERM, E.CREATOR_USER, E.CURRENT_USER, "
				+ "   E.CANPRINT_FLG, E.MODIFY_FLG, E.CREATOR_DATE, "
				+ "   E.CHK_USER1, E.CHK_DATE1, E.CHK_USER2, "
				+ "   E.CHK_DATE2, E.CHK_USER3, E.CHK_DATE3, "
				+ "   E.COMMIT_USER, E.COMMIT_DATE, E.IN_EXAMINE_USER, "
				+ "   E.IN_EXAMINE_DATE, E.DS_EXAMINE_USER, E.DS_EXAMINE_DATE, "
				+ "   E.PDF_CREATOR_USER, E.PDF_CREATOR_DATE, 'N' AS REPORT_FLG, "
				+ "   E.AUTOGRAPH_TIME, E.AUTOGRAPH_USER, E.AUTOGRAPH_IP, "
				+ "   E.AUTOGRAPH_STATE, E.AUTOGRAPH_KEY ,'JHW�鿴' AS JHW, DECODE(PDF_CREATOR_USER ,  NULL,'','PDF�鿴') AS PDF "
				+ "FROM JAVAHIS.EMR_FILE_INDEX E WHERE CASE_NO ='" + caseNo
				+ "' AND MR_NO='" + mrNo + "' ORDER BY FILE_SEQ";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("docTable");
		if (result.getCount() > 0) {
			for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("PDF", i).length() > 1
						&& result.getValue("OPT_DATE", i).compareTo(
								result.getValue("PDF_CREATOR_DATE", i)) > 0) {

					result.setData("PDF", i, result.getValue("PDF", i) + "(��)");
				}

			}
		}
		TParm check = new TParm();
		check.setData("MR_NO", mrNo);
		check.setData("CASE_NO", caseNo);
		int flg = PdfTool.getInstance().checkFlg(check);
		if (flg == PDF_FLG_WTJ) {
			((TLabel) this.getComponent("tip")).setText("δ�ύ");
		} else if (flg == PDF_FLG_YTJ) {
			((TLabel) this.getComponent("tip")).setText("���ύ");
		} else if (flg == PDF_FLG_SHTH) {
			((TLabel) this.getComponent("tip")).setText("����˻�");
		} else if (flg == PDF_FLG_YSH) {
			((TLabel) this.getComponent("tip")).setText("���ͨ��");
		} else if (flg == PDF_FLG_YGD) {
			((TLabel) this.getComponent("tip")).setText("�ѹ鵵");
		} else if (flg == PDF_FLG_GDTH) {
			((TLabel) this.getComponent("tip")).setText("�鵵�˻�");
		}
		docTable = this.getTTable("docTable");

		// add by wangb 2015/12/2 ���˵�ָ�������ļ� START
		String filterFileSql = "SELECT * FROM EMR_FILE_FILTER WHERE FILTER_TYPE='JHW' AND FILTER_FLG='Y'";
		TParm filterFileResult = new TParm(TJDODBTool.getInstance().select(
				filterFileSql));
		int count = filterFileResult.getCount();
		if (count > 0) {
			String filterList = filterFileResult.getValue("SUBCLASS_CODE");
			for (int i = result.getCount() - 1; i > -1; i--) {
				if (filterList.contains(result.getValue("SUBCLASS_CODE", i))) {
					result.removeRow(i);
				}
			}
		}
		// add by wangb 2015/12/2 ���˵�ָ�������ļ� END

		table.setParmValue(result);
		docServerSortParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT SEQ,FILE_TYPE FROM EMR_PDFLISTORDER ORDER BY SEQ "));
		
		socket = getFileServerAddress();
		serverPath = getEmrDataDir();
		fileServerRoot = TConfig.getSystemValue("FileServer.Main.Root");
	}
	
	/**
	 * ��ʼ��������������
	 */
	private void initTABLE() {
		String sql = "SELECT B.CAT1_TYPE, B.ISREAD, B.APPLICATION_NO, B.ORDER_CODE, B.ORDER_DESC,"
				+ "B.OPTITEM_CHN_DESC, B.ORDER_DATE, B.STATUS, B.ORDER_DR_CODE, B.RESERVED_DATE,"
				+ "B.REGISTER_DATE, B.INSPECT_DATE, B.EXAMINE_DATE, B.EXEC_DR_CODE, B.REPORT_DR,"
				+ "B.EXAMINE_DR, C.MR_NO, C.PAT_NAME,"
				+ "(CASE WHEN B.PDFRE_FLG = 'Y' THEN '������' ELSE '' END) AS PDFRE_FLG "
				+ "FROM HRM_PATADM A, MED_APPLY B, SYS_PATINFO C "
				+ "WHERE A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "AND A.MR_NO = '"
				+ mrNo
				+ "' "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.CASE_NO = B.CASE_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("TABLE");
		table.setParmValue(result);
	}

	/**
	 * TABLE���˫��
	 */
	public void tableDoubleClick() {
		TTable table = (TTable) this.getComponent("TABLE");
		int row = table.getSelectedRow();
		if (!isExist(table.getParmValue().getRow(row))) {
			this.messageBox("���鱨��״̬����δ���ɱ��档");
			return;
		}
		if (table.getValueAt(row, 0).equals("N")) {
			updateMedApplyFlg(table.getParmValue().getData("APPLICATION_NO",
					row).toString(), table.getParmValue().getData("CAT1_TYPE",
					row).toString());
		}
	}
	
	/**
	 * �ж��ļ��Ƿ����
	 * 
	 * @param parm
	 * @return
	 */
	private boolean isExist(TParm parm) {
		String type = parm.getValue("CAT1_TYPE").equalsIgnoreCase("LIS") ? "���鱨��"
				: "��鱨��";
		TParm serverDocParm = ((TTable) this.getComponent("serverDocTable"))
				.getParmValue();
		for (int i = 0; i < serverDocParm.getCount("FileName"); i++) {
			TParm serverDocRowParm = serverDocParm.getRow(i);
			// ��PDF��׺������ŷ�����Ϊ�������ַ���Ĭ��Ϊ�����ļ� 20120709 shibl modify
			if (serverDocRowParm.getValue("FileName")
					.contains(
							caseNo + "_" + type + "_"
									+ parm.getValue("APPLICATION_NO"))) {
				byte data[] = TIOM_FileServer.readFile(socket, serverPath
						+ "\\" + serverDocRowParm.getData("FileName"));

				if (data == null) {
					messageBox_("��������û���ҵ��ļ� " + serverPath + "\\"
							+ serverDocRowParm.getData("FileName"));
				}
				try {
					FileTool.setByte(tempPath + "\\"
							+ serverDocRowParm.getData("FileName"), data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Runtime runtime = Runtime.getRuntime();
				try {
					// ���ļ�
					runtime.exec("rundll32 url.dll FileProtocolHandler "
							+ tempPath + "\\"
							+ serverDocRowParm.getData("FileName"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ���
	 */
	public void onReader() {
		File file = new File(tempPath + "\\" + caseNo + ".pdf");
		if (!file.exists()) {
			messageBox_("��δ�������������� ");
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ʼ�������������
	 */
	private void initServerDocTable() {
		TTable jyjcTable = (TTable) this.getComponent("TABLE");
		addSortListener(jyjcTable);
		String s[] = TIOM_FileServer.listFile(socket, serverPath);
		TParm result = new TParm();
		int jyjcTableCount = jyjcTable.getParmValue().getCount("ORDER_CODE");
		if (s != null) {
			for (int i = 0; i < s.length; i++) {
				if(!s[i].startsWith(caseNo)){
					continue;
				}
				String[] sSplit = s[i].split("_");
				result.addData("FLG", "Y");
				result.addData("FileName", s[i]);
				result.addData("Type", sSplit[1]);
				String ORDER_DESC = "";
				for (int j = 0; j < jyjcTableCount; j++) {
					if (s[i].contains(jyjcTable.getParmValue().getValue(
							"APPLICATION_NO", j))) {
						ORDER_DESC = (String) jyjcTable.getParmValue().getData(
								"ORDER_DESC", j);
						break;
					}
				}
				result.addData("ORDER_DESC", ORDER_DESC);
			}
		}
		TParm sortParm = sortParm(result);
		TTable table = (TTable) this.getComponent("serverDocTable");

		table.setParmValue(sortParm);
	}

	/**
	 * ����
	 * 
	 * @param docServerParm
	 * @return
	 */
	private TParm sortParm(TParm docServerParm) {
		TParm result = new TParm();
		int sortCount = docServerSortParm.getCount("SEQ");
		for (int i = 0; i < sortCount; i++) {
			String type = (String) docServerSortParm.getData("FILE_TYPE", i);
			for (int j = 0; j < docServerParm.getCount("FileName"); j++) {
				String fileType = (String) docServerParm.getData("Type", j);
				if (fileType.indexOf(type)>=0) {
					result.addRowData(docServerParm, j);
					docServerParm.removeRow(j);
					j--;
				}else if(!StringUtil.isNullString((String)docServerParm.getData("ORDER_DESC", j))){
					fileType=(String)docServerParm.getData("ORDER_DESC", j);
					if (fileType.indexOf(type)>0) {
						result.addRowData(docServerParm, j);
						docServerParm.removeRow(j);
						j--;
				}
				}
					
			}

		}
		for (int j = 0; j < docServerParm.getCount("FileName"); j++) {
			result.addRowData(docServerParm, j);
		}
		return result;
	}

	/**
	 * JHW�������
	 * 
	 * @param parm
	 */
	public void onEmrRead(TParm parm) {
		onShow();
	}

	/**
	 * �����ĵ����˫��
	 */
	public void docTableDoubleClick() {
		int column = docTable.getSelectedColumn();
		int row = docTable.getSelectedRow();
		TParm parm = docTable.getParmValue().getRow(row);
		if (column == 5) {
			onEmrRead(parm);
			return;
		} else if (column == 6) {
			if (!StringUtil.isNullString((String) docTable.getValueAt(row,
					column))) {
				openPDF(parm);
			}
		}
	}

	/**
	 * �����������˫��
	 */
	public void serverDocTableDoubleClick() {
		TTable table = (TTable) this.getComponent("serverDocTable");
		int col = table.getSelectedColumn();
		if (col == 0) {
			return;
		}
		int row = table.getSelectedRow();

		TParm parm = table.getParmValue().getRow(row);
		parm.setData("FILE_NAME", parm.getData("FileName"));
		Runtime runtime = Runtime.getRuntime();
		byte data[] = TIOM_FileServer.readFile(socket, serverPath + "\\"
				+ parm.getData("FileName"));
		
		if (data == null) {
			messageBox_("��������û���ҵ��ļ� " + serverPath + "\\"
					+ parm.getData("FileName"));
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + parm.getData("FileName"), data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			// ���ļ�
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + parm.getData("FileName"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * PDF�������
	 */
	public boolean openPDF(TParm parm) {
		String fileName = parm.getValue("FILE_NAME");
		String filePath = serverPath + fileName + ".pdf";

		byte data[] = TIOM_FileServer.readFile(socket, filePath);

		if (data == null) {
			messageBox_("��������û���ҵ��ļ� " + filePath);
		}
		try {
			FileTool.setByte(tempPath + "\\" + parm.getData("FILE_NAME")
					+ ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime runtime = Runtime.getRuntime();
		try {
			// ���ļ�
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + parm.getData("FILE_NAME") + ".pdf");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * ��ҳ��ӡ
	 */
	public boolean onPrintIndex() {
		TTable table = this.getTTable("docTable");
		table.acceptText();
		int rowCount = table.getRowCount();

		boolean isShowMessage = true;

		boolean flg = true;

		File fDir = new File(tempPath);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		Map fileMap = new HashMap();
		this.delAllFile(tempPath);
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

		for (int i = 0; i < rowCount; i++) {
			// this.delAllFile(tempPath);
			TParm parm = table.getParmValue().getRow(i);// һ����¼
			parm.setData("TEMP_PATH", tempPath);// ��ʱ·��

			if (parm.getValue("REPORT_FLG").equals("Y")) {// �Ƿ�ѡ��

				isShowMessage = false;
				TWord word = new TWord();
				word.onOpen(parm.getValue("FILE_PATH"), parm// ��ģ��
						.getValue("FILE_NAME") + ".jhw", 3, true);
				// $$ =========add by lx 2012/08/10 ����һ�����¾�ʼ����
				// Start===============$$//
				TParm sexP = new TParm(TJDODBTool.getInstance().select(
						"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
								+ parm.getValue("MR_NO") + "'"));
				// System.out.println("===MR_NO==="+parm.getValue("MR_NO"));

				if (sexP.getInt("SEX_CODE", 0) == 9) {
					word.setSexControl(0);
				} else {
					word.setSexControl(sexP.getInt("SEX_CODE", 0));
				}
				// $$ =========add by lx 2012/08/10 ����һ�����¾�ʼ����
				// end===============$$//
				// word.print();
				try {
					word.getPageManager().setOrientation(1);
					word.getPageManager().print(PrinterJob.getPrinterJob(),
							parm.getValue("FILE_NAME"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				fileMap.put(parm.getValue("FILE_NAME"), parm);
				// flg = onUpdate(parm);
				// if (!flg) {
				// return false;
				// }
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
			}
		}
		TWord word = new TWord();
		word.print();
		flg = onUpdate(fileMap);
		if (isShowMessage) {
			// ��ѡ����
			this.messageBox("E0099");
			return false;

		}
		return true;

	}

	/**
	 * ����EMR�ļ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public boolean writePDFEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("PDF_CREATOR_USER", Operator.getID());
		parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parm.setData("PDF_CREATOR_DATE", TJDODBTool.getInstance().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CURRENT_USER", Operator.getID());

		TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction",
				"writePDFEmrFile", parm);

		return falg;
	}

	/**
	 * ����PDF
	 */
	public void onGeneratePDF() {
		TTable table = this.getTTable("docTable");
		boolean b = onPrintIndex();
		if (!b) {
			this.messageBox("����ʧ�ܡ�");
			return;
		}
		this.messageBox("���ɳɹ���");
		onInit();
	}

	/**
	 * �������
	 */
	public void onShow() {
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַ
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// ������ҳ����
			Runtime.getRuntime().exec("cmd /c start " + ip + mrNo);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �ϴ�����
	 */
	public boolean onUpdate(TParm parm) {
		Clipboard clipboard = null;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = clipboard.getContents(null);
		List fileList = this.getAllFile(tempPath);
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			// File f = (File) iterator.next();
			String fileName = parm.getValue("FILE_NAME");
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
					+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF")
					+ "\\" + fileName + ".pdf";
			try {
				TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						filePath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				byte data[] = FileTool.getByte(f);

				if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						filePath, data)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * �ϴ�����
	 */
	public boolean onUpdate(Map fileMap) {
		List fileList = this.getAllFile(tempPath);
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			TParm parm = (TParm) fileMap.get(f.getName().split("\\.")[0]);
			if (parm == null) {
				continue;
			}
			String fileName = parm.getValue("FILE_NAME");
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
					+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF")
					+ "\\" + fileName + ".pdf";
			try {
				TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						filePath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				byte data[] = FileTool.getByte(f);

				if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						filePath, data)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ��ָ���ļ����������ļ�
	 * 
	 * @param path �ļ�·��
	 */
	public boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}

	public static List getAllFile(String path) {
		List fileList = new ArrayList();
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			return null;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				fileList.add(temp);
			}
		}
		return fileList;
	}

	/**
	 * �ϲ���������ʿվ��
	 */
	public void onAllPDFonAddPdf() {
		File f = new File(tempPath + "\\data");
		if (!f.exists())
			f.mkdirs();
		TTable table = (TTable) getComponent("serverDocTable");
		TParm parm = table.getParmValue();
		int count = parm.getCount("FileName");
		List list = new ArrayList();
		int c = 0;
		for (int i = 0; i < count; i++) {
			String fileName = parm.getValue("FileName", i);
			String type = parm.getValue("Type", i);
			f = new File(tempPath + "\\" + fileName);
			if (!f.exists())
				continue;
			try {
				byte[] data = FileTool.getByte(tempPath + "\\" + fileName);
				FileTool.setByte(tempPath + "\\data\\" + c + ".pdf", data);
				list.add(type);
				c++;
			} catch (Exception e) {
			}
		}

		// ����ִ���ļ�
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox_("��������û���ҵ��ļ� " + PdfTool.getInstance().getRoot()
					+ "\\pdftk");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\data\\pdftk.exe", data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// �����������ļ�
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < c; i++)
			sb.append(i + ".pdf ");
		String s = tempPath.substring(0, 2) + "\r\n" + "cd " + tempPath
				+ "\\data\r\n" + "pdftk.exe " + sb.toString() + " cat output "
				+ caseNo + ".pdf";
		try {
			FileTool.setByte(tempPath + "\\data\\pdf.bat", s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// ִ���������ļ�
		PDFODITool tool = new PDFODITool();
		TParm p = new TParm(tool.exec(tempPath + "\\data\\pdf.bat"));
		if (p.getErrCode() != 0) {
			messageBox_(p.getErrText());
			//System.out.println(p.getErrText());
			return;
		}
		//������ǩĿ¼����
		jacobTool.setBookmarks(tempPath + "\\data\\" + caseNo + ".pdf", list);
		//
		for (int i = 0; i < c; i++) {
			File f1 = new File(tempPath + "\\data\\" + i + ".pdf");
			f1.delete();
		}
		File f1 = new File(tempPath + "\\data\\pdf.bat");
		f1.delete();
		f1 = new File(tempPath + "\\data\\pdftk.exe");
		f1.delete();
		try {
			data = FileTool.getByte(tempPath + "\\data\\" + caseNo + ".pdf");
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
		}
		f1 = new File(tempPath + "\\data\\" + caseNo + ".pdf");
		f1.delete();
		f1 = new File(tempPath + "\\data");
		f1.delete();
		messageBox_("�ϲ��ɹ�!");
	}

	/**
	 * �ϴ�����
	 */
	public void onUpdate() {
		String localPath = getText("tTextField_1");
		if (localPath == null || localPath.length() == 0) {
			messageBox_("��ѡ����ʱĿ¼!");
			return;
		}
		String caseno = getText("tTextField_4");
		if (caseno == null || caseno.length() == 0) {
			messageBox_("�����������!");
			return;
		}
		String mrno = getText("tTextField_3");
		if (mrno == null || mrno.length() == 0) {
			messageBox_("�����벡����!");
			return;
		}
		TParm check = new TParm();
		check.setData("MR_NO", mrno);
		check.setData("CASE_NO", caseno);
		
		File f = new File(localPath + "\\" + caseno + ".pdf");
		if (!f.exists()) {
			messageBox_("�����ļ�������!");
			return;
		}
		String fileName = fileServerRoot + "\\��ʽ����\\" + mrno.substring(0, 7)
				+ "\\" + mrno + "\\" + caseno + ".pdf";
		try {
			byte data[] = FileTool.getByte(f);
			if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					fileName, data)) {
				messageBox_("�ϴ�ʧ��!");
				return;
			}
		} catch (Exception e) {
		}
		TParm updateFlg = new TParm();
		updateFlg.setData("CHECK_FLG", PDF_FLG_YTJ); // �ύ״̬
		updateFlg.setData("OPT_USER", Operator.getID());
		updateFlg.setData("OPT_TERM", Operator.getIP());
		updateFlg.setData("MR_NO", mrno);
		updateFlg.setData("CASE_NO", caseno);
		TParm re = PdfTool.getInstance().updateCheckFlg(updateFlg);
		if (re.getErrCode() < 0) {
			this.messageBox_("����״̬����ʧ��");
		}
		messageBox_("�ϴ��ɹ�!");
	}


	/**
	 * ���
	 */
	public void onReaderSubmitPDF() {
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + mrNo.substring(0, 7) + "\\" + mrNo + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				bigFilePath);
		if (data == null) {
			messageBox_("��δ�ύPDF");
			return;
		}

		try {
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime runtime = Runtime.getRuntime();
		try {
			// ���ļ�
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void onSubmit() {
		TParm check = new TParm();
		check.setData("MR_NO", mrNo);
		check.setData("CASE_NO", caseNo);
		check.setData("CHECK_FLG", "N");
		check.setData("IN_FLG", "N");
		check.setData("BOX_CODE", "");
		int flg = PdfTool.getInstance().checkFlg(check);
		if (flg != PDF_FLG_WTJ && flg != PDF_FLG_SHTH && flg != PDF_FLG_GDTH) {
			this.messageBox_("��ǰ����״̬Ϊ��"
					+ ((TLabel) this.getComponent("tip")).getText() + ",�����޸�");
			return;
		}
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + mrNo.substring(0, 7) + "\\" + mrNo + "\\"
				+ caseNo + ".pdf";
		File file = new File(tempPath + "/" + caseNo + ".pdf");

		byte data[];
		try {
			if (!file.exists()) {
				this.messageBox("��ϲ����������ύ��");
				return;
			}
			data = FileTool.getByte(file);
			TParm updateFlg = new TParm();
			updateFlg.setData("CHECK_FLG", "2"); // �ύ״̬
			updateFlg.setData("OPT_USER", Operator.getID());
			updateFlg.setData("OPT_TERM", Operator.getIP());
			updateFlg.setData("MERGE_CODE", Operator.getID());
			updateFlg.setData("MERGE_DATE", Operator.getIP());
			updateFlg.setData("SUBMIT_CODE", Operator.getID());
			updateFlg.setData("SUBMIT_DATE", Operator.getIP());
			updateFlg.setData("MR_NO", mrNo);
			updateFlg.setData("CASE_NO", caseNo);

			updateFlg.setData("CREATE_HOSP", "HIS");
			updateFlg.setData("CHECK_FLG", PDF_FLG_YTJ);
			updateFlg.setData("IN_FLG", "2");
			updateFlg.setData("PRINT_FLG", "N");
			updateFlg.setData("CURT_HOSP", "HIS");
			updateFlg.setData("CURT_LOCATION", "HIS");
			updateFlg.setData("TRAN_HOSP", "HIS");
			updateFlg.setData("BOX_CODE", "");

			TParm re = null;
			if (flg == PDF_FLG_WTJ) {
				re = PdfTool.getInstance().insertMRV(updateFlg);
			} else {
				re = PdfTool.getInstance().updateCheckFlg(updateFlg);
			}

			if (re.getErrCode() < 0) {
				this.messageBox_("����״̬����ʧ��");
				return;
			}
			if (TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					bigFilePath, data)) {

				// PdfTool.getInstance().insertMRV(check);
				messageBox_("�ύ�ɹ�!");
				((TLabel) this.getComponent("tip")).setText("���ύ");

				return;
			}

			messageBox_("�ύʧ��!");
		} catch (IOException e) {
			e.printStackTrace();
			messageBox_("�ύʧ��!");
		}

	}

	/**
	 * ���ƶ�
	 */
	public void onUp() {
		TTable table = (TTable) getComponent("serverDocTable");
		table.acceptText();
		int row = table.getSelectedRow();
		if (row < 1)
			return;
		TParm p = table.getParmValue();
		String t = p.getValue("FileName", row);
		p.setData("FileName", row, p.getValue("FileName", row - 1));
		p.setData("FileName", row - 1, t);
		t = p.getValue("Type", row);
		p.setData("Type", row, p.getValue("Type", row - 1));
		p.setData("Type", row - 1, t);
		t = p.getValue("ORDER_DESC", row);
		p.setData("ORDER_DESC", row, p.getValue("ORDER_DESC", row - 1));
		p.setData("ORDER_DESC", row - 1, t);

		t = p.getValue("FLG", row);
		p.setData("FLG", row, p.getValue("FLG", row - 1));
		p.setData("FLG", row - 1, t);
		table.setParmValue(p);
		table.setSelectedRow(row - 1);
	}

	/**
	 * ���ƶ�
	 */
	public void onDown() {
		TTable table = (TTable) getComponent("serverDocTable");
		table.acceptText();
		int row = table.getSelectedRow();
		if (row < 0 || row > table.getRowCount() - 2)
			return;
		TParm p = table.getParmValue();
		String t = p.getValue("FileName", row);
		p.setData("FileName", row, p.getValue("FileName", row + 1));
		p.setData("FileName", row + 1, t);
		t = p.getValue("Type", row);
		p.setData("Type", row, p.getValue("Type", row + 1));
		p.setData("Type", row + 1, t);
		t = p.getValue("ORDER_DESC", row);
		p.setData("ORDER_DESC", row, p.getValue("ORDER_DESC", row + 1));
		p.setData("ORDER_DESC", row + 1, t);

		t = p.getValue("FLG", row);
		p.setData("FLG", row, p.getValue("FLG", row + 1));
		p.setData("FLG", row + 1, t);
		table.setParmValue(p);
		table.setSelectedRow(row + 1);
	}

	/**
	 * ɾ����ʱ����������
	 */
	public void onDelTempPDF() {

		// �ӱ����ȡѡ�е��ļ���
		TTable table = (TTable) getComponent("serverDocTable");
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫɾ�����ļ�!");
			return;
		}

		if (this.messageBox("ѯ��", "ȷ��Ҫɾ���ļ���", 2) == 0) {
			// ȡ�������ļ�·
			TParm p = table.getParmValue();
			String fileName = p.getValue("FileName", row);
			// Ҫɾ�����ļ�
			String filePath = serverPath + "\\" + fileName;

			// ��������������Ŀ¼;
			boolean isCreateDir = TIOM_FileServer.mkdir(TIOM_FileServer
					.getSocket(), fileServerRoot + "\\pdfbackup");
			if (isCreateDir) {
				// �ƶ��ļ�������Ŀ¼;
				// ȡ����˶�Ӧ�ļ���
				byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
						.getSocket(), filePath);
				if (data == null) {
					messageBox("�����û���ҵ�Ҫɾ�����ļ�!");
					return;
				}
				// д�ļ�������˱���Ŀ¼;
				boolean isBuckupFLG = TIOM_FileServer.writeFile(TIOM_FileServer
						.getSocket(), fileServerRoot + "\\pdfbackup\\"
						+ fileName, data);
				if (!isBuckupFLG) {
					messageBox("����˱����ļ�ʧ��!");
					return;
				}

				// ɾ���ļ�
				boolean isDelFLG = TIOM_FileServer.deleteFile(TIOM_FileServer
						.getSocket(), filePath);
				if (isDelFLG) {
					// ˢ�±��,ͬ��ɾ�������;
					table.removeRow(row);
					messageBox("ɾ���ļ��ɹ�!");

				} else {
					messageBox("ɾ���ļ�ʧ��!");
					return;
				}

			} else {
				messageBox("������������������Ŀ¼ʧ��!");
				return;
			}

		}

	}
	
    /**
     * ��ʿ�ϲ���������
     */
	public void onAddPdf() {
		int message = this.messageBox("��Ϣ", "�����ϲ��������ݲ���������ͬ��������Ҫ����һ��ʱ�䣬�Ƿ������", 0);
		if(message!=0){
			return;
		}
		TParm p = new TParm();
		this.delAllFile(tempPath);
		TTable table = this.getTTable("serverDocTable");
		table.acceptText();
		PDFODITool tool = new PDFODITool();
		List list = new ArrayList();
		tool.downLoadTempFile(serverPath, tempPath, table.getParmValue(),list);
		//this.messageBox("�����ļ��ϲ��У��벻Ҫ�ֹ��ر�����ڣ�");
		
		try {
			//setBookmarks(tempPath + "\\data\\" + caseNo + ".pdf", list);
			p=tool.addPdf(table.getParmValue(), tempPath, caseNo);
			setBookmarks(tempPath + "\\" + caseNo + ".pdf", list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(p.getErrCode() != 0){
			//
			String strmsg="��������ʧ�ܣ�\n"+p.getErrText()+"\n";
			strmsg+="���𻵵�pdf�ļ�����\n���鱾��C:\\JavaHisFile\\temp\\pdfĿ¼�¶�Ӧϵͳ����еĲ����ļ�������Ϣ����ϵ";
			this.messageBox(strmsg);
		}else{
			this.messageBox("�������ϳɹ���");
		}
		
		//messageBox_("�������ϳɹ�!");
	}

    public static String setBookmarks(String fileName, List marks) {
        String path = getPath(fileName);
        int rows[] = new int[marks.size()];
        int row = 0;
        for (int i = 0; i < marks.size(); i++) {
            rows[i] = row;
            row += getCount(path + "\\" + i + ".pdf");
        }
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            if (document.isEncrypted()) return "Error: Cannot add bookmarks to encrypted document.";
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            PDOutlineItem pagesOutline = new PDOutlineItem();
            pagesOutline.setTitle("All Pages");
            outline.appendChild(pagesOutline);
            List pages = document.getDocumentCatalog().getAllPages();
            for (int i = 0; i < rows.length; i++) {
                PDPage page = (PDPage) pages.get(rows[i]);
                PDPageFitWidthDestination dest = new PDPageFitWidthDestination();
                dest.setPage(page);
                PDOutlineItem bookmark = new PDOutlineItem();
                bookmark.setDestination(dest);
                bookmark.setTitle((String) marks.get(i));
                pagesOutline.appendChild(bookmark);
            }
            pagesOutline.openNode();
            outline.openNode();
            document.save(fileName);
            document.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public static String getPath(String fileName) {
        int index = fileName.lastIndexOf("\\");
        return fileName.substring(0, index);
    }

    public static int getCount(String fileName) {
        int count = 0;
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            List pages = document.getDocumentCatalog().getAllPages();
            count = pages.size();
            document.close();
        }
        catch (Exception e) {}
        return count;
    }
	    
    /**
     * ����excel
     */
    public void onExport() {// add by wanglong 20130719
        TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        if (tabPane.getSelectedIndex() == 1) {// ������ҳǩ
            TTable table = (TTable) this.getComponent("TABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("û������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "סԺ������������Ŀ��ϸ");
        }
    }
    // ============================================add by wanglong 20130719
    /**
     * �����������������
     * 
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // �������򷽷�;
                // ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // �����parmֵһ��,
                // 1.ȡparamwֵ;
                TParm tableData = getTTable("TABLE").getParmValue();
                // 2.ת�� vector����, ��vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // 3.���ݵ������,��vector����
                // ������������;
                String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
                // ת��parm�е���
                int col = tranParmColIndex(columnName, tblColumnName);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames);
            }
        });
    }

    /**
     * vectoryת��param
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        // ������->��
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // ������;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        getTTable("TABLE").setParmValue(parmTable);
    }

    /**
     * �õ� Vector ֵ
     * 
     * @param group String ����
     * @param names String "ID;NAME"
     * @param size int �������
     * @return Vector
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }
	
	/**
	 * ͨ���ļ�����ȷ���ļ��洢λ��(������)
	 * 
	 * @return tsocket �ļ��洢������
	 */
	private TSocket getFileServerAddress() {
		// Ĭ���ļ�����
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// ȡcaseNoǰ2λ�����
		String sYear = caseNo.substring(0, 2);
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String ip = config.getString("", "FileServer." + sYear + ".IP");
		if (ip != null && !ip.equals("")) {
			// ��ָ���������ļ�
			tsocket = TIOM_FileServer.getSocket(sYear);
		}

		return tsocket;
	}
	
	/**
	 * ͨ���ļ�����ȷ���ļ��洢λ��(·��)
     * 
     * @return strEmrDataDir �ļ��洢λ��
     */
	private String getEmrDataDir() {
		String strEmrDataDir = TIOM_FileServer.getRoot()
				+ TIOM_FileServer.getPath("EmrData");
		// ȡcaseNoǰ2λ
		String sYear = caseNo.substring(0, 2);
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String root = config.getString("", "FileServer." + sYear + ".Root");
		if (root != null && !root.equals("")) {
			// ��ָ���������ļ�
			strEmrDataDir = TIOM_FileServer.getRoot(sYear)
					+ TIOM_FileServer.getPath("EmrData");
		}
		
		String path = strEmrDataDir + "PDF" + File.separator
				+ caseNo.substring(0, 2) + File.separator
				+ caseNo.substring(2, 4) + File.separator + mrNo
				+ File.separator;

		return path;
	}
	
	/**
	 * ���²�����ȡ״̬
	 * 
	 * @param applicationNo
	 * @param CAT1_TYPE
	 */
	private void updateMedApplyFlg(String applicationNo, String CAT1_TYPE) {
		String sql = "UPDATE  MED_APPLY SET ISREAD='Y' WHERE  CAT1_TYPE='"
				+ CAT1_TYPE + "'  AND APPLICATION_NO='" + applicationNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		initTABLE();
	}
	
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
