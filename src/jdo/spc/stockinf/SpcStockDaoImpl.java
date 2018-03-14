package jdo.spc.stockinf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.spc.SPCSysFeeTool;
import jdo.spc.stockinf.dto.IndAgent;
import jdo.spc.stockinf.dto.IndStock;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 同步药库麻精药品Dao实现类
 * </p>
 * 		
 * <p>
 * Description: 同步药库麻精药品Dao实现类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: blueocre
 * </p>
 * 
 * @author fuwj 2013-3-25
 * @version 4.0
 */
public class SpcStockDaoImpl extends TJDOTool {

	/**
	 * 实例
	 */
	public static SpcStockDaoImpl instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SpcStockDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcStockDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcStockDaoImpl() {
		onInit();
	}

	/**
	 * 查询毒麻药库存
	 * 
	 * @return
	 */
	public List<IndStock> onSearchIndStock() {
		String sql = "SELECT A.* "
				+ "FROM IND_STOCK A,PHA_BASE B,SYS_CTRLDRUGCLASS C "
				+ "WHERE  A.ORDER_CODE=B.ORDER_CODE "
				+ "AND B.CTRLDRUGCLASS_CODE=C.CTRLDRUGCLASS_CODE AND C.CTRL_FLG='Y'";
		List<IndStock> stockList = new ArrayList<IndStock>();     
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
		}
		for (int i = 0; i < result.getCount(); i++) {
			IndStock indStock = new IndStock();
			indStock.setOrgCode((String) result.getData("ORG_CODE", i));
			TParm parm = new TParm();
			parm.setData("ORDER_CODE", (String)result.getData("ORDER_CODE",i));
			TParm hisResult = SPCSysFeeTool.getInstance().queryHisOrderCode(parm);
			if (hisResult.getErrCode() < 0||hisResult.getCount()<=0) {		
				err("ERR: " + result.getErrCode() + result.getErrText());
				continue;
			}
			indStock.setOrderCode((String)hisResult.getData("HIS_ORDER_CODE",0));
			indStock.setBatchSeq(result.getInt("BATCH_SEQ", i));
			indStock.setBatchNo((String) result.getData("BATCH_NO", i));
			indStock.setValidDate(StringTool.getString((Timestamp) result
					.getData("VALID_DATE", i), "yyyy-MM-dd HH:mm:ss"));
			indStock.setRegionCode((String) result.getData("REGION_CODE", i));
			indStock.setMaterialLocCode((String) result.getData(
					"MATERIAL_LOC_CODE", i));		
			indStock.setActiveFlg((String) result.getData("ACTIVE_FLG", i));
			indStock.setStockFlg((String) result.getData("STOCK_FLG", i));
			indStock.setReadjustpFlg((String) result
					.getData("READJUSTP_FLG", i));
			indStock.setStockQty(result.getDouble("STOCK_QTY", i));
			indStock.setLastTotstockQty(result
					.getDouble("LAST_TOTSTOCK_QTY", i));
			indStock.setLastTotstockAmt(result
					.getDouble("LAST_TOTSTOCK_AMT", i));
			indStock.setInQty(result.getDouble("IN_QTY", i));
			indStock.setInAmt(result.getDouble("IN_AMT", i));
			indStock.setOutQty(result.getDouble("OUT_QTY", i));
			indStock.setOutAmt(result.getDouble("OUT_AMT", i));
			indStock.setCheckmodiQty(result.getDouble("CHECKMODI_QTY", i));
			indStock.setCheckmodiAmt(result.getDouble("CHECKMODI_AMT", i));
			indStock.setVerifyinQty(result.getDouble("VERIFYIN_QTY", i));
			indStock.setVerifyinAmt(result.getDouble("VERIFYIN_AMT", i));
			indStock.setFavorQty(result.getDouble("FAVOR_QTY", i));
			indStock
					.setRegressgoodsQty(result.getDouble("REGRESSGOODS_QTY", i));
			indStock
					.setRegressgoodsAmt(result.getDouble("REGRESSGOODS_AMT", i));
			indStock.setDoseageQty(result.getDouble("DOSEAGE_QTY", i));
			indStock.setDosageAmt(result.getDouble("DOSAGE_AMT", i));
			indStock.setRegressdrugQty(result.getDouble("REGRESSDRUG_QTY", i));
			indStock.setRegressdrugAmt(result.getDouble("REGRESSDRUG_AMT", i));
			indStock.setFreezeTot(result.getDouble("FREEZE_TOT", i));
			indStock.setProfitLossAmt(result.getDouble("PROFIT_LOSS_AMT", i));
			indStock.setVerifyinPrice(result.getDouble("VERIFYIN_PRICE", i));
			indStock.setStockinQty(result.getDouble("STOCKIN_QTY", i));
			indStock.setStockinAmt(result.getDouble("STOCKIN_AMT", i));
			indStock.setStockoutQty(result.getDouble("STOCKOUT_QTY", i));
			indStock.setStockoutAmt(result.getDouble("STOCKOUT_AMT", i));
			indStock.setOptUser((String) result.getData("OPT_USER", i));
			indStock.setOptDate(StringTool.getString((Timestamp) result
					.getData("OPT_DATE", i), "yyyy-MM-dd HH:mm:ss"));
			indStock.setOptTerm((String) result.getData("OPT_TERM", i));
			indStock.setRequestInQty(result.getDouble("REQUEST_IN_QTY", i));
			indStock.setRequestInAmt(result.getDouble("REQUEST_IN_AMT", i));
			indStock.setRequestOutQty(result.getDouble("REQUEST_OUT_QTY", i));
			indStock.setRequestOutAmt(result.getDouble("REQUEST_OUT_AMT", i));
			indStock.setGifInQty(result.getDouble("GIF_IN_QTY", i));
			indStock.setGifInAmt(result.getDouble("GIF_IN_AMT", i));
			indStock.setGifOutQty(result.getDouble("GIF_OUT_QTY", i));
			indStock.setGifOutAmt(result.getDouble("GIF_OUT_AMT", i));
			indStock.setRetInQty(result.getDouble("RET_IN_QTY", i));
			indStock.setRetInAmt(result.getDouble("RET_IN_AMT", i));
			indStock.setRetOutQty(result.getDouble("RET_OUT_QTY", i));
			indStock.setRetOutAmt(result.getDouble("RET_OUT_AMT", i));
			indStock.setWasOutQty(result.getDouble("WAS_OUT_QTY", i));
			indStock.setWasOutAmt(result.getDouble("WAS_OUT_AMT", i));
			indStock.setThoOutQty(result.getDouble("THO_OUT_QTY", i));
			indStock.setThoOutAmt(result.getDouble("THO_OUT_AMT", i));
			indStock.setThiInQty(result.getDouble("THI_IN_QTY", i));
			indStock.setThiInAmt(result.getDouble("THI_IN_AMT", i));
			indStock.setCosOutQty(result.getDouble("COS_OUT_QTY", i));
			indStock.setCosOutAmt(result.getDouble("COS_OUT_AMT", i));
			indStock.setRetailPrice(result.getDouble("RETAIL_PRICE", i));
			stockList.add(indStock);
		}
		return stockList;
	}
	
	
	public String onSaveIndAgent(IndAgent indAgent) {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE",indAgent.getOrderCode());
		parm.setData("MAIN_FLG",indAgent.getMainFlg());
		parm.setData("CONTRACT_NO",indAgent.getContractNo());
		parm.setData("CONTRACT_PRICE",indAgent.getContractPrice());
		parm.setData("LAST_ORDER_DATE",indAgent.getLastOrderDate());
		parm.setData("LAST_ORDER_QTY",indAgent.getLastOrderQty());
		parm.setData("LAST_ORDER_PRICE",indAgent.getLastOrderPrice());
		parm.setData("LAST_ORDER_NO",indAgent.getLastOrderNo());
		parm.setData("LAST_VERIFY_DATE",indAgent.getLastVerifyDate());
		parm.setData("LAST_VERIFY_PRICE",indAgent.getLastVerifyPrice());
		parm.setData("OPT_USER",indAgent.getOptUser());
		parm.setData("OPT_DATE",indAgent.getOptDate());
		parm.setData("OPT_TERM",indAgent.getOptTerm());
		TParm result = SPCSysFeeTool.getInstance().insertIndAgent(parm);
		if (result == null || result.getErrCode() < 0) {
            return  "fail";                       
       }   
		return "success";   
	}

}
