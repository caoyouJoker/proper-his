package com.javahis.ui.sys;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title: 药品频率
 * </p>
 * 
 * <p>
 * Description:药品频率
 * </p>
 * 
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSPhaFreqControl extends TControl {

	TParm data;
	int selectRow = -1;
	int selectTimeRow = -1;
	TParm parmData;
	TTable table, trtTable;
	TDataStore allDs, trtDs;

	// 时点TAG名
	private final static String TIME_TAG = "0;1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21;22;23;24;25;26;27;28;29;30;31;32;33;34;35;36;37;38;39;40;41;42;43;44;45;46;47";
	private final static String TAG = "FREQ_CODE;SEQ;FREQ_CHN_DESC;PY1;PY2;FREQ_ENG_DESC;DESCRIPTION;TAKE_DAYS;NOCOMPUTE_FLG;STAT_FLG;CYCLE;FREQ_TIMES;SUN_FLG;MON_FLG;TUE_FLG;WED_FLG;THUR_FLG;FRI_FLG;STA_FLG";
	private final static String TRT_SQL = "SELECT FREQ_CODE, STANDING_TIME, OPT_USER, OPT_DATE, OPT_TERM FROM SYS_TRTFREQTIME ORDER BY FREQ_CODE,STANDING_TIME";
	private final static String DELETE_TRT_SQL = "DELETE SYS_TRTFREQTIME WHERE FREQ_CODE='";
	private final static String GET_MAX_SEQ = "SELECT MAX(SEQ) AS SEQ FROM SYS_PHAFREQ";
	private final static String ALL_SQL = "SELECT FREQ_CODE, FREQ_CHN_DESC, FREQ_ENG_DESC, PY1, PY2, "
			+ "SEQ, DESCRIPTION, WESMED_FLG, CHIMED_FLG, CYCLE, "
			+ "FREQ_TIMES, FREQ_UNIT_48, MON_FLG, TUE_FLG, WED_FLG, "
			+ "THUR_FLG, FRI_FLG, STA_FLG, SUN_FLG, NOCOMPUTE_FLG, "
			+ "TAKE_DAYS, STAT_FLG, OPT_USER, OPT_DATE, OPT_TERM "
			+ " FROM SYS_PHAFREQ ORDER BY FREQ_CODE,SEQ";
	// String[] tags = TAG.split(";");
	String[] tags;

	public void onInit() {
		super.onInit();
		init();
	}

	/**
	 * 时间点CHECKBOX点击事件，改变时间点TABLE的值和FREQ_TIMES的值
	 * 
	 * @param text
	 *            时间点CHECKBOX显示内容
	 * @param tag
	 *            时间点CHECKBOX的TAG
	 */
	public void onCheckTime(String text, String tag) {
		String code = this.getValueString("FREQ_CODE");
		if (StringUtil.isNullString(code)) {
			this.setValue(tag, "N");
			this.messageBox_("没有可以操作的频次");
			return;
		}
		int freqTimes = 0;
		if (tags == null || tags.length != 48) {
			this.setValue(tag, "N");
			this.messageBox_("数据取得有误");
			return;
		}
		// 设置FREQ_TIMES
		String temp;
		for (int i = 0; i < tags.length; i++) {
			temp = this.getValueString(tags[i]);
			if ("Y".equalsIgnoreCase(temp)) {
				freqTimes++;
			}
		}
		this.setValue("FREQ_TIMES", freqTimes);

		temp = this.getValueString(tag);
		// 设置时间点TABLE
		if (StringTool.getBoolean(temp)) {
			int row = trtDs.insertRow();
			trtDs.setItem(row, "FREQ_CODE", code);
			trtDs.setItem(row, "STANDING_TIME", text);
			trtDs.setItem(row, "OPT_USER", Operator.getID());
			trtDs.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
			trtDs.setItem(row, "OPT_TERM", Operator.getIP());
		} else {
			int row = trtDs.find("STANDING_TIME='" + text + "'");
			if (row < 0) {
				return;
			}
			trtDs.deleteRow(row);
		}
		// 给时间点TABLE排序
		trtDs.setFilter("FREQ_CODE='" + code + "'");
		trtDs.filter();
		trtDs.setSort("STANDING_TIME ASC");
		// System.out.println("trtDs start-------------------------");
		// trtDs.showDebug();
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * 删除处置时间频次
	 */
	public void onDeletePLN() {
		int row = trtTable.getSelectedRow();
		if (row < 0)
			return;
		trtDs.deleteRow(row);
		trtDs.filter();
		trtDs.setSort("STANDING_TIME ASC");
		// System.out.println("trtDs start-------------------------");
		// trtDs.showDebug();
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * 时间点TABLE的点击事件
	 */
	public void onTIMETABLEClicked(int row) {
		if (row < 0)
			return;
		selectTimeRow = row;
		this.callFunction("UI|DEL|setEnabled", true);
	}

	/**
	 * 时点TABLE值改变
	 */
	public boolean onTimeTableChangeValue(TTableNode tNode) {
		String value = TCM_Transform.getString(tNode.getValue());
		if (StringUtil.isNullString(value) || value.length() != 4)
			return true;
		int time;
		try {
			time = StringTool.getInt(value);
			String temp = StringTool.fill0(TCM_Transform.getString(time), 4);
			if (!value.equalsIgnoreCase(temp))
				return true;
		} catch (Exception e) {
			return true;
		}
		if (time < 0 || time > 2359)
			return true;
		trtDs.setItem(tNode.getRow(), "STANDING_TIME", value);
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
		trtTable.getTable().grabFocus();
		if (trtTable.getRowCount() > 0)
			trtTable.setSelectedRow(0);
		return false;
	}

	/**
	 * 初始化界面，查询所有的数据
	 * 
	 * @return TParm
	 */
	public void onQuery() {
		String freqCode = this.getValueString("FREQ_CODE");
		if (StringUtil.isNullString(freqCode)) {
			this.messageBox_("频次代码不能为空");
			return;
		}
		freqCode = freqCode.trim().toUpperCase();
		selectRow = 0;
		allDs.setFilter("FREQ_CODE = '" + freqCode + "'");
		allDs.filter();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		this.setValueForParm(TAG, parmData, 0);
		String time48 = parmData.getValue("FREQ_UNIT_48", 0);
		if (!set48(time48)) {
			this.messageBox_("没有查询数据");
			// onClear();
		}
		setTrtTable(freqCode);
	}

	/**
	 * 设置处置时间TABLE并且在DATASTORE里新增一行，以备保存时用
	 * 
	 * @param freqCode
	 *            String
	 */
	public void setTrtTable(String freqCode) {
		trtDs.setFilter("FREQ_CODE='" + freqCode + "'");
		trtDs.filter();
		// System.out.println("setTrtTable-----------");
		// trtDs.showDebug();
		trtDs.setSort("STANDING_TIME ASC");
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue(TIME_TAG);
		this.clearValue(TAG);
		selectRow = -1;
		// trtTable.removeRowAll();
		// table.removeRowAll();
		// allDs.setFilter("");
		// allDs.filter();
		// table.setDataStore(allDs);
		// table.setDSValue();
		// trtDs.setFilter("");
		// trtDs.filter();
		allDs = new TDataStore();
		allDs.setSQL(ALL_SQL);
		allDs.retrieve();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		trtDs = new TDataStore();
		trtDs.setSQL(TRT_SQL);
		trtDs.retrieve();

		callFunction("UI|FREQ_CODE|setEnabled", true);
	}

	/**
	 * 初始化界面，查询所有的数据
	 * 
	 * @return TParm
	 */
	public void init() {
		tags = StringTool.parseLine(TIME_TAG, ";");
		table = (TTable) this.getComponent("TABLE");
		trtTable = (TTable) this.getComponent("TRT_TABLE");
		trtTable.addRow();
		allDs = new TDataStore();
		allDs.setSQL(ALL_SQL);
		allDs.retrieve();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		trtDs = new TDataStore();
		trtDs.setSQL(TRT_SQL);
		trtDs.retrieve();
		trtTable.addEventListener("TRT_TABLE->" + TTableEvent.CHANGE_VALUE,
				this, "onTimeTableChangeValue");
		// trtTable.setDataStore(trtDs);
		// onClear();
	}

	/**
	 * 主TABLE点击事件
	 */
	public void onTableClick() {
		int row = table.getSelectedRow();
		selectRow = row;
		this.setValueForParm(TAG, parmData, row);
		String time48 = parmData.getValue("FREQ_UNIT_48", row);
		if (!set48(time48)) {
			this.messageBox_("没有查询数据");
		}
		String freqCode = parmData.getValue("FREQ_CODE", row);
		setTrtTable(freqCode);
		this.getTextField("FREQ_CODE").setEnabled(false);
	}

	/**
	 * 设置48个Combo
	 * 
	 * @param time48
	 *            String 时间点
	 */
	public boolean set48(String time48) {
		char[] char48 = time48.toCharArray();
		if (char48.length != 48)
			return false;
		for (int i = 0; i < char48.length; i++) {
			// System.out.println(i + ":" + char48[i]);
			this.setValue(i + "", char48[i] + "");
		}
		return true;
	}

	/**
	 * 取得48时间点
	 * 
	 * @return String
	 */
	public String get48() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < tags.length; i++) {
			// System.out.println(tags[i] + ":" + this.getValueString(tags[i]));
			result.append(this.getValueString(tags[i]));
		}
		return result.toString();

	}

	/**
	 * 保存
	 */
	public void onSave() {
		TParm result;
		String[] sql;
		String freqCode = this.getValueString("FREQ_CODE").toUpperCase();
		if (StringUtil.isNullString(freqCode)) {
			this.messageBox_("频次代码不能为空");
			return;
		}
		int row = -1;
		if (allDs == null || allDs.rowCount() < 1) {
			return;
		}
		int exist = allDs.find("FREQ_CODE='" + freqCode + "'");
		if (exist < 0)
			row = allDs.insertRow();
		else
			row = exist;
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		// FREQ_CODE
		allDs.setItem(row, "FREQ_CODE", freqCode);
		// FREQ_CHN_DESC
		allDs.setItem(row, "FREQ_CHN_DESC",
				this.getValueString("FREQ_CHN_DESC"));
		// FREQ_ENG_DESC
		allDs.setItem(row, "FREQ_ENG_DESC",
				this.getValueString("FREQ_ENG_DESC"));
		// PY1
		allDs.setItem(row, "PY1", this.getValueString("PY1"));
		// PY2
		allDs.setItem(row, "PY2", this.getValueString("PY2"));
		// SEQ
		allDs.setItem(row, "SEQ", getMaxSeq() + 1);
		// DESCRIPTION
		allDs.setItem(row, "DESCRIPTION", this.getValueString("DESCRIPTION"));
		// CYCLE
		allDs.setItem(row, "CYCLE", this.getValueInt("CYCLE"));
		// FREQ_TIMES
		allDs.setItem(row, "FREQ_TIMES", this.getValueInt("FREQ_TIMES"));
		// FREQ_UNIT_48
		allDs.setItem(row, "FREQ_UNIT_48", this.get48());
		// MON_FLG
		allDs.setItem(row, "MON_FLG", this.getValueString("MON_FLG"));
		// TUE_FLG
		allDs.setItem(row, "TUE_FLG", this.getValueString("TUE_FLG"));
		// WED_FLG
		allDs.setItem(row, "WED_FLG", this.getValueString("WED_FLG"));
		// THUR_FLG
		allDs.setItem(row, "THUR_FLG", this.getValueString("THUR_FLG"));
		// FRI_FLG
		allDs.setItem(row, "FRI_FLG", this.getValueString("FRI_FLG"));
		// STA_FLG
		allDs.setItem(row, "STA_FLG", this.getValueString("STA_FLG"));
		// SUN_FLG
		allDs.setItem(row, "SUN_FLG", this.getValueString("SUN_FLG"));
		// NOCOMPUTE_FLG
		allDs.setItem(row, "NOCOMPUTE_FLG",
				this.getValueString("NOCOMPUTE_FLG"));
		// TAKE_DAYS
		allDs.setItem(row, "TAKE_DAYS", this.getValueInt("TAKE_DAYS"));
		// STAT_FLG
		allDs.setItem(row, "STAT_FLG", this.getValueString("STAT_FLG"));
		// OPT_USER
		allDs.setItem(row, "OPT_USER", Operator.getID());
		// OPT_DATE
		allDs.setItem(row, "OPT_DATE", now);
		// OPT_TERM
		allDs.setItem(row, "OPT_TERM", Operator.getIP());
		sql = allDs.getUpdateSQL();
		sql = StringTool.copyArray(sql, trtDs.getUpdateSQL());
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			// this.messageBox_(result.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		onClear();
	}

	/**
	 * 取得最大SEQ
	 * 
	 * @return long
	 */
	public int getMaxSeq() {
		TParm result = new TParm(TJDODBTool.getInstance().select(GET_MAX_SEQ));
		// System.out.println("result" + result);
		// System.out.println("dfdfdf" + result.getValue("SEQ", 0));
		int seq = TCM_Transform.getInt(result.getValue("SEQ", 0));
		return seq;
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		if (selectRow < 0) {
			this.messageBox_("没有选中项");
			return;
		}
		String code = allDs.getItemString(selectRow, "FREQ_CODE");
		String deleteTrt = DELETE_TRT_SQL + code + "'";
		// this.messageBox_(selectRow);
		// System.out.println("onDelete---------------");
		// allDs.showDebug();
		allDs.deleteRow(selectRow);
		// System.out.println("onDelete---------------");
		// allDs.showDebug();
		String[] sqlTemp = allDs.getUpdateSQL();
		String[] sql = new String[] { deleteTrt };
		sql = StringTool.copyArray(sql, sqlTemp);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			// this.messageBox_(result.getErrText());
			this.messageBox("E0003");
		} else {
			this.messageBox("P0003");
		}
		onClear();
	}

	/**
	 * 根据汉字查询拼音首字母
	 */
	public void onCode() {
		// this.messageBox_(getMaxSeq());
		this.setValue("SEQ", getMaxSeq());
		TNumberTextField seq = (TNumberTextField) this.getComponent("SEQ");
		seq.grabFocus();
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

}
