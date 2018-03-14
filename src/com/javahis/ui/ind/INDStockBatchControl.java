package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.ind.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: 日结过帐管理Control
 * </p>
 *
 * <p>
 * Description: 日结过帐管理Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDStockBatchControl extends TControl{

    // 全部部门权限
    //private boolean dept_flg = true;

    public INDStockBatchControl() {
    }

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
        /**
         * 权限控制
         * 权限1:一般个人只显示自已所属科室
         * 权限9:显示全院药库部门
         */
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            getComboBox("ORG_CODE").setParmValue(parm);
            //dept_flg = false;
        }

        Timestamp date = StringTool.getTimestamp(new Date());
        date = StringTool.rollDate(date, -1);
        this.setValue("TRAN_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("未过账科室不能为空");
            return;
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        //1.药品库存日结批次作业
        if (this.getCheckBox("STOCK_BATCH_FLG").isSelected()) {
            //1.1 药库参数档设定
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getINDSysParm()));
            if (parm.getValue("MANUAL_TYPE", 0) == null) {
                this.messageBox("药库参数档尚未设定");
                return;
            }
            //1.2 查询部门是否为自动过批次
            String manual_type = parm.getValue("MANUAL_TYPE", 0);
            if ("0".equals(manual_type)) {
                this.messageBox("药库参数档中设定为自动过批次,不可手工过");
                return;
            }
            //1.3 判断是否已经过批次
            parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getStockBatchByOrgCode(this.getValueString("ORG_CODE"),
                                       this.getValueString("TRAN_DATE").
                                       replaceAll("-", ""))));
            if (parm.getInt("NUM", 0) > 0) {
                this.messageBox("该部门" +
                                this.getValueString("TRAN_DATE").substring(0, 10) +
                                "日已日结!");
                return;
            }
            //1.4 药品库存日结批次作业
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", Operator.getID());
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", Operator.getIP());
            inparm.setData("TRANDATE",
                           this.getValue("TRAN_DATE"));
            TParm result = new TParm();
            result = TIOM_AppServer.executeAction(
                "action.ind.INDStockBatchAction", "onIndStockBatch", inparm);
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }

        //2.产生药品自动补充档作业
        if (this.getCheckBox("GIF_FLG").isSelected()) {
            //this.messageBox("");
        }
    }

    /**
     * 变更未过账科室
     */
    public void onChangeOrgCode() {
        if (!"".equals(getValueString("ORG_CODE"))) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getINDOrgType(this.getValueString("ORG_CODE"))));
            if ("A".equals(parm.getValue("ORG_TYPE", 0))) {
                this.getCheckBox("GIF_FLG").setSelected(false);
            }
            else {
                this.getCheckBox("GIF_FLG").setSelected(true);
            }
        }
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
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

}
