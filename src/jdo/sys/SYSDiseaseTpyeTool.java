package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:传染病类别Tool
 * </p>
 * 
 * <p>
 * Description:传染病类别Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSDiseaseTpyeTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSDiseaseTpyeTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PositionTool
	 */
	public static SYSDiseaseTpyeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSDiseaseTpyeTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSDiseaseTpyeTool() {
		setModuleName("sys\\SYSDiseaseTypeModule.x");
		onInit();
	}

	/**
	 * 得到传染病信息
	 * 
	 * @param diseasetpye_code
	 *            传染病类别代码
	 * @return
	 */
	public TParm getDiseaseTpye(String diseasetpye_code) {
		TParm parm = new TParm();
		parm.setData("DISEASETYPE_CODE", diseasetpye_code);
		TParm result = this.query("queryDiseaseTpye", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
