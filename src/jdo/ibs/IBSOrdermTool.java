package jdo.ibs;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 住院费用主档工具类</p>
 *
 * <p>Description: 住院费用主档工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.03.18
 * @version 1.0
 */
public class IBSOrdermTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IBSOrdermTool instanceObject;
    /**
     * 得到实例
     * @return IBSOrdermTool
     */
    public static IBSOrdermTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSOrdermTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSOrdermTool() {
        setModuleName("ibs\\IBSOrdermModule.x");
        onInit();
    }

    /**
     * 查询所有数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdate(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @return TParm CASE_NO,CASE_NO_SEQ
     */
    public TParm deletedata(TParm parm) {
        TParm result = new TParm();
        result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新账单号码
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateBillNO(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateBillNO", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询所有病区,病区人数
     * @param parm TParm
     * @return TParm
     */
    public TParm selRegionStation(TParm parm) {
        TParm result = new TParm();
        result = query("selRegionStation", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    public TParm selRegionStationCaseNo(TParm parm)
    {
        TParm result = new TParm();
        result = query("selRegionStationCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 查询病区病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selStationPatInfo(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSOrdermTool.selStationPatInfo()参数异常!");
            return result;
        }
        result = query("selStationPatInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * 判断产生费用(For ADM)
     * @param parm TParm
     * @return boolean true 没产生 false 产生
     */
    public boolean existFee(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSOrdermTool.selStationPatInfo()参数异常!");
            return false;
        }
        result = query("existFee", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;

        }
        if (result.getCount("CASE_NO") > 0)
            return false;
        return true;
    }

    /**
     * 查询最大账务序号
     * @param caseNo String
     * @return TParm
     */
    public TParm selMaxCaseNoSeq(String caseNo) {
        TParm result = new TParm();
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", caseNo);
        result = query("selMaxCaseNoSeq", inParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询账务序号(批次)
     * @param caseNo String
     * @param billDate String
     * @return String
     */
    public String selCaseNoSeqForPatch(String caseNo, String billDate) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = query("selCaseNoSeqForPatch", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result.getErrName();
        }
        return result.getValue("CASE_NO_SEQ", 0);
    }
    /**
     * 
    * @Title: selCaseNoSeqForPatchSum
    * @Description: TODO(查询需要删除的所有账务序号)批次
    * @author pangben 2015-11-9
    * @param caseNo
    * @param billDate
    * @return
    * @throws
     */
    public TParm selCaseNoSeqForPatchSum(String caseNo, String billDate) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = query("selCaseNoSeqForPatch", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 删除费用明细档(批次)
     * @param caseNo String
     * @param billDate String
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteOrderMPatch(String caseNo, String billDate,
                                   TConnection conn) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = update("deleteOrderMPatch", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查询作废账单数据(作废账单)
     * @param parm TParm
     * @return TParm
     */
    public TParm selBillReturnM(TParm parm) {
        TParm result = new TParm();
        result = query("selBillReturnM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
