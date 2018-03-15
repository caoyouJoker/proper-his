package com.javahis.ui.udd;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:住院药房同步数据
 * </p>
 * 
 * <p>
 * Description: 住院药房同步数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl 2013.04.09
 * @version 1.0
 */
public class UDDHISGYControl extends TControl {
	private TTable table;
	private SimpleDateFormat Df = new SimpleDateFormat();

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		table = this.getTable("TABLE");
		Timestamp startD = SystemTool.getInstance().getDate();
		String year = String.valueOf(startD).substring(0, 4);
		String month = String.valueOf(startD).substring(5, 7);
		String day = String.valueOf(startD).substring(8, 10);
		Timestamp startDate = StringTool.getTimestamp(year + month + day
				+ "000000", "yyyyMMddHHmmss");
		this.setValue("START_D", startDate);
		this.setValue("END_D", startD);
		this.setValue("REGION_CODE", Operator.getRegion());
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DEPT_CODE;STATION_CODE;MR_NO;");
		table.removeRowAll();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CASE_NO,A.DEPT_CODE,A.STATION_CODE,A.MR_NO,A.ORDER_DESC||' '||A.SPECIFICATION AS ORDER_DESC,"
						+ "A.START_DTTM,A.PHA_DOSAGE_DATE,A.DOSAGE_QTY,A.DOSAGE_UNIT,A.OWN_PRICE,A.OWN_AMT,"
						+ "A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DR_NOTE,B.PAT_NAME,"
						+ "A.ORDER_NO,A.ORDER_SEQ,A.END_DTTM,A.ORDER_CODE,A.IPD_NO,A.BED_NO,'"
						+ Operator.getID()+"' AS OPT_USER,'"+Operator.getIP()+"' AS OPT_TERM,A.ORDER_DEPT_CODE,A.HIDE_FLG,"
						+ "A.ORDER_DR_CODE,A.EXEC_DEPT_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.VS_DR_CODE"
						+ " FROM ODI_DSPNM A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO ");
		if (!this.getValueString("MR_NO").equals(""))
			sql.append(" AND A.MR_NO='" + this.getValueString("MR_NO") + "'");
		if (!this.getValueString("STATION_CODE").equals(""))
			sql.append(" AND A.STATION_CODE='"
					+ this.getValueString("STATION_CODE") + "'");
		if (!this.getValueString("DEPT_CODE").equals(""))
			sql.append(" AND A.DEPT_CODE='" + this.getValueString("DEPT_CODE")
					+ "'");
		if (!this.getValueString("REGION_CODE").equals(""))
			sql.append(" AND A.REGION_CODE='"
					+ this.getValueString("REGION_CODE") + "'");
		if (!this.getValueString("START_D").equals("")
				&& !this.getValueString("END_D").equals(""))
			sql.append(" AND A.PHA_DOSAGE_DATE>=TO_DATE('"
					+ this.getValueString("START_D").replace("-", "").replace(
							":", "").replace(" ", "").substring(0, 12)
					+ "','YYYYMMDDHH24MISS')"
					+ " AND  A.PHA_DOSAGE_DATE<=TO_DATE('"
					+ this.getValueString("END_D").replace("-", "").replace(
							":", "").replace(" ", "").substring(0, 12)
					+ "','YYYYMMDDHH24MISS')");
		if (this.getTRadioButton("tRadioButton_0").isSelected())
			sql.append(" AND A.GYWSSEND_FLG='N'");
		else if (this.getTRadioButton("tRadioButton_1").isSelected())
			sql.append(" AND A.GYWSSEND_FLG='Y'");
		else if (this.getTRadioButton("tRadioButton_2").isSelected())
			sql.append(" AND A.GYWSSEND_FLG='C'");
		sql.append(" AND A.CAT1_TYPE='PHA' AND A.DSPN_KIND<>'RT'  ORDER BY  A.DEPT_CODE,A.STATION_CODE,A.CASE_NO DESC");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount() <= 0) {
			table.removeRowAll();
			this.setValue("TOT", "0");
			return;
		}
		if (result.getErrCode() < 0) {
			table.removeRowAll();
			this.setValue("TOT", "0");
			this.messageBox("查询错误");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("FLG", i, "Y");
			result.setData("START_DATE", i, StringTool.getTimestamp(result
					.getValue("START_DTTM", i), "yyyyMMddHHmm"));
			result.setData("PHA_DOSAGE_DATE", i, getFormateDate(result
							.getTimestamp("PHA_DOSAGE_DATE", i),
							"yyyy/MM/dd HH:mm:ss"));
		}
		this.setValue("TOT", ""+result.getCount());
		table.setParmValue(result);
	}

	/**
	 * 页签改变事件
	 */
	public void onChange() {
		int index = getTTabbedPane("TABLEPANLE").getSelectedIndex();
		switch (index) {
		case 0:
			callFunction("UI|query|setEnabled", true);
			onSelectRadion();
			break;
		case 1:
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|query|setEnabled", false);
			onMerge();
			break;
		}
	}

	/**
	 * 
	 */
	public void onMerge() {
		table.acceptText();
		TParm parm = table.getParmValue();
		if(parm==null){
			this.getTable("TABLE1").removeRowAll();
			return;
		}
		Map map = new HashMap();
		double qty = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);
			if (parmRow.getBoolean("FLG")) {
				if (map.get(parmRow.getValue("ORDER_CODE")) == null) {
					qty = parmRow.getDouble("DOSAGE_QTY");
					map.put(parmRow.getValue("ORDER_CODE"), qty);
				} else {
					qty = TypeTool.getDouble(map.get(parmRow
							.getValue("ORDER_CODE")))
							+ parmRow.getDouble("DOSAGE_QTY");
					map.put(parmRow.getValue("ORDER_CODE"), qty);
				}
			}
		}
		if (map.size() == 0) {
			this.getTable("TABLE1").removeRowAll();
			this.messageBox("没有选择数据");
			return;
		}
		TParm inParm = new TParm();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String ordercode = (String) entry.getKey();
			double val = TypeTool.getDouble(entry.getValue());
			String sql = "SELECT ORDER_CODE,ORDER_DESC||' '||SPECIFICATION AS ORDER_DESC,DOSAGE_UNIT "
					+ " FROM PHA_BASE WHERE ORDER_CODE='" + ordercode + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			inParm.addData("ORDER_DESC", result.getValue("ORDER_DESC", 0));
			inParm.addData("DISPENSE_QTY", val);
			inParm.addData("DOSAGE_UNIT", result.getValue("DOSAGE_UNIT", 0));
		}
		inParm.setCount(map.size());
		this.getTable("TABLE1").setParmValue(inParm);
	}

	/**
	 * 拿到TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * 选择事件
	 */
	public void onSelectRadion() {
		if (this.getTRadioButton("tRadioButton_0").isSelected()) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", true);
		} else if (this.getTRadioButton("tRadioButton_1").isSelected()) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
		}else if (this.getTRadioButton("tRadioButton_2").isSelected()) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
		}
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private Timestamp getFormateDate(Timestamp a, String b) {
		if (a == null) {
			return a;
		}
		Df = new SimpleDateFormat(b);
		String dfStr = Df.format(a);
		Timestamp redate = StringTool.getTimestamp(dfStr, b);
		return redate;
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		TParm inParm = new TParm();
		int Scount = 0;
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			if (parmRow.getBoolean("FLG")) {
				inParm.addRowData(parm, i);
				Scount++;
			}
		}
		if (Scount == 0) {
			this.messageBox("没有选择数据");
			return;
		}
		inParm.setCount(Scount);
		TParm result = TIOM_AppServer.executeAction(
				"action.udd.UddHisGYAction", "onSave", inParm);
		Object list = result.getData("ERR_LIST");
		StringBuffer sbErr = new StringBuffer();
		if (list != null) {
			TParm errList = result.getParm("ERR_LIST");
			if (errList != null) {
				int countErr = errList.getCount();
				if (countErr > 0) {
					for (int i = 0; i < countErr; i++) {
						String err = errList.getValue("MSG", i);
						messageBox_(err);
						sbErr.append(err).append("\r\n");
					}

				}
				String fileNmae = (new StringBuilder()).append(
						TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
						.append("\\同步错误日志").append(
								StringTool.getString(TJDODBTool.getInstance()
										.getDBTime(), "yyyyMMddHHmmss"))
						.append(".txt").toString();
				messageBox_((new StringBuilder()).append(
						"详细情况见C:/JavaHis/logs/同步错误日志").append(
						StringTool.getString(TJDODBTool.getInstance()
								.getDBTime(), "yyyyMMddHHmmss")).append(
						".txt文件").toString());
				try {
					FileTool.setString(fileNmae, sbErr.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (result.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			this.onQuery();
		}
	}
	/**
	 * 取消发药
	 */
    public  void  onDelete(){
    	table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		TParm inParm = new TParm();
		int Scount = 0;
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			if (parmRow.getBoolean("FLG")) {
				inParm.addRowData(parm, i);
				Scount++;
			}
		}
		if (Scount == 0) {
			this.messageBox("没有选择数据");
			return;
		}
		inParm.setCount(Scount);
		inParm.setData("ID", Operator.getID());
		inParm.setData("IP", Operator.getIP());
		inParm.setData("REGION_CODE", Operator.getRegion());
		TParm result = TIOM_AppServer.executeAction(
				"action.udd.UddHisGYAction", "onCancleDosage", inParm);
		Object list = result.getData("ERR_LIST");
		StringBuffer sbErr = new StringBuffer();
		if (list != null) {
			TParm errList = result.getParm("ERR_LIST");
			if (errList != null) {
				int countErr = errList.getCount();
				if (countErr > 0) {
					for (int i = 0; i < countErr; i++) {
						String err = errList.getValue("MSG", i);
						messageBox_(err);
						sbErr.append(err).append("\r\n");
					}

				}
				String fileNmae = (new StringBuilder()).append(
						TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
						.append("\\取消发药日志").append(
								StringTool.getString(TJDODBTool.getInstance()
										.getDBTime(), "yyyyMMddHHmmss"))
						.append(".txt").toString();
				messageBox_((new StringBuilder()).append(
						"详细情况见C:/JavaHis/logs/取消发药日志").append(
						StringTool.getString(TJDODBTool.getInstance()
								.getDBTime(), "yyyyMMddHHmmss")).append(
						".txt文件").toString());
				try {
					FileTool.setString(fileNmae, sbErr.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (result.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			this.onQuery();
		}
    }
	// 查询病患信息
	public void onQueryPatInfo() {
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		setValue("MR_NO", mrNo);
		this.onQuery();
	}

	/**
	 * 全选功能
	 */
	public void onAllCheck() {
		table.acceptText();
		TParm parm = table.getParmValue();
		boolean flg = ((TCheckBox) getComponent("ALL_CHECK")).isSelected();
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", flg ? "Y" : "N");
		}

	}

	/**
	 * 得到TTable对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TTextField对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TTextField对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TRadioButton getTRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
}
