package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title:给号方式工具类 </p>
 *
 * <p>Description:给号方式工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class REGQueMethodTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static REGQueMethodTool instanceObject;
    /**
     * 得到实例
     * @return REGQueMethodTool
     */
    public static REGQueMethodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGQueMethodTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public REGQueMethodTool() {
        setModuleName("reg\\REGQueMethodModule.x");
        onInit();
    }
    /**
     * 查询给号方式
     * @param quegroupCode String
     * @param queNo String
     * @return TParm
     */
    public TParm queryTree(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;

    }
    /**
     * 新增给号方式
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("QUEGROUP_CODE");
        String queNo = parm.getValue("QUE_NO");
        if (existsQueMethod(quegroupCode, queNo)) {
            result.setErr( -1, "给号方式" + " 已经存在!");
            return result;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新给号方式
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新给号方式(带连接)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatQueNo(TParm parm, TConnection connection) {
        TParm result = update("updatQueNo", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询给号方式
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除给号方式
     * @param quegroupCode String
     * @param queNo String
     * @return TParm
     */
    public TParm deletedata(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在给号方式
     * @param quegroupCode String
     * @param queNo String
     * @return boolean
     */
    public boolean existsQueMethod(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        return getResultInt(query("existsQueMethod", parm), "COUNT") > 0;
    }

    /**
     * 设定到院时间
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm setArriveTime(TParm parm, TConnection connection) {
        TParm result = update("updateArriveTime", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 时间累加方法
     * @param startTime String
     * @param interveenTime String
     * @return String
     */
    public String addTime(String startTime, String interveenTime) {
        //传入小时
        String hourS = startTime.substring(0, 2);
        //传入分钟
        String minuteS = startTime.substring(2, 4);
        int hourM = TCM_Transform.getInt(hourS);
        int minuteM = TCM_Transform.getInt(minuteS);
        //间隔时间
        int partition = TCM_Transform.getInt(interveenTime);
        double c = (minuteM + partition) / 60;
        //分钟部分+间隔时间后,取整(小时进位数)
        int f = TCM_Transform.getInt(c);
        //分钟部分+间隔时间后,取余(分钟剩余数)
        int d = (minuteM + partition) % 60;
        minuteM = minuteM + partition;
        if (minuteM + partition > 59) {
            hourM = hourM + f;
            minuteM = d;
        }
        if (hourM > 23) {
            hourM = hourM - 24;
        }
        String hourMS = TCM_Transform.getString(hourM);
        int hourLength = hourMS.length();
        if (hourLength <= 1)
            hourLength = 2;
        String hourE = StringTool.fill("0", hourLength - hourMS.length()) +
            hourMS;
        String minuteMS = TCM_Transform.getString(minuteM);
        int minuteLength = minuteMS.length();
        if (minuteLength <= 1)
            minuteLength = 2;
        String minuteE = StringTool.fill("0", minuteLength - minuteMS.length()) +
            minuteMS;
        String endTime = hourE + minuteE;
        return endTime;
    }

    /**
     * 刷新table用查询
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataForTable(TParm parm) {
        TParm result = new TParm();
        result = query("seldataForTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
}
