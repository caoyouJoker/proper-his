package com.javahis.ui.cts;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.cts.CTSTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

import java.applet.AudioClip;
import java.awt.Toolkit;
import java.io.File;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.swing.JApplet;
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

import com.javahis.device.Ring;
import com.javahis.ui.inv.RFIDTag;


/**
 * 
 * <p>
 * Title:洗衣分拣
 * </p>
 * 
 * <p>
 * Description:洗衣分拣
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * implements MessageListener
 * @author zhangp 2012.8.2
 * @version 1.0
 * 
 */

public class CTSEndControl extends TControl implements MessageListener {
	private static TTable tableM;
	private static TTable tableD;
//	private static TTable tableWM;
//	private static TTable tableWD;
	private static int rowM = -1;
	private static List<String> cloth_nos = new ArrayList<String>();
	private TParm rfidConfig;
	public final static String MQ_QUEUE_NAME = "MQ_QUEUE_NAME";
	public final static String MQ_IP = "MQ_IP";
//	private Set<String> set = new HashSet<String>();
	TParm tparm;
	private String ant_chn_desc = "";
	private String ant_id = "";
	private String rfid_url = "";
	private String mq_desc = "";
	private String rfid_ip = "";
	private String rfid_id = "";
	private String main_flg = "";
	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	ConnectionFactory connectionFactory;
	// Connection ：JMS 客户端到JMS Provider 的连接
	Connection connection = null;
	// Session： 一个发送或接收消息的线程
	Session session;

	
	//
	MessageConsumer consumer;
	String newWashNo="";
	Ring successRing;
	Ring errorRing;
	Ring repeatRing;
	
	private static TParm CtsoutM;   //出库主表数据
	private static TParm CtsoutD;   //出库细表数据
	
	private boolean disconn = true;   //在清空中是否断开连接 和清空队列
	
	/**
	 * 初始化方法
	 */
	public void onInit() {

		super.onInit();
		CtsoutM = new TParm();
		CtsoutD = new TParm();
		
		
		successRing = new Ring(Ring.SUCCESS);
		errorRing = new Ring(Ring.ERROR);
		repeatRing = new Ring(Ring.REPEAT);
		tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
//		tableWM = (TTable) getComponent("TABLE3");
//		tableWD = (TTable) getComponent("TABLE4");
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|tButton_1|setEnabled", false);
		//////////////////////////////////////////////////////////////////////////////////////////
		rfidConfig = CTSTool.getInstance().getRfidConfig(Operator.getIP());
		
		if(rfidConfig.getErrCode()<0){
			messageBox("读写器连接失败");
			this.callFunction("UI|START|setEnabled", false);
			this.callFunction("UI|END|setEnabled", false);
		}
		ant_chn_desc = rfidConfig.getValue("ANT_CHN_DESC");
		ant_id = rfidConfig.getValue("ANT_ID");
		rfid_url = rfidConfig.getValue("RFID_URL");
		mq_desc = rfidConfig.getValue("MQ_DESC");
		rfid_ip = rfidConfig.getValue("RFID_IP");
		rfid_id = rfidConfig.getValue("RFID_ID");
		main_flg = rfidConfig.getValue("MAIN_FLG");
		
//		if(main_flg.equals("Y")){
//			this.callFunction("UI|START|setEnabled", false);
//			this.callFunction("UI|END|setEnabled", false);
//			this.callFunction("UI|tButton_1|setEnabled", true);
//		}
		////////////////////////////////////////////////////////////////////////////////////////////
		

	}


	
	

	public void onStart() {

		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		this.callFunction("UI|START|setEnabled", false);
		this.callFunction("UI|END|setEnabled", true);
		grabFocus("CLOTH_NO");
		disconn = true;
		///////////////////////////////////////////////////////////////////////////////////////////
		tparm = new TParm();
		tparm.setData("READER_ID", rfid_id);
		tparm.setData("TERM_IP", "");
		tparm.setData("WS_URL", rfid_url);
		tparm.setData(MQ_QUEUE_NAME, mq_desc);
		tparm.setData(MQ_IP, rfid_ip);
		if ( TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm).getErrCode()==-1) {
//			messageBox("设备连接异常   请检查！");
			TIOM_AppServer.executeAction("action.device.RFIDAction",
					"disconnect", tparm);
			
			TIOM_AppServer.executeAction("action.device.RFIDAction", "connect",
					tparm);
			
		}
		TIOM_AppServer.executeAction("action.device.RFIDAction", "removeDLQQueue",
				tparm);
		onStartListener();
		//////////////////////////////////////////////////////////////////////////////////////////
//		System.out.println("分拣开始时间：："+date.toString());
		
	}

	public void onEnd() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		System.out.println("执行disconnect操作前的时间==="+date);
		///////////////////////////////////////////////////////////////////////////////////////////////
		TIOM_AppServer.executeAction("action.device.RFIDAction", "disconnect",
				tparm);
		Timestamp date1 = StringTool.getTimestamp(new Date());
		System.out.println("执行disconnect操作后的时间==="+date1);
		TIOM_AppServer.executeAction("action.device.RFIDAction", "purgeQueue",
				tparm);
		disconn = false;
		Timestamp date2 = StringTool.getTimestamp(new Date());
		System.out.println("执行purgeQueue操作后的时间==="+date2);
		
		////////////////////////////////////////////////////////////////////////////////////////////
		try {
			if (consumer != null)
				consumer.close();
			if (session != null)
				this.session.close();
			if (connection != null)
				this.connection.close();
//			System.out.println("--------断开成功----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("--------断开失败----------");
		}
		////////////////////////////////////////////////////////////////////////////////////////////
