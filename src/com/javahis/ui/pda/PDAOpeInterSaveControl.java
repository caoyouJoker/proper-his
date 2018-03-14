package com.javahis.ui.pda;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.adm.ADMXMLTool;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;

public class PDAOpeInterSaveControl extends TControl{
	private TParm Parameter;// ��Ų���
	private String opbookSeq;
	TParm result = new TParm();
	private String toUser;//�˲黤ʿ
	Timestamp sysDate = SystemTool.getInstance().getDate();
	private TWord word;
	private String[] saveFiles;
//	private String caseNo;
	private boolean update = false;
	
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	public void onInit(){
		super.onInit();
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) { // �ж��Ƿ���TParm
				Parameter = (TParm) obj;
			} else {
				this.closeWindow();
			}
		} else {
			this.closeWindow();
		}
//		this.messageBox("��ʼ���ɹ���");
		word = (TWord) this.getComponent("TWORD");
		openJhw();
	}
	/**
	 * �򿪲���
	 */
	private void openJhw() {
		opbookSeq = Parameter.getValue("OPBOOK_SEQ");
//		System.out.println("opbookSeq:"+opbookSeq);
		String sqlInsert = "SELECT A.ROOM_NO, A.OP_DATE, A.CASE_NO, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ "B.CHN_DESC AS OP_ROOM,C.BIRTH_DATE, O.HEIGHT,O.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,A.GDVAS_CODE,Q.GDVAS_DESC,O.IN_DATE," 
				+ " A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,A.OP_CODE1,O.ALLERGY,C.SEX_CODE,P.ALLERGIC_MARK,P.ALLERGIC_FLG,P.TRANSFER_CODE "
				+ "FROM OPE_OPBOOK A,SYS_DICTIONARY B,SYS_PATINFO C,SYS_DICTIONARY F,"
				+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"
				+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N,ADM_INP O,INW_TRANSFERSHEET_WO P,SYS_INPUTWAY Q "
				+ "WHERE B.GROUP_ID = 'OPE_OPROOM'AND A.ROOM_NO = B.ID(+) "
				+ "AND A.MR_NO = C.MR_NO (+) "
				+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "  
				+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   " 
				+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ "AND A.MAIN_SURGEON = I.USER_ID(+) "    
				+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "  
				+ "AND A.ANA_USER1 = M.USER_ID(+) "           
				+ "AND A.EXTRA_USER1 = N.USER_ID(+)" 
				+ "AND A.CASE_NO = O.CASE_NO(+) "
				+ "AND A.OPBOOK_SEQ = P.OPBOOK_SEQ(+) "
				+ "AND A.GDVAS_CODE = Q.GDVAS_CODE(+) "
				+ "AND A.OPBOOK_SEQ = '"+opbookSeq
				+"'  ORDER BY OPBOOK_SEQ";   
//		System.out.println("sqlInsert:"+sqlInsert);
		
		result = new TParm(TJDODBTool.getInstance().select(sqlInsert)); 
//		messageBox_(opebookSeq);
		result.setData("EMR_SAVE_MSG_FLG", "Y");
		saveFiles = getPreedFile(result.getValue("CASE_NO",0), "EMR0604", "EMR0604022", opbookSeq);
//		saveFiles = getPreedFile(result.getValue("CASE_NO",0), "EMR0604", "EMR0604011", opbookSeq);
		if(saveFiles == null || saveFiles[0].trim().equals("")){
//			this.messageBox("xinjian");
			// �½�
			update = false;
			saveFiles = getErdLevelTemplet("EMR0604022");
//			saveFiles = getErdLevelTemplet("EMR0604011");
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);
			TParm parm = new TParm();
			Timestamp temp;
			String age = "0";
			
			temp = result.getTimestamp("BIRTH_DATE", 0) == null ? sysDate
					: result.getTimestamp("BIRTH_DATE", 0);
			if (result.getTimestamp("IN_DATE", 0) != null){
				age = OdiUtil.showAge(temp,
						result.getTimestamp("IN_DATE", 0));
			}else{
				age = "";
			}
			parm.addData("AGE", age);
			parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", result.getValue("MR_NO",0));
			parm.setData("FILE_HEAD_TITLE_IPD_NO","TEXT", result.getValue("PAT_NAME",0));
			word.setWordParameter(parm);
			word.setMicroField("����", result.getValue("PAT_NAME",0));
			word.setMicroField("�Ա�", result.getValue("SEX",0));
			word.setMicroField("����", parm.getValue("AGE",0));//BIRTH_DATE
			word.setMicroField("������", result.getValue("MR_NO",0));
			word.setMicroField("���", result.getValue("HEIGHT",0)+" cm");
			word.setMicroField("����", result.getValue("WEIGHT",0)+" kg");
			
//			getMicroFieldValue("����", word);
			//����
			word.setMicroField("ROOM_NO", result.getValue("OP_ROOM",0));
			//����¼��;��
			if(!"".equals(result.getValue("GDVAS_CODE",0))){
				if("14".equals(result.getValue("GDVAS_CODE"))){
					String sql14 = "SELECT GDVAS_REMARKS FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '"+opbookSeq+"'";
					TParm tparm = new TParm(TJDODBTool.getInstance().select(sql14));
					setFixedValue("TYPE_CODE", word, "����("+tparm.getValue("GDVAS_REMARKS",0)+")");
				} else {
					setFixedValue("TYPE_CODE", word, result.getValue("GDVAS_DESC",0));
				}
			} else {
				setFixedValue("TYPE_CODE", word, "");
			}
			//�������
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
									+ " WHERE A.DRUG_TYPE <> 'N' AND A.MR_NO='" + result.getValue("MR_NO",0)
									+ "' ORDER BY A.ADM_DATE,A.OPT_DATE "));
			if (drugParm.getCount() > 0) {
				setCheckBoxChooseChecked("ALLERGIC_FLG1", word, true);
				setCheckBoxChooseChecked("ALLERGIC_FLG2", word, false);
				int rowCount = drugParm.getCount();
				for (int i = 0; i < rowCount; i++) {
					TParm tp = drugParm.getRow(i);
					drugStr.append(tp.getValue("ALLERGY_NAME") + ",");
				}
				String allergy = drugStr.toString();
				allergy = allergy.substring(0, allergy.length() - 1);
				setFixedValue("ALLERGIC_MARK", word, allergy);
			} else {
				setCheckBoxChooseChecked("ALLERGIC_FLG1", word, false);
				setCheckBoxChooseChecked("ALLERGIC_FLG2", word, true);
			}
			
			//׼����ȫ
			if(!"Y".equals(result.getValue("READY_FLG",0))){
				setCheckBoxChooseChecked("READY_FLG", word, false);
			} else {
				setCheckBoxChooseChecked("READY_FLG", word, true);
			}
			//���Ч��
			if(!"Y".equals(result.getValue("VALID_DATE_FLG",0))){
				setCheckBoxChooseChecked("VALID_DATE_FLG", word, false);
			} else {
				setCheckBoxChooseChecked("VALID_DATE_FLG", word, true);
			}
			//ȷ��ֲ�������ͺ�
			if(!"Y".equals(result.getValue("SPECIFICATION_FLG",0))){
				setCheckBoxChooseChecked("SPECIFICATION_FLG", word, false);
			} else {
				setCheckBoxChooseChecked("SPECIFICATION_FLG", word, true);
			}
			//����ҽ��
			setCaptureValueArray(word, "ANA_USER1", result.getValue("ANA_USER1",0));
			word.setCanEdit(true);
			word.update();
		}else{
//			this.messageBox("�����в���");
			update = true;
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
		}
	}
	/**
	 * ����
	 */
	public boolean onSave(){
		boolean falg = true;
		String path ="";
		String fileName = "";
		TParm parm = new TParm();
		String checkNo = SystemTool.getInstance().getNo("ALL", "OPE",
				"CHECK_NO", "CHECK_NO"); 
		String roomNo = result.getValue("ROOM_NO",0);
		String mrNo = result.getValue("MR_NO",0);
		String name = result.getValue("PAT_NAME",0);
		String sex = result.getValue("SEX_CODE",0);
		String birth = result.getValue("BIRTH_DATE",0).replace('-', '/').substring(0, 10);
		String typeCode = getFixedValue("TYPE_CODE", word);
		String operationIcd = result.getValue("OP_CODE1",0);
		String optChnDesc = result.getValue("OPT_CHN_DESC",0);
		String allergicFlg = getECheckBoxChooseValue("ALLERGIC_FLG1", word);
		String allergicFlg2 = getECheckBoxChooseValue("ALLERGIC_FLG2", word);
		String allergicMark = getFixedValue("ALLERGIC_MARK", word);
		String readyFlg = getECheckBoxChooseValue("READY_FLG", word);
		String validDateFlg = getECheckBoxChooseValue("VALID_DATE_FLG", word);
		String specificationFlg = getECheckBoxChooseValue("SPECIFICATION_FLG", word);
		if(StringUtils.isEmpty(getCaptureValue("ANA_USER1", word))){
			this.messageBox("����ҽ������Ϊ�գ�");
			return false;
		}
		String anaUser = getUserID(getCaptureValue("ANA_USER1", word));
		if(StringUtils.isEmpty(anaUser)){
			this.messageBox("δ��ѯ������ҽ���Ĺ��ţ�");
			return false;
		}
		if(StringUtils.isEmpty(getCaptureValue("CHECK_DR_CODE", word))){
			this.messageBox("�˶�ҽ������Ϊ�գ�");
			return false;
		}
		String checkDrCode = getUserID(getCaptureValue("CHECK_DR_CODE", word));
		if(StringUtils.isEmpty(checkDrCode)){
			this.messageBox("δ��ѯ���˶�ҽ���Ĺ��ţ�");
			return false;
		}
		String checkNsCode = getUserID(getFixedValue("CHECK_NS_CODE", word));
		String time = StringTool.getString(sysDate, "yyyyMMddHHmmss");
		String dstr = sysDate.toString().substring(0, 19);
		
		parm.setData("OPBOOK_SEQ", opbookSeq);
		parm.setData("CHECK_NO", checkNo);
		parm.setData("ROOM_NO", roomNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("CASE_NO", result.getValue("CASE_NO",0));
		parm.setData("PAT_NAME", name);
		parm.setData("SEX_CODE", sex);
		parm.setData("BIRTH_DATE", birth);
		parm.setData("TYPE_CODE", typeCode);
		parm.setData("OP_CODE1", operationIcd);
		parm.setData("OPT_CHN_DESC", optChnDesc);
		parm.setData("ALLERGIC_FLG", allergicFlg);
		parm.setData("ALLERGIC_MARK", allergicMark);
		parm.setData("READY_FLG", readyFlg);
		parm.setData("VALID_DATE_FLG", validDateFlg);
		parm.setData("SPECIFICATION_FLG", specificationFlg);
		parm.setData("ANA_USER1", anaUser);
		parm.setData("CHECK_DR_CODE", checkDrCode);
		parm.setData("CHECK_NS_CODE", checkNsCode);
		parm.setData("CHECK_DATE", time);
		parm.setData("DSTR", dstr);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("TRANSFER_CODE", result.getValue("TRANSFER_CODE",0));
//		System.out.println("parm:"+parm);
		// ִ�б������
//		this.messageBox_(parm.getValue("CHECK_NS_CODE"));
		if(("Y".equals(allergicFlg) && "Y".equals(allergicFlg2)) || ("N".equals(allergicFlg) && "N".equals(allergicFlg2))){
			this.messageBox("�����������ȷ��ѡ��");
		} else {
			if(!isBlank(parm.getValue("CHECK_NS_CODE"))){
				parm.setData("OPE_SAVE_CHECK", this.Parameter.getValue("OPE_SAVE_CHECK"));
				TParm resultSave = TIOM_AppServer.executeAction(
						"action.pda.PDAaction", "onSaveInterCheck",
						parm);
//				System.out.println("resultSave:"+resultSave);
				if (resultSave.getErrCode() < 0) {
					err(resultSave.getErrCode() + " " + resultSave.getErrText());
					this.messageBox("E0001");
				} else {
					//�ж��Ƿ񱣴�
					result.setData("EMR_SAVE_MSG_FLG", "N");
					opeListener();
					//���没���ļ�
					if(update){//���в���
						path = saveFiles[0];
						fileName = saveFiles[1];
					} else {//�½�
						path = resultSave.getValue("PATH");
						fileName = resultSave.getValue("FILENAME");
					}
//				System.out.println("path:"+path+";;;;;"+fileName);
					if(!isBlank(path) && !isBlank(fileName)){
						word.setMessageBoxSwitch(false);
						word.onSaveAs(path, fileName, 3);
					}
					this.messageBox("����ɹ�");
				}
			} else {
				this.messageBox("�˶Ի�ʿ��ǩ�֣�");
			}
		}
		return falg;
	}
	/**
	 * �˳�ʱ������ʾ
	 */
	public boolean onClosing() {
		if ("Y".equals(result.getValue("EMR_SAVE_MSG_FLG"))) {
			switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
			case 0:
				if (!onSave()) {
					return false;
				}
				break;
			case 1:
				break;
			case 2:
				return false;
			}
			// add by wangb 2017/1/9  ���뻤��ƽ̨���밲ȫ�˲鵥�ر�ʱ�Զ�������հ�ť
//			((TParm) obj).runListener("CLEAR_LISTENER", new TParm());
		} else {
			if (this.messageBox("ѯ��", "�Ƿ�رգ�", 2) != 0) {
				return false;
			}
		}
		
		super.onClosing();
//		this.setReturnValue(returnParm);
		// �˳��Զ����涨ʱ��
		// this.cancel();
		return true;
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if(StringUtils.isEmpty(getCaptureValue("ANA_USER1", word))){
			this.messageBox("����ҽ������Ϊ�գ�");
			return;
		}
		String anaUser = getUserID(getCaptureValue("ANA_USER1", word));
		if(StringUtils.isEmpty(anaUser)){
			this.messageBox("δ��ѯ������ҽ���Ĺ��ţ�");
			return;
		}
		if(StringUtils.isEmpty(getCaptureValue("CHECK_DR_CODE", word))){
			this.messageBox("�˶�ҽ������Ϊ�գ�");
			return;
		}
		String checkDrCode = getUserID(getCaptureValue("CHECK_DR_CODE", word));
		if(StringUtils.isEmpty(checkDrCode)){
			this.messageBox("δ��ѯ���˶�ҽ���Ĺ��ţ�");
			return;
		}
		if(isBlank(getFixedValue("CHECK_NS_CODE", word))){
			this.messageBox("�˶Ի�ʿ��ǩ�֣�");
		} else {
			if (this.getWord().getFileOpenName() != null) {
				this.getWord().onPreviewWord();
				this.getWord().print();
//			this.closeWindow();
			} else {
				// ��ѡ����
				this.messageBox("E0099");
			}
		}
	}
	
	/** У���Ƿ�Ϊ�� */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
	/**
     * �õ�֮ǰ����Ĳ���
     * @param caseNo String
     * @return String[]
     */
	 public String[] getPreedFile(String caseNo, String classCode, String subclassCode, String opBookSeq){
//	        String classCode = TConfig.getSystemValue("AMI_PRE_CLASSCODE");
//	        String subclassCode = TConfig.getSystemValue("AMI_PRE_SUBCLASSCODE");
	        TParm emrParm = new TParm(TJDODBTool.getInstance().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"' AND OPBOOK_SEQ = '"+opBookSeq+"'"));
