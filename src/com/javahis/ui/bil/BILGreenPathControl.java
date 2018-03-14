package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import java.util.Vector;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;
import jdo.bil.BILGreenPathTool;
import jdo.sys.Pat;
import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import javax.swing.SwingUtilities;
import com.dongyang.manager.TIOM_AppServer;
import jdo.adm.ADMTool;

/**
 * <p>绿色通道 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author JiaoY 20090428
 * @version 1.0
 */
public class BILGreenPathControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    TParm accptDate = new TParm();
    public void onInit() {
        super.onInit();
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("APPLY_DATE", now); //预定日期
        setValue("APPROVE_DATE", now); //预定日期
        setValue("APPLY_USER", Operator.getID());
        setValue("APPROVE_USER",Operator.getID());
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            accptDate = (TParm) obj;
        }
//        //测试参数
//        accptDate.setData("ADM_TYPE", "O");
//        accptDate.setData("MR_NO", "000000000099");
//                accptDate.setData("ADM_TYPE","O");
        setValue("ADM_TYPE", accptDate.getData("ADM_TYPE"));
        if (accptDate.getData("MR_NO") == null ||
            "".equals(accptDate.getData("MR_NO")))
            setValue("MR_NO", "");
        else {
            String mr_no = accptDate.getData("MR_NO").toString();
            this.setValue("MR_NO", mr_no);
            onMrNo();
        }
    }

    /**
     * 增加对Table的监听
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
            "ADM_TYPE;CASE_NO;IPD_NO;MR_NO;PAT_NAME;APPLY_DATE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_RSN;CANCLE_FLG;APPROVE_DATE;APPROVE_AMT;APPROVE_USER",
            data, row);
        selectRow = row;
        // 不可编辑
        ( (TTextField) getComponent("MR_NO")).setEnabled(false);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 病案号查询
     */
    public void onMrNo() {
        Pat pat = new Pat();
        String mrNo = getValue("MR_NO").toString().trim();
        pat = pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("查无病患！");
            return;
        }

        TParm parm = new TParm();
        parm.setData("MR_NO", pat.getMrNo());
        TParm check = ADMInpTool.getInstance().checkAdmInp(parm);
        if (check.getData("IPD_NO", 0) == null ||
            "".equals(check.getData("IPD_NO", 0))) {
            this.messageBox("此病患不在院！");
            this.onClear();
            return;
        }
        TParm queryCaseNo = new TParm();
        queryCaseNo.setData("MR_NO", pat.getMrNo());
        queryCaseNo.setData("IPD_NO", pat.getIpdNo());
        TParm caseNo = ADMInpTool.getInstance().queryCaseNo(queryCaseNo);
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("IPD_NO", caseNo.getData("IPD_NO", 0));
        this.setValue("CASE_NO", caseNo.getData("CASE_NO", 0));
        this.setValue("PAT_NAME", pat.getName());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    onQuery();
                }
                catch (Exception e) {
                }
            }
        });
        this.grabFocus("APPLY_AMT");
        // 不可编辑
        ( (TTextField) getComponent("MR_NO")).setEnabled(false);
    }

    /**
     * 新增
     */
    public void onInsert() {
        TParm parm = new TParm();
        parm.setData("CASE_NO",this.getValueString("CASE_NO"));
        parm.setData("MR_NO",this.getValueString("MR_NO"));
        parm.setData("IPD_NO",this.getValueString("IPD_NO"));
        parm.setData("PAT_NAME",this.getValueString("PAT_NAME"));
        parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));
        parm.setData("APPLY_AMT",this.getValueString("APPLY_AMT"));
        parm.setData("APPLY_USER",this.getValueString("APPLY_USER"));
        parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION"));
        parm.setData("APPLY_RSN",this.getValueString("APPLY_RSN"));
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.BILGreenPathAction",
            "insertData", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //校验是否停止划价
        TParm checkStopFee = ADMTool.getInstance().checkStopFee(this.
            getValueString("CASE_NO"));
        if (checkStopFee.getErrCode() < 0) {
            err(checkStopFee.getErrCode() + " " + checkStopFee.getErrText());
            return;
        }
        onMrNo();
        this.messageBox("P0001");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TTable table = (TTable) getComponent("TABLE");
        int row = table.getSelectedRow();
        //判断是否已经作废
        if(table.getValueAt(row,11).toString().equals("Y")){
            this.messageBox_("已经作废不可修改");
            return;
        }
        TParm parm = getParmForTag("CASE_NO;MR_NO;IPD_NO;PAT_NAME;ADM_TYPE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_RSN");
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = BILGreenPathTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox("P0005");
    }

    /**
     * 保存
     */
    public void onSave() {
        if(!checkSaveData()){
            return;
        }
        onInsert();
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "确认要作废该条绿色通道申请吗？", 2) == 0) {
            if (selectRow == -1)
                return;
            TParm parm = data.getRow(selectRow);
            parm.setData("APPLY_DATE",StringTool.getString(data.getTimestamp("APPLY_DATE",selectRow),"yyyyMMddHHmmss"));
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            if(!checkGreenPath(parm.getValue("CASE_NO"),parm.getDouble("APPROVE_AMT"))){
                this.messageBox_("不可作废");
                return;
            }
            TParm result = TIOM_AppServer.executeAction(
            "action.bil.BILGreenPathAction",
            "cancelGreenPath", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("P0005");
            this.clearValue("APPLY_AMT;APPLY_RSN;DESCRIPTION;APPROVE_USER");
            Timestamp now = SystemTool.getInstance().getDate();
            setValue("APPLY_DATE", now); //预定日期
            setValue("APPROVE_DATE", now); //预定日期
            setValue("APPLY_USER", Operator.getID());
            setValue("APPROVE_USER", Operator.getID());
            this.onMrNo();
            selectRow=-1;
        }
        else {
            return;
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("MR_NO", getValueString("MR_NO"));
        parm.setDataN("IPD_NO", getValueString("IPD_NO"));
        parm.setDataN("CASE_NO", getValueString("CASE_NO"));
        data = BILGreenPathTool.getInstance().selectGreenPath(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue(
            "MR_NO;IPD_NO;CASE_NO;PAT_NAME;APPLY_AMT;APPLY_RSN;DESCRIPTION");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TTextField) getComponent("MR_NO")).setEnabled(true);
        onQuery();
        setValue("APPLY_DATE", SystemTool.getInstance().getDate()); //预定日期
        setValue("APPLY_USER", Operator.getID());
    }
    /**
     * 检核保存数据
     * @return boolean
     */
    public boolean checkSaveData(){
        //检查申请日期不可为空
        if(this.getValue("APPLY_DATE")==null){
            this.messageBox_("请选择申请日期");
            this.grabFocus("APPLY_DATE");
            return false;
        }
        //检查申请费用
        if(!(this.getValueString("APPLY_AMT").length()>0&&Double.valueOf(this.getValueString("APPLY_AMT"))!=0)){
            this.messageBox_("请填写申请金额");
            this.grabFocus("APPLY_AMT");
            return false;
        }
        //检查申请人员
        if(this.getValueString("APPLY_USER").length()<=0){
            this.messageBox_("请选择申请人员");
            this.grabFocus("APPLY_USER");
            return false;
        }
        //检查批准日期不可为空
        if(this.getValue("APPROVE_DATE")==null){
            this.messageBox_("请选择批准日期");
            this.grabFocus("APPROVE_DATE");
            return false;
        }
        //检查批准费用
        if(!(this.getValueString("APPROVE_AMT").length()>0&&Double.valueOf(this.getValueString("APPROVE_AMT"))!=0)){
            this.messageBox_("请填写批准金额");
            this.grabFocus("APPROVE_AMT");
            return false;
        }
        //检查批准人员
        if(this.getValueString("APPROVE_USER").length()<=0){
            this.messageBox_("请选择批准人员");
            this.grabFocus("APPROVE_USER");
            return false;
        }
        return true;
    }
    /**
     * 查询adm_inp中的绿色通道的值 是否大于要作废的金额，如果小于要作废的金额不可作废
     * @param CASE_NO String
     * @param greenPath double
     * @return boolean  true:可以作废   false:不可作废
     */
    private boolean checkGreenPath(String CASE_NO,double greenPath){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm adm = ADMInpTool.getInstance().selectall(parm);
        if(adm.getDouble("GREENPATH_VALUE",0)>= greenPath)
            return true;
        else
            return false;
    }
}
