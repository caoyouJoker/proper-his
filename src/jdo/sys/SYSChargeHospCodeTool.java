package jdo.sys;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
/**
 * <p>Title:费用代码管理 </p>
 *
 * <p>Description:费用代码管理 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSChargeHospCodeTool
    extends TJDOTool {

    /**
    * 实例
    */
   private static SYSChargeHospCodeTool instanceObject;
   /**
    * 得到实例
    * @return PatTool
    */
   public static SYSChargeHospCodeTool getInstance()
   {
       if(instanceObject == null)
           instanceObject = new SYSChargeHospCodeTool();
       return instanceObject;
   }
   /**
    * 构造器
    */
   public SYSChargeHospCodeTool()
   {
       setModuleName("sys\\SYSChargeHospCodeModule.x");
       onInit();
   }
   /**
       * 初始化，查询全数据
       * @return TParm
       */
      public TParm selectalldata() {
          TParm result = query("selectalldata");
          if(result.getErrCode() < 0) {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;

   }
   /**
    * 根据条件查询数据
    * @return TParm
    */
   public TParm selectalldata(TParm parm) {
      TParm result = query("selectdata",parm);
      if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;

   }
   /**
    * 插入数据库
    * @return TParm
    */
public TParm savedata(TParm parm){
    TParm result = update("insertdata",parm);
     if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

   }
   /**
    * 更新数据库
    * @return TParm
    */
   public TParm updata(TParm parm) {
       TParm result = update("updatedata",parm);
       if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;


   }
   /**
       * 删除数据
       * @return TParm
    */
public TParm delete(TParm parm,TConnection connection){
       TParm result = update("deletedata",parm,connection);
       if(result.getErrCode() < 0)
          {
              err(result.getErrCode() + " " + result.getErrText());
              return result;
          }
          return result;

   }
   /**
       * 根据CHARGE_HOSP_CODE查询各种收费项目
       * @return TParm：CHARGE_CODE,MRO_CHARGE_CODE,STA_CHARGE_CODE
       */
      public TParm selectChargeCode(TParm parm) {
         TParm result = query("selectChargeCode",parm);
         if(result.getErrCode() < 0){
              err(result.getErrCode() + " " + result.getErrText());
              return result;
          }
          return result;

   }

}
