package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:单病种维护 </p>
 *
 * <p>Description:单病种维护 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-6
 * @version 4.0
 */
public class STASDListTool extends TJDOTool {
    /**
     * 实例
     */
    public static STASDListTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STASDListTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STASDListTool();
        return instanceObject;
    }

    public STASDListTool() {
        setModuleName("sta\\STASDListModule.x");
        onInit();
    }
    /**
     * 查询信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
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
    public TParm insertData(TParm parm){
        TParm result = this.update("insertData",parm);
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
    public TParm updateData(TParm parm){
        TParm result = this.update("updateData",parm);
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
     * @return TParm
     */
    public TParm deleteData(TParm parm){
        TParm result = this.update("deleteData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询单病种列表
     * @return TParm
     */
    public TParm selectSDList(){
        TParm result = this.query("selectDataList");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
