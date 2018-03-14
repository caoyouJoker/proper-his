package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCAccountVerifyinTool extends TJDOTool {
	
	private static SPCAccountVerifyinTool instanceObject;

	/**
	 * 构造器
	 */
	public SPCAccountVerifyinTool() {

	}

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCAccountVerifyinTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCAccountVerifyinTool();
		}
		return instanceObject;
	}
	
	/**
	 * 查询结算数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getSpcAccount(TParm parm) {
		String closeDate = parm.getValue("CLOSE_DATE");
		String sql ="SELECT A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION ," +
				        "SUM(A.ACCOUNT_QTY) AS ACCOUNT_QTY,A.CONTRACT_PRICE AS CONTRACT_PRICE," +
				        "SUM(A.ACCOUNT_QTY*A.CONTRACT_PRICE) AS ACCOUNT_AMT FROM IND_ACCOUNT A,PHA_BASE B" +
				        " WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CLOSE_DATE='"+closeDate+"' " +
				        		"GROUP BY A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,A.CONTRACT_PRICE ORDER BY A.ORDER_CODE DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询结算数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getVerifyin(TParm parm) {
		String verifyinDate = parm.getValue("VERIFYIN_DATE");
		String orderCode = parm.getValue("ORDER_CODE");
		String sql ="SELECT SUM(B.VERIFYIN_QTY) AS VERIFYIN_QTY ,B.VERIFYIN_PRICE," +
				"SUM(B.VERIFYIN_QTY*B.VERIFYIN_PRICE ) AS VERIFYIN_AMT FROM IND_VERIFYINM A,IND_VERIFYIND B WHERE A.VERIFYIN_NO=B.VERIFYIN_NO AND A.VERIFYIN_DATE=TO_DATE('"+verifyinDate+"','yyyy-mm-dd hh24:mi:ss') AND B.ORDER_CODE='"+orderCode+"' GROUP BY B.ORDER_CODE,B.VERIFYIN_PRICE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询结算数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getAccountForDis(TParm parm) {
		String closeDate = parm.getValue("CLOSE_DATE");
		String orgCode = parm.getValue("ORG_CODE");
		String sql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION ,SUM(A.ACCOUNT_QTY) AS ACCOUNT_QTY,A.CONTRACT_PRICE AS CONTRACT_PRICE,SUM(A.ACCOUNT_QTY*A.CONTRACT_PRICE) AS ACCOUNT_AMT FROM IND_ACCOUNT A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CLOSE_DATE='"+closeDate+"' " ;
		if(orgCode!=null&&!"".equals(orgCode)) {
			sql = sql+" AND A.ORG_CODE='"+orgCode+"' ";
		}
		sql = sql+"GROUP BY A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,A.CONTRACT_PRICE ORDER BY A.ORDER_CODE DESC";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询结算数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getDispense(TParm parm) {
		String dispenseDate = parm.getValue("DISPENSE_DATE");
		String orderCode = parm.getValue("ORDER_CODE");
		String orgCode = parm.getValue("ORG_CODE");
		String sql ="SELECT SUM(B.QTY ) AS DISPENSE_QTY ,B.VERIFYIN_PRICE ," +
				"SUM(B.QTY*B.VERIFYIN_PRICE) AS DISPENSE_AMT FROM IND_DISPENSEM A," +
				"IND_DISPENSED B WHERE A.DISPENSE_NO =B.DISPENSE_NO AND A.DISPENSE_DATE =TO_DATE('"+dispenseDate+"','yyyy-mm-dd hh24:mi:ss') AND B.ORDER_CODE='"+orderCode+"'" ;
		if(orgCode!=null&&!"".equals(orgCode)) {
			sql = sql +"AND A.APP_ORG_CODE='"+orgCode+"' ";
		}
			sql = sql+"GROUP BY B.ORDER_CODE,B.VERIFYIN_PRICE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
}
