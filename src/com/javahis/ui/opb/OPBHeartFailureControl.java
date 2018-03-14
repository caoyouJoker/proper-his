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
 * Title: 心衰
 * </p>
 *
 * <p>
 * Description: 心衰
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BlueCore
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */


public class OPBHeartFailureControl extends TControl {
	
	public OPBHeartFailureControl() {
		
	}
	
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	// initPage
	public void initPage() {
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
		//设置区域
		setValue("REGION_CODE", "H01");

		

	}
	
	
	
	/**
     * 根据记账日期进行查询
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String ds_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	ds_date +=" DS_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	ds_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String today=eDate.substring(0, 8).replace(" ", "").replace("/", "").
    				 replace(":", "").replace("-", "");
    	
    	String sql="SELECT a.REGION_CODE,c.MR_NO,a.CASE_NO,c.PAT_NAME,c.CTZ1_CODE,f.DEPT_CHN_DESC AS IN_DESC,e.DEPT_CHN_DESC AS OUT_DESC,a.IN_DATE,a.DS_DATE,c.BIRTH_DATE,"
    			+"ceil(( to_date('"+today+"','yyyymmdd')-c.BIRTH_DATE)/366) as AGE,d.USER_NAME "
    			+"FROM ADM_INP a,ADM_INPDIAG b,SYS_PATINFO c,SYS_OPERATOR d,SYS_DEPT e,SYS_DEPT f "
    			+" WHERE "
    			+ds_date
    			+" AND a.DS_DATE IS NOT NULL "
    			+" AND a.CANCEL_FLG = 'N'"
    			+" AND a.CASE_NO = b.CASE_NO"
    			+" AND b.IO_TYPE = 'O'"
    			+" AND b.ICD_CODE LIKE 'I21.0%'"
    			+" AND a.MR_NO = c.MR_NO"
    			+" AND a.DS_DEPT_CODE = e.DEPT_CODE"
    			+" AND a.IN_DEPT_CODE = f.DEPT_CODE"
    			+" AND a.VS_DR_CODE = d.USER_ID"
    			+" ORDER BY c.MR_NO";

    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getErrCode() < 0 ){
    		this.messageBox(parm.getErrText());
    		return;
    	}
        if(parm.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
        
      //在table中显示查询信息
    	TTable  table = (TTable)this.getComponent("TTable") ;
    	
    	table.setParmValue(parm);
    	
    }
	
    
    
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TTable");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "心衰");
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "START_DATE;END_DATE";
        this.clearValue(clearStr);

		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
	
	
	

}
