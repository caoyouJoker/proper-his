package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 住院诊断Tool</p>
 *
 * <p>Description: 住院诊断Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-9-9
 * @version 4.0
 */
public class ADMDiagTool
    extends TJDOTool {
    /**
    * 实例
    */
   public static ADMDiagTool instanceObject;

   /**
    * 得到实例
    * @return SYSRegionTool
    */
   public static ADMDiagTool getInstance() {
       if (instanceObject == null)
           instanceObject = new ADMDiagTool();
       return instanceObject;
   }

    public ADMDiagTool() {
        setModuleName("adm\\ADMDiagModule.x");
        onInit();
    }
    /**
     * 根据CASE_NO查询某一病患的所有诊断信息（病案首页使用）
     * @param parm TParm 必须参数：CASE_NO
     * @return TParm
     */
    public TParm queryDiagForMro(TParm parm){
        TParm result = this.query("queryDiagForMro", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询诊断全字段
     * @param parm TParm
     * @return TParm
     */
    public TParm queryData(TParm parm){
        TParm result = this.query("queryData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入诊断信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDiag(TParm parm,TConnection conn){
        TParm result = this.update("insertDiag",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
