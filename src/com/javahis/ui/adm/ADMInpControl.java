package com.javahis.ui.adm;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;

import jdo.sid.IdCardO;
import jdo.spc.SYSPatinfoClientTool;
import jdo.spc.StringUtils;
import jdo.spc.spcPatInfoSyncClient.SpcPatInfoService_SpcPatInfoServiceImplPort_Client;
import jdo.spc.spcPatInfoSyncClient.SysPatinfo;
import jdo.sys.Pat;
import jdo.adm.ADMResvTool;
import jdo.adm.ADMXMLTool;

import com.dongyang.data.TParm;

import jdo.sys.IReportTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.sys.SYSPostTool;

import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;

import java.sql.Timestamp;

import com.dongyang.ui.TCheckBox;

import jdo.sys.SYSBedTool;
import jdo.adm.ADMInpTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.util.StringTool;
import com.javahis.device.JMStudio;
import com.dongyang.ui.TPanel;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.Image;

import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;

import java.io.File;

import com.dongyang.util.ImageTool;

import java.io.IOException;
import java.awt.Color;

import com.javahis.device.JMFRegistry;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import jdo.adm.ADMTool;
import jdo.hl7.Hl7Communications;
import jdo.med.MEDApplyTool;
import jdo.mro.MROTool;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;

import jdo.mro.MROQueueTool;

import com.dongyang.root.client.SocketLink;

import jdo.sys.PatTool;

import javax.swing.SwingUtilities;

import com.dongyang.util.TypeTool;
import com.javahis.device.NJCityInwDriver;
import com.dongyang.ui.TTextFormat;

import jdo.bil.BILPayTool;
import jdo.clp.CLPSingleDiseTool;


//import org.eclipse.wb.swt.SWTshell;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * סԺ�Ǽ�
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author JiaoY 2008
 * @version 1.0
 */
public class ADMInpControl extends TControl {
	Pat pat;
	String patType = ""; // �ж��޸Ļ���������
	String saveType = "NEW"; // �ж�����/�޸� ��NEW�� ��������UPDATE���޸�
	String caseNo = ""; // �������
	String McaseNo = ""; // ĸ�׾������
	String BED_NO = ""; // ��λ��
	String IPD_NO = "";
	String MR_NO = ""; // ������
	String haveBedNo="";//����У��ò����Ƿ��д�λ

    private String dayOpeFlg;
	 /**
     * �������
     */
    //TFrame UI=(TFrame) this.getComponent("UI");
	
	// modified by WangQing 20170411 -start
	// ������ֶΣ�����ְҵ����ϵ����Ϣ��ʾ����
	String oldOccCode = "";
	String oldRelationCode = "";
	// modified by WangQing 20170411 -end

    public String getDayOpeFlg() {							//   2017/3/25   	by  yanmm   �����ռ�������ѡ				
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {			//   2017/3/25  	by  yanmm   �����ռ�������ѡ		
		this.dayOpeFlg = dayOpeFlg;
	}
	public void onInit() {
	//	super.onInit();
		TParm parmmeter = new TParm();
        Object obj = this.getParameter();
        if(obj.toString().length()>0){
            parmmeter = (TParm)obj;
            this.setValue("MR_NO",parmmeter.getValue("MR_NO"));
            this.onMrno();
        }
		this.setMenu(false); // menu botton
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // Ԥ������
	}

	/**
	 * menu botton ��ʾ����
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setMenu(boolean flg) {
		callFunction("UI|save|setEnabled", flg); // ���水ť
		callFunction("UI|stop|setEnabled", flg); // ȡ��סԺ
		// callFunction("UI|picture|setEnabled", flg); // ����
		callFunction("UI|patinfo|setEnabled", flg); // ��������
		callFunction("UI|bed|setEnabled", flg); // ����
		callFunction("UI|bilpay|setEnabled", flg); // Ԥ����
		callFunction("UI|greenpath|setEnabled", flg); // ��ɫͨ��
//		callFunction("UI|print|setEnabled", flg); // סԺ֤��ӡ
		// callFunction("UI|child|setEnabled", flg); //������ע��
		callFunction("UI|immunity|setEnabled", flg); // ����������
	}

	/**
	 * �޸Ĳ�����Ϣ��ȡ����
	 * 
	 * @param modifyPat
	 *            Pat
	 * @return Pat
	 */
	public Pat readModifyPat(Pat modifyPat) {
		modifyPat.modifyName(getValueString("PAT_NAME")); // ����
		modifyPat.modifySexCode(getValueString("SEX_CODE")); // �Ա�
		modifyPat.modifyBirthdy(TCM_Transform
				.getTimestamp(getValue("BIRTH_DATE"))); // ��������
		modifyPat.modifyCtz1Code(getValueString("PAT_CTZ")); // ���ʽ1
		modifyPat.modifyhomePlaceCode(getValueString("HOMEPLACE_CODE")); // ������
		
			// modified by WangQing 20170411 -start
		//modifyPat.modifyOccCode(getValueString("OCC_CODE")); // ְҵ
		String sql001 = "SELECT count(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ this.getValueString("OCC_CODE") + "' ";
		TParm result001 = new TParm(TJDODBTool.getInstance().select(sql001));
		int count001 = Integer.parseInt(result001.getValue("COUNT", 0));
		if(count001 == 0){
			pat.modifyOccCode(oldOccCode);
		}else{
			pat.modifyOccCode(getValueString("OCC_CODE")); // ְҵ
		}
		// modified by WangQing 20170411 -end
		
		
		
		
		
		
		modifyPat.modifyIdNo(getValueString("IDNO")); // ���֤��
		modifyPat.modifySpeciesCode(getValueString("SPECIES_CODE")); // ����
		modifyPat.modifyNationCode(getValueString("NATION_CODE")); // ����
		modifyPat.modifyMarriageCode(getValueString("MARRIAGE_CODE")); // ����״̬
		modifyPat.modifyCompanyDesc(getValueString("COMPANY_DESC")); // ��λ
		modifyPat.modifyTelCompany(getValueString("TEL_COMPANY")); // ��˾�绰
		modifyPat.modifyPostCode(getValueString("POST_CODE")); // �ʱ�
		modifyPat.modifyResidAddress(getValueString("RESID_ADDRESS")); // ������ַ
		modifyPat.modifyResidPostCode(getValueString("RESID_POST_CODE"));
		modifyPat.modifyContactsName(getValueString("CONTACTS_NAME"));
		
		
		// modified by WangQing 20170411 -start
		//pat.modifyRelationCode(getValueString("RELATION_CODE")); // ������ϵ�˹�ϵ
		String sql002 = "SELECT COUNT(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ this.getValueString("RELATION_CODE") + "' ";
		TParm result002 = new TParm(TJDODBTool.getInstance().select(sql002));
		int count002 = Integer.parseInt(result002.getValue("COUNT", 0));
		if(count002 == 0){
			pat.modifyRelationCode(oldRelationCode);
		}else{
			pat.modifyRelationCode(getValueString("RELATION_CODE")); // ������ϵ�˹�ϵ
		}
		// modified by WangQing 20170411 -end
		
		modifyPat.modifyContactsTel(getValueString("CONTACTS_TEL"));
		modifyPat.modifyContactsAddress(getValueString("CONTACTS_ADDRESS"));
		modifyPat.modifyTelHome(getValueString("TEL_HOME")); // ��ͥ�绰
		modifyPat.modifyAddress(getValueString("ADDRESS")); // ��ͥסַ
		modifyPat.modifyForeignerFlg(TypeTool
				.getBoolean(getValue("FOREIGNER_FLG"))); // �����ע��
		// shiblmodify 20120107
		modifyPat.modifyBirthPlace(this.getValueString("BIRTHPLACE")); // ����
		modifyPat.modifyCompanyAddress(this.getValueString("ADDRESS_COMPANY")); // ��λ��ַ
		modifyPat.modifyCompanyPost(this.getValueString("POST_COMPANY")); // ��λ��ַ
		return modifyPat;
	}

	/**
	 * ����/�޸Ĳ��� botton �¼�
	 */
	public void onNewpat() {
		if ("".equals(getValue("PAT_NAME"))) {
			this.messageBox("������������Ϊ�գ�");
			return;
		}
		if ("".equals(getValue("SEX_CODE"))) {
			this.messageBox("�Ա𲻿�Ϊ�գ�");
			return;
		}
		if ("".equals(getValue("BIRTH_DATE"))) {
			this.messageBox("�������ڲ���Ϊ�գ�");
			return;
		}
		if ("".equals(getValue("PAT_CTZ"))) {
			this.messageBox("���ʽ��");
			return;
		}
		// ================= ������Ҫ����סԺ�Ǽ���д���������֤��
/*		if ("".equals(this.getValue("NATION_CODE"))) {
			this.messageBox_("���������");
			this.grabFocus("NATION_CODE");
			return;
		}*/
		if (!this.getValueBoolean("FOREIGNER_FLG")) {
			if ("".equals(this.getValue("IDNO"))) {
				this.messageBox_("���������֤��");
				this.grabFocus("IDNO");
				return;
			}
		}
		// �õ������任checkbox
		TCheckBox checkbox = (TCheckBox) this
				.callFunction("UI|NEW_PAT_INFO|getThis");
		if (pat == null || checkbox.isSelected()) {
			if (!newPatInfo()) // ��������
				return;
		} else {
			if (modifyPatInfo()){ // �޸Ĳ�����Ϣ
            			//duzhw add 20131023(סԺ�Ǽ��ұ߱��水ť����������ͬʱ��Ҫͬ����Ϣ��������)
            			this.addMRO("update"); // �޸� ���� MRO
            	
            		}else{
            			return;
           		 }
		}
		this.messageBox("P0005");
		// ===������ start
		if (Operator.getSpcFlg().equals("Y")) {
			// SYSPatinfoClientTool sysPatinfoClientTool = new
			// SYSPatinfoClientTool(
			// this.getValue("MR_NO").toString());
			// SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client
			// serviceSpcPatInfoServiceImplPortClient = new
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
			// String msg = serviceSpcPatInfoServiceImplPortClient
			// .onSaveSpcPatInfo(syspat);
			// if (!msg.equals("OK")) {
			// System.out.println(msg);
			// }
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
					"action.sys.SYSSPCPatAction", "getPatName", spcParm);
		}
		// ===������ end
		// callFunction("UI|picture|setEnabled", true); // ����
		callFunction("UI|patinfo|setEnabled", true); // ��������
		// callFunction("UI|child|setEnabled", true); //������ע��
	}

	/**
	 * ������������
	 * 
	 * @return boolean
	 */
	public boolean newPatInfo() {
		if (!checkPatInfo())
			return false;
		pat = new Pat();
		pat = this.readModifyPat(pat);
		if (!pat.onNew()) {
			this.messageBox("E0005"); // ʧ��
			return false;
		} else {
			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|MR_NO|setEnabled", false); // ������
			callFunction("UI|IPD_NO|setEnabled", false); // סԺ��
			callFunction("UI|patinfo|setEnabled", true); // ������Ϣ
			// callFunction("UI|picture|setEnabled", true); // ����
			// callFunction("UI|child|setEnabled", true); //������ע��
			this.callFunction("UI|NEW_PAT|setText", "�޸Ĳ�������");
			callFunction("UI|NEW_PAT|setEnabled", true); // ��������botton
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
			// this.clearValue("NEW_PAT_INFO");//��� ���½�������checkbox
			return true;
		}
	}

