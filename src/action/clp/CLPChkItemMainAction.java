package action.clp;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.ChkItemMainTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPChkItemMainAction
    extends TAction {
    public CLPChkItemMainAction() {
    }

    /**
     * 关键诊疗项目执行 保存
     * @param parm TParm 列表
     */
    public TParm saveManagerD(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        int count = parm.getCount("CASE_NO");
        for (int i = 0; i < count - 1; i++) {
            TParm rowParm = parm.getRow(i);
            String optFlg = rowParm.getValue("OPT_FLG");
            if ("U".equals(optFlg)) {
                result = ChkItemMainTool.getInstance().updateManagerD(rowParm,
                    conn);
                if (result.getErrCode() < 0) {
                    conn.close();
                    return result;
                }
            }
            if ("I".equals(optFlg)) {
                result = ChkItemMainTool.getInstance().insertManagerD(rowParm,
                    conn);
                if (result.getErrCode() < 0) {
                    conn.close();
                    return result;
                }
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 关键诊疗项目执行 删除
     * @param parm TParm 列表
     */
    public TParm deleteManagerD(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        int count = parm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            result = ChkItemMainTool.getInstance().deleteManagerD(parm.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

}
