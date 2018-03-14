package com.javahis.ui.clp;

import com.dongyang.control.*;
import jdo.clp.ChkItemMainTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTreeEvent;
import java.awt.Component;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPChkItemMainControl extends TControl {
    public CLPChkItemMainControl() {
    }

    /** 病人基本信息 */
    private TTextFormat DEPT_CODE; // 科室
    private TTextFormat STATION_CODE; // 病区
    private TTextFormat BED_NO; // 床号
    private TTextField IPD_NO; // ID号
    private TTextField MR_NO; // 病案号
    private TTextField PAT_NAME; // 姓名
    private TComboBox SEX_CODE; // 性别
    private TTextFormat VS_DR_CODE; // 经治医师
    private TTextFormat CLNCPATH_CODE; // 临床路径
    private TTextField STAYHOSP_DAYS; // 预期住院天数
    private TTextField AVERAGECOST; // 预期住院费用
    private TTextFormat IN_DATE; // 入院日期
    private TTextFormat DS_DATE; // 出院日期
    private TCheckBox EXECUTE;
    /** 隐藏控件 */
    private TTextField CASE_NO;
    private TTextField SCHD_DAY;
    /** 治疗时程树形结构 */
    private TTree TREE; // 治疗时程树形结构
    /** 查询条件 */
    // 必执
    private TCheckBox EXEC_FLG;
    // 执行人员
    private TTextFormat CHKUSER_CODE;
    // 进度状态
    private TRadioButton PROGRESS_CODE1;
    private TRadioButton PROGRESS_CODE2;
    private TRadioButton PROGRESS_CODE3;
    /** CLP_MANAGERD */
    private TTable CLP_MANAGERD; // 实际与标准汇总对比
    /** 声明日期格式化类对象 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    //就诊序号
    private String case_no;
    //临床路径
    private String clncPathCode;
    /**
     * 初始化方法
     * 初始化查询全部
     */
    public void onInit() {
        super.onInit();
        initInParm();
        // 获取全部输入框控件
        getAllComponent();
        /** 测试数据 */
        TParm parm = new TParm();
        // 根据CASE_NO取出病患信息和临床路径
        parm.setData("CASE_NO",this.case_no);
        TParm result = ChkItemMainTool.getInstance().queryPatientInfo(parm).
                       getRow(0);
        // 初始化治疗时程树形结构
        initTree(result);
        // 注册监听事件
        initControler();
        // 设置到界面上的控件
        this.setValueForParm("DEPT_CODE;STATION_CODE;BED_NO;IPD_NO;MR_NO;PAT_NAME;SEX_CODE;VS_DR_CODE;CLNCPATH_CODE;STAYHOSP_DAYS;AVERAGECOST;IN_DATE;DS_DATE;CASE_NO",
                             result);
        STAYHOSP_DAYS.setValue(result.getValue("STAYHOSP_DAYS"));
        AVERAGECOST.setValue(result.getValue("AVERAGECOST"));
    }
    /**
     * 初始化传入参数
     */
    private void initInParm(){
        TParm inParm=(TParm)this.getParameter();
        case_no=inParm.getValue("CLP","CASE_NO");
        clncPathCode=inParm.getValue("CLP","CLNCPATH_CODE");
    }
    /**
     * 获取全部输入框控件
     */
    public void getAllComponent() {
//        // 病人基本信息
//        DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
//        DEPT_CODE.setEnabled(false);
//        STATION_CODE = (TTextFormat)this.getComponent("STATION_CODE");
//        STATION_CODE.setEnabled(false);
//        BED_NO = (TTextFormat)this.getComponent("BED_NO");
//        BED_NO.setEnabled(false);
//        IPD_NO = (TTextField)this.getComponent("IPD_NO");
//        IPD_NO.setEditable(false);
//        MR_NO = (TTextField)this.getComponent("MR_NO");
//        MR_NO.setEditable(false);
//        PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
//        PAT_NAME.setEditable(false);
//        SEX_CODE = (TComboBox)this.getComponent("SEX_CODE");
//        SEX_CODE.setEnabled(false);
//        VS_DR_CODE = (TTextFormat)this.getComponent("VS_DR_CODE");
//        VS_DR_CODE.setEnabled(false);
//        CLNCPATH_CODE = (TTextFormat)this.getComponent("CLNCPATH_CODE");
//        CLNCPATH_CODE.setEnabled(false);
//        STAYHOSP_DAYS = (TTextField)this.getComponent("STAYHOSP_DAYS");
//        STAYHOSP_DAYS.setEditable(false);
//        AVERAGECOST = (TTextField)this.getComponent("AVERAGECOST");
//        AVERAGECOST.setEditable(false);
//        IN_DATE = (TTextFormat)this.getComponent("IN_DATE");
//        IN_DATE.setEnabled(false);
//        DS_DATE = (TTextFormat)this.getComponent("DS_DATE");
//        DS_DATE.setEnabled(false);
        EXECUTE = (TCheckBox)this.getComponent("EXECUTE");
        // 查询条件
        EXEC_FLG = (TCheckBox)this.getComponent("EXEC_FLG");
        CHKUSER_CODE = (TTextFormat)this.getComponent("CHKUSER_CODE");
        PROGRESS_CODE1 = (TRadioButton)this.getComponent("PROGRESS_CODE1");
        PROGRESS_CODE2 = (TRadioButton)this.getComponent("PROGRESS_CODE2");
        PROGRESS_CODE3 = (TRadioButton)this.getComponent("PROGRESS_CODE3");

        // 隐藏控件
//        CASE_NO = (TTextField)this.getComponent("CASE_NO");
        SCHD_DAY = (TTextField)this.getComponent("SCHD_DAY");
        // 治疗时程树形结构
        TREE = (TTree)this.getComponent("TREE");
        // 实际与标准汇总对比
        CLP_MANAGERD = (TTable)this.getComponent("CLP_MANAGERD");
    }

    /**
     * 注册监听事件
     */
    public void initControler() {
        // 表格点击事件监听
        callFunction("UI|CLP_MANAGERD|addEventListener",
                     "CLP_MANAGERD->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        // 给TREE添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // 实际项目列监听事件
        CLP_MANAGERD.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                      "onCreateEditComoponent");
    }

    /**
     * 初始化树形结构
     * @param parm TParm
     */
    private void initTree(TParm parm) {
        // 根据CLNCPATH_CODE查询时程
        TParm result = ChkItemMainTool.getInstance().queryByClncPathCode(parm);
        TTreeNode root = (TTreeNode) TREE.getRoot();
        root.setText("治疗时程");
        root.setType("Root");
        root.setID("");
        root.removeAllChildren();
        // 初始化节点
        initNode(result, root);
        TREE.update();
    }

    private void initNode(TParm result, TTreeNode root) {
        //开始初始化树的数据
        for (int i = 0; i < result.getCount(); i++) {
            TParm rowParm = result.getRow(i);
            putNodeInTree(rowParm, root);
        }
    }

    /**
     * 将节点放入树形结构
     * @param rowParm TParm
     * @param root TTreeNode
     */
    private void putNodeInTree(TParm rowParm, TTreeNode root) {
        TTreeNode treeNode = new TTreeNode("KPILEAVE", "Path");
        treeNode.setText(rowParm.getValue("DURATION_CHN_DESC"));
        treeNode.setID(rowParm.getValue("DURATION_CODE"));
        String parentID = rowParm.getValue("PARENT_CODE");
        if (root.findNodeForID(rowParm.getValue("DURATION_CODE")) != null) {

        } else if (root.findNodeForID(parentID) != null) {
            root.findNodeForID(parentID).add(treeNode);
        } else {
            TParm parm = new TParm();
            parm.setData("DURATION_CODE", parentID);
            // 根据DURATION_CODE和PARENT_CODE查询时程
            TParm result = ChkItemMainTool.getInstance().queryByDurationCode(
                    parm);
            if (result.getCount() <= 0) {
                root.add(treeNode);
            } else {
                putNodeInTree(result.getRow(0), root);
                root.findNodeForID(parentID).add(treeNode);
            }
        }
    }

    /**
     * CLP_MANAGERD点击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        int column = CLP_MANAGERD.getSelectedColumn();
        String orderSeq = CLP_MANAGERD.getParmValue().getValue("ORDER_SEQ", row);
        String orderChnDesc = CLP_MANAGERD.getParmValue().getValue(
                "ORDER_CHN_DESC", row);
        // 实际项目（关键诊疗项目）
        // 原有数据
        if (checkInputString(orderSeq)) {
            if (column == 0 || column == 7 || column == 8 || column == 9) {
                setTableEnabled(CLP_MANAGERD, row, column);
            } else {
                return;
            }
        }
        // 新增数据
        else {
            if (!checkInputString(orderChnDesc)) {
                if (column == 4) {
                    setTableEnabled(CLP_MANAGERD, row, column);
                } else {
                    return;
                }
            } else {
                if (column == 0) {
                    return;
                } else {
                    setTableEnabled(CLP_MANAGERD, row, column);
                }
            }

        }
        // 执行列和进度状态列联动
        resetProgress(CLP_MANAGERD, row, column);
    }

    public TParm treeDataFormat(TTreeNode node) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", this.case_no);
        parm.setData("CLNCPATH_CODE", this.clncPathCode);
        parm.setData("SCHD_CODE", node.getID());
        parm.setData("ORDER_FLG", "Y");
        return parm;
    }

    /**
     * 树形结构点击事件
     */
    public void onTreeClicked(Object obj) {
        TTreeNode node = (TTreeNode) obj;
        TParm parm = treeDataFormat(node);
        if ("Y".equals(EXEC_FLG.getValue())) {
            parm.setData("EXEC_FLG", EXEC_FLG.getValue());
        }
        parm.setData("CHKUSER_CODE", CHKUSER_CODE.getValue());
        if (PROGRESS_CODE1.isSelected()) {
            parm.setData("PROGRESS_CODE", "");
        }
        if (PROGRESS_CODE2.isSelected()) {
            parm.setData("PROGRESS_CODE", "A");
        }
        if (PROGRESS_CODE3.isSelected()) {
            parm.setData("PROGRESS_CODE", "B");
        }
        TParm result = ChkItemMainTool.getInstance().queryManagerDBySchdCode(
                parm);
        CLP_MANAGERD.setParmValue(result, CLP_MANAGERD.getParmMap());
        // 设置时程天数
        setSchdDay(node.getID());
        insertNewRow(CLP_MANAGERD);
        //
        EXECUTE.setSelected(false);
    }

    /**
     * 实际项目 关键诊疗项目
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com, int row, int column) {
        int rowCount = CLP_MANAGERD.getRowCount();
        if (row < 0) {
            return;
        }
        if (column == 4) {
            // 实际项目列触发（关键诊疗项目）
            if (!(com instanceof TTextField)) {
                return;
            }
            TTextField textFilter = (TTextField) com;
            textFilter.onInit();
            // 关键诊疗项目
            TParm parm = new TParm();
            parm.setData("ORDER_FLG", "Y");
            this.putBasicSysInfoIntoParm(parm);
            textFilter.setPopupMenuParameter("IG",
                                             getConfigParm().newConfig(
                    "%ROOT%\\config\\clp\\CLPChkItemPopup.x"),
                                             parm);
            //定义接受返回值方法
            textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                        "popReturn");
        }
    }

    /**
     * 关键诊疗项目信息
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        // 判断对象是否为空和是否为TParm类型
        if (obj == null && !(obj instanceof TParm)) {
            return;
        }
        // 类型转换成TParm
        TParm parm = (TParm) obj;
        // 带回关键诊疗项目信息
        int row = CLP_MANAGERD.getSelectedRow();
        setManagerD(CLP_MANAGERD, parm, row);
    }

    /**
     * 设置列表指定单元格可编辑
     * @param table TTable
     * @param row int
     * @param column int
     */
    public void setTableEnabled(TTable table, int row, int column) {
        int rowCount = table.getRowCount();
        String lockRows = "";
        int columnCount = table.getColumnCount();
        String lockColumns = "";
        if (row < 0 || row >= rowCount) {
            return;
        }
        if (column < 0 || column >= columnCount) {
            return;
        }
        for (int i = 0; i < rowCount; i++) {
            if (i != row) {
                lockRows = lockRows + i + ",";
            }
        }
        for (int i = 0; i < columnCount; i++) {
            if (i != column) {
                lockColumns = lockColumns + i + ",";
            }
        }
        if (lockRows.endsWith(",")) {
            lockRows = lockRows.substring(0, lockRows.length() - 1);
        }
        table.setLockRows(lockRows);
        if (lockColumns.endsWith(",")) {
            lockColumns = lockColumns.substring(0, lockColumns.length() - 1);
        }
        table.setLockColumns(lockColumns);
    }

    /**
     * 字符串非空验证
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(String str) {
        if (str == null) {
            return false;
        } else if ("".equals(str.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 新增一行
     * @param table TTable
     */
    public void insertNewRow(TTable table) {
        String tableTagName = table.getTag();
        int rowID = table.addRow();
//        table.setItem(rowID, "MAINTOT", "1.0");
    }

    /**
     * EXECUTE执行列和进度状态联动
     * @param table TTable
     * @param row int
     * @param column int
     */
    public void resetProgress(TTable table, int row, int column) {
        // 执行列触发
        if (column == 7) {
            String execute = String.valueOf(table.getItemData(row,
                    column));
            if ("Y".equals(execute)) {
                table.setItem(row, "EXECUTE", "N");
                table.setItem(row, "PROGRESS_CODE", "");
            }
            if ("N".equals(execute)) {
                table.setItem(row, "EXECUTE", "Y");
                table.setItem(row, "PROGRESS_CODE", "A01");
            }
        }
    }

    /**
     * 带回关键诊疗项目信息
     * @param parm TParm
     */
    public void setManagerD(TTable table, TParm parm, int row) {
        table.acceptText();
        TParm result = new TParm();
        // 选择
        result.setData("SEL_FLG", "N");
        // 必执
        result.setData("EXEC_FLG", "N");
        // 执行人
        result.setData("CHKUSER_CODE", "");
        // 类别
        result.setData("CHKTYPE_CODE", parm.getValue("CHKTYPE_CODE"));
        // 实际项目
        result.setData("ORDER_CHN_DESC", parm.getValue("ORDER_CHN_DESC"));
        // 标总量
        //卢海修改
        result.setData("MAINTOT", parm.getValue("CLP_QTY"));
        // 单位
        result.setData("MAINDISPENSE_UNIT", parm.getValue("CLP_UNIT"));
        // 执行
        result.setData("EXECUTE", "N");
        // 进度状态
        result.setData("PROGRESS_CODE", "");
        // 备注
        result.setData("MANAGE_NOTE", "");
        /** 隐藏信息 */
        //
        result.setData("CASE_NO", this.case_no);
        //
        result.setData("CLNCPATH_CODE", this.clncPathCode);
        // 时程代码
        result.setData("SCHD_CODE", TREE.getSelectNode().getID());
        result.setData("ORDER_NO", "");
        //
        result.setData("ORDER_SEQ", "");
        //
        result.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        // 时程天数
        result.setData("SCHD_DAY", SCHD_DAY.getValue());
        /** 隐藏信息 */
        TParm data = table.getParmValue();
        data.setRowData(row, result);
        table.setParmValue(data, table.getParmMap());
        if (row >= table.getRowCount() - 1) {
            insertNewRow(table);
        }
    }

    public void setSchdDay(String schdCode) {
        String sql =
                " SELECT SCHD_DAY FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE = '"
                + this.clncPathCode + "'"
                + " AND SCHD_CODE = '" + schdCode + "'";
        TParm tmpParm = new TParm(TJDODBTool.getInstance().select(sql));
        SCHD_DAY.setValue(tmpParm.getValue("SCHD_DAY", 0));
    }

    public void onSelect() {
        if ("Y".equals(EXECUTE.getValue())) {
            int count = CLP_MANAGERD.getRowCount();
            for (int i = 0; i < count - 1; i++) {
                CLP_MANAGERD.setItem(i, 7, "N");
                resetProgress(CLP_MANAGERD, i, 7);
            }
        }
        if ("N".equals(EXECUTE.getValue())) {
            int count = CLP_MANAGERD.getRowCount();
            for (int i = 0; i < count - 1; i++) {
                CLP_MANAGERD.setItem(i, "EXECUTE", "Y");
                resetProgress(CLP_MANAGERD, i, 7);
            }
        }
    }

    public void onQuery() {
        onTreeClicked(TREE.getSelectNode());
    }

    public void onClear() {
        EXEC_FLG.setSelected(false);
        CHKUSER_CODE.setValue("");
        PROGRESS_CODE1.setSelected(true);
        EXECUTE.setSelected(false);
    }

    public boolean check(TParm data) {
        int count = data.getCount("CASE_NO");
        for (int i = 0; i < count - 1; i++) {
            String chkUserCode = data.getValue("CHKUSER_CODE", i);
            if (!checkInputString(chkUserCode)) {
                this.messageBox("执行人不能为空！");
                return false;
            }
            String chkTypeCode = data.getValue("CHKTYPE_CODE", i);
            if (!checkInputString(chkTypeCode)) {
                this.messageBox("类别不能为空！");
                return false;
            }
            String orderChnDesc = data.getValue("ORDER_CHN_DESC", i);
            if (!checkInputString(orderChnDesc)) {
                this.messageBox("实际项目不能为空！");
                return false;
            }
            String mainTot = data.getValue("MAINTOT", i);
            if (!checkInputString(mainTot)) {
                this.messageBox("标总量不能为空！");
                return false;
            }
//            String mainDispenseUnit = data.getValue("MAINDISPENSE_UNIT", i);
//            if (!checkInputString(mainDispenseUnit)) {
//                this.messageBox("单位不能为空！");
//                return false;
//            }
        }
        return true;
    }

    public void checkData(TParm parm, int i) {
        if (!checkInputString(parm.getValue("CASE_NO", i))) {
            parm.setData("CASE_NO", i, "");
        }
        if (!checkInputString(parm.getValue("CLNCPATH_CODE", i))) {
            parm.setData("CLNCPATH_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("SCHD_CODE", i))) {
            parm.setData("SCHD_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_NO", i))) {
            parm.setData("ORDER_NO", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_SEQ", i))) {
            parm.setData("ORDER_SEQ", i, "");
        }
        if (!checkInputString(parm.getValue("REGION_CODE", i))) {
            parm.setData("REGION_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_CODE", i))) {
            parm.setData("ORDER_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("CHKTYPE_CODE", i))) {
            parm.setData("CHKTYPE_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("START_DAY", i))) {
            parm.setData("START_DAY", i, "");
        }
        if (!checkInputString(parm.getValue("STANDING_DTTM", i))) {
            parm.setData("STANDING_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("CHKUSER_CODE", i))) {
            parm.setData("CHKUSER_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("EXEC_FLG", i))) {
            parm.setData("EXEC_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("TOT", i))) {
            parm.setData("TOT", i, "");
        }
        if (!checkInputString(parm.getValue("DISPENSE_UNIT", i))) {
            parm.setData("DISPENSE_UNIT", i, "");
        }
        if (!checkInputString(parm.getValue("STANDARD", i))) {
            parm.setData("STANDARD", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_FLG", i))) {
            parm.setData("ORDER_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("SCHD_DESC", i))) {
            parm.setData("SCHD_DESC", i, "");
        }
        if (!checkInputString(parm.getValue("CHANGE_FLG", i))) {
            parm.setData("CHANGE_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("STANDARD_FLG", i))) {
            parm.setData("STANDARD_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("MAINORD_CODE", i))) {
            parm.setData("MAINORD_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("MAINTOT", i))) {
            parm.setData("MAINTOT", i, "");
        }
        if (!checkInputString(parm.getValue("MAINDISPENSE_UNIT", i))) {
            parm.setData("MAINDISPENSE_UNIT", i, "");
        }
        if (!checkInputString(parm.getValue("CFM_DTTM", i))) {
            parm.setData("CFM_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("CFM_USER", i))) {
            parm.setData("CFM_USER", i, "");
        }
        if (!checkInputString(parm.getValue("PROGRESS_CODE", i))) {
            parm.setData("PROGRESS_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_MONCAT", i))) {
            parm.setData("MEDICAL_MONCAT", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_VARIANCE", i))) {
            parm.setData("MEDICAL_VARIANCE", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_NOTE", i))) {
            parm.setData("MEDICAL_NOTE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_MONCAT", i))) {
            parm.setData("MANAGE_MONCAT", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_VARIANCE", i))) {
            parm.setData("MANAGE_VARIANCE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_NOTE", i))) {
            parm.setData("MANAGE_NOTE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_DTTM", i))) {
            parm.setData("MANAGE_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_USER", i))) {
            parm.setData("MANAGE_USER", i, "");
        }
        if (!checkInputString(parm.getValue("R_DEPT_CODE", i))) {
            parm.setData("R_DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("R_USER", i))) {
            parm.setData("R_USER", i, "");
        }
        if (!checkInputString(parm.getValue("TOT_AMT", i))) {
            parm.setData("TOT_AMT", i, "");
        }
        if (!checkInputString(parm.getValue("MAIN_AMT", i))) {
            parm.setData("MAIN_AMT", i, "");
        }
        if (!checkInputString(parm.getValue("MAINCFM_USER", i))) {
            parm.setData("MAINCFM_USER", i, "");
        }
        if (!checkInputString(parm.getValue("ORDTYPE_CODE", i))) {
            parm.setData("ORDTYPE_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("DEPT_CODE", i))) {
            parm.setData("DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("EXE_DEPT_CODE", i))) {
            parm.setData("EXE_DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_USER", i))) {
            parm.setData("OPT_USER", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_DATE", i))) {
            parm.setData("OPT_DATE", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_TERM", i))) {
            parm.setData("OPT_TERM", i, "");
        }
    }

    public TParm dataFormat(TParm data) {
        int rowCount = CLP_MANAGERD.getRowCount();
        for (int i = 0; i < rowCount - 1; i++) {
            if (!checkInputString(data.getValue("ORDER_NO", i))) {
                // 操作标识 插入
                data.setData("OPT_FLG", i, "I");
                // 医嘱序号
                data.setData("ORDER_NO", i,
                             SystemTool.getInstance().getNo("ALL", "CLP", "CLP",
                        "ORDERNO"));
                // 循环序号
                data.setData("ORDER_SEQ", i, i);
                // 标准差
                data.setData("STANDARD", i, "0");
                // 标准注记
                data.setData("STANDARD_FLG", i, "N");
                // 异常状态注记
                data.setData("CHANGE_FLG", i, "N");
                // 判断是否为医嘱
                data.setData("ORDER_FLG", i, "N");
                // 展开日期
                data.setData("STANDING_DTTM", i,
                             dateFormat.format(SystemTool.getInstance().getDate()));

                // 区域
                data.setData("REGION_CODE", i, Operator.getRegion());

                // 标准项目->实际项目
                data.setData("MAINORD_CODE", i, data.getValue("ORDER_CODE", i));
                // 实际总量->标准总量
                data.setData("TOT", i, data.getValue("MAINTOT", i));
            } else {
                // 操作标识 更新
                data.setData("OPT_FLG", i, "U");
            }
            // 操作人员
            data.setData("OPT_USER", i, Operator.getID());
            // 操作时间
            data.setData("OPT_DATE", i,
                         dateFormat.format(SystemTool.getInstance().getDate()));
            // 操作终端
            data.setData("OPT_TERM", i, Operator.getIP());
            // 数据检查
            checkData(data, i);
        }
        return data;
    }

    public void onSave() {
        CLP_MANAGERD.acceptText();
        TParm data = CLP_MANAGERD.getParmValue();
        if (!check(data)) {
            return;
        }
        if (this.messageBox("询问", "是否覆盖原有数据？", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPChkItemMainAction", "saveManagerD",
                dataFormat(data));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("保存失败！");
            return;
        } else {
            this.messageBox("保存成功！");
            this.onTreeClicked(TREE.getSelectNode());
            // 最后一行选中状态
            if (CLP_MANAGERD.getRowCount() > 0) {
                CLP_MANAGERD.setSelectedRow(CLP_MANAGERD.getRowCount() - 1);
            }
            return;
        }

    }

    public void onDelete() {
        CLP_MANAGERD.acceptText();
        TParm parm = new TParm();
        TParm data = CLP_MANAGERD.getParmValue();
        for (int i = 0; i < CLP_MANAGERD.getRowCount() - 1; i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                parm.addData("CASE_NO", this.case_no);
                parm.addData("CLNCPATH_CODE",this.clncPathCode);
                parm.addData("SCHD_CODE", TREE.getSelectNode().getID());
                parm.addData("ORDER_NO", data.getValue("ORDER_NO", i));
                parm.addData("ORDER_SEQ", data.getValue("ORDER_SEQ", i));
            }
        }
        if (parm.getCount("CASE_NO") <= 0) {
            return;
        }
        if (this.messageBox("询问", "是否删除？", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPChkItemMainAction", "deleteManagerD", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("删除失败！");
            return;
        } else {
            this.messageBox("删除成功！");
            this.onTreeClicked(TREE.getSelectNode());
            // 最后一行选中状态
            if (CLP_MANAGERD.getRowCount() > 0) {
                CLP_MANAGERD.setSelectedRow(CLP_MANAGERD.getRowCount() - 1);
            }
            return;
        }
    }

    /**
     * 临床路径模板
     */
    public void onPreview() {
        TTreeNode node = TREE.getSelectNode();
        if (node == null || !checkInputString(node.getID())) {
            this.messageBox("请选择时程！");
            return;
        }
        TParm parm = treeDataFormat(node);
        parm.setData("SCHD_DAY", SCHD_DAY.getValue());
        Object result = openDialog("%ROOT%\\config\\clp\\CLPChkItemMainPopup.x",
                                   parm);
        if (result == null) {
            return;
        }
        int start = CLP_MANAGERD.getRowCount() - 1;
        TParm data = (TParm) result;
        for (int i = 0; i < data.getCount("SEL_FLG"); i++) {
            setManagerD(CLP_MANAGERD, data.getRow(i), start + i);
        }
    }

    /**
     * 处理当前TOOLBAR，该页面使用了公共的病患选择页面
     * 在选择其他菜单再次返回该页面时防止菜单变为病患选择页面,需加入此方法
     */
    public void onShowWindowsFunction() {
        //显示UIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
//        System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
    }

}
