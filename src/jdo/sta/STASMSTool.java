package jdo.sta;

import java.util.HashMap;
import java.util.Map;

import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: ҽ���ձ����ŷ���ƽ̨Tool </p>
 *
 * <p>Description: ҽ���ձ����ŷ���ƽ̨Tool </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangbin 2014.07.15
 * @version 1.0
 */
public class STASMSTool extends TJDOTool {
    public STASMSTool() {
    }

    /**
     * ʵ��
     */
    public static STASMSTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
	public static STASMSTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new STASMSTool();
		}
		return instanceObject;
	}
    
    /**
     * ���ز�����Ա����ϸ��Ϣ
     * @param id
     * @return
     */
    public TParm getOperatorInfo(String id) {
		String sql = "SELECT * FROM SYS_OPERATOR  WHERE USER_ID = '" + id + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * �����ײͺ�(ȡ��ԭ��)
     * @return
     */
    public String getNewPackCode() {
        String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO", "PACKAGE_NO");
        return packCode;
    }
    
    /**
     * ����
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null) {
            return result;
        }
        if (sql.length < 1) {
            return result;
        }
        for (String tempSql : sql) {
            result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }
    
    /**
     * ͳ��ҽ���ձ���Ϣ
     * 
     * @param parm
     * @return
     */
    public TParm onGenerate(String staDate) {
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT T2.DEPT_CHN_DESC,T1.DATA_01,T1.DATA_02,T1.DATA_08,T1.DATA_09,T1.DATA_16,T2.IPD_FIT_FLG ");
    	sql.append(" FROM STA_DAILY_02 T1,SYS_DEPT T2 ");
    	sql.append(" WHERE T1.STA_DATE = '");
    	sql.append(staDate);
    	sql.append("' AND T1.STATION_CODE = T2.DEPT_CODE AND T2.ACTIVE_FLG = 'Y' ");
    	sql.append(" ORDER BY T1.STATION_CODE");
    	
        TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
    
    /**
     * ���Ͷ���
     * 
     * @param smsParm ��������
     * @param telParm ���ͺ���
     * @return
     */
    public void sendSMS(TParm smsParm, TParm telParm) {
        String smsPath = XmlUtil.getSmsPath();
        String fileName = "STA_" + XmlUtil.getNowTime("yyyyMMddHHmmssSSS") + ".xml";
        String path = smsPath + fileName;
        XmlUtil.createXml(smsParm, telParm, path, fileName);
    }
    
}
