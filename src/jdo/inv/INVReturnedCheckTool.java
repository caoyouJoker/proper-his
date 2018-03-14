package jdo.inv;

import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author wangm	2013.11.22
 * @version 1.0
 */
public class INVReturnedCheckTool extends TJDOTool{

	
	public static INVReturnedCheckTool instanceObject;
	
	/**
     * 构造器
     */
    public INVReturnedCheckTool() {
        setModuleName("inv\\INVReturnedCheckModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVReturnedCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVReturnedCheckTool();
        return instanceObject;
    }
    
    /**
     * 新建退货单（保存主、细表）
     * */
    
    public TParm insertReturnedCheck(TParm parm, TConnection connection){
    
    	TParm result = this.update("insertReturnM", parm.getParm("RETURNM").getRow(0), connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
            
    	TParm tp = parm.getParm("RETURND");
    	for(int i=0;i<tp.getCount("INV_CODE");i++){
    		result = this.update("insertReturnD", tp.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
    	
    	TParm tpDD = parm.getParm("RETURNDD");
    	for(int i=0;i<tpDD.getCount("INV_CODE");i++){
    		result = this.update("insertReturnDD", tpDD.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}

    	result = this.update("updateSpcRecordCheck", parm.getParm("RETURNM").getRow(0), connection);
    	if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
    	
    	return result;
    }
	
	
    /**
     * 查询退货单主表
     * */
	public TParm queryReturnedCheckM(TParm parm){
		
		TParm result = this.query("queryReturnM", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	
	/**
     * 查询退货单细表
     * */
	public TParm queryReturnedCheckD(TParm parm){
		
		TParm result = this.query("queryReturnD", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 查询退货单细细表
     * */
	public TParm queryReturnedCheckDD(TParm parm){
		
		TParm result = this.query("queryReturnDD", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	public TParm updateReturnedCheck(TParm parm, TConnection connection){
		TParm result = new TParm();
		for(int i=0;i<parm.getCount("INV_CODE");i++){
    		result = this.update("updateReturnD", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
    	
    	return result;
		
	}
	
	public TParm deleteReturnedCheck(TParm parm, TConnection connection){
		
		TParm result = this.update("delReturnDD", parm, connection);
		if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
		
		result = this.update("delReturnD", parm, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
        result = this.update("delReturnM", parm, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
        
        result = this.update("updateSpcRecordUnCheck", parm, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
            
		return result;
	}
	
	public TParm confirmReturnedCheck(TParm parm, TConnection connection){
		
		
		TParm result = new TParm();
		
		TParm p = new TParm();
		p.setData("RETURNED_NO", parm.getValue("RETURNED_NO", 0));
		p.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", 0));
		p.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", 0));
		result = this.update("updateConfirmStatus", p, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
		
		for(int i=0;i<parm.getCount("INV_CODE");i++){
    		result = this.update("updateReturnDSec", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
		
		for(int i=0;i<parm.getCount("INV_CODE");i++){
    		result = this.update("updateStockMQTY", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
		
		for(int i=0;i<parm.getCount("INV_CODE");i++){
    		result = this.update("updateStockDQTY", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
    	
    	return result;
		
	}
	
	
}
