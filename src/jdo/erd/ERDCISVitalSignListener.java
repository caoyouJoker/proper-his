package jdo.erd;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: 急诊CIS体征监测数据采集Listener
 * </p>
 * 
 * <p>
 * Description: 急诊CIS体征监测数据采集Listener
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
 * @author wangbin 2015.4.27
 * @version 1.0
 */
public class ERDCISVitalSignListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().log("定时器销毁");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 启动ICU线程
		ERDCISVitalSignConectionThread CISThread = new ERDCISVitalSignConectionThread();
		Thread thread = new Thread(CISThread);
		thread.start();

		event.getServletContext().log("定时器已启用");
	}

}
