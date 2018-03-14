package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title:临床研究项目后台数据库工具
 * </p>
 * 
 * <p>
 * Description:临床研究项目后台数据库工具
 * </p>
 *
 */
public class CLPCliProjectTool extends TJDOTool{
	
	private static CLPCliProjectTool mInstance;
	
	public static CLPCliProjectTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new CLPCliProjectTool();
		}
		return mInstance;
	}

	public CLPCliProjectTool() {
		this.setModuleName("clp\\CLPCliProjectModule.x");
        onInit();
	}
	
	/**
	 * 查询
	 * @param parm
	 */
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
	
	/**
	 * 删除
	 */
	public boolean onDelete(TParm parm) {
		if(update("delete", parm).getErrCode() <0) {
			return false;
		}
		return true;
	}
	/**
	 * 更新
	 * @param parm : 更新的元素
	 */
	public boolean onUpdate(TParm parm) {
		TParm p = update("update", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 保存
	 * @param parm ： 保存的元素
	 */
	public boolean onSave(TParm parm) {
		TParm p = update("save", parm);
		if(p.getErrCode() < 0) {
			return false;
		}
		return true;
	}
}
