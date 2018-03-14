package jdo.onw;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>急诊抢救之口头医嘱套餐</p>
 * 
 * @author wangqing 20170901
 *
 */
public class ONWComPackTool extends TJDOTool {
    /**
     * 实例
     */
    private static ONWComPackTool instanceObject;
    /**
     * 得到实例
     * @return ONWComPackTool
     */
    public static ONWComPackTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWComPackTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ONWComPackTool() {
        setModuleName("onw\\ONWComPackModule.x");
        onInit();
    }
    
    /**
     * 新增套餐
     * @param parm
     * @param conn
     * @return
     */
	public TParm insertOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("insertOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * 删除套餐
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("deleteOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * 修改套餐
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("updateOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * 查询套餐
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwPackMain(TParm parm, TConnection conn){
		TParm result = query("selectOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * 新增医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("insertOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * 删除医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("deleteOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * 修改医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("updateOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * 查询医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwPackOrder(TParm parm, TConnection conn){
		TParm result = query("selectOnwPackOrder", parm, conn);
        return result;
	}

	/**
	 * ONW_ORDER新增医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertOnwOrder(TParm parm, TConnection conn){
		TParm result = update("insertOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDER删除医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwOrder(TParm parm, TConnection conn){
		TParm result = update("deleteOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDER查询医嘱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwOrder(TParm parm, TConnection conn){
		TParm result = query("selectOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDER更新医嘱或者护士签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder1(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder1", parm, conn);
        return result;
	}
	
	/**
	 * 取消护士签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder2(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder2", parm, conn);
        return result;
	}
	
	/**
	 * 医生签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder3(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder3", parm, conn);
        return result;
	}
	
	/**
	 * 取消医生签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder4(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder4", parm, conn);
        return result;
	}
	
	/**
	 * 更新体征数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord", parm, conn);
        return result;
	}
	
	/**
	 * 护士签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord1(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord1", parm, conn);
        return result;
	}
	
	/**
	 * 取消护士签名
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord2(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord2", parm, conn);
        return result;
	}
	
	


}
