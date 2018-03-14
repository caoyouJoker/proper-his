package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 查核类别字典</p>
 *
 * <p>Description: 查核类别字典</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110429
 * @version 1.0
 */
public class ClpchkTypeTool extends TJDOTool {

    /**
     * 实例
     */
    private static ClpchkTypeTool instanceObject;
    /**
     * 初始化类对象
     * @return ClpchkTypeTool
     */
    public static ClpchkTypeTool getInstance() {
        if (null == instanceObject)
            instanceObject = new ClpchkTypeTool();
        return instanceObject;
    }
    /**
    * 构造方法
    */
   public ClpchkTypeTool() {
       this.setModuleName("clp\\ClpchkTypeModule.x");
       onInit();
   }
   /**
    * 添加数据方法
    * @param parm TParm
    * @return TParm
    */
   public TParm saveClpchkType(TParm parm) {
       TParm result = this.update("insertClpchkType", parm);
       return boolTParmNUll(result);
   }
   /**
    * 修改数据方法
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpchkType(TParm parm){
       TParm result = this.update("updateClpchkType", parm);
       return boolTParmNUll(result);
   }
   /**
    * 删除数据方法
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteClpchkType(TParm parm){
       TParm result = this.update("deleteClpchkType", parm);
       return boolTParmNUll(result);
   }
   /**
    * 查询此用户是否存在
    * 此表需要三个主键判断
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
   /**
    *
    * @param parm TParm
    * @return TParm
    */
   public TParm selectAllCheckCode(TParm parm) {
       TParm result = this.query("selectAll", parm);
       return boolTParmNUll(result);
   }

}
