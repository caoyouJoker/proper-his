package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;

public class INDDrugMianTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INDDrugMianTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockDTool
     */
    public static INDDrugMianTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new INDDrugMianTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INDDrugMianTool() {
        setModuleName("ind\\IND_CONSERVATION_MEDModule.x");
        onInit();
    }

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
    * onInsert
    *
    * @param parm TParm
    * @return TParm
    */
   public TParm onInsert(TParm parm) {
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

   public TParm onUpdate(TParm parm) {
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

   public TParm onDelete(TParm parm) {
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
