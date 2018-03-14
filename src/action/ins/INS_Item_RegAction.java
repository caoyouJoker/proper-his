package action.ins;

import jdo.ins.INS_Item_RegTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: ��������
 * </p>
 * 
 * <p>
 * Description:��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20140414
 * @version 1.0
 */
public class INS_Item_RegAction extends TAction{

	/**
     * ������Ϣ�ϴ���ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            tempParm.setData("APROVE_TYPE", parm.getValue("APROVE_TYPE"));
           
            result = INS_Item_RegTool.getInstance().upInsItemReg(
                    tempParm, connection);
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ϣ������ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
//        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
//            TParm tempParm = parm.getRow(i);
//        System.out.println("onInsertInsRegisterItem_a"+parm);
            result = INS_Item_RegTool.getInstance().onInsertInsItemReg(
            		parm, connection);
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
//        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ϣ�޸�
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
//        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
//            TParm tempParm = parm.getRow(i);
//        parm.setData("OPT_USER", parm.getValue("OPT_USER"));
//        parm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
             
            result = INS_Item_RegTool.getInstance().onUpdateInsItemReg(
            		parm, connection);
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
//        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ϣ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsItemRegDown(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
//        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
//            TParm tempParm = parm.getRow(i);
//        parm.setData("OPT_USER", parm.getValue("OPT_USER"));
//        parm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
             
            result = INS_Item_RegTool.getInstance().UpdateInsItemRegDown(
            		parm, connection);
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
//        }
        connection.commit();
        connection.close();
        return result;
    }
}
