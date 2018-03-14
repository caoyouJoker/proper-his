package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;

/**
 * <p>Title: 膳食系统</p>
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
public class NSSTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSTool
     */
    public static NSSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSTool() {
        onInit();
    }

    /**
     * 删除套餐（包括：主项，细项和明细）
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPack(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        // 删除套餐菜名
        result = NSSPackDDTool.getInstance().onDeleteNSSPackDD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除套餐餐点
        result = NSSPackDTool.getInstance().onDeleteNSSPackD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除套餐字典
        result = NSSPackMTool.getInstance().onDeleteNSSPackM(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 删除餐点（包括：细项和明细）
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        // 删除套餐菜名
        result = NSSPackDDTool.getInstance().onDeleteNSSPackDD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除套餐餐点
        result = NSSPackDTool.getInstance().onDeleteNSSPackD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 订餐
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertNSSOrder(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            // 订餐
            result = NSSOrderTool.getInstance().onInsertNSSOrder(parm.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 收费(同时写入IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateNSSChagre(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            // 收费
            result = NSSOrderTool.getInstance().onUpdateNSSChagre(parm.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        //计价接口
        TParm inParmIbs = new TParm();
        inParmIbs.setData("M", parm.getData());
        inParmIbs.setData("FLG", "ADD");
        //zhangyong20110516 添加区域REGION_CODE
        inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));

        inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
        inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
        inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
        TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
        if (resultIbs.getErrCode() < 0) {
            return resultIbs;
        }
        TParm inIbs = new TParm();
        inIbs.setData("M", resultIbs.getData());
        inIbs.setData("DATA_TYPE", "4");
        inIbs.setData("FLG", "ADD");
        TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs, conn);
        if (resultIbsM2.getErrCode() != 0) {
            return resultIbsM2;
        }
        return result;
    }

    /**
     * 退费(同时写入IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateNSSUnChagre(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        // 退费
        result = NSSOrderTool.getInstance().onUpdateNSSChagre(parm.getRow(0), conn);
        if (result.getErrCode() < 0) {
            return result;
        }

        //计价接口
        TParm inParmIbs = new TParm();
        inParmIbs.setData("M", parm.getData());
        inParmIbs.setData("FLG", "ADD");
        //zhangyong20110516 添加区域REGION_CODE
        inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));

        inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
        inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
        inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
        TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
        if (resultIbs.getErrCode() < 0) {
            return resultIbs;
        }
        TParm inIbs = new TParm();
        inIbs.setData("M", resultIbs.getData());
        inIbs.setData("DATA_TYPE", "4");
        inIbs.setData("FLG", "DIX");
        TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs, conn);
        if (resultIbsM2.getErrCode() != 0) {
            return resultIbsM2;
        }
        return result;
    }

}
