package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 护士站数据</p>
 *
 * <p>Description: 护士站数据</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-09-18
 * @version 1.0
 */
public class SYSStationTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSStationTool instanceObject;
    /**
     * 得到实例
     * @return DeptTool
     */
    public static SYSStationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSStationTool();
        return instanceObject;
    }

//    /**
//     * 构造器
//     */
//    public SYSStationTool() {
//        setModuleName("inv\\INVOrgModule.x");
//        onInit();
//    }

    /**
     * 删除护士站及其下面的房间,以及房间中的床位
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteStation(TParm parm,TConnection connection) {
        TParm result = new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TParm inParm=parm.getParm("DELETE");
        String deleteStationSql=inParm.getValue("DELETESTATION");
        //执行删除护士站
       result = new TParm(TJDODBTool.getInstance().update(deleteStationSql,
           connection));
       if (result.getErrCode() < 0)
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());

        String deleteRoomSql=inParm.getValue("DELETEROOM");
        //执行删除房间
     result = new TParm(TJDODBTool.getInstance().update(deleteRoomSql,
         connection));
     if (result.getErrCode() < 0)
         err("ERR:" + result.getErrCode() + result.getErrText() +
             result.getErrName());

        String deleteBedSql=inParm.getValue("DELETEBED");
        //执行删除病床
        result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());


        return result;
    }
    /**
     * 删除房间及其下面的床位
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteRoom(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TParm inParm = parm.getParm("DELETE");
        String deleteRoomSql = inParm.getValue("DELETEROOM");
        //执行删除房间
        result = new TParm(TJDODBTool.getInstance().update(deleteRoomSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        String deleteBedSql = inParm.getValue("DELETEBED");
        //执行删除床位
        result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        return result;
    }
    /**
     * 删除床位
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteBed(TParm parm,TConnection connection) {
       TParm result = new TParm();
       if(parm==null)
            return result.newErrParm(-1,"参数为空");
       TParm inParm=parm.getParm("DELETE");
       String deleteBedSql = inParm.getValue("DELETEBED");
       //执行删除床位
       result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
           connection));
       if (result.getErrCode() < 0)
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());

       return result;
   }

   /**
    * 查询病区所在区域
    * @param parm TParm
    * @return TParm
    */
   public TParm selStationRegion(String stationCode) {
       TParm result = new TParm();
       String sql =
           " SELECT REGION_CODE FROM SYS_STATION WHERE STATION_CODE = '" + stationCode + "' ";
       result = new TParm(TJDODBTool.getInstance().select(sql));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }


}
