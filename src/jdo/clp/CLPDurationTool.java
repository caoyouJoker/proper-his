package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 临床路径时程维护</p>
 *
 * <p>Description:  临床路径时程维护</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDurationTool extends TJDOTool {

    /**
     * 初始化
     */
    public CLPDurationTool() {
        setModuleName("clp\\CLPDurationModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static CLPDurationTool instanceObject;

    /**
     * 得到实例
     * @return CLPDurationTool
     */
    public static CLPDurationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPDurationTool();
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
     * 删除数据
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm,TConnection conn) {
        TParm result = update("deleteData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 判断数据是否存在
     * @param parm TParm
     * @return TParm
     */
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
     * @return TParm
     */
    public TParm updateData(TParm parm) {
//        System.out.println("-------开始更新");
        TParm result = this.update("updateData", parm);
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
     * @return TParm
     */
    public TParm insertData(TParm parm) {
        TParm result = this.update("insertData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm checkParentId(TParm parm){
        TParm result = this.query("checkIsParentId", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


}
