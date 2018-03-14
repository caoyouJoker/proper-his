package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;
import jdo.sys.SystemTool;    
import com.dongyang.ui.TComboBox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 部门明细账
 * </p>
 *
 * <p>
 * Description: 部门明细账
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDPhaDetailQueryControl
    extends TControl {

    // 汇总TABLE
    private TTable table_m;
    // 主库明细TABLE
    private TTable table_d_a;
    // 中库明细TABLE
    private TTable table_d_b;

    private Map map;

    public INDPhaDetailQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        // 初始化统计区间
        //luhai modify 2012-1-24 修改开始时间和结束时间 begin
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // 结束时间(本月的第一天)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//                                                     substring(0, 4) + "/" +
//                                                     TypeTool.getString(date).
//                                                     substring(5, 7) +
//                                                     "/01 00:00:00",
//                                                     "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // 起始时间(上个月第一天)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        setStartEndDate();
      //luhai modify 2012-1-24 修改开始时间和结束时间 end
        table_m = this.getTable("TABLE_M");
        table_d_a = this.getTable("TABLE_D_A");
        table_d_b =  this.getTable("TABLE_D_B");

        //填充状态集合
        map = new HashMap();
        map.put("VER", "验收");
        map.put("RET", "退库");
        map.put("THI", "其他入库");
        map.put("DEP", "请领");
        map.put("GIF", "调拨");
        map.put("THO", "其他出库");
        map.put("REG", "退货");
        map.put("FRO", "盘点");
        map.put("O_RET", "门诊退药");
        map.put("E_RET", "急诊退药");
        map.put("I_RET", "住院退药");
        map.put("TEC", "备药补充");
        map.put("EXM", "科室备药");
        map.put("WAS", "损耗");
        map.put("COS", "卫耗材领用");
        map.put("O_DPN", "门诊发药");
        map.put("E_DPN", "急诊发药");
        map.put("I_DPN", "住院发药");
    }
    /**
     *
     * 设置起始时间和结束时间，上月26-本月25
     */
    private void setStartEndDate(){
    	Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 结束时间(本月的25)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
                                                     substring(0, 4) + "/" +
                                                     TypeTool.getString(date).
                                                     substring(5, 7) +
                                                     "/25 23:59:59",
                                                     "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // 起始时间(上个月26)
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(date.getTime());
        cd.add(Calendar.MONTH, -1);
        Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());
        setValue("START_DATE",
        		endDateTimestamp.toString().substring(0, 4) +
                 "/" +
                 endDateTimestamp.toString().substring(5, 7) +
                 "/26 00:00:00");
    }
    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("请选择统计部门");
            return;
        }
        String org_code = this.getValueString("ORG_CODE");
        String start_date = this.getValueString("START_DATE").substring(0, 4)
            + this.getValueString("START_DATE").substring(5, 7)
            + this.getValueString("START_DATE").substring(8, 10)
            + this.getValueString("START_DATE").substring(11, 13)
            + this.getValueString("START_DATE").substring(14, 16)
            + this.getValueString("START_DATE").substring(17, 19);
        String end_date = this.getValueString("END_DATE").substring(0, 4)
            + this.getValueString("END_DATE").substring(5, 7)
            + this.getValueString("END_DATE").substring(8, 10)
            + this.getValueString("END_DATE").substring(11, 13)
            + this.getValueString("END_DATE").substring(14, 16)
            + this.getValueString("END_DATE").substring(17, 19);
        String qty_in = this.getValueString("CHECK_A");
        String qty_out = this.getValueString("CHECK_B");
        String qty_check = this.getValueString("CHECK_C");
        String order_code = this.getValueString("ORDER_CODE");
        
        String drugLvl2 = this.getValueString("CHECK_D");
        System.out.println("----------drugLvl2------"+drugLvl2);
        
        // 药品明细账汇总查询 		
        String slqM = INDSQL.getINDPhaSumQuery(org_code, start_date,
                                                   end_date,
                                                   order_code,drugLvl2);
        System.out.println("----------slqM------"+slqM);
        TParm parmM = new TParm(TJDODBTool.getInstance().select(slqM));

        // 区分主库，中库查询
        if (getRadioButton("IND_ORG_A").isSelected()) {
            // 药品明细账明细查询(主库)
            String slqD_A = INDSQL.getINDPhaDetailDQueryA(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code,drugLvl2);	
            System.out.println("----------slqD_A------"+slqD_A);
            TParm parmD_A = new TParm(TJDODBTool.getInstance().select(slqD_A));

            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();
                    this.messageBox("无查询结果");
                }
                else {
                    table_m.setParmValue(parmM);
                    //luhai add 加入合计行2012-2-22 begin
//                    addTotRowM();
                    //luhai add 加入合计行2012-2-22 end
                }
            }
            else {
                if (parmD_A == null || parmD_A.getCount("ORDER_DESC") <= 0) {
                    table_d_a.removeRowAll();
                    this.messageBox("无查询结果");
                }
                else {
                    table_d_a.setParmValue(parmD_A);
                    addTotRowD();
                }
            }
           setSumAmt();
        } 
        else {
            // 药品明细账明细查询(中库)
            String slqD_B = INDSQL.getINDPhaDetailDQueryB(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code,drugLvl2); 
            System.out.println("----------slqD_B------"+slqD_B);
            TParm parmD_B = new TParm(TJDODBTool.getInstance().select(slqD_B));

            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();
                    this.messageBox("无查询结果");
                }
                else {
                    table_m.setParmValue(parmM);
                    //luhai 2012-2-22 add totRow
//                    addTotRowM();
                }
            }
            else {
                if (parmD_B == null || parmD_B.getCount("ORDER_DESC") <= 0) {
                    table_d_b.removeRowAll();
                    this.messageBox("无查询结果");
                }
                else {
                    table_d_b.setParmValue(parmD_B);
                    //加入合计行
                    addTotRowD();
                }
            }
            setSumAmt();
        }
    }
    private void addTotRowM(){
        TParm tableParm = table_m.getParmValue();
        TParm parm = new TParm();
        //药品总账加入合计功能 begin
        double totLastTotAMT=0;
        double totStockOutAMT=0;
        double totStockInAMT=0;
        double totModiyAMT=0;
        double totTotAMT=0;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION",
                         tableParm.getValue("SPECIFICATION", i));
            parm.addData("LAST_TOTSTOCK_QTY",
                         tableParm.getValue("LAST_TOTSTOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC",
                         tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("LAST_TOTSTOCK_AMT",
                         tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
            parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
            totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
            parm.addData("STOCKOUT_QTY",
                         tableParm.getValue("STOCKOUT_QTY", i));
            parm.addData("STOCKOUT_AMT",
                         tableParm.getValue("STOCKOUT_AMT", i));
            totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
            parm.addData("CHECKMODI_QTY",
                         tableParm.getValue("CHECKMODI_QTY", i));
            parm.addData("CHECKMODI_AMT",
                         tableParm.getValue("CHECKMODI_AMT", i));
            totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
            totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
        }
        //加入合计行
        parm.addData("ORDER_DESC","合计：");
        parm.addData("SPECIFICATION",
                    "");
        parm.addData("LAST_TOTSTOCK_QTY",
                     "");
        parm.addData("UNIT_CHN_DESC",
                     "");
        parm.addData("LAST_TOTSTOCK_AMT",
        		totLastTotAMT );
        parm.addData("STOCKIN_QTY","");
        parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
        parm.addData("STOCKOUT_QTY",
                     "");
        parm.addData("STOCKOUT_AMT",
        		StringTool.round(totStockOutAMT,2));
        parm.addData("CHECKMODI_QTY",
                     "");
        parm.addData("CHECKMODI_AMT",
        		StringTool.round(totModiyAMT,2));
        parm.addData("STOCK_QTY", "");
        parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
        //加入合计行end
        parm.setCount(parm.getCount("ORDER_DESC"));
        this.table_m.setParmValue(parm);
    }
    private void addTotRowD(){
    	TParm parm=new TParm();
    	double sum_status_amt = 0;
        if (this.getRadioButton("IND_ORG_A").isSelected()) {
            //加入总计信息 begin
            double totAmt = 0;
            TParm tableParm = table_d_a.getParmValue();
            for (int i = 0; i < table_d_a.getRowCount(); i++) {
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             tableParm.getValue("STATUS", i));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));
                totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("ORG_CHN_DESC",
                             tableParm.getValue("ORG_CHN_DESC", i));
                sum_status_amt += Double.parseDouble(tableParm.getValue("AMT", i));
                if (!tableParm.getValue("STATUS", i).equals(tableParm.getValue("STATUS", i+1))) {
                	parm.addData("CHECK_DATE","小计：");
		            parm.addData("STATUS","");
		            parm.addData("ORDER_DESC","");
		            parm.addData("SPECIFICATION","");
		            parm.addData("QTY", "");
		            parm.addData("UNIT_CHN_DESC","");
		            parm.addData("OWN_PRICE","");
		            parm.addData("AMT", StringTool.round(sum_status_amt, 7));
		            parm.addData("ORG_CHN_DESC","");
		            sum_status_amt = 0;
				}
            }
            //加入合计信息 begin
            parm.addData("CHECK_DATE",
            		"合计：");
            parm.addData("STATUS",
            		"");
            parm.addData("ORDER_DESC",
            		"");
            parm.addData("SPECIFICATION",
            		"");
            parm.addData("QTY", "");
            parm.addData("UNIT_CHN_DESC",
            		"");
            parm.addData("OWN_PRICE","");
            parm.addData("AMT", StringTool.round(totAmt, 7));
            parm.addData("ORG_CHN_DESC",
            		"");

            //加入合计信息 end
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
            parm.addData("SYSTEM", "COLUMNS", "STATUS");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
            table_d_a.setParmValue(parm);
        }
        else {
            //加入总计信息
            double totAmt = 0;
            TParm tableParm = table_d_b.getParmValue();
            for (int i = 0; i < table_d_b.getRowCount(); i++) {
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             map.get(tableParm.getValue("STATUS", i)));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));
                totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("DEPT_CHN_DESC",
                             tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                sum_status_amt += Double.parseDouble(tableParm.getValue("AMT", i));
                if (!tableParm.getValue("STATUS", i).equals(tableParm.getValue("STATUS", i+1))) {
                	parm.addData("CHECK_DATE","小计：");
		            parm.addData("STATUS","");
		            parm.addData("ORDER_DESC","");
		            parm.addData("SPECIFICATION","");
		            parm.addData("QTY", "");
		            parm.addData("UNIT_CHN_DESC","");
		            parm.addData("OWN_PRICE","");
		            parm.addData("AMT", StringTool.round(sum_status_amt, 7));
		            parm.addData("DEPT_CHN_DESC","");
		            parm.addData("MR_NO","");
		            parm.addData("PAT_NAME","");
		            parm.addData("CASE_NO","");
		            sum_status_amt = 0;
				}
            }
            //加入合计行
	           parm.addData("CHECK_DATE","合计：");
	           parm.addData("STATUS",
	                       "");
	           parm.addData("ORDER_DESC",
	                       "");
	           parm.addData("SPECIFICATION",
	                        "");
	           parm.addData("QTY", "");
	           parm.addData("UNIT_CHN_DESC",
	                       "");
	           parm.addData("OWN_PRICE","");
	           parm.addData("AMT", StringTool.round(totAmt,7));
	           parm.addData("DEPT_CHN_DESC",
	                        "");
	           parm.addData("MR_NO","");
	           parm.addData("PAT_NAME","");
	           parm.addData("CASE_NO","");
	           //add 合计行end
	           parm.setCount(parm.getCount("CHECK_DATE"));
	           table_d_b.setParmValue(parm);
        }

    }
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;SUM_AMT";
        this.clearValue(clearStr);
        this.getRadioButton("IND_ORG_A").setSelected(true);
        onChangeOrgType();
        this.getRadioButton("TYPE_A").setSelected(true);
        onChangeInfoType();
        this.getCheckBox("CHECK_A").setSelected(true);
        this.getCheckBox("CHECK_B").setSelected(true);
        this.getCheckBox("CHECK_C").setSelected(true);
        //luhai modify 2012-1-24 改用初始化时间的共用方法 begin
