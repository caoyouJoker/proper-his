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
 * <p>Title: 医疗日报短信发送平台Tool </p>
 *
 * <p>Description: 医疗日报短信发送平台Tool </p>
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
     * 实例
     */
    public static STASMSTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
	public static STASMSTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new STASMSTool();
		}
		return instanceObject;
	}
    
    /**
     * 返回操作人员的详细信息
     * @param id
     * @return
     */
    public TParm getOperatorInfo(String id) {
		String sql = "SELECT * FROM SYS_OPERATOR  WHERE USER_ID = '" + id + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 返回套餐号(取号原则)
     * @return
     */
    public String getNewPackCode() {
        String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO", "PACKAGE_NO");
        return packCode;
    }
    
    /**
     * 保存
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
     * 统计医疗日报信息
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
     * 发送短信
     * 
     * @param smsParm 短信详情
     * @param telParm 发送号码
     * @return
     */
    public void sendSMS(TParm smsParm, TParm telParm) {
        String smsPath = XmlUtil.getSmsPath();
        String fileName = "STA_" + XmlUtil.getNowTime("yyyyMMddHHmmssSSS") + ".xml";
        String path = smsPath + fileName;
        XmlUtil.createXml(smsParm, telParm, path, fileName);
    }
    
}
