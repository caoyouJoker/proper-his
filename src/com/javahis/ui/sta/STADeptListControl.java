package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sta.STADeptListTool;
import java.util.Vector;

import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 中间档部门列表</p>
 *
 * <p>Description: 中间档部门列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangk 2009-4-21
 * @version JavaHis 1.0
 */
public class STADeptListControl extends TControl {
    TParm data;
    int selectRow = -1;
    int  count=0;
    public void onInit() {
        super.onInit();
        showTextFormat();
        ((TTable) getComponent("TABLE")).addEventListener("TABLE->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
        onQuery(); 
        setSEQ();
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop


    }

    /**
     * 增加对Table的监听
     * @param row
     * int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
                "DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;PY1;STATION_CODE;REGION_CODE",//====pangben modify 20110519
                data, row);
        selectRow = row;
        // 不可编辑
        ((TTextField) getComponent("DEPT_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * 显示门急诊、住院combo数据
     * ============pangben modify 20110519
     */
    public void showTextFormat(){
        //门急诊科室
        TTextFormat OE_DEPT_CODE = (TTextFormat)this.getComponent(
                "OE_DEPT_CODE");
        //住院科室
        TTextFormat IPD_DEPT_CODE = (TTextFormat)this.getComponent(
                "IPD_DEPT_CODE");
//        //病区
//        TTextFormat STATION_CODE=(TTextFormat)this.getComponent(
//                "STATION_CODE");
        //区域条件添加
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            OE_DEPT_CODE.setPopupMenuSQL(OE_DEPT_CODE.getPopupMenuSQL()+" AND REGION_CODE='" +
                                            Operator.getRegion() + "'");
            IPD_DEPT_CODE.setPopupMenuSQL(OE_DEPT_CODE.getPopupMenuSQL()+" AND REGION_CODE='" +
                                            Operator.getRegion() + "'");
//            STATION_CODE.setPopupMenuFilter("REGION_CODE='" +
//                                             Operator.getRegion() + "'");
        }
        OE_DEPT_CODE.onQuery();
        IPD_DEPT_CODE.onQuery();
//        STATION_CODE.onQuery();
    }
    /**
     * 新增
     */
    public void onInsert() {
        if (!emptyTextCheck("DEPT_CODE,DEPT_DESC")) {
            this.messageBox_("请填写科别代码和科室名称！");
            return;
        }
        count++;
        TParm parm = getParmForTag("DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;STATION_CODE;PY1;REGION_CODE");//========pangben modify 20110519
        parm.setData("STA_SEQ",  StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmssSSS"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        //==========pangben modify 20110623 start
        TParm result = STADeptListTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
//        // 显示新增数据
//        int row =((TTable) getComponent("TABLE"))
//                .addRow(
//                        parm,
//                        "REGION_CHN_DESC;DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;STATION_CODE;PY1;STATION_CODE;REGION_CODE");//========pangben modify 20110519
////        int row = data.insertRow();
//        data.setRowData(row, parm);
        clearValue("DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;STATION_CODE;PY1;REGION_CODE");
        //========pangben modify 20110519
        this.setValue("REGION_CODE",Operator.getRegion());
        setSEQ();//设置最大序号
        this.messageBox_("添加成功！");
    }

    /**
     * 更新
     */
    public void onUpdate() {
    	((TTable) getComponent("TABLE")).acceptText();
        // 选中行
        int selrow = ((TTable) getComponent("TABLE")).getSelectedRow();
        TParm selParm= ((TTable) getComponent("TABLE")).getParmValue().getRow(selrow);
        TParm parm = getParmForTag("DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;STATION_CODE;PY1;REGION_CODE");//========pangben modify 20110519
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("STA_SEQ", selParm.getValue("STA_SEQ"));
        TParm result = STADeptListTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // 选中行
        int row = ((TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ((TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox_("修改成功！");
    }

    /**
     * 保存
     */
    public void onSave() {
//        if(!checkData())
//            return;
        if (selectRow == -1) {
            onInsert();
            onQuery();
            return;
        }
        onUpdate();
        onQuery();
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (selectRow == -1){
            this.messageBox_("请选中要删除的行！");
            return;
        }
        if (this.messageBox("提示", "是否删除", 2) == 0) {
        	((TTable) getComponent("TABLE")).acceptText();
            // 选中行
            int selrow = ((TTable) getComponent("TABLE")).getSelectedRow();
            TParm selParm= ((TTable) getComponent("TABLE")).getParmValue().getRow(selrow);
            String staSeq=selParm.getValue("STA_SEQ");
            TParm result = STADeptListTool.getInstance().deletedata(staSeq,Operator.getRegion());//=========pangben modify 20110525
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
            TTable table = ((TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            // 删除单行显示
            table.removeRow(row);
//            if (row == table.getRowCount())
//                table.setSelectedRow(row - 1);
//            else
//                table.setSelectedRow(row);
            this.messageBox_("删除成功！");
            onClear();
            onQuery();
            setSEQ();
        } else {
            return;
        }

    }

    /**
     * 查询
     */
    public void onQuery() {
        String dept_code = getValueString("DEPT_CODE");
        //========pangben modify 20110519 start 添加参数
        data = STADeptListTool.getInstance().selectdata(dept_code,this.getValueString("REGION_CODE"));
        //========pangben modify 20110519 stop
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox_(data.getErrText());
            return;
        }
        ((TTable) getComponent("TABLE")).setParmValue(data);
        selectRow = -1;
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("DEPT_CODE;DEPT_DESC;DEPT_LEVEL;SEQ;OE_DEPT_CODE;IPD_DEPT_CODE;STATION_CODE;PY1");
        ((TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ((TTextField) getComponent("DEPT_CODE")).setEnabled(true);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        //==============pangben modify 20110519
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * 设置最大排序号 SEQ
     */
    public void setSEQ() {
        long seq = 0;
        // 取SEQ最大值
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("SEQ", seq + 1);
        }
    }
    /**
     * 根据汉字输出拼音首字母
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("DEPT_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("DEPT_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // 光标下移
        ((TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
    /**
     * 审核数据
     * @return boolean
     */
    private boolean checkData(){
        //如果填写了 病区 必须填写 科室的code
        if(!"".equals(this.getValueString("IPD_DEPT_CODE"))){
            if("".equals(this.getValueString("OE_DEPT_CODE"))){
                this.messageBox_("请填写门急诊科室代码");
                this.grabFocus("OE_DEPT_CODE");
                return false;
            }
        }
        return true;
    }
}
