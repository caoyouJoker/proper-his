package com.javahis.ui.nss;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.SwingUtilities;

import jdo.bil.BILInvoiceTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.system.textFormat.TextFormatSYSDeptForOprt;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatSYSOperatorStation;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.ui.sys.LEDUI;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import device.PassDriver;

/**
 * <p>
 * Title: ��ʳϵͳ=>��ʳ����=>Ӫ��ʦ����վ
 * </p>
 * 
 * <p>
 * Description: 
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2012��7��3��
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lix
 * @version JavaHis 1.0
 */
public class NSSMainControl extends TControl {
	private static String TABLE = "TABLE";
	private static String PANEL = "Panel";
	/**
	 * ��ǰ��ҳ��TAG
	 */
	public String workPanelTag = "";
	/**
	 * ����ϵͳ����
	 */
	private String runFlg = "";
	/**
	 * Socket���ͻ�ʿվ����
	 */
	// private SocketLink client;
	/**
	 * ��ʿվȨ��
	 */
	private boolean inwPopedem;
	/**
	 * ������
	 */
	private LEDUI ledUi;
	private LEDUI ledUi1;
	/**
	 * �����Ʋ���
	 */
	private TParm ledParm;
	/**
	 * ������ҩ
	 */
	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;
	private String srceenWidth = "";
	/**
	 * ICUע��
	 */
	private boolean IsICU = false;
	// $$=============add by lx 2012-07-03 ����������start==================$$//
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by lx 2012-07-03 ����������end==================$$//
	public String getSrceenWidth() {
		return srceenWidth;
	}

	public void setSrceenWidth(String srceenWidth) {
		this.srceenWidth = srceenWidth;
	}

	/**
	 * ��ʼ������
	 */

	public void onInitParameter() {
		// �������� IBS
		// this.setPopedem("deptEnabled",true);
		// this.setPopedem("deptAll",true);
		// this.setPopedem("stationEnabled",true);
		// this.setPopedem("stationAll",true);
		// this.setPopedem("odiButtonVisible",true);
		// this.setPopedem("inwCheckVisible",true);
		// this.setPopedem("inwExecuteVisible",true);
		// this.setPopedem("ibsStButVisible",true);
		// סԺ

		this.setPopedem("deptEnabled", true);
		this.setPopedem("deptAll", true);
		this.setPopedem("stationEnabled", true);
		this.setPopedem("stationAll", true);
		this.setPopedem("odiButtonVisible", true);
		this.setPopedem("inwCheckVisible", true);
		// modify shibl 20120317
		// this.setPopedem("inwExecuteVisible", true);
		this.setPopedem("ibsStButVisible", true);

	}

	public void onInit() {
		super.onInit();
		this.getTMenuItem("card").setEnabled(false);
		// shibl 20120329 add
		this.callFunction("UI|tpr|setVisible", false);
		this.callFunction("UI|newtpr|setVisible", false);
		// TABLE˫���¼�
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicke");
		// //ע��TPanel����¼�
		// callFunction("UI|" + PANEL + "|addEventListener",
		// PANEL + "->" + "", this, "onTableClicked");
		// ����ϵͳ����
		Object obj = this.getParameter();

		if (obj != null) {
			// $$add by lx 2012/03/19 ����1024*768;
			String strParameter = this.getParameter().toString();
			String sysID = "";
			// ����;���
			if (strParameter.indexOf(";") != -1) {
				//this.messageBox("width"+ strParameter.split(";")[0]);
				sysID = strParameter.split(";")[0];
				this.setSrceenWidth(strParameter.split(";")[1]);
			} else {
				sysID = this.getParameter().toString();
			}

			this.setRunFlg(sysID);
		} else {
			// ����
			// this.setRunFlg("IBSPAYBILL");
			this.setRunFlg("ODI");
			// this.setRunFlg("INWCHECK");
			// this.setRunFlg("MRO");
			// this.setRunFlg("OIDR");
			// this.setRunFlg("NSSORDER");
			// this.setRunFlg("NSSCHAR");
		}
		// ��ʼ��Ȩ��
		this.onInitPopeDem();
		// ���
		this.onClear();
		// ������ϵͳ��ʼ��
		this.initSystem();
		// ��ʼ����ѯ
		this.onQuery();
		// ��ʼ��SYS_FEE
		// this.initSysFeeData();
		// ���ñ���
		String s = getConfigString(getRunFlg() + "_Title");
		if (s != null && s.length() > 0)
			this.setTitle(s);

		// $$=====add by lx 2012/06/24 �������򷽷�start============$$//
		addListener(getTTable("TABLE"));
		// $$=====add by lx 2012/06/24 �������򷽷�end============$$//
	}

