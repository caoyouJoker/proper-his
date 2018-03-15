package com.javahis.ui.mro;

import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * 
 * <p> Title:�����������Ѫͳ�Ƶ���EXCLE </p>
 * 
 * <p> Description:�����������Ѫͳ�Ƶ���EXCLE </p>
 * 
 * <p> Copyright: Copyright (c) 2011 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author sunqy 20140903
 * @version 2.0
 */
public class MROOpeBloodControl extends TControl{
	
	private TTable table;//���
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		table = (TTable)getComponent("Table");
		timerInit();
	}
	/**
	 * ��ʼ��ʱ��ؼ�
	 */
	private void timerInit() {
		Date now = SystemTool.getInstance().getDate();
		this.setValue("DATE_S", now);
		this.setValue("DATE_E", now);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String sql = "SELECT M.OUT_DEPT,COUNT(M.OP_CODE) QTY,SUM(M.RBC) RBC, ROUND(AVG(M.RBC),2) AVG_RBC FROM ";
		String groupSql = "(SELECT A.OUT_DEPT, E.OPT_CHN_DESC OP_CODE, A.RBC " +
				"FROM MRO_RECORD A, SYS_DEPT C, SYS_OPERATIONICD E WHERE  A.OUT_DEPT = C.DEPT_CODE " +
				"AND A.OP_CODE = E.OPERATION_ICD(+)";//SYS_OPERATIONICD ��ʽ�ֵ��
		if(this.getValueString("DEPT_TYPE").length()>0){//�����������
			groupSql += " AND C.DEPT_CAT1 = '" +this.getValueString("DEPT_TYPE")+ "'";
		}
		if(this.getValueString("DEPT_CODE").length()>0){//��������
			groupSql += " AND A.OUT_DEPT = '" +this.getValueString("DEPT_CODE")+ "'";
		}
		if(this.getValueString("DATE_S").length()>0){//��ʼʱ������
			groupSql += " AND A.OUT_DATE > TO_DATE('" +this.getValueString("DATE_S").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("��ʼʱ�䲻��Ϊ��");
			this.grabFocus("DATE_S");
			return;
		}
		if(this.getValueString("DATE_E").length()>0){//����ʱ������
			groupSql += " AND A.OUT_DATE < TO_DATE('" +this.getValueString("DATE_E").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("����ʱ�䲻��Ϊ��");
			this.grabFocus("DATE_E");
			return;
		}
		groupSql += ") M";
		sql = sql + groupSql;
		sql += " GROUP BY M.OUT_DEPT, M.OP_CODE";
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
		
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		callFunction("UI|DEPT_TYPE|setEnabled",true);//����������
		callFunction("UI|DEPT_CODE|setEnabled",true);//���ҿ���
		callFunction("UI|DATE_S|setEnabled",true);//��ʼʱ�����
		callFunction("UI|DATE_E|setEnabled",true);//����ʱ�����
		this.clearValue("DEPT_TYPE;DEPT_CODE;DATE_S;DATE_E");
		table.removeRowAll();
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0){
			this.messageBox("û����Ҫ����������");
		}else{
			ExportExcelUtil.getInstance().exportExcel(table, "�����������Ѫͳ��");
		}
	}
}
