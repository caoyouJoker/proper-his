package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.emr.EMRPublicTool;
import jdo.ope.OPEINTSaveTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>�����һ�ʿ-��ʹ���ļ�¼</p>
 * 
 * @author wangqing
 *
 */
public class OPEINTNSWindowControl extends TControl{

	private TWord wordIrns;
	/**
	 * ϵͳ����
	 */
	private TParm sysParm;
	/**
	 * ���Ѿ�����Ĳ����Ľṹ����������Ҫ�Ĵ洢·��saveFilesword
	 */
	private String[] saveFilesIrns;
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
	 * �����һ�ʿ��ʹ���ļ�¼�����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateIrns = false;
	/**
	 * �����һ�ʿ��ʹ���ļ�¼����ģ��classCodeConfig
	 */
	private final String irnsClassCodeConfig = "AMI_IRNS_CLASSCODE";
	/**
	 * �����һ�ʿ��ʹ���ļ�¼����ģ��subclassCodeConfig
	 */
	private final String irnsSubclassCodeConfig = "AMI_IRNS_SUBCLASSCODE";
		
	public void onInit(){
		super.onInit();
		wordIrns = (TWord) this.getComponent("tWord_0");
		wordIrns.setName("tWord_0");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("ϵͳ���� is null");
			return;
		}
		if(obj instanceof TParm){
			sysParm = (TParm)obj;
			System.out.println("===sysParm:"+sysParm);
			caseNo = sysParm.getValue("caseNo");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = sysParm.getValue("mrNo");
//			opBookSeq = sysParm.getValue("OPBOOK_SEQ");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"��":"Ů");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openIrnsJhw();
	}
	
	/**
	 * �򿪽����һ�ʿ����
	 */
	public void openIrnsJhw(){
		saveFilesIrns = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irnsClassCodeConfig, irnsSubclassCodeConfig);
		if(saveFilesIrns != null && saveFilesIrns[0] != null && saveFilesIrns[0].trim().length()>0 
				&& saveFilesIrns[1] != null && saveFilesIrns[1].trim().length()>0 
				&& saveFilesIrns[2] != null && saveFilesIrns[2].trim().length()>0){// �����в���
			System.out.println("�����н��뻤ʿ����");
			updateIrns = true;
			wordIrns.onOpen(saveFilesIrns[0], saveFilesIrns[1], 3, false);
			wordIrns.setCanEdit(true);
			wordIrns.update();	
		}else{// �½�
			System.out.println("�½����뻤ʿ����");
			updateIrns = false;
			saveFilesIrns = EMRPublicTool.getInstance().getEmrTemplet(irnsSubclassCodeConfig);
			if(saveFilesIrns == null 
					|| saveFilesIrns[0] == null || saveFilesIrns[0].trim().length()<=0 
					|| saveFilesIrns[1] == null || saveFilesIrns[1].trim().length()<=0 
					|| saveFilesIrns[2] == null || saveFilesIrns[2].trim().length()<=0){
				this.messageBox("û���ҵ����뻤ʿ��ʹ���ļ�¼ģ��");
				return;
			}
			wordIrns.onOpen(saveFilesIrns[0], saveFilesIrns[1], 2, false);		
			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
			this.setECaptureValue(wordIrns, "CCR_START_TIME", sysDate3);// ����������ʱ��
			this.setECaptureValue(wordIrns, "CCR_READY_TIME", sysDate3);// �����Ҽ���(���׼��)ʱ��
			this.setECaptureValue(wordIrns, "PAT_ARRIVE_TIME", sysDate3);// ���ߵ���(�����ҽ���)ʱ��
			this.setECaptureValue(wordIrns, "PUNCTURE_START_TIME", sysDate3); // ��ʼ����ʱ��
			this.setECaptureValue(wordIrns, "PUNCTURE_END_TIME", sysDate3); // ���̳ɹ�ʱ��
			this.setECaptureValue(wordIrns, "GRAPHY_START_TIME", sysDate3);// ��Ӱ��ʼʱ��
			this.setECaptureValue(wordIrns, "GRAPHY_END_TIME", sysDate3);// ��Ӱ����ʱ��
			this.setECaptureValue(wordIrns, "SUR_START_TIME", sysDate3);// ������ʼʱ��
			this.setECaptureValue(wordIrns, "PBMV_TIME", sysDate3);// ��������ʱ��			
			this.setECaptureValue(wordIrns, "SUR_END_TIME", sysDate3);// ��������ʱ��
			this.setECaptureValue(wordIrns, "STENT_GRAFT_START_TIME", sysDate3);// ��ʼ��������ʱ��
			this.setECaptureValue(wordIrns, "STENT_GRAFT_END_TIME", sysDate3);// ֧���ͷ�ʱ��
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordIrns.setWordParameter(allParm);
			wordIrns.setMicroField("����", patName);
			wordIrns.setMicroField("�Ա�", patSex);
			wordIrns.setMicroField("����", patAge);
			wordIrns.setCanEdit(true);
			wordIrns.update();			
		}			
	}
	
	/**
	 * ����
	 */
	public void onSave(){		
		String path = "";
		String fileName = "";
		if(updateIrns){// ����
			System.out.println("======���²���======");
			path = saveFilesIrns[0];
			fileName = saveFilesIrns[1];
		}else{// ����
			System.out.println("======��������======");
//			TParm erdParm = OPEINTSaveTool.getInstance().saveELFile(opdcaseNo, saveFilesIrns[2], saveFilesIrns[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, irnsClassCodeConfig, irnsSubclassCodeConfig, saveFilesIrns[1]);		
			if (erdParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		wordIrns.setMessageBoxSwitch(false);
		wordIrns.onSaveAs(path, fileName, 3); 
		wordIrns.update();
		this.messageBox("����ɹ�������");
		this.closeWindow();
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

}
