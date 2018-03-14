package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.javahis.system.combo.TComboOrgCode;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.ui.TComboBox;
import jdo.spc.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 日结/月结报表Control
 * </p>
 *
 * <p>
 * Description: 日结/月结报表Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDMonthControl
    extends TControl {

    // 全部部门权限
    private boolean dept_flg = true;

    public INDMonthControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }
 
    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("请选择查询部门");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        if (getRadioButton("TYPE_A").isSelected()) {
            parm.setData("ORG_TYPE", "A");
            parm.setData("TITLE", "药库日/月结报表");
        }
        else {
            parm.setData("ORG_TYPE", "B");
            parm.setData("TITLE", "药房日/月结报表");
        }
        if (getRadioButton("DAY_TYPE").isSelected()) {
            if (TypeTool.getTimestamp(this.getValue("START_DATE")).compareTo(
                TypeTool.getTimestamp(this.getValue("END_DATE"))) > 0) {
                this.messageBox("统计时间错误");
                return;
            }
            // 日结报表
            parm.setData("START_DATE",
                         this.getValueString("START_DATE").substring(0, 10).
                         replaceAll("-", ""));
            parm.setData("END_DATE",
                         this.getValueString("END_DATE").substring(0, 10).
                         replaceAll("-", ""));
        }
        else {
            // 月结报表
            String month_area = this.getValueString("MONTH_AREA");
            String year = month_area.substring(0, 4);
            String month = month_area.substring(5, 7);		
            //luhai modify 处理打印月结报表时的开始和结束时间  begin
//            parm.setData("START_DATE", year + month + "01");
//            if ("12".equals(month)) {
//                year = TypeTool.getString(TypeTool.getInt(year) + 1);
//                month = "01";
//            }
//            else {
//                month = TypeTool.getString(TypeTool.getInt(month) + 1);
//            }
//            Timestamp end_date = StringTool.getTimestamp(year + month + "01",
//                "yyyyMMdd");
//            parm.setData("END_DATE",
//                         StringTool.rollDate(end_date, -1).toString().
//                         substring(0, 10).replaceAll("-", ""));
            //结束时间为本月的25日
            parm.setData("END_DATE", year + month + "25");
            //开始时间为上月的25日
            if("01".equals(month)){
            	year = TypeTool.getString(TypeTool.getInt(year) - 1);
            	month="12";
            }else{
            	String str = month;
            	month = TypeTool.getString(TypeTool.getInt(month) - 1);	
            	if(str.startsWith("0")) {
            		month = "0"+month;
            	}
            }
            parm.setData("START_DATE", year + month + "26");
          //luhai modify 处理打印月结报表时的开始和结束时间  end
        }
        //起始日期前一天
        String last_trandate = StringTool.rollDate(StringTool.getTimestamp(parm.
            getValue("START_DATE"), "yyyyMMdd"), -1).toString();
        parm.setData("LAST_TRANDATE",
                     last_trandate.substring(0, 4) +
                     last_trandate.substring(5, 7) +
                     last_trandate.substring(8, 10));
        //System.out.println("parm---"+parm);
        TParm result = new TParm();
        if (getRadioButton("DAY_TYPE").isSelected()) {
            // 日结报表
            result = TIOM_AppServer.executeAction("action.spc.INDMonthAction",
                                                  "onQueryDay", parm);
            if (result.getCount("TYPE_CODE") == 0) {
                this.messageBox("无查询结果");
                return;
            }
        }
        else {
            // 月结报表
            result = TIOM_AppServer.executeAction("action.spc.INDMonthAction",
                                                  "onQueryDay", parm);
            if (result.getCount("TYPE_CODE") == 0) {
                this.messageBox("无查询结果");
                return;
            }
        }
        //System.out.println("result=="+result);
        // 打印数据
        parm.setData("DATA_PARM", result.getData());
        onPrintData(parm);
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        onQuery();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("DAY_TYPE").setSelected(true);
        getRadioButton("TYPE_A").setSelected(true);
        this.setValue("ORG_CODE","");
        if (!dept_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                  " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
        }
        else {
            TComboOrgCode org_code = (TComboOrgCode)this.getComponent("ORG_CODE");
            org_code.setOrgType("A");
            org_code.onQuery();
        }
        Timestamp date = SystemTool.getInstance().getDate();
        date = StringTool.rollDate(date, -1);
        this.setValue("MONTH_AREA",
                      date.toString().substring(0, 7).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        getTextFormat("MONTH_AREA").setEnabled(false);
        getTextFormat("START_DATE").setEnabled(true);
        getTextFormat("END_DATE").setEnabled(true);
    }

    /**
     * 变更统计类别
     */
    public void onChangeCountType() {
        if (getRadioButton("DAY_TYPE").isSelected()) {
            getTextFormat("MONTH_AREA").setEnabled(false);
            getTextFormat("START_DATE").setEnabled(true);
            getTextFormat("END_DATE").setEnabled(true);
        }
        else {
            getTextFormat("MONTH_AREA").setEnabled(true);
            getTextFormat("START_DATE").setEnabled(false);
            getTextFormat("END_DATE").setEnabled(false);
        }
    }

    /**
     * 变更部门类别
     */
    public void onChangeOrgType() {
        this.setValue("ORG_CODE","");
        if (!dept_flg) {
            if (getRadioButton("TYPE_A").isSelected()) {
                TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                      " AND B.ORG_TYPE = 'A' ")));
                getComboBox("ORG_CODE").setParmValue(parm);
            }
            else {
                TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                      " AND B.ORG_TYPE = 'B' ")));
                getComboBox("ORG_CODE").setParmValue(parm);
            }
        }
        else {
            TComboOrgCode org_code = (TComboOrgCode)this.getComponent(
                "ORG_CODE");
            if (getRadioButton("TYPE_A").isSelected()) {
                org_code.setOrgType("A");
            }
            else {
                org_code.setOrgType("B");
            }
            org_code.onQuery();
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),  " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        Timestamp date = SystemTool.getInstance().getDate();
        date = StringTool.rollDate(date, -1);
        this.setValue("MONTH_AREA",
                      date.toString().substring(0, 7).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
    }

    /**
     * 打印数据
     * @param parm TParm
     */
    private void onPrintData(TParm parm) {
        Timestamp datetime = SystemTool.getInstance().getDate();
        // 打印数据
        TParm data = new TParm();
        // 表头数据
        data.setData("TITLE_1", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()));
        data.setData("TITLE_2", "TEXT", parm.getValue("TITLE"));
        data.setData("ORG_CODE", "TEXT", "统计部门: " +
                     this.getComboBox("ORG_CODE").getSelectedName());
        String start_date = parm.getValue("START_DATE");
        String end_date = parm.getValue("END_DATE");
        
        data.setData("DATE_AREA", "TEXT",
                     "统计区间: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(4, 6) + "/" +
                     start_date.substring(6, 8) + " " +
                     "00:00:00" + " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(4, 6) + "/" +
                     end_date.substring(6, 8) + " " +
                     "23:59:59");
        data.setData("DATE", "TEXT",
                     "制表日期: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("USER", "TEXT", "制表人: " + Operator.getName());

        // 表格数据
        TParm dataParm = parm.getParm("DATA_PARM");
        //System.out.println("dataParm----"+dataParm);
        TParm inParm = new TParm();
        // 上期结存成本金额总计
        double last_stock_sum = 0;
        // 上期结存零售金额总计
        double last_own_sum = 0;
        // 本期入库成本金额总计
        double in_stock_sum = 0;
        // 本期入库零售金额总计
        double in_own_sum = 0;
        // 本期出库成本金额总计
        double out_stock_sum = 0;
        // 本期出库零售金额总计
        double out_own_sum = 0;
        // 盘盈亏成本金额总计
        double checkmodi_stock_sum = 0;
        // 盘盈亏零售金额总计
        double checkmodi_own_sum = 0;
        // 本期结存成本金额总计
        double stock_sum = 0;
        // 本期结存零售金额总计
        double own_sum = 0;        
        // 调价损益金额总计
        double profit_loss_sum = 0;            
        for (int i = 0; i < dataParm.getCount("TYPE_DESC"); i++) {
            // 本期入库成本金额小计
            double in_stock_amt = 0;
            // 本期入库零售金额小计  
            double in_own_amt = 0;
            // 本期出库成本金额小计
            double out_stock_amt = 0;
            // 本期出库零售金额小计
            double out_own_amt = 0;
            if ("A".equals(parm.getValue("ORG_TYPE"))) {
                //第一行
                inParm.addData("TYPE_DESC", dataParm.getValue("TYPE_DESC", i));
                inParm.addData("LAST_STOCK_AMT", StringTool.round(dataParm.
                    getDouble("LAST_STOCK_AMT", i), 7));
                inParm.addData("LAST_OWN_AMT", StringTool.round(dataParm.
                    getDouble("LAST_OWN_AMT", i), 7));
                inParm.addData("LAST_DIFF_AMT", StringTool.round(dataParm.
                    getDouble("LAST_OWN_AMT", i) -
                    dataParm.getDouble("LAST_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_IN", "验收");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                    "VERIFYIN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "VERIFYIN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt +
                    dataParm.getDouble("VERIFYIN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "VERIFYIN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT",
                               StringTool.round(dataParm.
                                                getDouble("VERIFYIN_OWN_AMT", i) -
                                                dataParm.getDouble(
                    "VERIFYIN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "退货");
                out_stock_amt = out_stock_amt +dataParm.getDouble("REGRESSGOODS_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REGRESSGOODS_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "REGRESSGOODS_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REGRESSGOODS_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REGRESSGOODS_OWN_AMT", i) -
                                                dataParm.getDouble(
                    "REGRESSGOODS_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i), 7));
                inParm.addData("CHECKMODI_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i) - dataParm.getDouble(
                                       "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("STOCK_AMT", StringTool.round(dataParm.getDouble(
                    "STOCK_AMT", i), 7));
                inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i), 7));
                inParm.addData("DIFF_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i) - dataParm.getDouble(
                        "STOCK_AMT", i), 7));
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第二行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "退库");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                    "RET_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "RET_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT",
                               StringTool.round(dataParm.
                                                getDouble("RET_IN_OWN_AMT", i) -
                                                dataParm.getDouble(
                    "RET_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "请领");
                out_stock_amt = out_stock_amt +dataParm.
                                     getDouble("REQUEST_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "REQUEST_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_OWN_AMT", i) -
                               dataParm.getDouble(
                                   "REQUEST_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第三行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "其他入");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "THI_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THI_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "THI_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THI_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT",
                               StringTool.round(dataParm.
                                                getDouble("THI_IN_OWN_AMT", i) -
                                                dataParm.getDouble(
                    "THI_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "其他出");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("THO_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "THO_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_OWN_AMT", i) -
                               dataParm.getDouble(
                                   "THO_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第四行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", "");
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", "");
                inParm.addData("IN_DIFF_AMT", "");
                inParm.addData("PROJECT_OUT", "耗损");
                out_stock_amt = out_stock_amt +dataParm.
                                     getDouble("WAS_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "WAS_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_OWN_AMT", i) -
                               dataParm.getDouble(
                                   "WAS_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第五行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", "");
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", "");
                inParm.addData("IN_DIFF_AMT", "");
                inParm.addData("PROJECT_OUT", "卫耗材");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("COS_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "COS_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_OWN_AMT", i) -
                               dataParm.getDouble(
                                   "COS_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第六行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "小计");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", StringTool.round(in_stock_amt,7));
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", StringTool.round(in_own_amt,7));
                inParm.addData("IN_DIFF_AMT", StringTool.round(in_own_amt - in_stock_amt,7));
                inParm.addData("PROJECT_OUT", "小计");
                inParm.addData("OUT_STOCK_AMT", StringTool.round(out_stock_amt,7));
                inParm.addData("OUT_OWN_AMT", StringTool.round(out_own_amt,7));
                inParm.addData("OUT_DIFF_AMT", StringTool.round(out_own_amt - out_stock_amt,7));
                inParm.addData("CHECKMODI_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i), 7));
                inParm.addData("CHECKMODI_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i) - dataParm.getDouble(
                                       "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("STOCK_AMT", StringTool.round(dataParm.getDouble(
                    "STOCK_AMT", i), 7));
                inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i), 7));
                inParm.addData("DIFF_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i) - dataParm.getDouble(
                        "STOCK_AMT", i), 7));
                double profit = +dataParm.getDouble("OWN_AMT", i) -
                    (dataParm.getDouble("LAST_OWN_AMT", i) + in_own_amt -
                     out_own_amt + dataParm.getDouble("CHECKMODI_OWN_AMT", i));

                inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit, 1));

                //总计
                last_stock_sum += dataParm.getDouble("LAST_STOCK_AMT", i);
                last_own_sum += dataParm.getDouble("LAST_OWN_AMT", i);
                in_stock_sum += in_stock_amt;
                in_own_sum += in_own_amt;
                out_stock_sum += out_stock_amt;
                out_own_sum += out_own_amt;
                checkmodi_stock_sum += dataParm.getDouble("CHECKMODI_STOCK_AMT",
                    i);
                checkmodi_own_sum += dataParm.getDouble("CHECKMODI_OWN_AMT", i);
                stock_sum += dataParm.getDouble("STOCK_AMT", i);
                own_sum += dataParm.getDouble("OWN_AMT", i);
                profit_loss_sum += profit;
            }
            else {
                //第一行
                inParm.addData("TYPE_DESC", dataParm.getValue("TYPE_DESC", i));
                inParm.addData("LAST_STOCK_AMT", StringTool.round(dataParm.
                    getDouble("LAST_STOCK_AMT", i), 7));
                inParm.addData("LAST_OWN_AMT", StringTool.round(dataParm.
                    getDouble("LAST_OWN_AMT", i), 7));
                inParm.addData("LAST_DIFF_AMT", StringTool.round(dataParm.
                    getDouble("LAST_OWN_AMT", i) -
                    dataParm.getDouble("LAST_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_IN", "请领");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "REQUEST_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "REQUEST_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT", StringTool.round(dataParm.
                    getDouble("REQUEST_IN_OWN_AMT", i) - dataParm.getDouble(
                        "REQUEST_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "退库");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("RET_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "RET_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_OUT_OWN_AMT", i) - dataParm.getDouble(
                                       "RET_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i), 7));
                inParm.addData("CHECKMODI_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i) - dataParm.getDouble(
                                       "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("STOCK_AMT", StringTool.round(dataParm.getDouble(
                    "STOCK_AMT", i), 7));
                inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i), 7));
                inParm.addData("DIFF_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i) - dataParm.getDouble(
                        "STOCK_AMT", i), 7));
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第二行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "科室退");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "RET_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "RET_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "RET_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT",
                               StringTool.round(dataParm.
                                                getDouble("RET_IN_OWN_AMT", i) -
                                                dataParm.getDouble(
                    "RET_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "科室领");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("REQUEST_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "REQUEST_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "REQUEST_OUT_OWN_AMT", i)-dataParm.getDouble(
                                   "REQUEST_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第三行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "调入");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "GIF_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "GIF_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "GIF_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "GIF_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT", StringTool.round(dataParm.
                    getDouble("GIF_IN_OWN_AMT", i) - dataParm.getDouble(
                        "GIF_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "调出");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("GIF_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "GIF_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "GIF_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm.
                    getDouble("GIF_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "GIF_OUT_OWN_AMT", i) - dataParm.getDouble(
                                   "GIF_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第四行  发药，退药
                /// begin
               /* TParm dosage_parm = getDosageAMT(this.getValueString("ORG_CODE"),
                                                 dataParm.getValue("TYPE_CODE",
                    i));*/
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "退药");
               /* in_stock_amt = in_stock_amt + dosage_parm.getDouble(
                    "REGRESSDRUG_STOCK_AMT", 0);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dosage_parm.getDouble(
                                   "REGRESSDRUG_STOCK_AMT", 0), 2));
                in_own_amt = in_own_amt + dosage_parm.getDouble(
                    "REGRESSDRUG_OWN_AMT", 0);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dosage_parm.getDouble(
                                   "REGRESSDRUG_OWN_AMT", 0), 2));
                inParm.addData("IN_DIFF_AMT", StringTool.round(dosage_parm.
                    getDouble("REGRESSDRUG_OWN_AMT", 0) - dosage_parm.getDouble(
                        "REGRESSDRUG_STOCK_AMT", 0), 2));
                inParm.addData("PROJECT_OUT", "发药");
                out_stock_amt = out_stock_amt + dosage_parm.
                    getDouble("DOSAGE_STOCK_AMT", 0);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dosage_parm.getDouble(
                                   "DOSAGE_STOCK_AMT", 0), 2));
                out_own_amt = out_own_amt + dosage_parm.getDouble(
                    "DOSAGE_OWN_AMT", 0);
                inParm.addData("OUT_OWN_AMT", StringTool.round(dosage_parm.
                    getDouble("DOSAGE_OWN_AMT", 0), 2));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dosage_parm.getDouble(
                                   "DOSAGE_OWN_AMT", 0) - dosage_parm.getDouble(
                                       "DOSAGE_STOCK_AMT", 0), 2));*/				     		
                	in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "IN_STOCK_AMT", i);
                    inParm.addData("IN_STOCK_AMT",
                                   StringTool.round(dataParm.getDouble(
                                       "IN_STOCK_AMT", i), 7));
                    in_own_amt = in_own_amt + dataParm.getDouble(
                        "IN_OWN_AMT", i);
                    inParm.addData("IN_OWN_AMT",
                                   StringTool.round(dataParm.getDouble(
                                       "IN_OWN_AMT", i), 7));
                    inParm.addData("IN_DIFF_AMT", StringTool.round(dataParm.
                        getDouble("IN_OWN_AMT", i) - dataParm.getDouble(
                            "IN_STOCK_AMT", i), 7));
                    inParm.addData("PROJECT_OUT", "发药");
                    out_stock_amt = out_stock_amt + dataParm.
                        getDouble("OUT_STOCK_AMT", i);
                    inParm.addData("OUT_STOCK_AMT",
                                   StringTool.round(dataParm.getDouble(
                                       "OUT_STOCK_AMT", i), 7));
                    out_own_amt = out_own_amt + dataParm.getDouble(
                        "OUT_OWN_AMT", i);
                    inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm.
                        getDouble("OUT_OWN_AMT", i), 7));
                    inParm.addData("OUT_DIFF_AMT",
                                   StringTool.round(dataParm.getDouble(
                                       "OUT_OWN_AMT", i) - dataParm.getDouble(
                                           "OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                /// end
                /////////////////////////////////////////////////
//                inParm.addData("TYPE_DESC", "");
//                inParm.addData("LAST_STOCK_AMT", "");
//                inParm.addData("LAST_OWN_AMT", "");
//                inParm.addData("LAST_DIFF_AMT", "");
//                inParm.addData("PROJECT_IN", "退药");
//                in_stock_amt = in_stock_amt + dataParm.getDouble(
//                        "REGRESSDRUG_STOCK_AMT", i);
//                inParm.addData("IN_STOCK_AMT",
//                               StringTool.round(dataParm.getDouble(
//                                   "REGRESSDRUG_STOCK_AMT", i), 2));
//                in_own_amt = in_own_amt + dataParm.getDouble(
//                    "REGRESSDRUG_OWN_AMT", i);
//                inParm.addData("IN_OWN_AMT",
//                               StringTool.round(dataParm.getDouble(
//                                   "REGRESSDRUG_OWN_AMT", i), 2));
//                inParm.addData("IN_DIFF_AMT", StringTool.round(dataParm.
//                    getDouble("REGRESSDRUG_OWN_AMT", i) - dataParm.getDouble(
//                        "REGRESSDRUG_STOCK_AMT", i), 2));
//                inParm.addData("PROJECT_OUT", "发药");
//                out_stock_amt = out_stock_amt + dataParm.
//                                     getDouble("DOSAGE_STOCK_AMT", i);
//                inParm.addData("OUT_STOCK_AMT",
//                               StringTool.round(dataParm.getDouble(
//                                   "DOSAGE_STOCK_AMT", i), 2));
//                out_own_amt = out_own_amt + dataParm.getDouble(
//                    "DOSAGE_OWN_AMT", i);
//                inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm.
//                    getDouble("DOSAGE_OWN_AMT", i), 2));
//                inParm.addData("OUT_DIFF_AMT",
//                               StringTool.round(dataParm.getDouble(
//                                   "DOSAGE_OWN_AMT", i) - dataParm.getDouble(
//                                   "DOSAGE_STOCK_AMT", i), 2));
//                inParm.addData("CHECKMODI_STOCK_AMT", "");
//                inParm.addData("CHECKMODI_OWN_AMT", "");
//                inParm.addData("CHECKMODI_DIFF_AMT", "");
//                inParm.addData("STOCK_AMT", "");
//                inParm.addData("OWN_AMT", "");
//                inParm.addData("DIFF_AMT", "");
//                inParm.addData("PROFIT_LOSS_AMT", "");
                ////////////////////////////////////////////
                //第五行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "其他入");
                in_stock_amt = in_stock_amt + dataParm.getDouble(
                        "THI_IN_STOCK_AMT", i);
                inParm.addData("IN_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THI_IN_STOCK_AMT", i), 7));
                in_own_amt = in_own_amt + dataParm.getDouble(
                    "THI_IN_OWN_AMT", i);
                inParm.addData("IN_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THI_IN_OWN_AMT", i), 7));
                inParm.addData("IN_DIFF_AMT", StringTool.round(dataParm.
                    getDouble("THI_IN_OWN_AMT", i) - dataParm.getDouble(
                        "THI_IN_STOCK_AMT", i), 7));
                inParm.addData("PROJECT_OUT", "其他出");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("THO_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "THO_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "THO_OUT_OWN_AMT", i)- dataParm.getDouble(
                                   "THO_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第六行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", "");
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", "");
                inParm.addData("IN_DIFF_AMT", "");
                inParm.addData("PROJECT_OUT", "耗损");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("WAS_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "WAS_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "WAS_OUT_OWN_AMT", i) - dataParm.getDouble(
                                   "WAS_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第七行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", "");
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", "");
                inParm.addData("IN_DIFF_AMT", "");
                inParm.addData("PROJECT_OUT", "卫耗材");
                out_stock_amt = out_stock_amt + dataParm.
                                     getDouble("COS_OUT_STOCK_AMT", i);
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_STOCK_AMT", i), 7));
                out_own_amt = out_own_amt + dataParm.getDouble(
                    "COS_OUT_OWN_AMT", i);
                inParm.addData("OUT_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_OWN_AMT", i), 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "COS_OUT_OWN_AMT", i)- dataParm.getDouble(
                                   "COS_OUT_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_STOCK_AMT", "");
                inParm.addData("CHECKMODI_OWN_AMT", "");
                inParm.addData("CHECKMODI_DIFF_AMT", "");
                inParm.addData("STOCK_AMT", "");
                inParm.addData("OWN_AMT", "");
                inParm.addData("DIFF_AMT", "");
                inParm.addData("PROFIT_LOSS_AMT", "");

                //第八行
                inParm.addData("TYPE_DESC", "");
                inParm.addData("LAST_STOCK_AMT", "");
                inParm.addData("LAST_OWN_AMT", "");
                inParm.addData("LAST_DIFF_AMT", "");
                inParm.addData("PROJECT_IN", "小计");
                //in_stock_amt = in_stock_amt;
                inParm.addData("IN_STOCK_AMT", StringTool.round(in_stock_amt,7));
                //in_own_amt = in_own_amt;
                inParm.addData("IN_OWN_AMT", StringTool.round(in_own_amt, 7));
                inParm.addData("IN_DIFF_AMT",
                               StringTool.round(in_own_amt - in_stock_amt, 7));
                inParm.addData("PROJECT_OUT", "小计");
                inParm.addData("OUT_STOCK_AMT",
                               StringTool.round(out_stock_amt, 7));
                inParm.addData("OUT_OWN_AMT", StringTool.round(out_own_amt, 7));
                inParm.addData("OUT_DIFF_AMT",
                               StringTool.round(out_own_amt - out_stock_amt, 7));
                inParm.addData("CHECKMODI_STOCK_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("CHECKMODI_OWN_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i), 7));
                inParm.addData("CHECKMODI_DIFF_AMT",
                               StringTool.round(dataParm.getDouble(
                                   "CHECKMODI_OWN_AMT", i) - dataParm.getDouble(
                                       "CHECKMODI_STOCK_AMT", i), 7));
                inParm.addData("STOCK_AMT", StringTool.round(dataParm.getDouble(
                    "STOCK_AMT", i), 7));
                inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i), 7));
                inParm.addData("DIFF_AMT", StringTool.round(dataParm.getDouble(
                    "OWN_AMT", i) - dataParm.getDouble(
                        "STOCK_AMT", i), 7));

                double profit = +dataParm.getDouble("OWN_AMT", i) -
                    (dataParm.getDouble("LAST_OWN_AMT", i) + in_own_amt -
                     out_own_amt + dataParm.getDouble("CHECKMODI_OWN_AMT", i));

                inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit, 1));

                //总计
                last_stock_sum += dataParm.getDouble("LAST_STOCK_AMT", i);
                last_own_sum += dataParm.getDouble("LAST_OWN_AMT", i);
                in_stock_sum += in_stock_amt;
                in_own_sum += in_own_amt;
                out_stock_sum += out_stock_amt;
                out_own_sum += out_own_amt;
                checkmodi_stock_sum += dataParm.getDouble("CHECKMODI_STOCK_AMT",
                    i);
                checkmodi_own_sum += dataParm.getDouble("CHECKMODI_OWN_AMT",
                    i);
                stock_sum += dataParm.getDouble("STOCK_AMT", i);
                own_sum += dataParm.getDouble("OWN_AMT", i);
                profit_loss_sum += profit;
            }
        }

        // 总计行
        inParm.addData("TYPE_DESC", "合计");
        inParm.addData("LAST_STOCK_AMT", StringTool.round(last_stock_sum,7));
        inParm.addData("LAST_OWN_AMT", StringTool.round(last_own_sum,7));
        inParm.addData("LAST_DIFF_AMT", StringTool.round(last_own_sum - last_stock_sum,7));
        inParm.addData("PROJECT_IN", "");
        inParm.addData("IN_STOCK_AMT", StringTool.round(in_stock_sum,7));
        inParm.addData("IN_OWN_AMT", StringTool.round(in_own_sum,7));
        inParm.addData("IN_DIFF_AMT", StringTool.round(in_own_sum - in_stock_sum,7));
        inParm.addData("PROJECT_OUT", "");
        inParm.addData("OUT_STOCK_AMT", StringTool.round(out_stock_sum,7));
        inParm.addData("OUT_OWN_AMT", StringTool.round(out_own_sum,7));
        inParm.addData("OUT_DIFF_AMT", StringTool.round(out_own_sum - out_stock_sum,7));
        inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(checkmodi_stock_sum,7));
        inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(checkmodi_own_sum,7));
        inParm.addData("CHECKMODI_DIFF_AMT",
                       StringTool.round(checkmodi_own_sum - checkmodi_stock_sum,7));
        inParm.addData("STOCK_AMT", StringTool.round(stock_sum,7));
        inParm.addData("OWN_AMT", StringTool.round(own_sum,7));
        inParm.addData("DIFF_AMT", StringTool.round(own_sum - stock_sum,7));
        inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit_loss_sum,1));

        if ("A".equals(parm.getValue("ORG_TYPE"))) {
            inParm.setCount(dataParm.getCount("TYPE_DESC") * 6 + 1);
        }
        else {
            inParm.setCount(dataParm.getCount("TYPE_DESC") * 8 + 1);
        }
