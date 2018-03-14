package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:端末主档Tool
 * </p>
 * 
 * <p>
 * Description:端末主档Tool
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
public class SYSTerminalTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSTerminalTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static SYSTerminalTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSTerminalTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSTerminalTool() {
		setModuleName("sys\\SYSTerminalModule.x");
		onInit();
	}

	/**
	 * 查询
	 * 
	 * @param ctz_code
	 * @param charge_hosp_code
	 * @return
	 */
	public TParm selectdata(String term_no) {
		TParm parm = new TParm();
		if (term_no != null && term_no.length() > 0) {
			parm.setData("TERM_NO", term_no);
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
