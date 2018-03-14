package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: 诚信审核信息下载
 * </p>
 * 
 * <p>
 * Description:诚信审核信息下载
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSBlackListTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static INSBlackListTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSNoticeTool
	 */
	public static INSBlackListTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSBlackListTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSBlackListTool() {
		setModuleName("ins\\INSBlackListModule.x");
		onInit();
	}

	/**
	 * 更新医保数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm saveTParm = parm.getParm("saveTParm");
		if (parm.getValue("ISALLOWCOVER").equalsIgnoreCase("Y")) {
			result = this.deleteData(parm, connection);
			// 判断错误值
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		int count = saveTParm.getCount();
		for (int i = 0; i < count; i++) {
			TParm insertTParm = saveTParm.getRow(i);
			result = update("insertdata", insertTParm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

		}

		return result;
	}

	/**
	 * 插入医保数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();

		result = update("insertdata", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除指定时间内的数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteData(TParm parm, TConnection connection) {
		TParm result = update("deletedata", parm, connection);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 保存通知内容
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm, TConnection connection) {
		TParm result = update("updatedata", parm, connection);
		// 判断错误值
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
	 * @param queryTParm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectdata(TParm queryTParm) {
		TParm result = query("selectdata", queryTParm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
