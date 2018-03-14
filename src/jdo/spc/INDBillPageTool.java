package jdo.spc;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INDBillPageTool extends TJDOTool {
    private static INDBillPageTool instanceObject;
    public INDBillPageTool() {
        setModuleName("spc\\INDBillPageToolModule.x");
        onInit();
    }
    public static INDBillPageTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new INDBillPageTool();
        }  
        return instanceObject;
    }
    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSysFeeMed(TParm parm) {
        TParm result = new TParm();
        result = query("selectSysFeeMed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());  
            return result;  
        }
        return result;
    }
    /**
     * 查询帐页入库数据                     
     * @param parm TParm                           
     * @return TParm 
     */
    public TParm selectDispenseIN(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseIN", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 查询帐页出库数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseOUT(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseOUT", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 查询帐页验收数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectVerifyin(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectVerifyin", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 查询帐页退货数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectRegress(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectRegress", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    
    /**
     * 查询帐页暫估数据
     * @param parm TParm  
     * @return TParm  
     */
    public TParm selectOdd(TParm parm) {  
    	TParm result = new TParm();
    	result = query("selectOdd", parm);  
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;  
    }
    
    /**
     * 查询盘点数据  
     * @param parm TParm    
     * @return TParm  
     */
    public TParm selectQtyCheck(TParm parm) {  
    	TParm result = new TParm();
    	result = query("selectQtyCheck", parm);  
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;  
    }
    
    /**
     * 查询指定日期的库存
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStockQty(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectStockQty", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
}
