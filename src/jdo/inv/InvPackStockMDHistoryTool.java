package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvPackStockMDHistoryTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static InvPackStockMDHistoryTool instanceObject;

    /**
     * 构造器
     */
    public InvPackStockMDHistoryTool() {
        setModuleName("inv\\INVPackStockMDHistoryModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static InvPackStockMDHistoryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPackStockMDHistoryTool();
        return instanceObject;
    }

    /**
     * 插入手术包打包历史主表
     *
     * @param parm
     * @return
     */
    public TParm onInsertPackMHistory(TParm parm, TConnection conn) {
        TParm result = this.update("insertPackageMHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入手术包打包历史细表
     *
     * @param parm
     * @return
     */
    public TParm onInsertPackDHistory(TParm parm, TConnection conn) {
        TParm result = this.update("insertPackageDHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    
    /**
     * 插入手术包打包历史主表（重新打包阶段）
     *
     * @param parm
     * @return
     */
    public TParm onInsertRepackPackMHistory(TParm parm, TConnection conn) {
        TParm result = this.update("insertRepackPackageMHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入手术包打包历史细表（重新打包阶段）
     *
     * @param parm
     * @return
     */
    public TParm onInsertRepackPackDHistory(TParm parm, TConnection conn) {
        TParm result = this.update("insertRepackPackageDHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
