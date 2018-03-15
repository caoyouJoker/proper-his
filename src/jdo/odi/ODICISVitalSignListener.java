package jdo.odi;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: 病区CIS体征监测数据采集Listener
 * </p>
 * 
 * <p>
 * Description: 病区CIS体征监测数据采集Listener
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
 * @author wangbin 2015.04.25
 * @version 1.0
 */
public class ODICISVitalSignListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().log("定时器销毁");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 启动ICU线程
		ODICISVitalSignConectionThread CISICUThread = new ODICISVitalSignConectionThread("ICU");
		Thread ICUThread = new Thread(CISICUThread);
		ICUThread.start();
		
		// 启动CCU线程
		ODICISVitalSignConectionThread CISCCUThread = new ODICISVitalSignConectionThread("CCU");
		Thread CCUThread = new Thread(CISCCUThread);
		CCUThread.start();
		
		// 启动WARD线程
		ODICISVitalSignConectionThread CISWARDThread = new ODICISVitalSignConectionThread("WARD");
		Thread WARDThread = new Thread(CISWARDThread);
		WARDThread.start();

		event.getServletContext().log("定时器已启用");
	}

}
