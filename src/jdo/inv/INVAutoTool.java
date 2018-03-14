package jdo.inv;

import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: 物资自动任务</p>
 *
 * <p>Description:物资自动任务 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author yuhb
 * @version 1.0
 */
public class INVAutoTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static INVAutoTool instanceObject;
	
	/**
	 * 构造器
	 */
	public INVAutoTool() {
		super.setModuleName("inv\\INVAutoModule.x");
		super.onInit();
	}
	
	/**
	 * 得到实例
	 * @return
	 */
	public static INVAutoTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new INVAutoTool();
		}
		return instanceObject;
	}
	
	
}
