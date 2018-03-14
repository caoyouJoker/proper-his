package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 临床路径评估项目维护</p>
 *
 * <p>Description:临床路径评估项目维护 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStandmTool
    extends TJDOTool {
    public CLPEVLStandmTool() {
        setModuleName("clp\\CLPEVLStandmModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static CLPEVLStandmTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static CLPEVLStandmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPEVLStandmTool();
        return instanceObject;
    }


    /**
     * 查询信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
//        System.out.println("数据查询方法");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = this.update("insertData", parm, conn);
//        System.out.println("插入信息");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm checkDataExist(TParm parm) {
        TParm result = this.query("checkDataExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateData(TParm parm, TConnection conn) {
        TParm result = this.update("updateData", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteData(TParm parm, TConnection connection) {
        TParm result = this.update("deleteData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 得到最大序列号
     * @return TParm
     */
    public TParm getMaxSEQ() {
        TParm result = this.query("maxSEQQuery");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }


}
