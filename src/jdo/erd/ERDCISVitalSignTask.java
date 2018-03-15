package jdo.erd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import jdo.odi.ODICISVitalSignTool;
import jdo.sys.SystemTool;

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
 * Company: ProperSoft
 * </p>
 * 
 * @author wangbin 2015.4.27
 * @version 1.0
 */
public class ERDCISVitalSignTask extends TimerTask {
	
	private TSocket socket = new TSocket("127.0.0.1", 8080, "web");
	private TConfig config;
	

	private static ERDCISVitalSignTask instance = new ERDCISVitalSignTask();
	
	private ERDCISVitalSignTask(){
	}
	
	public static ERDCISVitalSignTask getInstance() {
		return instance;
	}
	

	/**
	 * ���ݲɼ�
	 */
	public void run() {
    	System.out.println("--------����CIS�߳�����--------");
		config = this.getProp();
		boolean updateFlg = false;
		String startPoolingTime = "";
		String endPoolingTime = "";
		String tempEndPoolingTime = "";
		TParm result = new TParm();
		// ���ɽӿ���־����
		TParm insertSysPatchLogParm = new TParm();
		// �������ļ�ȡ��ץȡCIS�ӿ����ݵļ��ʱ��
		long periodTime = TypeTool.getLong(config.getString("CIS.ERDPeriodTime"));
		// ������ת��Ϊ����
		periodTime = periodTime/60000;
		// �������ļ�ȡ��ץȡCIS�ӿ����ݵķ�����ѯʱ��
		long bathcPeriodTime = TypeTool.getLong(config.getString("CIS.ERDBatchPeriodTime"));
		// ������ת��Ϊ����
		bathcPeriodTime = bathcPeriodTime/60000;
		TParm updateParm = new TParm();
		String batchStatus = ""; 
		TParm parm = new TParm();
		TParm saveResult = new TParm();
		List<String> filterList = new ArrayList<String>();
		String keyStr = "";
		
		// ��ѯ�������������
		TParm queryParm = ERDCISVitalSignTool.getInstance().queryOPDSysparm();
		
		if (queryParm.getErrCode() < 0) {
			System.out.println("��ѯ������������ݴ���");
			updateFlg = false;
		}
		
		if (queryParm.getCount("START_POOLING_TIME") > 0) {
			// ��ʼʱ��
			startPoolingTime = queryParm.getValue("START_POOLING_TIME", 0)
					.replaceAll("-", "/").substring(0, 16);
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
					
					TParm queryOPDCISParm = new TParm();
					queryOPDCISParm.setData("START_POOLING_TIME", startPoolingTime);
					queryOPDCISParm.setData("END_POOLING_TIME", endPoolingTime);
					// ��ѯ����ӿ�����
					result = ERDCISVitalSignTool.getInstance().queryERDCISData(queryOPDCISParm);
					
					if (result.getErrCode() < 0) {
						updateFlg = false;
						System.out.println("��ѯ����ӿ����ݴ���");
					} else {
						updateFlg = true;
					}
					
					// ���ݹ��˴���:ֻȡ�����ӵ����ݣ�ͬһ�������в�ͬ���������ݣ���ȡ�����һ���ӵ�����
					filterList = new ArrayList<String>();
					keyStr = "";
					if (updateFlg) {
						int count = result.getCount("BED_NO");
						for (int i = 0; i< count; i++) {
							keyStr = result.getValue("BED_NO", i) + "_"
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
					
					if (updateFlg) {
						parm = new TParm();
						parm.setData("INSERT", result.getData());
						parm.setData("UPDATE", updateParm.getData());
						
						// ִ�б������
						saveResult = TIOM_AppServer.executeAction(socket,
								"action.erd.ERDCISVitalSignAction",
								"onInsertERDCISVitalSign", parm);
						
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
						System.out.println("д�뼱��CIS���ݼ��ɽӿ���־����");
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
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		parm.setData("PATCH_CODE", "CIS04"
				+ df.format(new Date()).replaceAll("[^0-9]", "").substring(2,
						17));
		parm.setData("PATCH_DESC", "��������������ݼ���");
		parm.setData("PATCH_SRC", "jdo.erd.ERDCISVitalSignListener");
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
