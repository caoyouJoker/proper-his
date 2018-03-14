package com.javahis.ui.mro;

import jdo.mro.MRORecordTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

public class MRORecordDataQueryControl extends TControl {

	private TTable table;

	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
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
		this
				.clearValue("DEPT_CODE;MR_NO;PAT_NAME;VS_DR_CODE;OUT_MAIN_DIAG;OP_MAIN_DIAG");
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

		String startDate = this.getValueString("START_DATE").substring(0, 10);
		String endDate = this.getValueString("END_DATE").substring(0, 10);
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("VS_DR_CODE", this.getValueString("VS_DR_CODE"));
		parm.setData("OUT_MAIN_DIAG", this.getValueString("OUT_MAIN_DIAG"));
		parm.setData("OP_MAIN_DIAG", this.getValueString("OP_MAIN_DIAG"));

		// 查询病案首页数据
		TParm result = MRORecordTool.getInstance().queryMroRecordData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无数据");
			return;
		} else {
			table.setParmValue(result);
		}
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

			// modify by huangtt 20160930 EMPI患者查重提示 start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			}
			// modify by huangtt 20160930 EMPI患者查重提示 end

			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.setValue("PAT_NAME", pat.getName());
		}
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

	/**
	 * 汇出
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}

		ExportExcelUtil.getInstance().exportExcel(table, "病案首页");
	}
}
