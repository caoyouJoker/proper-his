package jdo.bms;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: ��Ѫʱ�䳬��4Сʱ֪ͨListener
 * </p>
 * 
 * <p>
 * Description: ��Ѫʱ�䳬��4Сʱ֪ͨListener
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
		event.getServletContext().log("ѪҺ���ⳬʱ���Ѷ�ʱ������");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// ������Ѫ�����߳�
		//System.out.println("---------������Ѫ����Listener-----------");
		BMSBloodOutConectionThread BMSThread = new BMSBloodOutConectionThread();
		Thread thread = new Thread(BMSThread);
		thread.start();
		event.getServletContext().log("ѪҺ���ⳬʱ���Ѷ�ʱ��������");
	}

}
