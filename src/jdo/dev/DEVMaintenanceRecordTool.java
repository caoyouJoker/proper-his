package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: 设备维护记录工具类</p>
 *
 * <p>Copyright: ProperSoft 2015</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceRecordTool extends TJDOTool {
	/**
     * 构造器
     */
    public DEVMaintenanceRecordTool() {
        setModuleName("dev\\DEVMaintenanceRecordModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DEVMaintenanceRecordTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceRecordTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceRecordTool();
        return instanceObject;
    }
   
    /**
     * 新增设备维护记录
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceRecord(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceRecord", parm, connection);
        return parm;
    }
    
    /**
     * 更新设备下次维护时间
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceDate(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceDate", parm, connection);
        return parm;
    }
    
}
