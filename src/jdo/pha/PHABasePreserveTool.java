package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 中包装转换维护工具类
 * </p>
 * 
 * <p>
 * Description: 中包装转换维护工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.4.29
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author shendongri
 * @version 1.0
 */
public class PHABasePreserveTool extends TJDOTool {

	/**
	 * 实例
	 */
	private static PHABasePreserveTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PhaBaseTool
	 */
	public static PHABasePreserveTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PHABasePreserveTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public PHABasePreserveTool() {
		setModuleName("pha\\PHABasePreserveControlModule.x");
		onInit();
	}

	/**
	 * 根据药品代码查询单位名称
	 * @param parm
	 * @return
	 */
	public TParm queryUnitByOrderCodeInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryUnitByOrderCodeInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 更新
	 * @param parm
	 * @return
	 */
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据ORDER_CODE查PHA_BASE
	 * @param parm
	 * @return
	 */
	public TParm queryByOrderCodeInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryByOrderCodeInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查PHA_BASE
	 * @return
	 */
	public TParm queryInfo() {
		TParm result = new TParm();
		result = query("queryInfo");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 根据UNIT_CHN_DESC查询对应的UNIT_CODE
	 * @param parm
	 * @return
	 */
	public TParm queryUnitDescByCode(TParm parm) {
		TParm result = new TParm();
		result = query("queryUnitDescByCode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

}
