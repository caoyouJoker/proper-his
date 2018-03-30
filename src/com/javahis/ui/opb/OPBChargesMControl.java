package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.data.TParm;

import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.sid.IdCardO;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.manager.TCM_Transform;

import jdo.sys.Operator;
import jdo.odi.OdiMainTool;
import jdo.odo.OpdOrder;
import jdo.opd.OrderList;
import jdo.opd.Order;
import jdo.opd.TotQtyTool;

import com.dongyang.ui.TTable;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.opb.OPB;
import jdo.opb.OPBReceipt;
import jdo.opb.OPBTool;
import jdo.reg.PatAdmTool;
import jdo.pha.PhaSysParmTool;


//import jdo.pha.client.PHADosageWsImplService_Client;
//import jdo.pha.client.SpcOpdOrderReturnDto;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktTradeContext;
import com.javahis.util.OdoUtil;

import java.awt.Component;
import java.util.Vector;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;

import jdo.bil.BIL;
import jdo.bil.BilInvoice;

import com.dongyang.util.StringTool;

import jdo.sys.PatTool;

import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TNumberTextField;
import com.javahis.util.OdiUtil;

import jdo.hl7.Hl7Communications;
import jdo.ins.INSIbsTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJReg;

import com.dongyang.jdo.TJDODBTool;

import java.util.List;
import java.util.ArrayList;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewTool;
//kangy  脱卡还原     import jdo.ekt.EKTReadCard;
import jdo.ekt.EKTTool;

import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.opb.OPBReceiptTool;
import jdo.opd.OrderTool;
import jdo.util.Manager;

import com.javahis.util.StringUtil;

import jdo.sys.DeptTool;
import jdo.sys.IReportTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.PATLockTool;
import jdo.sys.SYSRegionTool;

import com.javahis.system.root.RootClientListener;
import com.javahis.device.NJCityInwDriver;

import jdo.ins.INSOpdTJTool;

import com.tiis.util.TiMath;

/**
 * <p>
 * Title:门诊收费系统
 * </p>
 * 
 * <p>
 * Description:门诊收费系统
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author fudw
 * @version 1.0
 */
public class OPBChargesMControl extends TControl {
	// 传入数据
	Object paraObject;
	String systemCode = "";
	String onwType="";
	// opb对象
	OPB opb;
	// pat对象
	Pat pat;
	// reg对象
	Reg reg;
	// BIL 对象
	BIL opbbil;
	// 传入界面传参caseNo
	String caseNoPost;
	// 界面唯一caseNo
	String onlyCaseNo;
	// 传入界面传参mrNo
	String mrNoPost;
	// HL7接口数据
	TParm sendHL7Parm;
	// 当前选中的行
	int selectRow = -1;
	// 是否新增过处方签
	String pFlg = "N";
	// 记录医生开立医嘱的数量
	int drOrderCount = -1;
	// 新建orderlist
	OrderList orderList = null;
	// 收费权限
	boolean bilRight = true;
	// 补充计价权限
	boolean addOrder = true;
	// 全选
	TCheckBox checkBoxChargeAll;
	// 未收费
	TRadioButton checkBoxNotCharge;
	// EKT退费
	public TRadioButton ektTCharge;

	// 全部
	TRadioButton checkAll;
	// 给处方签赋值
	TComboBox comboPrescription;
	// table
	TTable table;
	// 调用系统名
	String systemName;
	// 服务等级
	String serviceLevel;
	// 交易号
    public String tredeNo;
	// 手术室医嘱套餐
	TParm operationParm;
	// 删除医嘱操作：没有就诊直接开立医嘱后频次修改也可以实现删除医嘱操作
	boolean deleteFun = false;
	// 显示删除按钮
	// ==================pangben modify 20110804 删除按钮操作
	int drOrderCountTemp = 0;
	boolean drOrderCountFalse = false;
	boolean feeShow = false; // 金额调用是否执行显示金额的数据保存
	boolean isbill = false; // 是否记账
	public TParm parmEKT; // 读取医疗卡信息
	private boolean EKTmessage = false; // 医疗卡退费输出消息
	private boolean isEKT = false; // 医疗卡操作
	private TParm tredeParm; // 获得当前扣款是否是医疗卡操作
	private TParm insParm = new TParm(); // 医保出参，U 方法 A 方法参数
	private boolean insFlg = false; // 医保卡读取操作
	private TParm resultBill; // 记账数据
	// =====zhangp 20120227 modify start
	private TCheckBox checkBox;
	private TParm regSysParm;//pangben 2013-4-28 挂号有效天数
	// private TParm insMZconfirmParm;// 判断此次就诊是否执行医保操作
	/**
	 * Socket传送门诊药房工具
	 */
	private SocketLink client1;
	private String phaRxNo;//===pangben 2013-5-17 药品审核界面添加跑马灯处方签数据 
	private TParm regionParm; // 获得医保区域代码
	
	private TParm oldOpdOrderParm; //未收费的全部医嘱  huangtt 2141126
	private String MESSAGE = "该病人医嘱已变，请重新查询！";
	
	public TParm ektParmSave; //医疗卡保存时需要的参数 
	
	public String oldMrNo="";
	// kangy 脱卡还原       private boolean dev_flg=false;
	
	

	
	/**
	 * 初始化界面
	 */
	public void onInit() {
		// this.messageBox("权限"+this.getPopedem("NOBILL"));
		super.onInit();
		checkBox = (TCheckBox) getComponent("CHARGEALL");// =====20120227 zhangp
		regSysParm=REGTool.getInstance().getRegParm();//pangben 2013-4-28 挂号有效天数
		paraObject = null;
		paraObject = this.getParameter();
		if (paraObject != null && paraObject.toString().length() > 0) {
			// System.out.println("this.getParameter()+"+this.getParameter());
			TParm paraParm = (TParm) this.getParameter();
			if (paraParm != null && paraParm.getData("SYSTEM") != null) {
				systemCode = paraParm.getValue("SYSTEM");
			}
			if (paraParm != null && paraParm.getData("ONW_TYPE") != null) {
				onwType = paraParm.getValue("ONW_TYPE");//=====pangben 2013-5-15 门急诊护士站解锁使用，监听不同的界面
			}
			
		}
		table = (TTable) getComponent("TABLE");
		comboPrescription = (TComboBox) getComponent("PRESCRIPTION");
		checkBoxNotCharge = (TRadioButton) getComponent("NOTCHARGE");
		ektTCharge = (TRadioButton) getComponent("EKT_R");
		checkAll = (TRadioButton) getComponent("ALL");
		checkBoxChargeAll = (TCheckBox) getComponent("CHARGEALL");
		// table1的侦听事件
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// table1值改变事件
		this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		// table专用的监听
		callFunction("UI|TABLE|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// 账单table专用的监听
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onChangeTableComponent");
		onClear();
		// 初始化界面上的数据
		iniTextValue();
		// 初始化权限
		// initPopedem();
		// BilInvoice bilInvoice = new BilInvoice();
		if (systemCode != "" && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			// ===zhangp 20120306 modify start
			this.callFunction("UI|EKT_R|setEnabled", false);
			// ===zhangp 20120306 modify end
			return;
		}
		// 初始化票据
		// initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
        // 权限为妇科进入
        if (!this.getPopedem("TITLE")) {// wanglong add 20141011 增加更改票据抬头权限
            this.callFunction("UI|TITLE_PANEL|setVisible", false);
        } else {
            this.callFunction("UI|TITLE|setEnabled", false);
        }
       //kangy 脱卡还原         dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());
	}

	/**
	 * 拿到table
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * table checkbox监听事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableComponent(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable("TABLE").getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("CHARGE".equals(columnName)) {
			double fee = getFee();
			if (ektTCharge.isSelected()) {
				callFunction("UI|TOT_AMT|setValue", -fee);
			} else {
				callFunction("UI|TOT_AMT|setValue", fee);
			}
			setFeeReview();
			//======pangben 2013-3-7 修改护士补充计费 添加两条医嘱 第二条为集合医嘱的情况时，
			//将前面的勾选挑掉 ，没有收取第二条医嘱，就会出现 第二条医嘱的细项 在 OPD_ORDER表中状态为收费状态
			if (chargeTable.getParmValue().getValue("SETMAIN_FLG", row).equals("Y")) {
				List list = opb.getPrescriptionList().getOrder();
				for (int i = 0; i < list.size(); i++) {
					// 取一条order
					Order order=(Order)list.get(i);
					if(chargeTable.getParmValue().getValue("RX_NO",row).equals(order.getRxNo())
							&&chargeTable.getParmValue().getInt("ORDERSET_GROUP_NO",row)==order.getOrderSetGroupNo()){
						if(chargeTable.getParmValue().getValue("CHARGE",row).equals("N"))
							((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(false);
						else if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y"))
							((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(true);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 初始化界面上的数据
	 */
	public void iniTextValue() {
		TParm parm = new TParm();
		// 预设就诊日期
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());
		// 当前医生
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// 当前科室
		callFunction("UI|REALDEPT_CODE|setValue", Operator.getDept());
		callFunction("UI|delete|setEnabled", false);
		Object obj = this.getParameter();
		if (obj == null || obj.toString().length() == 0) {
			return;
		}
		if (obj.toString().length() > 1) {
			parm = (TParm) obj;
			if (parm.getValue("CASE_NO") != null) {
				caseNoPost = parm.getData("CASE_NO").toString();
				mrNoPost = parm.getData("MR_NO").toString();
				// systemName = parm.getData("SYSTEM").toString();
				onQueryByCaseNo();
				if (getValue("BILL_TYPE").equals("E")) {//pangben 2013-9-18护士调用补充计费，保存不提示
					txReadEKT();
				}
			}
		}

	}

	/**
	 * 初始化权限
	 */
	public void initPopedem() {
		// 收费权限
		if (!getPopedem("BILCharge")) {
			// 收费权限为true
			bilRight = false;
			// 全选为未选中
			callFunction("UI|CHARGEALL|setValue", "N");
			// 全选checkbox空
			callFunction("UI|CHARGEALL|setEnabled", false);
			// table第一列收费锁定
			callFunction("UI|TABLE|setLockColumns", "0");
			// 置收费按钮为不可编辑
			callFunction("UI|CHARGE|setEnabled", false);

		}
		// 补充计价权限
		if (!getPopedem("ADDORDER")) {
			// 权限为false
			addOrder = false;
		}
	}

	/**
	 * table 点击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// 如果此医嘱是医生开立的,收费员无权删除和编辑
		callFunction("UI|delete|setEnabled", false);
		// int temp=0;
		if (drOrderCountTemp != 0) {
			drOrderCount = drOrderCountTemp;
		}
		if (table.getSelectedRow() >= drOrderCount || deleteFun) {
			callFunction("UI|delete|setEnabled", true);
		}
		// ===zhangp 20120414 start
		Order order = null;
		if (table.getSelectedRow() > -1) {
			if (table.getParmValue().getData("OBJECT", table.getSelectedRow()) instanceof Order)
				order = (Order) table.getParmValue().getData("OBJECT",
						table.getSelectedRow());
		}
		// RxType::医嘱分类0 OR 7：补充计价1：西成药2：管制药品3：中药饮片4：诊疗项目5：检验检查项目
		if (null != order
				&& (order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			callFunction("UI|delete|setEnabled", true);
		}
		// ===zhangp 20120414 end
	}

	/**
	 * 根据病案号带出所有信息
	 */
	public void onQuery() {
		
		// 如果当前有被锁的病患
		checkBoxNotCharge.setSelected(true);
		if (pat != null) {
			this.unLockPat();
		}
		// 设置默认没有新建处方
		pFlg = "N";
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		
		 // modify by huangtt 20160930 EMPI患者查重提示 start
		String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		oldMrNo = srcMrNo;
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	         
	     }
		 // modify by huangtt 20160930 EMPI患者查重提示 start
		
