package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ERDBedSelControl extends TControl{

    public ERDBedSelControl() {
    }

    public void onInit() {
        super.onInit();
        initTable();
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
      * 拿到科室
      * @param deptCode String
      * @return String
      */
     public TParm getERDBed(){
         String eRDRegionSQL = "";
         if(getValueString("ERD_REGION_CODE").length() != 0)
             eRDRegionSQL = " AND ERD_REGION_CODE = '"+getValueString("ERD_REGION_CODE")+"'";
         return new TParm(getDBTool().select(" SELECT * "+
                                             " FROM ERD_BED "+
                                             " WHERE MR_NO IS NULL "+
                                             eRDRegionSQL));
    }

    /**
     * 初始化界面Table
     */
    public void initTable(){
        ((TTable)getComponent("TABLE")).setParmValue(getERDBed());
    }

    /**
     * 确定
     */
    public void onConfirm() {
        if(((TTable)getComponent("TABLE")).getSelectedRow() < 0)
            return;
        setReturnValue(((TTable) getComponent("TABLE")).getParmValue().getRow(((TTable)getComponent("TABLE")).getSelectedRow()));
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel() {
        closeWindow();
    }
}
