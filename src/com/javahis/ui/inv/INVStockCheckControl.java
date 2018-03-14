package com.javahis.ui.inv;

import java.awt.Color;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap; 
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
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

  
/**
 *   
 * @author lit
 * 
 */
public class INVStockCheckControl extends TControl  implements MessageListener{
	
	
	
	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	ConnectionFactory connectionFactory;
	// Connection ：JMS 客户端到JMS Provider 的连接
	Connection connection = null;
	// Session： 一个发送或接收消息的线程
	Session session;
	//
	MessageConsumer consumer;
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

	private Timer timer;

	private TimerTask task;

	private long period = 1 * 1000;

	private long delay = 100;   

	TLabel tlabTip;
	 
	//private TParm parmStr;
	
	private TParm parmDD;
	     
	//接受RFID字符窜
	private String Str; 
	
	private TParm parm;
	
	private TParm tparm=new TParm();
	private Set<String> rfidetSet=new HashSet<String>();
	
	boolean flg;
	
	TButton start;
	TButton end;
	TButton ok;
	/** 
	 * 盘点变颜色
	 */
	Color Color = new Color(255, 0, 0);
	TTable table;
	TTextField scan;
	TTextField total;
	Map<String, Map<String, String>> mainMap;
	
	
	Map<String, String> rfidMap=new HashMap<String, String>();
	  Map<String, Map<String,String>>  invMap=new HashMap<String, Map<String,String>>();
	    private Map<String, Map<String,String>>  getInvMap() {
	    	  String sql = " SELECT B.INV_CODE, B.INV_CHN_DESC ,B.ORDER_CODE, " +
	          " B.DESCRIPTION,C.SUP_CHN_DESC,D.MAN_CHN_DESC  "+ 
	          " FROM INV_BASE B"+
	          " left join SYS_MANUFACTURER D on B.MAN_CODE=D.MAN_CODE"+
	          " left join SYS_SUPPLIER C ON C.SUP_CODE=B.UP_SUP_CODE"+
	          " WHERE   B.SEQMAN_FLG='Y' and B.EXPENSIVE_FLG='Y' ";
	         TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
	         Map<String, Map<String,String>> mainMap=new HashMap<String, Map<String,String>>();
	         
	         for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
	        	 Map<String, String> map=new HashMap<String, String>();
	        	map.put("INV_CHN_DESC", parm.getValue("INV_CHN_DESC", i)); 
	        	map.put("ORDER_CODE", parm.getValue("ORDER_CODE", i)); 
	        	map.put("DESCRIPTION", parm.getValue("DESCRIPTION", i)); 
	        	map.put("SUP_CHN_DESC", parm.getValue("SUP_CHN_DESC", i)); 
	        	map.put("MAN_CHN_DESC", parm.getValue("MAN_CHN_DESC", i)); 
	        	mainMap.put( parm.getValue("INV_CODE", i), map);
			}
	         return mainMap;
		}
	    
	    private Map<String,String> getRfidMap() {
	  	  String sql = " SELECT B.RFID, B.INV_CODE  " +
	        " FROM INV_STOCKDD B" +
	        " left join inv_base c on c.inv_code=B.inv_code "+
	        " WHERE   c.SEQMAN_FLG='Y' and c.EXPENSIVE_FLG='Y' and B.WAST_FLG='N'";
	       TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
	       Map<String, String> map=new HashMap<String, String>();
	       for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
	      	map.put(parm.getValue("RFID", i), parm.getValue("INV_CODE", i)); 
			}
	       return map;
		}
	/**
	 * 初始化
	 */ 
	public void onInit() {       
		super.onInit();
		

//    	rfidMap=getRfidMap();
    	invMap=getInvMap();
		
		mainMap=getAllRfidMap();
		start=(TButton)getComponent("start");
		end=(TButton)getComponent("end");
		ok=(TButton)getComponent("OK");
		end.setEnabled(false);
		ok.setEnabled(false);
		scan=(TTextField)getComponent("scan");
		total=(TTextField)getComponent("total");
		
		 table=(TTable)getComponent("TABLE");
		tlabTip = ((TLabel) this.getComponent("tLabel_0"));
		tlabTip.setVisible(false);
//		Operator.getID();
//		Operator.getIP();

		//((TTable)getComponent("TABLE_RFID")).setParmValue(parm);
		//onQuery();   
	} 
	
	public void onClear(){
		total.setValue("");
		scan.setValue("");
		parmDD=new TParm();
		parm=new TParm();
		set=new HashSet<String>();
		rfidetSet=new HashSet<String>();
		((TTable)getComponent("TABLE")).removeRowAll();  
	}
	
	public void onQuery(){
		//messageBox("1111");
		if (getValue("CAB")==null||"".equals(getValue("CAB"))) {
			messageBox("请选择智能柜");
		}
		onClear();
		parmDD = getInsertTableDDData();  
		
	}  
    /**
     * 取得验收序号管理细项数据(TABLE_DD)
     * @param parm TParm        
     * @return TParm            
     */  
    private TParm getInsertTableDDData() {
        //Timestamp date = SystemTool.getInstance().getDate();
//    	<?xml version="1.0" encoding="UTF-8" ?>   
//      //版本1.0，字体UTF-8""
    	String cab=getValue("CAB").toString();
    	tparm=new TParm();
    	//messageBox(cab);
    	
    	
    	//---------------
		String cabsql  = " SELECT CABINET_ID,CABINET_IP," +
		" ZKRFID_URL,MQ_DESC" + 
		" FROM IND_CABINET " +   
		" where CABINET_ID = '"+cab+"' AND CABINET_TYPE='02'" ;
		//System.out.println("sql"+sql); 
		// 获取对应的 配置信息    
		parm= new TParm(TJDODBTool.getInstance().select(cabsql));
		tparm.setData("READER_ID", parm.getValue("CABINET_ID",0)); 
		tparm.setData("TERM_IP", parm.getValue("CABINET_IP",0)); 
		tparm.setData("WS_URL", parm.getValue("ZKRFID_URL",0)); 
		tparm.setData(MQ_QUEUE_NAME, parm.getValue("MQ_DESC",0)); 
		
		String s = parm.getValue("ZKRFID_URL",0);
		s = s.substring(s.indexOf("http://") + 7, s.indexOf(":8080"));
		tparm.setData(MQ_IP,s);
    	//--------------
        TNull tnull = new TNull(Timestamp.class);      
        String sql = " SELECT B.INV_CHN_DESC AS INV_CHN_DESC," +
         " B.DESCRIPTION AS DESCRIPTION,A.VALID_DATE,A.BATCH_NO,A.BATCH_SEQ,A.ORGIN_CODE,"+ 
	     " D.UNIT_CHN_DESC AS STOCK_UNIT,B.COST_PRICE AS UNIT_PRICE,"+ 
         " A.RFID,A.INV_CODE "+
         " FROM INV_STOCKDD A,INV_BASE B,SYS_UNIT D"+
         " WHERE   A.INV_CODE = B.INV_CODE"+
         " AND A.WAST_FLG='N' AND B.STOCK_UNIT = D.UNIT_CODE AND A.CABINET_ID='"+cab+"'";        
        TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
                int invseq_no = 0;                  
                int batch_seq = 0;
//                String InvCn = parm.getData("INV_CHN_DESC").toString()
//                .replace("[", "").replace("]","");
//                String unit = parm.getData("STOCK_UNIT").toString()
//                .replace("[", "").replace("]","");   
//                String description = parm.getData("DESCRIPTION").toString()
//                .replace("[", "").replace("]",""); 
//                String ManCode = parm.getData("CONSIGN_MAN_CODE").toString()
//                .replace("[", "").replace("]","");    
//                String Price = parm.getData("UNIT_PRICE").toString()
//                .replace("[", "").replace("]","");  
                TParm parmDD = new TParm();   
                for (int j = 0; j < parm.getCount(); j++) {      
                	invseq_no = invseq_no + 1;
                	batch_seq = batch_seq + 1;    
                	//INVSEQ_NO;INV_CHN_DESC;DESCRIPTION;ORGIN_CODE;BATCH_SEQ
                	//;BATCH_NO;VALID_DATE;
                	//STOCK_UNIT;UNIT_PRICE;RFID;CONSIGN_FLG;CONSIGN_MAN_CODE     
                	parmDD.addData("UFLG", "N");  
                	parmDD.addData("RFLG", "N");  
                	parmDD.addData("MOREFLG", "N");  
                	parmDD.addData("INV_CHN_DESC", parm.getData("INV_CHN_DESC",j).toString()
                            .replace("[", "").replace("]",""));  
                	parmDD.addData("DESCRIPTION", parm.getData("DESCRIPTION",j).toString()
                            .replace("[", "").replace("]",""));
                	parmDD.addData("ORGIN_CODE", parm.getData("ORGIN_CODE",j)); 
                	parmDD.addData("BATCH_SEQ", parm.getData("BATCH_SEQ",j));
                	parmDD.addData("BATCH_NO",   parm.getData("BATCH_NO",j));  
                	parmDD.addData("INV_CODE",   parm.getData("INV_CODE",j));  
                	parmDD.addData("VALID_DATE",parm.getData("VALID_DATE",j));
                	parmDD.addData("STOCK_UNIT",  
                			parm.getData("STOCK_UNIT",j).toString()
                            .replace("[", "").replace("]",""));
                	parmDD.addData("UNIT_PRICE",                 
                			parm.getData("UNIT_PRICE",j).toString()
                            .replace("[", "").replace("]",""));      
//                    parm_DD.addData("RFID", "80.10.1203121000000" + j);  
////                    parm_DD.addData("EPC", parmStr.getData("EPC",j)); 
//                    parm_DD.addData("EPC", "80.10.1203121000000" + j); 
                	parmDD.addData("RFID", parm.getData("RFID",j));  
                	
                	rfidetSet.add(parm.getData("RFID",j).toString());
//                  parm_DD.addData("EPC", parmStr.getData("EPC",j));  
                }           
                ((TTable)getComponent("TABLE")).setParmValue(parmDD);
                total.setValue(String.valueOf(parmDD.getCount("RFID")));
                return parmDD;               
    }    
