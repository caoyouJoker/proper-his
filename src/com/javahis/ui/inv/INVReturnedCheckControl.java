package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVReturnedCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title:退货核对
 * </p>
 * 
 * <p>
 * Description: 退货核对
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-11-22
 * @version 1.0
 */
public class INVReturnedCheckControl extends TControl {

	private TTable tableRM; // 退货单主表

	private TTable tableRD; // 退货单细表

	private TTable tableRDD; // 退货单细细表

	private boolean isNew = false; // 新建标记

	private String returnedNo; // 退货单号

	private TParm tParm = new TParm();

	/**
	 * 初始化
	 */
	public void onInit() {

		tableRM = (TTable) getComponent("TABLEM");
		tableRD = (TTable) getComponent("TABLED");
		tableRDD = (TTable) getComponent("TABLEDD");

		this.setTimes();

	}

	private void setTimes() {
		// 初始化 退货日期查询区间
		Timestamp date = SystemTool.getInstance().getDate();
		String startDate = date.toString().substring(0, 10).replace('-', '/')
				+ " 00:00:00";
		String endDate = date.toString().substring(0, 19).replace('-', '/');
		this.setValue("END_DATE", endDate);
		this.setValue("START_DATE", startDate);
	}

	public void onNew() {
		if ("".equals(this.getValueString("FROM_ORG_CODE"))) {
			this.messageBox("请选择退货申请部门！");
			return;
		}
		if ("".equals(this.getValueString("TO_ORG_CODE"))) {
			this.messageBox("请选择退货接收部门！");
			return;
		}
		if ("".equals(this.getValueString("START_DATE"))
				|| "".equals(this.getValueString("END_DATE"))) {
			this.messageBox("请选择日期区间！");
			return;
		}
		if (isNew) {
			this.messageBox("不能同时新建两个退货单！");
			return;
		}

		isNew = true;
		tParm = new TParm();
		returnedNo = this.getReturnedNo();

		tableRM.removeRowAll();
		tableRD.removeRowAll();
		tableRDD.removeRowAll();

		this.createNewReturnedM();
		this.createNewReturnedD();
		this.createNewReturnedDD();

		if (tParm.getData("RETURND").toString().equals("-1")) {
			this.messageBox("无需退回的物资！");
			isNew = false;
			tParm = new TParm();
			returnedNo = "";
			return;
		} else {
			TParm result = TIOM_AppServer.executeAction(
					"action.inv.INVReturnedCheckAction", "onInsert", tParm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				messageBox("保存失败！");
				isNew = false;
				tParm = new TParm();
				returnedNo = "";
				return;
			}
			this.queryByNo();
			tableRM.setSelectedRow(0);

			tableRD.setVisible(true);
			tableRDD.setVisible(false);
		}

	}

