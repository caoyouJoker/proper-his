package com.javahis.ui.spc;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
			
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCSQL;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title:智能柜药品统计查询control
 * </p>
 *  
 * <p>
 * Description: 智能柜药品统计查询control
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liyh 20121110
 * @version 1.0
 */
public class SPCContainerStaticControl extends TControl implements MessageListener{
	TTable table_N_L;
	TTable table_N_R;
	TTable table_Y_L;
	TTable table_Y_R;
	TTable TABLE_CHECK;

	TPanel N_PANEL;
	TPanel Y_PANEL;
	String boxEslId;// 周转箱
	TParm parm1 = new TParm();
	String CABINET_ID = "";
	int k = 0;
	
	private Timer timer;

	private TimerTask task; // RFID任务
	
	private long period = 1 * 1000; // 间隔时间

	private long delay = 100; // 延迟时间	
	
	private TParm tparm=new TParm();
	
	private String ip = ""; // 智能柜IP
	
	String cabinetId = "";
	
	private String MQ_QUEUE_NAME = "";
	
	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	ConnectionFactory connectionFactory;
	// Connection ：JMS 客户端到JMS Provider 的连接
	Connection connection = null;
	// Session： 一个发送或接收消息的线程
	Session session;
	//
	MessageConsumer consumer;
	
	String MQ_IP="";
	
	private String GUARD_IP = ""; // 智能柜门禁IP

	private String RFID_IP = ""; // 智能柜RFID IP
	
	private String WS_URL = "";

