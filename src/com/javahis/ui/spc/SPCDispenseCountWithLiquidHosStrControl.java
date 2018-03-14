package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.spc.SPCSQL;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.ui.spc.util.ElectronicTagUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:住院药房普药-静配下架control
 * </p>
 * 
 * <p>
 * Description: 住院药房普药-静配下架control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liyh 20121110
 * @version 1.0
 */
public class SPCDispenseCountWithLiquidHosStrControl extends TControl {

	private static final String Y_FLAG = "Y";
	private static final String N_FLAG = "N";
	private static final int LIGHT_NUM = 100;
	private static final int TURN_LIGHT_NUM = 3;
	TTable table_N;
	TTable table_Y;

	TPanel N_PANEL;
	TPanel Y_PANEL;

	String boxEslId;// 周转箱
	TParm parm1 = new TParm();
	int k = 0;

	/** 开始时间 */
	TTextFormat start_date;

	/** 结束时间 */
	TTextFormat end_date;

	/** 病区 */
	// TTextFormat station_id;

	/** 统药单table */
	TTable table_order;

	/** 统药单号控件 */
	TTextField order_id;

	/** 统药单号 查询控件 */
	TTextField order_id_query;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.init();
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		N_PANEL = (TPanel) getComponent("PANEL_N");
		Y_PANEL = (TPanel) getComponent("PANEL_Y");

		start_date = (TTextFormat) getComponent("START_DATE");
		end_date = (TTextFormat) getComponent("END_DATE");
		table_order = this.getTable("TABLE_ORDER");
		order_id = (TTextField) getComponent("INTGMED_NO");
		order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");

