package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0PatInfoControl
 */
public class PatInfoControl extends TControl {
    private static String TABLE = "TABLEPAT";
    private boolean manyData = false;
    private String flg;
    public void onInit(){
         super.onInit();
         //注册Table点击事件
         callFunction("UI|" + TABLE + "|addEventListener",
                    TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this, "onDoubleTableClicked");
       Object obj = this.getParameter();
       if(obj!=null){
           TParm parm = (TParm)obj;
           this.initPage(parm);
       }
    }
    public void initPage(TParm parm){
        //System.out.println("parm"+parm);
        if (null != parm.getValue("FLG") && parm.getValue("FLG").equals("Y"))
            flg = parm.getValue("FLG");
        String systemCode = parm.getValue("SYSTEM_CODE");
        if(systemCode.length()!=0){
            if("ODI".equals(systemCode)){
                this.getTTable(TABLE).setParmValue(parm);
                this.manyData = true;
                return;
            }
        }
        this.getTTable(TABLE).setParmValue(parm);
//        this.getTTable(TABLE).addRow(parm);
    }
    /**
     * TABLE双击事件
     * @param row int
     */
    public void onDoubleTableClicked(int row){
        TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
        if (this.manyData) {
            parm.setData("FLG",flg);
            this.setReturnValue(parm);
        }else {
            parm.setData("FLG",flg);
            this.setReturnValue(parm);
        }
        this.closeWindow();
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
   /**
    * 确定
    */
   public void onCloseUI(){
       if(this.getTTable(TABLE).getSelectedRow()<0){
            this.setReturnValue(false);
       }else{
           TParm  parm = this.getTTable(TABLE).getParmValue().getRow(this.getTTable(TABLE).getSelectedRow());
           if(this.manyData){
                parm.setData("FLG",flg);
               this.setReturnValue(parm);
           }else{
                parm.setData("FLG",flg);
               this.setReturnValue(parm);
           }
       }
       this.closeWindow();
   }
}
