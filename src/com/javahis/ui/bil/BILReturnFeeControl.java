package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import java.sql.Timestamp;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;

/**
 *
 * <p>Title: 召回查询</p>
 *
 * <p>Description: 召回查询</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.08.10
 * @version 1.0
 */
public class BILReturnFeeControl
    extends TControl {
    TTextField IPD_NO;
    TTextField MR_NO;
    TTable table;
    TParm endData;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        endData = new TParm();
        IPD_NO = (TTextField)this.getComponent("IPD_NO");
        MR_NO = (TTextField)this.getComponent("MR_NO");
        table = (TTable)this.getComponent("Table");
        setValue("S_IPD", "Y");
        MR_NO.setEditable(false);
        setValue("MR_NO", "");
        setValue("IPD_NO", "");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_TIME", yesterday);
        setValue("E_TIME", SystemTool.getInstance().getDate());
        table.removeRowAll();
    }

    /**
     * 查询
     */
    public void onQuery() {
        checkNo();
        TParm selParm = new TParm();
        String mrNoWhere = " AND MR_NO = '" +
            getValue("MR_NO") + "' ";
        String ipdNoWhere = "";
        String sql =
            " SELECT RECEIPT_NO,OFFRECEIPT_NO,CHARGE_DATE,AR_AMT " +
            "   FROM BIL_RECPM " +
            "  WHERE AR_AMT < 0 " +
            "    AND REFUND_FLG = 'N' " +
            "    AND CHARGE_DATE BETWEEN TO_DATE('" +
            StringTool.getString(TypeTool.getTimestamp(getValue("S_TIME")),
                                 "yyyyMMdd") + "','yyyyMMdd')" +
            "    AND TO_DATE('" +
            StringTool.getString(TypeTool.getTimestamp(getValue("E_TIME")),
                                 "yyyyMMdd") + "','yyyyMMdd')";

        if (getValue("MR_NO").toString().length() != 0)
            sql = sql + mrNoWhere;
        if (getValue("IPD_NO").toString().length() != 0) {
            String selMrNo =
                " SELECT MR_NO,IPD_NO FROM ADM_INP WHERE IPD_NO='" +
                getValue("IPD_NO") + "'";
            TParm selMrNoParm = new TParm(TJDODBTool.getInstance().select(
                selMrNo));
            ipdNoWhere = " AND MR_NO = '" + selMrNoParm.getValue("MR_NO", 0) +
                "' ";
            sql = sql + ipdNoWhere;

        }
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        endData = new TParm();
        int count = selParm.getCount("RECEIPT_NO");
        for (int i = 0; i < count; i++) {
            String receiptNo = selParm.getValue("RECEIPT_NO", i);
            Timestamp chargeDate = selParm.getTimestamp("CHARGE_DATE", i);
            double arAmt = selParm.getDouble("AR_AMT", i);
            String offReceiptNo = selParm.getValue("OFFRECEIPT_NO", i);
            TParm offRecpParm = selOffReceiptNo(offReceiptNo);
            String mrNo = offRecpParm.getValue("MR_NO", 0);
            String ipdNo = offRecpParm.getValue("IPD_NO", 0);
            String patName = offRecpParm.getValue("PAT_NAME", 0);
            String stationDesc = offRecpParm.getValue("STATION_DESC", 0);
            String ctzDesc = offRecpParm.getValue("CTZ_DESC", 0);
            Timestamp chargeDateTwo = offRecpParm.getTimestamp("CHARGE_DATE", 0);
            double arAmtTwo = offRecpParm.getDouble("AR_AMT", 0);
            endData.addData("MR_NO", mrNo);
            endData.addData("IPD_NO", ipdNo);
            endData.addData("PAT_NAME", patName);
            endData.addData("STATION_DESC", stationDesc);
            endData.addData("CTZ_DESC", ctzDesc);
            endData.addData("RETURN_DATE", "");
            endData.addData("RETURN_USER", "");
            endData.addData("CHARGE_DATE_TWO",
                            StringTool.getString(chargeDateTwo,
                                                 "yyyy/MM/dd HH:mm:ss"));
            endData.addData("AR_AMT_TWO", arAmtTwo);
            endData.addData("CHARGE_DATE",
                            StringTool.getString(chargeDate,
                                                 "yyyy/MM/dd HH:mm:ss"));
            endData.addData("AR_AMT", arAmt);
        }
        endData.setCount(count);
//        this.messageBox("selParm" + selParm);
        if (selParm.getCount("RECEIPT_NO") < 1) {
            this.messageBox_("查无数据");
            this.initPage();
            return;
        }
        this.callFunction("UI|Table|setParmValue", endData);
        endData.addData("SYSTEM", "COLUMNS", "MR_NO");
        endData.addData("SYSTEM", "COLUMNS", "IPD_NO");
        endData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        endData.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        endData.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
        endData.addData("SYSTEM", "COLUMNS", "RETURN_DATE");
        endData.addData("SYSTEM", "COLUMNS", "RETURN_USER");
        endData.addData("SYSTEM", "COLUMNS", "CHARGE_DATE_TWO");
        endData.addData("SYSTEM", "COLUMNS", "AR_AMT_TWO");
        endData.addData("SYSTEM", "COLUMNS", "CHARGE_DATE");
        endData.addData("SYSTEM", "COLUMNS", "AR_AMT");

    }

    /**
     * 打印
     */
    public void onPrint() {
        if (endData == null || endData.getCount("MR_NO") <= 0) {
            this.messageBox("无打印数据");
            return;
        }
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {

        TParm printData = endData;
//        System.out.println("打印数据==>:"+printData);
        TParm parm = new TParm();
        parm.setData("Title", "召回统计表");
        parm.setData("S_TIME", "TEXT",
                     StringTool.getString(TypeTool.
                                          getTimestamp(getValue("S_TIME")),
                                          "yyyy/MM/dd"));
        parm.setData("E_TIME", "TEXT",
                     StringTool.getString(TypeTool.
                                          getTimestamp(getValue("E_TIME")),
                                          "yyyy/MM/dd"));
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("OPT_DATE", "TEXT",
                     StringTool.getString(TypeTool.
                                          getTimestamp(SystemTool.getInstance().
            getDate()), "yyyy/MM/dd HH:mm:ss"));
        parm.setData("summarytable", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\BILReturnFeeSummary.jhw",
                             parm);

    }

    /**
     * 汇出Excel
     */
    public void onExcel() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "召回统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
    }

    /**
     * 查找召回数据
     * @param offReceuptNo String
     * @return TParm
     */
    public TParm selOffReceiptNo(String offReceuptNo) {
        TParm result = new TParm();
        String sql =
            " SELECT A.CHARGE_DATE,A.AR_AMT,A.MR_NO,B.IPD_NO,C.PAT_NAME,D.STATION_DESC,E.CTZ_DESC " +
            "   FROM BIL_RECPM A,ADM_INP B,SYS_PATINFO C,SYS_STATION D,SYS_CTZ E " +
            "  WHERE A.CASE_NO = B.CASE_NO " +
            "    AND A.MR_NO = C.MR_NO " +
            "    AND B.DS_DEPT_CODE = D.STATION_CODE " +
            "    AND B.CTZ_CODE = E.CTZ_CODE " +
            "    AND A.RECEIPT_NO = '" + offReceuptNo + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 校验checkBox
     */
    public void checkFlg() {
        if ("Y".equals(getValue("S_IPD"))) {
            setValue("MR_NO", "");
            IPD_NO.setEditable(true);
            MR_NO.setEditable(false);
        }
        if ("Y".equals(getValue("S_MR"))) {
            setValue("IPD_NO", "");
            IPD_NO.setEditable(false);
            MR_NO.setEditable(true);
        }
    }

    /**
     * 补齐病案号，住院号长度
     */
    public void checkNo() {
        if ("Y".equals(getValue("S_IPD")) && !"".equals(getValue("IPD_NO"))) {
            String oldIpdNo = String.valueOf(getValue("IPD_NO"));
            String ipdNo = "000000000000".substring(0, 12 - oldIpdNo.length()) +
                oldIpdNo;
            setValue("IPD_NO", ipdNo);
            IPD_NO.setEditable(false);

        }
        if ("Y".equals(getValue("S_MR")) && getValue("MR_NO") != "") {
            String oldMrNo = String.valueOf(getValue("MR_NO"));
            String mrNo = "000000000000".substring(0, 12 - oldMrNo.length()) +
                oldMrNo;
            setValue("MR_NO", mrNo);
            MR_NO.setEditable(false);
        }

    }

}