	/**
	 * 接收消息        
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.init();					
		table_N_L = this.getTable("table_N_L");
		table_N_R = this.getTable("table_N_R");
		table_Y_L = this.getTable("table_Y_L");
		table_Y_R = this.getTable("table_Y_R");
		TABLE_CHECK = this.getTable("TABLE_CHECK");

		N_PANEL = (TPanel) getComponent("N_PANEL");
		Y_PANEL = (TPanel) getComponent("Y_PANEL");
		// 初始画面数据
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		CABINET_ID = parm.getValue("CABINET_ID");
		if (null != CABINET_ID && CABINET_ID.length() > 0)
			initPage(CABINET_ID);
		
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			this.messageBox("获取智能柜信息失败");
			return;
		}
		TParm inparm = new TParm();   
		inparm.setData("CABINET_IP", ip);  
		TParm result = SPCInStoreTool.getInstance().queryCabinet(inparm);					
		CABINET_ID = (String) result.getData("CABINET_ID", 0);
		RFID_IP = (String) result.getData("RFID_IP", 0);
		GUARD_IP = (String) result.getData("GUARD_IP", 0);
		WS_URL = (String)result.getData("ZKRFID_URL",0);
		MQ_QUEUE_NAME = (String)result.getData("MQ_DESC",0);
		this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
		this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
		this.setValue("CABINET_IP", ip);	
		
		String mqIp = WS_URL.substring(WS_URL.indexOf("http://") + 7, WS_URL.indexOf(":8080"));
		MQ_IP = mqIp;
		tparm.setData("MQ_IP",mqIp);
		tparm.setData("READER_ID",CABINET_ID);
		tparm.setData("WS_URL",WS_URL);
		tparm.setData("MQ_QUEUE_NAME",MQ_QUEUE_NAME);
	}

	/**
	 * 初始画面数据
	 */
	private void initPage(String cabinetId) {
		TParm result = new TParm();
		if (StringUtils.isNotBlank(cabinetId)) {
			TParm parm = new TParm();
			setValue("CABINET_ID", cabinetId);
			parm.setData("CABINET_ID", cabinetId);
			result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticDrugQtyInContainerM(parm)));
			table_N_L.setParmValue(result);
			result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticNromalQtyInContainerM(parm)));
			table_N_R.setParmValue(result);
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		//
		String cabinetId = getValueString("CABINET_ID");
		TParm result = new TParm();
		if (StringUtils.isNotBlank(cabinetId)) {
			TParm parm = new TParm();
			parm.setData("CABINET_ID", cabinetId);
			if (N_PANEL.isShowing()) {
				result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticDrugQtyInContainerM(parm)));
				table_N_L.setParmValue(result);
				result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticNromalQtyInContainerM(parm)));
				table_N_R.setParmValue(result);
			} else if (Y_PANEL.isShowing()) {
				result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticDrugQtyInContainerD(parm)));
				table_Y_L.setParmValue(result);
				result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStaticNromalQtyInContainerD(parm)));
				table_Y_R.setParmValue(result);
			}
		}

	}

	/**
	 * 清空操作
	 * */
	public void onClear() {
		setValue("CABINET_ID", "");
		table_N_L.removeRowAll();
		table_Y_L.removeRowAll();
		table_N_R.removeRowAll();
		table_Y_R.removeRowAll();
	}

	/**
	 * 回车事件
	 */
	public void onCabinetId() {
		onQuery();
	}

	/**
	 * 页签切换事件
	 */
	public void onTPanlClicked() {
		onQuery();
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
	/**
	 * 开始盘点
	 */
	public void startCheck() {
		onStart();
	}
	
	
	/**
	 * 结束盘点
	 */
	public void endCheck() {
		TABLE_CHECK.acceptText();
		int rowNum = TABLE_CHECK.getRowCount();
		TParm parm = TABLE_CHECK.getParmValue();		
		for(int i=0;i<rowNum;i++) {
			String orderCode = (String) TABLE_CHECK.getItemData(i, "ORDER_CODE");
			String containerId = (String) TABLE_CHECK.getItemData(i, "CONTAINER_ID");
			TParm inParm = new TParm();
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("CABINET_ID",CABINET_ID);
			TParm result = SPCContainerTool.getInstance().queryCabinetQty(inParm);
			TABLE_CHECK.setItem(i,"CHECKNUM",0);			
		}
		onEnd();
	}
	
	private Set<String> set = new HashSet<String>();
	
	/**
	 * 盘点智能柜库存
	 */
	public void addData() {
		TABLE_CHECK.removeRowAll();
		TParm parm = new TParm();
		parm.setData("CABINET_ID",CABINET_ID);				
		String str = "";
		for(String t:set){ 
			str +="'"+t+"',";
		} 
		str = str.substring(0, str.length()-1);
		parm.setData("CONTAINER_ID",str);		
		int rowCount = TABLE_CHECK.getRowCount();
		TParm result = SPCContainerTool.getInstance().queryToxicCheck(parm);
		TABLE_CHECK.setParmValue(result);  
					
	}
	
	
	/**
	 * 将xml转成tag
	 * 
	 * @param temp
	 * @return
	 */
	private List<RFIDTag> xml2rfidTag(String temp) {
		List<RFIDTag> list = new ArrayList<RFIDTag>();
		//temp = "<?xml version='1.0' encoding='UTF-8' ?><taglist><tag><epc>120034118801010865003102</epc><readerid>D001</readerid><antenna>1</antenna><direction></direction><time>2007-08-31 18:20:34.863</time></tag></taglist>";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		NodeList tags = null;
		try {  
			builder = dbf.newDocumentBuilder();

			Document doc = builder
					.parse(new InputSource(new StringReader(temp))); // 获取到xml文件
			Element root = doc.getDocumentElement(); // 获取根元素
			tags = root.getElementsByTagName("tag");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//
		for (int i = 0; i < tags.getLength(); i++) {
			RFIDTag tag = new RFIDTag();
			// 一次取得每一个学生元素
			Element ss = (Element) tags.item(i);
			NodeList names = ss.getElementsByTagName("epc");
			Element e = (Element) names.item(0);
			Node t = null;

			try {
				t = e.getFirstChild();
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			} 
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}  
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				t = e.getFirstChild();
				//tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			list.add(tag);
		}

		return list;
	}

	
	/**
	 * 开始扫描
	 */
    public void onStart(){	
    	TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm);
    	connectionFactory  = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://"+MQ_IP+":61616");
        try {				
			this.connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,	
					Session.AUTO_ACKNOWLEDGE);
			
			ActiveMQQueue topic = new ActiveMQQueue(MQ_QUEUE_NAME);										
			this.consumer=this.session.createConsumer(topic, "2 > 1", false);//
			
			//开始监听
			//多个消费者     同时临听  （需要多个时测试）
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    	   
    } 
	
	/**
	 * 停止操作
	 */
    public void onEnd(){	
    	try {
    		if (consumer != null)   consumer.close();  
    		if (session != null)  this.session.close();
    		if (connection != null) this.connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		TIOM_AppServer.executeAction("action.device.RFIDAction",
				"disconnect", tparm);
    }
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				List<RFIDTag> tagList = this.xml2rfidTag(msg);
				for(RFIDTag tag : tagList){   
					   String c = tag.getEpc();				
					    if (set.contains(c)) {
							continue;				
						}else {
							set.add(c);
						}
				}   
				addData();		
			} else {				   
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	

}
