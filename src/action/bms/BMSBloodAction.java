package action.bms;

import jdo.bms.BMSBloodTool;
import jdo.bms.BMSFeeTool;
import jdo.bms.BMSTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
/**
 * <p>
 * Title: ѪҺ��Ϣ
 * </p>
 *
 * <p>
 * Description: ѪҺ��Ϣ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSBloodAction
    extends TAction {
    public BMSBloodAction() {
    }

    public TParm onQuery(TParm parm) {
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onBMSBloodInInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ɾ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onBMSBloodInDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ,���²���Ѫ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdatePatCheckInfo(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdatePatCheckInfo(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���±�Ѫ��������Ѫ��Ϣ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodCross(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodCross(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * ���±�Ѫ��������Ѫ�����Ϣ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodCrossRecheck(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSBloodTool.getInstance().onUpdateBloodCrossRecheck(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���±�Ѫ��ѪƷ������Ϣ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBloodOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodOut(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback() ;
            conn.close();
            return result;
        }
        result =BMSFeeTool.getInstance().getIbsData(parm, conn) ;  
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback() ;
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ����ѪƷ��������ѪƷ���
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveBldSubcat(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onSaveBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 
     * �������� ����ѪƷ���.
     * @param parm
     * @return
     */
    public TParm onUpdateBldSubcat(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;    	
    }
   

    /**
     * ɾ��ѪƷ
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteBldSubcat(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onDeleteBldSubcat(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    /*
     * ��¼��Ѫ��Ϣ ��ʼ
     * */
    public TParm onTranBlood(TParm parm){
    	TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onTranBlood(parm, conn);
//        System.out.println("yangjj:"+result+"");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = BMSTool.getInstance().onInsertOrderForBL(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /*
     * ��¼��Ѫ��Ϣ ����
     * */
    public TParm onTranBlood2(TParm parm){
    	TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onTranBlood2(parm, conn);
//        System.out.println("yangjj:"+result+"");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = BMSTool.getInstance().onUpdateOrderForBL(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ������Ѫ���棬ɾ��������Ѫ��Ϣʱ��ͬʱ������Ѫ�����Ϣ
     * @author wangqing 20180108
     * @param parm
     * @return
     */
    public TParm onUpdateBmsBlood(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSTool.getInstance().onUpdateBloodCross(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        String sql = "";
        for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
        	sql = "UPDATE BMS_BLOOD "
        			+ "SET RECHECK_USER ='', RECHECK_TIME='' "
        			+ "WHERE BLOOD_NO='"+parm.getValue("BLOOD_NO", i)+"' ";
        	result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        	if (result.getErrCode() < 0) {
        		err("ERR:" + result.getErrCode() + result.getErrText()
        		+ result.getErrName());
        		conn.close();
        		return result;
        	}
        }      
        conn.commit();
        conn.close();
        return result;
    }


}