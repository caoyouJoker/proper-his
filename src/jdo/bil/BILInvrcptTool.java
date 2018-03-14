package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILInvrcptTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILInvrcptTool instanceObject;
    /**
     * 得到实例
     * @return BILInvrcptTool
     */
    public static BILInvrcptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvrcptTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILInvrcptTool() {
        setModuleName("bil\\BILInvrcptModule.x");
        onInit();
    }

    /**
     * 查询全部数据明细
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectData", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 打印票据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        parm.setData("PRINT_USER", parm.getData("OPT_USER"));
        TParm result = update("insertData", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 更新,作废等
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 更新数据数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 票据管理
     * @param parm TParm
     * @return TParm
     */
    public TParm selectByInvNo(TParm parm) {
        TParm result = query("selectByInvNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 查找一张票据(退费)
     * @param parm TParm
     * @return TParm
     */
    public TParm getOneInv(TParm parm) {
        TParm result = query("getOneInv", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 日结
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm account(TParm parm, TConnection connection) {
        TParm result = update("account", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * 日结o e h
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm accountAll(TParm parm, TConnection connection) {
    	TParm result = update("accountAll", parm, connection);
    	if (result.getErrCode() < 0)
    		err(result.getErrCode() + " " + result.getErrText());
    	return result;
    }

    /**
     * 得到日结退费张数
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvalidCount(TParm parm) {
        TParm result = query("getInvalidCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }   
    
    /**
     * 得到日结退费张数(O,E,H)
     * ====zhangp 20120310
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvalidCountAll(TParm parm) {
        TParm result = query("getInvalidCountAll", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 查找日结票据张数
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvrcpCount(TParm parm) {
        TParm result = query("getInvrcpCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * 查找日结票据张数(O,E,H)
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvrcpCountAll(TParm parm) {
    	TParm result = query("getInvrcpCountAll", parm);
    	if (result.getErrCode() < 0)
    		err(result.getErrCode() + " " + result.getErrText());
    	return result;
    }

    /**
     * 查找日结票据号
     * @param parm TParm
     * @return TParm
     */
    public TParm getPrintNo(TParm parm) {
        TParm result = query("getPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 得到退费票号
     * @param parm TParm
     * @return TParm
     */
    public TParm getBackPrintNo(TParm parm) {
        TParm result = query("getBackPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 得到作废票号
     * @param parm TParm
     * @return TParm
     */
    public TParm getTearPrintNo(TParm parm) {
        TParm result = query("getTearPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
}
