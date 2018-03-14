package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

public class BILInvoiceTool
    extends TJDOTool {
    private static final String GET_USING_INVOICE_RANGE_SQL =
        "SELECT START_INVNO,END_INVNO " +
        "	FROM BIL_INVOICE " +
        "	WHERE RECP_TYPE='#' AND STATUS <>'3'";
    /**
     * 实例
     */
    public static BILInvoiceTool instanceObject;
    /**
     * 得到实例
     * @return BILInvoiceTool
     */
    public static BILInvoiceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvoiceTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILInvoiceTool() {
        setModuleName("bil\\BILInvoiceModule.x");
        onInit();
    }

    /**
     * 查询全部数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查询票据主档
     * @param parm TParm
     * @return TParm
     */
    public TParm selDate(TParm parm) {
        TParm result = query("selData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 领票插入新数据数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
        TParm result = update("insertData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 开账更新数据数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新数据数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 交回更新数据数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updatainData(TParm parm) {
        TParm result = update("updatainData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检核票号是否被使用过
     * @param recpType String
     * @return boolean
     */
    public boolean checkData(String recpType) {
        String sql = this.GET_USING_INVOICE_RANGE_SQL.replaceFirst("#",
            recpType);
        TParm usingParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (usingParm.getErrCode() != 0) {
            return false;
        }
        //todo

        return true;
    }

    /**
     * 检核给入的票据字段是否在需要检核的票据字段内，如是则返回真，如不再需要检核的票据字段内，返回假
     * @param startInvo String
     * @param endInvo String
     * @param checkStart String
     * @param checkEnd String
     * @return boolean
     */
    private boolean checkInRange(String startInvo, String endInvo,
                                 String checkStart, String checkEnd) {
        if (startInvo == null || startInvo.trim().length() == 0) {
            return false;
        }
        if (endInvo == null || endInvo.trim().length() == 0) {
            return false;
        }
        if (checkStart == null || checkStart.trim().length() == 0) {
            return false;
        }
        if (checkEnd == null || checkEnd.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * 打印票据更新当前票号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDatePrint(TParm parm, TConnection connection) {
        TParm result = update("updateDatePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到当前票号和起始票号
     * @param parm TParm
     * @return String[]
     */
    public String[] getUpdateUpdateNo(TParm parm) {
        TParm result = query("selectUpdateNo", parm);
        String[] error = new String[] {
            "-1", "-1"};
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return error;
        }
        String[] returndata = new String[] {
            result.getValue("UPDATE_NO", 0), result.getValue("START_INVNO", 0)};
        return returndata;

    }

    /**
     * 得到当前使用的票据
     * @param parm TParm
     * @return String[]
     */
    public TParm selectNowReceipt(TParm parm) {
        TParm result = query("selectNowReceipt", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 调整票号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upadjustData(TParm parm, TConnection connection) {
        TParm result = update("upadjustData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 打印票据更新当前票号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDatePrint(TParm parm) {
        TParm result = update("updateDatePrint", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
   
    
    /**
     * 校验票号是否被使用
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm checkUpdateNo(TParm parm) {
    	TParm result = query("checkUpdateNo", parm);
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * 充值退费向BIL_INVRCP表插入数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertFeeDate(TParm parm,TConnection connection) {
    	TParm result = update("insertFeeDate", parm,connection);
    	
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * 补印作废旧数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm cancelDate(TParm parm,TConnection connection) {
    	TParm result = update("cancelDate", parm,connection);
    	
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
    /**
     * 补印更新EKT_BIL_PAY表print_no
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateprintNo(TParm parm,TConnection connection) {
    	TParm result = update("updateprintNo", parm,connection);
    	
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
}
