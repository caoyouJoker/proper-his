package jdo.ind;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdo.pha.PhaBaseTool;
import jdo.spc.INDSQL;
import jdo.spc.IndDispenseDTool;
import jdo.spc.IndDispenseMTool;
import jdo.spc.IndRequestDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndStockMTool;
import jdo.spc.SPCSQL;
import jdo.util.Manager;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 财务月自动备药批次Tool
 * </p>
 * 
 * <p>
 * Description:财务月自动备药批次Tool
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
 * @author wukai 2017.02.17
 * @version 1.0
 */
public class INDIBSOPDAutoMedTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INDIBSOPDAutoMedTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static INDIBSOPDAutoMedTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDIBSOPDAutoMedTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INDIBSOPDAutoMedTool() {
		onInit();
	}

	/**
	 * 新增出库作业(出库即入库)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutIn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		String batch_date = parm.getValue("BATCH_DATE");
		// 申请单类型
		String request_type = parm.getValue("REQTYPE_CODE");
		String out_org_code = parm.getValue("OUT_ORG_CODE");
		String in_org_code = parm.getValue("IN_ORG_CODE");
		// 单位类型
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// 入库注记
		boolean in_flg = parm.getBoolean("IN_FLG");
		// 判断是否自动将成本价存回批发价
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// 药品出库(申请单出库入库)
		result = onIndDispenseOutIn(parmD, out_org_code, in_org_code,
				unit_type, request_type, in_flg, reuprice_flg, batch_date, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新申请单状态及实际出入库数量
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 药品出库入库(申请单出库入库)
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TParm onIndDispenseOutIn(TParm parm, String out_org_code,
			String in_org_code, String unit_type, String request_type,
			boolean in_flg, String reuprice_flg,String batch_date, TConnection conn) {
		String order_code = "";
		int seq = 1;
		double actual_qty = 0;
		double stock_qty = 0;
		String batch_no = "";
		String valid_date = "";
		// 入库信息
		TParm stock_in = new TParm();
		TParm stock_parm = new TParm();
		TParm result = new TParm();
		// 出库作业
		System.out.println("出库细项 onIndDispenseOutIn parm count :::: " + parm.getCount("ORDER_CODE"));
		for (int X = 0, count = parm.getCount("ORDER_CODE"); X < count; X++) {
			int i = X;
			// 实际出库数量
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(out_org_code, order_code, "");
			if(stock_parm == null || stock_parm.getErrCode() < 0 || stock_parm.getCount() <= 0) {
				System.out.println("**" + out_org_code + " @@@ " + order_code
						+ "**** 没有库存");
				// TODO 库存不足 不扣库， 但是实际出库为 需要出库量
				result = nonNomalIndDispenseD(parm, actual_qty,
						request_type, out_org_code, order_code, i, seq,
						unit_type, batch_date, conn);
				if(result.getErrCode() < 0) {
					return result;
				}
				seq++;
			} else {
				stock_qty = 0;
				for (int j = 0; j < stock_parm.getCount(); j++) {
					stock_qty += stock_parm.getDouble("QTY", j);
				}
				if (stock_qty < actual_qty) {
					System.out.println("**" + out_org_code + " @@@ " + order_code
							+ "**** 库存不足");
					// TODO 库存不足 不扣库， 但是实际出库为 需要出库量
					result = nonNomalIndDispenseD( parm, actual_qty,
							request_type, out_org_code, order_code, i, seq,
							unit_type, batch_date, conn);
					if(result.getErrCode() < 0) {
						return result;
					}
					seq++;
				} else {
					for (int j = 0; j < stock_parm.getCount(); j++) {
						double qty = stock_parm.getDouble("QTY", j);
						int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
						if (qty >= actual_qty) {
							// 药品可以一次扣库
							double out_amt = StringTool.round(
									stock_parm.getDouble("RETAIL_PRICE", j)
											* actual_qty, 2);
							// 更新库存(申请单出库)
							result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
									request_type, out_org_code, order_code, batch_seq,
									actual_qty, out_amt, parm.getValue("OPT_USER", i),
									parm.getTimestamp("OPT_DATE", i),
									parm.getValue("OPT_TERM", i), conn);
							// 填入入库信息
							stock_in.addData("ORDER_CODE", order_code);
							stock_in.addData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							stock_in.addData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							stock_in.addData("IN_QTY", actual_qty);
							stock_in.addData("RETAIL_PRICE",
									parm.getDouble("RETAIL_PRICE", i));
							stock_in.addData("OPT_USER", parm.getValue("OPT_USER", i));
							stock_in.addData("OPT_DATE",
									parm.getTimestamp("OPT_DATE", i));
							stock_in.addData("OPT_TERM", parm.getValue("OPT_TERM", i));

							if (result.getErrCode() < 0) {
								return result;
							}

							// 新增出库单细项
							TParm inparm = new TParm();
							inparm.setData("DISPENSE_NO",
									parm.getValue("DISPENSE_NO", i));
							inparm.setData("SEQ_NO", seq);
							inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
							inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
							inparm.setData("BATCH_SEQ",
									stock_parm.getInt("BATCH_SEQ", j));
							inparm.setData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							inparm.setData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							if ("0".equals(unit_type)) {
								qty_out = actual_qty
										/ getPhaTransUnitQty(order_code, "2");
							} else {
								qty_out = actual_qty;
							}
							inparm.setData("QTY", qty_out);
							inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
							inparm.setData("RETAIL_PRICE",
									stock_parm.getDouble("RETAIL_PRICE", j));
							String verifyinPrice = stock_parm.getValue(
									"VERIFYIN_PRICE", j);
							inparm.setData("VERIFYIN_PRICE", verifyinPrice);
							inparm.setData("STOCK_PRICE", verifyinPrice);

							inparm.setData("ACTUAL_QTY", qty_out);
							inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
							inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
							inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
							inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
							inparm.setData("BOX_ESL_ID", parm.getValue("BOX_ESL_ID", i));
							inparm.setData("BOXED_USER", parm.getValue("BOXED_USER", i));
							inparm.setData("IS_BOXED", parm.getValue("IS_BOXED", i));
							inparm.setData("SUP_CODE",
									stock_parm.getValue("SUP_CODE", j));
							inparm.setData("INVENT_PRICE",
									stock_parm.getValue("INVENT_PRICE", j));
							inparm.setData("SUP_ORDER_CODE",
									stock_parm.getValue("SUP_ORDER_CODE", j));

							result = IndDispenseDTool.getInstance().onInsertD(inparm,
									conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							seq++;
							break;
						} else {
							// 药品不可一次扣库
							double out_amt = StringTool.round(
									stock_parm.getDouble("RETAIL_PRICE", i) * qty, 2);
							// 更新库存(申请单出库)
							result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
									request_type, out_org_code, order_code, batch_seq,
									qty, out_amt, parm.getValue("OPT_USER", i),
									parm.getTimestamp("OPT_DATE", i),
									parm.getValue("OPT_TERM", i), conn);

							// 填入入库信息
							stock_in.addData("ORDER_CODE", order_code);
							stock_in.addData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							stock_in.addData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							stock_in.addData("IN_QTY", qty);
							stock_in.addData("RETAIL_PRICE",
									parm.getDouble("RETAIL_PRICE", i));
							stock_in.addData("OPT_USER", parm.getValue("OPT_USER", i));
							stock_in.addData("OPT_DATE",
									parm.getTimestamp("OPT_DATE", i));
							stock_in.addData("OPT_TERM", parm.getValue("OPT_TERM", i));

							if (result.getErrCode() < 0) {
								return result;
							}

							// 新增出库单细项
							TParm inparm = new TParm();
							inparm.setData("DISPENSE_NO",
									parm.getValue("DISPENSE_NO", i));
							inparm.setData("SEQ_NO", seq);
							inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
							inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
							inparm.setData("BATCH_SEQ",
									stock_parm.getInt("BATCH_SEQ", j));
							inparm.setData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							inparm.setData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							if ("0".equals(unit_type)) {
								qty_out = qty / getPhaTransUnitQty(order_code, "2");
							} else {
								qty_out = qty;
							}
							inparm.setData("QTY", qty_out);
							inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
							inparm.setData("RETAIL_PRICE",
									stock_parm.getDouble("RETAIL_PRICE", j));
							String verifyinPrice = stock_parm.getValue(
									"VERIFYIN_PRICE", j);
							inparm.setData("VERIFYIN_PRICE", verifyinPrice);
							inparm.setData("STOCK_PRICE", verifyinPrice);

							inparm.setData("ACTUAL_QTY", qty_out);
							inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
							inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
							inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
							inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
							inparm.setData("BOX_ESL_ID", parm.getValue("BOX_ESL_ID", i));
							inparm.setData("BOXED_USER", parm.getValue("BOXED_USER", i));
							inparm.setData("IS_BOXED", parm.getValue("IS_BOXED", i));
							inparm.setData("SUP_CODE",
									stock_parm.getValue("SUP_CODE", j));
							inparm.setData("INVENT_PRICE",
									stock_parm.getValue("INVENT_PRICE", j));
							inparm.setData("SUP_ORDER_CODE",
									stock_parm.getValue("SUP_ORDER_CODE", j));

							result = IndDispenseDTool.getInstance().onInsertD(inparm,
									conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							seq++;
							// 更新出库量
							actual_qty = actual_qty - qty;
							// 更新实际出库数量
							qty_out = qty_out + qty;
						}
					}
					
				}
				
			}
		}

		// 入库作业
		if (in_flg) {
			// 根据批号和效期查询药品,存在的话更新库存量,不存在的话取最大批次序号进行新增
			List list = new ArrayList();
			double dosage_qty = 0;
			int batch_seq = 0;
			double retail_price = 0;
			for (int i = 0; i < stock_in.getCount("ORDER_CODE"); i++) {
				order_code = stock_in.getValue("ORDER_CODE", i);
				// 添加入列表
				list.add(order_code);
				dosage_qty = stock_in.getDouble("IN_QTY", i);
				retail_price = stock_in.getDouble("RETAIL_PRICE", i)
						/ dosage_qty;
				valid_date = stock_in.getValue("VALID_DATE", i)
						.replace('/', '-').substring(0, 10);
				batch_no = stock_in.getValue("BATCH_NO", i);
				String opt_user = stock_in.getValue("OPT_USER", i);
				Timestamp opt_date = stock_in.getTimestamp("OPT_DATE", i);
				String opt_term = stock_in.getValue("OPT_TERM", i);
				result = IndStockDTool.getInstance()
						.onUpdateStockByBatchVaildIn(request_type, in_org_code,
								order_code, batch_seq, valid_date, batch_no,
								dosage_qty, retail_price, opt_user, opt_date,
								opt_term, list, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 移动加权平均成本 */
			double in_qty = 0;
			double d_qty = 0;
			double stock_price = 0;
			double in_amt = 0;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				in_qty = parm.getDouble("ACTUAL_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				stock_price = parm.getDouble("STOCK_PRICE", i);
				in_amt = StringTool.round(in_qty * stock_price, 2);
				in_qty = StringTool.round(in_qty * d_qty, 2);
				stock_price = getPhaBaseStockPrice(
						parm.getValue("ORDER_CODE", i), in_amt, in_qty);
				TParm stock_price_parm = new TParm();
				stock_price_parm.setData("ORDER_CODE",
						parm.getValue("ORDER_CODE", i));
				stock_price_parm.setData("STOCK_PRICE", stock_price);
				result = PhaBaseTool.getInstance().onUpdateStockPrice(
						stock_price_parm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 批发价更新 */
			if ("Y".equals(reuprice_flg)) {
				for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
					d_qty = parm.getDouble("DOSAGE_QTY", i);
					stock_price = StringTool.round(
							parm.getDouble("STOCK_PRICE", i) / d_qty, 4);
					TParm trade_price_parm = new TParm();
					trade_price_parm.setData("ORDER_CODE",
							parm.getValue("ORDER_CODE", i));
					trade_price_parm.setData("TRADE_PRICE", stock_price);
					result = PhaBaseTool.getInstance().onUpdateTradePrice(
							trade_price_parm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		}

		return result;
	}

	/**
	 * 计算库存平均价 (加权平均成本)
	 * 
	 * @param order_code
	 *            药品代码
	 * @param verifyin_atm
	 *            验收金额
	 * @param in_qty
	 *            入库量(配药单位)
	 * @return 加权平均成本
	 */
	public double getPhaBaseStockPrice(String order_code, double verifyin_atm,
			double in_qty) {
		TParm result = new TParm();
		// 取得库存量
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		result = IndStockDTool.getInstance().onQueryStockQTY(parm);
		double qty = result.getDouble("QTY", 0);
		// System.out.println("库存量" + qty);
		// 取得原加成平均成本
		result = PhaBaseTool.getInstance().selectByOrder(order_code);
		double stock_price = result.getDouble("STOCK_PRICE", 0);
		// System.out.println("原加成平均成本" + stock_price);
		// System.out.println("验收金额" + verifyin_atm);
		// System.out.println("入库量" + in_qty);
		// 计算加权平均成本
		double new_price = (qty * stock_price + verifyin_atm) / (qty + in_qty);
		new_price = StringTool.round(new_price, 4);
		return new_price;
	}

	/**
	 * 新增出库作业(在途) / 耗损、其它出库作业、卫耗材、科室备药(出库即入库)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutOn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 申请单类型
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");
		// 单位类型
		String unit_type = parm.getValue("UNIT_TYPE");
		String batch_date = parm.getValue("BATCH_DATE");
		TParm parmD = parm.getParm("OUT_D");
		// System.out.println("parmD---"+parmD);
		// 药品出库(申请单出库)
		result = onIndDispenseOut(parmD, org_code, unit_type, request_type,
				batch_date, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新申请单状态及实际出入库数量
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;

	}

	/**
	 * 门诊补充计费和住院补充计费 药品出库(申请单出库)
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	public TParm onIndDispenseOut(TParm parm, String org_code,
			String unit_type, String request_type, String batch_date,
			TConnection conn) {
		String order_code = "";
		int seq = 1;
		double actual_qty = 0; // 药品需要的出库量
		TParm result = new TParm();
		TParm stock_parm = new TParm(); // 每个药品对应的库存
		double stock_qty = 0; // 每个药品所有的库存
		// 新的明细,合并
		TParm newParmD = parm;
		System.out.println("出库细项 onIndDispenseOut parm count :::: " + parm.getCount("ORDER_CODE"));
		for (int X = 0, count = parm.getCount("ORDER_CODE"); X < count; X++) {
			// 实际出库数量
			int i = X;
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
			stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
					org_code, order_code, "");
			if (stock_parm == null || stock_parm.getErrCode() < 0
					|| stock_parm.getCount() <= 0) {
				// 说明此药品没有库存
				System.out.println("**" + org_code + " @@@ " + order_code
						+ "**** 无库存");
				// TODO 无库存 不扣库， 但是实际出库为 需要出库量
				result = nonNomalIndDispenseD( parm, actual_qty, request_type,
						org_code, order_code, i, seq, unit_type, batch_date,
						conn);
				if(result.getErrCode() < 0) {
					return result;
				}
				seq++;
			} else {
				stock_qty = 0;
				for (int j = 0; j < stock_parm.getCount(); j++) {
					stock_qty += stock_parm.getDouble("QTY", j);
				}
				if (stock_qty < actual_qty) {
					System.out.println("**" + org_code + " @@@ " + order_code
							+ "**** 库存不足");
					// TODO 库存不足 不扣库， 但是实际出库为 需要出库量
					result = nonNomalIndDispenseD( parm, actual_qty,
							request_type, org_code, order_code, i, seq,
							unit_type, batch_date, conn);
					if(result.getErrCode() < 0) {
						return result;
					}
					seq++;
				} else {
					System.out.println("**" + org_code + " @@@ " + order_code
							+ "**** 库存充足，正常");
					for (int j = 0; j < stock_parm.getCount(); j++) {
						double qty = stock_parm.getDouble("QTY", j);
						int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
						if (qty >= actual_qty) {
							// 药品可以一次扣库
							double out_amt = StringTool.round(
									stock_parm.getDouble("STOCK_RETAIL_PRICE",
											j) * actual_qty, 2);
							// 更新库存(申请单出库)
							result = IndStockDTool.getInstance()
									.onUpdateQtyRequestOut(request_type,
											org_code, order_code, batch_seq,
											actual_qty, out_amt,
											parm.getValue("OPT_USER", i),
											parm.getTimestamp("OPT_DATE", i),
											parm.getValue("OPT_TERM", i), conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							// 新增出库单细项
							TParm inparm = new TParm();
							inparm.setData("DISPENSE_NO",
									parm.getValue("DISPENSE_NO", i));
							inparm.setData("SEQ_NO", seq);
							inparm.setData("REQUEST_SEQ",
									parm.getInt("REQUEST_SEQ", i));
							inparm.setData("ORDER_CODE",
									parm.getValue("ORDER_CODE", i));
							inparm.setData("BATCH_SEQ",
									stock_parm.getInt("BATCH_SEQ", j));
							inparm.setData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							inparm.setData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							// 修改取得转换率-发药单位的数量 by liyh 20120801
							double tranUnitQty = getDosageQty(order_code);

							if ("0".equals(unit_type)) {
								qty_out = actual_qty / tranUnitQty;
							} else {
								qty_out = actual_qty;
							}
							inparm.setData("QTY", qty_out);
							inparm.setData("UNIT_CODE",
									parm.getValue("UNIT_CODE", i));

							// 零售价取最小单位价格
							inparm.setData("RETAIL_PRICE", stock_parm
									.getDouble("STOCK_RETAIL_PRICE", j));

							// luhai modify 20120503 begin TEC，EXM，COS
							// 零售价用发药单位 end
							double stock_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j) * tranUnitQty;
							inparm.setData("STOCK_PRICE", stock_price);
							// luhai 2012-01-13 add verifyin_price begin

							// 备药生成时为最小单位不*转换率
							// 验收价格
							double verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j);
							inparm.setData("VERIFYIN_PRICE", verifyin_price);

							// 整盒价格
							double inventPrice = stock_parm.getDouble(
									"INVENT_PRICE", j);
							inparm.setData("INVENT_PRICE", inventPrice);

							// luhai 2012-01-13 add verifyin_price end
							inparm.setData("ACTUAL_QTY", qty_out);
							inparm.setData("PHA_TYPE",
									parm.getValue("PHA_TYPE", i));
							inparm.setData("OPT_USER",
									parm.getValue("OPT_USER", i));
							inparm.setData("OPT_DATE",
									parm.getTimestamp("OPT_DATE", i));
							inparm.setData("OPT_TERM",
									parm.getValue("OPT_TERM", i));
							inparm.setData("BOX_ESL_ID",
									parm.getValue("BOX_ESL_ID", i));
							inparm.setData("BOXED_USER",
									parm.getValue("BOXED_USER", i));
							inparm.setData("IS_BOXED",
									parm.getValue("IS_BOXED", i));
							inparm.setData("SUP_CODE",
									stock_parm.getValue("SUP_CODE", j));

							inparm.setData("SUP_ORDER_CODE",
									stock_parm.getValue("SUP_ORDER_CODE", j));
							result = IndDispenseDTool.getInstance().onInsertD(
									inparm, conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							seq++;
							break;
						} else {
							// 药品不可一次扣库
							double out_amt = StringTool.round(
									stock_parm.getDouble("STOCK_RETAIL_PRICE",
											j) * qty, 2);
							// 更新库存(申请单出库)
							result = IndStockDTool.getInstance()
									.onUpdateQtyRequestOut(request_type,
											org_code, order_code, batch_seq,
											qty, out_amt,
											parm.getValue("OPT_USER", i),
											parm.getTimestamp("OPT_DATE", i),
											parm.getValue("OPT_TERM", i), conn);
							if (result.getErrCode() < 0) {
								return result;
							}

							// 新增出库单细项
							TParm inparm = new TParm();
							inparm.setData("DISPENSE_NO",
									parm.getValue("DISPENSE_NO", i));
							inparm.setData("SEQ_NO", seq);
							inparm.setData("REQUEST_SEQ",
									parm.getInt("REQUEST_SEQ", i));
							inparm.setData("ORDER_CODE",
									parm.getValue("ORDER_CODE", i));
							inparm.setData("BATCH_SEQ",
									stock_parm.getInt("BATCH_SEQ", j));
							inparm.setData("BATCH_NO",
									stock_parm.getValue("BATCH_NO", j));
							inparm.setData("VALID_DATE",
									stock_parm.getData("VALID_DATE", j));
							// 修改取得转换率-发药单位的数量 by liyh 20120801
							double tranUnitQty = getDosageQty(order_code);
							/*
							 * double tranUnitQty =
							 * getPhaTransUnitQty(order_code, "2") *
							 * getPhaTransUnitQty(order_code, "1");
							 */
							if ("0".equals(unit_type)) {
								qty_out = qty / tranUnitQty;
							} else {
								qty_out = qty;
							}
							inparm.setData("QTY", qty_out);
							inparm.setData("UNIT_CODE",
									parm.getValue("UNIT_CODE", i));

							// 零售价取最小单位价格
							inparm.setData("RETAIL_PRICE", stock_parm
									.getDouble("STOCK_RETAIL_PRICE", j));

							double stock_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j) * tranUnitQty;
							inparm.setData("STOCK_PRICE", stock_price);

							double verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j);
							inparm.setData("VERIFYIN_PRICE", verifyin_price);

							// 整盒价格
							double inventPrice = stock_parm.getDouble(
									"INVENT_PRICE", j);
							inparm.setData("INVENT_PRICE", inventPrice);

							inparm.setData("ACTUAL_QTY", qty_out);
							inparm.setData("PHA_TYPE",
									parm.getValue("PHA_TYPE", i));
							inparm.setData("OPT_USER",
									parm.getValue("OPT_USER", i));
							inparm.setData("OPT_DATE",
									parm.getTimestamp("OPT_DATE", i));
							inparm.setData("OPT_TERM",
									parm.getValue("OPT_TERM", i));
							inparm.setData("BOX_ESL_ID",
									parm.getValue("BOX_ESL_ID", i));
							inparm.setData("BOXED_USER",
									parm.getValue("BOXED_USER", i));
							inparm.setData("IS_BOXED",
									parm.getValue("IS_BOXED", i));
							inparm.setData("SUP_CODE",
									stock_parm.getValue("SUP_CODE", j));
							inparm.setData("SUP_ORDER_CODE",
									stock_parm.getValue("SUP_ORDER_CODE", j));
							result = IndDispenseDTool.getInstance().onInsertD(
									inparm, conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							seq++;
							// 更新出库量
							actual_qty = actual_qty - qty;
							// 更新实际出库数量
							qty_out = qty_out + qty;
						}
					}

				}

			}

			// 出库新增更新再途量 yuanxm更改 2013/5/6新增要求 ,入库减掉
			TParm searchParm = new TParm();
			searchParm.setData("ORG_CODE", org_code);
			searchParm.setData("ORDER_CODE", order_code);
			result = IndStockMTool.getInstance().onQuery(searchParm);
			if (result.getCount("ORG_CODE") > 0) {

				/** 在途量更新 */
				TParm inparm1 = new TParm();
				inparm1.setData("ORG_CODE", org_code);
				inparm1.setData("ORDER_CODE", order_code);
				// 验收量
				inparm1.setData("BUY_UNRECEIVE_QTY", actual_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm1, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		for (int i = 0; i < newParmD.getCount("ORDER_CODE"); i++) {
			TParm rowParm = newParmD.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			actual_qty = rowParm.getDouble("ACTUAL_QTY");
			if ("0".equals(unit_type)) {
				actual_qty = actual_qty * getPhaTransUnitQty(order_code, "2");
			} 
			TParm updateParm = new TParm();
			updateParm.setData("ORDER_CODE", orderCode);
			updateParm.setData("ORG_CODE", org_code);
			updateParm.setData("LOCK_QTY", actual_qty);
			result = IndStockMTool.getInstance().onUpdateMinusLockQty(
					updateParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param stock_parm
	 *            ：库存parm
	 * @param result
	 *            ：结果
	 * @param parm
	 *            ：入库parm
	 * @param actual_qty
	 *            ：需求量
	 * @param request_type
	 *            ：请求类型
	 * @param org_code
	 *            ：接受部门
	 * @param order_code
	 *            ：药品代码
	 * @param i
	 *            ：i
	 * @param seq
	 *            ：seq
	 * @param unit_type
	 *            ：单位
	 * @param conn
	 *            ：TConnection
	 * @return
	 */
	private TParm nonNomalIndDispenseD(TParm parm,
			double actual_qty, String request_type, String org_code,
			String order_code, int i, int seq, String unit_type,
			String batch_date, TConnection conn) {
		// 1.新增出库单细项
		TParm result = new TParm();
		TParm inparm = new TParm();
		inparm.setData("DISPENSE_NO", parm.getValue("DISPENSE_NO", i));
		inparm.setData("SEQ_NO", seq);
		inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
		inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));

		double tranUnitQty = getDosageQty(order_code);
		double qty_out = 0;
		if ("0".equals(unit_type)) {
			qty_out = actual_qty / tranUnitQty;
		} else {
			qty_out = actual_qty;
		}
		inparm.setData("QTY", qty_out);

		inparm.setData("BATCH_SEQ", "");
		inparm.setData("BATCH_NO", "");
		inparm.setData("VALID_DATE", "");
		inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
		// 零售价取最小单位价格
		inparm.setData("RETAIL_PRICE", 0);
		// 零售价用发药单位 end
		inparm.setData("STOCK_PRICE", 0);
		// 验收价格
		inparm.setData("VERIFYIN_PRICE", 0);
		// 整盒价格
		inparm.setData("INVENT_PRICE", 0);
		// 供应商
		inparm.setData("SUP_CODE", "");
		// 上级供应商
		inparm.setData("SUP_ORDER_CODE", "");
		inparm.setData("ACTUAL_QTY", qty_out);
		inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
		inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
		inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
		inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
		inparm.setData("BOX_ESL_ID", parm.getValue("BOX_ESL_ID", i));
		inparm.setData("BOXED_USER", parm.getValue("BOXED_USER", i));
		inparm.setData("IS_BOXED", parm.getValue("IS_BOXED", i));
		result = IndDispenseDTool.getInstance().onInsertD(inparm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		// 2.将库存不足的药品插入到 IND_AUTOMED_LACK
		TParm autoMedLackParm = new TParm();
		autoMedLackParm.setData("BATCH_DATE", batch_date);
		autoMedLackParm.setData("ORG_CODE", org_code);
		autoMedLackParm.setData("ORDER_CODE", order_code);
		autoMedLackParm.setData("ACTUAL_QTY", qty_out);
		autoMedLackParm.setData("QTY", 0);
		autoMedLackParm.setData("STATUS", "0");
		autoMedLackParm.setData("DISPENSE_NO", parm.getValue("DISPENSE_NO", i));
		autoMedLackParm.setData("SEQ_NO", seq);
		result = INDAutoMedLackTool.getInstance().insertAllData(
				autoMedLackParm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		return result;
	}

	/**
	 * 更新申请单的状态和实际出入库数量
	 * 
	 * @param request_no
	 *            String
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateRequestFlgAndActual(String request_no, TParm parm,
			TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("REQUEST_SEQ"); i++) {
			String update_flg = "1";
			double qty = parm.getDouble("QTY", i);
			double actual_qty = parm.getDouble("ACTUAL_QTY", i);
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", request_no);
			inparm.setData("SEQ_NO", parm.getInt("REQUEST_SEQ", i));
			inparm.setData("ACTUAL_QTY", actual_qty);
			inparm.setData("OPT_USER", parm.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM", i));
			if (qty == actual_qty) {
				update_flg = "3";
			}
			inparm.setData("UPDATE_FLG", update_flg);
			result = IndRequestDTool.getInstance().onUpdateActualQty(inparm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// 更新申请单状态
		TParm parmT = new TParm(TJDODBTool.getInstance().update(
				INDSQL.onUpdateRequestFlg(request_no), conn));
		if (parmT.getErrCode() < 0) {
			return parmT;
		}
		return result;
	}

	/**
	 * 取得药品转换率
	 * 
	 * @param order_code
	 *            药品代码
	 * @param qty_type
	 *            转换率类型 1:进货/库存 2:库存/配药 3:配药/开药 (大单位-->小单位)
	 * @return
	 */
	public double getPhaTransUnitQty(String order_code, String qty_type) {
		TParm parm = new TParm(Manager.getMedicine()
				.getPhaTransUnit(order_code));
		if ("1".equals(qty_type)) {
			return parm.getDouble("STOCK_QTY", 0);
		} else if ("2".equals(qty_type)) {
			return parm.getDouble("DOSAGE_QTY", 0);
		} else if ("3".equals(qty_type)) {
			return parm.getDouble("MEDI_QTY", 0);
		}
		return 1;
	}

	/**
	 * 得到药品的发药-库存转换率
	 * 
	 * @return
	 */
	public double getDosageQty(String orderCode) {
		double dosageQty = 1;
		Map select = TJDODBTool.getInstance().select(
				INDSQL.getPHAInfoByOrder(orderCode));
		TParm orderParm = new TParm(select);
		if (orderParm.getCount() <= 0) {
			return 1;
		}
		dosageQty = orderParm.getDouble("DOSAGE_QTY", 0);
		return dosageQty;
	}

	/**
	 * 根据药品的batchseq ordercode 从ind_stock中得到验收时的价格 luhai add 2012-05-03
	 * 
	 * @return
	 */
	public double getVerifyInStockPrice(String orgCode, String orderCode,
			int batchSeq) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndVerifyInPrice(orgCode, orderCode, batchSeq)));
		if (result.getErrCode() < 0) {
			return 0;
		} else if (result.getCount() <= 0) {
			return 0;
		} else {
			return result.getDouble("VERIFYIN_PRICE", 0);
		}
	}
	

	
	
	
}
