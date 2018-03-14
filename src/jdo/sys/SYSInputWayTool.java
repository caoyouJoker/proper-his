package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class SYSInputWayTool extends TJDOTool {

	private static SYSInputWayTool mInstance;

	public static SYSInputWayTool getNewInstance() {
		if (mInstance == null) {
			mInstance = new SYSInputWayTool();
		}
		return mInstance;
	}

	public SYSInputWayTool() {
		this.setModuleName("sys\\SYSInputWayModule.x");
		onInit();
	}

	/**
	 * 查询录入途径
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectInputWay(TParm parm) {
		parm = this.query("selectInputWay", parm);
		return parm;
	}

	/**
	 * 删除录入途径
	 * 
	 * @return
	 */
	public TParm delectInputWay(TParm parm) {

		parm = this.update("delectInputWay", parm);

		return parm;
	}

	/**
	 * 新增录入途径
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveInputWay(TParm parm) {
		parm = this.update("saveInputWay", parm);
		return parm;

	}

	/**
	 * 更新录入途径
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInputWay(TParm parm) {

		parm = this.update("updateInputWay", parm);
		return parm;
	}

}
