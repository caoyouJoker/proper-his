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
 * Title: ���ͼ���PDF�Զ��鵵���γ���
 * </p>
 * 
 * <p>
 * Description: ���ͼ���PDF�Զ��鵵���γ���
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

	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// ���ͼ������ݽ���������IP
	private int port;// ���ͼ������ݽ����������˿ں�
	private String pdfPath = TConfig.getSystemValue("EXTRNAL_LIS.PDF_PATH");// ���ͼ���PDF·��
	private String pdfBackupPath = TConfig
			.getSystemValue("EXTRNAL_LIS.PDF_BACKUP_PATH");// ���ͼ���PDF����·��

    /**
     * �����߳�
     * @return boolean
     */
	public boolean run() {
		if (StringUtils.isEmpty(ip)) {
			System.out.println("�����ļ���δ�������ͼ������ݽ���������IP");
			return false;
		}
		if (StringUtils.isEmpty(TConfig
				.getSystemValue("EXTRNAL_LIS.SERVER_PORT"))) {
			System.out.println("�����ļ���δ�������ͼ������ݽ����������˿ں�");
			return false;
		} else {
			port = StringTool.getInt(TConfig
					.getSystemValue("EXTRNAL_LIS.SERVER_PORT"));
		}

		if (StringUtils.isEmpty(pdfPath)) {
			System.out.println("�����ļ���δ�������ͼ���PDF·��");
			return false;
		}

		if (StringUtils.isEmpty(pdfBackupPath)) {
			System.out.println("�����ļ���δ�������ͼ���PDF����·��");
			return false;
		}

		TSocket socket = new TSocket(ip, port);
		// ���pdf�ļ��б�
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
						// ��ѯ������ҽ����Ϣ
						parm = MEDApplyTool.getInstance().queryMedApplyInfo(
								parm);

						if (parm.getErrCode() < 0) {
							System.out.println("��ѯ������ҽ����Ϣʧ��:"
									+ parm.getErrText());
							continue;
						}

						if (parm.getCount() < 1) {
							System.out.println("δ�ҵ�������ҽ����Ϣ������ţ�"
									+ applicationNo);
							continue;
						}

						caseNo = parm.getValue("CASE_NO", 0);
						fileName = caseNo + "_���鱨��_" + applicationNo + ".pdf";
						path = targetPath + "PDF" + File.separator
								+ caseNo.substring(0, 2) + File.separator
								+ caseNo.substring(2, 4) + File.separator
								+ parm.getValue("MR_NO", 0);

						// ��PDF�ļ����в�����ת
						if (TIOM_FileServer.writeFile(targetSocket, path
								+ File.separator + fileName, data)) {
							updateParm = new TParm();
							updateParm.setData("STATUS", "7");
							updateParm.setData("PDFRE_FLG", "Y");
							updateParm.setData("CAT1_TYPE", "LIS");
							updateParm.setData("APPLICATION_NO", applicationNo);
							updateParm = MEDApplyTool.getInstance().updateMedApplyStatus(updateParm);
							
							if (updateParm.getErrCode() < 0) {
								System.out.println("�����Ϊ��" + applicationNo
										+ "�����������״̬����ʧ��");
								System.out.println(updateParm.getErrText());
							}
							
							// ���ļ�����
							if (TIOM_FileServer.writeFile(socket, pdfBackupPath
									+ File.separator + files[i], data)) {
								// ɾ�����ļ�
								if (!TIOM_FileServer.deleteFile(socket, pdfPath
										+ File.separator + files[i])) {
									System.out.println("�ļ���" + files[i]
											+ "��ɾ��ʧ��");
								}
							} else {
								System.out.println("�ļ���" + files[i] + "������ʧ��");
							}
						}
					} else {
						System.out.println("�ļ���" + files[i] + "����ȡʧ��");
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
