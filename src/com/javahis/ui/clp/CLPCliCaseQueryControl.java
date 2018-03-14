package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.clp.CLPCliCaseQueryTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 临床案例查询UI
 * </p>
 * 
 * <p>
 * Description:临床案例查询窗口控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author wukai 2016.05.24
 * @version 1.0
 */
public class CLPCliCaseQueryControl extends TControl {

	private static final String TABLE = "TABLE";
	private TTable table;

	@Override
	public void onInit() {
		super.onInit();
		((TMenuItem) getComponent("delete")).setEnabled(false);
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		table = getTable(TABLE);
		TParm parm = new TParm();
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_DATE", start);
		this.setValue("END_DATE", end);
		parm.setData("END_DATE", end);
		parm.setData("START_DATE", start);
		parm = CLPCliCaseQueryTool.getNewInstance().onQuery(parm);
		table.setParmValue(parm);
	}

	/**
	 * 获取表格空间
	 */
	private TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 清空查询条件
	 */
	public void onClear() {
		String linkedName = "START_DATE;END_DATE;PATLOGY_DEPT_CODE;PATLOGY_DOC_CODE;MR_NO;CLASSIFY_CODE";
		this.clearValue(linkedName);
		table.setSelectionMode(0);
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_DATE", start);
		this.setValue("END_DATE", end);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 进行查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		// 开始时间
		Timestamp start = (Timestamp) getValue("START_DATE");
		if (start != null) {
			start = Timestamp.valueOf(start.toString().substring(0, 10)
					+ " 00:00:00");
			parm.setData("START_DATE", start);
		}
		//结束时间
		Timestamp end = (Timestamp) getValue("END_DATE");
		if (end != null) {
			end = Timestamp.valueOf(end.toString().substring(0, 10)
					+ " 23:59:59");
			parm.setData("END_DATE", end);
		}
//		String start =  this.getText("START_DATE");
//		if(StringUtils.isEmpty(start)) {
//			parm.setData("START_DATE",Timestamp.valueOf(start.substring(0, 10) + " 00:00:00"));
//		}
//		String end = this.getText("END_DATE");
//		if(StringUtils.isEmpty(end)) {
//			parm.setData("END_DATE", Timestamp.valueOf(end.substring(0, 10) + " 23:59:59"));
//		}
		
		Object temp = null;
		// 科室
		temp = getValue("PATLOGY_DEPT_CODE");
		if (temp != null) {
			parm.setData("PATLOGY_DEPT_CODE", "%" + temp + "%");
		}
		// 医生
		temp = getValue("PATLOGY_DOC_CODE");
		if (temp != null) {
			parm.setData("PATLOGY_DOC_CODE", "%" + temp + "%");
		}
		// 病案号
		temp = getText("MR_NO");
		if (!StringUtils.isEmpty(temp.toString())) {
			parm.setData("MR_NO", "%" + temp + "%");
		}
		// 分类
		temp = getValue("CLASSIFY_CODE");
		if (temp != null) {
			parm.setData("CLASSIFY_CODE", "%" + temp + "%");
		}
		//this.messageBox(parm.toString());
		TParm res = CLPCliCaseQueryTool.getNewInstance().onQuery(parm);
		if(res == null || res.getCount() <=0) {
			this.messageBox("查询暂无此数据！");
		}
		this.table.setParmValue(res);
	}
	
	/**
	 * 删除
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if(row >= 0) {
			TParm parm = table.getParmValue().getRow(row);
			int res = messageBox("提示", "确认删除？", YES_NO_OPTION);
			if(res == YES_OPTION) {
				if(CLPCliCaseQueryTool.getNewInstance().deleteCase(parm)){
					table.removeRow(row);
					this.messageBox("删除成功");
					((TMenuItem) getComponent("delete")).setEnabled(false);
				}else {
					this.messageBox("删除失败");
				}
			}
		} else {
			this.messageBox("请选择一条项目删除");
		}
	}
	
	/**
	 * 表格点击
	 */
	public void onTableClick() {
		((TMenuItem) getComponent("delete")).setEnabled(true);
	}
}
