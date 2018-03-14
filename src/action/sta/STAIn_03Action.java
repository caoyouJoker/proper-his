package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.sta.STAIn_02Tool;
import jdo.sta.STAIn_03Tool;

/**
 * <p>Title: 医院住院病患动态及疗效报表</p>
 *
 * <p>Description: 医院住院病患动态及疗效报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-21
 * @version 1.0
 */
public class STAIn_03Action
    extends TAction {
    /**
     * 导入STA_IN_03表信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_03(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"参数不可为空！");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAIn_03Tool.getInstance().insertData(parm,conn);
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
     * 修改STA_IN_03表数据
     * @param parm TParm
     * @return TParm
     * ============pangben modify 20110526
     */
    public TParm updateSTA_IN_03(TParm parm){
       TParm result = new TParm();
       if(parm==null){
           result.setErr(-1,"参数不可为空！");
           return result;
       }
       TConnection conn = this.getConnection();
       for(int i=0;i<parm.getCount();i++){
           TParm temp=parm.getRow(i);
           result = STAIn_03Tool.getInstance().updateSTA_IN_03(temp,conn);
           if (result.getErrCode() < 0) {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               conn.close();
               return result;
           }
       }
       conn.commit();
       conn.close();
       return result;
   }
}
