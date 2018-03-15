package com.javahis.ui.ind;

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
 * Title: 一次性卫生材料统计
 * </p>
 *
 * <p>
 * Description: 一次性卫生材料统计
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

public class INDDisposableHyMatCountControl extends TControl {
	
	public INDDisposableHyMatCountControl() {
		
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
     * 根据记账日期以及医嘱进行查询
     */
    public void onQuery(){
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	String bill_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND　A.BILL_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	bill_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	
    	String sql="SELECT REGION_CODE,ORDER_CODE AS 医嘱代码,ORDER_DESC AS 医嘱名称,SPECIFICATION AS 规格,UNIT_CHN_DESC AS 单位,"
    			+"OWN_PRICE AS 单价,DOSAGE_QTY AS 总量,TOT_AMT AS 总价,COST_CENTER_CHN_DESC AS 执行科室  "
    			+"FROM ("
    				+"SELECT B.REGION_CODE,A.ORDER_CODE,A.EXE_DEPT_CODE, B.ORDER_DESC,B.SPECIFICATION, D.UNIT_CHN_DESC, A.OWN_PRICE , "
    				+"SUM (A.DOSAGE_QTY) AS DOSAGE_QTY,sum( A.OWN_PRICE * A.DOSAGE_QTY) AS TOT_AMT,C.COST_CENTER_CHN_DESC AS COST_CENTER_CHN_DESC "
    				+" FROM IBS_ORDD A, SYS_FEE B,SYS_COST_CENTER C,SYS_UNIT D"
    				+" WHERE A.ORDER_CODE  LIKE 'S1%'"
    				+" AND A.OWN_PRICE >= 2000 "
    				+" AND A.ORDER_CODE = B.ORDER_CODE "
    				+" AND A.EXE_DEPT_CODE = C.COST_CENTER_CODE"
    				+" AND A.MEDI_UNIT = D.UNIT_CODE "
    				+bill_date
    				+" GROUP BY A.ORDER_CODE,A.EXE_DEPT_CODE,B.ORDER_DESC,B.SPECIFICATION,B.REGION_CODE,"
    				+" D.UNIT_CHN_DESC,A.OWN_PRICE,C.COST_CENTER_CHN_DESC "
    				+" ORDER BY A.ORDER_CODE)";
    							

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
        ExportExcelUtil.getInstance().exportExcel(table, "一次性卫生材料统计");
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "START_DATE;END_DATE;ORDER_CODE";
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
