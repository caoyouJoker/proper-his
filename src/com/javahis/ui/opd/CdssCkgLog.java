package com.javahis.ui.opd;

import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.annotation.PKey;
import com.javahis.ui.testOpb.annotation.Table;
import com.javahis.ui.testOpb.bean.BasePOJO;
import com.javahis.ui.testOpb.tools.SqlTool;
import com.javahis.ui.testOpb.tools.Type;

@Table(tableName = "DSS_CKBLOG")
public class CdssCkgLog extends BasePOJO implements Cloneable{
	
	@PKey(name = "CASE_NO", type = Type.CHAR)
	public java.lang.String caseNo = null;
	
	@PKey(name = "CKB_ID", type = Type.CHAR)
	public java.lang.String ckbId = null;
	
	@PKey(name = "LOG_DATE", type = Type.DATE)
	public java.lang.String logDate = null;
	
    @Column(name = "ADM_TYPE", type = Type.CHAR)
    public java.lang.String admType = null;
    
    @Column(name = "MR_NO", type = Type.CHAR)
    public java.lang.String mrNo = null;
    
    @Column(name = "RISK_LEVEL", type = Type.CHAR)
    public java.lang.String riskLevel = null;
    
    @Column(name = "BYPASS_REASON", type = Type.CHAR)
    public java.lang.String bypassReason = null;
    
    @Column(name = "DEPT_CODE", type = Type.CHAR)
    public java.lang.String deptCode = null;
    
    @Column(name = "DR_CODE", type = Type.CHAR)
    public java.lang.String drCode = null;
    
    @Column(name = "IS_CONFIRM", type = Type.CHAR)
    public java.lang.String isConfirm = null;
    
    @Column(name = "ADVISE", type = Type.CHAR)
    public java.lang.String advise = null;
    
    @Column(name = "OPT_USER", type = Type.CHAR)
    public java.lang.String optUser = null;

    @Column(name = "OPT_TERM", type = Type.CHAR)
    public java.lang.String optTerm = null;

    @Column(name = "OPT_DATE", type = Type.DATE)
    public java.lang.String optDate = null;
    
  //add by huangtt 20150806
    @Column(name = "ORDER_NO", type = Type.CHAR)
    public java.lang.String orderNo = null;
    
  //add by huangtt 20150806
    @Column(name = "ORDER_SEQ", type = Type.CHAR)
    public java.lang.Number orderSeq = null;
    
    
  //add by huangtt 20150806 
    @Column(name = "ORDER_CODE", type = Type.CHAR)
    public java.lang.String orderCode = null;
    
    public static void main(String[] args) throws Exception {
    	SqlTool sqlTool = new SqlTool();
    	CdssCkgLog cdssCkgLog = new CdssCkgLog();
    	cdssCkgLog.modifyState = Type.UPDATE;
    	System.out.println(sqlTool.getSql(cdssCkgLog));
	}
    

}
