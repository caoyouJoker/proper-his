package jdo.odi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ����CIS����������ݲɼ�Task
 * </p>
 * 
 * <p>
 * Description: ����CIS����������ݲɼ�Task
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangbin 2015.04.25
 * @version 1.0
 */
public class ODICISVitalSignTask extends TimerTask {
	
	private TSocket socket = new TSocket("127.0.0.1", 8080, "web");
	private TConfig config;
	private String taskName;

	private static ODICISVitalSignTask instanceICU;
	private static ODICISVitalSignTask instanceCCU;
	private static ODICISVitalSignTask instanceWARD;

	private ODICISVitalSignTask() {
	}

	private ODICISVitalSignTask(String taskName) {
		this.taskName = taskName;
	}


	public static synchronized ODICISVitalSignTask getInstance(String taskName) {
		if (taskName.equals("ICU")) {
			if (instanceICU == null) {
				instanceICU = new ODICISVitalSignTask("ICU");
			}
			return instanceICU;
		}else if(taskName.equals("CCU")){
			if (instanceCCU == null) {
				instanceCCU = new ODICISVitalSignTask("CCU");
			}
			return instanceCCU;
						
		}else{
			if (instanceWARD == null) {
				instanceWARD = new ODICISVitalSignTask("WARD");
			}
			return instanceWARD;
		}

	}
	

