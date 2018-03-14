package com.javahis.ui.odi;



import java.text.DecimalFormat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: ɨ����ͳ��
 * Description:ɨ����ͳ��
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class ODISingleExeStatisticsControl extends TControl {
	private TTable table;
	DecimalFormat df1 = new DecimalFormat("##########0.00");
	DecimalFormat df = new DecimalFormat("##########0");
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
	 * ���ݼ��
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("��ʼ���ڲ���Ϊ��");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("�������ڲ���Ϊ��");
    		return true;
    	}		
	    return false; 
	}
    /**
     * ��ѯ
     */
    public void onQuery() {
    	//���ݼ��
    	if(checkdata())
		    return;
    	String sql ="";
    	String dept ="";//����
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//����ѡ��
		if(!this.getValue("DEPT_CODE").equals("")){
			String deptcode = this.getValue("DEPT_CODE").toString();
			dept = " AND M.DEPT_CODE = '"+ deptcode+ "'";
		}
		//ҽ��ѡ��
		//������Ŀ
    	if(this.getValue("ORDER_TYPE").equals("1")){
    		sql=" SELECT TO_CHAR(M.ORDER_DATE,'YYYYMM') AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
                 " SUM(CASE"+ 
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    			 " THEN 1"+ 
    			 " ELSE 0"+
    			 " END ) AS EXEC_ACCOUNT_NUM,'������Ŀ' AS OREDER_TYPE,M.DEPT_CODE"+
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT"+
    			 " WHERE M.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
    	         " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    			 " AND M.CAT1_TYPE='LIS'"+
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ORDER_CODE = M.ORDERSET_CODE"+
                 " AND M.OPTITEM_CODE  NOT IN ('I3','I4')"+
    			 " AND D.CASE_NO=M.CASE_NO"+
    			 " AND D.ORDER_NO=M.ORDER_NO"+
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ"+
    			 " AND D.DC_DATE IS NULL" +
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    			 " GROUP BY TO_CHAR(M.ORDER_DATE,'YYYYMM'),DEPT.DEPT_CHN_DESC,'������Ŀ',M.DEPT_CODE"+
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE"; 
    	}
    	//����������
    	else if(this.getValue("ORDER_TYPE").equals("2")){
    		sql=" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    			 " SUM(CASE" + 
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL" +
    			 " THEN 1" + 
    			 " ELSE 0" +
    			 " END ) AS EXEC_ACCOUNT_NUM,'����������' AS OREDER_TYPE,M.DEPT_CODE" +
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S" +
    			 " WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    			 " AND M.CAT1_TYPE='PHA'" +
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
                 " AND S.CLASSIFY_TYPE IN ('F','I')" +
                 " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
                 " AND (M.ROUTE_CODE !='IN.I.P' AND M.FREQ_CODE !='.')" +
    			 " AND D.CASE_NO=M.CASE_NO" +
    			 " AND D.ORDER_NO=M.ORDER_NO" +
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ" +
    			 " AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
    			 " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
    			 " AND M.PHA_CHECK_DATE IS NOT NULL"+
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') " +
    			 " OR D.DC_DATE IS NULL)" +
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE" +
    			 " GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'����������',M.DEPT_CODE" +
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE";
    	}
    	//�ڷ�����
    	else if(this.getValue("ORDER_TYPE").equals("3")){
    		sql=" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    			 " SUM(CASE" +
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL" +
    			 " THEN 1" + 
    			 " ELSE 0" +
    			 " END ) AS EXEC_ACCOUNT_NUM,'�ڷ�����' AS OREDER_TYPE,M.DEPT_CODE" +
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S" +
    			 " WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    			 " AND M.CAT1_TYPE='PHA'" +
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
                 " AND S.CLASSIFY_TYPE NOT IN ('F','I')" +
                 " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
    			 " AND D.CASE_NO=M.CASE_NO" +
    			 " AND D.ORDER_NO=M.ORDER_NO" +
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ" +
    			 " AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
    			 " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
    			 " AND M.PHA_CHECK_DATE IS NOT NULL"+
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') " +
    			 " OR D.DC_DATE IS NULL)" +
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE" +
    			 " GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'�ڷ�����',M.DEPT_CODE" + 
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE"; 		
    	}
    	else {
    		sql=" SELECT A.ORDER_DATE,A.DEPT_CHN_DESC ,SUM(A.ACCOUNT_NUM) AS ACCOUNT_NUM," +
    			" SUM(A.EXEC_ACCOUNT_NUM) AS EXEC_ACCOUNT_NUM,A.OREDER_TYPE,A.DEPT_CODE"+
    		" FROM ("+
    		" SELECT TO_CHAR(M.ORDER_DATE,'YYYYMM') AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'������Ŀ' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT"+
    		" WHERE M.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
	        " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    		" AND M.CAT1_TYPE='LIS'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ORDER_CODE = M.ORDERSET_CODE"+
            " AND M.OPTITEM_CODE  NOT IN ('I3','I4')"+
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND D.DC_DATE IS NULL"+
    		" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY TO_CHAR(M.ORDER_DATE,'YYYYMM'),DEPT.DEPT_CHN_DESC,'������Ŀ',M.DEPT_CODE"+
    		" UNION"+
    		" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'����������' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S"+
    		" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    		" AND M.CAT1_TYPE='PHA'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
            " AND S.CLASSIFY_TYPE IN ('F','I')" +
            " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
            " AND (M.ROUTE_CODE !='IN.I.P' AND M.FREQ_CODE !='.')" +
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR D.DC_DATE IS NULL)"+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'����������',M.DEPT_CODE"+
    		" UNION"+
    		" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'�ڷ�����' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S"+
    		" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    		" AND M.CAT1_TYPE='PHA'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
            " AND S.CLASSIFY_TYPE NOT IN ('F','I')" +
            " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR D.DC_DATE IS NULL)"+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'�ڷ�����',M.DEPT_CODE) A"+
    		" GROUP BY A.ORDER_DATE,A.DEPT_CODE,A.DEPT_CHN_DESC ,A.OREDER_TYPE"+
    		" ORDER BY A.ORDER_DATE,A.DEPT_CODE,A.OREDER_TYPE";
    	}
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//��������
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}
		double accountsum =0;//Ӧִ�����ϼ�
		double execaccountsum =0;//ʵ��ִ�����ϼ�		
		int count  = result.getCount();
		for (int i = 0; i < count; i++) {
			result.setData("SURPLUS_COUNT", i, df1.format((result.getDouble("EXEC_ACCOUNT_NUM",i)/
	    			result.getDouble("ACCOUNT_NUM",i))*100)+"%");			
			accountsum+=StringTool.round(result.getDouble("ACCOUNT_NUM",i),0);
			execaccountsum+=StringTool.round(result.getDouble("EXEC_ACCOUNT_NUM",i),0);	
		}
		result.addData("ORDER_DATE", "�ϼ�");
		result.addData("ACCOUNT_NUM",df.format(accountsum));
		result.addData("EXEC_ACCOUNT_NUM",df.format(execaccountsum));
		result.addData("SURPLUS_COUNT",df1.format((execaccountsum/accountsum)*100)+"%");
    	((TTable) getComponent("TABLE")).setParmValue(result);
  }
    
    /**
     * ���
     */
    public void onExport() {
    	 String title ="";
    	 if(this.getValue("ORDER_TYPE").equals(""))
    	   title ="���һ�ʿ����ִ��ȫ��ͳ�Ʊ�";
    	 else if (this.getValue("ORDER_TYPE").equals("1"))
    	   title ="���һ�ʿ����ִ�м�����Ŀͳ�Ʊ�";
    	 else if (this.getValue("ORDER_TYPE").equals("2"))
    	   title ="���һ�ʿ����ִ������������ͳ�Ʊ�";
    	 else if (this.getValue("ORDER_TYPE").equals("3"))
    	   title ="���һ�ʿ����ִ�пڷ�����ͳ�Ʊ�";   				 
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * ���
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	this.setValue("DEPT_CODE","");
    	this.setValue("ORDER_TYPE","");
 	   this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    /**
     * �����ϸ
     */
    public void onSelect() {
    	int Row = table.getSelectedRow();//����
    	TParm data = table.getParmValue().getRow(Row);//�������    	
//    	 System.out.println("data===========" + data);
    	if(data.getValue("ORDER_DATE").equals("�ϼ�"))
    		return;
        String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+"000000"; //��ʼ����
   	     String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+"235959"; //��������
   	    TParm parm = new TParm();
   	    parm.setData("START_DATE", startdate);
   	    parm.setData("END_DATE", enddate);
   	    parm.setData("ORDER_TYPE", data.getValue("OREDER_TYPE"));//ҽ������
   	    parm.setData("DEPT_CODE", data.getValue("DEPT_CODE"));//���Ҵ���
   	    parm.setData("DEPT_DESC", data.getValue("DEPT_CHN_DESC"));//��������
    	this.openDialog("%ROOT%\\config\\odi\\ODISingleDetail.x",parm);

    }
}
