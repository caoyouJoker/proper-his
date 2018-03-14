package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:  科室溢出人员管理 </p>
 *
 * <p>Description:  科室溢出人员管理 </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110708
 * @version 1.0
 */
public class CLPOverPersonManagerTool extends TJDOTool{
    public CLPOverPersonManagerTool() {
         setModuleName("clp\\CLPOverPersonManagerModule.x");
         onInit();
     }
     /**
     * 实例
     */
    public static CLPOverPersonManagerTool instanceObject;

     /**
      * 得到实例
      * @return IBSTool
      */
     public static CLPOverPersonManagerTool getInstance() {
         if (instanceObject == null)
             instanceObject = new CLPOverPersonManagerTool();
         return instanceObject;
     }
     /**
      * 显示数据
      * @param parm TParm
      */
     public TParm selectData(String sqlName,TParm parm){
         TParm result = query(sqlName, parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;

     }

}
