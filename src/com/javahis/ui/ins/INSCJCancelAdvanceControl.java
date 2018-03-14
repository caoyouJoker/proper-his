package com.javahis.ui.ins;

import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.ins.InsManager;
import jdo.ins.INSCJAdvanceTool;

/**
 * <p>Title: 撤销垫付申报</p>
 *
 * <p>Description: 撤销垫付申报</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.14
 * @version 1.0
 */
public class INSCJCancelAdvanceControl
    extends TControl {
    TParm data;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        //入院开始时间
        setValue("S_DATE", yesterday);
        //入院结束时间
        setValue("E_DATE", SystemTool.getInstance().getDate());

    }

    /**
     * 保存
     */
    public void onSave() {
        TParm result = new TParm();
        int row = getTTable("Table").getSelectedRow();
        if (row < 0) {
            this.messageBox("请点选一条数据");
            return;
        }
        TParm parm = new TParm();
        parm = getTTable("Table").getParmValue().getRow(row);
        //城乡垫付撤销申报
        this.DataDown_czys_I(parm);
        //更新INS_ADVANCE_PAYMENT医保状态
        result = INSCJAdvanceTool.getInstance().upInsStatus(parm);

        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;

        }
        else {
            this.messageBox("P0005");
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue(
                         "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue(
                         "E_DATE")), "yyyyMMdd"));
        result = INSCJAdvanceTool.getInstance().getINSIbsData(parm);

        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("ADM_SEQ") < 1) {
            //查无数据
            this.messageBox("E0008");
            this.initPage();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("MR_NO");
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 城乡垫付撤销申报
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_I(TParm parm) {
        TParm result = new TParm();
        parm.setData("PIPELINE", "DataDown_czys");
        parm.setData("PLOT_TYPE", "I");
        parm.addData("ADM_SEQ", "");
        parm.addData("HOSP_NHI_CODE", "");
        result = InsManager.getInstance().safe(parm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
}
