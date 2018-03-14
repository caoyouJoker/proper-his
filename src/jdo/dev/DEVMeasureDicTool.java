package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: �豸�����ֵ乤����</p>
 *
 * <p>Copyright: BlueCore 2015</p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVMeasureDicTool extends TJDOTool {
	/**
     * ������
     */
    public DEVMeasureDicTool() {
        setModuleName("dev\\DEVMeasureDicModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DEVMeasureDicTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DEVMeasureDicTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVMeasureDicTool();
        return instanceObject;
    }
   
    /**
     * �����豸�����ֵ�
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertMeasuremDic(TParm parm,TConnection connection){
        parm = this.update("onInsertMeasuremDic", parm, connection);
        return parm;
    }
    
    /**
     * �����豸�����ֵ�
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMeasuremDic(TParm parm,TConnection connection){
//    	System.out.println("toolParm:"+parm);
        parm = this.update("onUpdateMeasuremDic", parm, connection);
        return parm;
    }
    
    /**
     * �����豸�����ֵ�
     * @param parm
     * @param connection
     * @return
     */
    public TParm onDeleteMeasuremDic(TParm parm,TConnection connection){
//    	System.out.println("toolParm:"+parm);
        parm = this.update("onDeleteMeasuremDic", parm, connection);
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
    	System.out.println("parm>>>>"+parm);
        parm = this.update("onInsertDevMtnDate", parm, connection);
        return parm;
    }

}
