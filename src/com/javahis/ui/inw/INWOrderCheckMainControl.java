package com.javahis.ui.inw;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.inw.InwForOdiTool;
import jdo.inw.InwOrderCheckTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.sys.LEDUI;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

import jdo.emr.EMRAMITool;
import jdo.emr.EMRCdrTool;
import jdo.hrm.HRMPatAdm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import com.javahis.util.ADMUtil;
import com.javahis.util.AMIUtil;




/**
 * <p>
 * Title: 护士站医嘱审核
 * </p>
 * 
 * <p>
 * Description: 住院护士站医嘱审核主档
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */

public class INWOrderCheckMainControl extends TControl {
	private Compare compare = new Compare();

	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * 界面上的CUI对象
	 */
	private TTextFormat from_Date;
	private TTextFormat to_Date;

	private TTextField from_Time;
	private TTextField to_Time;

	// 医嘱类别
	private TRadioButton ord1All;
	private TRadioButton ord1ST;
	private TRadioButton ord1UD;
	private TRadioButton ord1DS;
	private TRadioButton ord1IG;

	// 医嘱种类
	private TRadioButton ord2All;
	private TRadioButton ord2PHA;
	private TRadioButton ord2PL;
	private TRadioButton ord2ENT;

	// 药嘱种类
	TCheckBox typeO;
	TCheckBox typeE;
	TCheckBox typeI;
	TCheckBox typeF;

	// 麻精
	TCheckBox typeC;

	// 临时/首日量
	TLabel StUdLab;
	// 智能柜取药
	private TRadioButton medSta;
	// 住院药房领药
	private TRadioButton medOrg;

    // 查询的时间种类 add by wanglong 20130626
    private TRadioButton firstDateRadio;// 默认时间
    private TRadioButton secondDateRadio;// 医生开立时间
    
	// 审核状态
	private TRadioButton checkAll;
	private TRadioButton checkYES;
	private TRadioButton checkNO;

	// 主table
	private TTable mainTable;
	// 全部执行
	private TCheckBox exeAll;
	// 全部打印
	private TCheckBox printAll;
	// 某个病人的就诊号
	private String caseNo = "";
	private String stationCode = "";

	private LEDUI ledui;
	
	boolean saveFlg = true;
	
	Map map;

    private boolean opeFlg = false;//术中医嘱注记wanglong add 20140707
    private String opDeptCode = "";//手术科室wanglong add 20140707
    private String opBookSeq = "";//手术申请单号wanglong add 20140707
    // 发送 取消标记
    private int flg;//wanglong add 20150520
    // 会调用HL7接口的数据
    TParm sendHL7Parm = new TParm();//wanglong add 20150520
    //20151028 wangjc add 补充计费用
    private String mrNo = "";
    private String ipdNo = "";
    private String clpCode_ = "";
    private String deptCode = "";
    
    private final int CHECK_INDEX = 18;   //审核表中的勾选框index
    
    /**
     * 手术类型
     * @author wangqing 20170701
     */
    private String typeCode;
    
    
    
	/**
	 * 初始化
	 */
	public INWOrderCheckMainControl() {
	}

	/**
	 * 初始化函数
	 */
	public void onInit() {
		super.onInit();
		// 得到外部的参数
		initParmFromOutside();
		// 本界面的初始化
		myInitControler();
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
            this.callFunction("UI|ord1ST|setSelected", true);// 选中“临时”单选框
        } else {
            callFunction("UI|medApplyNo|setVisible", false);// 显示
        }
		// 执行查询
		onQuery();
		lockComponet();
		// 排序监听
		addListener(mainTable);
		onCheckAMI(caseNo);
		
		// add by wangqing 20170701 start
		String sql = " SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ ='"+opBookSeq+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		if(result.getCount()<=0){
			return;
		}
		typeCode = result.getValue("TYPE_CODE", 0);
		// add by wangqing 20170701 end
		
