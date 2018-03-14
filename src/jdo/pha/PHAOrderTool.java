package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import jdo.opd.OrderList;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.opd.OrderTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PHAOrderTool extends TJDOTool {
    /**
     * 实例
     */
    public static PHAOrderTool instanceObject;

    /**
     * 得到实例
     * @return OrderTool
     */
    public static PHAOrderTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new PHAOrderTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PHAOrderTool() {
        setModuleName("opd\\OPDOrderModule.x");
        onInit();
    }

    /**
     * PHA异动主入口
     * @param parm
     * @param connection
     * @return result 保存结果
     */
    public TParm onSave(TParm parm, TConnection connection) {
        TParm result = OrderTool.getInstance().onDelete(parm.getParm(OrderList.
                DELETED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = OrderTool.getInstance().onInsert(parm.getParm(OrderList.NEW),
                                                  connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = onUpdate(parm.getParm(OrderList.MODIFIED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 更新
     * @param parm
     * @return result
     */
    public TParm onUpdate(TParm parm, TConnection connection) {
        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            inParm.setRowData( -1, parm, i);
            result = this.updatedata(inParm, connection);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 更新数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updatedataForPHA", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCostAmt(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateCostAmtForPHA", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新实际的发药数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDispenseDetailForPHA(TParm parm, TConnection connection) {
    	TParm result = new TParm();
    	result = update("updateDispenseDetailForPHA", parm, connection);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 
     * 查询处方的实际扣库信息
     * luhai 2012-1-31
     * @param parm
     * @param connection
     * @return
     */
    public TParm queryDispenseDetailForPHA(TParm parm, TConnection connection) {
    	TParm result = new TParm();
    	result = query("queryDispenseDetailForPHA", parm, connection);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }
}
