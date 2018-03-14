package action.adm;

import jdo.adm.ADMDaySurgeryTool;
import jdo.sys.SYSComorderReplaceTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: 日间手术变更
 * </p>
 * 
 * <p>
 * Description: 日间手术变更动作类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)   2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhagnlei 2017.03.31
 * @version 5.0
 */
public class ADMDaySurgeryAction extends TAction {
	
	/**
	 * 添加修改方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		 //更新
         result = ADMDaySurgeryTool.getInstance().update(parm,
				connection);

			if (result.getErrCode() < 0) {;
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
				}
		connection.commit();
		System.out.println("commit");
		connection.close();
		return result;
	}
}
