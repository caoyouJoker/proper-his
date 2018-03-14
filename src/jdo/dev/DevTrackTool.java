package jdo.dev;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:设备追踪 </p>
 *
 * <p>Description:设备追踪 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author yuhb
 * @version 1.0
 */
public class DevTrackTool  extends TJDOTool{
	/**
	 * 构造器
	 */
	public DevTrackTool() {
		setModuleName("dev\\DevTrackModule.x");
		onInit();
	}
	
	/**
	 * 实例
	 */
	private static DevTrackTool instanceObject;
	
	public static DevTrackTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevTrackTool();
		}
		return instanceObject;
	}
	
	/**
	 * 查询设备追踪主表信息
	 * @param parm
	 * @return
	 */
	public TParm selectDevTrackInfM(TParm parm) {
		parm = query("selectDevTrackInfM",parm); 
		return parm;
	}
	
	/**
	 * 查询设备追踪明细信息表信息
	 * @param parm
	 * @return
	 */
	public TParm selectDevTrackInfD(TParm parm) {
		parm = query("selectDevTrackInfD",parm);
		return parm;
	}
	
	/**
	 * 插入设备追踪记录
	 * @param parm
	 * @return
	 */
	public TParm insertDevTrackInf(TParm parm, TConnection connection) {
		parm = update("insertDevTrackInf",parm,connection);
		if (parm.getErrCode() < 0) {
			connection.close();
			return parm;
		}
        return parm;
	}
	
	/**
	 * 插入设备追踪记录
	 * @param parm
	 * @return
	 */
	public TParm insertDevTrackInf(TParm parm) {
		TParm result = update("insertDevTrackInf",parm);
		//system.out.println("after:" + parm);
		if (result.getErrCode() < 0) {
			return result;
		}
        return result;
	}
	
	/**
	 * 更新设备RFID状态-正常
	 * @param parm
	 * @return
	 */        
	public TParm updateDevRFIDBaseNormal(TParm parm, TConnection connection) {
		TParm result = update("updateDevRFIDBaseNormal",parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		return result;
	}
	
	/**
	 * 更新设备RFID状态-正常
	 * @param parm
	 * @return
	 */
	public TParm updateDevRFIDBaseNormal(TParm parm) {
		TParm result = update("updateDevRFIDBaseNormal",parm);
		return result;
	}
	
	/**
	 * 更新设备RFID状态-在途
	 * @param parm
	 * @return
	 */
	public TParm updateDevRFIDBaseOnpass(TParm parm, TConnection connection) {
		TParm result = update("updateDevRFIDBaseOnpass",parm,connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		return result;
	}
	
	/**
	 * 更新设备RFID状态-在途
	 * @param parm
	 * @return
	 */
	public TParm updateDevRFIDBaseOnpass(TParm parm) {
		TParm result = update("updateDevRFIDBaseOnpass",parm);
		return result;
	}
	
	
	/**
	 * 根据rfid查询dev_code
	 * @param parm
	 * @return
	 */
	public TParm selectDevCodeByRFID(TParm parm) {
		parm = query("selectDevCodeByRFID",parm);
		return parm;
	}
	
	
	/**
     * 将多条parm转换成parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// 返回结果list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// 一个parm里存放多少条数据
    	String strData = null;
    	for (int i = 0; i < names.length; i++) {
			if (null != parm.getData(names[i])) {
				strData = parm.getData(names[i]).toString();
				break;
			}
		}
    	int count = strData.split(",").length;
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }
    
    /**
     * 实时查询设备追踪数据
     * @param parm
     * @return
     */
    public TParm selectRFIDRealtime() {
    	TParm parm = query("selectRFIDRealtime");
    	return parm;
    }
}
















