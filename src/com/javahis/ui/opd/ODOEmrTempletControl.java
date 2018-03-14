package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.config.TConfig;

/**
 * <p>Title: 主诉模板</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 20110427
 * @version 1.0
 */
public class ODOEmrTempletControl
    extends TControl {
    public ODOEmrTempletControl() {
    }

    private String dr_code = "";
    private String dept_code = "";
    private String adm_type = "";
    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        TParm parm = (TParm)this.getParameter();
        dr_code = parm.getValue("DR_CODE");
        dept_code = parm.getValue("DEPT_CODE");
        adm_type = parm.getValue("ADM_TYPE");
        table = getTable("TABLE");
        onChangeRadioButton();
    }

    /**
     *
     */
    public void onChangeRadioButton() {
        TParm parm = new TParm();
        String sql = "";
        if (getRadioButton("RadioButtonA").isSelected()) {
            if (adm_type.equals("O")) {
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                    + " AND DEPTORDR_CODE = '" + dr_code +
                    "' AND B.OPD_FLG = 'Y' "
                    + " ORDER BY MAIN_FLG DESC ";
            }
            else {
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME  FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                    + " AND DEPTORDR_CODE = '" + dr_code +
                    "' AND B.EMG_FLG = 'Y' "
                    + " ORDER BY MAIN_FLG DESC ";
            }
            //// System.out.println("1------" + sql);
            parm = new TParm(TJDODBTool.getInstance().select(sql));
            table.setParmValue(parm);
        }
        else if (getRadioButton("RadioButtonB").isSelected()) {
            if (adm_type.equals("O")) {
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                    + " AND DEPTORDR_CODE = '" + dept_code +
                    "' AND B.EMG_FLG = 'Y' "
                    + " ORDER BY MAIN_FLG DESC ";
            }
            else {
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                    + " AND DEPTORDR_CODE = '" + dept_code +
                    "' AND B.EMG_FLG = 'Y' "
                    + " ORDER BY MAIN_FLG DESC ";
            }
            //// System.out.println("2------" + sql);
            parm = new TParm(TJDODBTool.getInstance().select(sql));
            table.setParmValue(parm);
        }
        else {
            parm.addData("TEMPLET_PATH",
                         TConfig.getSystemValue("ODOEmrTempletZSPath"));
            parm.addData("EMT_FILENAME",
                         TConfig.getSystemValue("ODOEmrTempletZSFileName"));
            parm.addData("SUBCLASS_CODE",
                         TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE"));
            parm.addData("SUBCLASS_DESC",
                         TConfig.getSystemValue("ODOEmrTempletZSFileName"));
            table.setParmValue(parm);
        }
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

    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     *
     */
    public void onOk() {
        int row = table.getSelectedRow();
        if(row  < 0){
            this.messageBox("请选择模板！");
            return;
        }
        this.setReturnValue(table.getParmValue().getRow(row));
        this.closeWindow();
    }

    /**
     *
     */
    public void onCancel(){
        this.closeWindow();
    }
}
