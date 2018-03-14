package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class HRMPatadMTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static HRMPatadMTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return HRMCompanyTool
	 */
	public static HRMPatadMTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new HRMPatadMTool();
		}
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public HRMPatadMTool() {
		setModuleName("hrm\\HRMPatadMModule.x");
		onInit();
	}
	/**
	 * 删除操作
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteHrmPatadm(TParm parm, TConnection conn) {
		TParm result = update("deleteHrmPatadm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询HRM_PATADM表数据
	 * @param parm
	 * @return
	 */
	public TParm selectHrmPatadm(TParm parm) {
		TParm result = query("selectHrmPatadm", parm);
		return result;
	}
}
