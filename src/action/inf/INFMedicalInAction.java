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
 * <p>Title: ҽ����ԱѪҺ��Һ�ǼǱ�</p>
 *
 * <p>Description: ҽ����ԱѪҺ��Һ�ǼǱ�</p>
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
     * �½�
     * @param parm TParm
     * @return TParm
     */
    public TParm onNew(TParm parm) {
    	System.out.println("����Action�е�onNew1");
    	System.out.println("ActionParm:::::::::::" + parm.getValue("EXPOSURE_NO"));
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
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
     * ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpate(TParm parm) {
    	System.out.println("����Action�е�onUpDate");
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
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
