package jdo.spc.accountinf;

import java.util.List;

import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 物联网结算接口-实现
 * @author liyanhui
 *
 */
public class IndAccountDaoImpl extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static IndAccountDaoImpl instanceObject;
	
	//成功标示
	public static final String RESULT_FLA_SUCCESS = "1";
	//失败标示
	public static final String RESULT_FLA_ERROR = "-1";
	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static IndAccountDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new IndAccountDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IndAccountDaoImpl() {
		onInit();
	}
	
	
	/**
	 * 保存物联网结算数据
	 * @param indAccounts
	 * @return
	 */
	public  String onSave(IndAccounts indAccounts){
		List<IndAccount> list = indAccounts.getIndAccounts();
		TParm result = new TParm();
		String msg = "";
		if(null != list && list.size()>0){
			String delSql = deleteIndAccount(null);
			//先删除
			result= new TParm(TJDODBTool.getInstance().update(delSql));
			if (result.getErrCode()<0) {
				msg = RESULT_FLA_ERROR;
				return msg;
			}			
			for (IndAccount vo : list) {
				Long qty = vo.getAccountQty();
				if (qty<=0) {
					continue;
				}
				String sql = onSaveIndAccoun(vo);
				//再保存
				result= new TParm(TJDODBTool.getInstance().update(sql));
				if (result.getErrCode()<0) {
					msg = RESULT_FLA_ERROR;
					break;
				}
				msg = RESULT_FLA_SUCCESS;
			}
			
/*			if (RESULT_FLA_SUCCESS.equals(msg)) {
				result = INDTool.getInstance().createIndRequsestAuto("", "AUTO_ID", "AUTO_IP", "H01");
			}*/
		}
		return msg;
	}
	
	/**
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }
	/**
	 * 保存
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
    private static String onSaveIndAccoun(IndAccount vo){
		String sql = " INSERT INTO IND_ACCOUNT " +
					 "  ( CLOSE_DATE, ORG_CODE, ORDER_CODE, LAST_ODD_QTY, OUT_QTY, TOTAL_OUT_QTY, TOTAL_UNIT_CODE, VERIFYIN_PRICE," +
					 "    VERIFYIN_AMT, ACCOUNT_QTY, ACCOUNT_UNIT_CODE, ODD_QTY, ODD_AMT, OPT_USER, OPT_DATE,OPT_TERM)" +
					 "  VALUES" +
					 "   ('"+ vo.getCloseDate() +"', '"+ vo.getOrgCode() +"', '"+ vo.getOrderCode() +"', "+ vo.getLastOddQty() +"," +
					 " "+ vo.getOutQty() +",  "+ vo.getTotalOutQty() +", '"+ vo.getTotalUnitCode() +"',"+ vo.getVerifyinPrice() +"," +
					 " "+ vo.getVerifyinAmt() +", "+ vo.getAccountQty() +"," + " '"+ vo.getAccountUnitCode() +"', "+ vo.getOddQty() +"," +
					 " "+ vo.getOddAmt() +", '"+ vo.getOptUser() +"',sysdate, '"+ vo.getOptTerm() +"') " ;
		return sql;
	} 	
	/**
	 * 删除
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
    private static String deleteIndAccount(IndAccount vo){
		String sql = " DELETE  IND_ACCOUNT " ;
		return sql;
	} 	
   

}
