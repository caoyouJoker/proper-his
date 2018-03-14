package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:医保卡费用分割主档
 * </p>
 * 
 * <p>
 * Description: 医保卡费用分割主档
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
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdOrderTXTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSOpdOrderTXTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSOpdOrderTXTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSOpdOrderTXTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSOpdOrderTXTool() {
		setModuleName("ins\\INSOpdOrderTXModule.x");
		onInit();
	}

	/**
	 * 插入分割主档
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("insertINSOpdOrder", parm, conn);
		return result;
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("deleteINSOpdOrder", parm, conn);
		return result;
	}

	/**
	 * 查询最大SEQ_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMAXSeqNo(TParm parm) {
		TParm result = query("selectMAXSeqNo", parm);
		return result;
	}
}
