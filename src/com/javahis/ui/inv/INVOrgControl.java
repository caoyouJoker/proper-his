package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import java.util.Vector;
import jdo.inv.INVSQL;
import com.javahis.system.combo.TComboOrgCode;
import com.dongyang.util.TMessage;
import jdo.sys.SystemTool;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 物资库房设定</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVOrgControl
    extends TControl {
    public INVOrgControl() {
    }

    private Vector vct;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        TTable table = getTable("TABLE");
        table.removeRowAll();  
        TDataStore dataStroe = new TDataStore();
       dataStroe.setSQL(INVSQL.getorg());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

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
        TextFormatINVOrg sup_code = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
        // ORG_TYPE_A:主库 ；ORG_TYPE_B：中库 ；ORG_TYPE_C：小库
        if (getRadioButton("ORG_TYPE_A").isSelected()) {
            onSetStatus(false);
            sup_code.setOrgType("");
            this.getCheckBox("STATION_FLG").setEnabled(false);
        }
        else if (getRadioButton("ORG_TYPE_B").isSelected()) {
            onSetStatus(true);
            sup_code.setOrgType("A");
            this.getCheckBox("STATION_FLG").setEnabled(false);
        }
        else {
            onSetStatus(true);
            sup_code.setOrgType("B");
            this.getCheckBox("STATION_FLG").setEnabled(true);
        }
        this.setValue("STATION_FLG", "N");
        onSetOrgCodeByOrgType();
        sup_code.onQuery();
    }

    /**
     * 设定页面状态
     *
     * @param parm
     *            状态参数
     */
    private void onSetStatus(boolean flg) {
        // 归属库房
        TextFormatINVOrg sup_org = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
        sup_org.setValue("");
        sup_org.setEnabled(flg);
    }

    /**
     * 根据库房类别设置物资部门列表(ORG_CODE)
     *
     * @param org_type
     */
    private void onSetOrgCodeByOrgType() {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(
            INVSQL.getOrgCodeByOrgType());
        this.getTextFormat("ORG_CODE").onQuery();
        this.getTextFormat("ORG_CODE").setText("");
    }

    /**
     * 护士站(CheckBox)改变事件
     */
    public void onSelectStation() {
        // 根据库房类别和护士站设置药库列表(ORG_CODE)
        onSetOrgCodeByTypeAndStation();
    }

    /**
     * 根据库房类别和护士站设置药库列表(ORG_CODE)
     *
     * @param org_type
     * @param station_flg
     */
    private void onSetOrgCodeByTypeAndStation() {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(INVSQL.
            getOrgCodeByTypeAndStation());
        this.getTextFormat("ORG_CODE").onQuery();
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
        table.setItem(row, "ORG_DESC", org_code.getText());
        String py = TMessage.getPy(org_code.getText());
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
        table.setItem(row, "STOCK_ORG_CODE", getValue("SUP_ORG_CODE"));
        table.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
        table.setItem(row, "STATION_FLG", getValueString("STATION_FLG"));
        table.setItem(row, "MAT_FLG", getValueString("MAT_FLG"));
        table.setItem(row, "INV_FLG", getValueString("INV_FLG"));
        table.setItem(row, "CON_FLG", getValueString("CON_FLG"));
        if (getValueString("CON_FLG").equals("Y")) {
        	table.setItem(row, "IN_ORG", getValueString("IN_ORG"));
        	table.setItem(row, "OUT_ORG", getValueString("OUT_ORG"));
			
		}else {
        	table.setItem(row, "IN_ORG","");
        	table.setItem(row, "OUT_ORG", "");
		}
        table.setItem(row, "REMARK", getValueString("REMARK"));
        table.setItem(row, "AUTO_FILL_TYPE", getValueString("AUTO_FILL_TYPE"));
        table.setItem(row, "FIXEDAMOUNT_FLG", getValueString("FIXEDAMOUNT_FLG"));
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
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
     * 检查数据
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("库房代码不能为空");
            return false;
        }
        TextFormatINVOrg sup_org = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
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
        setValue("REMARK", "");
        setValue("FIXEDAMOUNT_FLG", "");
        setValue("AUTO_FILL_TYPE", "");
        setValue("REMARK", "") ;
        setValue("IN_ORG", "") ;
        setValue("OUT_ORG", "") ;
        
        getTextFormat("IN_ORG").setEnabled(false);
		getTextFormat("OUT_ORG").setEnabled(false);

        // TABLE
        getTable("TABLE").setSelectionMode(0);
    }
    public void onChange() {
    	
    	if (getCheckBox("CON_FLG").isSelected()) {
    		getTextFormat("IN_ORG").setEnabled(true);
			getTextFormat("OUT_ORG").setEnabled(true);
    		
			
		}else {
			getTextFormat("IN_ORG").setEnabled(false);
			getTextFormat("OUT_ORG").setEnabled(false);
		}
    	
		
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
        getTextFormat("IN_ORG").setEnabled(false);
		getTextFormat("OUT_ORG").setEnabled(false);
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "ORG_CODE;REGION_CODE;SEQ;REMARK";
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
            getCheckBox("MAT_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("MAT_FLG")));
            getCheckBox("INV_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("INV_FLG")));
            getCheckBox("CON_FLG").setSelected(
                    TypeTool.getBoolean(parm.getValue("CON_FLG")));
            if (getCheckBox("CON_FLG").isSelected()) {
				getTextFormat("IN_ORG").setEnabled(true);
				getTextFormat("OUT_ORG").setEnabled(true);
			}else {
				getTextFormat("IN_ORG").setEnabled(false);
				getTextFormat("OUT_ORG").setEnabled(false);
			}

            // 根据库房类别和护士站设置药库列表(ORG_CODE)
            //onSetOrgCodeByTypeAndStation();
            this.getTextFormat("ORG_CODE").setValue(parm.getValue("ORG_CODE"));
           this.setValue("SUP_ORG_CODE", parm.getValue("STOCK_ORG_CODE"));
           this.setValue("AUTO_FILL_TYPE", parm.getValue("AUTO_FILL_TYPE"));
           this.setValue("FIXEDAMOUNT_FLG", parm.getValue("FIXEDAMOUNT_FLG"));
           this.setValue("IN_ORG", parm.getValue("IN_ORG"));
           this.setValue("OUT_ORG", parm.getValue("OUT_ORG"));

        }
    }

    /**
     * 删除方法
     */
    public void onDelete(){
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择删除项");
            return;
        }
        table.removeRow(row);
        if (!table.getDataStore().update()) {
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");
        table.setDSValue();
        vct = table.getDataStore().getVector("ORG_CODE");
        //onClear();
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
}
