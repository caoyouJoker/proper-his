package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 取号原则窗口</p>
 *
 * <p>Description: 取号原则窗口工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class NoTool extends TJDOTool{
    /**
    * 实例
    */
   public static NoTool instanceObject;
   /**
    * 得到实例
    * @return PositionTool
    */
   public static NoTool getInstance()
   {
       if(instanceObject == null)
           instanceObject = new NoTool();
       return instanceObject;
   }
   /**
    * 构造器
    */
   public NoTool()
   {
       setModuleName("sys\\SYSNoModule.x");
       onInit();
   }

   /**
    * 查询
    */

   public TParm selectdata(TParm parm){

       TParm result = new TParm();
       result = query("selectdata", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }


       return result;

   }

   /**
    * 插入
    */

   public TParm insertdata(TParm parm){

       TParm result = new TParm();
       String regionCode = parm.getValue("REGION_CODE");
       String systemCode = parm.getValue("SYSTEM_CODE");
       String operation = parm.getValue("OPERATION");
       String subl_operation = parm.getValue("SUB1_OPERATION");

       //判断是否已存在该数据
       if(existsData(regionCode,systemCode,operation,subl_operation)){
           return result ;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result ;
   }

   /**
    * 更新
    *
    */
   public TParm updatedata(TParm parm) {
       TParm result = update("updatedata", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
    }

     /**
       * 删除
       */
    public TParm deletedata(TParm parm) {

          TParm result = update("deletedata", parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;
    }
    /**
     * 插入之前判断是否以存在该主键
     *
     */

   public boolean existsData(String regionCode, String systemCode, String operation, String subl_operation){
       TParm parm = new TParm();
       parm.setData("REGION_CODE",regionCode);
       parm.setData("SYSTEM_CODE",systemCode);
       parm.setData("OPERATION",operation);
       parm.setData("SUB1_OPERATION",subl_operation);
       return getResultInt(query("existdata",parm),"COUNT") > 0;
   }


}
