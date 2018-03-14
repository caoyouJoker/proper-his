package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 评估种类数据库操作类</p>
 *
 * <p>Description: 评估种类数据库操作类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStanddTool extends TJDOTool {
    public CLPEVLStanddTool() {
        setModuleName("clp\\CLPEVLStanddModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static CLPEVLStanddTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static CLPEVLStanddTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPEVLStanddTool();
        return instanceObject;
    }

    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteData(TParm parm, TConnection connection) {
//        System.out.println("删除方法执行--------------------------");
        TParm result = this.update("deleteData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm insertData(TParm parm, TConnection connection) {
//        System.out.println("插入方法执行--------------------------");
        TParm result = this.update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm selectData(TParm parm) {
//        System.out.println("查询方法执行--------------------------");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


}
