package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 医生诊查费
 * </p>
 *
 * <p>
 * Description: 医生诊查费
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

public class INDDocExaminedFeeControl extends TControl {
	
	
	public INDDocExaminedFeeControl() {
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
						+ TypeTool.getString(date).substring(5, 7) + "/"
						+ TypeTool.getString(date).substring(8, 10)
						+ " 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月最后一天)
		setValue("END_DATE", StringTool.rollDate(dateTime, 0));
		// 起始时间(本月第一天)
		setValue("START_DATE",
				StringTool.rollDate(dateTime, -1).toString().substring(0, 4)
						+ "/"
						+ StringTool.rollDate(dateTime, -2).toString()
								.substring(5, 7) + "/01 00:00:00");
		

	}
	
	
	
	/**
     * 根据记账日期进行查询
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String sql="SELECT b.REALDEPT_CODE,b.REALDR_CODE, c.DEPT_CHN_DESC,"
    			+"d.USER_NAME,SUM( CLINIC_FEE_REAL) AS CLINIC_FEE_REAL,COUNT (REALDR_CODE) AS REALDR_CODE_COUNT,"
    			+"'' AS SIGN,"
    			+"'' AS REMARK  "
    			+"FROM BIL_REG_RECP a,REG_PATADM b,SYS_DEPT c,SYS_OPERATOR d "
    			+"WHERE  a.CASE_NO = b.CASE_NO AND b.REALDEPT_CODE = c.DEPT_CODE "
    			+"AND b.REALDR_CODE = D.USER_ID "
    			+bill_date
    			+" GROUP BY b.REALDEPT_CODE,b.REALDR_CODE,c.DEPT_CHN_DESC, d.USER_NAME"
    			+" ORDER BY b.REALDEPT_CODE,b.REALDR_CODE";
         

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
        ExportExcelUtil.getInstance().exportExcel(table, "医生诊查费用");
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
     					+ TypeTool.getString(date).substring(5, 7) + "/"
     					+ TypeTool.getString(date).substring(8, 10)
     					+ " 23:59:59", "yyyy/MM/dd HH:mm:ss");

        // (本月最后一天)
        setValue("END_DATE", StringTool.rollDate(dateTime, 0));
        // 起始时间(本月第一天)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -3).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
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
