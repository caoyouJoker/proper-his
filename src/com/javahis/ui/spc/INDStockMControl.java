package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDSQL;
import jdo.spc.IndStockMTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 药库库存主档
 * </p>
 * 
 * <p>
 * Description: 药库库存主档
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) ProperSoft 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liyh
 * @version 4.0
 */
public class INDStockMControl extends TControl {

	private String action = "insertM";

	// 主项表格
	private TTable table_m;

	// 细项表格
	private TTable table_d;

	public INDStockMControl() {

	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// 初始化TABLE
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 传入参数集
		TParm parm = new TParm();
		// 返回结果集
		TParm result = new TParm();
		String region_code = Operator.getRegion();
		Timestamp date = SystemTool.getInstance().getDate();
		parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
		parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		parm.setData("REGION_CODE", region_code);
		parm.setData("MATERIAL_LOC_CODE", this
				.getValueString("MATERIAL_LOC_CODE"));
		parm.setData("MATERIAL_LOC_DESC", this
				.getValueString("MATERIAL_LOC_DESC"));
		parm.setData("ELETAG_CODE", this.getValueString("ELETAG_CODE"));
		parm.setData("DISPENSE_FLG", this.getValueString("DISPENSE_FLG"));
		parm.setData("DISPENSE_ORG_CODE", this
				.getValueString("DISPENSE_ORG_CODE"));
		parm.setData("QTY_TYPE", this.getValueString("QTY_TYPE"));
		parm.setData("MM_USE_QTY", this.getValueDouble("MM_USE_QTY"));
		parm.setData("DD_USE_QTY", this.getValueDouble("DD_USE_QTY"));
		parm.setData("MAX_QTY", this.getValueDouble("MAX_QTY"));
		parm.setData("SAFE_QTY", this.getValueDouble("SAFE_QTY"));
		parm.setData("MIN_QTY", this.getValueDouble("MIN_QTY"));
		parm.setData("ECONOMICBUY_QTY", this.getValueDouble("ECONOMICBUY_QTY"));
		parm.setData("BUY_UNRECEIVE_QTY", this
				.getValueDouble("BUY_UNRECEIVE_QTY"));
		parm.setData("STANDING_QTY", this.getValueDouble("STANDING_QTY"));
		parm.setData("LOCK_QTY", 0);
		// <---------- identity by shendr 20131104
		// String active_flg = this.getValueString("ACTIVE_FLG");
		// if("N".equals(active_flg))
		// active_flg="Y";
		// else
		// active_flg="N";
		// parm.setData("ACTIVE_FLG", active_flg);
		// ---------->
		parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
		parm.setData("MATERIAL_LOC_SEQ", this
				.getValueString("MATERIAL_LOC_SEQ"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
		if ("insertM".equals(action)) {
			if (!CheckDataM()) {
				return;
			}
			result = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDStockM(this.getValueString("ORG_CODE"), this
							.getValueString("ORDER_CODE"))));
			if (result.getCount() > 0) {
				this.messageBox("该药品已存在");
				return;
			}
			result = IndStockMTool.getInstance().onInsertIndStockM(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
		} else if ("updateM".equals(action)) {
			if (!CheckDataM()) {
				return;
			}
			// 执行数据新增
			result = TIOM_AppServer.executeAction("action.spc.INDStockMAction",
					"onUpdate", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
		}else if ("updateD".equals(action)) {
			table_d.acceptText();
			int rows = table_d.getRowCount();
			TParm outParm = table_d.getParmValue();
			String org_code = this.getValueString("ORG_CODE");
			String order_code = this.getValueString("ORDER_CODE");
			String active_flg = "";
			String batch_seq = "";
			for (int i = 0; i < rows; i++) {
				active_flg = table_d.getParmValue().getRow(i).getValue("ACTIVE_FLG");
				if ("Y".equals(outParm.getValue("ACTIVE_FLG",i))) 
					active_flg = "N";
				else
					active_flg = "Y";
				batch_seq = outParm.getValue("BATCH_SEQ",i);
				result = new TParm(TJDODBTool.getInstance().update(INDSQL.updateIndStock(
						org_code, order_code, active_flg, batch_seq)));
			}
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			messageBox("P0001");
		} else {
			TDataStore dataStore = table_d.getDataStore();
			// 获得全部改动的行号
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table_d.update()) {
				messageBox("E0001");
				return;
			}
			table_d.setDSValue();
			messageBox("P0001");
		}
		onQuery();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		table_m.removeRowAll();
		table_d.removeRowAll();
		String org_code = getValueString("ORG_CODE");
		if ("".equals(org_code)) {
			this.messageBox("部门代码不能为空");
			return;
		}
		if ("Y".equals(this.getValueString("SAFE_FLG"))
				&& "Y".equals(this.getValueString("MAX_FLG"))) {
			this.messageBox("不可同时选择低于安全库存量和高于最高存量");
			return;
		}
		String sql = INDSQL.getIndStockMInfo(org_code,
				getValueString("ORDER_CODE"), "");
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("---"+sql);
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("没有查询数据");
			return;
		}
		TParm result = new TParm();
		String qty = "0.0";
		for (int i = 0; i < parm.getCount(); i++) {
			TParm stock_qty = new TParm();
			stock_qty.setData("ORG_CODE", this.getValueString("ORG_CODE"));
			stock_qty.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			// String qtyS = INDTool.getInstance().getCurrentStockQty(org_code,
			// parm.getValue("ORDER_CODE", i));//
			// IndStockDTool.getInstance().onQueryStockQTY(stock_qty);
			// <--- 界面修改 by shendr
			sql = INDSQL.getStockQtyOfStockM(this.getValueString("ORG_CODE"),
					parm.getData("ORDER_CODE", i).toString());
			result = new TParm(TJDODBTool.getInstance().select(sql));
			// qty = result.getDouble("QTY", 0);
			qty = result.getValue("STOCK_QTY");
			if (qty == "" || qty == "0.0") {
				qty = "0盒";
			} else {
				qty = qty.substring(qty.indexOf("[") + 1);
				qty = qty.substring(0, qty.lastIndexOf("]"));
			}
			// --->
			// System.out.println(parm.getValue("ORDER_CODE",
			// i)+"----qtyS:"+qtyS);
			/*
			 * qty = result.getDouble("QTY", 0); if (qty < 0) { qty = 0; }
			 */

			// parm.setData("STOCK_QTY", i, qtyS);
			parm.setData("STOCK_QTY", i, qty);
			String sql1 = INDSQL.getDeptDesc(parm.getValue("DISPENSE_ORG_CODE",
					i));
			TParm orgDesc = new TParm(TJDODBTool.getInstance().select(sql1));
			parm.setData("DISPENSE_ORG_CODE", i, orgDesc.getData(
					"ORG_CHN_DESC", 0));
		}

		/*
		 * if ("Y".equals(getValueString("SAFE_FLG"))) { for (int i =
		 * parm.getCount() - 1; i >= 0; i--) { if (parm.getDouble("STOCK_QTY",
		 * i) < parm.getDouble("SAFE_QTY", i)) { parm.removeRow(i); } } }
		 */
		/*
		 * if ("Y".equals(getValueString("MAX_FLG"))) { for (int i =
		 * parm.getCount() - 1; i >= 0; i--) { if (parm.getDouble("STOCK_QTY",
		 * i) > parm.getDouble("MAX_QTY", i)) { parm.removeRow(i); } } }
		 */
		// for (int i = 0; i < parm.getCount(); i++) {
		// if ("N".equals(parm.getValue("ACTIVE_FLG",i))) {
		// parm.setData("ACTIVE_FLG", i, "Y");
		// }else {
		// parm.setData("ACTIVE_FLG", i, "N");
		// }
		// }
		table_m.setParmValue(parm);
	}

