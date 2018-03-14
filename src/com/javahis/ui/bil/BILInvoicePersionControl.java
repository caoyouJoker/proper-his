package com.javahis.ui.bil;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;
import jdo.bil.BILCounteTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILInvrcptTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:个人票号管理 </p>
 *
 * <p>Description:个人票号管理 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILInvoicePersionControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    TTable tableM;
    TTable tableD;
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable)this.getComponent("TABLE1");
        tableD = (TTable)this.getComponent("TABLE2");
        //table1的侦听事件
        callFunction("UI|TABLE1|addEventListener", "TABLE1->"
                     + TTableEvent.CLICKED, this, "onTableClicked");

        //        //checkbox点击事件
        //        this.callFunction("UI|TABLE1|addEventListener",
        //                          TTableEvent.CHECK_BOX_CLICKED, this,
        //                          "onCheckCliced");
        //
        //        //table1值改变事件
        //        this.addEventListener("TABLE1->" + TTableEvent.CHANGE_VALUE,
        //                              "onTableChangeValue");
        //带出使用ID
        this.callFunction("UI|CASHIER_CODE|setValue", Operator.getID());
        callFunction("UI|CASHIER_CODE|setEnabled", false);
        //带出使用者
        this.callFunction("UI|OPT_TERM|setValue", Operator.getIP());
        callFunction("UI|OPT_TERM|setEnabled", false);
        this.onQuery();
        initCombo();
    }

    /**
     * 初始化combo
     */
    public void initCombo() {
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:ORG_CODE;name:DEPT_DESC");
        comboBox.setStringData(
            "[[id,name],[,],['0','有效'],['1','作废'],['2','调整票号'],['3','作废补印']]");
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableD.addItem("SS", comboBox);

    }

    /**
     * 增加对Table的监听
     */
    public void onTableClicked() {
        int row = tableM.getSelectedRow();
        //记录当前选中的行
        selectrow = row;
        //得到当前选中行数据
        TParm parm = tableM.getParmValue().getRow(row);
        //查询票据明细
        TParm result = BILInvrcptTool.getInstance().selectByInvNo(parm);
        if (result.getErrCode() < 0)
            return;
        tableD.setParmValue(result);
        //如果下一票号为空,则说明此票据已经用光
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0)
            return;
        for (int i = 0; i < tableM.getRowCount(); i++) {
            tableM.setValueAt("N", i, 0);
        }
        tableM.setValueAt("Y", row, 0);
        tableM.acceptText();
    }

    /**
     * 查询数据
     */
    public void onQuery() {
        selectrow = -1;
        //得到查询参数
        TParm parm = this.getdata();
        //清空table
        this.callFunction("UI|TABLE1|removeRowAll");
        data = new TParm();
        //查询数据
        data = BILInvoiceTool.getInstance().selectAllData(parm);
        //给table配参
        tableM.setParmValue(data);
    }

    /**
     * 选择领用类别过滤table
     */
    public void onSelect() {
        this.onQuery();
    }

    /**
     * 查询配参
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = Operator.getID();
        if (value.length() != 0)
            parm.setData("CASHIER_CODE", value);
        //0表示使用中，1表示状态是没有交回的
        parm.setData("STATUS", "1");
        return parm;
    }

    /**
     * 开账
     */
    public void onSave() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            this.messageBox("E0012");
            return;
        }
        //取table上数据，转成TParm
        TParm parm = tableM.getParmValue().getRow(row);
        //如果下一票号为空,则说明此票据已经用光
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0) {
            messageBox_("此票据已经用完!");
            return;
        }
        //01代表使用中
        if (checkout(parm)) {
            //限制条件....已经开账不能再开
            this.messageBox("E0013");
            return;
        }
        //datat.setData("START_INVNO", parm.getData("UPDATE_NO", selectrow));
        //状态0代表使用中
        parm.setData("STATUS", "0");
        parm.setData("OPEN_DATE", StringTool.getString(SystemTool
            .getInstance().getDate(), "yyyyMMdd HHmmss"));
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("TERM_IP", Operator.getIP());
//        System.out.println("传入Action数据" + parm);
        //调用开账Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "opencheck", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //执行失败
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
        onQuery();
    }

    /**
     * 关帐方法
     */
    public void onClosedata() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            this.messageBox("E0012");
            return;
        }
        //取table上数据，转成TParm
        TParm parm = tableM.getParmValue().getRow(row);
        //wanglong add 20141120 解决界面数据与数据库不同步问题
        String sql = "SELECT * FROM BIL_INVOICE WHERE RECP_TYPE='#' AND START_INVNO='#'";
        sql = sql.replaceFirst("#", parm.getValue("RECP_TYPE"));
        sql = sql.replaceFirst("#", parm.getValue("START_INVNO"));
        TParm updateParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (updateParm.getErrCode() < 0 || updateParm.getCount() < 1) {
            this.messageBox("E0005");// 执行失败
            return;
        }
        parm = updateParm.getRow(0);
        //add end
        if (!parm.getValue("STATUS").equals("0")) {
            this.messageBox("此票据不在使用中");
            return;
        }
        if (!checkout(parm)) {
            this.messageBox("E0014");
            return;
        }
        //传入action的数据
        TParm closeParm = new TParm();
        //更新invoice表用数据
        parm.setData("STATUS", "1");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("TERM_IP", Operator.getIP());
        closeParm.setData("invoice", parm.getData());
        parm.setData("CLS_DATE",
                     StringTool.getString(SystemTool.getInstance()
                                          .getDate(), "yyyyMMdd HH:mm:ss"));
        //需要减1
        //如果下一票号为空,则结束票号就是结束票号
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0) {
            parm.setData("END_INVNO", parm.getValue("END_INVNO"));
        }
        else {
            //下一票号减一作为结束票号
            parm.setData("END_INVNO",
                         StringTool.subString(parm.getValue("UPDATE_NO")));
        }
        //如果下一票号等于初始票号,结束票号等于起始票号
        if (parm.getValue("START_INVNO").equals(parm.getValue("UPDATE_NO")))
            parm.setData("END_INVNO", parm.getValue("UPDATE_NO"));
        //添加更新counter表用数据
        closeParm.setData("counter", parm.getData());
        //调用关账Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "closeCheck", closeParm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //执行失败
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
        this.onQuery();
    }

