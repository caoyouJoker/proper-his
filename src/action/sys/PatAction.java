package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.sys.PatTool;

public class PatAction extends TAction{
    public TParm getPatName(TParm parm)
    {
        TParm result = PatTool.getInstance().getInfoForMrno(parm.getValue("MR_NO"));
        result.setData("TEXT","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return result;
    }
}
