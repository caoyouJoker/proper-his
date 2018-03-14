package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVSterilizationHelpTool extends TJDOTool {
	public static INVSterilizationHelpTool instanceObject;

	public INVSterilizationHelpTool() {
		setModuleName("inv\\INVNewSterilizationModule.x");
		onInit();
	}

	/**
	 * 得到实例
	 * 
	 * @return IndPurPlanMTool
	 */
	public static INVSterilizationHelpTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INVSterilizationHelpTool();
		return instanceObject;
	}
	  
	
	/**
     * 插入灭菌记录
     *
     *
     */
    public TParm insertSterilization(TParm parm, TConnection connection) {
        TParm result = this.update("insertSterilization", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
	  
    /**
     * 更改手术包状态为 灭菌打包后 状态 （在库）
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
     * 更改回收单中手术包完成状态为  Y
     *
     *
     */
	public TParm updateDisinfectionFinishFlg(TParm parm, TConnection connection){
		TParm result = this.update("updateDisFinishFlg", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * 根据手术包编号查询手术包物资构成
     *
     *
     */
	public TParm queryPackDByPackCode(TParm parm, TConnection connection){
		
		TParm result = this.query("queryPackDByPackCode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * 删除手术包明细数据
     *
     *
     */
	public TParm deletePackageDetailInfo(TParm parm, TConnection connection){
		
		TParm result = this.update("delPackageDInfo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	
	
	/**
     * 插入手术包明细数据
     *
     *
     */
	public TParm insertPackageDetailInfo(TParm parm, TConnection connection){
		
		TParm result = this.update("insertPackageDInfo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	
	
	/**
     * 查询手术包状态
     *
     *
     */
	public TParm queryPackageStatus(TParm parm, TConnection connection){
		
		TParm result = this.query("queryPackageStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	/**
     * 将该手术包在回收单中的先前记录置为完成
     *
     *
     */
	public TParm updatePackageDisStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageDisStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	
	/**
     * 将该手术包在灭菌单中的先前记录置为完成
     *
     *
     */
	public TParm updatePackageSterStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageSterStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	
}
