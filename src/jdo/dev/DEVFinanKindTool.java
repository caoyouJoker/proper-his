package jdo.dev;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:设备财务类别工具类</p>
 *
 * <p>Description:设备财务类别工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: ProperSoft </p>
 * 
 * @author fux
 * @version 1.0
 */
public class DEVFinanKindTool  extends TJDOTool{
	
    /**
     * 构造器
     */
    public DEVFinanKindTool() {
        setModuleName("dev\\DEVFinanKindModule.x");
        onInit();
    }
    /**
     * 实例
     */
    private static DEVFinanKindTool instanceObject;

    /** 
     * 得到实例
     * @return DEVFinanKindTool
     */
    public static DEVFinanKindTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVFinanKindTool();
        return instanceObject;
    }
    /**
     * 写入设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertFinan(TParm parm){
    	parm = update("insertFinan",parm); 
        return parm;
    }
    /** 
     * 写入设备库存信息
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm
     */
    public TParm updateFinan(TParm parm){
    	parm = update("updateFinan",parm);
        return parm;
    }
}
