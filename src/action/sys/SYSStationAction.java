package action.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.CTZTool;
import jdo.sys.StatusDetailsTool;
import com.dongyang.action.TAction;
import jdo.sys.SYSStationTool;

/**
 * <p>Title: 护士站处理action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-09-17
 * @version 1.0
 */
public class SYSStationAction extends TAction {
    /**
     * 删除护士站及其下面的房间还有房间中的床位
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteStation(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSStationTool.getInstance().deleteStation(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 删除房间以及房间中的床位
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteRoom(TParm parm) {
       TConnection connection = getConnection();
       TParm result = new TParm();
       result = SYSStationTool.getInstance().deleteRoom(parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * 删除床位
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteBed(TParm parm) {
       TConnection connection = getConnection();
       TParm result = new TParm();
       result = SYSStationTool.getInstance().deleteBed(parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }


}
