package com.javahis.ui.spc;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 病区备药统计报表
 * </p>
 * 
 * <p>
 * Description: 病区备药统计报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author suny 2015.04.15
 * @version 1.0
 */

public class SPCNoStorageControl extends TControl {
	private TTable table;
	public SPCNoStorageControl() {
		
	}	
	/**
	 * 初始化方法
	 * 
	 */
	public void onInit() {
		table = (TTable) this.getComponent("TABLE");
		String datetime = SystemTool.getInstance().getDate().toString();
		this.setValue("START_TIME", datetime.substring(0, 10).replace("-","/"));
		this.setValue("END_TIME", datetime.substring(0, 10).replace("-", "/"));
		this.setValue("STATION_CODE", Operator.getStation());
	}
	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (StringUtils.isEmpty(this.getValueString("START_TIME"))
				|| StringUtils.isEmpty(this.getValueString("END_TIME"))) {
			this.messageBox("请输入查询时间");
			return;
		}
		
		String startTime = this.getValueString("START_TIME")
				.replaceAll("-", "").substring(0, 8)
				+ "000000";
		String endTime = this.getValueString("END_TIME").replaceAll("-", "")
				.substring(0, 8)
				+ "235959";		
		
		String sql = "SELECT A.DISPENSE_NO,D.APP_ORG_CODE,CASE "
				+ "WHEN B.GOODS_DESC IS NULL "
				+ "THEN B.ORDER_DESC ELSE B.ORDER_DESC || '(' || B.GOODS_DESC || ')'"
				+ "END AS ORDER_DESC, B.SPECIFICATION,"
				+ "A.QTY,E.ACTUAL_QTY,A.ACTUAL_QTY AS OUT_QTY,A.UNIT_CODE,"
				+ "A.VERIFYIN_PRICE AS STOCK_PRICE,"
				+ "A.RETAIL_PRICE AS RETAIL_PRICE,"
				+ "A.BATCH_NO,A.VALID_DATE,B.PHA_TYPE,"
				+ "A.ORDER_CODE,C.STOCK_QTY,C.DOSAGE_QTY,B.TRADE_PRICE,"
				+ "A.SEQ_NO,A.REQUEST_SEQ,A.BATCH_SEQ,F.ORG_CHN_DESC,G.UNIT_CHN_DESC,"
				+ "D.REQTYPE_CODE,C.DOSAGE_UNIT,C.STOCK_UNIT,"
				+ "A.VERIFYIN_PRICE,A.INVENT_PRICE,A.SUP_CODE FROM IND_DISPENSED A,PHA_BASE B,PHA_TRANSUNIT C,"
				+ "IND_DISPENSEM D,IND_REQUESTD E,IND_ORG F,"
				+ "SYS_UNIT G WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.DISPENSE_NO = D.DISPENSE_NO AND A.REQUEST_SEQ = E.SEQ_NO "
				+ "AND D.TO_ORG_CODE = F.ORG_CODE AND A.UNIT_CODE = G.UNIT_CODE "
				+ "AND D.REQUEST_NO = E.REQUEST_NO";

		sql = sql + "  AND D.DISPENSE_DATE BETWEEN " + "TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') " + "AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS')";
		// 病区
		if (StringUtils.isNotEmpty(this.getValueString("STATION_CODE"))) {
			sql = sql + " AND D.APP_ORG_CODE = '"
					+ this.getValueString("STATION_CODE") + "' ";
		}
		// 未入库
		if (this.getRadioButton("STORAGE_N").isSelected()) {
			sql = sql + " AND A.IS_PUTAWAY = 'N' ";
		} else {
			sql = sql + " AND A.IS_PUTAWAY = 'Y' ";
		}
		// 药品种类
		if (this.getRadioButton("COMMON_DRUG").isSelected()) {
			sql = sql + " AND D.DRUG_CATEGORY = '1' ";
		} else {
			sql = sql + " AND D.DRUG_CATEGORY = '2' ";
		}

		sql = sql + "ORDER BY A.DISPENSE_NO ASC,A.SEQ_NO DESC";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("查询错误");
			err("ERR:" + parm.getErrCode() + parm.getErrText());
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("没有可查询的数据!");
			table.removeRowAll();
			return;
		}
		table.setParmValue(parm);
	}
	/**
	 * 清空
	 */	
     public void onClear(){	 
    	table.removeRowAll();
    	String datetime = SystemTool.getInstance().getDate().toString();
 		this.setValue("START_TIME", datetime.substring(0, 10).replace("-","/"));
 		this.setValue("END_TIME", datetime.substring(0, 10).replace("-", "/"));		
 		getRadioButton("STORAGE_N").setSelected(true);
 		getRadioButton("COMMON_DRUG").setSelected(true);
     }   
     /**
      * 导出Excel
      * */
     public void onExport() {
    	 TParm parm = table.getParmValue();
         if (null == parm || parm.getCount("DISPENSE_NO") <= 0) {
             this.messageBox("没有可导出的数据!");
             return;
         }
         ExportExcelUtil.getInstance().exportExcel(table, "病区备药统计报表");
     }
	/**
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
}
