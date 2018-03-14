package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 药品编码转换
 * </p>
 *
 * <p>
 * Description: 药品编码转换
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author fuwj 2012.12.19
 * @version 1.0
 */
public class SPCSysFeeTool extends TJDOTool {
	
	 /**
     * 实例
     */
    public static SPCSysFeeTool instanceObject;				

    /**
     * 得到实例							
     *				
     * @return IndStockMTool					
     */
    public static SPCSysFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCSysFeeTool();
        return instanceObject;
    }
  
    /**
     * 构造器
     */
    public SPCSysFeeTool() {
    	setModuleName("spc\\SPCSysFeeModule.x");
        onInit();
    }
    
    /**
     * 查询药品信息
     * @param tparm    
     * @return
     */
    public TParm queryInfo(TParm tparm) {
    	TParm result = query("queryInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * 更新SYS_FEE的药品编码
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateSysFee(TParm parm,TConnection conn) {
    	TParm result = update("updateSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
   /**
    * 更新PHA_BASE的药品编码 
    * @param parm
    * @param conn
    * @return
    */
    public TParm updatePhaBase(TParm parm,TConnection conn) {
    	TParm result = update("updatePhaBase",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * 更新PHA_TRANSUNIT的药品编码
     * @param parm
     * @param conn
     * @return
     */
    public TParm updatePhaTransunit(TParm parm,TConnection conn) {
    	TParm result = update("updatePhaTransunit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * 添加SYS_FEE_SPC
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertSPCSysFee(TParm parm,TConnection conn) {
    	TParm result = update("insertSPCSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * 查询物联网药品信息
     * @param tparm    
     * @return
     */
    public TParm querySpcSysFee(TParm tparm) {
    	TParm result = query("querySpcSysFee", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    
    /**
     * 删除SYS_FEE未被更新数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteSysFee(TParm parm,TConnection conn) {
    	TParm result = update("deleteSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * 删除PHA_BASE未被更新数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm deletePhaBase(TParm parm,TConnection conn) {
    	TParm result = update("deletePhaBase",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * 删除PHA_TRANSUNIT未被更新数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm deletePhaTransunit(TParm parm,TConnection conn) {
    	TParm result = update("deletePhaTransunit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    
    /**
     * 根据his的药品编码查询物联网的药品编码
     * @param tparm    
     * @return
     */
    public TParm querySysFeeSpc(TParm tparm) {
    	TParm result = query("querySysFeeSpc", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    
    /**
     * 更新updateIndStock
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateIndStock(TParm parm,TConnection conn) {
    	TParm result = update("updateIndStock",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * 更新updateIndStock
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateIndStockM(TParm parm,TConnection conn) {
    	TParm result = update("updateIndStockM",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    
    /**
     * 根据his的药品编码查询物联网的药品编码
     * @param tparm    
     * @return
     */
    public TParm queryHisOrderCode(TParm tparm) {
    	TParm result = query("queryHisOrderCode", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * 更新updateIndAgent
     * @param parm
     * @param conn
     * @return 
     */
    public TParm updateIndAgent(TParm parm,TConnection conn) {
    	TParm result = update("updateIndAgent",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result; 
    }
    
    /**
     * 更新updateSysFeeHistory
     * @param parm
     * @param conn
     * @return   
     */
    public TParm updateSysFeeHistory(TParm parm,TConnection conn) {
    	TParm result = update("updateSysFeeHistory",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * 添加SysFeeSpc表数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertSysFeeSpc(TParm parm) {
    	TParm result = update("insertSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * 删除SysFeeSpc表数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteSysFeeSpc(TParm parm) {
    	TParm result = update("deleteSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * 更新SysFeeSpc表数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateSysFeeSpc(TParm parm) {
    	TParm result = update("updateSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    
    /**
     * 更新SysFeeSpc表数据
     * @param parm
     * @param conn
     * @return
     */
    public TParm indStockInsert(TParm parm,TConnection conn) {
    	TParm result = update("indStockInsert",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    
    public TParm updateQty(TParm parm,TConnection conn) {
    	TParm result = update("updateQty",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm updateTag(TParm parm,TConnection conn) {
    	TParm result = update("updateTag",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm searchTag(TParm parm) {
    	TParm result = query("searchTag",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm updatePackUnit(TParm parm,TConnection conn) {
    	TParm result = update("updatePackUnit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm insertIndAgent(TParm parm) {
    	TParm result = update("insertIndAgent",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;   
    }
	
}