		// 如果锁病患失败则清空数据
		if (!this.lockPat()) {
			return;
		}
		callFunction("UI|record|setEnabled", true);
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		if (((TCheckBox) this.getComponent("CHANGE_TITLE")).isSelected()) {// wanglong add 20141011 增加更改票据抬头权限
			this.setValue("TITLE", pat.getParm().getValue("PAT_NAME"));                                                        
	    }
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate()); // showAge(Timestamp birth,
		// Timestamp sysdate);
		setValue("AGE", age);
		// 查找就诊记录
		// ==========pangben modify 20110421 start
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				(Timestamp) getValue("STARTTIME"),
				(Timestamp) getValue("STARTTIME"), regionCode);
		// ==========pangben modify 20110421 start
		// 查找错误
		if (parm.getCount() < 0) {
			messageBox_("就诊序号选择错误!");
			return;
		}

		// 若挂号信息为0
		if (parm.getCount() == 0) {
			this.messageBox("无今日挂号信息!");
			// 就诊序号选择界面
			onRecord();
			return;
		}
		// 如果今天只有一次挂号信息
		if (parm.getCount() == 1) {
			// 初始化reg
			String caseNo = parm.getValue("CASE_NO", 0);
			reg = Reg.onQueryByCaseNo(pat, caseNo);
			// 判断挂号信息
			if (reg == null) {
				return;
			}
			// reg得到的数据放入界面
			afterRegSetValue();
			// 通过reg和caseNo得到pat
			opb = OPB.onQueryByCaseNo(reg);
			serviceLevel = opb.getReg().getServiceLevel();
			this.setValue("SERVICE_LEVEL", serviceLevel);
			// onlyCaseNo = "";
			onlyCaseNo = opb.getReg().caseNo();
			//添加就诊号是否报道的校验   20130814 yanjing
			String sql = "SELECT ARRIVE_FLG FROM REG_PATADM WHERE CASE_NO = '"+onlyCaseNo+"'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getValue("ARRIVE_FLG",0).equals("N")){
				this.messageBox("此病人尚未报道!");
				this.onClear();
				return;
			}
			//添加就诊号是否报道的校验   20130814 yanjing  end
			// 给界面上部分地方赋值
			if (opb == null) {
				// this.messageBox_(11111111);
				this.messageBox("此病人尚未就诊!");
				return;
			}
			// 初始化opb后数据处理
			afterInitOpb();
			oldOpdOrderParm = getOrder();
			//add by huangtt 20160620 Q医支付数据查询 start
			TLabel qTxt = (TLabel) this.getComponent("Q_TXT");
			int re = getQeOrder();
			if(re == 1){				
				qTxt.setText("Q医已缴费");
			}else if(re == 2){
				qTxt.setText("部分Q医已缴费");
			}else{
				qTxt.setText("");
			}
			//add by huangtt 20160620 Q医支付数据查询 end
			return;
		}
		onRecord();
		// ===zhangp 20120724 start
		// 初始化票据
		BilInvoice bilInvoice = new BilInvoice();
		if (!systemCode.equals("") && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
		} else {
			initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		}
		// ===zhangp 20120724 end
	}

	/**
	 * 创建XML文件 ================================pangben modify 20110806
	 */
	public void createOrderXML() {
		// 1.构造数据
		TParm inparm = new TParm();
		TParm parm = table.getParmValue();
		if (parm.getCount() - 1 <= 0) {
			this.messageBox("没有需要生成的数据");
			return;
		}
		int count = 0;
		for (int i = 0; i < parm.getCount() - 1; i++) {

			inparm.insertData("TBR", i, this.getValue("MR_NO"));
			inparm.insertData("XM", i, this.getValue("PAT_NAME"));
			inparm.insertData("YSM", i, parm.getValue("DR_CODE", i));
			inparm.insertData("BZBM", i, parm.getValue("ORDER_CODE", i));
			inparm.insertData("KSM", i, parm.getValue("DEPT_CODE", i));
			String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
					+ parm.getValue("DR_CODE", i) + "'";
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
			inparm.insertData("YSXM", i, sqlParm.getValue("USER_NAME", 0));
			sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
					+ parm.getValue("DEPT_CODE", i) + "'";
			sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
			inparm.insertData("KSMC", i, sqlParm.getValue("DEPT_CHN_DESC", 0));
			count++;
		}
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "YSM");
		inparm.addData("SYSTEM", "COLUMNS", "BZBM");
		inparm.addData("SYSTEM", "COLUMNS", "KSM");
		inparm.addData("SYSTEM", "COLUMNS", "YSXM");
		inparm.addData("SYSTEM", "COLUMNS", "KSMC");
		inparm.setCount(count);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzgzxx.xml");
		createFeeXML(parm);
	}

	private void createFeeXML(TParm parm) {
		// 1.构造数据
		TParm inparm = new TParm();
		int count = 0;
		for (int i = 0; i < parm.getCount() - 1; i++) {
			inparm.insertData("TBR", i, this.getValue("MR_NO"));
			inparm.insertData("XM", i, this.getValue("PAT_NAME"));
			inparm.insertData("ZBM", i, parm.getValue("ORDER_CODE", i));
			inparm.insertData("SL", i, parm.getValue("DOSAGE_QTY", i));
			inparm.insertData("DJ", i, parm.getValue("OWN_PRICE", i));
			inparm.insertData("YHJ", i, parm.getValue("OWN_PRICE", i));
			if ("PHA".equals(parm.getValue("CAT1_TYPE", i))) {
				inparm.insertData("BZ", i, 0);
			} else {
				inparm.insertData("BZ", i, 1);
			}
			inparm.insertData("YHLB", i, 0);
			count++;
		}
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "ZBM");
		inparm.addData("SYSTEM", "COLUMNS", "SL");
		inparm.addData("SYSTEM", "COLUMNS", "DJ");
		inparm.addData("SYSTEM", "COLUMNS", "YHJ");
		inparm.addData("SYSTEM", "COLUMNS", "BZ");
		inparm.addData("SYSTEM", "COLUMNS", "YHLB");
		inparm.setCount(count);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzcfsj.xml");
		this.messageBox("生成成功");

	}

	/**
	 * 传参界面接参后查询
	 */
	public void onQueryByCaseNo() {
		setValue("MR_NO", mrNoPost);
		// 如果当前有被锁的病患
		unLockPat();
		// 设置默认没有新建处方
		pFlg = "N";
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		// 如果锁病患失败则清空数据
		if (!this.lockPat()) {
			return;
		}
		callFunction("UI|record|setEnabled", true);
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());

		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());
		setValue("AGE", age);
		// 调用界面传参
		reg = Reg.onQueryByCaseNo(pat, caseNoPost);
		// 判断挂号信息
		if (reg == null) {
			return;
		}
		// reg得到的数据放入界面
		afterRegSetValue();
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		if (opb == null) {
			// this.messageBox_(22222222);
			this.messageBox("此病人尚未就诊!");
			return;
		}
		// 初始化opb后数据处理
		afterInitOpbParameter();
		return;
	}

	/**
	 * reg 初始化成功后放入数据
	 */
	public void afterRegSetValue() {
		// 三身份
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		// 就诊科室
		callFunction("UI|DEPT_CODE|setValue", reg.getRealdeptCode());
		// 经治医生
		callFunction("UI|DR_CODE|setValue", reg.getRealdrCode());
		String deptCode = Operator.getDept();
		TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// 执行科室
		if (deptParm.getCount("DEPT_CODE") > 0) {
			callFunction("UI|REALDEPT_CODE|setValue", deptCode);
		} else {
			callFunction("UI|REALDEPT_CODE|setValue", "");
		}
		// 执行医生
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// 预设就诊日期
		this.callFunction("UI|STARTTIME|setValue", reg.getAdmDate());
	}

	/**
	 * 初始化opb后的数据处理
	 */
	public void afterInitOpb() {
		String view = checkAll.getValue().toString();
		// 查询此次就诊是否记账操作
		String sql = "SELECT CASE_NO,CONTRACT_CODE FROM BIL_CONTRACT_RECODE WHERE CASE_NO='"
				+ onlyCaseNo + "'";
		resultBill = new TParm(TJDODBTool.getInstance().select(sql));
		if (resultBill.getCount() > 0) {
			setValue("BILL_TYPE", "P");
		}
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
		} else {
			// 调用显示未收费方法
			onNotCharge();
		}
		if (ektSave().getErrCode() < 0) { // =========pangb 2011-11-29
			// 获得医疗卡信息查看此次扣款是否是医疗卡操作
			this.messageBox("读取医疗卡信息有误");
			tredeParm = null;
			return;
		}
		if (paraObject != null && systemCode != null
				&& "ONW".equals(systemCode) || this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return;
		}
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return;
		}

		// 医疗卡操作如果没有开帐可以执行收费不可以执行打票,但是不可以执行现金收费
		// =====zhangp 20120302 modify start
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", true);
			// 初始化票据
			// BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// =====origion code start
				// 初始化票号
				if (!initBilInvoice(opb.getBilInvoice())) {
					// return;
				}
				// =====origion code end
			}
		}
		// =====zhangp 20120302 modify end
		// 显示下一票号

		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
	}

	/**
	 * 初始化票据
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			// this.onClear();
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		callFunction("UI|UPDATE_NO|setValue", bilInvoice.getUpdateNo());
		return true;
	}

	/**
	 * 查询当前操作是否医疗卡扣款
	 * 
	 * @return TParm
	 */
	private TParm ektSave() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("BUSINESS_TYPE", "REG"); // 类型
		parm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
		tredeParm = EKTTool.getInstance().selectTradeNo(parm);
		return tredeParm;
	}

	/**
	 * 判断此次就诊是否执行医保操作
	 */
	public void afterInitOpbParameter() {
		String view = checkAll.getValue().toString();
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
		} else {
			// 调用显示未收费方法
			onNotCharge();
		}
		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
	}

	/**
	 * 就诊记录选择
	 */
	public void onRecord() {
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号!");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo()); 
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("挂号信息错误!");
			return;
		}
		// reg得到的数据放入界面
		afterRegSetValue();
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		serviceLevel = opb.getReg().getServiceLevel();
		this.setValue("SERVICE_LEVEL", serviceLevel);
		onlyCaseNo = opb.getReg().caseNo();
		//添加就诊号是否报道的校验   20130814 yanjing
		String sql = "SELECT ARRIVE_FLG FROM REG_PATADM WHERE CASE_NO = '"+onlyCaseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getValue("ARRIVE_FLG",0).equals("N")){
			this.messageBox("此病人尚未报道!");
			this.onClear();
			return;
		}
		//添加就诊号是否报道的校验   20130814 yanjing  end
		if (opb == null) {
			// this.messageBox_(33333333);
			this.messageBox_("此病人尚未就诊!");
			return;
		}
		// 初始化opb后数据处理
		oldOpdOrderParm = getOrder();			
		afterInitOpb();
		//add by huangtt 20160620 Q医支付数据查询 start
		TLabel qTxt = (TLabel) this.getComponent("Q_TXT");
		int re = getQeOrder();
		if(re == 1){				
			qTxt.setText("Q医已缴费");
		}else if(re == 2){
			qTxt.setText("部分Q医已缴费");
		}else{
			qTxt.setText("");
		}
		//add by huangtt 20160620 Q医支付数据查询 end
	}

	/**
	 * 检核病人是否有已开医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrderCount() {
		// 检核opb
		if (opb == null) {
			return true;
		}
		if (opb.checkOrderCount()) {
			// this.messageBox_(44444444);
			this.messageBox("此病人尚未就诊!");
			// table.addRow(setParm());
			// return true;//=====pangben modify 20110801
		}
		return false;
	}

	/**
	 * 利用得到的odo给table赋值
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTableValue(TParm parm) {
		// 清空table数据
		table.removeRowAll();
		// 保存医生开的医嘱数量
		drOrderCount = parm.getCount();
		// 执行删除方法：判断第一次记录病患就诊的医嘱
		// ==================pangben modify 20110804 删除按钮显示
		if (!drOrderCountFalse && drOrderCount > 0) {
			drOrderCountTemp = drOrderCount; // 获得医生开立的医嘱个数
			drOrderCountFalse = true; // 可以显示删除按钮
		}
		// //没有开立任何收费项目
		// if (drOrderCount <= 0) {
		// //加一空行
		// if (addOrder){
		// table.setParmValue(setAddParm());
		// callFunction("UI|TABLE|addRow", setParm());
		// }
		// return;
		// }
		// String lockRow="";
		// for(int i=0;i<drOrderCount;i++){
		// lockRow+=""+i+",";
		// }
		// //把数据放入tabl
		table.setParmValue(parm);
		table.setLockColumns("1");// =========pangben 2012-4-16 添加锁死打票注记
		// table.setLockRows(lockRow);
		// table.setUI()
		// 放费用
		// 重新计算费用
		double fee = getFee();
		callFunction("UI|TOT_AMT|setValue", fee);
		setFeeReview();
		// 加一空行
		if (addOrder) {
			callFunction("UI|TABLE|addRow", setParm());
		}
	}

	public void setTableValue_R(TParm parm) {
		// 清空table数据
		table.removeRowAll();
		// 保存医生开的医嘱数量
		drOrderCount = parm.getCount();
		// //把数据放入tabl
		table.setParmValue(parm);
		table.setLockColumns("1,2,3,4,5,,6,7,8,9,10,11,12,13,14");
		// 放费用
		// 重新计算费用
		double fee = opb.getFee(!ektTCharge.isSelected());
		callFunction("UI|TOT_AMT|setValue", fee);
		setFeeReview();
	}

	/**
	 * 新增行添加默认数据
	 * 
	 * @return TParm
	 */
	public TParm setParm() {
		TParm parm = new TParm();
		Order order = new Order();
		parm = order.getParm();
		parm.setData("OBJECT", order);
		return parm;
	}

	/**
	 * 循环删除table的行
	 */
	public void removeTableAllRow() {
		int row = table.getRowCount();
		for (int i = 0; i < row; i++) {
			table.removeRow();
		}
	}

	/**
	 * 新增行添加默认数据
	 * 
	 * @return TParm
	 */
	public TParm setParmData() {
		TParm parm = new TParm();
		parm.addData("CHARGE", "N");
		parm.addData("MEDI_QTY", 0);
		parm.addData("TAKE_DAYS", 0);
		parm.addData("DOSAGE_QTY", 0);
		parm.addData("OWN_PRICE", 0);
		parm.addData("AR_AMT", 0);
		parm.addData("OBJECT", null);
		return parm;
	}

	/**
	 * 全选事件
	 */
	public void onSelectAll() {
		
		// 检核opb
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// 查看显示方式
		String allR = (String) callFunction("UI|ALL|getValue");
		String notChargeR = (String) callFunction("UI|NOTCHARGE|getValue");
		String ektR = (String) callFunction("UI|EKT_R|getValue");
		// 如果是全部
		if (allR.equals("Y")) {
			onAll();
			return;
		}
		if (notChargeR.equals("Y")) {
			onNotCharge();
			return;
		}
		if (ektR.equals("Y")) {
			onEKTCharge();
			return;
		}
		
		
		
		
		
		// checkBoxNotCharge.setSelected(true);
		// 显示未收费
		// onNotCharge();
	}

	/**
	 * 显示未收费医嘱
	 */
	public void onNotCharge() {
		// ====zhangp 20120227 modify start
		checkBox.setEnabled(true);
		//checkBox.setSelected(true); //modify caoyong 注掉这行代码
		// =======zhangp 20120227 modify end
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			// //加一空行
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParmNotCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"));
		// 调用过滤方法,给table赋值
		tableShow(tableShow);
		// setTableValue(tableShow);
		// 得到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo放数据
		comboPrescription.setVectorData(prescriptionCombopb);
		// 刷新combo
		comboPrescription.updateUI();
		EKTmessage = false; // 输出消息
	}

	/**
	 * 卡收费ridiobutton事件
	 */
	public void onEKTCharge() {
		// ====zhangp 20120227 modify start
		checkBox.setEnabled(false);
		checkBox.setSelected(false);
		// =======zhangp 20120227 modify end
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			// //加一空行
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		//
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeTAll(bo, comboPrescription.getValue());
		}
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParmEKTTCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"));
		// 调用过滤方法,给table赋值
		tableShow_R(tableShow);
		// setTableValue(tableShow);
		// 得到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo放数据
		comboPrescription.setVectorData(prescriptionCombopb);
		// 刷新combo
		comboPrescription.updateUI();
		EKTmessage = true; // 输出消息
	}

	/**
	 * 显示全部医嘱
	 */
	public void onAll() {
		// ====zhangp 20120227 modify start
		checkBox.setEnabled(false);
		checkBox.setSelected(true);
		// =======zhangp 20120227 modify end
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		boolean bo = false;
		// 如果选中全选checkbox
		if (checkBoxChargeAll.isSelected()) {
			bo = true;
		}
		// bo = false;
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParm(bo, comboPrescription.getValue(),
				true);
		// 调用过滤方法,给table赋值
		tableShow(tableShow);
		// setTableValue(tableShow);
		// 拿到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo赋值
		comboPrescription.setVectorData(prescriptionCombopb);
		comboPrescription.updateUI();
		EKTmessage = false; // 输出消息
	}

	/**
	 * 按处方签过滤
	 */
	public void onPrescription() {
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// 查看显示方式
		String view = callFunction("UI|ALL|getValue").toString();
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
			return;
		}
		// 显示未收费
		onNotCharge();
	}

	/**
	 * 集合医嘱过滤细项
	 * 
	 * @param parm
	 *            TParm
	 */
	public void tableShow(TParm parm) {
		// 医嘱代码
		String orderCode = "";
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		// 医嘱数量
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 删除按钮显示
		if (count < 0) {
			deleteFun = true;
		}
		// 需要删除的细项列表
		int[] removeRow = new int[count < 0 ? 0 : count]; // =====pangben modify
		// 20110801
		int removeRowCount = 0;
		// 循环医嘱
		for (int i = 0; i < count; i++) {
			Order order = (Order) parm.getData("OBJECT", i);
			// 如果不是集合医嘱主项
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			// 医嘱代码
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// 组
			groupNo = order.getOrderSetGroupNo();
			// 如果是主项循环所有医嘱清理细项
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) parm.getData("OBJECT", j);
				// 如果是这个主项的细项
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// 计算费用
					fee += orderNew.getArAmt();
					// 保存要删除的行
					removeRow[removeRowCount] = j;
					// 自加
					removeRowCount++;
				}
			}
			// 细项费用绑定主项
			parm.setData("AR_AMT", i, fee);
		}
		// 删除集合医嘱细项=====pangben modify 20110801 不用去医生站就诊直接可以开立医嘱计费
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// ==================pangben modify 20110804 删除按钮显示
		if (removeRowCount < 0) {
			deleteFun = true;
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// 调用table赋值方法
		setTableValue(parm);

	}

	public void tableShow_R(TParm parm) {
		TParm tableParm = parm;
		// 医嘱代码
		String orderCode = "";
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		// 医嘱数量
		int count = tableParm.getCount("ORDER_CODE");
		// 需要删除的细项列表
		int[] removeRow = new int[count];
		int removeRowCount = 0;
		// 循环医嘱
		for (int i = 0; i < count; i++) {
			Order order = (Order) tableParm.getData("OBJECT", i);
			// 如果不是集合医嘱主项
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			// 医嘱代码
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// 组
			groupNo = order.getOrderSetGroupNo();
			// 如果是主项循环所有医嘱清理细项
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) tableParm.getData("OBJECT", j);
				// 如果是这个主项的细项
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// 计算费用
					fee += orderNew.getArAmt();
					// 保存要删除的行
					removeRow[removeRowCount] = j;
					// 自加
					removeRowCount++;
				}
			}
			// 细项费用绑定主项
			tableParm.setData("OWN_PRICE", i, fee);
			tableParm.setData("AR_AMT", i, fee);
		}
		// ==============pangben 2012-05-23 start 医嘱显示修改
		int temp = -1;
		for (int i = 0; i < removeRowCount; i++) {
			for (int j = 0; j < removeRowCount - 1; j++) {
				temp = removeRow[i];
				if (temp < removeRow[j]) {
					removeRow[i] = removeRow[j];
					removeRow[j] = temp;
				}
			}
		}
		// ==============pangben 2012-05-23 stop
		// 删除集合医嘱细项
		for (int i = removeRowCount - 1; i >= 0; i--) {
			tableParm.removeRow(removeRow[i]);
		}
		tableParm.setCount(tableParm.getCount() - removeRowCount);
		// 调用table赋值方法
		setTableValue_R(tableParm);

	}

	/**
	 * sysFee弹出界面
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 设置当前选中的行
		selectRow = row;
		// 如果当前选中的行不是最后一行空行则什么都不做
		if (table.getRowCount() != selectRow + 1) {
			return;
		}
		TTextField textfield = (TTextField) com;
		//根据挂号参数中有效天数校验是否可以就诊，添加医嘱操作===========pangben 2013-4-28
		if(!OPBTool.getInstance().canEdit(reg, regSysParm)){
			this.messageBox("超过当前就诊时间");
			this.onClear();
			return;
		}
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		// 得到当前列名
		String columnName = table.getParmMap(column);
		// 弹出sysfee对话框的列
		if (!columnName.equals("ORDER_DESC")) {
			return;
		}
		// if (column != 1)
		// return;
		//
		if (!(com instanceof TTextField)) {
			return;
		}
		textfield.onInit();	
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", this.getValueString("CAT1_TYPE"));
		if (!"".equals(Operator.getRegion())) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("SYSFEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}

	/**
	 * 新开医嘱
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		if (systemCode != null && "ONW".equals(systemCode)) {
			TParm patParm = PatTool.getInstance().getLockPat(pat.getMrNo());
			// 判断是否加锁
			if (patParm != null && patParm.getCount() > 0) {
				if (!isMyPat(patParm)) {// 用户被锁定不能添加医嘱===pangben 2013-5-15
					this.messageBox("此病患已被" + patParm.getValue("OPT_USER", 0)
							+ "锁定");
					this.closeWindow();
					return;
				}
			}
		}
		String freq_code="";
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		//$$$$$$--------add caoyong 20131218 增加频次 start ----------------///
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				||  "RIS".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				||  "OTH".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				) {
			///order.setItem(row, "FREQ_CODE", "STAT");
			freq_code="STAT";
			//orderOne.modifyFreqCode("STAT");//默认立即使用
			//parm.setData("FREQ_CODE","STAT");//默认立即使用
		   }else{
			   TParm action = new TParm();
				action.setData("ORDER_CODE",parm.getValue("ORDER_CODE") );
				TParm result = OdiMainTool.getInstance().queryPhaBase(
						action);
			   freq_code=result.getValue(
						"FREQ_CODE", 0);
			   
		   }
		//$$$$$$--------add caoyong 20131218 增加频次  end  ----------------///
		newReturnOrder(parm,selectRow,0.00,parm.getValue("UNIT_CODE"),freq_code,true);
	}
	/**
	 * 传回医嘱公用
	 * ========pangben 2013-8-30
	 * @param parm
	 * flg :true 正常传回  false:手术套餐传回
	 * 
	 */
	private void newReturnOrder(TParm parm,int selectRow, double dosage_qty,String dosage_unit, String freq_code, boolean flg){//add String freq_code 频次caoyong 20131218
		// 新增处方签(补充计价),添加一个order
		String s[] = opb.getPrescriptionList().getGroupNames();
		String maxName = "0";
		if (s.length > 0) {
			maxName = s[s.length - 1];
		}
		if (pFlg.equals("N")) {
			orderList = opb.getPrescriptionList().newOrderList(maxName, reg.getAdmType() , //modify by huangtt 20170725 "O"改为reg.getAdmType()
					opb.getPat());
			pFlg = "Y";
		}
		// 拿到当前(补充计价)处方签中的最大医嘱号
		if (orderList == null) {
			orderList = opb.getPrescriptionList().getOrderList(maxName,
					opb.getPrescriptionList().getGroup(maxName).size());
		}
		
		// 新建医嘱
		Order orderOne = orderList.newOrder();

		// 是否收费
		boolean bo = false;
		// 收费标记
		String chargeFlg = "N";
		// 如果选中全选checkbox
		if (checkBoxChargeAll.isSelected()) {
			chargeFlg = "Y";
			bo = true;
		}
		// 划价注记
		orderOne.modifyChargeFlg(bo);
		// 如果是集合医嘱
		if (parm.getValue("ORDERSET_FLG").equals("Y")) {
			Order o=OdoUtil.initExaOrder(reg, parm, orderOne, false, serviceLevel);
			// 要新增order的位置
			int count = orderList.size() - 1;
			// 新增集合医嘱
			OdoUtil.addOrder(reg, orderList, count, false, serviceLevel);
			// 成本中心
			orderOne.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
					orderOne.getExecDeptCode(), ""));
			// 开立完成集合医嘱后的医嘱数量
			int orderListCount = orderList.size();
			// 拿到新开立的医嘱
			orderOne = orderList.getOrder(count);
			// 计算集合医嘱总费用
			double allFee = 0.0;
			// 整理新开立的医嘱
			for (int i = count; i < orderListCount; i++) {
				// 得到新开医嘱
				Order newOrder = orderList.getOrder(i);
				newOrder.modifyCostCenterCode(DeptTool.getInstance()
						.getCostCenter(newOrder.getExecDeptCode(), ""));
				newOrder.modifyChargeFlg(bo);
				newOrder.modifyDiscountRate(o.getDiscountRate());//======pangben 2013-8-30 折扣添加
				newOrder.modifyTakeDays(o.getTakeDays());
//				if (!flg) {
//					newOrder.modifyDosageQty(dosage_qty);
//				}
				if (newOrder.getOrderCode().equals(newOrder.getOrdersetCode())) {
					continue;
				}
				// 计算费用
				allFee += newOrder.getArAmt();
			}
			// 拿到医嘱项目显示数据
			parm = orderOne.getParm();
			// 给主项添加显示费用
			parm.setData("AR_AMT", allFee);
		} else {
			// 组装三身份
			String[] ctz = new String[3];
			ctz[0] = opb.getReg().getCtz1Code();
			ctz[1] = opb.getReg().getCtz2Code();
			ctz[2] = opb.getReg().getCtz3Code();
			// 调用公用方法组装order
			orderOne = OdoUtil.fillOrder(orderOne, parm, ctz,dosage_qty, dosage_unit ,serviceLevel,flg);
			TotQtyTool t = new TotQtyTool();
			// 拿到医嘱项目显示数据
			parm = orderOne.getParm();
			//==========pangben 2013-1-28护士 、补充计费默认频次
			 //orderOne.modifyFreqCode("STAT");//频次
			// parm.setData("FREQ_CODE","STAT");//频次
			
			//$-------------- modify caoyong 20131228 start------------------// 
				orderOne.modifyFreqCode(freq_code);//频次
				parm.setData("FREQ_CODE",freq_code);//频次
		   //$-------------- modify caoyong 20131228 end------------------// 
			
			if (!flg) {
				orderOne.modifyDosageQty(dosage_qty);
				orderOne.modifyDispenseQty(dosage_qty);
				orderOne.modifyDosageUnit(dosage_unit);
				orderOne.modifyTakeDays(1);
				parm.setData("TAKE_DAYS",1);
				orderOne.modifyOwnAmt(StringTool.round(orderOne.getOwnPrice() *
						orderOne.getDosageQty(), 2));
				double ctzRate=BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
						orderOne.getOrderCode(), orderOne.getDosageQty(), serviceLevel);
				orderOne.modifyArAmt(ctzRate<= 0 ?0.00 : ctzRate); 
				
				orderOne.modifyExecDeptCode(Operator.getCostCenter()); //add by huangtt 20150527 修改执行科室为登入人的成本中心
				parm.setData("EXEC_DEPT_CODE", Operator.getCostCenter()); //add by huangtt 20150527 修改执行科室为登入人的成本中心

			}else{//====pangben 2013-10-29 添加开立医嘱初始值赋值操作
				TParm qty = t.getTotQty(parm);
		        if ("Y".equalsIgnoreCase(orderOne.getGiveboxFlg())) {
		        	orderOne.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
		                qty.getData("QTY_FOR_STOCK_UNIT"))));
		        	orderOne.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
		                getData("TOT_QTY"))));
		        	orderOne.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
		                "STOCK_UNIT")));
		        }else {
		        	orderOne.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
		                getData("QTY"))));
		            orderOne.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
		                "DOSAGE_UNIT")));
		            orderOne.modifyDispenseQty(orderOne.getDosageQty());
		        }
		       // orderOne.modifyMediQty(orderOne.getMediQty()*orderOne.getDosageQty());//===pangben 2013-12-10 传回的医嘱用量要匹配  // modify caoyong 20131218
		        orderOne.modifyOwnAmt(StringTool.round(orderOne.getOwnPrice() * orderOne.getDosageQty(), 2));
		        orderOne.modifyArAmt(BIL.chargeTotCTZ(orderOne.getCtz1Code(), orderOne.getCtz2Code(),
		        		orderOne.getCtz3Code(), orderOne.getOrderCode(), orderOne
		                                       .getDosageQty(), serviceLevel));
			}
			parm.setData("DISPENSE_QTY", orderOne.getDosageQty());// 配药量
			parm.setData("DOSAGE_QTY", orderOne.getDosageQty());// 发药量
			parm.setData("AR_AMT", orderOne.getArAmt());
			parm.setData("OWN_AMT",orderOne.getOwnAmt()); 
			//parm.setData("DISPENSE_QTY",orderOne.getDosageQty());//配药量
			//orderOne.modifyDispenseQty(orderOne.getDosageQty());//配药量
			//orderOne.modifyOwnAmt(orderOne.getOwnPrice()*orderOne.getDosageQty());//自费金额
			//orderOne.modifyArAmt(orderOne.getTakeDays()*orderOne.getOwnPrice());//总金额====pangben 2013-8-29 注释，折扣金额
			//parm.setData("DOSAGE_QTY",orderOne.getDosageQty());//发药量
			//parm.setData("AR_AMT",orderOne.getTakeDays()*orderOne.getOwnPrice());//总金额====pangben 2013-8-29 注释，折扣金额
			//parm.setData("OWN_AMT",orderOne.getOwnPrice()*orderOne.getDosageQty());//自费金额

		}
		// 成本中心
		orderOne.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
				orderOne.getExecDeptCode(), ""));
		orderOne.modifyRexpCode(BIL.getRexpCode(orderOne.getHexpCode(), "O"));
		orderOne.modifyCaseNo(reg.caseNo());
