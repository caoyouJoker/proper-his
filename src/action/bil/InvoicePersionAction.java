package action.bil;

import com.dongyang.action.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.bil.BILCounteTool;
import jdo.bil.BILInvoiceTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILInvrcptTool;
import com.dongyang.data.TNull;

public class InvoicePersionAction
    extends TAction {
    /**
     * 开账
     * @param parm TParm
     * @return TParm
     */
    public TParm opencheck(TParm parm) {
        TConnection connection = getConnection();
        TParm invoice = new TParm();
//       invoice.setData("STATUS;OPT_USER;OPT_DATE;UPDATE_NO;OPT_TERM;TERM_IP;RECP_TYPE;START_INVNO",parm);
        invoice.setData("STATUS", parm.getData("STATUS"));
        invoice.setData("OPT_USER", parm.getData("OPT_USER"));
        invoice.setData("UPDATE_NO", parm.getData("UPDATE_NO"));
        invoice.setData("OPT_TERM", parm.getData("OPT_TERM"));
        invoice.setData("TERM_IP", parm.getData("OPT_TERM"));
        invoice.setData("RECP_TYPE", parm.getData("RECP_TYPE"));
        invoice.setData("START_INVNO", parm.getData("START_INVNO"));
        TParm result = new TParm();
        //开账写入counter表
        parm.setData("START_INVNO", parm.getData("UPDATE_NO"));
        result = BILCounteTool.getInstance().insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //更新invoice表

        result = BILInvoiceTool.getInstance().updataData(invoice, connection);
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
     * 关帐
     * @param parm TParm
     * @return TParm
     */
    public TParm closeCheck(TParm parm) {
        TConnection connection = getConnection();
        TParm datat = parm.getParm("counter");
        TParm result = BILCounteTool.getInstance().updataData(datat, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //更新invoice表
        TParm counterP = parm.getParm("invoice");
        result = BILInvoiceTool.getInstance().updataData(counterP, connection);
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
        String recpType = parm.getValue("RECP_TYPE");
        TParm selInvNoParm = new TParm();
        selInvNoParm.setData("INV_NO",updateno);
        selInvNoParm.setData("RECP_TYPE",recpType);
        TParm invNoParm = BILInvrcptTool.getInstance().getOneInv(selInvNoParm);
        if(invNoParm.getCount("INV_NO")>=0){
                result.setErr( -1, "票据中有已使用过，请核查");
                return result;
        }
        parm.setData("AR_AMT", 0);
        //调整票据
        parm.setData("CANCEL_FLG", new TNull(String.class));
        parm.setData("STATUS", "2");
        //Invrcpt循环插入调整的票据
        for (int i = 0; i < (Integer) parm.getData("NUMBER"); i++) {
            //取号原则
//            parm.setData("RECEIPT_NO",
//                         SystemTool.getInstance().getNo("ALL", "OPB",
//                "RECEIPT_NO",
//                "RECEIPT_NO"));
        	parm.setData("RECEIPT_NO", ""); //modify by huangtt 20141210
            parm.setData("INV_NO", updateno);
            err(" StringTool.addString((String)parm.getData(UPDATE_NO)):" +
                StringTool.addString( (String) parm.getData("UPDATE_NO")));
            if(recpType.equals("IBS")||recpType.equals("PAY"))
                parm.setData("ADM_TYPE","I");
            else if(recpType.equals("EKT")){
            	 parm.setData("ADM_TYPE","T");
            }else{
            	parm.setData("ADM_TYPE","O");
            }
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
        result = BILInvoiceTool.getInstance().upadjustData(parm, connection);
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
