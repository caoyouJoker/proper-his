package com.javahis.ui.adm;

import com.dongyang.control.*;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.adm.ADMMedQueryTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;

/**
 * <p>
 * Title: 在院患者医技进度查询
 * </p>
 * 
 * <p>
 * Description: 在院患者医技进度查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2010-11-11
 * @version 1.0
 */
public class ADMMedQueryControl extends TControl {
	public void onInit() {
		super.onInit();
		initDate();
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				if (parm.getValue("POPEDEM").length() != 0) {
					// 一般权限
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// 角色权限
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// 最高权限
					if ("3".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSDBA", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSOPERATOR", false);
					}
				}
			}
		}
		onInitPopeDem();
	}

	/**
	 * 初始化权限
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			this.setValue("USER_ID", Operator.getID());
			this.setValue("STATION_CODE", Operator.getStation());
			this.callFunction("UI|USER_ID|onQuery");
			this.callFunction("UI|STATION_CODE|onQuery");
		}
		if (this.getPopedem("SYSOPERATOR")) {
			this.setValue("USER_ID", Operator.getID());
			this.setValue("STATION_CODE", Operator.getStation());
			this.callFunction("UI|USER_ID|onQuery");
			this.callFunction("UI|STATION_CODE|onQuery");		
		}
		if (this.getPopedem("SYSDBA")) {
			
		}
	}
	/**
	 * 初始化日期combo
	 */
	public void initDate() {
		Timestamp now = SystemTool.getInstance().getDate();
		String today = StringTool.getString(now, "yyyyMMdd");
		String nextDay = StringTool.getString(StringTool.rollDate(now, 1),
				"yyyyMMdd");
		this.setValue("ORDERDATE_S",
				StringTool.getTimestamp(today + "000000", "yyyyMMddHHmmss"));
		this.setValue("ORDERDATE_E",
				StringTool.getTimestamp(nextDay + "235959", "yyyyMMddHHmmss"));
		this.setValue("DATE_S", "");
		this.setValue("DATE_E", "");
	}

	/**
	 * 检验检查筛选功能
	 */
	public void onSUB_SYSTEM_CODE() {
		this.clearValue("RPTTYPE_CODE");
		this.callFunction("UI|RPTTYPE_CODE|onQuery");
	}

	/**
	 * 病区筛选功能
	 */
	public void onDEPT_CODE() {
		this.clearValue("STATION_CODE");
		this.callFunction("UI|STATION_CODE|onQuery");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (this.getValue("ORDERDATE_S") == null) {
			this.grabFocus("ORDERDATE_S");
			this.messageBox_("请选择开单起始时间");
			return;
		}
		if (this.getValue("ORDERDATE_E") == null) {
			this.grabFocus("ORDERDATE_E");
			this.messageBox_("请选择开单截止时间");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORDERDATE_S", this.getValue("ORDERDATE_S"));
		parm.setData("ORDERDATE_E", this.getValue("ORDERDATE_E"));
		if (this.getValueString("DATE_S").length() > 0) {
			parm.setData("DATE_S", this.getValue("DATE_S"));
		}
		if (this.getValueString("DATE_E").length() > 0) {
			parm.setData("DATE_E", this.getValue("DATE_E"));
		}
		if (this.getValueString("SUB_SYSTEM_CODE").length() > 0) {
			parm.setData("SUB_SYSTEM_CODE", this.getValue("SUB_SYSTEM_CODE")
					+ "%");
		}
		if (this.getValueString("RPTTYPE_CODE").length() > 0) {
			parm.setData("RPTTYPE_CODE", this.getValue("RPTTYPE_CODE"));
		}
		if (this.getValueString("DEPT_CODE").length() > 0) {
			parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		if (this.getValueString("STATION_CODE").length() > 0) {
			parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
		}
		if (this.getValue("MR_NO") != null
				&& this.getValueString("MR_NO").length() > 0) {
			String MR_NO = PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO"));
			// modify by huangtt 20160928 EMPI患者查重提示 start
			this.setValue("MR_NO", MR_NO);
			Pat pat = Pat.onQueryByMrNo(MR_NO);
			if (!StringUtil.isNullString(MR_NO) && !MR_NO.equals(pat.getMrNo())) {
				this.messageBox("病案号" + MR_NO + " 已合并至 " + "" + pat.getMrNo());
				MR_NO= pat.getMrNo();
			}	
			// modify by huangtt 20160928 EMPI患者查重提示 start
			parm.setData("MR_NO", MR_NO);
			this.setValue("MR_NO", MR_NO);
		}
		// ==========pangben modify 20110511 start 添加区域参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110511 stop
		TParm result = ADMMedQueryTool.getInstance().selectData(parm);
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			((TTable) this.getComponent("Table")).removeRowAll();
			this.messageBox("E0008");
			return;
		}
		((TTable) this.getComponent("Table")).setParmValue(result);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DATE_S;DATE_E;RPTTYPE_CODE;SUB_SYSTEM_CODE;STATION_CODE;DEPT_CODE;MR_NO");
		initDate();
		((TTable) this.getComponent("Table")).removeRowAll();
	}

	/**
	 * 导出EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("Table"),
				"医技进度");
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
}
