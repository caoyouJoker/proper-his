package action.hrm;

import jdo.hrm.HRMCompanyTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
*
* <p>Title: 健康检查合同动作类</p>
*
* <p>Description: 健康检查合同动作类</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMCompanyAction extends TAction {

	public TParm onSaveContract(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("参数错误");
			return result;
		}
		// 取得链接
		TConnection conn = getConnection();
		result = HRMCompanyTool.getInstance().onSaveContract(parm, conn);
		if(result.getErrCode()!=0){
			conn.rollback();
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}
}
