package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:号别工具类 </p>
 *
 * <p>Description:号别工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class PanelTypeTool extends TJDOTool{
    /**
     * 实例
     */
    public static PanelTypeTool instanceObject;
    /**
     * 得到实例
     * @return PanelTypeTool
     */
    public static PanelTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelTypeTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PanelTypeTool() {
        setModuleName("reg\\REGPanelTypeModule.x");
        onInit();
    }
    /**
     * 查询号别
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
     * 新增号别
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String admType = parm.getValue("ADM_TYPE");
        String clinicTypeCode = parm.getValue("CLINICTYPE_CODE");
        if(existsClinicType(admType,clinicTypeCode)){
            result.setErr(-1,"号别"+" 已经存在!");
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
     * 更新号别
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
     * 根据门急别,号别代码查询号别
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
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
     * 删除号别
     * @param admType String
     * @param clinicTypeCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在号别
     * @param admType String
     * @param clinicTypeCode String
     * @return boolean
     */
    public boolean existsClinicType(String admType,String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        return getResultInt(query("existsClinicType", parm), "COUNT") > 0;
    }
    /**
     * 根据号别CODE取得号别DESC
     * @param clinicTypeCode String
     * @return String
     */
    public String getDescByCode(String clinicTypeCode) {
      TParm result = new TParm();
      TParm parm = new TParm();
      parm.setData("CLINICTYPE_CODE",clinicTypeCode);
      result = query("getDescByCode", parm);
      String lresult = result.getValue("CLINICTYPE_DESC",0);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return lresult;
      }
      return lresult;
  }
  /**
   * 是否为专家诊
   * @param clinictypeCode String 号别代码
   * @return boolean true 专家 false 非专家
   */
  public boolean selProfFlg(String clinictypeCode) {
      TParm result = new TParm();
      TParm parm = new TParm();
      parm.setData("CLINICTYPE_CODE", clinictypeCode);
      result = query("selProfFlg", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrName() + " " + result.getErrText());
          return false;
      }
      return true;
  }

}
