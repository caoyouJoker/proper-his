package jdo.ind;

import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.IndDispenseDTool;
import jdo.spc.IndStockDTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 自动备药批次却药Tool
 * </p>
 * 
 * <p>
 * Description: 自动备药批次却药Tool
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
 * @author wukai 2009.04.29
 * @version 1.0
 */
public class INDAutoMedLackTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INDAutoMedLackTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndStockDTool
	 */
	public static INDAutoMedLackTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDAutoMedLackTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INDAutoMedLackTool() {
		setModuleName("ind\\INDAutoMedLackModule.x");
		onInit();
	}

	/**
	 * 插入所有字段数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertAllData(TParm parm, TConnection conn) {
		 TParm result = this.update("insert", parm, conn);
		if (result == null || result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectForDispense(TParm parm) {
		TParm result = this.query("queryForDispense", parm);
		if (result == null || result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectForDispenseB(TParm parm) {
		TParm result = this.query("queryForDispenseB", parm);
		if (result == null || result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateForDispense(TParm parm, TConnection connection) {
		String sql = "UPDATE IND_AUTOMED_LACK SET " + "	QTY = "
				+ parm.getData("QTY") + ", STATUS='" + parm.getData("STATUS")
				+ "'" + " WHERE BATCH_DATE = '" + parm.getData("BATCH_DATE")
				+ "'" + " AND ORG_CODE= '" + parm.getData("ORG_CODE") + "'"
				+ " AND ORDER_CODE= '" + parm.getData("ORDER_CODE") + "'"
				+ " AND DISPENSE_NO= '" + parm.getData("DISPENSE_NO") + "'"
				+ " AND SEQ_NO= " + parm.getData("SEQ_NO");
//		System.out.println("updateForDispense sql:::: " + sql);
		TParm result = new TParm();
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		// parm = this.update("updateForDispense", connection);
		if (result == null || result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteIndDispenseD(TParm parm, TConnection connection) {
		//parm = this.update("deleteIndDispenseD", connection);
		String sql = "DELETE FROM IND_DISPENSED " + " WHERE DISPENSE_NO = '"
				+ parm.getData("DISPENSE_NO") + "' " + " AND SEQ_NO = "
				+ parm.getData("SEQ_NO");
//		System.out.println("updateForDispense sql:::: " + sql);
		TParm result =  new TParm(TJDODBTool.getInstance().update(sql, connection));
		//TParm result = this.update("deleteIndDispenseD", connection);
		if (result == null || result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 
	 * @param parm
	 * @return
	 */
	public int selectMaxSeq(TParm parm) {
		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM IND_DISPENSED "
				+ " WHERE DISPENSE_NO= '" +  parm.getData("DISPENSE_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result == null || result.getErrCode() < 0 || result.getCount("SEQ_NO") <= 0) {
			err(result.getErrText());
			return 0;
		}
		return result.getInt("SEQ_NO", 0);
	}
	
	/**
	 * 查询库存，不校验效期
	 * @param org_code
	 * @param order_code
	 * @return
	 */
	public double selectStockQty(String org_code, String order_code) {
		String sql = "SELECT SUM(STOCK_QTY) AS QTY FROM IND_STOCK WHERE ORG_CODE='"
				+ org_code + "' AND ORDER_CODE='" + order_code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount("QTY") <= 0) {
			return 0;
		} else {
			return Double.parseDouble(parm.getData("QTY", 0) + "");
		}
	}
	
	/**
	 * 查询库存，校验效期
	 * @param org_code
	 * @param order_code
	 * @return
	 */
	public double selectStockQtyVaildDate(String org_code, String order_code) {
		String sql = "SELECT SUM(STOCK_QTY) AS QTY FROM IND_STOCK WHERE ORG_CODE='"
				+ org_code + "' AND ORDER_CODE='" + order_code + "' AND SYSDATE < VALID_DATE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount("QTY") <= 0) {
			return 0;
		} else {
			return Double.parseDouble(parm.getData("QTY", 0) + "");
		}
	}
	
	
	/**
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDispenseOutForAutoMedLack(TParm parm, TConnection conn) {
		TParm result = new TParm();
		TParm updateParm = new TParm();
		double old_qty = 0;
		for(int i = 0; i < parm.getCount("BATCH_DATE"); i++) {
			//1.更新IND_AUTOMED_LACK表
			updateParm = parm.getRow(i);
			System.out.println(updateParm);
			result = updateForDispense(updateParm, conn);
			if(result.getErrCode() < 0 || result == null) {
				return result;
			}
			
			//2.在已出库量为0的情况下删除IND_DISPENSED表数据,重新出库，否则不删除数据。
			//old_qty = 0;
			old_qty = parm.getDouble("OLD_QTY", i);
			if(old_qty <= 0) {
				result = deleteIndDispenseD(updateParm, conn);
				if(result.getErrCode() < 0 || result == null) {
					return result;
				}
			}
			
			//3.出库补档操作
			int seq = selectMaxSeq(updateParm) + 1;
			result = onDispenseOut(updateParm, conn, seq);
			if(result.getErrCode() < 0 || result == null) {
				return result;
			}
		}
		return result;
		
	}

	/**
	 * 
	 * 自动备药批次 出库补档 INDAutoMedLack
	 * @return
	 */
	public TParm onDispenseOut(TParm parm, TConnection conn, int seq) {
		TParm result = new TParm();
		double actual_qty = parm.getDouble("OUT_QTY");  //本次总出库量
		double qty = 0;  //库存量
		double qty_out = 0; //出库量
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String request_type = "EXM";
		TParm stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(org_code, order_code, "");
		String unit_type = "";
		for (int j = 0; j < stock_parm.getCount(); j++) {
			qty = stock_parm.getDouble("QTY", j);
			int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
			unit_type = parm.getValue("UNIT_TYPE");
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
								parm.getValue("OPT_USER"),
								parm.getTimestamp("OPT_DATE"),
								parm.getValue("OPT_TERM"), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				// 新增出库单细项
				TParm inparm = new TParm();
				inparm.setData("DISPENSE_NO",
						parm.getValue("DISPENSE_NO"));
				inparm.setData("SEQ_NO", seq);
				inparm.setData("REQUEST_SEQ",
						parm.getInt("REQUEST_SEQ"));
				inparm.setData("ORDER_CODE", order_code);
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
						parm.getValue("UNIT_CODE"));

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
				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE"));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				inparm.setData("BOX_ESL_ID",
						parm.getValue("BOX_ESL_ID"));
				inparm.setData("BOXED_USER",
						parm.getValue("BOXED_USER"));
				inparm.setData("IS_BOXED",
						parm.getValue("IS_BOXED"));
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
								parm.getValue("OPT_USER"),
								parm.getTimestamp("OPT_DATE"),
								parm.getValue("OPT_TERM"), conn);
				if (result.getErrCode() < 0) {
					return result;
				}

				// 新增出库单细项
				TParm inparm = new TParm();
				inparm.setData("DISPENSE_NO",
						parm.getValue("DISPENSE_NO"));
				inparm.setData("SEQ_NO", seq);
				inparm.setData("REQUEST_SEQ",
						parm.getInt("REQUEST_SEQ"));
				inparm.setData("ORDER_CODE",
						parm.getValue("ORDER_CODE"));
				inparm.setData("BATCH_SEQ",
						stock_parm.getInt("BATCH_SEQ", j));
				inparm.setData("BATCH_NO",
						stock_parm.getValue("BATCH_NO", j));
				inparm.setData("VALID_DATE",
						stock_parm.getData("VALID_DATE", j));
				// 修改取得转换率-发药单位的数量 by liyh 20120801
				double tranUnitQty = getDosageQty(order_code);
				if ("0".equals(unit_type)) {
					qty_out = qty / tranUnitQty;
				} else {
					qty_out = qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE",
						parm.getValue("UNIT_CODE"));

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
						parm.getValue("PHA_TYPE"));
				inparm.setData("OPT_USER",
						parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE",
						parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM",
						parm.getValue("OPT_TERM"));
				inparm.setData("BOX_ESL_ID",
						parm.getValue("BOX_ESL_ID"));
				inparm.setData("BOXED_USER",
						parm.getValue("BOXED_USER"));
				inparm.setData("IS_BOXED",
						parm.getValue("IS_BOXED"));
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
		
		return result;
	}
	
	/**
	 * 得到药品的发药-库存转换率
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
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
	
	
}