		// add by wangqing 20170701 start
		// 只有胸痛中心的病人才有胸痛中心病历
		TParm amiResult = new TParm();
		amiResult= EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(ADMUtil.getCaseNo(caseNo));
		if(amiResult.getErrCode()<0){
			return;
		}
		if(amiResult.getCount()<=0){
			this.messageBox("没有病患信息！！！");
			return;
		}
		if(amiResult.getValue("ENTER_ROUTE", 0) != null 
				&& amiResult.getValue("ENTER_ROUTE", 0).equals("E02")){// 胸痛中心病患
			callFunction("UI|AMI|setEnabled", true);
		}else{
			callFunction("UI|AMI|setEnabled", false);
		}
		// add by wangqing 20170701 end
		
	}
	
	public void onCheckAMI(String incaseno){
		 TLabel amiL = (TLabel) this.getComponent("tLabel_23");

		 if(amiL!=null){
			 boolean isami = AMIUtil.isE02byInCaseNo(incaseno);
			 if(isami){			 
				 amiL.setVisible(true);
			 }else{
				 amiL.setVisible(false); 
			 }
		 }
	 }
	
	/**
	 * 介入/手术护士胸痛中心记录
	 */
	public void onAMI(){
		// add by wangqing 20170701 start
		// 只有胸痛中心的病人才有胸痛中心病历
		TParm amiResult = new TParm();
		amiResult= EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(ADMUtil.getCaseNo(caseNo));
		if(amiResult.getErrCode()<0){
			return;
		}
		if(amiResult.getCount()<=0){
			this.messageBox("没有病患信息！！！");
			return;
		}
		if(!(amiResult.getValue("ENTER_ROUTE", 0) != null 
				&& amiResult.getValue("ENTER_ROUTE", 0).equals("E02"))){// 胸痛中心病患
			this.messageBox("非胸痛中心病患！！！");
			return;
		}
		// add by wangqing 20170701 end
		
		
		if(typeCode != null && typeCode.equals("1")){// 外科手术
			onSURRecord();
		}else if (typeCode != null && typeCode.equals("2")){// 介入手术
			onINTRecord();
		}
	}

	
	/**
     * Kaison add
     *
     */
	public void onINTRecord(){
		System.out.println("onINTRecord!!!!!");

		TParm parm = new TParm();
		String mrNo = this.getMrNo().trim();
		String caseNo=this.getCaseNo().trim();
		parm.setData("mrNo",mrNo);
		parm.setData("caseNo",caseNo);
		this.openWindow("%ROOT%\\config\\ope\\OPEINTNSWindow.x", parm);
	
        
        System.out.println("end!!!!!");
	}
	
	public void onSURRecord(){
		System.out.println("onSURRecord!!!!!");

		TParm parm = new TParm();
		String mrNo = this.getMrNo().trim();
		String caseNo=this.getCaseNo().trim();
//		System.out.println("mrN0---->"+mrNo);
//		System.out.println("caseNo---->"+caseNo);
		
		parm.setData("mrNo",mrNo);
		parm.setData("caseNo",caseNo);
		//System.out.println("parm---->"+parm);
		this.openWindow("%ROOT%\\config\\ope\\OPESURNSWindow.x", parm);
	
        
        System.out.println("end!!!!!");
	}

	public void lockComponet() {
		// ((TTextFormat)getComponent("DEPT_CODE")).setEnabled(false);
		// ((TTextFormat)getComponent("VC_CODE")).setEnabled(false);
		((TMenuItem) getComponent("save")).setEnabled(saveFlg);
		callFunction("UI|medPrint|setEnabled", false);
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
            this.callFunction("UI|ord1All|setEnabled", false);// 禁用“全部”单选框
            this.callFunction("UI|ord1UD|setEnabled", false);// 禁用“长期”单选框
            this.callFunction("UI|ord1DS|setEnabled", false);// 禁用“出院带药”单选框
            this.callFunction("UI|ord1IG|setEnabled", false);// 禁用“住院草药”单选框
        }
	}

	/**
	 * 初始化界面参数caseNo/stationCode（从外部病患管理界面传来的参数）
	 */
	public void initParmFromOutside() {
		// 从病案管理界面拿到参数TParm
		TParm outsideParm = (TParm) this.getParameter();
		 System.out.println("护士：："+outsideParm);
		if (outsideParm != null) {
			// 按就诊号查询的caseNo
			setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
			// 按病区查询的stationCode
			setStationCode(outsideParm.getValue("INW", "STATION_CODE"));
			// 得到LED对象
			setLedui((LEDUI) outsideParm.getData("INW", "LEDUI"));
			saveFlg = outsideParm.getBoolean("INW", "SAVE_FLG");
            // 术中医嘱注记wanglong add 20140707
            this.setOpeFlg(outsideParm.getBoolean("INW", "OPE_FLG"));
            // 手术科室wanglong add 20140707
            this.setOpDeptCode(outsideParm.getValue("INW", "OP_DEPT_CODE"));
            // 手术申请单号 wanglong add 20140707
            this.setOpBookSeq(outsideParm.getValue("INW", "OPBOOK_SEQ"));
            
            //20151028 wangjc add 补充计费用
            this.setMrNo(outsideParm.getValue("INW", "MR_NO"));
            this.setIpdNo(outsideParm.getValue("INW", "IPD_NO"));
            this.setClpCode_(outsideParm.getValue("INW", "CLNCPATH_CODE"));
            this.setDeptCode(outsideParm.getValue("INW", "DEPT_CODE"));
		}
	}

	/**
	 * 首先得到所有UI的控件对象/注册相应的事件
	 */

	public void myInitControler() {

		// 得到时间控件
		from_Date = (TTextFormat) this.getComponent("from_Date");
		to_Date = (TTextFormat) this.getComponent("to_Date");
		from_Time = (TTextField) this.getComponent("from_Time");
		to_Time = (TTextField) this.getComponent("to_Time");

		// 得到table控件
		mainTable = (TTable) this.getComponent("MAINTABLE");
		
		// 给table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|MAINTABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");

		mainTable.addEventListener(mainTable.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onDateTime");
		// 给上下table注册单击事件监听
		this.callFunction("UI|MAINTABLE|addEventListener", "MAINTABLE->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");
		// 得到查询条件UI的对象
		ord1All = (TRadioButton) this.getComponent("ord1All");
		ord1ST = (TRadioButton) this.getComponent("ord1ST");
		ord1UD = (TRadioButton) this.getComponent("ord1UD");
		ord1DS = (TRadioButton) this.getComponent("ord1DS");
		ord1IG = (TRadioButton) this.getComponent("ord1IG");

		ord2All = (TRadioButton) this.getComponent("ord2All");
		ord2PHA = (TRadioButton) this.getComponent("ord2PHA");
		ord2PL = (TRadioButton) this.getComponent("ord2PL");
		ord2ENT = (TRadioButton) this.getComponent("ord2ENT");

		typeO = (TCheckBox) this.getComponent("typeO");
		typeE = (TCheckBox) this.getComponent("typeE");
		typeI = (TCheckBox) this.getComponent("typeI");
		typeF = (TCheckBox) this.getComponent("typeF");

		typeC = (TCheckBox) this.getComponent("typeC");

		this.StUdLab = (TLabel) this.getComponent("STUD_LAB");
		this.medOrg = (TRadioButton) this.getComponent("medOrg");
		this.medSta = (TRadioButton) this.getComponent("medSta");

        firstDateRadio = (TRadioButton) this.getComponent("firstDateRadio");// add by wanglong 20130626
        secondDateRadio = (TRadioButton) this.getComponent("secondDateRadio");
        
		checkAll = (TRadioButton) this.getComponent("checkAll");
		checkYES = (TRadioButton) this.getComponent("checkYES");
		checkNO = (TRadioButton) this.getComponent("checkNO");
		// 得到全全部执行控件
		exeAll = (TCheckBox) this.getComponent("exeALL");
		// 得到全部打印控件
		printAll = (TCheckBox) this.getComponent("PRINT_ALL");
		// 初始化时间
		initDateTime();
	}

	public void onMasterTableClicked(int row) {
		TParm tableDate = mainTable.getParmValue();
		// add shibl 2012-03-05
		setValue("INW_STATION_CODE", tableDate.getValue("STATION_CODE", row));
		setValue("IPD_NO", tableDate.getValue("IPD_NO", row));
		setValue("MR_NO", tableDate.getValue("MR_NO", row));
		setValue("BED_NO", tableDate.getValue("BED_NO", row));
		setValue("VC_CODE", tableDate.getValue("JZDR", row));

		TParm admParm = new TParm();
		admParm.setData("CASE_NO", tableDate.getValue("CASE_NO", row));
		admParm = ADMTool.getInstance().getADM_INFO(admParm);
		TParm sexParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_PATINFO A WHERE MR_NO ='"
						+ tableDate.getValue("CASE_NO", row) + "'"));
		setValue("PAT_NAME", tableDate.getValue("PAT_NAME", row));
		setValue("SEX", sexParm.getData("SEX_CODE", 0));
		setValue("SERVICE_LEVELIN", admParm.getData("SERVICE_LEVEL", 0));
		setValue("WEIGHT", admParm.getData("WEIGHT", 0));
		setValue("ADM_DATE", admParm.getData("ADM_DATE", 0));
	}

	/**
	 * 初始化时间控件
	 */
	public void initDateTime() {
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// String now = StringTool.getString(date, "yyyyMMddHHmmss");
		// 用今天的00：00初始化起始时间
		from_Date.setValue(date);
		from_Time.setValue("00:00");
		// 用下次摆药时间初始化
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(date); // 根据当前时间拿到最近摆药时间点（最近摆药时间，下次摆药时间）
		String disDateTime = (String) dispenseDttm.get(0);
		to_Date.setValue(disDateTime.substring(0, 4) + "/"
				+ disDateTime.substring(4, 6) + "/"
				+ disDateTime.substring(6, 8));
		to_Time.setValue(disDateTime.substring(8, 10) + ":"
				+ disDateTime.substring(10, 12));
        String dateStr = StringTool.getString(date, "yyyy/MM/dd");
        this.setValue("ORDER_START_DATE", dateStr + " 00:00");// add by wanglong 20130626
        this.setValue("ORDER_END_DATE",
                      disDateTime.substring(0, 4) + "/" + disDateTime.substring(4, 6) + "/"
                              + disDateTime.substring(6, 8) + " " + disDateTime.substring(8, 10)
                              + ":" + disDateTime.substring(10, 12));
	}

	/**
	 * 打印配药确认单
	 */
	public void onDispenseSheet() {
        if (!this.checkYES.isSelected() // 已审核
                || !this.ord2PHA.isSelected()// 药嘱
                || !this.medSta.isSelected()) {// 病区领药
			this.messageBox("只能在已审核,药瞩,智能柜取药的状态下才能打印");
			return;
		}
		mainTable.acceptText();
		Map map = getPhatakeMedNo();
		if(map==null){
			this.messageBox("没有数据");
			return;
		}
		Iterator order = map.values().iterator();
		while (order.hasNext()) {
			String it=(String)order.next();
			TParm inParm = new TParm();
			inParm.setData("TAKEMED_NO", it);
			inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
			TTextFormat station = (TTextFormat) getComponent("INW_STATION_CODE");
			String stationName = station.getText();
			if (StringUtil.isNullString(stationName))
				stationName = "全院";
			String fromDate = StringTool.getString(
					(Timestamp) from_Date.getValue(), "yyyyMMdd");
			String fromTime = (String) from_Time.getValue();
			String fromCheckDate = fromDate + fromTime.substring(0, 2)
					+ fromTime.substring(3);
			String toDate = StringTool.getString(
					(Timestamp) to_Date.getValue(), "yyyyMMdd");
			String toTime = (String) to_Time.getValue();
			String toCheckDate = toDate + toTime.substring(0, 2)
					+ toTime.substring(3);
			inParm.setData("STATION_NAME", stationName);
			inParm.setData("START_DATE",
					StringTool.getTimestamp(fromCheckDate, "yyyyMMddHHmm"));
			inParm.setData("END_DATE",
					StringTool.getTimestamp(toCheckDate, "yyyyMMddHHmm"));
			inParm.setData("IS_STATION", false);
			inParm.setData("DONE",
					Boolean.valueOf(TypeTool.getBoolean(getValue("checkNO"))));//未审核
			String caseNos = getCaseNos();
			inParm.setData("WHERE_1", caseNos);
			String phaDispenseNo = getPhaDispenseNo();
			inParm.setData("WHERE_2", phaDispenseNo);
			inParm.setData("TYPE_T", "临时医嘱/首日量取药单");
			inParm.setData("WHERE_3", " 'ST','F'");
			if ("''".equalsIgnoreCase(caseNos)) {
				messageBox_("没有数据");
				return;
			}
			// 剂型分类
			inParm.setData("WHERE_4", getDoseTypeByWhere());
			// 用法
			inParm.setData("DOSE_TYPE", getDoseTypeText());
			TParm parmData = this.mainTable.getParmValue();
			TParm parmRowData = new TParm();
            for (int i = 0; i < parmData.getCount("PRINT_FLG"); i++) {
                if (StringTool.getBoolean(parmData.getValue("PRINT_FLG", i))) {
                    parmRowData = parmData.getRow(i);
                    break;
                }
            }
            // 调配人员及时间
            inParm.setData("USER_NAME", Operator.getName());// 护士
            inParm.setData("DR_NAME",// 医生
                           getOperatorName(InwForOdiTool.getInstance().queryOrderDrCode(inParm)));// modify by wanglong 20130607
            if ("''".equalsIgnoreCase(phaDispenseNo)) {
                messageBox_("没有数据");
                return;
            } else {
                if (InwForOdiTool.getInstance().existDrugOrder(inParm)) {// modify by wanglong 20130607
                    inParm.setData("CTRLDRUG_FLG", "Y");
                    openPrintWindow("%ROOT%\\config\\prt\\INW\\INWDispenseConfirmList.jhw", inParm,
                                    false);
                }
                if (InwForOdiTool.getInstance().existCommonOrder(inParm)) {
                    inParm.setData("CTRLDRUG_FLG", "N");
                    openPrintWindow("%ROOT%\\config\\prt\\INW\\INWDispenseConfirmList.jhw", inParm,
                                    false);
                }
            }
        }
    }

	/**
	 * 
	 * @return
	 */
	private String getPhaDispenseNo() {
		mainTable.acceptText();
		TParm parm = mainTable.getParmValue();
		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i)))
				phaDispenseNo
						.append("'")
						.append(parm.getValue("ORDER_NO", i)
								+ parm.getValue("ORDER_SEQ", i)).append("',");
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}

	/**
	 * 
	 * @return
	 */
	private Map getPhatakeMedNo() {
		mainTable.acceptText();
		TParm parm = mainTable.getParmValue();
		StringBuffer phaDispenseNo = new StringBuffer();
		Map orderNoMap = new HashMap();
		if (parm.getCount() < 1)
			return orderNoMap;
		int count = parm.getCount();
		String caseNo="";
		String orderSeq="";
		String orderNo="";
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i))) {
				caseNo=parm.getValue("CASE_NO", i);
				orderSeq=parm.getValue("ORDER_SEQ", i);
				orderNo=parm.getValue("ORDER_NO", i);
				String sql="SELECT TAKEMED_NO FROM ODI_DSPNM WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+
				orderNo+"' AND ORDER_SEQ='"+orderSeq+"' AND (DSPN_KIND='F' OR DSPN_KIND='ST')";
				TParm inparm=new TParm(TJDODBTool.getInstance().select(sql));
				if(inparm.getCount()<=0)
					continue;
				if(inparm.getErrCode()<0)
					continue;
				String takeMedNo=inparm.getValue("TAKEMED_NO",0);
				if(takeMedNo.equals(""))
					continue;
				if (orderNoMap.get(takeMedNo) == null)
					orderNoMap.put(takeMedNo,
							takeMedNo);
			}
		return orderNoMap;
	}
    
	/**
	 * 取得PAT table里选中的CASE_NO，为其他SQL拼WHERE用
	 * 
	 * @return
	 */
	public String getCaseNos() {
		mainTable.acceptText();
		TParm parm = this.mainTable.getParmValue();
		StringBuffer caseNos = new StringBuffer();
		if (parm.getCount() < 1)
			return "''";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i)))
				caseNos.append("'").append(parm.getValue("CASE_NO", i))
						.append("',");

		if (caseNos.length() < 1) {
			return "''";
		} else {
			caseNos.deleteCharAt(caseNos.length() - 1);
			return caseNos.toString();
		}
	}

	/**
	 * 取得选择的剂型分类
	 * 
	 * @return String
	 */
	private String getDoseTypeByWhere() {
		String getDoseType = "";
		List list = new ArrayList();
		if (this.typeO.isSelected()) {//口服
			list.add("O");
		}
		if (this.typeE.isSelected()) {//外用
			list.add("E");
		}
		if (this.typeI.isSelected()) {//针剂
			list.add("I");
		}
		if (this.typeF.isSelected()) {//点滴
			list.add("F");
		}
		if (list == null || list.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
		}
		return getDoseType;
	}

	/**
	 * 取得选择的剂型分类用于报表显示
	 * 
	 * @return String
	 */
	private String getDoseTypeText() {
		String getDoseType = "";
		if ("N".equals(this.getValueString("typeO"))
				|| "N".equals(this.getValueString("typeE"))
				|| "N".equals(this.getValueString("typeI"))
				|| "N".equals(this.getValueString("typeF"))) {
			List list = new ArrayList();
			if ("Y".equals(this.getValueString("typeO"))) {
				list.add("口服");
			}
			if ("Y".equals(this.getValueString("typeE"))) {
				list.add("外用");
			}
			if ("Y".equals(this.getValueString("typeI"))) {
				list.add("针剂");
			}
			if ("Y".equals(this.getValueString("typeF"))) {
				list.add("点滴");
			}

			if (list == null || list.size() == 0) {
				return "";
			} else {
				for (int i = 0; i < list.size(); i++) {
					getDoseType = getDoseType + list.get(i) + " ,";
				}
				getDoseType = "用法: "
						+ getDoseType.substring(0, getDoseType.length() - 1);
			}
		} else {
			getDoseType = "用法: 全部";
		}
		return getDoseType;

	}

	/**
	 * 修改执行日期和时间（为了抢救医嘱）
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int (NS_CHECK_DATE_DAY;NS_CHECK_DATE_TIME)
	 */
	public void onDateTime(TTableNode node) {
		mainTable.acceptText();
		int col = node.getColumn();
		String colName = getColName(col, mainTable);
//		int row = mainTable.getSelectedRow();
	    int row = node.getRow();//wanglong modify 20150128
		TParm rowParm = mainTable.getParmValue().getRow(row);
		Map map = new HashMap();
		if ("NS_CHECK_DATE".equals(colName)) {
			Timestamp temp = (Timestamp) node.getValue();
			Timestamp date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				Timestamp tempT = (Timestamp) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO")
						+ rowParm.getValue("RX_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = mainTable.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = mainTable.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO")
							+ parm.getValue("RX_KIND")
							+ parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						mainTable.setItem(i, "NS_CHECK_DATE", tempT);
					}
				}
			}
		}
		if ("NS_CHECK_DATE_TIME".equals(colName)) {
			String temp = (String) node.getValue();
			if (temp.length() > 5||temp.length() < 4) {  //chenxi  modify 20130325 审核时间省去冒号
				this.messageBox("时间长度错误！");
				node.setValue(node.getOldValue());
				return;
			} else {
				String execDate = ""
						+ mainTable.getParmValue().getValue("NS_CHECK_DATE",
								row);
				String execTime = "" + temp;
				if (temp.length() == 5) {
				Timestamp checkDateTime = StringTool.getTimestamp(
						execDate.substring(0, 10).replaceAll("/", "")
								.replaceAll("-", "")
								+ " " + execTime.substring(0, 5) + ":00",
						"yyyyMMdd HH:mm:ss");
				if (checkDateTime == null) {
					this.messageBox("时间格式错误！");
					node.setValue(node.getOldValue());
					return;
				}
				Pattern pattern = Pattern
						.compile("((0[0-9])|(1[0-9])|(2[0-3])):([0-5][0-9])");
				if (!pattern.matcher(execTime).matches()) {
					this.messageBox("时间数值错误！");
					node.setValue(node.getOldValue());
					return;
				}
			}
				else if (temp.length() == 4) {
					Timestamp checkDateTime = StringTool.getTimestamp(execDate
							.substring(0, 10).replaceAll("/", "").replaceAll(
									"-", "")
							+ " " + execTime.substring(0, 4) + "00",
							"yyyyMMdd HHmmss");
					if (checkDateTime == null) {
						this.messageBox("时间格式错误！");
						node.setValue(node.getOldValue());
						return;
					}
					Pattern pattern = Pattern
							.compile("((0[0-9])|(1[0-9])|(2[0-3]))([0-5][0-9])");
					if (!pattern.matcher(execTime).matches()) {
						this.messageBox("时间数值错误！");
						node.setValue(node.getOldValue());
						return;
					}
				}
				}
			String date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				String tempT = (String) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO")
						+ rowParm.getValue("RX_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = mainTable.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = mainTable.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO")
							+ parm.getValue("RX_KIND")
							+ parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						mainTable.setItem(i, "NS_CHECK_DATE_TIME", tempT);
					}
				}
			}
		}

	}

	/**
	 * 是否是连结医嘱
	 * 
	 * @param linkOrder
	 *            TParm
	 * @return boolean
	 */
	public boolean isLinkOrder(TParm linkOrder) {
		boolean falg = false;
		if (linkOrder.getInt("LINK_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 根据列数得到列名
	 * 
	 * @param i
	 *            int
	 * @return String
	 */
	public String getColName(int i, TTable tbl) {
		String colName = "";
		colName = tbl.getParmMap(i);
		return colName;
	}

	/**
	 * 查询函数 1)查询 2)当有数据的时候改变相关的控件状态
	 */
	public void onQuery() {
		// 初始化当前table并且有数据
		if (initTable())
			changeStatus();
		if (checkYES.isSelected()) {
			exeAll.setSelected(true);
		} else if (checkNO.isSelected()) {
			exeAll.setSelected(false);
		}
		onPrtAll();
	}

	/**
	 * 当查询有数据之后改变某些控件的状态
	 */
	public void changeStatus() {
		exeAll.setEnabled(true);
	}

	/**
	 * 初始化Table
	 * 
	 * @return boolean
	 */
	public boolean initTable() {
		TParm selParm = new TParm();
		selParm = getQueryParm();
		TParm query = InwForOdiTool.getInstance().selectOdiOrder(selParm);
		// filterDrugByDoseCode(query);
		if (query.getCount() <= 0) {
			mainTable.setParmValue(query);
			return false;
		}
		TParm parm = new TParm();
		int count = 0;
		map=new HashMap();
		for (int i = 0; i < query.getCount(); i++) {
			//得到系统的时间与停用时间比较->是否显示停用时间，yanjing 201311CHECK_INDEX
			String sysDate = SystemTool.getInstance().getDate().toString().substring(0, 10).replace("-", "").replace("/", "");
			if(!query.getValue("DC_DATE",i).equals(null)&&!"".equals(query.getValue("DC_DATE",i))){
				String dc_date = query.getValue("DC_DATE", i).toString().substring(0, 10).replace("-", "").replace("/", "");
				if(dc_date.compareTo(sysDate)>0){
					query.setData("DC_DATE",i, null);
				}
			}
			
			query.addData("PRINT_FLG", false);
			// 和已执行标记一致
			query.addData("EXE_FLG", checkYES.isSelected());
			Timestamp exeDate = (Timestamp) query.getData("NS_CHECK_DATE", i);
			Timestamp exeDcDate = (Timestamp) query.getData("DC_NS_CHECK_DATE",
					i);
			if (exeDate != null) {
				String day = StringTool.getString(exeDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDate, "HH:mm:ss")
						.substring(0, 5);
				query.addData("NS_CHECK_DATE", exeDate);
				query.addData("NS_CHECK_DATE_TIME", time);
			} else {
				query.addData("NS_CHECK_DATE", null);
				query.addData("NS_CHECK_DATE_TIME", null);
			}
			if (exeDcDate != null) {
				String day = StringTool.getString(exeDcDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDcDate, "HH:mm:ss");
				query.addData("DC_NS_CHECK_DATE_DAY", day);
				query.addData("DC_NS_CHECK_DATE_TIME", time);
			} else {
				query.addData("DC_NS_CHECK_DATE_DAY", null);
				query.addData("DC_NS_CHECK_DATE_TIME", null);
			}
			// 药品过滤精麻药 SHIBL 20120821 ADD
			// 药品
			if (this.ord2PHA.isSelected()) {
				// 精麻
				if (!this.typeC.isSelected()
						&& (query.getValue("CTRL_FLG", i).equals("N") || query
								.getValue("CTRL_FLG", i).equals(""))) {
					// 已取值表格ODI_ORDER数值
					if (!query.getValue("TAKEMED_ORG", i).equals("")
							&& this.checkYES.isSelected()) {
						if (this.medSta.isSelected()
								&& (query.getValue("TAKEMED_ORG", i)
										.equals("1")))
							parm.addRowData(query, i);
						else if (this.medOrg.isSelected()
								&& (query.getValue("TAKEMED_ORG", i)
										.equals("2")))
							parm.addRowData(query, i);
					} else {
						// 默认
						query.setData("TAKEMED_ORG", i,
								getTakeMedOrg(query.getRow(i)));
						parm.addRowData(query, i);
					}
				}
				// 非精麻
				else if (this.typeC.isSelected()) {
					if (!query.getValue("TAKEMED_ORG", i).equals("")
							&& this.checkYES.isSelected()) {
						if (this.medSta.isSelected()
								&& (query.getValue("TAKEMED_ORG", i)
										.equals("1")))
							parm.addRowData(query, i);
						else if (this.medOrg.isSelected()
								&& (query.getValue("TAKEMED_ORG", i)
										.equals("2")))
							parm.addRowData(query, i);
					} else {
						// 默认
						query.setData("TAKEMED_ORG", i,
								getTakeMedOrg(query.getRow(i)));
						parm.addRowData(query, i);
					}
				}
				this.mainTable.setLockCell(count, "TAKEMED_ORG", false);
				count++;
			}// 非药品情况或全部
			else {
				if (query.getValue("CAT1_TYPE", i).equals("PHA")) {
					query.setData("TAKEMED_ORG", i,
							getTakeMedOrg(query.getRow(i)));
				}
				if (!query.getValue("CAT1_TYPE", i).equals("PHA")) {
					this.mainTable.setLockCell(count, "TAKEMED_ORG", true);
				} else {
					this.mainTable.setLockCell(count, "TAKEMED_ORG", false);
				}
				parm.addRowData(query, i);
				count++;
			}
		}
		parm.setCount(count);
		//System.out.println("InwOrderCheckQueryParm: " + parm);   //add by wukai 
		mainTable.setParmValue(parm);
		setColor();
		return true;
	}

	/**
	 * 得到取药来源注记
	 * 
	 * @param parm
	 * @return
	 */
	public String getTakeMedOrg(TParm parm) {
		// 判断库存 20121115 shibl add
		// 针对临时/首日量特殊处理如下
		// 系统判断该病人所在病区若有该药品足够的库存,则从病区智能柜取药(SET TAKEMED_ORG= “1”),
		// 若无足够的药品库存则从住院药房领药(SET TAKEMED_ORG= “2”)
		String MedOrg = "2";
		// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 START
        if (parm.getValue("CAT1_TYPE").equals("PHA")) {
        	TParm inparm=new TParm();
        	inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        	inparm.setData("REGION_CODE", Operator.getRegion());
        	inparm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        	TParm result = new TParm();
        	
        	if (Operator.getSpcFlg().equals("Y")) {//20130423  shibl add  国药开关
        		//modify by wanglong 20130115
            	result = TIOM_AppServer.executeAction(
        				"action.inw.InwOrderCheckAction", "getIndQty", inparm);
            	if(result.getErrCode()<0&&map.get("errorcode")==null){
            		this.messageBox(result.getErrText());
            		map.put("errorcode",result.getErrCode());
            		return MedOrg;
            	}
        	} else {
        		// 查询该药品在指定病区的当前库存
        		result = InwOrderCheckTool.getInstance().getIndQty(inparm);
        		
				if (result.getErrCode() < 0) {
					this.messageBox(result.getErrText());
					map.put("errorcode", result.getErrCode());
					return MedOrg;
				}
        	}
        	// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 END
            
//            System.out.println(rDto.getOrderCode()+"====dtoReturnList.get(0).getQty()======"+rDto.getQty());
            if (result.getDouble("QTY") >= parm.getDouble("DOSAGE_QTY")
                    && parm.getValue("RX_KIND").equals("ST")//只针对临时医嘱
                    &&StringUtil.isNullString(parm.getValue("LINK_NO"))// 组合医嘱不在病区取药  //add by wanglong 20130619
                    ) {
            //modify end
//			if (InwForOdiTool.getInstance().inspectIndStock(
//					parm.getValue("STATION_CODE"), parm.getValue("ORDER_CODE"),
//					parm.getDouble("DOSAGE_QTY"))
//					&& (parm.getValue("RX_KIND").equals("ST") || parm.getValue(
//							"RX_KIND").equals("UD"))) {
				MedOrg = "1";
			} else {
				MedOrg = "2";
			}
		}
        
		return MedOrg;
	}

	/**
	 * 获得界面上的所有查询参数
	 * 
	 * @return TParm
	 */
	public TParm getQueryParm() {
		// 获得界面上的参数
		TParm result = new TParm();
		// ===============pangben modify 20110512 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			result.setData("REGION_CODE", Operator.getRegion());
		// ===============pangben modify 20110512 stop

		// 取得医嘱类别
		if (ord1All.isSelected()) {
			// 所有
			// add by wangb 2015/10/28 护士审核只能看到临时、长期以及出院带药 START
			result.setData("INW_CHECK_RX_KIND", "ST,UD,DS");
			// add by wangb 2015/10/28 护士审核只能看到临时、长期以及出院带药 END
		} else if (ord1ST.isSelected()) {
			// 临时
			//20151105 wangjc add start
			if(opeFlg){
				result.setData("RX_KIND", "OP");
			}else{
				result.setData("RX_KIND", "ST");
				
			}
		} else if (ord1UD.isSelected()) {
			// 长期
			result.setData("RX_KIND", "UD");
		} else if (ord1DS.isSelected()) {
			// 出院带药
			result.setData("RX_KIND", "DS");
		} else if (ord1IG.isSelected()) {
			// 住院草药
			result.setData("RX_KIND", "IG");
		}

		// 医嘱种类
		if (ord2All.isSelected()) {
			// 所有
		} else if (ord2PHA.isSelected()) {
			// 药嘱
			result.setData("CAT1_TYPE_PHA", "PHA");
			List<String> doseType = new ArrayList<String>();
			if (typeO.isSelected()) { // 口服
				doseType.add("O");
			}
			if (typeE.isSelected()) { // 外用
				doseType.add("E");
			}
			if (typeI.isSelected()) { // 针剂
				doseType.add("I");
			}
			if (typeF.isSelected()) { // 点滴
				doseType.add("F");
			}
			int parmCount = doseType.size();
			if (parmCount == 0) {
				result.setData("DOSE_TYPES1", "");
				result.setData("DOSE_TYPE1", "Y");
			} else if (parmCount == 1) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPE1", "Y");
			} else if (parmCount == 2) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPE2", "Y");
			} else if (parmCount == 3) {
				result.setData("DOSE_TYPES3", "Y");
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPE3", doseType.get(2));
			} else if (parmCount == 4) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPES3", doseType.get(2));
				result.setData("DOSE_TYPES4", doseType.get(3));
				result.setData("DOSE_TYPE4", "Y");
			}
			// // 判断是否勾选了麻精
			// if (this.typeC.isSelected()) {
			// result.setData("CTRLDRUGCLASS_CODE", getPhaCtrlCode());
			// }
		} else if (ord2PL.isSelected()) {
			// 处置(检验检查)
			result.setData("CAT1_TYPE_TRT", "PHA");
		} else if (((TRadioButton) this.getComponent("ord2ENT")).isSelected()) {
			// 嘱托
			result.setData("CAT1_TYPE_ENT", "PHA");
		}
		// 审核状态
		if (checkAll.isSelected()) {
			// 所有
		} else if (checkYES.isSelected()) {
			// 已审核
            if (firstDateRadio.isSelected()) {// add by wanglong 20130626
                result.setData("CHECKTYPE_YES", "Y");
                String fromDate = StringTool.getString(
                        (Timestamp) from_Date.getValue(), "yyyyMMdd");
                String fromTime = (String) from_Time.getValue();
                String fromCheckDate = fromDate + fromTime.substring(0, 2)
                        + fromTime.substring(3);
                String toDate = StringTool.getString(
                        (Timestamp) to_Date.getValue(), "yyyyMMdd");
                String toTime = (String) to_Time.getValue();
                String toCheckDate = toDate + toTime.substring(0, 2)
                        + toTime.substring(3);
                result.setData("fromCheckDate", fromCheckDate);
                result.setData("toCheckDate", toCheckDate);
                result.setData("NS_CHECK_DATE", fromCheckDate); 
            } else {// add by wanglong 20130626
                result.setData("CHECKTYPE_YES", "Y");
                result.setData("fromOrderDate", (Timestamp) this.getValue("ORDER_START_DATE"));
                result.setData("toOrderDate", (Timestamp) this.getValue("ORDER_END_DATE"));
                result.setData("ORDER_DATE", (Timestamp) this.getValue("ORDER_START_DATE"));
            }
		} else if (checkNO.isSelected()) {
			// 未审核
			result.setData("CHECKTYPE_NO", "Y");
//			if(opeFlg){
//				result.setData("ROUTE_CODE", "CA");//20151106 WANGJC
//			}
		}
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
            result.setData("DEPT_CODE", ((TParm) this.getParameter()).getValue("INW", "DEPT_CODE"));
            result.setData("VS_DR_CODE",
                           ((TParm) this.getParameter()).getValue("INW", "VS_DR_CODE"));
            result.setData("OPE_YES", "Y");//显示手术医嘱
        } else {
            result.setData("OPE_NO", "Y");// 过滤掉手术医嘱
        }
		if (setQueryParm(result)) {
			clearTop();
			return result;
		}
		// 加如看诊号
		if (caseNo != null && !"".equals(caseNo.trim())
				&& !"null".equals(caseNo)) {

			result.setData("CASE_NO", caseNo);
		} else {
			// 为空的时候
		}
		// 加入病区号
		if (stationCode != null && !"".equals(stationCode.trim())
				&& !"null".equals(stationCode)) {
			result.setData("STATION_CODE", stationCode);
		} else {
			// 为空的时候
		}
        // 加入科室
        if (!this.getValueString("INW_DEPT_CODE").equals("")) {
            result.setData("DEPT_CODE", getValueString("INW_DEPT_CODE"));
        } else {
            // 为空的时候
        }
        // 加入经治医师
        if (!this.getValueString("INW_VC_CODE").equals("")) {
            result.setData("VS_DR_CODE", getValueString("INW_VC_CODE"));
        } else {
            // 为空的时候
        }
		// System.out.println("-----------------"+result);
		return result;
	}

	/**
	 * 判断管制药品等级
	 * 
	 * @param orderCode
	 * @return
	 */
	private boolean getPhaCtrlCode(String orderCode) {
		boolean CtrlStr = false;
		String sql = " SELECT A.CTRLDRUGCLASS_CODE,B.CTRL_FLG FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
				+ " WHERE A.CTRLDRUGCLASS_CODE=B.CTRLDRUGCLASS_CODE AND ORDER_CODE = '"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0)
			CtrlStr = parm.getBoolean("CTRL_FLG", 0);
		return CtrlStr;
	}

	public boolean setQueryParm(TParm parm) {
		if (!saveFlg)
			return false;
		// shibl 2012-03-05
		if (getValueString("INW_STATION_CODE").length() != 0)
			parm.setData("STATION_CODE", getValue("INW_STATION_CODE"));
		if (getValueString("IPD_NO").length() != 0)
			parm.setData("IPD_NO", getValue("IPD_NO"));
		if (getValueString("MR_NO").length() != 0)
			parm.setData("MR_NO", getValue("MR_NO"));
		if (getValueString("INW_VC_CODE").length() != 0)
			parm.setData("VS_DR_CODE", getValue("INW_VC_CODE"));
		// shibl 2012-03-05
		if (getValueString("INW_DEPT_CODE").length() != 0)
			parm.setData("DEPT_CODE", getValue("INW_DEPT_CODE"));
		parm.setData("DS_DATE_FLG", false);
		return true;
	}

	public void clearTop() {
		if (getValueString("IPD_NO").length() != 0)
			return;
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("BED_NO", "");
	}

	/**
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		// 拿到table对象
		TTable table = (TTable) this.getComponent(tableName);
		// 获得选中行的TParm
		TParm action = mainTable.getParmValue().getRow(
				mainTable.getSelectedRow());
		if ("LIS".equals(action.getValue("CAT1_TYPE"))
				|| "RIS".equals(action.getValue("CAT1_TYPE"))) {
			table.setPopupMenuSyntax("显示集合医嘱细项,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
		}
	}

	/**
	 * 打开集合医嘱细项查询
	 */
	public void openRigthPopMenu() {
		// 获得选中行的TParm
		TParm action = mainTable.getParmValue().getRow(
				mainTable.getSelectedRow());
		int groupNo = action.getInt("ORDERSET_GROUP_NO");
		String orderCode = action.getValue("ORDER_CODE");
		String caseNo = action.getValue("CASE_NO");
		TParm parm = getOrderSetDetails(caseNo, groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String caseNo, int groupNo,
			String orderSetCode) {
		TParm result = new TParm();
		if (groupNo < 0) {
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			return result;
		}
		// ===========pangben modify 20110516 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		String selSetOrder = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo
				+ "' AND ORDERSET_GROUP_NO<>0" + region;
		// ===========pangben modify 20110516 stop
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSetOrder));
		int count = parm.getCount();
		if (count < 0) {
			return result;
		}
		String tempCode;
		int tempNo;
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION",
						parm.getValue("SPECIFICATION", i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm ownPriceParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// 计算总价格
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("MEDI_QTY", i);
				result.addData("OWN_PRICE",
						ownPriceParm.getDouble("OWN_PRICE", 0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE",
						parm.getValue("EXEC_DEPT_CODE", i));
				result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	// 全部执行
	public void onExe() {

		boolean nowFlag = exeAll.isSelected();
		// 当全部执行的时候设置一次时间
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		String optName = Operator.getName();

		// 得到行数
		int ordCount = mainTable.getRowCount();
//		System.out.println("++++++ordCount"+ordCount);
		for (int i = 0; i < ordCount; i++) {
			// 循环取消对勾
			if (nowFlag) {

				selection(i, optName, chackTime);
			} else { // 取消审核

				unselection(i);
			}
		}

	}

	/**
	 * 选择
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void selection(int i, String optName, Timestamp chackTime) {
		// mainTable.setValueAt(true, i, 13);
		this.mainTable.setItem(i, "EXE_FLG", "Y");
		TParm parm = mainTable.getParmValue();
		if (checkNO.isSelected()
				&& parm.getValue("NS_CHECK_CODE", i).length() == 0) {
			mainTable.setItem(i, "NS_CHECK_CODE", optName);
			mainTable.setItem(i, "NS_CHECK_DATE",
					StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable
					.setItem(i, "NS_CHECK_DATE_TIME",
							StringTool.getString(chackTime, "HH:mm:ss")
									.substring(0, 5));
			// mainTable.setValueAt(chackTime,
			// i, 16);
		}
		if (checkYES.isSelected() || checkAll.isSelected()) {
			mainTable.setItem(i, "NS_CHECK_CODE", optName);
			mainTable.setItem(i, "NS_CHECK_DATE",
					StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable
					.setItem(i, "NS_CHECK_DATE_TIME",
							StringTool.getString(chackTime, "HH:mm:ss")
									.substring(0, 5));
			// mainTable.setValueAt(chackTime,
			// i, 16);
		}
		// DC时间，检验是否有DC 停用日期
		Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16);//modify by yangjj 20150612 12改13 //modify by wukai 20160523 13改15//modify by machao 20170120 15改16
		if (dcDate != null && !"".equals(dcDate)) {
			mainTable.setItem(i, "DC_NS_CHECK_CODE", optName);
			mainTable.setItem(i, "DC_NS_CHECK_DATE_DAY",
					StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable.setItem(i, "DC_NS_CHECK_DATE_TIME",
					StringTool.getString(chackTime, "HH:mm:ss"));

		}
	}

	/**
	 * 取消选择
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void unselection(int i) {
		// mainTable.setValueAt(false, i, 13);
		this.mainTable.setItem(i, "EXE_FLG", "N");
		TParm parm = mainTable.getParmValue();
		// SHIBL
		// if (checkNO.isSelected()
		// && parm.getValue("NS_CHECK_CODE", name).length() == 0) {
		// this.messageBox_("213");
		// mainTable.setItem(i, "NS_CHECK_CODE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		// // mainTable.setValueAt("", i, 16);
		// }
		// if (checkYES.isSelected() || checkAll.isSelected()) {
		// mainTable.setItem(i, "NS_CHECK_CODE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		// // mainTable.setValueAt("", i, 16);
		// }
		// DC时间，检验是否有DC
		Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16);//modify by yangjj 20150612 12改13//modify by wukai 20160601 13改15//modify by machao 20170120 15改16
		if (dcDate != null && !"".equals(dcDate)) {
			mainTable.setItem(i, "DC_NS_CHECK_CODE", "");
			mainTable.setItem(i, "DC_NS_CHECK_DATE_DAY", "");
			mainTable.setItem(i, "DC_NS_CHECK_DATE_TIME", "");

		} else {
			mainTable.setItem(i, "NS_CHECK_CODE", "");
			mainTable.setItem(i, "NS_CHECK_DATE", "");
			mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		}

	}

	/**
	 * table上的checkBox注册监听
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		// 当全部执行的时候设置一次时间
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		// 获得点击的table对象
		TTable table = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		table.acceptText();
		TParm tblParm = table.getParmValue();

		// 获得选中的列/行
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		// 获得table上的行数
		int rowcount = table.getRowCount();
		// 如果选中的是第12列就激发执行动作--执行
		String columnName = table.getParmMap(col);
		if (columnName.equals("EXE_FLG")) {
			boolean exeFlg;
			// 获得点击时的值
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			// 勾选时
			if (exeFlg) {
				// 勾选行动作
				selection(row, Operator.getName(), chackTime);
				// 得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
				String rxKind = (String) tblParm.getValue("RX_KIND", row);
				// -----------------------处理连结医嘱start------------------------
				// 找到相同的连接号
				String linkNo = (String) table.getValueAt(row, 4);//modify by yangjj 20150612 3改4
				String orderNo = (String) tblParm.getValue("ORDER_NO", row);
				String linkMain = (String) tblParm
						.getValue("LINKMAIN_FLG", row);
			    if (TCM_Transform.getInt(linkNo) > 0) {//wanglong modify 20140731 解决对于连嘱号超过1的失效问题
					for (int i = 0; i < rowcount; i++) {
						// 除了当前点击的行号以外的
						if (i != row
								&& linkNo.equals((String) table
										.getValueAt(i, 4))//modify by yangjj 20150612 3改4
								&& rxKind.equals((String) tblParm.getValue(
										"RX_KIND", i))
								&& orderNo.equals((String) tblParm.getValue(
										"ORDER_NO", i))) {

							selection(i, Operator.getName(), chackTime);
						}
					}
				}
				// -------------------------处理中草药start-----------------------
				if ("IG".equals(rxKind)) {
					// 中草药的时候以RX_NO(处方签)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// 记录其他的RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// 除了当前点击的行号以外的
						if (rxNoTemp.equals(rxNo)) {
							selection(i, Operator.getName(), chackTime);
						}
					}
				}
			} else { // 取消时
						// 勾选行动作
				unselection(row);
				// 得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
				String rxKind = (String) tblParm.getValue("RX_KIND", row);
				// -----------------------处理连结医嘱start----------------------------
				// 找到相同的连接号
				String linkNo = (String) table.getValueAt(row, 4);//modify by yangjj 20150612 3改4
				String orderNo = (String) tblParm.getValue("ORDER_NO", row);
				String linkMain = (String) tblParm
						.getValue("LINKMAIN_FLG", row);
			    if (TCM_Transform.getInt(linkNo) > 0) {//wanglong modify 20140731
					for (int i = 0; i < rowcount; i++) {
						// 除了当前点击的行号以外的
						if (i != row
								&& linkNo.equals((String) table
										.getValueAt(i, 4))//modify by yangjj 20150612 3改4
								&& rxKind.equals((String) tblParm.getValue(
										"RX_KIND", i))
								&& orderNo.equals((String) tblParm.getValue(
										"ORDER_NO", i))) {

							unselection(i);
						}
					}
				}
				// -------------------------处理中草药start-----------------------
				if ("IG".equals(rxKind)) {
					// 中草药的时候以RX_NO(处方签)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// 记录其他的RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// 除了当前点击的行号以外的
						if (rxNoTemp.equals(rxNo)) {
							unselection(i);
						}
					}
				}
			}
			// -----------------------end----------------------------------------
		}
	}

	/**
	 * 清空动作
	 */
    public void onClear() {
        // 清空
        if (!isOpeFlg()) {//术中医嘱 wanglong add 20140707
            this.clearValue("IPD_NO;patName;AGE;SUMFEE;BHFEE");
        }
        // 恢复状态
        firstDateRadio.setSelected(true); // 时间类别 add by wanglong 20130626
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
            this.callFunction("UI|ord1ST|setSelected", true);// 选中“临时”单选框
        } else {
            ord1All.setSelected(true); // 医嘱类别
        }
        ord2All.setSelected(true); // 医嘱种类
        checkNO.setSelected(true); // 审核状态

        typeO.setEnabled(false);
        typeE.setEnabled(false);
        typeI.setEnabled(false);
        typeF.setEnabled(false);
        typeC.setEnabled(false);

        onRemoveTbl();
        if (!isOpeFlg()) {// 术中医嘱 wanglong add 20140707
            clearQueryCondition();
        }
    }

	public void clearQueryCondition() {
		setValue("INW_DEPT_CODE", "");
		setValue("BED_NO", "");
		setValue("IPD_NO", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("INW_VC_CODE", "");
		setValue("ADM_DATE", "");
		setValue("TOTAL_AMT", "");
		setValue("PAY_INS", "");
		setValue("YJJ_PRICE", "");
		setValue("GREED_PRICE", "");
		setValue("YJYE_PRICE", "");
		setValue("PRESON_NUM", "");
	}

	/**
	 * 保存动作
	 */
	public boolean onSave() {
		// 保存完之后清空选勾
		exeAll.setSelected(false);
		printAll.setSelected(false);

		// 是否有数据被选择
		if (checkConsistSel()) {
			this.messageBox("无需要保存数据！");
			return false;
		}

		// 密码判断
		if (!checkPW()) {
			return false;
		}
		
		// 立刻接受值的改变
		mainTable.acceptText();
		
		// 调用保存
		if (checkNO.isSelected()) {
			if (!onCheck()) {
				this.messageBox("E0001");
				// 查询
				onQuery();
				return false;
			}
			// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 START
//			if(Operator.getSpcFlg().equals("Y"))
			// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 END
			this.onSendGYPha("A","1");
		} // 如果审核被选择（说明保存时是--取消审核），需要验证是否有执行的
		else {
			if (!onUndoCk()) {
				this.messageBox("E0001");
				onQuery();
				return false;
			}
			// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 START
//			if(Operator.getSpcFlg().equals("Y"))
			// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 END
			this.onSendGYPha("D","1");
		}
		// 保存成功之后删除LED的消息
		TParm caseNoParm = new TParm();
		caseNoParm.setData("CASE_NO", caseNo);
		if (this.getLedui() != null) {
			this.getLedui().removeMessage(caseNoParm);
		}

		this.messageBox("P0001");
        // 发送HL7信息
		// modify by wangb 2015/07/07 只有术中医嘱才在审核时发送消息
        if (sendHL7Parm.getCount("CASE_NO") > 0 && isOpeFlg()) {//wanglong add 20150520
            sendHL7Mes();
        }
		// 保存后再执行一边查询
		onQuery();

		return true;
	}

	/**
	 * 检测是否有数据被选中
	 * 
	 * @return boolean
	 */
	private boolean checkConsistSel() {
		int count = mainTable.getRowCount();
		if (count == 0)
			return true;
		for (int i = 0; i < count; i++) {
			if (checkNO.isSelected()
					&& TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX)))//modify by yangjj 20150612 15改16   modify by wukai 20160530 16改CHECK_INDEX
				return false;
			else if (checkYES.isSelected()
					&& !TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX)))//modify by yangjj 20150612 15改16  modify by wukai 20160530 16改CHECK_INDEX
				return false;
		}
		return true;
	}

	public boolean onUndoCk() {
		if (checkPHACheck())
			return false;
		// 拿到所有挑勾--展开人的caseNo
		TParm undoData = new TParm();
		TParm tablValue = mainTable.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = mainTable.getRowCount();
		
		//add by yangjj 20150616
		TParm p = new TParm();
		TParm xmlParm = new TParm();// 大屏医嘱消息
		// int selRows = 0;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) tablValue.getData("CASE_NO", i);
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			// 取药注记
			String takeMedOrg = (String) tablValue.getData("TAKEMED_ORG", i);
			// modify by wangb 2016/07/28 护士审核表格中增加输液泵列引发列判断错误 由16改为CHECK_INDEX_18
			if (!TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))) {//modify by yangjj 20150612 15改16
				
				//add by yangjj 20150616
				p.addRowData(tablValue, i);
				
				undoData.addData("CASE_NO", caseNo);
				undoData.addData("ORDER_NO", orderNo);
				undoData.addData("ORDER_SEQ", orderSeq);
				undoData.addData("OPT_USER", Operator.getID());
				undoData.addData("OPT_DATE", now);
				undoData.addData("OPT_TERM", Operator.getIP());
				// undoData.addData("TAKEMED_ORG", takeMedOrg);
				if (tablValue.getValue("ORDER_CODE", i).equals(
						tablValue.getValue("ORDERSET_CODE", i)))
					getSetOrder(tablValue.getValue("CASE_NO", i),
							tablValue.getValue("ORDER_NO", i),
							tablValue.getValue("ORDER_CODE", i),
							tablValue.getValue("ORDERSET_GROUP_NO", i), now,
							undoData);
				
				// add by wangb 2017/5/2 大屏接口消息 START
				xmlParm.addData("MrNo", tablValue.getValue("MR_NO", i));
				xmlParm.addData("CaseNo", tablValue.getValue("CASE_NO", i));
				xmlParm.addData("PatName", tablValue.getValue("PAT_NAME", i));
				xmlParm.addData("OrderType", "");
				xmlParm.addData("OrderCode", tablValue.getValue("ORDER_CODE", i));
				xmlParm.addData("OrderDesc", tablValue.getValue("ORDER_DESC_AND_SPECIFICATION", i));
				xmlParm.addData("DrNote", tablValue.getValue("DR_NOTE", i));
				xmlParm.addData("OrderDate", StringTool.getString(tablValue
						.getTimestamp("EFF_DATE", i), "yyyy-MM-dd HH:mm:ss"));
				xmlParm.addData("DcDate", tablValue.getTimestamp("DC_DATE", i));
				xmlParm.addData("CancelFlg", "Y");
				xmlParm.addData("OrderNo", orderNo + orderSeq);
				xmlParm.addData("OrderSeq", orderSeq);
				// add by wangb 2017/5/2 大屏接口消息 END
				
				// selRows++;
                if ("LIS".equals(tablValue.getValue("CAT1_TYPE", i))
                        || "RIS".equals(tablValue.getValue("CAT1_TYPE", i))) {//wanglong add 20150520
                    if (Hl7Communications.getInstance().IsExeOrder(tablValue.getRow(i), "I")) {
                        String orderDate =
                                tablValue.getValue("ORDER_DATE", i).substring(0, 19)
                                        .replaceAll("-", "/");
                        this.messageBox(tablValue.getValue("ORDER_DESC", i) + "已经执行，不能取消！\n 下达时间:"
                                + orderDate);
                        return false;
                    }
                    sendHL7Parm.addData("CASE_NO", caseNo);
                    sendHL7Parm.addData("ORDER_NO", orderNo);
                    sendHL7Parm.addData("ORDER_SEQ", TCM_Transform.getInt(orderSeq));
                    flg = 1;
                }
//               //取消审核  判断药房发药  不可取消 machao 2017/12/11 start
//    			if("N".equals(tablValue.getValue("EXE_FLG", i))){
//    				String phaCode = tablValue.getData("PHA_CHECK_CODE", i)+"";
//    				String phaDate = tablValue.getData("PHA_CHECK_DATE", i)+"";
//    				String orderDesc = tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i)+"";
//    				if(!phaCode.equals("null") && !phaDate.equals("null")){//都不为空的话 说明药房已经发药
//    					this.messageBox(orderDesc+":\r\n 药房已经发药,不可取消审核!");
//    					return false;
//    				}
//    			}
//    			//end 
                
                // add by wangqing 20180104 start
                // 泰心#6073：住院护士站针对药嘱，取消审核时，检验药房是否审核，变更为检验药房是否调配，去掉强制管控。并且增加提示框：药品：XXX已完成调配，请注意退药
                if("N".equals(tablValue.getValue("EXE_FLG", i)) && tablValue.getValue("CAT1_TYPE", i).equals("PHA")){
                	String orderDesc = tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i)+"";
                	TParm parm = new TParm();
                	parm.setData("CASE_NO", tablValue.getValue("CASE_NO", i));
                	parm.setData("ORDER_NO", tablValue.getValue("ORDER_NO", i));
                	parm.setData("ORDER_SEQ", tablValue.getValue("ORDER_SEQ", i));
                	if(isDispenseMedicines(parm)){
                		this.messageBox(orderDesc+":\r\n 已完成调配,请注意退药!");
                		if(this.messageBox("提示", orderDesc+":\r\n 已完成调配,请注意退药!", 2) == 0){
                			
                		}else{
                			return false;
                		}
                	}
                }
                // add by wangqing 20180104 start
                              
			}
		}
		// undoData.setCount(selRows);
		undoData.setCount(undoData.getCount("CASE_NO"));
		if (isExec(undoData)) {
			return false;
		}
		// System.out.println("undoData = " + undoData);
		// 调用action执行事务
        TParm result = new TParm();
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
        	
        	//modify by yangjj 20150616
        	
        	undoData.setData("dataParm", p.getData());
            //undoData.setData("dataParm", tablValue.getData());
            result =
                    TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onUndoCheckOP",
                                                 undoData);
        } else {
            result =
                    TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onUndoSave",
                                                 undoData);
        }
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		}
		
		// 向大屏发送特定医嘱消息
		this.sendOrderInfoXml(xmlParm);
		return true;

	}

	private boolean checkPHACheck() {
		// TParm tablValue = mainTable.getParmValue();
		// int rowCount = mainTable.getRowCount();
		// boolean isCheck = false;
		// String descAll = "";
		// for (int i = 0; i < rowCount; i++) {
		// if (TypeTool.getBoolean(mainTable.getValueAt(i, 13)))
		// continue;
		// String desc = (String)
		// tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i);
		// Timestamp phaCheckDate = (Timestamp)
		// tablValue.getData("PHA_CHECK_DATE", i);
		// if(phaCheckDate != null){
		// descAll += (desc +"\n");
		// isCheck = true;
		// }
		// }
		// if(isCheck)
		// messageBox(descAll + "药房已审核无法取消护士站审核");
		// return isCheck;
		// luhai modify 2012-2-25 高工讨论，已经审核的药品，护士也可以取消审核 begin
		TParm tablValue = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		boolean isCheck = false;
		String descAll = "";
		for (int i = 0; i < rowCount; i++) {
			// modify by wangb 2016/07/28 护士审核表格中增加输液泵列引发列判断错误 由16改为CHECK_INDEX_18
			if (TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX)))//modify by yangjj 20150612 15改16
				continue;
			String desc = (String) tablValue.getData(
					"ORDER_DESC_AND_SPECIFICATION", i);
			Timestamp phaCheckDate = (Timestamp) tablValue.getData(
					"PHA_CHECK_DATE", i);
			String cat1Type = (String) tablValue.getData("CAT1_TYPE", i);
			if (phaCheckDate != null && !cat1Type.equalsIgnoreCase("PHA")) {
				descAll += (desc + "\n");
				isCheck = true;
			}
		}
		if (isCheck)
			messageBox(descAll + "药房已审核无法取消护士站审核");
		return isCheck;
		// luhai modify 2012-2-25 高工讨论，已经审核的药品，护士也可以取消审核 end
	}

	/**
	 * 护士取消审核检验药房是否发药
	 * 
	 * @return
	 */
	private boolean checkUDDCheck() {
		TParm tablValue = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		boolean isCheck = false;
		String descAll = "";
		for (int i = 0; i < rowCount; i++) {
			// modify by wangb 2016/07/28 护士审核表格中增加输液泵列引发列判断错误 由16改为CHECK_INDEX_18
			if (TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX)))//modify by yangjj 20150612 15改16
				continue;
			String desc = (String) tablValue.getData(
					"ORDER_DESC_AND_SPECIFICATION", i);
			String cat1Type = (String) tablValue.getData("CAT1_TYPE", i);
			if (!cat1Type.equalsIgnoreCase("PHA")) {
				continue;
			}
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = (String) tablValue.getData("ORDER_SEQ", i);
			String SelSql = " SELECT PHA_DOSAGE_CODE,PHA_DOSAGE_DATE "
					+ " FROM ODI_DSPNM " + " WHERE CASE_NO='" + caseNo + "'"
					+ " AND   ORDER_NO = '" + orderNo + "'"
					+ " AND   ORDER_SEQ = '" + orderSeq + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(SelSql));
			for (int j = 0; j < result.getCount(); j++) {
				if (!result.getValue("PHA_DOSAGE_CODE", j).equals(""))
					isCheck = true;
			}
		}
		if (isCheck)
			messageBox(descAll + "药房已发药无法取消护士站审核");
		return isCheck;
	}

	private void getSetOrder(String caseNo, String orderNo, String orderCode,
			String orderSetGroupNo, Timestamp now, TParm parm) {
		String SelSql = " SELECT * " + " FROM ODI_ORDER " + " WHERE CASE_NO='"
				+ caseNo + "'" + " AND   ORDER_NO = '" + orderNo + "'"
				+ " AND   ORDERSET_GROUP_NO = '" + orderSetGroupNo + "'"
				+ " AND   ORDERSET_CODE = '" + orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(SelSql));
		for (int i = 0; i < result.getCount(); i++) {
			parm.addData("CASE_NO", result.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", result.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", result.getValue("ORDER_SEQ", i));
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_DATE", now);
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("TAKEMED_ORG", "");
		}
	}

	/**
	 * 逆流程(取消审核)保存的时候检核是否已经执行
	 * 
	 * @return boolean
	 */
	public boolean isExec(TParm ordParm) {
		Vector caseNo = (Vector) ordParm.getVector("CASE_NO");
		Vector orderNo = (Vector) ordParm.getVector("ORDER_NO");
		Vector orderSeq = (Vector) ordParm.getVector("ORDER_SEQ");
		String inCaseNo = "";
		String inOrderNo = "";
		String inOrderSeq = "";
		boolean flg1 = true;
		boolean flg2 = true;
		boolean flg3 = true;
		// ------------------------shibl 20120428 modify
		// for (int i = 0; i < ordParm.getCount(); i++) {
		// // //当有数据的时候(都是为拼IN)
		// // if (caseNo.size() != 0) {
		// // //当前面已经有数据并且和新的一个不相同的时候需要在前面的数据后加逗号(利用短路不会越界)
		// // if (!inCaseNo.equals("") &&
		// // ! ( (Vector) caseNo.get(i - 1)).get(0).
		// // equals( ( (Vector) caseNo.get(i)).get(0))) {
		// // inCaseNo += ",";
		// // flg1 = true;
		// // }
		// // if (!inOrderNo.equals("") &&
		// // ! ( (Vector) orderNo.get(i - 1)).get(0).
		// // equals( ( (Vector) orderNo.get(i)).get(0))) {
		// // inOrderNo += ",";
		// // flg2 = true;
		// // }
		// // if (!inOrderSeq.equals("") &&
		// // ! ( (Vector) orderSeq.get(i - 1)).get(
		// // 0).equals( ( (Vector) orderSeq.get(i)).get(0))) {
		// // inOrderSeq += ",";
		// // flg3 = true;
		// // }
		// // inCaseNo += flg1 ? "'" + ( (Vector) caseNo.get(i)).get(0) + "'" :
		// // "";
		// // flg1 = false;
		// // inOrderNo += flg2 ?
		// // "'" + ( (Vector) orderNo.get(i)).get(0) + "'" : "";
		// // flg2 = false;
		// // inOrderSeq += flg3 ?
		// // "'" + ( (Vector) orderSeq.get(i)).get(0) + "'" : "";
		// // flg3 = false;
		// // }
		// }
		// ===========pangben modify 20110516 start 区域添加
		String region = "";
		String checkSql = "";
		TParm result = new TParm();
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		
		if(isOpeFlg()){
			return false;
		}
		// ------------------------shibl 20120428 modify
		for (int i = 0; i < ordParm.getCount(); i++) {
			inCaseNo = ((Vector) caseNo.get(i)).get(0) + "";
			inOrderNo = ((Vector) orderNo.get(i)).get(0) + "";
			inOrderSeq = ((Vector) orderSeq.get(i)).get(0) + "";
			checkSql = "SELECT  ORDER_DESC" + " FROM ODI_DSPNM"
					+ " WHERE CASE_NO ='" + inCaseNo + "'" + " AND ORDER_NO ='"
					+ inOrderNo + "'" + " AND ORDER_SEQ ='" + inOrderSeq + "'"
					+ " AND NS_EXEC_CODE IS NOT NULL" + region;
			// 执行sql语句
			TJDODBTool tool = TJDODBTool.getInstance();
			result = new TParm(tool.select(checkSql));
			// 拿到返回的行数(PS:用TJDODBTool返回后包装的TParm取行数的特殊方法)
			int count = result.getInt("SYSTEM", "COUNT");
			if (count == 0) {
				continue;
			}
			String hadExecName = "";
			for (int j = 0; j < count; j++) {
				hadExecName += (result.getValue("ORDER_DESC", j) + "\n");
			}
			
			this.messageBox(hadExecName + "已经执行不可取消");
			return true;
		}
		return false;
	}

	/**
	 * 审核主方法
	 * 
	 * @return boolean
	 */
	public boolean onCheck() {
		// 拿到所有挑勾--展开人的caseNo
		mainTable.acceptText();
		TParm patData = mainTable.getParmValue();
		//TODO:表中的数据
		System.out.println("onCheck>>>patData:" + patData);
		
		int rowCount = mainTable.getRowCount();
		TParm parm = new TParm();// 创建数据TParm
		TParm xmlParm = new TParm();// 大屏接口医嘱消息
		int count = 0;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) patData.getData("CASE_NO", i);
			String orderNo = (String) patData.getData("ORDER_NO", i);
			String orderSeq = patData.getData("ORDER_SEQ", i) + "";
			String setMainFlg = patData.getData("SETMAIN_FLG", i) + "";
			String orderSetGroupNo = patData.getData("ORDERSET_GROUP_NO", i)
					+ "";
			// 取药注记
			String takeMedOrg = (String) patData.getData("TAKEMED_ORG", i);
			if (TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))) {//modify by yangjj 20150612 15改16 //modify by wukai 201605
                //===========================add by wanglong 20130710
                if ("UD".equals(patData.getValue("RX_KIND", i))
                        && !"PHA".equals(patData.getValue("CAT1_TYPE", i))
                        && !StringUtil.isNullString(patData.getValue("DC_DATE", i))) {
                    TParm result =
                            InwOrderCheckTool.getInstance().queryUnExecOrder(caseNo, orderNo,
                                                                             orderSeq);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result.getErrText());
                        return false;
                    } else if (result.getInt("NUM", 0) > 0) {
                        this.messageBox("病患:" + patData.getValue("PAT_NAME", i) + " 的 "
                                + patData.getValue("ORDER_DESC_AND_SPECIFICATION", i) + "（"
                                + patData.getValue("ORDER_CODE", i) + "）未执行过，不能停用！");
                        continue;
                    }
                }
                // ===========================add end
				// SHIBL 20120820 MODIFY 审核日期拆分日期+时间
				String checkDate = "" + mainTable.getValueAt(i, 22);//modify by yangjj 20150612 19改20 //modify by wukai 20160530 20改22
				String checkTime = ("" + mainTable.getValueAt(i, 23)).replace(":", ""); //chenxi modify 20130325//modify by yangjj 20150612 20改21//modify by wukai 20160530 21改23
				// 记录当前UI显示的审核时间--由于会手动修改该时间
				Timestamp checkDateTime = StringTool.getTimestamp(
						checkDate.substring(0, 10).replaceAll("/", "")
								.replaceAll("-", "")
								+ " " + checkTime.substring(0, 4) + "00",
						"yyyyMMdd HHmmss");
				// 一次拿到caseNo
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				parm.addData("SETMAIN_FLG", setMainFlg);
				parm.addData("ORDERSETGROUP_NO", orderSetGroupNo);
                parm.addData("ORDER_CODE", patData.getValue("ORDER_CODE", i));// wanglong add 20140707
                parm.addData("ORDERSET_CODE", patData.getValue("ORDERSET_CODE", i));// wanglong add 20140707
				parm.addData("CHECK_DATETIME", checkDateTime);
				parm.addData("TAKEMED_ORG", takeMedOrg);
				parm.addData("PUMP_CODE",patData.getValue("PUMP_CODE", i));   //泵入方式wukai add on 20160531
				parm.addData("INFLUTION_RATE", patData.getValue("INFLUTION_RATE",i));  //输液速率wukai on 20160531
				
				// add by wangb 2017/5/2 大屏接口消息 START
				xmlParm.addData("MrNo", patData.getValue("MR_NO", i));
				xmlParm.addData("CaseNo", patData.getValue("CASE_NO", i));
				xmlParm.addData("PatName", patData.getValue("PAT_NAME", i));
				xmlParm.addData("OrderType", "");
				xmlParm.addData("OrderCode", patData.getValue("ORDER_CODE", i));
				xmlParm.addData("OrderDesc", patData.getValue("ORDER_DESC_AND_SPECIFICATION", i));
				xmlParm.addData("DrNote", patData.getValue("DR_NOTE", i));
				xmlParm.addData("OrderDate", StringTool.getString(patData
						.getTimestamp("EFF_DATE", i), "yyyy-MM-dd HH:mm:ss"));
				xmlParm.addData("DcDate", patData.getTimestamp("DC_DATE", i));
				xmlParm.addData("CancelFlg", "N");
				xmlParm.addData("OrderNo", orderNo + orderSeq);
				xmlParm.addData("OrderSeq", orderSeq);
				// add by wangb 2017/5/2 大屏接口消息 END
				
                if ("LIS".equals(patData.getValue("CAT1_TYPE", i))
                        || "RIS".equals(patData.getValue("CAT1_TYPE", i))) {//wanglong add 20150520
                    sendHL7Parm.addData("CASE_NO", caseNo);
                    sendHL7Parm.addData("ORDER_NO", orderNo);
                    sendHL7Parm.addData("ORDER_SEQ", TCM_Transform.getInt(orderSeq));
                    flg = 0;
                }
				count++;
			}
		}
		// 如果没有病患
		if (parm.getCount("CASE_NO") <= 0) {
			this.messageBox("请选中要审核的病人！");
			return false;
		}
		// 设置数量
		parm.setCount(count);
		// 准备传给后台的数据
		TParm toData = new TParm();
		// 操作人员信息
		toData.setData("OPT_USER", Operator.getID());
		toData.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		toData.setData("OPT_TERM", Operator.getIP());
		toData.setData("dataParm", parm.getData());
		// 调用action执行事务
        TParm result = new TParm();
        if (isOpeFlg()) {// 术中医嘱 wanglong add 20140707
        	System.out.println("术中医嘱审核");
            result =
                    TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onCheckOP",
                                                 toData);
        } else {
            result =
                    TIOM_AppServer
                            .executeAction("action.inw.InwOrderCheckAction", "onSave", toData);
        }
