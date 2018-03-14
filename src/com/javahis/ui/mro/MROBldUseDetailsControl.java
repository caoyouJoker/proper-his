
package com.javahis.ui.mro;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import java.sql.Timestamp;
import java.util.Date;

public class MROBldUseDetailsControl extends TControl
{

    public MROBldUseDetailsControl()
    {
    }

    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
        comboBox = (TComboBox)getComponent("tTableName");
        comboBox.setStringData("[[id,text],[A,\u5FC3\u5916\u79D1\u624B\u672F\u60A3\u8005\u7528\u8840\u660E\u7EC6\u8868],[B,\u5404\u5916\u79D1\u6309\u75C5\u79CD\u672F\u5F0F\u5E73\u5747\u7528\u8840\u7EDF\u8BA1\u8868],[C,\u5404\u5916\u79D1\u624B\u672F\u7528\u8840\u7EDF\u8BA1],[D,\u975E\u5916\u79D1\u75C5\u4EBA\u7528\u8840\u7EDF\u8BA1]]");
        comboBox.setSelectedIndex(0);
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("START_DATE", (new StringBuilder(String.valueOf(StringTool.rollDate(date, -30L).toString().substring(0, 10).replace('-', '/')))).append(" 00:00:00").toString());
        setValue("END_DATE", (new StringBuilder(String.valueOf(date.toString().substring(0, 10).replace('-', '/')))).append(" 23:59:59").toString());
    }

    public void onQuery()
    {
        String date_s = getValueString("START_DATE");
        String date_e = getValueString("END_DATE");
        date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").replace("-", "").replace(" ", "");
        date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").replace("-", "").replace(" ", "");
        String value = getValueString("tTableName");
        if(value.equals("A"))
            CardialSurgeryBldUseD(date_s, date_e);
        else
        if(value.equals("B"))
            SurgeryBldUseD(date_s, date_e);
        else
        if(value.equals("C"))
            SurgeryBldStatistical(date_s, date_e);
        else
        if(value.equals("D"))
            nonSurgicalBldUseD(date_s, date_e);
    }

    public void onClear()
    {
        table.removeRowAll();
        clearValue("DEPT_CODE");
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("START_DATE", (new StringBuilder(String.valueOf(StringTool.rollDate(date, -30L).toString().substring(0, 10).replace('-', '/')))).append(" 00:00:00").toString());
        setValue("END_DATE", (new StringBuilder(String.valueOf(date.toString().substring(0, 10).replace('-', '/')))).append(" 23:59:59").toString());
    }

    public void onExport()
    {
        TTable table = (TTable)callFunction("UI|TABLE|getThis", new Object[0]);
        String Title = comboBox.getSelectedText();
        ExportExcelUtil.getInstance().exportExcel(table, Title);
    }

    public void CardialSurgeryBldUseD(String date_s, String date_e)
    {
        String Sql = (new StringBuilder(" SELECT D.DEPT_CHN_DESC , B.MR_NO ,  A.PAT_NAME,  E.CHN_DESC ,  FLOOR(MONTHS_BETWEEN( SYSDATE, A.BIRTH_DATE) / 12) AS AGE,  A.OUT_DATE,  F.ICD_CHN_DESC,  B.OP_DESC ,  C.USER_NAME ,  H.USER_NAME AS AST_NAME,  G.USER_NAME AS\u3000NAME,  A.RBC ,  A.PLASMA ,  A.PLATE ,  A.WHOLE_BLOOD ,  A.OTH_BLOOD   FROM MRO_RECORD A, MRO_RECORD_OP B, SYS_OPERATOR C, SYS_DEPT D, SYS_DICTIONARY E,  SYS_DIAGNOSIS F, SYS_OPERATOR G,SYS_OPERATOR H  WHERE A.CASE_NO = B.CASE_NO  AND B.MAIN_SUGEON = C.USER_ID  AND A.OUT_DEPT = D.DEPT_CODE  AND E.GROUP_ID = 'SYS_SEX'  AND A.SEX = E.ID  AND A.OUT_DIAG_CODE1 = F.ICD_CODE  AND B.MAIN_FLG = 'Y'  AND B.ANA_DR = G.USER_ID  AND A.OUT_DATE IS NOT NULL  AND A.OUT_DEPT IN ( '030201','0303')  AND A.OUT_DATE BETWEEN TO_DATE( '")).append(date_s).append("', 'YYYYMMDDHH24MISS') ").append(" AND TO_DATE( '").append(date_e).append("', 'YYYYMMDDHH24MISS') ").append(" AND B.AST_DR1 = H.USER_ID ").append(" ORDER BY D.DEPT_CHN_DESC ").toString();
        TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
        if(tabParm.getCount("DEPT_CHN_DESC") < 0)
        {
            messageBox("\u6CA1\u6709\u8981\u67E5\u8BE2\u7684\u6570\u636E\uFF01");
            onClear();
            return;
        } else
        {
            table.setHeader("\u79D1\u5BA4,80,DEPT_CHN_DESC;\u75C5\u6848\u53F7,100,MR_NO;\u59D3\u540D,80,PAT_NAME;\u6027\u522B,40,CHN_DESC;\u5E74\u9F84,40,AGE;\u51FA\u9662\u65F6\u95F4,80,OUT_DATE;\u4E3B\u8BCA\u65AD,150,ICD_CHN_DESC;\u4E3B\u672F\u5F0F,240,OP_DESC;\u672F\u8005,80,USER_NAME;\u4E00\u52A9,80,AST_NAME;\u4E3B\u9EBB\u9189\u5E08,80,NAME;\u7EA2\u8840\u7403,60;\u8840\u6D46,60;\u8840\u5C0F\u677F,60;\u5168\u8840,60;\u5176\u5B83\u8840\u54C1\u79CD\u7C7B,100");
            table.setParmMap("DEPT_CHN_DESC;MR_NO;PAT_NAME;CHN_DESC;AGE;OUT_DATE;ICD_CHN_DESC;OP_DESC;USER_NAME;AST_NAME;NAME;RBC;PLASMA;PLATE;WHOLE_BLOOD;OTH_BLOOD;");
            table.setItem("DEPT_CHN_DESC;MR_NO;PAT_NAME");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,right;12,right;13,right;14,right;15,right;");
            table.setParmValue(tabParm);
            return;
        }
    }

    public void SurgeryBldUseD(String date_s, String date_e)
    {
        String Sql = (new StringBuilder(" SELECT C.DEPT_CHN_DESC , B.OP_DESC ,  COUNT(*) AS TOT,  ROUND( AVG(A.RBC), 2) AS RBC,  ROUND( AVG(A.PLATE), 2) AS PLATE,  ROUND( AVG(A.PLASMA), 2) AS PLASMA,  ROUND( AVG(A.WHOLE_BLOOD), 2) AS WHOLE_BLOOD,  ROUND( AVG(A.OTH_BLOOD), 2) AS OTH_BLOOD FROM MRO_RECORD A, MRO_RECORD_OP B, SYS_DEPT C  WHERE A.CASE_NO = B.CASE_NO  AND A.OUT_DEPT = C.DEPT_CODE  AND A.OUT_DATE IS NOT NULL  AND A.OUT_DEPT IN ( '030201','0303')  AND A.OUT_DATE BETWEEN TO_DATE( '")).append(date_s).append("', 'YYYYMMDDHH24MISS') ").append(" AND TO_DATE( '").append(date_e).append("', 'YYYYMMDDHH24MISS') ").append(" GROUP BY C.DEPT_CHN_DESC, B.OP_DESC ").append(" ORDER BY C.DEPT_CHN_DESC, B.OP_DESC ").toString();
        TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
        if(tabParm.getCount("DEPT_CHN_DESC") < 0)
        {
            messageBox("\u6CA1\u6709\u8981\u67E5\u8BE2\u7684\u6570\u636E\uFF01");
            onClear();
            return;
        } else
        {
            table.setHeader("\u79D1\u5BA4,80,DEPT_CHN_DESC;\u672F\u5F0F,240,OP_DESC;\u4F8B\u6570,80;\u7EA2\u8840\u7403\u5E73\u5747\u503C,80;\u8840\u5C0F\u677F\u5E73\u5747\u503C,80;\u8840\u6D46\u5E73\u5747\u503C,80;\u5168\u8840\u5E73\u5747\u503C,80;\u5176\u5B83\u8840\u54C1\u79CD\u7C7B\u5E73\u5747\u503C,120");
            table.setParmMap("DEPT_CHN_DESC;OP_DESC;TOT;RBC;PLATE;PLASMA;WHOLE_BLOOD;OTH_BLOOD;");
            table.setItem("DEPT_CHN_DESC;OP_DESC");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;");
            table.setParmValue(tabParm);
            return;
        }
    }

    public void SurgeryBldStatistical(String date_s, String date_e)
    {
        String Sql = (new StringBuilder(" SELECT C.DEPT_CHN_DESC ,  COUNT(*) AS TOT ,  SUM(A.RBC) AS RBC,  ROUND( AVG(A.RBC), 2) AS RBC_AVG  FROM MRO_RECORD A, MRO_RECORD_OP B, SYS_DEPT C  WHERE A.CASE_NO = B.CASE_NO  AND A.OUT_DEPT = C.DEPT_CODE  AND A.OUT_DEPT IN ( '030201','0303')  AND A.OUT_DATE IS NOT NULL  AND A.OUT_DATE BETWEEN TO_DATE( '")).append(date_s).append("', 'YYYYMMDDHH24MISS') ").append(" AND TO_DATE( '").append(date_e).append("', 'YYYYMMDDHH24MISS') ").append(" GROUP BY C.DEPT_CHN_DESC ").append(" ORDER BY C.DEPT_CHN_DESC ").toString();
        TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
        if(tabParm.getCount("DEPT_CHN_DESC") < 0)
        {
            messageBox("\u6CA1\u6709\u8981\u67E5\u8BE2\u7684\u6570\u636E\uFF01");
            onClear();
            return;
        } else
        {
            table.setHeader("\u79D1\u5BA4,80,DEPT_CHN_DESC;\u4F8B\u6570,80;\u7528\u7EA2\u8840\u7403\u91CF,100;\u624B\u672F\u4F8B\u5747\u7528\u7EA2\u7EC6\u80DE\u91CF,150");
            table.setParmMap("DEPT_CHN_DESC;TOT;RBC;RBC_AVG;");
            table.setItem("DEPT_CHN_DESC");
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;");
            table.setParmValue(tabParm);
            return;
        }
    }

    public void nonSurgicalBldUseD(String date_s, String date_e)
    {
        String Sql = (new StringBuilder(" SELECT  D.DEPT_CHN_DESC,  B.MR_NO,  A.PAT_NAME,  A.OUT_DATE,  F.ICD_CHN_DESC,  B.OP_DESC,  C.USER_NAME,  A.RBC,  A.PLASMA,  A.PLATE,  A.WHOLE_BLOOD  FROM   MRO_RECORD A,   MRO_RECORD_OP B,   SYS_OPERATOR C,   SYS_DEPT D,   SYS_DIAGNOSIS F   WHERE   A.OUT_DATE BETWEEN TO_DATE( '")).append(date_s).append("', 'YYYYMMDDHH24MISS')  ").append(" AND TO_DATE( '").append(date_e).append("', 'YYYYMMDDHH24MISS')  ").append(" AND A.OUT_DEPT NOT IN ( '030201','0303')  ").append(" AND A.OUT_DATE IS NOT NULL  ").append(" AND A.CASE_NO        = B.CASE_NO  ").append(" AND B.MAIN_FLG       = 'Y'  ").append(" AND B.MAIN_SUGEON    = C.USER_ID  ").append(" AND A.OUT_DEPT       = D.DEPT_CODE  ").append(" AND A.OUT_DIAG_CODE1 = F.ICD_CODE  ").append(" AND ( A.RBC> 0 OR A.PLASMA> 0 OR A.PLATE > 0 OR A.WHOLE_BLOOD> 0 )  ").append(" UNION  ").append(" SELECT  ").append(" D.DEPT_CHN_DESC, ").append(" A.MR_NO,  ").append(" A.PAT_NAME,  ").append(" A.OUT_DATE, ").append(" F.ICD_CHN_DESC,  ").append(" '' AS OP_DESC, ").append(" '' AS USER_NAME, ").append(" A.RBC, ").append(" A.PLASMA, ").append(" A.PLATE, ").append(" A.WHOLE_BLOOD  ").append(" FROM  ").append(" MRO_RECORD A,  ").append(" SYS_DEPT D,  ").append(" SYS_DIAGNOSIS F  ").append(" WHERE  ").append(" A.OUT_DATE BETWEEN TO_DATE( '").append(date_s).append("', 'YYYYMMDDHH24MISS')  ").append(" AND TO_DATE( '").append(date_e).append("', 'YYYYMMDDHH24MISS')  ").append(" AND A.OUT_DEPT NOT IN ( '030201','0303')  ").append(" AND A.OUT_DATE IS NOT NULL  ").append(" AND A.OUT_DEPT       = D.DEPT_CODE  ").append(" AND A.OUT_DIAG_CODE1 = F.ICD_CODE  ").append(" AND ( A.RBC> 0 OR A.PLASMA> 0 OR A.PLATE > 0 OR A.WHOLE_BLOOD> 0 )  ").append(" ORDER BY DEPT_CHN_DESC  ").toString();
        TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
        if(tabParm.getCount("DEPT_CHN_DESC") < 0)
        {
            messageBox("\u6CA1\u6709\u8981\u67E5\u8BE2\u7684\u6570\u636E\uFF01");
            onClear();
            return;
        } else
        {
            table.setHeader("\u79D1\u5BA4,80,DEPT_CHN_DESC;\u75C5\u6848\u53F7,100,MR_NO;\u59D3\u540D,80,PAT_NAME;\u51FA\u9662\u65F6\u95F4,80,OUT_DATE;\u8BCA\u65AD,240,ICD_CHN_DESC;\u4ECB\u5165\u672F\u5F0F,240,OP_DESC;\u672F\u8005,80,USER_NAME;\u7EA2\u8840\u7403,80;\u8840\u6D46,80;\u8840\u5C0F\u677F,80;\u5168\u8840,80");
            table.setParmMap("DEPT_CHN_DESC;MR_NO;PAT_NAME;OUT_DATE;ICD_CHN_DESC;OP_DESC;USER_NAME;RBC;PLASMA;PLATE;WHOLE_BLOOD");
            table.setItem("DEPT_CHN_DESC;MR_NO;PAT_NAME");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,right");
            table.setParmValue(tabParm);
            return;
        }
    }

    private static final String TParm = null;
    private static TTable table;
    private static TComboBox comboBox;

}
