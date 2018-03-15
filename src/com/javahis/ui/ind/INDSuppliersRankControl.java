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
 * Title: 供应商排名
 * </p>
 *
 * <p>
 * Description: 供应商排名
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


public class INDSuppliersRankControl extends TControl {
	
	
	
	public INDSuppliersRankControl() {
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
     * 根据审查日期以及sup_code进行查询
     */
    public void onQuery(){
    	
    	
    	String sup_code=getValueString("SUP_CODE");
    	sup_code="='"+sup_code+"'";
    	
    	if("=''".equals(sup_code)){
    		sup_code=" IN (SELECT SUP_CODE FROM SYS_SUPPLIER) ";
    	}
    	
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	
    	
    	
    	String check_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	check_date +=" AND A.CHECK_DATE BETWEEN TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	check_date +=" AND TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	String sql="SELECT D.REGION_CODE,D.ORDER_DESC AS ORDER_DESC, D.GOODS_DESC AS GOODS_DESC, D.SPECIFICATION,"
    			  +"E.UNIT_CHN_DESC,H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC,"
    			  +"SUM(B.VERIFYIN_QTY) AS QTY, B.VERIFYIN_PRICE,"
    			  +"SUM(B.VERIFYIN_QTY) * B.VERIFYIN_PRICE AS VER_AMT,"
    			  +"B.RETAIL_PRICE AS OWN_PRICE,B.RETAIL_PRICE * SUM(B.VERIFYIN_QTY) AS OWN_AMT,"
    			  +"B.RETAIL_PRICE * SUM(B.VERIFYIN_QTY) - SUM(B.VERIFYIN_QTY) * B.VERIFYIN_PRICE AS DIFF_AMT,"
    			  +"D.MAN_CODE "
    			  +"FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE D, SYS_UNIT E, PHA_TRANSUNIT F,PHA_BASE G, PHA_DOSE H, SYS_PHAROUTE I "
    			  +"WHERE A.VERIFYIN_NO = B.VERIFYIN_NO AND B.ORDER_CODE = D.ORDER_CODE "
    			  +"AND B.BILL_UNIT = E.UNIT_CODE AND B.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE "
    			  +"AND B.UPDATE_FLG IN ('1', '3') AND B.ORDER_CODE = G.ORDER_CODE AND D.ORDER_CODE = G.ORDER_CODE "
    			  +"AND G.DOSE_CODE=H.DOSE_CODE(+) "
    			  +"AND G.ROUTE_CODE = I.ROUTE_CODE "
    			  +"AND F.ORDER_CODE = F.ORDER_CODE AND A.ORG_CODE = '040101' "
    			  +check_date
    			  +" AND A.SUP_CODE "
    			  +sup_code
    			  +" GROUP BY D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE,D.REGION_CODE, "
    			  +"B.RETAIL_PRICE, B.ORDER_CODE,D.MAN_CODE,H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC "
    			  +"ORDER BY B.ORDER_CODE";
    			  
    	
    	TParm newdata = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
    	
        
      //在table中显示查询信息
    	TTable  table = (TTable)this.getComponent("TTable") ;
        table.setParmValue(newdata);
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
        ExportExcelUtil.getInstance().exportExcel(table, "供应商排名");
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "SUP_CODE;START_DATE;END_DATE";
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
