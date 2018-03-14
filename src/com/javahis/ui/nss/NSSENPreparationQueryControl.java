package com.javahis.ui.nss;

import java.sql.Timestamp;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 肠内营养配制情况查询
 * </p>
 * 
 * <p>
 * Description: 肠内营养配制情况查询
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
 * @author wangb 2015.6.25
 * @version 1.0
 */
public class NSSENPreparationQueryControl extends TControl {
    public NSSENPreparationQueryControl() {
        super();
    }

    private TTable tableM;
    private TTable tableD;

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
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		
		// 配制主表数据单击事件
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
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
    	
    	clearValue("DEPT_CODE;STATION_CODE;MR_NO;DIETITIANS;DIET_TYPE");
    	
    	tableM.setParmValue(new TParm());
    	tableD.setParmValue(new TParm());
	}

    /**
     * 查询方法
     */
    public void onQuery() {
		tableM.setParmValue(new TParm());
		tableD.setParmValue(new TParm());
		
    	// 获取查询条件数据
    	TParm queryParm = this.getQueryParm();
    	
    	if (queryParm.getErrCode() < 0) {
    		this.messageBox(queryParm.getErrText());
    		return;
    	}
    	
    	TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("查询配制数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("查无数据");
			tableM.setParmValue(new TParm());
			tableD.setParmValue(new TParm());
			return;
		}
		
        tableM.setParmValue(result);
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
			tableM.setParmValue(new TParm());
			parm.setErr(-1, "请输入查询时间");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		
		// 科室
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		}
		// 病区
		if (StringUtils.isNotEmpty(this.getValueString("STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		}
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// 营养师
		if (StringUtils.isNotEmpty(this.getValueString("DIETITIANS").trim())) {
			parm.setData("ORDER_DR_CODE", this.getValueString("DIETITIANS"));
		}
		// 饮食种类
		if (StringUtils.isNotEmpty(this.getValueString("DIET_TYPE").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("DIET_TYPE"));
		}
		// 未取消
		parm.setData("CANCEL_FLG", "N");
		
		return parm;
	}
	
	/**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			//modify by huangtt 20160930 EMPI患者查重提示  start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
		            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
		    }
			//modify by huangtt 20160930 EMPI患者查重提示  end
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	/**
	 * 添加对tableM的选中监听事件
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableD.setParmValue(new TParm());
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderD(
				data.getRow(selectedRow));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询配方明细错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		tableD.setParmValue(result);
	}
	
    /**
     * 清空方法
     */
    public void onClear() {
    	// 初始化页面控件数据
    	this.onInitControl();
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
