package jdo.inv;


import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title:���ʷ�����ϸ��
 * </p>
 * 
 * <p>
 * Description:���ʷ�����ϸ��
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
	 * ʵ��
	 */
	public static  INVClassifyDetailTool instanceObject;
	/*
	 * �õ�ʵ��
	 * @return SysPatInfoTool
	 */
	public static INVClassifyDetailTool getInstance(){
		if(instanceObject == null)
			instanceObject = new INVClassifyDetailTool();
		return instanceObject;
		
	}
	
	/*
	 * ������
	 */
	public INVClassifyDetailTool(){
		setModuleName("inv\\INVClassifyDetailModule.x");
		onInit();
	}
	
	
	/*
	 * ��ѯ����
	 */
	public TParm onQuery(TParm parm){
		TParm result = this.query("selectdata",parm);
		System.out.println("::::"+result);
		return result;
	}

}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