//		if (!flg) {
//			orderOne.modifyDosageUnit(dosage_unit);
//			orderOne.modifyDispenseUnit(parm.getValue("STOCK_UNIT", 0));
//		}
		//parm.setData("FREQ_CODE","QD");
		// 划价标记
		parm.setData("CHARGE", chargeFlg);
		parm.setData("OBJECT", orderOne);
		// 把新开的医嘱放入table
		table.setRowParmValue(selectRow, parm);
		table.getParmValue().setRowData(selectRow, parm);
		double fee = getFee();
		// 新增一行
		table.addRow(setParm());
		callFunction("UI|TOT_AMT|setValue", fee);
		setFeeReview();
		table.getTable().grabFocus();
		table.setSelectedColumn(2);
		table.setSelectedRow(selectRow);
	}
	/**
	 * 增加对Table值改变的监听
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableChangeValue(Object obj) {
		// 当前编辑的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return false;
		}
		callFunction("UI|delete|setEnabled", false);
		// 当前编辑的列明
		String colunHeader = table.getParmMap(node.getColumn());
		// 更改划价注记
		if (colunHeader.equals("CHARGE")) {
			return chargeChange(node);
		}
		// 如果没有开医嘱的权限不能更改医嘱属性
		if (!addOrder) {
			return true;
		}
		if (node.getRow() != table.getRowCount() - 1) {
			// 拿到隐含列中的order
			Order order = (Order) table.getParmValue().getData("OBJECT",
					node.getRow());
			boolean dcOrder = order.getDcOrder();
			// ===zhangp 20120316 start
			// ===zhangp 20120414 start
			// if (dcOrder&&!order.getRxType().equals("0")) {
			if (dcOrder && !order.getRxType().equals("7")) {
				// ===zhangp 20120414 end
				messageBox_("医生开立的医嘱,不能更改");
				return true;
			}
			// ===zhangp 20120316 end
			// //如果是集合医嘱的主项
			// if ("Y".equals(order.getSetmainFlg())) {
			// this.messageBox("此医嘱是集合医嘱!");
			// return true;
			// }
		}
		// 如果此医嘱是医生开立的,收费员无权删除和编辑
		// ===zhangp 20120316 start
		// // 拿到隐含列中的order
		// Order order = (Order) table.getParmValue().getData("OBJECT",
		// node.getRow());
		if (node.getRow() >= drOrderCount) {
			// ===zhangp 20120316 end
			callFunction("UI|delete|setEnabled", true);
		}
		// 医嘱名称
		if (colunHeader.equals("ORDER_DESC")) {
			return orderDescChange(node);
		}
		// 用量
		if (colunHeader.equals("MEDI_QTY")) {
			return mediQtyChange(node);
			
		}
		// 频次
		if (colunHeader.equals("FREQ_CODE")) {
			return freqCodeChange(node);
		}
		// 用法
		if (colunHeader.equals("ROUTE_CODE")) {
			return routeCodeChange(node);
		}
		// 天数
		if (colunHeader.equals("TAKE_DAYS")) {
			return takeDateChange(node);
		}
		// 总量
		if (colunHeader.equals("DOSAGE_QTY")) {
			return dosageQtyChange(node);
		}
		// 就诊科室
		if (colunHeader.equals("DEPT_CODE")) {
			return deptCodeChange(node);
		}
		// 执行科室
		if (colunHeader.equals("EXEC_DEPT_CODE")) {
			return execDeptChange(node);
		}
		// 执行医生
		if (colunHeader.equals("EXEC_DR_CODE")) {
			return execDrCodeChange(node);
		}
		return false;
	}

	/**
	 * 修改批价列
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean chargeChange(TTableNode node) {
		table.acceptText();
		// 判断收费权限
		if (!bilRight) {
			return true;
		}
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1
				&& (!ektTCharge.isSelected())) {
			return true;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 当前node的值
		boolean b = TCM_Transform.getBoolean(node.getValue());
		// 给order设置收费标记
		order.modifyChargeFlg(b);
		// //如果order已经收费,不操作
		// if (order.getChargeFlg() != b)
		// return true;
		// 如果是集合医嘱的主项
		if ("Y".equals(order.getSetmainFlg())) {
			// 拿到医嘱代码
			String ordeCode = order.getOrderCode();
			// 拿到集合医嘱组序号
			int orderGroupNo = order.getOrderSetGroupNo();
			// 处方签号
			String rxNo = order.getRxNo();
			// 设置收费
			opb.congregation(ordeCode, orderGroupNo, rxNo, b);
		}
		// 重新计算费用
		// double fee = opb.getFee(!ektTCharge.isSelected());
		return false;
	}

	/**
	 * 改变医嘱名称
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean orderDescChange(TTableNode node) {
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 如果不是最后一行的空行
		if (node.getRow() != table.getRowCount() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * 如果改变用量
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean mediQtyChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		//如果节点的数据是负值，提示错误,yanjing,20130702
		if (Double.valueOf(node.getValue().toString())<0) {
			this.messageBox("用量不能为负值！");
			return true;
		}
		//=====20130702 yanjing end
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 调用用量改变事件
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order的对应列放入用量
		order.modifyMediQty(TCM_Transform.getDouble(node.getValue()));
		// 通过用量计算对应数据
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyMediQty(TCM_Transform.getDouble(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(4);
		table.setSelectedRow(selectRow);

		// //重新计算费用
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 频次更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean freqCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据

		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 如果不是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 设置order的频次
		order.modifyFreqCode(TCM_Transform.getString(node.getValue()));
		// 调用频次改变显示事件
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyFreqCode(TCM_Transform.getString(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(5);
		table.setSelectedRow(selectRow);

		// //设置table的显示
		// setTableValueAt(order,node);
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 用法更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean routeCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更改order的用法
		order.modifyRouteCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyRouteCode(TCM_Transform.getString(node.getValue()));
				}
			}

		}
		// 设置table的显示
		setTableValueAt(order, node);
		return false;
	}

	/**
	 * 天数更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean takeDateChange(TTableNode node) {
		//this.messageBox("==============="+"天数"+node.getValue() );
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order设置天数
		order.modifyTakeDays(TCM_Transform.getInt(node.getValue()));
		// 调用天数改变计算
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyTakeDays(TCM_Transform.getInt(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					
					///this.messageBox("======================"+sysFeeParm.getDouble("TOTQTY", 0));
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(7);
		table.setSelectedRow(selectRow);

		// //设置table的显示
		// setTableValueAt(order,node);
		// //重新计算费用
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 总量修改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean dosageQtyChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order设置总量
		order.modifyDosageQty(TCM_Transform.getDouble(node.getValue()));
		// 调用总量改变计算
		order = OdoUtil.calcuQtyAll(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(2);
		table.setSelectedRow(selectRow);
		return false;
	}

	/**
	 * 校验总量不能为0
	 * 
	 * @return
	 */
	private boolean dosageQtyCheck() {
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount("CHARGE"); i++) {
			if (null == parm.getValue("ORDER_DESC", i)
					|| parm.getValue("ORDER_DESC", i).equals("<TNULL>")
					|| parm.getValue("ORDER_DESC", i).length() <= 0) {
				continue;
			}
			if (parm.getDouble("DOSAGE_QTY", i) == 0) {
				this.messageBox(parm.getValue("ORDER_DESC", i) + "总量不可以为0");
				return false;
			}
		}
		return true;
	}

	/**
	 * EXEC_DEPT_CODE 执行科室更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDeptChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的执行科室
		order.modifyExecDeptCode(TCM_Transform.getString(node.getValue()));
		order.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
				order.getExecDeptCode(), ""));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyExecDeptCode(TCM_Transform.getString(node
							.getValue()));
					o.modifyCostCenterCode(DeptTool.getInstance()
							.getCostCenter(o.getExecDeptCode(), ""));
				}
			}

		}
		return false;
	}

	/**
	 * 医嘱属性改变后table的值显改变
	 * 
	 * @param order
	 *            Order
	 * @param node
	 *            TTableNode
	 */
	public void setTableValueAt(Order order, TTableNode node) {
		int row = node.getRow();
		// TParm parm=order.getParm();
		// table.setRowParmValue(row,parm);
		// 用量
		table.setValueAt(order.getMediQty(), row, 2);
		// 频次
		table.setValueAt(order.getFreqCode(), row, 4);
		// 天数
		table.setValueAt(order.getTakeDays(), row, 6);
		// 总量DOSAGE_QTY
		table.setValueAt(order.getDosageQty(), row, 7);
		// 小计费
		table.setValueAt(order.getArAmt(), row, 10);
	}

	/**
	 * EXEC_DR_CODE 更改医师
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDrCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的医师
		order.modifyExecDrCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o
							.modifyExecDrCode(TCM_Transform.getString(node
									.getValue()));
				}
			}

		}
		return false;
	}

	/**
	 * DEPT_CODE 就诊科别改变
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean deptCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的医师
		order.modifyDeptCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyDeptCode(TCM_Transform.getString(node.getValue()));
				}
			}

		}
		return false;
	}

	/**
	 * 打开医疗卡
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm modifyOrderParm) {
		TParm orderParm = opb.getEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		setEktExeParm(orderParm, modifyOrderParm, null);
		if (orderParm.getParm("newParm").getCount("RX_NO") <= 0) {
			this.messageBox("没有要执行的数据");
			return null;
		}
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("CASE_NO", reg.caseNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("ektParm", parmEKT.getData());
		TParm parm = new TParm();
		// 需要添加医保收费明细添加EKT_ACCNTDETAIL表数据添加
		parm = EKTIO.getInstance()
				.onOPDAccntClient(orderParm, onlyCaseNo, this);
		parm.setData("orderParm", orderParm.getData());
		return parm;
	}

	/**
	 * 收费操作收集数据 String exeTrade：UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
	 * 
	 * @param parm
	 */
	public void setEktExeParm(TParm parm, TParm modifyOrderParm,
			String exeTrade) {
		// 新增、未收费修改、未收费删除的数据
		// TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		TParm newParm = new TParm();// 此次操作的医嘱集合
		TParm hl7Parm = new TParm();// hl7集合
		String bill_flg = "";
		double sum = 0.00;// 执行金额
		StringBuffer phaRxNo=new StringBuffer();//pangben 2013-5-17获得所有操作的处方签号码 发送数据
		if (!ektTCharge.isSelected()) {
			bill_flg = "Y";
		} else {
			bill_flg = "N";
		}
		parm.setData("BUSINESS_TYPE", "OPB");
		for (int i = 0; i < modifyOrderParm.getParm(OrderList.NEW).getCount(//新增数据,数据库还没有保存的
				"RX_NO"); i++) {
			for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
				// 提示勾选收费状态
				if (parm.getValue("RX_NO", j).equals(
						modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i))&& 
						parm.getValue("SEQ_NO", j).equals(modifyOrderParm.getParm(OrderList.NEW).getValue("SEQ_NO", i))) {
					OPBTool.getInstance().setNewParm(newParm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j), "E");// 新增的医嘱
					// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
					OPBTool.getInstance().setHl7TParm(hl7Parm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j));
					if (parm.getValue("CHARGE_FLG", j).equals("Y")) {
						sum += modifyOrderParm.getParm(OrderList.NEW)
								.getDouble("AR_AMT", i);
					}
					if (null != modifyOrderParm.getParm(OrderList.NEW).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
							modifyOrderParm.getParm(OrderList.NEW).getValue("CAT1_TYPE", i).equals("PHA")&&
							!modifyOrderParm.getParm(OrderList.NEW).getValue("RX_TYPE", i).equals("7")&&
							!modifyOrderParm.getParm(OrderList.NEW).getValue("RX_TYPE", i).equals("0")) {
						if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i))) {
							phaRxNo.append(modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i)).append(",");
						}
					}
				}
			}
		}
		for (int i = 0; i < modifyOrderParm.getParm(OrderList.MODIFIED)
				.getCount("RX_NO"); i++) {
			if (!ektTCharge.isSelected()){// 收费操作直接添加数据 退费操作 需要查找到所操纵医嘱的所有内部交易号
				// 将存在此内部交易号 的所有医嘱重新赋值
				OPBTool.getInstance().setNewParm(newParm,
						modifyOrderParm.getParm(OrderList.MODIFIED), i, "Y",
						"E");// 数据库中未收费的医嘱
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("0")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
			}
			// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
			OPBTool.getInstance().setHl7TParm(hl7Parm,
					modifyOrderParm.getParm(OrderList.MODIFIED), i, bill_flg);
		}
		StringBuffer tradeNo = new StringBuffer();
		if (ektTCharge.isSelected()) {// 退费操作
			StringBuffer tempTradeNo = new StringBuffer();
			// 查找此次操作的所有内部交易号
			for (int i = 0; i < modifyOrderParm.getParm(OrderList.MODIFIED)
					.getCount("RX_NO"); i++) {
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
				if (!tempTradeNo.toString().contains(
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue(
								"BUSINESS_NO", i))) {
					// 汇总这次操作的医嘱使用
					tempTradeNo.append(
							modifyOrderParm.getParm(OrderList.MODIFIED)
									.getValue("BUSINESS_NO", i)).append(",");
					// UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
					tradeNo.append("'").append(
							modifyOrderParm.getParm(OrderList.MODIFIED)
									.getValue("BUSINESS_NO", i)).append("',");
				}
			}
			String[] tempTradeNames = new String[0];
			if (tempTradeNo.length() > 0) {
				tempTradeNames = tempTradeNo.toString().substring(0,
						tempTradeNo.lastIndexOf(",")).split(",");
			}
			boolean newFlg = false;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				for (int j = 0; j < tempTradeNames.length; j++) {
					// 选择的医嘱
					// EKT_TRADE 表内部交易号
					if (parm.getValue("BUSINESS_NO", i).equals(
							tempTradeNames[j])) {
						newFlg = false;
						for (int z = 0; z < modifyOrderParm.getParm(
								OrderList.MODIFIED).getCount("RX_NO"); z++) {
							// 将此次退费的医嘱移除
							if (parm.getValue("RX_NO", i).equals(
									modifyOrderParm.getParm(OrderList.MODIFIED)
											.getValue("RX_NO", z))
									&& parm.getValue("SEQ_NO", i).equals(
											modifyOrderParm.getParm(
													OrderList.MODIFIED)
													.getValue("SEQ_NO", z))) {
								newFlg = true;
								break;
							}
						}
						if (!newFlg) {
							OPBTool.getInstance().setNewParm(newParm, parm, i,
									"Y", "E");
							sum += parm.getDouble("AMT", i);// 执行金额
						} else {
							OPBTool.getInstance().setNewParm(newParm, parm, i,
									"N", "C");
						}
					}
				}
			}
		}
		String exeTradeNo = "";
		// 获得内部交易号码 ：此次操作的医嘱扣款所有需要退还的医嘱
		if (tradeNo.length() > 0) {
			exeTradeNo = tradeNo.toString().substring(0,
					tradeNo.toString().lastIndexOf(","));
		}
		parm.setData("newParm", newParm.getData());// 操作医嘱，增删改
		// parm.setData("unBillParm",
		// modifyOrderParm.getParm(OrderList.MODIFIED).getData());//转回未收费状态数据
		parm.setData("hl7Parm", hl7Parm.getData());// HL7发送接口集合
		parm.setData("EXE_AMT", !ektTCharge.isSelected() ? this
				.getValueDouble("TOT_AMT") : sum);// EKT_TRADE 中此次 操作的金额
		// 获得此次就诊医嘱总金额包括已经收费、新开立(包括这次操作的医嘱)
		parm.setData("SHOW_AMT", this.getValueDouble("TOT_AMT"));// 显示的金额
		parm.setData("ORDER", modifyOrderParm.getData());// 更改OPD_ORDER 使用
		parm.setData("TRADE_SUM_NO", null == exeTrade ? exeTradeNo : exeTrade);// UPDATE
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15添加药房审药显示跑马灯数据
		// EKT_TRADE
		// 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
	}

	/**
	 * 门诊收费医疗卡金额不足情况,执行收费医保分割打印操作
	 */
	public void exeInsPrint() {
		TParm exeParm = new TParm();
		TParm orderParm = opb.getInsEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		if (orderParm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有要执行的数据");
			return;
		}
		if (null == parmEKT || parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox("请读取医疗卡信息");
			return;
		}
		if (null == insParm) {
			this.messageBox("请读取医保卡信息");
			return;
		}
		if (null == this.getValue("UPDATE_NO")
				|| this.getValue("UPDATE_NO").toString().length() <= 0) {
			this.messageBox("没有可执行的票据号码");
			return;
		}
		TParm result = null;
		TParm parm = null;
		// 获得医保
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			parm = new TParm();
			parm.setData("BILL_D", SystemTool.getInstance().getDate());
			if (null != reg && null != reg.getAdmType()
					&& reg.getAdmType().equals("O")) {
				parm.setData("INS_CODE", orderParm.getValue("NHI_CODE_O", i));
			} else {
				parm.setData("INS_CODE", orderParm.getValue("NHI_CODE_E", i));
			}
			result = INSIbsTool.getInstance().queryInsIbsOrderByInsRule(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("获得医保数据失败");
				return;
			}
			orderParm.setData("YF", i, result.getValue("YF", 0));// 用法
			orderParm.setData("ZFBL1", i, result.getValue("ZFBL1", 0));// 自负比例
			orderParm.setData("PZWH", i, result.getValue("PZWH", 0));// 批准文号
		}
		insExeParm(orderParm);
		exeParm.setData("NAME", pat.getName());
		exeParm.setData("MR_NO", pat.getMrNo()); // 病患号
		exeParm.setData("PAY_TYPE", isEKT); // 支付方式
		exeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		// exeParm.setData("orderParm", orderParm.getData());// 需要收费的医嘱
		// exeParm.setData("parmSum", orderParm.getParm("parmSum").getData());//
		// 所有的医嘱
		// 包括集合医嘱用来修改
		// OPD_ORDER
		// MED_APPLY数据
		exeParm.setData("billAmt", orderParm.getDouble("billAmt"));// 所有医嘱金额
		// exeParm.setData("parmBill",
		// orderParm.getParm("parmBill").getData());// 未收费医嘱集合
		// 包括收费未收费
		exeParm.setData("ektParm", parmEKT.getData());// 医疗卡数据
		exeParm.setData("insParm", insParm.getData());// 医保数据
		exeParm.setData("FeeY", orderParm.getDouble("sumAmt"));// 应收金额
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("OPT_USER", Operator.getID());
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("ADM_TYPE", reg.getAdmType());
		exeParm.setData("ID_NO", pat.getIdNo());
		exeParm.setData("PRINT_NO", this.getValue("UPDATE_NO"));// 票号
		exeParm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());// 开始票号
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		setEktExeParm(orderParm, modifyOrderParm, orderParm
				.getValue("TRADE_SUM_NO"));
		exeParm.setData("orderParm", orderParm.getData());// 操作的数据
		TParm r = (TParm) openDialog("%ROOT%\\config\\ins\\INSFeePrint.x",
				exeParm);
		//======pangben 2013-3-13 添加校验为空
		if(null==r){
			return;
		}
		opdOrderSpc(orderParm);//===pangben 2013-5-22 添加物联网预审功能
		//正常患者或者垫付延迟无挂号条患者判断医疗卡扣款情况
		String sql1 = " SELECT * FROM OPD_ORDER"+
		              " WHERE CASE_NO ='"+ exeParm.getValue("CASE_NO") + "'"+
		              " AND REXP_CODE = '002'";
