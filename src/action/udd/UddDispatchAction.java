package action.udd;

import jdo.udd.UddDispatchTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 *
 * <p>
 * Title: 住院药房摆药动作类
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui 20090527
 * @version 1.0
 */
public class UddDispatchAction extends TAction {
	/**
	 * Action入口方法
	 * @param parm
	 * @return
	 */
	public TParm saveDispatch(TParm parm){
		//luhai modify 2012-04-14 begin add try catch  
//		TParm inParm =parm.getParm("IN_MAP");
//		TConnection conn=this.getConnection();
//		TParm result=UddDispatchTool.getInstance().saveDispatch(inParm,conn);
//		conn.close();
//		return result;
		TParm inParm =parm.getParm("IN_MAP");
		TConnection conn=null;
		TParm result=null;
		try {
			conn=this.getConnection();
			result = UddDispatchTool.getInstance().saveDispatch(inParm, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			conn.close();
			return result;
		}
		//luhai modify 2012-04-14 end  
	}
}
