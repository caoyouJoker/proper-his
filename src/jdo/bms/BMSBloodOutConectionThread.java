package jdo.bms;

import java.util.Timer;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 输血时间超过4小时通知线程
 * </p>
 * 
 * <p>
 * Description: 输血时间超过4小时通知线程
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhanglei 2017.6.7
 * @version 1.0
 */
public class BMSBloodOutConectionThread implements Runnable {
	
	private TConfig prop;
	
	public BMSBloodOutConectionThread() {
	}
	
	/**
	 * 线程调用
	 */
	public void run() {
		//System.out.println("22222222222ConectionThreadRun");
		boolean execFlg = true;
		boolean sleepFlg = true;
		
		while (sleepFlg) {
			try {
				//System.out.println("22222222222222sleepFlg" + sleepFlg);
				prop = getProp();
				//System.out.println("222222222222得到延迟时间起 " + prop);
				// 从配置文件取得服务启动后线程延时启动时间
				long delayTime = TypeTool.getLong(prop.getString("BMS.BloodOutOvertime"));
				//System.out.println("1212121212121212");
				//System.out.println("222222222222得到延迟时间停 " + delayTime);
				Thread.sleep(delayTime);
				sleepFlg = false;
			} catch (InterruptedException ie) {
				System.out.println("-----血液出库超时提醒线程中断-----");
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
				//System.out.println("222222222222execFlg ");
				executeSchedule();
				//System.out.println("222222222222executeSchedule ");
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
			long periodTime = TypeTool.getLong(prop.getString("BMS.BloodOutOvertime1"));
			new Timer().schedule(BMSBloodOutTask.getInstance(), 0, periodTime);
			//System.out.println("2222222222executeSchedule进入 " + periodTime);
		} catch (Exception e) {
			System.out.println("------血液出库超时提醒线程启动失败！---------");
			throw e;
		}
	}
	
	/**
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		//System.out.println("2222222222TConfig");
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		//System.out.println("2222222222TConfig完成");
		return config;
	}
	
}
