package jdo.ins;


import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: 取消申报
 * </p>
 * 
 * <p>
 * Description:取消申报
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
public class INSCancelDeclareTool extends TJDOTool{
	   /**
     * 实例
     */
    public static INSCancelDeclareTool instanceObject;
    /**
     * 得到实例
     * @return PanelTypeTool
     */
    public static INSCancelDeclareTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSCancelDeclareTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSCancelDeclareTool() {
        setModuleName("ins\\INSCancelDeclareModule.x");
        onInit();
    }

 
    /**
     * 查询取消申报信息列表
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
    /**
     * 更新INSStatus
     * @param parm TParm
     * @return TParm
     */
    public TParm updateINSStatus(TParm parm) {
        TParm result = new TParm();
        result = update("updateINSStatus", parm);
        //System.out.println(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新UploadFlg
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUploadFlg(TParm parm) {
    	TParm result = new TParm();
    	result = update("updateUploadFlg", parm);
    	//System.out.println(parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
}
