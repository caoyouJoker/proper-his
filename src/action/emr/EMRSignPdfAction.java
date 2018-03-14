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
 * Title:����ǩ��
 * </p>
 * 
 * <p>
 * Description: ����ǩ��
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
		// ��ò����ļ�������
		TSocket readSocket = TIOM_FileServer.getSocket();
		// ��õ���ǩ���ļ��洢·��
		String signPdfPath = TConfig.getSystemValue("CA.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(signPdfPath)) {
			result.setErr(-1, "CA����ǩ���ļ��洢·�����ô���");
			return result;
		}
		
		// ���CA������IP
		String caServerIp = TConfig.getSystemValue("CA.SERVER_IP");
		if (StringUtils.isEmpty(caServerIp)) {
			result.setErr(-1, "CA��������ַ���ô���");
			return result;
		}
		
		// ���CA�ļ��������˿�
		int caFileServerPort = 8103;
		String fileServerPort = TConfig.getSystemValue("CA.FILE_SERVER_PORT");
		if (StringUtils.isEmpty(fileServerPort)) {
			result.setErr(-1, "CA�ļ��������˿ں����ô���");
			return result;
		} else {
			caFileServerPort = StringTool.getInt(fileServerPort);
		}
		
		// ���CA����ǩ�����PDF��ŷ�������ַ
		String storeServerIp = TConfig.getSystemValue("CA.STORE.SERVER_IP");
		if (StringUtils.isEmpty(caServerIp)) {
			result.setErr(-1, "CA����ǩ�����PDF��ŷ�������ַ���ô���");
			return result;
		}
		
		// ���CA����ǩ�����PDF����ļ��������˿ں�
		int storeServerPort = 8103;
		String serverPort = TConfig.getSystemValue("CA.STORE.FILE_SERVER_PORT");
		if (StringUtils.isEmpty(serverPort)) {
			result.setErr(-1, "CA����ǩ�����PDF����ļ��������˿ں����ô���");
			return result;
		} else {
			storeServerPort = StringTool.getInt(serverPort);
		}
		
		// ���CA����ǩ�����PDF����ļ�λ��(�����洢������)
		String storeSignPdfPath = TConfig.getSystemValue("CA.STORE.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(storeSignPdfPath)) {
			result.setErr(-1, "CA����ǩ�����PDF����ļ�λ�����ô���");
			return result;
		}
		
		String bigFilePath = "";
		String mrNo = "";
		String caseNo = "";
		String path = TConfig.getSystemValue("FileServer.Main.Root");
		// ���CA����ǩ��pdf����
		TSocket signPdfSocket = new TSocket(caServerIp, caFileServerPort);
		// ���CA����ǩ�´洢pdf����
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
			bigFilePath = path + "\\��ʽ����\\" + mrNo.substring(0, 7) + "\\"
					+ mrNo + "\\" + caseNo + ".pdf";
			
			// ���ļ���������ȡ��������
			fs = TIOM_FileServer.readFile(readSocket, bigFilePath);
			if (fs == null) {
				errMsg.append("������:" + mrNo + ",��δ�ύPDF\r\n");
				continue;
			}
			
			// ����webservice���Ӳ�����ǩ����ǩ��
			fileName = ServiceSoap_ServiceSoap_Client.signPDF(fs, parm.getInt("X"), parm.getInt("Y"), parm.getInt("W"),
					parm.getInt("H"), 1);
				
			newFileByte = TIOM_FileServer.readFile(signPdfSocket,
					signPdfPath + File.separator + fileName);
			if (newFileByte != null) {
				flag = TIOM_FileServer.writeFile(storePdfSocket, bigFilePath.replace(path + "\\��ʽ����\\", storeSignPdfPath + "\\"), newFileByte);
				if (flag) {
					// ��ǩ�ɹ����¼�ɹ���������Ϣ���ڸ��¹鵵ͨ�����
					result.addData("CASE_NO", parm.getValue("CASE_NO", i));
				} else {
					errMsg.append("������:" + mrNo + ",ǩ��ʧ��PDF\r\n");
				}
			} else {
				errMsg.append("������:" + mrNo + ",ǩ��ʧ��PDF\r\n");
			}
		}
		
		if (StringUtils.isNotEmpty(errMsg.toString())) {
			result.setErr(-1, errMsg.toString());
		}
		
		return result;
	}
}
                                                  