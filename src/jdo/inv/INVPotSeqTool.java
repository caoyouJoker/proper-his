package jdo.inv;
  
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */ 
public class INVPotSeqTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVPotSeqTool instanceObject;

    /**
     * 得到实例
     *
     * @return INDTool
     */
    public static INVPotSeqTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPotSeqTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INVPotSeqTool() {
    	setModuleName("inv\\INVPotSeqModule.x");
        onInit();
    }

    /**
     * 新锅号
     *
     * @param parm
     * @return
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
     * 更新锅号
     *
     * @param parm
     * @return
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
    
    public TParm onQuery(TParm parm){
    	TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm onDelete(TParm parm){
    	TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