//	/**
//	 * table选中flg改变值事件
//	 */
//    public void onCharge(){
//    for(int i=0;i<((TTable)getComponent("TABLE")).getRowCount();i++){
//	   if("Y".equals(flg)){ 
//	      //噔的声音 
//	      Toolkit.getDefaultToolkit().beep();     
//	      //字体变色
//	      ((TTable)getComponent("TABLE")).setRowTextColor(i, Color); 
//        }
//      }
//    }
    
    
	/** 
	 * 启动操作
	 */
	public void onStart() { 
		
		// 1.校验 出库单号
		// 2.RFID数据采集控制器提供WebService方法。
		// connect(string readerId)。 
		TIOM_AppServer.executeAction("action.device.RFIDAction",
				"purgeQueue", tparm);
	    TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm);
	    
	    
	    
	
		// 3.RFID数据采集控制器提供WebService方法。
		// startScan(string readerId)。
//	    TIOM_AppServer.executeAction("action.device.RFIDAction",
//				"startScan", tparm);
		//		
	    start.setEnabled(false);
	    end.setEnabled(true);
	    
//		initTimer();
//		schedule();
	  onStartListener();
	   
		tlabTip.setVisible(true);
	}   

	/**
	 * 停止操作
	 */
	public void onEnd() {
		//
		//cancel();  
		tlabTip.setVisible(false); 
		// RFID数据采集控制器提供WebService方法。
		// stopScan(string readerId)。
//		TIOM_AppServer.executeAction("action.device.RFIDAction",
//				"stopScan", tparm);

		// RFID数据采集控制器提供WebService方法。
		// disconnect(string readerId)。
		start.setEnabled(true);
	    end.setEnabled(false);
	    ok.setEnabled(true);
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
		TIOM_AppServer.executeAction("action.device.RFIDAction",
				"disconnect", tparm);
		
	  

		//compare();
		sum();
		

	}
	
	
	private void sum() {
		TTable tablesum=getTTable("TABLE_SUM");
		Map<String, Integer> map=new TreeMap<String, Integer>();
		for (int i = 0; i <table.getRowCount(); i++) {
			if (table.getItemString(i,"RFLG").equals("Y")) {
				String invcode=table.getItemString(i,"INV_CODE");
				if (map.containsKey(invcode)) {
					int m=map.get(invcode);
					map.put(invcode, m+1);
				}else {
					map.put(invcode, 1);
				}
			}
			
		}
		TParm parm= new TParm();
		int row=0;
	
		for (String s : map.keySet()) {
			Map<String, String> mmap=invMap.get(s);
			parm.setData("QTY", row, map.get(s));
			parm.setData("INV_CHN_DESC", row, mmap.get("INV_CHN_DESC"));
			parm.setData("SUP_CHN_DESC", row,mmap.get("SUP_CHN_DESC"));
			parm.setData("MAN_CHN_DESC", row,mmap.get("MAN_CHN_DESC"));
			parm.setData("DESCRIPTION", row,mmap.get("DESCRIPTION"));
			parm.setData("ORDER_CODE", row,mmap.get("ORDER_CODE"));
			parm.setData("INV_CODE", row,s);
			row++;
		}
		tablesum.setParmValue(parm);
		
		
	}
	public void onExport() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE_SUM"),
		"智能柜盘点汇总表");
		
	}
	public void onExportUp() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),
		"智能柜盘点表");
		
	}
	
	
	
	private void compare() {
		// 表格中parm值一致,
		// 1.取paramw值;
		TParm tableData = getTTable("TABLE").getParmValue();
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
		String tblColumnName = getTTable("TABLE").getParmMap(0);
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
		getTTable("TABLE").setParmValue(parmTable);
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
	//
	public void initTimer() {
		this.timer = new Timer();
	    //setPeriod();
		
		 this.task = new TimerTask() 
		 {
		 public void run() { 
			 // onTest(); } };
		 }
		 };
	}

	/**
	 * 任务线程      
	 */
	public void schedule() {
		set=new HashSet<String>();
		this.task = new TimerTask() {
			public void run() {
				onTest();    
			}
		}; 	 
//		private Timer timer;
//
//		private TimerTask task;
//
//		private long period = 1 * 1000;（1秒）
//
//		private long delay = 5 * 1000;（5秒）
		this.timer.schedule(this.task, this.delay, this.period);
	}
	
	/**
	 * RFID扫描  
	 */
	private Set<String> set;
	public void onTest() {		
		
		
	}
	/**
	 * 盘点事件
	 */
	public void onConfirm(){   
	    //写入INV_STOCKDD（还是INV_DDSTOCK）表？
		//表结构未定   还要更新inv_stockm,inv_stockd
		//更新stockdd料位字段
        this.messageBox("盘点成功"); 
	}

	/**
	 * 取消任务 
	 */
	public void cancel() {
		this.task.cancel();
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
			//System.out.println("======temp=========="+temp);
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
				//System.out.println("----epc----" + t.getNodeValue());
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			} 
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----readerid----" + t.getNodeValue());
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----antenna----" + t.getNodeValue());
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}  
			//
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				//System.out.println("----e----" + e);  
				t = e.getFirstChild();
				//System.out.println("----direction----" + t);
				//tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				//System.out.println("----direction not null----");
				tag.setDirection("");
			}
			//
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----time----" + t.getNodeValue());
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			//
			list.add(tag);
		}

		return list;
	}
	public void onUpdate() {
		TParm tParm=table.getParmValue();
		TParm uParm=new TParm();
		for (int i = 0; i < tParm.getCount("RFID"); i++) {
			
			if (tParm.getValue("UFLG",i).equals("Y")) {
				
				uParm.addData("RFID", tParm.getValue("RFID", i));
				uParm.addData("CABINET_ID", getValue("CAB").toString());
			}
		}
		 TParm	 result = TIOM_AppServer.executeAction( "action.inv.INVBaseAction", "onUpdateCab", uParm);
		if (result == null || result.getErrCode() < 0) {
		  this.messageBox("E0001");
		  return;
		}
		this.messageBox("P0001");
			onClear();
		
	}
	

	//
    public void onStartListener(){
    	final String mqIP = tparm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			messageBox("MQ服务器IP配置参数错误!");
			return ;
		}
    	
    	this.connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.1.50:61616");
        try {
			this.connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			
			ActiveMQQueue topic = new ActiveMQQueue(tparm.getValue(MQ_QUEUE_NAME));
			this.consumer=this.session.createConsumer(topic, "2 > 1", false);//
			
			//开始监听
			//多个消费者     同时临听  （需要多个时测试）
			this.consumer.setMessageListener(this);
			
			
			
			
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
		};
    	
    }
	public static void main(String args[]) {
		INVStockCheckControl t = new INVStockCheckControl();
		String temp = "";
		t.xml2rfidTag(temp);
 
	}                                                                                             

	/**  
	 * 
	 * 得到TTabl
	 * 
	 * @param tag
	 *            String  
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				
				
				List<RFIDTag> lt=xml2rfidTag(msg);
				if (lt!=null&&lt.size()>0) {
					for (RFIDTag rfidTag : lt) {
						String rifd=rfidTag.getEpc();
						//System.out.println("--------------rfidTag.getEpc()------------------"+rfidTag.getEpc());
							if (set==null) {
								set=new HashSet<String>();
							}
						    if (set.contains(rifd)) {
								continue;
							}else {
								set.add(rifd);
							}
					}
					
				}
//				System.out.println("----------------96--------"+mainMap);
//				System.out.println("----------------96--------"+mainMap.get("130529000075"));
			
				for(int i=0;i<table.getRowCount();i++){
					
					
					//遍历PFID与Str相比                              
					if(set.contains(parmDD.getData("RFID", i))){     
					//if(parmDD.getData("RFID", i).equals("120034118801010865003102")){ 
					//噔的声音
					//Toolkit.getDefaultToolkit().beep();     
					//字体变色
					((TTable)getComponent("TABLE")).setItem(i, 0, "Y");   
					((TTable)getComponent("TABLE")).setItem(i, 1, "Y");   
					   
					}    
				} 
				set.removeAll(rfidetSet);
				for (String crfid : set) {
					
					Map<String, String> itemMap=mainMap.get(crfid);
					if (itemMap==null) {
						continue;
					}
					int row=table.addRow();
		        	table.setItem(row, "UFLG", itemMap.get("UFLG"));
		        	table.setItem(row, "RFLG", itemMap.get("RFLG"));
		        	table.setItem(row, "MOREFLG", itemMap.get("MOREFLG"));
		        	table.setItem(row, "INV_CHN_DESC", itemMap.get("INV_CHN_DESC"));
		        	table.setItem(row, "DESCRIPTION", itemMap.get("DESCRIPTION"));
		        	table.setItem(row, "STOCK_UNIT", itemMap.get("STOCK_UNIT"));
		        	table.setItem(row, "UNIT_PRICE", itemMap.get("UNIT_PRICE"));
		        	table.setItem(row, "RFID", itemMap.get("RFID"));
		        	
		        	table.setItem(row, "BATCH_NO", itemMap.get("BATCH_NO"));
		        	table.setItem(row, "VALID_DATE", itemMap.get("VALID_DATE"));
		        	table.setItem(row, "BATCH_SEQ",itemMap.get("BATCH_SEQ"));
		        	table.setItem(row, "ORGIN_CODE", itemMap.get("ORGIN_CODE"));
		        	table.setItem(row, "INV_CODE", itemMap.get("INV_CODE"));
		        	total.setValue(String.valueOf(table.getRowCount()));
					
					
					//防止定时器下次执行时    依然添加这些行
					rfidetSet.add(crfid);
					
				}
			
				int s=0;
				for (int i = 0; i < table.getRowCount(); i++) {
					if ("Y".equals(table.getItemString(i, "RFLG"))) {
						s++;
					}
					
				}
				//total.setValue(String.valueOf(m.getCount("RFID")));
				scan.setValue(String.valueOf(s));
				
				
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
//Color red = new Color(255, 0, 0);
//	TTabbedPane p = (TTabbedPane) this.getComponent("TABLEPANE");
//		p.setTabColor(4, red);//Color red = new Color(255, 0, 0);
	private Map<String,Map<String, String>> getAllRfidMap(){
		
		Map<String,Map<String, String>>  map=new HashMap<String, Map<String, String>>();
        String sql = " SELECT 'Y' as UFLG,'Y' as RFLG,'Y' as MOREFLG, B.INV_CHN_DESC AS INV_CHN_DESC," +
         " B.DESCRIPTION AS DESCRIPTION,A.VALID_DATE,A.BATCH_NO,A.BATCH_SEQ,A.ORGIN_CODE,"+ 
	     " D.UNIT_CHN_DESC AS STOCK_UNIT,B.COST_PRICE AS UNIT_PRICE,"+ 
         " A.RFID,A.INV_CODE"+
         " FROM INV_STOCKDD A,INV_BASE B,SYS_UNIT D"+
         " WHERE   A.INV_CODE = B.INV_CODE"+ 
         " AND A.WAST_FLG='N' AND B.STOCK_UNIT = D.UNIT_CODE  and A.RFID is not null and B.SEQMAN_FLG='Y' and B.EXPENSIVE_FLG='Y' ";     
        TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
        
	        for (int i = 0; i < parm.getCount("RFID"); i++) {
	        	Map<String, String> m=new HashMap<String, String>();
	        	m.put("UFLG", parm.getData("UFLG", i).toString());
	        	m.put( "RFLG", parm.getData("RFLG", i).toString());
	        	m.put( "MOREFLG", parm.getData("MOREFLG", i).toString());
	        	m.put( "INV_CHN_DESC", parm.getData("INV_CHN_DESC", i).toString());
	        	m.put( "DESCRIPTION", parm.getData("DESCRIPTION", i).toString());
	        	m.put( "STOCK_UNIT", parm.getData("STOCK_UNIT", i).toString());
	        	m.put( "UNIT_PRICE", parm.getData("UNIT_PRICE", i).toString());
	        	m.put( "RFID", parm.getData("RFID", i).toString());
	        	
	        	m.put( "BATCH_NO", parm.getData("BATCH_NO", i).toString());
	        	m.put( "VALID_DATE", parm.getData("VALID_DATE", i).toString());
	        	m.put("BATCH_SEQ", parm.getData("BATCH_SEQ", i).toString());
	        	m.put( "ORGIN_CODE", parm.getData("ORGIN_CODE", i).toString());
	        	m.put( "INV_CODE", parm.getData("INV_CODE", i).toString());
	        	map.put(parm.getData("RFID", i).toString(), m);
	        	
			}
	        return map;
		
	}

}

