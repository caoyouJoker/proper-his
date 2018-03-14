package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
 
/**
 * <p>Title: 设备请购主表</p> 
 *
 * <p>Description:设备请购主表 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:bulecore </p>
 *
 * @author  fux
 * @version 1.0 
 */
public class DevPurChaseMTool  extends TJDOTool{

    /**
     * 构造器
     */
    public DevPurChaseMTool() { 
        setModuleName("dev\\DevPurChaseModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DevPurChaseMTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DevPurChaseMTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevPurChaseMTool();
        return instanceObject;
    }

    /**
     * 写入设备请购主表
     * @param parm TParm
     * @return TParm  
     */
    public TParm insertDevPurChaseM(TParm parm,String type){
    	//system.out.println("写入设备请购主表");
    	if ("INSERT".equals(type)){ 
        //system.out.println("INSERT"); 
        parm = update("insertM",parm); 
    	} 
    	else{
        parm = update("updateM",parm);	
    	}
        return parm;
    }
    
 
   
    /** 
     * 删除设备请购主表
     * @param devCode String
     * @return TParm
     */
    public TParm deleteDevBase(String requestNo){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requestNo); 
        parm = update("deleteM",parm); 
        return parm;
    }
}