	/**
	 * �޸Ĳ�����Ϣ����
	 * 
	 * @return boolean
	 */
	public boolean modifyPatInfo() {
		if (pat.getMrNo() == null || "".equals(pat.getMrNo())) {
			return false;
		}
		pat = this.readModifyPat(pat);
		if (!pat.onSave()) {
			this.messageBox("E0005"); // ʧ��
			return false;
		} else {
			setValue("MR_NO", pat.getMrNo());
			callFunction("UI|new|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
			this.setValue("NEW_PAT_INFO", "N");
			return true;
		}
	}

	/**
	 * סԺ�Żس��¼�
	 */
	public void onIpdNo() {
		TParm parm = new TParm();
		parm.setData("IPD_NO",
				PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO")));
		TParm re = ADMInpTool.getInstance().selectall(parm);
		this.setValue("MR_NO", re.getValue("MR_NO", 0));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					onMrno();
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * �����Żس���ѯ�¼�
	 */
	public void onMrno() {
		// ============== chenxi ========== ����סԺ�����ؽ��棬���¼��������첡����Ժʱ�����
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // Ԥ������
		// ============== chenxi =========
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("���޲���! ");
			this.onClear(); // ���
			this.setUi(); // ������Ϣ�ɱ༭
			this.setUIAdmF(); // סԺ�Ǽ���Ϣ���ɱ༭
			this.setMenu(false);
			return;
		} else {
			callFunction("UI|MR_NO|setEnabled", false); // ������
			callFunction("UI|IPD_NO|setEnabled", false); // סԺ��
			callFunction("UI|patinfo|setEnabled", true); // ������Ϣ
			// callFunction("UI|picture|setEnabled", true); // ����
			// callFunction("UI|child|setEnabled", true); //������ע��
			this.callFunction("UI|NEW_PAT|setText", "�޸Ĳ�������");
			callFunction("UI|NEW_PAT|setEnabled", true); // ��������botton
			callFunction("UI|PHOTO_BOTTON|setEnabled", true);
			MR_NO = pat.getMrNo();
		}
		this.setPatForUI(pat);
		if (checkAdmInp(pat.getMrNo())) {
			this.messageBox_("�˲���סԺ�У�");
			this.inInpdata();
			this.setUIAdmF();
			
			// ��31���ٴ�סԺ���ѷ���
			this.AgnMessage();
			saveType = "UPDATE";
			callFunction("UI|save|setEnabled", true); // ����
			callFunction("UI|MR_NO|setEnabled", false); // ������
			callFunction("UI|IPD_NO|setEnabled", false); // סԺ��
			callFunction("UI|bed|setEnabled", true); // ����
			callFunction("UI|greenpath|setEnabled", true); // ��ɫͨ��
			// callFunction("UI|child|setEnabled", true); //������ע��
			this.setMenu(true);
			return;
		} else {
			// ��ѯԤԼסԺ��Ϣ
			TParm parm = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
			if (parm.getCount() <= 0) {
				this.messageBox_("�˲���û��ԤԼ��Ϣ!");
				callFunction("UI|save|setEnabled", false); // ����
				return;
			}
			// �ж�ԤԼ��Ϣ�Ƿ���������
			if (parm.getBoolean("NEW_BORN_FLG", 0)) {
				this.setValue("NEW_BORN_FLG",
						parm.getBoolean("NEW_BORN_FLG", 0));
				McaseNo = parm.getValue("M_CASE_NO", 0); // ��ѯԤԼ��Ϣ���Ƿ���ĸ�׵Ĳ�����
				// ��ѯĸ�׵�סԺ��Ϣ ��ȡסԺ��(Ӥ����IPD_NO��ĸ����ͬ)
				TParm admParm = new TParm();
				admParm.setData("CASE_NO", McaseNo);
				TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
				if (admInfo.getCount() <= 0) {
					this.messageBox_("û�в�ѯ��ĸ�׵�סԺ��Ϣ");
					callFunction("UI|save|setEnabled", false); // ����
					return;
				}
				Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
				this.setValue("IPD_NO", admInfo.getValue("IPD_NO", 0));
				this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
				this.setValue("M_NAME", M_PAT.getName());
				this.callFunction("UI|LM1|setVisible", true);
				this.callFunction("UI|LM2|setVisible", true);
				this.callFunction("UI|M_MR_NO|setVisible", true);
				this.callFunction("UI|M_NAME|setVisible", true);
			} else {
				this.setValue("IPD_NO", pat.getIpdNo());
				this.callFunction("UI|LM1|setVisible", false);
				this.callFunction("UI|LM2|setVisible", false);
				this.callFunction("UI|M_MR_NO|setVisible", false);
				this.callFunction("UI|M_NAME|setVisible", false);
			}
			TParm param = new TParm();// add by wanglong 20121025
			param.setData("MR_NO", parm.getValue("MR_NO", 0));// add by wanglong
																// 20121025
			TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(
					param);// add by wanglong 20121025
			this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong
															// 20121025
			callFunction("UI|save|setEnabled", true); // ����
			this.setValue("ADM_SOURCE", parm.getData("ADM_SOURCE", 0));
			this.setValue("TOTAL_BILPAY", parm.getData("BILPAY", 0));
			// this.setValue("SERVICE_LEVEL", parm.getData("SERVICE_LEVEL", 0));
			this.setValue("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
			this.setValue("DEPT_CODE", parm.getData("DEPT_CODE", 0));
			this.setValue("STATION_CODE", parm.getData("STATION_CODE", 0));
			this.setValue("OPD_DEPT_CODE", parm.getData("OPD_DEPT_CODE", 0));
			this.setValue("OPD_DR_CODE", parm.getData("OPD_DR_CODE", 0));
			this.setValue("VS_DR_CODE", parm.getData("DR_CODE", 0));
			this.setValue("PATIENT_CONDITION",
					parm.getData("PATIENT_CONDITION", 0));
			this.setValue("YELLOW_SIGN", parm.getData("YELLOW_SIGN", 0));
			this.setValue("RED_SIGN", parm.getData("RED_SIGN", 0));
			this.setValue("BED_NO", getBedDesc(parm.getValue("BED_NO", 0)));
			haveBedNo=parm.getValue("BED_NO", 0);
			this.setUIT();
			this.setValue("SERVICE_LEVEL", "1"); // ����ȼ�Ĭ����"�Է�"
			this.setValue("IN_COUNT", getInCount(MR_NO) + 1); // ��ȡ����סԺ�Ĵ���
			this.setValue("DAY_OPE_FLG", parm.getValue("DAY_OPE_FLG", 0)); //   2017/3/25  yanmm �ռ�����
			saveType = "NEW";
			// ��31���ٴ�סԺ���ѷ���
			this.AgnMessage();
			// callFunction("UI|child|setEnabled", true); //������ע��
		}
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		this.setMenu(false); // MENU ��ʾ����
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.setMenu(false); // MENU ��ʾ����
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("������" + allMrNo + " �Ѻϲ���" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}

	/**
	 * ������Ϣ��ֵ
	 * 
	 * @param patInfo
	 *            Pat
	 */
	public void setPatForUI(Pat patInfo) {
		// ������,����,�Ա�,����,ְҵ�����壬���������֤�ţ�����,������
		this.setValueForParm(
				"MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;OCC_CODE;SPECIES_CODE;NATION_CODE;IDNO;MARRIAGE_CODE;HOMEPLACE_CODE;TEL_HOME",
				patInfo.getParm());
		// ������λ,��λ�绰,��λ�ʱ�,���ڵ�ַ,�����ʱ࣬��ϵ����������ϵ����ϵ�˵绰����ϵ�˵�ַ
		this.setValueForParm(
				"COMPANY_DESC;TEL_COMPANY;POST_CODE;ADDRESS;ADDRESS_COMPANY;POST_COMPANY;BIRTHPLACE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;RELATION_CODE;CONTACTS_TEL;CONTACTS_ADDRESS;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE",
				patInfo.getParm());
		this.setValue("PAT_CTZ", patInfo.getCtz1Code());
		this.setText("TEL_O1", patInfo.getTelCompany());
		this.setValue("RESID_ADDRESS", patInfo.getResidAddress());
		this.setValue("FOREIGNER_FLG", patInfo.isForeignerFlg()); // �����ע��
		setBirth(); // ��������
		// onPOST_CODE();
		// onRESID_POST_CODE();
		// this.onCompanyPost();
		
		
		// modified by WangQing 20170331 -start 
		// �ϵ�ְҵ��Ϣ����ϵ�˹�ϵ��Ϣ��ʾ
		TComboBox occCodeCombo= (TComboBox) this.getComponent("OCC_CODE");
		TComboBox relationCombo= (TComboBox) this.getComponent("RELATION_CODE");
		occCodeCombo.setCanEdit(true);
		relationCombo.setCanEdit(true);
		String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ patInfo.getOccCode() + "' ";
		String sql2 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ patInfo.getRelationCode() + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		occCodeCombo.setText((String) result1.getData("CHN_DESC", 0));
		relationCombo.setText((String) result2.getData("CHN_DESC", 0));
		oldOccCode = pat.getOccCode();
		oldRelationCode = pat.getRelationCode();
		// modified by WangQing 20170331 -end
		
		this.viewPhoto(pat.getMrNo());

	}

	/**
	 * ���֤�ŵõ�������
	 */
	public void onIdNo() {
		String homeCode = "";
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);
		if(PatTool.getInstance().isExistHomePlace(homeCode)){
			setValue("HOMEPLACE_CODE", homeCode);
		}else{
			setValue("HOMEPLACE_CODE", "");
		}
	}

	// /**
	// * �����ʱ�õ�ʡ��
	// */
	// public void onPOST_CODE() {
	// if (getValueString("POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	//
	// String post = getValueString("POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	//
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("POST_P",
	// parm.getData("POST_CODE", 0).toString().substring(0, 2));
	// setValue("POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * �����ʱ�õ�ʡ��
	// */
	// public void onRESID_POST_CODE() {
	// if (getValueString("RESID_POST_CODE") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// String post = getValueString("RESID_POST_CODE");
	// TParm parm = this.getPOST_CODE(post);
	// if (parm.getData("POST_CODE", 0) == null
	// || "".equals(parm.getData("POST_CODE", 0)))
	// return;
	// setValue("RESID_POST_P", parm.getData("POST_CODE", 0).toString()
	// .substring(0, 2));
	// setValue("RESID_POST_C", parm.getData("POST_CODE", 0).toString());
	// }
	//
	// /**
	// * �õ�ʡ�д���
	// *
	// * @param post
	// * String
	// * @return TParm
	// */
	// public TParm getPOST_CODE(String post) {
	// TParm result = SYSPostTool.getInstance().getProvinceCity(post);
	// return result;
	// }
	//
	// /**
	// * ͨ�����д�����������
	// */
	// public void selectCode_1() {
	// String post = this.getValue("POST_C").toString();
	// if (post.length() == 0 || "".equals(post))
	// return;
	//
	// this.setValue("POST_CODE", this.getValue("POST_C"));
	// this.onPOST_CODE();
	// }
	//
	// /**
	// * ͨ�����д�����������
	// */
	// public void selectCode_2() {
	// if (this.getValue("RESID_POST_C") == null
	// || "".equals(getValueString("POST_CODE")))
	// return;
	// this.setValue("RESID_POST_CODE", this.getValue("RESID_POST_C"));
	// this.onRESID_POST_CODE();
	// }
	//
	// /**
	// * ͨ�����д�����������3
	// */
	// public void selectCode_3() {
	// this.setValue("POST_COMPANY", this.getValue("COMPANY_POST_C"));
	// this.onCompanyPost();
	// }
	//
	// /**
	// * ��λ�ʱ�ĵõ�ʡ��
	// */
	// public void onCompanyPost() {
	// String post = getValueString("POST_COMPANY");
	// if (post == null || "".equals(post)) {
	// return;
	// }
	// TParm parm = this.getPOST_CODE(post);
	// setValue("COMPANY_POST_P", parm.getData("POST_CODE", 0) == null ? ""
	// : parm.getValue("POST_CODE", 0).substring(0, 2));
	// setValue("COMPANY_POST_C", parm.getValue("POST_CODE", 0).toString());
	// }
	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto1() {
		setValue("RESID_POST_CODE", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("RESID_ADDRESS", getValue("ADDRESS"));

	}

	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto3() {
		setValue("POST_COMPANY", getValue("POST_CODE"));
		// this.onRESIDPOST();
		// callFunction("UI|SESSION_CODE|onQuery");
		setValue("ADDRESS_COMPANY", getValue("ADDRESS"));

	}

	/**
	 * ͬͨ�ŵ�ַ
	 */
	public void onSameto2() {
		setValue("CONTACTS_ADDRESS", getValue("ADDRESS"));
	}

	/**
	 * סԺ�ǼǱ���
	 */
	public void onSave() {
		// if(!checkPatInfo()){
		// return;
		// }
		if ("NEW".equals(saveType)) {
			this.admInpInsert(); // ����
		} else if ("UPDATE".equals(saveType)) {
			this.admInpUpdata(); // �޸�
		} else {
			this.messageBox_("û�б�������");
		}
	}

	/**
	 * 31�����ٴ�סԺ���ѷ���
	 */
	public void AgnMessage() {
		Timestamp date = SystemTool.getInstance().getDate();
		String mrNo = this.getValueString("MR_NO");
		if (mrNo == null || mrNo.length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("MR_NO", mrNo);
		inparm.setData("CANCEL_FLG", "N");
		inparm.setData("REGION_CODE", Operator.getRegion());
		TParm parm = ADMInpTool.getInstance().selectall(inparm);
		if (parm.getCount() <= 0)
			return;
		// סԺ����
		int count = parm.getCount();
		if (count > 0) {
			parm = ADMInpTool.getInstance().queryLastDsdate(inparm);
			// luhai MODIFY 2012-2-21 modify �����ڲ���֮ǰû��סԺ����
			// ���£���ѯ�����ϴ�סԺ����Ϊ�գ�����ѯ��������������ʾΪ1����� begin
			// if (parm.getCount() <= 0)
			// return;
			if (parm.getCount("DS_DATE") <= 0)
				return;
			if (parm.getTimestamp("DS_DATE", 0) == null) {
				return;
			}
			// luhai MODIFY 2012-2-21 modify �����ڲ���֮ǰû��סԺ����
			// ���£���ѯ�����ϴ�סԺ����Ϊ�գ�����ѯ��������������ʾΪ1����� end
			Timestamp lastdate = parm.getTimestamp("DS_DATE", 0);
			int time = StringTool.getDateDiffer(date, lastdate);
			if (time <= 31) {
				// ��������
				if ("NEW".equals(saveType)) {
					this.messageBox("�˲��˳�Ժ31�����ٴ�סԺ��");
				}
				callFunction("UI|AGN_CODE|setEnabled", true); // 31�����ٴ�סԺ�ȼ�
				callFunction("UI|AGN_INTENTION|setEnabled", true); // 31�����ٴ�סԺ�ȼ�
			} else {
				callFunction("UI|AGN_CODE|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
				callFunction("UI|AGN_INTENTION|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
			}
		}
	}

	/**
	 * ����
	 */
	public void admInpInsert() {
		if (checkAdmInp(this.getValueString("MR_NO"))) {
			this.messageBox_("�˲���סԺ�У�");
			this.inInpdata();
			this.setUIAdmF();
			
			saveType = "UPDATE";
			callFunction("UI|save|setEnabled", true); // ����
			callFunction("UI|MR_NO|setEnabled", false); // ������
			callFunction("UI|IPD_NO|setEnabled", false); // סԺ��
			callFunction("UI|bed|setEnabled", true); // ����
			callFunction("UI|greenpath|setEnabled", true); // ��ɫͨ��
			// callFunction("UI|child|setEnabled", true); //������ע��
			this.setMenu(true);
			return;
		}
		if (!this.checkData()) // �������
			return;
		
		/*modified by Eric 20170525 start
		������7���ٴ�סԺ����ϵͳ������ʾ*/
		if(this.getValueString("ADM_SOURCE") !=null 
				&& this.getValueString("ADM_SOURCE").equals("02")){// ������ԴΪ����			
			String dsDateSql = "SELECT MAX(DS_DATE) AS DS_DATE FROM ADM_INP WHERE MR_NO='"+this.getValueString("MR_NO")+"' ";
			TParm dsDateResult = new TParm(TJDODBTool.getInstance().select(dsDateSql));
			Timestamp dsDate = (Timestamp) dsDateResult.getData("DS_DATE", 0);
//			Timestamp now = TJDODBTool.getInstance().getDBTime();
			Timestamp inDate = (Timestamp) this.getValue("IN_DATE");
			// modify by wangb 2017/6/23 �����һ��סԺ������ò�����һ�γ�Ժʱ�䵼�µĿ�ָ�������
			if (null != inDate && null != dsDate) {
				int days = StringTool.getDateDiffer(inDate, dsDate);
				System.out.println("------dsDate=" + dsDate);
				// System.out.println("------now="+now);
				System.out.println("------inDate=" + inDate);
				System.out.println("------days=" + days);
				if (days <= 7) {
					this.messageBox("�˻��߱���סԺʱ����ϴγ�Ժʱ�䲻����7��");
				}
			}			
		}
		/*modified by Eric 20170525 end*/
		
		
		
		
		// add by wangb 2015/10/22 סԺ�Ǽ�ʱУ���Ƿ��Ѿ������סԺ֤ START
		if (!this.checkHospCard()) {
			this.messageBox("����\"סԺ֤��ӡ\"��ť��������סԺ֤");
			return;
		}
		// add by wangb 2015/10/22 סԺ�Ǽ�ʱУ���Ƿ��Ѿ������סԺ֤ END
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // Ԥ������
		TParm parm = new TParm();
		parm = this.readData(); // ��ȡ����
		TParm bed = new TParm();
		bed.setData("BED_NO", parm.getData("BED_NO"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bed);
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")) {
				this.messageBox("�˴���ռ��,�����ѡ��λ");
				return;
			}
		}
		// System.out.println("adm_1");
		BED_NO = parm.getData("BED_NO").toString();
		if ("Y".equals(getValue("NEW_BORN_FLG"))) {
			IPD_NO = getValue("IPD_NO").toString();
		} else {
			IPD_NO = pat.getIpdNo(); // �жϸò����Ƿ�ס��Ժ
			if ("".equals(IPD_NO))
				IPD_NO = SystemTool.getInstance().getIpdNo();

			if ("".equals(IPD_NO)) {
				this.messageBox_("סԺ��ȡ�δ���");
				return;
			}
		}
		// System.out.println("adm_2");
		caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
				"CASE_NO"); // ����ȡ��ԭ��
		// System.out.println("adm_3");
		// ��ȡ��������
		parm.setData("CASE_NO", caseNo);
		parm.setData("M_CASE_NO", McaseNo);
		parm.setData("IPD_NO", IPD_NO); 
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("IN_DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("STATION_CODE"));
		parm.setData("VS_DR_CODE", this.getValue("VS_DR_CODE"));
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_CLERK", Operator.getID()); // סԺ�Ǽ���ҵԱ
        parm.setData("DAY_OPE_FLG",this.getValue("DAY_OPE_FLG"));		// 2017/3/25    yanmm �ռ�����
		// ***********modify by lim 2012/02/21 begin
        
        //  �������ι���ʷ��¼  machao  start
        String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND "
        		+ " MR_NO = '"+parm.getValue("MR_NO")+"' AND"
        		+ " DRUG_TYPE is not null AND "
        		+ " DRUG_TYPE <> 'N' ";
        
//        if(!StringUtil.isNullString(parm.getValue("CASE_NO"))){
//        	sqlAllergy = sqlAllergy.replace("#", "AND CASE_NO = '"+parm.getValue("CASE_NO")+"'");
//        }else{
//        	sqlAllergy = sqlAllergy.replace("#", "");
//        }
        System.out.println("33333:"+sqlAllergy);
        TParm res = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
        
        parm.setData("ALLERGY", res.getCount("MR_NO")>0?"Y":"N");
        //�������ι���ʷ��¼  machao  end
        
		String mrNo = parm.getValue("MR_NO");
		String resvNo = "";

		
		String fileServerMainRoot = TIOM_FileServer
				.getPath("FileServer.Main.Root");
		String emrData = TIOM_FileServer.getPath("EmrData");
		TParm resv = ADMResvTool.getInstance().selectNotIn(mrNo);
		if (resv.getCount() < 0) {
			messageBox("�ò���û��ԤԼסԺ");
			return;
		}
		resvNo = resv.getValue("RESV_NO", 0);
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='" + resvNo
				+ "'  ORDER BY OPT_DATE DESC ";
		// System.out.println("======sql===###################===="+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {

		} else {
			// �ƶ�JHW�ļ���������.
			String oldFileName = result1.getValue("FILE_NAME", 0);
			String oldFilePath = result1.getValue("FILE_PATH", 0);
			String seq = result1.getValue("FILE_SEQ", 0);
			System.out.println(fileServerMainRoot + emrData + oldFilePath
					+ "\\" + oldFileName + ".jhw");

			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					fileServerMainRoot + emrData + oldFilePath + "\\"
							+ oldFileName + ".jhw");

			Timestamp ts = SystemTool.getInstance().getDate();
			String dateStr = StringTool.getString(ts, "yyyyMMdd");
			// ����µ��ļ�·��
			StringBuilder filePathSb = new StringBuilder();
			filePathSb.append("JHW\\").append(dateStr.substring(2, 4))
					.append("\\").append(dateStr.substring(4, 6)).append("\\")
					.append(mrNo);
			String newFilePath = filePathSb.toString();

			// ����µ��ļ�����
			String[] oldFileNameArray = oldFileName.split("_");

			StringBuilder sb = new StringBuilder(caseNo);
			sb.append("_").append(oldFileNameArray[1]).append("_")
					.append(oldFileNameArray[2]);
			String newFileName = sb.toString();

			try {
				// �ƶ��ļ�
				TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						fileServerMainRoot + emrData + newFilePath + "\\"
								+ newFileName + ".jhw", data);
				// this.messageBox("=====resvNo====="+resvNo);
				TParm action = new TParm(
						this.getDBTool()
								.select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
										+ caseNo + "'"));
				int index = action.getInt("MAXFILENO", 0);
				// �������ݿ�
				String sql1 = "UPDATE EMR_FILE_INDEX SET CASE_NO='" + caseNo
						+ "',FILE_PATH='" + newFilePath + "',FILE_NAME='"
						+ newFileName + "',FILE_SEQ='" + index
						+ "' WHERE CASE_NO='" + resvNo + "' AND FILE_SEQ='"
						+ seq + "'";

				// System.out.println("======sql11111========"+sql1);
				TParm result2 = new TParm(TJDODBTool.getInstance().update(sql1));
				if (result2.getErrCode() < 0) {
					err(result2.getErrName() + "" + result2.getErrText());
					messageBox("����ʧ��!");
					return;
				}
				// ɾ�����ļ���
				boolean delFlg = TIOM_FileServer.deleteFile(
						TIOM_FileServer.getSocket(), fileServerMainRoot
								+ emrData + oldFilePath + "\\" + oldFileName
								+ ".jhw");
				if (!delFlg) {
					this.messageBox("ɾ��ԭ�ļ�ʧ��!");
					return;
				}
			} catch (Exception e) {
				this.messageBox("�ƶ��ļ�ʧ��!");
			}
		}
		// ***********modify by lim 2012/02/21 end
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
				"insertADMData", parm); // סԺ�ǼǱ���
		// System.out.println("adm_4");
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			
			deletePreInfo();//ɾ��Ԥ�ǼǱ������
            // ���Խӿ� wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A01");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            // �������ӿ� wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
			saveType = "UPDATE";
			this.setValue("IPD_NO", IPD_NO);
			pat.modifyIpdNo(IPD_NO);
			pat.modifyRcntIpdDate((Timestamp) getValue("IN_DATE"));
			pat.modifyRcntIpdDept(getValue("DEPT_CODE").toString());
			pat.onSave();
			modifyPatInfo(); // ���²���������Ϣ
			if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by
																		// wanglong
																		// 20121025
				updateADMInpSDInfo();// ���뵥������Ϣ
			}
			this.addMRO("new"); // �½� ���� MRO
			this.setUIAdmF();
			this.setMenu(true);
			// HL7��Ϣ
			// this.sendHl7message();
			// ֪ͨ��ʿվsocket
			sendInwStationMessages(this.getValueString("MR_NO"), caseNo,
					pat.getName());
			// //������Ϣ����XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			if (!getConfigBoolean("OPENWINDOW.GREEN_PATH_isOpen"))
				return;
			TParm sendParm = new TParm();
			sendParm.setData("CASE_NO", caseNo);
			sendParm.setData("PRE_AMT", this.getValue("TOTAL_BILPAY"));
			String fileName = getConfigString("OPENWINDOW.BIIL_PAY");
			this.openWindow(fileName, sendParm);
			TParm queryData = new TParm();
			queryData.setData("CASE_NO", caseNo);
			TParm bilPay = ADMInpTool.getInstance().queryCaseNo(queryData);
			setValue("TOTAL_BILPAY", bilPay.getData("TOTAL_BILPAY", 0));

		}
	}
	
