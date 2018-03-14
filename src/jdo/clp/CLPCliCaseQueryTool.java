package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:临床病案查询Tool </p>
 * 
 * <p>Description:临床病案查询Tool </p>
 * 
 * <p>Company:bluecore Copyright C </p>
 * @author wukai on 20160524
 *
 */
public class CLPCliCaseQueryTool extends TJDOTool{
	
	private static CLPCliCaseQueryTool mInstance;
	
	public static CLPCliCaseQueryTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new CLPCliCaseQueryTool();
		}
		return mInstance;
	} 
	
	public CLPCliCaseQueryTool () {
		this.setModuleName("clp\\CLPCliCaseQueryModule.x");
		super.onInit();
	}
		
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
	
	/**
	 * 删除病例
	 * @param parm
	 * @return
	 */
	public boolean deleteCase(TParm parm) {
		if(this.update("delete",parm).getErrCode() < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 查询出所有的病例
	 * @return
	 */
	public TParm queryAll(){
		return query("queryAll");
	}
}
