package action.med;

import java.io.File;

import jdo.med.MEDApplyTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 外送检验PDF自动归档批次程序
 * </p>
 * 
 * <p>
 * Description: 外送检验PDF自动归档批次程序
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
 * @author wangb 2017.5.5
 * @version 1.0
 */
public class MEDExtrnalLisPdfBatch extends Patch {

	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// 外送检验数据交互服务器IP
	private int port;// 外送检验数据交互服务器端口号
	private String pdfPath = TConfig.getSystemValue("EXTRNAL_LIS.PDF_PATH");// 外送检验PDF路径
	private String pdfBackupPath = TConfig
			.getSystemValue("EXTRNAL_LIS.PDF_BACKUP_PATH");// 外送检验PDF备份路径

    /**
     * 批次线程
     * @return boolean
     */
	public boolean run() {
		if (StringUtils.isEmpty(ip)) {
			System.out.println("配置文件中未设置外送检验数据交互服务器IP");
			return false;
		}
		if (StringUtils.isEmpty(TConfig
				.getSystemValue("EXTRNAL_LIS.SERVER_PORT"))) {
			System.out.println("配置文件中未设置外送检验数据交互服务器端口号");
			return false;
		} else {
			port = StringTool.getInt(TConfig
					.getSystemValue("EXTRNAL_LIS.SERVER_PORT"));
		}

		if (StringUtils.isEmpty(pdfPath)) {
			System.out.println("配置文件中未设置外送检验PDF路径");
			return false;
		}

		if (StringUtils.isEmpty(pdfBackupPath)) {
			System.out.println("配置文件中未设置外送检验PDF备份路径");
			return false;
		}

		TSocket socket = new TSocket(ip, port);
		// 获得pdf文件列表
		String[] files = TIOM_FileServer.listFile(socket, pdfPath);

		if (files != null) {
			try {
				TSocket targetSocket = TIOM_FileServer.getSocket("Main");
				String targetPath = TIOM_FileServer.getRoot()
						+ TIOM_FileServer.getPath("EmrData");
				TParm parm = new TParm();
				TParm updateParm = new TParm();
				int length = files.length;
				byte[] data = null;
				String applicationNo = "";
				String fileName = "";
				String path = "";
				String caseNo = "";

				for (int i = 0; i < length; i++) {
					if (!files[i].contains("pdf") && !files[i].contains("PDF")) {
						continue;
					}

					data = TIOM_FileServer.readFile(socket, pdfPath
							+ File.separator + files[i]);

					if (data != null) {
						applicationNo = files[i].substring(0, files[i].indexOf(".pdf"));
						parm = new TParm();
						parm.setData("APPLICATION_NO", applicationNo);
						parm.setData("CAT1_TYPE", "LIS");
						// 查询检验检查医嘱信息
						parm = MEDApplyTool.getInstance().queryMedApplyInfo(
								parm);

						if (parm.getErrCode() < 0) {
							System.out.println("查询检验检查医嘱信息失败:"
									+ parm.getErrText());
							continue;
						}

						if (parm.getCount() < 1) {
							System.out.println("未找到检验检查医嘱信息，条码号："
									+ applicationNo);
							continue;
						}

						caseNo = parm.getValue("CASE_NO", 0);
						fileName = caseNo + "_检验报告_" + applicationNo + ".pdf";
						path = targetPath + "PDF" + File.separator
								+ caseNo.substring(0, 2) + File.separator
								+ caseNo.substring(2, 4) + File.separator
								+ parm.getValue("MR_NO", 0);

						// 将PDF文件进行病历周转
						if (TIOM_FileServer.writeFile(targetSocket, path
								+ File.separator + fileName, data)) {
							updateParm = new TParm();
							updateParm.setData("STATUS", "7");
							updateParm.setData("PDFRE_FLG", "Y");
							updateParm.setData("CAT1_TYPE", "LIS");
							updateParm.setData("APPLICATION_NO", applicationNo);
							updateParm = MEDApplyTool.getInstance().updateMedApplyStatus(updateParm);
							
							if (updateParm.getErrCode() < 0) {
								System.out.println("条码号为【" + applicationNo
										+ "】的外检数据状态更新失败");
								System.out.println(updateParm.getErrText());
							}
							
							// 将文件备份
							if (TIOM_FileServer.writeFile(socket, pdfBackupPath
									+ File.separator + files[i], data)) {
								// 删除该文件
								if (!TIOM_FileServer.deleteFile(socket, pdfPath
										+ File.separator + files[i])) {
									System.out.println("文件【" + files[i]
											+ "】删除失败");
								}
							} else {
								System.out.println("文件【" + files[i] + "】备份失败");
							}
						}
					} else {
						System.out.println("文件【" + files[i] + "】读取失败");
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}

		return true;
	}
}
