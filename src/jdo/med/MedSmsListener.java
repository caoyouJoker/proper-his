package jdo.med;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MedSmsListener implements ServletContextListener {
	
	 
	MedSmsTimerTask task = null;
	public void contextInitialized(ServletContextEvent event){
		String configDir = event.getServletContext().getRealPath("/");  
		/**
		 * 启动定时器
		 * 在1秒后执行此任务,每次间隔30秒.
		 */
		 
		task =  new MedSmsTimerTask(configDir);
		task.doJob() ;
		event.getServletContext().log("定时器已启用");
	}
	
	public void contextDestroyed(ServletContextEvent event){
		/**
		 * 销毁定时器
		 */
		task.close();
		event.getServletContext().log("定时器销毁");
	}

}