	/**
	 * 更新料位
	 * 
	 * @author liyh
	 * @date 20121013
	 */
	public void onUpdate() {
		TParm parm = new TParm();
		if (null != parm && parm.getCount() > 0) {
			int count = parm.getCount();
			for (int i = 0; i < count; i++) {
				String orgCode = parm.getValue("ORG_CODE", i);
				String orderCode = parm.getValue("ORDER_CODE", i);
				String eleTagCode = parm.getValue("ELETAG_CODE", i);
				TParm result = new TParm(TJDODBTool.getInstance()
						.update(
								INDSQL.updateEleTagCode(eleTagCode, orgCode,
										orderCode)));
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("更新失败");
					return;
				}
			}
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空VALUE
		String clear = "ORG_CODE;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE;DISPENSE_FLG;"
				+ "DISPENSE_ORG_CODE;MM_USE_QTY;DD_USE_QTY;MAX_QTY;SAFE_QTY;"
				+ "QTY_TYPE;MIN_QTY;ECONOMICBUY_QTY;BUY_UNRECEIVE_QTY;SUP_CODE;"
				+ "STANDING_QTY;ACTIVE_FLG;SAFE_FLG;MAX_FLG;MATERIAL_LOC_DESC;ELETAG_CODE";
		this.clearValue(clear);
		table_m.removeRowAll();
		table_m.setSelectionMode(0);
		table_d.removeRowAll();
		table_d.setSelectionMode(0);
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
		// .setEnabled(false);
		((TCheckBox) this.getComponent("DISPENSE_FLG")).setSelected(true);
		action = "insertM";
	}

