package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 灭菌记账明细</p>
 *
 * <p>Description: 灭菌记账明细</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.27
 * @version 1.0
 */
public class InvSuptitemDetailTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvSuptitemDetailTool instanceObject;

    /**
     * 构造器
     */
    public InvSuptitemDetailTool() {
        setModuleName("inv\\INVSuptitemDetailModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvSuptitemDetailTool
     */
    public static InvSuptitemDetailTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSuptitemDetailTool();
        return instanceObject;
    }

    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
       TParm result = this.update("update", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

   /**
     * 删除
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
       TParm result = this.update("delete", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }


}
