package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.bil.BillItemTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title:医疗项目明细日（月）结报表
 *
 * <p>Description: 医疗项目明细日（月）结报表
 *
 * <p>Copyright: Copyright (c) 2013
 *
 * <p>Company: ProperSoft</p>
 *
 * @author  chenx 2013.01.30
 * @version 1.0
 */
public class BILItemFeeDetailControl
        extends TControl {// refactor by wanglong 20130613

    private TTable tableM,tableD;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable) this.getComponent("TABLE_M");
        tableD = (TTable) this.getComponent("TABLE_D");
        addSortListener(tableM);// add by wanglong 20130926
        addSortListener(tableD);// add by wanglong 20130613
        this.initUI();
        callFunction("UI|MR_NO|setEnabled", false);
    }

    /**
     * 初始化界面
     */
    public void initUI() {
        // this.setValue("DEPT_CODE", Operator.getDept()) ;
        String now = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(now, "yyyyMMdd"));// 开始时间
        this.setValue("END_DATE", StringTool.getTimestamp(now, "yyyyMMdd"));// 结束时间
    }

    /**
     * 查询
     */
    public void onQuery() {
        if (!check()) {
            return;
        }
        TParm inParm = new TParm();
        inParm.setData("START_DATE", this.getValue("START_DATE"));
        inParm.setData("END_DATE", this.getValue("END_DATE"));
        inParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        inParm.setData("MR_NO", this.getValueString("MR_NO"));
        if (!this.getValueString("HOSP_CODE").equals("")) {
            inParm.setData("TRANS_HOSP_CODE", this.getValueString("HOSP_CODE"));
        }
        TParm result = new TParm();
        String admType=this.getValueString("ADM_TYPE");
        if (getPanel("PANEL_D").isShowing()) {// ===========================明细页签
            if (admType.equals("O")) {// 门诊OPD_ORDER
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryOPDByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryOPDByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("H")) { // 健康检查HRM_ORDER
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryHRMByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryHRMByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if(admType.equals("I")) {//住院 IBS_ORDD
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryODIByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryODIByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("S")) {// 汇总
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sqlO =
                            BillItemTool.getInstance().onQueryOPDByDept(inParm).split("ORDER BY")[0];
                    String sqlH =
                            BillItemTool.getInstance().onQueryHRMByDept(inParm).split("ORDER BY")[0];
                    String sqlI =
                            BillItemTool.getInstance().onQueryODIByDept(inParm).split("ORDER BY")[0];
                    String sql =
                            "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,TRUNC(AR_AMT/OWN_AMT,2) DISCOUNT_RATE,UNIT_CODE,"
                                    + "SUM(DISPENSE_QTY) DISPENSE_QTY, OWN_PRICE, SUM(OWN_AMT) OWN_AMT, SUM(AR_AMT) AR_AMT, NS_NOTE "
                                    + "FROM(SELECT * FROM(#)       UNION ALL      SELECT * FROM(#)       UNION ALL      SELECT * FROM(#)   ) "
                                    +"WHERE OWN_PRICE <> 0 AND AR_AMT <> 0 "
                                    + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE,OWN_PRICE,AR_AMT/OWN_AMT,NS_NOTE "
                                    + "ORDER BY DEPT_CODE,CHN_DESC,ORDER_CODE,DISCOUNT_RATE,DISPENSE_QTY";
                    sql = sql.replaceFirst("#", sqlO);
                    sql = sql.replaceFirst("#", sqlH);
                    sql = sql.replaceFirst("#", sqlI);
//                    System.out.println("-------明细查询汇总--------" + sql);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if (result.getErrCode() < 0) {
                        this.messageBox("查询失败 " + result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    this.messageBox("个人不能使用汇总查询");
                    return;
                }
            }
            // System.out.println("=================result==============" + result);
            tableD.setParmValue(result);
        } else {// =================================================汇总页签
            if (admType.equals("O")) {// 门诊OPD_ORDER
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryTotOPDByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotOPDByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("H")) { // 健康检查HRM_ORDER
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryTotHRMByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotHRMByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if(admType.equals("I")) {//住院 IBS_ORDD
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sql = BillItemTool.getInstance().onQueryTotODIByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotODIByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("查询失败 "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("S")) {// 汇总
                if (isSelected("SELECT_DEPT")) { // 科室
                    String sqlO =
                            BillItemTool.getInstance().onQueryTotOPDByDept(inParm).split("ORDER BY")[0];
                    String sqlH =
                            BillItemTool.getInstance().onQueryTotHRMByDept(inParm).split("ORDER BY")[0];
                    String sqlI =
                            BillItemTool.getInstance().onQueryTotODIByDept(inParm).split("ORDER BY")[0];
                    String sql =
                            "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE,SUM(DISPENSE_QTY) DISPENSE_QTY "
                                    + " FROM (#  UNION ALL  #  UNION ALL  # ) "
                                    + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE "
                                    + "ORDER BY DEPT_CODE,CHN_DESC,ORDER_CODE,DISPENSE_QTY";
                    sql = sql.replaceFirst("#", sqlO);
                    sql = sql.replaceFirst("#", sqlH);
                    sql = sql.replaceFirst("#", sqlI);
//                    System.out.println("------汇总查询汇总---------" + sql);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if (result.getErrCode() < 0) {
                        this.messageBox("查询失败 " + result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// 个人
                    this.messageBox("个人不能使用汇总查询");
                    return;
                }
            }
            // System.out.println("=================result==============" + result);
            tableM.setParmValue(result);
        }
    }

    /**
     * 校验
     * 
     * @return
     */
    public boolean check() {
        if (StringUtil.isNullString(this.getValueString("START_DATE"))) {
            this.messageBox("开始时间不能为空");
            return false;
        }
        if (StringUtil.isNullString(this.getValueString("END_DATE"))) {
            this.messageBox("结束时间不能为空");
            return false;
        }
        if (StringUtil.isNullString(this.getValueString("ADM_TYPE"))) {
            this.messageBox("就诊类别不能为空");
            return false;
        }
        if (isSelected("SELECT_DEPT")) {
            if (StringUtil.isNullString(this.getValueString("DEPT_CODE"))) {
                this.messageBox("科室不能为空");
                return false;
            }
        }
        if (isSelected("SELECT_MR")) {
            if (StringUtil.isNullString(this.getValueString("MR_NO"))) {
                this.messageBox("病案号不能为空");
                return false;
            }
        }
        return true;
    }

    /**
     * 病案号回车
     */
    public void onMrNo() {
        String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(mrNo));
        this.onQuery();
    }
    
    /**
     * 就诊记录选择
     * @param admType
     * @param mrNo
     * @return
     */
    public String onRecordChoose(String admType, String mrNo) {
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox("E0024");// 初始化参数失败
            return "";
        }
        if (!PatTool.getInstance().existsPat(mrNo)) {
            this.messageBox("E0081");// 查无此病患
            return "";
        }
        TParm parm = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("ADM_TYPE", admType);
        String lastCaseNo = (String) openDialog("%ROOT%\\config\\bil\\BilPatRecordChoose.x", parm);
        if (StringUtil.isNullString(lastCaseNo)) {
            return "";
        }
        if (lastCaseNo.equals("-1")) {
            this.messageBox("不存在就诊信息");
            return "";
        }
        return lastCaseNo;
    }

    /**
     * 调用“病患列表”
     */
    public void showPatList() {
        if (isSelected("SELECT_DEPT")) {// 只在按科室查询时才有效
            TParm inParm = new TParm();
            TParm parmValue = new TParm();
            int row = 0;
            if (getPanel("PANEL_D").isShowing()) {
                parmValue = (TParm) callFunction("UI|TABLE_D|getParmValue");
                row = (Integer) callFunction("UI|TABLE_D|getSelectedRow");// 获取选中行
            } else {
                parmValue = (TParm) callFunction("UI|TABLE_M|getParmValue");
                row = (Integer) callFunction("UI|TABLE_M|getSelectedRow");// 获取选中行
            }
            if (row < 0) {
                return;
            }
            inParm.setData("START_DATE", this.getValue("START_DATE"));
            inParm.setData("END_DATE", this.getValue("END_DATE"));
            inParm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
            inParm.setData("DEPT_CODE", parmValue.getValue("DEPT_CODE", row));
            inParm.setData("REXP_CODE", parmValue.getValue("REXP_CODE", row));//add by wanglong 20130717
            inParm.setData("ORDER_CODE", parmValue.getValue("ORDER_CODE", row));
            inParm.setData("OWN_PRICE", parmValue.getDouble("OWN_PRICE", row));//add by wanglong 20130805
            inParm.setData("OWN_AMT", parmValue.getDouble("OWN_AMT", row)/parmValue.getDouble("DISPENSE_QTY", row));
            inParm.setData("AR_AMT", parmValue.getDouble("AR_AMT", row)/parmValue.getDouble("DISPENSE_QTY", row));
//             System.out.println("====================inParm================" + inParm);
            this.openDialog("%ROOT%\\config\\bil\\BILItemPatList.x", inParm);
        }
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (getPanel("PANEL_D").isShowing()) {// 明细页签
            TParm parm = tableD.getParmValue();
            if (null == parm || parm.getCount("DEPT_CODE") <= 0) {
                this.messageBox("没有需要导出的数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(tableD, "医疗项目统计日(月)结明细表");
        }else{
            TParm parm = tableM.getParmValue();
            if (null == parm || parm.getCount("DEPT_CODE") <= 0) {
                this.messageBox("没有需要导出的数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(tableM, "医疗项目统计日(月)结汇总表");
        }

    }

    /**
     * 打印报表
     */
    public void onPrint() {
        TParm titleValue = new TParm();
        TParm parmValue = new TParm();
        TParm printParm = new TParm();
        if (getPanel("PANEL_D").isShowing()) {// 明细页签
            parmValue = tableD.getShowParmValue();
            if (parmValue == null || parmValue.getCount("CHN_DESC") <= 0) {
                this.messageBox("查无数据");
                return;
            }
            parmValue.setCount(parmValue.getCount("CHN_DESC"));
            parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parmValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            parmValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            parmValue.addData("SYSTEM", "COLUMNS", "AR_AMT");
            titleValue.addData("DEPT_CODE", "科室");
            titleValue.addData("CHN_DESC", "项目");
            titleValue.addData("ORDER_DESC", "项目明细");
            titleValue.addData("OWN_PRICE", "单价");
            titleValue.addData("DISPENSE_QTY", "数量");
            titleValue.addData("UNIT_CODE", "单位");
            titleValue.addData("OWN_AMT", "应收金额");
            titleValue.addData("AR_AMT", "实收金额");
            titleValue.setCount(1);
            titleValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            titleValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            titleValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            titleValue.addData("SYSTEM", "COLUMNS", "AR_AMT");
            printParm.setData("TITLE", "TEXT", "医疗统计明细表");
            printParm.setData("TITLE_D", titleValue.getData());
            printParm.setData("TABLE_D", parmValue.getData());
        } else {
            parmValue = tableM.getShowParmValue();
            if (parmValue == null || parmValue.getCount("CHN_DESC") <= 0) {
                this.messageBox("查无数据");
                return;
            }
            parmValue.setCount(parmValue.getCount("CHN_DESC"));
            parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            parmValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            titleValue.addData("DEPT_CODE", "科室");
            titleValue.addData("CHN_DESC", "项目");
            titleValue.addData("ORDER_CODE", "项目代码");
            titleValue.addData("ORDER_DESC", "项目明细");
            titleValue.addData("DISPENSE_QTY", "数量");
            titleValue.addData("UNIT_CODE", "单位");
            titleValue.setCount(1);
            titleValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            titleValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            printParm.setData("TITLE", "TEXT", "医疗统计汇总表");
            printParm.setData("TITLE_M", titleValue.getData());
            printParm.setData("TABLE_M", parmValue.getData());
        }
        // tableParm.addData("SYSTEM", "COLUMNS", "NS_NOTE");
        String time = this.getText("START_DATE") + " 至 " + this.getText("END_DATE");
        printParm.setData("REPORT_DATE", "TEXT", "统计区间:" + time);
        String date = SystemTool.getInstance().getDate().toString().substring(0, 19);
        printParm.setData("PRINT_DATE", "TEXT", "制表时间: " + date);
        printParm.setData("PRINT_USER", "TEXT", "制表人: " + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILItemFeeDetail.jhw", printParm);
    }
    
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("ADM_TYPE;HOSP_CODE;DEPT_CODE;MR_NO;PAT_NAME");
        tableD.setParmValue(new TParm());
        tableM.setParmValue(new TParm());
        this.initUI();
    }
    
    /**
     * TRadioButton判断是否选中
     * 
     * @param tagName
     * @return
     */
    private boolean isSelected(String tagName) {
        return ((TRadioButton) this.getComponent(tagName)).isSelected();
    }

    /**
     * radio button 选择控制
     */
    public void onSelect2() {
        this.clearText("DEPT_CODE");
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|MR_NO|setEnabled", true);
    }

    /**
     * radio button 选择控制
     */
    public void onSelect1() {
        this.clearValue("MR_NO;PAT_NAME");
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", true);
    }
    
    /**
     * 获得面板组件
     * 
     * @param tagName
     * @return
     */
    public TPanel getPanel(String tagName) {// add by wanglong 20130926
        return (TPanel) this.getComponent(tagName);
    }


    // ====================排序功能begin======================add by wanglong 20130613
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
}

