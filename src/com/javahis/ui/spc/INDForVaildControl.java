package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import jdo.spc.INDSQL;

/**
 * <p>Title: 批号效期查询Control</p>
 *
 * <p>Description: 批号效期查询Control</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2009.05.23
 * @version 1.0
 */
public class INDForVaildControl
    extends TControl {

    private TParm parm;
    // 查询部门
    private String org_code;
    // 查询药品
    private String order_code;
    // 单位类型
    private String unit_type;

    public INDForVaildControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 取得传入参数
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            org_code = parm.getValue("ORG_CODE");
            order_code = parm.getValue("ORDER_CODE");
            unit_type = parm.getValue("UNIT_TYPE");
        }
        // 初始画面数据
        initPage();
    }

    /**
     * 传回方法
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        if (table.getSelectedRow() < 0) {
            this.messageBox("没有选中行");
            return;
        }
        TParm result = table.getParmValue().getRow(table.getSelectedRow());
        //System.out.println("---"+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * 双击选项返回数据
     */
    public void onDoubleClickedAction() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        if (table.getSelectedRow() < 0) {
            this.messageBox("没有选中行");
            return;
        }
        TParm result = table.getParmValue().getRow(table.getSelectedRow());
        //System.out.println("---"+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 查询信息
        String sql = INDSQL.getOrderBatchNoValid(org_code, order_code, unit_type);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("药品信息错误");
            return;
        }
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有药品信息");
            return;
        }
        // 填充TABLE
        getTable("TABLE").setParmValue(result);
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


}
