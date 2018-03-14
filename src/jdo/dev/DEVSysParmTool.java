package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 设备编码方式</p>
 *
 * <p>Description:设备编码方式 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DEVSysParmTool  extends TJDOTool{
    /**
     * 构造器
     */
    public DEVSysParmTool() {
        setModuleName("dev\\DevSysParmModule.x");
        onInit();
    }
    /**
     * 实例
     */
    private static DEVSysParmTool instanceObject;
    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DEVSysParmTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVSysParmTool();
        return instanceObject;
    }

    /**
     * 取得药库参数档信息
     * @return TParm
     */
    public TParm selectDevSysParm(){
        TParm parm = new TParm();
        parm = query("selectDevSysParm",parm);
        return parm;
    }
    /**
     * 写入药库参数档信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDevSysParm(TParm parm){
        parm = update("insertDevSysParm",parm);
        return parm;
    }

    /**
     * 更新药库参数档信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDevSysParm(TParm parm){
        parm = update("updateDevSysParm",parm);
        return parm;
    }
}
