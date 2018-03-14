package jdo.erd;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: ����CIS����������ݲɼ�Listener
 * </p>
 * 
 * <p>
 * Description: ����CIS����������ݲɼ�Listener
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
		event.getServletContext().log("��ʱ������");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// ����ICU�߳�
		ERDCISVitalSignConectionThread CISThread = new ERDCISVitalSignConectionThread();
		Thread thread = new Thread(CISThread);
		thread.start();

		event.getServletContext().log("��ʱ��������");
	}

}
