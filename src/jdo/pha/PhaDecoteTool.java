package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import jdo.ind.IndAgentTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 中医煎药</p>
 *
 * <p>Description: 中医煎药</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.5.21
 * @version 1.0
 */
public class PhaDecoteTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static PhaDecoteTool instanceObject;

    /**
     * 得到实例
     *
     * @return PhaDecoteTool
     */
    public static PhaDecoteTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaDecoteTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaDecoteTool() {
        setModuleName("pha\\PhaDecoteModule.x");
        onInit();
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        // 门急诊数据
        TParm resultOE = new TParm();

        if (parm.existData("ADM_TYPE_O") || parm.existData("ADM_TYPE_O")) {
            resultOE = this.query("queryOE", parm);
            if (resultOE.getErrCode() < 0) {
                err("ERR:" + resultOE.getErrCode() + resultOE.getErrText()
                    + resultOE.getErrName());
                return resultOE;
            }
        }
        // 住院数据
        TParm resultI = new TParm();
        if (parm.existData("ADM_TYPE_I")) {
            resultI = this.query("queryI", parm);
            if (resultI.getErrCode() < 0) {
                err("ERR:" + resultI.getErrCode() + resultI.getErrText()
                    + resultI.getErrName());
                return resultI;
            }
        }
        for (int i = 0; i < resultOE.getCount("SELECT_FLG"); i++) {
            AddData(result, resultOE.getRow(i));
        }
        for (int i = 0; i < resultI.getCount("SELECT_FLG"); i++) {
            AddData(result, resultI.getRow(i));
        }
        return result;
    }

    /**
     * 新增一行数据
     * @param result TParm
     * @param parm TParm
     * @return TParm
     */
    private TParm AddData(TParm result, TParm parm) {
        result.addData("SELECT_FLG", parm.getValue("SELECT_FLG"));
        result.addData("URGENT_FLG", parm.getValue("URGENT_FLG"));
        //result.addData("PRESRT_NO", parm.getValue("PRESRT_NO"));
        result.addData("FINAL_TYPE", parm.getValue("FINAL_TYPE"));
        result.addData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        result.addData("PRINT_NO", parm.getValue("PRINT_NO"));
        result.addData("ORG_CHN_DESC", parm.getValue("ORG_CHN_DESC"));
        result.addData("MR_NO", parm.getValue("MR_NO"));
        result.addData("PAT_NAME", parm.getValue("PAT_NAME"));
        result.addData("CLINICROOM_DESC", parm.getValue("CLINICROOM_DESC"));
        result.addData("DCT_TAKE_QTY", parm.getDouble("DCT_TAKE_QTY"));
        result.addData("FREQ_CHN_DESC", parm.getValue("FREQ_CHN_DESC"));
        result.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS"));
        result.addData("CHN_DESC", parm.getValue("CHN_DESC"));
        result.addData("PACKAGE_AMT", parm.getInt("PACKAGE_AMT"));
        result.addData("DECOCT_REMARK", parm.getValue("DECOCT_REMARK"));
        result.addData("CASE_NO", parm.getValue("CASE_NO"));
        result.addData("RX_NO", parm.getValue("RX_NO"));
        result.addData("SEQ_NO", parm.getInt("SEQ_NO"));
        result.addData("DEPT_ABS_DESC", parm.getValue("DEPT_ABS_DESC"));
        return result;
    }

    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryD(TParm parm) {
        TParm result = new TParm();
        if ("O".equals(parm.getValue("ADM_TYPE")) ||
            "E".equals(parm.getValue("ADM_TYPE"))) {
            // 门急诊
            result = this.query("queryDetailOE", parm);
        }
        else {
            // 住院
            result = this.query("queryDetailI", parm);
        }
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 保存
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onSave(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm data = new TParm();
        if ("TYPE_A".equals(parm.getValue("TYPE"))) {
            // 代送煎药室
            for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
                data = parm.getRow(i);
                data.setData("FINAL_TYPE", parm.getValue("FINAL_TYPE"));
                data.setData("DATE", parm.getTimestamp("DATE"));
                data.setData("SEND_DCT_USER", parm.getValue("USER_ID"));
                data.setData("OPT_USER", parm.getValue("OPT_USER"));
                data.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                if ("O".equals(data.getValue("ADM_TYPE")) ||
                    "E".equals(data.getValue("ADM_TYPE"))) {
                    result = this.update("updateSendDctUserOE", data, conn);
                }
                else {
                    result = this.update("updateSendDctUserI", data, conn);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }
        else if ("TYPE_B".equals(parm.getValue("TYPE"))) {
            // 待接收煎药
            for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
                data = parm.getRow(i);
                data.setData("FINAL_TYPE", parm.getValue("FINAL_TYPE"));
                data.setData("DATE", parm.getTimestamp("DATE"));
                data.setData("DECOCT_USER", parm.getValue("USER_ID"));
                data.setData("OPT_USER", parm.getValue("OPT_USER"));
                data.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                if ("O".equals(data.getValue("ADM_TYPE")) ||
                    "E".equals(data.getValue("ADM_TYPE"))) {
                    result = this.update("updateDecoctUserOE", data, conn);
                }
                else {
                    result = this.update("updateDecoctUserI", data, conn);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }
        else if ("TYPE_C".equals(parm.getValue("TYPE"))) {
            // 代送发药药房
            for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
                data = parm.getRow(i);
                data.setData("FINAL_TYPE", parm.getValue("FINAL_TYPE"));
                data.setData("DATE", parm.getTimestamp("DATE"));
                data.setData("SEND_ORG_USER", parm.getValue("USER_ID"));
                data.setData("OPT_USER", parm.getValue("OPT_USER"));
                data.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                if ("O".equals(data.getValue("ADM_TYPE")) ||
                    "E".equals(data.getValue("ADM_TYPE"))) {
                    result = this.update("updateSendOrgUserOE", data, conn);
                }
                else {
                    result = this.update("updateSendOrgUserI", data, conn);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 取消
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onCancel(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if ("TYPE_B".equals(parm.getValue("TYPE"))) {
            // 代送煎药室
            parm.setData("SEND_DCT_USER", parm.getValue("USER_ID"));
            if ("O".equals(parm.getValue("ADM_TYPE")) ||
                "E".equals(parm.getValue("ADM_TYPE"))) {
                result = this.update("updateSendDctUserOE", parm, conn);
            }
            else {
                result = this.update("updateSendDctUserI", parm, conn);
            }
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else if ("TYPE_C".equals(parm.getValue("TYPE"))) {
            // 待接收煎药
            parm.setData("DECOCT_USER", parm.getValue("USER_ID"));
            if ("O".equals(parm.getValue("ADM_TYPE")) ||
                "E".equals(parm.getValue("ADM_TYPE"))) {
                result = this.update("updateDecoctUserOE", parm, conn);
            }
            else {
                result = this.update("updateDecoctUserI", parm, conn);
            }
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else if ("TYPE_D".equals(parm.getValue("TYPE"))) {
            // 代送发药药房
            parm.setData("SEND_ORG_USER", parm.getValue("USER_ID"));
            if ("O".equals(parm.getValue("ADM_TYPE")) ||
                "E".equals(parm.getValue("ADM_TYPE"))) {
                result = this.update("updateSendOrgUserOE", parm, conn);
            }
            else {
                result = this.update("updateSendOrgUserI", parm, conn);
            }
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

}
