package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: �豸ά����¼������</p>
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
     * ������
     */
    public DEVMaintenanceRecordTool() {
        setModuleName("dev\\DEVMaintenanceRecordModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DEVMaintenanceRecordTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceRecordTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceRecordTool();
        return instanceObject;
    }
   
    /**
     * �����豸ά����¼
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceRecord(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceRecord", parm, connection);
        return parm;
    }
    
    /**
     * �����豸�´�ά��ʱ��
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceDate(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceDate", parm, connection);
        return parm;
    }
    
}
