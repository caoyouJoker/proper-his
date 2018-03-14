package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: 中心通知下载
 * </p>
 * 
 * <p>
 * Description:中心通知下载
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
public class INSNoticeTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static INSNoticeTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSNoticeTool
	 */
	public static INSNoticeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSNoticeTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSNoticeTool() {
		setModuleName("ins\\INSNoticeModule.x");
		onInit();
	}

	/**
	 * 更新医保数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm saveTParm, TConnection connection) {
		TParm result = new TParm();
		//TParm saveTParm = parm.getParm("saveTParm");
		result = this.deleteData(saveTParm, connection);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
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
	 * 删除指定数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteData(TParm saveTParm, TConnection connection) {
		int count = saveTParm.getCount();
		TParm result=new TParm();
		for (int i = 0; i < count; i++) {
			TParm deleteTParm = saveTParm.getRow(i);
			 result = update("deletedata", deleteTParm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

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
