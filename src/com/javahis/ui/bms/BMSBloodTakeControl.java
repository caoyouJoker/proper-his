package com.javahis.ui.bms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.bms.BMSSQL;
import jdo.bms.BMSTakeTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: 取血单
 * </p>
 * 
 * @author yangjj
 * add by yangjj 20150427
 * 
 */
public class BMSBloodTakeControl extends TControl {
	private String caseNo = "";
	
	//病案号
	private TTextField MR_NO;
	
	//姓名
	private TTextField NAME;
	
	//年龄
	private TTextField AGE;
	
	//性别
	private TComboBox SEX;
	
	//床号
	private TComboBox BED_NO;
	
	//科室
	private TTextFormat DEPT_CODE;
	
	//病区
	private TTextFormat STATION_CODE;
	
	//血型
	private TLabel BLOOD_TYPE;
	
	//RH血型
	private TRadioButton BLOOD_RH_A;//阴性
	private TRadioButton BLOOD_RH_B;//阳性
	
	//开始日期
	private TTextFormat START_DATE;
	
	//结束日期
	private TTextFormat END_DATE;
	
	//取血申请单列表
	private TTable TABLE_TAKE;
	
	//备血申请单列表
	private TTable TABLE_APPLY;
	
	//统计列表
	private TTable TABLE_COUNT;
	
	//取血明细表
	private TTable TABLE_M;
	
	//血品
	private TComboBox BLD_CODE; 
	
	//取血单号
	private TTextField BLOOD_TANO;
	
	//患者传入信息
	private TParm patientParm;
	
	//开立医生
	private TComboBox ORDER_DRCODE;
	
	//开立日期
	private TTextFormat ORDER_DRDATE;
	
	//床号文本
	private TLabel BED_LABEL;
	
	//术间文本
	private TLabel OPE_ROOM_LABEL;
	
	//术间
	private TTextFormat OPE_ROOM;

