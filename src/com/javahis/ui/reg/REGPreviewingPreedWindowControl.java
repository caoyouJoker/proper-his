package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.emr.EMRPublicTool;
import jdo.reg.RegPreedTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

/**
 * <p>��ʹ���ļ��ﻤʿ��¼</p>
 * <p>ֻ���½��Ĳ����Ż�������²�����1����ʼ������ 2�����ü���</p>
 * <p>�Ѵ��ڵĲ���ֻ���ֶ��޸ģ�������Ϊ�յĳ��⣩</p>
 * @author wangqing 
 *
 */

public class REGPreviewingPreedWindowControl extends TControl{

	/**
	 * ��ʹ���ﻤʿ��¼����
	 */
	private TWord word;
	/**
	 * �ṹ������·��
	 */
	private String[] saveFiles;	
	/**
	 * ϵͳ�������
	 */
	private TParm parm;	
	/**
	 * ������˺�
	 */
	private String triageNo = "";
	/**
	 * ��������
	 */
	private String caseNo = "";
	/**
	 * ������
	 */
	private String mrNo = "";
	/**
	 * ���֤��
	 */
	private String idNo = "";
	/**
	 * ��������
	 */
	private String patName = "";
	/**
	 * �����Ա�
	 */
	private String patSex = "";
	/**
	 * ��������
	 */
	private String patAge = "";
	/**
	 * ��������
	 */
	private String patBirthday = "";
	/**
	 * ��ϵ�绰����ϵ�˵绰-�������ֻ�-�����˼�ͥ�绰-�����˹�˾�绰��
	 */
	private String cellPhone = "";
	/**
	 * ���뻹�Ǹ���
	 */
	private boolean update = false;
	
