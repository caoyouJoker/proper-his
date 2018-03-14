package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:给号方式工具类 </p>
 *
 * <p>Description:给号方式工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class QueMethodTool extends TJDOTool{
    /**
     * 实例
     */
    public static QueMethodTool instanceObject;
    /**
     * 得到实例
     * @return PanelGroupTool
     */
    public static QueMethodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new QueMethodTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public QueMethodTool() {
        setModuleName("reg\\REGQueMethodModule.x");
        onInit();
    }
    /**
      * 查询给号方式
      * @return TParm
      */
     public TParm queryTree(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         TParm result = query("queryTree", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;

     }

     /**
      * 新增给号方式
      * @param  String
      * @return TParm
      */
     public TParm insertdata(TParm parm) {
         TParm result = new TParm();
         String quegroupCode = parm.getValue("QUEGROUP_CODE");
         String queNo = parm.getValue("QUE_NO");
         if(existsQueMethod(quegroupCode,queNo)){
             result.setErr(-1,"给号方式"+" 已经存在!");
             return result ;
         }

         result = update("insertdata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 更新给号方式
      * @param  String
      * @return TParm
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
      * 查询给号方式
      * @parm
      * @return TParm
      */
     public TParm selectdata(TParm parm) {
         TParm result = new TParm();
         result = query("selectdata",parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 删除给号方式
      * @param quegroupCode,queNo String
      * @return boolean
      */
     public TParm deletedata(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 判断是否存在给号方式
      * @param quegroupCode,queNo String 给号方式
      * @return boolean TRUE 存在 FALSE 不存在
      */
     public boolean existsQueMethod(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         return getResultInt(query("existsQueMethod", parm), "COUNT") > 0;
     }
}
