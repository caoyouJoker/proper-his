package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsPaydTool extends TJDOTool {
    /**
    * 实例
    */
   public static InsPaydTool instanceObject;
   /**
    * 得到实例
    * @return RuleTool
    */
   public static InsPaydTool getInstance() {
       if (instanceObject == null)
           instanceObject = new InsPaydTool();
       return instanceObject;
   }
   /**
    * 构造器
    */
   public InsPaydTool() {
       setModuleName("ins\\INSPAYD.x");
       onInit();
   }
   /**
    * 查询申报结算明细
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsPayd(TParm parm){
       TParm result = new TParm();
       result = query("queryInsPayd", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询统筹和住院费用
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsOrderPrice(TParm parm){
       TParm result = new TParm();
       result = query("queryInsOrderPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询费用分割档相关数据
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsOrderList(TParm parm){
       TParm result = new TParm();
          result = query("queryInsOrderList", parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;
   }
   /**
    * 查询支付标准比例
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsRuel(TParm parm){
       TParm result = new TParm();
       result = query("queryInsRuel", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询最高限价支付比例
    * @param parm TParm
    * @return TParm
    */
   public TParm queryLimit(TParm parm){
       TParm result = new TParm();
       result = query("queryLimit", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询累计支付
    * @param parm TParm
    * @return TParm
    */
   public TParm queryAddpay(TParm parm){
       TParm result = new TParm();
       result = query("queryAddpay", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 全为N的情况
    * @param parm TParm
    * @return TParm
    */
   public TParm queryAllNo(TParm parm){
       TParm result = new TParm();
       result = query("queryAllNo", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 按比例全额支付
    * @param parm TParm
    * @return TParm
    */
   public TParm qeuryAllPrice(TParm parm){
       TParm result = new TParm();
       result = query("qeuryAllPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 插入结算表INS_PAYD
    * @param parm TParm
    * @return TParm
    */
   public TParm insertInsPayDList(TParm parm,TConnection conn){
       TParm result = new TParm();
        result = update("insertInsPayDList",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
   }
   /**
    * 删除结算数据
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm deleteInsPaydData(TParm parm,TConnection conn){
       TParm result = new TParm();
       result = update("deleteInsPaydData",parm,conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询起付线金额
    * @param parm TParm
    * @return TParm
    */
   public TParm queryStartLinePrice(TParm parm){
       TParm result = new TParm();
       result = query("queryStartLinePrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询统筹
    * @param parm TParm
    * @return TParm
    */
   public TParm queryPlanPrice(TParm parm){
       TParm result = new TParm();
      result = query("queryPlanPrice", parm);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
   }
   /**
    * 全为负数的情况支付标准
    * @param parm TParm
    * @return TParm
    */
   public TParm queryFSRule(TParm parm){
       TParm result = new TParm();
       result = query("queryFSRule", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 汇入
    * @return TParm
    */
   public TParm updateInsPaymData(TParm parm){
       TParm result = new TParm();
       result = update("updateInsPaymData",parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询打印数据
    * @param parm TParm
    * @return TParm
    */
   public TParm queryPrintData(TParm parm){
       TParm result = new TParm();
        result = query("queryPrintData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
   }
   /**
    * 计算总金额
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsPaydAllPrice(TParm parm){
       TParm result = new TParm();
       result = query("queryInsPaydAllPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 查询自付比例
    * @param parm TParm
    * @return TParm
    */
   public TParm queryRate(TParm parm){
       TParm result = new TParm();
       result = query("queryRate", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 得到费用类别
    * @param feeTypeCode String
    * @return String
    */
   public String getFeeType(String feeTypeCode){
       TParm result = new TParm();
       TParm action = new TParm();
       action.setData("FEE_TYPE",feeTypeCode);
       result = query("getFeeType",action);
       if(result.getErrCode()<0){
           err("ERR"+ result.getErrCode() + result.getErrText() +
               result.getErrName());
           return "";
       }
       return result.getData("FEE_TYPE_DESC",0).toString();
   }
   /**
    * 得到报表类别
    * @param parm TParm
    * @return TParm
    */
   public TParm getRPTType(TParm parm){
       TParm result = query("getRPTType",parm);
       if(result.getErrCode()<0){
           err("ERR"+ result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
}
