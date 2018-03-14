package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVRepackHelpTool extends TJDOTool {
	public static INVRepackHelpTool instanceObject;

	public INVRepackHelpTool() {
		setModuleName("inv\\INVNewRepackModule.x");
		onInit(); 
	}

	/**
	 * 得到实例
	 * 
	 * @return IndPurPlanMTool
	 */
	public static INVRepackHelpTool getInstance() { 
		if (instanceObject == null)
			instanceObject = new INVRepackHelpTool();
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
     * 更改手术包状态为 打包后 状态 
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
     * 查询手术包状态  已用勿删
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
     * 查询手术包中物资属性为 “一次性且高值” 的      已用勿删
     *
     *
     */
	public TParm queryMaterialAttr(TParm parm, TConnection connection){
		
		TParm result = this.query("queryHighOnce", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * 更新stockdd表中高值耗材的消耗属性      已用勿删
     *
     *
     */
	public TParm updateStockDDWastFlg(TParm parm, TConnection connection){
		
		TParm result = this.update("updateStockDDWastFlg", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * 将该手术包在打包单中的先前记录置为完成      已用勿删
     *
     *
     */
	public TParm updateRepackStatus(TParm parm, TConnection connection){
		
		TParm result = this.update("updateRepackStatus", parm, connection);
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
	
	/**
     * 查询手术包中序管且一次性的物资
     *
     *
     */
	public TParm queryHighOnceMaterial(TParm parm){
		TParm result = this.query("queryHOMaterial", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * 插入打包单记录
     *
     *
     */
    public TParm insertRepack(TParm parm, TConnection connection) {
        TParm result = this.update("insertRepack", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
	
    
    /**
     * 根据RFID查询序管且一次性物资
     *
     *
     */
    public TParm queryMaterialByRFID(TParm parm){
    	TParm result = this.query("queryMaterialByRFID", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 更新packstockm表条码
     *
     *
     */
    public TParm updatePackStockMBarcode(TParm parm, TConnection connection) {
        TParm result = this.update("updatePackstockMBarcode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 更新packstockd表条码
     *
     *
     */
    public TParm updatePackStockDBarcode(TParm parm, TConnection connection) {
        TParm result = this.update("updatePackstockDBarcode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 根据条码查询packstockm记录
     *
     *
     */
    public TParm queryPackageInfoByBarcode(TParm parm){
    	TParm result = this.query("queryPackageInfoByBarcode", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * 根据条码查询packstockd记录
     *
     * 
     */
    public TParm queryPackageDInfoByBarcode(TParm parm){
    	TParm result = this.query("queryPackageDInfoByBarcode", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
}
