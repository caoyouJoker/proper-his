package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.adm.ADMTransLogTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:转移记录
 * </p>
 * 
 * <p>
 * Description:转移记录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class ADMTransLogControl extends TControl {
	TTable table;
	TParm Parm = new TParm(); // 接参
	private String mrNo;
	private String caseNo;
	private String ipdNo;
	private Timestamp admDate;
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		Parm = new TParm();
		if (obj instanceof TParm) {
			Parm = (TParm) obj;
		}
		this.setCaseNo(Parm.getData("ADM", "CASE_NO").toString());
		this.setIpdNo(Parm.getData("ADM", "IPD_NO").toString());
		this.setMrNo(Parm.getData("ADM", "MR_NO").toString());
		this.setAdmDate(Parm.getTimestamp("ADM", "ADM_DATE"));
		this.initUI(Parm);
		table = this.getTable("TRANTABLE");
		this.onQuery();
	}

	/**
	 * 初始化界面
	 */
	public void initUI(TParm Parm) {
		setValue("MR_NO", this.getMrNo());
		setValue("IPD_NO", this.getIpdNo());
		Pat pat = new Pat();
		pat = pat.onQueryByMrNo(this.getMrNo());
		setValue("PAT_NAME", pat.getName());
		String admFlg = Parm.getData("ADM", "ADM_FLG").toString();
		if ("Y".equals(admFlg)) {
			setValue("ADMIN", "Y");
		} else {
			setValue("ADMOUT", "Y");
		}
		setValue("START_DATE", this.getAdmDate()); // 入院日期
		setValue("END_DATE", SystemTool.getInstance().getDate()); // 预定日期
		this.callFunction("UI|save|setEnabled", false);
		this.callFunction("UI|MR_NO|setEnabled", false);
		this.callFunction("UI|IPD_NO|setEnabled", false);
		this.callFunction("UI|PAT_NAME|setEnabled", false);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();

		parm.setData("CASE_NO", this.getCaseNo());
		// 病案号
		if (this.getValueString("MR_NO").length() > 0
				&& this.getValueString("MR_NO") != null) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// 住院号
		if (this.getValueString("IPD_NO").length() > 0
				&& this.getValueString("IPD_NO") != null) {
			parm.setData("IPD_NO", this.getValueString("IPD_NO"));
		}
		// 起始日期
		if (this.getValueString("START_DATE").length() > 0
				&& this.getValueString("START_DATE") != null) {
			String startStr = this.getValueString("START_DATE");
			parm.setData("START_DATE", this.dateFormat(startStr));
		}
		// 结束日期
		if (this.getValueString("END_DATE").length() > 0
				&& this.getValueString("END_DATE") != null) {
			String endstr = this.getValueString("END_DATE");
			parm.setData("END_DATE", this.dateFormat(endstr));
		}
		TParm result = ADMTransLogTool.getInstance().selectData(parm);
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		int count = result.getCount();
		TParm tableparm = new TParm();
		for (int i = 0; i < count; i++) {
			tableparm.addData("MR_NO", result.getValue("MR_NO", i));
			tableparm.addData("PAT_NAME", result.getValue("PAT_NAME", i));
			tableparm.addData("IPD_NO", result.getValue("IPD_NO", i));
			String indatestr = result.getValue("IN_DATE", i);
			Timestamp date = StringTool.getTimestamp(indatestr,
					"yyyyMMdd HHmmss");
			tableparm.addData("IN_DATE", date);
			tableparm.addData("PSF_KIND", result.getValue("PSF_KIND", i));
			tableparm.addData("DEPT_ATTR_FLG",
					result.getValue("DEPT_ATTR_FLG", i));
			tableparm.addData("OUT_DATE", result.getTimestamp("OUT_DATE", i));
			tableparm.addData("OUT_STATION_CODE",
					result.getValue("OUT_STATION_CODE", i));
			tableparm.addData("OUT_DEPT_CODE",
					result.getValue("OUT_DEPT_CODE", i));
			tableparm.addData("IN_STATION_CODE",
					result.getValue("IN_STATION_CODE", i));
			tableparm.addData("IN_DEPT_CODE",
					result.getValue("IN_DEPT_CODE", i));
		}
		table.setParmValue(tableparm);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中需要保存的数据");
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("CASE_NO", this.getCaseNo());
		inparm.setData("IN_DATE", dateFormat(this.getValueString("IN_DATE")));
		inparm.setData("DEPT_ATTR_FLG", this.getValueString("DEPT_ATTR_FLG"));
		inparm.setData("PSF_KIND", this.getValueString("PSF_KIND"));
		inparm.setData("OUT_DATE", this.getValueString("OUT_DATE").equals("")?"":StringTool.getTimestamp(
				this.getValueString("OUT_DATE").substring(0, 19),
				"yyyy-MM-dd HH:mm:ss"));
		inparm.setData("OUT_DEPT_CODE", this.getValueString("OUT_DEPT_CODE"));
		inparm.setData("OUT_STATION_CODE",
				this.getValueString("OUT_STATION_CODE"));
		inparm.setData("IN_DEPT_CODE", this.getValueString("IN_DEPT_CODE"));
		inparm.setData("IN_STATION_CODE",
				this.getValueString("IN_STATION_CODE"));
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		inparm.setData("OPT_TERM", Operator.getIP());
		TParm result = ADMTransLogTool.getInstance().updateData(inparm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败！");
			return;
		}
		this.messageBox("保存成功！");
		onQuery();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		String Str = "OUT_DEPT_CODE;OUT_STATION_CODE;DEPT_ATTR_FLG;IN_DEPT_CODE;IN_STATION_CODE;PSF_KIND;IN_DATE;OUT_DATE";
		this.clearValue(Str);
		this.initUI(Parm);

	}

	/**
	 * 删除
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择删除的数据！");
			return;
		}
		TParm parm = table.getParmValue().getRow(row);
		TParm deleteParm = new TParm();
		deleteParm.setData("CASE_NO", this.getCaseNo());
		deleteParm.setData("IN_DATE",
				dateFormat(this.getValueString("IN_DATE")));
		TParm result = ADMTransLogTool.getInstance().deleteData(deleteParm);
		if (result.getErrCode() < 0) {
			this.messageBox("删除失败！");
			table.removeRowAll();
			return;
		}
		this.messageBox("删除成功！");
		table.removeRow(row);
		this.onQuery();
	}

	/**
	 * 单击事件
	 */
	public void ontableClick() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		this.callFunction("UI|save|setEnabled", true);
		TParm tableparm = table.getParmValue();
		String linkNames = "OUT_DEPT_CODE;OUT_STATION_CODE;DEPT_ATTR_FLG;IN_DEPT_CODE;IN_STATION_CODE;PSF_KIND;IN_DATE;OUT_DATE";
		this.setValueForParm(linkNames, tableparm, row);
	}

	/**
	 * @return the mrNo
	 */
	public String getMrNo() {
		return mrNo;
	}

	/**
	 * @param mrNo
	 *            the mrNo to set
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * @return the caseNo
	 */
	public String getCaseNo() {
		return caseNo;
	}

	/**
	 * @param caseNo
	 *            the caseNo to set
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	/**
	 * @return the ipdNo
	 */
	public String getIpdNo() {
		return ipdNo;
	}

	/**
	 * @param ipdNo
	 *            the ipdNo to set
	 */
	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	/**
	 * @return the admDate
	 */
	public Timestamp getAdmDate() {
		return admDate;
	}

	/**
	 * @param admDate
	 *            the admDate to set
	 */
	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
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

	/**
	 * 得到TTextFormat对象
	 * 
	 * @param tagName
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 格式化
	 * 
	 * @param datestr
	 * @return
	 */
	private String dateFormat(String datestr) {
		if (datestr.length() > 18)
			datestr = datestr.substring(0, 19);
		Timestamp date = StringTool
				.getTimestamp(datestr, "yyyy-MM-dd HH:mm:ss");
		String str = StringTool.getString(date, "yyyyMMdd HHmmss");
		return str;
	}
}
