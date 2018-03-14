package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: 设备保养工具类</p>
 *
 * <p>Copyright: BlueCore 2015</p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceTool extends TJDOTool {
	/**
     * 构造器
     */
    public DEVMaintenanceTool() {
        setModuleName("dev\\DEVMaintenanceModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DEVMaintenanceTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceTool();
        return instanceObject;
    }
   
    /**
     * 更新设备维护/计量时间
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenance(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenance", parm, connection);
        return parm;
    }
    
    
}
