package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: 设备维护主档工具类</p>
 *
 * <p>Copyright: ProperSoft 2015</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceMasterTool extends TJDOTool {
	/**
     * 构造器
     */
    public DEVMaintenanceMasterTool() {
        setModuleName("dev\\DEVMaintenanceMasterModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DEVMaintenanceMasterTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceMasterTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceMasterTool();
        return instanceObject;
    }
   
    /**
     * 新增设备维护主档
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * 更新设备维护主档
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * 插入设备维护明细表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * 更新设备维护明细表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * 删除设备维护主表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onDeleteMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * 删除设备维护明细表某一条数据
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteAllMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onDeleteAllMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * 删除设备维护明细表某一条数据
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteOneMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onDeleteOneMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * 插入DEV_MTN_DATE
     * 下次维护时间表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertDevMtnDate(TParm parm,TConnection connection){
        parm = this.update("onInsertDevMtnDate", parm, connection);
        return parm;
    }

}
