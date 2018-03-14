package com.javahis.ui.onw;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import com.dongyang.ui.TTable;
import jdo.opd.OrderTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 门诊护士 皮试执行</p>
 *
 * <p>Description: 门诊护士 皮试执行</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-03-11
 * @version 4.0
 */
public class ONWNSExecControl
    extends TControl {
    private TParm data;//记录历史数据 用于对比是否修改
    private TTable table;
    private Timestamp dateNow;//记录系统当前时间
    public void onInit(){
        super.onInit();
        data = new TParm();
        table = (TTable)this.getComponent("TABLE");
        callFunction("UI|TABLE|addEventListener",
                                TTableEvent.CHECK_BOX_CLICKED, this,
                                "onCellChange");
        pageInit();
        dateInit();
        onQuery();
    }
    /**
     * 页面信息初始化
     */
    public void pageInit(){
        TParm parm = (TParm)this.getParameter();
        //虚拟参数
//        TParm parm = new TParm();
//        parm.setData("MR_NO","000000000340");
//        parm.setData("CASE_NO","100113000024");
        this.setValue("MR_NO",parm.getValue("MR_NO"));
        this.setValue("CASE_NO",parm.getValue("CASE_NO"));
    }
    /**
     * 日期控件初始化
     */
    public void dateInit(){
        String DATE = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
        this.setValue("DATE_S",StringTool.getTimestamp(DATE+"000000","yyyyMMddHHmmss"));
        this.setValue("DATE_E",StringTool.getTimestamp(DATE+"235959","yyyyMMddHHmmss"));
    }
    /**
     * 查询
     */
    public void onQuery(){
        dateNow = SystemTool.getInstance().getDate();
        TParm parm = new TParm();
        parm.setData("DATE_S",this.getValue("DATE_S"));
        parm.setData("DATE_E",this.getValue("DATE_E"));
        if(this.getValueString("MR_NO").length()>0){
            Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
            if(pat==null){
                this.messageBox("E0008");
                return;
            }
            this.setValue("MR_NO",pat.getMrNo());
            parm.setData("MR_NO", pat.getMrNo());
        }
        if(this.getValueString("CASE_NO").length()>0){
            parm.setData("CASE_NO",this.getValueString("CASE_NO"));
        }
        if(this.getValueBoolean("checkYES")){
            parm.setData("PS_FLG_Y","");
            this.callFunction("UI|save|setEnabled",false);
            table.setLockColumns("all");
        }
        if(this.getValueBoolean("checkNO")){
            parm.setData("PS_FLG_N","");
            this.callFunction("UI|save|setEnabled",true);
            table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,13,14,15,16");
        }
        if(this.getValueBoolean("checkAll")){
            table.setLockColumns("all");
        }
        data = OrderTool.getInstance().selectPS(parm);
        if(data.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(data.getCount()<=0){
            this.messageBox("E0008");
            table.removeRowAll();
        }
        this.clearValue("exeALL");
        setTableDate();
    }
    /**
     * table赋值
     * @param parm TParm
     */
    private void setTableDate(){
        for(int i=0;i<data.getCount("CASE_NO");i++){
            //判断是否已经执行
            if(data.getValue("NS_EXEC_DATE",i).length()>0)
                data.setData("PS_FLG",i,"Y");
            else
                data.setData("PS_FLG",i,"N");
        }
        table.setParmValue(data);
    }
    /**
     * 单元格值改变事件
     */
    public void onCellChange(Object obj) {
        table.acceptText();
        int row = table.getSelectedRow();
        if (table.getItemString(row,"PS_FLG").toString().equals("Y")) {
            table.setItem(row, "NS_EXEC_CODE", Operator.getID());
            table.setItem(row, "NS_EXEC_DEPT", Operator.getDept());
            table.setItem(row, "NS_EXEC_DATE", dateNow);
        }
        else {
            table.setItem(row, "NS_EXEC_CODE", "");
            table.setItem(row, "NS_EXEC_DEPT", "");
            table.setItem(row, "NS_EXEC_DATE", "");
            table.setItem(row, "NS_NOTE", "");
        }
    }
    /**
     * 保存
     */
    public void onSave(){
        if(table.getRowCount()==0)
            return;
        table.acceptText();
        for(int i=0;i<data.getCount("CASE_NO");i++){
            if(data.getBoolean("PS_FLG",i)){
                TParm parm = new TParm();
                parm.setData("CASE_NO",data.getValue("CASE_NO",i));
                parm.setData("RX_NO",data.getValue("RX_NO",i));
                parm.setData("SEQ_NO",data.getValue("SEQ_NO",i));
                parm.setData("NS_NOTE",data.getValue("NS_NOTE",i));
                parm.setData("NS_EXEC_CODE",data.getValue("NS_EXEC_CODE",i));
                parm.setData("NS_EXEC_DEPT",data.getValue("NS_EXEC_DEPT",i));
                TParm re = OrderTool.getInstance().updatePS(parm);
                if(re.getErrCode()<0){
                    this.messageBox("E0005");
                    onQuery();
                    return;
                }
            }
        }
        this.messageBox("P0005");
        onQuery();
    }
    /**
     * 清空
     */
    public void onClear(){
        this.dateInit();
        this.clearValue("MR_NO;CASE_NO;exeALL");
        table.removeRowAll();
    }
    /**
     * 全选事件
     */
    public void onCheck(){
        String flg = "N";
        if(this.getValueBoolean("exeALL"))
            flg = "Y";
        for(int i=0;i<table.getRowCount();i++){
            table.setItem(i,"PS_FLG",flg);
            if("Y".equals(flg)){
                table.setItem(i, "NS_EXEC_CODE", Operator.getID());
                table.setItem(i, "NS_EXEC_DEPT", Operator.getDept());
                table.setItem(i, "NS_EXEC_DATE", dateNow);
            }
            else {
                table.setItem(i, "NS_EXEC_CODE", "");
                table.setItem(i, "NS_EXEC_DEPT", "");
                table.setItem(i, "NS_EXEC_DATE", "");
                table.setItem(i, "NS_NOTE", "");
            }
        }
    }
}
