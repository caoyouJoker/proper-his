package com.javahis.ui.ctr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.ctr.CtrQueryTool;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title:医令管控查询 </p>
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
public class CtrQueryControl
    extends TControl {

    private TTable table;
    private CtrQueryTool jdo = null;
    /**
     * 初始化
     */
    public void onInit() {
        //JDO类一般用于操作数据库,这个类封装了对数据库操作的大多数方法
        super.init();
        table = this.getTable("TABLE");
        callFunction("UI| table|addEventListener",
                     table + "->" + TTableEvent.CLICKED, this, "onTableClicked");
        onQuery();

    }

    /**
     * 保存数据<br>
     *
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        //参数一
        if ("Y".equals(this.getValueString("RESTRPARA_TYPE1_1"))) {
            parm.setData("RESTRPARA_TYPE1", "1");
        }
        else if ("Y".equals(this.getValueString("RESTRPARA_TYPE1_2"))) {
            parm.setData("RESTRPARA_TYPE1", "2");
        }
        else {
            parm.setData("RESTRPARA_TYPE1", "");
        }
        if ("Y".equals(this.getValueString("PARADATATYPE_1_1"))) {
            parm.setData("PARADATATYPE_1", "1");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_1_2"))) {
            parm.setData("PARADATATYPE_1", "2");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_1_3"))) {
            parm.setData("PARADATATYPE_1", "3");
        }
        else {
            parm.setData("PARADATATYPE_1", "");
        }

        //参数二
        if ("Y".equals(this.getValueString("RESTRPARA_TYPE2_1"))) {
            parm.setData("RESTRPARA_TYPE2", "1");
        }
        else if ("Y".equals(this.getValueString("RESTRPARA_TYPE2_2"))) {
            parm.setData("RESTRPARA_TYPE2", "2");
        }
        else {
            parm.setData("RESTRPARA_TYPE2", "");
        }

        if ("Y".equals(this.getValueString("PARADATATYPE_2_1"))) {
            parm.setData("PARADATATYPE_2", "1");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_2_2"))) {
            parm.setData("PARADATATYPE_2", "2");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_2_3"))) {
            parm.setData("PARADATATYPE_2", "3");
        }
        else {
            parm.setData("PARADATATYPE_2", "");
        }

        //参数三
        if ("Y".equals(this.getValueString("RESTRPARA_TYPE3_1"))) {
            parm.setData("RESTRPARA_TYPE3", "1");
        }
        else if ("Y".equals(this.getValueString("RESTRPARA_TYPE3_2"))) {
            parm.setData("RESTRPARA_TYPE3", "2");
        }
        else {
            parm.setData("RESTRPARA_TYPE3", "");
        }
        if ("Y".equals(this.getValueString("PARADATATYPE_3_1"))) {
            parm.setData("PARADATATYPE_3", "1");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_3_2"))) {
            parm.setData("PARADATATYPE_3", "2");
        }
        else if ("Y".equals(this.getValueString("PARADATATYPE_3_3"))) {
            parm.setData("PARADATATYPE_3", "3");
        }
        else {
            parm.setData("PARADATATYPE_3", "");
        }

        parm.setData("RESTRITEM_CODE", this.getValueString("RESTRITEM_CODE"));
        parm.setData("RESTRITEM_DESC", this.getValueString("RESTRITEM_DESC"));
        parm.setData("DRUG_FLG", this.getValueString("DRUG_FLG"));
        parm.setData("TREAT_FLG", this.getValueString("TREAT_FLG"));
        parm.setData("MATERIAL_FLG", this.getValueString("MATERIAL_FLG"));
        parm.setData("OPD_FLG", this.getValueString("OPD_FLG"));
        parm.setData("EMG_FLG", this.getValueString("EMG_FLG"));
        parm.setData("INP_FLG", this.getValueString("INP_FLG"));
        parm.setData("UD_FLG", this.getValueString("UD_FLG"));
        parm.setData("INCLUDE_FLG", this.getValueString("INCLUDE_FLG"));
        parm.setData("EXCLUDE_FLG", this.getValueString("EXCLUDE_FLG"));
        parm.setData("PROMPT_MSG1", this.getValueString("PROMPT_MSG1"));
        parm.setData("PROMPT_MSG2", this.getValueString("PROMPT_MSG2"));
        parm.setData("PROMPT_MSG3", this.getValueString("PROMPT_MSG3"));
        parm.setData("PROMPT_MSG3", this.getValueString(""));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result2 = new TParm();
        TParm checkresult = CtrQueryTool.getNewInstance().onQuery(
            parm);
        if (checkresult.getCount() <= 0) {
            // 新增数据
            result2 = CtrQueryTool.getNewInstance().onTableInsert(parm);
        }
        else {
            // 更新数据
            result2 = CtrQueryTool.getNewInstance().onTableUpdate(parm);
        }
        if (result2.getErrCode() < 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            onQuery();
        }

    }

    /**
     * 删除数据<br>
     *
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择删除数据");
            return;
        }
        TParm parm = table.getParmValue().getRow(row);
        TParm result = CtrQueryTool.getNewInstance().onTableDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
        }
        else {
            this.messageBox("删除成功");
            table.removeRow(row);
        }
        this.clearValue("RESTRITEM_CODE;RESTRITEM_DESC;DRUG_FLG;TREAT_FLG;MATERIAL_FLG;OPD_FLG;EMG_FLG;INP_FLG;UD_FLG;INCLUDE_FLG;EXCLUDE_FLG;PROMPT_MSG1;RESTRPARA_TYPE1;PARADATATYPE_1;PROMPT_MSG2;RESTRPARA_TYPE2;PARADATATYPE_2;PROMPT_MSG3;RESTRPARA_TYPE3;PARADATATYPE_3");
        ( (TTextField) getComponent("RESTRITEM_CODE")).setEnabled(true);
        ( (TTextField) getComponent("RESTRITEM_DESC")).setEnabled(true);
    }

    /**
     * 查询数据<br>
     *
     */

    public void onQuery() {
        TParm parm = new TParm();
        String code = this.getValueString("RESTRITEM_CODE");
        if (!"".equals(code)) {
            parm.setData("RESTRITEM_CODE", code);
        }
        TParm result = CtrQueryTool.getNewInstance().onQuery(parm);
        // 判断错误值
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        ( (TTable)this.getTable("TABLE")).setParmValue(result);
    }

    /**
     * 清空数据<br>
     *
     */
    public void onClear() {
        this.getRadioButton("RESTRPARA_TYPE1_3").setSelected(true);
        this.getRadioButton("PARADATATYPE_1_4").setSelected(true);
        this.getRadioButton("RESTRPARA_TYPE2_3").setSelected(true);
        this.getRadioButton("PARADATATYPE_2_4").setSelected(true);
        this.getRadioButton("RESTRPARA_TYPE3_3").setSelected(true);
        this.getRadioButton("PARADATATYPE_3_4").setSelected(true);
        this.clearValue("RESTRITEM_CODE;RESTRITEM_DESC;DRUG_FLG;TREAT_FLG;MATERIAL_FLG;OPD_FLG;EMG_FLG;INP_FLG;" +
                        "UD_FLG;INCLUDE_FLG;EXCLUDE_FLG;PROMPT_MSG1;RESTRPARA_TYPE1;PARADATATYPE_1;PROMPT_MSG2;" +
                        "RESTRPARA_TYPE2;PARADATATYPE_2;PROMPT_MSG3;RESTRPARA_TYPE3;PARADATATYPE_3");
        table.removeRowAll();
        ( (TTextField) getTextField("RESTRITEM_CODE")).setEnabled(true);
        ( (TTextField) getTextField("RESTRITEM_DESC")).setEnabled(true);
    }

    /**
     * 单击表数据事件<br>
     *
     */
    public void onTableClicked() {
        int row = table.getClickedRow();
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
            "RESTRITEM_CODE;RESTRITEM_DESC;DRUG_FLG;TREAT_FLG;MATERIAL_FLG;OPD_FLG;EMG_FLG;INP_FLG;" +
            "UD_FLG;INCLUDE_FLG;EXCLUDE_FLG;PROMPT_MSG1;PROMPT_MSG2;" +
            "PROMPT_MSG3;",
            data, row);
        //参数一管控
        if ("1".equals(data.getValue("RESTRPARA_TYPE1", row))) {
            this.getRadioButton("RESTRPARA_TYPE1_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("RESTRPARA_TYPE1", row))) {
            this.getRadioButton("RESTRPARA_TYPE1_2").setSelected(true);
        }
        else if ("".equals(data.getValue("RESTRPARA_TYPE1", row))) {
            this.getRadioButton("RESTRPARA_TYPE1_3").setSelected(true);
        }

        if ("1".equals(data.getValue("PARADATATYPE_1", row))) {
            this.getRadioButton("PARADATATYPE_1_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("PARADATATYPE_1", row))) {
            this.getRadioButton("PARADATATYPE_1_2").setSelected(true);
        }
        else if ("3".equals(data.getValue("PARADATATYPE_1", row))) {
            this.getRadioButton("PARADATATYPE_1_3").setSelected(true);
        }
        else if ("".equals(data.getValue("PARADATATYPE_1", row))) {
            this.getRadioButton("PARADATATYPE_1_4").setSelected(true);
        }
        //参数二管控
        if ("1".equals(data.getValue("RESTRPARA_TYPE2", row))) {
            this.getRadioButton("RESTRPARA_TYPE2_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("RESTRPARA_TYPE2", row))) {
            this.getRadioButton("RESTRPARA_TYPE2_2").setSelected(true);
        }
        else if ("".equals(data.getValue("RESTRPARA_TYPE2", row))) {
            this.getRadioButton("RESTRPARA_TYPE2_3").setSelected(true);
        }

        if ("1".equals(data.getValue("PARADATATYPE_2", row))) {
            this.getRadioButton("PARADATATYPE_2_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("PARADATATYPE_2", row))) {
            this.getRadioButton("PARADATATYPE_2_2").setSelected(true);
        }
        else if ("3".equals(data.getValue("PARADATATYPE_2", row))) {
            this.getRadioButton("PARADATATYPE_2_3").setSelected(true);
        }
        else if ("".equals(data.getValue("PARADATATYPE_2", row))) {
            this.getRadioButton("PARADATATYPE_2_4").setSelected(true);
        }

        //参数三管控
        if ("1".equals(data.getValue("RESTRPARA_TYPE3", row))) {
            this.getRadioButton("RESTRPARA_TYPE3_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("RESTRPARA_TYPE3", row))) {
            this.getRadioButton("RESTRPARA_TYPE3_2").setSelected(true);
        }
        else if ("".equals(data.getValue("RESTRPARA_TYPE3", row))) {
            this.getRadioButton("RESTRPARA_TYPE3_3").setSelected(true);
        }

        if ("1".equals(data.getValue("PARADATATYPE_3", row))) {
            this.getRadioButton("PARADATATYPE_3_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("PARADATATYPE_3", row))) {
            this.getRadioButton("PARADATATYPE_3_2").setSelected(true);
        }
        else if ("3".equals(data.getValue("PARADATATYPE_3", row))) {
            this.getRadioButton("PARADATATYPE_3_3").setSelected(true);
        }
        else if ("".equals(data.getValue("PARADATATYPE_3", row))) {
            this.getRadioButton("PARADATATYPE_3_4").setSelected(true);
        }

        ( (TTextField) getTextField("RESTRITEM_CODE")).setEnabled(false);
        ( (TTextField) getTextField("RESTRITEM_DESC")).setEnabled(false);
    }

    /**
     *得到TTable对象
     * @param tagName String
     * @return TTable
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * getTextField
     * 得到TTextField对象
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 检验数据
     * @return boolean
     */
    private boolean checkData() {
        String code = this.getValueString("RESTRITEM_CODE");
        if (code == null || code.length() == 0) {
            this.messageBox("请先填写代码");
            return false;
        }
        String type = this.getValueString("RESTRITEM_DESC");
        if (type == null || type.length() == 0) {
            this.messageBox("请先填写名称");
            return false;
        }
        return true;
    }

}
