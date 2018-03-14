package action.bil;

import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BILTool;

/**
 * <p>Title:账务action </p>
 *
 * <p>Description:账务action </p>
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
     * 保存门诊日结
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionOpb(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
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
     * 住院日结保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionBIL(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
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
     * 收据召回保存
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
     * 账单审核保存
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
     * 账单审核更新审核状态
     * @param parm TParm
     * @return TParm
     */
    public TParm onAuditFeeCheck(TParm parm){
//        System.out.println("进入账单审核更新审核状态Action"+parm);
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
     * 住院收据补印
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
     * 保存发票
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
