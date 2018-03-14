package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 查找病人信息</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPEvaluateTool extends TJDOTool {
    private static CLPEvaluateTool instanceObject;
    public CLPEvaluateTool() {
        setModuleName("clp\\CLPEvaluateModule.x");
        onInit();
    }

    public static CLPEvaluateTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new CLPEvaluateTool();
        }
        return instanceObject;
    }

    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectPatientInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selectPatientInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectManagemWithPatientInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selectManagemWithPatientInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
