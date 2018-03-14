package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 变异字典</p>
 *
 * <p>Description: 变异字典</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2011-05-02
 * @version 1.0
 */
public class ClpVarMoncatTool extends TJDOTool{

    /**
     * 实例
     */
    private static ClpVarMoncatTool instanceObject;
    /**
     * 初始化类对象
     * @return ClpVarMoncatTool
     */
    public static ClpVarMoncatTool getInstance() {
        if (null == instanceObject)
            instanceObject = new ClpVarMoncatTool();
        return instanceObject;
    }

    /**
     * 构造方法
     */
    public ClpVarMoncatTool() {
        this.setModuleName("clp\\ClpVarMoncatModule.x");
        onInit();
    }
    /**
    * 查询方法
    * 第一个表格中的数据
    * @param parm TParm
    * @return TParm
    */
   public TParm getQueryClpVarMoncat(TParm parm) {
       TParm result = this.query("queryClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
   * 查询方法
   * 第二个表格中的数据
   * @param parm TParm
   * @return TParm
   */
  public TParm getQueryClpVariance(TParm parm) {
      TParm result = this.query("queryClpVariance", parm);
      return boolTParmNUll(result);
  }

   /**
    * 添加数据方法
    * ClpVarMoncat表添加数据主档表格
    * @param parm TParm
    * @return TParm
    */
   public TParm saveClpVarMoncat(TParm parm) {
       TParm result = this.update("insertClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
   * 添加数据方法
   * ClpVariance表添加数据
   * @param parm TParm
   * @return TParm
   */
  public TParm saveClpVariance(TParm parm,TConnection conn) {

      TParm result = this.update("insertClpVariance", parm,conn);
      return boolTParmNUll(result);
  }

   /**
    * 修改数据方法
    * ClpVarMoncat表修改数据：主档表
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpVarMoncat(TParm parm){
       TParm result = this.update("updateClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
    * 修改数据方法
    * ClpVariance表修改数据：子表
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpVariance(TParm parm,TConnection conn){
       TParm result = this.update("updateClpVariance", parm,conn);
       return boolTParmNUll(result);
   }

   /**
    * 删除数据方法
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteClpVarMoncat(TParm parm){
       TParm result = this.update("deleteClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
    * 查询此用户是否存在
    * 此表需要两个主键判断
    * @return TParm
    */
   public TParm selectIsExist(TParm parm) {
       TParm result = this.query("selectIsExist", parm);
       return boolTParmNUll(result);
   }
   /**
    * 数据库操作以后判断是否出现错误方法
    * @param result TParm
    * @return TParm
    */
   public TParm boolTParmNUll(TParm result) {
       //等于0是不报错
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return null;
       }
       return result;
   }

}