//		  luhai 2012-1-24 modify 取消进销差价和调价损益的相关内容 begin
//        inParm.addData("SYSTEM", "COLUMNS", "TYPE_DESC");
//        inParm.addData("SYSTEM", "COLUMNS", "LAST_STOCK_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "LAST_OWN_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "LAST_DIFF_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "PROJECT_IN");
//        inParm.addData("SYSTEM", "COLUMNS", "IN_STOCK_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "IN_OWN_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "IN_DIFF_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "PROJECT_OUT");
//        inParm.addData("SYSTEM", "COLUMNS", "OUT_STOCK_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "OUT_OWN_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "OUT_DIFF_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_STOCK_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_OWN_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_DIFF_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
//        inParm.addData("SYSTEM", "COLUMNS", "PROFIT_LOSS_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "TYPE_DESC");
        inParm.addData("SYSTEM", "COLUMNS", "LAST_STOCK_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "LAST_OWN_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "LAST_DIFF_AMT1");
        inParm.addData("SYSTEM", "COLUMNS", "PROJECT_IN");
        inParm.addData("SYSTEM", "COLUMNS", "IN_STOCK_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "IN_OWN_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "IN_DIFF_AMT1");
        inParm.addData("SYSTEM", "COLUMNS", "PROJECT_OUT");
        inParm.addData("SYSTEM", "COLUMNS", "OUT_STOCK_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "OUT_OWN_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "OUT_DIFF_AMT1");
        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_STOCK_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_OWN_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_DIFF_AMT1");
        inParm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        inParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT1");
        inParm.addData("SYSTEM", "COLUMNS", "PROFIT_LOSS_AMT1");
        data.setData("TABLE", inParm.getData());

        // 表尾数据
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDMonth.jhw",
                             data);

    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TTextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     *
     * @return TParm
     */
    private TParm getDosageAMT(String org_code, String type_code) {
        String start_date = "";
        String end_date = "";
        if (getRadioButton("DAY_TYPE").isSelected()) {
            start_date = this.getValueString("START_DATE").substring(0, 10).replaceAll("-", "");
            end_date = this.getValueString("END_DATE").substring(0, 10).replaceAll("-", "");
        }
        else {
            String month_area = this.getValueString("MONTH_AREA");
            String year = month_area.substring(0, 4);
            String month = month_area.substring(5, 7);
            start_date = year + month + "01";
            if ("12".equals(month)) {
                year = TypeTool.getString(TypeTool.getInt(year) + 1);
                month = "01";
            }
            else {
                month = TypeTool.getString(TypeTool.getInt(month) + 1);
            }
            Timestamp end_datetime = StringTool.getTimestamp(year + month + "01",
                "yyyyMMdd");
            end_date = StringTool.rollDate(end_datetime, -1).toString().
                substring(0, 10).replaceAll("-", "");
        }
        start_date = start_date + "000000";
        end_date = end_date + "235959";

        TParm parm = new TParm();
        parm.addData("REGRESSDRUG_STOCK_AMT", 0);
        parm.addData("REGRESSDRUG_OWN_AMT", 0);
        parm.addData("DOSAGE_STOCK_AMT", 0);
        parm.addData("DOSAGE_OWN_AMT", 0);
        //门诊发药
        String sql_dosage_opd = " SELECT SUM (AR_AMT) AS DOSAGE_OWN_AMT, "
            + " SUM (COST_AMT) AS DOSAGE_STOCK_AMT "
            + " FROM OPD_ORDER A, PHA_BASE B "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
            + " AND B.TYPE_CODE = '" + type_code + "' "
            + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') "
            + " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') AND A.PHA_RETN_CODE IS NULL ";
        TParm dosage_opd = new TParm(TJDODBTool.getInstance().select(sql_dosage_opd));
        if (dosage_opd != null && dosage_opd.getCount("DOSAGE_STOCK_AMT") > 0) {
            parm.setData("DOSAGE_STOCK_AMT", 0,
                         parm.getDouble("DOSAGE_STOCK_AMT", 0) +
                         dosage_opd.getDouble("DOSAGE_STOCK_AMT", 0));
            parm.setData("DOSAGE_OWN_AMT", 0,
                         parm.getDouble("DOSAGE_OWN_AMT", 0) +
                         dosage_opd.getDouble("DOSAGE_OWN_AMT", 0));
        }
        //住院发药
       /* String sql_dosage_odi = " SELECT SUM (A.TOT_AMT) AS DOSAGE_OWN_AMT, "
            + " SUM (COST_AMT) AS DOSAGE_STOCK_AMT "
            + " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND A.EXE_DEPT_CODE = '" + org_code + "' "
            + " AND C.TYPE_CODE = '" + type_code + "' "
            + " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS ') AND A.TOT_AMT > 0 ";*/
        String sql_dosage_odi = "SELECT SUM(A.OWN_AMT) AS DOSAGE_OWN_AMT," +
        		"SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)" +
        		"/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY3)* A.DOSAGE_QTY)  AS DOSAGE_STOCK_AMT " +
        		"FROM ODI_DSPNM A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND " +
        		"A.EXEC_DEPT_CODE='"+org_code+"' AND A.DSPN_KIND!='RT' AND " +
        				"A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') " +
        						"AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS')";
        										
        TParm dosage_odi = new TParm(TJDODBTool.getInstance().select(sql_dosage_odi));
        if (dosage_odi != null && dosage_odi.getCount("DOSAGE_STOCK_AMT") > 0) {
            parm.setData("DOSAGE_STOCK_AMT", 0,
                         parm.getDouble("DOSAGE_STOCK_AMT", 0) +
                         dosage_odi.getDouble("DOSAGE_STOCK_AMT", 0));
            parm.setData("DOSAGE_OWN_AMT", 0,
                         parm.getDouble("DOSAGE_OWN_AMT", 0) +
                         dosage_odi.getDouble("DOSAGE_OWN_AMT", 0));
        }
        //门诊退药
       /* String sql_re_dosage_opd =
            " SELECT SUM (A.AR_AMT) AS REGRESSDRUG_OWN_AMT , "
            + " SUM (A.DOSAGE_QTY * B.STOCK_PRICE) AS REGRESSDRUG_STOCK_AMT "
            + " FROM PHA_ORDER_HISTORY A, PHA_BASE B, OPD_ORDER C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.CASE_NO = C.CASE_NO "
            + " AND A.RX_NO = C.RX_NO "
            + " AND A.SEQ_NO = C.SEQ_NO "
            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
            + " AND B.TYPE_CODE = '" + type_code + "' "
            + " AND A.PHA_RETN_DATE >= '" + start_date +
            "' AND A.PHA_RETN_DATE <= '" + end_date + "'";*/
        String sql_re_dosage_opd = " SELECT SUM (AR_AMT) AS REGRESSDRUG_OWN_AMT, "
            + " SUM (COST_AMT) AS REGRESSDRUG_STOCK_AMT "				
            + " FROM OPD_ORDER A, PHA_BASE B "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
            + " AND B.TYPE_CODE = '" + type_code + "' "
            + " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') "
            + " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') AND A.PHA_RETN_CODE IS NOT NULL ";
        TParm re_dosage_opd = new TParm(TJDODBTool.getInstance().select(
            sql_re_dosage_opd));
        if (re_dosage_opd != null && re_dosage_opd.getCount("REGRESSDRUG_STOCK_AMT") > 0) {
            parm.setData("REGRESSDRUG_STOCK_AMT", 0,
                         parm.getDouble("REGRESSDRUG_STOCK_AMT", 0) +
                         re_dosage_opd.getDouble("REGRESSDRUG_STOCK_AMT", 0));
            parm.setData("REGRESSDRUG_OWN_AMT", 0,
                         parm.getDouble("REGRESSDRUG_OWN_AMT", 0) +
                         re_dosage_opd.getDouble("REGRESSDRUG_OWN_AMT", 0));
        }
        //住院退药
/*        String sql_re_dosage_odi =
            " SELECT SUM (A.TOT_AMT) * -1 AS REGRESSDRUG_OWN_AMT, "
            + " SUM (COST_AMT) * -1 AS REGRESSDRUG_STOCK_AMT "
            + " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND A.EXE_DEPT_CODE = '" + org_code + "' "
            + " AND C.TYPE_CODE = '" + type_code + "' "
            + " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS ') AND A.TOT_AMT < 0 ";*/
        String sql_re_dosage_odi = "SELECT SUM(A.OWN_AMT) AS DOSAGE_OWN_AMT," +
		" SUM(A.DOSAGE_QTY*B.STOCK_PRICE) AS DOSAGE_STOCK_AMT " +
		"FROM ODI_DSPNM A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND " +   
		"A.EXEC_DEPT_CODE='"+org_code+"' AND A.DSPN_KIND='RT' AND " +
				"A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') " +
						"AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS')";
        TParm re_dosage_odi = new TParm(TJDODBTool.getInstance().select(
            sql_re_dosage_odi));
        if (re_dosage_odi != null && re_dosage_odi.getCount("REGRESSDRUG_STOCK_AMT") > 0) {
            parm.setData("REGRESSDRUG_STOCK_AMT", 0,
                         parm.getDouble("REGRESSDRUG_STOCK_AMT", 0) +
                         re_dosage_odi.getDouble("REGRESSDRUG_STOCK_AMT", 0));
            parm.setData("REGRESSDRUG_OWN_AMT", 0,
                         parm.getDouble("REGRESSDRUG_OWN_AMT", 0) +
                         re_dosage_odi.getDouble("REGRESSDRUG_OWN_AMT", 0));
        }

        return parm;
    }
}
