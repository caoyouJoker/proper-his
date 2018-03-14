package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 开单科室核算报表</p>
 *
 * <p>Description: 开单科室核算报表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.20
 * @version 1.0
 */
public class BILDeptWordControl extends TControl {
    public void onInit() {
        super.onInit();
        initPage();
    }

    String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
                         "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
                         "CHARGE09",
                         "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
                         "CHARGE14",
                         //===zhangp 20120312 start
                         "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
                         "CHARGE19", "CHARGE20"};
    //===zhangp 20120312 end
    private TParm parmName; //费用名称
    private TParm parmCode; //费用代码

    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("DEPT_CODE", "");
        setValue("STATION_CODE", "");
        String sql = SYSSQL.getBillRecpparm(); //获得费用代码
        this.callFunction("UI|Table|removeRowAll");
        sql += " WHERE ADM_TYPE='I'";
        parmCode = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
            this.messageBox("设置费用字典有问题");
            return;
        }
        //获得费用名称
        sql =
                "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
        parmName = new TParm(TJDODBTool.getInstance().select(sql));
        TTable table = (TTable)this.getComponent("Table");
        table.setParmValue(getHeard());
    }

    /**
     * 添加表头
     * @return TParm
     */
    private TParm getHeard() {
        TParm heardParm = new TParm();
        heardParm.addData("DEPT_CHN_DESC", "科室名称");
        heardParm.addData("STATION_DESC", "病区");
        heardParm.addData("TOT_AMT", "合计金额");
        for (int i = 0; i < chargName.length; i++) {
            heardParm.addData(chargName[i],
                              getChargeName(parmName,
                                            parmCode.getValue(chargName[i], 0)));
        }
        heardParm.setCount(1);
        return heardParm;
    }

    /**
     * 获得费用名称
     * @param parmName TParm
     * @param chargeCode String
     * @return String
     */
    private String getChargeName(TParm parmName, String chargeCode) {
        for (int i = 0; i < parmName.getCount(); i++) {
            if (parmName.getValue("ID", i).equals(chargeCode)) {
                return parmName.getValue("CHN_DESC", i);
            }
        }
        return "";
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 1) {
            this.messageBox_("先查询数据!");
            return;
        }
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("TITLE", "TEXT", "开单科室核算报表");
        parm.setData("DATE", "TEXT", sDate + " 至 " + eDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("printDate", "TEXT", sysDate);
        parm.setData("T1", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\BILExeDeptFee.jhw",
                             parm);

    }

    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String deptWhere = "";
        if (getValue("DEPT_CODE") != null) {
            if (getValue("DEPT_CODE").toString().length() != 0)
                deptWhere = " AND A.DEPT_CODE = '" + getValue("DEPT_CODE") +
                            "'  ";
        }
        if (this.getValue("STATION_CODE") != null &&
            this.getValueString("STATION_CODE").length() > 0) {
            deptWhere += " AND A.STATION_CODE = '" +
                    this.getValueString("STATION_CODE") + "'";
        }
        String regionWhere = "";
        if (!"".equals(Operator.getRegion()))
            regionWhere = " AND B.REGION_CODE = '" + Operator.getRegion() +
                          "' ";
        String sql =
                "   SELECT A.DEPT_CODE AS EXE_DEPT_CODE, SUM (A.TOT_AMT)  AS TOT_AMT, A.REXP_CODE, B.DEPT_CHN_DESC,C.STATION_DESC " +
                "     FROM IBS_ORDD A, SYS_DEPT B,SYS_STATION C " +
                "    WHERE A.BILL_DATE BETWEEN TO_DATE ('" + startTime +
                "000000" +
                "', 'yyyyMMddhh24miss') " +
                "                      AND TO_DATE ('" + endTime + "235959" +
                "', 'yyyyMMddhh24miss') " +
                deptWhere +
                regionWhere +
                "      AND A.DEPT_CODE = B.DEPT_CODE(+) " +
                "      AND A.STATION_CODE = C.STATION_CODE(+) " +
                " GROUP BY A.DEPT_CODE,C.STATION_DESC, A.REXP_CODE, B.DEPT_CHN_DESC " +
                " ORDER BY A.DEPT_CODE ";
//        System.out.println("sql"+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("EXE_DEPT_CODE") < 1) {
            //查无数据
            this.messageBox("E0008");
            this.initPage();
            return selParm;
        }
        BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
        TParm endParm = endData.getValue(selParm);
        TParm resultParm = getHeard(); //表头
        int count = resultParm.getCount();
        for (int i = 0; i < endParm.getCount(); i++) {
            resultParm.setRowData(count, endParm, i);
            count++;
        }
        resultParm.setCount(count);
        resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        for (int i = 0; i < chargName.length; i++) {
            resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
        }
        this.callFunction("UI|Table|setParmValue", resultParm);
        return resultParm;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "开单科室核算报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        // TTable table = (TTable)this.getComponent("Table");
        // table.removeRowAll();
        this.clearValue("STATION_CODE;DEPT_CODE");
    }

    /**
     * 科室combo事件
     */
    public void onDEPT() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }
}
