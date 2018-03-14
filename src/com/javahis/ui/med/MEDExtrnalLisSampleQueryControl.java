package com.javahis.ui.med;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import jdo.med.MEDApplyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.CSVUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.FileUtils;

/**
 * <p>
 * Title: ����������ݲ�ѯ
 * </p>
 * 
 * <p>
 * Description: ����������ݲ�ѯ
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
 * @author wangb 2017.2.24
 * @version 1.0
 */
public class MEDExtrnalLisSampleQueryControl extends TControl {

	private TTable table;
	private String lisTransHospCode;// ���Ժ��
	private String ip;// �ļ�����������
	private int port;// �ļ������˿�
	private String sampleExportPath;// �걾����·��

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		table = ((TTable) getComponent("TABLE"));
		lisTransHospCode = TConfig.getSystemValue("LIS_TRANS_HOSP_CODE");
		if (StringUtils.isEmpty(lisTransHospCode)) {
			this.messageBox("�����ļ���δ�������ͼ���Ժ������");
			return;
		}
		ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");
		if (StringUtils.isEmpty(ip)) {
			this.messageBox("�����ļ���δ�������ͼ������ݽ���������IP");
			return;
		}
		if (StringUtils.isEmpty(TConfig
				.getSystemValue("EXTRNAL_LIS.SERVER_PORT"))) {
			this.messageBox("�����ļ���δ�������ͼ������ݽ����������˿ں�");
			return;
		} else {
			port = StringTool.getInt(TConfig
					.getSystemValue("EXTRNAL_LIS.SERVER_PORT"));
		}
		sampleExportPath = TConfig.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_PATH");
		if (StringUtils.isEmpty(sampleExportPath)) {
			this.messageBox("�����ļ���δ�������ͼ���걾����CSV����·��");
			return;
		}
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", Timestamp.valueOf(today.toString()
				.substring(0, 10)
				+ " 00:00:00"));
		this.setValue("END_DATE", today);
		getRadioButton("ADM_TYPE_H").setSelected(true);
		getRadioButton("UNFINISHED").setSelected(true);
		table.setParmValue(new TParm());
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String admType = "";

		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			this.messageBox("��ֹ���ڲ���Ϊ��");
			return;
		}

		startDate = startDate.substring(0, 19).replace("-", "/");
		endDate = endDate.substring(0, 19).replace("-", "/");

		if (getRadioButton("ADM_TYPE_H").isSelected()) {
			admType = "H";
		} else if (getRadioButton("ADM_TYPE_O").isSelected()) {
			admType = "O";
		} else if (getRadioButton("ADM_TYPE_I").isSelected()) {
			admType = "I";
		}

		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", startDate);
		queryParm.setData("END_DATE", endDate);
		queryParm.setData("ADM_TYPE", admType);
		queryParm.setData("CAT1_TYPE", "LIS");
		queryParm.setData("TRANS_HOSP_CODE", lisTransHospCode);
		// ���״̬
		if (getRadioButton("UNFINISHED").isSelected()) {
			queryParm.setData("FINISH_STATUS", "N");
		} else {
			queryParm.setData("FINISH_STATUS", "Y");
		}
		
		// ��ѯ���ͼ���걾���Ϻ�CSV����
		TParm result = MEDApplyTool.getInstance().queryExtrnalLisSampleData(
				queryParm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ������������쳣");
			System.out.println(result.getErrText());
			return;
		}
		
		int count = result.getCount();
		if (count < 1) {
			this.messageBox("��������");
			return;
		}
		
		table.setParmValue(result);
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "���ͼ�������");
	}

	/**
	 * CSV�ļ��ϴ�
	 */
	public void onUpLoad() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ�ϴ�������");
			return;
		}

		String tempPath = "C:\\JavaHisFile\\temp\\csv";
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// ɾ�����������ļ�
		FileUtils.getInstance().delAllFile(tempPath);
		file = new File(tempPath + File.separator + "export.csv");
		if (CSVUtils.getInstance().exportCsv(file, table)) {
			try {
				byte[] data = FileTool.getByte(file);
				TSocket socket = new TSocket(ip, port);
				Timestamp optTime = SystemTool.getInstance().getDate();
				if (TIOM_FileServer.writeFile(socket, sampleExportPath
						+ File.separator
						+ StringTool.getString(optTime, "yyyyMMddHHmmss")
						+ ".csv", data)) {
					this.messageBox("�ϴ��ɹ�");
					if (getRadioButton("UNFINISHED").isSelected()) {
						// ����MED_APPLY��Ľ�����Ϣ
						this.updateLisReceiveData(parm);
					}
				} else {
					this.messageBox("�ϴ�ʧ��");
					return;
				}
			} catch (IOException e) {
				this.messageBox("ȡ�����걾CSV�ļ����쳣");
				System.out.println(e.getMessage());
				return;
			}
		} else {
			this.messageBox("CSV�ļ�����ʧ��");
		}
	}

	/**
	 * ����MED_APPLY��Ľ�����Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private void updateLisReceiveData(TParm parm) {
		int count = parm.getCount();
		String user = Operator.getID();
		String term = Operator.getIP();
		TParm result = new TParm();
		TParm updateParm = new TParm();

		StringBuffer errMsg = new StringBuffer();
		for (int i = 0; i < count; i++) {
			updateParm = new TParm();
			updateParm.setData("LIS_RE_USER", "DIAN");
			updateParm.setData("OPT_USER", user);
			updateParm.setData("OPT_TERM", term);
			updateParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			updateParm.setData("APPLICATION_NO", parm.getValue(
					"APPLICATION_NO", i));
			updateParm.setData("CAT1_TYPE", "LIS");
			// ����MED_APPLY��Ľ�����Ϣ
			result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(
					updateParm);

			if (result.getErrCode() < 0) {
				errMsg.append("������" + parm.getValue("PAT_NAME", i) + "�������Ϊ��"
						+ parm.getValue("APPLICATION_NO", i) + "���ı걾���ո���ʧ��");
				errMsg.append("\r\n");
				err("ERR:" + result.getErrText());
				continue;
			}
		}

		if (errMsg.toString().length() > 0) {
			this.messageBox(errMsg.toString());
		}
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

}
