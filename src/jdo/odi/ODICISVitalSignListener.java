package jdo.odi;

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
 * Company: ProperSoft
 * </p>
 * 
 * @author wangbin 2015.04.25
 * @version 1.0
 */
public class ODICISVitalSignListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().log("��ʱ������");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// ����ICU�߳�
		ODICISVitalSignConectionThread CISICUThread = new ODICISVitalSignConectionThread("ICU");
		Thread ICUThread = new Thread(CISICUThread);
		ICUThread.start();
		
		// ����CCU�߳�
		ODICISVitalSignConectionThread CISCCUThread = new ODICISVitalSignConectionThread("CCU");
		Thread CCUThread = new Thread(CISCCUThread);
		CCUThread.start();
		
		// ����WARD�߳�
		ODICISVitalSignConectionThread CISWARDThread = new ODICISVitalSignConectionThread("WARD");
		Thread WARDThread = new Thread(CISWARDThread);
		WARDThread.start();

		event.getServletContext().log("��ʱ��������");
	}

}
