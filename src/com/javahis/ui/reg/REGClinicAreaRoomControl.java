package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import java.util.ArrayList;
import com.dongyang.ui.TTableNode;
import jdo.sys.SYSHzpyTool;
import java.util.Vector;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: 诊区对诊室控制类</p>
 *
 * <p>Description: 诊区对诊室控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class REGClinicAreaRoomControl
    extends TControl {
    int selectRow = -1;
    boolean flg;
    boolean enforceFlg=false;//在初始化界面时，没有显示出一行新的数据就修改或删除数据会出现错误
    TParm data;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        // 添加诊区table单击事件
        callFunction("UI|AREATABLE|addEventListener",
                     "AREATABLE->" + TTableEvent.CLICKED, this,
                     "onAREATABLEClicked");
        // 添加诊室table单击事件
        callFunction("UI|ROOMTABLE|addEventListener",
                     "ROOMTABLE->" + TTableEvent.CLICKED, this,
                     "onROOMTABLEClicked");
        //添加诊室table值改变事件
        this.addEventListener("ROOMTABLE->" + TTableEvent.CHANGE_VALUE,
                              "onROOMTABLEChargeValue");

        initPage();
        onClear();
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        TTable roomTable = (TTable) getComponent("ROOMTABLE");
        //===========pangben modify 20110421 stop

    }

    /**
     * 初始化界面
     */
    public void initPage() {
        TTable roomTable = (TTable)this.getComponent("ROOMTABLE");
        String whereSql = "";
        if(!"".equals(Operator.getRegion()))
            whereSql = " WHERE  REGION_CODE = '"+Operator.getRegion()+"'  ";
        String orderBySql = " ORDER BY CLINICROOM_NO,SEQ ";
        String roomSql = roomTable.getSQL()+whereSql+orderBySql;
        roomTable.setSQL(roomSql);
        roomTable.filter();

    }

    /**
     * 诊室table值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onROOMTABLEChargeValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //拿到table
        TTable table = node.getTable();
        //得到改变的行
        int row = node.getRow();
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        //如果是名称改变了拼音1自动带出,并且科室名称不能为空
        if ("CLINICROOM_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox("名称不能为空!");
                return true;
            }
            //默认简称
            table.setItem(row, "CLINICROOM_DESC", value);
            String py = SYSHzpyTool.getInstance().charToCode(value);
            table.setItem(row, "PY1", py);
            //设置行可保存
            table.getDataStore().setActive(row, true);

        }
        return false;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String clinicCode = TypeTool.getString(getValue("CLINICAREA_CODE"));
        String clinicDesc = TypeTool.getString(getValue("CLINIC_DESC"));
        //===========pangben modify 20110421 start
        String regionCode=this.getValueString("REGION_CODE");
            //===========pangben modify 20110421 stop
        StringBuffer sb = new StringBuffer();
        if (clinicCode != null && clinicCode.length() > 0)
            sb.append(" CLINICAREA_CODE like '" + clinicCode + "%' ");
        if (clinicDesc != null && clinicDesc.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CLINIC_DESC like '" + clinicDesc + "%' ");
        }
       //===========pangben modify 20110421 start
        if (!"".equals(regionCode) && null != regionCode){
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE='" + regionCode + "' ");
        }
            //===========pangben modify 20110421 stop
        String sql1 =
            "  SELECT CLINICAREA_CODE,CLINIC_DESC,PY1,PY2,SEQ," +
            "         DESCRIPTION,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM " +
            "    FROM REG_CLINICAREA ";
        String sql2 = " ORDER BY SEQ,CLINICAREA_CODE ";
        if (sb.length() > 0)
            sql1 += " WHERE " + sb.toString() + sql2;
        else
            sql1 = sql1 + sql2;
      //  System.out.println("sql::"+sql1);
        TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql1));
        if (sqlParm.getErrCode() < 0) {
            messageBox(sqlParm.getErrText());
            return;
        }
        //诊区table赋值
        this.callFunction("UI|AREATABLE|setParmValue", sqlParm);
        //诊室table赋值
        TTable roomTable = (TTable) getComponent("ROOMTABLE");
        TDataStore roomDataStore = roomTable.getDataStore();
        roomDataStore.retrieve();
        String orderBySql = " ORDER BY CLINICROOM_NO,SEQ ";
        roomTable.setFilter("  REGION_CODE = '" + Operator.getRegion() + "'  " +
                            orderBySql);
        roomTable.filter();

    }

    /**
     * 保存
     * @return boolean
     */
    public boolean onSave() {
//        if(!enforceFlg){
//            this.messageBox("请先选择此诊区对应的诊室");
//            return false;
//        }
        //诊区table保存
        if (!this.emptyTextCheck("CLINICAREA_CODE"))
            return false;
        String areaCode = getValue("CLINICAREA_CODE").toString(); //诊区代码
        String areaDesc = getValue("CLINIC_DESC").toString(); //诊区说明
        String py1 = getValue("AREA_PY1").toString(); //拼音码
        String py2 = getValue("AREA_PY2").toString(); //注记码
        int seq = TypeTool.getInt(getValue("AREA_SEQ")); //顺序号
        String description = getValue("AREA_DESCRIPTION").toString(); //备注
        String regionCode = getValue("REGION_CODE").toString(); //区域
        String optUser = Operator.getID(); //操作人员
        Timestamp optDate = SystemTool.getInstance().getDate(); //操作日期
        String optTerm = Operator.getIP(); //操作终端
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE", areaCode);
        parm.setData("CLINIC_DESC", areaDesc);
        parm.setData("PY1", py1);
        parm.setData("PY2", py2);
        parm.setData("SEQ", seq);
        parm.setData("DESCRIPTION", description);
        parm.setData("REGION_CODE", regionCode);
        parm.setData("OPT_USER", optUser);
        parm.setData("OPT_DATE", optDate);
        parm.setData("OPT_TERM", optTerm);
        String selSql =
            " SELECT CLINICAREA_CODE FROM REG_CLINICAREA WHERE CLINICAREA_CODE = '" +
            areaCode + "'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(selSql));
        if (selParm.getCount("CLINICAREA_CODE") > 0) {
            String updateSql =
                " UPDATE REG_CLINICAREA " +
                "    SET CLINIC_DESC='" + areaDesc + "',PY1='" + py1 +
                "',PY2='" + py2 + "',SEQ='" + seq + "',DESCRIPTION='" +
                description + "'," +
                "        REGION_CODE='" + regionCode + "',OPT_USER='" + optUser +
                "',OPT_DATE=SYSDATE,OPT_TERM='" + optTerm + "' " +
                "  WHERE CLINICAREA_CODE='" + areaCode + "'";
            TParm updateParm = new TParm(TJDODBTool.getInstance().update(
                updateSql));
            //刷新，设置末行某列的值
            int row = (Integer) callFunction("UI|AREATABLE|getSelectedRow");
            if (row < 0){}
            else {
                TParm data = (TParm) callFunction("UI|AREATABLE|getParmValue");
                data.setRowData(row, parm);
                callFunction("UI|AREATABLE|setRowParmValue", row, data);
            }

        }
        else {
            String sql =
                " INSERT INTO REG_CLINICAREA " +
                "             (CLINICAREA_CODE,CLINIC_DESC,PY1,PY2,SEQ," +
                "             DESCRIPTION,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM) " +
                "      VALUES ('" + areaCode + "','" + areaDesc + "','" + py1 +
                "','" + py2 + "','" + seq + "'," +
                "             '" + description + "','" + regionCode + "','" +
                optUser + "',SYSDATE,'" + optTerm + "') ";
            TParm insertParm = new TParm(TJDODBTool.getInstance().update(sql));
            if (insertParm.getErrCode() < 0) {
                messageBox(insertParm.getErrText());
                return false;
            }
            //table上加入新增的数据显示
            callFunction("UI|AREATABLE|addRow", parm,
                         "CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;" +
                         "DESCRIPTION;REGION_CODE;OPT_USER;OPT_DATE;OPT_TERM");
        }
        //诊室table保存
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable roomTable = (TTable) getComponent("ROOMTABLE");
        // 接收文本
        roomTable.acceptText();
        TDataStore dataStore = roomTable.getDataStore();
        // 获得全部改动的行号
        int rows[] = dataStore.getModifiedRows();
        // 给固定数据配数据
        for (int i = 0; i < rows.length; i++) {
            dataStore.setItem(rows[i], "OPT_USER", optUser);
            dataStore.setItem(rows[i], "OPT_DATE", date);
            dataStore.setItem(rows[i], "OPT_TERM", optTerm);
        }
        if (!roomTable.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        roomTable.setDSValue();
        this.onClear();
        return true;
    }

    /**
     * 新增方法
     */
    public void onNew() {
        enforceFlg=true;//可以修改或删除操作
        TTable table = (TTable) getComponent("ROOMTABLE");
        String maxCode = "";
        // 新添加数据的顺序编号
        int maxSeq = getMaxSeq(table.getDataStore(), "SEQ");
        if (getValue("CLINICAREA_CODE").toString().length() > 0) {
            // 新添加的行号
            int row = table.addRow();
            // 当前选中的行
            table.setSelectedRow(row);
            // 默认职称代码
            table.setItem(row, "CLINICROOM_NO", maxCode);
            // 默认顺序编号
            table.setItem(row, "SEQ", maxSeq);
            //默认区域
            table.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
            //默认诊区代码
            table.setItem(row, "CLINICAREA_CODE", getValue("CLINICAREA_CODE"));
            //设置行不可保存
            table.getDataStore().setActive(row, false);
        }
        else {
            this.messageBox("无归属诊区");
            return;
        }
    }

    /**
     * 删除
     */
    public void onDelete() {
//        this.messageBox_(flg);
        TTable areaTable = (TTable)this.getComponent("AREATABLE");
        String clinicAreaCode = getValue("CLINICAREA_CODE").toString();
        TTable roomTable = (TTable)this.getComponent("ROOMTABLE");
        int roomSelRow = roomTable.getSelectedRow();
        String clinicRoomNo = roomTable.getItemString(roomSelRow,
            "CLINICROOM_NO");
        String clinicRoomAreaCode = roomTable.getItemString(roomSelRow,
            "CLINICAREA_CODE");
        String delAreaSql = "";
        String delAreaRoomSql = "";
        String delRoomSql = "";
        ArrayList list = new ArrayList();
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            if (flg) {
                delAreaSql =
                    " DELETE REG_CLINICAREA WHERE CLINICAREA_CODE = '" +
                    clinicAreaCode + "' ";
                delAreaRoomSql =
                    " DELETE REG_CLINICROOM WHERE CLINICAREA_CODE = '" +
                    clinicAreaCode + "'";
                list.add(delAreaSql);
                list.add(delAreaRoomSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delAreaParm = new TParm(TJDODBTool.getInstance().update(
                    allSql));
                if (delAreaParm.getErrCode() < 0) {
                    err(delAreaParm.getErrName() + " " + delAreaParm.getErrText());
                    return;
                }
                areaTable.retrieve();
            }
            else {
                delRoomSql =
                    " DELETE REG_CLINICROOM WHERE CLINICROOM_NO = '" +
                    clinicRoomNo + "' ";
                TParm delRoomParm = new TParm(TJDODBTool.getInstance().update(
                    delRoomSql));
                if (delRoomParm.getErrCode() < 0) {
                    err(delRoomParm.getErrName() + " " + delRoomParm.getErrText());
                    return;
                }
                roomTable.retrieve();
                roomTable.setFilter(" CLINICAREA_CODE = '" + clinicRoomAreaCode + "' AND REGION_CODE = '"+Operator.getRegion()+"'  ");
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
    public void onDeleteC() {
//        this.messageBox_(flg);
        TTable areaTable = (TTable)this.getComponent("AREATABLE");
        TParm areaParm = areaTable.getParmValue();
        int areaSelRow = areaTable.getSelectedRow();
        String clinicAreaCode = areaParm.getValue("CLINICAREA_CODE",
                                                  areaSelRow);
        TTable roomTable = (TTable)this.getComponent("ROOMTABLE");
        int roomSelRow = roomTable.getSelectedRow();
        String clinicRoomNo = roomTable.getItemString(roomSelRow,
            "CLINICROOM_NO");
        String clinicRoomAreaCode = roomTable.getItemString(roomSelRow,
            "CLINICAREA_CODE");
        String delAreaSql = "";
        String delAreaRoomSql = "";
        String delRoomSql = "";
        ArrayList list = new ArrayList();
//        if (this.messageBox("询问", "是否删除", 2) == 0) {
        if (false) {
            if (flg) {
                delAreaSql =
                    " DELETE REG_CLINICAREA WHERE CLINICAREA_CODE = '" +
                    clinicAreaCode + "' ";
                delAreaRoomSql =
                    " DELETE REG_CLINICROOM WHERE CLINICAREA_CODE = '" +
                    clinicAreaCode + "'";
                list.add(delAreaSql);
                list.add(delAreaRoomSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delAreaParm = new TParm(TJDODBTool.getInstance().update(
                    allSql));
                if (delAreaParm.getErrCode() < 0) {
                    err(delAreaParm.getErrName() + " " + delAreaParm.getErrText());
                    return;
                }
                areaTable.retrieve();
            }
            else {
                delRoomSql =
                    " DELETE REG_CLINICROOM WHERE CLINICROOM_NO = '" +
                    clinicRoomNo + "' ";
                TParm delRoomParm = new TParm(TJDODBTool.getInstance().update(
                    delRoomSql));
                if (delRoomParm.getErrCode() < 0) {
                    err(delRoomParm.getErrName() + " " + delRoomParm.getErrText());
                    return;
                }
                roomTable.retrieve();
                roomTable.setFilter(" CLINICAREA_CODE = '" + clinicRoomAreaCode +
                                    "' AND REGION_CODE = '"+Operator.getRegion()+"'  ");
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
        clearValue("CLINICAREA_CODE;CLINIC_DESC;AREA_PY1;AREA_PY2;AREA_SEQ;AREA_DESCRIPTION;REGION_CODE");
        this.callFunction("UI|AREATABLE|removeRowAll");
        this.callFunction("UI|ROOMTABLE|removeRowAll");
        //===========pangben modify 20110421 start
        this.setValue("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110421 stop
        TTable areaTable = (TTable)this.getComponent("AREATABLE");
        TDataStore areaDataStore = areaTable.getDataStore();
        areaDataStore.resetModify();
//        TTable roomTable = (TTable)this.getComponent("ROOMTABLE");
//        TDataStore roomDataStore = roomTable.getDataStore();
//        roomDataStore.resetModify();
//        roomTable.setFilter("  REGION_CODE = '"+Operator.getRegion()+"'  ");
//        roomTable.filter();
        selectRow = -1;
        this.onQuery();
        data = areaTable.getParmValue();
        long seq = 0;
        // 取SEQ最大值
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("AREA_SEQ", seq + 1);
        }
        enforceFlg=false;
    }

    /**
     * 诊区table监听事件
     * @param row int
     */
    public void onAREATABLEClicked(int row) {
        flg = true;
        if (row < 0)
            return;
        TTable roomTable = (TTable) getComponent("ROOMTABLE");

        int roomAllRow = roomTable.getRowCount();
        String clinicRoomNo = roomTable.getItemString(roomAllRow - 1,
            "CLINICROOM_NO");
        if ( (clinicRoomNo != null && clinicRoomNo.length() != 0) ||
            roomAllRow == 0) {
            this.onDeleteC();
        }

        TParm data = (TParm) callFunction("UI|AREATABLE|getParmValue");
        setValueForParm(
            "CLINICAREA_CODE;CLINIC_DESC;AREA_PY1:PY1;AREA_PY2:PY2;AREA_SEQ:SEQ;AREA_DESCRIPTION:DESCRIPTION;REGION_CODE",
            data, row);
        selectRow = row;
        //=============pangben modify 20110531 start
        String region="";
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").trim().length()>0 ){
            region = " AND REGION_CODE = '" + this.getValueString("REGION_CODE") + "' ";
        }
        roomTable.setFilter("CLINICAREA_CODE = '" +
                            getValue("CLINICAREA_CODE").toString() + "' "+region);
        //=============pangben modify 20110531 stop
//        roomTable.retrieve();
        roomTable.filter();
        roomAllRow = roomTable.getRowCount();
        clinicRoomNo = roomTable.getItemString(roomAllRow - 1, "CLINICROOM_NO");
        if ( (clinicRoomNo != null && clinicRoomNo.length() != 0) ||
            roomAllRow == 0) {
            this.onNew();
        }
    }

    /**
     * 诊室table监听事件
     * @param row int
     */
    public void onROOMTABLEClicked(int row) {
        flg = false;
        TTable areaTable = (TTable)this.getComponent("AREATABLE");
        areaTable.clearSelection();
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
