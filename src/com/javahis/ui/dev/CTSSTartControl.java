package com.javahis.ui.dev;

import java.awt.Toolkit;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdo.sys.Operator;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.device.Uitltool;

/**
 * 
 * <p>
 * Title:洗衣清点
 * </p>
 * 
 * <p>
 * Description:洗衣清点
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class CTSSTartControl extends TControl {
	private static TTable tableM;
	private static List<String> cloth_nos = new ArrayList<String>();
	/** 自动保存定时器 */
	private Timer timer;
	/** 自动保存任务 */
	private TimerTask task;
	/** 定时器时间间隔 */
	private long period = 1000;
	/** 定时器延迟时间 */
	private long delay = 1000;
	AlienClass1Reader reader;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		setValue("EVAL_CODE", Operator.getID());
		setValue("REGION_CODE", Operator.getRegion());
		tableM = (TTable) getComponent("TABLE1");
		this.callFunction("UI|END|setEnabled", false);
	}

	/**
	 * 自动保存定时器初始化
	 */
	public void schedule() {
		this.task = new TimerTask() {
			public void run() {
				// autoSave();
				try {
					onClothNo();
				} catch (AlienReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		this.timer.schedule(this.task, this.delay, this.period);
	}

	/**
	 * 自动保存定时器初始化
	 */
	public void initTimer() {
		this.timer = new Timer();
		this.delay = this.period;
		this.task = new TimerTask() {
			public void run() {
				// autoSave();
			}
		};
	}

	/**
	 * 自动保存任务取消
	 * 
	 * @param tmpFilePath
	 *            String
	 * @param tmpFileName
	 *            String
	 */
	public void cancel() {
		this.task.cancel();
	}

	public void onStart() throws AlienReaderException {
		this.clearValue("TABLE1");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		this.callFunction("UI|START|setEnabled", false);
		this.callFunction("UI|END|setEnabled", true);
		reader = new AlienClass1Reader("192.168.1.100", 23);
		reader.open();
		reader.setFactorySettings();
		reader.setDHCP(0);
		reader.setRFAttenuation(20);
		grabFocus("CLOTH_NO");
		initTimer();
		schedule();
	}

	public void onEnd() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		reader.close();
		if (cloth_nos.size() > 0) {
			TParm parm = new TParm();
			String clothNos = "";
			for (int i = 0; i < cloth_nos.size(); i++) {
				clothNos += "'" + cloth_nos.get(i) + "',";
			}
			clothNos = clothNos.substring(0, clothNos.length() - 1);
			parm.setData("CLOTHNOS", clothNos);
			TParm result = null;// CTSTool.getInstance().selectClothByIn(parm);
			tableM.setParmValue(result);
			// onSave();
		}
		cancel();
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|START|setEnabled", true);
	}

	public void onEnter() {
		String clothNo = getValueString("CLOTH_NO");
		for (int i = 0; i < cloth_nos.size(); i++) {
			if (clothNo.equals(cloth_nos.get(i))) {
				return;
			}
		}
		tableM.acceptText();
		TParm tmpParm = tableM.getParmValue();
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		tmpParm.addData("CLOTH_NO", clothNo);
		tmpParm.addData("INV_CODE", "");
		tmpParm.addData("OWNER", "");
		tmpParm.addData("DEPT_CODE", "");
		tmpParm.addData("STATION_CODE", "");
		tmpParm.addData("STATE", "");
		tmpParm.addData("ACTIVE_FLG", "");
		tmpParm.addData("PAT_FLG", "");
		cloth_nos.add(clothNo);
		tableM.setParmValue(tmpParm);
		Toolkit.getDefaultToolkit().beep();
	}

	// }

	// /**
	// * 查询
	// */
	// public void onQuery() {
	// TParm parm = new TParm();
	// if (getValueString("PES_NO").equals("")) {
	// messageBox("请选择点评期间");
	// return;
	// }
	//
	// if (getValueString("PES_TYPE").equals("")) {
	// messageBox("请选择点评类别");
	// return;
	// }
	// parm.setData("TYPE_CODE", getValueString("PES_TYPE"));
	// parm.setData("PES_NO", getValueString("PES_NO"));
	// TParm result = PESTool.getInstance().selectOPDM(parm);
	// tableM.setParmValue(result);
	// }

	// /**
	// * table1点击事件
	// */
	// public void onClickTableM() {
	// if (row != tableM.getSelectedRow()) {
	// onSave("");
	// }
	// row = tableM.getSelectedRow();
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", tableM.getParmValue().getValue("CASE_NO", row));
	// parm.setData("PES_RX_NO", tableM.getParmValue().getValue("PES_RX_NO",
	// row));
	// TParm result = PESTool.getInstance().selectOPDD(parm);
	// }

	/**
	 * 保存
	 */
	public void onSave() {
		tableM.acceptText();
		if (tableM.getRowCount() < 1) {
			return;
		}
		TParm parmM = tableM.getParmValue();
		System.out.println(parmM);
		TParm parm = new TParm();
		parm.setData("WASH", parmM.getData());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("START_DATE", getValue("START_DATE"));
		// parm.setData("END_DATE", getValue("END_DATE"));
		TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction",
				"saveWash", parm);
		if (result.getErrCode() < 0) {
			// messageBox("插入失败");
			return;
		}
		TParm washNoParm = result.getParm("WASH_NO");
		for (int i = 0; i < washNoParm.getCount("WASH_NO"); i++) {
			onPrint(washNoParm.getValue("WASH_NO", i));
		}
	}

	// /**
	// * 汇出Excel
	// */
	// public void onExport() {
	// tableM.acceptText();
	// // 得到UI对应控件对象的方法
	// TParm parm = tableM.getParmValue();
	// if (null == parm || parm.getCount() <= 0) {
	// this.messageBox("没有需要导出的数据");
	// return;
	// }
	// ExportExcelUtil.getInstance().exportExcel(tableM, "处方点评处方列表");
	// }

	/**
	 * 清空
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableM.setParmValue(parm);
	}

	public void onClothNo() throws AlienReaderException {
		reader.clearTagList();
		Tag[] tag = reader.getTagList();
		if (tag != null) {
			for (int i = 0; i < tag.length; i++) {
				setValue("CLOTH_NO", Uitltool.decode(tag[i].getTagID().replace(
						" ", "")));
				onEnter();
			}
		} else {
			setValue("CLOTH_NO", "");
		}
	}

	// public void onPrint(String wash_no){
	// String sql =
	// " SELECT A.WASH_NO, B.DEPT_CHN_DESC, C.STATION_DESC, A.QTY, A.OUT_QTY," +
	// " A.PAT_FLG, D.USER_NAME" +
	// " FROM CTS_WASHM A, SYS_DEPT B, SYS_STATION C, SYS_OPERATOR D" +
	// " WHERE A.DEPT_CODE = B.DEPT_CODE" +
	// " AND A.STATION_CODE = C.STATION_CODE" +
	// " AND A.WASH_CODE = D.USER_ID" +
	// " AND A.WASH_NO = '" + wash_no + "'";
	// TParm resultM = new TParm(TJDODBTool.getInstance().select(sql));
	// sql =
	// " SELECT A.SEQ_NO, D.INV_CHN_DESC, D.DESCRIPTION, C.USER_NAME" +
	// " FROM CTS_WASHD A, CTS_CLOTH B, SYS_OPERATOR C, INV_BASE D" +
	// " WHERE A.CLOTH_NO = B.CLOTH_NO" +
	// " AND B.OWNER = C.USER_ID(+)" +
	// " AND B.INV_CODE = D.INV_CODE" +
	// " AND A.WASH_NO = '" + wash_no + "'";
	// TParm resultD = new TParm(TJDODBTool.getInstance().select(sql));
	// resultM = resultM.getRow(0);
	// TParm printParm = new TParm();
	// printParm.setData("DEPT_CHN_DESC", "TEXT",
	// resultM.getValue("DEPT_CHN_DESC"));
	// printParm.setData("STATION_DESC", "TEXT",
	// resultM.getValue("STATION_DESC"));
	// printParm.setData("QTY", "TEXT", resultM.getValue("QTY"));
	// printParm.setData("OUT_QTY", "TEXT", resultM.getValue("OUT_QTY"));
	// printParm.setData("PAT_FLG", "TEXT",
	// resultM.getValue("PAT_FLG").equals("Y")?"病患布衣洗衣单":"员工布衣洗衣单");
	// printParm.setData("USER_NAME", "TEXT","洗衣人: " +
	// resultM.getValue("USER_NAME"));
	// TParm tableParm = new TParm();
	// for (int i = 0; i < resultD.getCount("SEQ_NO"); i++) {
	// tableParm.addData("SEQ_NO", resultD.getValue("SEQ_NO", i));
	// tableParm.addData("INV_CHN_DESC", resultD.getValue("INV_CHN_DESC", i));
	// tableParm.addData("DESCRIPTION", resultD.getValue("DESCRIPTION", i));
	// tableParm.addData("USER_NAME", resultD.getValue("USER_NAME", i));
	// }
	// tableParm.setCount(resultD.getCount("SEQ_NO"));
	// tableParm.addData("SYSTEM", "COLUMNS", "SEQ_NO");
	// tableParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
	// tableParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
	// tableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
	// printParm.setData("TABLE", tableParm.getData());
	// this.openPrintWindow("%ROOT%\\config\\prt\\CTS\\CTSPrint.jhw",
	// printParm);
	// }
	public void onPrint(String wash_no) {
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
				+ wash_no
				+ "'" + " ORDER BY B.INV_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm printParm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp start_date = result.getTimestamp("START_DATE", 0);
		Timestamp end_date = result.getTimestamp("END_DATE", 0);
		printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
		if (result.getValue("PAT_FLG", 0).equals("Y")) {
			printParm.setData("TITLE2", "TEXT", "病患洗衣登记单");
		} else {
			printParm.setData("TITLE2", "TEXT", "员工洗衣登记单");
		}
		printParm.setData("PROGRAM", "TEXT", "洗衣清点");
		printParm.setData("PRINT_DATE", "TEXT", date.toString()
				.substring(0, 10).replaceAll("-", "/"));
		printParm.setData("PRINT_NO", "TEXT", wash_no);
		printParm.setData("DEPT_CHN_DESC", "TEXT", result.getValue(
				"DEPT_CHN_DESC", 0));
		printParm.setData("STATION_DESC", "TEXT", result.getValue(
				"STATION_DESC", 0));
		printParm.setData("QTY", "TEXT", result.getValue(
				"QTY", 0));
		printParm.setData("OUT_QTY", "TEXT", result.getValue(
				"OUT_QTY", 0));
		printParm.setData("START_DATE", "TEXT", start_date.toString()
				.substring(0, 19).replaceAll("-", "/"));
//		printParm.setData("END_DATE", "TEXT", end_date.toString()
//				.substring(0, 19).replaceAll("-", "/"));
		printParm.setData("END_DATE", "TEXT", "");
		printParm.setData("WASH_CODE", "TEXT",  result.getValue(
				"WASH_CODE", 0));
		String inv_code = result.getValue("INV_CHN_DESC", 0);
		int count = 1;
		TParm clothParm = new TParm();
		for (int i = 1; i < result.getCount("INV_CODE"); i++) {
			if (result.getValue("INV_CHN_DESC", i).equals(inv_code)) {
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
