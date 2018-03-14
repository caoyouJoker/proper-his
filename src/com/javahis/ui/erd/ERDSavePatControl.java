package com.javahis.ui.erd;

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
public class ERDSavePatControl extends TControl {
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
		table = (TTable) this.getComponent("TABLE");
		// ��table��˫���¼�
		callFunction("UI|TABLE|addEventListener",
				"TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			parm = (TParm) o;
		}
		this.onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String sql = "";
		sql = " SELECT A.CASE_NO, A.MR_NO, A.PAT_NAME, A.TRIAGE_NO, B.REG_DATE "
				+ "FROM ERD_EVALUTION A, REG_PATADM B "
				+ "WHERE A.TRIAGE_NO=B.TRIAGE_NO AND A.MR_NO='"+parm.getValue("MR_NO")+"' ";
		System.out.println("===sql:::"+sql);
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
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
