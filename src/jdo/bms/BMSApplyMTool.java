package jdo.bms;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSApplyMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BMSApplyMTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return
     */
    public static BMSApplyMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSApplyMTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BMSApplyMTool() {
        setModuleName("bms\\BMSApplyMModule.x");
        onInit();
    }

    /**
     * ��Ѫ������
     *
     * @param parm
     * @return
     */
    public TParm onApplyInsert(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyInsert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ѫ������
     *
     * @param parm
     * @return
     */
    public TParm onApplyUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdate", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ(��������)
     *
     * @param parm
     * @return
     */
    public TParm onUpdatePatCheckInfo(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdatePatCheckInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ����Ѫ��ϸ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onApplyDelete(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyDelete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onApplyQuery(TParm parm) {
        TParm result = this.query("ApplyQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ѫ���뵥��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onApplyNoQuery(TParm parm) {
        TParm result = this.query("ApplyNoQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
  
	/**
     * ���ݱ걾Ѫ�ܺ�application_codeУ��ɼ�ʱ���뵱ǰʱ������Ƿ�����С��72Сʱ���ܳ����Ҫ��
     * trueΪ������⣬falseΪ���������
     * @param application_code String
     * @return boolean
     */
    public boolean check72Hour(String application_code){
    	boolean b = false;
    	
    	String sql = "";
    	sql += " SELECT " 
    		+ " O.NS_EXEC_DATE_REAL AS DATE1 " 
    		+ " FROM ODI_DSPND O , ODI_ORDER B " 
    		+ " WHERE "
    		+ " B.ORDER_SEQ = O.ORDER_SEQ " 
    		+ " AND B.ORDER_NO = O.ORDER_NO "
    		+ " AND B.CASE_NO = O.CASE_NO "
    		+ " AND B.SETMAIN_FLG = 'Y' "
    		+ " AND B.MED_APPLY_NO = '"+application_code+"'";
    	System.out.println(sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	if(result.getCount() <= 0 || result.getData("DATE1", 0) == null){
    		return false;
    	}
    	
    	Timestamp execDate = (Timestamp)result.getData("DATE1", 0);
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	
    	Long l = (now.getTime() - execDate.getTime())/1000/60/60;
    	if(l < 72){
    		b = true;
    	}
    	
    	return b;
    }

}