	/**
	 * TABLE_M单击事件
	 */
	public void onTableMClicked() {
		int row = table_m.getSelectedRow();
		TParm parm = table_m.getParmValue();
		if (row != -1) {
			this.setValue("ORDER_CODE", table_m.getItemData(row, "ORDER_CODE"));
			this.setValue("ORDER_DESC", table_m.getItemData(row, "ORDER_DESC"));
			// 添加 料位 和电子标签 by liyh 20121026
			this.setValue("MATERIAL_LOC_CODE", table_m.getItemData(row,
					"MATERIAL_LOC_CODE"));
			this.setValue("MATERIAL_LOC_DESC", parm.getValue(
					"MATERIAL_LOC_DESC", row));
			// 主要供应商
			this.setValue("SUP_CODE", parm.getValue("SUP_CODE", row));
			this.setValue("ELETAG_CODE", table_m
					.getItemData(row, "ELETAG_CODE"));
			this.setValue("DISPENSE_FLG", table_m.getItemData(row,
					"DISPENSE_FLG"));
			// this.setValue("DISPENSE_ORG_CODE", table_m.getItemData(row,
			// "DISPENSE_ORG_CODE"));
			this.setValue("MM_USE_QTY", table_m.getItemData(row, "MM_USE_QTY"));
			this.setValue("DD_USE_QTY", table_m.getItemData(row, "DD_USE_QTY"));
			this.setValue("MAX_QTY", table_m.getItemData(row, "MAX_QTY"));
			this.setValue("SAFE_QTY", table_m.getItemData(row, "SAFE_QTY"));
			this.setValue("MIN_QTY", table_m.getItemData(row, "MIN_QTY"));
			this.setValue("ECONOMICBUY_QTY", table_m.getItemData(row,
					"ECONOMICBUY_QTY"));
			this.setValue("BUY_UNRECEIVE_QTY", table_m.getItemData(row,
					"BUY_UNRECEIVE_QTY"));
			this.setValue("STANDING_QTY", table_m.getItemData(row,
					"STANDING_QTY"));
			this.setValue("QTY_TYPE", table_m.getItemData(row, "QTY_TYPE"));
			this.setValue("ACTIVE_FLG", table_m.getItemData(row, "ACTIVE_FLG"));
			String locSeq = table_m.getItemData(row, "MATERIAL_LOC_SEQ") + "";
			this.setValue("MATERIAL_LOC_SEQ", locSeq);

			// if ("Y".equals(table_m.getItemData(row, "DISPENSE_FLG"))) {
			// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
			// .setEnabled(true);
			// } else {
			// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
			// .setEnabled(false);
			// }
			action = "updateM";

			// 明细信息
			table_d.removeRowAll();
			table_d.setSelectionMode(0);
			TParm parmD = new TParm(TJDODBTool.getInstance().select(
					INDSQL
							.getINDStockAllOfStockM(this
									.getValueString("ORG_CODE"), this
									.getValueString("ORDER_CODE"), Operator
									.getRegion())));
			 for (int i = 0; i < parmD.getCount(); i++) {
				 if ("N".equals(parmD.getValue("ACTIVE_FLG",i))) {
				 parmD.setData("ACTIVE_FLG", i, "Y");
				 }else {
				 parmD.setData("ACTIVE_FLG", i, "N");
				 }
			 }
			table_d.setParmValue(parmD);
			/*
			 * tds.setSQL(INDSQL.getINDStockAllOfStockM(this.getValueString("ORG_CODE"
			 * ), this.getValueString("ORDER_CODE"), Operator.getRegion()));
			 * tds.retrieve(); if (tds.rowCount() == 0) {
			 * this.messageBox("没有明细信息"); }
			 * 
			 * // 观察者 IndMainBatchValidObserver obser = new
			 * IndMainBatchValidObserver(); tds.addObserver(obser);
			 * table_d.setDataStore(tds); table_d.setDSValue();
			 */
		}
	}

