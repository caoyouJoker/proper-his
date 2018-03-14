package com.javahis.ui.odi;


import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;

import psyg.graphic.SysObj;




/**
 * Title: ����ѡ��
 * Description:����ѡ��
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class ODITransfertypeControl extends TControl {
	private TParm parm;
	private String mrNo;//������	
	private String caseNo;//�����
	private String patName;//����
	private String fromDept;//����
	private String deptTypeFlg; //���ڿ���ѡ�������ʾ���ұ��
	private TComboBox combobox;
	private String dayOpeCode;//�ռ��������
	
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
		dayOpeCode = parm.getValue("DAY_OPE_CODE");
//		System.out.println("---deptTypeFlg----------------"+deptTypeFlg);
		  //��������
		  combobox = (TComboBox) this.getComponent("DEPT_TYPE");
		if (deptTypeFlg.equals("ODI")) {
			combobox.setStringData("[[id,text],[,],[0306,������������],[030503,������]]");
		} else if (deptTypeFlg.equals("OPE")) {
			String toDept = parm.getValue("TO_DEPT");
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT"
					+ " WHERE DEPT_CODE = '" + toDept + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			String toDeptname = result.getValue("DEPT_CHN_DESC", 0);
			combobox.setStringData("[[id,text],[,],[0304,CCU],[" + toDept + ","
					+ toDeptname + "]]");
		} else if (deptTypeFlg.equals("ERD")) {
			String toDept = parm.getValue("TO_DEPT");
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT"
					+ " WHERE DEPT_CODE = '" + toDept + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			String toDeptname = result.getValue("DEPT_CHN_DESC", 0);
			combobox.setStringData("[[id,text],[,],[" + toDept + ","
					+ toDeptname + "],[0306,������������],[030503,������]]");
		}
    }
    /**
     * ���ɽ��ӵ�
     */
    public void onCreate() {
    	TParm action = new TParm();
    	if(this.getValue("DEPT_TYPE").equals("")){
    		this.messageBox("ת����Ҳ���Ϊ��");
    		return;
    	}
    	if(this.getValue("DEPT_TYPE")==null){
    		this.messageBox("ת����Ҳ���Ϊ��");
    		return;
    	}
		if (deptTypeFlg.equals("ODI")) {
			// ����������
			if (this.getValue("DEPT_TYPE").equals("0306")) {
				TParm recptype = new TParm();
				recptype.setData("CASE_NO", caseNo);
				recptype.setData("TYPE_CODE", "2");
				TParm result = (TParm) this.openDialog("%ROOT%\\config\\ope\\OPEOpDetailList.x", recptype);
				
				// ��ѯģ����Ϣ
				TParm actionParm = new TParm();
				action.setData("MR_NO", mrNo);// ������
				action.setData("CASE_NO", caseNo);// �����
				action.setData("PAT_NAME", patName);// ����
				action.setData("FROM_DEPT", fromDept); // ת������
				action.setData("TO_DEPT", getValue("DEPT_TYPE")); // ת�����(����)
				action.setData("TRANSFER_CLASS", "WT"); // ��������(����-���� WT)
				// ��ѯģ����Ϣ
				actionParm = this.getEmrFilePath("EMR0603022");
				action.setData("TEMPLET_PATH", actionParm.getValue(
						"TEMPLET_PATH", 0));// ���ӵ�·��
				action.setData("EMT_FILENAME", actionParm.getValue(
						"EMT_FILENAME", 0));// ���ӵ�����
				action.setData("FLG", false);// ��ģ��
				action.setData("DAY_OPE_CODE",this.dayOpeCode);
				action.setData("OPBOOK_SEQ", result.getValue("OPBOOK_SEQ"));// �������뵥��
				String optChnDescSql = "SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD A WHERE A.OPERATION_ICD = '"+result.getValue("OP_CODE1")+"'";
				TParm optChnDescParm = new TParm(TJDODBTool.getInstance().select(optChnDescSql)); 
				action.setData("OPT_CHN_DESC", optChnDescParm.getValue("OPT_CHN_DESC",0));// ��ʽ
				//20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
		        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "�ռ�����":"");//�ռ��������
				// System.out.println("---action----------------"+action);
				// ����ģ��
				TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
				if (action != null)
					frame.setParameter(action);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
			}
			// ������������(�ǲ������ӵ�)
			else {
				// �õ���������
				TParm Data = (TParm) parm.getData("PARM");
				// System.out.println("---Data----------------"+Data);
				// ���ó���
				this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x",
						Data);
			}
		} else if (deptTypeFlg.equals("OPE")) {
			// ��ѯģ����Ϣ
			TParm actionParm = new TParm();
			action.setData("MR_NO", mrNo);// ������
			action.setData("CASE_NO", caseNo);// �����
			action.setData("PAT_NAME", patName);// ����
			action.setData("FROM_DEPT", fromDept); // ת������
			action.setData("TO_DEPT", getValue("DEPT_TYPE")); // ת�����(CCU����)
			action.setData("TRANSFER_CLASS", "TC/TW"); // ��������(����-CCU/���� TC/TW)
			// action.setData("OP_CODE",parm.getValue("OP_CODE")); //��ʽ
			// ��ѯģ����Ϣ
			actionParm = this.getEmrFilePath("EMR0603033");
			action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",
					0));// ���ӵ�·��
			action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",
					0));// ���ӵ�����
			action.setData("FLG", false);// ��ģ��
			action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// �������뵥��
			action.setData("DAY_OPE_CODE",this.dayOpeCode);
			// System.out.println("---action----------------"+action);
			
			//20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
	        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "�ռ�����":"");//�ռ��������

	        
			// ����ģ��
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
				frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
		}
		// ����-����/������/������
		else if (deptTypeFlg.equals("ERD")) {
			// ��ѯģ����Ϣ
			TParm actionParm = new TParm();
			action.setData("MR_NO", mrNo);// ������
			action.setData("CASE_NO", caseNo);// �����(סԺ)
			action.setData("REG_CASE_NO", parm.getValue("REG_CASE_NO"));// �����(�ż���)
			action.setData("PAT_NAME", patName);// ����
			action.setData("FROM_DEPT", fromDept); // ת������
			action.setData("TO_DEPT", getValue("DEPT_TYPE")); // ת�����
			String transferClass = "";
			if (StringUtils.equals("0306", getValueString("DEPT_TYPE"))) {
				// ����-����
				transferClass = "ET";
			} else if (StringUtils.equals("030503", getValueString("DEPT_TYPE"))) {
				// ����-������
				transferClass = "EO";
			} else {
				// ����-����
				transferClass = "EW";
			}
			action.setData("TRANSFER_CLASS", transferClass); // ��������
			// ��ѯģ����Ϣ
			actionParm = this.getEmrFilePath("EMR06030601");
			action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",
					0));// ���ӵ�·��
			action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",
					0));// ���ӵ�����
			action.setData("FLG", false);// ��ģ��
			action.setData("DAY_OPE_CODE",this.dayOpeCode);
			action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// �������뵥��
			//20170328 zhanglei Ϊ�ṹ�������������������ǰ���ӵ� ��ʵ�ռ��������
	        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "�ռ�����":"");//�ռ��������
			// ����ģ��
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
				frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
		}
    	
    	this.closeWindow();		
	    
    	
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
