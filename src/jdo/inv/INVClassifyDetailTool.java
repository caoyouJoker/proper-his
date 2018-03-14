package jdo.inv;


import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title:物资分类明细表
 * </p>
 * 
 * <p>
 * Description:物资分类明细表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author donglt 2016-03-11
 * @version 1.0
 */

public class INVClassifyDetailTool  extends  TJDOTool {
	
	/*
	 * 实例
	 */
	public static  INVClassifyDetailTool instanceObject;
	/*
	 * 得到实例
	 * @return SysPatInfoTool
	 */
	public static INVClassifyDetailTool getInstance(){
		if(instanceObject == null)
			instanceObject = new INVClassifyDetailTool();
		return instanceObject;
		
	}
	
	/*
	 * 构造器
	 */
	public INVClassifyDetailTool(){
		setModuleName("inv\\INVClassifyDetailModule.x");
		onInit();
	}
	
	
	/*
	 * 查询数据
	 */
	public TParm onQuery(TParm parm){
		TParm result = this.query("selectdata",parm);
		System.out.println("::::"+result);
		return result;
	}

}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

