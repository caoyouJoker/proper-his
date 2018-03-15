package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: �豸ά������������</p>
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
     * ������
     */
    public DEVMaintenanceMasterTool() {
        setModuleName("dev\\DEVMaintenanceMasterModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DEVMaintenanceMasterTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DEVMaintenanceMasterTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMaintenanceMasterTool();
        return instanceObject;
    }
   
    /**
     * �����豸ά������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * �����豸ά������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * �����豸ά����ϸ��
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onInsertMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * �����豸ά����ϸ��
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onUpdateMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * ɾ���豸ά������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteMaintenanceMaster(TParm parm,TConnection connection){
        parm = this.update("onDeleteMaintenanceMaster", parm, connection);
        return parm;
    }
    
    /**
     * ɾ���豸ά����ϸ��ĳһ������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteAllMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onDeleteAllMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * ɾ���豸ά����ϸ��ĳһ������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteOneMaintenanceMasterDetail(TParm parm,TConnection connection){
        parm = this.update("onDeleteOneMaintenanceMasterDetail", parm, connection);
        return parm;
    }
    
    /**
     * ����DEV_MTN_DATE
     * �´�ά��ʱ���
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertDevMtnDate(TParm parm,TConnection connection){
        parm = this.update("onInsertDevMtnDate", parm, connection);
        return parm;
    }

}
