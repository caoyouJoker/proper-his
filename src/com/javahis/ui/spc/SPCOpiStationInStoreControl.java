package com.javahis.ui.spc;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
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

import jdo.ind.IndSysParmTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCNarTrunkBindTool;
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
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.device.PullDriver;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:麻精备药入智能柜Control
 * </p>
 * 
 * <p>
 * Description:麻精备药入智能柜Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shendr 2013.08.07
 * @version 1.0
 */
public class SPCOpiStationInStoreControl extends TControl implements
		MessageListener {

	TTable TABLE_M_N;
	TTable TABLE_D_N;
	TTable TABLE_M_Y;
	TTable TABLE_D_Y;
	TPanel PANEL_N;
	TPanel PANEL_Y;

	private TimerTask logTask; // log任务

	private Timer logTimer;

	private long period = 1 * 1000; // 间隔时间

	private long delay = 1 * 1000; // 延迟时间

	private static int i = 0;

	private String ip = ""; // 智能柜IP

	private String GUARD_IP = ""; // 智能柜门禁IP

	private String RFID_IP = ""; // 智能柜RFID IP

	private String CABINET_ID = ""; // 智能柜ID

	private String request_type; // 单据类型

	private String request_no; // 申请单号

	private String u_type; // 使用单位

	private String out_org_code; // 出库部门

	private String in_org_code; // 入库部门

	private boolean out_flg; // 是否出库

	private boolean in_flg; // 是否入库

	private String dispense_no; // 入库单号

	int seq = 1;

	private int rtn1;

	private TParm tparm = new TParm();

	private String WS_URL = "";

	private String MQ_QUEUE_NAME = "";

	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	ConnectionFactory connectionFactory;
	// Connection ：JMS 客户端到JMS Provider 的连接
	Connection connection = null;
	// Session： 一个发送或接收消息的线程
	Session session;
	//
	MessageConsumer consumer;

	String MQ_IP = "";

	/**
	 * 接收消息
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		TABLE_M_N = getTable("TABLE_M_N");
		TABLE_D_N = getTable("TABLE_D_N");
		TABLE_M_Y = getTable("TABLE_M_Y");
		TABLE_D_Y = getTable("TABLE_D_Y");
		initPage();
		// 初始化智能柜门禁
		int i = PullDriver.init();
		String params = "protocol=TCP,ipaddress=" + GUARD_IP
				+ ",port=4370,timeout=2000,passwd=";
		rtn1 = PullDriver.Connect(params);
		if (rtn1 <= 0) {
			this.messageBox("智能柜初始化失败");
			// getTextField("DISPENSE_NO").setEnabled(false);
			// getTextField("BOX_ESL_ID").setEnabled(false);
		}
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
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
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		//parm.setData("CABINET_IP","192.168.1.203");
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		// add by wangbin 2014/11/24 非智能柜设备访问该页面时IP控件可编辑 START
		if (result.getCount() > 0) {
			CABINET_ID = (String) result.getData("CABINET_ID", 0);
			RFID_IP = (String) result.getData("RFID_IP", 0);
			GUARD_IP = (String) result.getData("GUARD_IP", 0);
			WS_URL = (String) result.getData("ZKRFID_URL", 0);
			MQ_QUEUE_NAME = (String) result.getData("MQ_DESC", 0);
			this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
			this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
			this.setValue("CABINET_IP", ip);
			String mqIp = WS_URL.substring(WS_URL.indexOf("http://") + 7,
					WS_URL.indexOf(":8080"));
			MQ_IP = mqIp;
			tparm.setData("MQ_IP", mqIp);
			tparm.setData("READER_ID", CABINET_ID);
			tparm.setData("WS_URL", WS_URL);
			tparm.setData("MQ_QUEUE_NAME", MQ_QUEUE_NAME);
		} else {
			getTextField("CABINET_IP").setEnabled(true);
		}
		// add by wangbin 2014/11/24 非智能柜设备访问该页面时IP控件可编辑 END
	}

	/**
	 * 构造器
	 */
	public SPCOpiStationInStoreControl() {
		super();
	}

	/**
	 * 读入日志线程
	 */
	public void logSchedule() {
		this.logTimer = new Timer();
		this.logTask = new TimerTask() {
			public void run() {
				onLog();
			}
		};
		this.logTimer.schedule(this.logTask, this.delay, this.period);
	}

	/**
	 * 日志监听
	 */
	public void onLog() {
		String logStr = this.getLog();
		String[] log = logStr.split(",");
		// 监听关门事件
		if ("200".equals(log[4])) {
			TParm logParm = new TParm();
			String dispenseNo = getValueString("DISPENSE_NO");
			logParm.setData("CABINET_ID", CABINET_ID);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "4");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "1");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);
		}
		if ("201".equals(log[4])) {
			TParm logParm = new TParm();
			String dispenseNo = getValueString("DISPENSE_NO");
			logParm.setData("CABINET_ID", CABINET_ID);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "4");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "2");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);
			onStart(); // 监听到关门事件初始化RFID连接
			logTimer.cancel();
			logTask.cancel();
		}
		/*
		 * onStart(); //监听到关门事件初始化RFID连接 logTimer.cancel(); logTask.cancel();
		 */
	}

	private Set<String> set = new HashSet<String>();

	/**
	 * 麻精药入智能柜
	 */
	public void onInstore() {

		// 获取读取到RFID的集合
		TParm talbleparm = TABLE_M_N.getParmValue();
		TParm inserParm = new TParm();
		inserParm.setData("IS_STORE", "Y");
		String dispenseNo = getValueString("DISPENSE_NO");
		inserParm.setData("DISPENSE_NO", dispenseNo);
		inserParm.setData("CABINET_ID", CABINET_ID);
		int count = TABLE_M_N.getRowCount();
		// 根据出库单号查询IND_DISPENSEM表信息

		for (int i = 0; i < count; i++) {
			String containerId = (String) talbleparm.getData("CONTAINER_ID", i);
			inserParm.setData("CONTAINER_ID", containerId);
			if (set.contains(containerId)) {
				TParm result = TIOM_AppServer.executeAction(
						"action.spc.INDDispenseAction", "onInsertToxicStation",
						inserParm);
				if (result == null || result.getErrCode() < 0) {
					return;
				}
				TABLE_M_N.removeRow(i);
				TABLE_D_N.removeRowAll();
				if (TABLE_M_N.getRowCount() == 0) {
					onEnd();
				}
				break;
			}
		}

	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		String dispenseNo = (String) getValue("DISPENSE_NO");
		String boxId = (String) getValue("BOX_ESL_ID");
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		if (!StringUtil.isNullString(dispenseNo)
				|| !StringUtil.isNullString(boxId)) {
			TParm parm = new TParm();
			if (!StringUtil.isNullString(dispenseNo)) {
				parm.setData("DISPENSE_NO", dispenseNo);
			}
			if (!StringUtil.isNullString(boxId)) {
				parm.setData("BOX_ESL_ID", boxId);
			}
			if (PANEL_N.isShowing()) {
				parm.setData("IS_STORE", "N");
			} else if (PANEL_Y.isShowing()) {
				parm.setData("IS_STORE", "Y");
			}
			parm.setData("DRUG_CATEGORY", "1");
			TParm result = SPCInStoreTool.getInstance().queryInfo(parm);
			if (result.getCount() <= 0) {
				this.messageBox("没有查询到数据");
				if (PANEL_N.isShowing()) {
					TABLE_M_N.setParmValue(new TParm());
				}
				if (PANEL_Y.isShowing()) {
					TABLE_M_Y.setParmValue(new TParm());
				}
				return;
			}
			if (PANEL_N.isShowing()) {
				TABLE_M_N.setParmValue(result);
			} else if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(result);
			}
			if (!StringUtil.isNullString(boxId)) {
				this.setValue("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			}
			parm.setData("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			result = SPCInStoreTool.getInstance().queryOrg(parm);
			this.setValue("ORG_CODE", result.getData("ORG_CHN_DESC", 0));
			String type = (String) result.getData("REQTYPE_CODE", 0);
			String reqtypeCode = this.getReqtype(type);
			this.setValue("ORDER_TYPE", reqtypeCode);
			// getTextField("DISPENSE_NO").setEnabled(false);
			// getTextField("BOX_ESL_ID").setEnabled(false);
		}
		// logSchedule();
	}

	/**
	 * 查询已入智能柜容器
	 */
	public void onStockInQuery() {
		String dispenseNo = (String) getValue("DISPENSE_NO");
		String boxId = (String) getValue("BOX_ESL_ID");
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		if (!StringUtil.isNullString(dispenseNo)
				|| !StringUtil.isNullString(boxId)) {
			TParm parm = new TParm();
			if (!StringUtil.isNullString(dispenseNo)) {
				parm.setData("DISPENSE_NO", dispenseNo);
			}
			if (!StringUtil.isNullString(boxId)) {
				parm.setData("BOX_ESL_ID", boxId);
			}
			if (PANEL_N.isShowing()) {
				parm.setData("IS_STORE", "N");
			} else if (PANEL_Y.isShowing()) {
				parm.setData("IS_STORE", "Y");
			}
			parm.setData("DRUG_CATEGORY", "1");
			TParm result = SPCInStoreTool.getInstance().queryInfo(parm);
			if (result.getCount() <= 0) {
				this.messageBox("没有查询到数据");
				if (PANEL_N.isShowing()) {
					TABLE_M_N.setParmValue(new TParm());
				}
				if (PANEL_Y.isShowing()) {
					TABLE_M_Y.setParmValue(new TParm());
				}
				return;
			}
			if (PANEL_N.isShowing()) {
				TABLE_M_N.setParmValue(result);
			} else if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(result);
			}
			if (!StringUtil.isNullString(boxId)) {
				this.setValue("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			}
			parm.setData("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			result = SPCInStoreTool.getInstance().queryOrg(parm);
			this.setValue("ORG_CODE", result.getData("ORG_CHN_DESC", 0));
			String type = (String) result.getData("REQTYPE_CODE", 0);
			String reqtypeCode = this.getReqtype(type);
			this.setValue("ORDER_TYPE", reqtypeCode);
		}
	}

	/**
	 * 获取table对象
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.setValue("DISPENSE_NO", "");
		this.setValue("BOX_ESL_ID", "");
		this.setValue("ORG_CODE", "");
		this.setValue("ORDER_TYPE", "");
		this.getTTable("TABLE_M_N").removeRowAll();
		this.getTTable("TABLE_D_N").removeRowAll();
		this.getTTable("TABLE_M_Y").removeRowAll();
		this.getTTable("TABLE_D_Y").removeRowAll();
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 扫描出库单
	 */
	public void DisClick() {
		onQuery();
	}

	/**
	 * 开启麻精药柜门
	 * 
	 * @param type
	 * @param ip
	 * @return
	 */
	private int openZNDor(int type, String ip) {
		int ret = 0;
		int operid = 1;
		int doorid = type;
		int outputadr = 1;
		int doorstate = 6;
		ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
				doorstate, 0, "");
		if (ret < 0) {
			return 0;
		}
		return 1;
	}

	/**
	 * 未入智能柜主table点击事件
	 */
	public void onTableMClicked() {
		int row = TABLE_M_N.getSelectedRow();
		TParm rowParm = TABLE_M_N.getParmValue();
		int seqNo = rowParm.getInt("DISPENSE_SEQ_NO", row);
		String disNo = (String) rowParm.getData("DISPENSE_NO", row);
		String containerNo = (String) rowParm.getData("CONTAINER_ID", row);
		TParm parm = new TParm();
		parm.setData("DISPENSE_SEQ_NO", seqNo);
		parm.setData("DISPENSE_NO", disNo);
		parm.setData("CONTAINER_ID", containerNo);
		TParm result = SPCNarTrunkBindTool.getInstance().queryNarTrunkBindMX(
				parm);
		TABLE_D_N.removeRowAll();
		TABLE_D_N.setParmValue(result);
	}

	/**
	 * 已入智能柜主table点击事件
	 */
	public void onTableMClicked1() {
		int row = TABLE_M_Y.getSelectedRow();
		TParm rowParm = TABLE_M_Y.getParmValue();
		int seqNo = rowParm.getInt("DISPENSE_SEQ_NO", row);
		String disNo = (String) rowParm.getData("DISPENSE_NO", row);
		String containerNo = (String) rowParm.getData("CONTAINER_ID", row);
		TParm parm = new TParm();
		parm.setData("DISPENSE_SEQ_NO", seqNo);
		parm.setData("DISPENSE_NO", disNo);
		parm.setData("CONTAINER_ID", containerNo);
		TParm result = SPCNarTrunkBindTool.getInstance().queryNarTrunkBindMX(
				parm);
		TABLE_D_Y.removeRowAll();
		TABLE_D_Y.setParmValue(result);
	}

	/**
	 * 得到单号类型　
	 * 
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype) {
		if (StringUtil.isNullString(reqtype)) {
			return "";
		}
		if (reqtype.equals("DEP")) {
			return "部门请领";
		} else if (reqtype.equals("TEC")) {
			return "备药生成";
		} else if (reqtype.equals("EXM")) {
			return "补充计费";
		} else if (reqtype.equals("GIF")) {
			return "药房调拨";
		} else if (reqtype.equals("RET")) {
			return "退库";
		} else if (reqtype.equals("WAS")) {
			return "损耗";
		} else if (reqtype.equals("THO")) {
			return "其它出库";
		} else if (reqtype.equals("COS")) {
			return "卫耗材领用";
		} else if (reqtype.equals("THI")) {
			return "其它入库";
		} else if (reqtype.equals("EXM")) {
			return "科室备药";
		}
		return "";
	}

	/**
	 * 智能柜开门
	 * 
	 * @param type
	 */
	public void onOpen(int type) {
		// 5.开门操作 日志正常，但设备正常开 (成功)
		int ret = 0;
		int operid = 1;
		int doorid = type;
		int outputadr = 1;
		int doorstate = 6;
		ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
				doorstate, 0, "");
	}

	/**
	 * 读日志
	 * 
	 * @return
	 */
	private String getLog() {
		byte[] data = new byte[256];
		int rtn2 = PullDriver.GetRTLog(rtn1, data, 256);
		if (rtn1 < 0) {
		}
		String strData = byte2Str(data);
		return strData;
	}

	/**
	 * 将 byte转成符号
	 */
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		return Total_Data;
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
    	// add by wangbin 2141124 取不到智能柜编号时提示 START
    	if (StringUtils.isEmpty(getValueString("CABINET_ID"))) {
    		this.messageBox("智能柜编号不能为空");
    		return;
    	}
    	// add by wangbin 2141124 取不到智能柜编号时提示 END
		// onEnd();
		/*
		 * TABLE_M_N = getTable("TABLE_M_N"); int num =
		 * TABLE_M_N.getParmValue().getCount(); onEnd(); if (num <= 0) {
		 * this.messageBox("药品全部入智能柜成功"); return; } String msg = "尚有" + num +
		 * "个容器未入智能柜"; this.messageBox(msg);
		 */
		// 获取读取到RFID的集合
		TParm talbleparm = TABLE_M_N.getParmValue();
		TParm inserParm = new TParm();
		inserParm.setData("IS_STORE", "Y");
		String dispenseNo = getValueString("DISPENSE_NO");
		inserParm.setData("DISPENSE_NO", dispenseNo);
		inserParm.setData("CABINET_ID", CABINET_ID);
		int count = TABLE_M_N.getRowCount();
		// 根据出库单号查询IND_DISPENSEM表信息
		for (int i = 0; i < count; i++) {
			String containerId = (String) talbleparm.getData("CONTAINER_ID", i);
			inserParm.setData("CONTAINER_ID", containerId);
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.INDDispenseAction", "onInsertToxicStation",
					inserParm);
			if (result == null || result.getErrCode() < 0) {
				return;
			}
			TABLE_M_N.removeRow(i);
			TABLE_D_N.removeRowAll();
			if (TABLE_M_N.getRowCount() == 0) {
			}
			break;
		}
		this.messageBox("保存成功");
	}

	/**
	 * 关闭资源
	 */
	public void onCloseCabinet() {
		// PullDriver.Disconnect(rtn1);
		// 注销dll
		// PullDriver.free();
		// onEnd();
		this.closeWindow();
	}

	/**
	 * 查询智能柜药品信息
	 */
	public void onSearch() {
		TParm parm = new TParm();
		parm.setData("CABINET_ID", CABINET_ID);
		Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x",
				parm);
	}

	/**
	 * 入库作业
	 * 
	 * @param out_org_code
	 * @param in_org_code
	 * @param batchvalid
	 */
	private void getDispenseOutIn(String out_org_code, String in_org_code,
			String batchvalid, boolean out_flg, boolean in_flg, TParm mParm,
			TParm dParm, TParm insertParm) {
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO", insertParm.getData("DISPENSE_NO"));
		parm.setData("CONTAINER_ID", insertParm.getData("CONTAINER_ID"));
		parm.setData("CABINET_ID", insertParm.getData("CABINET_ID"));
		parm.setData("IS_STORE", insertParm.getData("IS_STORE"));
		// 主项信息(OUT_M)
		parm = getDispenseMParm(parm, "3", mParm, dParm);
		// 细项信息(OUT_D)
		if (!CheckDataD(dParm)) {
			return;
		}
		parm = getDispenseDParm(parm, dParm);
		if (!"".equals(in_org_code)) {
			// 入库部门(IN_ORG)
			parm.setData("IN_ORG", in_org_code);
			// 是否入库(IN_FLG)
			parm.setData("IN_FLG", in_flg);
		}
		// 执行数据新增
		TParm result = TIOM_AppServer.executeAction(
				"action.spc.INDDispenseAction", "onInsertToxic", parm);
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			// this.messageBox("E0001");
			return;
		}
	}

	/**
	 * 获得主项信息
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseMParm(TParm parm, String update_flg, TParm mParm,
			TParm dparm) {
		// 药库参数信息
		TParm sysParm = getSysParm();
		// 是否回写购入价格
		String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
		parm.setData("REUPRICE_FLG", reuprice_flg);
		TParm parmM = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		parmM.setData("DISPENSE_NO", mParm.getValue("DISPENSE_NO"));
		// 申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
		parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE"));
		parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO"));
		parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE"));
		parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE"));
		parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE"));
		parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG"));
		parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION"));
		parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE"));
		parmM.setData("DISPENSE_USER", Operator.getID());
		if (!"1".equals(update_flg)) {
			parmM.setData("WAREHOUSING_DATE", mParm
					.getValue("WAREHOUSING_DATE"));
			parmM.setData("WAREHOUSING_USER", Operator.getID());
		} else {
			parmM.setData("WAREHOUSING_DATE", tnull);
			parmM.setData("WAREHOUSING_USER", "");
		}
		// 药品种类--普药:1,麻精：2
		parmM.setData("DRUG_CATEGORY", "1");
		parmM.setData("REASON_CHN_DESC", "");
		parmM.setData("UNIT_TYPE", mParm.getValue("UNIT_TYPE"));
		if (dparm.getDouble("OUT_QTY") + dparm.getDouble("INSTORE_QTY") >= dparm
				.getDouble("ACTUAL_QTY")
				&& mParm.getInt("ORDERCOUNT") == 1) {
			update_flg = "3";
		} else {
			update_flg = "1";
		}
		parmM.setData("UPDATE_FLG", update_flg);
		parmM.setData("OPT_USER", Operator.getID());
		parmM.setData("OPT_DATE", date);
		parmM.setData("OPT_TERM", Operator.getIP());
		parmM.setData("REGION_CODE", Operator.getRegion());
		if (parmM != null) {
			parm.setData("OUT_M", parmM.getData());
		}
		return parm;
	}

	/**
	 * 获得明细信息
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseDParm(TParm parm, TParm rowParm) {
		TParm parmD = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		String batch_no = "";
		int count = 0;
		String order_code = "";
		String valid_date = "";
		parmD.setData("DISPENSE_NO", count, rowParm.getValue("DISPENSE_NO"));
		parmD.setData("SEQ_NO", count, rowParm.getValue("SEQ_NO"));
		parmD.setData("REQUEST_SEQ", count, rowParm.getInt("REQUEST_SEQ"));
		order_code = rowParm.getValue("ORDER_CODE");
		parmD.setData("ORDER_CODE", count, order_code);
		parmD.setData("QTY", count, rowParm.getDouble("QTY"));
		parmD.setData("UNIT_CODE", count, rowParm.getValue("UNIT_CODE"));
		parmD.setData("RETAIL_PRICE", count, rowParm.getDouble("RETAIL_PRICE"));
		parmD.setData("STOCK_PRICE", count, rowParm.getDouble("STOCK_PRICE"));
		parmD.setData("ACTUAL_QTY", count, rowParm.getDouble("ACTUAL_QTY"));
		parmD.setData("PHA_TYPE", count, rowParm.getValue("PHA_TYPE"));
		parmD.setData("BATCH_SEQ", count, rowParm.getInt("BATCH_SEQ"));
		parmD.setData("SUP_CODE", count, rowParm.getValue("SUP_CODE"));
		parmD.setData("REGION_CODE", count, Operator.getRegion());
		batch_no = rowParm.getValue("BATCH_NO");
		parmD.setData("BATCH_NO", count, batch_no);
		valid_date = rowParm.getValue("VALID_DATE");
		if ("".equals(valid_date)) {
			parmD.setData("VALID_DATE", count, tnull);
		} else {
			parmD.setData("VALID_DATE", count, rowParm
					.getTimestamp("VALID_DATE"));
		}
		parmD.setData("DOSAGE_QTY", count, rowParm.getDouble("DOSAGE_QTY"));
		parmD.setData("INSTORE_QTY", count, rowParm.getDouble("OUT_QTY"));
		parmD.setData("OLD_INSTORE_QTY", count, rowParm
				.getDouble("INSTORE_QTY"));
		parmD.setData("STOCK_QTY", count, rowParm.getDouble("STOCK_QTY"));
		parmD.setData("OPT_USER", count, Operator.getID());
		parmD.setData("OPT_DATE", count, date);
		parmD.setData("OPT_TERM", count, Operator.getIP());
		parmD.setData("SUP_CODE", count, rowParm.getValue("SUP_CODE"));
		parmD.setData("INVENT_PRICE", count, rowParm.getDouble("INVENT_PRICE"));
		count++;
		if (parmD != null) {
			parm.setData("OUT_D", parmD.getData());
		}
		return parm;
	}

	/**
	 * 药库参数信息
	 * 
	 * @return TParm
	 */
	private TParm getSysParm() {
		return IndSysParmTool.getInstance().onQuery();
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataD(TParm dParm) {
		double qty = dParm.getDouble("OUT_QTY");
		if (qty <= 0) {
			this.messageBox("入库数数量不能小于或等于0");
			return false;
		}
		double price = dParm.getDouble("STOCK_PRICE");
		if (price <= 0) {
			this.messageBox("成本价不能小于或等于0");
			return false;
		}
		return true;
	}

	/**
	 * 其他入库作业
	 * 
	 * @param in_org_code
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void getDispenseOtherIn(String in_org_code, boolean in_flg,
			TParm mParm, TParm dParm) {

		TParm parm = new TParm();
		parm.setData("ORG_CODE", in_org_code);
		// 使用单位
		parm.setData("UNIT_TYPE", u_type);
		// 申请单类型
		parm.setData("REQTYPE_CODE", request_type);
		parm = getDispenseMParm(parm, "3", mParm, dParm);
		// 细项信息(OUT_D)
		if (!CheckDataD(dParm)) {
			return;
		}
		parm = getDispenseDParm(parm, dParm);
		if (!"".equals(in_org_code)) {
			// 入库部门(IN_ORG)
			parm.setData("IN_ORG", in_org_code);
			// 是否入库(IN_FLG)
			parm.setData("IN_FLG", in_flg);
		}
		// 执行数据新增
		parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
				"onInsertOtherIn", parm);
		// 保存判断
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * 入库类型赋值
	 * 
	 * @param mParm
	 */
	public void setValueAll(TParm mParm) {
		request_type = mParm.getValue("REQTYPE_CODE");
		if ("TEC".equals(request_type) || "EXM".equals(request_type)
				|| "COS".equals(request_type)) {
			u_type = "1";
		} else if ("DEP".equals(request_type)) {
			u_type = IndSysParmTool.getInstance().onQuery().getValue(
					"UNIT_TYPE", 0);
		} else {
			u_type = "0";
		}
		if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = true;
		} else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
			out_org_code = mParm.getValue("APP_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("TO_ORG_CODE");
			in_flg = true;
		} else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = false;
		} else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
			out_org_code = mParm.getValue("APP_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("TO_ORG_CODE");
			in_flg = false;
		} else if ("THI".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = false;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = true;
		}
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 开始扫描
	 */
	public void onStart() {
		TIOM_AppServer.executeAction("action.device.RFIDAction", "connect",
				tparm);
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + MQ_IP
						+ ":61616");
		try {
			this.connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			ActiveMQQueue topic = new ActiveMQQueue(MQ_QUEUE_NAME);
			this.consumer = this.session.createConsumer(topic, "2 > 1", false);//
			// 开始监听
			// 多个消费者 同时临听 （需要多个时测试）
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	/**
	 * 停止操作
	 */
	public void onEnd() {
		try {
			if (consumer != null)
				consumer.close();
			if (session != null)
				this.session.close();
			if (connection != null)
				this.connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		TIOM_AppServer.executeAction("action.device.RFIDAction", "disconnect",
				tparm);
		PullDriver.Disconnect(rtn1);
		// 注销dll
		PullDriver.free();
	}

	/**
	 * 将xml转成tag
	 * 
	 * @param temp
	 * @return
	 */
	private List<RFIDTag> xml2rfidTag(String temp) {
		List<RFIDTag> list = new ArrayList<RFIDTag>();
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
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
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
	 * MQ监听方法
	 */
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				List<RFIDTag> tagList = this.xml2rfidTag(msg);
				for (RFIDTag tag : tagList) {
					String c = tag.getEpc();
					if (set.contains(c)) {
						continue;
					} else {
						set.add(c);
					}
				}
				onInstore();
			} else {
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过手动输入智能柜IP地址完成入库
	 * 
	 * @author wangbin 2014/11/24
	 */
	public void onQueryCabinet() {
		ip =  this.getValueString("CABINET_IP").trim();
		if (ip.length() < 1) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		// add by wangbin 2014/11/19 非智能柜设备访问该页面时IP控件可编辑 START
		if (result.getCount() > 0) {
			CABINET_ID = (String) result.getData("CABINET_ID", 0);
			RFID_IP = (String) result.getData("RFID_IP", 0);
			GUARD_IP = (String) result.getData("GUARD_IP", 0);
			WS_URL = (String) result.getData("ZKRFID_URL", 0);
			MQ_QUEUE_NAME = (String) result.getData("MQ_DESC", 0);
			this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
			this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
			this.setValue("CABINET_IP", ip);

			String mqIp = WS_URL.substring(WS_URL.indexOf("http://") + 7,
					WS_URL.indexOf(":8080"));
			MQ_IP = mqIp;
			tparm.setData("MQ_IP", mqIp);
			tparm.setData("READER_ID", CABINET_ID);
			tparm.setData("WS_URL", WS_URL);
			tparm.setData("MQ_QUEUE_NAME", MQ_QUEUE_NAME);
		} else {
			this.messageBox("无此IP地址的智能柜信息");
			this.setValue("CABINET_IP", "");
			this.setValue("CABINET_ID", "");
			this.setValue("CABINET_DESC", "");
			this.setValue("ORG_CODE", "");
			this.grabFocus("CABINET_IP");
			return;
		}
	}

}
