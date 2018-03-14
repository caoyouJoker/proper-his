package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.cxf.common.util.StringUtils;

import jdo.adm.ADMInpTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 * <p>Title: �������ʹ��ڿ�����</p>
 * 
 * <p>Description:�������ʹ��ڿ�����</p>
 * 
 * <p>Copyright: Copyright (c) 2016</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author wukai 2016.05.24
 * @version 1.0
 */
public class ADMPathologyControl extends TControl{
	TParm acceptData = new TParm(); // �Ӳ�
	TParm initParm = new TParm(); // ��ʼ����
	
	@Override
	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			this.initUI(acceptData);
		}
	}
	
	/**
	 * ���ô����Ĳ�����ʼ��ҳ��
	 * ��ADM_INP�в����Ӧ�Ĳ���Code�Ͳ���ע
	 * @param acceptData
	 */
	private void initUI(TParm parm) {
		this.initQuery();
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
		Object code = result.getData("PATLOGY_PRO_CODE",0);
		if(code == null) {  //û�в�ѯ���������,��½����
			setValue("PATLOGY_DEPT_CODE",Operator.getDept());   //�������
			setValue("PATLOGY_DOC_CODE",Operator.getID());    //����ҽ��
			setValue("PATLOGY_PRO_DATE",StringTool.getTimestamp(new Date()));   //����ʱ��
			return;
		}
		initParm.setRowData(result);
		// ��ȡ����������Ϣ
		setValue("PATLOGY_PRO_CODE",code);  //������Ŀ
		setValue("PATLOGY_DEPT_CODE",result.getData("PATLOGY_DEPT_CODE", 0));   //�������
		setValue("PATLOGY_PRO_REMARK",result.getData("PATLOGY_PRO_REMARK",0)); //��ע
		setValue("PATLOGY_DOC_CODE",result.getData("PATLOGY_DOC_CODE",0));    //����ҽ��
		setValue("PATLOGY_PRO_DATE",result.getData("PATLOGY_PRO_DATE",0));   //����ʱ��
		
	}
	
	/**
	 * ����...���Ǹ���ADM_INP  ����Ӧ�������ֶ�
	 * ������Ӧ���ֶ�
	 */
	public void onSave() {
		if(checkData()) {
			TParm parm = new TParm();
			Timestamp time = (Timestamp) getValue("PATLOGY_PRO_DATE");
			if(time != null) {
				time = StringTool.getTimestamp(time.toString().substring(0, 10) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			}
			parm.setData("MR_NO", acceptData.getData("MR_NO").toString());
			parm.setData("DEPT_CODE",acceptData.getData("DEPT_CODE").toString());
			parm.setData("STATION_CODE", acceptData.getData("STATION_CODE").toString());
			parm.setData("PATLOGY_PRO_CODE", getValue("PATLOGY_PRO_CODE").toString());
			parm.setData("PATLOGY_PRO_DATE", (time == null) ? "" : time);
			parm.setData("PATLOGY_PRO_REMARK", getText("PATLOGY_PRO_REMARK"));
			parm.setData("PATLOGY_DEPT_CODE",getValue("PATLOGY_DEPT_CODE").toString());
			parm.setData("PATLOGY_DOC_CODE", getValue("PATLOGY_DOC_CODE").toString());
			//this.messageBox(parm.toString());
			if(ADMInpTool.getInstance().updatePatPro(parm)) {
				this.messageBox("����ɹ�");
			} else {
				this.messageBox("����ʧ��");
			}
		}
	}
	
	/**
	 * ���Ԫ��
	 */
	public boolean checkData() {
		if(getValue("PATLOGY_PRO_CODE") == null || getText("PATLOGY_PRO_CODE").length() <= 0) {
			this.messageBox("�������Ŀ");
			return false; 
		}
		if(getValue("PATLOGY_PRO_DATE") == null || getText("PATLOGY_PRO_DATE").length() <= 0) {
			this.messageBox("���������ʱ��");
			return false;
		}
		return true;
	}
	/**
	 * ��������������Ŀ
	 */
	public void onClear() {
		String linkedNames="PATLOGY_PRO_CODE;PATLOGY_DEPT_CODE;PATLOGY_DOC_CODE;PATLOGY_PRO_DATE;PATLOGY_PRO_REMARK";
		this.clearValue(linkedNames);
		setValue("PATLOGY_PRO_DATE",StringTool.getTimestamp(new Date()));   //����ʱ�仹ԭ
	}
}