	public void onInit() {
		super.onInit();
		
		//获取当前日期
		Timestamp date = StringTool.getTimestamp(new Date());
		
		//初始化控件
		MR_NO = (TTextField) this.getComponent("MR_NO");
		NAME = (TTextField) this.getComponent("NAME");
		AGE = (TTextField) this.getComponent("AGE");
		SEX = (TComboBox) this.getComponent("SEX");
		BED_NO = (TComboBox) this.getComponent("BED_NO");
		DEPT_CODE = (TTextFormat) this.getComponent("DEPT_CODE");
		STATION_CODE = (TTextFormat) this.getComponent("STATION_CODE");
		BLOOD_TYPE = (TLabel) this.getComponent("BLOOD_TYPE");
		BLOOD_RH_A = (TRadioButton) this.getComponent("BLOOD_RH_A");
		BLOOD_RH_B = (TRadioButton) this.getComponent("BLOOD_RH_B");
		START_DATE = (TTextFormat) this.getComponent("START_DATE");
		END_DATE = (TTextFormat) this.getComponent("END_DATE");
		START_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 00:00:00");
		END_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 23:59:59");
		TABLE_TAKE = (TTable) this.getComponent("TABLE_TAKE");
		TABLE_APPLY = (TTable) this.getComponent("TABLE_APPLY");
		TABLE_COUNT = (TTable) this.getComponent("TABLE_COUNT");
		TABLE_M = (TTable) this.getComponent("TABLE_M");
		TABLE_APPLY.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onApplyCheck");
		BLD_CODE = (TComboBox) this.getComponent("BLD_CODE");
		BLOOD_TANO = (TTextField) this.getComponent("BLOOD_TANO");
		ORDER_DRCODE = (TComboBox) this.getComponent("ORDER_DRCODE");
		ORDER_DRDATE = (TTextFormat) this.getComponent("ORDER_DRDATE");
		
		//add by yangjj 20150601
		BED_LABEL = (TLabel) this.getComponent("BED_LABEL");
		OPE_ROOM_LABEL = (TLabel) this.getComponent("OPE_ROOM_LABEL");
		OPE_ROOM = (TTextFormat) this.getComponent("OPE_ROOM");
		
		
		//获取就诊号参数
		TParm parm = (TParm) this.getParameter();
		patientParm = parm;
		caseNo = parm.getValue("CASE_NO");
		//===pangben 2016-5-6 添加备血申请单查询界面，取血单按钮管控操作，不可以操作保存和删除操作
		if(null!=parm.getData("APPLYNO_FLG")&&parm.getValue("APPLYNO_FLG").equals("Y")){
			callFunction("UI|save|setEnabled", false); // 保存按钮
			callFunction("UI|delete|setEnabled", false);//删除按钮
		}
		//根据CASE_NO获取患者信息
		String patientSql = getPatientByCaseNoSql(caseNo);
		TParm patientParm = new TParm(TJDODBTool.getInstance().select(patientSql));
		
		//填入界面患者信息
		MR_NO.setValue(patientParm.getValue("MR_NO", 0));
		NAME.setValue(patientParm.getValue("NAME", 0));
		AGE.setValue(patientParm.getValue("AGE", 0));
		SEX.setValue(patientParm.getValue("SEX", 0));
		STATION_CODE.setValue(patientParm.getValue("STATION_CODE", 0));
		BED_NO.setValue(patientParm.getValue("BED_NO", 0));
		OPE_ROOM.setValue(parm.getValue("OPE_ROOM"));
		
		
		//modify by yangjj 20150601
		DEPT_CODE.setValue(parm.getValue("DEPT_CODE"));
		
		//手术
		if("Y".equals(parm.getValue("OPE_FLG"))){
			BED_LABEL.setVisible(false);
			OPE_ROOM_LABEL.setVisible(true);
			BED_NO.setVisible(false);
			OPE_ROOM.setVisible(true);
		}
		//非手术
		else{
			BED_LABEL.setVisible(true);
			OPE_ROOM_LABEL.setVisible(false);
			BED_NO.setVisible(true);
			OPE_ROOM.setVisible(false);
		}
		
		
		
		
		BLOOD_TYPE.setValue(patientParm.getValue("BLOOD_TYPE", 0));
		
		String rh = patientParm.getValue("BLOOD_RH_TYPE", 0);
		if("-".equals(rh)){
			BLOOD_RH_B.setSelected(false);
			BLOOD_RH_A.setSelected(true);
		}else if("+".equals(rh)){
			BLOOD_RH_B.setSelected(true);
			BLOOD_RH_A.setSelected(false);
		}
		
		//获取取血申请单列表
		String takeSql = getTakeByCaseNo(caseNo, "", "");
		TParm takeParm = new TParm(TJDODBTool.getInstance().select(takeSql));
		TABLE_TAKE.setParmValue(takeParm);
		
		//获取备血申请单列表
		String applySql = getApplyByCaseNoSql(caseNo);
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(applySql));
		TABLE_APPLY.setParmValue(applyParm);
		