		// 初始化查询区间
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		onQuery();
	}

	public void onQuery() {
		TParm parm = new TParm();

		parm.setData("START_DATE", start_date.getValue());
		parm.setData("END_DATE", end_date.getValue());
		parm.setData("STATION_ID", this.getValueString("STATION_ID"));
		parm.setData("INTGMED_NO", order_id_query.getValue());

		TParm result = SPCDispenseOutAssginNormalTool.getInstance().query(parm);
		table_order.setParmValue(result);
	}

	/** table_order 单击事件 */
	public void tableOrderClicked() {
		int row_m = table_order.getSelectedRow();
		if (row_m != -1) {
			order_id.setValue((String) table_order.getItemData(row_m,
					"INTGMED_NO"));
			onIntgmEdNo();
		}

	}

	/** panel 单击事件 */
	public void onTPanlClicked() {
		onIntgmEdNo();
	}

	/**
	 * 周转箱的回车事件
	 */
	public void onBoxId() {
		String boxIdString = getValueString("BOX_ID");
		String stationDesc = getValueString("STATION_DESC");
		String intgmedNo = getValueString("INTGMED_NO");
		if (null == stationDesc || "".equals(stationDesc)) {
			this.messageBox("请先扫描统药单");
			setValue("BOX_ID", "");
			this.getTextField("INTGMED_NO").grabFocus();
			return;
		}
		if (null != boxIdString || !"".equals(boxIdString)) {
			ElectronicTagUtil.getInstance().login();
			System.out.println("--boxIdString:" + boxIdString + ",stationDesc:"
					+ stationDesc + ",intgmedNo:" + intgmedNo
					+ ",TURN_LIGHT_NUM=" + TURN_LIGHT_NUM);
			ElectronicTagUtil.getInstance().sendEleTag(boxIdString,
					stationDesc, intgmedNo, "", TURN_LIGHT_NUM);
		}
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * 统药单的回车事件
	 * */
	public void onIntgmEdNo() {
		String intgmedNo = (String) getValue("INTGMED_NO");
		if (StringUtils.isNotBlank(intgmedNo)) {
			TParm parm = new TParm();
			if (N_PANEL.isShowing()) {
				parm = new TParm(TJDODBTool.getInstance()
						.select(
								SPCSQL.getStaOrderCodeWithLiquidInfo(intgmedNo,
										N_FLAG)));
				table_N.setParmValue(parm);
			} else if (Y_PANEL.isShowing()) {
				parm = new TParm(TJDODBTool.getInstance()
						.select(
								SPCSQL.getStaOrderCodeWithLiquidInfo(intgmedNo,
										Y_FLAG)));
				table_Y.setParmValue(parm);
			}
			if (null != parm && parm.getCount() > 0) {
				String orgChnDesc = (String) parm.getData("STATION_DESC", 0);
				setValue("STATION_DESC", orgChnDesc);
				this.getTextField("STATION_DESC").setEditable(false);
				this.getTextField("INTGMED_NO").setEditable(false);
				String boxid = getValueString("BOX_ID");
				if (null == boxid || "".equals(boxid)) {
					this.getTextField("BOX_ID").grabFocus();
				} else {
					this.getTextField("ELETAG_CODE").grabFocus();
				}
			}
		}
	}

	/**
	 * 电子标签的回车事件
	 * */
	public void onEleTagCode() {
		N_PANEL = (TPanel) getComponent("PANEL_N");
		if (N_PANEL.isShowing()) {
			// 电子标签
			String eletagCode = this.getValueString("ELETAG_CODE");
			// 统药单
			String intgmedNo = this.getValueString("INTGMED_NO");
			// 周转箱
			String turnElsId = this.getValueString("BOX_ID");
			if (StringUtil.isNullString(eletagCode)) {
				return;
			}
			if (StringUtil.isNullString(intgmedNo)) {
				this.messageBox("请先扫描统药单");
				setValue("ELETAG_CODE", "");
				this.getTextField("INTGMED_NO").grabFocus();
				return;
			}
			/*
			 * if (StringUtil.isNullString(turnElsId)) {
			 * this.messageBox("请先扫描周转箱条码"); setValue("ELETAG_CODE", "");
			 * this.getTextField("BOX_ID").grabFocus(); return; }
			 */
			table_N.acceptText();

			String labelNo = "";
			String orgCode = "";
			String orderCode = "";
			TParm parm = table_N.getParmValue();
			int count = table_N.getParmValue().getCount();

			boolean b = false;
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				orgCode = rowParm.getValue("EXEC_DEPT_CODE");
				String eleCode = rowParm.getValue("ELETAG_CODE").toUpperCase();
				if (eletagCode.toUpperCase().equals(eleCode)) {
					TParm updateParm = new TParm();
					orderCode = rowParm.getValue("ORDER_CODE");
					updateParm.setData("INTGMED_NO", intgmedNo);
					updateParm.setData("ORDER_CODE", orderCode);
					// System.out.println("------------uupdate sql :" +
					// SPCSQL.updateStateInTgLiquid(intgmedNo,
					// orderCode,turnElsId));
					TParm result = new TParm(TJDODBTool.getInstance().update(
							SPCSQL.updateStateInTgLiquid(intgmedNo, orderCode,
									turnElsId)));
					// System.out.println("---update: " + result);
					table_N.removeRow(i);
					b = true;
					break;
				}
			}

			// 是的，调用电子标签接口显示库存与闪烁
			if (b) {
				TParm inParm = new TParm();
				inParm.setData("ORDER_CODE", orderCode);
				inParm.setData("ORG_CODE", orgCode);
				// System.out.println("------------get stock qty :" +
				// SPCSQL.getStockQty(inParm));
				TParm outParm = new TParm(TJDODBTool.getInstance().select(
						SPCSQL.getQty(inParm)));
				String spec = outParm.getValue("SPECIFICATION");
				String orderDesc = outParm.getValue("ORDER_DESC");
				String qty = outParm.getValue("QTY", 0);
				//System.out.println("-------qty:" + qty + ",order_desc:"
				//		+ orderDesc + ",spec:" + spec + ",eletagCode:"
				//		+ eletagCode);
				// <------------- identify by shendr
				// ElectronicTagUtil.getInstance().login();
				// ElectronicTagUtil.getInstance().sendEleTag(eletagCode,
				// orderDesc, spec + " ", qty, LIGHT_NUM);
				// login();
				// sendEleTag(labelNo, "", spec, outParm.getValue("QTY",0), 0);
				/**
				 * 调用电子标签
				 */
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				// 如果电子标签eletagCode为空时，不调用电子标签接口
				if (!StringUtil.isNullString(eletagCode)) {
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					map.put("ProductName", orderDesc);
					map.put("SPECIFICATION", spec);
					map.put("NUM", qty);
					map.put("TagNo", eletagCode);
					map.put("Light", 1);
					// 调ind_sysparm的数据获取apRegion
					TParm res = queryApRegion();
					if (res.getErrCode() < 0) {
						this.messageBox("查询住院药房区域代码出错！");
						return;
					}
					String apRegion = res.getValue("AP_REGION", 0);
					if (apRegion == null || apRegion.equals("")) {
						System.out.println("标签区域没有设置部门代码");
					}
					map.put("APRegion", apRegion);
					list.add(map);
					if (list != null && list.size() > 0) {
						try {
							String url = Constant.LABELDATA_URL;
							LabelControl.getInstance().sendLabelDate(list, url);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("调用电子标签服务失败");
						}
					}
				}
				// ----------->
			}
			this.setValue("ELETAG_CODE", "");
			return;
		}
	}

	public void onChange() {
		onQuery();
	}

	/**
	 * 清空操作
	 * */
	public void onClear() {
		String controlName = "INTGMED_NO;STATION_DESC;BOX_ID;ELETAG_CODE";
		this.clearValue(controlName);

		this.getTextField("INTGMED_NO").setEditable(true);
		this.getTextField("ELETAG_CODE").setEditable(true);
		this.getTextField("INTGMED_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
	}

	/**
	 * 查询住院药房AP_REGION author shendr 2013.07.05
	 */
	private TParm queryApRegion() {
		String sql = "SELECT AP_REGION FROM IND_SYSPARM ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
}
