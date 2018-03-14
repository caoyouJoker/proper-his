package com.javahis.ui.bil;

import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TRadioButton;
import java.text.DecimalFormat;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.system.textFormat.TextFormatDept;
import com.dongyang.ui.TTextField;
import jdo.sys.PatTool;

/**
 * <p>Title: 住院退费查询</p>
 *
 * <p>Description: 住院退费查询</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.06.11
 * @version 1.0
 */
public class BILReturnFeeSelControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    String data_type = "";

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
//        onClear();

        this.initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        //当前时间
        Timestamp today = SystemTool.getInstance().getDate();
        //获取选定日期的前一天的日期
        Timestamp yesterday = StringTool.rollDate(today, -1);
        this.setValue("S_DATE", yesterday);
        this.setValue("E_DATE", today);
        TextFormatDept deptCode = (TextFormatDept)this.getComponent("DEPT_CODE");
        deptCode.setEnabled(false);
        TTextField mrNo = (TTextField)this.getComponent("MR_NO");
        mrNo.setEnabled(false);
        TTextField ipdNo = (TTextField)this.getComponent("IPD_NO");
        ipdNo.setEnabled(false);
        TRadioButton selExeDept = (TRadioButton)this.getComponent("SEL_EXE_DEPT");
        selExeDept.setSelected(true);
    }

    /**
     * 查询
     */
    public void onQuery() {
        String mrNo ="";
        if(getValue("MR_NO").toString().length()>0){
            mrNo = PatTool.getInstance().checkMrno(getValue("MR_NO").toString());
            setValue("MR_NO",mrNo);

        }
        String ipdNo = "";
        if(getValue("IPD_NO").toString().length()>0){
            ipdNo = PatTool.getInstance().checkMrno(getValue("IPD_NO").toString());
            setValue("IPD_NO",ipdNo);
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        String startTime = StringTool.getString((Timestamp)getValue("S_DATE"),"yyyyMMdd");
        String endTime = StringTool.getString((Timestamp)getValue("E_DATE"),"yyyyMMdd");;
        TParm selParm = new TParm();
        String where = "";
        if (data_type.equals("0")) {
            where = " AND SD.EXE_DEPT_CODE = '" + getValue("EXE_DEPT_CODE").toString() +
                "' ";
        }
        else if (data_type.equals("1")) {
            where = " AND SD.DEPT_CODE = '" +
                getValue("DEPT_CODE").toString() + "' ";
        }
        else if (data_type.equals("2")) {
            where = " AND SM.MR_NO = '" + getValue("MR_NO").toString() + "' ";
        }
        else if (data_type.equals("3")) {
            where = " AND SM.IPD_NO = '" + getValue("IPD_NO").toString() + "' ";
        }
        String sql =
            " SELECT SD.BILL_DATE, SM.MR_NO, SP.PAT_NAME,SF.ORDER_DESC, SD.DOSAGE_QTY, SD.TOT_AMT, " +
            "        SD.OPT_USER, SD.DEPT_CODE, SD.EXE_DEPT_CODE, SD.REXP_CODE " +
            "   FROM IBS_ORDM SM, IBS_ORDD SD, SYS_PATINFO SP, SYS_FEE SF " +
            "  WHERE SM.CASE_NO = SD.CASE_NO " +
            "    AND SM.CASE_NO_SEQ = SD.CASE_NO_SEQ " +
            "    AND SD.BILL_DATE BETWEEN TO_DATE('" + startTime +
            "000000" + "','yyyyMMddHH24miss') " +
            "            AND TO_DATE('" + endTime + "235959" +
            "','yyyyMMddHH24miss') " +
            "    AND SM.MR_NO = SP.MR_NO " +
            "    AND SD.ORDER_CODE = SF.ORDER_CODE " +
            "    AND SD.TOT_AMT < 0 "+
            where+
            "  ORDER BY SD.BILL_DATE " ;
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("BILL_DATE") < 1) {
            this.messageBox("查无数据");
            this.onClear();
            return;
        }
        int count = selParm.getCount("BILL_DATE");
        double totAmt = 0.00;
        for(int i =0;i<count;i++){
        double fee=0.00;
        fee=selParm.getDouble("TOT_AMT",i);
        totAmt = fee+totAmt;
        }
        this.callFunction("UI|Table|setParmValue", selParm);
        this.setValue("TOT_AMT",totAmt);
    }
    /**
     * ridiobutton改变事件
     */
    public void selChange() {
        TextFormatDept exeDeptCode = (TextFormatDept)this.getComponent("EXE_DEPT_CODE");
        TextFormatDept deptCode = (TextFormatDept)this.getComponent("DEPT_CODE");
        TTextField mrNo = (TTextField)this.getComponent("MR_NO");
        TTextField ipdNo = (TTextField)this.getComponent("IPD_NO");
            //执行科室
        if ("Y".equals(getValue("SEL_EXE_DEPT"))) {
            exeDeptCode.setEnabled(true);
            deptCode.setEnabled(false);
            setValue("DEPT_CODE", "");
            mrNo.setEnabled(false);
            setValue("MR_NO", "");
            ipdNo.setEnabled(false);
            setValue("IPD_NO", "");
            data_type = "0";
        }//开单科室
        else if ("Y".equals(getValue("SEL_DEPT"))) {
            exeDeptCode.setEnabled(false);
            setValue("EXE_DEPT_CODE", "");
            deptCode.setEnabled(true);
            mrNo.setEnabled(false);
            setValue("MR_NO", "");
            ipdNo.setEnabled(false);
            setValue("IPD_NO", "");
            data_type = "1";

        }//病案号
        else if ("Y".equals(getValue("SEL_MR_NO"))) {
            exeDeptCode.setEnabled(false);
            setValue("EXE_DEPT_CODE", "");
            deptCode.setEnabled(false);
            setValue("DEPT_CODE", "");
            mrNo.setEnabled(true);
            ipdNo.setEnabled(false);
            setValue("IPD_NO", "");
            data_type = "2";

        }//病案号
        else if ("Y".equals(getValue("SEL_IPD_NO"))) {
            exeDeptCode.setEnabled(false);
            setValue("EXE_DEPT_CODE", "");
            deptCode.setEnabled(false);
            setValue("DEPT_CODE", "");
            mrNo.setEnabled(false);
            setValue("MR_NO", "");
            ipdNo.setEnabled(true);
            data_type = "3";
        }
    }
    /**
     * 清空
     */
    public void onClear() {
        clearValue("EXE_DEPT_CODE;DEPT_CODE;MR_NO;IPD_NO;S_DATE;" +
                   "E_DATE;TOT_AMT");
        ( (TTable) getComponent("TABLE")).removeRowAll();
        this.initPage();
        selectRow = -1;
    }

}
