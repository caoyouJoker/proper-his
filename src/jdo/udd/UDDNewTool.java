/**
 * @className INDNewTool.java 
 * @author litong
 * @Date 2013-3-23 
 * @version V 1.0 
 */
package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-23 
 */
public class UDDNewTool extends TJDOTool {

	/**
	 * 构造器
	 */
	  private UDDNewTool() {
	        //加载Module文件
	        this.setModuleName("UDD\\UDDAntbacterPatientedDetial.x");
	        onInit();
	    }
	  /**
		 * 实例
		 */
		private static UDDNewTool instanceObject;

	 /**
	  * 得到实例
	  * @return
	  */
	  public static UDDNewTool getInstance(){
	        if(instanceObject == null){
	            instanceObject = new UDDNewTool();
	        }
	        return instanceObject;
	}
	  
	  /**
		 * 查询使用抗菌药物出院病人的信息
		 */
		
	public TParm getPatientDetail(TParm parm) {
		TParm result = query("getPatientDetail",parm);
		if(result.getErrCode() < 0){
			err("ERR:M "+result.getErrCode() + result.getErrText()
			 + result.getErrName());
			return result;
		}
		return result;	
}
}










