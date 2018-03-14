package com.javahis.ui.inv;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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

import jdo.inv.INVDispenseDDTool;
import jdo.inv.INVSQL;
import jdo.inv.InvDispenseDTool;
import jdo.inv.InvStockDDTool;
import jdo.inv.InvStockDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory; 
import org.apache.activemq.command.ActiveMQQueue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 物资库入库管理Control  按单子出库（RFID扫描然后写入DD表），
 *                              按扫码，启动结束扫描（全部）入库    
 * </p>   在途显示出库单号，入库显示入库单号    INV_STOCKDD 取批号效期 
 *
 * <p> 
 * 出库（保存时只更新请领状态）-插入表，更新请领单 
 * 入库-（保存时只更新出库状态）插入表，更新出库单 （入库）
 * 
 * Description: 物资库入库管理Control     
 * </p>           
 * 未完成：出库单(0)  在途：入库状态0 完成：入库状态1   
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2013.05.02
 * @version 1.0 
 */
public class INVCabInControl 
    extends TControl implements MessageListener{ 
    public INVCabInControl() {  
    }  
	/**  
	 * 接收消息      
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";
	
	/**
	 * MQ消息服务IP地址  
	 */
	public final static String MQ_IP = "MQ_IP";
	/**
	 * 对列名称
	 */
	public final static String MQ_QUEUE_NAME = "MQ_QUEUE_NAME";
 

	private TimerTask task;


	TLabel tlabTip;
	 
	
	private TParm parm;
	
	private TParm tparm=new TParm();
	private Set<String> rfidetSet=new HashSet<String>();
	
	boolean flg;
	 
	TButton start;
	
	TButton end;
    
    
    
    private TTable table_m;//申请单

    private TTable table_d;//具体细项
     
    private TTable table_dd;//序号细项 
    
    private TTable table_dd_select;//查询序号细项 
    
    private TTable table_m_select; //完成状态查询入库单号 
    
    
    int total;//总数
    int scanNum; //扫描数
    

    // 全院药库部门作业单据
    private boolean request_all_flg = true;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }
  
    /**
     * 清空方法 
     */
    public void onClear() {
        getTable("TABLE_DD").removeRowAll();  
    } 

  
    /**  
     * 保存方法
     */  
    public void onSave() { 
    	
    }


    
    

 


    /**
     * 初始画面数据
     */
    private void initPage() {
        table_m = getTable("TABLE_M");   
        table_d = getTable("TABLE_D"); 
        table_dd = getTable("TABLE_DD"); 
        table_dd_select = getTable("TABLE_DD_SELECT"); 
        table_m_select = getTable("TABLE_M_SELECT");   
    	table_dd.setVisible(true); 
    	table_dd_select.setVisible(false);  
        // 全院药库部门作业单据
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        } 
        else {  
            request_all_flg = true;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间  
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");      
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
            
        // TABLE_D值改变事件 
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue"); 
        //CABINET_ID 回车事件 取消        
//        callFunction("UI|CABINET_ID|addEventListener", 
//				TTextFieldEvent.KEY_PRESSED, this, "onCharge");
        //智能柜值改变事件
        callFunction("UI|CABINET_ID|addEventListener", 
        		TTableEvent.CHANGE_VALUE, this, "onCharge");
        table_d.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox");
    	callFunction("UI|RFID|addEventListener", 
				TTextFieldEvent.KEY_PRESSED, this, "onCharge");
        //单据状态变更事件     
    } 
     
    /**
	 * 细表checkBox事件 
	 * 
	 * @param obj 
	 *            Object
	 * @return boolean
	 */
    
    
    /**
     * CABINET_ID赋值+改变焦点（智能柜相关查询功能）  
     */     
    
     
    /**
     * 扫描开始事件
     * 开始向智能柜字段赋值
     */
    public void onStartScan(){ 
    	list=new ArrayList<MessageConsumer>();
		listConnection=new ArrayList<Connection>();
		listSession=new ArrayList<Session>();
		String cabsql  = " SELECT CABINET_ID,CABINET_IP," +
		" ZKRFID_URL,MQ_DESC" + 
		" FROM IND_CABINET " +   
		" where ACTIVE_FLG='Y' AND CABINET_TYPE='03'" ;
		//System.out.println("sql"+sql); 
		// 获取对应的 配置信息    
		TParm mparm= new TParm(TJDODBTool.getInstance().select(cabsql));
		tparm=new TParm();
		for (int i = 0; i < mparm.getCount("CABINET_ID"); i++) {
			tparm.addData("READER_ID", mparm.getData("CABINET_ID",i)); 
			tparm.addData("TERM_IP", mparm.getData("CABINET_IP",i)); 
			tparm.addData("WS_URL", mparm.getData("ZKRFID_URL",i)); 
			tparm.addData(MQ_QUEUE_NAME, mparm.getData("MQ_DESC",i)); 
			
			String s = mparm.getData("ZKRFID_URL",i).toString();
			s = s.substring(s.indexOf("http://") + 7, s.indexOf(":8080"));
			tparm.addData(MQ_IP,s);
			
		}
	
		
		
		
		for( int i=0;i< tparm.getCount("READER_ID");i++){
			//执行扫描
			TParm myParm=new TParm();
			myParm.setData("READER_ID", tparm.getData("READER_ID",i));
			myParm.setData("WS_URL",  tparm.getData("WS_URL",i));
			System.out.println("---------myParm-----------------"+myParm);
			TIOM_AppServer.executeAction("action.device.RFIDAction",
					"purgeQueue", myParm);
			  TIOM_AppServer.executeAction("action.device.RFIDAction",
						"connect", myParm);
		}
	  
	    
	    this.onStartListener();
		
		tlabTip.setVisible(true); 
    }
	
	/**
	 * 获取接收的数据
	 * 
	 * @return
	 */
	private TParm getReceiver() {
		System.out.println("------------getReceiver--------");  
		//MQ_QUEUE_NAME = parm.getValue("QUEUE_NAME");
		System.out.println("-=---------------------===="+tparm.getData(MQ_QUEUE_NAME));
		TParm parm = TIOM_AppServer.executeAction("action.device.RFIDAction",
				"receiver", tparm);
		System.out.println("------------getReceiver count--------"
				+ parm.getCount());
		System.out.println("------------getReceiver rtn--------" + parm);
		// 调用XML解析生成对象操作;
		if (parm.getErrCode() < 0) {
			this.messageBox(parm.getErrText());
		}

		return parm;  

	}

	/**
	 * 获取parm中接收到的多个RFID标签，响应数据  
	 * 
	 * @return
	 */
	private List<List<RFIDTag>> getRFIDList(TParm parm) {
		List<List<RFIDTag>> list = new ArrayList<List<RFIDTag>>();
		String temp = "";
		for (int i = 0; i < parm.getCount(); i++) {
			temp = parm.getValue(RECEIVER_MSG, i);
			System.out.println("======temp=========="+temp);
			list.add(this.xml2rfidTag(temp));
			// parm.getValue(name);  

		}

		return list;

	}

	/**
	 * 将xml转成tag
	 * 
	 * @param temp
	 * @return
	 */
	private List<RFIDTag> xml2rfidTag(String temp) {
		List<RFIDTag> list = new ArrayList<RFIDTag>();
		/**
		 * <?xml version="1.0" encoding="UTF-8"
		 * ?><taglist><tag><epc>120034118801010865003102
		 * </epc><readerid>D001</readerid
		 * ><antenna>1</antenna><direction></direction><time>2007-08-31
		 * 18:20:34.863</time></tag></taglist>
		 */
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
				System.out.println("----epc----" + t.getNodeValue());
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			} 
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				System.out.println("----readerid----" + t.getNodeValue());
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				System.out.println("----antenna----" + t.getNodeValue());
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}  
			//
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				System.out.println("----e----" + e);  
				t = e.getFirstChild();
				System.out.println("----direction----" + t);
				//tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				System.out.println("----direction not null----");
				tag.setDirection("");
			}
			//
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				System.out.println("----time----" + t.getNodeValue());
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			//
			list.add(tag);
		}

		return list;
	}  
	
	
    /**  
     * 扫描停止事件
     * 停止向智能柜字段赋值
     */
    public void onStopScan(){ 
    		for( int i=0;i< tparm.getCount("READER_ID");i++){
    			//执行扫描
    			TParm myParm=new TParm();
    			myParm.setData("READER_ID", tparm.getData("READER_ID",i));
    			myParm.setData("WS_URL",  tparm.getData("WS_URL",i));
    			TIOM_AppServer.executeAction("action.device.RFIDAction",
    					"disconnect", myParm);
    		}
    	  	try {
    	  		for (MessageConsumer c:list) {
    	  			if (c != null)   c.close();  
    			}
    	  		for (Session c:listSession) {
    	  			if (c != null)   c.close();  
    			}
    	  		for (Connection c:listConnection) {
    	  			if (c != null)   c.close();  
    			}
    			System.out.println("--------断开成功----------");
    		} catch (JMSException e) {
    			e.printStackTrace();
    			System.out.println("--------断开失败----------");
    		}

    } 

    
    
    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * 得到TCheckBox对象
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TTextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    
    /**
     * 以下是排序相关方法
     *
     * @param tagName
     *            元素TAG名称
     * @return  
     */
    private void compare() {
		// 表格中parm值一致,
		// 1.取paramw值;
		TParm tableData = getTable("TABLE").getParmValue();
		// 2.转成 vector列名, 行vector ;
		String columnName[] = tableData.getNames("Data");
		String strNames = "";
		for (String tmp : columnName) {
			strNames += tmp + ";";
		}
		strNames = strNames.substring(0, strNames.length() - 1);
		// System.out.println("==strNames=="+strNames);
		Vector vct = getVector(tableData, "Data", strNames, 0);
		// System.out.println("==vct=="+vct);

		// 3.根据点击的列,对vector排序
		// System.out.println("sortColumn===="+sortColumn);
		// 表格排序的列名;
		String tblColumnName = getTable("TABLE").getParmMap(0);
		// 转成parm中的列
		int col = tranParmColIndex(columnName, tblColumnName);
		// System.out.println("==col=="+col);
		Compare compare=new Compare();
		compare.setDes(false);
		compare.setCol(col);
		java.util.Collections.sort(vct, compare);
		// 将排序后的vector转成parm;
		cloneVectoryParam(vct, new TParm(), strNames);

		// getTMenuItem("save").setEnabled(false);
	}
	
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
		
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	List<MessageConsumer> list=new ArrayList<MessageConsumer>();
	List<Session> listSession=new ArrayList<Session>();
	List<Connection> listConnection=new ArrayList<Connection>();
	List<ConnectionFactory> listConnectionFactory=new ArrayList<ConnectionFactory>();
    public void onStartListener(){
    	
    	
    	try {
			
			for (int i = 0; i < tparm.getCount(MQ_QUEUE_NAME); i++) {
		    	
		      String mqIP = tparm.getData(MQ_IP,i).toString();
				if (mqIP == null || mqIP.length() == 0) {
					messageBox("MQ服务器IP配置参数错误!");
					return ;
				}

				ConnectionFactory myconnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD, "tcp://"+mqIP+":61616");
				Connection myconnection = myconnectionFactory.createConnection();
				listConnection.add(myconnection);
				// 启动
				myconnection.start();
				// 获取操作连接
				 Session mysession = myconnection.createSession(Boolean.FALSE,
						Session.AUTO_ACKNOWLEDGE);
				 listSession.add(mysession);
				ActiveMQQueue topic = new ActiveMQQueue(tparm.getData(MQ_QUEUE_NAME, i).toString());
				MessageConsumer consumer=mysession.createConsumer(topic, "2 > 1", false);//
				list.add(consumer);
				
				
				//开始监听
				//多个消费者     同时临听  （需要多个时测试）
				consumer.setMessageListener(this);
				
			}
		
			
			
			
//			//---------------------
//			ActiveMQQueue topic2 = new ActiveMQQueue("amq.D012");
//			MessageConsumer consumer2=this.session.createConsumer(topic2, "2 > 1", false);//
//			
//			//开始监听
//			//多个消费者     同时临听  （需要多个时测试）
//			consumer2.setMessageListener(this);
//			
//			System.out.println("--------启动成功----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------启动失败----------");
		}
		};

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	Map<String,String> map=new HashMap<String, String>();
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				
				
			//	System.out.println("Consumer:->Received: " + msg);
				List<RFIDTag> lt=xml2rfidTag(msg);
				if (lt!=null&&lt.size()>0) {
					for (RFIDTag rfidTag : lt) {
						String rifd=rfidTag.getEpc();
						    if (map.containsKey(rifd)) {
								continue;
							}else {
								map.put(rifd,rfidTag.getReaderid() );
							}
					}
				}
				int ss=0;
				for(int i=0;i<table_dd.getRowCount();i++){
					//遍历PFID与Str相比         
					String rfidString=table_dd.getItemData(i, "RFID").toString();
					if(map.containsKey(rfidString)){     
					//噔的声音
					//Toolkit.getDefaultToolkit().beep();     
					//字体变色
					table_dd.setItem(i, 0, "Y"); 
					table_dd.setItem(i, "CABINET_ID", map.get(rfidString));
					ss++;
					}    
				} 
				String cc=String.valueOf(ss);
				getTextField("TOTAL").setValue(cc);
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