	/**
	 * ���ݲɼ�
	 */
	public void run() {
		//
		System.out.println("--------" + taskName + "CIS�߳�����--------");
		config = this.getProp();
		boolean updateFlg = false;
		// ��ͼ��
		String viewName = "";
		// ���ݿ���
		String dataBaseName = "";
		String startPoolingTime = "";
		String endPoolingTime = "";
		String tempEndPoolingTime = "";
		// ���²�����ץȡʱ��ķ�������
		String methodName = "";
		// �������ļ�ȡ��ץȡCIS�ӿ����ݵļ��ʱ��
		long periodTime = TypeTool.getLong(config.getString("CIS.ODIPeriodTime"));
		// ������ת��Ϊ����
		periodTime = periodTime/60000;
		// �������ļ�ȡ��ץȡCIS�ӿ����ݵķ�����ѯʱ��
		long bathcPeriodTime = TypeTool.getLong(config.getString("CIS.ODIBatchPeriodTime"));
		// ������ת��Ϊ����
		bathcPeriodTime = bathcPeriodTime/60000;
		TParm queryODICISParm = new TParm();
		// ���ɽӿ���־����
		TParm insertSysPatchLogParm = new TParm();
		TParm updateParm = new TParm();
		TParm parm = new TParm();
		String batchStatus = "";
		List<String> filterList = new ArrayList<String>();
		String keyStr = "";
		
		// ��ѯסԺ����������
		TParm queryParm = ODICISVitalSignTool.getInstance().queryODISysparm();
		
		if (queryParm.getErrCode() < 0) {
			System.out.println("��ѯסԺ���������ݴ���");
			updateFlg = false;
		}
		
		// ���ݲ�ͬ�̳߳�ʼ����Ӧ�����ݿ��Լ���ͼ��
		if (StringUtils.equals("ICU", taskName)) {
			viewName = config.getString("CIS.ICU_VIEW_NAME");
			dataBaseName = "javahisICU";
			startPoolingTime = queryParm.getValue("ICU_SPOOL_TIME", 0);
			methodName = "updateODISysparmByICU";
		} else if (StringUtils.equals("CCU", taskName)) {
			viewName = config.getString("CIS.CCU_VIEW_NAME");
			dataBaseName = "javahisCCU";
			startPoolingTime = queryParm.getValue("CCU_SPOOL_TIME", 0);
			methodName = "updateODISysparmByCCU";
		} else if (StringUtils.equals("WARD", taskName)) {
			viewName = config.getString("CIS.WARD_VIEW_NAME");
			dataBaseName = "javahisWard";
			startPoolingTime = queryParm.getValue("WARD_SPOOL_TIME", 0);
			methodName = "updateODISysparmByWARD";
		} else {
			System.out.println("-------���ݿ��ʼ������------");
		}
		
		if (StringUtils.isNotEmpty(startPoolingTime)) {
			startPoolingTime = startPoolingTime.replaceAll("-", "/").substring(0, 16);
			// �Ե�ǰʱ��Ϊ����ʱ��
			endPoolingTime = SystemTool.getInstance().getDate().toString()
					.replaceAll("-", "/").substring(0, 16);
			tempEndPoolingTime = endPoolingTime;
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			
			try {
				// Ϊ����ʱ�����������·������ݳ�ʱ����ȡ�������ļ��ķ������ʱ��Ϊ�ָ����޷�����ȡ���ݵķ�ʽ
				Date d1 = df.parse(startPoolingTime);
				Date d2 = df.parse(endPoolingTime);
				double diff = d2.getTime() - d1.getTime();
				int days = (int) Math.floor(diff / (1000 * 60 * bathcPeriodTime));
				if (days == 0) {
					days = 1;
				}
				Date date = new Date();
				
				for (int j = 1; j <= days; j++) {
					if (j == days) {
						date = DateUtils.addMinutes(StringTool.getDate(
								tempEndPoolingTime, "yyyy/MM/dd HH:mm"),
								TypeTool.getInt(-1));
						endPoolingTime = StringTool.getString(date,
								"yyyy/MM/dd HH:mm");
					} else {
						date = DateUtils.addMinutes(StringTool.getDate(
								startPoolingTime, "yyyy/MM/dd HH:mm"), TypeTool
								.getInt(bathcPeriodTime - 1));
						endPoolingTime = StringTool.getString(date,
								"yyyy/MM/dd HH:mm");
					}
					
					queryODICISParm = new TParm();
					queryODICISParm.setData("START_POOLING_TIME", startPoolingTime);
					queryODICISParm.setData("END_POOLING_TIME", endPoolingTime);
					// ��ѯ�ӿ�����
					TParm result = ODICISVitalSignTool.getInstance().queryODICISData(
							queryODICISParm, viewName, dataBaseName);
					
					if (result.getErrCode() < 0) {
						updateFlg = false;
						System.out.println("��ѯ�����ӿ����ݴ���");
					} else {
						updateFlg = true;
					}
					
					// ���ݹ��˴���,����������ͻ
					filterList = new ArrayList<String>();
					keyStr = "";
					if (updateFlg) {
						int count = result.getCount("CASE_NO");
						for (int i = 0; i< count; i++) {
							keyStr = result.getValue("CASE_NO", i) + "_"
									+ result.getValue("MONITOR_TIME", i) + "_"
									+ result.getValue("MONITOR_ITEM_EN", i);
							
							if (!filterList.contains(keyStr)) {
								filterList.add(keyStr);
								result.setData("INSERT_FLG", i, "Y");
							} else {
								result.setData("INSERT_FLG", i, "N");
							}
						}
					}
					
					// ��װ���ɽӿ���־����
					insertSysPatchLogParm = new TParm();
					insertSysPatchLogParm.setData("PATCH_START_DATE",
							startPoolingTime.replaceAll("/", "").replaceAll(
									":", "").replaceAll(" ", "").substring(0,
									12)
									+ "00");
					insertSysPatchLogParm.setData("PATCH_END_DATE",
							endPoolingTime.replaceAll("/", "").replaceAll(":",
									"").replaceAll(" ", "").substring(0, 12)
									+ "59");

					updateParm = new TParm();
					date = DateUtils.addMinutes(StringTool.getDate(
							endPoolingTime, "yyyy/MM/dd HH:mm"), TypeTool
							.getInt(1));
					updateParm.setData("START_POOLING_TIME", StringTool
							.getString(date, "yyyy/MM/dd HH:mm").replaceAll(
									"/", "").replaceAll(":", "").replaceAll(
									" ", "").substring(0, 12));
					date = DateUtils.addMinutes(StringTool.getDate(
							tempEndPoolingTime, "yyyy/MM/dd HH:mm"), TypeTool
							.getInt(periodTime - 1));
					updateParm.setData("END_POOLING_TIME", StringTool
							.getString(date, "yyyy/MM/dd HH:mm").replaceAll(
									"/", "").replaceAll(":", "").replaceAll(
									" ", "").substring(0, 12));
					updateParm.setData("METHOD_NAME", methodName);
					
					if (updateFlg) {
						parm = new TParm();
						parm.setData("INSERT", result.getData());
						parm.setData("UPDATE", updateParm.getData());
						
						// ִ�б������
						TParm saveResult = TIOM_AppServer.executeAction(socket,
								"action.odi.ODICISVitalSignAction",
								"onInsertODICISVitalSign", parm);
						
						if (saveResult.getErrCode() < 0) {
							System.out.println("����CIS����ִ�д���");
						}
						
						// ���ִ�гɹ�
						if (saveResult.getBoolean("SUCCESS_FLG")) {
							batchStatus = "�ɹ�";
						} else {
							batchStatus = "ʧ��";
						}
					} else {
						batchStatus = "ʧ��";
					}
					
					insertSysPatchLogParm.setData("PATCH_STATUS", batchStatus);
					// ��װ������־����
					this.setSysPatchLogParm(insertSysPatchLogParm);
					
					// д�����ݼ��ɽӿ���־
					result = ODICISVitalSignTool.getInstance().insertSysPatchLog(insertSysPatchLogParm);
					
					if (result.getErrCode() < 0) {
						System.out.println("д�벡��CIS���ݼ��ɽӿ���־����");
					}
					
					// ִ����Ϻ󽫿�ʼʱ�����Ϊ���ν�ֹʱ��
					date = DateUtils.addMinutes(StringTool.getDate(
							endPoolingTime, "yyyy/MM/dd HH:mm"), TypeTool
							.getInt(1));
					startPoolingTime = StringTool.getString(date,
							"yyyy/MM/dd HH:mm");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��װ������־����
	 */
	private void setSysPatchLogParm(TParm parm) {
		String batchCode = "";
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		String dateStr = df.format(new Date()).replaceAll("[^0-9]", "")
				.substring(2, 17);
		if (StringUtils.equals("ICU", taskName)) {
			batchCode = "CIS01" + dateStr;
		} else if (StringUtils.equals("CCU", taskName)) {
			batchCode = "CIS02" + dateStr;
		} else if (StringUtils.equals("WARD", taskName)) {
			batchCode = "CIS03" + dateStr;
		}
		parm.setData("PATCH_CODE", batchCode);
		parm.setData("PATCH_DESC", "��������������ݼ���");
		parm.setData("PATCH_SRC", "jdo.odi.ODICISVitalSignListener");
		parm.setData("PATCH_TYPE", "3");
		parm.setData("PATCH_REOMIT_COUNT", "");
		parm.setData("PATCH_REOMIT_INTERVAL", "");
		parm.setData("PATCH_REOMIT_POINT", "");
		parm.setData("PATCH_REOMIT_INDEX", "");
		parm.setData("PATCH_MESSAGE", "");
		parm.setData("SERVER_IP", "127.0.0.1");
		parm.setData("OPT_USER", "BLUECORE");
		parm.setData("OPT_TERM", "127.0.0.1");
	}
	
	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
