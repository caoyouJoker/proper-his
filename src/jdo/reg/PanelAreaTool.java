package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:诊区维护工具类 </p>
 *
 * <p>Description:诊区维护工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.25
 * @version 1.0
 */
public class PanelAreaTool extends TJDOTool{
    /**
     * 实例
     */
    public static PanelAreaTool instanceObject;
    /**
     * 得到实例
     * @return PanelAreaTool
     */
    public static PanelAreaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelAreaTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PanelAreaTool() {
        setModuleName("reg\\REGClinicAreaModule.x");
        onInit();
    }
    /**
      * 查询诊区
      * @return TParm
      */
     public TParm queryTree() {
         TParm result = query("queryTree");
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }
     /**
      * 新增诊区
      * @param parm TParm
      * @return TParm
      */
     public TParm insertdata(TParm parm) {
         TParm result = new TParm();
         String clinicAreaCode = parm.getValue("CLINICAREA_CODE");
         if(existsClinicArea(clinicAreaCode)){
             result.setErr(-1,"诊区"+" 已经存在!");
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
      * 更新诊区
      * @param parm TParm
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
      * 查询诊区
      * @param parm TParm
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
      * 删除诊区
      * @param clinicAreaCode String
      * @return boolean
      */
     public TParm deletedata(String clinicAreaCode) {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 判断是否存诊区
      * @param clinicAreaCode String 诊区代码
      * @return boolean TRUE 存在 FALSE 不存在
      */
     public boolean existsClinicArea(String clinicAreaCode) {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         return getResultInt(query("existsClinicArea", parm), "COUNT") > 0;
     }
     /**
      * 得到诊区
      * @param clinicAreaCode String
      * @return int
      */
     public int getclinicAreaCombo(String clinicAreaCode)
     {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         return getResultInt(query("getclinicAreaCombo",parm),"CLINICAREA_CODE");
     }
}
