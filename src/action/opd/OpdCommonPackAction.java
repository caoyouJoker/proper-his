package action.opd;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class OpdCommonPackAction extends TAction {
	/**
	 * 连接
	 */
	TConnection connection;
	/**
	 * 门急医生站保存入口
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		//System.out.println("in     onSave method");
		String[] sql=(String[]) parm.getData("SQL");
		for(String sqls:sql){
			//System.out.println("save sql====="+sqls);
		}
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()!=0){
			return result;
		}
		return result;
	}
}
