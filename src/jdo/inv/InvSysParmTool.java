package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:物质参数设定Tool
 * </p>
 * 
 * <p>
 * Description:物质参数设定Tool
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
 * @author wangzl 2013.1.5
 * @version 1.0
 */

public class InvSysParmTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static InvSysParmTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndSysParmTool
	 */
	public static InvSysParmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InvSysParmTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public InvSysParmTool() {
		setModuleName("inv\\INVSysParmModule.x");
		onInit();
	}

	/**
	 * 添加新数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertData", parm);
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
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("updateData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询
	 * 
	 * @return
	 */
	public TParm onQuery() {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INVNewSQL.getINDSysParm()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
