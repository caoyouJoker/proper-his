package com.javahis.ui.bil;

import java.sql.Timestamp;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.bil.BILInvoiceTool;
import com.dongyang.util.StringTool;

public class BILRecipientsReturnControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm recptype = (TParm)this.getParameter();
//        System.out.println("初始化数据" + recptype);
        this.callFunction("UI|TOT|setValue",
                          StringTool.bitDifferOfString(
                        		  //===zhangp 20130204 start
                              recptype.getValue("START_INVNO"),
//                              recptype.getValue("UPDATE_NO"),
//                              recptype.getValue("END_INVNO")) + 1);
                              recptype.getValue("UPDATE_NO")) + 1);
        					//===zhangp 20130204 end
        this.callFunction("UI|START_INVNO|setValue",
                          recptype.getValue("START_INVNO"));
        this.callFunction("UI|END_INVNO|setValue",
                          recptype.getValue("END_INVNO"));
        this.callFunction("UI|UPDATE_NO|setValue",
                          recptype.getValue("UPDATE_NO"));
        this.callFunction("UI|CASHIER_CODE|setValue", Operator.getID());
        this.callFunction("UI|OPT_TERM|setValue", Operator.getIP());
        this.callFunction("UI|CASHIER_CODE|setEnabled", false);
        this.callFunction("UI|OPT_TERM|setEnabled", false);
    }

    /**
     * 交回方法
     */
    public void onSave() {
        TParm recptype = (TParm)this.getParameter();
//        System.out.println("缴回传入数据"+recptype);
        recptype.setData("STATUS", "2");
//        System.out.println("缴回票据数据" + recptype);
        //如果没有打印票据
//        if (StringTool.bitDifferOfString(recptype.getValue("START_INVNO"),
//                                         recptype.getValue("UPDATE_NO")) == 0) {
//            recptype.setData("END_INVNO", "");
//            recptype.setData("UPDATE_NO", "");
//        }
//        else {
            //结束票号等于当前票号
//        }
            recptype.setData("UPDATE_NO_SUB",StringTool.subString(recptype.getValue("UPDATE_NO")));
            Timestamp date = SystemTool.getInstance().getDate();
            String now = date.toString().substring(0, 10).replace("-", "");
        String sql =
        	" UPDATE BIL_INVOICE SET STATUS='" + recptype.getValue("STATUS") + "'," +
//        			"START_INVNO='" + recptype.getValue("UPDATE_NO_SUB") + "', " +
        	" UPDATE_NO='" + recptype.getValue("UPDATE_NO") + "',OPT_USER='" + recptype.getValue("OPT_USER") + "', " +
        	" OPT_DATE=SYSDATE,OPT_TERM='" + recptype.getValue("OPT_TERM") + "', " +
        	" END_VALID_DATE=TO_DATE('" + now + "','YYYYMMDD') " +
        	" WHERE RECP_TYPE='" + recptype.getValue("RECP_TYPE") + "' " +
        	" AND START_INVNO='" + recptype.getValue("START_INVNO") + "'";
//        TParm result = BILInvoiceTool.getInstance().updatainData(recptype);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("E0005"); //执行失败
            return;
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
        this.callFunction("UI|onClose");
    }

}
