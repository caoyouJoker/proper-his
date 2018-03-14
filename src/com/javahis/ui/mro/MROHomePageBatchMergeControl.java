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
 * Title: 病案首页批量合并
 * </p>
 * 
 * <p>
 * Description: 病案首页批量合并
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
	
	/** 传入参数 */
	private TParm parameter;
	private String tempPath ="C:\\JavaHisFile\\temp\\pdf";
	private TParm homePageParm;// 已打印病案首页数据集合
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
			
			// 删除临时文件夹文件
			FileUtils.getInstance().delAllFile(tempPath);
			
			int count = parameter.getCount();
			if (count < 1) {
				this.closeWindow();
				return;
			}
			label.setValue("总计：" + count);
			String caseNo = "";
			
			for (int i = 0; i < count; i++) {
				caseNo = caseNo + "'" + parameter.getValue("CASE_NO", i) + "'";
				if (i < count - 1) {
					caseNo = caseNo + ",";
				}
			}
			
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO_LIST", caseNo);
			// 查询病案首页数据
			homePageParm = MROTool.getInstance().queryHomePageInfo(queryParm);
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					// 批量处理病案首页
					batchDealWithEMRFile();
					label.setValue("总计：" + parameter.getCount() + "  失败："
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
						progressBar.setString("合并完成！");
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
	 * 批量处理病历
	 */
	private void batchDealWithEMRFile() {
//		int count = parameter.getCount();
		int homePageCount = homePageParm.getCount();
		List<String> caseNoList = new ArrayList<String>();
		StringBuffer errMsg = new StringBuffer();
		
		/*// 未打印病案首页的数据给予提示
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
				errMsg.append("病案号【" + parameter.getValue("MR_NO", i) + "】尚未打印病案首页\r\n");
				if (!errorCaseNoList.contains(parameter.getValue("CASE_NO", i))) {
					errorCaseNoList.add(parameter.getValue("CASE_NO", i));
				}
			}
		}*/
		
		// 下载正式病历
		String downLoadMsg = onDonwLoadPDF();
		if (StringUtils.isNotEmpty(downLoadMsg)) {
			errMsg.append(downLoadMsg);
		}
		
		Date beginDate;
		Date endDate;
		// 默认超时时间为60s
		int timeOut = 60;
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		File file;
		String uploadMsg = "";
		
		// 将病案首页jhw转换成pdf
		for (int j = 0 ; j < homePageCount; j++) {
			if (!caseNoList.contains(homePageParm.getValue("CASE_NO", j))) {
				caseNoList.add(homePageParm.getValue("CASE_NO", j));
				try {
					// 按页打印病案首页
					onPrintIndex(homePageParm.getRow(j));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				// 上传病历
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
						errMsg.append("病案号【"+ homePageParm.getValue("MR_NO", j) +"】的病案首页文件生成错误，文件过大导致超时或PDF存储路径设置错误\r\n");
						if (!errorCaseNoList.contains(homePageParm.getValue("CASE_NO", j))) {
							errorCaseNoList.add(homePageParm.getValue("CASE_NO", j));
						}
						break;
					}
					file = new File(tempPath + "\\" + "0"
							+ homePageParm.getValue("CASE_NO", j) + ".pdf");
					if (file.exists()) {
						completeFlg = true;
						// 上传病案首页病历
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
		
		// 合并病历
		this.mergeAllPdf();
	}
	
	/**
	 * 按页打印病案首页
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
	 * 上传病案首页病历
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
				msg = "病案号【" + parm.getValue("MR_NO") + "】病案首页上传失败\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * 下载正式病历
	 * 
	 * @return 提示信息
	 */
	private String onDonwLoadPDF() {
		int count = homePageParm.getCount();
		String severRoot = TConfig.getSystemValue("FileServer.Main.Root");
		String bigFilePath = "";
		byte[] data = null;
		StringBuffer errMsg = new StringBuffer();

		for (int i = 0; i < count; i++) {
			try {
				bigFilePath = severRoot + "\\正式病历\\"
						+ homePageParm.getValue("MR_NO", i).substring(0, 7) + "\\"
						+ homePageParm.getValue("MR_NO", i) + "\\"
						+ homePageParm.getValue("CASE_NO", i) + ".pdf";

				data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
						bigFilePath);
				if (null == data) {
					errMsg.append("病案号【" + homePageParm.getValue("MR_NO", i)
							+ "】尚未提交完整病历PDF\r\n");
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
	 * 合并病历
	 */
	private void mergeAllPdf() {
		File file = new File(tempPath + "");

		if (!file.exists()) {
			file.mkdirs();
		}
		// 下载执行文件
		byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox("服务器上没有找到文件 " + PdfTool.getInstance().getRoot()
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
		// 制作批处理文件
		String s = tempPath.substring(0, 2) + "\r\n" + "cd " + tempPath
				+ "\r\n" + sb.toString() + " \r\n exit";
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
			messageBox(p.getErrText());
			System.out.println(p.getErrText());
			return;
		}
	}
	
	/**
	 * 上传合并病历
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
									+ "\\正式病历\\"
									+ homePageParm.getValue("MR_NO", j)
											.substring(0, 7) + "\\"
									+ homePageParm.getValue("MR_NO", j) + "\\"
									+ homePageParm.getValue("CASE_NO", j)
									+ ".pdf";
							TIOM_FileServer.deleteFile(TIOM_FileServer
									.getSocket(), bigFilePath);
							if (!TIOM_FileServer.writeFile(TIOM_FileServer
									.getSocket(), bigFilePath, data)) {
								sbMsg.append("病案号【"
										+ homePageParm.getValue("MR_NO", j)
										+ "】合并病历上传失败\r\n");
							} else {
								updateParm = new TParm();
								updateParm.setData("CASE_NO", homePageParm
										.getValue("CASE_NO", j));
								updateParm.setData("HP_MERGE_CODE", optId);
								updateParm.setData("HP_MERGE_DATE", "SYSDATE");
								updateParm.setData("OPT_USER", optId);
								updateParm.setData("OPT_TERM", optIp);
								// 更新合并病案首页信息
								updateParm = MROTool.getInstance()
										.updateMergeHomePageInfo(updateParm);

								if (updateParm.getErrCode() < 0) {
									sbMsg.append("病案号【"
											+ homePageParm.getValue("MR_NO", j)
											+ "】更新合并病案首页信息失败\r\n");
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
			this.messageBox("本次操作无病案首页合并成功病历，上传失败");
			this.closeWindow();
			return;
		} else if (uploadCaseNolist.size() > 0) {
			result.setData("FLG", "SUCCESS");
			this.setReturnValue(result);
			this.messageBox("上传成功");
			this.closeWindow();
			return;
		}
	}
}
