package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 评估细分类Tool</p>
 *
 * <p>Description: 评估细分类Tool</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLCat3Tool extends TJDOTool{
    public static CLPEVLCat3Tool instanceObject;
    public CLPEVLCat3Tool() {
    setModuleName("clp\\CLPEVLCat3Module.x");
    onInit();
}
public static CLPEVLCat3Tool getInstance() {
    if (instanceObject == null) {
        instanceObject = new CLPEVLCat3Tool();
    }
    return instanceObject;
    }
    /**
 * 查询
 * @param parm TParm
 * @return TParm
 */
public TParm selectData(TParm parm) {
    TParm result = new TParm();
    result = query("selectData", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}

}
