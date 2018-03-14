package com.javahis.ui.hrm;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.hrm.HRMCompanyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:检验检查人数统计 </p>
 *
 * <p>Description:检验检查人数统计 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class HRMLisANDRISStatisticsControl
        extends TControl {

    private String start_Date;
    private String end_Date;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 得到当前时间
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("end_Date", date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("start_Date", StringTool.rollDate(date, -7).toString().substring(0, 10)
                .replace('-', '/'));
        setValue("PURORDER_DATE", date);
        // this.onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        start_Date =
                this.getValue("start_Date").toString().substring(0, 10).replace('-', '/')
                        + " 00:00:00";
        end_Date =
                this.getValue("end_Date").toString().substring(0, 10).replace('-', '/')
                        + " 23:59:59";
        // add by wangb 2016/06/27 增加角色类型筛选过滤
		String roleType = "";
		String roleTypeFilter = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
			roleTypeFilter = " AND B.ROLE_TYPE in ('" + roleType + "') ";
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
			roleTypeFilter = " AND B.ROLE_TYPE = '" + roleType + "' ";
		}
        
        String sql =
                "  SELECT CAT1_TYPE, COUNT(CASE_NO) " + " FROM HRM_ORDER A, HRM_CONTRACTD B "
                        + " WHERE CAT1_TYPE IS NOT NULL " + "AND SETMAIN_FLG = 'Y'"//modify by wanglong 20130503
                        + " AND ORDER_DATE BETWEEN TO_DATE('" + start_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " AND TO_DATE('" + end_Date
                        + "','YYYY-MM-DD HH24:MI:SS') "
                        + " AND A.CONTRACT_CODE = B.CONTRACT_CODE AND A.MR_NO = B.MR_NO "
                        + roleTypeFilter
                        + " GROUP BY CAT1_TYPE";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount() < 1) {
            this.messageBox("E0008");// 没有数据
        } else {
            this.getTTable("tbl1").setParmValue(result);
        }
    }
    
    /**
     * 明细
     */
    public void onDetial() {
        TTable table = (TTable) this.getComponent("tbl1");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请点选行数据!");
            return;
        }
        TParm tableParm = table.getParmValue();
        String catType = tableParm.getValue("CAT1_TYPE", row);
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", catType);
        parm.setData("start_Date", start_Date);
        parm.setData("end_Date", end_Date);
        this.openDialog("%ROOT%\\config\\hrm\\HRMCJBloodDIS.x", parm);
    }
    
    /**
     * 打印方法
     */
    public void onPrint() {
        int ATM = 0;
        TTable table = getTTable("tbl1");
        if (table.getRowCount() > 0) {
            // 打印数据OPT_USER;COUNT(CASE_NO)
            TParm date = new TParm();
            // 表头数据
            date.setData("Title", "TEXT",
                         Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
                                 + "检验检查人数统计报表");
            date.setData("createDate", "TEXT",
                         "制表日期: "
                                 + SystemTool.getInstance().getDate().toString().substring(0, 10)
                                         .replace('-', '/'));
            date.setData("start_Date", "TEXT",
                         "开始时间: " + start_Date.substring(0, 10).replace('-', '/'));
            date.setData("end_Date", "TEXT", "结束时间: " + end_Date.substring(0, 10).replace('-', '/'));
            // 表格数据 B.CHN_DESC, COUNT (A.CASE_NO)
            TParm parm = new TParm();
            TParm tableParm = table.getParmValue();
            // System.out.println("tableParm====" + tableParm);
            int count = tableParm.getCount();
            for (int i = 0; i < count; i++) {
                parm.addData("CAT1_TYPE", tableParm.getValue("CAT1_TYPE", i));
                parm.addData("COUNT(CASE_NO)", tableParm.getValue("COUNT(CASE_NO)", i));
                ATM += StringTool.getInt(tableParm.getValue("COUNT(CASE_NO)", i));
            }
            parm.setCount(parm.getCount("COUNT(CASE_NO)"));
            // System.out.println("parm====" + parm);
            parm.addData("SYSTEM", "COLUMNS", "CAT1_TYPE");
            parm.addData("SYSTEM", "COLUMNS", "COUNT(CASE_NO)");
            date.setData("tbl1", parm.getData());
            // 表尾数据
            date.setData("createUser", "TEXT", "制表人: " + Operator.getName());
            date.setData("pass", "TEXT", "审核人: ");
            date.setData("ATM", "TEXT", "总计人数：" + ATM);
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMLisANDRISStatistics.jhw", date);
        } else {
            this.messageBox("E0010");// 弹出提示对话框（“没打印有数据”）
            return;
        }
    }
    
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        // 得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table_d = getTTable("tbl1");
        ExportExcelUtil.getInstance().exportExcel(table_d, "检验检查人数统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        this.getTTable("tbl1").setSelectionMode(0);
        this.getTTable("tbl1").removeRowAll();
        onInit();
    }


}
