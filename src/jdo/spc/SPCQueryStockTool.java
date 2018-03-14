package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:查询库存结余Tool
 * </p>
 * 
 * <p>
 * Description:查询库存结余Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-05-16
 * @version 1.0
 */
public class SPCQueryStockTool extends TJDOTool {

	private static SPCQueryStockTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCQueryStockTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCQueryStockTool();
		}
		return instanceObject;
	}

	/**
	 * 查询
	 */
	public TParm queryStock(TParm parm) {
		String dept_code = parm.getValue("DEPT_CODE");
		String trandate = parm.getValue("TRANDATE");
		String sql = "SELECT A.SUP_ORDER_CODE,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,A.VERIFYIN_PRICE, "
				+ "SUM(A.STOCK_QTY) AS STOCK_QTY_SMALL,J.UNIT_CHN_DESC AS DOSAGE_UNIT,D.DOSAGE_QTY,B.CONVERSION_RATIO AS CONVERT_PER, "
				+ "FLOOR (SUM(A.STOCK_QTY) / D.DOSAGE_QTY) || I.UNIT_CHN_DESC || "
				+ "CASE WHEN MOD (SUM(A.STOCK_QTY), D.DOSAGE_QTY) > 0 "
				+ "THEN MOD (SUM(A.STOCK_QTY), D.DOSAGE_QTY) || J.UNIT_CHN_DESC "
				+ "ELSE '' END AS STOCK_QTY_SPELL "
				+ "FROM IND_DDSTOCK A,IND_CODE_MAP B,PHA_BASE C,PHA_TRANSUNIT D,SYS_UNIT I,SYS_UNIT J "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.SUP_CODE = B.SUP_CODE "
				//+ "AND A.SUP_ORDER_CODE = B.SUP_ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND I.UNIT_CODE = D.STOCK_UNIT "
				+ "AND J.UNIT_CODE = D.DOSAGE_UNIT "
				+ "AND A.TRANDATE = '"
				+ trandate + "' ";
		if (!StringUtil.isNullString(dept_code)) {
			sql += "AND A.ORG_CODE = '" + dept_code + "' ";
		}
		sql += "GROUP BY A.SUP_ORDER_CODE,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,A.VERIFYIN_PRICE, "
				+ "D.DOSAGE_UNIT,D.DOSAGE_QTY,B.CONVERSION_RATIO,I.UNIT_CHN_DESC, "
				+ "J.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
