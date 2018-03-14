package com.javahis.ui.adm;

import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import javax.swing.JLabel;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.ImageTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TComboBox;

import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.Operator;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;

import com.javahis.util.StringUtil;

//import org.eclipse.wb.swt.PictureShow;

/**
 * <p>
 * Title: ����������Ϣ��ADM���ת���ã�
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
 * Company:JavaHis
 * </p>
 * 
 * @author JiaoY 2009.01.14
 * @version 1.0  
 */
public class ADMPatInfoControl extends TControl {
	public ADMPatInfoControl() {
	}

	TParm acceptData = new TParm(); // �Ӳ�
	Pat pat = new Pat();
	TParm initParm = new TParm(); // ��ʼ����

	public void onInit() {
		callFunction("UI|TRAN_BED|setEnabled", false);
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			this.initUI(acceptData);
		}
	}

	/**
	 * �����ʼ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public void initUI(TParm parm) {
		Pat pat = new Pat();
		String mrNo = acceptData.getData("MR_NO").toString();
		pat = pat.onQueryByMrNo(mrNo);
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("SEX_CODE", pat.getSexCode());
		this.setValue("IPD_NO", acceptData.getData("IPD_NO"));
		this.setValue("SPECIES_CODE", pat.getSpeciesCode()); //����
		// �趨�ɱ���Ȩ�� �ɷ�ת��תҽʦ
		if (acceptData.getBoolean("SAVE_FLG")) {
			callFunction("UI|save|setVisible", true);
		} else {
			callFunction("UI|save|setVisible", false);
		}
		this.initQuery();
		viewPhoto(pat.getMrNo());
	}

	/**
	 * ��ʾphoto
	 * 
	 * @param mrNo
	 *            String
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
			if (data == null)
				return;
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
			g.fillRect(4, 10, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 10, 136, 180, null);
			}
		}
	}

	/**
	 * ��ʼ����ѯ
	 */
	public void initQuery() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", acceptData.getData("MR_NO"));
		parm.setData("IPD_NO", acceptData.getData("IPD_NO"));
		
		// ��ѯ����סԺ��Ϣ
		TParm result = ADMInpTool.getInstance().selectall(parm);
		initParm.setRowData(result);
		// ��ȡ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(acceptData.getValue("MR_NO"));
		setValue(
				"AGE",
				StringUtil.showAge(pat.getBirthday(),
						result.getTimestamp("IN_DATE", 0)));
		setValue("DEPT_CODE", result.getData("DEPT_CODE", 0));
		setValue("STATION_CODE", result.getData("STATION_CODE", 0));
		setValue("BED_NO", result.getData("BED_NO", 0));
		setValue("VS_DR_CODE", result.getData("VS_DR_CODE", 0));
		setValue("ATTEND_DR_CODE", result.getData("ATTEND_DR_CODE", 0));
		setValue("DIRECTOR_DR_CODE", result.getData("DIRECTOR_DR_CODE", 0));
		setValue("VS_NURSE_CODE", result.getData("VS_NURSE_CODE", 0));
		setValue("PATIENT_CONDITION", result.getData("PATIENT_CONDITION", 0));
		setValue("NURSING_CLASS", result.getData("NURSING_CLASS", 0));
		setValue("PATIENT_STATUS", result.getData("PATIENT_STATUS", 0));
		setValue("DIE_CONDITION", result.getData("DIE_CONDITION", 0));
		setValue("CARE_NUM", result.getData("CARE_NUM", 0));
		setValue("IO_MEASURE", result.getValue("IO_MEASURE", 0));
		setValue("ISOLATION", result.getValue("ISOLATION", 0));
		setValue("TOILET", result.getValue("TOILET", 0));
		if ("Y".equals(result.getValue("ALLERGY", 0)))
			setValue("ALLERGY_Y", "Y");
		//fux modify 20161011 ��������˵��
		setValue("ALLERGIC_MARK", result.getValue("ALLERGIC_MARK", 0));
		
	}

	/**
	 * �����¼�
	 */
	public void onSave() {
		if ("Y".equals(getValue("TRANBED_CHECK"))) {
			if ("Y".equals(acceptData.getData("BED_OCCU_FLG"))) {
				this.messageBox_("�˲����Ѱ���");
				return;
			}
		}
		TParm newParm = this
				.getParmForTag("MR_NO;IPD_NO;PAT_NAME;SPECIES_CODE;SEX_CODE;AGE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;DIRECTOR_DR_CODE;" +
						"VS_NURSE_CODE;PATIENT_CONDITION;NURSING_CLASS;PATIENT_STATUS;" +
						//fux modify 20161011 ���ӹ���˵��
						"DIE_CONDITION;CARE_NUM;IO_MEASURE;ISOLATION;TOILET;ALLERGIC_MARK");
        if (newParm.getValue("VS_DR_CODE").equals("")) {//add by wanglong 20140331
            this.messageBox("����ҽ������Ϊ��");
            return;
        }
		newParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		newParm.setData("BED", getValue("TRANBED_CHECK"));
		newParm.setData("BED_NO", getValue("BED_NO"));
		newParm.setData("TRAN_BED", getValue("TRAN_BED"));
		// =====liuf=====//
		newParm.setData("BED_NO_DESC", ((TComboBox) getComponent("BED_NO"))
				.getComboEditor().getText());
		// =====liuf========//
		if (this.getValueBoolean("ALLERGY_Y")) {
			newParm.setData("ALLERGY", "Y");
		} else {
			newParm.setData("ALLERGY", "N");
		}
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		TParm DATA = new TParm();
		DATA.setData("OLD_DATA", initParm.getData());
		DATA.setData("NEW_DATA", newParm.getData());
		// System.out.println("data=====ת�����������==========="+DATA);
		// ===========pangben modify 20110617 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			DATA.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "changeDcBed", DATA); // ����
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ�ܣ���" + result.getErrName()); // ����������ʾ chenxi
			this.clearValue("TRANBED_CHECK;TRAN_BED");
		} else {
			this.messageBox("P0005");
			
			turnBedAction();//ת������
			
			// ===liuf ���͸�CISת������Ϣ===
			if ("Y".equals(getValue("TRANBED_CHECK"))) {
				sendMessage(newParm);
			}
			this.initQuery();
			this.clearValue("TRANBED_CHECK;TRAN_BED");
            TParm xmlParm = ADMXMLTool.getInstance().creatXMLFile(acceptData.getValue("CASE_NO"));
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            if ("Y".equals(newParm.getData("BED"))) {
                // ���Խӿ� wanglong add 20140731
                xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(acceptData.getValue("CASE_NO"), "A02");
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
            }
            // �������ӿ� wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(acceptData.getValue("CASE_NO"));
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
		}
	}
	/**
	 * ת���¼�
	 */
	public void turnBedAction(){
		if("Y".equals(getValue("TRANBED_CHECK"))){
			String oldBedNo=acceptData.getValue("BED_NO");
			String sql="UPDATE SYS_BED SET PRE_FLG='N',PRETREAT_OUT_NO='' WHERE BED_NO='"+oldBedNo+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			sql="UPDATE SYS_BED SET PRE_FLG='Y', PRETREAT_OUT_NO='"+acceptData.getValue("PRETREAT_OUT_NO")+"' WHERE BED_NO_DESC = '"+this.getValue("TRAN_BED")+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
		}
	}
	/**
	 * ��ѡת��
	 */
	public void onTRANBED_CHECK() {
		if ("Y".equals(getValue("TRANBED_CHECK"))) {
			TParm sendParm = new TParm();
			if (getValue("DEPT_CODE") == null
					|| "".equals(getValue("DEPT_CODE"))) {
				return;
			}
			if (getValue("STATION_CODE") == null
					|| "".equals(getValue("STATION_CODE"))) {
				return;
			}
			sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
			sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
			TParm reParm = (TParm) this.openDialog(
					"%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
			if (reParm != null) {
				this.setValue("TRAN_BED", reParm.getValue("BED_NO", 0));
			} else {
				this.clearValue("TRANBED_CHECK;TRAN_BED");
			}
		} else {
			this.clearValue("TRANBED_CHECK;TRAN_BED");
		}
	}

	/**
	 * ����CIS��Ѫ��ת����Ϣ
	 * 
	 * @param parm
	 */
	public void sendMessage(TParm parm) {
		// System.out.println("sendCISMessage()");
		// ICU��CCUע��
		String caseNO = parm.getValue("CASE_NO");
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		// ת��
		String type = "ADM_TRAN_BED";
		parm.setData("ADM_TYPE", "I");
		// CIS
		if (IsICU || IsCCU) {
			List list = new ArrayList();
			parm.setData("SEND_COMP", "CIS");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
					list, type);
			if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		////////////////////////////////////////////zhangs add start
		//��Һ��
		System.out.println("��Һ��:"+this.checkIsCS5(caseNO));
		if (this.checkIsCS5(caseNO))
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CS5");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		///////////////////////////////////////////////zhangs add end
		// Ѫ��
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			messageBox(resultParm.getErrText());
		// add by wangb 2017/3/24 ҽԺͬʱ����ŵ�ߺ�ǿ������Ѫ�ǳ��̽ӿڣ�һ����Ϣͬʱ���������̷���
		// ��ǿ��Ѫ�ǽӿڷ�����Ϣ START
		list = new ArrayList();
		parm.setData("SEND_COMP", "JNJ");
		list.add(parm);
		resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
		// ��ǿ��Ѫ�ǽӿڷ�����Ϣ END
		
		//add by lij 2017/04/13 ���� NIS��HL7��Ϣ
		list = new ArrayList();
		parm.setData("SEND_COMP", "NIS");
		list.add(parm);
		resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
	}

	public void onIdentificationPic() {
//		final String Front = "_IDFront.jpg";
//		final String back = "_IDBack.jpg";
//		String mrno = acceptData.getData("MR_NO").toString();
//		String imageFront = getFileServerPath(mrno, Front);
//		String imageback = getFileServerPath(mrno, back);
//		Image imageTemp = getFileServer(imageFront);
//		if (imageTemp == null) {
//			this.messageBox_("�û��ߵ����֤δ����,����ϵסԺ������");
//			return;
//		}
//		new PictureShow(imageFront, imageback).setVisible(true);

		TParm parm = new TParm();
		parm.setData("MR_NO", acceptData.getData("MR_NO").toString().trim());
		this.openDialog("%ROOT%\\config\\adm\\ADMPictureShow.x", parm);
	}

	/**
	 * ����ļ�������photo·��
	 * 
	 * @param mrNo
	 *            String
	 */
	public String getFileServerPath(String mrNo, String side) {
		String photoName = mrNo + side;
		String fileName = photoName;
		try {
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			return (dir + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * ����ļ�������photo
	 * 
	 * @param Path
	 *            String
	 */
	public Image getFileServer(String Path) {
		try {
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					Path);
			if (data == null)
				return null;
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
//////////////////////////////////////////////zhangs add start
	/**
	 * �Ƿ���CS5
	 * @param parm
	 * @return
	 */
	public boolean checkIsCS5(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
        boolean cs5Flg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query(inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
        cs5Flg=result.getBoolean("CS5_FLG",0);
		return cs5Flg;
	}


	private TParm query(TParm inparm) {
		String Sql =" SELECT B.CS5_FLG "+
		" FROM ADM_INP A,SYS_DEPT B "+
		" WHERE A.CASE_NO='"+inparm.getValue("CASE_NO")+"' "+
		" AND B.DEPT_CODE=A.DEPT_CODE ";
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("CS5_FLG") < 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}
//////////////////////////////////////////////////////zhangs add end
}