//	        System.out.println("======�ѿ���  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'AND OPBOOK_SEQ = '"+opBookSeq+"'");

	        String dir="";
	        String file="";
	        String subClassCode = "";
	        if(emrParm.getCount()>0){
	            dir = emrParm.getValue("FILE_PATH",0);
	            file = emrParm.getValue("FILE_NAME",0);
	            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
	            String s[] = {dir,file,subClassCode};
	            return s;
	        }else{
	        	return null;
	        }
	    }
	 /**
     * �õ�ģ��
     * @return
     */
    public String[] getErdLevelTemplet(String subClassCode){ 	
//        String subClassCode = TConfig.getSystemValue("AMI_PRE_SUBCLASSCODE");
        TParm result = new TParm();
        String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH," +
        		"SEQ,EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"+subClassCode+"'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        String s[] = null;
        if (result.getCount("CLASS_CODE") > 0) {
            s = new String[] {
                result.getValue("TEMPLET_PATH", 0),
                result.getValue("SUBCLASS_DESC", 0),
                result.getValue("SUBCLASS_CODE", 0)};
        }
        return s;     
    }
    /**
	 * ��������״̬�������������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	public void opeListener() {
		if (StringUtils.isNotEmpty(opbookSeq)) {
			// ��������״̬(5_������(����))
			OPEOpBookTool.getInstance().updateOpeStatus(opbookSeq, "5",
					"'1','3','4'");
			// ������ӿڷ�����Ϣ
			TParm xmlParm = ADMXMLTool.getInstance().creatOPEStateXMLFile(
					result.getValue("CASE_NO"), opbookSeq);
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
		}
	}
	/**
	 * �˶Ի�ʿǩ��
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
    /**
     * �˶Ի�ʿ
     * @param inParm
     */
    public void onReturntouser(TParm inParm) {
		//		System.out.println("onReturntouser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		toUser =inParm.getValue("USER_ID");
		//	      System.out.println("toUser====="+toUser);
		EComponent com = this.getWord().getPageManager().findObject(
				"CHECK_NS_CODE", EComponent.FIXED_TYPE);
		EFixed d =(EFixed) com;
		//			System.out.println("EFixed�Ӱ���====="+d);
		if (d != null) {
			d.setText(this.getUserName(toUser));
			this.getWord().update();
		}
		EComponent com1 = this.getWord().getPageManager().findObject(
				"saveDate", EComponent.FIXED_TYPE);
		EFixed d1 =(EFixed) com1;		
		if (d1 != null) {
			String dateStr = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyy/MM/dd HH:mm:ss");
			d1.setText(dateStr);
			this.getWord().update();
		}
	}
    /**
	 * �õ��˶Ի�ʿ����
	 */
	public String getUserName(String userId) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
						+ userId + "'"));
		return parm.getValue("USER_NAME", 0);
	}
    /**
	 * �õ��˶�ҽ����ʿID
	 */
	public String getUserID(String userName) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT USER_ID FROM SYS_OPERATOR WHERE USER_NAME ='"
						+ userName + "'"));
		return parm.getValue("USER_ID", 0);
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
	 * ��ȡ��ؼ�ֵ
	 * @param name
	 * @param word
	 * @return
	 */
	public String getMicroFieldValue(String name, TWord word){
		EMicroField mf = (EMicroField) word.findObject(name, EComponent.MICRO_FIELD_TYPE);
		String value = mf.getText();
		return value;
	}
	
    /**
	 * ����ץȡ��
	 * 
	 * @param name String
	 * @param value String
	 */
	public void setCaptureValueArray(TWord word, String name, String value) {
		ECapture ecap = word.findCapture(name);
		if (ecap == null) return;
		ecap.setFocusLast();
		ecap.clear();
		this.word.pasteString(value);
	}
	/**
	 * ��ȡץȡ�ؼ�ֵ
	 * @param name
	 * @param word
	 * @return
	 */
	public String getCaptureValue(String name, TWord word){
		//		ECapture sysTime = (ECapture) word.findObject(capture, EComponent.CAPTURE_TYPE);
		ECapture ecap = word.findCapture(name);
		String value = ecap.getValue();
		return value;
	}
	/**
	 * ��ȡ��ѡ��
	 * @param cbcName
	 * @param word
	 * @return
	 */
	public String getECheckBoxChooseValue(String cbcName, TWord word){
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(cbcName, EComponent.CHECK_BOX_CHOOSE_TYPE);
		String cbcValue = cbc.isChecked() ? "Y":"N";
		return cbcValue;
	}
	/**
	 * ����ѡ���ؼ�ֵ
	 * @param name
	 * @param word
	 * @param value
	 */
	public void setCheckBoxChooseChecked(String name, TWord word, boolean value){
		ECheckBoxChoose sc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		sc.setChecked(value);
	}
	/**
	 * <p>��ȡ�̶��ı�ֵ</p>
	 * @param name
	 * @param word
	 * @return
	 */
	public String getFixedValue(String name, TWord word){
		EFixed e = word.findFixed(name);
		return e.getText();
	}
	
	/**
	 * <p>���ù̶��ı�ֵ</p>
	 * @param name
	 * @param word
	 * @return
	 */
	public void setFixedValue(String name, TWord word, String value){
		EFixed e = word.findFixed(name);
		e.clearString();
		e.addString(value);
		this.word.update();
	}
}
