package jdo.clp;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 临床路径评估标准</p>
 *
 * <p>Description: 临床路径评估标准</p>
 *
 * <p>Copyright: Copyright (c) javahis 2011</p>
 *
 * <p>Company: 深圳中航 </p>
 *
 * @author ZhenQin 2011-05-04
 * @version 4.0
 */
public class CLPAssessStandardTool
    extends TJDOTool {

    private static CLPAssessStandardTool jdo = null;

    /**
     * 不能通过构造方法取得对象的实例
     */
    private CLPAssessStandardTool() {
        super();
        setModuleName("clp\\CLPAssessStandardModule.x");
        onInit();
    }

    /**
     * 得到实例
     * @return CLPCureScheduleStatusTool
     */
    public static CLPAssessStandardTool getInstance() {
        if (jdo == null){
            jdo = new CLPAssessStandardTool();
        }
        return jdo;
    }

    /**
     * 查询clp为key的model
     * @param clp model的key
     * @param param TParm 参数
     * @return TParm
     */
    public TParm queryCLP_EVL_CAT(String clp, TParm param){
        TParm result = null;
        result = this.query(clp, param);
        if(result.getErrCode() < 0){
            err(result.getErrName() + result.getErrText());
        }
        return result;

    }

    /**
     * 查询数据库中标的最大值,或者数据总和,这个用来新增评估标准创建code
     * @param clp model的key
     * @param param 参数,在返回值中,
     * 包含了MAXCODE字段,通过TParm.getValue("MAXCODE", 0)可以得到该值
     * @return
     */
    public TParm queryMaxCode(String clp, TParm param){
        TParm result = null;
        result = this.query(clp, param);
        if(result.getErrCode() < 0){
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * 插入一条状态,需要提供REGION_CODE
     * @param clp model的key
     * @param param 参数,在返回值中,
     * @return TParm
     */
    public TParm insertCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }



    /**
     * 更新状态,更新时需要提供CAT1_CODE
     * @param param TParm
     * @return TParm 返回更新状态
     */
    public TParm updateCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * 删除,需要提供CAT1_CODE
     * @param param 需要提供CAT1_CODE
     * @return TParm 返回删除状态
     */
    public TParm deleteCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * 删除,需要提供CAT1_CODE
     * @param param TParm
     * @return TParm 返回执行状态
     */
    public TParm deleteCLP_EVL_CAT(String clp, TParm param, TConnection con) {
        TParm result = null;
        result = this.update(clp, param, con);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

}
