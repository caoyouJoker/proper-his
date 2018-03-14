package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:药品剂型 </p>
 *
 * <p>Description: 处理有关药品剂型的全部数据</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author JiaoY 2008-09-5
 * @version 1.0
 */

public class SYSPhaDoseTool extends TJDOTool{

    /**
   * 实例
   */
  public static SYSPhaDoseTool instanceObject;
  /**
   * 得到实例
   * @return PositionTool
   */
  public static SYSPhaDoseTool getInstance()
  {
      if(instanceObject == null)
          instanceObject = new SYSPhaDoseTool();
      return instanceObject;
  }
  /**
   * 构造器
   */
  public SYSPhaDoseTool()
  {
      setModuleName("sys\\SYSPhaDoseModule.x");
      onInit();
  }

  /**
   * 查询数据
   * @param doseCode String
   * @return TParm
   */

  public TParm selectdata(String doseCode, String dose_type){
      TParm parm = new TParm();

      if (doseCode != null && doseCode.length() > 0)
          parm.setData("DOSE_CODE", doseCode);
      if (dose_type != null && dose_type.length() > 0)
          parm.setData("DOSE_TYPE", dose_type);
      TParm result = query("selectall", parm);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
        return result;
    }

    public TParm existsData(TParm parm ){
//        System.out.println("验证begin");
        TParm result = query("existsPHA_DOSE",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
             return result;
         }
//         System.out.println("验证end--》"+result);
        return result;


    }
    /**
    * 新增一条数据
    * @param parm TParm
    * @return TParm
    */
   public TParm insertData(TParm parm){
//       System.out.println("新增begin");
       TParm result = new TParm();
       result = update("insertdata",parm);
       if (result.getErrCode() < 0) {
    err("ERR:" + result.getErrCode() + result.getErrText() +
        result.getErrName());
//    System.out.println("新增end");
     return result;
 }
 return result;

   }

   /**
   * 更新一条数据
   * @param parm TParm
   * @return TParm
   */
  public TParm updataData(TParm parm){
      TParm result = new TParm();
      result = update("updatedata", parm);
      if (result.getErrCode() < 0) {
   err("ERR:" + result.getErrCode() + result.getErrText() +
       result.getErrName());
    return result;
}

      return result;


  }

  /**
   * 删除一条数据
   * @param parm TParm
   * @return TParm
   */
  public TParm deleteData(TParm parm){
      TParm result = update("deletedata",parm);
      if (result.getErrCode() < 0) {
   err("ERR:" + result.getErrCode() + result.getErrText() +
       result.getErrName());
    return result;
}

      return result ;


  }








}
