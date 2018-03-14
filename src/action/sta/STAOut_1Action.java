package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_1Tool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Description: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STAOut_1Action
    extends TAction {
    public STAOut_1Action() {
    }
    /**
     * 修改STA_OUT_01中的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_OUT_01(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数为空");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_1Tool.getInstance().updateSTA_OUT_01(parm,conn);
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
     * 插入STA_OUT_01表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_01(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数为空");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_1Tool.getInstance().insertData(parm,conn);
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
