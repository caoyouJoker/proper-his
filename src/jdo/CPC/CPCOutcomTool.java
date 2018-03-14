package jdo.CPC;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

import jdo.adm.ADMInpTool;

public class CPCOutcomTool extends TJDOTool {
	
	public static CPCOutcomTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static CPCOutcomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CPCOutcomTool();
        return instanceObject;
    }
    public CPCOutcomTool() {
        setModuleName("CPC\\CPCOutcomModule.x");
        onInit();
    }
    
	/**
	 * 
	 *保存方法
	 */
	public TParm update(TParm parm){
		 TParm result = this.update("update", parm);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
	        return result;
	}
}
