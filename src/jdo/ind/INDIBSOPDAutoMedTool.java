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
 * Title: �������Զ���ҩ����Tool
 * </p>
 * 
 * <p>
 * Description:�������Զ���ҩ����Tool
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
	 * ʵ��
	 */
	public static INDIBSOPDAutoMedTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static INDIBSOPDAutoMedTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDIBSOPDAutoMedTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INDIBSOPDAutoMedTool() {
		onInit();
	}

	/**
	 * ����������ҵ(���⼴���)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutIn(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		String batch_date = parm.getValue("BATCH_DATE");
		// ���뵥����
		String request_type = parm.getValue("REQTYPE_CODE");
		String out_org_code = parm.getValue("OUT_ORG_CODE");
		String in_org_code = parm.getValue("IN_ORG_CODE");
		// ��λ����
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// ���ע��
		boolean in_flg = parm.getBoolean("IN_FLG");
		// �ж��Ƿ��Զ����ɱ��۴��������
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// ҩƷ����(���뵥�������)
		result = onIndDispenseOutIn(parmD, out_org_code, in_org_code,
				unit_type, request_type, in_flg, reuprice_flg, batch_date, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������뵥״̬��ʵ�ʳ��������
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ҩƷ�������(���뵥�������)
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
		// �����Ϣ
		TParm stock_in = new TParm();
		TParm stock_parm = new TParm();
		TParm result = new TParm();
		// ������ҵ
		System.out.println("����ϸ�� onIndDispenseOutIn parm count :::: " + parm.getCount("ORDER_CODE"));
		for (int X = 0, count = parm.getCount("ORDER_CODE"); X < count; X++) {
			int i = X;
			// ʵ�ʳ�������
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
						+ "**** û�п��");
				// TODO ��治�� ���ۿ⣬ ����ʵ�ʳ���Ϊ ��Ҫ������
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
							+ "**** ��治��");
					// TODO ��治�� ���ۿ⣬ ����ʵ�ʳ���Ϊ ��Ҫ������
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
							// ҩƷ����һ�οۿ�
							double out_amt = StringTool.round(
									stock_parm.getDouble("RETAIL_PRICE", j)
											* actual_qty, 2);
							// ���¿��(���뵥����)
							result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
									request_type, out_org_code, order_code, batch_seq,
									actual_qty, out_amt, parm.getValue("OPT_USER", i),
									parm.getTimestamp("OPT_DATE", i),
									parm.getValue("OPT_TERM", i), conn);
							// ���������Ϣ
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

							// �������ⵥϸ��
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
							// ҩƷ����һ�οۿ�
							double out_amt = StringTool.round(
									stock_parm.getDouble("RETAIL_PRICE", i) * qty, 2);
							// ���¿��(���뵥����)
							result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
									request_type, out_org_code, order_code, batch_seq,
									qty, out_amt, parm.getValue("OPT_USER", i),
									parm.getTimestamp("OPT_DATE", i),
									parm.getValue("OPT_TERM", i), conn);

							// ���������Ϣ
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

							// �������ⵥϸ��
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
							// ���³�����
							actual_qty = actual_qty - qty;
							// ����ʵ�ʳ�������
							qty_out = qty_out + qty;
						}
					}
					
				}
				
			}
		}

		// �����ҵ
		if (in_flg) {
			// �������ź�Ч�ڲ�ѯҩƷ,���ڵĻ����¿����,�����ڵĻ�ȡ���������Ž�������
			List list = new ArrayList();
			double dosage_qty = 0;
			int batch_seq = 0;
			double retail_price = 0;
			for (int i = 0; i < stock_in.getCount("ORDER_CODE"); i++) {
				order_code = stock_in.getValue("ORDER_CODE", i);
				// ������б�
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

			/** �ƶ���Ȩƽ���ɱ� */
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

			/** �����۸��� */
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
	 * ������ƽ���� (��Ȩƽ���ɱ�)
	 * 
	 * @param order_code
	 *            ҩƷ����
	 * @param verifyin_atm
	 *            ���ս��
	 * @param in_qty
	 *            �����(��ҩ��λ)
	 * @return ��Ȩƽ���ɱ�
	 */
	public double getPhaBaseStockPrice(String order_code, double verifyin_atm,
			double in_qty) {
		TParm result = new TParm();
		// ȡ�ÿ����
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		result = IndStockDTool.getInstance().onQueryStockQTY(parm);
		double qty = result.getDouble("QTY", 0);
		// System.out.println("�����" + qty);
		// ȡ��ԭ�ӳ�ƽ���ɱ�
		result = PhaBaseTool.getInstance().selectByOrder(order_code);
		double stock_price = result.getDouble("STOCK_PRICE", 0);
		// System.out.println("ԭ�ӳ�ƽ���ɱ�" + stock_price);
		// System.out.println("���ս��" + verifyin_atm);
		// System.out.println("�����" + in_qty);
		// �����Ȩƽ���ɱ�
		double new_price = (qty * stock_price + verifyin_atm) / (qty + in_qty);
		new_price = StringTool.round(new_price, 4);
		return new_price;
	}

	/**
	 * ����������ҵ(��;) / ��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutOn(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���뵥����
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");
		// ��λ����
		String unit_type = parm.getValue("UNIT_TYPE");
		String batch_date = parm.getValue("BATCH_DATE");
		TParm parmD = parm.getParm("OUT_D");
		// System.out.println("parmD---"+parmD);
		// ҩƷ����(���뵥����)
		result = onIndDispenseOut(parmD, org_code, unit_type, request_type,
				batch_date, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������뵥״̬��ʵ�ʳ��������
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;

	}

	/**
	 * ���ﲹ��ƷѺ�סԺ����Ʒ� ҩƷ����(���뵥����)
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
		double actual_qty = 0; // ҩƷ��Ҫ�ĳ�����
		TParm result = new TParm();
		TParm stock_parm = new TParm(); // ÿ��ҩƷ��Ӧ�Ŀ��
		double stock_qty = 0; // ÿ��ҩƷ���еĿ��
		// �µ���ϸ,�ϲ�
		TParm newParmD = parm;
		System.out.println("����ϸ�� onIndDispenseOut parm count :::: " + parm.getCount("ORDER_CODE"));
		for (int X = 0, count = parm.getCount("ORDER_CODE"); X < count; X++) {
			// ʵ�ʳ�������
			int i = X;
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
			stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
					org_code, order_code, "");
			if (stock_parm == null || stock_parm.getErrCode() < 0
					|| stock_parm.getCount() <= 0) {
				// ˵����ҩƷû�п��
				System.out.println("**" + org_code + " @@@ " + order_code
						+ "**** �޿��");
				// TODO �޿�� ���ۿ⣬ ����ʵ�ʳ���Ϊ ��Ҫ������
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
							+ "**** ��治��");
					// TODO ��治�� ���ۿ⣬ ����ʵ�ʳ���Ϊ ��Ҫ������
					result = nonNomalIndDispenseD( parm, actual_qty,
							request_type, org_code, order_code, i, seq,
							unit_type, batch_date, conn);
					if(result.getErrCode() < 0) {
						return result;
					}
					seq++;
				} else {
					System.out.println("**" + org_code + " @@@ " + order_code
							+ "**** �����㣬����");
					for (int j = 0; j < stock_parm.getCount(); j++) {
						double qty = stock_parm.getDouble("QTY", j);
						int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
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
											parm.getValue("OPT_USER", i),
											parm.getTimestamp("OPT_DATE", i),
											parm.getValue("OPT_TERM", i), conn);
							if (result.getErrCode() < 0) {
								return result;
							}
							// �������ⵥϸ��
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
							// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
							double tranUnitQty = getDosageQty(order_code);

							if ("0".equals(unit_type)) {
								qty_out = actual_qty / tranUnitQty;
							} else {
								qty_out = actual_qty;
							}
							inparm.setData("QTY", qty_out);
							inparm.setData("UNIT_CODE",
									parm.getValue("UNIT_CODE", i));

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
							// ҩƷ����һ�οۿ�
							double out_amt = StringTool.round(
									stock_parm.getDouble("STOCK_RETAIL_PRICE",
											j) * qty, 2);
							// ���¿��(���뵥����)
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

							// �������ⵥϸ��
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
							// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
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
							// ���³�����
							actual_qty = actual_qty - qty;
							// ����ʵ�ʳ�������
							qty_out = qty_out + qty;
						}
					}

				}

			}

			// ��������������;�� yuanxm���� 2013/5/6����Ҫ�� ,������
			TParm searchParm = new TParm();
			searchParm.setData("ORG_CODE", org_code);
			searchParm.setData("ORDER_CODE", order_code);
			result = IndStockMTool.getInstance().onQuery(searchParm);
			if (result.getCount("ORG_CODE") > 0) {

				/** ��;������ */
				TParm inparm1 = new TParm();
				inparm1.setData("ORG_CODE", org_code);
				inparm1.setData("ORDER_CODE", order_code);
				// ������
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
	 *            �����parm
	 * @param result
	 *            �����
	 * @param parm
	 *            �����parm
	 * @param actual_qty
	 *            ��������
	 * @param request_type
	 *            ����������
	 * @param org_code
	 *            �����ܲ���
	 * @param order_code
	 *            ��ҩƷ����
	 * @param i
	 *            ��i
	 * @param seq
	 *            ��seq
	 * @param unit_type
	 *            ����λ
	 * @param conn
	 *            ��TConnection
	 * @return
	 */
	private TParm nonNomalIndDispenseD(TParm parm,
			double actual_qty, String request_type, String org_code,
			String order_code, int i, int seq, String unit_type,
			String batch_date, TConnection conn) {
		// 1.�������ⵥϸ��
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
		// ���ۼ�ȡ��С��λ�۸�
		inparm.setData("RETAIL_PRICE", 0);
		// ���ۼ��÷�ҩ��λ end
		inparm.setData("STOCK_PRICE", 0);
		// ���ռ۸�
		inparm.setData("VERIFYIN_PRICE", 0);
		// ���м۸�
		inparm.setData("INVENT_PRICE", 0);
		// ��Ӧ��
		inparm.setData("SUP_CODE", "");
		// �ϼ���Ӧ��
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

		// 2.����治���ҩƷ���뵽 IND_AUTOMED_LACK
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
	 * �������뵥��״̬��ʵ�ʳ��������
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
		// �������뵥״̬
		TParm parmT = new TParm(TJDODBTool.getInstance().update(
				INDSQL.onUpdateRequestFlg(request_no), conn));
		if (parmT.getErrCode() < 0) {
			return parmT;
		}
		return result;
	}

	/**
	 * ȡ��ҩƷת����
	 * 
	 * @param order_code
	 *            ҩƷ����
	 * @param qty_type
	 *            ת�������� 1:����/��� 2:���/��ҩ 3:��ҩ/��ҩ (��λ-->С��λ)
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
	 * �õ�ҩƷ�ķ�ҩ-���ת����
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
	 * ����ҩƷ��batchseq ordercode ��ind_stock�еõ�����ʱ�ļ۸� luhai add 2012-05-03
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
