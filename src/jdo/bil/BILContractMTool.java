package jdo.bil;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: 合同单位信息维护
 * </p>
 * 
 * <p>
 * Description: 合同单位信息维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractMTool extends TJDOTool{

	 public BILContractMTool() {
	        setModuleName("bil\\BILContractMModule.x");
	        onInit();
	    }
	    /**
	    * 实例
	    */
	   public static BILContractMTool instanceObject;

	    /**
	     * 得到实例
	     * @return IBSTool
	     */
	    public static BILContractMTool getInstance() {
	        if (instanceObject == null)
	            instanceObject = new BILContractMTool();
	        return instanceObject;
	    }
	    /**
	     * 显示数据
	     * @param parm TParm
	     */
	    public TParm selectData(String sqlName,TParm parm){
	        TParm result = query(sqlName, parm);
	        if (result.getErrCode() < 0) {
	            err(result.getErrCode() + " " + result.getErrText());
	            return result;
	        }
	        return result;
	    }
	    /**
	      * 删除合同单位数据
	     * @param connection 
	      * @param ACIRecordNo String
	      * @return TParm
	      */
	     public TParm deleteData(TParm parm, TConnection connection) {
	         TParm result = update("deleteData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	   

	     /**
	      * 判断合同单位数据是否存在
	      * @param parm TParm
	      * @return TParm
	      */
	     public TParm checkDataExist(TParm parm) {
	         TParm result = this.query("checkDataExist", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }

	     /**
	      * 修改合同单位数据
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm updateData(TParm parm, TConnection connection) {
	         TParm result = this.update("updateData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }
	  /**
	   * 更新合同单位预交金
	   * */
	     public TParm updPrePay(TParm parm,TConnection connection){
	    	 TParm result = this.update("updPrePay",parm);
	    	 if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }

	     /**
	      * 插入合同单位信息
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm insertData(TParm parm, TConnection connection) {
	         TParm result = this.update("insertData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	  
}
