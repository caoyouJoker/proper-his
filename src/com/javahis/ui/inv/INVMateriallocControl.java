package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import jdo.inv.INVSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;
import com.dongyang.data.TParm;

/**
 * <p>Title: 物资料位</p>
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
public class INVMateriallocControl extends TControl{
    public INVMateriallocControl() {
    }

    private String action = "save";

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
        dataStroe.setSQL(INVSQL.getMaterialloc());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);

        // 最大号+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if ("save".equals(action)) {
            TTextFormat combo = this.getTextFormat("ORG_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
            table.setItem(row, "MATERIAL_LOC_CODE",
                          getValueString("MATERIAL_LOC_CODE"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "REMARK", getValueString("REMARK"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
            table.setItem(row, "OPT_TERM", Operator.getIP());
            table.setItem(row, "CORRSUPDEPT", getValueString("CORRSUPDEPT"));
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        this.onClear() ;
        table.setDSValue();
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("部门代码不能为空");
            return false;
        }
        String material_loc_code = getValueString("MATERIAL_LOC_CODE");
        if ("".equals(material_loc_code)) {
            this.messageBox("料位代码不能为空");
            return false;
        }
        String material_loc_desc = getValueString("DESCRIPTION");
        if ("".equals(material_loc_desc)) {
            this.messageBox("料位名称不能为空");
            return false;
        }
        String sql = INVSQL.getMaterialloc(org_code, material_loc_code);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("部门和料位重复，无法保存");
            return false;
        }
        return true;
    }

    /**
     * 料位名称回车事件
     */
    public void onMaterialAction() {
        String name = getValueString("DESCRIPTION");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getTextFormat("ORG_CODE").setEnabled(true);
        getTextField("MATERIAL_LOC_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags =
            "ORG_CODE;MATERIAL_LOC_CODE;DESCRIPTION;PY1;PY2;REMARK;CORRSUPDEPT";
        clearValue(tags);
        // 最大号+1(SEQ)
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        TTable table = getTable("TABLE");
        table.setSelectionMode(0);
        action = "save";
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "ORG_CODE;MATERIAL_LOC_CODE;DESCRIPTION;PY1;PY2;REMARK;SEQ;CORRSUPDEPT";
            this.setValueForParm(likeNames, parm);
            getTextFormat("ORG_CODE").setEnabled(false);
            getTextField("MATERIAL_LOC_CODE").setEnabled(false);
            action = "save";
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String org = getValueString("ORG_CODE");
        String material = getValueString("MATERIAL_LOC_CODE");
        String filterString = "";
        if (org.length() > 0 && material.length() > 0) {
            filterString = "ORG_CODE = '" + org
                + "' AND MATERIAL_LOC_CODE like '" + material + "%'  ";
        }
        else if (org.length() > 0) {
            filterString = "ORG_CODE = '" + org + "'  ";
        }
        else if (material.length() > 0) {
            filterString = "MATERIAL_LOC_CODE like '" + material + "%'  ";
        }
        else {
            filterString = "";
        }
        
        String corrSupDept = getValueString("CORRSUPDEPT");
        if( corrSupDept.length()>0 ){
        	
        	if(filterString!="" && filterString.length()>0){
        		filterString = filterString + " AND CORRSUPDEPT = '"+corrSupDept+"' ";
        	}else{
        		filterString = " CORRSUPDEPT = '"+corrSupDept+"' ";
        	}
        	
        }
        
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
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
        this.onClear() ;
        table.setDSValue();
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
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
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
    private int getMaxSeq(TDataStore dataStore, String columnName,
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
