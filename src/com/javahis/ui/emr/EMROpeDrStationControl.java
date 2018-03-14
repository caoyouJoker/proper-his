package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRPublicTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

/**
 * <p>������ҽ��-��ʹ���ļ�¼</p>
 * 
 * <p>TIMIֻ���½������������棬ֻ��������Ϊ���ֹ���</p>
 * 
 * @author WangQing 20170224
 *
 */

// 20170823
// 1��TIMIֻ���½������������棬ֻ��������Ϊ���ֹ���

public class EMROpeDrStationControl extends TControl {
	/**
	 * wordOpe
	 */
	private TWord wordIrdr;
	/**
	 * TIMIword
	 */
	private TWord wordTimi;
	/**
	 * ����ҽ��saveFiles
	 */
	private String[] saveFilesIrdr;
	/**
	 * TIMIsaveFiles
	 */
	private String[] saveFilesTimi;

	/**
	 * ϵͳ�������
	 */
	private TParm allParm;

	/**
	 * סԺ����ţ�ϵͳ���룩
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
	 * ��������
	 */
	private String opBookSeq = "";

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
	 * ������ҽ����ʹ���ļ�¼�����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateIrdr = false;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		wordIrdr = (TWord) this.getComponent("WORD_OPE");
		wordIrdr.setName("WORD_OPE");
		wordTimi = (TWord) this.getComponent("WORD_TIMI");
		wordTimi.setName("WORD_TIMI");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("ϵͳ���� is null");
			return;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
			caseNo = allParm.getValue("CASE_NO");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = allParm.getValue("MR_NO");
			opBookSeq = allParm.getValue("OPBOOK_SEQ");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"��":"Ů");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openIrdrJhw();
		openTimiJhw();
	}

	/**
	 * �򿪽���ҽ��վ����
	 */
	public void openIrdrJhw(){
		saveFilesIrdr = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig);
		if(saveFilesIrdr != null && saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// �����в���
			System.out.println("�����н���ҽ��վ����");
			updateIrdr = true;
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 3, false);
			wordIrdr.setCanEdit(true);
			wordIrdr.update();	
		}else{// �½�
			System.out.println("�½�����ҽ��վ����");
			updateIrdr = false;
			saveFilesIrdr = EMRPublicTool.getInstance().getEmrTemplet(irdrSubclassCodeConfig);
			if(saveFilesIrdr == null 
					|| saveFilesIrdr[0] == null || saveFilesIrdr[0].trim().length()<=0 
					|| saveFilesIrdr[1] == null || saveFilesIrdr[1].trim().length()<=0 
					|| saveFilesIrdr[2] == null || saveFilesIrdr[2].trim().length()<=0){
				this.messageBox("û���ҵ�����ҽ��վ��ʹ���ļ�¼ģ��");
				return;
			}
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 2, false);		
			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
			
			this.setECaptureValue(wordIrdr, "DECIDE_TIME", sysDate3);
			this.setECaptureValue(wordIrdr, "INFO_CONSENT_START_TIME", sysDate3);
			this.setECaptureValue(wordIrdr, "INFO_CONSENT_SIGN_TIME", sysDate3);
			
