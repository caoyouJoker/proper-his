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
 * Title: 病区CIS体征监测数据采集Task
 * </p>
 * 
 * <p>
 * Description: 病区CIS体征监测数据采集Task
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
	 * 数据采集
	 */
	public void run() {
		//
		System.out.println("--------" + taskName + "CIS线程启动--------");
		config = this.getProp();
		boolean updateFlg = false;
		// 视图名
		String viewName = "";
		// 数据库名
		String dataBaseName = "";
		String startPoolingTime = "";
		String endPoolingTime = "";
		String tempEndPoolingTime = "";
		// 更新参数档抓取时间的方法名称
		String methodName = "";
		// 从配置文件取得抓取CIS接口数据的间隔时间
		long periodTime = TypeTool.getLong(config.getString("CIS.ODIPeriodTime"));
		// 将毫秒转换为分钟
		periodTime = periodTime/60000;
		// 从配置文件取得抓取CIS接口数据的分批查询时间
		long bathcPeriodTime = TypeTool.getLong(config.getString("CIS.ODIBatchPeriodTime"));
		// 将毫秒转换为分钟
		bathcPeriodTime = bathcPeriodTime/60000;
		TParm queryODICISParm = new TParm();
		// 集成接口日志数据
		TParm insertSysPatchLogParm = new TParm();
		TParm updateParm = new TParm();
		TParm parm = new TParm();
		String batchStatus = "";
		List<String> filterList = new ArrayList<String>();
		String keyStr = "";
		
		// 查询住院参数档数据
		TParm queryParm = ODICISVitalSignTool.getInstance().queryODISysparm();
		
		if (queryParm.getErrCode() < 0) {
			System.out.println("查询住院参数档数据错误");
			updateFlg = false;
		}
		
		// 根据不同线程初始化对应的数据库以及视图名
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
			System.out.println("-------数据库初始化错误------");
		}
		
		if (StringUtils.isNotEmpty(startPoolingTime)) {
			startPoolingTime = startPoolingTime.replaceAll("-", "/").substring(0, 16);
			// 以当前时间为结束时间
			endPoolingTime = SystemTool.getInstance().getDate().toString()
					.replaceAll("-", "/").substring(0, 16);
			tempEndPoolingTime = endPoolingTime;
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			
			try {
				// 为避免时间间隔过长导致访问数据超时，采取以配置文件的分批间隔时间为分隔界限分批捞取数据的方式
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
					// 查询接口数据
					TParm result = ODICISVitalSignTool.getInstance().queryODICISData(
							queryODICISParm, viewName, dataBaseName);
					
					if (result.getErrCode() < 0) {
						updateFlg = false;
						System.out.println("查询病区接口数据错误");
					} else {
						updateFlg = true;
					}
					
					// 数据过滤处理,避免主键冲突
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
					
					// 组装集成接口日志数据
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
						
						// 执行保存操作
						TParm saveResult = TIOM_AppServer.executeAction(socket,
								"action.odi.ODICISVitalSignAction",
								"onInsertODICISVitalSign", parm);
						
						if (saveResult.getErrCode() < 0) {
							System.out.println("病区CIS批次执行错误");
						}
						
						// 如果执行成功
						if (saveResult.getBoolean("SUCCESS_FLG")) {
							batchStatus = "成功";
						} else {
							batchStatus = "失败";
						}
					} else {
						batchStatus = "失败";
					}
					
					insertSysPatchLogParm.setData("PATCH_STATUS", batchStatus);
					// 组装集成日志数据
					this.setSysPatchLogParm(insertSysPatchLogParm);
					
					// 写入数据集成接口日志
					result = ODICISVitalSignTool.getInstance().insertSysPatchLog(insertSysPatchLogParm);
					
					if (result.getErrCode() < 0) {
						System.out.println("写入病区CIS数据集成接口日志错误");
					}
					
					// 执行完毕后将开始时间更新为本次截止时间
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
	 * 组装集成日志数据
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
		parm.setData("PATCH_DESC", "病区体征监测数据集成");
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
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
