package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.util.StringTool;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;
import jdo.adm.ADMDrResvOutTool;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRPublicTool;
import jdo.ibs.IBSOrderdTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

/**
 * <p>סԺҽ��վ��ʹ���ļ�¼</p>
 * 
 * <p>����ֻ�ܴ����в���</p>
 * 
 * <p>TIMIֻ���½������������棬ֻ��������Ϊ���ֹ���</p>
 * 
 * @author wangqing
 *
 */
// 20170823
// 1������ֻ�ܴ����в���
// 2��TIMIֻ���½������������棬ֻ��������Ϊ���ֹ���

public class REGXTcoreControl extends TControl {
	/**
	 * סԺword
	 */
	private TWord wordOdi;
	/**
	 * ����word
	 */
	private TWord wordIrdr;
	/**
	 * TIMIword
	 */
	private TWord wordTimi;

	/**
	 * ϵͳ�������
	 */
	private TParm allParm;

	/**
	 * סԺsaveFiles
	 */
	private String[] saveFilesOdi;
	/**
	 * ����saveFiles
	 */
	private String[] saveFilesIrdr;
	/**
	 * TIMIsaveFiles
	 */
	private String[] saveFilesTimi;


	/**
	 * סԺ�����
	 */
	private String caseNo;
	/**
	 * ��������
	 */
	private String opdCaseNo;
	/**
	 * ������
	 */
	private String mrNo;	
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
	 * סԺҽ��վ��ʹ���ļ�¼����ģ��classCodeConfig
	 */
	private final String odiClassCodeConfig = "AMI_ODI_CLASSCODE";
	/**
	 * סԺҽ��վ��ʹ���ļ�¼����ģ��subclassCodeConfig
	 */
	private final String odiSubclassCodeConfig = "AMI_ODI_SUBCLASSCODE";
	/**
	 * ������ҽ����ʹ���ļ�¼����ģ��classCodeConfig
	 */
	private final String irdrClassCodeConfig = "AMI_IRDR_CLASSCODE";
	/**
	 * ������ҽ����ʹ���ļ�¼����ģ��subclassCodeConfig
	 */
	private final String irdrSubclassCodeConfig = "AMI_IRDR_SUBCLASSCODE";
	/**
	 * Timi���ּ�¼����classCodeConfig
	 */
	private static String timiClassCodeConfig = "AMI_TIMI_CLASSCODE";
	/**
	 * Timi���ּ�¼����subClassCodeConfig
	 */
	private static String timiSubclassCodeConfig = "AMI_TIMI_SUBCLASSCODE";
	/**
	 * סԺҽ��վ��ʹ���ļ�¼�����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateOdi = false;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		wordOdi = (TWord) this.getComponent("WORD_ODI");
		wordOdi.setName("WORD_ODI");
		wordIrdr = (TWord) this.getComponent("WORD_OPE");
		wordIrdr.setName("WORD_OPE");
		wordTimi = (TWord) this.getComponent("WORD_TIMI");
		wordTimi.setName("WORD_TIMI");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox("ϵͳ���� is null");
			return;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
			caseNo = allParm.getValue("CASE_NO");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = allParm.getValue("MR_NO");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"��":"Ů");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openOdiJhw();
		openOpeJhw();
		openTimiJhw();
	}

	/**
	 * ��סԺҽ��վ����
	 */
	public void openOdiJhw(){
		//		saveFilesOdi = EMRAMITool.getInstance().getSaveFile(opdCaseNo,classCodeConfigOdi,subclassCodeConfigOdi);
		saveFilesOdi = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig);
		if(saveFilesOdi != null && saveFilesOdi[0] != null && saveFilesOdi[0].trim().length()>0 
				&& saveFilesOdi[1] != null && saveFilesOdi[1].trim().length()>0 
				&& saveFilesOdi[2] != null && saveFilesOdi[2].trim().length()>0){// �����в���
			System.out.println("������סԺҽ��վ����");
			updateOdi = true;
			wordOdi.onOpen(saveFilesOdi[0], saveFilesOdi[1], 3, false);	
			wordOdi.setCanEdit(true);
			wordOdi.update();		
		}else{// �½�����
			System.out.println("�½�סԺҽ��վ����");
			updateOdi = false;
			//			saveFilesOdi = EMRAMITool.getInstance().getJHWTemplet(subclassCodeConfigOdi);
			saveFilesOdi = EMRPublicTool.getInstance().getEmrTemplet(odiSubclassCodeConfig);
			if(saveFilesOdi == null 
					|| saveFilesOdi[0] == null || saveFilesOdi[0].trim().length()<=0 
					|| saveFilesOdi[1] == null || saveFilesOdi[1].trim().length()<=0 
					|| saveFilesOdi[2] == null || saveFilesOdi[2].trim().length()<=0){
				this.messageBox("û���ҵ�סԺҽ��վ��ʹ���ļ�¼ģ��");
				return;
			}
			wordOdi.onOpen(saveFilesOdi[0], saveFilesOdi[1], 2, false);	
			// ��ѯסԺ����
			String daysSql = " SELECT A.IN_DATE, A.DS_DATE, B.BIRTH_DATE FROM ADM_INP A, SYS_PATINFO B"
					+ " WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+caseNo+"' AND A.DS_DATE IS NULL ";
			TParm daysResult = new TParm(TJDODBTool.getInstance().select(daysSql));
			// ����סԺ����
			Timestamp tp = daysResult.getTimestamp("DS_DATE", 0);
			Timestamp sysDate = SystemTool.getInstance().getDate();
			if (tp == null) {
				int days = 0;
				if (daysResult.getTimestamp("IN_DATE", 0) == null) {
					daysResult.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
							"00:00:00"), StringTool.setTime(daysResult.getTimestamp(
									"IN_DATE", 0), "00:00:00"));
					setECaptureValue(wordOdi, "HOSPITAL_DAY", ""+days);// סԺ����
				}
			} else {
				int days = 0;
				if (daysResult.getTimestamp("IN_DATE", 0) == null) {
					daysResult.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(daysResult
							.getTimestamp("DS_DATE", 0), "00:00:00"),
							StringTool.setTime(daysResult.getTimestamp("IN_DATE", 0),
									"00:00:00"));
				}
			}
			//��ѯ���������Ϣ
			TParm diagParm = new TParm();
			diagParm.setData("IO_TYPE","O");//��Ժ���
			diagParm.setData("CASE_NO", caseNo);
			diagParm.setData("MAINDIAG_FLG","Y");//�����
			TParm diagInfo = ADMDrResvOutTool.getInstance().selectDiag(diagParm);
			if(diagInfo.getCount()>0){
				this.setECaptureValue(wordOdi, "OUT_DIG_TIME", timestampToString(diagInfo.getTimestamp("OPT_DATE", 0), "yyyy/MM/dd HH:mm"));// ��Ժ���ʱ��
			}
			// ��ѯסԺ�ܷ���
			TParm rexpParm = new TParm();
			rexpParm.setData("CASE_NO", caseNo);
			TParm rexpData = new TParm();
			rexpData = IBSOrderdTool.getInstance().selectdataAll(rexpParm);
			// System.out.println("rexpData�վ���Ŀ" + rexpData);
			double sunTotAmt = 0.00;
			DecimalFormat    df   = new DecimalFormat("######0.00");   
			for (int i = 0; i < rexpData.getCount(); i++) {
				double totAmt = 0.00;
				totAmt = rexpData.getDouble("AR_AMT", i);
				sunTotAmt = sunTotAmt + totAmt;	
			}
			this.setECaptureValue(wordOdi, "TOTAL_FEE", df.format(sunTotAmt));// סԺ�ܷ���
			wordOdi.setMicroField("����", patName);
			wordOdi.setMicroField("�Ա�", patSex);
			wordOdi.setMicroField("����", patAge);
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordOdi.setWordParameter(allParm);
			wordOdi.setCanEdit(true);
			wordOdi.update();
		}		
	}

	/**
	 * �򿪽���ҽ��վ������ֻ�����в�����û����������
	 */
	public void openOpeJhw(){
		//		saveFilesIrdr = EMRAMITool.getInstance().getSaveFile(opdCaseNo,classCodeConfigOpe,subclassCodeConfigOpe);
		saveFilesIrdr = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig);
		if(saveFilesIrdr != null && saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// �����в���
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 3, false);	
			wordIrdr.setCanEdit(true);
			wordIrdr.update();
		}
	}

	/**
	 * ��TIMI������ֻ������������
	 */
	public void openTimiJhw(){
//		saveFilesTimi = EMRAMITool.getInstance().getJHWTemplet(timiSubclassCodeConfig);
		saveFilesTimi = EMRPublicTool.getInstance().getEmrTemplet(timiSubclassCodeConfig);
		if(saveFilesTimi == null 
				|| saveFilesTimi[0] == null || saveFilesTimi[0].trim().length()<=0 
				|| saveFilesTimi[1] == null || saveFilesTimi[1].trim().length()<=0 
				|| saveFilesTimi[2] == null || saveFilesTimi[2].trim().length()<=0){
			System.out.println("û�н���Timi����ģ��");
			return;
		}
		wordTimi.onOpen(saveFilesTimi[0], saveFilesTimi[1], 2, false);
		wordTimi.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");
		wordTimi.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");// add by wangqing 20170814
		wordTimi.setMicroField("����", patName);
		wordTimi.setMicroField("�Ա�", patSex);
		wordTimi.setMicroField("����", patAge);
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		wordTimi.setWordParameter(allParm);
		wordTimi.setCanEdit(true);
		wordTimi.update();
	}

	/**
	 * ���淽��
	 */
	public void onSave (){	 	
		String path = "";
		String fileName = "";
		// סԺ����		
		if(updateOdi){// ����
			path = saveFilesOdi[0];
			fileName = saveFilesOdi[1];
		}else{// ����
			//			TParm erdParm = EMRAMITool.getInstance().saveJHWFile(opdCaseNo, mrNo, classCodeConfigOdi, saveFilesOdi[2],saveFilesOdi[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig, saveFilesOdi[1]);
			if (erdParm.getErrCode() < 0) {				 
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}		
		wordOdi.setMessageBoxSwitch(false);
		wordOdi.onSaveAs(path, fileName, 3);
		wordOdi.setCanEdit(true);
		wordOdi.update();
		// ���뱣��	
		if(saveFilesIrdr != null 
				&& saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// �����в���
			path = saveFilesIrdr[0];
			fileName = saveFilesIrdr[1]; 	
			wordIrdr.setMessageBoxSwitch(false);
			wordIrdr.onSaveAs(path, fileName, 3);
			wordIrdr.setCanEdit(true);
			wordIrdr.update();
		}
		this.messageBox("����ɹ�������");
		this.closeWindow(); 
	}

	/**
	 * Timestamp->String
	 * @param time
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);//yyyy/MM/dd HH:mm
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
	 * ���ø�ѡ��ֵ
	 * @param name
	 * @param word
	 * @param value
	 */
	public void setCheckBoxChooseValue(TWord word, String name, String value){
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
		ECheckBoxChoose cbc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		if(cbc == null) {
			System.out.println("word--->name�ؼ�������");
			return;
		}
		cbc.setCbValue(value);
	}

	/**
	 * ��ȡ�̶��ı�ֵ
	 * @param word
	 * @param name
	 * @return
	 */
	public String getEFixedValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// �̶��ı�
		if(f == null){ 
			System.out.println("word--->name�ؼ�������");
			return null;	
		}
		return f.getText();
	}
	
	/**
	 * <p>���ù̶��ı�ֵ</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setEFixedValue(TWord word, String name, String value){
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// �̶��ı�
		if(f == null){ 
			System.out.println("word--->name�ؼ�������");
			return;	
		}
		f.setText(value);
	}
	
	/**
	 * ��ѡ�򵥻��¼�
	 * @param wordName
	 * @param checkBoxChooseName
	 */
	public void checkBoxChooseClicked(String wordName, String checkBoxChooseName){
		System.out.println("wordName:"+wordName);
		System.out.println("checkBoxChooseName:"+checkBoxChooseName);
		if(wordName != null && wordName.equals("WORD_TIMI")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("timiAgeCheckBox")){
				int valueAge = Integer.parseInt(patAge.replace("��", ""));
				System.out.println("===valueAge:"+valueAge);
				ECheckBoxChoose timiAgeCheckBox = getECheckBoxChoose(wordTimi, "timiAgeCheckBox");
				boolean timiAgeCheckBoxStatus = timiAgeCheckBox.isChecked();
				if(timiAgeCheckBoxStatus){
					if (valueAge >= 60 && valueAge <= 64) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "1");
					} else if (valueAge >= 65 && valueAge <= 74) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "2");
					} else if (valueAge >= 75) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "3");
					} 
				}else{
					setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "0");
				}
			}
		}	
	}
	
	/**
	 * ������ʽ
	 * @param wordName
	 */
	public void calculateExpression(String wordName){
		if(wordName != null && wordName.equals("WORD_TIMI")){
			String timiTotal = getEFixedValue(wordTimi, "timiTotal");
			String timlLevelStr = getTimlLevel(timiTotal);
			setEFixedValue(wordTimi, "timlLevel", timlLevelStr);
			wordTimi.update();	
//			setCaptureValueArray("timiScore", timiTotal, wordEDDR);
//			wordEDDR.update();		
		}
	}
	
	/**
	 * ����TIMI�ּ�
	 * @author wangqing 20170628
	 * 
	 * */
	public String getTimlLevel(String timiTotalStr) {
		// add by wangqing 20170628
		if(timiTotalStr == null || timiTotalStr.trim().length()==0){
			return "";
		}
		int timlLevelInt = Integer.parseInt(timiTotalStr);
		String timlLevelStr = "";

		if(timlLevelInt >= 0  &&  timlLevelInt <= 4) {
			timlLevelStr="��Σ";
		} else if (timlLevelInt >= 5  &&  timlLevelInt <= 9) {
			timlLevelStr="��Σ";
		} else if (timlLevelInt >= 10  &&  timlLevelInt <= 14){
			timlLevelStr="��Σ";
		}	
		return timlLevelStr;
	}
	
//	/**
//	 * ɾ�����в���
//	 */
//	public void onDelete(){
//		if(updateOdi){
//			if(EMRPublicTool.getInstance().deleteEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig)){
//				this.messageBox("ɾ���ɹ�������");
//			}else{
//				this.messageBox("ɾ��ʧ�ܣ�����");
//			}
//			this.closeWindow();
//		}else{
//			this.messageBox("û�в���������");
//		}
//	}
	
	

}
