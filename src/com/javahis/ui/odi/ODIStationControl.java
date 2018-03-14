package com.javahis.ui.odi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import oracle.jdbc.oracore.TDSPatch;

import org.apache.commons.lang.time.DateUtils;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.clp.CLPTool;
import jdo.ctr.CTRPanelTool;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRCdrTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSTool;
import jdo.ins.INSTJTool;
import jdo.odi.ODIPICTool;
import jdo.odi.OdiDrugAllergy;
import jdo.odi.OdiMainTool;
import jdo.odi.OdiObject;
import jdo.odi.OdiOrderTool;
import jdo.odi.OdiSqlObject;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.DrugAllergyTool;
import jdo.opd.TotQtyTool;
import jdo.ope.OPEDeptOpTool;
import jdo.pha.PassTool;
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PatTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.DateTool;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.javahis.system.textFormat.TextFormatSYSPhaFreq;
import com.javahis.system.textFormat.TextFormatSYSPhaRoute;
import com.javahis.ui.opd.CDSSStationDosntWork;
import com.javahis.ui.opd.CDSSStationDrools;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ADMUtil;
import com.javahis.util.AMIUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;
import com.javahis.util.SysFeeUtil;

import device.PassDriver;

/**
 * <p>
 * Title: סԺ����=>סԺҽ��վϵͳ=>סԺҽ��վ
 * </p>
 * 
 * <p>
 * 
 * Description: סԺҽ��վ
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2009��1��
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class ODIStationControl extends TControl {
	/**
	 * ��ʱҽ��TABLE
	 */
	public static String TABLE1 = "TABLE1";
	/**
	 * ����ҽ��TABLE
	 */
	public static String TABLE2 = "TABLE2";
	/**
	 * ��Ժ��ҩҽ��TABLE
	 */
	public static String TABLE3 = "TABLE3";
	/**
	 * ��ҩ��ƬTABLE
	 */
	public static String TABLE4 = "TABLE4";
	/**
	 * ������¼
	 */
	private static String GMTABLE = "DRUGALLERGY_TABLE";
	/**
	 * Ժǰ��ҩ
	 */
	private static String TABLE6 = "TABLE6";
	/**
	 * ������ҩ
	 */
	private static String TABLE7 = "TABLE7";
	/**
	 * ��Ѫ��¼
	 */
	private static String TABLE8 = "TABLE8";
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ҽ������
	 */
	public OdiObject odiObject = new OdiObject();
	/**
	 * SQL����
	 */
	private OdiSqlObject odiSqlObject = new OdiSqlObject();
	/**
	 * Socket���ͻ�ʿվ����
	 */
	private SocketLink client1;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ����
	 */
	private String patName;
	/**
	 * ����ҩ��
	 */
	private String orgCode;
	/**
	 * ֹͣ����ע��
	 */
	private boolean stopBillFlg;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * סԺ����
	 */
	private Timestamp admDate;
	/**
	 * ��������1
	 */
	private String patName1;
	/**
	 * ��������
	 */
	private Timestamp birthDay;
	/**
	 * �����Ա�
	 */
	private String sexCode;
	/**
	 * �����ʱ�
	 */
	private String postCode;
	/**
	 * ����ͨѶ��ַ
	 */
	private String address;
	/**
	 * ���õ�λ
	 */
	private String companyDesc;
	/**
	 * ���õ绰
	 */
	private String tel;
	/**
	 * ���õ绰
	 */
	private String idNo;
	/**
	 * �����
	 */
	private String mainDiag;
	/**
	 * ���
	 */
	private String ctzCode;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ����
	 */
	private String stationCode;
	/**
	 * ҽ��վ����ע��
	 */
	private boolean saveFlg;
	/**
	 * ������ϴ���
	 */
	private String icdCode;
	/**
	 * �����������
	 */
	private String icdDesc;
	/**
	 * ��ɫ����
	 */
	Thread colorThread;
	/**
	 * Ҫ��������
	 */
	int colorRow = -1;
	/**
	 * ������TABLE����
	 */
	int colorType;
	/**
	 * ����״̬
	 */
	boolean colorRowState;
	/**
	 * ����ҩƷ��ɫ
	 */
	Color ctrlDrugClassColor = new Color(255, 0, 0);
	/**
	 * �������ײʹ��ر�� yanjing 20130908
	 */
	boolean antiFechFlg;
	/**
	 * ҽ�������Է�ҩƷ��ɫ
	 */
	Color nhiColor = new Color(128, 0, 128);
	/**
	 * ������ҩƷ��ɫ
	 */
	Color antibioticColor = new Color(255, 0, 0);
	/**
	 * ��ͨ��ɫ
	 */
	Color normalColor = new Color(0, 0, 0);
	/**
	 * ��ͨ������ɫ
	 */
	Color normalColorBJ = new Color(255, 255, 255);

	/**
	 * DCҽ��������ɫ
	 */
	Color dcColor = new Color(192, 192, 192);
	/**
	 * ��ʿ��˺����ɫ(ǳ��)
	 */
	// Color checkColor = new Color(146, 200, 255);
	Color checkColor = new Color(193, 224, 255);
	/**
	 * �л�ʿ��ע��ҽ��(�ۺ�)
	 */
	Color nsNodeColor = new Color(255, 170, 255);
	// ����ʷ�任Ϊ��ɫ
	Color red = new Color(255, 0, 0); // ======pangben modify 20110608

	Color blue = new Color(0, 0, 255); // ==============cxcx

	private boolean antflg = true;
	/**
	 * ת�����ڿ���
	 */
	boolean chagePage = true;
	/**
	 * ��һ�β���ҳ���INDEX
	 */
	int indexPage;
	/**
	 * ��ǰ�༭��
	 */
	int rowOnly;
	/**
	 * ���ñ�ע��
	 */
	boolean yyList = false;
	/**
	 * ���ע��
	 */
	String clearFlg = "N";
	/**
	 * ������¼
	 */
	public OdiDrugAllergy odiDrugArrergy;
	/**
	 * ��Ժ��ҩ����ǩ��COMBO
	 */
	private TParm rxNoTComboParm;
	/**
	 * ��ҩ��ƬCOMBO
	 */
	private TParm igRxNoTComboParm;
	/**
	 * ��ǰ����ǩ
	 */
	private String onlyRxNo;
	/**
	 * ����ע��
	 */
	private boolean oidrFlg;
	/**
	 * ������ҩ
	 */
	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;

	private boolean passFlg;
	/**
	 * ICUע��
	 */
	private boolean icuFlg;
	private boolean opeFlg = false;// ����ҽ��ע��wanglong add 20140707
	private String opDeptCode = "";// ��������wanglong add 20140707
	private String opBookSeq = "";// �������뵥��wanglong add 20140707
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	private String phaApproveFlg = "";// ����ҩƷ���������Ƿ���д�������뵥==pangben 2013-9-10
	private boolean tabSaveFlg = false;// yanjing �л�ҳǩ������
	// private String arvFlg = "";//yanjing �Ƿ�ԽȨ���

	// private ODIStationDrools odiStationDrools;
	private CDSSStationDrools odiStationDrools = new CDSSStationDosntWork();
	private boolean opeClpFlg = false;// =====pangben 2015-8-14
	// ����ҽ��վ�����ٴ�·��ʱ�̸�ֵʹ��

	private String tempPath;// add by huangjw
	private String serverPath;// add by huangjw
	// private TParm opeParm=new TParm();
	//private String dayOpeCode;//�ռ�������� add  by huangjw
	private String dayOpeFlg;
	private TParm caParm;
	private boolean BYXflg=true;

	// modified by WangQing 20170428
	/**
	 * ��ѧҽ��վ���
	 */
	private boolean tFlg = false;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// 2012/03/04�������ӹ�����START========================$$//
		TPanel tPanel2 = (TPanel) this.getComponent("TPanel2");
		TRootPanel tRootPanel = (TRootPanel) this.getComponent("tRootPanel_1");

		JScrollPane scrollPane = new JScrollPane(tRootPanel);
		scrollPane.setBounds(2, 3, 135, 1300);
		tRootPanel.setPreferredSize(new Dimension(scrollPane.getWidth(),
				scrollPane.getHeight() + 400));

		tPanel2.add(scrollPane);
		tRootPanel.revalidate();
		// $$=============add by lx
		// 2012/03/04�������ӹ�����END========================$$//
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		serverPath = "";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		initUiView();
		// ��ʼ��ҳ��
		this.initPage();
		
		//machao  start  20170504 ��Ⱦҳ�����
		Object obj = this.getParameter();	
		if(obj instanceof TParm){
			if(!StringUtil.isNullString(((TParm)obj).getValue("INF"))){
				callFunction("UI|close|setEnabled", false); 
				return;
			}			
		}
		//machao end 20170504

		
		TDS tds = odiObject.getDS("ODI_ORDER");

		// =======pangben modify 20110608 ����ʷ��ǩ�任��ɫ
		onDiagPnChange(true);

		// $$=============add by lx

		if (CDSSStationDrools.isCdssOn(Operator.getRegion())) {
			odiStationDrools = new ODIStationDrools(this);
		}
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {// add by huangjw 20151105

		} else {
			odiStationDrools.fireRules(odiObject, 2);
		}
		
		
		
	}

	/**
	 * �ɲ�������Ĳ�ѯ���� �ٴθ�ֵ����
	 */
	public void onInitReset() {
		// ��ʼ��ҳ��
		this.initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		// modified by WangQing 20170518 T_FLG
//		this.tFlg = (boolean) (((TParm) this.getParameter()).getData("ODI", "T_FLG"));	
		this.tFlg =  ((TParm) this.getParameter()).getBoolean("ODI", "T_FLG");
		System.out.println("------20170518:tFlg="+tFlg);
		
		// ֹͣ����ע��STOP_BILL_FLG
		this.setStopBillFlg(stopFlg());
		// ���þ����
		this.setCaseNo(((TParm) this.getParameter()).getData("ODI", "CASE_NO")
				.toString());
		// ���ò�����
		this.setMrNo(((TParm) this.getParameter()).getData("ODI", "MR_NO")
				.toString());
		// ���ò�������
		this.setPatName(((TParm) this.getParameter())
				.getData("ODI", "PAT_NAME").toString());
		// ����סԺ��
		this.setIpdNo(((TParm) this.getParameter()).getData("ODI", "IPD_NO")
				.toString());
		// ����ҩ������
		this.setOrgCode(((TParm) this.getParameter())
				.getData("ODI", "ORG_CODE").toString());
		// סԺ����
		this.setAdmDate((Timestamp) ((TParm) this.getParameter()).getData(
				"ODI", "ADM_DATE"));
		// ��������1
		this.setPatName1(((TParm) this.getParameter()).getValue("ODI",
				"PAT_NAME1"));
		// ��������
		this.setBirthDay((Timestamp) ((TParm) this.getParameter()).getData(
				"ODI", "BIRTH_DATE"));
		// �����Ա�
		this.setSexCode(((TParm) this.getParameter())
				.getData("ODI", "SEX_CODE").toString());
		// �����ʱ�
		this.setPostCode(((TParm) this.getParameter()).getData("ODI",
				"POST_CODE").toString());
		// ����ͨѶ��ַ
		this.setAddress(((TParm) this.getParameter()).getData("ODI", "ADDRESS")
				.toString());
		// ���õ�λ
		this.setCompanyDesc(((TParm) this.getParameter()).getData("ODI",
				"COMPANY_DESC").toString());
		// ���õ绰
		this.setTel(((TParm) this.getParameter()).getData("ODI", "TEL1")
				.toString());
		// �������֤��
		this.setIdNo(((TParm) this.getParameter()).getData("ODI", "IDNO")
				.toString());
		// ���ÿ���
		this.setDeptCode(((TParm) this.getParameter()).getData("ODI",
				"DEPT_CODE").toString());
		// ���ò���
		this.setStationCode(((TParm) this.getParameter()).getData("ODI",
				"STATION_CODE").toString());
		// ����ע��
		this.setSaveFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"SAVE_FLG"));
		// ������ϴ���
		this.setIcdCode(((TParm) this.getParameter()).getValue("ODI",
				"ICD_CODE"));
		// �����������
		this.setIcdDesc(((TParm) this.getParameter()).getValue("ODI",
				"ICD_DESC"));
		// ����ע��
		this.setOidrFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"OIDRFLG"));
		this.setDayOpeFlg(((TParm) this.getParameter()).getValue("ODI",
				"DAY_OPE_FLG"));
		// ���
		this.setCtzCode(((TParm) this.getParameter())
				.getData("ODI", "CTZ_CODE").toString());
		passIsReady = ((TParm) this.getParameter()).getBoolean("ODI", "PASS");
		enforcementFlg = ((TParm) this.getParameter()).getBoolean("ODI",
				"FORCE");
		warnFlg = ((TParm) this.getParameter()).getInt("ODI", "WARN");
		passFlg = ((TParm) this.getParameter()).getBoolean("ODI", "passflg");
		if (null != ((TParm) this.getParameter()).getValue("ODI", "OPECLP_FLG")) {// ====pangben
			// 2015-8-14
			// ����ҽ��վʱ�̸�ֵ
			opeClpFlg = ((TParm) this.getParameter()).getBoolean("ODI",
					"OPECLP_FLG");
		}
		// ICUע��
		this.setIcuFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"ICU_FLG"));
		// ����ҽ��ע��wanglong add 20140707
		this.setOpeFlg(((TParm) this.getParameter()).getBoolean("ODI",
				"OPE_FLG"));
		// ��������wanglong add 20140707
		this.setOpDeptCode(((TParm) this.getParameter()).getValue("ODI",
				"OP_DEPT_CODE"));
		// �������뵥�� wanglong add 20140707
		this.setOpBookSeq(((TParm) this.getParameter()).getValue("ODI",
				"OPBOOK_SEQ"));
		
		// ��ʼ��ҳ�����
		this.initPageComponent();
		// ��ѯ��ʼ��DataStore
		initDataStoreToTable();

		TDS tds = odiObject.getDS("ODI_ORDER");
		// ע��SYSFeePopup
		initSYSFeePopup();
		// ��ʼ��TABLE
		initTableData();

	}

	/**
	 * ��ʼ������
	 */
	public void initUiView() {
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null) {
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("DR")) {
				((TButton) this.getComponent("DR_CHECK")).setVisible(true);// ��ʾ����ҽ��ȷ��ҽ����ť
			}
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("OPE_N")) {
				TMenuItem menuItem = (TMenuItem) getComponent("close");
				menuItem.setActionMessage("onClose");// �ı�رշ�ʽ
			}
			return;
		}
		TMenuItem charge = (TMenuItem) getComponent("charge");
		charge.setVisible(false);
		TTable table = (TTable) this.getComponent(TABLE1);
		table.setLockColumns("4,12,18,19,20,21,22,23,24,25,26,27,28,29,30,31");
	}

	/**
	 * RadioButtonѡ��
	 */
	public void onRadioSel() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int type = 0;
		switch (tab.getSelectedIndex()) {
		// ��ʱ
		case 0:
			type = getRadioSelType(0);
			break;
			// ����
		case 1:
			type = getRadioSelType(1);
			break;
		}
		setQueryTime(type);
		// System.out.println("0000998765"+this.getTRadioButton("DCORDER").isSelected());
		// System.out.println("qqqqqqqqqq98765===="+type);
		if (this.getTRadioButton("DCORDER").isSelected() && type == 5)// shibl
		{
			// System.out.println("===============qqqqqqqqqq98765===="+type);
			dcOrderShow();
		}
	}

	/**
	 * ����ʱ��
	 * 
	 * @param type
	 *            int
	 */
	public void setQueryTime(int type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp startDate = null;
		Timestamp endDate = null;
		switch (type) {
		// ʹ������ʱ
		case 1:
			// ����
			startDate = StringTool.getTimestamp(StringTool.getString(sysDate,
					"yyyy/MM/dd")
					+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// ��ͣ����ʱ
		case 2:
			// ����
			startDate = this.getAdmDate();
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// ȫ����ʱ
		case 3:
			// ����
			startDate = this.getAdmDate();
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEST", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEST", endDate);
			break;
			// //ʹ���г���
		case 4:
			// ����
			startDate = StringTool.getTimestamp(StringTool.getString(sysDate,
					"yyyy/MM/dd")
					+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
			// ��ͣ�ó���
		case 5:
			// ����
			startDate = this.getAdmDate();
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
			// ȫ������
		case 6:
			// ����
			startDate = this.getAdmDate();
			// ����
			endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
					"yyyy/MM/dd HH:mm:ss");
			// ����YYYY/MM/DD
			this.setValue("START_DATEUD", startDate);
			// ����YYYY/MM/DD
			this.setValue("END_DATEUD", endDate);
			break;
		}
	}

	/**
	 * �ҵ�ѡ�е�RadionButton
	 * 
	 * @param type
	 *            int
	 * @return int
	 */
	public int getRadioSelType(int type) {
		switch (type) {
		case 0:
			if (this.getTRadioButton("YXORDER").isSelected())
				return 1;
			if (this.getTRadioButton("STOPORDER").isSelected())
				return 2;
			if (this.getTRadioButton("ALLORDER").isSelected())
				return 3;
			break;
		case 1:
			if (this.getTRadioButton("UDUTIL").isSelected()) {// shibl modify
				// 20130515
				callFunction("UI|TABLE22|setVisible", false);
				callFunction("UI|TABLE2|setVisible", true);
				return 4;
			}
			if (this.getTRadioButton("DCORDER").isSelected()) {// shibl modify
				// 20130515
				callFunction("UI|TABLE2|setVisible", false);
				callFunction("UI|TABLE22|setVisible", true);
				// dcOrderShow();
				return 5;
			}
			if (this.getTRadioButton("UDALLORDER").isSelected()) {// shibl
				// modify
				// 20130515
				callFunction("UI|TABLE22|setVisible", false);
				callFunction("UI|TABLE2|setVisible", true);
				return 6;
			}
			break;
		case 6:
			return 5;
		}
		return 0;
	}

	/**
	 * dcҽ����ʾ���� shibl modify 20130515
	 */
	public void dcOrderShow() {
		String sline = StringTool.getString((Timestamp) this
				.getValue("START_DATEUD"), "yyyyMMdd HHmmss");
		String eline = StringTool.getString((Timestamp) this
				.getValue("END_DATEUD"), "yyyyMMdd HHmmss");
		String nline = StringTool.getString((Timestamp) SystemTool
				.getInstance().getDate(), "yyyyMMdd HHmmss");
		String sql = "SELECT A.*, A.ORDER_DESC||A.GOODS_DESC||A.SPECIFICATION AS ORDER_DESCCHN,A.EFF_DATE AS EFF_DATEDAY FROM ODI_ORDER A "
				+ " WHERE A.CASE_NO='"
				+ this.caseNo
				+ "' AND A.DC_DATE IS NOT NULL AND "
				+ " A.DC_DATE <= TO_DATE('"
				+ nline
				+ "','YYYYMMDD HH24MISS') AND "
				+ // yanjing 20131212 modify ͣ��ʱ���뵱ǰʱ��Ƚ�
				"A.ORDER_DATE>=TO_DATE('"
				+ sline
				+ "','YYYYMMDD HH24MISS') AND A.ORDER_DATE<=TO_DATE('"
				+ eline
				+ "','YYYYMMDD HH24MISS')"
				+ " AND A.RX_KIND='UD' AND A.HIDE_FLG='N'";
		TTable table = this.getTTable("TABLE22");
		// System.out.println("parm����:::;PANGBEN:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("parm:::;PANGBEN:::"+parm);
		if (parm.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		int rowCount = parm.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			// System.out.println("rrrrrrrowCount is::"+rowCount);
			TParm action = parm.getRow(i);
			// System.out.println("------++++++action is :::"+action);
			// ================= chenxi ҩƷ��ʾ��Ϣ
			String orderCode = action.getValue("ORDER_CODE");
			String sql1 = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "' ";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql1));
			sqlparm = sqlparm.getRow(0);
			// System.out.println("===========sqlparm========"+sqlparm);
			// ============ chenxi

			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(action.getValue("INSPAY_TYPE"))) {
					table.setRowTextColor(i, nhiColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(action.getValue("CAT1_TYPE"))) {
				// �Ƿ��ǹ���ҩƷ
				if (action.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					table.setRowTextColor(i, ctrlDrugClassColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// �жϿ�����
			// System.out.println("898989action is::"+action);
			if (action.getValue("ANTIBIOTIC_CODE").length() != 0) {// =====pangben
				// 2013-11-14
				// ͣ��ҽ��У�鿹��ҩƷ������ɫ
				// System.out.println("======++++=====");
				table.setRowTextColor(i, antibioticColor);
			} else {
				if (sqlparm.getValue("DRUG_NOTES_DR").length() != 0) {// ===========cxcx
					table.setRowTextColor(i, blue);
					// continue;
				} else {
					table.setRowTextColor(i, normalColor);
				}
			}
			// �Ƿ���ͣ��ҽ��
			// ȡ��ϵͳ��ͣ��ʱ��Ƚ�,ͣ��ʱ��С�ڵ���ϵͳʱ��ʱ,�������óɻ�ɫ
			// ȡ��ϵͳʱ��
			String sysdate = SystemTool.getInstance().getDate().toString();
			String dc_date = action.getValue("DC_DATE");
			if (action.getValue("DC_DR_CODE").length() != 0
					&& sysdate.compareTo(dc_date) >= 0) {
				// this.messageBox("2222222222222222");
				table.setRowColor(i, dcColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}

			// �Ƿ��л�ʿ��עNS_NOTE
			if (action.getValue("NS_NOTE").length() != 0) {
				table.setRowColor(i, nsNodeColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
			// ��ʿ�Ƿ����
			if (action.getValue("NS_CHECK_CODE").length() != 0) {
				table.setRowColor(i, checkColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
		}
		this.getTTable("TABLE22").setParmValue(parm);
	}

	/**
	 * �õ�RadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * ��ʼ��TABLE����
	 */
	public void initTableData() {
		if (this.isStopBillFlg())
			return;
		// ��ʼ��
		TTable table = this.getTTable(TABLE1);
		table.getTable().grabFocus();
		table.setSelectedRow(rowOnly);
		table.setSelectedColumn(2);
	}

	/**
	 * ��ʼ����Ժ��ҩCOMBO
	 */
	public void initDSRxNoCombo() {
		this.getTComboBox("RX_NO").setParmMap(
				"id:ID;name:NAME;text:TEXT;value:ACTIVE;py1:PRESRT_NO");
		rxNoTComboParm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT DISTINCT RX_NO AS ID,'����' AS NAME,'��'||PRESRT_NO||'��' AS TEXT,'N' AS ACTIVE,PRESRT_NO FROM ODI_ORDER WHERE RX_KIND='DS' AND CASE_NO='"
								+ this.getCaseNo() + "' ORDER BY ID"));
		this.getTComboBox("RX_NO").setParmValue(rxNoTComboParm);
	}

	/**
	 * ��ʼ����ҩ��ƬCOMBO
	 */
	public void initIGRxNoCombo() {
		this.getTComboBox("IG_RX_NO").setParmMap(
				"id:ID;name:NAME;text:TEXT;value:ACTIVE;py1:PRESRT_NO");
		igRxNoTComboParm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT DISTINCT RX_NO AS ID,'����' AS NAME,'��'||PRESRT_NO||'��' AS TEXT,'N' AS ACTIVE,PRESRT_NO FROM ODI_ORDER WHERE RX_KIND='IG' AND CASE_NO='"
								+ this.getCaseNo() + "' ORDER BY ID"));
		this.getTComboBox("IG_RX_NO").setParmValue(igRxNoTComboParm);
	}

	/**
	 * �õ�COMBO
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * ��ʼ��ҳ�����
	 */
	public void initPageComponent() {
		// ���ø�Ⱦ��� -start machao 20170615
		TParm p = new TParm(TJDODBTool.getInstance().select("SELECT INF_FLG FROM ADM_INP WHERE CASE_NO='"+this.getCaseNo()+"'"));
		if(!StringUtil.isNullString(p.getData("INF_FLG", 0)+"") &&
				"Y".equals(p.getData("INF_FLG", 0))){
			this.setValue("INF_FLG", "Y");
		}
		//-end machao 20170615
		// ������ʱҽ����ҩ��
		this.setValue("DEPT_CODEST", this.getOrgCode());
		// ���ó���ҽ����ҩ��
		this.setValue("DEPT_CODEUD", this.getOrgCode());
		// ���ó�Ժ��ҩҽ����ҩ��
		this.setValue("DEPT_CODEDS", this.getOrgCode());
		// ������ҩ��Ƭҽ����ҩ��
		this.setValue("DEPT_CODEIG", this.getOrgCode());
		// ���ó�Ժ��ҩ����ǩ
		initDSRxNoCombo();
		// ��ʼ����ҩ��ƬCOMBO
		initIGRxNoCombo();
		// ����
		Timestamp startDate = StringTool.getTimestamp(StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd")
				+ " 00:00:00", "yyyy/MM/dd HH:mm:ss");
		// ����
		Timestamp endDate = StringTool.getTimestamp("9999/12/31 23:59:59",
				"yyyy/MM/dd HH:mm:ss");
		// ����YYYY/MM/DD
		this.setValue("START_DATEST", startDate);
		// ����HH:mm:ss
		// this.setValue("START_MISSST",startDate);
		// ����YYYY/MM/DD
		this.setValue("END_DATEST", endDate);
		// ����HH:mm:ss
		// this.setValue("END_MISSST",endDate);
		// ����״̬
		((TComboBox) this.getComponent("KLSTAR")).setSelectedIndex(0);
		// ����YYYY/MM/DD
		this.setValue("START_DATEUD", this.getAdmDate());
		// ����HH:mm:ss
		// this.setValue("START_MISSUD",startDate);
		// ����YYYY/MM/DD
		this.setValue("END_DATEUD", endDate);
		// ����HH:mm:ss
		// this.setValue("END_MISSUD",endDate);
		// ����״̬
		((TComboBox) this.getComponent("KLSTARUD")).setSelectedIndex(0);
		// ����״̬
		((TComboBox) this.getComponent("KLSTARDS")).setSelectedIndex(0);
		// ��ҩ���ֳ�ʼ��
		// ��/��
		this.setValue("RF", odiObject.getAttribute(odiObject.DCT_TAKE_DAYS));
		// ��Ƭ����
		this.setValue("YPJL", odiObject.getAttribute(odiObject.DCT_TAKE_QTY));
		// ��ҩĬ��Ƶ��
		this.setValue("IGFREQCODE", odiObject
				.getAttribute(odiObject.G_FREQ_CODE));
		// ��ҩĬ���÷�
		this.setValue("IG_ROUTE", odiObject
				.getAttribute(odiObject.G_ROUTE_CODE));
		// ��ҩ��ҩ��ʽ
		this.setValue("IG_DCTAGENT", odiObject
				.getAttribute(odiObject.G_DCTAGENT_CODE));
		// ��ססԺ��ҽTABLE
		this.getTTable(TABLE4).getTable().getTableHeader()
		.setReorderingAllowed(false);
		// =================����ҽ�� begin wanglong add 20140707
		if (isOpeFlg()) {
			TLabel label = (TLabel) this.getComponent("tLabel_9");
			label.setText("ִ�п���");// ���ġ���ҩ���ҡ�Ϊ��ִ�п��ҡ�
			label.setZhText("ִ�п���");
			TComboBox deptCodeST = (TComboBox) this.getComponent("DEPT_CODEST");
			String deptSql = "SELECT DEPT_CODE ID, DEPT_CHN_DESC NAME, DEPT_ENG_DESC ENNAME, PY1, PY2 "
					+ " FROM SYS_DEPT WHERE OP_FLG = 'Y' ORDER BY DEPT_CODE";
			TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
			if (deptParm.getErrCode() < 0) {
				this.messageBox_("ȡ��ִ�п�����Ϣʧ��");
				return;
			}
			deptCodeST.setParmValue(deptParm);// ��ִ�п��ҡ�ֻ�г���������
			deptCodeST.setParmMap("id:ID;name:NAME");
			deptCodeST.setShowID(true);
			deptCodeST.setShowName(true);
			deptCodeST.setTableShowList("name");


			//modify by yangjj 20161230 #4648
			//if (!getOpDeptCode().equals("")
			//&& deptParm.getValue("ID").contains(getOpDeptCode())) {
			//deptCodeST.setSelectedID(getOpDeptCode());
			//} else {
			deptCodeST.setSelectedID(Operator.getDept());
			//}
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(1,
					false);
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(2,
					false);
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(3,
					false);
			// ����ҽ��վ����Ժǰ��ҩ
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(5,
					false);
			// ����ҽ��վ����������ҩ
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(6,
					false);
			// ����ҽ��վ������Ѫ��¼
			((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(7,
					false);
			if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
					&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
					.equals("OPE_N")) {
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(5,
						false);
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(6,
						false);
				((TTabbedPane) this.getComponent("TABLEPANE")).setEnabledAt(7,
						false);
			}
			TPanel panel_2 = (TPanel) getComponent("tPanel_2");
			Component[] component_2 = panel_2.getComponents();
			for (Component component2 : component_2) {
				component2.setEnabled(false);// ���õڶ���ҳǩ�е��������
			}
			getTTable("TABLE2").setLockColumns("all");
			TPanel panel_3 = (TPanel) getComponent("tPanel_3");
			Component[] component_3 = panel_3.getComponents();
			for (Component component3 : component_3) {
				component3.setEnabled(false);// ���õ�����ҳǩ�е��������
			}
			getTTable("TABLE3").setLockColumns("all");
			TPanel panel_4 = (TPanel) getComponent("tPanel_4");
			Component[] component_4 = panel_4.getComponents();
			for (Component component4 : component_4) {
				component4.setEnabled(false);// ���õ��ĸ�ҳǩ�е��������
			}
			getTTable("TABLE4").setLockColumns("all");
		} else {
			callFunction("UI|medApplyNo|setVisible", false);// ��ʾ
		}
		// =================����ҽ�� end
	}

	/**
	 * ע��SYSFeePopup�¼�
	 */
	public void initSYSFeePopup() {
		// TABLE1˫���¼�
		callFunction("UI|" + TABLE1 + "|addEventListener", TABLE1 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE2˫���¼�
		callFunction("UI|" + TABLE2 + "|addEventListener", TABLE2 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE3˫���¼�
		callFunction("UI|" + TABLE3 + "|addEventListener", TABLE3 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE4˫���¼�
		callFunction("UI|" + TABLE4 + "|addEventListener", TABLE4 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// TABLE4˫���¼�
		callFunction("UI|" + GMTABLE + "|addEventListener", GMTABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// Ժǰ��ҩTABLE6�����¼�
		callFunction("UI|" + TABLE6 + "|addEventListener", TABLE6 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ������ҩTABLE7�����¼�
		callFunction("UI|" + TABLE7 + "|addEventListener", TABLE7 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ��Ѫ��¼TABLE8�����¼�
		callFunction("UI|" + TABLE8 + "|addEventListener", TABLE8 + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// ��ʱTABLE1�����¼�
		getTTable(TABLE1).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentST");
		// ���ڼ����¼�
		getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentUD");
		// ��Ժ��ҩ�����¼�
		getTTable(TABLE3).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentDS");
		// ��ҩ��Ƭ�����¼�
		getTTable(TABLE4).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentIG");
		// ��ҩ��Ƭ�����¼�
		getTTable(GMTABLE).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentGM");
		// Ժǰ��ҩ�����¼�
		getTTable(TABLE6).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onCreateEditComoponentPA");

		// ��ʱTABLEֵ�ı����
		addEventListener(TABLE1 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueST");
		// ����TABLEֵ�ı����
		addEventListener(TABLE2 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueUD");
		// ��Ժ��ҩTABLEֵ�ı����
		addEventListener(TABLE3 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueDS");
		// ��ҩ��ƬTABLEֵ�ı����
		addEventListener(TABLE4 + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValueIG");
		// ��ʱҽ��CHECK_BOX�����¼�
		getTTable(TABLE1).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueST");
		// ����ҽ��CHECK_BOX�����¼�
		getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueUD");
		// ��Ժ��ҩҽ��CHECK_BOX�����¼�
		getTTable(TABLE3).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValueDS");
		// �������
		addListener(getTTable("TABLE22"));
		addListener(getTTable("TABLE7"));
	}

	/**
	 * ����ѡ���¼�SYSPOU
	 * 
	 * @param obj
	 *            Object
	 */
	public void onComboxSelect(Object obj) {
		int tableType = StringTool.getInt("" + obj);
		switch (tableType) {
		case 0:
			this.getTTable(TABLE1).acceptText();
			break;
		case 1:
			this.getTTable(TABLE2).acceptText();
			break;
		case 2:
			this.getTTable(TABLE3).acceptText();
			break;
		}
	}

	/**
	 * ����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// this.messageBox("======onTableClicked======");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");

		/**
		 * if (tab.getSelectedIndex() == 0) { TTable table=getTTable(TABLE1);
		 * //xueyf begin if(row==table.getSelectedRow() &&
		 * 2==table.getSelectedColumn()){ TParm parm=table.getShowParmValue();
		 * if(parm.getValue("ORDER_DESCCHN",row).startsWith("*")){
		 * table.setLockCell(table.getSelectedRow(),2,true);
		 * table.getDataStore().setActive(row, false); if(!table.isLockCell(row,
		 * 2)){ table.getDataStore().setActive(row, false); table.acceptText();
		 * } table.getDataStore().setActive(row, true); //return ; }else{
		 * table.setLockCell(table.getSelectedRow(),2,false); } } //xueyf end }
		 **/

		if (tab.getSelectedIndex() == 4) {
			TParm parm = this.getTTable(GMTABLE).getDataStore().getRowParm(
					this.getTTable(GMTABLE).getSelectedRow());
			callFunction("UI|setSysStatus", parm.getValue("DRUGORINGRD_CODE")
					+ ":" + parm.getValue("ORDER_DESC"));
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getRowParm(row, ds.PRIMARY);
		if (parm.getValue("ORDER_CODE").length() == 0)
			return;

		// ===============chenxi ҽ����ʾ����start
		String orderCode = parm.getValue("ORDER_CODE");
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// System.out.println("=====sqlparm======"+sqlparm);
		// ״̬����ʾ
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR")); // chexi modified
		// DRUG_NOTES_DR
		// ================= chenxi modify ҽ����ʾ����2012.06.06 f
		// ״̬����ʾ
		/**
		 * callFunction( "UI|setSysStatus", parm.getValue("ORDER_CODE") + " " +
		 * parm.getValue("ORDER_DESC") + " " + parm.getValue("GOODS_DESC") + " "
		 * + parm.getValue("DESCRIPTION") + " " + parm.getValue("SPECIFICATION")
		 * + " " + parm.getValue("REMARK_1") + " " + parm.getValue("REMARK_2"));
		 **/
	}

	/**
	 * ��ʱҽ��CHECK_BOX�����¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValueST(Object obj) {
		// this.messageBox("onCheckBoxValueST====");
		//TODO �޸���ʱҽ��checkbox�����¼�
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE1).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// System.out.println("LINKMAIN_FLG Y====");
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// �뿪��ҽ��
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// ��ѯ��������
				int maxLinkNo = getMaxLinkNo("ST");
				if (opeFlg) {
					maxLinkNo = getMaxLinkNo("OP");
				}
				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 �����Զ�һ�� start==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//==================add by chenhj start
				linkP.setData("INFLUTION_RATE",linkParm.getValue("INFLUTION_RATE"));
				//==================add by chenhj end

				// =================add by huangjw
				if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
						&& ((TParm) this.getParameter()).getData("ODI",
								"OPERATOR").equals("OPE_N")) {
					linkP.setData("NS_CHECK_DATE", linkParm
							.getTimestamp("NS_CHECK_DATE"));
					linkP.setData("ORDER_DATE", linkParm
							.getTimestamp("ORDER_DATE"));
					linkP.setData("ORDER_DR_CODE", linkParm
							.getValue("ORDER_DR_CODE"));
				}

				// =================add by huangjw
				// $$========add by lx 2012-06-13 end==========$$//

				// $$==============Del by lx 2012/02/23 Start
				// �ҵ���ҽ����Ĭ������
				/**
				 * TParm actionParm = this.getPhaBaseData(
				 * 
				 * linkParm.getData("ORDER_CODE").toString(), linkParm
				 * .getData("CAT1_TYPE").toString(), "ST", linkParm);
				 * linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				 * linkP.setData("ROUTE_CODE",
				 * actionParm.getValue("ROUTE_CODE"));
				 **/
				// $$==============Del by lx 2012/02/23 end================$$//
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				for (int i = row; i < ds.rowCount(); i++) {
					// this.messageBox("���ñ��");
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (ds.getItemString(i, "SETMAIN_FLG").equals("Y"))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeST(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					table.setLockCellRow(i, true);
					table.setLockCell(i, 0, false);// ������
					table.setLockCell(i, 1, false);// ���
					table.setLockCell(i, 2, false);// ҽ��
					table.setLockCell(i, 3, false);// ����
					//===========================add by wukai on 20160527 start
					table.setLockCell(i, 6, false);//���뷽ʽ
					table.setLockCell(i, 7, false);//��Һ����
					//===========================add by wukai on 20160527 end
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						table.setLockCell(i, 5, false);// �÷�
						table.setLockCell(i, 8, false);// Ƶ��
						table.setLockCell(i, 9, false);// ִ�п���
						table.setLockCell(i, 10, false);// ����ʱ��

						// ===================================
						if (((TParm) this.getParameter()).getData("ODI",
								"OPERATOR") != null
								&& ((TParm) this.getParameter()).getData("ODI",
										"OPERATOR").equals("OPE_N")) {
							table.setLockCell(i, 22, false);
							table.setLockCell(i, 23, false);
							table.setLockCell(i, 25, false);
						}
						// ===================================
					}
					table.setLockCell(i, 11, false);// ҽ����ע
					table.setLockCell(i, 13, false);// ������ʶ
					table.setLockCell(i, 14, false);// ��
					table.setLockCell(i, 15, false);// ��
					table.setLockCell(i, 16, false);// ��
					table.setLockCell(i, 17, false);// ��
					table.setDSValue(i);
				}

				// shibl 20121228 modify start �����������Ӻ�ҽ����ͬ����ʱ��
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm temp = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					if (temp.getInt("LINK_NO") == maxLinkNo) {
						temp.setData("EFF_DATE", linkParm
								.getTimestamp("EFF_DATE"));
						temp.setData("EFF_DATEDAY", linkParm
								.getTimestamp("EFF_DATE"));
						// ===================================add by huangjw
						if (((TParm) this.getParameter()).getData("ODI",
								"OPERATOR") != null
								&& ((TParm) this.getParameter()).getData("ODI",
										"OPERATOR").equals("OPE_N")) {
							temp.setData("NS_CHECK_DATE", linkParm
									.getTimestamp("NS_CHECK_DATE"));
							temp.setData("ORDER_DR_CODE", linkParm
									.getValue("ORDER_DR_CODE"));
							temp.setData("ORDER_DATE", linkParm
									.getTimestamp("ORDER_DATE"));
						}

						// ===================================add by huangjw
						// ����ҽ��
						if (this.isOrderSet(temp)) {
							int groupNo = temp.getInt("ORDERSET_GROUP_NO");
							Object objT = odiObject
									.getAttribute(odiObject.OID_DSPN_TIME);
							TDS dsT = odiObject.getDS("ODI_ORDER");
							String buffT = dsT.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							// �¼ӵ�����
							int newRowT[] = dsT.getNewRows(buffT);
							for (int j : newRowT) {
								TParm tempT = dsT.getRowParm(j, buffT);
								if (!dsT.isActive(j, buffT))
									continue;
								if (tempT.getInt("ORDERSET_GROUP_NO") == groupNo) {
									tempT.setData("EFF_DATE", linkParm
											.getTimestamp("EFF_DATE"));
									tempT.setData("EFF_DATEDAY", linkParm
											.getTimestamp("EFF_DATE"));
									// ===================================add by
									// huangjw
									if (((TParm) this.getParameter()).getData(
											"ODI", "OPERATOR") != null
											&& ((TParm) this.getParameter())
											.getData("ODI", "OPERATOR")
											.equals("OPE_N")) {
										tempT.setData("NS_CHECK_DATE", linkParm
												.getTimestamp("NS_CHECK_DATE"));
										tempT.setData("ORDER_DR_CODE", linkParm
												.getValue("ORDER_DR_CODE"));
										tempT.setData("ORDER_DATE", linkParm
												.getTimestamp("ORDER_DATE"));
									}
									// ===================================add by
									// huangjw
									odiObject.setItem(dsT, j, tempT, buffT);
									this.getTTable(TABLE1).setDSValue(i);
								}
							}
						} else {
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
						// shibl 20121228 modify end
					}
				}
			} else {
				// this.messageBox("LINKMAIN_FLG N====");
				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// �ҵ���ҽ����Ĭ������
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "ST", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));

				// odiObject.setItem(ds, row, linkP);
				// table.setDSValue(row);
				TParm linkBlankP = new TParm();
				linkBlankP.setData("LINK_NO", "");
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");
				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					for (int i = row; i < ds.rowCount(); i++) {
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							// this.messageBox("i"+i);
							if (i > row)
								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeST(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE1, ""));
							table.setLockCellRow(i, true);
							table.setLockCell(i, 0, false);// ������
							table.setLockCell(i, 1, false);// ���
							table.setLockCell(i, 2, false);// ҽ��
							table.setLockCell(i, 3, false);// ����
							table.setLockCell(i, 5, false);// �÷�
							//===================add by wukai on 20160527 start
							table.setLockCell(i, 6, false);// ���뷽ʽ
							table.setLockCell(i, 7, false);// ��Һ����
							//===================add by wukai on 20160527 end
							table.setLockCell(i, 8, false);// Ƶ��
							table.setLockCell(i, 9, false);// ִ�п���
							table.setLockCell(i, 10, false);// ����ʱ��
							table.setLockCell(i, 11, false);// ҽ����ע
							table.setLockCell(i, 13, false);// ������ʶ
							table.setLockCell(i, 14, false);// ��
							table.setLockCell(i, 15, false);// ��
							table.setLockCell(i, 16, false);// ��
							table.setLockCell(i, 17, false);// ��
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
			}
		}
		// ����
		if ("CONTINUOUS_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("CONTINUOUS_FLG"))) {
				if (isCheckOrderContinuousFlg(linkParm, 0)) {
					// ��������
					this.messageBox("E0154");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("CONTINUOUS_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					linkParm.setData("CONTINUOUS_FLG", "Y");
					linkParm.setData("#NEW#", true);
					TParm orderParmCon = this.getTempStartQty(linkParm);
					odiObject.setItem(ds, row, orderParmCon);
					table.setDSValue(row);
				}
			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				linkParm.setData("CONTINUOUS_FLG", "N");
				linkParm.setData("#NEW#", true);
				TParm orderParmCon = this.getTempStartQty(linkParm);
				odiObject.setItem(ds, row, orderParmCon);
				table.setDSValue(row);
			}
		}
	}

	/**
	 * ����ҽ��CHECK_BOX�����¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValueUD(Object obj) {
		//TODO CHECK_BOX�����¼�
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		// System.out.println("000000000000000000linkParm linkParm is :::"+linkParm);
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// �뿪��ҽ��
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// ��ѯ��������
				int maxLinkNo = getMaxLinkNo("UD");

				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 start �����Զ�һ��==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//====add by chenhj 2017.4.10 start
				linkP.setData("INFLUTION_RATE", linkParm.getValue("INFLUTION_RATE"));
				//====add by chenhj 2017.4.10 
				// ======yanjing 20140213 ����ҩ�������Զ�Ϊͣ��ʱ�䡢������ʶ��ͣ��ҽ����ֵ
				if (!"".equals(linkParm.getValue("ANTIBIOTIC_WAY"))
						&& !linkParm.getValue("ANTIBIOTIC_WAY").equals(null)) {
					linkP.setData("DC_DATE", linkParm.getTimestamp("DC_DATE"));
					linkP.setData("ANTIBIOTIC_WAY", linkParm
							.getValue("ANTIBIOTIC_WAY"));
					linkP
					.setData("DC_DR_CODE", linkParm
							.getValue("DC_DR_CODE"));
					linkP.setData("DC_DEPT_CODE", linkParm
							.getValue("DC_DEPT_CODE"));// =====yanjing 20140901
					// ͣ�ÿ���
				}

				// $$========add by lx 2012-06-13 end==========$$//
				// �ҵ���ҽ����Ĭ������
				// $$==============Del by lx 2012/02/23 Start
				// ��ʱ��ȡ��¼================$$//
				/**
				 * TParm actionParm = this.getPhaBaseData(
				 * linkParm.getData("ORDER_CODE").toString(), linkParm
				 * .getData("CAT1_TYPE").toString(), "UD", linkParm);
				 * linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				 * linkP.setData("ROUTE_CODE",
				 * actionParm.getValue("ROUTE_CODE"));
				 **/
				// $$==============Del by lx 2012/02/23 end================$$//
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				// $$==========add by lx 2012/02/23 ѡ����
				// ��START===============$$//
				// this.messageBox("table.getDataStore().rowCount()"+table.getDataStore().rowCount());
				for (int i = row; i < ds.rowCount(); i++) {
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (!ds.getItemString(i, "ORDER_CODE").equals("")
							&& (ds.getItemString(i, "SETMAIN_FLG").equals("Y") || !ds
									.getItemString(i, "CAT1_TYPE")
									.equals("PHA")))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeUD(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// ������
					this.getTTable(TABLE2).setLockCell(i, 1, false);// ���
					this.getTTable(TABLE2).setLockCell(i, 2, false);// ҽ��
					this.getTTable(TABLE2).setLockCell(i, 3, false);// ����
					//&&========================add by wukai 20160525 start
					this.getTTable(TABLE2).setLockCell(i, 7, false);//���뷽ʽ
					this.getTTable(TABLE2).setLockCell(i, 8, false);//��Һ����
					//&&========================add by wukai 20160525 start
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						this.getTTable(TABLE2).setLockCell(i, 5, false);// Ƶ��
						this.getTTable(TABLE2).setLockCell(i, 6, false);// �÷�
						this.getTTable(TABLE2).setLockCell(i, 12, false);// ִ�п���
						this.getTTable(TABLE2).setLockCell(i, 13, false);// ����ʱ��
						this.getTTable(TABLE2).setLockCell(i, 14, false);// ͣ��ʱ��

					}
					this.getTTable(TABLE2).setLockCell(i, 9, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 10, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 11, false);// ҽ����ע
					this.getTTable(TABLE2).setLockCell(i, 13, false);// ͣ��ʱ��
					this.getTTable(TABLE2).setLockCell(i, 16, false);// ����
					table.setDSValue(i);
				}
				// $$==========add by lx 2012/02/23 ѡ���� ��END===============$$//

			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// �ҵ���ҽ����Ĭ������
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "UD", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
				// odiObject.setItem(ds, row, linkP);
				// table.setDSValue(row);
				// $$==========add by lx 2012/02/23 ѡ����
				// ��START===============$$//
				// TParm linkBlankP = new TParm();
				// linkBlankP.setData("LINK_NO", "");
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");

				// this.messageBox("LINK_NO===="+ds.getItemInt(row, "LINK_NO"));
				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					// this.messageBox("LINK_NO===="+ds.getItemInt(row,
					// "LINK_NO"));

					for (int i = 0; i < ds.rowCount(); i++) {
						TParm linkBlankP = new TParm();
						linkBlankP.setData("LINK_NO", "");
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							// this.messageBox("i"+i)
							if (i > row)

								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeUD(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE2, ""));
							// ȡ������ʱ��������ֶε�ֵ yanjing 20140217
							this.onClearLinkAnti(ds.getItemString(i,
									"ORDER_CODE"), linkBlankP);
							this.getTTable(TABLE2).setLockCellRow(i, true);
							this.getTTable(TABLE2).setLockCell(i, 0, false);// ������
							this.getTTable(TABLE2).setLockCell(i, 1, false);// ���
							this.getTTable(TABLE2).setLockCell(i, 2, false);// ҽ��
							this.getTTable(TABLE2).setLockCell(i, 3, false);// ����
							this.getTTable(TABLE2).setLockCell(i, 5, false);// Ƶ��
							this.getTTable(TABLE2).setLockCell(i, 6, false);// �÷�
							//&&========================add by wukai 20160525 start
							this.getTTable(TABLE2).setLockCell(i, 7, false);//���뷽ʽ
							this.getTTable(TABLE2).setLockCell(i, 8, false);//��Һ����
							//&&========================add by wukai 20160525 start
							this.getTTable(TABLE2).setLockCell(i, 9, false);// ��
							this.getTTable(TABLE2).setLockCell(i, 10, false);// ��
							this.getTTable(TABLE2).setLockCell(i, 11, false);// ҽ����ע
							this.getTTable(TABLE2).setLockCell(i, 12, false);// ִ�п���
							this.getTTable(TABLE2).setLockCell(i, 13, false);// ����ʱ��
							this.getTTable(TABLE2).setLockCell(i, 14, false);// ͣ��ʱ��
							this.getTTable(TABLE2).setLockCell(i, 16, false);// ����
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
				// $$==========add by lx 2012/02/23 ѡ���� ��END===============$$//
			}
		}
	}

	/**
	 * ��Ժ��ҩ
	 */
	public void onCheckBoxValueDS(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE3).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		// ����ҽ��
		//		if ("LINKMAIN_FLG".equals(columnName)) {
		//			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
		//				if (linkParm.getValue("ORDER_CODE").length() == 0) {
		//					// �뿪��ҽ��
		//					this.messageBox("E0152");
		//					TDS ds = odiObject.getDS("ODI_ORDER");
		//					TParm linkFlg = new TParm();
		//					linkFlg.setData("LINKMAIN_FLG", "N");
		//					odiObject.setItem(ds, row, linkFlg);
		//					table.setDSValue(row);
		//					return;
		//				}
		//				// ��ѯ��������
		//				int maxLinkNo = getMaxLinkNo("DS");
		//				TDS ds = odiObject.getDS("ODI_ORDER");
		//				// ������������
		//				TParm linkP = new TParm();
		//				linkP.setData("LINK_NO", maxLinkNo);
		//				// �ҵ���ҽ����Ĭ������
		//				TParm actionParm = this.getPhaBaseData(linkParm.getData(
		//						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
		//						.toString(), "DS", linkParm);
		//				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
		//				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
		//				odiObject.setItem(ds, row, linkP);
		//				table.setDSValue(row);
		//			} else {
		//				TDS ds = odiObject.getDS("ODI_ORDER");
		//				// ������������
		//				TParm linkP = new TParm();
		//				linkP.setData("LINK_NO", "");
		//				// �ҵ���ҽ����Ĭ������
		//				TParm actionParm = this.getPhaBaseData(linkParm.getData(
		//						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
		//						.toString(), "DS", linkParm);
		//				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
		//				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
		//				odiObject.setItem(ds, row, linkP);
		//				table.setDSValue(row);
		//			}
		//	}
		//========== add by chenhj start 2017.4.10
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				if (linkParm.getValue("ORDER_CODE").length() == 0) {
					// �뿪��ҽ��
					this.messageBox("E0152");
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "N");
					odiObject.setItem(ds, row, linkFlg);
					table.setDSValue(row);
					return;
				}
				// ��ѯ��������
				int maxLinkNo = getMaxLinkNo("DS");
				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", maxLinkNo);
				// $$========add by lx 2012-06-13 start �����Զ�һ��==========$$//
				linkP.setData("FREQ_CODE", linkParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", linkParm.getValue("ROUTE_CODE"));
				linkP.setData("EFF_DATE", linkParm.getTimestamp("EFF_DATE"));
				//====add by chenhj 2017.4.10 start
				linkP.setData("INFLUTION_RATE", linkParm.getValue("INFLUTION_RATE"));
				//====add by chenhj 2017.4.10 
				if (!"".equals(linkParm.getValue("ANTIBIOTIC_WAY"))
						&& !linkParm.getValue("ANTIBIOTIC_WAY").equals(null)) {
					linkP.setData("DC_DATE", linkParm.getTimestamp("DC_DATE"));
					linkP.setData("ANTIBIOTIC_WAY", linkParm
							.getValue("ANTIBIOTIC_WAY"));
					linkP
					.setData("DC_DR_CODE", linkParm
							.getValue("DC_DR_CODE"));
					linkP.setData("DC_DEPT_CODE", linkParm
							.getValue("DC_DEPT_CODE"));
					// ͣ�ÿ���
				}
				odiObject.setItem(ds, row, linkP);
				table.setDSValue(row);
				for (int i = row; i < ds.rowCount(); i++) {
					if (i > row) {
						linkP.setData("LINKMAIN_FLG", "N");
					}
					if (!ds.getItemString(i, "ORDER_CODE").equals("")
							&& (ds.getItemString(i, "SETMAIN_FLG").equals("Y") || !ds
									.getItemString(i, "CAT1_TYPE")
									.equals("PHA")))
						continue;
					linkP
					.setData("EXEC_DEPT_CODE", this.getExeDeptCodeUD(ds
							.getItemString(row, "ORDER_CODE"), row,
							TABLE1, ""));
					odiObject.setItem(ds, i, linkP);
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// ������
					this.getTTable(TABLE2).setLockCell(i, 1, false);// ���
					this.getTTable(TABLE2).setLockCell(i, 2, false);// ҽ��
					this.getTTable(TABLE2).setLockCell(i, 3, false);// ����
					this.getTTable(TABLE2).setLockCell(i, 7, false);//���뷽ʽ
					this.getTTable(TABLE2).setLockCell(i, 8, false);//��Һ����
					if (ds.getItemString(i, "LINK_NO").equals("")
							|| ds.getItemString(i, "LINKMAIN_FLG").equals("Y")) {
						this.getTTable(TABLE2).setLockCell(i, 5, false);// Ƶ��
						this.getTTable(TABLE2).setLockCell(i, 6, false);// �÷�
						this.getTTable(TABLE2).setLockCell(i, 12, false);// ִ�п���
						this.getTTable(TABLE2).setLockCell(i, 13, false);// ����ʱ��
						this.getTTable(TABLE2).setLockCell(i, 14, false);// ͣ��ʱ��

					}
					this.getTTable(TABLE2).setLockCell(i, 9, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 10, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 11, false);// ҽ����ע
					this.getTTable(TABLE2).setLockCell(i, 13, false);// ͣ��ʱ��
					this.getTTable(TABLE2).setLockCell(i, 16, false);// ����
					table.setDSValue(i);
				}
			} else {
				TDS ds = odiObject.getDS("ODI_ORDER");
				// ������������
				TParm linkP = new TParm();
				linkP.setData("LINK_NO", "");
				// �ҵ���ҽ����Ĭ������
				TParm actionParm = this.getPhaBaseData(linkParm.getData(
						"ORDER_CODE").toString(), linkParm.getData("CAT1_TYPE")
						.toString(), "UD", linkParm);
				linkP.setData("FREQ_CODE", actionParm.getValue("FREQ_CODE"));
				linkP.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
				TParm linkMainP = new TParm();
				linkMainP.setData("LINKMAIN_FLG", "N");

				if (ds.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = ds.getItemInt(row, "LINK_NO");
					for (int i = 0; i < ds.rowCount(); i++) {
						TParm linkBlankP = new TParm();
						linkBlankP.setData("LINK_NO", "");
						if (linktemp == ds.getItemInt(i, "LINK_NO")) {
							if (i > row)
								linkBlankP.setData("EXEC_DEPT_CODE", this
										.getExeDeptCodeUD(ds.getItemString(i,
												"ORDER_CODE"), i, TABLE2, ""));
							this.onClearLinkAnti(ds.getItemString(i,
									"ORDER_CODE"), linkBlankP);
							this.getTTable(TABLE2).setLockCellRow(i, true);
							this.getTTable(TABLE2).setLockCell(i, 0, false);// ������
							this.getTTable(TABLE2).setLockCell(i, 1, false);// ���
							this.getTTable(TABLE2).setLockCell(i, 2, false);// ҽ��
							this.getTTable(TABLE2).setLockCell(i, 3, false);// ����
							this.getTTable(TABLE2).setLockCell(i, 5, false);// Ƶ��
							this.getTTable(TABLE2).setLockCell(i, 6, false);// �÷�
							this.getTTable(TABLE2).setLockCell(i, 7, false);//���뷽ʽ
							this.getTTable(TABLE2).setLockCell(i, 8, false);//��Һ����
							this.getTTable(TABLE2).setLockCell(i, 9, false);// ��
							this.getTTable(TABLE2).setLockCell(i, 10, false);// ��
							this.getTTable(TABLE2).setLockCell(i, 11, false);// ҽ����ע
							this.getTTable(TABLE2).setLockCell(i, 12, false);// ִ�п���
							this.getTTable(TABLE2).setLockCell(i, 13, false);// ����ʱ��
							this.getTTable(TABLE2).setLockCell(i, 14, false);// ͣ��ʱ��
							this.getTTable(TABLE2).setLockCell(i, 16, false);// ����
							odiObject.setItem(ds, i, linkBlankP);
							odiObject.setItem(ds, i, linkMainP);
							table.setDSValue(i);
						}
					}
				}
			}
		}

		//========== add by chenhj end 2017.4.10

		// ��ҩע��
		if ("GIVEBOX_FLG".equals(columnName)) {
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// �뿪��ҽ��
				this.messageBox("E0152");
				TDS ds = odiObject.getDS("ODI_ORDER");
				TParm linkFlg = new TParm();
				linkFlg.setData("GIVEBOX_FLG", "N");
				odiObject.setItem(ds, row, linkFlg);
				table.setDSValue(row);
				return;
			}
			TDS ds = odiObject.getDS("ODI_ORDER");
			odiObject.setItem(ds, row, getOutHospStartQty(linkParm));
			table.setDSValue(row);
		}
	}

	/**
	 * �õ���������
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxLinkNo(String type) {
		// System.out.println("------------getMaxLinkNo--------------");
		System.out.println("------" + type);
		if (type.equalsIgnoreCase("ST")) {
			// �ȴӱ��ض�����ȡ����;
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// ������Ӷ�����ȡ����;
			int dbresult = getMaxLinkSTNo();
			// ������Ӷ�����ȡ����;
			if ((result + 1) > dbresult) {
				// System.out.println("----���ڶ�����ȡ����-----");
				return result + 1;
			} else {
				return dbresult;
			}
			// 20151109 WANGJC ADD START
		} else if (type.equalsIgnoreCase("OP")) {
			// �ȴӱ��ض�����ȡ����;
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// ������Ӷ�����ȡ����;
			int dbresult = getMaxLinkOPNo();
			// ������Ӷ�����ȡ����;
			if ((result + 1) > dbresult) {
				// System.out.println("----���ڶ�����ȡ����-----");
				return result + 1;
			} else {
				return dbresult;
			}
			// 20151109 WANGJC ADD END
		} else {
			// this.messageBox("===getMaxLinkNo222222===");
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			TParm parm = ds.getBuffer(buff);
			// System.out.println("===SQL==="+ds.getSQL());
			int result = 0;
			for (int i = 0; i < parm.getCount(); i++) {
				// System.out.println("=============getMaxLinkNo parm============="+parm);
				if (!parm.getBoolean("#ACTIVE#", i))
					continue;
				if (!(type.equals(parm.getValue("RX_KIND", i))))
					continue;
				if (parm.getRow(i).getInt("LINK_NO") > result)
					result = parm.getRow(i).getInt("LINK_NO");
			}
			// this.messageBox("����ֵ:"+result);
			return result == 0 ? 1 : result + 1;
		}
	}

	/**
	 * �õ���󼯺�ҽ�����
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxOrderGroupNo() {
		// �ȴӱ��ض�����ȡ����;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		// System.out.println("===SQL==="+ds.getSQL());
		int result = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			// System.out.println("=============getMaxLinkNo parm============="+parm);
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (parm.getRow(i).getInt("ORDERSET_GROUP_NO") > result)
				result = parm.getRow(i).getInt("ORDERSET_GROUP_NO");
		}
		int dbresult = getMaxOrderGroupSTNo();
		// ������Ӷ�����ȡ����;
		if ((result + 1) > dbresult) {
			// System.out.println("----���ڶ�����ȡ����-----");
			return result + 1;
		} else {
			return dbresult;
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public int getMaxLinkSTNo() {

		// this.getCaseNo()
		TParm parm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT MAX(TO_NUMBER(LINK_NO)) MAX_NO FROM ODI_ORDER  WHERE RX_KIND='ST' AND  CASE_NO='"
								+ this.getCaseNo() + "'"));
		// System.out.println("---getMaxLinkSTNo---" + parm.getInt("MAX_NO",
		// 0));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * 
	 * @param type
	 * @return 20151109 WANGJC ADD
	 */
	public int getMaxLinkOPNo() {

		// this.getCaseNo()
		TParm parm = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT MAX(TO_NUMBER(LINK_NO)) MAX_NO FROM ODI_ORDER  WHERE RX_KIND='OP' AND  CASE_NO='"
								+ this.getCaseNo() + "'"));
		// System.out.println("---getMaxLinkSTNo---" + parm.getInt("MAX_NO",
		// 0));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public int getMaxOrderGroupSTNo() {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT MAX(ORDERSET_GROUP_NO) MAX_NO FROM ODI_ORDER  WHERE  CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (parm.getCount() <= 0) {
			return 1;
		}
		return parm.getInt("MAX_NO", 0) + 1;
	}

	/**
	 * ����Ƿ�������
	 * 
	 * @param linkNo
	 *            int
	 * @param linkMainFlg
	 *            boolean
	 * @return boolean
	 */
	public boolean checkMainLinkItem(int linkNo, boolean linkMainFlg,
			String type) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int mainLinkCount = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (!(type.equals(parm.getValue("RX_KIND", i))))
				continue;
			if (linkMainFlg) {
				if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
						&& parm.getRow(i).getInt("LINK_NO") == linkNo)
					mainLinkCount++;
			} else {
				if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
						&& parm.getRow(i).getInt("LINK_NO") == linkNo) {
					falg = true;
				}
			}
		}
		// this.messageBox_("linkMainFlg"+mainLinkCount);
		if (linkMainFlg)
			if (mainLinkCount > 0)
				falg = true;
		return falg;
	}

	/**
	 * �õ���������
	 * 
	 * @param linkNo
	 *            int
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm getMainLinkOrder(int linkNo, String type) {
		TParm result = new TParm();
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (!(type.equals(parm.getValue("RX_KIND", i))))
				continue;
			if (parm.getRow(i).getBoolean("LINKMAIN_FLG")
					&& parm.getRow(i).getInt("LINK_NO") == linkNo) {
				result = parm.getRow(i);
			}
		}
		return result;
	}

	/**
	 * ��ʼ��DataStore
	 */
	public void initDataStoreToTable() {
		// add by lx ����ʱҽ���������ϴ󣬼���ʱ���
		String startDate = StringTool.getString((Timestamp) this
				.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss");
		String endDate = StringTool.getString((Timestamp) this
				.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss");
		// System.out.println("---initDataStoreToTable startDate---"+startDate);
		// System.out.println("---initDataStoreToTable endDate---"+endDate);

		// ��ҽ��
		TParm action = new TParm();
		// ���ò�ѯ��ֵCASE_NO
		action.setData("CASE_NO", getCaseNo());
		// ����������
		action.setData("ACTION", "COUNT", 1);
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			action.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// ����SQL
		odiObject
		.setSQL(
				"ODI_ORDER",
				odiSqlObject.creatSQL("ODI_ORDER", action, startDate,
						endDate, isOpeFlg()),// wanglong modify 20150106
				"CASE_NO",
				"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE:SAVE_TIME;ORDER_NO;ORDER_DR_CODE",
				"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
		// ���ò����б�
		TParm actionParm = new TParm();
		actionParm.setData("CASE_NO", getCaseNo());
		// ORDERWHEREDATA��������
		odiObject.setAttribute("ORDERWHEREDATA", actionParm);
		// DSPNMWHEREDATA��������
		odiObject.setAttribute("DSPNMWHEREDATA", actionParm);
		// DSPNDWHEREDATA��������
		odiObject.setAttribute("DSPNDWHEREDATA", actionParm);
		// ����Ϊҽ��վ����
		odiObject.setOdiFlg(true);
		// ִ�г�ʼ��DataStore
		odiObject.retrieve();
		// System.out.println("=========11111111111111==========="+new Date());
		// Debug
		// odiObject.getDS("ODI_ORDER").showDebug();
		TDS tds = odiObject.getDS("ODI_ORDER");
		// shibl 20120808 add ����ҽ������ʱ������
		tds.setSort("EFF_DATE ASC,LINKMAIN_FLG DESC");
		tds.sort();
		// System.out.println("������ѯ���2222222222222:======="+new Date());
		// tds.showDebug();
		// ��TABLE1��ʱҽ����
		this.getTTable(TABLE1).setDataStore(tds);
		this.getTTable(TABLE2).setDataStore(tds);
		this.getTTable(TABLE3).setDataStore(tds);
		this.getTTable(TABLE6).setDataStore(tds);
		// this.getTTable(TABLE4).setDataStore(tds);
		// ������¼DATASTORE
		odiDrugArrergy = new OdiDrugAllergy();
		// ���ù�����¼��ѡ���ĸ������
		odiDrugArrergy.setCaseNo(this.getCaseNo());
		// ���ù�����¼��ѡ���ĸ�������
		odiDrugArrergy.setMrNo(this.getMrNo());
		// ��ʼ��������¼
		odiDrugArrergy.onQuery();
		this.getTTable(GMTABLE).setDataStore(odiDrugArrergy);
		selPhaClass(); // add by huangtt 20150506
		// System.out.println("������¼");
		// this.getTTable(GMTABLE).getDataStore().showDebug();
		// ����CASE_NO
		odiObject.setAttribute("CASE_NO", this.getCaseNo());
		// ���ò�������
		odiObject.setAttribute("PAT_NAME", this.getPatName());
		// ��������1
		odiObject.setAttribute("PAT_NAME1", this.getPatName1());
		// ��������
		odiObject.setAttribute("BIRTH_DATE", this.getBirthDay());
		// �����Ա�
		odiObject.setAttribute("SEX_CODE", this.getSexCode());
		// �����ʱ�
		odiObject.setAttribute("POST_CODE", this.getPostCode());
		// ����ͨѶ��ַ
		odiObject.setAttribute("ADDRESS", this.getAddress());
		// ���õ�λ
		odiObject.setAttribute("COMPANY_DESC", this.getCompanyDesc());
		// ���õ绰
		odiObject.setAttribute("TEL", this.getTel());
		// �������֤��
		odiObject.setAttribute("IDNO", this.getIdNo());
		// �����
		odiObject.setAttribute("MAINDIAG", this.getMainDiag());
		// ���
		odiObject.setAttribute("CTZ_CODE", this.getCtzCode());
		// ����������ҩ�������
		this.setOdiOpTableData(getTTable("TABLE7"));
		// ������Ѫ��¼�������
		this.setOdiBlTableData(getTTable("TABLE8"));
		//add by kangy 20170602
		TParm tdsParm=new TParm();
		if(BYXflg){
		for(int i=0;i<tds.rowCount()-1;i++){
			tdsParm.setRowData(i, tds.getRowParm(i));
		}
		tdsParm.setCount(tds.rowCount()-1);
		if(checkAntibioticsTDSParm(tdsParm)){
		if(!checkEtiolgexmParm(tdsParm)){//add by kangy 20170527
			messageBox("û������ԭѧ���");
		} 
		}
		}
		onChange();
	}

	/**
	 * �Ƿ���Ҫ���������
	 * 
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean onSaveFlg(int index) {
		boolean falg = false;
		if (index == 5) {
			// ������¼�Ƿ���Ҫ���������
			falg = isSaveGM();
			return falg;
		} else {
			if (index == 4) {
				TDS ds = odiObject.getDS("ODI_ORDER");
				int count = 0;
				int newRow[] = ds.getNewRows();
				for (int i : newRow) {
					if (!ds.isActive(i))
						continue;
					count++;
				}
				int modifRow[] = ds.getOnlyModifiedRows(this.getTTable(
						"TABLE" + index).getDataStore().PRIMARY);
				int delRowCount = ds.getDeleteCount() < 0 ? 0 : this.getTTable(
						"TABLE" + index).getDataStore().getDeleteCount();
				if (modifRow.length + count + delRowCount > 0)
					falg = true;
				return falg;
			}
			// Ҫ����������
			int count = 0;
			int newRow[] = this.getTTable("TABLE" + index).getDataStore()
					.getNewRows();
			for (int i : newRow) {
				if (!this.getTTable("TABLE" + index).getDataStore().isActive(i))
					continue;
				count++;
			}
			int modifRow[] = this
					.getTTable("TABLE" + index)
					.getDataStore()
					.getOnlyModifiedRows(
							this.getTTable("TABLE" + index).getDataStore().PRIMARY);
			int delRowCount = this.getTTable("TABLE" + index).getDataStore()
					.getDeleteCount() < 0 ? 0 : this.getTTable("TABLE" + index)
							.getDataStore().getDeleteCount();
			if (modifRow.length + count + delRowCount > 0)
				falg = true;
			return falg;
		}
	}

	/**
	 * �õ�TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * ҳǩ�ı��¼�
	 */
	public void onChangeStart() {
		int chageTab = 0;
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TParm pubParm = null;
		TTable table = null;
		// �Ƴ���UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// �Ƴ���UIToolBar
		callFunction("UI|removeChildToolBar");
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
		if (indexPage <= 3 || indexPage == 4 || indexPage == 5) {
			// ת�����ڿ���
			if (this.chagePage && onSaveFlg(indexPage + 1)) {
				if (indexPage == 4) {
					if (messageBox("��ʾ��Ϣ Tips", "������¼�Ƿ���Ҫ����? \n Are you Save?",
							this.YES_NO_OPTION) != 0) {
						this.chagePage = false;
						tab.setSelectedIndex(indexPage);
						this.chagePage = true;
						return;
					}
				}
				if (messageBox("��ʾ��Ϣ", "ҽ���Ƿ���Ҫ����? \n Are you Save?",
						this.YES_NO_OPTION) != 0) {
					chageTab = tab.getSelectedIndex();
					this.chagePage = false;
					tab.setSelectedIndex(indexPage);
					this.chagePage = true;
					return;
				} else {
					tabSaveFlg = true;// �ñ�������onSave���������Ϊ�л�ҳǩʱ�ı���
					this.onSave();
					tabSaveFlg = false;
					// ҳǩ�л�����һ��ҳǩ
					chageTab = tab.getSelectedIndex();
					this.chagePage = false;
					tab.setSelectedIndex(indexPage);
					this.chagePage = true;
				}
			}
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:

			// ��ʱ
			table = getTTable(TABLE1);
			// table.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// ===add by lx 2012-07-04====//
			String f1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String f2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				f1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				f2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(f1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ f2);
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			// ==========pangben 2013-9-10 �޸���ʾͣ��ʱ��
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND ( DC_DATE='' OR DC_DATE>'"
					+ StringTool.getString(SystemTool.getInstance()
							.getDate(), "yyyy-MM-dd HH:mm:ss")
					+ ".0')) OR RX_KIND='UD' AND #ACTIVE#='N'");

			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			table.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' "
					+ getRxNoString()
					+ " AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// ���ô�����
			onNewRxNo("2");
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			ds.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' "
					+ getRxNoIGString()
					+ " AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			// ���ô�����
			onNewRxNoIG("2");
			break;
			// case 4:
			// //�ٴ����
			// pubParm = new TParm();
			// pubParm.setData("CASE_NO",this.getCaseNo());
			// pubParm.setData("IPD_NO",this.getIpdNo());
			// pubParm.setData("MR_NO",this.getMrNo());
			// pubParm.setData("RULE_TYPE","I");
			// ((TPanel)this.getComponent("CLNDIAG_PANEL")).addItem(
			// "CLNDIAG", "%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm,false);
			// return;
			// case 5:
			// //������ҳ
			// pubParm = new TParm();
			// pubParm.setData("SYSTEM_CODE", "ODI");
			// pubParm.setData("MR_NO", this.getMrNo());
			// pubParm.setData("CASE_NO", this.getCaseNo());
			// //ҽʦ����(USER_TYPE=2)
			// pubParm.setData("USER_TYPE","2");
			// pubParm.setData("OPEN_USER",Operator.getID());
			// ((TPanel)this.getComponent("MROINDEX_PANEL")).addItem(
			// "MROINDEX", "%ROOT%\\config\\mro\\MRORecord.x", pubParm,false);
			// return;
			// case 6:
			// //�������
			// pubParm = new TParm();
			// pubParm.setData("MRO", "STATE", "ODI");
			// pubParm.setData("MRO", "MR_NO", this.getMrNo());
			// pubParm.setData("MRO", "CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROCHR_PANEL")).addItem(
			// "MROCHR", "%ROOT%\\config\\mro\\MRO_Chrtvetrec.x", pubParm,false);
			// return;
		case 4:
			// ������¼
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// ������¼
			break;
		case 5:
			// Ժǰ��ҩ
			table = getTTable(TABLE6);
			table.setFilter(" RX_KIND='PA' ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 6:
			// ������ҩ
			// ��¼��ǰҳ��INDEX
			indexPage = tab.getSelectedIndex();
			this.clearFlg = "N";
			return;
		case 7:
			// ��Ѫ��¼
			// ��¼��ǰҳ��INDEX
			indexPage = tab.getSelectedIndex();
			this.clearFlg = "N";
			return;
		}
		// ��ҩ��Ƭ
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				// �Ѿ�ֹͣ���ۣ�
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// if (tab.getSelectedIndex() != 4)
			// table.sort();
			if (tab.getSelectedIndex() == 5) {
				table.setSort("ORDER_NO ASC,ORDER_SEQ ASC");
				table.sort();
			}
			table.setDSValue();
		}
		// this.antflg = false;//ȥ������ʾ�����صĿ�ҩ����ʱ�������������
		// ��ʼ��ҽ������
		initOrderStart();
		// ���һ��
		this.onAddRow();
		// ������
		lockRowOrder();
		// ��¼��ǰҳ��INDEX
		indexPage = tab.getSelectedIndex();
		this.clearFlg = "N";
	}

	// ¬������ ����ҽ��ת��Ժ��ҩ����
	public void udTods() {
		TTable table2 = getTTable(TABLE2);
		// table2.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String dsFilter = ds.getFilter();
		ds
		.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
		ds.filter();
		// ��Ҫ�ƶ���order
		TParm result = new TParm();
		Vector ordvct = ds.getVector();
		for (int i = 0; i < ordvct.size(); i++) {
			Vector orderVectorRow = ds.getVectorRow(i, "ORDER_CODE");
			Vector cat1TypeVectorRow = ds.getVectorRow(i, "CAT1_TYPE");
			Vector routeTypeVectorRow = ds.getVectorRow(i, "ROUTE_CODE");
			String orderCode = (String) orderVectorRow.get(0);
			String cat1Type = (String) cat1TypeVectorRow.get(0);
			String routeCode = (String) routeTypeVectorRow.get(0);

			if (!"".equals(orderCode) && "PHA".equals(cat1Type)
					&& "PO".equals(routeCode)) {
				result.addData("ORDER_CODE", orderCode);
				// $$=============add by lx 2012/04/26
				// start==================$$//
				// ��������
				result.addData("MEDI_QTY", ds.getVectorRow(i, "MEDI_QTY")
						.get(0));
				// System.out.println("MEDI_QTY===="+ds.getVectorRow(i,
				// "MEDI_QTY").get(0));
				// Ƶ��
				result.addData("FREQ_CODE", ds.getVectorRow(i, "FREQ_CODE")
						.get(0));
				// System.out.println("FREQ_CODE===="+ds.getVectorRow(i,
				// "FREQ_CODE").get(0));
				// �÷�
				result.addData("ROUTE_CODE", ds.getVectorRow(i, "ROUTE_CODE")
						.get(0));
				// System.out.println("ROUTE_CODE===="+ds.getVectorRow(i,
				// "ROUTE_CODE").get(0));
				// $$=============add by lx 2012/04/26 end==================$$//
			}
		}
		// �ָ�
		ds.setFilter(dsFilter);
		ds.filter();
		// ��Ժ��ҩ
		TTable table = getTTable(TABLE3);
		table.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' " + getRxNoString()
		+ " AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
		int rowCount = result.getCount("ORDER_CODE");
		int row = getExitRow();
		yyList = true;
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
					result.getValue("ORDER_CODE", i));
			// $$=============add by lx 2012/04/26 start==================$$//
			OrderParm.setData("MEDI_QTY", result.getValue("MEDI_QTY", i));
			OrderParm.setData("FREQ_CODE", result.getValue("FREQ_CODE", i));
			OrderParm.setData("ROUTE_CODE", result.getValue("ROUTE_CODE", i));

			// $$=============add by lx 2012/04/26 end==================$$//
			OrderParm.setData("EXID_ROW", row);
			OrderParm.setData("CDSS_FLG", false); // add by huangtt
			// 20151116��Ժ��ҩʱ���ó���ҽ��������CDSS�����ж�
			row++;
			// System.out.println("===OrderParm==="+i+"==="+OrderParm);
			this.popReturn("DS", OrderParm);
		}
		yyList = false;
	}

	/**
	 * �ٴ����
	 */
	public void onLcICD() {
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		TParm pubParm = new TParm();
		pubParm.setData("CASE_NO", this.getCaseNo());
		pubParm.setData("IPD_NO", this.getIpdNo());
		pubParm.setData("MR_NO", this.getMrNo());
		pubParm.setData("DEPT_CODE", this.getDeptCode());
		pubParm.setData("DR_CODE", Operator.getID());
		pubParm.setData("RULE_TYPE", "I");
		pubParm.setData("BIRTHDAY", getBirthDay());
		pubParm.setData("SEX_CODE", getSexCode());
		pubParm.setData("IN_DATE", getAdmDate());
		this.openWindow("%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);// shibl
		// modify
		// 20130515
		// this.openWindow("%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);
	}

	/**
	 * ������Ŀ
	 */
	public void onBASY() {
		TParm pubParm = new TParm();
		pubParm.setData("SYSTEM_CODE", "ODI");
		pubParm.setData("MR_NO", this.getMrNo());
		pubParm.setData("CASE_NO", this.getCaseNo());
		// ҽʦ����(USER_TYPE=2)
		pubParm.setData("USER_TYPE", "2");
		pubParm.setData("OPEN_USER", Operator.getID());
		TParm result = (TParm) this.openWindow(
				"%ROOT%\\config\\mro\\MRORecord.x", pubParm);// shibl modify
		// 20130515
	}

	/**
	 * �������
	 */
	public void onBABM() {
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		TParm pubParm = new TParm();
		pubParm.setData("MRO", "STATE", "ODI");
		pubParm.setData("MRO", "MR_NO", this.getMrNo());
		pubParm.setData("MRO", "CASE_NO", this.getCaseNo());
		this.openWindow("%ROOT%\\config\\mro\\MRO_Chrtvetrec.x", pubParm);// shibl
		// modify
		// 20130515
	}

	/**
	 * ����ʷ���
	 * 
	 * @return String
	 */
	public String getDrugType() {
		String resultStr = "";
		if (((TRadioButton) this.getComponent("PHA_DRUGALLERGY")).isSelected()) {
			// resultStr = "B";
			resultStr = this.getValueString("PHA_CLASS"); // modify by huangtt
			// 20150507
		}
		if (((TRadioButton) this.getComponent("CF_DRUGALLERGY")).isSelected()) {
			resultStr = "A";
		}
		if (((TRadioButton) this.getComponent("OTHER_DRUGALLERGY"))
				.isSelected()) {
			resultStr = "C";
		}
		if (((TRadioButton) this.getComponent("NONE_DRUGALLERGY")).isSelected()) {
			resultStr = "N";
		}
		return resultStr;
	}

	/**
	 * ҳǩ�ı��¼�
	 */
	public void onChange() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// ���ò���
		TParm pubParm = null;
		TTable table = null;
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			table = getTTable(TABLE1);
			// $$===========add by lx 2012-06-30 ��ʱҽ��ֻ��ʾ����,�����ʾ�ٶ�
			// START================$$//
			String f1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String f2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				f1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				f2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(f1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ f2);
			// $$===========add by lx 2012-06-30 ��ʱҽ��ֻ��ʾ����,�����ʾ�ٶ�
			// END================$$//
			// del by lx 2012-06-30 ��ʱҽ������ʾȫ�����ݣ������ʾ�ٶ�
			// table.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND ( DC_DATE='' OR DC_DATE>'"
					+ StringTool.getString(SystemTool.getInstance()
							.getDate(), "yyyy-MM-dd HH:mm:ss")
					+ ".0')) OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			onNewRxNo("2");
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			this.onNewRxNoIG("2");
			break;
			// case 4:
			// //�ٴ����
			// pubParm = new TParm();
			// pubParm.setData("CASE_NO",this.getCaseNo());
			// pubParm.setData("IPD_NO",this.getIpdNo());
			// pubParm.setData("MR_NO",this.getMrNo());
			// pubParm.setData("RULE_TYPE","I");
			// ( (TPanel)this.getComponent("CLNDIAG_PANEL")).addItem(
			// "CLNDIAG", "%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);
			// return;
			// case 5:
			// //������ҳ
			// pubParm = new TParm();
			// pubParm.setData("SYSTEM_CODE", "ODI");
			// pubParm.setData("MR_NO", this.getMrNo());
			// pubParm.setData("CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROINDEX_PANEL")).addItem(
			// "MROINDEX", "%ROOT%\\config\\mro\\MRORecord.x", pubParm);
			// return;
			// case 6:
			// //�������
			// pubParm = new TParm();
			// pubParm.setData("MRO", "STATE","ODI");
			// pubParm.setData("MRO","MR_NO", this.getMrNo());
			// pubParm.setData("MRO","CASE_NO", this.getCaseNo());
			// ((TPanel)this.getComponent("MROCHR_PANEL")).addItem(
			// "MROCHR", "%ROOT%\\config\\mro\\MROChrtvetrec.x", pubParm);
			// return;
		case 4:
			// ������¼
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			// ������¼
			break;
		case 5:
			// Ժǰ��ҩ
			table = getTTable(TABLE6);
			table.setFilter(" RX_KIND='PA' ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		}
		// ��ҩ��Ƭ
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				// �Ѿ�ֹͣ���ۣ�
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		this.onAddRow();
		// ��ʼ��ҽ������
		initOrderStart();
		// ���һ��
		// table.getDataStore().showDebug();
		// ��¼��ǰҳ��INDEX
		indexPage = tab.getSelectedIndex();
		// ������
		lockRowOrder();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		BYXflg=false;
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// ���ò���
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// System.out.println("tab.getSelectedIndex():::::"+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// $$========add by lx 2013/01/11 ��ʱ���¼�������==========$$//
			initDataStoreToTable();
			//
			// ��ʱ
			table = getTTable(TABLE1);
			String filter1 = "(RX_KIND='ST' AND HIDE_FLG='N' ";
			String filter2 = ".0' OR (RX_KIND='ST' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='ST' AND #ACTIVE#='N'";
			if (opeFlg) {
				filter1 = "(RX_KIND='OP' AND HIDE_FLG='N' ";
				filter2 = ".0' OR (RX_KIND='OP' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='OP' AND #ACTIVE#='N'";
			}
			table.setFilter(filter1
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss")
					+ filter2);
			// table.setSort("ORDER_NO,ORDER_SEQ");
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			// System.out.println("DG11111111111111111111");
			if (this.getTRadioButton("DCORDER").isSelected()) {// shibl
				// System.out.println("2222222222222222222222222");
				dcOrderShow();
				break;
			}
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG='N' "
					+ getQueryOrderCat1Type()
					+ " "
					+ getPhaOrderUtil()
					+ " AND ORDER_DATE >= '"
					+ StringTool.getString((Timestamp) this
							.getValue("START_DATEUD"),
							"yyyy-MM-dd HH:mm:ss")
					+ ".0' AND ORDER_DATE < '"
					+ StringTool.getString((Timestamp) this
							.getValue("END_DATEUD"),
							"yyyy-MM-dd HH:mm:ss")
					+ ".0' OR (RX_KIND='UD' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			// System.out.println("�������"+"(RX_KIND='UD' AND HIDE_FLG='N' "
			// + getQueryOrderCat1Type()
			// + " "
			// + getPhaOrderUtil()
			// + " AND ORDER_DATE >= '"
			// + StringTool.getString((Timestamp) this
			// .getValue("START_DATEUD"),
			// "yyyy-MM-dd HH:mm:ss")
			// + ".0' AND ORDER_DATE < '"
			// + StringTool.getString((Timestamp) this
			// .getValue("END_DATEUD"),
			// "yyyy-MM-dd HH:mm:ss")
			// +
			// ".0' OR (RX_KIND='UD' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='UD' AND #ACTIVE#='N'");

			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG='N' "
					+ getRxNoString()
					+ " OR (RX_KIND='DS' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG='N' "
					+ getRxNoIGString()
					+ " OR (RX_KIND='IG' AND #NEW#='Y' AND HIDE_FLG ='N')) OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			break;
		case 4:
			// ������¼
			table = getTTable(GMTABLE);
			table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
					+ this.getMrNo() + "'");
			// table.setSort("ADM_DATE");
			break;
		}
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			// table.sort();
			table.setDSValue();
		} else {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				this.messageBox("E0155");
			}
		}
		// ��ʼ��ҽ������
		initOrderStart();
		// ���һ��
		this.onAddRow();
		// ������
		lockRowOrder();
		// ��¼��ǰҳ��INDEX
		indexPage = tab.getSelectedIndex();
		// �������ע��(�������ִ��)
		this.clearFlg = "N";
	}

	// 4,10,16,17,18,19,20,21,22,23,24,25,26,27,28,29
	/**
	 * �õ�������
	 * 
	 * @return String
	 */
	public String getRxNoString() {
		// if(this.getValueString("RX_NO").length()==0)
		// return "";
		return " AND RX_NO='" + this.getValueString("RX_NO") + "' ";
	}

	/**
	 * �õ�������
	 * 
	 * @return String
	 */
	public String getRxNoIGString() {
		// if(this.getValueString("IG_RX_NO").length()==0)
		// return "";
		return " AND RX_NO='" + this.getValueString("IG_RX_NO") + "' ";
	}

	/**
	 * �õ�ҽ�����
	 * 
	 * @return String
	 */
	public String getQueryOrderCat1Type() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String cat1Type = "";
		String type = "";
		// ����ҳǩ�л�ȡ��ͬ�ؼ��е�ֵ SHIBL 20120801 modify
		switch (tab.getSelectedIndex()) {
		case 0:
			type = this.getValueString("KLSTAR");
			break;
		case 1:
			type = this.getValueString("KLSTARUD");
			break;
		case 2:
			type = this.getValueString("KLSTARDS");
			break;
		}
		if ("A".equals(type)) {
			return cat1Type;
		}
		if ("B".equals(type)) {
			cat1Type = "AND (CAT1_TYPE='LIS' OR CAT1_TYPE='RIS')";
		}
		if ("C".equals(type)) {
			cat1Type = "AND (CAT1_TYPE='TRT' OR CAT1_TYPE='PLN' OR CAT1_TYPE='OTH')";
		}
		if ("D".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_W'";
		}
		if ("E".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_C'";
		}
		if ("F".equals(type)) {
			cat1Type = "AND ORDER_CAT1_CODE='PHA_G'";
		}
		if ("G".equals(type)) {
			cat1Type = "AND LCS_CLASS_CODE != ''";
		}
		return cat1Type;
	}

	/**
	 * ҽ����Ч��
	 * 
	 * @return String
	 */
	public String getPhaOrderUtil() {
		String phaOrderUtilStr = "";
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			// ��Ч
			if (((TRadioButton) this.getComponent("YXORDER")).isSelected()) {
				phaOrderUtilStr = "AND DC_DATE = ''";
			}
			// ��ͣ��
			if (((TRadioButton) this.getComponent("STOPORDER")).isSelected()) {
				phaOrderUtilStr = "AND DC_DATE != ''";
			}
			// ȫ��
			if (((TRadioButton) this.getComponent("ALLORDER")).isSelected()) {
				return phaOrderUtilStr;
			}
			break;
		case 1:
			// ����
			// ��Чyanjing 20131119
			String nowDate = StringTool.getString((Timestamp) SystemTool
					.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss");
			nowDate += ".0";
			if (((TRadioButton) this.getComponent("UDUTIL")).isSelected()) {
				// phaOrderUtilStr = "AND DC_DATE = '' ";
				// stSQL+=" AND ORDER_DATE<= TO_DATE ('"+stEndDate+"', 'YYYY-MM-DD HH24:MI:SS'))";
				// phaOrderUtilStr = "AND (DC_DATE = '' OR DC_DATE > SYSDATE) ";
				phaOrderUtilStr = "AND (DC_DATE = '' OR DC_DATE >'" + nowDate
						+ "')";
			}
			// ��ͣ��
			if (((TRadioButton) this.getComponent("DCORDER")).isSelected()) {
				// phaOrderUtilStr =
				// "AND DC_DATE != '' AND DC_DATE <= SYSDATE ";
				// phaOrderUtilStr = "AND DC_DATE != '' ";
				phaOrderUtilStr = "AND DC_DATE != '' AND DC_DATE <= '"
						+ nowDate + "' ";
			}
			// ȫ��
			if (((TRadioButton) this.getComponent("UDALLORDER")).isSelected()) {
				return phaOrderUtilStr;
			}
			break;
		}
		return phaOrderUtilStr;
	}

	/**
	 * ������
	 */
	public void lockRowOrder() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		int rowCount = 0;
		switch (tab.getSelectedIndex()) {
		// ��ʱ
		case 0:
			table = this.getTTable(TABLE1);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {

					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 9, false);
					}
					if (getTempFlg(temp).equals("Y")) { // ��ʿ��˹�ҽ������ shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 9, true);
					}
				} else {

					table.setLockCellRow(i, false);
				}
			}
			int newRowST[] = table.getDataStore().getModifiedRows();
			for (int i : newRowST) {
				TParm temp = table.getDataStore().getRowParm(i);
				// ��������ҽ��ϸ��
				if (temp.getValue("LINKMAIN_FLG").equals("N")
						&& !temp.getValue("LINK_NO").equals("")) {// shibl
					// 20130315
					// modify
					table.setLockCellRow(i, true);
					table.setLockCell(i, 0, false);// ������
					table.setLockCell(i, 1, false);// ���
					table.setLockCell(i, 2, false);// ҽ��
					table.setLockCell(i, 3, false);// ����
					table.setLockCell(i, 6, false);// ���뷽ʽ
					table.setLockCell(i, 7, false);// ��Һ����
					table.setLockCell(i, 11, false);// ҽ����ע
					table.setLockCell(i, 13, false);// ������ʶ
					table.setLockCell(i, 14, false);// ��
					table.setLockCell(i, 15, false);// ��
					table.setLockCell(i, 16, false);// ��
					table.setLockCell(i, 17, false);// ��
				} else {
					table.setLockCellRow(i, false);
				}
			}
			break;
			// ����
		case 1:
			table = this.getTTable(TABLE2);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 11, false);  //modify by wukai 9��11
					}
					if (getTempFlg(temp).equals("Y")) { // ��ʿ��˹�ҽ������ shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 11, true); //modify by wukai 9��11
					}
				} else {

					table.setLockCellRow(i, false);

				}
			}
			int newRowUD[] = table.getDataStore().getModifiedRows();
			for (int i : newRowUD) {// ��������ҽ��ϸ��
				TParm temp = table.getDataStore().getRowParm(i);
				if (temp.getValue("LINKMAIN_FLG").equals("N")
						&& !temp.getValue("LINK_NO").equals("")) {// shibl
					// 20130315
					// modify
					this.getTTable(TABLE2).setLockCellRow(i, true);
					this.getTTable(TABLE2).setLockCell(i, 0, false);// ������
					this.getTTable(TABLE2).setLockCell(i, 1, false);// ���
					this.getTTable(TABLE2).setLockCell(i, 2, false);// ҽ��
					this.getTTable(TABLE2).setLockCell(i, 3, false);// ����
					this.getTTable(TABLE2).setLockCell(i, 7, false);// ���뷽ʽ
					this.getTTable(TABLE2).setLockCell(i, 8, false);// ��Һ����
					this.getTTable(TABLE2).setLockCell(i, 9, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 10, false);// ��
					this.getTTable(TABLE2).setLockCell(i, 11, false);// ҽ����ע
					this.getTTable(TABLE2).setLockCell(i, 16, false);// ����
				} else {
					table.setLockCellRow(i, false);
				}
			}
			break;
			// ��Ժ��ҩ
		case 2:
			table = this.getTTable(TABLE3);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("ORDER_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, 11, false);
						table.setLockCell(i, 3, false);
						table.setLockCell(i, 5, false);
						table.setLockCell(i, 6, false);
						table.setLockCell(i, 7, false);
						table.setLockCell(i, 8, false);
						table.setLockCell(i, 9, false);
						table.setLockCell(i, 10, false);
						table.setLockCell(i, 13, false);// ������ʶ

					}
					if (getTempFlg(temp).equals("Y")) { // ��ʿ��˹�ҽ������ shibl
						// 20120927 modify
						table.setLockCellRow(i, true);
					}
					if (temp.getValue("DC_DR_CODE").length() != 0) {
						table.setLockCell(i, 11, true);
						table.setLockCell(i, 3, true);
						table.setLockCell(i, 5, true);
						table.setLockCell(i, 6, true);
						//						table.setLockCell(i, 7, true);
						//						table.setLockCell(i, 8, true);
						table.setLockCell(i, 9, true);
						table.setLockCell(i, 10, true);
						table.setLockCell(i, 13, false);// ������ʶ
					}
				} else {
					table.setLockCellRow(i, false);
				}
			}
			int newRowDS[] = table.getDataStore().getModifiedRows();
			for (int i : newRowDS) {
				table.setLockCellRow(i, false);
			}
			break;
			// סԺ��ҽ
		case 3:

			break;
			// ������¼
		case 4:
			table = this.getTTable(GMTABLE);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = table.getDataStore().getRowParm(i);
				if (table.getDataStore().isActive(i)) {
					if (temp.getValue("DRUGORINGRD_CODE").length() != 0) {
						table.setLockCellRow(i, true);
						table.setLockCell(i, "ALLERGY_NOTE", false);
					}
				} else {
					table.setLockCellRow(i, false);
				}
			}
			int newRowGM[] = table.getDataStore().getModifiedRows();
			for (int i : newRowGM) {
				table.setLockCellRow(i, false);
			}
			break;
		case 5:
			table = this.getTTable(TABLE6);
			rowCount = table.getDataStore().rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (table.getDataStore().isActive(i)) {
					table.setLockCell(i, 0, true);
				} else {
					table.setLockCell(i, 0, false);
				}
			}
		}
	}

	/**
	 * �õ�ҽ���Ƿ���˹�ע��
	 * 
	 * @param parm
	 * @return
	 */
	private String getTempFlg(TParm parm) {
		if (parm == null)
			return "";
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String orderSeq = parm.getValue("ORDER_SEQ");
		String sql = "SELECT TEMPORARY_FLG FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='"
				+ orderSeq + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlparm.getCount() <= 0)
			return "";

		return sqlparm.getValue("TEMPORARY_FLG", 0);
	}

	/**
	 * ��ʼ��ҽ��״̬
	 */
	public void initOrderStart() {
		boolean flg = true;
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = getTTable("TABLE" + (tab.getSelectedIndex() + 1));
		// this.messageBox("2");
		// ��ҩ��Ƭʱ�������ж�
		if (tab.getSelectedIndex() >= 3)
			return;
		TParm parm = table.getDataStore().getBuffer(
				table.getDataStore().PRIMARY);
		int rowCount = parm.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm action = parm.getRow(i);
			// System.out.println("111111action is:::"+action);
			// ================= chenxi ҩƷ��ʾ��Ϣ
			String orderCode = action.getValue("ORDER_CODE");
			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "' ";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
			sqlparm = sqlparm.getRow(0);
			// System.out.println("===========sqlparm========"+sqlparm);
			// ============ chenxi
			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(action.getValue("INSPAY_TYPE"))) {
					table.setRowTextColor(i, nhiColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(action.getValue("CAT1_TYPE"))) {
				// �Ƿ��ǹ���ҩƷ
				if (action.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					table.setRowTextColor(i, ctrlDrugClassColor);
				}
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// �жϿ�����
			if (action.getValue("ANTIBIOTIC_CODE").length() != 0) {
				flg = this.antflg;
				if ("UD".equals(action.getValue("RX_KIND"))
						&& action.getValue("DC_DATE").equals("") && flg) {// shibl
					// 20130326
					// modify
					// ��ͣ��ҩƷ����
					// �鿴�����������Ƿ񳬹�Ԥ������
					int day = OrderUtil.getInstance().checkAntibioticDay(
							action.getValue("ANTIBIOTIC_CODE"));
					int cDay = StringTool.getDateDiffer(SystemTool
							.getInstance().getDate(), (Timestamp) action
							.getData("EFF_DATE"));

					// if (day != -1 && cDay + 1 >= day) {// modify by wanglong
					// 20130801 ��ҩ�����ȱ�׼������һ��Ϳ�ʼ��
					// // ����
					// this.changeTableRowColor(i, tab.getSelectedIndex());
					// // if ("en".equals(this.getLanguage())) {
					// // this
					// // .messageBox(action
					// // .getValue("ORDER_ENG_DESC")
					// // + ":Beyond the default number of antibiotics:"
					// // + day + "days��");
					// // } else {
					// // this.messageBox(action.getValue("ORDER_DESC")
					// // + ":�����س�����Ԥ������" + day + "�죡");
					// // }
					// // ֹͣ����
					// // this.stopColor();
					// }
				}
				table.setRowTextColor(i, antibioticColor);

			} else if (sqlparm.getValue("DRUG_NOTES_DR").length() != 0) {// ===========cxcx��ҩע�����Ϊ��
				table.setRowTextColor(i, blue);
				// continue;
			} else {
				table.setRowTextColor(i, normalColor);
			}
			// �Ƿ���ͣ��ҽ��
			// ȡ��ϵͳ��ͣ��ʱ��Ƚ�,ͣ��ʱ��С�ڵ���ϵͳʱ��ʱ,�������óɻ�ɫ
			// ȡ��ϵͳʱ��
			String sysdate = SystemTool.getInstance().getDate().toString();
			String dc_date = action.getValue("DC_DATE");
			if (action.getValue("DC_DR_CODE").length() != 0
					&& sysdate.compareTo(dc_date) >= 0) {
				// this.messageBox("1111111111111111111");
				table.setRowColor(i, dcColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}

			// �Ƿ��л�ʿ��עNS_NOTE
			if (action.getValue("NS_NOTE").length() != 0) {
				table.setRowColor(i, nsNodeColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
			// ��ʿ�Ƿ����
			if (action.getValue("NS_CHECK_CODE").length() != 0) {
				table.setRowColor(i, checkColor);
				continue;
			} else {
				table.setRowColor(i, normalColorBJ);
			}
		}
		// this.messageBox("22");
	}

	/**
	 * ����ʵ������
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * �õ�����֮ǰ���к�
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTTable(table).getColumnModel().getColumnIndex(column);
	}

	/**
	 * �õ���ǰѡ���к�
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getNewThisColumnIndex(int column) {
		return this.getTTable(TABLE1).getColumnModel()
				.getColumnIndexNew(column);
	}

	/**
	 * ������¼TABLE�༭ʱ����Ӧ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentGM(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// �õ�����
		String columnName = this.getFactColumnName(GMTABLE, column);
		// if(!"DRUGORINGRD_CODE".equals(columnName))
		// return;
		if (!"ORDER_DESC".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		String drugType = this.getDrugType();
		// �ɷ�
		if ("A".equals(drugType)) {
			// ������¼����
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "A");
			// ���õ����˵�
			textFilter.setPopupMenuParameter("GMA", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}
		// ҩƷ
		if ("B".equals(drugType)) {
			// ������¼����
			TParm parm = new TParm();
			parm.setData("ODI_ORDER_TYPE", "A");
			// ���õ����˵�
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		}
		// ����
		if ("C".equals(drugType)) {
			// ������¼����
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "C");
			// ���õ����˵�
			textFilter.setPopupMenuParameter("GMC", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}
		// �޹��� add by guoy 20151112
		if ("N".equals(drugType)) {
			// ������¼����
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "N");
			// ���õ����˵�
			textFilter.setPopupMenuParameter("GMN", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);
		}

		// add by huangtt 20150507 start
		// ҩ������ add by huangtt 20150507
		if ("D".equals(drugType)) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "D");
			// ��table�ϵ���text����sys_fee��������
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}
		// ҩ��η��� add by huangtt 20150507
		if ("E".equals(drugType)) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "E");
			// ��table�ϵ���text����sys_fee��������
			textFilter.setPopupMenuParameter("GMB", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSPhaClassPopup.x"), parm);
		}

		// add by huangtt 20150507 end

		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ��ʱ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentST(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// ��ǰ�༭��
		this.rowOnly = row;
		// �õ�����
		String columnName = this.getFactColumnName(TABLE1, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;

		// xueyf begin
		TTable table = getTTable(TABLE1);
		/**
		 * TParm parm1=table.getShowParmValue();d //�Ǽ���ҽ��
		 * if(parm1.getValue(columnName,row).startsWith("*")){ TTextField
		 * textFilter = (TTextField) com; textFilter.setEnabled(false); return ;
		 * }
		 **/
		int selRow = this.getTTable(TABLE1).getSelectedRow();
		TParm existParm = this.getTTable(TABLE1).getDataStore().getRowParm(
				selRow);
		// System.out.println("===come in existParm==="+existParm);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if ("PHA".equals(existParm.getValue("CAT1_TYPE"))
				&& null != existParm.getValue("ANTIBIOTIC_CODE")
				&& !existParm.getValue("ANTIBIOTIC_CODE").equals("")) {// =====pangben
			// 2014-6-26
			// �޸Ĵ��صĿ���ҩƷ�������޸�
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			this.messageBox("����ҩƷ�������޸�");
			return;
		}
		// xueyf end

		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ��ʱҽ������
		TParm parm = new TParm();
		parm.setData("ODI_ORDER_TYPE", this.getValue("KLSTAR"));
		// ���õ����˵�
		textFilter.setPopupMenuParameter("ST", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ��ʱҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueST(Object obj) {
		//this.messageBox("================onChangeTableValueST=====================");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		//this.messageBox(""+columnName);
		// �жϵ�ǰ���Ƿ���ҽ;
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE1).getDataStore().getRowParm(selRow);
		// System.out.println("=======orderP======="+orderP);

		// this.messageBox("================11111=====================");
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// ���ҽ������
			clearRow("ST", selRow, "ORDER_DESC");
			this.getTTable(TABLE1).setDSValue(selRow);
		}
		//this.messageBox("================22222====================");
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			//this.messageBox("================22222_1====================");
			// �����
			int row = this.getTTable(TABLE1).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE1).getDataStore().getRowParm(
					row);
			// ���Ϊ0���
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("��ǰѡ��������:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// �뿪��ҽ��
				this.messageBox("E0152");
				return true;
			}
			// ����ҽ�����ܿ�����
			if (Integer.parseInt(node.getValue().toString()) > 0
					&& isOrderSet(linkParm)) {
				this.messageBox("����ҽ�����ܿ�������");
				return true;
			}
			// this.messageBox("================22222_2====================");
			//add by wukai 20160607 satrt
			this.getTTable(TABLE1).setLockCell(row, 6, false);// Ƶ��
			this.getTTable(TABLE1).setLockCell(row, 7, false);// ִ�п���
			//add by wukai 20160607 end 
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// �Ƿ�������Y���ǵ�ʱ�༭��ֵN�Զ�������
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "ST")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE1).setLockCellRow(row, true);
					this.getTTable(TABLE1).setLockCell(row, 0, false);// ������
					this.getTTable(TABLE1).setLockCell(row, 1, false);// ���
					this.getTTable(TABLE1).setLockCell(row, 2, false);// ҽ��
					this.getTTable(TABLE1).setLockCell(row, 3, false);// ����
					this.getTTable(TABLE1).setLockCell(row, 5, false);// �÷�
					this.getTTable(TABLE1).setLockCell(row, 6, false);// ���뷽ʽ
					this.getTTable(TABLE1).setLockCell(row, 7, false);// ��Һ����
					this.getTTable(TABLE1).setLockCell(row, 8, false);// Ƶ��
					this.getTTable(TABLE1).setLockCell(row, 9, false);// ִ�п���
					this.getTTable(TABLE1).setLockCell(row, 10, false);// ����ʱ��
					this.getTTable(TABLE1).setLockCell(row, 11, false);// ҽ����ע
					this.getTTable(TABLE1).setLockCell(row, 13, false);// ������ʶ
					this.getTTable(TABLE1).setLockCell(row, 14, false);// ��
					this.getTTable(TABLE1).setLockCell(row, 15, false);// ��
					this.getTTable(TABLE1).setLockCell(row, 16, false);// ��
					this.getTTable(TABLE1).setLockCell(row, 17, false);// ��
					this.getTTable(TABLE1).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"ST");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					if (linkParm.getInt("LINK_NO") != 0) {
						islinkP.setData("EXEC_DEPT_CODE", linkP
								.getData("EXEC_DEPT_CODE"));
						this.getTTable(TABLE1).setLockCellRow(row, true);
						this.getTTable(TABLE1).setLockCell(row, 0, false);// ������
						this.getTTable(TABLE1).setLockCell(row, 1, false);// ���
						this.getTTable(TABLE1).setLockCell(row, 2, false);// ҽ��
						this.getTTable(TABLE1).setLockCell(row, 3, false);// ����
						//add by wukai 20160607 satrt
						this.getTTable(TABLE1).setLockCell(row, 6, false);// ���뷽ʽ
						this.getTTable(TABLE1).setLockCell(row, 7, false);// ��Һ����
						//add by wukai 20160607 end
						this.getTTable(TABLE1).setLockCell(row, 11, false);// ҽ����ע
						this.getTTable(TABLE1).setLockCell(row, 13, false);// ������ʶ
						this.getTTable(TABLE1).setLockCell(row, 14, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 15, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 16, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 17, false);// ��
					} else {
						// ִ�п������¸�ֵ
						islinkP.setData("EXEC_DEPT_CODE", this
								.getExeDeptCodeST(linkParm
										.getValue("ORDER_CODE"), row, TABLE1,
										""));
						islinkP.setData("ROUTE_CODE", getMediQty(new TParm(),
								linkParm).getValue("ROUTE_CODE", 0));
						this.getTTable(TABLE1).setLockCellRow(row, true);
						this.getTTable(TABLE1).setLockCell(row, 0, false);// ������
						this.getTTable(TABLE1).setLockCell(row, 1, false);// ���
						this.getTTable(TABLE1).setLockCell(row, 2, false);// ҽ��
						this.getTTable(TABLE1).setLockCell(row, 3, false);// ����
						this.getTTable(TABLE1).setLockCell(row, 5, false);// �÷�
						//add by wukai 20160607 satrt
						this.getTTable(TABLE1).setLockCell(row, 6, false);
						this.getTTable(TABLE1).setLockCell(row, 7, false);
						//add by wukai 20160607 end
						this.getTTable(TABLE1).setLockCell(row, 8, false);// Ƶ��
						this.getTTable(TABLE1).setLockCell(row, 9, false);// ִ�п���
						this.getTTable(TABLE1).setLockCell(row, 10, false);// ����ʱ��
						this.getTTable(TABLE1).setLockCell(row, 11, false);// ҽ����ע
						this.getTTable(TABLE1).setLockCell(row, 13, false);// ������ʶ
						this.getTTable(TABLE1).setLockCell(row, 14, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 15, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 16, false);// ��
						this.getTTable(TABLE1).setLockCell(row, 17, false);// ��
					}
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE1).setDSValue(row);
					return false;
				}
			} else {
				this.getTTable(TABLE1).setLockCellRow(row, true);
				this.getTTable(TABLE1).setLockCell(row, 0, false);// ������
				this.getTTable(TABLE1).setLockCell(row, 1, false);// ���
				this.getTTable(TABLE1).setLockCell(row, 2, false);// ҽ��
				this.getTTable(TABLE1).setLockCell(row, 3, false);// ����
				this.getTTable(TABLE1).setLockCell(row, 5, false);// Ƶ��
				this.getTTable(TABLE1).setLockCell(row, 6, false);// ���뷽ʽ
				this.getTTable(TABLE1).setLockCell(row, 7, false);// ��Һ����
				this.getTTable(TABLE1).setLockCell(row, 8, false);// Ƶ��
				this.getTTable(TABLE1).setLockCell(row, 9, false);// ִ�п���
				this.getTTable(TABLE1).setLockCell(row, 10, false);// ����ʱ��
				this.getTTable(TABLE1).setLockCell(row, 11, false);// ҽ����ע
				this.getTTable(TABLE1).setLockCell(row, 13, false);// ������ʶ
				this.getTTable(TABLE1).setLockCell(row, 14, false);// ��
				this.getTTable(TABLE1).setLockCell(row, 15, false);// ��
				this.getTTable(TABLE1).setLockCell(row, 16, false);// ��
				this.getTTable(TABLE1).setLockCell(row, 17, false);// ��
				// �Ƿ��Ѿ�����������Ѿ�����ʾ��ȥ����������
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "ST")
						&& linkParm.getInt("LINK_NO") != 0) {
					// ��������Ѿ������
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("ST");
					// this.messageBox_(linkNo);
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE1).setDSValue(row);
					return true;
				}
			}
		}
		// this.messageBox("===============33333333====================");
		// $$================add by lx 2012/02/28 ����ҽ��У��
		// START========================$$//
		if (columnName.equals("ROUTE_CODE") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("CAT1_TYPE===="+parm.getValue("CAT1_TYPE"));
			if (parm.getValue("CAT1_TYPE").equals("RIS")
					|| parm.getValue("CAT1_TYPE").equals("LIS")) {

				this.messageBox("����������޸ģ�");
				return true;

			}

		}
		// $$================add by lx 2012/02/28
		// ����ҽ��У��END========================$$//
		// this.messageBox("===============444444444====================");

		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("��ǰѡ��������:"+parm);
			if (parm.getValue("ORDER_CODE").length() == 0) {
				// ��¼��ҽ����
				// ============xueyf modify 20120217 start
				if (Float.valueOf(parm.getValue("MEDI_QTY")) > 0) {
					this.messageBox("E0157");
				}
				// ============xueyf modify 20120217 stop
				return true;
			}
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// ����������
				if (columnName.equals("MEDI_QTY")) {
					parm.setData("MEDI_QTY", node.getValue());
				}
				if (columnName.equals("FREQ_CODE")) {
					// �ж�Ƶ���Ƿ��������ʱʹ��
					if (!OrderUtil.getInstance().isSTFreq(
							node.getValue().toString())) {
						// ������ʱ��ҩƵ�Σ�
						this.messageBox("E0158");
						return true;
					}
					// �Ƿ�������ҽ��
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						for (int i : newRow) {
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getTempStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// ����������
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"��ʾ��Ϣ Tips",
												"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											return true;
										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE1)
											.setDSValue(i);
											return false;
										}
									}
									this.messageBox(temp.getErrText());
									return true;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				TParm action = this.getTempStartQty(parm);
				action.setData("LINK_NO", parm.getData("LINK_NO"));
				if (action.getErrCode() < 0) {
					// ����������
					if (action.getErrCode() == -2) {
						if (messageBox("��ʾ��Ϣ Tips",
								"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else {// shibl 20130123 modify ������δ�ش�
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// ��ֵ
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// ��ֵ����
				this.getTTable(TABLE1).setDSValue(node.getRow());
				return false;
			}
			// this.messageBox("===============5555555555===================");

			// ��ҩƷ
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						// ��ҩƷ�������޸�����
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							parm.setData("MEDI_QTY", node.getValue());
							TParm actions = this.getTempStartQty(parm);
							actions.setData("LINK_NO", parm.getData("LINK_NO"));
							// this.messageBox_(actions);
							// ��ֵ
							odiObject.setItem(odiObject.getDS("ODI_ORDER"),
									node.getRow(), actions);
							// ��ֵ����
							this.getTTable(TABLE1).setDSValue(node.getRow());
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// �ҵ����˻������д�ҽ����ΨһID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// �ҵ����������д�ҽ����ΨһID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									TParm action = this
											.getTempStartQty(linkParm);
									action.setData("LINK_NO", linkParm
											.getData("LINK_NO"));
									// this.messageBox_(action);
									// ��ֵ
									odiObject.setItem(ds, i, action, buff);
									this.getTTable(TABLE1).setDSValue(i);
								}
							}
						} else {
							// ���ú����Ƽƻ�
							// ����������
							if (columnName.equals("MEDI_QTY")) {
								parm.setData("MEDI_QTY", node.getValue());
							}
							if (columnName.equals("FREQ_CODE")) {
								// �ж�Ƶ���Ƿ��������ʱʹ��
								if (!OrderUtil.getInstance().isSTFreq(
										node.getValue().toString())) {
									// ������ʱ��ҩƵ�Σ�
									this.messageBox("E0158");
									return true;
								}
								// �Ƿ�������ҽ��
								if (this.isLinkOrder(parm)) {
									int linkNo = parm.getInt("LINK_NO");
									TDS ds = odiObject.getDS("ODI_ORDER");
									String buff = ds.PRIMARY;
									int newRow[] = ds.getNewRows(buff);
									for (int i : newRow) {
										TParm linkParm = ds.getRowParm(i, buff);
										if (!ds.isActive(i, buff))
											continue;
										if (i == node.getRow())
											continue;
										if (linkParm.getInt("LINK_NO") == linkNo) {
											linkParm.setData("FREQ_CODE", node
													.getValue());
											linkParm.setData("#NEW#", true);
											TParm temp = this
													.getTempStartQty(linkParm);
											if (temp.getErrCode() < 0) {
												// ����������
												if (temp.getErrCode() == -2) {
													if (messageBox(
															"��ʾ��Ϣ Tips",
															"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
															this.YES_NO_OPTION) != 0)
														return true;
													else {
														temp
														.setData(
																"FREQ_CODE",
																node
																.getValue());
														odiObject.setItem(ds,
																i, temp, buff);
														this.getTTable(TABLE1)
														.setDSValue(i);
														return false;
													}
												}
												this.messageBox(temp
														.getErrText());
												return true;
											}
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE1)
											.setDSValue(i);
										}
									}
								}
								parm.setData("FREQ_CODE", node.getValue());
							}
							TParm action = this.getTempStartQty(parm);
							action.setData("LINK_NO", parm.getData("LINK_NO"));
							if (action.getErrCode() < 0) {
								// ����������
								if (action.getErrCode() == -2) {
									if (messageBox(
											"��ʾ��Ϣ Tips",
											"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
											this.YES_NO_OPTION) != 0)
										return true;
									else
										return false;
								}
								this.messageBox(action.getErrText());
								return true;
							}
							// ��ֵ
							odiObject.setItem(odiObject.getDS("ODI_ORDER"),
									node.getRow(), action);
							// ��ֵ����
							this.getTTable(TABLE1).setDSValue(node.getRow());
							return false;
						}
					}
				}
			}

			// this.messageBox("===============66666666===================");
			// ����ҽ��Ƶ���޸�(temperr)
			if (columnName.equals("FREQ_CODE")) {
				if (this.isLinkOrder(parm)) {
					int linkNo = parm.getInt("LINK_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					// �Ƿ�������
					if (parm.getBoolean("LINKMAIN_FLG")) {
						for (int i : newRow) {
							TParm temp = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (temp.getInt("LINK_NO") == linkNo) {
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
						}
					} else {
						parm.setData("FREQ_CODE", node.getValue());
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE1).setDSValue(node.getRow());
					}
				}
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// �ҵ����˻������д�ҽ����ΨһID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// �ҵ����������д�ҽ����ΨһID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// this.messageBox("===============77777777===================");
		// ����ҽ���÷��޸�
		// =============================================================================
		if (columnName.equals("ORDER_DR_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DR_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DR_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DR_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		if (columnName.equals("ORDER_DR_CODE")) {

			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DR_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DR_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DR_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}
		// ===========����ʱ�� add by huangjw
		if (columnName.equals("ORDER_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ORDER_DATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ORDER_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ORDER_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}
		// ===========ȷ��ʱ�� add by huangjw
		if (columnName.equals("NS_CHECK_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("NS_CHECK_DATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("NS_CHECK_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("NS_CHECK_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		// ====================================================================
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());

			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ROUTE_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		// this.messageBox("===============888888888===================");
		// EXEC_DEPT_CODE
		// ����ҽ��ִ�п����޸�(temperr)
		if (columnName.equals("EXEC_DEPT_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EXEC_DEPT_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("EXEC_DEPT_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EXEC_DEPT_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}

		//INFLUTION_RATE
		//����
		//add by chenhj
		if (columnName.equals("INFLUTION_RATE")){
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE1).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("INFLUTION_RATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
		}


		// this.messageBox("===============999999===================");

		// ����ʱ�䲻�ܳ����ײ�ʱ����ж�
		if (columnName.equals("EFF_DATEDAY")) {
			// System.out.println("===EFF_DATEDAY �޸���������  ===");

			if (!DateTool.checkDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss")) {
				// ʱ���ʽ����ȷ��
				this.messageBox("E0160");
				return true;
			}

			// $$================add by lx
			// 2012/02/29��ʱҽ������ʱ��Ӧ��С��סԺ����=================================$$//
			long leffDate = strToDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss").getTime();
			if (leffDate < this.getAdmDate().getTime()) {
				this.messageBox("����ʱ�䲻��С����Ժ���ڣ�");
				return true;
			}
			// $$================add by lx
			// 2012/02/29��ʱҽ������ʱ��Ӧ��С��סԺ����=================================$$//
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());

			// ==============add by lx
			// 2012/04/17�����Զ���ʱ��start========================$$//
			// �Ƿ�������ҽ��
			if (this.isLinkOrder(parm)) {
				// System.out.println("=====isLinkOrder======");
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EFF_DATE", node.getValue());
							temp.setData("EFF_DATEDAY", node.getValue());
							// -----------------------------shibl 20120606
							// modify start
							// ����ҽ��
							if (this.isOrderSet(temp)) {
								int groupNo = temp.getInt("ORDERSET_GROUP_NO");
								Object objT = odiObject
										.getAttribute(odiObject.OID_DSPN_TIME);
								TDS dsT = odiObject.getDS("ODI_ORDER");
								String buffT = dsT.isFilter() ? ds.FILTER
										: ds.PRIMARY;
								// �¼ӵ�����
								int newRowT[] = dsT.getNewRows(buffT);
								for (int j : newRowT) {
									TParm tempT = dsT.getRowParm(j, buffT);
									if (!dsT.isActive(j, buffT))
										continue;
									if (tempT.getInt("ORDERSET_GROUP_NO") == groupNo) {
										tempT.setData("EFF_DATE", node
												.getValue());
										tempT.setData("EFF_DATEDAY", node
												.getValue());
										odiObject.setItem(dsT, j, tempT, buffT);
										this.getTTable(TABLE1).setDSValue(i);
									}
								}
							} else {
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE1).setDSValue(i);
							}
							// -----------------------------shibl 20120606
							// modify end
						}
					}
				} else {
					parm.setData("EFF_DATE", node.getValue());
					parm.setData("EFF_DATEDAY", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE1).setDSValue(node.getRow());
				}
			}
			// ==============add by lx
			// 2012/04/17�����Զ���ʱ��end========================$$//
			if (this.isOrderSet(parm)) {
				// this.messageBox("==isOrderSet111111==");
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						// System.out.println("==============linkParmOld==================="+linkParm);
						linkParm.setData("EFF_DATE", node.getValue());
						// System.out.println("==============EFF_DATE==================="+node.getValue());
						// System.out.println("==============linkParmNew==================="+linkParm);
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE1).setDSValue(i);
					}
				}
			}
			node.getTable().getDataStore().setItem(node.getRow(),
					"EFF_DATEDAY", node.getValue().toString());
		}
		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("ҽ���Ѿ�չ���������޸�ҽ����ע");
				return true;
			}
		}
		return false;
	}












	/**
	 * �ж�ҽ���Ƿ��Ѿ����
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean checkOrderNSCheck(TParm parm, boolean ncCheckFlg) {
		boolean falg = false;
		if (ncCheckFlg) {
			TParm result = new TParm(this.getDBTool().select(
					"SELECT TEMPORARY_FLG FROM ODI_ORDER WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "'"));
			if (result.getErrCode() < 0)
				falg = true;
			if (result.getValue("TEMPORARY_FLG", 0).equals("Y"))
				falg = true;
		} else {
			String startDttm = StringTool.getString(parm
					.getTimestamp("START_DTTM"), "yyyyMMddHHmmss");
			// System.out.println("startDttm"+startDttm);
			TParm resultExe = new TParm(this.getDBTool().select(
					"SELECT NS_EXEC_DATE FROM ODI_DSPNM WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "' AND START_DTTM='"
							+ startDttm + "'"));
			if (resultExe.getErrCode() < 0)
				falg = true;
			if (resultExe.getTimestamp("NS_EXEC_DATE", 0) != null)
				falg = true;
		}
		return falg;
	}

	/**
	 * ������ʱ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getTempStartQty(TParm parm) {

		// System.out.println("=====getTempStartQty parm====="+parm);
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// ������
			parm.setData("ACUMDSPN_QTY", 0);
			// �ۼƿ�ҩ��
			parm.setData("ACUMMEDI_QTY", 0);
		}
		String effDate = StringTool.getString(parm.getTimestamp("EFF_DATE"),
				"yyyyMMddHHmmss");
		// �õ���ҩ���պ�����
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("EFF_DATE"));
		// System.out.println("===dispenseDttm==="+dispenseDttm);
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			// �����д���
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("��ҩʱ������:"+dispenseDttm.get(0)+"��ҩʱ������:"+dispenseDttm.get(1));
		// ����������
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// System.out.println(""+this.caseNo);
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop

		// System.out.println("==selLevelParm=="+selLevelParm);
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		// System.out.println("selLevel==="+selLevel+"====="+selLevel.getValue("SERVICE_LEVEL",
		// 0));
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		parm.setData("RX_KIND", "ST");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate,
				parm.getValue("DC_DATE"), dispenseDttm.get(0).toString(),
				dispenseDttm.get(1).toString(), parm, level);
		// this.messageBox_(startQty);
		// System.out.println(""+startQty);
		// �ײ�ʱ�� START_DTTM
		List startDate = (List) startQty.get(0);
		// System.out.println("======startDate====="+startDate);
		// ������Ҫ����//order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M���dispenseQty M_DISPENSE_QTY
		// M���dispenseUnit M_DISPENSE_UNIT
		// M���dosageQty M_DOSAGE_QTY
		// M���dosageUnit M_DOSAGE_UNIT
		// D���MediQty D_MEDI_QTY
		// D���MediUnit D_MEDI_UNIT
		// D���dosageQty D_DOSAGE_QTY
		// D���dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		// System.out.println("===otherData==="+otherData);
		if (StringUtil.isNullList(startDate)
				&& (otherData == null || otherData.isEmpty())) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}

		// �ײ�ʱ���
		result.setData("START_DTTM_LIST", startDate);
		// �ײ�ʱ��
		// this.messageBox_(startDate.get(0).toString()+":"+startDate.get(0).getClass());
		result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(0)
				.toString(), "yyyyMMddHHmm"));
		// ������
		result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// �����ҩ��
		result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// ������
		result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
		// �ۼƿ�ҩ��
		result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
		// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
		result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
		// ������λ
		result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
		// ��ҩ������ʵ�ʿۿ�����
		result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
		// ��ҩ��λ < ʵ�ʿۿ��� >
		result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
		// ��ҩ����
		result.setData("MEDI_QTY", otherData.get("D_MEDI_QTY"));
		// ��ҩ��λ
		result.setData("MEDI_UNIT", otherData.get("D_MEDI_UNIT"));
		// ��ҩ����
		result.setData("DOSAGE_QTY", otherData.get("D_DOSAGE_QTY"));
		// ��ҩ��λ�����䵥λ
		result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
		// ����ҩ�Ƿ���
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify ������δ�ش�
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * �����Ժ��ҩ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getOutHospStartQty(TParm parm) {
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// ������
			parm.setData("ACUMDSPN_QTY", 0);
			// �ۼƿ�ҩ��
			parm.setData("ACUMMEDI_QTY", 0);
		}
		// this.messageBox_("========="+parm);
		// .out.println("===parm==="+parm);
		TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
		// this.messageBox_("---------"+totParm);
		// ��ҩע��(��浥λ��ҩ)
		if (parm.getBoolean("GIVEBOX_FLG")) {
			// ��淢ҩ��λ
			result.setData("DOSAGE_UNIT", totParm.getData("DOSAGE_UNIT"));
			// ��������
			result.setData("DOSAGE_QTY", totParm.getData("TOT_QTY"));
			// ��ҩ����
			result.setData("DISPENSE_QTY", totParm
					.getData("QTY_FOR_STOCK_UNIT"));
			// ��ҩ��λ
			result.setData("DISPENSE_UNIT", totParm.getData("STOCK_UNIT"));
		} else {
			// ��ҩ��λ
			result.setData("DOSAGE_UNIT", totParm.getData("DOSAGE_UNIT"));
			// ��������
			result.setData("DOSAGE_QTY", totParm.getData("QTY"));
			// ��ҩ����
			result.setData("DISPENSE_QTY", totParm.getData("QTY"));
			// ��ҩ��λ
			result.setData("DISPENSE_UNIT", totParm.getData("DOSAGE_UNIT"));
		}
		// ����ҩ�Ƿ���
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) {// shibl 20130123
			// modify ������δ�ش�
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * ��Ժ��ҩ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getOutHospDosageQty(TParm parm) {
		TParm result = new TParm();
		TParm dosageQtyParm = TotQtyTool.getInstance().getTakeQty(parm);
		// this.messageBox("��Ժ��ҩ����������:"+dosageQtyParm);
		result.setData("MEDI_QTY", dosageQtyParm.getDouble("QTY"));
		return result;
	}

	/**
	 * ����ҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueUD(Object obj) {
		// this.messageBox("===========onChangeTableValueUD==================");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE2).getDataStore().getRowParm(selRow);
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// ���ҽ������
			clearRow("UD", selRow, "ORDER_DESC");
			this.getTTable(TABLE2).setDSValue(selRow);
		}
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			// �����
			int row = this.getTTable(TABLE2).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE2).getDataStore().getRowParm(
					row);
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("��ǰѡ��������:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// �뿪��ҽ��
				this.messageBox("E0152");
				return true;
			}
			// ����ҽ�����ܿ�����
			if (Integer.parseInt(node.getValue().toString()) > 0
					&& (isOrderSet(linkParm) || !linkParm.getValue("CAT1_TYPE")
							.equals("PHA"))) {
				this.messageBox("����ҽ�����ܿ�������");
				return true;
			}
			// this.messageBox("LINKMAIN_FLG=========================="+linkParm.getValue("LINKMAIN_FLG"));
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// �Ƿ�������Y���ǵ�ʱ�༭��ֵN�Զ�������
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "UD")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					// ȡ������ʱ��������ֶε�ֵ yanjing 20140217
					this
					.onClearLinkAnti(orderP.getValue("ORDER_CODE"),
							linkFlg);
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
					// 20130315
					// modify
					this.getTTable(TABLE2).setLockCellRow(row, true);
					this.getTTable(TABLE2).setLockCell(row, 0, false);// ������
					this.getTTable(TABLE2).setLockCell(row, 1, false);// ���
					this.getTTable(TABLE2).setLockCell(row, 2, false);// ҽ��
					this.getTTable(TABLE2).setLockCell(row, 3, false);// ����
					this.getTTable(TABLE2).setLockCell(row, 5, false);// Ƶ��
					this.getTTable(TABLE2).setLockCell(row, 6, false);// �÷�
					//modify by wukai 20160607 start
					this.getTTable(TABLE2).setLockCell(row, 7, false);// Ƶ��
					this.getTTable(TABLE2).setLockCell(row, 8, false);// �÷�
					//modify by wukai 20160607 end
					this.getTTable(TABLE2).setLockCell(row, 9, false);// ��
					this.getTTable(TABLE2).setLockCell(row, 10, false);// ��
					this.getTTable(TABLE2).setLockCell(row, 11, false);// ҽ����ע
					this.getTTable(TABLE2).setLockCell(row, 12, false);// ִ�п���
					this.getTTable(TABLE2).setLockCell(row, 13, false);// ����ʱ��
					this.getTTable(TABLE2).setLockCell(row, 14, false);// ͣ��ʱ��
					this.getTTable(TABLE2).setLockCell(row, 16, false);// ����
					this.getTTable(TABLE2).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"UD");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					islinkP.setData("EXEC_DEPT_CODE", linkP
							.getData("EXEC_DEPT_CODE"));
					// �ж������Ƿ��ǿ���ҩ�yanjing 20140214
					if (!linkP.getValue("ANTIBIOTIC_WAY").equals(null)
							&& !"".equals(linkP.getValue("ANTIBIOTIC_WAY"))) {// ����ҩ��ʱ��ֵ
						islinkP.setData("DC_DATE", linkP.getData("DC_DATE"));
						islinkP.setData("ANTIBIOTIC_WAY", linkP
								.getData("ANTIBIOTIC_WAY"));
						islinkP.setData("DC_DR_CODE", linkP
								.getData("DC_DR_CODE"));
						islinkP.setData("DC_DEPT_CODE", linkP
								.getData("DC_DEPT_CODE"));// ======20140901
						// YANJING
					}
					if (linkParm.getInt("LINK_NO") != 0) {
						this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
						// 20130315
						// modify
						this.getTTable(TABLE2).setLockCellRow(row, true);
						this.getTTable(TABLE2).setLockCell(row, 0, false);// ������
						this.getTTable(TABLE2).setLockCell(row, 1, false);// ���
						this.getTTable(TABLE2).setLockCell(row, 2, false);// ҽ��
						this.getTTable(TABLE2).setLockCell(row, 3, false);// ����
						//modify by wukai 20160607 start
						this.getTTable(TABLE2).setLockCell(row, 7, false);// Ƶ��
						this.getTTable(TABLE2).setLockCell(row, 8, false);// �÷�
						//modify by wukai 20160607 end
						this.getTTable(TABLE2).setLockCell(row, 9, false);// ��
						this.getTTable(TABLE2).setLockCell(row, 10, false);// ��
						this.getTTable(TABLE2).setLockCell(row, 11, false);// ҽ����ע
						this.getTTable(TABLE2).setLockCell(row, 14, false);// ͣ��ʱ��
						this.getTTable(TABLE2).setLockCell(row, 16, false);// ����

					} else {
						// ִ�п������¸�ֵ
						islinkP.setData("EXEC_DEPT_CODE", this
								.getExeDeptCodeUD(linkParm
										.getValue("ORDER_CODE"), row, TABLE2,
										""));
						islinkP.setData("ROUTE_CODE", getMediQty(new TParm(),
								linkParm).getValue("ROUTE_CODE", 0));
						// ȡ������ʱ��������ֶε�ֵ yanjing 20140217
						this.onClearLinkAnti(orderP.getValue("ORDER_CODE"),
								islinkP);
						this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
						// 20130315
						// modify
						this.getTTable(TABLE2).setLockCellRow(row, true);
						this.getTTable(TABLE2).setLockCell(row, 0, false);// ������
						this.getTTable(TABLE2).setLockCell(row, 1, false);// ���
						this.getTTable(TABLE2).setLockCell(row, 2, false);// ҽ��
						this.getTTable(TABLE2).setLockCell(row, 3, false);// ����
						this.getTTable(TABLE2).setLockCell(row, 5, false);// Ƶ��
						this.getTTable(TABLE2).setLockCell(row, 6, false);// �÷�
						//modify by wukai 20160607 start
						this.getTTable(TABLE2).setLockCell(row, 7, false);// Ƶ��
						this.getTTable(TABLE2).setLockCell(row, 8, false);// �÷�
						//modify by wukai 20160607 end
						this.getTTable(TABLE2).setLockCell(row, 9, false);// ��
						this.getTTable(TABLE2).setLockCell(row, 10, false);// ��
						this.getTTable(TABLE2).setLockCell(row, 11, false);// ҽ����ע
						this.getTTable(TABLE2).setLockCell(row, 12, false);// ִ�п���
						this.getTTable(TABLE2).setLockCell(row, 13, false);// ����ʱ��
						this.getTTable(TABLE2).setLockCell(row, 14, false);// ͣ��ʱ��
						this.getTTable(TABLE2).setLockCell(row, 16, false);// ����
					}
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE2).setDSValue(row);
					return false;
				}
			} else {
				this.getTTable(TABLE2).setLockCellRow(row, true); // shibl
				// 20130315
				// modify
				this.getTTable(TABLE2).setLockCellRow(row, true);
				this.getTTable(TABLE2).setLockCell(row, 0, false);// ������
				this.getTTable(TABLE2).setLockCell(row, 1, false);// ���
				this.getTTable(TABLE2).setLockCell(row, 2, false);// ҽ��
				this.getTTable(TABLE2).setLockCell(row, 3, false);// ����
				this.getTTable(TABLE2).setLockCell(row, 5, false);// Ƶ��
				this.getTTable(TABLE2).setLockCell(row, 6, false);// �÷�
				//modify by wukai 20160607 start
				this.getTTable(TABLE2).setLockCell(row, 7, false);// Ƶ��
				this.getTTable(TABLE2).setLockCell(row, 8, false);// �÷�
				//modify by wukai 20160607 end
				this.getTTable(TABLE2).setLockCell(row, 9, false);// ��
				this.getTTable(TABLE2).setLockCell(row, 10, false);// ��
				this.getTTable(TABLE2).setLockCell(row, 11, false);// ҽ����ע
				this.getTTable(TABLE2).setLockCell(row, 12, false);// ִ�п���
				this.getTTable(TABLE2).setLockCell(row, 14, false);// ͣ��ʱ��
				this.getTTable(TABLE2).setLockCell(row, 16, false);// ����
				// �Ƿ��Ѿ�����������Ѿ�����ʾ��ȥ����������
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "UD")
						&& linkParm.getInt("LINK_NO") != 0) {
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("UD");
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE2).setDSValue(row);
					// return false;
					return true;
				}
			}
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("��ǰѡ��������:" + parm);
			// ��¼��ҽ����
			/**
			 * if (Float.valueOf(parm.getValue("MEDI_QTY")) > 0) {
			 * this.messageBox("E0157"); return true; }
			 **/
			if (parm.getValue("ORDER_CODE").length() == 0) {
				this.messageBox("E0157");
				return true;
			}

			if (columnName.equals("FREQ_CODE")) {
				// this.messageBox("====FREQ_CODE=====");
				// add by lx ���ڲ�����STAT
				if (isStat((String) node.getValue())) {
					this.messageBox("����ҽ���������ʹ�ã�");
					return true;
				}

			}

			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// ����������
				if (columnName.equals("MEDI_QTY"))
					parm.setData("MEDI_QTY", node.getValue());
				if (columnName.equals("FREQ_CODE")) {
					// �Ƿ�������ҽ��
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						// this.messageBox("==newRow=="+newRow);

						for (int i : newRow) {
							// System.out.println("=====i======"+i);
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getLongStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// ����������
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"��ʾ��Ϣ Tips",
												"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											// return true;
											continue;

										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE2)
											.setDSValue(i);
											// return false;
											continue;
										}
									}
									this.messageBox(temp.getErrText());
									// return true;
									continue;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE2).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				// this.messageBox_(parm);
				TParm action = this.getLongStartQty(parm);
				if (action.getErrCode() < 0) {
					// ����������
					if (action.getErrCode() == -2) {
						if (messageBox("��ʾ��Ϣ Tips",
								"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else {// shibl 20130123 modify ������δ�ش�
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// ��ֵ
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// ��ֵ����
				this.getTTable(TABLE2).setDSValue(node.getRow());
				return false;
			}
			// ��ҩƷ
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						// ��ҩƷ�������޸�����
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// �ҵ����˻������д�ҽ����ΨһID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// �ҵ����������д�ҽ����ΨһID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									this.getTTable(TABLE2).setDSValue(i);
								}
							}
						}
					}
				}
			}
			// ����ҽ��Ƶ���޸�(temperr)
			if (columnName.equals("FREQ_CODE")) {
				if (this.isLinkOrder(parm)) {
					int linkNo = parm.getInt("LINK_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					// �Ƿ�������
					if (parm.getBoolean("LINKMAIN_FLG")) {
						for (int i : newRow) {
							TParm temp = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (temp.getInt("LINK_NO") == linkNo) {
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE2).setDSValue(i);
							}
						}
					} else {
						parm.setData("FREQ_CODE", node.getValue());
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE2).setDSValue(node.getRow());
					}
				}
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// �ҵ����˻������д�ҽ����ΨһID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// �ҵ����������д�ҽ����ΨһID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// ����ҽ���÷��޸�
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("===ROUTE_CODE===");

			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			// ����ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("ROUTE_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
			}
		}
		// EXEC_DEPT_CODE
		// ����ҽ��ִ�п����޸�(temperr)
		if (columnName.equals("EXEC_DEPT_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EXEC_DEPT_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("EXEC_DEPT_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EXEC_DEPT_CODE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
			}
		}

		//INFLUTION_RATE
		//����һ��
		//add by chenhj
		if (columnName.equals("INFLUTION_RATE")){
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// �Ƿ�������ҽ��
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}

		// ====yanjing 20140616 ����ʱ�޸�ͣ��ʱ�䱣��һ�� start
		if (columnName.equals("DC_DATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// �Ƿ�������ҽ��
			if (this.isLinkOrder(parm)) {

				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				// System.out.println("parm parm is ::"+parm);
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("DC_DATE", node.getValue());
							temp.setData("DC_DR_CODE", Operator.getID());
							temp.setData("DC_DEPT_CODE", Operator.getDept());// ====pangben
							// 2015-1-15
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("DC_DATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}
		// ====yanjing 20140616 ����ʱ�޸�ͣ��ʱ�䱣��һ�� end

		// ����ʱ�䲻�ܳ����ײ�ʱ����ж�
		if (columnName.equals("EFF_DATEDAY")) {
			// this.messageBox("==come in EFF_DATEDAY==");
			if (!DateTool.checkDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss")) {
				this.messageBox("E0160");
				return true;
			}
			// $$================add by lx
			// 2012/03/29��ʱҽ������ʱ��Ӧ��С��סԺ����START=================================$$//
			long leffDate = strToDate(node.getValue().toString(),
					"yyyy/MM/dd HH:mm:ss").getTime();
			if (leffDate < this.getAdmDate().getTime()) {
				this.messageBox("����ʱ�䲻��С����Ժ���ڣ�");
				return true;
			}
			// $$================add by lx
			// 2012/03/29��ʱҽ������ʱ��Ӧ��С��סԺ����END=================================$$//
			// add by lx ����ʱ�䲻�ܴ��ڵ�ǰʱ��
			// if (leffDate > new Date().getTime()) {
			// this.messageBox("����ʱ�䲻�ܴ��ڵ�ǰʱ�䣡");
			// return true;
			// }

			// �õ�סԺ��ҩʱ��
			String timeBY = odiObject.getAttribute(odiObject.OID_DSPN_TIME)
					.toString();
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			// this.messageBox("RX_KIND===="+parm.getValue("RX_KIND"));

			if ("UD".equals(parm.getValue("RX_KIND"))) {
				// ����ʱ�䲻�ܳ����ײ�ʱ��
				// this.messageBox_(node.getValue().toString());
				long effDate = strToDate(node.getValue().toString(),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// this.messageBox_(parm.getData("START_DTTM")+":"+parm.getData("START_DTTM").getClass());
				// System.out.println("---------dsss----------"+parm);
				/**
				 * String sDt = StringTool.getString(
				 * parm.getTimestamp("START_DTTM"), "yyyy/MM/dd HH:mm:ss");
				 * 
				 * long sDttm = strToDate(sDt, "yyyy/MM/dd HH:mm:ss").getTime();
				 **/

				// �õ��հ�ҩʱ���
				Timestamp nowTime = SystemTool.getInstance().getDate();
				String nowTimeStr = StringTool.getString(nowTime,
						"yyyyMMddHHmmss");
				// this.messageBox_("��ǰʱ��:" + nowTimeStr);
				String nowTimeBY = StringTool.getString(nowTime, "yyyyMMdd")
						+ timeBY + "00";
				// this.messageBox_("���հ�ҩʱ��:" + nowTimeBY);
				// Ԥ��ʱ���ײ�ʱ��
				// this.messageBox_(node.getValue().getClass()+":"+node.getValue().toString());
				Timestamp ykTime = StringTool.getTimestamp(node.getValue()
						.toString(), "yyyy/MM/dd HH:mm:ss");
				String ykTimeStr = StringTool.getString(ykTime,
						"yyyyMMddHHmmss");
				// this.messageBox_("Ԥ��ʱ���ײ�ʱ��:" + ykTimeStr);
				// String tomorrowTime =
				// StringTool.getString(StringTool.rollDate(
				// nowTime, 1), "yyyyMMdd")
				// + "000000";

				String yestodayTime = StringTool.getString(StringTool.rollDate(
						nowTime, -1), "yyyyMMdd HH:mm:ss");
				// System.out.println(yestodayTime+"-----------------"+StringTool.rollDate(nowTime,
				// -1));
				// this.messageBox_("0��ʱ��:"+tomorrowTime);
				// ��ҩƷ
				// this.messageBox("cat1_type"+parm.getValue("CAT1_TYPE"));
				if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
					/**
					 * if (effDate > sDttm) { if
					 * ("en".equals(this.getLanguage())) { this.messageBox(
					 * "Opening time cannot exceed the meal time.:" + sDt +
					 * "��"); } else { this.messageBox("����ʱ�䲻�ܳ����ײ�ʱ��:" + sDt +
					 * "��"); } return true; }
					 **/

					// ��ǰʱ��������ڵ��ڵ��հ�ҩʱ��
					if (nowTimeStr.compareTo(nowTimeBY) > 0) {
						// Ԥ��ʱ��С�ڵ��հ�ҩʱ��
						if (ykTimeStr.compareTo(nowTimeBY) <= 0) {
							// this.messageBox("===Ԥ��ʱ��С�ڵ��հ�ҩʱ��===");
							if ("en".equals(this.getLanguage())) {
								this
								.messageBox("Opening time cannot put less time:"
										+ StringTool
										.getString(
												StringTool
												.getTimestamp(
														nowTimeBY,
														"yyyyMMddHHmmss"),
												"yyyy/MM/dd HH:mm:ss"));
							} else {
								this.messageBox("ҽ������ʱ�䲻��С�ڽ���סԺ��ҩʱ��:"
										+ StringTool.getString(StringTool
												.getTimestamp(nowTimeBY,
														"yyyyMMddHHmmss"),
												"yyyy/MM/dd HH:mm:ss"));
							}

							return true;
						}
					} else {

						// this.messageBox("===����ʱ��<ǰһ���ʱ��===");
						// ����ʱ��<ǰһ���ʱ��
						if (effDate < strToDate(yestodayTime,
								"yyyyMMdd HH:mm:ss").getTime()) {
							this.messageBox("ҽ������ʱ�䲻��С������סԺ��ҩʱ��:"
									+ StringTool.getString(StringTool
											.getTimestamp(nowTimeBY,
													"yyyyMMddHHmmss"),
											"yyyy/MM/dd HH:mm:ss"));

							return true;
						}
						// return true;
					}

				}
				// else {
				// // this.messageBox("===����DATE�Ƿ��Ѿ���������0��===");
				// // ����EFF_DATE�Ƿ��Ѿ���������0��
				// if (ykTimeStr.compareTo(tomorrowTime) > 0) {
				// if ("en".equals(this.getLanguage())) {
				// this.messageBox("Opening time cannot cross:"
				// + StringTool.getString(StringTool
				// .getTimestamp(tomorrowTime,
				// "yyyyMMddHHmmss"),
				// "yyyy/MM/dd HH:mm:ss"));
				// }
				// else {
				// this.messageBox("ҽ������ʱ�䲻�ܿ���������:"
				// + StringTool.getString(StringTool
				// .getTimestamp(tomorrowTime,
				// "yyyyMMddHHmmss"),
				// "yyyy/MM/dd HH:mm:ss"));
				// }
				// return true;
				// }
				// }
			}

			// this.messageBox("�Ƿ�������ҽ��"+this.isLinkOrder(parm));
			// �Ƿ�������ҽ��
			if (this.isLinkOrder(parm)) {

				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("EFF_DATE", node.getValue());
							temp.setData("EFF_DATEDAY", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE2).setDSValue(i);
						}
					}
				} else {
					parm.setData("EFF_DATE", node.getValue());
					parm.setData("EFF_DATEDAY", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
			// ����ҽ��
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
						linkParm.setData("EFF_DATE", node.getValue());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					}
				}
				node.getTable().getDataStore().setItem(node.getRow(),
						"EFF_DATEDAY", node.getValue().toString());
			}
		}
		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("ҽ���Ѿ�չ���������޸�ҽ����ע");
				return true;
			}
		}
		if (columnName.equals("DC_DATE")) {
			TDS ds = odiObject.getDS("ODI_ORDER");
			String buff = ds.PRIMARY;
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			String value = String.valueOf(node.getValue());
			long dcDate = ((Timestamp) node.getValue()).getTime();
			Timestamp ykTime = parm.getTimestamp("EFF_DATE");
			if (dcDate < ykTime.getTime()) {
				this.messageBox("ͣ��ʱ�䲻��С������ʱ�䣡");
				return false;
			}
			if (this.isOrderSet(parm)) {
				int groupNo = parm.getInt("ORDERSET_GROUP_NO");
				int newRow[] = ds.getNewRows(buff);
				for (int i : newRow) {
					TParm linkParm = ds.getRowParm(i, buff);
					if (!ds.isActive(i, buff))
						continue;
					// �ҵ����˻������д�ҽ����ΨһID
					int filterId = (Integer) ds.getItemData(i, "#ID#", buff);
					// �ҵ����������д�ҽ����ΨһID
					int primaryId = (Integer) ds.getItemData(node.getRow(),
							"#ID#", ds.PRIMARY);
					if (filterId == primaryId)
						continue;
					if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo
							&& !value.equals("") && !value.equals("null")) {
						linkParm.setData("DC_DR_CODE", Operator.getID());
						linkParm.setData("DC_DEPT_CODE", Operator.getDept());
						odiObject.setItem(ds, i, linkParm, buff);
						this.getTTable(TABLE2).setDSValue(i);
					} else if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo
							&& (value.equals("") || value.equals("null"))) {
						linkParm.setData("DC_DR_CODE", "");
						linkParm.setData("DC_DEPT_CODE", "");
						odiObject.setItem(ds, node.getRow(), parm, buff);
						this.getTTable(TABLE2).setDSValue(node.getRow());
					}
				}
			} else {
				if (!value.equals("") && !value.equals("null")) {
					parm.setData("DC_DR_CODE", Operator.getID());
					parm.setData("DC_DEPT_CODE", Operator.getDept());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				} else {
					parm.setData("DC_DR_CODE", "");
					parm.setData("DC_DEPT_CODE", "");
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE2).setDSValue(node.getRow());
				}
			}
		}
		return false;
	}

	/**
	 * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate, String forMat) {
		SimpleDateFormat formatter = new SimpleDateFormat(forMat);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * ��Ժ��ҩ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueDS(Object obj) {

		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE3).getDataStore().getRowParm(selRow);
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// ���ҽ������
			clearRow("DS", selRow, "ORDER_DESC");
			this.getTTable(TABLE3).setDSValue(selRow);
		}
		if ("LINK_NO".equals(columnName)) {
			if (Integer.parseInt(node.getValue().toString()) == 0) {
				node.setValue("");
			}
			// �����
			int row = this.getTTable(TABLE3).getSelectedRow();
			TParm linkParm = this.getTTable(TABLE3).getDataStore().getRowParm(
					row);
			linkParm.setData("LINK_NO", node.getValue());
			// this.messageBox("��ǰѡ��������:"+linkParm);
			if (linkParm.getValue("ORDER_CODE").length() == 0) {
				// �뿪��ҽ��
				this.messageBox("E0152");
				return true;
			}
			if ("N".equals(linkParm.getValue("LINKMAIN_FLG"))) {
				// �Ƿ�������Y���ǵ�ʱ�༭��ֵN�Զ�������
				if (!checkMainLinkItem(linkParm.getInt("LINK_NO"), false, "DS")
						&& linkParm.getInt("LINK_NO") != 0) {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkFlg = new TParm();
					linkFlg.setData("LINKMAIN_FLG", "Y");
					odiObject.setItem(ds, row, linkFlg);
					this.getTTable(TABLE3).setDSValue(row);
					return false;
				} else {
					TDS ds = odiObject.getDS("ODI_ORDER");
					TParm linkP = getMainLinkOrder(linkParm.getInt("LINK_NO"),
							"DS");
					TParm islinkP = new TParm();
					islinkP.setData("FREQ_CODE", linkP.getData("FREQ_CODE"));
					islinkP.setData("ROUTE_CODE", linkP.getData("ROUTE_CODE"));
					islinkP.setData("EXEC_DEPT_CODE", linkP
							.getData("EXEC_DEPT_CODE"));
					odiObject.setItem(ds, row, islinkP);
					this.getTTable(TABLE3).setDSValue(row);
					return false;
				}
			} else {
				// �Ƿ��Ѿ�����������Ѿ�����ʾ��ȥ����������
				if (checkMainLinkItem(linkParm.getInt("LINK_NO"), true, "DS")
						&& linkParm.getInt("LINK_NO") != 0) {
					this.messageBox("E0156");
					// int linkNo = getMaxLinkNo("DS");
					// TDS ds = odiObject.getDS("ODI_ORDER");
					// TParm linkFlg = new TParm();
					// linkFlg.setData("LINK_NO",linkNo);
					// odiObject.setItem(ds, row, linkFlg);
					// this.getTTable(TABLE3).setDSValue(row);
					return false;
				}
			}
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")
				|| columnName.equals("TAKE_DAYS")
				|| columnName.equals("DISPENSE_QTY")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			parm.setData("#NEW#", true);
			// this.messageBox("��ǰѡ��������:"+parm);
			if (parm.getValue("ORDER_CODE").length() == 0) {
				this.messageBox("E0157");
				return true;
			}
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				// ����������
				if (columnName.equals("MEDI_QTY"))
					parm.setData("MEDI_QTY", node.getValue());
				// �޸�Ƶ��
				if (columnName.equals("FREQ_CODE")) {
					// �Ƿ�������ҽ��
					if (this.isLinkOrder(parm)) {
						int linkNo = parm.getInt("LINK_NO");
						TDS ds = odiObject.getDS("ODI_ORDER");
						String buff = ds.PRIMARY;
						int newRow[] = ds.getNewRows(buff);
						for (int i : newRow) {
							TParm linkParm = ds.getRowParm(i, buff);
							if (!ds.isActive(i, buff))
								continue;
							if (i == node.getRow())
								continue;
							if (linkParm.getInt("LINK_NO") == linkNo) {
								linkParm.setData("FREQ_CODE", node.getValue());
								linkParm.setData("#NEW#", true);
								TParm temp = this.getOutHospStartQty(linkParm);
								if (temp.getErrCode() < 0) {
									// ����������
									if (temp.getErrCode() == -2) {
										if (messageBox(
												"��ʾ��Ϣ Tips",
												"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
												this.YES_NO_OPTION) != 0)
											return true;
										else {
											temp.setData("FREQ_CODE", node
													.getValue());
											odiObject
											.setItem(ds, i, temp, buff);
											this.getTTable(TABLE3)
											.setDSValue(i);
											return false;
										}
									}
									this.messageBox(temp.getErrText());
									return true;
								}
								temp.setData("FREQ_CODE", node.getValue());
								odiObject.setItem(ds, i, temp, buff);
								this.getTTable(TABLE3).setDSValue(i);
							}
						}
					}
					parm.setData("FREQ_CODE", node.getValue());
				}
				// �޸�����
				if (columnName.equals("TAKE_DAYS")) {
					// �жϿ�����
					if (parm.getValue("ANTIBIOTIC_CODE").length() != 0) {
						int day = OrderUtil.getInstance().checkAntibioticDay(
								parm.getValue("ANTIBIOTIC_CODE"));
						int cDay = StringTool
								.getInt(node.getValue().toString());
						if (day != -1 && cDay > day) {
							if ("en".equals(this.getLanguage())) {
								this
								.messageBox(parm
										.getValue("ORDER_ENG_DESC")
										+ ":Beyond the default number of antibiotics:"
										+ day + "days��");
							} else {
								this.messageBox(parm.getValue("ORDER_DESC")
										+ ":�����س�����Ԥ������" + day + "�죡");
							}
							return true;
						}
					}
					parm.setData("TAKE_DAYS", node.getValue());
				}
				// �޸�����
				if (columnName.equals("DISPENSE_QTY")) {
					parm.setData("DOSAGE_QTY", node.getValue());
					// ������������������
					TParm dosageQtyParm = this.getOutHospDosageQty(parm);
					if (dosageQtyParm.getErrCode() < 0) {
						this.messageBox(dosageQtyParm.getErrText());
						return true;
					}
					dosageQtyParm.setData("DOSAGE_QTY", node.getValue());
					// ��ֵ
					odiObject.setItem(odiObject.getDS("ODI_ORDER"), node
							.getRow(), dosageQtyParm);
					// ��ֵ����
					this.getTTable(TABLE3).setDSValue(node.getRow());
					return false;
				}
				TParm action = this.getOutHospStartQty(parm);
				if (action.getErrCode() < 0) {
					// ����������
					if (action.getErrCode() == -2) {
						if (messageBox("��ʾ��Ϣ Tips",
								"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return true;
					} else { // shibl 20130123 modify ������δ�ش�
						this.messageBox(action.getErrText());
						return true;
					}
				}
				// ��ֵ
				odiObject.setItem(odiObject.getDS("ODI_ORDER"), node.getRow(),
						action);
				// ��ֵ����
				this.getTTable(TABLE3).setDSValue(node.getRow());
				return false;
			}
			// ��ҩƷ
			if (!parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (columnName.equals("MEDI_QTY")) {
					if (parm.getValue("CAT1_TYPE").equals("RIS")
							|| parm.getValue("CAT1_TYPE").equals("LIS")) {
						this.messageBox("E0159");
						return true;
					} else {
						if (this.isOrderSet(parm)) {
							int groupNo = parm.getInt("ORDERSET_GROUP_NO");
							TDS ds = odiObject.getDS("ODI_ORDER");
							String buff = ds.isFilter() ? ds.FILTER
									: ds.PRIMARY;
							int newRow[] = ds.getNewRows(buff);
							for (int i : newRow) {
								TParm linkParm = ds.getRowParm(i, buff);
								if (!ds.isActive(i, buff))
									continue;
								// �ҵ����˻������д�ҽ����ΨһID
								int filterId = (Integer) ds.getItemData(i,
										"#ID#", buff);
								// �ҵ����������д�ҽ����ΨһID
								int primaryId = (Integer) ds.getItemData(node
										.getRow(), "#ID#", ds.PRIMARY);
								if (filterId == primaryId)
									continue;
								if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
									linkParm.setData("MEDI_QTY", node
											.getValue());
									odiObject.setItem(ds, i, linkParm, buff);
									this.getTTable(TABLE3).setDSValue(i);
								}
							}
						}
					}
				}
			}


			// ����ҽ��Ƶ���޸�
			if (columnName.equals("FREQ_CODE")) {
				if (this.isOrderSet(parm)) {
					int groupNo = parm.getInt("ORDERSET_GROUP_NO");
					TDS ds = odiObject.getDS("ODI_ORDER");
					String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
					int newRow[] = ds.getNewRows(buff);
					for (int i : newRow) {
						TParm linkParm = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						// �ҵ����˻������д�ҽ����ΨһID
						int filterId = (Integer) ds
								.getItemData(i, "#ID#", buff);
						// �ҵ����������д�ҽ����ΨһID
						int primaryId = (Integer) ds.getItemData(node.getRow(),
								"#ID#", ds.PRIMARY);
						if (filterId == primaryId)
							continue;
						if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
							linkParm.setData("FREQ_CODE", node.getValue());
							odiObject.setItem(ds, i, linkParm, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				}
			}
			return false;
		}
		// ����ҽ���÷��޸�
		if (columnName.equals("ROUTE_CODE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("ROUTE_CODE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				} else {
					parm.setData("ROUTE_CODE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE3).setDSValue(node.getRow());
				}
			}
		}

		// ����ҽ�������޸�
		//add by chenhj 2017.4.10
		if (columnName.equals("INFLUTION_RATE")) {
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			if (this.isLinkOrder(parm)) {
				int linkNo = parm.getInt("LINK_NO");
				TDS ds = odiObject.getDS("ODI_ORDER");
				String buff = ds.PRIMARY;
				int newRow[] = ds.getNewRows(buff);
				// �Ƿ�������
				if (parm.getBoolean("LINKMAIN_FLG")) {
					for (int i : newRow) {
						TParm temp = ds.getRowParm(i, buff);
						if (!ds.isActive(i, buff))
							continue;
						if (temp.getInt("LINK_NO") == linkNo) {
							temp.setData("INFLUTION_RATE", node.getValue());
							odiObject.setItem(ds, i, temp, buff);
							this.getTTable(TABLE3).setDSValue(i);
						}
					}
				} else {
					parm.setData("INFLUTION_RATE", node.getValue());
					odiObject.setItem(ds, node.getRow(), parm, buff);
					this.getTTable(TABLE3).setDSValue(node.getRow());
				}
			}
		}



		if (columnName.equals("DR_NOTE")) {// shibl 20130205 add
			TParm parm = node.getTable().getDataStore().getRowParm(
					node.getRow());
			boolean nsCheckFlg = odiObject
					.getAttributeBoolean("INW_NS_CHECK_FLG");
			if (checkOrderNSCheck(parm, nsCheckFlg)) {
				this.messageBox("ҽ���Ѿ�չ���������޸�ҽ����ע");
				return true;
			}
		}
		return false;
	}

	/**
	 * ��Ժ��ҩ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableValueIG(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = this.getFactColumnName(TABLE4, node.getColumn());
		String columnArray[] = columnName.split("_");
		TParm columnParm = this.getTTable(TABLE4).getParmValue().getRow(
				node.getRow());
		// ��ǰ�༭��
		int onlyEditRow = getIGEditRowSetId(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		// ��ǰ�༭������
		TParm rowTParm = getIGEditRowSetParm(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		TDS dsIG = odiObject.getDS("ODI_ORDER");
		// ����
		if (columnName.contains("MEDI_QTY")) {
			TParm igParm = new TParm();
			igParm.setData("MEDI_QTY", node.getValue());
			rowTParm.setData("MEDI_QTY", node.getValue());
			rowTParm.setData("TAKE_DAYS", this.getValueInt("RF"));
			// ���������Ժ��ҩ
			if ("PHA".equals(rowTParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(rowTParm.getValue("IS_REMARK"))) {
				rowTParm.setData("#NEW#", true);
				TParm igIndParm = getOutHospStartQty(rowTParm);
				// ��������
				igParm.setData("DOSAGE_QTY", igIndParm.getData("DOSAGE_QTY"));
				// ��ҩ����
				igParm.setData("DISPENSE_QTY", igIndParm
						.getData("DISPENSE_QTY"));
			}
			odiObject.setItem(dsIG, onlyEditRow, igParm);
			// �����ܿ���
			this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(dsIG));
		}
		// ����巨
		if (columnName.contains("DCTEXCEP_CODE")) {
			TParm igParm = new TParm();
			igParm.setData("DCTEXCEP_CODE", node.getValue());
			odiObject.setItem(dsIG, onlyEditRow, igParm);
		}

		return false;
	}

	/**
	 * ���㳤��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getLongStartQty(TParm parm) {
		TParm result = new TParm();
		if (parm.getBoolean("#NEW#")) {
			// ������
			parm.setData("ACUMDSPN_QTY", 0);
			// �ۼƿ�ҩ��
			parm.setData("ACUMMEDI_QTY", 0);
		}
		// this.messageBox_(parm.getData("EFF_DATE"));
		// StringTool.getTimestamp(parm.getValue("EFF_DATE"),"yyyyMMddHHmmss");
		String effDate = StringTool.getString(parm.getTimestamp("EFF_DATE"),
				"yyyyMMddHHmmss");
		// this.messageBox_(effDate);
		// �õ���ҩ���պ�����
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		// System.out.println("==="+parm.getTimestamp("EFF_DATE"));
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("EFF_DATE"));
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("��ҩʱ������:"+dispenseDttm.get(0)+"��ҩʱ������:"+dispenseDttm.get(1));
		// ����������
		// this.messageBox("�������:"+parm);
		// this.messageBox_(dispenseDttm.get(0).toString());
		// this.messageBox_(dispenseDttm.get(1).toString());
		// this.messageBox_(parm.getValue("DC_DATE"));
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		String dcDate = parm.getTimestamp("DC_DATE") == null ? "" : StringTool
				.getString(parm.getTimestamp("DC_DATE"), "yyyyMMddHHmmss");
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate, dcDate,
				dispenseDttm.get(0).toString(), dispenseDttm.get(1).toString(),
				parm, level);
		// this.messageBox(""+startQty);
		if (StringUtil.isNullList(startQty)) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// �ײ�ʱ�� START_DTTM
		List startDate = (List) startQty.get(0);
		// ������Ҫ����//order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M���dispenseQty M_DISPENSE_QTY
		// M���dispenseUnit M_DISPENSE_UNIT
		// M���dosageQty M_DOSAGE_QTY
		// M���dosageUnit M_DOSAGE_UNIT
		// D���MediQty D_MEDI_QTY
		// D���MediUnit D_MEDI_UNIT
		// D���dosageQty D_DOSAGE_QTY
		// D���dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		if (otherData == null || otherData.isEmpty()) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		// �ײ�ʱ���
		result.setData("START_DTTM_LIST", startDate);

		// this.messageBox("====startDate===="+startDate);
		// this.messageBox_(startDate.size()+""+StringUtil.isNullList(startDate));
		if (!StringUtil.isNullList(startDate)) {
			// this.messageBox_("============"+startDate.get(0)+":"+startDate.get(0).getClass());
			// �ײ�ʱ��
			result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(
					0).toString(), "yyyyMMddHHmm"));
			// ������
			// this.messageBox_("������"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
			// RX_KINDΪF
			result.setData("RX_KIND", "F");
			// �����ҩ��
			// this.messageBox_("�����ҩ��"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
			// ������
			// this.messageBox_("ORDER_ACUMDSPN_QTY:"+otherData.get("ORDER_ACUMDSPN_QTY"));
			result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
			// �ۼƿ�ҩ��
			// this.messageBox_("ORDER_ACUMMEDI_QTY:"+otherData.get("ORDER_ACUMMEDI_QTY"));
			result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
			// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
			// this.messageBox_("��ҩ����"+otherData.get("M_DISPENSE_QTY"));
			result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
			// ������λ(��ҩ����)
			// this.messageBox_("������λ"+otherData.get("M_DISPENSE_UNIT"));
			result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
			// ��ҩ������ʵ�ʿۿ�����
			// this.messageBox_("��ҩ����:"+otherData.get("M_DOSAGE_QTY"));
			result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
			// ��ҩ��λ < ʵ�ʿۿ��� >
			// this.messageBox_("��ҩ��λ:"+otherData.get("M_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", otherData.get("M_DOSAGE_UNIT"));
			// ��ҩ����
			// this.messageBox_("��ҩ����:"+otherData.get("D_MEDI_QTY"));
			// result.setData("MEDI_QTY",otherData.get("D_MEDI_QTY"));
			// ��ҩ��λ
			// this.messageBox_("��ҩ��λ"+otherData.get("D_MEDI_UNIT"));
			// result.setData("MEDI_UNIT",otherData.get("D_MEDI_UNIT"));
			// //��ҩ����
			// this.messageBox_("��ҩ����"+otherData.get("D_DOSAGE_QTY"));
			// result.setData("DISPENSE_QTY",otherData.get("D_DOSAGE_QTY"));
			// ��ҩ��λ�����䵥λ
			// this.messageBox_("��ҩ��λ�����䵥λ"+otherData.get("D_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
			// ��ҩʱ������==�����ҩ����
			// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
			// ������ע�ǹ۲�����ʹ��
			result.setData("RX_FLG", "Y");
		} else {
			// �ײ�ʱ��
			result.setData("START_DTTM", StringTool.getString(parm
					.getTimestamp("EFF_DATE"), "yyyyMMddHHmmss"));
			// ������
			// this.messageBox_("������"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("FRST_QTY", 0);
			// RX_KINDΪF
			result.setData("RX_KIND", "UD");
			// �����ҩ��
			// this.messageBox_("�����ҩ��"+otherData.get("ORDER_LASTDSPN_QTY"));
			result.setData("LASTDSPN_QTY", 0);
			// ������
			// this.messageBox_("ORDER_ACUMDSPN_QTY:"+otherData.get("ORDER_ACUMDSPN_QTY"));
			result.setData("ACUMDSPN_QTY", 0);
			// �ۼƿ�ҩ��
			// this.messageBox_("ORDER_ACUMMEDI_QTY:"+otherData.get("ORDER_ACUMMEDI_QTY"));
			result.setData("ACUMMEDI_QTY", 0);
			// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
			// this.messageBox_("��ҩ����"+otherData.get("M_DISPENSE_QTY"));
			result.setData("DISPENSE_QTY", 0);
			// ������λ(��ҩ����)
			// this.messageBox_("������λ"+otherData.get("M_DISPENSE_UNIT"));
			result.setData("DISPENSE_UNIT", "");
			// ��ҩ������ʵ�ʿۿ�����
			// this.messageBox_("��ҩ����:"+otherData.get("M_DOSAGE_QTY"));
			result.setData("DOSAGE_QTY", 0);
			// ��ҩ��λ < ʵ�ʿۿ��� >
			// this.messageBox_("��ҩ��λ:"+otherData.get("M_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", "");
			// ��ҩ����
			// this.messageBox_("��ҩ����:"+otherData.get("D_MEDI_QTY"));
			// result.setData("MEDI_QTY",otherData.get("D_MEDI_QTY"));
			// ��ҩ��λ
			// this.messageBox_("��ҩ��λ"+otherData.get("D_MEDI_UNIT"));
			// result.setData("MEDI_UNIT",otherData.get("D_MEDI_UNIT"));
			// //��ҩ����
			// this.messageBox_("��ҩ����"+otherData.get("D_DOSAGE_QTY"));
			// result.setData("DISPENSE_QTY",otherData.get("D_DOSAGE_QTY"));
			// ��ҩ��λ�����䵥λ
			// this.messageBox_("��ҩ��λ�����䵥λ"+otherData.get("D_DOSAGE_UNIT"));
			result.setData("DOSAGE_UNIT", "");
			// ��ҩʱ������==�����ҩ����
			// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
			// ������ע�ǹ۲�����ʹ��
			result.setData("RX_FLG", "N");
		}
		// else{
		// //��ҩʱ������==�����ҩ����
		// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
		// }
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify
			// ����ҩƷ������δ�ش�
			result.setErrCode(-2);
			return result;
		}
		return result;
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// ��ǰ�༭��
		this.rowOnly = row;
		// �õ�����
		String columnName = this.getFactColumnName(TABLE2, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;
		// 20121109 shibl add �ظ��޸�ҽ������
		TTable table = getTTable(TABLE2);
		int selRow = this.getTTable(TABLE2).getSelectedRow();
		TParm existParm = this.getTTable(TABLE2).getDataStore().getRowParm(
				selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if ("PHA".equals(existParm.getValue("CAT1_TYPE"))
				&& null != existParm.getValue("ANTIBIOTIC_CODE")
				&& !existParm.getValue("ANTIBIOTIC_CODE").equals("")) {// =====pangben
			// 2014-6-26
			// �޸Ĵ��صĿ���ҩƷ�������޸�
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			this.messageBox("����ҩƷ�������޸�");
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();

		// if (this.isOrderSet(obj)) {
		// TTextField textFilter = (TTextField) this;
		// textFilter.setEnabled(false);
		// return;
		// }
		// ����ҽ������
		TParm parm = new TParm();
		if ("B".equals(this.getValue("KLSTARUD"))) {
			// �˿���״̬�����ڳ�Ժ��ҩ��ʹ��
			this.messageBox("E0161");
			return;
		} else {
			parm.setData("ODI_ORDER_TYPE", this.getValue("KLSTARUD"));
		}
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ��Ժ��ҩ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentDS(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// ��ǰ�༭��
		this.rowOnly = row;
		// �õ�����
		String columnName = this.getFactColumnName(TABLE3, column);
		if (!"ORDER_DESCCHN".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ��Ժ��ҩҽ������
		TParm parm = new TParm();
		// if("A".equals(this.getValue("KLSTARDS"))||"D".equals(this.getValue("KLSTARDS"))||"E".equals(this.getValue("KLSTARDS"))||"F".equals(this.getValue("KLSTARDS"))){
		// parm.setData("ODI_ORDER_TYPE",this.getValue("KLSTARDS"));
		// }else{
		// this.messageBox("E0161");
		// return;
		// }
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("DS", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ��ҩ��Ƭ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentIG(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// �õ�����
		String columnName = this.getFactColumnName(TABLE4, column);
		String columnArray[] = columnName.split("_");
		TParm columnParm = this.getTTable(TABLE4).getParmValue().getRow(row);
		// ��ǰ�༭��
		this.rowOnly = getIGEditRowSetId(columnParm.getInt("ROW_ID_"
				+ columnArray[columnArray.length - 1]));
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ��ҩҽ������
		TParm parm = new TParm();
		// �в�ҩ����
		parm.setData("ODI_ORDER_TYPE", "F");
		textFilter.setPopupMenuParameter("IG", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * �õ�סԺ��ҽ��ǰ�༭��
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public int getIGEditRowSetId(int row) {
		int rowId = -1;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// ds.showDebug();
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowSet = ds.getItemInt(i, "#ID#");
			if (row == rowSet) {
				rowId = i;
				break;
			}
		}
		return rowId;
	}

	/**
	 * �õ�סԺ��ҽ��ǰ�༭��
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public TParm getIGEditRowSetParm(int row) {
		TParm rowParm = new TParm();
		TDS ds = odiObject.getDS("ODI_ORDER");
		// ds.showDebug();
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowSet = ds.getItemInt(i, "#ID#");
			if (row == rowSet) {
				rowParm = ds.getRowParm(i);
				break;
			}
		}
		return rowParm;
	}

	/**
	 * ���ָ����������
	 * 
	 * @param row
	 *            int
	 * @param columnName
	 *            String
	 */
	public void clearRow(String tag, int row, String columnName) {
		// �õ���ǰѡ���к�
		if (!"IG".equals(tag)) {
			TParm orderParm = new TParm();
			orderParm.setData(columnName, "");
			odiObject.setItem(odiObject.getDS("ODI_ORDER"), row, orderParm);
		} else {
			TParm rowParm = this.getTTable(TABLE4).getParmValue().getRow(row);
			rowParm.setData(columnName, "");
			this.getTTable(TABLE4).setRowParmValue(row, rowParm);
		}

	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// this.messageBox("==popReturn==");
		// System.out.println("���ܷ���ֵ����"+obj);
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		boolean cdssFlg = true; // ��ͯ������ҩ�����ж� add by huangtt 20151116
		// ����ת����TParm
		TParm action = (TParm) obj;
		// ===================yanjing 20130908 ���ں���ʱ���ر��Ϊ
		if ("ST".equals(tag) || "UD".equals(tag)) {
			antiFechFlg = action.getBoolean("FLG");
		}

		// ״̬����ʾ
		if (tag.equals("GMA") || tag.equals("GMC") || tag.equals("GMN")) {
			callFunction("UI|setSysStatus", action.getValue("ID") + ":"
					+ action.getValue("CHN_DESC"));
		} else {
			callFunction("UI|setSysStatus", action.getValue("ORDER_CODE") + " "
					+ action.getValue("ORDER_DESC") + " "
					+ action.getValue("GOODS_DESC") + " "
					+ action.getValue("DESCRIPTION") + " "
					+ action.getValue("SPECIFICATION") + " "
					+ action.getValue("REMARK1_1") + " "
					+ action.getValue("REMARK1_2"));
		}
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// ���ñ�
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// ���ѡ��
						tableOid.clearSelection();
						return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// ���ñ�
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// ���ѡ��
						tableOid.clearSelection();
						return;
		}
		// modified by WangQing 20170428 end
		TParm isLock = this.isLockPat();
		if (isLock.getErrCode() == 0) {
			this.messageBox(isLock.getErrText());
			int selRows = -1;
			TTable tableOid = null;
			if ("ST".equals(tag)) {
				tableOid = this.getTTable(TABLE1);
			}
			if ("UD".equals(tag)) {
				tableOid = this.getTTable(TABLE2);
			}
			if ("DS".equals(tag)) {
				tableOid = this.getTTable(TABLE3);
			}
			if ("IG".equals(tag)) {
				tableOid = this.getTTable(TABLE4);
			}
			if ("PA".equals(tag)) {
				tableOid = this.getTTable(TABLE6);
			}
			if ("GMA".equals(tag) || "GMB".equals(tag) || "GMC".equals(tag)) {
				return;
			}
			// ���ñ�
			if (yyList)
				selRows = ((TParm) obj).getInt("EXID_ROW");
			else
				selRows = tableOid.getSelectedRow() < 0 ? ((TParm) obj)
						.getInt("EXID_ROW") : tableOid.getSelectedRow();
						this.clearRow(tag, selRows, "ORDER_DESC");
						tableOid.setDSValue(selRows);
						// ���ѡ��
						tableOid.clearSelection();
						return;
		}
		// System.out.println("action"+action);
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		boolean falg = true;
		// ѡ����
		int selRow = -1;
		// ��ҩѡ����
		int columnIG = -1;
		// ��ҩѡ����
		int rowIG = -1;

		// ��ʱ
		if ("ST".equals(tag)) {
			table = this.getTTable(TABLE1);
			table.acceptText();
			// ���ñ��ص�
			if (action.getInt("EXID_ROW") < 0)
				return;

			// System.out.println("====yyList====="+yyList);
			// ���ñ�
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();

				// System.out.println("�к�:"+selRow);

				action.setData("RX_KIND", "ST");
				// �Ƿ�ɹ���ֵ
				falg = this.setNewRowOrder(selRow, action, 0);
				// �����ʾ��ҽ������
				if (!falg) {
					// ���ҽ������
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// ���ѡ��
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
				table.setLockCell(table.getSelectedRow(), 20, false);
				// System.out.println("��ʱҽ��========="+falg);
				// table.getDataStore().showDebug();
		}
		// ����
		if ("UD".equals(tag)) {
			// this.messageBox("popReturn=====");
			table = this.getTTable(TABLE2);
			table.acceptText();
			// ���ñ��ص�
			if (action.getInt("EXID_ROW") < 0)
				return;
			// ���ñ�
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "UD");
				// �Ƿ�ɹ���ֵ
				falg = this.setNewRowOrder(selRow, action, 1);
				// �����ʾ��ҽ������
				if (!falg) {
					// ���ҽ������
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// ���ѡ��
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}

		// ��Ժ��ҩ
		if ("DS".equals(tag)) {
			table = this.getTTable(TABLE3);
			table.acceptText();
			// ���ñ��ص�
			if (action.getInt("EXID_ROW") < 0)
				return;
			cdssFlg = action.getBoolean("CDSS_FLG"); // add by huangtt 20151116
			// ��Ժ��ҩ���ó���ҽ��ʱ��������������ҩ�ж�
			// ���ñ�
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "DS");
				// �Ƿ�ɹ���ֵ
				falg = this.setNewRowOrder(selRow, action, 2);
				// �����ʾ��ҽ������
				if (!falg) {
					// ���ҽ������
					clearRow(tag, selRow, "ORDER_DESC");
					table.setDSValue(selRow);
					// ���ѡ��
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}
		// ��ҩ��Ƭ
		if ("IG".equals(tag)) {
			table = this.getTTable(TABLE4);
			selRow = this.rowOnly;
			action.setData("RX_KIND", "IG");
			// �Ƿ�ɹ���ֵ
			falg = this.setNewRowOrder(selRow, action, 3);
			// �����ʾ��ҽ������
			if (!falg) {
				// ���ҽ������
				clearRow(tag, table.getSelectedRow(), this.getFactColumnName(
						TABLE4, table.getSelectedColumn()));
				// ���ѡ��
				table.clearSelection();
				return;
			}
			ds.setActive(selRow, true);
			columnIG = table.getSelectedColumn();
			rowIG = table.getSelectedRow();
		}
		// Ժǰ��ҩ
		if ("PA".equals(tag)) {
			table = this.getTTable(TABLE6);
			table.acceptText();
			// ���ñ��ص�
			if (action.getInt("EXID_ROW") < 0)
				return;
			// ���ñ�
			if (yyList)
				selRow = action.getInt("EXID_ROW");
			else
				selRow = table.getSelectedRow() < 0 ? action.getInt("EXID_ROW")
						: table.getSelectedRow();
				action.setData("RX_KIND", "PA");
				// �Ƿ�ɹ���ֵ
				falg = this.setNewRowOrder(selRow, action, 5);
				// �����ʾ��ҽ������
				if (!falg) {
					// ���ҽ������
					clearRow(tag, selRow, "ORDER_DESCCHN");
					table.setDSValue(selRow);
					// ���ѡ��
					table.clearSelection();
					return;
				}
				table.setDSValue(selRow);
				table.getDataStore().setActive(selRow, true);
		}
		// �ɷֹ���
		if ("GMA".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// ҩƷ����
		if ("GMB".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ORDER_CODE"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// ��������
		if ("GMC".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		// �޹��� add by guoy 20151112
		if ("GMN".equals(tag)) {
			table = this.getTTable(GMTABLE);
			table.acceptText();
			selRow = table.getSelectedRow();
			table.getDataStore().setItem(selRow, "DRUGORINGRD_CODE",
					action.getValue("ID"));
			table.getDataStore().setActive(selRow, true);
			table.setDSValue(selRow);
		}
		if (falg) {
			antflg = false;// ����ʾ������
			// ����TABLE
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			if (!"IG".equals(tag)) {
				table.getTable().grabFocus();
				table.setSelectedRow(selRow);
				if ("PA".equals(tag)) {
					// �Ǳ�Ժ��ҩ
					String notOurHospDrugCode = TConfig
							.getSystemValue("NotOurHospDrugCode");
					if (StringUtils.isNotEmpty(notOurHospDrugCode)
							&& StringUtils.equals(table.getItemString(selRow,
									"ORDER_CODE"), notOurHospDrugCode)) {
						table.setSelectedColumn(1);
					} else {
						table.setSelectedColumn(2);
					}
				} else {
					table.setSelectedColumn(3);
				}
			} else {
				table.getTable().grabFocus();
				// סԺѡ����
				String columnName = this.getFactColumnName(TABLE4, columnIG);
				table.setSelectedRow(rowIG);
				table.setSelectedColumn(querySetIGFocusColumn(columnName));
				// �����ܿ���
				this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(ds));
			}
		}

		// add by huangtt 20151015��ͯ��������
		if (cdssFlg) {
			odiStationDrools.onCdssCal(true);
		}

	}

	/**
	 * ����Ƿ�����
	 * 
	 * @return TParm
	 */
	public TParm isLockPat() {
		TParm result = new TParm();
		TParm parm = PatTool.getInstance().getLockPat(this.getMrNo());
		if (parm == null) {
			result.setErrCode(-1);
			return result;
		}
		if (parm.getCount() == 0) {
			result.setErrCode(-1);
			return result;
		}
		if (!Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
			String userName = OperatorTool.getInstance().getOperatorName(
					parm.getValue("OPT_USER", 0));
			String time = StringTool.getString(
					parm.getTimestamp("OPT_DATE", 0), "yyyy��MM��dd�� HH:mm:ss");
			String name = PatTool.getInstance().getNameForMrno(mrNo);
			String ip = parm.getValue("OPT_TERM", 0);
			String program = parm.getValue("PRG_ID", 0);
			String errStr = userName + "��" + time + "��������" + name + ",IP��ַ:"
					+ ip + "ϵͳ��" + program + "\n" + "In " + time + " in "
					+ name + " is locked,and IP address for " + ip
					+ " system for " + program;
			result.setErrCode(0);
			result.setErrText(errStr);
		} else {
			result.setErrCode(-1);
		}
		return result;
	}

	/**
	 * ������ҩ�ܿ���
	 * 
	 * @param ds
	 *            TDS
	 * @return double
	 */
	public double getChnPhaMediQtyAll(TDS ds) {
		double mediQty = 0.0;
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!ds.isActive(i))
				continue;
			mediQty += ds.getItemDouble(i, "DOSAGE_QTY");
		}
		return mediQty;
	}

	/**
	 * �������λ��
	 * 
	 * @return int
	 */
	public int querySetIGFocusColumn(String columnName) {
		int row = -1;
		String columnArray[] = columnName.split("_");
		if (columnArray[columnArray.length - 1].equals("1")) {
			row = 1;
		}
		if (columnArray[columnArray.length - 1].equals("2")) {
			row = 4;
		}
		if (columnArray[columnArray.length - 1].equals("3")) {
			row = 7;
		}
		if (columnArray[columnArray.length - 1].equals("4")) {
			row = 10;
		}
		return row;
	}

	/**
	 * ����ҽ����
	 * 
	 * @param newRow
	 *            int
	 * @param parm
	 *            TParm
	 */
	public boolean setNewRowOrder(int newRow, TParm parm, int tabIndex) {
		TParm actionParm = new TParm();
		Object obj = null;
		switch (tabIndex) {
		// ��ʱ
		case 0:

			TDS dsST = odiObject.getDS("ODI_ORDER");
			// �õ���Ҫ��ӵ�ҽ����
			actionParm = this.creatOrderInfo(parm, 0, "NEW", newRow);
			// System.out.println("====actionParm========"+actionParm);
			// modify by wangb 2016/6/12 ���һ���ٴ������ײͲ���ʾ��ͬҽ�� START
			if (!"Y".equals(parm.getValue("PHASE_I_CLINICAL_FLG"))) {
				// �ж��Ƿ�����ͬҽ��
				if (!isCheckOrderSame(actionParm, 0, newRow)) {
					if (messageBox("��ʾ��Ϣ Tips",
							"����ͬҽ���Ƿ�����ҽ��? \n Have the same project?",
							this.YES_NO_OPTION) != 0)
						return false;
				}
			}
			// modify by wangb 2016/6/12 ���һ���ٴ������ײͲ���ʾ��ͬҽ�� END
			// �ж��Ƿ��в���������������
			if (actionParm.getErrCode() < 0) {
				// ��ʾ������
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// ����ҽ���TABLE��
			// String buff = dsST.isFilter()?dsST.FILTER:dsST.PRIMARY;
			// ����������ʱ stIndParm
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);
				TParm stIndParm = getTempStartQty(actionParm);
				if (stIndParm.getErrCode() < 0) {
					// this.messageBox("===========come in============="+stIndParm.getErrCode());
					// $$============add by lx 2011 ��ʾ������ bug�޸�
					// �����س���Start===================//
					// ����������
					if (stIndParm.getErrCode() == -2) {
						if (messageBox("��ʾ��Ϣ Tips",
								"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
								this.YES_NO_OPTION) != 0)
							return false;
					}
					// this.messageBox(stIndParm.getErrText());
					// return true;
					// $$============add by lx 2011 ��ʾ������ bug�޸�
					// �����س���END===================//
					// ��ʾ������
					// this.messageBox(actionParm.getErrText());
					// return false;
				}
				actionParm.setData("START_DTTM_LIST", stIndParm
						.getData("START_DTTM_LIST"));
				actionParm.setData("START_DTTM", stIndParm
						.getData("START_DTTM"));
				actionParm.setData("FRST_QTY", stIndParm.getData("FRST_QTY"));
				actionParm.setData("LASTDSPN_QTY", stIndParm
						.getData("LASTDSPN_QTY"));

				actionParm.setData("ACUMMEDI_QTY", stIndParm
						.getData("ACUMMEDI_QTY"));
				actionParm.setData("DISPENSE_QTY", stIndParm
						.getData("DISPENSE_QTY"));
				actionParm.setData("DISPENSE_UNIT", stIndParm
						.getData("DISPENSE_UNIT"));

				actionParm.setData("DOSAGE_UNIT", stIndParm
						.getData("DOSAGE_UNIT"));
				actionParm.setData("MEDI_UNIT", stIndParm.getData("MEDI_UNIT"));
				// ====pangben 2013-9-10 ��ҽ���ײͿ�������ҩƷ��ʾ
				// //��ѯ��ҩ���Ƿ�ΪƤ��ҩ��
				String sql = "SELECT SKINTEST_FLG, ANTIBIOTIC_CODE"
						+ " FROM PHA_BASE  WHERE ORDER_CODE = '"
						+ parm.getValue("ORDER_CODE") + "' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if ((!actionParm.getValue("ANTIBIOTIC_CODE").equals("") && result
						.getValue("SKINTEST_FLG", 0).equals("N"))
						&& (null == parm.getValue("PHA_ANTI_FLG") || !parm
						.getValue("PHA_ANTI_FLG").equals("Y"))) {// add
					String anti_flg = "01";// ������ʶ
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));
					this.messageBox("��ӿ���ҩ����濪������ҩƷ!");
					return false;
					// this
					// .messageBox("<html><font color=\"red\">��Ϊ����ҩƷ������д������ʶ</font></html>");
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {
					// ====donglt 2016-3-4 ��ʱҽ��ҳǩ--��������ҩƷ��ʾ
					String anti_flg = "01";// ������ʶ
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));// ������ʶ�Զ���ֵ
					// this.messageBox("<html><font color=\"red\">��Ϊ����ҩƷ������д������ʶ</font></html>");
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsST)){
						messageBox("û������ԭѧ���");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// add by
				// wanglong
				// 20140401
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// ����ҩƷԽȨ������ʾ�ܿ�
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!this.yyList) {
					// ==============pangben modify 20110609 start ��ʼ������ʾ
					TParm mediQTY = getMediQty(stIndParm, actionParm);
					// ==============pangben modify 20110609 stop
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : stIndParm
									.getData("MEDI_QTY"));// ============pangben
					actionParm.setData("ACUMDSPN_QTY", stIndParm
							.getDouble("ACUMDSPN_QTY") > 0 ? stIndParm
									.getDouble("ACUMDSPN_QTY") : mediQTY.getCount());
					actionParm.setData("DOSAGE_QTY", mediQTY.getDouble(
							"DOSAGE_QTY", 0) > 0 ? mediQTY.getDouble(
									"DOSAGE_QTY", 0) : stIndParm
									.getDouble("DOSAGE_QTY"));// SHIBL 20130228 MODIFY
					// =============pangben modify 20110711 start ��ʱҽ��Ƶ��������ʹ��
					if (actionParm.getInt("LINK_NO") == 0) {
						// Ƶ��
						actionParm.setData("FREQ_CODE", odiObject
								.getAttribute(odiObject.ODI_UDD_STAT_CODE));
						// �÷�
						actionParm.setData("ROUTE_CODE", mediQTY.getValue(
								"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("ROUTE_CODE", 0).trim() : null);
					}
				}
				// �ײʹ���
				else {
					/**
					 * System.out.println("====dosageQty====="+stIndParm
					 * .getDouble("DOSAGE_QTY"));
					 **/
					actionParm.setData("ACUMDSPN_QTY", stIndParm
							.getDouble("ACUMDSPN_QTY"));
					actionParm.setData("DOSAGE_QTY", stIndParm
							.getDouble("DOSAGE_QTY"));

				}

				// $$=====Modified by lx 2012/04/16 end ��ʱ�����ײʹ���======$$//

			}

			// add by yangjj 20150410���ӿ�����Ѫҽ��ʱ������Ѫ��ѡ�񴰿�
			// ��Ѫ���뵥��
			String apply_no = "";
			if (checkBMSOrder(actionParm.getValue("ORDER_CODE"))) {
				TParm parmdate = new TParm();
				String caseno = getCaseNo();
				String deptCode = getDeptCode();
				String mrno = getMrNo();
				parmdate.setData("ADM_TYPE", "");
				parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
				parmdate.setData("DR_CODE", Operator.getID());
				parmdate.setData("CASE_NO", caseno);
				parmdate.setData("MR_NO", mrno);
				parmdate.setData("DEPT_CODE", deptCode);
				parmdate.setData("new", "No");
				TParm r = new TParm(TJDODBTool.getInstance().select(
						getEXECPRGSql(actionParm.getValue("ORDER_CODE"))));
				TParm result = (TParm) this.openDialog(r
						.getValue("EXEC_PRG", 0), parmdate);

				if (result == null) {
					return false;
				}
				apply_no = result.getValue("APPLY_NO");
				actionParm.setData("APPLY_NO", apply_no);

			}

			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע start=====$$//
			if (this.yyList) {
				// ���ñ�ע
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// ����·����������--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// ҽ����ע
			}
			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע end=====$$

			// add by wangb ����һ���ٴ�ע�� 2016/6/12 START
			actionParm.setData("PHASE_I_CLINICAL_FLG", parm.getValue("PHASE_I_CLINICAL_FLG"));
			// add by wangb ����һ���ٴ�ע�� 2016/6/12 END

			// add by wangb һ���ٴ������ײͽ�ʱ�������뵽ҽ����ע�� START
			if ("Y".equals(parm.getValue("PHASE_I_CLINICAL_FLG")) && "LIS".equals(parm.getValue("CAT1_TYPE"))) {
				String drNote = parm.getValue("BC_INTERVAL_TIME");
				if (StringUtils.isNotEmpty(drNote)) {
					if (0 == parm.getDouble("BC_INTERVAL_TIME")) {
						drNote = "0h";
					} else {
						drNote = new BigDecimal(drNote).stripTrailingZeros() + "h";
					}
				}
				actionParm.setData("DR_NOTE", drNote);
			}
			// add by wangb һ���ٴ������ײͽ�ʱ�������뵽ҽ����ע�� END

			odiObject.setItem(dsST, newRow, actionParm);// ���ҽ��������
			if (opeFlg) {
				dsST = odiObject.getDS("ODI_ORDER");
				dsST.setItem(newRow, "RX_KIND", "OP");
			}
			// dsST.showDebug();
			obj = actionParm.getData("TABLEROW_COLOR");
			// ������ɫ
			if (obj != null) {
				this.getTTable(TABLE1).setRowTextColor(newRow, (Color) obj);
			}

			// �ж��Ƿ��Ǽ���ҽ��
			if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
				// System.out.println("===========actionParm����ҽ������=============="+actionParm);
				// �õ�ִ�п���
				String exDeptCode = actionParm.getValue("EXEC_DEPT_CODE");
				// �õ�ҽ��ϸ����
				String orderCatCode = actionParm.getValue("ORDER_CAT1_CODE");
				// ҽ�����
				String catType = actionParm.getValue("CAT1_TYPE");
				// Ƶ��
				String freqCode = actionParm.getValue("FREQ_CODE");
				// ��λ
				String dosageUnit = actionParm.getValue("DOSAGE_UNIT");
				// ����ҽ�����
				String orderSetGroupNo = actionParm
						.getValue("ORDERSET_GROUP_NO");
				// $$============= add by lx 2012-06-12
				// ϸ������ʱ��һ��start============$$//
				Timestamp effDate = actionParm.getTimestamp("EFF_DATE");
				// this.messageBox("==effDate=="+effDate);
				// $$============= add by lx 2012-06-12
				// ϸ������ʱ��һ��end============$$//
				// �õ�����ҽ��ϸ��
				TParm action = this.getOrderSetList(actionParm);
				if (action.getInt("ACTION", "COUNT") == 0) {
					// û�м���ҽ��ϸ����Ϣ!
					this.messageBox("E0162");
					return true;
				}

				// ���뼯������ϸ��
				return this.insertOrderSetList(action, exDeptCode,
						orderCatCode, catType, freqCode, dosageUnit,
						orderSetGroupNo, tabIndex, dsST, newRow, effDate);
			}
			TTable table = (TTable) this.getComponent(TABLE1);
			table.setLockCell(20, table.getSelectedRow(), false);
			table.setLockCell(21, table.getSelectedRow(), false);
			table.setLockCell(23, table.getSelectedRow(), false);
			break;
			// ����
		case 1:
			// System.out.println("===����actionParm==="+parm);
			TDS dsUD = odiObject.getDS("ODI_ORDER");
			// �õ���Ҫ��ӵ�ҽ����
			actionParm = this.creatOrderInfo(parm, 1, "NEW", newRow);
			// �ж��Ƿ�����ͬҽ��
			if (!isCheckOrderSame(actionParm, 1, newRow)) {
				if (messageBox("��ʾ��Ϣ Tips",
						"����ͬҽ���Ƿ�����ҽ��? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// �ж��Ƿ��в���������������
			if (actionParm.getErrCode() < 0) {
				// ��ʾ������
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// �������㳤��
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);

				TParm udIndParm = getLongStartQty(actionParm);

				// System.out.println("=====udIndParm======="+udIndParm);

				actionParm.setData("START_DTTM_LIST", udIndParm
						.getData("START_DTTM_LIST"));
				actionParm.setData("START_DTTM", udIndParm
						.getData("START_DTTM"));
				actionParm.setData("FRST_QTY", udIndParm.getData("FRST_QTY"));
				actionParm.setData("RX_KIND", udIndParm.getData("RX_KIND"));
				// �����ҩ��
				actionParm.setData("LASTDSPN_QTY", udIndParm
						.getData("LASTDSPN_QTY"));
				// ������
				actionParm.setData("ACUMDSPN_QTY", udIndParm
						.getData("ACUMDSPN_QTY"));
				// �ۼƿ�ҩ��
				actionParm.setData("ACUMMEDI_QTY", udIndParm
						.getData("ACUMMEDI_QTY"));
				// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
				actionParm.setData("DISPENSE_QTY", udIndParm
						.getData("DISPENSE_QTY"));
				// ������λ(��ҩ����)
				actionParm.setData("DISPENSE_UNIT", udIndParm
						.getData("DISPENSE_UNIT"));
				// ��ҩ������ʵ�ʿۿ�����
				actionParm.setData("DOSAGE_QTY", udIndParm
						.getData("DOSAGE_QTY"));
				// ��ҩ��λ < ʵ�ʿۿ��� >
				actionParm.setData("DOSAGE_UNIT", udIndParm
						.getData("DOSAGE_UNIT"));

				// ====donglt 2016-3-4 ��ʱҽ��ҳǩ--��������ҩƷ��ʾ
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")
						&& ("".equals(parm.getValue("PHA_ANTI_FLG"))
								|| null == parm.getValue("PHA_ANTI_FLG") || !parm
								.getValue("PHA_ANTI_FLG").equals("Y"))) { // add
					this.messageBox("��ӿ���ҩ����濪������ҩƷ!");
					return false;
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {
					// this.messageBox("<html><font color=\"red\">��Ϊ����ҩƷ������д������ʶ</font></html>");
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsUD)){
						messageBox("û������ԭѧ���");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// ===========pangben
				// 2014-2-27
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// ����ҩƷԽȨ������ʾ�ܿ�
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {

					// =====pangben 2013-9-10 ����ҩƷ��������ͣ��ʱ��
					if (parm.getValue("RADIO_FLG").equals("OPRDO")) {
						Timestamp temp = parm.getTimestamp("OP_START_DATE");
						// System.out.println("ͣ��ʱ�� is������"+temp);
						actionParm.setData("DC_DATE", StringTool.rollDate(temp,
								parm.getInt("PHA_DS_DAY")));
						// System.out.println("345ͣ��ʱ�� is������"+actionParm);
					} else {

						actionParm.setData("DC_DATE", StringTool.rollDate(
								SystemTool.getInstance().getDate(), parm
								.getInt("PHA_DS_DAY")));
					}
					actionParm.setData("DC_DR_CODE", Operator.getID());// yanjing,20131111
					actionParm.setData("DC_DEPT_CODE", Operator.getDept());
					actionParm.setData("ANTIBIOTIC_WAY", parm
							.getValue("ANTI_FLG"));// ������ʶ 20131111
					String sktSql = "SELECT SKINTEST_NOTE,BATCH_NO FROM PHA_ANTI WHERE CASE_NO= '"
							+ caseNo
							+ "' AND ORDER_CODE='"
							+ actionParm.getValue("ORDER_CODE")
							+ "' "
							+ " AND ROUTE_CODE = 'PS'  AND SKINTEST_NOTE IS NOT NULL ORDER BY ORDER_DATE DESC ";
					TParm sktParm = new TParm(TJDODBTool.getInstance().select(
							sktSql));
					String nsNode = null;
					String nsRNode = null;
					if (sktParm.getCount() > 0) {
						nsRNode = ",Ƥ������:" + sktParm.getValue("BATCH_NO", 0);
					}
					String sql = "SELECT ID,CHN_DESC AS NAME "
							+ "FROM SYS_DICTIONARY WHERE GROUP_ID='SKINTEST_FLG' AND ID='"
							+ sktParm.getValue("SKINTEST_NOTE", 0) + "'";
					sktParm = new TParm(TJDODBTool.getInstance().select(sql));
					if (sktParm.getCount() > 0) {
						nsNode = "Ƥ�Խ��:" + sktParm.getValue("NAME", 0)
						+ nsRNode;
					}
					actionParm.setData("NS_NOTE", nsNode);// 20131108 yanjing
					// ��ʿ��ע
				}
				if (!this.yyList) {
					// ==============pangben modify 20110609 start ��ʼ������ʾ
					TParm mediQTY = getMediQty(udIndParm, actionParm);
					// ��ҩ����
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : null);
					// ��ҩ��λ
					actionParm.setData("MEDI_UNIT",
							mediQTY.getCount() > 0 ? mediQTY.getValue(
									"MEDI_UNIT", 0) : null);
					// ��������ҽ����ȡĬ��

					if (actionParm.getInt("LINK_NO") == 0) {
						// Ƶ��
						actionParm.setData("FREQ_CODE", mediQTY.getValue(
								"FREQ_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("FREQ_CODE", 0).trim() : odiObject
										.getAttribute(odiObject.ODI_UDD_STAT_CODE));
						// �÷�
						actionParm.setData("ROUTE_CODE", mediQTY.getValue(
								"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
										.getValue("ROUTE_CODE", 0).trim() : null);
					}
				}
				// �ײʹ���
				/**
				 * }else{ System.out.println("====dosageQty====="+udIndParm
				 * .getDouble("DOSAGE_QTY")); actionParm .setData(
				 * "ACUMDSPN_QTY", udIndParm .getDouble("ACUMDSPN_QTY"));
				 * actionParm.setData( "DOSAGE_QTY", udIndParm
				 * .getDouble("DOSAGE_QTY")); }
				 **/
				// ��ҩ��λ�����䵥λ
				// actionParm.setData("DOSAGE_UNIT",
				// mediQTY.getValue("DOSAGE_UNIT",0));
				// ��ҩʱ������==�����ҩ����
				// result.setData("LAST_DSPN_DATE",StringTool.getTimestamp(otherData.get("ORDER_LAST_DSPN_DATE").toString(),"yyyyMMddHHmm"));
				// ������ע�ǹ۲�����ʹ��
				actionParm.setData("RX_FLG", "Y");

			}
			if (actionParm.getInt("LINK_NO") > 0
					&& !"PHA".equals(actionParm.getValue("CAT1_TYPE"))) {// wanglong
				// add
				// 20140725
				this.messageBox("��������ֻ�ܰ���ҩ��");
				return false;
			}
			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע start=====$$//
			if (this.yyList) {
				// ���ñ�ע
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// ����·����������--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// ҽ����ע
			}
			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע end=====$$//

			// ����ҽ���TABLE��
			// String buffUD = dsUD.isFilter()?dsUD.FILTER:dsUD.PRIMARY;
			if ((OrderUtil.getInstance().isSTFreq(actionParm
					.getValue("FREQ_CODE")))
					&& ("PHA".equals(actionParm.getValue("CAT1_TYPE")))) {
				actionParm.setData("FREQ_CODE", "");// ����ҽ������ҽ��ʱΪ����Ƶ��Ϊ��ʱʱΪ��
				// shibl 20130315 modify
			}
			odiObject.setItem(dsUD, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// ������ɫ
			if (obj != null) {
				// this.getTTable(TABLE2).setRowColor(newRow, (Color) obj);
				this.getTTable(TABLE2).setRowTextColor(newRow, (Color) obj);
			}
			// �ж��Ƿ��Ǽ���ҽ������
			if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
				// �õ�ִ�п���
				String exDeptCode = actionParm.getValue("EXEC_DEPT_CODE");
				// �õ�ҽ��ϸ����
				String orderCatCode = actionParm.getValue("ORDER_CAT1_CODE");
				// ҽ�����
				String catType = actionParm.getValue("CAT1_TYPE");
				// Ƶ��
				String freqCode = actionParm.getValue("FREQ_CODE");
				// �÷�
				String dosageUnit = actionParm.getValue("DOSAGE_UNIT");
				// ����ҽ�����
				String orderSetGroupNo = actionParm
						.getValue("ORDERSET_GROUP_NO");

				// $$============= add by lx 2012-06-12
				// ϸ������ʱ��һ��start============$$//
				Timestamp effDate = actionParm.getTimestamp("EFF_DATE");
				// $$============= add by lx 2012-06-12
				// ϸ������ʱ��һ��end============$$//

				// �õ�����ҽ��ϸ��
				TParm action = this.getOrderSetList(actionParm);
				if (action.getInt("ACTION", "COUNT") == 0) {
					this.messageBox("E0162");
					return true;
				}
				// ���뼯������ϸ��
				return this.insertOrderSetList(action, exDeptCode,
						orderCatCode, catType, freqCode, dosageUnit,
						orderSetGroupNo, tabIndex, dsUD, newRow, effDate);
			}
			break;
			// ��Ժ��ҩ
		case 2:
			if (this.getValueString("RX_NO").length() == 0) {
				// ��ѡ�񴦷��ţ�
				this.messageBox("E0069");
				return false;
			}
			TDS dsDS = odiObject.getDS("ODI_ORDER");
			// �õ���Ҫ��ӵ�ҽ����
			actionParm = this.creatOrderInfo(parm, 2, "NEW", newRow);
			// �ж��Ƿ�����ͬҽ��
			if (!isCheckOrderSame(actionParm, 2, newRow)) {
				if (messageBox("��ʾ��Ϣ Tips",
						"����ͬҽ���Ƿ�����ҽ��? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// �ж��Ƿ��в���������������
			if (actionParm.getErrCode() < 0) {
				// ��ʾ������
				this.messageBox(actionParm.getErrText());
				return false;
			}
			// ���������Ժ��ҩ
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);

				TParm dsIndParm = getOutHospStartQty(actionParm);
				// ��淢ҩ��λ
				actionParm.setData("DOSAGE_UNIT", dsIndParm
						.getData("DOSAGE_UNIT"));
				// ��������
				actionParm.setData("DOSAGE_QTY", dsIndParm
						.getData("DOSAGE_QTY"));
				// ��ҩ����
				actionParm.setData("DISPENSE_QTY", dsIndParm
						.getData("DISPENSE_QTY"));
				// ��ҩ��λ
				actionParm.setData("DISPENSE_UNIT", dsIndParm
						.getData("DISPENSE_UNIT"));


				// ====donglt 2016-3-4 ��ʱҽ��ҳǩ--��������ҩƷ��ʾ
				if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")
						&& ("".equals(parm.getValue("PHA_ANTI_FLG"))
								|| null == parm.getValue("PHA_ANTI_FLG") || !parm
								.getValue("PHA_ANTI_FLG").equals("Y"))) { // add
					this.messageBox("��ӿ���ҩ����濪������ҩƷ!");
					return false;
				} else if (!actionParm.getValue("ANTIBIOTIC_CODE").equals("")) {

					String anti_flg = "02";// ������ʶ
					parm.addData("ANTI_FLG", anti_flg);
					actionParm.setData("ANTIBIOTIC_WAY", parm.getValue(
							"ANTI_FLG", 0));// ������ʶ�Զ���ֵ
				}
				if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
					if(!checkEtiolgexm(dsDS)){
						messageBox("û������ԭѧ���");
					}
					}
				TParm antiParm = antiOrderCheck(parm, tabIndex);// add by
				// wanglong
				// 20140401
				if (antiParm.getErrCode() < 0) {
					if (null != antiParm.getValue("ANTICHECK_FLG")
							&& antiParm.getValue("ANTICHECK_FLG").equals("Y")) {// ����ҩƷԽȨ������ʾ�ܿ�
					} else {
						this.messageBox(antiParm.getErrText());
					}
					return false;
				}
				if (!this.yyList) {
					// System.out.println("====��ҽ���ײʹ����====");
					// ==============pangben modify 20110609 start ��ʼ������ʾ
					TParm mediQTY = getMediQty(dsIndParm, actionParm);
					// ��ҩ����
					actionParm.setData("MEDI_QTY",
							mediQTY.getCount() > 0 ? mediQTY.getDouble(
									"MEDI_QTY", 0) : null);
					// ��ҩ��λ
					actionParm.setData("MEDI_UNIT",
							mediQTY.getCount() > 0 ? mediQTY.getValue(
									"MEDI_UNIT", 0) : null);
					// Ƶ��
					actionParm.setData("FREQ_CODE", mediQTY.getValue(
							"FREQ_CODE", 0).trim().length() > 0 ? mediQTY
									.getValue("FREQ_CODE", 0).trim() : odiObject
									.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					// �÷�
					actionParm.setData("ROUTE_CODE", mediQTY.getValue(
							"ROUTE_CODE", 0).trim().length() > 0 ? mediQTY
									.getValue("ROUTE_CODE", 0).trim() : null);
					// ==========pangben modify 20110609 stop
				}
			}

			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע start=====$$//
			if (this.yyList) {
				// ���ñ�ע
				actionParm.setData("DR_NOTE", parm.getValue("DESCRIPTION"));
			}
			if (parm.getValue("CLP_FLG").equals("Y")) {// ����·����������--xiongwg20150429
				actionParm.setData("DR_NOTE", parm.getValue("DR_NOTE"));// ҽ����ע
			}
			// $$====add by lx 2012/04/17 ���ײʹ���ģ����ñ�ע end=====$$//

			// ����ҽ���TABLE��
			odiObject.setItem(dsDS, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// ������ɫ
			if (obj != null) {
				// this.getTTable(TABLE3).setRowColor(newRow, (Color) obj);
				this.getTTable(TABLE3).setRowTextColor(newRow, (Color) obj);
			}
			break;
			// ��ҩ��Ƭ
		case 3:
			if (this.getValueString("IG_RX_NO").length() == 0) {
				this.messageBox("E0069");
				return false;
			}
			TDS dsIG = odiObject.getDS("ODI_ORDER");
			// �õ���Ҫ��ӵ�ҽ����
			actionParm = this.creatOrderInfo(parm, 3, "NEW", newRow);
			// �ж��Ƿ�����ͬҽ��
			if (!isCheckOrderSame(actionParm, 3, newRow)) {
				if (messageBox("��ʾ��Ϣ Tips",
						"����ͬҽ���Ƿ�����ҽ��? \n Have the same project?",
						this.YES_NO_OPTION) != 0)
					return false;
			}
			// �ж��Ƿ��в���������������
			if (actionParm.getErrCode() < 0) {
				// ��ʾ������
				this.messageBox(actionParm.getErrText());
				return false;
			}
			if(checkAntibiotics(parm.getValue("ORDER_CODE"))){//add by kangy 20170524
				if(!checkEtiolgexm(dsIG)){
					messageBox("û������ԭѧ���");
				}
				}
			// ���������Ժ��ҩ
			if ("PHA".equals(actionParm.getValue("CAT1_TYPE"))
					&& !"Y".equals(actionParm.getValue("IS_REMARK"))) {
				actionParm.setData("#NEW#", true);
				TParm igIndParm = getOutHospStartQty(actionParm);
				// ��淢ҩ��λ
				// actionParm.setData("DOSAGE_UNIT",actionParm.getData("DOSAGE_UNIT"));
				// ��������
				actionParm.setData("DOSAGE_QTY", igIndParm
						.getData("DOSAGE_QTY"));
				// ��ҩ����
				actionParm.setData("DISPENSE_QTY", igIndParm
						.getData("DISPENSE_QTY"));
				// ��ҩ��λ
				// actionParm.setData("DISPENSE_UNIT",actionParm.getData("DISPENSE_UNIT"));

			}
			// ��������
			this.setValue("MEDI_QTYALL", getChnPhaMediQtyAll(dsIG));
			// this.messageBox_(actionParm);
			// ����ҽ���TABLE��
			odiObject.setItem(dsIG, newRow, actionParm);
			obj = actionParm.getData("TABLEROW_COLOR");
			// this.messageBox_(obj);
			// ������ɫ
			// if (obj != null) {
			// // this.getTTable(TABLE4).setRowColor(newRow, (Color) obj);
			// //errtemp(��Ԫ���ɫ)
			// this.getTTable(TABLE4).setRowTextColor(newRow, (Color) obj);
			// }
			break;
			// Ժǰ��ҩ
		case 5:
			TDS dsPa = odiObject.getDS("ODI_ORDER");
			// �õ���Ҫ��ӵ�ҽ����
			actionParm = this.creatOrderInfo(parm, 5, "NEW", newRow);
			// �ж��Ƿ��в���������������
			if (actionParm.getErrCode() < 0) {
				// ��ʾ������
				this.messageBox(actionParm.getErrText());
				return false;
			}
			actionParm.setData("#NEW#", true);
			odiObject.setItem(dsPa, newRow, actionParm);
			break;
		}
		return true;
	}

	/**
	 * �õ�����ҽ��ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @param exDeptCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetList(TParm parm) {
		TParm result = new TParm();
		String orderCode = parm.getValue("ORDER_CODE");
		result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT A.ORDER_CODE,B.ORDER_DESC,B.DESCRIPTION,B.UNIT_CODE,B.INSPAY_TYPE,"
								+ " B.ORDERSET_FLG,B.INDV_FLG,ROWNUM+1 AS ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
								+ " B.RPTTYPE_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.SPECIFICATION,'Y' AS HIDE_FLG,B.DEV_CODE,B.IPD_FIT_FLG,A.DOSAGE_QTY AS MEDI_QTY"
								+ " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='"
								+ orderCode
								+ "' AND A.ORDER_CODE=B.ORDER_CODE"));
		// System.out.println("�õ�����ҽ��ϸ��:"+result);
		return result;
	}

	/**
	 * ���뼯��ҽ��ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @param exDeptCode
	 *            String
	 * @param orderCatCode
	 *            String
	 * @param catType
	 *            String
	 */
	public boolean insertOrderSetList(TParm parm, String exDeptCode,
			String orderCatCode, String catType, String freqCode,
			String dosageUnit, String orderSetGroupNo, int type, TDS ds,
			int selrow, Timestamp effDate) {
		int rowCount = parm.getInt("ACTION", "COUNT");
		// this.messageBox("����ҽ������:"+rowCount);
		if (rowCount == 0)
			return true;
		odiObject.setAttribute("CHANGE_FLG", true);
		String rxKinds[] = new String[] { "ST", "UD", "DS", "IG" };
		odiObject.setAttribute("RX_KIND", rxKinds[type]);
		// �õ�����ҽ��ϸ��ʵ��Ҫ�����ֵ
		for (int i = 0; i < rowCount; i++) {
			TParm action = parm.getRow(i);
			action.setData("EXEC_DEPT_CODE", exDeptCode);
			action.setData("ORDER_CAT1_CODE", orderCatCode);
			action.setData("CAT1_TYPE", catType);
			action.setData("FREQ_CODE", freqCode);
			action.setData("RX_KIND", rxKinds[type]);
			action.setData("DOSAGE_UNIT", dosageUnit);
			action.setData("ORDERSET_GROUP_NO", orderSetGroupNo);
			// System.out.println("����ҽ��ϸ�������old============:"+action);
			action = this.creatOrderInfo(action, type, "OLD", selrow);
			if (action.getErrCode() < 0) {
				this.messageBox(action.getErrText());
				return false;
			}

			// $$ ==== add by lx 2012-06-30 ��ȫҽ������ʱ����һ��start====$$//
			action.setData("EFF_DATE", effDate);
			// $$ ==== add by lx 2012-06-30 end====$$//

			// this.messageBox("���غ�ļ���ҽ��ϸ��:"+action);
			// System.out.println("����ҽ��ϸ�������:"+action);
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", true);
			// System.out.println("�ײ�ʱ��:"+action.getData("START_DTTM"));
			action.setData("RX_KIND", rxKinds[type]);
			action.setData("ORDER_NO", "999999999999");
			// System.out.println("====insertOrderSetList action===="+action);
			if (opeFlg) {
				action.setData("RX_KIND", "OP");
			}
			TParm actionParm = odiObject.insertRow(ds, action, buff);
			if (actionParm.getErrCode() < 0) {
				odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", false);
				return false;
			}
		}
		odiObject.getDS("ODI_ORDER").setAttribute("ORDERJH_FLG", false);
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
	 * �õ�����ҽ���������
	 * 
	 * @return int
	 */
	public int getMaxOrderSetGroupNo() {
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int rowCount = parm.getCount();
		int maxGroupNo = 0;
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (temp.getInt("ORDERSET_GROUP_NO") > maxGroupNo)
				maxGroupNo = temp.getInt("ORDERSET_GROUP_NO");
		}
		return maxGroupNo == 0 ? 1 : maxGroupNo + 1;
	}

	/**
	 * �Ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            int
	 * @return boolean
	 */
	public boolean isCheckOrderContinuousFlg(TParm parm, int type) {
		boolean falg = true;
		String orderCode = parm.getValue("ORDER_CODE");
		TTable table = this.getTTable("TABLE" + (type + 1));
		int selectRow = table.getSelectedRow();
		if ("N".equals(this.clearFlg)) {
			TParm action = table.getDataStore().getBuffer(
					table.getDataStore().PRIMARY);
			int rowCount = action.getCount();
			for (int i = 0; i < rowCount; i++) {
				if (i == selectRow)
					continue;
				TParm temp = action.getRow(i);
				if (temp.getValue("ORDER_CODE").equals(orderCode)) {
					falg = false;
				}
			}
		} else {
			int selId = (Integer) table.getDataStore().getItemData(selectRow,
					"#ID#", table.getDataStore().PRIMARY);
			String buff = table.getDataStore().isFilter() ? table
					.getDataStore().FILTER : table.getDataStore().PRIMARY;
					TParm action = table.getDataStore().getBuffer(buff);
					int rowCount = action.getCount();
					for (int i = 0; i < rowCount; i++) {
						int tempId = action.getInt("#ID#");
						if (tempId == selId)
							continue;
						TParm temp = action.getRow(i);
						if (temp.getValue("ORDER_CODE").equals(orderCode)) {
							falg = false;
						}
					}
		}
		return falg;
	}

	/**
	 * ��ǰ�Ƿ�����ͬҽ��
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            int
	 * @return boolean
	 */
	public boolean isCheckOrderSame(TParm parm, int type, int selRow) {
		boolean falg = true;
		String orderCode = parm.getValue("ORDER_CODE");
		TTable table = this.getTTable("TABLE" + (type + 1));
		TDS ds = odiObject.getDS("ODI_ORDER");
		if (type == 3) {
			int chnNewRow[] = ds.getNewRows();
			for (int temp : chnNewRow) {
				if (temp == selRow)
					continue;
				if (ds.getRowParm(temp).getValue("ORDER_CODE")
						.equals(orderCode)) {
					falg = false;
					break;
				}
			}
			return falg;
		}
		int newRow[] = table.getDataStore().getNewRows();
		// ��ǰѡ����
		for (int i : newRow) {
			if (i == selRow)
				continue;
			if (table.getDataStore().getRowParm(i).getValue("ORDER_CODE")
					.equals(orderCode)) {
				falg = false;
				break;
			}
		}
		return falg;
	}

	/**
	 * ����ҩƷԽȨ����
	 * 
	 * @param parm
	 * @param tabIndex
	 * @param result
	 * @return ===========pangben 2014-2-27
	 */
	private TParm antiOrderCheck(TParm parm, int tabIndex) {
		TParm result = new TParm();
		// ���֤��
		Object obj = parm.getData("LCS_CLASS_CODE");
		if (obj != null && obj.toString().length() != 0) {
			// System.out.println("֤�����������"+OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),""
			// + obj));
			if (!OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),
					"" + obj)) {
				// result.setErrCode(-1);
				if ((obj.toString().equals("2") || obj.toString().equals("3") || obj
						.toString().equals("4"))
						&& tabIndex == 1) {// Ϊ����ҩ���֤��tabIndex ==1����ҽ��
					// if (this.messageBox("��ʾ��Ϣ Tips", "�Ƿ�ԽȨ? ",
					// this.YES_NO_OPTION) == 0) {// �ǣ�ԽȨ����pha_anti����д����
					// parm.addData("INDEX", "0");
					// parm.setData("CASE_NO", caseNo);
					// parm.setData("MR_NO", mrNo);
					// parm.setData("OPT_USER", Operator.getID());
					// parm.setData("OPT_TERM", Operator.getIP());
					// parm.setData("PHA_USE_FLG", "N");
					// parm.setData("NODE_FLG", "N");
					// TParm result1 = TIOM_AppServer.executeAction(
					// "action.pha.PHAAntiAction", "onSavePha", parm);
					// if(result1.getErrCode()>=0){
					// TParm drParm = new TParm();
					// drParm.setData("CASE_NO", result1.getValue("CASE_NO"));
					// drParm.setData("PHA_SEQ", result1.getValue("PHA_SEQ"));
					// drParm.setData("SEQ_NO", result1.getValue("SEQ_NO"));
					// TParm newparm = (TParm) openDialog(
					// "%ROOT%\\config\\odi\\ODIOverRideDc.x",drParm);
					// }
					// //��ҽ�����͹���
					//                      
					//                      
					// }
					// result.setData("ANTICHECK_FLG","Y");
					// result.setErrCode(-1);
				} else {
					// ��û�д�ҽ����֤�գ�
					result.setErrCode(-1);
					result.setErrText("E0166");
				}
				return result;
			}
		}
		return result;
	}

	/**
	 * ��ҽ��ORDER�ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm creatOrderInfo(TParm parm, int type, String insertType, int row) {
		// this.messageBox("==============creatOrderInfo��ҽ��ORDER�ֵ===================");
		TParm result = new TParm();
		// �ж��Ƿ�סԺ����ҽ��
		if (!("Y".equals(parm.getValue("IPD_FIT_FLG")))) {
			result.setErrCode(-2);
			// ����סԺ����ҽ����
			result.setErrText("E0165");
			return result;
		}
		// this.messageBox_(parm);
		// �����Կ�������ҽ��ϸ��
		if (!"Y".equals(parm.getValue("ORDERSET_FLG"))
				&& ("RIS".equals(parm.getValue("CAT1_TYPE")) || "LIS"
						.equals(parm.getValue("CAT1_TYPE")))
				&& "NEW".equals(insertType)) {
			result.setErrCode(-3);
			// ����ҽ��ϸ����Կ�����
			result.setErrText("E0167");
			return result;
		}
		switch (type) {
		// ��ʱ
		case 0:
			// ������ʱҽ��
			result = this.returnTempOrderData(parm, row);
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// �жϿ����(�ӿ�)ORDER_CODE,ORG_CODE(ҽ������,ҩ������)
				// fux modify start
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// // ��治�㣡
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// fux modify end
				// �Ƿ��ǹ���ҩƷ
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;
				}
				// �Ƿ��ǿ�����
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;
				}
			}
			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// ����
		case 1:
			if ("LIS".equals(parm.getData("CAT1_TYPE").toString())
					|| "RIS".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// ����ҽ�����������ҽ�
				result.setErrText("E0168");
				return result;
			}
			// ���ó���ҽ��
			result = this.returnLongOrderData(parm, row);
			// System.out.println("����ҽ��===========================>>"+result);
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// // �жϿ����(�ӿ�)ORDER_CODE,ORG_CODE(ҽ������,ҩ������)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// �Ƿ��ǹ���ҩƷ
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// �Ƿ��ǿ�����
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// ��Ժ��ҩ
		case 2:
			if (!"PHA".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// ������ҩƷ��
				result.setErrText("E0169");
				return result;
			}
			// ���ó�Ժ��ҩ
			result = this.returnOutPhaOrderData(parm, row);
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// �жϿ����(�ӿ�)ORDER_CODE,ORG_CODE(ҽ������,ҩ������)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// �Ƿ��ǹ���ҩƷ
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// �Ƿ��ǿ�����
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;
			// ��ҩ��Ƭ
		case 3:
			// ������ҩ��Ƭ
			result = this.returnChinaPhaOrderData(parm, row);
			// �жϹ���ҩƷ�ȼ�
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// �жϿ����(�ӿ�)ORDER_CODE,ORG_CODE(ҽ������,ҩ������)
				// if ("N".equals(parm.getValue("IS_REMARK"))) {
				// if (!INDTool.getInstance().inspectIndStock(
				// result.getValue("EXEC_DEPT_CODE"),
				// result.getValue("ORDER_CODE"),
				// result.getDouble("DOSAGE_QTY"))) {
				// result.setErrCode(-3);
				// result.setErrText("E0052");
				// return result;
				// }
				// }
				// �Ƿ��ǹ���ҩƷ
				if (result.getValue("CTRLDRUGCLASS_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", ctrlDrugClassColor);
					return result;

				}
				// �Ƿ��ǿ�����
				if (result.getValue("ANTIBIOTIC_CODE").length() != 0) {
					result.setData("TABLEROW_COLOR", antibioticColor);
					return result;

				}
			}
			// �Ƿ���ҽ��ҩƷ
			if (OrderUtil.getInstance().isNhiPat(this.getCaseNo())) {
				if ("C".equals(parm.getValue("INSPAY_TYPE"))) {
					result.setData("TABLEROW_COLOR", nhiColor);
					return result;
				}
			}
			return result;

			// Ժǰ��ҩ
		case 5:
			if (!"PHA".equals(parm.getData("CAT1_TYPE").toString())) {
				result.setErrCode(-1);
				// Ժǰ��ҩ���������ҽ�
				result.setErrText("Ժǰ��ҩ���������ҩƷ��Ϣ��");
				return result;
			}
			// ����Ժǰ��ҩҽ��
			result = this.returnPAData(parm, row);
			return result;
		}
		return result;
	}

	/**
	 * ���س���ҽ�������
	 * 
	 * @return TParm
	 */
	public TParm returnLongOrderData(TParm parm, int row) {
		// this.messageBox("============returnLongOrderData===================");
		TParm result = new TParm();
		// �õ������Ϣ
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "UD", parm);
		// System.out.println("================>>>>>>>>>>>>>>>�õ������Ϣ"+parm);
		// �������
		result.setData("REGION_CODE", Operator.getRegion());
		// ��������
		result.setData("STATION_CODE", this.getStationCode());
		// ����
		result.setData("DEPT_CODE", this.getDeptCode());
		// ����ҽʦ
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// ��λ��
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// סԺ��
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// ������
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// �ݴ�ע��
		result.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		result.setData("ORDER_STATE", "N");
		// ����ҽ������
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		// ����ҽ��
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				// this.messageBox("========LINKMAIN_FLG ������ҽ��===========");
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0
						&& "Y".equals(this.getTTable(TABLE2).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					// this.messageBox("Link no====="+tempParm.getInt("LINK_NO"));
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("UD"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {

					result.setData("LINK_NO", getMaxLinkNo("UD"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			}
		}
		// ҽ������
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// ҽ������
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// ҽ�����ƣ���ʾ��
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// ��Ʒ��
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// ���
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// Ӣ������
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// ��ҩ����
			if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
				result.setData("MEDI_QTY", parm.getDouble("MEDI_QTY") <= 0 ? 1
						: parm.getDouble("MEDI_QTY"));// ����ȡֵ
			} else {
				// System.out.println("��ҩ����====="+actionParm.getData("MEDI_QTY"));
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// ��ҩ����
				if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
					result.setData("MEDI_QTY", 1);
				} else {
					result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
				}
			}
		}
		// ��ҩ��λ
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			// System.out.println("��ҩ��λ====="+actionParm.getData("MEDI_UNIT"));
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// Ƶ�δ���
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {

				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);

				// this.messageBox("========FREQ_CODE ��Ƶ��==========="+tempParm.getInt("LINK_NO"));
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						// this.messageBox("freq_code================="+tempParm.getData("FREQ_CODE"));

						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// Ƶ�δ���
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					// this.messageBox("FREQ_CODE");

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl

							result.setData("FREQ_CODE", tempParm
									.getData("FREQ_CODE"));
						} else {
							result.setData("FREQ_CODE", actionParm
									.getData("FREQ_CODE"));
						}
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}

		// ͣ��ʱ�� yanjing 20140613 yanjing ע20140613 ����ͣ��ʱ��
		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DATE", actionParm.getData("DC_DATE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DATE", tempParm.getData("DC_DATE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result
						.setData("DC_DATE", actionParm
								.getData("DC_DATE"));
					}
				} else {
					result.setData("DC_DATE", actionParm.getData("DC_DATE"));
				}
			}
		} else {
			if (parm.getData("DC_DATE") != null) {
				result.setData("DC_DATE", parm.getData("DC_DATE"));
			} else {
				if (row == 0) {
					result.setData("DC_DATE", actionParm.getData("DC_DATE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DATE", tempParm
									.getData("DC_DATE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DATE", actionParm
									.getData("DC_DATE"));
						}
					} else {
						result
						.setData("DC_DATE", actionParm
								.getData("DC_DATE"));
					}
				}
			}
		}

		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DR_CODE", actionParm.getData("DC_DR_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DR_CODE", tempParm
								.getData("DC_DR_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("DC_DR_CODE", actionParm
								.getData("DC_DR_CODE"));
					}
				} else {
					result.setData("DC_DR_CODE", actionParm
							.getData("DC_DR_CODE"));
				}
			}
		} else {
			if (parm.getData("DC_DR_CODE") != null) {
				result.setData("DC_DR_CODE", parm.getData("DC_DR_CODE"));
			} else {
				if (row == 0) {
					result.setData("DC_DR_CODE", actionParm
							.getData("DC_DR_CODE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DR_CODE", tempParm
									.getData("DC_DR_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DR_CODE", actionParm
									.getData("DC_DR_CODE"));
						}
					} else {
						result.setData("DC_DR_CODE", actionParm
								.getData("DC_DR_CODE"));
					}
				}
			}
		}

		if (!this.yyList) {
			if (row == 0) {
				result.setData("DC_DEPT_CODE", actionParm
						.getData("DC_DEPT_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("DC_DEPT_CODE", tempParm
								.getData("DC_DEPT_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("DC_DEPT_CODE", actionParm
								.getData("DC_DEPT_CODE"));
					}
				} else {
					result.setData("DC_DEPT_CODE", actionParm
							.getData("DC_DEPT_CODE"));
				}
			}
		} else {
			if (parm.getData("DC_DEPT_CODE") != null) {
				result.setData("DC_DEPT_CODE", parm.getData("DC_DEPT_CODE"));
			} else {
				if (row == 0) {
					result.setData("DC_DEPT_CODE", actionParm
							.getData("DC_DEPT_CODE"));
				} else {

					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("DC_DEPT_CODE", tempParm
									.getData("DC_DEPT_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("DC_DEPT_CODE", actionParm
									.getData("DC_DEPT_CODE"));
						}
					} else {
						result.setData("DC_DEPT_CODE", actionParm
								.getData("DC_DEPT_CODE"));
					}
				}
			}
		}

		if (!this.yyList) {
			// �÷�
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
			} else {
				// this.messageBox("========ROUTE_CODE ���÷�===========");
				TParm tempParm = this.getTTable(TABLE2).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ROUTE_CODE", tempParm
								.getData("ROUTE_CODE"));
					} else {
						// this.messageBox_(actionParm.getData("ROUTE_CODE"));
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getData("ROUTE_CODE"));
			} else {
				// �÷�
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE2).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE2)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("ROUTE_CODE", tempParm
									.getData("ROUTE_CODE"));
						} else {
							// this.messageBox_(actionParm.getData("ROUTE_CODE"));
							result.setData("ROUTE_CODE", actionParm
									.getData("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				}
			}
		}

		// ����
		if (row == 0) {
			result.setData("TAKE_DAYS", 1);
		} else {
			TParm tempParm = this.getTTable(TABLE2).getDataStore().getRowParm(
					row - 1);
			if (!parm.getBoolean("ORDERSET_FLG")
					&& parm.getValue("ORDERSET_CODE").length() == 0) {
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE2).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 1);
				}
			} else {
				result.setData("TAKE_DAYS", 1);
			}
		}
		// ��ҩ����
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// ��ҩ��λ
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// ��ҩ����
		result.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// �з�ҩע��
		result.setData("GIVEBOX_FLG", "N");
		// ����ע��
		result.setData("CONTINUOUS_FLG", "N");
		// ������
		result.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		result.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������
		if (row == 0) {
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE2).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {// shibl
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// ��������
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// ����ҽʦ
		result.setData("ORDER_DR_CODE", Operator.getID());
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEUD"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
						// modify
						// 20130312ȡ�������ڿ���
					}
				} else {// ������
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (this.getValueString("DEPT_CODEUD").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEUD"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					}
				}
			}
		} else {
			if (row == 0) {
				result.setData("EXEC_DEPT_CODE", parm
						.getValue("EXEC_DEPT_CODE").length() == 0 ? this
								.getDeptCode() : parm.getValue("EXEC_DEPT_CODE"));// shibl
				// modify
				// 20130312ȡ�������ڿ���
			} else {
				TParm temp = this.getTTable(TABLE2).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312ȡ�������ڿ���
					}
				} else {// ������
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE2).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", parm.getValue(
								"EXEC_DEPT_CODE").length() == 0 ? this
										.getDeptCode() : parm
										.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312ȡ�������ڿ���
					}
				}
			}
		}
		// ִ�м�ʦ
		result.setData("EXEC_DR_CODE", "");
		// // ͣ�ÿ���
		// result.setData("DC_DEPT_CODE", "");
		// // ͣ��ҽʦ
		// result.setData("DC_DR_CODE", "");
		// // ͣ��ʱ�䣬yanjing ע
		// result.setData("DC_DATE", "");
		// ͣ��ԭ�����
		result.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		result.setData("DR_NOTE", "");
		// ��ʿ��ע
		result.setData("NS_NOTE", "");
		// �������
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// �����ش���
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// ����ǩ��(��ҩʹ��)
		result.setData("RX_NO", "");
		// ����(��ҩʹ��)
		result.setData("PRESRT_NO", 0);
		// ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// �������
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// ��Ƭ������
		result.setData("DCT_TAKE_QTY", 0);
		// ��ҩ��ʽ(��ҩ����)
		result.setData("DCTAGENT_CODE", "");
		// ���������(��ҩ����)
		result.setData("PACKAGE_AMT", 0);
		// ����ҽ������ע��
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// ��˻�ʿ
		result.setData("NS_CHECK_CODE", "");
		// ��������ע��
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// ����ע��(����ҽ��ϸ���)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// ����ҽ��˳���
		Object obj = new Object();
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			// result.setData("ORDERSET_GROUP_NO", getMaxOrderSetGroupNo());
			result.setData("ORDERSET_GROUP_NO", getMaxOrderGroupNo());
		} else {
			obj = parm.getData("ORDERSET_GROUP_NO");
			if (obj != null) {
				result.setData("ORDERSET_GROUP_NO", parm
						.getData("ORDERSET_GROUP_NO"));
			} else {
				result.setData("ORDERSET_GROUP_NO", 0);
			}
		}
		// ����ҽ���������
		obj = parm.getData("ORDERSET_CODE");
		if (obj != null) {
			result.setData("ORDERSET_CODE", parm.getData("ORDERSET_CODE"));
		} else {
			if ("Y".equals(parm.getData("ORDERSET_FLG"))) {
				result.setData("ORDERSET_CODE", parm.getData("ORDER_CODE"));
			} else {
				result.setData("ORDERSET_CODE", "");
			}
		}
		// ҽ��ϸ����
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// �������
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// ������
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// ���뵥���
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// ��Ч����
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// ��ʿ���ʱ��
		result.setData("NS_CHECK_DATE", "");
		// ��˻�ʿDCȷ��
		result.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		result.setData("DC_NS_CHECK_DATE", "");
		// �����ҩ����
		result.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		result.setData("FRST_QTY", 0);
		// ���ҩʦ
		result.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		result.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		result.setData("INJ_ORG_CODE", "");
		// ����
		result.setData("URGENT_FLG", "N");
		// �ۼƿ�ҩ��
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// �ײ�����ʱ��
		// result.setData("START_DTTM",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("START_DTTM", StringTool.getTimestamp(StringTool
				.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
		// ҽ��ִ��ʱ��(����)
		// result.setData("ORDER_DATETIME",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("ORDER_DATETIME", StringTool.getString(result
				.getTimestamp("START_DTTM"), "HHmmss"));

		// ҽ������
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// ��ҩע��
		result.setData("DISPENSE_FLG", "N");
		// ҽ����ע,ҩƷ��ע
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * ���ִ�п��Ҵ���
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getExeDeptCodeST(String orderCode, int row, String table,
			String linkNo) {
		// System.out.println("==========order_Code========="+orderCode);
		String sql = "SELECT CAT1_TYPE,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("==========parm========="+parm);
		String exeDeptCode = "";
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE", 0))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					}
				} else if (this.getValueString("DEPT_CODEST").length() != 0) {
					exeDeptCode = this.getValueString("DEPT_CODEST");
				} else {
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
				}
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEST");
						}
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					} else {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEST");
					}
				} else
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0).length() == 0 ? this
							.getDeptCode()
							: parm.getValue("EXEC_DEPT_CODE", 0);// shibl
							// modify
							// 20130312ȡ�������ڿ���
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEST");
						}
					} else
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0)
						.length() == 0 ? this.getDeptCode() : parm
								.getValue("EXEC_DEPT_CODE", 0);// shibl
						// modify
						// 20130312ȡ�������ڿ���
				}
			}
		}
		// System.out.println("exeDeptCode========================"+exeDeptCode);
		return exeDeptCode;
	}

	/**
	 * ���ִ�п��Ҵ���
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getExeDeptCodeUD(String orderCode, int row, String table,
			String linkNo) {
		// System.out.println("==========order_Code========="+orderCode);
		String sql = "SELECT CAT1_TYPE,EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("==========parm========="+parm);
		String exeDeptCode = "";
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE", 0))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					}
				} else if (this.getValueString("DEPT_CODEUD").length() != 0) {
					exeDeptCode = this.getValueString("DEPT_CODEUD");
				} else {
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
				}
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEUD");
						}
					} else if (this.getValueString("DEPT_CODEUD").length() != 0) {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					} else {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
					} else {
						exeDeptCode = this.getValueString("DEPT_CODEUD");
					}
				} else
					exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0).length() == 0 ? this
							.getDeptCode()
							: parm.getValue("EXEC_DEPT_CODE", 0);// shibl
							// modify
							// 20130312ȡ�������ڿ���
			} else {
				TParm temp = this.getTTable(table).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(table).getDataStore()
								.getItemString(row - 1, "#NEW#"))
						&& temp.getValue("LINK_NO").equals(linkNo)) {
					exeDeptCode = temp.getValue("EXEC_DEPT_CODE");
				} else {
					if (isOpeFlg()) {// wanglong add 20150422
						if (parm.getValue("EXEC_DEPT_CODE", 0).length() != 0) {
							exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0);
						} else {
							exeDeptCode = this.getValueString("DEPT_CODEUD");
						}
					} else
						exeDeptCode = parm.getValue("EXEC_DEPT_CODE", 0)
						.length() == 0 ? this.getDeptCode() : parm
								.getValue("EXEC_DEPT_CODE", 0);// shibl
						// modify
						// 20130312ȡ�������ڿ���
				}
			}
		}
		// System.out.println("exeDeptCode========================"+exeDeptCode);
		return exeDeptCode;
	}

	/**
	 * ��ȡ�ٴ�·����ʱ��
	 * 
	 */
	private String getClpCode() {
		String clpCode = "";
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			clpCode = parm.getValue("CLNCPATH_CODE", 0);
		}
		return clpCode;
	}

	/**
	 * ��ȡ�ٴ�·��
	 * 
	 */
	private String getSchdCode() {
		String schdCode = "";
		String sql = "SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO = '" + caseNo
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			schdCode = parm.getValue("SCHD_CODE", 0);
		}
		return schdCode;
	}

	/**
	 * ������ʱҽ�������
	 * 
	 * @return TParm
	 */
	public TParm returnTempOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// �õ������Ϣ
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "ST", parm);
		// System.out.println("================>>>>>>>>>>>>>>>�õ������Ϣ"+actionParm);
		// this.messageBox_(actionParm.getValue("DOSE_CODE"));
		if (opeClpFlg) {
			result.setData("SCHD_CODE", this.getValue("SCHD_CODE"));// ʱ�̴���
			// pangben
			// 20150814
		} else {
			result.setData("SCHD_CODE", getSchdCode());// ʱ�̴��� yanjing 20140902
		}

		// �������
		result.setData("REGION_CODE", Operator.getRegion());
		// ��������
		result.setData("STATION_CODE", this.getStationCode());
		// ����
		result.setData("DEPT_CODE", this.getDeptCode());
		// ����ҽʦ
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// ��λ��
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// סԺ��
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// ������
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// �ݴ�ע��
		result.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		result.setData("ORDER_STATE", "N");
		// ����ҽ������
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		// Ӣ������
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		// ����ҽ��
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0
						&& "Y".equals(this.getTTable(TABLE1).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("ST"));
				} else {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE1).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));

					} else {
						result.setData("LINK_NO", "");

					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("ST"));

				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));

					} else {
						result.setData("LINK_NO", "");

					}
				}
			}
		}
		// ҽ������
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// ҽ������
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// ҽ�����ƣ���ʾ��
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// ��Ʒ��
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// ���
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		if (!this.yyList) {
			// ��ҩ����
			if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
				result.setData("MEDI_QTY", parm.getDouble("MEDI_QTY") <= 0 ? 1
						: parm.getDouble("MEDI_QTY"));// ����ȡֵ
			} else {
				// this.messageBox_(actionParm.getData("MEDI_QTY"));
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
					result.setData("MEDI_QTY", 1);
				} else {
					result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
				}
			}
		}
		// ��ҩ��λ
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// Ƶ�δ���
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				// System.out.println("tempParm"+tempParm);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// Ƶ�δ���
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					// System.out.println("tempParm"+tempParm);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE1)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("FREQ_CODE", tempParm
									.getData("FREQ_CODE"));
						} else {
							result.setData("FREQ_CODE", actionParm
									.getData("FREQ_CODE"));
						}
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// �÷�
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getValue("ROUTE_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE1).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ROUTE_CODE", tempParm
								.getValue("ROUTE_CODE"));
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getValue("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getValue("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getValue("ROUTE_CODE"));
			} else {
				// �÷�
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getValue("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE1).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE1)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {// shibl
							result.setData("ROUTE_CODE", tempParm
									.getValue("ROUTE_CODE"));
						} else {
							result.setData("ROUTE_CODE", actionParm
									.getValue("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getValue("ROUTE_CODE"));
					}
				}
			}
		}
		// ����
		if (row == 0) {
			result.setData("TAKE_DAYS", 1);
		} else {
			TParm tempParm = this.getTTable(TABLE1).getDataStore().getRowParm(
					row - 1);
			if (!parm.getBoolean("ORDERSET_FLG")
					&& parm.getValue("ORDERSET_CODE").length() == 0) {
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE1).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {// shibl
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 1);
				}
			} else {
				result.setData("TAKE_DAYS", 1);
			}
		}
		// ��ҩ����
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// ��ҩ��λ
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// ��ҩ����
		result.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// �з�ҩע��
		result.setData("GIVEBOX_FLG", "N");
		// ����ע��
		result.setData("CONTINUOUS_FLG", "N");
		// ������
		result.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		result.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������
		if (row == 0) {
			// $$================����ҽ��ʱ����������=====================$$//
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE1).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// ��������
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// ����ҽʦ

		// add by huangjw
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			Date date = TJDODBTool.getInstance().getDBTime();
			if (row == 0) {
				result.setData("ORDER_DR_CODE", ((TParm) this.getParameter())
						.getData("ODI", "ORDER_DR_CODE"));
				result.setData("ORDER_DATE", date);
				result.setData("NS_CHECK_DATE", date);
			} else {
				TParm nParm = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (nParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("ORDER_DR_CODE", nParm
								.getData("ORDER_DR_CODE"));
						result.setData("ORDER_DATE", nParm
								.getTimestamp("ORDER_DATE"));
						result.setData("NS_CHECK_DATE", nParm
								.getTimestamp("NS_CHECK_DATE"));
					} else {
						result.setData("ORDER_DR_CODE", ((TParm) this
								.getParameter())
								.getData("ODI", "ORDER_DR_CODE"));
						result.setData("ORDER_DATE", date);
						result.setData("NS_CHECK_DATE", date);
					}
				} else {
					result.setData("ORDER_DR_CODE", ((TParm) this
							.getParameter()).getData("ODI", "ORDER_DR_CODE"));
					result.setData("ORDER_DATE", date);
					result.setData("NS_CHECK_DATE", date);
				}
			}
			// add by huangjw
		} else {
			result.setData("ORDER_DR_CODE", Operator.getID());
			// ��ʿ���ʱ��
			result.setData("NS_CHECK_DATE", "");
		}
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEST"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getOrgCode());
					}
				} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEST"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				/**
				 * if (!parm.getBoolean("ORDERSET_FLG") &&
				 * parm.getValue("ORDERSET_CODE").length() == 0 &&
				 * temp.getInt("LINK_NO") != 0 &&
				 * "Y".equals(this.getTTable(TABLE1).getDataStore()
				 * .getItemString(row - 1, "#NEW#"))) {// shibl
				 * result.setData("EXEC_DEPT_CODE", temp
				 * .getValue("EXEC_DEPT_CODE")); } else { if
				 * (this.getValueString("DEPT_CODEST").length() == 0) {
				 * result.setData("EXEC_DEPT_CODE", this.getOrgCode()); } else {
				 * result.setData("EXEC_DEPT_CODE", this
				 * .getValue("DEPT_CODEST")); } }
				 */
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
						// modify
						// 20130312ȡ�������ڿ���
					}
				} else {// ������
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getOrgCode());
							}
						} else if (this.getValueString("DEPT_CODEST").length() != 0) {// wanglong
							// modify
							// 20150504
							result.setData("EXEC_DEPT_CODE", this
									.getValue("DEPT_CODEST"));
						} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
							result.setData("EXEC_DEPT_CODE", parm
									.getValue("EXEC_DEPT_CODE"));
						} else {
							result.setData("EXEC_DEPT_CODE", this.getOrgCode());
						}
					}
				}
			}
		} else {
			if (row == 0) {
				if (isOpeFlg()) {// wanglong add 20150422
					if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else if (this.getValueString("DEPT_CODEST").length() != 0) {
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEST"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getDeptCode());
					}
				} else
					result.setData("EXEC_DEPT_CODE", parm.getValue(
							"EXEC_DEPT_CODE").length() == 0 ? this
									.getDeptCode() : parm.getValue("EXEC_DEPT_CODE"));// shibl
				// modify
				// 20130312ȡ�������ڿ���
			} else {
				TParm temp = this.getTTable(TABLE1).getDataStore().getRowParm(
						row - 1);
				if (!this.yyList) {
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& temp.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312ȡ�������ڿ���
					}
				} else {// ������
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));
					} else if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0
							&& parm.getInt("LINK_NO") != 0
							&& !parm.getBoolean("LINKMAIN_FLG")
							&& "Y".equals(this.getTTable(TABLE1).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {// shibl
						result.setData("EXEC_DEPT_CODE", temp
								.getValue("EXEC_DEPT_CODE"));
					} else {
						if (isOpeFlg()) {// wanglong add 20150422
							if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
								result.setData("EXEC_DEPT_CODE", parm
										.getValue("EXEC_DEPT_CODE"));
							} else if (this.getValueString("DEPT_CODEST")
									.length() != 0) {
								result.setData("EXEC_DEPT_CODE", this
										.getValue("DEPT_CODEST"));
							} else {
								result.setData("EXEC_DEPT_CODE", this
										.getDeptCode());
							}
						} else
							result.setData("EXEC_DEPT_CODE", parm.getValue(
									"EXEC_DEPT_CODE").length() == 0 ? this
											.getDeptCode() : parm
											.getValue("EXEC_DEPT_CODE"));// shibl
						// modify
						// 20130312ȡ�������ڿ���
					}
				}
			}
		}
		// ִ�м�ʦ
		result.setData("EXEC_DR_CODE", "");
		// ͣ�ÿ���
		result.setData("DC_DEPT_CODE", "");
		// ͣ��ҽʦ
		result.setData("DC_DR_CODE", "");
		// ͣ��ʱ��
		result.setData("DC_DATE", "");
		// ͣ��ԭ�����
		result.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		result.setData("DR_NOTE", "");
		// ��ʿ��ע
		result.setData("NS_NOTE", "");
		// �������
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// �����ش���
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// ����ǩ��(��ҩʹ��)
		result.setData("RX_NO", "");
		// ����(��ҩʹ��)
		result.setData("PRESRT_NO", 0);
		// ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// �������
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// ��Ƭ������
		result.setData("DCT_TAKE_QTY", 0);
		// ��ҩ��ʽ(��ҩ����)
		result.setData("DCTAGENT_CODE", "");
		// ���������(��ҩ����)
		result.setData("PACKAGE_AMT", 0);
		// ����ҽ������ע��
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// ��˻�ʿ
		result.setData("NS_CHECK_CODE", "");
		// ��������ע��
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// ����ע��(����ҽ��ϸ���)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// ����ҽ��˳���
		Object obj = new Object();
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			result.setData("ORDERSET_GROUP_NO", getMaxOrderGroupNo());
		} else {
			obj = parm.getData("ORDERSET_GROUP_NO");
			if (obj != null) {
				result.setData("ORDERSET_GROUP_NO", parm
						.getData("ORDERSET_GROUP_NO"));
			} else {
				result.setData("ORDERSET_GROUP_NO", 0);
			}
		}
		// ����ҽ���������
		obj = parm.getData("ORDERSET_CODE");
		if (obj != null) {
			result.setData("ORDERSET_CODE", parm.getData("ORDERSET_CODE"));
		} else {
			if ("Y".equals(parm.getData("ORDERSET_FLG"))) {
				result.setData("ORDERSET_CODE", parm.getData("ORDER_CODE"));
			} else {
				result.setData("ORDERSET_CODE", "");
			}
		}
		// ҽ��ϸ����
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// �������
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// ������
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// ���뵥���
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// ��Ч����
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));

		// ��˻�ʿDCȷ��
		result.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		result.setData("DC_NS_CHECK_DATE", "");
		// �ײ�����ʱ��
		// result.setData("START_DTTM",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("START_DTTM", StringTool.getTimestamp(StringTool
				.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
		// ҽ��ִ��ʱ��(����)
		// result.setData("ORDER_DATETIME",StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		result.setData("ORDER_DATETIME", StringTool.getString(result
				.getTimestamp("START_DTTM"), "HHmmss"));
		// �����ҩ����
		result.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		result.setData("FRST_QTY", 0);
		// ���ҩʦ
		result.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		result.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		result.setData("INJ_ORG_CODE", "");
		// ����
		result.setData("URGENT_FLG", "N");
		// �ۼƿ�ҩ��
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// ҽ������
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// ��ҩע��
		result.setData("DISPENSE_FLG", "N");
		// ҽ����ע,ҩƷ��ע
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		// �������뵥�� wanglong add 20140707
		result.setData("OPBOOK_SEQ", this.getOpBookSeq());

		return result;
	}

	/**
	 * ���س�Ժ��ҩҽ�������
	 * 
	 * @return TParm
	 */
	public TParm returnOutPhaOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// �õ������Ϣ
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "DS", parm);
		// System.out.println("================>>>>>>>>>>>>>>>�õ������Ϣ"+actionParm);
		// �������
		result.setData("REGION_CODE", Operator.getRegion());
		// ��������
		result.setData("STATION_CODE", this.getStationCode());
		// ����
		result.setData("DEPT_CODE", this.getDeptCode());
		// ����ҽʦ
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// ��λ��
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// סԺ��
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// ������
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// �ݴ�ע��
		result.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		result.setData("ORDER_STATE", "N");
		// ����ҽ������
		if (!this.yyList) {
			result.setData("LINKMAIN_FLG", "N");
		} else {
			result.setData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG"));
		}
		if (!this.yyList) {
			if (row == 0) {
				result.setData("LINK_NO", "");
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if ("Y".equals(this.getTTable(TABLE3).getDataStore()
						.getItemString(row - 1, "#NEW#"))) {
					result.setData("LINK_NO",
							tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
									.getInt("LINK_NO"));
				} else {
					result.setData("LINK_NO", "");
				}
			}
		} else {
			if (row == 0) {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("DS"));
				} else {
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE3).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			} else {
				if (parm.getBoolean("LINKMAIN_FLG")) {
					result.setData("LINK_NO", getMaxLinkNo("DS"));
				} else {
					if (parm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						TParm tempParm = this.getTTable(TABLE3).getDataStore()
								.getRowParm(row - 1);
						result.setData("LINK_NO",
								tempParm.getInt("LINK_NO") == 0 ? "" : tempParm
										.getInt("LINK_NO"));
					} else {
						result.setData("LINK_NO", "");
					}
				}
			}
		}
		// ҽ������
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// ҽ������
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// ҽ�����ƣ���ʾ��
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// ��Ʒ��
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// ���
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// Ӣ������
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// ��ҩ����
			result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// ��ҩ����
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		}
		// ��ҩ��λ
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		if (!this.yyList) {
			// Ƶ�δ���
			if (row == 0) {
				result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("FREQ_CODE", tempParm.getData("FREQ_CODE"));
				} else {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				}
			}
		} else {
			if (parm.getData("FREQ_CODE") != null) {
				result.setData("FREQ_CODE", parm.getData("FREQ_CODE"));
			} else {
				// Ƶ�δ���
				if (row == 0) {
					result
					.setData("FREQ_CODE", actionParm
							.getData("FREQ_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("FREQ_CODE", tempParm
								.getData("FREQ_CODE"));
					} else {
						result.setData("FREQ_CODE", actionParm
								.getData("FREQ_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// �÷�
			if (row == 0) {
				result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (!parm.getBoolean("ORDERSET_FLG")
						&& parm.getValue("ORDERSET_CODE").length() == 0) {
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("ROUTE_CODE", tempParm
								.getData("ROUTE_CODE"));
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				} else {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				}
			}
		} else {
			if (parm.getData("ROUTE_CODE") != null) {
				result.setData("ROUTE_CODE", parm.getData("ROUTE_CODE"));
			} else {
				// �÷�
				if (row == 0) {
					result.setData("ROUTE_CODE", actionParm
							.getData("ROUTE_CODE"));
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (!parm.getBoolean("ORDERSET_FLG")
							&& parm.getValue("ORDERSET_CODE").length() == 0) {
						if (tempParm.getInt("LINK_NO") != 0
								&& "Y".equals(this.getTTable(TABLE3)
										.getDataStore().getItemString(row - 1,
												"#NEW#"))) {
							result.setData("ROUTE_CODE", tempParm
									.getData("ROUTE_CODE"));
						} else {
							result.setData("ROUTE_CODE", actionParm
									.getData("ROUTE_CODE"));
						}
					} else {
						result.setData("ROUTE_CODE", actionParm
								.getData("ROUTE_CODE"));
					}
				}
			}
		}
		if (!this.yyList) {
			// ����
			if (row == 0) {
				result.setData("TAKE_DAYS", 0);
			} else {
				TParm tempParm = this.getTTable(TABLE3).getDataStore()
						.getRowParm(row - 1);
				if (tempParm.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("TAKE_DAYS", tempParm.getData("TAKE_DAYS"));
				} else {
					result.setData("TAKE_DAYS", 0);
				}
			}
		} else {
			if (parm.getData("TAKE_DAYS") != null) {
				result.setData("TAKE_DAYS", parm.getData("TAKE_DAYS"));
			} else {
				// ����
				if (row == 0) {
					result.setData("TAKE_DAYS", 0);
				} else {
					TParm tempParm = this.getTTable(TABLE3).getDataStore()
							.getRowParm(row - 1);
					if (tempParm.getInt("LINK_NO") != 0
							&& "Y".equals(this.getTTable(TABLE3).getDataStore()
									.getItemString(row - 1, "#NEW#"))) {
						result.setData("TAKE_DAYS", tempParm
								.getData("TAKE_DAYS"));
					} else {
						result.setData("TAKE_DAYS", 0);
					}
				}
			}
		}
		// ��ҩ����
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			if (parm.getDouble("DOSAGE_QTY") <= 0)
				result.setData("DOSAGE_QTY", 1);
			else
				result.setData("DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// ��ҩ��λ
		result.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));
		// ��ҩ����
		result.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		result.setData("DISPENSE_UNIT", actionParm.getData("DISPENSE_UNIT"));
		// �з�ҩע��
		result.setData("GIVEBOX_FLG", "N");
		// ����ע��
		result.setData("CONTINUOUS_FLG", "N");
		// ������
		result.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		result.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������
		if (row == 0) {
			result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		} else {
			TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
					row - 1);
			if (temp.getInt("LINK_NO") != 0
					&& "Y".equals(this.getTTable(TABLE3).getDataStore()
							.getItemString(row - 1, "#NEW#"))) {
				result.setData("EFF_DATE", temp.getData("EFF_DATE"));
			} else {
				result
				.setData("EFF_DATE", TJDODBTool.getInstance()
						.getDBTime());
			}
		}
		// ��������
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// ����ҽʦ
		result.setData("ORDER_DR_CODE", Operator.getID());
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (row == 0) {
				if (this.getValueString("DEPT_CODEDS").length() != 0) {// wanglong
					// modify
					// 20150504
					result.setData("EXEC_DEPT_CODE", this
							.getValue("DEPT_CODEDS"));
				} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
					result.setData("EXEC_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", this.getOrgCode());
				}
			} else {
				TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("EXEC_DEPT_CODE", temp
							.getValue("EXEC_DEPT_CODE"));
				} else {
					if (this.getValueString("DEPT_CODEDS").length() != 0) {// wanglong
						// modify
						// 20150504
						result.setData("EXEC_DEPT_CODE", this
								.getValue("DEPT_CODEDS"));
					} else if (parm.getValue("EXEC_DEPT_CODE").length() != 0) {
						result.setData("EXEC_DEPT_CODE", parm
								.getValue("EXEC_DEPT_CODE"));
					} else {
						result.setData("EXEC_DEPT_CODE", this.getOrgCode());
					}
				}
			}
		} else {
			if (row == 0) {
				result.setData("EXEC_DEPT_CODE", parm
						.getValue("EXEC_DEPT_CODE").length() == 0 ? Operator
								.getDept() : parm.getValue("EXEC_DEPT_CODE"));
			} else {
				TParm temp = this.getTTable(TABLE3).getDataStore().getRowParm(
						row - 1);
				if (temp.getInt("LINK_NO") != 0
						&& "Y".equals(this.getTTable(TABLE3).getDataStore()
								.getItemString(row - 1, "#NEW#"))) {
					result.setData("EXEC_DEPT_CODE", temp
							.getValue("EXEC_DEPT_CODE"));
				} else {
					result.setData("EXEC_DEPT_CODE", parm.getValue(
							"EXEC_DEPT_CODE").length() == 0 ? Operator
									.getDept() : parm.getValue("EXEC_DEPT_CODE"));
				}
			}
		}
		// ִ�м�ʦ
		result.setData("EXEC_DR_CODE", "");
		// ͣ�ÿ���
		result.setData("DC_DEPT_CODE", "");
		// ͣ��ҽʦ
		result.setData("DC_DR_CODE", "");
		// ͣ��ʱ��
		result.setData("DC_DATE", "");
		// ͣ��ԭ�����
		result.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		result.setData("DR_NOTE", "");
		// ��ʿ��ע
		result.setData("NS_NOTE", "");
		// �������
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// �����ش���
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// ����ǩ��(��ҩʹ��)
		result.setData("RX_NO", this.getValueString("RX_NO"));
		// ����(��ҩʹ��)
		result.setData("PRESRT_NO", this.getTComboBox("RX_NO")
				.getSelectedNode().getPy1());
		// ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// �������
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// ��Ƭ������
		result.setData("DCT_TAKE_QTY", 0);
		// ��ҩ��ʽ(��ҩ����)
		result.setData("DCTAGENT_CODE", "");
		// ���������(��ҩ����)
		result.setData("PACKAGE_AMT", 0);
		// ����ҽ������ע��
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// ��˻�ʿ
		result.setData("NS_CHECK_CODE", "");
		// ��������ע��
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// ����ע��
		result.setData("HIDE_FLG", "N");
		// ����ҽ��˳���
		result.setData("ORDERSET_GROUP_NO", 0);
		// ����ҽ������
		result.setData("ORDERSET_CODE", "");
		// ҽ��ϸ����
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// �������
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// ������
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// ���뵥���
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// ��Ч����
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// ��ʿ���ʱ��
		result.setData("NS_CHECK_DATE", "");
		// ��˻�ʿDCȷ��
		result.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		result.setData("DC_NS_CHECK_DATE", "");
		// �ײ�����ʱ��
		result.setData("START_DTTM", StringTool.getString(TJDODBTool
				.getInstance().getDBTime(), "yyyyMMddHHmmss"));
		// �����ҩ����
		result.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		result.setData("FRST_QTY", 0);
		// ���ҩʦ
		result.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		result.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		result.setData("INJ_ORG_CODE", "");
		// ����
		result.setData("URGENT_FLG", "N");
		// �ۼƿ�ҩ��
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// ҽ������
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// ��ҩע��
		result.setData("DISPENSE_FLG", "N");
		// ҽ����ע,ҩƷ��ע
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * ������ҩҽ�������
	 * 
	 * @return TParm
	 */
	public TParm returnChinaPhaOrderData(TParm parm, int row) {
		TParm result = new TParm();
		// �õ������Ϣ
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "IG", parm);
		// System.out.println("================>>>>>>>>>>>>>>>�õ������Ϣ"+actionParm);
		// �������
		result.setData("REGION_CODE", Operator.getRegion());
		// ��������
		result.setData("STATION_CODE", this.getStationCode());
		// ����
		result.setData("DEPT_CODE", this.getDeptCode());
		// ����ҽʦ
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// ��λ��
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// סԺ��
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// ������
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// �ݴ�ע��
		result.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		result.setData("ORDER_STATE", "N");
		// ����ҽ������
		result.setData("LINKMAIN_FLG", "N");
		// ���Ӻ�
		result.setData("LINK_NO", "");
		// ҽ������
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// ҽ������
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// ��Ʒ��
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// ���
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// Ӣ������
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		if (!this.yyList) {
			// ��ҩ����
			result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		} else {
			if (parm.getData("MEDI_QTY") != null) {
				result.setData("MEDI_QTY", parm.getData("MEDI_QTY"));
			} else {
				// ��ҩ����
				result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
			}
		}
		// ��ҩ��λ
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		} else {
			result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
		}
		// Ƶ�δ���
		result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
		// �÷�
		result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
		// ����
		result.setData("TAKE_DAYS", this.getValueInt("RF"));
		// ��ҩ����
		if (!("PHA".equals(parm.getValue("CAT1_TYPE")))) {
			result.setData("DOSAGE_QTY", 1);
		} else {
			result.setData("DOSAGE_QTY", 0);
		}
		// ��ҩ��λ
		result.setData("DOSAGE_UNIT", actionParm.getData("MEDI_UNIT"));
		// ��ҩ����
		result.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		result.setData("DISPENSE_UNIT", actionParm.getData("MEDI_UNIT"));
		// �з�ҩע��
		result.setData("GIVEBOX_FLG", "N");
		// ����ע��
		result.setData("CONTINUOUS_FLG", "N");
		// ������
		result.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		result.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������
		result.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		// ��������
		result.setData("ORDER_DEPT_CODE", Operator.getDept());// ====pangben
		// 2014-8-14
		// ����ҽʦ
		result.setData("ORDER_DR_CODE", Operator.getID());
		// ִ�п���
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			if (this.getValueString("DEPT_CODEIG").length() == 0) {
				result.setData("EXEC_DEPT_CODE", this.getOrgCode());
			} else {
				result.setData("EXEC_DEPT_CODE", this.getValue("DEPT_CODEIG"));
			}
		} else {
			result.setData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE")
					.length() == 0 ? Operator.getDept() : parm
							.getValue("EXEC_DEPT_CODE"));
		}
		// ִ�м�ʦ
		result.setData("EXEC_DR_CODE", "");
		// ͣ�ÿ���
		result.setData("DC_DEPT_CODE", "");
		// ͣ��ҽʦ
		result.setData("DC_DR_CODE", "");
		// ͣ��ʱ��
		result.setData("DC_DATE", "");
		// ͣ��ԭ�����
		result.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		result.setData("DR_NOTE", this.getValueString("IG_DR_NOTE"));
		// ��ʿ��ע
		result.setData("NS_NOTE", "");
		// �������
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// �����ش���
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// ����ǩ��(��ҩʹ��)
		result.setData("RX_NO", this.getValueString("IG_RX_NO"));
		// ����(��ҩʹ��)
		result.setData("PRESRT_NO", this.getTComboBox("IG_RX_NO")
				.getSelectedNode().getPy1());
		// ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// �������
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// ��Ƭ������
		result.setData("DCT_TAKE_QTY", this.getValue("YPJL"));
		// ��ҩ��ʽ(��ҩ����)
		result.setData("DCTAGENT_CODE", this.getValueString("IG_DCTAGENT"));
		// ����巨
		// this.getTComboBox("DCTAGENT_COMBO").setSelectedIndex(0);
		if (!this.yyList) {
			result.setData("DCTEXCEP_CODE", "");
		} else {
			if (parm.getData("DCTEXCEP_CODE") != null) {
				result.setData("DCTEXCEP_CODE", parm.getData("DCTEXCEP_CODE"));
			} else {
				result.setData("DCTEXCEP_CODE", "");
			}
		}
		// ���������(��ҩ���ø���*Ƶ��)
		int rfNum = this.getValueInt("RF");
		String freqCode = this.getValueString("IGFREQCODE");
		int rfNumCount = OdiUtil.getInstance().getPACKAGE_AMT(rfNum, freqCode);
		result.setData("PACKAGE_AMT", rfNumCount);
		// ����ҽ������ע��
		result.setData("SETMAIN_FLG", parm.getData("ORDERSET_FLG"));
		// ��˻�ʿ
		result.setData("NS_CHECK_CODE", "");
		// ��������ע��
		result.setData("INDV_FLG", parm.getData("INDV_FLG"));
		// ����ע��(����ҽ��ϸ���)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			result.setData("HIDE_FLG", parm.getData("HIDE_FLG"));
		} else {
			result.setData("HIDE_FLG", "N");
		}
		// ����ҽ��˳���
		result.setData("ORDERSET_GROUP_NO", 0);
		// ����ҽ������
		result.setData("ORDERSET_CODE", "");
		// ҽ��ϸ����
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// �������
		result.setData("RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
		// ������
		result.setData("OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
		// ���뵥���
		result.setData("MR_CODE", parm.getData("MR_CODE"));
		// FILE_NO
		result.setData("FILE_NO", "");
		// ��Ч����
		result.setData("DEGREE_CODE", parm.getData("DEGREE_CODE"));
		// ��ʿ���ʱ��
		result.setData("NS_CHECK_DATE", "");
		// ��˻�ʿDCȷ��
		result.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		result.setData("DC_NS_CHECK_DATE", "");
		// �ײ�����ʱ��
		result.setData("START_DTTM", StringTool.getString(TJDODBTool
				.getInstance().getDBTime(), "yyyyMMddHHmmss"));
		// �����ҩ����
		result.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		result.setData("FRST_QTY", 0);
		// ���ҩʦ
		result.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		result.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		result.setData("INJ_ORG_CODE", "");
		// ����
		result.setData("URGENT_FLG", ((TCheckBox) this.getComponent("URGENT"))
				.isSelected() ? "Y" : "N");
		// �ۼƿ�ҩ��
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("ACUMMEDI_QTY", 0);
		} else {
			result.setData("ACUMMEDI_QTY", 1);
		}
		// ҽ������
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// ��ҩע��
		result.setData("DISPENSE_FLG", "N");
		// �Ա�
		result.setData("RELEASE_FLG", ((TCheckBox) this.getComponent("OWNED"))
				.isSelected() ? "Y" : "N");
		// ҽ����ע,ҩƷ��ע
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		return result;
	}

	/**
	 * ��ѯ��һҽ��Ƶ��
	 * 
	 * @return String
	 */
	public String queryFreqCodeUp() {
		TDS dsOrder = odiObject.getDS("ODI_ORDER");
		TParm parmBuff = dsOrder.getBuffer(dsOrder.PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("FREQ_CODE", lastRow - 1);
		return "" + obj;
	}

	/**
	 * ����ҽ��ϸ��������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getPhaBaseData(String orderCode, String orderCat1,
			String type, TParm parm) {
		// System.out.println("����ҽ��ϸ��������������"+orderCode+"   ::   "+orderCat1);
		TParm result = new TParm();
		// ��ʱҽ��
		if ("ST".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start ��Ӳ���
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop

				result = OdiMainTool.getInstance().queryPhaBase(action);

				// ��ʱ��ҩԤ��Ƶ��
				if (odiObject.getAttribute(odiObject.ODI_UDD_STAT_CODE)
						.toString().length() == 0) {
					// ��һҽ��Ƶ��
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", odiObject
							.getAttribute(odiObject.ODI_UDD_STAT_CODE));
				}
				// �÷�
				int row = this.getTTable(TABLE1).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE1).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE1).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// ��ҩ��λ==��浥λ
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// ��ҩ��λ
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// Ĭ�Ͽ�ҩ����
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// ����ҩƷ����
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// �����ش���
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("�õ�PHA_BASE����" + result);
			} else {
				// ��ʱ����Ԥ��Ƶ��
				if (odiObject.getAttribute(odiObject.ODI_ODI_STAT_CODE)
						.toString().length() == 0) {
					// ��һҽ��Ƶ��
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", odiObject
							.getAttribute(odiObject.ODI_ODI_STAT_CODE));
				}
				// �÷�
				int row = this.getTTable(TABLE1).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE1).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",this.getTTable(TABLE1).getDataStore().getRowParm(row
						// -1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				// DISPENSE_UNIT��ҩ��λ
				result.setData("DISPENSE_UNIT", parm.getData("UNIT_CODE"));
				// MEDI_UNIT
				result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
			}
		}
		// ����ҽ��
		if ("UD".equals(type)) {
			// this.messageBox("========getPhaBaseData ����ҽ��============");
			if ("PHA".equals(orderCat1.trim())) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start ��Ӳ���
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop
				result = OdiMainTool.getInstance().queryPhaBase(action);
				// this.messageBox_(result);
				// ������ҩԤ��Ƶ��
				if (result.getValue("FREQ_CODE", 0).length() == 0) {
					// ��һҽ��Ƶ��
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
					// //============pangben modify 20110609 start Ĭ��Ƶ������ʹ��
					// result.setData("FREQ_CODE",
					// odiObject.getAttribute(odiObject.
					// ODI_UDD_STAT_CODE));
					// //============pangben modify 20110609 stop
				}
				// �÷�
				int row = this.getTTable(TABLE2).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE2).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE2).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// ��ҩ��λ==��浥λ
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// ��ҩ��λ
				// System.out.println("��ҩ��λ"+result.getData("MEDI_UNIT", 0));
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// Ĭ�Ͽ�ҩ����
				// System.out.println("Ĭ�Ͽ�ҩ����"+result.getData("MEDI_QTY",0));
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// ����ҩƷ����
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// System.out.println("�õ�PHA_BASE����" + result);
				// �����ش���
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
			} else {
				// ���ڴ���Ԥ��Ƶ��
				if (odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG)
						.toString().length() == 0) {
					// ��һҽ��Ƶ��
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					// this.messageBox("come in2111111================"+odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG).toString());
					// $$==============Modified by lx
					// 2012/02/24������ڲ���������ȡ������START=========================$$//
					if (odiObject.getAttribute(odiObject.ODI_ODI_DEFA_FREG) != null
							&& odiObject.getAttribute(
									odiObject.ODI_ODI_DEFA_FREG).toString()
							.length() > 0) {
						result.setData("FREQ_CODE", odiObject.getAttribute(
								odiObject.ODI_ODI_DEFA_FREG).toString());
					} else {
						// ===============pangben modfiy 20110608 Ĭ��Ƶ������ʹ��
						result.setData("FREQ_CODE", odiObject
								.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					}
					// $$==============Modified by lx
					// 2012/02/24������ڲ���������ȡ������END=========================$$//
				}
				// �÷�
				int row = this.getTTable(TABLE2).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE2).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE2).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				// DISPENSE_UNIT��ҩ��λ
				result.setData("DISPENSE_UNIT", parm.getData("UNIT_CODE"));
				// MEDI_UNIT
				result.setData("MEDI_UNIT", parm.getData("UNIT_CODE"));
			}
		}
		// ��Ժ��ҩҽ��
		if ("DS".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start ��Ӳ���
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop

				result = OdiMainTool.getInstance().queryPhaBase(action);
				// ��ʱ��ҩԤ��Ƶ��
				if (result.getValue("FREQ_CODE", 0).length() == 0) {
					// ��һҽ��Ƶ��
					result.setData("FREQ_CODE", queryFreqCodeUp());
				} else {
					// ===============pangben modfiy 20110608 Ĭ��Ƶ������ʹ��
					// result.setData("FREQ_CODE",odiObject.getAttribute(odiObject.ODI_UDD_STAT_CODE));
					result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
				}
				// �÷�
				int row = this.getTTable(TABLE3).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE3).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						// result.setData("ROUTE_CODE",
						// this.getTTable(TABLE3).getDataStore().getRowParm(row-1).getValue("ROUTE_CODE"));
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// ��ҩ��λ==��浥λ
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// ��ҩ��λ
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// Ĭ�Ͽ�ҩ����
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// ����ҩƷ����
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// �����ش���
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("�õ�PHA_BASE����" + result);
			}
		}
		// ��ҩ��Ƭҽ��
		if ("IG".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				// =============pangben modify 20110516 start ��Ӳ���
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());
				// =============pangben modify 20110516 stop
				result = OdiMainTool.getInstance().queryPhaBase(action);
				// סԺ��ҩ��ҩԤ��Ƶ��
				result.setData("FREQ_CODE", this.getValueString("IGFREQCODE"));
				result.setData("ROUTE_CODE", this.getValueString("IG_ROUTE"));
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// ��ҩ��λ==��浥λ
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// ��ҩ��λ
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// Ĭ�Ͽ�ҩ����
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// ����ҩƷ����
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// �����ش���
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
				// System.out.println("�õ�PHA_BASE����" + result);
			}
		}
		// Ժǰ��ҩ
		if ("PA".equals(type)) {
			if ("PHA".equals(orderCat1.trim())) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", orderCode);
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0)
					action.setData("REGION_CODE", Operator.getRegion());

				result = OdiMainTool.getInstance().queryPhaBase(action);
				result.setData("FREQ_CODE", result.getData("FREQ_CODE", 0));
				// �÷�
				int row = this.getTTable(TABLE6).getSelectedRow();
				if (row == 0) {
					result.setData("ROUTE_CODE", result
							.getData("ROUTE_CODE", 0));
				} else {
					if (this.getTTable(TABLE6).getDataStore().getRowParm(
							row - 1).getInt("LINK_NO") != 0) {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					} else {
						result.setData("ROUTE_CODE", result.getData(
								"ROUTE_CODE", 0));
					}
				}
				result.setData("PHA_TYPE", result.getData("PHA_TYPE", 0));
				result.setData("DOSE_CODE", result.getData("DOSE_TYPE", 0));
				// ��ҩ��λ==��浥λ
				result
				.setData("DISPENSE_UNIT", result.getData("STOCK_UNIT",
						0));
				// ��ҩ��λ
				result.setData("MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				// Ĭ�Ͽ�ҩ����
				result.setData("MEDI_QTY", result.getData("MEDI_QTY", 0));
				// ����ҩƷ����
				result.setData("CTRLDRUGCLASS_CODE", result.getData(
						"CTRLDRUGCLASS_CODE", 0));
				// �����ش���
				result.setData("ANTIBIOTIC_CODE", result.getData(
						"ANTIBIOTIC_CODE", 0));
			}
		}
		return result;
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
	 * �õ������
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public boolean isStopBillFlg() {
		return stopBillFlg;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public Timestamp getBirthDay() {
		return birthDay;
	}

	public String getTel() {
		return tel;
	}

	public String getSexCode() {
		return sexCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getPatName1() {
		return patName1;
	}

	public String getIdNo() {
		return idNo;
	}

	public String getCompanyDesc() {
		return companyDesc;
	}

	public String getAddress() {
		return address;
	}

	public String getMainDiag() {
		return mainDiag;
	}

	public String getCtzCode() {
		return ctzCode;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public boolean isSaveFlg() {
		return saveFlg;
	}

	public String getIcdCode() {
		return icdCode;
	}

	public String getIcdDesc() {
		return icdDesc;
	}

	public String getStationCode() {
		return stationCode;
	}

	public boolean isOidrFlg() {
		return oidrFlg;
	}

	/**
	 * ���þ����
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public void setStopBillFlg(boolean stopBillFlg) {
		this.stopBillFlg = stopBillFlg;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setBirthDay(Timestamp birthDay) {
		this.birthDay = birthDay;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public void setPatName1(String patName1) {
		this.patName1 = patName1;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public void setCompanyDesc(String companyDesc) {
		this.companyDesc = companyDesc;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setMainDiag(String mainDiag) {
		this.mainDiag = mainDiag;
	}

	public void setCtzCode(String ctzCode) {
		this.ctzCode = ctzCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setSaveFlg(boolean saveFlg) {
		this.saveFlg = saveFlg;
	}

	public void setIcdCode(String icdCode) {
		this.icdCode = icdCode;
	}

	public void setIcdDesc(String icdDesc) {
		this.icdDesc = icdDesc;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setOidrFlg(boolean oidrFlg) {
		this.oidrFlg = oidrFlg;
	}

	public boolean isIcuFlg() {
		return icuFlg;
	}

	public void setIcuFlg(boolean icuFlg) {
		this.icuFlg = icuFlg;
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

	/*public String getDayOpeCode() {
		return dayOpeCode;
	}

	public void setDayOpeCode(String dayOpeCode) {
		this.dayOpeCode = dayOpeCode;
	}*/
	public String getDayOpeFlg() {
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {
		this.dayOpeFlg = dayOpeFlg;
	}

	/**
	 * �Ƿ��������޸���
	 * 
	 * @return boolean
	 */
	public boolean checkNewModifRowCount() {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		int newCount = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			newCount++;
		}
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
		if (newCount + modifRow.length + delCount > 0)
			falg = true;
		return falg;
	}

	/**
	 * ɾ������
	 */
	public void delRowNull() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		// System.out.println("tab.getSelectedIndex()!!!============================="+tab.getSelectedIndex());
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			table = getTTable(TABLE1);
			table
			.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='ST' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='UD' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='DS' AND #ACTIVE#='N'");
			// table.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y') OR RX_KIND='IG' AND #ACTIVE#='N'");
			// ds.setSort("ORDER_NO,ORDER_SEQ");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(false);
			break;
		case 4:
			// ������¼
			table = getTTable(GMTABLE);
			table.setFilter("#NEW#='Y' OR #ACTIVE#='N'");
			// table.setSort("ADM_DATE");
			((TMenuItem) this.getComponent("delTableRow")).setEnabled(true);
			break;

		}
		// ��ҩ��Ƭ
		if (tab.getSelectedIndex() == 3) {
			if (!odiObject.filter(table, ds, this.isStopBillFlg())) {
				this.messageBox("E0155");
			}
		} else {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		this.antflg = false;
		// ��ʼ��ҽ������
		initOrderStart();
		// ���һ��
		// table.getDataStore().showDebug();
		this.onAddRow();
		// ��¼��ǰҳ��INDEX
		indexPage = tab.getSelectedIndex();
		// ������
		lockRowOrder();
	}

	/**
	 * ��ҽ�޸ĵ���
	 */
	public void onChangeIGOrder() {
		if (this.getValueString("IG_RX_NO").length() == 0) {
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm igParm = new TParm();
		// �������
		int rfNum = this.getValueInt("RF");
		String freqCode = this.getValueString("IGFREQCODE");
		int rfNumCount = OdiUtil.getInstance().getPACKAGE_AMT(rfNum, freqCode);
		igParm.setData("PACKAGE_AMT", rfNumCount);
		// Ƶ��
		igParm.setData("FREQ_CODE", this.getValueString("IGFREQCODE"));
		// �÷�
		igParm.setData("ROUTE_CODE", this.getValueString("IG_ROUTE"));
		// ҩ��(ִ�п���)
		igParm.setData("EXEC_DEPT_CODE", this.getValueString("DEPT_CODEIG"));
		// ��ҩ��ʽ
		igParm.setData("DCTAGENT_CODE", this.getValueString("IG_DCTAGENT"));
		// ��ע
		igParm.setData("DR_NOTE", this.getValueString("IG_DR_NOTE"));
		// ����
		igParm.setData("URGENT_FLG", ((TCheckBox) this.getComponent("URGENT"))
				.isSelected() ? "Y" : "N");
		// �Ա�
		igParm.setData("RELEASE_FLG", ((TCheckBox) this.getComponent("OWNED"))
				.isSelected() ? "Y" : "N");
		// ������
		String rxNoIG = this.getValueString("IG_RX_NO");
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!ds.isActive(i))
				continue;
			TParm temp = ds.getRowParm(i);
			if (temp.getValue("RX_NO").equals(rxNoIG)) {
				odiObject.setItem(ds, i, igParm);
			}
		}
	}

	/**
	 * ����
	 */
	public boolean onSave() {
		BYXflg=false;
		// this.messageBox("come save.");
		// odiObject.getDS("ODI_ORDER").showDebug();
		// �����Կ�ҽ���ļ���������1��������Ժ 2�� ���ﲻ���Կ�ҽ����
		if (!this.isSaveFlg()) {
			// �˲����Ѿ���Ժ�����Կ���ҽ����
			this.messageBox("E0163");
			initDataStoreToTable();
			this.clearFlg = "N";
			return false;
		}

		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return false;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return false;
		}// modified by WangQing 20170428 end
		//==pangben 2016-9-13
		try {
			TParm clpChekcParm=IBSTool.getInstance().onCheckClpDiff(caseNo);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//��ADM_INP���и��¸Ĳ���Ϊ���Ƹ�Ⱦ ADM_INP INF_FLG��ΪY��Ⱦ  ��ΪNΪû�и�Ⱦ  -start machao 20170615 
		String sqlUpdate = "UPDATE ADM_INP SET INF_FLG ='"+this.getValueString("INF_FLG")+"' WHERE CASE_NO='"+this.getCaseNo()+"'";
		try{
			TJDODBTool.getInstance().update(sqlUpdate);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//-end machao 20170615
		// ��ǰѡ��ҳǩ
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// TABLE���ܸı丶ֵ
		acceptTextTable(new String[] { TABLE1, TABLE2, TABLE3, TABLE4, GMTABLE,
				TABLE6 });

		if (odiStationDrools.fireRules(odiObject, 1)) {
			return false;
		}

		TDS ds = odiObject.getDS("ODI_ORDER");
		// delRowNull();
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		// ===pangben 2015-8-14 ��Ժ����ʱ���ٴ�·��У�����
		String orderCode = TConfig.getSystemValue("clp.orderCode");// ��ó�Ժ֪ͨҽ��
		boolean clpDsFlg = false;// У���Ƿ������ʾ���
		int newCount = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			TParm tempParm = ds.getRowParm(i, buff);// =====pangben 2015-8-14
			// opeParm.setRowData(newCount, tempParm);
			// ���ҽ���Ƚϣ�·��У��ʹ��
			if (null != tempParm.getValue("ORDER_CODE")
					&& tempParm.getValue("ORDER_CODE").equals(orderCode)) {
				clpDsFlg = true;// ���������ݴ������õ�ҽ��
			}
			newCount++;
		}
		// opeParm.setCount(newCount);
		// this.messageBox("==newCount=="+newCount);
		// ===================���δ���ҽ��ϸ��ORDERSET_GROUP_NO�����ͬ��BUG���ڱ���ǰ��Ⲣ��ʾ
		TParm vParm = ds.getBuffer(ds.FILTER);// wanglong add 20140523
		for (int i = 0; i < vParm.getCount(); i++) {
			if (vParm.getBoolean("#NEW#", i)
					&& !vParm.getValue("ORDERSET_CODE", i).equals("")
					&& vParm.getValue("SETMAIN_FLG", i).equals("N")) {
				int j = 0;
				for (; j < vParm.getCount(); j++) {
					if (j == i) {
						continue;
					}
					if (vParm.getBoolean("#NEW#", j)
							&& !vParm.getValue("ORDERSET_CODE", j).equals("")
							&& vParm.getValue("SETMAIN_FLG", j).equals("Y")
							&& vParm.getValue("ORDERSET_CODE", j).equals(
									vParm.getValue("ORDERSET_CODE", i))
							&& vParm.getValue("ORDERSET_GROUP_NO", j).equals(
									vParm.getValue("ORDERSET_GROUP_NO", i))) {
						break;
					}
				}
				if (j == vParm.getCount()) {
					System.out.println("-----------Debug begin---------");
					ds.showDebug();
					System.out.println("-----------Debug end-----------");
					this.messageBox("����ҽ����ϸ����ڲ�ͬ��GROUP_NO����������BUG������ϵ����");
					return false;
				}
			}
		}
		// �Ƿ���Ҫ���������  
		if (!checkNewModifRowCount() && !isSaveGM()) {
			// û��Ҫ��������ݣ�
			this.messageBox("E0164");
			return false;
		}
		// ������¼����    
		if (tab.getSelectedIndex() == 4 ) {
			boolean falg = false;
//			//machao start 
//			TDataStore data = this.getTTable(GMTABLE).getDataStore();
//			Vector vector = data.getVector();
//			if(vector.size()>1){
//				Set<String> set = new HashSet<String>();
//				Vector v = new Vector();
//				String typeAgergy = "";
//				if("Y".equals(this.getValueString("PHA_DRUGALLERGY"))){
//					typeAgergy = this.getValueString("PHA_CLASS");
//				}else if("Y".equals(this.getValueString("CF_DRUGALLERGY"))){
//					typeAgergy = "A";
//				}else if("Y".equals(this.getValueString("OTHER_DRUGALLERGY"))){
//					typeAgergy = "C";
//				}else if("Y".equals(this.getValueString("NONE_DRUGALLERGY"))){
//					typeAgergy = "N";
//				}
//
//				for(int i = 0;i<vector.size()-1;i++){
//					v = (Vector) vector.get(i);
//					set.add((String) v.get(2));
//				}
//				//List list = new ArrayList(set);
//				List<String> listIsExist = new ArrayList();
//				for(String str :set){
//					String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND MR_NO = '"+this.getMrNo()+"' "
//							+ "AND DRUG_TYPE='"+typeAgergy+"' "
//							+ "AND DRUGORINGRD_CODE='"+str+"' "
//							+ "AND CASE_NO <>'"+this.getCaseNo()+"' ";
//					System.out.println("1111111111"+sqlAllergy);
//					TParm p = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
//					
//					if(p.getCount()>0){
//						listIsExist.add(str);
//					}
//				}
//				TParm parm = new TParm();
//				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
//				for(String str:listIsExist){
//					parm.addData("DRUG_TYPE", typeAgergy);
//					parm.addData("DRUGORINGRD_CODE", str);				
//					parm.addData("MR_NO", this.getMrNo());
//					parm.addData("ADM_DATE",df.format(SystemTool.getInstance().getDate()));
//									
//					parm.addData("ADM_TYPE", "I");
//					parm.addData("CASE_NO", this.getCaseNo());
//					parm.addData("DEPT_CODE", this.getDeptCode());
//					parm.addData("DR_CODE",Operator.getID());
//					parm.addData("ALLERGY_NOTE", "");
//					parm.addData("OPT_USER", Operator.getID());
//					parm.addData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,19));
//					parm.addData("OPT_TERM", Operator.getIP());
//				}
//				//this.messageBox(""+parm);
//				TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
//						"insertOpddrugallergy", parm);			
//				
////				TTable table = this.getTTable(GMTABLE);
////				Vector vector = table.getDataStore().getVector();
////				System.out.println("0000000:"+vector);
////				if(vector.size()>=2){
////					Set<String> set = new HashSet<String>();
////					SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
////					Vector v = new Vector();
////					for(int i = 0;i<vector.size()-1;i++){
////						v = (Vector) vector.get(i);
////						set.add((String) v.get(2));
////					}
////					TParm parm = new TParm();
////					v = (Vector) vector.get(0);
////					for(String str : set){
////						parm.addData("DRUG_TYPE", v.get(1));
////						parm.addData("DRUGORINGRD_CODE", str);				
////						parm.addData("MR_NO", this.getMrNo());
////						parm.addData("ADM_DATE",df.format(SystemTool.getInstance().getDate()));
////										
////						parm.addData("ADM_TYPE", "I");
////						parm.addData("CASE_NO", this.getCaseNo());
////						parm.addData("DEPT_CODE", this.getDeptCode());
////						parm.addData("DR_CODE",Operator.getID());
////						parm.addData("ALLERGY_NOTE", "");
////						parm.addData("OPT_USER", Operator.getID());
////						parm.addData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,19));
////						parm.addData("OPT_TERM", Operator.getIP());
////					}
////					
////					TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
////							"insertOpddrugallergy", parm);
////					this.messageBox(""+parm);
////				}
//			}
//			//machao end
			falg = this.getTTable(GMTABLE).getDataStore().update();
			if (!falg) {
				this.messageBox("E0001");
				return falg;
			}
			this.messageBox("P0001");
			// TABLE����
			initDataStoreToTable();
			this.clearFlg = "N";
			onDiagPnChange(false);// ����ʷ��ǩ�ı���ɫ

			// add by wangb 2015/11/06 ����ADM_INP��Ĺ����ֶ� START
			String sql = "SELECT * FROM OPD_DRUGALLERGY WHERE MR_NO = '"
					+ this.getMrNo() + "' AND DRUG_TYPE != 'N'";
			TParm allergyParm = new TParm(TJDODBTool.getInstance().select(sql));
			String allergy = "";
			if (allergyParm.getCount() > 0) {// modify by guoy 20151116
				allergy = "Y";
			} else {
				allergy = "N";
			}
			sql = "UPDATE ADM_INP SET ALLERGY='" + allergy
					+ "' WHERE CASE_NO = '" + this.getCaseNo() + "'";
			TJDODBTool.getInstance().update(sql);
			// add by wangb 2015/11/06 ����ADM_INP��Ĺ����ֶ� END
			return falg;
		}
		// Ժǰ��ҩ��¼����
		if (tab.getSelectedIndex() == 5) {
			boolean flag = false;
			TParm checkParm = this.checkPADataSave();

			if (checkParm.getErrCode() < 0) {
				this.messageBox(checkParm.getErrText());
				TTable table = this.getTTable(TABLE6);
				table.getTable().grabFocus();
				table.setSelectedRow(checkParm.getInt("ROW"));
				table.setSelectedColumn(checkParm.getInt("COL"));
				return flag;
			}

			flag = this.savePAData();
			if (!flag) {
				this.messageBox("E0001");
				return flag;
			}
			this.messageBox("P0001");
			// TABLE����
			initDataStoreToTable();
			this.clearFlg = "N";
			return flag;
		}
		// ds.showDebug();
		// ҽ�����
		TParm parm = checkOrderSave();
		// System.out.println("======ҩƷ��ʾ====="+parm.getErrText());
		// fux modify start
		// $$=========add by lx 2012-07-04 ���ҩƷ��ʾSTART=============$$//
		/*
		 * if (parm.getErrText().indexOf("��治��") != -1) { String orderCode =
		 * parm.getErrText().split(";")[1]; TParm inParm = new TParm();
		 * 
		 * inParm.setData("orderCode", orderCode);
		 * this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
		 * return false; }
		 */
		// $$=========add by lx 2012-07-04 END=============$$//
		this.antflg = true;
		// fux modify end
		// ҽ������δͨ��
		if (parm.getErrCode() == -1) {
			if (!parm.getErrText().equals("")) {
				this.messageBox(parm.getErrText());
			}
			return false;
		}
		if (parm.getErrCode() < 0 && parm.getErrCode() != -6
				&& parm.getErrCode() != -9) {
			// ������ҩ
			// System.out.println("7777777777777777");
			if (parm.getErrCode() == -102) {
				// TABLE����
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// ����TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				return false;
			}
			// ҽ��ܿ�
			if (parm.getErrCode() == -101) {
				String flg = parm.getValue("FORCE_FLG", 0);
				if (flg.equals("Y")) {
					this.messageBox(parm.getErrText());
				}
				int row = parm.getInt("ROW", 0);
				this.DelOrder(row);
				// TABLE����
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// ����TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				return false;
			}
			// ������
			// this.messageBox_("111������"+parm);
			if (tab.getSelectedIndex() == 3) {
				this.messageBox(parm.getErrText());
				// ֹͣ����
				this.stopColor();
				this.chagePage = true;
				return false;
			}
			errSaveOrder(parm);
			this.messageBox(parm.getErrText());
			// ֹͣ����
			this.stopColor();
			this.chagePage = true;
			return false;
		}

		// �޸Ĺ���ҽ������
		if (parm.getErrCode() == -6 || parm.getErrCode() == -9) {
			// ������
			errSaveOrder(parm);
			if (this.messageBox("��ʾ��Ϣ", parm.getErrText(), this.YES_NO_OPTION) != 0) {
				// TABLE����
				initDataStoreToTable();
				if (tab.getSelectedIndex() == 2)
					this.onQuery();
				else {
					// ����TABLE
					if ("N".equals(this.clearFlg))
						this.onChange();
					if ("Y".equals(this.clearFlg))
						this.delRowNull();
					if ("Q".equals(this.clearFlg))
						this.onQuery();
				}
				this.chagePage = true;
				return false;
			}
		}
		// �ж����뵥
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// �õ����뵥�����
		TParm emrParm = OrderUtil.getInstance()
				.getOrderPasEMR(orderData, "ODI");
		// ====pangben 2013-7-30 ���ODI_PHA ���������뵥
		TParm phaEmrParm = OrderUtil.getInstance().getOrderPasEMR(orderData,
				"ODI_PHA");
		String msg = ""; // add by wanglong 20121210
		if (!(msg = checkAntiBioticWayisFilled(odiObject)).equals("")) { // add
			// System.out.println("phaEmrParm:::::" + phaEmrParm);
			System.out.println("COUNT:::"
					+ phaEmrParm.getInt("ACTION", "COUNT"));
			if (null != phaEmrParm && phaEmrParm.getInt("ACTION", "COUNT") > 0) {
				System.out.println("phaEmrParm:::" + phaEmrParm);
				this.messageBox("���濹��ҩƷ�������⣬����ϵ��Ϣ����");
			}
			// by
			// wanglong
			// 20121210
			// actionParm.setData("ANTIBIOTIC_WAY",parm.getValue("ANTI_FLG"));
			// this.messageBox("<html><font color=\"red\">" + msg
			// + "</font> ���ڿ���ҩƷ��</html>\n����û��д������ʶ�������ᱻ����");
		}
		// ��ǰ��������ݴ��ڿ���ҩƷ===pangben 2013-9-10
		if (null != phaEmrParm && phaEmrParm.getInt("ACTION", "COUNT") > 0) {
			// ֱ�ӱ�������л�ҳǩ������
			if (tabSaveFlg) {
				this.messageBox("���п���ҩƷ,�����л�ҳǩ��");
				return false;
			}
			System.out.println("phaApproveFlg:::::" + phaApproveFlg);
			if (phaApproveFlg.equals("Y")) {// ���õ����ﵥ,�Ѿ�����ҽ��
			} else {
				// ���ҽ���Ƿ��п���ҩ���֤������TParm
				TParm newPhaEmrParm = phaEmrParm;
				if (tab.getSelectedIndex() == 1) {// ����ҽ��ʱ
					newPhaEmrParm = reCheckAnti(phaEmrParm);
				}
				if (newPhaEmrParm.getErrCode() < 0) {
					this.messageBox(newPhaEmrParm.getErrText());
					return false;
				}
				System.out.println("newPhaEmrParm������" + newPhaEmrParm);
				System.out.println("ORDER_CODE:COUNT:::"
						+ newPhaEmrParm.getCount("ORDER_CODE"));
				if (newPhaEmrParm.getCount("ORDER_CODE") <= 0) {
					return false;
				}
				// phaEmrParm.setData("TYPE_CHANGE",typeChange);/
				TParm result = OrderUtil.getInstance().getPhaAntiCreate(
						newPhaEmrParm, caseNo, mrNo, tab.getSelectedIndex(),
						this);
				if (result.getErrCode() < 0 && result.getErrCode() != -9) {
					this.messageBox(result.getErrText());
					if (null != result.getValue("MESSAGE")
							&& result.getValue("MESSAGE").equals("Y")) {
						// onInpDepApp();
						onConsApply();
						initDataStoreToTable();
					}
					return false;
				} else if (result.getErrCode() == -9) {
					if (this.messageBox("��ʾ", result.getErrText(), 2) != 0) {
						return false;
					}
				}
			}
			String sql = "SELECT CASE_NO FROM PHA_ANTI WHERE CASE_NO='"
					+ caseNo + "'";
			TParm phaAntiParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (phaAntiParm.getCount() <= 0) {
				this.messageBox("���濹��ҩƷ�������⣬����ϵ��Ϣ����");
			}
			phaApproveFlg = "";
		}
		TDS newDs = odiObject.getDS("ODI_ORDER");
		ds.setFilter("");
		// ds.setSort("ORDER_NO,ORDER_SEQ");
		ds.filter();
		// סԺ��ҩ���ֵ
		if (tab.getSelectedIndex() == 3) {
			onChangeIGOrder();
		}

		// ����ҽ��ʱУ���Ƿ�Խ������ yanjing
		if (tab.getSelectedIndex() == 1) {
			isOverRideCheck(odiObject);
		}
		if (newCount > 0) {// wanglong add 20150123
			String sql = "SELECT A.DS_DATE, B.MR_NO FROM ADM_INP A, SYS_BED B "
					+ " WHERE A.BED_NO = B.BED_NO(+)  AND A.CASE_NO = B.CASE_NO(+) "
					+ "   AND A.MR_NO = B.MR_NO(+) AND B.ACTIVE_FLG = 'Y' "
					+ "   AND A.CASE_NO = '#'";
			sql = sql.replaceFirst("#", this.getCaseNo());
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
			if (result.getCount() < 1) {
				this.messageBox("�ò������ڴ������ܿ���ҽ��������ʿվѯ�ʲ���״̬");
				return false;
			}
			if (result.getTimestamp("DS_DATE", 0) != null) {
				this.messageBox("�ò����ѳ�Ժ�����ܿ���ҽ��������ʿվѯ�ʲ���״̬");
				return false;
			}
		}
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			odiObject.inparam = (TParm) this.getParameter();
		}
		// ����
		if (!odiObject.update()) {
			if (!odiObject.update()) {
				this.messageBox("E0001");
				return false;
			}
		}

		// ===================���δ���ҽ��ϸ��ORDERSET_GROUP_NO�����ͬ��BUG���ڱ���ǰ��Ⲣ��ʾ
		TDS ds1 = odiObject.getDS("ODI_ORDER");
		String buff1 = ds1.isFilter() ? ds1.FILTER : ds1.PRIMARY;
		TParm vParm1 = ds1.getBuffer(buff1);// wanglong add 20140523
		TParm updateParm = new TParm();
		for (int i = 0; i < vParm1.getCount(); i++) {

			if (vParm1.getBoolean("#NEW#", i)) {
				if (vParm1.getValue("ORDER_CODE", i).toString().length() > 0) {
					updateParm.addData("ORDER_NO", vParm1.getValue("ORDER_NO",
							i));
					updateParm.addData("ORDER_SEQ", vParm1.getValue(
							"ORDER_SEQ", i));
					updateParm.addData("ORDER_CODE", vParm1.getValue(
							"ORDER_CODE", i));
					updateParm.addData("MEDI_QTY", vParm1.getValue("MEDI_QTY",
							i));
					updateParm.addData("MEDI_UNIT", vParm1.getValue(
							"MEDI_UNIT", i));
					updateParm.addData("FREQ_CODE", vParm1.getValue(
							"FREQ_CODE", i));
					updateParm.addData("EFF_DATE", vParm1.getTimestamp(
							"EFF_DATE", i));
					updateParm.addData("CASE_NO", this.getCaseNo());
				}

			}

			if (vParm1.getBoolean("#NEW#", i)
					&& !vParm1.getValue("ORDERSET_CODE", i).equals("")
					&& vParm1.getValue("SETMAIN_FLG", i).equals("N")) {
				int j = 0;
				for (; j < vParm1.getCount(); j++) {
					if (j == i) {
						continue;
					}
					if (vParm1.getBoolean("#NEW#", j)
							&& !vParm1.getValue("ORDERSET_CODE", j).equals("")
							&& vParm1.getValue("SETMAIN_FLG", j).equals("Y")
							&& vParm1.getValue("ORDERSET_CODE", j).equals(
									vParm1.getValue("ORDERSET_CODE", i))
							&& vParm1.getValue("ORDERSET_GROUP_NO", j).equals(
									vParm1.getValue("ORDERSET_GROUP_NO", i))) {
						break;
					}
				}
				if (j == vParm1.getCount()) {
					System.out.println("-----------Debug1 begin---------");
					ds1.showDebug();
					System.out.println("-----------Debug1 end-----------");
					this.messageBox("+1����ҽ����ϸ����ڲ�ͬ��GROUP_NO����������BUG������ϵ����");
				}
			}
		}

		odiStationDrools.updateCkbLog(updateParm); // add by huangtt 20150819

		this.messageBox("P0001");
		// ��Ժ��ҩ
		if (tab.getSelectedIndex() == 2) {
			onPrintOrder();
		}
		// ��ҩ
		if (tab.getSelectedIndex() == 3) {
			onPrintChnOrder();
		}
		// ����������ʱ���Ӧ�Ļ�ʿվ������Ϣ
		if (newCount > 0) {
			// this.sendHl7Messages();
			this.sendInwStationMessages();
		}
		// ���ó�Ժ��ҩ����ǩ
		initDSRxNoCombo();
		// ������ҩ��Ƭ����ǩ
		initIGRxNoCombo();
		// TABLE����
		initDataStoreToTable();
		// ���ó�Ժ��ҩ����ǩĬ��ѡ��
		this.getTComboBox("RX_NO").setSelectedID("");
		this.onRxNoSel();
		// ������ҩ��Ƭ����ǩĬ��ѡ��
		this.getTComboBox("IG_RX_NO").setSelectedID("");
		this.onRxNoSelIG();
		this.clearFlg = "N";
		this.setValue("MEDI_QTYALL", 0.0);
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			// �������뵥
			onApplyList();
		}
		if (clpDsFlg) {// pangben 2015-8-14 ��Ժ����ʱ��1.�ٴ�·��У�������Ƿ��� 2.�������У��
			TParm clpParm = new TParm();
			clpParm.setData("CASE_NO", caseNo);
			clpParm.setData("MR_NO", mrNo);
			CLPTool.getInstance().onCheckOutAdmOpeBook(clpParm, this);
			CLPTool.getInstance().onCheckOutAmtIbs(clpParm, this);
		}
		// סԺҽ��վУ���һ�ν���·������
		TParm result = CLPTool.getInstance().checkOdiClpExe(
				getClpOdiStationParm(), this);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return false;
		}
		// ============pangben 2013-7-30
		// if ((msg =
		// checkAntiBioticWayisFilled(odiObject)).equals("")&&phaEmrParm.getInt("ACTION",
		// "COUNT") > 0) {
		// // ���ÿ��������뵥
		// onApplyListPha();
		// onBoardMessage(phaEmrParm);
		// }
		// 20151026 wangjc start 崻�����
		class BackThread extends Thread {
			TControl tc;

			public BackThread(TControl control) {
				tc = control;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ����CASE_NO
				OPDBackupDataToPDFControl opdBackupDataToPDFControl = new OPDBackupDataToPDFControl(
						caseNo);
				opdBackupDataToPDFControl.backupDataToPDF(caseNo, tc);
			}

		}
		BackThread backThread = new BackThread(this);
		backThread.start();
		// 20151026 wangjc end 崻�����
		if (((TParm) this.getParameter()).getData("ODI", "OPERATOR") != null
				&& ((TParm) this.getParameter()).getData("ODI", "OPERATOR")
				.equals("OPE_N")) {
			try {
				onFee(newDs);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		// 20151106 wangjc add ͣ���÷�ҽ���Ʒ�
		try {
			if (opeFlg) {
				cardioplegiaFee(newDs);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.messageBox("ͣ���÷�ҽ���Ʒ�ʧ��!");
		}
		return true;
	}

	/**
	 * ����ҽ���Ʒ�
	 */
	public void onFee(TDS ds) {
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);
		TParm result = new TParm();
		int count = 0;
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			TParm tempParm = ds.getRowParm(i, buff);// =====pangben 2015-8-14
			result.setRowData(count, tempParm);
			count++;
		}
		result.setCount(count);
		onCheckOpOrder(result);
		insertInterPlatData(result);
	}

	/**
	 * ������뻤��ƽ̨����
	 */
	public void insertInterPlatData(TParm parm) {
		// String date =
		// TJDODBTool.getInstance().getDBTime().toString().substring(0,19).replaceAll("-",
		// "/");
		TParm result = new TParm();
		int seqNo = OPEDeptOpTool.getInstance().getMaxSeqNo(this.getCaseNo());
		int count = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getValue("HIDE_FLG", i).equals("N")) {
				System.out
				.println("ORDER_SEQ:" + parm.getValue("ORDER_SEQ", i));
				System.out.println("ORDER_NO:" + parm.getValue("ORDER_NO", i));
				result.setData("SAVE_FLG", i, "Y");
				result.setData("CASE_NO", i, this.getCaseNo());
				result.setData("SEQ_NO", i, ++seqNo);
				result.setData("TIME", i, parm.getValue("NS_CHECK_DATE", i)
						.toString().substring(0, 19).replaceAll("-", "/"));
				result.setData("ORDER_DESC", i, parm.getValue("ORDER_DESC", i));
				result.setData("HEART_RATE", i, "");
				result.setData("BREATH", i, "");
				result.setData("PRESSURE", i, "");
				result.setData("OXYGEN_SATURATION", i, "");
				result.setData("PAIN_ASSESSMENT", i, "");
				result.setData("ILLNESS_RECORD", i, "");
				result.setData("OP_DEPT_CODE", i, this.getDeptCode());
				result.setData("STATION_CODE", i, this.getStationCode());
				result.setData("ORDER_CODE", i, parm.getValue("ORDER_CODE", i));
				result.setData("MEDI_QTY", i, parm.getDouble("MEDI_QTY", i));
				result.setData("MEDI_UNIT", i, parm.getDouble("MEDI_UNIT", i));
				result.setData("ROUTE_CODE", i, parm.getValue("ROUTE_CODE", i));
				result.setData("OPT_USER", i, Operator.getID());
				result.setData("OPT_TERM", i, Operator.getIP());
				result.setData("OPBOOK_SEQ", i, this.getOpBookSeq());
				result.setData("ORDER_NO", i, parm.getValue("ORDER_NO", i));
				result.setData("ORDER_SEQ", i, parm.getInt("ORDER_SEQ", i));
				count++;
			}
		}
		result.setCount(count);
		OPEDeptOpTool.getInstance().insertInterData(result);
	}

	/**
	 *����ʱ,�������ҽ��
	 */
	public void onCheckOpOrder(TParm parm) {
		TParm data = new TParm();
		String checkDate = TJDODBTool.getInstance().getDBTime().toString();
		Timestamp checkDateTime = StringTool.getTimestamp(checkDate.substring(
				0, 19).replaceAll("-", "").replaceAll(":", ""),
				"yyyyMMdd HHmmss");
		int count = 0;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {

			data.addData("CASE_NO", this.getCaseNo());
			data.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			data.addData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
			data.addData("SETMAIN_FLG", parm.getValue("SETMAIN_FLG", i));
			data.addData("ORDERSETGROUP_NO", parm.getValue("ORDERSET_GROUP_NO",
					i));
			data.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			data.addData("ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
			data.addData("CHECK_DATETIME", checkDateTime);
			data.addData("TAKEMED_ORG", parm.getValue("TAKEMED_ORG", i));
			count++;
		}
		data.setCount(count);
		TParm toData = new TParm();
		toData.setData("OPT_USER", Operator.getID());
		toData.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		toData.setData("OPT_TERM", Operator.getIP());
		toData.setData("dataParm", data.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.InwOrderCheckAction", "onCheckOP", toData);
	}

	// /**
	// * ���ﵥ
	// * =======pangben 2013-9-10
	// */
	// public void onInpDepApp() {
	// TParm parm = new TParm();
	// parm.setData("MR_NO", mrNo);
	// parm.setData("CASE_NO", caseNo);
	// parm.setData("ADM_DATE",getAdmDate());
	// this.openDialog("%ROOT%\\config\\inp\\INPDeptApp.x", parm);
	// }
	private TParm getClpOdiStationParm() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("CTZ_CODE", this.getCtzCode());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		return parm;
	}

	/**
	 * �õ���ӡ����
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmAmt(String type, String rxNo) {
		TParm result = new TParm(this.getDBTool().select(
				" SELECT SUM(TOT_AMT) AMT" + " FROM ODI_ORDER A,IBS_ORDD B"
						+ " WHERE A.CASE_NO='" + this.getCaseNo() + "'"
						+ " AND A.RX_KIND='" + type + "'" + " AND A.RX_NO='"
						+ rxNo + "'" + " AND A.CASE_NO = B.CASE_NO"
						+ " AND A.ORDER_NO = B.ORDER_NO"
						+ " AND A.ORDER_SEQ = B.ORDER_SEQ"));
		return result;
	}

	/**
	 * ��ӡ��ҩ����ǩ
	 */
	public void onPrintChnOrder() {
		String rxNo = this.getTComboBox("IG_RX_NO").getSelectedID();
		if (rxNo.length() <= 0) {
			// ��ѡ�񴦷�ǩ
			this.messageBox("E0029");
			return;
		}
		TParm parm = this.getOrderParm("IG", rxNo);
		if (parm.getCount() <= 0) {
			// �޴�ӡ���ݣ�
			this.messageBox("E0010");
			return;
		}
		TParm orderParm = new TParm();
		orderParm.setData("RX_TYPE", "DS");
		orderParm.setData("CASE_NO", this.getCaseNo());
		orderParm.setData("RX_NO", rxNo);
		orderParm.setData("ADDRESS", "TEXT", "ODIStationControl LPT1");
		orderParm.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		if ("en".equals(this.getLanguage())) {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalENGFullName(Operator.getRegion()));
		} else {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()));
		}
		orderParm.setData("ORDER_TYPE", "TEXT", "סԺ��ҽ");
		orderParm.setData("ORG_CODE", "TEXT", "ҩ��:"
				+ ((TextFormatINDOrg) this.getComponent("DEPT_CODEIG"))
				.getText());
		orderParm.setData("PAY_TYPE", "TEXT", "�ѱ�"
				+ OdiOrderTool.getInstance().getCTZDesc(this.getCtzCode()));
		orderParm.setData("PAT_NAME", "TEXT", "������" + this.getPatName());
		orderParm.setData("SEX_CODE", "TEXT", "�Ա�"
				+ OdiUtil.getInstance().getDictionary("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("AGE", "TEXT", "���䣺"
				+ OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
		orderParm.setData("MR_NO", "TEXT", "�����ţ�" + this.getMrNo());
		orderParm.setData("DEPT_CODE", "TEXT", "���ң�"
				+ OdiUtil.getInstance().getDeptDesc(this.getDeptCode()));
		orderParm.setData("CLINIC_ROOM", "TEXT", "������"
				+ OdiUtil.getInstance().getStationDesc(this.getStationCode()));
		orderParm.setData("DR_CODE", "TEXT", "ҽ����" + Operator.getName());
		orderParm.setData("ADM_DATE", "TEXT", "ʱ�䣺"
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("BAR_CODE", "TEXT", this.getMrNo());
		// orderParm.setData("TAKE_DAYS","TEXT","����:"+this.getValueInt("RF"));
		orderParm.setData("TAKE_DAYS", "TEXT", "����:"
				+ parm.getData("TAKE_DAYS", 0));
		orderParm.setData("FREQ_CODE", "TEXT", "��Σ�"
				+ ((TextFormatSYSPhaFreq) this.getComponent("IGFREQCODE"))
				.getText());
		orderParm.setData("ROUTE_CODE", "TEXT", "������"
				+ ((TextFormatSYSPhaRoute) this.getComponent("IG_ROUTE"))
				.getText());
		orderParm.setData("DCT_TAKE_QTY", "TEXT", "ÿ�η�������"
				+ this.getValueString("YPJL") + "��");
		// int rfNum = this.getValueInt("RF");
		// String freqCode = this.getValueString("IGFREQCODE");
		// int rfNumCount =
		// OdiUtil.getInstance().getPACKAGE_AMT(rfNum,freqCode);
		// orderParm.setData("PACKAGE_TOT","TEXT","ÿ���ܿ�����"+rfNumCount);
		orderParm.setData("PACKAGE_TOT", "TEXT", "ÿ���ܿ�����"
				+ this.getValueString("MEDI_QTYALL") + "��");
		int rowCount = parm.getCount();
		int orderRowCount = 1;
		int orderColumns = 1;
		for (int i = 0; i < rowCount; i++) {
			orderParm.setData("ORDER_DESC" + orderRowCount + orderColumns,
					"TEXT", parm.getData("ORDER_DESC", i));
			orderParm.setData("MEDI_QTY" + orderRowCount + orderColumns,
					"TEXT", parm.getData("MEDI_QTY", i));
			orderParm.setData("DCTAGENT" + orderRowCount + orderColumns,
					"TEXT", OdiUtil.getInstance().getDictionary("PHA_DCTEXCEP",
							parm.getValue("DCTEXCEP_CODE", i)));
			if (orderColumns == 4)
				orderColumns = 1;
			if ((i + 1) % 4 == 0)
				orderRowCount++;
			else
				orderColumns++;
		}
		TParm parmAmt = this.getOrderParmAmt("IG", rxNo);
		if (parmAmt.getCount() == 0 || parmAmt.getData("AMT", 0) == null
				|| parmAmt.getValue("AMT", 0).equalsIgnoreCase("null")
				|| parmAmt.getDouble("AMT", 0) == 0)
			orderParm.setData("AMT", "TEXT", " ");
		else
			orderParm.setData("AMT", "TEXT", "���:"
					+ parmAmt.getDouble("AMT", 0) + "Ԫ");
		this.openPrintDialog(
				"%ROOT%\\config\\prt\\ODI\\OdiChnOrderSheetNew.jhw", orderParm,
				false);
	}

	/**
	 * ��ӡ��Ժ��ҩ����ǩ =============shibaoliu 20110718 �޸Ľ�Ӣ��ȥ��
	 */
	public void onPrintOrder() {
		String rxNo = this.getTComboBox("RX_NO").getSelectedID();
		if (rxNo.length() <= 0) {
			this.messageBox("E0029");
			return;
		}
		TParm parm = this.getOrderParm("DS", rxNo);
		if (parm.getCount() <= 0) {
			this.messageBox("E0010");
			return;
		}
		TParm orderParm = new TParm();
		orderParm.setData("RX_TYPE", "DS");
		orderParm.setData("CASE_NO", this.getCaseNo());
		orderParm.setData("RX_NO", rxNo);
		orderParm.setData("ADDRESS", "TEXT", "ODIStationControl From "
				+ Operator.getID() + " To LPT1");
		orderParm.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("HOSP_NAME", "TEXT", OpdRxSheetTool.getInstance()
				.getHospFullName());
		orderParm.setData("HOSP_NAME_ENG", "TEXT", Operator
				.getHospitalENGShortName());
		// if("en".equals(this.getLanguage())){
		// orderParm.setData("HOSP_NAME","TEXT",Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
		// }else{
		// orderParm.setData("HOSP_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
		// }
		orderParm.setData("ORDER_TYPE", "TEXT", "(ס)��Ժ��ҩ");
		orderParm.setData("ORG_CODE", "TEXT", "ҩ��:"
				+ ((TComboOrgCode) this.getComponent("DEPT_CODEDS"))
				.getSelectedName());
		orderParm.setData("PAY_TYPE", "TEXT", "�ѱ�:"
				+ OdiOrderTool.getInstance().getCTZDesc(this.getCtzCode()));
		orderParm.setData("PAY_TYPE_ENG", "TEXT", "Cate:"
				+ OpdRxSheetTool.getInstance().getPayTypeEngName(
						this.getCtzCode()));
		orderParm.setData("MR_NO", "TEXT", "������:" + this.getMrNo());
		orderParm.setData("MR_NO_ENG", "TEXT", "Pat ID:" + this.getMrNo());
		orderParm.setData("PAT_NAME", "TEXT", "����:" + this.getPatName());
		orderParm.setData("PAT_NAME_ENG", "TEXT", "Name:"
				+ OpdRxSheetTool.getInstance().getPatEngName(this.getMrNo()));
		orderParm.setData("PAT_ID", "TEXT", "�������֤�ţ�"
				+ OpdRxSheetTool.getInstance().getId(this.getMrNo()));
		orderParm.setData("SEX_CODE", "TEXT", "�Ա�:"
				+ OdiUtil.getInstance().getDictionary("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("SEX_CODE_ENG", "TEXT", "Gender:"
				+ DictionaryTool.getInstance().getEnName("SYS_SEX",
						this.getSexCode()));
		orderParm.setData("BIRTHDAY", "TEXT", "��������:"
				+ StringTool.getString(this.getBirthDay(), "yyyy/MM/dd"));
		orderParm.setData("BBB", "TEXT", "Birthday:"
				+ StringTool.getString(this.getBirthDay(), "yyyy/MM/dd"));
		orderParm.setData("AGE", "TEXT", "����:"
				+ OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
		orderParm.setData("AGE_ENG", "TEXT", "Age:"
				+ OdoUtil.showEngAge(this.getBirthDay(), this.getAdmDate()));
		orderParm.setData("DEPT_CODE", "TEXT", "����:"
				+ OdiUtil.getInstance().getDeptDesc(this.getDeptCode()));
		orderParm.setData("DEPT_CODE_ENG", "TEXT", "Dept:"
				+ OpdRxSheetTool.getInstance().getDeptEngName(
						this.getDeptCode()));
		orderParm.setData("CLINIC_ROOM", "TEXT", "����:"
				+ OdiUtil.getInstance().getStationDesc(this.getStationCode()));
		orderParm.setData("CLINIC_ROOM_ENG", "TEXT", "Station:"
				+ OdiUtil.getInstance()
				.getStationDescEnd(this.getStationCode()));
		orderParm.setData("DR_CODE", "TEXT", "ҽ��:" + Operator.getName());
		orderParm.setData("DR_CODE_ENG", "TEXT", "M.D.:" + Operator.getName());
		orderParm.setData("ADM_DATE", "TEXT", "��Ժʱ��:"
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("ADM_DATE_ENG", "TEXT", "Date:"
				+ StringTool
				.getString(this.getAdmDate(), "yyyy/MM/dd HH:mm:ss"));
		orderParm.setData("FOOT_DR", "TEXT", "ҽʦ:" + Operator.getName());
		orderParm.setData("FOOT_DR_CODE", "TEXT", "ҽʦ����:" + Operator.getID());
		orderParm.setData("BAR_CODE", "TEXT", this.getMrNo());
		orderParm.setData("DIAG", "TEXT", "��ϣ�"
				+ OdiOrderTool.getInstance().getICDCode(this.getCaseNo()));
		orderParm.setData("DIAG_ENG", "TEXT", "Diagnosis:"
				+ OdiOrderTool.getInstance().getICDCodeEng(this.getCaseNo()));
		this.openPrintDialog("%ROOT%\\config\\prt\\ODI\\OdiOrderSheetNew.jhw",
				orderParm, false);
	}

	/**
	 * �õ���ӡ����
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParm(String type, String rxNo) {
		TParm result = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_ORDER WHERE CASE_NO='" + this.getCaseNo()
				+ "' AND RX_KIND='" + type + "' AND RX_NO='" + rxNo
				+ "' ORDER BY ORDER_NO,ORDER_SEQ"));
		return result;
	}

	/**
	 * ���ҽ��
	 */
	public TParm checkOrderSave() {
		TParm result = new TParm();
		// �Ƿ񾭻�ʿ���
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		// �õ�סԺ��ҩʱ��
		Object obj = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// �¼ӵ�����
		int newRow[] = ds.getNewRows(buff);
		// �޸ĵ�����
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		// ������ҩ
		if (!checkDrugAuto()) {
			result.setErrCode(-102);
			return result;
		}

		Map index = new HashMap();
		index.put("ST", 0);
		index.put("UD", 1);
		index.put("DS", 2);
		index.put("IG", 3);
		index.put("F", 1);

		// ������¼TABLE
		TTable tab = this.getTTable(GMTABLE);
		// ������¼������
		String buffGM = tab.getDataStore().isFilter() ? tab.getDataStore().FILTER
				: tab.getDataStore().PRIMARY;
		int gmRowCount = tab.getDataStore().isFilter() ? tab.getDataStore()
				.rowCountFilter() : tab.getDataStore().rowCount();
				boolean insFlg = true;
				for (int i : newRow) {
					if (!ds.isActive(i, buff))
						continue;
					String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
					String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
					// $$====================add by lx 2012-02-13 �� ��ҽ��ҽ�����
					// START============================$$//
					if (!insOrderCheck(order_code, this.getCtzCode(), order_desc)) {
						result.setErrCode(-1);
						return result;
					}
					// $$====================add by lx 2012-02-13 ��
					// ��ҽ��ҽ�����END============================$$//
					// $$====================add by lx 2012-02-14 �� ��ҽ��ҽ�����
					// START============================$$//
					/**
					 * if (ds.getRowParm(i, buff).getValue("ORDER_DESC").equals("")) {
					 * result.setErrCode(-1); result.setErrText(ds.getRowParm(i,
					 * buff).getValue( "ORDER_DESC") + "ҽ����������Ϊ��!");
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result;
					 * 
					 * }
					 **/

					/**
					 * if (ds.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0) {
					 * result.setErrCode(-1); result.setErrText(ds.getRowParm(i,
					 * buff).getValue( "ORDER_DESC") + "��������Ϊ:0 \n Dosage for:0");
					 * 
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result;
					 * 
					 * }
					 **/
					// $$====================add by lx 2012-02-14 �� ��ҽ��ҽ�����
					// START============================$$//

					// ����ҽ���
					if (getctrflg(order_code).equals("Y")) {
						TParm crtParm = new TParm();
						crtParm.setData("ADM_TYPE", "I");
						crtParm.setData("CTZ_CODE", this.getCtzCode());
						crtParm.setData("ORDER_CODE", ds.getRowParm(i, buff).getValue(
								"ORDER_CODE"));
						crtParm.setData("CASE_NO", ds.getRowParm(i, buff).getValue(
								"CASE_NO"));
						crtParm.setData("MR_NO", ds.getRowParm(i, buff).getValue(
								"MR_NO"));
						crtParm.setData("Parm", ds.getRowParm(i, buff).getData());
						if (CTRPanelTool.getInstance().selCTRPanel(crtParm)
								.getErrCode() == 100) {
							// shibl add 20121101 ��ӵ��Ӳ�����
							String subClass = CTRPanelTool.getInstance().selCTRPanel(
									crtParm).getValue("SUBCLASS_CODE");
							if (!subClass.equals("")) {
								Object o = (Object) this.openDialog(
										"%ROOT%\\config\\emr\\EMRUIcplNG.x", subClass);
							}
							if (!CTRPanelTool.getInstance().selCTRPanel(crtParm)
									.getValue("FORCE_FLG").equals("Y")) {
								if (this.messageBox("��ʾ��Ϣ/Tip",
										""
												+ CTRPanelTool.getInstance()
												.selCTRPanel(crtParm).getValue(
														"MESSAGE") + ",��������?",
												0) != 0) {
									result.setErrCode(-101);
									result.setErrText(CTRPanelTool.getInstance()
											.selCTRPanel(crtParm).getValue("MESSAGE"));
									result.setData("ERR", "INDEX", index.get(ds
											.getRowParm(i, buff).getValue("RX_KIND")));
									result.setData("ERR", "ORDER_CODE", ds.getRowParm(
											i, buff).getValue("ORDER_CODE"));
									result.addData("ROW", i);
									return result;
								}
							} else {
								result.setErrCode(-101);
								result.setErrText(CTRPanelTool.getInstance()
										.selCTRPanel(crtParm).getValue("MESSAGE"));
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.addData("ROW", i);
								result.addData("FORCE_FLG", "Y");
								return result;
							}
						}
					}
					// ����
					if (ds.getRowParm(i, buff).getDouble("MEDI_QTY") == 0) {
						// this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY")+"==="+i);
						result.setErrCode(-1);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ " Dosage for:0");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "��������Ϊ:0 \n Dosage for:0");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// ִ�п���
					if (ds.getRowParm(i, buff).getValue("EXEC_DEPT_CODE").length() == 0) {
						result.setErrCode(-2);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ " Executive Dept in not null!");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "ִ�п��Ҳ���Ϊ��");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// Ƶ��
					if (ds.getRowParm(i, buff).getValue("FREQ_CODE").length() == 0) {
						result.setErrCode(-3);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ "Freq is not null");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "ҽ��Ƶ�β�����Ϊ��");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// ��Ժ��ҩ����
					if (ds.getRowParm(i, buff).getInt("TAKE_DAYS") == 0
							&& "DS".equals(ds.getRowParm(i, buff).getValue("RX_KIND"))) {
						result.setErrCode(-4);
						if ("en".equals(this.getLanguage())) {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_ENG_DESC")
									+ "Do not think 0 days");
						} else {
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "ҽ������������Ϊ0");
						}
						result.setData("ERR", "INDEX", index.get(ds.getRowParm(i, buff)
								.getValue("RX_KIND")));
						result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
					// fux modify start
					// ��˿��
					/*
					 * if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE")) &&
					 * !"Y" .equals(ds.getRowParm(i, buff) .getValue("IS_REMARK"))) { if
					 * (!INDTool.getInstance().inspectIndStock( ds.getRowParm(i,
					 * buff).getValue("EXEC_DEPT_CODE"), ds.getRowParm(i,
					 * buff).getValue("ORDER_CODE"), ds.getRowParm(i,
					 * buff).getDouble("DOSAGE_QTY"))) { result.setErrCode(-5); if
					 * ("en".equals(this.getLanguage())) {
					 * result.setErrText(ds.getRowParm(i, buff).getValue(
					 * "ORDER_ENG_DESC") + "The medicine is lack of Storage"); } else {
					 * result .setErrText(ds.getRowParm(i, buff).getValue( "ORDER_DESC")
					 * + "��治��;" + ds.getRowParm(i, buff).getValue( "ORDER_CODE")); }
					 * result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					 * buff).getValue("RX_KIND"))); result.setData("ERR", "ORDER_CODE",
					 * ds.getRowParm(i, buff) .getValue("ORDER_CODE")); return result; }
					 * }
					 */
					// fux modify end
					// ����ҩƷ�÷�
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						if (ds.getRowParm(i, buff).getValue("ROUTE_CODE").length() == 0) {
							result.setErrCode(-10);
							if ("en".equals(this.getLanguage())) {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ " Route is not null");
							} else {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_DESC")
										+ "�÷�����Ϊ�գ�");
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							return result;
						}
					}
					// PHA��ִ�п�������һ���������ܱ��� duzhw modify 20131202
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						String execDeptCode = ds.getRowParm(i, buff).getValue(
								"EXEC_DEPT_CODE");
						if (!isOpeFlg() && !getPhaDeptSql(execDeptCode)) { // ����ҽ��
							// wanglong
							// modify
							// 20140707
							result.setErrCode(-10);
							result.setErrText(ds.getRowParm(i, buff).getValue(
									"ORDER_DESC")
									+ "ҽ������ΪҩƷ��ִ�п��Ҳ���ȷ��");
							// return null;
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							return result;
						}
					}

					// ����ҽ���ײ�ʱ����
					if ("UD".equals(ds.getRowParm(i, buff).getValue("RX_KIND"))) {
						// ��ǰʱ���Ƿ񳬹����հ�ҩʱ��
						// �õ��հ�ҩʱ���
						Timestamp nowTime = SystemTool.getInstance().getDate();
						String nowTimeStr = StringTool.getString(nowTime,
								"yyyyMMddHHmmss");
						// this.messageBox_("��ǰʱ��:" + nowTimeStr);
						String nowTimeBY = StringTool.getString(nowTime, "yyyyMMdd")
								+ obj + "00";
						// this.messageBox_("���հ�ҩʱ��:" + nowTimeBY);
						// Ԥ��ʱ���ײ�ʱ��
						Timestamp ykTime = ds.getRowParm(i, buff).getTimestamp(
								"EFF_DATE");
						String ykTimeStr = StringTool.getString(ykTime,
								"yyyyMMddHHmmss");
						// this.messageBox_("Ԥ��ʱ���ײ�ʱ��:" + ykTimeStr);
						String tomorrowTime = StringTool.getString(StringTool.rollDate(
								nowTime, 1), "yyyyMMdd")
								+ "000000";
						String nowDateTime = StringTool.getString(StringTool.rollDate(
								nowTime, 0), "yyyyMMdd")
								+ "000000";

						// Ԥͣʱ��
						String ytTimeStr = ds.getRowParm(i, buff).getValue("DC_DATE");
						if (!ytTimeStr.equals("")) {// =====ʱ��У��pangben 2014-3-5
							ytTimeStr = ytTimeStr.substring(0, ytTimeStr.indexOf("."))
									.replace("-", "").replace(" ", "").replace(":", "");
						}
						// String ytTimeStr = StringTool.getString(ytTime,
						// "yyyyMMddHHmmss");
						// String ytTimeStr = "";
						// this.messageBox_("0��ʱ��:"+tomorrowTime);
						// ��ҩƷ
						if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
							// ��ǰʱ��������ڵ��ڵ��հ�ҩʱ��
							if (nowTimeStr.compareTo(nowTimeBY) > 0) {
								// Ԥ��ʱ��С�ڵ��հ�ҩʱ��
								if (ykTimeStr.compareTo(nowTimeBY) <= 0) {
									result.setErrCode(-7);
									if ("en".equals(this.getLanguage())) {
										result.setErrText(ds.getRowParm(i, buff)
												.getValue("ORDER_ENG_DESC")
												+ "Opening time cannot put less time::"
												+ StringTool.getString(StringTool
														.getTimestamp(nowTimeBY,
																"yyyyMMddHHmmss"),
														"yyyy/MM/dd HH:mm:ss"));
									} else {
										result.setErrText(ds.getRowParm(i, buff)
												.getValue("ORDER_DESC")
												+ "ҽ������ʱ�䲻��С�ڽ���סԺ��ҩʱ��:"
												+ StringTool.getString(StringTool
														.getTimestamp(nowTimeBY,
																"yyyyMMddHHmmss"),
														"yyyy/MM/dd HH:mm:ss"));
									}
									result.setData("ERR", "INDEX", index.get(ds
											.getRowParm(i, buff).getValue("RX_KIND")));
									result.setData("ERR", "ORDER_CODE", ds.getRowParm(
											i, buff).getValue("ORDER_CODE"));
									result.setData("ERR", "EFF_DATE", ds.getRowParm(i,
											buff).getData("EFF_DATE"));
									result.setData("ERR", "ROWINDEX", i);
									return result;
								}
							}

						} else {
							// ����EFF_DATE�Ƿ��Ѿ���������0��
							if (ykTimeStr.compareTo(nowDateTime) < 0) {
								result.setErrCode(-8);
								if ("en".equals(this.getLanguage())) {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_ENG_DESC")
											+ "Opening time cannot cross:"
											+ StringTool.getString(StringTool
													.getTimestamp(nowDateTime,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								} else {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_DESC")
											+ "ҽ������ʱ�䲻��С�ڽ����賿���:"
											+ StringTool.getString(StringTool
													.getTimestamp(nowDateTime,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								}
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.setData("ERR", "EFF_DATE", ds
										.getRowParm(i, buff).getData("EFF_DATE"));
								result.setData("ERR", "ROWINDEX", i);
								return result;
							}
						}
						// Ԥ��ʱ�����Ԥͣʱ��
						if (!"".equals(ytTimeStr)) {
							if (ykTimeStr.compareTo(ytTimeStr) > 0) {
								result.setErrCode(-7);
								if ("en".equals(this.getLanguage())) {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_ENG_DESC")
											+ "Stop time cannot put less time::"
											+ StringTool.getString(StringTool
													.getTimestamp(ykTimeStr,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								} else {
									result.setErrText(ds.getRowParm(i, buff).getValue(
											"ORDER_DESC")
											+ "ҽ��ͣ��ʱ�䲻��С��ҽ������ʱ��:"
											+ StringTool.getString(StringTool
													.getTimestamp(ykTimeStr,
															"yyyyMMddHHmmss"),
													"yyyy/MM/dd HH:mm:ss"));
								}
								result.setData("ERR", "INDEX", index.get(ds.getRowParm(
										i, buff).getValue("RX_KIND")));
								result.setData("ERR", "ORDER_CODE", ds.getRowParm(i,
										buff).getValue("ORDER_CODE"));
								result.setData("ERR", "DC_DATE", ds.getRowParm(i, buff)
										.getData("DC_DATE"));
								result.setData("ERR", "ROWINDEX", i);
								return result;
							}
						}
					}

					// ��˹�����¼
					// modify by huangtt 20150507
					if ("PHA".equals(ds.getRowParm(i, buff).getValue("CAT1_TYPE"))) {
						String message = DrugAllergyTool.getInstance()
								.CBDDrugAllergyCheck(
										ds.getRowParm(i, buff).getValue("MR_NO"),
										ds.getRowParm(i, buff).getValue("ORDER_CODE"),
										ds.getRowParm(i, buff).getValue("ORDER_DESC"));
						if (message.length() > 0) {
							result.setErrCode(-9);
							if ("en".equals(this.getLanguage())) {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ "For drug allergy patients��");
							} else {
								result.setErrText(message);
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", i);
							return result;
						}

					}

					// for (int j = 0; j < gmRowCount; j++) {
					// if (!tab.getDataStore().isActive(j, buffGM))
					// continue;
					// TParm gmParm = tab.getDataStore().getRowParm(j, buffGM);
					// if (ds.getRowParm(i, buff).getValue("ORDER_CODE").equals(
					// gmParm.getValue("DRUGORINGRD_CODE"))
					// && "B".equals(gmParm.getValue("DRUG_TYPE"))) {
					// result.setErrCode(-9);
					// if ("en".equals(this.getLanguage())) {
					// result.setErrText(ds.getRowParm(i, buff).getValue(
					// "ORDER_ENG_DESC")
					// + "For drug allergy patients��");
					// } else {
					// result.setErrText(ds.getRowParm(i, buff).getValue(
					// "ORDER_DESC")
					// + "������ҩƷ������");
					// }
					// result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
					// buff).getValue("RX_KIND")));
					// result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
					// .getValue("ORDER_CODE"));
					// result.setData("ERR", "ROWINDEX", i);
					// return result;
					// }
					// }

				}

				// �޸ĵ�ҽ��
				for (int i : modifRow) {
					if (!ds.getRowParm(i, buff).getValue("RX_KIND").equals("UD")) {
						if (OrderUtil.getInstance().checkOrderNSCheck(
								ds.getRowParm(i, buff), nsCheckFlg)) {
							result.setErrCode(-6);
							if ("en".equals(this.getLanguage())) {
								result
								.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_ENG_DESC")
										+ "Already audit may not modify, whether other orders?");
							} else {
								result.setErrText(ds.getRowParm(i, buff).getValue(
										"ORDER_DESC")
										+ "ҽ���Ѿ���˲������޸��Ƿ񱣴�����ҽ����");
							}
							result.setData("ERR", "INDEX", index.get(ds.getRowParm(i,
									buff).getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", ds.getRowParm(i, buff)
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", (Integer) ds.getItemData(
									i, "#ID#", buff));
							return result;
						}
					}
				}
				// ɾ����ҽ������
				int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
				if (delCount > 0) {
					TParm delParm = ds.getBuffer(ds.DELETE);
					int delRowCount = delParm.getCount("ORDER_CODE");
					for (int i = 0; i < delRowCount; i++) {
						TParm temp = delParm.getRow(i);
						if (OrderUtil.getInstance().checkOrderNSCheck(temp, nsCheckFlg)) {
							result.setErrCode(-6);
							if ("en".equals(this.getLanguage())) {
								result
								.setErrText(temp.getValue("ORDER_ENG_DESC")
										+ "Already audit may not modify, whether other orders?");
							} else {
								result.setErrText(temp.getValue("ORDER_DESC")
										+ "ҽ���Ѿ���˲�����ɾ���Ƿ񱣴�����ҽ����");
							}

							result.setData("ERR", "INDEX", index.get(temp
									.getValue("RX_KIND")));
							result.setData("ERR", "ORDER_CODE", temp
									.getValue("ORDER_CODE"));
							result.setData("ERR", "ROWINDEX", temp.getInt("#ID#"));
							return result;
						}
					}
				}
				return result;
	}

	/**
	 * ��ȡPHAִ�п����Ƿ���������sql ����ҩƷ�ı�ִ�п���ʱ��ҩ�����ʱ��������Ϣ���⡣ duzhw modify 20131202
	 */
	public boolean getPhaDeptSql(String deptCode) {
		boolean flag = false;
		String sql = "select t.* from sys_dept t where t.dept_code in ("
				+ "select a.dept_code from sys_dept a where a.ipd_fit_flg = 'Y' and a.active_flg = 'Y' and a.classify = '2'"
				+ ") and t.dept_code = '" + deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount("DEPT_CODE") > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * ���������
	 * 
	 * @param parm
	 *            TParm
	 */
	public void errSaveOrder(TParm parm) {
		// �Ƿ񾭻�ʿ���
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// ת�����ڿ���
		this.chagePage = false;
		tab.setSelectedIndex(parm.getInt("ERR", "INDEX"));
		TDS ds = odiObject.getDS("ODI_ORDER");
		// �¼�����
		int newRow[] = ds.getNewRows(ds.PRIMARY);
		// �޸ĵ�����
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		for (int i : newRow) {
			if (!ds.isActive(i, ds.PRIMARY))
				continue;
			if (parm.getErrCode() == -1) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getInt("MEDI_QTY") == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -2) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getValue(
								"EXEC_DEPT_CODE").length() == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -3) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getValue("FREQ_CODE")
						.length() == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			if (parm.getErrCode() == -4) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getInt("TAKE_DAYS") == 0) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// ���
			// if(parm.getErrCode()==-5){
			// if(ds.getRowParm(i,ds.PRIMARY).getValue("ORDER_CODE").equals(parm.getValue("ERR","ORDER_CODE"))){
			// changeTableRowColor(i,parm.getInt("ERR","INDEX"));
			// }
			// }
			// ����ҽ��Ԥ��Ԥͣʱ���ж�(ҩ)
			if (parm.getErrCode() == -7) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getData("EFF_DATE")
						.equals(parm.getData("ERR", "EFF_DATE"))) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// ����ҽ��Ԥ��Ԥͣʱ���ж�(����)
			if (parm.getErrCode() == -8) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))
						&& ds.getRowParm(i, ds.PRIMARY).getData("EFF_DATE")
						.equals(parm.getData("ERR", "EFF_DATE"))) {
					changeTableRowColor(i, parm.getInt("ERR", "INDEX"));
				}
			}
			// ������¼���
			if (parm.getErrCode() == -9) {
				if (ds.getRowParm(i, ds.PRIMARY).getValue("ORDER_CODE").equals(
						parm.getValue("ERR", "ORDER_CODE"))) {
					// changeTableRowColor(i,parm.getInt("ERR","INDEX"));
				}
			}
		}
		if (parm.getErrCode() == -6) {
			// �޸�ҽ��
			for (int i : modifRow) {
				if (OrderUtil.getInstance().checkOrderNSCheck(
						ds.getRowParm(i, buff), nsCheckFlg)) {
					// ��ҽ��������
					ds.setActive(i, false, buff);
				}
			}
			// ɾ����ҽ��
			int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
			if (delCount > 0) {
				TParm delParm = ds.getBuffer(ds.DELETE);
				int rowCountDel = ds.getDeleteCount();
				int delRowCount = delParm.getCount("ORDER_CODE");
				List delList = new ArrayList();
				for (int i = 0; i < delRowCount; i++) {
					TParm temp = delParm.getRow(i);
					if (OrderUtil.getInstance().checkOrderNSCheck(temp,
							nsCheckFlg)) {
						int delID = temp.getInt("#ID#");
						delList.add(delID);
					}
				}
				for (int i = 0; i < delList.size(); i++) {
					int rowID = Integer.parseInt(delList.get(i).toString());
					for (int j = 0; j < rowCountDel; j++) {
						if (!ds.isActive(j, ds.DELETE))
							if ((Integer) ds.getItemData(j, "#ID#", ds.DELETE) == rowID) {
								ds.setActive(j, false, ds.DELETE);
							}
					}
				}
			}
		}
	}

	/**
	 * �ı���ɫ����TABLE
	 * 
	 * @param row
	 *            int
	 * @param flg
	 *            boolean
	 */
	public void startRowColor() {
		if (colorThread != null)
			return;
		colorThread = new Thread() {
			public void run() {
				while (colorThread != null) {
					try {
						Thread.sleep(300);
						workColor();
					} catch (InterruptedException ex) {
					}
				}
				getTTable("TABLE" + (colorType + 1)).setRowColor(colorRow,
						new Color(255, 255, 255));
				getTTable("TABLE" + (colorType + 1)).setDSValue(colorRow);
			}
		};
		colorThread.start();
	}

	/**
	 * ֹͣ����
	 */
	public void stopColor() {
		colorThread = null;
	}

	/**
	 * �ı�TABLE��ɫ
	 * 
	 * @param row
	 *            int
	 */
	public void changeTableRowColor(int row, int type) {
		colorRow = row;
		colorType = type;
		startRowColor();
	}

	/**
	 * ��ʼ����
	 */
	public void workColor() {
		if (colorRow == -1) {
			this.stopColor();
			return;
		}
		colorRowState = !colorRowState;
		this.getTTable("TABLE" + (colorType + 1)).setRowColor(colorRow,
				colorRowState ? new Color(255, 0, 0) : new Color(0, 0, 0));
		this.getTTable("TABLE" + (colorType + 1)).setDSValue(colorRow);
	}

	/**
	 * ���Ӧ�Ļ�ʿվ������Ϣ
	 */
	public void sendInwStationMessages() {
		// this.messageBox("station code===="+this.getStationCode());
		// this.messageBox("depart code====="+this.getDeptCode());
		// $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
		// START==================$$//
		// client1 = SocketLink.running("", "ODISTATION", "odi");
		client1 = SocketLink
				.running("", this.getDeptCode(), this.getDeptCode());

		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		String admDate = StringTool.getString(this.getAdmDate(),
				"yyyy/MM/dd HH:mm:ss");
		/**
		 * client1.sendMessage( "INWSTATION", "CASE_NO:" + this.getCaseNo() +
		 * "|STATION_CODE:" + this.getStationCode() + "|MR_NO:" + this.getMrNo()
		 * + "|PAT_NAME:" + this.getPatName() + "|IPD_NO:" + this.getIpdNo() +
		 * "|ADM_DATE:" + admDate);
		 **/

		/**
		 * System.out.println("+++++sendMessage++++"+"CASE_NO:" +
		 * this.getCaseNo() + "|STATION_CODE:" + this.getStationCode() +
		 * "|MR_NO:" + this.getMrNo() + "|PAT_NAME:" + this.getPatName() +
		 * "|IPD_NO:" + this.getIpdNo() + "|ADM_DATE:" + admDate);
		 **/

		client1.sendMessage(this.getStationCode(), "CASE_NO:"
				+ this.getCaseNo() + "|STATION_CODE:" + this.getStationCode()
				+ "|MR_NO:" + this.getMrNo() + "|PAT_NAME:" + this.getPatName()
				+ "|IPD_NO:" + this.getIpdNo() + "|ADM_DATE:" + admDate);

		if (client1 == null)
			return;
		client1.close();

		// $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
		// END==================$$//
	}

	/**
	 * TABLE���ܸı丶ֵ
	 * 
	 * @param tag
	 *            String[]
	 */
	public void acceptTextTable(String tag[]) {
		int length = tag.length;
		if (length == 0)
			return;
		for (int i = 0; i < length; i++) {
			TTable tabTemp = (TTable) this.getComponent(tag[i]);
			tabTemp.acceptText();
		}
	}

	/**
	 * ���ҽ��
	 */
	public void onAddRow() {
		// this.messageBox_("===========onAddRow=============");
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TABLEPANE");
		int selTabIndex = tabPane.getSelectedIndex();
		// System.out.println("============"+"TABLE" + (selTabIndex + 1));
		if (selTabIndex >= 3 && selTabIndex != 4 && selTabIndex != 5)
			return;
		TTable table = null;
		if (selTabIndex < 3 || selTabIndex == 5) {
			table = getTTable("TABLE" + (selTabIndex + 1));
			if (this.isStopBillFlg()) {
				this.messageBox("E0155");
				return;
			}
			// ��δ�༭��ʱ����
			if (!this.isNewRow()) {
				// this.messageBox("1111");
				// ��ԭ��������Ϊ��ʼ����ɫ
				table
				.setRowTextColor(table.getRowCount() - 1,
						this.normalColor);
				// this.messageBox("111");
				return;
			}
			odiObject.setAttribute("CHANGE_FLG", true);
			String rxKinds[] = new String[] { "ST", "UD", "DS", "IG", "", "PA" };// SD�ǳ�Ժ��ҩ
			odiObject.setAttribute("RX_KIND", rxKinds[selTabIndex]);
			int row = table.addRow();
			odiObject.getDS("ODI_ORDER").setItem(row, "CASE_NO",
					this.getCaseNo());
			String rxk = rxKinds[selTabIndex];
			if (opeFlg && "ST".equals(rxKinds[selTabIndex])) {
				rxk = "OP";
			}
			odiObject.getDS("ODI_ORDER").setItem(row, "RX_KIND", rxk);
			odiObject.getDS("ODI_ORDER").setItem(row, "ORDER_NO",
					"999999999999");
			odiObject.getDS("ODI_ORDER").setItem(row, "ORDER_SEQ", "20");
			table.getDataStore().setActive(row, false);
			// System.out.println("row����"+row);
			// ������������ɫ
			// this.messageBox("2222");
			table.setRowTextColor(row, this.normalColor);
			// this.messageBox("222");
			rowOnly = row;
		} else {
			table = getTTable(GMTABLE);
			int rowCountGM = table.getDataStore().rowCount();
			boolean falgAdd = true;
			for (int i = 0; i < rowCountGM; i++) {
				if (!table.getDataStore().isActive(i,
						table.getDataStore().PRIMARY)) {
					falgAdd = false;
				}
			}
			if (falgAdd) {
				int rowSet = table.getDataStore().insertRow();
				Timestamp sysDate = SystemTool.getInstance().getDate();
				table.getDataStore().setItem(rowSet, "MR_NO", this.getMrNo());
				// ============xueyf modify 20120217 start
				sysDate = SystemTool.getInstance().getDate();
				table.getDataStore().setItem(rowSet, "ADM_DATE",
						StringTool.getString(sysDate, "yyyy/MM/dd"));
				// table.getDataStore().setItem(rowSet, "ADM_DATE",
				// StringTool.getString(sysDate, "yyyyMMdd"));
				// ============xueyf modify 20120217 stop
				table.getDataStore().setItem(rowSet, "DRUG_TYPE",
						this.getDrugType());
				table.getDataStore().setItem(rowSet, "DEPT_CODE",
						this.getDeptCode());
				table.getDataStore().setItem(rowSet, "DR_CODE",
						Operator.getID());
				table.getDataStore().setItem(rowSet, "ADM_TYPE", "I");
				table.getDataStore().setItem(rowSet, "CASE_NO",
						this.getCaseNo());
				table.getDataStore().setItem(rowSet, "OPT_USER",
						Operator.getID());
				table.getDataStore().setItem(rowSet, "OPT_DATE", sysDate);
				table.getDataStore().setItem(rowSet, "OPT_TERM",
						Operator.getIP());
				table.getDataStore().setActive(rowSet, false);
				table.setDSValue();
			}
			rowOnly = 0;
		}
		// System.out.println("ORDER��");
		// odiObject.getDS("ODI_ORDER").showDebug();
		// System.out.println("DSPNM��");
		// odiObject.getDS("ODI_DSPNM").showDebug();
		// System.out.println("DSPND��");
		// odiObject.getDS("ODI_DSPND").showDebug();
	}

	/**
	 * ������ҩ��Ƭһ��
	 * 
	 * @param tab
	 *            TTable
	 */
	public void insertChnOrder(TTable tab, String rxKind) {
		int row = odiObject.addChnRow(tab.getDataStore(), rxKind);
		tab.getDataStore().setActive(row, false);
		// ������������ɫ
		tab.setRowTextColor(row, this.normalColor);
		rowOnly = row;
	}

	// ����
	int gmSelect = 1;

	/**
	 * �������ѡ���¼�
	 */
	public void onSelectRadio(Object obj) {
		// �ж��Ƿ񱣴�
		if (isSaveGM()) {
			// �뱣�����ݺ���ѡ����࣡
			this.messageBox("E0171");
			switch (gmSelect) {
			case 1:
				this.getTRadioButton("PHA_DRUGALLERGY").setSelected(true);
				break;
			case 2:
				this.getTRadioButton("CF_DRUGALLERGY").setSelected(true);
				break;
			case 3:
				this.getTRadioButton("OTHER_DRUGALLERGY").setSelected(true);
				break;
			}
			return;
		}
		onChangeStart();
		gmSelect = StringTool.getInt("" + obj);
	}

	/**
	 * �Ƿ��б��������
	 * 
	 * @return boolean
	 */
	public boolean isSaveGM() {
		boolean falg = false;
		TTable table = this.getTTable(GMTABLE);
		String buff = table.getDataStore().PRIMARY;
		int newRow[] = table.getDataStore().getNewRows(buff);
		int countSaveRow = 0;
		for (int i : newRow) {
			if (!table.getDataStore().isActive(i, buff))
				continue;
			countSaveRow++;
		}
		int modifRow[] = table.getDataStore().getOnlyModifiedRows(buff);
		int delCount = table.getDataStore().getDeleteCount() < 0 ? 0 : table
				.getDataStore().getDeleteCount();
		if (countSaveRow + modifRow.length + delCount > 0)
			falg = true;
		return falg;
	}

	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	public boolean isNewRow() {
		Boolean falg = false;
		TDS dsOrder = odiObject.getDS("ODI_ORDER");
		// System.out.println("=============MMMMMMM");
		// dsOrder.showDebug();
		TParm parmBuff = dsOrder.getBuffer(dsOrder.PRIMARY);
		// System.out.println("===========");
		// dsOrder.showDebug();
		// System.out.println("parmBuff"+parmBuff);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
			// ��ֵ��ǰ�༭��
			if (!falg)
				this.rowOnly = lastRow - 1;
		} else {
			falg = true;
		}
		// System.out.println("===================================>"+falg);
		return falg;
	}

	/**
	 * ɾ��ҽ��
	 */
	public void onDelRow() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TABLEPANE");
		int selTabIndex = tabPane.getSelectedIndex();
		TTable tab = null;
		switch (selTabIndex) {
		// ��ʱҽ��
		case 0:
			tab = this.getTTable(TABLE1);
			// DCҽ��
			dcOrder(tab, 0);
			break;
			// ����ҽ��
		case 1:
			tab = this.getTTable(TABLE2);
			// DCҽ��
			dcOrder(tab, 1);
			break;
			// ��Ժ��ҩ
		case 2:
			tab = this.getTTable(TABLE3);
			// DCҽ��
			dcOrder(tab, 2);
			break;
			// ��ҩ��Ƭ
		case 3:
			tab = this.getTTable(TABLE4);
			// DCҽ��
			dcOrder(tab, 3);
			break;
			// ������¼
		case 4:
			tab = this.getTTable(GMTABLE);
			// DC������¼
			dcGMOrder(tab);
			break;
			// Ժǰ��ҩ
		case 5:
			// ɾ��Ժǰ��ҩ
			tab = this.getTTable(TABLE6);
			// ǿ��ʧȥ�༭����
			if (tab.getTable().isEditing()) {
				tab.getTable().getCellEditor().stopCellEditing();
			}

			if (tab.getSelectedRow() < 0) {
				this.messageBox("E0173");
				return;
			}

			int seletedRow = tab.getSelectedRow();
			tab.removeRow(seletedRow);
			tab.setDSValue();
			break;
		}
		// this.onChange();
		// ����TABLE
		if ("N".equals(this.clearFlg))
			this.onChange();
		if ("Y".equals(this.clearFlg))
			this.delRowNull();
		if ("Q".equals(this.clearFlg))
			this.onChange(); // shibl 20130118 modify ��ʱ�޸� ɾ����ʱҽ��
		// System.out.println("ɾ����");
		// tab.getDataStore().showDebug();
	}

	/**
	 * ɾ��������
	 * 
	 * @param tab
	 *            TTable
	 */
	public void delRxNoAll(TTable tab) {
		if (tab.getTag().equals(TABLE4)) {
			TDS ds = odiObject.getDS("ODI_ORDER");
			String rxNo = this.getTComboBox("IG_RX_NO").getSelectedID();
			ds.setFilter("");
			ds.filter();
			int rowCountRx = ds.rowCount();
			boolean ncCheckFlg = false;
			for (int i = 0; i < rowCountRx; i++) {
				if (ds.getItemString(i, "NS_CHECK_CODE").length() != 0
						&& rxNo.equals(ds.getItemData(i, "RX_NO").toString())) {
					ncCheckFlg = true;
					break;
				}
			}
			if (ncCheckFlg) {
				// �˴����Ѿ�����˵�ҽ��������DC��
				this.messageBox("E0172");
				return;
			}
			for (int i = rowCountRx - 1; i >= 0; i--) {
				if (!ds.isActive(i))
					continue;
				if (ds.getItemData(i, "RX_NO").toString().equals(rxNo)) {
					int delId = (Integer) ds.getItemData(i, "#ID#");
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					odiObject.deleteRow(ds, i);
				}
			}
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			// this.getTComboBox("IG_RX_NO").setSelectedID("");
		} else {
			String rxNo = this.getTComboBox("RX_NO").getSelectedID();
			tab.getDataStore().setFilter("");
			tab.getDataStore().filter();
			int rowCountRx = tab.getDataStore().rowCount();
			boolean ncCheckFlg = false;
			for (int i = 0; i < rowCountRx; i++) {
				if (tab.getDataStore().getItemString(i, "NS_CHECK_CODE")
						.length() != 0
						&& rxNo.equals(tab.getDataStore().getItemData(i,
								"RX_NO").toString())) {
					ncCheckFlg = true;
					break;
				}
			}
			if (ncCheckFlg) {
				this.messageBox("E0172");
				return;
			}
			for (int i = rowCountRx - 1; i >= 0; i--) {
				if (!tab.getDataStore().isActive(i))
					continue;
				if (tab.getDataStore().getItemData(i, "RX_NO").toString()
						.equals(rxNo)) {
					int delId = (Integer) tab.getDataStore().getItemData(i,
							"#ID#");
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					TDS ds = odiObject.getDS("ODI_ORDER");
					odiObject.deleteRow(ds, i);
					// tab.getDataStore().deleteRow(i);
				}
			}
			if ("N".equals(this.clearFlg))
				this.onChange();
			if ("Y".equals(this.clearFlg))
				this.delRowNull();
			if ("Q".equals(this.clearFlg))
				this.onQuery();
			// this.getTComboBox("RX_NO").setSelectedID("");
		}

	}

	/**
	 * ɾ��������¼
	 * 
	 * @param tab
	 *            TTable
	 */
	public void dcGMOrder(TTable tab) {
		int selRow = tab.getSelectedRow();
		if (selRow < 0)
			return;
		if (!tab.getDataStore().isActive(selRow))
			return;
		tab.getDataStore().deleteRow(selRow);
		tab.setDSValue();
	}

	/**
	 * ����������ɾ����λ��
	 * 
	 * @param rowId
	 *            int
	 * @param tabStore
	 *            TDataStore
	 * @return int
	 */
	public int getDelRowSet(int rowId, TDS ds) {
		int row = -1;
		int rowCount = ds.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if ((Integer) ds.getItemData(i, "#ID#") == rowId) {
				row = i;
				break;
			}
		}
		return row;
	}

	/**
	 * DC��ʱҽ��
	 * 
	 * @param tab
	 *            TTable
	 */
	public void dcOrder(TTable tab, int type) {
		// this.messageBox("==type=="+type);
		// this.messageBox("==dcOrder==");
		// System.out.println("����:" + tab.getDataStore().isFilter() + "ɾ��ǰ");
		// tab.getDataStore().showDebug();
		// �Ƿ񾭻�ʿ���
		boolean nsCheckFlg = odiObject.getAttributeBoolean("INW_NS_CHECK_FLG");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// this.messageBox_(tab.getTag());
		int rowOnlyS = tab.getSelectedRow();
		int id = (Integer) tab.getDataStore().getItemData(rowOnlyS, "#ID#");
		TParm orderParm = tab.getDataStore().getRowParm(rowOnlyS);
		// System.out.println("ɾ��ѡ��ҽ��orderParm is :: "+orderParm);
		tab.getDataStore().setFilter("");
		tab.getDataStore().filter();
		rowOnlyS = getDelRowSet(id, odiObject.getDS("ODI_ORDER"));

		// this.messageBox("===rowOnlyS===="+rowOnlyS);

		if (rowOnlyS < 0) {
			return;
		}
		// �ж��Ƿ�����ҽ��
		if (this.isNewOrder(rowOnlyS, tab.getDataStore().PRIMARY)) {
			// this.messageBox("��1111111111111111111"+"��ʿ��˱�ǣ���"+OrderUtil.getInstance()
			// .checkOrderNSCheck(orderParm, nsCheckFlg));

			if (OrderUtil.getInstance()
					.checkOrderNSCheck(orderParm, nsCheckFlg)) {
				this.messageBox("ҽ���Ѿ���˲�����ɾ��");
				return;
			}
			// �Ƿ��Ǽ���ҽ��
			if (this.isOrderSet(orderParm)) {
				// this.messageBox_("ɾ������ҽ��ϸ��");
				// ɾ������ҽ��ϸ��
				this.delOrderSetList(orderParm, "NEW");
				return;
			}
			// �Ƿ�������ҽ��
			if (this.isLinkOrder(orderParm)
					&& orderParm.getBoolean("LINKMAIN_FLG")) {
				// this.messageBox_("ɾ������ҽ��");
				// ɾ������ҽ��
				this.delLinkOrder(orderParm, "NEW");
				return;
			}
			// ɾ����ҳ����ʾ��
			// this.messageBox_("ɾ����:"+rowOnlyS);
			int delId = (Integer) tab.getDataStore().getItemData(rowOnlyS,
					"#ID#");
			odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
			tab.getDataStore().deleteRow(rowOnlyS);
		} else {
			// this.messageBox("��22222222222222222222222"+"��ʿ��˱�ǣ���"+OrderUtil.getInstance()
			// .checkOrderNSCheck(orderParm, nsCheckFlg));
			// this.messageBox_("����DC");
			// //20131029 yanjing ��ӳ���ҽ������ɾ������ҩ��
			// ============yanjing ע
			// if(type == 1){
			// String sql =
			// "SELECT ANTIBIOTIC_CODE FROM PHA_BASE WHERE ORDER_CODE = '"+orderParm.getValue("ORDER_CODE")+"'";
			// TParm antiParm = new TParm(TJDODBTool.getInstance().select(sql));
			// if(!antiParm.getValue("ANTIBIOTIC_CODE",0).equals(null)&&!"".equals(antiParm.getValue("ANTIBIOTIC_CODE",0))){
			// this.messageBox("����ҩ�����ɾ����");
			// return;
			// }
			// }
			// ============yanjing ע
			if (tab.getSelectedRow() < 0) {
				// ��ѡ����ҪDC��ҽ����
				this.messageBox("E0173");
				return;
			}
			if (OrderUtil.getInstance()
					.checkOrderNSCheck(orderParm, nsCheckFlg)
					&& !orderParm.getValue("RX_KIND").equals("UD")) {// shibl
				// 20130205
				// modify

				this.messageBox("ҽ���Ѿ���˻�ִ�в�����ɾ��");
				return;
			} else {
				// this.messageBox("33333333333333333333333");
				// ����ҽ����ɾ��ֻ�޸�
				if (type == 1) {
					// $$====Modifed by lx 2012/04/19/��ʿδ���Start===$$//
					// System.out.println("NS_CHECK_DATE��ʿ�Ƿ����====="+orderParm.getValue("NS_CHECK_DATE"));
					// δ������
					if (orderParm.getValue("NS_CHECK_DATE").equals("")
							&& orderParm.getValue("NS_CHECK_DATE").length() == 0) {
						// ����δ���ҽ����ȷʵͣ����
						if (this.messageBox("ѯ��", "ҽ����ʿδ���,ȷʵɾ����", 2) == 0) {
							if (this.isOrderSet(orderParm)) {
								// this.messageBox_("ɾ������ҽ��ϸ��");
								// ɾ������ҽ��ϸ��
								this.delOrderSetList(orderParm, "DELETE");
								return;
							}
							// �Ƿ�������ҽ��
							if (this.isLinkOrder(orderParm)
									&& orderParm.getBoolean("LINKMAIN_FLG")) {
								// this.messageBox_("ɾ������ҽ��");
								// ɾ������ҽ��
								this.delLinkOrder(orderParm, "DELETE");
								return;
							}
							int delId = (Integer) tab.getDataStore()
									.getItemData(rowOnlyS, "#ID#");

							// this.messageBox("==delId=="+delId);
							odiObject.getDS("ODI_ORDER").setAttribute("DELROW",
									delId);
							TDS ds = odiObject.getDS("ODI_ORDER");
							// System.out.println("ɾ����UT===" +
							// ds.getRowParm(rowOnlyS));
							odiObject.deleteRow(ds, rowOnlyS);

						} else {
							return;
						}

						// ��������
					} else {
						// this.messageBox("44444444444444444444444444");
						// DCҽ��
						if (this.isOrderSet(orderParm)) {
							// this.messageBox("555555555555555555555");
							// DC����ҽ��ϸ��
							this.delOrderSetList(orderParm, "MODIF");
							return;
						}
						// �Ƿ�������ҽ��
						if (this.isLinkOrder(orderParm)
								&& orderParm.getBoolean("LINKMAIN_FLG")) {
							// this.messageBox("66666666666666666666666666");
							// DC����ҽ��
							this.delLinkOrder(orderParm, "MODIF");
							return;
						}
						TParm dcParm = new TParm();
						dcParm.setData("DC_DATE", sysDate);
						dcParm.setData("DC_DEPT_CODE", this.getDeptCode());
						dcParm.setData("DC_DR_CODE", Operator.getID());
						dcParm
						.setData("DC_STATION_CODE", this
								.getStationCode());
						dcParm.setData("OPT_DATE", sysDate);
						dcParm.setData("OPT_USER", Operator.getID());
						dcParm.setData("OPT_TERM", Operator.getIP());
						TDS ds = odiObject.getDS("ODI_ORDER");
						odiObject.setItem(ds, rowOnlyS, dcParm);
					}
					// $$====Modifed by lx 2012/04/19/��ʿδ���End===$$//

				} else {

					// this.messageBox_(0);
					// this.messageBox_(Operator.getID()+"=="+orderParm.getValue("ORDER_DR_CODE"));
					// �ж�
					if (Operator.getID().equals(
							orderParm.getValue("ORDER_DR_CODE"))) {

					} else {
						TParm parm = new TParm(this.getDBTool().select(
								"SELECT * FROM ADM_INP WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
						// this.messageBox_(parm);
						// $$ ======Modified by lx2012/04/11 start==========$$//
						if ((!Operator.getID().equals(
								parm.getValue("VS_DR_CODE", 0)))
								&& (!Operator.getID().equals(
										parm.getValue("ATTEND_DR_CODE", 0)) && (!Operator
												.getID().equals(
														parm.getValue(
																"DIRECTOR_DR_CODE", 0))))
								&& (!isDutyDr())) {
							this.messageBox("��Ȩ��ɾ����");
							return;
						}
						// $$ ======Modified by lx2012/04/11 end==========$$//
					}

					// ��ʱ��Ժ��ҩ��ҩ��Ƭִ��ɾ������
					// �Ƿ��Ǽ���ҽ��
					if (this.isOrderSet(orderParm)) {
						// this.messageBox_("ɾ������ҽ��ϸ��");
						// ɾ������ҽ��ϸ��
						this.delOrderSetList(orderParm, "DELETE");
						return;
					}
					// �Ƿ�������ҽ��
					if (this.isLinkOrder(orderParm)
							&& orderParm.getBoolean("LINKMAIN_FLG")) {
						// this.messageBox_("ɾ������ҽ��");
						// ɾ������ҽ��
						this.delLinkOrder(orderParm, "DELETE");
						return;
					}
					// ɾ����ҳ����ʾ��
					// this.messageBox_("ɾ����:"+rowOnlyS);
					int delId = (Integer) tab.getDataStore().getItemData(
							rowOnlyS, "#ID#");

					// this.messageBox("==delId=="+delId);
					odiObject.getDS("ODI_ORDER").setAttribute("DELROW", delId);
					TDS ds = odiObject.getDS("ODI_ORDER");
					// System.out.println("ɾ����YZ===" + ds.getRowParm(rowOnlyS));
					odiObject.deleteRow(ds, rowOnlyS);
					// if(((TParm)this.getParameter()).getData("ODI","OPERATOR")!=null
					// &&
					// ((TParm)this.getParameter()).getData("ODI","OPERATOR").equals("OPE_N")){
					// ɾ�����뻤��ƽ̨����ɾ��
					/*
					 * TParm intenParm = new
					 * TParm(TJDODBTool.getInstance().select
					 * ("DELETE FROM OPE_INTERVENNURPLAT WHERE" +
					 * "  CASE_NO ='"+
					 * this.getCaseNo()+"'  AND ORDER_CODE =  '"+ds
					 * .getRowParm(rowOnlyS).getValue("ORDER_CODE")+"' " ));
					 */
					// ɾ�����뻤��ƽ̨����ɾ��
					// }
					// tab.getDataStore().deleteRow(rowOnlyS);
				}
			}
		}
	}

	/**
	 * �ж��Ƿ�����ҽ��
	 * 
	 * @return boolean
	 */
	public boolean isNewOrder(int row, String buff) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		int newRow[] = ds.getNewRows(buff);
		for (int i : newRow) {
			if (row == i) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * �Ƿ����޸ĵ�ҽ��
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isModifOrder(int row, String buff) {
		boolean falg = false;
		TDS ds = odiObject.getDS("ODI_ORDER");
		int modifRow[] = ds.getOnlyModifiedRows(buff);
		for (int i : modifRow) {
			if (row == i) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * �Ƿ��Ǽ���ҽ��
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) {
		boolean falg = false;
		if (orderParm.getBoolean("SETMAIN_FLG")) {
			falg = true;
		}
		return falg;
	}

	/**
	 * �Ƿ�������ҽ��
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
	 * ɾ������ҽ��ϸ��
	 * 
	 * @param orderParm
	 *            TParm
	 */
	public void delOrderSetList(TParm orderParm, String type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		int orderSetGroupNo = orderParm.getInt("ORDERSET_GROUP_NO");
		// ================�������ظ�����ɾ������========shibl
		// 20140710===============================
		String orderNo = orderParm.getValue("ORDER_NO");
		String orderSetCode = orderParm.getValue("ORDERSET_CODE");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// ����
		if ("NEW".equals(type)) {
			int newRow[] = ds.getNewRows(buff);
			List arrayListSet = new ArrayList();
			Map setIdMap = new HashMap();
			for (int i : newRow) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				// if(temp.getInt("ORDERSET_GROUP_NO")==orderSetGroupNo)
				// ds.setActive(i,false,buff);
				// �ж�ϸ��λ�ò���¼���б���
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
					arrayListSet.add(delRowSetId);
					setIdMap.put(delRowSetId, i);
				}
			}
			// ɾ��ϸ��
			for (int i = arrayListSet.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", arrayListSet.get(i));
				odiObject.deleteRow(ds, (Integer) setIdMap.get(arrayListSet
						.get(i)));
			}
		}
		// �޸�
		if ("MODIF".equals(type)) {
			int rowCount = ds.rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					TParm dcParm = new TParm();
					dcParm.setData("DC_DATE", sysDate);
					dcParm.setData("DC_DEPT_CODE", this.getDeptCode());
					dcParm.setData("DC_DR_CODE", Operator.getID());
					dcParm.setData("DC_STATION_CODE", this.getStationCode());
					dcParm.setData("OPT_DATE", sysDate);
					dcParm.setData("OPT_USER", Operator.getID());
					dcParm.setData("OPT_TERM", Operator.getIP());
					// System.out.println("�к�:"+i);
					// DCҽ��
					odiObject.setItem(ds, i, dcParm, buff);
				}
			}
		}
		// ɾ��
		if ("DELETE".equals(type)) {
			int rowCount = ds.rowCount();
			List arrayListSet = new ArrayList();
			Map setIdMap = new HashMap();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// if(temp.getBoolean("SETMAIN_FLG"))
				// continue;
				if (temp.getInt("ORDERSET_GROUP_NO") == orderSetGroupNo
						&& temp.getValue("ORDER_NO").equals(orderNo)
						&& temp.getValue("ORDERSET_CODE").equals(orderSetCode)) {
					int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
					arrayListSet.add(delRowSetId);
					setIdMap.put(delRowSetId, i);
				}
			}
			// ɾ��ϸ��
			for (int i = arrayListSet.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", arrayListSet.get(i));
				odiObject.deleteRow(ds, (Integer) setIdMap.get(arrayListSet
						.get(i)));
			}
		}

	}

	/**
	 * ɾ������ҽ��
	 * 
	 * @param orderParm
	 *            TParm
	 * @param type
	 *            String
	 */
	public void delLinkOrder(TParm orderParm, String type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		int linkNo = orderParm.getInt("LINK_NO");
		String rxKind = orderParm.getValue("RX_KIND");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.PRIMARY;
		// ����
		if ("NEW".equals(type)) {
			int newRow[] = ds.getNewRows(buff);
			List rowDelAttribute = new ArrayList();
			Map rowDels = new HashMap();
			for (int i : newRow) {
				TParm temp = ds.getRowParm(i, buff);
				if (!ds.isActive(i, buff))
					continue;
				// System.out.println("i:"+i);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
			for (int i = rowDelAttribute.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", rowDelAttribute.get(i));
				odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
						.get(i)));
			}
		}
		// �޸�
		if ("MODIF".equals(type)) {
			int rowCount = ds.rowCount();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					TParm dcParm = new TParm();
					dcParm.setData("DC_DATE", sysDate);
					dcParm.setData("DC_DEPT_CODE", Operator.getDept());
					dcParm.setData("DC_DR_CODE", Operator.getID());
					dcParm.setData("DC_STATION_CODE", Operator.getStation());
					dcParm.setData("OPT_DATE", sysDate);
					dcParm.setData("OPT_USER", Operator.getID());
					dcParm.setData("OPT_TERM", Operator.getIP());
					// DCҽ��
					odiObject.setItem(ds, i, dcParm, buff);
				}
			}
		}
		// ɾ��
		if ("DELETE".equals(type)) {
			int rowCount = ds.rowCount();
			List rowDelAttribute = new ArrayList();
			Map rowDels = new HashMap();
			for (int i = 0; i < rowCount; i++) {
				TParm temp = ds.getRowParm(i, buff);
				if (temp.getInt("LINK_NO") == linkNo
						&& temp.getValue("RX_KIND").equals(rxKind)) {
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
			for (int i = rowDelAttribute.size() - 1; i >= 0; i--) {
				ds.setAttribute("DELROW", rowDelAttribute.get(i));
				odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
						.get(i)));
			}
		}

	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		if (!checkNewModifRowCount() && !isSaveGM()) {
			// ����
			if (!PatTool.getInstance().unLockPat(this.getMrNo())) {
				this.messageBox("E0170");
			}
			return true;
		} else {
			switch (messageBox("��ʾ��Ϣ Tips", "�Ƿ񱣴�? \n Save it",
					this.YES_NO_CANCEL_OPTION)) {
					case 0:
						if (!onSave())
							return false;
						break;
					case 1:
						break;
					case 2:
						return false;
			}
		}
		// ����
		if (!PatTool.getInstance().unLockPat(this.getMrNo())) {
			// ����ʧ�ܣ�
			this.messageBox("E0170");
		}
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// ���ò���
		TTable table = null;
		TDS ds = odiObject.getDS("ODI_ORDER");
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			table = getTTable(TABLE1);
			// �������ݷ���������
			table.setFilter("");
			table.filter();
			// ɾ������
			delNewRow(table, ds, "ST");
			table.setFilter("RX_KIND='ST' AND #ACTIVE#='N'");
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			// �������ݷ���������
			table.setFilter("");
			table.filter();
			// ɾ������
			delNewRow(table, ds, "UD");
			table.setFilter("RX_KIND='UD' AND #ACTIVE#='N'");
			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			// �������ݷ���������
			table.setFilter("");
			table.filter();
			// ɾ������
			delNewRow(table, ds, "DS");
			table.setFilter("RX_KIND='DS' AND #ACTIVE#='N'");
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			ds.setFilter("");
			ds.filter();
			delNewRow(table, ds, "IG");
			ds.setFilter("RX_KIND='IG' AND #ACTIVE#='N'");
			odiObject.filter(table, ds, this.isStopBillFlg());
			break;
		case 4:
			// ������¼
			table = getTTable(GMTABLE);
			// �������ݷ���������
			table.setFilter("");
			table.filter();
			// ɾ������
			delNewRow(table, ds, "GM");
			table.setFilter("#ACTIVE#='N'");
			break;
		}
		// ��Ϊ��ҩ��Ƭʱִ��
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			// table.sort();
			table.setDSValue();
		}
		// ������
		lockRowOrder();
		// ���״̬
		this.clearFlg = "Y";
		phaApproveFlg = "";
	}

	/**
	 * ɾ������
	 * 
	 * @param tab
	 *            TTable
	 */
	public void delNewRow(TTable tab, TDS ds, String tag) {
		if (!"IG".equals(tag)) {
			String buff = tab.getDataStore().isFilter() ? tab.getDataStore().FILTER
					: tab.getDataStore().PRIMARY;
			int newRow[] = tab.getDataStore().getNewRows(buff);
			for (int row : newRow) {
				if (!tab.getDataStore().isActive(row, buff))
					continue;
				TParm orderParm = ds.getRowParm(row, buff);
				// System.out.println("=============="+orderParm);
				tab.getDataStore().deleteRow(row, buff);
				// �Ƿ��Ǽ���ҽ��
				if (this.isOrderSet(orderParm)) {// shibl modify 20130515
					// ɾ������ҽ��ϸ��
					this.delOrderSetList(orderParm, "NEW");
					return;
				}
			}
			tab.setDSValue();
		} else {
			int newRowIG[] = ds.getNewRows();
			for (int row : newRowIG) {
				if (!ds.isActive(row))
					continue;
				ds.deleteRow(row);
			}
		}
	}

	/**
	 * ���ñ�
	 */
	public void onInputList() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			yyList = true;
			// Object obj =
			// this.openDialog("%ROOT%\\config\\sys\\SysExaSheetTree.x",this,true);
			TWindow window = (TWindow) this.openWindow(
					"%ROOT%\\config\\sys\\SysExaSheetTree.x", this, true);
			window.setVisible(true);
			// window.setX(ImageTool.getScreenWidth() - window.getWidth());
			// window.setY(0);
			// window.setVisible(true);
			// if(obj.toString().equals("Y")){
			// yyList = false;
			// }
			break;
		case 1:
			// ����ҽ�����ɿ�����ҽ����
			this.messageBox("E0175");
			break;
		case 2:
			// ��Ժ��ҩ���ɿ�����ҽ����
			this.messageBox("E0175");
			break;
		case 3:
			// ��ҩ��Ƭ���ɿ�����ҽ����
			this.messageBox("E0175");
			break;
		}
	}

	/**
	 * ���ñ���������
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setYYlist(Object obj) {
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		if ("Y".equals(action.getValue("YYLIST"))) {
			yyList = false;
		}
	}

	/**
	 * ���ñ��ص�����
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheet(Object obj) {
		boolean flag = false;
		// �ж��Ƿ���TPARM����
		if (!(obj instanceof TParm)) {
			return false;
		}
		TParm action = (TParm) obj;
		action.setData("EXID_ROW", getExitRow());
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			// ��ʱ
			this.popReturn("ST", action);
			flag = true;
			break;
		}
		return flag;
	}

	/**
	 * ҽʦ�ײ�
	 */
	public void onDrPack() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			// ���ɿ�����ҽ��
			this.messageBox("E0175");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 2);
		parm.setData("RULE_TYPE", tab.getSelectedIndex());
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		// this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x",parm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * ��Ⱦҽ���ײ�
	 */
	public void onInfecPack() {
		TParm icdparm = new TParm();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 2) {
			// ���ɿ�����ҽ��
			this.yyList = false;
			this.messageBox("�������ÿ���ҩƷ");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("TAB_FLG", tab);// ===yanjing 20130909 ҳǩ���
		parm.setData("MR_NO", this.getMrNo());// add caoyong 2013908
		parm.setData("CASE_NO", this.getCaseNo());// add caoyong 2013908
		parm.setData("ADM_DATE", this.getAdmDate());// add caoyong 2013908
		parm.setData("IPD_NO", this.getIpdNo());// add caoyong 2013908
		parm.setData("PAT_NAME", this.getPatName());// add caoyong 2013908
		parm.setData("USER_ID", Operator.getID());
		parm.setData("RX_KIND", getRxKindString(tab.getSelectedIndex()));
		// parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		// parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		TParm result = null;
		if (tab.getSelectedIndex() == 0) {
			parm.setData("TYPE", "ST");// ��ʱҳǩ
			String sql = "SELECT A.OP_CODE1 AS DIAG_CODE1,B.OPT_CHN_DESC FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_CODE1=B.OPERATION_ICD  AND CASE_NO='"
					+ caseNo
					+ "' AND PHA_PREVENCODE IS NOT NULL AND (CANCEL_FLG IS NULL OR CANCEL_FLG ='N' ) ";
			// "AND (A.STATE='0' OR A.STATE='1')";
			icdparm = new TParm(this.getDBTool().select(sql));
			sql = "SELECT A.ICD_CODE AS ICD_CODE1,A.ICD_TYPE,B.ICD_CHN_DESC AS ICD_CODE,A.CASE_NO FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND IO_TYPE='Z' AND A.ICD_CODE = B.ICD_CODE AND A.ICD_TYPE = B.ICD_TYPE";
			TParm icdparm1 = new TParm(this.getDBTool().select(sql));
			if (icdparm.getCount() <= 0 && icdparm1.getCount() <= 0) {
				this.yyList = false;
				this.messageBox("û�м�⵽����ҩƷ�������������,��������");
				return;
			}
			// =====pangben 2013-12-30������
			parm.setData("ICDPARM", icdparm1.getData());
			parm.setData("PACK_CODE", icdparm1.getValue("ICD_CODE1", 0));
			if (icdparm1.getCount() <= 0) {
				parm.setData("DESC1", "");
			} else {
				parm.setData("DESC1", icdparm1.getValue("ICD_CODE1", 0)
						+ icdparm1.getValue("ICD_CODE", 0));
			}
			parm.setData("PACK_CODE", icdparm.getValue("DIAG_CODE1", 0));
			parm.setData("DESC", icdparm.getValue("DIAG_CODE1", 0)
					+ icdparm.getValue("OPT_CHN_DESC", 0));
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\odi\\ODIINFECPACKSheetST.x", parm, true);
		} else if (tab.getSelectedIndex() == 1 || tab.getSelectedIndex() == 2) {
			parm.setData("TYPE", "UDD");// ����ҳǩ
			// ��ѯ��������
			String sql1 = "SELECT A.OP_CODE1 AS DIAG_CODE1,B.OPT_CHN_DESC FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_CODE1=B.OPERATION_ICD  AND CASE_NO='"
					+ caseNo
					+ "' AND PHA_PREVENCODE IS NOT NULL AND (CANCEL_FLG IS NULL OR CANCEL_FLG ='N' )";
			// "AND (A.STATE='0' OR A.STATE='1')";yanjing 20131104 ע
			TParm icdparm1 = new TParm(this.getDBTool().select(sql1));
			String sql = "SELECT A.ICD_CODE AS ICD_CODE1,A.ICD_TYPE,B.ICD_CHN_DESC AS ICD_CODE,A.CASE_NO FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND IO_TYPE='Z' AND A.ICD_CODE = B.ICD_CODE AND A.ICD_TYPE = B.ICD_TYPE";
			icdparm = new TParm(this.getDBTool().select(sql));
			if (icdparm.getCount() <= 0 && icdparm1.getCount() <= 0) {
				this.messageBox("û�м�⵽������ϻ���������,��������");
				// ���ɿ�����ҽ��
				this.yyList = false;
				return;
			}
			parm.setData("ICDPARM", icdparm.getData());
			parm.setData("PACK_CODE", icdparm.getValue("ICD_CODE1", 0));
			if (icdparm.getCount() <= 0) {
				parm.setData("DESC", "");
			} else {
				parm.setData("DESC", icdparm.getValue("ICD_CODE1", 0)
						+ icdparm.getValue("ICD_CODE", 0));
			}
			// �������뼰����
			parm.setData("PACK_CODE1", icdparm1.getValue("DIAG_CODE1", 0));
			parm.setData("DESC1", icdparm1.getValue("DIAG_CODE1", 0)
					+ icdparm1.getValue("OPT_CHN_DESC", 0));
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\odi\\ODIINFECPACKSheetUDD.x", parm, true);
		} else {
			this.yyList = false;
			this.messageBox("ҳǩѡ����ȷ");
			return;
		}
		// System.out.println("����Ԥ�����ؽ��result is������"+result);
		if (null != result && result.getCount() > 0) {
			// �õ�ʹ���ߵ�֤���б�
			TParm lcsParm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
									+ Operator.getID()
									+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
			for (int i = 0; i < result.getCount(); i++) {
				Object obj = result.getRow(i);// =====pangben 2013-11-5
				String lcsClassCode = ((TParm) obj).getValue("LCS_CLASS_CODE");// ����ҩƷ���ҽ��֤�պ���У��
				if (tab.getSelectedIndex() == 1
						&& result.getValue("RADIO_FLG", i).equals("WRDO")
						&& result.getInt("PHA_DS_DAY", i) == 1) {
					if (!OrderUtil.getInstance().checkLcsClassCode(lcsParm,
							Operator.getID(), "" + lcsClassCode)) {
						lcsParm.setErrCode(-1);
						// ��û�д�ҽ����֤�գ�
						lcsParm.setErrText("E0166");
						this.messageBox("E0166");
						return;
					}
					this.messageBox("���������࿹���أ�����д�������뵥");
					String flg = "ODI";
					TParm newparm = new TParm();
					newparm.setData("INW", "CASE_NO", caseNo);
					newparm.setData("INW", "PAT_NAME", patName);
					newparm.setData("INW", "ADM_DATE", admDate);
					newparm.setData("INW", "MR_NO", mrNo);
					newparm.setData("INW", "ODI_FLG", flg);
					newparm.setData("INW", "IPD_NO", ipdNo);
					newparm = (TParm) openDialog(
							"%ROOT%\\config\\inp\\INPConsApplication.x",
							newparm);
					return;
				}
				onQuoteSheetInsert(obj);
			}
			phaApproveFlg = result.getValue("PHA_APPROVE_FLG");// ����ҩƷ���������Ƿ���д�������뵥==pangben
			// 2013-9-10
			if (phaApproveFlg.equals("Y") && result.getCount() > 0) {// ���ｨ�鴫�ص�ҽ����Ҫ�޸�״̬
				result.setData("OPT_USER", Operator.getID());
				result.setData("OPT_TERM", Operator.getIP());
				TParm result1 = TIOM_AppServer.executeAction(
						"action.pha.PHAAntiAction", "onUpdateStatePhaAnti",
						result);
				if (result1.getErrCode() < 0) {
					this.messageBox("�޸Ŀ���ҩƷ״̬ʧ��");
					return;
				}
			}
		}
		yyList = false;// yanjing modify 20130929
	}

	/**
	 * �����������
	 */
	public void onConsApply() {
		TParm consparm = new TParm();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 2) {
			// ���ɿ�����ҽ��
			this.messageBox("�������û�������");
			return;
		} else {
			String flg = "ODI";
			TParm parm = new TParm();
			// ��ȡtable��ֵ
			int row = getTTable("TABLE").getSelectedRow();
			if (row < 0)
				return;
			TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
			String caseNo = Rowparm.getValue("CASE_NO");
			String patName = Rowparm.getValue("PAT_NAME");
			String inDate = Rowparm.getValue("IN_DATE");
			parm.setData("INW", "CASE_NO", caseNo);
			parm.setData("INW", "PAT_NAME", patName);
			parm.setData("INW", "ADM_DATE", inDate);
			parm.setData("INW", "MR_NO", mrNo);
			parm.setData("INW", "ODI_FLG", flg);
			parm.setData("INW", "IPD_NO", ipdNo);
			parm = (TParm) openDialog(
					"%ROOT%\\config\\inp\\INPConsApplication.x", parm);
		}
	}

	/**
	 * �����ײ�
	 */
	public void onDeptPack() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			// ���ɿ�����ҽ��
			this.messageBox("E0175");
			return;
		}
		this.yyList = true;
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 1);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		parm.setData("RULE_TYPE", tab.getSelectedIndex());
		// this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x",parm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);

	}

	/**
	 * �ײ͸�ֵ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetList(Object obj) {
		boolean falg = true;
		if (obj != null) {
			List orderList = (ArrayList) obj;
			Iterator iter = orderList.iterator();
			while (iter.hasNext()) {
				TParm temp = (TParm) iter.next();
				// System.out.println("====onQuoteSheetList temp===="+temp);
				falg = onQuoteSheetInsert(temp);
				if (!falg)
					break;
			}
		}
		return falg;
	}

	/**
	 * �õ���ǰҳǩ
	 * 
	 * @param type
	 *            int
	 * @return String
	 */
	public String getRxKindString(int type) {
		String rxKind = "ST";
		switch (type) {
		case 0:
			rxKind = "ST";
			break;
		case 1:
			rxKind = "UD";
			break;
		case 2:
			rxKind = "DS";
			break;
		case 3:
			rxKind = "IG";
			break;
		}
		return rxKind;

	}

	/**
	 * ���ñ��ص�����
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetInsert(Object obj) {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tagStr = getRxKindString(tab.getSelectedIndex());
		boolean flag = true;
		// �ж��Ƿ���TPARM����
		if (!(obj instanceof TParm)) {
			return false;
		}
		TParm action = (TParm) obj;
		action.setData("EXID_ROW", getExitRow());
		if (!tagStr.equals(action.getValue("RX_KIND"))) {
			this.messageBox("E0175");
			return false;
		}
		this.popReturn(action.getValue("RX_KIND"), action);
		return flag;
	}

	/**
	 * �õ���ǰ�ɱ༭��
	 * 
	 * @return int
	 */
	public int getExitRow() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TTable table = null;
		int row = -1;
		if (tab.getSelectedIndex() > 3)
			return row;
		switch (tab.getSelectedIndex()) {
		case 0:
			table = this.getTTable(TABLE1);
			break;
		case 1:
			table = this.getTTable(TABLE2);
			break;
		case 2:
			table = this.getTTable(TABLE3);
			break;
		case 3:
			table = this.getTTable(TABLE4);
			break;
		}
		int rowCount = table.getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!table.getDataStore().isActive(i)) {
				this.rowOnly = i;
				break;
			}
		}
		return this.rowOnly;
	}

	/**
	 * �����
	 */
	public void onDeptDiag() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 4) {
			// ��ѡ���ٴ���ϣ�
			this.messageBox("E0176");
			return;
		}
		if (this.getComponent("CLNDIAG") == null) {
			// �ٴ����δ��ʼ����
			this.messageBox("E0177");
			return;
		}
		TControl control = ((TPanel) this.getComponent("CLNDIAG")).getControl();
		ODIClnDiagControl diagControl = null;
		if (control instanceof ODIClnDiagControl) {
			diagControl = ((ODIClnDiagControl) control);
		}
		// �ж�����ҽ������ҽ
		String param = "1," + Operator.getDept() + ","
				+ diagControl.getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		// this.messageBox_(result);
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		// this.messageBox_(diagList);
		diagControl.insertRow(diagList);
	}

	/**
	 * ҽʦ���
	 */
	public void onDrDiag() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 4) {
			this.messageBox("E0176");
			return;
		}
		if (this.getComponent("CLNDIAG") == null) {
			this.messageBox("E0177");
			return;
		}
		TControl control = ((TPanel) this.getComponent("CLNDIAG")).getControl();
		ODIClnDiagControl diagControl = null;
		if (control instanceof ODIClnDiagControl) {
			diagControl = ((ODIClnDiagControl) control);
		}
		// �ж�����ҽ������ҽ
		String param = "2," + Operator.getDept() + ","
				+ diagControl.getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		// this.messageBox_(result);
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		// this.messageBox_(diagList);
		diagControl.insertRow(diagList);
	}

	/**
	 * ��ҽ��
	 */
	public void onDeptOrder() {
		// this.messageBox_("��ҽ��");
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "1");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// ��ʱ0������Ƽۡ�1����ҩ��2������ҩƷ��3����ҩ��Ƭ��4��������Ŀ��5��������
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// ����
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// ��Ժ��ҩ
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			//			this.messageBox("ORDER_CODE:"+result.getValue("ORDER_CODE",i));
			//			this.messageBox("DEPT_OR_DR:"+result.getValue("DEPT_OR_DR",i));
			//			this.messageBox("DEPTORDR_CODE:"+result.getValue("DEPTORDR_CODE",i));
			//			this.messageBox("RX_TYPE:"+result.getValue("RX_TYPE",i));
			//			this.messageBox("SEQ_NO:"+result.getValue("SEQ_NO",i));
			//start 20170228 machao 
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrderr(
					result.getValue("ORDER_CODE", i),result.getValue("DEPT_OR_DR",i),result.getValue("DEPTORDR_CODE",i),result.getValue("RX_TYPE",i),result.getValue("SEQ_NO",i));
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * �ٴ�·��ģ��
	 */
	public void onAddCLNCPath() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "1");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// ��ʱ0������Ƽۡ�1����ҩ��2������ҩƷ��3����ҩ��Ƭ��4��������Ŀ��5��������
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// ����
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// ��Ժ��ҩ
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		// ========pangben 2012-06 -23���»���ٴ�·������
		String clncPathCode = "";
		StringBuffer sql = new StringBuffer();
		sql
		.append("SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = result.getValue("CLNCPATH_CODE", 0);
		}
		TParm inParm = new TParm();
		// =======�Ӡ�2012-06-04 ��õ�ǰʱ��
		// �õ���ǰʱ��
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("CLNCPATH_CODE", clncPathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("PAGE_FLG", tag);
		yyList = true;
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		// System.out.println("result::"+result);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			// yuml s 20141031
			// TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
			// result.getValue("ORDER_CODE", i));
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
					result.getValue("ORDER_CODE", i),
					result.getValue("CLNCPATH_CODE", i),
					result.getValue("SCHD_CODE", i),
					result.getValue("CHKTYPE_CODE", i),
					result.getValue("ORDER_SEQ_NO", i),
					result.getValue("ORDER_TYPE", i));
			// yuml e 20141031
			OrderParm.setData("CLP_FLG", "Y");
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * ҽʦҽ��
	 */
	public void onDrOrder() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "2");
		parm.setData("FIT", "IPD_FIT_FLG");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		String tag = "";
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		switch (tab.getSelectedIndex()) {
		// ��ʱ
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// ����
		case 1:
			parm.setData("RX_TYPE", "1,2,3,4");
			tag = "UD";
			break;
			// ��Ժ��ҩ
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
					result.getValue("ORDER_CODE", i));
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * ҽ����
	 */
	public void onSelYZD() {
		TParm parm = new TParm();
		parm.setData("INW", "CASE_NO", this.getCaseNo());
		this.openDialog("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x",
				parm);
	}

	/**
	 * ���±�
	 */
	public void onSelTWD() {
		TParm parm = new TParm();
		parm.setData("SUM", "CASE_NO", this.getCaseNo());
		parm.setData("SUM", "ADM_TYPE", "I");
		// modified by WangQing 20170428
		if (this.isOidrFlg()|| this.tFlg) {
			parm.setData("SUM", "SAVE_FLG", true);
		} else {
			parm.setData("SUM", "SAVE_FLG", false);
		}
		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
	}

	/**
	 * ���뵥
	 */
	public void onApplyList() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end 
		// �ж����뵥
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// �õ����뵥�����
		TParm emrParm = OrderUtil.getInstance().getOrderPasEMRAll(orderData,
				"ODI");
		onExeApply(emrParm, "LIS");
	}

	/**
	 * ���뵥���� ========pangben 2013-7-30
	 * 
	 * @param emrParm
	 */
	private void onExeApply(TParm emrParm, String phaType) {
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			TParm actionParm = new TParm();
			actionParm.setData("SYSTEM_CODE", "ODI");
			actionParm.setData("ADM_TYPE", "I");
			actionParm.setData("MR_NO", this.getMrNo());
			actionParm.setData("IPD_NO", this.getIpdNo());
			actionParm.setData("PAT_NAME", this.getPatName());
			actionParm.setData("CASE_NO", this.getCaseNo());
			actionParm.setData("ADM_DATE", this.getAdmDate());
			actionParm.setData("DEPT_CODE", this.getDeptCode());
			actionParm.setData("STATION_CODE", this.getStationCode());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			for(int i = 0; i < emrParm.getCount(); i++){
				emrParm.setData("DAY_OPE_FLG", i, this.getDayOpeFlg());
			}
			actionParm.setData("EMR_DATA_LIST", emrParm);
			actionParm.setData("PHATYPE", phaType);// ===pangben 2013-7-30
			//emrParm.setData("DAY_OPE_CODE",this.getDayOpeCode());
			// ����ҩƷ��ʾ���뵥
			this.openWindow("%ROOT%\\config\\emr\\EMRSingleUI.x", actionParm);
		} else {
			// û����Ҫ�༭�����뵥��
			this.messageBox("E0179");
		}
	}

	/**
	 * ����ҩƷ���뵥 ========pangben 2013-7-30
	 */
	public void onApplyListPha() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170228 end
		// �ж����뵥
		TDataStore orderData = odiObject.getDS("ODI_ORDER");
		// �õ����뵥�����
		TParm emrParm = OrderUtil.getInstance().getOrderPasEMRAll(orderData,
				"ODI_PHA");
		onExeApply(emrParm, "PHA");

	}

	/**
	 * ����ҩƷ���������� ===========pangben 2013-7-31
	 */
	private void onBoardMessage(TParm parm) {
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("USER_NAME", Operator.getName());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("SQL", " WHERE DEPT_CODE in ('0111','0401') ");// ҩ����\��Ⱦ���ƿƷ�����Ϣ
		// ִ����������
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROQlayControlAction", "onOdiBoardMessage", parm);
		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("����ʧ��" + " , " + result.getErrText());
			return;
		}
		// this.messageBox("���ͳɹ�");
	}

	/**
	 * ������д
	 */
	public void onEmrWrite() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("IPD_NO", this.getIpdNo());
		parm.setData("ADM_DATE", this.getAdmDate());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("STATION_CODE", this.getStationCode());
		if (this.isOidrFlg()) {
			parm.setData("RULETYPE", "3");
			// д������(����)
			parm.setData("WRITE_TYPE", "OIDR");
		} else if(this.tFlg){// add by wangqing 20170804 ��������
			parm.setData("RULETYPE", "3");
			// д������(����)
			parm.setData("WRITE_TYPE", "T");
		}else {
			parm.setData("RULETYPE", "2");
			parm.setData("WRITE_TYPE", "");
		}
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		if (isOpeFlg()) {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordNewUI.x", parm);// wanglong
			// modify
			// 20140707
		} else {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		}
	}

	/**
	 * ���û����¼
	 */
	public void onHLSel() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("IPD_NO", this.getIpdNo());
		parm.setData("ADM_DATE", this.getAdmDate());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("RULETYPE", "1");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		if (isOpeFlg()) {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordNewUI.x", parm);// wanglong
			// modify
			// 20140707
		} else {
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		}
	}

	/**
	 * ���ò�ѯ
	 */
	public void onSelIbs() {
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", this.getCaseNo());
		parm.setData("IBS", "MR_NO", this.getMrNo());
		parm.setData("IBS", "TYPE", "ODISTATION");
		// this.openDialog()
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	/**
	 * EMR����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrListener(TParm parm) {
		TParm parmAction = new TParm();
		parm.runListener("setMicroData", "S", "A");
	}

	/**
	 * EMR�������
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrSaveListener(TParm parm) {
		// this.messageBox_(parm);
	}

	/**
	 * ����ѡ��ر��¼�
	 */
	public void onCloseChickedCY() {
		TMovePane mp = (TMovePane) callFunction("UI|MOVEPANE|getThis");
		mp.onDoubleClicked(true);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
			.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
			.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int count = parm.getCount();
		if (count < 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// System.out.println("groupNo=-============" + groupNo);
		// System.out.println("orderSetCode===========" + orderSetCode);
		String tempCode;
		int tempNo;
		// System.out.println("count===============" + count);
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			// System.out.println("tempCode==========" + tempCode);
			// System.out.println("tempNO============" + tempNo);
			// System.out.println("setmain_flg========" +
			// parm.getBoolean("SETMAIN_FLG", i));
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				result.addData("DOSAGE_QTY", parm.getValue("MEDI_QTY", i));// modify
				// by
				// by
				// 20151015
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm ownPriceParm = new TParm(this.getDBTool().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// �����ܼ۸�
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)// modify
						// by
						// guoy
						// 20151015
						* parm.getDouble("MEDI_QTY", i);
				result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",
						0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				result
				.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
						i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = new TParm();
		if (selectIndex == 2 && (this.getTRadioButton("DCORDER").isSelected()))
			action = this.getTTable("TABLE22").getParmValue().getRow(
					getTTable("TABLE22").getSelectedRow());
		else
			action = this.getTTable("TABLE" + selectIndex).getDataStore()
			.getRowParm(
					this.getTTable("TABLE" + selectIndex)
					.getSelectedRow());
		int groupNo = action.getInt("ORDERSET_GROUP_NO");
		String orderCode = action.getValue("ORDER_CODE");
		TParm parm = getOrderSetDetails(groupNo, orderCode);
		// this.messageBox_("����ҽ��ϸ��"+parm);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = new TParm();
		if (selectIndex == 2 && (this.getTRadioButton("DCORDER").isSelected()))
			action = this.getTTable("TABLE22").getParmValue().getRow(
					getTTable("TABLE22").getSelectedRow());
		else
			action = this.getTTable("TABLE" + selectIndex).getDataStore()
			.getRowParm(
					this.getTTable("TABLE" + selectIndex)
					.getSelectedRow());
		if ("LIS".equals(action.getValue("CAT1_TYPE"))
				|| "RIS".equals(action.getValue("CAT1_TYPE"))
				|| "Y".equals(action.getValue("SETMAIN_FLG"))) {
			table
			.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu;�鿴���� \n Report,showRept");
			return;
		} else if ("PHA".equals(action.getValue("CAT1_TYPE"))) {// add by
			// wanglong
			// 20130522
			table
			.setPopupMenuSyntax("��ʾ������ҩ��Ϣ|Show Rational Drug Use,onQueryRationalDrugUse");
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * �鿴����
	 */
	public void showRept() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (tab.getSelectedIndex() > 3)
			return;
		TParm action = this.getTTable("TABLE" + selectIndex).getDataStore()
				.getRowParm(
						this.getTTable("TABLE" + selectIndex).getSelectedRow());
		// LIS����
		if ("LIS".equals(action.getValue("CAT1_TYPE"))) {
			String labNo = action.getValue("MED_APPLY_NO");
			if (labNo.length() == 0) {
				this.messageBox("E0188");
				return;
			}
			SystemTool.getInstance().OpenLisWeb(this.getMrNo());
		}
		// RIS����
		if ("RIS".equals(action.getValue("CAT1_TYPE"))) {

			if ("ECC".equals(action.getValue("ORDER_CAT1_CODE"))) {// ���������pdf
				TParm parm = new TParm();
				parm.setData("CASE_NO", action.getValue("CASE_NO"));
				parm.setData("OPE_BOOK_NO", action.getValue("MED_APPLY_NO"));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", i)
							+ ".pdf";
					String filePath = pathData.getValue("FILE_PATH", i);
					serverPath = TConfig.getSystemValue("FileServer.Main.Root")
							+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
							+ filePath.replaceFirst("JHW", "PDF");
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), serverPath + "\\" + fileName);
					if (data == null) {
						messageBox_("��������û���ҵ��ļ� " + serverPath + "\\"
								+ fileName);
						return;
					}
					try {
						FileTool.setByte(tempPath + "\\" + fileName, data);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						// ���ļ�
						runtime.exec("rundll32 url.dll FileProtocolHandler "
								+ tempPath + "\\" + fileName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {// �ǵ����� ����Ӱ��
				SystemTool.getInstance().OpenRisByMrNoAndApplyNo(
						this.getMrNo(), action.getValue("MED_APPLY_NO"));
			}
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
	 * �����޸�
	 */
	public void onRXEditAll() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		// �ж��Ƿ��Ѿ����
		if (isCheckNus()) {
			// �˴����Ѿ������ҽ�������������޸ģ�
			this.messageBox("E0180");
			return;
		}
		Object obj = this.openDialog("%ROOT%\\config\\odi\\ODIRXEditAll.x");
		if (obj == null)
			return;
		this.setZPValue(obj);
	}

	/**
	 * ���������޸�ֵ
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setZPValue(Object obj) {
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		int rowCount = this.getTTable(TABLE3).getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!this.getTTable(TABLE3).getDataStore().isActive(i))
				continue;
			Object mediQtyObj = this.getTTable(TABLE3).getDataStore()
					.getItemData(i, "MEDI_QTY");
			if (parm.getDouble("MEDI_QTY") > 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "MEDI_QTY",
						parm.getDouble("MEDI_QTY"));
			if (parm.getInt("TAKE_DAYS") > 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "TAKE_DAYS",
						parm.getInt("TAKE_DAYS"));
			if (parm.getValue("FREQ_CODE").length() != 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "FREQ_CODE",
						parm.getValue("FREQ_CODE"));
			if (parm.getValue("ROUTE_CODE").length() != 0)
				this.getTTable(TABLE3).getDataStore().setItem(i, "ROUTE_CODE",
						parm.getValue("ROUTE_CODE"));
			this.getTTable(TABLE3).setDSValue(i);
			TTableNode tableNode = new TTableNode();
			tableNode.setTable(this.getTTable(TABLE3));
			tableNode.setRow(i);
			tableNode.setColumn(3);
			if (parm.getDouble("MEDI_QTY") > 0) {
				tableNode.setValue(parm.getDouble("MEDI_QTY"));
				tableNode.setOldValue(mediQtyObj);
			} else {
				tableNode.setValue(mediQtyObj);
				tableNode.setOldValue(parm.getDouble("MEDI_QTY"));
			}
			this.onChangeTableValueDS(tableNode);
		}
	}

	/**
	 * ��ʿ�Ƿ��Ѿ����
	 * 
	 * @return boolean
	 */
	public boolean isCheckNus() {
		boolean falg = false;
		int rowCount = this.getTTable(TABLE3).getDataStore().rowCount();
		for (int i = 0; i < rowCount; i++) {
			TParm parm = this.getTTable(TABLE3).getDataStore().getRowParm(i);
			if (parm.getValue("NS_CHECK_CODE").length() != 0) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * ������ѡ���¼�
	 */
	public void onRxNoSel() {
		// ��ֹ��ʼ������
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 2)
			return;
		if (getTComboBox("RX_NO").getSelectedID().equals(onlyRxNo)) {
			this.onQuery();
			return;
		}
		// �Ƿ���û�б���Ĵ�����
		if (this.onSaveFlg(3)) {
			// �뱣������
			this.messageBox("E0181");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						getTComboBox("RX_NO").setSelectedID(onlyRxNo);
						onQuery();
					} catch (Exception e) {
					}
				}
			});
			return;
		}
		onlyRxNo = this.getValueString("RX_NO");
		this.onQuery();
		setIGParm("DS", onlyRxNo);
	}

	/**
	 * סԺ��ҽ������ѡ���¼�
	 */
	public void onRxNoSelIG() {
		// ��ֹ��ʼ������
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 3)
			return;
		if (getTComboBox("IG_RX_NO").getSelectedID().equals(onlyRxNo)) {
			this.onQuery();
			return;
		}
		// �Ƿ���û�б���Ĵ�����
		if (this.onSaveFlg(4)) {
			this.messageBox("E0181");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						getTComboBox("IG_RX_NO").setSelectedID(onlyRxNo);
						onQuery();
					} catch (Exception e) {
					}
				}
			});
			this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
					.getDS("ODI_ORDER")));
			return;
		}
		onlyRxNo = this.getValueString("IG_RX_NO");
		this.onQuery();
		// �����ܿ���
		this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
				.getDS("ODI_ORDER")));
		setIGParm("IG", onlyRxNo);
	}

	/**
	 * ������ҩ��������
	 * 
	 * @param rxNo
	 *            String
	 */
	public void setIGParm(String rxKind, String rxNo) {
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getBuffer(ds.PRIMARY);
		// ��Ժ��ҩ
		if ("DS".equals(rxKind)) {
			if (isChangValue(parm)) {
				String orgCode = ds.getItemString(0, "EXEC_DEPT_CODE");
				this.setValue("DEPT_CODEDS", orgCode);
			} else {
				this.setValue("DEPT_CODEDS", this.getOrgCode());
			}
		}
		// ��ҩ
		if ("IG".equals(rxKind)) {
			if (isChangValue(parm)) {
				this.setValue("DEPT_CODEIG", ds.getItemString(0,
						"EXEC_DEPT_CODE"));
				this.setValue("RF", ds.getItemString(0, "TAKE_DAYS"));
				this.setValue("YPJL", ds.getItemString(0, "DCT_TAKE_QTY"));
				this.setValue("IGFREQCODE", ds.getItemString(0, "FREQ_CODE"));
				this.setValue("IG_ROUTE", ds.getItemString(0, "ROUTE_CODE"));
				this.setValue("IG_DCTAGENT", ds.getItemString(0,
						"DCTAGENT_CODE"));
			} else {
				this.setValue("DEPT_CODEIG", this.getOrgCode());
				this.setValue("RF", odiObject
						.getAttribute(odiObject.DCT_TAKE_DAYS));
				this.setValue("YPJL", odiObject
						.getAttribute(odiObject.DCT_TAKE_QTY));
				this.setValue("IGFREQCODE", odiObject
						.getAttribute(odiObject.G_FREQ_CODE));
				this.setValue("IG_ROUTE", odiObject
						.getAttribute(odiObject.G_ROUTE_CODE));
				this.setValue("IG_DCTAGENT", odiObject
						.getAttribute(odiObject.G_DCTAGENT_CODE));
			}
		}
	}

	/**
	 * �Ƿ�ı��������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean isChangValue(TParm parm) {
		boolean falg = false;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("#ACTIVE#", i)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * �ҵ��´�����
	 * 
	 * @return String
	 */
	public String getRxNoID() {
		String id = "";
		int rowCount = this.rxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.rxNoTComboParm.getValue("ACTIVE", i))) {
				id = this.rxNoTComboParm.getValue("ID", i);
				break;
			}
		}
		return id;
	}

	/**
	 * �ҵ��´�����
	 * 
	 * @return String
	 */
	public String getRxNoIGID() {
		String id = "";
		int rowCount = this.igRxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.igRxNoTComboParm.getValue("ACTIVE", i))) {
				id = this.igRxNoTComboParm.getValue("ID", i);
				break;
			}
		}
		return id;
	}

	/**
	 * �����´�����
	 */
	public void onNewRxNo(String type) {
		// �Ƿ����µĴ�����û�б���
		if (isRxNoID()) {
			// �����´�����δ����ʹ��
			if ("1".equals(type))
				this.messageBox("E0182");
			onRxNoSel();
			return;
		}
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODI", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			// ����������ʧ�ܣ�
			this.messageBox("E0183");
			return;
		}
		int presrtNo = getPresrtNo(this.rxNoTComboParm);
		this.rxNoTComboParm.addData("ID", rxNo);
		this.rxNoTComboParm.addData("NAME", "���� Rx");
		this.rxNoTComboParm.addData("TEXT", "��" + presrtNo + "��");
		this.rxNoTComboParm.addData("ACTIVE", "Y");
		this.rxNoTComboParm.addData("PRESRT_NO", presrtNo);
		this.rxNoTComboParm.setData("COUNT", presrtNo);
		this.rxNoTComboParm.setData("ACTION", "COUNT", presrtNo);
		this.getTComboBox("RX_NO").setParmValue(this.rxNoTComboParm);
		this.getTComboBox("RX_NO").setSelectedID(rxNo);
		onRxNoSel();
	}

	/**
	 * �õ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return int
	 */
	public int getPresrtNo(TParm parm) {
		int presrtNo = 0;
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (presrtNo > parm.getInt("PRESRT_NO", i))
				continue;
			presrtNo = parm.getInt("PRESRT_NO", i);
		}
		return presrtNo + 1;
	}

	/**
	 * ��ҩ��Ƭ������
	 */
	public void onNewRxNoIG(String type) {
		// �Ƿ����µĴ�����û�б���
		if (isRxNoIGID()) {
			if ("1".equals(type))
				this.messageBox("E0182");
			this.onRxNoSelIG();
			return;
		}
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODI", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			this.messageBox("E0183");
			return;
		}
		int presrtNo = getPresrtNo(this.igRxNoTComboParm);
		this.igRxNoTComboParm.addData("ID", rxNo);
		this.igRxNoTComboParm.addData("NAME", "���� Rx");
		this.igRxNoTComboParm.addData("TEXT", "��" + presrtNo + "��");
		this.igRxNoTComboParm.addData("ACTIVE", "Y");
		this.igRxNoTComboParm.addData("PRESRT_NO", presrtNo);
		this.igRxNoTComboParm.setData("COUNT", presrtNo);
		this.igRxNoTComboParm.setData("ACTION", "COUNT", presrtNo);
		this.getTComboBox("IG_RX_NO").setParmValue(this.igRxNoTComboParm);
		this.getTComboBox("IG_RX_NO").setSelectedID(rxNo);
		this.onRxNoSelIG();
	}

	/**
	 * �Ƿ����µĴ�����(��ҽ)
	 * 
	 * @return boolean
	 */
	public boolean isRxNoIGID() {
		boolean falg = false;
		int rowCount = this.igRxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.igRxNoTComboParm.getValue("ACTIVE", i))) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * �Ƿ����µĴ�����
	 * 
	 * @return boolean
	 */
	public boolean isRxNoID() {
		boolean falg = false;
		int rowCount = this.rxNoTComboParm.getCount("ACTIVE");
		for (int i = 0; i < rowCount; i++) {
			if ("Y".equals(this.rxNoTComboParm.getValue("ACTIVE", i))) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * ɾ����
	 */
	public void onDelRxNo() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		this.delRxNoAll(this.getTTable(TABLE3));
	}

	/**
	 * ��ҽɾ����
	 */
	public void onDelIGRxNo() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.getTComboBox("IG_RX_NO").getSelectedID().length() == 0) {
			this.messageBox("E0069");
			return;
		}
		this.delRxNoAll(this.getTTable(TABLE4));
		// �����ܿ���
		this.setValue("MEDI_QTYALL", this.getChnPhaMediQtyAll(odiObject
				.getDS("ODI_ORDER")));
	}

	/**
	 * ��Ѫ����
	 */
	public void onBXResult() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TParm inparm = new TParm();
		inparm.setData("MR_NO", this.getMrNo());
		inparm.setData("ADM_TYPE", "I");
		inparm.setData("USE_DATE", SystemTool.getInstance().getDate());
		inparm.setData("DEPT_CODE", this.getDeptCode());
		inparm.setData("DR_CODE", Operator.getID());
		inparm.setData("ICD_CODE", this.getIcdCode());
		inparm.setData("ICD_DESC", this.getIcdDesc());
		inparm.setData("CASE_NO", this.getCaseNo());
		// û����ʷ��¼
		this.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", inparm);
	}

	/**
	 * ȡѪ���� add by yangjj 20150427
	 */
	public void onQXResult() {

		TParm parmdate = new TParm();

		// add by yangjj 20150601
		boolean opeFlg = ((TParm) this.getParameter()).getBoolean("ODI",
				"OPE_FLG");

		String caseno = this.getCaseNo();
		String deptCode = this.getValueString("DEPT_CODE");
		String mrno = this.getMrNo();

		parmdate.setData("ADM_TYPE", "I");
		parmdate.setData("IPD_NO", getValueString("IPD_NO"));
		parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
		parmdate.setData("DR_CODE", Operator.getID());
		parmdate.setData("CASE_NO", caseno);
		parmdate.setData("MR_NO", mrno);

		if (opeFlg) {
			parmdate.setData("DEPT_CODE", getValueString("OP_DEPT_CODE"));
			parmdate.setData("OPE_ROOM", getValueString("ROOM_NO"));
		} else {
			parmdate.setData("DEPT_CODE", deptCode);
			parmdate.setData("OPE_ROOM", "");
		}

		parmdate.setData("OPE_FLG", opeFlg);
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parmdate);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));

	}

	/**
	 * ��������
	 */
	public void onSSMZ() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("STATION_CODE", this.getStationCode());
		parm.setData("ICD_CODE", this.getIcdCode());
		parm.setData("ADM_TYPE", "I");// �ż�ס��
		parm.setData("BOOK_DEPT_CODE", this.getDeptCode());// ���벿��
		parm.setData("BOOK_DR_CODE", Operator.getID());// ������Ա
		parm.setData("DAY_OPE_FLG",this.getDayOpeFlg());
		this.openDialog("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
	}

	/**
	 * ��Ժ֪ͨ
	 */
	public void onOutHosp() {
		// boolean falg = false;
		// TParm parm = new
		// TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+this.getCaseNo()+"' AND RX_KIND='UD' AND DC_DATE IS NULL"));
		// if(parm.getCount()>0){
		// if(this.messageBox("��ʾ��Ϣ","�Ƿ�ͣ��ҽ��",this.YES_NO_OPTION)!=0){
		// this.messageBox("��δͣ��ҽ�������Կ�����Ժ֪ͨ��");
		// return;
		// }
		// TParm saveParm = new
		// TParm(this.getDBTool().update("UPDATE ODI_ORDER SET DC_DR_CODE='"+Operator.getID()+"' ,DC_DATE=SYSDATE,DC_DEPT_CODE='"+Operator.getDept()+"' WHERE CASE_NO='"+this.getCaseNo()+"' AND DC_DATE IS NULL"));
		// if(saveParm.getErrCode()<0){
		// this.messageBox("ͣ��ʧ�ܣ�");
		// }else{
		// this.messageBox("ͣ�óɹ���");
		// }
		// }
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		this
		.openDialog("%ROOT%\\config\\adm\\ADMDrResvOut.x", this
				.getCaseNo());
	}

	/**
	 * ���ﲡ��
	 */
	public void onOpdBL() {
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + this.getMrNo()
				+ "' AND (ADM_TYPE='O' OR ADM_TYPE='E')"));
		if (opdParm.getCount() < 0) {
			// �˲���û�����ﲡ����
			this.messageBox("E0184");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);
	}

	/**
	 * ���鱨��
	 */
	public void onLis() {
		SystemTool.getInstance().OpenLisWeb(this.getMrNo());
	}

	/**
	 * ��鱨��
	 */
	public void onRis() {
		SystemTool.getInstance().OpenRisWeb(this.getMrNo());
	}

	/**
	 * ҽ��ȫͣ
	 */
	public void onStopUD() {
		BYXflg=false;
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 start
		if (this.tFlg) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170428 end
		if (this.messageBox("��ʾ��Ϣ Tips", "�Ƿ�ͣ��ҽ�� \n Out medical advice if",
				this.YES_NO_OPTION) != 0) {
			return;
		}
		TParm saveParm = new TParm(
				this
				.getDBTool()
				.update(
						"UPDATE ODI_ORDER SET DC_DR_CODE='"
								+ Operator.getID()
								+ "' ,DC_DATE=SYSDATE,DC_DEPT_CODE='"
								+ this.getDeptCode()
								+ "' WHERE CASE_NO='"// shibl 20130312
								// modify
								// ȥ�������ڿ���
								+ this.getCaseNo()
								+ "' AND (DC_DATE IS NULL OR (DC_DATE IS NOT NULL AND DC_DATE > SYSDATE)) "
								+ " AND RX_KIND='UD'"));
		if (saveParm.getErrCode() < 0) {
			// ͣ��ʧ��
			this.messageBox("E0185");
		} else {
			// ͣ�óɹ���
			this.messageBox("E0186");
		}
		// ���ó�Ժ��ҩ����ǩ
		// initDSRxNoCombo();
		// ������ҩ��Ƭ����ǩ
		// initIGRxNoCombo();
		// TABLE����
		initDataStoreToTable();
		// ���ó�Ժ��ҩ����ǩĬ��ѡ��
		// this.getTComboBox("RX_NO").setSelectedID("");
		// this.onRxNoSel();
		// ������ҩ��Ƭ����ǩĬ��ѡ��
		// this.getTComboBox("IG_RX_NO").setSelectedID("");
		// this.onRxNoSelIG();
		this.clearFlg = "N";
		// this.setValue("MEDI_QTYALL",0.0);
	}

	/**
	 * ������ҩ
	 */
	public void onRational() {
		if (!passIsReady) {
			this.messageBox("E0067");
			return;
		}
		if (!passFlg) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		if (((TTabbedPane) this.getComponent("TABLEPANE")).getSelectedIndex() == 4) {
			this.messageBox("������ҩ�����ù�����¼��");
			return;
		}
		TParm parm = setodiRecipeInfo();
		// System.out.println("----------------------"+parm);
		if (parm.getCount("ERR") > 0) {
			this.messageBox("E0068");
			return;
		}
		if (!(parm.getCount("ORDER_CODE") > 0)) {
			this.messageBox("δ��⵽ҩƷ��");
			return;
		}
		// סԺʹ��
		PassDriver.PassDoCommand(3);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			PassDriver.PassGetWarn1(parm.getValue("SEQ", i) + "");
			// System.out.println("-------------------"+PassDriver.PassGetWarn1(parm.getValue("SEQ",
			// i) + ""));
		}
	}

	/**
	 * �õ�������ҩ��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String[]
	 */
	public String[] getOrderInfo(TParm parm) {
		String[] result = new String[12];
		// ҩƷΨһ�룺RX_NO+FILL0(SEQ,3)+ORDER_CODE
		result[0] = parm.getValue("ORDER_NO")
				+ StringTool.fill0(parm.getValue("SEQ_NO") + "", 3)
				+ parm.getValue("ORDER_CODE");
		result[1] = parm.getValue("ORDER_CODE");
		result[2] = parm.getValue("ORDER_DESC");
		result[3] = parm.getValue("MEDI_QTY");
		result[4] = OrderUtil.getInstance().getUnit(parm.getValue("MEDI_UNIT"));
		result[5] = OrderUtil.getInstance().getFreq(parm.getValue("FREQ_CODE"));
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		result[6] = date;
		result[7] = date;
		result[8] = OrderUtil.getInstance().getRoute(
				parm.getValue("ROUTE_CODE"));
		result[9] = "1";
		String type = "UD".equals(parm.getValue("RX_KIND")) ? "0" : "1";
		result[10] = type;
		result[11] = Operator.getName();
		return result;
	}

	/**
	 * 
	 * �Զ���������ҩ
	 * 
	 */
	private boolean checkDrugAuto() {
		// ������ҩ����
		if (!passIsReady) {
			return true;
		}
		// ��ʼ����ʶ
		if (!passFlg) {
			return true;
		}
		PassTool.getInstance().setadmPatientInfo(this.getCaseNo());
		PassTool.getInstance().setAllergenInfo(this.getMrNo());
		PassTool.getInstance().setadmMedCond(this.getCaseNo());
		TParm parm = setodiRecipeInfoAuto();
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "��ҩƷʹ�ò�����,�Ƿ�浵?", "��Ϣ",
				JOptionPane.YES_NO_OPTION) != 0) {
			TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
			String rx_no = "";
			int selTabIndex = tab.getSelectedIndex();
			TDS ds = odiObject.getDS("ODI_ORDER");
			switch (selTabIndex) {
			case 0:
				// ��ʱ
				// �������ݷ���������
				ds
				.setFilter("RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 1:

				// ����
				// �������ݷ���������
				ds
				.setFilter("RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 2:

				// ��Ժ��ҩ
				// �������ݷ���������
				ds
				.setFilter("RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
				break;
			case 3:
				// ��ҩ��Ƭ
				rx_no = this.getValueString("IG_RX_NO");
				ds
				.setFilter("RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='' AND #NEW#='Y'  ");
				ds.filter();
			}
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			int newRow[] = ds.getNewRows(buff);
			for (int i : newRow) {
				this.DelOrder(newRow[0]);
			}
			return false;
		}
		return true;
	}

	/**
	 * סԺҽ�����ҩƷ�Զ�
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm setodiRecipeInfoAuto() {
		TParm parm = setodiRecipeInfo();
		PassDriver.PassDoCommand(1);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			result.addData("FLG", PassDriver.PassGetWarn1(parm.getValue("SEQ",
					i)));
		}
		return result;
	}

	/**
	 * ����סԺҽ��ҩƷ��Ϣ
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm setodiRecipeInfo() {
		TParm parm = new TParm();
		int j;
		String[] orderInfo;
		String[] orderInfo1;
		String[] orderInfo2;
		Object obj = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
		TDS ds = odiObject.getDS("ODI_ORDER");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		// ��Ժ��ҩ
		if (tab.getSelectedIndex() == 2) {
			ds
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='DS' AND #ACTIVE#='N'");
			ds.filter();
		}
		String sql1 = "";
		// ������ʱ
		if (tab.getSelectedIndex() == 0 || tab.getSelectedIndex() == 1) {
			sql1 = " SELECT * " + " FROM   ODI_ORDER A  "
					+ " WHERE  A.CASE_NO ='" + this.caseNo + "'"
					+ " AND (A.RX_KIND='ST' OR A.RX_KIND='UD')"
					+ " AND    A.CAT1_TYPE = 'PHA' "
					+ " AND    A.DC_DATE IS NULL ";
			// ds.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='' AND CAT1_TYPE = 'PHA' ) OR RX_KIND='ST' AND CAT1_TYPE = 'PHA' AND  #ACTIVE#='N'");
			// ds.filter();
			// String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			// Timestamp d1 = SystemTool.getInstance().getDate();
			// String nowtime1 = ("" + d1).substring(0, 10).replaceAll("-", "");
			// long nowStr1 = d1.getTime();
			// long startStr1 = StringTool.getTimestamp(nowtime1 + "000000",
			// "yyyyMMddHHmmss").getTime();
			// for (int i = 0; i < buff.length(); i++) {
			// TParm temp = ds.getRowParm(i);
			// String effdate = temp.getValue("EFF_DATE");
			// if (!effdate.equals("")) {
			// long leffDate = strToDate(
			// temp.getValue("EFF_DATE").substring(0, 19)
			// .replaceAll("-", "/"),
			// "yyyy/MM/dd HH:mm:ss").getTime();
			// if (temp.getValue("RX_KIND").equals("ST")
			// && leffDate < startStr1)
			// continue;
			// }
			// if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
			// continue;
			// }
			// System.out.println("-------ST-----------"+temp);
			// orderInfo1 = this.getOrderInfo(temp);
			// j = PassDriver.PassSetRecipeInfo(orderInfo1[0], orderInfo1[1],
			// orderInfo1[2], orderInfo1[3], orderInfo1[4], orderInfo1[5],
			// orderInfo1[6], orderInfo1[7], orderInfo1[8], orderInfo1[9],
			// orderInfo1[10], orderInfo1[11]);
			// if (j != 1) {
			// parm.addData("ERR", orderInfo1[0]);
			// break;
			// } else {
			// parm.addData("SEQ", orderInfo1[0]);
			// parm.addData("ORDER_CODE", orderInfo1[1]);
			// }
			// }
			// Object obj1 = odiObject.getAttribute(odiObject.OID_DSPN_TIME);
			// TDS dst = odiObject.getDS("ODI_ORDER");
			// dst.setFilter("(RX_KIND='UD'  AND CAT1_TYPE = 'PHA' ) OR RX_KIND='UD' AND CAT1_TYPE = 'PHA' AND #ACTIVE#='N'");
			// dst.filter();
			// buff = dst.isFilter() ? dst.FILTER : dst.PRIMARY;
			// for (int i = 0; i < buff.length(); i++) {
			// TParm temp = ds.getRowParm(i);
			// if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
			// continue;
			// }
			// if(!temp.getValue("DC_DATE").equals(""))
			// continue;
			// System.out.println("-------UD-----------"+temp);
			// orderInfo2 = this.getOrderInfo(temp);
			// j = PassDriver.PassSetRecipeInfo(orderInfo2[0], orderInfo2[1],
			// orderInfo2[2], orderInfo2[3], orderInfo2[4], orderInfo2[5],
			// orderInfo2[6], orderInfo2[7], orderInfo2[8], orderInfo2[9],
			// orderInfo2[10], orderInfo2[11]);
			// if (j != 1) {
			// parm.addData("ERR", orderInfo2[0]);
			// break;
			// } else {
			// parm.addData("SEQ", orderInfo2[0]);
			// parm.addData("ORDER_CODE", orderInfo2[1]);
			// }
			// }
		}
		// ��ҩ��Ƭ
		if (tab.getSelectedIndex() == 3) {
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='IG' AND #ACTIVE#='N'");
			ds.filter();
		}
		if (tab.getSelectedIndex() == 0 || tab.getSelectedIndex() == 1) {
			TParm USparm = new TParm(TJDODBTool.getInstance().select(sql1));
			if (USparm.getErrCode() < 0) {
				return null;
			}
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			Timestamp d1 = SystemTool.getInstance().getDate();
			String nowtime1 = ("" + d1).substring(0, 10).replaceAll("-", "");
			long nowStr1 = d1.getTime();
			long startStr1 = StringTool.getTimestamp(nowtime1 + "000000",
					"yyyyMMddHHmmss").getTime();
			for (int i = 0; i < USparm.getCount(); i++) {
				String effdate = USparm.getValue("EFF_DATE", i);
				if (!effdate.equals("")) {
					long leffDate = strToDate(
							USparm.getValue("EFF_DATE", i).substring(0, 19)
							.replaceAll("-", "/"),
							"yyyy/MM/dd HH:mm:ss").getTime();
					if (USparm.getValue("RX_KIND", i).equals("ST")
							&& leffDate < startStr1)
						continue;
				}
				TParm temp = USparm.getRow(i);
				String caseNo = temp.getValue("CASE_NO");
				String orderNo = temp.getValue("ORDER_NO");
				String orderSeq = temp.getValue("ORDER_SEQ");
				if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
					continue;
				}
				// ----------------------start------------------------------------------
				// ɾ����ҽ������ shibl 20120713 add ����ɾ����ҽ�����滻�޸ĵ�ҽ��
				int delCount = ds.getDeleteCount() < 0 ? 0 : ds
						.getDeleteCount();
				boolean delflg = false;
				if (delCount > 0) {
					TParm delParm = ds.getBuffer(ds.DELETE);
					int delRowCount = delParm.getCount("ORDER_CODE");
					for (int di = 0; di < delRowCount; di++) {
						TParm dParm = delParm.getRow(di);
						String delcaseNo = dParm.getValue("CASE_NO");
						String delorderNo = dParm.getValue("ORDER_NO");
						String delorderSeq = dParm.getValue("ORDER_SEQ");
						if ((delcaseNo + delorderNo + delorderSeq)
								.equals(caseNo + orderNo + orderSeq)) {
							delflg = true;
							break;
						}
					}
				}
				if (delflg)
					continue;
				// �޸ĵ�����
				int modifRow[] = ds.getOnlyModifiedRows(buff);
				TParm modParm = new TParm();
				for (int ti : modifRow) {
					modParm = ds.getRowParm(ti, buff);
					String modcaseNo = modParm.getValue("CASE_NO");
					String modorderNo = modParm.getValue("ORDER_NO");
					String modorderSeq = modParm.getValue("ORDER_SEQ");
					if ((modcaseNo + modorderNo + modorderSeq).equals(caseNo
							+ orderNo + orderSeq))
						temp = modParm;
				}
				orderInfo = this.getOrderInfo(temp);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
			// ----------------------end------------------------------------------
			// �¼ӵ�����
			int newRow[] = ds.getNewRows(buff);
			for (int i : newRow) {
				if (!ds.isActive(i, buff))
					continue;
				TParm newParm = ds.getRowParm(i, buff);
				if (!newParm.getValue("RX_KIND").equals("ST")
						&& !newParm.getValue("RX_KIND").equals("UD"))
					continue;
				if (!newParm.getValue("CAT1_TYPE").equals("PHA"))
					continue;
				if (newParm.getValue("ORDER_DESC").equals("")) {
					continue;
				}
				orderInfo = this.getOrderInfo(newParm);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
		} else {
			String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
			for (int i = 0; i < buff.length(); i++) {
				TParm temp = ds.getRowParm(i);
				if (StringUtil.isNullString(temp.getValue("ORDER_DESC"))) {
					continue;
				}
				orderInfo = this.getOrderInfo(temp);
				j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
						orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
						orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
						orderInfo[10], orderInfo[11]);
				if (j != 1) {
					parm.addData("ERR", orderInfo[0]);
					break;
				} else {
					parm.addData("SEQ", orderInfo[0]);
					parm.addData("ORDER_CODE", orderInfo[1]);
				}
			}
		}
		return parm;
	}

	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * ���м���ʷ�����ʷҳǩͷ�ͱ�ɫ ��������һ�δ򿪽��������true ����ɾ���޸������false
	 * ���ڵ�һ�δ򿪽���û��������е�����һ�����ݵ���� ɾ������ʱ���Ѿ�������������Ա���е����������Ǵ���1
	 * =============pangben modify 20110608
	 */
	public void onDiagPnChange(boolean firstOpen) {
		// ������¼
		TTable table = getTTable(GMTABLE);
		table.setFilter("DRUG_TYPE='" + getDrugType() + "' AND MR_NO='"
				+ this.getMrNo() + "'");
		table.getFilter();
		table.setDSValue();
		TTabbedPane p = (TTabbedPane) this.getComponent("TABLEPANE");
		// �򿪽���
		// if (table.getRowCount() >= 1 && firstOpen) {
		// p.setTabColor(4, red);
		// // ����ɾ���޸�
		// } else if (table.getRowCount() > 1 && !firstOpen) {
		// p.setTabColor(4, red);
		// } else
		// p.setTabColor(4, null);// û������
		//

		String sql = " SELECT DRUG_TYPE FROM OPD_DRUGALLERGY WHERE MR_NO='"
				+ this.getMrNo() + "' AND DRUG_TYPE != 'N'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			p.setTabColor(4, red);
		} else {
			p.setTabColor(4, null);
		}

	}

	/**
	 * ��õ�ǰҩƷ��Ĭ������ ==============pangben modify 20110609
	 * 
	 * @return TParm
	 */
	public TParm getMediQty(TParm stIndParm, TParm actionParm) {
		TParm mediQTY = null;
		String mediQtySQL = "SELECT MEDI_QTY,MEDI_UNIT,DOSAGE_UNIT,FREQ_CODE,ROUTE_CODE FROM PHA_BASE WHERE ORDER_CODE='"
				+ actionParm.getValue("ORDER_CODE") + "'";
		mediQTY = new TParm(TJDODBTool.getInstance().select(mediQtySQL));
		return mediQTY;
	}

	/**
	 * ���ҽ��ܿر�ʶ
	 */
	public String getctrflg(String order_code) {
		String ctr_flg;
		TParm flg = SYSFeeTool.getInstance().getCtrFlg(order_code);
		ctr_flg = flg.getValue("CRT_FLG", 0);
		return ctr_flg;
	}

	/**
	 * ɾ��ҽ������
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public void DelOrder(int row) {
		// ��ǰѡ��ҳǩ
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TDS ds = odiObject.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TTable table = null;
		String rx_no = "";
		ds.deleteRow(row, buff);
		int selTabIndex = tab.getSelectedIndex();
		switch (selTabIndex) {
		case 0:
			// ��ʱ
			table = getTTable(TABLE1);
			// �������ݷ���������
			table
			.setFilter("(RX_KIND='ST' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='ST' AND #ACTIVE#='N'");
			break;
		case 1:
			// ����
			table = getTTable(TABLE2);
			// �������ݷ���������
			table
			.setFilter("(RX_KIND='UD' AND HIDE_FLG = 'N' AND DC_DATE='') OR RX_KIND='UD' AND #ACTIVE#='N'");
			break;
		case 2:
			// ��Ժ��ҩ
			table = getTTable(TABLE3);
			rx_no = this.getValueString("RX_NO");
			// �������ݷ���������
			table
			.setFilter("(RX_KIND='DS' AND HIDE_FLG = 'N' AND DC_DATE='' AND RX_NO='"
					+ rx_no + "') OR RX_KIND='DS' AND #ACTIVE#='N'");
			break;
		case 3:
			// ��ҩ��Ƭ
			table = getTTable(TABLE4);
			rx_no = this.getValueString("IG_RX_NO");
			ds
			.setFilter("(RX_KIND='IG' AND HIDE_FLG = 'N' AND DC_DATE=''  AND RX_NO='"
					+ rx_no + "') OR RX_KIND='IG' AND #ACTIVE#='N'");
			ds.filter();
			int totRow = ds.rowCount();
			if (!StringUtil.isNullString(ds.getItemString(totRow - 1,
					"ORDER_CODE"))
					|| totRow % 4 != 0 || totRow < 1) {
				for (int a = 0; a < 4 - totRow % 4; a++) {
					ds.setItem(a, "PHA_TYPE", "G");
				}
			}
			TParm dataparm = odiObject.getDS("ODI_ORDER").getBuffer(ds.PRIMARY);
			TParm tableParm = new TParm();
			for (int j = 0; j < dataparm.getCount(); j++) {
				int idx = j % 4 + 1;
				tableParm.setData("ORDER_DESC" + idx, 1, dataparm.getValue(
						"ORDER_DESC", j));
				tableParm.setData("MEDI_QTY" + idx, 1, dataparm.getDouble(
						"MEDI_QTY", j));
				tableParm.setData("DCTEXCEP_CODE" + idx, 1, dataparm.getValue(
						"DCTEXCEP_CODE", j));
			}
			tableParm.setCount(1);
			callFunction("UI|TABLE4|setParmValue", tableParm);
			odiObject.filter(table, ds, this.isStopBillFlg());
			break;
		}
		// ��Ϊ��ҩ��Ƭʱִ��
		if (tab.getSelectedIndex() != 3) {
			table.filter();
			table.setDSValue();
		}
		this.onAddRow();
	}

	/**
	 * �����ٴ�·��ʱ��
	 */
	public void intoDuration() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", caseNo);
		// ==liling 20140821 modify start====
		String clncPathCode = "";
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append("SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"
				+ caseNo
				+ "' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' AND ROWNUM<2 ");
		// System.out.println("�õ��ٴ�·��sql:"+sqlbf.toString());
		TParm parm = new TParm(TJDODBTool.getInstance()
				.select(sqlbf.toString()));
		if (parm.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("��ִ��׼��·������");
			return;
		}
		sendParm.setData("CLNCPATH_CODE", clncPathCode);
		// ==liling 20140821 modify end====
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);

	}

	/**
	 * �����ٴ�֪ʶ��
	 */
	public void onLook() {
		this.openDialog("%ROOT%\\config\\emr\\EMRUIcpl.x");

	}

	/**
	 * ������� ==========pangben modify 20110706
	 */
	public void onShow() {

		if (null == this.getValueString("MR_NO")
				|| this.getValueString("MR_NO").length() <= 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַ
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// ������ҳ����
			run.exec("IEXPLORE.EXE " + ip + this.getValueString("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �������
	 */
	public void onCxShow() {
		TParm result = queryPassword();
		String user_password = result.getValue("USER_PASSWORD", 0);
		String url = "http://" + getWebServicesIp() + "?userId="
				+ Operator.getID() + "&password=" + user_password + "&mrNo="
				+ this.getMrNo() + "&caseNo=" + getCaseNo();
		try {
			Runtime.getRuntime().exec(
					String.valueOf(String.valueOf((new StringBuffer(
							"cmd.exe /c start iexplore \"")).append(url)
							.append("\""))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TParm queryPassword() {
		String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"
				+ Operator.getID() + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ��ȡ�����ļ�
	 * 
	 * @author shendr
	 */
	public static TConfig getProp() {
		TConfig config = null;
		try {
			config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	/**
	 * ��ȡ�����ļ��еĵ��Ӳ���������IP
	 * 
	 * @return
	 */
	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "EMRIP");
		return url;
	}

	// $$=============== 2012/03/13Modified by lxҽ��У��
	// START=========================$$//
	/**
	 * ҽ��ҽ��У��
	 * 
	 * @param order_code
	 * @param ctzCode
	 * @return
	 */
	public boolean insOrderCheck(String orderCode, String ctzCode,
			String orderDesc) {
		boolean flg = true;
		// ����
		TParm parm = INSTJTool.getInstance().orderCheck(orderCode,
				this.getCtzCode(), "I", "1");
		if (parm.getErrCode() < 0 && parm.getErrCode() != -1) {
			if (messageBox("��ʾ��Ϣ Tips", orderDesc + parm.getErrText()
			+ "�Ƿ���? \n Are you Save?", this.YES_NO_OPTION) != 0) {
				return false;
			} else {
				return true;
			}
		}

		return flg;
	}

	public void onSubmitPDF() {
		this.openWindow("%ROOT%\\config\\ODI\\ODIDocQuery.x", (TParm) this
				.getParameter());
	}

	// $$=============== 2012/03/13Modified by lxҽ��У��
	// END=========================$$//

	// $$===========Add by lx �Ƿ��ֵ��ҽ��Start===============$$//
	public boolean isDutyDr() {
		boolean falg = false;
		final String sql = "SELECT * FROM ODI_DUTYDRLIST WHERE DEPT_CODE='"
				+ this.getDeptCode() + "' AND DR_CODE='" + Operator.getID()
				+ "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		// System.out.println("====sql===="+sql);
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;

	}

	/**
	 * �Ƿ�������ʹ��
	 * 
	 * @param freqCode
	 * @return
	 */
	private boolean isStat(String freqCode) {
		// this.messageBox("freqCode"+freqCode);
		final String sql = "SELECT STAT_FLG FROM SYS_PHAFREQ"
				+ " WHERE FREQ_CODE='" + freqCode + "'";
		// System.out.println("==sql=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		String f = parm.getValue("STAT_FLG", 0);
		if (f.equals("Y")) {
			return true;
		}
		return false;
	}

	// $$===========Add by lx �Ƿ��ֵ��ҽ��End=================$$//
	// /**
	// * ����HL7��Ϣ
	// * @param parm
	// */
	// public void sendHl7Messages(){
	// TParm parm=new TParm();
	// TDS ds = odiObject.getDS("ODI_ORDER");
	// String buffer = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
	// int newRow[] = ds.getNewRows(buffer);
	// int newCount = 0;
	// for (int i : newRow) {
	// if (!ds.isActive(i, buffer))
	// continue;
	// newCount++;
	// parm=ds.getRowParm(i, buffer);
	// parm.setData("ADM_TYPE", i, "I");
	// }
	// parm.setCount(newCount);
	// String type="NBW";
	// List list = new ArrayList();
	// list.add(parm);
	// // ���ýӿ�
	// TParm resultParm =
	// Hl7Communications.getInstance().Hl7MessageCIS(list,type);
	// if (resultParm.getErrCode() < 0){
	// this.messageBox(resultParm.getErrText());
	// }else{
	// this.messageBox("����HL7�ļ�");
	// }
	// }

	/**
	 * �ٴ�·��׼�� ================pangben 2012-6-7
	 */
	public void onClpManageM() {
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "MR_NO", mrNo);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPManagem.x", parm);
	}

	/**
	 * ������� ================pangben 2012-6-12
	 */
	public void onClpVariation() {
		// ========pangben 2012-06 -23���»���ٴ�·������
		String clncPathCode = this.getValueString("CLNCPATH_CODE");
		if (null == clncPathCode || clncPathCode.length() <= 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO='"
					+ caseNo + "'");
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sql.toString()));
			if (result.getCount("CLNCPATH_CODE") > 0) {
				clncPathCode = result.getValue("CLNCPATH_CODE", 0);
			}
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("��ִ��׼��·������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "CLNCPATH_CODE", clncPathCode);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPVariation.x", parm);
	}

	/**
	 * ����ʱ���޸�================xiongwg 2015-4-26
	 */
	public void onClpOrderReSchdCode() {
		String clncPathCode = this.getValueString("CLNCPATH_CODE");
		if (null == clncPathCode || clncPathCode.length() <= 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO='"
					+ caseNo + "'");
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sql.toString()));
			if (result.getCount("CLNCPATH_CODE") > 0) {
				clncPathCode = result.getValue("CLNCPATH_CODE", 0);
			}
		}
		if (clncPathCode.length() <= 0) {
			this.messageBox("��ִ��׼��·������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CLP", "CASE_NO", caseNo);
		parm.setData("CLP", "MR_NO", mrNo);
		parm.setData("CLP", "FLG", "Y");
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
	}

	/**
	 * ��鿹��ҩ���Ƿ���д�Ѿ���д������ʾ
	 * 
	 * @param obj
	 */
	public String checkAntiBioticWayisFilled(OdiObject obj) { // add by wanglong
		// 20121210
		String result = "";
		TDS ds = obj.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);// �¼ӵ�����
		// System.out.println("buff  ppppp���������� �ǣ���"+buff);
		for (int i : newRow) {
			// System.out.println("" +
			// "ds.isActive(i, buff)����"+i+ds.isActive(i, buff));
			if (!ds.isActive(i, buff))
				continue;
			String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String antibiotic_code = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			String antibiotic_way = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_WAY");
			// System.out.println("order_code order_code���������� �ǣ���"+order_code);
			if ((!antibiotic_code.equals("")) && antibiotic_way.equals("")) {
				result += ds.getRowParm(i, buff).getValue("ORDER_DESC") + ",";
				ds.deleteRow(i, buff);
			}
		}
		int modifRow[] = ds.getOnlyModifiedRows(buff);// �޸ĵ�����
		for (int i : modifRow) {
			if (!ds.isActive(i, buff))
				continue;
			String order_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String antibiotic_code = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			String antibiotic_way = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_WAY");
			if ((!antibiotic_code.equals("")) && antibiotic_way.equals("")) {
				result += ds.getRowParm(i, buff).getValue("ORDER_DESC") + ",";
				ds.deleteRow(i, buff);
			}
		}
		if (result.length() > 0)
			result = result.substring(0, result.length() - 1);
		return result;
	}

	/**
	 * Խ������
	 * 
	 * @param obj
	 *            yanjing 20140304
	 */
	public void isOverRideCheck(OdiObject obj) {
		String result = "";
		TDS ds = obj.getDS("ODI_ORDER");
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newRow[] = ds.getNewRows(buff);// �¼ӵ�����
		List<Integer> rowDelAttribute = new ArrayList<Integer>();
		Map rowDels = new HashMap();
		for (int i : newRow) {
			if (!ds.isActive(i, buff))
				continue;
			String iOrder_code = ds.getRowParm(i, buff).getValue("ORDER_CODE");
			String order_desc = ds.getRowParm(i, buff).getValue("ORDER_DESC");
			String linkMainFlg = ds.getRowParm(i, buff)
					.getValue("LINKMAIN_FLG");
			String linkNo = ds.getRowParm(i, buff).getValue("LINK_NO");
			String iAntiCode = ds.getRowParm(i, buff).getValue(
					"ANTIBIOTIC_CODE");
			if (linkMainFlg.equals("Y")) {// ����ʱ
				boolean antiFlg = false;// false:�������Ӧ��ϸ���в����п���ҩ��
				// ѭ��У��ÿ��ҽ�����Ƿ��п���ҩ��
				// for(int j : newRow){
				for (int m : newRow) {// ѭ��У��ϸ���Ƿ��п���ҩ��(�����������ͬ������)
					TParm meTemp = ds.getRowParm(m, buff);
					String mLinkNo = meTemp.getData("LINK_NO").toString();
					if (linkNo.equals(mLinkNo)
							&& (!"".equals(meTemp.getData("ANTIBIOTIC_CODE")
									.toString()) && !meTemp.getData(
											"ANTIBIOTIC_CODE").toString().equals(null))) {
						antiFlg = true;// ϸ���к��п���ҩ����
					}
				}
				if (antiFlg) {// ���������к��п���ҩ��
					for (int j : newRow) {// ѭ��У���Ƿ���֤��
						TParm reTemp = ds.getRowParm(j, buff);
						String LlinkNo = reTemp.getValue("LINK_NO");
						String antiCode = reTemp.getValue("ANTIBIOTIC_CODE");
						String order_code = reTemp.getValue("ORDER_CODE");
						if (LlinkNo.equals(linkNo)
								&& (!"".equals(antiCode) && !antiCode
										.equals(null))) {// ѭ���ж�ÿ��Ŀ���ҩ���Ƿ���֤��
							// ����order_code��ѯlcs_class_code
							String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
									+ order_code + "'";
							TParm lcsCodeParm = new TParm(this.getDBTool()
									.select(selLcsCode));
							String lcsString = lcsCodeParm.getValue(
									"LCS_CLASS_CODE", 0);

							// �õ�ʹ���ߵ�֤���б�
							TParm lcsParm = new TParm(
									this
									.getDBTool()
									.select(
											"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
													+ Operator.getID()
													+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
							if (!lcsString.equals("")
									&& !OrderUtil.getInstance()
									.checkLcsClassCode(lcsParm,
											Operator.getID(),
											"" + lcsString)) {

								TParm orderParm = ds.getRowParm(i, buff);
								// �Ƿ��Ǽ���ҽ��
								if (this.isOrderSet(orderParm)) {
									// this.messageBox_("ɾ������ҽ��ϸ��");
									// ɾ������ҽ��ϸ��
									this.delOrderSetList(orderParm, "NEW");
									// return;
								}
								// �Ƿ�������ҽ��
								if (this.isLinkOrder(orderParm)) {
									// this.messageBox_("ɾ������ҽ��");
									// ɾ������ҽ��
									// this.delLinkOrder(orderParm, "NEW");
									int newRowtemp[] = ds.getNewRows(buff);
									for (int n : newRowtemp) {
										TParm temp = ds.getRowParm(n, buff);
										// System.out.println("i:"+i);
										if (temp.getInt("LINK_NO") == orderParm
												.getInt("LINK_NO")
												&& temp
												.getValue("RX_KIND")
												.equals(
														orderParm
														.getValue("RX_KIND"))) {
											int delId = (Integer) ds
													.getItemData(i, "#ID#",
															buff);
											rowDelAttribute.add(delId);
											rowDels.put(delId, i);
										}
									}
								}
							}
						}

					}
				}
				// }
			} else if (("".equals(linkNo) || linkNo.equals(null))
					&& (!"".equals(iAntiCode) && !iAntiCode.equals(null))) {// ��odi_order����д����ʱ��ɾ��������û��Ȩ�޵Ŀ���ҩ��
				// ѭ���ж�ÿ��Ŀ���ҩ���Ƿ���֤��
				// ����order_code��ѯlcs_class_code
				// this.messageBox("0000000000000");
				String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
						+ iOrder_code + "'";
				TParm lcsCodeParm = new TParm(this.getDBTool().select(
						selLcsCode));
				String lcsString = lcsCodeParm.getValue("LCS_CLASS_CODE", 0);

				// �õ�ʹ���ߵ�֤���б�
				TParm lcsParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
										+ Operator.getID()
										+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
				if (!lcsString.equals("")
						&& !OrderUtil.getInstance().checkLcsClassCode(lcsParm,
								Operator.getID(), "" + lcsString)) {
					TParm orderParm = ds.getRowParm(i, buff);
					// �Ƿ��Ǽ���ҽ��
					if (this.isOrderSet(orderParm)) {
						// this.messageBox_("ɾ������ҽ��ϸ��");
						// ɾ������ҽ��ϸ��
						this.delOrderSetList(orderParm, "NEW");
						// return;
					}
					int delId = (Integer) ds.getItemData(i, "#ID#", buff);
					rowDelAttribute.add(delId);
					rowDels.put(delId, i);
				}
			}
		}

		for (int j = rowDelAttribute.size() - 1; j >= 0; j--) {
			TParm parm = ds.getRowParm(rowDelAttribute.get(j), buff);
			String orderDesc = parm.getValue("ORDER_DESC");
			this.messageBox("��û��" + orderDesc + "֤��,��ɾ����");
			ds.setAttribute("DELROW", rowDelAttribute.get(j));
			odiObject.deleteRow(ds, (Integer) rowDels.get(rowDelAttribute
					.get(j)));
		}
	}

	/**
	 * ���ҽ���Ƿ���Ȩ��,����TParm
	 * 
	 * @param obj
	 *            yanjing 20140305
	 */
	public TParm reCheckAnti(TParm parm) {
		// System.out.println("�������parm ��ֵ parm parm parm  is������"+parm);
		TParm result = new TParm();
		int rowCount = 0;
		String sktSql = "";
		TParm sktParm = null;
		for (int i = 0; i < parm.getCount(); i++) {
			// ���У��Ƥ��������ʾ
			sktSql = "SELECT SKINTEST_NOTE FROM PHA_ANTI WHERE CASE_NO= '"
					+ caseNo + "' AND ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "' "
					+ " AND ROUTE_CODE = 'PS'  AND SKINTEST_NOTE IS NOT NULL ";
			sktParm = new TParm(TJDODBTool.getInstance().select(sktSql));
			if (sktParm.getCount() > 0) {
				if (sktParm.getValue("SKINTEST_NOTE", 0).equals("1")) {
					if (this.messageBox("��ʾ", "��ҩƷƤ�Խ��Ϊ����,�Ƿ����", 2) != 0) {
						result.setErr(-1, "ȡ������");
						return result;
					}
				}
			}
		}
		// ���ҽ��֤�յ�У��
		for (int i = 0; i < parm.getCount(); i++) {
			String linkNo = parm.getValue("LINK_NO", i);
			String anti = parm.getValue("ANTIBIOTIC_CODE", i);
			if (parm.getData("LINKMAIN_FLG", i).equals("Y")) {// ҽ��Ϊ����ʱ
				boolean onSaveFlg = false;// ͬһ�������ŵķǿ���ҩ���Ƿ񱣴����false��Ϊδ�������
				if ("".equals(parm.getValue("DC_DATE", i))
						|| parm.getValue("DC_DATE", i).equals(null)) {
					// this.messageBox("���������ͣ��ʱ�䲻��Ϊ�ա�");
					result.setErr(-1, "���������ͣ��ʱ�䲻��Ϊ�ա�");
					return result;

				}
				for (int m = 0; m < parm.getCount(); m++) {// ѭ��У����������ͬ��ҽ��
					String dLinkNo = parm.getData("LINK_NO", m).toString();
					String antiCode = parm.getData("ANTIBIOTIC_CODE", m)
							.toString();
					if (linkNo.equals(dLinkNo)
							&& (!"".equals(antiCode) && !antiCode.equals(null))) {// ��������ͬ����Ϊ����ҩ��ʱУ�鿹��ҩ���֤��
						String orderCode = parm.getValue("ORDER_CODE", m);
						// ����order_code��ѯlcs_class_code
						String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
								+ orderCode + "'";
						TParm lcsCodeParm = new TParm(TJDODBTool.getInstance()
								.select(selLcsCode));
						String lcsString = lcsCodeParm.getValue(
								"LCS_CLASS_CODE", 0);
						// �õ�ʹ���ߵ�֤���б�
						TParm lcsParm = new TParm(
								TJDODBTool
								.getInstance()
								.select(
										"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
												+ Operator.getID()
												+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
						if (!lcsString.equals("")
								&& !OrderUtil.getInstance().checkLcsClassCode(
										lcsParm, Operator.getID(),
										"" + lcsString)) {
							lcsParm.setErrCode(-1);
							// ��û�д�ҽ����֤�գ�
							lcsParm.setErrText("E0166");
							String orderDesc = parm.getData("ORDER_DESC", m)
									.toString();
							if (this
									.messageBox("��ʾ��Ϣ Tips", "û��Ȩ�޿���"
											+ orderDesc + ",�Ƿ�ԽȨ? ",
											this.YES_NO_OPTION) != 0) {// �񣬲�Խ��,����parm���������
							} else {// ��parm���������
								for (int j = 0; j < parm.getCount(); j++) {
									String rLinkNo = parm.getData("LINK_NO", j)
											.toString();
									String rAntiCode = parm.getData(
											"ANTIBIOTIC_CODE", j).toString();
									if (!onSaveFlg
											&& linkNo.equals(rLinkNo)
											&& (rAntiCode.equals(null) || ""
													.equals(rAntiCode))) {// �������еķǿ���ҩ��д��pha_anti��
										result.addData("LINKMAIN_FLG", parm
												.getData("LINKMAIN_FLG", j));// yanjing
										// �������
										// 20140310
										result.addData("LINK_NO", parm.getData(
												"LINK_NO", j));// yanjing ��ű��
										// 20140310
										result.addData("ANTI_FLG", "N");// YANJING
										// 20140619
										// �������
										result.addData("OVERRIDE_FLG", "Y");// YANJING
										// 20140311
										// �Ƿ�Խ�������ı��
										result.addData("TAKE_DAYS", parm
												.getInt("TAKE_DAYS", j));
										result.addData("ANTI_TAKE_DAYS", parm
												.getInt("TAKE_DAYS", j));
										result.addData("ORDER_DATE", parm
												.getData("ORDER_DATE", j));
										result.addData("ORDER_CODE", parm
												.getData("ORDER_CODE", j));
										result.addData("ORDER_DESC", parm
												.getData("ORDER_DESC", j));
										result.addData("MR_CODE", parm.getData(
												"MR_CODE", j));
										result.addData("OPTITEM_CODE", parm
												.getData("OPTITEM_CODE", j));
										result.addData("REQUEST_NO", parm
												.getData("REQUEST_NO"));
										result.addData("SPECIFICATION", parm
												.getData("SPECIFICATION", j));// ���
										result.addData("MEDI_UNIT", parm
												.getData("MEDI_UNIT", j));// ��ҩ��λ
										result.addData("MEDI_QTY", parm
												.getData("MEDI_QTY", j));// ��ҩ��
										result.addData("FREQ_CODE", parm
												.getData("FREQ_CODE", j));// Ƶ��
										result.addData("ROUTE_CODE", parm
												.getData("ROUTE_CODE", j));// �÷�
										rowCount++;
									}
								}
							}
						} else {
							onSaveFlg = true;// ��Ǹ����еķǿ���ҩ���Ѿ�����
							// �������ҩ��д��pha_anti����
							result.addData("LINKMAIN_FLG", parm.getData(
									"LINKMAIN_FLG", m));// yanjing ������� 20140310
							result.addData("LINK_NO", parm
									.getData("LINK_NO", m));// yanjing ��ű��
							// 20140310
							result.addData("ANTI_FLG", "Y");// YANJING 20140619
							// �������
							result.addData("OVERRIDE_FLG", "Y");// YANJING
							// 20140311
							// �Ƿ�Խ�������ı��
							result.addData("TAKE_DAYS", parm.getInt(
									"TAKE_DAYS", m));
							result.addData("ANTI_TAKE_DAYS", parm.getInt(
									"TAKE_DAYS", m));
							result.addData("ORDER_DATE", parm.getData(
									"ORDER_DATE", m));
							result.addData("ORDER_CODE", parm.getData(
									"ORDER_CODE", m));
							result.addData("ORDER_DESC", parm.getData(
									"ORDER_DESC", m));
							result.addData("MR_CODE", parm
									.getData("MR_CODE", m));
							result.addData("OPTITEM_CODE", parm.getData(
									"OPTITEM_CODE", m));
							result.addData("REQUEST_NO", parm
									.getData("REQUEST_NO"));
							result.addData("SPECIFICATION", parm.getData(
									"SPECIFICATION", m));// ���
							result.addData("MEDI_UNIT", parm.getData(
									"MEDI_UNIT", m));// ��ҩ��λ
							result.addData("MEDI_QTY", parm.getData("MEDI_QTY",
									m));// ��ҩ��
							result.addData("FREQ_CODE", parm.getData(
									"FREQ_CODE", m));// Ƶ��
							result.addData("ROUTE_CODE", parm.getData(
									"ROUTE_CODE", m));// �÷�
							rowCount++;
						}
					}
				}
			} else if ("".equals(linkNo) && !"".equals(anti)) {// ����������Ŀ���ҩ��
				String orderCode = parm.getValue("ORDER_CODE", i);
				// ����order_code��ѯlcs_class_code
				String selLcsCode = "SELECT LCS_CLASS_CODE FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "'";
				TParm lcsCodeParm = new TParm(TJDODBTool.getInstance().select(
						selLcsCode));
				String lcsString = lcsCodeParm.getValue("LCS_CLASS_CODE", 0);
				// �õ�ʹ���ߵ�֤���б�
				TParm lcsParm = new TParm(
						TJDODBTool
						.getInstance()
						.select(
								"SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
										+ Operator.getID()
										+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
				if (!lcsString.equals("")
						&& !OrderUtil.getInstance().checkLcsClassCode(lcsParm,
								Operator.getID(), "" + lcsString)) {
					lcsParm.setErrCode(-1);
					// ��û�д�ҽ����֤�գ�
					lcsParm.setErrText("E0166");
					String orderDesc = parm.getData("ORDER_DESC", i).toString();
					if (this.messageBox("��ʾ��Ϣ Tips", "û��Ȩ�޿���" + orderDesc
							+ ",�Ƿ�ԽȨ? ", this.YES_NO_OPTION) != 0) {// �񣬲�Խ��,����parm���������
					} else {
						result.addData("LINKMAIN_FLG", parm.getData(
								"LINKMAIN_FLG", i));// yanjing ������� 20140310
						result.addData("LINK_NO", parm.getData("LINK_NO", i));// yanjing
						// ��ű��
						// 20140310
						result.addData("ANTI_FLG", "Y");// YANJING 20140619 �������
						result.addData("OVERRIDE_FLG", "Y");// YANJING 20140311
						// �Ƿ�Խ�������ı��
						result
						.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS",
								i));
						result.addData("ANTI_TAKE_DAYS", parm.getInt(
								"TAKE_DAYS", i));
						result.addData("ORDER_DATE", parm.getData("ORDER_DATE",
								i));
						result.addData("ORDER_CODE", parm.getData("ORDER_CODE",
								i));
						result.addData("ORDER_DESC", parm.getData("ORDER_DESC",
								i));
						result.addData("MR_CODE", parm.getData("MR_CODE", i));
						result.addData("OPTITEM_CODE", parm.getData(
								"OPTITEM_CODE", i));
						result
						.addData("REQUEST_NO", parm
								.getData("REQUEST_NO"));
						result.addData("SPECIFICATION", parm.getData(
								"SPECIFICATION", i));// ���
						result.addData("MEDI_UNIT", parm
								.getData("MEDI_UNIT", i));// ��ҩ��λ
						result.addData("MEDI_QTY", parm.getData("MEDI_QTY", i));// ��ҩ��
						result.addData("FREQ_CODE", parm
								.getData("FREQ_CODE", i));// Ƶ��
						result.addData("ROUTE_CODE", parm.getData("ROUTE_CODE",
								i));// �÷�
						rowCount++;
					}
				} else {
					result.addData("LINKMAIN_FLG", parm.getData("LINKMAIN_FLG",
							i));// yanjing ������� 20140310
					result.addData("LINK_NO", parm.getData("LINK_NO", i));// yanjing
					// ��ű��
					// 20140310
					result.addData("ANTI_FLG", "Y");// YANJING 20140619 �������
					result.addData("OVERRIDE_FLG", "Y");// YANJING 20140311
					// �Ƿ�Խ�������ı��
					result.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS", i));
					result.addData("ANTI_TAKE_DAYS", parm
							.getInt("TAKE_DAYS", i));
					result.addData("ORDER_DATE", parm.getData("ORDER_DATE", i));
					result.addData("ORDER_CODE", parm.getData("ORDER_CODE", i));
					result.addData("ORDER_DESC", parm.getData("ORDER_DESC", i));
					result.addData("MR_CODE", parm.getData("MR_CODE", i));
					result.addData("OPTITEM_CODE", parm.getData("OPTITEM_CODE",
							i));
					result.addData("REQUEST_NO", parm.getData("REQUEST_NO"));
					result.addData("SPECIFICATION", parm.getData(
							"SPECIFICATION", i));// ���
					result.addData("MEDI_UNIT", parm.getData("MEDI_UNIT", i));// ��ҩ��λ
					result.addData("MEDI_QTY", parm.getData("MEDI_QTY", i));// ��ҩ��
					result.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));// Ƶ��
					result.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));// �÷�
					rowCount++;
				}
			}
		}
		result.setData("ACTION", "COUNT", rowCount);
		return result;
	}

	/**
	 * ���ϲ�����ť�¼�
	 */
	public void onMerge() {// add by wanglong 20121025
		TParm param = new TParm();
		param.setData("MR_NO", getMrNo());
		param.setData("CASE_NO", getCaseNo());
		Object obj = this.openDialog("%ROOT%\\config\\clp\\CLPPatInfoUI.x",
				param);
		if (obj != null) {
			TParm acceptParm = (TParm) obj;
			String caseNo_old = acceptParm.getValue("CASE_NO");
			String diseCode = acceptParm.getValue("DISE_CODE");
			if (!caseNo_old.trim().equals("")) {
				TParm action = new TParm();
				action.setData("CASE_NO", caseNo);
				action.setData("CASE_NO_OLD", caseNo_old);
				// ��������ʷ�����ϲ�
				TParm result = TIOM_AppServer.executeAction(
						"action.clp.CLPSingleDiseAction", "mergeEMRhistory",
						action);
				if (result.getErrCode() < 0) {
					this.messageBox("�ϲ�ʧ�ܣ�" + result.getErrText());
				} else {
					this.messageBox("�ϲ��ɹ�");
				}
			}
		}
	}

	/**
	 * ������׼��
	 */
	public void onSingleDise() {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("ADM_TYPE", "I");// סԺ
		// System.out.println("============������==============="+caseNo);
		action.setData("CASE_NO", caseNo);// �����
		this.openWindow("%ROOT%\\config\\clp\\CLPSingleDise.x",// �������������ѯCASE_NO
				action);
	}

	/**
	 * ������ҩ--ҩƷ��Ϣ��ѯ
	 */
	public void onQueryRationalDrugUse() {// add by wanglong 20130522
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		int tabbedIndex = ((TTabbedPane) this.getComponent("TABLEPANE"))
				.getSelectedIndex();
		int row = -1;
		String orderCode = "";
		switch (tabbedIndex) {
		case 0:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 1:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 2:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		case 3:
			row = this.getTTable("TABLE" + (tabbedIndex + 1)).getSelectedRow();
			orderCode = this.getTTable("TABLE" + (tabbedIndex + 1))
					.getDataStore().getItemString(row, "ORDER_CODE");
			break;
		}
		if (row < 0) {
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
		if (value == null || value.length() == 0) {
			return;
		}
		int conmmand = Integer.parseInt(value);
		if (conmmand != 6) {
			PassTool.getInstance().setQueryDrug(orderCode, conmmand);
		} else {
			PassTool.getInstance().setWarnDrug2("", "");
		}
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		final TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (getRadioSelType(tab.getSelectedIndex()) != 5)
					return;
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
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
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);

				// getTMenuItem("save").setEnabled(false);
			}
		});
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
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, TTable table) {
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
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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

	// =================== chenxi modify ȥ����
	private TParm getBedIpdNo(String caseNo) {
		TParm result = new TParm();
		String sql = "SELECT BED_NO,IPD_NO,BED_NO_DESC FROM  SYS_BED WHERE CASE_NO = '"
				+ caseNo + "' AND BED_OCCU_FLG = 'N' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0) {
			result.setData("BED_NO", "");
			result.setData("IPD_NO", "");
		} else {
			result.setData("BED_NO", parm.getValue("BED_NO", 0));
			result.setData("IPD_NO", parm.getValue("IPD_NO", 0));
		}
		return result;
	}

	/**
	 * ��񵥻��¼� yanjing 20131119 ���״̬����ʾ
	 */
	public void onTableClick(String tag) {
		TTable table = (TTable) this.getComponent(tag);
		int row = table.getSelectedRow();
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm dataparm = odiObject.getDS("ODI_ORDER").getBuffer(ds.PRIMARY);
		String orderCode = dataparm.getValue("ORDER_CODE", row);
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE", 0) + " "
				+ sqlparm.getValue("ORDER_DESC", 0) + " "
				+ sqlparm.getValue("GOODS_DESC", 0) + " "
				+ sqlparm.getValue("DESCRIPTION", 0) + " "
				+ sqlparm.getValue("SPECIFICATION", 0) + " "
				+ sqlparm.getValue("REMARK_1", 0) + " "
				+ sqlparm.getValue("REMARK_2", 0) + " "
				+ sqlparm.getValue("DRUG_NOTES_DR", 0));
	}

	/**
	 * ����ҩ��ȡ������ʱ�����ͣ��ʱ�䡢ͣ��ҽ����ͣ�ÿ��Ҽ��������� yanjing 20140217
	 */
	private void onClearLinkAnti(String orderCode, TParm parm) {
		// ����ҽ�������ѯ��ҽ���Ƿ�Ϊ����ҩ��
		String selectAnti = "SELECT ANTIBIOTIC_CODE FROM PHA_BASE "
				+ "WHERE ORDER_CODE = '" + orderCode + "'";
		TParm antiParm = new TParm(TJDODBTool.getInstance().select(selectAnti));
		if ("".equals(antiParm.getValue("ANTIBIOTIC_CODE", 0))
				|| antiParm.getValue("ANTIBIOTIC_CODE", 0).equals(null)) {
			parm.setData("DC_DATE", "");
			parm.setData("ANTIBIOTIC_WAY", "");
			parm.setData("DC_DR_CODE", "");
			parm.setData("DC_DEPT_CODE", "");// ====yanjing 20140901 ͣ�ÿ���
		}
	}

	public void onCdssCal() {
		odiStationDrools.onCdssCal(false);
	}

	// add by yangjj 20150410 �ж��Ƿ�Ϊ��Ѫҽ��������Ѫҽ��Ϊtrue������Ϊfalse
	private boolean checkBMSOrder(String orderCode) {
		String sql = getCheckBMSOrderSql(orderCode);
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		boolean b = false;
		if (!"".equals(sqlParm.getValue("EXEC_PRG", 0))) {
			b = true;
		}
		return b;
	}

	private String getCheckBMSOrderSql(String orderCode) {
		String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode
				+ "'";
		return sql;
	}

	private String getEXECPRGSql(String orderCode) {
		String sql = "SELECT EXEC_PRG FROM SYS_FEE WHERE ORDER_CODE = '"
				+ orderCode + "'";
		return sql;
	}

	/**
	 * ҩƷ����ѡ�� add by huangtt 20150507
	 */
	public void selPhaClass() {
		TComboBox type = (TComboBox) this.getComponent("PHA_CLASS");
		if (TCM_Transform.getBoolean(this.getValue("PHA_DRUGALLERGY"))) {
			type.setVisible(true);
			setValue("PHA_CLASS", "B");
			// onAllg(this.getValueString("PHA_CLASS"));
			onSelectRadio(1);
		} else {
			setValue("PHA_CLASS", "");
			type.setVisible(false);
		}
	}

	/**
	 * ҩƷ�����еķ���ѡ�� add by huangtt 20150507
	 */
	public void onSelg() {
		onSelectRadio(1);
	}

	/**
	 * �����ӡ
	 */
	public void onMedApplyPrint() {// wanglong add 20150520
		TParm outsideParm = this.getInputParm();
		if (outsideParm == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		parm.setData("MR_NO", outsideParm.getValue("ODI", "MR_NO"));
		parm.setData("CASE_NO", outsideParm.getValue("ODI", "CASE_NO"));
		parm.setData("IPD_NO", outsideParm.getValue("ODI", "IPD_NO"));
		parm.setData("PAT_NAME", outsideParm.getValue("ODI", "PAT_NAME"));
		parm.setData("BED_NO", outsideParm.getValue("ODI", "BED_NO"));
		parm.setData("DEPT_CODE", outsideParm.getValue("ODI", "DEPT_CODE"));
		parm.setData("STATION_CODE", outsideParm
				.getValue("ODI", "STATION_CODE"));
		parm.setData("ADM_DATE", outsideParm.getData("ODI", "ADM_DATE"));
		parm.setData("POPEDEM", "1");
		this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
	}

	/**
	 * 
	 * @Title: onClpPack
	 * @Description: TODO(�ٴ�·���ײ�)
	 * @author pangben 2015-8-13
	 * @throws
	 */
	public void onClpPack() {
		TParm parm = new TParm();
		String clncPathCode = "";
		StringBuffer sql = new StringBuffer();
		sql
		.append("SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = result.getValue("CLNCPATH_CODE", 0);
		}
		// if (clncPathCode.length() <= 0) {
		// this.messageBox("�����·��׼��");
		// return;
		// }
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			this.messageBox("E0178");
			return;
		}
		String tag = "";
		switch (tab.getSelectedIndex()) {
		// ��ʱ0������Ƽۡ�1����ҩ��2������ҩƷ��3����ҩ��Ƭ��4��������Ŀ��5��������
		case 0:
			parm.setData("RX_TYPE", "1,2,4,5");
			tag = "ST";
			break;
			// ����
		case 1:
			parm.setData("RX_TYPE", "1,2,4");
			tag = "UD";
			break;
			// ��Ժ��ҩ
		case 2:
			parm.setData("RX_TYPE", "1,2,3");
			tag = "DS";
			break;
			// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			tag = "IG";
			break;
		}
		TParm inParm = new TParm();
		// �õ���ǰʱ��
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("SELECT_INDEX", tab.getSelectedIndex());
		inParm.setData("CLNCPATH_CODE", clncPathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("MR_NO", mrNo);
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPPackAgeOrder.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		yyList = true;
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
					result.getValue("ORDER_CODE", i),
					result.getValue("CLNCPATH_CODE", i),
					result.getValue("SCHD_CODE", i),
					result.getValue("CHKTYPE_CODE", i),
					result.getValue("ORDER_SEQ_NO", i),
					result.getValue("ORDER_TYPE", i));
			OrderParm.setData("CLP_FLG", "Y");
			OrderParm.setData("EXID_ROW", getExitRow());
			this.popReturn(tag, OrderParm);
		}
		yyList = false;
	}

	/**
	 * ԺǰTABLE�༭ʱ����Ӧ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentPA(Component com, int row, int column) {
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// ��ǰ�༭��
		this.rowOnly = row;
		// �õ�����
		String columnName = this.getFactColumnName(TABLE6, column);
		if (!"ORDER_DESCCHN".equals(columnName)) {
			return;
		}
		TTable table = getTTable(TABLE6);
		int selRow = this.getTTable(TABLE6).getSelectedRow();
		TParm existParm = this.getTTable(TABLE6).getDataStore().getRowParm(
				selRow);
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();

		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("PA", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ����Ժǰ��ҩҽ�������
	 * 
	 * @return TParm
	 */
	private TParm returnPAData(TParm parm, int row) {
		TParm result = new TParm();
		// �õ������Ϣ
		TParm actionParm = this.getPhaBaseData(parm.getData("ORDER_CODE")
				.toString(), parm.getData("CAT1_TYPE").toString(), "PA", parm);
		// �������
		result.setData("REGION_CODE", Operator.getRegion());
		// ��������
		result.setData("STATION_CODE", this.getStationCode());
		// ����
		result.setData("DEPT_CODE", this.getDeptCode());
		// ����ҽʦ
		result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getData(
				"ODI", "VS_DR_CODE").toString());
		// ��λ��
		result.setData("BED_NO", this.getBedIpdNo(caseNo).getValue("BED_NO"));
		// סԺ��
		result.setData("IPD_NO", this.getBedIpdNo(caseNo).getValue("IPD_NO"));
		// ������
		result.setData("MR_NO", ((TParm) this.getParameter()).getData("ODI",
				"MR_NO").toString());
		// �ݴ�ע��
		result.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		result.setData("ORDER_STATE", "N");
		// ����ҽ������
		result.setData("LINKMAIN_FLG", "N");
		// ����ҽ��
		result.setData("LINK_NO", "");
		// ҽ������
		result.setData("ORDER_CODE", parm.getData("ORDER_CODE"));
		// ҽ������
		result.setData("ORDER_DESC", parm.getData("ORDER_DESC").toString());
		// ҽ�����ƣ���ʾ��
		result.setData("ORDER_DESCCHN", parm.getValue("ORDER_DESC")
				+ parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION")
				+ parm.getValue("SPECIFICATION"));
		// ��Ʒ��
		result.setData("GOODS_DESC", parm.getData("GOODS_DESC"));
		// ���
		result.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		// Ӣ������
		result.setData("ORDER_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
		// ��ҩ����
		result.setData("MEDI_QTY", actionParm.getData("MEDI_QTY"));
		// ��ҩ��λ
		result.setData("MEDI_UNIT", actionParm.getData("MEDI_UNIT"));
		// Ƶ�δ���
		result.setData("FREQ_CODE", actionParm.getData("FREQ_CODE"));
		// ͣ��ҽ��
		result.setData("DC_DR_CODE", "");
		// ͣ��ִ�п���
		result.setData("DC_DEPT_CODE", "");
		// �÷�
		result.setData("ROUTE_CODE", actionParm.getData("ROUTE_CODE"));
		// ����
		result.setData("TAKE_DAYS", 0);
		// ��ҩ����
		result.setData("DOSAGE_QTY", 0);
		// ��ҩ��λ
		result.setData("DOSAGE_UNIT", "");
		// ��ҩ����
		result.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		result.setData("DISPENSE_UNIT", "");
		// �з�ҩע��
		result.setData("GIVEBOX_FLG", "N");
		// ����ע��
		result.setData("CONTINUOUS_FLG", "N");
		// ������
		result.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		result.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������
		result.setData("EFF_DATE", "");
		// ��������
		result.setData("ORDER_DEPT_CODE", "");
		// ����ҽʦ(���ݿⲻ��Ϊ��)
		result.setData("ORDER_DR_CODE", Operator.getID());
		// ִ�п���
		result.setData("EXEC_DEPT_CODE", "");
		// ִ�м�ʦ
		result.setData("EXEC_DR_CODE", "");
		// ͣ��ԭ�����
		result.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		result.setData("DR_NOTE", "");
		// ��ʿ��ע
		result.setData("NS_NOTE", "");
		// �������
		result.setData("INSPAY_TYPE", parm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("CTRLDRUGCLASS_CODE", actionParm
					.getData("CTRLDRUGCLASS_CODE"));
		} else {
			result.setData("CTRLDRUGCLASS_CODE", "");
		}
		// �����ش���
		result
		.setData("ANTIBIOTIC_CODE", actionParm
				.getData("ANTIBIOTIC_CODE"));
		// ����ǩ��(��ҩʹ��)
		result.setData("RX_NO", "");
		// ����(��ҩʹ��)
		result.setData("PRESRT_NO", 0);
		// ҩƷ����
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("PHA_TYPE", actionParm.getData("PHA_TYPE"));
		} else {
			result.setData("PHA_TYPE", "");
		}
		// �������
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			result.setData("DOSE_TYPE", actionParm.getData("DOSE_CODE"));
		} else {
			result.setData("DOSE_TYPE", "");
		}
		// ��Ƭ������
		result.setData("DCT_TAKE_QTY", 0);
		// ��ҩ��ʽ(��ҩ����)
		result.setData("DCTAGENT_CODE", "");
		// ���������(��ҩ����)
		result.setData("PACKAGE_AMT", 0);
		// ����ҽ������ע��
		result.setData("SETMAIN_FLG", "");
		// ��˻�ʿ
		result.setData("NS_CHECK_CODE", "");
		// ��������ע��
		result.setData("INDV_FLG", "");
		// ����ע��
		result.setData("HIDE_FLG", "N");
		// ����ҽ��˳���
		result.setData("ORDERSET_GROUP_NO", 0);
		// ����ҽ���������
		result.setData("ORDERSET_CODE", "");
		// ҽ��ϸ����
		result.setData("ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		result.setData("CAT1_TYPE", parm.getData("CAT1_TYPE"));
		// �������
		result.setData("RPTTYPE_CODE", "");
		// ������
		result.setData("OPTITEM_CODE", "");
		// ���뵥���
		result.setData("MR_CODE", "");
		// FILE_NO
		result.setData("FILE_NO", "");
		// ��Ч����
		result.setData("DEGREE_CODE", "");
		// ��ʿ���ʱ��
		result.setData("NS_CHECK_DATE", "");
		// ��˻�ʿDCȷ��
		result.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		result.setData("DC_NS_CHECK_DATE", "");
		// �����ҩ����
		result.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		result.setData("FRST_QTY", 0);
		// ���ҩʦ
		result.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		result.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		result.setData("INJ_ORG_CODE", "");
		// ����
		result.setData("URGENT_FLG", "");
		// �ۼƿ�ҩ��
		result.setData("ACUMMEDI_QTY", 0);
		// �ײ�����ʱ��
		result.setData("START_DTTM", "");
		// ҽ��ִ��ʱ��(����)
		result.setData("ORDER_DATETIME", "");
		// ҽ������
		result.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// ��ҩע��
		result.setData("DISPENSE_FLG", "");
		// ҽ����ע,ҩƷ��ע
		result.setData("IS_REMARK", parm.getData("IS_REMARK"));
		// ȡҩ����
		result.setData("TAKEMED_ORG", "");
		result.setData("OPT_USER", Operator.getID());
		Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
		result.setData("OPT_DATE", StringTool.getString(sysdate,
				"yyyyMMddHHmmss"));
		result.setData("OPT_TERM", Operator.getIP());
		// ��ҩ��ʼʱ��
		if (row == 0) {
			result.setData("ORDER_DATE", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("ORDER_DATE", tempParm.getData("ORDER_DATE"));
		}
		// ��ҩ����ʱ��
		if (row == 0) {
			result.setData("DC_DATE", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("DC_DATE", tempParm.getData("DC_DATE"));
		}
		// ��ҩ������
		if (row == 0) {
			result.setData("MED_REPRESENTOR", "");
		} else {
			TParm tempParm = this.getTTable(TABLE6).getDataStore().getRowParm(
					row - 1);
			result.setData("MED_REPRESENTOR", tempParm
					.getData("MED_REPRESENTOR"));
		}
		return result;
	}

	/**
	 * ����Ժǰ��ҩ
	 * 
	 * @param tab
	 *            TTable
	 */
	private boolean savePAData() {
		TParm parm = new TParm();
		boolean flag = true;
		String orderNo = "";
		TDataStore tds = this.getTTable(TABLE6).getDataStore();
		String[] sql = tds.getUpdateSQL();
		TParm delParm = tds.getBuffer(tds.DELETE);
		TParm modifyParm = tds.getBuffer(tds.MODIFY);

		if (null != sql) {
			int newrows[] = tds.getNewRows(tds.PRIMARY);
			for (int i = 0; i < newrows.length; i++) {
				if (sql[i].contains("INSERT")) {
					if (StringUtils.isEmpty(orderNo)) {
						orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
								"ORDER_NO", "ORDER_NO");
					}
					tds.setItem(newrows[i], "EFF_DATE", tds.getItemString(
							newrows[i], "ORDER_DATE").replaceAll("-", "/")
							.replaceAll(" 00:00:00.0", ""));
					tds.setItem(newrows[i], "ORDER_NO", orderNo);
					tds.setItem(newrows[i], "ORDER_SEQ", i + 1);
				}
			}

			// ����
			flag = this.getTTable(TABLE6).getDataStore().update();
			if (!flag) {
				return flag;
			}
		}

		// ɾ��
		if (delParm.getCount() > 0) {
			String delSql = "";
			TParm delResult = new TParm();
			for (int i = 0; i < delParm.getCount(); i++) {
				delSql = "DELETE FROM ODI_ORDER WHERE CASE_NO = '#' AND ORDER_NO = '#' AND ORDER_SEQ = # AND RX_KIND = 'PA' ";
				delSql = delSql.replaceFirst("#",
						delParm.getValue("CASE_NO", i)).replaceFirst("#",
								delParm.getValue("ORDER_NO", i)).replaceFirst("#",
										delParm.getValue("ORDER_SEQ", i));
				delResult = new TParm(TJDODBTool.getInstance().update(delSql));

				if (delResult.getErrCode() < 0) {
					err("ERR:" + delResult.getErrText());
					return false;
				}
			}
		}

		// �޸�
		if (modifyParm.getCount() > 0) {
			int modRows[] = tds.getModifiedRows();
			String delSql = "";
			TParm delResult = new TParm();
			String userId = Operator.getID();
			String userIp = Operator.getIP();

			for (int i = 0; i < modRows.length; i++) {
				if (StringUtils.isEmpty(modifyParm.getValue("CASE_NO", i))) {
					continue;
				}
				delSql = "UPDATE ODI_ORDER SET DR_NOTE='#',ORDER_DATE=TO_DATE('#','YYYY/MM/DD'),"
						+ "DC_DATE=TO_DATE('#','YYYY/MM/DD'),MED_REPRESENTOR='#',MEDI_QTY='#',MEDI_UNIT='#',"
						+ "FREQ_CODE='#',EFF_DATE=TO_DATE('#','YYYY/MM/DD'),OPT_USER='"
						+ userId
						+ "',OPT_DATE=SYSDATE,OPT_TERM='"
						+ userIp
						+ "' WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=# AND RX_KIND='PA' ";
				delSql = delSql.replaceFirst("#",
						tds.getItemString(modRows[i], "DR_NOTE")).replaceFirst(
								"#",
								tds.getItemString(modRows[i], "ORDER_DATE").replaceAll(
										" 00:00:00.0", "")).replaceFirst(
												"#",
												tds.getItemString(modRows[i], "DC_DATE").replaceAll(
														" 00:00:00.0", "")).replaceFirst("#",
																tds.getItemString(modRows[i], "MED_REPRESENTOR"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "MEDI_QTY"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "MEDI_UNIT"))
						.replaceFirst("#",
								tds.getItemString(modRows[i], "FREQ_CODE"))
						.replaceFirst(
								"#",
								tds.getItemString(modRows[i], "ORDER_DATE")
								.replaceAll(" 00:00:00.0", ""))
						.replaceFirst("#", modifyParm.getValue("CASE_NO", i))
						.replaceFirst("#", modifyParm.getValue("ORDER_NO", i))
						.replaceFirst("#", modifyParm.getValue("ORDER_SEQ", i));

				delResult = new TParm(TJDODBTool.getInstance().update(delSql));

				if (delResult.getErrCode() < 0) {
					err("ERR:" + delResult.getErrText());
					return false;
				}
			}
		}

		return flag;
	}

	/**
	 * ���Ժǰ��ҩ
	 */
	public TParm checkPADataSave() {
		TParm parm = new TParm();
		TTable table = this.getTTable(TABLE6);
		TDataStore tds = table.getDataStore();
		int rowCount = tds.rowCount();

		for (int i = 0; i < rowCount - 1; i++) {
			if (StringUtils.isEmpty(table.getItemString(i, "ORDER_DATE"))) {
				parm.setErr(-1, "��ҩ��ʼʱ�䲻��Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 2);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "DC_DATE"))) {
				parm.setErr(-1, "��ҩ����ʱ�䲻��Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 3);
				return parm;
			}
			if (table.getItemTimestamp(i, "ORDER_DATE").getTime() > table
					.getItemTimestamp(i, "DC_DATE").getTime()) {
				parm.setErr(-1, "��ҩ��ʼʱ�䲻�ܴ�����ҩ����ʱ��");
				parm.setData("ROW", i);
				parm.setData("COL", 2);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MED_REPRESENTOR"))) {
				parm.setErr(-1, "��ҩ�����˲���Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 4);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MEDI_QTY"))) {
				parm.setErr(-1, "��������Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 5);
				return parm;
			}
			if (table.getItemDouble(i, "MEDI_QTY") == 0) {
				parm.setErr(-1, "��������Ϊ0");
				parm.setData("ROW", i);
				parm.setData("COL", 5);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "MEDI_UNIT"))) {
				parm.setErr(-1, "��λ����Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 6);
				return parm;
			}
			if (StringUtils.isEmpty(table.getItemString(i, "FREQ_CODE"))) {
				parm.setErr(-1, "Ƶ�β���Ϊ��");
				parm.setData("ROW", i);
				parm.setData("COL", 7);
				return parm;
			}
			// �Ǳ�Ժ��ҩ
			String notOurHospDrugCode = TConfig
					.getSystemValue("NotOurHospDrugCode");
			if (StringUtils.isNotEmpty(notOurHospDrugCode)) {
				if (StringUtils.equals(table.getItemString(i, "ORDER_CODE"),
						notOurHospDrugCode)) {
					if (StringUtils.isEmpty(table.getItemString(i, "DR_NOTE"))) {
						parm.setErr(-1, "�Ǳ�Ժ��ҩ����ҽ����ע��ע����ҩ����");
						parm.setData("ROW", i);
						parm.setData("COL", 1);
						return parm;
					}
				}
			}
		}

		return parm;
	}

	/**
	 * ��������Ƽ۴���
	 */
	public void onCharge() {
		TParm ibsParm = new TParm();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		String execDeptCode_ = "";
		String vsDrCode_ = "";

		caseNo_ = this.getCaseNo();
		mrNo_ = this.getMrNo();
		ipdNo_ = this.getIpdNo();
		station_ = this.getStationCode();
		execDeptCode_ = Operator.getDept();
		// this.getValue("");
		vsDrCode_ = this.getValueString("VC_CODE");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo_);
		parm = ADMTool.getInstance().getADM_INFO(parm);
		bedNo_ = parm.getValue("BED_NO", 0);

		ibsParm.setData("IBS", "INWLEAD_FLG", false);
		ibsParm.setData("IBS", "SCHD_CODE", getSchdCode());// �ٴ�·��ʱ��
		ibsParm.setData("IBS", "CASE_NO", caseNo_);
		ibsParm.setData("IBS", "IPD_NO", ipdNo_);
		ibsParm.setData("IBS", "MR_NO", mrNo_);
		ibsParm.setData("IBS", "BED_NO", bedNo_);
		ibsParm.setData("IBS", "DEPT_CODE", ((TParm) this.getParameter())
				.getData("ODI", "DEPT_CODE"));
		ibsParm.setData("IBS", "STATION_CODE", station_);
		ibsParm.setData("IBS", "VS_DR_CODE", ((TParm) this.getParameter())
				.getData("ODI", "ORDER_DR_CODE"));
		ibsParm.setData("IBS", "TYPE", "INW");
		ibsParm.setData("IBS", "CLNCPATH_CODE", getClpCode());

		openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
	}

	/**
	 * ���ξ���
	 */
	public void onQuerySummaryInfo() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("TABLE");

		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

		parm.setData("MR_NO", this.getMrNo());
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
	 * �ĵ���
	 */
	public void getPdfReport() {
		String sql = "SELECT APPLICATION_NO  FROM MED_APPLY WHERE MR_NO = '"
				+ mrNo + "' AND ORDER_CAT1_CODE = 'ECC' AND STATUS != '9' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("111111111"+sql);
		if (result.getCount() <= 0) {
			this.messageBox("�ò���û���ĵ���ҽ��"); 
			return;
		}
		// ���������pdf
		TParm parm = new TParm();
		String opbBookNo = "";
		for (int i = 0; i < result.getCount(); i++) {
			opbBookNo += "'" + result.getValue("APPLICATION_NO", i) + "'" + ",";
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("TYPE", "3");
		parm.setData("OPE_BOOK_NO", opbBookNo.substring(0,
				opbBookNo.length() - 1));
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);

	}
	
	/**
	 * ��������
	 *   == 20180115 zhanglei add
	 */
	public void getPDFQiTa() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		this.openDialog("%ROOT%\\config\\sys\\SYSOpeQiTaPDF.x", parm);
	}

	/**
	 * Ѫ�Ǳ���
	 */
	public void getXTReport() {
		SystemTool.getInstance().OpenTnbWeb(this.getMrNo());
	}

	/**
	 * ��֢�໤
	 */
	public void getCCEmrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("TYPE", "1");
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
	}

	/**
	 * ���鲡��
	 */
	public void getOpeMrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("TYPE", "2");
		this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
	}
	/**
	 * ��������
	 */
	public void onOtherReport() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		//parm.setData("TYPE", "2");
		this.openDialog("%ROOT%\\config\\odi\\ODIOtherReport.x", parm);
	}


	/**
	 * �趨������ҩ����
	 */
	private void setOdiOpTableData(TTable table) {
		String sql = "SELECT A.*,NVL(MEDI_QTY,DISPENSE_QTY) AS SUM_QTY,"
				+ "CASE WHEN A.DOSAGE_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.DOSAGE_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS DOSAGE_UNIT_DESC,"
				+ "CASE WHEN A.MEDI_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.MEDI_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS UNIT_CODE FROM ODI_ORDER A WHERE RX_KIND='OP' AND CASE_NO='"
				+ this.getCaseNo() + "' AND CAT1_TYPE='PHA'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			table.setParmValue(new TParm());
			return;
		}

		table.setParmValue(result);
	}

	/**
	 * �趨��Ѫ��¼����
	 */
	private void setOdiBlTableData(TTable table) {
		StringBuffer sbSql = new StringBuffer();
		sbSql
		.append("SELECT NVL(B.BLDTRANS_TIME,A.EFF_DATE) AS BLDTRANS_TIME,A.ORDER_DESC,NVL(B.BLDTRANS_END_TIME,A.DC_DATE) AS BLDTRANS_END_TIME,");
		sbSql
		.append("CASE WHEN A.DISPENSE_UNIT IS NOT NULL THEN (SELECT UNIT_CHN_DESC FROM SYS_UNIT B WHERE A.DISPENSE_UNIT = B.UNIT_CODE) ELSE A.DISPENSE_UNIT_DESC END AS DISPENSE_UNIT,");
		sbSql.append("A.DISPENSE_QTY,A.MR_NO,");
		sbSql
		.append("B.BLD_TYPE,B.BLOOD_NO,B.BLD_CODE,B.SUBCAT_CODE,B.RH_FLG,B.SHIT_FLG,");
		sbSql
		.append("CASE WHEN B.SUBCAT_CODE IS NOT NULL THEN (SELECT C.SUBCAT_DESC FROM BMS_BLDSUBCAT C WHERE B.SUBCAT_CODE = C.SUBCAT_CODE) ELSE A.SPECIFICATION END AS SPECIFICATION,");
		sbSql
		.append("B.CROSS_MATCH_L,B.CROSS_MATCH_S,B.RESULT,B.BLDTRANS_USER");
		sbSql.append(" FROM ODI_ORDER A, BMS_BLOOD B ");
		sbSql
		.append(" WHERE A.CASE_NO = B.CASE_NO(+) AND A.BLOOD_NO = B.BLOOD_NO(+) ");
		sbSql.append(" AND A.RX_KIND = 'BL' AND A.CASE_NO = '");
		sbSql.append(this.getCaseNo());
		sbSql
		.append("' AND (A.BLOOD_NO IS NULL OR (A.BLOOD_NO IS NOT NULL AND B.OUT_NO IS NOT NULL ");
		sbSql
		.append(" AND B.RECEIVED_USER IS NOT NULL AND B.BLDTRANS_USER IS NOT NULL))");
		sbSql.append(" ORDER BY A.ORDER_NO,A.ORDER_SEQ ");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			table.setParmValue(new TParm());
			return;
		}

		table.setParmValue(result);
	}

	/**
	 * ҽʦȷ���¼�
	 */
	public void drCheck() {
		TParm parm = new TParm(TJDODBTool.getInstance().update(
				"UPDATE ODI_ORDER SET DR_CHECK_CODE = '" + Operator.getID()
				+ "' " + " WHERE CASE_NO = '" + this.getCaseNo() + "'"
				+ " AND RX_KIND = 'OP' AND DR_CHECK_CODE IS NULL"));
		if (parm.getErrCode() < 0) {
			this.messageBox("ȷ��ʧ��");
			return;
		}
		this.messageBox("ȷ�ϳɹ�");
		this.onQuery();
	}

	/**
	 * ������
	 */
	public void assessReport() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("PAT_NAME", this.getPatName());
		parm.setData("MR_NO", this.getMrNo());
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("BED_NO_DESC", this.getBedIpdNo(caseNo).getValue(
				"BED_NO_DESC", 0));
		parm.setData("IPD_NO", this.getIpdNo());

		this.openWindow("%ROOT%\\config\\sys\\SYSAssessmentList.x", parm);// wanglong
	}

	/**
	 * ����ҽ��RX_KINDд��OP
	 */
	// private void opeChangeRxkind(){
	// if(opeFlg){
	// TDS dsb = odiObject.getDS("ODI_ORDER");
	// //1
	// int[] nr = dsb.getNewRows(TDS.PRIMARY);
	// for (int i : nr) {
	// dsb.setItem(i, "RX_KIND", "OP");
	// }
	// //2
	// // for (int i = 0; i < dsb.rowCount(); i++) {
	// // if("true".equals(dsb.getItemString(i, "#NEW#"))){
	// // dsb.setItem(i, "RX_KIND", "OP");
	// // }
	// // }
	// odiObject.setAttribute("ODI_ORDER", dsb);



	// }
	// }

	/**
	 * ͣ���÷�ҽ���Ʒ�
	 */
	public void cardioplegiaFee(TDS tds) {
		// TDS dsb = odiObject.getDS("ODI_ORDER");
		String buff = tds.isFilter() ? tds.FILTER : tds.PRIMARY;
		TParm result = new TParm();
		// 1
		int[] nr = tds.getNewRows(buff);
		int count = 0;
		for (int i : nr) {
			String routeCode = tds.getItemData(i, "ROUTE_CODE") + "";
			if (routeCode.equals("CA")) {
				TParm tempParm = tds.getRowParm(i, buff);// =====pangben
				// 2015-8-14
				result.setRowData(count, tempParm);
				count++;
			}
		}
		result.setCount(count);
		onCheckOpOrder(result);
	}

	/**
	 * ͣ���÷�ҽ�����
	 */
	// public void cardioplegiaCheck(){
	// TDS dsb = odiObject.getDS("ODI_ORDER");
	// Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
	// //1
	// int[] nr = dsb.getNewRows(TDS.PRIMARY);
	// int count = 0;
	// for (int i : nr) {
	// String routeCode = dsb.getItemData(i, "ROUTE_CODE")+"";
	// if(routeCode.equals("CA")){
	// dsb.setItem(i, "NS_CHECK_CODE", Operator.getID());
	// dsb.setItem(i, "NS_CHECK_DATE", StringTool.getString(sysdate,
	// "yyyyMMddHHmmss"));
	// }
	// }
	// }

	/**
	 * ����NIS�����
	 */
	public void onNisFormList() {
		SystemTool.getInstance().OpeNisFormList(caseNo, mrNo);
	}

	/**
	 * �����޸�ҽ��ʱ��
	 * 
	 * @author wangb 2016/6/13
	 */
	public void onBatchModOrderDate() {
		// ��ǰѡ��ҳǩ
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() != 0) {
			return;
		}

		TTable table = (TTable) this.getComponent(TABLE1);
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��������ҩҽ��������");
			return;
		}
		TDS ds = odiObject.getDS("ODI_ORDER");
		TParm parm = ds.getBuffer(ds.FILTER);
		TParm selPhaParm = table.getDataStore().getRowParm(row);
		if (!"PHA".equalsIgnoreCase(selPhaParm.getValue("CAT1_TYPE"))) {
			this.messageBox("��ǰѡ�е������з�ҩ������");
			return;
		}

		// ҩ������ʱ��
		Date phaDate = StringTool.getDate(selPhaParm.getValue("EFF_DATE")
				.replace("-", "/").substring(0, 19), "yyyy/MM/dd HH:mm:ss");

		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", selPhaParm.getValue("CASE_NO"));
		// ֻ��ѯ����ҽ��
		queryParm.setData("CAT1_TYPE", "LIS");

		// ��ѯ����һ���ٴ�ע�ǵ�δ���ҽ��
		TParm orderResult = ODIPICTool.getInstance().queryPICOrder(queryParm);
		if (orderResult.getErrCode() < 0) {
			this.messageBox("��ѯһ���ٴ�ҽ������");
			err("ERR:" + orderResult.getErrText());
			return;
		} else if (orderResult.getCount() <= 0) {  
			this.messageBox("δ��ѯ���ѱ�����δ��˵�PK��Ѫҽ��");
			return;
		} else {
			// modify by wangb 2016/09/30 ������ʿ�Ƿ񵥴�ִ�е�У�� START
			TParm odiDspnDParm = new TParm();
			odiDspnDParm.setData("CASE_NO", orderResult.getValue("CASE_NO", 0));
			odiDspnDParm.setData("ORDER_NO", orderResult
					.getValue("ORDER_NO", 0));
			odiDspnDParm.setData("ORDER_SEQ_LIST", orderResult.getValue(
					"ORDER_SEQ").replace("[", "").replace("]", "").replace(" ",
							"").replace(",", "','"));

			// ��ѯ��ʿִ��ϸ������
			TParm dspnDResult = ODIPICTool.getInstance().queryOdiDspnD(odiDspnDParm);

			if (dspnDResult.getErrCode() < 0) {
				this.messageBox("��ѯ��ʿ����ִ�����ݴ���");
				err("ERR:" + orderResult.getErrText());
				return;
			} else {
				// ����ִ������
				boolean execFlg = false;
				int dspnCount = dspnDResult.getCount();
				for (int i = 0; i < dspnCount; i++) {
					if (StringUtils.isNotEmpty(dspnDResult.getValue(
							"NS_EXEC_DATE_REAL", i))) {
						execFlg = true;
						break;
					}
				}

				// ������ڵ���ִ�������������ٴθ���ҽ������ʱ��
				if (execFlg) {
					this.messageBox("�����ѵ���ִ�е�ҽ�����ݣ��������ٴ��޸�ҽ������ʱ��");
					return;
				}
			}
			// modify by wangb 2016/09/30 ������ʿ�Ƿ񵥴�ִ�е�У�� END

			int count = orderResult.getCount();
			Date date = null;
			int intervalTime = 0;
			String drNote = "";

			for (int i = 0; i < count; i++) {
				drNote = orderResult.getValue("DR_NOTE", i);
				if (StringUtils.isEmpty(drNote) || !drNote.contains("h")) {
					continue;
				}

				intervalTime = (int) Math.round(Double.valueOf(drNote
						.split("h")[0]) * 60);
				date = DateUtils.addMinutes(phaDate, intervalTime);
				orderResult.setData("EFF_DATE", i, StringTool.getString(date,
						"yyyy/MM/dd HH:mm:ss"));
			}

			// ִ�б������
			TParm result = TIOM_AppServer.executeAction(
					"action.odi.ODIPICAction", "onSaveByBatchModOrderDate",
					orderResult);

			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("E0001");
				return;
			} else {
				// ������ɺ�ˢ��ҽ��վ��������
				this.onQuery();
				this.messageBox("�޸ĳɹ�");
			}
		}
	}


	// modified by WangQing 20170316
	// ����סԺҽ��վ������ҽ��վ
	// ����ҽ��վ���� OPE_FLG 
	/**
	 * ��ʹ���Ĳ���  
	 */
	public void onAMI(){
		// add by wangqing 20170701 start
		// ֻ����ʹ���ĵĲ��˲�����ʹ���Ĳ���
		TParm amiResult = new TParm();
		amiResult= EMRAMITool.getInstance().getEnterRouteAndPathKindByCaseNo(ADMUtil.getCaseNo(caseNo));
		if(amiResult.getErrCode()<0){
			this.err(amiResult);
			return;
		}
		if(amiResult.getCount()<=0){
			this.messageBox("û�в�����Ϣ������");
			return;
		}
		if(!(amiResult.getValue("ENTER_ROUTE", 0) != null 
				&& amiResult.getValue("ENTER_ROUTE", 0).equals("E02"))){// ��ʹ���Ĳ���
			this.messageBox("����ʹ���Ĳ���������");
			return;
		}
		// add by wangqing 20170701 end
		
		
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			TParm sysParm = (TParm) o;
			// modified by WangQing 20170510
			boolean opeFlg = sysParm.getData("ODI", "OPE_FLG")==null ? false : true;

			if(opeFlg){// ����ҽ��վ
				// ���ݲ���
				TParm sysParm1 = new TParm();
				sysParm1.setData("CASE_NO", caseNo);// סԺ�����
				sysParm1.setData("MR_NO", mrNo);
				sysParm1.setData("PAT_NAME", patName);
				sysParm1.setData("SEX_CODE", sexCode);
				sysParm1.setData("OPBOOK_SEQ", opBookSeq);
				sysParm1.setData("AGE", OdiUtil.getInstance().showAge(this.getBirthDay(),
						this.getAdmDate()));
				
				String sql = " SELECT CASE_NO, MR_NO, OPBOOK_SEQ, TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ='"+opBookSeq+"' AND TYPE_CODE='2' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					return;
				}
				if(result.getCount()<=0){
					this.messageBox("û�н�������������");
					return;
				}		
			
				this.openWindow("%ROOT%\\config\\emr\\EMROpeDrStation.x", sysParm1, false);
			}else{// סԺҽ��վ
				TParm xtParm = new TParm();
				xtParm.setData("CASE_NO", this.getCaseNo());
				xtParm.setData("MR_NO",this.getMrNo());
				xtParm.setData("SEX",this.getSexCode());
				xtParm.setData("AGE",this.getValue("AGE"));
				xtParm.setData("NAME",this.getPatName());
				this.openWindow("%ROOT%\\config\\reg\\REGXTcore.x",xtParm);
			}
		}	
	}

	/**
	 * ����Ѫ�Ǳ���(ǿ��)
	 */
	public void getBgReport() {
		// ǿ��Ѫ��webչ��
		TParm result = SystemTool.getInstance().OpenJNJWeb(this.getCaseNo());
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}
	
	/**
	 * ����������У���Ƿ�����ԭѧ���
	 */
	public boolean checkEtiolgexm(TDS ds){// add by kangy 20170524
		boolean flg=false;
			int chnNewRow[] = ds.getNewRows();
			for (int temp : chnNewRow) {
				if ("Y1004".equals(ds.getRowParm(temp).getValue("RPTTYPE_CODE"))) {
					flg = true;
					break;
				}
			}
			return flg;
		
	}
	/**
	 * У�鿪��ҽ���Ƿ��ǿ�����
	 */
	public boolean checkAntibiotics(String orderCode){// ADD BY kangy 20170524
		boolean flg=false;
		String sql="SELECT ORDER_CODE FROM PHA_BASE WHERE ANTIBIOTIC_CODE IS NOT NULL AND ORDER_CODE='"+orderCode+"'";
		 TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		 if(parm.getCount()==0){
			 flg=false;
		 }else if(parm.getCount()==1){
			 flg=true;
		 }
		return flg;
	}
	
	/**
	 * ��ʼ��У��ҽ���Ƿ������ԭѧ���
	 */
	public boolean checkEtiolgexmParm(TParm TDSparm){// add by kangy 20170601
		boolean flg=false;
		for(int i=0;i<TDSparm.getCount();i++){
			if ("Y1004".equals(TDSparm.getValue("RPTTYPE_CODE",i))) {
				flg = true;
				break;
			}
		}
			return flg;
		
	}
	
	/**
	 * ��ʼ��У���Ƿ��п�����
	 */
	public boolean checkAntibioticsTDSParm(TParm TDSparm){// add by kangy 20170524
		boolean flg=false;
			TParm parm=new TParm();
			for (int i=0;i<TDSparm.getCount();i++) {
				String sql="SELECT ORDER_CODE FROM PHA_BASE WHERE ANTIBIOTIC_CODE IS NOT NULL AND ORDER_CODE='"+TDSparm.getValue("ORDER_CODE",i)+"'";
				  parm=new TParm(TJDODBTool.getInstance().select(sql));
				 if(parm.getCount()==0){
					 flg=false;
				 }else if(parm.getCount()==1){
					 flg=true;
					 break;
				 }	
			}
			return flg;
		
	}

	/**
     * ������У��
     * 
     * @return
     */
	public boolean stopFlg() {
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		StringBuffer sbMsg = new StringBuffer();
		String sysDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 19).replace("-", "/");
		String caseNo = ((TParm) this.getParameter())
				.getValue("ODI", "CASE_NO");
		String sql = "SELECT UNLOCKED_FLG,STOP_BILL_FLG FROM ADM_INP WHERE CASE_NO ='"
				+ caseNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ��ݱ���
		String sqlD = "SELECT A.CASE_NO,B.LOCK_CTZ_FLG AS LOCK_CTZO,C.LOCK_CTZ_FLG AS LOCK_CTZT,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
				+ "DISCOUNT_RATE "
				+ "FROM ADM_INP A, SYS_CTZ_REBATE B, SYS_CTZ_REBATE C   "
				+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+)  AND A.CTZ2_CODE = C.CTZ_CODE(+) "
				+ "AND A.CASE_NO='"
				+ caseNo + "' ";
		TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
		
		
		// ȡ��ƾ�����
		String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
		TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));

		// �����ܽ��
		String sql2 = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD "
				+ "WHERE CASE_NO='" + caseNo + "' AND BILL_DATE <= TO_DATE('"
				+ sysDate + "','YYYY/MM/DD HH24:MI:SS')";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result2.getCount() > 0
				&& result2.getValue("TOT_AMT", 0).length() > 0) {
			totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
		}

		// Ԥ����
		String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
				+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
				+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
				+ "AND CHARGE_DATE <= TO_DATE ('" + sysDate
				+ "','YYYY/MM/DD HH24:MI:SS') AND CASE_NO = '" + caseNo
				+ "' GROUP BY CASE_NO";
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if (result3.getCount() > 0
				&& result3.getValue("PRE_AMT", 0).length() > 0) {
			totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
		}

		// ���
		double balance = StringTool.round(totAmt3
				- (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)), 2);

		sbMsg.append("DBʱ�䣺" + sysDate + ",�ͻ���ID:" + Operator.getID()
				+ ",�ͻ���IP:" + Operator.getIP());
		sbMsg.append(",����ţ�" + caseNo + ",Ԥ�����ܶ" + totAmt3);
		sbMsg.append(",ԭʼ�����ܶ" + totAmt2 + ",�ۿ�:"
				+ resultD.getDouble("DISCOUNT_RATE", 0));
		sbMsg.append(",��" + balance + ",��ɫ�����ߣ�"
				+ resultSign.getDouble("RED_SIGN", 0));
		sbMsg.append(",ԭʼֹͣ����ע�ǣ�" + result.getValue("STOP_BILL_FLG", 0));
		TParm errLogParm = new TParm();
		//��ʱ�������Ŵ�������
				if (balance >= resultSign.getDouble("YELLOW_SIGN", 0)) {
					String sqlS = "UPDATE ADM_INP SET SMS_COUNT='0' WHERE CASE_NO='"
							+ caseNo + "' ";
					TJDODBTool.getInstance().update(sqlS);
				}
				//�����Ϊ���ܹܿ�ʱ,��������
				if( resultD.getValue("LOCK_CTZO", 0).equals("Y") 
								|| resultD.getValue("LOCK_CTZT", 0).equals("Y") ){
					return false;
				}
		
		// ��ʱ���������ý��������ݲ�������
		if (result.getValue("UNLOCKED_FLG", 0).equals("1")
						|| result.getValue("UNLOCKED_FLG", 0).equals("2")) {
			return false;
		}
		// �����ں�ɫ������,���н���
		if (balance > resultSign.getDouble("RED_SIGN", 0)) {
			String sqlSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='N',UNLOCKED_FLG='0' "
					+ " WHERE CASE_NO='" + caseNo + "' ";
			sbMsg.append(",����sql��" + sqlSBF);
			TJDODBTool.getInstance().update(sqlSBF);
			errLogParm.setData("MSG", sbMsg.toString());
			TIOM_AppServer.executeAction("action.pha.PHAATCAction", "printLog",
					errLogParm);
			return false;
		}

		if ("Y".equals(result.getValue("STOP_BILL_FLG", 0))) {
			errLogParm.setData("MSG", sbMsg.toString());
			TIOM_AppServer.executeAction("action.pha.PHAATCAction", "printLog",
					errLogParm);
			return true;
		}

		return false;
	}
	


	/**
	 * ����������Ϣ
	 * @param result
	 */
	public void err(TParm result){
    	err("ERR:" + result.getErrCode() + result.getErrText() +
		result.getErrName());
	}


}
