package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * Description: 返洗率登记数据操作类
 * 
 * 
 * @author wangming	2013.8.1
 * @version 1.0
 */
public class INVBackwashingTool extends TJDOTool {

	/**
	 * 构造器
	 */
	private INVBackwashingTool() {
		setModuleName("inv\\INVBackwashingModule.x");
		onInit();
	}

	/**
	 * 实例对象
	 */
	private static INVBackwashingTool instanceObject;

	/**
	 * 获得实例
	 * 
	 * @return
	 */
	public static INVBackwashingTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INVBackwashingTool();
		}
		return instanceObject;
	}

	/**
	 * 保存
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertBSInfo(TParm parm) {
		 TParm result = this.update("insertBSInfo", parm);
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
	public TParm updateBSInfo(TParm parm) {
		 TParm result = this.update("updateBSInfo", parm);
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
	public TParm deleteBSInfo(TParm parm) {
		 TParm result = this.update("deleteBSInfo", parm);
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
	public TParm queryBSInfo(TParm parm) {
		TParm result = query("queryBSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 反洗率统计查询
	 * @param tparm
	 * @return
	 */
	public TParm queryBSCount(TParm parm) {
		TParm result = query("queryBSCount", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	} 



}
