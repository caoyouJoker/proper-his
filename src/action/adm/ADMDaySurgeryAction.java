package action.adm;

import jdo.adm.ADMDaySurgeryTool;
import jdo.sys.SYSComorderReplaceTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>
 * Title: �ռ��������
 * </p>
 * 
 * <p>
 * Description: �ռ��������������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)   2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhagnlei 2017.03.31
 * @version 5.0
 */
public class ADMDaySurgeryAction extends TAction {
	
	/**
	 * ����޸ķ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		 //����
         result = ADMDaySurgeryTool.getInstance().update(parm,
				connection);

			if (result.getErrCode() < 0) {;
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
				}
		connection.commit();
		System.out.println("commit");
		connection.close();
		return result;
	}
}
