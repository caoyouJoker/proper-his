package action.inw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.odi.TestNtool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INWOrderSingleExeAction extends TAction{
    public INWOrderSingleExeAction() {
    }
    public TParm onSaveExe(TParm parm){
        TParm parmM = parm.getParm("DSPNM");
        TParm parmD = parm.getParm("DSPND");
        TParm result = new TParm();
        TConnection connection = getConnection();
        int rowM = parmM.getCount("CASE_NO");
        for(int i = 0;i<rowM;i++){
            result = TestNtool.getInstance().updateDspnm(parmM.getRow(i),connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                connection.rollback();
                connection.close();
                return result;
            }
        }
        int rowD = parmD.getCount("CASE_NO");
        for(int i = 0;i<rowD;i++){
            result = TestNtool.getInstance().updateDspnd(parmD.getRow(i),connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }
}
