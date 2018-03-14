/**
 * 
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 替代药</p>
*
* <p>Description:替代药 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800901
* @version 1.0
*/
public class SYSDrugReplacementTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSDrugReplacementTool instanceObject;
    /**
     * 得到实例
     * @return SYSDrugReplacementTool
     */
    public static SYSDrugReplacementTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSDrugReplacementTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSDrugReplacementTool()
    {
        setModuleName("sys\\SYSDrugReplacementModule.x");
        onInit();
    }
    /**
     * 根据 ORDER_CODE,REPLACE_ORDER_CODE 查询数据
     * @param ORDER_CODE String ,REPLACE_ORDER_CODE String
     * @return TParm
     */
    public TParm selectdata(String ORDER_CODE,String REPLACE_ORDER_CODE){
    	TParm parm = new TParm();
    	TParm result=new TParm();
    	if("".equals(REPLACE_ORDER_CODE)){
    		parm.setData("ORDER_CODE",ORDER_CODE);
            result = query("selectbyordercode",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
    	}else if("".equals(ORDER_CODE)){
    		parm.setData("REPLACE_ORDER_CODE",REPLACE_ORDER_CODE);
            result = query("selectbyreplaceordercode",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            
    	}else{
    		parm.setData("ORDER_CODE",ORDER_CODE);
    		parm.setData("REPLACE_ORDER_CODE",REPLACE_ORDER_CODE);
            result = query("selectdata",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
    	}
    	return result;
        
    }
    public TParm selectSysFee(String ORDER_CODE){
    	TParm parm = new TParm();
        parm.setData("ORDER_CODE",ORDER_CODE);
        TParm result = query("selectsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
