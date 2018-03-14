package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_4Tool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Description: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-16
 * @version 1.0
 */
public class STAOut_4Action
    extends TAction {
    public STAOut_4Action() {
    }
    /**
     * 导入STA_OUT_04数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_04(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数不可为空");
        }
        TConnection conn = this.getConnection();
        result = STAOut_4Tool.getInstance().insertData(parm,conn);
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
    /**
     * 修改STA_OUT_04中的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_OUT_04(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数为空");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_4Tool.getInstance().updateSTA_OUT_04(parm,conn);
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