//			TParm opbook = OPEINTSaveTool.getInstance().getOPBOOK(opdCaseNo);//����
//			TParm inform = OPEINTSaveTool.getInstance().getInformed(opdCaseNo);//����̽������֪��ͬ����
//			if (inform!=null && inform.getCount()>0){
//				// ��ʼ֪��ͬ��ʱ��
//				this.setCaptureValueArray("INFO_CONSENT_START_TIME", StringTool.getString(inform.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));		
//			}else{
//				this.setCaptureValueArray("INFO_CONSENT_START_TIME", sysDate3);
//			}
//			
//			if (opbook!=null && opbook.getCount()>0){
//				// ��������ʱ��
//				this.setCaptureValueArray("DECIDE_TIME", StringTool.getString(opbook.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));
//				this.setCaptureValueArray("DECIDE_DR", opbook.getValue("USER_NAME",0));
//				this.setCaptureValueArray("INT_DR", opbook.getValue("USER_NAME",0));			
//			}else{
//				this.setCaptureValueArray("DECIDE_TIME", sysDate3);
//			}	
			
			// add by wangqing 20170707 start
			// ����ҽ��������ҽ���Զ�������������ʱ�ľ���ҽ���ͽ���ҽ��
			String sql = " SELECT A.BOOK_DR_CODE, A.MAIN_SURGEON, B.USER_NAME AS BOOK_DR_NAME, C.USER_NAME AS MAIN_SURGEON_NAME FROM OPE_OPBOOK A, SYS_OPERATOR B, SYS_OPERATOR C "
					+ "WHERE A.BOOK_DR_CODE=B.USER_ID(+) AND A.MAIN_SURGEON=C.USER_ID(+) AND A.OPBOOK_SEQ ='"+opBookSeq+"' ";
			
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("û�н�������������");
			}
			String bookDrCode = result.getValue("BOOK_DR_NAME", 0);// ����ҽ��
			String mainSurgeon = result.getValue("MAIN_SURGEON_NAME", 0);// ����ҽ��
			this.setECaptureValue(wordIrdr, "DECIDE_DR", bookDrCode);
			this.setECaptureValue(wordIrdr, "INT_DR", mainSurgeon);
			// add by wangqing 20170707 end
			wordIrdr.setMicroField("����", patName);
			wordIrdr.setMicroField("�Ա�", patSex);
			wordIrdr.setMicroField("����", patAge);
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordIrdr.setWordParameter(allParm);
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
	 * ����
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		// ���뱣��
		if (updateIrdr) {// ����
			path = saveFilesIrdr[0];
			fileName = saveFilesIrdr[1];	
		} else {// ����
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig, saveFilesIrdr[1]);
			if (erdParm.getErrCode() < 0) {				 
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");		
		} 
		wordIrdr.setMessageBoxSwitch(false);
		wordIrdr.onSaveAs(path, fileName, 3);
		wordIrdr.setCanEdit(true);
		wordIrdr.update();
		this.messageBox("����ɹ�������");
		this.closeWindow(); 
	}

	/**
	 * ��ȡ�Ա���������
	 * @param sexCode
	 * @return
	 */
	public String getSexChnDesc(String sexCode){
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = '" + sexCode +"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String sexChnDesc = result.getValue("CHN_DESC", 0);
		return sexChnDesc;
	}

	/**
	 * ��ȡ����
	 * @param birthDate
	 * @param sysDate
	 * @return
	 */
	public String getAge(Timestamp birthDate, Timestamp sysDate){
		return OdoUtil.showAge(birthDate, sysDate);
	}

	/**
	 * Date->String
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		//		Date date = new Date();
		String dateStr = "";
		//format�ĸ�ʽ��������
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * String->Date
	 * @param dateString
	 * @return
	 */
	public Date stringToDate(String dateStr){
		//		String dateStr = "2010-05-04 12:34:23";
		Date date = new Date();
		//ע��format�ĸ�ʽҪ������String�ĸ�ʽ��ƥ��
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
		try {
			date = sdf.parse(dateStr);
			System.out.println(date.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return date;		
	}

	/**
	 * Timestamp->Date
	 * @param time
	 * @return
	 */
	public Date timestampToDate(Timestamp time){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Date date = new Date();
		try {
			date = ts;
			System.out.println(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * timestampToString
	 * @param ts
	 * @param format
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
	 * ��ȡץȡ�ؼ�ֵ
	 * @param word
	 * @param name
	 * @return
	 */
	public String getCaptureValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECapture ecap = (ECapture) word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name�ؼ�������");
			return null;
		}
		return ecap.getValue();
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
	 * ���ø�ѡ��״̬
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setCheckBoxChooseStatus(TWord word, String name, boolean value){
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		ECheckBoxChoose cbc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		if(cbc == null) {
			System.out.println("word--->name�ؼ�������");
			return;	
		}
		cbc.setChecked(value);
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

}