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
 * Title: �Զ���ҩ����ȴҩTool
 * </p>
 * 
 * <p>
 * Description: �Զ���ҩ����ȴҩTool
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
	 * ʵ��
	 */
	public static INDAutoMedLackTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndStockDTool
	 */
	public static INDAutoMedLackTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDAutoMedLackTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INDAutoMedLackTool() {
		setModuleName("ind\\INDAutoMedLackModule.x");
		onInit();
	}

	/**
	 * ���������ֶ�����
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
	 * ����
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
	 * ��ѯ��棬��У��Ч��
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
	 * ��ѯ��棬У��Ч��
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
			//1.����IND_AUTOMED_LACK��
			updateParm = parm.getRow(i);
			System.out.println(updateParm);
			result = updateForDispense(updateParm, conn);
			if(result.getErrCode() < 0 || result == null) {
				return result;
			}
			
			//2.���ѳ�����Ϊ0�������ɾ��IND_DISPENSED������,���³��⣬����ɾ�����ݡ�
			//old_qty = 0;
			old_qty = parm.getDouble("OLD_QTY", i);
			if(old_qty <= 0) {
				result = deleteIndDispenseD(updateParm, conn);
				if(result.getErrCode() < 0 || result == null) {
					return result;
				}
			}
			
			//3.���ⲹ������
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
	 * �Զ���ҩ���� ���ⲹ�� INDAutoMedLack
	 * @return
	 */
	public TParm onDispenseOut(TParm parm, TConnection conn, int seq) {
		TParm result = new TParm();
		double actual_qty = parm.getDouble("OUT_QTY");  //�����ܳ�����
		double qty = 0;  //�����
		double qty_out = 0; //������
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
				// ҩƷ����һ�οۿ�
				double out_amt = StringTool.round(
						stock_parm.getDouble("STOCK_RETAIL_PRICE",
								j) * actual_qty, 2);
				// ���¿��(���뵥����)
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
				// �������ⵥϸ��
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
				// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
				double tranUnitQty = getDosageQty(order_code);

				if ("0".equals(unit_type)) {
					qty_out = actual_qty / tranUnitQty;
				} else {
					qty_out = actual_qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE",
						parm.getValue("UNIT_CODE"));

				// ���ۼ�ȡ��С��λ�۸�
				inparm.setData("RETAIL_PRICE", stock_parm
						.getDouble("STOCK_RETAIL_PRICE", j));

				// luhai modify 20120503 begin TEC��EXM��COS
				// ���ۼ��÷�ҩ��λ end
				double stock_price = stock_parm.getDouble(
						"VERIFYIN_PRICE", j) * tranUnitQty;
				inparm.setData("STOCK_PRICE", stock_price);
				// luhai 2012-01-13 add verifyin_price begin

				// ��ҩ����ʱΪ��С��λ��*ת����
				// ���ռ۸�
				double verifyin_price = stock_parm.getDouble(
						"VERIFYIN_PRICE", j);
				inparm.setData("VERIFYIN_PRICE", verifyin_price);

				// ���м۸�
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
				// ҩƷ����һ�οۿ�
				double out_amt = StringTool.round(
						stock_parm.getDouble("STOCK_RETAIL_PRICE",
								j) * qty, 2);
				// ���¿��(���뵥����)
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

				// �������ⵥϸ��
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
				// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
				double tranUnitQty = getDosageQty(order_code);
				if ("0".equals(unit_type)) {
					qty_out = qty / tranUnitQty;
				} else {
					qty_out = qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE",
						parm.getValue("UNIT_CODE"));

				// ���ۼ�ȡ��С��λ�۸�
				inparm.setData("RETAIL_PRICE", stock_parm
						.getDouble("STOCK_RETAIL_PRICE", j));

				double stock_price = stock_parm.getDouble(
						"VERIFYIN_PRICE", j) * tranUnitQty;
				inparm.setData("STOCK_PRICE", stock_price);

				double verifyin_price = stock_parm.getDouble(
						"VERIFYIN_PRICE", j);
				inparm.setData("VERIFYIN_PRICE", verifyin_price);

				// ���м۸�
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
				// ���³�����
				actual_qty = actual_qty - qty;
				// ����ʵ�ʳ�������
				qty_out = qty_out + qty;
			}
		}
		
		return result;
	}
	
	/**
	 * �õ�ҩƷ�ķ�ҩ-���ת����
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
