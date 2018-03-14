package jdo.ctr;

import com.dongyang.data.*;
import com.dongyang.jdo.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author
 * @version 1.0
 */
public class CtrQueryTool
    extends TJDOTool {

//使这个对象是单例的,只能初始化一个对象
    private static CtrQueryTool instance = null;
    private CtrQueryTool() {

        //加载Module文件,文件格式在下面说明
        this.setModuleName("ctr\\CtrQueryModule.x");
        onInit();
    }

    /**
     *实例化
     * @return
     */
    public static CtrQueryTool getNewInstance() {
        if (instance == null) {
            instance = new CtrQueryTool();
        }
        return instance;
    }


    /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
     }

     /**
     * 新增
     * @param parm TParm
     * @return TParm
     */
     public TParm onTableInsert(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.update("insert", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }
     /**
      * 更新
      * @param parm TParm
      * @return TParm
      */
    public TParm onTableUpdate(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
      * 删除
      * @param parm TParm
      * @return TParm
      */
    public TParm onTableDelete(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
