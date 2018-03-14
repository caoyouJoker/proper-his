package action.spc;

import jdo.spc.SPCExportXmlToCmsClinicTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SPCExportXmlToCmsClinicAction extends TAction {

	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCExportXmlToCmsClinicTool.getInstance().onSave(parm,conn);
		if (result.getErrCode() < 0) {			
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result ;
        }	
		conn.commit();
        conn.close();
        return result;
	}
	
	
	
}

