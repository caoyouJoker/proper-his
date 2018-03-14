package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 临床路径费用分析 </p>
 *
 * <p>Description: 临床路径费用分析 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPBillTool extends TJDOTool{
    private static CLPBillTool instanceObject;
     public CLPBillTool() {
         setModuleName("clp\\CLPBillModule.x");
         onInit();
     }
     public static CLPBillTool getInstance() {
         if (instanceObject == null) {
             instanceObject = new CLPBillTool();
         }
         return instanceObject;
     }

     /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm selectPatientData(TParm parm) {
         TParm result = new TParm();
         result = query("selectPatientData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm selectBillData(TParm parm) {
         TParm result = new TParm();
         result = query("selectBillData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm getChargeHeader(TParm parm) {
         TParm result = new TParm();
         result = query("selectChargeHeader", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm getaverageCharge(TParm parm) {
         TParm result = new TParm();
         result = query("getaverageCharge", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }


     /**
      * 查找实际的治疗天数
      * @param parm TParm
      * @return TParm
      */
     public TParm selectDurationSchdDay(TParm parm ){
         TParm result = new TParm();
         result = query("selectDurationSchdDay", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }




}
