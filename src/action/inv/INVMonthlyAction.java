package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.inv.INVMonthlyTool;

/**
 * <p>
 * Copyright: Copyright (c) 2009  
 * </p>
 *    ·´±àÒë£¬Ô´Âë¶ªÊ§
 * <p> 
 * Company: JavaHis 
 * </p>
 * 
 * @author zhangh 2013.12.9
 * @version 1.0
 */
public class INVMonthlyAction extends TAction
{
  public TParm onSave(TParm parm)                  
  {  
    TConnection conn = getConnection(); 
    TParm result = new TParm();  
    result = INVMonthlyTool.getInstance().onSaveMonthlyInfo(parm, conn);
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