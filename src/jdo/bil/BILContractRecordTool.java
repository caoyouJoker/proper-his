package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:记账单位账务结算 </p>
 *
 * <p>Description:记账单位账务结算 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordTool extends TJDOTool {
    /**
     * 实例
     */
    public static BILContractRecordTool instanceObject;
    /**
     * 得到实例
     * @return BILAccountTool
     */
    public static BILContractRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILContractRecordTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILContractRecordTool() {
        setModuleName("bil\\BILContractRecondModule.x");
        onInit();
    }
    /**
     * 添加数据方法
     * @param parm TParm
     * @return TParm
     */
    public TParm insertRecode(TParm parm,TConnection connection) {
        TParm result = update("insertRecode", parm,connection);
        result=getisError(result);
        return result;

    }
    /**
     * 出现错误显示
     * @param result TParm
     * @return TParm
     */
    private TParm getisError(TParm result) {
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
        }
        return result;
    }
    /**
     *
     * 查询方法
     * @param result TParm
     * @return TParm
     */
    public TParm recodeQuery(TParm parm){
        TParm result = query("selRecode", parm);
        result=getisError(result);
        return result;
    }
    /**
     *  修改方法
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRecode(TParm parm,TConnection connection){
         TParm result = update("updateRecode", parm,connection);
         result=getisError(result);
         return result;
    }
    /**
    *
    * 查询方法
    * @param result TParm
    * @return TParm
    */
   public TParm regRecodeQuery(TParm parm){
       TParm result = query("selRegRecode", parm);
       result=getisError(result);
       return result;
   }

}
