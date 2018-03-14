package jdo.bil;



import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: 病患信息维护
 * </p>
 * 
 * <p>
 * Description:病患信息维护
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
public class BILContractDTool extends TJDOTool{

	 public BILContractDTool() {
	        setModuleName("bil\\BILContractDModule.x");
	        onInit();
	    }
	    /**
	    * 实例
	    */
	   public static BILContractDTool instanceObject;

	    /**
	     * 得到实例
	     * @return IBSTool
	     */
	    public static BILContractDTool getInstance() {
	        if (instanceObject == null)
	            instanceObject = new BILContractDTool();
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
	      * 修改病患数据
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm updateDataPat(TParm parm, TConnection connection) {
	         TParm result = this.update("updateDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }


	     /**
	      * 插入病患信息
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm insertDataPat(TParm parm, TConnection connection) {
	         TParm result = this.update("insertDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	     /**
	      * 删除病患数据
	     * @param connection 
	      * @param ACIRecordNo String
	      * @return TParm
	      */
	     public TParm deleteDataPat(TParm parm, TConnection connection) {
	         TParm result = update("deleteDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
}
