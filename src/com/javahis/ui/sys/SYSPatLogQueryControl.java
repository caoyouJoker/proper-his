package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Pat;

/**
 * <p>
 * Title: 病患资料修改记录
 * </p>
 * 
 * <p>
 * Description: 病患资料修改记录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangy 2011.5.10
 * @version 1.0
 */
public class SYSPatLogQueryControl extends TControl {

	private TTable table;

	public SYSPatLogQueryControl() {
	}

	/**
	 * 初始化
	 */
	public void onInit() {
		table = this.getTable("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		Object obj = this.getParameter();
		if (obj != null&& obj instanceof TParm) {
			TParm parmData = (TParm) obj;
			Timestamp start=parmData.getTimestamp("IN_DATE");
			String mrNo=parmData.getValue("MR_NO");
			this.setValue("START_DATE",start);
			// 初始化查询区间
			this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/'));
			this.setValue("MR_NO", mrNo);
			onMrNo();
			this.onQuery();
		
		} else {
			// 初始化查询区间
			this.setValue("END_DATE", date.toString().substring(0, 10).replace(
					'-', '/'));
			this.setValue("START_DATE", StringTool.rollDate(date, -7)
					.toString().substring(0, 10).replace('-', '/'));
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String mr_no = this.getValueString("MR_NO");
		if (!"".equals(mr_no)) {
			mr_no = " AND MR_NO = '" + mr_no + "' ";
		}
		String start_date = this.getValueString("START_DATE");
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10);
		String end_date = this.getValueString("END_DATE");
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10);
		String sql = "SELECT MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, "
				+ " ITEM_NEW, OPT_USER, OPT_TERM FROM SYS_PATLOG "
				+ "WHERE OPT_DATE BETWEEN TO_DATE('" + start_date
				+ "000000','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date
				+ "235959','YYYYMMDDHH24MISS') " + mr_no + " ORDER BY OPT_DATE";
		// System.out.println("sql----"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm == null || parm.getCount("MR_NO") <= 0) {
			table.removeRowAll();
			this.messageBox("没有查询数据！");
		} else {
			table.setParmValue(parm);
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		Object obj = this.getParameter();
		if (obj != null&& obj instanceof TParm) {
			TParm parmData = (TParm) obj;
			Timestamp start=parmData.getTimestamp("IN_DATE");
			this.setValue("START_DATE",start);
			// 初始化查询区间
			this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/'));		
		} else {
			// 初始化查询区间
			this.setValue("END_DATE", date.toString().substring(0, 10).replace(
					'-', '/'));
			this.setValue("START_DATE", StringTool.rollDate(date, -7)
					.toString().substring(0, 10).replace('-', '/'));
		}
		this.setValue("MR_NO", "");
		this.setValue("PAT_NAME", "");
		table.removeRowAll();
	}

	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());
		if (pat == null) {
			this.messageBox("查无此病患!");
			this.setValue("PAT_NAME", "");
		} else {
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());
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
