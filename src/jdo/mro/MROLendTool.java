package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 借阅原因字典</p>
 *
 * <p>Description: 借阅原因字典</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-6
 * @version 1.0
 */
public class MROLendTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROLendTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROLendTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROLendTool();
        return instanceObject;
    }

    public MROLendTool() {
        this.setModuleName("mro\\MROLendModule.x");
        this.onInit();
    }
    /**
     * 按条件查询信息  模糊查询
     * @param LEND_CODE String  借阅原因代码
     * @param LEND_DESC String  借阅原因说明
     * @return TParm
     */
    public TParm selectdata(String LEND_CODE,String LEND_DESC){
        TParm pm = new TParm();
        pm.setData("LEND_CODE",LEND_CODE+"%");
        pm.setData("LEND_DESC",LEND_DESC+"%");
        TParm result = query("selectdata",pm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新数据
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String code){
        TParm parm = new TParm();
        parm.setData("LEND_CODE",code);
        TParm result = update("deletedata",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
