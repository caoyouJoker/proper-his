package jdo.ekt;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import java.util.Map;

import com.dongyang.db.TConnection;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.javahis.util.JavaHisDebug;

import java.util.HashMap;
import java.util.Iterator;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;

import jdo.sys.PatTool;

import java.util.List;
import java.util.ArrayList;

import com.javahis.device.EktDriver;

import java.util.Date;
import java.net.URL;

import com.dongyang.manager.TIOM_AppServer;

import java.applet.AudioClip;
import java.applet.Applet;

import org.apache.bcel.generic.IF_ICMPEQ;

import com.javahis.device.NJSMCardDriver;
import com.javahis.device.ReadCardService;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.device.CardRW;
import com.javahis.device.card.IccCardRWUtil;

import jdo.reg.PatAdmTool;
import jdo.reg.ws.RegQETool;
import jdo.odo.OPDAbnormalRegTool;
import jdo.opb.OPBReceiptTool;
import jdo.opd.OrderTool;

public class EKTIO extends TJDODBTool {
	/**
	 * 实例
	 */
	private static EKTIO instanceObject;
	private String COM = getEktPort();// 泰心医疗卡参数，默认病案号键值名称

	/**
	 * 得到实例
	 * 
	 * @return EKTIO
	 */
	public static EKTIO getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTIO();
		return instanceObject;
	}

	/**
	 * ekt开关
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}

	/**
	 * 医疗卡开关
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}

	/**
	 * 创建卡
	 * 
	 * @param cardNo
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param balance
	 *            double
	 * @return boolean
	 */
	public boolean createCard(String cardNo, String mrNo, double balance) {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("ID_NO", "none");
		parm.setData("MR_NO", mrNo);
		// parm.setData("CASE_NO", caseNo);
		parm.setData("NAME", PatTool.getInstance().getNameForMrno(mrNo));
		parm.setData("CURRENT_BALANCE", balance);
		parm.setData("CREAT_USER", Operator.getID());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		return createCard(parm.getData());
	}
	public boolean unConsume(String tredeNo, TControl control) {
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			if (control != null)
				control.messageBox(result);
			return false;
		}
		String sdatetime = StringTool.getString(TJDODBTool.getInstance()
				.getDBTime(), "yyyy-MM-dd HH:mm:ss");
		result = EktDriver
				.unConsume(1000, Operator.getID(), tredeNo, sdatetime);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			if (control != null)
				control.messageBox(result);
			return false;
		}
		EktDriver.close();
		beep(1);
		consumeCancel(tredeNo);
		return true;
	}

	/**
	 * 判断卡片是否存在途中
	 * 
	 * @param cardNo
	 *            String
	 * @return String
	 */
	public String queryTredeNoCancel(String cardNo) {
		if (isClientlink())
			return (String) callServerMethod(cardNo);
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		TParm result = EKTTool.getInstance().queryTradeNoCancel(parm);
		if (result.getCount() <= 0)
			return "";
		return result.getValue("TREDE_NO", 0);
	}

	/**
	 * 取消交易
	 * 
	 * @param tredeNo
	 *            String
	 * @return boolean
	 */
	public boolean consumeCancel(String tredeNo) {
		if (isClientlink())
			return (Boolean) callServerMethod(tredeNo);
		return EKTTool.getInstance().consumeCancel(tredeNo);
	}

	/**
	 * 扣费
	 * 
	 * @param p
	 *            TParm
	 * @return TParm
	 */
	public TParm consume(TParm p) {
		TParm parm = new TParm();
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		String sdatetime = StringTool.getString(p.getTimestamp("OPT_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		double value = p.getDouble("AMT");
		result = EktDriver.consume(1000, Operator.getID(), value, sdatetime);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		TConnection connection = getConnection();
		String tredeNo = result.substring(3);
		p.setData("TREDE_NO", tredeNo);
		p.setData("AMT", value);
		p.setData("STATE", 0);
		p.setData("OPT_USER", Operator.getID());
		p.setData("OPT_TERM", Operator.getIP());
		p.setData("BANK_FLG", "N");// 银行卡操作
		p.setData("INS_FLG", "N");// 医保卡操作
		parm.setData("TREDE_NO", tredeNo);
		if (!insetTrede(p.getData())) {
			parm.setErr(-1, "医疗卡写交易档错误!");
		}
		connection.commit();
		connection.close();
		EktDriver.close();
		return parm;
	}

	/**
	 * 扣费操作
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            int 1：泰心医院医疗卡扣分操作
	 * @return TParm ==================pangben 20110910
	 */
	public TParm consume(TParm parm, int flg) {
		TParm result = null;

		switch (flg) {

		case 1:
			parm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));
			result = TXwriteEKTATM(parm, parm.getValue("MR_NO"));
			if (result.getErrCode() < 0) {
				System.out.println("医疗卡扣款失败");
				return result;
			}
			String tredeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
			result.setData("TREDE_NO", tredeNo);
			parm.setData("TREDE_NO", tredeNo);
			// parm.setData("AMT", parm.getDouble("AMT"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("STATE", 1);// 已扣款状态
			// }
			if (!insetTrede(parm.getData())) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
				writeParm.setData("SEQ", parm.getValue("SEQ"));
				writeParm.setData("MR_NO", parm.getValue("MR_NO"));
				writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
						parm.getValue("MR_NO")); // 回写医疗卡金额
				if (writeParm.getErrCode() < 0)
					System.out.println("err:" + writeParm.getErrText());
				parm.setErr(-1, "医疗卡写交易档错误!");
			}
			break;
		}

		return parm;
	}

	/**
	 * 创建医疗卡信息
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean insetTrede(Map map) {
		if (isClientlink())
			return (Boolean) callServerMethod(map);
		TParm parm = new TParm(map);
		// =====20120225 zhangp modify start
		parm.setData("BANK_FLG", "N");
		// =====20120225 zhangp modify end
		TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrede " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		// 存在绿色通道金额
		if (parm.getValue("FLG").equals("Y")) {
			// 更新挂号主档表中绿色通道金额
			result = PatAdmTool.getInstance().updateEKTGreen1(
					ektParmTemp(parm), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return false;
			}
		}
		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * 创建医疗卡信息 泰心医院医疗卡操作
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean insetTrede(Map map, TConnection connection) {
		if (isClientlink())
			return (Boolean) callServerMethod(map, connection);
		TParm parm = new TParm(map);
		// TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrede " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		// 存在绿色通道金额
		if (parm.getValue("FLG").equals("Y")) {
			// 更新挂号主档表中绿色通道金额
			result = PatAdmTool.getInstance().updateEKTGreen1(
					ektParmTemp(parm), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return false;
			}
		}

		return true;
	}

	/**
	 * 医疗卡 绿色通道参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 20111009
	 */
	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// 获得此就诊病患绿色通道金额扣款金额
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("GREEN_BALANCE"), 2));
		return ektParm;
	}

	/**
	 * 保存病案号
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @return boolean
	 */
	public boolean saveMRNO(String mrNo, TControl control) {
		return saveMRNO(mrNo, control, true);
	}

	/**
	 * 保存病案号
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @param beep
	 *            boolean true 蜂鸣
	 * @return boolean
	 */
	public boolean saveMRNO(String mrNo, TControl control, boolean beep) {
		if (EktDriver.init() != 1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox("无卡");
			return false;
		}
		result = EktDriver.loadKey("FFFFFFFFFFFF");
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		String data = StringTool.fill0(mrNo, 32);
		result = EktDriver.writeCard(48, data);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		EktDriver.close();
		if (beep)
			beep(1);
		return true;
	}

	/**
	 * 南京建邺医院医保卡清卡方法 ===============pangben modify 20110808
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @param beep
	 *            boolean
	 * @return boolean
	 */
	public boolean saveMRNO1(TParm parm, TControl control, boolean beep) {
		// ====加载dll======
		if (NJSMCardDriver.init() != 1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		if (NJSMCardYYDriver.init() != -1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		// ====连接情况=====
		if (ReadCardService.LinkReaderPro() != 0) {
			NJSMCardDriver.close();
			control.messageBox("连接失败");
			return false;
		}

		String data = StringTool.fill0(parm.getValue("MR_NO"), 32);
		NJSMCardYYDriver.close();
		return true;
		// ReadCardService.WriteCardInfo(data);
		// if (!result.substring(0, 2).equals("00")) {
		// EktDriver.close();
		// control.messageBox(result);
		// return false;
		// }
		// EktDriver.close();

		// return true;
	}

	/**
	 * 南京建邺医院写入医保卡 ===============pangben modify 20110808
	 * 
	 * @param parm
	 *            TParm
	 * @return int
	 */
	public boolean writeEKT(TParm parm, boolean beep) {
		int i = ReadCardService.WriteCardInfo(parm);
		if (i == 0) {
			return true;
		} else
			return false;

	}

	/**
	 * 读取病案号
	 * 
	 * @return String
	 */
	public String readMRNO() {
		if (EktDriver.init() != 1) {
			System.out.println("EktDriver.init()->EKTDLL init err!");
			return "";
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.open()->" + result);
			return "";
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.hasCard()->无卡");
			return "";
		}
		result = EktDriver.loadKey("FFFFFFFFFFFF");
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.loadKey()->" + result);
			return "";
		}
		result = EktDriver.readCard(48);
		EktDriver.close();
		if (result.length() > 12)
			result = result.substring(result.length() - 12);
		if ("000000000000".equals(result))
			return "";

		return result;
	}

	/**
	 * 读取卡信息
	 * 
	 * @return TParm
	 */
	public TParm getPat() {
		return getPat(true);
	}

	/**
	 * 读取卡信息
	 * 
	 * @param beep
	 *            boolean true 蜂鸣
	 * @return TParm
	 */
	public TParm getPat(boolean beep) {
		TParm parm = new TParm();
		if (EktDriver.init() != 1) {
			parm.setErr(-1, "EKTDLL init err!");
			parm.setData("ERRCode", 1);
			return parm;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setData("ERRCode", "医疗卡读卡错误");
			parm.setErr(-1, result);
			return parm;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setData("ERRCode", "无卡");
			parm.setErr(-1, "无卡!");
			return parm;
		}
		result = EktDriver.readUser();
		String s[] = StringTool.parseLine(result, "|");
		if (!s[0].equals("00")) {
			parm.setData("ERRCode", result);
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		EktDriver.close();

		// 测试是否有在途数据
		String tredeNo = queryTredeNoCancel(s[1]);
		if (tredeNo != null && tredeNo.length() > 0) {
			unConsume(tredeNo, null);
			EktDriver.open();
			result = EktDriver.readUser();
			s = StringTool.parseLine(result, "|");
			EktDriver.close();
		}

		parm.setData("CARD_NO", s[1]);
		parm.setData("CARD_TYPE", s[2]);
		parm.setData("CTZ_CODE", s[3]);
		try {
			parm.setData("CURRENT_BALANCE", Double.parseDouble(s[4]));
		} catch (Exception e) {
		}
		parm.setData("NAME", s[5]);
		parm.setData("SEX", s[6]);
		Timestamp birth = StringTool.getTimestamp(s[7], "yyyy-MM-dd");
		parm.setData("BIRTH_DATE", birth);
		parm.setData("ID", s[8]);
		parm.setData("TEL_NO", s[9]);
		if (s.length < 11)
			parm.setData("ADDRESS", "");
		else
			parm.setData("ADDRESS", s[10]);
		parm.setData("MR_NO", readMRNO());
		if (beep)
			beep(1);
		return parm;
	}

	/**
	 * 读取病案号
	 * 
	 * @param cardNo
	 *            String
	 * @return String
	 */
	public String getMrno(String cardNo) {
		TParm parm = new TParm(
				select("SELECT MR_NO FROM EKT_MASTER WHERE CARD_NO='" + cardNo
						+ "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("MR_NO", 0);
	}

	/**
	 * 创建医疗卡信息
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean createCard(Map map) {
		if (isClientlink())
			return (Boolean) callServerMethod(map);
		TParm parm = new TParm(map);
		TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		result = EKTTool.getInstance().insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		connection.commit();
		connection.close();

		return true;
	}

	/**
	 * 创建医疗卡信息
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param control
	 *            TControl
	 * @return TParm ===============pangben 20111007
	 */
	public TParm createCard(TParm parm, TConnection connection) {
		TParm result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		result = EKTTool.getInstance().insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * 银行卡与医疗卡关联
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateEKTAndBank(TParm parm) {
		TParm result = EKTTool.getInstance().updateEKTAndBank(parm);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 泰心医院扣费接口
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String 就诊号
	 * @param control
	 *            TControl
	 * @return TParm [RX_NO,ORDER_CODE,SEQ_NO,AMT,EXEC_FLG,RECEIPT_FLG,BILL_FLG]
	 *         OP_TYPE 0 医疗卡开关关闭 OP_TYPE 1 扣费 OP_TYPE 2 退费 OP_TYPE 3 取消保存动作
	 *         OP_TYPE 4 无费用操作 OP_TYPE 5 无卡能新增医嘱 OP_TYPE -1 调用参数错误或者不能在后台调用医疗卡
	 *         RX_LIST 处方列表
	 */
	public TParm onOPDAccntClient(TParm parm, String caseNo, TControl control) {
		TParm result = new TParm();
		// 禁止在服务器端调用
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient 服务器端禁止调用本方法");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// 医疗卡流程是否启动
		if (!ektSwitch()) {
			result.setData("OP_TYPE", 0);
			return result;
		}
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm 参数为空");
			result.setData("OP_TYPE", -1);
			return result;
		}
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo 参数为空");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// =========pangben 20110919 修改医疗卡读卡操作
		// TParm readCard = getPat(false);
		TParm readCard = parm.getParm("ektParm");// 泰心医疗卡读卡操作
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("无卡".equals(readCard.getValue("ERRCode")))
				control.messageBox("无卡,新增医嘱保存未收费！");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",新增医嘱保存未收费！");
			result.setData("OP_TYPE", 5);
			return result;

		}

		if (readCard.getErrCode() != 0) {
			result.setData("OP_TYPE", -1);
			control.messageBox("医疗卡操作,中途失败");
			return result;
		}
		//parm.setData("CASE_NO", caseNo);
		// 泰心医院获得卡号
		String cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");//医疗卡金额
		String mrNo = parm.getValue("MR_NO");
		String businessType = parm.getValue("BUSINESS_TYPE");
		//查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
		String ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		String type_flg = parm.getValue("TYPE_FLG");// 退费操作
		String ins_flg = parm.getValue("INS_FLG");// 医保卡注记
		double insAmt = parm.getDouble("INS_AMT");// 医保金额
		String unFlg = parm.getValue("UN_FLG");// 医生修改的医嘱超过医疗卡金额执行的操作
		String tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("此卡片不属于该患者,新增医嘱保存未收费！");
				result.setData("OP_TYPE", 5);
				return result;
			}
			control.messageBox("此卡片不属于该患者!");
			result.setData("OP_TYPE", 3);
			return result;
		}
		int type = 1;
		double ektAMT = 0.00;// 医疗卡执行以后金额
		double ektOldAMT = 0.00;// 医疗卡原来金额，操作失败时使用
		String cancelTrede = null;// 扣款操作失败回滚医疗卡主档数据
		String opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// 医保卡回冲金额添加
		double opbAmt = 0.00;// 门诊医生站操作的金额
		double greenBalance = 0.00;// 绿色通道总扣款金额
		double greenPathTotal = 0.00;// 绿色通道审批金额

		String greenFlg = null;// 判断是否操作绿色通道，添加BIL_OPB_RECP
		// 表PAY_MEDICAL_CARD数据时需要判断为0时的操作
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control 参数为空");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			
			// 扣款金额
			double amt = 0.00;
			// 查询此就诊病患所有数据汇总金额
			//TParm orderSumParm=parm.getParm("orderSumParm");
			//查询此病患已收费未打票的所有数据汇总金额
			//TParm ektSumParm =parm.getParm("ektSumParm");
			amt=parm.getDouble("SHOW_AMT");//未收费的总金额 显示金额
			if (amt == 0) {
				result.setData("OP_TYPE", 6);// 没有需要操作的数据
				return result;
			}
			if (amt != 0) {
				opbAmt = amt;
				p.setData("AMT", amt);
				p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//执行金额(EKT_TRADE 中此次 操作的金额)
				//未打票数据总金额
				readCard.setData("NAME", parm.getValue("NAME"));
				readCard.setData("SEX", parm.getValue("SEX"));
				p.setData("READ_CARD", readCard.getData());
				p.setData("BUSINESS_TYPE", businessType);// 扣款字段
				p.setData("EKT_TRADE_TYPE", ektTradeType);
				p.setData("TYPE_FLG", type_flg);// 退费注记
				p.setData("INS_FLG", ins_flg);// 医保注记
				p.setData("INS_AMT", insAmt);// 医保金额
				p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
				p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
				//p.setData("newParm", parm.getParm("newParm").getData());//操作医生站医嘱，需要操作的医嘱(增删改数据集合)
				TParm r = null;
				// 查询绿色通道使用金额
				TParm tempParm=new TParm();
				tempParm.setData("CASE_NO",caseNo);
				TParm greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
				if (greenParm.getErrCode() < 0) {
					result.setData("OP_TYPE", -1);
					return result;
				}
				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// 医疗卡中金额小于此次收费的金额
					if (null != unFlg && unFlg.equals("Y")) {// 医生修改医嘱操作
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// 绿色通道剩余金额
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// 绿色通道审批金额
						p.setData("unParm", parm.getParm("unParm").getData());
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTOpdChageUI.x", p);
						result.setData("unParm", r.getData());
					} else {
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
					}
				} else {
					r = (TParm) control.openDialog(
							"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
				}
				if (r == null) {
					// System.out.println("asdadasd");
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					result.setData("OP_TYPE", 3);
					return result;
				}
				type = r.getInt("OP_TYPE");
				// 余额不足
				if (type == 2) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (null == unFlg
						|| unFlg.equals("N")
						|| amt <= (oldAmt + greenParm.getDouble(
								"GREEN_BALANCE", 0))) {
					// cardNo = r.getValue("CARD_NO");
					// =========pangben 20111024 start
					ektAMT = r.getDouble("EKTNEW_AMT");// 现在医疗卡中的金额
					ektOldAMT = r.getDouble("OLD_AMT");
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // 收费金额
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // 扣医疗卡金额
						// greenUseAmt=r.getDouble("GREEN_USE");//绿色通道使用金额
						// ektUseAmt= r.getDouble("EKT_USE");//医疗卡使用金额
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // 扣绿色通道金额
					}
					greenBalance = r.getDouble("GREEN_BALANCE"); // 绿色通道未扣款金额
					greenPathTotal = r.getDouble("GREEN_PATH_TOTAL"); // 绿色通道总金额
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
			} 
		}
		double currentBalance = readCard.getDouble("CURRENT_BALANCE");
		List list = onOPDAccnt(parm.getData(), cardNo, caseNo,
				Operator.getID(), Operator.getIP(), type, currentBalance,
				ektAMT, greenBalance, greenPathTotal,businessType);
		result.setData("EKTNEW_AMT", ektAMT);
		result.setData("OLD_AMT", ektOldAMT); // 医疗卡已经交易以后的金额
		if (list == null) {
			result.setData("OP_TYPE", 4);
			return result;
		}
		result.setData("OP_TYPE", type);
		result.setData("RX_LIST", list);
		result.setData("TRADE_NO", tradeNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// 医生修改医嘱操作
		result.setData("CANCLE_TREDE", cancelTrede);// 撤销使用
		result.setData("OPB_AMT", opbAmt);// 门诊医生站操作金额
		result.setData("GREEN_FLG", greenFlg);// 添加BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance); // 绿色通道未扣款金额
		result.setData("GREEN_PATH_TOTAL", greenPathTotal); // 绿色通道总金额
		// 表数据时使用用来管控是否操作绿色通道
		return result;
	}
	/**
	 * 医保退费操作
	 * @return
	 */
	public TParm insUnFee(TParm p, TControl control){
		TParm readCard = TXreadEKT();// 泰心医疗卡读卡操作
		if (null==readCard || null==readCard.getValue("MR_NO")) {
			p.setErr(-1,"获得医疗卡信息失败");
			return p;
		}
		
		p.setData("CURRENT_BALANCE",readCard.getDouble("CURRENT_BALANCE") - p.getDouble("AMT"));
		p.setData("SEQ",readCard.getValue("SEQ"));
		if (!readCard.getValue("MR_NO").equals(p.getValue("MR_NO"))) {
			p.setErr(-1,"病患信息不符");
			return p;
		}
		TParm returnParm=EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
//		TParm r = (TParm)control.openDialog(
//				"%ROOT%\\config\\ekt\\EKTOpdChageUI.x", p);
		return returnParm;
	}
	/**
	 * 
	 * 门诊医疗卡计费接口
	 * 
	 * @param data
	 *            Map ORDER_CODE,RX_NO,SEQ_NO,AMT
	 * @param businessNo
	 *            String
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param cashierCode
	 *            String 交易人员
	 * @param term
	 *            String
	 * @param type
	 *            int
	 * @param currentBalance
	 *            double ektAmt 医疗卡中已经计费的金额
	 * @param greenUseAmt
	 *            绿色通道使用金额
	 * @param ektUseAmt
	 *            医疗卡使用金额
	 * @param greenBalance
	 *            double//绿色通道总扣款金额
	 * @param greenPathTotal
	 *            double//绿色通道审批金额
	 * @return List
	 */
	public List onOPDAccnt(Map data, String cardNo,
			String caseNo, String cashierCode, String term, int type,
			double currentBalance, double ektAmt, double greenBalance,
			double greenPathTotal,String businessType) {
		if (cardNo == null || cardNo.length() == 0)
			return null;
		if (isClientlink())
			return (List) callServerMethod(data, cardNo, caseNo,
					cashierCode, term, type, currentBalance, ektAmt,
					greenBalance, greenPathTotal,businessType);

		TParm ioparm = new TParm(data);
		// 更新医疗卡明细表EKT_ACCNTDETAIL
		// 以票据修改
		String mrNo = ioparm.getValue("MR_NO");
		if (mrNo == null || mrNo.length() == 0) {
			System.out.println("ERR:EKTIO.onOPDAccnt 不存在的CaseNo " + caseNo);
			return null;
		}
		String opdFlg = ioparm.getValue("OPD_UN_FLG");// 医生修改的医嘱超过医疗卡金额执行的操作
		if (null != opdFlg && opdFlg.equals("Y")) {
			TParm unParm = ioparm.getParm("unParm");
			ioparm = unParm;
		}
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");
		List list = new ArrayList();
		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			TParm parm = (TParm) map.get(rxNo);
			if (parm == null) {
				parm = new TParm();
				map.put(rxNo, parm);
			}
			list.add(rxNo);
			parm.addData("ORDER_CODE", ioparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", ioparm.getData("SEQ_NO", i));
			parm.addData("AMT", ioparm.getData("AMT", i));
			parm.addData("BILL_FLG", ioparm.getData("BILL_FLG", i));
		}
		// list.add(pv[3]);
		return list;
	}

	private double getDoubleSum(TParm parm, String name) {
		double amt = 0;
		int count = parm.getCount(name);
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("BILL_FLG", i))
				amt += parm.getDouble(name, i);
		}
		return amt;
	}

	/**
	 * 得到病案号
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getMrNo(String caseNo) {
		if (isClientlink())
			return (String) callServerMethod(caseNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = EKTTool.getInstance().getMrNo(parm);
		return result.getValue("MR_NO", 0);
	}

	/**
	 * 得到处方医疗卡余额
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxBalance(String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxBalance(parm);
		return result.getDouble("CURRENT_BALANCE", 0);
	}

	/**
	 * 得到处方医疗卡原额
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxOriginal(String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxOriginal(parm);
		return result.getDouble("ORIGINAL_BALANCE", 0);
	}

	/**
	 * 得到当前余额
	 * 
	 * @param cardNo
	 *            String
	 * @return TParm
	 */
	public TParm getCurrentBalance(String cardNo) {
		TParm p = new TParm();
		p.setData("CARD_NO", cardNo);
		return EKTTool.getInstance().getEKTCurrentBalance(p);
	}

	/**
	 * 得到卡片号码
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	// zhangp 20120106 注释
	// public String getCardNo(String caseNo) {
	// if (isClientlink())
	// return (String) callServerMethod(caseNo);
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", caseNo);
	// TParm result = EKTTool.getInstance().getCardNo(parm);
	// return result.getValue("CARD_NO", 0);
	// }

	/**
	 * 得到处方费用
	 * 
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxAmt(String cardNo, String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(cardNo, caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxAmt(parm);
		return result.getDouble("AMT", 0);
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		TParm parm = new TParm();
		double[] d = new double[] { 17.5, 21, 16.8, 12.25, 13.13, 39.38, 2.52,
				13.13 };
		for (int i = 0; i < d.length; i++) {
			parm.addData("ORDER_CODE", "1");
			parm.addData("RX_NO", "101007000011");
			parm.addData("AMT", d[i]);
			parm.addData("EXEC_FLG", false);
			parm.addData("RECEIPT_FLG", false);
			parm.addData("BILL_FLG", true);
		}

		// System.out.println(EKTIO.getInstance().getAmt(parm.getData(),"0792D24D","101007000006"));
	}

	/**
	 * 得到本次收费费用总额
	 * 
	 * @param data
	 *            Map
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @return double
	 */
	public double getAmt(Map data, String cardNo, String caseNo) {
		if (isClientlink())
			return (Double) callServerMethod(data, cardNo, caseNo);
		TParm ioparm = new TParm(data);
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");
		// System.out.println("ioParm::" + ioparm);
		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			double amt = ioparm.getDouble("AMT", i);
			if (amt > 0)
				amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
			else if (amt < 0)
				amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
			if (!ioparm.getBoolean("BILL_FLG", i))
				amt = 0;
			// System.out.println("处方" + rxNo + " code " +
			// ioparm.getData("ORDER_CODE",i) + " 金额 " + amt);
			Object value = map.get(rxNo);
			if (value == null)
				map.put(rxNo, amt);
			else
				map.put(rxNo, (Double) value + amt);
		}
		// System.out.println("累计数据 " + map);
		Iterator iterator = map.keySet().iterator();
		double sumamt = 0;
		while (iterator.hasNext()) {
			String rxNo = (String) iterator.next();
			sumamt += (Double) map.get(rxNo);
			double d = getRxAmt(cardNo, caseNo, rxNo);
			// System.out.println("处方 " + rxNo + " 总金额" + d);
			sumamt -= d;
		}
		if (sumamt > 0)
			sumamt = ((int) (sumamt * 100.0 + 0.5)) / 100.0;
		else if (sumamt < 0)
			sumamt = ((int) (sumamt * 100.0 - 0.5)) / 100.0;
		// System.out.println("总费用 " + sumamt);
		return sumamt;
	}

	/**
	 * 计算
	 * 
	 * @param ioparm
	 *            TParm
	 * @param cardNo
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @param cashierCode
	 *            String
	 * @param term
	 *            String
	 * @param pv
	 *            Object[]
	 * @param date
	 *            Timestamp
	 * @param connection
	 *            TConnection
	 * @param type
	 *            int
	 * @param greenUseAmt
	 *            double 绿色通道使用金额
	 * @param ektUseAmt
	 *            double 医疗卡使用金额
	 * @param greenBalance
	 *            double //绿色通道扣款剩余总金额
	 * @param greenPathTotAl
	 *            double //绿色通道审批金额
	 * @return boolean
	 */
	private boolean accnt(TParm ioparm, String cardNo, String mrNo,
			String caseNo, String rxNo, String cashierCode, String term,
			Object[] pv, Timestamp date, TConnection connection, int type,
			double greenBalance, double greenPathTotAl,String businessType) {

		String businessNo = (String) pv[0];
		int index = (Integer) pv[1];
		double originalBalance = (Double) pv[2];// 医疗卡中的金额未扣款前的金额
		double dB = (Double) pv[3];
		TParm p = new TParm();
		p.setData("CASE_NO", caseNo);
		TParm r = new TParm();
		p.setData("RX_NO", rxNo);
		r = EKTTool.getInstance().getDetail(p);
		int count = r.getCount("ORDER_CODE");
		// 医保操作不用添加退款
		for (int i = 0; i < count; i++) {
			index++;
			TParm parm = new TParm();
			parm.setData("BUSINESS_NO", businessNo);
			parm.setData("BUSINESS_SEQ", index);
			parm.setData("CARD_NO", cardNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_CODE", r.getValue("ORDER_CODE", i));
			parm.setData("RX_NO", rxNo);
			parm.setData("SEQ_NO", r.getInt("SEQ_NO", i));
			double amt = -r.getDouble("BUSINESS_AMT", i);// 这一个医嘱需要扣款的金额
			parm.setData("CHARGE_FLG", "2");// 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
			parm.setData("ORIGINAL_BALANCE", originalBalance);// 收费前余额
			parm.setData("BUSINESS_AMT", amt);
			double greenTempAmt = 0.00;// 使用绿色通道的金额
			dB -= amt;
			if (greenBalance < greenPathTotAl) {
				greenTempAmt -= amt;// 使用绿色通道的金额
				if (greenBalance + greenTempAmt >= greenPathTotAl) {// 判断回退金额是否超过审批金额
					originalBalance += greenBalance + greenTempAmt
							- greenPathTotAl;
					greenBalance = greenPathTotAl;
					greenTempAmt = greenPathTotAl - greenBalance;// 补充差额
				} else {// 还在使用绿色通道金额
					greenBalance += greenTempAmt;// 绿色通道剩余金额
					originalBalance = 0;
				}
			} else {
				originalBalance -= amt;
				greenTempAmt = 0;
			}
			parm.setData("CURRENT_BALANCE", originalBalance);
			parm.setData("GREEN_BALANCE", greenBalance);// 绿色通道剩余金额=当前剩余金额-每一笔操作金额
			parm.setData("GREEN_BUSINESS_AMT", greenTempAmt);// 绿色通道每一笔使用金额
			parm.setData("CASHIER_CODE", cashierCode);
			parm.setData("BUSINESS_DATE", date);
			// 1：交易执行完成
			// 2：双方确认完成
			parm.setData("BUSINESS_STATUS", "1");
			// 1：未对帐
			// 2：对账成功
			// 3：对账失败
			parm.setData("ACCNT_STATUS", "1");
			parm.setData("ACCNT_USER", new TNull(String.class));
			parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
			parm.setData("OPT_USER", cashierCode);
			parm.setData("OPT_DATE", date);
			parm.setData("OPT_TERM", term);
			parm.setData("BUSINESS_TYPE", businessType);
			TParm result = EKTTool.getInstance().insertEKTDetail(parm,
					connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return false;
			}
		}
		// }
		// if (null != uEktFlg && uEktFlg.equals("Y")) {// 医疗卡退费操作
		// // 明细添加修改操作通过票据更改数据
		// r = EKTTool.getInstance().detailCancelPrintNo(p, connection);
		// }else{
		r = EKTTool.getInstance().detailCancel(p, connection);
		// }
		if (r.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt.detailCancel " + r.getErrCode()
					+ r.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		if (type == 1) {
			count = ioparm.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				if (!ioparm.getBoolean("BILL_FLG", i))
					continue;
				index++;
				TParm parm = new TParm();
				parm.setData("BUSINESS_NO", businessNo);
				parm.setData("BUSINESS_SEQ", index);
				parm.setData("CARD_NO", cardNo);
				parm.setData("MR_NO", mrNo);
				parm.setData("CASE_NO", caseNo);
				parm.setData("ORDER_CODE", ioparm.getValue("ORDER_CODE", i));
				parm.setData("RX_NO", rxNo);
				parm.setData("SEQ_NO", ioparm.getInt("SEQ_NO", i));

				double amt = ioparm.getDouble("AMT", i);
				if (amt > 0)
					amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
				else if (amt < 0)
					amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
				// if (insFlg) {// 医保退款
				// parm.setData("CHARGE_FLG", "2"); //
				// 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
				// parm.setData("BUSINESS_AMT", -amt);
				// } else {
				parm.setData("CHARGE_FLG", "1"); // 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废)
				parm.setData("BUSINESS_AMT", amt);
				// }
				parm.setData("ORIGINAL_BALANCE", originalBalance); // 收费前余额
				double greenTempAmt = 0.00;
				if (amt > originalBalance) {

					greenTempAmt = amt - originalBalance;// 使用绿色通道的金额
					greenBalance -= greenTempAmt;// 绿色通道剩余金额
					originalBalance = 0;
				} else {
					originalBalance -= amt;// 正常医疗卡扣款
				}

				parm.setData("GREEN_BALANCE", greenBalance);// 绿色通道剩余金额=当前剩余金额-每一笔操作金额
				parm.setData("GREEN_BUSINESS_AMT", greenTempAmt);// 绿色通道每一笔使用金额
				// if (insFlg) {
				// originalBalance += amt;// 医保退款操作
				// } else {
				// }
				parm.setData("CURRENT_BALANCE", originalBalance);
				parm.setData("CASHIER_CODE", cashierCode);
				parm.setData("BUSINESS_DATE", date);
				// 1：交易执行完成
				// 2：双方确认完成

				parm.setData("BUSINESS_STATUS", "1");
				// 1：未对帐
				// 2：对账成功
				// 3：对账失败
				parm.setData("ACCNT_STATUS", "1");
				parm.setData("ACCNT_USER", new TNull(String.class));
				parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
				parm.setData("OPT_USER", cashierCode);
				parm.setData("OPT_DATE", date);
				parm.setData("OPT_TERM", term);
				parm.setData("BUSINESS_TYPE", businessType);
				TParm result = EKTTool.getInstance().insertEKTDetail(parm,
						connection);
				if (result.getErrCode() != 0) {
					err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
							+ result.getErrText());
					connection.rollback();
					connection.close();
					return false;
				}
			}
		}
		pv[0] = businessNo;
		pv[1] = index;
		pv[2] = originalBalance;
		pv[3] = dB;
		return true;
	}

	/**
	 * 测试交易
	 * 
	 * @param tredeNo
	 *            String
	 * @return String
	 */
	public String check(String tredeNo, String caseNo) {
		if (isClientlink())
			return (String) callServerMethod(tredeNo, caseNo);
		return EKTTool.getInstance().check(tredeNo, caseNo);
	}

	/**
	 * 蜂鸣
	 * 
	 * @param times
	 *            int
	 * @return String
	 */
	public static String beep(int times) {
		if (true)
			return "1";
		if (EktDriver.init() != 1) {
			//System.out.println("EktDriver.init() err");
			return "";
		}
		EktDriver.open();
		String value = EktDriver.beep(times);
		EktDriver.close();
		return value;
	}

	/*
	 * public static void main(String args[]) { JavaHisDebug.initClient(); TParm
	 * parm = new TParm(); //MR_NO,CASE_NO,ORDER_CODE,RX_NO,SEQ_NO,AMT for(int j
	 * = 0;j < 2;j++) for(int i = 0;i < 3;i++) { parm.addData("RX_NO",j);
	 * parm.addData("ORDER_CODE", "ORDER_CODE" + i); parm.addData("SEQ_NO", i);
	 * parm.addData("AMT", 10); }
	 * //EKTIO.getInstance().onOPDAccnt(parm.getData()
	 * ,"0001","100113000006","cashierCode","term",1); }
	 */
	/**
	 * 扣款操作
	 * 
	 * @param parm
	 *            TParm
	 * @param control
	 *            TControl
	 * @return TParm
	 */
	public TParm onREGAccntClient(TParm parm, TControl control) {
		TParm result = new TParm();
		// 禁止在服务器端调用
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onREGAccntClient 服务器端禁止调用本方法");
			result.setData("REG_TYPE", -1);
			return result;
		}

		// 参数出现问题
		if (null == parm || null == parm.getValue("CASE_NO")
				|| parm.getValue("CASE_NO").length() <= 0) {
			//System.out.println("ERR:EKTIO.onREGAccntClient TParm 参数为空");
			result.setData("REG_TYPE", -1);
			return result;
		}
		result = TXreadEKT();
		// 读取医疗卡回传数据有问题
		if (result.getErrCode() < 0 || null == result.getValue("MR_NO")
				|| result.getValue("MR_NO").length() <= 0) {
			//System.out.println("ERR:EKTIO.onREGAccntClient 读卡出现错误");
			result.setData("REG_TYPE", -1);
			return result;
		}
		// 不是本人卡
		if (!result.getValue("MR_NO").equals(parm.getValue("MR_NO"))) {
			control.messageBox("此卡片不属于该患者!");
			result.setData("REG_TYPE", 3);
			return result;
		}
		String tredeNo = null;
		// 卡号
		String cardNo = result.getValue("CARD_NO");
		String caseNo = parm.getValue("CASE_NO");
		// 金额
		double oldAmt = result.getDouble("CURRENT_BALANCE");
		// 病案号
		String mrNo = result.getValue("MR_NO");
		// 类型
		String businessType = parm.getValue("BUSINESS_TYPE");
		int type = 1;
		// 交易号
		String businessNo = EKTTool.getInstance().getBusinessNo();
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control 参数为空");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			// 医疗卡金额不等于0
			if (oldAmt != 0) {
				p.setData("AMT", oldAmt);
				p.setData("READ_CARD", parm.getData());
				p.setData("BUSINESS_NO", businessNo);
				p.setData("BUSINESS_TYPE", businessType);
				beep(1);
				TParm r = (TParm) control.openDialog(
						"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
				if (r == null) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					result.setData("OP_TYPE", 3);
					return result;
				}
				// cardNo = r.getValue("CARD_NO");
				type = r.getInt("OP_TYPE");
				// 余额不足
				if (type == 2) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				tredeNo = r.getValue("TREDE_NO");
			} else {
				tredeNo = "无交易" + businessNo;
				//System.out.println("tredeNo:::" + tredeNo);
				TParm wparm = new TParm();
				wparm.setData("TREDE_NO", tredeNo);
				wparm.setData("CARD_NO", cardNo);
				wparm.setData("MR_NO", mrNo);
				wparm.setData("CASE_NO", caseNo);
				wparm.setData("BUSINESS_NO", businessNo);
				wparm.setData("PAT_NAME", PatTool.getInstance().getNameForMrno(
						mrNo));
				wparm.setData("OLD_AMT", oldAmt);
				wparm.setData("AMT", 0);
				wparm.setData("STATE", 1);
				wparm.setData("BUSINESS_TYPE", businessType);
				wparm.setData("OPT_USER", Operator.getID());
				wparm.setData("OPT_TERM", Operator.getIP());
				wparm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
				if (!insetTrede(wparm.getData())) {
					control.messageBox("医疗卡写交易档错误!");
					result.setErr(-1, "医疗卡写交易档错误!");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 泰心医院读医疗卡操作
	 * 
	 * @param commPort
	 *            String 默认参数是“COM1” 返回病案号
	 * @return TParm
	 */
	public TParm readEkt() {

		// 写卡
		/*
		 * TParm param=cardRW.writeEKT("COM1", "123456789987001100.12----");
		 * if(param.getErrCode()==-1){
		 * System.out.println("=========写卡操作出错.============");
		 * System.out.println(param.getErrParm().getErrText()); }
		 */
		VRTool vrTool = new VRTool();
		TParm param1 = new TParm();
		if(vrTool.getVrSwitch()){
			param1 = vrTool.read();
		}else{
			// 读卡
			CardRW cardRW = new CardRW();
			int i = CardRW.init();
			param1 = cardRW.readEKT(COM);
//			System.out.println("param1 is:"+param1);
			if (param1.getErrCode() == -1) {
//				System.out.println("=========读卡操作出错.============");
//				System.out.println(param1.getErrParm().getErrText());
			}
			CardRW.free();
		}
		
		return param1;
	}
	
	public TParm readEktNew(){
		IccCardRWUtil cardDev = new IccCardRWUtil();
		String cardType = cardDev.getCardType();
		TParm result = new TParm();
		if(cardType.equals("INSCard")){
			TParm re = cardDev.readINSCard();
			String cardNo = re.getValue("");
			
			TParm parm = new TParm();
			parm.setData("TEXT", ";"+cardNo+"=");
			TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
			String admDate  = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd"); //拿到当前的时间
			parm.setData("ADVANCE_CODE",regionParm.getValue("NHI_NO", 0)+"@"+
					admDate+"@"+"1");//医院编码@费用发生时间@类别
			 TParm insParm = RegQETool.getInstance().readCardPat(parm);

			 String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
						+ "FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE "
						+ " B.IDNO= '"
						+ insParm.getValue("SID")
						+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y' ORDER BY A.CARD_NO DESC ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			
		}else if(cardType.equals("EKTCard")){
			TParm re = cardDev.readEKT("");
			String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
					+ "FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE A.MR_NO = '"
					+ re.getValue("MR_NO")
					+ "' AND A.CARD_SEQ = '"
					+ re.getValue("SEQ")
					+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			
		}else if(cardType.equals("IDCard")){
			TParm re = cardDev.readIdCard();
			String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
					+ " FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE "
					+ " B.IDNO= '"
					+ re.getValue("IDNO")
					+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y' ORDER BY A.CARD_NO DESC ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			
		}
		return result;
	}

	/**
	 ** 泰心医院读医疗卡操作
	 * 
	 * 
	 * ====zhangp 20120105 判断医疗卡是否有效
	 * 
	 * @param commPort
	 *            String 默认参数是“COM1” 返回病案号
	 * @return TParm zhangp 20120105 判断医疗卡是否有效
	 */
	public TParm TXreadEKT() {
	//modify by huangtt 20171103 将医疗卡脱卡注掉还原到之前版本  start 	
//		String ektSql = "SELECT * FROM EKT_DEV WHERE IP='"+Operator.getIP()+"' AND  DEV_TYPE= 'IDCARD'";
//		TParm ektParm = new TParm(TJDODBTool.getInstance().select(ektSql));
//
//		TParm result = new TParm();
//		if(ektParm.getCount() > 0){
//			TParm parm = readEkt();
//			String mrNo = parm.getValue("MR_NO");
//			String seq = parm.getValue("SEQ");
//			String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
//					+ "FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE A.MR_NO = '"
//					+ mrNo
//					+ "' AND A.CARD_SEQ = '"
//					+ seq
//					+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
//			result = new TParm(TJDODBTool.getInstance().select(sql));
//		}else{
//			
//			result = readEktNew();
//			
//		}
		
		TParm parm = readEkt();
		String mrNo = parm.getValue("MR_NO");
		String seq = parm.getValue("SEQ");
		String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
				+ "FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE A.MR_NO = '"
				+ mrNo
				+ "' AND A.CARD_SEQ = '"
				+ seq
				+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		//modify by huangtt 20171103 将医疗卡脱卡注掉还原到之前版本  end 
		if (result.getErrCode() < 0) {
			result.setErr(-1, "例外错误");
			return result;
		}
		if (result.getCount() <= 0) {
			result.setErr(-1, "此医疗卡无效");
			return result;
		}
		return result.getRow(0);
	}
	/**
	 * 泰心医院写医疗卡操作
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm TXwriteEKT(TParm parm) {
		TParm result = new TParm();
		String currentBalance = parm.getValue("CURRENT_BALANCE");
		String inputData = parm.getValue("MR_NO");
		VRTool vrTool = new VRTool();
		if(vrTool.getVrSwitch()){
			vrTool.write(parm.getValue("SEQ"), inputData, "00", Double.valueOf(currentBalance));
		}else{
			CardRW cardRW = new CardRW();
			CardRW.init();
			result = cardRW.writeEKT(COM, inputData, parm.getValue("SEQ"),
					currentBalance);
			CardRW.free();
		}
		return result;
	}

	/**
	 * 泰心医院医疗卡扣款操作
	 * 
	 * 
	 * ======zhangp 20120106 modify
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm TXwriteEKTATM(TParm parm, String mrNo) {
		//TParm readParm = readEkt();
		// ======zhangp 20120225 modify start 暂时不用
		// String card_no = readParm.getValue("MR_NO") +
		// readParm.getValue("SEQ");
		// String sql =
		// "SELECT CURRENT_BALANCE FROM EKT_MASTER WHERE CARD_NO = '"
		// + card_no + "' AND CURRENT_BALANCE = '"
		// + readParm.getValue("CURRENT_BALANCE") + "'";
		// TParm readBalance = new TParm(TJDODBTool.getInstance().select(sql));
		// if (readBalance.getErrCode() < 0) {
		// readBalance.setErr(-1, "金额错误");
		// return readBalance;
		// }
		// if (readBalance.getCount() <= 0) {
		// readBalance.setErr(-1, "此医疗卡无效");
		// return readBalance;
		// }
		// ======zhangp 20120225 modify end 暂时不用
//		CardRW cardRW = new CardRW();
//		CardRW.init();
//		// 操作医疗卡金额出现不是十位的数字的操作
		String currentBalance = parm.getValue("CURRENT_BALANCE");
		double balance = Double.parseDouble(currentBalance);
//		TParm result = cardRW.writeBalance(COM, currentBalance);
//		CardRW.free();
		String cardNo = parm.getValue("MR_NO") + parm.getValue("SEQ");
		TParm result=new TParm();
		if(!createCard(cardNo, mrNo, balance)){
			result.setErr(-1,"医疗卡写卡动作失败");
			return result;
		}
		return result;
	}
	/**
	 * 得到debug标记
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "ekt.port");
		if (com == null || com.trim().length() <= 0) {
			//System.out.println("配置文件医疗卡com标记错误！");
		}
		return com;
	}

	/**
	 * 读取 TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x 文件没有找到！");
		}
		return config;
	}
}
