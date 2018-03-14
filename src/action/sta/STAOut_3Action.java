package action.sta;

import com.dongyang.action.*;
import jdo.sta.STAOut_3Tool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 出院病人疾病分类年龄别情况月报表(卫统5表2) </p>
 *
 * <p>Description: 出院病人疾病分类年龄别情况月报表(卫统5表2)</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-14
 * @version 1.0
 */
public class STAOut_3Action
    extends TAction {
    public STAOut_3Action() {
    }
    /**
     * 导入STA_OUT_03表的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_03(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数不可为空");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_3Tool.getInstance().insertData(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

}
