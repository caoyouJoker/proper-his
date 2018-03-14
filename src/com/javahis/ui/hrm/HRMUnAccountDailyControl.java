package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMUnAccountDailyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title: 健检对账查询
 * </p>
 * 
 * <p>
 * Description:TODO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class HRMUnAccountDailyControl extends TControl {
	TTable table;

	public void onInit() {
		Timestamp today = SystemTool.getInstance().getDate();
		String startDate = today.toString();
		startDate = startDate.substring(0, 4) + "/" + startDate.substring(5, 7)
				+ "/" + startDate.substring(8, 10) + " 00:00:00";
		setValue("START_DATE", startDate);
		setValue("END_DATE", today);
		setValue("REGION_CODE", Operator.getRegion());
		setValue("CASH_CODE", Operator.getID());
		setValue("ADM_TYPE", "H");
		this.table = ((TTable) getComponent("TABLE"));
	}

	public void onQuery() {
		String startDate = getValueString("START_DATE")
				+ getValueString("S_TIME");
		String endDate = getValueString("END_DATE") + getValueString("E_TIME");
		if ((startDate.equals("")) || (endDate.equals(""))) {
			messageBox("请选择时间段");
			return;
		}
		startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
				+ startDate.substring(8, 10) + startDate.substring(11, 13)
				+ startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
				+ endDate.substring(8, 10) + endDate.substring(11, 13)
				+ endDate.substring(14, 16) + endDate.substring(17, 19);

		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("REGION_CODE", getValueString("REGION_CODE"));
		parm.setData("CASHIER_CODE", getValueString("CASH_CODE"));
		parm.setData("ADM_TYPE", "H");
		TParm result = HRMUnAccountDailyTool.getInstance().onQuery(parm);

		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("查无数据");
			return;
		}
		TParm returnParm = getTableParm(result);
		this.table.setParmValue(returnParm);
	}

	public void onClear() {
		this.table.setParmValue(new TParm());
		setValue("REGION_CODE", Operator.getRegion());
		setValue("CASH_CODE", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String startDate = today.toString();
		startDate = startDate.substring(0, 4) + "/" + startDate.substring(5, 7)
				+ "/" + startDate.substring(8, 10) + " 00:00:00";
		setValue("START_DATE", startDate);
		setValue("END_DATE", today);
		setValue("ADM_TYPE", "O");
	}

	public void onExport() {
		if (this.table.getRowCount() <= 0) {
			messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(this.table, "健检日结对账表");
	}

	public TParm getTableParm(TParm parm) {
		double ar_amt_tot = 0.0D;
		double cash_tot = 0.0D;
		double bank_tot = 0.0D;
		double check_tot = 0.0D;
		double ekt_tot = 0.0D;
		double ins_tot = 0.0D;
		double other_tot = 0.0D;
		double debit_tot = 0.0D;
		double draft_tot = 0.0D;
		for (int i = 0; i < parm.getCount(); i++) {
			ar_amt_tot += parm.getDouble("AR_AMT", i);
			cash_tot += parm.getDouble("PAY_CASH", i);
			bank_tot += parm.getDouble("PAY_BANK_CARD", i);
			check_tot += parm.getDouble("PAY_CHECK", i);
			ekt_tot += parm.getDouble("PAY_MEDICAL_CARD", i);
			ins_tot += parm.getDouble("PAY_INS_CARD", i);
			other_tot += parm.getDouble("OTHER_FEE1", i);
			debit_tot += parm.getDouble("PAY_DEBIT", i);
			draft_tot += parm.getDouble("PAY_DRAFT", i);
		}

		parm.addData("USER_NAME", "总计");
		parm.addData("PRINT_NO", "");
		parm.addData("AR_AMT", Double.valueOf(ar_amt_tot));
		parm.addData("PAY_CASH", Double.valueOf(cash_tot));
		if (bank_tot > 0.0D)
			parm.addData("PAY_BANK_CARD", Double.valueOf(bank_tot));
		else {
			parm.addData("PAY_BANK_CARD", "");
		}
		if (check_tot > 0.0D)
			parm.addData("PAY_CHECK", Double.valueOf(check_tot));
		else {
			parm.addData("PAY_CHECK", "");
		}
		parm.addData("PAY_MEDICAL_CARD", Double.valueOf(ekt_tot));
		parm.addData("PAY_INS_CARD", Double.valueOf(ins_tot));
		parm.addData("OTHER_FEE1", Double.valueOf(other_tot));
		parm.addData("PAY_DEBIT", Double.valueOf(debit_tot));
		parm.addData("PAY_DRAFT", Double.valueOf(draft_tot));
		parm.addData("PRINT_DATE", "");
		parm.addData("ACCOUNT_SEQ", "");
		parm.addData("ACCOUNT_DATE", "");
		parm.addData("ACCOUNT_USER", "");
		return parm;
	}
}