	/**
	 * 明细表格(TABLE_D)单击事件
	 */
	public void onTableDClicked() {
		int row_d = table_d.getSelectedRow();
		if (row_d != -1) {
			action = "updateD";
			table_m.setSelectionMode(0);
		}
	}

	/**
	 * 部门改变事件
	 */
	public void onChangeOrgCode() {
		this.setValue("MATERIAL_LOC_CODE", "");
		String org_code = this.getValueString("ORG_CODE");
		// if (!"".equals(org_code)) {
		// // 设定料位
		// ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
		// setOrgCode(org_code);
		// ( (TComboINDMaterialloc)
		// getComponent("MATERIAL_LOC_CODE")).onQuery();
		// }

		// TParm parm = new TParm(TJDODBTool.getInstance().select(
		// INDSQL.getINDOrgType(org_code)));
		// String org_type = "";
		// this.setValue("DISPENSE_ORG_CODE", "");
		// if ("C".equals(parm.getValue("ORG_TYPE", 0))) {
		// org_type = "B";
		// } else if ("B".equals(parm.getValue("ORG_TYPE", 0))) {
		// org_type = "A";
		// } else {
		// org_type = "-";
		// }
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
		// .setOrgType(org_type);
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE")).onQuery();
	}

	/**
	 * 拨补改变事件
	 */
	public void onDispenseFlgAction() {
		if ("Y".equals(this.getValueString("DISPENSE_FLG"))) {
			((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
					.setEnabled(true);
		} else {
			((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
					.setEnabled(false);
		}
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * 导入国药出货单
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpXML() {
		TParm parm = new TParm();
		Object result = openDialog(
				"%ROOT%\\config\\spc\\INDMateriallocEleTag.x", parm);
		if (null != result) {
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm xmlParm = new TParm();
			try {
				try {
					filePath = filePath.replaceAll("\\\\", "/");
					xmlParm = (TParm) FileUtils
							.readXMLFileOfMateriaLocAndOrderCode(filePath);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int count = xmlParm.getCount("ORDER_CODE");
				if (null != xmlParm && count > 0) {
					TParm resultTParm = new TParm();
					for (int i = 0; i < count; i++) {
						TParm mlTParm = new TParm();
						String orgCode = (String) xmlParm
								.getData("ORG_CODE", i);
						String orderCode = (String) xmlParm.getData(
								"ORDER_CODE", i);
						String materialLocCode = (String) xmlParm.getData(
								"MATERIAL_LOC_CODE", i);
						mlTParm.setData("ORG_CODE", orgCode);
						mlTParm.setData("ORDER_CODE", orderCode);
						mlTParm.setData("REGION_CODE", materialLocCode);
						mlTParm.setData("MATERIAL_LOC_CODE", xmlParm.getData(
								"MATERIAL_LOC_CODE", i));
						mlTParm.setData("MATERIAL_LOC_DESC", xmlParm.getData(
								"MATERIAL_LOC_DESC", i));
						mlTParm.setData("ELETAG_CODE", xmlParm.getData(
								"ELETAG_CODE", i));
						mlTParm.setData("OPT_USER", Operator.getID());
						mlTParm.setData("OPT_TERM", Operator.getIP());
						// 先查询部门的药品是否存在，不存在 INSERT OR UPDATE
						TParm stockParm = new TParm(
								TJDODBTool.getInstance()
										.select(
												INDSQL.getINDStockM(orgCode,
														orderCode)));
						String sql = "";
						if (null == stockParm || stockParm.getCount() < 1) {
							sql = INDSQL.onSaveMatLocStockM(mlTParm);
						} else {
							sql = INDSQL.onUpdateMatLocStockM(mlTParm);
						}
						resultTParm = new TParm(TJDODBTool.getInstance()
								.update(sql));
						if (resultTParm.getErrCode() < 0) {
							err("ERR:" + resultTParm.getErrCode()
									+ resultTParm.getErrText()
									+ resultTParm.getErrName());
							break;
						}
						// 修改药库料位
						String updateSql = INDSQL
								.onUpdateIndStcokMaterialLocCode(orgCode,
										orderCode, materialLocCode);
						resultTParm = new TParm(TJDODBTool.getInstance()
								.update(updateSql));
						if (resultTParm.getErrCode() < 0) {
							err("ERR:" + resultTParm.getErrCode()
									+ resultTParm.getErrText()
									+ resultTParm.getErrName());
							break;
						}

					}
					if (resultTParm.getErrCode() < 0) {
						err("ERR:" + resultTParm.getErrCode()
								+ resultTParm.getErrText()
								+ resultTParm.getErrName());
						messageBox("保存失败");
						return;
					}
					messageBox("导入成功");
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("库存部门不能为空");
			return false;
		}
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("药品名称不能为空");
			return false;
		}

		return true;
	}

	public void onBarcode() {
		TTable t = this.getTable("TABLE_M");
		TParm rowParm = t.getParmValue();
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择要打印条码的记录");
			return;
		}
		String containerId = t.getItemString(row, "MATERIAL_LOC_CODE");
		// String containerName = t.getItemString(row, "CONTAINER_DESC");
		String orderName = t.getItemString(row, "ORDER_DESC");
		String specification = rowParm.getValue("SPECIFICATION", row);
		TParm printData = new TParm();
		printData.setData("BAR_CODE", "TEXT", containerId);
		printData.setData("ORDER_DESC", "TEXT", orderName);
		printData.setData("CONTAINER_CODE", "TEXT", "");
		printData.setData("SPECIFICATION", "TEXT", specification);
		printData.setData("GOODS_DESC", "TEXT", rowParm.getValue("GOODS_DESC",
				row));
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\SPCStockM.jhw",
				printData);
	}

	/**
	 * 电子标签刷新库存
	 */
	public void onRefresh() {
		int row = table_m.getSelectedRow();
		if (row < 0) {
			messageBox("请选中药品，再进行刷新库存操作");
			return;
		}
		String eletagCode = "";
		String orgCode = "";
		String orderCode = "";
		/**
		 * 调用电子标签
		 */
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		orderCode = this.getValueString("ORDER_CODE");
		eletagCode = this.getValueString("ELETAG_CODE");
		orgCode = this.getValueString("ORG_CODE");
		// 调用电子标签接口显示库存与闪烁
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE", orderCode);
		inParm.setData("ORG_CODE", orgCode);
		TParm outParm = new TParm(TJDODBTool.getInstance().select(
				SPCSQL.getQty(inParm)));
		String orderDesc = outParm.getValue("ORDER_DESC", 0);
		String spec = outParm.getValue("SPECIFICATION");
		String qty = outParm.getValue("QTY", 0);
		// 如果电子标签eletagCode为空时，不调用电子标签接口
		if (eletagCode != null && !eletagCode.equals("")) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("ProductName", orderDesc);
			map.put("SPECIFICATION", spec);
			map.put("NUM", qty);
			map.put("TagNo", eletagCode);
			map.put("Light", 3);
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
		}
		if (list != null && list.size() > 0) {
			try {
				String url = Constant.LABELDATA_URL;
				System.out.println("URL---:"+url);
				LabelControl.getInstance().sendLabelDate(list, url);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("调用电子标签服务失败");
			}
		}
	}

	/**
	 * 查询住院药房AP_REGION
	 */
	private TParm queryApRegion() {
		String org_code = this.getValueString("ORG_CODE");
		String sql = "SELECT AP_REGION FROM IND_LABEL WHERE LABEL_ID='"
				+ org_code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

}
