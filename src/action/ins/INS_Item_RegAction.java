package action.ins;

import jdo.ins.INS_Item_RegTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: 备案管理
 * </p>
 * 
 * <p>
 * Description:备案管理
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
     * 备案信息上传按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 备案信息新增按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 备案信息修改
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsItemReg(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 备案信息下载
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsItemRegDown(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
