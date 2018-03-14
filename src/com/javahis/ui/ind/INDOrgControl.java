package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.system.textFormat.TextFormatDept;

/**
 * <p>Title:药库药房设定 </p>
 *
 * <p>Description:药库药房设定 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangy 2009.4.22
 * @version 1.0
 */

public class INDOrgControl
    extends TControl {

    private Vector vct;

    public INDOrgControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if (table.getSelectedRow() < 0) {
            // 新增数据
            if (!CheckData())
                return;
            row = table.addRow();
        }
        else {
            row = table.getSelectedRow();
        }
        table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
        TTextFormat org_code = this.getTextFormat("ORG_CODE");
        table.setItem(row, "ORG_CHN_DESC", org_code.getText());
        String py = TMessage.getPy(org_code.getName());
        table.getDataStore().setItem(row, "PY1", py);
        table.setItem(row, "SEQ", getValueString("SEQ"));
        String org_type = "C";
        if (getRadioButton("ORG_TYPE_A").isSelected())
            org_type = "A";
        else if (getRadioButton("ORG_TYPE_B").isSelected())
            org_type = "B";
        else
            org_type = "C";
        table.setItem(row, "ORG_TYPE", org_type);
        table.setItem(row, "SUP_ORG_CODE", getValueString("SUP_ORG_CODE"));
        table.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
        table.setItem(row, "DECOCT_CODE", getValueString("DECOCT_CODE"));
        
        table.setItem(row, "IS_SUBORG", getValueString("IS_SUBORG"));		
        
        table.setItem(row, "STATION_FLG", getValueString("STATION_FLG"));
        table.setItem(row, "EXINV_FLG", getValueString("EXINV_FLG"));
        table.setItem(row, "INJ_ORG_FLG", getValueString("INJ_ORG_FLG"));
        table.setItem(row, "ATC_FLG", getValueString("ATC_FLG"));
        table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
        table.setItem(row, "OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        table.setItem(row, "OPT_DATE", date);
        table.setItem(row, "OPT_TERM", Operator.getIP());
        table.getDataStore().setItem(row, "ORG_FLG", "Y");
        if (!table.getDataStore().update()) {
            messageBox("E0001");
            return;
        }
        messageBox("P0001");
        table.setDSValue();
        vct = table.getDataStore().getVector("ORG_CODE");
        onClear();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String region = getValueString("REGION_CODE");
        String type = "";
        if (getRadioButton("ORG_TYPE_A").isSelected())
            type = "A";
        else if (getRadioButton("ORG_TYPE_B").isSelected())
            type = "B";
        else
            type = "C";
        String org = getValueString("ORG_CODE");
        String filterString = "ORG_TYPE = '" + type + "'";
        if (region.length() > 0)
            filterString += " AND REGION_CODE = '" + region + "'";
        if (org.length() > 0)
            filterString += " AND ORG_CODE = '" + org + "'";
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
    }

    /**	
     * 清空方法
     */
    public void onClear() {
        // 药房名称
        TTextFormat org_code = getTextFormat("ORG_CODE");
        org_code.setEnabled(true);
        org_code.setValue("");

        // 库房类型
        getRadioButton("ORG_TYPE_C").setSelected(true);
        onChangeOrgType();

        // 登陆者所属区域
        TComboBox region = getComboBox("REGION_CODE");
        region.setSelectedID(Operator.getRegion());

        // 序号
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);

        // 备注
        setValue("DESCRIPTION", "");

        // TABLE
        getTable("TABLE").setSelectionMode(0);
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "ORG_CODE;REGION_CODE;"
                + "DECOCT_CODE;SEQ;DESCRIPTION;DECOCT_CODE";
            this.setValueForParm(likeNames, parm);
            String org_type = parm.getValue("ORG_TYPE");
            if ("A".equals(org_type))
                getRadioButton("ORG_TYPE_A").setSelected(true);
            else if ("B".equals(org_type))
                getRadioButton("ORG_TYPE_B").setSelected(true);
            else
                getRadioButton("ORG_TYPE_C").setSelected(true);
            onChangeOrgType();     
            getCheckBox("STATION_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("STATION_FLG")));
            getCheckBox("EXINV_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("EXINV_FLG")));
            getCheckBox("INJ_ORG_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("INJ_ORG_FLG")));
            getCheckBox("ATC_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("ATC_FLG")));
            getCheckBox("IS_SUBORG").setSelected(                              
                    TypeTool.getBoolean(parm.getValue("IS_SUBORG")));
            
            // 根据库房类别和护士站设置药库列表(ORG_CODE)
            onSetOrgCodeByTypeAndStation(org_type, TypeTool.getBoolean(parm
                .getValue("STATION_FLG")));
            getComboBox("SUP_ORG_CODE").setSelectedID(
                parm.getValue("SUP_ORG_CODE"));
            getComboBox("znCombobox").setSelectedID(
                    parm.getValue("CABINET_ID"));
            this.getTextFormat("ORG_CODE").setValue(parm.getValue("ORG_CODE"));
            TextFormatDept decoct = (TextFormatDept)this.getComponent(
                "DECOCT_CODE");
            decoct.setValue(parm.getValue("DECOCT_CODE"));
            onChildClick();
        }

    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INDSQL.getINDORG(Operator.getRegion()));
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();
        callFunction("UI|CABINET_ID|setEnabled", false);
        // 登陆者所属区域
        TComboBox region = getComboBox("REGION_CODE");
        region.setSelectedID(Operator.getRegion());

        // 最大号+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);

        // 查询所有的已有库房
        vct = dataStroe.getVector("ORG_CODE");
        // 库房类型改变事件
        onChangeOrgType();
    }

    /**
     * 库房类型改变事件
     */
    public void onChangeOrgType() {
        // 归属药房SUP_ORG_CODE
        TComboOrgCode sup_code = (TComboOrgCode)this
            .getComboBox("SUP_ORG_CODE");
        // ORG_TYPE_A:主库 ；ORG_TYPE_B：中库 ；ORG_TYPE_C：小库
        if (getRadioButton("ORG_TYPE_A").isSelected()) {
            onSetStatus("N,N,N,N,N,N");
            sup_code.setOrgType("");
            // 根据库房类别设置药库列表(ORG_CODE)
            onSetOrgCodeByOrgType("A");
        }
        else if (getRadioButton("ORG_TYPE_B").isSelected()) {
            onSetStatus("N,Y,N,Y,Y,Y");
            sup_code.setOrgType("A");
            // 根据库房类别设置药库列表(ORG_CODE)
            onSetOrgCodeByOrgType("B");
        }
        else {
            onSetStatus("Y,Y,Y,N,N,N");
            sup_code.setOrgType("B");
            // 根据库房类别设置药库列表(ORG_CODE)
            onSetOrgCodeByOrgType("C");
        }
        sup_code.onQuery();
    }

    /**
     * 护士站(CheckBox)改变事件
     */
    public void onSelectStation() {
        String org_type = "";
        if (getRadioButton("ORG_TYPE_A").isSelected())
            org_type = "A";
        else if (getRadioButton("ORG_TYPE_B").isSelected())
            org_type = "B";
        else
            org_type = "C";
        // 根据库房类别和护士站设置药库列表(ORG_CODE)
        onSetOrgCodeByTypeAndStation(org_type,
                                     getCheckBox("STATION_FLG").isSelected());
    }

    /**
     * 根据库房类别设置药库列表(ORG_CODE)
     *
     * @param org_type
     */
    private void onSetOrgCodeByOrgType(String org_type) {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(INDSQL.
                                                   getOrgCodeByOrgType(org_type, Operator.getRegion()));
        this.getTextFormat("ORG_CODE").onQuery();
        this.getTextFormat("ORG_CODE").setText("");
    }

    /**
     * 根据库房类别和护士站设置药库列表(ORG_CODE)
     *
     * @param org_type
     * @param station_flg
     */
    private void onSetOrgCodeByTypeAndStation(String org_type,
                                              boolean station_flg) {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(INDSQL.
            getOrgCodeByTypeAndStation(org_type, station_flg, Operator.getRegion()));
        this.getTextFormat("ORG_CODE").onQuery();
    }

    /**
     * 设定页面状态
     *
     * @param parm
     *            状态参数
     */
    private void onSetStatus(String parm) {
        String[] status = parm.split(",");
        // 护士站
        TCheckBox station = getCheckBox("STATION_FLG");
        station.setSelected(false);
        station.setEnabled(StringTool.getBoolean(status[0]));
        // 归属药房
        TComboBox sup_org = getComboBox("SUP_ORG_CODE");
        sup_org.setSelectedIndex( -1);
        sup_org.setEnabled(StringTool.getBoolean(status[1]));
        // 科室请领单
        TCheckBox exinv = getCheckBox("EXINV_FLG");
        exinv.setSelected(false);
        exinv.setEnabled(StringTool.getBoolean(status[2]));
        // 静脉配液中心
        TCheckBox inj_org = getCheckBox("INJ_ORG_FLG");
        inj_org.setSelected(false);
        inj_org.setEnabled(StringTool.getBoolean(status[3]));
        // 包药机
        TCheckBox act = getCheckBox("ATC_FLG");
        act.setSelected(false);
        act.setEnabled(StringTool.getBoolean(status[4]));
        // 煎药室 DECOCT_CODE
        TextFormatDept decoct = (TextFormatDept)this.getComponent("DECOCT_CODE");
        decoct.setValue("");
        decoct.setEnabled(StringTool.getBoolean(status[5]));
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("库房代码不能为空");
            return false;
        }
        TComboBox sup_org = getComboBox("SUP_ORG_CODE");
        if (sup_org.isEnabled() && "".equals(getValueString("SUP_ORG_CODE"))) {
            this.messageBox("中小库房的归属药房不能为空");
            return false;
        }
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("区域代码不能为空");
            return false;
        }
        for (int i = 0; i < vct.size(); i++) {
            if (org_code.equals( ( (Vector) vct.get(i)).get(0).toString())) {
                this.messageBox("库房已存在");
                return false;
            }
        }
        return true;
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // 保存最大值
            if (max < value) {
                max = value;
                continue;
            }
        }
        // 最大号加1
        max++;
        return max;
    }
    
    public void onChildClick() {								
    	TCheckBox t = this.getCheckBox("IS_SUBORG");						
    	if("Y".equals(t.getValue())) {				
    		//TTextFormat comboBox =  (TTextFormat)this.getComponent("CABINET_CODE");			
    		callFunction("UI|CABINET_ID|setEnabled", true);
    	}			
    	if("N".equals(t.getValue())) {							
    		//TComboBox comboBox =  this.getComboBox("CABINET_CODE");			
    		callFunction("UI|CABINET_ID|setEnabled", false);
    	}		
    }
}
