package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 人员职别工具类
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 20080818
 *
 * <p>Company: javahis
 *
 * @author wangl 2008.08.18
 * @version 1.0
 */
public class PositionTool extends TJDOTool{
    /**
     * 实例
     */
    public static PositionTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static PositionTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new PositionTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public PositionTool()
    {
        setModuleName("sys\\SYSPositionModule.x");
        onInit();
    }
    /**
     * 新增指定编号职别数据
     * @param posCode String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String code = parm.getValue("POS_CODE");
        if(existsPosition(code)){
            result.setErr(-1,"职别编号 "+code+" 已经存在!");
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
     * 更新指定编号职别数据
     * @param posCode String
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
     * 根据职别代码查询职别信息(右忽略)
     * @param posCode String 职别代码
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        //针对于LIKE条件--------start----------
        if (parm == null)
            return null;
        if (parm.existData("POS_CODE")) {
            String posCode = parm.getValue("POS_CODE");
            posCode += "%";
            parm.setData("POS_CODE", posCode);
        }
        //针对于LIKE条件--------end----------

        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除指定编号职别数据
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String posCode){
        TParm parm = new TParm();
        parm.setData("POS_CODE",posCode);
        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在职别
     * @param posCode String 职别代码
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsPosition(String posCode){
        TParm parm = new TParm();
        parm.setData("POS_CODE",posCode);
        return getResultInt(query("existsPosition",parm),"COUNT") > 0;
    }
}
