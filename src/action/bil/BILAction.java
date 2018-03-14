package action.bil;

import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BILTool;

/**
 * <p>Title:����action </p>
 *
 * <p>Description:����action </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 20090903
 * @version 1.0
 */
public class BILAction extends TAction {
    /**
     * ���������ս�
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionOpb(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILTool.getInstance().onSaveAccountOpb(parm,connection);
        if (result==null||result.getErrCode() < 0) {
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * סԺ�սᱣ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionBIL(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TConnection connection = getConnection();
        result=BILTool.getInstance().onSaveAcctionBIL(parm,connection);
        if (result==null||result.getErrCode() < 0) {
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    /**
     * �վ��ٻر���
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveReceiptReturn(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().insertRcpReturn(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * �˵���˱���
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAuditFee(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onSaveAuditFee(parm, connection);
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
     * �˵���˸������״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm onAuditFeeCheck(TParm parm){
//        System.out.println("�����˵���˸������״̬Action"+parm);
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onAuditFeeCheck(parm, connection);
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
     * סԺ�վݲ�ӡ
     * @param parm TParm
     * @return TParm
     */
    public TParm onIBSReprint(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onIBSReprint(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
    
    /**
     * ���淢Ʊ
     * ======zhangp
     * @param parm
     * @return
     */
    public TParm onSaveTax(TParm parm){
    	TConnection connection = getConnection();
    	String sql = "";
    	TParm result = new TParm();
    	for (int i = 0; i < parm.getCount("SQL"); i++) {
    		sql = parm.getValue("SQL", i);
    		System.out.println(sql);
    		result = new TParm(TJDODBTool.getInstance().update(sql, connection)); 
    		if(result.getErrCode() < 0){
    			connection.rollback();
    			connection.close();
    			return result;
    		}
		}
    	connection.commit();
        connection.close();
    	return result;
    }
    
}
