package jdo.dss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;


/**
 * <p>Title: 指标等级设定工具类</p>
 *
 * <p>Description: 指标等级设定工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: 指标等级设定</p>
 *
 * @author sundx
 * @version 1.0
 */

public class DSSKPISQLTool extends TJDOTool{

    /**
     * 实例
     */
    public static DSSKPISQLTool instanceObject;

    /**
     * 得到实例
     * @return DSSSQLTool
     */
    public static DSSKPISQLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DSSKPISQLTool();
        return instanceObject;
    }

    /**
     * 构造函数
     */
    public DSSKPISQLTool() {
        setModuleName("dss\\DSSKPICodeModule.x");
        onInit();
    }

    /**
     * 查询指标等级信息
     * @return TParm
     */
    public TParm queryDSSKPI() {
        TParm parm = new TParm();
        TParm result = query("queryDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

     /**
      * 通过等级信息编码查询指标等级信息
      * @return TParm
      */
     public TParm queryDSSKPIByCode(String KPICode) {
         TParm parm = new TParm();
         parm.setData("KPI_CODE",KPICode);
         TParm result = query("queryDSSKPIByCode", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }


     /**
      * 通过父等级编码查询指标等级信息
      * @return TParm
      */
     public TParm queryDSSKPIByParentCode(String parentCode) {
         TParm parm = new TParm();
         parm.setData("PARENT_CODE",parentCode);
         TParm result = query("queryDSSKPIByParentCode", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }

     /**
      * 更新DSSKPI相关信息
      * @param parm TParm
      * @return TParm
      */
     public TParm updateDSSKPIByCode(TParm parm){
          TParm result = update("updateDSSKPIByCode", parm);
          if (result.getErrCode() < 0) {
              err(result.getErrCode() + " " + result.getErrText());
              return null;
          }
          return result;
      }

    /**
     * 写入DSSKPI
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDSSKPI(TParm parm){
        TParm result = update("insertDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 删除DSSKPI
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteDSSKPI(String KPICode){
        TParm parm = new TParm();
        parm.setData("KPI_CODE",KPICode);
        TParm result = update("deleteDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 删除节点及其子节点
     * @param KPICode String
     * @return TParm
     */
    public TParm deleteKPIAndChlidren(String KPICode){
        TParm parm = queryDSSKPIByParentCode(KPICode);
        if(parm == null)
            return null;
        TParm result = new TParm();
        for(int i = 0;i < parm.getCount("KPI_CODE");i++){
            result = deleteKPIAndChlidren(parm.getValue("KPI_CODE",i));
        }
        result = deleteDSSKPI(KPICode);
        return result;
    }

    /**
     * 得到最大序号
     * @return TParm
     */
    public String getMaxSeq() {
        TParm parm = new TParm();
        TParm result = query("getMaxSeq", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        if (result.getCount("SEQ") <= 0)
            return "1";
        return "" + (Integer.parseInt(result.getValue("SEQ",0)) + 1);
     }
  }
