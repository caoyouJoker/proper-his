package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.sys.SYSSQL;
import jdo.spc.INDTool;
import jdo.spc.IndAgentTool;
import jdo.spc.IndPurorderMTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndVerifyinObserver;
import com.javahis.util.StringUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: 验收入库Control
 * </p>
 * 
 * <p>
 * Description: 验收入库Control
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class INDVerifyinNoSpcControl extends TControl {

	// 操作标记
	private String action = "insert";
	// 细项序号
	private int seq;

	private TParm resultParm;

	private Map map;

	// 赠与权限
	private boolean gift_flg = true;

	// 全部部门权限
	private boolean dept_flg = true;

	// 审核入库权限
	private boolean check_flg = true;

	// 打印输出格式
	java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
	java.text.DecimalFormat df3 = new java.text.DecimalFormat("##########0.000");
	java.text.DecimalFormat df4 = new java.text.DecimalFormat("##########0.0000");
	// luhai 2012-2-28 加入一位的格式化
	java.text.DecimalFormat df5 = new java.text.DecimalFormat("##########0");

	public INDVerifyinNoSpcControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		initPage();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// 传入参数集
		TParm parm = new TParm();
		if (!"".equals(getValueString("ORG_CODE"))) {
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		} else {
			// 没有全部部门权限
			if (!dept_flg) {
				this.messageBox("请选择查询部门");
				return;
			}
		}
		// this.messageBox(getValueString("SUP_CODE"));
		if (!"".equals(getValueString("SUP_CODE"))) {
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		}
		if (!"".equals(getValueString("VERIFYIN_NO"))) {
			parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
		}
		if (!"".equals(getValueString("START_DATE")) && !"".equals(getValueString("END_DATE"))) {
			parm.setData("START_DATE", getValue("START_DATE"));
			parm.setData("END_DATE", getValue("END_DATE"));
		}
		if (getRadioButton("TOXIC_DRUG").isSelected()) {// 麻精
			parm.setData("DRUG_CATEGORY", "2");
		} else {
			parm.setData("DRUG_CATEGORY", "1");
		}
		// zhangyong20110517
		parm.setData("REGION_CODE", Operator.getRegion());
		if (parm == null) {
			return;
		}
		// 返回结果集
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onQueryM", parm);
		// System.out.println("------------query: "+result);
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
			// 已审核
			for (int i = result.getCount() - 1; i >= 0; i--) {
				if ("".equals(result.getValue("CHECK_USER", i))) {
					result.removeRow(i);
				}
			}
		} else {
			// 未审核
			for (int i = result.getCount() - 1; i >= 0; i--) {
				if (!"".equals(result.getValue("CHECK_USER", i)) || result.getData("CHECK_USER", i) != null) {
					result.removeRow(i);
				}
			}
		}
		if (result == null || result.getCount("VERIFYIN_NO") <= 0) {
			getTable("TABLE_M").removeRowAll();
			getTable("TABLE_D").removeRowAll();
			this.messageBox("没有查询数据");
			return;
		}
		// 填充表格TABLE_M
		TTable table_M = getTable("TABLE_M");
		table_M.setParmValue(result);
		((TMenuItem) getComponent("save")).setEnabled(true);		
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		getRadioButton("UPDATE_FLG_B").setSelected(true);
		String clearString = "START_DATE;END_DATE;VERIFYIN_DATE;ORG_CODE;VERIFYIN_NO;" + "PLAN_NO;REASON_CHN_DESC;CHECK_FLG;DESCRIPTION;"
				+ "SELECT_ALL;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
		this.clearValue(clearString);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("VERIFYIN_DATE", date);
		// 画面状态
		((TMenuItem) getComponent("save")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		((TMenuItem) getComponent("export")).setEnabled(true);
		getComboBox("ORG_CODE").setEnabled(true);
		getTextFormat("SUP_CODE").setEnabled(true);
		// getCheckBox("CHECK_FLG").setEnabled(false);
		getTextField("VERIFYIN_NO").setEnabled(true);
		getTable("TABLE_M").setSelectionMode(0);
		getTable("TABLE_M").removeRowAll();
		getTable("TABLE_D").setSelectionMode(0);
		getTable("TABLE_D").removeRowAll();
		action = "insert";
		this.setCheckFlgStatus(action);
		resultParm = null;
		TTable table_D = getTable("TABLE_D");
		TDS tds = new TDS();
		tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
		tds.retrieve();
		// 观察者
		IndVerifyinObserver obser = new IndVerifyinObserver();
		tds.addObserver(obser);
		table_D.setDataStore(tds);
		table_D.setDSValue();
		getTextFormat("SUP_CODE").setText("");
		getTextFormat("SUP_CODE").setValue("");
		getRadioButton("GEN_DRUG").setEnabled(true);
		getRadioButton("TOXIC_DRUG").setEnabled(true);
		((TMenuItem) getComponent("export")).setEnabled(true);
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		map = new HashMap();
		// 传入参数集
		TParm parm = new TParm();
		// 出库单号
		String dispenseNo = "";
		if (getRadioButton("GEN_DRUG").isSelected()) {// 非麻精
			parm.setData("DRUG_CATEGORY", "1");
		} else {// 麻精
			parm.setData("DRUG_CATEGORY", "2");
		}
		// 返回结果集
		TParm result = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		if ("insert".equals(action)) {
			if (!CheckDataD()) {
				return;
			}
			// 主项信息
			// 验收日期
			parm.setData("VERIFYIN_DATE", getValue("VERIFYIN_DATE"));
			// 验收人员
			parm.setData("VERIFYIN_USER", Operator.getID());
			// 验收部门
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
			// 供应厂商
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			// 备注
			parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
			// 暂缓原因
			parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
			// 计划单号
			parm.setData("PLAN_NO", getValueString("PLAN_NO"));
			// OPT_USER,OPT_DATE,OPT_TERM
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", date);
			parm.setData("OPT_TERM", Operator.getIP());
//			parm.setData("SPC_BOX_BARCODE", getValueString("SPC_BOX_BARCODE"));
			parm.setData("SPC_BOX_BARCODE", "0");
			// zhangyong20110517
			parm.setData("REGION_CODE", Operator.getRegion());
			// 审核
			boolean check_flg = false;
			if ("Y".equals(getValueString("CHECK_FLG"))) {
				// 审核人员
				parm.setData("CHECK_USER", Operator.getID());
				// 审核时间
				parm.setData("CHECK_DATE", date);
				check_flg = true;
			} else {
				parm.setData("CHECK_USER", "");
				parm.setData("CHECK_DATE", tnull);
				check_flg = false;
			}
			// 细项表格
			TTable table_D = getTable("TABLE_D");
			table_D.acceptText();
			TDataStore dataStore = table_D.getDataStore();

			// 库存是否可异动状态判断
			String org_code = parm.getValue("ORG_CODE");
			if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
				this.messageBox("药房批次过帐中则提示请先手动做日结");
				return;
			}

			// 细项信息
			// 验收入库单号
			String verifyin_no = "";
			// 所有新增行
			int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
			// 验收量
			double qty = 0.00;
			// 赠送量
			double g_qty = 0.00;
			// 订购量
			double pur_qty = 0.00;
			// 库存量
			double stock_qty = 0.00;
			// 状态
			String update_flg = "0";
			// 药品代码
			String order_code = "";
			// 出入库数量转换率
			TParm getTRA = new TParm();
			// 出入库效期批号者
			TParm getSEQ = new TParm();
			// 进货转换率
			double s_qty = 0.00;
			// 库存转换率
			double d_qty = 0.00;
			// 批号
			String batch_no = "";
			// 批次序号
			int batch_seq = 1;
			// 有效期
			String valid_date = "";
			// 更新或新增IND_STOCK的FLG->U:更新;I,新增
			String flg = "";
			// 零售价格
			double retail = 0.00;
			// 验收单价
			double verprice = 0.00;
			// 累计质量扣款金额
			double deduct_atm = 0.00;
			// 库存平均价
			double stock_price = 0.00;
			// 订购单号
			String pur_no = "";
			// 订购单号序号
			int pur_no_seq = 0;
			// 得到IND_SYSPARM信息
			result = IndSysParmTool.getInstance().onQuery();
			//周转箱
			String spcBoxCode = "";
			// 购入单价自动维护
			boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
			if (reuprice_flg) {
				// 判断验收单价是否与合约单价相同
				boolean agent_flg = false;
				for (int i = 0; i < newrows.length; i++) {
					// 判断该行的启用状态
					if (!dataStore.isActive(newrows[i])) {
						continue;
					}
					order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
					verprice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE");
					String sup_code = this.getValueString("SUP_CODE");
					TParm inparm = new TParm();
					inparm.setData("SUP_CODE", sup_code);
					inparm.setData("ORDER_CODE", order_code);
					TParm agent_parm = IndAgentTool.getInstance().onQuery(inparm);
					if (agent_parm.getDouble("CONTRACT_PRICE", 0) != verprice) {
						agent_flg = true;
					}
				}
				if (agent_flg) {
					if (this.messageBox("提示", "是否自动维护购入单价", 2) == 0) {
						parm.setData("REUPRICE_FLG", "Y");
					} else {
						parm.setData("REUPRICE_FLG", "N");
					}
				} else {
					parm.setData("REUPRICE_FLG", "N");
				}
			} else {
				parm.setData("REUPRICE_FLG", "N");
			}

			// 判断是否自动将最后一次验收入库单价维护至药品基本档中批发价
			parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");

			// 细项信息
			int count = 0;
			// 验收入库单号
			verifyin_no = SystemTool.getInstance().getNo("ALL", "IND", "IND_VERIFYIN", "No");
			seq = 1;
			String material_loc_code = "";
			for (int i = 0; i < newrows.length; i++) {
				// 判断该行的启用状态
				if (!dataStore.isActive(newrows[i])) {
					continue;
				}
				// // 根据不同的发票号生成不同的入库单号
				// verifyin_no =
				// getVerifyinNO(dataStore.getItemString(newrows[i],
				// "INVOICE_NO"));
				// 入库单号集合
				parm.setData("VERIFYIN", count, verifyin_no);
				// 药品代码集合
				order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
				parm.setData("ORDER_CODE", count, order_code);
				// 出入库数量转换率
				getTRA = INDTool.getInstance().getTransunitByCode(order_code);
				if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
					this.messageBox("药品" + order_code + "转换率错误");
					return;
				}
				// 进货转换率集合
				s_qty = getTRA.getDouble("STOCK_QTY", 0);
				parm.setData("STOCK_QTY", count, s_qty);
				// 库存转换率集合
				d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
				parm.setData("DOSAGE_QTY", count, d_qty);
				// 库存量
				stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
				parm.setData("QTY", count, stock_qty);
				// 批号集合
				batch_no = dataStore.getItemString(newrows[i], "BATCH_NO");
				parm.setData("BATCH_NO", count, batch_no);
				// 有效期
				valid_date = StringTool.getString(dataStore.getItemTimestamp(newrows[i], "VALID_DATE"), "yyyy/MM/dd");
				parm.setData("VALID_DATE", count, StringTool.getTimestamp(valid_date, "yyyy/MM/dd"));
				// 出入库效期批号者
				getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date);
				if (getSEQ.getErrCode() < 0) {
					this.messageBox("药品" + order_code + "批次序号错误");
					return;
				}
				if (getSEQ.getCount("BATCH_SEQ") > 0) {
					flg = "U";
					// 该批次药品存在
					batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
				} else {
					flg = "I";
					// 该批次药品不存在,抓取该库存药品最大+1批次序号新增
					getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
					if (getSEQ.getErrCode() < 0) {
						this.messageBox("药品" + order_code + "批次序号错误");
						return;
					}
					// 最大+1批次序号
					batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
					material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
					// this.messageBox(material_loc_code);
				}
				// 出入库效期批号者集合
				parm.setData("BATCH_SEQ", count, batch_seq);
				// 料位
				parm.setData("MATERIAL_LOC_CODE", count, material_loc_code);
				// 更新或新增IND_STOCK的FLG集合
				parm.setData("UI_FLG", count, flg);
				// 验收量集合
				qty = dataStore.getItemDouble(newrows[i], "VERIFYIN_QTY");
				parm.setData("VERIFYIN_QTY", count, qty);
				// 赠送量集合
				g_qty = dataStore.getItemDouble(newrows[i], "GIFT_QTY");
				parm.setData("GIFT_QTY", count, g_qty);
				// 计划量集合
				pur_qty = resultParm.getDouble("PURORDER_QTY", newrows[i]);
				parm.setData("PURORDER_QTY", count, pur_qty);
				// 零售价格集合
				retail = dataStore.getItemDouble(newrows[i], "RETAIL_PRICE");
				parm.setData("RETAIL_PRICE", count, retail);
				// 验收价格集合
				// zhangyong20091014 begin 修改验收价格的单位为配药单位
				verprice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE") / d_qty / s_qty;
				parm.setData("VERIFYIN_PRICE", count, StringTool.round(verprice, 4));
				// zhangyong20091014 end
				// 验收序号集合
				parm.setData("SEQ_NO", count, seq + count);
				// 累计质量扣款金额集合
				deduct_atm = dataStore.getItemDouble(newrows[i], "QUALITY_DEDUCT_AMT");
				parm.setData("QUALITY_DEDUCT_AMT", count, deduct_atm);
				// 订购单号集合
				pur_no = dataStore.getItemString(newrows[i], "PURORDER_NO");
				parm.setData("PURORDER_NO", count, pur_no);
				// 订购单号序号集合
				pur_no_seq = dataStore.getItemInt(newrows[i], "PURSEQ_NO");
				parm.setData("PURSEQ_NO", count, pur_no_seq);
				// 库存平均价集合
				stock_price = resultParm.getDouble("STOCK_PRICE", newrows[i]);
				parm.setData("STOCK_PRICE", count, stock_price);
				// 累计验收数
				parm.setData("ACTUAL_QTY", count, resultParm.getDouble("ACTUAL_QTY", newrows[i]));

				// 验收单号
				dataStore.setItem(newrows[i], "VERIFYIN_NO", verifyin_no);
				// 序号
				dataStore.setItem(newrows[i], "SEQ_NO", seq + count);

				// 状态
				if (check_flg && qty == pur_qty) {
					update_flg = "3";
				} else if (check_flg && qty < pur_qty) {
					update_flg = "1";
				} else if (!check_flg) {
					update_flg = "0";
				}
				dataStore.setItem(newrows[i], "UPDATE_FLG", update_flg);
				// System.out.println("I_UPDATE_FLG:" + update_flg);
				// OPT_USER,OPT_DATE,OPT_TERM
				dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(newrows[i], "OPT_DATE", date);
				dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
				dataStore.setItem(newrows[i], "BATCH_SEQ", batch_seq);// add by
																		// liyh
																		// 20120613
				//by liyh 20121128 周转箱
				spcBoxCode = dataStore.getItemString(newrows[i], "SPC_BOX_BARCODE");
