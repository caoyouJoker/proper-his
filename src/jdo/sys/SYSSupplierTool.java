package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:供应厂商基本檔Tool
 * </p>
 * 
 * <p>
 * Description:供应厂商基本檔Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSSupplierTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSSupplierTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static SYSSupplierTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSSupplierTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSSupplierTool() {
		setModuleName("sys\\SYSSupplierModule.x");
		onInit();
	}

	/**
	 * 查询
	 * 
	 * @param ctz_code
	 * @param charge_hosp_code
	 * @return
	 */
	public TParm selectdata(String sup_code) {
		TParm parm = new TParm();
		if (sup_code != null && sup_code.length() > 0) {
			parm.setData("SUP_CODE", sup_code);
		}
		TParm result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
