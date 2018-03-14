package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 体检报到统计 </p>
 * 
 * <p> Description: 体检报到统计 </p>
 * 
 * <p> Copyright: Copyright (c) 2008 </p>
 * 
 * <p> Company: javahis </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class HRMTJBDStatisticsControl extends TControl {// refactor by wanglong 20130120

    private TTable table;
    private TTextFormat contract;// 合同下拉框
    private HRMContractD contractD;// 合同DataStore
    private TTextFormat company;// 团体名称TTextFormat

    /**
     * 初始化方法
     */
    public void onInit() {
        contractD = new HRMContractD();
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        this.clearValue("CONTRACT_CODE;COMPANY_CODE");
        table = (TTable) this.getComponent("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        
        // add by wangb 2016/06/27 团体名称需要根据不同登录角色筛选
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
        
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE").replaceAll("/", "");
        String endDate = this.getText("END_DATE").replaceAll("/", "");
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        TParm result = new TParm();
        
        // add by wangb 2016/06/27 增加角色类型筛选过滤
		String roleType = "";
		String roleTypeFilter = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
			roleTypeFilter = " AND A.ROLE_TYPE IN ('" + roleType + "') ";
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
			roleTypeFilter = " AND A.ROLE_TYPE = '" + roleType + "' ";
		}
        
        if (getRadioButton("MASTER").isSelected()) {// 汇总
            String sql = // modify by wanglong 20130207
                    "SELECT COMPANY_CODE,CONTRACT_CODE,SUM(UNREPORT_NUM) UNREPORT_NUM,SUM(REPORT_NUM) REPORT_NUM,SUM(TOTAL_NUM) TOTAL_NUM "
                            + " FROM (SELECT A.COMPANY_CODE,A.CONTRACT_CODE,SUM(CASE WHEN COVER_FLG <> 'Y' THEN 1 ELSE 0 END) UNREPORT_NUM,"
                            + "              0 REPORT_NUM, COUNT(*) TOTAL_NUM "// 统计未报到的数量，以及总人数
                            + "         FROM HRM_CONTRACTD A "
                            + "        WHERE 1 = 1 "
                            + "  #  @ "
                            + "     GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE "
                            + "        UNION "
                            + "       SELECT A.COMPANY_CODE,A.CONTRACT_CODE,0 UNREPORT_NUM,COUNT(*) REPORT_NUM,0 TOTAL_NUM "
                            + "         FROM HRM_CONTRACTD A "
                            + "        WHERE COVER_FLG = 'Y' "// 统计已报到的数量
                            + "  #  @ "
                            + "     GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE) "
                            + "GROUP BY COMPANY_CODE, CONTRACT_CODE "// 分组后，数量加在一起，就能在一行中同时显示未报到，已报到，总人数
                            + "ORDER BY REPORT_NUM DESC, COMPANY_CODE, CONTRACT_CODE";
            String where1 = "";
            if (!companyCode.equals("")) {
                where1 += " AND A.COMPANY_CODE='" + companyCode + "' ";
            }
            if (!contractCode.equals("")) {
                where1 += " AND A.CONTRACT_CODE='" + contractCode + "' ";
            }
            String where2 = where1 + "";
            where2 +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + startDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + endDate + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";
            sql = sql.replaceFirst("#", where1);
            sql = sql.replaceFirst("#", where2);
            sql = sql.replace("@", roleTypeFilter);
//            System.out.println("------------sql111--------" + sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
        } else {// 明细
            String sql = // modify by wanglong 20130207
                    "SELECT DISTINCT A.SEQ_NO, A.STAFF_NO, A.COMPANY_CODE, A.CONTRACT_CODE, A.PACKAGE_CODE,"
                            + "      A.MR_NO, A.PAT_NAME, A.SEX_CODE, A.IDNO, A.REAL_CHK_DATE AS REPORT_DATE, A.COVER_FLG,"
                            + "      NVL((SELECT CASE WHEN SUM(CASE WHEN C.EXEC_DR_CODE IS NOT NULL THEN 100 WHEN C.EXEC_DR_CODE IS NULL THEN -1 END) > 0 "
                            + "                            THEN 'Y' "
                            + "                       WHEN SUM( CASE WHEN C.EXEC_DR_CODE IS NOT NULL THEN 100 WHEN C.EXEC_DR_CODE IS NULL THEN -1 END) < 0 "
                            + "                            THEN  'N' END "
                            + "             FROM HRM_ORDER C "
                            + "            WHERE B.CASE_NO = C.CASE_NO(+) "
                            + "              AND C.SETMAIN_FLG = 'Y' "
                            + "              AND C.DEPT_ATTRIBUTE = '04'), 'Z') AS FINISH_FLG,"
                            + "      CASE B.BILL_FLG WHEN '1' THEN 'Y' ELSE 'N' END AS BILL_FLG "
                            + "  FROM HRM_CONTRACTD A, HRM_PATADM B "
                            + " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "   AND A.MR_NO = B.MR_NO "
                            + " #  @ "
                            + "   AND A.COVER_FLG = 'Y' "
                            + " ORDER BY A.COVER_FLG DESC NULLS LAST, A.COMPANY_CODE, A.CONTRACT_CODE, A.PACKAGE_CODE, A.SEQ_NO";
            String where = "";
            if (!companyCode.equals("")) {
                where += " AND A.COMPANY_CODE='" + companyCode + "' ";
            }
            if (!contractCode.equals("")) {
                where += " AND A.CONTRACT_CODE='" + contractCode + "' ";
            }
            where +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + startDate + "', 'YYYY/MM/DD') "
                            + "AND TO_DATE( '" + endDate + " 23:59:59', 'YYYY/MM/DD HH24:MI:SS') ";
            sql = sql.replaceFirst("#", where);
            sql = sql.replace("@", roleTypeFilter);
//            System.out.println("------------sql222--------"+sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
        }
        if (result.getErrCode() != 0) {
            this.messageBox("E0035");// 操作失败
            return;
        }
        if (result.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.setValue("COUNT", "");
            this.messageBox("E0116");// 没有数据
            return;
        }
        if (getRadioButton("MASTER").isSelected()) {// 汇总
            if (getCheckBox("NOT_SHOW").isSelected()) {
                int count = result.getCount();
                for (int i = count - 1; i > -1; i--) {
                    if (result.getInt("REPORT_NUM", i) == 0) {
                        result.removeRow(i);
                    }
                }
                result.setCount(result.getCount("REPORT_NUM"));
            }
            int count = result.getCount();
            int unReportAmount = 0;// 未报到人数
            int reportAmount = 0;// 已报到人数
            int totAmount = 0;// 总人数
            for (int i = 0; i < result.getCount(); i++) {
                unReportAmount += result.getInt("UNREPORT_NUM", i);
                reportAmount += result.getInt("REPORT_NUM", i);
                totAmount += result.getInt("TOTAL_NUM", i);
            }
            String[] ParmMapString = result.getNames();
            for (int i = 0; i < ParmMapString.length; i++) {
                result.addData(ParmMapString[i], "");
            }
            result.setCount(count + 1);
            result.setData("CONTRACT_CODE", count, "合计");
            result.setData("UNREPORT_NUM", count, unReportAmount);
            result.setData("REPORT_NUM", count, reportAmount);
            result.setData("TOTAL_NUM", count, totAmount);
            this.setValue("COUNT", totAmount + "");
        } else {// 明细
            if (getCheckBox("NOT_SHOW").isSelected()) {
                int count = result.getCount();
                for (int i = count - 1; i > -1; i--) {
                    if (!result.getValue("COVER_FLG", i).equalsIgnoreCase("Y")) {
                        result.removeRow(i);
                    }
                }
                result.setCount(result.getCount("COVER_FLG"));
            }
            this.setValue("COUNT", result.getCount() + "");
        }
        table.setParmValue(result);
    }

    // /**
    // * 打印方法
    // */
    // public void onPrint() {
    // int ATM = 0;
    // TTable table_d = getTTable("tbl1");
    // if (table_d.getRowCount() > 0) {
    // // 打印数据OPT_USER;COUNT(CASE_NO)
    // TParm date = new TParm();
    // // 表头数据
    // date.setData("Title", "TEXT",
    // Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
    // + "体检报到人数报表");
    // date.setData("createDate", "TEXT",
    // "制表日期: "
    // + SystemTool.getInstance().getDate().toString().substring(0, 10)
    // .replace('-', '/'));
    // date.setData("start_Date", "TEXT",
    // "开始时间: " + start_Date.substring(0, 10).replace('-', '/'));
    // date.setData("end_Date", "TEXT", "结束时间: " + end_Date.substring(0, 10).replace('-', '/'));
    // // 表格数据
    // TParm parm = new TParm();
    // TParm tableParm = table_d.getParmValue();
    // int count = tableParm.getCount();
    // for (int i = 0; i < count; i++) {
    // parm.addData("T2", tableParm.getValue("T2", i));
    // parm.addData("T3", tableParm.getValue("T3", i));
    // parm.addData("T1", tableParm.getValue("T1", i));
    // ATM += StringTool.getInt(tableParm.getValue("T1", i));
    // }
    // parm.setCount(parm.getCount("T1"));
    // parm.addData("SYSTEM", "COLUMNS", "T2");
    // parm.addData("SYSTEM", "COLUMNS", "T3");
    // parm.addData("SYSTEM", "COLUMNS", "T1");
    // date.setData("tbl1", parm.getData());
    // // 表尾数据
    // date.setData("createUser", "TEXT", "制表人: " + Operator.getName());
    // date.setData("pass", "TEXT", "审核人: ");
    // date.setData("ATM", "TEXT", "合计人次：" + ATM);
    // // 调用打印方法
    // this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMTJBDStatistics.jhw", date);
    // } else {
    // this.messageBox("E0010");// 弹出没有打印数据
    // return;
    // }
    // }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (getRadioButton("MASTER").isSelected()) {// 汇总
            ExportExcelUtil.getInstance().exportExcel(table, "报到人次统计报表");
        } else ExportExcelUtil.getInstance().exportExcel(table, "报到人次统计报表明细");
    }

    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.clearValue("CONTRACT_CODE");
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);// 查询相对应合同下拉框的数据
        if (contractParm == null || contractParm.getCount() <= 0 || contractParm.getErrCode() != 0) {
            this.messageBox("没有合同数据");
            return;
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contract.setHisOneNullRow(true);
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox("E0034");// 取得数据错误
            return;
        }
        contract.setValue(contractCode);
    }

    /**
     * 设置表格的属性
     */
    public void onSetTableAttribute() {// add by wanglong 20130120
        if (getRadioButton("MASTER").isSelected()) {// 汇总
            table.setHeader("团体名称,200,COMPANY_COMBO;合同名称,150,CONTRACT_CODE1;未报到人数,120,int;已报到人数,120,int;总人数,70,int");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right");//modify by wanglong 20130207
            table.setParmMap("COMPANY_CODE;CONTRACT_CODE;UNREPORT_NUM;REPORT_NUM;TOTAL_NUM");
        } else {// 细项
            table.setHeader("团体名称,200,COMPANY_COMBO;合同名称,150,CONTRACT_CODE1;套餐名称,150,PACKAGE_CODE;"
                    + "病案号,100;姓名,80;性别,40,SEX_CODE;身份证号,140;报到状态,80,COVER_FLG;总检状态,80,FINISH_FLG;结算状态,80,BILL_FLG");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;4,left;5,left;6,left;7,left;8,left;9,left");
            table.setParmMap("COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;MR_NO;PAT_NAME;SEX_CODE;IDNO;COVER_FLG;FINISH_FLG;BILL_FLG");
        }
    }

    /**
     * 获得单选框组件
     * 
     * @param tagName
     * @return
     */
    public TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) this.getComponent(tagName);
    }
    
    /**
     * 获得复选框组件
     * 
     * @param tagName
     * @return
     */
    public TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) this.getComponent(tagName);
    }

    /**
     * 得到table组件
     * 
     * @param tag
     * @return
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("CONTRACT_CODE;COMPANY_CODE");
        this.setValue("COUNT", "");
        contract.getPopupMenuData().getData().clear();
        contract.filter();
        table.removeRowAll();
        onInit();
    }
}
