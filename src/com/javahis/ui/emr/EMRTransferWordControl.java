package com.javahis.ui.emr;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMXMLTool;
import jdo.erd.ERDCISVitalSignTool;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;


/**
 * ���ӵ�
 * @author yufh
 */
public class EMRTransferWordControl extends TControl implements DMessageIO {
	private String hospAreaName = "";//ҽԺȫ��
	private String hospEngAreaName = "";//Ӣ��ȫ��
	private static final String TWORD = "WORD";//WORD����
	private String caseNo;//�����
	private String mrNo;//������	
	private String patName;//����
	private String onlyEditType;// ��ǰ�༭״̬
	//	private Timestamp admDate;//��������
	//	private String ipdNo;//סԺ��
	private String deptCode;//����
	private TParm emrChildParm = new TParm();//��������� 
	private String subFileName;//�����ļ�����
	private String yearStr="";//��
	private String mouthStr="";//��
	private TWord word;//WORD����
	private String transfer_no ="";//���ӵ���
	private String fromUser;//������
	private String toUser;//������
	private String opBookSeq;//�������뵥��
	private String dayOpeFlg;//�ռ��������



	public String getDayOpeFlg() {
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {
		this.dayOpeFlg = dayOpeFlg;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public TParm getEmrChildParm() {
		return emrChildParm;
	}

	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
	}
	public String getOnlyEditType() {
		return onlyEditType;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}
	//	public String getTransferNo() {
	//		return transferNo;
	//	}
	//
	//	public void setTransferNo(String transferNo) {
	//		this.transferNo = transferNo;
	//	}
	public void onInit() {
		super.onInit();
		this.hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		this.hospEngAreaName = Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion());
		// ��ʼ��WORD
		initWord();
		// ��ʼ������
		initPage();
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}
	/**
	 * �õ�WORD����
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	/**
	 * ��ʼ������
	 */
	public void initPage() {
		Object obj = this.getParameter();
		if (obj != null) {
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setPatName(((TParm) obj).getValue("PAT_NAME"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			this.setDayOpeFlg(((TParm) obj).getValue("DAY_OPE_FLG"));
			opBookSeq = ((TParm) obj).getValue("OPBOOK_SEQ");

			//this.messageBox("2."+getDayOpeFlg());
			//�򿪲���
			openfile(obj);
		}
		yearStr = caseNo.substring(0, 2);
		mouthStr = caseNo.substring(2, 4);
	}
	/**
	 * �򿪲���
	 */
	public void openfile(Object obj) {
		TParm action = (TParm) obj;
		
//		this.messageBox("===flg: "+action.getBoolean("FLG"));
		//�򿪽��ӵ�����
		if (action.getBoolean("FLG")) {
			String filePath = action.getValue("TRANSFER_FILE_PATH");
			String fileName = action.getValue("TRANSFER_FILE_NAME");
			//			System.out.println("filePath====="+filePath);
			//			System.out.println("fileName====="+fileName);
			this.getWord().onOpen(filePath,fileName, 3, false);
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			this.getWord().setWordParameter(allParm);
			// ���ñ༭״̬
			this.setOnlyEditType("ONLYONE");
			//ץȡʱ��
			EComponent com = this.getWord().getPageManager().findObject(
					"����ʱ��", EComponent.FIXED_TYPE);
			EFixed d =(EFixed) com;
			action.setData("D",d.getText());
			//�༭
			onEdit();	      
			// ���õ�ǰ�༭����
			this.setEmrChildParm(action);
		}else {
			// �򿪽��ӵ�ģ��
			String templetPath = action.getValue("TEMPLET_PATH");
			String templetName = action.getValue("EMT_FILENAME");
			this.getWord().onOpen(templetPath, templetName, 2, false);
			// ����ת�����
			word.setMicroField("ת�����", this.getDeptDesc(action
					.getValue("TO_DEPT")));
			word.setMicroField("��ʽ", action.getValue("OPT_CHN_DESC"));
			// ץȡʱ��
			EComponent com = this.getWord().getPageManager().findObject("����ʱ��",
					EComponent.FIXED_TYPE);
			EFixed d = (EFixed) com;
			action.setData("D", d.getText());
			this.getWord().onEditWord();
			setMicroField();
			// ���ñ༭״̬
			this.setOnlyEditType("NEW");
			// �༭
			onEdit();

			// ���õ�ǰ�༭����
			this.setEmrChildParm(action);
			

			// add by wangb 2016/1/25  �������ɵĽ��ӵ�����
			if ("ET,EO,EW".contains(action.getValue("TRANSFER_CLASS"))) {
				EFixed title = (EFixed) this.getWord().getPageManager().findObject(
						"����", EComponent.FIXED_TYPE);
				if (title != null) {
					String strTitle = "";
					if ("ET".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "��������뽻�ӵ�";
					} else if ("EO".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "�����������ҽ��ӵ�";
					} else if ("EW".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "�����벡�����ӵ�";
					}

					if (StringUtils.isNotEmpty(strTitle)) {
						title.setText(strTitle);
						this.getWord().update();
					}

					// �򽻽ӵ��д��벡�����µ��������������Ϣ
					this.setCISVitalsignData(action.getValue("REG_CASE_NO"));
					// �򽻽ӵ��д��벡���ż������
					this.setOpdDiagData(action.getValue("REG_CASE_NO"));
				}
			}
		}
	}
	/**
	 * �Ƿ�༭
	 */
	private void onEdit(){
		// �ɱ༭
		this.getWord().setCanEdit(true);
	}
	/**
	 * ���ú�
	 */
	private void setMicroField() {
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		allParm.addListener("onDoubleClicked", this,"onDoubleClicked");
		this.getWord().setWordParameter(allParm);
		this.setCaptureValue("DAY_OPE_FLG", this.getDayOpeFlg());
		setMicroFieldOne(true);
	}
	private void setMicroFieldOne(boolean falg){

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

				if (parm.getCount() > 0) {
					for (String parmName : parm.getNames()) {
						parm.addData(parmName, parm.getValue(parmName, 0));
					}

				} else {
					for (String parmName : parm.getNames()) {
						parm.addData(parmName, "");
					}

				}
				String dateStr = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd HH:mm:ss");
				parm.addData("����", age);
				parm.addData("�����", this.getCaseNo());
				parm.addData("������", this.getMrNo());
				parm.addData("����", this.getDeptDesc(Operator.getDept()));
				parm.addData("������", Operator.getName());
				parm.addData("��������", dateStr);
				parm.addData("����", StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd"));
				parm.addData("ʱ��", StringTool.getString(SystemTool.getInstance()
						.getDate(), "HH:mm:ss"));	
				parm.addData("����ʱ��", dateStr);
				parm.addData("��Ժʱ��", StringTool.getString(new java.sql.Timestamp(System
						.currentTimeMillis()), "yyyy/MM/dd"));

				String sqldept = " SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B"+
						" WHERE A.MR_NO = '" + this.getMrNo()+ "'"+
						" AND A.CASE_NO = '" + this.getCaseNo()+ "'"+
						" AND A.DEPT_CODE = B.DEPT_CODE";
				TParm result = new TParm(TJDODBTool.getInstance().select(sqldept)); 		
				parm.addData("���ÿ���", result.getValue("DEPT_CHN_DESC",0));
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "�����");
				parm.addData("SYSTEM", "COLUMNS", "������");
				parm.addData("SYSTEM", "COLUMNS", "סԺ��");
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "������");
				parm.addData("SYSTEM", "COLUMNS", "��������");
				parm.addData("SYSTEM", "COLUMNS", "����");
				parm.addData("SYSTEM", "COLUMNS", "ʱ��");
				parm.addData("SYSTEM", "COLUMNS", "����ʱ��");
				parm.addData("SYSTEM", "COLUMNS", "��Ժʱ��");
				parm.addData("SYSTEM", "COLUMNS", "���ÿ���");

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
				// ����ʷ(MR_NO);
				StringBuffer drugStr = new StringBuffer();
				TParm drugParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT A.CASE_NO,A.MR_NO,CASE A.DRUG_TYPE "
								// MODIFIED BY WANGQING 20170411
										+ " WHEN 'A' THEN TO_CHAR((SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='PHA_INGREDIENT' AND B.ID=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'B' THEN TO_CHAR((SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'C' THEN TO_CHAR((SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='SYS_ALLERGYTYPE' AND B.ID=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'D' THEN TO_CHAR((SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'E' THEN TO_CHAR((SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'N' THEN TO_CHAR('��') "
										+ " ELSE TO_CHAR('') END AS ALLERGY_NAME,OPT_DATE  "
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
				// ��ѯ�������б���ͼ		
				List<String> macroNameList = new ArrayList<String>();
				String sql = "SELECT MACRO_NAME,MACRO_VALUE,INFECT_FLG FROM MACRO_PHYSIDX_VIEW A, EMR_MICRO_CONVERT B WHERE CASE_NO = '"
						+ this.getCaseNo()
						+ "' AND A.MACRO_NAME = B.MICRO_NAME AND A.MACRO_CODE = B.MACRO_CODE ORDER BY EPISODE_DATE DESC ";
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
				//ɸ����
				this.setInfectResult(parm, macroViewParm);
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
					// ?��������?�Դ��ŵĴ���)
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
						obj.setData(temp, "TEXT", tempValue);
					} else {
						obj.setData(temp, "TEXT", tempValue);
					}
				}

