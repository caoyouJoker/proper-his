package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.inv.INVTool;
import jdo.inv.InvStockDDTool;
import jdo.inv.InvVerifyinDDTool;

import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *  
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INVVerifyinAction
    extends TAction {
    public INVVerifyinAction() {
    }

    /**
     * 查询验收入库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = INVTool.getInstance().onQueryVerifyinM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增验收入库单
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onCreateVerifyin(parm, conn);
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
     * 赋码
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    
    public TParm onUpdateBarCode(TParm parm){
    	  TConnection conn = getConnection();
          TParm result = new TParm();
          TParm result1 = new TParm();
        
          for (int i = 0; i < parm.getCount("RFID"); i++) {
        	  TParm c= new TParm();          
        	  c.setData("RFID", parm.getData("RFID",i));  
        	  c.setData("ORGIN_CODE", parm.getData("ORGIN_CODE",i));
        	  
        	  result = InvStockDDTool.getInstance().onUpdateOrginCode(c, conn);
          }   
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText()
                  + result.getErrName());
              conn.close();
              return result;
          }                       
         
          for (int i = 0; i < parm.getCount("RFID"); i++) {
        	  TParm c= new TParm();
        	  c.setData("RFID", parm.getData("RFID",i)); 
        	  c.setData("ORGIN_CODE", parm.getData("ORGIN_CODE",i));
        	  result1 = InvVerifyinDDTool.getInstance().onUpdateOrginCode(c, conn);
          }
          if (result1.getErrCode() < 0) {  
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
     * 更新验收入库单
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onUpdateVerifyin(parm, conn);
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
     * 更新验收入库单(效期)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateValData(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = InvVerifyinDDTool.getInstance().onUpdateValData(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = InvVerifyinDDTool.getInstance().onUpdateValDataByStockDD(parm, conn);
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
     * 更新验收入库单(批号)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateBatchData(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = InvVerifyinDDTool.getInstance().onUpdateBatchData(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        result = InvVerifyinDDTool.getInstance().onUpdateBatchDataByStockDD(parm, conn);
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
     * 删除验收入库单(整张删除)
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onDeleteVerifyin(parm, conn);
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
}