//        // 初始化统计区间
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // 结束时间(本月的第一天)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//            substring(0, 4) + "/" +
//            TypeTool.getString(date).
//            substring(5, 7) +
//            "/01 00:00:00",
//            "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // 起始时间(上个月第一天)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        //初始化查询时间
        setStartEndDate();
        //luhai modify 2012-1-24 改用初始化时间的共用方法 end
        table_m.removeRowAll();
        table_d_a.removeRowAll();
        table_d_b.removeRowAll();
    }

    /**
     * 打印方法
     */
    public void onPrint() {
    	 //*********************************************
    	//药库明细账加入合计功能 luhai begin 2012-2-22
    	//*********************************************
        // 打印数据
        TParm date = new TParm();
        date.setData("ORG_CODE", "TEXT", "统计部门: " +
                     getTextFormat("ORG_CODE").getText());
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT", "统计区间: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) + " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19) );
        date.setData("DATE", "TEXT", "制表时间: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());

        // 表格数据
        TParm parm = new TParm();
        if (this.getRadioButton("TYPE_A").isSelected()) {
            //汇总
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //药库
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "药库药品总账");
            }
            else {
                //药房
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "药房药品总账");
            }
            TParm tableParm = table_m.getParmValue();
            double sum_amt = 0;//add by shendr 20131217 金额计算规则统一  先四舍五入再加总
            for (int i = 0; i < table_m.getRowCount(); i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("LAST_TOTSTOCK_QTY",
                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                // <---  identity by shendr 20131217 金额计算规则统一  先四舍五入再加总
                //parm.addData("LAST_TOTSTOCK_AMT",
                //             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
                parm.addData("LAST_TOTSTOCK_AMT",
                		StringTool.round(tableParm.getDouble("LAST_TOTSTOCK_AMT", i),2));
                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
                //parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
                parm.addData("STOCKIN_AMT", StringTool.round(tableParm.getDouble("STOCKIN_AMT", i),2));
                parm.addData("STOCKOUT_QTY",
                             tableParm.getValue("STOCKOUT_QTY", i));
                //parm.addData("STOCKOUT_AMT",
                //             tableParm.getValue("STOCKOUT_AMT", i));
                parm.addData("STOCKOUT_AMT",
                		StringTool.round(tableParm.getDouble("STOCKOUT_AMT", i),2));
                parm.addData("CHECKMODI_QTY",
                             tableParm.getValue("CHECKMODI_QTY", i));
                //parm.addData("CHECKMODI_AMT",
                //             tableParm.getValue("CHECKMODI_AMT", i));
                parm.addData("CHECKMODI_AMT",
                		StringTool.round(tableParm.getDouble("CHECKMODI_AMT", i),2));
                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
                //parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
                parm.addData("STOCK_AMT", StringTool.round(tableParm.getDouble("STOCK_AMT", i),2));
                sum_amt+=StringTool.round(tableParm.getDouble("STOCK_AMT", i),2);
                // ---->
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
            date.setData("TABLE", parm.getData());
            // 表尾数据
            //<--- identity by shendr 20131217
            //date.setData("SUM_AMT", "TEXT",
            //             "总金额： " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add 四舍五入 2012-2-13
            date.setData("SUM_AMT", "TEXT", "总金额： " + sum_amt);
            //--->
            // 调用打印方法
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryA.jhw", date);
        }
        else {
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //药库明细
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "药库药品明细表");
                TParm tableParm = table_d_a.getParmValue();
//                System.out.println("---   PRINT 药库药品明细表-"+tableParm);
                double sum_amt = 0;//add by shendr 20131217 金额计算规则统一  先四舍五入再加总
                for (int i = 0; i < table_d_a.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
                	String key = tableParm.getValue("STATUS", i);
//                	System.out.println("----------key:"+key);
//                	System.out.println("------------map>"+map.get(key));
                    parm.addData("STATUS",
                                 map.get(tableParm.getValue("STATUS", i)));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    //<--- identity by shendr 20131217
                    //parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("AMT", StringTool.round(tableParm.getDouble("AMT", i),2));
                    sum_amt += StringTool.round(tableParm.getDouble("AMT", i),2);
                    //--->
                    parm.addData("ORG_CHN_DESC",
                                 tableParm.getValue("ORG_CHN_DESC", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
                date.setData("TABLE", parm.getData());
                // 表尾数据
                //<--- identity by shendr 20131217
                //date.setData("SUM_AMT", "TEXT",
                //             "总金额： " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 四舍五入
                date.setData("SUM_AMT", "TEXT", "总金额： " + sum_amt);
                //--->
                // 调用打印方法
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryB.jhw", date);
            }
            else {
                //药房明细
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "药房药品明细表");
                TParm tableParm = table_d_b.getParmValue();
                double sum_amt = 0;//add by shendr 20131217 金额计算规则统一  先四舍五入再加总
                for (int i = 0; i < table_d_b.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>=11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
                    parm.addData("STATUS",
                                 map.get(tableParm.getValue("STATUS", i)));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    //<--- identity by shendr 20131217
                    //parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("AMT", StringTool.round(tableParm.getDouble("AMT", i),2));
                    sum_amt += StringTool.round(tableParm.getDouble("AMT", i),2);
                    //--->
                    parm.addData("DEPT_CHN_DESC",
                                 tableParm.getValue("DEPT_CHN_DESC", i));
                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
                date.setData("TABLE", parm.getData());
                // 表尾数据
                //<--- identity by shendr 20131217
                //date.setData("SUM_AMT", "TEXT",
                //             "总金额： " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 四舍五入
                date.setData("SUM_AMT", "TEXT", "总金额： " + sum_amt);
                //--->
                // 调用打印方法
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryC.jhw", date);
            }
        }
//        // 打印数据
//        TParm date = new TParm();
//        date.setData("ORG_CODE", "TEXT", "统计部门: " +
//                     getTextFormat("ORG_CODE").getText());
//        String start_date = getValueString("START_DATE");
//        String end_date = getValueString("END_DATE");
//        date.setData("DATE_AREA", "TEXT", "统计区间: " +
//                     start_date.substring(0, 4) + "/" +
//                     start_date.substring(5, 7) + "/" +
//                     start_date.substring(8, 10) + " " +
//                     start_date.substring(11, 13) + ":" +
//                     start_date.substring(14, 16) + ":" +
//                     start_date.substring(17, 19) + " ~ " +
//                     end_date.substring(0, 4) + "/" +
//                     end_date.substring(5, 7) + "/" +
//                     end_date.substring(8, 10) + " " +
//                     end_date.substring(11, 13) + ":" +
//                     end_date.substring(14, 16) + ":" +
//                     end_date.substring(17, 19) );
//        date.setData("DATE", "TEXT", "制表时间: " +
//                     SystemTool.getInstance().getDate().toString().
//                     substring(0, 10).replace('-', '/'));
//        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
//
//        // 表格数据
//        TParm parm = new TParm();
//        if (this.getRadioButton("TYPE_A").isSelected()) {
//            //汇总
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //药库
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "药库药品总账");
//            }
//            else {
//                //药房
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "药房药品总账");
//            }
//            TParm tableParm = table_m.getParmValue();
//            //药品总账加入合计功能 begin
//            double totLastTotAMT=0;
//            double totStockOutAMT=0;
//            double totStockInAMT=0;
//            double totModiyAMT=0;
//            double totTotAMT=0;
//            for (int i = 0; i < table_m.getRowCount(); i++) {
//                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
//                parm.addData("SPECIFICATION",
//                             tableParm.getValue("SPECIFICATION", i));
//                parm.addData("LAST_TOTSTOCK_QTY",
//                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
//                parm.addData("UNIT_CHN_DESC",
//                             tableParm.getValue("UNIT_CHN_DESC", i));
//                parm.addData("LAST_TOTSTOCK_AMT",
//                             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
//                parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
//                totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
//                parm.addData("STOCKOUT_QTY",
//                             tableParm.getValue("STOCKOUT_QTY", i));
//                parm.addData("STOCKOUT_AMT",
//                             tableParm.getValue("STOCKOUT_AMT", i));
//                totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
//                parm.addData("CHECKMODI_QTY",
//                             tableParm.getValue("CHECKMODI_QTY", i));
//                parm.addData("CHECKMODI_AMT",
//                             tableParm.getValue("CHECKMODI_AMT", i));
//                totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
//                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
//                parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
//                totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
//            }
//            //加入合计行
//            parm.addData("ORDER_DESC","合计：");
//            parm.addData("SPECIFICATION",
//                        "");
//            parm.addData("LAST_TOTSTOCK_QTY",
//                         "");
//            parm.addData("UNIT_CHN_DESC",
//                         "");
//            parm.addData("LAST_TOTSTOCK_AMT",
//            		totLastTotAMT );
//            parm.addData("STOCKIN_QTY","");
//            parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
//            parm.addData("STOCKOUT_QTY",
//                         "");
//            parm.addData("STOCKOUT_AMT",
//            		StringTool.round(totStockOutAMT,2));
//            parm.addData("CHECKMODI_QTY",
//                         "");
//            parm.addData("CHECKMODI_AMT",
//            		StringTool.round(totModiyAMT,2));
//            parm.addData("STOCK_QTY", "");
//            parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
//            //加入合计行end
//            parm.setCount(parm.getCount("ORDER_DESC"));
//            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
//            date.setData("TABLE", parm.getData());
//            // 表尾数据
//            date.setData("SUM_AMT", "TEXT",
//                         "总金额： " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add 四舍五入 2012-2-13
//            // 调用打印方法
//            this.openPrintWindow(
//                "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryA.jhw", date);
//        }
//        else {
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //药库明细
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "药库药品明细表");
//                //加入总计信息 begin
//                double totAmt = 0;
//                TParm tableParm = table_d_a.getParmValue();
//                for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("ORG_CHN_DESC",
//                                 tableParm.getValue("ORG_CHN_DESC", i));
//                }
//                //加入合计信息 begin
//                parm.addData("CHECK_DATE",
//                		"合计：");
//                parm.addData("STATUS",
//                		"");
//                parm.addData("ORDER_DESC",
//                		"");
//                parm.addData("SPECIFICATION",
//                		"");
//                parm.addData("QTY", "");
//                parm.addData("UNIT_CHN_DESC",
//                		"");
//                parm.addData("OWN_PRICE","");
//                parm.addData("AMT", StringTool.round(totAmt, 2));
//                parm.addData("ORG_CHN_DESC",
//                		"");
//
//                //加入合计信息 end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
//                date.setData("TABLE", parm.getData());
//                // 表尾数据
//                date.setData("SUM_AMT", "TEXT",
//                             "总金额： " +  StringTool.round(totAmt, 2));//luhai 2012-2-13 四舍五入
//                // 调用打印方法
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryB.jhw", date);
//            }
//            else {
//                //药房明细
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "药房药品明细表");
//                //加入总计信息
//                double totAmt = 0;
//                TParm tableParm = table_d_b.getParmValue();
//                for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("DEPT_CHN_DESC",
//                                 tableParm.getValue("DEPT_CHN_DESC", i));
//                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
//                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
//                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
//                }
//                //加入合计行
//		           parm.addData("CHECK_DATE","合计：");
//		           parm.addData("STATUS",
//		                       "");
//		           parm.addData("ORDER_DESC",
//		                       "");
//		           parm.addData("SPECIFICATION",
//		                        "");
//		           parm.addData("QTY", "");
//		           parm.addData("UNIT_CHN_DESC",
//		                       "");
//		           parm.addData("OWN_PRICE","");
//		           parm.addData("AMT", StringTool.round(totAmt,2));
//		           parm.addData("DEPT_CHN_DESC",
//		                        "");
//		           parm.addData("MR_NO","");
//		           parm.addData("PAT_NAME","");
//		           parm.addData("CASE_NO","");
//           //add 合计行end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
//                date.setData("TABLE", parm.getData());
//                // 表尾数据
//                date.setData("SUM_AMT", "TEXT",
//                             "总金额： " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 四舍五入
//                // 调用打印方法
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryC.jhw", date);
//            }
//        }
   	 //*********************************************
    	//药库明细账加入合计功能 luhai end 2012-2-22
    	//*********************************************
    }

    /**
     * 计算总金额
     */
    private void setSumAmt() {
        double amt = 0;
//        if (this.getRadioButton("IND_ORG_A").isSelected()) {
//        		for (int i = 0; i < table_m.getRowCount(); i++) {
//        			amt += table_m.getItemDouble(i, "STOCK_AMT");
//        		}
//        }
//        else if (this.getRadioButton("TYPE_A").isSelected()) {
//            for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                amt += table_d_a.getItemDouble(i, "AMT");
//            }
//        }
//        else {
//            for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                amt += table_d_b.getItemDouble(i, "AMT");
//            }
//        }
        if (this.getRadioButton("TYPE_A").isSelected()){
    		for (int i = 0; i < table_m.getRowCount()-1; i++) {//去掉合计行
    			amt += table_m.getItemDouble(i, "STOCK_AMT");
    		}
        }else{
		      if (this.getRadioButton("IND_ORG_A").isSelected()) {
		          for (int i = 0; i < table_d_a.getRowCount()-1; i++) {//去掉合计行
	        		  amt += table_d_a.getItemDouble(i, "AMT");
		          }
		      }
		      else {
		          for (int i = 0; i < table_d_b.getRowCount()-1; i++) {//去掉合计行
	        		  amt += table_d_b.getItemDouble(i, "AMT");
		          }
		      }
		      amt = amt/2; //去掉小计行
        }
        this.setValue("SUM_AMT", amt);
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * 统计单位变更事件
     */
    public void onChangeOrgType() {
        TextFormatINDOrg ind_org = (TextFormatINDOrg)this.getComponent(
            "ORG_CODE");
        ind_org.setValue("");
        if (getRadioButton("IND_ORG_A").isSelected()) {
            ind_org.setOrgType("A");
        }
        else {
            ind_org.setOrgType("B");
        }
        ind_org.onQuery();
        onChangeInfoType();
    }

    /**
     * 类型变更事件
     */
    public void onChangeInfoType() {
        if (getRadioButton("TYPE_A").isSelected()) {
            getCheckBox("CHECK_A").setEnabled(false);
            getCheckBox("CHECK_B").setEnabled(false);
            getCheckBox("CHECK_C").setEnabled(false);
            table_m.setVisible(true);
            table_d_a.setVisible(false);
            table_d_b.setVisible(false);
        }
        else {
            getCheckBox("CHECK_A").setEnabled(true);
            getCheckBox("CHECK_B").setEnabled(true);
            getCheckBox("CHECK_C").setEnabled(true);
            table_m.setVisible(false);
            if (getRadioButton("IND_ORG_A").isSelected()) {
                table_d_a.setVisible(true);
                table_d_b.setVisible(false);
            }
            else {
                table_d_a.setVisible(false);
                table_d_b.setVisible(true);
            }
        }
        setSumAmt();
    }
    
    /**
	 * 汇出Excel
	 * @author shendr 20140212
	 */
	public void onExport() {
		if (table_m.isShowing()) {
			if (table_m.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_m, "药品明细账汇总");
		}
		if (table_d_a.isShowing()) {
			if (table_d_a.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d_a, "药品明细账明细");
		}
		if (table_d_b.isShowing()) {
			if (table_d_b.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d_b, "药品明细账明细");
		}
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

    /**
     * 得到CheckBox对象
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
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
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