		//初始化开立医生和开立日期
		ORDER_DRCODE.setValue(Operator.getID());
		ORDER_DRDATE.setValue(date);
		
	}
	
	//申请单勾选事件
	public void onApplyCheck(Object obj){
		TTable table = (TTable) obj;
		TABLE_APPLY.acceptText();
		TParm parm = TABLE_APPLY.getParmValue();
		
		List<String> lst = new ArrayList<String>();
		for(int i = 0 ; i < parm.getCount(); i++){
			String s = parm.getValue("SELECT_FLG",i);	
			if("Y".equals(s)){
				lst.add(parm.getValue("APPLY_NO",i));
			}
		}
		
		if(lst.size() <= 0 ){
			TABLE_M.setParmValue(new TParm());
			return;
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(getApplySql(lst,caseNo)));
		TABLE_COUNT.setParmValue(result);
		
		TParm takeParm = new TParm();
		for(int i = 0 ; i < result.getCount() ; i++){
			takeParm.setData("BLD_CODE", i, result.getValue("BLD_CODE", i));
			takeParm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
			
			String str = result.getValue("TOTAL", i);
			if("".equals(str)){
				str = "0";
			}
			int totalQty = Integer.parseInt(str);
			
			String str1 = result.getValue("APPLY_QTY", i);
			if("".equals(str1)){
				str1 = "0";
			}
			int applyQty = Integer.parseInt(str1);
			
			int takeQty = applyQty - totalQty;
			if(takeQty < 0){
				takeQty = 0;
			}
			
			takeParm.setData("TAKE_QTY", i, takeQty);
			takeParm.setCount(i+1);
			
		}
		
		TABLE_M.setParmValue(takeParm);
	}
	
	/**
	 * 选择事件
	 */
	public void onBldCodeSel() {
		String bldCode = BLD_CODE.getSelectedID();
		if("".equals(bldCode)){
			this.clearValue("UNIT_CODE");
			this.clearValue("APPLY_QTY");
			return ;
		}
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(getBloodSql(bldCode)));
		this.setValue("UNIT_CODE", parm.getValue("UNIT_CODE", 0));
		this.clearValue("APPLY_QTY");
	}
	
	/**
	 * 增加
	 */
	public void onAdd() {
		if (BLD_CODE.getSelectedIndex() <= 0) {
			this.messageBox("E0138");
			this.clearValue("BLD_CODE;TAKE_QTY;UNIT_CODE;");
			return;
		}
		for (int i = 0; i < TABLE_M.getRowCount(); i++) {
			if (getValueString("BLD_CODE").equals(
					TABLE_M.getItemString(i, "BLD_CODE"))) {
				this.messageBox("此血品已存在");
				return;
			}
		}
		TABLE_M.acceptText();
		int row = TABLE_M.addRow();
		TABLE_M.setItem(row, "BLD_CODE", BLD_CODE.getValue());
		TABLE_M.setItem(row, "TAKE_QTY", this.getValueDouble("TAKE_QTY"));
		TABLE_M.setItem(row, "UNIT_CODE", this.getValueString("UNIT_CODE"));
		this.clearValue("BLD_CODE;TAKE_QTY;UNIT_CODE;");

	}
	
	/**
	 * 删除血液种类
	 */
	public void onRemove() {
		int row = TABLE_M.getSelectedRow();
		if (row == -1) {
			this.messageBox("E0134");
		}
		TABLE_M.removeRow(row);
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		
		//add by yangjj 20150515ABO血型和RH血型为空时不允许保存
		if("".equals(BLOOD_TYPE.getText())){
			this.messageBox("请根据检验结果，在备血申请单中填入患者ABO血型，更新患者ABO血型！");
			return;
		}
		
		if((!BLOOD_RH_B.isSelected())&&(!BLOOD_RH_A.isSelected())){
			this.messageBox("请根据检验结果，在备血申请单中填入患者RH血型，更新患者RH血型！");
			return;
		}
		
		TABLE_M.acceptText();
		TParm inparm = new TParm();
		
		//获取细表数据
		if (TABLE_M.getRowCount() <= 0) {
			onClear();
			return ;
		}
		TParm tableParm = new TParm();
		int seq = 0;
		for (int i = 0; i < TABLE_M.getRowCount(); i++) {
			String s = TABLE_M.getItemData(i, "TAKE_QTY").toString();
			if("0.0".equals(s) || "0".equals(s)){
				this.messageBox("数量不允许为0！");
				return ;
			}
			
			double d = 0.0;
			try {
				d = Double.parseDouble(s);
			} catch (Exception e) {
				// TODO: handle exception
				this.messageBox("数量错误，请重新输入！");
				return;
			}
			
			
			tableParm.addData("BLOOD_TANO", getValueString("BLOOD_TANO"));
			tableParm.addData("SEQ", seq);
			tableParm.addData("BLD_CODE", TABLE_M.getItemData(i, "BLD_CODE"));
			tableParm.addData("APPLY_QTY", TABLE_M.getItemData(i, "TAKE_QTY"));
			tableParm.addData("UNIT_CODE", TABLE_M.getItemData(i, "UNIT_CODE"));
			tableParm.addData("OPT_USER", Operator.getID());
			tableParm.addData("OPT_TERM", Operator.getIP());
			seq++;
			
			tableParm.setCount(i+1);
		}
		
		
		
		TParm Mparm = new TParm();
		Mparm.setData("ADM_TYPE", patientParm.getValue("ADM_TYPE"));
		Mparm.setData("CASE_NO", caseNo);
		Mparm.setData("IPD_NO", patientParm.getValue("IPD_NO"));
		Mparm.setData("MR_NO", getValueString("MR_NO"));
		Mparm.setData("BED_NO", getValueString("BED_NO"));
		Mparm.setData("STATION_CODE", getValueString("STATION_CODE"));
		Mparm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		
		//add by yangjj 20150601
		Mparm.setData("OPE_ROOM", getValueString("OPE_ROOM"));
		Mparm.setData("OPE_FLG", ((TParm) this.getParameter()).getValue("OPE_FLG"));
		
		Mparm.setData("BLOOD_TYPE", getValueString("BLOOD_TYPE"));
		if (BLOOD_RH_A.isSelected()) {
			Mparm.setData("BLOOD_RH_TYPE", "-");
		} else {
			Mparm.setData("BLOOD_RH_TYPE", "+");
		}
		
		Mparm.setData("OPT_USER", Operator.getID());
		Mparm.setData("OPT_TERM", Operator.getIP());
		Mparm.setData("ORDER_DRCODE", this.getValueString("ORDER_DRCODE"));
		Mparm.setData("ORDER_DRDATE", this.getValueString("ORDER_DRDATE").replace("-", "").replace(" ", "").replace(".0", "").replace(":", ""));
		
		//新增
		if ("".equals(getValueString("BLOOD_TANO"))) {
			this.setValue("BLOOD_TANO", BMSTakeTool.getNo());
			Mparm.setData("BLOOD_TANO", getValueString("BLOOD_TANO"));
			for(int i = 0 ; i < tableParm.getCount() ; i++){
				tableParm.setData("BLOOD_TANO", i, getValueString("BLOOD_TANO"));
			}
			
			inparm.setData("MTABLE", Mparm.getData());
			inparm.setData("DTABLE", tableParm.getData());
			
			TParm result = TIOM_AppServer.executeAction(
					"action.bms.BMSTakeAction", "onInsert", inparm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				onClear();
				return;
			}
			this.messageBox("P0001");

		} 
		//修改
		else {
			
			if(!checkOut(getValueString("BLOOD_TANO"))){
				this.messageBox("血袋已出库，不允许修改取血单！");
				return;
			}
			
			
			Mparm.setData("BLOOD_TANO", getValueString("BLOOD_TANO"));
			for(int i = 0 ; i < tableParm.getCount() ; i++){
				tableParm.setData("BLOOD_TANO", i, getValueString("BLOOD_TANO"));
			}
			inparm.setData("MTABLE", Mparm.getData());
			inparm.setData("DTABLE", tableParm.getData());
			
			TParm result = TIOM_AppServer.executeAction(
					"action.bms.BMSTakeAction", "onUpdate", inparm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				onClear();
				return;
			}
			this.messageBox("P0001");

		}
		onClear();
		return;
	}
	
	/*public boolean checkOut(String takeNo){
		boolean b = false;
		
	}*/
	
	public void onClear(){
		//获取取血申请单列表
		String takeSql = getTakeByCaseNo(caseNo, "", "");
		TParm takeParm = new TParm(TJDODBTool.getInstance().select(takeSql));
		TABLE_TAKE.setParmValue(takeParm);
		
		//获取备血申请单列表
		String applySql = getApplyByCaseNoSql(caseNo);
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(applySql));
		TABLE_APPLY.setParmValue(applyParm);
		
		//初始化开立医生和开立日期
		ORDER_DRCODE.setValue(Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		ORDER_DRDATE.setValue(date);
		
		TABLE_M.setParmValue(new TParm());
		TABLE_COUNT.setParmValue(new TParm());
		setValue("BLD_CODE", "");
		setValue("TAKE_QTY","");
		setValue("UNIT_CODE","");
		setValue("BLOOD_TANO","");
	}
	
	public void onDelete(){
		int row = TABLE_TAKE.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择需要删除的取血单！");
			return;
		}
		
		
		TABLE_TAKE.acceptText();
		TParm p = TABLE_TAKE.getParmValue();
		String takeNo = p.getValue("TAKE_NO", row);
		if(!checkOut(takeNo)){
			this.messageBox("血袋已出库，不允许删除取血单！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("BLOOD_TANO", takeNo);
		TParm result = TIOM_AppServer.executeAction(
				"action.bms.BMSTakeAction", "onDelete", parm);
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("删除失败");
			onClear();
			return;
		}else{
			this.messageBox("删除成功！");
			onClear();
		}
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		if ("".equals(this.getValueString("BLOOD_TANO"))) {
			this.messageBox("请选择需要打印的取血单！");
			return;
		}
		
		int row = TABLE_TAKE.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择需要打印的取血单！");
			return;
		}
		
		
		TABLE_TAKE.acceptText();
		TParm p = TABLE_TAKE.getParmValue();
		
		String mrNo=this.getValueString("MR_NO");
		
		Pat pat=Pat.onQueryByMrNo(mrNo);
		// 打印数据
		TParm date = new TParm();
		date.setData("TITLE", "TEXT", "取血单");//modify by sunqy 20140806 去掉医院全称  表头已存在
