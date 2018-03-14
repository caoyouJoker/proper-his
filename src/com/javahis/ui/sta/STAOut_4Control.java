package com.javahis.ui.sta;

import com.dongyang.control.*;
import jdo.sta.STAOut_4Tool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.text.DecimalFormat;

/**
 * <p>Title: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Description: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_4Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false;//记录是否提交
    private String DATA_StaDate = "";//记录目前显示的数据的主键(日期)
    private TTable table1;
    private TTable table2;
    private TTable table3;
    private TTable table4;
    private String LEADER = "";//记录是否是组长权限  如果LEADER=2那么就是组长权限
    /**
     * 初始化
     */
    public void onInit(){
        super.init();
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        table1 = (TTable)this.getComponent("Table1");
        table2 = (TTable)this.getComponent("Table2");
        table3 = (TTable)this.getComponent("Table3");
        table4 = (TTable)this.getComponent("Table4");
        this.addEventListener("Table1->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table2->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table3->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table4->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        //初始化权限
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }
    public void onClear(){
        this.clearValue("STA_DATE;Submit");
    }
    /**
     * 查询
     */
    public void onQuery(){
        STA_CONFIRM_FLG = false;
        String STA_DATE = this.getText("STA_DATE").replace("/","");
        if(STA_DATE.trim().length()<=0){
            this.messageBox_("请选择要统计的年月");
            return;
        }
        //检查是否可以重新生成数据
        if(!canGeneration(STA_DATE)){
            //如果不能重新生成数据，绑定原有数据
            gridBind(STA_DATE);//数据绑定
            return;
        }
        TParm parm = STAOut_4Tool.getInstance().selectData(STA_DATE,Operator.getRegion());//=========pangben modify 20110523
        if(parm.getErrCode()<0){
            this.messageBox_("数据统计错误 " +parm.getErrText() + parm.getErrName());
            return;
        }
        //填入必须信息
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //===========pangben modify 20110523 start
        parm.setData("REGION_CODE",Operator.getRegion());
       //===========pangben modify 20110523 stop
        parm.setData("CONFIRM_FLG","N");
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STAOut_4Action",
            "insertSTA_OUT_04", parm);
        if(result.getErrCode()<0){
            this.messageBox_("数据生成失败！"+result.getErrText());
            return;
        }
        this.messageBox_("数据生成完毕");
        gridBind(STA_DATE);
    }
    /**
     * 表格数据绑定
     */
    private void gridBind(String STADATE){
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        TTable table3 = (TTable)this.getComponent("Table3");
        TTable table4 = (TTable)this.getComponent("Table4");
        table1.setSQL(STASQLTool.getInstance().getSTA_OUT_04_1(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table1.retrieve();
        table1.setDSValue();
        table2.setSQL(STASQLTool.getInstance().getSTA_OUT_04_2(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table2.retrieve();
        table2.setDSValue();
        table3.setSQL(STASQLTool.getInstance().getSTA_OUT_04_3(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table3.retrieve();
        table3.setDSValue();
        table4.setSQL(STASQLTool.getInstance().getSTA_OUT_04_4(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table4.retrieve();
        table4.setDSValue();
        DATA_StaDate = STADATE;
    }
    /**
     * 检查是否可以生成数据
     * 检核是否已经该月份的数据 数据是否已经提交  true:可以生成  false:不可以生成
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE){
        boolean can=false;
        //检核数据状态
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OUT_04",STADATE,Operator.getRegion());//====pangben modify 20110523
        //数据已经提交
        if (reFlg == 2) {
            this.messageBox_("数据已经提交，不能重新生成！");
            can = false;
            STA_CONFIRM_FLG = true; //标识数据已经提交
        }
        //数据存在但没有提交
        else if (reFlg == 1) {
            switch (this.messageBox("提示信息",
                                    "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
                case 0: //生成
                    can = true;
                    break;
                case 1: //不生成
                    can = false;
                    break;
            }
        }else if(reFlg==0){//没有数据
            can=true;
        }else if(reFlg==-1){//数据检核错误
            can=false;
        }
        if(STA_CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);

        return can;
    }
    /**
     * 保存
     */
    public void onSave(){
        //如果不是组长权限  那么已经提交的数据不可再修改
        if(!LEADER.equals("2")){
            //判断数据是否已经提交
            if (STA_CONFIRM_FLG) {
                this.messageBox_("数据已经提交，不可再次修改！");
                return;
            }
        }
        String message = "保存成功！";//提示信息
        table1.acceptText();
        table2.acceptText();
        table3.acceptText();
        table4.acceptText();
        //检查表中是否有数据
        if(table1.getRowCount()<=0||table2.getRowCount()<=0||table3.getRowCount()<=0)
            return;
        TDataStore ds = table3.getDataStore();
        ds.setItem(0,"OPT_USER",Operator.getID());//设置修改人员信息
        ds.setItem(0,"OPT_TERM",Operator.getIP());//设置修改IP
        ds.setItem(0,"OPT_DATE",SystemTool.getInstance().getDate());//设置修改时间
        //判断是否提交
        if(((TCheckBox)this.getComponent("Submit")).isSelected()){
            ds.setItem(0,"CONFIRM_FLG","Y");//设置状态为 提交
            ds.setItem(0,"CONFIRM_USER",Operator.getID());//设置提交人
            ds.setItem(0,"CONFIRM_DATE",SystemTool.getInstance().getDate());//提交时间
            message = "提交成功!";
        }
        TParm parm = new TParm();
        parm.setData("SQL1",table1.getUpdateSQL());
        parm.setData("SQL2",table2.getUpdateSQL());
        parm.setData("SQL3",ds.getUpdateSQL());
        parm.setData("SQL4",table4.getUpdateSQL());
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_4Action",
            "updateSTA_OUT_04", parm);
        if(re.getErrCode()<0){
            this.messageBox_("操作失败！");
            return;
        }
        this.messageBox_(message);
        if(((TCheckBox)this.getComponent("Submit")).isSelected())
            STA_CONFIRM_FLG = true;
    }
    /**
     * 打印
     */
    public void onPrint(){
        if (DATA_StaDate.trim().length() <= 0) {
            this.messageBox("没有需要打印的数据");
            return;
        }
//        if (!STA_CONFIRM_FLG) {
//            this.messageBox_("数据提交后才能生成报表");
//            return;
//        }
        TParm printParm = new TParm();
        String dataDate = DATA_StaDate.substring(0,4) + "年" + DATA_StaDate.subSequence(4,6) + "日";
        TParm Basic = new TParm();//报表基本参数
        Basic.setData("Date",dataDate);//数据日期
        Basic.setData("Unit",Operator.getHospitalCHNFullName());//填报单位
        TParm dataP = STAOut_4Tool.getInstance().selectPrint(DATA_StaDate,Operator.getRegion());//=======pangben modify 20110523
        DecimalFormat df = new DecimalFormat("0.00");
        TParm data = new TParm();
        data.addData("DATA_01",dataP.getValue("DATA_01",0));
        data.addData("DATA_02",dataP.getValue("DATA_02",0));
        data.addData("DATA_03",dataP.getValue("DATA_03",0));
        data.addData("DATA_04",dataP.getValue("DATA_04",0));
        data.addData("DATA_05",dataP.getValue("DATA_05",0));
        data.addData("DATA_06",dataP.getValue("DATA_06",0));
        data.addData("DATA_07",dataP.getValue("DATA_07",0));
        data.addData("DATA_08",dataP.getValue("DATA_08",0));
        data.addData("DATA_09",dataP.getValue("DATA_09",0));
        data.addData("DATA_10",dataP.getValue("DATA_10",0));
        data.addData("DATA_11",dataP.getValue("DATA_11",0));
        data.addData("DATA_12",df.format(dataP.getDouble("DATA_12",0)));
        data.addData("DATA_13",df.format(dataP.getDouble("DATA_13",0)));
        data.addData("DATA_14",df.format(dataP.getDouble("DATA_14",0)));
        data.addData("DATA_15",df.format(dataP.getDouble("DATA_15",0)));
        data.addData("DATA_16",df.format(dataP.getDouble("DATA_16",0)));
        data.addData("DATA_17",df.format(dataP.getDouble("DATA_17",0)));
        data.addData("DATA_18",df.format(dataP.getDouble("DATA_18",0)));
        data.addData("DATA_19",df.format(dataP.getDouble("DATA_19",0)));
        data.addData("DATA_20",df.format(dataP.getDouble("DATA_20",0)));
        data.addData("DATA_21",df.format(dataP.getDouble("DATA_21",0)));
        data.addData("DATA_22",df.format(dataP.getDouble("DATA_22",0)));
        data.addData("DATA_23",df.format(dataP.getDouble("DATA_23",0)));
        data.addData("DATA_24",df.format(dataP.getDouble("DATA_24",0)));
        data.addData("DATA_25",df.format(dataP.getDouble("DATA_25",0)));
        data.addData("DATA_26",df.format(dataP.getDouble("DATA_26",0)));
        data.addData("DATA_27",df.format(dataP.getDouble("DATA_27",0)));
        data.addData("DATA_28",df.format(dataP.getDouble("DATA_28",0)));
        data.addData("DATA_29",df.format(dataP.getDouble("DATA_29",0)));
        data.addData("DATA_30",df.format(dataP.getDouble("DATA_30",0)));
        data.addData("DATA_31",df.format(dataP.getDouble("DATA_31",0)));
        data.addData("DATA_32",df.format(dataP.getDouble("DATA_32",0)));
        data.addData("DATA_33",df.format(dataP.getDouble("DATA_33",0)));
        data.addData("DATA_34",df.format(dataP.getDouble("DATA_34",0)));
        data.addData("DATA_35",df.format(dataP.getDouble("DATA_35",0)));
        data.addData("DATA_36",df.format(dataP.getDouble("DATA_36",0)));
        data.addData("DATA_37",df.format(dataP.getDouble("DATA_37",0)));
        data.addData("DATA_38",df.format(dataP.getDouble("DATA_38",0)));
        data.addData("DATA_39",df.format(dataP.getDouble("DATA_39",0)));
        data.addData("DATA_40",df.format(dataP.getDouble("DATA_40",0)));
        data.addData("DATA_41",df.format(dataP.getDouble("DATA_41",0)));
        data.addData("DATA_42",dataP.getValue("DATA_42",0));
        data.addData("DATA_43",df.format(dataP.getDouble("DATA_43",0)));
        data.addData("DATA_44",df.format(dataP.getDouble("DATA_44",0)));
        data.addData("DATA_45",df.format(dataP.getDouble("DATA_45",0)));
        data.addData("DATA_46",df.format(dataP.getDouble("DATA_46",0)));
        data.addData("DATA_47",df.format(dataP.getDouble("DATA_47",0)));
        data.addData("DATA_48",df.format(dataP.getDouble("DATA_48",0)));
        data.addData("DATA_49",df.format(dataP.getDouble("DATA_49",0)));
        data.addData("DATA_50",df.format(dataP.getDouble("DATA_50",0)));
        data.addData("DATA_51",df.format(dataP.getDouble("DATA_51",0)));
        data.addData("DATA_52",df.format(dataP.getDouble("DATA_52",0)));
        data.addData("DATA_53",df.format(dataP.getDouble("DATA_53",0)));
        data.addData("DATA_54",df.format(dataP.getDouble("DATA_54",0)));
        data.addData("DATA_55",df.format(dataP.getDouble("DATA_55",0)));
        data.addData("DATA_56",df.format(dataP.getDouble("DATA_56",0)));
        data.addData("DATA_57",df.format(dataP.getDouble("DATA_57",0)));
        data.addData("DATA_58",df.format(dataP.getDouble("DATA_58",0)));
        data.addData("DATA_59",df.format(dataP.getDouble("DATA_59",0)));
        data.addData("DATA_60",df.format(dataP.getDouble("DATA_60",0)));
        data.addData("DATA_61",df.format(dataP.getDouble("DATA_61",0)));
        data.setCount(1);
        printParm.setData("Basic",Basic.getData());
        printParm.setData("Data",data.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_04.jhw", printParm);
    }

    /**
     * 单元格值改变时间
     */
    public void onCellChange(Object obj) {
        //当前编辑的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        TTable table = node.getTable();
        table1.acceptText();
        table2.acceptText();
        table3.acceptText();
        table4.acceptText();
        DecimalFormat df = new DecimalFormat("0.00");
        String tableName = table.getTag(); //表名
//        this.messageBox_(tableName);
        int colunm = node.getColumn(); //获取选中列
        if("Table2".equals(tableName)&&(colunm==3||colunm==4||colunm==5)){
            double DATA_15,DATA_16,DATA_17;
            if(colunm==3)
                DATA_15 = Double.valueOf(node.getValue().toString());
            else
                DATA_15 = table2.getItemDouble(0, "DATA_15");
            if(colunm==4)
                DATA_16 = Double.valueOf(node.getValue().toString());
            else
                DATA_16 = table2.getItemDouble(0, "DATA_16");
            if(colunm==5)
                DATA_17 = Double.valueOf(node.getValue().toString());
            else
                DATA_17 = table2.getItemDouble(0, "DATA_17");
            //门诊收入小计
            table2.setItem(0, "DATA_14",DATA_15+DATA_16+DATA_17);
        }
        if("Table2".equals(tableName)&&(colunm==7||colunm==8||colunm==9||colunm==10)){
            double DATA_19,DATA_20,DATA_21,DATA_22;
            if(colunm==7)
                DATA_19 = Double.valueOf(node.getValue().toString());
            else
                DATA_19 = table2.getItemDouble(0, "DATA_19");
            if(colunm==8)
                DATA_20 = Double.valueOf(node.getValue().toString());
            else
                DATA_20 = table2.getItemDouble(0, "DATA_20");
            if(colunm==9)
                DATA_21 = Double.valueOf(node.getValue().toString());
            else
                DATA_21 = table2.getItemDouble(0, "DATA_21");
            if(colunm==10)
                DATA_22 = Double.valueOf(node.getValue().toString());
            else
                DATA_22 = table2.getItemDouble(0, "DATA_22");

            //住院小计
            table2.setItem(0, "DATA_18",DATA_19+DATA_20+DATA_21+DATA_22);
        }
        if("Table2".equals(tableName)&&(colunm==12||colunm==13)){
            double DATA_24,DATA_25;
            if(colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            if(colunm==13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            //药品收入小计
            table2.setItem(0, "DATA_23",DATA_24 +DATA_25);
        }
        if("Table2".equals(tableName)&&(colunm==2||colunm==6||colunm==11)){
            double DATA_14,DATA_18,DATA_23;
            if(colunm==2)
                DATA_14 = Double.valueOf(node.getValue().toString());
            else
                DATA_14 = table2.getItemDouble(0, "DATA_14");
            if(colunm==6)
                DATA_18 = Double.valueOf(node.getValue().toString());
            else
                DATA_18 = table2.getItemDouble(0, "DATA_18");
            if(colunm==11)
                DATA_23 = Double.valueOf(node.getValue().toString());
            else
                DATA_23 = table2.getItemDouble(0, "DATA_23");
            //计算业务收入合计=门诊收入小计+住院小计+药品收入小计
            table2.setItem(0, "DATA_13",DATA_14+DATA_18+DATA_23);
        }
        if("Table2".equals(tableName)&&(colunm==1||colunm==14||colunm==15||colunm==16)){
            double DATA_13,DATA_26,DATA_27,DATA_28;
            if(colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0, "DATA_13");
            if(colunm==14)
                DATA_26 = Double.valueOf(node.getValue().toString());
            else
                DATA_26 = table2.getItemDouble(0, "DATA_26");
            if(colunm==15)
                DATA_27 = Double.valueOf(node.getValue().toString());
            else
                DATA_27 = table2.getItemDouble(0, "DATA_27");
            if(colunm==16)
                DATA_28 = Double.valueOf(node.getValue().toString());
            else
                DATA_28 = table2.getItemDouble(0, "DATA_28");
            //计算收入总计 = 业务收入总计+业务补助+专项补助+其他收入
            table2.setItem(0, "DATA_12",DATA_13 +DATA_26+DATA_27+DATA_28);
        }
        if("Table3".equals(tableName)&&(colunm==2||colunm==4||colunm==5)){
            double DATA_31,DATA_33,DATA_34;
            if(colunm==2)
                DATA_31 = Double.valueOf(node.getValue().toString());
            else
                DATA_31 = table3.getItemDouble(0, "DATA_31");
            if(colunm==4)
                DATA_33 = Double.valueOf(node.getValue().toString());
            else
                DATA_33 = table3.getItemDouble(0, "DATA_33");
            if(colunm==5)
                DATA_34 = Double.valueOf(node.getValue().toString());
            else
                DATA_34 = table3.getItemDouble(0, "DATA_34");
            //计算表3  业务支出合计 = 人员经费小计 + 药品费 + 其他业务支出
            table3.setItem(0, "DATA_30",DATA_31+DATA_33+DATA_34);
        }
        if("Table3".equals(tableName)&&(colunm==1||colunm==6)){
            double DATA_30,DATA_35;
            if(colunm==1)
                DATA_30 = Double.valueOf(node.getValue().toString());
            else
                DATA_30 = table3.getItemDouble(0, "DATA_30");
            if(colunm==6)
                DATA_35 = Double.valueOf(node.getValue().toString());
            else
                DATA_35 = table3.getItemDouble(0, "DATA_35");
            //计算表3  支出总计 = 业务支出合计 + 其他支出
            table3.setItem(0, "DATA_29",DATA_30+DATA_35);
        }
        //平均每诊疗人次医疗费 药费 DATA_25/DATA_05
        if (("Table2".equals(tableName) && colunm == 13) ||
            ("Table1".equals(tableName) && colunm == 4)) {
            double DATA_05,DATA_25;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            if (DATA_05 != 0) {
                table4.setItem(0, "DATA_48",
                               df.format(DATA_25 / DATA_05));
            }
        }
        //平均每诊疗人次医疗费 检查费 DATA_16/DATA_05
        if (("Table2".equals(tableName) && colunm == 4) ||
            ("Table1".equals(tableName) && colunm == 4)) {
            double DATA_05,DATA_16;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 4)
                DATA_16 = Double.valueOf(node.getValue().toString());
            else
                DATA_16 = table2.getItemDouble(0, "DATA_16");
            if (DATA_05 != 0) {
                table4.setItem(0, "DATA_49",
                               df.format(DATA_16 / DATA_05));
            }
        }
        //平均每诊疗人次医疗费 合计 DATA_48+DATA_49
        if (("Table1".equals(tableName) && colunm == 4)||
            ("Table2".equals(tableName) && colunm == 2)||
            ("Table2".equals(tableName) && colunm ==13)) {
            double DATA_14,DATA_25,DATA_05;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 2)
                DATA_14 = Double.valueOf(node.getValue().toString());
            else
                DATA_14 = table2.getItemDouble(0, "DATA_14");
            if ("Table2".equals(tableName) && colunm == 13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            table4.setItem(0, "DATA_47",
                           df.format((DATA_14+DATA_25)/DATA_05));
        }
        double DATA_06;
        if("Table1".equals(tableName)&&colunm==5)
            DATA_06 = Double.valueOf(node.getValue().toString());
        else
            DATA_06 = table1.getItemDouble(0, "DATA_06");
        if(DATA_06!=0){
            double DATA_19,DATA_24,DATA_20,DATA_21;
            //平均每一出院者医疗费 床位费 DATA_19/DATA_06
            if ("Table2".equals(tableName) && colunm == 7)
                DATA_19 = Double.valueOf(node.getValue().toString());
            else
                DATA_19 = table2.getItemDouble(0, "DATA_19");
            table4.setItem(0, "DATA_51", df.format(DATA_19 / DATA_06));

            //平均每一出院者医疗费 药费 DATA_24/DATA_06
            if("Table2".equals(tableName)&&colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            table4.setItem(0, "DATA_52",df.format(DATA_24/DATA_06));
            //平均每一出院者医疗费 治疗费 DATA_20/DATA_06
            if("Table2".equals(tableName)&&colunm==8)
                DATA_20 = Double.valueOf(node.getValue().toString());
            else
                DATA_20 = table2.getItemDouble(0, "DATA_20");
            table4.setItem(0, "DATA_53",
                           df.format(DATA_20/DATA_06));
            //平均每一出院者医疗费 检查费 DATA_21/DATA_06
            if("Table2".equals(tableName)&&colunm==9)
                DATA_21 = Double.valueOf(node.getValue().toString());
            else
                DATA_21 = table2.getItemDouble(0, "DATA_21");
            table4.setItem(0, "DATA_54",
                           df.format(DATA_21/DATA_06));
            //出院者平均每天住院医疗费
            double DATA_11;
            if("Table1".equals(tableName)&&colunm==12)
                DATA_11 = Double.valueOf(node.getValue().toString());
            else
                DATA_11 = table1.getItemDouble(0, "DATA_11");
            if(DATA_11!=0){
                double DATA_18;
                if("Table2".equals(tableName)&&colunm==6)
                    DATA_18 = Double.valueOf(node.getValue().toString());
                else
                    DATA_18 = table2.getItemDouble(0, "DATA_18");
                double DATA_10;
                if("Table1".equals(tableName)&&colunm==9)
                    DATA_10 = Double.valueOf(node.getValue().toString());
                else
                    DATA_10 = table1.getItemDouble(0, "DATA_10");
                if(DATA_10!=0)
                    table4.setItem(0, "DATA_55",df.format((DATA_18+DATA_24)/DATA_10));
            }
        }
        //平均每一出院者医疗费 合计 DATA_51+DATA_52+DATA_53+DATA_54
        if ("Table4".equals(tableName) && (colunm == 6||colunm == 7||colunm == 8||colunm == 9)) {
            double DATA_51,DATA_52,DATA_53,DATA_54;
            if("Table4".equals(tableName)&&colunm==6)
                DATA_51 = Double.valueOf(node.getValue().toString());
            else
                DATA_51 = table4.getItemDouble(0, "DATA_51");
            if("Table4".equals(tableName)&&colunm==7)
                DATA_52 = Double.valueOf(node.getValue().toString());
            else
                DATA_52 = table4.getItemDouble(0, "DATA_52");
            if("Table4".equals(tableName)&&colunm==8)
                DATA_53 = Double.valueOf(node.getValue().toString());
            else
                DATA_53 = table4.getItemDouble(0, "DATA_53");
            if("Table4".equals(tableName)&&colunm==9)
                DATA_54 = Double.valueOf(node.getValue().toString());
            else
                DATA_54 = table4.getItemDouble(0, "DATA_54");
            table4.setItem(0, "DATA_50",
                           df.format(DATA_51+DATA_52+DATA_53+DATA_54));
        }
        //平均每床占用固定资产总金额中的合计=固定资产总金额/病床数(DATA_36/DATA_02)
        if(("Table1".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==7)){
            double DATA_02,DATA_36;
            if("Table1".equals(tableName)&&colunm==1)
                DATA_02 = Double.valueOf(node.getValue().toString());
            else
                DATA_02 = table1.getItemDouble(0, "DATA_02");
            if("Table3".equals(tableName)&&colunm==7)
                DATA_36 = Double.valueOf(node.getValue().toString());
            else
                DATA_36 = table3.getItemDouble(0, "DATA_36");
            if(DATA_02!=0)
                table3.setItem(0, "DATA_38",
                               df.format(DATA_36/DATA_02));
        }
        //平均每床占用固定资产总金额中的专业设备金额=固定资产总金额的专业设备金额/病床数(DATA_37/DATA_02)
        if(("Table1".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==8)){
            double DATA_02,DATA_37;
            if("Table1".equals(tableName)&&colunm==1)
                DATA_02 = Double.valueOf(node.getValue().toString());
            else
                DATA_02 = table1.getItemDouble(0, "DATA_02");
            if("Table3".equals(tableName)&&colunm==8)
                DATA_37 = Double.valueOf(node.getValue().toString());
            else
                DATA_37 = table3.getItemDouble(0, "DATA_37");
            if(DATA_02!=0)
                table3.setItem(0, "DATA_39",
                               df.format(DATA_37/DATA_02));
        }
        //期内病人欠费率=期内病人欠费总额/业务收入合计 (DATA_40/DATA_13)
        if(("Table2".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==11)){
            double DATA_40,DATA_13;
            if("Table2".equals(tableName)&&colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0, "DATA_13");
            if("Table3".equals(tableName)&&colunm==11)
                DATA_40 = Double.valueOf(node.getValue().toString());
            else
                DATA_40 = table3.getItemDouble(0, "DATA_40");
            if(DATA_13!=0)
                table3.setItem(0,"DATA_41",df.format(DATA_40/DATA_13*100));
        }
        //平均每天诊疗人次=总诊疗人次/月份数
        int days = STATool.getInstance().getDaysOfMonth(DATA_StaDate);
        if("Table1".equals(tableName)&&colunm==4)
            table3.setItem(0,"DATA_42",df.format(Double.valueOf(node.getValue().toString())/(double)days));
        //病床周转次数=出院人数/平均开放病床数 (DATA_06/DATA_08)
        if("Table1".equals(tableName)&&(colunm==5||colunm==7)){
            double DATA_08;
            if(colunm==7)
                DATA_08 = Double.valueOf(node.getValue().toString());
            else
                DATA_08 = table1.getItemDouble(0, "DATA_08");
            if(DATA_08!=0)
                table3.setItem(0,"DATA_44",df.format(DATA_06/DATA_08));
        }
        //平均药品加成率(%) = 【药品收入小计（DATA_23）-业务支出药品费（DATA_33）】/业务支出药品费（DATA_33）
        if(("Table2".equals(tableName)&&colunm==11)||("Table3".equals(tableName)&&colunm==4)){
            double DATA_23,DATA_33;
            if("Table2".equals(tableName)&&colunm==11)
                DATA_23 = Double.valueOf(node.getValue().toString());
            else
                DATA_23 = table2.getItemDouble(0, "DATA_23");
            if("Table3".equals(tableName)&&colunm==4)
                DATA_33 = Double.valueOf(node.getValue().toString());
            else
                DATA_33 = table3.getItemDouble(0, "DATA_33");
            if(DATA_33!=0)
                table4.setItem(0,"DATA_46",df.format((DATA_23-DATA_33)/DATA_33*100));
        }
        //平均每一出院者医疗费={住院收入小计+药品收入（住院）}/出院人数
        if("Table2".equals(tableName)&&(colunm==6||colunm==12)||("Table1".equals(tableName)&&colunm==5)){
            double DATA_18,DATA_24;
            if("Table2".equals(tableName)&&colunm==6)
                DATA_18 = Double.valueOf(node.getValue().toString());
            else
                DATA_18 = table2.getItemDouble(0, "DATA_18");
            if("Table2".equals(tableName)&&colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            if(DATA_06!=0)
                table4.setItem(0,"DATA_50",df.format((DATA_18+DATA_24)/DATA_06));
        }
        //每一职工 和 每一医师 部分计算
        if(("Table1".equals(tableName)&&(colunm==2||colunm==3||colunm==4||colunm==8))||("Table2".equals(tableName)&&colunm==1)){
            double DATA_05,DATA_09,DATA_13,DATA_03,DATA_04;
            if("Table1".equals(tableName)&&colunm==2)
                DATA_03 = Double.valueOf(node.getValue().toString());
            else
                DATA_03 = table1.getItemDouble(0,"DATA_03");
            if("Table1".equals(tableName)&&colunm==3)
                DATA_04 = Double.valueOf(node.getValue().toString());
            else
                DATA_04 = table1.getItemDouble(0,"DATA_04");
            if("Table1".equals(tableName)&&colunm==4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0,"DATA_05");
            if("Table1".equals(tableName)&&colunm==8)
                DATA_09 = Double.valueOf(node.getValue().toString());
            else
                DATA_09 = table1.getItemDouble(0,"DATA_09");
            if("Table2".equals(tableName)&&colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0,"DATA_13");
            if(DATA_03>0){
                table4.setItem(0,"DATA_56",df.format(DATA_05/DATA_03));
                table4.setItem(0,"DATA_57",df.format(DATA_09/DATA_03));
                table4.setItem(0,"DATA_58",df.format(DATA_13/DATA_03));
            }
            if(DATA_04>0){
                table4.setItem(0,"DATA_59",df.format(DATA_05/DATA_04));
                table4.setItem(0,"DATA_60",df.format(DATA_09/DATA_04));
                table4.setItem(0,"DATA_61",df.format(DATA_13/DATA_04));
            }
        }
    }
}
