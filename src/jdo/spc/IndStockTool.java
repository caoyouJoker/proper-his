package jdo.spc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 药库库存内部公用方法及对外接口方法
 * </p>
 * 
 * <p>
 * Description: 药库库存内部公用方法及对外接口方法
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class IndStockTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static IndStockTool instanceObject;

	// 本次库存量
	private static final String SUM_STOCK_QTY = "SELECT SUM(STOCK_QTY) FROM IND_STOCK";

	/**
	 * 得到实例
	 * 
	 * @return IndStockTool
	 */
	public static IndStockTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndStockTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IndStockTool() {
		onInit();
	}

	/**
	 * 判断库存
	 * 
	 * @param org_code
	 *            药房
	 * @param order_code
	 *            医嘱
	 * @param dosage
	 *            用量
	 * @return boolean
	 */
//	public boolean InspectIndStock(String org_code, String order_code,
//			double dosage) {
//		String sql = SUM_STOCK_QTY + " WHERE ORG_CODE = '" + org_code
//				+ "' AND ORDER_CODE = '" + order_code + "'";
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return false;
//		}
//		Object sum = ((Vector) result.getVector("SUM(STOCK_QTY)").get(0))
//				.get(0);
//		if (TypeTool.getDouble(sum) >= dosage) {
//			return true;
//		}
//		return false;
//	}

	/**
	 * 查询指定药品
	 * 
	 * @param parm :
	 *            ORG_CODE,ORDER_CODE
	 * @return
	 */
//	public boolean onQueryStockM(TParm parm) {
//		if ("".equals(parm.getValue("ORG_CODE"))) {
//			return false;
//		}
//		if ("".equals(parm.getValue("ORDER_CODE"))) {
//			return false;
//		}
//		TParm result = IndStockMTool.getInstance().onQuery(parm);
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return false;
//		}
//		if (result == null) {
//			return false;
//		}
//		return true;
//	}

	/**
	 * 为指定药库新增药品
	 * 
	 * @param parm:
	 *            ORG_CODE,ORDER_CODE
	 * @param conn
	 * @return
	 */
//	public boolean onCreateNewStockM(TParm parm, TConnection conn) {
//		if ("".equals(parm.getValue("ORG_CODE"))) {
//			return false;
//		}
//		if ("".equals(parm.getValue("ORDER_CODE"))) {
//			return false;
//		}
//		parm.setData("OPT_USER", Operator.getID());
//		Timestamp date = StringTool.getTimestamp(new Date());
//		parm.setData("OPT_DATE", date);
//		parm.setData("OPT_TERM", Operator.getIP());
//		TParm result = IndStockMTool.getInstance().onInsert(parm, conn);
//		if (result.getErrCode() < 0) {
//			err("ERR:" + result.getErrCode() + result.getErrText()
//					+ result.getErrName());
//			return false;
//		}
//		if (result == null) {
//			return false;
//		}
//		return true;
//	}
}
