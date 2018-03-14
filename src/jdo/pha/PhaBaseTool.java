package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p> Title: 药品基本档tool </p>
 *
 * <p> Description: 药品基本档tool </p>
 *
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 *
 * <p> Company: javahis </p>
 *
 * @author ehui 20081005
 * @version 1.0
 */
public class PhaBaseTool extends TJDOTool {
    /**
     * 实例
     */
    public static PhaBaseTool instanceObject;

    /**
     * 得到实例
     *
     * @return PhaBaseTool
     */
    public static PhaBaseTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaBaseTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaBaseTool() {
        setModuleName("pha\\PhaBaseModule.x");
        onInit();
    }

    /**
     * 抓取指定药品基本数据
     */
    public TParm queryForAmout(String orderCode) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        result = query("queryforAmout", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 抓取指定药品基本数据
     */
    public TParm selectByOrder(String orderCode) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        result = query("selectByOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 返回抗生素默认使用天数
     * @param orderCode
     * @return
     */
	public TParm getAntiDrugDays(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TAKE_DAYS FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
						+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
						+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE"
						+ " AND B.ANTIBIOTIC_FLG='Y'"));
		return result;
	}

    /**
     * 返回是否为抗生素
     * @param orderCode
     * @return
     */
	public TParm isAntiDrug(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(
				TJDODBTool.getInstance().select(
								"SELECT A.ORDER_CODE FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
										+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
										+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE AND B.ANTIBIOTIC_FLG='Y'"));
		return result;
	}

    /**
     * 返回是否为毒麻药
     * @param orderCode
     * @return
     */
	public TParm isCtrlDrug(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(
				TJDODBTool.getInstance().select(
								"SELECT A.ORDER_CODE FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
										+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
										+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE AND B.CTRL_FLG='Y'"));
		return result;
	}

    /**
     * 更新移动加权平均价
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockPrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockPrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新批发价
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateTradePrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateTradePrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新零售价
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateRetailPrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateRetailPrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
