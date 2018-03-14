package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;  

public class SYSPatchFalseLog  
extends TControl {
	// 开始时间
   private String start_date;
	// 结束时间
   private String end_date;
	// 表格
   private TTable table;
   /**
    * 初始化方法
    */
   public void onInit() {
       initPage();
   }
   /**
    * 初始画面数据
    */  
   private void initPage() {
	   this.callFunction("UI|Table|removeRowAll");
	   Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化时间查询区间(一周的区间)
		this.setValue("e_Date", date.toString().substring(0, 10).replace('-',
				'/')  
				+ " 23:59:59");   
		this.setValue("s_Date", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");    
		table = (TTable) this.getComponent("Table");
		this.clearValue("SYSTEM_CODE");      
//		this.setValue("SYSTEM_CODE","FIXED_COST");
//		this.setValue("SYSTEM_CODE","STANDING_ORDER");
//      this.setValue("SYSTEM_CODE","");
   }    
   public void onQuery(){     
	    String systemCode = this.getValueString("SYSTEM_CODE");
	    start_date = this.getValueString("s_Date") ;//begin
	    end_date = this.getValueString("e_Date") ;//end
	    start_date = start_date.replace("-", "");
	    start_date = start_date.replace(":", "");
	    start_date = start_date.replace(" ", "");
	    start_date = start_date.substring(0,14);
	    end_date = end_date.replace("-", "");   
	    end_date = end_date.replace(":", "");
	    end_date = end_date.replace(" ", "");
	    end_date = end_date.substring(0,14);
	    	String sql = " SELECT A.POST_DATE,A.SYSTEM_CODE,A.SEQ,A.CASE_NO,A.MR_NO," +
		     " A.IPD_NO,B.DEPT_CHN_DESC,C.STATION_DESC " +
		     " FROM SYS_FALSE_BATCH_LOG A,SYS_DEPT B,SYS_STATION C" +
		     " WHERE A.DEPT_CODE = B.DEPT_CODE" +    
		     " AND A.STATION_CODE = C.STATION_CODE" +     
		     " AND A.POST_DATE BETWEEN '"+start_date+"' AND '"+end_date+"'";
			 if(systemCode != null && !"".equals(systemCode))
			    {
				 sql = sql + "AND A.SYSTEM_CODE = '"+systemCode+"'";
	            }
				 sql = sql + " ORDER BY A.POST_DATE,A.SYSTEM_CODE,A.SEQ";
		    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (selParm == null || selParm.getCount() < 0) {
				    this.messageBox("查无数据");
				    this.callFunction("UI|Table|removeRowAll");
				    return ;
				}
				table.setParmValue(selParm);    
   }  
	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "批次错误记录");
		}
	}
	/**
	 *  清空
	 */
	public void onClear() {
		table.removeRowAll();
		this.initPage();
	}  
	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("s_Date");
		if (start == null || start.length() <= 0) {
			this.messageBox("开始时间不能为空");
			return false;
		}
		
		String end = this.getValueString("e_Date");
		if (end == null || end.length() <= 0) {
			this.messageBox("结束时间不能为空");
			return false;
		}
		return true;
	}
	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
}