//				System.out.println("---------spcBoxCode: "+spcBoxCode);
				parm.setData("SPC_BOX_BARCODE", count,spcBoxCode);
				dataStore.setItem(newrows[i], "SPC_BOX_BARCODE", spcBoxCode);
				count++;
			}
			// 得到dataStore的更新SQL
			String updateSql[] = dataStore.getUpdateSQL();
		
			 for (int i = 0; i < updateSql.length; i++) { 
				 System.out.println(i + ":" + updateSql[i]); 
			}
			
			parm.setData("UPDATE_SQL", updateSql);

			// 执行数据新增(验收主项和验收细项)
			result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onInsert", parm);

			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");

			this.setValue("VERIFYIN_NO", verifyin_no);

			// 根据验收单号判断验收入库量更新入库状态
			updateIndVerifinDUpdateFlg(verifyin_no);

		} else if ("updateM".equals(action)) {
/*			if (getRadioButton("GEN_DRUG").isSelected()) {//普药不入库
				return;
			}*/
			// 更新主项信息
			if (!CheckDataM()) {
				return;
			}
			// 画面值取得
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			parm.setData("PLAN_NO", getValue("PLAN_NO"));
			parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
			parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
			// System.out.println("------------------------");
			String check_flg = getValueString("CHECK_FLG");
			if ("Y".equals(check_flg)) {
				// 审核人员
				parm.setData("CHECK_USER", Operator.getID());
				// 审核时间
				parm.setData("CHECK_DATE", date);
				// 细项表格
				TTable table_D = getTable("TABLE_D");
				table_D.acceptText();
				TDataStore dataStore = table_D.getDataStore();

				// 库存是否可异动状态判断
				String org_code = parm.getValue("ORG_CODE");
				if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
					this.messageBox("药房批次过帐中则提示请先手动做日结");
					return;
				}

				// 验收量
				double qty = 0.00;
				// 赠送量
				double g_qty = 0.00;
				// 订购量
				double pur_qty = 0.00;
				// 库存量
				double stock_qty = 0.00;
				// 药品代码
				String order_code = "";
				// 出入库数量转换率
				TParm getTRA = new TParm();
				// 出入库效期批号者
				TParm getSEQ = new TParm();
				// 进货转换率
				double s_qty = 0.00;
				// 库存转换率
				double d_qty = 0.00;
				// 批号
				String batch_no = "";
				// 批次序号
				int batch_seq = 1;
				// 有效期
				String valid_date = "";
				// 更新或新增IND_STOCK的FLG->U:更新;I,新增
				String flg = "";
				// 零售价格
				double retail = 0.00;
				// 验收单价
				double verprice = 0.00;
				// 累计质量扣款金额
				double deduct_atm = 0.00;
				// 库存平均价
				double stock_price = 0.00;
				// 订购单号
				String pur_no = "";
				// 订购单号序号
				int pur_no_seq = 0;
				//周转箱
				String spcBoxCode = "";
				String verifyin_no = this.getValueString("VERIFYIN_NO");

				// 得到IND_SYSPARM信息
				result = IndSysParmTool.getInstance().onQuery();
				// 购入单价自动维护
				boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
				if (reuprice_flg) {
					// 判断验收单价是否与合约单价相同
					boolean agent_flg = false;
					for (int i = 0; i < table_D.getRowCount(); i++) {
						// 判断该行的启用状态
						if (!dataStore.isActive(i)) {
							continue;
						}
						order_code = dataStore.getItemString(i, "ORDER_CODE");
						verprice = dataStore.getItemDouble(i, "VERIFYIN_PRICE");
						String sup_code = this.getValueString("SUP_CODE");
						TParm inparm = new TParm();
						inparm.setData("SUP_CODE", sup_code);
						inparm.setData("ORDER_CODE", order_code);
						TParm agent_parm = IndAgentTool.getInstance().onQuery(inparm);
						if (agent_parm.getDouble("CONTRACT_PRICE", 0) != verprice) {
							agent_flg = true;
						}
					}
					if (agent_flg) {
						if (this.messageBox("提示", "是否自动维护购入单价", 2) == 0) {
							parm.setData("REUPRICE_FLG", "Y");
						} else {
							parm.setData("REUPRICE_FLG", "N");
						}
					} else {
						parm.setData("REUPRICE_FLG", "N");
					}
				} else {
					parm.setData("REUPRICE_FLG", "N");
				}

				// 判断是否自动将最后一次验收入库单价维护至药品基本档中批发价
				parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");

				// 细项信息
				String material_loc_code = "";
				for (int i = 0; i < table_D.getRowCount(); i++) {
					// 入库单号集合
					parm.setData("VERIFYIN", i, verifyin_no);
					// 药品代码集合
					order_code = dataStore.getItemString(i, "ORDER_CODE");
					parm.setData("ORDER_CODE", i, order_code);
					// 出入库数量转换率
					getTRA = INDTool.getInstance().getTransunitByCode(order_code);
					if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
						this.messageBox("药品" + order_code + "转换率错误");
						return;
					}
					// 进货转换率集合
					s_qty = getTRA.getDouble("STOCK_QTY", 0);
					parm.setData("STOCK_QTY", i, s_qty);
					// 库存转换率集合
					d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
					parm.setData("DOSAGE_QTY", i, d_qty);
					// 库存量
					stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
					parm.setData("QTY", i, stock_qty);
					// 批号集合
					batch_no = dataStore.getItemString(i, "BATCH_NO");
					parm.setData("BATCH_NO", i, batch_no);
					// 有效期
					valid_date = StringTool.getString(dataStore.getItemTimestamp(i, "VALID_DATE"), "yyyy/MM/dd");
					// System.out.println("0000" + valid_date);
					parm.setData("VALID_DATE", i, valid_date);
					//20121128 liyh添加周转箱
					spcBoxCode = dataStore.getItemString(i, "SPC_BOX_BARCODE");
					parm.setData("SPC_BOX_BARCODE", i, spcBoxCode);
					// *************************************************************
					// luahi 2012-01-10 add 加入批次序号选取条件加入验收价格条件begin
					// *************************************************************
					// 出入库效期批号者
					// getSEQ =
					// IndStockDTool.getInstance().onQueryStockBatchSeq(
					// org_code, order_code, batch_no, valid_date);
					// add by liyh 20120614 在IND_STOCK
					// 里查询药品验收价格时，应该是片/支的价格（最小单位的价格） start
					double verpriceD = dataStore.getItemDouble(i, "VERIFYIN_PRICE") / d_qty / s_qty;
					verpriceD = StringTool.round(verpriceD, 4);
					String verpriceDStr = String.valueOf(verpriceD);
					// String verifyInPrice = dataStore.getItemString(i,
					// "VERIFYIN_PRICE");
					// add by liyh 20120614 在IND_STOCK
					// 里查询药品验收价格时，应该是片/支的价格（最小单位的价格） end
					// 出入库效期批号者
					getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date, verpriceDStr);//
					// *************************************************************
					// luahi 2012-01-10 add 加入批次序号选取条件加入验收价格条件end
					// *************************************************************
					if (getSEQ.getErrCode() < 0) {
						this.messageBox("药品" + order_code + "批次序号错误");
						return;
					}
					if (getSEQ.getCount("BATCH_SEQ") > 0) {
						flg = "U";
						// 该批次药品存在
						batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
					} else {
						flg = "I";
						// 该批次药品不存在,抓取该库存药品最大+1批次序号新增
						getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
						if (getSEQ.getErrCode() < 0) {
							this.messageBox("药品" + order_code + "批次序号错误");
							return;
						}
						// 最大+1批次序号
						// System.out.println("SEQ:"
						// + getSEQ.getInt("BATCH_SEQ", 0));
						// System.out.println("getSEQ:" + getSEQ);
						batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
						material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
					}
					// 出入库效期批号者集合
					parm.setData("BATCH_SEQ", i, batch_seq);
					// 料位
					parm.setData("MATERIAL_LOC_CODE", i, material_loc_code);
					// 更新或新增IND_STOCK的FLG集合
					parm.setData("UI_FLG", i, flg);
					// 验收量集合
					qty = dataStore.getItemDouble(i, "VERIFYIN_QTY");
					parm.setData("VERIFYIN_QTY", i, qty);
					// 赠送量集合
					g_qty = dataStore.getItemDouble(i, "GIFT_QTY");
					parm.setData("GIFT_QTY", i, g_qty);

					// 零售价格集合
					retail = dataStore.getItemDouble(i, "RETAIL_PRICE");
					parm.setData("RETAIL_PRICE", i, retail);
					// 验收价格集合
					// zhangyong20091014 begin 修改验收价格的单位为配药单位
					verprice = dataStore.getItemDouble(i, "VERIFYIN_PRICE") / d_qty / s_qty;
					parm.setData("VERIFYIN_PRICE", i, StringTool.round(verprice, 4));
					// zhangyong20091014 end
					// 验收序号集合
					int seq_no = dataStore.getItemInt(i, "SEQ_NO");
					parm.setData("SEQ_NO", i, seq_no);
					// 累计质量扣款金额集合
					deduct_atm = dataStore.getItemDouble(i, "QUALITY_DEDUCT_AMT");
					parm.setData("QUALITY_DEDUCT_AMT", i, deduct_atm);
					// 订购单号集合
					pur_no = dataStore.getItemString(i, "PURORDER_NO");
					parm.setData("PURORDER_NO", i, pur_no);
					// 订购单号序号集合
					pur_no_seq = dataStore.getItemInt(i, "PURSEQ_NO");
					parm.setData("PURSEQ_NO", i, pur_no_seq);

