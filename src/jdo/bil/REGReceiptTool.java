package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title:挂号收据当 </p>
 *
 * <p>Description:挂号收据当 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class REGReceiptTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static REGReceiptTool instanceObject;
    /**
     * 得到实例
     * @return REGReceiptTool
     */
    public static REGReceiptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGReceiptTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public REGReceiptTool() {
        setModuleName("reg\\REGReceiptModule.x");
        onInit();
    }

    /**
     * 打印写账务收据
     * @return TParm
     */
    public TParm insertBill(TParm parm, TConnection connection) {
        TParm result = update("insertBill", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查询是否产生挂号收据(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm selCaseCountForREG(TParm parm) {
        TParm result = query("selCaseCountForREG", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新退挂收据(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateRecpForUnReg(TParm parm,TConnection connection) {
        TParm result = update("updateRecpForUnReg", parm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 退挂写入一笔负数资料(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDataForUnReg(TParm parm,TConnection connection) {
        TParm result = update("insertDataForUnReg", parm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
