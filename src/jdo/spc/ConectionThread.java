package jdo.spc;

import java.util.Timer;
import java.util.concurrent.TimeUnit;
/**
 * <p>
 * Title: 物联网与HIS同步
 * </p>
 * 
 * <p>
 * Description: 物联网与HIS同步，定时器
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */

public class ConectionThread implements Runnable {

	private boolean isTry = true;
	
	public void run() {
		try {
			Thread.sleep(1000 * 60 * 15);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while(isTry){
			
			try{
				build();
				isTry = false;
			}catch (Exception e){
				isTry = true;
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			}
			
		}
		
	}

	public void build() throws Exception {
		
		//间隔1小时执行一次
		try{
			new Timer().schedule(new SYSDictionaryTask(), 0, SYSDictionaryConfig.getTime());
		}catch(Exception e){
		System.out.println("------统一代码客户端启动失败！---------");
			throw e;
		}
		

	}

}