/*					TParm inparm = new TParm();
					inparm.setData("PURORDER_NO", pur_no);
					inparm.setData("SEQ_NO", pur_no_seq);
					result = IndPurorderMTool.getInstance().onQueryDone(inparm);
					if (result.getCount() == 0 || result.getErrCode() < 0) {
						return;
					}*/
					// 订购量集合
					pur_qty = result.getDouble("PURORDER_QTY", 0);
					parm.setData("PURORDER_QTY", i, pur_qty);
					// 库存平均价集合
					stock_price = result.getDouble("STOCK_PRICE", 0);
					parm.setData("STOCK_PRICE", i, stock_price);
					// 累计验收数
					parm.setData("ACTUAL_QTY", i, result.getDouble("ACTUAL_QTY", i));
					// 状态
					String update_flg = "0";
					if ("Y".equals(check_flg) && qty == pur_qty) {
						update_flg = "3";
					} else if ("Y".equals(check_flg) && qty < pur_qty) {
						update_flg = "1";
					} else if (!"Y".equals(check_flg)) {
						update_flg = "0";
					}
					// System.out.println("U_UPDATE_FLG:" + update_flg);
					dataStore.setItem(i, "UPDATE_FLG", update_flg);
					// OPT_USER,OPT_DATE,OPT_TERM
					dataStore.setItem(i, "OPT_USER", Operator.getID());
					dataStore.setItem(i, "OPT_DATE", date);
					dataStore.setItem(i, "OPT_TERM", Operator.getIP());
					// luhai 2012-01-10 modify 验收审核入库时记录BATCH_SEQ begin
					dataStore.setItem(i, "BATCH_SEQ", batch_seq);
					// luhai 2012-01-10 modify 验收审核入库时记录BATCH_SEQ end
				}
				parm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
			} else {
				parm.setData("CHECK_USER", "");
				parm.setData("CHECK_DATE", tnull);
			}
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", date);
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("REGION_CODE", Operator.getRegion());
			parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
			// 执行数据更新
			result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onUpdate", parm);
			// 保存判断
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");

			if ("2".equals(parm.getValue("DRUG_CATEGORY")) && "Y".equals(getValueString("CHECK_FLG"))) {// 如果是审核
																										// 且
																										// 是麻精
																										// 则自动生成请领单和出库单
				dispenseNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_DISPENSE", "No");
				parm.setData("DISPENSE_NO", dispenseNo);
				// 执行数据更新
				result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onSaveReqeustAndDispense", parm);
			}
			// 根据验收单号判断验收入库量更新入库状态
			updateIndVerifinDUpdateFlg(getValueString("VERIFYIN_NO"));
			// 当入库的药品为中药时判断是否更新生产厂商
			updateManCode(getValueString("VERIFYIN_NO"));
			// 判断中草药入库修改零售价
			// updateGrpricePrice(getValueString("VERIFYIN_NO"));

			
			if ("Y".equals(getValueString("CHECK_FLG"))) {// 如果是审核
				// 打印验收入库单
				this.onPrint();
				if("2".equals(parm.getValue("DRUG_CATEGORY"))){
					this.onPrintDispendseOut(dispenseNo);
				}
			}
		} else if ("updateD".equals(action)) {
			// 更新明细信息
			TTable table_D = getTable("TABLE_D");
			table_D.acceptText();
			TDataStore dataStore = table_D.getDataStore();
			// 填充resultParm
			TParm inparm = new TParm();
			inparm.setData("PURORDER_NO", dataStore.getItemString(0, "PURORDER_NO"));
			inparm.setData("SEQ_NO", dataStore.getItemInt(0, "PURSEQ_NO"));
			resultParm = IndPurorderMTool.getInstance().onQueryDone(inparm);
			// 细项检查
			// if (!CheckDataD()) {
			// return;
			// }
			// 获得全部改动的行号
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			// 给固定数据配数据
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			// 细项保存判断
			if (!table_D.update()) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			table_D.setDSValue();
			return;
		}
		this.onClear();
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
			this.messageBox("单据已完成不可删除");
		} else {
			if (getTable("TABLE_M").getSelectedRow() > -1) {
				if (this.messageBox("删除", "确定是否删除验收单", 2) == 0) {
					TTable table_D = getTable("TABLE_D");
					TParm parm = new TParm();
					// 细项信息
					if (table_D.getRowCount() > 0) {
						table_D.removeRowAll();
						String deleteSql[] = table_D.getDataStore().getUpdateSQL();
						parm.setData("DELETE_SQL", deleteSql);
					}
					// 主项信息
					parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
					TParm result = new TParm();
					result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onDeleteM", parm);
					// 删除判断
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("删除失败");
						return;
					}
					getTable("TABLE_M").removeRow(getTable("TABLE_M").getSelectedRow());
					this.messageBox("删除成功");
				}
			} else {
				if (this.messageBox("删除", "确定是否删除验收细项", 2) == 0) {
					TTable table_D = getTable("TABLE_D");
					int row = table_D.getSelectedRow();
					table_D.removeRow(row);
					// 细项保存判断
					if (!table_D.update()) {
						messageBox("E0001");
						return;
					}
					messageBox("P0001");
					table_D.setDSValue();
				}
			}
		}
	}

	/**
	 * 打开未验收明细
	 */
	public void onExport() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("验收部门不能为空");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		Object result = openDialog("%ROOT%\\config\\spc\\INDUnVerifyin.x", parm);
		if (result != null) {
			TParm addParm = (TParm) result;
			if (addParm == null) {
				return;
			}
			resultParm = (TParm) result;
			// System.out.println("RESULT" + resultParm);
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			TNull tnull = new TNull(Timestamp.class);
			// 供应厂商
			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// 计划单号
			this.setValue("PLAN_NO", addParm.getValue("PLAN_NO", 0));
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				int row = table.addRow();
				// 填充DATESTORE
				// ORDER_CODE
				table.getDataStore().setItem(i, "ORDER_CODE", addParm.getValue("ORDER_CODE", i));
				// 填充TABLE_D数据
				// 验收量
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				actual_qty = addParm.getDouble("ACTUAL_QTY", i);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty - actual_qty);
				// 赠送量
				table.setItem(row, "GIFT_QTY", addParm.getData("GIFT_QTY", i));
				// 进货单位
				table.setItem(row, "BILL_UNIT", addParm.getValue("BILL_UNIT", i));
				// 验收单价
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
				table.setItem(row, "VERIFYIN_PRICE", puroder_price);
				// 进货金额
				table.setItem(row, "INVOICE_AMT", StringTool.round(puroder_price * (purorder_qty - actual_qty), 2));
				// 零售价
				retail_price = addParm.getDouble("RETAIL_PRICE", i);
				table.setItem(row, "RETAIL_PRICE", retail_price);
				// 订购单号
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// 订购单号序号
				table.setItem(row, "PURSEQ_NO", addParm.getData("SEQ_NO", i));
				// 累计验收数
				table.setItem(row, "ACTUAL", addParm.getData("ACTUAL_QTY", i));
				// 发票日期
				table.setItem(row, "INVOICE_DATE", tnull);
				// 判断药品类型:中草，中成，西药
				TParm pha_parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(addParm.getValue("ORDER_CODE", i))));
				// 根据药品类型给定效期
				if ("G".equals(pha_parm.getValue("PHA_TYPE", 0))) {
					table.setItem(row, "VALID_DATE", "9999/12/31");
				} else {
					table.setItem(row, "VALID_DATE", tnull);
				}
				// 生产厂商
				table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			// zhangyong20110517
			((TMenuItem) getComponent("export")).setEnabled(false);
		}
	}

	/**
	 * 导入国药出货单
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpXML() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("验收部门不能为空");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		if (getRadioButton("GEN_DRUG").isSelected()) {// 非麻精
			parm.setData("DROG_TYPE", "N");
		} else {// 麻精
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
				addParm = (TParm) FileUtils.readXMLFileP(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultParm = (TParm) addParm;
//			System.out.println("---------------in   addpamr: "+addParm);
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			// 供应厂商
			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// 计划单号
			this.setValue("PLAN_NO", "");

			getRadioButton("GEN_DRUG").setEnabled(false);
			getRadioButton("TOXIC_DRUG").setEnabled(false);
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				int row = table.addRow();

				TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(addParm.getValue("ORDER_CODE", i))));
				// System.out.println("--phaParm:"+phaParm);
				// 填充DATESTORE
				resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
				// ORDER_CODE
				table.getDataStore().setItem(i, "ORDER_CODE", addParm.getValue("ORDER_CODE", i));
				// 填充TABLE_D数据
				// 验收量
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty);
				// 赠送量
				table.setItem(row, "GIFT_QTY", 0);
				// 进货单位
				// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
				// 0));
				table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));
				// 验收单价
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
				table.setItem(row, "VERIFYIN_PRICE", puroder_price);
				// 进货金额
				table.setItem(row, "INVOICE_AMT", StringTool.round(puroder_price * purorder_qty, 2));
				// 零售价
				retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
				table.setItem(row, "RETAIL_PRICE", retail_price);
				// 订购单号
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// 订购单号序号
				table.setItem(row, "PURSEQ_NO", addParm.getData("PURSEQ_NO", i));
				// 累计验收数
				table.setItem(row, "ACTUAL", 0);

				String time1 = addParm.getData("INVOICE_DATE", i) + "";
				time1 = time1.replaceAll("-", "/");
				// 发票日期
				table.setItem(row, "INVOICE_DATE", time1);
				String validDate = addParm.getData("VALID_DATE", i) + "";
				validDate = validDate.replaceAll("-", "/");
				table.setItem(row, "REASON_CHN_DESC", "VER01");
				// 效期
				table.setItem(row, "VALID_DATE", validDate);
				// 生产厂商
				table.setItem(row, "MAN_CODE", phaParm.getData("MAN_CODE", 0));
				// 发票号
				table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
				// 批号
				table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
				// 装箱单号
				String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
				table.setItem(row, "SPC_BOX_BARCODE",boxCode);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			getCheckBox("SELECT_ALL").setSelected(true);
			onCheckSelectAll();
		}
	}
	
	/**
	 * 导入国药-发票信息
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpInvoiceFromXML() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("验收部门不能为空");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		if (getRadioButton("GEN_DRUG").isSelected()) {// 非麻精
			parm.setData("DROG_TYPE", "N");
		} else {// 麻精
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
				addParm = (TParm) FileUtils.readExcelFile(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("---------------in   addpamr: "+addParm);
			TTable table = getTable("TABLE_D");
//			System.out.println("11--------"+table.getItemString(0, "ORDER_CODE"));
//			System.out.println("--------"+table.getItemString(1, "ORDER_CODE"));
			for (int i = 0; i < table.getRowCount(); i++) {//BILLNO
                String orderCodeD =table.getItemString(i, "ORDER_CODE");
                //国药销售单号
                String purOrderNoD = table.getItemString(i, "PURORDER_NO");
                String verifyinNOD =  table.getItemString(i, "VERIFYIN_NO");
//                System.out.println(i+"---purOrderNoD:"+purOrderNoD);
//                System.out.println(i+"---verifyinNOD:"+verifyinNOD);
//                System.out.println(i+"---orderCodeD:"+orderCodeD);
                for (int j = 0; j < addParm.getCount("ORDER_CODE") ;j++) {
					String orderCodeA = addParm.getValue("ORDER_CODE",j);
//					System.out.println(j+"---orderCodeA:"+orderCodeA);
	                //国药销售单号
	                String purOrderNoA = addParm.getValue("PURORDER_NO", j);
//	                System.out.println(j+"---purOrderNoA:"+purOrderNoA);
	                if(orderCodeD.equals(orderCodeA) && purOrderNoD.equals(purOrderNoA)){
	                	String invoiceNo = addParm.getValue("INVOICE_NO", j);
	                	String invoiceDate = addParm.getValue("INVOICE_DATE", j);
	    				table.setItem(i, "INVOICE_NO", invoiceNo);
	    				table.setItem(i, "INVOICE_DATE", invoiceDate.replaceAll("-", "/"));
//	    				System.out.println("------1160-------sql:"+SPCSQL.updateVerifyin( invoiceNo,invoiceDate , verifyinNOD, orderCodeD, purOrderNoD));
	    				TJDODBTool.getInstance().update(SPCSQL.updateVerifyin( invoiceNo,invoiceDate , verifyinNOD, orderCodeD, purOrderNoD));
	                }
				}
			}
			table.setDSValue();
		}
	}	


	/**
	 * 导入国药出货单
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpExcel() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("验收部门不能为空");
			return;
		}
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("供应厂商不能为空");
			return;
		}		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		String supCode = getValueString("SUP_CODE");
		if (getRadioButton("GEN_DRUG").isSelected()) {// 非麻精
			parm.setData("DROG_TYPE", "N");
		} else {// 麻精
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
//				addParm = (TParm) FileUtils.readXMLFileP(filePath);
				addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
//				System.out.println("---------------parm: "+addParm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultParm = (TParm) addParm;
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			// 供应厂商
//			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// 计划单号
			this.setValue("PLAN_NO", "");

			getRadioButton("GEN_DRUG").setEnabled(false);
			getRadioButton("TOXIC_DRUG").setEnabled(false);
			int rowCount = 0 ;
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
				//先判断ERP_ID是否已经存在 true存在
				boolean flg = isImpERPInfo("","","",erpId);
				if(flg){//如果存在 进行下一个循环
					continue;
				}
				
				int row = table.addRow();
				// ORDER_CODE
				String orderCode = addParm.getValue("ORDER_CODE", i);
				TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
//				System.out.println(i+"--"+orderCode+"--1127-------phaParm:"+phaParm);
				// 填充DATESTORE
				resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
				table.getDataStore().setItem(row, "ORDER_CODE",orderCode);
				table.getDataStore().setItem(row, "ORDER_DESC", phaParm.getValue("ORDER_DESC", 0));
				// 填充TABLE_D数据
				// 验收量
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				// 出入库数量转换率
				TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
				if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
					this.messageBox("药品" + orderCode + "转换率错误");
					return;
				}
				//库存转换率
				int stockQty = getTRA.getInt("STOCK_QTY", 0);	
//				System.out.println("--------stockQty: "+stockQty);
				purorder_qty = purorder_qty * stockQty;
//				System.out.println("--------purorder_qty: "+purorder_qty);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty);
				// 赠送量
				table.setItem(row, "GIFT_QTY", 0);
				// 进货单位
				// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
				// 0));
				table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));

				// 验收单价
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
//				System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
				//查询 供应商的价格
				TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
				if(null != agentParm && agentParm.getCount()>0 ){
					double verifyPrice =  agentParm.getDouble("LAST_VERIFY_PRICE", 0);
					verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
					// 进货金额
					table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
		
				}else{
					//如果次供应商没有代理这个药品则保村
					onSaveAgentInfo(orderCode,StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4),supCode);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
					// 进货金额
					table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));
				}
				// 零售价
				retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
				table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
					
				// 订购单号
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// 订购单号序号
				table.setItem(row, "PURSEQ_NO", addParm.getData("PURSEQ_NO", i));
				// 累计验收数
				table.setItem(row, "ACTUAL", 0);

				String time1 = addParm.getData("INVOICE_DATE", i) + "";
				time1 = time1.replaceAll("-", "/");
				// 发票日期
				table.setItem(row, "INVOICE_DATE", time1);
				String validDate = addParm.getData("VALID_DATE", i) + "";
				validDate = validDate.replaceAll("-", "/");
				table.setItem(row, "REASON_CHN_DESC", "VER01");
				// 效期
				table.setItem(row, "VALID_DATE", validDate);
				// 生产厂商
				table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
				// 发票号
				table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
				// 批号
				table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
				// 批号
				table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
				// 装箱单号
				String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
				table.setItem(row, "SPC_BOX_BARCODE",boxCode);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			getCheckBox("SELECT_ALL").setSelected(true);
			onCheckSelectAll();
		}
	}
	
	/**
	 * 保存供应商代理的药品
	 * @param orderCode
	 * @param price
	 */
	public static void onSaveAgentInfo(String orderCode,double price,String supCode){
        TParm parm = new TParm();
        parm.setData("SUP_CODE", supCode);
        parm.setData("ORDER_CODE", orderCode);
        parm.setData("MAIN_FLG", "Y");
        parm.setData("CONTRACT_NO", "");
        parm.setData("CONTRACT_PRICE",price);
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDAgentAction",
                                                  "onInsert", parm);
	}
	
    /**
     * 查询国药出货单是否已经导入到物联网-验收用
     * @param orderCode 药品代码
     * @param boxCode   货箱条码
     * @param billNo    销售单号-在验收表里字段是PURORDER_NO
     * @param erpId     国药出货单-ID
     * @return String
     */
	public static boolean isImpERPInfo(String orderCode,String boxCode,String billNo,String erpId){
		//false 表示 未导入，true表示已经导入
		boolean flag = false;
//		System.out.println("------------SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId):"+SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId)));
		if(null != parm && parm.getCount() > 0 && parm.getInt("COUNT_NUM", 0)> 0){
			flag = true;
		}
//		System.out.println("-------------flag: "+flag);
		return flag;
	}
	
	/**
	 * 打印验收入库单
	 */
	public void onPrint() {
		Timestamp datetime = SystemTool.getInstance().getDate();
		TTable table_d = getTable("TABLE_D");
		if ("".equals(this.getValueString("VERIFYIN_NO"))) {
			this.messageBox("不存在验收单");
			return;
		}
		if (table_d.getRowCount() > 0) {
			// 打印数据
			TParm date = new TParm();
			// 表头数据
			date.setData("TITLE", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()) + "验收入库单");
			date.setData("VER_NO", "TEXT", "入库单号: " + this.getValueString("VERIFYIN_NO"));
			date.setData("SUP_CODE", "TEXT", "供货厂商: " + this.getTextFormat("SUP_CODE").getText());
			date.setData("ORG_CODE", "TEXT", "验收部门: " + this.getComboBox("ORG_CODE").getSelectedName());
			date.setData("DATE", "TEXT", "制表日期: " + datetime.toString().substring(0, 10).replace('-', '/'));
			// 表格数据
			TParm parm = new TParm();
			String order_code = "";
			double ver_sum = 0;
			double own_sum = 0;
			double diff_sum = 0;
			for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
				if (!table_d.getDataStore().isActive(i)) {
					continue;
				}
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				parm.addData("INVOICE_NO", table_d.getItemString(i, "INVOICE_NO"));
				String invoiceDate =  table_d.getItemString(i, "INVOICE_DATE");
				if(invoiceDate.length()>=10){
					invoiceDate=invoiceDate.substring(0, 10).replace('-', '/');
				}
				parm.addData("INVOICE_DATE", invoiceDate);
				order_code = table_d.getDataStore().getItemString(i, "ORDER_CODE");
				TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderInfoByCode(order_code, this.getValueString("SUP_CODE"), "VER")));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("药品信息有误");
					return;
				}
				parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
				parm.addData("SPECIFICATION", inparm.getValue("SPECIFICATION", 0));
				parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
				// luhai 2012-2-28 modify 验收数量取1位 begin
				// parm.addData("VERIFYIN_QTY",
				// df3.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
				parm.addData("VERIFYIN_QTY", df5.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
				// luhai 2012-2-28 modify 验收数量取1位 end
				parm.addData("VERIFYIN_PRICE", df4.format(StringTool.round(table_d.getItemDouble(i, "VERIFYIN_PRICE"), 4)));
				double ver_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") * table_d.getItemDouble(i, "VERIFYIN_PRICE");
				parm.addData("VER_AMT", df2.format(StringTool.round(ver_atm, 2)));
				ver_sum += ver_atm;
				parm.addData("OWN_PRICE", df4.format(StringTool.round(table_d.getItemDouble(i, "RETAIL_PRICE"), 4)));
				double own_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") * table_d.getItemDouble(i, "RETAIL_PRICE");
				parm.addData("OWN_AMT", df2.format(StringTool.round(own_atm, 2)));
				own_sum += own_atm;
				parm.addData("DIFF_AMT", df2.format(StringTool.round(own_atm - ver_atm, 2)));
				diff_sum += own_atm - ver_atm;
				parm.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
				parm.addData("VALID_DATE", table_d.getItemString(i, "VALID_DATE").substring(0, 10).replace('-', '/'));
				String pha_type = inparm.getValue("PHA_TYPE", 0);
				if ("W".equals(pha_type)) {
					parm.addData("PHA_TYPE", "西药");
				} else if ("C".equals(pha_type)) {
					parm.addData("PHA_TYPE", "中成药");
				} else if ("G".equals(pha_type)) {
					parm.addData("PHA_TYPE", "中草药");
				}
				// 生产厂商
				TParm manparm = new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSManufacturer(table_d.getItemString(i, "MAN_CODE"))));
				// System.out.println(""+SYSSQL.getSYSManufacturer(table_d.getItemString(i,
				// "MAN_CODE")));
				parm.addData("MAN_CODE", manparm.getValue("MAN_CHN_DESC", 0));
				if ("Y".equals(inparm.getValue("BID_FLG", 0))) {
					parm.addData("BID_FLG", "是");
				} else {
					parm.addData("BID_FLG", "否");
				}
			}
			if (parm.getCount("ORDER_DESC") == 0) {
				this.messageBox("没有打印数据");
				return;
			}
			int row = parm.getCount("ORDER_DESC");


			// luhai 2012-3-7 删除合计部分改用图区实现 begin
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");
			// luhai delete 2012-2-11 begin
			// parm.addData("SYSTEM", "COLUMNS", "INVOICE_DATE");
			// luhai delete 2012-2-11 end
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");

			date.setData("TABLE", parm.getData());
			date.setData("BAR_CODE", "TEXT", this.getValueString("VERIFYIN_NO"));
			// 表尾数据
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// luhai 2012-3-7 begin 加入合计部分
			date.setData("VER_AMT", "TEXT", df2.format(StringTool.round(ver_sum, 2)));// 验收价格
			date.setData("OWN_AMT", "TEXT", df2.format(StringTool.round(own_sum, 2)));// 零售价格
			date
					.setData("VER_AMT_CHN", "TEXT", "合计（大写）："
							+ StringUtil.getInstance().numberToWord(Double.parseDouble(df2.format(StringTool.round(ver_sum, 2)))));// 验收大写
			// luhai 2012-3-7 end
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\VerifyIn.jhw", date);
		} else {
			this.messageBox("没有打印数据");
			return;
		}
	}

	/**
	 * 打印出库单
	 */
	public void onPrintDispendseOut(String dispsenseNo) {
		Timestamp datetime = SystemTool.getInstance().getDate();
		if ("".equals(dispsenseNo)) {
			return;
		}
		TParm dispenseMParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getDispenseByDisNo(dispsenseNo)));
