/**
 * 
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-6-13下午03:01:50
 * @version 1.0
 */
public class ReplaceMedicineTool extends TJDOTool {
	private static ReplaceMedicineTool instance;

	private ReplaceMedicineTool() {
		setModuleName("sys\\ReplaceMedicineModule.x");
		onInit();
	};

	public static ReplaceMedicineTool getInstance() {
		if (instance == null)
			instance = new ReplaceMedicineTool();
		return instance;
	}

	/**
	 * 查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("queryAll", parm);
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	/**
	 * 查询 获得SEQ
	 */
	public TParm onQuerySEQ() {
		TParm result = this.query("queryMaxSEQ");
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// 根据药品编号和替药品编号修改
	public TParm onUpdateByCode(TParm parm) {
		TParm result = this.update("onUpdateByCode", parm);
		if (result.getErrCode() < 0) {
			err("Err" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}
	// 根据药品编号和替药品编号修改
	public TParm onQueryByCode(TParm parm) {
		TParm result = this.update("onQueryByCode", parm);
		System.out.println("-------根据药品编号和替药品编号修改: parm: "+ result);
		if (result.getErrCode() < 0) {
			err("Err" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// 新增
	public TParm onSave(TParm parm) {
		TParm result = this.update("onSave", parm);

		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}

	// 删除
	public TParm onDelete(TParm parm) {
		TParm result = this.update("onDelete", parm);
		if (result.getErrCode() < 0) {
			err("Err:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
		}
		return result;
	}
}
