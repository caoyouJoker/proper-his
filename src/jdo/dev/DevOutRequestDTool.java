package jdo.dev;

import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: 请领细表工具类</p>
 *
 * <p>Description:请领细表工具类）</p>
 *  
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux  
 * @version 1.0
 */  
public class DevOutRequestDTool  extends TJDOTool{
	 /**
     * 构造器
     */
    public DevOutRequestDTool() {
    	//申请主档表
        setModuleName("dev\\DevRequestDModule.x");    
        onInit();
    }

    /**
     * 实例
     */
    private static DevOutRequestDTool instanceObject;

    /**
     * 得到实例  
     * @return MainStockRoomTool
     */  
    public static DevOutRequestDTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutRequestDTool();
        return instanceObject;
    }

    /**  
     * 得到设备请领信息
     * @param parm TParm
     * @return TParm 
     */
    public TParm queryRequestD(TParm parm){  
            parm = query("queryRequestD",parm);    
        return parm;
    }
    /**  
     * 得到设备请领信息  
     * @param parm TParm      
     * @return TParm
     */        
    public TParm queryRequestD(String requestNO){  
    	    TParm parm = new TParm();   
    	    parm.setData("REQUEST_NO",requestNO);  
            parm = query("queryRequestD",parm);    
        return parm;    
    }

 
    /**
     * 更新设备请领明细档  
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */ 
    public TParm updateRequestD(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateRequestD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();   
				return parm;
			}  
		}   
        return parm; 
    }
    /**
     * 写入设备请领明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm 
     */
    public TParm createNewRequestD(TParm parm, TConnection connection){
    	////system.out.println("写入设备请领明细档");
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("createNewRequestD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;
			}  
		}  
        return parm;
    }
    /**
     * 更新设备请领明细档状态
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm updateRequestDDFlg(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {  
			parm = update("updateRequestDFlg",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}  
		}  
        return parm;
    }
    /**
     * 删除请领细表DEV_REQUESTDD
     * @param parm
     * @param connection
     * @return
     */
    public TParm deletedevrequestdd(TParm parm) {  
    	parm = update("deletedevrequestd", parm);
		if (parm.getErrCode() < 0) {
   		    err("ERR:" + parm.getErrCode() + parm.getErrText() +
			             parm.getErrName());
	        return parm;  
		}    
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
    	int count = parm.getCount(names[0]);
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList; 
    }




}
