package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title: 住院账务明细档工具类</p>
 *
 * <p>Description: 住院账务明细档工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSBilldTool extends TJDOTool{
    /**
     * 实例
     */
    public static IBSBilldTool instanceObject;
    /**
     * 得到实例
     * @return IBSBilldTool
     */
    public static IBSBilldTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSBilldTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSBilldTool() {
        setModuleName("ibs\\IBSBilldModule.x");
        onInit();
    }
    /**
     * 新增
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertDdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm){
        TParm result = new TParm();
        result = update("deletedata",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询账单明细档所有数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBilldTool.selectAllData()参数异常!");
            return result;
        }
        result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 查询账单数据(缴费作业)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForCharge(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBilldTool.selDataForCharge()参数异常!");
            return result;
        }
        result = query("selDataForCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 作废账单
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataDate(TParm parm, TConnection connection) {
    TParm result = new TParm();
    String sql =
        " UPDATE IBS_BILLD SET REFUND_FLG = 'Y',"+
        "                      REFUND_BILL_NO='"+parm.getValue("REFUND_BILL_NO")+"',"+
        "                      REFUND_CODE='"+parm.getValue("REFUND_CODE")+"',"+
        "                      REFUND_DATE = SYSDATE "+
        "  WHERE BILL_NO = '"+parm.getValue("BILL_NO")+"' "+
        "    AND REFUND_FLG <> 'Y'";
//    System.out.println("作废账单"+sql);
    result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
    
    /**
	 * 修改身份
	 * 
	 * @author caowl
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataDateforCTZ(TParm parm, TConnection connection) {

		TParm result = new TParm();

		String sql = "UPDATE  IBS_BILLD SET REFUND_FLG = 'Y' "
				+ ",REFUND_DATE =SYSDATE " 
				+ ",REFUND_CODE = '"+parm.getValue("REFUND_CODE")+"' ,REFUND_BILL_NO = '"+parm.getValue("REFUND_BILL_NO")+"' "
				+ "WHERE BILL_NO ='" + parm.getData("BILL_NO") + "'";

		//System.out.println("sql--->"+sql);
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
