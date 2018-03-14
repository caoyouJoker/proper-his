package com.javahis.ui.mro;

import jdo.mro.MROTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 外籍人员统计查询报表</p>
 *
 * <p>Description: 外籍人员统计查询报表</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2017.12.29
 * @version 1.0
 */
public class MROForeignerDataQueryControl extends TControl {

	private TTable table;
	
	public MROForeignerDataQueryControl() {
		super();
	}
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = (TTable)this.getComponent("TABLE");
		this.onInitPage();
    }
    
    /**
	 * 初始化页面
	 */
	public void onInitPage() {
		// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 设定默认展开日期
    	this.setValue("START_DATE", todayDate);
    	this.setValue("END_DATE", todayDate);
    	this.clearValue("DEPT_CODE;TOTAL");
    	((TRadioButton)this.getComponent("STATUS_ALL")).setSelected(true);
    	table.setParmValue(new TParm());
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		if (!checkData()) {
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", this.getValueString("START_DATE").substring(0, 10));
		queryParm.setData("END_DATE", this.getValueString("END_DATE").substring(0, 10));
		queryParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		if (((TRadioButton)getComponent("STATUS_IN")).isSelected()) {
			queryParm.setData("STATUS", "IN");
		} else if (((TRadioButton)getComponent("STATUS_OUT")).isSelected()) {
			queryParm.setData("STATUS", "OUT");
		}
		
		// 查询外籍人员住院数据
		TParm result = MROTool.getInstance().queryForeignerData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无数据");
			this.setValue("TOTAL", "0");
			return;
		} else {
			table.setParmValue(result);
			this.setValue("TOTAL", String.valueOf(result.getCount()));
			return;
		}
	}
	
	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "外籍人员统计");
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.onInitPage();
	}
	
	/**
	 * 数据合理性验证
	 * 
	 * @return
	 */
	private boolean checkData() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		if (StringUtils.isEmpty(startDate)) {
			this.messageBox("请填写查询开始日期");
			return false;
		}
		if (StringUtils.isEmpty(endDate)) {
			this.messageBox("请填写查询截止日期");
			return false;
		}
		
		return true;
	}
}
