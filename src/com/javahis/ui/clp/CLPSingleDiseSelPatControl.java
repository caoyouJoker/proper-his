package com.javahis.ui.clp;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 单病种病患查询</p>
 *
 * <p>Description: 单病种病患查询</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPSingleDiseSelPatControl extends TControl {
//    TParm data;
    int selectRow = -1;
    TParm allParm;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
//        initPage();
        onClear();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
//        TParm parm = new TParm(TJDODBTool.getInstance().select(
//            "SELECT current date as DATE FROM sysibm.sysdummy1"));
//        Timestamp eDate = parm.getTimestamp("DATE", 0);
//        Timestamp sDate = StringTool.rollDate(eDate, -1);
//        setValue("S_DATE", sDate);
//        setValue("E_DATE", eDate);

    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0) {
            return;
        }
        allParm = new TParm();
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm("PAT_NAME;CASE_NO;HEIGHT;WEIGHT",
                        data, row);
        allParm = data.getRow(row);
        selectRow = row;
    }

    /**
     * 查询
     */
    public void onQuery() {
        out("begin");
        TParm parm = new TParm();
        String sDate = StringTool.getString((Timestamp) getValue("S_DATE"),
                                            "yyyy-MM-dd HH:mm:ss");
        String eDate = StringTool.getString((Timestamp) getValue("E_DATE"),
                                            "yyyy-MM-dd HH:mm:ss");
        String deptCode = getValue("DEPT_CODE").toString();
        String deptWhere = "";
        if (!(deptCode == null || deptCode.length() == 0)) {
            deptWhere = " AND  DS_DEPT_CODE='" + deptCode + "' ";
        }
        String mrNo = getValue("MR_NO").toString();
        if (mrNo != null) {
            parm.setData("MR_NO", mrNo);
        }
        String sql =
                " SELECT D.PAT_NAME,E.SEX_DESC AS SEX_CODE,C.CTZ_DESC,A.CASE_NO,A.MR_NO," +
                "        A.IN_DATE,A.DS_DATE AS OUT_DATE ,A.DS_DEPT_CODE AS DEPT_CODE," +
                "        A.DIS_DR_CODE AS DR_CODE,A.TOTAL_PAY_AMT AS AMT_PRICE,A.OPT_DATE," +
                "        A.OPT_USER AS OPT_ID,A.OPT_TERM,B.USER_NAME AS OPT_USER," +
                "        D.TEL_H1,A.WEIGHT,A.HEIGHT " +
                "   FROM ADM_INP A, SYS_OPERATOR B,SYS_CTZ C,SYS_PATINFO D,SYS_SEX E " +
                "  WHERE IN_DATE BETWEEN TIMESTAMP('" + sDate +
                "') AND TIMESTAMP('" + eDate + "') " +
                deptWhere +
                "    AND A.OPT_USER = B.USER_ID " +
                "    AND A.CTZ_CODE = C.CTZ_CODE " +
                "    AND D.SEX_CODE = E.SEX_CODE " +
                "    AND A.MR_NO = D.MR_NO " +
                " ORDER BY IN_DATE DESC";
        //SYSTEM.OUT.PRINTln("单病种" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        out("date=" + result);
        this.callFunction("UI|Table|setParmValue", result);
        out("end");

    }

    /**
     * 为EMR模板配参
     * @return TParm
     */
    public TParm getInfoValue() {
        TParm parm = new TParm();
        parm.setData("TYPE", getValue("SINGLE_DISE").toString());
        parm.setData("MR_NO", allParm.getData("MR_NO"));
        parm.setData("IN_DATE",
                     StringTool.getString(StringTool.getTimestamp(allParm.
                getValue("IN_DATE"), "yyyyMMddHHmmss"), "yyyy/MM/dd HH:mm:ss"));
        parm.setData("OUT_DATE",
                     StringTool.getString(StringTool.getTimestamp(allParm.
                getValue("OUT_DATE"), "yyyyMMddHHmmss"), "yyyy/MM/dd HH:mm:ss"));
        parm.setData("HOSP_DESC", "泰达心血管医院");
//        parm.setData("JRPRICE", allParm.getDouble("JRPRICE")==0?0.00:allParm.getDouble("JRPRICE"));
//        parm.setData("PHA_PRICE", allParm.getDouble("PHA_PRICE")==0?0.00:allParm.getDouble("PHA_PRICE"));
        parm.setData("JRPRICE", 0.00);
        parm.setData("PHA_PRICE", 0.00);
        parm.setData("AMT_PRICE",
                     allParm.getDouble("AMT_PRICE") == 0 ? 0.00 :
                     allParm.getDouble("AMT_PRICE"));
        parm.setData("DEPT_CODE",
                     allParm.getValue("DEPT_CODE") == null ? "" :
                     allParm.getValue("DEPT_CODE"));
        parm.setData("DR_CODE",
                     allParm.getValue("DR_CODE") == null ? "" :
                     allParm.getValue("DR_CODE"));
        parm.setData("CASE_NO",
                     allParm.getValue("CASE_NO") == null ? "" :
                     allParm.getValue("CASE_NO"));
        parm.setData("OPT_DATE",
                     StringTool.getString(StringTool.getTimestamp(allParm.
                getValue("OPT_DATE"), "yyyyMMddHHmmss"), "yyyy/MM/dd HH:mm:ss"));
        parm.setData("OPT_USER", allParm.getData("OPT_USER"));
        parm.setData("OPT_TERM", allParm.getData("OPT_TERM"));
        return parm;

    }

    /**
     * 调出EMR模板编辑器
     */
    public void onEditEmr() {
        if (getValue("SINGLE_DISE") == null ||
            getValue("SINGLE_DISE").toString().length() == 0) {
            this.messageBox("请选择病种类别");
            return;
        }
        //SYSTEM.OUT.PRINTln("EMR参数" + getInfoValue());
        this.openDialog("%ROOT%\\config\\clp\\CLPUI.x", getInfoValue());

    }

    /**
     *清空
     */
    public void onClear() {

        clearValue("DEPT_CODE;MR_NO;PAT_NAME;" +
                   "HEIGH;WEIGH;SINGLE_DISE");
//        initPage();
        this.callFunction("UI|Table|removeRowAll");
//        TTable table = (TTable)this.getComponent("Table");
//        table.removeAll();
        selectRow = -1;
    }
}