//		 System.out.println("sql1=========="+sql1);
		TParm ADVANCE = new TParm(TJDODBTool.getInstance().select(sql1));
//		 System.out.println("ADVANCE=========="+ADVANCE);
//		 System.out.println("count=========="+ADVANCE.getCount());
		if(this.getValue("OPB_ADVANCE_TYPE").equals("1")||
		  (this.getValue("OPB_ADVANCE_TYPE").equals("2")&&ADVANCE.getCount()==0)){			
		String re = EKTIO.getInstance().check(r.getValue("TRADE_NO"),
				reg.caseNo());
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("请马上与信息中心联系");
		}
	}
		//垫付延迟患者更新发放状态
		if(this.getValue("OPB_ADVANCE_TYPE").equals("2")){
			String sql = " UPDATE INS_ADVANCE_OUT SET PAY_FLG ='1',PAY_DATE = SYSDATE" +
					     " WHERE CASE_NO ='"+ exeParm.getValue("CASE_NO") + "'";
			 TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
             // 判断错误值
             if (result1.getErrCode() < 0) {
                 messageBox(result1.getErrText());
                 return;
             }
		}		
		this.onClear();
	}
	/**
	 * =====pangben 2013-5-22 添加物联网预审功能
	 * OPD_ORDER表添加数据 
	 * @param orderParm
	 */
	private void opdOrderSpc(TParm orderParm){
		if (Operator.getSpcFlg().equals("Y")&&orderParm.getValue("PHA_RX_NO").length()>0) {
			// ==========pangben 2013-5-22 添加预审功能
			TParm spcParm = new TParm();
			spcParm.setData("RX_NO", orderParm.getValue("PHA_RX_NO"));
			spcParm.setData("CASE_NO", reg.caseNo());
			spcParm.setData("CAT1_TYPE", "PHA");
			spcParm.setData("RX_TYPE", "7");
			// 物联网获得此次操作的医嘱，通过处方签获得
			TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
					spcParm);
			if (spcResult.getErrCode() < 0) {
				this.messageBox("物联网操作：医嘱查询出现错误");
			} else {
				spcResult.setData("SUM_RX_NO", orderParm.getValue("PHA_RX_NO"));
				spcResult = TIOM_AppServer.executeAction(
						"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
						spcResult);
				if (spcResult.getErrCode() < 0) {
					System.out.println("物联网操作:" + spcResult.getErrText());
					this.messageBox("物联网操作：医嘱添加出现错误," + spcResult.getErrText());
				} else {
					phaRxNo = orderParm.getValue("PHA_RX_NO");// =pangben2013-5-15添加药房审药显示跑马灯数据
					sendMedMessages();
				}
			}
		}
	}
	/**
	 * 执行医保卡操作 添加数据 ，获得扣除以后的医嘱金额集合============pangb 2011-11-29
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm onINSAccntClient(TParm parm) {

		TParm result = INSTJReg.getInstance()
				.insCommFunction(insParm.getData());
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE")); // 现金支付： 享受医保救助 、
			// 上传费用明细失败
			// 、结算明细生成失败、账户支付失败
			if (null != result.getValue("FLG")
					&& result.getValue("FLG").length() > 0) {
				result.setErr(-1, "医保卡执行出错");
				return result;
			}
			return result;
		}
		return result;

	}

	/**
	 * 保存医嘱
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		
		//add by huangtt 20141126    保存前查一下医嘱，进行比对，查看是否有不一样的
		if(!orderComparison(getOrder())){
			return false;
		}

//		//=====20130510 yanjing 用量是否为负值的校验
//		TTable table1  = (TTable) getComponent("TABLE");
//		table1.acceptText();
//		TParm tableParm = table1.getParmValue();
//		for (int i = 0; i < tableParm.getCount(); i++) {
//			TParm parm = new TParm();
//			 parm.setData("MEDI_QTY",tableParm.getValue("MEDI_QTY",i ));		
//			 double medi_qty = StringTool.round(table1.getItemDouble(i, "MEDI_QTY"), 3);
//			if (medi_qty<0) {
//				this.messageBox("用量不正确,请重新输入！");
//				return true;
//			}
//		}
//		//===========yanjing end

		// table接收
		table.acceptText();
//		if (!PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			this.messageBox("病患已经被其他用户占用!");
//			return false;
//		}
//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
//		if (("ODO".equals(parm.getValue("PRG_ID", 0))||"ODE".equals(parm.getValue("PRG_ID", 0)))//锁病患操作校验===pangben 2013-7-18
//				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
//				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
//			this.messageBox(PatTool.getInstance().getLockParmString(
//					pat.getMrNo()));
//			return false;
//		}

		if (getValue("BILL_TYPE").equals("")) {
			this.messageBox("支付方式不能为空");
			return false;
		}
		// 查询当前就诊病患是否执行记账
		// String sql =
		// "SELECT A.PRINT_NO,B.CONTRACT_CODE FROM BIL_REG_RECP A,REG_PATADM B WHERE A.CASE_NO=B.CASE_NO(+)  AND  A.CASE_NO='"
		// + onlyCaseNo +
		// "'";
		// CASE_NO!=null || CASE_NO!="" 记账: 不记账==========pangben 20110818
		String CONTRACT_CODE = "";
		if (resultBill != null && resultBill.getCount("CASE_NO") > 0) {
			if (null != resultBill && null != resultBill.getValue("CASE_NO", 0)
					&& !"".equals(resultBill.getValue("CASE_NO", 0))) {
				isbill = true; // 记账
				CONTRACT_CODE = resultBill.getValue("CONTRACT_CODE", 0); // =====pangben
			}
		}
		if (!dosageQtyCheck()) {
			return false;
		}
		// 20110818
		// 记账单位
		// 判断支付方式是否为医疗卡
		if (getValue("BILL_TYPE").equals("E")) {
			// 保存医疗卡
			return onEktSave();
		}
		if (getValue("BILL_TYPE").equals("C")
				|| getValue("BILL_TYPE").equals("P")) {
			if (systemCode != null && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// // 初始化票号
				// if (!initBilInvoice(opb.getBilInvoice())) {
				// return false;
				// }
			}
			// 保存现金
			return onCashSave(CONTRACT_CODE);
		}
		// 医保卡操作
		if (getValue("BILL_TYPE").equals("I")) {
			if (isEKT) {
				return onEktSave();
			} else {
				return onCashSave(CONTRACT_CODE);
			}
		}
		// 检核开关帐
		if (checkOpenBill()) {
			return false;
		}
		// 组装票据
		setOpbReceipt();
		// 拿到收据金额
		double totAmt = getValueDouble("TOT_AMT");
		if (totAmt == 0) {
			this.messageBox("无收款金额");
			return false;
		}
		double pay = getValueDouble("PAY");
		if (pay - totAmt < 0 || pay == 0) {
			this.messageBox("金额不足!");
			this.grabFocus("PAY");
			return false;
		}
		// 如果收费金额大于0传入收费
		String charge = "N";
		if (bilRight) {
			charge = "Y";
		}

		// //得到收费项目
		// sendHL7Parm = table.getParmValue();
		// 调用opb的保存方法
		TParm result = opb.onSave(charge);
		if (result.getErrCode() < 0) {
			this.messageBox("缴费失败!");
			return false;
		}
		this.messageBox("收费成功");
		// //调用HL7
		// sendHL7Mes();
		// 得到后台保存返回的票据号
		String[] receiptNo = (String[]) result.getData("RECEIPTNO");
		// 调用处理打印的方法
		dealPrintData(receiptNo);
		// 收费成功重新刷新当前病患
		onClear();
		return true;
	}

	/**
	 * 校验保存操作数据是否执行
	 * 
	 * @return
	 */
	private boolean checkOnEktSave(boolean flg, TParm hl7ParmEnd,
			TParm modifyOrderParm) {
		TParm orderParm = modifyOrderParm.getParm(OrderList.MODIFIED);
		TParm checkParm = table.getParmValue();
		TParm checkParmEnd = new TParm();
		int checkCount = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < checkCount; i++) {
			if (checkParm.getBoolean("CHARGE", i)) {
				checkParmEnd.addData("ORDER_CODE", checkParm.getData(
						"ORDER_CODE", i));
				checkParmEnd.addData("EXEC_DEPT_CODE", checkParm.getData(
						"EXEC_DEPT_CODE", i));
				if (checkParm.getData("EXEC_DEPT_CODE", i) == null
						|| checkParm.getData("EXEC_DEPT_CODE", i).toString()
								.length() == 0) {
					this.messageBox("录入的医嘱执行科室不能为空!");
					return false;
				}
				if (checkParm.getBoolean("PRINT_FLG", i)) {
					this.messageBox("已打票,不能退费!");
					return false;
				}
				//add by huangtt 20170815
//				System.out.println("receipt_no---"+checkParm.getValue("RECEIPT_NO", i));
				if(checkParm.getValue("RECEIPT_NO", i).length() > 0 && !checkParm.getValue("RECEIPT_NO", i).equals("<TNULL>")){
					this.messageBox("Q医已收费,请去Q医指定柜台打票退票后再执行退费!");
					return false;
				}
				checkParmEnd.addData("DOSAGE_QTY", checkParm.getData(
						"DOSAGE_QTY", i));

			}
		}
		// 全院
		if (!this.getPopedem("ALL")) {
			if (!chekeRolo(checkParmEnd)) {
				return false;
			}
		}
		int count = orderParm.getCount("CASE_NO");
		for (int i = 0; i < count; i++) {
			//===zhangp 物联网修改 start
			if (!Operator.getSpcFlg().equals("Y")) {
				if ("PHA".equals(orderParm.getValue("CAT1_TYPE", i))
						&& !opb.checkDrugCanUpdate(orderParm.getRow(i), i)) {
					this.messageBox("药品已审核或发药,不能退费!");
					return false;
				}
			}else{
				if ("PHA".equals(orderParm.getValue("CAT1_TYPE", i))) {
					String caseNo = orderParm.getValue("CASE_NO", i);
					String rxNo = orderParm.getValue("RX_NO", i);
					String seqNo = orderParm.getValue("SEQ_NO", i);
					TParm spcParm = new TParm();
					spcParm.setData("CASE_NO", caseNo);
					spcParm.setData("RX_NO", rxNo);
					spcParm.setData("SEQ_NO", seqNo);
					TParm spcReturn = TIOM_AppServer.executeAction(
			                "action.opb.OPBSPCAction",
			                "getPhaStateReturn", spcParm);
//					PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
//					SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
//					if(spcOpdOrderReturnDto == null){
//						return true;
//					}
					if(spcReturn.getErrCode()==-2){
						return true;
					}
					boolean needExamineFlg = false;
					// 如果是西药 审核或配药后就不可以再进行修改或者删除
					if ("W".equals(orderParm.getValue("PHA_TYPE"))
							|| "C".equals(orderParm.getValue("PHA_TYPE"))) {
						// 判断是否审核
						needExamineFlg = PhaSysParmTool.getInstance()
								.needExamine();
					}
					// 如果有审核流程 那么判断审核医师是否为空
					if (needExamineFlg) {
						// System.out.println("有审核");
						// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
//						if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0
//								&& spcOpdOrderReturnDto.getPhaRetnCode()
//										.length() == 0) {
//							this.messageBox("药品已审核,不能退费!");
//							return false;
//						}
						if (spcReturn.getValue("PhaCheckCode").length() > 0
								&& spcReturn.getValue("PhaRetnCode")
										.length() == 0) {
							this.messageBox("药品已审核,不能退费!");
							return false;
						}
					} else {// 没有审核流程 直接配药
						// 判断是否有配药药师
						// System.out.println("无审核");
						if (spcReturn.getValue("PhaDosageCode").length() > 0
								&& spcReturn.getValue("PhaRetnCode")
										.length() == 0) {
							this.messageBox("药品已发药,不能退费!");
							return false;// 已经配药不可以做修改
						}
					}
				}
			}
			//===zhangp 物联网修改 end	
			if (ektTCharge.isSelected()) {
				if (!"PHA".equals(orderParm.getValue("CAT1_TYPE", i))
						&& "Y".equals(orderParm.getValue("EXEC_FLG", i))) {
					this.messageBox("已到检,不能退费!");
					return false;
				}
			}
			// ===zhangp 20120421 end
		}
		// 发送接口集合
		if (!flg) {
			getCashHl7Parm(checkParm, hl7ParmEnd);
		}
		return true;
	}

	/**
	 * 现金操作发送接口
	 * 
	 * @param checkParm
	 * @param hl7ParmEnd
	 */
	private void getCashHl7Parm(TParm checkParm, TParm hl7ParmEnd) {
		int hl7Count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			if (checkParm.getBoolean("CHARGE", i)) {
				if ((checkParm.getValue("CAT1_TYPE", i).equals("RIS") || checkParm
						.getValue("CAT1_TYPE", i).equals("LIS"))
						&& checkParm.getBoolean("SETMAIN_FLG", i)
						&& checkParm.getValue("ORDERSET_CODE", i).equals(
								checkParm.getValue("ORDER_CODE", i))) {
					hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
							"TEMPORARY_FLG", i));
					hl7ParmEnd.addData("ADM_TYPE", checkParm.getData(
							"ADM_TYPE", i));
					hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData(
							"CAT1_TYPE", i));
					hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
					hl7ParmEnd
							.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
					// hl7ParmEnd.addData("BILL_FLG",
					// checkParm.getData("BILL_FLG", i));
					hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
							"MED_APPLY_NO", i));
				}
			}
		}
	}

	/**
	 * 医疗卡保存
	 * 
	 * @return boolean
	 */
	public boolean onEktSave() {
		ektParmSave = new TParm();
		// ======zhangp 20120227 modify start
		this.callFunction("UI|save|setEnabled", false);
		this.callFunction("UI|CHARGE|setEnabled", false);
		if (!isEKT) {
			this.messageBox("请读取医疗卡信息");
			return false;
		}
		int type = 0;
		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return false;
		}
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		if (!checkOnEktSave(true, null, modifyOrderParm)) {
			return false;
		}
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (!EKTIO.getInstance().ektSwitch()) {
			messageBox_("医疗卡流程没有启动!");
			return false;
		}
		
		//modify by huangtt 20160918  医疗卡数据存储调整  start
		EktParam ektParam = new EktParam();
		ektParam.setOpbChargesMControl(this);
		ektParam.setType("OPB");
		ektParam.setPat(pat);
		ektParam.setReg(reg);
		ektParam.setOrderParm(modifyOrderParm);
		EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
		try {
			
			//创建参数，打开收费界面，执行收费
			ektTradeContext.openClient(opb);
			
			
		} catch (Exception e) {
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|CHARGE|setEnabled", true);
			oldOpdOrderParm = getOrder();
			e.printStackTrace();
			return false;
		}
		
		
		
