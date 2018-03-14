package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:病区药盒对照维护
 * </p>
 * 
 * <p>
 * Description:病区药盒对照维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-05-21
 * @version 1.0
 */
public class INDMedBoxTool extends TJDOTool {

	private static INDMedBoxTool instanceObject;

	/**
	 * 构造器
	 */
	public INDMedBoxTool() {
		setModuleName("spc\\INDMedBoxModule.x");
	}

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static INDMedBoxTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INDMedBoxTool();
		}
		return instanceObject;
	}

	/**
	 * 查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 增加
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInfo(TParm parm) {
		TParm result = new TParm();
		result = update("insertInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
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
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
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
	 * @param parm
	 * @return
	 */
	public TParm deleteInfo(TParm parm) {
		TParm result = new TParm();
		result = update("deleteInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
