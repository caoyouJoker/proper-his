package jdo.ekt;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class EKTNewTool extends TJDOTool{
	/**
	 * 构造器
	 */
	public EKTNewTool() {
		setModuleName("ekt\\EKTNewModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static EKTNewTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return EKTTool
	 */
	public static EKTNewTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTNewTool();
		return instanceObject;
	}
	/**
	 * 查询此病患已收费未打票的所有数据汇总金额
	 * @return
	 */
	public TParm selectEktTrade(TParm parm){
		String sql="SELECT TRADE_NO,CARD_NO,CARD_NO, MR_NO, CASE_NO, PAT_NAME, "+
                    "OLD_AMT, AMT, STATE,BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT, RESET_TRADE_NO,OPT_USER, OPT_DATE, "+
                    "OPT_TERM FROM EKT_TRADE "+
                   "WHERE CASE_NO="+parm.getValue("CASE_NO")+" AND STATE='1' "+
              	          "AND BUSINESS_TYPE IN ("+parm.getValue("EKT_TRADE_TYPE")+")";  
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 打票操作 查询所有此次操作的金额
	 * @return
	 */
	public TParm selectEktSumTrade(TParm parm){
		String sql = "SELECT SUM(AMT) AS AMT, SUM(GREEN_BUSINESS_AMT) AS GREEN_BUSINESS_AMT FROM EKT_TRADE "
				+"WHERE CASE_NO='" + parm.getValue("CASE_NO") + "' AND TRADE_NO IN ("+ parm.getValue("TRADE_SUM_NO")
				+ ") ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	/**
	 * 保退票操作 扣除医疗卡金额查询此病患此收据号码所有的不同的内部交易号
	 * @return
	 */
	public TParm selectBusinessByReceiptNo(TParm parm){
		return	query("selectBusinessByReceiptNo", parm);	
	}
	
	/**
	 * 医生站的增删改数据查询 通过处方签查询所有要操作的医嘱
	 * @param parm
	 * @return
	 */
	public TParm selectOpdRxNo(TParm parm){
		return	query("selectOpdRxNo", parm);	
	}
	/**
	 * 扣款冲负数据查询
	 * @param parm
	 * @return
	 */
	public TParm selectTradeNo(TParm parm) {
		if(parm.getValue("TRADE_SUM_NO").length()<=0){
			TParm result=new TParm();
			result.setCount(-1);
			return result;
		}
		String sql = "SELECT TRADE_NO,CARD_NO,CARD_NO, MR_NO, CASE_NO, PAT_NAME, "
				+ "OLD_AMT, AMT, STATE,BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT,RESET_TRADE_NO, OPT_USER, OPT_DATE, "
				+ "OPT_TERM FROM EKT_TRADE "
				+ "WHERE CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND TRADE_NO IN ("+parm.getValue("TRADE_SUM_NO")+") ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	/**
	 * 查询此就诊病患所有数据汇总金额
	 * @return
	 */
	public TParm selectSumOrderUnBillFlg(TParm parm){
		return	query("selectSumOrderUnBillFlg", parm);	
	}
	/**
	 * 查询此次操作收费的数据需要退还金额
	 * @return
	 */
	public TParm selectEktTradeUnSum(TParm parm){
		return	query("selectEktTradeUnSum", parm);	
	}
	
	/**
	 * 添加数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertEktTrade(TParm parm,TConnection connection){
		return update("insertEktTrade", parm, connection);
	}
	/**
	 * 删除数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteEktTrade(TParm parm,TConnection connection){
		return update("deleteEktTrade", parm, connection);
	}
	/**
	 * 作废数据修改状态
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateOpdEktTrade(TParm parm,TConnection connection){
		return update("updateOpdEktTrade", parm, connection);
	}
	/**
	 * 医保退费操作 将修改OPD_ORDER 表中内部交易号码
	 */
	public TParm updateOpdOrderBusinessNo(TParm parm,TConnection connection){
		return update("updateOpdOrderBusinessNo", parm, connection);
	}
	
	/**
	 * 修改添加票号
	 * @return
	 */
	public TParm updateEktTradeByPrintNo(TParm parm,TConnection connection){
		return update("updateEktTreadeByPrintNo", parm, connection);
	}
	/**
	 * 更新医疗卡主表余额
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("updateEKTMaster", parm, connection);
		return result;
	}
	/**
	 * 添加医疗卡主表余额
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("insertEKTMaster", parm, connection);
		return result;
	}
	/**
	 * 删除医疗卡主表余额
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("deleteEKTMaster", parm, connection);
		return result;
	}
}