//		parm = onOpenCard(modifyOrderParm);
//		if (parm == null) {
//			this.messageBox("E0115");
//			return false;
//		}
//		type = parm.getInt("OP_TYPE");
//		if (type == 3 || type == -1) {
//			this.messageBox("E0115");
//			return false;
//		}
//		if (type == 2) {
//			return false;
//		}
//		tredeNo = parm.getValue("TRADE_NO");
		parm = ektParmSave;
		parm.setData("EKT_SQL", opb.getEktSql());
		//modify by huangtt 20160918  医疗卡数据存储调整  end
		

		// 得到收费项目
		// sendHL7Parm = hl7ParmEnd;
		// 调用opb的保存方法
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		// ==================pangben 需要修改的数据
		
		TParm result = opb.onSaveEKT(parm, ektTCharge.isSelected());
		if (result.getErrCode() < 0) {
			// EKTIO.getInstance().unConsume(tredeNo, this);
//			TParm writeParm = new TParm();
//			writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
//			writeParm.setData("MR_NO", pat.getMrNo());
//			writeParm.setData("SEQ", parmEKT.getValue("SEQ"));
//			writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
//					pat.getMrNo()); // 回写医疗卡金额
//			if (writeParm.getErrCode() < 0)
//				System.out.println("err:" + writeParm.getErrText());
			if (EKTmessage) {
				this.messageBox("医疗卡退费失败!");
			} else {
				this.messageBox("缴费失败!");
			}
			return false;
		} else {
			if (EKTmessage) {
				this.messageBox("医疗卡退费成功");
			} else {
				this.messageBox("收费成功");
			}
			opdOrderSpc(parm.getParm("orderParm"));//===pangben 2013-5-22 添加物联网预审功能
			// 调用HL7
			// 调用HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(
					parm.getParm("orderParm").getParm("hl7Parm"),
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// 护士补充计费不提示信息
		String re = EKTIO.getInstance().check(tredeNo, reg.caseNo());
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("请马上与信息中心联系");
			return false;
		}
		// 收费成功重新刷新当前病患
		onClear();
		return true;
	}
	
	/**
	 * 现金收费保存
	 * 
	 * @param CONTRACT_CODE
	 *            String
	 * @return boolean
	 */
	public boolean onCashSave(String CONTRACT_CODE) {
		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return false;
		}
		TParm hl7ParmEnd = new TParm();
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		if (!checkOnEktSave(false, hl7ParmEnd, modifyOrderParm)) {
			return false;
		}
		parm = opb.getCashParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		
		if (parm == null) {
			this.messageBox("E0115");
			return false;
		}
		// 得到收费项目
		// sendHL7Parm = hl7ParmEnd;
		// 调用opb的保存方法
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			this.messageBox("护士无收款权限");
			return false;
		}
		// 医保操作
		TParm insResult = null;
		if ("I".equals(this.getValueString("BILL_TYPE"))) { // 医保卡操作
			insResult = insCashSave();
			if (null == insResult) {
				return false;
			}
		}
		System.out.println("==========parm1========"+parm);
		TParm result = opb.onSaveCash(parm, ektTCharge.isSelected());
		if (result.getErrCode() < 0) {
			this.messageBox("缴费失败!");
			return false;
		} else {
			this.messageBox("收费成功");
			// 调用HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(hl7ParmEnd,
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// 收费成功重新刷新当前病患
		onEKTPrint(CONTRACT_CODE, false, insResult); // ============pangben
		// 20110818 添加参数
		onClear();
		return true;
	}

	/**
	 * 医保现金收费操作
	 * 
	 * @return TParm
	 */
	private TParm insCashSave() {
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", reg.caseNo());
		selOpdParm.setData("REGION_CODE", Operator.getRegion());
		TParm opdParm = OrderTool.getInstance()
				.selDataForOPBCashIns(selOpdParm);
		TParm insReturnParm = insExeFee(opdParm, true); // 医保收费操作
		if (null == insReturnParm) {
			this.messageBox("医保操作失败");
			return null;
		}
		insParm.setData("ACCOUNT_AMT", insReturnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		insParm
				.setData("UACCOUNT_AMT", insReturnParm
						.getDouble("UACCOUNT_AMT")); // 现金金额
		insParm.setData("comminuteFeeParm", insReturnParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		insParm.setData("settlementDetailsParm", insReturnParm.getParm(
				"settlementDetailsParm").getData()); // 费用结算
		TParm ins_result = new TParm();
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			ins_result = onINSAccntClient(opdParm); // 现金操作，回参数据获得扣除医保金额以后的医嘱信息
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("医保收费失败");
				return null;
			}
			if (null != ins_result.getValue("MESSAGE")
					&& ins_result.getValue("MESSAGE").length() > 0) {
				// 现金支付
				return null;
			} else { // 现金使用 医疗卡已经打印收据不需要

				insFlg = true; // 判断医保在途状态执行
			}
		}
		return insReturnParm;
	}

	/**
	 * 处理打印数据
	 * 
	 * @param receiptNo
	 *            String[]
	 */
	public void dealPrintData(String[] receiptNo) {
		int size = receiptNo.length;
		for (int i = 0; i < size; i++) {
			// 取出一张票据号
			String recpNo = receiptNo[i];
			if (recpNo == null || recpNo.length() == 0) {
				return;
			}
			// 调用打印一张票据的方法
			onPrint(new OPBReceipt().getOneReceipt(recpNo));
		}
	}

	/**
	 * 打印票据
	 * 
	 * @param receiptOne
	 *            OPBReceipt
	 */
	public void onPrint(OPBReceipt receiptOne) {
		if (receiptOne == null) {
			return;
		}
		TParm oneReceiptParm = receiptOne.getParm();
		oneReceiptParm.setData("PAT_NAME", opb.getPat().getName());
		oneReceiptParm.setData("DEPT_CODE", opb.getReg().getDeptCode());
		oneReceiptParm.setData("SEX_CODE", opb.getPat().getSexCode());
		oneReceiptParm.setData("OPT_USER", Operator.getName());
		oneReceiptParm.setData("OPT_ID", Operator.getID());
		oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw",
				oneReceiptParm);
	}

	/**
	 * 组装票据数据
	 */
	public void setOpbReceipt() {
		// 拿到一个票据对象
		OPBReceipt receiptOne = opb.getReceiptList().newReceipt();
		// 现金支付
		receiptOne.setPayCash(getValueDouble("PAY_CASH"));
		// 记账支付
		receiptOne.setPayDepit(getValueDouble("PAY_DEBIT"));
		// 刷卡
		receiptOne.setPayBankCard(getValueDouble("PAY_BANK_CARD"));
		// 支票
		receiptOne.setPayCheck(getValueDouble("PAY_CHECK"));
		// 医保卡
		receiptOne.setPayInsCard(getValueDouble("PAY_INS_CARD"));
		// 医疗卡
		receiptOne.setPayMedicalCard(getValueDouble("PAY_MEDICAL_CARD"));
		// 其它支付(慢性病)
		receiptOne.setPayOther1(getValueDouble("PAY_OTHER1"));
		// 支票备注
		receiptOne.setPayRemark(getValueString("PAY_REMARK"));
		// 押金
		receiptOne.setPayBilPay(getValueDouble("PAY_BILPAY"));
		// 医保
		receiptOne.setPayIns(getValueDouble("PAY_INS"));
		// 总金额
		receiptOne.setTotAmt(getValueDouble("TOT_AMT"));
		// 自费金额
		receiptOne.setArAmt(getValueDouble("TOT_AMT")
				- getValueDouble("PAY_INS"));
		// 折扣金额
		receiptOne.setReduceAmt(getValueDouble("REDUCE_AMT"));
		// 减免科室
		receiptOne.setReduceDeptCode(getValueString("REDUCE_DEPT_CODE"));
		// 减免原因
		receiptOne.setReduceReason(getValueString("REDUCE_REASON"));
		// 减免人员
		receiptOne.setReduceRespond(getValueString("REDUCE_RESPOND"));
		// 收据费用的添加(charge01~charge30)
		receiptOne.initCharge(opb.getChargeParm());
		// 给票据对象添加总金额
		opb.getBilinvrcpt().setArAmt(getValueDouble("TOT_AMT"));

	}

	/**
	 * 删除新增医嘱
	 */
	public void onDelete() {
		// 如果没有新开立的医嘱无法删除
		// if (orderList == null) {
		// return;
		// }
		// 得到要删除的table行
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return;
		}
		// ===zhangp 20120424 start
		if (ektTCharge.isSelected()) {
			this.messageBox("已收费医嘱不能删除");
			return;
		}
		// ===zhangp 20120424 end
		int removeRow = table.getSelectedRow();
		// 检核是否有权限删除选中的医嘱
		// ===zhangp 20120414 start
		Order order = (Order) table.getParmValue().getData("OBJECT", removeRow);
		// if (removeRow < drOrderCount && !deleteFun) {
		if (removeRow < drOrderCount
				&& !deleteFun
				&& !(order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			// ===zhangp 20120414 end
			this.messageBox("此医嘱是医生开立!");
			return;
		}
		if (orderList == null) {
			if (!deleteSetCodeOrder(order, removeRow, true))
				return;
		}
		if (orderList != null) {
			// ============pangben 2013-1-7 操作错误删除集合医嘱细项
			Order orderTemp = null;// 删除集合医嘱细项
			orderList.removeData(order);
			table.removeRow(removeRow);
			if (order.getOrderSetGroupNo() > 0) {// 移除集合医嘱
				for (int i = orderList.getTableParm().getCount("ORDER_CODE") - 1; i >= 0; i--) {
					orderTemp = orderList.getOrder(i);
					if (null == orderTemp)
						continue;
					if (null != orderTemp.getRxNo()
							&& orderTemp.getRxNo().equals(order.getRxNo())
							&& orderTemp.getOrderSetGroupNo() == order
									.getOrderSetGroupNo()) {
						orderList.removeData(orderTemp);// 移除细项
					}
				}
			}
			if (!deleteSetCodeOrder(order, removeRow, false))
				return;
		} else {
			onClear();
			onEKT();
		}
		double fee = getFee();
		callFunction("UI|TOT_AMT|setValue", fee);
		setFeeReview();
	}

	/**
	 * 删除操作 删除界面上集合医嘱细项问题 =======pangben 2013-1-10
	 * 
	 * @param order
	 * @param removeRow
	 * @return
	 */
	private boolean deleteSetCodeOrder(Order order, int removeRow, boolean flg) {
		TParm result = new TParm();
		if (order.getOrderSetGroupNo() > 0) {
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"deleteOPBChargeSet", order.getParm());
			if (result.getErrCode() < 0) {
				messageBox("删除失败");
				return false;
			}
			if (flg) {
				OdoUtil.deleteOrderSet(orderList, order);
				// 把删除的医嘱从table上一处
				table.removeRow(removeRow);
				if (orderList == null) {
					onClear();
					onEKT();
				}
				double fee = getFee();
				callFunction("UI|TOT_AMT|setValue", fee);
				setFeeReview();
				return false;
			}
		} else {
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"deleteOPBCharge", order.getParm());
			if (result.getErrCode() < 0) {
				messageBox("删除失败");
				return false;
			}
		}
		return true;
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		oldMrNo="";
		// ====zhangp 20120227 modify start
		checkBox.setEnabled(true);
		checkBox.setSelected(true);
		this.callFunction("UI|save|setEnabled", true);
		this.callFunction("UI|CHARGE|setEnabled", true);
		// =====zhangp 20120227 modify end
		clearValue("REDUCE_REASON;REDUCE_DEPT_CODE;REDUCE_RESPOND;REDUCE_AMT;");
		clearValue("PAY_CASH;PAY_MEDICAL_CARD;PAY_BILPAY;PAY_INS;PAY_BANK_CARD;PAY_CHECK;");
		clearValue("PAY_DEBIT;PAY_OTHER1;TOT_AMT;PAY;PAY_RETURN;PAY_INS_CARD;PAY_OTHER2");
		clearValue("PAY_REMARK;MR_NO;PAT_NAME;IDNO;AGE;SEX_CODE;CTZ1_CODE");
		clearValue("CTZ2_CODE;CTZ3_CODE;PRESCRIPTION;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;CAT1_TYPE;EKT_CURRENT_BALANCE;AMT");
		//wanglong add 20141011 增加更改票据抬头权限
		callFunction("UI|CHANGE_TITLE|setSelected", false);
		callFunction("UI|TITLE|setEnabled", false);
	    clearValue("TITLE");
		callFunction("UI|record|setEnabled", false);
		this.setValue("SERVICE_LEVEL", "");
		TLabel qTxt = (TLabel) this.getComponent("Q_TXT");
		qTxt.setText("");
		// 清空table数据
		table.removeRowAll();
		setViewModou(false);
		opb = null;
		// 如果病患被锁定解锁
		unLockPat();

		reg = null;
		pat = null;
		
		ektParmSave = null;

		// 当前选中的行
		selectRow = -1;
		// 是否新增过处方签
		pFlg = "N";
		// 记录医生开立医嘱的数量
		drOrderCount = -1;
		// 新建orderlist
		orderList = null;
		setViewModou(false);
		setValue("BILL_TYPE", "C");
		/**
		 * 判断是否为全院或组长权限
		 */
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", true);
			// ======zhangp 20120227 modify start
			// 初始化票据
			BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				initBilInvoice(bilInvoice.initBilInvoice("OPB"));
			}
			// ===============pangben 2012-3-30 管控
