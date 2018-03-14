package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p> Title: 就诊记录选择 </p>
 * 
 * <p> Description: 就诊记录选择  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130617
 * @version 1.0
 */
public class BILPatRecordChooseControl extends TControl {
	private static String TABLE = "TABLE";
	private TTable table;

	/**
	 * 初始化方法
	 */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        TParm parm = (TParm) this.getParameter();
        if (parm.getData("ADM_TYPE") == null || parm.getData("MR_NO") == null) {
            messageBox("E0024");// 初始化参数失败
            return;
        }
        String mrNo = parm.getValue("MR_NO");
        String admType = parm.getValue("ADM_TYPE");
        setTableParameter(admType, table);
        TParm result = getAllMedRecord(admType, mrNo);
        if (result.getErrCode() < 0) {
            this.messageBox("查询历次就诊记录失败 " + result.getErrText());
            this.closeWindow();
        }
        table.setParmValue(result);
    }

    /**
     * 取得历次就诊记录
     * @param admType
     * @param mrNo
     * @return
     */
    public TParm getAllMedRecord(String admType, String mrNo) {
        TParm result = new TParm();
        String sql = "";
        if (admType.equals("O")) {
            sql = "SELECT * FROM REG_PATADM WHERE SEE_DR_FLG<>'N' AND MR_NO = '#' ORDER BY ADM_DATE,REG_DATE";
            sql = sql.replaceFirst("#", mrNo);
        } else if (admType.equals("H")) {
            sql = "SELECT * FROM HRM_PATADM WHERE MR_NO = '#' ORDER BY REPORT_DATE";
            sql = sql.replaceFirst("#", mrNo);
        } else if (admType.equals("I")) {
            sql = "SELECT * FROM ADM_INP WHERE MR_NO = '#' ORDER BY IN_DATE";
            sql = sql.replaceFirst("#", mrNo);
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 设置表格的title,parmMap等属性
     * @param admType
     * @param table
     */
    public void setTableParameter(String admType, TTable table) {
        if (admType.equals("O")) {
            table.setHeader("挂号日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;就诊日期,100;开始问诊时间,150,TimeStamp,yyyy/MM/dd HH:mm:ss;看诊科室,100,DEPT_CODE;看诊医生,100,VC_CODE");
            table.setColumnHorizontalAlignmentData("3,left;4,left");
            table.setParmMap("REG_DATE;ADM_DATE;SEEN_DR_TIME;REALDEPT_CODE;REALDR_CODE");
        } else if (admType.equals("H")) {
            table.setHeader("报到日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;报到科室,100,DEPT_CODE;团体名称,160,COMPANY_CODE;合同名称,140,CONTRACT_CODE;套餐名称,140,PACKAGE_CODE");
            table.setColumnHorizontalAlignmentData("1left;2,left;3,left;4,left");
            table.setParmMap("REPORT_DATE;DEPT_CODE;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE");
        } else if (admType.equals("I")) {
            table.setHeader("入院日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;入院科室,100,DEPT_CODE;入院病区,100,STATION_CODE;出院日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;出院科室,90,DEPT_CODE;出院病区,90,STATION_CODE");
            table.setColumnHorizontalAlignmentData("1,left;2,left;4,left;5,left");
            table.setParmMap("IN_DATE;IN_DEPT_CODE;IN_STATION_CODE;DS_DATE;DS_DEPT_CODE;DS_STATION_CODE");
        }
    }

    /**
     * TABLE双击事件
     */
    public void onTableDoubleCliecked() {
        if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
            messageBox("请选择一条记录");
            return;
        }
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        this.setReturnValue(parm.getValue("CASE_NO", row));
        this.closeWindow();
    }
    
}
