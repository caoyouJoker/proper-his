package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.inv.INVReturnedCheckTool;


/**
 * 
 * <p>
 * Title:�˻��˶�
 * </p>
 * 
 * <p>
 * Description:�˻��˶�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author fux 2014.07.19
 * @version 1.0
 */
public class INVReturnedCheckAction extends TAction
{
	/**
	 * ����
	 */
  public TParm onInsert(TParm parm)
  {
    TConnection conn = getConnection();
    TParm result = new TParm();
    result = INVReturnedCheckTool.getInstance().insertReturnedCheck(parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      conn.close();
      return result;
    }
    conn.commit();
    conn.close();
    return result;
  }

	/**
	 * ����
	 */
  public TParm onSave(TParm parm)
  {
    TConnection conn = getConnection();
    TParm result = new TParm();
    result = INVReturnedCheckTool.getInstance().updateReturnedCheck(parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      conn.close();
      return result;
    }
    conn.commit();
    conn.close();
    return result;
  }

	/**
	 * ɾ��
	 */
  public TParm onDelete(TParm parm)
  {
    TConnection conn = getConnection();
    TParm result = new TParm();
    result = INVReturnedCheckTool.getInstance().deleteReturnedCheck(parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      conn.close();
      return result;
    }
    conn.commit();
    conn.close();
    return result;  
  }
  
	/**
	 * �˶�
	 */
  public TParm onConfirm(TParm parm)
  {
    TConnection conn = getConnection();
    TParm result = new TParm();
    result = INVReturnedCheckTool.getInstance().confirmReturnedCheck(parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      conn.close();
      return result;
    }
    conn.commit();
    conn.close();
    return result;
  }
}