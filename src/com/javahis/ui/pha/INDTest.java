package com.javahis.ui.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;

/**
 * <p>Title:门急诊医嘱配药确认单 </p>
 *
 * <p>Description: 门急诊医嘱配药确认单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INDTest
    extends TControl {
    public INDTest() {
    }

    //CASE_NO
  //   String CASE_NO = "100416000015";
  //  String CASE_NO = "100526000047";//
    /**
     *  打印方法
     */
    public void onPrint(String CASE_NO) {
        TParm parm = onQuery(CASE_NO);
        String Date = StringTool.getString(parm.getTimestamp("ORDER_DATE",0),"yyyy/MM/dd"); //parm.getValue("ORDER_DATE",0);
        String INDT = parm.getValue("COUNTER_NO",0);
        String INDNO = parm.getValue("PRINT_NO",0);
        String Name = parm.getValue("PAT_NAME",0);
        String Sex = parm.getValue("CHN_DESC",0);
        String Age = parm.getValue("AGE",0) + "岁";
        String kb = parm.getValue("DEPT_CHN_DESC",0);
        String Room = parm.getValue("USER_NAME",0);
        if (parm != null) {
            TParm date = new TParm();
            // 表头数据
            date.setData("INDT", "TEXT","领药台："+INDT);
            date.setData("INDNO", "TEXT","领药号："+INDNO);
            date.setData("Name", "TEXT", Name);
            date.setData("Sex", "TEXT", Sex);
            date.setData("Age", "TEXT", Age);
            date.setData("kb", "TEXT", kb);
            date.setData("Room", "TEXT", Room);
            date.setData("Date", "TEXT", Date);
            // 调用打印方法
           // this.messageBox("date" + date);
            date.setData("table","CASE_NO",parm.getValue("CASE_NO",0));
            this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInd.jhw",
                                 date);
        }
        else {
            this.messageBox("E0010"); //弹出提示对话框（“没打印有数据”）
            return;
        }
    }


    public TParm onQuery(String case_no) {
        String sql = " SELECT opd_order.Case_No as Case_No,  opd_order.COUNTER_NO as COUNTER_NO,opd_order.PRINT_NO as PRINT_NO,SYS_PATINFO.Pat_Name as Pat_Name,SYS_DICTIONARY.CHN_DESC as ," +
            "   FLOOR (MONTHS_BETWEEN (SYSDATE, SYS_PATINFO.BIRTH_DATE) / 12) AS AGE, SYS_DEPT.DEPT_CHN_DESC as  DEPT_CHN_DESC,SYS_OPERATOR.USER_NAME as USER_NAME, opd_order.ORDER_DATE as ORDER_DATE" +
            "  FROM   opd_order,SYS_PATINFO,SYS_DICTIONARY,SYS_OPERATOR,SYS_DEPT" +
            "  WHERE  CASE_NO = '" + case_no + "' AND SYS_PATINFO.MR_NO = opd_order.MR_NO  AND SYS_DICTIONARY.ID = SYS_PATINFO.SEX_CODE" +
            "  AND SYS_DICTIONARY.GROUP_ID = 'SYS_SEX'" +
            "  AND SYS_OPERATOR.USER_ID = opd_order.DR_CODE" +
            "  AND SYS_DEPT.DEPT_CODE = opd_order.DEPT_CODE" +
            "  GROUP BY   opd_order.COUNTER_NO, opd_order.PRINT_NO, SYS_PATINFO.Pat_Name, SYS_DICTIONARY.CHN_DESC, SYS_PATINFO.BIRTH_DATE," +
            "  opd_order.DEPT_CODE,SYS_DEPT.DEPT_CHN_DESC,SYS_OPERATOR.USER_NAME,opd_order.ORDER_DATE,opd_order.Case_No";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        return parm;
    }




}
