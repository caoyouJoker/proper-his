package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import jdo.opb.OPBCashierStaTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 门诊收费员工作量统计</p>
 *
 * <p>Description: 门诊收费员工作量统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-24
 * @version 1.0
 */
public class OPBCashierStaControl
    extends TControl {
    TParm DATA; //记录查询出的信息
    TTable TABLE;
    String STA_DATE; //记录统计区间
    public void onInit() {
        super.onInit();
        TABLE = (TTable)this.getComponent("Table");
        onClear();
        //================pangben modify 20110405 start 区域锁定
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110405 stop
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    /**
     * 查询
     */
    public void onQuery() {
        if (this.getValue("DATE_S") == null) {
            this.messageBox_("请选择查询起始日期");
            this.grabFocus("DATE_S");
        }
        if (this.getValue("DATE_E") == null) {
            this.messageBox_("请选择查询起始日期");
            this.grabFocus("DATE_E");
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",
                     StringTool.getString( (Timestamp)this.getValue("DATE_S"),
                                          "yyyyMMdd") +
                     StringTool.getString( (Timestamp)this.getValue("TIME_S"),
                                          "HHmmss"));
        parm.setData("DATE_E",
                     StringTool.getString( (Timestamp)this.getValue("DATE_E"),
                                          "yyyyMMdd") +
                     StringTool.getString( (Timestamp)this.getValue("TIME_E"),
                                          "HHmmss"));
        if (this.getValueString("CASHIER_CODE").length() > 0) {
            parm.setData("CASHIER_CODE", this.getValue("CASHIER_CODE"));
        }
        if (this.getValueString("ADM_TYPE").length() > 0) {
            parm.setData("ADM_TYPE", this.getValue("ADM_TYPE"));
        }
        //=======================pangben modify 20110405 start  添加查询条件
        //区域
       if(getValueString("REGION_CODE").length()>0)
           parm.setData("REGION_CODE", getValueString("REGION_CODE"));
       //=======================pangben modify 20110405 stop
        //记录统计区间 打印使用
        STA_DATE = StringTool.getString( (Timestamp)this.getValue("DATE_S"),
                                        "yyyy/MM/dd") + "～" +
            StringTool.
            getString( (Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd");
        //查询收费数据
        TParm in = OPBCashierStaTool.getInstance().selectIn(parm);
        //查询退费数据
        TParm out = OPBCashierStaTool.getInstance().selectOut(parm);
        //========pangben modify 20110414 start 跨院区查找时，分组显示区域的种类
        TParm regionParm = OPBCashierStaTool.getInstance().selectRegionCode(parm);
     // Map map=new HashMap();
     // map.put("",)
//      TParm regionParm=new TParm();
//      regionParm.addData("REGION_CODE","HIS");
//      regionParm.addData("REGION_CHN_DESC","河北燕达医院");
//      regionParm.addData("COUNT",1);
     // regionParm.addData("REGION_CODE","HIS01");
         //========pangben modify 20110414 start
        //从收费和退费数据中筛选所有收费人员CODE
        HashSet usersIn = new HashSet();
        HashSet usersOut = new HashSet();
        for (int i = 0; i < in.getCount(); i++) {
            //=========pangben modify 20110414 start 条件汇总显示数据
            String [] usersTemp=new String[3];
            usersTemp[0]=in.getValue("CASHIER_CODE", i);
            usersTemp[1]=in.getValue("REGION_CHN_DESC", i);
            usersTemp[2]=in.getValue("REGION_CODE", i);
            usersIn.add(usersTemp);
              //=========pangben modify 20110414 stop
        }
        for (int i = 0; i < out.getCount(); i++) {
            //=========pangben modify 20110414 start 条件汇总显示数据
            String [] usersTemp=new String[2];
            usersTemp[0]=out.getValue("CASHIER_CODE", i);
            usersTemp[1]=out.getValue("REGION_CHN_DESC", i);
            usersOut.add(usersTemp);
            //=========pangben modify 20110414 stop
        }

        DecimalFormat df = new DecimalFormat("0.00");
        DATA = new TParm(); //清空DATA原有数据
        //===========pangben modify 20110415 star 判断得到的表中那个数据总值多，因为数据操作人员的名称都是相同的所以循环最多的数据进行汇总
        Iterator iter ;
        if(usersIn.size()>=usersOut.size())
            iter = usersIn.iterator();
        else
            iter=  usersOut.iterator();
        //===========pangben modify 20110415 star
        int row = 0; //记录行数
        //根据每位收费员整理数据
        while (iter.hasNext()) {

            //=========pangben modify 20110414 start
            String[] user_code =(String[]) iter.next(); //收费员CODE
            String user_name = ""; //记录收费员姓名
            double sum = 0; //计算总金额
            boolean inFlg = false; //记录该收费员是否有收费数据
            boolean outFlg = false; //记录该收费员是否有退费数据
            //一条收费数据汇总
            for (int i = 0; i < in.getCount(); i++) {
                if (user_code[0].equals(in.getValue("CASHIER_CODE", i))&&user_code[2].equals(in.getValue("REGION_CODE", i))) {
                    DATA.setData("IN_NUM", row, in.getData("NUM", i));
                    DATA.setData("IN_MONEY", row,
                                 df.format(in.getData("MONEY", i)));
                    sum += in.getDouble("MONEY", i);
                    user_name = in.getValue("USER_NAME", i);
                    inFlg = true;
                }
            }
           //一条退费数据汇总
            for (int i = 0; i < out.getCount(); i++) {
                if (user_code[0].equals(out.getValue("CASHIER_CODE", i))&&user_code[2].equals(out.getValue("REGION_CODE", i))) {
                    DATA.setData("OUT_NUM", row, out.getData("NUM", i));
                    DATA.setData("OUT_MONEY", row,
                                 df.format(0 - out.getDouble("MONEY", i))); //退费显示负数
                    sum += (0 - out.getDouble("MONEY", i));
                    //如果用户名为空 证明收费信息中没有改收费员的数据 那么从退费信息中统计
                    if ("".equals(user_name)) {
                        user_name = out.getValue("USER_NAME", i);
                    }
                    outFlg = true;
                }
            }
            //如果不存在收费数据 那么补零
            if (!inFlg) {
                DATA.setData("IN_NUM", row, 0);
                DATA.setData("IN_MONEY", row, 0);
            }
            //如果不存在退费数据 那么补零
            if (!outFlg) {
                DATA.setData("OUT_NUM", row, 0);
                DATA.setData("OUT_MONEY", row, 0);
            }
            DATA.setData("USER_NAME", row, user_name);
            DATA.setData("SUM", row, df.format(sum));
            DATA.setData("REGION_CHN_DESC",row,user_code[1]);
            DATA.setData("REGION_CODE",row,user_code[2]);
            row++;
        }
        for (int j = 0; j < regionParm.getCount("REGION_CODE"); j++) {
            //计算合计行
            int inCountSum = 0; //收费总人数
            int outCountSum = 0; //退费总人数
            double inMoneySum = 0; //收费总金额
            double outMoneySum = 0; //退费总金额
            double sumMoney = 0; //总金额'
            for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {

                //========pangben modify 20110414 start
                if(regionParm.getValue("REGION_CODE",j).equals(DATA.getValue("REGION_CODE",i))){
                    inCountSum += DATA.getInt("IN_NUM", i);
                    outCountSum += DATA.getInt("OUT_NUM", i);
                    inMoneySum += DATA.getDouble("IN_MONEY", i);
                    outMoneySum += DATA.getDouble("OUT_MONEY", i);
                    sumMoney +=StringTool.round(DATA.getDouble("SUM", i),2) ;
                }
                //========pangben modify 20110414 stop
            }
            DATA.setData("REGION_CHN_DESC", row,regionParm.getValue("REGION_CHN_DESC",j));//========pangben modify 20110414
            DATA.setData("USER_NAME", row, "总计：");
            DATA.setData("IN_NUM", row, inCountSum);
            DATA.setData("OUT_NUM", row, outCountSum);
            DATA.setData("IN_MONEY", row, df.format(inMoneySum));
            DATA.setData("OUT_MONEY", row, df.format(outMoneySum));
            DATA.setData("SUM", row, df.format(StringTool.round(sumMoney,2)));
            row++;
        }
        for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {
        	DATA.addData("SUM_NUM", DATA.getInt("IN_NUM", i)+DATA.getInt("OUT_NUM", i));
		}
        TABLE.setParmValue(DATA);
    }

    /**
     * 清空
     */
    public void onClear() {
        //日期初始化
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("DATE_S", now);
        this.setValue("DATE_E", now);
        this.setValue("TIME_S", StringTool.getTimestamp("00:00:00", "hh:mm:ss"));
        this.setValue("TIME_E", now);
        this.clearValue("CASHIER_CODE;ADM_TYPE");
        TABLE.removeRowAll();
        DATA = new TParm();
    }

    /**
     * 打印
     */
    public void onPrint() {
        if (DATA.getCount("USER_NAME") <= 0) {
            return;
        }
        TParm printData = new TParm();
        TParm T1 = new TParm();
        for (int i = 0; i < DATA.getCount("USER_NAME"); i++) {
            T1.setRowData(i, DATA.getRow(i));
        }
        T1.setCount(DATA.getCount("USER_NAME"));
        //=========pangben modify 20110419 start
        T1.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 20110419 stop
        T1.addData("SYSTEM", "COLUMNS", "USER_NAME");
        T1.addData("SYSTEM", "COLUMNS", "IN_NUM");
        T1.addData("SYSTEM", "COLUMNS", "IN_MONEY");
        T1.addData("SYSTEM", "COLUMNS", "OUT_NUM");
        T1.addData("SYSTEM", "COLUMNS", "OUT_MONEY");
        T1.addData("SYSTEM", "COLUMNS", "SUM");
        printData.setData("T1", T1.getData());
        printData.setData("date", "TEXT", STA_DATE);
        //========pangben modify 20110419 start
        String region = ((TTable)this.getComponent("Table")).getParmValue().getRow(
                0).getValue("REGION_CHN_DESC");
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "门急诊收费员绩效统计");
        //========pangben modify 20110419 stop

        printData.setData("printDate", "TEXT",
                          StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd"));
        printData.setData("printUser", "TEXT", Operator.getName());
        this.openPrintDialog("%ROOT%\\config\\prt\\bil\\BILOpbCashierSta.jhw",
                             printData);
    }
}
