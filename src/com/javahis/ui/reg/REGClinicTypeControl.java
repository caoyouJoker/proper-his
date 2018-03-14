package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import java.util.Date;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import java.util.ArrayList;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.bil.BIL;

/**
 *
 * <p>Title: 诊别对诊别费用控制类</p>
 *
 * <p>Description: 诊别对诊别费用控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.07.16
 * @version 1.0
 */
public class REGClinicTypeControl
    extends TControl {
    int selectRow = -1;
    boolean flg;
    TParm data;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        // 添加诊别table单击事件
        callFunction("UI|TYPETABLE|addEventListener",
                     "TYPETABLE->" + TTableEvent.CLICKED, this,
                     "onTYPETABLEClicked");
        // 添加诊别费用table单击事件
        callFunction("UI|TYPEFEETABLE|addEventListener",
                     "TYPEFEETABLE->" + TTableEvent.CLICKED, this,
                     "onTYPEFEETABLEClicked");
        //诊别费用table专用的监听
        getTTable("TYPEFEETABLE").addEventListener(TTableEvent.
            CREATE_EDIT_COMPONENT, this,
            "onCreateEditComponent");
        OrderList orderDesc = new OrderList();
        TTable table = (TTable)this.getComponent("TYPEFEETABLE");
        table.addItem("ORDER_LIST", orderDesc);
//        //添加诊别费用table值改变事件
//        this.addEventListener("TYPEFEETABLE->" + TTableEvent.CHANGE_VALUE,
//              "onTYPEFEETABLEChargeValue");
        initPage();
        onClear();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
        typeFeeTable.setFilter(" CLINICTYPE_CODE = '' ");
        typeFeeTable.filter();

    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * sysFee弹出界面
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComponent(Component com, int row, int column) {
        //设置当前选中的行

        selectRow = row;
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
        //如果当前选中的行不是最后一行空行则什么都不做
        if (typeFeeTable.getRowCount() != selectRow + 1)
            return;
        //求出当前列号
        column = typeFeeTable.getColumnModel().getColumnIndex(column);
        //得到当前列名
        String columnName = typeFeeTable.getParmMap(column);
        //弹出sysfee对话框的列
        if (!columnName.equals("ORDER_CODE"))
            return;
//       if (column != 1)
//           return;
        //
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("SYSFEE",
                                        getConfigParm().newConfig(
                                            "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        //给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newOrder");
    }

    /**
     * sysFee模糊查询
     */
    public class OrderList
        extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ORDER_CODE");
            Vector d = (Vector) parm.getData("ORDER_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }


    /**
     * 新增医嘱
     * @param tag String
     * @param obj Object
     */
    public void newOrder(String tag, Object obj) {
        TParm parm = (TParm) obj;
//        System.out.println("parm"+parm);
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
//        typeFeeTable.acceptText();
        String orderCode = parm.getValue("ORDER_CODE");
        String orderDesc=parm.getValue("ORDER_DESC");
        int selRow = typeFeeTable.getSelectedRow();
        typeFeeTable.setItem(selRow, "ORDER_CODE", orderCode);
        //===========pangben modify 20110608 start 添加列
        typeFeeTable.setItem(selRow, "ORDER_DESC", orderDesc);
        //===========pangben modify 20110608 stop
        typeFeeTable.setValueAt(parm.getDouble("OWN_PRICE")<0?0.00:parm.getDouble("OWN_PRICE"), selRow, 5);
        //设置行可保存
        typeFeeTable.getDataStore().setActive(selRow, true);
    }

    /**
     * 查询
     */
    public void onQuery() {
        String clinicCode = TypeTool.getString(getValue("CLINICTYPE_CODE"));
        String clinicDesc = TypeTool.getString(getValue("CLINICTYPE_DESC"));
        StringBuffer sb = new StringBuffer();
        if (clinicCode != null && clinicCode.length() > 0)
            sb.append(" CLINICTYPE_CODE like '" + clinicCode + "%' ");
        if (clinicDesc != null && clinicDesc.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CLINIC_DESC like '" + clinicDesc + "%' ");
        }
        String sql1 =
            "  SELECT CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,SEQ," +
            "         DESCRIPTION,ADM_TYPE,PROF_FLG,OPT_USER,OPT_DATE,OPT_TERM " +
            "    FROM REG_CLINICTYPE ";
        String sql2 = " ORDER BY SEQ,CLINICTYPE_CODE ";
        if (sb.length() > 0)
            sql1 += " WHERE " + sb.toString() + sql2;
        else
            sql1 = sql1 + sql2;
        TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql1));
        if (sqlParm.getErrCode() < 0) {
            messageBox(sqlParm.getErrText());
            return;
        }
        //号别table赋值
        this.callFunction("UI|TYPETABLE|setParmValue", sqlParm);
        //号别费用table赋值
        TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");
        TDataStore typeFeeDataStore = typeFeeTable.getDataStore();
        typeFeeDataStore.retrieve();

    }

    /**
     * 保存
     * @return boolean
     */
    public boolean onSave() {
        //诊区table保存
        if (!this.emptyTextCheck("ADM_TYPE") &&
            !this.emptyTextCheck("CLINICTYPE_CODE"))
            return false;
        String admType = getValue("ADM_TYPE").toString(); //门急别
        String typeCode = getValue("CLINICTYPE_CODE").toString(); //号别代码
        String typeDesc = getValue("CLINICTYPE_DESC").toString(); //号别说明
        String py1 = getValue("PY1").toString(); //拼音码
        String py2 = getValue("PY2").toString(); //注记码
        int seq = TypeTool.getInt(getValue("SEQ")); //顺序号
        String description = getValue("DESCRIPTION").toString(); //备注
        String profFlg = getValue("PROF_FLG").toString(); //专家诊注记
        String optUser = Operator.getID(); //操作人员
        Timestamp optDate = SystemTool.getInstance().getDate(); //操作日期
        String optTerm = Operator.getIP(); //操作终端
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", typeCode);
        parm.setData("CLINICTYPE_DESC", typeDesc);
        parm.setData("PY1", py1);
        parm.setData("PY2", py2);
        parm.setData("SEQ", seq);
        parm.setData("DESCRIPTION", description);
        parm.setData("PROF_FLG", profFlg);
        parm.setData("OPT_USER", optUser);
        parm.setData("OPT_DATE", optDate);
        parm.setData("OPT_TERM", optTerm);
        String selSql =
            " SELECT CLINICTYPE_CODE FROM REG_CLINICTYPE WHERE CLINICTYPE_CODE = '" +
            typeCode + "'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(selSql));
        if (selParm.getCount("CLINICTYPE_CODE") > 0) {
            String updateSql =
                " UPDATE REG_CLINICTYPE " +
                "    SET CLINICTYPE_DESC='" + typeDesc + "',PY1='" + py1 +
                "',PY2='" + py2 + "',SEQ='" + seq + "',DESCRIPTION='" +
                description + "'," +
                "        PROF_FLG='" + profFlg + "',OPT_USER='" + optUser +
                "',OPT_DATE=SYSDATE,OPT_TERM='" + optTerm + "' " +
                "  WHERE CLINICTYPE_CODE='" + typeCode + "'" +
                "    AND ADM_TYPE ='" + admType + "'";
            TParm updateParm = new TParm(TJDODBTool.getInstance().update(
                updateSql));
            //刷新，设置末行某列的值
            int row = (Integer) callFunction("UI|TYPETABLE|getSelectedRow");
            if (row < 0)
                System.out.println("号别无更新");
            else {
                TParm data = (TParm) callFunction("UI|TYPETABLE|getParmValue");
                data.setRowData(row, parm);
                callFunction("UI|TYPETABLE|setRowParmValue", row, data);
            }

        }
        else {
            String sql =
                " INSERT INTO REG_CLINICTYPE " +
                "             (ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,SEQ," +
                "             DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,OPT_TERM) " +
                "      VALUES ('" + admType + "','" + typeCode + "','" +
                typeDesc + "','" + py1 +
                "','" + py2 + "','" + seq + "'," +
                "             '" + description + "','" + profFlg + "','" +
                optUser + "',SYSDATE,'" + optTerm + "') ";
            TParm insertParm = new TParm(TJDODBTool.getInstance().update(sql));
            if (insertParm.getErrCode() < 0) {
                messageBox(insertParm.getErrText());
                return false;
            }
            //table上加入新增的数据显示
            callFunction("UI|TYPETABLE|addRow", parm,
                         "ADM_TYPE,CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;" +
                         "DESCRIPTION;PROF_FLG;OPT_USER;OPT_DATE;OPT_TERM");
        }
        //诊室table保存
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");
        // 接收文本
        typeFeeTable.acceptText();
        TDataStore dataStore = typeFeeTable.getDataStore();
        // 获得全部改动的行号
        int rows[] = dataStore.getModifiedRows();
        // 给固定数据配数据
        for (int i = 0; i < rows.length; i++) {
            dataStore.setItem(rows[i], "OPT_USER", optUser);
            dataStore.setItem(rows[i], "OPT_DATE", date);
            dataStore.setItem(rows[i], "OPT_TERM", optTerm);
        }
        if (!typeFeeTable.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        typeFeeTable.setDSValue();
        this.onClear();
        return true;
    }

    /**
     * 新增方法
     */
    public void onNew() {
        TTable table = (TTable) getComponent("TYPEFEETABLE");
        String maxCode = "";
        // 新添加数据的顺序编号
        int maxSeq = getMaxSeq(table.getDataStore(), "SEQ");
        if (getValue("CLINICTYPE_CODE").toString().length() > 0) {
            // 新添加的行号
            int row = table.addRow();
            // 当前选中的行
            table.setSelectedRow(row);
            // 默认号门急别代码
            table.setItem(row, "ADM_TYPE", getValue("ADM_TYPE"));
            // 默认号别费用代码
            table.setItem(row, "CLINICTYPE_CODE", getValue("CLINICTYPE_CODE")); //RECEIPT_TYPE
            // 默认顺序编号
            table.setItem(row, "SEQ", maxSeq);
            //设置行不可保存
            table.getDataStore().setActive(row, false);
        }
        else {
            this.messageBox("无归属号别");
            return;
        }
    }

    /**
     * 删除
     */
    public void onDeleteC() {
        TTable typeTable = (TTable)this.getComponent("TYPETABLE");
        TParm typeParm = typeTable.getParmValue();
        int typeSelRow = typeTable.getSelectedRow();
        String admType = typeParm.getValue("ADM_TYPE", typeSelRow);
        String typeCode = typeParm.getValue("CLINICTYPE_CODE", typeSelRow);
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
        int typeFeeSelRow = typeFeeTable.getSelectedRow();
        String orderCode = typeFeeTable.getItemString(typeFeeSelRow,
            "ORDER_CODE");
        String delTypeSql = "";
        String delTypeAndFeeSql = "";
        String delTypeFeeSql = "";
        ArrayList list = new ArrayList();
        if (false) {
//            if (this.messageBox("询问", "是否删除", 0) == 0) {
            if (flg) {
                delTypeSql =
                    " DELETE REG_CLINICTYPE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType + "'";
                delTypeAndFeeSql =
                    " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType + "' ";
                list.add(delTypeSql);
                list.add(delTypeAndFeeSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delTypeParm = new TParm(TJDODBTool.getInstance().update(
                    allSql));
                if (delTypeParm.getErrCode() < 0) {
                    err(delTypeParm.getErrName() + " " + delTypeParm.getErrText());
                    return;
                }
                typeTable.retrieve();
            }
            else {
                delTypeFeeSql =
                    " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType +
                    "' AND ORDER_CODE = '" + orderCode + "'" +
                    orderCode + "' ";
                TParm delTypeFeeParm = new TParm(TJDODBTool.getInstance().
                                                 update(
                    delTypeFeeSql));
                if (delTypeFeeParm.getErrCode() < 0) {
                    err(delTypeFeeParm.getErrName() + " " +
                        delTypeFeeParm.getErrText());
                    return;
                }
                typeFeeTable.retrieve();
                typeFeeTable.setFilter("ADM_TYPE = '" +
                                       getValue("ADM_TYPE").toString() +
                                       "' AND CLINICTYPE_CODE = '" +
                                       getValue("CLINICTYPE_CODE").toString() +
                                       "'");
            }
        }
        else {
            return;
        }
        this.onClear();
    }

    /**
     * 删除
     */
    public void onDelete() {
        TTable typeTable = (TTable)this.getComponent("TYPETABLE");
//        TParm typeParm = typeTable.getParmValue();
//        int typeSelRow = typeTable.getSelectedRow();
        String admType = getValue("ADM_TYPE").toString();
        String typeCode = getValue("CLINICTYPE_CODE").toString();
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
        int typeFeeSelRow = typeFeeTable.getSelectedRow();
        String orderCode = typeFeeTable.getItemString(typeFeeSelRow,
            "ORDER_CODE");
        String delTypeSql = "";
        String delTypeAndFeeSql = "";
        String delTypeFeeSql = "";
        ArrayList list = new ArrayList();
        if (this.messageBox("询问", "是否删除", 0) == 0) {
            if (flg) {
                delTypeSql =
                    " DELETE REG_CLINICTYPE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType + "'";
                delTypeAndFeeSql =
                    " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType + "' ";
                list.add(delTypeSql);
                list.add(delTypeAndFeeSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delTypeParm = new TParm(TJDODBTool.getInstance().update(
                    allSql));
                if (delTypeParm.getErrCode() < 0) {
                    err(delTypeParm.getErrName() + " " + delTypeParm.getErrText());
                    return;
                }
                typeTable.retrieve();
            }
            else {
                delTypeFeeSql =
                    " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '" +
                    typeCode + "' AND ADM_TYPE = '" + admType +
                    "' AND ORDER_CODE = '" + orderCode + "' ";
//                System.out.println("delTypeFeeSql" + delTypeFeeSql);
                TParm delTypeFeeParm = new TParm(TJDODBTool.getInstance().
                                                 update(
                    delTypeFeeSql));
                if (delTypeFeeParm.getErrCode() < 0) {
                    err(delTypeFeeParm.getErrName() + " " +
                        delTypeFeeParm.getErrText());
                    return;
                }
                typeFeeTable.retrieve();
                typeFeeTable.setFilter("ADM_TYPE = '" +
                                       getValue("ADM_TYPE").toString() +
                                       "' AND CLINICTYPE_CODE = '" +
                                       getValue("CLINICTYPE_CODE").toString() +
                                       "'");
            }
        }
        else {
            return;
        }
        this.onClear();
    }


    /**
     * 清空
     */
    public void onClear() {
        data = new TParm();
        clearValue(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG");
        this.callFunction("UI|TYPETABLE|removeRowAll");
        this.callFunction("UI|TYPEFEETABLE|removeRowAll");
        TTable typeTable = (TTable)this.getComponent("TYPETABLE");
        TDataStore typeDataStore = typeTable.getDataStore();
        typeDataStore.resetModify();
        TTable typeFeeTable = (TTable)this.getComponent("TYPEFEETABLE");
        TDataStore typeFeeDataStore = typeFeeTable.getDataStore();
        typeFeeDataStore.resetModify();
        selectRow = -1;
        onQuery();
        data = typeTable.getParmValue();
        long seq = 0;
        // 取SEQ最大值
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("SEQ", seq + 1);
        }

    }

    /**
     * 号别table监听事件
     * @param row int
     */
    public void onTYPETABLEClicked(int row) {
        flg = true;
        if (row < 0)
            return;
        TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");

        int typeFeeAllRow = typeFeeTable.getRowCount();
        String orderCode = typeFeeTable.getItemString(typeFeeAllRow - 1,
            "ORDER_CODE");
        if ( (orderCode != null && orderCode.length() != 0) ||
            typeFeeAllRow == 0) {
            this.onDeleteC();
        }

        TParm data = (TParm) callFunction("UI|TYPETABLE|getParmValue");
        setValueForParm(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG",
            data, row);
        selectRow = row;

        typeFeeTable.setFilter("ADM_TYPE = '" + getValue("ADM_TYPE").toString() +
                               "' AND CLINICTYPE_CODE = '" +
                               getValue("CLINICTYPE_CODE").toString() + "'");
        typeFeeTable.filter();
        //开始反查费用
        String selOrderCode = "";
        double ownPrice = 0.00;
        double sumPrice=0.00;
        int allRow = typeFeeTable.getRowCount();
        for (int i = 0; i < allRow; i++) {
            selOrderCode = typeFeeTable.getValueAt(i, 2).toString();
            ownPrice = BIL.getFee(selOrderCode);
            if (ownPrice < 0)
                ownPrice = 0;
            sumPrice+=ownPrice;
            typeFeeTable.setValueAt(ownPrice<0?0.00:ownPrice, i, 5);
        }
        //显示总费用
        this.setValue("TOT_FEE",sumPrice+"");
        //结束反查费用
        typeFeeAllRow = typeFeeTable.getRowCount();
        orderCode = typeFeeTable.getItemString(typeFeeAllRow - 1, "ORDER_CODE");
        if ( (orderCode != null && orderCode.length() != 0) ||
            typeFeeAllRow == 0) {
            this.onNew();
        }
    }

    /**
     * 号别table监听事件
     * @param row int
     */
    public void onTYPEFEETABLEClicked(int row) {
        flg = false;
        TTable typeTable = (TTable)this.getComponent("TYPETABLE");
        typeTable.clearSelection();
        if (row < 0)
            return;
    }

    /**
     * 得到最大的编号
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.rowCount();
        // 保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // 保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        // 最大号加1
        s++;
        return s;
    }
}
