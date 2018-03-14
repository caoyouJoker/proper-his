package jdo.ins;


import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
 * 
 * <p>
 * Title: 职业医师证书号管理
 * </p>
 * 
 * <p>
 * Description:职业医师证书号管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.2.2
 * @version 1.0
 */
public class INSDRQualifyLogTool extends TJDOTool{
	   /**
     * 实例
     */
    public static INSDRQualifyLogTool instanceObject;
    /**
     * 得到实例
     * @return PanelTypeTool
     */
    public static INSDRQualifyLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSDRQualifyLogTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSDRQualifyLogTool() {
        setModuleName("ins\\INSDRQualifyLogModule.x");
        onInit();
    }
 
    /**
     * 新增职业医师证书号
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        parm.setData("OPT_DATE",StringTool.getString(SystemTool.getInstance().getDate(),
		"yyyy-MM-dd HH:mm:ss"));
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = update("updateOperatordata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }
    /**
     * 更新职业医师证书号
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOperatorData(TParm parm) {
        TParm result = update("updateOperatordata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询用户
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOperator(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectOperatordata", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 查询用户
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdatabyuser(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectdatabyuser", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 查询职业医师证书号管理信息列表
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
  
}
