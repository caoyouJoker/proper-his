package com.javahis.ui.customquery;

import java.util.Vector;

import jdo.sys.BuildSqlTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: 查询显示窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 20090818
 * @version 1.0
 */
public class QueryViewControl
    extends TControl {
    TComboBox tempCode;
    TTable tempTable, selectTable, table;
    String author;
    TButton set;
    /**
     * 初始化
     */
    public void onInit() {
//    	Object o=this.getParameter();
//    	System.out.println(o.getClass().getName());
//    	Vector vct=(Vector)o;
//    	System.out.println("vct============"+vct);
//    	if(vct!=null&&vct.size()>0){
//    		author=vct.get(1)+"";
//    	}
//
        initComponent();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        table = (TTable) getComponent("TABLE");
        selectTable = (TTable) getComponent("SELECT_TABLE");
        tempTable = (TTable) getComponent("TEMP_TABLE");

        tempCode = (TComboBox) getComponent("TEMP_COMBO");
        set = (TButton) getComponent("SETBUTTON");
//        if (author == null || author.trim().length() < 1 ||
//            "1".equalsIgnoreCase(author)) {
//            set.setVisible(false);
//        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        TParm comboData = BuildSqlTool.getInstance().getTempCodeComboData(
            Operator.getID());
        if (comboData == null || comboData.getErrCode() != 0) {
            this.messageBox_("没有数据");
            return;
        }
        tempCode.setParmValue(comboData);

        TParm tableParm = BuildSqlTool.getInstance().getTempCodeTableData(
            Operator.getID());
        if (tableParm == null || tableParm.getErrCode() != 0) {
            this.messageBox_("没有数据");
            return;
        }
        //模板名称,200;处理表名,100;建立者,100;操作人员,100;操作时间,200;操作端末,100
        //TEMP_DESC;TABLE_ID;OWNER;USER_NAME;BUILDDATE;OPT_TERM
        tempTable.setParmValue(tableParm);
    }

    /**
     * 模板TABLE行点击事件
     */
    public void onClickRow() {
        int row = tempTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("没有数据");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        String tempCodeString = tempParm.getValue("TEMP_CODE", row);
        tempCode.setValue(tempCodeString);
        onTempChange();
    }

    /**
     * COMBO点选事件，初始化3个TABLE
     */
    public void onTempChange() {
        String tempCodeString = tempCode.getSelectedID();
        if (tempCodeString == null || tempCodeString.trim().length() < 1) {
            onClear();
            return;
        }
        TParm tempTableParm = BuildSqlTool.getInstance().
            getTempCodeTableDataByTempCode(Operator.getID(), tempCodeString);
        if (tempTableParm == null || tempTableParm.getCount() < 1) {
            this.messageBox_("没有数据");
            return;
        }
        tempTable.setParmValue(tempTableParm);

        String tableId = tempTableParm.getValue("TABLE_ID", 0);
        if (tableId == null || tableId.trim().length() < 1) {
            this.messageBox_("没有数据");
//            this.messageBox_("2");
            return;
        }
        TParm whereParm = BuildSqlTool.getInstance().getWhereTable(
            tempCodeString, tableId);
        if (whereParm == null || whereParm.getErrCode() < 0) {
            this.messageBox_("没有数据");
//            this.messageBox_("3");
            return;
        }
        for (int i = 0; i < whereParm.getCount("VALUE"); i++) {
            whereParm.setData("VALUE", i, "");
        }
        selectTable.setParmValue(whereParm);
    }

    /**
     * 清空事件
     */
    public void onClear() {
        initData();
        selectTable.removeRowAll();
        table.removeRowAll();
    }

    public void onQuery() {
    	//selectTable无显示
        selectTable.acceptText();
        String tempCodeString = tempCode.getSelectedID();
        if (tempCodeString == null || tempCodeString.trim().length() < 1) {
//            this.messageBox_("1");
            this.messageBox_("没有数据");
            return;
        }
        TParm column = BuildSqlTool.getInstance().getColumnInfo(tempCodeString);
        if (column == null || column.getErrCode() < 0) {
//            this.messageBox_("2");
            this.messageBox_("没有数据");
            return;
        }
        String header = BuildSqlTool.getInstance().getColumnHeader(column);
        String parmMap = BuildSqlTool.getInstance().getColumnParmMap(column);
        System.out.println("parmMap===============" + parmMap);
        System.out.println("header===============" + header);
        table.setHeader(header);
        table.setParmMap(parmMap);
        //????
        TParm whereValue = selectTable.getParmValue();
        int row = 0;
        int columnIndex = tempTable.getColumnIndex("TABLE_ID");
        String tableName = tempTable.getValueAt(row, columnIndex) + "";
        System.out.println("WHERE______VALUE=" + whereValue);
        String sql = BuildSqlTool.getInstance().buildSql(tempCodeString,
            whereValue, tableName);
        if (sql == null || sql.trim().length() < 1) {
//            this.messageBox_("4");
            this.messageBox_("没有数据");
            return;
        }
        //!!!对
        System.out.println("sql===================" + sql);
        TParm data = BuildSqlTool.getInstance().queryBySql(sql);
        if (data == null || data.getErrCode() != 0) {
            this.messageBox_("没有数据");
            return;
        }
        table.setParmValue(data);
        String tempDesc = tempCode.getSelectedName();
        if ("各科抗菌素用量统计表".equalsIgnoreCase(tempDesc)) {
            header = "科室,100;抗菌素金额,100;药品总金额,100;抗菌素/总金额,100";
            parmMap = "DEPTDESC;ANTISUM;TOT_AMT;RATE";
            int count = data.getCount();
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            for (int i = 0; i < count; i++) {
                String deptDesc = data.getValue("DEPTDESC", i);

                TParm antiSum = BuildSqlTool.getInstance().getAntiByDept(
                    deptDesc, startDate, endDate);
                System.out.println("antiSum==" + antiSum);
                long anti = antiSum.getLong("ANTISUM", 0);
                long totAmt = data.getLong("TOT_AMT", i);
                long rate = anti / totAmt;
                double rateD = StringTool.round(Double.longBitsToDouble(rate),
                                                2);
                data.addData("ANTISUM", anti);
                data.addData("RATE", rateD);
            }
            data.setCount(data.getCount("TOT_AMT"));
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
        else if ("各类药品销售额/总金额比例表".equalsIgnoreCase(tempDesc)) {
            header = "药品分类,100;类销售额,100;总销售额,100;类销售额/总销售额,100";
            parmMap = "CATDESC;TOTAMT;ALLAMT;RATE";
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            TParm allAmt = BuildSqlTool.getInstance().getTotAmtByDate(startDate,
                endDate);
            if (allAmt.getErrCode() != 0) {
                this.messageBox_("没有数据");
                return;
            }
            long all = allAmt.getLong("ALLAMT", 0);
            double allD = Double.longBitsToDouble(all);
            int count = data.getCount();
            System.out.println("parm===========" + data);
            for (int i = 0; i < count; i++) {
                long totAmt = data.getLong("TOTAMT", i);
                double totAmtD = Double.longBitsToDouble(totAmt);
                System.out.println("totAmt==============" + totAmt);
                double rate = totAmtD / allD;
                System.out.println("rate=" + rate);
                rate = StringTool.round(rate, 2);
                data.addData("ALLAMT", all);
                data.addData("RATE", rate);
            }
            data.setCount(data.getCount("TOT_AMT"));
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
        else if ("各种药品销售额/同类销售额比例表".equalsIgnoreCase(tempDesc)) {
            header = "药品名称,100;销售额,100;类销售额,100;销售额/类销售额,100";
            parmMap = "ORDERDESC;TOT_AMT;ALLAMT;RATE";
            String startDate = selectTable.getValueAt(0, 1) + "";
            String endDate = selectTable.getValueAt(1, 1) + "";
            TParm classifyTot = BuildSqlTool.getInstance().getPhaClassifyTotAmt(
                startDate, endDate);
            Vector catCodeVct = classifyTot.getVectorValue("CATCODE");
            System.out.println("classifyTot+" + classifyTot);
            System.out.println("catCodeVct=" + catCodeVct);
            int count = data.getCount();
            for (int i = 0; i < count; i++) {
                String orderCode = data.getValue("ORDERCODE", i);
                orderCode = orderCode.substring(0, 4);
                int rowOfOrderCode = catCodeVct.indexOf(orderCode);
                double allamt = 0.0;
                double totAmt = data.getDouble("TOT_AMT", i);
                double rate = 0.0;
                if (rowOfOrderCode > -1) {
                    allamt = classifyTot.getDouble("TOTAMT", rowOfOrderCode);
                    rate = totAmt / allamt;
                    rate = StringTool.round(rate, 2);
                }
                data.addData("ALLAMT", allamt);
                data.addData("RATE", rate);
            }
            table.setHeader(header);
            table.setParmMap(parmMap);
            table.setParmValue(data);
        }
    }

    /**
     * 导出到EXCEL
     */
    public void onExportExcel() {
        int count = table.getRowCount();
        if (count < 1) {

            this.messageBox_("没有数据");
            return;
        }
        int column = tempTable.getColumnIndex("TEMP_DESC");
        String tableName = tempTable.getValueAt(0, column) + "";
        ExportExcelUtil.getInstance().exportExcel(table,
                                                  "C:\\CustomeQuery\\" + tableName);
    }

    /**
     * 删除模板
     */
    public void onDelete() {
        if (tempTable == null) {
            this.messageBox_("没有数据");
            return;
        }
        int row = tempTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("没有数据");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        String tempCode = tempParm.getValue("TEMP_CODE", row);
        TParm parm = new TParm();
        parm.setData("TEMP_CODE", tempCode);
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.BuildSqlAction", "onDelete", parm);
        if (result.getErrCode() != 0) {
            System.out.println(result.getErrText());
            this.messageBox_("保存失败");
            return;
        }
        this.messageBox_("保存成功");
        initData();
    }

    /**
     * 新建查询模板
     */
    public void onInsert() {
        TParm inParm = new TParm();
        inParm.setData("TEMP_CODE", "");
        this.openDialog("%ROOT%\\config\\customquery\\BuildSql.x", inParm, false);
        initData();
    }

    /**
     * 设置模板
     */
    public void onUpdate() {
        if (tempTable == null) {
            this.messageBox_("没有数据");
            return;
        }
        TParm tempParm = tempTable.getParmValue();
        if (tempParm == null || tempParm.getCount() < 1) {
            this.messageBox_("没有数据");
            return;
        }

        String tempCodeString = tempCode.getSelectedID();
        int column = tempTable.getColumnIndex("TABLE_ID");
        String tableId = tempTable.getValueAt(0, column) + "";
        TParm inParm = new TParm();
        System.out.println("queryViewControl.tempCode===" + tempCodeString);
        inParm.setData("TEMP_CODE", tempCodeString);
        inParm.setData("TABLE_ID", tableId);
        this.openDialog("%ROOT%\\config\\customquery\\BuildSql.x", inParm, false);
        initData();
    }
    //表格双击事件
    public void onOK(){
        int rowIndex = table.getSelectedRow();
        TParm data = table.getParmValue();
        TParm re = data.getRow(rowIndex);
        this.setReturnValue(re);
        this.closeWindow();
    }

    public static void main(String args[]) {

        com.javahis.util.JavaHisDebug.TBuilder();
        Operator.setData("tiis", "HIS", "127.0.0.1", "20803", "001");

//        Object parm = com.javahis.util.JavaHisDebug.runDialog("customquery\\QueryView.x",new Object[]{"UDD_SHEET"});
//        System.out.println("parm=" + parm);
    }

}
