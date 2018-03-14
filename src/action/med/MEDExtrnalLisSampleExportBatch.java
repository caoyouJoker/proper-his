package action.med;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.med.MEDApplyTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.CSVUtils;
import com.javahis.util.FileUtils;

/**
 * <p>
 * Title: ���ͼ����������γ���
 * </p>
 * 
 * <p>
 * Description: ���ͼ����������γ���
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
 * @author wangb 2017.4.10
 * @version 1.0
 */
public class MEDExtrnalLisSampleExportBatch extends Patch {

	private String lisTransHospCode = TConfig
			.getSystemValue("LIS_TRANS_HOSP_CODE");// �������Ժ������
	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// ���ͼ������ݽ���������IP
	private int port;// ���ͼ������ݽ����������˿ں�
	private String sampleExportPath = TConfig
			.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_PATH");// ���ͼ���걾����CSV����·��
	private String sampleExportTime = TConfig
			.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_TIME");// ���ͼ���걾�������ε�����ֹʱ���

    /**
     * �����߳�
     * @return boolean
     */
	public boolean run() {
		// �ͼ�Ժ������
		if (StringUtils.isEmpty(lisTransHospCode)) {
			System.out.println("�����ļ���δ�������ͼ���Ժ������");
			return false;
		}
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

		if (StringUtils.isEmpty(sampleExportPath)) {
			System.out.println("�����ļ���δ�������ͼ���걾����CSV����·��");
			return false;
		}

		if (StringUtils.isEmpty(sampleExportTime)) {
			System.out.println("�����ļ���δ�������ͼ���걾�������ε�����ֹʱ���");
			return false;
		}

		Timestamp endDate = SystemTool.getInstance().getDate();
		Timestamp startDate = StringTool.rollDate(endDate, -1);

		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", startDate.toString().substring(0, 11)
				+ sampleExportTime);
		queryParm.setData("END_DATE", endDate.toString().substring(0, 11)
				+ sampleExportTime);
		queryParm.setData("ADM_TYPE", "H");
		queryParm.setData("CAT1_TYPE", "LIS");
		queryParm.setData("TRANS_HOSP_CODE", lisTransHospCode);
		queryParm.setData("FINISH_STATUS", "N");

		// ��ѯ���ͼ���걾���Ϻ�CSV����
		TParm result = MEDApplyTool.getInstance().queryExtrnalLisSampleData(
				queryParm);
		
		if (result.getErrCode() < 0) {
			System.out.println("��ѯ���ͼ���걾���Ϻ�CSV���ݴ���");
			return false;
		} else if (result.getCount() < 1) {
			return true;
		}

		int count = result.getCount();
		String tempPath = "C:\\JavaHisFile\\temp\\csv";
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// ɾ�����������ļ�
		FileUtils.getInstance().delAllFile(tempPath);
		file = new File(tempPath + File.separator + "export.csv");
		
		String header = "ҽԺ����,סԺ�����,��������,�Ա�,����,��������,����,����ʱ��,"
				+ "�ͼ�ҽ��,��������,�ٳ����,��Ŀ����,��Ŀ������,�걾��,�ͼ�����,����ʱ��,"
				+ "�걾����,��ע,�ϰ�����,������ϵ��ʽ,ҽ����ϵ��ʽ";
		List<String> dataList = new ArrayList<String>();
		dataList.add(header);
		
		// ���ݱ������ʾ˳������ƴ��ÿ��csv��ʽ����
		String[] nameArray = { "BAR_CODE", "APPLICATION_NO", "PAT_NAME", "SEX",
				"AGE", "SAMPLE_TYPE", "BED_NO_DESC", "ORDER_DATE",
				"ORDER_DR_DESC", "DEPT_DESC", "DIAGNOSIS", "ORDER_CODE",
				"ORDER_DESC", "SAMPLE_NO", "LIS_RE_DATE", "BLOOD_DATE",
				"SAMPLE_COUNT", "REMARKS", "DA_BAR_CODE", "PAT_TEL", "DR_TEL" };
		int nameArrayLength = nameArray.length;
		String dataStr = "";
		String tempStr = "";
		for (int j = 0; j < count; j++) {
			dataStr = "";
			for (int k = 0; k < nameArrayLength; k++) {
				tempStr = result.getValue(nameArray[k], j);
				// CSV��һ�����а������ŵĻ�����Ҫʹ��˫���Ž�������
				if (tempStr.contains(",")) {
					tempStr = "\"" + tempStr + "\"";
				}
				dataStr = dataStr + tempStr;
				if (k < nameArrayLength - 1) {
					dataStr = dataStr + ",";
				}
			}
			dataList.add(dataStr);
		}
		
		
		if (CSVUtils.getInstance().exportCsv(file, dataList)) {
			try {
				byte[] data = FileTool.getByte(file);
				TSocket socket = new TSocket(ip, port);
				Timestamp optTime = SystemTool.getInstance().getDate();
				if (TIOM_FileServer.writeFile(socket, sampleExportPath
						+ File.separator
						+ StringTool.getString(optTime, "yyyyMMddHHmmss")
						+ ".csv", data)) {
					// ����MED_APPLY��Ľ�����Ϣ
					this.updateLisReceiveData(result);
				} else {
					System.out.println("���걾CSV�ļ���"
							+ StringTool.getString(optTime, "yyyyMMddHHmmss")
							+ ".csv���ϴ�ʧ��");
					return false;
				}
			} catch (IOException e) {
				System.out.println("ȡ�����걾CSV�ļ����쳣");
				System.out.println(e.getMessage());
				return false;
			}
		} else {
			System.out.println("���걾CSV�ļ�����ʧ��");
		}

		return true;
	}
	
	/**
	 * ����MED_APPLY��Ľ�����Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private void updateLisReceiveData(TParm parm) {
		int count = parm.getCount();
		TParm result = new TParm();
		TParm updateParm = new TParm();
		
		StringBuffer errMsg = new StringBuffer();
		for (int i = 0; i < count; i++) {
			updateParm = new TParm();
			updateParm.setData("LIS_RE_USER", "DIAN");
			updateParm.setData("OPT_USER", "BATCH");
			updateParm.setData("OPT_TERM", "127.0.0.1");
			updateParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			updateParm.setData("APPLICATION_NO", parm.getValue("APPLICATION_NO", i));
			updateParm.setData("CAT1_TYPE", "LIS");
			// ����MED_APPLY��Ľ�����Ϣ
			result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(updateParm);
			
			if (result.getErrCode() < 0) {
				errMsg.append("������" + parm.getValue("PAT_NAME", i) + "�������Ϊ��"
						+ parm.getValue("APPLICATION_NO", i) + "���ı걾���ո���ʧ��");
				errMsg.append("\r\n");
				System.out.println("ERR:" + result.getErrText());
				continue;
			}
		}
		
		if (errMsg.toString().length() > 0) {
			System.out.println(errMsg.toString());
		}
	}
}
