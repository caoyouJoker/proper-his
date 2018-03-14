package jdo.dev;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:设备月结</p>
 *
 * <p>Description:设备月结 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:bluecore </p>  
 *
 * @author fux
 * @version 1.0
 */
public class DevMMTool extends TJDOTool{
	/**
	 * 构造器
	 */
	public DevMMTool() {
		setModuleName("dev\\DevMMModule.x");
		onInit();
	}
	
	/**
	 * 实例
	 */
	private static DevMMTool instanceObject;
	
	/**
	 * 得到实例
	 * @return
	 */
	public static DevMMTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevMMTool();
		}
		return instanceObject;
	} 
	
	
	/**
	 * 查询相应月份月结数据
	 * @param parm
	 * @return  
	 */
	public TParm queryDevMMStock(TParm parm) {
		parm = query("queryDevMMStock", parm);
		return parm;
	}
	
	 
	/**
	 * 查询相应月份是否有月结数据
	 * @param parm
	 * @return 
	 */
	public TParm queryDevMMStockCount(TParm parm) {
		parm = query("queryDevMMStockCount", parm);
		return parm;
	}
	
	/**
	 * 查询月结入库数据
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthRKData(TParm parm) {
		parm = query("queryDevMonthRKData", parm);
		return parm;
	}
	
	/**
	 * 查询月结出库调入数据 
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthCKTRData(TParm parm) {
		parm = query("queryDevMonthCKTRData", parm);
		return parm;
	}
	

	
	/**
	 * 查询月结出库调出数据
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthCKTCData(TParm parm) {
		parm = query("queryDevMonthCKTCData", parm);
		return parm;
	}
	
	
	
	/**
	 * 查询月结退货数据
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthTHData(TParm parm) {
		parm = query("queryDevMonthTHData", parm);
		return parm;
	}
	
	
	
	/**
	 * 查询损耗初始化
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthSHData(TParm parm) {
		parm = query("queryDevMonthSHData", parm);
		return parm;
	}
	
	
	
	/**
	 * 查询盘点初始化
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthPDData(TParm parm) {
		parm = query("queryDevMonthPDData", parm);
		return parm;
	}
	
 
	
	/**
	 * 更新DEV_MMSTOCK
	 * @param parm
	 * @return
	 */
	public TParm updateMMStock(TParm parm) {
		parm = update("updateMMStock",parm);
		return parm;
	}
	
	
	/**
	 * 插入DEV_MMSTOCK
	 * @param parm
	 * @return  
	 */ 
	public TParm insertMMStock(TParm parm) {
		System.out.println("插入DEV_MMSTOCK");
		parm = update("insertMMStock",parm);
		return parm;   
	}
	 
	
	/**
	 * 删除DEV_MMSTOCK 
	 * @param parm
	 * @return
	 */
	public TParm deleteMMStock(TParm parm) {
		parm = update("deleteMMStock",parm);
		return parm;
	}
	
	
	
}
