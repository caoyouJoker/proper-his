package action.onw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import jdo.onw.ONWRegSchdayDrTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.SessionTool;
import java.util.Iterator;
import jdo.reg.SchDayTool;


/**
 *
 * <p>Title: 普通诊出诊医师维护</p>
 *
 * <p>Description:普通诊出诊医师维护 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author JiaoY
 *
 * @version 1.0
 */
public class ONWRegSchdayDrAction
    extends TAction {

    /**
     * 诊间诊室数据同步
     * @param parm TParm
     * @return TParm
     */
    public TParm seldate(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        Map map = new HashMap();
        String regionCode= parm.getValue("REGION_CODE");
        //查询普通诊医生班表的排班信息
        TParm schDayDr = ONWRegSchdayDrTool.getInstance().selectdata();
        int count = schDayDr.getCount();
        //将普通诊医生班表的逐渐拼接成字符串 用于与日版表的信息进行对比，找出新诊室
        for (int i = 0; i < count; i++) {
            String key = schDayDr.getValue("ADM_TYPE", i) + ":" +
                schDayDr.getValue("SESSION_CODE", i) + ":" +
                schDayDr.getValue("CLINIC_AREA", i) + ":" +
                schDayDr.getValue("CLINICROOM_NO", i);
            map.put(key, i);
        }
        if (parm.getErrCode() < 0)
            return null;
        //设置门急别
        TParm admTypes = new TParm();
        admTypes.addData("admType", "O");
        admTypes.addData("admType", "E");
        for (int admIndex = 0; admIndex < admTypes.getCount("admType");
             admIndex++) {
            String admType = admTypes.getData("admType", admIndex).toString();
            TParm ro = new TParm();
            ro.setData("ADM_TYPE", admType); //门急住别
            ro.setData("REGION_CODE",regionCode);
            TParm rooms = ClinicRoomTool.getInstance().getClinicRoomForONW(ro); //获取诊室信息
            String[] sessions = SessionTool.getInstance().getSessionCode(
                admType,regionCode); //得到的时段编号
            int roomCount = rooms.getCount();
            for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
                for (int sessionIndex = 0; sessionIndex < sessions.length;
                     sessionIndex++) {
                    String key = admType + ":" + sessions[sessionIndex] + ":" +
                        rooms.getValue("CLINICAREA_CODE", roomIndex) + ":" +
                        rooms.getValue("CLINICROOM_NO", roomIndex);
                    //如果该主键字符转 在 普通班表中不存在 就是新的诊室 需要插入到普通班表中
                    if (map.get(key) == null) {
                        //insert
                        TParm inParm = new TParm();
                        inParm.setData("ADM_TYPE", admType);
                        inParm.setData("SESSION_CODE", sessions[sessionIndex]);
                        inParm.setData("CLINIC_AREA",
                                       rooms.getValue("CLINICAREA_CODE",
                            roomIndex));
                        inParm.setData("CLINICROOM_NO",
                                       rooms.getValue("CLINICROOM_NO",
                            roomIndex));
                        inParm.setData("REGION",
                                       rooms.getValue("REGION_CODE", roomIndex));
                        inParm.setData("OPT_USER", parm.getData("OPT_USER"));
                        inParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
                        if (inParm.getValue("REGION") == null) {
                            return null;
                        }
                        result = ONWRegSchdayDrTool.getInstance().insertData(
                            inParm, connection);
                        //插入完成
                        if (result.getErrCode() < 0) {
                            connection.close();
                            return result;
                        }
                    }
                    else//如果班表中存在 则删除Map中逐渐字符串
                        map.remove(key);
                    //----同步完成-----
                }
            }
        }
        //map中剩下的值 是目前日版表中已经不存在的诊室，所以要将普通排班表中的对应的诊室删除掉
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
//          ---------删除-----------
            String name = (String) iterator.next();
            int index = (Integer) map.get(name);
            TParm inParm = new TParm();
            inParm.setRowData( -1, schDayDr, index);
            inParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
            result = ONWRegSchdayDrTool.getInstance().deleteData(inParm,
                connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 与日排班同步
     * @param parm TParm
     * @return TParm
     */
    public TParm selReghday(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm date = new TParm();
        TParm seldate = new TParm();
        TParm select = ONWRegSchdayDrTool.getInstance().selectdata(); //查询REG_SCHDAY_DR

        select.setData("REGION_CODE", parm.getData("REGION_CODE"));
        select.setData("ADM_DATE", parm.getData("ADM_DATE"));
        select.setData("OPT_USER", parm.getData("OPT_USER"));
        select.setData("OPT_TERM", parm.getData("OPT_USER"));

        date = SchDayTool.getInstance().selectdata(parm); //查询日班表
        int count = date.getCount();

        TParm schdydr = ONWRegSchdayDrTool.getInstance().clear(connection); //清空REG_SCHDAY_DR里 的 科室 医生
        for (int i = 0; i < count; i++) {
            seldate = new TParm();
            seldate.setData("REGION_CODE", parm.getValue("REGION_CODE"));
            seldate.setData("ADM_TYPE", date.getData("ADM_TYPE", i));
            seldate.setData("SESSION_CODE", date.getData("SESSION_CODE", i));
            seldate.setData("CLINICROOM_NO", date.getData("CLINICROOM_NO", i));
            seldate.setData("DEPT_CODE", date.getData("DEPT_CODE", i));
            seldate.setData("DR_CODE",
                            (date.getData("DR_CODE", i) == null) ? "" :
                            date.getData("DR_CODE", i));
            seldate.setData("CLINICTYPE_CODE",
                            date.getData("CLINICTYPE_CODE", i));
            seldate.setData("OPT_USER", parm.getValue("OPT_USER"));
            seldate.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            result = new TParm();
            result = ONWRegSchdayDrTool.getInstance().update(seldate,
                connection); //与日班表 同步
            if(result.getErrCode()<0){
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 保存 （insert reg_schday ,updata reg_schday_dr）
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = SchDayTool.getInstance().insertdata(parm,
            connection); //向日班表插入数据
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        result = ONWRegSchdayDrTool.getInstance().onSave(parm, connection); //更新chday_dr表
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdata(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = SchDayTool.getInstance().updateForSchdayDr(parm, connection); //更新Schday表
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        result = ONWRegSchdayDrTool.getInstance().onSave(parm, connection); //更新chday_dr表
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