	/**
	 * ���´�λ��Ϣ
	 */
	public void deletePreInfo(){
		String sql="DELETE FROM ADM_PRETREAT WHERE MR_NO='"+this.getValue("MR_NO")+"'";
		System.out.println(":::"+sql);
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
	}
	/**
	 * �޸�
	 */
	public void admInpUpdata() {
        if (!this.checkData()) // �������
            return;
		if (caseNo == null || "".equals(caseNo))
			return;
		if (!checkBedNo()) {
			return;
		}
		
		/*modified by Eric 20170525 start
		������7���ٴ�סԺ����ϵͳ������ʾ*/
		if(this.getValueString("ADM_SOURCE") !=null 
				&& this.getValueString("ADM_SOURCE").equals("02")){// ������ԴΪ����			
			String dsDateSql = "SELECT MAX(DS_DATE) AS DS_DATE FROM ADM_INP WHERE MR_NO='"+this.getValueString("MR_NO")+"' ";
			TParm dsDateResult = new TParm(TJDODBTool.getInstance().select(dsDateSql));
			Timestamp dsDate = (Timestamp) dsDateResult.getData("DS_DATE", 0);
//			Timestamp now = TJDODBTool.getInstance().getDBTime();
			Timestamp inDate = (Timestamp) this.getValue("IN_DATE");
			// modify by wangb 2017/6/23 �����һ��סԺ������ò�����һ�γ�Ժʱ�䵼�µĿ�ָ�������
			if (null != inDate && null != dsDate) {
				int days = StringTool.getDateDiffer(inDate, dsDate);
				System.out.println("------dsDate=" + dsDate);
				// System.out.println("------now="+now);
				System.out.println("------inDate=" + inDate);
				System.out.println("------days=" + days);
				if (days <= 7) {
					this.messageBox("�˻��߱���סԺʱ����ϴγ�Ժʱ�䲻����7��");
				}
			}			
		}
		/*modified by Eric 20170525 end*/
		
		TParm parm = new TParm();
		parm = this.readData(); // ��ȡ����
		if (!BED_NO.equals(this.getValueString("BED_DESC"))) {
			parm.setData("UPDATE_BED", "Y");
		} else {
			parm.setData("UPDATE_BED", "N");
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("IPD_NO", getValue("IPD_NO"));
		
		 //  �������ι���ʷ��¼  machao  start
		String sqlAllergy = "SELECT * FROM opd_drugallergy WHERE 1=1 AND "
        		+ " MR_NO = '"+parm.getValue("MR_NO")+"' AND"
        		+ " DRUG_TYPE is not null AND "
        		+ " DRUG_TYPE <> 'N' ";        
//        if(!StringUtil.isNullString(parm.getValue("CASE_NO"))){
//        	sqlAllergy = sqlAllergy.replace("#", "AND CASE_NO = '"+parm.getValue("CASE_NO")+"'");
//        }else{
//        	sqlAllergy = sqlAllergy.replace("#", "");
//        }
        System.out.println("22222:"+sqlAllergy);
        TParm res = new TParm(TJDODBTool.getInstance().select(sqlAllergy));
        
        parm.setData("ALLERGY", res.getCount("MR_NO")>0?"Y":"N");
		// machao end
        
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
				"upDataAdmInp", parm); // סԺ�ǼǱ���
		if (result.getErrCode() < 0)
			this.messageBox("E0005");
		else {
			this.messageBox("P0005");
			modifyPatInfo();
			// //������Ϣ����XML
			// try{
			// ADMXMLTool.getInstance().creatXMLFile(caseNo);
			// }catch(Exception e){}
			this.addMRO("update"); // �޸� ���� MRO
			this.setUIAdmF();
		}
	}