				this.getWord().setWordParameter(obj);		
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
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ�����
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}
	/**
	 * �õ������˺ͽӰ�������
	 */
	public String getUserName(String userId) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
						+ userId + "'"));
		return parm.getValue("USER_NAME", 0);
	}
	/**
	 * �õ���ʽ����
	 */
	public String getOPDesc(String opCode) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"
						+ opCode + "'"));
		return parm.getValue("OPT_CHN_DESC", 0);
	}
	/**
	 * ����Ҽ��¼�
	 */
	public void onMouseRightPressed() {
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		if (!this.getWord().canEdit()) {
			return;
		}
		// ץȡ��
		if (e instanceof ECapture) {
			return;
		}
	}

	/**
	 * ����
	 */
	public boolean onSave() {
		this.getWord().setMessageBoxSwitch(false);		
		this.getWord().setFileAuthor(Operator.getID());
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		// ����ʱ��
		this.getWord().setFileCreateDate(dateStr);
		// ����޸���
		this.getWord().setFileLastEditUser(Operator.getID());
		// ����޸�����
		this.getWord().setFileLastEditDate(dateStr);
		// ����޸�IP
		this.getWord().setFileLastEditIP(Operator.getIP());
		String fileName ="";//�ļ�����
		String filePath ="";//�ļ�·��
		String transferNo="";//���ӵ���
		TParm asSaveParm=new TParm();
		//���ɽ��ӵ�
		if (this.getOnlyEditType().equals("NEW")) {
			if(getEmrChildParm().getValue("TRANSFER_CLASS").equals("WT")){
				String Dept = getEmrChildParm().getValue("TO_DEPT");//ת�����
				//��С�����
				if(!Dept.equals("030202")){
					ESingleChoose com1 = (ESingleChoose)word.findObject(
							"Ƥ��׼��", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com2 = (ESingleChoose)word.findObject(
							"������", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com3 = (ESingleChoose)word.findObject(
							"�����", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com4 = (ESingleChoose)word.findObject(
							"���", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com5 = (ESingleChoose)word.findObject(
							"Ƥ��", EComponent.SINGLE_CHOOSE_TYPE);
					//			 System.out.println("com1====="+com1.getText());
					//			 System.out.println("com2====="+com2.getText());
					//			 System.out.println("com3====="+com3.getText());
					//			 System.out.println("com4====="+com4.getText());
					//			 System.out.println("com5====="+com5.getText());
					if(com1.getText().equals("��ѡ")||
							com2.getText().equals("��ѡ")||
							com3.getText().equals("��ѡ")||
							com4.getText().equals("��ѡ")||
							com5.getText().equals("��ѡ")){
						this.messageBox("���ʵ�������,��δѡ�����Ŀ");
						return false;	 
					}
				}
				else{//С�����
					ESingleChoose com6 = (ESingleChoose)word.findObject(
							"��ʳˮ", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com7 = (ESingleChoose)word.findObject(
							"�㱳�������", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com8 = (ESingleChoose)word.findObject(
							"������", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com9 = (ESingleChoose)word.findObject(
							"���ƻ����", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com10 = (ESingleChoose)word.findObject(
							"�������", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com11 = (ESingleChoose)word.findObject(
							"����Ƥ��", EComponent.SINGLE_CHOOSE_TYPE);
					//			  System.out.println("com6====="+com6.getText());
					//			  System.out.println("com7====="+com7.getText());
					//			  System.out.println("com8====="+com8.getText());
					//			  System.out.println("com9====="+com9.getText());
					//			  System.out.println("com10====="+com10.getText());
					//			  System.out.println("com11====="+com11.getText());
					if(com6.getText().equals("��ѡ")||
							com7.getText().equals("��ѡ")||
							com8.getText().equals("��ѡ")||
							com9.getText().equals("��ѡ")||
							com10.getText().equals("��ѡ")||
							com11.getText().equals("��ѡ")){
						this.messageBox("���ʵ�������,��δѡ�����Ŀ");
						return false;	 
					}
				}
			}
			//ȡ��ԭ��õ����ӵ���
			transferNo = SystemTool.getInstance().getNo("ALL", "MRO",
					"TRANSFER_NO","TRANSFER_NO");
			String name = getEmrChildParm().getValue("EMT_FILENAME");
			if(transfer_no.equals(""))
				fileName = caseNo + "_" + name + "_" + transferNo;
			else
				fileName = caseNo + "_" + name + "_" + transfer_no;	
			filePath = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			//			System.out.println("fileName======"+fileName);
			//			System.out.println("filePath======"+filePath);
			asSaveParm.setData("STATUS_FLG","4");//������
			asSaveParm.setData("FROM_DEPT",getEmrChildParm().getValue("FROM_DEPT"));//ת������
			asSaveParm.setData("TO_DEPT",getEmrChildParm().getValue("TO_DEPT"));//ת�����
		}
		//���ս��ӵ�(�����޸�����)
		else{
			//			System.out.println("D======"+getEmrChildParm().getValue("D"));			
			//			System.out.println("fromUser======"+fromUser);
			//			System.out.println("toUser===="+toUser);
			if(getEmrChildParm().getValue("D").equals("����ʱ��")){
				if(fromUser==null){
					this.messageBox("������δ¼��,���ܱ���");
					return false;
				}
				if(toUser==null){
					this.messageBox("�Ӱ���δ¼��,���ܱ���");
					return false;
				}
				if(fromUser.equals(toUser)){
					this.messageBox("�����˺ͽӰ�����ͬһ��,���ܱ���");
					return false;
				} 
				if (this.messageBox("ѯ��", "�����Ľ��ӵ��������޸�,�Ƿ񱣴�", 2) != 0)
					return false;	
			}else{
				this.messageBox("���ӵ�������,�����޸�");
				return false;
			}
			transferNo = getEmrChildParm().getValue("TRANSFER_CODE");
			String name = getEmrChildParm().getValue("TRANSFER_FILE_NAME");
			fileName = name;
			filePath = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("STATUS_FLG","5");//�ѽ���		
		}
		boolean success = this.getWord().onSaveAs(filePath, fileName, 3);
		if(success){
			asSaveParm.setData("TRANSFER_CODE",transferNo);
			asSaveParm.setData("TRANSFER_FILE_PATH",filePath);
			asSaveParm.setData("TRANSFER_FILE_NAME",fileName);
			asSaveParm.setData("CASE_NO",this.getCaseNo());
			asSaveParm.setData("MR_NO",this.getMrNo());
			asSaveParm.setData("PAT_NAME",this.getPatName());
			asSaveParm.setData("OPBOOK_SEQ",this.opBookSeq);
			asSaveParm.setData("TRANSFER_CLASS",
					getEmrChildParm().getValue("TRANSFER_CLASS"));//���ӵ�����
			asSaveParm.setData("ONLY_EDIT_TYPE", this.getOnlyEditType());
			asSaveParm.setData("TRANSFER_NO", this.transfer_no);
			asSaveParm.setData("OPT_USER", Operator.getID());//������Ա
			asSaveParm.setData("OPT_TERM", Operator.getIP());//�����ն�
			asSaveParm.setData("FROM_USER", this.fromUser);
			asSaveParm.setData("TO_USER", this.toUser);
			asSaveParm.setData("OP_DESC", word.getCaptureValue("��ʽ"));
			TParm resultSave = TIOM_AppServer.executeAction("action.emr.EMRTransferWordAction", "saveTransferFile",asSaveParm);
			if(resultSave.getErrCode() < 0){
				this.messageBox("����ʧ��");
				return false;
			}
			this.messageBox("����ɹ�");
			// add by wangb 2015/12/18 �����������ӵ�����ʱ��������״̬�����ʹ�����Ϣ START
			// ��ѯ��ǰ������Ϣ
			TParm inwTransInfo = this.selectInwTransInfo(transferNo);
			if (inwTransInfo.getErrCode() < 0) {
				return true;
			} else if (inwTransInfo.getCount() > 0) {
				String opeStatus = "";
				// ���������Ϣδ���͹�
				if (StringUtils.equals("N", inwTransInfo.getValue(
						"OPE_MSG_SEND_FLG", 0))
						&& StringUtils.isNotEmpty(opBookSeq)) {
					// ������ӵ�Ϊ����_�����ҽӰ��˷ǿգ����������״̬�����ʹ�����Ϣ
					if (StringUtils.equals("WT", inwTransInfo.getValue(
							"TRANSFER_CLASS", 0))
							&& StringUtils.isNotEmpty(inwTransInfo.getValue(
									"TO_USER", 0))) {
						// 4_���������ȴ�
						opeStatus = "4";

						// ������ӵ�Ϊ����_CCU/�����ҽ��ӿ��ҷǿգ����������״̬�����ʹ�����Ϣ
					} else if (StringUtils.equals("TC/TW", inwTransInfo.getValue(
							"TRANSFER_CLASS", 0))
							&& StringUtils.isNotEmpty(inwTransInfo.getValue(
									"FROM_DEPT", 0))) {
						// 4_������������
						opeStatus = "7";
					}

					if (StringUtils.isNotEmpty(opeStatus)) {
						// ��������״̬
						OPEOpBookTool.getInstance().updateOpeStatus(opBookSeq,
								opeStatus, null);

						// ������ӿڷ�����Ϣ
						TParm xmlParm = ADMXMLTool.getInstance()
								.creatOPEStateXMLFile(
										inwTransInfo.getValue("CASE_NO", 0),
										opBookSeq);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
						} else {
							// ���½��ӵ���Ϣ����ע��
							this.updateInwTransMsgSendFlg(transferNo);
						}
					}
				}
				
				//add by huangtt 20170503  start ������-ICU���ӵ��Ӱ���ǩ�ֱ���ʱ�����Ͳ�����Ϣ�ļ����Խ���ʱ����Ϊ��ICUʱ��
				EComponent com1 = this.getWord().getPageManager().findObject(
						"����ʱ��", EComponent.FIXED_TYPE);
				EFixed d1 =(EFixed) com1;
				if (StringUtils.equals("OI", inwTransInfo.getValue(
						"TRANSFER_CLASS", 0)) && toUser != null){
					String TransferToICUTime = "";
					if(d1 != null){
//						System.out.println("1-----"+d1.getText());
						
						TransferToICUTime =d1.getText().replaceAll("/", "-");

//						System.out.println("TransferToICUTime--"+TransferToICUTime);
						if(TransferToICUTime.length() > 0){
							TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFileTime(caseNo,TransferToICUTime);
					        if (xmlParm.getErrCode() < 0) {
					            this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
					        }
							
						}
						
					}
					
					//����������Ϣ
					
//					System.out.println("��ʽ--------"+word.getCaptureValue("��ʽ"));
					// add by wangb 2017/09/04 �����ӵ�����ʽδ���򲻷���������Ϣ������ʹ�ÿյ���ʽ���ݸ��Ǵ���ԭ����Ϣ
					if (StringUtils.isNotEmpty(word.getCaptureValue("��ʽ"))) {
						TParm xmlParm = ADMXMLTool.getInstance()
								.creatOPEInfoXMLFile(this.getCaseNo(),
										this.opBookSeq,
										word.getCaptureValue("��ʽ"));
						if (xmlParm.getErrCode() < 0) {
							this
									.messageBox("�������ӿڷ���ʧ�� "
											+ xmlParm.getErrText());
						}
					}
				}
				
				//add by huangtt 20170503  end ������-ICU���ӵ��Ӱ���ǩ�ֱ���ʱ�����Ͳ�����Ϣ�ļ����Խ���ʱ����Ϊ��ICUʱ��
				
				// modify by wangb 2017/09/04 ֻҪ���������ż����뽻�ӵ����У������û��ڴ���ʱ����д�����������������Ź������ϵ�����
				// add by wangb 2017/05/31 ��������صĽ��ӵ������������ź���ʽ���������ݿ���(���ӵ��е���ʽ�����׼ȷ) START
				//modify by wangjc 20171207 ����һ������д��action��
//				if (StringUtils.isNotEmpty(opBookSeq)) {
//					String sql = "UPDATE INW_TRANSFERSHEET SET OPBOOK_SEQ = '"
//							+ opBookSeq + "',OP_DESC = '"
//							+ word.getCaptureValue("��ʽ")
//							+ "' WHERE TRANSFER_CODE = '" + transferNo + "'";
//					TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//				}
				// add by wangb 2017/05/31 ��������صĽ��ӵ������������ź���ʽ���������ݿ���(���ӵ��е���ʽ�����׼ȷ) END
			}
			// add by wangb 2015/12/18 ����-���뽻�ӵ�����ʱ��������״̬�����ʹ�����Ϣ END
		
		}
		return true;
	}
	/**
	 * ������ǩ��
	 */
	public void onFromuser() {
		String type = "transfer";	
		TParm Parm  = new TParm();
		Parm.setData("TYPE",type);
		Parm.addListener("onReturnfromuser", this,
				"onReturnfromuser");		
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(Parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);			
	} 
	/**
	 * �Ӱ���ǩ��
	 */
	public void onTouser() {
		String type = "transfer";
		TParm Parm  = new TParm();
		Parm.setData("TYPE",type);
		Parm.addListener("onReturntouser", this,
				"onReturntouser");
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(Parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);	
	}
	public void onReturnfromuser(TParm inParm) {
		//		System.out.println("onReturnfromuser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		fromUser =inParm.getValue("USER_ID");
		//	      System.out.println("fromUser====="+fromUser);
		EComponent com = this.getWord().getPageManager().findObject(
				"������", EComponent.FIXED_TYPE);
		EFixed d =(EFixed) com;
		//	     System.out.println("EFixed������====="+d);
		if (d != null) {
			d.setText(this.getUserName(fromUser));
			this.getWord().update();	
		}   
	}
	public void onReturntouser(TParm inParm) {
		//		System.out.println("onReturntouser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		toUser =inParm.getValue("USER_ID");
		//	      System.out.println("toUser====="+toUser);
		EComponent com = this.getWord().getPageManager().findObject(
				"�Ӱ���", EComponent.FIXED_TYPE);
		EFixed d =(EFixed) com;
		//			System.out.println("EFixed�Ӱ���====="+d);
		if (d != null) {
			d.setText(this.getUserName(toUser));
			this.getWord().update();
		}
		EComponent com1 = this.getWord().getPageManager().findObject(
				"����ʱ��", EComponent.FIXED_TYPE);
		EFixed d1 =(EFixed) com1;		
		if (d1 != null) {
			String dateStr = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyy/MM/dd HH:mm:ss");
			d1.setText(dateStr);
			this.getWord().update();
		}
	}
	/**
	 * ���潻�ӵ������ݿ�
	 */
	public boolean saveTransferFile(TParm parm) {
		//		System.out.println("saveTransferFile======"+parm);
		TParm result = new TParm();
		String optUser = Operator.getID();//������Ա
		String optTerm = Operator.getIP();//�����ն�
		String transferCode = parm.getValue("TRANSFER_CODE");//���ӵ���
		String transferFilePath = parm.getValue("TRANSFER_FILE_PATH");//�ļ�·��
		String transferFileName = parm.getValue("TRANSFER_FILE_NAME");//�ļ�����
		String mrNo =  parm.getValue("MR_NO");//������
		String caseNo =  parm.getValue("CASE_NO");//�����
		String patName =  parm.getValue("PAT_NAME");//����
		String fromDept =  parm.getValue("FROM_DEPT");//ת������
		String toDept =  parm.getValue("TO_DEPT");//ת�����
		String statusFlg = parm.getValue("STATUS_FLG");//����״̬
		String transferClass = parm.getValue("TRANSFER_CLASS");//���ӵ�����
		TConnection conn= TDBPoolManager.getInstance().getConnection();
		if (this.getOnlyEditType().equals("NEW")) {
			if(transfer_no.equals("")){
				String sql= " INSERT INTO INW_TRANSFERSHEET(TRANSFER_CODE,TRANSFER_FILE_PATH," +
						" TRANSFER_FILE_NAME,MR_NO,CASE_NO,PAT_NAME,FROM_DEPT,TO_DEPT," +
						" STATUS_FLG,TRANSFER_CLASS,CRE_USER,CRE_DATE," +
						" OPT_USER,OPT_DATE,OPT_TERM,OPBOOK_SEQ)"+ 
						" VALUES ('"+ transferCode+ "','"+ transferFilePath+"','"+transferFileName+ "'," +
						" '"+ mrNo+ "','"+ caseNo+ "','"+ patName+ "','"+ fromDept+ "','"+ toDept+ "'," +
						" '"+ statusFlg+ "','"+ transferClass+ "'," +
						" '"+optUser+ "',SYSDATE,'"+optUser+ "',SYSDATE,'"+optTerm+ "','"+parm.getValue("OPBOOK_SEQ")+"')";
				//            System.out.println("sql=========="+sql); 	
				result = new TParm(TJDODBTool.getInstance().update(sql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return false;
				}
				String unlockRoomSql = "UPDATE OPE_IPROOM SET OPBOOK_SEQ= NULL WHERE OPBOOK_SEQ='"+parm.getValue("OPBOOK_SEQ")+"'";
				result = new TParm(TJDODBTool.getInstance().update(unlockRoomSql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return false;
				}
				transfer_no = transferCode;
			}
		}else if (this.getOnlyEditType().equals("ONLYONE")) {
			String sql= " UPDATE INW_TRANSFERSHEET SET STATUS_FLG ='"+ statusFlg + "'," +
					" FROM_USER = '"+fromUser+ "'," +
					" TO_USER = '"+toUser+ "'," +
					" TRANSFER_DATE =SYSDATE" +
					" WHERE TRANSFER_CODE = '"+transferCode+ "'";
			result = new TParm(TJDODBTool.getInstance().update(sql,conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return false;
			}
		}
		conn.commit();
		conn.close();
		return true;
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
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// 9Ϊץȡ��;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
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
							//�̶��ı���ֵ
							if(block.getObjectType() == EComponent.FIXED_TYPE){
								EComponent com = block;
								EFixed efix = (EFixed) com;
								if("DAY_OPE_FLG".equals(efix.getName()) && efix.getName().equals(macroName)){
									efix.setText(value);
									isSetValue = true;
									break;
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
	 * ��ѯ��ǰ������Ϣ
	 * 
	 * @param transferCode ���ӵ���
	 * @return TParm
	 * @author wangb 2015/12/18
	 */
	private TParm selectInwTransInfo(String transferCode) {
		String sql = "SELECT * FROM INW_TRANSFERSHEET WHERE TRANSFER_CODE = '"
				+ transferCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���½��ӵ���Ϣ����ע��
	 * 
	 * @param transferCode ���ӵ���
	 * @return TParm
	 * @author wangb 2015/12/18
	 */
	private TParm updateInwTransMsgSendFlg(String transferCode) {
		String sql = "UPDATE INW_TRANSFERSHEET SET OPE_MSG_SEND_FLG = 'Y' WHERE TRANSFER_CODE = '"
				+ transferCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �򽻽ӵ��д��벡�����µ��������������Ϣ
	 * 
	 * @param erdCaseNo �����
	 */
	private void setCISVitalsignData(String erdCaseNo) {
		TParm result = ERDCISVitalSignTool.getInstance().queryERDCISVitalSign(erdCaseNo);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + ":" + result.getErrText());
			return;
		}

		int count = result.getCount();
		// modify by wangb ���ﵽ����/CCU���ӵ�����Ѫ�����Ͷ�
		String[] itemArrays = {"BT2","HR","NBPD","NBPM","NBPS","PULSE","RR","SPO2"};
		int itemArrayLength = itemArrays.length;
		Map<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < itemArrayLength; i++) {
			for (int j = 0; j < count; j++) {
				if (itemArrays[i].equals(result.getValue("MONITOR_ITEM_EN", j))) {
					map.put(itemArrays[i], result.getValue("MONITOR_VALUE", j));
					break;
				}
			}
		}

		// modify by wangb �����������ȡ�������ݵ�����½��ӵ������ÿգ�����ȡסԺ���µ�������Ϊ��������
		if (StringUtils.isNotEmpty(map.get("BT2"))) {
			this.getWord().setMicroField("����", map.get("BT2") + " ��");
		} else {
			this.getWord().setMicroField("����", "");
		}
		if (StringUtils.isNotEmpty(map.get("HR"))) {
			this.getWord().setMicroField("����", map.get("HR") + " ��/��");
		} else {
			this.getWord().setMicroField("����", "");
		}
		if (StringUtils.isNotEmpty(map.get("RR"))) {
			this.getWord().setMicroField("����", map.get("RR") + " ��/��");
		} else {
			this.getWord().setMicroField("����", "");
		}
		if (StringUtils.isNotEmpty(map.get("NBPS")) && StringUtils.isNotEmpty(map.get("NBPD"))) {
			this.getWord().setMicroField("Ѫѹ", map.get("NBPS") + "/" + map.get("NBPD") + " mmHg");
		} else {
			this.getWord().setMicroField("Ѫѹ", "");
		}
		if (StringUtils.isNotEmpty(map.get("SPO2"))) {
			this.getWord().setMicroField("Ѫ�����Ͷ�", map.get("SPO2") + " %");
		} else {
			this.getWord().setMicroField("Ѫ�����Ͷ�", "");
		}
	}

	/**
	 * �򽻽ӵ��д��벡���ż������
	 * 
	 * @param erdCaseNo �����
	 */
	private void setOpdDiagData(String erdCaseNo) {
		String sql = "SELECT CASE_NO,A.ICD_TYPE,A.ICD_CODE,B.ICD_CHN_DESC FROM OPD_DIAGREC A, SYS_DIAGNOSIS B "
				+ " WHERE A.CASE_NO = '"
				+ erdCaseNo
				+ "' AND A.ICD_CODE = B.ICD_CODE AND A.MAIN_DIAG_FLG = 'Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + " " + result.getErrText());
			return;
		} else {
			this.getWord().setMicroField("�ż������",
					result.getValue("ICD_CHN_DESC", 0));
		}
	}
}
