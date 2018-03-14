package com.javahis.ui.reg;

import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.Vector;

/**
 * <p>Title: 挂号员工作量统计查询(明细)</p>
 *
 * <p>Description: 挂号员工作量统计查询(明细)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.08.27
 * @version 1.0
 */
public class REGWorkloadDetialControl
    extends TControl {
	TParm selParm ;
    TParm endData;
    public void onInit() {
        super.onInit();
        Vector vct = (Vector)this.getParameter();
        String sDate = StringTool.getString(TypeTool.getTimestamp(vct.get(0)),
                                            "yyyyMMdd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(vct.get(1)),
                                            "yyyyMMdd");
        String cashCode = vct.get(2).toString();
        setValue("S_DATE", vct.get(0));
        setValue("E_DATE", vct.get(1));
        setValue("CASH_CODE", cashCode);
        setPageValue(sDate, eDate);
    }

    public void setPageValue(String sDate, String eDate) {
        String cashCodeWhere = "";
        if (getValue("CASH_CODE").toString().length() != 0)
            cashCodeWhere = " AND B.CASH_CODE = '" + getValue("CASH_CODE") +
                "'  ";
        String sql =
            " SELECT A.CASE_NO, C.MR_NO, C.PAT_NAME, A.CLINICTYPE_CODE, A.REALDEPT_CODE," +
            "        A.REALDR_CODE,B.PRINT_NO, B.REG_FEE, B.CLINIC_FEE," +
            "        (B.REG_FEE + B.CLINIC_FEE) AR_AMT " +
            "   FROM REG_PATADM A, BIL_REG_RECP B, SYS_PATINFO C" +
            "  WHERE A.CASE_NO = B.CASE_NO " +
            "    AND A.MR_NO = C.MR_NO " +
            cashCodeWhere +
            "    AND A.ADM_DATE BETWEEN TO_DATE ('" + sDate + "000000" +
            "', 'yyyyMMddHH24miss')" +
            "    AND TO_DATE ('" + eDate + "235959" + "', 'yyyyMMddHH24miss') ORDER BY B.PRINT_NO " ;
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        double count = selParm.getCount();
        this.setValue("COUNT", count+"");
        this.callFunction("UI|Table|setParmValue", selParm);
     

    }


}
