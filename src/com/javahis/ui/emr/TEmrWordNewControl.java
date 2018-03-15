package com.javahis.ui.emr;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;

import org.apache.commons.lang.StringUtils;

import com.sun.awt.AWTUtilities;
import painting.Image_Paint;

import jdo.clp.CLPEMRTool;
import jdo.emr.EMRCreateHTMLTool;
import jdo.emr.EMRCreateXMLTool;
import jdo.emr.EMRCreateXMLToolForSD;
import jdo.emr.GetWordValue;
import jdo.emr.OptLogTool;
import jdo.hl7.Hl7Communications;
import jdo.med.MEDApplyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VText;
import com.dongyang.util.TList;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.IBlock;
import com.javahis.util.ClipboardTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;


/**
 * <p>
 * Title: ���Ӳ���-סԺҽ��վ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author whao
 * @version 1.0
 */
@SuppressWarnings({"unchecked","static-access"})
public class TEmrWordNewControl extends TControl implements DMessageIO {
	/**
	 * ����ͼ������
	 */
	public static final String UNVISIBLE_MV = "UNVISITABLE_ATTR";

	/**
	 * WORD����
	 */
	private static final String TWORD = "WORD";
	/**
	 * ��������
	 */
	private static final String TREE_NAME = "TREE";
	/**
	 * ֪ʶ��ǰ׺
	 */
	private static final String KNOWLEDGE_STORE_PREFIX="EMR9";
	/**
	 * WORD����
	 */
	private TWord word;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ϵͳ���
	 */
	private String systemType;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ����
	 */
	private String patName;
	/**
	 * Ӣ������
	 */
	private String patEnName;
	/**
	 * �õ���
	 */
	private String admYear;
	/**
	 * �õ���
	 */
	private String admMouth;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ���������
	 */
	private TParm emrChildParm = new TParm();
	/**
	 * ����
	 */
	private TTreeNode treeRoot;
	/**
	 * ����ҽʦ
	 */
	private String vsDrCode;
	/**
	 * ����ҽʦ
	 */
	private String attendDrCode;
	/**
	 * ������
	 */
	private String directorDrCode;
	/**
	 * ���ÿ���
	 */
	private String deptCode;
	/**
	 * ���ò���
	 */
	private String stationCode;
	/**
	 * ��ǰ�༭״̬
	 */
	private String onlyEditType;
	/**
	 * ��������
	 */
	private Timestamp admDate;
	/**
	 * Ȩ�ޣ�1,ֻ�� 2,��д 3,���ֶ�д
	 */
	private String ruleType = "1";
	/**
	 * ����ʽ
	 */
	private String styleType = "2";
	/**
	 * ��������
	 */
	private String type = "";
	/**
	 * ҽԺȫ��
	 */
	private String hospAreaName = "";
	/**
	 * Ӣ��ȫ��
	 */
	private String hospEngAreaName = "";
	/**
	 * д����
	 */
	private String writeType;
	/**
	 * ������
	 */
	private String diseDesc;// add by wanglong 20121025 Ϊ����סԺ֤����ʾ������
	private TParm diseBasicData; // add by wanglong 20121115 ��¼�����ֻ�����Ϣ
	TParm returnParm = new TParm();// add by wanglong 20121115 �˳���ķ���ֵ
	private TParm opeBasicData; // add by wanglong 20130514 ��¼����������Ϣ
	String adm_type = "";
	/**
	 * ԤԼ����
	 */
	private String bedNo ;  //  add by  chenxi 20130308
	/**
	 * ���Ӧ
	 */
	private Map microMap = new HashMap();

	/**
	 * ����Ԫ�ؼ���Ӧ��ֵ��
	 */
	private Map hideElemMap = new HashMap();

	// ��ͼ��ʱ�ļ���
	private String picTempPath = "C:/hispic/temp/";

	/**
	 * �Ƿ����ͼƬ
	 */
	private boolean containPic = false;
	// �������뵥��;
	private String medApplyNo = "";

	//�Ƿ���׷�ӱ�־(�ճ�����)
	private boolean isApplend=false;

