package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:护理等级 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMNursingClassTool extends TJDOTool {

    /**
   * 实例
   */
  public static ADMNursingClassTool instanceObject;

  /**
   * 得到实例
   * @return RegMethodTool
   */
  public static ADMNursingClassTool getInstance()
  {
      if(instanceObject == null)
          instanceObject = new ADMNursingClassTool();
      return instanceObject;
  }

  /**
   * 构造器
   */
  public ADMNursingClassTool()
  {
      setModuleName("adm\\ADMNursingClassModule.x");
      onInit();
  }

  /**
   * 新增挂号方式
   * @param regMethod String
   * @return TParm
   */
  public TParm insertdata(TParm parm) {
      TParm result = new TParm();
      String regMethod = parm.getValue("REGMETHOD_CODE");
      if(existsRegMethod(regMethod)){
          result.setErr(-1,"挂号方式 "+" 已经存在!");
          return result ;
      }
      result = update("insertdata", parm);
      // 判断错误值
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * 更新挂号方式
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
   * 根据挂号方式代码查询方式信息(右忽略)
   * @param regMethod String 挂号方式代码
   * @return TParm
   */
  public TParm selectdata(String regMethod){
      TParm parm = new TParm();
      regMethod += "%";
      parm.setData("REGMETHOD_CODE",regMethod);
      TParm result = query("selectdata",parm);
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
   * 删除挂号方式
   * @param regMethod String
   * @return boolean
   */
  public TParm deletedata(String regMethod){
      TParm parm = new TParm();
      parm.setData("REGMETHOD_CODE",regMethod);
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

  /**
   * 判断是否存在挂号方式
   * @param regMethod String 挂号方式代码
   * @return boolean TRUE 存在 FALSE 不存在
   */
  public boolean existsRegMethod(String regMethod){
      TParm parm = new TParm();
      parm.setData("REGMETHOD_CODE",regMethod);
      return getResultInt(query("existsRegMethod",parm),"COUNT") > 0;
  }

}
