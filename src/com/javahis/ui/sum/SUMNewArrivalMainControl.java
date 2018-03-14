package com.javahis.ui.sum;

import java.awt.Color;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.ArrayList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDS;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sys.Pat;
import jdo.sys.Operator;
import jdo.sys.PatTool;

/**
 * <p>
 * Title: 新生儿体温单
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: javahis
 * </p>
 * 
 * @author ZangJH
 * 
 * @version 1.0
 */
public class SUMNewArrivalMainControl extends TControl {

	TTable masterTable; // 体温记录table（主）
	TTable detailTable; // 体温明细table（细）
	int masterRow = -1;// 主table选中行号
	int detailRow = -1;// 细table选中行号
	TParm patInfo = new TParm(); // 新生儿信息
	TParm patMotherInfo = new TParm(); // 新生儿母亲信息
	String admType = "I";// 门急住别
	String caseNo = ""; // 就诊号
	TParm tprDtl = new TParm(); // 体温表数据
	TParm inParm = new TParm();// 入参

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		masterTable = (TTable) this.getComponent("masterTable");// 初始化组件
		detailTable = (TTable) this.getComponent("detailTable");
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");// table点击事件
		inParm = this.getInputParm();
		if (inParm != null) {
			admType = inParm.getValue("SUM", "ADM_TYPE");
			caseNo = inParm.getValue("SUM", "CASE_NO");
			onQuery();
		}
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// 记录时间
		this.setValue("TMPTRKINDCODE", "3");// 体温种类：肛温
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		patInfo = ADMTool.getInstance().getADM_INFO(parm); // 新生儿信息
		patInfo.setData("ADM_DAYS", 0, ADMTool.getInstance().getAdmDays(caseNo));
		patMotherInfo = ADMTool.getInstance().getMotherInfo(parm); // 新生儿母亲信息
		TParm result = SUMNewArrivalTool.getInstance().selectExmDateUser(parm);// 体温记录
		// 初始化体温记录table
		for (int row = 0; row < result.getCount(); row++) {
			result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool
					.getDate(result.getValue("EXAMINE_DATE", row), "yyyyMMdd"),
					"yyyy/MM/dd"));
			if ((row + 1) % 7 == 0) {// 每七天设置一个黄颜色
				masterTable.setRowColor(row - 1, new Color(255, 255, 132));
			}
		}
		masterTable.getTable().repaint();
		masterTable.setParmValue(result);
		if (masterTable.getRowCount() > 0) {
			masterTable.setSelectedRow(masterTable.getRowCount() - 1); // 默认选中最后一行
			onMasterTableClicked(masterTable.getRowCount() - 1); // 手动执行单击事件
		}
	}

	/**
	 * 行转列
	 * 
	 * @param tprDtl
	 *            TParm
	 * @return TParm
	 */
	public TParm rowToColumn(TParm tprDtl) {
		TParm result = new TParm();
		for (int i = 0; i < tprDtl.getCount(); i++) {
			result.addData("" + i, tprDtl.getData("TEMPERATURE", i)); // 体温
			result.addData("" + i, tprDtl.getData("WEIGHT", i)); // 体重
		}
		return result;
	}

	/**
	 * 新建
	 */
	public void onNew() {
		// 拿到服务器/数据库当前时间
		String today = StringTool.getString(TJDODBTool.getInstance()
				.getDBTime(), "yyyyMMdd");
		for (int i = 0; i < masterTable.getRowCount(); i++) {
			if (today.equals(StringTool.getString(
					StringTool.getDate(
							masterTable.getItemString(i, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd"))) {
				this.messageBox("已存在今天数据\n不可以新建");
				return;
			}
		}
		// 插入一条数据
		TParm MData = new TParm();
		MData.setData("EXAMINE_DATE",
				today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
						+ today.substring(6));
		MData.setData("USER_ID", Operator.getName());
		// 默认选中新增行
		int newRow = masterTable.addRow(MData);
		masterTable.setSelectedRow(newRow);
		onMasterTableClicked(newRow);// 手动执行单击事件
		// 新建的时候写入当前时间
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// 住院天数
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "3");// 体温种类：肛温
	}

	/**
	 * 体温记录table单击事件
	 */
	public void onMasterTableClicked(int row) {
		if (row == 0) { // 当只有点中出生的那天(第一天)‘出生体重’才可编辑
			this.callFunction("UI|BORNWEIGHT|setEnabled", true);
		} else {
			this.callFunction("UI|BORNWEIGHT|setEnabled", false);
		}
		masterRow = row; // 主table选中行号
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(
				masterTable.getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"),
				"yyyyMMdd"));
		// =========================主表数据
		TParm master = SUMNewArrivalTool.getInstance().selectOneDateDtl(parm);
		this.clearComponent();// 清空组件
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0)); // 住院天数
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "3");// 体温种类：肛温
		// =========================细表数据
		tprDtl = SUMNewArrivalTool.getInstance().selectOneDateDtl(parm);
		if (tprDtl.getCount() == 0) {
			detailTable.removeRowAll();
			detailTable.addRow();
			detailTable.addRow();
			// return;
		} else {
			detailTable.setParmValue(rowToColumn(tprDtl));
		}
		// =========================细表下面的组件
		// if (master.getInt("BORNWEIGHT", 0) > 0) {
		this.setValue("BORNWEIGHT", master.getData("BORNWEIGHT", 0));// 出生体重
		// } else {
		// Pat pat = Pat.onQueryByMrNo(patInfo.getValue("MR_NO", 0));// wanglong
		// add 20130504 泰心目前无法取出生体重，暂时注释掉
		// this.setValue("BORNWEIGHT", pat.getNewBodyWeight());// 出生体重
		// }
		this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// 小便次数
		this.setValue("DRAINTIMES", master.getValue("DRAINTIMES", 0));// 大便次数
		this.setValue("DRAINQUALITY", master.getValue("DRAINQUALITY", 0));// 大便性质
		this.setValue("DRINKQTY", master.getValue("DRINKQTY", 0));// 饮水量
		this.setValue("FEEDWAY", master.getValue("FEEDWAY", 0));// 喂养方法
		this.setValue("ADDDARIYQTY", master.getValue("ADDDARIYQTY", 0));// 加乳品及量
		this.setValue("VOMIT", master.getValue("VOMIT", 0));// 呕吐
		this.setValue("BATHEDWAY", master.getValue("BATHEDWAY", 0));// 淋浴方法
		this.setValue("EYE", master.getValue("EYE", 0));// 眼睛
		this.setValue("EAR_NOSE", master.getValue("EAR_NOSE", 0));// 鼻耳
		this.setValue("UNBILICAL", master.getValue("UNBILICAL", 0));// 脐带
		this.setValue("BUTTRED", master.getValue("BUTTRED", 0));// 臀红
		this.setValue("ICTERUSINDEX", master.getValue("ICTERUSINDEX", 0));// 黄疸指数
		this.setValue("ELES", master.getValue("ELES", 0));// 其他
	}

	/**
	 * 体温明细table单击事件(通过界面注册法)
	 */
	public void onDTableFocusChange() {
		// PS:由于存在矩阵转换的问题，所以选中的列为数据PARM的行
		// 初始化细table被选中的行（即是table的列号）
		detailRow = detailTable.getSelectedColumn();
		((TComboBox) this.getComponent("EXAMINESESSION"))
				.setSelectedIndex(detailRow + 1);
		this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// 记录时间
		if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			this.setValue("RECTIME", now);// 记录时间
		}
		this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// 体温变化特殊情况
		this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// 物理降温
		this.setValue("TMPTRKINDCODE",
				tprDtl.getValue("TMPTRKINDCODE", detailRow));// 体温种类
		if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
			this.setValue("TMPTRKINDCODE", "3");// 体温种类：肛温
		}
		this.setValue("NOTPRREASONCODE",
				tprDtl.getValue("NOTPRREASONCODE", detailRow));// 未量原因
		this.setValue("PTMOVECATECODE",
				tprDtl.getValue("PTMOVECATECODE", detailRow));// 病人动态
		this.setValue("PTMOVECATEDESC",
				tprDtl.getValue("PTMOVECATEDESC", detailRow));// 病人动态附注
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		masterTable.acceptText();
		detailTable.acceptText();
		if (masterRow < 0) {
			this.messageBox("请选择一条记录！");
			return false;
		}
		TParm saveParm = getValueFromUI();
		if (saveParm.getErrCode() < 0) {
			this.messageBox(saveParm.getErrText());
			return false;
		}
		// 判断是否已有该数据插入/更新
		String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
		// 得到左边table的数据
		TParm checkDate = new TParm();
		checkDate.setData("CASE_NO", caseNo);
		checkDate.setData("ADM_TYPE", admType);
		checkDate.setData("EXAMINE_DATE", saveDate);
		TParm existParm = SUMNewArrivalTool.getInstance().checkIsExist(
				checkDate);
		// 没有改天数据，直接保存
		if (existParm.getCount() == 0) {// 不存在记录，新建
			saveParm.setData("I", true);
			// 调用action执行事务
			TParm result = TIOM_AppServer.executeAction(
					"action.sum.SUMNewArrivalAction", "onSave", saveParm);
			// 调用保存
			if (result.getErrCode() < 0) {
				this.messageBox_("保存失败 " + result.getErrText());
				return false;
			}
			this.messageBox("P0001");
			// 电视屏接口 wanglong add 20150527
			TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
			}
			return true;
		}
		// 不是插入--update
		saveParm.setData("I", false);
		// 不等于0说明已经有存在的改天数据了--作废、没作废--更新动作
		if (existParm.getData("DISPOSAL_FLG", 0) != null
				&& existParm.getData("DISPOSAL_FLG", 0).equals("Y")) {
			if (0 == this.messageBox("", "该数据已经作废过，\n是否在确定保存？",
					this.YES_NO_OPTION)) {
				// 调用action执行事务
				TParm result = TIOM_AppServer.executeAction(
						"action.sum.SUMNewArrivalAction", "onSave", saveParm);
				// 调用保存
				if (result.getErrCode() < 0) {
					this.messageBox_("保存失败 " + result.getErrText());
					return false;
				}
				this.messageBox("P0001");
				// 电视屏接口 wanglong add 20150527
				TParm xmlParm = ADMXMLTool.getInstance()
						.creatPatXMLFile(caseNo);
				if (xmlParm.getErrCode() < 0) {
					this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
				}
				return true;
			} else {
				this.messageBox("没有更新数据！");
				return true;
			}
		}
		// 直接诶更新--DISPOSAL_FLG==null或者N
		TParm result = TIOM_AppServer.executeAction(
				"action.sum.SUMNewArrivalAction", "onSave", saveParm);
		// 调用保存
		if (result.getErrCode() < 0) {
			this.messageBox_("保存失败 " + result.getErrText());
			return false;
		}
		this.messageBox("P0001");
		if (masterRow == 0
				&& saveParm.getParm("MASET").getDouble("BORNWEIGHT") != 0) {
			String sql = "UPDATE SUM_NEWARRIVALSIGN SET BORNWEIGHT = # WHERE ADM_TYPE = '@' AND CASE_NO = '&'";
			sql = sql.replaceFirst("#",
					saveParm.getParm("MASET").getValue("BORNWEIGHT"));
			sql = sql.replaceFirst("@",
					saveParm.getParm("MASET").getValue("ADM_TYPE"));
			sql = sql.replaceFirst("&",
					saveParm.getParm("MASET").getValue("CASE_NO"));
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		// 电视屏接口 wanglong add 20150527
		TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
		}
		return true;
	}

	/**
	 * 保存：从控件上面获得值
	 */
	public TParm getValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// 检查日期
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// 住院天数
		masterParm.setData("OPE_DAYS", "");// 术后天数（暂时没用）
		masterParm.setData("ECTTIMES", "");// 目前ECT次数（暂时没用）
		masterParm.setData("DISPOSAL_FLG", ""); // 如果以后改为不可以为NULL就可以改成--“N”再修改对应的SQL
		masterParm.setData("DISPOSAL_REASON", "");
		masterParm.setData("USER_ID", Operator.getID());// 记录人员
		masterParm.setData("BORNWEIGHT", this.getValueDouble("BORNWEIGHT"));// 出生体重
		masterParm.setData("URINETIMES", this.getValueDouble("URINETIMES"));// 小便次数
		masterParm.setData("DRAINTIMES", this.getValueDouble("DRAINTIMES"));// 大便次数
		masterParm.setData("DRAINQUALITY", this.getValue("DRAINQUALITY"));// 大便性质
		masterParm.setData("DRINKQTY", this.getValueDouble("DRINKQTY"));// 饮水量
		masterParm.setData("FEEDWAY", this.getValueString("FEEDWAY"));// 喂养方法
																		// wanglong
																		// modify
																		// 20141028
		masterParm.setData("ADDDARIYQTY", this.getValueDouble("ADDDARIYQTY"));// 加乳品及量
		masterParm.setData("VOMIT", this.getValueDouble("VOMIT"));// 呕吐
		masterParm.setData("BATHEDWAY", this.getValue("BATHEDWAY"));// 淋浴方法
		masterParm.setData("EYE", this.getValue("EYE"));// 眼睛
		masterParm.setData("EAR_NOSE", this.getValue("EAR_NOSE"));// 鼻耳
		masterParm.setData("UNBILICAL", this.getValue("UNBILICAL"));// 脐带
		masterParm.setData("BUTTRED", this.getValue("BUTTRED"));// 臀红
		masterParm.setData("ICTERUSINDEX", this.getValue("ICTERUSINDEX"));// 黄疸指数
		masterParm.setData("ELES", this.getValue("ELES"));// 其他
		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		// 选中的时间段
		String columnIndex = this.getValueString("EXAMINESESSION");
		// this.messageBox("" + columnIndex);
		// add by wukai on 20170213
		String weight = null;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setRoundingMode(RoundingMode.UP);
		for (int i = 0; i < 6; i++) {// 时段有6个
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// 记录时间
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// 物理降温
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// 体温种类
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// 病人动态
				if (!StringUtil.isNullString(this
						.getValueString("PTMOVECATECODE"))
						&& StringUtil.isNullString(this
								.getValueString("PTMOVECATEDESC"))) {
					TParm errParm = new TParm();
					errParm.setErr(-1, "请填写病人动态附注");
					return errParm;
				}
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// 病人动态附注
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// 记录时间
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// 物理降温
				oneParm.setData("TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("")// wanglong
																		// modify
																		// 20140428
						? this.getValue("TMPTRKINDCODE")
								: tprDtl.getValue("TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// 病人动态
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// 病人动态附注
			}
			// 得到table上的主数据
			oneParm.setData("TEMPERATURE", TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// 体温
			// alert by wukai on 20170213 体重保留两位小数,自动四舍五入
			//oneParm.setData("WEIGHT",  TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// 体重
			weight = detailTable.getValueAt(1, i) + "";
			if(!StringUtils.isEmpty(weight) && !"null".equals(weight)) {
				try {
					oneParm.setData("WEIGHT", nf.parse(weight).doubleValue());
				} catch (ParseException e) {
					e.printStackTrace();
					oneParm.setData("WEIGHT", TCM_Transform.getDouble(0));
				}
			} else {
				oneParm.setData("WEIGHT",  TCM_Transform.getDouble(detailTable.getValueAt(1, i)));
			}
			
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// 重新设置全局变量
		masterRow = -1;
		detailRow = -1;
		this.clearComponent();
		detailTable.removeAll();
		onQuery();// 执行查询
	}

	/**
	 * 清空组件
	 */
	public void clearComponent() {
		// 清理上半部分
		this.clearValue("EXAMINESESSION;RECTIME;"// INHOSPITALDAYS住院天数不清空
				+ "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE体温种类不清空
				+ "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
		// 清理下半部分
		this.clearValue("BORNWEIGHT;URINETIMES;DRAINTIMES;DRAINQUALITY;DRINKQTY;FEEDWAY;ADDDARIYQTY;VOMIT;"
				+ "BATHEDWAY;EYE;EAR_NOSE;UNBILICAL;BUTTRED;ICTERUSINDEX;ELES");
	}

	/**
	 * 作废数据
	 */
	public void onDefeasance() {
		int selRow = masterTable.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中作废数据！");
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
		// 得到选中行的EXAMINE_DATE
		String examineDate = StringTool.getString(
				StringTool.getDate(
						masterTable.getItemString(selRow, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd");
		String defSel = "SELECT * FROM SUM_NEWARRIVALSIGN WHERE ADM_TYPE = '"
				+ admType + "' AND CASE_NO = '" + caseNo
				+ "' AND EXAMINE_DATE = '" + examineDate + "'";
		TDS defData = new TDS();
		defData.setSQL(defSel);
		defData.retrieve();
		defData.setItem(0, "DISPOSAL_REASON", value);
		defData.setItem(0, "DISPOSAL_FLG", "Y");
		if (!defData.update()) {
			this.messageBox("作废失败！");
			return;
		}
		this.messageBox("作废成功！");
		onClear();
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (masterTable.getRowCount() <= 0) {
			this.messageBox("没有打印数据！");
			return;
		}
		TParm printData = getValueForPrt(); // 整理打印参数
		if (printData.getData("STOP") != null) {
			this.messageBox(printData.getValue("STOP"));
			return;
		}
		this.openPrintDialog(
				"%ROOT%\\config\\prt\\sum\\SUMNewArrival_PrtSheet.jhw",
				printData);
	}

	/**
	 * 得到UI上的参数给打印程序
	 * 
	 * @return TPram
	 */
	private TParm getValueForPrt() {
		TParm prtForSheetParm = new TParm();
		// 获得打印的区间
		TParm parmDate = new TParm();
		// 入院日期时间
		Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
		parmDate.setData("IN_DATE", inDate);
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
		if (value == null) {
			prtForSheetParm.setData("STOP", "取消打印！");
			return prtForSheetParm;
		}
		// 得到选择时间之间的‘天数差’+1===>打印的天数
		int differCount = StringTool.getDateDiffer(
				value.getTimestamp("END_DATE"),
				value.getTimestamp("START_DATE")) + 1;
		if (differCount <= 0) {
			prtForSheetParm.setData("STOP", "查询区域错误！");
			return prtForSheetParm;
		}
		// 获得生命标记数据
		Vector tprSign = getVitalSignDate(value);
		// 打印核心算法，将数据转化成坐标
		prtForSheetParm = dataToCoordinate(tprSign, differCount);
		String stationCode = patInfo.getValue("STATION_CODE", 0);
		String mrNo = patInfo.getValue("MR_NO", 0);
		// 通过MR_NO拿到性别
		Pat pat = Pat.onQueryByMrNo(mrNo);
		String sex = pat.getSexString();
		String ipdNo = patInfo.getValue("IPD_NO", 0);
		String bedNo = patInfo.getValue("BED_NO", 0);
		String motherName = (String) patMotherInfo.getValue("PAT_NAME", 0);
		String dept = patInfo.getValue("DEPT_CODE", 0);
		prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
		prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
		prtForSheetParm.setData("BED_NO", "TEXT", bedNo);
		prtForSheetParm.setData("STATION", "TEXT", StringUtil.getDesc(
				"SYS_STATION", "STATION_DESC", "STATION_CODE='" + stationCode
						+ "'"));
		prtForSheetParm.setData("NAME", "TEXT", motherName);
		prtForSheetParm.setData("SEX", "TEXT", sex);
		prtForSheetParm.setData(
				"DEPT",
				"TEXT",
				StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='"
						+ dept + "'"));
		prtForSheetParm.setData(
				"PAT_NAME",
				"TEXT",
				StringUtil.getDesc("SYS_PATINFO", "PAT_NAME", "MR_NO='" + mrNo
						+ "'"));
		return prtForSheetParm;
	}

	/**
	 * 得到需要打印的主数据
	 * 
	 * @param date
	 *            TParm
	 */
	public Vector getVitalSignDate(TParm date) {
		Vector tprSign = new Vector();
		date.setData("ADM_TYPE", admType);
		date.setData("CASE_NO", caseNo);
		// 体温，体重数据
		TParm newArrivalMstParm = SUMNewArrivalTool.getInstance()
				.selectdataMst(date);
		TParm newArrivalDtlParm = SUMNewArrivalTool.getInstance()
				.selectdataDtl(date);
		// 生命标记结果：0-主表信息 1-细表信息
		tprSign.add(newArrivalMstParm);
		tprSign.add(newArrivalDtlParm);
		return tprSign;
	}

	/**
	 * 打印核心算法，将数据转化成坐标
	 * 
	 * @param tprSign
	 *            Vector 主要数据
	 * @param differCount
	 *            int 要打印的天数（endDate-startDate）
	 */
	public TParm dataToCoordinate(Vector tprSign, int differCount) {
		TParm mainPrtData = new TParm();
		// 得到第一天的数据作为标准
		String getFistDateSQL = "SELECT * FROM SUM_NEWARRIVALSIGN WHERE ADM_TYPE='"
				+ admType
				+ "' AND CASE_NO='"
				+ caseNo
				+ "' AND BORNWEIGHT IS NOT NULL"; // 出生体重不为空的
		TParm firstData = new TParm(TJDODBTool.getInstance().select(
				getFistDateSQL));
		// 出生时间
		String mrNo = StringUtil.getDesc("ADM_INP", "MR_NO", "CASE_NO='"
				+ caseNo + "'");
		Timestamp bornDate = PatTool.getInstance().getInfoForMrno(mrNo)
				.getTimestamp("BIRTH_DATE", 0);// wanglong modify 20150225
		// StringTool.getTimestamp(firstData.getValue("EXAMINE_DATE", 0),
		// "yyyyMMdd");
		// 得到出生的体重--基准体重
		int bornWeight = firstData.getInt("BORNWEIGHT", 0);
		// 主细表数据
		TParm master = (TParm) tprSign.get(0); // 下面的数据
		TParm detail = (TParm) tprSign.get(1); // (点线)
		// 得到报表下面的数据
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0, c11 = 0, c12 = 0, c13 = 0, c14 = 0;
		// 计数依次拿出常量
		int countWord = 0;
		// 如果选择区间的天数>数据/6说明有作废数据，以数据/6为“新”天数--体重的有效数据/6
		int newDates = detail.getCount("WEIGHT") / 6;
		if (differCount > newDates)
			differCount = newDates;
		// 根据（天数/7）得到需要花的总页数
		int pageCount = differCount / 7 + 1;
		TParm controlPage = new TParm();
		// 外层控制页
		for (int i = 1; i <= pageCount; i++) {
			ArrayList dotList_T = new ArrayList();
			ArrayList dotList_W = new ArrayList();
			ArrayList dotList_P = new ArrayList();
			ArrayList lineList_W = new ArrayList();
			ArrayList lineList_T = new ArrayList();
			ArrayList lineList_P = new ArrayList();
			// 设置页数
			controlPage.addData("PAGE", "" + i);
			// 嵌套子循环控制天----------------------start-------------------------
			int date = differCount - (i * 7) % 7;
			int xT = -1;
			int yT = -1;
			int xW = -1;
			int yW = -1;
			for (int j = 1; j <= date; j++) {
				// 最内层控制时段
				for (int exa = 1; exa <= 6; exa++) {
					// 得到体温-------------------start---------------------------
					double temper = detail.getDouble("TEMPERATURE", exa
							+ (j - 1) * 6 - 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					if (temper == 0.0)
						continue;
					// 当温度<=35的时候写“体温不升”
					if (temper <= 35) {
						// 最低边界35度
						temper = 35;
						controlPage.addData("NORAISE" + (exa + (j - 1) * 6),
								"体温不升");
					}
					// 得到体温的横纵坐标（点）
					int temperHorizontal = countHorizontal(j, exa);
					int temperVertical = (int) (getVertical(temper) + 0.5); // 取整
					// 得到一个点的坐标
					int dataTemper[] = new int[] { temperHorizontal,
							temperVertical, 6, 6, 4 };
					// 存入所有点
					dotList_T.add(dataTemper);
					// --------------------------end-----------------------------
					// 得到体重的点----------------start--------------------------
					int weight = detail.getInt("WEIGHT", exa + (j - 1) * 6 - 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					if (weight == 0)
						continue;
					// 得到体重的横纵坐标（点）
					int weightHorizontal = countHorizontal(j, exa);
					int weightVertical = (int) (getVertical(weight, bornWeight) + 0.5); // 取整
					// 得到一个点的坐标
					int dataWeight[] = new int[] { weightHorizontal,
							weightVertical, 6, 6, 4 };
					// 存入所有点
					dotList_W.add(dataWeight);
					// ---------------------------end----------------------------
					// 得到物理降温-----------------start--------------------------
					String tempPhsi = detail.getValue("PHYSIATRICS", exa
							+ (j - 1) * 6 - 1);
					if (!StringUtil.isNullString(tempPhsi)) {
						// 得到数字类型的
						double phsiatrics = TCM_Transform.getDouble(tempPhsi);
						if (phsiatrics <= 35) {
							// 最低边界35度
							phsiatrics = 35;
						}
						// 得到体温的横纵坐标（点）
						int phsiHorizontal = countHorizontal(j, exa);
						int phsiVertical = (int) (getVertical(phsiatrics) + 0.5); // 取整
						// 得到一个点的坐标
						int dataPhsi[] = new int[] { phsiHorizontal,
								phsiVertical, 6, 6, 6 };
						// 存入所有点
						dotList_P.add(dataPhsi);
						// 得到物理降温线-----------start--------------------------
						int dataTempLine[] = new int[] { temperHorizontal + 3,
								temperVertical + 3, phsiHorizontal + 3,
								phsiVertical + 3, 1 };
						lineList_P.add(dataTempLine);
					}
					// ----------------------------end---------------------------
					// 得到为测量原因
					String not = nullToEmptyStr(detail.getValue(
							"NOTPRREASONCODE", (exa + (j - 1) * 6) - 1));
					// 得到体温的线----------------start--------------------------
					if (xT != -1 && yT != -1 && StringUtil.isNullString(not)) {
						int dataTempLine[] = new int[] { xT + 3, yT + 3,
								temperHorizontal + 3, temperVertical + 3, 1 };
						lineList_T.add(dataTempLine);
					}
					xT = temperHorizontal;
					yT = temperVertical;
					// --------------------------end----------------------------
					// 得到体重的线----------------start--------------------------
					if (xW != -1 && yW != -1 && StringUtil.isNullString(not)) {
						int dataWeightLine[] = new int[] { xW + 3, yW + 3,
								weightHorizontal + 3, weightVertical + 3, 1 };
						lineList_W.add(dataWeightLine);
					}
					xW = weightHorizontal;
					yW = weightVertical;
					// --------------------------end----------------------------
					// 病人动态信息----------------start--------------------------
					String ptMoveCode = nullToEmptyStr(detail.getValue(
							"PTMOVECATECODE", exa + (j - 1) * 6 - 1));
					if (!StringUtil.isNullString(ptMoveCode)) {
						String ptMoveDesc = nullToEmptyStr(detail.getValue(
								"PTMOVECATEDESC", exa + (j - 1) * 6 - 1));
						controlPage.addData("MOVE" + (exa + (j - 1) * 6),
								ptMoveCode + "||" + ptMoveDesc);
					}
				}
				// 得到日期-------------------------start-------------------------
				String tenmpDate = master.getValue("EXAMINE_DATE", countWord++);
				String fomatDate = tenmpDate.substring(0, 4) + "-"
						+ tenmpDate.substring(4, 6) + "-"
						+ tenmpDate.substring(6);
				controlPage.addData("DATE" + j, fomatDate);
				// 得到该出生天数（该日子-出生日子）-------------------------------
				// int dates = getBornDateDiffer(tenmpDate, bornDate);
				String dates = getBornDateDiffer(tenmpDate, bornDate);
				// controlPage.addData("BRON" + j, dates == 0 ? "" : dates);
				controlPage.addData("BRON" + j, dates);
				// 得到报表下面的数据----------------------start------------------------
				controlPage.addData("L1" + j,
						master.getData("URINETIMES", c1++));
				controlPage.addData("L2" + j,
						master.getData("DRAINTIMES", c2++));
				controlPage.addData("L3" + j,
						master.getData("DRAINQUALITY", c3++));
				controlPage.addData("L4" + j, master.getData("DRINKQTY", c4++));
				controlPage.addData("L5" + j, master.getData("FEEDWAY", c5++));
				controlPage.addData("L6" + j,
						master.getData("ADDDARIYQTY", c6++));
				controlPage.addData("L7" + j, master.getData("VOMIT", c7++));
				controlPage
						.addData("L8" + j, master.getData("BATHEDWAY", c8++));
				controlPage.addData("L9" + j, master.getData("EYE", c9++));
				controlPage.addData("L10" + j,
						master.getData("EAR_NOSE", c10++));
				controlPage.addData("L11" + j,
						master.getData("UNBILICAL", c11++));
				controlPage
						.addData("L12" + j, master.getData("BUTTRED", c12++));
				controlPage.addData("L13" + j,
						master.getData("ICTERUSINDEX", c13++));
				controlPage.addData("L14" + j, master.getData("ELES", c14++));
			}
			// 体温点
			int pageDataForT[][] = new int[dotList_T.size()][5];
			for (int j = 0; j < dotList_T.size(); j++)
				pageDataForT[j] = (int[]) dotList_T.get(j);
			// 体重点
			int pageDataForW[][] = new int[dotList_W.size()][5];
			for (int j = 0; j < dotList_W.size(); j++)
				pageDataForW[j] = (int[]) dotList_W.get(j);
			// 物理降温点
			int pageDataForP[][] = new int[dotList_P.size()][5];
			for (int j = 0; j < dotList_P.size(); j++)
				pageDataForP[j] = (int[]) dotList_P.get(j);
			// 体温线
			int pageDataForTLine[][] = new int[lineList_T.size()][5];
			for (int j = 0; j < lineList_T.size(); j++)
				pageDataForTLine[j] = (int[]) lineList_T.get(j);
			// 体重线
			int pageDataForWLine[][] = new int[lineList_W.size()][5];
			for (int j = 0; j < lineList_W.size(); j++)
				pageDataForWLine[j] = (int[]) lineList_W.get(j);
			// 体重线
			int pageDataForPLine[][] = new int[lineList_P.size()][5];
			for (int j = 0; j < lineList_P.size(); j++)
				pageDataForPLine[j] = (int[]) lineList_P.get(j);
			controlPage.addData("PAGE", "" + i);
			controlPage.addData("TEMPDOT", pageDataForT);
			controlPage.addData("WEIGHTDOT", pageDataForW);
			controlPage.addData("PHSIDOT", pageDataForP);
			controlPage.addData("TEMPLINE", pageDataForTLine);
			controlPage.addData("WEIGHTLINE", pageDataForWLine);
			controlPage.addData("PHSILINE", pageDataForPLine);
			// ----------------------------end----------------------------------
			// 出生体重
			controlPage.addData("BRONWEIGHT", bornWeight + "g");
		}
		// 设置页数
		controlPage.setCount(pageCount);
		controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
		mainPrtData.setData("TABLE", controlPage.getData());
		return mainPrtData;
	}

	/**
	 * 得到与出生天数的差
	 * 
	 * @param nowDate
	 *            String
	 * @return int
	 */
	public String getBornDateDiffer(String date, Timestamp bornDate) {
		Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
		return OdiUtil.getInstance().showAge(bornDate, nowDate);
		// return StringTool.getDateDiffer(nowDate, bornDate);
	}

	/**
	 * 计算横向坐标位置
	 * 
	 * @param date
	 *            int
	 * @param examineSession
	 *            int
	 * @return int
	 */
	private int countHorizontal(int date, int examineSession) {
		int adaptX = 7;
		return 10 * ((date - 1) * 6 + examineSession) - adaptX;
	}

	/**
	 * 结算新生儿体温单的纵向坐标--体温
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value) {
		int adaptY = 8;
		return (125 - countVerticalForT(value, 40, 35, 25) * 5) - adaptY;
	}

	/**
	 * 结算新生儿体温单的纵向坐标--体重
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value, double bronWeight) {
		int adaptY = 2;
		return (0 - countVerticalForW(value - bronWeight, 500, 0, 25) * 5)
				+ adaptY;
	}

	/**
	 * 计算横坐标的位置--体温
	 * 
	 * @param value
	 *            int 数据库中记录的数据
	 * @param topValue
	 *            int 表格中最大的值--顶
	 * @param butValue
	 *            int 表格中最小的值--底
	 * @param level
	 *            int 最大与最小之间的有多少等级-行数
	 * @return int
	 */
	private double countVerticalForT(double value, double topValue,
			double butValue, int level) {
		return (value - butValue) / ((topValue - butValue) / level) - 1;
	}

	private double countVerticalForW(double value, double topValue,
			double butValue, int level) {
		return value / ((topValue - butValue) / level) + 1;
	}

	/**
	 * TParm中“null”串转为空字符串
	 * 
	 * @param str
	 * @return
	 */
	public String nullToEmptyStr(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		return str;
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

	public static void main(String[] args) {
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sum\\SUMNewArrival.x");
	}

}
