package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 出入库管理明细Tool
 * </p>
 *
 * <p>
 * Description: 出入库管理明细Tool
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
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndDispenseDTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockMTool
     */
    public static IndDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDispenseDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndDispenseDTool() {
        setModuleName("spc\\INDDispenseDModule.x");
        onInit();
    }

    /**
     * 新增细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertD(TParm parm, TConnection conn) {
        TParm result = this.update("createNewDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertDAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("createNewDispenseDAcnt", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateD(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateDAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateDispenseDAcnt", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新细项(门急诊药房出麻精)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateDDrug(TParm parm, TConnection conn) {
        TParm result = this.update("updateDDrug", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 门急诊药房出麻精查询
     * @param parm
     * @return
     */
    public TParm onQueryDispenseDDrug(TParm parm){
    	 TParm result = this.query("queryDDrug", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * 查询是否全部上架
     * @param parm
     * @return
     */
    public TParm onQueryDispenseD(TParm parm){
    	 TParm result = this.query("queryDispenseD", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * 查询是否全部上架
     * @param parm
     * @return
     */
    public TParm onQueryDispenseDAcnt(TParm parm){//wanglong add 20150202
         TParm result = this.query("queryDispenseDAcnt", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * 更新细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateDToxic(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseDToxic", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
