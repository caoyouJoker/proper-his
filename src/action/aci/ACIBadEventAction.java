package action.aci;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.aci.ACIBadEventTool;

/**
 * <p>Title: �����¼��������� </p>
 * 
 * <p>Description: �����¼��������� </p>
 * 
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BLueCore </p>
 *
 * @author wanglong 2013.11.01
 * @version 1.0
 */
public class ACIBadEventAction extends TAction {

    /**
     * ����
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("��������");
            return result;
        }
        // ȡ������
        TConnection conn = getConnection();
        result = ACIBadEventTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

}
