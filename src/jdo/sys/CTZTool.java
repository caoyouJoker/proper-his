package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class CTZTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static CTZTool instanceObject;
    /**
     * 得到实例
     * @return CTZTool
     */
    public static CTZTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CTZTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public CTZTool() {
        setModuleName("sys\\SYSCTZModule.x");
        onInit();
    }
    /**
     * 查询全部数据
     * @return TParm CTZ_CODE;CTZ_DESC
     */
    public TParm selecTtreeData() {
        TParm result = query("selectTreeData");
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
    * 查询全部数据
    * @return TParm CTZ_CODE;CTZ_DESC
    */
   public TParm selectData(TParm parm) {
       TParm result = query("selectData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * 插入新数据数据
    * @return TParm CTZ_CODE;CTZ_DESC
    */
   public TParm insertData(TParm parm) {
       TParm result = update("insertData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * 更新数据数据
    * @return TParm CTZ_CODE;CTZ_DESC
    */
   public TParm updataData(TParm parm) {
       TParm result = update("updataData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
      * 删除数据数据
      * @return TParm CTZ_CODE
      */
     public TParm deleteData(TParm parm,TConnection connection) {
         TParm result = update("deleteData",parm,connection);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }
   /**
    * 根据主身份查询医保单位
    * @param parm TParm
    * @return TParm(ctz_code)
    */
   public TParm selCompanyCodeByCtz(TParm parm){
       TParm result = query("",parm);
       if(result.getErrCode()<0){
       err(result.getErrCode() + " " +result.getErrText());
       return result;
       }
       return result;

   }
   /**
    * 根据身份代码得到医保身份标记
    * @param ctzCode String
    * @return TParm
    */
   public TParm getFlgByCtz(String ctzCode) {
       TParm data = new TParm();
       data.setData("CTZ_CODE", ctzCode);
       TParm result = query("getFlgByCtz", data);
       return result;
   }
   	/**
   	 * 获得医保身份代码
	 * pangb 2012-2-10
   	 * @param ctzCode
   	 * @return
   	 */
   public TParm getNhiNoCtz(String ctzCode) {
	   TParm data = new TParm();
       data.setData("CTZ_CODE", ctzCode);
       TParm result = query("getNhiNoCtz", data);
       return result;
   }
   
   /**
    * 根据身份代码得到病案首页身份
    * @param ctzCode String
    * @return TParm
    */
  public TParm getMroCtz(String ctzCode) {
      TParm data = new TParm();
      data.setData("CTZ_CODE", ctzCode);
      TParm result = query("getMroCtz", data);
      return result;
  }
}
