package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class InvoiceTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvoiceTool instanceObject;
    /**
     * 得到实例
     * @return InvoiceTool
     */
    public static InvoiceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvoiceTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public InvoiceTool() {
        setModuleName("bil\\BILInvoiceModule.x");
        onInit();
    }
    /**
     * 查询全部数据
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
   /**
    * 领票插入新数据数据
    * @return TParm
    */
   public TParm insertData(TParm parm) {
       TParm result = update("insertData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * 开账更新数据数据
    * @return TParm
    */
   public TParm updataData(TParm parm,TConnection connection) {
       TParm result = update("updataData",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
      * 更新数据数据
      * @return TParm
      */
     public TParm updataData(TParm parm) {
         TParm result = update("updataData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }
   /**
      * 交回更新数据数据
      * @return TParm
      */
     public TParm updatainData(TParm parm) {
         TParm result = update("updatainData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }
   /**
    * 检核票号是否被使用过
    * @param parm TParm
    * @return TParm
    */
   public TParm checkData(TParm parm){
    TParm result = update("checkData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;

}
     /**
    * 打印票据更新当前票号
    * @return TParm
    */
   public TParm updateDatePrint(TParm parm,TConnection connection) {
       TParm result = update("updateDatePrint",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * 得到当前票号和起始票号
    * @param parm TParm
    * @return String[]
    */
   public String[] getUpdateUpdateNo(TParm parm){
    TParm result = query("selectUpdateNo",parm);
    String[] error=new String[]{"-1","-1"};
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return error;
       }
       String[] returndata=new String[]{result.getValue("UPDATE_NO",0),result.getValue("START_INVNO",0)};
       return returndata;

}

   /**
    * 得到当前使用的票据
    * @param parm TParm
    * @return String[]
    */
   public TParm selectNowReceipt(TParm parm) {
       TParm result = query("selectNowReceipt", parm);
       if (result.getErrCode() < 0) {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }

}
