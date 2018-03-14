package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:到院时间工具类 </p>
 *
 * <p>Description:到院时间工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.12
 * @version 1.0
 */
public class ArvTimeTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ArvTimeTool instanceObject;
    /**
     * 得到实例
     * @return REGArvTimeTool
     */
    public static ArvTimeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ArvTimeTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ArvTimeTool() {
        setModuleName("reg\\REGArvTimeModule.x");
        onInit();
    }
    /**
     * 新增到院时间
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String queGrop = parm.getValue("QUE_GROUP");
        String startTime = parm.getValue("START_TIME");
        if(existsArvTime(queGrop,startTime)){
            result.setErr(-1,"到院时间"+" 已经存在!");
            return result ;
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
     * 更新到院时间
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
     * 根据给号组别，开始时间查询到院时间
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
     * 删除到院时间
     * @param queGrop String
     * @param startTime String
     * @return TParm
     */
    public TParm deletedata(String queGrop,String startTime) {
        TParm parm = new TParm();
        parm.setData("QUE_GROUP", queGrop);
        parm.setData("START_TIME", startTime);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在到院时间
     * @param queGrop String
     * @param startTime String
     * @return boolean
     */
    public boolean existsArvTime(String queGrop,String startTime) {
        TParm parm = new TParm();
        parm.setData("QUE_GROUP", queGrop);
        parm.setData("START_TIME", startTime);
        return getResultInt(query("existsArvTime", parm), "COUNT") > 0;
    }

}
