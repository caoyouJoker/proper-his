package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 *
 * <p>Title:周班转日班工具类 </p>
 *
 * <p>Description:周班转日班工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.18
 * @version 1.0
 */
public class SchWeekForDayTool extends TJDOTool{
    /**
     * 实例
     */
    public static SchWeekForDayTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekForDayTool
     */
    public static SchWeekForDayTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SchWeekForDayTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SchWeekForDayTool()
    {
        setModuleName("reg\\REGSchWeekForDayModule.x");
        onInit();
    }
    /**
     * 新增周班表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }}
