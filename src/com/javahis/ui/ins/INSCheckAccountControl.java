package com.javahis.ui.ins;

import java.awt.Color;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ins.INSCheckAccountTool;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * Title: 门诊对账
 * 
 * Description:门诊对账：自动对账 总对账 明细对账 对账确认
 * 
 * Copyright: ProperSoft (c) 2011
 * 
 * @author pangben 2012-1-8
 * @version 2.0
 */
public class INSCheckAccountControl extends TControl {
	// private boolean exeProgress=false;//执行自动对账
	private String[] company = {"OPT_USER", "ACCOUNT_DATE", "INS_CROWD_TYPE",
			"INS_PAT_TYPE", "CONFIRM_NO" };
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private TTable tableOpd;// 对总账表
	private TParm checkOutParm = new TParm();// 校验所有选中的数据
	private TParm opdParm;// 总对账下载
	private TTabbedPane tabPanel;// 页签
	private TTable tableOpdOrder;// 对明细帐表
	// private String[] opdTableMap = { "FLG", "CASE_NO", "CONFIRM_NO", "MR_NO",
	// "PAT_NAME", "SEX_CODE", "RECP_TYPE", "INV_NO", "INSAMT_FLG",
	// "REGION_CODE" };// 总账表数据显示值
	private TParm checkOutOpbParm = new TParm();// 明细查询操作使用获得当前操作的数据
	private boolean flg = false;// 判断此操作是否将之前的数据勾选去掉
	private TTable tableRule;// 明细下载显示数据
	private TParm opdOrderParm;// 明细账下载
	private TTable tableAccount;// 门诊对账
	Color red = new Color(255, 0, 0); // 明细下载不同数据颜色
	private TParm regionParm;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		this.callFunction("UI|ACCOUNT_DATE|setValue", SystemTool.getInstance()
				.getDate());
		tableOpd = (TTable) this.getComponent("TABLE_OPD");// 总对账
		tabPanel = (TTabbedPane) this.getComponent("TTABPANEL");// 页签
		tableOpdOrder = (TTable) this.getComponent("TABLE_OPD_ORDER");// 明细对账
		tableRule = (TTable) this.getComponent("TABLE_RULE");// 明细对账下载
		// 总对账复选框事件
		tableOpd.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// 明细对账复选框事件
		tableOpdOrder.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tableAccount = (TTable) this.getComponent("TABLE_ACCOUNT");// 门诊对账
		regionParm = SYSRegionTool.getInstance().selectdata(
	                Operator.getRegion()); // 获得医保区域代码
		//this.setValue("USER_ID", Operator.getID());
		this.setValue("INS_CROWD_TYPE", 1);
		this.setValue("INS_PAT_TYPE", 1);
	}

	/**
	 * 自动对账
	 */
	public void onAutoSave() {
		// 需要自动对账的数据
//		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(
//				new TParm());
//		if (insOpdParm.getErrCode() < 0) {
//			this.messageBox("对账失败");
//			// exeProgress=true;
//			return;
//		}
//		if (insOpdParm.getCount() <= 0) {
//			this.messageBox("没有需要自动对账的数据");
//			// exeProgress=true;
//			return;
//		}
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSChooseVisit.x", new TParm());
//		if (getValue("ACCOUNT_DATE") == null
//				|| getValueString("ACCOUNT_DATE").length() <= 0) {
//			messageBox("请选择对账时间!");
//			return;
//		}
//		// 就诊类型
//		if (!this.emptyTextCheck("INS_CROWD_TYPE,INS_PAT_TYPE")) {
//			return;
//		}
		
//		int insType = getInsType();
//		insOpdParm.setData("INS_TYPE", insType);
//		insOpdParm.setData("ACCOUNT_DATE", getValue("ACCOUNT_DATE"));// 对账时间
//		insOpdParm.setData("OPT_USER", Operator.getID());// 操作人员
//		insOpdParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",0));// 操作人员
//		// insOpdParm
//		// .setData("REGION_CODE", insOpdParm.getValue("REGION_CODE", 0));//
//		// 操作人员
//		parm = new TParm(INSTJReg.getInstance().insOPBAutoAccount(
//				insOpdParm.getData()));
//		if (parm.getErrCode() < 0) {
//			this.messageBox(parm.getErrText());
//			return;
//		}
		//this.messageBox("自动对账成功");
	}

	/**
	 * 获得就诊类别
	 * 
	 * @return
	 */
	private int getInsType() {
		int insType = -1;
		if (this.getValueInt("INS_CROWD_TYPE") == 1
				&& this.getValueInt("INS_PAT_TYPE") == 1) {
			insType = 1;// 城职普通
		} else if (this.getValueInt("INS_CROWD_TYPE") == 1
				&& this.getValueInt("INS_PAT_TYPE") == 2) {
			insType = 2;// 城职门特
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2
				&& this.getValueInt("INS_PAT_TYPE") == 2) {
			insType = 3;// 城居门特
		}
		return insType;
	}

	/**
	 * 查询对总账数据
	 */
	public void onQueryOpd() {
//		if (!checkAutoAccount())
//			return;
		if (!checkOut()) {
			return;
		}
		TParm parm = checkOutParm();
		parm.setData("INSAMT_FLG", 3);// 对账标志
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm result =INSOpdTJTool.getInstance()
				.selectMrNoAccount(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			tableOpd.removeRowAll();
			((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第一个页签全选
			return;
		}
		//System.out.println("result::"+result);
		
		tableOpd.setParmValue(result);
	}

	/**
	 * 校验是否已经自动对账
	 * 
	 * @return
	 */
	private boolean checkAutoAccount() {
		// 需要自动对账的数据
		TParm parm=new TParm();
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(
				parm);
		if (insOpdParm.getErrCode() < 0) {
			this.messageBox("E0005");
			// exeProgress=true;
			return false;
		}
		if (insOpdParm.getCount() > 0) {
			this.messageBox("用户存在未下帐的消费记录");
			// insOpdParm.setErr(-1, "请执行自动对账");
			return false;
		}
		return true;

	}

	/**
	 * 校验查询条件
	 */
	private TParm checkOutParm() {
		TParm parm = new TParm();
		for (int i = 0; i < company.length; i++) {
			if (this.getValueString(company[i]).length() > 0) {
				parm.setData(company[i], this.getValueString(company[i]));
			}
		}

		return parm;
	}

	/**
	 * INS_CROWD_TYPE combox 校验
	 */
	public void onSelType() {
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			this.setValue("INS_PAT_TYPE", 1);
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			this.setValue("INS_PAT_TYPE", 2);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
		}
	}

	/**
	 * 对总账全选操作
	 */
	public void onSelectAllOpd() {
		boolean flg = false;// 判断是否选中
		if (this.getValueBoolean("SELECT_ALL")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableOpd.getParmValue();// 获得总账数据
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableOpd.setParmValue(parm);
	}

	/**
	 * 对明细账全选操作
	 */
	public void onSelectAllOpdOrder() {
		boolean flg = false;// 判断是否选中
		if (this.getValueBoolean("SELECT_ALL_O")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableOpdOrder.getParmValue();// 获得总账数据
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableOpdOrder.setParmValue(parm);
	}
	/**
	 * 判断是否可以对账
	 * @return
	 */
	private boolean getAccount(){
		Date date =new Date();
		String time=date.getHours()+""+date.getMinutes();
		//StringTool.getDate(, "HHmm");
		if (time.compareTo("1500")<0) {
			this.messageBox("请下午三点以后进行对账操作");
			return false;
		}
		return true;
	}
	/**
	 * 对总账下载
	 */
	public void onOpdLoadDown() {
//		if (!getAccount()) {
//			return;
//		}
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// 校验是否可以执行
				int insType = getInsType();
				checkOutParm.setData("INS_TYPE", insType);// 医保就诊类别
				checkOutParm.setData("ACCOUNT_DATE", df1
						.format(getValue("ACCOUNT_DATE")));// 对账时间
				if (null!=this.getValue("OPT_USER")&&this.getValue("OPT_USER").toString().length()>0) {
					checkOutParm.setData("USER_ID", this.getValue("OPT_USER"));// 操作人员
				}
				checkOutParm.setData("PAT_TYPE", this
						.getValueInt("INS_CROWD_TYPE"));// 人员类别

				checkOutParm.setData("REGION_CODE", regionParm.getValue("NHI_NO",0));// 操作人员
				if (insType == 1) {
					checkOutParm.setData("PAY_KIND", "11");// 支付类别PAY_KIND
				} else if (insType == 2) {
					checkOutParm.setData("PAY_KIND", "12");// 支付类别PAY_KIND
				} else if (insType == 3) {
					checkOutParm.setData("PAY_KIND", "22");// 支付类别PAY_KIND
				}
				TParm temParm =checkOutParm();
				temParm.setData("INSAMT_FLG", 3);// 对账标志
				temParm.setData("START_DATE", StringTool.setTime((Timestamp) this
						.getValue("ACCOUNT_DATE"), "00:00:00"));
				temParm.setData("END_DATE", StringTool.setTime((Timestamp) this
						.getValue("ACCOUNT_DATE"), "23:59:59"));
				TParm result = INSOpdTJTool.getInstance().selectSpecialPatAccount(temParm);
				if (result.getErrCode() < 0) {
					this.messageBox("E0005");
					return;
				}
				checkOutParm.setData("specialParm",result.getData());
				opdParm = new TParm(INSTJReg.getInstance().insOpdAccount(
						checkOutParm.getData()));
				if (opdParm.getErrCode() < 0) {
					this.messageBox(opdParm.getErrText());
				} else {
					onSaveOpd();
					//this.messageBox("P0005");
				}
			} else {
				this.messageBox("对账数据不可以执行");
			}
		} else {
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 校验选中的数据是否可以执行 如果需要执行的数据 存在票据不相同 判断是否选中了 所有的数据 通过CAES_NO 管理 不可以跨人操作
	 * 
	 * @return
	 */
	private boolean checkOutExe(TTable table) {
		TParm parm = table.getParmValue();
		// 判断选中的数据是一个CASE_NO还是多个人CASE_NO
		StringBuffer caseNoTemp = new StringBuffer();
		for (int index = 0; index < checkOutParm.getCount("CASE_NO"); index++) {
			if (!caseNoTemp.toString().contains(
					checkOutParm.getValue("CASE_NO", index))) {
				caseNoTemp
						.append(checkOutParm.getValue("CASE_NO", index) + ",");
			}
		}
		String[] caseNos = caseNoTemp.toString().split(",");// caseNo 的个数
		if (caseNos.length <= 0) {
			this.messageBox("没有需要执行的数据");
			return false;
		}
		if (caseNos.length <= 1) {
			return true;
		} else {
			// 校验每一个CASE_NO 是否全部选中
			for (int i = 0; i < caseNos.length; i++) {
				int checkOutCount = 0;// 选中的数据
				int tableCount = 0;// 表格中的数据
				for (int j = 0; j < checkOutParm.getCount("CASE_NO"); j++) {
					if (caseNos[i].equals(checkOutParm.getValue("CASE_NO", j))) {
						checkOutCount++;// 累计选中的数据个数
					}
				}
				for (int j = 0; j < parm.getCount(); j++) {
					if (caseNos[i].equals(parm.getValue("CASE_NO", j))) {
						tableCount++;// 累计表格中个数
					}
				}
				if (checkOutCount == tableCount) {
					continue;
				} else {
					return false;
				}

			}
			return true;

		}

	}

	/**
	 * 校验是否有选中的数据
	 * 
	 * @param table
	 * @return
	 */
	private boolean checkOutOnSelected(TTable table) {
		table.acceptText();
		TParm parm = table.getParmValue();
		checkOutParm = new TParm();
		boolean flg = false;// 校验是否有选中的数据
		// if (parm.getCount() <= 0) {
		// this.messageBox("没有需要下载的数据");
		// return false;
		// }
		// TParm tempParm =parm;
		if (parm==null) {
			return false;
		}
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				flg = true;
				checkOutParm.setRowData(index, parm, i);
				// tempParm.removeRow(i);
				// checkOutParmValue(parm, i);// 需要校验是否跨人操作
				index++;
				// //System.out.println("checkOutParm!!111111::::"+checkOutParm);
			}
		}
		if (flg) {
			checkOutParm.setCount(index);
			if (tabPanel.getSelectedIndex() == 1) {// 此方法需要复用
				// 如果当前是第一个页签将获得的数据保存
				checkOutOpbParm = parm;
			} else {
				checkOutOpbParm = new TParm();
			}
		}

		return flg;
	}

	// /**
	// * 校验选中数据的个数
	// *
	// * @param parm
	// * @param i
	// */
	// private void checkOutParmValue(TParm parm, int i) {
	// for (int j = 0; j < opdTableMap.length; j++) {
	// checkOutParm.addData(opdTableMap[j], parm.getValue(opdTableMap[j],
	// i));
	// }
	// }

	/**
	 * 保存对总账记录
	 */
	public void onSaveOpd() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// 校验是否可以执行
				if (null == opdParm || opdParm.getInt("INSAMT_FLG") != 5
						|| opdParm.getErrCode() < 0) {
					this.messageBox("未下载,不可以保存");
					return;
				}
				if (opdParm.getInt("INSAMT_FLG") == 5) {// 对总账相等
					TParm result = new TParm(INSTJReg.getInstance()
							.insOpdAccountSave(checkOutParm.getData()));
					if (result.getErrCode() < 0) {
						this.messageBox("对账保存失败");
					} else {
						this.messageBox("对账成功");
						onQueryOpd();// 查询
					}
				} else {
					this.messageBox("未下载,不可以保存");
				}

			} else {
				this.messageBox("对账数据不可以执行");
			}
		} else {
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 保存明细对账
	 */
	public void onSaveOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpdOrder)) {
			if (checkOutExe(tableOpdOrder)) {// 校验是否可以执行
				if (null == opdOrderParm || opdOrderParm.getErrCode() < 0) {
					this.messageBox("未下载,不可以保存");
				} else {
					TParm result = new TParm(INSTJReg.getInstance()
							.insOpdOrderAccountSave(checkOutParm.getData()));
					if (result.getErrCode() < 0) {
						this.messageBox("对账保存失败");
					} else {
						this.messageBox("对明细账成功");
						onQueryOpdOrder();// 查询
						tableRule.removeRowAll();
					}
				}

			} else {
				this.messageBox("对账数据不可以执行");
			}
		} else {
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("USER_ID;CONFIRM_NO");
		tableOpd.removeRowAll();// 对总账表
		onClearOpdOrder();
		this.setValue("INS_CROWD_TYPE", 1);// 医保就医类别
		this.setValue("INS_PAT_TYPE", 1);
		callFunction("UI|INS_CROWD_TYPE|setEnabled", true);
		callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		tabPanel.setSelectedIndex(0);// 页签
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第一个页签全选
		checkOutParm = null;// 选中数据
		opdParm = null;// 总对账下载数据
		opdOrderParm = null;// 明细帐下载数据
		tableAccount.removeRowAll();// 门诊对账
		checkOutOpbParm = new TParm();
	}

	/**
	 * 校验数据是否可以执行
	 */
	public void checkOutOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// 校验是否可以执行
				onQueryOpdOrder(checkOutParm, false);
			} else {
				tabPanel.setSelectedIndex(0);
				this.messageBox("对账数据不可以执行");
			}
		} else {
			tabPanel.setSelectedIndex(0);
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 明细帐查询事件
	 */
	public void onQueryOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		onQueryOpdOrder(checkOutOpbParm, true);
	}

	/**
	 * 查询对明细账数据
	 */
	private void onQueryOpdOrder(TParm parm, boolean flg) {
		TParm opdOrderParm = new TParm();
		if (null == parm) {
			return;
		}
		if (flg) {// 点选查询按钮管控 移除 相同的数据
			parm = removeOpdOrderParm(parm);
			if (parm.getCount("CASE_NO") <= 0) {
				this.messageBox("没有查询的数据");
				initShow();
				return;
			}
		}
		//System.out.println("查询明细总数据显示数据:::" + parm);
		int index = 0;
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);
			tempParm.setData("INSAMT_FLG", 3);
			tempParm = INSOpdOrderTJTool.getInstance().selectOpdOrderAccount(
					tempParm);
			if (tempParm.getErrCode() < 0) {
				this.messageBox("查询明细数据失败");
				return;
			}
			// 将查询的数据放入TParm 中
			for (int j = 0; j < tempParm.getCount(); j++) {
				opdOrderParm.setRowData(index, tempParm, j);
				index++;
			}
		}
		opdOrderParm.setCount(index);
		// opdOrderParm.setCount(index+1);
		if (opdOrderParm.getCount("CASE_NO") <= 0) {
			this.messageBox("没有查询的数据");
			initShow();
			return;
		}
		tableOpdOrder.setParmValue(opdOrderParm);
	}

	/**
	 * 没有数据初始设置
	 */
	private void initShow() {
		checkOutOpbParm = new TParm();
		tabPanel.setSelectedIndex(0);
		tableOpdOrder.removeRowAll();
	}

	/**
	 * 移除相同的数据 根据 RECP_TYPE 明细对账使用
	 * 
	 * @param parm
	 */
	private TParm removeOpdOrderParm(TParm parm) {
		// StringBuffer recpType = new StringBuffer();
		// recpType.append(parm.getValue("RECP_TYPE", 0)+",");
		TParm tempParm = new TParm();
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < checkOutParm.getCount(); j++) {
				if (i != getSelectedRow(parm, checkOutParm.getValue("CASE_NO",
						j), checkOutParm.getValue("CONFIRM_NO", j),
						checkOutParm.getValue("SEQ_NO", j))) {
					tempParm.setRowData(index, parm, i);
				}
			}
		}
		tempParm.setCount(index);
		return tempParm;
	}

	/**
	 * 页签点击事件
	 */
	public void onChangeTab() {

		switch (tabPanel.getSelectedIndex()) {
		case 1:
			checkOutOpdOrder();
			break;
		case 0:
			onQueryOpd();
			break;
		}
		tableRule.removeRowAll();// 明细下载显示数据
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第一个页签全选
		// tableOpd.removeRowAll();
	}

	/**
	 * 总对账明细账checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		TParm oldTempParm = new TParm();
		if (tabPanel.getSelectedIndex() == 1
				|| tabPanel.getSelectedIndex() == 0) {
			TParm oldParm = table.getParmValue();
			int index = 0;
			for (int i = 0; i < oldParm.getCount(); i++) {
				if (oldParm.getBoolean("FLG", i)) {
					oldTempParm.setRowData(index, oldParm, i);
					index++;
					flg = true;
				}
			}
			oldTempParm.setCount(index);
		}
		table.acceptText();// 更新数据
		if (tabPanel.getSelectedIndex() == 1) {
			commManager(table, oldTempParm, 1);
			tableRule.removeRowAll();// 明细下载显示数据
			opdOrderParm = null;// 明细帐下载数据
		} else if (tabPanel.getSelectedIndex() == 0) {
			commManager(table, oldTempParm, 0);
		}
		return false;

	}

	/**
	 * 第二个页签选中checkBox管理
	 * 
	 * @param oldTempParm
	 *            对总账操作勾选复选框 不执行将原来勾选数据删除
	 */
	private void commManager(TTable table, TParm oldTempParm, int type) {

		// StringBuffer caseNo = new StringBuffer();
		// 第二个页签对明细账下载 需要管控 不可以跨人操作 已CASE_NO 过滤
		TParm parm = table.getParmValue();
		TParm tempParm = new TParm();// 选中的数据
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			// 累计需要操作的数据
			if (parm.getBoolean("FLG", i)) {
				tempParm.setRowData(index, parm, i);
				index++;
			}
		}
		tempParm.setCount(index);
		TParm newParm = new TParm();
		int newIndex = 0;
		if (type == 0) {// 第一个页签操作
			if (oldTempParm.getCount() < tempParm.getCount()) {
				flg = false;// 不执行已经勾选的数据删除操作
			} else if (oldTempParm.getCount() > tempParm.getCount()) {// 将当前撤销的数据中关联的数据移除
				boolean unFlg = false;// 查询不相同的
				for (int i = 0; i < oldTempParm.getCount(); i++) {
					for (int j = 0; j < tempParm.getCount(); j++) {

						if (tempParm.getValue("CASE_NO", j).equals(
								oldTempParm.getValue("CASE_NO", i))
								&& tempParm.getValue("CONFIRM_NO", j).equals(
										oldTempParm.getValue("CONFIRM_NO", i))
								&& tempParm.getValue("RECP_TYPE", j).equals(
										oldTempParm.getValue("RECP_TYPE", i))) {// 唯一数据判断
							// 如果没有点选已经勾选的数据不执行删除操作
							unFlg = false;
							break;
						}
						flg = true;
						unFlg = true;
					}
					if (unFlg) {
						newParm.setRowData(newIndex, oldTempParm, i);
						newIndex++;
					}
				}
				newParm.setCount(newIndex);
				tempParm = newParm;
			}
		}
		if (flg) {
			// 如果选择的是已经勾选的数据查看此数据是否有关联的，将其它的数据也设置没有勾选状态
			for (int i = 0; i < tempParm.getCount(); i++) {
				for (int j = 0; j < parm.getCount(); j++) {
					if (type == 0) {// 对总账操作
						if (tempParm.getValue("INV_NO", i).equals(
								parm.getValue("INV_NO", j))) {
							parm.setData("FLG", j, "N");
						}
					} else if (type == 1) {// 对明细帐操作
						if (tempParm.getValue("CASE_NO", i).equals(
								parm.getValue("CASE_NO", j))
								&& tempParm.getValue("CONFIRM_NO", i).equals(
										parm.getValue("CONFIRM_NO", j))
								&& tempParm.getValue("SEQ_NO", i).equals(
										parm.getValue("SEQ_NO", j))) {

							parm.setData("FLG", j, "N");
						}
					}
				}
			}
			table.setParmValue(parm);
			flg = false;
			return;
		}
		//System.out.println("总账数据：：：" + parm);
		for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < tempParm.getCount(); j++) {
				if (type == 0) {
					if (tempParm.getValue("INV_NO", j).equals(
							parm.getValue("INV_NO", i))) {
						parm.setData("FLG", i, "Y");
					}
				} else if (type == 1) {
					if (tempParm.getValue("INV_NO", j).equals(
							parm.getValue("INV_NO", i))
							&& tempParm.getValue("RECP_TYPE", j).equals(
									parm.getValue("RECP_TYPE", i))) {
						parm.setData("FLG", i, "Y");
					}
				}

			}
		}
		table.setParmValue(parm);
	}

	/**
	 * 获得选中的行数,明细帐使用
	 * 
	 * @return
	 */
	private int getSelectedRow(TParm parm, String caseNo, String confirmNo,
			String seqNo) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (caseNo.equals(parm.getValue("CASE_NO", i))
					&& confirmNo.equals(parm.getValue("CONFIRM_NO", i))
					&& seqNo.equals(parm.getValue("SEQ_NO", i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 明细帐下载
	 */
	public void onOpdOrderLoadDown() {
//		if (!getAccount()) {
//			return;
//		}
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpdOrder)) {
			if (checkOutExe(tableOpdOrder)) {// 校验是否可以执行
				int insType = getInsType();
				checkOutParm.setData("INS_TYPE", insType);// 医保就诊类别
				checkOutParm.setData("ACCOUNT_DATE", df1
						.format(getValue("ACCOUNT_DATE")));// 对账时间
				checkOutParm.setData("OPT_USER", this.getValue("OPT_USER"));// 操作人员
				//checkOutParm.setData("PAT_TYPE", "11");// 支付类别
				checkOutParm.setData("REGION_CODE", regionParm.getValue(
						"NHI_NO", 0));// 操作人员
				opdOrderParm = INSTJReg.getInstance().insOpdOrderAccount(
						checkOutParm.getData()); // 医保数据
				// onQueryOpdOrder(checkOutParm);
				if (null == opdOrderParm || opdOrderParm.getErrCode() < 0) {
					this.messageBox(opdOrderParm.getErrText());
				} else {
					this.messageBox("P0005");
					// //System.out.println("opdOrderParm::::"+opdOrderParm);
					TParm ownParm = opdOrderParm.getParm("ownParm");// 本地数据
					
					String sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE='"
							+ opdOrderParm.getValue("PIPELINE")
							+ "' AND PLOT_TYPE='"
							+ opdOrderParm.getValue("PLOT_TYPE")
							+ "' AND IN_OUT='OUT' AND COLUMN_NAME NOT IN('PROGRAM_STATE','PROGRAM_MESSAGE') ORDER BY ID";
					TParm insIoParm = new TParm(TJDODBTool.getInstance()
							.select(sql));
					for (int i = 0; i < insIoParm.getCount(); i++) {
						insIoParm.addData("OWN_AMT", ownParm.getValue(insIoParm
								.getValue("COLUMN_NAME", i)));
						insIoParm
								.addData("INS_AMT", opdOrderParm
										.getValue(insIoParm.getValue(
												"COLUMN_NAME", i)));
					}
					tableRule.setParmValue(insIoParm);
					Map map = new HashMap();
					TParm insParm = tableRule.getParmValue();
					// 添加不同数据颜色
					for (int i = 0; i < insParm.getCount(); i++) {
						if (!insParm.getValue("OWN_AMT", i).equals(
								insParm.getValue("INS_AMT", i))) {
							map.put(i, red);
						}

					}
					if (map.size() > 0) {
						tableRule.setRowTextColorMap(map);
					}
				}
				// 下载数据
			} else {
				// tabPanel.setSelectedIndex(0);
				this.messageBox("对账数据不可以执行");
			}
		} else {
			// tabPanel.setSelectedIndex(0);
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 清空总对账数据
	 */
	public void onClearOpd() {
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第一个页签全选
		tableOpd.removeRowAll();
	}

	/**
	 * 清空对明细帐数据
	 */
	public void onClearOpdOrder() {
		tableOpdOrder.removeRowAll();// 对明细帐表
		tableRule.removeRowAll();// 明细下载显示数据
	}

	/**
	 * 结算保存查询操作
	 */
	public void onQueryAccount() {
//		if (!checkAutoAccount())
//			return;
		if (!checkOut()) {
			return;
		}
		TParm parm = checkOutParm();
		// parm.setData("INSAMT_FLG", 5);// 对账标志
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm result = INSOpdTJTool.getInstance().selectSaveAccount(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getInt("INSAMT_FLG", i) != 5) {
				this.messageBox("未执行对总账,请查看");
				tableAccount.removeRowAll();
				tabPanel.setSelectedIndex(0);
				return;
			}

		}
		TParm accParm = getAccountParm(result);
		// //System.out.println("accParm:l:::"+accParm);
		tableAccount.setParmValue(accParm);
	}

	/**
	 * 修改结算,显示数据
	 */
	private TParm getAccountParm(TParm parm) {
		int check_type = getInsType();// 对账类别11城职普通12城职门特22城居门特
		int check = -1;
		switch (check_type) {
		case 1:
			check = 11;
			break;
		case 2:
			check = 12;
			break;
		case 3:
			check = 22;
			break;
		}
		double total_amt = 0.00;// TOTAL_AMT 发生金额
		double nhi_amt = 0.00; // NHI_AMT 申报金额
		double own_amt = 0.00; // 自费金额
		double addpay_amt = 0.00;// 增负金额
		double account_pay_amt = 0.00;// 个人帐户实际支付金额
		double account_pay_amt_b = 0.00; // 个人帐户实际支付金额(退费)
		double apply_amt = 0.00; // 统筹基金申报金额(所有正常)
		double apply_amt_b = 0.00;// 统筹基金社保支付金额(退费)
		double flg_agent_amt = 0.00;// 救助基金申报金额(所有正常)
		double flg_agent_amt_b = 0.00; // 医疗救助支付金额(退费)
		int sum_pertime = 0; // 总人次
		int sum_pertime_b = 0;// 总人次(退费)
		double army_ai_amt = 0.00; // 军残补助金额(所有正常)
		double army_ai_amt_b = 0.00;// 军残补助金额(所有退费)
		double servant_amt = 0.00; // 公务员补助金额(所有正常)
		double servant_amt_b = 0.00;// 公务员补助金额(所有退费)
		double mz_agent_amt = 0.00; // 民政救助补助金额
		double mz_agent_amt_b = 0.00; // 民政救助补助金额(退费)
		double fy_agent_amt = 0.00; // 优抚对象补助金额
		double fy_agent_amt_b = 0.00; // 优抚对象补助金额(退费)
		double fd_agent_amt = 0.00; // 非典后遗症补助金额
		double fd_agent_amt_b = 0.00;// 非典后遗症补助金额(退费)
		double unreim_amt = 0.00; // 基金未报销金额
		double unreim_amt_b = 0.00;// 基金未报销金额(退费)
		double otot_amt = 0.00; // 专项基金社保支付
		double otot_amt_b = 0.00;// 专项基金社保支付(退费)
		TParm accParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			total_amt += parm.getDouble("TOT_AMT", i);
			nhi_amt += parm.getDouble("NHI_AMT", i);
			own_amt += parm.getDouble("OWN_AMT", i);
			addpay_amt += parm.getDouble("ADD_AMT", i);
			// 退费数据累计
			if (parm.getValue("RECP_TYPE", i).equals("REGT")
					|| parm.getValue("RECP_TYPE", i).equals("OPBT")) {
				account_pay_amt += parm.getDouble("ACCOUNT_PAY_AMT", i);
				sum_pertime += parm.getInt("SUM_PERTIME", i);
				apply_amt += parm.getDouble("APPLY_AMT", i);
				flg_agent_amt += parm.getDouble("FLG_AGENT_AMT", i);
				army_ai_amt += parm.getDouble("ARMY_AI_AMT", i);
				servant_amt += parm.getDouble("SERVANT_AMT", i);
				unreim_amt += parm.getDouble("UNREIM_AMT", i);
				otot_amt += parm.getDouble("OTOT_AMT", i);
			} else {
				account_pay_amt_b += parm.getDouble("ACCOUNT_PAY_AMT", i);
				apply_amt_b += parm.getDouble("APPLY_AMT", i);
				flg_agent_amt_b += parm.getDouble("FLG_AGENT_AMT", i);
				sum_pertime_b += parm.getInt("SUM_PERTIME", i);
				army_ai_amt_b += parm.getDouble("ARMY_AI_AMT", i);
				servant_amt_b += parm.getDouble("SERVANT_AMT", i);
				unreim_amt_b += parm.getDouble("UNREIM_AMT", i);
				otot_amt_b += parm.getDouble("OTOT_AMT", i);
			}
		}
		accParm.addData("FLG", "N");
		accParm.addData("CHECK_TYPE", check);
		accParm.addData("TOTAL_AMT", total_amt);
		accParm.addData("NHI_AMT", nhi_amt);
		accParm.addData("OWN_AMT", own_amt);
		accParm.addData("ADDPAY_AMT", addpay_amt);
		accParm.addData("ACCOUNT_PAY_AMT", account_pay_amt);
		accParm.addData("ACCOUNT_PAY_AMT_B", account_pay_amt_b);

		accParm.addData("APPLY_AMT", apply_amt);
		accParm.addData("APPLY_AMT_B", apply_amt_b);
		accParm.addData("FLG_AGENT_AMT", flg_agent_amt);
		accParm.addData("FLG_AGENT_AMT_B", flg_agent_amt_b);
		accParm.addData("SUM_PERTIME", sum_pertime);
		accParm.addData("SUM_PERTIME_B", sum_pertime_b);
		accParm.addData("ARMY_AI_AMT", army_ai_amt);
		accParm.addData("ARMY_AI_AMT_B", army_ai_amt_b);
		accParm.addData("SERVANT_AMT", servant_amt);
		accParm.addData("SERVANT_AMT_B", servant_amt_b);
		accParm.addData("MZ_AGENT_AMT", mz_agent_amt);
		accParm.addData("MZ_AGENT_AMT_B", mz_agent_amt_b);
		accParm.addData("FY_AGENT_AMT", fy_agent_amt);
		accParm.addData("FY_AGENT_AMT_B", fy_agent_amt_b);
		accParm.addData("FD_AGENT_AMT", fd_agent_amt);
		accParm.addData("FD_AGENT_AMT_B", fd_agent_amt_b);
		accParm.addData("UNREIM_AMT", unreim_amt);
		accParm.addData("UNREIM_AMT_B", unreim_amt_b);
		accParm.addData("OTOT_AMT", otot_amt);
		accParm.addData("OTOT_AMT_B", otot_amt_b);
		accParm.addData("INSAMT_FLG", "5");
		accParm.setCount(1);
		return accParm;
	}

	/**
	 * 结算保存操作
	 */
	public void onAccountSave() {
		tableAccount.acceptText();
		TParm parm = tableAccount.getParmValue();
		//System.out.println("结算数据parm:::::" + parm);
		if (parm.getCount() <= 0) {
			this.messageBox("没有查询数据");
			return;
		}
		boolean flg = false;// 判断是否有需要执行的数据
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				flg = true;
				break;
			}
		}
		TParm accountParm = checkOutParm();
		// parm.setData("INSAMT_FLG", 5);// 对账标志
		accountParm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		accountParm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));

		if (flg) {
			accountParm.setData("CHECK_DATE", df1.format(this
					.getValue("ACCOUNT_DATE")));
			// 查询是否存在数据 根据日期查询
			TParm checkAccountParm = INSCheckAccountTool.getInstance()
					.queryInsCheckAccount(accountParm);
			if (checkAccountParm.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}
			if (checkAccountParm.getCount() > 0) {
				this.messageBox("已存在数据");
				return;
			}
			parm = parm.getRow(0);
			//System.out.println("parm::::" + parm);
			parm.setData("CHECK_DATE", df1
					.format(this.getValue("ACCOUNT_DATE")));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = INSCheckAccountTool.getInstance()
					.insertInsCheckAccount(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
			}

		} else {
			this.messageBox("请选择数据");
		}
	}

	/**
	 * 校验数据
	 * 
	 * @return
	 */
	private boolean checkOut() {
		if (getValue("ACCOUNT_DATE") == null
				|| getValueString("ACCOUNT_DATE").length() <= 0) {
			messageBox("请选择对账时间!");
			return false;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE,INS_PAT_TYPE")) {
			return false;
		}
		return true;
	}

	public void onClearAccount() {
		tableAccount.removeRowAll();
	}

}
