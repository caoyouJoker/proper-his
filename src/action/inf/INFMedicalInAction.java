package action.inf;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;

import jdo.bil.BILTool;
import jdo.inf.INFExamTool;
import jdo.inf.INFCaseTool;
import jdo.inf.INFMedicalInTool;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: 医务人员血液体液登记表</p>
 *
 * <p>Description: 医务人员血液体液登记表</p>
 *
 * <p>Copyright: Copyright (c) 20170509</p>
 *
 * <p>Company: javahis </p>
 *
 * @author yanmm 	2017/5/9
 * @version 5.0
 */
public class INFMedicalInAction extends TAction{

    /**
     * 新建
     * @param parm TParm
     * @return TParm
     */
    public TParm onNew(TParm parm) {
    	System.out.println("进入Action中的onNew1");
    	System.out.println("ActionParm:::::::::::" + parm.getValue("EXPOSURE_NO"));
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = INFMedicalInTool.getInstance().onNew(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpate(TParm parm) {
    	System.out.println("进入Action中的onUpDate");
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = INFMedicalInTool.getInstance().onUpDate(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
 
}
