package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 路径套餐业务类
 * </p>
 * 
 * <p>
 * Description: 路径套餐业务类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20150810
 * @version 1.0
 */
public class CLPPackAgeTool extends TJDOTool {
	public CLPPackAgeTool() {
		setModuleName("clp\\CLPPackAgeModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	public static CLPPackAgeTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IBSTool
	 */
	public static CLPPackAgeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new CLPPackAgeTool();
		return instanceObject;
	}
	/**
	 * 
	* @Title: onQuery
	* @Description: TODO(查询数据)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm queryPackAge(TParm parm){
		TParm result=this.query("queryPackAge",parm);
		return result;
	}
	/**
	 * 
	* @Title: getMaxSeqNo
	* @Description: TODO(查询最大序号)
	* @author Dangzhang
	* @param parm
	* @return
	* @throws
	 */
	public TParm getMaxSeqNo(TParm parm){
		TParm result=this.query("getMaxSeqNo",parm);
		return result;
	}
	/**
	 * 
	* @Title: onInsert
	* @Description: TODO(添加操作)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onInsert(TParm parm){
		TParm result=this.update("onInsert",parm);
		return result;
	}
	/**
	 * 
	* @Title: onUpdate
	* @Description: TODO(修改操作)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onUpdate(TParm parm){
		TParm result=this.update("onUpdate",parm);
		return result;
	}
	/**
	 * 
	* @Title: onDelete
	* @Description: TODO(删除)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onDelete(TParm parm){
		TParm result=this.update("onDelete",parm);
		return result;
	}
}