	/**
	 * �����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicke(int row) {
		//this.messageBox("==come in1111111==");
		if (row < 0)
			return;
		TParm parm = getTTable("TABLE").getParmValue().getRow(row);

		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// ����
			TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
					.getComponent("INW_STATION_CODE");
			h.setUserID(Operator.getID());
			h.onQuery();
			// ����
			TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
					.getComponent("INW_DEPT_CODE");
			d.setStationCode(this.getValueString("INW_STATION_CODE"));
			d.onQuery();
			// ����ҽʦ
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("INW_VC_CODE");
			k.setDept(this.getValueString("INW_DEPT_CODE"));
			k.onQuery();
			// ��Ժ����
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// ��Ժ����ҽʦ
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
			this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
			this.setValue("INW_DEPT_CODE", parm.getValue("DEPT_CODE"));
			this.setValue("INW_VC_CODE", parm.getValue("VS_DR_CODE"));
		} else {
			// ����
			TextFormatSYSStation h = (TextFormatSYSStation) this
					.getComponent("STATION_CODE");
			h.setDeptCode(this.getValueString("DEPT_CODE"));
			h.onQuery();
			// ����ҽʦ
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("VC_CODE");
			k.setDept(this.getValueString("DEPT_CODE"));
			k.onQuery();
			// ��Ժ����
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// ��Ժ����ҽʦ
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
		}
	}

	public void initSystem() {
		// ҽ��վ��ʼ��
		if (this.getRunFlg().equals("ODI")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
		}
		// ����Socket�Ʒ�
		if (this.getRunFlg().equals("IBS")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
		}
		// �ٴ�·��
		if (this.getRunFlg().equals("CLPMANAGEM")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
		}
		// ����Socket��ʿվ
		if (this.getRunFlg().equals("INWCHECK")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			// this.messageBox("===INWCHECK===");
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
			this.getTMenuItem("card").setEnabled(true);
			// shibl add
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
			// ��LEDUI
			openLEDUI();
		}
		if (this.getRunFlg().equals("INWEXE")) {
			this.callFunction("UI|tpr|setVisible", true);
			this.callFunction("UI|newtpr|setVisible", true);
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			// shibl add
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
			this.getTMenuItem("card").setEnabled(true);
			// modify shibl 20120317
			// openLEDOneUI();
		}

		/**
		 * ��ʿվҽ������ӡ
		 */
		if (this.getRunFlg().equals("SHEET")) {
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
			// this.getTMenuItem("bedcard").setEnabled(true);
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			// shibl add
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
		}

	}

	/**
	 * ��ʼ��SYS_FEE
	 */
	public void initSysFeeData() {
		if ("ODI".equals(this.getRunFlg())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						// Thread.sleep(100);
						TIOM_Database.getLocalTable("SYS_FEE");
						TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
					} catch (Exception e) {
					}
				}
			});
		}
	}

	/**
	 * ����LEDUI
	 */
	public void openLEDUI() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		parm.setData("STATION_CODE", this.getValueString("INW_STATION_CODE"));
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		ledUi = new LEDUI((Frame) com, this, parm);
		ledUi.openWindow();
	}

	/**
	 * ����LEDUI
	 */
	public void openLEDOneUI() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		parm.setData("STATION_CODE", this.getValueString("INW_STATION_CODE"));
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		ledUi1 = new LEDUI((Frame) com, this, parm, true);
		ledUi1.openWindow();
	}

	/**
	 * �õ�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onSelStationListenerLed(TParm parm) {
		// System.out.println("parm"+parm);
		this.ledParm = parm;
	}

	/**
	 * ����ѡ���¼�
	 */
	public void onSel() {
		if (this.getValueString("INW_STATION_CODE").length() != 0)
			this.ledParm.runListener("onListenerLed", this
					.getValueString("INW_STATION_CODE"));
	}

	/**
	 * ����ʿվͨѶ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void openInwCheckWindow(TParm parm) {
		// ���û�ʿվѡ�񲡻�ҳ��
		Object obj = this.openDialog("%ROOT%\\config\\odi\\PatInfoUI.x", parm);
		if (obj != null) {
			TParm action = (TParm) obj;
			action.setData("LEDUI", ledUi);
			// ���û�ʿ��˽���
			this.runPaneSocketInwCheck("INWSTATIONCHECK",
					"inw\\INWOrderCheckMain.x", action);
			// ledUi.removeMessage(action);\
			// modify shibl 20120317
			// if (null != action.getValue("FLG")
			// || action.getValue("FLG").equals("Y")) {// �жϻ�ʿվִ�в���
			// action.setData("LEDUI", ledUi1);
			// // System.out.println("action::::"+action);
			// // ���û�ʿִ�н���
			// this.runPaneSocketInwExe("INWSTATIONEXECUTE",
			// "inw\\INWOrderExecMain.x", action);
			//
			// } else {
			// action.setData("LEDUI", ledUi);
			// // ���û�ʿ��˽���
			// this.runPaneSocketInwCheck("INWSTATIONCHECK",
			// "inw\\INWOrderCheckMain.x", action);
			//
			// }

		}
	}

	/**
	 * ��ʿվִ�еõ�������ϸ����
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 * @param parm
	 *            TParm =================pangben 2011-11-10
	 */
	public void runPaneSocketInwExe(String tag, String path, TParm parm) {

		// this.messageBox("runPaneSocketInwExe");
		// �رյ�ǰ����ҳ��
		// onClosePanel();
		// �õ�ѡ��������
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
		this.setValue("IPD_NO", parm.getValue("IPD_NO"));
		this.setValue("CASE_NO", parm.getValue("CASE_NO"));
		TParm actionParm = ExeQuery().getRow(0);
		// System.out.println("actionParm:::"+actionParm);
		TParm action = new TParm();
		action.setData("CASE_NO", parm.getData("CASE_NO"));

		action.setData("STATION_CODE", parm.getData("STATION_CODE"));
		action.setData("POPEDEM", this.isInwPopedem());
		this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
		// ����
		TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
				.getComponent("INW_STATION_CODE");
		h.setUserID(Operator.getID());
		h.onQuery();
		// ����
		TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
				.getComponent("INW_DEPT_CODE");
		d.setStationCode(this.getValueString("INW_STATION_CODE"));
		d.onQuery();
		// ����ҽʦ
		TextFormatSYSOperator k = (TextFormatSYSOperator) this
				.getComponent("INW_VC_CODE");
		k.setDept(this.getValueString("INW_DEPT_CODE"));
		k.onQuery();
		// ��Ժ����
		TextFormatSYSStation h1 = (TextFormatSYSStation) this
				.getComponent("STATION_CODEOUT");
		h1.setDeptCode(actionParm.getValue("DEPT_CODE"));
		h1.onQuery();
		// ��Ժ����ҽʦ
		TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
				.getComponent("VC_CODEOUT");
		k1.setDept(actionParm.getValue("DEPT_CODE"));
		k1.onQuery();

		// ����1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// ����2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// ����3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// ����
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// ����
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// סԺ��
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// ������
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// ��Ժʱ��
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// ����Ȩ��ע��
		action.setData("INW", "SAVE_FLG", true);
		action.setData("INW", "LEDUI", parm.getData("LEDUI"));
		this.setValue("BED_NO", actionParm.getValue("BED_NO_DESC"));

		this.setValue("SEX", actionParm.getValue("SEX_CODE"));
		this.setValue("SERVICE_LEVELIN", actionParm.getValue("SERVICE_LEVEL"));
		this.setValue("ADM_DATE", actionParm.getTimestamp("IN_DATE"));
		this.setValue("TOTAL_AMT", actionParm.getDouble("TOTAL_AMT"));
		this.setValue("INW_VC_CODE", actionParm.getValue("VS_DR_CODE"));

		onClosePanel();
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		tabPane.setEnabled(false);
		getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
		((TTable) this.getComponent(TABLE)).setVisible(false);
		workPanelTag = tag;
	}

	private TParm ExeQuery() {
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		TParm actionParm = null;
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")) {
				// �õ���ѯSQL
				String sqlStr = this.creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				// System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// //�����ѯΪ�շ���
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //���Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return null;
			// }
			// //�õ�����ҳǩ��TAG��������ѯ
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "IN");
			// return null;
			// }
			// //����TABLE�ϵ�����
			// this.setTableData(actionParm, "IN");
		} else {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);
			// System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// //�����ѯΪ�շ���
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //���Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return;
			// }
			// //�õ�����ҳǩ��TAG��������ѯ
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "OUT");
			// return;
			// }
			// //����TABLE�ϵ�����
			// this.setTableData(actionParm, "OUT");
		}
		return actionParm;
	}

	/**
	 * ��ʼ��Ȩ��
	 */
	public void onInitPopeDem() {
		// Ȩ�޿ɷ�ѡ�����
		if (!this.getPopedem("deptEnabled") && !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
			this.callFunction("UI|DEPT_CODEOUT|setEnabled", false);
		}
		// Ȩ�޿ɷ�ѡ����
		if (!this.getPopedem("stationEnabled")
				&& !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|STATION_CODE|setEnabled", false);
			this.callFunction("UI|STATION_CODEOUT|setEnabled", false);
		}
		// ѡ��ʿվCOMBO������ʿվ
		this.setInwPopedem(this.getPopedem("InwCheckEnabled"));
	}

	/**
	 * ˫���¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubled(int row) {
		//this.messageBox("==come in11111111=");
		if (row < 0)
			return;
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		TParm parm = this.getSelectRowData(TABLE);
		if (selType == 0) {
			if (parm.getData("IN_DATE") == null
					&& parm.getValue("CASE_NO").length() == 0) {
				// ����û��סԺ��Ϣ��
				// this.messageBox("E0151");
				return;
			}
			// �õ�Ԥ�����������������ʾ��ǿ��
			double rPrice = parm.getDouble("CUR_AMT");
			// ��ɫ����
			double yellowPrice = parm.getDouble("YELLOW_SIGN");
			if (rPrice <= yellowPrice) {
				if (this.messageBox("��ʾ��Ϣ Tips",
						"Ԥ�������㣡\n Paying insufficient balance gold!",
						this.YES_NO_OPTION) != 0)
					return;
			}
		}
		// �ٴ�·����֤begin
		if (this.getRunFlg().equals("CLPCHECKITEMMAIN")
				|| this.getRunFlg().equals("CLPVARIATION")) {
			if (parm.getValue("CLNCPATH_CODE") == null
					|| "".equals(parm.getValue("CLNCPATH_CODE"))) {
				this.messageBox("�ò���û�н����ٴ�·����������ѡ��");
				return;
			}
		}
		// ��Ժ
		if (selType == 1) {
			if (this.getRunFlg().equals("CLPMANAGEM")) {
				this.messageBox("�ò����Ѿ���Ժ���ܽ����ٴ�·����������ѡ��");
				return;
			}
		}
		// �ٴ�·����֤end

		this.initOtherUi();
	}

	public void initOtherUi() {
		// ҽ��վ
		if ("ODI".equals(this.getRunFlg())) {
			if ("en".equals(this.getLanguage())) {
				this.setTitle("IP Station");
			} else {
				this.setTitle("סԺҽ��վ");
			}
			// �ж��Ƿ����
			if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
				if (this.messageBox("�Ƿ���� Whether to unlock", PatTool
						.getInstance().getLockParmString(
								this.getValueString("MR_NO")), 0) == 0) {
					PatTool.getInstance().unLockPat(
							this.getValueString("MR_NO"));
					PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
							"ODI");

				} else {
					return;
				}
			} else {
				// ����
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");
			}
			passcheck();
			// $$==== modified by lx 2012/03/19����1024*768ҽ��վ���� start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				// System.out.println("is 1024.");
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");
			} else {
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19����1024*768ҽ��վ���� end====$$//

		}
		// ҽ��վ
		if ("OIDR".equals(this.getRunFlg())) {
			//this.messageBox("===OIDR===");
			// $$==== modified by lx 2012/03/19����1024*768���ﴦ�� start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("����");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");

			} else {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("����");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19����1024*768���ﴦ�� end====$$//
		}

		// ��ʿվ���
		if ("INWCHECK".equals(this.getRunFlg())) {
			this.setTitle("סԺ��ʿվ���");
			this.runPane("INWSTATIONCHECK", "inw\\INWOrderCheckMain.x");
		}
		// ��ʿִ��
		if ("INWEXE".equals(this.getRunFlg())) {
			this.setTitle("סԺ��ʿվִ��");
			this.runPane("INWSTATIONEXECUTE", "inw\\INWOrderExecMain.x");
		}
		// סԺ�Ƽ�
		if ("IBS".equals(this.getRunFlg())) {
			this.setTitle("סԺ�Ƽ�");
			this.runPane("IBSSTATION", "ibs\\IBSOrderm.x");
		}
		// ���ò�ѯ
		if ("IBSQUERYFEE".equals(this.getRunFlg())) {
			this.setTitle("���ò�ѯ");
			this.runPane("IBSQUERYFEESTATION", "ibs\\IBSSelOrderm.x");
		}
		// �ɷ���ҵ
		if ("IBSPAYBILL".equals(this.getRunFlg())) {
			this.setTitle("�ɷ���ҵ");
			if (!checkNo()) {
				// ��δ�������ȿ���!
				this.messageBox("E0014");
				return;
			}
			this.runPane("IBSCUTBILLSTATION", "bil\\BilIBSRecp.x");
		}
		// ��������
		if ("MRO".equals(this.getRunFlg())) {
			this.setTitle("��������");
			this.runPane("MROSTATION", "mro\\MRO_Chrtvetrec.x");
		}
		// ������̬��ѯ
		if ("ADMCHG".equals(this.getRunFlg())) {
			this.setTitle("������̬��ѯ");
			this.runPane("ADMCHGSTATION", "adm\\ADMQueryChgLog.x");
		}
		// ҽ������ӡ
		if ("SHEET".equals(this.getRunFlg())) {
			this.setTitle("ҽ������ӡ");
			this.runPane("INWSHEET", "inw\\INWOrderSheetPrtAndPreView.x");
		}
		// ����
		if ("NSSORDER".equals(this.getRunFlg())) {
			this.setTitle("����");
			this.runPane("NSSORDER", "nss\\NSSOrder.x");
		}
		// ���ͽɷ�
		if ("NSSCHAR".equals(this.getRunFlg())) {
			this.setTitle("���ͽɷ�");
			this.runPane("NSSCHARGE", "nss\\NSSCharge.x");
		}

		// �ٴ�·��׼��׼��
		if ("CLPMANAGEM".equals(this.getRunFlg())) {
			// System.out.println("�����ٴ�·��׼��׼��");
			this.setTitle("�ٴ�·��׼��׼��");
			this.runPane("CLPMANAGEM", "clp\\CLPManagem.x");
		}
		// �ٴ�·���������
		if ("CLPVARIATION".equals(this.getRunFlg())) {
			// System.out.println("�����ٴ�·���������");
			this.setTitle("�ٴ�·���������");
			this.runPane("CLPVARIATION", "clp\\CLPVariation.x");
		}
		// �ٴ�·���ؼ�������Ŀִ��
		if ("CLPCHECKITEMMAIN".equals(this.getRunFlg())) {
			// System.out.println("�ٴ�·���ؼ�������Ŀִ��");
			this.setTitle("�ٴ�·���ؼ�������Ŀִ��");
			this.runPane("CLPCHECKITEMMAIN", "clp\\CLPChkItemMain.x");
		}
		// ת�Ƽ�¼ά��
		if ("TRANSHOSPLOG".equals(this.getRunFlg())) {
			// System.out.println("�ٴ�·���ؼ�������Ŀִ��");
			this.setTitle("ת�Ƽ�¼ά��");
			this.runPane("TRANSHOSPLOG", "adm\\ADM_TRANS_LOGUI.x");
		}
		// ����Ԥ����
		if ("BILPAY".equals(this.getRunFlg())) {
			this.setTitle("Ԥ����");
			this.runPane("BILPAY", "bil\\BILPay.x");
		}

		// //����ת����
		// if("ADMTRAN".equals(this.getRunFlg())){
		// this.runPane("ADMTRANSTATION","adm\\ADMWaitTrans.x");
		// }
	}

	public boolean checkNo() {
		TParm parm = new TParm();
		parm.setData("RECP_TYPE", "IBS");
		parm.setData("CASHIER_CODE", Operator.getID());
		parm.setData("STATUS", "0");
		parm.setData("TERM_IP", Operator.getIP());
		TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
		String updateNo = noParm.getValue("UPDATE_NO", 0);
		if (updateNo == null || updateNo.length() == 0) {
			return false;
		}
		return true;
	}

	// $$==============add by lx 2012/02/07���봲ͷ������==========================$$//
	/**
	 * ��ͷ��
	 */
	public void onBedCard() {
		TTable table = ((TTable) this.getComponent(TABLE));

		// table.clearSelection();
		TParm parm = new TParm();
		parm = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWBedCard.x");
		// this.messageBox(""+parm);

		if (parm != null) {
			this.onClear();
			this.setValue("INW_VC_CODE", "");
			// this.messageBox("case no"+parm.getValue("CASE_NO"));
			//
			// String strCaseNo=parm.getValue("CASE_NO");
			String strIPDNo = parm.getValue("IPD_NO");
			this.setValue("INW_DEPT_CODE", "");
			this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
			this.setValue("MR_NO", parm.getValue("MR_NO"));
			this.setValue("IPD_NO", strIPDNo);
			// this.getTCheckBox("ALLO_FLG").setSelected(false);
			this.onQuery();
			// ���������¼��ȡ������
			// ����
			int currentRow = 0;
			table.setSelectedRow(currentRow);
			onTableClicke(currentRow);
			onTableDoubled(currentRow);
			//
		}

	}

	// $$==============add by lx
	// 2012/02/07���봲ͷ������END==========================$$//

	/**
	 * ������Ϣ
	 */
	public void onPatInfo() {

		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openDialog("%ROOT%\\config\\adm\\AdmPatinfo.x", parm);

	}

	/**
	 * TTablePanel�л��¼�
	 */
	public void onChangedPanel() {
		// this.messageBox("==onChangedPanel==");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		// SHIBL 20120425 MODIFY
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// ���ÿ���(��Ժ)
			this.setValue("INW_DEPT_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// ����ҽʦ����ʿվ��
			this.setValue("INW_VC_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		} else {
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODE", Operator.getDept());
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODE", Operator.getStation());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// Ĭ��������Ժҳǩ��סԺ����
		this.setValue("ADM_DATE", sysDate);
		// Ĭ��������ʼ����
		this.setValue("START_DATE", sysDate);
		// Ĭ��������ֹ����
		this.setValue("END_DATE", StringTool.rollDate(sysDate, 1));
		// Ĭ��������ʼ����
		this.setValue("START_DATEOUT", StringTool.rollDate(sysDate, -7));
		// Ĭ��������ֹ����
		this.setValue("END_DATEOUT", StringTool.rollDate(sysDate, 1));
		// Ĭ�����ó�Ժҳǩ����Ժ����
		// this.setValue("ADM_DATEOUT", SystemTool.getInstance().getDate());
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// �����ؿؼ���Ϣ(��Ժ)
		this
				.clearValue("BED_NO;IPD_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;TOTAL_AMT;PAY_INS;YJJ_PRICE;GREED_PRICE;YJYE_PRICE;PRESON_NUM;DIE_CODE");
		// �����ؿؼ���Ϣ(��Ժ)
		this
				.clearValue("BILL_STATUS;IPD_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;SERVICE_LEVELOUT;DIE_CODE_OUT");
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		switch (selType) {
		case 0:

			// TABLE��Ժ
			this
					.getTTable(TABLE)
					.setHeader(
							"����,80;����,80;�Ա�,60,SEX;����,80;��Ժ����,120,Timestamp;סԺ����,80;����ҽʦ,100,USER_ID;�������,120;�����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;��ʳ���,100,DIE_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
			this
					.getTTable(TABLE)
					.setEnHeader(
							"BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;MrNo;IpdNo;Mr Check;Service Level");
			this
					.getTTable(TABLE)
					.setParmMap(
							"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;DIE_CONDITION;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
			this
					.getTTable(TABLE)
					.setColumnHorizontalAlignmentData(
							"0,Left;1,Left;2,Left;3,Left;4,Left;5,Right;6,Left;7,Left;8,Left;9,left;10,left;11,Right;12,left;13,right;14,right;15,left;16,left;17,left;");
			break;
		case 1:

			// TABLE��Ժ
			this
					.getTTable(TABLE)
					.setHeader(
							"����,80;����,80;�Ա�,60,SEX;����,80;��Ժ����,120,Timestamp;��Ժ����,120,Timestamp;סԺ����,80;��Ժ���,120;���ʽ,100,CTZ_CODE;����״̬,120,BILL_STATUS;�ٴ�·��,100,CLNCPATH_CODE_OUT;��ʳ���,100,DIE_CODE_OUT;������,100;סԺ��,100;�������,140,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
			this
					.getTTable(TABLE)
					.setEnHeader(
							"BedNo;Name;Gender;Age;Admission Date;Discharge Date;Days;Discharge Diagnosis;PayType;Financial Status;CLNCPATH;MrNo;IpdNo;Mr Check;Service Level");
			this
					.getTTable(TABLE)
					.setParmMap(
							"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DS_DATE;DAYNUM;MAINDIAG;CTZ1_CODE;BILL_STATUS;CLNCPATH_CODE;DIE_CONDITION;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE;HEIGHT;WEIGHT;CASE_NOOUT;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
			this
					.getTTable(TABLE)
					.setColumnHorizontalAlignmentData(
							"0,Left;1,Left;2,Left;3,Left;4,Left;5,left;6,right;7,Left;8,Left;9,left;10,left;11,Right;12,Right;13,Right;14,left;15,left;16,left;");
			break;
		}
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ���
	 */
	public void onClear() {
		 //this.messageBox("���begin");
		// �õ�TabbedPane�ؼ�

		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// ���ÿ���(��Ժ)
			this.setValue("INW_DEPT_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// ����ҽʦ����ʿվ��
			this.setValue("INW_VC_CODE", "");
		} else {
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODE", Operator.getDept());
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODE", Operator.getStation());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// Ĭ��������Ժҳǩ��סԺ����
		this.setValue("ADM_DATE", sysDate);
		// Ĭ��������ʼ����
		this.setValue("START_DATE", sysDate);
		// Ĭ��������ֹ����
		this.setValue("END_DATE", sysDate);
		// Ĭ�����ó�Ժҳǩ��סԺ����
		this.setValue("ADM_DATEOUT", sysDate);
		
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// ����ҳǩΪ��Ժ
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		
		this.setValue("VC_CODE", "");
		// this.messageBox("selType"+selType);

		this.setValue("DIE_CODE", "");
		this.setValue("DIE_CODE_OUT", "");
		// // ��ʾҳǩѡ���״̬(��Ժ)
		// tabPane.setSelectedIndex(0);
		// �����ؿؼ���Ϣ(��Ժ)
		this
				.clearValue("BED_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;YELLOW;PRESON_NUM;YJJ_PRICE;YJYE_PRICE;GREED_PRICE;CASE_NO;IPD_NO;TOTAL_AMT");
		// �����ؿؼ���Ϣ(��Ժ)
		this
				.clearValue("BED_NOOUT;MR_NOOUT;PAT_NAMEOUT;PRESON_NUMOUT;CASE_NOOUT;IPD_NOOUT;SERVICE_LEVELOUT;DEPT_CODEOUT;VC_CODEOUT");
		// �Ƴ�����ҳ��
		this.onClosePanel();
		// out("���end");
	}

	/**
	 * ������ҳ��������ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 */
	public void queryDataOtherTPane(TParm parm, String type) {
		// System.out.println("������ѯ���"+parm);
		TParm action = new TParm();
		if (workPanelTag.toUpperCase().equals("INWSTATIONCHECK")
				|| workPanelTag.toUpperCase().equals("INWSTATIONEXECUTE")
				|| workPanelTag.toUpperCase().equals("INWSHEET")) {
			if ("IN".equals(type)) {
				// ��Ժ
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// ����1
				action
						.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
								0));
				// ����2
				action
						.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
								0));
				// ����3
				action
						.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
								0));
				// ����
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			} else {
				// ��Ժ
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// ����1
				action
						.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
								0));
				// ����2
				action
						.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
								0));
				// ����3
				action
						.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
								0));
				// ����
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			}
			// ���¸�ֵ�����������¼��ص����ж��Ƿ��Ѿ���������Ѿ�������ֻ��ֵ��ʼ������(INW������ֻҪ���ǿ��ַ����Ϳ���)
			getTPanel().addItem(workPanelTag, "INW", action, false);
		}
		// //ҽ��վ
		// if(workPanelTag.toUpperCase().equals("STATIONMAIN")){
		// if ("IN".equals(type)) {
		// //��Ժ
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //�õ���Ӧҩ��
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //ֹͣ����
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// else {
		// //��Ժ
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //�õ���Ӧҩ��
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //ֹͣ����
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// //���¸�ֵ�����������¼��ص����ж��Ƿ��Ѿ���������Ѿ�������ֻ��ֵ��ʼ������(ODI������ֻҪ���ǿ��ַ����Ϳ���)
		// getTPanel().addItem(workPanelTag,"ODI",action,false);
		// }
		// �Ƽ�
		if (workPanelTag.toUpperCase().equals("IBSSTATION")) {
			// ��Ժ
			action.setData("IBS", "CASE_NO", parm.getData("CASE_NO", 0));
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", parm.getData("IPD_NO", 0));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action
					.setData("IBS", "STATION_CODE", this
							.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", parm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", this.getValue("BED_NO"));
			// ����1
			action.setData("IBS", "CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
			// ����2
			action.setData("IBS", "CTZ2_CODE", parm.getData("CTZ2_CODE", 0));
			// ����3
			action.setData("IBS", "CTZ3_CODE", parm.getData("CTZ3_CODE", 0));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", parm.getData("RED_SIGN", 0));
			// ��ɫ����
			action
					.setData("IBS", "YELLOW_SIGN", parm.getData("YELLOW_SIGN",
							0));
			// ��ɫͨ��
			action.setData("IBS", "GREENPATH_VALUE", parm.getData(
					"GREENPATH_VALUE", 0));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", parm.getData("TOTAL_AMT", 0));
			getTPanel().addItem(workPanelTag, "IBS", action, false);
		}
		// ������ҳ
		if (workPanelTag.toUpperCase().equals("MROSTATION")) {
			action.setData("MRO", "CASE_NO", parm.getData("CASE_NO", 0));
		}
		// ADM������̬��ѯ
		if (workPanelTag.toUpperCase().equals("ADMCHGSTATION")) {
			action.setData("ADM", "CASE_NO", parm.getData("CASE_NO", 0));
			action.setData("ADM", "MR_NO", parm.getData("MR_NO", 0));
			action.setData("ADM", "IPD_NO", parm.getData("IPD_NO", 0));
			action.setData("ADM", "ADM_DATE", parm.getData("IN_DATE", 0));
		}
		if (workPanelTag.toUpperCase().equals("CLPMANAGEM")) {
			action.setData("CLPMANAGEM", "CASE_NO", parm.getData("CASE_NO", 0));
			getTPanel().addItem(workPanelTag, "CLPMANAGEM", action, false);
		}
		// ��Ԥ�ӽ�
		if (workPanelTag.toUpperCase().equals("BILPAY")) {
			action.setData("BILPAY", "CASE_NO", parm.getData("CASE_NO", 0));
			getTPanel().addItem(workPanelTag, "BILPAY", action, false);
		}
	}

	/**
	 * סԺҽ��վ��ѯ
	 */
	public void onQuery() {
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// �õ���ѯSQL
				String sqlStr = creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				// System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "IN");
		} else {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);
			// System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "OUT");
		}
	}

	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryForMrNo() {
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// �õ���ѯSQL
				String sqlStr = creatInwQuerySqlForMrNo(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySqlForMrNo(queryData, "IN",
						stationFlg);
				// System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "IN");
		} else {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySqlForMrNo(queryData, "OUT",
					stationFlg);
			// System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "OUT");
		}
	}

	/**
	 * ��ʿ��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String creatInwQuerySQL(TParm parm, String type) {
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����
		String stationStr = "";
		if ("IN".equals(type)) {
			stationStr = " AND A.STATION_CODE='"
					+ parm.getValue("STATION_CODE") + "'";
		} else {
			stationStr = " AND B.STATION_CODE='"
					+ parm.getValue("STATION_CODE") + "'";
		}
		// ����
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
							+ "'";
		}
		// ================liming modify 20120217 start
		String vsDoctor = "";
		if ("IN".equals(type)) {
			vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
					: " AND B.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		}
		// ================liming modify 20120217 end

		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");
		// ��Ժ
		if (type.equals("IN")) {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ stationStr
						+ region
						+ vsDoctor
						+ deptCode
						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " AND B.CASE_NO='"
						// + parm.getValue("CASE_NO") + "'" +
						// " ORDER BY A.BED_NO";
						+ parm.getValue("CASE_NO")
						+ "'"
						+ " ORDER BY A.BED_NO_DESC";
				// System.out.println("sql3::" + sql);
			} else {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ stationStr
						+ region + vsDoctor + deptCode + // =====pangben modify
						// 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						// + " ORDER BY A.BED_NO";
						+ " ORDER BY A.BED_NO DESC";
				// System.out.println("sql4::" + sql);
			}
			// ��Ժ
		} else {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ stationStr
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ deptCode
						+ region
						+ // ============pangben modify 20110512
						" AND A.CASE_NO='" + parm.getValue("CASE_NO") + "'"
						// + billStatus + " ORDER BY A.BED_NO";
						+ billStatus + " ORDER BY A.BED_NO_DESC";
				// System.out.println("sql5::"+sql);
			} else {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ stationStr
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ deptCode
						+ region
						+ // ============pangben modify 20110512
						// billStatus + " ORDER BY A.BED_NO";
						billStatus + " ORDER BY A.BED_NO_DESC";
				// System.out.println("sql6::"+sql);
			}
		}
		return sql;
	}

	public String creatInwQuerySqlForMrNo(TParm parm, String type) {
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
							+ "'";
		}
		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");
		// ��Ժ
		if (type.equals("IN")) {
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			if (parm.getValue("CASE_NO").length() != 0) {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ region
						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " AND B.CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " ORDER BY A.BED_NO";
				// System.out.println("sql3::" + sql);
			} else {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ region
						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " ORDER BY A.BED_NO";
				// System.out.println("sql4::" + sql);
			}
			// ��Ժ
		} else {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ region
						+ // ============pangben modify 20110512
						" AND A.CASE_NO='"
						+ parm.getValue("CASE_NO")
						+ "'"
						+ billStatus + " ORDER BY A.BED_NO";
				// System.out.println("sql5::"+sql);
			} else {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ region + // ============pangben modify 20110512
						billStatus + " ORDER BY A.BED_NO";
				// System.out.println("sql6::"+sql);
			}
		}
		return sql;
	}

	/**
	 * ����ODIҽ��վSQL
	 * 
	 * @param parm
	 *            TParm ��ѯ����
	 * @param type
	 *            String ���Ժ���
	 * @param flg
	 *            boolean UI���()
	 * @return String
	 */
	public String createODIQuerySQL(TParm parm, String type, boolean flg) {
		//this.messageBox("come in");
		// SQL
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����
		String stationStr = "";
		if ("IN".equals(type)) {
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
							+ "'";
		} else {
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND B.STATION_CODE='" + parm.getValue("STATION_CODE")
							+ "'";
		}
		// ����
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
							+ "'";
		}
		// ================liming modify 20120217 start
		String vsDoctor = "";
		if ("IN".equals(type)) {
			vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
					: " AND B.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		}
		// ================liming modify 20120217 end
		
		//add by lx 2012-7-3
		String dieCode = "";
		if ("IN".equals(type)) {
			dieCode = parm.getValue("DIE_CODE").length() == 0 ? ""
					: " AND B.DIE_CONDITION='" + parm.getValue("DIE_CODE") + "'";
		}else{
			
			dieCode = parm.getValue("DIE_CODE_OUT").length() == 0 ? ""
					: " AND A.DIE_CONDITION='" + parm.getValue("DIE_CODE_OUT")
							+ "'";
		}
		//$$=============add by lx 2012/07/05 START=====================$$//
		String admDateSQL = "";
		String strADMDate = StringTool.getString((Timestamp) parm
				.getData("ADM_DATE"), "yyyyMMdd");
		if ("IN".equals(type)) {

			admDateSQL = parm.getValue("ADM_DATE").length() == 0 ? ""
					: " AND (B.IN_DATE BETWEEN TO_DATE('"
							+ strADMDate
							+ "'||'000000','YYYYMMDDHH24MISS') AND TO_DATE('"
							+ strADMDate
							+ "'||'235959','YYYYMMDDHH24MISS'))";
		} else {

			admDateSQL = parm.getValue("ADM_DATE").length() == 0 ? ""
					: " AND (A.IN_DATE BETWEEN TO_DATE('"
							+ strADMDate
							+ "'||'000000','YYYYMMDDHH24MISS') AND TO_DATE('"
							+ strADMDate
							+ "'||'235959','YYYYMMDDHH24MISS'))";
			;
		}
		//$$=============add by lx 2012/07/05 END=====================$$//
		
		
		//
		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("����ʱ��:"+endDate);
		// System.out.println("���:"+flg);
		if (flg) {
			// ��Ժ
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ vsDoctor
							+ dieCode
							+ admDateSQL
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'" + " ORDER BY B.IN_DATE DESC";
					//System.out.println("sql3============================" + sql);
				} else {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ vsDoctor
							+ dieCode
							+ admDateSQL
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY B.IN_DATE DESC";
					//System.out.println("sql4::" + sql);
				}
				// ��Ժ
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							/**
							 * + " AND (A.DS_DATE BETWEEN TO_DATE('" +
							 * startDateOut + "','YYYYMMDD') AND TO_DATE('" +
							 * endDateOut + "','YYYYMMDD') " +
							 * " OR A.IN_DATE BETWEEN TO_DATE('" + startDate +
							 * "','YYYYMMDD') AND TO_DATE('" + endDate +
							 * "','YYYYMMDD'))"
							 **/
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode
							+ region
							+ dieCode
							+ admDateSQL
							+ // ============pangben modify 20110512
							" AND A.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'"
							+ billStatus + " ORDER BY A.IN_DATE DESC";
					//System.out.println("sql5::" + sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode
							+ dieCode
							+ admDateSQL
							+ region + // ============pangben modify 20110512
							billStatus + " ORDER BY A.IN_DATE DESC";
					//System.out.println("sql6::" + sql);
				}
			}
		} else {
			// ��Ժ
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ dieCode
							+ admDateSQL
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND (B.VS_DR_CODE='"
							+ Operator.getID()
							+ "' OR ATTEND_DR_CODE='"
							+ Operator.getID()
							+ "' OR DIRECTOR_DR_CODE='"
							+ Operator.getID()
							+ "')"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO") + "' ORDER BY B.IN_DATE DESC";
					//System.out.println("sql7::" + sql);
				} else {
					sql = "SELECT  B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ dieCode
							+ admDateSQL
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND (B.VS_DR_CODE='"
							+ Operator.getID()
							+ "' OR ATTEND_DR_CODE='"
							+ Operator.getID()
							+ "' OR DIRECTOR_DR_CODE='"
							+ Operator.getID()
							+ "') ORDER BY B.IN_DATE DESC";
					//System.out.println("sql8::" + sql);
				}
				// ��Ժ
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode
							+ billStatus
							+ region
							+ dieCode
							+ admDateSQL
							+ // ===========pangben modify 20110512
							" AND A.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "' ORDER BY A.IN_DATE DESC";
					// System.out.println("sql9::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode
							+ dieCode
							+ admDateSQL
							+ region + // ===========pangben modify 20110512
							billStatus + " ORDER BY A.IN_DATE DESC";
					// System.out.println("sql10::"+sql);
				}
			}
		}
		//System.out.println("sql:::::::::"+sql);
		return sql;
	}

	public String createODIQuerySqlForMrNo(TParm parm, String type, boolean flg) {
		// SQL
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
							+ "'";
		}
		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("����ʱ��:"+endDate);
		// System.out.println("���:"+flg);
		if (flg) {
			// ��Ժ
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'" + " ORDER BY B.IN_DATE DESC";
					// System.out.println("sql3::" + sql);
				} else {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY B.IN_DATE DESC";
					// System.out.println("sql4::" + sql);
				}
				// ��Ժ
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ region
							+ // ============pangben modify 20110512
							" AND A.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'"
							+ billStatus + " ORDER BY A.IN_DATE";
					// System.out.println("sql5::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)" + region + // ============pangben
							// modify
							// 20110512
							billStatus + " ORDER BY A.IN_DATE DESC";
					// System.out.println("sql6::"+sql);
				}
			}
		} else {
			// ��Ժ
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "' ORDER BY B.IN_DATE DESC";
					// System.out.println("sql7::" + sql);
				} else {
					sql = "SELECT  B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,B.MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS,B.DIE_CONDITION"
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY B.IN_DATE DESC";
					// System.out.println("sql8::" + sql);
				}
				// ��Ժ
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
							+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ billStatus
							+ region
							+ // ===========pangben modify 20110512
							" AND A.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "' ORDER BY A.IN_DATE DESC";
					// System.out.println("sql9::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DIE_CONDITION"
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)" + region + // ===========pangben
							// modify
							// 20110512
							billStatus + " ORDER BY A.IN_DATE DESC";
					// System.out.println("sql10::"+sql);
				}
			}
		}
		// System.out.println("sql:::::::::"+sql);
		return sql;
	}

	/**
	 * �õ���ѡ��
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ��ѯ�����TABLE�е�������ʾ
	 * 
	 * @param parm
	 *            TParm ��Ҫ���������
	 * @param type
	 *            String �������ݵ���� INΪ��Ժ,OUTΪ��Ժ
	 */
	public void setTableData(TParm parm, String type) {
		// ����TABLE����
		if (type.equals("IN")) {
			// this.messageBox("IN come in");
			// ��Ժ
			TParm actionParm = this.filterTParmData(parm);
			// out("��Ժ����"+actionParm);
			// ����TABLE����(��Ժ)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);
			// ���ò�ѯ���ݺ�ҳ����ʾ���ݶ�Ӧ
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
					.setModifyTag("DEPT_CODE:DEPT_CODE;STATION_CODE:STATION_CODE;BED_NO:BED_NO_DESC;MR_NO:MR_NO;PAT_NAME:PAT_NAME;SEX:SEX_CODE;HEIGHT:HEIGHT;WEIGHT:WEIGHT;YELLOW:YELLOW_SIGN;ADM_DATE:IN_DATE:Timestamp;YJJ_PRICE:TOTAL_BILPAY;YJYE_PRICE:CUR_AMT;GREED_PRICE:GREENPATH_VALUE;VC_CODE:VS_DR_CODE;CASE_NO:CASE_NO;IPD_NO:IPD_NO;TOTAL_AMT:TOTAL_AMT;SERVICE_LEVELIN:SERVICE_LEVEL;CLNCPATH_CODE:CLNCPATH_CODE;CTZ_CODE:CTZ1_CODE");
			// ��������
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PRESON_NUM", personNum);
		} else {
			// ��Ժ
			TParm actionParm = this.filterTParmData(parm);
			// out("��Ժ����"+actionParm);
			// ����TABLE����(��Ժ)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);
			// ���ò�ѯ���ݺ�ҳ����ʾ���ݶ�Ӧ
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
					.setModifyTag("DEPT_CODEOUT:DEPT_CODE;STATION_CODEOUT:STATION_CODE;MR_NOOUT:MR_NO;PAT_NAMEOUT:PAT_NAME;VC_CODEOUT:VS_DR_CODE;CASE_NOOUT:CASE_NO;IPD_NOOUT:IPD_NO;SERVICE_LEVELOUT:SERVICE_LEVEL;CLNCPATH_CODE_OUT:CLNCPATH_CODE");
			// ��������
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PRESON_NUMOUT", personNum);
		}
	}

	/**
	 * ���˲�ѯ���Ĳ���������Ϣ����סԺ���ڼ��������סԺ���������뵽��ѯ������TParm��
	 * 
	 * @param parm
	 *            TParm ��Ҫ���˵�����
	 * @return TParm
	 */
	public TParm filterTParmData(TParm parm) {
		// System.out.println("����TABLE����"+parm);
		/*
		 * ����ѭ��������������е����������䣬��Ժ������סԺ����
		 * (�����ֶ�:SYS_PATINFO.BIRTH_DATE��ӦKEY(AGE),��Ժ�����ֶ�
		 * :ADM_INP.IN_DATE��ӦKEY(DAYNUM))
		 */
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���ص�����
		int rowCount = parm.getCount("PAT_NAME");
		for (int i = 0; i < rowCount; i++) {
			Timestamp temp = parm.getTimestamp("BIRTH_DATE", i) == null ? sysDate
					: parm.getTimestamp("BIRTH_DATE", i);
			// ��������
			String age = "0";
			if (parm.getTimestamp("IN_DATE", i) != null)
				age = OdiUtil.getInstance().showAge(temp,
						parm.getTimestamp("IN_DATE", i));
			else
				age = "";
			parm.addData("AGE", age);
			// ����סԺ����
			Timestamp tp = parm.getTimestamp("DS_DATE", i);
			if (tp == null) {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
							"00:00:00"), StringTool.setTime(parm.getTimestamp(
							"IN_DATE", i), "00:00:00"));
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			} else {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					// ===============modify by chenxi 20120703 start
					days = StringTool.getDateDiffer(StringTool.setTime(parm
							.getTimestamp("DS_DATE", i), "00:00:00"),
							StringTool.setTime(parm.getTimestamp("IN_DATE", i),
									"00:00:00"));
					// =========== modify by chenxi 20120703 stop
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			}
		}
		return parm;
	}

	/**
	 * �õ�סԺҽ��վ�Ĳ�ѯ����
	 * 
	 * @param type
	 *            String INΪ��Ժ;OUTΪ��Ժ
	 * @return TParm
	 */
	public TParm getQueryData(String type) {
		TParm result = new TParm();
		if (type.equals("IN")) {
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// ����
				result.setData("DEPT_CODE", this.getValue("INW_DEPT_CODE"));
				// ����
				result.setData("STATION_CODE", this
						.getValue("INW_STATION_CODE"));
				// ����ҽʦ
				result.setData("VC_CODE", this.getValue("INW_VC_CODE"));
			} else {
				// ����
				result.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
				// ����
				result.setData("STATION_CODE", this.getValue("STATION_CODE"));
				// ����ҽʦ
				result.setData("VC_CODE", this.getValue("VC_CODE"));
			}
			// ����
			// result.setData("BED_NO",this.getValue("BED_NO"));
			// ������
			String mrNo = getValueString("MR_NO");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				
				//modify by huangtt 20160930 EMPI���߲�����ʾ  start
				Pat pat = Pat.onQueryByMrNo(mrNo);
				if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			            mrNo= pat.getMrNo();
			    }
				setValue("PAT_NAME", pat.getName());
				//modify by huangtt 20160930 EMPI���߲�����ʾ  end
				
				
				setValue("MR_NO", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// סԺ��
			String ipdNo = getValueString("IPD_NO");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NO", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// ��ѯ�õ������SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
			// MR_NO=<MR_NO>
			TParm action = new TParm();
			if (mrNo.length() != 0) {
				action.setData("MR_NO", mrNo);
			}
			if (ipdNo.length() != 0) {
				action.setData("IPD_NO", ipdNo);
			}
			// String caseNo = getCaseNo(action);
			String sql = " SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO='"
					+ mrNo + "'";
			String caseNo = "";
			TParm caseParm = new TParm(this.getDBTool().select(sql));
			if (caseParm.getCount() <= 0) {
				this.messageBox("��ѯ���ξ����ʧ��" + action.getErrText());
				return caseParm;
			}
			caseNo = caseParm.getValue("CASE_NO", 0);
			result.setData("CASE_NO", caseNo);
			// סԺ����
			result.setData("ADM_DATE", this.getValue("ADM_DATE"));
			
			result.setData("DIE_CODE", this.getValue("DIE_CODE"));
			
		} else {
			// �õ���Ժҳǩ����
			// סԺ��ʼ����
			result.setData("START_DATE", this.getValue("START_DATE"));
			// סԺ��ֹ����
			result.setData("END_DATE", this.getValue("END_DATE"));
			// ��Ժ��ʼ����
			result.setData("START_DATEOUT", this.getValue("START_DATEOUT"));
			// ��Ժ��ֹ����
			result.setData("END_DATEOUT", this.getValue("END_DATEOUT"));
			// ����
			result.setData("DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ����
			result.setData("STATION_CODE", this.getValue("STATION_CODEOUT"));
			// ����ҽʦ
			result.setData("VC_CODE", this.getValue("VC_CODEOUT"));
			// ������
			String mrNo = getValueString("MR_NOOUT");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				
				//modify by huangtt 20160930 EMPI���߲�����ʾ  start
				Pat pat = Pat.onQueryByMrNo(mrNo);
				if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			            mrNo= pat.getMrNo();
			    }
				setValue("PAT_NAMEOUT", pat.getName());
				//modify by huangtt 20160930 EMPI���߲�����ʾ  end
				
				setValue("MR_NOOUT", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// סԺ��
			String ipdNo = getValueString("IPD_NOOUT");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NOOUT", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// ��ѯ�õ������SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
			// MR_NO=<MR_NO>
			TParm action = new TParm();
			if (mrNo.length() != 0) {
				action.setData("MR_NO", mrNo);
			}
			if (ipdNo.length() != 0) {
				action.setData("IPD_NO", ipdNo);
			}
			String caseNo = getCaseNo(action);
			result.setData("CASE_NO", caseNo);
			// ����״̬
			result.setData("BILL_STATUS", this.getValueString("BILL_STATUS"));
			// ��Ժ����
			// result.setData("ADM_DATE",this.getValue("ADM_DATEOUT"));
			
			result.setData("DIE_CODE_OUT", this.getValue("DIE_CODE_OUT"));
		}
		return result;
	}

	/**
	 * ��ǰ�������Ƿ���ֵ��ҽʦ
	 * 
	 * @return boolean
	 */
	public boolean isKeepWatch() {
		if (!this.getRunFlg().equals("ODI")) {
			return true;
		}
		// �õ���ǰ�û�ID
		String userId = Operator.getID();
		// �õ���ǰ����
		String stationCode = Operator.getStation();
		TParm action = new TParm(this.getDBTool().select(
				"SELECT DR_CODE FROM ODI_DUTYDRLIST WHERE STATION_CODE='"
						+ stationCode + "' AND DR_CODE='" + userId + "'"));
		// System.out.println("isKeepWatch:"+action.getCount());
		if (action.getInt("ACTION", "COUNT") == 0) {
			// ����ҽʦ
			this.setValue("VC_CODE", Operator.getID());
			// ����ֻ���ǵ�ǰҽʦ�༭
			this.callFunction("UI|VC_CODE|setEnabled", false);
			return false;
		}
		// ����ҽʦ
		this.setValue("VC_CODE", Operator.getID());
		// ����ֻ���ǵ�ǰҽʦ�༭
		this.callFunction("UI|VC_CODE|setEnabled", true);
		return true;
	}

	/**
	 * �õ�CASE_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String getCaseNo(TParm parm) {
		String caseNo = "";
		// ���
		if (!parm.existData("MR_NO") && !parm.existData("IPD_NO")) {
			return caseNo;
		}
		TParm action = OdiMainTool.getInstance().queryPatCaseNo(parm);
		if (action.getErrCode() < 0) {
			this.messageBox(action.getErrText());
			return caseNo;
		}
		// System.out.println("���ظ���:" + action.getInt("ACTION", "COUNT"));
		if (action.getInt("ACTION", "COUNT") > 1) {
			// �������������ѯCASE_NO
			action.setData("SYSTEM_CODE", "ODI");
			Object obj = this.openDialog("%ROOT%\\config\\odi\\PatInfoUI.x",
					action);
			if (obj != null) {
				TParm actionParm = (TParm) obj;
				caseNo = actionParm.getValue("CASE_NO");
			}
		} else {
			caseNo = action.getValue("CASE_NO", 0);
		}
		return caseNo;
	}

	/**
	 * �õ�PANEL
	 * 
	 * @return TPanel
	 */
	public TPanel getTPanel() {
		return (TPanel) this.getComponent(PANEL);
	}

	/**
	 * ���������
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 */
	public void runPane(String tag, String path) {
		// �õ�ϵͳ����Ϊ����ʾ
		if (this.getRunFlg().length() == 0) {
			// ������ϵͳ������
			this.messageBox("E0153");
			return;
		}
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// �رյ�ǰ����ҳ��
		onClosePanel();
		// �õ���ǰѡ����
		int selectRow = (Integer) callFunction("UI|" + TABLE
				+ "|getSelectedRow");
		// �Ƿ���ѡ��
		if (selectRow < 0)
			return;
		// �õ�ѡ��������
		TParm actionParm = this.getSelectRowData(TABLE);
		// סԺҽ��վ����
		String mrNo = actionParm.getValue("MR_NO");
		if (mrNo == null || mrNo.length() == 0)
			return;
		// System.out.println("�õ�ѡ��������"+actionParm);
		// ����CASE_NO
		TParm action = new TParm();
		String caseNo = "";
		int i = 0;
		if (selType == 0) {
			// ��Ժ
			caseNo = this.getValueString("CASE_NO");
			IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
			// ҽ��վ
			action.setData("ODI", "CASE_NO", caseNo);
			action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("ODI", "BED_NO", actionParm.getData("BED_NO"));
			action.setData("ODI", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
			// ����
			action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����1
			action.setData("ODI", "PAT_NAME1", TMessage.getPy(this
					.getValueString("PAT_NAME")));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// �õ���Ӧҩ��
			action.setData("ODI", "ORG_CODE", orgCode);
			action
					.setData("ODI", "STATION_CODE", this
							.getValue("STATION_CODE"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// �Ա�
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// �ʱ�
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// ��ַ
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// ��λ
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// �绰
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// ��ͥ�绰
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// ����֤��
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// �����
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// ����
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// ����Ȩ��ע��
			action.setData("ODI", "SAVE_FLG", true);
			// ������ϱ���
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// �����������
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// ������ҩ
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// ����ע��
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}
			// ICUע��
			action.setData("ODI", "ICU_FLG", IsICU);

			// ��ʿվ
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODE"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// ����1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ����2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ����3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ����
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// סԺ��
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// ������
			action.setData("INW", "MR_NO", this.getValue("MR_NO"));
			// ��Ժʱ��
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����Ȩ��ע��
			action.setData("INW", "SAVE_FLG", true);
			// ICUע��
			action.setData("INW", "ICU_FLG", IsICU);

			// �Ƽ�
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action
					.setData("IBS", "STATION_CODE", this
							.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// ����1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ����2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ����3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// ��ɫ����
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// ��ɫͨ��
			action.setData("IBS", "GREENPATH_VALUE", actionParm
					.getData("GREENPATH_VALUE"));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// ����Ȩ��ע��
			action.setData("IBS", "SAVE_FLG", true);
			// ������ҳ
			action.setData("MRO", "CASE_NO", caseNo);
			// ����Ȩ��ע��
			action.setData("MRO", "SAVE_FLG", true);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NO"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "Y");
			// ����Ȩ��ע��
			action.setData("ADM", "SAVE_FLG", true);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NO"));
			// �ٴ�·��׼��׼��
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", this.getValue("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));

			// Ԥ����
			action.setData("BILPAY", "CASE_NO", caseNo);
		} else {
			// ��Ժ
			caseNo = this.getValueString("CASE_NOOUT");
			action.setData("ODI", "CASE_NO", caseNo);
			action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODEOUT"));
			action.setData("ODI", "BED_NO", actionParm.getData("BED_NO"));
			action.setData("ODI", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ODI", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAMEOUT"));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODEOUT")
					.toString(), this.getValue("DEPT_CODEOUT").toString());
			// �õ���Ӧҩ��
			action.setData("ODI", "ORG_CODE", orgCode);
			action.setData("ODI", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ֹͣ����
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// סԺ����
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// �Ա�
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// �ʱ�
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// ��ַ
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// ��λ
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// �绰
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// ��ͥ�绰
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// ����֤��
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// �����
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// ����
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// ����Ȩ��ע��
			action.setData("ODI", "SAVE_FLG", false);
			// ������ϱ���
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// �����������
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// ������ҩ
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// ����ע��
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}
			// ICUע��
			action.setData("ODI", "ICU_FLG", IsICU);

			// ��ʿվ
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODEOUT"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// ����1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ����2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ����3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ����
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// סԺ��
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// ������
			action.setData("INW", "MR_NO", actionParm.getData("MR_NO"));
			// ��Ժʱ��
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����Ȩ��ע��
			action.setData("INW", "SAVE_FLG", false);
			// �Ƽ�
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODEOUT"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAMEOUT"));
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// ����1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ����2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ����3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// ��ɫ����
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// ����Ȩ��ע��
			action.setData("IBS", "SAVE_FLG", false);
			// ������ҳ
			action.setData("MRO", "CASE_NO", caseNo);
			// ����Ȩ��ע��
			action.setData("MRO", "SAVE_FLG", false);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NOOUT"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "N");
			// ����Ȩ��ע��
			action.setData("ADM", "SAVE_FLG", false);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NOOUT"));
			// �ٴ�·��׼��׼��
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));
			// Ԥ����
			action.setData("BILPAY", "CASE_NO", caseNo);
		}

		// System.out.println("path"+path+"TParm"+action);
		// $$ modified by lx 2012/04/06
		// סԺ��ѯ(PDF����)
		if (path.indexOf("ODIDocQuery") != -1) {
			this.openWindow("%ROOT%\\config\\ODI\\ODIDocQuery.x", action);
		} else {
			getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
			workPanelTag = tag;
			tabPane.setEnabled(false);
			((TTable) this.getComponent(TABLE)).setVisible(false);
		}
		//
	}

	/**
	 * �õ���Ӧҩ��
	 * 
	 * @param stationCode
	 *            String
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getOrgCode(String stationCode, String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT ORG_CODE FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		if (parm.getInt("ACTION", "COUNT") == 0) {
			return "";
		}
		return parm.getValue("ORG_CODE", 0);
	}

	/**
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 * @param parm
	 *            TParm
	 */
	public void runPaneSocketInwCheck(String tag, String path, TParm parm) {
		// this.messageBox(""+parm);
		// �رյ�ǰ����ҳ��
		// onClosePanel();
		// �õ�ѡ��������
		TParm actionParm = this.getSelectRowData(TABLE);
		TParm action = new TParm();
		action.setData("INW", "CASE_NO", parm.getData("CASE_NO"));
		action.setData("INW", "STATION_CODE", parm.getData("STATION_CODE"));
		action.setData("INW", "POPEDEM", this.isInwPopedem());
		// ����1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// ����2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// ����3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// ����
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// ����
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// סԺ��
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// ������
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// ��Ժʱ��
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// ����Ȩ��ע��
		action.setData("INW", "SAVE_FLG", true);
		action.setData("INW", "LEDUI", parm.getData("LEDUI"));
		onClosePanel();
		getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
		((TTable) this.getComponent(TABLE)).setVisible(false);
		workPanelTag = tag;
	}

	/**
	 * �õ�ѡ��������
	 * 
	 * @param tableTag
	 *            String
	 * @return TParm
	 */
	public TParm getSelectRowData(String tableTag) {
		// this.messageBox("===getSelectRowData==");
		int selectRow = (Integer) callFunction("UI|" + tableTag
				+ "|getSelectedRow");
		if (selectRow < 0)
			return new TParm();
		// out("�к�" + selectRow);
		TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
		// out("GRID����" + parm);
		TParm parmRow = parm.getRow(selectRow);
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("INWSHEET")) {
			parmRow.setData("INW_DEPT_CODE", parmRow.getValue("DEPT_CODE"));
			parmRow.setData("INW_STATION_CODE", parmRow
					.getValue("STATION_CODE"));
			parmRow.setData("INW_VC_CODE", parmRow.getValue("VC_CODE"));
		}
		return parmRow;
	}

	public static void main(String args[]) {
		JavaHisDebug.runFrame("odi\\ODIMainUI.x");

	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// ���״̬����Ϣ
		callFunction("UI|setSysStatus", "");
		if (!onClosePanel())
			return false;
		// �ر�Socket
		// if (client == null){
		// if (this.getRunFlg().equals("INWCHECK")) {
		// this.messageBox_(2);
		// ledUi.close();
		// }
		// return true;
		// }
		// client.close();
		if (this.getRunFlg().equals("INWCHECK")) {
			ledUi.close();
		}
		// modify shibl 20120317
		// if (this.getRunFlg().equals("INWEXE")) {
		// ledUi1.close();
		// }
		return true;
	}

	/**
	 * �رչ���ҳ��
	 * 
	 * @return boolean
	 */
	public boolean onClosePanel() {
		if (workPanelTag == null || workPanelTag.length() == 0)
			return true;
		TPanel p = (TPanel) getComponent(workPanelTag);
		if (!p.getControl().onClosing())
			return false;
		// �Ƴ���ǰ��UI
		callFunction("UI|" + PANEL + "|removeItem", workPanelTag);
		// �Ƴ���UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// �Ƴ���UIToolBar
		callFunction("UI|removeChildToolBar");
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
		workPanelTag = "";
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ���Ա༭
		tabPane.setEnabled(true);
		// ��ʾTABLE
		((TTable) this.getComponent(TABLE)).setVisible(true);
		return true;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ�ϵͳ����
	 * 
	 * @return String
	 */
	public String getRunFlg() {
		return runFlg;
	}

	public boolean isInwPopedem() {
		return inwPopedem;
	}

	/**
	 * ����ϵͳ����
	 * 
	 * @param runFlg
	 *            String
	 */
	public void setRunFlg(String runFlg) {
		this.runFlg = runFlg;
	}

	public void setInwPopedem(boolean inwPopedem) {
		this.inwPopedem = inwPopedem;
	}

	/**
	 * ��ʾ��ǰTOOLBAR
	 */
	public void onShowWindowsEvent() {
		if (workPanelTag == null || workPanelTag.length() == 0) {
			// ��ʾUIshowTopMenu
			callFunction("UI|showTopMenu");
			return;
		}
		TPanel p = (TPanel) getComponent(workPanelTag);
		p.getControl().callFunction("onShowWindowsFunction");
	}

	public void passcheck() {
		// ������ҩ
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		// Ԥ���ȼ�
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		// �Ƿ�ǿ��
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		// ������ҩ
		if (passIsReady) {
			if (!initReasonbledMed()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
	}

	/**
	 * ��ʼ��������ҩ
	 * 
	 * @return boolean
	 */
	public boolean initReasonbledMed() {
		try {
			if (PassDriver.init() != 1) {
				return false;
			}
			// ������ҩ��ʼ��
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
				return false;
			}
			// ������ҩ���Ʋ���
			if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) != 1) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			e1.printStackTrace();
			return false;
		} catch (NoClassDefFoundError e2) {
			e2.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �������µ��ӿ�
	 */
	public void onVitalSign() {
		TParm sumParm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0)
			return;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		String caseNo_ = Rowparm.getValue("CASE_NO");
		String mrNo_ = Rowparm.getValue("MR_NO");
		String station_ = Rowparm.getValue("STATION_CODE");
		String ipdNo_ = Rowparm.getValue("IPD_NO");
		String bedNo_ = Rowparm.getValue("BED_NO");
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm, false);
	}

	/**
	 * �������������µ�
	 * 
	 * @param name
	 *            String
	 * @return TPanel
	 */
	public void onNewArrival() {
		TParm sumParm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0)
			return;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		String caseNo_ = Rowparm.getValue("CASE_NO");
		String mrNo_ = Rowparm.getValue("MR_NO");
		String station_ = Rowparm.getValue("STATION_CODE");
		String ipdNo_ = Rowparm.getValue("IPD_NO");
		String bedNo_ = Rowparm.getValue("BED_NO");
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		this.openDialog("%ROOT%\\config\\sum\\SUMNewArrival.x", sumParm, false);
	}

	/**
	 * PDF����
	 */
	public void onSubmitPDF() {
		this.runPane("STATIONMAIN", "ODI\\ODIDocQuery.x");
	}

	/**
	 * ����ס�ɼƼ�
	 */
	public void onFee(){
		TParm ibsParm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0){
			this.messageBox("����ѡ���¼��");
			return;
		}
		//�������;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		String caseNo_ = Rowparm.getValue("CASE_NO");
		String mrNo_ = Rowparm.getValue("MR_NO");
		String station_ = Rowparm.getValue("STATION_CODE");
		String ipdNo_ = Rowparm.getValue("IPD_NO");
		String bedNo_ = Rowparm.getValue("BED_NO");
		String execDeptCode_ = Operator.getCostCenter();
		ibsParm.setData("IBS", "CASE_NO", caseNo_);
		ibsParm.setData("IBS", "IPD_NO", ipdNo_);
		ibsParm.setData("IBS", "MR_NO", mrNo_);
		ibsParm.setData("IBS", "BED_NO", bedNo_);
		ibsParm.setData("IBS", "DEPT_CODE", execDeptCode_);
		ibsParm.setData("IBS", "STATION_CODE", station_);
		ibsParm.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
		ibsParm.setData("IBS", "TYPE", "NSS");
		ibsParm.setData("IBS", "CLNCPATH_CODE", "");

		this.openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm,false);

	}

	// $$==============add by lx 2012/06/24 ����������start=============$$//
	/**
	 * ������������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// ������parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// �������������;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	// $$==============add by lx 2012/06/24 ����������end=============$$//
	/**
     * CDR
     */
    public void onQuerySummaryInfo() {
        TParm parm = new TParm();
        TTable table = (TTable)this.getComponent("TABLE");
        
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("��ѡ��Ҫ�鿴�Ĳ�����Ϣ");
            return;
        }    
        Container container = (Container) callFunction("UI|getThis");
        while (!(container instanceof TTabbedPane)) {
            container = container.getParent();
        }
        TTabbedPane tabbedPane = (TTabbedPane) container;

        parm.setData("MR_NO", table.getParmValue().getRow(selRow).getValue("MR_NO"));
        // ���ۺϲ�ѯ����
        tabbedPane.openPanel("CDR_SUMMARY_UI",
                "%ROOT%\\config\\emr\\EMRCdrSummaryInfo.x", parm);
        TComponent component = (TComponent) callFunction(
                "UI|SYSTEM_TAB|findObject", "CDR_SUMMARY_UI");
        if (component != null) {
            tabbedPane.setSelectedComponent((Component) component);
            return;
        }
    }
    /**
     * �������
     */
    public void onCxShow(){  
    	TTable table =(TTable)this.getComponent("TABLE");
     	TParm parm = table.getParmValue();
    	String mrNo = parm.getValue("MR_NO", table.getSelectedRow());
    	String caseNo = parm.getValue("CASE_NO", table.getSelectedRow());
        TParm result = queryPassword();
        String user_password = result.getValue("USER_PASSWORD",0);
        String url = "http://"+getWebServicesIp()+"?userId="+Operator.getID()+"&password="+user_password+"&mrNo="+mrNo+"&caseNo="+caseNo;
        try {
            Runtime.getRuntime().exec(String.valueOf(String.valueOf((new
                    StringBuffer("cmd.exe /c start iexplore \"")).append(
                            url).append("\""))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private TParm queryPassword(){
        String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"' AND REGION_CODE = '"+Operator.getRegion()+"'";
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * ��ȡ�����ļ��еĵ��Ӳ���������IP
     * @return
     */
    public static String getWebServicesIp() {
        TConfig config = getProp();
        String url = config.getString("", "EMRIP");
        return url;
    }
    
    /**
     * ��ȡ�����ļ�
     * @author shendr
     */
    public static TConfig getProp() {
        TConfig config=null;
        try{
         config = TConfig
                .getConfig("WEB-INF\\config\\system\\TConfig.x");
        }catch(Exception e){
            e.printStackTrace();
        }
        return config;
    }
	

}