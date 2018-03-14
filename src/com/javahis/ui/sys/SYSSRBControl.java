package com.javahis.ui.sys;

import jdo.sys.SYSRuleTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import jdo.sys.SYSPublic;
import com.dongyang.ui.TTreeNode;
import jdo.bil.BILSQL;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:护士站房间床 </p>
 *
 * <p>Description:护士站房间床 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-9-18
 * @version 1.0
 */
public class SYSSRBControl
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
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool = new SYSRuleTool("SYS_STATION");
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * 树
     */
    TTree tree;

    /**
     * 初始化
     */
    public void onInit() { //初始化程序
        super.onInit();
        //给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

        tree = (TTree)this.getComponent("TREE");
        treeRoot = tree.getRoot();
        //重新加载树
        retriveTree();
    }

    /**
     * 重新加载树
     */
    public void retriveTree() {
        //初始化树
        onInitSelectTree();
        //种树
        onCreatTree();
        //
        getTreeLoyar();
        //初始化界面上的可输入
        cleckRoot("");

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
     * 下载树数据
     */
    public void onCreatTree() {
        //得到table对象
        tableNow = new TTable();
        tableNow.setSQL("select * from sys_station");
        tableNow.retrieve();

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
        TTable tableRoom = new TTable();
        tableRoom.setSQL("select * from sys_room");
        tableRoom.retrieve();
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
            creatBedTree(node);
        }
    }

    /**
     * 创建床位
     * @param parentNode TTreeNode
     */
    public void creatBedTree(TTreeNode parentNode) {
        //查看传入的节点
        if (parentNode == null)
            return;

        //得到的datastore
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("select * from sys_bed");
        dataStore.retrieve();

        String value = "ROOM_CODE='" + parentNode.getID() + "'";
        dataStore.setFilter(value);
        dataStore.filter();
        //得到数据总量
        int stationCount = dataStore.rowCount();
        //循环给树插节点
        for (int i = 0; i < stationCount; i++) {
            String id = dataStore.getItemString(i, "BED_NO");
            String name = dataStore.getItemString(i, "BED_NO_DESC");
            String type = "BEDSEQ";
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
        //保存选中节点的类型
        String type = node.getType();
        //选中的树节点的ID
        String id = node.getID();
        //如果选中根节点
        if (type.equals("Station")) {
            //根节点选择处理
            cleckRoot(id);
        }
        //如果选中护士站节点
        if (type.equals("Room")) {
            //点击护士站
            cleckStation(id);
        }
        //如果拿到的是房间节点
        if (type.equals("Bed")) {
            //点击房间
            cleckRoom(id);
        }
        //病床节点
        if (type.equals("BEDSEQ")) {
            //病床点击
            cleckBed(id);
        }
    }

    /**
     * 根节点选择处理
     * @param id String
     */
    public void cleckRoot(String id) {
        //护士站属性可编辑
        setStationEnabled(true);
        clearStation();
        //房间属性可编辑
        setRoomEnabled(false);
        clearRoom();
        //床位属性克编辑
        setBedEnabled(false);
        clearBed();
        this.callFunction("UI|STATIONSAVE|setEnabled", true);
        this.callFunction("UI|STATIONCLEAR|setEnabled", true);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", false);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", false);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", false);
    }

    /**
     * 点击护士站
     * @param id String
     */
    public void cleckStation(String id) {
        //处理护士站属性
        TParm stationParm = setStationData(id);
        //护士站属性可编辑
        setStationEnabled(true);
        this.callFunction("UI|STATION_CODE|setEnabled", false);
        //房间属性可编辑
        setRoomEnabled(true);
        clearRoom();
        //床位属性克编辑
        setBedEnabled(false);
        clearBed();

        this.callFunction("UI|STATIONSAVE|setEnabled", true);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", true);

        this.callFunction("UI|ROOMSAVE|setEnabled", true);
        this.callFunction("UI|ROOMCLEAR|setEnabled", true);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", false);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", false);
    }

    /**
     * 点击房间
     * @param id String
     */
    public void cleckRoom(String id) {
        //处理房间数据
        TParm parmRoom = setRoomData(id);
        //处理护士站数据
        setStationData(parmRoom.getValue("STATION_CODE", 0));
        //护士站属性可编辑
        setStationEnabled(false);
        //房间属性可编辑
        setRoomEnabled(true);
        this.callFunction("UI|ROOM_CODE|setEnabled", false);
        //床位属性克编辑
        setBedEnabled(true);
        clearBed();

        this.callFunction("UI|STATIONSAVE|setEnabled", false);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", true);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", true);

        this.callFunction("UI|BEDSAVE|setEnabled", true);
        this.callFunction("UI|BEDCLEAR|setEnabled", true);
        this.callFunction("UI|BEDDELETE|setEnabled", false);

    }

    /**
     * 床位点击处理事件
     * @param id String
     */
    public void cleckBed(String id) {
        TParm bedData = setBedData(id);
        //处理护士站数据
        setStationData(bedData.getValue("STATION_CODE", 0));
        //床位数据
        setRoomData(bedData.getValue("ROOM_CODE", 0));
        //护士站属性可编辑
        setStationEnabled(false);
        //房间属性可编辑
        setRoomEnabled(false);
        //床位属性克编辑
        setBedEnabled(true);
        this.callFunction("UI|BED_NO|setEnabled", false);

        this.callFunction("UI|STATIONSAVE|setEnabled", false);
        this.callFunction("UI|STATIONCLEAR|setEnabled", false);
        this.callFunction("UI|STATIONDELETE|setEnabled", false);

        this.callFunction("UI|ROOMSAVE|setEnabled", false);
        this.callFunction("UI|ROOMCLEAR|setEnabled", false);
        this.callFunction("UI|ROOMDELETE|setEnabled", false);

        this.callFunction("UI|BEDSAVE|setEnabled", true);
        this.callFunction("UI|BEDCLEAR|setEnabled", false);
        this.callFunction("UI|BEDDELETE|setEnabled", true);

    }

    /**
     * 处理护士站数据
     * @param stationCode String
     * @return TParm
     */
    public TParm setStationData(String stationCode) {
        TParm parmStation = getStationData(stationCode);
        if (parmStation.getErrCode() < 0)
            return parmStation;
        setValueForParm(
            "STATION_CODE;STATION_DESC;PY1;DEPT_CODE;REGION_CODE;ORG_CODE;" +
            "LOC_CODE;PRINTER_NO;TEL_EXT;SEQ", parmStation, 0);
        return parmStation;
    }

    /**
     * 处理房间数据
     * @param roomCode String
     * @return TParm
     */
    public TParm setRoomData(String roomCode) {
        TParm parmRoom = getRoomData(roomCode);
        if (parmRoom.getErrCode() < 0)
            return parmRoom;
        setValueForParm(
            "ROOM_CODE;ROOM_DESC;ROOMPY1;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;DESCRIPT",
            parmRoom, 0);
        setValue("ROOMPY1", parmRoom.getValue("PY1", 0));
        return parmRoom;
    }

    /**
     * 处理床位数据
     * @param bedCode String
     * @return TParm
     */
    public TParm setBedData(String bedCode) {
        TParm parmBed = getBedData(bedCode);
        if (parmBed.getErrCode() < 0)
            return parmBed;
        setValueForParm(
            "BED_NO;BED_NO_DESC;BEDPY1;ROOM_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;" +
            "APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;BED_ATTR_CODE;SEX_CODE;" +
            "OCCU_RATE_FLG;DR_APPROVE_FLG;HTL_FLG;BEDSEQ;DESCRIPTION", parmBed,
            0);
        setValue("BEDPY1", parmBed.getValue("PY1", 0));
        setValue("BEDSEQ", parmBed.getInt("SEQ", 0));
        return parmBed;
    }

    /**
     * 得到护士站数据
     * @param stationCode String
     * @return TParm
     */
    public TParm getStationData(String stationCode) {
        TParm result = new TParm();
        String sql = BILSQL.getStationSql(stationCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据房间号得到房间数据
     * @param roomCode String
     * @return TParm
     */
    public TParm getRoomData(String roomCode) {
        TParm result = new TParm();
        String sql = BILSQL.getRoomSql(roomCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 处理床的数据
     * @param bedCode String
     * @return TParm
     */
    public TParm getBedData(String bedCode) {
        TParm result = new TParm();
        String sql = BILSQL.getBedSql(bedCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 清空
     */
    public void onClear() {
        //护士站属性可编辑
        setStationEnabled(true);
        clearStation();
        //房间属性可编辑
        setRoomEnabled(true);
        clearRoom();
        //床位属性克编辑
        setBedEnabled(true);
        clearBed();
    }

    /**
     * 清空护士站属性
     */
    public void clearStation() {
        //清空护士站属性
        clearValue(
            "STATION_CODE;STATION_DESC;PY1;DEPT_CODE;REGION_CODE;ORG_CODE;" +
            "LOC_CODE;PRINTER_NO;TEL_EXT;SEQ");
    }

    /**
     * 清空房间属性
     */
    public void clearRoom() {
        //清空过滤条件
        clearValue(
            "ROOM_CODE;ROOM_DESC;ROOMPY1;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;DESCRIPT");
    }

    /**
     * 清空床位属性
     */
    public void clearBed() {
        //清空过滤条件
        clearValue(
            "BED_NO;BED_NO_DESC;BEDPY1;BED_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;" +
            "APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;BED_ATTR_CODE;SEX_CODE;" +
            "OCCU_RATE_FLG;DR_APPROVE_FLG;HTL_FLG;BEDSEQ;DESCRIPTION");
    }

    /**
     * 床位属性可编辑
     * @param enable boolean
     */
    public void setStationEnabled(boolean enable) {
        this.callFunction("UI|STATION_CODE|setEnabled", enable);
        this.callFunction("UI|STATION_DESC|setEnabled", enable);
        this.callFunction("UI|PY1|setEnabled", enable);
        this.callFunction("UI|DEPT_CODE|setEnabled", enable);
        this.callFunction("UI|REGION_CODE|setEnabled", enable);
        this.callFunction("UI|ORG_CODE|setEnabled", enable);
        this.callFunction("UI|LOC_CODE|setEnabled", enable);
        this.callFunction("UI|PRINTER_NO|setEnabled", enable);
        this.callFunction("UI|TEL_EXT|setEnabled", enable);
        this.callFunction("UI|SEQ|setEnabled", enable);
    }

    /**
     * 房间属性编辑
     * @param enable boolean
     */
    public void setRoomEnabled(boolean enable) {
        this.callFunction("UI|ROOM_CODE|setEnabled", enable);
        this.callFunction("UI|ROOM_DESC|setEnabled", enable);
        this.callFunction("UI|ROOMPY1|setEnabled", enable);
        this.callFunction("UI|SEX_LIMIT_FLG|setEnabled", enable);
        this.callFunction("UI|RED_SIGN|setEnabled", enable);
        this.callFunction("UI|YELLOW_SIGN|setEnabled", enable);
        this.callFunction("UI|DESCRIPT|setEnabled", enable);
    }

    /**
     * 床位属性编辑
     * @param enable boolean
     */
    public void setBedEnabled(boolean enable) {

        this.callFunction("UI|BED_NO|setEnabled", enable);
        this.callFunction("UI|BED_NO_DESC|setEnabled", enable);
        this.callFunction("UI|BEDPY1|setEnabled", enable);
        this.callFunction("UI|BED_CLASS_CODE|setEnabled", enable);
        this.callFunction("UI|BED_TYPE_CODE|setEnabled", enable);
        this.callFunction("UI|ACTIVE_FLG|setEnabled", enable);
        this.callFunction("UI|APPT_FLG|setEnabled", enable);
        this.callFunction("UI|ALLO_FLG|setEnabled", enable);
        this.callFunction("UI|BED_OCCU_FLG|setEnabled", enable);
        this.callFunction("UI|RESERVE_BED_FLG|setEnabled", enable);
        this.callFunction("UI|SEX_CODE|setEnabled", enable);
        this.callFunction("UI|OCCU_RATE_FLG|setEnabled", enable);
        this.callFunction("UI|DR_APPROVE_FLG|setEnabled", enable);
        this.callFunction("UI|HTL_FLG|setEnabled", enable);
        this.callFunction("UI|BEDSEQ|setEnabled", enable);
        this.callFunction("UI|DESCRIPTION|setEnabled", enable);
    }

    /**
     * 护士站名称检核
     */
    public void onStationDescAction() {
        String stationDesc = getValueString("STATION_DESC");
        if (stationDesc == null || stationDesc.length() == 0) {
            if (!message("护士站名称为空,是否继续!"))
                return;
        }
        else {
            String sql = BILSQL.getStationDescSql(stationDesc);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("护士站名称重复,是否继续!"))
                    return;
        }
        String py = TMessage.getPy(stationDesc);
        this.setValue("PY1", py);
    }

    /**
     * 护士站代码检核
     * @return boolean
     */
    public boolean onStationCodeAction() {
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("护士站代码不能为空!");
            return false;
        }
        else {
            String sql = BILSQL.getStationSql(stationCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("护士站代码重复!");
                return false;
            }
        }
        //得到当前层编码长度
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("编号" + stationCode + "长度不等于" + lengthStation + "!");
            return false;
        }
        return true;
    }

    /**
     * 房间名称检核
     */
    public void onRoomDescAction() {
        String roomDesc = getValueString("ROOM_DESC");
        if (roomDesc == null || roomDesc.length() == 0) {
            if (!message("护士站名称为空,是否继续!"))
                return;
        }
        else {
            String sql = BILSQL.getRoomDescSql(roomDesc);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("护士站名称重复,是否继续!"))
                    return;
        }
        String py = TMessage.getPy(roomDesc);
        this.setValue("ROOMPY1", py);
    }

    /**
     * 房间代码检核
     * @return boolean
     */
    public boolean onRoomCodeAction() {
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("房间号不能为空!");
            return false;
        }
        else {
            String sql = BILSQL.getRoomSql(roomCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("房间号重复重复!");
                return false;
            }
        }
        //得到当前层编码长度
        int lengthStation = ruleTool.getClassNumber(2);
        if (lengthStation != roomCode.length()) {
            messageBox("编号" + roomCode + "长度不等于" + lengthStation + "!");
            return false;
        }
        return true;
    }

    /**
     * 床位名称检核
     */

    public void onBedDescAction() {
        String bedNoDesc = getValueString("BED_NO_DESC");
        if (bedNoDesc == null || bedNoDesc.length() == 0) {
            if (!message("床位名称为空,是否继续!"))
                return;
        }
        else {
            String sql = BILSQL.getBedNoDescSql(bedNoDesc);
            //System.out.println("bedCheckSql=" + sql);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            //System.out.println("bedSescCheckResult======" + result);
            if (result.getErrCode() < 0 || result.getCount() > 0)
                if (!message("床位名称重复,是否继续!"))
                    return;
        }
        String py = TMessage.getPy(bedNoDesc);
        this.setValue("BEDPY1", py);

    }

    /**
     * 床位号检核
     * @return boolean
     */
    public boolean onbedNoAction() {
        String bedNo = getValueString("BED_NO");
        if (bedNo == null || bedNo.length() == 0) {
            messageBox("床位号不能为空!");
            return false;
        }
        else {
            String sql = BILSQL.getBedSql(bedNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() > 0) {
                messageBox("床位号重复重复!");
                return false;
            }
        }
        //得到当前层编码长度
        int lengthStation = ruleTool.getTot() + ruleTool.getSerial();
        if (lengthStation != bedNo.length()) {
            messageBox("编号" + bedNo + "长度不等于" + lengthStation + "!");
            return false;
        }
        return true;
    }


    /**
     * 得到当前选择的节点类型
     * @return String
     */
    public String getTreeSelectNodeType() {
        //找到选中的树节点
        TTreeNode node = tree.getSelectionNode();
        //如果没找到当前选中的节点
        if (node == null)
            return "";
        //检核当前选中的围着
        String type = node.getType();
        return type;
    }

    /**
     * 保存护士站
     */
    public void onSaveStation() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Station"))
            sql = insertStation();
        if (type.equals("Room"))
            sql = updateStation();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (saveMessage(result)) {
            if (type.equals("Station"))
                insertTreeNode("", getValueString("STATION_DESC"),
                               "Room", getValueString("STATION_CODE"));
            if (type.equals("Room"))
                changeDesc(getValueString("STATION_CODE"),
                           getValueString("STATION_DESC"));
        }

        return;
    }

    /**
     * 保存完成提示信息
     * @param result TParm
     * @return boolean
     */
    public boolean saveMessage(TParm result) {
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        return true;
    }

    /**
     * 新增护士站
     * @return String
     */
    public String insertStation() {
        //检核护士站代码
        if (!onStationCodeAction())
            return "";
        return StationValue();
    }

    /**
     * 护士站新增sql
     * @return String
     */
    public String StationValue() {
        String value =
            "INSERT INTO SYS_STATION (STATION_CODE,STATION_DESC,PY1," +
            "DEPT_CODE,REGION_CODE,ORG_CODE,LOC_CODE,PRINTER_NO,TEL_EXT,SEQ," +
            "OPT_USER,OPT_DATE,OPT_TERM) VALUES (";
        //护士站代码
        value += "'" + this.getValue("STATION_CODE") + "'";
        //护士站名称
        value += ",'" + this.getValue("STATION_DESC") + "'";
        //拼音
        value += ",'" + this.getValue("PY1") + "'";
        //科室
        value += ",'" + this.getValue("DEPT_CODE") + "'";
        value += ",'" + this.getValue("REGION_CODE") + "'";
        value += ",'" + this.getValue("ORG_CODE") + "'";
        value += ",'" + this.getValue("LOC_CODE") + "'";
        value += ",'" + this.getValue("PRINTER_NO") + "'";
        value += ",'" + this.getValue("TEL_EXT") + "'";
        value += ",'" + this.getValueInt("SEQ") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * 跟新护士站
     * @return String
     */
    public String updateStation() {
        String value = "UPDATE SYS_STATION SET ";
        //护士站名称
        value += "STATION_DESC='" + this.getValue("STATION_DESC") + "'";
        //拼音
        value += ",PY1='" + this.getValue("PY1") + "'";
        //科室
        value += ",DEPT_CODE='" + this.getValue("DEPT_CODE") + "'";
        value += ",REGION_CODE='" + this.getValue("REGION_CODE") + "'";
        value += ",ORG_CODE='" + this.getValue("ORG_CODE") + "'";
        value += ",LOC_CODE='" + this.getValue("LOC_CODE") + "'";
        value += ",PRINTER_NO='" + this.getValue("PRINTER_NO") + "'";
        value += ",TEL_EXT='" + this.getValue("TEL_EXT") + "'";
        value += ",SEQ='" + this.getValue("SEQ") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "' WHERE STATION_CODE='" +
            this.getValue("STATION_CODE") + "'";
        return value;

    }

    /**
     * 保存房间
     */
    public void onSaveRoom() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Room"))
            sql = insertRoom();
        if (type.equals("Bed"))
            sql = updateRoom();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));

        if (saveMessage(result)) {
            if (type.equals("Room"))
                insertTreeNode(getValueString("STATION_CODE"),
                               getValueString("ROOM_DESC"),
                               "Bed", getValueString("ROOM_CODE"));
            if (type.equals("Bed"))
                changeDesc(getValueString("ROOM_CODE"),
                           getValueString("ROOM_DESC"));
        }
        return;
    }

    /**
     * 新增房间
     * @return String
     */
    public String insertRoom() {
        if (!onRoomCodeAction())
            return "";
        //检核护士站代码
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("护士站代码不能为空!");
            return "";
        }
        //得到护士站编码长度
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("编号" + stationCode + "长度不等于" + lengthStation + "!");
            return "";
        }
        return insertRoomSql();
    }

    /**
     * 插入房间的sql
     * @return String
     */
    public String insertRoomSql() {
        String value = "INSERT INTO SYS_ROOM (ROOM_CODE,ROOM_DESC,PY1," +
            "SEX_LIMIT_FLG,RED_SIGN,YELLOW_SIGN,DESCRIPT,STATION_CODE," +
            "OPT_USER,OPT_DATE,OPT_TERM) VALUES(";
        value += "'" + this.getValue("ROOM_CODE") + "'";
        value += ",'" + this.getValue("ROOM_DESC") + "'";
        value += ",'" + this.getValue("PY1") + "'";
        value += ",'" + this.getValue("SEX_LIMIT_FLG") + "'";
        value += ",'" + this.getValue("RED_SIGN") + "'";
        value += ",'" + this.getValue("YELLOW_SIGN") + "'";
        value += ",'" + this.getValue("DESCRIPT") + "'";
        value += ",'" + this.getValue("STATION_CODE") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * 更新房间
     * @return String
     */
    public String updateRoom() {
        String value = "UPDATE SYS_ROOM SET ";
        value += " ROOM_DESC='" + this.getValue("ROOM_DESC") + "'";
        value += ",PY1='" + this.getValue("ROOMPY1") + "'";
        value += ",SEX_LIMIT_FLG='" + this.getValue("SEX_LIMIT_FLG") + "'";
        value += ",RED_SIGN='" + this.getValue("RED_SIGN") + "'";
        value += ",YELLOW_SIGN='" + this.getValue("YELLOW_SIGN") + "'";
        value += ",DESCRIPT='" + this.getValue("DESCRIPT") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "'" +
            " WHERE ROOM_CODE ='" + this.getValue("ROOM_CODE") + "'";
        return value;

    }

    /**
     * 保存床位
     */
    public void onSaveBed() {
        String type = getTreeSelectNodeType();
        String sql = "";
        if (type.equals("Bed"))
            sql = insertBed();
        if (type.equals("BEDSEQ"))
            sql = updateBedSql();
        if (sql == null || sql.length() == 0)
            return;
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (saveMessage(result)) {
            if (type.equals("Bed"))
                insertTreeNode(getValueString("ROOM_CODE"),
                               getValueString("BED_NO_DESC"),
                               "BEDSEQ", getValueString("BED_NO"));
            if (type.equals("BEDSEQ"))
                changeDesc(getValueString("BED_NO"),
                           getValueString("BED_NO_DESC"));
        }
        return;
    }
    /**
     * 判断房间性别管制
     */
    public void onSexCodeChoose() {
        String sexLimitFlg = getValueString("SEX_LIMIT_FLG");
        String sexCode=getValueString("SEX_CODE");
        if(sexCode==null||sexCode.equals("")||sexCode.length()==0)
            return;
        if (sexLimitFlg == null || sexLimitFlg.length() == 0 ||
            sexLimitFlg.equals("N")) {
            messageBox("此房间不用性别管制!");
            setValue("SEX_CODE", "");
        }
    }
    /**
     * 新增床位
     * @return String
     */
    public String insertBed() {
        //床位检核
        if (!onbedNoAction())
            return "";
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("护士站代码不能为空!");
            return "";
        }
        //得到护士站编码长度
        int lengthStation = ruleTool.getClassNumber(1);
        if (lengthStation != stationCode.length()) {
            messageBox("编号" + stationCode + "长度不等于" + lengthStation + "!");
            return "";
        }
        //房间代码检核
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("房间号不能为空!");
            return "";
        }
        //得到房间编码长度
        int lengthRoom = ruleTool.getClassNumber(2);
        if (lengthRoom != roomCode.length()) {
            messageBox("编号" + roomCode + "长度不等于" + lengthRoom + "!");
            return "";
        }
        return insertBedSql();
    }

    /**
     * 插入床位的sql
     * @return String
     */
    public String insertBedSql() {
        String value =
            "INSERT INTO SYS_BED (ROOM_CODE,STATION_CODE,BED_NO,BED_NO_DESC,PY1," +
            "BED_CLASS_CODE,BED_TYPE_CODE,ACTIVE_FLG," +
            "APPT_FLG,ALLO_FLG,BED_OCCU_FLG,RESERVE_BED_FLG,SEX_CODE," +
            "OCCU_RATE_FLG,DR_APPROVE_FLG,HTL_FLG,SEQ,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM) VALUES(";
        value += "'" + this.getValue("ROOM_CODE") + "'";
        value += ",'" + this.getValue("STATION_CODE") + "'";
        value += ",'" + this.getValue("BED_NO") + "'";
        value += ",'" + this.getValue("BED_NO_DESC") + "'";
        value += ",'" + this.getValue("BEDPY1") + "'";
        value += ",'" + this.getValue("BED_CLASS_CODE") + "'";
        value += ",'" + this.getValue("BED_TYPE_CODE") + "'";
        value += ",'" + this.getValue("ACTIVE_FLG") + "'";
        value += ",'" + this.getValue("APPT_FLG") + "'";
        value += ",'" + this.getValue("ALLO_FLG") + "'";
        value += ",'" + this.getValue("BED_OCCU_FLG") + "'";
        value += ",'" + this.getValue("RESERVE_BED_FLG") + "'";
        value += ",'" + this.getValue("SEX_CODE") + "'";
        value += ",'" + this.getValue("OCCU_RATE_FLG") + "'";
        value += ",'" + this.getValue("DR_APPROVE_FLG") + "'";
        value += ",'" + this.getValue("HTL_FLG") + "'";
        value += ",'" + this.getValue("SEQ") + "'";
        value += ",'" + this.getValue("DESCRIPTION") + "'";
        value += ",'" + Operator.getID() + "'";
        value += ",TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",'" + Operator.getIP() + "')";
        return value;
    }

    /**
     * 更新床位属性
     * @return String
     */
    public String updateBedSql() {
        String value =
            "UPDATE SYS_BED SET ";
        value += "BED_NO_DESC='" + this.getValue("BED_NO_DESC") + "'";
        value += ",PY1='" + this.getValue("BEDPY1") + "'";
        value += ",BED_CLASS_CODE='" + this.getValue("BED_CLASS_CODE") + "'";
        value += ",BED_TYPE_CODE='" + this.getValue("BED_TYPE_CODE") + "'";
        value += ",ACTIVE_FLG='" + this.getValue("ACTIVE_FLG") + "'";
        value += ",APPT_FLG='" + this.getValue("APPT_FLG") + "'";
        value += ",ALLO_FLG='" + this.getValue("ALLO_FLG") + "'";
        value += ",BED_OCCU_FLG='" + this.getValue("BED_OCCU_FLG") + "'";
        value += ",RESERVE_BED_FLG='" + this.getValue("RESERVE_BED_FLG") + "'";
        value += ",OCCU_RATE_FLG='" + this.getValue("OCCU_RATE_FLG") + "'";
        value += ",DR_APPROVE_FLG='" + this.getValue("DR_APPROVE_FLG") + "'";
        value += ",HTL_FLG='" + this.getValue("HTL_FLG") + "'";
        value += ",SEQ='" + this.getValueInt("BEDSEQ") + "'";
        value += ",DESCRIPTION='" + this.getValue("DESCRIPTION") + "'";
        value += ",OPT_USER='" + Operator.getID() + "'";
        value += ",OPT_DATE=TO_DATE('" +
            StringTool.getString(SystemTool.getInstance().getDate(),
                                 "yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
        value += ",OPT_TERM='" + Operator.getIP() + "'" +
            " WHERE BED_NO='" + this.getValue("BED_NO") + "'"; ;
        return value;

    }

    /**
     * 删除护士站
     */
    public void deleteStation() {
        if (!message("连带删除其房间,及房间内的床位,是否继续!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("Room")) {
            messageBox_("请选择护士站!");
            return;
        }
        String stationCode = getValueString("STATION_CODE");
        if (stationCode == null || stationCode.length() == 0) {
            messageBox("护士站代码不能为空!");
            return;
        }
        else {
            String sql = BILSQL.getStationSql(stationCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("护士站不存在!");
                return;
            }
        }
        //放入删除sql
        TParm parm = new TParm();
        //删除护士站的sql
        String deleteStationSql = "DELETE SYS_STATION WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETESTATION", deleteStationSql);
        //删除护士站下面的房间sql
        String deleteRoomSql = "DELETE SYS_ROOM WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETEROOM", deleteRoomSql);
        //删除护士站下面的床位sql
        String deleteBedSql = "DELETE SYS_BED WHERE STATION_CODE='" +
            stationCode + "'";
        parm.setData("DELETEBED", deleteBedSql);
        //调用后台action
        TParm saveParm = new TParm();
        saveParm.setData("DELETE", parm.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSStationAction", "deleteStation", saveParm);
        if (saveMessage(result)) {
            updateTree("", stationCode);
            this.clearStation();
        }
    }

    /**
     * 删除房间
     */
    public void deleteRoom() {
        if (!message("连带删除房间中的床位,是否继续!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("Bed")) {
            messageBox_("请选择房间!");
            return;
        }
        String roomCode = getValueString("ROOM_CODE");
        if (roomCode == null || roomCode.length() == 0) {
            messageBox("房间号不能为空!");
            return;
        }
        else {
            String sql = BILSQL.getRoomSql(roomCode);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("房间不存在!");
                return;
            }
        }

        //放入删除sql
        TParm parm = new TParm();
        //删除房间
        String deleteRoomSql = "DELETE SYS_ROOM WHERE ROOM_CODE='" + roomCode +
            "'";
        parm.setData("DELETEROOM", deleteRoomSql);
        //删除病床
        String deleteBedSql = "DELETE SYS_BED WHERE ROOM_CODE='" + roomCode +
            "'";
        parm.setData("DELETEBED", deleteBedSql);
        //调用后台action
        TParm saveParm = new TParm();
        saveParm.setData("DELETE", parm.getData());

        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSStationAction", "deleteRoom", saveParm);
        if (saveMessage(result)) {
            updateTree(getValueString("STATION_CODE"), roomCode);
            this.clearRoom();
        }
    }

    /**
     * 删除刷新树
     * @param parentId String
     * @param id String
     */
    public void updateTree(String parentId, String id) {
        TTreeNode deleteNode = treeRoot.findNodeForID(parentId);
        if (deleteNode != null)
            deleteNode.remove(deleteNode.findChildNodeForID(id));
        tree.update();
    }

    /**
     * 新增节点插入树上节点
     * @param parentId String
     * @param name String
     * @param type String
     * @param id String
     */
    public void insertTreeNode(String parentId, String name, String type,
                               String id) {
        TTreeNode node = treeRoot.findNodeForID(parentId);
        TTreeNode nodeNew = new TTreeNode(name, type);
        nodeNew.setText(name);
        nodeNew.setID(id);
        node.add(nodeNew);
        tree.update();
    }

    /**
     * 更新树上的名称
     * @param id String
     * @param desc String
     */
    public void changeDesc(String id, String desc) {
        TTreeNode node = treeRoot.findNodeForID(id);
        node.setText(desc);
        tree.update();
    }

    /**
     * 删除床位
     */
    public void onDeleteBed() {
        if (!message("建议停用,确认删除!"))
            return;
        String type = getTreeSelectNodeType();
        if (!type.equals("BEDSEQ")) {
            messageBox_("请选择床位!");
            return;
        }
        String bedNo = getValueString("BED_NO");
        if (bedNo == null || bedNo.length() == 0) {
            messageBox("床位号不能为空!");
            return;
        }
        else {
            String sql = BILSQL.getBedSql(bedNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0 || result.getCount() == 0) {
                messageBox("床位不存在!");
                return;
            }
        }
        String deleteBedSql = "DELETE SYS_BED WHERE BED_NO='" + bedNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(deleteBedSql));
        if (saveMessage(result)) {
            updateTree(getValueString("ROOM_CODE"), bedNo);
            this.clearBed();
        }
        return;
    }

    /**
     * 提示信息
     * @param inMessage String
     * @return boolean
     */
    public boolean message(String inMessage) {
        switch (this.messageBox("提示信息",
                                inMessage, this.YES_NO_OPTION)) {
            //保存
            case 0:
                return true;
                //不保存
            case 1:
                return false;
        }
        //没有变更的数据
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