	public void onSave() {
		if (isNew) {
			if (tableRD.getRowCount() < 1) {
				messageBox("没有需要保存的数据！");
				return;
			}

			TParm tp = tableRD.getParmValue();
			TParm result = TIOM_AppServer.executeAction(
					"action.inv.INVReturnedCheckAction", "onSave", tp);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				messageBox("保存失败！");
				return;
			}
			messageBox("保存成功！");
			this.queryByNo();
			isNew = false;
			tParm = new TParm();
			returnedNo = "";

			tableRD.setVisible(true);
			tableRDD.setVisible(false);
		}
	}

	public void onDelete() {
		int row = tableRM.getSelectedRow();
		if (row < 0) {
			messageBox("请选择退货单！");
			return;
		}
		TParm tp = tableRM.getParmValue().getRow(row);
		if (tp.getData("CONFIRM_FLG").equals("Y")) {
			messageBox("已确认的退货单不能删除！");
			return;
		}

		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVReturnedCheckAction", "onDelete", tp);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			messageBox("删除失败！");
			return;
		}
		onQuery();

		isNew = false;
		tParm = new TParm();
		returnedNo = "";
	}

	public void onQuery() {

		if ("".equals(this.getValueString("START_DATE"))
				|| "".equals(this.getValueString("END_DATE"))) {
			this.messageBox("请选择日期区间！");
			return;
		}

		TParm tp = new TParm();
		if (!"".equals(this.getValueString("START_DATE"))) {
			tp.setData("START_DATE", this.getValueString("START_DATE")
					.toString().substring(0, 19));
		}
		if (!"".equals(this.getValueString("END_DATE"))) {
			tp.setData("END_DATE", this.getValueString("END_DATE").toString()
					.substring(0, 19));
		}
		if (!"".equals(this.getValueString("FROM_ORG_CODE"))) {
			tp.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
		}
		if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
			tp.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		}
		if (!"".equals(this.getValueString("RETURNEDNO"))) {
			tp.setData("RETURNED_NO", this.getValueString("RETURNEDNO"));
		}
		if (((TRadioButton) this.getComponent("CONFIRM")).isSelected()) {
			tp.setData("CONFIRM_FLG", "Y");
		} else {
			tp.setData("CONFIRM_FLG", "N");
		}
		tableRM.removeRowAll();
		tableRDD.removeRowAll();
		tableRD.removeRowAll();
		TParm mTP = INVReturnedCheckTool.getInstance().queryReturnedCheckM(tp);
		tableRM.setParmValue(mTP);

	}

	public void onTableMClick() {
		int row = tableRM.getSelectedRow();
		TParm selParm = tableRM.getParmValue().getRow(row);

		TParm tp = new TParm();
		tp.setData("RETURNED_NO", selParm.getData("RETURNED_NO"));
		TParm dTP = INVReturnedCheckTool.getInstance().queryReturnedCheckD(tp);
		tableRD.removeRowAll();
		tableRD.setParmValue(dTP);
		TParm ddTP = INVReturnedCheckTool.getInstance()
				.queryReturnedCheckDD(tp);
		tableRDD.removeRowAll();
		tableRDD.setParmValue(ddTP);
	}

	public void onClear() {

		if (isNew) {
			this.onDelete();
		}

		this.setTimes();
		tableRM.removeRowAll();
		tableRD.removeRowAll();
		tableRDD.removeRowAll();
		setValue("FROM_ORG_CODE", "");
		setValue("TO_ORG_CODE", "");
		setValue("RETURNEDNO", "");

		((TRadioButton) this.getComponent("SHOWTOTAL")).setSelected(true);
		this.onRadioChanged();

		((TRadioButton) this.getComponent("UNCONFIRM")).setSelected(true);

		isNew = false;
		tParm = new TParm();
		returnedNo = "";
	}

	private void createNewReturnedM() {
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();

		parm.setData("CONFIRM_FLG", 0, "N");
		parm.setData("RETURNED_NO", 0, returnedNo);

		parm.setData("FROM_ORG_CODE", 0, this.getValueString("FROM_ORG_CODE"));
		parm.setData("TO_ORG_CODE", 0, this.getValueString("TO_ORG_CODE"));

		parm.setData("OPT_DATE", 0, date.toString().substring(0, 19));
		parm.setData("OPT_USER", 0, Operator.getID());
		parm.setData("OPT_TERM", 0, Operator.getIP());

		parm.setData("CHECK_DATE", 0, date.toString().substring(0, 19));
		parm.setData("CHECK_USER", 0, Operator.getID());

		tParm.setData("RETURNM", parm.getData());

	}

	private void createNewReturnedD() {

		TParm allParm = new TParm();

		String sql = this.getSql();
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		sql = this.getSqlY();
		TParm parmY = new TParm(TJDODBTool.getInstance().select(sql)); // 12-16添加

		sql = this.getSqlUsedNCancelY();
		TParm parmNY = new TParm(TJDODBTool.getInstance().select(sql)); // 12-17添加

		sql = this.getSqlUsedYCancelY();
		TParm parmYY = new TParm(TJDODBTool.getInstance().select(sql)); // 12-17添加

		if ((null == parm || parm.getCount() < 1)
				&& (null == parmY || parmY.getCount() < 1)
				&& (null == parmNY || parmNY.getCount() < 1)
				&& (null == parmYY || parmYY.getCount() < 1)) {
			tParm.setData("RETURND", "-1");
			return;
		} else {
			Timestamp date = SystemTool.getInstance().getDate();
			int seq = 1;

			if (null != parm && parm.getCount() > 0) {
				for (int i = 0; i < parm.getCount("INV_CODE"); i++) {

					if (null != allParm && allParm.getCount("INV_CODE") < 0) {
						allParm
								.addData("INV_CODE", parm
										.getData("INV_CODE", i));
						allParm.addData("UNIT_CODE", parm.getData("UNIT_CODE",
								i));
						allParm.addData("QTY", parm.getData("QTY", i));
						allParm.addData("RETURNED_NO", returnedNo);
						allParm.addData("SEQ", seq);
						allParm.addData("OPT_DATE", date.toString().substring(
								0, 19));
						allParm.addData("OPT_USER", Operator.getID());
						allParm.addData("OPT_TERM", Operator.getIP());
						allParm.addData("ACTUAL_QTY", parm.getData("QTY", i));
						seq = seq + 1;
					} else {

						boolean tag = false;
						for (int m = allParm.getCount("INV_CODE") - 1; m >= 0; m--) {

							if (allParm.getValue("INV_CODE", m).equals(
									parm.getValue("INV_CODE", i))) {

								allParm.setData("QTY", m, Double
										.parseDouble(parm.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));
								allParm.setData("ACTUAL_QTY", m, Double
										.parseDouble(parm.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));

								tag = true;
							}
						}
						if (!tag) {
							allParm.addData("INV_CODE", parm.getData(
									"INV_CODE", i));
							allParm.addData("UNIT_CODE", parm.getData(
									"UNIT_CODE", i));
							allParm.addData("QTY", parm.getData("QTY", i));
							allParm.addData("RETURNED_NO", returnedNo);
							allParm.addData("SEQ", seq);
							allParm.addData("OPT_DATE", date.toString()
									.substring(0, 19));
							allParm.addData("OPT_USER", Operator.getID());
							allParm.addData("OPT_TERM", Operator.getIP());
							allParm.addData("ACTUAL_QTY", parm
									.getData("QTY", i));
							seq = seq + 1;
						}

					}

				}
			}

			// -------------------------------------------12-16添加start-------------------------------------------
			if (null != parmY && parmY.getCount() > 0) {
				for (int i = 0; i < parmY.getCount("INV_CODE"); i++) {

					if (null != allParm && allParm.getCount("INV_CODE") < 0) {
						allParm.addData("INV_CODE", parmY
								.getData("INV_CODE", i));
						allParm.addData("UNIT_CODE", parmY.getData("UNIT_CODE",
								i));
						allParm.addData("QTY", parmY.getData("QTY", i));
						allParm.addData("RETURNED_NO", returnedNo);
						allParm.addData("SEQ", seq);
						allParm.addData("OPT_DATE", date.toString().substring(
								0, 19));
						allParm.addData("OPT_USER", Operator.getID());
						allParm.addData("OPT_TERM", Operator.getIP());
						allParm.addData("ACTUAL_QTY", parmY.getData("QTY", i));
						seq = seq + 1;
					} else {

						boolean tag = false;
						for (int m = allParm.getCount("INV_CODE") - 1; m >= 0; m--) {
							if (allParm.getValue("INV_CODE", m).equals(
									parmY.getValue("INV_CODE", i))) {

								allParm.setData("QTY", m, Double
										.parseDouble(parmY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));
								allParm.setData("ACTUAL_QTY", m, Double
										.parseDouble(parmY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));

								tag = true;
							}
						}
						if (!tag) {

							allParm.addData("INV_CODE", parmY.getData(
									"INV_CODE", i));
							allParm.addData("UNIT_CODE", parmY.getData(
									"UNIT_CODE", i));
							allParm.addData("QTY", parmY.getData("QTY", i));
							allParm.addData("RETURNED_NO", returnedNo);
							allParm.addData("SEQ", seq);
							allParm.addData("OPT_DATE", date.toString()
									.substring(0, 19));
							allParm.addData("OPT_USER", Operator.getID());
							allParm.addData("OPT_TERM", Operator.getIP());
							allParm.addData("ACTUAL_QTY", parmY.getData("QTY",
									i));

							seq = seq + 1;

						}

					}

				}
			}
			// -------------------------------------------12-16添加end-------------------------------------------

			// -------------------------------------------12-18添加start-------------------------------------------
			if (null != parmNY && parmNY.getCount() > 0) {
				for (int i = 0; i < parmNY.getCount("INV_CODE"); i++) {

					if (null != allParm && allParm.getCount("INV_CODE") < 0) {
						allParm.addData("INV_CODE", parmNY.getData("INV_CODE",
								i));
						allParm.addData("UNIT_CODE", parmNY.getData(
								"UNIT_CODE", i));
						allParm.addData("QTY", parmNY.getData("QTY", i));
						allParm.addData("RETURNED_NO", returnedNo);
						allParm.addData("SEQ", seq);
						allParm.addData("OPT_DATE", date.toString().substring(
								0, 19));
						allParm.addData("OPT_USER", Operator.getID());
						allParm.addData("OPT_TERM", Operator.getIP());
						allParm.addData("ACTUAL_QTY", parmNY.getData("QTY", i));
						seq = seq + 1;
					} else {
						boolean tag = false;
						for (int m = allParm.getCount("INV_CODE") - 1; m >= 0; m--) {
							if (allParm.getValue("INV_CODE", m).equals(
									parmNY.getValue("INV_CODE", i))) {

								allParm.setData("QTY", m, Double
										.parseDouble(parmNY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));
								allParm.setData("ACTUAL_QTY", m, Double
										.parseDouble(parmNY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));

								tag = true;
							}
						}
						if (!tag) {

							allParm.addData("INV_CODE", parmNY.getData(
									"INV_CODE", i));
							allParm.addData("UNIT_CODE", parmNY.getData(
									"UNIT_CODE", i));
							allParm.addData("QTY", parmNY.getData("QTY", i));
							allParm.addData("RETURNED_NO", returnedNo);
							allParm.addData("SEQ", seq);
							allParm.addData("OPT_DATE", date.toString()
									.substring(0, 19));
							allParm.addData("OPT_USER", Operator.getID());
							allParm.addData("OPT_TERM", Operator.getIP());
							allParm.addData("ACTUAL_QTY", parmNY.getData("QTY",
									i));

							seq = seq + 1;

						}

					}
				}
			}

			if (null != parmYY && parmYY.getCount() > 0) {
				for (int i = 0; i < parmYY.getCount("INV_CODE"); i++) {

					if (null != allParm && allParm.getCount("INV_CODE") < 0) {
						allParm.addData("INV_CODE", parmYY.getData("INV_CODE",
								i));
						allParm.addData("UNIT_CODE", parmYY.getData(
								"UNIT_CODE", i));
						allParm.addData("QTY", parmYY.getData("QTY", i));
						allParm.addData("RETURNED_NO", returnedNo);
						allParm.addData("SEQ", seq);
						allParm.addData("OPT_DATE", date.toString().substring(
								0, 19));
						allParm.addData("OPT_USER", Operator.getID());
						allParm.addData("OPT_TERM", Operator.getIP());
						allParm.addData("ACTUAL_QTY", parmYY.getData("QTY", i));

						seq = seq + 1;
					} else {
						boolean tag = false;
						for (int m = allParm.getCount("INV_CODE") - 1; m >= 0; m--) {
							if (allParm.getValue("INV_CODE", m).equals(
									parmYY.getValue("INV_CODE", i))) {

								allParm.setData("QTY", m, Double
										.parseDouble(parmYY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));
								allParm.setData("ACTUAL_QTY", m, Double
										.parseDouble(parmYY.getData("QTY", i)
												.toString())
										+ Double.parseDouble(allParm.getData(
												"QTY", m).toString()));

								tag = true;
							}
						}
						if (!tag) {

							allParm.addData("INV_CODE", parmYY.getData(
									"INV_CODE", i));
							allParm.addData("UNIT_CODE", parmYY.getData(
									"UNIT_CODE", i));
							allParm.addData("QTY", parmYY.getData("QTY", i));
							allParm.addData("RETURNED_NO", returnedNo);
							allParm.addData("SEQ", seq);
							allParm.addData("OPT_DATE", date.toString()
									.substring(0, 19));
							allParm.addData("OPT_USER", Operator.getID());
							allParm.addData("OPT_TERM", Operator.getIP());
							allParm.addData("ACTUAL_QTY", parmYY.getData("QTY",
									i));

							seq = seq + 1;

						}

					}
				}
			}
			// -------------------------------------------12-18添加end-------------------------------------------

			tParm.setData("RETURND", allParm.getData());
		}
		// System.out.println("RETURND--------"+allParm.getData());
	}

	private void createNewReturnedDD() {

		TParm allParm = new TParm();

		String sql = this.getDetailSql();
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		sql = this.getDetailSqlY();
		TParm parmY = new TParm(TJDODBTool.getInstance().select(sql));

		sql = this.getDetailSqlUsedNCancelY();
		TParm parmNY = new TParm(TJDODBTool.getInstance().select(sql));

		sql = this.getDetailSqlUsedYCancelY();
		TParm parmYY = new TParm(TJDODBTool.getInstance().select(sql));

		if ((null == parm || parm.getCount() < 1)
				&& (null == parmY || parmY.getCount() < 1)
				&& (null == parmNY || parmNY.getCount() < 1)
				&& (null == parmYY || parmYY.getCount() < 1)) {
			tParm.setData("RETURNDD", "-1");
			return;
		} else {
			Timestamp date = SystemTool.getInstance().getDate();
			int seq = 1;

			if (null == parm || parm.getCount() > 0) {
				for (int j = parm.getCount("INV_CODE") - 1; j >= 0; j--) {

					allParm.addData("RETURNED_NO", returnedNo);
					allParm.addData("SEQ", seq);
					allParm.addData("OPT_DATE", date.toString()
							.substring(0, 19));
					allParm.addData("OPT_USER", Operator.getID());
					allParm.addData("OPT_TERM", Operator.getIP());

					allParm.addData("INV_CODE", parm.getData("INV_CODE", j));
					allParm.addData("QTY", parm.getData("QTY", j));
					allParm.addData("UNIT_CODE", parm.getData("UNIT_CODE", j));
					allParm.addData("PACK_CODE", parm.getData("PACK_CODE", j));
					allParm.addData("PACK_GROUP_NO", parm.getData(
							"PACK_GROUP_NO", j));
					allParm.addData("MR_NO", parm.getData("MR_NO", j));
					allParm.addData("PAT_NAME", parm.getData("PAT_NAME", j));
					allParm.addData("BUSINESS_NO", parm.getData("BUSINESS_NO",
							j));
					allParm.addData("BUSINESS_SEQ", parm.getData(
							"BUSINESS_SEQ", j));
					seq = seq + 1;

				}
			}

			// -------------------------------------------12-16添加start-------------------------------------------
			if (null != parmY || parmY.getCount() > 0) {

				for (int j = parmY.getCount("INV_CODE") - 1; j >= 0; j--) {

					allParm.addData("RETURNED_NO", returnedNo);
					allParm.addData("SEQ", seq);
					allParm.addData("OPT_DATE", date.toString()
							.substring(0, 19));
					allParm.addData("OPT_USER", Operator.getID());
					allParm.addData("OPT_TERM", Operator.getIP());

					allParm.addData("INV_CODE", parmY.getData("INV_CODE", j));
					allParm.addData("QTY", parmY.getData("QTY", j));
					allParm.addData("UNIT_CODE", parmY.getData("UNIT_CODE", j));
					allParm.addData("PACK_CODE", parmY.getData("PACK_CODE", j));
					allParm.addData("PACK_GROUP_NO", parmY.getData(
							"PACK_GROUP_NO", j));
					allParm.addData("MR_NO", parmY.getData("MR_NO", j));
					allParm.addData("PAT_NAME", parmY.getData("PAT_NAME", j));
					allParm.addData("BUSINESS_NO", parmY.getData("BUSINESS_NO",
							j));
					allParm.addData("BUSINESS_SEQ", parmY.getData(
							"BUSINESS_SEQ", j));
					seq = seq + 1;

				}

			}
			// -------------------------------------------12-16添加end-------------------------------------------

			// -------------------------------------------12-18添加start-------------------------------------------
			if (null != parmNY || parmNY.getCount() > 0) {

				for (int j = parmNY.getCount("INV_CODE") - 1; j >= 0; j--) {

					allParm.addData("RETURNED_NO", returnedNo);
					allParm.addData("SEQ", seq);
					allParm.addData("OPT_DATE", date.toString()
							.substring(0, 19));
					allParm.addData("OPT_USER", Operator.getID());
					allParm.addData("OPT_TERM", Operator.getIP());

					allParm.addData("INV_CODE", parmNY.getData("INV_CODE", j));
					allParm.addData("QTY", parmNY.getData("QTY", j));
					allParm
							.addData("UNIT_CODE", parmNY
									.getData("UNIT_CODE", j));
					allParm
							.addData("PACK_CODE", parmNY
									.getData("PACK_CODE", j));
					allParm.addData("PACK_GROUP_NO", parmNY.getData(
							"PACK_GROUP_NO", j));
					allParm.addData("MR_NO", parmNY.getData("MR_NO", j));
					allParm.addData("PAT_NAME", parmNY.getData("PAT_NAME", j));
					allParm.addData("BUSINESS_NO", parmNY.getData(
							"BUSINESS_NO", j));
					allParm.addData("BUSINESS_SEQ", parmNY.getData(
							"BUSINESS_SEQ", j));
					seq = seq + 1;

				}

			}

			if (null != parmYY || parmYY.getCount() > 0) {

				for (int j = parmYY.getCount("INV_CODE") - 1; j >= 0; j--) {

					allParm.addData("RETURNED_NO", returnedNo);
					allParm.addData("SEQ", seq);
					allParm.addData("OPT_DATE", date.toString()
							.substring(0, 19));
					allParm.addData("OPT_USER", Operator.getID());
					allParm.addData("OPT_TERM", Operator.getIP());

					allParm.addData("INV_CODE", parmYY.getData("INV_CODE", j));
					allParm.addData("QTY", parmYY.getData("QTY", j));
					allParm
							.addData("UNIT_CODE", parmYY
									.getData("UNIT_CODE", j));
					allParm
							.addData("PACK_CODE", parmYY
									.getData("PACK_CODE", j));
					allParm.addData("PACK_GROUP_NO", parmYY.getData(
							"PACK_GROUP_NO", j));
					allParm.addData("MR_NO", parmYY.getData("MR_NO", j));
					allParm.addData("PAT_NAME", parmYY.getData("PAT_NAME", j));
					allParm.addData("BUSINESS_NO", parmYY.getData(
							"BUSINESS_NO", j));
					allParm.addData("BUSINESS_SEQ", parmYY.getData(
							"BUSINESS_SEQ", j));
					seq = seq + 1;

				}

			}
			// -------------------------------------------12-18添加end-------------------------------------------
			tParm.setData("RETURNDD", allParm.getData());
		}
		// System.out.println("RETURNDD--------"+allParm.getData());

	}

	public void onRadioChanged() {

		TRadioButton showTotal = (TRadioButton) this.getComponent("SHOWTOTAL");
		TRadioButton showDetail = (TRadioButton) this
				.getComponent("SHOWDETAIL");

		if (showTotal.isSelected()) {
			tableRD.setVisible(true);
			tableRDD.setVisible(false);
		} else if (showDetail.isSelected()) {
			tableRD.setVisible(false);
			tableRDD.setVisible(true);
		}

	}

	/**
	 * 生成退货单号
	 * */
	private String getReturnedNo() {
		String returnedNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_SUP_RETURNM", "No");
		return returnedNo;
	}

	/**
	 * 根据退货单编号查询退货单信息
	 * */
	private void queryByNo() {
		TParm tp = new TParm();
		tp.setData("RETURNED_NO", returnedNo);
		tp.setData("CONFIRM_FLG", "N");

		TParm mTP = INVReturnedCheckTool.getInstance().queryReturnedCheckM(tp);
		TParm dTP = INVReturnedCheckTool.getInstance().queryReturnedCheckD(tp);
		TParm ddTP = INVReturnedCheckTool.getInstance()
				.queryReturnedCheckDD(tp);

		tableRM.setParmValue(mTP);
		tableRD.setParmValue(dTP);
		tableRDD.setParmValue(ddTP);

	}

	/**
	 * 查询退货明细 汇总SQL 当USED_FLG = 'N' CANCEL_FLG = 'N'时
	 * */
	private String getSql() {

		String sql = " SELECT SUM(S.QTY) AS QTY, S.INV_CODE, S.UNIT_CODE "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE "
				+ " WHERE S.USED_FLG = 'N' AND S.CHECK_FLG = 'N' AND S.CANCEL_FLG = 'N' "
				+
				// " AND S.BILL_DATE BETWEEN TO_DATE('" +
				// this.getValueString("START_DATE").toString().substring(0, 19)
				// + "','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('" +
				// this.getValueString("END_DATE").toString().substring(0, 19) +
				// "','yyyy/mm/dd hh24:mi:ss') " +
				" AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE")
				+ "' AND S.PACK_BARCODE IS NOT NULL "
				+ " AND SUBSTR(S.PACK_BARCODE, 7, 6) = '000000' GROUP BY S.INV_CODE, S.UNIT_CODE ";

		return sql;
	}

	/**
	 * 查询退货明细 汇总SQL 当USED_FLG = 'Y' CANCEL_FLG = 'N'时 2013-12-16添加
	 * */
	private String getSqlY() {

		String sql = "  SELECT SUM(D.QTY - S.QTY) AS QTY, S.INV_CODE, S.UNIT_CODE " 
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE LEFT JOIN INV_PACKD D ON " +
				  " ( S.INV_CODE = D.INV_CODE AND SUBSTR(S.PACK_BARCODE, 1, 6) = D.PACK_CODE ) "
				+ " WHERE S.USED_FLG = 'Y' AND S.CHECK_FLG = 'N' AND D.QTY > S.QTY AND S.CANCEL_FLG = 'N' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE")
				+ "' AND S.PACK_BARCODE IS NOT NULL AND SUBSTR(S.PACK_BARCODE, 7, 6) = '000000' GROUP BY S.INV_CODE, S.UNIT_CODE ";

		return sql;

	}

	/**
	 * 查询取消后的退货明细 汇总SQL 当USED_FLG = 'N' CANCEL_FLG = 'Y'时 2013-12-17添加
	 * */
	private String getSqlUsedNCancelY() {

		String sql = " SELECT SUM(S.QTY) AS QTY, S.INV_CODE, S.UNIT_CODE "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE "
				+ " WHERE S.USED_FLG = 'N' AND S.CHECK_FLG = 'N' AND S.CANCEL_FLG = 'Y' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE")
				+ "' AND S.PACK_BARCODE IS NOT NULL AND SUBSTR(S.PACK_BARCODE, 7, 6) = '000000' GROUP BY S.INV_CODE, S.UNIT_CODE ";

		return sql;

	}

	/**
	 * 查询取消后的退货明细 汇总SQL 当USED_FLG = 'Y' CANCEL_FLG = 'Y'时 2013-12-17添加
	 * */
	private String getSqlUsedYCancelY() {

		String sql = "  SELECT SUM(D.QTY - ABS(S.QTY))*-1 AS QTY, S.INV_CODE, S.UNIT_CODE "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE LEFT JOIN INV_PACKD D ON" +
				  " ( S.INV_CODE = D.INV_CODE AND SUBSTR(S.PACK_BARCODE, 1, 6) = D.PACK_CODE ) "
				+ " WHERE S.USED_FLG = 'Y' AND S.CHECK_FLG = 'N' AND D.QTY > ABS(S.QTY) AND S.CANCEL_FLG = 'Y' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE")
				+ "' AND S.PACK_BARCODE IS NOT NULL AND SUBSTR(S.PACK_BARCODE, 7, 6) = '000000' GROUP BY S.INV_CODE, S.UNIT_CODE ";

		return sql;

	}

	/**
	 * 查询退货明细项 SQL 当USED_FLG = 'N' CANCEL_FLG = 'N'时
	 * */
	private String getDetailSql() {

		String sql = "  SELECT  S.INV_CODE, S.UNIT_CODE, S.QTY, S.MR_NO, S.PAT_NAME, S.PACK_BARCODE AS PACK_CODE, S.PACK_GROUP_NO, " +
				" S.BUSINESS_NO, S.SEQ AS BUSINESS_SEQ  "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE "
				+ " WHERE S.USED_FLG = 'N' AND S.CHECK_FLG = 'N' AND S.CANCEL_FLG = 'N' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE") + "' ";

		return sql;

	}

	/**
	 * 查询退货明细项 SQL 当USED_FLG = 'Y' CANCEL_FLG = 'N'时 2013-12-16添加
	 * */
	private String getDetailSqlY() {

		String sql = "  SELECT  S.INV_CODE, S.UNIT_CODE, ( D.QTY - S.QTY ) AS QTY, S.MR_NO, S.PAT_NAME, S.PACK_BARCODE AS PACK_CODE, S.PACK_GROUP_NO, S.BUSINESS_NO, S.SEQ AS BUSINESS_SEQ "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE LEFT JOIN INV_PACKD D ON ( S.INV_CODE = D.INV_CODE AND SUBSTR(S.PACK_BARCODE, 1, 6) = D.PACK_CODE ) "
				+ " WHERE S.USED_FLG = 'Y' AND S.CHECK_FLG = 'N' AND D.QTY > S.QTY AND S.CANCEL_FLG = 'N' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE") + "' ";

		return sql;

	}

	/**
	 * 查询取消后的退货明细项 SQL 当USED_FLG = 'N' CANCEL_FLG = 'Y'时 2013-12-16添加
	 * */
	private String getDetailSqlUsedNCancelY() {

		String sql = "  SELECT  S.INV_CODE, S.UNIT_CODE, S.QTY, S.MR_NO, S.PAT_NAME, S.PACK_BARCODE AS PACK_CODE, S.PACK_GROUP_NO, S.BUSINESS_NO, S.SEQ AS BUSINESS_SEQ "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE "
				+ " WHERE S.USED_FLG = 'N' AND S.CHECK_FLG = 'N' AND S.CANCEL_FLG = 'Y' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE") + "' ";

		return sql;

	}

	/**
	 * 查询取消后的退货明细项 SQL 当USED_FLG = 'Y' CANCEL_FLG = 'Y'时 2013-12-16添加
	 * */
	private String getDetailSqlUsedYCancelY() {

		String sql = "  SELECT  S.INV_CODE, S.UNIT_CODE, ( D.QTY - ABS(S.QTY) ) * -1 AS QTY, S.MR_NO, S.PAT_NAME, S.PACK_BARCODE AS PACK_CODE, S.PACK_GROUP_NO, S.BUSINESS_NO, S.SEQ AS BUSINESS_SEQ "
				+ " FROM SPC_INV_RECORD S LEFT JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE LEFT JOIN INV_PACKD D ON ( S.INV_CODE = D.INV_CODE AND SUBSTR(S.PACK_BARCODE, 1, 6) = D.PACK_CODE ) "
				+ " WHERE S.USED_FLG = 'Y' AND S.CHECK_FLG = 'N' AND D.QTY > ABS(S.QTY) AND S.CANCEL_FLG = 'Y' "
				+ " AND S.BILL_DATE <= TO_DATE('"
				+ this.getValueString("END_DATE").toString().substring(0, 19)
				+ "','yyyy/mm/dd hh24:mi:ss') "
				+ " AND S.EXE_DEPT_CODE = '"
				+ this.getValueString("FROM_ORG_CODE") + "' ";

		return sql;

	}

}
