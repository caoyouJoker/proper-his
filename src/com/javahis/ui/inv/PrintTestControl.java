package com.javahis.ui.inv;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.util.RFIDPrintUtils;

/**
 * 
 * @author lixiang
 *
 */
public class PrintTestControl  extends TControl implements MessageListener{
	
	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	ConnectionFactory connectionFactory;
	// Connection ：JMS 客户端到JMS Provider 的连接
	Connection connection = null;
	// Session： 一个发送或接收消息的线程
	Session session;
	//
	MessageConsumer consumer;
	
	

	
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
    }
    
    /**
     * 打印测试
     */
    public void onPrint() {
    	TParm parm = new TParm(); 

		parm.setData(RFIDPrintUtils.PARM_NAME, "螺旋式支架");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "4*4");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013-06-30");

		// 十进制
		parm.setData(RFIDPrintUtils.PARM_CODE, "130530000002");

		// 需要转化(看一下应用制作模版是否可以设置)
		// 十六进制
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130530000002");

		// 单个打印
		RFIDPrintUtils.send2LPT(parm);
    }
    
    /**
     * 打印测试(多)
     */
    public void  onPrintM(){
    	TParm parm = new TParm();
    	//初始值
    	for(int i=10;i<=19;i++){
	    	// 十六进制
    		String s1="1305300000"+i;
    		System.out.println("--s1--"+s1);
			parm.setData(RFIDPrintUtils.PARM_PRFID, s1);
			this.messageBox("--2秒后执行--");
			// 单个打印
			RFIDPrintUtils.send2LPT(parm);	
			try {
				System.out.println("--等待2秒-");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch blockr
				e.printStackTrace();
			}
			
    	}
    	
    	/*TParm parm = new TParm();

		parm.setData(RFIDPrintUtils.PARM_NAME, "品名测试2");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "规格测试215*15");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/14");

		// 十进制
		parm.setData(RFIDPrintUtils.PARM_CODE, "130104000002");

		// 需要转化(看一下应用制作模版是否可以设置)
		// 十六进制
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000002");

		// 单个打印
		RFIDPrintUtils.send2LPT(parm);*/
		
		//2.----------------------------------------------------
		//TParm parm = new TParm();

/*		parm.setData(RFIDPrintUtils.PARM_NAME, "品名测试3");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "规格测试3 15*15");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/15");

		// 十进制
		parm.setData(RFIDPrintUtils.PARM_CODE, "130104000003");

		// 需要转化(看一下应用制作模版是否可以设置)
		// 十六进制
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000003");

		// 单个打印
		RFIDPrintUtils.send2LPT(parm);*/
    	
    }	
    
    
    public void onStart(){
    	
    	this.connectionFactory = connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://127.0.0.1:61616");
        try {
			this.connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			
			ActiveMQQueue topic = new ActiveMQQueue("FirstQueue");
			this.consumer=this.session.createConsumer(topic, "2 > 1", false);//
			
			//开始监听
			//多个消费者     同时临听  （需要多个时测试）
			this.consumer.setMessageListener(this);
			
			System.out.println("--------启动成功----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------启动失败----------");
		};
    	
    }
    
    
    public void onEnd(){
   	
    	try {
    		if (consumer != null)   consumer.close();  
    		if (session != null)  this.session.close();
    		if (connection != null) this.connection.close();
			System.out.println("--------断开成功----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------断开失败----------");
		}
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintTestControl  p=new PrintTestControl();
		 p.onPrintM();
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				System.out.println("Consumer:->Received: " + msg);
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