//		this.messageBox("end=="+rowM);
//		if(rowM == -1){
			rowM = tableM.getSelectedRow();
//		}
//		System.out.println("onEnd::rowM======"+rowM);
		onSave("");
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|tButton_1|setEnabled", true);
//		System.out.println("CtsoutM::======="+CtsoutM);
//		System.out.println("CtsoutD::======="+CtsoutD);
//		System.out.println("分拣结束时间：："+date.toString());
		
		
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		tableM.acceptText();
		this.rowM = tableM.getSelectedRow();
		int selRow=0;   //CtsoutM中的与页面wash_no对应的列
//		System.out.println("onquery::wash_no==="+getValueString("WASH_NO"));
//		TParm parm = new TParm();
//		if (!getValueString("WASH_NO").equals("")) {
//			parm.setData("WASH_NO", getValueString("WASH_NO"));
//		}
		
//		TParm result = CTSTool.getInstance().selectOUTM(parm);
		if(CtsoutM != null){
			for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
				if(CtsoutM.getValue("WASH_NO", i).equals(getValueString("WASH_NO"))){
					selRow = i;
				}
			}
		}
		
//		System.out.println("onQuery::rowM======"+rowM);
//		System.out.println("onQuery::CtsoutM======"+CtsoutM);
		tableM.acceptText();
		TParm tableParm = tableM.getParmValue();
		if (tableParm == null) {
			tableParm = new TParm();
		}
		int row = 0;
		boolean flg = false;
		for (int i = 0; i < tableParm.getCount("WASH_NO"); i++) {
			if (CtsoutM.getValue("WASH_NO", selRow).equals(
					tableParm.getValue("WASH_NO", i))) {
				row = i;
				flg = true;
			}
		}
//		System.out.println("onQuery::row==1==="+row);
		if (!flg) {
			tableParm.addData("WASH_NO", CtsoutM.getData("WASH_NO", selRow));
			tableParm.addData("DEPT_CODE", CtsoutM.getData("DEPT_CODE", selRow));
			tableParm
					.addData("STATION_CODE", CtsoutM.getData("STATION_CODE", selRow));
			tableParm.addData("QTY", CtsoutM.getData("QTY", selRow));
			tableParm.addData("START_DATE", CtsoutM.getData("START_DATE", selRow));
			tableParm.addData("END_DATE", CtsoutM.getData("END_DATE", selRow));
			tableParm.addData("PAT_FLG", CtsoutM.getData("PAT_FLG", selRow));
			tableParm.addData("STATE", CtsoutM.getData("STATE", selRow));
			tableParm.addData("WASH_CODE", CtsoutM.getData("WASH_CODE", selRow));
			tableParm.addData("OPT_USER", CtsoutM.getData("OPT_USER", selRow));
			tableParm.addData("OPT_DATE", CtsoutM.getData("OPT_DATE", selRow));
			tableParm.addData("OPT_TERM", CtsoutM.getData("OPT_TERM", selRow));
			tableParm.addData("TURN_POINT", CtsoutM.getData("TURN_POINT", selRow));
			row = tableParm.getCount("WASH_NO") - 1;
		}
//		setValue("TURN_POINT", result.getValue("TURN_POINT", 0));
		tableM.setParmValue(tableParm);
		tableM.setSelectedRow(row);
