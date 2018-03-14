package jdo.spc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: 物联网与HIS同步
 * </p>
 * 
 * <p>
 * Description: 物联网与HIS同步,监听服务器启动，就开始启动线程
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */
public class SYSDictionaryListener implements ServletContextListener {
	SYSDictionaryTask task = null;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		sce.getServletContext().log("定时器销毁");

	}

	@Override
	//服务器启动，就开始执行此方法
	public void contextInitialized(ServletContextEvent sce) {
		//启动线程
		ConectionThread thread = new ConectionThread();
		Thread aa = new Thread(thread);
		aa.start();

		sce.getServletContext().log("定时器已启用");
	}

}