//		System.out.println("-----dispenseMParm: "+dispenseMParm);
		if (dispenseMParm.getCount() > 0) {
			// 打印数据
			TParm date = new TParm();
			// 表头数据
			date.setData("TITLE", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()) + "出库单");
			// ===============pangben modify 20110607 添加急件注记
			String urGentFlg = dispenseMParm.getValue("URGENT_FLG", 0);
			if (null != urGentFlg && urGentFlg.equals("Y"))
				date.setData("URGENT", "TEXT", "急");
			else
				date.setData("URGENT", "TEXT", "");
			date.setData("DISP_NO", "TEXT", "出库单号: " + dispsenseNo);
			date.setData("REQ_NO", "TEXT", "申请单号: " + dispenseMParm.getValue("REQUEST_NO",0));
			String dispenseDate = dispenseMParm.getValue("DISPENSE_DATE",0);
			if(null != dispenseDate && dispenseDate.length() > 10){
				dispenseDate = dispenseDate.substring(0, 10).replace('-', '/');
			}
			date.setData("OUT_DATE", "TEXT", "出库日期: " + dispenseDate);
			date.setData("REQ_TYPE", "TEXT", "申请类别: " + "麻精过度");
			TParm appOrgCodeParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgDescByOrgCode(dispenseMParm.getValue("APP_ORG_CODE",0))));
			TParm toOrgCodeParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgDescByOrgCode(dispenseMParm.getValue("TO_ORG_CODE",0))));
			date.setData("ORG_CODE_APP", "TEXT", "申请部门: " +appOrgCodeParm.getValue("ORG_CHN_DESC",0));
			date.setData("ORG_CODE_FROM", "TEXT", "接受部门: " +toOrgCodeParm.getValue("ORG_CHN_DESC",0));
			date.setData("DATE", "TEXT", "制表日期: " + datetime.toString().substring(0, 10).replace('-', '/'));

			// 表格数据
			TParm parm = new TParm();
			String order_code = "";
			String order_desc = "";
			double qty = 0;
			double sum_retail_price = 0;
			// luhai 2012-1-22 加入采购价总和
			double sum_verifyin_price = 0;

			String sql = "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL " + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  " + "  END AS ORDER_DESC, "
					+ " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
					+ " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE "// ADD
					+ " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C " + " WHERE A.ORDER_CODE = B.ORDER_CODE " + " AND A.UNIT_CODE = C.UNIT_CODE "
					+ " AND A.DISPENSE_NO = '" + dispsenseNo + "' " + " ORDER BY A.SEQ_NO";
			// luhai modify 出库单删除商品名 begin
			TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
				parm.addData("ORDER_DESC", printData.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", printData.getValue("SPECIFICATION", i));
				parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
				parm.addData("UNIT_PRICE", df4.format(printData.getDouble("RETAIL_PRICE", i)));
				parm.addData("VERIFYIN_PRICE", df4.format(printData.getDouble("VERIFYIN_PRICE", i)));
				qty = printData.getDouble("OUT_QTY", i);
				parm.addData("QTY", qty);
				parm.addData("AMT", StringTool.round(printData.getDouble("RETAIL_PRICE", i) * qty, 2));
				parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble("VERIFYIN_PRICE", i) * qty, 2));
				sum_retail_price += printData.getDouble("RETAIL_PRICE", i) * qty;
				sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) * qty;
				parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
				parm.addData("VALID_DATE", StringTool.getString(printData.getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
			}

			if (parm.getCount("ORDER_DESC") <= 0) {
				this.messageBox("没有打印数据");
				return;
			}
			// luhai 2012-1-22 modify 加入采购金额，采购单价 begin
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT");
			parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
			date.setData("TABLE", parm.getData());

			// 表尾数据
			String atm = StringTool.round(sum_retail_price, 2) + "";
			String verifyinAtm = StringTool.round(sum_verifyin_price, 2) + "";
			// "合计(大写): " + StringUtil.getInstance().numberToWord(atm));
			date.setData("ATM", "TEXT", atm);
			date.setData("VERIFYIN_ATM", "TEXT", verifyinAtm);
			date.setData("VERIFYIN_ATM_DESC", "TEXT", StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
			date.setData("TOT", "TEXT", "合计: ");
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			date.setData("BAR_CODE", "TEXT", dispsenseNo);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", date);
		} else {
			this.messageBox("没有打印数据");
			return;
		}
	}

	/**
	 * 主项表格(TABLE_M)单击事件
	 */
	public void onTableMClicked() {
		TTable table = getTable("TABLE_M");
		int row = table.getSelectedRow();
		if (row != -1) {
			// 主项信息(TABLE中取得)
			setValue("VERIFYIN_DATE", table.getItemTimestamp(row, "VERIFYIN_DATE"));
			setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
			setValue("VERIFYIN_NO", table.getItemString(row, "VERIFYIN_NO"));
			setValue("PLAN_NO", table.getItemString(row, "PLAN_NO"));
			setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
			setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
			setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
			if (getRadioButton("UPDATE_FLG_B").isSelected()) {
				setValue("CHECK_FLG", "Y");
			} else {
				setValue("CHECK_FLG", "N");
			}
			// 设定页面状态
			getTextField("VERIFYIN_NO").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
//			((TMenuItem) getComponent("export")).setEnabled(false);
			action = "updateM";
			this.setCheckFlgStatus(action);

			// 查询选中验收单号所对应的细项
			TTable table_D = getTable("TABLE_D");
			table_D.removeRowAll();
			table_D.setSelectionMode(0);
			TDS tds = new TDS();
			tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
			tds.retrieve();
			if (tds.rowCount() == 0) {
				this.messageBox("没有订购明细");
			}
			// 观察者
			IndVerifyinObserver obser = new IndVerifyinObserver();
			tds.addObserver(obser);
			table_D.setDataStore(tds);
			table_D.setDSValue();

			this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
			this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			this.setValue("PRICE_DIFFERENCE", getSumRetailMoney() - getSumVerifinMoney());
/*			if (getRadioButton("TOXIC_DRUG").isSelected()) {// 麻精
				// 画面状态
				((TMenuItem) getComponent("save")).setEnabled(true);
			} else {//普药
				// 画面状态
				((TMenuItem) getComponent("save")).setEnabled(false);
			}*/			
		}
	}

	/**
	 * 明细表格(TABLE_D)单击事件
	 */
	public void onTableDClicked() {
		TTable table = getTable("TABLE_D");
		int row = table.getSelectedRow();
		if (row != -1) {
			// 主项信息
			TTable table_M = getTable("TABLE_M");
			table_M.setSelectionMode(0);
			if (getTextField("VERIFYIN_NO").isEnabled()) {
				action = "insert";
			} else {
				action = "updateD";
			}
			this.setCheckFlgStatus(action);

			// 取得SYS_FEE信息，放置在状态栏上
			/*
			 * String order_code = table.getDataStore().getItemString(table.
			 * getSelectedRow(), "ORDER_CODE"); if (!"".equals(order_code)) {
			 * this.setSysStatus(order_code); } else {
			 * callFunction("UI|setSysStatus", ""); }
			 */
		}
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableDChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		if (node.getValue() != null && node.getOldValue() != null && node.getValue().equals(node.getOldValue()))
			return true;
		// Table的列名
		TTable table = node.getTable();
		String columnName = table.getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("VERIFYIN_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("验收数量不能小于或等于0");
				return true;
			}
			double amt = StringTool.round(qty * table.getItemDouble(row, "VERIFYIN_PRICE"), 2);
			table.setItem(row, "INVOICE_AMT", amt);
			return false;
		}
		if ("GIFT_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("赠送量不能小于0");
				return true;
			}
			return false;
		}
		if ("VERIFYIN_PRICE".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("验收单价不能小于或等于0");
				return true;
			}
			double amt = StringTool.round(qty * table.getItemDouble(row, "VERIFYIN_QTY"), 2);
			table.setItem(row, "INVOICE_AMT", amt);
			return false;
		}
		if ("QUALITY_DEDUCT_AMT".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("品质扣款不能小于0");
				return true;
			}
		}
		// 发票号码
		if ("INVOICE_NO".equals(columnName) && row == 0) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "INVOICE_NO", node.getValue());
			}
		}
		// 发票日期
		if ("INVOICE_DATE".equals(columnName) && row == 0) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "INVOICE_DATE", node.getValue());
			}
			return false;
		}
		String order_code = table.getItemString(row, "ORDER_CODE");
		String sql = "SELECT PHA_TYPE FROM PHA_BASE WHERE ORDER_CODE = '" + order_code + "' ";
		// System.out.println("sql---"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String pha_type = parm.getValue("PHA_TYPE", 0);
		// 生产厂商
		if ("MAN_CODE".equals(columnName)) {
			if (!"G".equals(pha_type)) {
				this.messageBox("该药品非中草药，不可修改生产厂商!");
				return true;
			}
		}
		// 零售价
		if ("RETAIL_PRICE".equals(columnName)) {
			if (!"G".equals(pha_type)) {
				this.messageBox("该药品非中草药，不可修改零售价格!");
				return true;
			} else {
				double own_price = TypeTool.getDouble(node.getValue());
				if (own_price <= 0) {
					this.messageBox("零售单价不能小于或等于0");
					return true;
				}
				double amt = StringTool.round(own_price * (table.getItemDouble(row, "VERIFYIN_QTY") + table.getItemDouble(row, "GIFT_QTY")), 2);
				table.setItem(row, "SELL_SUM", amt);
				return false;
			}
		}
		return false;
	}

	/**
	 * 表格(TABLE)复选框改变事件
	 * 
	 * @param obj
	 */
	public void onTableCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的列
		int column = tableDown.getSelectedColumn();
		// 获得选中的行
		int row = tableDown.getSelectedRow();
		if (column == 0) {
			if (tableDown.getDataStore().isActive(row)) {
				// 计算零售总金额
				double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
				double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
				double sum = tableDown.getItemDouble(row, "RETAIL_PRICE") * (amount1 + amount2);
				double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
				this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum + sum_atm, 2));
				// 计算进货总金额
				double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
				double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
				this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(ver_amt + amt, 2));
			} else {
				// 计算零售总金额
				double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
				double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
				double sum = tableDown.getItemDouble(row, "RETAIL_PRICE") * (amount1 + amount2);
				double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
				if (sum_atm - sum < 0) {
					this.setValue("SUM_RETAIL_PRICE", 0);
				} else {
					this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum_atm - sum, 2));
				}
				// 计算进货总金额
				double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
				double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
				if (ver_amt - amt < 0) {
					this.setValue("SUM_VERIFYIN_PRICE", 0);
				} else {
					this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(ver_amt - amt, 2));
				}
			}
			// 进销差价
			this.setValue("PRICE_DIFFERENCE", StringTool.round(getValueDouble("SUM_RETAIL_PRICE") - getValueDouble("SUM_VERIFYIN_PRICE"), 2));
		}
	}

	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		TTable table = getTable("TABLE_D");
		table.acceptText();
		if (table.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.getDataStore().setActive(i, true);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
				this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
				this.setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney() - getSumVerifinMoney(), 2));
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.getDataStore().setActive(i, false);
				table.getDataStore().setItem(i, "UPDATE_FLG", "");
				this.setValue("SUM_RETAIL_PRICE", 0);
				this.setValue("SUM_VERIFYIN_PRICE", 0);
				this.setValue("PRICE_DIFFERENCE", 0);
			}
		}
		table.setDSValue();
	}

	/**
	 * 单选框改变事件
	 */
	public void onChangeRadioButton() {
		if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TMenuItem) getComponent("export")).setEnabled(false);
		} else {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TMenuItem) getComponent("export")).setEnabled(true);
		}
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		/**
		 * 权限控制 权限1:一般个人无赠与权限,只显示自已所属科室;无赠与录入功能 权限2:一般个人赠与权限,只显示自已所属科室;包含赠与录入功能
		 * 权限9:最大权限,显示全院药库部门包含赠与录入功能
		 */
		// 赠与权限
		if (!this.getPopedem("giftEnabled")) {
			TTable table_d = getTable("TABLE_D");
			table_d.setLockColumns("1,2,4,5,7,9,10,18,19,20");
			gift_flg = false;
		}
		// 显示全院药库部门
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByUserId(Operator.getID(), " AND B.ORG_TYPE = 'A' ")));
			getComboBox("ORG_CODE").setParmValue(parm);
			if (parm.getCount("NAME") > 0) {
				getComboBox("ORG_CODE").setSelectedIndex(1);
			}
			dept_flg = false;
		}
		// 审核入库权限
		if (!this.getPopedem("checkEnabled")) {
			getCheckBox("CHECK_FLG").setEnabled(false);
			check_flg = false;
		}

		// 添加侦听事件
		addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, "onTableDChangeValue");
		// 给TABLEDEPT中的CHECKBOX添加侦听事件
		callFunction("UI|TABLE_D|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
		// 初始化验收时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("VERIFYIN_DATE", date);
		// 初始化画面状态
		((TMenuItem) getComponent("delete")).setEnabled(false);
		// 初始化TABLE_D
		TTable table_D = getTable("TABLE_D");
		table_D.removeRowAll();
		table_D.setSelectionMode(0);
		TDS tds = new TDS();
		tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
		tds.retrieve();
		if (tds.rowCount() == 0) {
			seq = 1;
		} else {
			seq = getMaxSeq(tds, "SEQ_NO");
		}
		IndVerifyinObserver obser = new IndVerifyinObserver();
		tds.addObserver(obser);
		table_D.setDataStore(tds);
		table_D.setDSValue();
		// getComboBox("ORG_CODE").setSelectedIndex(1);
		action = "insert";
		this.setCheckFlgStatus(action);
		resultParm = new TParm();
/*		if (getRadioButton("TOXIC_DRUG").isSelected()) {// 麻精q
			// 画面状态
			((TMenuItem) getComponent("save")).setEnabled(true);
		} else {//普药
			// 画面状态
			((TMenuItem) getComponent("save")).setEnabled(false);
		}	*/
	}

	/**
	 * 设定审核入库的状态
	 * 
	 * @param action
	 *            String
	 */
	private void setCheckFlgStatus(String action) {
		if ("insert".equals(action)) {
			if (check_flg) {
				TTable table_M = getTable("TABLE_M");
				if (table_M.getSelectedRow() >= 0) {
					getCheckBox("CHECK_FLG").setEnabled(true);
				} else {
					getCheckBox("CHECK_FLG").setEnabled(false);
				}
			} else {
				getCheckBox("CHECK_FLG").setEnabled(false);
			}
		} else if ("updateM".equals(action)) {
			if (check_flg) {
				getCheckBox("CHECK_FLG").setEnabled(true);
			} else {
				getCheckBox("CHECK_FLG").setEnabled(false);
			}
		} else if ("updateD".equals(action)) {
			getCheckBox("CHECK_FLG").setEnabled(false);
		}
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("验收部门不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataD() {
		// 判断细项是否有被选中项
		TTable table = getTable("TABLE_D");
		table.acceptText();
		int count = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getDataStore().isActive(i)) {
				count++;
				// 判断数据正确性
				double qty = table.getItemDouble(i, "VERIFYIN_QTY");
				if (qty <= 0) {
					this.messageBox("验收数量不能小于或等于0");
					return false;
				}
				double pur_qty = resultParm.getDouble("PURORDER_QTY", i);
/*				if (qty > pur_qty) {
					this.messageBox("验收数量不能大于订购量");
					return false;
				}*/
				qty = table.getItemDouble(i, "GIFT_QTY");
				if (qty < 0) {
					this.messageBox("赠送量不能小于0");
					return false;
				}
				qty = table.getItemDouble(i, "VERIFYIN_PRICE");
				if (qty <= 0) {
					this.messageBox("验收单价不能小于或等于0");
					return false;
				}
				// 发票号码
				/*
				 * if ("".equals(table.getItemString(i, "INVOICE_NO"))) {
				 * this.messageBox("发票号码不能为空"); return false; }
				 */
				// 发票日期
				/*
				 * if (table.getItemTimestamp(i, "INVOICE_DATE") ==
				 * null||table.getItemTimestamp(i, "INVOICE_DATE").equals(new
				 * TNull(Timestamp.class))) { this.messageBox("发票日期不能为空");
				 * return false; }
				 */
				// 批号
				if ("".equals(table.getItemString(i, "BATCH_NO"))) {
					this.messageBox("批号不能为空");
					return false;
				}
				// 效期
				if (table.getItemTimestamp(i, "VALID_DATE") == null || table.getItemTimestamp(i, "VALID_DATE").equals(new TNull(Timestamp.class))) {
					this.messageBox("效期不能为空");
					return false;
				}
				// 品质扣款
				qty = table.getItemDouble(i, "QUALITY_DEDUCT_AMT");
				if (qty < 0) {
					this.messageBox("品质扣款不能小于0");
					return false;
				}
			}
		}
		if (count == 0) {
			this.messageBox("没有执行数据");
			return false;
		}
		return true;
	}

	/**
	 * 取得验收单号
	 * 
	 * @return String
	 */
	private String getVerifyinNO(String invoiceNo) {
		String verifyinNo = "";
		if (map.containsKey(invoiceNo)) {
			verifyinNo = (String) map.get(invoiceNo);
			TDS tds = new TDS();
			tds.setSQL(INDSQL.getVerifyinDByNo(verifyinNo));
			tds.retrieve();
			seq++;
		} else {
			verifyinNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_VERIFYIN", "No");
			map.put(invoiceNo, verifyinNo);
			seq = 1;
		}
		return verifyinNo;
	}

	/**
	 * 计算零售总金额
	 * 
	 * @return
	 */
	private double getSumRetailMoney() {
		TTable table = getTable("TABLE_D");
		TDataStore ds = table.getDataStore();
		table.acceptText();
		double sum = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
				double amount1 = table.getItemDouble(i, "VERIFYIN_QTY");
				double amount2 = table.getItemDouble(i, "GIFT_QTY");
				sum += table.getItemDouble(i, "RETAIL_PRICE") * (amount1 + amount2);
			}
		}
		return StringTool.round(sum, 2);
	}

	/**
	 * 计算进货总金额
	 * 
	 * @return
	 */
	private double getSumVerifinMoney() {
		TTable table = getTable("TABLE_D");
		TDataStore ds = table.getDataStore();
		table.acceptText();
		double sum = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
				sum += table.getItemDouble(i, "INVOICE_AMT");
			}
		}
		return StringTool.round(sum, 2);
	}

	/**
	 * 得到最大的编号
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	private int getMaxSeq(TDataStore dataStore, String columnName) {
		if (dataStore == null)
			return 0;
		// 保存数据量
		int count = dataStore.rowCount();
		// 保存最大号
		int s = 0;
		for (int i = 0; i < count; i++) {
			int value = dataStore.getItemInt(i, columnName);
			// 保存最大值
			if (s < value) {
				s = value;
				continue;
			}
		}
		// 最大号加1
		s++;
		return s;
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
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
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
	 * 取得SYS_FEE信息，放置在状态栏上
	 * 
	 * @param order_code
	 *            String
	 */
	private void setSysStatus(String order_code) {
		TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
		String status_desc = "药品代码:" + order.getValue("ORDER_CODE") + " 药品名称:" + order.getValue("ORDER_DESC") + " 商品名:" + order.getValue("GOODS_DESC") + " 规格:"
				+ order.getValue("SPECIFICATION");
		callFunction("UI|setSysStatus", status_desc);
	}

	// 根据验收单号判断验收入库量更新入库状态
	private void updateIndVerifinDUpdateFlg(String verifyin_no) {
		String sql = "SELECT A.PURORDER_QTY + A.GIFT_QTY AS QTY, " + " B.VERIFYIN_QTY, B.VERIFYIN_NO, B.SEQ_NO "
				+ " FROM IND_PURORDERD A, IND_VERIFYIND B, IND_VERIFYINM C " + " WHERE A.PURORDER_NO = B.PURORDER_NO " + " AND A.SEQ_NO = B.PURSEQ_NO "
				+ " AND B.VERIFYIN_NO = C.VERIFYIN_NO " + " AND A.PURORDER_QTY = B.VERIFYIN_QTY " + " AND C.CHECK_DATE IS NOT NULL " + " AND B.VERIFYIN_NO = '"
				+ verifyin_no + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String update_sql = "UPDATE IND_VERIFYIND SET UPDATE_FLG = '";
		String update_where = "' WHERE VERIFYIN_NO = '" + verifyin_no + "' AND SEQ_NO = ";
		String update_flg = "1";
		String sql_list = "";
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
			if (parm.getDouble("VERIFYIN_QTY", i) >= parm.getDouble("QTY", i)) {
				update_flg = "3";
			} else {
				update_flg = "1";
			}
			sql_list = update_sql + update_flg + update_where + parm.getInt("SEQ_NO", i);
			result = new TParm(TJDODBTool.getInstance().update(sql_list));
		}
	}

	/**
	 * 当入库的药品为中药时判断是否更新生产厂商
	 * 
	 * @param verifyin_no
	 *            String
	 */
	private void updateManCode(String verifyin_no) {
		String sql = " SELECT A.ORDER_CODE, A.MAN_CODE , B.MAN_CHN_DESC " + " FROM IND_VERIFYIND A, PHA_BASE B " + " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND B.PHA_TYPE = 'G' AND A.VERIFYIN_NO = '" + verifyin_no + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
			return;
		}
		List list = new ArrayList();
		String man_code = "";
		String order_code = "";
		String sql_1 = "";
		String sql_2 = "";
		String sql_3 = "";
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			if (parm.getValue("MAN_CHN_DESC").equals(parm.getValue("MAN_CODE", i))) {
				continue;
			} else {
				order_code = parm.getValue("ORDER_CODE", i);
				man_code = parm.getValue("MAN_CODE", i);
				sql_1 = "UPDATE PHA_BASE SET MAN_CHN_DESC = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' ";
				sql_2 = "UPDATE SYS_FEE SET MAN_CODE = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' ";
				sql_3 = "UPDATE SYS_FEE_HISTORY SET MAN_CODE = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' AND ACTIVE_FLG = 'Y'";
				list.add(sql_1);
				list.add(sql_2);
				list.add(sql_3);
			}
		}
		if (list == null || list.size() <= 0) {
			return;
		} else {
			String[] sql_list = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				sql_list[i] = list.get(i).toString();
			}
			TParm result = new TParm(TJDODBTool.getInstance().update(sql_list));
			if (result == null || result.getErrCode() < 0) {
				return;
			} else {
				TIOM_Database.logTableAction("SYS_FEE");
			}
		}
	}
}
