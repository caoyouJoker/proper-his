package action.ins;

import jdo.ins.INS_RegiterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: ������Ŀ��������
 * </p>
 * 
 * <p>
 * Description:������Ŀ��������
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
public class INS_RegiterAction extends TAction{
	/**
     * �豸��Ŀ¼��Ϣ���ذ�ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInsNeedRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
//        System.out.println("deleteINSNeedRegisterItem>>>"+parm);
      result =INS_RegiterTool.getInstance().deleteINSNeedRegisterItem(parm,connection);
//      System.out.println("deleteINSNeedRegisterItem>>>"+result);
        // �������
      System.out.println("deleteINSNeedRegisterItem>>>"+parm);
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            System.out.println("deleteINSNeedRegisterItem>>>"+tempParm);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                        System.out.println("�豸��Ŀ¼��Ϣ����>>>"+tempParm);
           
            result = INS_RegiterTool.getInstance().insertINSNeedRegisterItemOne(
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
     * ������Ŀ������Ϣ���ذ�ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            tempParm.setData("CATEGORY", parm.getValue("CATEGORY"));
            tempParm.setData("ITEM_CLASSIFICATION", parm.getValue("ITEM_CLASSIFICATION"));
//                        System.out.println("������Ŀ������Ϣ����>>>"+tempParm);
           
            result = INS_RegiterTool.getInstance().updateINSRegisterItemOne(
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
     * ������Ŀ������Ϣȡ����ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onCancelInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            tempParm.setData("ISVERIFY", parm.getValue("ISVERIFY"));
            tempParm.setData("UPDATE_FLG", parm.getValue("UPDATE_FLG"));
             
            result = INS_RegiterTool.getInstance().onCancelInsRegisterItemOne(
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
     * ������Ŀ������Ϣɾ����ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
             
            result = INS_RegiterTool.getInstance().onDeleteInsRegisterItem(
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
     * ������Ŀ������Ϣ������ť
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
//        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
//            TParm tempParm = parm.getRow(i);
//        System.out.println("onInsertInsRegisterItem_a"+parm);
            result = INS_RegiterTool.getInstance().onInsertInsRegisterItem(
            		parm, connection);
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
//            }
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������Ŀ������Ϣ�޸�
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsRegisterItemUpdate(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // ��������
//        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
//            TParm tempParm = parm.getRow(i);
//        parm.setData("OPT_USER", parm.getValue("OPT_USER"));
//        parm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
             
            result = INS_RegiterTool.getInstance().onInsRegisterItemUpdate(
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
