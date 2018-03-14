package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class InvKindQueryTool extends TJDOTool{
	 /**
     * ʵ��
     */
    public static InvKindQueryTool instanceObject;
    
    public InvKindQueryTool() {
        setModuleName("inv\\INVKindQueryModule.x");
        onInit();
    }
	/**
     * �õ�ʵ��
     *
     * @return InvSupTypeTool
     */
    public static InvKindQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvKindQueryTool();
        return instanceObject;
    }
    /**
     * ����
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
     * ����
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
    /**
     * �õ����ı�� +1
     *
     * @return
     */
    public TParm onfindMaxSeq() {
        TParm result = this.query("findMaxSeq");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm){
    	TParm result = this.update("delete",parm);
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
		return result;
    	
    }
}
