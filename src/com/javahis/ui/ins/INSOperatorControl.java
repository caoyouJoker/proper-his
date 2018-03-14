package com.javahis.ui.ins;

import jdo.ope.OPEOpDetailTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title:单病种费用分割中病案首页之手术资料
 * </p>
 * 
 * <p>
 * Description:单病种费用分割中病案首页之手术资料
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-02-12
 * @version 2.0
 */
public class INSOperatorControl extends TControl {
	private TTable table;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initParm();
	}

	/**
	 * 初始化数据
	 */
	private void initParm() {
		// 得到前台传来的数据并显示在界面上
		TParm recptype = (TParm) getParameter();
		setValueForParm("MR_NO;CASE_NO;PAT_NAME", recptype, -1);
		table = (TTable) this.getComponent("TABLE");// 表格
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("CASE_NO", this.getValue("CASE_NO"));
		String sql = "SELECT OP_DATE,OP_RECORD_NO,"
				+ "OP_DEPT_CODE, OP_STATION_CODE, BED_NO,"
				+ "TYPE_CODE, RANK_CODE, WAY_CODE,"
				+ "ANA_CODE, DIAG_CODE1, "
				+ " OP_CODE1,"
				+ "MAIN_SURGEON, REAL_AST1, CIRCULE_USER1,"
				+ "SCRUB_USER1, ANA_USER1,EXTRA_USER1,DRG_CODE,B.ICD_CHN_DESC " 
				+ "FROM OPE_OPDETAIL A ,SYS_DIAGNOSIS B WHERE DIAG_CODE1=ICD_CODE(+) ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			return;
		}
		table.setParmValue(result);
	}
}