//		date.setData("TITLE", "TEXT", Manager.getOrganization()
//				.getHospitalCHNFullName(Operator.getRegion())+"取血单");
		date.setData("BAR_CODE", "TEXT", getValueString("BLOOD_TANO"));
		date.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		date.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",pat.getIpdNo());
		date.setData("filePatName", "TEXT", pat.getName());
		date.setData("fileSex", "TEXT", pat.getSexString());
		String birth=StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");
		date.setData("fileBirthday", "TEXT", birth);
		
		date.setData("MR_NO", "TEXT","病案号:"+mrNo);
		date.setData("PATNAME", "TEXT","受血者姓名:"+pat.getName());
		String stationCode=((TTextFormat)this.getComponent("STATION_CODE")).getText();
		date.setData("STATION_CODE", "TEXT","病区:"+stationCode);
		
		String opeFlg = ((TParm) this.getParameter()).getValue("OPE_FLG");
		if("Y".equals(opeFlg)){
			TParm tope = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_OPROOM' AND ID = '"+OPE_ROOM.getValue()+"'"));
			String opeRoom = tope.getValue("CHN_DESC", 0);
			date.setData("BED_NO", "TEXT","术间:"+opeRoom);
			TParm deptParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_DEPT WHERE DEPT_CODE = '"+Operator.getDept()+"'"));
			date.setData("DEPT_NO","TEXT","科室:"+deptParm.getValue("DEPT_CHN_DESC", 0));
		}else{
			String bed=BED_NO.getSelectedName();
			date.setData("BED_NO", "TEXT","床号:"+bed);
			date.setData("DEPT_NO","TEXT","科室:"+((TTextFormat)this.getComponent("DEPT_CODE")).getText());
		}
		
		
		
		//add by yangjj 20150414
		date.setData("SEX_CODE","TEXT","性别:"+("1".equals(pat.getSexCode())?"男":"女"));
		String age = "0";
		age = OdiUtil.getInstance().showAge(pat.getBirthday(),SystemTool.getInstance().getDate());
		date.setData("AGE", "TEXT", "年龄:"+age);
		date.setData("CASE_NO", "TEXT", "住院号:"+pat.getIpdNo());
		
		
		String bld=BLOOD_TYPE.getText();
		date.setData("BLOOD_TYPE", "TEXT", "ABO血型:"+bld);
		String rh="Y".equals(getValueString("BLOOD_RH_A")) ? "RH(D):-"
					: "RH(D):+";
		date.setData("RH_TYPE", "TEXT",rh);
		date.setData("BLOOD_DATE", "TEXT","开立时间:"+TABLE_TAKE.getItemString(row, "ORDER_DRDATE").replace(".0", "").replace("-", "/"));
		String orderDrCode = TABLE_TAKE.getItemString(row, "ORDER_DRCODE");
		TParm dr = new TParm(TJDODBTool.getInstance().select(getDrSql(orderDrCode)));
		
		//modify by yangjj 20150601 增加联系电话
		date.setData("BLOOD_USER", "TEXT","开立医生:"+dr.getValue("USER_NAME", 0)+"   联系电话:"+dr.getValue("TEL1",0));
		//表格数据
		TParm parm = new TParm();
		String bloodCode = "";//add by sunqy 20140805
		for (int i = 0; i < TABLE_M.getRowCount(); i++) {
			String bldCode = TABLE_M.getItemString(i, "BLD_CODE");
			bloodCode += bldCode + "','";//add by sunqy 20140805
			TParm inparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSBldCodeInfo(bldCode)));
			TParm unitparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSUnit(bldCode)));
			if (inparm == null || inparm.getErrCode() < 0) {
				this.messageBox("E0034");
				return;
			}
			parm.addData("BLDCODE_DESC", inparm.getValue("BLDCODE_DESC", 0));
			parm.addData("QTY", TABLE_M.getItemString(i, "TAKE_QTY"));
			parm.addData("UNIT", unitparm.getValue("UNIT_CHN_DESC", 0));
		}
		
		//add by sunqy 20140805=============
		bloodCode = bloodCode.substring(0, bloodCode.length()-3);
		
		/*
		TParm inparmExcept = new TParm(TJDODBTool.getInstance().select(BMSSQL.getBMSBldCodeInfoExcept(bloodCode)));
		for (int i = 0; i < inparmExcept.getCount(); i++) {
			parm.addData("BLDCODE_DESC", inparmExcept.getValue("BLDCODE_DESC", i));
			parm.addData("QTY", 0);
			parm.addData("UNIT", inparmExcept.getValue("UNIT_CHN_DESC", i));
		}
		*/
		//add by sunqy 20140805==============
		parm.setCount(parm.getCount("BLDCODE_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT");
		date.setData("TABLE", parm.getData());
		this.openPrintDialog("%ROOT%\\config\\prt\\BMS\\TakeNo_V45.jhw", date);
	}
	