//		System.out.println("================="+result);
		if (result.getErrCode() == -100) {
			this.messageBox_(result.getErrText());
			return false;
		}
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		}
		
		// 向大屏发送特定医嘱消息
		this.sendOrderInfoXml(xmlParm);
		return true;

	}
	/**
	 * 调用国药发送消息
	 * @param flg
	 */
    public boolean onSendGYPha(String flg,String type){
    	// 拿到所有挑勾--展开人的caseNo
		mainTable.acceptText();
		TParm patData = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		TParm parm = new TParm();// 创建数据TParm
		
		System.out.println("patData:" + patData + "\nrowCount:" + rowCount);
		int count = 0;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) patData.getData("CASE_NO", i);
			String orderNo = (String) patData.getData("ORDER_NO", i);
			String orderSeq = patData.getData("ORDER_SEQ", i) + "";
			String cat1type = patData.getData("CAT1_TYPE", i) + "";
			String rxkind=patData.getData("RX_KIND", i) + "";
			System.out.println("cat1type:" + cat1type + "rxkind:" + rxkind);
			
			if (TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))&&cat1type.equals("PHA")//modify by yangjj 20150612 15改16 //modify by wukai 20160530 16改18
					&&rxkind.equals("ST")&&flg.equals("A")&&type.equals("1")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (!TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))&&cat1type.equals("PHA")//modify by yangjj 20150612 15改16//modify by wukai 20160530 16改18
					&&rxkind.equals("ST")&&flg.equals("D")&&type.equals("1")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))&&cat1type.equals("PHA")//modify by yangjj 20150612 15改16//modify by wukai 20160530 16改18
					&&rxkind.equals("ST")&&flg.equals("A")&&type.equals("2")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (!TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX))&&cat1type.equals("PHA")//modify by yangjj 20150612 15改16//modify by wukai 20160530 16改18
					&&rxkind.equals("ST")&&flg.equals("D")&&type.equals("2")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}			
		}
		if(count<=0){
			this.messageBox("没有发送的数据");
			return false;
		}		
		parm.setCount(count);
		parm.setData("TYPE", flg);
		// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 START
		// 根据物联网开关决定是否调用webService
		String methodName = "";
		if (Operator.getSpcFlg().equals("Y")) {
			methodName = "onSendGYPha";
		} else {
			// 向本地数据库插入临时医嘱出库数据
			methodName = "onSavePha";
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.InwOrderCheckAction", methodName, parm);
		
		// modify by wangbin 2015/01/22 内部需求 #778 住院护士站审核相关调整 END
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return false;
		}
		return true;
    }
    /**
     * 重送功能 		
     */
    public void onReSendGYPha(){
    	if(Operator.getSpcFlg().equals("N")){
    		this.messageBox("物联网开关已关闭");
    		return;
    	}	
    	String flg="";
    	if(checkYES.isSelected()){
    		flg="A";
    	}else if(this.checkNO.isSelected()){
    		flg="D";
    	}else{
    		this.messageBox("重送只能在已审核或未审核下使用");
    		return;
    	}
    	if(onSendGYPha(flg,"2")){
    		this.messageBox("重送成功");
    	}else{
    		this.messageBox("重送失败");
    	}
    }
	/**
	 * 整理table上得到数据，把没有执行的（NS_EXEC_CODE是空的剔除）
	 * 
	 * @param totalParm
	 *            table上得到的所有数据
	 * @return TParm 整理之后需要执行的数据
	 */
	public TParm arrangeDateToIBS(TParm totalParm) {
		TParm result = new TParm();
		int count = totalParm.getCount();
		// 未审核界面
		if (!checkYES.isSelected()) {
			// 倒序删除--vector
			for (int i = count - 1; i >= 0; i--) {
				// 如果执行护士为空说明没有执行，将其剔除不传给后台IBS
				if (totalParm.getData("NS_EXEC_CODE", i) != null
						&& !totalParm.getData("NS_EXEC_CODE", i).equals("")
						&& !totalParm.getData("DSPN_KIND", i).equals("PHA")) { // 不是药品的在护士站计费
					continue;
				}
				totalParm.removeRow(i);

			}
		} // 审核界面
		else {
			// 倒序删除--vector
			for (int i = count - 1; i >= 0; i--) {
				// 如果执行护士为空说明没有执行，将其剔除不传给后台IBS
				if (totalParm.getData("NS_EXEC_CODE", i) == null
						&& !totalParm.getData("DSPN_KIND", i).equals("PHA")) { // 不是药品的在护士站计费
					continue;
				}
				totalParm.removeRow(i);

			}
		}
		result = totalParm;
		return result;
	}

	/**
	 * 检查对应table上是否存在数据 （每次查询）
	 * 
	 * @param tab
	 *            TTable
	 */
	public void existsDateForTabl(TTable tab) {
		if (tab.getRowCount() == 0)
			this.messageBox("没有相关数据！");
		// 跟新全部执行控件
		exeAll.setSelected(updateAllExe());
	}

	/**
	 * 单击某条医嘱在状态栏上显示信息
	 */
	public void onClick() {

		TParm action = mainTable.getDataStore().getRowParm(
				mainTable.getSelectedRow());
		callFunction(
				"UI|setSysStatus",
				action.getValue("ORDER_CODE") + action.getValue("ORDER_DESC")
						+ action.getValue("GOODS_DESC")
						+ action.getValue("DESCRIPTION")
						+ action.getValue("SPECIFICATION"));

	}

	/**
	 * 打印多病人医嘱审核单
	 */
	public void onPrintExe() {
		if (this.ord1All.isSelected() || this.ord2All.isSelected()
				|| checkAll.isSelected()) {
			this.messageBox("不能在全部状态下打印！");
			return;
		}
		String orderKind = "";
		if (ord1ST.isSelected()) {
			orderKind = "临时";
		} else if (ord1UD.isSelected()) {
			orderKind = "长期";
		} else if (ord1DS.isSelected()) {
			orderKind = "出院带药";
		} else if (ord1IG.isSelected()) {
			orderKind = "中药饮片";
		}
		String orderType = "";
		if (ord2PHA.isSelected()) {
			orderType = "药嘱   ";
		} else if (ord2PL.isSelected()) {
			orderType = "处置";
		} else if (ord2ENT.isSelected()) {
			orderType = "嘱托";
		}
		String orderExe = "";
		if (this.checkYES.isSelected()) {
			orderExe = "已审核";
		} else if (checkNO.isSelected()) {
			orderExe = "未审核";
		}
		mainTable.acceptText();
		TParm tableDate = this.mainTable.getParmValue();
		String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
		Map map = new HashMap();
		TParm printData = new TParm();
		int count = 0;
		int tableDataCount = tableDate.getCount();
		for (int i = 0; i < tableDataCount; i++) {
			boolean prtFlg = tableDate.getBoolean("PRINT_FLG", i);
			if (!prtFlg)
				continue;
			String caseNoKey = tableDate.getValue("CASE_NO", i);
			if (map.get(caseNoKey) != null) {
				printData.addData("BED_NO", "");
				// printData.addData("MR_NO", "");
				printData.addData("PAT_NAME", "");
				printData.addData("CASE_NO", caseNoKey);
			} else if (map.get(caseNoKey) == null && i == 0) {
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", this.getNewbedDesc(caseNoKey));
				printData
						.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			} else {
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", " ");
				// printData.addData("MR_NO", "病案号");
				printData.addData("PAT_NAME", " ");
				printData.addData("LINK_NO", " ");
				printData.addData("ORDER_DESC", " ");
				printData.addData("MEDI_QTY", " ");
				printData.addData("MEDI_UNIT", " ");
				printData.addData("FREQ_CODE", "");
				printData.addData("ROUTE_CODE", " ");
//				printData.addData("DISPENSE_FLG", " ");
				printData.addData("DC_DATE", " ");
				printData.addData("NS_CHECK_DATE", " ");
				printData.addData("DR_NOTE", " ");
				printData.addData("NS_CHECK_CODE", " ");
				count++;
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", this.getNewbedDesc(caseNoKey));
				// printData.addData("MR_NO", tableDate.getValue("MR_NO", i));
				printData
						.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			}
			map.put(caseNoKey, caseNoKey);
			printData.addData("LINK_NO", tableDate.getValue("LINK_NO", i));
			printData.addData("ORDER_DESC",
					tableDate.getValue("ORDER_DESC_AND_SPECIFICATION", i));
			printData.addData("MEDI_QTY", tableDate.getValue("MEDI_QTY", i));
			printData.addData("MEDI_UNIT",
					getUnit(tableDate.getValue("MEDI_UNIT", i)));
			printData.addData(
					"FREQ_CODE",
					getFreqData(tableDate.getValue("FREQ_CODE", i)).getValue(
							"FREQ_CHN_DESC", 0));
			printData.addData(
					"ROUTE_CODE",
					OrderUtil.getInstance().getRoute(
							tableDate.getValue("ROUTE_CODE", i)));
//			String parpareFlg =  tableDate.getValue("DISPENSE_FLG", i);
//            if(parpareFlg == null || "".equals(parpareFlg)) {
//          	  printData.addData("DISPENSE_FLG" , "");
//            } else {
//          	  printData.addData("DISPENSE_FLG", "Y".equals(parpareFlg)?"是":"否");
//            }
			printData.addData("DC_DATE", tableDate.getValue("DC_DATE", i)
					.equals("") ? " " : tableDate.getValue("DC_DATE", i)
					.replaceAll("-", "/").substring(5, 16));
			printData.addData(
					"NS_CHECK_DATE",
					tableDate.getValue("NS_CHECK_DATE", i).equals("") ? " "
							: tableDate.getValue("NS_CHECK_DATE", i)
									.replaceAll("-", "/").substring(5, 10)
									+ " "
									+ (tableDate.getValue("NS_CHECK_DATE_TIME",
											i).equals("") ? " " : tableDate
											.getValue("NS_CHECK_DATE_TIME", i)
											.substring(0, 5)));
			printData.addData("DR_NOTE", tableDate.getValue("DR_NOTE", i));
			printData.addData("NS_CHECK_CODE",
					getOperatorName(tableDate.getValue("NS_CHECK_CODE", i)));
			count++;
		}
		printData.setCount(count);
		TParm GprintParm = new TParm();
		if (count <= 0) {
			this.messageBox("无打印数据！");
			return;
		} else {
			Map patMap = new HashMap();
			Map pat = groupByPatParm(printData);
			// Iterator it = pat.values().iterator();
			for (int j = 0; j < tableDate.getCount(); j++) {
				boolean prtFlg = tableDate.getBoolean("PRINT_FLG", j);
				if (!prtFlg)
					continue;
				if (patMap.get(tableDate.getValue("CASE_NO", j)) == null) {
					if (pat.get(tableDate.getValue("CASE_NO", j)) != null) {
						TParm patParm = (TParm) pat.get(tableDate.getValue(
								"CASE_NO", j));
						int rows = patParm.getCount();
						for (int i = 0; i < rows; i++) {
							GprintParm.addData("BED_NO",
									patParm.getValue("BED_NO", i));
							GprintParm.addData("PAT_NAME",
									patParm.getValue("PAT_NAME", i));
							GprintParm.addData("LINK_NO",
									patParm.getValue("LINK_NO", i));
							GprintParm.addData("ORDER_DESC",
									patParm.getValue("ORDER_DESC", i));
							GprintParm.addData("MEDI_QTY",
									patParm.getValue("MEDI_QTY", i));
							GprintParm.addData("MEDI_UNIT",
									patParm.getValue("MEDI_UNIT", i));
							GprintParm.addData("FREQ_CODE",
									patParm.getValue("FREQ_CODE", i));
							GprintParm.addData("ROUTE_CODE",
									patParm.getValue("ROUTE_CODE", i));
							GprintParm.addData("NS_CHECK_DATE",
									patParm.getValue("NS_CHECK_DATE", i));
//							GprintParm.addData("DISPENSE_FLG",
//									patParm.getValue("DISPENSE_FLG", i));
							GprintParm.addData("DC_DATE",
									patParm.getValue("DC_DATE", i));
							GprintParm.addData("DR_NOTE",
									patParm.getValue("DR_NOTE", i));
							GprintParm.addData("NS_CHECK_CODE",
									patParm.getValue("NS_CHECK_CODE", i));
						}
					}
				}
				patMap.put(tableDate.getValue("CASE_NO", j),
						tableDate.getValue("CASE_NO", j));
			}
		}
		GprintParm.setCount(count);
		GprintParm.addData("SYSTEM", "COLUMNS", "BED_NO");
		// printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		GprintParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_UNIT");
		GprintParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_DATE");
	//	GprintParm.addData("SYSTEM", "COLUMNS", "DISPENSE_FLG");
		GprintParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
		GprintParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_CODE");
		TParm printParm = new TParm();
		printParm.setData("TITLE", "TEXT", "医嘱审核单");
		printParm.setData("ORDER_KIND", "TEXT", "医嘱类别:" + orderKind);
		printParm.setData("ORDER_TYPE", "TEXT", "医嘱种类:" + orderType);
		printParm.setData("ORDER_EXE", "TEXT", "审核确认:" + orderExe);
		printParm.setData("STATION_CODE", "TEXT", "病区：   "
				+ getStationDesc(this.getValueString("INW_STATION_CODE")));
		printParm.setData(
				"DATE",
				"TEXT",
				"审核起日："
						+ this.getValueString("from_Date").replaceAll("-", "/")
								.substring(0, 10)
						+ " "
						+ this.getValueString("from_Time")
						+ " "
						+ "审核迄日："
						+ this.getValueString("to_Date").replaceAll("-", "/")
								.substring(0, 10) + " "
						+ this.getValueString("to_Time"));
		printParm.setData("TABLE", GprintParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\inw\\inwCheckNewPrint.jhw",
				printParm);
	}

	/**
	 * 清除table上的的数据行
	 */
	public void onRemoveTbl() {
		mainTable.setParmValue(new TParm());
		exeAll.setSelected(false);
		printAll.setSelected(false);
		if (this.checkYES.isSelected() && this.ord2PHA.isSelected()) {
			callFunction("UI|medPrint|setEnabled", true);
			this.StUdLab.setEnabled(true);
			this.medSta.setEnabled(true);
			this.medOrg.setEnabled(true);
			this.medSta.setSelected(true);
			this.medOrg.setSelected(false);
		} else {
			callFunction("UI|medPrint|setEnabled", false);
			this.StUdLab.setEnabled(false);
			this.medSta.setEnabled(false);
			this.medOrg.setEnabled(false);
			this.medSta.setSelected(true);
			this.medOrg.setSelected(false);
		}
	}

	/**
	 * 根据table上的数据更新全部执行标记是否选中
	 */
	public boolean updateAllExe() {
		int rowCount = mainTable.getRowCount();
		if (rowCount != 0) {
			for (int i = 0; i < rowCount; i++) {
				// modify by wangb 2016/07/28 护士审核表格中增加输液泵列引发列判断错误 由16改为CHECK_INDEX_18
				if (!TypeTool.getBoolean(mainTable.getValueAt(i, CHECK_INDEX)))//modify by yangjj 20150612 15改16
					return false;
			}
			return true;
		}
		return false;
	}

	public void selDOSE(Object flg) {
		boolean temp = TypeTool.getBoolean(flg);
		// 清空选择
		typeO.setSelected(temp);
		typeE.setSelected(temp);
		typeI.setSelected(temp);
		typeF.setSelected(temp);
		typeC.setSelected(temp);
		// 编辑状态
		typeO.setEnabled(temp);
		typeE.setEnabled(temp);
		typeI.setEnabled(temp);
		typeF.setEnabled(temp);
		typeC.setEnabled(temp);
		// 清除table
		onRemoveTbl();
	}

	/**
	 * 勾选所有选项打印
	 */
	public void onPrtAll() {
		boolean prt = printAll.isSelected();
		int count = mainTable.getRowCount();
		for (int i = 0; i < count; i++) {
			mainTable.setItem(i, "PRINT_FLG", prt);
		}
	}

	/**
	 * 处理切换的时候跟换TopMenu
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
		// case 0:
		// if (!onSave())
		// return false;
		// break;
		// case 1:
		// break;
		// case 2:
		// return false;
		// }
		return true;
	}

	/**
	 * 调用密码验证
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "inwCheck";
		String value = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", inwCheck);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}

	public String getCaseNo() {
		return caseNo;
	}

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }
    
	public String getStationCode() {
		return stationCode;
	}

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    
	public LEDUI getLedui() {
		return ledui;
	}

	public void setLedui(LEDUI ledui) {
		this.ledui = ledui;
	}

    public boolean isOpeFlg() {
        return opeFlg;
    }

    public void setOpeFlg(boolean opeFlg) {
        this.opeFlg = opeFlg;
    }

    public String getOpDeptCode() {
        return opDeptCode;
    }

    public void setOpDeptCode(String opDeptCode) {
        this.opDeptCode = opDeptCode;
    }

    public String getOpBookSeq() {
        return opBookSeq;
    }

    public void setOpBookSeq(String opBookSeq) {
        this.opBookSeq = opBookSeq;
    }
    
	/**
	 * 设置特殊药品颜色
	 */
	private void setColor() {
		TParm tableParm = mainTable.getParmValue();
		Color antibioticColor = new Color(255, 0, 0);
		Color normalColor = new Color(0, 0, 0);
		for (int i = 0; i < tableParm.getCount("ANTIBIOTIC_CODE"); i++) {
			if (tableParm.getValue("ANTIBIOTIC_CODE", i).length() == 0) {
				mainTable.setRowTextColor(i, normalColor);
				continue;
			}
			mainTable.setRowTextColor(i, antibioticColor);
		}
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = mainTable.getParmValue();
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
				String tblColumnName = mainTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
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
		mainTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
	 * 如果选择了药嘱，则根据药嘱精麻说明进行过滤
	 * 
	 * @param parm
	 */
	private void filterDrugByDoseCode(TParm parm) {
		if (!ord2PHA.isSelected())
			return;
		if (parm.getCount() <= 0)
			return;
		for (int i = parm.getCount() - 1; i >= 0; i--) {
			if (!parm.getValue("ORDER_CAT1_CODE", i).startsWith("PHA"))
				continue;
			if (typeC.isSelected()
					&& !getPhaCtrlCode(parm.getValue("ORDER_CODE", i)))
				parm.removeRow(i);
		}
		parm.setCount(parm.getCount("ORDER_CAT1_CODE"));
	}

	/**
	 * 根据床号查到床位名称
	 */
	private String getNewbedDesc(String caseNo) {
		String bed = "";
		// modify by wangb 2016/12/23 因存在包床的情况，因此不能直接根据就诊号查询床位取第一条数据
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE CASE_NO = '"
						+ caseNo + "' AND BED_OCCU_FLG = 'N'"));
		if (parm.getCount() > 0) {
			bed = parm.getValue("BED_NO_DESC", 0);
		}
		return bed;
	}

	/**
	 * 取得药品单位中文
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	private String getUnit(String code) {
		if (code.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT "
						+ " WHERE UNIT_CODE = '" + code + "'"));
		return parm.getValue("UNIT_CHN_DESC", 0);
	}

	/**
	 * 取得频次数据
	 * 
	 * @param freqCode
	 * @return
	 */
	private TParm getFreqData(String freqCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION "
						+ " FROM SYS_PHAFREQ " + " WHERE FREQ_CODE='"
						+ freqCode + "'"));
		return parm;
	}

	/**
	 * 根据userId得到姓名
	 * 
	 * @param userID
	 * @return
	 */
	private String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID = '" + userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 将按病患分组
	 * 
	 * @param parm
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("CASE_NO", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * 得到病区
	 * 
	 * @param stationCode
	 * @return
	 */
	private String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE = '" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\INWOrderCheckMain.x");
	}
	
    /**
     * 条码打印
     */
    public void onMedApplyPrint() {// wanglong add 20150519
        TParm outsideParm = this.getInputParm();
        if (outsideParm == null) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", "I");
        parm.setData("MR_NO", outsideParm.getValue("INW", "MR_NO"));
        parm.setData("CASE_NO", outsideParm.getValue("INW", "CASE_NO"));
        parm.setData("IPD_NO", outsideParm.getValue("INW", "IPD_NO"));
        parm.setData("PAT_NAME", outsideParm.getValue("INW", "PAT_NAME"));
        parm.setData("BED_NO", outsideParm.getValue("INW", "BED_NO"));
        parm.setData("DEPT_CODE", outsideParm.getValue("INW", "DEPT_CODE"));
        parm.setData("STATION_CODE", outsideParm.getValue("INW", "STATION_CODE"));
        parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
        parm.setData("POPEDEM", "1");
        this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
    }
    /**
     * 发送HL7消息
     * 
     * @param admType
     *            String 门急住别
     * @param catType
     *            医令分类
     * @param patName
     *            病患姓名
     * @param caseNo
     *            String 就诊号
     * @param applictionNo
     *            String 条码号
     * @param flg
     *            String 状态(0,发送1,取消)
     */
    private void sendHL7Mes() {//wanglong add 20150520
        int count = ((Vector) sendHL7Parm.getData("CASE_NO")).size();
        if (count <= 0) {
            return;
        }
        List list = new ArrayList();
        for (int i = 0; i < count; i++) {
            String sql = " SELECT * FROM ODI_ORDER WHERE CASE_NO ='"
                    + sendHL7Parm.getValue("CASE_NO", i) + "' AND ORDER_NO='"
                    + sendHL7Parm.getValue("ORDER_NO", i) + "' AND ORDER_SEQ="
                    + sendHL7Parm.getInt("ORDER_SEQ", i) + "";

            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            TParm parm = new TParm();
            parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
            parm.setData("ADM_TYPE", "I");
            parm.setData("FLG", flg);
            parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
            parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
            parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
            parm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
            parm.setData("SEQ_NO", result.getInt("ORDER_SEQ", 0));
            list.add(parm);
        }
        // 清空parm
        while (sendHL7Parm.getCount("CASE_NO") > 0) {
            sendHL7Parm.removeRow(0);
        }
        // 调用接口
        TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
        if (resultParm.getErrCode() < 0)
            this.messageBox(resultParm.getErrText());
    }
    
       
    /**
     * 补充计价
     * 20151028 wangjc add
     */
    public void onPay(Object isDbClick) {
        // this.messageBox("come in.");
        boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
        // 得到选中的行数
        /**
         * int selRow = masterTbl.getSelectedRow(); if (selRow < 0) { //
         * messageBox("请选择病患医嘱"); // return; outsideParm.setData("INWEXE",
         * "CASE_NO", getCaseNo()); outsideParm.setData("INWEXE", "ORDER_NO",
         * ""); outsideParm.setData("INWEXE", "ORDER_SEQ", ""); } else { TParm
         * tableParm = masterTbl.getParmValue(); // 取得需要传给IBS的参数 String caseNo =
         * tableParm.getValue("CASE_NO", selRow); String orderNo =
         * tableParm.getValue("ORDER_NO", selRow); String orderSeq =
         * tableParm.getValue("ORDER_SEQ", selRow); // 传给IBS的数据
         * outsideParm.setData("INWEXE", "CASE_NO", caseNo);
         * outsideParm.setData("INWEXE", "ORDER_NO", orderNo);
         * outsideParm.setData("INWEXE", "ORDER_SEQ", orderSeq); }
         **/
        TParm ibsParm = new TParm();
        int selRow = mainTable.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        String execDeptCode_ = "";
        String vsDrCode_ = "";

        if (selRow != -1) {
            TParm tableParm = mainTable.getParmValue();
            System.out.println("==tableParm=="+tableParm.getRow(selRow));
            // 取得需要传给SUM的参数
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
            execDeptCode_ = tableParm.getValue("ORDER_DEPT_CODE", selRow);//zhangp 成本中心修改为科室，补充计费不会再错
            // System.out.println("===execDeptCode_==="+execDeptCode_);
            vsDrCode_ = tableParm.getValue("VS_DR_CODE", selRow);
            // System.out.println("===vsDrCode_==="+vsDrCode_);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
            execDeptCode_ = Operator.getDept();
            // this.getValue("");
            vsDrCode_ = this.getValueString("VC_CODE");
            // System.out.println("===vsDrCode_==="+vsDrCode_);
            // outsideParm.getValue("")
            // this.getValue("");
            TParm parm = new TParm();
            parm.setData("CASE_NO", caseNo_);
            parm = ADMTool.getInstance().getADM_INFO(parm);
            bedNo_ = parm.getValue("BED_NO", 0);
            // System.out.println("===bedNo_==="+bedNo_);
            // outsideParm.getValue(name)

        }

        // 先选中再调用界面
        if (!dbClickFlg) {
            // outsideParm.setData("IBS", "TYPE", "INW");
        	 ibsParm.setData("IBS", "INWLEAD_FLG", false);
//        	 ibsParm.setData("IBS", "SCHD_CODE", schdCode);//临床路径时程
            ibsParm.setData("IBS", "CASE_NO", caseNo_);
            ibsParm.setData("IBS", "IPD_NO", ipdNo_);
            ibsParm.setData("IBS", "MR_NO", mrNo_);
            ibsParm.setData("IBS", "BED_NO", bedNo_);
//            ibsParm.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));//===pangben 2015-8-5 修改开单科室获得病患当前科室
            ibsParm.setData("IBS", "DEPT_CODE", this.getDeptCode());
            ibsParm.setData("IBS", "STATION_CODE", station_);
            ibsParm.setData("IBS", "VS_DR_CODE", vsDrCode_);
            ibsParm.setData("IBS", "TYPE", "INW");
            ibsParm.setData("IBS", "CLNCPATH_CODE", clpCode_);
            
            openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
            /*
             * //激发MovePane的双击效果 mp1.onDoubleClicked(isCharge); if (!isCharge) {
             * 
             * getTPanel("ChargePanel").addItem("ChargePanel_",
             * "%ROOT%\\config\\ibs\\IBSOrderm.x", outsideParm, false); }
             * isCharge = isCharge ? false : true; //隐藏右边的按钮
             * mp2.onDoubleClicked(true); mp3.onDoubleClicked(true);
             */
        } else { // 界面打开着，双击调用 outsideParm
            this.callFunction("UI|ChargePanel_|getINWData", ibsParm);
        }
    
    }

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public String getClpCode_() {
		return clpCode_;
	}

	public void setClpCode_(String clpCode_) {
		this.clpCode_ = clpCode_;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	/**
     * 调用结构化病历
     */
    public void onEmr() {
        int selRow = mainTable.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String patName_ = "";
        if (selRow != -1) {
            TParm tableParm = mainTable.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            patName_ = tableParm.getValue("PAT_NAME", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            patName_ = (String) ((TParm) this.getParameter()).getData("INW", "PAT_NAME");
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("请选择病人！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", caseNo_);// this.getCaseNo());
        parm.setData("PAT_NAME", patName_);// this.getPatName());
        parm.setData("MR_NO", mrNo_);// this.getMrNo());
        parm.setData("IPD_NO", ipdNo_);// this.getIpdNo());
        parm.setData("ADM_DATE", ((TParm) this.getParameter()).getData("INW", "ADM_DATE"));
        parm.setData("DEPT_CODE", this.getDeptCode());
        parm.setData("EMR_DATA_LIST", new TParm());
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }

    /**
     * 护理记录
     */
    public void onHLSel() {
        int selRow = mainTable.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String patName_ = "";
        if (selRow != -1) {
            TParm tableParm = mainTable.getParmValue();
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            patName_ = tableParm.getValue("PAT_NAME", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            patName_ = (String) ((TParm) this.getParameter()).getData("INW", "PAT_NAME");
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
        }
        if (caseNo_.length() == 0) {
            messageBox("请选择病人！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", caseNo_);
        parm.setData("PAT_NAME", patName_);
        parm.setData("MR_NO", mrNo_);
        parm.setData("IPD_NO", ipdNo_);
     
        parm.setData("ADM_DATE", ((TParm) this.getParameter()).getData("INW", "ADM_DATE"));
        parm.setData("DEPT_CODE", this.getDeptCode());
        parm.setData("RULETYPE", "2");
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

    }
	
    /**
     * 呼叫体温单接口
     */
    public void onVitalSign() {
        TParm sumParm = new TParm();
        // 得到选中的行数
        int selRow = mainTable.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        if (selRow != -1) {
            TParm tableParm = mainTable.getParmValue();
            // 取得需要传给SUM的参数
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
        }
        if (caseNo_.equals("")) {
            this.messageBox("请选择病人！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo_);
        parm = ADMTool.getInstance().getADM_INFO(parm);
        bedNo_ = parm.getValue("BED_NO", 0);
        sumParm.setData("SUM", "CASE_NO", caseNo_);
        sumParm.setData("SUM", "MR_NO", mrNo_);
        sumParm.setData("SUM", "IPD_NO", ipdNo_);
        sumParm.setData("SUM", "STATION_CODE", station_);
        sumParm.setData("SUM", "BED_NO", bedNo_);
        sumParm.setData("SUM", "ADM_TYPE", "I");
        //getTPanel("second").setVisible(false);
        this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x",  sumParm);
             
    }
    // 动态调出相应的panel
    public TPanel getTPanel(String name) {
    	
    		TPanel panel = (TPanel) this.getComponent(name);
	        return panel;
    	
    }
    /**
     * 呼叫新生儿体温单
     * 
     * @param name
     *            String
     * @return TPanel
     */
    public void onNewArrival() {
        TParm sumParm = new TParm();
        // 得到选中的行数
        int selRow = mainTable.getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        if (selRow != -1) {
            TParm tableParm = mainTable.getParmValue();
            // 取得需要传给SUM的参数
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
        }
        if (caseNo_.equals("")) {
            this.messageBox("请选择病人！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo_);
        parm = ADMTool.getInstance().getADM_INFO(parm);
        bedNo_ = parm.getValue("BED_NO", 0);
        sumParm.setData("SUM", "CASE_NO", caseNo_);
        sumParm.setData("SUM", "MR_NO", mrNo_);
        sumParm.setData("SUM", "IPD_NO", ipdNo_);
        sumParm.setData("SUM", "STATION_CODE", station_);
        sumParm.setData("SUM", "BED_NO", bedNo_);
        sumParm.setData("SUM", "ADM_TYPE", "I");
        this.openDialog("%ROOT%\\config\\sum\\SUMNewArrival.x",  sumParm);
    }
    /**
     * 血糖报告
     */
    public void getXTReport(){
    	SystemTool.getInstance().OpenTnbWeb(this.getMrNo());
    }
    /**
     * 心电报告
     */
    public void getPdfReport(){
    	String sql = "SELECT  DISTINCT MED_APPLY_NO  FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND ORDER_CAT1_CODE = 'ECC'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() <= 0){
    		this.messageBox("该病患没有心电检查医嘱");
    		return;
    	}
    	TParm parm = new TParm();
    	String opbBookNo = "";
    	for(int i = 0; i < result.getCount(); i++){
    		opbBookNo += "'"+result.getValue("MED_APPLY_NO", i)+"'"+",";
    	}
    	parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
    	parm.setData("CASE_NO",caseNo);
    	parm.setData("TYPE","3");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
	 * 检验报告
	 */
	public void onLis() {
		SystemTool.getInstance().OpenLisWeb(this.getMrNo());
	}

	/**
	 * 检查报告
	 */
	public void onRis() {
		SystemTool.getInstance().OpenRisWeb(this.getMrNo());
	}
	
	 /**
     * 重症监护
     */
    public void getCCEmrData(){
    	TParm parm = new TParm();
    	parm.setData("CASE_NO",this.getCaseNo());
    	parm.setData("TYPE","1");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
     * 手麻病历
     */
    public void getOpeMrData(){
    	TParm parm = new TParm();
    	parm.setData("CASE_NO",this.getCaseNo());
    	parm.setData("TYPE","2");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
     * 向大屏发送医嘱消息
     * 
     * @param parm
     */
    private void sendOrderInfoXml(TParm parm) {
    	int count = parm.getCount("CaseNo");
    	StringBuffer errMsg = new StringBuffer();
    	String conOrderCode = TConfig.getSystemValue("CON_ORDER_CODE");// 会诊医嘱
		String opeOrderCode = TConfig.getSystemValue("OPE_ORDER_CODE");// 拟行手术医嘱
		String tcOrderCode = TConfig.getSystemValue("TC_ORDER_CODE");// 气管插管医嘱
		// 查询特殊检查医嘱
		TParm seOrderResult = SYSFeeTool.getInstance().querySEOrder();
		String seOrderCode = seOrderResult.getValue("ORDER_CODE");
		TParm xmlParm = new TParm();
		
    	for (int i = 0; i < count; i++) {
    		// 会诊
    		if (conOrderCode.contains(parm.getValue("OrderCode", i))) {
    			parm.setData("OrderType", i, "CON");
    			if (!ADMXMLTool.getInstance().createOrderInfoXML(parm.getRow(i))) {
    				errMsg.append("病案号为【"+ parm.getValue("MrNo", i) + "】的病患，大屏接口医嘱消息发送失败");
    				errMsg.append("\n");
    			}
    			continue;
    		}
    		
    		// 拟行手术
    		if (opeOrderCode.contains(parm.getValue("OrderCode", i))) {
    			parm.setData("OrderType", i, "OPE");
    			if (!ADMXMLTool.getInstance().createOrderInfoXML(parm.getRow(i))) {
    				errMsg.append("病案号为【"+ parm.getValue("MrNo", i) + "】的病患，大屏接口医嘱消息发送失败");
    				errMsg.append("\n");
    			}
    			continue;
    		}
    		
    		// 气管插管,护士审核停用保留气管插管医嘱时发送病患消息
    		if (tcOrderCode.contains(parm.getValue("OrderCode", i))
    				&& StringUtils.isNotEmpty(parm.getValue("DcDate", i))) {
    			xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(parm.getValue("CaseNo", i));
    	        if (xmlParm.getErrCode() < 0) {
    				errMsg.append("病案号为【"+ parm.getValue("MrNo", i) + "】的病患，大屏接口病患消息发送失败");
    				errMsg.append("\n");
    	        }
    	        continue;
    		}
    		
    		// 特殊检查
    		if (seOrderCode.contains(parm.getValue("OrderCode", i))) {
    			parm.setData("OrderType", i, "SE");
    			if (!ADMXMLTool.getInstance().createOrderInfoXML(parm.getRow(i))) {
    				errMsg.append("病案号为【"+ parm.getValue("MrNo", i) + "】的病患，大屏接口医嘱消息发送失败");
    				errMsg.append("\n");
    			}
    			continue;
    		}
    	}
    	
    	if (errMsg.length() > 0) {
    		this.messageBox(errMsg.toString());
    	}
    }
    
    /**
     * 判断医嘱是否已经配药
     * @param parm
     * @return true，已配药；false，未配药
     * @author wangqing 20180104
     */
    private boolean isDispenseMedicines(TParm parm){
    	String caseNo = parm.getValue("CASE_NO");
    	String orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String sql = " SELECT CASE_NO, ORDER_NO, ORDER_SEQ, PHA_DOSAGE_CODE, PHA_DOSAGE_DATE "
    			+ "FROM ODI_DSPNM "
    			+ "WHERE PHA_DOSAGE_CODE IS NOT NULL "
    			+ "AND PHA_DOSAGE_DATE IS NOT NULL "
    			+ "AND CASE_NO='"+caseNo+"' "
    			+ "AND ORDER_NO='"+orderNo+"' "
    			+ "AND ORDER_SEQ='"+orderSeq+"' ";
    	System.out.println("{sql:"+sql+"}");
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<=0){
    		return false;
    	}else{
    		return true;
    	}
    }

}
