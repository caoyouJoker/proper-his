package jdo.ind;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: 药库设定
 * </p>
 *
 * <p>
 * Description: 药库设定
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
public class IndOrgTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndOrgTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndOrgTool
     */
    public static IndOrgTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndOrgTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndOrgTool() {
        setModuleName("ind\\INDOrgModule.x");
        onInit();
    }
    /**
     * 获取ordCode
     * add by wukai on 20161206
     * @param parm
     * @param conn
     * @return
     */
    public TParm getOrgCode(TParm parm, TConnection conn){
    	TParm result = this.query("getOrgCode", parm, conn);
    	if(result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
    		return result;
    	}
    	return result;
    }
    
    /**
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBatchFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateBatchFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
