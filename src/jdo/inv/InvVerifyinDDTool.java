package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class InvVerifyinDDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvVerifyinDDTool instanceObject;

    /**
     * 构造器
     */
    public InvVerifyinDDTool() {
        setModuleName("inv\\INVVerifyinDDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinDTool
     */
    public static InvVerifyinDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvVerifyinDDTool();
        return instanceObject;
    }

    /**
     * 查询验收序号管理细项数据
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryVerifyinDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 新增验收序号管理细项数据
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createVerifyinDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新库存序号管理细项(出库即入库)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateOrginCode(TParm parm, TConnection conn) {
        TParm result = this.update("updateOrginCode", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
    
    /**
     * 更新效期
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateValData(TParm parm, TConnection conn) {
    	TParm result   = new TParm();;
    	for (int i = 0; i < parm.getCount("RFID"); i++) {
    		
    		result = this.update("onUpdateValData", parm.getRow(i), conn);
    		if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result; 
            } 
		}
        
        return result;
    }
    
    /**
     * 更新效期
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateValDataByStockDD(TParm parm, TConnection conn) {
    	TParm result   = new TParm();;
    	for (int i = 0; i < parm.getCount("RFID"); i++) {
    		
    		result = this.update("onUpdateValDataByStockDD", parm.getRow(i), conn);
    		if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result; 
            } 
		}
        
        return result;
    }
    
    
    /**
     * 更新批号
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBatchData(TParm parm, TConnection conn) {
    	TParm result   = new TParm();;
    	for (int i = 0; i < parm.getCount("RFID"); i++) {
    		
    		result = this.update("onUpdateBatchData", parm.getRow(i), conn);
    		if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;   
            } 
		}
        
        return result;
    }
    
    /**
     * 更新批号
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBatchDataByStockDD(TParm parm, TConnection conn) {
    	TParm result   = new TParm();;
    	for (int i = 0; i < parm.getCount("RFID"); i++) {
    		
    		result = this.update("onUpdateBatchDataByStockDD", parm.getRow(i), conn);
    		if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result; 
            } 
		}
        
        return result;
    }

}
