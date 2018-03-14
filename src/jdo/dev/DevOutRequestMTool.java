package jdo.dev;

import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: 请领主表工具类</p>  
 *
 * <p>Description:请领主表工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0
 */  
public class DevOutRequestMTool  extends TJDOTool{
	 /**
     * 构造器
     */
    public DevOutRequestMTool() {
        setModuleName("dev\\DevRequestMModule.x");  
        onInit();
    }
            
    /**
     * 实例
     */
    private static DevOutRequestMTool instanceObject;

    /**
     * 得到实例
     * @return DevOutRequestMTool
     */
    public static DevOutRequestMTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutRequestMTool();
        return instanceObject;  
    }
    /**  
     * 得到设备请领信息1
     * @param parm TParm  
     * @return TParm
     */ 
    public TParm queryRequestM(TParm parm){ 
        parm = query("queryRequestM",parm);
        return parm;
    }
    /**      
     * 得到设备请领信息2 
     * @param parm TParm
     * @return TParm
     */
    public TParm queryRequestM(String requestNO){  
    	    TParm parm = new TParm();
    	    parm.setData("REQUEST_NO",requestNO);
            parm = query("queryRequestM",parm);     
        return parm;
    }  
    /**  
     * 取得请领号(不是请购号) 
     * @return String
     */
    public String getRequestNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "REQUEST_NO1", "REQUEST_NO1");  
    }
    /**
     * 写入设备请领明细档
     * @param parm TParm 
     * @param connection TConnection 
     * @return TParm
     */
    public TParm createNewRequestM(TParm parm, TConnection connection){
    	////system.out.println("写入设备请领明细档"); 
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {   
			parm = update("createNewRequestM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * 更新设备请领明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRequestM(TParm parm, TConnection connection){
    	List<TParm> parmList = getTParmList(parm);
    	////system.out.println("parmList"+parmList); 
    	for (TParm tParm : parmList) {     
			parm = update("updateRequestM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * 删除请领细表DEV_REQUESTM
     * @param parm
     * @param connection
     * @return
     */
    public TParm deletedevrequestmm(TParm parm) {    
    	parm = update("DELETEDEVREQUESTM", parm);  
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
			////system.out.println("tempParm"+tempParm);  
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }

  
}
