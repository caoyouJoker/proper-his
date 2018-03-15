package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ����ƹ�����ͳ��
 * </p>
 *
 * <p>
 * Description: ����ƹ�����ͳ��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */



public class OPBRadiologyDeptWorkControl extends TControl {

	public OPBRadiologyDeptWorkControl() {
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	// initPage
	public void initPage() {
		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
		//��������
		setValue("REGION_CODE", "H01");


	}
	
	
	
	/**
     * ���ݼ������ڽ��в�ѯ
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String sql="SELECT REGION_CODE,DEPT_ABS_DESC,STATION_DESC,CRCOUNT,CRSUM,CTCOUNT,CTSUM,MRCOUNT,MRSUM "
    			+"FROM ("
    			+"SELECT B.DEPT_ABS_DESC, C.STATION_DESC, A.CRCOUNT, A.CRSUM, A.CTCOUNT,A.CTSUM, A.MRCOUNT, A.MRSUM , B.REGION_CODE "
    			+" FROM (  "
    					+"SELECT   AA.DEPT_CODE, AA.STATION_CODE,"
    					+" SUM (AA.CRCOUNT) AS CRCOUNT, SUM (AA.CRSUM) AS CRSUM,"
    					+" SUM (AA.CTCOUNT) AS CTCOUNT, SUM (AA.CTSUM) AS CTSUM,"
    					+"SUM (AA.MRCOUNT) AS MRCOUNT, SUM (AA.MRSUM) AS MRSUM "
    					+"  FROM ("
    							+"SELECT   DEPT_CODE, STATION_CODE,COUNT (CASE_NO) AS CRCOUNT,"
    							+"SUM (TOT_AMT) AS CRSUM, 0 AS CTCOUNT,0 AS CTSUM, 0 AS MRCOUNT, 0 AS MRSUM"
    							+" FROM IBS_ORDD"
    							+" WHERE "
    							+bill_date
    							+" AND ORDERSET_CODE LIKE 'Y0101%'"
    							+" GROUP BY DEPT_CODE, STATION_CODE"
    							+" UNION ALL"
    							+" SELECT   DEPT_CODE, STATION_CODE, 0 AS CRCOUNT,0 AS CRSUM, COUNT (CASE_NO) AS CTCOUNT,"
    							+" SUM (TOT_AMT) AS CTSUM, 0 AS MRCOUNT,"
    							+" 0 AS MRSUM"
    							+" FROM IBS_ORDD"
    							+" WHERE"
    							+bill_date
    							+" AND ORDERSET_CODE LIKE 'Y0202%'"
    							+" GROUP BY DEPT_CODE, STATION_CODE"
    							+" UNION ALL"
    							+" SELECT DEPT_CODE, STATION_CODE, 0 AS CRCOUNT,0 AS CRSUM, 0 AS CTCOUNT, 0 AS CTSUM,"
    							+" COUNT (CASE_NO) AS MRCOUNT,"
    							+" SUM (TOT_AMT) AS MRSUM"
    							+" FROM IBS_ORDD"
    							+" WHERE"
    							+bill_date
    							+" AND ORDERSET_CODE LIKE 'Y0303%'"
    							+" GROUP BY DEPT_CODE, STATION_CODE) AA "
	    			+" GROUP BY AA.DEPT_CODE, AA.STATION_CODE) A,"
	    			+" SYS_DEPT B,"
	    			+" SYS_STATION C"
	    			+" WHERE A.DEPT_CODE = B.DEPT_CODE AND A.STATION_CODE = C.STATION_CODE)";
    	

    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(parm.getErrCode() < 0 ){
    		this.messageBox(parm.getErrText());
    		return;
    	}
        if(parm.getCount() <= 0)
        {
        	this.messageBox("��������");
        }
    	       
      //��table����ʾ��ѯ��Ϣ
    	TTable  table = (TTable)this.getComponent("TTable") ;
    	
    	table.setParmValue(parm);
    	
    }
	
    
    
    
    /**
     * ���Excel
     */
    public void onExport() {
        TTable table = this.getTable("TTable");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "��ҽѧ�ƹ�����ͳ��");
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "START_DATE;END_DATE";
        this.clearValue(clearStr);

		// ��ʼ��ͳ������
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (����25)
		setValue("END_DATE", dateTime);

		// ��ʼʱ��(�ϸ���26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
        TTable  table = this.getTable("TTable");

        table.removeRowAll();
    }
	
	
	
	
	
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
	
	
	

}