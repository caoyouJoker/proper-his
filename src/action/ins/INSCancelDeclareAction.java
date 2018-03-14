package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;
import jdo.ins.INSCancelDeclareTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;

/**
 *
 * <p>
 * Title: 取消申报
 * </p>
 *
 * <p>
 * Description:取消申报
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author xueyf 2012.02.11
 * @version 1.0
 */
public class INSCancelDeclareAction extends TAction {
    /**
     * 查询取消申报数据
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = null;
        try {
            connection = getConnection();
            result = INSCancelDeclareTool.getInstance().selectdata(parm);
            if (result == null || result.getErrCode() < 0) {
                return result;
            }
            connection.commit();
        } catch (Exception ex) {

        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;

    }

    /**
     * 查询取消申报数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        result = INSCancelDeclareTool.getInstance().updateINSStatus(parm);
        if (result == null || result.getErrCode() < 0) {
            return result;
        }
        result = INSCancelDeclareTool.getInstance().updateUploadFlg(parm);
        if (result == null || result.getErrCode() < 0) {
            return result;
        }
        return result;

    }

    /**
     * 取消申报 ————城职
     * @param tableData TParm
     * @return TParm
     */
    public TParm cancelByCZ(TParm tableData) {

        TParm result = new TParm();

        if (tableData == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = null;
        try {
            connection = getConnection();
            TParm updateParm = new TParm();

            for (int i = 0; i < tableData.getCount(); i++) {
             if (tableData.getValue("LOCAL_FLG").equals("Y")) {
                TParm parm = new TParm();
                parm.addData("ADM_SEQ", (String) tableData
                             .getData("ADM_SEQ", i)); // 就诊序号
                parm.addData("NHI_HOSP_NO", tableData
                             .getValue("HOSP_NHI_NO")); // 医院编码
                parm.addData("ACCOUNT_PAY_AMT", tableData.getDouble(
                        "ACCOUNT_PAY_AMT", i)); // 个人帐户支付金额
                parm.addData("PARM_COUNT", 3);
                parm.setData("PIPELINE", "DataDown_sp");
                parm.setData("PLOT_TYPE", "E9");
                TParm data = InsManager.getInstance().safe(parm);
                if (data == null || data.getErrCode() < 0) {
                    return data;
                }
              }
                TParm parm1 = new TParm();
                parm1.addData("HOSP_NHI_NO", (String) tableData
                             .getValue("HOSP_NHI_NO")); // 医院编码
                parm1.addData("CONFIRM_NO", (String) tableData.getData(
                        "ADM_SEQ", i));
                parm1.addData("PARM_COUNT", 2);
                parm1.setData("PIPELINE", "DataDown_sp");
                parm1.setData("PLOT_TYPE", "H");
                TParm data1 = InsManager.getInstance().safe(parm1);
                if (data1 == null || data1.getErrCode() < 0) {
                    return data1;
                }
                updateParm.setData("OPT_USER", tableData.getValue("OPT_USER"));
                updateParm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
                updateParm.setData("CONFIRM_NO", (String) tableData.getData(
                        "CONFIRM_NO", i));
                updateParm.setData("YEAR_MON", (String) tableData.getData(
                        "YEAR_MON", i));
                result = updateData(updateParm, connection);
                if (result == null || result.getErrCode() < 0) {
                    return result;
                }
                throw new Exception("更新数据库未成功，数据如下：" + parm1);
            }

            connection.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;
    }

    /**
     * 取消申报 ————城居
     * @param tableData TParm
     * @return TParm
     */
    public TParm cancelByCJ(TParm tableData) {
        TParm result = new TParm();

        if (tableData == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = null;
        try {
            connection = getConnection();
            TParm updateParm = new TParm();
//            System.out.println("前台table数据"+tableData);
            updateParm.setData("OPT_USER", Operator.getID());
            updateParm.setData("OPT_TERM", Operator.getIP());
            for (int i = 0; i < tableData.getCount(); i++) {

                TParm parm = new TParm();
                parm.addData("ADM_SEQ", (String) tableData
                             .getData("ADM_SEQ", i)); // 就诊序号
                parm.addData("HOSP_NHI_CODE", (String) tableData
                             .getValue("HOSP_NHI_NO")); // 医院编码
                parm.addData("PARM_COUNT", 2);
                parm.setData("PIPELINE", "DataDown_czys");
                parm.setData("PLOT_TYPE", "I");
//                System.out.println("撤销个人账户入参"+parm);
                TParm data = InsManager.getInstance().safe(parm);
                if (data == null || data.getErrCode() < 0) {
                    return data;
                }

                updateParm.setData("CONFIRM_NO", (String) tableData.getData(
                        "CONFIRM_NO", i));
                updateParm.setData("YEAR_MON", (String) tableData.getData(
                        "YEAR_MON", i));
                updateParm.setData("OPT_USER", tableData.getValue("OPT_USER"));
                updateParm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
                result = updateData(updateParm, connection);
                if (result == null || result.getErrCode() < 0) {
                    return result;
                }
                throw new Exception("更新数据库未成功，数据如下：" + parm);
            }

            connection.commit();
        } catch (Exception ex) {

        } finally {
            if (connection != null) {
                connection.close();
            }

        }
        return result;
    }
}
