package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSPublic;
import jdo.sys.SYSSQL;

/**
 *
 * <p>Title: 护士站</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw 2008-12-24
 * @version 1.0
 */
public class SYSStationControl
    extends TControl {
    /**
     * 初始化拿到当前显示的护士站数据
     */
    TTable tableNow;
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 选中的树节点
     */
    private TTreeNode selectTreeNode;
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool = new SYSRuleTool("SYS_STATION");
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * 护士站
     */
    TTable tableStation;
    /**
     * 房间
     */
    TTable tableRoom;
    /**
     * 床
     */
    TTable tableBed;
    /**
     * 树
     */
    TTree tree;
    /**
     * 页签
     */
    TTabbedPane tablePane;
    /**
     * 初始化
     */
    public void onInit() { //初始化程序
        super.onInit();
        //给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //给Table添加侦听事件
        addEventListener("TABLESTATION->" + TTableEvent.CHANGE_VALUE,
                         "onTableStationChangeValue");
        addEventListener("TABLEROOM->" + TTableEvent.CHANGE_VALUE,
                         "onTableRoomChangeValue");
        addEventListener("TABLEBED->" + TTableEvent.CHANGE_VALUE,
                         "onTableBedChangeValue");
        tableStation = (TTable)this.getComponent("TABLESTATION");
        //===========pangben modify 20110422 start 添加区域参数
        tableStation.getDataStore().setSQL(SYSSQL.getSYSStation(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableStation.getDataStore().retrieve();
        tableStation.setDataStore(tableStation.getDataStore());
        tableStation.setDSValue();

        tableRoom = (TTable)this.getComponent("TABLEROOM");
        //===========pangben modify 20110422 start 添加区域参数
        tableRoom.getDataStore().setSQL(SYSSQL.getSYSRoom(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableRoom.getDataStore().retrieve();
        tableRoom.setDataStore(tableRoom.getDataStore());
        tableRoom.setDSValue();

        tableBed = (TTable)this.getComponent("TABLEBED");
         //===========pangben modify 20110422 start 添加区域参数
        tableBed.getDataStore().setSQL(SYSSQL.getSYSBed(Operator.getRegion()));
        //===========pangben modify 20110422 stop
        tableBed.getDataStore().retrieve();
        tableBed.setDataStore(tableBed.getDataStore());
        tableBed.setDSValue();

        tree = (TTree)this.getComponent("TREE");
        treeRoot = tree.getRoot();
        tablePane = (TTabbedPane)this.getComponent("TABBEDPANLE");
        //初始化树
        onInitSelectTree();
        //种树
        onCreatTree();
        tableNow = tableStation;
        //
        getTreeLoyar();
        iniComBox();
    }

    /**
     * 给展开层combox赋值
     */
    public void getTreeLoyar() {
        TComboBox comBox = (TComboBox)this.callFunction("UI|TREELOYAR|getThis");
        //吧数据付给combo
        comBox.setParmValue(SYSPublic.getTreeLoyar(treeRoot));
        //刷现界面上的combo
        comBox.updateUI();
    }

    /**
     * 展开到对应的层
     */
    public void onExtendToLoyar() {
        tree.expandLayer(getValueInt("TREELOYAR") - 1);
        tree.collapseLayer(getValueInt("TREELOYAR"));
    }

    /**
     * 初始化树
     */
    public void onInitSelectTree() {
        //得到树根
        treeRoot = tree.getRoot();
        if (treeRoot == null)
            return;
        //给根节点添加文字显示
        treeRoot.setText("护士站");
        //给根节点赋tag
        treeRoot.setType("Station");
        //设置根节点的id
        treeRoot.setID("");
        //清空所有节点的内容
        treeRoot.removeAllChildren();
        //调用树点初始化方法
        callMessage("UI|TREE|update");
    }

    /**
     * 设置combox
     */
    public void iniComBox() {
        TParm parm = new TParm();
        //护士站parm
        tableStation.acceptText();
        TDataStore dateStore = tableStation.getDataStore();
        String name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:STATION_CODE;name:STATION_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableBed.addItem("STATION", comboBox);
        tableRoom.addItem("STATION", comboBox);

        //房间
        dateStore = tableRoom.getDataStore();
        name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        comboBox = new TComboBox();
        comboBox.setParmMap("id:ROOM_CODE;name:ROOM_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableBed.addItem("ROOM", comboBox);

    }

    /**
     * 下载树数据
     * @param parentNode TTreeNode
     */
    public void onCreatTree() {
        //得到table对象
        tableNow = tableStation;
        //table接收所有改变值
        tableNow.acceptText();
        //得到护士站的datastore
        TDataStore dataStoreStation = tableNow.getDataStore();
        //得到数据总量
        int stationCount = dataStoreStation.rowCount();
        //循环给树插节点
        for (int i = 0; i < stationCount; i++) {
            String id = dataStoreStation.getItemString(i, "STATION_CODE");
            String name = dataStoreStation.getItemString(i, "STATION_DESC");
            String type = "Room";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            treeRoot.add(node);
            //调用房间节点
            CcratRoomTree(node);
        }
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //更新树
        tree.update();
        //设置树的默认选中节点
        tree.setSelectNode(treeRoot);
        selectTreeNode = treeRoot;

        tableStation.setFilter("");
        tableStation.filter();
        tableStation.setDSValue();

        tableRoom.setFilter("");
        tableRoom.filter();
        tableRoom.setDSValue();

    }

    /**
     * 初始化树上房间节点
     * @param parentNode TTreeNode
     */
    public void CcratRoomTree(TTreeNode parentNode) {
        //查看传入的节点
        if (parentNode == null)
            return;
        //table接收所有改变值
        tableRoom.acceptText();
        //得到的datastore
        TDataStore dataStoreRoom = tableRoom.getDataStore();
        String value = "STATION_CODE='" + parentNode.getID() + "'";
        dataStoreRoom.setFilter(value);
        dataStoreRoom.filter();
        //得到数据总量
        int stationCount = dataStoreRoom.rowCount();
        //循环给树插节点
        for (int i = 0; i < stationCount; i++) {
            String id = dataStoreRoom.getItemString(i, "ROOM_CODE");
            String name = dataStoreRoom.getItemString(i, "ROOM_DESC");
            String type = "Bed";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }
    }

    /**
     * 树的点击事件
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //得到点击树的节点对象
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if (tableNow != null)
            tableNow.acceptText();
        selectTreeNode = node;
        //保存选中节点的类型
        String type = node.getType();
        //存储过滤条件
        String value = "";
        //选中的树节点的ID
        String id = node.getID();
        //如果选中根节点
        if (type.equals("Station")) {
            //拿到护士站的table
            tableNow = tableStation;
            tableFilter(tableRoom, "");
            tableFilter(tableBed, "");
        }
        //如果选中护士站节点
        if (type.equals("Room")) {
            //拿到房间的table
            tableNow = tableRoom;
            value = "STATION_CODE='" + id + "'";
            tableFilter(tableRoom, value);
            tableFilter(tableBed, value);

        }
        //如果拿到的是房间节点
        if (type.equals("Bed")) {
            //得到床位的table
            tableNow = tableBed;
            //得到父节点对象
            TTreeNode parern = (TTreeNode) node.getParent();
            value = "ROOM_CODE='" + id + "' AND STATION_CODE='" + parern.getID() +
                "'";
            tableFilter(tableBed, value);

        }
    }

    /**
     * 过滤table
     * @param table TTable
     * @param filterSql String
     */
    public void tableFilter(TTable table, String filterSql) {
        table.setFilter(filterSql);
        table.filter();
    }

    /**
     * 护士站双击事件
     */
    public void onTableStationDoubleClicked() {
        //设置树的选中节点
        tree.setSelectNode(treeRoot);
        tree.update();

    }

    /**
     * 房间双击事件
     */
    public void onTableRoomDoubleClicked() {
        int row = tableRoom.getSelectedRow();
        String stationCode = tableRoom.getItemString(row, "STATION_CODE");
        TTreeNode node = treeRoot.findNodeForID(stationCode);
        if (node == null)
            return;
        //设置树的选中节点
        tree.setSelectNode(node);
        tree.update();

    }

    /**
     * 床双击事件
     */
    public void onTableBedDoubleClicked() {
        int row = tableBed.getSelectedRow();
        String roomCode = tableBed.getItemString(row, "ROOM_CODE");
        TTreeNode node = treeRoot.findNodeForID(roomCode);
        if (node == null)
            return;
        //设置树的选中节点
        tree.setSelectNode(node);
        tree.update();

    }

    /**
     * 属性护士站table改变值
     * @param obj Object
     */
    public boolean onTableStationChangeValue(Object obj) {
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
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        int row = node.getRow();
        //得到当前选中的树上节点
        String id = node.getTable().getItemString(row, 0);
        TTreeNode newTree = selectTreeNode.findChildNodeForID(id);
        //如果护士站名称改变带拼音
        if ("STATION_DESC".equals(columnName)) {
            //改名称就改树节点
            if (newTree != null) {
                newTree.setText(value);
                tree.update();
            }
            //给拼音
            String py = TMessage.getPy(value);
            node.getTable().setItem(row, "PY1", py);
            return false;
        }
        //如果是护士站代码更改
        if ("STATION_CODE".equals(columnName)) {

            //不能为空
            if (value.equals("") || value == null) {
                messageBox_("代码不能为空!");
                return true;
            }
            //不能重复
            if (node.getTable().getDataStore().exist("STATION_CODE='" +
                value +
                "'")) {
                messageBox_("编号" + node.getValue() + "重复!");
                return true;
            }
            //得到当前层编码长度
            int lengthStation = ruleTool.getClassNumber(1);
            if (lengthStation != value.length()) {
                messageBox_("编号" + node.getValue() + "长度不等于" + lengthStation +
                            "!");
                return true;
            }
            //改代码也改树
            if (newTree != null)
                newTree.setID(value);
            String oldValue = node.getOldValue().toString();
            //对房间中的护士站代码和房间号进行更改
            TTable tableRoom = (TTable) callFunction("UI|TABLEROOM|getThis");
            TDataStore dataStoreRoom = tableRoom.getDataStore();
            if (dataStoreRoom.isFilter()) {
                tableRoom.setFilter("");
                tableRoom.filter();
            }
            //对房间中的护士站代码进行修改
            int countRoom = dataStoreRoom.rowCount();
            for (int i = 0; i < countRoom; i++) {
                //得到护士站代码
                String codeStation = TCM_Transform.getString(dataStoreRoom.
                    getItemData(
                        i, "STATION_CODE"));
                //拿到老的房间号
                String codeRoom = TCM_Transform.getString(dataStoreRoom.
                    getItemData(
                        i, "ROOM_CODE"));
                if (codeStation.equals(oldValue)) {
                    //给护士站赋新值
                    dataStoreRoom.setItem(i, "STATION_CODE", value);
                    //给房间赋新值
                    String room = codeRoom.substring(value.length()); //切房间号
                    //换新房间号
                    dataStoreRoom.setItem(i, "ROOM_CODE", value + room);
                    //根据老房间号查找树上的节点
                    TTreeNode treenode1 = newTree.findNodeForID(codeRoom);
                    if (treenode1 != null)
                        treenode1.setID(value + room);
                }
            }
            //对床档的护士站代码进行更改
            TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
            TDataStore dataStoreBed = tableBed.getDataStore();
            if (dataStoreBed.isFilter()) {
                tableBed.setFilter("");
                tableBed.filter();
            }
            int countBed = dataStoreBed.rowCount();
            for (int i = 0; i < countBed; i++) {
                //拿到老的护士站号
                String codeStation = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "STATION_CODE"));
                //拿到老的房间号
                String codeRoom = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "ROOM_CODE"));
                //拿到老的床号
                String codeBed = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "BED_NO"));
                if (codeStation.equals(oldValue)) {
                    //换新护士站
                    dataStoreBed.setItem(i, "STATION_CODE", value);
                    //给房间赋新值
                    String room = codeRoom.substring(value.length()); //切房间号
                    //换新房间号
                    dataStoreBed.setItem(i, "ROOM_CODE", value + room);
                    //给房间赋新值
                    String Bed = codeBed.substring(value.length()); //切床号
                    //换新房间号
                    dataStoreBed.setItem(i, "BED_NO", value + Bed);
                }
            }
            return false;
        }
        tree.update();
        return false;
    }

    /**
     * 属性房间table改变值
     * @param obj Object
     */
    public boolean onTableRoomChangeValue(Object obj) {
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
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        int row = node.getRow();
        //得到当前选中的树上节点
        String id = node.getTable().getItemString(row, 0);
        TTreeNode newTree = selectTreeNode.findChildNodeForID(id);
        //如果房间名称改变带拼音
        if ("ROOM_DESC".equals(columnName)) {
            //改名称就改树节点
            if (newTree != null) {
                newTree.setText(value);
                tree.update();
            }
            //给拼音
            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(), "PY1", py);
            return false;
        }
        //房间号变化要检核重复和空
        if ("ROOM_CODE".equals(columnName)) {
            //不能为空
            if (value.equals("") || value == null) {
                messageBox_("代码不能为空!");
                return true;
            }
            if (node.getTable().getDataStore().exist("ROOM_CODE='" + value +
                "'")) {
                messageBox_("编号" + node.getValue() + "重复!");
                return true;
            }
            //得到当前层编码长度
            int length = ruleTool.getClassNumber(2);
            if (length != value.length()) {
                messageBox_("编号" + node.getValue() + "长度不等于" + length + "!");
                return true;
            }
            //改代码也改树
            if (newTree != null)
                newTree.setID(value);
            String oldValue = node.getOldValue().toString();
            //对床档的护士站代码进行更改
            TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
            TDataStore dataStoreBed = tableBed.getDataStore();
            if (dataStoreBed.isFilter()) {
                tableBed.setFilter("");
                tableBed.filter();
            }
            int countBed = dataStoreBed.rowCount();
            for (int i = 0; i < countBed; i++) {
                //拿老房间号
                String codeRoom = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "ROOM_CODE"));
                //拿老床号
                String codeBed = TCM_Transform.getString(dataStoreBed.
                    getItemData(
                        i, "BED_NO"));
                if (codeRoom.equals(oldValue)) {
                    //换新房间
                    dataStoreBed.setItem(i, "ROOM_CODE", value);
                    //换新床
                    String Bed = codeBed.substring(value.length()); //切床号
                    //换新房间号
                    dataStoreBed.setItem(i, "BED_NO", value + Bed);
                }
            }
            return false;
        }
        tree.update();
        return false;
    }

    /**
     * 属性床位table改变值
     * @param obj Object
     */
    public boolean onTableBedChangeValue(Object obj) {
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
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        if ("BED_NO_DESC".equals(columnName)) {
            //给拼音
            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(), "PY1", py);
            return false;
        }

        //床号变化
        if ("BED_NO".equals(columnName)) {
            //不能为空
            if (value.equals("") || value == null) {
                messageBox_("代码不能为空!");
                return true;
            }
            if (node.getTable().getDataStore().exist("BED_NO='" + value +
                "'")) {
                messageBox_("编号" + node.getValue() + "重复!");
                return true;
            }
            //得到当前层编码长度
            int length = ruleTool.getTot() + ruleTool.getSerial();
            if (length != value.length()) {
                messageBox_("编号" + node.getValue() + "长度不等于" + length + "!");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 新增方法
     */
    public void onNew() {
        //接收文本
        if (tableNow != null)
            tableNow.acceptText();
        //找到选中的树节点
        TTreeNode node = tree.getSelectionNode();
        //如果没找到当前选中的节点
        if (node == null)
            return;
        //检核当前选中的围着
        String type = node.getType();
        //如果选中根节点
        if (type.equals("Station")) {
            //调用新增护士站的方法
            tablePane.setSelectedIndex(0);
            stationNew(node);
            iniComBox();
        }
        //如果选中护士站节点
        if (type.equals("Room")) {
            //调用新增房间的方法
            tablePane.setSelectedIndex(1);
            roomNew(node.getID(), node);
            iniComBox();
        }
        //如果拿到的是房间节点
        if (type.equals("Bed")) {
            tablePane.setSelectedIndex(2);
            TTreeNode parentNode = (TTreeNode) node.getParent();
            //调用新增床位的方法
            bedNew(node.getID(), parentNode.getID());
        }
    }

    /**
     * 新增护士站
     */
    public void stationNew(TTreeNode node) {

        String stationMaxCode = getMaxCode(tableStation.
                                           getDataStore(), "STATION_CODE");
        // getNewCodeByString
        String stationCode = ruleTool.getNewCodeByString(stationMaxCode, 1);
        int seq = getMaxSeq(tableStation.
                            getDataStore(), "SEQ");
        //得到新添加的table数据行号
        int row = tableStation.addRow();
        //设置当前选中行为添加的行
        tableStation.setSelectedRow(row);
        //默认护士站代码
        tableStation.setItem(row, "STATION_CODE", stationCode);
        //默认护士站名称
        tableStation.setItem(row, "STATION_DESC", "");
        tableStation.setItem(row, "SEQ", seq);
        //树上新种节点
        String name = "护士站";
        String type = "Room";
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText("护士站");
        nodeNew.setID(stationCode);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * 新增房间
     */
    public void roomNew(String stationCode, TTreeNode node) {

        String roomMaxCode = getMaxCode(tableRoom.
                                        getDataStore(), "ROOM_CODE");
        String roomCode = stationCode + ruleTool.getNewCodeByString(roomMaxCode, 2);
        //得到新添加的table数据行号
        int row = tableRoom.addRow();
        //设置当前选中行为添加的行
        tableRoom.setSelectedRow(row);
        //默认房间号
        tableRoom.setItem(row, "ROOM_CODE", roomCode);
        //默认房间名称
        tableRoom.setItem(row, "ROOM_DESC", "");
        //默认护士站号
        tableRoom.setItem(row, "STATION_CODE", stationCode);
        //新种房间
        String name = "房间";
        String type = "Bed";
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText("房间");
        nodeNew.setID(roomCode);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * 新增床位
     */
    public void bedNew(String roomCode, String stationCode) {

        String bedMaxCode = getMaxCode(tableBed.getDataStore(),
                                       "BED_NO");
        String bedCode = roomCode + ruleTool.getNewCodeByString(bedMaxCode, 3);
        int seq = getMaxSeq(tableBed.getDataStore(), "SEQ");
        //得到新添加的table数据行号
        int row = tableBed.addRow();
        //设置当前选中行为添加的行
        tableBed.setSelectedRow(row);
        //默认房间号
        tableBed.setItem(row, "BED_NO", bedCode);
        //默认护士站号
        tableBed.setItem(row, "STATION_CODE", stationCode);
        //默认房间号
        tableBed.setItem(row, "ROOM_CODE", roomCode);
        tableBed.setItem(row, "SEQ", seq);
//        //默认科室
//        tableBed.setItem(row, "DEPT_CODE", deptCode);
    }

    /**
     * 得到最大的编号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        int count = dataStore.rowCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = dataStore.getItemString(i, columnName);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }

    /**
     * 得到最大的序号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        //保存数据量
        int count = dataStore.rowCount();
        //保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            //保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        //最大号加1
        s++;
        return s;
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        //接收文本
        if (tableNow != null)
            tableNow.acceptText();
        //得到要删除的table行号
        int row = tableNow.getSelectedRow();
        //如果没有选中行
        if (row == -1)
            return;
        //找到选中的树节点
        TTreeNode node = tree.getSelectionNode();
        //如果没找到当前选中的节点
        if (node == null)
            return;
        //检核当前选中的围着
        String type = node.getType();
        //如果选中根节点
        if (type.equals("Station")) {
            tablePane.setSelectedIndex(0);
            switch (messageBox("提示信息", "将连同房间和病床一并删除!", this.YES_NO_OPTION)) {
                //保存
                case 0:

                    //删除护士站的方法
                    stationDeleteRow(row, node);
                    return;
                    //不保存
                case 1:
                    return;
            }
        }
        //如果选中护士站节点
        if (type.equals("Room")) {
            tablePane.setSelectedIndex(1);
            switch (messageBox("提示信息", "将连同病床一起删除!", this.YES_NO_OPTION)) {
                //保存
                case 0:

                    //删除病房的方法
                    roomDeleteRow(row, node);
                    return;
                    //不保存
                case 1:
                    return;
            }
        }
        //如果拿到的是房间节点
        if (type.equals("Bed")) {
            tablePane.setSelectedIndex(2);
            switch (messageBox("提示信息", "确认删除", this.YES_NO_OPTION)) {
                //保存
                case 0:

                    //删除床位的方法
                    bedDeleteRow(row);
                    return;
                    //不保存
                case 1:
                    return;
            }
        }
    }

    /**
     * 删除护士站
     */
    public void stationDeleteRow(int row, TTreeNode node) {
        //拿到要删除的护士站代码
        String code = tableStation.getItemString(row, 0);

        //删房间
        TDataStore dataStoreRoom = tableRoom.getDataStore();
        if (dataStoreRoom.isFilter()) {
            tableRoom.setFilter("");
            tableRoom.filter();
        }
        int countRoom = dataStoreRoom.rowCount();
        for (int i = countRoom - 1; i >= 0; i--) {
            //拿到护士站号
            String stationCode = dataStoreRoom.getItemString(i, "STATION_CODE");
            //如果是本护士站的
            if (stationCode.equals(code)) {
                //删除房间
                dataStoreRoom.deleteRow(i);
            }
        }
        //删床
        TDataStore dataStoreBed = tableBed.getDataStore();
        if (dataStoreBed.isFilter()) {
            tableBed.setFilter("");
            tableBed.filter();
        }
        int countBed = dataStoreBed.rowCount();
        for (int i = countBed - 1; i >= 0; i--) {
            //拿到护士站代码
            String stationCode = dataStoreBed.getItemString(i, "STATION_CODE");
            //如果是本护士站的
            if (stationCode.equals(code)) {
                dataStoreBed.deleteRow(i);
            }
        }
        //删除护士站的行
        tableStation.removeRow(row);
        //删除树上的节点
        TTreeNode treenodeChild = node.findNodeForID(code);
        if (treenodeChild != null)
            node.remove(treenodeChild);

        tree.update();
        tableStation.setDSValue();
        tableBed.setDSValue();
        tableRoom.setDSValue();
    }

    /**
     * 删除房间
     */
    public void roomDeleteRow(int row, TTreeNode node) {
        //拿到当前删除的房间
        String code = tableNow.getItemString(row, 0);
        //删除table上的当前行
        tableNow.removeRow(row);
        //删除当前房间节点
        TTreeNode treeNodeChild = node.findNodeForID(code);
        if (treeNodeChild != null)
            node.remove(treeNodeChild);
        //同时删除本护士站的所有床位
        TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
        TDataStore dataStoreBed = tableBed.getDataStore();
        if (dataStoreBed.isFilter()) {
            tableBed.setFilter("");
            tableBed.filter();
        }
        int countBed = dataStoreBed.rowCount();
        for (int i = countBed - 1; i >= 0; i--) {
            //拿到护士站代码
            String roomCode = TCM_Transform.getString(dataStoreBed.getItemData(
                i, "ROOM_CODE"));
            //如果是本护士站的
            if (roomCode.equals(code)) {
                dataStoreBed.deleteRow(i);
            }
        }
        tree.update();
    }

    /**
     * 删除病床
     */
    public void bedDeleteRow(int row) {
        tableNow.removeRow(row);
    }

    /**
     * 保存数据
     */
    public boolean onUpdate() {
        Timestamp date = SystemTool.getInstance().getDate();
        //接收文本
        tableStation.acceptText();
        TDataStore dateStoreStation = tableStation.getDataStore();
        String name = dateStoreStation.isFilter() ? dateStoreStation.FILTER :
            dateStoreStation.PRIMARY;
        int rowsStation[] = dateStoreStation.getModifiedRows(name);
        //给固定数据配数据
        for (int i = 0; i < rowsStation.length; i++) {
            tableStation.setItem(rowsStation[i], "OPT_USER",
                                 Operator.getID());
            tableStation.setItem(rowsStation[i], "OPT_DATE", date);
            tableStation.setItem(rowsStation[i], "OPT_TERM",
                                 Operator.getIP());
        }

        for (int i = 0; i < dateStoreStation.rowCount(); i++) {
            if ("".equals(dateStoreStation.getItemString(i, "DEPT_CODE"))) {
                this.messageBox("科室代码不能为空");
                return false;
            }
        }
        //取出代理产品sql
        String[] station = tableStation.getUpdateSQL();
        //接收文本
        tableRoom.acceptText();

        TDataStore dataStoreRoom = tableRoom.getDataStore();
        name = dataStoreRoom.isFilter() ? dataStoreRoom.FILTER :
            dataStoreRoom.PRIMARY;
        //获得全部改动的行号
        int rowsRoom[] = dataStoreRoom.getModifiedRows(name);
        //给固定数据配数据
        for (int i = 0; i < rowsRoom.length; i++) {
            tableRoom.setItemFilter(rowsRoom[i], "OPT_USER", Operator.getID());
            tableRoom.setItemFilter(rowsRoom[i], "OPT_DATE", date);
            tableRoom.setItemFilter(rowsRoom[i], "OPT_TERM", Operator.getIP());
        }

        for (int i = 0; i < dataStoreRoom.rowCount(); i++) {
            if ("".equals(dataStoreRoom.getItemString(i, "STATION_CODE"))) {
                this.messageBox("病区代码不能为空");
                return false;
            }
        }

        String[] room = dataStoreRoom.getUpdateSQL();

        //接收文本
        tableBed.acceptText();
        TDataStore dataStoreBed = tableBed.getDataStore();
        name = dataStoreBed.isFilter() ? dataStoreBed.FILTER :
            dataStoreBed.PRIMARY;
        //获得全部改动的行号
        int rowsBed[] = dataStoreBed.getModifiedRows(name);
        //给固定数据配数据
        for (int i = 0; i < rowsBed.length; i++) {
            tableBed.setItemFilter(rowsBed[i], "OPT_USER", Operator.getID());
            tableBed.setItemFilter(rowsBed[i], "OPT_DATE", date);
            tableBed.setItemFilter(rowsBed[i], "OPT_TERM", Operator.getIP());
        }

        String[] bed = dataStoreBed.getUpdateSQL();
        //把两组sql合并
        bed = StringTool.copyArray(bed, room);
        bed = StringTool.copyArray(bed, station);
        //拿到保存sql的对象
        TJDODBTool dbTool = new TJDODBTool();
        //保存数据
        TParm result = new TParm(dbTool.update(bed));
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        //清空保存记录
        dateStoreStation.resetModify();
        dataStoreRoom.resetModify();
        dataStoreBed.resetModify();
        return true;
    }

    /**
     * 是否关闭窗口
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing() {
        // 如果有数据变更
        if (CheckChange())
            switch (this.messageBox("提示信息",
                                    "是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onUpdate())
                        return false;
                    break;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        //没有变更的数据
        return true;

    }

    /**
     * 检核是否有数据变更
     * @return boolean
     */
    public boolean CheckChange() {
        //变更病床
        TTable tableBed = (TTable) callFunction("UI|TABLEBED|getThis");
        if (tableBed.isModified())
            return true;
        //变更护士站
        TTable tableStation = (TTable) callFunction("UI|TABLESTATION|getThis");
        if (tableStation.isModified())
            return true;
        //变更房间
        TTable tableRoom = (TTable) callFunction("UI|TABLEROOM|getThis");
        if (tableRoom.isModified())
            return true;
        return false;
    }

    /* *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//        Operator.setData("admin", "HIS", "127.0.0.1", "C00101");
    }
}
