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
public class InsOrderTool extends TJDOTool {
    /**
     * 实例
     */
    public static InsOrderTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static InsOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsOrderTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public InsOrderTool() {
        setModuleName("ins\\INSORDER.x");
        onInit();
    }
    /**
     * 查询费用分割档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderTable(TParm parm) {
        TParm result = new TParm();
        result = query("queryOrderTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询合并的分割数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryGroupData(TParm parm){
        TParm result = new TParm();
        result = query("queryGroupData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询用那个视图
     * @param parm TParm
     * @return TParm
     */
    public TParm queryView(TParm parm){
        TParm result = new TParm();
        result = query("queryView", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医保明细帐
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataXA(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataXA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医保明细帐
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataSX(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataSX", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医保明细帐
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataZG(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataZG", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询医保明细帐
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataGX(TParm parm) {
        TParm result = new TParm();
        result = query("queryOrderMDataGX", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除费用分割档数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInsOrderData(TParm parm,TConnection conn){
        TParm result = new TParm();
        //System.out.println("删除费用分割档数据"+parm);
        result = this.update("deleteInsOrderData",parm,conn);
        return result;
    }
    /**
     * 查询序号
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInsOrderSEQNO(TParm parm){
        TParm result = new TParm();
        result = this.query("selectInsOrderSEQNO",parm);
       // System.out.println("查询序号:"+result);
        return result;
    }
    /**
     * 保存医保费用分割档数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveInsOrder(TParm parm,TConnection conn){
        TParm result = new TParm();
        //System.out.println("保存医保费用分割档数据"+parm);
        result = this.update("saveInsOrder",parm,conn);
        return result;
    }
    /**
     * 查询预交金余额
     * @param parm TParm
     * @return TParm
     */
    public TParm queryAdmInpTOTAL_DEPOSIT(TParm parm){
        TParm result = new TParm();
       // System.out.println("查询预交金余额" + parm);
        result = this.query("queryAdmInpTOTAL_DEPOSIT",parm);
        return result;
    }
    /**
    * 审核通过
    * @param parm TParm
    * @return TParm
    */
   public TParm onSaveInfo(TParm parm){
       TParm result = new TParm();
       result = this.update("queryInsPaymInsStatus",parm);
       return result;
   }
   /**
   * 结算通过
   * @param parm TParm
   * @return TParm
   */
  public TParm onSaveInfoJS(TParm parm){
      TParm result = new TParm();
      result = this.update("queryInsPaymInsStatusJS",parm);
      return result;
  }
  /**
   * 结算通过
   * @param parm TParm
   * @return TParm
   */
  public TParm onSaveInfoJS(TParm parm,TConnection conn){
      TParm result = new TParm();
      result = this.update("queryInsPaymInsStatusJS",parm,conn);
      return result;
  }

   /**
    * 查询打印数据
    * @return TParm
    */
   public TParm queryPrintData(TParm parm){
       TParm result = new TParm();
       result = this.query("queryPrintData",parm);
       return result;
   }
   /**
    * 查询结算进度
    * @param parm TParm
    * @return TParm
    */
   public TParm checkInsStatus(TParm parm){
       TParm result = new TParm();
       result = this.query("checkInsStatus",parm);
       return result;
   }
   /**
    * 查询是否有出院账单
    * @param parm TParm
    * @return TParm
    */
   public TParm checkIbsStatus(TParm parm){
       TParm result = new TParm();
       result = this.query("checkIbsStatus",parm);
       return result;
   }

}
