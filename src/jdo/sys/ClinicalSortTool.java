package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * 后台处理临床项目列表工具类
 * 
 * @author Administrator
 * 
 */
public class ClinicalSortTool extends TJDOTool {

	private static ClinicalSortTool mInstanse;

	public static ClinicalSortTool getNewInstance() {
		if (mInstanse == null) {
			mInstanse = new ClinicalSortTool();
		}

		return mInstanse;
	}

	public ClinicalSortTool() {
		setModuleName("sys\\ClinicalSortModule.x");
		onInit();
	}

	/**
	 * 保存数据
	 */
	public boolean onSave(TParm parm) {
		TParm p = this.update("insert", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 更新数据
	 */
	public boolean onUpdate(TParm parm) {
		TParm p = this.update("update", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 删除数据
	 * @param parm
	 */
	public boolean onDelete(TParm parm) {
		if(update("delete", parm).getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 查询数据
	 * @param parm
	 */
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
}
