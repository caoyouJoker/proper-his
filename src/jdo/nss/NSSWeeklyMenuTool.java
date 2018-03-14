package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 每周套餐</p>
 *
 * <p>Description: 每周套餐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSWeeklyMenuTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSWeeklyMenuTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSWeeklyMenuTool
     */
    public static NSSWeeklyMenuTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSWeeklyMenuTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSWeeklyMenuTool() {
        setModuleName("nss\\NSSWeeklyMenuModule.x");
        onInit();
    }

    /**
     * 查询每周套餐
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSWeeklyMenu(TParm parm) {
        TParm result = this.query("queryNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增每周套餐
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新每周套餐
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除每周套餐
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSWeeklyMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSWeeklyMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
