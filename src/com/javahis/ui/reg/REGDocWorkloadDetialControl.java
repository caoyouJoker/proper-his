package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import java.util.Vector;

import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;

import java.sql.Timestamp;

/**
 * <p>Title: 医生工作量统计报表(明细)</p>
 *
 * <p>Description: 医生工作量统计报表(明细)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2009.08.31 wangl
 * @version 1.0
 */
public class REGDocWorkloadDetialControl
    extends TControl {
    TParm endData;
    private String sessionCode;
    private String deptCode;
    private String clinictypeCode;
    private String startTime;
    private String endTime;
    private String clinicType;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        Vector vct = (Vector)this.getParameter();
        String drCode = vct.get(0).toString();
        String admDate = vct.get(1).toString();
        sessionCode = vct.get(2).toString();
        deptCode = vct.get(3).toString();
        clinictypeCode = vct.get(4).toString();
        startTime = StringTool.getString(TypeTool.getTimestamp(vct.get(5)), "yyyyMMdd");
        endTime = StringTool.getString(TypeTool.getTimestamp(vct.get(6)),"yyyyMMdd");
        clinicType=vct.get(7).toString();
        setValue("S_DATE", vct.get(0));
        setValue("E_DATE", vct.get(1));
        setValue("ADM_DATE", admDate);
        setValue("DR_CODE", drCode);
        String admDateStr = admDate.replace("/","");
        setPageValue(admDateStr);
    }

    /**
     * 给界面控件赋值
     * @param sDate String
     * @param eDate String
     */
    public void setPageValue(String sDate) {
        TParm selParm = new TParm();
        String drCodeWhere = "";
        String where = "";
        if (getValue("DR_CODE").toString().length() != 0)
            drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE") +
                "'  ";
        if(sessionCode.length() != 0){
        	where += " AND A.SESSION_CODE ='"+sessionCode+"'";
        }
        if(deptCode.length() != 0){
        	where += " AND A.REALDEPT_CODE ='"+deptCode+"'";
        }
        if(clinictypeCode.length() != 0){
        	where += " AND A.CLINICTYPE_CODE ='"+clinictypeCode+"'";
        }
        if (clinicType.equals("1")){
        	where +=" AND B.CLINIC_FEE=0";
        }else if(clinicType.equals("2")){
        	where += " AND B.CLINIC_FEE>0";
        }
        String sql =
            " SELECT A.MR_NO, C.PAT_NAME, A.CTZ1_CODE, A.CTZ2_CODE, A.CLINICTYPE_CODE," +
            "        A.REALDEPT_CODE, A.REALDR_CODE, B.REG_FEE_REAL, B.CLINIC_FEE_REAL," +
            "        B.AR_AMT, G.CTZ_DESC CTZ1_DESC, H.CTZ_DESC CTZ2_DESC, D.DEPT_ABS_DESC," +
            "        E.USER_NAME,F.CLINICTYPE_DESC " +
            "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,SYS_DEPT D,SYS_OPERATOR E,REG_CLINICTYPE F,SYS_CTZ G,SYS_CTZ H" +
            "  WHERE A.CASE_NO = B.CASE_NO(+) " +
            "    AND A.MR_NO = C.MR_NO " +
            "    AND A.ARRIVE_FLG = 'Y'" +
            "    AND A.REALDEPT_CODE = D.DEPT_CODE " +
            "    AND A.REALDR_CODE = E.USER_ID " +
            "    AND A.CLINICTYPE_CODE = F.CLINICTYPE_CODE " +
            "    AND A.CTZ1_CODE = G.CTZ_CODE(+) " +
            "    AND A.CTZ2_CODE = H.CTZ_CODE(+) " +
            drCodeWhere +
            where +
            " AND A.ADM_DATE = TO_DATE ('" + sDate + "000000" +
            "', 'yyyyMMddHH24miss')" +
            " AND B.BILL_DATE BETWEEN TO_DATE('" + startTime +
            "000000" + "','yyyyMMddHH24miss') " +
            "            AND TO_DATE('" + endTime + "235959" +
            "','yyyyMMddHH24miss') " ;
//        System.out.println("sql"+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        this.callFunction("UI|Table|setParmValue", selParm);

    }

    public void onTableClicked(int row) {
        if (row < 0)
            return;
        TTable table = (TTable)this.getComponent("Table");
        TParm parm = table.getParmValue();
        String mrNo = parm.getValue("MR_NO", row);
        String patName = parm.getValue("PAT_NAME", row);
        setValue("MR_NO", mrNo);
        setValue("PAT_NAME", patName);

    }

}
