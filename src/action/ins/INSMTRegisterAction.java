package action.ins;

import jdo.ins.INSMTRegisterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * 
 * Title: 门特登记 \审核
 * 
 * Description:门特登记 \审核:门特登记开具或下载 \审核查询
 * 
 * Copyright: ProperSoft (c) 2011
 * 
 * @author pangben 2012-1-19
 * @version 2.0
 */
public class INSMTRegisterAction extends TAction{
	/**
	 * 审核修改执行状态
	 * @param parm
	 * @return
	 */
	public TParm updateStutsType(TParm parm){
		String optUser=parm.getValue("OPT_USER");//ID
		String optTerm=parm.getValue("OPT_TERM");//IP
		String statusType=parm.getValue("STATUS_TYPE_FLG");//状态修改
		TConnection connection = getConnection();
		TParm result=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm tempParm=parm.getRow(i);
			tempParm.setData("OPT_USER",optUser);//ID
			tempParm.setData("OPT_TERM",optTerm);//IP
			tempParm.setData("STATUS_TYPE",statusType);//审核状态STATUS_TYPE
			result=INSMTRegisterTool.getInstance().updateStutsType(tempParm);
			if (result.getErrCode()<0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