//    /**
//     *领票
//     */
//    public void Recipients() {
//        String value = getValueString("RECP_TYPE");
//        if (value.length() == 0) {
//            this.messageBox("E0012");
//            return;
//        }
//        this.openDialog("%ROOT%\\config\\bil\\BILRecipients.x", value);
//        this.onQuery();
//    }

    /**
     *缴回
     */
    public void returnback() {
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        //取table上数据，转成TParm
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        if (checkout(datat)) {
            this.messageBox("此柜台尚未关账");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipientsReturn.x", datat);
        this.onQuery();
    }

    /**
     * 调整票号
     */
    public void onAdjustment() {
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        this
            .openDialog("%ROOT%\\config\\bil\\BILAdjustmentRecipients.x",
                        datat);
        this.onQuery();

    }

    /**
     * 清空方法
     */
    public void onClear() {
        clearValue("RECP_TYPE");
        selectrow = -1;
        callFunction("UI|TABLE2|clearSelection");
        callFunction("UI|TABLE1|clearSelection");
    }

    /**
     * 检查是否已经开账状态
     * @param parm TParm
     * @return boolean
     */
    public boolean checkout(TParm parm) {
        //用结束票号作为检查这组票是否正在使用中
        parm.setData("CASHIER_CODE", Operator.getID());
//		System.out.println("check parm="+parm);
        //关账时间
        TParm result = BILCounteTool.getInstance().CheckCounter(parm);
        if (result.getCount("CASHIER_CODE") > 0)
            return true;
        return false;
    }

    public boolean checkUseNow() {
        return false;
    }
    /**
     * 导出EXECL
     */
    public void onExecl(){
        if(tableM.getRowCount()<=0){
            this.messageBox("无导出数据！");
            return;
        }
//        tableD.getShowParmValue();

        ExportExcelUtil.getInstance().exportExcel(tableD,"票据使用明细");

    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }

    //    /**
     //    *
     //    * @param args String[]
     //    */
    //   public static void main(String args[]) {
    //       com.javahis.util.JavaHisDebug.TBuilder();
    //       Operator.setData("admin","HIS","127.0.0.1","C00101");
    //       System.out.println("sssssss-->"+Operator.getID());
    //   }

}
