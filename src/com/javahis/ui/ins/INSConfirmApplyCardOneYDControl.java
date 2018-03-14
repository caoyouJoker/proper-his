package com.javahis.ui.ins;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.Variant.ByrefHolder;
import org.jawin.win32.Ole32;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;

public class INSConfirmApplyCardOneYDControl extends TControl {
	private TParm readParm = new TParm();// ˢ������
	// private String case_no;// �����
	private String mr_no;// ��������
	TParm regionParm = null;// ���ҽ���������
	private String advancecode;//ҽԺ����@���÷���ʱ��@���
	DispatchPtr app = null;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		regionParm  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//���ҽ���������
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		mr_no=parm.getValue("MR_NO");
		callFunction("UI|INS_PAT_TYPE|setEnabled", false);// ������������
		onExeEnable(false);
		this.setValue("INS_PAT_TYPE", "");// 1.��ͨ2.����
		advancecode = parm.getValue("ADVANCE_CODE");//ҽԺ����@���÷���ʱ��@���
//		 System.out.println("advancecode===============:"+advancecode);
//		try {		
//		    if (app == null){
//		   	Ole32.CoInitialize();
//				app = new DispatchPtr("PB90.n_yhinterface");
//				    }		    
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
	}

	/**
	 * ˢ������
	 */
	public void readCard() {
		//��������ˢ������--------------------------------------------begin
		  String ref = new String();
		  ByrefHolder refh = new ByrefHolder("");
		  String data = advancecode;
		  try { 
			  if (app == null){
				  Ole32.CoInitialize();
				app = new DispatchPtr("PB90.n_yhinterface");
			}
				app.invoke("f_sblwsk",data,refh);
//				Ole32.CoUninitialize();
			} catch (COMException e) {
				e.printStackTrace();
			}
		 ref  = ""+ refh.getRef();
		 System.out.println("PB90.n_yhinterface========1=======:"+ref);
		  // �õ�ҽ����������
		 TParm sysparm = new TParm();	   
		   sysparm.setData("HOSP_AREA", "HIS");
			TParm sysParm = getTool().getSysParm(sysparm);
		 System.out.println("sysParm===============:"+sysParm);
			if (sysParm.getErrCode() < 0) {
				sysparm.setErr(-1, sysParm.getErrText(), sysParm.getErrName());
				return;
			}
			if (sysParm.getErrCode() > 0) {
				sysparm.setErr(-1, "INS_SYSPARM ����û���ҵ�Ĭ�ϵ�ҽ������!");
				return;
			}
			// Ĭ�ϵķָ��
			String separator = sysParm.getValue("SEPARATOR", 0);
			// Ĭ�ϵ��н�����
			String newline = initNewline(sysParm.getValue("NEWLINE", 0));
			// Ĭ�ϵĽ�����
			String finish = initNewline(sysParm.getValue("FINISH", 0));
			ref = ref.trim();
			if (ref.endsWith(finish)) {
				ref = ref.substring(0, ref.length() - finish.length());
			}
			if (!ref.endsWith(newline))
				ref += newline;
			String[] sData = parseNewLine(ref, newline, false);
			//�����ز�����Ϣ
			this.messageBox(ref);
		    String[] cData = parseNewLine(sData[0], separator, true);		
			readParm.setData("RETURN_TYPE", cData[0]);//����ִ��״̬
			readParm.setData("SID", cData[2]);//���֤��
			readParm.setData("PAT_NAME", cData[3]);//����
			readParm.setData("PAT_AGE", cData[4]);//����
			readParm.setData("SEX_CODE", cData[5]);//�Ա�
			readParm.setData("COMPANY_DESC", cData[6]);//��λ����	
			readParm.setData("PERSONAL_NO", cData[7]);//���˱���
			readParm.setData("CROWD_TYPE", cData[8]);//��Ⱥ���
			readParm.setData("CHECK_CODES", cData[9]);//ˢ����֤��			
			readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO",0));// ҽ���������
			readParm.setData("ADVANCE_CODE", advancecode);//ҽԺ����@סԺʱ��@���				
			readParm.setData("OPT_USER", Operator.getID());
			readParm.setData("OPT_TERM", Operator.getIP());
			readParm.setData("MR_NO", mr_no);//������
			readParm.setData("CARD_NO", "");//����			
			System.out.println("readParm===============:"+readParm);
//		��������ˢ������--------------------------------------------end
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// ��Ⱥ���
		this.setValue("INS_READ_TYPE", insReadType);//1.��ְ2.�Ǿ�3.���
		this.grabFocus("PASSWORD");
	}
	/**
	 * �������ģ��INSTool
	 *
	 * @alias �������ģ��INSTool
	 * @return INSTool
	 */
	public INSTool getTool() {
		return INSTool.getInstance();
	}
	/**
	 * ��ʼ�����з�
	 *
	 * @param s
	 *            String
	 * @return String
	 */
	public static String initNewline(String s) {
		if (s.startsWith("char(") && s.endsWith(")"))
			return ""
					+ ((char) Integer.parseInt(s.substring(5, s.length() - 1)));
		return s;
	}
	/**
	 * �����ݲ�ֳɶ���
	 *
	 * @param s
	 *            String
	 * @param newline
	 *            String
	 * @param b
	 *            boolean
	 * @return String[]
	 */
	private static String[] parseNewLine(String s, String newline, boolean  flg) {
		List list = new ArrayList();
		if (s.startsWith(newline))
		s = s.substring(newline.length(), s.length());
		int index = s.indexOf(newline);
		while (index >= 0) {
			list.add(s.substring(0, index));
			s = s.substring(index + newline.length(), s.length());
			index = s.indexOf(newline);
		}
		if (flg)
			list.add(s);
		return (String[]) list.toArray(new String[] {});
	}
	/**
	 * 
	 * ȷ����ť
	 */
	public void onOK() {

//		if (null == readParm || readParm.getErrCode() < 0
//				|| null == readParm.getValue("CARD_NO")
//				|| readParm.getValue("CARD_NO").length() <= 0) {
//			this.messageBox("��ִ�ж�������");
//			return;
//		}
//		if (!this.emptyTextCheck("PASSWORD")) {
//			return;
//		}
		readParm.setData("PASSWORD","111111");//����
		readParm.setData("RETURN_TYPE", 1);// ����ִ��״̬
		this.setReturnValue(readParm);
		this.closeWindow();
	}

	/**
	 * ��ѡ��ť�¼�
	 */
	public void onExeType() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			onExeEnable(false);
			this.grabFocus("READ_TEXT");
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			onExeEnable(true);
			this.grabFocus("IDNO");
		}
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT", "PASSWORD" };//���ó�ʼֵ
		for (int i = 0; i < name.length; i++) {
			this.setValue(name[i], "");
		}

	}

	/**
	 * ��ִ�в�������
	 * 
	 * @param flg
	 */
	private void onExeEnable(boolean flg) {
		callFunction("UI|IDNO|setEnabled", flg);// ���֤����
		callFunction("UI|PAT_NAME|setEnabled", flg);// ��������
		callFunction("UI|READ_TEXT|setEnabled", flg ? false : true);// ����
		//callFunction("UI|PASSWORD|setEnabled", flg ? false : true);// ����
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ͨ�����֤�����ò�����Ϣ
	 */
	public void onGetInfo() {
		TParm parm = new TParm();
		parm.setData("IDNO", this.getValue("IDNO"));// ���֤����
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			this.grabFocus("IDNO1");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("�����ڲ�����Ϣ");
			this.grabFocus("IDNO1");
			return;
		}
		// ���û�����ͨ��ѡ���ò�����Ϣ
		if (result.getCount() > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSPatInfo.x", parm);
			if (null == result || null == result.getValue("MR_NO")
					|| result.getValue("MR_NO").length() <= 0) {
				this.setValue("IDNO1", "");
				return;
			}
			this.setValue("MR_NO", result.getValue("MR_NO"));
			this.setValue("PAT_NAME1", result.getValue("PAT_NAME"));
			return;
		}
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME1", result.getValue("PAT_NAME", 0));
	}
}
