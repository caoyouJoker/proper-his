package jdo.bms;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: 输血时间超过4小时通知Listener
 * </p>
 * 
 * <p>
 * Description: 输血时间超过4小时通知Listener
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhanglei 2017.6.7
 * @version 1.0
 */
public class BMSBloodOutListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().log("血液出库超时提醒定时器销毁");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 启动输血监听线程
		//System.out.println("---------启动输血监听Listener-----------");
		BMSBloodOutConectionThread BMSThread = new BMSBloodOutConectionThread();
		Thread thread = new Thread(BMSThread);
		thread.start();
		event.getServletContext().log("血液出库超时提醒定时器已启用");
	}

}