/**
 * 调阅交叉配血信息	
 */
	public void onFind(){//add by guoy 20151101
		TABLE_APPLY.acceptText();
		TParm parm = TABLE_APPLY.getParmValue();
		TParm findparm = new TParm();
		List<String> list = new ArrayList<String>();
		for(int i = 0 ; i < parm.getCount(); i++){
			String flg = parm.getValue("SELECT_FLG",i);	
			if("Y".equals(flg)){
				list.add(parm.getValue("APPLY_NO",i));
			}
		}
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++){
				findparm.setData("APPLY_NO", list.get(i));
				this.openDialog("%ROOT%\\config\\bms\\BMSBloodCrossDetail.x", findparm);
			}
		}
		else{
			this.messageBox("请选择要调阅交叉配血信息的备血申请！");
		}
	}
	
	
	public void onTableTakeClicked(){
		int row = TABLE_TAKE.getSelectedRow();
		if(row < 0){
			return;
		}
		TABLE_TAKE.acceptText();
		TParm parm = TABLE_TAKE.getParmValue();
		String takeNo = parm.getValue("TAKE_NO", row);
		String orderDrCode = parm.getValue("ORDER_DRCODE", row);
		String orderDrDate = parm.getValue("ORDER_DRDATE", row).replace("-", "/").replace(".0", "");
		
		//除TABLE_TAKE初始化
		{
			
			//获取备血申请单列表
			String applySql = getApplyByCaseNoSql(caseNo);
			TParm applyParm = new TParm(TJDODBTool.getInstance().select(applySql));
			TABLE_APPLY.setParmValue(applyParm);
			
			//初始化开立医生和开立日期
			ORDER_DRCODE.setValue(Operator.getID());
			Timestamp date = StringTool.getTimestamp(new Date());
			ORDER_DRDATE.setValue(date);
			
			TABLE_COUNT.setParmValue(new TParm());
			setValue("BLD_CODE", "");
			setValue("TAKE_QTY","");
			setValue("UNIT_CODE","");
			setValue("BLOOD_TANO","");
		}
		
		setValue("BLOOD_TANO", takeNo);
		setValue("ORDER_DRCODE", orderDrCode);
		setValue("ORDER_DRDATE", orderDrDate);
		TParm p = new TParm(TJDODBTool.getInstance().select(getTakeDByTakeNo(takeNo)));
		TABLE_M.setParmValue(p);
		
	}
	
	public void onQuery(){
		String sql = getTakeByCaseNo(caseNo,this.getValueString("START_DATE"),this.getValueString("END_DATE"));
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		TABLE_TAKE.setParmValue(p);
	}
	
	public boolean checkOut(String takeNo){
		String sql = "SELECT * FROM BMS_BLOOD WHERE TAKE_NO = '"+takeNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() <= 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	public String getPatientByCaseNoSql(String caseNo){
		String sql = "";
		sql += " SELECT " +
					" B.CASE_NO, " +
					" A.MR_NO, " +
					" A.PAT_NAME AS NAME, " +
					" FLOOR (MONTHS_BETWEEN (SYSDATE, A.BIRTH_DATE) / 12)||'岁' AS AGE, " +
					" A.SEX_CODE AS SEX, " +
					" B.BED_NO, " +
					" B.STATION_CODE, " + 
					" B.DEPT_CODE, " +
					" A.BLOOD_TYPE, " +
					" A.BLOOD_RH_TYPE " +
				" FROM " +
					" SYS_PATINFO A, "+
					" ADM_INP B " +
				" WHERE "+
					" A.MR_NO = B.MR_NO " +
					" AND B.CASE_NO = '"+caseNo+"'";
		return sql;
					
	}
	
	public String getTakeByCaseNo(String caseNo,String startDate,String endDate){
		String sql = "";
		sql += " SELECT " +
					" BLOOD_TANO AS TAKE_NO, "+
					" ORDER_DRCODE, "+
					" ORDER_DRDATE "+
				" FROM " +
					" BMS_BLDTAKEM "+
				" WHERE CASE_NO = '"+caseNo+"'";
		if(!"".equals(startDate)){
			sql += " AND ORDER_DRDATE > TO_DATE('" + startDate.replace("-", "").replace(" ", "").replace(".0", "").replace(":", "") + "', 'YYYYMMDDHH24:MI:SS')";
		}
		
		if(!"".equals(endDate)){
			sql += " AND ORDER_DRDATE < TO_DATE('" + endDate.replace("-", "").replace(" ", "").replace(".0", "").replace(":", "") + "', 'YYYYMMDDHH24:MI:SS')";
		}
		
		sql += " ORDER BY ORDER_DRDATE DESC ";
		
		return sql;
				
	}
	
	public String getApplyByCaseNoSql(String caseNo){
		String sql = "";
		sql += " SELECT "+
					" 'N' AS SELECT_FLG, "+
					" APPLY_NO, "+
					" DR_CODE, "+//开立医生
					" APPLY_DATE "+//开立时间
				" FROM " +
					" BMS_APPLYM "+
				" WHERE " +
					" CASE_NO = '"+caseNo+"'"+
				" ORDER BY APPLY_DATE DESC ";
		return sql;
	}
	
	public String getApplySql(List<String> lstApplyNo,String caseNo){
		String sql = "";
		
		String apply = "";
		for(int i = 0 ; i < lstApplyNo.size() ; i++){
			String s = lstApplyNo.get(i);
			apply += "'"+s+"'";
			if(i != lstApplyNo.size()-1){
				apply += ",";
			}
		}
		
		sql += " SELECT "+ 
	    			" A.BLD_CODE, "+
	    			" SUM(A.APPLY_QTY) AS APPLY_QTY, " +
	    			" (SELECT SUM(BLOOD_VOL) FROM BMS_BLOOD B WHERE B.CASE_NO = '"+caseNo+"' AND OUT_NO IS NOT NULL AND B.BLD_CODE = A.BLD_CODE) as TOTAL, " +
	    			" A.UNIT_CODE "+
	    		" FROM "+ 
	    			" BMS_APPLYD A "+ 
	    		" WHERE "+ 
	    			" A.APPLY_NO IN ("+apply+")"+
	    		" GROUP BY A.BLD_CODE,A.UNIT_CODE ";

		
		return sql;
	}
	
	public String getBloodSql(String bloodCode){
		String sql = "";
		sql += " SELECT " +
					" DISTINCT B.UNIT_CHN_DESC,A.UNIT_CODE " +
        	   " FROM " +
        	   		" BMS_BLDSUBCAT A," +
        	   		" SYS_UNIT B " +
        	   " WHERE " +
        	   		" A.BLD_CODE = '" + bloodCode + "' " +
        	   		" AND A.UNIT_CODE = B.UNIT_CODE " +
        	   " ORDER BY A.BLD_CODE ";
		
		return sql;
	}
	
	public String getTakeDByTakeNo(String takeNo){
		String sql = "";
		sql += " SELECT BLD_CODE , APPLY_QTY AS TAKE_QTY , UNIT_CODE FROM BMS_BLDTAKED WHERE BLOOD_TANO = '"+takeNo+"'";
		return sql;
	}
	
	public String getDrSql(String drCode){
		String sql = "";
		sql += "SELECT * FROM SYS_OPERATOR WHERE USER_ID = '"+drCode+"'";
		return sql;
	}
	
	
}
