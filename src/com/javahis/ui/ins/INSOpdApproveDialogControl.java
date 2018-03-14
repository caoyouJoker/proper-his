package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.ins.INSOpdApproveTool;
/**
 *
 * <p>Title:门诊医保审核就诊号控制类 </p>
 *
 * <p>Description:门诊医保审核就诊号控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class INSOpdApproveDialogControl extends TControl {
    private TParm approve;
    int selectrow = -1;
    /**
     * 初始化界面
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //得到前台传来的数据并显示在界面上
        TParm approve = (TParm) getParameter();
        if (!approve.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;ADM_DATE;SEX_CODE", approve.getParm("PATINFO"),
                            0);
            callFunction("UI|TABLE|setParmValue", approve.getParm("PATINFO"));
        }
        if (approve.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;ADM_DATE;SEX_CODE", approve, 0);
        }
        //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1的双击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        //默认Table上显示当天挂号记录
        //    onQuery();
    }

    /**
     *监听Table的单击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
        selectrow = row;
    }
    /**
     * 监听Table的双击击事件
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue((String) data.getData("CASE_NO", row));
        this.callFunction("UI|onClose");
    }

    /**
     * 查询
     */
    public void onQuery() {

        TParm result = INSOpdApproveTool.getInstance().selPatInfo(approve.getParm("PATINFO"));
        if (result.getErrCode() < 0)
            return;
        if (result.getCount() == 0)
            this.messageBox("无挂号信息");
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * 确认按钮事件
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue((String) data.getData("CASE_NO", selectrow));
        this.callFunction("UI|onClose");
    }
}
