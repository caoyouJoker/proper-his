package jdo.dev;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:设备盘点 </p>
 *
 * <p>Description:设备盘点 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author yuhb
 * @version 1.0
 */
public class DevCheckTool extends TJDOTool{
	/**
	 * 构造器
	 */
	public DevCheckTool() {
		setModuleName("dev\\DevCheckModule.x");
		onInit();
	}
	
	/**
	 * 实例
	 */
	private static DevCheckTool instanceObject;
	
	/**
	 * 得到实例
	 * @return
	 */
	public static DevCheckTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevCheckTool();
		}
		return instanceObject;
	}
	
	/**
	 * 查询序号管理设备库存信息
	 * @param parm
	 * @return
	 */
	public TParm selectSeqDevInf(TParm parm) {
		parm = query("selectSeqDevInf", parm);
		return parm;
	}
	
	/**
	 * 盘点 更新库存主表
	 * @param parm
	 * @return
	 */
	public TParm updateStockM(TParm parm, TConnection connection) {
		parm = update("updateStockM",parm,connection);
		return parm;
	}
	
	/**
	 * 盘点 更新库存细表
	 * @param parm
	 * @return
	 */
	public TParm updateStockD(TParm parm, TConnection connection) {
		parm = update("updateStockD",parm,connection);
		return parm;
	}
	
	/**
	 * 盘点 插入盘点库存表
	 * @param parm
	 * @return  
	 */
	public TParm insertdevQtycheck(TParm parm, TConnection connection) {
		parm = update("insertdevQtycheck",parm,connection);
		return parm;  
	}
	
	
	/**
	 * 盘点 综合更新库存
	 * @param parmList
	 * @param connection
	 * @return
	 */
	public TParm updateStockByCheck(List<TParm> parmList, TConnection connection) {
		TParm parm = new TParm();
		//list放进去 
		for (TParm tParm : parmList) {
			parm = updateStockM(tParm, connection);
			if (parm.getErrCode() < 0) {
				return parm;
			}
			parm = updateStockD(tParm, connection);
			if (parm.getErrCode() < 0) {
				return parm;
			}  
			parm = insertdevQtycheck(tParm, connection);
			if (parm.getErrCode() < 0 ){
				return parm;
			}
			
		}
		return parm;
	}
}
