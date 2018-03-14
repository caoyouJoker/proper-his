package com.javahis.ui.odi;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import jdo.bil.BILComparator;
import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.pdf.jacobTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.pdf.server.PDFServer;

public class DocQueryControl extends TControl {
	/**
	 * 动作类名字
	 */
	private static final String actionName = "action.odi.ODIAction";
	private TParm docServerSortParm = null;
	/**
	 * 已审核
	 */
	private static int PDF_FLG_YSH = 2;
	/**
	 * 已提交
	 */
	private static int PDF_FLG_YTJ = 1;
	/**
	 * 未提交
	 */
	private static int PDF_FLG_WTJ = -1;
	/**
	 * 审核退回
	 */
	private static int PDF_FLG_SHTH = -2;
	/**
	 * 归档退回
	 */
	private static int PDF_FLG_GDTH = -3;
	/**
	 * 已归档
	 */
	private static int PDF_FLG_YGD = 3;
	
    private BILComparator compare = new BILComparator();//加排序功能 add by wanglong 20130719
    private boolean ascending = false;//加排序功能 
    private int sortColumn = -1;//加排序功能 
    
	/**
	 * 初始化方法
	 */
	public void onInit() {

		super.onInit();
		// 初始化页面
		this.initPage();
		this.initTABLE();
		this.initServerDocTable();
	}

	private String MR_NO = "";
	private String IPD_NO = "";
	private String caseNo = "";
	private TTable docTable;
	private String fileServerRoot = TConfig
			.getSystemValue("FileServer.Main.Root");
	String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	private String serverPath = "";

