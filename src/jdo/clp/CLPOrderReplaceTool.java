package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:路径医嘱替换
 * </p>
 * 
 * <p>
 * Description: 临床路径医嘱替换操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2014-8-28
 * @version 1.0
 */
public class CLPOrderReplaceTool extends TJDOTool {
	public CLPOrderReplaceTool() {
		setModuleName("clp\\CLPOrderReplaceModule.x");
		onInit();
	}
	 /**
     * 实例
     */
    public static CLPOrderReplaceTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static CLPOrderReplaceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPOrderReplaceTool();
        return instanceObject;
    }
	/**
	 * 查询替换医嘱
	 * @param parm
	 * @return
	 */
	public TParm selectComorderReplace(TParm parm){
		TParm result=query("selectComorderReplace",parm);
		return result;
	}
	/**
	 * 查询临床路径医嘱
	 * @param parm
	 * @return
	 */
	public TParm selectClpPack(TParm parm){
		TParm result=query("selectClpPack",parm);
		return result;
	}
	/**
	 * 更新医嘱数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPack(TParm parm,TConnection conn){
		TParm result=update("updateClpPack",parm,conn);
		return result;
	}
	/**
	 * 更新历史医嘱数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackHistory(TParm parm,TConnection conn){
		TParm result=update("updateClpPackHistory",parm,conn);
		return result;
	}
	
	/**
	 * 查询临床路径项目所有的医嘱
	 * @param parm
	 * @return
	 */
	public TParm selectClpPackSum(TParm parm){
		TParm result=query("selectClpPackSum",parm);
		return result;
	}
	/**
	 * 修改版本号码
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpPackVersion",parm,conn);
		return result;
	}
	/**
	 * 修改临床路径版本号码
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpBscinfoVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpBscinfoVersion",parm,conn);
		return result;
	}
	
	/**
	 * 添加历史临床路径医嘱数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertClpPackHistory(TParm parm,TConnection conn){
		TParm result=update("insertClpPackHistory",parm,conn);
		return result;
	}
	
	/**
	 * 修改历史表版本号码
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackHistoryVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpPackHistoryVersion",parm,conn);
		return result;
	}
}
