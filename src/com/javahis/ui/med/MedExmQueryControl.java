package com.javahis.ui.med;

import com.dongyang.control.*;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: HIS医疗系统</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class MedExmQueryControl extends TControl {
    private String admType ="O";
    private String deptCode="";
    public void onInit() {
        Object obj = this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            this.admType = parm.getValue("ADM_TYPE");
            this.deptCode = parm.getValue("DEPT_CODE");
            this.setValue("EXEC_DEPT_CODE",this.deptCode);
        }
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String tDate = StringTool.getString(sysDate,"yyyyMMdd");
        //默认设置起始日期
        this.setValue("START_DATE", StringTool.getTimestamp(tDate+"000000","yyyyMMddHHmmss"));
        //默认设置终止日期
        this.setValue("END_DATE", StringTool.getTimestamp(tDate+"235959","yyyyMMddHHmmss"));
    }
    /**
     * 查询
     */
    public void onQuery(){
        TParm parm = new TParm();
        String startDate = StringTool.getString((Timestamp)this.getValue("START_DATE"),"yyyyMMddHHmmss");
        String endDate = StringTool.getString((Timestamp)this.getValue("END_DATE"),"yyyyMMddHHmmss");
        String sqlwhere = "";
        String deptCode = this.getValueString("EXEC_DEPT_CODE");
        if(deptCode.length()!=0){
            sqlwhere = "AND A.EXEC_DEPT='"+deptCode+"' ";
        }
        if("O".equals(this.admType)){
            parm = new TParm(this.getDBTool().select("SELECT A.MR_NO,A.CASE_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.EXEC_NO,CASE WHEN A.EXEC_STATUS='Y' THEN '已执行' ELSE '未执行' END EXEC_STATUS,A.EXEC_DEPT,A.EXEC_USER,A.EXEC_DATE,C.OWN_PRICE FROM EXM_EXEC_RECORD A, SYS_PATINFO B,OPD_ORDER C "+
                                    " WHERE "+
                                    " A.MR_NO=B.MR_NO AND "+
                                    " A.MR_NO=C.MR_NO AND "+
                                    " A.CASE_NO=C.CASE_NO AND "+
                                    " A.RX_NO=C.RX_NO AND "+
                                    " A.SEQ_NO=C.SEQ_NO AND "+
                                    " A.EXEC_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
                                    sqlwhere+
                                    " ORDER BY A.MR_NO,A.CASE_NO,A.ORDER_DESC,A.EXEC_NO"));
        }
        if("I".equals(this.admType)){
            parm = new TParm(this.getDBTool().select("SELECT A.MR_NO,A.CASE_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.EXEC_NO,CASE WHEN A.EXEC_STATUS='Y' THEN '已执行' ELSE '未执行' END EXEC_STATUS,A.EXEC_DEPT,A.EXEC_USER,A.EXEC_DATE,C.OWN_PRICE FROM EXM_EXEC_RECORD A, SYS_PATINFO B,SYS_FEE_HISTORY C,ODI_ORDER D "+
                " WHERE "+
                " A.MR_NO=B.MR_NO "+
                " AND "+
                " A.CASE_NO=D.CASE_NO "+
                " AND "+
                " A.RX_NO=D.ORDER_NO "+
                " AND "+
                " A.SEQ_NO=D.ORDER_SEQ "+
                " AND "+
                " A.ORDER_CODE = C.ORDER_CODE "+
                " AND "+
                " A.EXEC_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') AND"+
                " NVL(A.EXEC_DATE,SYSDATE) BETWEEN TO_DATE(C.START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(C.END_DATE,'YYYYMMDDHH24MISS') " +
                sqlwhere+
                " ORDER BY A.MR_NO,A.CASE_NO,A.ORDER_DESC,A.EXEC_NO"));
        }
        this.getTTable("TABLE1").setParmValue(parm);
    }
    /**
     * 导出EXECLE
     */
    public void onExecl(){
        int rows = this.getTTable("TABLE1").getRowCount();
        if(rows<=0){
            this.messageBox("无需要导出的数据！");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE1"),"中医复检执行记录统计");
    }
    /**
     * 拿到TTABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 清空s
     */
    public void onClear(){
        this.clearValue("EXEC_DEPT_CODE");
        this.onInit();
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
