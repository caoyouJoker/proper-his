package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 中间档损伤中毒单档</p>
 *
 * <p>Description: 中间档损伤中毒单档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-2
 * @version 1.0
 */
public class STAExListTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAExListTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAExListTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STAExListTool();
        return instanceObject;
    }

    public STAExListTool() {
        setModuleName("sta\\STAExListModule.x");
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
     * 查询损伤中毒病种列表
     * @return TParm
     */
    public TParm selectEXList(){
        TParm result = this.query("selectDataList");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
