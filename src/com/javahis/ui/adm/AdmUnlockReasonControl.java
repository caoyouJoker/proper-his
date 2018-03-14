package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

import jdo.sys.Pat;
import jdo.adm.ADMInpTool;

/**
 * <p>
 * Title: 解锁记录
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author yanmm
 * @version 1.0
 */
public class AdmUnlockReasonControl extends TControl {
	public AdmUnlockReasonControl() {
	}

	TParm acceptData = new TParm(); // 接参
	Pat pat = new Pat();
	TParm initParm = new TParm(); // 初始数据
	private static TTable mainTable;

	public void onInit() {
		mainTable = (TTable) getComponent("TABLE");
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			this.initUI(acceptData);
		}
	}

	/**
	 * 界面初始化
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
		this.setValue("CASE_NO", acceptData.getData("CASE_NO"));
		// this.messageBox("记录初始化"+acceptData.getData("CASE_NO"));
		// this.setValue("STATION_CODE", acceptData.getData("STATION_CODE"));
		// this.setValue("DEPT_CODE", acceptData.getData("DEPT_CODE"));
		// parm.setData("ARREARAGE_AMT", this.getValueDouble("ARREARAGE_AMT"));
		this.initQuery();
	}

	/**
	 * 初始化查询
	 */
	public void initQuery() {
		// 原因类型,100;解锁人员,100;解锁时间,100;科室,100;病区,100;欠费金额,150;备注,200
		// CHN_DESC;USER_NAME;UNLOCK_DATE;DEPT_CHN_DESC;STATION_DESC;ARREARAGE_AMT;UNLOCK_CASE_TEXT
		String sql = "SELECT S.CHN_DESC,O.USER_NAME,A.UNLOCK_DATE,D.DEPT_CHN_DESC,N.STATION_DESC,A.ARREARAGE_AMT,A.UNLOCK_CASE_TEXT  "
				+ "FROM ADM_UNLOCK_CAUSE A, SYS_DICTIONARY S,SYS_OPERATOR O,SYS_DEPT D,SYS_STATION N "
				+ "WHERE S.ID = A.UNLOCK_CASE "
				+ "AND A.OPT_USER = O.USER_ID "
				+ "AND D.DEPT_CODE = '"
				+ acceptData.getData("DEPT_CODE")
				+ "' "
				+ "AND N.STATION_CODE = '"
				+ acceptData.getData("STATION_CODE")
				+ "'"
				+ "AND S.GROUP_ID = 'SYS_UNLOCK_CAUSE' "
				+ "AND A.CASE_NO = '"
				+ acceptData.getData("CASE_NO") + "' ";
	//	System.out.println("sql11111" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		//this.messageBox_(resultParm);
		mainTable.setParmValue(resultParm);
	}

}
