package com.javahis.util;

//import com.alien.enterpriseRFID.notify.Message;
import java.net.InetAddress;

import com.alien.enterpriseRFID.notify.MessageListener;
import com.alien.enterpriseRFID.notify.MessageListenerService;
import com.alien.enterpriseRFID.reader.AlienClass1Reader;

public abstract class AlienRFIDUtil implements MessageListener{
	
	/**
	 * 启动开关
	 */
	private boolean open=true;
	/**
	 * 对应设备IP
	 */
	private String ip="";
	
	private int port=23;

	/**
	 * 判断是否打开
	 * @return
	 */
	public synchronized boolean isOpen() {
		return open;
	}
	/**
	 * 开关设置
	 * @param open
	 */
	public synchronized void setOpen(boolean open) {
		this.open = open;
	}
	
	public synchronized String getIp() {
		return ip;
	}
	public synchronized void setIp(String ip) {
		this.ip = ip;
	}
	
	public synchronized int getPort() {
		return port;
	}
	public synchronized void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * 
	 */
	
	public AlienRFIDUtil(String ip, int port){
		this.ip = ip;
		this.port = port;
		/*try {
			startMessageListener(4000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/**
	 * 关闭扫描
	 */
	public void doColse(){
		this.setOpen(false);
	}
	/**
	 * 
	 * @throws Exception
	 */
	public void startMessageListener(int port) throws Exception{
		System.out.println("---------startMessageListener----------");
		MessageListenerService service = new MessageListenerService(port);
		  service.setMessageListener(this);
		  service.startService();
		  System.out.println("Message Listener has Started");

		  // Instantiate a new reader object, and open a connection to it on COM1
		  //AlienClass1Reader reader = new AlienClass1Reader("COM1");
		  //"192.168.1.100"  23
		  System.out.println("RFID设备IP"+this.getIp());
		  System.out.println("RFID设备port"+this.getPort());
		  AlienClass1Reader reader =new AlienClass1Reader(this.getIp(), this.getPort());
		  
		  //reader.setAntennaSequence("0");
		  //reader.setAntennaSequence("1");
		  reader.open();
		  System.out.println("Configuring Reader");
		  
		  System.out.println("通知 ip:"+InetAddress.getLocalHost().getHostAddress());
		  System.out.println("通知  port:"+service.getListenerPort());
		  
		  reader.setNotifyAddress(InetAddress.getLocalHost().getHostAddress(), service.getListenerPort());
		  reader.setNotifyFormat(AlienClass1Reader.XML_FORMAT); // Make sure service can decode it.
		  reader.setNotifyTrigger("TrueFalse"); // Notify whether there's a tag or not
		  reader.setNotifyMode(AlienClass1Reader.ON);

		  // Set up AutoMode
		  reader.autoModeReset();
		  reader.setAutoStopTimer(1000); // Read for 1 second
		  reader.setAutoMode(AlienClass1Reader.ON);

		  // Close the connection and spin while messages arrive
		  reader.close();
		  //long runTime = 10000; // milliseconds
		  //long startTime = System.currentTimeMillis();
		  do {
		    Thread.sleep(1500);
		    
		  } while(service.isRunning() && this.isOpen());
		  
		  //停止服务
		  service.stopService();		  
		  // Reconnect to the reader and turn off AutoMode and TagStreamMode.
		  System.out.println("\nResetting Reader");
		  reader.open();
		  reader.autoModeReset();
		  reader.setNotifyMode(AlienClass1Reader.OFF);
		  reader.close();
		
	}
	

/*	@Override
	public void messageReceived(Message arg0) {
		// TODO Auto-generated method stub
		
	}*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
