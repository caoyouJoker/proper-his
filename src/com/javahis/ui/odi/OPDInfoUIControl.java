package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import jdo.odo.OpdRxSheetTool;
import com.dongyang.ui.TWord;
import com.dongyang.manager.TIOM_FileServer;
import jdo.sys.Operator;
import com.dongyang.data.TNull;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import java.io.File;
import jdo.odi.OdiMainTool;
import com.javahis.ui.emr.EMRTool;

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
public class OPDInfoUIControl extends TControl {
    /**
     * TABLE1名字
     */
    private static String TABLE = "TABLE1";
    /**
     * WORD控件
     */
    private static String WORD = "WORD";
    //当前表格选中行
    private int rowIndex = -1;
    public void onInit() {
        super.onInit();
        //注册Table点击事件
        callFunction("UI|" + TABLE + "|addEventListener",
                     TABLE + "->" + TTableEvent.CLICKED, this, "onCheckTable");
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            this.getTTable(TABLE).setParmValue(parm);
        }
    }

    /**
     * TABLE双击事件
     * @param obj Object
     */
    public void onCheckTable(int row) {
        this.rowIndex = row;
        TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
        TParm opdParm = new TParm();
        opdParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        opdParm.setData("MR_NO", parm.getValue("MR_NO"));
        opdParm.setData("HOSP_NAME", "TEXT",
                        OpdRxSheetTool.getInstance().getHospFullName());
        opdParm.setData("DR_NAME", "TEXT",
                        "医师签字:" +
                        OpdRxSheetTool.getInstance().
                        getDrName(opdParm.getValue("CASE_NO")));
        opdParm.setData("REALDEPT_CODE", parm.getValue("REALDEPT_CODE"));
        this.getTWord(WORD).setWordParameter(opdParm);
        this.getTWord(WORD).setFileName(
                "%ROOT%\\config\\prt\\OPD\\OpdCaseSheet1010.jhw");
    }

    /**
     * 拿到WORD
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * TABLE名字
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    //保存EMR方法
    public void onSave() {
        if (rowIndex < 0) {
            this.messageBox("请选择数据");
            return;
        }
        if (this.getTWord(WORD) == null) {
            return;
        }
        saveEMR(this.getTWord(WORD), "门诊病历记录", "EMR020001","EMR02000106");
    }
    /**
     * 上传EMR
     * @param obj Object
     */
    private void saveEMR(TWord word, String fileName, String classCode, String subClassCode) {
        if (word == null) {
            return;
        }
        TParm parm = this.getTTable(TABLE).getParmValue().getRow(rowIndex);
        String case_no = parm.getValue("CASE_NO");
        String mr_no = parm.getValue("MR_NO");
        EMRTool emrTool = new EMRTool(case_no, mr_no, this);
        emrTool.saveEMR(word, fileName,classCode,subClassCode);
    }
}
