package action.med;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.med.MEDApplyTool;
import jdo.med.MEDExternalLisResultXmlTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ���ͼ������������γ���
 * </p>
 * 
 * <p>
 * Description: ���ͼ������������γ���
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
 * @author wangb 2017.4.17
 * @version 1.0
 */
public class MEDExtrnalLisResultParseBatch extends Patch {

	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// ���ͼ������ݽ���������IP
	private int port;// ���ͼ������ݽ����������˿ں�
	private String resultParsePath = TConfig
			.getSystemValue("EXTRNAL_LIS.RESULT_PARSE_PATH");// ���ͼ���������XML����·��
	private String resultBackupPath = TConfig
			.getSystemValue("EXTRNAL_LIS.RESULT_BACKUP_PATH");// ���ͼ���������XML����·��

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

		if (StringUtils.isEmpty(resultParsePath)) {
			System.out.println("�����ļ���δ�������ͼ���������·��");
			return false;
		}

		if (StringUtils.isEmpty(resultBackupPath)) {
			System.out.println("�����ļ���δ�������ͼ���������XML����·��");
			return false;
		}

		TSocket socket = new TSocket(ip, port);
		// ���XMl�ļ��б�
		String[] files = TIOM_FileServer.listFile(socket, resultParsePath);

		if (files != null) {
			try {
				String dataStr = "";
				String applicationNo = "";
				int length = files.length;
				int seqNo = 0;
				byte[] data = null;
				TParm result = null;
				TParm queryParm = null;
				TParm medLisRptResult = null;
				Map<String, Integer> rpdtlSeqMap = null;
				Map<String, String> orderNoMap = null;
				Map<String, String> seqNoMap = null;
				List<String> applicationNoList = null;

				queryParm = new TParm();
				// ��ѯ���ͼ����ֵ�����
				TParm externalParam = MEDApplyTool.getInstance()
						.queryMedLisExternalParam(queryParm);
				if (externalParam.getErrCode() < 0) {
					System.out.println("��ѯ���ͼ����ֵ����ݴ���:"
							+ externalParam.getErrText());
					return false;
				}
				int externalParamCount = externalParam.getCount();

				for (int i = 0; i < length; i++) {
					applicationNoList = new ArrayList<String>();
					data = TIOM_FileServer.readFile(socket, resultParsePath
							+ File.separator + files[i]);

					if (data != null) {
						dataStr = new String(data, "UTF-8");
						// �������ͼ���ӿڻش���XML���ݲ�ת��ΪTParm����
						result = MEDExternalLisResultXmlTool.getInstance()
								.parseExternalLisResultToParm(dataStr);

						if (result.getErrCode() < 0 || result.getCount() < 1) {
							System.out.println("XMLתTParmʧ�ܣ��ļ�����" + files[i]);
							continue;
						}

						int count = result.getCount();
						rpdtlSeqMap = new HashMap<String, Integer>();
						orderNoMap = new HashMap<String, String>();
						seqNoMap = new HashMap<String, String>();

						for (int j = 0; j < count; j++) {
							result.addData("CAT1_TYPE", "LIS");
							result.addData("RPDTL_SEQ", 1);
							result.addData("CRTCLUPLMT", "");
							result.addData("CRTCLLWLMT", "");
							result.addData("EXEC_DEV_CODE", "");
							result.addData("OPT_USER", "DIAN");
							result.addData("OPT_TERM", "127.0.0.1");
							result.addData("ORDER_NO", "");
							result.addData("SEQ_NO", "");
							result.addData("EXEC_DEV_DESC", "");
							result.addData("PY1", "");

							applicationNo = result
									.getValue("APPLICATION_NO", j);

							if (StringUtils.isEmpty(applicationNo)) {
								System.out.print("�ļ���" + files[i]
										+ "���д������뵥��Ϊ�յ����ݡ�");
								System.out.println("��������:"
										+ result.getValue("PAT_NAME", j)
										+ "����Ŀ���ƣ�"
										+ result.getValue("TESTITEM_CHN_DESC",
												j));
								continue;
							}

							queryParm = new TParm();
							queryParm.setData("APPLICATION_NO", applicationNo);
							queryParm.setData("CAT1_TYPE", "LIS");

							if (!orderNoMap.containsKey(applicationNo)) {
								// ��ѯ����ҽ������
								medLisRptResult = MEDApplyTool.getInstance()
										.queryMedApplyInfo(queryParm);

								if (medLisRptResult.getErrCode() < 0) {
									System.out.println("�ļ���" + files[i]
											+ "���������:" + applicationNo
											+ "��ѯ����ҽ�����ݴ���"
											+ medLisRptResult.getErrText());
									continue;
								}

								if (medLisRptResult.getCount() < 1) {
									System.out.println("�ļ���" + files[i]
											+ "���������:" + applicationNo
											+ "δ��ѯ������ҽ������");
									continue;
								}

								orderNoMap.put(applicationNo, medLisRptResult
										.getValue("ORDER_NO", 0));
								seqNoMap.put(applicationNo, medLisRptResult
										.getValue("SEQ_NO", 0));
							}

							// Ϊ������ܣ��ظ������뵥��ֻУ��һ�α���������ͻ����
							if (!applicationNoList.contains(applicationNo)) {
								applicationNoList.add(applicationNo);
								// ��ѯ�ټ�������
								medLisRptResult = MEDApplyTool.getInstance()
										.queryMedLisRpt(queryParm);

								if (medLisRptResult.getErrCode() < 0) {
									System.out.println("�ļ���" + files[i]
											+ "���������:" + applicationNo
											+ "��ѯ�ټ������ݴ���:"
											+ medLisRptResult.getErrText());
									continue;
								}

								if (medLisRptResult.getCount() > 0) {
									// ɾ���ټ�������
									medLisRptResult = MEDApplyTool
											.getInstance().deleteMedLisRpt(
													queryParm);
									if (medLisRptResult.getErrCode() < 0) {
										System.out.println("�ļ���" + files[i]
												+ "���������:" + applicationNo
												+ "ɾ���ټ������ݴ���:"
												+ medLisRptResult.getErrText());
										continue;
									}
								}
							}

							if (rpdtlSeqMap.containsKey(applicationNo)) {
								seqNo = (Integer) rpdtlSeqMap
										.get(applicationNo) + 1;
								rpdtlSeqMap.put(applicationNo, seqNo);
							} else {
								rpdtlSeqMap.put(applicationNo, 1);
							}

							result.setData("RPDTL_SEQ", j, rpdtlSeqMap
									.get(applicationNo));
							// ����ҽ����ʱ�޷����ݼ���ϸ�����־���ҽ�������seq_noֻ��ȡ����ҽ���е�һ��
							result.setData("ORDER_NO", j, orderNoMap
									.get(applicationNo));
							result.setData("SEQ_NO", j, seqNoMap
									.get(applicationNo));

							// ���ּ�����Ŀ�Ĳο�ֵ��Ҫʹ�ñ�Ժ�Զ�������
							for (int k = 0; k < externalParamCount; k++) {
								if (StringUtils.equals(result.getValue(
										"TESTITEM_CODE", j), externalParam
										.getValue("TEST_CODE", k))) {
									result.setData("LOWER_LIMIT", j,
											externalParam.getValue(
													"NORMAL_LOW", k));
									result.setData("UPPE_LIMIT", j,
											externalParam.getValue(
													"NORMAL_HIGH", k));
									result.setData("REMARK", j, "");
								}
							}

							// �����ټ�������
							medLisRptResult = MEDApplyTool.getInstance()
									.insertMedLisRpt(result.getRow(j));

							if (medLisRptResult.getErrCode() < 0) {
								System.out.println("�ļ���" + files[i] + "���������:"
										+ applicationNo + "�����ټ������ݴ���:"
										+ medLisRptResult.getErrText());
								continue;
							}
						}

						// ���ļ�����
						if (TIOM_FileServer.writeFile(socket, resultBackupPath
								+ File.separator + files[i], data)) {
							// ɾ�����ļ�
							if (!TIOM_FileServer
									.deleteFile(socket, resultParsePath
											+ File.separator + files[i])) {
								System.out.println("�ļ���" + files[i] + "��ɾ��ʧ��");
							}
						} else {
							System.out.println("�ļ���" + files[i] + "������ʧ��");
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
