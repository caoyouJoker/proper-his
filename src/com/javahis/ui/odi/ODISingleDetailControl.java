package com.javahis.ui.odi;




import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;


/**
 * Title: 扫码率统计明细
 * Description:扫码率统计明细
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class ODISingleDetailControl extends TControl {
	private TParm parm;
	private TTable table;
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        parm = (TParm) getParameter();//查询信息
//       System.out.println("parm===========" + parm);
		//若无此信息返回
		if (null == parm) {
			return;
		}     
		onQuery();
    }
    /**
     * 查询
     */
    public void onQuery() { 		
  	    String sql="";
  	    if(parm.getValue("ORDER_TYPE").equals("检验项目"))
  	    sql=" SELECT M.MR_NO,A.PAT_NAME,M.LINKMAIN_FLG,M.LINK_NO,"+
  	    	" M.ORDER_CODE,M.ORDER_DESC,M.SPECIFICATION,M.MEDI_QTY,"+
  	    	" B.UNIT_CHN_DESC ,C.FREQ_CHN_DESC,E.ROUTE_CHN_DESC,M.TAKE_DAYS,"+
  	    	" M.DOSAGE_QTY,F.UNIT_CHN_DESC AS UNIT_DESC,M.ORDER_DATE,M.START_DTTM,"+
  	    	" M.END_DTTM,G.USER_NAME,M.BAR_CODE,"+
  	    	" CASE WHEN M.DSPN_KIND ='F' THEN '首日量' WHEN M.DSPN_KIND ='UD' THEN '长期'"+ 
  	    	" WHEN M.DSPN_KIND ='ST' THEN '临时' END AS DSPN_KIND,"+
  	    	" TO_CHAR(TO_DATE (D.ORDER_DATE || D.ORDER_DATETIME,'YYYYMMDDHH24MISS'),"+
  	    	" 'YYYY-MM-DD HH24:MI:SS') NS_EXEC_DATE,D.NS_EXEC_DATE_REAL"+
  	    	" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PATINFO A,"+
  	    	" SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE E,SYS_UNIT F,SYS_OPERATOR G"+
  	    	" WHERE M.ORDER_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
  	    	" AND  TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
  	    	" AND M.CAT1_TYPE='LIS'"+
  	    	" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
  	    	" AND M.ORDER_CODE = M.ORDERSET_CODE"+
  	    	" AND M.OPTITEM_CODE  NOT IN ('I3','I4')"+
  	    	" AND D.NS_EXEC_DATE_REAL IS NULL"+                         
  	    	" AND D.CASE_NO=M.CASE_NO"+
  	    	" AND D.ORDER_NO=M.ORDER_NO"+
  	    	" AND D.ORDER_SEQ=M.ORDER_SEQ"+
  	    	" AND D.DC_DATE IS NULL"+
  	    	" AND M.DC_NS_CHECK_DATE IS NULL"+
  	    	" AND M.DEPT_CODE = '"+ parm.getValue("DEPT_CODE")+ "'"+
  	    	" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
  	    	" AND M.MR_NO = A.MR_NO"+
  	    	" AND M.MEDI_UNIT = B.UNIT_CODE(+)"+
  	    	" AND M.FREQ_CODE = C.FREQ_CODE(+)"+
  	    	" AND M.ROUTE_CODE = E.ROUTE_CODE(+)"+
  	    	" AND M.MEDI_UNIT = F.UNIT_CODE(+)"+
  	    	" AND M.ORDER_DR_CODE = G.USER_ID";
  	    else if(parm.getValue("ORDER_TYPE").equals("针剂大量点滴"))
  	    sql=" SELECT M.MR_NO,A.PAT_NAME,M.LINKMAIN_FLG,M.LINK_NO,"+
  	    	" M.ORDER_CODE,M.ORDER_DESC,M.SPECIFICATION,M.MEDI_QTY,"+
  	    	" B.UNIT_CHN_DESC,C.FREQ_CHN_DESC,E.ROUTE_CHN_DESC,M.TAKE_DAYS,"+
  	    	" M.DOSAGE_QTY,F.UNIT_CHN_DESC AS UNIT_DESC,M.ORDER_DATE,M.START_DTTM,"+
  	    	" M.END_DTTM,G.USER_NAME,M.BAR_CODE,"+
  	    	" CASE WHEN M.DSPN_KIND ='F' THEN '首日量' WHEN M.DSPN_KIND ='UD' THEN '长期'"+ 
  	    	" WHEN M.DSPN_KIND ='ST' THEN '临时' END AS DSPN_KIND,"+
  	    	" TO_CHAR(TO_DATE (D.ORDER_DATE || D.ORDER_DATETIME,'YYYYMMDDHH24MISS'),"+
  	    	" 'YYYY-MM-DD HH24:MI:SS')NS_EXEC_DATE,D.NS_EXEC_DATE_REAL"+
  	    	" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S,SYS_PATINFO A,"+
  	    	" SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE E,SYS_UNIT F,SYS_OPERATOR G"+
  	    	" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN  '"+ parm.getValue("START_DATE")+ "' " +
  	    	" AND '"+ parm.getValue("END_DATE")+ "'"+
  	    	" AND M.CAT1_TYPE='PHA'"+
  	    	" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
  	    	" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)"+
  	    	" AND S.CLASSIFY_TYPE IN ('F','I')"+
  	    	" AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
  	    	" AND (M.ROUTE_CODE !='IN.I.P' AND M.FREQ_CODE !='.')" +
  	    	" AND D.NS_EXEC_DATE_REAL IS NULL"+                         
  	    	" AND D.CASE_NO=M.CASE_NO"+
  	    	" AND D.ORDER_NO=M.ORDER_NO"+
  	    	" AND D.ORDER_SEQ=M.ORDER_SEQ"+
  	    	" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
  	    	" AND M.DEPT_CODE = '"+ parm.getValue("DEPT_CODE")+ "'"+
  	    	" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR"+ 
  	    	" D.DC_DATE IS NULL)"+
  	    	" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
  	    	" AND M.MR_NO = A.MR_NO"+
  	    	" AND M.MEDI_UNIT = B.UNIT_CODE(+)"+
  	    	" AND M.FREQ_CODE = C.FREQ_CODE(+)"+
  	    	" AND M.ROUTE_CODE = E.ROUTE_CODE(+)"+
  	    	" AND M.MEDI_UNIT = F.UNIT_CODE(+)"+
  	    	" AND M.ORDER_DR_CODE = G.USER_ID";
  	    else if(parm.getValue("ORDER_TYPE").equals("口服外用"))
  	    	 sql=" SELECT M.MR_NO,A.PAT_NAME,M.LINKMAIN_FLG,M.LINK_NO,"+
   	    	" M.ORDER_CODE,M.ORDER_DESC,M.SPECIFICATION,M.MEDI_QTY,"+
   	    	" B.UNIT_CHN_DESC,C.FREQ_CHN_DESC,E.ROUTE_CHN_DESC,M.TAKE_DAYS,"+
   	    	" M.DOSAGE_QTY,F.UNIT_CHN_DESC AS UNIT_DESC,M.ORDER_DATE,M.START_DTTM,"+
   	    	" M.END_DTTM,G.USER_NAME,M.BAR_CODE,"+
   	    	" CASE WHEN M.DSPN_KIND ='F' THEN '首日量' WHEN M.DSPN_KIND ='UD' THEN '长期'"+ 
   	    	" WHEN M.DSPN_KIND ='ST' THEN '临时' END AS DSPN_KIND,"+
   	    	" TO_CHAR(TO_DATE (D.ORDER_DATE || D.ORDER_DATETIME,'YYYYMMDDHH24MISS'),"+
   	    	" 'YYYY-MM-DD HH24:MI:SS')NS_EXEC_DATE,D.NS_EXEC_DATE_REAL"+
   	    	" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S,SYS_PATINFO A,"+
   	    	" SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE E,SYS_UNIT F,SYS_OPERATOR G"+
   	    	" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN  '"+ parm.getValue("START_DATE")+ "' " +
   	    	" AND '"+ parm.getValue("END_DATE")+ "'"+
   	    	" AND M.CAT1_TYPE='PHA'"+
   	        " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
   	    	" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)"+
   	    	" AND S.CLASSIFY_TYPE NOT IN ('F','I')"+
   	        " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
   	    	" AND D.NS_EXEC_DATE_REAL IS NULL"+                         
   	    	" AND D.CASE_NO=M.CASE_NO"+
   	    	" AND D.ORDER_NO=M.ORDER_NO"+
   	    	" AND D.ORDER_SEQ=M.ORDER_SEQ"+
   	    	" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
   	    	" AND M.DEPT_CODE = '"+ parm.getValue("DEPT_CODE")+ "'"+
   	    	" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR"+ 
   	    	" D.DC_DATE IS NULL)"+
   	    	" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
   	    	" AND M.MR_NO = A.MR_NO"+
   	    	" AND M.MEDI_UNIT = B.UNIT_CODE(+)"+
   	    	" AND M.FREQ_CODE = C.FREQ_CODE(+)"+
   	    	" AND M.ROUTE_CODE = E.ROUTE_CODE(+)"+
   	    	" AND M.MEDI_UNIT = F.UNIT_CODE(+)"+
   	    	" AND M.ORDER_DR_CODE = G.USER_ID";
	    TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//	    System.out.println("data=========="+data);  
	    if (data.getErrCode() < 0) {
	 	    this.messageBox("E0116");//没有数据
		    return;
	      }	
	    if (data.getCount() <= 0) {
	 	    this.messageBox("查无资料");
		    return;
	      }	 
	     ((TTable) getComponent("TABLE")).setParmValue(data);	
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	String  a ="";
    	if(parm.getValue("ORDER_TYPE").equals("检验项目"))
    	    a ="检验项目";
    	 else if(parm.getValue("ORDER_TYPE").equals("针剂大量点滴"))
    	    a ="针剂大量点滴";
    	else if(parm.getValue("ORDER_TYPE").equals("口服外用"))
    	    a ="口服外用";   		 
    		title =parm.getValue("DEPT_DESC")+a+"未执行扫码明细表";
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }   	  
}
