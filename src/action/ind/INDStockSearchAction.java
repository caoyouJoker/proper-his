package action.ind;

import java.sql.Timestamp;
import java.util.List;

import jdo.ind.IndStockDTool;

import action.ind.stockclient.SpcStockService_SpcStockServiceImplPort_Client;
import action.ind.stockclient.StockClient;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: webservice同步物联网库存
 * </p>
 * 
 * <p>
 * Description: webservice同步物联网库存
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author fuwj 2013.04.18
 * @version 1.0
 */
public class INDStockSearchAction extends TAction {

	/**
	 * 同步物联网麻精药库存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSearchIndStock(TParm searchParm) {
		TConnection conn = getConnection();
		String sql = "DELETE FROM IND_STOCK WHERE " +
				"ORDER_CODE IN(SELECT A.ORDER_CODE FROM IND_STOCK A,PHA_BASE B," +
				"SYS_CTRLDRUGCLASS C WHERE  A.ORDER_CODE=B.ORDER_CODE AND " +
				"B.CTRLDRUGCLASS_CODE=C.CTRLDRUGCLASS_CODE AND C.CTRL_FLG='Y')";
		TParm deleteResult = new TParm(TJDODBTool.getInstance().update(sql,
				conn));
		if (deleteResult.getErrCode() < 0) {
			err("ERR:" + deleteResult.getErrCode() + deleteResult.getErrText()
					+ deleteResult.getErrName());
			conn.close();
			return deleteResult;
		}
		List<action.ind.stockclient.IndStock> list = StockClient
				.onSearchIndStock();
		if (list.size() <= 0) {
			conn.close();
			return null;
		}
		TParm result = null;
		for (int i = 0; i < list.size(); i++) {
			TParm parm = new TParm();
			action.ind.stockclient.IndStock indStock = list.get(i);
			parm.setData("ORG_CODE", indStock.getOrgCode());
			parm.setData("ORDER_CODE", indStock.getOrderCode());
			parm.setData("BATCH_SEQ", indStock.getBatchSeq());
			parm.setData("BATCH_NO", indStock.getBatchNo());
			parm.setData("VALID_DATE", Timestamp.valueOf(indStock
					.getValidDate()));
			parm.setData("REGION_CODE", indStock.getRegionCode());
			parm.setData("ACTIVE_FLG", indStock.getActiveFlg());
			parm.setData("STOCK_FLG", indStock.getStockFlg());
			parm.setData("READJUSTP_FLG", indStock.getReadjustpFlg());
			parm.setData("STOCK_QTY", indStock.getStockQty());
			parm.setData("LAST_TOTSTOCK_QTY", indStock.getLastTotstockQty());
			parm.setData("LAST_TOTSTOCK_AMT", indStock.getLastTotstockAmt());
			parm.setData("IN_QTY", indStock.getInQty());
			parm.setData("IN_AMT", indStock.getInAmt());
			parm.setData("OUT_QTY", indStock.getOutQty());
			parm.setData("OUT_AMT", indStock.getOutAmt());
			parm.setData("CHECKMODI_QTY", indStock.getCheckmodiQty());
			parm.setData("CHECKMODI_AMT", indStock.getCheckmodiAmt());
			parm.setData("VERIFYIN_QTY", indStock.getVerifyinQty());
			parm.setData("VERIFYIN_AMT", indStock.getVerifyinAmt());
			parm.setData("FAVOR_QTY", indStock.getFavorQty());
			parm.setData("REGRESSGOODS_QTY", indStock.getRegressgoodsQty());
			parm.setData("REGRESSGOODS_AMT", indStock.getRegressgoodsAmt());
			parm.setData("DOSEAGE_QTY", indStock.getDoseageQty());
			parm.setData("DOSAGE_AMT", indStock.getDosageAmt());
			parm.setData("REGRESSDRUG_QTY", indStock.getRegressdrugQty());
			parm.setData("REGRESSDRUG_AMT", indStock.getRegressdrugAmt());
			parm.setData("FREEZE_TOT", indStock.getFreezeTot());
			parm.setData("PROFIT_LOSS_AMT", indStock.getProfitLossAmt());
			parm.setData("VERIFYIN_PRICE", indStock.getVerifyinPrice());
			parm.setData("STOCKIN_QTY", indStock.getStockinQty());
			parm.setData("STOCKIN_AMT", indStock.getStockinAmt());
			parm.setData("STOCKOUT_QTY", indStock.getStockoutQty());
			parm.setData("STOCKOUT_AMT", indStock.getStockoutAmt());
			parm.setData("OPT_USER", indStock.getOptUser());
			parm.setData("OPT_DATE", Timestamp.valueOf(indStock.getOptDate()));
			parm.setData("OPT_TERM", indStock.getOptTerm());
			parm.setData("REQUEST_IN_QTY", indStock.getRequestInQty());
			parm.setData("REQUEST_IN_AMT", indStock.getRequestInAmt());
			parm.setData("REQUEST_OUT_QTY", indStock.getRequestOutQty());
			parm.setData("REQUEST_OUT_AMT", indStock.getRequestOutAmt());
			parm.setData("GIF_IN_QTY", indStock.getGifInQty());
			parm.setData("GIF_IN_AMT", indStock.getGifInAmt());
			parm.setData("GIF_OUT_QTY", indStock.getGifOutQty());
			parm.setData("GIF_OUT_AMT", indStock.getGifOutAmt());
			parm.setData("RET_IN_QTY", indStock.getRetInQty());
			parm.setData("RET_IN_AMT", indStock.getRetInAmt());
			parm.setData("RET_OUT_QTY", indStock.getRetOutQty());
			parm.setData("RET_OUT_AMT", indStock.getRetOutAmt());
			parm.setData("WAS_OUT_QTY", indStock.getWasOutQty());
			parm.setData("WAS_OUT_AMT", indStock.getWasOutAmt());
			parm.setData("THO_OUT_QTY", indStock.getThoOutQty());
			parm.setData("THO_OUT_AMT", indStock.getThoOutAmt());
			parm.setData("THI_IN_QTY", indStock.getThiInQty());
			parm.setData("THI_IN_AMT", indStock.getThiInAmt());
			parm.setData("COS_OUT_QTY", indStock.getCosOutQty());
			parm.setData("COS_OUT_AMT", indStock.getCosOutAmt());
			parm.setData("RETAIL_PRICE", indStock.getRetailPrice());
			parm.setData("MATERIAL_LOC_CODE", indStock.getMaterialLocCode());
			result = IndStockDTool.getInstance().onInsert(parm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}
			conn.commit();
			conn.close();
		}
		return result;
	}

}
