package com.javahis.ui.iva;

import jdo.iva.IVAAllocatecheckTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 静配中心成品核对Control
 * </p>
 * 
 * <p>
 * Description: 静配中心成品核对Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2013.07.28
 * @version 1.0
 */
public class IVAAllocatecheckControl extends TControl {
	// 得到table控件
	private TTable table_d;
	private TTable table_m;
	TTextFormat order_desc;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	// liuyalin 20170324 add
	public void onInit() {
		super.onInit();
		TTextFormat order_desc;
		order_desc = (TTextFormat) this.getComponent("IBS_ORDER_DESC");
		((TTextFormat) this.getComponent("IBS_ORDER_DESC")).setVisible(true);
		String deptSql = "SELECT ORDER_CODE,ORDER_DESC AS IBS_ORDER_DESC FROM SYS_FEE WHERE ORDER_DESC like '%集中配置'";
		order_desc.setPopupMenuSQL(deptSql);
		order_desc.setPopupMenuHeight(88);
		order_desc.setPopupMenuWidth(223);
	}

	/*
	 * 扫描瓶签查询调配药品信息
	 */
	public void onQuery() {
		// 得到TABLE_M控件

		table_m = this.getTTable("TABLE_D");
		table_m.removeRowAll();
		// 封装前台得到的值
		TParm parm = new TParm();
		// 得到前台控件值
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// liuyalin 20170328 add
		TParm resultC = new TParm();
		String barcode = this.getValueString("BAR_CODE");

		if (barcode != null && !"".equals(barcode)) {
			parm.setData("BAR_CODE", barcode);
			resultM = IVAAllocatecheckTool.getInstance().queryInfo(parm);
			if (!resultM.getValue("IVA_CHECK_USER", 0).equals("")
					&& resultM.getValue("IVA_CHECK_USER", 0) != null) {
				this.messageBox("此成品已核对！");
				this.clearValue("PAT_NAME;STATION_CODE;MR_NO;CASE_NO;BAR_CODE");
				((TTextField) this.getComponent("BAR_CODE")).requestFocus();
				return;
			}
		} else {
			messageBox("请输入查询信息！");
			return;
		}
		this.setValue("PAT_NAME", resultM.getValue("PAT_NAME", 0));
		this.setValue("STATION_CODE", resultM.getValue("STATION_DESC", 0));
		this.setValue("MR_NO", resultM.getValue("MR_NO", 0));
		this.setValue("CASE_NO", resultM.getValue("CASE_NO", 0));
		resultD.setData("CASE_NO", resultM.getValue("CASE_NO", 0));
		resultD.setData("BAR_CODE", parm.getValue("BAR_CODE"));

		TParm result = IVAAllocatecheckTool.getInstance().querydetail(resultD);
		if (result.getCount() <= 0) {
			this.messageBox("未查询到数据");
			return;
		}
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			result.setData("EXEC_DATE", i, result.getValue("EXEC_DATE", i)
					.substring(0, 4)
					+ "-"
					+ result.getValue("EXEC_DATE", i).substring(4, 6)
					+ "-"
					+ result.getValue("EXEC_DATE", i).substring(6, 8)
					+ " "
					+ result.getValue("EXEC_DATE", i).substring(8, 10)
					+ ":" + result.getValue("EXEC_DATE", i).substring(10, 12));
		}
		// liuyalin add 20170328
		resultC = IVAAllocatecheckTool.getInstance().querycheck(resultC);
		String order_code = "";
		TParm parmCheck = new TParm();
		for (int i = 0; i < resultC.getCount("ORDER_CODE"); i++) {
			order_code = resultC.getValue("ORDER_CODE", i);
			for (int j = 0; j < result.getCount("ORDER_CODE"); j++) {
				if (!order_code.equals(result.getValue("ORDER_CODE", j))) {
					String sql = "SELECT ORDER_DESC AS IBS_ORDER_DESC,OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE = 'D0300163'";
					parmCheck = new TParm(TJDODBTool.getInstance().select(sql));
					this.setValue("IBS_ORDER_DESC", parmCheck.getValue(
							"IBS_ORDER_DESC").replace("[", "").replace("]", "")
							.replace(" ", ""));
					this
							.setValue("OWN_PRICE", parmCheck.getValue(
									"OWN_PRICE").replace("[", "").replace("]",
									"").replace(" ", ""));
					table_m.setParmValue(result);
				} else {
					String sql = "SELECT ORDER_DESC AS IBS_ORDER_DESC,OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE = 'D0300161'";
					parmCheck = new TParm(TJDODBTool.getInstance().select(sql));
					this.setValue("IBS_ORDER_DESC", parmCheck.getValue(
							"IBS_ORDER_DESC").replace("[", "").replace("]", "")
							.replace(" ", ""));
					this
							.setValue("OWN_PRICE", parmCheck.getValue(
									"OWN_PRICE").replace("[", "").replace("]",
									"").replace(" ", ""));
					table_m.setParmValue(result);
					return;
				}
			}
		}
		table_m.setParmValue(result);
	}

	// liuyalin 20170324 add
	/*
	 * 配液费用随医嘱名称选择改变事件
	 */
	public void onChange() {
		TParm parm = new TParm();
		String sql = "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE= '"
				+ this.getValueString("IBS_ORDER_DESC") + "' ";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("OWN_PRICE", parm.getValue("OWN_PRICE").replace("[", "")
				.replace("]", "").replace(" ", ""));
	}

	/*
	 * 调配审核完之后保存
	 */
	public void onSave() {
		String check_user = this.getValueString("IVA_CHECK_USER");
		if (check_user.equals("")) {
			String type = "singleExe";
			TParm inParm = (TParm) this.openDialog(
					"%ROOT%\\config\\inw\\passWordCheck.x", type);
			String OK = inParm.getValue("RESULT");
			if (!OK.equals("OK")) {
				return;
			}
			this.setValue("IVA_CHECK_USER", inParm.getValue("USER_ID"));
			check_user = this.getValueString("IVA_CHECK_USER");
		}
		String case_no = this.getValueString("CASE_NO");
		String bar_code = this.getValueString("BAR_CODE");
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("IVA_CHECK_USER", check_user);
		parm.setData("BAR_CODE", bar_code);
		// String sqlM = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
		// + " A.START_DTTM,A.END_DTTM,B.BAR_CODE,B.BATCH_CODE, "
		// + " B.ORDER_DATE,B.ORDER_DATETIME "
		// + " FROM ODI_DSPNM A,ODI_DSPND B " + " WHERE A.CASE_NO='"
		// + parm.getValue("CASE_NO") + "' AND A.IVA_FLG='Y' "
		// + " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
		// + " AND A.CASE_NO=B.CASE_NO " + " AND A.ORDER_NO=B.ORDER_NO "
		// + " AND A.ORDER_SEQ=B.ORDER_SEQ "
		// + " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
		// + " A.START_DTTM AND  A.END_DTTM " + " AND B.BAR_CODE='"
		// + parm.getValue("BAR_CODE")
		// + "' AND B.IVA_FLG='Y' AND B.IVA_DEPLOY_USER IS NOT NULL "
		// + " AND B.IVA_DEPLOY_USER IS NOT NULL "
		// + " AND B.IVA_FLG = 'Y'" + " AND B.IVA_CHECK_USER IS NULL ";
		// liuyalin modify 20170330
		String sqlCode = " SELECT ORDER_CODE AS IBS_ORDER_CODE,CAT1_TYPE,ORDER_CAT1_CODE, CHARGE_HOSP_CODE AS HEXP_CODE FROM SYS_FEE WHERE ORDER_DESC = '"
				+ this.getValue("IBS_ORDER_DESC") + "'";
		TParm CodeParm = new TParm(TJDODBTool.getInstance().select(sqlCode));
		// fux modify 20170711 在配药功能上关联上了 ibs_ordd与odi_dspnm

		// CASE_NO VARCHAR2(20 BYTE) NOT NULL,
		// CASE_NO_SEQ NUMBER(5) NOT NULL,
		// BILL_DATE DATE,
		// IPD_NO VARCHAR2(20 BYTE),
		// MR_NO VARCHAR2(20 BYTE),
		// DEPT_CODE VARCHAR2(20 BYTE),
		// STATION_CODE VARCHAR2(20 BYTE),
		// BED_NO VARCHAR2(20 BYTE),
		// DATA_TYPE VARCHAR2(1 BYTE),
		// BILL_NO VARCHAR2(20 BYTE),
		// OPT_USER VARCHAR2(20 BYTE) NOT NULL,
		// OPT_DATE DATE NOT NULL,
		// OPT_TERM VARCHAR2(20 BYTE) NOT NULL,
		// REGION_CODE VARCHAR2(20 BYTE),
		// COST_CENTER_CODE VARCHAR2(20 BYTE)

		String sqlM = "SELECT X.*,C.CASE_NO_SEQ AS IBS_CASE_NO_SEQ,C.SEQ_NO AS IBS_SEQ_NO,C.EXE_DEPT_CODE,C.COST_CENTER_CODE, "
				+ "C.ORDERSET_GROUP_NO,C.ORDERSET_CODE,C.INDV_FLG,C.DS_FLG,C.DR_CODE,C.EXE_STATION_CODE, "
				+ "C.EXE_DR_CODE,C.OWN_FLG,C.BILL_FLG,C.BEGIN_DATE,C.END_DATE "
				+ "FROM (SELECT A.LINKMAIN_FLG,A.LINK_NO,A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
				+ "A.MR_NO,A.START_DTTM,A.END_DTTM,B.BAR_CODE,B.BATCH_CODE,B.ORDER_DATE,"
				+ "B.ORDER_DATETIME,B.ORDER_CODE,B.DOSAGE_UNIT,A.IBS_CASE_NO_SEQ,A.IBS_SEQ_NO,"
				+ "A.IPD_NO, A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.REGION_CODE "
				+ "FROM ODI_DSPNM A, ODI_DSPND B "
				+ "WHERE     A.CASE_NO = '"
				+ parm.getValue("CASE_NO")
				+ "'"
				+ "AND A.IVA_FLG = 'Y' "
				+ "AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
				+ "AND A.CASE_NO = B.CASE_NO "
				+ "AND A.ORDER_NO = B.ORDER_NO "
				+ "AND A.ORDER_SEQ = B.ORDER_SEQ "
				+ "AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM "
				+ "AND A.END_DTTM "
				+ "AND B.BAR_CODE = '"
				+ parm.getValue("BAR_CODE")
				+ "' "
				+ "AND B.IVA_FLG = 'Y' "
				+ "AND B.IVA_DEPLOY_USER IS NOT NULL "
				+ "AND B.IVA_CHECK_USER IS NULL) X "
				+ " ,IBS_ORDD C WHERE    "
				+ " X.CASE_NO = C.CASE_NO "
				+ " AND X.IBS_CASE_NO_SEQ = C.CASE_NO_SEQ "
				+ " AND X.IBS_SEQ_NO = C.SEQ_NO ";
		TParm updateParm = new TParm(TJDODBTool.getInstance().select(sqlM));
		// 临时倒排序版本 现在将 odi_dspnm odi_dspnd ibs_ordd 彻底关联上 已解决
		String sqlLS = " SELECT MAX(D.CASE_NO_SEQ) AS IBS_CASE_NO_SEQ "
				+ " FROM IBS_ORDM M,IBS_ORDD D "
				+ " WHERE M.CASE_NO = D.CASE_NO "
				+ " AND M.CASE_NO_SEQ =D.CASE_NO_SEQ" + " AND M.CASE_NO = '"
				+ parm.getValue("CASE_NO") + "'";
		TParm LSParm = new TParm(TJDODBTool.getInstance().select(sqlLS));
		// liuyalin 20170331 add
		for (int k = 0; k < updateParm.getCount("CASE_NO"); k++) {
			// fux 临时 修改 20170707
			updateParm.setData("EXE_DEPT_CODE", k, "040103");
			updateParm.setData("COST_CENTER_CODE", k, "040103");
			updateParm.setData("IBS_CASE_NO_SEQ", k, LSParm.getInt(
					"IBS_CASE_NO_SEQ", 0) + 1);
			// 医嘱名称都没写入
			updateParm.setData("ORDER_CHN_DESC", k, this
					.getValue("IBS_ORDER_DESC"));
			updateParm.setData("IBS_ORDER_DESC", k, this
					.getValue("IBS_ORDER_DESC"));
			updateParm.setData("OWN_PRICE", k, this.getValue("OWN_PRICE"));
			updateParm.setData("OPT_USER", k, Operator.getID());
			updateParm.setData("OPT_TERM", k, Operator.getIP());
			updateParm.setData("IBS_ORDER_CODE", k, CodeParm.getValue(
					"IBS_ORDER_CODE").replace("[", "").replace("]", "")
					.replace(" ", ""));
			updateParm.setData("ORDER_CAT1_CODE", k, CodeParm.getValue(
					"ORDER_CAT1_CODE").replace("[", "").replace("]", "")
					.replace(" ", ""));
			updateParm.setData("CAT1_TYPE", k, CodeParm.getValue("CAT1_TYPE")
					.replace("[", "").replace("]", "").replace(" ", ""));
		}
		// messageBox_(updateParm);
		for (int j = 0; j < updateParm.getCount("CASE_NO"); j++) {
			updateParm.setData("IVA_CHECK_USER", j, parm
					.getValue("IVA_CHECK_USER"));
		}
		// System.out.println("updateParm======="+updateParm);

		// messageBox("updateParm======="+updateParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.iva.IVADsAciton", "onUpdateDepCheck", updateParm);
		if (result.getErrCode() < 0) {
			messageBox("审核失败！");
			return;
		}
		// TParm result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
		// "onUpdateDepCheck", parm);
		this.messageBox("审核成功！");
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this.clearValue("PAT_NAME;STATION_CODE;MR_NO;CASE_NO;BAR_CODE;IBS_ORDER_DESC;OWN_PRICE;IVA_CHECK_USER");
		((TTextField) this.getComponent("BAR_CODE")).requestFocus();
	}

	/*
	 * 清空
	 */

	public void onClear() {
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this
				.clearValue("PAT_NAME;IVA_CHECK_USER;STATION_CODE;MR_NO;CASE_NO;BAR_CODE;IBS_ORDER_DESC;OWN_PRICE");
		((TTextField) this.getComponent("BAR_CODE")).requestFocus();
		// this.clearValue("STATION_CODE");
		// this.clearValue("MR_NO");
		// this.clearValue("CASE_NO");
		// this.clearValue("BAR_CODE");

	}

	public void onActionquery() {
		onQuery();
	}

	/*
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
