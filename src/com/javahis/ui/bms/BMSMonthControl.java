package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.bms.BMSBloodTool;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/**
 * <p>Title: 血库用血月报表</p>
 *
 * <p>Description: 血库用血月报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: zhangy 2010.10.9</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSMonthControl
    extends TControl {
    public BMSMonthControl() {
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        TParm parm = new TParm();
        String dept_code = this.getValueString("DEPT_CODE");
        if (dept_code != null && dept_code.length() > 0) {
            parm.setData("DEPT_CODE", dept_code);
        }
        String station_code = this.getValueString("STATION_CODE");
        if (station_code != null && station_code.length() > 0) {
            parm.setData("STATION_CODE", station_code);
        }
        String start_date = this.getValueString("START_DATE");
        if (start_date != null && start_date.length() > 0) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
        }
        String end_date = this.getValueString("END_DATE");
        if (end_date != null && end_date.length() > 0) {
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        String bld_code = this.getValueString("BLD_CODE");
        if (bld_code != null && bld_code.length() > 0) {
            parm.setData("BLD_CODE", bld_code);
        }
        TParm result = BMSBloodTool.getInstance().onQueryMonthStock(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        TParm resultParm = getParmData(result);
        //System.out.println("resultParm---"+resultParm);
        table.setParmValue(resultParm);
    }

    /**
     * 清空方法
     */
    public void onClear(){
        String clearStr = "DEPT_CODE;STATION_CODE;BLD_CODE;START_DATE;END_DATE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        table = getTable("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getParmData(TParm parm) {
        TParm result = new TParm();
        String[] tableData = {
            "DEPT_CHN_DESC", "STATION_DESC", "BLD_CODE_01", "BLD_CODE_02",
            "BLD_CODE_03", "BLD_CODE_04", "BLD_CODE_05", "BLD_CODE_06",
            "BLD_CODE_07", "BLD_CODE_08", "BLD_CODE_09"};
        for (int i = 0; i < tableData.length; i++) {
            result.setData(tableData[i], new Vector());
        }

        Map map = new HashMap();
        String dept_desc = "";
        String station_desc = "";
        String bld_code = "";
        double blood_vol = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            dept_desc = parm.getValue("DEPT_CHN_DESC", i);
            station_desc = parm.getValue("STATION_DESC", i);
            bld_code = parm.getValue("BLD_CODE", i);
            blood_vol = parm.getDouble("BLOOD_VOL", i);
            if (map.isEmpty()) {
                map.put(dept_desc + station_desc, 0);
                result.addData("DEPT_CHN_DESC", dept_desc);
                result.addData("STATION_DESC", station_desc);
                result.addData("BLD_CODE_" + bld_code, blood_vol);
                setBloodVolZero(result, "BLD_CODE_" + bld_code, 0);
            }
            else {
                if (map.containsKey(dept_desc + station_desc)) {
                    int row = ( (Integer) map.get(dept_desc + station_desc)).
                        intValue();
                    result.setData("BLD_CODE_" + bld_code, row, blood_vol);
                }
                else {
                    int row = result.getCount("DEPT_CHN_DESC");
                    result.insertRow();
                    map.put(dept_desc + station_desc, row);
                    result.setData("DEPT_CHN_DESC", row, dept_desc);
                    result.setData("STATION_DESC", row, station_desc);
                    result.setData("BLD_CODE_" + bld_code, row, blood_vol);
                    setBloodVolZero(result, "BLD_CODE_" + bld_code, row);
                }
            }

        }
        return result;
    }

    /**
     * 设定BloodVol为0
     * @param parm TParm
     * @return TParm
     */
    private TParm setBloodVolZero(TParm parm, String bld_code, int row) {
        String[] bld_str = {
            "BLD_CODE_01", "BLD_CODE_02", "BLD_CODE_03", "BLD_CODE_04",
            "BLD_CODE_05", "BLD_CODE_06", "BLD_CODE_07", "BLD_CODE_08",
            "BLD_CODE_09"};
        for (int i = 0; i < bld_str.length; i++) {
            if (bld_str[i].equals(bld_code)) {
                continue;
            }
            else {
                parm.setData(bld_str[i], row, 0);
            }
        }
        return parm;
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
