package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.emr.EMRPublicTool;
import jdo.ope.OPESURSaveTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;

/**
 * �����һ�ʿ��ʹ���ļ�¼
 * 
 * @author wangqing
 *
 */
public class OPESURNSWindowControl extends TControl{
	private TWord wordSrns;
	/**
	 * ϵͳ����
	 */
	private TParm sysParm;
	/**
	 * ���Ѿ�����Ĳ����Ľṹ����������Ҫ�Ĵ洢·��saveFilesword
	 */
	private String[] saveFilesSrns;
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
	private boolean updateSrns = false;//true ��ʾֻ�޸ļ���  false��ʾ�������ݱ���
	/**
	 * �����һ�ʿ��ʹ���ļ�¼����ģ��classCodeConfig
	 */
	private final String srnsClassCodeConfig = "AMI_SRNS_CLASSCODE";
	/**
	 * �����һ�ʿ��ʹ���ļ�¼����ģ��subclassCodeConfig
	 */
	private final String srnsSubclassCodeConfig = "AMI_SRNS_SUBCLASSCODE";

	public void onInit(){
		super.onInit();
		wordSrns = (TWord) this.getComponent("tWord_0");
		wordSrns.setName("tWord_0");
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
		openSrnsJhw();
	}
	
	/**
	 * ��������ʿ����
	 */
	public void openSrnsJhw(){
		saveFilesSrns = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, srnsClassCodeConfig, srnsSubclassCodeConfig);
		if(saveFilesSrns != null && saveFilesSrns[0] != null && saveFilesSrns[0].trim().length()>0 
				&& saveFilesSrns[1] != null && saveFilesSrns[1].trim().length()>0 
				&& saveFilesSrns[2] != null && saveFilesSrns[2].trim().length()>0){// �����в���
			System.out.println("������������ʿ����");
			updateSrns = true;
			wordSrns.onOpen(saveFilesSrns[0], saveFilesSrns[1], 3, false);
			wordSrns.setCanEdit(true);
			wordSrns.update();	
		}else{// �½�
			System.out.println("�½�������ʿ����");
			updateSrns = false;
			saveFilesSrns = EMRPublicTool.getInstance().getEmrTemplet(srnsSubclassCodeConfig);
			if(saveFilesSrns == null 
					|| saveFilesSrns[0] == null || saveFilesSrns[0].trim().length()<=0 
					|| saveFilesSrns[1] == null || saveFilesSrns[1].trim().length()<=0 
					|| saveFilesSrns[2] == null || saveFilesSrns[2].trim().length()<=0){
				this.messageBox("û���ҵ����뻤ʿ��ʹ���ļ�¼ģ��");
				return;
			}
			wordSrns.onOpen(saveFilesSrns[0], saveFilesSrns[1], 2, false);		

			/*modified by Eric 20170517 add system time*/
			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
			TParm cabgbook1 = OPESURSaveTool.getInstance().getCABGOPBOOK1(caseNo);//CABG
			TParm cabgbook2 = OPESURSaveTool.getInstance().getCABGOPBOOK2(caseNo);
			TParm cabgdetail = OPESURSaveTool.getInstance().getCABGDETAIL(caseNo);
			//			System.out.println("cabgdetail---->"+cabgdetail);
			TParm et = OPESURSaveTool.getInstance().getET(caseNo);//����̽������֪��ͬ����
			TParm etbook1 = OPESURSaveTool.getInstance().getETOPBOOK1(caseNo);//����
			TParm etbook2 = OPESURSaveTool.getInstance().getETOPBOOK2(caseNo);
			if (cabgbook1!=null && cabgbook1.getCount()>0){
				this.setECaptureValue(wordSrns, "CABG_DECIDE_TIME", StringTool.getString(cabgbook1.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));				
			}else if(cabgbook2!=null && cabgbook2.getCount()>0){
				this.setECaptureValue(wordSrns, "CABG_DECIDE_TIME", StringTool.getString(cabgbook2.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm:"));
			}else{
				this.setECaptureValue(wordSrns, "CABG_DECIDE_TIME", sysDate3);
			}	
			if(cabgdetail!=null && cabgdetail.getCount()>0){
				this.setECaptureValue(wordSrns, "CABG_START_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_START_DATE", 0),"yyyy/MM/dd HH:mm"));
				this.setECaptureValue(wordSrns, "CABG_END_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_END_DATE", 0),"yyyy/MM/dd HH:mm"));
				this.setECaptureValue(wordSrns, "THOR_START_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_START_DATE", 0),"yyyy/MM/dd HH:mm"));
				this.setECaptureValue(wordSrns, "THOR_END_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_END_DATE", 0),"yyyy/MM/dd HH:mm"));
			}else{
				this.setECaptureValue(wordSrns, "CABG_START_TIME", sysDate3);
				this.setECaptureValue(wordSrns, "CABG_END_TIME", sysDate3);
				this.setECaptureValue(wordSrns, "THOR_START_TIME",  sysDate3);
				this.setECaptureValue(wordSrns, "THOR_END_TIME",  sysDate3);
			}			
			if (et!=null && et.getCount()>0){
				this.setECaptureValue(wordSrns, "THOR_INFO_CONT_START", StringTool.getString(et.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));		
			}else{			
				this.setECaptureValue(wordSrns, "THOR_INFO_CONT_START", sysDate3);
			}
			if (etbook1!=null && etbook1.getCount()>0){
				this.setECaptureValue(wordSrns, "THOR_DECIDE_TIME", StringTool.getString(etbook1.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));;				
			}else if(etbook2!=null && etbook2.getCount()>0){
				this.setECaptureValue(wordSrns, "THOR_DECIDE_TIME", StringTool.getString(etbook2.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));
			}else{
				this.setECaptureValue(wordSrns, "THOR_DECIDE_TIME", sysDate3);
			}
			this.setECaptureValue(wordSrns, "THOR_INFO_CONT_SIGN", sysDate3);

			//			TParm etdetail = OPESURSaveTool.getInstance().getETDETAIL(parm.getData("caseNo").toString());
			//			this.setCaptureValueArray("THOR_START_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_START_DATE", 0),"yyyy/MM/dd HH:mm:ss"));
			//			this.setCaptureValueArray("THOR_END_TIME", StringTool.getString(cabgdetail.getTimestamp("OP_END_DATE", 0),"yyyy/MM/dd HH:mm:ss"));
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordSrns.setWordParameter(allParm);
			wordSrns.setMicroField("����", patName);
			wordSrns.setMicroField("�Ա�", patSex);
			wordSrns.setMicroField("����", patAge);
			wordSrns.setCanEdit(true);
			wordSrns.update();	
		
		}			
	}

	/**
	 * ����
	 */
	public void onSave(){
		String path = "";
		String fileName = "";	
		if(updateSrns){// ����
			path = saveFilesSrns[0];
			fileName = saveFilesSrns[1];
		}else{// ����
			System.out.println("======��������======");
//			TParm erdParm = OPEINTSaveTool.getInstance().saveELFile(opdcaseNo, saveFilesIrns[2], saveFilesIrns[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, srnsClassCodeConfig, srnsSubclassCodeConfig, saveFilesSrns[1]);		
			if (erdParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		wordSrns.setMessageBoxSwitch(false);
		wordSrns.onSaveAs(path, fileName, 3); 
		wordSrns.update();
		this.messageBox("����ɹ���");
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
