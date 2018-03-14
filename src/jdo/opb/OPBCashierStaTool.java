package jdo.opb;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 门诊收费员工作量统计</p>
 *
 * <p>Description: 门诊收费员工作量统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-24
 * @version 1.0
 */
public class OPBCashierStaTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPBCashierStaTool instanceObject;
    /**
     * 得到实例
     * @return BILCounteTool
     */
    public static OPBCashierStaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBCashierStaTool();
        return instanceObject;
    }

    public OPBCashierStaTool() {
        setModuleName("opb\\OPBCashierStaModule.x");
        onInit();
    }
    /**
     * 查询收费数量和收费总金额
     * @param parm TParm
     * @return TParm
     */
    public TParm selectIn(TParm parm){
        TParm result = this.query("selectIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询退费数量和退费总金额
     * @return TParm
     */
    public TParm selectOut(TParm parm){
        TParm result = this.query("selectOut",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
    * 门诊收费员统计报表中汇总查找的数据中region的种类及个数
    * @param parm TParm
    * @return TParm
    * ==============pangben modify 20110415
    */
   public TParm selectRegionCode(TParm parm){
       TParm result = this.query("selectRegionCode",parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;


   }

}
