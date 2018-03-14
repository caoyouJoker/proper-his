package action.bil;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TNull;

/**
 * 打印票据Action
 * <p>Title: </p>
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
public class BILRecipientsAction
    extends TAction {
    /**
     * 打印方法
     * 进参：BIL_Invrcpt票据类型，打印票号，流水号，操作员，总金额，作废注记，操作员，操作时间，操作末端
     * RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,TOT_AMT,CANCEL_FLG,CANCEL_USER,CANCEL_DATE,OPT_USER,OPT_DATEOPT_TERM
     * 进参：BIL_Invoice,票据类型，起始票号，结束票号，下一票号，操作人员，操作员，操作时间，操作终端
     * RECP_TYPE,START_INVNO,END_INVNO,UPDATE_NO,CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM
     * @param parm TParm
     * @return TParm
     */
    public TParm print(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        //更新当前票号BIL_Invoice
        result = BILInvoiceTool.getInstance().updataData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //插入打印的票据BIL_Invrcpt
        parm.setData("CANCEL_FLG", new TNull(String.class));
        parm.setData("STATUS", "0");
        result = BILInvrcptTool.getInstance().insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 调整票号方法
     * 进参：BIL_Invrcpt票据类型，打印票号，流水号，操作员，总金额，作废注记，操作员，操作时间，操作末端
     * RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,TOT_AMT,CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM
     * 进参：BIL_Invoice,票据类型，起始票号，下一票号，调整票号，操作人员，操作员，操作时间，操作终端
     * RECP_TYPE,START_INVNO,END_INVNO,UPDATE_NO,CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM
     * @param parm TParm
     * @return TParm
     */
    public TParm Adjustment(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        String updateno = (String) parm.getData("UPDATE_NO");
        parm.setData("TOT_AMT", 0);
        //调整票据
        parm.setData("CANCEL_FLG", new TNull(String.class));
        parm.setData("STATUS", "2");
        //Invrcpt循环插入调整的票据
        for (int i = 0; i < (Integer) parm.getData("NUMBER"); i++) {
            //取号原则
            parm.setData("INV_NO",
                         SystemTool.getInstance().getNo("ALL", "BIL", "INV_NO",
                "INV_NO"));
            parm.setData("RECEIPT_NO", updateno);
            err(" StringTool.addString((String)parm.getData(UPDATE_NO)):" +
                StringTool.addString( (String) parm.getData("UPDATE_NO")));
            //调用插入方法
            result = BILInvrcptTool.getInstance().insertData(parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
            //票号自加一
            updateno = StringTool.addString(updateno);
        }
        //调整当前票号Invoice
        parm.setData("UPDATE_NO", parm.getData("NOWNUMBER"));
        result = BILInvoiceTool.getInstance().updataData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }


}
