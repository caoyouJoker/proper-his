package com.javahis.ui.odi;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.ui.reg.REGCTTriageControl;

/**
 * ���ﻼ����Ϣ
 * @author WangQing 20170316
 *
 */
public class ODIPatInfoControl extends TControl {
	private TTable table ;
	public static SimpleDateFormat dt = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
	/**
	 * ϵͳ����
	 */
	TParm parm;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLEPAT");
		// ��table��˫���¼�
		callFunction("UI|TABLEPAT|addEventListener",
				"TABLEPAT->" + TTableEvent.CLICKED, this, "onTableClicked");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			parm = (TParm) o;
		}
		System.out.println("------the parm for test="+parm);
		this.onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		// ���ɲ�ѯsql
		String sql = "";
		// CASE_NO ����ţ�MR_NO �����ţ�PAT_NAME ���������� SEX_CODE �Ա�BIRTH_DATE �������ڣ�CT_TRIAGE_NO CT���˺ţ�CT_LEVEL_CODE CT���˵ȼ�
		sql = "SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, C.TRIAGE_NO as CT_TRIAGE_NO, C.LEVEL_CODE as CT_LEVEL_CODE, B.SEX_CODE, B.BIRTH_DATE ,A.ENTER_ROUTE "
				+ "FROM REG_PATADM A, SYS_PATINFO B , ERD_EVALUTION C,AMI_CT_RECORD D, OPD_ORDER E "
				+ "WHERE A.MR_NO=B.MR_NO(+)  "
				+ "AND A.CASE_NO=C.CASE_NO(+) "
				+ " AND A.CASE_NO=D.CASE_NO(+) "
				+ "AND A.CASE_NO=E.CASE_NO(+) "
//				+ "AND ORDER_CODE LIKE '"+REGCTTriageControl.CT_ORDER_CODE+"%' "
				+ "AND A.ENTER_ROUTE='E02' " //��ʹ���Ĳ���

				+ " AND A.CASE_NO='"+parm.getValue("CASE_NO", 0)+"' "
				+ " AND A.MR_NO='"+parm.getValue("MR_NO", 0)+"' "
				+ " AND C.TRIAGE_NO='"+parm.getValue("TRIAGE_NO", 0)+"' "
//				+ "AND D.PAT_ARRIVE_TIME IS NULL " //������δ����
				+ "ORDER BY A.CASE_NO";
		sql = "SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, C.TRIAGE_NO as CT_TRIAGE_NO, C.LEVEL_CODE as CT_LEVEL_CODE, B.SEX_CODE, B.BIRTH_DATE ,A.ENTER_ROUTE "
				+ "FROM REG_PATADM A, SYS_PATINFO B , ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO(+)  "
				+ "AND A.CASE_NO=C.CASE_NO(+) "
				+ "AND A.ENTER_ROUTE='E02' " //��ʹ���Ĳ���
				+ " AND A.CASE_NO='"+parm.getValue("CASE_NO", 0)+"' "
				+ " AND A.MR_NO='"+parm.getValue("MR_NO", 0)+"' "
				+ " AND C.TRIAGE_NO='"+parm.getValue("TRIAGE_NO", 0)+"' "
				+ "ORDER BY A.CASE_NO";
		System.out.println("===sql:::"+sql);
		// ִ�в�ѯ
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
				System.out.println("======tableParm:::"+tableParm);
		// CASE_NO �����;PAT_NAME ��������;CT_TRIAGE_NO CT���˺�;CT_LEVEL_CODE CT���˵ȼ�
		table.setParmValue(tableParm);
	}

	/**
	 * TABLE�����¼�
	 * @param row int
	 */
	public void onTableClicked(int row){
		TParm parm = table.getParmValue().getRow(row);
		parm.setData("CLICK_TIME", new Timestamp(new Date().getTime()));
		this.setReturnValue(parm);
	}

}
