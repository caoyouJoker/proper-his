package com.javahis.ui.ope;


import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;




/**
 * Title: ����ѡ��
 * Description:����ѡ��
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class OPETransfertypeControl extends TControl {
	private TParm parm;
	private String mrNo;//������	
	private String caseNo;//�����
	private String patName;//����
	private String fromDept;//����
	private String deptTypeFlg; //���ڿ���ѡ�������ʾ���ұ��	

	private String dayopeflg;//�ռ�������� zhanglei 20170329

	public void onInit() {
		super.onInit();
		parm = (TParm) getParameter();//��ѯ��Ϣ
		//���޴���Ϣ����
		if (null == parm) {
			return;
		}
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		patName = parm.getValue("PAT_NAME");
		fromDept = parm.getValue("FROM_DEPT");
		deptTypeFlg = parm.getValue("DEPT_TYPE_FLG");//���ڿ���ѡ�������ʾ���ұ��
		//		System.out.println("---deptTypeFlg----------------"+deptTypeFlg);
		dayopeflg = parm.getValue("DAY_OPE_FLG");//�ռ�������� zhanglei 20170329	
	}
	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate() {
		/** modified by WangQing 20170411 -start*/
		TParm action = new TParm();
		if(this.getValueString("DEPT_TYPE").trim().equals("") || this.getValue("DEPT_TYPE")==null){
			this.messageBox("ת����Ҳ���Ϊ��");
			return;
		}
		/*modified by Eric 20170518 */
		TParm actionParm = new TParm();// ����ģ��
		if(fromDept.equals("030503")){// ������
			if(getValue("DEPT_TYPE").equals("0303")){// ICU
				actionParm = this.getEmrFilePath("EMR0603055");
				action.setData("TRANSFER_CLASS", "OI"); // ��������
			}else{// CCU/����
				actionParm = this.getEmrFilePath("EMR0603088");
				action.setData("TRANSFER_CLASS", "OC/OW"); // ��������
			}		
		}else{// ������
			actionParm = this.getEmrFilePath("EMR0603033");
			action.setData("TRANSFER_CLASS", "TC/TW"); // ��������
		}
		
		
		action.setData("MR_NO", mrNo);// ������
		action.setData("CASE_NO", caseNo);// �����
		action.setData("PAT_NAME", patName);// ����
		action.setData("FROM_DEPT", fromDept); // ת������
		action.setData("TO_DEPT", getValue("DEPT_TYPE")); // ת�����
		// action.setData("OP_CODE",parm.getValue("OP_CODE")); //��ʽ
		action.setData("DAY_OPE_FLG", dayopeflg);//�ռ�������� zhanglei 20170329
		
		/*modified by Eric 20170518*/
//		action.setData("TEMPLET_PATH", parm.getValue("TEMPLET_PATH"));// ���ӵ�·��
//		action.setData("EMT_FILENAME", parm.getValue("EMT_FILENAME"));// ���ӵ�����
		action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",0));// ���ӵ�·��
		action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",0));// ���ӵ�����
		
		
		action.setData("FLG", false);// ��ģ��
		action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// �������뵥��
//		action.setData("TRANSFER_CLASS", parm.getValue("TRANSFER_CLASS")); // ��������
		// ����ģ��
		this.openWindow("%ROOT%\\config\\emr\\EMRTransferWordUI.x", action);
		this.closeWindow();		
		/** modified by WangQing 20170411 -end*/
	}

	/**
	 * �õ�EMR·��
	 */
	public TParm getEmrFilePath(String subclassCode){
		String sql=" SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE," +
				" A.TEMPLET_PATH FROM EMR_TEMPLET A"+
				" WHERE A.SUBCLASS_CODE = '"+subclassCode+ "'";
		TParm result = new TParm();
		result = new TParm(TJDODBTool.getInstance().select(sql)); 
		//    	System.out.println("---result----------------getEmrFilePath"+result);
		return result;
	}
	
}