	private final String classCodeConfig = "AMI_PRE_CLASSCODE";
	private final String subClassCodeConfig = "AMI_PRE_SUBCLASSCODE";

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			parm = (TParm) this.getParameter();
			triageNo = parm.getValue("triageNo");
			caseNo = parm.getValue("caseNo");
			mrNo = parm.getValue("mrNo");
			if(mrNo != null && !mrNo.trim().equals("")){
				Pat pat = Pat.onQueryByMrNo(mrNo);
				this.idNo = pat.getIdNo();
				patName = pat.getName();
				patSex = ("1".equals(pat.getSexCode())?"��":"Ů");
				patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());
				patBirthday = StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");			
				if(pat.getContactsTel() != null && pat.getContactsTel().trim().length()>0){
					cellPhone = pat.getContactsTel();
				}else if (pat.getCellPhone() != null && pat.getCellPhone().trim().length()>0){
					cellPhone = pat.getCellPhone();
				}else if (pat.getTelHome() != null && pat.getTelHome().trim().length()>0){
					cellPhone = pat.getTelHome();
				}else if (pat.getTelCompany() != null && pat.getTelCompany().trim().length()>0){
					cellPhone = pat.getTelCompany();
				}
			}
		}	
		initJHW();
	}

	/**
	 * ��ʼ���ṹ������
	 */
	public void initJHW(){
		word = (TWord) this.getComponent("tWord_0");
		word.setName("tWord_0");
//		saveFiles = RegPreedTool.getInstance().getPreedFile(triageNo);
		saveFiles = EMRPublicTool.getInstance().getEmrFile(triageNo, classCodeConfig, subClassCodeConfig);
		if(saveFiles == null 
				|| saveFiles[0] == null || saveFiles[0].trim().length()<=0 
				|| saveFiles[1] == null || saveFiles[1].trim().length()<=0 
				|| saveFiles[2] == null || saveFiles[2].trim().length()<=0){// �½�����
			System.out.println("=======�½�����======");
//			saveFiles = RegPreedTool.getInstance().getErdLevelTemplet();
			saveFiles = EMRPublicTool.getInstance().getEmrTemplet(subClassCodeConfig);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);

			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";

			this.setECaptureValue(word, "PAT_LOG_TIME",sysDate2);//��ʹ���ߵǼ�ʱ��
			this.setECaptureValue(word, "START_TIME", sysDate3);// ����ʱ��
			this.setECaptureValue(word, "CALL_HELP_TIME", sysDate3);// ����ʱ��
			this.setECaptureValue(word, "TNI_BLOOD_DRAWING_TIME", sysDate3);// ������飬TNIȡѪʱ��
			this.setECaptureValue(word, "REPORT_TIME", sysDate3);// ������飬����ʱ��
	
			word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");// add by wangqing ������Ժ��ʽ�ĸı�
			word.setCanEdit(true);
			word.update();
			update = false;
		}else{
			System.out.println("=======�����в���======");
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");// add by wangqing ������Ժ��ʽ�ĸı�
			word.setCanEdit(true);
			word.update();
			update=true;
		}
		
		try{
			if(mrNo != null && !mrNo.trim().equals("")){
				parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", parm.getData("mrNo").toString());	
				word.setWordParameter(parm);
				word.setMicroField("����", patName);
				word.setMicroField("�Ա�", patSex);
				word.setMicroField("����", patAge);
				word.setMicroField("��������",  patBirthday);			
				word.setMicroField("��ϵ�˵绰", cellPhone);
				this.setECaptureValue(word, "ID_NO", idNo);
//				word.getFocusManager().reset();
				word.setCanEdit(true);
				word.update();
			}
		}catch(Exception e){

		}
		
			
	}

	/**
	 * ��ѡ��ѡ���¼�
	 * @param wordName
	 * @param singleChooseName
	 */
	public void singleChooseSelected(String wordName, String singleChooseName){
		if(wordName != null && wordName.equals("tWord_0")){
			if(singleChooseName != null && singleChooseName.equals("ERD_TYPE")){
				// ��ѯ������ʱ��ͽ���ʱ��
				String triageTimeSql = "SELECT (TO_CHAR (ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR(COME_TIME, 'HH24:MI')) AS GATE_TIME, TO_CHAR (TRIAGE_TIME, 'YYYY/MM/DD HH24:MI') AS TRIAGE_TIME FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
				TParm triageTimeResult = new TParm(TJDODBTool.getInstance().select(triageTimeSql));
				String gateTime = "";
				String triageTime = "";
				if(triageTimeResult.getCount()>0){
					triageTime = triageTimeResult.getValue("TRIAGE_TIME", 0);	// ����ʱ��
					gateTime = triageTimeResult.getValue("GATE_TIME", 0);	// ������ʱ��			
				}
				String erdType = getESingleChooseText(word, "ERD_TYPE");	
				System.out.println("===erdType:"+erdType);
				if(erdType.equals("����(120������)����")){
					setCaptureValueArray2(word, "TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
					setECaptureValue(word, "DOOR_TIME", gateTime);// ���ﱾԺ����ʱ��
					setECaptureValue(word, "IN_ADMIT_TIME", triageTime);// Ժ�ڽ���ʱ��
				}else if(erdType.equals("תԺ")){
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
					setECaptureValue(word, "TRANSFER_DOOR_TIME_IN", gateTime);// ���ﱾԺ����ʱ��
					setECaptureValue(word, "TRANSFER_IN_ADMIT_TIME", triageTime);// Ժ�ڽ���ʱ��
				}else if(erdType.equals("������Ժ")){
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME", " ");
					setECaptureValue(word, "SELF_DOOR_TIME", gateTime);// ���ﱾԺ����ʱ��
					setECaptureValue(word, "SELF_ADMIT_TIME", triageTime);// Ժ�ڽ���ʱ��
				}else{
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
				}
			}
		}	
	}

	/**
	 * ����
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		if(update){
			System.out.println("---��������---");
			path = saveFiles[0];
			fileName = saveFiles[1];
		}else{
			System.out.println("---��������---");
//			TParm erdParm = RegPreedTool.getInstance().saveELFile(triageNo, saveFiles[2], saveFiles[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(triageNo, classCodeConfig, subClassCodeConfig, saveFiles[1]);
			if(erdParm.getErrCode()<0){
				this.messageBox("ERR:" + erdParm.getErrCode() + erdParm.getErrText() +
						erdParm.getErrName());
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		word.setMessageBoxSwitch(false);
		word.onSaveAs(path, fileName, 3);
		word.setCanEdit(true);
		this.messageBox("����ɹ���");
		this.closeWindow();
	}

	/**
	 * ɾ�����в���
	 */
	public void onDelete(){
		if(update){
			if(EMRPublicTool.getInstance().deleteEmrFile(triageNo, classCodeConfig, subClassCodeConfig)){
				this.messageBox("ɾ���ɹ�������");
			}else{
				this.messageBox("ɾ��ʧ�ܣ�����");
			}
			this.closeWindow();
		}else{
			this.messageBox("û�в���������");
		}
	}
		
	/**
	 * ɾ��ģ���ļ�
	 * @param templetPath String
	 * @param templetName String
	 * @return boolean
	 */
	public boolean delFileTempletFile(String templetPath, String templetName) {
		//Ŀ¼���һ����Ŀ¼FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		//ģ��·��������
		String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
		//�õ�SocketͨѶ����
		TSocket socket = TIOM_FileServer.getSocket();
		//ɾ���ļ�
		return TIOM_FileServer.deleteFile(socket,
				rootName + templetPathSer +
				templetPath +
				"\\" + templetName + ".jhw");
	}

	/**
	 * <p>����ץȡֵ</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setECaptureValue(TWord word, String name, String value) {
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		if(value.equals("")){
			value = " ";
		}
		ECapture ecap = (ECapture)word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name�ؼ�������");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(value);
	}

	/**
	 * <p>���ö��ץȡֵ</p>
	 * @param names name1;name2;name3
	 * @param value
	 */
	public void setCaptureValueArray2(TWord word, String names, String value) {
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(names == null){
			System.out.println("names is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		String[] cName = names.split(";");
		for(int i=0; i<cName.length; i++){
			setECaptureValue(word, cName[i], value);
		}	
	}

	/**
	 * ��ȡ��ѡtext
	 * @param word
	 * @param name
	 * @return
	 */
	public String getESingleChooseText(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//������ѡ
		if(sc == null){ 
			System.out.println("word--->name�ؼ�������");
			return null;		
		}
		return sc.getText();
	}

	/**
	 * ��ȡ��ѡ��ؼ�
	 * @param word
	 * @param name
	 * @return
	 */
	public ECheckBoxChoose getECheckBoxChoose(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//��ѡ��
		if(cbc == null){
			System.out.println("word--->name�ؼ�������");
		}
		return cbc;
	}
	
	/**
	 * <p>Timestamp->String</p>
	 * @param ts
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);// yyyy/MM/dd HH:mm:ss
		try {
			//����һ
			tsStr = sdf.format(ts);
			//������
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * �ڵ�ǰ��꽹��¼��ʱ��
	 */
	public void onNow(){
		String now = StringTool.getString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
		this.word.pasteString(now);
	}

}
