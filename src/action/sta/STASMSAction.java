package action.sta;

import jdo.aci.ACIBadEventTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ���ձ����ŷ���ƽ̨�������� </p>
 * 
 * <p>Description: ҽ���ձ����ŷ���ƽ̨�������� </p>
 * 
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author wangbin 2014.07.15
 * @version 1.0
 */
public class STASMSAction extends TAction {

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
