package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;

/**
 * <p>Title: SYS_FEE批次界面</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0 (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 */
public class SYSFee_BanchControl
    extends TControl {

    TTextFormat date;
    TTable table;

    //全部执行
    TCheckBox exeAll;

    TRadioButton unpro;
    TRadioButton proed;
    TButton buttonAdd;
    TParm parmType = new TParm();

    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

    public SYSFee_BanchControl() {
    }

    public void onInit() {
        super.onInit();
        myInitControler();
        setPermissions();
        onQuery();
    }

    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {

        //用当前时间初始化时间控件
        date = (TTextFormat)this.getComponent("DTAE");
        date.setValue(TJDODBTool.getInstance().getDBTime());

        table = (TTable)this.getComponent("TABLE");
        exeAll = (TCheckBox)this.getComponent("EXEALL");
        //为执行
        unpro = (TRadioButton)this.getComponent("UNPRO");
        //已执行
        proed = (TRadioButton)this.getComponent("PROED");
        buttonAdd=(TButton)this.getComponent("Button");
        //给table注册CHECK_BOX_CLICKED点击监听事件
        this.callFunction("UI|TABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxChangeValue");

    }

    private void setPermissions(){
        ((TComboBox)getComponent("TYPE")).setParmMap("id:ID;name:DESC");
        if (getPopedem("FEE_PHA")) {
            parmType.addData("ID", "PHA_RULE");
            parmType.addData("DESC", "药品");
        }
        if (getPopedem("FEE")) {
            parmType.addData("ID", "SYS_FEE_RULE");
            parmType.addData("DESC", "项目");
        }
        if (getPopedem("EXM")) {
            parmType.addData("ID","EXM_RULE");
            parmType.addData("DESC","检验检查");
        }
        if (getPopedem("ORDPACK")) {
            parmType.addData("ID","ORDPACK_RULE");
            parmType.addData("DESC","医嘱套餐");
        }
        parmType.setCount(parmType.getCount("ID"));
        ((TComboBox)getComponent("TYPE")).setParmValue(parmType);
        if(parmType.getCount("ID") > 0)
            ((TComboBox)getComponent("TYPE")).setSelectedIndex(1);
    }


    public void onBanch() {
        if (onDoIt()) {
            this.messageBox("执行成功！");
            onQuery();
            return;
        }
        this.messageBox("执行失败！");
    }



    /**
     * 批次（调价计划）
     */
    public boolean onDoIt() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        //当前时间（后面作为比较使用）
        long nowTime = now.getTime();
        //总的parm [[调价计划所有数据] opt_user opt_date opt_term]
        TParm parmToBanch = new TParm();
        int tblRows = table.getRowCount();
        if (tblRows <= 0) {
            this.messageBox("没有调价数据！");
            return false;
        }
        TParm order = new TParm();
        String oldDate = "";
        for (int i = 0; i < tblRows; i++) {

            //如果没有花勾就跳过
            if (!TypeTool.getBoolean(table.getValueAt(i, 0))) {
                continue;
            }
            //得到该行当前时间
            String activeDate = (String) table.getValueAt(i, 1);
            Long exeTime = ( (Timestamp) StringTool.getTimestamp(activeDate,
                "yyyy/MM/dd HH:mm:ss")).getTime();
            if (!"".equals(oldDate) && !oldDate.equals(activeDate)) {
                parmToBanch.setData("OPT_USER", Operator.getID());
                parmToBanch.setData("OPT_TERM", Operator.getIP());
                parmToBanch.setData("OPT_DATE", now);
                //调用生成批次接口
                //------------start------------
                //为生成
                if (unpro.isSelected()) {
                    //判断调价启用时间是否已过
                    if (exeTime < nowTime) {
                        //当执行时间早于当前那么调用另一个Action
                        TParm result = TIOM_AppServer.executeAction(
                            "action.sys.SYSFeeReadjustAction",
                            "onInsertSYSFeeReadjust", parmToBanch);//立即执行
                        if (result.getErrCode() < 0) {
                            this.messageBox_(result);
                            return false;
                        }
                    }else{
                        //调用插入
                        //调用action执行事务
                        TParm result = TIOM_AppServer.executeAction(
                            "action.sys.SYSFeeReadjustAction",
                            "onSYSFeeReadjustPatch", parmToBanch); //走批次—预执行
                        if (result.getErrCode() < 0) {
                            this.messageBox_(result);
                            return false;
                        }
                    }
                }
                else {
                    //调用更新
                    //调用action执行事务
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onUpdateSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
                //刷新传给接口的parm
                parmToBanch = new TParm();
                order = new TParm();
                oldDate = "";
            }

            //压数据
            if (activeDate.equals( (String) table.getValueAt(i, 1))) {
                //整理传给批次接口的参数
                order.addRowData(table.getParmValue(), i);
                if("Y".equals(table.getParmValue().getValue("ORDERSET_FLG",i))){
                    order.setData("OWN_PRICE",order.getCount("OWN_PRICE")-1, 0);
                    order.setData("OWN_PRICE2",order.getCount("OWN_PRICE2")-1, 0);
                    order.setData("OWN_PRICE3",order.getCount("OWN_PRICE3")-1 ,0);
                }
                parmToBanch.setData("ORDER", order.getData());
            }

            //----------------end----------------------------------------------
            //备份老时间--比较用
            oldDate = activeDate;
        }
        //防止最后一条问题
        if (!"".equals(oldDate)) {
            parmToBanch.setData("OPT_USER", Operator.getID());
            parmToBanch.setData("OPT_TERM", Operator.getIP());
            parmToBanch.setData("OPT_DATE", now);
            //调用生成批次接口
            //------------------------
            //得到该行当前时间
            String activeDate = (String) table.getValueAt(tblRows-1, 1);
            Long exeTime = ( (Timestamp) StringTool.getTimestamp(activeDate,
                "yyyy/MM/dd HH:mm:ss")).getTime();
            //判断调价启用时间是否已过
            if (exeTime < nowTime) {
                //当执行时间早于当前那么调用另一个Action
                TParm result = TIOM_AppServer.executeAction(
                    "action.sys.SYSFeeReadjustAction",
                    "onInsertSYSFeeReadjust", parmToBanch);
                if (result.getErrCode() < 0) {
                    this.messageBox_(result);
                    return false;
                }
            }else{
                //为生成
                if (unpro.isSelected()) {
                    //调用插入
                    //调用action执行事务
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
                else {
                    //调用更新
                    //调用action执行事务
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onUpdateSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
            }
            //刷新传给接口的parm
            parmToBanch = new TParm();
            oldDate = "";
        }


        //前台刷新
        TIOM_Database.logTableAction("SYS_FEE");

        return true;
    }

    /**
     * 全部执行
     */
    public void onExe() {
        boolean exeFlg;
        //获得点击时的值
        exeFlg = TypeTool.getBoolean(exeAll.getValue());

        int rows = table.getRowCount();
        for (int i = 0; i < rows; i++) {
            table.setValueAt(exeFlg, i, 0);
        }

    }

    /**
     * 勾选启用时间相同的所有药品
     * @param obj Object
     */
    public void onTableCheckBoxChangeValue(Object obj) {

        //只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        table.acceptText();
        //获得选中的列/行
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        //获得table上的行数
        int rowcount = table.getRowCount();
        //如果选中的是第0列就激发执行动作--执行
        if (col == 0) {
            boolean exeFlg;
            //获得点击时的值
            exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
            //得到选中行的执行时间
            String exeDateTime = (String) table.getValueAt(row, 1);
            for (int i = 0; i < rowcount; i++) {
                //和选中的执行时间相同就花勾
                if (exeDateTime.equals( (String) table.getValueAt(i, 1)))
                    table.setValueAt(exeFlg, i, 0);
            }
        }

    }

    /**
     * 查询
     */
    public void onQuery() {
        table.removeRowAll();
        if(parmType.getCount("ID") < 4 &&
           (getValue("TYPE") == null || getValueString("TYPE").length() == 0)){
            return;
        }
//        String now = StringTool.getString( (Timestamp) date.getValue(),
//                                          "yyyyMMdd");
        //===========pangben modify 20110512 start
        String region = "";
        if (null != Operator.getRegion() &&
            Operator.getRegion().length() > 0) {
            region = " AND (REGION_CODE='" + Operator.getRegion() + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        }
        //===========pangben modify 201105121 stop

        String selSql =
            " SELECT START_DATE,ORDER_DESC,OWN_PRICE,NHI_PRICE,GOV_PRICE,RPP_CODE,ORDER_CODE,END_DATE,OWN_PRICE2,OWN_PRICE3,ORDERSET_FLG " +
            " FROM SYS_FEE_HISTORY " +
            " WHERE ACTIVE_FLG='Y' " +//只显示当前启用的
//            " AND TO_DATE (START_DATE, 'YYYYMMDDHH24MISS') > TO_DATE (" + now +//modify by wangjc 20171225 去掉时间限制，避免有未执行的调价计划
//            " , 'YYYYMMDD')" +
            region+//===========pangben modify 201105121
            getAndCondition() +
            " ORDER BY START_DATE ";
       // System.out.println("sql"+selSql);
        TParm result = new TParm(TJDODBTool.getInstance().select(selSql));
        if (result.getCount() <= 0) {
            this.messageBox("目前没有调价项目");
            return;
        }
        filterSysFee(result);
        //整理合适的Parm放到table上
        table.setParmValue(arrangementData(result));
    }

    /**
     * 得到查询条件
     * @return String
     */
    public String getAndCondition() {
        if (unpro.isSelected())
            return "AND RPP_CODE IS NULL ";
        return "AND RPP_CODE IS NOT NULL ";
    }

    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    public TParm arrangementData(TParm parm) {
        TParm data = parm;
        for (int i = 0; i < parm.getCount(); i++) {
            data.addData("EXE", unpro.isSelected() ? false : true);
            //检查时间是否为年月日时分秒
            String startDate = checkDateTime( (String) parm.getData(
                "START_DATE", i));
            //如果不足年月日时分秒
            data.setData("START_DATE", i,
                         startDate.substring(0, 4) + "/" +
                         startDate.substring(4, 6) + "/" +
                         startDate.substring(6, 8) + " " +
                         startDate.substring(8, 10) + ":" +
                         startDate.substring(10, 12) + ":" +
                         startDate.substring(12));
            if(data.getValue("ORDERSET_FLG",i).equals("N"))
                continue;
            String sqlForDtl = " SELECT ORDER_CODE,DOSAGE_QTY "+
                               " FROM SYS_ORDERSETDETAIL "+
                               " WHERE ORDERSET_CODE = '" + data.getValue("ORDER_CODE",i) + "'";
            TParm parmDtl = new TParm(TJDODBTool.getInstance().select(sqlForDtl));
            int count = parmDtl.getCount();
            double totFee = 0.0;
            double totFee2 = 0.0;
            double totFee3 = 0.0;
            for (int j = 0; j < count; j++) {
                String code = parmDtl.getValue("ORDER_CODE", j);
                double qty = TypeTool.getDouble(parmDtl.getValue("DOSAGE_QTY", j));
                totFee += getPrice(code) * qty;
                totFee2 += getPrice2(code) * qty;
                totFee3 += getPrice3(code) * qty;
            }
            data.setData("OWN_PRICE", i, totFee);
            data.setData("OWN_PRICE2", i, totFee2);
            data.setData("OWN_PRICE3", i, totFee3);
        }
        return data;
    }

    //根据order拿到单价
    public double getPrice(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    //根据order拿到单价
    public double getPrice2(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE2");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    //根据order拿到单价
    public double getPrice3(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE3");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    /**
     * 检查是否是满14位（yyyyMMddHHmmss）
     * @param dateTime String
     * @return String
     */
    private String checkDateTime(String dateTime) {

        if (dateTime.length() >= 14) {
            return dateTime.substring(0, 14);
        }
        //小于14位
        int addZero = 14 - dateTime.length();
        for (int i = 0; i < addZero; i++)
            dateTime += "0";
        return dateTime;
    }

    /**
     * 清空
     * @param args String[]
     */
    public void onClear() {
        table.removeRowAll();

    }

    public void onProduce(Object ob) {
        onClear();
        if("Y".equals(ob+"")){
            buttonAdd.setEnabled(false);
        }else{
            buttonAdd.setEnabled(true);
        }

    }
    public TParm getType(){
        String SQL = " SELECT CATEGORY_CODE "+
                     " FROM SYS_CATEGORY "+
                     " WHERE RULE_TYPE='"+getValueString("TYPE")+"'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        return parm;
    }


    public void filterSysFee(TParm parm){
        TParm typeParm = getType();
        if(typeParm.getCount() <= 0)
            return;
        for(int i = parm.getCount() - 1;i >= 0;i--){
            boolean isRemove = false;
            for(int j= 0;j<typeParm.getCount();j++){
               if(parm.getValue("ORDER_CODE",i).startsWith(typeParm.getValue("CATEGORY_CODE",j))){
                   isRemove = false;
                   break;
               }
               else
                   isRemove = true;
            }
            if(isRemove)
                parm.removeRow(i);
        }
    }


    public static void main(String[] args) {
        JavaHisDebug.initClient();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_BANCH.x");

    }


}
