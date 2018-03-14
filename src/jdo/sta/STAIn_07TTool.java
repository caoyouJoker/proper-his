package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: STA_IN_07医院手术情况台帐</p>
 *
 * <p>Description: STA_IN_07医院手术情况台帐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class STAIn_07TTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_07TTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAIn_07TTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_07TTool();
        return instanceObject;
    }

    public STAIn_07TTool() {
        setModuleName("sta\\STAIn_07TModule.x");
        onInit();
    }
    /**
     * 查询本期数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBQ(TParm parm){
        TParm result = this.query("selectBQ",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询趋势数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectQS(TParm parm){
        TParm result = this.query("selectQS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
