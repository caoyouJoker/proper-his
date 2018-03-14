package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.bil.BILPrintTool;
import jdo.bil.BILSQL;
import jdo.sys.SystemTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p> Title: 票据资料汇出控制类  </p>
 * 
 * <p> Description: 票据资料汇出控制类  </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * <p> Company: JavaHis </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class BILExportRecpControl extends TControl {

    TTable table;
    TParm table2;
    TParm table3;

    private boolean ascending = false;// 用于排序 
    private int sortColumn = -1;// 用于排序
    private BILComparator compare = new BILComparator();// 用于排序add by wanglong 20130120
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        // 账单table专用的监听
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
        onSetTableAttribute();// add by wanglong 20130109
        initPage();
        addListener(table);//add by wanglong 20130120
    }

    /**
     * 账单table监听事件
     * 
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }

    /**
     * 初始化界面数据
     */
    public void initPage() {
        // 初始化查询起时,迄时
        String yesterday =
                StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(), -1),
                                     "yyyy/MM/dd");
        String today = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd");
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        String todayTime = StringTool.getString(SystemTool.getInstance().getDate(), "HH:mm:ss");
        setValue("S_TIME", "00:00:00");
        setValue("E_TIME", todayTime);
        setValue("SEL_O", "Y");
    }

    /**
     * 查询
     */
    public void onQuery() {
        String sTime =
                StringTool.getString((Timestamp) this.getValue("S_DATE"), "yyyyMMdd")
                        + StringTool.getString((Timestamp) this.getValue("S_TIME"), "HHmmss");
        String eTime =
                StringTool.getString((Timestamp) this.getValue("E_DATE"), "yyyyMMdd")
                        + StringTool.getString((Timestamp) this.getValue("E_TIME"), "HHmmss");
        String recpType = "";
        String sql = "";
        TParm result = new TParm();
        double charge01 = 0.00;
        double charge02 = 0.00;
        double charge0102 = 0.00;// add by wanglong 20130109
        double charge03 = 0.00;
        double charge04 = 0.00;
        double charge0304 = 0.00;// add by wanglong 20130109
        double charge05 = 0.00;
        double charge06 = 0.00;
        double charge07 = 0.00;
        double charge08 = 0.00;
        double charge09 = 0.00;
        double charge10 = 0.00;
        double charge11 = 0.00;
        double charge12 = 0.00;
        double charge13 = 0.00;
        double charge14 = 0.00;
        double charge15 = 0.00;
        double charge16 = 0.00;
        double charge17 = 0.00;
        double charge18 = 0.00;
        double charge19 = 0.00;
        double charge20 = 0.00;// add by wanglong 20130109
        double totAmt = 0.00;
        int count = 0;
        if (this.getValue("SEL_O").toString().equals("Y")) {// 门诊  modify by wanglong 20130120
            recpType = "OPB";
            sql = BILSQL.getRecpDataO(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("数据查询错误  " + result.getErrText());
                return;
            }
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge0102 = charge0102 + result.getDouble("CHARGE0102", i);
                charge03 = charge03 + result.getDouble("CHARGE03", i);
                charge04 = charge04 + result.getDouble("CHARGE04", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "合  计：");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE0102", StringTool.round(charge0102, 2));
            result.addData("CHARGE03", StringTool.round(charge03, 2));
            result.addData("CHARGE04", StringTool.round(charge04, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count + 1);
        }
        else if (this.getValue("SEL_I").toString().equals("Y")) {// 住院 modify by wanglong 20130120
            recpType = "IBS";
            sql = BILSQL.getRecpDataI(sTime, eTime, null);// modify by wanglong 20130120
            TParm selRecpDParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (selRecpDParm.getErrCode() < 0) {
                messageBox("数据查询错误  " + selRecpDParm.getErrText());
                return;
            }
            HashSet invNo = new HashSet();
            for (int i = 0; i < selRecpDParm.getCount(); i++) {
                invNo.add(selRecpDParm.getValue("INV_NO", i));
            }
            // System.out.println("票据数"+invNo);
            Iterator iter = invNo.iterator();
            int row = 0; // 记录行数
            while (iter.hasNext()) {
                String invNoS = iter.next().toString(); // 收费员CODE
                // System.out.println("票号"+invNoS);
                double sum = 0; // 计算总金额
                count = selRecpDParm.getCount("REXP_CODE");
                String rexpCode = "";
                double totAmtI = 0.00;
                double wrtOffSingle = 0.00;
                result.addData("INV_NO", "");
                result.addData("PRINT_DATE", "");
                result.addData("RECEIPT_NO", "");
                result.addData("CANCEL_FLG", "");
                result.addData("PAT_NAME", "");
                result.addData("CHARGE01", 0.00);
                result.addData("CHARGE02", 0.00);
                result.addData("CHARGE0304", 0.00);
                result.addData("CHARGE05", 0.00);
                result.addData("CHARGE06", 0.00);
                result.addData("CHARGE07", 0.00);
                result.addData("CHARGE08", 0.00);
                result.addData("CHARGE09", 0.00);
                result.addData("CHARGE10", 0.00);
                result.addData("CHARGE11", 0.00);
                result.addData("CHARGE12", 0.00);
                result.addData("CHARGE13", 0.00);
                result.addData("CHARGE14", 0.00);
                result.addData("CHARGE15", 0.00);
                result.addData("CHARGE16", 0.00);
                result.addData("CHARGE17", 0.00);
                result.addData("CHARGE18", 0.00);
                result.addData("CHARGE19", 0.00);
                result.addData("CHARGE20", 0.00);
                result.addData("TOT_AMT", 0.00);
                for (int i = 0; i < selRecpDParm.getCount(); i++) {
                    if (invNoS.equals(selRecpDParm.getValue("INV_NO", i))) {
                        rexpCode = selRecpDParm.getValue("REXP_CODE", i);
                        wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
                        totAmtI = totAmtI + wrtOffSingle;
                        sum = sum + wrtOffSingle;
                        result.setData("INV_NO", row, selRecpDParm.getData("INV_NO", i));
                        result.setData("PRINT_DATE", row, selRecpDParm.getData("PRINT_DATE", i));
                        result.setData("RECEIPT_NO", row, selRecpDParm.getData("RECEIPT_NO", i));
                        result.setData("CANCEL_FLG", row, selRecpDParm.getData("CANCEL_FLG", i));
                        result.setData("PAT_NAME", row, selRecpDParm.getData("PAT_NAME", i));
                        if ("020".equals(rexpCode)) { // 床位费
                            result.setData("CHARGE01", row, result.getDouble("CHARGE01", row)
                                    + wrtOffSingle);
                        } else if ("021".equals(rexpCode)) { // 诊察费
                            result.setData("CHARGE02", row, result.getDouble("CHARGE02", row)
                                    + wrtOffSingle);
                        } else if ("022.01".equals(rexpCode)) { // 西药费（抗生素）
                            // result.setData("CHARGE03", row, wrtOffSingle);
                            charge03 += wrtOffSingle;// modify by wanglong 20130129
                        } else if ("022.02".equals(rexpCode)) { // 西药费（非抗生素）
                            // result.setData("CHARGE04", row, wrtOffSingle);
                            charge04 += wrtOffSingle;// modify by wanglong 20130129
                        } else if ("023".equals(rexpCode)) { // 中成药
                            result.setData("CHARGE05", row, result.getDouble("CHARGE05", row)
                                    + wrtOffSingle);
                        } else if ("024".equals(rexpCode)) { // 中草药
                            result.setData("CHARGE06", row, result.getDouble("CHARGE06", row)
                                    + wrtOffSingle);
                        } else if ("025".equals(rexpCode)) { // 检查费
                            result.setData("CHARGE07", row, result.getDouble("CHARGE07", row)
                                    + wrtOffSingle);
                        } else if ("026".equals(rexpCode)) { // 治疗费
                            result.setData("CHARGE08", row, result.getDouble("CHARGE08", row)
                                    + wrtOffSingle);
                        } else if ("027".equals(rexpCode)) { // 放射费
                            result.setData("CHARGE09", row, result.getDouble("CHARGE09", row)
                                    + wrtOffSingle);
                        } else if ("028".equals(rexpCode)) { // 手术费;
                            result.setData("CHARGE10", row, result.getDouble("CHARGE10", row)
                                    + wrtOffSingle);
                        } else if ("029".equals(rexpCode)) { // 化验费
                            result.setData("CHARGE11", row, result.getDouble("CHARGE11", row)
                                    + wrtOffSingle);
                        } else if ("02A".equals(rexpCode)) { // 输血费
                            result.setData("CHARGE12", row, result.getDouble("CHARGE12", row)
                                    + wrtOffSingle);
                        } else if ("02B".equals(rexpCode)) { // 输氧费
                            result.setData("CHARGE13", row, result.getDouble("CHARGE13", row)
                                    + wrtOffSingle);
                        } else if ("02C".equals(rexpCode)) { // 接生费
                            result.setData("CHARGE14", row, result.getDouble("CHARGE14", row)
                                    + wrtOffSingle);
                        } else if ("02D".equals(rexpCode)) { // 护理费
                            result.setData("CHARGE15", row, result.getDouble("CHARGE15", row)
                                    + wrtOffSingle);
                        } else if ("02E".equals(rexpCode)) { // 家床费
                            result.setData("CHARGE16", row, result.getDouble("CHARGE16", row)
                                    + wrtOffSingle);
                        } else if ("032".equals(rexpCode)) { // CT费
                            result.setData("CHARGE17", row, result.getDouble("CHARGE17", row)
                                    + wrtOffSingle);
                        } else if ("033".equals(rexpCode)) { // MR费
                            result.setData("CHARGE18", row, result.getDouble("CHARGE18", row)
                                    + wrtOffSingle);
                        } else if ("02F".equals(rexpCode)) { // 自费部分
                            result.setData("CHARGE19", row, result.getDouble("CHARGE19", row)
                                    + wrtOffSingle);
                        } else if ("035".equals(rexpCode)) { // 材料费
                            result.setData("CHARGE20", row, result.getDouble("CHARGE20", row)
                                    + wrtOffSingle);
                        }
                        result.setData("TOT_AMT", row, StringTool.round(sum, 2));
                    }
                }
               // result.setData("CHARGE0304", row, result.getDouble("CHARGE03", row) + result.getDouble("CHARGE04", row));
                result.setData("CHARGE0304", row, charge03 + charge04);// modify by wanglong 20130120
                charge03 = 0;// add by wanglong 20130129
                charge04 = 0;// add by wanglong 20130129
                row++;
            }// while结束
            result.setCount(row);
            charge01 = 0.00;
            charge02 = 0.00;
            charge0304 = 0.00;
            charge05 = 0.00;
            charge06 = 0.00;
            charge07 = 0.00;
            charge08 = 0.00;
            charge09 = 0.00;
            charge10 = 0.00;
            charge11 = 0.00;
            charge12 = 0.00;
            charge13 = 0.00;
            charge14 = 0.00;
            charge15 = 0.00;
            charge16 = 0.00;
            charge17 = 0.00;
            charge18 = 0.00;
            charge19 = 0.00;
            charge20 = 0.00;
            totAmt = 0.00;
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge01 = charge01 + result.getDouble("CHARGE01", i);
                charge02 = charge02 + result.getDouble("CHARGE02", i);
                charge0304 = charge0304 + result.getDouble("CHARGE0304", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                charge20 = charge20 + result.getDouble("CHARGE20", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
      
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "合  计：");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE01", StringTool.round(charge01, 2));
            result.addData("CHARGE02", StringTool.round(charge02, 2));
            result.addData("CHARGE0304", StringTool.round(charge0304, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE20", StringTool.round(charge20, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count+1);
        }
        else if (this.getValue("SEL_H").toString().equals("Y")){// 健检 modify by wanglong 20130120
            recpType = "HRM";//modify by wanglong 20130120
            sql = BILSQL.getRecpDataH(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("数据查询错误  " + result.getErrText());
                return;
            }
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge0102 = charge0102 + result.getDouble("CHARGE0102", i);
                charge03 = charge03 + result.getDouble("CHARGE03", i);
                charge04 = charge04 + result.getDouble("CHARGE04", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("COMPANY_DESC", "");
            result.addData("PAT_NAME", "合  计：");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE0102", StringTool.round(charge0102, 2));
            result.addData("CHARGE03", StringTool.round(charge03, 2));
            result.addData("CHARGE04", StringTool.round(charge04, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count + 1); 
        }
        else if (this.getValue("SEL_R").toString().equals("Y")) {//挂号 add by wanglong 20130120
            recpType = "REG";
            sql = BILSQL.getRecpDataR(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("数据查询错误  " + result.getErrText());
                return;
            }
            count = result.getCount();
            double regFeeReal = 0;
            double clinicFeeReal = 0;
            double spcFee = 0;
            double otherFee1 = 0;
            double otherFee2 = 0;
            double otherFee3 = 0;
            for (int i = 0; i < count; i++) {
                regFeeReal = regFeeReal + result.getDouble("REG_FEE_REAL", i);
                clinicFeeReal = clinicFeeReal + result.getDouble("CLINIC_FEE_REAL", i);
                spcFee = spcFee + result.getDouble("SPC_FEE", i);
                otherFee1 = otherFee1 + result.getDouble("OTHER_FEE1", i);
                otherFee2 = otherFee2 + result.getDouble("OTHER_FEE2", i);
                otherFee3 = otherFee3 + result.getDouble("OTHER_FEE3", i);
                totAmt = totAmt + result.getDouble("AR_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "合  计：");
            result.addData("AR_AMT", StringTool.round(totAmt, 2));
            result.addData("REG_FEE_REAL", StringTool.round(regFeeReal, 2));
            result.addData("CLINIC_FEE_REAL", StringTool.round(clinicFeeReal, 2));
            result.addData("SPC_FEE", StringTool.round(spcFee, 2));
            result.addData("OTHER_FEE1", StringTool.round(otherFee1, 2));
            result.addData("OTHER_FEE2", StringTool.round(otherFee2, 2));
            result.addData("OTHER_FEE3", StringTool.round(otherFee3, 2));
            result.setCount(count + 1);
        }
        table.setDSValue();
        table.setParmValue(result);
        // ==========pangben modify 20110704 查询所有数据没有过滤院区
        String sqlTable2 = BILSQL.getRecpDataAll(sTime, eTime, recpType, null);//全部票据 modify by wanglong 20130120
        // System.out.println("票据总查询" + sqlTable2);
        TParm table2Parm = new TParm(TJDODBTool.getInstance().select(sqlTable2));
        if (table2Parm.getErrCode() < 0) {
            System.out.println("总数据查询错误  " + table2Parm.getErrText());
            return;
        }
        int nowRecpCount = table2Parm.getCount("INV_NO");
        // ==========pangben modify 20110704 查询所有数据没有过滤院区
        String sqlTable2Left = BILSQL.getRecpDataLeft(sTime, eTime, recpType, null);//全部作废票据 modify by wanglong 20130120
        TParm table2LeftParm = new TParm(TJDODBTool.getInstance().select(sqlTable2Left));
        if (table2LeftParm.getErrCode() < 0) {
            System.out.println("作废数据查询错误  " + table2LeftParm.getErrText());
            return;
        }
        int cancelCount = table2LeftParm.getCount("INV_NO");
        table2 = new TParm();
        table2.addData("NOW_RECP", nowRecpCount >= 0 ? nowRecpCount : 0);
        table2.addData("CANCEL_RECP", cancelCount >= 0 ? cancelCount : 0);
        table2.addData("SYSTEM", "COLUMNS", "NOW_RECP");
        table2.addData("SYSTEM", "COLUMNS", "CANCEL_RECP");
        table2.setCount(1);
        String sqlTable3 = "";
        if ("IBS".equals(recpType)) {// modify by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpI(sTime, eTime, recpType, null);// modify by wanglong 20130120
        } else if ("OPB".equals(recpType)) {
            sqlTable3 = BILSQL.getCancelRecpO(sTime, eTime, recpType, null);// modify by wanglong 20130120
        } else if ("HRM".equals(recpType)) {// modify by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpH(sTime, eTime, "OPB", null);// modify by wanglong 20130120
        } else if ("REG".equals(recpType)) {// add by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpR(sTime, eTime, recpType, null);
        }
        // System.out.println("作废票据查询" + sqlTable3);
        table3 = new TParm(TJDODBTool.getInstance().select(sqlTable3));
        if (table3.getErrCode() < 0) {
            System.out.println("作废数据查询错误  " + table3.getErrText());
            return;
        }
    }

    /**
     * 汇出
     */
    public void onExport() {
        TParm table1 = table.getParmValue();
        if (table1.getCount() <= 1) {
            this.messageBox("没有需要汇出的数据");
            return;
        }
        String type = "";
        if (((TRadioButton) this.getComponent("SEL_O")).isSelected()) {// 门诊
            type = "门诊";
        } else if (((TRadioButton) this.getComponent("SEL_I")).isSelected()) {
            type = "住院";
        } else if (((TRadioButton) this.getComponent("SEL_H")).isSelected()) {// modify by wanglong 20130120
            type = "健检";
        } else if (((TRadioButton) this.getComponent("SEL_R")).isSelected()) {// add by wanglong 20130120
            type = "挂号";
        }
        table1.setData("TITLE", type + "发票明细");
        table1.setData("HEAD", table.getHeader());
        String[] header = table.getParmMap().split(";");
        for (int i = 0; i < header.length; i++) {
            table1.addData("SYSTEM", "COLUMNS", header[i]);
        }
        // System.out.println("table2" + table2);
        table2.setData("TITLE", type + "发票总数");// modify by wanglong 20130120
        table2.setData("HEAD", "使用发票数,140;作废发票数,200");// modify by wanglong 20130120
        table3.setData("TITLE",  type + "废票清单");// modify by wanglong 20130120
        table3.setData("HEAD", "发票号,120;账务序号,130;金额,200;状态,100");
        // System.out.println("table2" + table2);
        TParm[] execleTable = new TParm[]{table1, table2, table3 };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, type + "税务发票明细");// modify by wanglong 20130120
    }

    /**
     * 清空
     */
    public void onClear() {
        table.setDSValue();//modify by wanglong 20130120
    }

    /**
     * 设置表格的属性
     */
    public void onSetTableAttribute() {// add by wanglong 20130109
        if (((TRadioButton) this.getComponent("SEL_O")).isSelected()) {// 门诊
            String header="发票号,100;日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;账务序号,120;状态,60;姓名,100;合计,90,double,#########0.00;西药费,90,double,#########0.00";
            String chargeStr="CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("O");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;TOT_AMT;CHARGE0102;CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_H")).isSelected()) {// 健检
            String header="发票号,100;日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;账务序号,120;状态,60;团体名称,150;个人名称,100;合计,90,double,#########0.00;西药费,90,double,#########0.00";
            String chargeStr="CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("O");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,left;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;COMPANY_DESC;PAT_NAME;TOT_AMT;CHARGE0102;CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_I")).isSelected()) {// 住院  // modify by wanglong 20130120
            String header="发票号,100;日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;账务序号,120;状态,60;姓名,100;合计,90,double,#########0.00";
            String chargeStr="CHARGE01;CHARGE02;CHARGE0304;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE20;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("I");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                if(chargeArr[i].equals("CHARGE0304")){
                    header+=";西药费,90,double,#########0.00";
                }else{
                    header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00"; 
                }
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;TOT_AMT;CHARGE01;CHARGE02;CHARGE0304;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE20;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_R")).isSelected()) {// 挂号 // add by wanglong 20130120
            table.setHeader("发票号,100;日期,150,TimeStamp,yyyy/MM/dd HH:mm:ss;账务序号,120;状态,60;姓名,100;合计,90,double,#########0.00;挂号费,90,double,#########0.00;诊查费,90,double,#########0.00;附加费,90,double,#########0.00;其他费用1,90,double,#########0.00;其他费用2,90,double,#########0.00;其他费用3,90,double,#########0.00");
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;AR_AMT;REG_FEE_REAL;CLINIC_FEE_REAL;SPC_FEE;OTHER_FEE1;OTHER_FEE2;OTHER_FEE3");
        }
    }
    
    // =================================排序功能开始==================================
    /**
     * 加入表格排序监听方法
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // 表格中parm值一致,
                // 1.取paramw值;
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
                // 2.转成 vector列名, 行vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                // System.out.println("==strNames=="+strNames);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // System.out.println("==vct=="+vct);
                // 3.根据点击的列,对vector排序
                // System.out.println("sortColumn===="+sortColumn);
                // 表格排序的列名;
                String tblColumnName = table.getParmMap(sortColumn);
                // 转成parm中的列
                int col = tranParmColIndex(columnName, tblColumnName);
                // System.out.println("==col=="+col);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                TParm lastResultParm = new TParm();// 记录最终结果
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * 列名转列索引值
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                // System.out.println("tmp相等");
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
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
     * vectory转成param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // 行数据;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        return parmTable;
    }
    // ================================排序功能结束==================================
    
}
 