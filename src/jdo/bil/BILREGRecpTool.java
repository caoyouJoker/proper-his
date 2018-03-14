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
public class BILREGRecpTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILREGRecpTool instanceObject;
    /**
     * 得到实例
     * @return BILREGRecpTool
     */
    public static BILREGRecpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILREGRecpTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILREGRecpTool() {
        setModuleName("bil\\BILREGRecpModule.x");
        onInit();
    }

    /**
     * 打印写账务收据
     * @param parm TParm
     * @param connection TConnection
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
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRecpForUnReg(TParm parm, TConnection connection) {
        TParm result = update("updateRecpForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 退挂写入一笔负数资料(FOR REG)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDataForUnReg(TParm parm, TConnection connection) {
        TParm result = update("insertDataForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 退挂查询收据数据
     * @param caseNo String
     * @return TParm
     */
    public TParm selDataForUnReg(String caseNo) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = query("selDataForUnReg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询挂号日结数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForAccount(TParm parm) {
        TParm result = query("selDateForAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新日结标记,日结号,日结人员,日结日期
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateAccount(TParm parm, TConnection connection) {
        TParm result = update("updateAccount", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询不同支付方式付款金额(日结)
     * @param parm TParm
     * @return TParm
     */
    public TParm selPayTypeFee(TParm parm) {
        TParm result = query("selPayTypeFee", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 补印查询全字段
     * @param caseNo String
     * @return TParm
     */
    public TParm selForRePrint(String caseNo) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = query("selForRePrint", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 更新补印收据(FOR REG)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upRecpForRePrint(TParm parm, TConnection connection) {
        TParm result = update("upRecpForRePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查询作废票据号码（日结）
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm getRegResetCount(TParm parm) {
    	TParm result = query("getRegResetCount", parm);
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
}
