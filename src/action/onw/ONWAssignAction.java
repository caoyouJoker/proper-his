package action.onw;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.reg.PatAdmTool;
import jdo.reg.REGTool;
import jdo.reg.SchDayTool;

/**
 * <p>Title: 门急诊护士站分诊</p>
 *
 * <p>Description: 门急诊护士站分诊</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-9-25
 * @version 1.0
 */
public class ONWAssignAction
    extends TAction {
    /**
     * 分诊
     * @param parm TParm
     * @return TParm
     */
    public TParm onAssign(TParm parm){
        TConnection conn = getConnection();
        TParm selPatadm = new TParm();
        TParm parm1 = new TParm();
        parm1.setData("REGION_CODE",parm.getData("REGION_CODE"));
        parm1.setData("ADM_TYPE",parm.getData("ADM_TYPE"));
        parm1.setData("ADM_DATE",parm.getData("ADM_DATE"));
        parm1.setData("SESSION_CODE",parm.getData("SESSION_CODE"));
        parm1.setData("DEPT_CODE_SORT",parm.getData("REALDEPT_CODE"));
        selPatadm = SchDayTool.getInstance().selectDrTable(parm1);
        TParm result = REGTool.getInstance().onSaveTriage(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        int queNo =  selPatadm.getInt("QUE_NO",0);
        parm.setData("QUE_NO",queNo+1);
        result = PatAdmTool.getInstance().updateInfoForONW(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
