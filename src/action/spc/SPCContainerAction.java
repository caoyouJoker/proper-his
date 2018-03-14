package action.spc;

import jdo.spc.SPCInStoreTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 麻精容器Action
 * </p>
 *
 * <p>
 * Description: 麻精容器Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fuwj 2012.11.14
 * @version 1.0
 */
public class SPCContainerAction extends TAction {
	
	
	/**
	 * 病区麻精入智能柜
	 * @param parm
	 * @return
	 */
	public TParm statioaInStore(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCInStoreTool.getInstance().updateContainerD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		result= SPCInStoreTool.getInstance().updateContainerM(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		} 
		conn.commit(); 
		conn.close();			
		return result;
	}
	
}
