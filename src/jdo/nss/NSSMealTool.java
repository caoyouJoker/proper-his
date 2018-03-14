package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 餐次设置档</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSMealTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSMealTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSMealTool
     */
    public static NSSMealTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSMealTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSMealTool() {
        setModuleName("nss\\NSSMealModule.x");
        onInit();
    }

    /**
     * 查询餐次设置档
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSMeal(TParm parm) {
        TParm result = this.query("queryNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增餐次设置档
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新餐次设置档
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除餐次设置档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
