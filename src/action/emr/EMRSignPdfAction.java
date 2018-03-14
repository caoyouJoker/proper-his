package action.emr;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import action.emr.client.ServiceSoap_ServiceSoap_Client;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:电子签章
 * </p>
 * 
 * <p>
 * Description: 电子签章
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
 * @author wangb 2016.01.11
 * @version 1.0
 */
public class EMRSignPdfAction extends TAction {
	
	public TParm signPdf(TParm parm) {
		TParm result = new TParm();
		// 获得病历文件服务器
		TSocket readSocket = TIOM_FileServer.getSocket();
		// 获得电子签章文件存储路径
		String signPdfPath = TConfig.getSystemValue("CA.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(signPdfPath)) {
			result.setErr(-1, "CA电子签章文件存储路径配置错误");
			return result;
		}
		
		// 获得CA服务器IP
		String caServerIp = TConfig.getSystemValue("CA.SERVER_IP");
		if (StringUtils.isEmpty(caServerIp)) {
			result.setErr(-1, "CA服务器地址配置错误");
			return result;
		}
		
		// 获得CA文件服务器端口
		int caFileServerPort = 8103;
		String fileServerPort = TConfig.getSystemValue("CA.FILE_SERVER_PORT");
		if (StringUtils.isEmpty(fileServerPort)) {
			result.setErr(-1, "CA文件服务器端口号配置错误");
			return result;
		} else {
			caFileServerPort = StringTool.getInt(fileServerPort);
		}
		
		// 获得CA电子签章完毕PDF存放服务器地址
		String storeServerIp = TConfig.getSystemValue("CA.STORE.SERVER_IP");
		if (StringUtils.isEmpty(caServerIp)) {
			result.setErr(-1, "CA电子签章完毕PDF存放服务器地址配置错误");
			return result;
		}
		
		// 获得CA电子签章完毕PDF存放文件服务器端口号
		int storeServerPort = 8103;
		String serverPort = TConfig.getSystemValue("CA.STORE.FILE_SERVER_PORT");
		if (StringUtils.isEmpty(serverPort)) {
			result.setErr(-1, "CA电子签章完毕PDF存放文件服务器端口号配置错误");
			return result;
		} else {
			storeServerPort = StringTool.getInt(serverPort);
		}
		
		// 获得CA电子签章完毕PDF存放文件位置(病历存储服务器)
		String storeSignPdfPath = TConfig.getSystemValue("CA.STORE.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(storeSignPdfPath)) {
			result.setErr(-1, "CA电子签章完毕PDF存放文件位置配置错误");
			return result;
		}
		
		String bigFilePath = "";
		String mrNo = "";
		String caseNo = "";
		String path = TConfig.getSystemValue("FileServer.Main.Root");
		// 获得CA电子签章pdf监听
		TSocket signPdfSocket = new TSocket(caServerIp, caFileServerPort);
		// 获得CA电子签章存储pdf监听
		TSocket storePdfSocket = new TSocket(storeServerIp, storeServerPort);
		StringBuffer errMsg = new StringBuffer();
		boolean flag = false;
		String fileName = "";
		byte[] fs = null;
		byte[] newFileByte = null;
		
		for (int i = 0; i < parm.getCount(); i++) {
			if (!"Y".equals(parm.getValue("FLG", i))) {
				continue;
			}
			mrNo = parm.getValue("MR_NO", i);
			caseNo = parm.getValue("CASE_NO", i);
			bigFilePath = path + "\\正式病历\\" + mrNo.substring(0, 7) + "\\"
					+ mrNo + "\\" + caseNo + ".pdf";
			
			// 从文件服务器读取完整病历
			fs = TIOM_FileServer.readFile(readSocket, bigFilePath);
			if (fs == null) {
				errMsg.append("病案号:" + mrNo + ",尚未提交PDF\r\n");
				continue;
			}
			
			// 调用webservice电子病历加签数字签名
			fileName = ServiceSoap_ServiceSoap_Client.signPDF(fs, parm.getInt("X"), parm.getInt("Y"), parm.getInt("W"),
					parm.getInt("H"), 1);
				
			newFileByte = TIOM_FileServer.readFile(signPdfSocket,
					signPdfPath + File.separator + fileName);
			if (newFileByte != null) {
				flag = TIOM_FileServer.writeFile(storePdfSocket, bigFilePath.replace(path + "\\正式病历\\", storeSignPdfPath + "\\"), newFileByte);
				if (flag) {
					// 加签成功后记录成功的数据信息便于更新归档通过标记
					result.addData("CASE_NO", parm.getValue("CASE_NO", i));
				} else {
					errMsg.append("病案号:" + mrNo + ",签章失败PDF\r\n");
				}
			} else {
				errMsg.append("病案号:" + mrNo + ",签章失败PDF\r\n");
			}
		}
		
		if (StringUtils.isNotEmpty(errMsg.toString())) {
			result.setErr(-1, errMsg.toString());
		}
		
		return result;
	}
}
                                                  