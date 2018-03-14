package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import jdo.sta.STAWorkLogTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: 挂号收入统计报表</p>
 *
 * <p>Description: 挂号收入统计报表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.06.16
 * @version 1.0
 */
public class REGSummaryPersonControl
    extends TControl {
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

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_TIME", yesterday);
        setValue("E_TIME", SystemTool.getInstance().getDate());
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

        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
//        System.out.println("打印数据==>:"+printData);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("Title", "挂号收入统计表");
        parm.setData("S_TIME", sDate);
        parm.setData("E_TIME", eDate);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("summarytable", printData.getData());
//        this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGSummary.jhw", parm);
        this.openPrintWindow("%ROOT%\\config\\prt\\REGSummary.jhw", parm);

    }
    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm oFeeDate = new TParm();
        oFeeDate = PatAdmTool.getInstance().selSummaryPersonO(startTime,
            endTime);
        int oFeeDateCount = oFeeDate.getCount();
        TParm eFeeDate = new TParm();
        eFeeDate = PatAdmTool.getInstance().selSummaryPersonE(startTime,
            endTime);
        int eFeeDateCount = eFeeDate.getCount();
        TParm retrunFeeDate = new TParm();
        retrunFeeDate = PatAdmTool.getInstance().selSummaryPersonReturn(
            startTime, endTime);
        int retrunFeeDateCount = retrunFeeDate.getCount();
        //获取所有1，2，3级科室 作为报表的左边部门列
        TParm DeptList = STAWorkLogTool.getInstance().selectDeptList();
        //获取所有STA_DAILY_02表数据（以四级科室为基础的数据）
        TParm printData = new TParm(); //打印数据
        for (int i = 0; i < DeptList.getCount(); i++) {
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i); //部门等级
            String d_CODE = DeptList.getValue("DEPT_CODE", i); //中间部门CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i); //中间部门名称
            int subIndex = 0; //记录根据科室级别要截取CODE的长度
            //如果是一级科室 code长度为1
            if (d_LEVEL.equals("1")) {
                subIndex = 1;
            }
            //如果是二级科室 code长度为3
            else if (d_LEVEL.equals("2")) {
                subIndex = 3;
                d_DESC = " " + d_DESC; //加入前方空格
            }
            //如果是三级科室 code长度为5
            else if (d_LEVEL.equals("3")) {
                subIndex = 5;
                d_DESC = "  " + d_DESC; //加入前方空格
            }
            /*定义变量 用来累加子部门的数值 初始值为-1，如果用于累加的数据为null那么变量始终为-1，
                      那个插入该变量所属字段的时候就插入null*/
            double regFeeO = 0;
            double clinicFeeO = 0;
            int countPersonO = 0;
            double regFeeE = 0;
            double clinicFeeE = 0;
            int countPersonE = 0;
            int countPersonReturn = 0;
            double returnFee = 0;
            int totPerson = 0;
            double totFee = 0;

            //循环遍历数据 取出符合条件的部门的数据进行累加
            for (int j = 0; j < oFeeDateCount; j++) {
                //如果部门id截取了指定长度后 等于外层循环中的部门CODE那么就是外层循环的子部门，就进行累加
//                System.out.println("截取索引："+subIndex);
//                System.out.println("截取部门CODE："+reData.getValue("DEPT_CODE",j).substring(0,subIndex));
//                System.out.println("1，2级部门："+d_CODE);
                if (oFeeDate.getValue("DEPT_CODE", j).substring(0, subIndex).
                    equals(d_CODE)) {
//                    System.out.println("循环次数:"+i);
                    regFeeO += oFeeDate.getDouble("REG_FEE", j);
                    clinicFeeO += oFeeDate.getDouble("CLINIC_FEE", j);
                    countPersonO += oFeeDate.getInt("COUNT", j);
                }
            }
            for (int k = 0; k < eFeeDateCount; k++) {
                if (eFeeDate.getValue("DEPT_CODE", k).substring(0, subIndex).
                    equals(d_CODE)) {
                    regFeeE += eFeeDate.getDouble("REG_FEE", k);
                    clinicFeeE += eFeeDate.getDouble("CLINIC_FEE", k);
                    countPersonE += eFeeDate.getInt("COUNT", k);
                }
            }
//            System.out.println("第" + i + "长度" + subIndex);
            for (int h = 0; h < retrunFeeDateCount; h++) {
                if (retrunFeeDate.getValue("DEPT_CODE", h).substring(0,
                    subIndex).equals(d_CODE)) {
                    returnFee += retrunFeeDate.getDouble("RETURN_FEE", h);
                    countPersonReturn += retrunFeeDate.getInt("COUNT", h);
                }
            }
            totPerson = countPersonO + countPersonE + countPersonReturn;
            totFee = regFeeO + clinicFeeO + regFeeE + clinicFeeE + returnFee;
            printData.addData("DEPT_DESC", d_DESC);
            printData.addData("REG_FEE_O", regFeeO == 0 ? "" : df.format(regFeeO));
            printData.addData("CLINIC_FEE_O", clinicFeeO == 0 ? "" : df.format(clinicFeeO));
            printData.addData("COUNT_PERSON_O",
                              countPersonO == 0 ? "" : countPersonO);
            printData.addData("REG_FEE_E", regFeeE == 0 ? "" : df.format(regFeeE));
            printData.addData("CLINIC_FEE_E", clinicFeeE == 0 ? "" : df.format(clinicFeeE));
            printData.addData("COUNT_PERSON_E",
                              countPersonE == 0 ? "" : countPersonE);
            printData.addData("RETURN_FEE", returnFee == 0 ? "" : df.format(returnFee));
            printData.addData("TOT_PERSON", totPerson == 0 ? "" : totPerson);
            printData.addData("TOT_FEE", totFee == 0 ? "" : df.format(totFee));
        }
//        System.out.println(""+printData);
        printData.setCount(DeptList.getCount());
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "REG_FEE_O");
        printData.addData("SYSTEM", "COLUMNS", "CLINIC_FEE_O");
        printData.addData("SYSTEM", "COLUMNS", "COUNT_PERSON_O");
        printData.addData("SYSTEM", "COLUMNS", "REG_FEE_E");
        printData.addData("SYSTEM", "COLUMNS", "CLINIC_FEE_E");
        printData.addData("SYSTEM", "COLUMNS", "COUNT_PERSON_E");
        printData.addData("SYSTEM", "COLUMNS", "RETURN_FEE");
        printData.addData("SYSTEM", "COLUMNS", "TOT_PERSON");
        printData.addData("SYSTEM", "COLUMNS", "TOT_FEE");
        return printData;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
        if (printData.getErrCode() < 0) {
            messageBox(printData.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", printData);

    }
    /**
     * 汇出Excel
     */
    public void onExcel() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table,"挂号收入统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();

    }


}
