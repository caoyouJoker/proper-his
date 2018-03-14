package com.javahis.ui.mro;

import java.util.Date;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p> Title:���������������Ѫ��ϸ����EXCLE </p>
 * 
 * <p> Description:���������������Ѫ��ϸ����EXCLE </p>
 * 
 * <p> Copyright: Copyright (c) 2011 </p>
 * 
 * <p> Company:bluecore </p>
 * 
 * @author sunqy 20140815
 * @version 2.0
 */

public class MROSurgeryPatBloodDetailControl extends TControl{
	
	private TTable table;//���
	
	public void onInit(){
		table = (TTable)getComponent("Table");
		timerInit();
		onQuery();
	}
	private void timerInit() {
		Date now = SystemTool.getInstance().getDate();
		this.setValue("DATE_S", now);
		this.setValue("DATE_E", now);
	}
	/**
	 * �����Żس���ѯ
	 */
	public void onMrno(){
        if(this.getValueString("MR_NO").trim().length()>0){
            String MR_NO = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            this.setValue("MR_NO",MR_NO);
        }
	}
//	/**
//	 * ��񵥻��¼�
//	 */
//	public void onTableClick(){
//		
//	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String sql = "SELECT A.OUT_DEPT, A.MR_NO, A.PAT_NAME, A.SEX, A.AGE, " +
				"A.OUT_DATE, D.ICD_CHN_DESC OUT_DIAG_CODE1, E.OPT_CHN_DESC OP_CODE, " +
				"A.MAIN_SUGEON, B.ANA_DR, A.RBC, A.PLASMA, A.PLATE, A.WHOLE_BLOOD, A.OTH_BLOOD " +
				"FROM MRO_RECORD A, MRO_RECORD_OP B, SYS_DEPT C, SYS_DIAGNOSIS D, SYS_OPERATIONICD E " +
				"WHERE 1=1 AND A.CASE_NO = B.CASE_NO(+) AND A.OUT_DEPT = C.DEPT_CODE " +
				"AND A.OUT_DIAG_CODE1 = D.ICD_CODE AND A.OP_CODE = E.OPERATION_ICD(+)";
		if(getValueString("MR_NO").length()>0){//����������
			onMrno();
			sql += " AND A.MR_NO = '" +this.getValueString("MR_NO")+ "'";
		}
		if(this.getValueString("DEPT_TYPE").length()>0){//�����������
			sql += " AND C.DEPT_CAT1 = '" +this.getValueString("DEPT_TYPE")+ "'";
		}
		if(this.getValueString("DEPT_CODE").length()>0){//��������
			sql += " AND A.OUT_DEPT = '" +this.getValueString("DEPT_CODE")+ "'";
		}
		if(this.getValueString("DATE_S").length()>0){//��ʼʱ������
			sql += " AND A.OUT_DATE > TO_DATE('" +this.getValueString("DATE_S").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("��ʼʱ�䲻��Ϊ��");
			this.grabFocus("DATE_S");
			return;
		}
		if(this.getValueString("DATE_E").length()>0){//����ʱ������
			sql += " AND A.OUT_DATE < TO_DATE('" +this.getValueString("DATE_E").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("����ʱ�䲻��Ϊ��");
			this.grabFocus("DATE_E");
			return;
		}
		sql += " ORDER BY A.MR_NO";
//		System.out.println("sql====:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			messageBox(result.getErrText());
			return;
		}
		if(result.getCount() <= 0){
			this.messageBox("û�в�ѯ������");
			return;
		}
		table.setParmValue(result);
//		callFunction("UI|MR_NO|setEnabled",false);//�������û�
//		callFunction("UI|DEPT_TYPE|setEnabled",false);//��������û�
//		callFunction("UI|DEPT_CODE|setEnabled",false);//�����û�
//		callFunction("UI|DATE_S|setEnabled",false);//��ʼʱ���û�
//		callFunction("UI|DATE_E|setEnabled",false);//����ʱ���û�
	}
	/**
	 * ���
	 */
	public void onClear(){
		callFunction("UI|MR_NO|setEnabled",true);//�����ſ���
		callFunction("UI|DEPT_TYPE|setEnabled",true);//����������
		callFunction("UI|DEPT_CODE|setEnabled",true);//���ҿ���
		callFunction("UI|DATE_S|setEnabled",true);//��ʼʱ�����
		callFunction("UI|DATE_E|setEnabled",true);//����ʱ�����
		this.clearValue("MR_NO;DEPT_TYPE;DEPT_CODE;DATE_S;DATE_E");
		table.removeRowAll();
//		onQuery();
//		timerInit();
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0){
			this.messageBox("û����Ҫ����������");
		}else{
			ExportExcelUtil.getInstance().exportExcel(table, "���������������Ѫ��ϸ��");
		}
	}
}
