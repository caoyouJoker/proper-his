package com.javahis.ui.med;

import java.sql.Timestamp;

import javax.swing.AbstractButton;

import com.dongyang.control.TControl;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ���������Ŀ����
 * </p>
 * 
 * <p>
 * Description: ���������Ŀ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author yanmm 2017/6/27
 * @version 1.0
 */
public class MEDGeneTestWordControl extends TControl {
	private static TTable mainTable;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		mainTable = (TTable) getComponent("TABLE");
		super.onInit();
		initUI();
	}

	public void initUI() {
		setValue("Q_DATE", "");
		setValue("DEPT_CODE", "");
		onTimeQ();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		mainTable.removeRowAll();// �������
		String qTime = this.getText("Q_DATE").replaceAll("/", "");
		String mrNo = getValueString("MR_NO");
		String deptCode = getValueString("DEPT_CODE");
		String sql1 = "";

		// ������
		if (!"".equals(mrNo)) {
			sql1 += " AND A.MR_NO = '" + mrNo + "' ";
		}
		// ʱ��
		if (this.getCheckBox("TIME_Q").isSelected()) {
			qTime = qTime.substring(0, 6).replaceAll("/", "").trim();
			sql1 += " AND  TO_CHAR (A.ORDER_DATE,'YYYYMM')= '" + qTime + "' ";
		}

		if (getValue("DEPT_CODE") != null) {
			if (getValue("DEPT_CODE").toString().length() != 0)
				sql1 += " AND A.DEPT_CODE = '" + deptCode + "'  ";
		}
		// ����,100;�����,150;������,150;���ܴ��,100;��������,80,ADM_TYPE;��Ŀ���ʱ��,150;��Ŀ����,200;�Ƿ��ѽɷ�,100,BILL_FLG
		// PAT_NAME;CASE_NO;MR_NO;USER_NAME;ADM_TYPE;ORDER_DATE;ORDER_DESC;BILL_FLG
		String sql = "SELECT A.PAT_NAME,A.CASE_NO,A.MR_NO,C.USER_NAME,A.ADM_TYPE,"
				+ "A.ORDER_DATE,A.ORDER_DESC,A.BILL_FLG "
				+ "FROM MED_APPLY A, SYS_FEE D, SYS_OPERATOR C ,OPD_ORDER E "
				+ "WHERE A.ORDER_CODE = D.ORDER_CODE "
				+ " AND D.TRANS_OUT_FLG = 'Y' "
				+ " AND D.TRANS_HOSP_CODE = '000519' "
				+ " AND D.RPTTYPE_CODE = 'Y1011' "
				+ " AND A.ORDER_DR_CODE = C.USER_ID "
				+ " AND A.ORDER_CODE = E.ORDER_CODE "
				+ " AND A.APPLICATION_NO  = E.MED_APPLY_NO "
				+ " AND A.CASE_NO = E.CASE_NO "
				+ " AND A.STATUS NOT IN ('5','9') "
				+ sql1
				+ " union "
				+ " SELECT A.PAT_NAME,A.CASE_NO,A.MR_NO,C.USER_NAME,A.ADM_TYPE,"
				+ "A.ORDER_DATE,A.ORDER_DESC,A.BILL_FLG "
				+ "FROM MED_APPLY A,SYS_FEE D,SYS_OPERATOR C,ODI_ORDER E  "
				+ "WHERE   A.ORDER_CODE = D.ORDER_CODE AND D.TRANS_OUT_FLG = 'Y' "
				+ "AND D.TRANS_HOSP_CODE = '000519' AND D.RPTTYPE_CODE = 'Y1011' "
				+ "AND A.ORDER_DR_CODE = C.USER_ID AND A.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.APPLICATION_NO = E.MED_APPLY_NO AND A.CASE_NO = E.CASE_NO "
				+ "AND A.STATUS NOT IN ('5', '9') " + sql1;
		// System.out.println("sql:::" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		for(int i=0;i<resultParm.getCount();i++){
			String admType = resultParm.getValue("ADM_TYPE",i);
			if(admType.equals("I")){
				resultParm.setData("BILL_FLG",i, "Y");
			}
		}
		// System.out.println("parm:"+resultParm);
		if (resultParm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}

		mainTable.setParmValue(resultParm);
	}

	/**
	 * �����Żس�
	 */
	public void onQueryMrno() {
		String mrNo = PatTool.getInstance().checkMrno(
				this.getValueString("MR_NO"));
		setValue("MR_NO", mrNo);
		onQuery();
	}

	/**
	 * getCheckBox
	 * 
	 * @param string
	 * @return
	 */
	private AbstractButton getCheckBox(String string) {
		return (TCheckBox) getComponent(string);
	}

	/**
	 * ʱ���ѯ�ؼ�
	 */
	public void onTimeQ() {
		if (getValueString("TIME_Q").equals("Y")) {
			((TTextFormat) getComponent("Q_DATE")).setEnabled(true);
			setValue("Q_DATE", SystemTool.getInstance().getDate());
		} else {
			((TTextFormat) getComponent("Q_DATE")).setEnabled(false);
			setValue("Q_DATE", "");
		}
	}

	/**
	 * ����Excel���
	 */
	public void onExcel() {
		TTable mainTable = getTable("TABLE");
		if (mainTable.getRowCount() <= 0) {
			messageBox("�޵�������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(mainTable, "���������Ŀ����");
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.clearValue("Q_TIME;MR_NO;DEPT_CODE;Time_Q");
		getTable("TABLE").removeRowAll();
		onInit();
	}

	/**
	 * ȡ��Table�ؼ�
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}

}