	private void initPage() {
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		TParm parm = (TParm) this.getParameter();
		this.setValue("DEPT_CODE", parm.getData("ODI", "DEPT_CODE").toString());
		((TComboBox) this.getComponent("SEX")).setValue(parm.getData("ODI",
				"SEX_CODE").toString());
		this.setValue("STATION_CODE", parm.getData("ODI", "STATION_CODE")
				.toString());
		// 设置病患姓名
		((TTextField) this.getComponent("PAT_NAME")).setValue(parm.getData(
				"ODI", "PAT_NAME").toString());
		// 床位号
		((TTextField) this.getComponent("BED_NO")).setValue(parm.getData("ODI",
				"BED_NO").toString());
		
		IPD_NO = parm.getData("ODI", "IPD_NO").toString();
		((TTextField) this.getComponent("IPD_NO")).setValue(IPD_NO);
		// 病案号
		MR_NO = parm.getData("ODI", "MR_NO").toString();
		((TTextField) this.getComponent("MR_NO")).setValue(MR_NO);
		
		
		// 设置就诊号
		caseNo = parm.getData("ODI", "CASE_NO").toString();
		String mrNo = parm.getData("ODI", "MR_NO").toString();
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
				+ "   E.AUTOGRAPH_STATE, E.AUTOGRAPH_KEY ,'JHW查看' AS JHW, DECODE(PDF_CREATOR_USER ,  NULL,'','PDF查看') AS PDF "
				+ "FROM JAVAHIS.EMR_FILE_INDEX E WHERE CASE_NO ='" + caseNo
				+ "' AND MR_NO='" + mrNo + "' ORDER BY FILE_SEQ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("docTable");
		if (result.getCount() > 0) {
			for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("PDF", i).length() > 1
						&& result.getValue("OPT_DATE", i).compareTo(
								result.getValue("PDF_CREATOR_DATE", i)) > 0) {

					result.setData("PDF", i, result.getValue("PDF", i) + "(旧)");
				}

			}
		}
		TParm check = new TParm();
		check.setData("MR_NO", MR_NO);
		check.setData("CASE_NO", caseNo);
		int flg = PdfTool.getInstance().checkFlg(check);
		if(flg== PDF_FLG_WTJ){
			((TLabel) this.getComponent("tip")).setText("未提交");
			
		}else if(flg== PDF_FLG_YTJ){
			((TLabel) this.getComponent("tip")).setText("已提交");
		}else if(flg== PDF_FLG_SHTH){
			((TLabel) this.getComponent("tip")).setText("审核退回");
		}else if(flg== PDF_FLG_YSH){
			((TLabel) this.getComponent("tip")).setText("审核通过");
		}else if(flg== PDF_FLG_YGD){
			((TLabel) this.getComponent("tip")).setText("已归档");
		}else if(flg== PDF_FLG_GDTH){
			((TLabel) this.getComponent("tip")).setText("归档退回");
		}
		docTable = this.getTTable("docTable");
		
		// add by wangb 2015/12/2 过滤掉指定病历文件 START
		String filterFileSql = "SELECT * FROM EMR_FILE_FILTER WHERE FILTER_TYPE='JHW' AND FILTER_FLG='Y'";
		TParm filterFileResult = new TParm(TJDODBTool.getInstance().select(filterFileSql));
		int count = filterFileResult.getCount();
		if (count > 0) {
			String filterList = filterFileResult.getValue("SUBCLASS_CODE");
			for (int i = result.getCount() - 1; i > -1; i--) {
				if (filterList.contains(result.getValue("SUBCLASS_CODE", i))) {
					result.removeRow(i);
				}
			}
		}
		// add by wangb 2015/12/2 过滤掉指定病历文件 END
		
		table.setParmValue(result);
		docServerSortParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT SEQ,FILE_TYPE FROM EMR_PDFLISTORDER ORDER BY SEQ "));
		
	}
	/**
	 * 浏览
	 */
	public void onReader() {
		// String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
		// + "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
		// + caseNo + ".pdf";
		// try{
		// byte data[] = TIOM_FileServer.readFile(bigFilePath);
		// if(data == null)
		// {
		// messageBox_("服务器上没有找到文件 " + bigFilePath);
		// return;
		// }
		File file = new File(tempPath + "\\" + caseNo + ".pdf");

		if (!file.exists())
			messageBox_("尚未生成完整病历。 ");
		// FileTool.setByte(tempPath+"\\temp.pdf", data);
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void initServerDocTable() {
		TTable jyjcTable = (TTable) this.getComponent("TABLE");
		addSortListener(jyjcTable);// add by wanglong 20130719
		TParm parm = docTable.getParmValue().getRow(0);
		//
		String fileName = parm.getValue("FILE_NAME");
		if(fileName==null||fileName.equals("")){
			messageBox_("没有任何JHW病历文件！ ");
			return;
		}
		//
		/*
		 * serverPath = TConfig.getSystemValue("FileServer.Main.Root") + "\\" +
		 * TConfig.getSystemValue("EmrData") + "\\" +
		 * parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF");
		 */

		serverPath = getEmrDataDir(fileName) + "\\"
				+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF");
		//
		/*
		 * String s[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(),
		 * serverPath);
		 */
		//
		String s[] = TIOM_FileServer.listFile(getFileServerAddress(fileName),
				serverPath);
		
		
		
		//
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
					if (sSplit[sSplit.length - 1].startsWith(jyjcTable
							.getParmValue().getData("APPLICATION_NO", j)
							+ ".")) {
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

	private TParm sortParm(TParm docServerParm) {
		TParm result = new TParm();
		int sortCount = docServerSortParm.getCount("SEQ");
		for (int i = 0; i < sortCount; i++) {
			String type = (String) docServerSortParm.getData("FILE_TYPE", i);
			for (int j = 0; j < docServerParm.getCount("FileName"); j++) {
				String fileType = (String) docServerParm.getData("Type", j);
				if (fileType.indexOf(type)>=0) {
					// result.addParm(docServerParm.getRow(j));6
					result.addRowData(docServerParm, j);
					docServerParm.removeRow(j);
					j--;
				}else if(!StringUtil.isNullString((String)docServerParm.getData("ORDER_DESC", j))){
					fileType=(String)docServerParm.getData("ORDER_DESC", j);
					if (fileType.indexOf(type)>0) {
						// result.addParm(docServerParm.getRow(j));6
						result.addRowData(docServerParm, j);
						docServerParm.removeRow(j);
						j--;
				}
				}
					
			}

		}
		for (int j = 0; j < docServerParm.getCount("FileName"); j++) {
			// result.addParm(docServerParm.getRow(j));6
			result.addRowData(docServerParm, j);
		}
		// result.addParm(docServerParm);
		return result;
	}

	private void initTABLE() {
		String sql = "SELECT B.CAT1_TYPE, B.ISREAD, B.APPLICATION_NO, B.ORDER_CODE, B.ORDER_DESC,"+
				        	"B.OPTITEM_CHN_DESC, B.ORDER_DATE, B.STATUS, B.ORDER_DR_CODE, B.RESERVED_DATE,"+
				    		"B.REGISTER_DATE, B.INSPECT_DATE, B.EXAMINE_DATE, B.EXEC_DR_CODE, B.REPORT_DR,"+
				    		"B.EXAMINE_DR, C.MR_NO, C.PAT_NAME,"+
					    	"(CASE WHEN B.PDFRE_FLG = 'Y' THEN '已生成' ELSE '' END) AS PDFRE_FLG "+
				 	   "FROM ADM_INP A, MED_APPLY B, SYS_PATINFO C "+
					  "WHERE A.CASE_NO = '"+caseNo+"' "+
						"AND A.MR_NO = '" + MR_NO + "' "+
						"AND A.MR_NO = C.MR_NO "+
						"AND A.CASE_NO = B.CASE_NO";//modify by wanglong 20120912  增加“pdf是否生成”列
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("TABLE");
		table.setParmValue(result);
	}

	/**
	 * JHW病历浏览
	 */
	public void onEmrRead(TParm parm) {
		onShow();
	}

	public void docTableDoubleClick() {

		// this.messageBox("======onTableClicked======");

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

	public void serverDocTableDoubleClick() {

		// this.messageBox("======onTableClicked======");
		TTable table = (TTable) this.getComponent("serverDocTable");
		int col = table.getSelectedColumn();
		if (col == 0) {
			return;
		}
		int row = table.getSelectedRow();

		TParm parm = table.getParmValue().getRow(row);
		parm.setData("FILE_NAME", parm.getData("FileName"));
		String fileName=(String)parm.getData("FileName");
		
/*		String filePath = getEmrDataDir(fileName) + "\\"
		+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
		+ fileName;*/
		// parm.setValue();
		Runtime runtime = Runtime.getRuntime();

		/*byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				serverPath + "\\" + parm.getData("FileName"));*/
		
		byte data[] = TIOM_FileServer.readFile(getFileServerAddress(fileName),
				serverPath + "\\" + parm.getData("FileName"));
		
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + serverPath + "\\"
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
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + parm.getData("FileName"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void tableDoubleClick() {
		TParm docParm = docTable.getParmValue().getRow(0);
		TTable table = this.getTTable("TABLE");
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		if (!isExist(table.getParmValue().getRow(row))) {
			this.messageBox("请检查报告状态，尚未生成报告。");
			return;
		}
		if (table.getValueAt(row, 0).equals("N")) {
			updateMedApplyFlg(table.getParmValue().getData("APPLICATION_NO",
					row).toString(), table.getParmValue().getData("CAT1_TYPE",
					row).toString());
		}

	}

	private boolean isExist(TParm parm) {
		String type = parm.getValue("CAT1_TYPE").equalsIgnoreCase("LIS") ? "检验报告"
				: "检查报告";
		TParm serverDocParm = ((TTable) this.getComponent("serverDocTable"))
				.getParmValue();
		for (int i = 0; i < serverDocParm.getCount("FileName"); i++) {
			TParm serverDocRowParm = serverDocParm.getRow(i);
			//因PDF后缀样本编号方法改为包含此字符串默认为所找文件  20120709  shibl  modify
			if (serverDocRowParm.getValue("FileName").contains(
					caseNo + "_" + type + "_" + parm.getValue("APPLICATION_NO"))) {
				
				/*byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
						.getSocket(), serverPath + "\\"
						+ serverDocRowParm.getData("FileName"));*/
				
				byte data[] = TIOM_FileServer.readFile(
						getFileServerAddress((String) serverDocRowParm
								.getData("FileName")), serverPath + "\\"
								+ serverDocRowParm.getData("FileName"));
				//
				if (data == null) {
					messageBox_("服务器上没有找到文件 " + serverPath + "\\"
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
					// 打开文件
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

	private void updateMedApplyFlg(String applicationNo, String CAT1_TYPE) {
		String sql = "Update  MED_APPLY set isread='Y' where  CAT1_TYPE='"
				+ CAT1_TYPE + "'  AND APPLICATION_NO='" + applicationNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		initTABLE();
	}

	/**
	 * PDF病历浏览
	 */
	public boolean openPDF(TParm parm) {
		String fileName = parm.getValue("FILE_NAME");
		//

		/*
		 * String filePath = TConfig.getSystemValue("FileServer.Main.Root") +
		 * "\\" + TConfig.getSystemValue("EmrData") + "\\" +
		 * parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\" +
		 * fileName + ".pdf";
		 */
		String filePath = getEmrDataDir(fileName) + "\\"
				+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF") + "\\"
				+ fileName + ".pdf";

		//
		/*
		 * byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
		 * filePath);
		 */
		byte data[] = TIOM_FileServer.readFile(getFileServerAddress(fileName),
				filePath);
			
		//
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + filePath);
		}
		try {
			FileTool.setByte(tempPath + "\\" + parm.getData("FILE_NAME")
					+ ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + parm.getData("FILE_NAME") + ".pdf");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 按页打印
	 */
	public boolean onPrintIndex() {
		// TWord word = (TWord) this.getComponent("WORD");
		// !this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
		// emrParm.getValue("FILE_NAME"), 3, true)) {
		// return

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
		// 默认超时时间为30s
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
			TParm parm = table.getParmValue().getRow(i);//一条记录
			parm.setData("TEMP_PATH", tempPath);//临时路径
			
			if (parm.getValue("REPORT_FLG").equals("Y")) {//是否被选中

				isShowMessage = false;
				TWord word = new TWord();
				word.onOpen(parm.getValue("FILE_PATH"), parm//打开模板
						.getValue("FILE_NAME")
						+ ".jhw", 3, true);
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
			}
		}
		TWord word = new TWord();
		word.print();
		flg = onUpdate(fileMap);
		//this.messageBox("--flg--"+flg);
		if (isShowMessage) {
			// 请选择病历
			this.messageBox("E0099");
			return false;

		}
		return true;

	}

	/**
	 * 保存EMR文件
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

		// this.messageBox("type"+this.getOnlyEditType());
		TParm result = TIOM_AppServer.executeAction(actionName,
				"writePDFEmrFile", parm);
		
		//System.out.println("-----result-----"+result);

		return falg;
	}



	public void onGeneratePDF() {
		TTable table = this.getTTable("docTable");
		boolean b = onPrintIndex();
		if (!b) {
			this.messageBox("生成失败。");
			return;
		}
		this.messageBox("生成成功。");
		onInit();
		// PDFServer server=new PDFServer();
		// server.creatTextPDF();
	}

	public void onShow() {
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// 连接网页方法
			Runtime.getRuntime().exec("cmd /c start " + ip + MR_NO);
			// run.exec("IEXPLORE.EXE " + ip + MR_NO);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param parmCase
	 * @return
	 */
	private TParm getTextListByPat(TParm parmCase) {
		TParm result = new TParm();
		// PDFGetINWData.getINWTextList(parmCase, result);
		// PDFGetSUMData.getSUMATextList(parmCase, result);
		PDFServer server = new PDFServer();
		server.creatTextPDF();
		// PDFGetEMRData.getEMRTextList(parmCase, result);
		return result;
	}

	

	/**
	 * 上传病历
	 */
	public boolean onUpdate(TParm parm) {

		TParm check = new TParm();
		check.setData("MR_NO", parm.getValue("MR_NO"));
		check.setData("CASE_NO", parm.getValue("CASE_NO"));

		Clipboard clipboard = null;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = clipboard.getContents(null);
		// messageBox_("上传成功!");
		Iterator iterator = null;
		java.util.List fileList = null;

		fileList = this.getAllFile(tempPath);
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			// File f = (File) iterator.next();
			String fileName = parm.getValue("FILE_NAME");
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
					+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF")
					+ "\\" + fileName + ".pdf";
			// String fileName = fileServerRoot + "\\正式病历\\" +
			// mrno.substring(0,7) +
			// "\\" + mrno + "\\" + caseno + ".pdf";
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
	 * 上传病历
	 */
	public boolean onUpdate(Map fileMap) {
		//System.out.println("----tempPath-----"+tempPath);
		List fileList = this.getAllFile(tempPath);
		//System.out.println("-----fileList size------"+fileList.size());
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
			//System.out.println("------fileName------"+fileName);
			//System.out.println("------filePath------"+filePath);
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
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
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
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
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

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 合并病历（护士站）
	 */
	public void onAllPDFonAddPdf() {
		// TParm check = new TParm();
		// check.setData("MR_NO", MR_NO);
		// check.setData("CASE_NO", caseNo);
		// if (PdfTool.getInstance().checkFlg(check)) {
		// this.messageBox_("病历已经通过审核，不可修改");
		// return;
		// }
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

		// 下载执行文件
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + PdfTool.getInstance().getRoot()
					+ "\\pdftk");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\data\\pdftk.exe", data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// 制作批处理文件
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
		// 执行批处理文件
		PDFODITool tool = new PDFODITool();
		TParm p = new TParm(tool.exec(tempPath + "\\data\\pdf.bat"));
		if (p.getErrCode() != 0) {
			messageBox_(p.getErrText());
			//System.out.println(p.getErrText());
			return;
		}
		//加入书签目录功能
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
		messageBox_("合并成功!");
	}

	/**
	 * 上传病历
	 */
	public void onUpdate() {
		String localPath = getText("tTextField_1");
		if (localPath == null || localPath.length() == 0) {
			messageBox_("请选择临时目录!");
			return;
		}
		String caseno = getText("tTextField_4");
		if (caseno == null || caseno.length() == 0) {
			messageBox_("请输入问诊号!");
			return;
		}
		String mrno = getText("tTextField_3");
		if (mrno == null || mrno.length() == 0) {
			messageBox_("请输入病案号!");
			return;
		}
		TParm check = new TParm();
		check.setData("MR_NO", mrno);
		check.setData("CASE_NO", caseno);
		// if (PdfTool.getInstance().checkFlg(check)) {
		// this.messageBox_("病历已经通过审核，不可修改");
		// return;
		// }
		File f = new File(localPath + "\\" + caseno + ".pdf");
		if (!f.exists()) {
			messageBox_("病历文件不存在!");
			return;
		}
		String fileName = fileServerRoot + "\\正式病历\\" + mrno.substring(0, 7)
				+ "\\" + mrno + "\\" + caseno + ".pdf";
		try {
			byte data[] = FileTool.getByte(f);
			if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					fileName, data)) {
				messageBox_("上传失败!");
				return;
			}
		} catch (Exception e) {
		}
		TParm updateFlg = new TParm();
		updateFlg.setData("CHECK_FLG", PDF_FLG_YTJ); // 提交状态
		updateFlg.setData("OPT_USER", Operator.getID());
		updateFlg.setData("OPT_TERM", Operator.getIP());
		updateFlg.setData("MR_NO", mrno);
		updateFlg.setData("CASE_NO", caseno);
		TParm re = PdfTool.getInstance().updateCheckFlg(updateFlg);
		if (re.getErrCode() < 0) {
			this.messageBox_("病历状态更新失败");
		}
		messageBox_("上传成功!");
	}


	/**
	 * 浏览
	 */
	public void onReaderSubmitPDF() {
		 String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
		 + "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
		 + caseNo + ".pdf";
		 byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
					.getSocket(),bigFilePath);
		 if(data == null)
		 {
		 messageBox_("尚未提交PDF");
		 return;
		 }

		try {
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath + "\\" + caseNo + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	public void onSubmit() {
		TParm check = new TParm();
		check.setData("MR_NO", MR_NO);
		check.setData("CASE_NO", caseNo);
		check.setData("CHECK_FLG", "N");
		check.setData("IN_FLG", "N");
		check.setData("BOX_CODE", "");
		int flg = PdfTool.getInstance().checkFlg(check);
		if (flg !=PDF_FLG_WTJ  && flg !=PDF_FLG_SHTH && flg!=PDF_FLG_GDTH ) {
			this.messageBox_("当前病历状态为："+((TLabel) this.getComponent("tip")).getText()+",不可修改");
			return;
		}
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		File file = new File(tempPath + "/" + caseNo + ".pdf");

		byte data[];
		try {
			if (!file.exists()) {
				this.messageBox("请合并病历后再提交。");
				return;
			}
			data = FileTool.getByte(file);
			TParm updateFlg = new TParm();
			updateFlg.setData("CHECK_FLG", "2"); // 提交状态
			updateFlg.setData("OPT_USER", Operator.getID());
			updateFlg.setData("OPT_TERM", Operator.getIP());
			updateFlg.setData("MERGE_CODE", Operator.getID());
			updateFlg.setData("MERGE_DATE", Operator.getIP());
			updateFlg.setData("SUBMIT_CODE", Operator.getID());
			updateFlg.setData("SUBMIT_DATE", Operator.getIP());
			updateFlg.setData("MR_NO", MR_NO);
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
				this.messageBox_("病历状态更新失败");
				return;
			}
			if (TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					bigFilePath, data)) {

				// PdfTool.getInstance().insertMRV(check);
				messageBox_("提交成功!");
				((TLabel) this.getComponent("tip")).setText("已提交");
				
				return;
			}

			messageBox_("提交失败!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox_("提交失败!");
		}

	}

	/**
	 * 上移动
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
	 * 下移动
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
	 * 删除临时病历并备份
	 */
	public void onDelTempPDF() {

		// 从表格中取选中的文件；
		TTable table = (TTable) getComponent("serverDocTable");
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要删除的文件!");
			return;
		}

		if (this.messageBox("询问", "确定要删除文件吗？", 2) == 0) {
			// 取服务器文件路
			TParm p = table.getParmValue();
			String fileName = p.getValue("FileName", row);
			// 要删除的文件
			String filePath = serverPath + "\\" + fileName;

			// 服务器创建备份目录;
			boolean isCreateDir = TIOM_FileServer.mkdir(TIOM_FileServer
					.getSocket(), fileServerRoot + "\\pdfbackup");
			if (isCreateDir) {
				// 移动文件到备份目录;
				// 取服务端对应文件；
				byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
						.getSocket(), filePath);
				if (data == null) {
					messageBox("服务端没有找到要删除的文件!");
					return;
				}
				// 写文件到服务端备份目录;
				boolean isBuckupFLG = TIOM_FileServer.writeFile(TIOM_FileServer
						.getSocket(), fileServerRoot + "\\pdfbackup\\"
						+ fileName, data);
				if (!isBuckupFLG) {
					messageBox("服务端备份文件失败!");
					return;
				}

				// 删除文件
				boolean isDelFLG = TIOM_FileServer.deleteFile(TIOM_FileServer
						.getSocket(), filePath);
				if (isDelFLG) {
					// 刷新表格,同步删除表格行;
					table.removeRow(row);
					messageBox("删除文件成功!");

				} else {
					messageBox("删除文件失败!");
					return;
				}

			} else {
				messageBox("服务器创建病历备份目录失败!");
				return;
			}

		}

	}
	
    /**
     * 护士合并病历操作
     */
	public void onAddPdf() {
		int message = this.messageBox("消息", "病历合并处理依据病历数量不同，可能需要花费一段时间，是否继续？", 0);
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
		//this.messageBox("病历文件合并中，请不要手工关闭命令窗口！");
		
		try {
			//setBookmarks(tempPath + "\\data\\" + caseNo + ".pdf", list);
			p=tool.addPdf(table.getParmValue(), tempPath, caseNo);
			setBookmarks(tempPath + "\\" + caseNo + ".pdf", list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(p.getErrCode() != 0){
			//
			String strmsg="病历整合失败！\n"+p.getErrText()+"\n";
			strmsg+="有损坏的pdf文件导致\n请检查本机C:\\JavaHisFile\\temp\\pdf目录下对应系统表格中的病历文件，与信息部联系";
			this.messageBox(strmsg);
		}else{
			this.messageBox("病历整合成功！");
		}
		
		//messageBox_("病历整合成功!");
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
     * 导出excel
     */
    public void onExport() {// add by wanglong 20130719
        TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        if (tabPane.getSelectedIndex() == 1) {// 检验检查页签
            TTable table = (TTable) this.getComponent("TABLE");
            if (table.getRowCount() <= 0) {
                this.messageBox("没有数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table, "住院病患检验检查项目明细");
        }
    }
    // ============================================add by wanglong 20130719
    /**
     * 加入表格排序监听方法
     * 
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断 f
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // 表格中parm值一致,
                // 1.取paramw值;
                TParm tableData = getTTable("TABLE").getParmValue();
                // 2.转成 vector列名, 行vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // 3.根据点击的列,对vector排序
                // 表格排序的列名;
                String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
                // 转成parm中的列
                int col = tranParmColIndex(columnName, tblColumnName);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames);
            }
        });
    }

    /**
     * vectory转成param
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        // 行数据->列
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // 行数据;
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
     * 得到 Vector 值
     * 
     * @param group String 组名
     * @param names String "ID;NAME"
     * @param size int 最大行数
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
                // System.out.println("tmp相等");
                return index;
            }
            index++;
        }
        return index;
    }
    // ============================================add end
    
	/**
	 * 通过文件名，确认文件存储位置
	 * 
	 * @param fileName
	 * @return
	 */
	public TSocket getFileServerAddress(String fileName) {
		System.out.println("=====fileName:====="+fileName);
		//默认文件服器
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// 1.文件名不是空串情况下
		if (fileName != null && !fileName.equals("")) {
			// 2.包含-的情况
			if (fileName.indexOf("_") != -1) {
				// 3.取第一个caseNo下的情况前2位的情况
				String sYear = fileName.substring(0, 2);
				System.out.println("---sYear：---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		        String ip = config.getString("","FileServer." + sYear + ".IP");
		        System.out.println("====IP：======"+ip);
		        if (ip != null && !ip.equals("")) {
				// 4.找指定的配置文件，如果有则：
		        	tsocket = TIOM_FileServer.getSocket(sYear);
		        }
				
			}
		}
		//   	
		return tsocket;
	}
	
    /**
     * 
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
    
}
