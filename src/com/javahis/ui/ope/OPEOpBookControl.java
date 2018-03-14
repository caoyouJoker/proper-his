package com.javahis.ui.ope;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.clp.BscInfoTool;
import jdo.hl7.Hl7Communications;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboOPEAnaMethod;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������
 * </p>
 * 
 * <p>
 * Description: ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-9-24
 * @version 4.0
 */
public class OPEOpBookControl extends TControl {
	private String MR_NO = "";// MR_NO
	private String CASE_NO = "";// CASE_NO
	private String IPD_NO = "";// סԺ�� סԺҽ��վ���õ�ʱ�� ��Ҫ���˲���
	private String ADM_TYPE = "";// �ż�ס��
	private String BED_NO = "";// ������ ֻ��סԺ�����д˲���
	private String BOOK_DEPT_CODE = "";// �������code
	private String BOOK_DR_CODE = "";// ������ԱID
	private String OPBOOK_SEQ = "";// ����������
	private String SAVE_FLG = "new";// ���淽ʽ new:�½� update:�޸�
	private TTable Table_BOOK_AST;
	private TTable Daily_Table;
	// 20170302 liuyalin add
	private TTable GDVAS_TABLE;
	private TTable OP_Table;
	private String CANCEL_FLG = "N";// ȡ��������
	
	private TParm parameter;

	public void onInit() {
		Table_BOOK_AST = (TTable) this.getComponent("Table_BOOK_AST");
		Daily_Table = (TTable) this.getComponent("Daily_Table");
		OP_Table = (TTable) this.getComponent("OP_Table");
		// 20170302 liuyalin add
		GDVAS_TABLE = (TTable) this.getComponent("GDVAS_TABLE");
		pageInit();
		TableInit();
	}

	/**
	 * ��ʼ������
	 */
	public void onParmInit() {
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			this.parameter = parm;
		} else {
			return;
		}
		// ������� ����
		// TParm parm = new TParm();
		// parm.setData("MR_NO", "000000000579");
		// parm.setData("CASE_NO", "100307000010");
		// parm.setData("ADM_TYPE", "O");//�ż�ס��
		// parm.setData("BOOK_DEPT_CODE", "10101");//���벿��
		// parm.setData("STATION_CODE","1");//�������߲���
		// parm.setData("BOOK_DR_CODE", "D001");//������Ա
		// parm.setData("ICD_CODE","A01");//�����
		// ******************************
		// *******����ҽ��վ������Ҫ�Ĳ���********
		MR_NO = parm.getValue("MR_NO");
		CASE_NO = parm.getValue("CASE_NO");
		ADM_TYPE = parm.getValue("ADM_TYPE");
		BOOK_DEPT_CODE = parm.getValue("BOOK_DEPT_CODE");
		BOOK_DR_CODE = parm.getValue("BOOK_DR_CODE");
		// ********��ѯ����������ϸ��Ϣ ��Ҫ�Ĳ���******
		if (parm.getValue("FLG").length() > 0)
			SAVE_FLG = parm.getValue("FLG"); // "update" ��ʾ��ѯ �������ʱ���޸�ԭ����Ϣ
		OPBOOK_SEQ = parm.getValue("OPBOOK_SEQ");
		this.setValue("OP_DEPT_CODE", BOOK_DEPT_CODE);
		this.setValue("MAIN_SURGEON", BOOK_DR_CODE);
		// modify by liming 2012/03/15 begin
		String opDateStr = SystemTool.getInstance().getDate().toString();
		String year = String.valueOf(opDateStr.substring(0, 4));
		String month = String.valueOf(opDateStr.substring(5, 7));
		String day = String.valueOf(opDateStr.substring(8, 10));
		Timestamp opeDate = StringTool.getTimestamp(year + month + day,
				"yyyyMMdd");
		this.setValue("OP_DATE", StringTool.rollDate(opeDate, 1));
		// modify by liming 2012/03/15 end.

