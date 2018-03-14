package action.hrm;

import jdo.hrm.HRMRisPrintTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 检查单打印Action
 * </p>
 *
 * <p>Description: 检查单打印Action</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangm
 * @version 1.0
 */
public class HRMRisPrintAction  extends TAction{

	
	public TParm saveHRMRisPrintStat(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = HRMRisPrintTool.getInstance().saveHRMRisPrintStat(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
	
	
	
}