//			this.callFunction("UI|MR_NO|setEnabled", false); //delete by huangtt 20150629
			// =======zhang 20120227 modify end
		}
		/**
		 * 根据进参默认医嘱类型
		 */
		if (this.getPopedem("opbPHA")) {
			setValue("CAT1_TYPE", "PHA");
		}
		if (this.getPopedem("LIS")) {
			setValue("CAT1_TYPE", "LIS");
		}
		if (this.getPopedem("RIS")) {
			setValue("CAT1_TYPE", "RIS");
		}
		if (this.getPopedem("TRT")) {
			setValue("CAT1_TYPE", "TRT");
		}
		if (this.getPopedem("PLN")) {
			setValue("CAT1_TYPE", "PLN");
		}
		if (this.getPopedem("OTH")) {
			setValue("CAT1_TYPE", "OTH");
		}
		// ==================pangben modify 20110804 删除按钮隐藏
		deleteFun = false; // 设定医嘱删除
		drOrderCountFalse = false; // 第一次记录就诊病患的医嘱信息
		drOrderCountTemp = 0; // 第一次记录就诊病患的医嘱信息
		feeShow = false; // 管控，显示金额使用 pangben modify 20110804
		isbill = false; // 管控，是否记账 pangben modify 20110818
		EKTmessage = false; // 管控 医疗卡退费操作
		isEKT = false; // 医疗卡信息读取操作
		tredeParm = null; // 判断支付方式
		insParm = null; // 医保 参数
		insFlg = false; // 医保卡读取操作
		resultBill = null; // 记账数据
		// ===zhangp 20120309 modify start
		ektTCharge.setEnabled(false);
		// ==zhangp 20120309 modify end
		// 医保结算打印
		callFunction("UI|insPrint|setEnabled", false);
		// 医疗卡打印
		callFunction("UI|ektPrint|setEnabled", false);
		// ===zhangp 20120331 start
		setValue("BILL_TYPE", "E");
		// ===zhangp 20120331 end
		setValue("OPB_ADVANCE_TYPE", "1");//收费类型默认为正常
	}

	/**
	 * 设置显示是否收费
	 * 
	 * @param view
	 *            boolean
	 */
	public void setViewModou(boolean view) {
		checkBoxNotCharge.setSelected(!view);
		checkAll.setSelected(view);
	}

	/**
	 * 右击MENU弹出事件
	 */
	public void onTableRightClicked() {
		int tableSelectRow = table.getSelectedRow();
		// 检核医嘱信息...如果是集合医嘱....如果是pha
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				tableSelectRow);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().length() > 0) {
			table.setPopupMenuSyntax("显示集合医嘱细相,onOrderSetShow");
			return;
		}
		if (order.getOrderCat1Code().contains("PHA")) {
			table.setPopupMenuSyntax("显示药嘱信息,onSysFeeShow");
			return;
		}
		table.setPopupMenuSyntax("");
	}

	/**
	 * 右击MENU显示集合医嘱事件
	 */
	public void onOrderSetShow() {
		Order order = (Order) table.getParmValue().getData("OBJECT",
				table.getSelectedRow());
		String orderCode = order.getOrderCode();
		int groupNo = order.getOrderSetGroupNo();
		TParm parm = opb.getOrderSetParm(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);

	}

	/**
	 * 右击MENU显示SYS_FEE事件
	 */
	public void onSysFeeShow() {
		Order order = (Order) table.getParmValue().getData("OBJECT",
				table.getSelectedRow());
		String orderCode = order.getOrderCode();
		TParm parm = new TParm();
		parm.setData("FLG", "OPD");
		parm.setData("ORDER_CODE", orderCode);
		this.openWindow("%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_PHA.x", parm);

	}

	/**
	 * 各种操作前的数据检核
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// 检核人
		if (opb == null) {
			return true;
		}
		// 检核开关长
		if (checkOpenBill()) {
			return true;
		}
		// 检核医嘱
		if (opb.checkOrder()) {
			this.messageBox("没有要收费的医嘱");
			return true;
		}
		return true;
	}

	/**
	 * 检核开关账
	 * 
	 * @return boolean
	 */
	public boolean checkOpenBill() {
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return false;
		}
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| !opb.initBilInvoice()) {
			this.messageBox("没有开账!");
			return true;
		}

		return false;
	}

	/**
	 * 检核医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrder() {
		if (table.getRowCount() <= 1) {
			this.messageBox("无任何需要保存的医嘱!");
			return true;
		}
		return false;
	}

	/**
	 * 病患加锁
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat() {
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
//		// 判断是否加锁
//		if (parm != null && parm.getCount() > 0) {
//			if (isMyPat(parm)) {
//				return true;
//			}
//			if (RootClientListener.getInstance().isClient()) {
//				parm.setData("PRGID_U", "OPB");
//				parm.setData("MR_NO", pat.getMrNo());
//				String prgId = parm.getValue("PRG_ID", 0);
//				if ("ODO".equals(prgId)) {
//					parm.setData("WINDOW_ID", "OPD01");
//				} else if ("ODE".equals(prgId)) {
//					parm.setData("WINDOW_ID", "ERD01");
//				} else if ("OPB".equals(prgId)) {
//					parm.setData("WINDOW_ID", "OPB0101");
//				} else if ("ONW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:门诊
//					parm.setData("WINDOW_ID", "ONW01");
//				else if ("ENW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:急诊
//					parm.setData("WINDOW_ID", "ONWE");
//				String flg = (String) openDialog(
//						"%ROOT%\\config\\sys\\SYSPatLcokMessage.x", parm);
//				if ("UNLOCKING".equals(flg)) {
//					this.onQuery();
//					return false;
//				}
//				if ("LOCKING".equals(flg)) {
//					this.onClear();
//					return false;
//				}
//				if ("OK".equals(flg)) {
//					PatTool.getInstance().unLockPat(pat.getMrNo());
//					PATLockTool.getInstance().log(
//							"ODO->" + SystemTool.getInstance().getDate() + " "
//									+ Operator.getID() + " "
//									+ Operator.getName() + " 强制解锁[" + aa
//									+ " 病案号：" + pat.getMrNo() + "]");
//				} else {
//					this.onClear();
//					return false;
//				}
//			} else {
//				if (this.messageBox("是否解锁", PatTool.getInstance()
//						.getLockParmString(pat.getMrNo()), 0) == 0) {
//					PatTool.getInstance().unLockPat(pat.getMrNo());
//					PATLockTool.getInstance().log(
//							"ODO->" + SystemTool.getInstance().getDate() + " "
//									+ Operator.getID() + " "
//									+ Operator.getName() + " 强制解锁[" + aa
//									+ " 病案号：" + pat.getMrNo() + "]");
//				} else {
//					onClear();
//					return false;
//				}
//			}
//		}
//		// 锁病患信息
//		if (!PatTool.getInstance().lockPat(pat.getMrNo(), checklockPat())) {
//			onClear();
//			return false;
//		}
		return true;
	}
	/**
	 * pangben 2013-5-15
	 * 护士站解锁功能
	 * @return
	 */
	private String checklockPat(){
		String type = "OPB";// 添加护士站拒绝解锁功能，区分门急诊护士站操作
		if (systemCode!=null && "ONW".equals(systemCode)) {
			if(null!=onwType && onwType.equals("O"))
				type = "ONW";
			else{
				type = "ENW";
			}
		}
		return type;
	}
	/**
	 * 是否正在本人手中锁住病患
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean isMyPat(TParm parm) {
		if (!checklockPat().equals(parm.getValue("PRG_ID", 0))
				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
			return false;
		}
		return true;
	}

	/**
	 * 病患解锁
	 */
	public void unLockPat() {
//		if (pat == null) {
//			return;
//		}
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
//			if ("OPB".equals(parm.getValue("PRG_ID", 0))
//					&& (Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
//					&& (Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//			}
//		}
//		pat = null;
	}

	/**
	 * 现金回车实践 PAY_CASH
	 */
	public void onPayCash() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// //刷卡
		// double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payBankCard = arAmt - payCash - payDebit - payCheck
				- payMedicalCard - payOther2 - payBilPay - payInsCard;
		// 格式化金额
		payBankCard = StringTool.round(payBankCard, 2);
		if (payBankCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_BANK_CARD|setValue", payBankCard);
		// 银行卡得到焦点
		callFunction("UI|PAY_BANK_CARD|grabFocus");
	}

	/**
	 * 银行卡回车事件 PAY_BANK_CARD
	 */
	public void onPayBankCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// //医疗卡
		// double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payMoidCard = arAmt - payCash - payDebit - payCheck
				- payBankCard - payOther2 - payBilPay - payInsCard;
		// 格式化金额
		payMoidCard = StringTool.round(payMoidCard, 2);
		if (payMoidCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_MEDICAL_CARD|setValue", payMoidCard);
		// 医疗卡得到焦点
		callFunction("UI|PAY_MEDICAL_CARD|grabFocus");

	}

	/**
	 * 医疗卡回车事件 PAY_MEDICAL_CARD
	 */
	public void onPayMediCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// //医保卡
		// double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payInsCard = arAmt - payCash - payDebit - payCheck - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// 格式化金额
		payInsCard = StringTool.round(payInsCard, 2);
		if (payInsCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_INS_CARD|setValue", payInsCard);
		// 医疗卡得到焦点
		callFunction("UI|PAY_INS_CARD|grabFocus");
	}

	/**
	 * 医保卡回车事件 PAY_INS_CARD
	 */
	public void onPayInsCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// //支票
		// double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// /其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payCheck = arAmt - payCash - payDebit - payInsCard - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// 格式化金额
		payCheck = StringTool.round(payCheck, 2);
		if (payCheck < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}

		// 赋值
		callFunction("UI|PAY_CHECK|setValue", payCheck);
		// 支票支付得到焦点
		callFunction("UI|PAY_CHECK|grabFocus");

	}
	/**
	 * 关闭事件
	 * =============pangben 2014-7-11
	 * @return boolean
	 */
	public boolean onClosing() {
		unLockPat();
		return true;
	}
	/**
	 * 支票支付回车事件 PAY_CHECK
	 */
	public void onPayCheck() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER2");
		// //押金
		// double payBilPay = getValueDouble("PAY_BILPAY");

		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payBilPay = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payOther2 - payCheck - payMedicalCard;
		// 格式化金额
		payBilPay = StringTool.round(payBilPay, 2);
		if (payBilPay < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_BILPAY|setValue", payBilPay);
		// 押金支付得到焦点
		callFunction("UI|PAY_BILPAY|grabFocus");

	}

	/**
	 * 押金支付 PAY_BILPAY
	 */
	public void onPayBilPay() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// //其它支付
		// double payOther2 = getValueDouble("PAY_OTHER2");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");

		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payOther2 = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payCheck - payMedicalCard - payBilPay;
		// 格式化金额
		payOther2 = StringTool.round(payOther2, 2);
		if (payOther2 < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_OTHER2|setValue", payOther2);
		// 其它支付得到焦点
		callFunction("UI|PAY_OTHER2|grabFocus");
	}

	/**
	 * 其它支付 PAY_OTHER2
	 */
	public void onPayOther2() {
		// 检核费用事件
		// 收款金额得到焦点
		callFunction("UI|PAY|grabFocus");
	}

	/**
	 * 交款金额回车事件 PAY
	 */
	public void onPay() {
		// 折扣金额
		double pay = getValueDouble("PAY");
		// 折扣金额
		double arAmt = getValueDouble("TOT_AMT");
		if (pay - arAmt < 0 || pay == 0) {
			this.messageBox("金额不足!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_RETURN|setValue", StringTool.round((pay - arAmt),
				2));
		// this.grabFocus("CHARGE");
	}

	/**
	 * 费用明细查询和退费
	 */
	public void onBackReceipt() {
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO_OLD", oldMrNo);
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		this.openDialog("%ROOT%\\config\\opb\\OPBBackReceipt.x", opbParm);
		// 通过reg和caseNo重新初始化opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		if (opb == null) {
			// this.messageBox_(555555555);
			this.messageBox("此病人尚未就诊!");
			// return;=====pangben modify 20110801
		}
		// 初始化opb后数据处理
		afterInitOpb();
		this.onClear();

	}

	/**
	 * 记账费用明细查询和退费 =========================pangben 20110823
	 */
	public void onBackContract() {
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		this.openDialog("%ROOT%\\config\\opb\\OPBBackContract.x", opbParm);
		// 通过reg和caseNo重新初始化opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		if (opb == null) {
			// this.messageBox_(555555555);
			this.messageBox("此病人尚未就诊!");
			// return;=====pangben modify 20110801
		}
		// 初始化opb后数据处理
		afterInitOpb();
		this.onClear();

	}

	/**
	 * 重新计算费用
	 */
	public void setFeeReview() {
		double fee = TypeTool.getDouble(getValue("TOT_AMT"));
		if ("E".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 医疗卡
			callFunction("UI|PAY_MEDICAL_CARD|setValue", fee);
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(true);

		} else if ("C".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 现金
			callFunction("UI|PAY_CASH|setValue", fee);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("P".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 记账
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", fee);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("I".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 医保
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", fee);
			if (null != tredeParm) {
				ektTCharge.setEnabled(true);
			} else {
				ektTCharge.setEnabled(false);
			}
		}

	}

	/**
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();
		// Operator.setData("admin","HIS","127.0.0.1","C00101");
		// System.out.println("成本中心代码"
		// + DeptTool.getInstance().getCostCenter("01020101", "0003"));
	}

	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		txReadEKT();
	}

	public void onEKTPrint() {
		if (this.messageBox("提示", "是否打印", 2) != 0) {
			return;
		}
		onEKTPrint("", true, null);
	}

	/**
	 * 医疗卡和现金打票(记账单位：现金使用)
	 * 
	 * @param contractCode
	 *            String
	 * @param ektPrintFlg
	 *            boolean
	 * @param insResult
	 *            TParm
	 */
	public void onEKTPrint(String contractCode, boolean ektPrintFlg,
			TParm insResult) {
		if (opb == null || onlyCaseNo.length() == 0) {
			this.messageBox("先选择病患");
		}
		// 检核开关帐
		if (opb.getBilInvoice() == null) {
			this.messageBox_("你尚未开账!");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return;
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| opb.getBilInvoice().getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			return;
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().equals(
				opb.getBilInvoice().getEndInvno())) {
			this.messageBox_("最后一张票据!");
			// return;
		}
		// 显示下一票号
		callFunction("UI|UPDATE_NO|setValue", opb.getBilInvoice().getUpdateNo());
		String updateNo = this.getValueString("UPDATE_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", onlyCaseNo);
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("INV_NO", updateNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());
		// =============pangben modify 201110817 start
		parm.setData("feeShow", feeShow); // 金额的保存使用管控
		parm.setData("TOT_AMT", this.getValueDouble("TOT_AMT"));
		parm.setData("billFlg", isbill ? "N" : "Y"); // 记账: N 不记账:Y
		parm.setData("CONTRACT_CODE", contractCode); // 记账单位
		parm.setData("ADM_TYPE", reg.getAdmType()); // 挂号方式 :0 \E
		// =============pangben modify 201110817 stop
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm opdParm = new TParm();
		TParm result = new TParm();
		boolean flg = true; // 控制医疗卡打票
		TParm opbReceiptParm = new TParm(); // 获得收据号和医疗卡金额
		if ("E".equals(this.getValueString("BILL_TYPE")) && isEKT) { // 医疗卡打票操作
			opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
			result = ektSavePrint(opdParm, opbReceiptParm, parm);
			isbill = false;
			flg = false;
		} else if ("C".equals(this.getValueString("BILL_TYPE"))
				|| "P".equals(this.getValueString("BILL_TYPE")) && !isEKT) {
			opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
			result = cashSavePrint(opdParm, parm, null);
		} else if ("I".equals(this.getValueString("BILL_TYPE"))) { // 医保卡操作
			// 执行医保操作
			if (ektPrintFlg && isEKT) {
				TParm readCard = EKTIO.getInstance().TXreadEKT();// 泰心医疗卡读卡操作
				if (!this.getValue("MR_NO").equals(readCard.getValue("MR_NO"))) {
					this.messageBox("医疗卡病患信息不符,不可以执行打票操作");
					return;
				}

				opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
				TParm insReturnParm = insExeFee(opdParm, true); // 医保收费操作
				if (null == insReturnParm) {
					this.messageBox("医保操作失败");
					return;
				}
				insParm.setData("ACCOUNT_AMT", insReturnParm
						.getDouble("ACCOUNT_AMT")); // 医保金额
				insParm.setData("UACCOUNT_AMT", insReturnParm
						.getDouble("UACCOUNT_AMT")); // 现金金额

				insParm.setData("comminuteFeeParm", insReturnParm.getParm(
						"comminuteFeeParm").getData()); // 费用分割数据
				insParm.setData("settlementDetailsParm", insReturnParm.getParm(
						"settlementDetailsParm").getData()); // 费用结算
			} else {
				opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
			}

			if (ektPrintFlg && isEKT) {
				parm.setData("ACCOUNT_AMT", insParm.getDouble("ACCOUNT_AMT")); // 医保金额
				result = ektSavePrint(opdParm, opbReceiptParm, parm);
				isbill = false;
				flg = false; // 医疗卡打票操作
			} else {
				result = cashSavePrint(opdParm, parm, insResult);
			}
		}
		if (null == result || result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String receiptNo = result.getValue("RECEIPT_NO", 0);
		String printNoFee = "N";
		if (result.getData("PRINT_NOFEE", 0) != null) {
			printNoFee = "Y";
		}
		if ("Y".equals(printNoFee)) {
			this.onClear();
			return;
		}
		// =================pangben modify20110818
		if (isbill) {
			this.messageBox("已经记帐,不打印票据");
			return;
		}
		TParm recpParm = null;

		// 门诊收据档数据:医疗卡收费打票|现金收费打票||医保打票
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0 && insFlg) {
			// 医保在途状态删除
			if (!updateINSPrintNo(reg.caseNo(), "OPB")) {
				updateINSPrintNo(reg.caseNo(), "OPB");
			}
		}
		onPrint(recpParm, flg);
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", "OPB");
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 修改医保票据号
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean updateINSPrintNo(String caseNo, String exeType) {
		TParm parm = new TParm();
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		parm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		parm.setData("PRINT_NO", insParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE"));
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
				"updateINSPrintNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 医疗卡执行打票操作
	 * 
	 * @param opdParm
	 *            TParm
	 * @param opbReceiptParm
	 *            TParm
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm ektSavePrint(TParm opdParm, TParm opbReceiptParm, TParm parm) {
		TParm EKTTemp = EKTIO.getInstance().TXreadEKT();
		TParm result = new TParm();
		if (null == EKTTemp || EKTTemp.getErrCode() < 0
				|| EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox("此医疗卡无效");
			return null;
		}
		// opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("无可收费医嘱");
			return null;
		}
		if (!insFee(opdParm, opbReceiptParm, EKTTemp))
			return null;
		parm.setData("opdParm", opdParm.getData()); // 获得一条汇总金额
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBEktprint", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 现金打票
	 * 
	 * @param opdParm
	 *            TParm
	 * @param parm
	 *            TParm
	 * @param insResult
	 *            TParm
	 * @return TParm
	 */
	private TParm cashSavePrint(TParm opdParm, TParm parm, TParm insResult) {
		// 现金 记账 医保卡执行操作
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("无可收费医嘱");
			return null;
		}
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			parm.setData("INS_RESULT", insResult.getData()); // 医保出参
			parm.setData("INS_FLG", "Y");
		}
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBCashprint", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 医保收费
	 * 
	 * @param opdParm
	 *            TParm
	 * @param opbReceiptParm
	 *            TParm
	 * @param EKTTemp
	 *            TParm
	 * @return boolean
	 */
	private boolean insFee(TParm opdParm, TParm opbReceiptParm, TParm EKTTemp) {
		// TParm result = new TParm();
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			// 添加医保操作
			TParm ins_result = onINSAccntClient(opdParm); // 医保卡操作，回参数据获得扣除医保金额以后的医嘱信息
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("E0005");
				return false;
			}
			insFlg = true; // 判断是否执行医保在途状态删除
			// 医保退费回冲医疗卡金额操作
			// orderParm.setData("INS_FLG", "N");// 非医保卡操作
			opdParm.setData("AMT", -insParm.getDouble("ACCOUNT_AMT"));
			opdParm.setData("NAME", pat.getName());
			opdParm.setData("SEX", pat.getSexCode() != null
					&& pat.getSexCode().equals("1") ? "男" : "女");
			opdParm.setData("INS_FLG", "Y"); // 医保使用
			// 需要修改的地方
			opdParm.setData("MR_NO", pat.getMrNo());
			opdParm.setData("RECP_TYPE", "OPB"); // 收费类型

			// readCard.setData("CARD_NO", cardNo);
			// =================pangben 20110919 stop
			TParm result = insExeUpdate(insParm.getDouble("ACCOUNT_AMT"),
					EKTTemp, reg.caseNo(), "OPB", 9);
			// TParm result=EKTIO.getInstance().insUnFee(opdParm,this);
			if (result.getErrCode() < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 打印票据封装===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		// 特殊人员类别
		String spcPerson = "";
		
		double gbNhiPay = 0.00; // 医保支付
		double gbCashPay = 0.00; // 现金支付
		String caseNo = opb.getReg().caseNo();
		//查询医生名称
		String CtzDescSql =" SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
        					" WHERE A.CASE_NO = '"+caseNo+"'"+
        					" AND A.CTZ1_CODE = B.CTZ_CODE"+
        					" AND A.REALDR_CODE =C.USER_ID";		 
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
		String personClass  = CtzDescParm.getValue("CTZ_DESC", 0);
		// 票据信息		
		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// 姓名
		oneReceiptParm.setData("SEX", "TEXT", opb.getPat().getSexString());//性别
		oneReceiptParm.setData("ID_NO", "TEXT", opb.getPat().getIdNo());//	
		oneReceiptParm.setData("RECEIPT_NO", "TEXT",  recpParm.getValue("RECEIPT_NO", 0));		
		
		if (((TPanel) this.getComponent("TITLE_PANEL")).isVisible()) {// wanglong add 20141011 增加更改票据抬头权限
			if (((TCheckBox) this.getComponent("CHANGE_TITLE")).isSelected()) {
                oneReceiptParm.setData("PAT_NAME", "TEXT", this.getValueString("TITLE"));
            }
        }
		
		
		// 特殊人员类别
				oneReceiptParm.setData("SPC_PERSON", "TEXT",
						(personClass+spcPerson).length() == 0 ? "自费" : personClass+" "+spcPerson);
		//零差率显示
			oneReceiptParm.setData("ADVANCE_TITLE", "TEXT", "天津医保垫付患者需回医院补联网");
		//医院名称	
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
		// 费用合计
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// 费用显示大写金额
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));
		// 现金支付= 医疗卡金额+现金+绿色通道+支付宝
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2)
				+ StringTool.round(recpParm.getDouble("ALIPAY",0), 2);
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));
        //医院机构类型(三甲医院)
        String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
		oneReceiptParm.setData("MR_NO", "TEXT", pat.getMrNo());
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy   MM   dd"));//
		//医保统筹支付
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df.format(gbNhiPay));
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		
		// 医生名称
		oneReceiptParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue("USER_NAME", 0));
		
		oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用清单)");

		// 打印人
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		TParm EKTTemp = null;
		if (!flg) {
			EKTTemp = EKTIO.getInstance().TXreadEKT();
			if (EKTTemp.getErrCode() < 0) {
				this.messageBox("此医疗卡无效");
				return;
			}
			if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
				this.messageBox("此医疗卡无效");
				// 添加出现问题撤销
				return;
			}
		}
		
		oneReceiptParm.setData("CARD_CODE", "TEXT", "");
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE0" + i, 0));
			} else {
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE" + i, 0));
			}
		}
		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
				.getDouble("CHARGE01", 0)
				+ recpParm.getDouble("CHARGE02", 0)));
		

		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", reg.getAdmType());
		//个人账户支付
		oneReceiptParm.setData("DA_AMT", "TEXT", "0.00");
		//其他医保支付
		oneReceiptParm.setData("QTYL", "TEXT", "0.00");
		//清单打印
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730
		this.onClear();
		return;

	}

	/**
	 * 现金打票明细入参
	 * 
	 * @param oneReceiptParm
	 *            TParm
	 * @param recpParm
	 *            TParm
	 * @param dparm
	 *            TParm
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (tableresultparm.getCount() > 6) {
			oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * 权限检核
	 * 
	 * @param orderParm
	 *            TParm
	 * @return boolean
	 */
	public boolean chekeRolo(TParm orderParm) {
		int count = orderParm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (this.getPopedem("LEADER")) {
				if (!Operator.getDept().equals(
						orderParm.getValue("EXEC_DEPT_CODE", i))
						&& orderParm.getDouble("DOSAGE_QTY", i) < 0) {
					this.messageBox("不同科室不可输入负值!");
					return false;
				}
			} else {
				if (orderParm.getDouble("DOSAGE_QTY", i) < 0) {
					this.messageBox("不可输入负值!");
					return false;
				}
			}
		}
		return true;

	}

	/**
	 *医嘱类别改变
	 */
	public void onChangeCat1Type() {
		this.setValue("PRESCRIPTION", "");
		if (table != null) {
			table.acceptText();
		}
		checkBoxNotCharge.setSelected(true);
		onNotCharge();
	}

	/**
	 * 重新计算费用
	 * 
	 * @return double
	 */
	public double getFee() {
		double fee = 0.00;
		double allFee = 0.00;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int tableCount = tableParm.getCount("AR_AMT");
		for (int i = 0; i < tableCount; i++) {
			if ("Y".equals(tableParm.getValue("CHARGE", i))
					&& tableParm.getValue("CHARGE", i).length() != 0) {
				fee = tableParm.getDouble("AR_AMT", i);
				allFee = allFee + fee;
			}
		}
		return allFee;
	}

	/**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) {
		if (!"OPB".equalsIgnoreCase(prgId)) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x", parm);
		if ("OK".equals(flg)) {
			this.onClear();
			return "OK";
		}
		return "";
	}

	/**
	 * 读取数据 ================pangben modify 20110815 获得的价格可以保存到数据中
	 */
	public void readXML() {
		// TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzcfsj.xml");
		TParm parm = NJCityInwDriver.getPame("c:/mzcfsj.xml");
		double sum = 0.0;
		for (int i = 0; i < parm.getCount(); i++) {
			String temp = parm.getValue("DJ").substring(1,
					parm.getValue("DJ").indexOf("]"));
			Double price = new Double(temp);
			temp = parm.getValue("SL").substring(1,
					parm.getValue("SL").indexOf("]"));
			int count = new Integer(temp);
			sum += price * count;

		}
		feeShow = true; // 管控
		this.setValue("TOT_AMT", sum); // 收费
	}

	/**
	 * 泰心读取医疗卡操作
	 */
	public void txReadEKT() {
		// parmEKT= TXreadEKT("MR_NO");
		// kangy 脱卡还原    start
/*		if(dev_flg){
  		  parmEKT=EKTReadCard.getInstance().readEKT();
  	}else{
  		 parmEKT = EKTReadCard.getInstance().TXreadEKT();
  	}*/
		parmEKT = EKTIO.getInstance().TXreadEKT();
		//kangy 脱卡还原   end
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		EKT(parmEKT);
/*		// 医疗卡打印
		callFunction("UI|ektPrint|setEnabled", true);
		if (insParm != null) {
			// 医保结算打印
			callFunction("UI|insPrint|setEnabled", true);
		}
		isEKT = true; // 医疗卡操作
		this.setValue("PAY_MEDICAL_CARD", parmEKT.getDouble("CURRENT_BALANCE"));
		this.setValue("BILL_TYPE", "E");
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		onQuery();
		// ===zhangp 20120309 modify start
		if (!systemCode.equals("") && "ONW".equals(systemCode)) {
			ektTCharge.setEnabled(false);
		} else {
			ektTCharge.setEnabled(true);
			// ===ZHANGP 20120319 start
			setValue("EKT_CURRENT_BALANCE", parmEKT
					.getDouble("CURRENT_BALANCE"));
			// ================chenxi modify start 2012.05.21
			double surrentBalance = parmEKT.getDouble("CURRENT_BALANCE");
			double totAmt = getValueDouble("TOT_AMT");
			setValue("AMT", StringTool.round((surrentBalance - totAmt), 2));
			// callFunction("UI|AMT|setValue", StringTool.round((surrentBalance
			// - totAmt),
			// 2));
			// ========================chenxi modify stop 2012.05.21
			// ===ZHANGP 20120319 start
		}
		// ===zhangp 20120309 modify end
*/	}
	
	public void EKT(TParm parmEKT){// add by kangy 20170308
		// 医疗卡打印
				callFunction("UI|ektPrint|setEnabled", true);
				if (insParm != null) {
					// 医保结算打印
					callFunction("UI|insPrint|setEnabled", true);
				}
				isEKT = true; // 医疗卡操作
				this.setValue("PAY_MEDICAL_CARD", parmEKT.getDouble("CURRENT_BALANCE"));
				this.setValue("BILL_TYPE", "E");
				this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
				onQuery();
				// ===zhangp 20120309 modify start
				if (!systemCode.equals("") && "ONW".equals(systemCode)) {
					ektTCharge.setEnabled(false);
				} else {
					ektTCharge.setEnabled(true);
					// ===ZHANGP 20120319 start
					setValue("EKT_CURRENT_BALANCE", parmEKT
							.getDouble("CURRENT_BALANCE"));
					// ================chenxi modify start 2012.05.21
					double surrentBalance = parmEKT.getDouble("CURRENT_BALANCE");
					double totAmt = getValueDouble("TOT_AMT");
					setValue("AMT", StringTool.round((surrentBalance - totAmt), 2));
				}
				
	}
	
	
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 读医保卡======pangben 2011-11-29
	 */
	public void readINSCard() {
		TJreadINSCard();
	}

	/**
	 * 泰心医院医保卡读卡操作 =============pangben 20111129
	 */
	private void TJreadINSCard() {
		// String mrNo = "000000001116";
		// if (!isEKT) {
		//
		// this.messageBox("请先获得医疗卡信息");
		// return;
		// }
		if (null == pat || null == pat.getMrNo() || pat.getMrNo().length() <= 0
				|| null == reg) {
			this.messageBox("请获得病患信息");
			return;
		}
		//判断是否正常或是延迟垫付
		if(this.getValue("OPB_ADVANCE_TYPE").equals("")){
			this.messageBox("收费类型不能为空");
		    return;
		}			
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("INS_TYPE", reg.getInsPatType()); // 就医类别 1.城职普通2.城职门特
		// 挂号不是医保操作,在收费时不可以执行医保收费 提示不能执行医保操作
		if (null == reg.getConfirmNo() || reg.getConfirmNo().length() <= 0) {
			this.messageBox("此次就诊病患不是医保挂号,不能执行医保收费操作");
			return;
		}
		// 查询是否存在特批款操作
		TParm greenParm = new TParm();
		greenParm.setData("CASE_NO", reg.caseNo());
		greenParm = PatAdmTool.getInstance().selEKTByMrNo(greenParm);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (greenParm.getDouble("GREEN_BALANCE", 0) > 0) {
			this.messageBox("此就诊病患使用特批款,不可以使用医保操作");
			return;
		}
		parm.setData("CARD_TYPE", 3); // 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院,5 :门特登记）
		parm.setData("INS_TYPE", reg.getInsPatType());// 挂号医保就诊类别
		//医院编码@费用发生时间@类别
		String admDate = StringTool.getString((Timestamp) this
				.getValue("STARTTIME"), "yyyyMMdd");// 费用发生时间
//		 System.out.println("admDate=========="+admDate);
		String opbadvancetype = "1";//收费类别
		String SQL = " SELECT PERSONAL_NO FROM INS_ADVANCE_OUT"+
        " WHERE CASE_NO = '"+ reg.caseNo()+ "'" +
        " AND APPROVE_TYPE ='1'" +
        " AND PAY_FLG = '0'";            
        TParm DATA= new TParm(TJDODBTool.getInstance().select(SQL));
		if(this.getValue("OPB_ADVANCE_TYPE").equals("2")){
//            System.out.println("DATA=========="+DATA);
            if (DATA.getCount()<= 0) {
    			messageBox("没有延迟垫付患者");
    			return;
    		}
//            System.out.println("PERSONAL_NO=========="+DATA.getValue("PERSONAL_NO",0));
            String personalno = DATA.getValue("PERSONAL_NO",0).trim();
        	opbadvancetype = "2";
            parm.setData("ADVANCE_PERSONAL_NO",personalno);//个人编码（无卡人员）	           
		}
		else{
			 if (DATA.getCount()> 0) {
				 messageBox("此患者是垫付延迟患者，收费类型为垫付延迟");	
	    		return;
	    	}
		}
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+opbadvancetype;
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@费用发生时间@类别
		parm.setData("ADVANCE_TYPE",opbadvancetype);//延迟垫付		
		insParm = null;
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null==insParm || null==insParm.getValue("RETURN_TYPE")) {
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType != 1) {
			this.messageBox("读取医保卡失败");
			insParm = null;
			return;
		}
		// ===zhangp 20120408 start
		String insType = insParm.getValue("INS_TYPE"); // 医保就诊类型: 1.城职普通 2.城职门特
		// 3.城居门特
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		// ============pangben 查询数据是否存在
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID") + "' AND PAT_NAME='"
					+ name + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID") + "' AND PAT_NAME='"
					+ name + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (insPresonParm.getErrCode() < 0) {
			this.messageBox("获得病患信息失败");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("此医保病患不存在医疗卡信息,\n医保信息:身份证号码:"
					+ opbReadCardParm.getValue("SID") + "\n医保病患名称:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		} else if (insPresonParm.getCount("MR_NO") > 1) {
			int flg = -1;
			if (this.getValue("MR_NO").toString().length() > 0) {
				for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
					if (insPresonParm.getValue("MR_NO", i).equals(
							this.getValue("MR_NO"))) {
						flg = i;
						break;
					}
				}
				if (flg == -1) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
			// onPatName();
		}
		// ===zhangp 20120408 end
		if (parmEKT != null && null != parmEKT.getValue("MR_NO")
				&& parmEKT.getValue("MR_NO").length() > 0) {
			// 医保结算打印
			callFunction("UI|insPrint|setEnabled", true);
			this.callFunction("UI|ektPrint|setEnabled", false);
		}
		// 需要校验身份证号码是否相同 不相同说明不是本人的医保卡 不能操作挂号
		// 判断人群类别
		// insFlg = true;// 医保卡读取成功
		this.setValue("BILL_TYPE", "I"); // 支付方式修改
		//===start==== modify by kangy
//		String sql1="SELECT distinct B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
//	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E WHERE "
//	               //+ "E.NHI_NO='6221511100082797685' "
//	               +" B.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
//	               +" AND E.MR_NO=A.MR_NO "
//	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
//		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql1));
//		TParm p3=infoParm.getRow(0);
//		p3.setData("READ_TYPE","INSCARD");
//		EKT(p3);
		//===end==== modify by kangy
	}

	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param opbParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(TParm opbParm, boolean flg) {
		// TParm insParm = new TParm();
		TParm insFeeParm = new TParm();
		if (null == reg.caseNo()) {
			return null;
		}
		double totAmt = 0.00; // 获得要收费的医嘱------集合医嘱所有医嘱都会显示
		TParm newOpbParm = new TParm();
		for (int i = 0; i < opbParm.getCount("ORDER_CODE"); i++) {
			if (null == opbParm.getValue("ORDERSET_CODE", i)
					|| !opbParm.getValue("ORDERSET_CODE", i).equals(
							opbParm.getValue("ORDER_CODE", i))) {
				totAmt += opbParm.getDouble("OWN_AMT", i);
				newOpbParm.addRowData(opbParm, i);
			}
		}
		insFeeParm.setData("CASE_NO", reg.caseNo()); // 退挂使用
		insFeeParm.setData("RECP_TYPE", "OPB"); // 收费使用
		insFeeParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 医保就诊号
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // 病患号
		insFeeParm.setData("FeeY", totAmt); // 应收金额
		insFeeParm.setData("PAY_TYPE", isEKT); // 支付方式
		insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		insExeParm(newOpbParm);
		insFeeParm.setData("REGION_CODE", insParm.getValue("REGION_CODE")); // 医保区域
		insFeeParm.setData("insParm", insParm.getData());
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
				insFeeParm);
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
		if (returnType == 1) {
			return returnParm;
		} else {
			return null;
		}
	}

	/**
	 * 医保扣款数据
	 * 
	 * @param newOpbParm
	 *            TParm
	 */
	private void insExeParm(TParm newOpbParm) {
		insParm.setData("REG_PARM", newOpbParm.getData()); // 所有要分割的医嘱
		insParm.setData("MR_NO", pat.getMrNo()); // 病患号
		insParm.setData("PAY_KIND", "11"); // 4 支付类别:11门诊、药店21住院//支付类别12
		insParm.setData("CASE_NO", reg.caseNo()); // 就诊号
		insParm.setData("RECP_TYPE", "OPB"); // 就诊类别
		insParm.setData("OPT_USER", Operator.getID()); // 区域代码
		// insParm.setData("REG_PARM", parm.getData());
		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // 科室代码
		insParm.setData("REG_TYPE", "0"); // 挂号标志:1 挂号0 非挂号
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("OPBEKTFEE_FLG", "Y"); // 门诊医疗卡收费注记----扣款打票时使用用来操作 收据表
		// BIL_OPB_RECP 医保金额修改
		insParm.setData("PRINT_NO", this.getValue("UPDATE_NO")); // 票号
		insParm.setData("DR_CODE", this.getValue("DR_CODE")); // 医生代码
		String admdate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		insParm.setData("ADM_DATE", admdate);
		if (reg.getAdmType().equals("E")) {
			insParm.setData("EREG_FLG", "1"); // 急诊
		} else {
			insParm.setData("EREG_FLG", "0"); // 普通
		}
	}

	/**
	 * 特殊人员类别
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "伤残军人";
		if ("06".equals(type))
			return "公务员";
		if ("07".equals(type))
			return "民政救助人员";
		if ("08".equals(type))
			return "优抚对象";
		return "";
	}

	/**
	 * 医疗卡操作保存此次医保卡扣款金额
	 * 
	 * @param accountAmt
	 *            double
	 * @param readCardParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param business_type
	 *            String
	 * @param type
	 *            int
	 * @return TParm
	 */
	private TParm insExeUpdate(double accountAmt, TParm readCardParm,
			String caseNo, String business_type, int type) {
		// 入参:AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("TYPE", type);
		orderParm.setData("readCard", readCardParm.getData());
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("OPT_TERM", Operator.getIP());
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}

	/**
	 * 手术室补充计费
	 */
	public void onOperation() {
		operationParm = new TParm();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", parm,
				false);
		if (null==operationParm) {//==pangben  2013-08-05
			return;
		}
		TParm parm_obj = new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
					+ operationParm.getValue("ORDER_CODE", i) + "' ";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm_obj == null || parm_obj.getCount() <= 0) {
				continue;
			}
			// TParm rowParm = parm_obj.getRow(0);
			// rowParm.setData("",operationParm.getDouble("DOSAGE_QTY", i));
			insertNewOperationOrder(parm_obj.getRow(0), operationParm
					.getDouble("DOSAGE_QTY", i),operationParm.getValue("DOSAGE_UNIT", i));
		}
	}

	/**
	 * 手术套餐回传新增
	 * 
	 * @param parm
	 *            TParm
	 * @param dosage_qty
	 *            double
	 */
	private void insertNewOperationOrder(TParm parm, double dosage_qty,String dosage_unit) {
		int selectRow = table.getRowCount() - 1;
		newReturnOrder(parm,selectRow,dosage_qty,dosage_unit,"STAT",false);//add "STAT" caoyong 20131218

	}
	/**
	 * 医疗卡充值
	 * yanjing
	 * 20130510
	 */
	public void onFee() {
		TParm parm =new TParm();
		parm.setData("FLG","Y");
		parm = (TParm) openDialog("%ROOT%\\config\\ekt\\EKTTopUp.x",
				parm);
		this.onClear();
	}
	/**
	 * 向对应的门诊药房发送消息
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages() {
		client1 = SocketLink
				.running("","ODO", "ODO");
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		String [] phaArray=new String [0];
		if(phaRxNo.length()>0){//获得所有操作的处方签号码 发送数据
			phaArray=phaRxNo.split(",");
		}
		for (int i = 0; i < phaArray.length; i++) {
			client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER 表添加数据
					+ phaArray[i] + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		}
//		client1.sendMessage("PHAMAIN", "RX_NO:"
//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
//		client1.sendMessage("PHAMAIN", "RX_NO:"
//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
//		client1.sendMessage("PHAMAIN", "RX_NO:"
//				+ "00000000002" + "|MR_NO:000022222222|PAT_NAME:李磊");
//		client1.sendMessage("PHAMAIN", "RX_NO:"
//				+ "00000000003" + "|MR_NO:000022222222|PAT_NAME:王浩");
//		client1.sendMessage("PHAMAIN", "RX_NO:"
//				+ "00000000004" + "|MR_NO:000033333333|PAT_NAME:张涛");
		if (client1 == null)
			return;
		client1.close();
	}
	
	/**
	 * 更改票据抬头
	 */
    public void onChangeTitle() {// wanglong add 20141011
        if (((TCheckBox) this.getComponent("CHANGE_TITLE")).isSelected()) {
            callFunction("UI|TITLE|setEnabled", true);
            this.setValue("TITLE", this.getValueString("PAT_NAME"));  
        } else {
            callFunction("UI|TITLE|setEnabled", false);
            this.setValue("TITLE", "");  
        }
    }
    
    
    /**
     * 取得所有医嘱   add by huangtt 20141126
     * 
     * @return
     */
    public TParm getOrder(){
		String sql = "SELECT CASE_NO,ORDER_CODE,DOSAGE_QTY,OWN_AMT,BILL_FLG,CTZ1_CODE," +
				" PHA_CHECK_DATE,PHA_DOSAGE_DATE,PHA_DISPENSE_DATE,RX_NO,SEQ_NO,BUSINESS_NO " +
				" FROM OPD_ORDER WHERE CASE_NO='"
				+ opb.getReg().caseNo() + "' ORDER BY ORDER_CODE";
		// System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
    }
    
    /**
     * 取得Q医支付数据 add by huangtt 20160620
     * @return
     */
    public int getQeOrder(){
    	String sql = "SELECT COUNT(CASE_NO) QCOUNT FROM OPD_ORDER WHERE CASE_NO='"+ opb.getReg().caseNo() +"' AND BILL_USER='QeApp' AND RECEIPT_NO IS NOT NULL";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	int countQ = parm.getInt("QCOUNT", 0);
    	
    	 sql = "SELECT COUNT(CASE_NO) COUNT FROM OPD_ORDER WHERE CASE_NO='"+ opb.getReg().caseNo() +"'";
    	TParm parmS = new TParm(TJDODBTool.getInstance().select(sql));
    	int countS = parmS.getInt("COUNT", 0);
    	
    	int re =0;
    	
    	if(countS > 0){
    		if(countQ == countS){
        		re = 1; //Q医已缴费
        	}else if(countS > countQ && countQ > 0){
        		re = 2; //Q医部分缴费 
        	}
        	
    	}
    	 
    	
    	
    	return re;
    }

    
    /**
     * 保存前进行新旧医嘱比对  add by huangtt 20141126
     * 
     * @param newOpdOrderParm
     * @return
     */
    public boolean orderComparison(TParm newOpdOrderParm){
    	String [] valueName = newOpdOrderParm.getNames();
		if(oldOpdOrderParm.getCount() != newOpdOrderParm.getCount()){
			this.messageBox(MESSAGE);
			return false;
		}else{
			List<Integer> temp = new ArrayList<Integer>();
			for (int i = 0; i < oldOpdOrderParm.getCount(); i++) {
				for (int j = 0; j < newOpdOrderParm.getCount(); j++) {
					if(oldOpdOrderParm.getValue("RX_NO", i).equalsIgnoreCase(newOpdOrderParm.getValue("RX_NO", j))
							&& oldOpdOrderParm.getValue("SEQ_NO", i).equalsIgnoreCase(newOpdOrderParm.getValue("SEQ_NO", j))){
						if(!temp.contains(i)){
							temp.add(i);
						}								
					}
				}
			}
			for (int i = 0; i < oldOpdOrderParm.getCount(); i++) {
				if(!temp.contains(i)){
					this.messageBox(MESSAGE);
					return false;
				}
			}
			for (int i = 0; i < newOpdOrderParm.getCount(); i++) {
				String orderCode = newOpdOrderParm.getValue("ORDER_CODE", i);
				String rxNo = newOpdOrderParm.getValue("RX_NO", i);
				String seqNo = newOpdOrderParm.getValue("SEQ_NO", i);
				int row = -1;
				for (int j = 0; j < oldOpdOrderParm.getCount(); j++) {
					if(orderCode.equals(oldOpdOrderParm.getValue("ORDER_CODE", j))
							&& rxNo.equals(oldOpdOrderParm.getValue("RX_NO", j))
									&& seqNo.equals(oldOpdOrderParm.getValue("SEQ_NO", j))){
						row =j;
						break;
					}
				}
				
				if(row == -1){
					this.messageBox(MESSAGE);
					return false;
				}
				
				for (int k = 0; k < valueName.length; k++) {
					if(!newOpdOrderParm.getValue(valueName[k], i).equalsIgnoreCase(oldOpdOrderParm.getValue(valueName[k], row))){
						this.messageBox(MESSAGE);
						return false;
					}
				}

				oldOpdOrderParm.removeRow(row);				
			}
		}
		return true;
    }
    //kangy 脱卡还原   start
/*    public void readIdCard(){// add by kangy 20170308 读取身份证执行读医疗卡操作 
    	TParm idParm= EKTReadCard.getInstance().readIDCard();
		if(idParm.getCount()==0){
			this.messageBox("该病人没有医疗卡，请执行制卡操作");
			return;
		}
		if(idParm.getCount()>1){
			parmEKT = (TParm) openDialog(
					"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", idParm);
		}else
			parmEKT=idParm.getRow(0);
    
		EKT(parmEKT);
    }*/
    //kangy 脱卡还原   end
}
