package action.inv;

import jdo.inv.INV_AssignSysParmTool;
import jdo.inv.InvBaseTool;
import jdo.inv.InvStockDDTool;
import jdo.inv.InvTransUnitTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: 物资字典</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVBaseAction extends TAction{
    public INVBaseAction() {
    }

    /**
     * 新增物资
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm(); 
        // 新增物资字典  
        result = InvBaseTool.getInstance().onInsert(parm.getParm("INV_BASE"), conn);
        if (result.getErrCode() < 0) {   
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }      
        // 新增物资单位换算   
        result = InvTransUnitTool.getInstance().onInsert(parm.getParm(
            "INV_TRANSUNIT"), conn);
         
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新物资
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = InvBaseTool.getInstance().onUpdate(parm.getParm("INV_BASE"), conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result; 
        }
        // 更新物资单位换算
        result = InvTransUnitTool.getInstance().onUpdate(parm.getParm("INV_TRANSUNIT"), conn);
        
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除物资
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = InvBaseTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        // 更新物资单位换算
        result = InvTransUnitTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    
    /**
     * 更新物资SYSPARM基本档
     * @param parm TParm
     * @return TParm
     */
    public TParm onSysParmUpdate(TParm parm) {  
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INV_AssignSysParmTool.getInstance().onUpdateSysParm(parm, conn);
        System.out.println("result"+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName()); 
            conn.close();
            return result;   
        }
        conn.commit();
        conn.close();
        return result;
    }
    public TParm onUpdateCab(TParm parm)
    {
      TConnection conn = getConnection();
      TParm result = new TParm();
      for (int i = 0; i < parm.getCount("RFID"); i++) {
        TParm mypParm = new TParm();
        mypParm.setData("RFID", parm.getData("RFID", i));
        mypParm.setData("CABINET_ID", parm.getData("CABINET_ID", i));
        result = InvStockDDTool.getInstance().onUpdateCab(mypParm, conn);
      }

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
