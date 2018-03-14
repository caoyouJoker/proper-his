package com.javahis.ui.nss;

import java.sql.Timestamp;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 营养粉使用情况查询
 * </p>
 * 
 * <p>
 * Description: 营养粉使用情况查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2015.7.1
 * @version 1.0
 */
public class NSSENNPUsageAmountQueryControl extends TControl {
    public NSSENNPUsageAmountQueryControl() {
        super();
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
		this.onInitPage();
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		table = getTable("TABLE");
		
		// 控件初始化
		this.onInitControl();
	}
	
	/**
	 * 控件初始化
	 */
	public void onInitControl() {
		// 取得当前日期
		Timestamp nowDate = SystemTool.getInstance().getDate();

		// 设定默认展开日期
    	this.setValue("QUERY_DATE_S", StringTool.rollDate(nowDate, -7));
    	this.setValue("QUERY_DATE_E", nowDate);
    	
    	clearValue("NUTRITIONAL_POWDER;ORDER_CODE");
    	
    	table.setParmValue(new TParm());
	}

    /**
     * 查询方法
     */
    public void onQuery() {
		table.setParmValue(new TParm());
		
    	// 获取查询条件数据
    	TParm queryParm = this.getQueryParm();
    	
    	if (queryParm.getErrCode() < 0) {
    		this.messageBox(queryParm.getErrText());
    		return;
    	}
    	
    	// 查询营养粉使用情况
    	TParm result = NSSEnteralNutritionTool.getInstance().queryNutritionalPowderUsage(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("查询营养粉使用情况错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("查无数据");
			table.setParmValue(new TParm());
			return;
		}
		
        table.setParmValue(result);
    }
    
	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
			table.setParmValue(new TParm());
			parm.setErr(-1, "请输入查询时间");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace("-", "")
				+ "000000");
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace("-", "")
				+ "235959");
		
		// 营养粉
		if (StringUtils.isNotEmpty(this.getValueString("NUTRITIONAL_POWDER").trim())) {
			parm.setData("NUTRITIONAL_POWDER", this.getValueString("NUTRITIONAL_POWDER"));
		}
		
		// 药品编码
		if (StringUtils.isNotEmpty(this.getValueString("ORDER_CODE").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		}
		
		return parm;
	}
	
	/**
	 * 营养粉下拉框值改变调用方法
	 */
	public void onChangeValue() {
		// 营养粉代码
		String nutritaionalPowderCode = this.getValueString("NUTRITIONAL_POWDER");
		if (StringUtils.isEmpty(nutritaionalPowderCode)) {
			this.setValue("ORDER_CODE", "");
		} else {
			TParm parm = new TParm();
			parm.setData("FORMULA_CODE", nutritaionalPowderCode);
			parm = NSSEnteralNutritionTool.getInstance().selectDataPFM(parm);
			
			if (parm.getErrCode() < 0) {
				this.messageBox("查询配方字典错误");
				return;
			}
			
			this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE", 0));
		}
	}
	
    /**
     * 清空方法
     */
    public void onClear() {
    	// 初始化页面控件数据
    	this.onInitControl();
    }
    
	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "营养粉使用情况");
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
