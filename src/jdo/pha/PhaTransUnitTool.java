package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 药品转换率档tool
*
* <p>Description: 药品转换率档tool</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20081005
* @version 1.0
*/
public class PhaTransUnitTool extends TJDOTool{
	/**
     * 实例
     */
    public static PhaTransUnitTool instanceObject;
    /**
     * 得到实例
     * @return PhaTransUnitTool
     */
    public static PhaTransUnitTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaTransUnitTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaTransUnitTool() {
        setModuleName("pha\\PhaTransUnitModule.x");

        onInit();
    }
    /**
     * 抓取药品开药单位、发药单位、库存单位转换算率
     * @param parm
     * @return TParm 
     */
    public TParm queryForAmount(TParm parm){
    	TParm result = new TParm();
        result = query("queryForAmount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
