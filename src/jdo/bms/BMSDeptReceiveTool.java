package jdo.bms;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class BMSDeptReceiveTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static BMSDeptReceiveTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return
     */
    public static BMSDeptReceiveTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSDeptReceiveTool();
        return instanceObject;
    }
    
    /**
     * ������
     */
    public BMSDeptReceiveTool() {
        setModuleName("bms\\BMSDeptReceive.x");
        onInit();
    }
    
    public TParm updateReceive(TParm parm ){
    	TParm result = this.update("updateReceive", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
