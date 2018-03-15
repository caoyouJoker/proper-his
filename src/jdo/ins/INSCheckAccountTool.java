package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 医保门诊对账
 * </p>
 * 
 * <p>
 * Description: 结算保存操作 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft 
 * </p>
 * 
 * @author pangben 2012-1-11
 * @version 1.0
 */
public class INSCheckAccountTool  extends TJDOTool{
	/**
	 * 实例
	 */
	public static INSCheckAccountTool instanceObject;
	/**
	 * 得到实例
	 * 
	 * @return INSTJTool
	 */
	public static INSCheckAccountTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSCheckAccountTool();
		return instanceObject;
	}
	/**
	 * 构造器
	 */
	public INSCheckAccountTool() {
		setModuleName("ins\\INSCheckAccountModule.x");
		onInit();
	}
	/**
	 * 添加对账结算数据
	 * @param parm
	 * @return
	 */
	public TParm insertInsCheckAccount(TParm parm){
		TParm result = update("insertInsCheckAccount", parm);
		return result;
	}
	/**
	 * 查询是否存在数据 根据日期查询
	 * @param parm
	 * @return
	 */
	public TParm queryInsCheckAccount(TParm parm){
		TParm result = query("queryInsCheckAccount", parm);
		return result;
	}
}
