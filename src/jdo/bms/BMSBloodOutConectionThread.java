package jdo.bms;

import java.util.Timer;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ��Ѫʱ�䳬��4Сʱ֪ͨ�߳�
 * </p>
 * 
 * <p>
 * Description: ��Ѫʱ�䳬��4Сʱ֪ͨ�߳�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhanglei 2017.6.7
 * @version 1.0
 */
public class BMSBloodOutConectionThread implements Runnable {
	
	private TConfig prop;
	
	public BMSBloodOutConectionThread() {
	}
	
	/**
	 * �̵߳���
	 */
	public void run() {
		//System.out.println("22222222222ConectionThreadRun");
		boolean execFlg = true;
		boolean sleepFlg = true;
		
		while (sleepFlg) {
			try {
				//System.out.println("22222222222222sleepFlg" + sleepFlg);
				prop = getProp();
				//System.out.println("222222222222�õ��ӳ�ʱ���� " + prop);
				// �������ļ�ȡ�÷����������߳���ʱ����ʱ��
				long delayTime = TypeTool.getLong(prop.getString("BMS.BloodOutOvertime"));
				//System.out.println("1212121212121212");
				//System.out.println("222222222222�õ��ӳ�ʱ��ͣ " + delayTime);
				Thread.sleep(delayTime);
				sleepFlg = false;
			} catch (InterruptedException ie) {
				System.out.println("-----ѪҺ���ⳬʱ�����߳��ж�-----");
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
				//System.out.println("222222222222execFlg ");
				executeSchedule();
				//System.out.println("222222222222executeSchedule ");
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
			long periodTime = TypeTool.getLong(prop.getString("BMS.BloodOutOvertime1"));
			new Timer().schedule(BMSBloodOutTask.getInstance(), 0, periodTime);
			//System.out.println("2222222222executeSchedule���� " + periodTime);
		} catch (Exception e) {
			System.out.println("------ѪҺ���ⳬʱ�����߳�����ʧ�ܣ�---------");
			throw e;
		}
	}
	
	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		//System.out.println("2222222222TConfig");
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		//System.out.println("2222222222TConfig���");
		return config;
	}
	
}
