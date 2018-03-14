package com.javahis.ui.udd;

import java.sql.Timestamp;

import jdo.odi.ODISingleExeTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 药品条码核对
 * </p>
 * 
 * <p>
 * Description: 药品条码核对
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
 * @author wangb 2015.12.7
 * @version 1.0
 */
public class UDDDrugBarCodeCheckControl extends TControl {

	private TTable table;
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	// 表格点选事件
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.onInitPage();
    }
    
    /**
	 * 初始化页面
	 */
	public void onInitPage() {
		this.setValue("QY", Operator.getRegion());
		clearValue("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;SEX_CODE;BED_NO;AGE;BLOOD_TYPE;ICD;BAR_CODE;CASE_NO");
		table.setParmValue(new TParm());
		getRadioButton("UNCHECK").setSelected(true);
		Timestamp now = SystemTool.getInstance().getDate();
		Timestamp yes = StringTool.rollDate(now, -1);
		setValue("START_DATE", yes);
		setValue("END_DATE", now);
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		String startDate = this.getValueString("START_DATE").substring(0, 19);
		String endDate = this.getValueString("END_DATE").substring(0, 19);
		String caseNo = this.getValueString("CASE_NO");
		String deptCode = this.getValueString("DEPT_CODE");
		String stationCode = this.getValueString("STATION_CODE");
		String checkFlg = "N";
		if (getRadioButton("CHECKED").isSelected()) {
			checkFlg = "Y";
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("CAT1_TYPE", "PHA");
		parm.setData("DEPT_CODE", deptCode);
		parm.setData("STATION_CODE", stationCode);
		parm.setData("CHECK_FLG", checkFlg);
		
		TParm result = this.queryOrderInfo(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			this.messageBox("查询数据错误");
			return;
		} else if (result.getCount() < 1) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");
			return;
		} else {
			table.setParmValue(result);
		}
	}
	
	/**
	 * 条码号回车
	 */
	public void onBarCodeQuery() {
		table.setParmValue(new TParm());
		clearValue("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;SEX_CODE;BED_NO;AGE;BLOOD_TYPE;ICD;CASE_NO");
		String barCode = getValueString("BAR_CODE");
		String startDate = this.getValueString("START_DATE").substring(0, 19);
		String endDate = this.getValueString("END_DATE").substring(0, 19);
		String caseNo = this.getValueString("CASE_NO");
		String deptCode = this.getValueString("DEPT_CODE");
		String stationCode = this.getValueString("STATION_CODE");
		String checkFlg = "N";
		if (getRadioButton("CHECKED").isSelected()) {
			checkFlg = "Y";
		}
		
		TParm parm = new TParm();
		TParm result = new TParm();
		TParm tableResult = new TParm();
		TParm updateResult = new TParm();
		
		if (barCode.contains("@")) {
			String[] str = barCode.split("@");
			if (str.length <= 2) {
				this.messageBox("扫描条码内容异常");
				return;
			}
			String mrNo = str[0];// 病案号
			String orderdataStr = str[1];// 医嘱处方信息
			String orderDateStr = str[2];// 医嘱餐次时间
			String orderNo = "";// 处方号
			String orderSeq = "";// 处方序号
			String orderDate = "";// 医嘱日期
			String orderDatetime = "";// 医嘱时间
			
			String sql = "SELECT * FROM ADM_INP WHERE MR_NO = '" + mrNo + "' AND (CANCEL_FLG IS NULL OR CANCEL_FLG = 'N') ORDER BY CASE_NO DESC";
			caseNo = new TParm(TJDODBTool.getInstance().select(sql)).getValue("CASE_NO", 0);
			
			if (orderDateStr.contains("|")) {
				orderDate = orderDateStr
						.substring(0, orderDateStr.indexOf("|")).trim();
				if (orderDateStr.substring(orderDateStr.indexOf("|"),
						orderDateStr.length()).length() >= 4)
					orderDatetime = orderDateStr
							.substring(orderDateStr.indexOf("|") + 1,
									orderDateStr.length()).substring(0, 4)
							.trim();
			}
			if (orderdataStr.contains(";")) {
				String[] order = orderdataStr.split(";");
				int count = order.length;
				for (int i = 0; i < count; i++) {
					if (order[i].contains(",")) {
						boolean udFlg = orderDatetime.startsWith("2355")?false:true;//是否为长期(暂定时间2355为临时)
						orderNo = order[i].substring(0, order[i].indexOf(","))
								.trim();
						orderSeq = order[i].substring(
								order[i].indexOf(",") + 1, order[i].length())
								.trim();
						parm = new TParm();
						parm.setData("CASE_NO", caseNo);
						parm.setData("START_DATE", startDate);
						parm.setData("END_DATE", endDate);
						parm.setData("CAT1_TYPE", "PHA");
						parm.setData("ORDER_NO", orderNo);
						parm.setData("ORDER_SEQ", orderSeq);
						parm.setData("ORDER_DATE", orderDate);
						parm.setData("ORDER_DATETIME", orderDatetime);
						parm.setData("UD_FLG", udFlg);
						parm.setData("DEPT_CODE", deptCode);
						parm.setData("STATION_CODE", stationCode);
						parm.setData("CHECK_FLG", checkFlg);
						
						// 根据条码号查询数据
						result = this.queryOrderInfo(parm);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrText());
							this.messageBox("查询数据错误");
							return;
						} else {
							if (tableResult.getCount() < 1) {
								tableResult = result;
							} else if (result.getCount() > 0) {
								tableResult.addParm(result);
							}
						}
					}
				}
			}
		}
		
		if (tableResult.getCount() < 1) {
			this.messageBox("查无数据");
		}
		table.setParmValue(tableResult);
		
		if (getRadioButton("UNCHECK").isSelected()) {
			if (tableResult.getCount() > 0) {
				for (int i = 0; i < tableResult.getCount(); i++) {
					updateResult = this.updateCheckFlg(tableResult.getRow(i));
					if (updateResult.getErrCode() == 0) {
						table.setItem(i, "CHECK_FLG", "Y");
					}
				}
			}
		}
		
		// 清空条码号
		clearValue("BAR_CODE");
	}
	
	/**
	 * 病案号回车事件
	 */
	public void onMrNoEnter() {
		clearValue("DEPT_CODE;STATION_CODE;PAT_NAME;SEX_CODE;BED_NO;AGE;BLOOD_TYPE;ICD;CASE_NO");
		// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			// modify by huangtt 20160928 EMPI患者查重提示 start
			this.setValue("MR_NO", mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				mrNo =  pat.getMrNo();
				this.setValue("MR_NO", mrNo);
			}
		
//			mrNo = pat.getMrNo();
//			this.setValue("MR_NO", mrNo);
			
			// modify by huangtt 20160928 EMPI患者查重提示 start
			this.onQueryPatInfo();
		}
	}
	
	/**
	 * 查询病患信息
	 */
	private void onQueryPatInfo() {
		String mrNo = getValueString("MR_NO");
		String deptCode = getValueString("DEPT_CODE");
		String stationCode = getValueString("STATION_CODE");
		String sql = ODISingleExeTool.getInstance().queryPatInfo(mrNo,
				deptCode, stationCode);
		TParm patInfo = new TParm(TJDODBTool.getInstance().select(sql));
		if (patInfo.getCount() <= 0) {
			this.messageBox("没有病患！");
			return;
		}
		
		setValue("MR_NO", mrNo);
		setValue("DEPT_CODE", patInfo.getValue("DEPT_CODE",0));
		setValue("STATION_CODE", patInfo.getValue("STATION_CODE",0));
		setValue("CASE_NO", patInfo.getValue("CASE_NO",0));
		setValue("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
		setValue("SEX_CODE", patInfo.getValue("CHN_DESC", 0));
		setValue("BED_NO", patInfo.getValue("BED_NO_DESC", 0));
		setValue("ICD", patInfo.getValue("ICD",0));
		setValue("BLOOD_TYPE", patInfo.getValue("BLOOD_TYPE", 0));
		String age = "";
		if (StringUtils.isNotEmpty(patInfo.getValue("BIRTH_DATE", 0))) {
			age = OdiUtil.showAge((Timestamp) patInfo.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate());
		}
		setValue("AGE", age);
    }
	
	/**
	 * 根据条码号查询数据
	 */
	public TParm queryOrderInfo(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT CHECK_FLG,B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS') AS NS_EXEC_DATE,");
		sbSql.append(" B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,");
		sbSql.append(" B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,A.ORDER_NO||A.ORDER_SEQ  AS BAR_CODE,A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,");
		sbSql.append(" B.ORDERSET_GROUP_NO,B.CAT1_TYPE,A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,A.BARCODE_1,A.BARCODE_2,A.BARCODE_3,B.MR_NO");
		sbSql.append(" FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D ");
		sbSql.append(" WHERE 1 = 1 AND B.ROUTE_CODE = 'PO' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND A.CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("'");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sbSql.append(" AND A.ORDER_NO = '");
			sbSql.append(parm.getValue("ORDER_NO"));
			sbSql.append("'");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND A.ORDER_SEQ = ");
			sbSql.append(parm.getValue("ORDER_SEQ"));
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND B.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("STATION_CODE"))) {
			sbSql.append(" AND B.STATION_CODE = '");
			sbSql.append(parm.getValue("STATION_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("START_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("END_DATE"))) {
			sbSql.append(" AND B.PHA_DOSAGE_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("START_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('");
			sbSql.append(parm.getValue("END_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("UD_FLG"))) {
			sbSql.append(" AND A.ORDER_DATE='");
			sbSql.append(parm.getValue("ORDER_DATE"));
			sbSql.append("'  AND A.ORDER_DATETIME='");
			sbSql.append(parm.getValue("ORDER_DATETIME"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
			sbSql.append(" AND B.CAT1_TYPE = '");
			sbSql.append(parm.getValue("CAT1_TYPE"));
			sbSql.append("' ");
		}
		
		// 核对注记
		if (StringUtils.equals("Y", parm.getValue("CHECK_FLG"))) {
			sbSql.append(" AND A.CHECK_FLG = 'Y' ");
		} else {
			sbSql.append(" AND (A.CHECK_FLG IS NULL OR A.CHECK_FLG <> 'Y') ");
		}
		
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.ORDER_NO = B.ORDER_NO ");
		sbSql.append(" AND A.ORDER_SEQ = B.ORDER_SEQ ");
		sbSql.append(" AND A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO ");
		sbSql.append(" AND A.ORDER_NO = C.ORDER_NO ");
		sbSql.append(" AND A.ORDER_SEQ = C.ORDER_SEQ ");
		sbSql.append(" AND B.ROUTE_CODE=D.ROUTE_CODE(+) ");
		sbSql.append(" ORDER BY A.ORDER_NO,A.ORDER_SEQ ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		return result;
	}
	
	/**
	 * 更新核对状态
	 */
	public TParm updateCheckFlg(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE ODI_DSPND SET CHECK_FLG = 'Y' WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getValue("ORDER_SEQ"));
		sbSql.append(" AND ORDER_DATE = '");
		sbSql.append(parm.getValue("ORDER_DATE"));
		sbSql.append("' AND ORDER_DATETIME = '");
		sbSql.append(parm.getValue("ORDER_DATETIME"));
		sbSql.append("' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE = '");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
		if (result.getErrCode() < 0) {
			this.messageBox("核对失败");
		}
		return result;
	}
	
	/**
	 * 添加对tablePat的监听事件
	 * 
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		clearValue("DEPT_CODE;STATION_CODE;PAT_NAME;SEX_CODE;BED_NO;AGE;BLOOD_TYPE;ICD;CASE_NO");
		setValue("MR_NO", table.getParmValue().getRow(row).getValue("MR_NO"));
		onQueryPatInfo();
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.onInitPage();
		grabFocus("BAR_CODE");
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
