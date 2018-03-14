package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>Title:身份明细 </p>
 *
 * <p>Description:身份明细 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class StatusDetailsTool
    extends TJDOTool {
    /**
   * 实例
   */
  private static StatusDetailsTool instanceObject;
  /**
   * 得到实例
   * @return PatTool
   */
  public static StatusDetailsTool getInstance()
  {
      if(instanceObject == null)
          instanceObject = new StatusDetailsTool();
      return instanceObject;
  }
  /**
   * 构造器
   */
  public StatusDetailsTool()
  {
      setModuleName("sys\\SYSStatusDetailsModule.x");
      onInit();
  }
  /**
      * 初始化，查询全数据
      * @return TParm
      */
     public TParm selectalldata() {
         return query("selectalldata");
  }
  /**
     * 得到组列表
     * @param group String 组名
     * @return TParm 回值<ID>,<NAME>,<TYPE>
     */
    public TParm getGroupList(String group)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",group);
        return query("getGroupList",parm);
    }

  /**
   * 根据条件查询数据
   * @return TParm
   */
  public TParm selectalldata(TParm parm) {
      return query("selectdata",parm);
  }
  /**
   * 插入数据库
   * @return TParm
   */
public TParm insertData(TParm parm){
   TParm result= update("insertData",parm);
    if(result.getErrCode() < 0)
            {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
            return result;

  }
  /**
   * 更新数据库
   * @return TParm
   */
  public TParm updateData(TParm parm) {
      TParm result = update("updateData",parm);
      if(result.getErrCode() < 0)
             {
                 err(result.getErrCode() + " " + result.getErrText());
                 return result;
             }
             return result;

  }
  /**
      * 删除数据
      * @return TParm
   */
  public TParm delete(TParm parm,TConnection connection){
         TParm result = update("deleteData",parm,connection);
         if(result.getErrCode() < 0)
            {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
            return result;

   }
   /**
    * 得到三身份的折扣率
    * @param ctz1 String
    * @param ctz2 String
    * @param ctz3 String
    * @param chargeHospCode String
    * @return double
    */
	public double getOwnRate(String ctz1, String ctz2, String ctz3,
			String chargeHospCode, String orderCode) {
		// zhangp
		double ownRate1 = getOwnRateByOrder(ctz1, orderCode, chargeHospCode);
		if (ownRate1 < 0) {
			return getOwnRate(ctz1, chargeHospCode)
					* getOwnRate(ctz2, chargeHospCode)
					* getOwnRate(ctz3, chargeHospCode);
		}
		double ownRate2 = getOwnRateByOrder(ctz2, orderCode, chargeHospCode);
		if (ownRate2 < 0) {
			return getOwnRate(ctz1, chargeHospCode)
					* getOwnRate(ctz2, chargeHospCode)
					* getOwnRate(ctz3, chargeHospCode);
		}
		double ownRate3 = getOwnRateByOrder(ctz3, orderCode, chargeHospCode);
		if (ownRate3 < 0) {
			return getOwnRate(ctz1, chargeHospCode)
					* getOwnRate(ctz2, chargeHospCode)
					* getOwnRate(ctz3, chargeHospCode);
		}
		double ownRateByOrderCode = ownRate1 * ownRate2 * ownRate3;
//		if (ownRateByOrderCode != 1) {
			return ownRateByOrderCode;
//		}
//		return getOwnRate(ctz1, chargeHospCode)
//				* getOwnRate(ctz2, chargeHospCode)
//				* getOwnRate(ctz3, chargeHospCode);
	}
/**
 * 得到身份对应的折扣率
 * @param ctz String
 * @param chargeHospCode String
 * @return double
 */
public double getOwnRate(String ctz,String chargeHospCode){
    if(ctz==null || ctz.trim().length()==0)
        return 1;
    TParm parm =new TParm();
    parm.setData("CTZ_CODE", ctz);
    parm.setData("CHARGE_HOSP_CODE", chargeHospCode);
    TParm result = query("selectOwnRate", parm);
    if (result.getErrCode() < 0) {
        err(result.getErrCode() + " " + result.getErrText());
        return -1;
    }
    return this.getResultDouble(result, "DISCOUNT_RATE");

}

	/**
	 * 通过orderCode和ctz判断折扣
	 * =====zhangp
	 * @param ctz
	 * @param orderCode
	 * @return
	 */
	private double getOwnRateByOrder(String ctz, String orderCode, String chargeHospCode){
		String sql =
			" SELECT A.CTZ_CODE, A.ORDER_CODE, A.DISCOUNT_RATE, B.ORDERSET_FLG" +
			" FROM SYS_CTZ_ORDER_DETAIL A, SYS_FEE B" +
			" WHERE A.CTZ_CODE = '" + ctz + "' AND A.ORDER_CODE = '" + orderCode + "'" +
			" AND A.ORDER_CODE = B.ORDER_CODE" +
			" AND B.ACTIVE_FLG = 'Y'";
		TParm ownRateParm = new TParm(TJDODBTool.getInstance().select(sql));
	    if (ownRateParm.getErrCode() < 0) {
	        err(ownRateParm.getErrCode() + " " + ownRateParm.getErrText());
	        return -1;
	    }
		if(ownRateParm.getCount()<0){
			return getOwnRate(ctz, chargeHospCode);
		}
		if(ownRateParm.getBoolean("ORDERSET_FLG", 0)){
			
		}
		return this.getResultDouble(ownRateParm, "DISCOUNT_RATE");
	}
	
	
}
