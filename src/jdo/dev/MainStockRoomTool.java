package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 主库部门设定</p>
 *
 * <p>Description: 主库部门设定</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class MainStockRoomTool  extends TJDOTool{
    /**
     * 构造器
     */
    public MainStockRoomTool() {
        setModuleName("dev\\MainStockRoomModule.x");
        onInit();
    }
    /**
     * 实例
     */
    private static MainStockRoomTool instanceObject;
    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static MainStockRoomTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new MainStockRoomTool();
        return instanceObject;
    }

    /**
     * 得到主库部门信息
     * @param deptCode String
     * @return TParm
     */
    public TParm selectDevDeptInf(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm = query("selectDevDeptInf",parm);
        return parm;
    }
    /**
     * 更新主库部门信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDevDeptInf(TParm parm){
        parm = update("updateDevDeptInf",parm);
        return parm;
    }
    /**
     * 写入主库部门信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDevDeptInf(TParm parm){
        parm = update("insertDevDeptInf",parm);
        return parm;
    }

    /**
     * 删除主库部门信息
     * @param deptCode String
     * @return TParm
     */
    public TParm deleteDevDeptInf(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", deptCode);
        parm = update("deleteDevDeptInf",parm);
        return parm;
    }

    /**
     * 取得设备主库最大顺序号
     * @return int
     */
    public int getDevDeptMaxSeq(){
        TParm parm = query("getDevDeptMaxSeq");
        if(parm.getErrCode() < 0)
            return 1;
        if(parm.getCount("SEQ") <= 0)
            return 1;
        if(parm.getValue("SEQ",0) == null ||
           parm.getValue("SEQ",0).length() == 0)
            return 1;
        return parm.getInt("SEQ",0) + 1;
    }
}
