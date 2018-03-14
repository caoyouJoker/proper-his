package jdo.ind.inf;

import java.util.List;

import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * ����������ӿ�-ʵ��
 * 
 * @author liyanhui
 * 
 */
public class IndAccountDaoImpl extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static IndAccountDaoImpl instanceObject;

	// �ɹ���ʾ
	public static final String RESULT_FLA_SUCCESS = "1";
	// ʧ�ܱ�ʾ
	public static final String RESULT_FLA_ERROR = "-1";

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static IndAccountDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new IndAccountDaoImpl();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public IndAccountDaoImpl() {
		onInit();
	}

	/**
	 * ������������������
	 * 
	 * @param indAccounts
	 * @return
	 */
	public String onSave(IndAccounts indAccounts) {
		List<IndAccount> list = indAccounts.getIndAccounts();
		/**
		 * ȥ���ظ��ϲ�
		 */
		for (int i = 0; i < list.size(); i++) {
			IndAccount indAccount = (IndAccount) list.get(i);
			String orgCode = indAccount.getOrgCode();
			String orderCode = indAccount.getOrderCode();
			long dosageQty = indAccount.getAccountQty();
			double lastOddQty = indAccount.getLastOddQty();
			double outQty = indAccount.getOutQty();
			double totalOutQty = indAccount.getTotalOutQty();
			double verifyinPrice = indAccount.getVerifyinPrice();
			double verifyinAmt = indAccount.getVerifyinAmt();
			double oddQty = indAccount.getOddQty();
			double oddAmt = indAccount.getOddAmt();
			for (int j = i + 1; j < list.size(); j++) {
				IndAccount account = (IndAccount) list.get(j);
				String orgCodeNew = account.getOrgCode();
				String orderCodeNew = account.getOrderCode();
				long dosageQtyNew = account.getAccountQty();
				double lastOddQtyNew = account.getLastOddQty();
				double outQtyNew = account.getOutQty();
				double totalOutQtyNew = account.getTotalOutQty();
				double verifyinPriceNew = account.getVerifyinPrice();
				double verifyinAmtNew = account.getVerifyinAmt();
				double oddQtyNew = account.getOddQty();
				double oddAmtNew = account.getOddAmt();
				if (orgCode.equals(orgCodeNew)
						&& orderCode.equals(orderCodeNew)) {
					dosageQty += dosageQtyNew;
					lastOddQty += lastOddQtyNew;
					outQty += outQtyNew;
					totalOutQty += totalOutQtyNew;
					verifyinPrice += verifyinPriceNew;
					verifyinAmt += verifyinAmtNew;
					oddQty += oddQtyNew;
					oddAmt += oddAmtNew;
					indAccount.setAccountQty(dosageQty);
					indAccount.setLastOddQty(lastOddQty);
					indAccount.setOutQty(outQty);
					indAccount.setTotalOutQty(totalOutQty);
					indAccount.setVerifyinPrice(verifyinPrice);
					indAccount.setVerifyinAmt(verifyinAmt);
					indAccount.setOddQty(oddQty);
					indAccount.setOddAmt(oddAmt);
					list.remove(j);
					j--;
				}
			}
		}
		TParm result = new TParm();
		String msg = "";
		if (null != list && list.size() > 0) {
			String delSql = deleteIndAccount(null);
			// ��ɾ��
			result = new TParm(TJDODBTool.getInstance().update(delSql));
			if (result.getErrCode() < 0) {
				msg = RESULT_FLA_ERROR;
				return msg;
			}
			for (IndAccount vo : list) {
				Long qty = vo.getAccountQty();
				if (qty <= 0) {
					continue;
				}
				String sql = onSaveIndAccoun(vo);
				// �ٱ���
				result = new TParm(TJDODBTool.getInstance().update(sql));
				if (result.getErrCode() < 0) {
					msg = RESULT_FLA_ERROR;
					break;
				}
				msg = RESULT_FLA_SUCCESS;
			}
			String delCodeSql = deleteIndCodeMap(null);
			// ��ɾ��
			result = new TParm(TJDODBTool.getInstance().update(delCodeSql));
			if (result.getErrCode() < 0) {
				msg = RESULT_FLA_ERROR;
				return msg;
			}
			List<IndCodeMap> indCodeMaps = indAccounts.getIndCodeMaps();
			for (int a = 0; a < indCodeMaps.size(); a++) {
				IndCodeMap indCodeMap = indCodeMaps.get(a);
				String sql = onSaveIndCodeMap(indCodeMap);
				// �ٱ���
				result = new TParm(TJDODBTool.getInstance().update(sql));
				if (result.getErrCode() < 0) {
					msg = RESULT_FLA_ERROR;
					break;
				}  
			}

			/*List<IndAccount> accountlist = indAccounts.getIndAccounts();
			if (null != accountlist && accountlist.size() > 0) {
				String delaccountSql = deleteIndAccountSpc(null);
				// ��ɾ��
				result = new TParm(TJDODBTool.getInstance().update(
						delaccountSql));
				if (result.getErrCode() < 0) {
					msg = RESULT_FLA_ERROR;
					return msg;
				}*/
				/*for (IndAccount vo : accountlist) {
					Long qty = vo.getAccountQty();
					if (qty <= 0) {
						continue;
					}
					String sql = onSaveIndAccounSpc(vo);
					// �ٱ���
					result = new TParm(TJDODBTool.getInstance().update(sql));
					if (result.getErrCode() < 0) {
						msg = RESULT_FLA_ERROR;
						break;
					}
					msg = RESULT_FLA_SUCCESS;
				}*/
			//}	   
		}
		return msg;
	}

	/**
	 * ҩ�������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm getSysParm() {
		return IndSysParmTool.getInstance().onQuery();
	}

	/**
	 * ����
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	private static String onSaveIndAccoun(IndAccount vo) {
		String sql = " INSERT INTO IND_ACCOUNT "
				+ "  ( CLOSE_DATE, ORG_CODE, ORDER_CODE, LAST_ODD_QTY, OUT_QTY, TOTAL_OUT_QTY, TOTAL_UNIT_CODE, VERIFYIN_PRICE,"
				+ "    VERIFYIN_AMT, ACCOUNT_QTY, ACCOUNT_UNIT_CODE, ODD_QTY, ODD_AMT, OPT_USER, OPT_DATE,OPT_TERM,SUP_CODE,SUP_ORDER_CODE,CONTRACT_PRICE)"
				+ "  VALUES" + "   ('"
				+ vo.getCloseDate()
				+ "', '"
				+ vo.getOrgCode()
				+ "', '"
				+ vo.getOrderCode()
				+ "', "
				+ vo.getLastOddQty()
				+ ","
				+ " "
				+ vo.getOutQty()
				+ ",  "
				+ vo.getTotalOutQty()
				+ ", '"
				+ vo.getTotalUnitCode()
				+ "',"
				+ vo.getVerifyinPrice()
				+ ","
				+ " "
				+ vo.getVerifyinAmt()
				+ ", "
				+ vo.getAccountQty()
				+ ","
				+ " '"
				+ vo.getAccountUnitCode()
				+ "', "
				+ vo.getOddQty()
				+ ","
				+ " "
				+ vo.getOddAmt()
				+ ", '"
				+ vo.getOptUser()
				+ "',sysdate, '"
				+ vo.getOptTerm()
				+ "','"
				+ vo.getSupCode()
				+ "','"
				+ vo.getSupOrderCode()
				+ "',"
				+ vo.getContractPrice()
				+ ") ";
		return sql;
	}

	/**
	 * ����
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	private static String onSaveIndAccounSpc(IndAccount vo) {
		String sql = " INSERT INTO IND_ACCOUNT_SPC "
				+ "  ( CLOSE_DATE, ORG_CODE, ORDER_CODE, LAST_ODD_QTY, OUT_QTY, TOTAL_OUT_QTY, TOTAL_UNIT_CODE, VERIFYIN_PRICE,"
				+ "    VERIFYIN_AMT, ACCOUNT_QTY, ACCOUNT_UNIT_CODE, ODD_QTY, ODD_AMT, OPT_USER, OPT_DATE,OPT_TERM,SUP_CODE,SUP_ORDER_CODE,CONTRACT_PRICE)"
				+ "  VALUES" + "   ('"
				+ vo.getCloseDate()
				+ "', '"
				+ vo.getOrgCode()
				+ "', '"
				+ vo.getOrderCode()
				+ "', "
				+ vo.getLastOddQty()
				+ ","
				+ " "
				+ vo.getOutQty()
				+ ",  "
				+ vo.getTotalOutQty()
				+ ", '"
				+ vo.getTotalUnitCode()
				+ "',"
				+ vo.getVerifyinPrice()
				+ ","
				+ " "
				+ vo.getVerifyinAmt()
				+ ", "
				+ vo.getAccountQty()
				+ ","
				+ " '"
				+ vo.getAccountUnitCode()
				+ "', "
				+ vo.getOddQty()
				+ ","
				+ " "
				+ vo.getOddAmt()
				+ ", '"
				+ vo.getOptUser()
				+ "',sysdate, '"
				+ vo.getOptTerm()
				+ "','"
				+ vo.getSupCode()
				+ "','"
				+ vo.getSupOrderCode()
				+ "',"
				+ vo.getContractPrice()
				+ ") ";
		return sql;
	}

	/**
	 * �����ֵ��
	 * 
	 * @param vo
	 * @return
	 */
	private static String onSaveIndCodeMap(IndCodeMap vo) {
		String sql = " INSERT INTO IND_CODE_MAP "
				+ " (SUP_CODE,SUP_ORDER_CODE,ORDER_CODE,SUPPLY_UNIT_CODE,CONVERSION_RATIO,OPT_USER,OPT_DATE,OPT_TERM)"
				+ " VALUES " + " ('" + vo.getSupCode() + "','"
				+ vo.getSupOrderCode() + "','" + vo.getOrderCode() + "','"
				+ vo.getSupplyUnitCode() + "'," + vo.getConversionRatio()
				+ ",'" + vo.getOptUser() + "',sysdate,'" + vo.getOptTerm()
				+ "')";
		return sql;
	}

	/**
	 * ɾ��
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	private static String deleteIndAccount(IndAccount vo) {
		String sql = " DELETE  IND_ACCOUNT ";
		return sql;
	}

	/**
	 * ɾ��
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	private static String deleteIndCodeMap(IndAccount vo) {
		String sql = " DELETE  IND_CODE_MAP ";
		return sql;
	}

	/**
	 * ɾ��
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	private static String deleteIndAccountSpc(IndAccount vo) {
		String sql = " DELETE  IND_ACCOUNT_SPC ";
		return sql;
	}

}
