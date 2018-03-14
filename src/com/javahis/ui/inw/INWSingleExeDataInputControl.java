package com.javahis.ui.inw;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title: 护士单次执行数据补录控制类
 * </p>
 * 
 * <p>
 * Description: 护士单次执行数据补录控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangb 2016/10/17
 * @version 1.0
 */
public class INWSingleExeDataInputControl extends TControl {
	
	private TTable table;
	/** 就诊号 */
	private String caseNo;

	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.grabFocus("MR_NO");
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		String mrNo = this.getValueString("MR_NO").trim();
		String barCode = this.getValueString("BAR_CODE").trim();
		
		if (StringUtils.isEmpty(mrNo)) {
			this.messageBox("请先输入病案号");
			return;
		}
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT TO_CHAR (TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME, 'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS') NS_EXEC_DATE,");
		sbSql.append("B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,B.FREQ_CODE,B.ROUTE_CODE,B.DR_NOTE,B.ORDER_DR_CODE,");
		sbSql.append("CASE WHEN B.CAT1_TYPE = 'PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,");
		sbSql.append("A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.CAT1_TYPE,A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,");
		sbSql.append("B.START_DTTM,B.END_DTTM,A.LATE_REASON,C.MR_NO ");
		sbSql.append("FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,ADM_INP D ");
		sbSql.append("WHERE B.MR_NO = D.MR_NO AND A.CASE_NO = D.CASE_NO  AND A.CASE_NO = B.CASE_NO AND A.CASE_NO = C.CASE_NO ");
		sbSql.append("AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ AND A.ORDER_NO = C.ORDER_NO AND A.ORDER_SEQ = C.ORDER_SEQ ");
		sbSql.append("AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM AND (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE) ");
		if (getRadioButton("UNCOMPLETE").isSelected()) {
			sbSql.append("AND A.NS_EXEC_DATE_REAL IS NULL AND A.NS_EXEC_CODE_REAL IS NULL ");
		} else {
			sbSql.append("AND A.NS_EXEC_DATE_REAL IS NOT NULL AND A.NS_EXEC_CODE_REAL IS NOT NULL ");
		}
		
		sbSql.append(" AND B.MR_NO = '");
		sbSql.append(mrNo);
		sbSql.append("' AND A.CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' ");

		if (StringUtils.isNotEmpty(barCode)) {
			sbSql.append(" AND (C.MED_APPLY_NO = '");
			sbSql.append(barCode);
			sbSql.append("' OR A.BAR_CODE = ' ");
			sbSql.append(barCode);
			sbSql.append("')");
		}

