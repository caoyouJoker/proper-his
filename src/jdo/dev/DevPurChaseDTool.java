package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
 
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
public class DevPurChaseDTool  extends TJDOTool{

    /**
     * 构造器
     */
    public DevPurChaseDTool() { 
        setModuleName("dev\\DevPurChaseModule.x");
        onInit();
    } 

    /**
     * 实例
     */
    private static DevPurChaseDTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DevPurChaseDTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevPurChaseDTool();
        return instanceObject;
    }

    /**
     * 写入设备请购细表
     * @param parm TParm
     * @return TParm 
     */
    public TParm insertDevPurChaseD(TParm parm,String type){
    	//system.out.println("写入设备请购细表");; 
    	if ("INSERT".equals(type)){   
        parm = update("insertD",parm);
    	}
    	else{
        parm = update("updateD",parm);	
    	} 
        return parm;
    }
 
  
    /** 
     * 删除设备请购细表
     * @param devCode String
     * @return TParm
     */
    public TParm deleteDevBase(String requestNo){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requestNo); 
        parm = update("deleteD",parm);
        return parm;
    }
}
