package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVDisnfectionHelpTool extends TJDOTool{

	public static INVDisnfectionHelpTool instanceObject;
	
	public INVDisnfectionHelpTool() {
        setModuleName("inv\\INVNewBackDisinfectionModule.x");
        onInit();
    }
	
	/**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVDisnfectionHelpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVDisnfectionHelpTool();
        return instanceObject;
    }
	
    /**
     * 插入消毒记录
     *
     *
     */
    public TParm insertDisnfection(TParm parm, TConnection connection) {
        TParm result = this.update("insertDisinfection", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    } 
    
    /**
     * 更改手术包状态为 回收清洗消毒 状态
     *
     *
     */
	public TParm updatePackageStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * 更新器械折损次数
     *
     *
     */
	public TParm updateRecountTime(TParm parm, TConnection connection){
		TParm result = this.update("updateRecountTime", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * 查询器械的批号
     *
     *
     */
	public TParm queryBatchNo(TParm parm, TConnection connection){
		TParm result = this.query("queryBatchNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * 更新主表库存
     *
     *
     */
	public TParm updateStockMQTY(TParm parm, TConnection connection){
		TParm result = this.update("updateStockM", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * 更新细表库存
     *
     *
     */
	public TParm updateStockDQTY(TParm parm, TConnection connection){
		TParm result = this.update("updateStockD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
}
