package action.ins;

import jdo.ins.INS_RegiterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: 诊疗项目备案管理
 * </p>
 * 
 * <p>
 * Description:诊疗项目备案管理
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
     * 需备案目录信息下载按钮
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
        // 添加数据
      System.out.println("deleteINSNeedRegisterItem>>>"+parm);
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            System.out.println("deleteINSNeedRegisterItem>>>"+tempParm);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                        System.out.println("需备案目录信息下载>>>"+tempParm);
           
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
     * 诊疗项目备案信息下载按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
        for (int i = 0; i < parm.getCount("NHI_CODE"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            tempParm.setData("CATEGORY", parm.getValue("CATEGORY"));
            tempParm.setData("ITEM_CLASSIFICATION", parm.getValue("ITEM_CLASSIFICATION"));
//                        System.out.println("诊疗项目备案信息下载>>>"+tempParm);
           
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
     * 诊疗项目备案信息取消按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onCancelInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 诊疗项目备案信息删除按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 诊疗项目备案信息新增按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertInsRegisterItem(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
     * 诊疗项目备案信息修改
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsRegisterItemUpdate(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
       // 更新数据
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
