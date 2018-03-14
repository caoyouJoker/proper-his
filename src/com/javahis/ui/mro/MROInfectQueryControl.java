package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sys.PatTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.mro.MROInfectTool;
import com.dongyang.ui.TTable;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: 传染病报告卡查询</p>
 *
 * <p>Description: 传染病报告卡查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-16
 * @version 1.0
 */
public class MROInfectQueryControl
    extends TControl {
    private TTable Table;
    private String TYPE = "H";//记录调用状态 H:“历史查询”按钮调用  M:当指定病患存在多条传染病记录时自动调用
    private boolean returnType = false;//记录是否选中了回传值进行回传
    public void onInit(){
        Table = (TTable)this.getComponent("Table");
        OrderList orderList = new OrderList();
        Table.addItem("OrderList",orderList);
        PageInit();
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("DATE_S",date);
        this.setValue("DATE_E",date);
        //只有text有这个方法，调用ICD10弹出框
        callFunction("UI|ICD_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //textfield接受回传值
        callFunction("UI|ICD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }
    /**
     * 根据参数 页面初始化
     */
    private void PageInit(){
        //获取调用参数
        /**
         * 参数设置：
         * MR_NO：病案号
         * TYPE：调用状态 H:“历史查询”按钮调用  M:当指定病患存在多条传染病记录时自动调用
         */
//        TParm a = new TParm();
//        a.setData("MR_NO","2211111");
//        a.setData("TYPE","H");
//        Object obj = a;
        Object obj = this.getParameter();
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
            this.setValue("MR_NO",parm.getValue("MR_NO"));
            if(parm.getValue("CASE_NO").length()>0)
                this.setValue("CASE_NO",parm.getValue("CASE_NO"));
            TYPE = parm.getValue("TYPE");
        }
        //如果是 指定病患存在多条传染病记录时自动调用的 那么不可以查询其他病患
        if("M".equals(TYPE)){
            this.callFunction("UI|MR_NO|setEnabled",false);
            this.callFunction("UI|close|setVisible",false);
            //如果CASE_NO有值 表示医生站调用传染病报告卡 CASE_NO不可修改
            if(this.getValueString("CASE_NO").trim().length()>0){
                this.callFunction("UI|CASE_NO|setEnabled",false);
                this.callFunction("UI|IPD_NO|setEnabled",false);
            }
        }
        //如果是 “历史查询”按钮调用 那么新建按钮不可 使用
        if("H".equals(TYPE)){
            this.callFunction("UI|new|setVisible",false);
        }
        queryData();
    }
    /**
     * 查询
     */
    public void onQuery(){
        queryData();
    }
    /**
     * 根据条件查询传染病报告卡信息
     */
    public void queryData(){
        TParm parm = new TParm();
        if(this.getValueString("MR_NO").trim().length()>0){
            String MR_NO = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            parm.setData("MR_NO",MR_NO);
            this.setValue("MR_NO",MR_NO);
        }
        if(this.getValueString("CASE_NO").trim().length()>0){
            parm.setData("CASE_NO",this.getValue("CASE_NO"));
        }
        if(this.getValueString("IPD_NO").trim().length()>0){
            parm.setData("IPD_NO",this.getValue("IPD_NO"));
        }
        if(this.getValue("FIRST_FLG")!=null&&!"".equals(this.getValue("FIRST_FLG"))){
            parm.setData("FIRST_FLG",this.getValue("FIRST_FLG"));
        }
        if(this.getValueString("PAT_NAME").trim().length()>0){
            parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
        }
        if(!(this.getValue("DATE_S")==null)&&!(this.getValue("DATE_E")==null)){
            parm.setData("DATE_S",this.getText("DATE_S").replace("/","")+"000000");
            parm.setData("DATE_E",this.getText("DATE_E").replace("/","")+"235959");
        }
        if(this.getValueString("ICD_CODE").length()>0){
            parm.setData("ICD_CODE",this.getValue("ICD_CODE"));
        }
        if(this.getValueString("DISEASETYPE_CODE").length()>0){
            parm.setData("DISEASETYPE_CODE",this.getValueString("DISEASETYPE_CODE"));
        }
        if(this.getValueString("ADM_TYPE").length()>0){
            parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        }
        TParm result = MROInfectTool.getInstance().selectInfect(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        Table.setParmValue(result);
    }
    /**
     * 清空
     */
    public void onClear(){
        if("H".equals(TYPE)){
            this.clearValue("MR_NO;CASE_NO");
        }
        this.clearValue("FIRST_FLG;PAT_NAME;DATE_S;DATE_E;ICD_CODE;ICD_DESC;DISEASETYPE_CODE;ADM_TYPE");
        Table.removeRowAll();
    }
    /**
     * 模糊查询（内部类）
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    /**
     * 回传选中值事件
     */
    public void onBack(){
        int rowIndex = Table.getSelectedRow();
        if(rowIndex<0){
            this.messageBox_("请选择一条报告卡信息!");
            return;
        }
        TParm result = Table.getParmValue();
        TParm parm = new TParm();
        parm.setData("MR_NO",result.getValue("MR_NO",rowIndex));
        parm.setData("CASE_NO",result.getValue("CASE_NO",rowIndex));
        parm.setData("CARD_SEQ_NO",result.getValue("CARD_SEQ_NO",rowIndex));
        parm.setData("SAVE_FLG","UPDATE");//返回保存状态 为 修改
        this.setReturnValue(parm);
        returnType = true;//表示选中了回传值
        this.closeWindow();
    }
    /**
     * 新建按钮事件
     */
    public void onNew(){
        TParm parm = new TParm();
        parm.setData("SAVE_FLG","NEW");//返回保存状态 为  新建
        this.setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * 窗口关闭时激发
     * @return boolean
     */
    public boolean onClosing(){
        if(!returnType){//如果没有选中回传值
            //并且是医生站调用 查询指定病患的时候 返回保存状态为 新增
            if ("M".equals(TYPE)) {
                TParm parm = new TParm();
                parm.setData("SAVE_FLG", "NEW"); //返回保存状态 为  新建
                this.setReturnValue(parm);
            }
        }
        return true;
    }
    /**
     * 诊断事件
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ICD_CODE", parm.getValue("ICD_CODE"));
        this.setValue("ICD_DESC", parm.getValue("ICD_CHN_DESC"));
    }
}
