package com.javahis.ui.odi;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.mro.MROTool;
import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.pdf.server.PDFServer;

public class ODIDocMergeHomeControl extends TControl {

	/**
	 * 初始化方法
	 */
	public void onInit() {

		super.onInit();
		// 初始化页面
		TParm parm = (TParm) this.getParameter();
		this.setTitle("病案首页合并_" + parm.getValue("MR_NO") + "_"
				+ parm.getValue("PAT_NAME"));
		initPage();
	}

	private String caseNo = "";
	private String fileServerRoot = TConfig
			.getSystemValue("FileServer.Main.Root");
	/*String tempPath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\temp\\pdf";*/
	String tempPath ="C:\\JavaHisFile\\temp\\pdf";
	
	private String serverPath = "";

	private void initPage() {

		TParm parm = (TParm) this.getParameter();
		caseNo = parm.getValue("CASE_NO");
		this.setTitle("病案首页合并_" + parm.getValue("MR_NO") + "_"
				+ parm.getValue("PAT_NAME"));

		if (onPrintIndex()) {
			onAllPDFonAddPdf();
		}
	}

	/**
	 * 按页打印
	 */
	public boolean onPrintIndex() {
		TParm parm = (TParm) this.getParameter();
		String sql = "SELECT  E.FILE_PATH, E.FILE_NAME "
				+ "FROM EMR_FILE_INDEX E WHERE CASE_NO ='"
				+ parm.getValue("CASE_NO") + "' AND MR_NO='"
				+ parm.getValue("MR_NO")
				+ "' AND FILE_NAME like '%病案首页%' ORDER BY CREATOR_DATE DESC";

		boolean flg = true;

		File fDir = new File(tempPath);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		Map fileMap = new HashMap();
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// parm.setData("TEMP_PATH", tempPath);
		if (result.getCount("FILE_NAME") > 0) {
			this.delAllFile(tempPath);
			TWord word = new TWord();
			word.onOpen(result.getValue("FILE_PATH", 0), result.getValue(
					"FILE_NAME", 0)
					+ ".jhw", 3, true);
			try {
				word.getPageManager().setOrientation(1);
				word.getPageManager().print(PrinterJob.getPrinterJob(),
						"0" + result.getValue("FILE_NAME", 0).split("_")[0]);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			fileMap.put(result.getValue("FILE_NAME"), result);
		} else {
			this.messageBox("病案首尚未生成。");
			((TButton) this.getComponent("tButton_0")).setEnabled(false);
			((TButton) this.getComponent("tButton_1")).setEnabled(false);

			return false;
		}
		TWord word = new TWord();
		word.print();
		
		// add by wangb 2017/7/10 检查本地首页pdf是否生成
		if (!this.checkFile(caseNo)) {
			return false;
		}
		
		flg = onUpdate(fileMap);
		onDonwLoadPDF();
		return true;

	}

	public void onGeneratePDF() {
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

	/**
	 * PDF病历浏览
	 */
	public boolean openPDF() {
		Runtime runtime = Runtime.getRuntime();

		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
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
	 * 浏览
	 */
	public void onDonwLoadPDF() {
		TParm parm = (TParm) this.getParameter();
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + parm.getValue("MR_NO").substring(0, 7) + "\\"
				+ parm.getValue("MR_NO") + "\\" + caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("尚未提交PDF");
			return;
		}

		try {
			FileTool.setByte(tempPath + "\\" + 1 + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Runtime runtime = Runtime.getRuntime();
		// try {
		// // 打开文件
		// runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath +
		// "\\" + caseNo + ".pdf");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

	}

	/**
	 * 上传病历
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
	 * 合并病历
	 */
	public void onAllPDFonAddPdf() {
		File f = new File(tempPath + "");

		if (!f.exists())
			f.mkdirs();
		// 下载执行文件
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox_("服务器上没有找到文件 " + PdfTool.getInstance().getRoot()
					+ "\\pdftk");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\pdftk.exe", data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// 制作批处理文件
		StringBuffer sb = new StringBuffer();
		sb.append(0 + caseNo + "" + ".pdf ");
		sb.append(1 + caseNo + "" + ".pdf ");
		String s = tempPath.substring(0, 2) + "\r\n" + "cd " + tempPath
				+ "\r\n" + "pdftk.exe " + sb.toString() + " cat output "
				+ caseNo + ".pdf \r\n exit";
		try {
			FileTool.setByte(tempPath + "\\pdf.bat", s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// 执行批处理文件
		PDFODITool tool = new PDFODITool();
		TParm p = new TParm(tool.exec(tempPath + "\\pdf.bat"));
		if (p.getErrCode() != 0) {
			messageBox_(p.getErrText());
			System.out.println(p.getErrText());
			return;
		}
		// jacobTool.setBookmarks(tempPath + "\\data\\" + caseNo + ".pdf",
		// list);
		try {
			data = FileTool.getByte(tempPath + "\\" + caseNo + ".pdf");
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
		}
	}

	/**
	 * 浏览
	 */
	public void onReader() {
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

	public void onSubmit() {
		TParm check = new TParm();
		TParm parm = (TParm) this.getParameter();
		check.setData("MR_NO", parm.getValue("MR_NO"));
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + parm.getValue("MR_NO").substring(0, 7) + "\\"
				+ parm.getValue("MR_NO") + "\\" + caseNo + ".pdf";
		File file = new File(tempPath + "/" + caseNo + ".pdf");

		byte data[];
		try {
			data = FileTool.getByte(file);

			if (TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					bigFilePath, data)) {

				// PdfTool.getInstance().insertMRV(check);
				messageBox_("提交成功!");
				
				String userId = Operator.getID();
				String userIp = Operator.getIP();
				// add by wangb 2017/07/11 更新首页合并人员和时间
				TParm updateParm = new TParm();
				updateParm.setData("CASE_NO", caseNo);
				updateParm.setData("HP_MERGE_CODE", userId);
				updateParm.setData("HP_MERGE_DATE", "SYSDATE");
				updateParm.setData("OPT_USER", userId);
				updateParm.setData("OPT_TERM", userIp);
				// 更新合并病案首页信息
				updateParm = MROTool.getInstance()
						.updateMergeHomePageInfo(updateParm);
				
				if (updateParm.getErrCode() < 0) {
					this.messageBox("更新合并病案首页信息失败");
					return;
				} else {
					TParm result = new TParm();
					result.setData("FLG", "SUCCESS");
					this.setReturnValue(result);
				}
				
				this.closeWindow();
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
	 * 检查本地首页pdf是否生成
	 * 
	 * @param caseNo 就诊号
	 * @return
	 */
	private boolean checkFile(String caseNo) {
		Date beginDate;
		Date endDate;
		// 默认超时时间为60s
		int timeOut = 60;
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		File file;

		beginDate = SystemTool.getInstance().getDate();
		completeFlg = false;
		do {
			// 生成过程中计算用时，超过指定时间后可认定为文件目录设定错误或文件过大，跳过本次循环
			endDate = SystemTool.getInstance().getDate();
			diff = endDate.getTime() - beginDate.getTime();
			// 实时计算时间间隔，超过30s视为失败
			time = (int) Math.floor(diff / (1000));
			if (time > timeOut) {
				this.messageBox("病案首页文件生成错误，文件过大导致超时\r\n");
				return false;
			}
			file = new File(tempPath + "\\" + "0" + caseNo + ".pdf");
			if (file.exists()) {
				completeFlg = true;
			}
		} while (!completeFlg);

		return true;
	}

}