		// ������Ժ����Ϻ���Ժ�����====pangben 2016-5-5
		if (parm.getValue("ICD_CODE").length() > 0) {
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("ICD_CODE"));
			Daily_Table.setItem(index, 3, parm.getValue("ICD_CODE"));
			Daily_Table.setItem(index, 1, "Y");
		}
		TLabel tLabel_18 = (TLabel) this.getComponent("tLabel_18");
		// �ж��ż�ס�� ��ʾ�������߲���
		if (ADM_TYPE.equals("O") || ADM_TYPE.equals("E")) {
			tLabel_18.setZhText("��������");
			this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
			this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
			this.setValue("OP_STATION_CODE_O", parm.getValue("STATION_CODE"));
		} else if (ADM_TYPE.equals("I")) {
			tLabel_18.setZhText("��������");
			this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
			this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
			this.setValue("OP_STATION_CODE_I", parm.getValue("STATION_CODE"));
		}
	}

	/**
	 * ������������ OpICD
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponentOP(Component com, int row, int column) {
		// ����ICD10�Ի������
		if (column != 2)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		textfield.setPopupMenuParameter("OP_ICD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOPOrder");
	}

	/**
	 * ȡ������ICD����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOPOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|OP_Table|getThis");
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("OPERATION_ICD");
		table.setItem(table.getSelectedRow(), "OP_ICD", orderCode);
		table.setItem(0, "MAIN_FLG", "Y"); // chenxi modufy 20130319
		if ("en".equals(this.getLanguage()))
			table.setItem(table.getSelectedRow(), "OP_DESC",
					parm.getValue("OPT_ENG_DESC"));
		else
			table.setItem(table.getSelectedRow(), "OP_DESC",
					parm.getValue("OPT_CHN_DESC"));
	}

	/**
	 * ��ϵ������� ICD10
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ����ICD10�Ի������
		if (column != 2)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		textfield
				.setPopupMenuParameter(
						"ICD10",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newAgentOrder");
	}

	/**
	 * ȡ��ICD10����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newAgentOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|Daily_Table|getThis");
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		table.setItem(table.getSelectedRow(), "DAILY_CODE", orderCode);
		if ("en".equals(this.getLanguage()))
			table.setItem(table.getSelectedRow(), "DAILY_DESC",
					parm.getValue("ICD_ENG_DESC"));
		else
			table.setItem(table.getSelectedRow(), "DAILY_DESC",
					parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * ���Grid ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		TTable DiagGrid = (TTable) this.getComponent("Daily_Table");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node.getColumn() == 2) {
			if (node.getRow() == (DiagGrid.getRowCount() - 1))
				DiagGrid.addRow();
		}
	}

	/**
	 * ����Grid ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onOpTableValueCharge(Object obj) {
		TTable OP_Table = (TTable) this.getComponent("OP_Table");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node.getColumn() == 2) {
			if (node.getRow() == (OP_Table.getRowCount() - 1))
				OP_Table.addRow();
		}
	}

	/**
	 * ���Grid ����ϱ���޸��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableMainCharge(Object obj) {
		TTable DiagGrid = (TTable) this.getComponent("Daily_Table");
		DiagGrid.acceptText();
		if (DiagGrid.getSelectedColumn() == 1) {
			int row = DiagGrid.getSelectedRow();
			for (int i = 0; i < DiagGrid.getRowCount(); i++) {
				DiagGrid.setItem(i, "MAIN_FLG", "N");
			}
			DiagGrid.setItem(row, "MAIN_FLG", "Y");
		}
	}

	/**
	 * ����Grid ����ϱ���޸��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onOpTableMainCharge(Object obj) {
		TTable OP_Table = (TTable) this.getComponent("OP_Table");
		OP_Table.acceptText();
		if (OP_Table.getSelectedColumn() == 1) {
			int row = OP_Table.getSelectedRow();
			for (int i = 0; i < OP_Table.getRowCount(); i++) {
				OP_Table.setItem(i, "MAIN_FLG", "N");
			}
			OP_Table.setItem(row, "MAIN_FLG", "Y");
		}
	}

	/**
	 * ҳ���ʼ��
	 */
	public void pageInit() {
		onParmInit();// ��ʼ������
//		System.out.println("sfsfsd:::::" + SAVE_FLG);
		if ("new".equals(SAVE_FLG)) {
			setDataForDoctor();
		} else if ("update".equals(SAVE_FLG)) {
			queryDataByOPBOOK_SEQ();
		}
	}

	/**
	 * ����ҽ��վ����ʱ ��ʼ��ҳ��
	 */
	private void setDataForDoctor() {
		if (MR_NO.length() <= 0) {
			return;
		}
		// ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		this.setValue("MR_NO", pat.getMrNo());// ������
		this.setValue("SEX", pat.getSexCode());// �Ա�
		this.setValue("ADM_TYPE", ADM_TYPE);// ������Դ
		if ("en".equals(this.getLanguage())) {
			this.setValue("PAT_NAME", pat.getName1());// ����
			// ��������
			String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					SystemTool.getInstance().getDate());
			this.setValue("AGE", res[0] + "Y");
		} else {
			this.setValue("PAT_NAME", pat.getName());// ����
			this.setValue(
					"AGE",
					StringUtil.getInstance().showAge(pat.getBirthday(),
							SystemTool.getInstance().getDate()));// ����

		}
		this.setValue("BOOK_DEPT_CODE", BOOK_DEPT_CODE);
		this.setValue("BOOK_DR_CODE", BOOK_DR_CODE);
		// �ж��Ƿ���סԺҽ��վ����
		if ("I".equals(ADM_TYPE)) {
			// ��ѯ����סԺ��Ϣ
			TParm admParm = new TParm();
			admParm.setData("CASE_NO", CASE_NO);
			//this.messageBox(CASE_NO);
			TParm admData = ADMTool.getInstance().getADM_INFO(admParm);
			IPD_NO = admData.getValue("IPD_NO", 0);// ��¼סԺ��
			BED_NO = admData.getValue("BED_NO", 0);// ��¼��λ��
			this.setValue("BED_NO", BED_NO);
			TParm parm = new TParm();
			parm.setData("CASE_NO", CASE_NO);
			// parm.setData("MAIN_FLG","Y");//�����
			parm.setData("IO_TYPE", "M");// ��Ժ���
			if(admData.getValue("DAY_OPE_FLG", 0).equals("Y")) {			// �ռ�������ѡ��ֵ   2017/3/29    BY  yanmm
				
				getCheckbox("DAY_OPE_FLG").setSelected(true);
	        }
		
			// ��ѯ������Ժ����Լ������=====pangben 2016-5-5
			TParm adm_daily = ADMTool.getInstance().queryDailyData(parm);
			if (adm_daily.getCount() > 0) {
				TParm dailyData = new TParm();
				// ���������ת����Grid�ܹ�ʶ��ĸ�ʽ
				for (int i = 0; i < adm_daily.getCount(); i++) {
					dailyData.addData("DEL", "N");
					dailyData.addData("MAIN_FLG",
							adm_daily.getValue("MAINDIAG_FLG", i));
					dailyData.addData("DAILY_CODE",
							adm_daily.getValue("ICD_CODE", i));
					dailyData.addData("DAILY_DESC",
							adm_daily.getValue("ICD_CODE", i));
				}
				Daily_Table.setParmValue(dailyData);
			}
		}
		initOPBookTable(MR_NO);
	}

	/**
	 * ����MR_NO ��ѯ��������������Ϣ
	 * 
	 * @param MR_NO
	 *            String
	 */
	private void initOPBookTable(String MR_NO) {
		TTable OPBookTable = (TTable) this.getComponent("OPBookTable");
		TParm parm = new TParm();
		parm.setData("MR_NO", MR_NO);
		TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
		OPBookTable.setParmValue(result);
	}

	/**
	 * Table��ʼ��
	 */
	public void TableInit() {
		// ����Table ����
		OP_Table = (TTable) this.getComponent("OP_Table");
		OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponentOP");
		// �������ı��¼�
		OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onOpTableMainCharge");
		OP_Table.addRow();
		OpList opList = new OpList();
		OP_Table.addItem("OpList", opList);
		// ���Table����
		Daily_Table = (TTable) this.getComponent("Daily_Table");
		Daily_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// ����ϸı��¼�
		Daily_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onDiagTableMainCharge");
		Daily_Table.addRow();
		OrderList orderList = new OrderList();
		Daily_Table.addItem("OrderList", orderList);
		// //���Gridֵ�ı��¼�
		this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
				"onDiagTableValueCharge");

		// ����Gridֵ�ı��¼�
		this.addEventListener("OP_Table->" + TTableEvent.CHANGE_VALUE,
				"onOpTableValueCharge");

	}

	/**
	 * �������Ӱ�ť�¼�
	 */
	public void onAddASTTable() {
		String userid = this.getValueString("BOOK_AST");// �������user_id
		if (userid.length() > 0) {
			TTable table = (TTable) this.getComponent("Table_BOOK_AST");
			int rowIndex = table.addRow();
			table.setValueAt("N", rowIndex, 0);
			table.setValueAt(userid, rowIndex, 1);
			table.setValueAt(userid, rowIndex, 2);
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		delTable();
		if (!checkData())
			return;
		if (SAVE_FLG.equals("new")) {
			insert();
		} else if (SAVE_FLG.equals("update")) {
			update();
		}
	}

	/**
	 * �½���������
	 */
	private void insert() {

		OPBOOK_SEQ = SystemTool.getInstance().getNo("ALL", "OPE", "OPBOOK_SEQ",
				"OPBOOK_SEQ"); // ����ȡ��ԭ��
		TParm insert = getSaveData();
		// �µ�����״̬ 0,���룻1,�ų���ϣ�2,�ӻ��ߣ�3,�����ҽ��ӣ�4,�����ȴ���5,������ʼ��6,���أ�7,����������8,���ز���
		insert.setData("STATE", "0");// ԤԼ״̬ 0 ���룬 1 �ų���� ��2�������
		// ===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			insert.setData("REGION_CODE", Operator.getRegion());
		}
		// =============pangben modify 20110630 stop

		String oldDiagCode = "";
		// MAIN_FLG;OP_ICD
		for (int i = 0; i < OP_Table.getDataStore().rowCount(); i++) {
			if (!OP_Table.getDataStore().isActive(i)) {
				continue;
			}
			if ("Y".equals(OP_Table.getItemString(i, "MAIN_FLG")))
				oldDiagCode = OP_Table.getItemString(i, "OP_ICD");
		}
		// System.out.println("�������ICD"+oldDiagCode);
		String caseNo = CASE_NO;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm clpPathParm = ADMInpTool.getInstance().selectall(parm);
		// System.out.println("�ٴ�·����Ϣ"+clpPathParm);
		TParm inBscParm = new TParm();
		inBscParm.setData("CLNCPATH_CODE",
				clpPathParm.getValue("CLNCPATH_CODE", 0));
		// �ٴ�·���Ƿ�������
		TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
		parm.setData("CASE_NO", caseNo);
		if (clpPathParm.getData("CLNCPATH_CODE", 0) != null
				&& clpPathParm.getValue("CLNCPATH_CODE", 0).length() > 0) {
			// System.out.println("1111111111");
			if (oldDiagCode.length() > 0) {
				// System.out.println("�������" + oldDiagCode);
				// IO_TYPE;MAINDIAG_FLG
				if (!oldDiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
					this.messageBox("��������ٴ�·�����");
				}
			}
		} else {
			// System.out.println("����ICD"+oldDiagCode);
			if (oldDiagCode.length() > 0) {
				TParm inBscParmNew = new TParm();
				inBscParmNew.setData("OPE_ICD_CODE", oldDiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(
						inBscParmNew);
				if (bscParmNew.getData("CLNCPATH_CODE") != null
						&& bscParmNew.getValue("CLNCPATH_CODE", 0).length() > 0)
					this.messageBox("�������"
							+ bscParmNew.getValue("CLNCPATH_CODE", 0) + "�ٴ�·��");

			}
		}
		insert.setData("MR_NO", this.parameter.getValue("MR_NO"));
		insert.setData("CASE_NO", this.parameter.getValue("CASE_NO"));
		insert.setData("STATION_CODE", this.parameter.getValue("STATION_CODE"));
		insert.setData("ICD_CODE", this.parameter.getValue("ICD_CODE"));
		insert.setData("ADM_TYPE", this.parameter.getValue("ADM_TYPE"));
		insert.setData("BOOK_DEPT_CODE", this.parameter.getValue("BOOK_DEPT_CODE"));
		insert.setData("BOOK_DR_CODE", this.parameter.getValue("BOOK_DR_CODE"));
		insert.setData("DAY_OPE_FLG", this.parameter.getValue("DAY_OPE_FLG"));
		TParm result = OPEOpBookTool.getInstance().insertOpBook(insert);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		SAVE_FLG = "update";
		// sendHL7Mes�ӿ�
		sendHL7Mes(insert, "OPE_APPLY");
		initOPBookTable(MR_NO);

        // add by wangb 2017/3/14 Ӧ�ƶ��鷿ϵͳҪ���ѳ�Ժ���߲��ٷ�����Ϣ START
        TParm amdParm = new TParm();
        amdParm.setData("CASE_NO", CASE_NO);
        // ��ѯסԺ��Ϣ
        amdParm = ADMInpTool.getInstance().selectall(amdParm);
        
        if (StringUtils.isEmpty(amdParm.getValue("DS_DATE", 0))) {
            //������Ϣ����XML
            ADMXMLTool.getInstance().creatXMLFile(CASE_NO);
        }
        // add by wangb 2017/3/14 Ӧ�ƶ��鷿ϵͳҪ���ѳ�Ժ���߲��ٷ�����Ϣ END

		// add by wangb 2015/06/08 ���ڽ����������ų̣���������ʱ�򿴰巢����Ϣ start
		if (StringUtils.equals("2", this.getValueString("TYPE_CODE"))) {
			// ��������ӿڷ�����Ϣ add by wangb 2015/06/08
			TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(
					CASE_NO, OPBOOK_SEQ);
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
		}
		// add by wangb 2015/06/08 ���ڽ����������ų̣���������ʱ�򿴰巢����Ϣ end
	}

	/**
	 * �޸�������Ϣ
	 */
	private void update() {
		TParm updateData = getSaveData();
		TParm result = OPEOpBookTool.getInstance().updateOpBook(updateData);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		// sendHL7Mes�ӿ�
		sendHL7Mes(updateData, "OPE_APPLY");
		initOPBookTable(MR_NO);

        // add by wangb 2017/3/14 Ӧ�ƶ��鷿ϵͳҪ���ѳ�Ժ���߲��ٷ�����Ϣ START
        TParm amdParm = new TParm();
        amdParm.setData("CASE_NO", CASE_NO);
        // ��ѯסԺ��Ϣ
        amdParm = ADMInpTool.getInstance().selectall(amdParm);
        
        if (StringUtils.isEmpty(amdParm.getValue("DS_DATE", 0))) {
            //������Ϣ����XML
            ADMXMLTool.getInstance().creatXMLFile(CASE_NO);
        }
        // add by wangb 2017/3/14 Ӧ�ƶ��鷿ϵͳҪ���ѳ�Ժ���߲��ٷ�����Ϣ END

		// add by wangb 2015/06/08 ���ڽ����������ų̣���������ʱ�򿴰巢����Ϣ start
		if (StringUtils.equals("2", this.getValueString("TYPE_CODE"))) {
			// ��������ӿڷ�����Ϣ add by wangb 2015/06/08
			TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(
					CASE_NO, OPBOOK_SEQ);
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
		}
		// add by wangb 2015/06/08 ���ڽ����������ų̣���������ʱ�򿴰巢����Ϣ end
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm getSaveData() {
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);// ����������
		parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", IPD_NO);
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("BED_NO", BED_NO);
		parm.setData("URGBLADE_FLG", this.getValueString("URGBLADE_FLG"));// �����������
		parm.setData("DAY_OPE_FLG", this.getValueString("DAY_OPE_FLG"));// �ռ�����
																			// add
																			// by
																			// huangtt
																			// 20161213
		parm.setData("OP_DATE", StringTool.getString(
				(Timestamp) this.getValue("OP_DATE"), "yyyyMMddHHmmss"));// ����ʱ��
		parm.setData("TF_FLG", this.getValueString("TF_FLG"));// ��̨���
		parm.setData("TIME_NEED", this.getValueString("TIME_NEED"));// ����ʱ��
		parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));// ������
		parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));// ��������
		parm.setData("PART_CODE", this.getValueString("PART_CODE"));// ������λ add
																	// by
																	// wanglong
																	// 20121206
		parm.setData("ISO_FLG", this.getValueString("ISO_FLG"));// ����������� add by
																// wanglong
																// 20121206
		parm.setData("ANA_CODE", this.getValueString("ANA_CODE"));// ����ʽ
		parm.setData("OP_DEPT_CODE", this.getValueString("OP_DEPT_CODE"));// ��������
		if ("I".equals(ADM_TYPE))
			parm.setData("OP_STATION_CODE",
					this.getValueString("OP_STATION_CODE_I"));// ��������
		else if ("O".equals(ADM_TYPE) || "E".equals(ADM_TYPE))
			parm.setData("OP_STATION_CODE",
					this.getValueString("OP_STATION_CODE_O"));// ��������
		TParm Daily_Data = this.getDailyData();// ��ȡ�����Ϣ
		parm.setData("DIAG_CODE1", Daily_Data.getValue("DIAG_CODE1"));
		parm.setData("DIAG_CODE2", Daily_Data.getValue("DIAG_CODE2"));
		parm.setData("DIAG_CODE3", Daily_Data.getValue("DIAG_CODE3"));
		parm.setData("BOOK_DEPT_CODE", this.getValue("BOOK_DEPT_CODE"));// ԤԼ����
		TParm Op_Data = this.getOpData();// ��ȡ������Ϣ
		parm.setData("OP_CODE1", Op_Data.getValue("OP_CODE1"));
		parm.setData("OP_CODE2", Op_Data.getValue("OP_CODE2"));
		parm.setData("BOOK_DR_CODE", this.getValue("BOOK_DR_CODE"));// ԤԼҽʦ
		parm.setData("MAIN_SURGEON", this.getValue("MAIN_SURGEON"));// ����ҽʦ
		TParm BOOK_AST = this.getASTData();// ������Ϣ
		parm.setData("BOOK_AST_1", BOOK_AST.getValue("BOOK_AST_1"));
		parm.setData("BOOK_AST_2", BOOK_AST.getValue("BOOK_AST_2"));
		parm.setData("BOOK_AST_3", BOOK_AST.getValue("BOOK_AST_3"));
		parm.setData("BOOK_AST_4", BOOK_AST.getValue("BOOK_AST_4"));
		parm.setData("REMARK", this.getValueString("REMARK"));
		parm.setData("GRANT_AID", this.getValueDouble("GRANT_AID"));// wanglong
																	// add
																	// 20141010
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		// 20170303 liuyalin add
		TParm GDVASData = this.getGDVASData();// ¼��;����Ϣ
		parm.setData(
				"GDVAS_CODE",
				GDVASData.getValue("GDVAS_CODE").replace("[", "")
						.replace("]", "").replace(" ", ""));
		parm.setData(
				"GDVAS_DESC",
				GDVASData.getValue("GDVAS_DESC").replace("[", "")
						.replace("]", "").replace(" ", ""));
		parm.setData("GDVAS_REMARKS", GDVASData.getValue("GDVAS_REMARKS")
				.replace("[", "").replace("]", "").replace(" ", ""));
		// fux modify 20140626 ������¼Ѫ��
		// parm.setData("GDVAS_CODE", this.getValue("GDVAS_CODE"));
		return parm;
	}

	/**
	 * ѡ��Ƴ�������
	 */
	public void onDeptOp() {
		String dept_code = this.getValueString("OP_DEPT_CODE");
		if (dept_code.length() <= 0) {
			this.messageBox("E0077");
			return;
		}
		String op_icd = (String) this.openDialog(
				"%ROOT%/config/ope/OPEDeptOpShow.x", dept_code);
		TTable Op_Table = (TTable) this.getComponent("OP_Table");
		// ���ش�ֵ ��ʾ�ڱ����
		Op_Table.setValueAt_(op_icd, Op_Table.getRowCount() - 1, 2);
		Op_Table.setValueAt_(op_icd, Op_Table.getRowCount() - 1, 3);
		if (op_icd.length() > 0)
			Op_Table.addRow();
	}

	/**
	 * ��ȡ�������
	 */
	private TParm getDailyData() {
		TTable Daily_Table = (TTable) this.getComponent("Daily_Table");
		TParm parm = new TParm();
		int index = 2;// ����� ��2��Ϊ��ʼֵ ��Ϊ�������1
		for (int i = 0; i < Daily_Table.getRowCount(); i++) {
			if (Daily_Table.getValueAt(i, 2).toString().trim().length() > 0) {
				// �ж������
				if ("Y".equals(Daily_Table.getValueAt(i, 1).toString())) {
					parm.setData("DIAG_CODE1", Daily_Table.getValueAt(i, 2));
				} else {
					parm.setData("DIAG_CODE" + index,
							Daily_Table.getValueAt(i, 2));
					index++;
				}
			}
		}
		return parm;
	}

	/**
	 * ��ȡ������ʽ����
	 * 
	 * @return TParm
	 */
	private TParm getOpData() {
		TTable Op_Table = (TTable) this.getComponent("OP_Table");
		TParm parm = new TParm();
		for (int i = 0; i < Op_Table.getRowCount(); i++) {
			if (Op_Table.getValueAt(i, 2).toString().trim().length() > 0) {
				// �ж������
				if ("Y".equals(Op_Table.getValueAt(i, 1))) {
					parm.setData("OP_CODE1", Op_Table.getValueAt(i, 2));
				} else {
					parm.setData("OP_CODE2", Op_Table.getValueAt(i, 2));
				}
			}
		}
		return parm;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return TParm
	 */
	private TParm getASTData() {
		TTable AST_Table = (TTable) this.getComponent("Table_BOOK_AST");
		TParm parm = new TParm();
		for (int i = 0; i < AST_Table.getRowCount(); i++) {
			if (AST_Table.getValueAt(i, 1).toString().trim().length() > 0) {
				parm.setData("BOOK_AST_" + (i + 1), AST_Table.getValueAt(i, 1));
			}
		}
		return parm;
	}

	// 20170303 liuyalin add
	/**
	 * ��ȡ¼��;��Grid����
	 * 
	 * @return TParm
	 */
	// 20170303 liuyalin add
	private TParm getGDVASData() {
		TParm parm = new TParm();
		for (int i = 0; i < GDVAS_TABLE.getRowCount(); i++) {
			if (GDVAS_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
				parm.addData("GDVAS_CODE",GDVAS_TABLE.getItemString(i, "GDVAS_CODE"));
				parm.addData("GDVAS_DESC",GDVAS_TABLE.getItemString(i, "GDVAS_DESC"));
				if ("14".equals(GDVAS_TABLE.getItemString(i, "GDVAS_CODE"))) {
					parm.addData("GDVAS_REMARKS",GDVAS_TABLE.getItemString(i, "GDVAS_REMARKS"));
				}
			}
		}
		return parm;
	}

	/**
	 * ɾ����Ϻ�����������Ϣ����ѡɾ����ǵģ�
	 */
	private void delTable() {
		TTable opTable = (TTable) this.getComponent("OP_Table");
		opTable.acceptText();
		TTable dailyTable = (TTable) this.getComponent("Daily_Table");
		dailyTable.acceptText();
		TTable ASTTable = (TTable) this.getComponent("Table_BOOK_AST");
		ASTTable.acceptText();
		for (int i = opTable.getRowCount() - 1; i >= 0; i--) {
			if ("Y".equals(opTable.getValueAt(i, 0))) {
				opTable.removeRow(i);
			}
		}
		for (int i = dailyTable.getRowCount() - 1; i >= 0; i--) {
			if ("Y".equals(dailyTable.getValueAt(i, 0))) {
				dailyTable.removeRow(i);
			}
		}
		for (int i = ASTTable.getRowCount() - 1; i >= 0; i--) {
			if ("Y".equals(ASTTable.getValueAt(i, 0))) {
				ASTTable.removeRow(i);
			}
		}
	}

	/**
	 * �������
	 */
	private boolean checkData() {
		if ("Y".equals(CANCEL_FLG)) {
			this.messageBox("E0089");
			return false;
		}
		TTable opTable = (TTable) this.getComponent("OP_Table");
		opTable.acceptText();
		TTable dailyTable = (TTable) this.getComponent("Daily_Table");
		dailyTable.acceptText();
		TTable gdvasTable = (TTable) this.getComponent("GDVAS_TABLE");
		gdvasTable.acceptText();
		boolean flg = false;// ����ϱ�ʶ true:��������ϣ��������� false:�����������(������)
		for (int i = 0; i < opTable.getRowCount(); i++) {
			if ("Y".equals(opTable.getValueAt(i, 1))) {
				flg = true;
			}
		}
		if (!flg) {
			this.messageBox("E0078");
			return flg;
		}
		for (int i = 0; i < dailyTable.getRowCount(); i++) {
			if ("Y".equals(dailyTable.getValueAt(i, 1))) {
				flg = true;
			}
		}
		if (!flg) {
			this.messageBox("E0079");
			return false;
		}
		if (this.getValue("OP_DATE") == null
				|| this.getValueString("OP_DATE").equals("")) {
			this.messageBox("E0076");
			this.grabFocus("OP_DATE");
			return false;
		}
		// modify shibaoliu 20120317
		// if(this.getValue("TIME_NEED")==null||this.getValueString("TIME_NEED").equals("")){
		// this.messageBox("E0090");
		// this.grabFocus("TIME_NEED");
		// return false;
		// }
		if (this.getValue("OP_DEPT_CODE") == null
				|| this.getValueString("OP_DEPT_CODE").equals("")) {
			this.messageBox("E0077");
			this.grabFocus("OP_DEPT_CODE");
			return false;
		}
		if (ADM_TYPE.equals("I")) {
			if (this.getValue("OP_STATION_CODE_I") == null
					|| this.getValueString("OP_STATION_CODE_I").equals("")) {
				this.messageBox("E0091");
				this.grabFocus("OP_STATION_CODE_I");
				return false;
			}
		}
		if (this.getValue("MAIN_SURGEON") == null
				|| this.getValueString("MAIN_SURGEON").equals("")) {
			this.messageBox("E0092");
			this.grabFocus("MAIN_SURGEON");
			return false;
		}
		if (this.getValueString("TYPE_CODE").equals("")) {// wanglong add
															// 20150413
			this.messageBox("��ѡ����������");
			this.grabFocus("TYPE_CODE");
			return false;
		}
		return flg;
	}

	/**
	 * ����OPBOOK_SEQ��ѯĳһ�������������Ϣ
	 */
	private void queryDataByOPBOOK_SEQ() {
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
		if (result.getErrCode() < 0) {
			return;
		}
		this.setData(result);
	}

	/**
	 * ��ҳ��ؼ���ֵ
	 */
	private void setData(TParm parm) {
		MR_NO = parm.getValue("MR_NO", 0);
		CASE_NO = parm.getValue("CASE_NO", 0);
		IPD_NO = parm.getValue("IPD_NO", 0);
		BED_NO = parm.getValue("BED_NO", 0);
		CANCEL_FLG = parm.getValue("CANCEL_FLG", 0);
		ADM_TYPE = parm.getValue("ADM_TYPE", 0);
		if (MR_NO.length() <= 0) {
			return;
		}
		// ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		this.setValue("MR_NO", pat.getMrNo());// ������
		this.setValue("SEX", pat.getSexCode());// �Ա�
		if ("en".equals(this.getLanguage())) {
			this.setValue("PAT_NAME", pat.getName1());// ����
			// ��������
			String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					SystemTool.getInstance().getDate());
			this.setValue("AGE", res[0] + "Y");
		} else {
			this.setValue("PAT_NAME", pat.getName());// ����
			this.setValue(
					"AGE",
					StringUtil.getInstance().showAge(pat.getBirthday(),
							SystemTool.getInstance().getDate()));// ����

		}

		// add by huangtt 20161213����ռ�����������
		//this.setValue("DAY_OPE_CODE", parm.getValue("DAY_OPE_CODE", 0));

		this.setValue("ADM_TYPE", parm.getValue("ADM_TYPE", 0));
		this.setValue("BED_NO", parm.getValue("BED_NO", 0));
		this.setValue("OP_DATE", parm.getTimestamp("OP_DATE", 0));
		this.setValue("TYPE_CODE", parm.getValue("TYPE_CODE", 0));
		this.setValue("ROOM_NO", parm.getValue("ROOM_NO", 0));
		this.setValue("BOOK_DEPT_CODE", parm.getValue("BOOK_DEPT_CODE", 0));
		this.setValue("BOOK_DR_CODE", parm.getValue("BOOK_DR_CODE", 0));
		this.setValue("TIME_NEED", parm.getValue("TIME_NEED", 0));
		this.setValue("TF_FLG", parm.getValue("TF_FLG", 0));
		this.setValue("URGBLADE_FLG", parm.getValue("URGBLADE_FLG", 0));
		this.setValue("PART_CODE", parm.getValue("PART_CODE", 0));// ������λ add by
																	// wanglong
																	// 20121206
		this.setValue("ISO_FLG", parm.getValue("ISO_FLG", 0));// ����������� add by
																// wanglong
																// 20121206
		this.setValue("OP_DEPT_CODE", parm.getValue("OP_DEPT_CODE", 0));
		if (ADM_TYPE.equals("I"))
			this.setValue("OP_STATION_CODE_I",
					parm.getValue("OP_STATION_CODE", 0));
		else if (ADM_TYPE.equals("O") || ADM_TYPE.equals("E"))
			this.setValue("OP_STATION_CODE_O",
					parm.getValue("OP_STATION_CODE", 0));
		this.setValue("MAIN_SURGEON", parm.getValue("MAIN_SURGEON", 0));
		this.setValue("ANA_CODE", parm.getValue("ANA_CODE", 0));
		this.setValue("REMARK", parm.getValue("REMARK", 0));
		this.setValue("GRANT_AID", parm.getDouble("GRANT_AID", 0));
		// // fux modify 20140626
		// this.setValue("GDVAS_CODE", parm.getValue("GDVAS_CODE", 0));
		// liuyalin add 20170307
		TTable GDVAS_TABLE = (TTable) this.getComponent("GDVAS_TABLE");
		String[] gdvas_code = parm.getValue("GDVAS_CODE").replace("[", "").replace("]", "").replace(" ","").split(",");
//		if (parm.getValue("GDVAS_CODE", 0).length() > 0) {
//			for (int i = 0; i < gdvas_code.length; i++) {
//			this.setValue(
//					"GDVAS_CODE",
//					parm.getValue("GDVAS_CODE").replace("[", "")
//							.replace("]", "").replace(" ","").split(",")[i]);
//	     		int row = GDVAS_TABLE.addRow();
//				GDVAS_TABLE.setItem(row, 0, ((TComboBox) this
//						.getComponent("GDVAS_CODE")).getSelectedID());
//				GDVAS_TABLE.setItem(row, 1, ((TComboBox) this
//						.getComponent("GDVAS_CODE")).getSelectedName());
//				if ("14".equals(GDVAS_TABLE.getItemString(i, "GDVAS_CODE"))) {
//					parm.addData("GDVAS_REMARKS",
//							GDVAS_TABLE.setItem(row, 2, parm.getValue("GDVAS_REMARKS").replace("[", "").replace("]", "")));
//				}
//			}
//		}
		//liuyalin modify 20170410
		if (parm.getValue("GDVAS_CODE", 0).length() > 0) {
			for (int i = 0; i < gdvas_code.length; i++) {
			this.setValue("GDVAS_CODE",parm.getValue("GDVAS_CODE").replace("[", "").replace("]", "").replace(" ","").split(",")[i]);
	     		int row = GDVAS_TABLE.addRow();
				GDVAS_TABLE.setItem(row, 0, this.getValueString("GDVAS_CODE"));
				String sql = "SELECT GDVAS_CODE,GDVAS_DESC FROM SYS_INPUTWAY WHERE GDVAS_CODE = '"+ this.getValueString("GDVAS_CODE")+"'";
				TParm descParm = new TParm(TJDODBTool.getInstance().select(sql));
				GDVAS_TABLE.setItem(row, 1, descParm.getValue("GDVAS_DESC").replace("[", "").replace("]", ""));
				if ("14".equals(GDVAS_TABLE.getItemString(i, "GDVAS_CODE"))) {
					parm.addData("GDVAS_REMARKS",GDVAS_TABLE.setItem(row, 2, parm.getValue("GDVAS_REMARKS").replace("[", "").replace("]", "")));
				}
			}
		}
		this.clearValue("GDVAS_CODE");
		TTable Table_BOOK_AST = (TTable) this.getComponent("Table_BOOK_AST");
		if (parm.getValue("BOOK_AST_1", 0).length() > 0) {
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index, 1, parm.getValue("BOOK_AST_1", 0));
			Table_BOOK_AST.setItem(index, 2, parm.getValue("BOOK_AST_1", 0));
		}
		if (parm.getValue("BOOK_AST_2", 0).length() > 0) {
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index, 1, parm.getValue("BOOK_AST_2", 0));
			Table_BOOK_AST.setItem(index, 2, parm.getValue("BOOK_AST_2", 0));
		}
		if (parm.getValue("BOOK_AST_3", 0).length() > 0) {
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index, 1, parm.getValue("BOOK_AST_3", 0));
			Table_BOOK_AST.setItem(index, 2, parm.getValue("BOOK_AST_3", 0));
		}
		if (parm.getValue("BOOK_AST_4", 0).length() > 0) {
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index, 1, parm.getValue("BOOK_AST_4", 0));
			Table_BOOK_AST.setItem(index, 2, parm.getValue("BOOK_AST_4", 0));
		}
		Daily_Table = (TTable) this.getComponent("Daily_Table");
		if (parm.getValue("DIAG_CODE1", 0).length() > 0) {
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("DIAG_CODE1", 0));
			Daily_Table.setItem(index, 3, parm.getValue("DIAG_CODE1", 0));
			Daily_Table.setItem(index, 1, "Y");
		}
		if (parm.getValue("DIAG_CODE2", 0).length() > 0) {
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("DIAG_CODE2", 0));
			Daily_Table.setItem(index, 3, parm.getValue("DIAG_CODE2", 0));
		}
		if (parm.getValue("DIAG_CODE3", 0).length() > 0) {
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("DIAG_CODE3", 0));
			Daily_Table.setItem(index, 3, parm.getValue("DIAG_CODE3", 0));
		}
		OP_Table = (TTable) this.getComponent("OP_Table");
		if (parm.getValue("OP_CODE1", 0).length() > 0) {
			int index = OP_Table.addRow();
			OP_Table.setItem(index, 2, parm.getValue("OP_CODE1", 0));
			OP_Table.setItem(index, 3, parm.getValue("OP_CODE1", 0));
			OP_Table.setItem(index, 1, "Y");
		}
		if (parm.getValue("OP_CODE2", 0).length() > 0) {
			int index = OP_Table.addRow();
			OP_Table.setItem(index, 2, parm.getValue("OP_CODE2", 0));
			OP_Table.setItem(index, 3, parm.getValue("OP_CODE2", 0));
		}

		initOPBookTable(MR_NO);
	}

	/**
	 * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			Vector e = (Vector) parm.getData("ICD_ENG_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i))) {
					if ("en".equals(OPEOpBookControl.this.getLanguage())) {
						return "" + e.get(i);
					} else {
						return "" + d.get(i);
					}
				}
			}
			return s;
		}
	}

	/**
	 * ����CODE�滻���� ģ����ѯ���ڲ��ࣩ
	 */
	public class OpList extends TLabel {
		TDataStore dataStore = new TDataStore();

		public OpList() {
			dataStore.setSQL("select * from SYS_OPERATIONICD");
			dataStore.retrieve();
		}

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("OPERATION_ICD");
			Vector d = (Vector) parm.getData("OPT_CHN_DESC");
			Vector e = (Vector) parm.getData("OPT_ENG_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i))) {
					if ("en".equals(OPEOpBookControl.this.getLanguage())) {
						return "" + e.get(i);
					} else {
						return "" + d.get(i);
					}
				}
			}
			return s;
		}
	}

	/**
	 * ���������б�
	 */
	public void onOPBookTableClick() {
		TTable OPBookTable = (TTable) this.getComponent("OPBookTable");
		int row = OPBookTable.getSelectedRow();
		if (row > -1) {
			// add by wangb 2016/09/05 START
			// �������ɹ����������룬����У�飬״̬Ϊ��������Ĳ���ʾ������������״̬����ʾ��״̬�Ƿ�����޸�
			// ��������
			String opeType = OPBookTable.getParmValue().getValue("TYPE_CODE",
					row);
			// ��ǰ����״̬
			int opeState = OPBookTable.getParmValue().getInt("STATE", row);
			String message1 = "";
			String message2 = "Ҫ�޸�ѡ�е�����������Ϣ��";

			if (opeState != 0) {
				String sql = "SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_STATE"
						+ opeType + "'";
				// ��ѯ����״̬��Ϣ
				TParm opeStateParm = new TParm(TJDODBTool.getInstance().select(
						sql));

				int count = opeStateParm.getCount();
				if (opeStateParm.getErrCode() == 0 && count > 0) {
					for (int i = 0; i < count; i++) {
						if (opeState == opeStateParm.getInt("ID", i)) {
							message1 = "��ǰ����״̬Ϊ��"
									+ opeStateParm.getValue("CHN_DESC", i)
									+ "����";
							break;
						}
					}
				}
			}

			int re = this.messageBox("��ʾ", message1 + message2, 0);
			// add by wangb 2016/09/05 END
			if (re == 0) {
				OPBOOK_SEQ = OPBookTable.getValueAt(row, 0).toString();
				SAVE_FLG = "update";
				this.clearValue("OP_DATE;TYPE_CODE;ROOM_NO;TIME_NEED;TF_FLG;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O;GDVAS_CODE");
				this.clearValue("MAIN_SURGEON;ANA_CODE;BOOK_AST;REMARK;GRANT_AID;PART_CODE;ISO_FLG");// modify
																										// by
																										// wanglong
																										// 20121213
				TableClear();
				queryDataByOPBOOK_SEQ();
				TParm parm = new TParm();
				parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
				TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
				if(result.getValue("DAY_OPE_FLG", 0).equals("Y")) {			// �ռ�������ѡ��ֵ   2017/3/29    BY  yanmm
					getCheckbox("DAY_OPE_FLG").setSelected(true);
		        }else{
		        	getCheckbox("DAY_OPE_FLG").setSelected(false);
		        }
				
				Daily_Table.addRow();
				OP_Table.addRow();
			}
			
		}
	}

	// 20170302 liuyalin add
	/**
	 * ���;�� ����¼�
	 */
	public void onGDVAS_ADD() {
		String id = this.getValueString("GDVAS_CODE");
		if (!checkGrid(GDVAS_TABLE, id, 0)) {
			return;
		}
		if (GDVAS_TABLE.getRowCount() >= 4) {
			this.messageBox("ֻ��ѡ�����������ָ��¼��;����һ���Զ�����");
			return;
		}
		if (id.length() > 0) {
			TTable table = (TTable) this.getComponent("GDVAS_TABLE");
			int row = table.addRow();
			table.setItem(row, 0, id);
			table.setItem(row, 1, this.getText("GDVAS_CODE"));
			this.clearValue("GDVAS_CODE");
		}
	}


	/**
	 * ���;�� ɾ���¼�
	 */
	public void onGDVAS_DEL() {
		int row = GDVAS_TABLE.getSelectedRow();
		if (row > -1) {
			GDVAS_TABLE.removeRow(row);
		}
	}

	/**
	 * ���Grid���Ƿ����ظ�����Ա
	 * 
	 * @param table
	 *            TTable
	 * @param user_id
	 *            String
	 * @return boolean
	 */
	private boolean checkGrid(TTable table, String user_id, int column) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (user_id.equals(table.getValueAt(i, column).toString())) {
				this.messageBox("�����ظ�ѡ��ͬһ��¼��;��");
				return false;
			}
		}
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("OP_DATE;TYPE_CODE;ROOM_NO;TIME_NEED;TF_FLG;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O;GDVAS_CODE");
		this.clearValue("MAIN_SURGEON;ANA_CODE;BOOK_AST;REMARK;GRANT_AID;PART_CODE;ISO_FLG;DAY_OPE_FLG");// modify
																											// by
																											// wanglong
																											// 20121213
		Table_BOOK_AST = (TTable) this.getComponent("Table_BOOK_AST");
		Table_BOOK_AST.removeRowAll();
		Daily_Table = (TTable) this.getComponent("Daily_Table");
		Daily_Table.removeRowAll();
		Daily_Table.addRow();
		// 20170302 liuyalin modify
		GDVAS_TABLE = (TTable) this.getComponent("GDVAS_TABLE");
		GDVAS_TABLE.removeRowAll();
		OP_Table = (TTable) this.getComponent("OP_Table");
		OP_Table.removeRowAll();
		OP_Table.addRow();
		CANCEL_FLG = "N";
		SAVE_FLG = "new";
		CASE_NO = this.parameter.getValue("CASE_NO");
		TParm admParm = new TParm();
		admParm.setData("CASE_NO", CASE_NO);
		TParm admData = ADMTool.getInstance().getADM_INFO(admParm);
		if(admData.getValue("DAY_OPE_FLG", 0).equals("Y")) {			// �ռ�������ѡ��ֵ   2017/3/29   BY  yanmm
			getCheckbox("DAY_OPE_FLG").setSelected(true);
        }
		setDataForDoctor();
	}

	/**
	 * ��ձ��
	 */
	private void TableClear() {
		Table_BOOK_AST = (TTable) this.getComponent("Table_BOOK_AST");
		Table_BOOK_AST.removeRowAll();
		Daily_Table = (TTable) this.getComponent("Daily_Table");
		Daily_Table.removeRowAll();
		OP_Table = (TTable) this.getComponent("OP_Table");
		OP_Table.removeRowAll();
		// 20170303 liuyalin add
		GDVAS_TABLE = (TTable) this.getComponent("GDVAS_TABLE");
		GDVAS_TABLE.removeRowAll();
	}

	/**
	 * ȡ������
	 */
	public void onCancel() {
		if (OPBOOK_SEQ.length() <= 0) {
			this.messageBox("E0094");
			return;
		}
		if ("Y".equals(CANCEL_FLG)) {
			this.messageBox("E0095");
			return;
		}
		TParm parm = new TParm();
		//start machao ȡ����������ȡ���ˣ�ID��ʱ��
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);		
		parm.setData("CANCELDATE", StringTool.getString(
				new Timestamp(System.currentTimeMillis()), "yyyyMMddHHmmss"));
		parm.setData("CANCELTREM",Operator.getIP());
		parm.setData("CANCELUSER",Operator.getID());
		//System.out.println("mmmm::"+parm);
		//end machao
		TParm re = OPEOpBookTool.getInstance().cancelOpBook(parm);
		if (re.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		// liuf 20140626
		sendHL7Mes(OPBOOK_SEQ, "OPE_SCHEDULE");

		// add by wangb 2015/06/30 ��������ȡ��ʱ�����������Ϣ start
		// ��������ӿڷ�����Ϣ add by wangb 2015/06/30
		TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(CASE_NO,
				OPBOOK_SEQ);
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
		}
		// add by wangb 2015/06/30 ��������ȡ��ʱ�����������Ϣ end

		initOPBookTable(MR_NO);
		OPBOOK_SEQ = "";
		this.onClear();
	}

	// liuf 20140626
	// $$==========zhangs =============$$//
	// /**
	// * hl7�ӿ�
	// * @param parm TParm
	// * @param type String
	// */
	private void sendHL7Mes(String OPBOOK_SEQ, String type) {
		try {
			// �����ų�
			if (type.equals("OPE_SCHEDULE")) {
				List list = new ArrayList();
				String sql = " SELECT * FROM OPE_OPBOOK WHERE OPBOOK_SEQ ='"
						+ OPBOOK_SEQ + "'";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				result.setData("ROOM_NAME", this.getText("ROOM_NO"));
				list.add(result);
				TParm resultParm = Hl7Communications.getInstance()
						.Hl7MessageOPE(list, type);
				if (resultParm.getErrCode() < 0)
					this.messageBox(resultParm.getErrText());
			}
		} catch (Exception ex3) {
			System.out.println(ex3.getMessage());
		}
	}

	public void onSendReCancel() {
		if (OPBOOK_SEQ.length() <= 0) {
			this.messageBox("E0094");
			return;
		}
		// if("Y".equals(CANCEL_FLG)){
		// this.messageBox("E0095");
		// return;
		// }
		// ��������
		// TParm parm = new TParm();
		// parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
		// liuf 20140626
		sendHL7Mes(OPBOOK_SEQ, "OPE_SCHEDULE");
	}

	// $$==========zhangs =============$$//
	/**
	 * ���ò�����Ϣ����
	 */
	public void onPatInfo() {
		TParm parm = new TParm();
		parm.setData("OPE", "OPE");
		parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * ����ѡ���¼�
	 */
	public void onOP_DEPT_CODE() {
		this.clearValue("OP_STATION_CODE_I;MAIN_SURGEON");
	}

	/**
	 * ��Ѫ����
	 */
	public void onBlood() {
		if ("new".equals(SAVE_FLG)) {
			this.messageBox("E0096");
			return;
		}
		TParm Daily_Data = this.getDailyData();// ��ȡ�����Ϣ ��һ���������
		if (Daily_Data.getValue("DIAG_CODE1").length() <= 0) {
			this.messageBox("E0097");
			return;
		}
		if (this.getValue("OP_DATE") == null) {
			this.messageBox("E0076");
			this.grabFocus("OP_DATE");
			return;
		}
		OrderList order = new OrderList();
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", ADM_TYPE);
		parm.setData("MR_NO", MR_NO);
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("DEPT_CODE", BOOK_DEPT_CODE);
		parm.setData("DR_CODE", BOOK_DR_CODE);
		parm.setData("ICD_CODE", Daily_Data.getValue("DIAG_CODE1"));
		parm.setData("ICD_DESC",
				order.getTableShowValue(Daily_Data.getValue("DIAG_CODE1")));
		parm.setData("USE_DATE", this.getValue("OP_DATE"));
		this.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parm);
	}

	/**
	 * ��������
	 */
	public void onDetail() {
		if (OPBOOK_SEQ.length() <= 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.setData("MR_NO", MR_NO);
		parm.setData("ADM_TYPE", ADM_TYPE);
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);
	}

	// $$==========zhangs =============$$//
	/**
	 * hl7�ӿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 */
	private void sendHL7Mes(TParm parm, String type) {
		try {
			parm.setData("OP_DEPT_DESC", this.getText("OP_DEPT_CODE"));// ������������
			parm.setData("DAILY_TABLE",
					((TTable) this.getComponent("Daily_Table")).getValue());// ��ȡ�����Ϣ
			parm.setData("OP_TABLE",
					((TTable) this.getComponent("OP_Table")).getValue());// ��ȡ������Ϣ
			parm.setData("TABLE_BOOK_AST",
					((TTable) this.getComponent("Table_BOOK_AST")).getValue());// ��ȡ������Ϣ
			parm.setData("ANA_DESC", ((TComboOPEAnaMethod) this
					.getComponent("ANA_CODE")).getSelectedName());// ��������ʽ����
			parm.setData("ROOM_NAME", this.getText("ROOM_NO"));// ����������

			// ��������
			if (type.equals("OPE_APPLY")) {
				List list = new ArrayList();
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance()
						.Hl7MessageOPE(list, type);
				if (resultParm.getErrCode() < 0)
					this.messageBox(resultParm.getErrText());
			}
		} catch (Exception ex3) {
			System.out.println(ex3.getMessage());
		}
	}
	
    
	/**
	 * �õ�TCheckBox�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TCheckBox getCheckbox(String tag) {
		return (TCheckBox) this.getComponent(tag);
		
	}
    
	
	

	public void onSendRe() {
		if (!checkData())
			return;
		TParm Data = getSaveData();
		sendHL7Mes(Data, "OPE_APPLY");
	}
}
