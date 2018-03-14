package com.javahis.ui.ctr;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.manager.*;
import com.dongyang.ui.*;
import com.dongyang.ui.event.*;
import com.javahis.util.*;
import jdo.ctr.*;
import jdo.sys.*;
import com.dongyang.util.StringTool;

/**
 * <p>Title:医令管控设定 </p>
 *
 * <p>Description: 医令管控设定</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author shibl 2011.11.30
 * @version 1.0
 */
public class CTRMainControl
    extends TControl {

    //主表
    private TTable mtable;
    //细表
    private TTable dtable;
    //细表参数一
    private TTextFormat PARAVALUE;
    /**
     * 初始化
     * onInit
     */
    public void onInit() {
        super.init();
        mtable = this.getTable("MTABLE");
        dtable = this.getTable("DTABLE");
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //textfield接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
        //textfield接受回传值
        callFunction("UI|PARAVALUE_1|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn2");
        //主表单击事件注册
        callFunction("UI| mtable|addEventListener",
                     mtable + "->" + TTableEvent.CLICKED, this,
                     "onMTableClicked");
        //主表双击事件注册
        callFunction("UI| mtable|addEventListener",
                     mtable + "->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onMTabledoubleClicked");
        //细表单击事件注册
        callFunction("UI| dtable|addEventListener",
                     dtable + "->" + TTableEvent.CLICKED, this,
                     "ondtableClicked");
        //值更换事件注册
        callFunction("UI|RESTRITEM_CODE|addEventListener",
                     TComboBoxEvent.SELECTED, this,
                     "CodeSelect");
        //初始化TextFormat
        PARAVALUE = getTextFormat("PARAVALUE");
        getcheckdata(05);
        //页签改变事件
        onChangedPanel();
    }

    /**
     * 页签改变事件<br>
     * onChangedPanel
     */
    public void onChangedPanel() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //主项初始化
        if (index == 0) {
            initpage();

        }
        //细项初始化
        else if (index == 1) {
            initdetail();
        }
    }

    /**
     * 初始化页面
     */
    public void initpage() {
        setUIMainF();
        onQuery();
    }

    /**
     * 对话框空时的方法
     */
    public void onDiagLost() {
        if (this.getValueString("ORDER_CODE").trim().length() <= 0) {
            this.setValue("ORDER_DESC", "");
        }
    }

    /**
     * 医令接受返回值方法
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn1(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
    }

    /**
     * 参数值一接受返回值方法
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("PARAVALUE_1").setValue(order_code);
        }
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("PARAVALUE_1_DESC").setValue(order_desc);
        }
    }

    /**
     * 为单击事件参数值一接受返回值方法
     *
     * @param value String
     * @return String
     */
    public String meddesc(String value) {
        //参数一值不为空、
        String testitem_desc = "";
        if (!value.equals("")) {
            TParm medparm = new TParm(this.getDBTool().select(
                "SELECT DISTINCT TESTITEM_CHN_DESC FROM MED_LIS_RPT WHERE TESTITEM_CODE='" +
                value +
                "'"));
            if (medparm == null || medparm.getCount() < 0) {
                return testitem_desc;
            }
            testitem_desc = medparm.getValue("TESTITEM_CHN_DESC", 0);
        }
        else {
            testitem_desc = "";
        }
        return testitem_desc;
    }

    /**
     * 为单击事件参数值一接受返回值方法
     *
     * @param value String
     * @return String
     */
    public String paravaluedesc(String value) {
        //参数一值不为空、
        String order_desc = "";
        if (!value.equals("")) {
            TParm feeparm = new TParm(this.getDBTool().select(
                "SELECT ORDER_DESC FROM SYS_FEE  WHERE ORDER_CODE='" + value +
                "'"));
            if (feeparm == null || feeparm.getCount() < 0) {
                return order_desc;
            }
            order_desc = feeparm.getValue("ORDER_DESC", 0);
        }
        else {
            order_desc = "";
        }
        return order_desc;
    }

    /**
     * 主页面控件不可编辑
     * setUIMainF
     */
    public void setUIMainF() {
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
        callFunction("UI|CONTROL_ID|setEnabled", false);
        callFunction("UI|NHI_FLG|setEnabled", false);
        callFunction("UI|OWN_FLG|setEnabled", false);
        callFunction("UI|FORCE_FLG|setEnabled", false);
        callFunction("UI|MESSAGE_TEXT|setEnabled", false);
        callFunction("UI|MESSAGE_TEXT_E|setEnabled", false);
        callFunction("UI|CTRL_COMMENT|setEnabled", false);
        callFunction("UI|ACTIVE_FLG|setEnabled", false);
        callFunction("UI|SUBCLASS_CODE|setEnabled", false);
    }

    /**
     * 主页面控件可编辑
     * setUIMainT
     */

    public void setUIMainT() {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
        callFunction("UI|CONTROL_ID|setEnabled", false);
        callFunction("UI|NHI_FLG|setEnabled", true);
        callFunction("UI|OWN_FLG|setEnabled", true);
        callFunction("UI|FORCE_FLG|setEnabled", true);
        callFunction("UI|MESSAGE_TEXT|setEnabled", true);
        callFunction("UI|MESSAGE_TEXT_E|setEnabled", true);
        callFunction("UI|CTRL_COMMENT|setEnabled", true);
        callFunction("UI|ACTIVE_FLG|setEnabled", true);
        callFunction("UI|SUBCLASS_CODE|setEnabled", true);
    }

    /**
     * 细项页面控件不可编辑
     * setUIDMainF
     */

    public void setUIDMainF() {
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|ORDER_CODE_1|setEnabled", false);
        callFunction("UI|ORDER_DESC_1|setEnabled", false);
        callFunction("UI|CONTROL_ID_1|setEnabled", false);
        callFunction("UI|RESTRITEM_CODE|setEnabled", false);
        callFunction("UI|PARAVALUE_1|setEnabled", false);
        callFunction("UI|PARAVALUE|setEnabled", false);
        callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
        callFunction("UI|GROUP_NO|setEnabled", false);
        callFunction("UI|PARADATATYPE_1|setEnabled", false);
        callFunction("UI|PERIOD_CODE|setEnabled", false);
        callFunction("UI|PERIOD_VALUE|setEnabled", false);
        callFunction("UI|LIMIT_TYPE_1|setEnabled", false);
        callFunction("UI|LIMIT_TYPE_2|setEnabled", false);
        callFunction("UI|and|setEnabled", false);
        callFunction("UI|or|setEnabled", false);
        callFunction("UI|none|setEnabled", false);
    }

    /**
     * 细项页面控件可编辑
     * setUIDMainT
     */
    public void setUIDMainT() {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|RESTRITEM_CODE|setEnabled", true);
        callFunction("UI|PARAVALUE_1|setEnabled", true);
        callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
        callFunction("UI|PARAVALUE|setEnabled", true);
        callFunction("UI|GROUP_NO|setEnabled", true);
        callFunction("UI|PERIOD_CODE|setEnabled", true);
        callFunction("UI|PERIOD_VALUE|setEnabled", true);
        callFunction("UI|LIMIT_TYPE_1|setEnabled", true);
        callFunction("UI|LIMIT_TYPE_2|setEnabled", true);
        callFunction("UI|and|setEnabled", true);
        callFunction("UI|or|setEnabled", true);
        callFunction("UI|none|setEnabled", true);
    }


    /**
     * 保存数据<br>
     * onSave
     */
    public void onSave() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        TParm result = new TParm();
        //细项设定保存方法
        if (index == 1) {
            //检验数据输入
            if (!checkData()) {
                return;
            }
            String restritem = this.getValueString("RESTRITEM_CODE");
            int restritemcode = Integer.parseInt(restritem);
            TParm parm = new TParm();
            //参数一限制判断
            if ("Y".equals(this.getValueString("LIMIT_TYPE_1"))) {
                parm.setData("LIMIT_TYPE", "1");
            }
            else if ("Y".equals(this.getValueString("LIMIT_TYPE_2"))) {
                parm.setData("LIMIT_TYPE", "2");
            }
            else {
                parm.setData("LIMIT_TYPE", "");
            }
            //逻辑判断
            if ("Y".equals(this.getValueString("and"))) {
                parm.setData("LOGICAL_TYPE", "1");
            }
            else if ("Y".equals(this.getValueString("or"))) {
                parm.setData("LOGICAL_TYPE", "2");
            }
            else if ("Y".equals(this.getValueString("none"))) {
                parm.setData("LOGICAL_TYPE", "3");
            }
            else {
                parm.setData("LOGICAL_TYPE", "");
            }
            //参数一值的控件选择
            if (restritemcode == 5) {
                parm.setData("PARAVALUE_1", this.getValueString("PARAVALUE"));

            }
            else {
                parm.setData("PARAVALUE_1", this.getValueString("PARAVALUE_1"));
            }
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE_1"));
            parm.setData("CONTROL_ID", this.getValueInt("CONTROL_ID_1"));
            parm.setData("SERIAL_NO", this.getValueInt("SERIAL_NO"));
            parm.setData("GROUP_NO", this.getValueInt(" GROUP_NO"));
            parm.setData("RESTRITEM_CODE", this.getValueString("RESTRITEM_CODE"));
            parm.setData("START_VALUE", this.getValueString("START_VALUE"));
            parm.setData("END_VALUE", this.getValueString("END_VALUE"));
            parm.setData("PARADATATYPE_1", this.getValueString("PARADATATYPE_1"));
            parm.setData("PERIOD_CODE", this.getValueString("PERIOD_CODE"));
            parm.setData("PERIOD_VALUE", this.getValueInt("PERIOD_VALUE"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            result = CTRMainTool.getNewInstance().onDQuery(
                parm);
            int row = dtable.getClickedRow();
            TParm tableparm = dtable.getParmValue();
            if (result.getCount() <= 0) {
                //细项表的第一条数据
                if (logicalstr().equals("")) {
                    // 新增数据
                    result = CTRMainTool.getNewInstance().onDInsert(parm);
                }
                else {
                    //最后一条数据的逻辑条件为NONE
                    if (logicalstr().equals("3")) {
                        this.messageBox("最后一条数据的逻辑条件不能为NONE!");
                        return;
                    }
                    else {
                        result = CTRMainTool.getNewInstance().onDInsert(parm);
                    }
                }
            }
            else {
                //检验逻辑条件
                int serialno = tableparm.getInt("SERIAL_NO", row);
                if (serialno < (MaxSeq() - 1)) {
                    if (parm.getValue("LOGICAL_TYPE").equals("3")) {
                        this.messageBox("逻辑条件不能为NONE！");
                        return;
                    }
                }
                // 更新数据
                result = CTRMainTool.getNewInstance().onDUpdate(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                onQuery();
            }
        }
        else if (index == 0) {
            //主项设定保存方法
            //检验数据输入
            if (!checkData()) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
            parm.setData("CONTROL_ID", this.getValueInt("CONTROL_ID"));
            parm.setData("NHI_FLG", this.getValueString("NHI_FLG"));
            parm.setData("OWN_FLG", this.getValueString("OWN_FLG"));
            parm.setData("FORCE_FLG", this.getValueString("FORCE_FLG"));
            parm.setData("MESSAGE_TEXT", this.getValueString("MESSAGE_TEXT"));
            parm.setData("MESSAGE_TEXT_E",
                         this.getValueString("MESSAGE_TEXT_E"));
            parm.setData("CTRL_COMMENT", this.getValueString("CTRL_COMMENT"));
            parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
            parm.setData("SUBCLASS_CODE", this.getValueString("SUBCLASS_CODE"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            result = CTRMainTool.getNewInstance().onMQuery(
                parm);
            if (result.getCount() <= 0) {
                // 新增数据
                result = TIOM_AppServer.executeAction(
                    "action.ctr.CTRMainAction",
                    "InsertCTRMaincode", parm);

            }
            else {
                // 更新数据
                result = CTRMainTool.getNewInstance().onMUpdate(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                //刷新
                onQuery();
            }

        }
    }

    /**
     * 单击主项表数据事件<br>
     *
     */
    public void onMTableClicked() {
        if (mtable.getSelectedRow() < 0) {
            this.messageBox("没有选中数据!");
        }
        int row = mtable.getClickedRow();
        TParm tableParm = mtable.getParmValue();
        TParm parm = tableParm.getRow(row);
        TParm mresult = CTRMainTool.getNewInstance().onMQuery(parm);
        if (mresult.getCount() < 0) {
            this.messageBox("E0116");
        }
        setValueForParm(
            "ORDER_CODE;ORDER_DESC;CONTROL_ID;NHI_FLG;OWN_FLG;FORCE_FLG;ACTIVE_FLG;" +
            "MESSAGE_TEXT;MESSAGE_TEXT_E;CTRL_COMMENT;SUBCLASS_CODE", mresult, 0);
        setUIMainT();

    }

    /**
     * onAdd
     *增加
     */
    public void onAdd() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //主项管控的新增
        if (index == 0) {
            onMClear();
            setUIMainT();
            callFunction("UI|ORDER_CODE|setEnabled", true);
            callFunction("UI|CONTROL_ID|setEnabled", true);
        }
        //细项管控新增
        else if (index == 1) {
            onDClear();
            setUIDMainT();
            getTextField("SERIAL_NO").setValue(MaxSeq() + "");
        }
    }

    /**
     * onDelete
     * 删除方法
     */
    public void onDelete()

    {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //主项设定删除
        if (index == 0) {
            int row = mtable.getSelectedRow();
            if (row < 0) {
                this.messageBox("请选择删除数据！");
                return;
            }
            TParm mtableparm = mtable.getParmValue().getRow(row);
            TParm result = TIOM_AppServer.executeAction(
                "action.ctr.CTRMainAction",
                "deleteCTRMaincode", mtableparm);
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败!");
            }
            else {
                this.messageBox("删除成功!");
                mtable.removeRow(row);
                onMClear();
                onQuery();

            }
        }
        //细项设定删除
        else if (index == 1) {
            int row = dtable.getSelectedRow();
            if (row < 0) {
                this.messageBox("请选择删除数据!");
                return;
            }
            TParm dtableparm = dtable.getParmValue().getRow(row);
            TParm result = TIOM_AppServer.executeAction(
                "action.ctr.CTRMainAction",
                "deleteCTRDetailcode", dtableparm);
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败!");
            }
            else {
                this.messageBox("删除成功!");
                dtable.removeRow(row);
                onDClear();
                onQuery();
            }

        }

    }

    /**
     * 查询数据<br>
     *onQuery
     */
    public void onQuery() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //主项管控的查询
        if (index == 0) {
            TParm parm = new TParm();
            String code = this.getValueString("ORDER_CODE").trim();
            int id = this.getValueInt("CONTROL_ID");
            if (!"".equals(code)) {
                parm.setData("ORDER_CODE", code);
                if (!"".equals(id)) {
                    parm.setData("CONTROL_ID", id);
                }
            }
            TParm mresult = CTRMainTool.getNewInstance().onMQuery(parm);
            // 判断错误值
            if (mresult == null || mresult.getCount() <= 0) {
                this.messageBox("没有查询数据!");
                return;
            }
            TParm tableparm = new TParm();
            for (int i = 0; i < mresult.getCount(); i++) {
                tableparm.addData("ORDER_CODE",
                                  mresult.getValue("ORDER_CODE", i));
                tableparm.addData("ORDER_DESC",
                                  mresult.getValue("ORDER_DESC", i));
                tableparm.addData("CONTROL_ID",
                                  mresult.getValue("CONTROL_ID", i));
                tableparm.addData("NHI_FLG", mresult.getValue("NHI_FLG", i));
                tableparm.addData("OWN_FLG", mresult.getValue("OWN_FLG", i));
                tableparm.addData("FORCE_FLG", mresult.getValue("FORCE_FLG", i));
                String text = mresult.getValue("MESSAGE_TEXT", i) +
                    mresult.getValue("MESSAGE_TEXT_E", i);
                tableparm.addData("MESSAGE_TEXT", text);
                tableparm.addData("CTRL_COMMENT",
                                  mresult.getValue("CTRL_COMMENT", i));
                tableparm.addData("ACTIVE_FLG",
                                  mresult.getValue("ACTIVE_FLG", i));
                tableparm.addData("SUBCLASS_CODE",
                        mresult.getValue("SUBCLASS_CODE", i));
            }
            ( (TTable)this.getTable("MTABLE")).setParmValue(tableparm);
            setUIMainF();
        }
        //细项设定查询
        if (index == 1) {
            TParm parm = new TParm();
            String code = this.getValueString("ORDER_CODE");
            getTextField("ORDER_CODE_1").setValue(code);
            String desc = this.getValueString("ORDER_DESC");
            getTextField("ORDER_DESC_1").setValue(desc);
            int id = this.getValueInt("CONTROL_ID");
            getNumberTextField("CONTROL_ID_1").setValue(id);
            if (!"".equals(code)) {
                parm.setData("ORDER_CODE", code);
            }
            if (!"".equals(id)) {
                parm.setData("CONTROL_ID", id);
            }
            TParm result = CTRMainTool.getNewInstance().onDQuery(parm);
            // 判断错误值
            if (result == null || result.getCount() <= 0) {
                dtable.removeRowAll();
                return;
            }
            TParm tableparm = new TParm();
            for (int i = 0; i < result.getCount(); i++) {
                String value = result.getValue("PARAVALUE_1", i);
                String restritemcode = result.getValue("RESTRITEM_CODE", i);
                tableparm.addData("ORDER_CODE_1",
                                  result.getValue("ORDER_CODE_1", i));
                tableparm.addData("CONTROL_ID_1",
                                  result.getValue("CONTROL_ID_1", i));
                tableparm.addData("SERIAL_NO", result.getValue("SERIAL_NO", i));
                tableparm.addData("GROUP_NO", result.getValue("GROUP_NO", i));
                tableparm.addData("RESTRITEM_CODE",
                                  result.getValue("RESTRITEM_CODE", i));
                tableparm.addData("PARAVALUE_1",
                                  result.getValue("PARAVALUE_1", i));
                //检验值
                if (restritemcode.equals("05")) {
                    tableparm.addData("PARAVALUE_1_DESC",
                                      meddesc(value));
                }
                else {
                    tableparm.addData("PARAVALUE_1_DESC",
                                      paravaluedesc(value));
                }
                tableparm.addData("PARAVALUE_2",
                                  result.getValue("PARAVALUE_2", i));
                tableparm.addData("PARAVALUE_3",
                                  result.getValue("PARAVALUE_3", i));
                tableparm.addData("PERIOD_CODE",
                                  result.getValue("PERIOD_CODE", i));
                tableparm.addData("PERIOD_VALUE",
                                  result.getValue("PERIOD_VALUE", i));
                tableparm.addData("START_VALUE",
                                  result.getValue("START_VALUE", i));
                tableparm.addData("END_VALUE", result.getValue("END_VALUE", i));
                String type = result.getValue("LOGICAL_TYPE", i);
                int Inttype = Integer.parseInt(type);
                switch (Inttype) {
                    case 1:
                        tableparm.addData("LOGICAL_TYPE", "AND");
                        break;
                    case 2:
                        tableparm.addData("LOGICAL_TYPE", "OR");
                        break;
                    case 3:
                        tableparm.addData("LOGICAL_TYPE", "NONE");
                        break;
                    default:
                        break;
                }
            }
            ( (TTable)this.getTable("dtable")).setParmValue(tableparm);
            setUIDMainF();
        }

    }

    /**
     * 清空数据<br>
     * onClear
     */
    public void onClear() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //主项设定清空
        if (index == 0) {
            onMClear();
            setUIMainF();
        }
        //细项设定清空
        else if (index == 1) {
            onDClear();
        }
    }

    /**
     *主表清空事件<br>
     *onMClear
     */

    public void onMClear() {
        this.clearValue("ORDER_CODE;ORDER_DESC;CONTROL_ID;NHI_FLG;OWN_FLG;FORCE_FLG;MESSAGE_TEXT;MESSAGE_TEXT_E;CTRL_COMMENT;ACTIVE_FLG;SUBCLASS_CODE;");
    }

    /**
     * 细项清空事件<br>
     *onDClear
     */
    public void onDClear() {
        this.clearValue(
            "SERIAL_NO;GROUP_NO;RESTRITEM_CODE;LIMIT_TYPE;PARAVALUE_1;PARAVALUE;PARAVALUE_1_DESC;PARADATATYPE_1;PARAMETER;PARAVALUE;"
            +
            "PERIOD_CODE;PERIOD_VALUE;LOGICAL_TYPE;PARAMETER_FLG;START_VALUE;END_VALUE;");
    }

    /**
     * 单击细项表数据事件<br>
     * ondtableClicked
     */
    public void ondtableClicked() {
        if (dtable.getSelectedRow() < 0) {
            this.messageBox("没有选中数据!");
        }
        int row = dtable.getClickedRow();
        TParm parm = new TParm();
        TParm tableParm = dtable.getParmValue();
        parm.setData("ORDER_CODE", tableParm.getValue("ORDER_CODE_1", row));
        parm.setData("CONTROL_ID", tableParm.getValue("CONTROL_ID_1", row));
        parm.setData("SERIAL_NO", tableParm.getValue("SERIAL_NO", row));
        TParm data = CTRMainTool.getNewInstance().onDQuery(parm);
        //查询无数据
        if (!data.equals("") && data.getCount() < 0) {
            this.messageBox("E0116");
            return;
        }
        setValueForParm(
            "ORDER_CODE_1;ORDER_DESC_1;CONTROL_ID_1;GROUP_NO;RESTRITEM_CODE;" +
            "LIMIT_TYPE;PARADATATYPE_1;SERIAL_NO;" +
            "PERIOD_CODE;PERIOD_VALUE;LOGICAL_TYPE;START_VALUE;END_VALUE"
            , data, 0);
        this.getTextField("PARAMETER").setValue(data.getValue("PROMPT_MSG1", 0));
        this.getTextField("SERIAL_NO").setValue(data.getValue("SERIAL_NO", 0));
        this.getTextField("PARAMETER").setEnabled(false);
        setUIDMainT();
        //限制排除
        if ("1".equals(data.getValue("LIMIT_TYPE", 0))) {
            this.getRadioButton("LIMIT_TYPE_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("LIMIT_TYPE", 0))) {
            this.getRadioButton("LIMIT_TYPE_2").setSelected(true);
        }
        //逻辑条件
        if ("1".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("and").setSelected(true);
        }
        else if ("2".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("or").setSelected(true);
        }
        else if ("3".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("none").setSelected(true);
        }
        //判断管控一类型
        if (data.getValue("RESTRPARA_TYPE1", 0).equals("1")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", false);
            callFunction("UI|END_VALUE|setEnabled", false);
        }
        else if (data.getValue("RESTRPARA_TYPE1", 0).equals("2")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", true);
            callFunction("UI|END_VALUE|setEnabled", true);
        }
        //判断数据类型
        if (data.getValue("PARADATATYPE_1", 0).equals("")) {
            callFunction("UI|PARADATATYPE_1|setEnabled", false);
            callFunction("UI|PARAVALUE_1|setEnabled", false);
        }
        else {
            String restritem = data.getValue("RESTRITEM_CODE", 0);
            String value = data.getValue("PARAVALUE_1", 0);
            int restritemcode = Integer.parseInt(restritem);
            switch (restritemcode) {
                //主诊断
                case 1:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //任一诊断
                case 2:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //施予医令之前
                case 3:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //施予医令之后
                case 4:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //检验值
                case 5:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    this.getTextFormat("PARAVALUE").setValue(value);
                    break;
                    //施予医令之前
                case 6:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * initdetail
     * 细项初始化
     */
    public void initdetail() {
        String code = this.getValueString("ORDER_CODE");
        getTextField("ORDER_CODE_1").setValue(code);
        String desc = this.getValueString("ORDER_DESC");
        getTextField("ORDER_DESC_1").setValue(desc);
        int id = this.getValueInt("CONTROL_ID");
        getNumberTextField("CONTROL_ID_1").setValue(id);
        onQuery();
        onDClear();
    }

    /**
     * CodeSelect
     * 项目代码选择事件
     */
    public void CodeSelect() {
        String box = getComboBox("RESTRITEM_CODE").getValue();
        TParm result = new TParm();
        if (box.equals("")) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("RESTRITEM_CODE", box);
        result = CtrQueryTool.getNewInstance().onQuery(parm);
        //查询无数据
        if (!result.equals("") && result.getCount() <= 0) {
            this.messageBox("E0116");
            return;
        }
        //清空数据
        this.clearValue(
            "PARAVALUE_1;PARAVALUE_1_DESC;PARAVALUE;START_VALUE;END_VALUE;PERIOD_CODE;PERIOD_VALUE;");
        setUIDMainT();
        //给提示框赋值
        getComboBox("PARADATATYPE_1").setValue(result.getValue(
            "PARADATATYPE_1", 0));
        int restritemcode = Integer.parseInt(box);
        //判断数据类型
        if (result.getValue("PARADATATYPE_1", 0).equals("")) {
            callFunction("UI|PARADATATYPE_1|setEnabled", false);
            callFunction("UI|PARAVALUE_1|setEnabled", false);
        }
        else {
            switch (restritemcode) {
                //主诊断
                case 1:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //任一诊断
                case 2:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //施予医令之前
                case 3:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //施予医令之后
                case 4:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //检验值
                case 5:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", true);
                    callFunction("UI|PERIOD_VALUE|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    getcheckdata(restritemcode);
                    break;
                    //施予医令之前
                case 6:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
            }

        }
        //提示框
        getTextField("PARAMETER").setValue(result.getValue("PROMPT_MSG1", 0));
        //判断是否多笔类型
        if (result.getValue("RESTRPARA_TYPE1", 0).equals("1")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", false);
            callFunction("UI|END_VALUE|setEnabled", false);
        }
        else if (result.getValue("RESTRPARA_TYPE1", 0).equals("2")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", true);
            callFunction("UI|END_VALUE|setEnabled", true);
        }
    }

    /**
     * 查询检验检查表
     * @param code int
     */
    private void getcheckdata(int code) {
        //检验检查
        if (code == 05) {
            String sql =
                "SELECT DISTINCT TESTITEM_CODE AS ID,TESTITEM_CHN_DESC AS NAME,PY1 FROM MED_LIS_RPT ORDER BY TESTITEM_CODE ";
            PARAVALUE.setPopupMenuSQL(sql);
            PARAVALUE.onQuery();
            PARAVALUE.setText("");
        }
    }

    /**
     * 检验数据
     * @return boolean
     */
    private boolean checkData() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        if (index == 1) {
            //管制项目是否为空
            String seq = this.getValueString("SERIAL_NO");
            if (seq == null || seq.length() <= 0) {
                this.messageBox("顺序号不能为空！");
                return false;
            }
            //管制项目是否为空
            String RESTRITEM_CODE = this.getValueString("RESTRITEM_CODE");
            if (RESTRITEM_CODE == null || RESTRITEM_CODE.length() <= 0) {
                this.messageBox("管制项目编码不能为空！");
                return false;
            }
            //组号是否为空
            String GROUP_NO = this.getValueString("GROUP_NO");
            if (GROUP_NO == null || GROUP_NO.length() <= 0) {
                this.messageBox("组号不能为空！");
                return false;
            }
            String start = this.getValueString("START_VALUE");
            String end = this.getValueString("END_VALUE");
            String[] str = new String[] {
                start, end};
            //管制项目为检验值
            if (Integer.parseInt(RESTRITEM_CODE) == 05) {
                if (!start.equals("") && !end.equals("") && start.length() > 0 &&
                    end.length() > 0) {
                    if (this.getValueString("PARAVALUE").equals("")) {
                        this.messageBox("请先填写参数一选项！");
                        return false;
                    }
                    if (CTRPanelTool.isCharic(str) ||
                        CTRPanelTool.isChinese(str)) {
                        if (!start.equals(end)) {
                            this.messageBox("汉字或字符不相等！");
                            return false;
                        }
                    }
                    else if (CTRPanelTool.isNumeric(str)) {
                        if (Double.parseDouble(start) > Double.parseDouble(end)) {
                            this.messageBox("数字大小错误！");
                            return false;
                        }
                    }
                    else {
                        this.messageBox("输入非法字符！");
                        return false;
                    }
                }
                //其中文本框有一个为空
                else if ( (!start.equals("") && end.equals("")) ||
                         (start.equals("") && !end.equals(""))) {
                    this.messageBox("请填写完整检验值区间！");
                    return false;
                }
                else {
                    if (!this.getValueString("PERIOD_CODE").equals("")) {
                        this.messageBox("请填写检验值区间！");
                        return false;
                    }
                }
            }
            //管制项目为主诊断或任一诊断
            if (Integer.parseInt(RESTRITEM_CODE) == 01 ||
                Integer.parseInt(RESTRITEM_CODE) == 02) {
                if ( (!start.equals("") && end.equals("")) ||
                    (start.equals("") && !end.equals(""))) {
                    this.messageBox("请填写完整诊断值区间！");
                    return false;
                }
                if (!start.equals("") && !end.equals("")) {
                    if (!CTRPanelTool.ismixic(str)) {
                        this.messageBox("诊断值区间数据不规范！");
                        return false;
                    }
                    if (StringTool.compareTo(start, end) > 0) {
                        this.messageBox("诊断值区间大小错误！");
                        return false;
                    }
                }

            }
            TParm inparm = new TParm();
            inparm.setData("RESTRITEM_CODE", RESTRITEM_CODE);
            TParm result = CtrQueryTool.getNewInstance().onQuery(inparm);
            //限制适用范围
            if (!result.getBoolean("INCLUDE_FLG", 0)) {
                if ("Y".equals(this.getValueString("LIMIT_TYPE_1"))) {
                    this.messageBox("此管制不适合限制！");
                    return false;
                }
            }
            //排除适用范围
            if (!result.getBoolean("EXCLUDE_FLG", 0)) {
                if ("Y".equals(this.getValueString("LIMIT_TYPE_2"))) {
                    this.messageBox("此管制不适合排除！");
                    return false;
                }
            }
            //门诊期间类型适用
            if (!result.getBoolean("OPD_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("O")) {
                    this.messageBox("此管制不适合门诊！");
                    return false;
                }
            }
            //急诊范围适用
            if (!result.getBoolean("EMG_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("E")) {
                    this.messageBox("此管制不适合急诊！");
                    return false;
                }
            }
            //住院范围适用
            if (!result.getBoolean("INP_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("I")) {
                    this.messageBox("此管制不适合住院！");
                    return false;
                }
            }
            //UD范围适用
            if (!result.getBoolean("UD_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("UD")) {
                    this.messageBox("此管制不适合UD！");
                    return false;
                }
            }
        }
        else {
            //医令代码是否为空
            String order_code = this.getValueString("ORDER_CODE");
            if (order_code == null || order_code.length() <= 0) {
                this.messageBox("医令代码不能为空");
                return false;
            }
            //管制编码是否为空
            String control_code = this.getValueString("CONTROL_ID");
            if (control_code == null || control_code.length() <= 0) {
                this.messageBox("管制编码不能为空");
                return false;
            }
            //信息内容是否为空
            String text = this.getValueString("MESSAGE_TEXT");
            if (text == null || text.length() <= 0) {
                this.messageBox("信息内容不能为空");
                return false;
            }
        }
        return true;
    }

    /**
     * 得到TTable对象
     * @param tagName String
     * @return TTable
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TTextField对象
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到TNumberTextField对象
     * @param tagName String
     * @return TNumberTextField
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
    }

    /**
     * 得到TRadioButton对象
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     *  得到TComboBox对象
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     * @param tagName String
     * @return TTextFormat
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * getDBTool
     * 数据库工具实例
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * MaxSeq
     * 细表获取最大顺序号
     * @return int
     */
    private int MaxSeq() {
        int number;
        TParm MaxSeqParm = new TParm(this.getDBTool().select(
            "SELECT MAX(SERIAL_NO) AS NO FROM CTR_DETAIL WHERE ORDER_CODE='" +
            this.getValueString("ORDER_CODE_1") + "' AND CONTROL_ID='" +
            this.getValueInt("CONTROL_ID_1") + "'"));
        if (MaxSeqParm.getCount() <= 0) {
            number = 0;
        }
        else {
            number = MaxSeqParm.getInt("NO", 0) + 1;
        }
        return number;
    }

    /**
     * 获取细表逻辑属性
     * @return String
     */
    private String logicalstr() {
        //检验逻辑条件
        String logicalytypestr = "";
        if (MaxSeq() == 0) {
            logicalytypestr = "";
        }
        if (MaxSeq() > 0) {
            TParm checklogical = new TParm(this.getDBTool().select(
                "SELECT LOGICAL_TYPE FROM CTR_DETAIL WHERE ORDER_CODE='" +
                this.getValueString("ORDER_CODE_1") +
                "' AND CONTROL_ID='" +
                this.getValueInt("CONTROL_ID_1") + "' AND SERIAL_NO='" +
                (MaxSeq() - 1) + "'" +
                "ORDER BY SERIAL_NO"));
            if (checklogical.getCount() <= 0) {
                logicalytypestr = "";
            }
            else {
                logicalytypestr = checklogical.getValue("LOGICAL_TYPE", 0);
            }
        }
        return logicalytypestr;
    }


    /**
     * 动态加载界面
     * @param name String
     * @return TPanel
     */
    public TPanel getTPanel(String name) {

        TPanel panel = (TPanel)this.getComponent(name);
        return panel;
    }

}
