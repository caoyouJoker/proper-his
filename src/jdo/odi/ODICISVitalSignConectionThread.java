package jdo.odi;

import java.util.Timer;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 病区CIS体征监测数据采集线程
 * </p>
 * 
 * <p>
 * Description: 病区CIS体征监测数据采集线程
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
public class ODICISVitalSignConectionThread implements Runnable {
	
	private TConfig prop;
	private String threadName;
	
	public ODICISVitalSignConectionThread(String threadName) {
		this.threadName = threadName;
	}
	
	/**
	 * 线程调用
	 */
	public void run() {
		boolean execFlg = true;
		boolean sleepFlg = true;
		
		while (sleepFlg) {
			try {
				prop = getProp();
				// 从配置文件取得服务启动后线程延时启动时间
				long delayTime = TypeTool.getLong(prop.getString("CIS.ODIDelayTime"));
				Thread.sleep(delayTime);
				sleepFlg = false;
			} catch (InterruptedException ie) {
				System.out.println("-----线程中断-----");
				ie.printStackTrace();
			} catch (NullPointerException nle) {
				try {
					// 如果执行发生异常则1分钟之后再执行
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (execFlg) {
			try {
				executeSchedule();
				execFlg = false;
			} catch (Exception e) {
				execFlg = true;
				try {
					// 如果执行发生异常则1分钟之后再执行
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 定时任务执行
	 */
	public void executeSchedule() throws Exception {
		try {
			// 从配置文件取得定时间隔时间
			long periodTime = TypeTool.getLong(prop.getString("CIS.ODIPeriodTime"));
			new Timer().schedule(ODICISVitalSignTask.getInstance(threadName), 0, periodTime);
		} catch (Exception e) {
			System.out.println("------病区CIS体征监测数据采集线程启动失败！---------");
			throw e;
		}
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