//		System.out.println("onQuery::row==2==="+row);
		// this.row = row;
		onClickTableM();
	}

	/**
	 * table1点击事件
	 */
	public void onClickTableM() {
		tableM.acceptText();
		// setOutQty();
//		System.out.println(rowM+"=="+tableM.getSelectedRow()+"================================");
		
		if (rowM != tableM.getSelectedRow()) {

			onSave("");
		}
		tableM.acceptText();
		// row = tableM.getSelectedRow();
//		TParm parm = new TParm();
//		parm.setData("WASH_NO", tableM.getParmValue().getValue("WASH_NO",
//				tableM.getSelectedRow()));
		String washNO = tableM.getParmValue().getValue("WASH_NO",tableM.getSelectedRow());
//		TParm result = CTSTool.getInstance().selectOUTD(parm);
		TParm result = new TParm();
//		System.out.println("tableM::washno===="+tableM.getParmValue().getValue("WASH_NO",tableM.getSelectedRow()));
//		System.out.println("CtsoutD::===="+CtsoutD);
//		System.out.println("parm::===="+parm);
//		System.out.println("washno::===="+parm.getValue("WASH_NO"));
//		System.out.println("CtsoutD::count===="+CtsoutD.getCount("CLOTH_NO"));
		for(int i=0;i<CtsoutD.getCount("CLOTH_NO");i++){
			if(CtsoutD.getValue("WASH_NO", i).equals(washNO)){
				result.addData("WASH_NO", CtsoutD.getValue("WASH_NO", i));
				result.addData("CLOTH_NO", CtsoutD.getValue("CLOTH_NO", i));
				result.addData("OWNER", CtsoutD.getValue("OWNER", i));
				result.addData("OWNER_CODE", CtsoutD.getValue("OWNER_CODE", i));
				result.addData("PAT_FLG", CtsoutD.getValue("PAT_FLG", i));
				result.addData("OPT_USER", CtsoutD.getValue("OPT_USER", i));
				result.addData("OPT_DATE", CtsoutD.getValue("OPT_DATE", i));
				result.addData("OPT_TERM", CtsoutD.getValue("OPT_TERM", i));
				result.addData("OUT_FLG", "Y");

			}
		}
		// System.out.println(result);
		for (int i = 0; i < result.getCount("CLOTH_NO"); i++) {
			result.setData("NEW_FLG", i, "N");
		}
//		System.out.println("tableD::result======="+result);
		tableD.setParmValue(result);
	}
	

	
	/**
	 * 查询outm 与outd 得到出库单号
	 */
	public TParm selectOUTMD(TParm parm){
		TParm result = new TParm();
		String clothNo=parm.getValue("CLOTH_NO");
//		System.out.println("CtsoutD=="+CtsoutD);
		if(CtsoutD != null){
			for(int i=0 ; i < CtsoutD.getCount("CLOTH_NO") ; i++){
				if(CtsoutD.getValue("CLOTH_NO", i).equals(clothNo)){
					result.addData("WASH_NO", CtsoutD.getValue("WASH_NO",i ));
					return result;
				}
				
			}
		}else{
			result.addData("WASH_NO", "");
		}
		
		return result;
		
	}
	
	/**
	 * 取得在分拣时新增的洗衣单
	 * @return
	 */
	public TParm getNewWash(String station_code, String pat_flg, String turn_point){
		TParm result = new TParm();
		if(CtsoutM != null){
			for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
				if(CtsoutM.getValue("TURN_POINT", i).equals(turn_point) && CtsoutM.getValue("PAT_FLG", i).equals(pat_flg)){
					result.addData("WASH_NO", CtsoutM.getValue("WASH_NO", i));
					result.addData("STATION_CODE", CtsoutM.getValue("STATION_CODE", i));
					result.addData("END_DATE", CtsoutM.getValue("END_DATE", i));
					result.addData("PAT_FLG", CtsoutM.getValue("PAT_FLG", i));
					result.addData("STATE", CtsoutM.getValue("STATE", i));
					result.addData("WASH_CODE", CtsoutM.getValue("WASH_CODE", i));
					result.addData("OPT_USER", CtsoutM.getValue("OPT_USER", i));
					result.addData("OPT_DATE", CtsoutM.getValue("OPT_DATE", i));
					result.addData("OPT_TERM", CtsoutM.getValue("OPT_TERM", i));
					return result;
				}
			}
		}
		
		return null;
		
	}

	public void onEnter() {
		TParm parm = new TParm();
		parm.setData("CLOTH_NO", getValue("CLOTH_NO"));
		TParm result = CTSTool.getInstance().selectCloth(parm);
		if (result.getCount() > 0) {
			if(result.getValue("STATE", 0).equals("1")){
				Timestamp date = StringTool.getTimestamp(new Date());
		        String sDate = date.toString().replace("-", "").substring(0, 8) + "000000";
		        String eDate = date.toString().replace("-", "").substring(0, 8) + "235959";
		        String sql = "SELECT A.WASH_NO,A.CLOTH_NO,A.IN_WASH_NO,B.TURN_DESC FROM CTS_OUTD A ,CTS_TURN_POINT B WHERE A.OPT_DATE BETWEEN TO_DATE ('" + 
		          sDate + "', 'YYYYMMDDHH24MISS')" + 
		          " AND TO_DATE ('" + eDate + "', 'YYYYMMDDHH24MISS')" + 
		          " AND A.IN_WASH_NO IS NOT NULL" + 
		          " AND A.TURN_POINT = B.ID" + 
		          " AND A.CLOTH_NO='" + getValue("CLOTH_NO") + "'";
		        TParm outParm = new TParm(TJDODBTool.getInstance().select(sql));
		        if (outParm.getCount() > 0) {
		          messageBox("该布衣" + getValue("CLOTH_NO") + "在出库单" + outParm.getValue("WASH_NO", 0) + "里面，将被送往的转发点为" + outParm.getValue("TURN_DESC", 0));
		          this.setValue("CLOTH_NO", "");
		          return;
		        }
				TParm reParm = (TParm)this.openDialog(
						"%ROOT%\\config\\cts\\CTSAddStart.x", parm);
				this.setValue("CLOTH_NO", "");
				return;
			}
			
			if(!cloth_nos.contains(result.getValue("CLOTH_NO", 0))){
				cloth_nos.add(result.getValue("CLOTH_NO", 0));
				successRing.play();
			}else{
				repeatRing.play();
			}
			
			String station = getValueString("STATION_CODE");
			String turnPoint = getValueString("TURN_POINT");
			
//			System.out.println("turnPoint:old===="+turnPoint);
			setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
			setValue("OWNER", result.getValue("OWNER", 0));
			setValue("OWNER_CODE", result.getValue("OWNER_CODE", 0));
			setValue("TURN_POINT", result.getValue("TURN_POINT", 0));
			
//			System.out.println("turnPoint:new===="+getValueString("TURN_POINT"));
			if(result.getValue("PAT_FLG", 0).equals("Y")){
				setValue("PAT_FLGT", "√");
			}else{
				setValue("PAT_FLGT", "");
			}
//			setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
			tableD.acceptText();
			TParm td = tableD.getParmValue();
			TParm result1 = selectOUTMD(parm);
//			System.out.println("result1===="+result1.getValue("WASH_NO", 0));
			
//			System.out.println("WASH_NO===="+getValue("WASH_NO"));
			if (getValue("WASH_NO").equals(result1.getValue("WASH_NO", 0))&&getValueString("WASH_NO").length()>0) {
				tableD.acceptText();
				td = tableD.getParmValue();
				if (td == null) {
					td = new TParm();
				}
				for (int i = 0; i < td.getCount("CLOTH_NO"); i++) {
					if (td.getValue("CLOTH_NO", i).equals(
							result.getValue("CLOTH_NO", 0))) {
						td.setData("OUT_FLG", i, "Y");
					}
				}
				tableD.setParmValue(td);
//				System.out.println("td1============="+td);
			} else {
//				System.out.println("WASH_NO::else1==========="+getValue("WASH_NO"));
				setValue("WASH_NO", result1.getValue("WASH_NO", 0));
//				System.out.println("WASH_NO::else2==========="+getValue("WASH_NO"));
				//如果洗衣单不为空，则查询原有洗衣单，否则，新增洗衣单
				if (!getValue("WASH_NO").equals("")) {
//					System.out.println("11111111111111111");
//					onSave("");
					onQuery();
//					System.out.println("washdddd====="+CtsoutD);
					
				}else{
//					System.out.println("2222222222222222222");
//					System.out.println("pat_flg=="+result.getValue("PAT_FLG", 0));
					TParm newWash = new TParm();
					newWash = getNewWash(result.getValue("STATION_CODE", 0), result.getValue("PAT_FLG", 0), result.getValue("TURN_POINT", 0));
//					System.out.println("newWase::===="+newWash);
					if(newWash != null){
//						System.out.println("33333333333333333333333");
						if(!turnPoint.equals(result.getValue("TURN_POINT", 0))  ){
							setValue("WASH_NO", newWash.getValue("WASH_NO", 0));
//							System.out.println("WASH_NO:333========"+getValue("WASH_NO"));
							onQuery();
						}
						tableD.acceptText();
						td = tableD.getParmValue();
//						System.out.println(td);
						List list = (List) td.getData("CLOTH_NO");
						if(!list.contains(result.getValue("CLOTH_NO", 0))){
							td.addData("WASH_NO", newWash.getValue("WASH_NO", 0));
							td.addData("CLOTH_NO", result.getValue("CLOTH_NO", 0));
							td.addData("SEQ_NO", td.getCount("CLOTH_NO")+1);
							td.addData("OWNER", result.getValue("OWNER", 0));
							td.addData("OWNER_CODE", result.getValue("OWNER_CODE", 0));
							td.addData("PAT_FLG", result.getValue("PAT_FLG", 0));
							td.addData("OUT_FLG", "N");
							td.addData("NEW_FLG", "Y");
							td.addData("OPT_USER", Operator.getID());
							td.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
							td.addData("OPT_TERM", Operator.getIP());
							tableD.setParmValue(td);
//							System.out.println("tableD11==="+tableD.getParmValue());
						}
					}else{
//						System.out.println("444444444444444444444444");
						TParm parmNew = new TParm();
						parmNew.setData("WASH", result.getData());
						parmNew.setData("OPT_USER", Operator.getID());
						parmNew.setData("OPT_TERM", Operator.getIP());
						parmNew.setData("END_DATE", getValue("START_DATE"));
						parmNew.setData("TURN_POINT", getValue("TURN_POINT"));
//						System.out.println("保存turnPoint=="+getValue("TURN_POINT"));
//						TParm resultNew = TIOM_AppServer.executeAction("action.cts.CTSAction",
//								"saveWashOut", parmNew);
//						if (resultNew.getErrCode() < 0) {
//							return;
//						}
						saveWashOut(parmNew);
						onEnter();
						onQuery();
					}
					
				}
				tableD.acceptText();
				td = tableD.getParmValue();
				if (td == null) {
					td = new TParm();
				}
				for (int i = 0; i < td.getCount("CLOTH_NO"); i++) {
					if (td.getValue("CLOTH_NO", i).equals(
							result.getValue("CLOTH_NO", 0))) {
						td.setData("OUT_FLG", i, "Y");
					}
				}
				tableD.setParmValue(td);
//				System.out.println("tableD22==="+tableD.getParmValue());
			}
		}else{
			errorRing.play();
		}
		setOutQty();
	
	}
	
	
	//往容器CtsoutM和CtsoutD里面添加数据
	public void saveWashOut(TParm parm){
		TParm result = new TParm();
		TParm tmp = parm.getParm("WASH");
		
		List<TParm> list = new ArrayList<TParm>();
		//如果分人，则分成本中心保存多个洗衣单，否则，直接保存为一个洗衣单
		if(tmp.getValue("PAT_FLG", 0).equals("Y")){
			getWashGroup(tmp, list);
		}else{
			tmp.setData("OPT_USER", parm.getValue("OPT_USER"));
			tmp.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			tmp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			tmp.setData("END_DATE", parm.getTimestamp("END_DATE"));
			tmp.setData("TURN_POINT", parm.getValue("TURN_POINT"));
			insertOutMD(tmp);
		}
		
		TParm noList = new TParm();
		for (int i = 0; i < list.size(); i++) {
			TParm insertParm = list.get(i);
			insertParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertParm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertParm.setData("END_DATE", parm.getTimestamp("END_DATE"));
			insertParm.setData("TURN_POINT", parm.getValue("TURN_POINT"));
			insertOutMD(insertParm);

			noList.addData("WASH_NO", result.getValue("WASH_NO"));
		}	
	}
	
	private void getWashGroup(TParm parm, List<TParm> list) {
//		System.out.println("parm=="+parm);
		for (int i = 0; i < list.size(); i++) {
//			System.out.println(i+"==="+list.get(i));
		}
//		String DEPT_CODE = parm.getValue("DEPT_CODE", 0);
		String STATION_CODE = parm.getValue("STATION_CODE", 0);
		String TURN_POINT = parm.getValue("TURN_POINT", 0);
		TParm ss = new TParm();
		ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", 0));
		ss.addData("INV_CODE", parm.getValue("INV_CODE", 0));
		ss.addData("OWNER", parm.getValue("OWNER", 0));
//		ss.addData("DEPT_CODE", DEPT_CODE);
		ss.addData("STATION_CODE", STATION_CODE);
		ss.addData("STATE", parm.getValue("STATE", 0));
		ss.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG", 0));
		ss.addData("PAT_FLG", parm.getValue("PAT_FLG", 0));
		ss.addData("NEW_FLG", parm.getValue("NEW_FLG", 0));
		ss.addData("TURN_POINT", TURN_POINT);
		parm.removeRow(0);
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < parm.getCount("STATION_CODE"); i++) {
//			if (parm.getValue("DEPT_CODE", i).equals(DEPT_CODE)
//					&& parm.getValue("STATION_CODE", i).equals(STATION_CODE)) {
//			if (parm.getValue("STATION_CODE", i).equals(STATION_CODE)) {
			if (parm.getValue("TURN_POINT", i).equals(TURN_POINT)) {
				ss.addData("CLOTH_NO", parm.getValue("CLOTH_NO", i));
				ss.addData("INV_CODE", parm.getValue("INV_CODE", i));
				ss.addData("OWNER", parm.getValue("OWNER", i));
//				ss.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
				ss.addData("STATION_CODE", parm.getValue("STATION_CODE", i));
				ss.addData("STATE", parm.getValue("STATE", i));
				ss.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG", i));
				ss.addData("PAT_FLG", parm.getValue("PAT_FLG", i));
				ss.addData("NEW_FLG", parm.getValue("NEW_FLG", i));
				ss.addData("TURN_POINT", parm.getValue("TURN_POINT", i));
				l.add(i);
			}
		}
		for (int i = 0; i < l.size(); i++) {
//			System.out.println(i);
//			System.out.println("Integer.valueOf('' + l.get(i)) - i=="+(Integer.valueOf("" + l.get(i)) - i));
//			System.out.println("for parm=="+parm);
			parm.removeRow(Integer.valueOf("" + l.get(i)) - i);
		}
		list.add(ss);
		if (parm.getCount("STATION_CODE") > 0) {
			getWashGroup(parm, list);
		}
	}
	
	public void insertOutMD(TParm parm){
		int qty = parm.getCount("CLOTH_NO");
		String wash_no = CTSTool.getInstance().getWashOutNo();
//		System.out.println("wash_no==="+wash_no);
		CtsoutM.addData("WASH_NO", wash_no);
		CtsoutM.addData("STATION_CODE",
				parm.getValue("STATION_CODE", 0) == null ? new TNull(
						String.class) : parm.getValue("STATION_CODE", 0));
		CtsoutM.addData("QTY", qty);
		CtsoutM.addData("START_DATE",
				parm.getTimestamp("START_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("START_DATE"));
		CtsoutM.addData("END_DATE",
		 parm.getTimestamp("END_DATE") == null ? new TNull(
		 Timestamp.class) : parm.getTimestamp("END_DATE"));
		CtsoutM.addData("PAT_FLG",
				parm.getValue("PAT_FLG", 0) == null ? new TNull(String.class)
						: parm.getValue("PAT_FLG", 0));
//		//分拣时无洗衣单保存时
//		if(parm.getValue("END_DATE").length()>0){
		CtsoutM.addData("STATE", 1);
//		}else{
//			washM.setData("STATE", 2);
//		}
		CtsoutM.addData("WASH_CODE",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		CtsoutM.addData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		CtsoutM.addData("OPT_DATE",
				parm.getTimestamp("OPT_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("OPT_DATE"));
		CtsoutM.addData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		
		CtsoutM.addData("TURN_POINT",
				parm.getValue("TURN_POINT", 0) == null ? new TNull(String.class)
						: parm.getValue("TURN_POINT"));
		
	
		for (int i = 0; i < qty; i++) {
			CtsoutD.addData("OWNER_CODE", getValue("OWNER_CODE"));
			CtsoutD.addData("WASH_NO", wash_no);
			CtsoutD.addData("SEQ_NO", i + 1);
			CtsoutD.addData("CLOTH_NO",
					parm.getValue("CLOTH_NO", i) == null ? new TNull(
							String.class) : parm.getValue("CLOTH_NO", i));
			CtsoutD.addData("OWNER",
					parm.getValue("OWNER", i) == null ? new TNull(String.class)
							: parm.getValue("OWNER", i));
			CtsoutD.addData("PAT_FLG",
					parm.getValue("PAT_FLG", i) == null ? new TNull(
							String.class) : parm.getValue("PAT_FLG", i));
			CtsoutD.addData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			CtsoutD.addData("OPT_DATE",
					parm.getTimestamp("OPT_DATE") == null ? new TNull(
							Timestamp.class) : parm.getTimestamp("OPT_DATE"));
			CtsoutD.addData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			CtsoutD.addData("NEW_FLG",
					parm.getValue("NEW_FLG", i) == null ? new TNull(String.class)
							: parm.getValue("NEW_FLG", i));
			
			CtsoutD.addData("TURN_POINT",
					parm.getValue("TURN_POINT", i) == null ? new TNull(String.class)
							: parm.getValue("TURN_POINT"));
			
//			System.out.println("CtsoutD"+i+"::====aa==="+CtsoutD);
		}
		
	}
	
	
	public void setOutQty() {
		tableD.acceptText();
		TParm d = tableD.getParmValue();
		if (d == null) {
			d = new TParm();
		}
		int count = 0;
		for (int i = 0; i < d.getCount("CLOTH_NO"); i++) {
			if (d.getValue("OUT_FLG", i).equals("Y")) {
				count++;
			}
		}
		tableM.acceptText();
		TParm m = tableM.getParmValue();
		m.setData("OUT_QTY", tableM.getSelectedRow(), count);
		int row = tableM.getSelectedRow();
		tableM.setParmValue(m);
		tableM.setSelectedRow(row);
		
		String washNo = tableM.getParmValue().getValue("WASH_NO", tableM.getSelectedRow());
		for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
			if(CtsoutM.getValue("WASH_NO", i).equals(washNo)){
				CtsoutM.setData("QTY", i, count);
			}
		}
		
		
		setValue("OUT_QTY", cloth_nos.size());
	}

	/**
	 * 保存按钮
	 */
	public void onSave() {
//		onSave("保存成功");
		for(int i=0;i<CtsoutM.getCount("WASH_NO");i++){
			CtsoutM.setData("END_DATE", i, getValue("END_DATE"));
			CtsoutM.setData("STATE", i, 3);
		}
//		System.out.println("end_date==="+getValue("END_DATE"));
//		System.out.println("CtsoutM===::==="+CtsoutM);
		TParm parm = new TParm();
		parm.setData("CtsoutM", CtsoutM.getData());
		parm.setData("CtsoutD", CtsoutD.getData());
		System.out.println("parm=="+parm);
		TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction","insertCTSOUTMD", parm);
		if (result.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		updateEndDate();
//		onPrint();
//		initPage();
		onClear();
	}
	

	private void updateEndDate() {
		tableM.acceptText();
		TParm parmM = tableM.getParmValue();
		for (int i = 0; i < parmM.getCount("WASH_NO"); i++) {
			TParm parm = new TParm();
			parm.setData("END_DATE", getValue("END_DATE"));
			parm.setData("STATE", 3);
			parm.setData("WASH_NO", parmM.getValue("WASH_NO", i));
			parm.setData("QTY", parmM.getValue("OUT_QTY", i));
//			TParm result = CTSTool.getInstance().updateWASHMEndDate(parm);
//			if (result.getErrCode() < 0) {
//				messageBox("更新失败");
//				return;
//			}
			
			TParm result= TIOM_AppServer.executeAction("action.cts.CTSAction",
					"updateInOutWashNo", parm);

			String sql="SELECT CLOTH_NO FROM CTS_OUTD  WHERE WASH_NO = '"+parmM.getValue("WASH_NO", i)+"'";
//			System.out.println("stockdd======"+sql);
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
//			System.out.println("stockdd22======="+sqlParm);
//			System.out.println("stockdd22getCount======="+sqlParm.getCount());
			for(int j=0;j<sqlParm.getCount();j++){
				TParm parmDD = new TParm();
				parmDD.setData("RFID", sqlParm.getValue("CLOTH_NO", j));
				parmDD.setData("STATE", "1");
				result = CTSTool.getInstance().updateStockDD(parmDD);
				if (result.getErrCode() < 0) {
					messageBox("更新aa失败");
					return;
				}
			}
			
			
			
			
		}
		

		
		messageBox("保存成功");
	}

	/**
	 * 保存    更新ctsd
	 */
	private void onSave(String message) {
//		this.messageBox("save");
//		setOutQty();
		tableM.acceptText();
		tableD.acceptText();
		if (rowM != -1) {
			TParm parmM = null;
			if (message.equals("")) {
				parmM = tableM.getParmValue().getRow(rowM);
				parmM.setData("TMP_FLG", "Y");
			} else {
				parmM = tableM.getParmValue().getRow(tableM.getSelectedRow());
			}
			TParm parmD = tableD.getParmValue();
			// System.out.println(parmM);
			 System.out.println("onsave====="+parmD);
			TParm parm = new TParm();
			parm.setData("WASHM", parmM.getData());
			parm.setData("WASHD", parmD.getData());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("END_DATE", getValue("END_DATE"));
			updateMD(parm);
//			TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction",
//					"updateMD", parm);
//			if (result.getErrCode() < 0) {
////				messageBox("插入失败");
//				return;
//			}
		
			
		}	
		if (!message.equals("")) {
			messageBox(message);
		}
	}
	
	public void updateMD(TParm parm) {

		TParm washM = parm.getParm("WASHM");
		TParm washD = parm.getParm("WASHD");
//		System.out.println("washD===="+washD);
//		System.out.println("washM===="+washM);
		TParm updateM = new TParm();
		updateM.setData("WASH_NO", washM.getData("WASH_NO"));
		updateM.setData("END_DATE",
				parm.getTimestamp("END_DATE") == null ? new TNull(
						Timestamp.class) : parm.getTimestamp("END_DATE"));
		updateM.setData("STATE", 1);
		updateM.setData("OUT_QTY",
				washM.getData("OUT_QTY") == null ? new TNull(
						Integer.class) : washM.getData("OUT_QTY"));
		TParm result = new TParm();

		for (int i = 0; i < washD.getCount("CLOTH_NO"); i++) {

			if(washD.getValue("NEW_FLG", i).equals("Y")){

				if(washM.getValue("TURN_POINT").equals("")){
					CtsoutD.addData("TURN_POINT", "");
//					System.out.println("washM1===="+washM.getValue("TURN_POINT"));
				}else{
					CtsoutD.addData("TURN_POINT", washM.getData("TURN_POINT"));
//					System.out.println("washM2===="+washM.getData("TURN_POINT"));
				}
				
				
				
				CtsoutD.addData("WASH_NO", washM.getData("WASH_NO"));
				CtsoutD.addData("SEQ_NO", washD.getData("SEQ_NO", i));
				CtsoutD.addData("CLOTH_NO",
						washD.getValue("CLOTH_NO", i) == null ? new TNull(
								String.class) : washD.getValue("CLOTH_NO", i));
				
				CtsoutD.addData("OWNER",
						washD.getValue("OWNER", i) == null ? new TNull(String.class)
								: washD.getValue("OWNER", i));
				CtsoutD.addData("PAT_FLG",
						washD.getValue("PAT_FLG", i) == null ? new TNull(
								String.class) : washD.getValue("PAT_FLG", i));
				CtsoutD.addData("OPT_USER",
						washD.getValue("OPT_USER", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_USER", i));
				CtsoutD.addData("OPT_DATE",
						washD.getTimestamp("OPT_DATE", i) == null ? new TNull(
								Timestamp.class) : washD.getTimestamp("OPT_DATE", i));
				CtsoutD.addData("OPT_TERM",
						washD.getValue("OPT_TERM", i) == null ? new TNull(String.class)
								: washD.getValue("OPT_TERM", i));
				CtsoutD.addData("OUT_FLG", "Y");
				CtsoutD.addData("NEW_FLG",
						washD.getValue("NEW_FLG", i) == null ? new TNull(String.class)
								: washD.getValue("NEW_FLG", i));
//				System.out.println("CtsoutD::=====bb===="+i+"======"+CtsoutD);
				
			}
		}


	}
	
	
	/**
	 * 清空
	 */
	public void onClear() {
		
		tableM.acceptText();
//		TParm parmM = tableM.getParmValue();

		TParm parm = new TParm();
		tableM.setParmValue(parm);
		tableD.setParmValue(parm);
		
		CtsoutM = new TParm();
		CtsoutD = new TParm();
		
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|tButton_1|setEnabled", false);
		this.clearValue("CLOTH_NO;START_DATE;END_DATE;WASH_NO;PAT_FLGT;OUT_QTY;STATION_CODE;OWNER;TURN_POINT;OWNER_CODE");
		cloth_nos = new ArrayList<String>();
//		initPage();
		////////////////////////////////////////////////////////////////////////////////////////////////
		if(disconn){
			TIOM_AppServer.executeAction("action.device.RFIDAction", "disconnect",
					tparm);
			TIOM_AppServer.executeAction("action.device.RFIDAction", "purgeQueue",
					tparm);
			
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////

	}
	
	public boolean onClosing() {
		try {
			// ///////////////////////////////////////////////////////////////////////////////////////
//			TIOM_AppServer.executeAction("action.device.RFIDAction",
//					"disconnect", tparm);
			// ///////////////////////////////////////////////////////////////////////////////////////

			onClear();
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onClosing();
		return true;
	}

	public void onPrint() {
		tableM.acceptText();
		TParm parmM = tableM.getParmValue();
		for (int j = 0; j < parmM.getCount("WASH_NO"); j++) {
			String wash_no = parmM.getValue("WASH_NO", j);
			String sql = " SELECT   B.INV_CODE, D.DEPT_CODE, D.STATION_CODE, D.QTY, D.OUT_QTY,"
					+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, F.DEPT_CHN_DESC,"
					+ " G.STATION_DESC, A.OUT_FLG, H.USER_NAME OWNER, D.START_DATE, D.END_DATE"
					+ " FROM CTS_WASHD A,"
					+ " CTS_CLOTH B,"
					+ " INV_BASE C,"
					+ " CTS_WASHM D,"
					+ " SYS_OPERATOR E,"
					+ " SYS_DEPT F,"
					+ " SYS_STATION G,"
					+ " SYS_OPERATOR H"
					+ " WHERE A.CLOTH_NO = B.CLOTH_NO"
					+ " AND B.INV_CODE = C.INV_CODE"
					+ " AND A.WASH_NO = D.WASH_NO"
					+ " AND D.WASH_CODE = E.USER_ID"
					+ " AND D.DEPT_CODE = F.DEPT_CODE"
					+ " AND D.STATION_CODE = G.STATION_CODE"
					+ " AND B.OWNER = H.USER_ID(+)"
					+ " AND D.WASH_NO = '"
					+ wash_no + "'" + " ORDER BY B.INV_CODE";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			TParm printParm = new TParm();
			Timestamp date = StringTool.getTimestamp(new Date());
			Timestamp start_date = result.getTimestamp("START_DATE", 0);
			Timestamp end_date = result.getTimestamp("END_DATE", 0);
			printParm.setData("TITLE1", "TEXT", Operator
					.getHospitalCHNShortName());
			if (result.getValue("PAT_FLG", 0).equals("Y")) {
				printParm.setData("TITLE2", "TEXT", "病患洗衣登记单");
			} else {
				printParm.setData("TITLE2", "TEXT", "员工洗衣登记单");
			}
			printParm.setData("PROGRAM", "TEXT", "洗衣清点");
			printParm.setData("PRINT_DATE", "TEXT", date.toString().substring(
					0, 10).replaceAll("-", "/"));
			printParm.setData("PRINT_NO", "TEXT", wash_no);
			printParm.setData("DEPT_CHN_DESC", "TEXT", result.getValue(
					"DEPT_CHN_DESC", 0));
			printParm.setData("STATION_DESC", "TEXT", result.getValue(
					"STATION_DESC", 0));
			printParm.setData("QTY", "TEXT", result.getValue("QTY", 0));
			printParm.setData("OUT_QTY", "TEXT", result.getValue("OUT_QTY", 0));
			printParm.setData("START_DATE", "TEXT", start_date.toString()
					.substring(0, 19).replaceAll("-", "/"));
			printParm.setData("END_DATE", "TEXT", end_date.toString()
					.substring(0, 19).replaceAll("-", "/"));
			printParm.setData("WASH_CODE", "TEXT", result.getValue("WASH_CODE",
					0));
			String inv_code = result.getValue("INV_CHN_DESC", 0);
			int count = 1;
			TParm clothParm = new TParm();
			for (int i = 1; i < result.getCount("INV_CODE"); i++) {
				if (result.getValue("INV_CHN_DESC", i).equals(inv_code)
						&& result.getValue("OUT_FLG", i).equals("Y")) {
					count++;
				} else {
					clothParm.addData("INV_CODE", inv_code);
					clothParm.addData("COUNT", count);
					inv_code = result.getValue("INV_CHN_DESC", i);
					count = 1;
				}
			}
			clothParm.addData("INV_CODE", inv_code);
			clothParm.addData("COUNT", count);
			TParm clothParm2 = new TParm();
			for (int i = 0; i < clothParm.getCount("INV_CODE"); i++) {
				if (i % 2 == 0) {
					clothParm2.addData("C1", clothParm.getValue("INV_CODE", i));
					clothParm2.addData("C2", clothParm.getValue("COUNT", i));
				} else {
					clothParm2.addData("C3", clothParm.getValue("INV_CODE", i));
					clothParm2.addData("C4", clothParm.getValue("COUNT", i));
				}
				if (i == clothParm.getCount("INV_CODE") - 1 && i % 2 == 0) {
					clothParm2.addData("C3", "");
					clothParm2.addData("C4", "");
				}
			}
			clothParm2.setCount(clothParm2.getCount("C1"));
			clothParm2.addData("SYSTEM", "COLUMNS", "C1");
			clothParm2.addData("SYSTEM", "COLUMNS", "C2");
			clothParm2.addData("SYSTEM", "COLUMNS", "C3");
			clothParm2.addData("SYSTEM", "COLUMNS", "C4");
			printParm.setData("TABLE", clothParm2.getData());
			this.openPrintWindow("%ROOT%\\config\\prt\\CTS\\CTSRegList.jhw",
					printParm);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				System.out.println("Consumer:->Received: " + msg);
				List<RFIDTag> lt = xml2rfidTag(msg);
				if (lt != null && lt.size() > 0) {
					for (RFIDTag rfidTag : lt) {
						String rifd = "";
						rfidTag.getEpc();
//						messageBox(rfidTag.getAntenna());
//						if(rfidTag.getAntenna().equals(ant_id)){
							rifd = rfidTag.getEpc();
//							messageBox(rifd);
							setValue("CLOTH_NO", rifd);
							Timestamp date = StringTool.getTimestamp(new Date());
							System.out.println("rfid===="+rifd);
							System.out.println("中间键读到桌面上的时间：====="+date);
							onEnter();
							Timestamp date1 = StringTool.getTimestamp(new Date());
							System.out.println("执行完回车事件后的时间：====="+date1);
							
//						}
						// messageBox(rifd);
					}

				}
//				for (String s : set) {
//					setValue("CLOTH_NO", s);
//					System.out.println(s);
//					onEnter();
//				}
//				set = new HashSet<String>();
			} else {
//				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

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
		// temp =
		// "<?xml version='1.0' encoding='UTF-8' ?><taglist><tag><epc>120034118801010865003102</epc><readerid>D001</readerid><antenna>1</antenna><direction></direction><time>2007-08-31 18:20:34.863</time></tag></taglist>";

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
				Timestamp date = StringTool.getTimestamp(new Date());
				System.out.println("----epc----时间：====="+date);
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
				// System.out.println("----readerid----" + t.getNodeValue());
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				// System.out.println("----antenna----" + t.getNodeValue());
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}
			//
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				// System.out.println("----e----" + e);
				t = e.getFirstChild();
				// System.out.println("----direction----" + t);
				//tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				// System.out.println("----direction not null----");
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

	public void onStartListener() {
		final String mqIP = tparm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			messageBox("MQ服务器IP配置参数错误!");
			return;
		}

		this.connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + mqIP + ":61616");
		try {
			this.connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			

			ActiveMQQueue topic = new ActiveMQQueue(mq_desc);
			this.consumer = this.session.createConsumer(topic, "2 > 1", false);//

			// 开始监听
			// 多个消费者 同时临听 （需要多个时测试）
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("--------启动失败----------");
		}
		;

	}
}
