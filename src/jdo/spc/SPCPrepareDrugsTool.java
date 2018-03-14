package jdo.spc;

import com.dongyang.jdo.TJDOTool;

public class SPCPrepareDrugsTool extends TJDOTool {
	
	/**
	 * 实例
	 */
	private static SPCPrepareDrugsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCPrepareDrugsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCPrepareDrugsTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCPrepareDrugsTool() {
		setModuleName("spc\\SPCPrepareDrugsModule.x");
		onInit();
	}

}
