package com.javahis.ui.ope;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.ope.OPEOpBookTool;
import com.dongyang.ui.TTable;
import jdo.ope.OPEOpDetailTool;

/**
 * <p>Title: 手术预约及相关手术记录列表</p>
 *
 * <p>Description: 手术预约及相关手术记录列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 4.0
 */
public class OPEOpBookChooseControl
    extends TControl {
    private String MR_NO = "";
    private String OPBOOK_SEQ = "";
    private String CASE_NO = "";
    private String FLG = "close";//返回状态 默认为关闭
    public void onInit(){
        super.onInit();
        PageParmInit();
        DataInit();
    }
    /**
     * 页面参数初始化
     */
    private void PageParmInit(){
        //接参  手术预约单号
        Object obj = this.getParameter();
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
        }else{
            return;
        }
        MR_NO = parm.getValue("MR_NO");
        OPBOOK_SEQ = parm.getValue("OPBOOK_SEQ");
        CASE_NO = parm.getValue("CASE_NO");
    }
    /**
     * 数据初始化
     */
    private void DataInit(){
        TParm opBook = new TParm();
        if(MR_NO.length()>0){
            opBook.setData("MR_NO",MR_NO);
        }else if(OPBOOK_SEQ.length()>0){
            opBook.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        }
        if(CASE_NO.length()>0){
            opBook.setData("CASE_NO",CASE_NO);
        }
        TParm result = OPEOpBookTool.getInstance().selectOpBook(opBook);
        TTable table1 = (TTable)this.getComponent("Table1");
        table1.setParmValue(result);
    }
    /**
     * 回传事件
     */
    public void onBack(){
        TTable table1= (TTable)this.getComponent("Table1");
        TTable table2= (TTable)this.getComponent("Table2");
        int row1 = table1.getSelectedRow();
        int row2 = table2.getSelectedRow();
        TParm reParm = new TParm();
        //如果选中手术预约单号 没有选中手术记录单号 那么表示 新建手术记录
        if(row1>-1&&row2<0){
            int re = this.messageBox("提示","E0098",0);
            //新建
            if(re==0){
                FLG = "new";
                reParm.setData("FLG","new");//返回状态
                reParm.setData("OPBOOK_SEQ",table1.getValueAt(row1,0));//回传手术预约单号
                this.setReturnValue(reParm);
                this.closeWindow();
            }else if(re==1){//不新建

            }
        }
        if(row2>=0){
            FLG = "update";
            reParm.setData("FLG","update");//返回状态
            reParm.setData("OPBOOK_SEQ",table1.getValueAt(row1,0));//回传手术预约单号
            reParm.setData("OP_RECORD_NO",table2.getValueAt(row2,0));//回传手术记录单号
            this.setReturnValue(reParm);
            this.closeWindow();
        }
    }
    /**
     * 手术预约记录表点击事件
     */
    public void onTable1Selected(){
        TTable table= (TTable)this.getComponent("Table1");
        int row = table.getSelectedRow();
        String opBookNo = table.getValueAt(row,0).toString();
        TParm parm = new TParm();
        parm.setData("OPBOOK_NO",opBookNo);
        TParm result = OPEOpDetailTool.getInstance().selectData(parm);
        TTable table2 = (TTable)this.getComponent("Table2");
        table2.setParmValue(result);
    }
    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing(){
        //如果没有选择新建或者修改信息 那么返回“关闭”状态 直接关闭父窗口
        if(FLG.equals("close")){
            TParm parm = new TParm();
            parm.setData("FLG",FLG);
            this.setReturnValue(parm);
        }
        return true;
    }
}
