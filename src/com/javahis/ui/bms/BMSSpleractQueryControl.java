package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.data.TParm;
import jdo.bms.BMSQueryPatTool;

/**
 * <p>
 * Title: 输血反应查询
 * </p>
 *
 * <p>
 * Description: 输血反应查询
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
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSSpleractQueryControl extends TControl {
    public BMSSpleractQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            String mr_no = parm.getValue("MR_NO");
            TParm inparm = new TParm();
            inparm.setData("MR_NO", mr_no);
            // 门急诊信息
            TParm resultOE = BMSQueryPatTool.getInstance().onQueryOE(inparm);
            // 住院信息
            TParm resultI = BMSQueryPatTool.getInstance().onQueryI(inparm);

            TParm parmValue = new TParm();
            if (resultOE.getCount() > 0) {
                for (int i = 0; i < resultOE.getCount(); i++) {
                    parmValue.addData("DEPT_CODE",
                                      resultOE.getValue("DEPT_CODE", i));
                    parmValue.addData("STATION_CODE", "");
                    parmValue.addData("MR_NO", resultOE.getValue("MR_NO", i));
                    parmValue.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
                    parmValue.addData("IPD_NO", "");
                }
            }

            if (resultI.getCount() > 0) {
                for (int i = 0; i < resultI.getCount(); i++) {
                    parmValue.addData("DEPT_CODE",
                                      resultI.getValue("DEPT_CODE", i));
                    parmValue.addData("STATION_CODE",
                                      resultI.getValue("STATION_CODE", i));
                    parmValue.addData("MR_NO", resultI.getValue("MR_NO", i));
                    parmValue.addData("CASE_NO", resultI.getValue("CASE_NO", i));
                    parmValue.addData("IPD_NO", resultI.getValue("IPD_NO", i));
                }
            }

            getTable("TABLE").setParmValue(parmValue);
        }
    }

    /**
     * 返回方法
     */
    public void onReturn() {
        TTable table = this.getTable("TABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("没有选中项");
            return;
        }
        this.setReturnValue(table.getParmValue().getRow(table.getSelectedRow()));
        this.closeWindow();
    }

    /**
    * 得到Table对象
    *
    * @param tagName
    *            元素TAG名称
    * @return
    */
   private TTable getTable(String tagName) {
       return (TTable) getComponent(tagName);
   }

}
