package jdo.med;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class MedSmsDeptSetupTool  extends TJDOTool {
	public MedSmsDeptSetupTool() {
		setModuleName("med\\MEDSmsDeptSetupModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	public static MedSmsDeptSetupTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return SYSRegionTool
	 */
	public static MedSmsDeptSetupTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MedSmsDeptSetupTool();
		return instanceObject;
	}
	
	/**
	 * 根据条件查询
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 添加新数据
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 修改
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 删除
	 *
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm onDelete(TParm parm) {
		TParm result = this.update("deletedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}


}
