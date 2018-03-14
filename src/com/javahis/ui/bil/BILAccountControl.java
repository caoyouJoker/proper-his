package com.javahis.ui.bil;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTableNode;
import jdo.bil.BILAccountTool;
import jdo.bil.BILCounteTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:个人日结 </p>
 *
 * <p>Description:个人日结 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class BILAccountControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //table1的侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1值改变事件
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        this.onQuery();
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("ACCOUNT_TYPE;ACCOUNT_USER;TOT_AMT",
                        data, row); //数据上翻
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * 增加对Table值改变的监听
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
    }

    /**
     * 查询数据
     */
    public void onQuery() {
        //得到查询参数
        TParm parm = new TParm();
        //清空table
        this.callFunction("UI|TABLE|removeRowAll");
        data = new TParm();
        //查询数据
        data = BILAccountTool.getInstance().selectData(parm);
        //整理数据
//        data = this.FinishData(data);
        //给table配参
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * 关帐方法
     */
    public void onClosedata() {
        //检核界面上的数据
        if (!this.emptyTextCheck("ACCOUNT_TYPE,ACCOUNT_USER"))
            return;
        TParm datat = this.Value();
        datat.setData("CLS_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd HH:mm:ss"));
        //需要减一
        datat.setData("END_INVNO", datat.getData("UPDATE_NO"));
        datat.setData("CASHIER_CODE", getValueString("ACCOUNT_USER"));
        datat.setData("OPT_USER", Operator.getID());
        datat.setData("OPT_TERM", Operator.getIP());
        TParm result = BILCounteTool.getInstance().updataData(datat);
        if (result.getErrCode() < 0) {
            this.messageBox("E0005"); //执行失败
            return;
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
    }

    /**
     * 日结方法
     */
    public void onAccount() {
        //检核界面上的数据
        if (!this.emptyTextCheck("ACCOUNT_TYPE,ACCOUNT_USER"))
            return;
        if (this.checkout().getValue("RECP_TYPE").length() == 0) {
            this.messageBox("请先关帐");
            return;
        }
        TParm parm = new TParm();
    }

    /**
     * 检查是否已经开账状态.带回当前使用票号
     * @return String
     */
    public TParm checkout() {
        //关账时间
        TParm parm = getParmForTag("CASHIER_CODE;RECP_TYPE");
        parm.setData("CLS_DATE", "");
        TParm result = BILAccountTool.getInstance().selectData(parm);
        return result;
    }

    /**
     * 查询当前使用着的票据
     * @return TParm
     */
    public TParm Value() {
        TParm parm = getParmForTag("CASHIER_CODE;RECP_TYPE");
        parm.setData("STARTDATE",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMM") + " 00:00:00");
        parm.setData("ENDDATE",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMM") + " 23:59:59");
        //1表示正在使用中
        parm.setData("STATUS", "1");
        TParm result = BILInvoiceTool.getInstance().selectAllData(parm);
        return result;
    }
}
