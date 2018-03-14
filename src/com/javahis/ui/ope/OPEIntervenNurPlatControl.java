package com.javahis.ui.ope;

import java.awt.Component;
import java.awt.Container;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMXMLTool;
import jdo.ope.OPEDeptOpTool;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWindow;

public class OPEIntervenNurPlatControl extends TControl {
	TTable table;
	String caseNo;
	String mrNo;
	String opBookSeq;
	TParm fileParm = new TParm();

	@Override
	public void onInit() {
		super.onInit();
		opBookSeq = ((TParm) this.getParameter()).getValue("OPBOOK_SEQ");
		caseNo = ((TParm) this.getParameter()).getData("ODI", "CASE_NO")
				.toString();
		// System.out.println("caseNo:::"+caseNo);
		mrNo = ((TParm) this.getParameter()).getData("ODI", "MR_NO").toString();
		table = (TTable) this.getComponent("TABLE");
		this.setValue("PAIN_ASSESSMENT", 0);

		// add by yangjj 20161228 #4645
		this.setValue("OXYGEN_SATURATION", "99");

		onIniteTime();
		onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm result = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT TIME,HEART_RATE||'��/����' HEART_RATE,"
										+ " BREATH||'��/����' BREATH,PRESSURE||'mmHg' PRESSURE,OXYGEN_SATURATION||'%' OXYGEN_SATURATION,PAIN_ASSESSMENT,ILLNESS_RECORD,ORDER_DESC"
										+ " ,SEQ_NO FROM OPE_INTERVENNURPLAT WHERE CASE_NO = '"
										+ caseNo
										+ "' AND ORDER_DESC IS NULL AND OPBOOK_SEQ = '"
										+ this.opBookSeq
										+ "' ORDER BY TIME DESC "));
		table.setParmValue(result);
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (!onCheck()) {
			this.messageBox("�ò���û�а�ȫ�˲鵥�����ɲ���");
			return;
		}
		if (!checkData()) {// У����ʹ����
			return;
		}
		TParm action = new TParm();
		if (this.getValue("TIME") == null || this.getValue("TIME").equals("")) {
			this.messageBox("ʱ�䲻��Ϊ��");
			return;
		}
		int seq = OPEDeptOpTool.getInstance().getMaxSeqNo(caseNo);
		if (this.getValue("ILLNESS_RECORD") != null
				&& this.getValue("ILLNESS_RECORD").toString().length() > 500) {
			this.messageBox("�����¼¼�� ��Χ���ܴ���500");
			this.grabFocus("ILLNESS_RECORD");
			return;
		}

		// add by yangjj 20161228 #4645 begin
		// ���ʣ�����200 ����0
		String heartRate = this.getValueString("HEART_RATE");
		int hr = 0;
		try {
			hr = Integer.parseInt(heartRate);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ�����ʣ�");
			return;
		}

		if (hr > 200) {
			this.messageBox("��������Ϊ200");
			return;
		}

		if (hr < 0) {
			this.messageBox("��������Ϊ0");
			return;
		}

		// ����������50 ����0
		String breath = this.getValueString("BREATH");
		int b = 0;
		try {
			b = Integer.parseInt(breath);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ�ĺ�����");
			return;
		}

		if (b > 50) {
			this.messageBox("��������Ϊ50");
			return;
		}

		if (b < 0) {
			this.messageBox("��������Ϊ0");
			return;
		}

		// ����ѹ ����200 ����0
		String lowPressure = this.getValueString("LOW_PRESSURE");
		int lp = 0;
		try {
			lp = Integer.parseInt(lowPressure);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ������ѹ");
			return;
		}

		if (lp > 200) {
			this.messageBox("����ѹ����Ϊ200");
			return;
		}

		if (lp < 0) {
			this.messageBox("����ѹ����Ϊ0");
			return;
		}

		// ����ѹ ����300 ����0
		String highPressure = this.getValueString("HIGH_PRESSURE");
		int hp = 0;
		try {
			hp = Integer.parseInt(highPressure);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ������ѹ");
			return;
		}

		if (hp > 300) {
			this.messageBox("����ѹ����Ϊ300");
			return;
		}

		if (hp < 0) {
			this.messageBox("����ѹ����Ϊ0");
			return;
		}

		// Ѫ�����Ͷȣ�����100 ����0��Ĭ��ֵ99
		String oxygen = this.getValueString("OXYGEN_SATURATION");
		int os = 0;
		try {
			os = Integer.parseInt(oxygen);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ��Ѫ�����Ͷ�");
			return;
		}

		if (os > 100) {
			this.messageBox("Ѫ�����Ͷ�����Ϊ100");
			return;
		}

		if (os < 0) {
			this.messageBox("Ѫ�����Ͷ�����Ϊ0");
			return;
		}

		// ��ʹ����������10 ����0
		String pain = this.getValueString("PAIN_ASSESSMENT");
		int pa = 0;
		try {
			pa = Integer.parseInt(pain);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox("��������ȷ����ʹ����");
			return;
		}

		if (pa > 10) {
			this.messageBox("��ʹ��������Ϊ10");
			return;
		}

		if (pa < 0) {
			this.messageBox("��ʹ��������Ϊ0");
			return;
		}
		// end

		if (table.getSelectedRow() >= 0) {
			action.setData("SAVE_FLG", 0, "N");
			action.setData("SEQ_NO", 0, table.getParmValue().getValue("SEQ_NO",
					table.getSelectedRow()));
		} else {
			action.setData("SAVE_FLG", 0, "Y");
			action.setData("SEQ_NO", 0, ++seq);
		}
		TParm tagParm = this
				.getParmForTag("TIME;HEART_RATE;BREATH;LOW_PRESSURE;HIGH_PRESSURE;OXYGEN_SATURATION;PAIN_ASSESSMENT;ILLNESS_RECORD");
		action.setData("TIME", 0, tagParm.getValue("TIME").toString()
				.substring(0, 19).replaceAll("-", "/"));
		action.setData("CASE_NO", 0, caseNo);
		action.setData("HEART_RATE", 0, tagParm.getValue("HEART_RATE"));
		action.setData("BREATH", 0, tagParm.getValue("BREATH"));
		action.setData("PRESSURE", 0, tagParm.getValue("LOW_PRESSURE") + "/"
				+ tagParm.getValue("HIGH_PRESSURE"));
		action.setData("OXYGEN_SATURATION", 0, tagParm
				.getValue("OXYGEN_SATURATION"));
		action.setData("PAIN_ASSESSMENT", 0, tagParm
				.getValue("PAIN_ASSESSMENT"));
		action.setData("ILLNESS_RECORD", 0, tagParm.getValue("ILLNESS_RECORD"));
		action.setData("OP_DEPT_CODE", 0, ((TParm) this.getParameter())
				.getData("ODI", "DEPT_CODE"));

		action.setData("STATION_CODE", 0, Operator.getStation());
		action.setData("OPT_USER", 0, Operator.getID());
		action.setData("OPT_TERM", 0, Operator.getIP());
		action.setData("OPBOOK_SEQ", 0, this.opBookSeq);
		action.setData("ORDER_DESC", 0, "");
		action.setData("ORDER_CODE", 0, "");
		action.setData("MEDI_QTY", 0, 0);
		action.setData("ROUTE_CODE", 0, "");
		action.setData("MEDI_UNIT", 0, "");
		action.setData("ORDER_NO", 0, "");
		action.setData("ORDER_SEQ", 0, 0);
		action.setCount(1);
		TParm result = OPEDeptOpTool.getInstance().insertInterData(action);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			return;
		}
		this.messageBox("����ɹ�");
		this
				.clearValue("TIME;HEART_RATE;BREATH;LOW_PRESSURE;HIGH_PRESSURE;OXYGEN_SATURATION;PAIN_ASSESSMENT;ILLNESS_RECORD");
		this.onQuery();

		// add by yangjj 20161228 #4645
		this.setValue("OXYGEN_SATURATION", "99");

		onIniteTime();
	}

	/**
	 * У���Ƿ���ڰ�ȫ�˲鵥
	 * 
	 * @return
	 */
	public boolean onCheck() {
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE " + " CASE_NO = '"
				+ caseNo + "' AND SUBCLASS_CODE= 'EMR0604022' "
				+ " AND OPBOOK_SEQ = '" + opBookSeq
				+ "' AND FILE_SEQ = (SELECT MAX(FILE_SEQ) FROM EMR_FILE_INDEX "
				+ " WHERE CASE_NO = '" + caseNo
				+ "' AND SUBCLASS_CODE= 'EMR0604022' AND OPBOOK_SEQ = '"
				+ opBookSeq + "' )   ";
		// System.out.println(""+sql);
		fileParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (fileParm.getCount() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {

		int row = table.getSelectedRow();
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}

		TParm action = new TParm();
		TParm tableParm = table.getParmValue().getRow(row);
		action.setData("TIME", getValue("TIME").toString().substring(0, 19)
				.replaceAll("-", "/"));
		action.setData("CASE_NO", caseNo);
		action.setData("SEQ_NO", tableParm.getValue("SEQ_NO"));
		TParm result = OPEDeptOpTool.getInstance().deleteInterData(action);
		if (result.getErrCode() < 0) {
			this.messageBox("ɾ��ʧ��");
			return;
		}
		this.messageBox("ɾ���ɹ�");
		this
				.clearValue("TIME;HEART_RATE;BREATH;LOW_PRESSURE;HIGH_PRESSURE;OXYGEN_SATURATION;PAIN_ASSESSMENT;ILLNESS_RECORD");
		this.onQuery();
		onIniteTime();
	}

	/**
	 * ����ҽ��¼��
	 */
	public void onOpeOrder() {

		// TMenuBar menu = this.getComponent("");

		TParm parm = (TParm) this.getParameter();
		// this.openDialog("%ROOT%\\config\\ope\\OPEDRStation.x", parm,true);
		String panelUI = "";
		String configName = "";

		panelUI = "DR_STATION";
		configName = "%ROOT%\\config\\ope\\OPEDRStation.x";

		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		Component[] cs = container.getComponents();
		for (Component component : cs) {
			System.out.println(component.getName());
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

		// ��ǰ�ȹرո�ҳ��
		tabbedPane.closePanel(panelUI);
		// �򿪽���
		tabbedPane.openPanel(panelUI, configName, parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", panelUI);
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return;
		}

	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {

		TParm data = new TParm();

		String sql = " SELECT * " + " FROM (  SELECT DISTINCT A.TIME, "
				+ " A.HEART_RATE, " + " A.BREATH, " + " A.PRESSURE, "
				+ " A.OXYGEN_SATURATION, " + " A.PAIN_ASSESSMENT, "
				+ " A.ILLNESS_RECORD, " + " A.ORDER_DESC, "
				+ " '' ROUTE_CHN_DESC, " + " A.MEDI_QTY, "
				+ " '' UNIT_CHN_DESC " + " FROM OPE_INTERVENNURPLAT A "
				+ " WHERE     A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ " AND A.OPBOOK_SEQ = '"
				+ opBookSeq
				+ "' "
				+ " AND A.ORDER_DESC IS NULL "
				+ " UNION  "
				+ " SELECT DISTINCT A.TIME, "
				+ " A.HEART_RATE, "
				+ " A.BREATH, "
				+ " A.PRESSURE, "
				+ " A.OXYGEN_SATURATION, "
				+ " A.PAIN_ASSESSMENT, "
				+ " A.ILLNESS_RECORD, "
				+ " A.ORDER_DESC, "
				+ " C.ROUTE_CHN_DESC, "
				+ " B.MEDI_QTY, "
				+ " D.UNIT_CHN_DESC "
				+ " FROM OPE_INTERVENNURPLAT A, ODI_ORDER B, SYS_PHAROUTE C ,SYS_UNIT D "
				+ " WHERE     A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ " AND A.OPBOOK_SEQ = '"
				+ opBookSeq
				+ "' "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND A.ORDER_NO = B.ORDER_NO "
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
				+ " AND B.ROUTE_CODE = C.ROUTE_CODE(+) "
				+ " AND B.MEDI_UNIT = D.UNIT_CODE(+)  "
				+ " AND A.ORDER_DESC IS NOT NULL " + " ) ORDER BY TIME DESC";
		// System.out.println(""+sql);
		TParm printParm = new TParm(TJDODBTool.getInstance().select(sql));

		for (int i = 0; i < printParm.getCount(); i++) {
			data.addData("TIME", printParm.getValue("TIME", i).toString()
					.substring(5, 16).replaceAll("-", "/"));
			data.addData("HEART_RATE", printParm.getValue("HEART_RATE", i)
					.equals("") ? ""
					: (printParm.getValue("HEART_RATE", i) + "��/����"));
			data.addData("BREATH",
					printParm.getValue("BREATH", i).equals("") ? ""
							: (printParm.getValue("BREATH", i) + "��/����"));
			data.addData("PRESSURE", printParm.getValue("PRESSURE", i).equals(
					"") ? "" : (printParm.getValue("PRESSURE", i) + "mmHg"));
			data.addData("OXYGEN_SATURATION", printParm.getValue(
					"OXYGEN_SATURATION", i).equals("") ? "" : (printParm
					.getValue("OXYGEN_SATURATION", i) + "%"));
			data.addData("PAIN_ASSESSMENT", printParm.getValue(
					"PAIN_ASSESSMENT", i));
			data.addData("ORDER_DESC", printParm.getValue("ORDER_DESC", i));
			data.addData("ROUTE_CODE", printParm.getValue("ROUTE_CHN_DESC", i));
			data
					.addData("MEDI_CODE",
							printParm.getDouble("MEDI_QTY", i) > 0 ? printParm
									.getDouble("MEDI_QTY", i)
									+ " "
									+ printParm.getValue("UNIT_CHN_DESC", i)
									: "");
			data.addData("ILLNESS_RECORD", printParm.getValue("ILLNESS_RECORD",
					i));
		}
		//

		data.setCount(data.getCount("TIME"));
		data.addData("SYSTEM", "COLUMNS", "TIME");
		data.addData("SYSTEM", "COLUMNS", "HEART_RATE");
		data.addData("SYSTEM", "COLUMNS", "BREATH");
		data.addData("SYSTEM", "COLUMNS", "PRESSURE");
		data.addData("SYSTEM", "COLUMNS", "OXYGEN_SATURATION");
		data.addData("SYSTEM", "COLUMNS", "PAIN_ASSESSMENT");
		data.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		data.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		data.addData("SYSTEM", "COLUMNS", "MEDI_CODE");
		data.addData("SYSTEM", "COLUMNS", "ILLNESS_RECORD");

		TParm result = new TParm();
		result.setData("TABLE", data.getData());
		result.setData("MR_NO", "TEXT", ((TParm) this.getParameter()).getData(
				"ODI", "MR_NO"));
		result.setData("OP_DATE", "TEXT", ((TParm) this.getParameter())
				.getValue("OP_DATE").toString().substring(0, 10));
		result.setData("PAT_NAME", "TEXT", ((TParm) this.getParameter())
				.getData("ODI", "PAT_NAME"));
		result.setData("OP_ROOM", "TEXT", ((TParm) this.getParameter())
				.getValue("OP_ROOM"));
		this
				.openPrintWindow(
						"%ROOT%\\config\\prt\\OPE\\OPEIntervenNurPlatPrint.jhw",
						result);

	}

	/**
	 * ���
	 */
	public void onClear() {
		this
				.clearValue("TIME;HEART_RATE;BREATH;LOW_PRESSURE;HIGH_PRESSURE;OXYGEN_SATURATION;PAIN_ASSESSMENT;ILLNESS_RECORD");
		table.removeRowAll();
		this.onInit();
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		TParm rowParm = table.getParmValue().getRow(table.getSelectedRow());
		this
				.setValueForParm(
						"TIME;HEART_RATE;BREATH;OXYGEN_SATURATION;PAIN_ASSESSMENT;ILLNESS_RECORD",
						rowParm);
		try {
			String str[] = rowParm.getValue("PRESSURE").split("/");
			this.setValue("LOW_PRESSURE", str[0]);
			this.setValue("HIGH_PRESSURE", str[1].substring(0, str[1]
					.indexOf("m")));
			this.setValue("HEART_RATE", rowParm.getValue("HEART_RATE")
					.substring(0, rowParm.getValue("HEART_RATE").indexOf("��")));
			this.setValue("BREATH", rowParm.getValue("BREATH").substring(0,
					rowParm.getValue("BREATH").indexOf("��")));
			this.setValue("OXYGEN_SATURATION", rowParm.getValue(
					"OXYGEN_SATURATION").substring(0,
					rowParm.getValue("OXYGEN_SATURATION").indexOf("%")));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * ����ǰTOOLBAR
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * ˢ��ʱ��ؼ�
	 */
	public void onIniteTime() {
		this.setValue("TIME", TJDODBTool.getInstance().getDBTime().toString()
				.substring(0, 19).replaceAll("-", "/"));
	}

	/**
	 * ����Ƭ��
	 */
	public void inData() {

		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
		this.grabFocus("ILLNESS_RECORD");
	}

	/**
	 * Ƭ���¼����ش�ֵ
	 * 
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		if (this.getValue("ILLNESS_RECORD") != null
				&& !this.getValue("ILLNESS_RECORD").equals("")) {
			this.setValue("ILLNESS_RECORD", this.getValue("ILLNESS_RECORD")
					+ " " + value);
		} else {
			this.setValue("ILLNESS_RECORD", value);
		}

	}

	/**
	 * У��ؼ�
	 */
	public boolean checkData() {
		int value = this.getValueInt("PAIN_ASSESSMENT");
		if (value < 0 || value > 10) {
			this.messageBox("������0~10������");
			this.setValue("PAIN_ASSESSMENT", 0);
			this.grabFocus("PAIN_ASSESSMENT");
			return false;
		}
		this.grabFocus("ILLNESS_RECORD");
		return true;
	}
	/**
	 * ���밲ȫ�˲鵥
	 * add lij 20170621
	 */
	public void onPrintBAE(){
		TParm parm = new TParm();
		String opebookSeq = ((TParm) this.getParameter()).getParm("INITUI").getValue("OPBOOK_SEQ");
		parm.setData("OPBOOK_SEQ", opebookSeq);
		//add by wangjc 20171206 û���������Ӳ�����˲� start
		String checkSql = "SELECT TO_USER,FROM_USER FROM INW_TRANSFERSHEET WHERE TRANSFER_CLASS IN ('ET','WT','WO','EW') AND OPBOOK_SEQ = '"+opebookSeq+"' ORDER BY TRANSFER_CODE";
		TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
		if(checkParm.getCount("TO_USER") <= 0){
			this.messageBox("û�����ɽ��ӵ���������˲�");
			return;
		}else if(StringUtils.isEmpty(checkParm.getValue("FROM_USER", 0)) || StringUtils.isEmpty(checkParm.getValue("TO_USER", 0))){
			this.messageBox("û�н��ӣ�������˲�");
			return;
		}
		//add by wangjc 20171206 û���������Ӳ�����˲� end
		//add by wangjc 20171206 ��������� start
		String iproomsql = "SELECT ROOM_NO, OPBOOK_SEQ FROM OPE_IPROOM WHERE IP = '"+Operator.getIP()+"'";
		TParm iproomParm = new TParm(TJDODBTool.getInstance().select(iproomsql));
		if(iproomParm.getCount() > 0){
			String opbookSql = "SELECT ROOM_NO,TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '"+opebookSeq+"'";
			TParm opbookParm = new TParm(TJDODBTool.getInstance().select(opbookSql));
			if(StringUtils.isNotEmpty(iproomParm.getValue("OPBOOK_SEQ", 0))){
				if(!iproomParm.getValue("OPBOOK_SEQ", 0).equals(opebookSeq)){
					this.messageBox("�������Ѱ��������������Ƚ��");
					return;
				}
			}
			if("1".equals(opbookParm.getValue("TYPE_CODE", 0))){
				if(!opbookParm.getValue("ROOM_NO", 0).equals(iproomParm.getValue("ROOM_NO", 0))){
					this.messageBox("������������԰����䲻����");
					return;
				}
			}
		}else{
			this.messageBox("�õ���δ��������а�");
			return;
		}
		parm.setData("OPE_SAVE_CHECK", "Y");
		//add by wangjc 20171206 ��������� end
//		messageBox_(parm);
		this.openWindow("%ROOT%\\config\\pda\\PDAOpeInterSaveUI.x", parm);
	}
	/**
	 * ��ȫ�˲鵥
	 */
//	public void onPrintBAE() {
//		// TParm parmRow = ((TParm) this.getParameter()).getParm("PARM_ROW");
//		TParm parm = new TParm();
//		Timestamp ts = SystemTool.getInstance().getDate();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("MR_NO", mrNo);
//		parm.setData("PAT_NAME", ((TParm) this.getParameter()).getData("ODI",
//				"PAT_NAME"));
//		parm.setData("ADM_DATE", ts);
//		parm.setData("SYSTEM_TYPE", "ODI");
//		parm.setData("ADM_TYPE", "I");
//		parm.setData("IPD_NO", ((TParm) this.getParameter()).getData("ODI",
//				"IPD_NO").toString());
//		// parm.setData("RULETYPE", "1");
//		TParm emrFileData = new TParm();
//
//		if (this.onCheck()) {
//			emrFileData = fileParm.getRow(0);
//			emrFileData.setData("TEMPLET_PATH", fileParm.getValue("FILE_PATH",
//					0)
//					+ "\\" + fileParm.getValue("FILE_NAME", 0) + ".jhw");
//			emrFileData.setData("EMT_FILENAME", fileParm.getValue("FILE_NAME",
//					0));
//			emrFileData.setData("FLG", true);
//		} else {
//			emrFileData.setData("TEMPLET_PATH", "JHW\\�����¼\\��ȫ�˲�");
//			emrFileData.setData("EMT_FILENAME", "���밲ȫ�˲鵥");
//			emrFileData.setData("SUBCLASS_CODE", "EMR0604022");
//			emrFileData.setData("CLASS_CODE", "EMR0604");
//			emrFileData.setData("FLG", false);
//			parm.addListener("EMR_LISTENER", this, "baeListener");
//			parm.setData("OPE_MSG_SEND_FLG", "Y");
//			parm.addListener("OPE_LISTENER", this, "opeListener");
//		}
//		parm.setData("EMR_FILE_DATA", emrFileData);
//		parm.setData("OPBOOK_SEQ", ((TParm) this.getParameter())
//				.getValue("OPBOOK_SEQ"));
//		// add by wangb 2017/1/6 ���밲ȫ�˲鵥������д�ر�ʱѯ���Ƿ񱣴�
//		parm.setData("EMR_SAVE_MSG_FLG", "Y");
//		// add by wangb 2017/1/9 ���밲ȫ�˲鵥�ر�ʱ�Զ�������հ�ť
//		parm.addListener("CLEAR_LISTENER", this, "clearListener");
//		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
//	}

	/**
	 * �¼�
	 * (����)
	 * @param parm
	 *            TParm
	 */
	public void baeListener(TParm parm) {
		String sql = "SELECT * FROM OPE_CHECK WHERE OPBOOK_SEQ = '"
				+ ((TParm) this.getParameter()).getParm("INITUI").getValue(
						"OPBOOK_SEQ") + "'";
		TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
		// messageBox("parm+++++"+dataParm);
		parm.runListener("setMicroData", "ROOM_NO", ((TParm) this
				.getParameter()).getValue("OP_ROOM"));// ����
		// String typeCode = dataParm.getValue("TYPE_CODE", 0);
		// if(typeCode.equals("1")){
		// parm.runListener("setMicroData","TYPE_CODE", "�ɶ���");//����¼��;��
		// }else if(typeCode.equals("2")){
		// parm.runListener("setMicroData","TYPE_CODE", "�Ӷ���");//����¼��;��
		// }else{
		// parm.runListener("setMicroData","TYPE_CODE", "");//����¼��;��
		// }
		// liuyalin add 20170317
		String[] typeCode = dataParm.getValue("TYPE_CODE", 0).split(",");
		for (int i = 0; i < typeCode.length; i++) {
			// liuyalin modify 20170410
			String typeCode1 = typeCode[i];
			if (!"".equals(typeCode1)) {
				String sqlG = "SELECT GDVAS_CODE,GDVAS_DESC FROM SYS_INPUTWAY WHERE GDVAS_CODE = '"
						+ typeCode1 + "' ";
				TParm gdvasParm = new TParm(TJDODBTool.getInstance().select(
						sqlG));
				if ("14".equals(typeCode1)) {
					String sql14 = "SELECT GDVAS_REMARKS FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '"
							+ ((TParm) this.getParameter()).getParm("INITUI")
									.getValue("OPBOOK_SEQ") + "'";
					TParm tparm = new TParm(TJDODBTool.getInstance().select(
							sql14));
					parm.addData("TYPE_CODE", "����("
							+ tparm.getValue("GDVAS_REMARKS").replace("[", "")
									.replace("]", "").replace(" ", "") + ")");
				} else {
					parm
							.addData("TYPE_CODE", gdvasParm.getValue(
									"GDVAS_DESC").replace("[", "").replace("]",
									"").replace(" ", ""));
				}
			} else {
				parm.addData("TYPE_CODE", "");
			}
		}
		parm.runListener("setMicroData", "TYPE_CODE", parm
				.getValue("TYPE_CODE").replace("[", "").replace("]", "")
				.replace(" ", ""));// ����¼��;����ʾ
		// if ("1".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�ɶ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�ɶ���");//
		// // ����¼��;��
		// } else if ("2".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�㶯��");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�㶯��");//
		// // ����¼��;��
		// } else if ("3".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�����¾���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�����¾���");//
		// // ����¼��;��
		// } else if ("4".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�ɾ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�ɾ���");//
		// // ����¼��;��
		// } else if ("5".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "���ھ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "���ھ���");//
		// // ����¼��;��
		// } else if ("6".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�Ŷ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�Ŷ���");//
		// // ����¼��;��
		// } else if ("7".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "ͷ����");
		// // parm.runListener("setMicroData", "TYPE_CODE", "ͷ����");//
		// // ����¼��;��
		// } else if ("8".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "���㶯��");
		// // parm.runListener("setMicroData", "TYPE_CODE", "���㶯��");//
		// // ����¼��;��
		// } else if ("9".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "���㶯��");
		// // parm.runListener("setMicroData", "TYPE_CODE", "���㶯��");//
		// // ����¼��;��
		// } else if ("10".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "��ɶ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "��ɶ���");//
		// // ����¼��;��
		// } else if ("11".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�ҹɶ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�ҹɶ���");//
		// // ����¼��;��
		// } else if ("12".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "��ɾ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "��ɾ���");//
		// // ����¼��;��
		// } else if ("13".equals(typeCode1)) {
		// parm.addData("TYPE_CODE", "�ҹɾ���");
		// // parm.runListener("setMicroData", "TYPE_CODE", "�ҹɾ���");//
		// // ����¼��;��
		// } else if ("14".equals(typeCode1)) {
		// String sql14 =
		// "SELECT GDVAS_REMARKS FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '"
		// + ((TParm) this.getParameter()).getParm("INITUI")
		// .getValue("OPBOOK_SEQ") + "'";
		// TParm tparm = new TParm(TJDODBTool.getInstance().select(sql14));
		// if (tparm.getValue("GDVAS_REMARKS", 0).length() <= 0) {
		// parm.addData("TYPE_CODE", "����");
		// } else {
		// parm.addData(
		// "TYPE_CODE",
		// "����("
		// + tparm.getValue("GDVAS_REMARKS")
		// .replace("[", "").replace("]", "")
		// .replace(" ", "") + ")");
		// }
		// parm.runListener("setMicroData", "TYPE_CODE", "����");//
		// ����¼��;��
		// } else {
		// parm.addData("TYPE_CODE", "");
		// // parm.runListener("setMicroData", "TYPE_CODE", "");//
		// // ����¼��;��
		// }
		// }
		// parm.runListener("setMicroData", "TYPE_CODE", parm
		// .getValue("TYPE_CODE").replace("[", "").replace("]", "")
		// .replace(" ", ""));// ����¼��;��
		String allergicFlg = dataParm.getValue("ALLERGIC_FLG", 0);
		if (allergicFlg.equals("Y")) {
			parm.runListener("setMicroData", "ALLERGIC_FLG1", "��");// ������� ��
			parm.runListener("setMicroData", "ALLERGIC_FLG2", "��");// ������� ��

			// add by wangb 2017/1/10 ���ڹ������ʱ����������Ĺ�������
			String allergy = this.getAllergy();
			if (allergy.length() > 0) {
				parm.runListener("setMicroData", "ALLERGY", allergy);// ��������
			}
		} else {
			parm.runListener("setMicroData", "ALLERGIC_FLG1", "��");// ������� ��
			parm.runListener("setMicroData", "ALLERGIC_FLG2", "��");// ������� ��
		}
		String readyFlg = dataParm.getValue("READY_FLG", 0);
		if (readyFlg.equals("Y")) {
			parm.runListener("setMicroData", "READY_FLG", "��");// ׼����ȫ
		} else {
			parm.runListener("setMicroData", "READY_FLG", "��");// ׼����ȫ
		}
		String validDateFlg = dataParm.getValue("VALID_DATE_FLG", 0);
		if (validDateFlg.equals("Y")) {
			parm.runListener("setMicroData", "VALID_DATE_FLG", "��");// ���Ч��
		} else {
			parm.runListener("setMicroData", "VALID_DATE_FLG", "��");// ���Ч��
		}
		String specificationFlg = dataParm.getValue("SPECIFICATION_FLG", 0);
		if (specificationFlg.equals("Y")) {
			parm.runListener("setMicroData", "SPECIFICATION_FLG", "��");// ȷ��ֲ�������ͺ�
		} else {
			parm.runListener("setMicroData", "SPECIFICATION_FLG", "��");// ȷ��ֲ�������ͺ�
		}
		String userSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
				+ dataParm.getValue("CHECK_DR_CODE", 0) + "'";
		TParm userParm = new TParm(TJDODBTool.getInstance().select(userSql));
		parm.runListener("setMicroData", "CHECK_DR_CODE", userParm.getValue(
				"USER_NAME", 0));// Ѳ�ػ�ʿ
		userSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
				+ dataParm.getValue("CHECK_NS_CODE", 0) + "'";
		userParm = new TParm(TJDODBTool.getInstance().select(userSql));
		parm.runListener("setMicroData", "CHECK_NS_CODE", userParm.getValue(
				"USER_NAME", 0));// Ѳ�ػ�ʿ
		if (!this.onCheck()) {// �����ڰ�ȫ�˲鵥ʱ���Ŵ��뵱ǰʱ����Ϊ ����ʱ��
			parm.runListener("setMicroData", "saveDate", TJDODBTool
					.getInstance().getDBTime().toString().substring(0, 19)
					.replaceAll("-", "/"));
		}

	}

	/**
	 * 20170328 zhanglei Ϊ���ӵ���ѯ�ռ�����״̬ ADM_INP��DAY_OPE_FLG��Y��ʾ�ռ�����
	 */

	private String getDayOpeFlg(String MrNo, String CaseNo) {

		String sqlRJ = " SELECT DAY_OPE_FLG FROM ADM_INP WHERE MR_NO = '"
				+ MrNo + "'  AND  " + "CASE_NO = '" + CaseNo + "'";
		// System.out.println("sql:::::::"+sqlRJ);
		TParm parmRJ = new TParm(TJDODBTool.getInstance().select(sqlRJ));
		// System.out.println("SQL::"+sqlRJ);

		String DayOpeFlg = parmRJ.getValue("DAY_OPE_FLG");

		return DayOpeFlg;
	}

	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate() {
		TParm action = new TParm();
		/** modified by WangQing 20170411 -start */
		action.setData("MR_NO", mrNo);// ������
		action.setData("CASE_NO", caseNo);// �����
		action.setData("PAT_NAME", ((TParm) this.getParameter()).getData("ODI",
				"PAT_NAME"));// ����

		// 20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
		String getDayOpeFlg = getDayOpeFlg(action.getValue("MR_NO"), action
				.getValue("CASE_NO"));
		action.setData("DAY_OPE_FLG", "[Y]".equals(getDayOpeFlg) ? "�ռ�����" : "");// �ռ��������
		TParm actionParm = new TParm();// ����ģ��
		if (((TParm) this.getParameter()).getData("TYPE_CODE").equals("1")) {// �������
			actionParm = this.getEmrFilePath("EMR0603055");
			action.setData("TRANSFER_CLASS", "OC/OW"); // ��������
			action.setData("OP_CODE", ((TParm) this.getParameter()).getData(
					"ODI", "OP_CODE")); // ��ʽ
			// fux modify 20170515 ���뻤��ƽ̨�������ɽ��뵽�������ӵ���ת������Ϊ�����ң�ӦΪ�ж� id:5292
			action.setData("FROM_DEPT", "030503"); // ת������(������)
		} else {// ��������
			actionParm = this.getEmrFilePath("EMR0603033");
			action.setData("TRANSFER_CLASS", "TC/TW"); // ��������
			action.setData("OP_CODE", ((TParm) this.getParameter())
					.getData("OP_CODE"));// ��ʽ
			// fux modify 20170515 ���뻤��ƽ̨�������ɽ��뵽�������ӵ���ת������Ϊ�����ң�ӦΪ�ж� id:5292
			action.setData("FROM_DEPT", "0306"); // ת������(������)
		}
		// action.setData("FROM_DEPT", "030503"); // ת������(������)
		action.setData("OPBOOK_SEQ", opBookSeq);// ��������
		action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH", 0));// ���ӵ�·��
		action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME", 0));// ���ӵ�����
		action.setData("FLG", false);// ��ģ��
		this.openWindow("%ROOT%\\config\\ope\\OPETransfertype.x", action);
		/** modified by WangQing 20170411 -end */
	}

	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate_() {
		TParm action = new TParm();
		action.setData("MR_NO", mrNo);// ������
		action.setData("CASE_NO", caseNo);// �����
		action.setData("PAT_NAME", ((TParm) this.getParameter()).getData("ODI",
				"PAT_NAME"));// ����

		// 20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
		String getDayOpeFlg = getDayOpeFlg(action.getValue("MR_NO"), action
				.getValue("CASE_NO"));

		action.setData("DAY_OPE_FLG", "[Y]".equals(getDayOpeFlg) ? "�ռ�����" : "");// �ռ��������

		// ��ѯģ����Ϣ
		TParm actionParm = new TParm();
		// ����������
		if (((TParm) this.getParameter()).getData("TYPE_CODE").equals("1")) {
			action.setData("FROM_DEPT", "030503"); // ת������(������)
			action.setData("TO_DEPT", "0303"); // ת�����(ICU)
			action.setData("TRANSFER_CLASS", "OI"); // ��������(������-ICU OI)
			action.setData("OP_CODE", ((TParm) this.getParameter()).getData(
					"ODI", "OP_CODE")); // ��ʽ
			// ��ѯģ����Ϣ
			actionParm = this.getEmrFilePath("EMR0603055");
			action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",
					0));// ���ӵ�·��
			action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",
					0));// ���ӵ�����
			action.setData("FLG", false);// ��ģ��
			// System.out.println("---action----------------"+action);
			// ����ģ��
			this.openDialog("%ROOT%\\config\\emr\\EMRTransferWordUI.x", action);
		}
		// ��������
		else {
			action.setData("FROM_DEPT", "0306"); // ת������
			action.setData("TO_DEPT", ((TParm) this.getParameter()).getData(
					"ODI", "OP_DEPT_CODE")); // ת�����
			action.setData("DEPT_TYPE_FLG", "OPE");// ���ڿ���ѡ�������ʾ���ұ��
			action.setData("OP_CODE", ((TParm) this.getParameter())
					.getData("OP_CODE"));// ��ʽ
			action.setData("OPBOOK_SEQ", opBookSeq);
			this.openDialog("%ROOT%\\config\\ope\\OPETransfertype.x", action);
		}
	}

	/**
	 * ����һ����
	 */
	public void onTransfer() {
		TParm action = new TParm();
		action.setData("MR_NO", mrNo);// ������
		action.setData("CASE_NO", caseNo);// �����
		action.setData("OP_CODE", ((TParm) this.getParameter()).getData("ODI",
				"OP_CODE1"));// ��ʽ
		action.setData("OPBOOK_SEQ", opBookSeq);// �������뵥��
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x", action);
	}

	/**
	 * �õ�EMR·��
	 */
	public TParm getEmrFilePath(String subclassCode) {
		String sql = " SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,"
				+ " A.TEMPLET_PATH FROM EMR_TEMPLET A"
				+ " WHERE A.SUBCLASS_CODE = '" + subclassCode + "'";
		TParm result = new TParm();
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ��������״̬�������������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	public void opeListener(TParm parm) {
		if (StringUtils.isNotEmpty(opBookSeq)) {
			// ��������״̬(5_������(����))
			OPEOpBookTool.getInstance().updateOpeStatus(opBookSeq, "5",
					"'1','3','4'");
			// ������ӿڷ�����Ϣ
			TParm xmlParm = ADMXMLTool.getInstance().creatOPEStateXMLFile(
					caseNo, opBookSeq);
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
		}
	}

	/**
	 * ���밲ȫ�˲鵥�ر���Ӧ�¼�
	 * 
	 * @param parm
	 *            TParm
	 */
	public void clearListener(TParm parm) {
		this.onClear();
	}

	/**
	 * ȡ�ù���ʷ������Ϣ
	 * 
	 * @return ��������
	 */
	private String getAllergy() {
		String allergy = "";
		String drugStr = "";
		List allergyList = new ArrayList<String>();
		String sql = "SELECT A.CASE_NO,A.MR_NO,A.DRUGORINGRD_CODE,CASE A.DRUG_TYPE "
				+ " WHEN 'A' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='PHA_INGREDIENT' AND B.ID=A.DRUGORINGRD_CODE) "
				+ " WHEN 'B' THEN (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE=A.DRUGORINGRD_CODE) "
				+ " WHEN 'C' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='SYS_ALLERGYTYPE' AND B.ID=A.DRUGORINGRD_CODE) "
				+ " WHEN 'D' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
				+ " WHEN 'E' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
				+ " WHEN 'N' THEN '��' "
				+ " ELSE '' END AS ALLERGY_NAME,OPT_DATE  "
				+ " FROM OPD_DRUGALLERGY A "
				+ " WHERE A.MR_NO='"
				+ mrNo
				+ "'"
				+ " ORDER BY A.ADM_DATE,A.OPT_DATE ";
		TParm drugParm = new TParm(TJDODBTool.getInstance().select(sql));

		String allergyCode = "";
		if (drugParm.getCount() > 0) {
			int rowCount = drugParm.getCount();
			for (int i = 0; i < rowCount; i++) {
				allergyCode = drugParm.getValue("DRUGORINGRD_CODE", i);
				if ("N".equals(allergyCode)) {
					continue;
				}

				// ��ͬ�Ĺ�������ֻȡһ��
				if (!allergyList.contains(allergyCode)) {
					allergyList.add(allergyCode);
				} else {
					continue;
				}
				drugStr = drugStr + drugParm.getValue("ALLERGY_NAME", i) + ",";
			}

			if (drugStr.length() > 1) {
				drugStr = drugStr.substring(0, drugStr.length() - 1);
				allergy = "��������:" + drugStr;
			}
		}

		return allergy;
	}
}
