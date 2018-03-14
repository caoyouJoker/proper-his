package jdo.adm;

import com.dongyang.jdo.*;
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
public class ADMMedQueryTool
    extends TJDOTool {
    /**
    * 实例
    */
   public static ADMMedQueryTool instanceObject;

   /**
    * 得到实例
    * @return SYSRegionTool
    */
   public static ADMMedQueryTool getInstance() {
       if (instanceObject == null)
           instanceObject = new ADMMedQueryTool();
       return instanceObject;
   }
    public ADMMedQueryTool() {
        setModuleName("adm\\ADMMedQueryModule.x");
        onInit();
    }
    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
