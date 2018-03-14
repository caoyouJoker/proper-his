package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:身份及折扣明细档Tool
 * </p>
 * 
 * <p>
 * Description:身份及折扣明细档Tool
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
public class SYSChargeDetailTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSChargeDetailTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static SYSChargeDetailTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSChargeDetailTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSChargeDetailTool() {
		setModuleName("sys\\SYSChargeDetailModule.x");
		onInit();
	}

	/**
	 * 查询
	 * 
	 * @param ctz_code
	 * @param charge_hosp_code
	 * @return
	 */
	public TParm selectdata(String ctz_code, String charge_hosp_code) {
		TParm parm = new TParm();
		if (ctz_code != null && ctz_code.length() > 0
				&& charge_hosp_code != null && charge_hosp_code.length() > 0) {
			parm.setData("CTZ_CODE", ctz_code);
			parm.setData("CHARGE_HOSP_CODE", charge_hosp_code);
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