	/**
	 * ����Ƿ�����޸Ĵ�λ true �����޸� false ������
	 * 
	 * @return boolean
	 */
	public boolean checkBedNo() {
		boolean check = false;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = SYSBedTool.getInstance().checkInBed(parm);
        if (result.getErrCode() < 0) {
            this.messageBox_(result.getErrText());
            return false;
        }
        int count = result.getCount("BED_STATUS");
        if (count == -1 || count == 0) {
            return check;
        }
        for (int i = 0; i < count; i++) {
            if (result.getData("BED_STATUS", i) == null
                || "".equals(result.getData("BED_STATUS", i))
                || "0".equals(result.getData("BED_STATUS", i))) {
                check = true;
            } else if(result.getValue("BED_STATUS", i).equals("1")
            		&&result.getValue("BED_OCCU_FLG", i).equals("N")){
            	BED_NO=result.getValue("BED_NO", i);
            	this.setValue("BED_DESC", BED_NO);
            }
        }
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_OCCU_FLG", i) == null
					|| "".equals(result.getData("BED_OCCU_FLG", i))
					|| "N".equals(result.getData("BED_OCCU_FLG", i))) {
				check = true;
			} else {
				check = false;
			}
		}
		if (!check) {
			int message = this.messageBox("��Ϣ", "�˲����Ѱ����Ƿ������", 0);
			if (message == 0) {
				check = true;
			} else {
				check = false;
				return check;
			}
		}

		TParm bedNo = new TParm();
		bedNo.setData("BED_NO", this.getValue("BED_DESC"));
		TParm checkbed = ADMInpTool.getInstance().QueryBed(bedNo);
		String mrNo = this.getValueString("MR_NO");
		if (checkbed.getData("ALLO_FLG", 0) != null) {
			if (checkbed.getData("ALLO_FLG", 0).equals("Y")
					&& !(mrNo.equals(checkbed.getData("MR_NO", 0)))) {
				this.messageBox("�˴���ռ��,�����ѡ��λ");
				return false;
			}
		}
		return check;
	}

	/**
	 * ����Ƿ����ȡ��סԺ
	 * 
	 * @return boolean
	 */
	public boolean checkCanInp() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		boolean result = ADMTool.getInstance().checkCancelOutInp(parm);
		return result;
	}

	/**
	 * У��Ԥ�������
	 * 
	 * @return boolean
	 */
	public boolean checkBilPay() {
		TParm parm = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (parm.getErrCode() < 0) {
			return false;
		}
		if (parm.getDouble("PRE_AMT", 0) > 0) {
			return false;
		}

		return true;
	}

	/**
	 * ����Ƿ�����Ѿ���������(���ü�¼)
	 * 
	 * @return boolean
	 */
	public boolean checkCanPay() {
		boolean result = true;    
		
		//modify by yangjj 20151110 ȡ��סԺ �����ܺ�С�ڵ���0
		String sql = " SELECT SUM(TOT_AMT) AS COUNT FROM IBS_ORDD WHERE CASE_NO = '"+caseNo+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(Integer.parseInt(parm.getValue("COUNT", 0)) > 0){
			result = false;
		}
		//String sql = "SELECT CASE_NO FROM IBS_ORDM WHERE CASE_NO = '"+caseNo+"' ";
		//TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		parm.setData("CASE_NO", caseNo);
		
		//if(parm.getCount()>0){
			//result = false;
		//}
		//boolean result = ADMTool.getInstance().checkCancelOutInp(parm); 
		return result;
	}
	
	/**
	 * ��ȡUI��������
	 * 
	 * @return TParm
	 */
	public TParm readData() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO")); // ������
		parm.setData("NEW_BORN_FLG", getValue("NEW_BORN_FLG")); // ������ע��
		parm.setData("ADM_SOURCE", getValue("ADM_SOURCE")); // ������Դ
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // סԺ�Ʊ�
		parm.setData("TOTAL_BILPAY", getValueDouble("TOTAL_BILPAY")); // Ԥ����
		parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION")); // ��Ժ״̬
		parm.setData("SERVICE_LEVEL", getValue("SERVICE_LEVEL")); // ����ȼ�
		parm.setData("CTZ1_CODE", getValue("CTZ1_CODE")); // ���ʽ1
		parm.setData("CTZ2_CODE", getValue("CTZ2_CODE")); // 2
		parm.setData("CTZ3_CODE", getValue("CTZ3_CODE")); // 3
		parm.setData("IN_COUNT", getText("IN_COUNT")); // ��Ժ����
		parm.setData("IN_DATE", getValue("IN_DATE")); // 3��Ժ����
		parm.setData("DEPT_CODE", getValue("DEPT_CODE")); // סԺ�Ʊ�
		parm.setData("STATION_CODE", getValue("STATION_CODE")); // סԺ����
		parm.setData("BED_NO", getValue("BED_DESC")); // ��λ��
		parm.setData("OPD_DR_CODE", this.getValue("OPD_DR_CODE")); // �ż���ҽʦ
		parm.setData("VS_DR_CODE", getValue("VS_DR_CODE")); // ����ҽʦ
		parm.setData("ATTEND_DR_CODE", getValue("ATTEND_DR_CODE")); // ����ҽʦ
		parm.setData("ADM_DATE", getValue("ADM_DATE")); // �Ǽ�����
		parm.setData("RED_SIGN", getValueDouble("RED_SIGN")); // ��ɫ����
		parm.setData("YELLOW_SIGN", getValueDouble("YELLOW_SIGN")); // ��ɫ����
		parm.setData("AGN_CODE", this.getValueString("AGN_CODE"));
		parm.setData("AGN_INTENTION", this.getValueString("AGN_INTENTION"));
		parm.setData("DAY_OPE_FLG",getValue("DAY_OPE_FLG")); //�ռ�����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		return parm;
	}

	/**
	 * �������ݼ��
	 * 
	 * @return Boolean
	 */
	public Boolean checkData() {
		if ("".equals(this.getValueString("ADM_SOURCE"))) {
			this.messageBox_("�����벡����Դ");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DEPT_CODE"))) {
			this.messageBox_("�������ż������");
			return false;
		}
		if ("".equals(this.getValueString("OPD_DR_CODE"))) {
			this.messageBox_("�������ż���ҽʦ");
			return false;
		}
		if ("".equals(this.getValueString("PATIENT_CONDITION"))) {
			this.messageBox_("��������Ժ״̬");
			return false;
		}
		if ("".equals(this.getValueString("SERVICE_LEVEL"))) {
			this.messageBox_("��ѡ�����ȼ�");
			return false;
		}
		if ("".equals(this.getValueString("CTZ1_CODE"))) {
			this.messageBox_("�����븶�ʽ");
			return false;
		}
		if ("".equals(this.getValueString("TOTAL_BILPAY"))){
			this.messageBox_("����Ԥ����");
			return false;
		}
		if ("".equals(this.getValueString("DEPT_CODE"))) {
			this.messageBox_("������סԺ�Ʊ�");
			return false;
		}
		if ("".equals(this.getValueString("STATION_CODE"))) {
			this.messageBox_("������סԺ����");
			return false;
		}
		if ("".equals(this.getValueString("VS_DR_CODE"))) {
			this.messageBox_("�����뾭��ҽʦ");
			return false;
		}
		if ("".equals(this.getValueString("IN_DATE"))) {
			this.messageBox_("��������Ժ����");
			return false;
		}
		// ====zhangp 20120828 start
		if ("".equals(this.getValueString("BIRTH_DATE"))) {
			this.messageBox_("�������������");
			return false;
		}
		// ===zhangp 20120828 end
		if ("Y".equals(getValue("NEW_BORN_FLG"))) {
			if (getValue("IPD_NO") == null || "".equals(getValue("IPD_NO"))) {
				this.messageBox_("δѡ��ĸ��");
				return false;
			}
		}
		// ============chenxi modify 20130422===== ������Ҫ����סԺ�Ǽ���д���������֤��
		// modify by wangb 2016/07/26 ӦסԺ��Ϊ��߹���Ч�ʣ������弰�����±������Ϊ�Ǳ�����ݺ�����¼
		/*if ("".equals(this.getValueString("NATION_CODE"))) {
			this.messageBox_("���������");
			this.grabFocus("NATION_CODE");
			return false;
		}*/
		if (!this.getValueBoolean("FOREIGNER_FLG")) {
			if ("".equals(this.getValueString("IDNO"))) {
				this.messageBox_("���������֤��");
				this.grabFocus("IDNO");
				return false;
			}
		}
        if ("".equals(this.getValueString("MR_NO"))) {// wanglong add 20140815
            this.messageBox_("�����벡����");
            return false;
        }
        if ("".equals(this.getValueString("PAT_NAME"))) {
            this.messageBox_("����������");
            return false;
        }
        if ("".equals(this.getValueString("SEX_CODE"))) {
            this.messageBox_("��ѡ���Ա�");
            return false;
        }
        if ("".equals(this.getValueString("BIRTH_DATE"))) {
            this.messageBox_("��ѡ���������");
            return false;
        }
        /*if ("".equals(this.getValueString("MARRIAGE_CODE"))) {
            this.messageBox_("��ѡ�����");
            return false;
        }*/
        if ("".equals(this.getValueString("AGE"))) {
            this.messageBox_("����������");
            return false;
        }
        /*if ("".equals(this.getValueString("SPECIES_CODE"))) {
            this.messageBox_("��ѡ������");
            return false;
        }*/
        if ("".equals(this.getValueString("TEL_HOME"))) {
            this.messageBox_("������绰");
            return false;
        }
        /*if ("".equals(this.getValueString("OCC_CODE"))) {
            this.messageBox_("��ѡ��ְҵ");
            return false;
        }
        if ("".equals(this.getValueString("ADDRESS"))) {
            this.messageBox_("��������סַ");
            return false;
        }
        if ("".equals(this.getValueString("RESID_ADDRESS"))) {
            this.messageBox_("�����뻧����ַ");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_NAME"))) {
            this.messageBox_("��������ϵ������");
            return false;
        }
        if ("".equals(this.getValueString("RELATION_CODE"))) {
            this.messageBox_("��ѡ���ϵ");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_TEL"))) {
            this.messageBox_("��������ϵ�˵绰");
            return false;
        }
        if ("".equals(this.getValueString("CONTACTS_ADDRESS"))) {
            this.messageBox_("��������ϵ�˵�ַ");
            return false;
        }*/
		return true;
	}

	/**
	 * ���ò�����Ϣ����
	 */
	public void onPatInfo() {
		TParm parm = new TParm();
		parm.setData("ADM", "ADM");
		parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * ����Ƿ�סԺ�� false δסԺ true סԺ��
	 * 
	 * @param MrNo
	 *            String
	 * @return boolean
	 */
	public boolean checkAdmInp(String MrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", MrNo);
		TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
		if (result.checkEmpty("IPD_NO", result))
			return false;
		caseNo = result.getData("CASE_NO", 0).toString();
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		pat = new Pat();
		caseNo = "";
		McaseNo = "";
		saveType = "NEW";
		BED_NO = "";
		TPanel photo = (TPanel) this.getComponent("VIEW_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(photo.getWidth(), photo.getHeight());
		pic.setLocation(0, 0);
		photo.removeAll();
		photo.add(pic);
		pic.repaint();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		String date = df.format(SystemTool.getInstance().getDate());
		setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // Ԥ������
		setValue("IN_COUNT", "1"); // Ԥ������
		this.setValue("NEW_BORN_FLG", "N");
		callFunction("UI|NEW_PAT|setEnabled", false); // ��������
		this.callFunction("UI|NEW_PAT|setText", "������������");
		callFunction("UI|MR_NO|setEnabled", true); // ������
		callFunction("UI|IPD_NO|setEnabled", true); // סԺ��
		clearValue("MR_NO");
		clearValue("ADM_SOURCE;PR_DEPT_CODE;OPD_DR_CODE;TOTAL_BILPAY;SERVICE_LEVEL;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;YELLOW_SIGN;RED_SIGN;DEPT_CODE;VS_DR_CODE;PATIENT_CONDITION;DEPT_CODE;BED_NO;BED_DESC;STATION_CODE;DAY_OPE_FLG");
		clearValue("RESID_ADDRESS;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;AGE;HOMEPLACE_CODE;MARRIAGE_CODE;OCC_CODE;SPECIES_CODE;tComboBox_0;NATION_CODE;IDNO;COMPANY_DESC;TEL_01;POST_CODE;RESID_ROAD;RESID_POST_CODE;CONTACTS_NAME;CONTACTS_ADDRESS;CONTACTS_TEL;NEW_PAT;RELATION_CODE");
		clearValue("PAT_CTZ;;TEL_COMPANY;TEL_HOME;M_MR_NO;M_NAME;FOREIGNER_FLG;ADDRESS");
		clearValue("ADDRESS_COMPANY;POST_COMPANY;AGN_CODE;AGN_INTENTION;BIRTHPLACE");
		
		// this.setValue("POST_C", "");
		// this.setValue("POST_P", "");
		// ((TComboBox) this.getComponent("POST_P")).onQuery();
		// ((TComboBox) this.getComponent("POST_C")).onQuery();
		// this.setValue("RESID_POST_C", "");
		// this.setValue("RESID_POST_P", "");
		// ((TComboBox) this.getComponent("RESID_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("RESID_POST_C")).onQuery();
		// this.setValue("COMPANY_POST_P", "");
		// this.setValue("COMPANY_POST_C", "");
		// ((TComboBox) this.getComponent("COMPANY_POST_P")).onQuery();
		// ((TComboBox) this.getComponent("COMPANY_POST_C")).onQuery();
		this.setValue("OPD_DEPT_CODE", "");
		this.setValue("OPD_DR_CODE", "");
		this.setValue("DEPT_CODE", "");
		this.setValue("STATION_CODE", "");
		this.setValue("VS_DR_CODE", "");
		this.setValue("DAY_OPE_FLG", "");
		setUIT();
		setMenu(false);
		this.callFunction("UI|LM1|setVisible", false);
		this.callFunction("UI|LM2|setVisible", false);
		this.callFunction("UI|M_MR_NO|setVisible", false);
		this.callFunction("UI|M_NAME|setVisible", false);
		callFunction("UI|AGN_CODE|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
		callFunction("UI|AGN_INTENTION|setEnabled", false); // 31�����ٴ�סԺ�ȼ�
		callFunction("UI|PHOTO_BOTTON|setEnabled", false);
		//callFunction("UI|DAY_OPE_FLG|setEnabled", true);
	}

	/**
	 * ����סԺ�пؼ����ɱ༭
	 */
	public void setUIAdmF() {
		callFunction("UI|ADM_SOURCE|setEnabled", false);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
		callFunction("UI|OPD_DR_CODE|setEnabled", false);
		callFunction("UI|PATIENT_CONDITION|setEnabled", false);
		callFunction("UI|SERVICE_LEVEL|setEnabled", false);
		callFunction("UI|CTZ1_CODE|setEnabled", false);
		callFunction("UI|CTZ2_CODE|setEnabled", false);
		callFunction("UI|CTZ3_CODE|setEnabled", false);
		callFunction("UI|tNumberTextField_3|setEnabled", false);
		callFunction("UI|DEPT_CODE|setEnabled", false);
		callFunction("UI|STATION_CODE|setEnabled", false);
		callFunction("UI|VS_DR_CODE|setEnabled", false);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", false);
		callFunction("UI|TOTAL_BILPAY|setEnabled", false);
		callFunction("UI|tButton_0|setEnabled", false);
	//	callFunction("UI|DAY_OPE_FLG|setEnabled", false);
	}

	/**
	 * �ؼ��ɱ༭
	 */
	public void setUIT() {
		callFunction("UI|ADM_SOURCE|setEnabled", true);
		callFunction("UI|OPD_DEPT_CODE|setEnabled", true);
		callFunction("UI|OPD_DR_CODE|setEnabled", true);
		callFunction("UI|PATIENT_CONDITION|setEnabled", true);
		callFunction("UI|SERVICE_LEVEL|setEnabled", true);
		callFunction("UI|CTZ1_CODE|setEnabled", true);
		callFunction("UI|CTZ2_CODE|setEnabled", true);
		callFunction("UI|CTZ3_CODE|setEnabled", true);
		callFunction("UI|tNumberTextField_3|setEnabled", true);
		callFunction("UI|DEPT_CODE|setEnabled", true);
		callFunction("UI|STATION_CODE|setEnabled", true);
		callFunction("UI|VS_DR_CODE|setEnabled", true);
		callFunction("UI|ATTEND_DR_CODE|setEnabled", true);
		callFunction("UI|TOTAL_BILPAY|setEnabled", true);
		callFunction("UI|tButton_0|setEnabled", true);
	}

	/**
	 * ��ѯסԺ�в���������Ϣ
	 */
	public void inInpdata() {
		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setDataN("IPD_NO", getValue("IPD_NO"));
		// ��ѯ��Ժ�����Ļ�����Ϣ
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm resvParm = new TParm();
		resvParm.setData("MR_NO", getValue("MR_NO"));
		resvParm.setData("IN_CASE_NO", result.getData("CASE_NO", 0));
		//modify by yangjj 20150806
		//TParm resv = ADMResvTool.getInstance().selectAll(resvParm);
		
		String resvSql = " SELECT " +
							" OPD_DEPT_CODE, " +
							" PATIENT_CONDITION " +
						 " FROM " +
						 	" ADM_RESV " +
						 " WHERE " +
						 	" CAN_DATE IS NULL " +
						 	" AND MR_NO = '"+getValue("MR_NO")+"' " +
						 	" AND IN_CASE_NO = '"+result.getData("CASE_NO", 0)+"'";
		TParm resv = new TParm(TJDODBTool.getInstance().select(resvSql));
		
		this.setValue("OPD_DEPT_CODE", resv.getData("OPD_DEPT_CODE", 0));
		this.setValue("PATIENT_CONDITION", resv.getData("PATIENT_CONDITION", 0));
		BED_NO = result.getValue("BED_NO", 0);
		IPD_NO = result.getValue("IPD_NO", 0);
		this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
		this.setValue("OPD_DR_CODE", result.getData("OPD_DR_CODE", 0));
		this.setValue("ADM_SOURCE", result.getData("ADM_SOURCE", 0));
		this.setValue("TOTAL_BILPAY", result.getData("TOTAL_BILPAY", 0));
		this.setValue("SERVICE_LEVEL", result.getData("SERVICE_LEVEL", 0));
		this.setValue("CTZ1_CODE", result.getData("CTZ1_CODE", 0));
		this.setValue("CTZ2_CODE", result.getData("CTZ2_CODE", 0));
		this.setValue("CTZ3_CODE", result.getData("CTZ3_CODE", 0));
		this.setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
		this.setValue("STATION_CODE", result.getData("STATION_CODE", 0));
		this.setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
		this.setValue("YELLOW_SIGN", result.getData("YELLOW_SIGN", 0));
		this.setValue("RED_SIGN", result.getData("RED_SIGN", 0));
		this.setValue("IN_DATE", result.getData("IN_DATE", 0));
		this.setValue("NEW_BORN_FLG", result.getBoolean("NEW_BORN_FLG", 0));
		this.setValue("IN_COUNT", result.getData("IN_COUNT", 0));
		this.setValue("AGN_CODE", result.getData("AGN_CODE", 0)); // 31����סԺ�ȼ�
		this.setValue("AGN_INTENTION", result.getData("AGN_INTENTION", 0)); // 31����סԺԭ��
		this.setValue("BED_NO", getBedDesc(BED_NO));
		//this.setValue("DAY_OPE_FLG",  parm.getBoolean("DAY_OPE_FLG", 0)); // �ռ�����
		
		((TCheckBox)this.getComponent("DAY_OPE_FLG")).setSelected("Y".equals(result.getValue("DAY_OPE_FLG", 0)) ? true : false);	//   �ռ�����
		
		
		TParm result1 = CLPSingleDiseTool.getInstance().queryADMResvSDInfo(parm);// add by wanglong 20121025
		this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong
														// 20121025
		// �жϸò����Ƿ���������
		if (result.getBoolean("NEW_BORN_FLG", 0)) {
			McaseNo = result.getValue("M_CASE_NO", 0); // ��ѯԤԼ��Ϣ���Ƿ���ĸ�׵Ĳ�����
			// ��ѯĸ�׵�סԺ��Ϣ ��ȡסԺ��(Ӥ����IPD_NO��ĸ����ͬ)
			TParm admParm = new TParm();
			admParm.setData("CASE_NO", McaseNo);
			TParm admInfo = ADMTool.getInstance().getADM_INFO(admParm);
			Pat M_PAT = Pat.onQueryByMrNo(admInfo.getValue("MR_NO", 0));
			this.setValue("M_MR_NO", admInfo.getValue("MR_NO", 0));
			this.setValue("M_NAME", M_PAT.getName());
			this.callFunction("UI|LM1|setVisible", true);
			this.callFunction("UI|LM2|setVisible", true);
			this.callFunction("UI|M_MR_NO|setVisible", true);
			this.callFunction("UI|M_NAME|setVisible", true);
		} else {
			this.callFunction("UI|LM1|setVisible", false);
			this.callFunction("UI|LM2|setVisible", false);
			this.callFunction("UI|M_MR_NO|setVisible", false);
			this.callFunction("UI|M_NAME|setVisible", false);
		}
	}

	/**
	 * ��������
	 */
	public void setBirth() {
		if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
			return;
		String AGE = com.javahis.util.StringUtil.showAge(
				(Timestamp) getValue("BIRTH_DATE"),
				(Timestamp) getValue("IN_DATE"));
		setValue("AGE", AGE);
	}

	/**
	 * ���������ؼ��ɱ༭
	 */
	public void setUi() {
		callFunction("UI|PAT_NAME|setEnabled", true);
		callFunction("UI|SEX_CODE|setEnabled", true);
		callFunction("UI|BIRTH_DATE|setEnabled", true);
		callFunction("UI|AGE|setEnabled", true);
		callFunction("UI|OCC_CODE|setEnabled", true);
		callFunction("UI|BORN|setEnabled", true);
		callFunction("UI|IDNO|setEnabled", true);
		callFunction("UI|SPECIES_CODE|setEnabled", true);
		callFunction("UI|NATION_CODE|setEnabled", true);
		callFunction("UI|MARRIAGE_CODE|setEnabled", true);
		callFunction("UI|COMPANY_DESC|setEnabled", true);
		callFunction("UI|POST_CODE|setEnabled", true);
		callFunction("UI|RESID_POST_CODE|setEnabled", true);
		callFunction("UI|TEL_01|setEnabled", true);
		callFunction("UI|RESID_ROAD|setEnabled", true);
		callFunction("UI|CONTACTS_NAME|setEnabled", true);
		callFunction("UI|RELATION_CODE|setEnabled", true);
		callFunction("UI|CONTACTS_TEL|setEnabled", true);
		callFunction("UI|CONTACTS_ADDRESS|setEnabled", true);

	}

	/**
	 * ����������ѡ�¼�
	 */
	public void onNewPatInfo() {
		this.onClear();
		TCheckBox checkbox = (TCheckBox) this
				.callFunction("UI|NEW_PAT_INFO|getThis");
		if (checkbox.isSelected()) {
			callFunction("UI|NEW_BORN_FLG|setEnabled", false);
			callFunction("UI|MR_NO|setEnabled", false);
			callFunction("UI|IPD_NO|setEnabled", false);
			callFunction("UI|NEW_PAT|setEnabled", true);
			this.callFunction("UI|NEW_PAT|setText", "������������");
		} else {
			callFunction("UI|NEW_BORN_FLG|setEnabled", true);
			callFunction("UI|MR_NO|setEnabled", true);
			callFunction("UI|IPD_NO|setEnabled", true); // סԺ��
			callFunction("UI|NEW_PAT|setEnabled", false);
			this.callFunction("UI|NEW_PAT|setText", "�޸Ĳ�������");
		}
	}

	/**
	 * ��λ����
	 */
	public void onBedNo() {
		TParm sendParm = new TParm();
		if (getValue("DEPT_CODE") == null || "".equals(getValue("DEPT_CODE"))) {
			this.messageBox("��ѡ����ң�");
			return;
		}
		if (getValue("STATION_CODE") == null
				|| "".equals(getValue("STATION_CODE"))) {
			this.messageBox("��ѡ������");
			return;
		}
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("TYPE", "RESV"); // ===== chenxi modify 20130301
											// ԤԼ��ʱ��ռ��Ҳ��ԤԼ
		sendParm.setData("HAVEBEDNO",haveBedNo);
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
		if (reParm != null) {
			this.setValue("BED_NO", getBedDesc(reParm.getValue("BED_NO", 0)));
			this.setValue("YELLOW_SIGN", reParm.getData("YELLOW_SIGN", 0));
			this.setValue("RED_SIGN", reParm.getData("RED_SIGN", 0));
		}
	}

	/**
	 * ��������
	 */
	public void onBed() {
		if (this.getValue("BED_DESC") == null
				|| "".equals(this.getValue("BED_DESC"))) {
			this.messageBox("�˲�����δ���Ŵ�λ��");
			return;
		}
		TParm bed = new TParm();

		TParm parm = new TParm();
		parm.setData("MR_NO", getValue("MR_NO"));
		parm.setData("IPD_NO", getValue("IPD_NO"));
		TParm result = ADMInpTool.getInstance().queryCaseNo(parm);
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", result.getData("CASE_NO", 0));
		sendParm.setData("MR_NO", result.getData("MR_NO", 0));
		sendParm.setData("IPD_NO", result.getData("IPD_NO", 0));
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("BED_NO", getValue("BED_DESC"));
		bed.setData("BED_NO", getValue("BED_DESC"));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = result.getData("CASE_NO", 0).toString().trim();
		int count = check.getCount("BED_NO");

		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				this.messageBox("�˲�����������������");
				return;
			}
		}
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
	}

	/**
	 * ��ɫͨ��
	 */
	public void onGreenPath() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "O");
		parm.setData("MR_NO", getValue("MR_NO"));
		this.openWindow("%ROOT%\\config\\bil\\BILGreenPath.x", parm);
	}

	/**
	 * Ԥ����
	 */
	public void onBilpay() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", caseNo);
		this.openWindow("%ROOT%\\config\\bil\\BILPay.x", sendParm);
		TParm parm = ADMInpTool.getInstance().queryCaseNo(sendParm);
		this.setValue("TOTAL_BILPAY", parm.getData("TOTAL_BILPAY", 0));

	}

	/**
	 * ������ע�� (�Ƶ�ԤԼסԺ�˷�����ʱ������)
	 */
	public void onChild() {
		if (pat == null) {
			this.messageBox("û�в�����Ϣ��");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMBabyFlg.x", sendParm);
		if (reParm == null) {
			setValue("NEW_BORN_FLG", "N");
			return;
		}
		if (reParm.checkEmpty("IPD_NO", reParm)) {
			setValue("NEW_BORN_FLG", "N");
		} else {
			setValue("NEW_BORN_FLG", "Y");
			this.setValue("IPD_NO", reParm.getData("IPD_NO"));
			McaseNo = reParm.getData("M_CASE_NO").toString();
		}
	}

	/**
	 * ������ѯ
	 */
	public void onQuery() {
		TParm sendParm = new TParm();
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onMrno();
	}

	/**
	 * ȡ��סԺ
	 */
	public void onStop() {
		if (!checkCanInp()) {
			this.messageBox_("�˲����Ѿ���ס����λ,����ȡ��סԺ");
			return;
		}
		if (!checkBilPay()) {
			this.messageBox_("�˲�������Ԥ����δ��,����ȡ��סԺ");
			return;
		}
		//fux modify 2010805  
		if (!checkCanPay()) {
			this.messageBox_("�˲����Ѿ���������,����ȡ��סԺ");
			return;
		}

		int check = this.messageBox("��Ϣ", "�Ƿ�ȡ����", 0);
		if (check == 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("PSF_KIND", "INC");
			parm.setData("PSF_HOSP", "");
			parm.setData("CANCEL_FLG", "Y");
			parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
			parm.setData("CANCEL_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			// ======pangben modify 20110617 start
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
			}
			// ======pangben modify 20110617 start
			TParm result = TIOM_AppServer.executeAction(
					"action.adm.ADMInpAction", "ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				
				// add by wangb 2016/2/2 ȡ��סԺ���ʹ�����Ϣ START
				TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A04");
	            if (xmlParm.getErrCode() < 0) {
	                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
	            }
	            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
	            if (xmlParm.getErrCode() < 0) {
	                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
	            }
	            // add by wangb 2016/2/2 ȡ��סԺ���ʹ�����Ϣ END
			}
			this.setMenu(false);
		} else {
			this.setMenu(true);
			return;
		}

	}

	/**
	 * ����
	 * 
	 * @throws IOException
	 */
	public void onPhoto() throws IOException {

		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		new File(dir).mkdirs();
		JMStudio jms = JMStudio.openCamera(dir + photoName);
		jms.addListener("onCameraed", this, "sendpic");
	}

	/**
	 * //ע���������
	 */
	public void onRegist() {
		// ע���������
		JMFRegistry jmfr = new JMFRegistry();
		jmfr.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {
				event.getWindow().dispose();
				System.exit(0);
			}

		});
		jmfr.setVisible(true);

	}

	/**
	 * ������Ƭ
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(Image image) {
		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		String localFileName = dir + photoName;
		try {
			File file = new File(localFileName);
			byte[] data = FileTool.getByte(localFileName);
			if (file.exists()) {
				new File(localFileName).delete();
			}
			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
		} catch (Exception e) {
			System.out.println("e::::" + e.getMessage());
		}
		this.viewPhoto(pat.getMrNo());

	}

	/**
	 * ��ʾphoto
	 * 
	 * @param mrNo
	 *            String ������
	 */
	public void viewPhoto(String mrNo) {

		String photoName = mrNo + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null) {
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			// Image image = ImageTool.getImage(data);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}

	/**
	 * סԺ�Ʊ�Combo�¼�
	 */
	public void onDEPT_CODE() {
		// ���סԺ����������ҽʦ����λ�ŵ�ѡ��ֵ
		this.clearValue("STATION_CODE;VS_DR_CODE;BED_NO;BED_DESC");
	}

	/**
	 * ����Ʊ�Combo�¼�
	 */
	public void onOPD_DEPT_CODE() {
		// �������ҽʦ��ѡ��ֵ
		this.clearValue("OPD_DR_CODE");
	}

	/**
	 * ��������
	 * 
	 * @param type
	 *            String new:�½� update:�޸�
	 */
	public void addMRO(String type) {
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		String mr_no = this.getValueString("MR_NO");
		String hospid = "";
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		if (regionParm.getCount() > 0) {
			hospid = regionParm.getValue("NHI_NO", 0);
		}
		// System.out.println("------------------"+hospid);
		TParm result = new TParm();
		// �ж��Ƿ��½�����
		if ("new".equals(type)) {
			TParm creat = new TParm();
			creat.setData("MR_NO", mr_no);
			creat.setData("CASE_NO", caseNo);
			creat.setData("OPT_USER", user_id);
			creat.setData("OPT_TERM", user_ip);
			creat.setData("DAY_OPE_FLG", this.getValue("DAY_OPE_FLG"));			
			// ============pangben modify 20110617 start
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				creat.setData("REGION_CODE", Operator.getRegion());
			// ============pangben modify 20110617 stop
			creat.setData("HOSP_ID", hospid);
			// �½�����
			result = MROTool.getInstance().insertMRO(creat);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			// �жϸò������Ƿ��Ѿ����ڲ������� ������ھͲ��ٲ���
			if (!MROQueueTool.getInstance().checkHasMRO_MRV(mr_no)) {
				TParm mro_mrv = new TParm();
				String region = Operator.getRegion();
				mro_mrv.setData("MR_NO", mr_no);
				mro_mrv.setData("IPD_NO", IPD_NO);
				mro_mrv.setData("CREATE_HOSP", region);
				mro_mrv.setData("IN_FLG", "2");
				mro_mrv.setData("CURT_HOSP", region);
				mro_mrv.setData("CURT_LOCATION", region);
				mro_mrv.setData("TRAN_HOSP", region);
				mro_mrv.setData("BOX_CODE", "");
				mro_mrv.setData("OPT_USER", Operator.getID());
				mro_mrv.setData("OPT_TERM", Operator.getIP());
				result = MROQueueTool.getInstance().insertMRO_MRV(mro_mrv);
				if (result.getErrCode() < 0) {
					this.messageBox_("�������ʧ�ܣ�");
				}
			}
			// ��ѯ������ԤԼ��Ϣ ������ȡ�ż������
			TParm resv = ADMResvTool.getInstance().selectNotIn(mr_no);
			String OE_DIAG_CODE = "";
			// ���������ԤԼ��Ϣ
			if (resv.getCount() > 0) {
				OE_DIAG_CODE = resv.getValue("DIAG_CODE", 0);
			}
			TParm b_parm = new TParm();
			b_parm.setData("BED_NO", BED_NO);
			TParm bed = SYSBedTool.getInstance().queryAll(b_parm);
			// �޸Ĳ��� סԺ��Ϣ
			TParm adm = new TParm();
			adm.setData("IPD_NO", this.getValueString("IPD_NO"));
			adm.setData("IN_DATE", StringTool.getString(
					(Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss"));
			adm.setData("IN_DEPT", this.getValueString("DEPT_CODE"));
			adm.setData("IN_STATION", this.getValueString("STATION_CODE"));
			adm.setData("IN_ROOM_NO", bed.getValue("ROOM_CODE", 0)); // ��Ժ����
			// ���ݴ�λ��
			// ��ѯ��
			adm.setData("OE_DIAG_CODE", OE_DIAG_CODE); // �ż������
			adm.setData("IN_CONDITION", this.getValue("PATIENT_CONDITION")); // ��Ժ״̬
			adm.setData("IN_COUNT", this.getValue("IN_COUNT") == null ? "1"
					: this.getValue("IN_COUNT")); // סԺ����
			adm.setData("PG_OWNER", Operator.getID()); // ��ҳ������
			adm.setData("STATUS", "0"); // ״̬ 0 ��Ժ��1 ��Ժδ��ɣ�2 ��Ժ�����
			adm.setData("CASE_NO", caseNo);
			adm.setData("ADM_SOURCE", this.getValue("ADM_SOURCE")); // ������Դ
			adm.setData("AGN_CODE", this.getValue("AGN_CODE") == null ? ""
					: this.getValue("AGN_CODE")); // 31����סԺ
			adm.setData("AGN_INTENTION", this.getValue("AGN_INTENTION")); // 31����סԺԭ��
			adm.setData("DAY_OPE_FLG",this.getValue("DAY_OPE_FLG"));	
			// System.out.println("-=-------------------" + adm);
			result = MROTool.getInstance().updateADMData(adm);
			if (result.getErrCode() < 0) {
				this.messageBox_(result.getErrText());
			}
		}

		// �޸Ĳ��� ���߻�����Ϣ
		TParm opt = new TParm();
		opt.setData("MR_NO", mr_no);
		opt.setData("CASE_NO", caseNo);
		opt.setData("OPT_USER", user_id);
		opt.setData("OPT_TERM", user_ip);
		result = MROTool.getInstance().updateMROPatInfo(opt);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}

	/**
	 * ���ݴ�λcode
	 * 
	 * @param Bed_Code
	 *            String
	 * @return String
	 */
	private String getBedDesc(String Bed_Code) {
		this.setValue("BED_DESC", Bed_Code);
		TComboBox combo = (TComboBox) this.getComponent("BED_DESC");
		return combo.getSelectedName();
	}

    /**
     * ��ӡסԺ֤
     */
    public void onPrint() {
		if (StringUtils.isEmpty(getValueString("MR_NO"))) {
			return;
		}
    	
        String caseNo = "";
        //this.messageBox("mrNo===="+pat.getMrNo());
        TParm resv = ADMResvTool.getInstance().selectNotIn(pat.getMrNo());
        //this.messageBox("======resv========"+resv);
        TParm mrParm = new TParm();
        mrParm.setData("MR_NO", pat.getMrNo());
        //TParm result = ADMInpTool.getInstance().checkAdmInp(mrParm);
        //this.messageBox("result============"+result);
        //if (result.getCount() < 0) {
        //    messageBox("�ò���δ��Ժ��");
        //    return;
        //}
        //ԤԼסԺ��
        caseNo = resv.getValue("RESV_NO", 0);        
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        
        // ��ԤԼ����ס
        if (checkAdmInp(pat.getMrNo())) {
        	caseNo = this.caseNo;
        }

        String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
        sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";

//        System.out.println("===sql===" + sql);
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
        if (result1.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        if (result1.getCount() < 0) {
            this.onPrint1();
        } else {
            String filePath = result1.getValue("FILE_PATH", 0);
            String fileName = result1.getValue("FILE_NAME", 0);
            TParm p = new TParm();
            p.setData("RESV_NO", caseNo);
            TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p);
            TParm parm = new TParm();
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("IPD_NO", pat.getIpdNo());
            parm.setData("PAT_NAME", pat.getName());
            parm.setData("SEX", pat.getSexString());
            parm.setData("AGE", StringUtil.showAge(pat.getBirthday(),
                    resvPrint.getTimestamp("APP_DATE", 0))); //����
            //parm.setData("CASE_NO", caseNo);
            parm.setData("CASE_NO", caseNo);//duzhw add
            Timestamp ts = SystemTool.getInstance().getDate();
            parm.setData("ADM_TYPE", "O");
            parm.setData("DEPT_CODE", resvPrint.getValue("DEPT_CODE", 0));
            parm.setData("STATION_CODE", resvPrint.getValue("STATION_CODE", 0));
            
//        	this.setDayOpeFlg("Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "�ռ�����": "");			//   2017/3/25   	by  yanmm   �����ռ�������ѡ		
//    		parm.setData("DAY_OPE_FLG","Y".equals(resvPrint.getValue("DAY_OPE_FLG",0)) ? "�ռ�����": "") ;

 
            parm.setData("ADM_DATE", ts);
            parm.setData("STYLETYPE", "1");
            parm.setData("RULETYPE", "3");
            parm.setData("SYSTEM_TYPE", "ODO");
            TParm emrFileData = new TParm();
            emrFileData.setData("FILE_PATH", filePath);
            emrFileData.setData("FILE_NAME", fileName);
            emrFileData.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
            emrFileData.setData("SUBCLASS_CODE", subClassCode);
            emrFileData.setData("CLASS_CODE", classCode);
            emrFileData.setData("FLG", true);
            parm.setData("EMR_FILE_DATA", emrFileData);
    	//	parm.addListener("EMR_LISTENER",this,"emrListener");				
            this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
        }
    }

    public void onPrint1() {
        //��ȡԤԼ��
        String sql = " select * from adm_resv where mr_no = '"+MR_NO+"' AND CAN_DATE IS NULL AND IN_CASE_NO IS NULL ORDER BY RESV_NO DESC ";
        TParm resvParm = new TParm(TJDODBTool.getInstance().select(sql));
        String resvNo = resvParm.getValue("RESV_NO", 0);
        TParm myParm = new TParm();
        myParm.setData("CASE_NO", caseNo);
        TParm casePrint = ADMInpTool.getInstance().selectall(myParm);
        TParm actionParm = new TParm();
        this.setDayOpeFlg("Y".equals(resvParm.getValue("DAY_OPE_FLG",0)) ? "�ռ�����":"");			//   2017/3/25   	by  yanmm   �����ռ�������ѡ		
        actionParm.setData("DAY_OPE_FLG","Y".equals(resvParm.getValue("DAY_OPE_FLG",0)) ? "�ռ�����":"");
        actionParm.setData("MR_NO", pat.getMrNo());
        actionParm.setData("IPD_NO", pat.getIpdNo());
        actionParm.setData("PAT_NAME", pat.getName());
        actionParm.setData("SEX", pat.getSexString());
        actionParm.setData("AGE", StringUtil.showAge(pat.getBirthday(), casePrint.getTimestamp("IN_DATE", 0))); //����
        Timestamp ts = SystemTool.getInstance().getDate();
        //actionParm.setData("CASE_NO", caseNo);
        actionParm.setData("CASE_NO", resvNo); //duzhw add
        actionParm.setData("ADM_TYPE", "O");
        actionParm.setData("DEPT_CODE", casePrint.getValue("DEPT_CODE", 0));
        actionParm.setData("STATION_CODE", casePrint.getValue("STATION_CODE", 0));
        actionParm.setData("ADM_DATE", ts);
        actionParm.setData("STYLETYPE", "1");
        actionParm.setData("RULETYPE", "3");
        actionParm.setData("SYSTEM_TYPE", "ODO");
        TParm emrFileData = new TParm();
        String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
        String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
        String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
        String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
        emrFileData.setData("TEMPLET_PATH", path);
        emrFileData.setData("EMT_FILENAME", fileName);
        emrFileData.setData("SUBCLASS_CODE", subClassCode);
        emrFileData.setData("CLASS_CODE", classCode);
        actionParm.setData("EMR_FILE_DATA", emrFileData);
        actionParm.addListener("EMR_LISTENER",this,"emrListener");			//   2017/3/25   	by  yanmm   �����ռ�������ѡ	
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
    }

    /* �¼�
    * @param parm TParm
    */
   public void emrListener(TParm parm)					//   2017/3/25   	by  yanmm   �����ռ�������ѡ		
   {
       parm.runListener("setCaptureValue","DAY_OPE_FLG",this.getDayOpeFlg());
       
   }
    
    
	/**
	 * ���Ӧ�Ļ�ʿվ������Ϣ
	 * 
	 * @param MR_NO
	 *            String ������
	 * @param CASE_NO
	 *            String �������
	 * @param PAT_NAME
	 *            String ��������
	 */
	public void sendInwStationMessages(String MR_NO, String CASE_NO,
			String PAT_NAME) {

		// $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
		// START==================$$//
		// SocketLink client1 = SocketLink.running("", "ODISTATION", "odi");
		SocketLink client1 = SocketLink.running("", Operator.getDept(),
				Operator.getDept());
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		/**
		 * client1.sendMessage("INWSTATION", "CASE_NO:" + CASE_NO +
		 * "|STATION_CODE:" + Operator.getStation() + "|MR_NO:" + MR_NO +
		 * "|PAT_NAME:" + PAT_NAME);
		 **/
		client1.sendMessage(Operator.getStation(), "CASE_NO:" + CASE_NO
				+ "|STATION_CODE:" + Operator.getStation() + "|MR_NO:" + MR_NO
				+ "|PAT_NAME:" + PAT_NAME);

		if (client1 == null)
			return;
		client1.close();

		// $$ ============ Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27
		// END==================$$//

	}

	/**
	 * ģ����ѯ���ڲ��ࣩ ��������滻
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
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ����������
	 */
	public void onImmunity() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", IPD_NO);
		this.openDialog("%ROOT%\\config\\adm\\ADMChildImmunity.x", parm);

	}

	/**
	 * ��鲡��������Ϣ�Ƿ�����ύ
	 * 
	 * @return boolean
	 */
	private boolean checkPatInfo() {
		// emr�ṹ��������Ҫ�����ֶα���
		if (this.getValueString("TEL_HOME").length() <= 0) {
			this.messageBox_("����д��ͥ�绰��");
			this.grabFocus("TEL_HOME");
			return false;
		}
		if (this.getValueString("RESID_ADDRESS").length() <= 0) {
			this.messageBox_("����д���ڵ�ַ��");
			this.grabFocus("RESID_ADDRESS");
			return false;
		}
		return true;
	}

	/**
	 * ��ѯ���˵�סԺ����
	 * 
	 * @param mrNo
	 *            String
	 * @return int
	 */
	private int getInCount(String mrNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("CANCEL_FLG", "N");
		TParm result = ADMInpTool.getInstance().selectall(parm); // ��ѯ�ò��˵�����δȡ����סԺ��Ϣ
		if (result.getErrCode() < 0) {
			return 1;
		}
		return result.getCount();
	}

	/**
	 * �����ӡ
	 */
	public void onWrist() {
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		// this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist", print);
		
		// ��ѯһ���ٴ������߱��
		String recruitNo = MEDApplyTool.getInstance().queryRecruitNo(caseNo);
		
		// �����������߱�ŵ���Ϊһ���ٴ��������ߣ���ӡ���ʹ�õ���ģ����ʽ
		if (StringUtils.isEmpty(recruitNo)) {
			// ����ϲ�modify by wanglong 20130730
			this.openPrintDialog(
					IReportTool.getInstance().getReportPath("ADMWrist.jhw"),
					IReportTool.getInstance()
							.getReportParm("ADMWrist.class", print));
		} else {
			// ��ѯһ���ٴ��������
			String planNo = MEDApplyTool.getInstance().queryPlanNo(caseNo);
			String patName = recruitNo + "-" + pat.getName();
			print.setData("PatName", "TEXT", patName);
			print.setData("PlanNo", "TEXT", planNo);
			this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMWrist", print);
		}
		
	}
	
	
	/**
	 * ��ͯ�����ӡ
	 */
	public void onChildWrist() {
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMChildWrist", print);
	}
	
	/**
	 * ������ʽ�����ӡ
	 */
	public void onAdultWrist() {//add by guoy 20150818
		if (this.getValueString("MR_NO").length() == 0 || pat == null) {
			return;
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", pat.getMrNo());
		print.setData("PatName", "TEXT", pat.getName());
		print.setData("Sex", "TEXT", pat.getSexString());
		print.setData("BirthDay", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		
		// һ���ٴ������߱��
		String recruitNo = "";
		// һ���ٴ��������
		String planNo = "";
		
		// ��סԺ������ϰ�߲�ͬ�������ֱ���ǰ�ͱ�������ַ�ʽ��ӡ���
		if (StringUtils.isEmpty(caseNo)) {
			String sql = "SELECT RECRUIT_NO,OPD_CASE_NO FROM ADM_RESV WHERE MR_NO = '"
					+ this.getValueString("MR_NO")
					+ "' AND CAN_DATE IS NULL AND IN_CASE_NO IS NULL";
			// ��ѯ��ԤԼδ�Ǽǵ�����
			TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (admParm.getErrCode() == 0 && admParm.getCount() > 0) {
				recruitNo = admParm.getValue("RECRUIT_NO", 0);

				if (StringUtils.isNotEmpty(recruitNo)) {
					String opdCaseNo = admParm.getValue("OPD_CASE_NO", 0);
					if (StringUtils.isNotEmpty(opdCaseNo)) {
						opdCaseNo = opdCaseNo.split(",")[0];
						// ��ѯ�����Ľ�����﷽����
						sql = "SELECT PLAN_NO FROM HRM_CONTRACTD A,HRM_PATADM B WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE AND A.ROLE_TYPE = 'PIC' AND B.CASE_NO = '"
								+ opdCaseNo + "'";
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getErrCode() == 0 && result.getCount() > 0) {
							planNo = result.getValue("PLAN_NO", 0);
						}
					}
				}
			}
		} else {
			// ��ѯһ���ٴ������߱��
			recruitNo = MEDApplyTool.getInstance().queryRecruitNo(caseNo);
			// ��ѯһ���ٴ��������
			planNo = MEDApplyTool.getInstance().queryPlanNo(caseNo);
		}
		
		// �����������߱�ŵ���Ϊһ���ٴ��������ߣ���ӡ���ʹ�õ���ģ����ʽ
		if (StringUtils.isEmpty(recruitNo)) {
			this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMAdultWrist", print);
		} else {
			String patName = recruitNo + "-" + pat.getName();
			print.setData("PatName", "TEXT", patName);
			print.setData("PlanNo", "TEXT", planNo);
			this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMWrist", print);
		}
	}

	/**
	 * ����xml�ļ���סԺ�Ǽ�xml =======================pangben modify 20110812
	 */
	public void djXML() {
		// 1.��������
		if (this.getValueString("MR_NO").length() <= 0) {
			return;
		}
		TParm inparm = new TParm();
		inparm.insertData("TBR", 0, this.getValue("MR_NO")); // ������
		inparm.insertData("XM", 0, this.getValue("PAT_NAME")); // ����
		inparm.insertData("XB", 0, this.getValue("SEX_CODE")); // �Ա�
		inparm.insertData("CSNY", 0, StringTool.getString(
				(Timestamp) this.getValue("BIRTH_DATE"), "yyyyMMdd")); // ��������
		inparm.insertData("SFZH", 0, this.getValue("IDNO")); // ���֤��
		inparm.insertData("YRXZ", 0, "��ְ"); // ��Ա����
		inparm.insertData("XH", 0, caseNo); // ��Ժ���
		inparm.insertData("RYSJ", 0, StringTool.getString(
				(Timestamp) this.getValue("IN_DATE"), "yyyyMMddHHmmss")); // ��Ժʱ��
		inparm.insertData("LXDH", 0, this.getValue("CONTACTS_TEL")); // ��ϵ�绰
		inparm.insertData("KSM", 0, this.getValue("DEPT_CODE")); // ������
		inparm.insertData("ZYH", 0, this.getValue("IPD_NO")); // סԺ��
		TTextFormat format = (TTextFormat) this.getComponent("STATION_CODE");
		inparm.insertData("BQMC", 0, format.getText()); // ��������
		inparm.insertData("CWH", 0, this.getValue("BED_NO")); // ��λ��
		inparm.insertData("ZHYE", 0, "0"); // �����˻����
		inparm.insertData("YSM", 0, this.getValue("VS_DR_CODE")); // ����ҽ����
		inparm.insertData("XZMC", 0, "0"); // ����
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "XB");
		inparm.addData("SYSTEM", "COLUMNS", "CSNY");
		inparm.addData("SYSTEM", "COLUMNS", "SFZH");
		inparm.addData("SYSTEM", "COLUMNS", "YRXZ");
		inparm.addData("SYSTEM", "COLUMNS", "XH");
		inparm.addData("SYSTEM", "COLUMNS", "RYSJ");
		inparm.addData("SYSTEM", "COLUMNS", "LXDH");
		inparm.addData("SYSTEM", "COLUMNS", "KSM");
		inparm.addData("SYSTEM", "COLUMNS", "ZYH");
		inparm.addData("SYSTEM", "COLUMNS", "BQMC");
		inparm.addData("SYSTEM", "COLUMNS", "CWH");
		inparm.addData("SYSTEM", "COLUMNS", "ZHYE");
		inparm.addData("SYSTEM", "COLUMNS", "YSM");
		inparm.addData("SYSTEM", "COLUMNS", "XZMC");
		inparm.setCount(1);
		// System.out.println("=======inparm=============" + inparm);
		// 2.�����ļ�
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/zydjxx.xml");
		this.messageBox("���ɳɹ�");
	}

	/**
	 * �������:��¼ҽ������סԺ���
	 */
	public void readDjXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		if (null == parm)
			return;
		this.setValue(
				"IPD_NO",
				parm.getValue("ZYH").substring(1,
						parm.getValue("ZYH").indexOf("]")));
	}

	/**
	 * Ѫ��Hl7�ӿ�
	 */
	public void sendHl7message() {
		TParm parm = new TParm();
		String type = "ADM_IN";
		List list = new ArrayList();
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", this.caseNo);
		parm.setData("IPD_NO", this.IPD_NO);
		list.add(parm);
		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
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
	 * ����סԺ�ǼǱ��еĵ�������Ϣ
	 */
	public void updateADMInpSDInfo() {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("CASE_NO", caseNo);
		action.setData("DISE_CODE", this.getValue("DISE_CODE") + "");
		TParm result = CLPSingleDiseTool.getInstance().updateADMInpSDInfo(
				action);
		if (result.getErrCode() < 0) {
			messageBox("��������Ϣ����ʧ��");
			return;
		}
	}

	// ================= chenxi add 20130319 �����֤����Ϣ
	public void onIdCardNo() {
		// String dir = "C:/Program Files/Routon/���֤��������Ķ����" ;
		String dir = SystemTool.getInstance().Getdir();
		
		// add by yangjj 20150629
		System.out.println("���֤��������־��"+dir);
		
		CardInfoBO cardInfo = null;
		try {
			cardInfo = IdCardReaderUtil.getCardInfo(dir);
		} catch (Exception e) {
			this.messageBox("���»�ȡ��Ϣ");
			System.out.println("���»�ȡ��Ϣ:" + e.getMessage());
			// TODO: handle exception
		}
		// CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
		if (cardInfo == null) {
			this.messageBox("δ������֤��Ϣ,�����²���");
			return;
		}
		
		//add by yangjj 20150629
		System.out.println("������Ϣ�� ���֤�ţ�"+cardInfo.getCode()+",������"+cardInfo.getName());
		
		// ͨ�����֤�Ų�ѯ������Ϣ
		TParm parm = new TParm();
		parm.setData("IDNO", cardInfo.getCode().trim());// ���֤��
		TParm infoParm = PatTool.getInstance().getInfoForIdNo(parm);
		
		//add by yangjj 20150629
		System.out.println("infoParm:"+infoParm);
		
		if (infoParm.getCount() > 0) {
			// this.messageBox("�Ѵ��ڴ˾��ﲡ����Ϣ");
		} else {
			String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
					+ cardInfo.getName() + "%'";
			infoParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (infoParm.getCount() <= 0) {
				this.messageBox("�����ڴ˾��ﲡ����Ϣ");
			}
		}
		if (infoParm.getCount() > 0) {// ����������ʾ===pangben 2013-8-6
			if (infoParm.getCount() == 1) {// ֻ����һ������
				if (!checkPatInfo(infoParm.getValue("MR_NO", 0))) {
					return;
				}
				this.setValue("MR_NO", infoParm.getValue("MR_NO", 0));
			} else {
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",// ���Ψһ�Ĳ�����
						infoParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					if (!checkPatInfo(patParm.getValue("MR_NO"))) {
						return;
					}
					this.setValue("MR_NO", patParm.getValue("MR_NO"));
				} else {
					return;
				}
			}
			this.sendpic(cardInfo.getImagesPath().get(2));
			onMrno();
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // ���ڵ�ַ
			setValue("SPECIES_CODE",
					onGetSPECIES_CODE(cardInfo.getFolk() + "��")); // ����
		} else {
			this.onClear();// û�в�ѯ�����ݽ������������
			setValue("PAT_NAME", cardInfo.getName()); // ����
			setValue("IDNO", cardInfo.getCode()); // ���֤��
			setValue("SEX_CODE", cardInfo.getSex().equals("��") ? "1" : "2"); // �Ա�
			setValue("BIRTH_DATE",
					StringTool.getTimestamp(cardInfo.getBirth(), "yyyyMMdd")); // ����
			setValue("RESID_ADDRESS", cardInfo.getAdd()); // ���ڵ�ַ
			setValue("SPECIES_CODE",
					onGetSPECIES_CODE(cardInfo.getFolk() + "��")); // ����
			setBirth(); // ��������
		}
		// IdCardO.getInstance().delFolder(dir) ;
	}

	/**
	 * У�鲡����Ϣ
	 * 
	 * @return ===========pangben 2013-8-6 �������֤У��
	 */
	private boolean checkPatInfo(String mrNo) {
		if (this.getValue("MR_NO").toString().length() > 0 && null != pat) {
			if (!this.getValue("MR_NO").equals(mrNo)) {
				if (this.messageBox("��ʾ", "���֤��Ϣ�뵱ǰ���ﲡ����Ϣ����,�Ƿ����", 2) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ������Ƭ
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(String localFileName) {
		String dir = "";
		String mrNo = getValue("MR_NO").toString();
		String photoName = mrNo + ".jpg";
		File file = new File(localFileName);
		try {
			// String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
			// dir = dir + mrNo.substring(0, 3) + "\\"
			// + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			File filepath = new File(dir);
			if (!filepath.exists())
				filepath.mkdirs();
			BufferedImage input = ImageIO.read(file);
			Image scaledImage = input.getScaledInstance(300, 400,
					Image.SCALE_DEFAULT);
			BufferedImage output = new BufferedImage(300, 400,
					BufferedImage.TYPE_INT_BGR);
			output.createGraphics().drawImage(scaledImage, 0, 0, null); // ��ͼ
			ImageIO.write(output, "jpg", new File(dir + photoName));
			sendpic(scaledImage);
		} catch (Exception e) {
		}
	}

	/**
	 * ȡ����code
	 */
	public String onGetSPECIES_CODE(String name) {
		String code = "";
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' "
				+ "  AND CHN_DESC = '" + name + "'";
		// System.out.println("name======"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return code;
		code = parm.getValue("ID", 0);
		return code;

	}

	// ================= chenxi add 20130319 �����֤����Ϣ
	public void onIdentificationPic() {
		String dir = TIOM_FileServer.getPath("PatInfPIC.LocalPath");
		String mrNo = getValue("MR_NO").toString();
		if (mrNo == null || mrNo.equals("")) {
			this.messageBox("���ȶ�ȡ���ﲡ����Ϣ");
			return;
		}
		try {
//			Display display = Display.getDefault();
//			SWTshell shell = new SWTshell(display, dir, mrNo);
//			shell.open();
//			shell.layout();
//			while (!shell.isDisposed()) {
//				if (!display.readAndDispatch()) {
//					display.sleep();
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		IdCardO.getInstance().delFolder(dir);
	}

	/**
	 * ��֤�ò����Ƿ��Ѿ�����סԺ֤
	 * 
	 * @return �Ƿ�����סԺ֤
	 * @author wangb
	 */
	private boolean checkHospCard() {
		String querCaseNO = "";
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		
		// ��ѯ�������һ�� ԤԼ��Ϣ
		String resvSql = "SELECT * FROM ADM_RESV WHERE MR_NO = '" + pat.getMrNo()
				+ "' AND CAN_DATE IS NULL ORDER BY RESV_NO DESC";
		
		TParm resv = new TParm(TJDODBTool.getInstance().select(resvSql));
		
		if (resv.getErrCode() < 0) {
			this.messageBox("��ѯ����ԤԼסԺ��Ϣ����");
			err("ERR:" + resv.getErrText());
			return false;
		}
		
		querCaseNO = resv.getValue("RESV_NO", 0);
		
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO = '#' AND CLASS_CODE='"
				+ classCode + "' AND SUBCLASS_CODE='" + subClassCode + "'";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.replaceFirst("#", querCaseNO)));
        
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯסԺ֤������Ϣ����");
			err("ERR:" + result.getErrText());
			return false;
		}
		
		if (result.getCount() > 0) {
			return true;
		}
		
		return false;
	}
	
	
	// modified by WangQing 20170315
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
		
}