	/**
	 * �����ļ�����
	 */
	private String subFileName;

	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}

	public String getMedApplyNo() {
		return medApplyNo;
	}

	public void setMedApplyNo(String medApplyNo) {
		this.medApplyNo = medApplyNo;
	}


	public boolean isApplend() {
		return isApplend;
	}

	public void setApplend(boolean isApplend) {
		this.isApplend = isApplend;
	}

	//
	private String TREE_TYPE = null;//wuh add
	private final String TREE_TYPE_1 = "����������";
	private final String TREE_TYPE_2 = "ҽ�ƹ���";
	private final String TREE_TYPE_3 = "���ҷ���";
	//
	//    private TEmrTreeOperate eto = TEmrTreeOperate.getInstance();//wuh add

	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";

	public void onInit() {

		super.onInit();
		// ��ʼ��WORD
		initWord();
		// ��ʼ������
		initPage();
		// ע���¼�
		initEven();
		// ��������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��½
		if (this.isCheckUserDr() || this.isDutyDrList()) {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			OptLogTool.getInstance().writeOptLog(this.getParameter(), "L", emrParm);

		}
		// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/����
		else {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			OptLogTool.getInstance().writeOptLog(this.getParameter(), "R", emrParm);
		}

		// �Զ����涨ʱ����ʼ��
		// initTimer();
		initMicroMap();
		// ��Ļ����,���Ŵ�
		callFunction("UI|showMaxWindow");

		String[] btnStr = // wuh
			{"SVAE_PAT", "CLINIC_DATA", "SUB_TEMPLATE", "SPECIAL_CHARS", "NATION_STANDARD",
					"CUSTOM_SORT1", "CUSTOM_SORT2" };
		for (int i = 0; i < btnStr.length; i++) {
			TButton newBtn = (TButton) this.getComponent(btnStr[i]);

			newBtn.setBorder(BorderFactory.createLineBorder(Color.black));
			newBtn.setContentAreaFilled(false);
		}
	}

	private void initMicroMap() {
		TParm microParm = new TParm(getDBTool().select(
				"SELECT MICRO_NAME,DATA_ELEMENT_CODE FROM EMR_MICRO_CONVERT"));
		int count = microParm.getCount();
		for (int i = 0; i < count; i++) {
			microMap.put(microParm.getValue("MICRO_NAME", i), microParm
					.getValue("DATA_ELEMENT_CODE", i));
		}
	}

	/**
	 * ��ȡ��ʾ��Ϣ;
	 *
	 * @param elementGroup
	 *            String
	 * @param elementCode
	 *            String
	 * @return String
	 */
	private String getShowMessage(String elementGroup, String elementCode) {
		TParm nameParm = new TParm(getDBTool().select(
				"SELECT DATA_DESC FROM EMR_CLINICAL_DATAGROUP WHERE GROUP_CODE='"
						+ elementGroup + "' AND DATA_CODE='" + elementCode
						+ "'"));
		int count = nameParm.getCount();
		if (count > 0) {
			return nameParm.getValue("DATA_DESC", 0);
		}
		return elementCode;

	}

	/**
	 * ��ʼ��WORD
	 */
	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
		// ����
		this.getWord().setFontComboTag("ModifyFontCombo");
		// ����
		this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");

		// ���
		this.getWord().setFontBoldButtonTag("FontBMenu");

		// б��
		this.getWord().setFontItalicButtonTag("FontIMenu");


		//saveWord();
		//

	}

	/**
	 * �õ�WORD����
	 *
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * ע���¼�
	 */
	public void initEven() {
		// ����ѡ������Ŀ
		addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubled");
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {

		this.hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		this.hospEngAreaName = Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion());

		// �õ�ϵͳ���
		Object obj = this.getParameter();
		if (obj != null) {
			this.setSystemType(((TParm) obj).getValue("SYSTEM_TYPE"));
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.patEnName = getPatEnName(this.getMrNo()).getValue("PAT_NAME1",
					0);
			this.setIpdNo(((TParm) obj).getValue("IPD_NO"));
			this.setPatName(((TParm) obj).getValue("PAT_NAME"));
			adm_type = (((TParm) obj).getValue("ADM_TYPE_ZYZ"));//yanjing 20130807
			if (((TParm) obj).getValue("PAT_NAME").equals("")) {
				this.setPatName(getPatEnName(this.getMrNo()).getValue(
						"PAT_NAME", 0));
			}
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			/**
			 * System.out.println("==CASE_NO==" + ((TParm)
			 * obj).getValue("CASE_NO"));
			 **/
			String yearStr = ((TParm) obj).getValue("CASE_NO").substring(0, 2);
			this.setAdmYear(yearStr);
			String mouthStr = ((TParm) obj).getValue("CASE_NO").substring(2, 4);
			this.setAdmMouth(mouthStr);
			this.setAdmType(((TParm) obj).getValue("ADM_TYPE"));
			this.setDeptCode(((TParm) obj).getValue("DEPT_CODE"));
			this.setStationCode(((TParm) obj).getValue("STATION_CODE"));
			this.setAdmDate(((TParm) obj).getTimestamp("ADM_DATE"));
			this.ruleType = ((TParm) obj).getValue("RULETYPE");
			this.styleType = ((TParm) obj).getValue("STYLETYPE");
			String typeStr = ((TParm) obj).getValue("TYPE");
			if (typeStr.length() == 0) {
				this.type = this.getAdmType();
			} else {
				this.type = typeStr;
			}
			this.writeType = ((TParm) obj).getValue("WRITE_TYPE");
			this.bedNo = ((TParm) obj).getValue("BED_NO"); //  chenxi add 20130308  ԤԼ����
			this.diseDesc = ((TParm) obj).getValue("DISE_DESC");//add by wanglong 20121025 Ϊ����סԺ֤����ʾ������
			if(((TParm) obj).getData("diseData")!=null){ //add by wanglong 20121115 ��¼�����ֻ�����Ϣ
				this.diseBasicData=(TParm)((TParm) obj).getData("diseData");
			}
			if (((TParm) obj).getData("OPE_DATA") != null) { // add by wanglong 20130514 ��������������������Ϣ
				// this.opeBasicData = ((TParm) obj).getParm("OPE_DATA");
				this.opeBasicData = (TParm) ((TParm) obj).getData("OPE_DATA");
			}
			setTMenuItem(false);

			// �õ���������
			TParm emrFileDataParm = (TParm) ((TParm) obj)
					.getData("EMR_FILE_DATA");

			// ������������
			if (emrFileDataParm != null) {
				// �޸ĵĵ���
				if (emrFileDataParm.getBoolean("FLG")) {
					// �򿪲���
					if (!this.getWord().onOpen(
							emrFileDataParm.getValue("FILE_PATH"),
							emrFileDataParm.getValue("FILE_NAME"), 3, false)) {
						return;
					}
					TParm allParm = new TParm();
					allParm.setData("FILE_TITLE_TEXT", "TEXT",
							this.hospAreaName);
					allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
							this.hospEngAreaName);
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this
							.getMrNo());
					allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this
							.getIpdNo());
					allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
					// �������뵥��
					allParm.setData("FLG", "TEXT", emrFileDataParm.getValue(
							"URGENT_FLG").equalsIgnoreCase("Y") ? "��" : "");

					allParm.addListener("onDoubleClicked", this,
							"onDoubleClicked");
					allParm.addListener("onMouseRightPressed", this,
							"onMouseRightPressed");
					this.getWord().setWordParameter(allParm);
					// $$=======add by lx 2012/03/20�������뵥��start=============$$//
					this
					.setMedApplyNo(emrFileDataParm
							.getValue("MED_APPLY_NO"));
					// $$=======add by lx 2012/03/20�������뵥��end=============$$//
					// ���ò��ɱ༭
					this.getWord().setCanEdit(false);
					// ���ú�(�оɲ��������º�)
					// setMicroField();
					// ���ñ༭״̬
					this.setOnlyEditType("ONLYONE");
					TParm emrParm = new TParm();
					emrParm.setData("FILE_SEQ", emrFileDataParm
							.getValue("FILE_SEQ"));
					emrParm.setData("FILE_NAME", emrFileDataParm
							.getValue("FILE_NAME"));
					emrParm.setData("CLASS_CODE", emrFileDataParm
							.getValue("CLASS_CODE"));
					emrParm.setData("SUBCLASS_CODE", emrFileDataParm
							.getValue("SUBCLASS_CODE"));
					emrParm.setData("CASE_NO", this.getCaseNo());
					emrParm.setData("MR_NO", this.getMrNo());
					emrParm.setData("IPD_NO", this.getIpdNo());
					emrParm.setData("FILE_PATH", emrFileDataParm
							.getValue("FILE_PATH"));
					emrParm.setData("DESIGN_NAME", emrFileDataParm
							.getValue("DESIGN_NAME"));
					emrParm.setData("DISPOSAC_FLG", emrFileDataParm
							.getValue("DISPOSAC_FLG"));

					// ���õ�ǰ�༭����
					this.setEmrChildParm(emrParm);
					setTMenuItem(false);
					//
					// ����༭
					if ((ruleType.equals("2") || ruleType.equals("3"))
							&& ("O".equals(this.getAdmType()) || "E"
									.equals(this.getAdmType()))
							|| ((ruleType.equals("2") || ruleType.equals("3")) && isCheckUserDr())) {
						onEdit();
					}
				}
				// �½��ĵ���
				else {
					// �򿪲���
					if (!this.getWord().onOpen(
							emrFileDataParm.getValue("TEMPLET_PATH"),
							emrFileDataParm.getValue("EMT_FILENAME"), 2, false)) {

						return;
					}

					TParm allParm = new TParm();
					allParm.setData("FILE_TITLE_TEXT", "TEXT",
							this.hospAreaName);
					allParm.setData("FILE_TITLEENG_TEXT", "TEXT",
							this.hospEngAreaName);
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this
							.getMrNo());
					allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this
							.getIpdNo());
					allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
					// �������뵥��
					allParm.setData("FLG", "TEXT", emrFileDataParm.getValue(
							"URGENT_FLG").equalsIgnoreCase("Y") ? "��" : "");
					allParm.addListener("onDoubleClicked", this,
							"onDoubleClicked");
					allParm.addListener("onMouseRightPressed", this,
							"onMouseRightPressed");

					// $$=======add by lx 2012/03/20�������뵥��start=============$$//
					this
					.setMedApplyNo(emrFileDataParm
							.getValue("MED_APPLY_NO"));
					// $$=======add by lx 2012/03/20�������뵥��end=============$$//
					this.getWord().setWordParameter(allParm);
					// ���ò��ɱ༭
					this.getWord().setCanEdit(false);
					// ���ú�
					setMicroField(true);
					// ���ñ༭״̬
					this.setOnlyEditType("NEW");
					// ���ýڵ�ֵ
					this.setEmrChildParm(emrFileDataParm);
					// TParm emrNameP = this.getFileServerEmrName();
					this.setEmrChildParm(this.getFileServerEmrName());
					setTMenuItem(false);

					// ����༭
					if ((ruleType.equals("2") || ruleType.equals("3"))
							&& ("O".equals(this.getAdmType()) || "E"
									.equals(this.getAdmType()))
							|| (ruleType.equals("2") || ruleType.equals("3"))
							&& (isCheckUserDr() || this.isDutyDrList())) {
						onEdit();
					}
					// ˢ��ץ�ı�
					this.getWord().fixedTryReset(this.getMrNo(),
							this.getCaseNo());
				}

			} else {
				this.getWord().setCanEdit(false);
			}
			// ����������
			TParm ioParm = new TParm();
			// �����ļ��Ƿ������ļ�
			if (emrFileDataParm == null) {
				ioParm.setData("FLG", false);
			} else {
				ioParm.setData("FLG", emrFileDataParm.getBoolean("FLG"));
			}

			// ���ú�
			ioParm.addListener("setMicroData", this, "setMicroData");
			// ����ץȡ
			ioParm.addListener("getCaptureValue", this, "getCaptureValue");
			// ����ץȡ��
			ioParm.addListener("getCaptureValueArray", this,
					"getCaptureValueArray");
			// ����ץȡ��ֵ
			ioParm.addListener("setCaptureValue", this, "setCaptureValueArray");
			// ((TParm)obj).runListener("MicroListener",ioParm);
			// EMR_LISTENER(null Object[])
			// ((TParm)obj).runListener("EMR_LISTENER","S");
			((TParm) obj).runListener("EMR_LISTENER", ioParm);
			if ("1".equals(this.styleType)) {
				onCloseChickedCY();
			}
			if (emrFileDataParm != null) {
				if (!emrFileDataParm.getBoolean("FLG")) {
					this.setOnlyEditType("NEW");
				}
			} else {
				this.setOnlyEditType("NEW");
			}
		} else {
			//TEST
			// this.setSystemType("EMG");
			// this.setMrNo("000000000052");
			// this.setIpdNo("");
			// this.setPatName("��ѩ��");
			// this.setCaseNo("100414000022");
			// this.setAdmYear("2010");
			// this.setAdmMouth("04");
			// this.setAdmType("E");
			// this.ruleType="2";
			// this.setDeptCode("10101");
		}


		// ������
		//loadTree();//wuh delete
		this.doInitTree();//wuh
	}

	/**
	 * �����
	 */
	private void doInitTree(){//wuh
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		treeRoot.setText(null);
		treeRoot.removeAllChildren();
		this.setValue("MR_NO", this.getMrNo());
		if ("en".equals(this.getLanguage()) && patEnName != null && patEnName.length() > 0) {
			this.setValue("PAT_NAME", this.patEnName);
		} else {
			this.setValue("PAT_NAME", this.getPatName());
		}
		this.setValue("IPD_NO", this.getIpdNo());
		isCheckUserDr();
		onNationStandard();
	}

	/**
	 * �õ�����Ӣ����
	 *
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public TParm getPatEnName(String mrNo) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT PAT_NAME,PAT_NAME1"
						+ " FROM SYS_PATINFO"
						+ " WHERE MR_NO='" + mrNo + "'"));
		return parm;
	}

	/**
	 * ����ѡ��ر��¼�
	 */
	public void onCloseChickedCY() {

		TMovePane mp = (TMovePane) callFunction("UI|MOVEPANE|getThis");
		mp.onDoubleClicked(true);
		((TMovePane) this.getComponent("MOVEPANE")).setEnabled(false);
	}

	/**
	 * �õ�ץȡֵ
	 *
	 * @param name
	 *            String
	 * @return String
	 */
	public String getCaptureValue(String name) {
		return this.getWord().getCaptureValue(name);
	}

	/**
	 * �����
	 *
	 * @param names
	 *            String[]
	 * @return TParm
	 */
	public Map getCaptureValueArray(List name) {
		Map result = new HashMap();
		int rowCount = name.size();
		for (int i = 0; i < rowCount; i++) {
			result.put(name.get(i), this.getCaptureValue("" + name.get(i)));
		}
		return result;
	}

	/**
	 * ����ץȡ��
	 *
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setCaptureValueArray(String name, String value) {
		//ԭ���������ظ���,������Ҫ��Tword���мӸ����� ͨ������ȡ�ؼ������� ��ֵ�� ͬ���Ḳ����ǰ��ֵ��
		boolean isSetCaptureValue = this.setCaptureValue(name, value);
		if (!isSetCaptureValue) {
			ECapture ecap = this.getWord().findCapture(name);
			if (ecap == null) {
				return;
			}
			ecap.setFocusLast();
			ecap.clear();
			this.getWord().pasteString(value);

		}

	}

	/**
	 * ���ú�����
	 *
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setMicroData(String name, String value) {
		this.getWord().setMicroField(name, value);
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
	 * ��������ҽ���༭Ȩ��
	 */
	/*	private void setEditLevel() {
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy��MM��dd�� HHʱmm��ss��");
		int stuts = this.getWord().getNodeIndex();
		int stutsOnly = this.getWord().getNodeIndex();
		if ("ODI".equals(this.getSystemType())) {
			// ����ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == -1) {
				stuts = 0;
			}
			// ����ҽʦ
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0) {
				stuts = 1;
			}
			// ����ҽʦ
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1) {
				stuts = 2;
			}
			// ���Ǿ���ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = 1;
			}
			// ���Ǿ���ҽʦ��������ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = 2;
			}
		}
		// �޸ļ�¼
		MModifyNode modifyNode = this.getWord().getPM().getModifyNodeManager();
		int count = modifyNode.size();
		if (count > 0) {
			// �����޸�ʱ��
			modifyNode.get(stutsOnly).setModifyDate(dateStr);
		} else {
			// ����ҽʦ
			EModifyInf infDr = new EModifyInf();
			// �޸���
			infDr.setUserName(getOperatorName(this.getVsDrCode()));
			// �޸�ʱ��
			infDr.setModifyDate(dateStr);
			// �����޸ģ������Է���ADD��
			modifyNode.add(infDr);
			// ����ҽʦ
			EModifyInf infAtten = new EModifyInf();
			// �޸���
			infAtten.setUserName(getOperatorName(this.getAttendDrCode()));
			// �޸�ʱ��
			infAtten.setModifyDate(dateStr);
			// �����޸ģ������Է���ADD��
			modifyNode.add(infAtten);
			// ����ҽʦ
			EModifyInf infDir = new EModifyInf();
			// �޸���
			infDir.setUserName(getOperatorName(this.getDirectorDrCode()));
			// �޸�ʱ��
			infDir.setModifyDate(dateStr);
			// �����޸ģ������Է���ADD��
			modifyNode.add(infDir);
		}
		if (stuts != this.getWord().getNodeIndex()) {
			this.getWord().setNodeIndex(stuts);
		}
		saveWord();
	}*/

	/**
	 * ��������ҽ���༭Ȩ��
	 */
	/*	private void setEditLevelCancel() {
		String creatFileUser = this.getWord().getFileLastEditUser();
		int stuts = this.getWord().getNodeIndex();
		if ("ODI".equals(this.getSystemType())) {
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0
					&& creatFileUser.equals(Operator.getID())) {
				stuts = -1;
			}
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1
					&& creatFileUser.equals(Operator.getID())) {
				stuts = 0;
			}
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 2
					&& creatFileUser.equals(Operator.getID())) {
				stuts = 1;
			}
			// ���Ǿ���ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = -1;
			}
			// ���Ǿ���ҽʦ��������ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = -1;
			}
		}
		if (stuts == this.getWord().getNodeIndex()) {
			return;
		}
		this.getWord().setNodeIndex(stuts);
		saveWord();
	}*/

	/*private void saveWord() {
		boolean mSwitch = this.getWord().getMessageBoxSwitch();
		//this.messageBox("mSwitch" + mSwitch);
		this.getWord().setMessageBoxSwitch(false);
		this.getWord().onSave();
		this.getWord().setMessageBoxSwitch(mSwitch);
	}*/

	/**
	 * �ύ
	 */
	public void onSubmit() {
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return;
		}
		// 4�������ύȨ�޿��ƣ�
		if (!this.checkDrForSubmit()) {
			this.messageBox("E0011"); // Ȩ�޲���
			return;
		}
		// �ж��Ƿ�����һ������
		if (!this.checkonSumbit()) {
			this.messageBox("���ϼ�ҽʦ,�޷��ύ��");
			return;
		}
		// ̩���ݲ���Ҫ
		// setEditLevel();
		TParm emrParm = this.getEmrChildParm();
		this.setOptDataParm(emrParm, 2);
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.messageBox("�ύ�ɹ���");
			this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();
			//������
			loadTreeByType();//wuh
			return;
		} else {
			this.messageBox("�ύʧ�ܣ�");
			return;
		}
	}

	public boolean checkonSumbit() {
		if (Operator.getID().equals("")) {
			return false;
		} else if (Operator.getID().equals(this.getVsDrCode())) {
			if (this.getAttendDrCode().equals("")) {
				return false;
			}
		} else if (Operator.getID().equals(this.getAttendDrCode())) {
			if (this.getDirectorDrCode().equals("")) {
				return false;
			}
		} else if (Operator.getID().equals(this.getDirectorDrCode())) {
			return false;
		}
		return true;

	}

	/**
	 * ȡ���ύ
	 */
	public void onSubmitCancel() {
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return;
		}
		// 5������ȡ���ύȨ�޿��ƣ�
		if (!this.checkDrForSubmitCancel()) {
			this.messageBox("E0011"); // Ȩ�޲���
			return;
		}
		TParm emrParm = this.getEmrChildParm();
		if (this.checkUserSubmit(emrParm, Operator.getID())) {
			if (this.messageBox("ѯ��", "��ǰ�û����ύ���Ƿ�ȡ���ύ��", 2) != 0) {
				return;
			}
			this.setOptDataParm(emrParm, 3);
		} else {
			if (this.messageBox("ѯ��", "��ǰ�û�δ�ύ���Ƿ�ȡ���ύ��", 2) != 0) {
				return;
			}
			this.setOptDataParm(emrParm, 4);
		}
		// ̩����ȥ��
		// setEditLevelCancel();
		if (this.saveEmrFile(emrParm)) {
			this.setEmrChildParm(emrParm);
			this.getWord().setCanEdit(true);
			this.getWord().onEditWord();
			this.messageBox("ȡ���ύ�ɹ���");
			return;
		} else {
			this.messageBox("ȡ���ύʧ�ܣ�");
			return;
		}
	}

	/**
	 * ����༭
	 */
	public boolean onEdit() { //modify by wanglong 20121205
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return false; //modify by wanglong 20121205
		}
		// ��дȨ��
		if (ruleType.equals("1")) {
			// ��û�б༭Ȩ��
			this.messageBox("E0102");
			return false; //modify by wanglong 20121205

		} else {
			if (!ruleType.equals("3")) {
				//��סԺ�򣬰���ɫ
				if (this.getSystemType().equals("ODI")) {
					// ��ɫ�ж� ������ͬ��ְλ�� �ſ��޸�
					/**
					 * String userRule = this.getWord().getFileLastEditUser();
					 * TParm ruleParm = new TParm(this.getDBTool().select("SELECT B.POS_TYPE FROM SYS_OPERATOR A,SYS_POSITION B WHERE A.POS_CODE=B.POS_CODE AND USER_ID='"
					 * + userRule + "'"));
					 *
					 * TParm ruleParmM = new TParm(this.getDBTool().select("SELECT B.POS_TYPE FROM SYS_OPERATOR A,SYS_POSITION B WHERE A.POS_CODE=B.POS_CODE AND USER_ID='"
					 * + Operator.getID() + "'"));
					 *
					 * if (ruleParm.getCount() > 0 && ruleParmM.getCount() > 0)
					 * { String posType = ruleParm.getValue("POS_TYPE", 0);
					 * String posTypeM = ruleParmM.getValue("POS_TYPE", 0); if
					 * (!posType.equals(posTypeM)) { this.messageBox("E0102");
					 * return; } }
					 **/
				} else {
					// ��סԺ
				}

			}
		}
		// �½�ģ��
		if (this.getOnlyEditType().equals("NEW")) {
			//�ɱ༭
			this.getWord().setCanEdit(true);
			//�༭״̬(������)
			this.getWord().onEditWord();
			setTMenuItem(true);

			return true; //modify by wanglong 20121205
		}
		// ��
		if (this.getOnlyEditType().equals("ONLYONE")) {
			// ��סԺϵͳ�Ľ�����������;
			if (this.getSystemType().equals("ODI") && !this.checkDrForEdit()) {
				// 3�������༭����Ȩ�޿��ƣ�
				this.messageBox("E0102"); // ��û�б༭Ȩ��
				this.getWord().onPreviewWord();
				return false; //modify by wanglong 20121205
			}
			if ("3".equals(this.ruleType)) {
				TParm emrChildParm = this.getEmrChildParm();
				if ("OIDR".equals(this.writeType)) {
					if ("N".equals(emrChildParm.getValue("OIDR_FLG"))) {
						this.callFunction("UI|save|setEnabled", false);
						this.messageBox("E0102");
						return false; //modify by wanglong 20121205
					}
				}
				if ("NSS".equals(this.writeType)) {
					if ("N".equals(emrChildParm.getValue("NSS_FLG"))) {
						this.callFunction("UI|save|setEnabled", false);
						this.messageBox("E0102");
						return false; //modify by wanglong 20121205
					}
				}
			}

			// �ɱ༭
			this.getWord().setCanEdit(true);
			// PIC
			this.setContainPic(false);
			// �༭״̬(������)
			this.getWord().onEditWord();
			setTMenuItem(true);

			if ("3".equals(this.ruleType)) {
				this.callFunction("UI|save|setEnabled", true);
			}
		}
		return true; //modify by wanglong 20121205
	}

	/**
	 * �Ƿ���ֵ��ҽ��
	 *
	 * @return boolean
	 */
	public boolean isDutyDrList() {
		boolean falg = false;
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_DUTYDRLIST WHERE DR_CODE='"
						+ Operator.getID() + "'"));
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * �Ƿ���ֵ��ҽ��
	 */
	public boolean isDutyDrList(String user) {
		boolean falg = false;
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_DUTYDRLIST WHERE STATION_CODE='"
						+ this.getStationCode() + "' AND DR_CODE='" + user
						+ "'"));
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * ȡ���༭
	 */
	public void onCancelEdit() {
		if (!this.getWord().canEdit()) {
			// �Ѿ���ȡ���༭״̬
			this.messageBox("E0104");
			return;
		}
		if (this.checkSave()) {
			// ���ɱ༭
			this.getWord().setCanEdit(false);
			setTMenuItem(false);
		}
	}

	/**
	 * �Ƿ�����������ҽʦ
	 */
	public boolean isCheckUserDr() {
		// �ж��Ƿ���ת��
		if (isAdmChg(this.getCaseNo())) {
			// ��ǰ������ҽʦ����
			this.setVsDrCode(this.getDr(this.getCaseNo(), this.getDeptCode(),
					"VS_DR_CODE"));
			// ����ҽʦ
			this.setAttendDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "ATTEND_DR_CODE"));
			// ������
			this.setDirectorDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "DIRECTOR_DR_CODE"));
			if (Operator.getID().equals(this.getVsDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				return true;
			}

			// ��ת�ƴ�ADM_INP���ж�
		} else {
			String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
					+ " FROM ADM_INP " + " WHERE CASE_NO = '"
					+ this.getCaseNo() + "'";

			TParm parm = new TParm(this.getDBTool().select(sql));
			// ����ҽʦ
			this.setVsDrCode(parm.getValue("VS_DR_CODE", 0));
			// ����ҽʦ
			this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE", 0));
			// ������
			this.setDirectorDrCode(parm.getValue("DIRECTOR_DR_CODE", 0));
			if (Operator.getID().equals(parm.getValue("VS_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("ATTEND_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("DIRECTOR_DR_CODE", 0))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * ������
	 */
	public void loadTree() {
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		this.setValue("MR_NO", this.getMrNo());
		if ("en".equals(this.getLanguage()) && patEnName != null
				&& patEnName.length() > 0) {
			this.setValue("PAT_NAME", this.patEnName);
		} else {
			this.setValue("PAT_NAME", this.getPatName());
		}
		boolean queryFlg = false;
		// סԺ
		if (this.getSystemType().equals("ODI")) {
			this.setValue("IPD_NO", this.getIpdNo());
			// �õ�����
			treeRoot.setText("סԺ");
			treeRoot.setEnText("Hospital");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			isCheckUserDr();
		}
		// ����
		if (this.getSystemType().equals("ODO")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("����");
			treeRoot.setEnText("Outpatient");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// ����
		if (this.getSystemType().equals("EMG")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("����");
			treeRoot.setEnText("Emergency");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("E")) {
				queryFlg = true;
			}
		}
		// �������
		if (this.getSystemType().equals("HRM")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("�������");
			treeRoot.setEnText("Health Check");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("H")) {
				queryFlg = true;
			}
		}
		// ���ﻤʿվ
		if (this.getSystemType().equals("ONW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("���ﻤʿվ");
			treeRoot.setEnText("Out-patient nurse station");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// סԺ��ʿվ
		if (this.getSystemType().equals("INW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("סԺ��ʿվ");
			treeRoot.setEnText("Hospital nurse station");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("I")) {
				queryFlg = true;
			}
		}
		// ��Ⱦ����
		if (this.getSystemType().equals("INF")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			// �õ�����
			treeRoot.setText("��Ⱦ����");
			treeRoot.setEnText("Infection Control");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// ����
		if (this.getSystemType().equals("OPE")) {//add by wanglong 20121220
			// �õ�����
			treeRoot.setText("������¼");
			treeRoot.setEnText("Op Record");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// ������
		if (this.getSystemType().equals("SD")) {//add by wanglong 20121120
			// �õ�����
			treeRoot.setText("������");
			treeRoot.setEnText("Single Disease");
			treeRoot.setType("Root");
			treeRoot.removeAllChildren();
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// �õ��ڵ�����
		TParm parm = this.getRootNodeData();
		if (parm.getInt("ACTION", "COUNT") == 0) {
			// û������EMRģ�壡�������ݿ�����
			this.messageBox("E0105");
			return;
		}
		int rowCount = parm.getInt("ACTION", "COUNT");
		for (int i = 0; i < rowCount; i++) {
			TParm mainParm = parm.getRow(i);
			// �ӽڵ� EMR_CLASS���е�STYLE�ֶ�
			TTreeNode node = new TTreeNode();
			node.setID(mainParm.getValue("CLASS_CODE"));
			node.setText(mainParm.getValue("CLASS_DESC"));
			node.setEnText(mainParm.getValue("ENG_DESC"));
			// ���ò˵���ʾ��ʽ
			node.setType("" + mainParm.getInt("CLASS_STYLE"));
			node.setGroup(mainParm.getValue("CLASS_CODE"));
			node.setValue(mainParm.getValue("SYS_PROGRAM"));
			node.setData(mainParm);

			// ��������
			treeRoot.add(node);

			// $$=============start add by lx ����ģ��������޸�; ================$$//
			TParm allChildsParm = new TParm();
			this.getAllChildsByParent(mainParm.getValue("CLASS_CODE"),
					allChildsParm);
			int childRowCount = allChildsParm.getCount();
			for (int j = 0; j < childRowCount; j++) {
				//TParm childDirParm = allChildsParm.getRow(j);

				TTreeNode childrenNode = new TTreeNode(allChildsParm.getValue(
						"CLASS_DESC", j), allChildsParm.getValue("CLASS_STYLE",
								j));
				childrenNode.setID(allChildsParm.getValue("CLASS_CODE", j));
				childrenNode.setGroup(allChildsParm.getValue("CLASS_CODE", j));
				// mainParm.getValue("SYS_PROGRAM")?????��ʱû��
				treeRoot.findNodeForID(
						allChildsParm.getValue("PARENT_CLASS_CODE", j)).add(
								childrenNode);

				// ��Ҷ�ڵ�ļ������ļ�
				if (((String) allChildsParm.getValue("LEAF_FLG", j))
						.equalsIgnoreCase("Y")) {
					//$$=====Add by lx 2012/10/10 start=======$$//
					TParm childParm=new TParm();
					//�ж��Ƿ����ٴ�֪ʶ�⣬
					//��������,�����Ӧ�Ķ�Ӧ��֪ʶ���ļ�
					if(isKnowLedgeStore(allChildsParm.getValue("CLASS_CODE", j))){

						childParm=getChildNodeDataByKnw(allChildsParm.getValue("CLASS_CODE", j));
						//$$=====Add by lx 2012/10/10 end=======$$//
					}else{
						// ��ѯ���ļ�
						childParm = this.getRootChildNodeData(
								allChildsParm.getValue("CLASS_CODE", j),
								queryFlg);
					}
					int rowCldCount = childParm.getInt("ACTION", "COUNT");
					for (int z = 0; z < rowCldCount; z++) {
						TParm chlidParmTemp = childParm.getRow(z);
						TTreeNode nodeChlid = new TTreeNode();
						nodeChlid
						.setID(chlidParmTemp.getValue("FILE_NAME"));
						nodeChlid.setText(chlidParmTemp
								.getValue("DESIGN_NAME"));
						nodeChlid.setType("4");
						nodeChlid.setGroup(chlidParmTemp
								.getValue("CLASS_CODE"));
						nodeChlid.setValue(chlidParmTemp
								.getValue("SUBCLASS_CODE"));
						nodeChlid.setData(chlidParmTemp);
						childrenNode.add(nodeChlid);
					}
				}

			}
			// $$=============end add by lx ����ģ��������޸�; ================$$//
		}
		this.getTTree(TREE_NAME).update();
	}

	/**
	 * �õ����ڵ�����
	 *
	 * @return TParm
	 */
	public TParm getRootNodeData() {
		TParm result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,A.CLASS_CODE,A.SYS_PROGRAM,A.SEQ "
								+ " FROM EMR_TREE A,EMR_CLASS B"
								+ " WHERE A.SYSTEM_CODE = '"+ this.getSystemType()
								+ "' AND A.CLASS_CODE=B.CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE"));
		return result;
	}

	/**
	 * �õ����ڵ�����
	 *
	 * @return TParm
	 */
	public TParm getRootChildNodeData(String classCode, boolean queryFlg) {
		TParm result = new TParm();
		String myTemp =
				// ����ģ��
				" AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
				// ����ģ��
				+ " OR (B.DEPT_CODE = '" + Operator.getDept()
				+ "' AND B.USER_ID IS NULL) "
				// ����ģ��
				+ " OR B.USER_ID = '" + Operator.getID() + "') ";
		String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";
		if (queryFlg) {
			if ("O".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("I".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("E".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("H".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.MR_NO='"
								+ this.getMrNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
		} else {
			if ("O".equals(this.getAdmType())) {

				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND OPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

			}
			if ("I".equals(this.getAdmType())) {

				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));

			}
			if ("E".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND EMG_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
			if ("H".equals(this.getAdmType())) {
				result = new TParm(
						this
						.getDBTool()
						.select(
								sql
								+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
								+ this.getCaseNo()
								+ "' AND A.CLASS_CODE='"
								+ classCode
								+ "' AND A.DISPOSAC_FLG<>'Y' AND HRM_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
								+ myTemp
								+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ"));
			}
		}
		return result;
	}

	/**
	 * ͨ��֪ʶ��Ҷ�ڵ�ȡ��Ӧ֪ʶ��ģ��
	 * @param classCode
	 * @return
	 */
	public TParm getChildNodeDataByKnw(String classCode){
		TParm result = new TParm();
		String sql = "SELECT   SUBCLASS_CODE, CLASS_CODE, EMT_FILENAME FILE_NAME,";
		sql += "TEMPLET_PATH FILE_PATH, SUBCLASS_DESC DESIGN_NAME ";
		sql += "FROM EMR_TEMPLET ";
		sql += "WHERE CLASS_CODE = '" + classCode + "' ";
		sql += "ORDER BY SEQ";
		result = new TParm(this.getDBTool().select(sql));
		return result;
	}

	/**
	 * �����
	 *
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {// wuh
		TTreeNode node = (TTreeNode) parm;
		if (null == node) return;
		// ��ʼ����׷�ӱ��
		this.setApplend(false);
		TParm emrParm = (TParm) node.getData();
		if (null != emrParm) {
			this.doEditEmr(parm, emrParm, node);
		}
		return;
	}

	/**
	 * �½�����
	 * @param emrParm
	 */
	private void doAddEmr(TTreeNode node) {//wuh
		// ���ֶ�дȨ�޹ܿ�
		this.setApplend(false);
		// 2�������½�����Ȩ�޿��ƣ�
		if (!this.checkDrForNew()) {
			this.messageBox("E0011"); // Ȩ�޲���
			return;
		}
		//
		TParm action = this.getTemplateData(node);
		// Ԥ����������
		String runProgarmName = action.getValue("RUN_PROGARM");

		TParm runParm = new TParm();
		// �ж������Ƿ���Դ�
		String styleClass = action.getValue("CLASS_STYLE");
		// ֻ���½�
		if ("1".equals(styleClass)) {
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());
				// ��Ժʱ��
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// Ψһֻ�ܽ���һ��
		if ("2".equals(styleClass)) {
			String subclassCode = action.getValue("SUBCLASS_CODE");
			TParm fileParm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG" +
									" FROM EMR_FILE_INDEX" +
									" WHERE CASE_NO='"
									+ this.getCaseNo()
									+ "' AND SUBCLASS_CODE='"
									+ subclassCode + "'"));
			if (fileParm.getCount() > 0) {
				// ���ļ��Ѿ����ڲ��������½�
				this.messageBox("E0106");
				return;
			}
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());

				// ��Ժʱ��
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// �ļ�����׷������ǰ����dd
		if ("3".equals(styleClass)) {
			// �õ�����ģ���ļ���
			String subtempletCode = action.getValue("SUBTEMPLET_CODE");
			String emtFileName = new TParm(this.getDBTool().select(
					"SELECT EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE ='"
							+ subtempletCode + "'"))
					.getValue("EMT_FILENAME", 0);

			// ��ǰģ��
			String subclassCode = action.getValue("SUBCLASS_CODE");
			if (subtempletCode.length() != 0) {
				// �鿴�����ļ������Ƿ����
				//���ڵ�ǰģ��;
				if (subclassCode!=null&&!subclassCode.equals("")) {
					// ���ļ�
					if (runProgarmName.length() != 0) {
						TParm ermParm = new TParm();
						ermParm.setData("TYPE", "2");
						ermParm.setData("FILE_NAME", action
								.getValue("SUBCLASS_DESC"));
						ermParm.setData("ADM_DATE", this.getAdmDate());
						ermParm.setData("CASE_NO", this.getCaseNo());
						ermParm.setData("SUBTEMPLET_FILE", emtFileName);

						// ��Ժʱ��
						TParm tem = this.getAdmInpDSData();
						if (tem != null) {
							ermParm.setData("DS_DATE", tem.getTimestamp(
									"DS_DATE", 0));
						} else {
							ermParm.setData("DS_DATE", "");
						}
						// �����ⲿ����;
						Object objParm = this.openDialog(runProgarmName,
								ermParm);

						if (objParm != null) {
							runParm = (TParm) objParm;
							//�������ļ���
							this.setSubFileName(runParm.getValue("FILE_NAME_MAIN"));

							//�������ļ�����ǰʱ��
							//1.��ģ���ļ�;
							String templetPath = action.getValue("TEMPLET_PATH");

							String templetName = action.getValue("EMT_FILENAME");
							try{
								openTempletNoEdit(templetPath, templetName, runParm);
							}catch (Exception e) {

							}
							this.setEmrChildParm(action);
							// ���ܳ�����
							this.setEmrChildParm(this.getFileServerEmrName());
							// ����������ӡ�������޸ı�ʶ�����أ�
							setCanPrintFlgAndModifyFlg("", "", false);
							//�����¼ʱ��
							EFixed fixedDate = this.getWord().findFixed(
									"DATETIME");

							if (fixedDate != null) {
								fixedDate.setText(runParm.getValue("EMRADD_DATE"));
							}
							//�Ƿ���׷�� FLG
							this.setApplend(true);
							this.getWord().update();
							//2.�����ļ������棻
							//3.�������ļ���this.word׷���µĲ��̼�¼(·�����ļ�);
						} else {
							return;
						}
					}
					return;
				} else {
					// ������δ��д�޷�����
					this.messageBox("E0108��");
					return;
				}
			} else {
				// ��ģ������û�����ø���ģ�������ú���д��
				this.messageBox("E0109");
				return;
			}
		}
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");
		//this.messageBox("===templetName==="+templetName);
		try{
			openTempletNoEdit(templetPath, templetName, runParm);
		}catch (Exception e) {

		}
		this.setEmrChildParm(action);
		// ���ܳ�����
		//this.setEmrChildParm(this.getFileServerEmrName());
		// ����������ӡ�������޸ı�ʶ�����أ�
		setCanPrintFlgAndModifyFlg("", "", false);
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}

	/**
	 * �õ�ģ����Ϣ
	 * @return
	 */
	private TParm getTemplateData(TTreeNode node) {//wuh
		String sql =
				" SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,"
						+ " EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,CLASS_STYLE AS TYPE "
						+ " FROM EMR_TEMPLET WHERE SUBCLASS_CODE='" + node.getValue() + " AND IPD_FLG='Y' ";
		if (this.getSystemType() != null && this.getSystemType().length() != 0) {
			sql += " AND (SYSTEM_CODE='" + this.getSystemType() + "' OR SYSTEM_CODE IS NULL)";
		}
		TParm emrParm = new TParm(TJDODBTool.getInstance().select(sql));
		emrParm = emrParm.getRow(0);
		emrParm.setData("SYSTEM_CODE", this.getSystemType());
		emrParm.setData("NODE_NAME", node.getText());
		emrParm.setData("DEPT_CODE", this.getDeptCode());
		return emrParm;
	}

	/**
	 * �༭����
	 * @param parm
	 * @param emrParm
	 * @param node
	 */
	private void doEditEmr(Object parm,TParm emrParm,TTreeNode node){//wuh
		if (emrParm != null && emrParm.getValue("REPORT_FLG").equals("Y")) {
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			// ���ò��ɱ༭
			this.getWord().setCanEdit(false);
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

		} else {
			//System.out.println("======˫����======"+emrParm);
			if (!this.checkInputObject(parm)) {
				return;
			}
			// 1.�����鿴Ȩ�޿��ƣ�
			if (!checkDrForOpen()) {
				this.messageBox("E0011"); // Ȩ�޲���
				return;
			}
			if (null != node) {//wuh
				if (!node.getType().equals("4")) {
					return;
				}
			}
			//=====add by lx 2012/10/10�������ٴ�֪ʶ�� ====start//
			if(this.isKnowLedgeStore(emrParm.getValue("SUBCLASS_CODE"))){
				//��֪ʶ��ģ��
				if (!this.getWord().onOpen(
						emrParm.getValue("FILE_PATH"),
						emrParm.getValue("FILE_NAME"), 2, false))
					// ���ò��ɱ༭
					this.getWord().setCanEdit(false);
				this.getTMenuItem("save").setEnabled(false);
				setTMenuItem(false);
				this.getWord().onPreviewWord();
				return;
			}
			//=====add by lx 2012/10/10�����ٴ�֪ʶ�� ====End//
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			setSingleDiseBasicData();//add by wanglong 20121115  ���õ����ֻ�����Ϣ
			setOPEData();//add by wanglong 20130514 ��������������Ϣ
			// ���ò��ɱ༭
			this.getWord().setCanEdit(false);

			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);
			// ���ú�
			setMicroField(false);
			TParm sexP = new TParm(this.getDBTool().select(
					"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
							+ this.getMrNo() + "'"));

			if (sexP.getInt("SEX_CODE", 0) == 9) {
				this.getWord().setSexControl(0);
			} else {
				this.getWord().setSexControl(sexP.getInt("SEX_CODE", 0));
			}
			// ���ñ༭״̬
			this.setOnlyEditType("ONLYONE");
			// ���õ�ǰ�༭����
			this.setEmrChildParm(emrParm);
			setTMenuItem(false);
			//$$===========add by lx 2012-06-18 ���벡���Ѿ��ύ�����޸�start===============$$//
			if(getSubmitFLG()){
				this.getWord().onPreviewWord();
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
				//this.messageBox("���ύ�����������޸�");
			}
			//$$===========add by lx 2012-06-18���벡���Ѿ��ύ�����޸� end===============$$//
			// ����༭
			else if (ruleType.equals("2") || ruleType.equals("3")) {
				onEdit();
				setCanEdit(emrParm);
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
			} else {
				this.getWord().onPreviewWord();
				LogOpt(emrParm);
				setPrintAndMfyFlg(emrParm);
			}
			
			// add by wangqing 20170817 start
			// ����ҽ��Ȩ��
			if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
				String tSql = " SELECT B.INTERNSHIP_FLG "
						+ "FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
						+ "WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE(+) AND A.CASE_NO = '"+caseNo+"' AND A.SUBCLASS_CODE = '"+emrParm.getValue("SUBCLASS_CODE")+"' ";
				TParm tResult = new TParm(TJDODBTool.getInstance().select(tSql));
				if(tResult.getCount()<=0 || tResult.getErrCode()<0){
					return;
				}
				if(tResult.getValue("INTERNSHIP_FLG", 0) != null && tResult.getValue("INTERNSHIP_FLG", 0).equals("N")){// ���ò��ɱ༭
					this.getWord().setCanEdit(false);
					this.messageBox("����ҽ�����ɱ༭������");
				}
			}		
			// add by wangqing 20170817 end

		}
	}

	/**
	 * �õ��û���ָ��ģ����Ϣ
	 * @param subClassCode
	 * @return
	 */
	private TParm getEmrData(String subClassCode) {//wuh
		String sql =
				"SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,"
						+ " A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
						+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
						+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
						+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,"
						+ " A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
						+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,"
						+ " B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG "
						+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B " + " WHERE A.MR_NO='"
						+ this.getMrNo() + "' AND A.SUBCLASS_CODE='" + subClassCode
						+ "' AND A.CASE_NO='" + this.getCaseNo()
						+ "' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE " + " ORDER BY A.FILE_SEQ DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * �õ��ڵ���Ϣ
	 * @param node
	 * @return
	 */
	private TParm getNodeData(TTreeNode node) {//wuh
		if (TREE_TYPE.equals(TREE_TYPE_1)) {
			return (TParm) node.getData();
		} else if (TREE_TYPE.equals(TREE_TYPE_2) || TREE_TYPE.equals(TREE_TYPE_3)) {
			return this.getEmrData(node.getValue());
		}
		return null;
	}

	/**
	 * ͨ������ѡ��������Ӧ��
	 */
	private void loadTreeByType() {//wuh
		if (TREE_TYPE.equals(TREE_TYPE_1)) {
			this.loadTree();
			return;
		} else if (TREE_TYPE.equals(TREE_TYPE_2) || TREE_TYPE.equals(TREE_TYPE_3)) {
			this.loadTreeNew();
			return;
		}
	}

	/**
	 * ��־��¼
	 */
	private void LogOpt(TParm emrParm) {
		// ��������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��
		if (this.isCheckUserDr() || this.isDutyDrList()) {
			OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "O", emrParm);
		}
		// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/����
		else {
			OptLogTool.getInstance().writeOptLog(
					this.getParameter(), "R", emrParm);
		}

	}

	/**
	 * ����������ӡ��ʶ�������޸ı�ʶ
	 *
	 * @param emrParm
	 *            TParm
	 */
	private void setPrintAndMfyFlg(TParm emrParm) {
		// ����������ӡ��ʶ�������޸ı�ʶ
		String caseNo = ((TParm) this.getParameter()).getValue("CASE_NO");
		String fileSeq = emrParm.getValue("FILE_SEQ");
		if (!this.checkDrForDel()) {
			setCanPrintFlgAndModifyFlg("", "", false);
		} else {
			setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
		}

	}

	private void setCanEdit(TParm emrParm) {
		int fileSeq = emrParm.getInt("FILE_SEQ");
		String subTempletCode = emrParm.getValue("SUBTEMPLET_CODE");
		String subClassCode = emrParm.getValue("SUBCLASS_CODE");
		String caseNo = emrParm.getValue("CASE_NO");
		if (subTempletCode.length() != 0) {
			TParm result = new TParm(getDBTool().select(
					" SELECT MAX(FILE_SEQ) FILE_SEQ" + " FROM EMR_FILE_INDEX "
							+ " WHERE CASE_NO='" + caseNo + "' "
							+ " AND   SUBCLASS_CODE='" + subClassCode + "'"));
			if (fileSeq != result.getInt("FILE_SEQ", 0)) {
				this.getWord().setCanEdit(false);
				setTMenuItem(false);

			}
		} else {
			TParm result = new TParm(getDBTool().select(
					" SELECT B.FILE_SEQ"
							+ " FROM EMR_TEMPLET A,EMR_FILE_INDEX B "
							+ " WHERE A.SUBTEMPLET_CODE='" + subClassCode + "'"
							+ " AND   A.SUBCLASS_CODE = B.SUBCLASS_CODE"
							+ " AND   B.CASE_NO = '" + caseNo + "'"));
			if (result.getCount() > 0) {
				this.getWord().setCanEdit(false);
				setTMenuItem(false);

			}
		}
	}

	/**
	 * �õ���
	 *
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}

	/**
	 * �½�����(���)
	 */
	public void onCreatMenu() {
		// ���ֶ�дȨ�޹ܿ�
		openChildDialog();
		this.setOnlyEditType("NEW");
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}

	/**
	 * �򿪲���(Ψһ)
	 */
	public void onOpenMenu() {
		// ���ֶ�дȨ�޹ܿ�
		openChildDialog();
		this.setOnlyEditType("NEW");
	}

	/**
	 * ��������(����׷��)
	 */
	public void onAddMenu() {
		// ���ֶ�дȨ�޹ܿ�
		openChildDialog();
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}

	/**
	 * ���Ӵ���
	 */
	public void openChildDialog() {
		this.setApplend(false);
		// 2�������½�����Ȩ�޿��ƣ�
		if (!this.checkDrForNew()) {
			this.messageBox("E0011"); // Ȩ�޲���
			return;
		}
		/**
		 * if (!checkSave()) { return; }
		 **/

		// ģ������
		String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
		String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
		String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
		String programName = this.getTTree(TREE_NAME).getSelectNode()
				.getValue();

		// ����ǵ���HIS��������������
		if (programName.length() != 0) {
			TParm hisParm = new TParm();
			hisParm.setData("CASE_NO", this.getCaseNo());
			this.openDialog(programName, hisParm);
			return;
		}
		TParm parm = new TParm();
		String whereOtherStr = "";
		if (this.ruleType.equals("3") && "OIDR".equals(this.writeType)) {
			whereOtherStr += " AND OIDR_FLG='Y'";
		}
		if (this.ruleType.equals("3") && "NSS".equals(this.writeType)) {
			whereOtherStr += " AND NSS_FLG='Y'";
		}
		// add by wangqing 20170804  ��������
		if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
			whereOtherStr += " AND INTERNSHIP_FLG='Y'";
		}
		
		if (this.getSystemType() != null && this.getSystemType().length() != 0) {
			whereOtherStr += " AND (SYSTEM_CODE='" + this.getSystemType()
			+ "' OR SYSTEM_CODE IS NULL)";
		}
		String myTemp =
				// ����ģ��
				" AND ((DEPT_CODE IS NULL AND USER_ID IS NULL) "
				// ����ģ��
				+ " OR (DEPT_CODE = '" + Operator.getDept()
				+ "' AND USER_ID IS NULL) "
				// ����ģ��
				+ " OR USER_ID = '" + Operator.getID() + "') ";
		// סԺ
		if (this.getAdmType().equals("I")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND IPD_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));

			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		// ����
		if (this.getAdmType().equals("O")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND OPD_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		// ����
		if (this.getAdmType().equals("E")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND EMG_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		if (this.getAdmType().equals("H")) {
			parm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
									" FROM EMR_TEMPLET" +
									" WHERE CLASS_CODE='"
									+ emrClass
									+ "' AND HRM_FLG='Y' "
									+ whereOtherStr
									+ myTemp
									+ " ORDER BY SEQ"));
			parm.setData("SYSTEM_CODE", this.getSystemType());
			parm.setData("NODE_NAME", nodeName);
			parm.setData("TYPE", emrType);
			parm.setData("DEPT_CODE", this.getDeptCode());
		}
		Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x", parm);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}

		TParm action = (TParm) obj;
		// Ԥ����������
		String runProgarmName = action.getValue("RUN_PROGARM");

		TParm runParm = new TParm();
		// �ж������Ƿ���Դ�
		String styleClass = action.getValue("CLASS_STYLE");
		// ֻ���½�
		if ("1".equals(styleClass)) {
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());
				// ��Ժʱ��
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// Ψһֻ�ܽ���һ��
		if ("2".equals(styleClass)) {
			String subclassCode = action.getValue("SUBCLASS_CODE");
			TParm fileParm = new TParm(
					this
					.getDBTool()
					.select(
							"SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG" +
									" FROM EMR_FILE_INDEX" +
									" WHERE CASE_NO='"
									+ this.getCaseNo()
									+ "' AND SUBCLASS_CODE='"
									+ subclassCode + "'"));
			if (fileParm.getCount() > 0) {
				// ���ļ��Ѿ����ڲ��������½�
				this.messageBox("E0106");
				return;
			}
			if (runProgarmName.length() != 0) {
				TParm ermParm = new TParm();
				ermParm.setData("TYPE", "1");
				ermParm.setData("FILE_NAME", action.getValue("SUBCLASS_DESC"));
				ermParm.setData("ADM_DATE", this.getAdmDate());
				ermParm.setData("CASE_NO", this.getCaseNo());

				// ��Ժʱ��
				TParm tem = this.getAdmInpDSData();
				if (tem != null) {
					ermParm.setData("DS_DATE", tem.getTimestamp("DS_DATE", 0));
				} else {
					ermParm.setData("DS_DATE", "");
				}
				Object objParm = this.openDialog(runProgarmName, ermParm);
				if (objParm != null) {
					runParm = (TParm) objParm;
				} else {
					return;
				}
			}
		}
		// �ļ�����׷������ǰ����dd
		if ("3".equals(styleClass)) {
			// �õ�����ģ���ļ���
			String subtempletCode = action.getValue("SUBTEMPLET_CODE");
			String emtFileName = new TParm(this.getDBTool().select(
					"SELECT EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE ='"
							+ subtempletCode + "'"))
					.getValue("EMT_FILENAME", 0);

			// ��ǰģ��
			String subclassCode = action.getValue("SUBCLASS_CODE");

			if (subtempletCode.length() != 0) {
				// �鿴�����ļ������Ƿ����
				//���ڵ�ǰģ��;
				if (subclassCode!=null&&!subclassCode.equals("")) {
					// ���ļ�
					if (runProgarmName.length() != 0) {
						TParm ermParm = new TParm();
						ermParm.setData("TYPE", "2");
						ermParm.setData("FILE_NAME", action
								.getValue("SUBCLASS_DESC"));
						ermParm.setData("ADM_DATE", this.getAdmDate());
						ermParm.setData("CASE_NO", this.getCaseNo());
						ermParm.setData("SUBTEMPLET_FILE", emtFileName);

						// ��Ժʱ��
						TParm tem = this.getAdmInpDSData();
						if (tem != null) {
							ermParm.setData("DS_DATE", tem.getTimestamp(
									"DS_DATE", 0));
						} else {
							ermParm.setData("DS_DATE", "");
						}

						// �����ⲿ����;
						Object objParm = this.openDialog(runProgarmName,
								ermParm);

						if (objParm != null) {
							runParm = (TParm) objParm;
							//�������ļ���
							this.setSubFileName(runParm.getValue("FILE_NAME_MAIN"));

							//�������ļ�����ǰʱ��
							//1.��ģ���ļ�;
							String templetPath = action.getValue("TEMPLET_PATH");

							String templetName = action.getValue("EMT_FILENAME");
							try{
								openTempletNoEdit(templetPath, templetName, runParm);
							}catch (Exception e) {

							}
							this.setEmrChildParm(action);
							// ���ܳ�����
							this.setEmrChildParm(this.getFileServerEmrName());
							// ����������ӡ�������޸ı�ʶ�����أ�
							setCanPrintFlgAndModifyFlg("", "", false);
							//�����¼ʱ��
							EFixed fixedDate = this.getWord().findFixed(
									"DATETIME");

							if (fixedDate != null) {
								fixedDate.setText(runParm.getValue("EMRADD_DATE"));
							}

							//�Ƿ���׷�� FLG
							this.setApplend(true);
							this.getWord().update();
							//2.�����ļ������棻
							//3.�������ļ���this.word׷���µĲ��̼�¼(·�����ļ�);
						} else {
							return;
						}
					}
					return;
				} else {
					// ������δ��д�޷�����
					this.messageBox("E0108��");
					return;
				}
			} else {
				// ��ģ������û�����ø���ģ�������ú���д��
				this.messageBox("E0109");
				return;
			}
		}
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");
		//this.messageBox("===templetName==="+templetName);
		try{
			openTempletNoEdit(templetPath, templetName, runParm);
		}catch (Exception e) {

		}
		this.setEmrChildParm(action);
		// ���ܳ�����
		//this.setEmrChildParm(this.getFileServerEmrName());
		// ����������ӡ�������޸ı�ʶ�����أ�
		setCanPrintFlgAndModifyFlg("", "", false);

	}

	/**
	 * �򿪲���
	 *
	 * @param parm
	 *            Object
	 */
	public void onOpenEmrFile(TParm parm) {
		if (parm == null) {
			return;
		}
		// ��������
		if ("ODI".equals(this.getSystemType())) {
			// �Ƿ��в鿴��Ȩ��
			if (!isCheckUserDr() && !this.isDutyDrList()
					&& !"2".equals(this.ruleType) && !"3".equals(this.ruleType)) {
				// Ȩ�޲���
				this.messageBox("E0011");
				return;
			}
		}
		// �򿪲���
		if (!this.getWord().onOpen(parm.getValue("FILE_PATH"),
				parm.getValue("FILE_NAME"), 3, false)) {
			return;
		}
		// ���ò��ɱ༭
		this.getWord().setCanEdit(false);
		TParm allParm = new TParm();
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		// ���ú�
		if (!parm.getBoolean("FLG")) {
			setMicroField(true);
		}
		// ���ñ༭״̬
		this.setOnlyEditType("NEW");
		// ���õ�ǰ�༭����
		this.setEmrChildParm(parm);
		setTMenuItem(false);

		// ����༭
		if (ruleType.equals("2") || ruleType.equals("3")) {
			onEdit();
		}
	}

	/**
	 * �ж��Ƿ�Ҫ�����ļ�
	 *
	 * @param fileParm
	 *            TParm
	 * @param subclassCode
	 *            String
	 * @return boolean
	 */
	public boolean isEmrCreatFile(TParm fileParm, String subclassCode) {
		boolean falg = true;
		int rowCount = fileParm.getCount();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = fileParm.getRow(i);
			if (subclassCode.equals(temp.getValue("SUBCLASS_CODE"))) {
				falg = false;
				break;
			}
		}
		return falg;
	}

	/**
	 * �õ���Ժʱ��
	 *
	 * @return TParm
	 */
	public TParm getAdmInpDSData() {
		TParm admParm = new TParm(this.getDBTool().select(
				"SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (admParm.getCount() < 0) {
			return null;
		}
		return admParm;
	}

	/**
	 * ɾ������
	 */
	public void onDelFile() {
		// ����ѯ��ɾ����
		if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
			TTreeNode node = this.getTTree(TREE_NAME).getSelectionNode();

			//TParm fileData = (TParm) node.getData();//wuh delete
			TParm fileData = this.getNodeData(node);//wuh

			// 6������ɾ��Ȩ�޿��ƣ�
			if (!this.checkDrForDel()) {
				this.messageBox("E0107"); // ������ɾ��
				return;
			}
			// ����ɾ���ļ��Ĵ���
			try {
				// ɾ���ļ�
				delFileTempletFile(fileData.getValue("FILE_PATH"), fileData
						.getValue("FILE_NAME"));
			} catch (Exception e) {

			}
			// ɾ�����ݿ�����
			Map del = this.getDBTool().update(
					"DELETE EMR_FILE_INDEX WHERE CASE_NO='"
							+ fileData.getValue("CASE_NO") + "' AND FILE_SEQ='"
							+ fileData.getValue("FILE_SEQ") + "'");
			TParm delParm = new TParm(del);
			if (delParm.getErrCode() < 0) {
				// ɾ��ʧ��
				this.messageBox("E0003");
				this.setOnlyEditType("NEW");
				return;
			}
			// ɾ���ɹ�
			this.messageBox("P0003");
			this.getWord().onNewFile();
			this.setOnlyEditType("NEW");
			this.getWord().setCanEdit(false);
			// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/ɾ��
			OptLogTool.getInstance().writeOptLog(this.getParameter(), "D", fileData);

			// ������
			//   loadTree();//wuh delete
			this.loadTreeByType();//wuh

		} else {
			return;
		}
	}

	/**
	 * Ƭ��
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * ����ģ��Ƭ��
	 */
	public void onInsertTemplatePY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.setData("TWORD", this.getWord());
		inParm.addListener("onReturnTemplateContent", this,
				"onReturnTemplateContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm,
				true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);

	}

	public void onReturnTemplateContent(TWord templateWord) {
		this.word.onPaste();
	}

	/**
	 * Ƭ���¼
	 *
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		if (!this.getWord().pasteString(value)) {
			// ִ��ʧ��
			this.messageBox("E0005");
		}
	}

	/**
	 * �ٴ�����
	 */
	public void onInsertLCSJ() {
		if (!word.canEdit()) {
			messageBox("��ѡ����ģ��!");
			return;
		} 
		TParm inParm = new TParm();
		inParm.setData("ADM_TYPE", this.getAdmType());//wanglong 20140908
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		//		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		//		window.setY(0);
		TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
		window.setX(wordPanel.getX() + 10);
		window.setY(130);
		window.setVisible(true);
		AWTUtilities.setWindowOpacity(window, 0.8f);
	}

	/**
	 * ɾ�������ļ�
	 *
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 * @return boolean
	 */
	public boolean delFileTempletFile(String templetPath, String templetName) {
		// Ŀ¼����һ����Ŀ¼FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// ģ��·��������
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		// �õ�SocketͨѶ����
		TSocket socket = TIOM_FileServer.getSocket();

		// ɾ���ļ�
		boolean isDelFile = TIOM_FileServer.deleteFile(socket, rootName
				+ templetPathSer + templetPath + "\\" + templetName + ".jhw");

		// ɾ��ͼƬ�ļ����е��ϴ�ͼƬ;
		String dir = rootName + templetPathSer + templetPath + "\\"
				+ templetName + "\\";
		String s[] = TIOM_FileServer.listFile(socket, dir);
		if (s != null) {
			for (int i = 0; i < s.length; i++) {
				String path = dir + "\\" + s[i];
				TIOM_FileServer.deleteFile(socket, path);
			}
		}
		if (isDelFile) {
			return true;
		}

		return false;
	}

	/**
	 * ��ģ��
	 *
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 */
	public void openTempletNoEdit(String templetPath, String templetName,
			TParm parm) {
		// �½�����޸ı�־;
		this.getWord().onOpen(templetPath, templetName, 2, false);
		TParm allParm = new TParm();
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		// add by wangb 2015/12/17 ��������ͬ���������Ӳ�����������
		allParm.setData("MR_NO_BAR_CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		// ���ú�
		setMicroField(true);
		String comlunName[] = parm.getNames();
		for (String temp : comlunName) {
			this.getWord().setMicroField(temp, parm.getValue(temp));
			this.setCaptureValueArray(temp, parm.getValue(temp));
		}
		setSingleDiseBasicData();//add by wanglong 20121115  ���õ����ֻ�����Ϣ
		setOPEData();//add by wanglong 20130514 ��������������Ϣ
		// ���ñ༭״̬
		this.setOnlyEditType("NEW");
		// ���ò����Ա༭
		this.getWord().setCanEdit(false);
		setTMenuItem(false);

		// ����༭
		if (ruleType.equals("2") || ruleType.equals("3")) {
			onEdit();
		}
	}

	/**
	 * ���ú�
	 */
	public void setMicroField(boolean falg) {
		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");

		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// ȡ�ò��˻�������ʧ��
			this.messageBox("E0110");
			return;
		}

		Timestamp tempBirth = parm.getValue("��������", 0).length() == 0 ? SystemTool
				.getInstance().getDate()
				: StringTool.getTimestamp(parm.getValue("��������", 0),
						"yyyy-MM-dd");
				// ��������
				String age = "0";
				if (this.getAdmDate() != null) {
					age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
				} else {
					age = "";
				}
				String dateStr = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd HH:mm:ss");
				parm.addData("����", age);
				parm.addData("�����", this.getCaseNo());
				parm.addData("������", this.getMrNo());
				parm.addData("סԺ��", this.getIpdNo());
				parm.addData("����", this.getDeptDesc(Operator.getDept()));
				parm.addData("������", Operator.getName());
				parm.addData("��������", dateStr);
				parm.addData("����", StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd"));
				parm.addData("ʱ��", StringTool.getString(SystemTool.getInstance()
						.getDate(), "HH:mm:ss"));
				// parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
				parm.addData("����ʱ��", dateStr);
				parm.addData("��Ժʱ��", StringTool.getString(this.getAdmDate(),
						"yyyy/MM/dd HH:mm:ss"));

				parm.addData("��Ժʱ��", StringTool.getString(new java.sql.Timestamp(System
						.currentTimeMillis()), "yyyy/MM/dd"));

				parm.addData("���ÿ���", this.getDeptDesc(this.getDeptCode()));
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "�����");
				parm.addData("SYSTEM", "COLUMNS", "������");
				parm.addData("SYSTEM", "COLUMNS", "סԺ��");
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "������");
				parm.addData("SYSTEM", "COLUMNS", "��������");
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "ʱ��");
				// parm.addData("SYSTEM","COLUMNS","EMR_DATE");
				parm.addData("SYSTEM", "COLUMNS", "����ʱ��");
				parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
				parm.addData("SYSTEM", "COLUMNS", "���ÿ���");
				//		parm.addData("SYSTEM", "COLUMNS", "��ɸ���");// ===pangben 2015-11-4
				//		parm.addData("SYSTEM", "COLUMNS", "��ɸ�������");// ===pangben 2015-11-4
				// ��ѯסԺ������Ϣ(���ţ�סԺ���)
				TParm odiParm = new TParm(this.getDBTool().select(
						"SELECT * FROM MACRO_ADMINP_VIEW WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));

				if (odiParm.getCount() > 0) {
					for (String parmName : odiParm.getNames()) {
						parm.addData(parmName, odiParm.getValue(parmName, 0));
					}

				} else {
					for (String parmName : odiParm.getNames()) {
						parm.addData(parmName, "");
					}

				}

				// $$====add by lx 2012/02/21 סԺԤԼ start======$$//

				// ��ѯԤԼ��ص�ֵ����ͼ;
				TParm admRsvParm = new TParm(this.getDBTool().select(
						"SELECT * FROM ADM_RESV_VIEW WHERE RESV_NO='"
								+ this.getCaseNo() // ===== chenxi modify
								+ "'"));

				if (admRsvParm.getCount() > 0) {
					for (String parmName : admRsvParm.getNames()) {
						parm.addData(parmName, admRsvParm.getValue(parmName, 0));
					}
				} else {
					for (String parmName : admRsvParm.getNames()) {
						parm.addData(parmName, "");
					}
				}
				// $$=====add by lx 2012/02/21 end =====$$//

				// סԺ���
				if ("ODI".equals(this.getSystemType())
						|| "INW".equals(this.getSystemType())) {
					parm.addData("�ż�ס��", "סԺ");
					String sql = "SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.IO_TYPE,A.MAINDIAG_FLG,A.ICD_TYPE"
							+ " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B"
							+ " WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='"
							+ this.getCaseNo() + "' ORDER BY MAINDIAG_FLG DESC";
					TParm odiDiagParm = new TParm(this.getDBTool().select(sql));
					// ������Ժ���
					StringBuffer inDiagAll = new StringBuffer();
					int inDiags = 1;
					// ���г�Ժ���
					StringBuffer diag = new StringBuffer();
					int diags = 1;
					if (odiDiagParm.getCount() > 0) {
						int rowCount = odiDiagParm.getCount();
						for (int i = 0; i < rowCount; i++) {
							TParm temp = odiDiagParm.getRow(i);
							// �ż������
							if ("I".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("�ż������", temp.getValue("ICD_CHN_DESC"));
								inDiagAll.append("�ż������:"
										+ temp.getValue("ICD_CHN_DESC") + ",");

							}
							// ��Ժ���
							if ("M".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("��Ժ���", temp.getValue("ICD_CHN_DESC"));
								inDiagAll.append("��Ժ�����:"
										+ temp.getValue("ICD_CHN_DESC") + ",");
							}
							// add
							if ("M".equals(temp.getValue("IO_TYPE"))
									&& "N".equals(temp.getValue("MAINDIAG_FLG"))) {
								inDiagAll.append("��Ժ�������" + inDiags + ":"
										+ temp.getValue("ICD_CHN_DESC") + ",");
								inDiags++;
							}

							// ��Ժ���
							if ("O".equals(temp.getValue("IO_TYPE"))
									&& "Y".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("��Ժ���", temp.getValue("ICD_CHN_DESC"));

								diag.append("��Ժ�����:" + temp.getValue("ICD_CHN_DESC")
								+ ",");
							}
							if ("O".equals(temp.getValue("IO_TYPE"))
									&& "N".equals(temp.getValue("MAINDIAG_FLG"))) {
								parm.addData("��Ժ��ϸ���", temp.getValue("ICD_CHN_DESC"));
								diag.append("��Ժ��ϸ���" + diags + ":"
										+ temp.getValue("ICD_CHN_DESC") + ",");
								diags++;
							}
						}

						parm.addData("��Ժ�������", diag.toString());
						parm.addData("��Ժ�������", inDiagAll.toString());
					} else {
						parm.addData("�ż������", "");
						// parm.addData("��Ժ���", "");
						parm.addData("��Ժ���", "");

						parm.addData("��Ժ�������", "");
						parm.addData("��Ժ�������", "");
					}

					// ����סԺ����
					if (this.getAdmDate() != null) {
						// ==============modify by wanglong 20121128
						Timestamp nowDate = SystemTool.getInstance().getDate();
						Timestamp admDate = this.getAdmDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// ��ʽ��������
						String strNowDate = sdf.format(nowDate);
						String strAdmDate = sdf.format(admDate);
						nowDate = java.sql.Timestamp.valueOf(strNowDate
								+ " 00:00:00.000");
						admDate = java.sql.Timestamp.valueOf(strAdmDate
								+ " 00:00:00.000");
						int inDays = StringTool.getDateDiffer(nowDate, admDate);
						// ==============modify end
						parm.setData("סԺ����", 0, inDays == 0 ? "1" : inDays + " ��");
					} else {
						parm.setData("סԺ����", 0, 1);
					}

					parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
					parm.addData("SYSTEM", "COLUMNS", "����");
					parm.addData("SYSTEM", "COLUMNS", "����");
					parm.addData("SYSTEM", "COLUMNS", "סԺ�������");
					parm.addData("SYSTEM", "COLUMNS", "�ż������");
					parm.addData("SYSTEM", "COLUMNS", "��Ժ���");
					parm.addData("SYSTEM", "COLUMNS", "��Ժ���");
					parm.addData("SYSTEM", "COLUMNS", "��Ժ��ϸ���");
					parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
					parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
					parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
					parm.addData("SYSTEM", "COLUMNS", "����ҽʦ");
					parm.addData("SYSTEM", "COLUMNS", "סԺ����");
				}
				// �������
				if ("ODO".equals(this.getSystemType())
						|| "EMG".equals(this.getSystemType())) {
					parm.addData("�ż�ס��", "����");
					// ���������
					StringBuffer mainDiag = new StringBuffer();
					// �����������
					StringBuffer DiagAll = new StringBuffer();
					// ������ϸ���
					int odoDiagAllCount = 1;
					// ���ﱾ���������(CASE_NO����ҽ�����)
					// ���ﱾ���������(CASE_NO)
					TParm odoDiagParm = new TParm(this.getDBTool().select(
							"SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.ICD_TYPE,A.MAIN_DIAG_FLG"
									+ " FROM OPD_DIAGREC A,SYS_DIAGNOSIS B"
									+ " WHERE A.ICD_CODE=B.ICD_CODE AND CASE_NO='"
									+ this.getCaseNo() + "'"));
					if (odoDiagParm.getCount() > 0) {
						int rowCount = odoDiagParm.getCount();
						for (int i = 0; i < rowCount; i++) {
							TParm temp = odoDiagParm.getRow(i);
							if ("Y".equals(temp.getValue("MAIN_DIAG_FLG"))) {
								if ("W".equals(temp.getValue("ICD_TYPE"))) {
									mainDiag.append("��ҽ�����:"
											+ temp.getValue("ICD_CHN_DESC") + "|");
								}
								if ("C".equals(temp.getValue("ICD_TYPE"))) {
									mainDiag.append("��ҽ�����:"
											+ temp.getValue("ICD_CHN_DESC") + "|");
								}
							} else {
								DiagAll.append("�������" + odoDiagAllCount + ":"
										+ temp.getValue("ICD_CHN_DESC") + "|");
								odoDiagAllCount++;
							}
						}
						parm.addData("�������", mainDiag.toString());
						parm
						.addData("�����������", mainDiag.toString()
								+ DiagAll.toString());
					} else {
						parm.addData("�������", "");
						parm.addData("�����������", "");
					}
					parm.addData("������", diseDesc);// add by wanglong 20121025
					// Ϊ����סԺ֤����ʾ������
					parm.addData("ԤԼ����", bedNo); // add by chenxi 20130308 ԤԼ����
					parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
					parm.addData("SYSTEM", "COLUMNS", "�������");
					parm.addData("SYSTEM", "COLUMNS", "�����������");
				}
				// ���ﲿ��
				if ("EMG".equals(this.getSystemType())) {
					parm.addData("�ż�ס��", "����");
					parm.addData("ԤԼ����", bedNo); // add by chenxi 20130308 ԤԼ����
					parm.addData("������", diseDesc);// add by wanglong 20121025
					// Ϊ����סԺ֤����ʾ������
					parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
				}
				// �������
				if ("HRM".equals(this.getSystemType())) {
					parm.addData("�ż�ס��", "�������");
					parm.addData("SYSTEM", "COLUMNS", "�ż�ס��");
				}

				// ������Ϣ
				// ����ʷ(MR_NO)
				// �ݴӿ���ȡ���� �Ĺ��ú�(����Ӱ���ٶ�,���һ������);
				StringBuffer drugStr = new StringBuffer();
				TParm drugParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT A.CASE_NO,A.MR_NO,CASE A.DRUG_TYPE "
										+ " WHEN 'A' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='PHA_INGREDIENT' AND B.ID=A.DRUGORINGRD_CODE) "
										+ " WHEN 'B' THEN (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'C' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='SYS_ALLERGYTYPE' AND B.ID=A.DRUGORINGRD_CODE) "
										+ " WHEN 'D' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'E' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE) "
										+ " WHEN 'N' THEN '��' "
										+ " ELSE '' END AS ALLERGY_NAME,OPT_DATE  "
										+ " FROM OPD_DRUGALLERGY A "
										+ " WHERE A.MR_NO='" + this.getMrNo()
										+ "'" + " ORDER BY A.ADM_DATE,A.OPT_DATE "));
				if (drugParm.getCount() > 0) {
					drugStr.append("��������:");
					int rowCount = drugParm.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm temp = drugParm.getRow(i);
						drugStr.append(temp.getValue("ALLERGY_NAME") + ",");
					}
					String allergy = drugStr.toString();
					allergy = allergy.substring(0, allergy.length() - 1);
					parm.addData("����ʷ", allergy);
				} else {
					parm.addData("����ʷ", "-");
				}
				parm.addData("SYSTEM", "COLUMNS", "����ʷ");
				// ���ο������������ֲ�ʷ(CASE_NO)
				TParm subjParm = new TParm(this.getDBTool().select(
						"SELECT SUBJ_TEXT,OBJ_TEXT FROM OPD_SUBJREC WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				if (subjParm.getCount() > 0) {
					parm.addData("�����ֲ�ʷ", subjParm.getValue("SUBJ_TEXT", 0) + ";"
							+ subjParm.getValue("OBJ_TEXT", 0));
				} else {
					parm.addData("�����ֲ�ʷ", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "�����ֲ�ʷ");
				// ����ʷ(MR_NO)
				StringBuffer medhisStr = new StringBuffer();
				TParm medhisParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT B.ICD_CHN_DESC,A.DESCRIPTION FROM OPD_MEDHISTORY A,SYS_DIAGNOSIS B WHERE A.MR_NO='"
										+ this.getMrNo()
										+ "' AND A.ICD_CODE=B.ICD_CODE"));
				if (medhisParm.getCount() > 0) {
					int rowCount = medhisParm.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm temp = medhisParm.getRow(i);
						medhisStr
						.append(temp.getValue("ICD_CHN_DESC").length() != 0 ? temp
								.getValue("ICD_CHN_DESC")
								: "" + temp.getValue("DESCRIPTION"));
					}
					parm.addData("����ʷ", medhisStr.toString());
				} else {
					parm.addData("����ʷ", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "����ʷ");
				// modify by wangb 2015/11/6 ��������ͳһ��MACRO_PHYSIDX_VIEW��ͼ�ṩ
				/*// ����
		TParm sumParm = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE FROM SUM_INFO_VIEW WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
		if (sumParm.getCount() > 0) {
			parm.addData("����", sumParm.getValue("TEMPERATURE", 0));
			parm.addData("����", sumParm.getValue("PLUSE", 0));
			parm.addData("����", sumParm.getValue("RESPIRE", 0));
			parm.addData("����ѹ", sumParm.getValue("SYSTOLICPRESSURE", 0));
			parm.addData("����ѹ", sumParm.getValue("DIASTOLICPRESSURE", 0));
		} else {
			parm.addData("����", "");
			parm.addData("����", "");
			parm.addData("����", "");
			parm.addData("����ѹ", "");
			parm.addData("����ѹ", "");
		}
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "����");
		parm.addData("SYSTEM", "COLUMNS", "����ѹ");
		parm.addData("SYSTEM", "COLUMNS", "����ѹ");*/
				// �����쳣====pangben 2015-11-4
				TParm lisDParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT TESTITEM_CHN_DESC,TEST_VALUE,UPPE_LIMIT,LOWER_LIMIT FROM MED_LIS_DEVIANT_VIEW WHERE CASE_NO='"
										+ this.getCaseNo() + "' "));
				// System.out.println("lisDParm�����ң�����"+lisDParm);
				if (lisDParm.getCount() > 0) {
					StringBuffer lisString = new StringBuffer();
					lisString.append("��Ŀ:");
					for (int i = 0; i < lisDParm.getCount(); i++) {
						if (i != 0) {
							lisString.append("    "
									+ lisDParm.getValue("TESTITEM_CHN_DESC", i)
									+ ",���ֵ:" + lisDParm.getValue("TEST_VALUE", i));
						} else {
							lisString.append(lisDParm.getValue("TESTITEM_CHN_DESC", i)
									+ ",���ֵ:" + lisDParm.getValue("TEST_VALUE", i));
						}

					}
					parm.addData("�����쳣ֵ", lisString.toString());
				} else {
					parm.addData("�����쳣ֵ", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "�����쳣ֵ");
				// ����쳣====pangben 2015-11-4
				TParm pasDParm = new TParm(this.getDBTool().select(
						"SELECT OUTCOME_CONCLUSION FROM MED_RIS_DEVIANT_VIEW WHERE CASE_NO='"
								+ this.getCaseNo() + "' "));
				// System.out.println("pasDParmdddfd������"+pasDParm);
				if (pasDParm.getCount() > 0) {
					StringBuffer lisString = new StringBuffer();
					lisString.append("�����:");
					for (int i = 0; i < pasDParm.getCount(); i++) {
						lisString.append(pasDParm.getValue("OUTCOME_CONCLUSION", i)
								+ "\n");
					}
					parm.addData("����쳣ֵ", lisString.toString());
				} else {
					parm.addData("����쳣ֵ", "");
				}
				parm.addData("SYSTEM", "COLUMNS", "����쳣ֵ");
				// add by wangb 2015/11/4 �������б� START
				// ��ѯ�������б���ͼ
				List<String> macroNameList = new ArrayList<String>();
				String sql = "SELECT MACRO_NAME,MACRO_VALUE,INFECT_FLG FROM MACRO_PHYSIDX_VIEW A, EMR_MICRO_CONVERT B WHERE CASE_NO = '"
						+ this.getCaseNo()
						+ "' AND A.MACRO_NAME = B.MICRO_NAME AND A.MACRO_CODE = B.MACRO_CODE ORDER BY EPISODE_DATE DESC";
				TParm macroViewParm = new TParm(this.getDBTool().select(sql));
				for (int i = 0; i < macroViewParm.getCount(); i++) {
					if (!macroNameList
							.contains(macroViewParm.getValue("MACRO_NAME", i))) {
						macroNameList.add(macroViewParm.getValue("MACRO_NAME", i));
						parm.addData(macroViewParm.getValue("MACRO_NAME", i),
								macroViewParm.getValue("MACRO_VALUE", i));
						parm.addData("SYSTEM", "COLUMNS", macroViewParm.getValue(
								"MACRO_NAME", i));
					}
				}
				// add by wangb 2015/11/4 �������б� END

				// add by wangb 2015/11/4 ��ɸ��� START
				this.setInfectResult(parm, macroViewParm);
				// add by wangb 2015/11/4 ��ɸ��� END

				String names[] = parm.getNames();
				TParm obj = (TParm) this.getWord().getFileManager().getParameter();
				TParm macroCodeParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT MICRO_NAME,HIS_ATTR,HIS_TABLE_NAME FROM EMR_MICRO_CONVERT WHERE CODE_FLG='Y'"));
				for (String temp : names) {
					// ��ֵ��־;
					boolean flag = false;
					// ѭ��������(�Դ��ŵĴ���)
					for (int j = 0; j < macroCodeParm.getCount(); j++) {
						// �ֵ����� P ���õ�,D�Զ���� �ֵ�;
						String dictionaryType = macroCodeParm.getValue("HIS_ATTR", j);
						// ��Ӧ�ı���;
						String tableName = macroCodeParm.getValue("HIS_TABLE_NAME", j);
						if (macroCodeParm.getValue("MICRO_NAME", j).equals(temp)) {
							if ("�Ա�".equals(temp)) {
								if (parm.getInt(temp, 0) == 9) {
									this.getWord().setSexControl(0);
								} else {
									// 1.�� 2.Ů
									this.getWord().setSexControl(parm.getInt(temp, 0));
								}
							}
							if (falg) {
								// ���ú��������ʾ��
								this.getWord()
								.setMicroFieldCode(
										temp,
										getDictionary(tableName, parm.getValue(
												temp, 0)),
										this.getEMRCode(dictionaryType,
												tableName, parm.getValue(temp,
														0)));

								// ��������Ԫ�� CODE
								hideElemMap.put(temp, this.getEMRCode(dictionaryType,
										tableName, parm.getValue(temp, 0)));

								// ����ץȡ��ֵ;
								this.setCaptureValueArray(temp, getDictionary(
										tableName, parm.getValue(temp, 0)));

								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));

							} else {
								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));
							}
							// �Ѹ�ֵ;
							flag = true;
							break;

						}
					}
					// �Ѿ���ֵ,����ѭ����һ����
					if (flag) {
						continue;
					}

					String tempValue = parm.getValue(temp, 0);
					if (tempValue == null) {
						continue;
					}
					if (falg) {
						this.getWord().setMicroField(temp, tempValue);
						this.setCaptureValueArray(temp, tempValue);
						// ��������Ԫ�� CODE
						hideElemMap.put(temp, tempValue);

						obj.setData(temp, "TEXT", tempValue);
					} else {
						obj.setData(temp, "TEXT", tempValue);
					}
				}

				this.setHiddenElements(obj);
				// ����ϵͳ��Ҫ����
				// �����ٴ�·��
				this.setCLPValue();
				// ���ô�λ֤��ֵ;
				this.setAdmResv(parm);

				this.getWord().setWordParameter(obj);

	}

	/**
	 * ������
	 */
	private EFixed fixed;

	private EMacroroutine macroroutine;

	/**
	 * �ṹ�������Ҽ�����
	 */
	public void onMouseRightPressed() {		
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		// �Ƿ�ɱ༭
		if(!this.onEdit()){ //modify by wanglong 20121205
			return;
		}
		// $$======Modified by lx 2012/02/23START=========$$//
		TTreeNode node = getTTree(TREE_NAME).getSelectionNode();
		// ����������
		if (node != null) {
			if (node.getType().equals("4")) {
				//TParm emrParm = (TParm) node.getData();//wuh delete
				TParm emrParm = this.getNodeData(node);//wuh
				setCanEdit(emrParm);
				
				// add by wangqing 20170817 start			
				// ����ҽ��Ȩ��
				if (this.ruleType.equals("3") && "T".equals(this.writeType)) {
					String tSql = " SELECT B.INTERNSHIP_FLG "
							+ "FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
							+ "WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE(+) AND A.CASE_NO = '"+caseNo+"' AND A.SUBCLASS_CODE = '"+emrParm.getValue("SUBCLASS_CODE")+"' ";
					TParm tResult = new TParm(TJDODBTool.getInstance().select(tSql));
					if(tResult.getCount()<=0 || tResult.getErrCode()<0){
						return;
					}
					if(tResult.getValue("INTERNSHIP_FLG", 0) != null && tResult.getValue("INTERNSHIP_FLG", 0).equals("N")){// ���ò��ɱ༭
						this.getWord().setCanEdit(false);
						this.messageBox("����ҽ�����ɱ༭������");
					}
				}
				// add by wangqing 20170817 end
						
			}
			// �޷��������
		} else {
			setCanEdit(this.getEmrChildParm());
		}

		// $$========Modified by lx 2012/02/23END=============$$//

		if (!this.getWord().canEdit()) {
			return;
		}

		// ץȡ��
		if (e instanceof ECapture) {
			return;
		}

		// ��
		if (e instanceof EFixed) {
			fixed = (EFixed) e;
			this.getWord().popupMenu(fixed.getName() + "�޸�,onModify", this);
		}
		// ͼƬ
		if (e instanceof EMacroroutine) {
			macroroutine = (EMacroroutine) e;
			this.getWord().popupMenu(
					macroroutine.getName() + "�༭,onModifyMacroroutine", this);

		}

	}

	/**
	 * ����ͼƬFileServer��Ӧȫ·��;
	 *
	 * @return String
	 */
	private String getImgFullPath() {
		// �ļ�������·��;
		// fileserver+/JHW/caseNo��λ/caseNo��/MR_NO/fileName/
		// Ŀ¼����һ����Ŀ¼FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// ģ��·��������
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		String filePath = this.getEmrChildParm().getValue("FILE_PATH");
		String foldName = this.getEmrChildParm().getValue("FILE_NAME");
		String fileFullPath = rootName + templetPathSer + filePath + "/"
				+ foldName;
		return fileFullPath;
	}

	/**
	 * ���ģ��·��
	 *
	 * @return String
	 */
	private String getTemplatePath() {
		// ģ��·��������
		String templetPathSer = TIOM_FileServer.getPath("EmrData");
		String filePath = this.getEmrChildParm().getValue("FILE_PATH");
		String foldName = this.getEmrChildParm().getValue("FILE_NAME");
		String templatePath = templetPathSer + filePath + "/" + foldName;
		templatePath = templatePath.replaceAll("\\\\", "/");
		return templatePath;
	}

	/**
	 * �ϴ�word�д������������༭��ͼƬ�ļ�;
	 *
	 * @return boolean
	 */
	private boolean uploadPictureToFileServer() {
		boolean flag = true;
		// ����word�ļ���picԪ��������Ǳ�����ʱ·���ģ����ϴ���fileServer.
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// this.messageBox("EPanel size" + ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				//
				for (int k = 0; k < ePanel.getBlockSize(); k++) {
					IBlock block = (IBlock) ePanel.get(k);
					if (block != null) {
						if (block.getObjectType() == EComponent.TABLE_TYPE) {
							// System.out.println("��������Ԫ��.....");
							for (int row = 0; row < ((ETable) block)
									.getComponentList().size(); row++) {
								int columnCount = ((ETable) block).get(row)
										.getComponentList().size();
								// ����������;
								for (int column = 0; column < columnCount; column++) {
									int tPanelCount = ((ETable) block).get(row)
											.get(column).getComponentList()
											.size();
									// ȡ����������;
									for (int tpc = 0; tpc < tPanelCount; tpc++) {
										IBlock tBlock = ((ETable) block).get(
												row).get(column).get(tpc)
												.get(0);
										if (tBlock != null) {
											// ����ͼƬ����,���ϴ�;
											flag = this
													.processPicElement(tBlock);
											if (!flag) {
												return false;
											}

										}
									}
								}
							}

						} else {
							flag = this.processPicElement(block);
							if (!flag) {
								return false;
							}

						}

					}

				}

			}

		}
		// �ϴ��ļ��ɹ����������ʱ�༭�ļ������ļ�;
		if (flag) {
			try {
				this.delDir(picTempPath);
			} catch (IOException ex) {
			}
		}

		return flag;
	}

	/**
	 * ����PICԪ��,���ϴ���������;
	 *
	 * @param block
	 *            IBlock
	 * @return boolean
	 */
	private boolean processPicElement(IBlock block) {
		TSocket socket = TIOM_FileServer.getSocket();
		String filePath = this.getImgFullPath();
		// System.out.println("===filePath===" + filePath);
		if (block instanceof EMacroroutine && ((EMacroroutine) block).isPic()) {
			// this.messageBox("is pic");
			VPic pic = ((VPic) ((EMacroroutine) block).getModel().getMVList()
					.get(0).get(0));
			String fullPathName = pic.getPictureName();
			if (fullPathName != null) {
				// �������������ʱ·����,˵����Ҫ�ϴ�
				if (pic.getPictureName().indexOf(picTempPath) != -1) {
					// �����ļ�����ʼ�ϴ���
					String finalPictureName = fullPathName.substring(
							fullPathName.lastIndexOf("/") + 1, fullPathName
							.lastIndexOf("."));
					String fileName = finalPictureName.substring(0,
							finalPictureName.lastIndexOf("_"));
					// System.out.println("fileName" + fileName);
					// ���챳��ͼ������ǰ��ͼ������ϳ�ͼ��
					String bgImage = fileName + "_BG.gif";
					String ticImage = fileName + "_FG.TIC";
					String finalImage = fileName + "_FINAL.jpg";

					byte[] bgData = TIOM_FileServer.readFile(picTempPath
							+ bgImage);
					// ����ͼƬ�ļ�Ŀ¼;
					boolean isMkDir = TIOM_FileServer.mkdir(socket, filePath);
					if (isMkDir) {
						boolean isflag1 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + bgImage, bgData);

						byte[] fgData = TIOM_FileServer.readFile(picTempPath
								+ ticImage);
						boolean isflag2 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + ticImage, fgData);

						byte[] finalData = TIOM_FileServer.readFile(picTempPath
								+ finalImage);
						boolean isflag3 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + finalImage, finalData);

						if (!isflag1 || !isflag2 || !isflag3) {
							return false;
						}
						// pic path���Ϊ�������ļ�·����
						pic.setPictureName("%FILE.ROOT%" + getTemplatePath()
						+ "/" + finalImage);
						this.getWord().update();

					}

				} // ֻ�Ǳ���ֱ���ϴ��ı���ͼ
				else if (pic.getPictureName().indexOf(":") == 1) {
					String finalPictureName = fullPathName.substring(
							fullPathName.lastIndexOf("/") + 1, fullPathName
							.length());

					boolean isMkDir = TIOM_FileServer.mkdir(socket, filePath);
					if (isMkDir) {
						byte[] bgData = TIOM_FileServer.readFile(fullPathName);
						boolean isflag1 = TIOM_FileServer.writeFile(socket,
								filePath + "/" + finalPictureName, bgData);
						if (!isflag1) {
							return false;
						}
						pic.setPictureName("%FILE.ROOT%" + getTemplatePath()
						+ "/" + finalPictureName);
						this.getWord().update();

					}

				}
			}
		}

		return true;
	}

	/**
	 * �༭ͼƬ����;
	 */
	public void onModifyMacroroutine() {
		// ����һ����ʱ��ͼ�ļ��У� ����ʱ�����ʱ�ļ�;
		File f = new File(picTempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		// �ļ�������·��;
		// fileserver+/JHW/caseNo��λ/caseNo��/MR_NO/fileName/
		String fileFullPath = this.getImgFullPath();

		// ȡ��Ӧ��ͼƬ;
		MV mv = macroroutine.getModel().getMVList().get("ͼ��");
		if (mv == null) {
			return;
		}
		DIV div = mv.get("ͼƬ");
		if (!(div instanceof VPic)) {
			return;
		}
		VPic pic = (VPic) div;

		// �ϳ�ͼ;
		String finalImg = pic.getPictureName();

		// �ϳ�ͼ�Ƿ�Ϊnull; _FINAL.jp
		// �ϳ�ͼ��null���������ϴ�����ͼƬ��
		if (finalImg == null) {
			this.messageBox("�����ϴ�����ͼƬ!");
			return;
			// �ϳ�ͼ����null,
		} else {
			String fullFileName = finalImg.substring(
					finalImg.lastIndexOf("/") + 1, finalImg.length());
			String fileName = fullFileName.substring(0, fullFileName
					.indexOf("."));
			String extendName = fullFileName.substring(fullFileName
					.indexOf(".") + 1, fullFileName.length());

			//���������ļ����ļ���
			String bgImage = "";
			String ticImage = "";
			String finalImage = "";
			// ����_FINALΪ���������ļ�;
			if (fileName.indexOf("_FINAL") != -1) {
				fileName = fileName.substring(0, fileName.lastIndexOf("_"));
			} else {
				if (!extendName.equalsIgnoreCase("gif")) {
					this.messageBox("��gif����ͼ�����Ա༭!");
					return;
				}
			}
			// extendName
			bgImage = fileName + "_BG.gif";
			ticImage = fileName + "_FG.TIC";
			finalImage = fileName + "_FINAL.jpg";

			// �ж�tic��ǰ��ͼ�Ƿ���ڣ� ������������
			String flg = "NEW";
			// ���жϱ�����ʱ�ļ��������б༭���ļ���
			// ����Ϊ flg=NEW;
			File ticFile = new File(picTempPath + ticImage);
			// ����TIC�ļ�
			if (ticFile.exists()) {
				flg = "OPEN";
			} else {
				// û�У����ļ����������Ƿ��У�
				// �У������ص�������ʱ�ļ����¡� flg=OPEN
				// �õ�SocketͨѶ����
				TSocket socket = TIOM_FileServer.getSocket();
				byte ticByte[] = TIOM_FileServer.readFile(socket, fileFullPath
						+ "/" + ticImage);
				// Ӧ������������
				if (ticByte != null) {
					// д��ʱ�ļ�;
					TIOM_FileServer.writeFile(picTempPath + ticImage, ticByte);
					// ���ص�����;
					byte bgByte[] = TIOM_FileServer.readFile(socket,
							fileFullPath + "/" + bgImage);
					// д��ʱ�ļ�;
					TIOM_FileServer.writeFile(picTempPath + bgImage, bgByte);
					flg = "OPEN";

					// ��������û�еģ�
				} else {
					long timestamp = new Date().getTime();
					// �����ļ���;
					bgImage = fileName + "_" + timestamp + "_BG." + extendName;
					ticImage = fileName + "_" + timestamp + "_FG.TIC";
					finalImage = fileName + "_" + timestamp + "_FINAL.jpg";
					// ����
					if (pic.getPictureName().indexOf(":") == 1) {
						byte bgByte[] = TIOM_AppServer.readFile(pic
								.getPictureName());
						TIOM_FileServer
						.writeFile(picTempPath + bgImage, bgByte);
						// ����Ϊ�ļ��������ϱ���
					} else {
						// Զ���ļ���ζ�ȡ�ģ������ĸ�ʽ��
						byte bgByte[] = null;
						String imageFilePath = "";
						if (pic.getPictureName().indexOf("%ROOT%") != -1) {
							bgByte = TIOM_AppServer
									.readFile(TIOM_AppServer.SOCKET, pic
											.getPictureName());
						} else if (pic.getPictureName().indexOf("%FILE.ROOT%") != -1) {
							imageFilePath = pic.getPictureName().replace(
									"%FILE.ROOT%", TIOM_FileServer.getRoot());
							bgByte = TIOM_FileServer.readFile(TIOM_FileServer
									.getSocket(), imageFilePath);
						}

						TIOM_FileServer
						.writeFile(picTempPath + bgImage, bgByte);
					}
					// д��ʱ�ļ�;
					flg = "NEW";
				}

			}

			BufferedImage bfImage = this.readImage(picTempPath + bgImage);
			int imgWidth = bfImage.getWidth();
			int imgHeight = bfImage.getHeight();

			// OPEN NEW
			// ���粻����tic ��Ϊ������
			if (flg.equals("NEW")) {
				// �����Ƿ���� �ļ��������� 800 /600
				Image_Paint image_Paint1 = new Image_Paint(this.getWord(),
						picTempPath, ticImage, imgWidth, imgHeight,
						picTempPath, bgImage, picTempPath, finalImage, "NEW",
						pic);
				// 1024*768
				image_Paint1.setSize(ImageTool.getScreenWidth(), ImageTool
						.getScreenHeight());
				image_Paint1.setVisible(true);
				image_Paint1.setTitle("ͼƬ�༭");
				// ���� Ϊ���޸�;
			} else {
				Image_Paint image_Paint1 = new Image_Paint(this.getWord(),
						picTempPath, ticImage, imgWidth, imgHeight,
						picTempPath, bgImage, picTempPath, finalImage, "OPEN",
						pic);
				// 1024*768
				image_Paint1.setSize(ImageTool.getScreenWidth(), ImageTool
						.getScreenHeight());
				image_Paint1.setVisible(true);
				image_Paint1.setTitle("ͼƬ�༭");

			}

		}
		// ����ͼƬ��Ӧ����(��������Ӧ��·��������)��

	}

	/**
	 * ���޸�
	 */
	public void onModify() {
		if (fixed == null) {
			return;
		}
		Object obj = this.openDialog("%ROOT%\\config\\emr\\ModifUI.x", fixed
				.getText());
		if (obj != null) {
			fixed.setText(obj.toString());
		}
	}

	/**
	 * �õ��ֵ���Ϣ
	 *
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * �õ�����
	 *
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * �õ��û���
	 *
	 * @param userID
	 *            String
	 * @return String
	 */
	public String getOperatorName(String userID) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
				+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * ����
	 */
	public boolean onSave() {

		//��׷�Ӽ�¼
		if(this.isApplend()){
			//1.������ʷ������¼
			boolean flg=onsaveEmr(false);
			if(flg){
				//ȡ׷���ļ�����
				TParm saveParm=this.getEmrChildParm();
				final String filePath=saveParm.getValue("FILE_PATH");
				final String fileName=saveParm.getValue("FILE_NAME");
				/*				System.out.println("==onSave save filePath=="
						+ saveParm.getValue("FILE_PATH"));
				System.out.println("==onSave save fileName=="
						+ saveParm.getValue("FILE_NAME"));*/

				//2.����������;
				TParm fileParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG" +
										" FROM EMR_FILE_INDEX" +
										" WHERE CASE_NO='"
										+ this.getCaseNo()
										+ "' AND FILE_NAME='"
										+ this.getSubFileName()
										+ "' ORDER BY FILE_SEQ DESC"));
				TParm fParm = fileParm.getRow(0);

				fParm.setData("FLG", true);

				this.onOpenEmrFile(fParm);
				// TEMP
				EFixed fixed = this.getWord().findFixed(
						"APPLEND");
				if (fixed == null) {
					this.messageBox("�޲���㣡");
					return false;
				}
				fixed.setFocus();
				if (!this.getWord().separatePanel()) {
					return false;
				}
				//System.out.println("===filePath==="+filePath);
				//System.out.println("===fileName==="+fileName);
				this.getWord().onInsertFileFrontFixed(
						"APPLEND",
						filePath,
						fileName, 3,
						false);
				this.setEmrChildParm(fParm);
				this.setOnlyEditType("ONLYONE");

				this.getWord().update();
				boolean flg1=onsaveEmr(true);
				//�򿪵��ճ���������¼��׷�ӱ�־false;
				this.setApplend(false);

				return flg1;

			}else{
				this.messageBox("����ʧ�ܣ�");
				return false;
			}

			//��׷�Ӽ�¼����ԭ��������;
		}else{
			return onsaveEmr(true);
		}
		//return false;

	}

	/**
	 * ����EMR�ļ�
	 *
	 * @param parm
	 *            TParm
	 */
	public boolean saveEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", this.getDBTool().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REPORT_FLG", "N");
		parm.setData("CURRENT_USER", Operator.getID());
		// $$=================������ֵ��ҽ��=======================$$//
		if (isDutyDrList()) {
			parm.setData("CURRENT_USER", Operator.getID());
			parm.setData("CHK_USER1", "");
			parm.setData("CHK_DATE1", "");
			parm.setData("CHK_USER2", "");
			parm.setData("CHK_DATE2", "");
			parm.setData("CHK_USER3", "");
			parm.setData("CHK_DATE3", "");
			parm.setData("COMMIT_USER", "");
			parm.setData("COMMIT_DATE", "");
		}
		// $$=================������ֵ��ҽ��=======================$$//

		if (this.getOnlyEditType().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		// this.messageBox("type"+this.getOnlyEditType());

		if (this.getOnlyEditType().equals("ONLYONE")) {
			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}

	/**
	 * �õ��ļ�������·��
	 *
	 * @param rootPath
	 *            String
	 * @param fileServerPath
	 *            String
	 * @return String
	 */
	public TParm getFileServerEmrName() {
		TParm emrParm = new TParm();
		String emrName = "";
		TParm childParm = this.getEmrChildParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		//System.out.println("=============getFileServerEmrName templetName================"+templetName);

		TParm action = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO" +
								" FROM EMR_FILE_INDEX" +
								" WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
		int index = action.getInt("MAXFILENO", 0);
		emrName = this.getCaseNo() + "_" + templetName + "_" + index;
		//this.messageBox("==emrName=="+emrName);
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		emrParm.setData("FILE_SEQ", index);
		emrParm.setData("FILE_NAME", emrName);
		emrParm.setData("CLASS_CODE", childParm.getData("CLASS_CODE"));
		emrParm.setData("SUBCLASS_CODE", childParm.getData("SUBCLASS_CODE"));
		emrParm.setData("CASE_NO", this.getCaseNo());
		emrParm.setData("MR_NO", this.getMrNo());
		emrParm.setData("IPD_NO", this.getIpdNo());
		emrParm.setData("FILE_PATH", "JHW" + "\\" + this.getAdmYear() + "\\"
				+ this.getAdmMouth() + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("EMT_FILENAME", childParm.getValue("EMT_FILENAME"));
		return emrParm;
	}

	/**
	 * ����Ƿ񱣴�
	 */
	public boolean checkSave() {
		if (this.getWord().getFileOpenName() != null
				&& (ruleType.equals("2") || ruleType.equals("3"))
				&& this.getTMenuItem("save").isEnabled()) {
			// P0009��ʾ��Ϣ
			if (this.messageBox("��ʾ��Ϣ Tips", "�Ƿ񱣴�"
					+ this.getWord().getFileOpenName() + "�ļ�?\n To save"
					+ this.getWord().getFileOpenName() + "File?",
					this.YES_NO_OPTION) == 0) {
				// �������ݿ�����
				if (onSave()) {
					//this.loadTree();//wuh delete
					this.loadTreeByType();//wuh
					return true;
				} else {
					// ����ʧ��
					this.messageBox("E0001");
					return false;
				}
			} else {

			}
		}
		return true;
	}

	public static void main(String args[]) {
		/*
		 * JFrame f = new JFrame(); f.getRootPane().add(new EMRPad30());
		 * f.getRootPane().setLayout(new BorderLayout()); f.setVisible(true);
		 */
		JavaHisDebug.TBuilder();
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
	 * �õ�ϵͳ���
	 *
	 * @return String
	 */
	public String getSystemType() {
		return systemType;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getAdmMouth() {
		return admMouth;
	}

	public String getAdmYear() {
		return admYear;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public TParm getEmrChildParm() {
		return emrChildParm;
	}

	public String getDirectorDrCode() {
		return directorDrCode;
	}

	public String getAttendDrCode() {
		return attendDrCode;
	}

	public String getVsDrCode() {
		return vsDrCode;
	}

	public String getAdmType() {
		return admType;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getOnlyEditType() {
		return onlyEditType;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getType() {
		return type;
	}

	public String getStationCode() {
		return stationCode;
	}

	/**
	 * �õ�ϵͳ���
	 *
	 * @param systemType
	 *            String
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setAdmMouth(String admMouth) {
		this.admMouth = admMouth;
	}

	public void setAdmYear(String admYear) {
		this.admYear = admYear;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
	}

	public void setDirectorDrCode(String directorDrCode) {
		this.directorDrCode = directorDrCode;
	}

	public void setAttendDrCode(String attendDrCode) {
		this.attendDrCode = attendDrCode;
	}

	public void setVsDrCode(String vsDrCode) {
		this.vsDrCode = vsDrCode;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	/**
	 *
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * �������
	 */
	public void onInsertTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertBaseTableDialog();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ɾ������
	 */
	public void onDelTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTable();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ���������
	 */
	public void onInsertTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ׷�ӱ�����
	 */
	public void onAddTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().appendTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ɾ��������
	 */
	public void onDelTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ӡ����
	 */
	public void onPrintSetup() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().printSetup();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().print();
			//yanjing 20130807 modify
			if(adm_type.equals("E")||adm_type.equals("O")){
				this.closeWindow();
			}
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * �����ӡ
	 */
	public void onPrintClear() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ����
	 */
	public void onCutMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onCut();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ����
	 */
	public void onCopyMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onCopy();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ճ��
	 */
	public void onPasteMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPaste();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ɾ��
	 */
	public void onDeleteMenu() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onDelete();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ҳ��ӡ
	 */
	public void onPrintIndex() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().getPM().getPageManager().printDialog();
			//yanjing 20130807 modify
			if(adm_type.equals("E")||adm_type.equals("O")){
				this.closeWindow();
			}
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ˢ��
	 */
	public void onHongR() {
		if (this.getWord().getFileOpenName() != null) {
			this.setMicroField(true);
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ӡ
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().printXDDialog();

		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��ʾ�кſ���
	 */
	public void onShowRowIDSwitch() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().setShowRowID(!word.isShowRowID());
			this.getWord().update();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * �������
	 */
	public void onAddDLText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onParagraphDialog();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
	}

	public void onChangeLanguage(String language) {

	}

	/****************************** �Ž��� �ǿ���֤ ******************************/
	/**
	 * ����ǿ���֤
	 *
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputObject(Object obj) {
		if (obj == null) {
			return false;
		}
		String str = String.valueOf(obj);
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/****************************** �Ž��� �Ƿ������޸ġ��Ƿ�������ӡ���� ******************************/
	/**
	 * ��ѯ���Ӳ����ļ��浵
	 *
	 * @param caseNo
	 *            String
	 * @param fileSeq
	 *            String
	 * @return TParm
	 */
	public TParm getEmrFileIndex(String caseNo, String fileSeq) {
		String sql = " SELECT EFI.* FROM EMR_FILE_INDEX EFI WHERE EFI.CASE_NO = '"
				+ caseNo + "' AND FILE_SEQ = '" + fileSeq + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ����������ӡ��ʶ�������޸ı�ʶ
	 *
	 * @param isVisible
	 * @param canPrintFlg
	 */
	public void setCanPrintFlgAndModifyFlg(String caseNo, String fileSeq,
			boolean isVisible) {
		TCheckBox CANPRINT_FLG = (TCheckBox) this.getComponent("CANPRINT_FLG");
		TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
		TMenuItem PrintShow = (TMenuItem) this.getComponent("PrintShow");
		TMenuItem PrintSetup = (TMenuItem) this.getComponent("PrintSetup");
		TMenuItem PrintClear = (TMenuItem) this.getComponent("PrintClear");
		TMenuItem printIndex = (TMenuItem) this.getComponent("printIndex");
		TMenuItem LinkPrint = (TMenuItem) this.getComponent("LinkPrint");
		TMenuItem save = (TMenuItem) this.getComponent("save");
		// �Ƿ���ʾ
		if (isVisible) {
			CANPRINT_FLG.setVisible(true);
			MODIFY_FLG.setVisible(true);
			// ��ѯ���Ӳ����ļ��浵
			TParm result = getEmrFileIndex(caseNo, fileSeq);
			// ����������ӡ��ʶ
			CANPRINT_FLG.setValue(result.getValue("CANPRINT_FLG", 0));
			// ���������޸ı�ʶ
			MODIFY_FLG.setValue(result.getValue("MODIFY_FLG", 0));
			// ��ǰ�û��Ƿ�Ϊ���༭��
			if (Operator.getID().equals(result.getValue("CURRENT_USER", 0))) {
				CANPRINT_FLG.setEnabled(true);
				MODIFY_FLG.setEnabled(true);
				// ������ӡ
				PrintShow.setEnabled(true);
				PrintSetup.setEnabled(true);
				PrintClear.setEnabled(true);
				printIndex.setEnabled(true);
				LinkPrint.setEnabled(true);
				// �����޸�
				save.setEnabled(true);
			} else {
				CANPRINT_FLG.setEnabled(false);
				MODIFY_FLG.setEnabled(false);
				// �����Ƿ�������ӡ
				if ("Y".equals(result.getValue("CANPRINT_FLG", 0))) {
					PrintShow.setEnabled(true);
					PrintSetup.setEnabled(true);
					PrintClear.setEnabled(true);
					printIndex.setEnabled(true);
					LinkPrint.setEnabled(true);
				} else {
					PrintShow.setEnabled(false);
					PrintSetup.setEnabled(false);
					PrintClear.setEnabled(false);
					printIndex.setEnabled(false);
					LinkPrint.setEnabled(false);
				}
				// �����Ƿ������޸�
				if ("Y".equals(result.getValue("MODIFY_FLG", 0))) {
					save.setEnabled(true);
				} else {
					save.setEnabled(false);
				}
			}
		} else {
			CANPRINT_FLG.setVisible(false);
			MODIFY_FLG.setVisible(false);
			CANPRINT_FLG.setEnabled(false);
			MODIFY_FLG.setEnabled(false);
			PrintShow.setEnabled(true);
			PrintSetup.setEnabled(true);
			PrintClear.setEnabled(true);
			printIndex.setEnabled(true);
			LinkPrint.setEnabled(true);
			save.setEnabled(true);
		}
	}
	//$$==========del by lx2012/03/23==============$$//

	/**
	 * ���ڹر�ʱȡ��Timer��ʱ��
	 *
	 * @return boolean
	 */
	public boolean onClosing() {
		//yanjing 20130808 �ż����Զ��رգ�����Ҫѯ��
		if(!(adm_type.equals("E"))&&!(adm_type.equals("O"))){
			if (this.messageBox("ѯ��", "�Ƿ�رգ�", 2) != 0) {
				return false;
			}
		}
		super.onClosing();
		this.setReturnValue(returnParm);//add by wanglong 20121115
		// �˳��Զ����涨ʱ��
		// this.cancel();
		return true;
	}

	/****************************** �Ž��� �������� ******************************/
	/**
	 * ��������Ȩ�޿��Ƹ�Ҫ 1��ruleType=1 ������������м�¼ֻ�ܲ鿴 2��ruleType=2��systemType!=ODI
	 * ������������Բ鿴���½����༭��ɾ��������¼���ǻ�����¼ֻ�ܲ鿴
	 * 3��ruleType=3��systemType=ODI��writeType=OIDR
	 * ������������Բ鿴���½����༭��ɾ�����ﲡ�����ǻ��ﲡ��ֻ�ܲ鿴 4��ruleType=2��systemType=ODI ��������Ȩ�޿���
	 */
	/**
	 * ��֤�û��Ƿ��ύ�˲���
	 */
	public boolean checkUserSubmit(TParm parm, String user) {
		String chkUser1 = parm.getValue("CHK_USER1");
		String chkUser2 = parm.getValue("CHK_USER2");
		String chkUser3 = parm.getValue("CHK_USER3");
		if (user.equals(chkUser1)) {
			return true;
		}
		if (user.equals(chkUser2)) {
			return true;
		}
		if (user.equals(chkUser3)) {
			return true;
		}
		return false;
	}

	/**
	 * ��ȡ�û���ְ����� 1���Ǿ���ҽʦ ������ҽʦ ������ҽʦ 2������ҽʦ ������ҽʦ ������ҽʦ 3���Ǿ���ҽʦ ����ҽʦ ������ҽʦ
	 * 4���Ǿ���ҽʦ ������ҽʦ ����ҽʦ 5������ҽʦ ����ҽʦ ������ҽʦ 6������ҽʦ ������ҽʦ ����ҽʦ 7���Ǿ���ҽʦ ����ҽʦ ����ҽʦ
	 * 8������ҽʦ ����ҽʦ ����ҽʦ
	 */
	public int getUserDuty(String user) {
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 1;
		}
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 2;
		}
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 3;
		}
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 4;
		}
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 5;
		}
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 6;
		}
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 7;
		}
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 8;
		}
		return 0;
	}

	/**
	 * ��װ��������
	 *
	 * @param parm
	 *            ��������
	 * @param optType
	 *            �������� 1:�༭���� 2:�ύ 3:ȡ���ύ(��ǰ�û����ύ) 4:ȡ���ύ(��ǰ�û�δ�ύ)
	 */
	public void setOptDataParm(TParm parm, int optType) {
		TParm data = this.getEmrChildParm();
		int operDuty = this.getUserDuty(Operator.getID());
		// ����ʱ
		if (optType == 1) {
			// ������CANPRINT_FLG��MODIFY_FLG
			TCheckBox CANPRINT_FLG = (TCheckBox) this
					.getComponent("CANPRINT_FLG");
			TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
			parm.setData("CANPRINT_FLG", CANPRINT_FLG.getValue());
			parm.setData("MODIFY_FLG", MODIFY_FLG.getValue());
		}
		// �ύʱ
		if (optType == 2) {
			// ������ӡ�������޸�
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USERΪ�ϼ�ҽʦ����ǰ�û�Ϊ����ҽʦʱΪ�㣩
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "0");
			}
			// ��д���������˺�ʱ��
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", Operator.getID());
				parm.setData("CHK_DATE1", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", Operator.getID());
				parm.setData("CHK_DATE2", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", Operator.getID());
				parm.setData("CHK_DATE3", this.getDBTool().getDBTime());
			}
			// ��д�ύ�˺�ʱ��
			parm.setData("COMMIT_USER", Operator.getID());
			parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
		}
		// ȡ���ύ(��ǰ�û����ύ)
		if (optType == 3) {
			// ������ӡ�������޸�
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USERΪ��ǰ�û�����ǰ�û�Ϊ����ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
				parm.setData("COMMIT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			// ���������˺�ʱ�����
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", "");
				parm.setData("CHK_DATE3", "");
			}
		}
		// ȡ���ύ(��ǰ�û�δ�ύ)
		if (optType == 4) {
			// ������ӡ�������޸�
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USERΪ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ���¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����¼������˺�ʱ�����
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
		}

		// һ��������
		if (!parm.existData("CHK_USER1")) {
			parm.setData("CHK_USER1", data.getValue("CHK_USER1"));
		}
		// һ������ʱ��
		if (!parm.existData("CHK_DATE1")) {
			parm.setData("CHK_DATE1", data.getValue("CHK_DATE1"));
		}
		// ����������
		if (!parm.existData("CHK_USER2")) {
			parm.setData("CHK_USER2", data.getValue("CHK_USER2"));
		}
		// ��������ʱ��
		if (!parm.existData("CHK_DATE2")) {
			parm.setData("CHK_DATE2", data.getValue("CHK_DATE2"));
		}
		// ����������
		if (!parm.existData("CHK_USER3")) {
			parm.setData("CHK_USER3", data.getValue("CHK_USER3"));
		}
		// ��������ʱ��
		if (!parm.existData("CHK_DATE3")) {
			parm.setData("CHK_DATE3", data.getValue("CHK_DATE3"));
		}
		// �ύ��
		if (!parm.existData("COMMIT_USER")) {
			parm.setData("COMMIT_USER", data.getValue("COMMIT_USER"));
		}
		// �ύʱ��
		if (!parm.existData("COMMIT_DATE")) {
			parm.setData("COMMIT_DATE", data.getValue("COMMIT_DATE"));
		}
		// ��Ժ�����
		if (!parm.existData("IN_EXAMINE_USER")) {
			parm.setData("IN_EXAMINE_USER", data.getValue("IN_EXAMINE_USER"));
		}
		// ��Ժ���ʱ��
		if (!parm.existData("IN_EXAMINE_DATE")) {
			parm.setData("IN_EXAMINE_DATE", data.getValue("IN_EXAMINE_DATE"));
		}
		// ��Ժ�����
		if (!parm.existData("DS_EXAMINE_USER")) {
			parm.setData("DS_EXAMINE_USER", data.getValue("DS_EXAMINE_USER"));
		}
		// ��Ժ���ʱ��
		if (!parm.existData("DS_EXAMINE_DATE")) {
			parm.setData("DS_EXAMINE_DATE", data.getValue("DS_EXAMINE_DATE"));
		}
		// PDF������
		if (!parm.existData("PDF_CREATOR_USER")) {
			parm.setData("PDF_CREATOR_USER", data.getValue("PDF_CREATOR_USER"));
		}
		// PDF����ʱ��
		if (!parm.existData("PDF_CREATOR_DATE")) {
			parm.setData("PDF_CREATOR_DATE", data.getValue("PDF_CREATOR_DATE"));
		}
	}

	/**
	 * 1.�����鿴Ȩ�޿��ƣ�
	 */
	public boolean checkDrForOpen() {
		return true;
	}

	/**
	 * 2.�����½�����Ȩ�޿��ƣ� ruleType=1�����½� ����ʱ����д�����˺�ʱ��
	 */
	public boolean checkDrForNew() {
		if ("1".equals(this.ruleType)) {
			return false;
		}
		return true;
	}

	/**
	 * 3.�����༭����Ȩ�޿��ƣ� ruleType=1���ɱ༭ ruleType=2��systemType!=ODI
	 * EMR_FILE_INDEX.SYSTEM_CODE!=INW���ɱ༭��������Ա༭
	 * ruleType=3��systemType=ODI��writeType=OIDR
	 * EmrFileIndex.OIDR_FLG!=Y���ɱ༭��������Ա༭ ruleType=2��systemType=ODI��
	 * ����������ҽʦ��ֵ��ҽʦ���ɱ༭ ��CURRNET_USERΪ�գ�����ҽʦ���������������ҽʦ��ֵ��ҽʦ���ɱ༭ ����
	 * ����ǰ�û�ΪCURRENT_USER���Ա༭ ���� ��MODIFY_FLG=N���ɱ༭ ����
	 * ������ҽʦδ�ύ�������������ҽʦ��ֵ��ҽʦ���ɱ༭ ����������ҽʦδ�ύ���������ҽʦ�ҷ�����ҽʦ���ɱ༭
	 * ����������ҽʦδ�ύ���������ҽʦ���ɱ༭ ���򣬣�����ҽʦ���ύ��������������ҽʦ��ֵ��ҽʦ�����ɱ༭ ���򲻿ɱ༭
	 * ����ʱ��������CANPRINT_FLG��MODIFY_FLG
	 */
	public boolean checkDrForEdit() {
		TParm data = this.getEmrChildParm();
		if ("1".equals(this.ruleType)) {
			return false;
		}
		if ("2".equals(this.ruleType) && !"ODI".equals(this.getSystemType())) {
			if (!"INW".equals(data.getValue("SYSTEM_CODE"))) {
				return false;
			} else {
				return true;
			}
		}
		if ("3".equals(this.ruleType) && "ODI".equals(this.getSystemType())
				&& "OIDR".equals(this.writeType)) {
			if (!"Y".equals(data.getValue("OIDR_FLG"))) {
				return false;
			} else {
				return true;
			}
		}
		if ("2".equals(this.ruleType) && "ODI".equals(this.getSystemType())) {
			// this.messageBox("current_user" + data.getValue("CURRENT_USER"));
			if (!this.isCheckUserDr() && !this.isDutyDrList()) {
				return false;
			}
			if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
				return true;
			} else {
				if (Operator.getID().equals(data.getValue("CURRENT_USER"))) {
					return true;
				} else {

					//
					if ("N".equals(data.getValue("MODIFY_FLG"))) {
						return false;
					} else {
						// ������ֵ��ҽʦ�����Ա༭��
						if (isDutyDrList()) {
							return true;
						}
						// ����ҽʦû���ύ����
						if (!this.checkUserSubmit(data, this.getVsDrCode())) {
							return false;
							// ����ҽʦû���ύ����
						} else if (!this.checkUserSubmit(data, this
								.getAttendDrCode())) {
							if (!Operator.getID()
									.equals(this.getAttendDrCode())
									&& !Operator.getID().equals(
											this.getDirectorDrCode())) {
								return false;
							} else {
								return true;
							}
							// ������û���ύ����
						} else if (!this.checkUserSubmit(data, this
								.getDirectorDrCode())) {
							if (!Operator.getID().equals(
									this.getDirectorDrCode())) {
								return false;
							} else {
								return true;
							}

							// ��û�ύ
						} else {
							return false;
						}
						// ��û�ύ
						// return true;
					}
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * 4.�����ύȨ�޿��ƣ� ruleType!=2��systemType!=ODI�����ύ ����������ҽʦ�����ύ
	 * ��CURRENT_USERΪ�գ�����ҽʦ������Ǿ���ҽʦ�����ύ ���� ����ǰ�û���CURRENT_USER�����ύ ��������ύ
	 * �ύʱ��CURRENT_USERΪ�ϼ�ҽʦ����ǰ�û�Ϊ����ҽʦʱΪ�㣩��������ӡ�������޸ģ���д���������˺�ʱ�䣬��д�ύ�˺�ʱ��
	 */
	public boolean checkDrForSubmit() {
		TParm data = this.getEmrChildParm();
		if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
			return false;
		}
		if (!this.isCheckUserDr()) {
			return false;
		}
		if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
			if (!Operator.getID().equals(this.getVsDrCode())) {
				return false;
			} else {
				return true;
			}
		} else {
			if (!Operator.getID().equals(data.getValue("CURRENT_USER"))) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 5.����ȡ���ύȨ�޿��ƣ� ruleType!=2��systemType!=ODI����ȡ���ύ ����������ҽʦ����ȡ���ύ
	 * ��CURRNET_USERΪ�գ�����ҽʦ�����򲻿�ȡ���ύ ���� ����ǰ�û�ΪCURRENT_USER�������ȡ���ύ ����
	 * ������ҽʦ���ύ���������ҽʦ����ȡ���ύ ����������ҽʦ���ύ���������ҽʦ�ҷ�����ҽʦ�����ύ
	 * ����������ҽʦ���ύ����Ǿ���ҽʦ�ҷ�����ҽʦ�����ύ ���򣬣�����ҽʦδ�ύ��������ȡ���ύ
	 * ��ǰ�û����ύ��CURRENT_USERΪ��ǰ�û�
	 * ����ǰ�û�Ϊ����ҽʦʱ��գ���������ӡ�������޸ģ����������˺�ʱ����գ��ύ�˺�ʱ��Ϊ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
	 * ��ǰ�û�δ�ύ��CURRENT_USERΪ�¼�ҽʦ
	 * ����ǰ�û�Ϊ����ҽʦʱ��գ���������ӡ�������޸ģ��¼������˺�ʱ����գ��ύ�˺�ʱ��Ϊ�¼�ҽʦ���¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
	 */
	public boolean checkDrForSubmitCancel() {
		TParm data = this.getEmrChildParm();
		if (!"2".equals(this.ruleType) || !"ODI".equals(this.getSystemType())) {
			return false;
		}
		if (!this.isCheckUserDr()) {
			return false;
		}
		if (!this.checkInputObject(data.getValue("CURRENT_USER"))) {
			return false;
		} else {
			if (Operator.getID().equals(data.getValue("CURRENT_USER"))) {
				return true;
			} else {
				if (this.checkUserSubmit(data, this.getDirectorDrCode())) {
					if (!Operator.getID().equals(this.getDirectorDrCode())) {
						return false;
					} else {
						return true;
					}
				} else if (this.checkUserSubmit(data, this.getAttendDrCode())) {
					if (!Operator.getID().equals(this.getAttendDrCode())
							&& !Operator.getID().equals(
									this.getDirectorDrCode())) {
						return false;
					} else {
						return true;
					}
				} else if (this.checkUserSubmit(data, this.getVsDrCode())) {
					if (!Operator.getID().equals(this.getVsDrCode())
							&& !Operator.getID().equals(this.getAttendDrCode())) {
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * 6.����ɾ�����Ƿ�������ӡ���á��Ƿ������޸�����Ȩ�޿��ƣ� ruleType=1����ɾ�� ��ǰ�û���CURRENT_USER����ɾ��
	 */
	public boolean checkDrForDel() {
		TParm data = this.getEmrChildParm();
		// TTreeNode node = this.getTTree(TREE_NAME).getSelectionNode();
		// TParm data = (TParm) node.getData();
		// this.messageBox("data"+data);
		// CURRENT_USER
		String currentUser = data.getValue("CURRENT_USER");
		// this.messageBox("ruleType==="+this.ruleType);
		if ("1".equals(this.ruleType)) {
			return false;
		}
		if (!Operator.getID().equals(currentUser)) {
			return false;
		}
		return true;
	}

	/**
	 * ��ø��ڵ��µ������ӽڵ�
	 *
	 * @param parentClassCode
	 *            String ���ڵ�
	 * @param allChilds
	 *            TParm ���շ������нڵ�
	 */

	private void getAllChildsByParent(String parentClassCode, TParm allChilds) {
		TParm childs = this.getChildsByParentNode(parentClassCode);
		if (childs != null && childs.getCount() > 0) {
			// ��ʼֵ��
			if (allChilds.getCount() == -1) {
				allChilds.setCount(0);
			}
			allChilds.setCount(allChilds.getCount() + childs.getCount());

			for (int i = 0; i < childs.getCount(); i++) {
				allChilds
				.addData("CLASS_CODE", childs.getData("CLASS_CODE", i));
				allChilds
				.addData("CLASS_DESC", childs.getData("CLASS_DESC", i));
				allChilds.addData("CLASS_STYLE", childs.getData("CLASS_STYLE",
						i));
				allChilds.addData("LEAF_FLG", childs.getData("LEAF_FLG", i));
				allChilds.addData("PARENT_CLASS_CODE", childs.getData(
						"PARENT_CLASS_CODE", i));
				allChilds.addData("SEQ", childs.getData("SEQ", i));
				this.getAllChildsByParent(((String) childs.getData(
						"CLASS_CODE", i)), allChilds);
			}
		}

	}

	/**
	 * add by lx ͨ�����ڵ�õ��ӽڵ�;
	 *
	 * @param parentClassCode
	 *            String
	 * @return TParm
	 */
	private TParm getChildsByParentNode(String parentClassCode) {
		TParm result = new TParm(
				this
				.getDBTool()
				.select(
						"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ" +
								" FROM EMR_CLASS" +
								" WHERE PARENT_CLASS_CODE='"
								+ parentClassCode
								+ "' ORDER BY SEQ,CLASS_CODE"));
		return result;
	}

	/**
	 * ����ͼƬ�༭��
	 */
	public void onInsertImageEdit() {

		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return;
		}
		this.getWord().insertImage();

	}

	/**
	 * ɾ��ͼƬ�༭��
	 */
	public void onDeleteImageEdit() {
		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return;
		}
		this.getWord().deleteImage();

	}

	/**
	 * �����
	 */
	public void onInsertGBlock() {
		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return;
		}

		this.getWord().insertGBlock();

	}

	/**
	 * ������
	 */
	public void onInsertGLine() {
		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return;
		}
		this.getWord().insertGLine();

	}

	/**
	 * ������ߴ�
	 *
	 * @param index
	 *            int
	 */
	public void onSizeBlockMenu(String index) {
		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return;
		}
		this.getWord().onSizeBlockMenu(StringTool.getInt(index));
	}

	/**
	 * ��ȡ���ұ�׼��code���粻���ڷ���hisϵͳcode;
	 *
	 * @param HisAttr
	 *            String P:pattern�����ֵ�|D��dictionary�����ֵ�
	 * @param hisTableName
	 *            String hisϵͳ����
	 * @param hisCode
	 *            String hisϵͳ����
	 * @return String ��Ӧ�Ĺ��ұ�׼��code
	 */
	private String getEMRCode(String HisAttr, String hisTableName,
			String hisCode) {

		String sql = "SELECT EMR_CODE FROM EMR_CODESYSTEM_D";
		sql += " WHERE HIS_ATTR='" + HisAttr + "'";
		sql += " AND HIS_TABLE_NAME='" + hisTableName + "'";
		sql += " AND HIS_CODE='" + hisCode + "'";
		TParm emrCodeParm = new TParm(getDBTool().select(sql));
		int count = emrCodeParm.getCount();
		// �ж�Ӧ
		if (count > 0) {
			return emrCodeParm.getValue("EMR_CODE", 0);
		}

		return hisCode;
	}

	/**
	 * ��������Ԫ��ֵ��
	 *
	 * @param objParameter
	 *            TParm
	 */
	private void setHiddenElements(TParm objParameter) {
		for (int i = 0; i < this.getWord().getPageManager().getMVList().size(); i++) {
			MV mv = this.getWord().getPageManager().getMVList().get(i);
			if (mv.getName().equals("UNVISITABLE_ATTR")) {
				for (int j = 0; j < mv.size(); j++) {
					DIV div = mv.get(j);
					if (div instanceof VText) {
						// this.messageBox("�Ǵ�������ֵ:"+((VText)div).isTextT());
						if (((VText) div).isTextT()) {
							// ��������ֵ�����ֵ��
							// objParameter.setData(div.getName(), "TEXT",
							// hideElemMap.get(div.getName()));
							// System.out.println("���صĺ���++++"+ ( (VText)
							// div).getMicroName());
							// System.out.println("���صĺ���ֵ++++"+ hideElemMap.get(
							// ( (VText) div).getMicroName()));

							objParameter.setData(((VText) div).getMicroName(),
									"TEXT", hideElemMap.get(((VText) div)
											.getMicroName()));

						} else {
							// ��ֵ;
							objParameter.setData(div.getName(), "TEXT",
									((VText) div).getText());

						}

					}
				}

			}

		}

	}

	/**
	 * ͨ����������ץȡ��ֵ��
	 *
	 * @param macroName
	 *            String
	 * @param value
	 *            String
	 */
	private boolean setCaptureValue(String macroName, String value) {
		boolean isSetValue = false;
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// EComponent
			// this.messageBox("EPanel size"+ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// this.messageBox("type"+block.getObjectType());
						// this.messageBox("value"+block.getBlockValue());
						// 9Ϊץȡ��;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
									// this.messageBox("microName"+capture.getMicroName());
									// �ǿ�ʼ����ֵ;
									if (capture.getCaptureType() == 0) {
										capture.setFocusLast();
										capture.clear();
										this.getWord().pasteString(value);
										isSetValue = true;
										break;
									}
								}
							}

						}

						if (isSetValue) {
							break;
						}
					}
					if (isSetValue) {
						break;
					}

				}

			}

		}
		return isSetValue;

	}

	/**
	 * ����סԺ֤(��ʱ����д���ں�ץȡ���óɻ��)
	 */
	private void setAdmResv(TParm parm) {
		// ����
		String patSource = (String) parm.getData("������Դ", 0);
		if (patSource != null && !patSource.equals("")) {
			if (patSource.equalsIgnoreCase("01")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"������Ժ", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}
			} else if (patSource.equalsIgnoreCase("02")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"������Ժ", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}

			} else if (patSource.equalsIgnoreCase("09")) {
				EComponent com = this.getWord().getPageManager().findObject(
						"��Ժת��", EComponent.CHECK_BOX_CHOOSE_TYPE);
				ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
				if (checkbox != null) {
					checkbox.setChecked(true);
				}
			}
		}

		this.getWord().update();
	}

	/**
	 * �����ٴ�·��ֵ;
	 */
	private void setCLPValue() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("REGION_CODE", Operator.getRegion());

		TParm result = CLPEMRTool.getInstance().getEMRManagedData(parm);
		// System.out.println("====�ٴ�·��result===="+result);
		String names[] = result.getNames();
		// this.messageBox("setCLPValue===="+names.length);

		for (int i = 0; i < names.length; i++) {
			// this.messageBox("name"+names[i]);
			// this.messageBox("value"+result.getValue(names[i]));
			EComponent com = this.getWord().getPageManager().findObject(
					names[i], EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
			if (checkbox != null) {
				if (result.getValue(names[i]).equalsIgnoreCase("Y")) {
					checkbox.setChecked(true);

				}
			}
		}
		this.getWord().update();
	}

	/**
	 * ɾ����ʱ�ļ�Ŀ¼;
	 *
	 * @param filepath
	 *            String
	 * @throws IOException
	 */
	private void delDir(String filepath) throws IOException {
		File f = new File(filepath); // �����ļ�·��
		if (f.exists() && f.isDirectory()) { // �ж����ļ�����Ŀ¼
			// ��Ŀ¼��û���ļ���ֱ��ɾ��
			if (f.listFiles().length == 0) {
				f.delete();
			} else { // ��������ļ��Ž����飬���ж��Ƿ����¼�Ŀ¼
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						// �ݹ����del������ȡ����Ŀ¼·��
						delDir(delFile[j].getAbsolutePath());
					}
					// ɾ���ļ�dddd
					delFile[j].delete();
				}
			}
		}
	}

	/**
	 * ��ͼƬ�ļ�
	 *
	 * @param url
	 * @return
	 */
	private BufferedImage readImage(String fileName) {
		File file = new File(fileName);
		BufferedImage image = null;
		if (file != null && file.isFile() && file.exists()) {
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	/**
	 * ɾ���̶��ı�
	 */
	public void onDelFixText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteFixed();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
	}

	/**
	 * ����ͼƬ
	 */
	public void onInsertPictureObject() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertPicture();
			this.setContainPic(true);
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
	}

	/**
	 * ���뵱ǰʱ��
	 */
	public void onInsertCurrentTime() {
		// ��ȡʱ��
		Timestamp sysDate = StringTool.getTimestamp(new Date());
		String strSysDate = StringTool
				.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
		// ���㴦����ʱ��;
		// EComponent e = word.getFocusManager().getFocus();
		this.getWord().pasteString(strSysDate);
	}

	/**
	 * ճ��ͼƬ
	 */
	public void onPastePicture() {
		ClipboardTool tool = new ClipboardTool();
		try {
			Image img = tool.getImageFromClipboard();
			// �ж�ͼƬ��ʽ�Ƿ���ȷ
			if (img == null || img.getWidth(null) == -1) {
				this.messageBox("���а���û��ͼƬ����,����ץȡ!");
				return;

			}
			this.getWord().onPaste();

		} catch (Exception ex) {
		}

	}

	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}

	/**
	 * ������Ϣ�ղع���
	 */
	public void onSavePatInfo() {
		this.getWord().onCopy();
		// ��ϵͳ���а����Ƿ�������ݣ�
		if (CopyOperator.getComList() == null
				|| CopyOperator.getComList().size() == 0) {
			this.messageBox("����ѡ��Ҫ����Ĳ�����Ϣ��");
			return;
		}
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "SavePatInfo");
		TWindow window = (TWindow)this.openWindow("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		//		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		//      window.setY(0);
		TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
		window.setX(wordPanel.getX() + 10);
		window.setY(130);
		window.setVisible(true);
		AWTUtilities.setWindowOpacity(window, 0.8f);
	}

	/**
	 * ���벡����Ϣ;
	 */
	public void onInsertPatInfo() {
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "InsertPatInfo");
		inParm.setData("TWORD", this.getWord());

		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * �ж��Ƿ���Ҫ�ϴ�
	 *
	 * @return boolean
	 */
	private boolean isUloadPic() {
		boolean isUpload = false;
		if ("NEW".equals(this.getOnlyEditType())) {
			if (this.isContainPic()) {
				isUpload = true;
				this.getWord().setFileContainPic("Y");
			} else {
				isUpload = false;
				this.getWord().setFileContainPic("N");
			}
		}

		if ("ONLYONE".equals(this.getOnlyEditType())) {
			if (this.isContainPic()
					|| (this.getWord().getFileContainPic() != null && this
					.getWord().getFileContainPic().equals("Y"))) {
				isUpload = true;
				this.getWord().setFileContainPic("Y");
			} else {
				isUpload = false;
				this.getWord().setFileContainPic("N");
			}

		}

		return isUpload;

	}

	public void setContainPic(boolean containPic) {
		this.containPic = containPic;
	}

	public boolean isContainPic() {
		return this.containPic;
	}

	/**
	 * ���没��
	 *
	 * @param isShow
	 *            boolean �Ƿ���ʾ����ɹ�
	 * @return boolean
	 */
	private boolean onsaveEmr(boolean isShow) {
		if (this.getWord().getFileOpenName() == null) {
			// ��ѡ��һ����Ҫ�༭�Ĳ���
			this.messageBox("E0111");
			return false;
		}
		// 2�������½�����Ȩ�޿��ƣ�
		if ("NEW".equals(this.getOnlyEditType())) {
			if (!this.checkDrForNew()) {
				this.messageBox("E0102");
				return false;
			}
		}
		// 3�������༭����Ȩ�޿��ƣ�
		if ("ONLYONE".equals(this.getOnlyEditType())) {
			// סԺ���� У����������
			if (this.getAdmType().equals("ODI") && !this.checkDrForEdit()) {
				// this.messageBox("aaaaa");
				this.messageBox("E0102");
				return false;
			}
		}

		// ����Ӧ�ü�����ʾ��Ϣ�ֶΣ����û���ȷ����Ϣ��ʾ;
		TParm nullParm = GetWordValue.getInstance().checkValue(this.getWord());
		// ������ֵ��
		// ���Ҷ�Ӧ�����֣�������
		/**
		 * Vector nullDataGroup = (Vector) nullParm.getData("NULL_GROUP_NAME");
		 * Vector nullDataCode = (Vector) nullParm.getData("NULL_NAME"); String
		 * strNull = ""; if (nullDataGroup != null && nullDataGroup.size() > 0)
		 * { for (int i = 0; i < nullDataGroup.size(); i++) { strNull +=
		 * this.getShowMessage( (String) nullDataGroup.get(i),(String)
		 * nullDataCode.get(i)) + "\r\n"; } this.messageBox(strNull + "������д��");
		 * return false;
		 *
		 * }
		 **/
		// ���볬����Χ
		Vector numDataGroup = (Vector) nullParm.getData("RANG_GROUP_NAME");
		Vector numDataCode = (Vector) nullParm.getData("RANG_NAME");
		String strNum = "";
		if (numDataGroup != null && numDataGroup.size() > 0) {
			for (int i = 0; i < numDataGroup.size(); i++) {
				strNum += this.getShowMessage((String) numDataGroup.get(i),
						(String) numDataCode.get(i))
						+ "\r\n";
			}
			this.messageBox(strNum + "�ѳ�����Χ��");
			return false;

		}

		// this.messageBox("�ļ���" + this.getWord().getFileName());
		if (this.isUloadPic()) {
			boolean isUploadSuccess = uploadPictureToFileServer();
			if (!isUploadSuccess) {
				// ����ʧ��
				this.messageBox("ͼƬ�ϴ�ʧ��.");
				return false;
			}
		}
		//$$=== add by lx 2012/09/11 ���ȡseq��ͻ���� ===$$//
		//System.out.println("==========OnlyEditType========="+this.getOnlyEditType());

		if ("NEW".equals(this.getOnlyEditType())) {
			this.setEmrChildParm(this.getFileServerEmrName());
		}

		// ���浽�û��ļ�
		TParm asSaveParm = this.getEmrChildParm();
		//System.out.println("================asSaveParm=========="+asSaveParm);
		// ����
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy��MM��dd�� HHʱmm��ss��");
		if ("NEW".equals(this.getOnlyEditType())) {
			this.getWord().setMessageBoxSwitch(false);
			// ����(��һ�α���)
			// if(this.word.getFileAuthor()==null||this.word.getFileAuthor().length()==0||"admin".equals(this.word.getFileAuthor()))
			this.getWord().setFileAuthor(Operator.getID());
			// ��˾
			this.getWord().setFileCo("JAVAHIS");
			// ����
			this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
			// ��ע
			this.getWord().setFileRemark(
					asSaveParm.getValue("CLASS_CODE") + "|"
							+ asSaveParm.getValue("FILE_PATH") + "|"
							+ asSaveParm.getValue("FILE_NAME"));
			// ����ʱ��
			this.getWord().setFileCreateDate(dateStr);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			/*System.out.println("==save filePath=="
					+ asSaveParm.getValue("FILE_PATH"));
			System.out.println("==save fileName=="
					+ asSaveParm.getValue("FILE_NAME"));*/
			// ����Ϊ
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			if (diseBasicData != null) {//add by wanglong 20121128
				EMRCreateXMLToolForSD.getInstance().createXML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			} else//add by wanglong 20121128
				EMRCreateXMLTool.getInstance()
				.createXML(asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			// $$===========���ɼ�����뵥html�ļ�Start===========$$//
			// ������ڼ�������,������html
			if (this.getMedApplyNo() != null
					&& !this.getMedApplyNo().equals("")) {
				EMRCreateHTMLTool.getInstance().createHTML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), this.getMedApplyNo(),
						"EmrData");
			}
			// $$===========���ɼ�����뵥html�ļ�End===========$$//
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				this.getWord().setMessageBoxSwitch(true);
				// ���ɱ༭
				this.getWord().setCanEdit(false);
				setTMenuItem(false);
				return false;
			}
			this.getWord().setMessageBoxSwitch(true);
			// ���ɱ༭
			this.getWord().setCanEdit(false);
			setTMenuItem(false);

			// �������ݿ�����
			if (saveEmrFile(asSaveParm)) {
				//TParm returnParm=new TParm();//add by wanglong 20121115
				returnParm.addData("FILE_PATH", asSaveParm.getValue("FILE_PATH"));//add by wanglong 20121115
				returnParm.addData("FILE_NAME", asSaveParm.getValue("FILE_NAME"));//add by wanglong 20121115
				//this.setReturnValue(returnParm);//add by wanglong 20121115
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
				// �����Զ����涨ʱ��
				// this.schedule();
				//this.loadTree();//wuh delete
				this.loadTreeByType();//wuh
				Object obj = this.getParameter();
				TParm emrFileDataParm = this.getEmrChildParm();
				if (obj != null
						&& "SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {
					emrFileDataParm = (TParm) ((TParm) obj)
							.getData("EMR_FILE_DATA");
					emrFileDataParm.setData("FILE_SEQ", asSaveParm
							.getValue("FILE_SEQ"));
					emrFileDataParm.setData("CASE_NO", this.getCaseNo());
					emrFileDataParm.setData("ADM_TYPE", this.getAdmType());
				}
				// ���뵥����
				if ("SQD".equals(emrFileDataParm.getValue("TYPEEMR"))) {

					((TParm) obj).runListener("EMR_SAVE_LISTENER",
							emrFileDataParm);
				} else {
					((TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());
				}
				this.setOnlyEditType("ONLYONE");
				// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/�½�
				OptLogTool.getInstance().writeOptLog(this.getParameter(), "C", emrFileDataParm);
				// ����������ӡ��ʶ�������޸ı�ʶ
				String caseNo = ((TParm) this.getParameter())
						.getValue("CASE_NO");
				String fileSeq = emrFileDataParm.getValue("FILE_SEQ");
				if (!this.checkDrForDel()) {
					setCanPrintFlgAndModifyFlg("", "", false);
				} else {
					setCanPrintFlgAndModifyFlg(caseNo, fileSeq, true);
				}

				// 2015/11/13 ��TEmrWordControlͬ������,�������ҽ��վд�������ֹ�����ҽ��վ��ͳһ������
				// add by wangb 2015/10/20 ���ͼ�����뵥HTML START
				this.sendRisHtml();
				// add by wangb 2015/10/20 ���ͼ�����뵥HTML END

				return true;
			} else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				return false;
			}
		}
		if ("ONLYONE".equals(this.getOnlyEditType())) {
			// ������ʾ������
			this.getWord().setMessageBoxSwitch(false);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());

			// ����
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			if (diseBasicData != null) {//add by wanglong 20121128
				EMRCreateXMLToolForSD.getInstance().createXML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			} else//add by wanglong 20121128
				EMRCreateXMLTool.getInstance()
				.createXML(asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), "EmrData",
						this.getWord());
			// $$===========���ɼ�����뵥html�ļ�Start===========$$//
			// ������ڼ�������,������html
			if (this.getMedApplyNo() != null
					&& !this.getMedApplyNo().equals("")) {
				EMRCreateHTMLTool.getInstance().createHTML(
						asSaveParm.getValue("FILE_PATH"),
						asSaveParm.getValue("FILE_NAME"), this.getMedApplyNo(),
						"EmrData");
			}
			// $$===========���ɼ�����뵥html�ļ�End===========$$//
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				// ������ʾ��Ϊ����ʾ
				this.getWord().setMessageBoxSwitch(true);
				// ���ɱ༭
				this.getWord().setCanEdit(false);
				setTMenuItem(false);
				return false;
			}
			this.getWord().setMessageBoxSwitch(true);
			// ������ʾ��Ϊ����ʾ
			//this.getWord().setMessageBoxSwitch(true);
			// ���ɱ༭
			this.getWord().setCanEdit(false);
			setTMenuItem(false);

			// ��װ��������TParm���༭���棩
			this.setOptDataParm(asSaveParm, 1);
			// ��������
			if (saveEmrFile(asSaveParm)) {
				//TParm returnParm=new TParm();//add by wanglong 20121115
				returnParm.addData("FILE_PATH", asSaveParm.getValue("FILE_PATH"));//add by wanglong 20121115
				returnParm.addData("FILE_NAME", asSaveParm.getValue("FILE_NAME"));//add by wanglong 20121115
				//this.setReturnValue(returnParm);//add by wanglong 20121115
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
				Object obj = this.getParameter();
				((TParm) obj).runListener("EMR_SAVE_LISTENER", new TParm());

				//this.loadTree();//wuh delete
				loadTreeByType();//wuh

				// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/�޸�
				OptLogTool.getInstance().writeOptLog(this.getParameter(), "M", asSaveParm);

				// 2015/11/13 ��TEmrWordControlͬ������,�������ҽ��վд�������ֹ�����ҽ��վ��ͳһ������
				// add by wangb 2015/10/20 ���ͼ�����뵥HTML START
				this.sendRisHtml();
				// add by wangb 2015/10/20 ���ͼ�����뵥HTML END

				return true;
			} else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * ���ò˵���ĿȨ��
	 *
	 * @param flg
	 */
	private void setTMenuItem(boolean flg) {
		this.getTMenuItem("InsertTable").setEnabled(flg);
		this.getTMenuItem("DelTable").setEnabled(flg);
		this.getTMenuItem("InsertTableRow").setEnabled(flg);
		this.getTMenuItem("AddTableRow").setEnabled(flg);
		this.getTMenuItem("DelTableRow").setEnabled(flg);
		this.getTMenuItem("InsertPY").setEnabled(flg);
		this.getTMenuItem("InsertLCSJ").setEnabled(flg);

		this.getTMenuItem("InsertPictureObject").setEnabled(flg);
		this.getTMenuItem("PastePictureObject").setEnabled(flg);
		this.getTMenuItem("InsertTemplatePY").setEnabled(flg);
		this.getTMenuItem("InsertPatInfo").setEnabled(flg);
		this.getTMenuItem("InsertCurrentTime").setEnabled(flg);

		this.getTMenuItem("ClearMenu").setEnabled(flg);
		this.getTMenuItem("delFixText").setEnabled(flg);

	}

	/**
	 * �Ƿ����ת��
	 *
	 * @param caseNo
	 * @return
	 */
	private boolean isAdmChg(String caseNo) {
		boolean flg = false;
		// ������Ʋ���,˵��INDP;
		String sql = "SELECT COUNT(*) FROM ADM_CHG";
		sql += " WHERE PSF_KIND='INDP' AND CASE_NO='" + caseNo + "'";
		sql += " AND CANCEL_FLG='N'";
		//System.out.println("==sql----=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		//System.out.println("==parm=="+parm);
		//System.out.println("===count==="+parm.getCount());
		// >1,˵����ת����
		if (parm.getCount() > 1) {
			flg = true;
		}

		return flg;
	}

	/**
	 * ��ȡ�������ڿ���ʱ�Ķ�Ӧҽʦ(����,����,������)
	 *
	 * @param caseNo
	 * @param dept
	 * @param drType
	 *            (VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE)
	 * @return ҽ������
	 */
	private String getDr(String caseNo, String dept, String drType) {
		String sql = "SELECT " + drType + " FROM ADM_CHG";
		sql += " WHERE CASE_NO='" + caseNo + "' AND DEPT_CODE='" + dept
				+ "' AND " + drType + " IS NOT NULL";
		sql += " ORDER BY SEQ_NO DESC";
		// System.out.println("====sql===="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// �ÿ��Ҵ��� ������ҽʦ
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				if (Operator.getID().equals(parm.getValue(drType, i))) {
					return parm.getValue(drType, i);
				}
			}
		}
		return "";
	}

	/**
	 * �����Ƿ��Ѿ��ύ
	 * @return
	 */
	private boolean getSubmitFLG(){
		String sql="SELECT CHECK_FLG";
		sql+=" FROM MRO_MRV_TECH";
		sql+=" WHERE CASE_NO='"+this.getCaseNo()+"'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() > 0) {
			if( parm.getValue("CHECK_FLG", 0).equals("1")){
				this.messageBox("���ύ�����������޸�!");
				return true;
			}else if(parm.getValue("CHECK_FLG", 0).equals("2")){
				this.messageBox("����˲����������޸�!");
				return true;
			}else if(parm.getValue("CHECK_FLG", 0).equals("3")){
				this.messageBox("�ѹ鵵�����������޸�!");
				return true;
			}
		}

		return false;
	}

	/**
	 * �Ƿ���֪ʶ��
	 * @param classcode
	 * @return
	 */
	private boolean isKnowLedgeStore(String classcode){
		if(classcode.startsWith(KNOWLEDGE_STORE_PREFIX)){
			return true;
		}
		return false;
	}

	/**
	 * ���õ����ֻ�����Ϣ
	 */
	public void setSingleDiseBasicData(){//add by wanglong 20121115
		if (diseBasicData != null) {
			word.setMicroField("PAT_NAME",diseBasicData.getValue("PAT_NAME"));//����
			ESingleChoose esc=(ESingleChoose)word.findObject("HR02.02.001", EComponent.SINGLE_CHOOSE_TYPE);
			String sexCode=diseBasicData.getValue("SEX_CODE");// �Ա����
			if (sexCode.equals("1")) {
				esc.setText("��");
			} else if (sexCode.equals("2")) {
				esc.setText("Ů");
			} else if (sexCode.equals("9")) {
				esc.setText("δ˵��");
			} else {
				esc.setText("δ֪");
			}

			ENumberChoose en=(ENumberChoose)word.findObject("HR02.03.001", EComponent.NUMBER_CHOOSE_TYPE);
			en.setText(diseBasicData.getValue("AGE"));//����
			//System.out.println("==================��Ժ����======================"+diseBasicData.getValue("IN_DATE"));/////////////////////
			word.setMicroField("IN_DATE",diseBasicData.getValue("IN_DATE").split("\\.")[0]);//��Ժ����
			word.setMicroField("OUT_DATE",diseBasicData.getValue("OUT_DATE").split("\\.")[0]);//��Ժ����
			word.setMicroField("STAY_DAYS",diseBasicData.getValue("STAY_DAYS"));//סԺ����
			word.setMicroField("ICD_CODE",diseBasicData.getValue("ICD_CODE"));//��ϴ���
			word.setMicroField("ICD_CHN_DESC",diseBasicData.getValue("ICD_CHN_DESC"));//�������
			word.setMicroField("TBYS",diseBasicData.getValue("TBYS_CHN"));//���ҽʦ
			word.setMicroField("��������",diseBasicData.getValue("BIRTH_DATE"));//��������
			word.setMicroField("������",diseBasicData.getValue("MR_NO"));//������

			ECheckBoxChoose WYZR_Y = (ECheckBoxChoose) word.findObject("WYZR_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose WYZR_N = (ECheckBoxChoose) word.findObject("WYZR_N", EComponent.CHECK_BOX_CHOOSE_TYPE);
			if (diseBasicData.getValue("ADM_SOURCE").equals("09")) {
				WYZR_Y.setChecked(true);// ��Ժת��_�ǣ�ѡ���
				WYZR_N.setChecked(false);// ��Ժת��_��ѡ���
			} else {
				WYZR_Y.setChecked(false);// ��Ժת��_�ǣ�ѡ���
				WYZR_N.setChecked(true);// ��Ժת��_��ѡ���
			}
			ECheckBoxChoose WXPF_Y = (ECheckBoxChoose) word.findObject("WXPF_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);
			ECheckBoxChoose WXPF_N = (ECheckBoxChoose) word.findObject("WXPF_N", EComponent.CHECK_BOX_CHOOSE_TYPE);
			if (diseBasicData.getValue("PATIENT_CONDITION").equals("1")) {
				WXPF_Y.setChecked(true);// Σ������_�ǣ�ѡ���
				WXPF_N.setChecked(false);// Σ������_��ѡ���
			} else {
				WXPF_Y.setChecked(false);// Σ������_�ǣ�ѡ���
				WXPF_N.setChecked(true);// Σ������_��ѡ���
			}

			//ECheckBoxChoose ST_Y=(ECheckBoxChoose)word.findObject("ST_Y", EComponent.CHECK_BOX_CHOOSE_TYPE);//ST��ѡ���
			//ST_Y.setChecked(true);//ST��ѡ���

			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT",diseBasicData.getValue("MR_NO"));//ҳü-������
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",diseBasicData.getValue("IPD_NO"));//ҳü-סԺ��
			allParm.addListener("onDoubleClicked", this,"onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,"onMouseRightPressed");
			word.setWordParameter(allParm);

		}
	}

	/**
	 * ����������Ϣ
	 */
	public void setOPEData() {// add by wanglong 20130514
		if (opeBasicData != null) {
			GetWordValue.getInstance().setWordValueByParm(word, opeBasicData);
		}
	}

	/**
	 * ���ͼ�����뵥HTML
	 * 
	 * @author wangb
	 */
	private void sendRisHtml() {
		if (StringUtils.isNotEmpty(this.getMedApplyNo())) {
			TParm sendHtmlParm = new TParm();
			sendHtmlParm.setData("CASE_NO", this.getCaseNo());
			sendHtmlParm.setData("MR_NO", this.getMrNo());
			sendHtmlParm.setData("APPLICATION_NO", this.getMedApplyNo());

			// ��ѯ������ҽ����Ϣ
			TParm result = MEDApplyTool.getInstance().queryMedApplyInfo(sendHtmlParm);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
				+ result.getErrName());
				return;
			}

			sendHtmlParm.setData("ORDER_CAT1_CODE", result.getValue("ORDER_CAT1_CODE", 0));

			// �������뵥HTML�ļ�
			Hl7Communications.getInstance().CreatRisHtml(sendHtmlParm);
		}
	}

	/**
	 * ����ģ�塱�����¼�
	 */
	public void onSubTemplate() {
		if (!word.canEdit()) {
			messageBox("��ѡ����ģ��!");
			return;
		} else {
			openSubTemplate();
		}
	}

	/**
	 * ����ģ��ѡ�񴰿�
	 */
	public void openSubTemplate() {
		TParm parm = new TParm();
		parm.addListener("onReturnTemplateContent", this, "onReturnTemplateContent");
		parm.setData("TWORD", this.getWord());
		TWindow window = (TWindow) openWindow("%ROOT%\\config\\emr\\EMRSubTemplate.x", parm, true);
		//        window.setBounds(13, 157, 270, 566);
		TPanel wordPanel = ((TPanel) this.getComponent("TREEPANEL"));
		window.setX(wordPanel.getX() + 12);
		window.setY(132);
		window.setVisible(true);
	}

	/**
	 * �������ַ��������¼�
	 */
	public void onSpecialChars() {
		if (!word.canEdit()) {
			messageBox("��ѡ����ģ��!");
			return;
		} 
		TParm parm = new TParm();
		parm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow)openWindow("%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);// wanglong add 20140908
		//        window.setX(ImageTool.getScreenWidth() - window.getWidth());
		//        window.setY(0);
		TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
		window.setX(wordPanel.getX() + 10);
		window.setY(130);
		window.setVisible(true);
		AWTUtilities.setWindowOpacity(window, 0.8f);
	}

	/**
	 * �����������ࡱ�����¼�
	 */
	public void onNationStandard() {
		TREE_TYPE = TREE_TYPE_1;
		this.loadTree();
	}

	/**
	 * ��ҽ�ƹ��̡������¼�
	 */
	public void onCustomSort1() {
		TREE_TYPE = TREE_TYPE_2;
		this.loadTreeNew();
	}

	/**
	 * �����ҷ��ࡱ�����¼�
	 */
	public void onCustomSort2() {
		TREE_TYPE = TREE_TYPE_3;
		this.loadTreeNew();
	}

	/**
	 * �����û��Զ����������
	 */
	private void loadTreeNew() {
		//        String name = TREE_TYPE;
		//        treeRoot.removeAllChildren();
		//        treeRoot.setText(name);
		//        treeRoot.setType("Root");
		//        treeRoot.setID("0");
		//        eto.doLoadTreeByTypeName(treeRoot, name, this.getCaseNo(), this.getMrNo());

		boolean queryFlg = false;
		// סԺ
		if (this.getSystemType().equals("ODI")) {
			this.setValue("IPD_NO", this.getIpdNo());
			isCheckUserDr();
		}
		// ����
		if (this.getSystemType().equals("ODO")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// ����
		if (this.getSystemType().equals("EMG")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("E")) {
				queryFlg = true;
			}
		}
		// �������
		if (this.getSystemType().equals("HRM")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("H")) {
				queryFlg = true;
			}
		}
		// ���ﻤʿվ
		if (this.getSystemType().equals("ONW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("O")) {
				queryFlg = true;
			}
		}
		// סԺ��ʿվ
		if (this.getSystemType().equals("INW")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("I")) {
				queryFlg = true;
			}
		}
		// ��Ⱦ����
		if (this.getSystemType().equals("INF")) {
			((TTextField) this.getComponent("IPD_NO")).setVisible(false);
			((TLabel) this.getComponent("IPD_LAB")).setVisible(false);
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// ����
		if (this.getSystemType().equals("OPE")) {// add by wanglong 20121220
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}
		// ������
		if (this.getSystemType().equals("SD")) {// add by wanglong 20121120
			if (!this.getType().equals("F")) {
				queryFlg = true;
			}
		}

		TParm rootParm = this.getTreeNewRoot(); // ȡ���ڵ�����
		treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));
		treeRoot.setType("Root");// �ڵ�����
		treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
		treeRoot.removeAllChildren(); // ����������Ҷ�ӽڵ�
		TParm childrenParm = this.getTreeNewChildren(); // �õ�Tree�Ļ�������
		// System.out.println("��ѯ������������:" + children);
		// ��ʼ��ʼ����������
		for (int i = 0; i < childrenParm.getCount(); i++) {
			TParm childParm = childrenParm.getRow(i);
			this.putNodeInTree(childParm, treeRoot);
		}
		//        int count = getTTree(TREE_NAME).getTree().getRowCount();
		//        System.out.println("----------------count-----"+count);
		//        for (int i = 0; i < count; i++) {
		//            TTreeNode nowNode = getTTree(TREE_NAME).getTree().getNodeForRow(i);
		//            System.out.println("----------------nowNode.getType()-----"+nowNode.getType());
		//            if (nowNode.getType().equals("3")) {
		//                this.getTreeNewFileIndex(nowNode, queryFlg);
		//            }
		//        }
		this.visitAllNodes(treeRoot,queryFlg);
		getTTree(TREE_NAME).update();
	}

	/**
	 * ȡ���û��Զ������ĸ��ڵ�����
	 * @return
	 */
	private TParm getTreeNewRoot() {
		String sql = "";
		if (TREE_TYPE.equals(TREE_TYPE_2)) {
			sql =
					"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' AND PARENT_CLASS_CODE IS NULL";
		} else if (TREE_TYPE.equals(TREE_TYPE_3)) {
			sql =
					"SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' AND PARENT_CLASS_CODE IS NULL";
		}
		TParm result = new TParm(this.getDBTool().select(sql));
		return result;
	}

	/**
	 * ȡ���û��Զ��������ӽڵ�����
	 * @return
	 */
	private TParm getTreeNewChildren() {
		return this.getTreeNewChildren("");
	}

	/**
	 * ȡ���û��Զ��������ӽڵ�����
	 * @return
	 */
	private TParm getTreeNewChildren(String classCode) {
		String sql = "";
		if (TREE_TYPE.equals(TREE_TYPE_2)) {
			sql =
					"SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
							+ "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' # ORDER BY LEAF_FLG,SEQ";
		} else if (TREE_TYPE.equals(TREE_TYPE_3)) {
			sql =
					"SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
							+ "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
							+ " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' # ORDER BY LEAF_FLG,SEQ";
		}
		if (!StringUtil.isNullString(classCode)) {
			sql = sql.replaceFirst("#", " AND CLASS_CODE = '@' ".replaceFirst("@", classCode));
		} else {
			sql = sql.replaceFirst("#", "");
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ���ӽڵ���뵽����
	 * @param childParm
	 * @param root
	 */
	private void putNodeInTree(TParm childParm, TTreeNode root) {
		String noteType = childParm.getValue("CLASS_STYLE"); // �ڵ�����
		if (childParm.getValue("LEAF_FLG").equals("Y")) {
			noteType = "3";
		}
		TTreeNode node = new TTreeNode(childParm.getValue("CLASS_DESC"), noteType);
		node.setID(childParm.getValue("CLASS_CODE"));
		node.setGroup(childParm.getValue("CLASS_CODE"));
		//        node.setData(childParm);
		String parentID = childParm.getValue("PARENT_CLASS_CODE");
		// System.out.println("parentID-------------:" + parentID);
		if (root.findNodeForID(childParm.getValue("CLASS_CODE")) != null) {
			// System.out.println("�Ѿ����д˽ڵ㲻��ִ�����Ӳ���");
		} else if (root.findNodeForID(parentID) != null) {
			root.findNodeForID(parentID).add(node);
			//            if (root.findNodeForID(parentID) != root) {
			//                root.findNodeForID(parentID).setType("2");
			//            }
		} else {
			TParm parentParm = this.getTreeNewChildren(parentID);
			// System.out.println("��ѯ���ĸ��ڵ�:" + resultParm);
			if (parentParm.getCount() <= 0) {
				root.add(node);
			} else {
				this.putNodeInTree(parentParm.getRow(0), root);
				root.findNodeForID(parentID).add(node);
			}
		}
	}

	/**
	 * 
	 * @param node
	 * @param queryFlg
	 */
	public void visitAllNodes(TTreeNode node, boolean queryFlg) {
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TTreeNode nowNode = (TTreeNode) e.nextElement();
				if (nowNode.getType().equals("3")) {
					this.getTreeNewFileIndex(nowNode, queryFlg);
				} else visitAllNodes(nowNode, queryFlg);
			}
		}
	}

	/**
	 * Ҷ�ڵ�ļ������ļ�
	 * @param leafNode
	 * @param queryFlg
	 */
	private void getTreeNewFileIndex(TTreeNode leafNode, boolean queryFlg) {
		TParm childParm = new TParm();
		// �ж��Ƿ����ٴ�֪ʶ�⣬
		// ��������,�����Ӧ�Ķ�Ӧ��֪ʶ���ļ�
		if (isKnowLedgeStore(leafNode.getID())) {
			childParm = getChildNodeDataByKnw(leafNode.getID());
		} else {
			// ��ѯ���ļ�
			childParm = this.getRootChildNodeData(leafNode.getID(), queryFlg);
		}
		int rowCldCount = childParm.getCount();
		for (int z = 0; z < rowCldCount; z++) {
			TParm chlidParmTemp = childParm.getRow(z);
			TTreeNode nodeChlid = new TTreeNode();
			nodeChlid.setID(chlidParmTemp.getValue("FILE_NAME"));
			nodeChlid.setText(chlidParmTemp.getValue("DESIGN_NAME"));
			nodeChlid.setType("4");
			nodeChlid.setGroup(chlidParmTemp.getValue("CLASS_CODE"));
			nodeChlid.setValue(chlidParmTemp.getValue("SUBCLASS_CODE"));
			nodeChlid.setData(chlidParmTemp);
			leafNode.add(nodeChlid);
		}
		if (rowCldCount > 0) {
			leafNode.setText("(�����)" + leafNode.getText());
		} else {
			leafNode.setText("(δ���)" + leafNode.getText());
		}
	}

	/**
	 * �趨��ɸ�����
	 * 
	 * @param parm
	 * @param macroViewParm
	 * @author wangb
	 */
	private void setInfectResult(TParm parm, TParm macroViewParm) {
		// ��ɸ���
		String infectResult = "��;";
		int infectCount = 0;
		int count = 0;
		TParm result = new TParm();

		for (int m = 0; m < macroViewParm.getCount(); m++) {
			if ("Y".equals(macroViewParm.getValue("INFECT_FLG", m))) {
				result.addData("MACRO_NAME", macroViewParm.getValue("MACRO_NAME", m));
				result.addData("MACRO_VALUE", macroViewParm.getValue("MACRO_VALUE", m));
				infectCount++;
			}
		}

		result.setCount(infectCount);

		// δ�鵽��ɸ����˵����δ�ش�
		if (result.getCount() == 0) {
			parm.addData("��ɸ���", "��");
			return;
		}

		for (int i = 0; i < result.getCount(); i++) {
			// ���ڴ�ɸ�ش����Ϊ�ı�������ֻ�ܸ����Ƿ������Ե����ֿ�ͷ���ж�
			if (result.getValue("MACRO_VALUE", i).startsWith("��")) {
				infectResult = infectResult
						+ result.getValue("MACRO_NAME", i) + ";";
			} else if (result.getValue("MACRO_VALUE", i).startsWith("��")) {
				count++;
			}
		}

		// �ش�����ȫΪ����
		if (count == result.getCount()) {
			parm.addData("��ɸ���", "����");
		} else {
			parm.addData("��ɸ���", infectResult.substring(0, infectResult
					.length() - 1));
		}

		parm.addData("SYSTEM", "COLUMNS", "��ɸ���");
	}

}