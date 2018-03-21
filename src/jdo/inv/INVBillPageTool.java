package jdo.inv;
/**
*
* <strong>Title : INVBillPageTool<br></strong>
* <strong>Description : </strong>物资库帐<br>
* <strong>Create on : 2013-11-11<br></strong>
* <p>
* <strong>Copyright (C) <br></strong>
* <p>
* @author duzhw<br>
* @version <strong>ProperSoft</strong><br>
* <br>
* <strong>修改历史:</strong><br>
* 修改人		修改日期		修改描述<br>
* -------------------------------------------<br>
* <br>
* <br>
*/

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INVBillPageTool extends TJDOTool {
    private static INVBillPageTool instanceObject;
    public INVBillPageTool() {
        setModuleName("inv\\INVBillPageToolModule.x");
        onInit();
    }
    public static INVBillPageTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new INVBillPageTool();
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
     * 台账明细
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseALL(TParm parm) {
    	TParm result = new TParm();
        result = query("selectDispenseALL", parm);
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
