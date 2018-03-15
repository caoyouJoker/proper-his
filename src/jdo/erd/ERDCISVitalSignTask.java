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
 * Title: 急诊CIS体征监测数据采集Task
 * </p>
 * 
 * <p>
 * Description: 急诊CIS体征监测数据采集Task
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
	 * 数据采集
	 */
	public void run() {
    	System.out.println("--------急诊CIS线程启动--------");
		config = this.getProp();
		boolean updateFlg = false;
		String startPoolingTime = "";
		String endPoolingTime = "";
		String tempEndPoolingTime = "";
		TParm result = new TParm();
		// 集成接口日志数据
		TParm insertSysPatchLogParm = new TParm();
		// 从配置文件取得抓取CIS接口数据的间隔时间
		long periodTime = TypeTool.getLong(config.getString("CIS.ERDPeriodTime"));
		// 将毫秒转换为分钟
		periodTime = periodTime/60000;
		// 从配置文件取得抓取CIS接口数据的分批查询时间
		long bathcPeriodTime = TypeTool.getLong(config.getString("CIS.ERDBatchPeriodTime"));
		// 将毫秒转换为分钟
		bathcPeriodTime = bathcPeriodTime/60000;
		TParm updateParm = new TParm();
		String batchStatus = ""; 
		TParm parm = new TParm();
		TParm saveResult = new TParm();
		List<String> filterList = new ArrayList<String>();
		String keyStr = "";
		
		// 查询门诊参数档数据
		TParm queryParm = ERDCISVitalSignTool.getInstance().queryOPDSysparm();
		
		if (queryParm.getErrCode() < 0) {
			System.out.println("查询门诊参数档数据错误");
			updateFlg = false;
		}
		
		if (queryParm.getCount("START_POOLING_TIME") > 0) {
			// 开始时间
			startPoolingTime = queryParm.getValue("START_POOLING_TIME", 0)
					.replaceAll("-", "/").substring(0, 16);
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
					
					TParm queryOPDCISParm = new TParm();
					queryOPDCISParm.setData("START_POOLING_TIME", startPoolingTime);
					queryOPDCISParm.setData("END_POOLING_TIME", endPoolingTime);
					// 查询急诊接口数据
					result = ERDCISVitalSignTool.getInstance().queryERDCISData(queryOPDCISParm);
					
					if (result.getErrCode() < 0) {
						updateFlg = false;
						System.out.println("查询急诊接口数据错误");
					} else {
						updateFlg = true;
					}
					
					// 数据过滤处理:只取整分钟的数据，同一分钟若有不同秒数的数据，则取最靠近下一分钟的数据
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
					
					if (updateFlg) {
						parm = new TParm();
						parm.setData("INSERT", result.getData());
						parm.setData("UPDATE", updateParm.getData());
						
						// 执行保存操作
						saveResult = TIOM_AppServer.executeAction(socket,
								"action.erd.ERDCISVitalSignAction",
								"onInsertERDCISVitalSign", parm);
						
						if (saveResult.getErrCode() < 0) {
							System.out.println("急诊CIS批次执行错误");
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
						System.out.println("写入急诊CIS数据集成接口日志错误");
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
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		parm.setData("PATCH_CODE", "CIS04"
				+ df.format(new Date()).replaceAll("[^0-9]", "").substring(2,
						17));
		parm.setData("PATCH_DESC", "急诊体征监测数据集成");
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
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
