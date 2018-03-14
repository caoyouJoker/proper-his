package com.javahis.ui.odi;

import java.util.Date;

import jdo.odi.ODIPICTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 一期临床批量修改医嘱时间
 * </p>
 * 
 * <p>
 * Description: 一期临床批量修改医嘱时间
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.6.13
 * @version 1.0
 */
public class ODIBatchModOrderDateControl extends TControl {
	private TParm inParm;
	private TTable packTable;
	private TTable orderTable;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 取得传入参数
		Object obj = this.getParameter();
		if (obj != null) {
			inParm = (TParm) obj;
		}
		
		packTable = getTable("PACK_TABLE");
		orderTable = getTable("ORDER_TABLE");
		
		this.onQuery();
	}
	
	/**
	 * 查询
	 */
	private void onQuery() {
		TParm packParm = new TParm();
		packParm.setData("DEPTORDR_CODE", inParm.getValue("DEPT_CODE"));
		// 查询一期临床科室套餐
		TParm packResult = ODIPICTool.getInstance().queryPICPackMain(packParm);
		
		if (packResult.getErrCode() < 0) {
			this.messageBox("查询一期临床科室套餐错误");
			err("ERR:" + packResult.getErrText());
			return;
		} else {
			packTable.setParmValue(packResult);
		}
		
		packParm.setData("CASE_NO", inParm.getValue("CASE_NO", 0));
		// 只查询检验医嘱
		packParm.setData("CAT1_TYPE", "LIS");
		
		// 查询带有一期临床注记的医嘱
		TParm orderResult = ODIPICTool.getInstance().queryPICOrder(packParm);
		if (orderResult.getErrCode() < 0) {
			this.messageBox("查询一期临床医嘱错误");
			err("ERR:" + orderResult.getErrText());
			return;
		} else {
			orderTable.setParmValue(orderResult);
		}
	}
	
	/**
	 * 自动修改医嘱启用时间
	 */
	public void onModifyDate() {
		int selPackTableRow = packTable.getSelectedRow();
		if (selPackTableRow < 0) {
			this.messageBox("请选择相应的套餐");
			return;
		}
		
		if (orderTable.getParmValue() == null
				|| orderTable.getParmValue().getCount() < 1) {
			this.messageBox("没有需要修改的医嘱");
			return;
		}
		
		String packCode = packTable.getParmValue().getRow(selPackTableRow)
				.getValue("PACK_CODE");
		
		// 查询一期临床科室套餐明细
		TParm packOrderResult = ODIPICTool.getInstance().queryPICPackOrder(packCode);
		if (packOrderResult.getErrCode() < 0) {
			this.messageBox("查询一期临床科室套餐明细错误");
			err("ERR:" + packOrderResult.getErrText());
			return;
		} else {
			int orderCount = orderTable.getParmValue().getCount();
			int packOrderCount = packOrderResult.getCount();
			int count = orderCount;
			if (orderCount > packOrderCount) {
				count = packOrderCount;
			}
			
			// 强制失去编辑焦点
			if (orderTable.getTable().isEditing()) {
				orderTable.getTable().getCellEditor().stopCellEditing();
			}
			
			// 药嘱启用时间
			Date phaDate = StringTool.getDate(orderTable
					.getItemString(0, "EFF_DATE").replace("-", "/")
					.substring(0, 19), "yyyy/MM/dd HH:mm:ss");
			Date date = null;
			int intervalTime = 0;
			for (int i = 0; i < count; i++) {
				intervalTime = (int) Math.round(packOrderResult.getDouble(
						"BC_INTERVAL_TIME", i) * 60);
				date = DateUtils.addMinutes(phaDate, intervalTime);
				orderTable.setItem(i, "EFF_DATE", StringTool.getString(date,
						"yyyy/MM/dd HH:mm:ss"));
			}
		}
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		if (orderTable.getParmValue() == null
				|| orderTable.getParmValue().getCount() < 1) {
			this.messageBox("没有需要保存的医嘱");
			return;
		}
		
		TParm tableParm = orderTable.getParmValue();
		TParm orderParm = new TParm();
		orderParm.setData("CASE_NO", tableParm.getValue("CASE_NO", 0));
		orderParm.setData("ORDER_NO", tableParm.getValue("ORDER_NO", 0));
		orderParm.setData("ORDER_NO_LIST", tableParm.getValue("ORDER_NO").replace(
				"[", "").replace("]", "").replace(" ", "").replace(",", "','"));
		
		// 查询医嘱表数据验证该医嘱是否被护士审核
		TParm result = ODIPICTool.getInstance().queryOdiOrder(orderParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询医嘱数据错误");
			err("ERR:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无医嘱数据");
			return;
		}
		
		// 审核注记
		boolean nsCheckFlg = false;
		
		for (int i = 0; i < result.getCount(); i++) {
			if (StringUtils.isNotEmpty(result.getValue("NS_CHECK_CODE", i))) {
				nsCheckFlg = true;
				break;
			}
		}
		
		if (nsCheckFlg) {
			this.messageBox("当前医嘱中存在已审核的数据，请取消审核后再进行操作");
			return;
		} else {
			// 执行保存操作
			result = TIOM_AppServer.executeAction(
					"action.odi.ODIPICAction",
					"onSaveByBatchModOrderDate", tableParm);
			
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("E0001");
				return;
			} else {
				this.messageBox("P0001");
				// 保存完成后刷新医生站界面数据
				inParm.runListener("addListener", result);
				this.closeWindow();
			}
		}
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
