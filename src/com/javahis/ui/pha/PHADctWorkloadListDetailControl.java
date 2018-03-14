package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PHADctWorkloadListDetailControl  extends TControl {

    TParm outsideParm = new TParm();

    public void onInit() {
        super.onInit();
        initUI();
        initTable();
    }

    private void initUI(){
        outsideParm = (TParm)getParameter();
        setValue("DATE",StringTool.getDate(outsideParm.getValue("DATE"),"yyyyMMdd"));
        setValue("DR_CODE",outsideParm.getValue("DR_CODE"));
    }

    private void initTable(){
        String SQL = "";
        if(outsideParm.getValue("ADM_TYPE").equals("I")){
            SQL = " SELECT B.MR_NO,B.PAT_NAME,C.DEPT_ABS_DESC,D.USER_NAME,A.DOSAGE_QTY,A.TOT_AMT AR_AMT"+
                  " FROM   IBS_ORDD A,SYS_PATINFO B,SYS_DEPT C,SYS_OPERATOR D,ADM_INP E" +
                  " WHERE  A.BILL_DATE BETWEEN TO_DATE('"+outsideParm.getData("DATE")+"000000','YYYYMMDDHH24MISS') "+
                  "                        AND TO_DATE('"+outsideParm.getData("DATE")+"235959','YYYYMMDDHH24MISS') "+
                  " " + (outsideParm.getValue("DEPT_CODE").length() == 0 ? "": " AND A.DEPT_CODE = '"+outsideParm.getValue("DEPT_CODE")+"'")+
                  " " + (outsideParm.getValue("DR_CODE").length() == 0 ? "": " AND A.DR_CODE = '"+outsideParm.getValue("DR_CODE")+"'") +
                  "   AND  A.ORDER_CAT1_CODE = 'DCT'"+
                  "   AND A.CASE_NO = E.CASE_NO"+
                  "   AND E.MR_NO = B.MR_NO"+
                  "   AND  A.DEPT_CODE = C.DEPT_CODE"+
                  "   AND  A.DR_CODE = D.USER_ID";
        }
        else{
            SQL = " SELECT A.MR_NO,B.PAT_NAME,C.DEPT_ABS_DESC,D.USER_NAME,A.DOSAGE_QTY,A.AR_AMT"+
                  " FROM   OPD_ORDER A,SYS_PATINFO B,SYS_DEPT C,SYS_OPERATOR D" +
                  " WHERE  A.REGION_CODE = '"+outsideParm.getValue("REGION_CODE")+"'" +
                  "   AND  A.BILL_DATE BETWEEN TO_DATE('"+outsideParm.getData("DATE")+"000000','YYYYMMDDHH24MISS') "+
                  "                        AND TO_DATE('"+outsideParm.getData("DATE")+"235959','YYYYMMDDHH24MISS') "+
                  "   AND  A.ADM_TYPE = '"+outsideParm.getValue("ADM_TYPE")+"'" +
                  " " + (outsideParm.getValue("DEPT_CODE").length() == 0 ? "": " AND A.DEPT_CODE = '"+outsideParm.getValue("DEPT_CODE")+"'")+
                  " " + (outsideParm.getValue("DR_CODE").length() == 0 ? "": " AND A.DR_CODE = '"+outsideParm.getValue("DR_CODE")+"'") +
                  "   AND  A.ORDER_CAT1_CODE = 'DCT'" +
                  "   AND  A.MR_NO = B.MR_NO"+
                  "   AND  A.DEPT_CODE = C.DEPT_CODE"+
                  "   AND  A.DR_CODE = D.USER_ID";
        }
        System.out.println("SQL = " + SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        callFunction("UI|Table|setParmValue", parm);
    }
}





