package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * Description: 灭菌失败登记数据操作类
 * 
 * 
 * @author wangming	2013.8.1
 * @version 1.0
 */
public class INVReSterilizationTool extends TJDOTool {

	/**
	 * 构造器
	 */
	private INVReSterilizationTool() {
		setModuleName("inv\\INVReSterilizationModule.x");
		onInit();
	}

	/**
	 * 实例对象
	 */
	private static INVReSterilizationTool instanceObject;

	/**
	 * 获得实例
	 * 
	 * @return
	 */
	public static INVReSterilizationTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INVReSterilizationTool();
		}
		return instanceObject;
	}

	/**
	 * 保存
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertRSInfo(TParm parm) {
		 TParm result = this.update("insertRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateRSInfo(TParm parm) {
		 TParm result = this.update("updateRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 删除
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteRSInfo(TParm parm) {
		 TParm result = this.update("deleteRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 从inv_packstockm中查询手术包信息
	 * @param tparm
	 * @return
	 */
	public TParm queryPackMByBarcode(TParm parm) {
		TParm result = query("queryPackMByBarcode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 从inv_repack中查询手术包信息
	 * @param tparm
	 * @return
	 */
	public TParm queryPackRByBarcode(TParm parm) {
		TParm result = query("queryPackRByBarcode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 从inv_backwashing中查询反洗登记信息
	 * @param tparm
	 * @return
	 */
	public TParm queryRSInfo(TParm parm) {
		TParm result = query("queryRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 更新手术包状态为“灭菌”
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePackageStatus(TParm parm) {
		 TParm result = this.update("updatePackageStatus", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	
	/**
	 * 查询手术包灭菌月工作量
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm queryRSCount(TParm parm) {
		TParm result = query("queryRSCount", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}


}
