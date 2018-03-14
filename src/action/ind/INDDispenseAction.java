package action.ind;

import jdo.ind.INDTool;
import jdo.ind.IndDispenseDTool;
import jdo.ind.IndDispenseMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: 出入库管理
 * </p>
 *
 * <p>
 * Description: 出入库管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.25
 * @version 1.0
 */
public class INDDispenseAction
    extends TAction {
    /**
     * 查询出库主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryOutM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryOutM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询入库主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryInM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryInM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 出库新增(在途) / 耗损、其它出库作业、卫耗材、科室备药(出库即入库)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
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
     * 出库主项更新(在途)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateMOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onUpdateM(parm, conn);
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
     * 出库即入库
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
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
     * 入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
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
     * 其他入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOtherIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOtherIn(parm, conn);
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
     * 取消出库作业
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDipenseCancel(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateDipenseCancel(parm, conn);
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
     * 草药自动维护收费标准
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateGrpricePrice(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateGrpricePrice(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        TIOM_Database.logTableAction("SYS_FEE");
        return result;
    }
    
    /**
     * 物联网调用HIS出库（HIS请领调用物联网）
     * @param parm
     * @return
     */
    public TParm onInsertOutOnSPC(TParm parm){
    	 TConnection conn = getConnection();
         TParm result = new TParm();
         TParm parmM = parm.getParm("OUT_M");
         
         //直接新增主表数据
         result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
         //直接保存细表数据
         TParm parmD = parm.getParm("OUT_D");
         for(int i = 0 ; i <   parmD.getCount("ORDER_CODE"); i++ ){
        	 
        	 TParm rowParm = (TParm)parmD.getRow(i);
	         result = IndDispenseDTool.getInstance().onInsertD(
	        		 rowParm, conn);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText()
	                 + result.getErrName());
	             conn.close();
	             return result;
	         }  
	         
	       //更新申请单状态及实际出入库数量
	         result = INDTool.getInstance().onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),rowParm,conn);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText()
	                 + result.getErrName());
	             conn.close();
	             return result;
	         }  
         }
         conn.commit();
         conn.close();
         return result;
    }
    
    
    /**
     * 出库即入库(物联网直接写数据不做任何判断)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutInSPC(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        
        //主表
        TParm parmM = parm.getParm("OUT_M");
        result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        
        TParm parmD = parm.getParm("OUT_D");
        
        for(int i = 0 ; i <   parmD.getCount("ORDER_CODE"); i++ ){
        	
        	TParm rowParm = (TParm)parmD.getRow(i);
	        result = IndDispenseDTool.getInstance().onInsertD(
	                rowParm, conn);
	        
	      //更新申请单状态及实际出入库数量
	        result = INDTool.getInstance().onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
	                                             parmD, conn);
        }
        
        conn.commit();
        conn.close();
        return result;
    }


    /**
     * 批量扣库
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateStockQty(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
          //扣库动作
        result = INDTool.getInstance().onUpdateStockQty(parm, conn);
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
