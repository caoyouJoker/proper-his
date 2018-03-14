package com.javahis.ui.med;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

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
 * @version 1.0
 */
public class MEDPatInfoControl extends TControl {
    /**
     * table
     */
    private static String TABLE="TABLE";
    /**
     * 初始化方法
     */
    public void onInit(){
        //TABLE1双击事件
      callFunction("UI|" + TABLE + "|addEventListener",
                   TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");

        Object obj=this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            String admType = parm.getValue("ADM_TYPE");
            if("O".equals(admType)){
                this.getTTable(TABLE).setHeader("就诊日,160,Timestamp,yyyy/MM/dd HH:mm:ss;就诊号,150;诊区,150,CLINICAREA_CODE;诊室,150,CLINICROOM_NO;病案号,80");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;CLINICAREA_CODE;CLINICROOM_NO;MR_NO");
            }
            if("E".equals(admType)){
                this.getTTable(TABLE).setHeader("就诊日,160,Timestamp,yyyy/MM/dd HH:mm:ss;就诊号,150;诊区,150,CLINICAREA_CODE;诊室,150,CLINICROOM_NO;病案号,80");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;CLINICAREA_CODE;CLINICROOM_NO;MR_NO");
            }
            if("I".equals(admType)){
                this.getTTable(TABLE).setHeader("就诊日,160,Timestamp,yyyy/MM/dd HH:mm:ss;就诊号,150;科室,150,DEPT_CODE;病区,150,STATION_CODE;病案号,80");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;DEPT_CODE;STATION_CODE;MR_NO");
            }
            if("H".equals(admType)){
                this.getTTable(TABLE).setHeader("就诊日,160,Timestamp,yyyy/MM/dd HH:mm:ss;就诊号,150;科室,150,DEPT_CODE;病案号,80");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;DEPT_CODE;MR_NO");
            }
            this.getTTable(TABLE).setParmValue(parm);
        }
    }
    /**
     * 双击
     */
    public void onTableDoubleClicked(int row){
        this.setReturnValue(this.getTTable(TABLE).getParmValue().getRow(row));
        this.closeWindow();
    }
    /**
     * 得到TABLE对象
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
}