		sbSql.append(" ORDER BY A.ORDER_NO, A.ORDER_SEQ ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询单次执行数据错误");
			err("查询单次执行数据错误:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无数据");
			return;
		} else {
			table.setParmValue(result);
		}
	}
	
	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			
			String sql = "SELECT A.PAT_NAME,B.CHN_DESC FROM SYS_PATINFO A, SYS_DICTIONARY B "
					+ " WHERE A.SEX_CODE = B.ID AND B.GROUP_ID = 'SYS_SEX' AND A.MR_NO = '"
					+ mrNo + "' ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询病患基本信息错误");
				err("查询病患基本信息错误:" + result.getErrText());
				return;
			} else if (result.getCount() < 1) {
				this.messageBox("查无该病患基本信息");
				return;
			} else {
				this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
				this.setValue("SEX", result.getValue("CHN_DESC", 0));
				
				sql = "SELECT CASE_NO FROM ADM_INP WHERE CANCEL_FLG <> 'Y' AND MR_NO = '"
						+ mrNo + "' ORDER BY CASE_NO DESC";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getErrCode() < 0) {
					this.messageBox("查询病患住院信息错误");
					err("查询病患住院信息错误：" + result.getErrText());
					return;
				} else if (result.getCount() < 1) {
					this.messageBox("查无该病患住院基本信息");
					return;
				} else {
					caseNo = result.getValue("CASE_NO", 0);
				}
				
				this.grabFocus("BAR_CODE");
			}
		}
	}
	
	/**
	 * 根据录入条码号查询医嘱信息
	 */
	public void onBarCode() {
		String barCode = this.getValueString("BAR_CODE").trim();
		
		if (StringUtils.isEmpty(barCode)) {
			return;
		} else {
			String sql = "SELECT * FROM MED_APPLY WHERE APPLICATION_NO = '"
				+ barCode + "' AND CAT1_TYPE IN ('LIS','RIS')";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				this.messageBox("查询条码信息错误");
				err("查询条码信息错误:" + result.getErrText());
				return;
			} else if (result.getCount() < 1) {
				this.messageBox("查无该条码信息");
				this.setValue("BAR_CODE", "");
				return;
			}
			
			String mrNo = this.getValueString("MR_NO").trim();
			if (StringUtils.isEmpty(mrNo)) {
				this.setValue("MR_NO", result.getValue("MR_NO", 0));
				this.onMrNo();
			}
			
			caseNo = result.getValue("CASE_NO", 0);
			this.onQuery();
		}
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		table.acceptText();
		String sql = "UPDATE ODI_DSPND SET NS_EXEC_DATE_REAL=TO_DATE('#','YYYY/MM/DD HH24:MI:SS'),NS_EXEC_CODE_REAL='#',LATE_REASON='#' "
				+ " WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ='#' AND ORDER_DATE='#' AND ORDER_DATETIME='#'";
		
		TParm parm = table.getParmValue();
		TParm reslut = new TParm();
		int count = table.getRowCount();
		int notInputCount = 0;
		int execSuccessCount = 0;
		int errSaveCount = 0;
		String execDate = "";
		String execCode = "";
		
		// 查询结果集多条数据的情况下，同时没有录入执行时间和执行人的数据不予处理
		for (int i = 0; i < count; i++) {
			execDate = table.getItemString(i, "NS_EXEC_DATE_REAL");
			execCode = table.getItemString(i, "NS_EXEC_CODE_REAL");
			if ((StringUtils.isNotEmpty(execDate) && StringUtils
					.isEmpty(execCode))
					|| (StringUtils.isEmpty(execDate) && StringUtils
							.isNotEmpty(execCode))) {
				this.messageBox("第" + (i + 1) + "行数据，执行时间和执行人需要同时填写");
				return;
			}
			
			if (!(StringUtils.isNotEmpty(execDate) && StringUtils
					.isNotEmpty(execCode))) {
				notInputCount++;
			}
		}
		
		if (notInputCount == count) {
			this.messageBox("无保存数据,请录入执行时间和执行人");
			return;
		}
		
		String updateSql = "";
		for (int i = 0; i < count; i++) {
			execDate = table.getItemString(i, "NS_EXEC_DATE_REAL");
			execCode = table.getItemString(i, "NS_EXEC_CODE_REAL");
			if (StringUtils.isNotEmpty(execDate)
					&& StringUtils.isNotEmpty(execCode)) {
				updateSql = sql;
				updateSql = updateSql.replaceFirst("#",
						execDate.substring(0, 19).replace("-", "/"))
						.replaceFirst("#", execCode).replaceFirst("#",
								table.getItemString(i, "LATE_REASON"))
						.replaceFirst("#", parm.getValue("CASE_NO", i))
						.replaceFirst("#", parm.getValue("ORDER_NO", i))
						.replaceFirst("#", parm.getValue("ORDER_SEQ", i))
						.replaceFirst("#", parm.getValue("ORDER_DATE", i))
						.replaceFirst("#", parm.getValue("ORDER_DATETIME", i));
				reslut = new TParm(TJDODBTool.getInstance().update(updateSql));
				if (reslut.getErrCode() < 0) {
					err("单次执行数据补录失败:" + reslut.getErrText());
					errSaveCount++;
				} else {
					execSuccessCount++;
				}
			}
		}
		
		if (execSuccessCount > 0 && errSaveCount == 0) {
			this.messageBox("保存成功");
			this.onClear();
			return;
		} else if (execSuccessCount > 0 && errSaveCount > 0) {
			this.messageBox("部分数据未能成功保存，请重新录入后再次保存");
			this.onQuery();
		} else if (execSuccessCount == 0 && errSaveCount > 0) {
			this.messageBox("保存失败");
		}
	}
	
	/**
	 * 切换完成状态
	 */
	public void onChange() {
		if (getRadioButton("UNCOMPLETE").isSelected()) {
			callFunction("UI|save|enabled", true);
		} else {
			callFunction("UI|save|enabled", false);
		}
		table.setParmValue(new TParm());
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.clearText("MR_NO;PAT_NAME;SEX;BAR_CODE");
		getRadioButton("UNCOMPLETE").setSelected(true);
		this.onChange();
		this.grabFocus("MR_NO");
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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
}
