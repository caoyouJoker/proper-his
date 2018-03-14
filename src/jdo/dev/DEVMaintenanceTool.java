package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: �豸����������</p>
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
     * ������
     */
    public DEVMaintenanceTool() {
        setModuleName("dev\\DEVMaintenanceModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DEVMaintenanceTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceTool();
        return instanceObject;
    }
   
    /**
     * �����豸ά��/����ʱ��
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenance(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenance", parm, connection);
        return parm;
    }
    
    
}
