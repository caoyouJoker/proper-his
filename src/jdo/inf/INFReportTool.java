package jdo.inf;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: 感控报表工具类</p>
 *
 * <p>Description: 感控报表工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFReportTool extends TJDOTool{

    /**
     * 构造器
     */
    public INFReportTool() {
        setModuleName("inf\\INFReportModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static INFReportTool instanceObject;

    /**
     * 得到实例
     * @return INFReportTool
     */
    public static INFReportTool getInstance() {
        if (instanceObject == null) instanceObject = new INFReportTool();
        return instanceObject;
    }

    /**
     * 查询当月所有科室出院病人病历总数
     * @param parm TParm
     * @return TParm
     */
    public TParm countMroRecord(TParm parm) {
        TParm result = query("countMroRecord", parm);
        return result;
    }

    /**
     * 统计科室感控病历信息
     * @param parm TParm
     * @return TParm
     */
    public TParm countDeptInfCount(TParm parm) {
        TParm result = query("countDeptInfCount", parm);
        return result;
    }

    /**
     * 取得科室总病历数
     * @param parm TParm
     * @return TParm
     */
    public TParm countDeptMroRecord(TParm parm) {
        TParm result = query("countDeptMroRecord", parm);
        return result;
    }

    /**
     * 取得所有临床科室
     * @return TParm
     */
    public TParm selectAllDept(TParm parm) {
        TParm result = query("selectAllDept", parm);
        return result;
    }

    /**
     * 取得科室检测汇总信息
     * @return TParm
     */
    public TParm selectDeptAllGainPoint(TParm parm) {
        TParm result = query("selectDeptAllGainPoint", parm);
        return result;
    }

    /**
     * 取得科室检测项目信息
     * @return TParm
     */
    public TParm selectDeptAllEXMItem(TParm parm) {
        TParm result = query("selectDeptAllEXMItem", parm);
        return result;
    }

    /**
     * 取得所有科室检测信息
     * @return TParm
     */
    public TParm selectYearMonEXMCount(TParm parm) {
        TParm result = query("selectYearMonEXMCount", parm);
        return result;
    }

    /**
     * 取得所有科室统计日期
     * @return TParm
     */
    public TParm selectYearMonEXMDate(TParm parm) {
        TParm result = query("selectYearMonEXMDate", parm);
        return result;
    }

    /**
     * 感染病例月报表
     * @param parm TParm
     * @return TParm
     */
    public TParm countInfCaseMonReport(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        // 查询当月所有科室出院病人病历总数
        TParm recordCount = countMroRecord(parm);
        // 取得所有临床科室
        TParm allDept = selectAllDept(parm);
        // 设置科室名称
        result.addData("DEPT_CHN_DESC", "合计");
        // 设置科室总病历数
        result.addData("COUNT", recordCount.getInt("COUNT", 0));
        // 设置感染病例数
        result.addData("INF_COUNT", "");
        // 设置感染率
        result.addData("INF_RATE", "");
        // 设置构成比
        result.addData("CONSTRUCT_RATE", "100.00");
        // 设置实报感染病历数
        result.addData("REPORT_COUNT", "");
        // 设置实报感染率
        result.addData("REPORT_RATE", "");
        // 设置漏报数
        result.addData("NO_REPORT_COUNT", "");
        // 设置漏报率
        result.addData("NO_REPORT_RATE", "");
        // 设置送检病例数
        result.addData("ETIOLGEXM_COUNT", "");
        // 设置送检率
        result.addData("ETIOLGEXM_RATE", "");
        // 总感染例数
        int allInfCount = 0;
        // 总实报感染例数
        int allReportCount = 0;
        // 总送检数
        int allEtiolgexmCount = 0;
        for (int i = 0; i < allDept.getCount(); i++) {
            parm.setData("DEPT_CODE", allDept.getData("DEPT_CODE", i));
            // 取得科室总病历数
            TParm recordDeptCount = countDeptMroRecord(parm);
            // 取得科室感控病历信息
            TParm deptInfCount = countDeptInfCount(parm);
            // 设置科室名称
            result.addData("DEPT_CHN_DESC", allDept.getData("DEPT_CHN_DESC", i));
            // 设置科室总病历数
            result.addData("COUNT", recordDeptCount.getInt("COUNT", 0));
            // 设置感染病例数
            result.addData("INF_COUNT", deptInfCount.getInt("INF_COUNT", 0));
            // 设置感染率(感染例数/科室病历总数)
            if (recordDeptCount.getDouble("COUNT", 0) != 0) result.addData("INF_RATE", df
                    .format(deptInfCount.getDouble("INF_COUNT", 0)
                            / recordDeptCount.getDouble("COUNT", 0) * 100));
            else result.addData("INF_RATE", "0.00");
            // 设置构成比(科室病历总数/所有科室病历总数)
            if (recordCount.getDouble("COUNT", 0) != 0) result.addData("CONSTRUCT_RATE", df
                    .format(recordDeptCount.getDouble("COUNT", 0)
                            / recordCount.getDouble("COUNT", 0) * 100));
            else result.addData("CONSTRUCT_RATE", "0.00");
            // 设置实报感染病历数
            result.addData("REPORT_COUNT", deptInfCount.getInt("REPORT_COUNT", 0));
            // 设置实报感染率(实报感染例数/科室病历总数)
            if (recordDeptCount.getDouble("COUNT", 0) != 0) result.addData("REPORT_RATE", df
                    .format(deptInfCount.getDouble("REPORT_COUNT", 0)
                            / recordDeptCount.getDouble("COUNT", 0) * 100));
            else result.addData("REPORT_RATE", "0.00");
            // 设置漏报数(感染例数-实报感染例数)
            result.addData("NO_REPORT_COUNT",
                           deptInfCount.getInt("INF_COUNT", 0)
                                   - deptInfCount.getInt("REPORT_COUNT", 0));
            // 设置漏报率(漏报数/感染例数)
            if (deptInfCount.getDouble("INF_COUNT", 0) != 0) result.addData("NO_REPORT_RATE", df
                    .format((deptInfCount.getDouble("INF_COUNT", 0) - deptInfCount
                            .getDouble("REPORT_COUNT", 0))
                            / deptInfCount.getDouble("INF_COUNT", 0) * 100));
            else result.addData("NO_REPORT_RATE", "0.00");
            // 设置送检病例数
            result.addData("ETIOLGEXM_COUNT", deptInfCount.getInt("ETIOLGEXM_COUNT", 0));
            // 设置送检率(送检例数/感染例数)
            if (deptInfCount.getDouble("INF_COUNT", 0) != 0) result.addData("ETIOLGEXM_RATE", df
                    .format(deptInfCount.getDouble("ETIOLGEXM_COUNT", 0)
                            / deptInfCount.getDouble("INF_COUNT", 0) * 100));
            else result.addData("ETIOLGEXM_RATE", "0.00");
            allInfCount += deptInfCount.getInt("INF_COUNT", 0);
            allReportCount += deptInfCount.getInt("REPORT_COUNT", 0);
            allEtiolgexmCount += deptInfCount.getInt("ETIOLGEXM_COUNT", 0);
        }
        // 设置合计相应数据
        result.setData("INF_COUNT", 0, allInfCount);
        if (recordCount.getInt("COUNT", 0) != 0) result.setData("INF_RATE", 0, df
                .format(allInfCount / recordCount.getDouble("COUNT", 0) * 100));
        else result.setData("INF_RATE", 0, "0.00");
        if (allInfCount == 0) result.setData("CONSTRUCT_RATE", 0, "0.00");
        result.setData("REPORT_COUNT", 0, allReportCount);
        if (recordCount.getInt("COUNT", 0) != 0) result.setData("REPORT_RATE", 0, df
                .format(allReportCount / recordCount.getDouble("COUNT", 0) * 100));
        else result.setData("REPORT_RATE", 0, "0.00");
        result.setData("NO_REPORT_COUNT", 0, allInfCount - allReportCount);
        if (allInfCount != 0) result.setData("NO_REPORT_RATE",
                                             0,
                                             df.format((allInfCount - allReportCount)
                                                     / (double) allInfCount * 100));
        else result.setData("NO_REPORT_RATE", 0, "0.00");
        result.setData("ETIOLGEXM_COUNT", 0, allEtiolgexmCount);
        if (allInfCount != 0) result.setData("ETIOLGEXM_RATE",
                                             0,
                                             df.format(allEtiolgexmCount / (double) allInfCount
                                                     * 100));
        else result.setData("ETIOLGEXM_RATE", 0, "0.00");
        result.setCount(result.getCount("INF_COUNT"));
        // 设置科室名称
        result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        // 设置科室总病历数
        result.addData("SYSTEM", "COLUMNS", "COUNT");
        // 设置感染病例数
        result.addData("SYSTEM", "COLUMNS", "INF_COUNT");
        // 设置感染率
        result.addData("SYSTEM", "COLUMNS", "INF_RATE");
        // 设置构成比
        result.addData("SYSTEM", "COLUMNS", "CONSTRUCT_RATE");
        // 设置实报感染病历数
        result.addData("SYSTEM", "COLUMNS", "REPORT_COUNT");
        // 设置实报感染率
        result.addData("SYSTEM", "COLUMNS", "REPORT_RATE");
        // 设置漏报数
        result.addData("SYSTEM", "COLUMNS", "NO_REPORT_COUNT");
        // 设置漏报率
        result.addData("SYSTEM", "COLUMNS", "NO_REPORT_RATE");
        // 设置送检病例数
        result.addData("SYSTEM", "COLUMNS", "ETIOLGEXM_COUNT");
        // 设置送检率
        result.addData("SYSTEM", "COLUMNS", "ETIOLGEXM_RATE");
        TParm printParm = new TParm();
        printParm.setData("MONTH", parm.getValue("DATE").substring(4, 6));
        printParm.setData("DATE",
                          parm.getValue("DATE").substring(0, 4) + "年"
                                  + parm.getValue("DATE").substring(4, 6) + "月");
        printParm.setData("OPERATOR", Operator.getName());
        printParm.setData("INF_DEPT_COUNT", result.getData());
        return printParm;
    }

    /**
     * 发热日报
     * @param parm TParm
     * @return TParm
     */
    public TParm selectHeatDayReport(TParm parm) {
        TParm result = new TParm();
        result = query("selectHeatDayReport", parm);
        // 病区
        result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        // 姓名
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        // 病历号
        result.addData("SYSTEM", "COLUMNS", "MR_NO");
        // 手术日期
        result.addData("SYSTEM", "COLUMNS", "OP_DATE");
        // 体温
        result.addData("SYSTEM", "COLUMNS", "TEMPERATURE");
        TParm printParm = new TParm();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        printParm.setData("TABLE", result.getData());
        printParm.setData("EXAMINE_DATE",
                          parm.getValue("EXAMINE_DATE").substring(0, 4) + "年"
                                  + parm.getValue("EXAMINE_DATE").substring(4, 6) + "月"
                                  + parm.getValue("EXAMINE_DATE").substring(6, 8) + "日");
        printParm.setData("PRINT_DATE", timestamp.toString().substring(0, 4) + "年"
                + timestamp.toString().substring(5, 7) + "月"
                + timestamp.toString().substring(8, 10) + "日");
        printParm.setData("PRINT_USER", Operator.getName());
        return printParm;
    }

    /**
     * 取得病患抗生素不合理使用信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfAntibiotrcd(TParm parm) {
        TParm result = new TParm();
        result = query("selectInfAntibiotrcd", parm);
        // 科室
        result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        // 病区
        result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        // 医生姓名
        result.addData("SYSTEM", "COLUMNS", "USER_NAME");
        // 病人姓名
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        // 体温
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        // 用量
        result.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
        // 起停用日期
        result.addData("SYSTEM", "COLUMNS", "SE_DATE");
        // 不合理原因
        result.addData("SYSTEM", "COLUMNS", "ILLEGIT_REMARK");
        // 不良反映
        result.addData("SYSTEM", "COLUMNS", "MEDALLERG_SYMP");
        TParm printParm = new TParm();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        printParm.setData("TABLE", result.getData());
        printParm.setData("PRINT_DATE", timestamp.toString().substring(0, 4) + "年"
                + timestamp.toString().substring(5, 7) + "月"
                + timestamp.toString().substring(8, 10) + "日");
        printParm.setData("PRINT_USER", Operator.getName());
        return printParm;
    }

    /**
     * 医院感染病例检测汇总表1
     * @param parm TParm
     * @return TParm
     */
    public TParm selestInfCaseForReport(TParm parm) {
        TParm result = new TParm();
        result = query("selestInfCaseForReport", parm);
        TParm dataParm = new TParm();
        String deptCode = "";
        String mrNo = "";
        if (result.getCount() > 0) {
            deptCode = result.getValue("DEPT_CODE", 0);
            mrNo = result.getValue("MR_NO", 0);
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (!deptCode.equals(result.getValue("DEPT_CODE", i)) || i == 0) {
                deptCode = result.getValue("DEPT_CODE", i);
                mrNo = result.getValue("MR_NO", i);
                // 设置科室名称
                dataParm.addData("DEPT_DESC", result.getData("DEPT_CHN_DESC", i));
                // 出院人数
                if (result.getData("DS_DATE", i) != null
                        && result.getValue("DS_DATE", i).length() != 0) dataParm
                        .addData("DS_COUNT", 1);
                else dataParm.addData("DS_COUNT", 0);
                // 感染人数
                dataParm.addData("INF_PAT_COUNT", 1);
                // 感染次数数
                dataParm.addData("INF_COUNT", 1);
                // 感染部位
                for (int j = 1; j < 14; j++) {
                    if (j == result.getInt("INFPOSITION_CODE", i)) {
                        dataParm.addData("POSITION_" + "00".substring(0, 2 - ("" + j).length()) + j,
                                         1);
                        continue;
                    }
                    dataParm.addData("POSITION_" + "00".substring(0, 2 - ("" + j).length()) + j, 0);
                }
                // 死亡人数
                if ("04".equals(result.getData("CODE1_STATUS", i))) dataParm
                        .addData("DIE_COUNT", 1);
                else dataParm.addData("DIE_COUNT", 0);
                // 对死亡影响
                if ("4".equals(result.getData("DIEINFLU_CODE", i))) dataParm.addData("DIE_04", 1);
                else dataParm.addData("DIE_04", 0);
                if ("2".equals(result.getData("DIEINFLU_CODE", i))
                        || "3".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .addData("DIE_02", 1);
                else dataParm.addData("DIE_02", 0);
                if ("1".equals(result.getData("DIEINFLU_CODE", i))) dataParm.addData("DIE_01", 1);
                else dataParm.addData("DIE_01", 0);
            } else {
                int row = dataParm.getCount("DEPT_DESC") - 1;
                // 出院人数
                if (!mrNo.equals(result.getValue("MR_NO", i))
                        && result.getData("DS_DATE", i) != null
                        && result.getValue("DS_DATE", i).length() != 0) dataParm
                        .setData("DS_COUNT", row, dataParm.getInt("DS_COUNT", row) + 1);
                // 感染人数
                if (!mrNo.equals(result.getValue("MR_NO", i))) dataParm
                        .setData("INF_PAT_COUNT", row, dataParm.getInt("INF_COUNT", row) + 1);
                // 感染次数
                dataParm.setData("INF_COUNT", row, dataParm.getInt("INF_COUNT", row) + 1);
                // 感染部位
                dataParm.setData("POSITION_" + result.getData("INFPOSITION_CODE", i), row, dataParm
                        .getInt("POSITION_" + result.getData("INFPOSITION_CODE", i), row) + 1);
                // 死亡人数
                if (!mrNo.equals(result.getValue("MR_NO", i))) {
                    dataParm.setData("DIE_COUNT", row, dataParm.getInt("DIE_COUNT", row) + 1);
                    mrNo = result.getValue("MR_NO", i);
                }
                // 对死亡影响
                if ("4".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_04", row, dataParm.getInt("DIE_04", row) + 1);
                if ("2".equals(result.getData("DIEINFLU_CODE", i))
                        || "3".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_02", row, dataParm.getInt("DIE_02", row) + 1);
                if ("1".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_01", row, dataParm.getInt("DIE_01", row) + 1);
            }
        }
        TParm printParm = new TParm();
        dataParm.setCount(dataParm.getCount("DEPT_DESC"));
        dataParm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        dataParm.addData("SYSTEM", "COLUMNS", "DS_COUNT");
        dataParm.addData("SYSTEM", "COLUMNS", "INF_PAT_COUNT");
        dataParm.addData("SYSTEM", "COLUMNS", "INF_COUNT");
        for (int j = 1; j < 14; j++) {
            dataParm.addData("SYSTEM", "COLUMNS",
                             "POSITION_" + "00".substring(0, 2 - ("" + j).length()) + j);
        }
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_04");
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_02");
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_01");
        printParm.setData("TABLE", dataParm.getData());
        printParm.setData("EXM_DATE", parm.getValue("INF_DATE").substring(0, 4) + "年"
                + parm.getValue("INF_DATE").substring(4, 6) + "月");
        printParm.setData("PRINT_USER", Operator.getName());
        printParm.setData("HOSP_NAME", Operator.getHospitalCHNFullName());
        return printParm;
    }

    /**
     * 科室统计查询按日期
     * @param parm TParm
     * @return TParm
     */
    public TParm deptEXMStatisticsDate(TParm parm) {
        TParm result = new TParm();
        TParm point = selectDeptAllGainPoint(parm);
        if (point.getCount() <= 0) return result;
        TParm item = selectDeptAllEXMItem(parm);
        if (item.getCount() <= 0) return result;
        String dept = point.getValue("DEPT_CODE", 0);
        for (int i = 0; i < point.getCount(); i++) {
            if (point.getValue("DEPT_CODE", i).equals(dept) && i != 0) result
                    .addData("DEPT_CHN_DESC", "");
            else {
                result.addData("DEPT_CHN_DESC", point.getValue("DEPT_CHN_DESC", i));
                dept = point.getValue("DEPT_CODE", i);
            }
            result.addData("EXAM_DATE", point.getValue("EXAM_DATE", i));
            result.addData("ITEM_GAINPOINT", point.getValue("ITEM_GAINPOINT", i));
            for (int j = 0; j < item.getCount(); j++) {
                if (!dept.equals(item.getData("DEPT_CODE", j))
                        || !point.getValue("EXAM_NO", i).equals(item.getValue("EXAM_NO", j))
                        || !point.getValue("EXAM_DATE", i).equals(item.getValue("EXAM_DATE", j))) continue;
                result.addData("CHN_DESC", item.getValue("CHN_DESC", j));
                result.addData("PASS_FLG", item.getValue("PASS_FLG", j));
                result.addData("REMARK", item.getValue("REMARK", j));
                if (result.getCount("EXAM_DATE") < result.getCount("CHN_DESC")) {
                    result.addData("DEPT_CHN_DESC", "");
                    result.addData("EXAM_DATE", "");
                    result.addData("ITEM_GAINPOINT", "");
                }
            }
        }
        return result;
    }

    /**
     * 取得科室年度检测信息
     * @return TParm
     */
    public TParm selectDeptMonCount(TParm parm) {
        TParm result = query("selectDeptMonCount", parm);
        if (result.getCount() <= 0) return result;
        DecimalFormat df = new DecimalFormat("0.00");
        int pointMon = 0;
        String month = result.getValue("EXAM_MONTH", 0);
        int loc = 0;
        int count = 0;
        for (int i = 0; i < result.getCount(); i++) {
            if (month.equals(result.getValue("EXAM_MONTH", i))) {
                pointMon += result.getInt("ITEM_GAINPOINT", i);
                count++;
            } else {
                result.setData("ITEM_GAINPOINT_AVERAGE", loc, df.format(pointMon / count));
                month = result.getValue("EXAM_MONTH", i);
                loc = i;
                pointMon = result.getInt("ITEM_GAINPOINT", i);
                count = 1;
            }
            if (i == result.getCount() - 1) result.setData("ITEM_GAINPOINT_AVERAGE", loc,
                                                           df.format(pointMon / count));
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (result.getValue("ITEM_GAINPOINT_AVERAGE", i).length() != 0) continue;
            result.setData("EXAM_MONTH", i, "");
        }
        return result;
    }

    /**
     * 按照月份取得所有科室检测信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectYearMonEXMStatistics(TParm parm) {
        TParm date = selectYearMonEXMDate(parm);
        if (date.getCount() <= 0) return date;
        DecimalFormat df = new DecimalFormat("0.00");
        TParm point = selectYearMonEXMCount(parm);
        for (int i = 0; i < point.getCount(); i++) {
            String dateAll = parm.getValue("YEAR_MONTH").substring(4, 6) + "/";
            int count = 0;
            for (int j = 0; j < date.getCount(); j++) {
                if (!point.getValue("DEPT_CODE", i).equals(date.getValue("DEPT_CODE", j))) continue;
                if (dateAll.length() != 3) dateAll += ",";
                dateAll += date.getData("EXAM_DATE", j);
                count++;
            }
            point.setData("DATE_ALL", i, dateAll);
            point.setData("ITEM_GAINPOINT", i, df.format(point.getInt("ITEM_GAINPOINT", i) / count));
        }
        return point;
    }
}
