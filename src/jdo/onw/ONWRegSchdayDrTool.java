package jdo.onw;

import jdo.sys.SYSPhaDoseTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.ArrayList;
import jdo.reg.SessionTool;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 普通出诊医师维护</p>
 *
 * <p>Description:普通出诊医师维护 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author JiaoY
 * @version 1.0
 */

public class ONWRegSchdayDrTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static ONWRegSchdayDrTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static ONWRegSchdayDrTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWRegSchdayDrTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ONWRegSchdayDrTool() {
        setModuleName("reg\\RegSchdayDrModule.x");
        onInit();
    }

    /**
     * 查询数据
     * @param CLINIC_AREA String 诊区CODE
     * @return TParm
     */

    public TParm selectdata(String CLINIC_AREA) {
        TParm parm = new TParm();
        parm.setData("CLINIC_AREA", CLINIC_AREA);
        TParm result = query("selectdate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询数据
     * @return TParm
     */
    public TParm selectdata() {
        TParm result = query("selectdate");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 初始化数据同步
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertdata", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 删除数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("deletedata", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 保存事件
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateAll", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 初始化查询数据
     * @param parm TParm
     * @return TParm
     */
    public TParm initsel(TParm parm) {
        TParm result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * update 科别 医师 号别
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("update", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 清空 科别 医师
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm clear(TConnection connection) {
        TParm result = new TParm();
        result = update("clear", connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
