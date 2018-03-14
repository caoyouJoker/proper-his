package action.aci;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.aci.ACIBadEventTool;

/**
 * <p>Title: 不良事件事务处理类 </p>
 * 
 * <p>Description: 不良事件事务处理类 </p>
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
     * 保存
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("参数错误");
            return result;
        }
        // 取得链接
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
