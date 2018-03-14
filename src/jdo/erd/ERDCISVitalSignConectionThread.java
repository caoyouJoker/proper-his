package jdo.erd;

import java.util.Timer;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ����CIS����������ݲɼ��߳�
 * </p>
 * 
 * <p>
 * Description: ����CIS����������ݲɼ��߳�
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
 * @author wangbin 2015.04.25
 * @version 1.0
 */
public class ERDCISVitalSignConectionThread implements Runnable {
	
	private TConfig prop;
	
	public ERDCISVitalSignConectionThread() {
	}
	
	/**
	 * �̵߳���
	 */
	public void run() {
		boolean execFlg = true;
		boolean sleepFlg = true;
		
		while (sleepFlg) {
			try {
				prop = getProp();
				// �������ļ�ȡ�÷����������߳���ʱ����ʱ��
				long delayTime = TypeTool.getLong(prop.getString("CIS.ERDDelayTime"));
				Thread.sleep(delayTime);
				sleepFlg = false;
			} catch (InterruptedException ie) {
				System.out.println("-----�߳��ж�-----");
				ie.printStackTrace();
			} catch (NullPointerException nle) {
				try {
					// ���ִ�з����쳣��1����֮����ִ��
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (execFlg) {
			try {
				executeSchedule();
				execFlg = false;
			} catch (Exception e) {
				execFlg = true;
				try {
					// ���ִ�з����쳣��1����֮����ִ��
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��ʱ����ִ��
	 */
	public void executeSchedule() throws Exception {
		try {
			// �������ļ�ȡ�ö�ʱ���ʱ��
			long periodTime = TypeTool.getLong(prop.getString("CIS.ERDPeriodTime"));
			new Timer().schedule(ERDCISVitalSignTask.getInstance(), 0, periodTime);
		} catch (Exception e) {
			System.out.println("------����CIS����������ݲɼ��߳�����ʧ�ܣ�---------");
			throw e;
		}
	}
	
	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
