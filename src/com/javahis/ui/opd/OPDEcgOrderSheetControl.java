package com.javahis.ui.opd;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>
 * Title: 胸痛心电医嘱列表
 * </p>
 * 
 * <p>
 * Description: 胸痛心电医嘱列表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.6.7
 * @version 1.0
 */
public class OPDEcgOrderSheetControl extends TControl {

	private TTable table;
	private TParm parameterParm; // 页面传输参数
	
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (!(obj instanceof TParm)) {
				return;
			}
			this.parameterParm = (TParm) obj;
		} else {
			return;
		}
		
		this.onInitPage();
	}
	
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		table = (TTable) getComponent("ECG_TABLE");
		// 表格数据双击事件
		this.callFunction("UI|ECG_TABLE|addEventListener", "ECG_TABLE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");
		if (null != parameterParm) {
			// 从配置文件中取得胸痛中心心电医嘱
			String orderCode = TConfig.getSystemValue("CPC_ECG_ORDER");
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("SELECT A.ORDER_CODE,A.ORDER_DESC,");
			sbSql.append("SUM(B.DOSAGE_QTY * C.OWN_PRICE) AS AMT,A.UNIT_CODE ");
			sbSql.append(" FROM SYS_FEE A,SYS_ORDERSETDETAIL B,SYS_FEE C ");
			sbSql.append(" WHERE A.ORDER_CODE = B.ORDERSET_CODE AND B.ORDER_CODE = C.ORDER_CODE ");
			sbSql.append(" AND A.ACTIVE_FLG = 'Y' AND A.EMG_FIT_FLG = 'Y' AND A.ORDER_CAT1_CODE = 'ECC' ");
			sbSql.append(" AND A.RPTTYPE_CODE = 'Y040A' ");
			if (StringUtils.isNotEmpty(orderCode)) {
				sbSql.append(" AND A.ORDER_CODE IN ('" + orderCode.replace(",", "','") + "') ");
			}
			sbSql.append(" GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.UNIT_CODE ");
			sbSql.append(" ORDER BY A.ORDER_CODE ");
			
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sbSql.toString()));

			if (result.getErrCode() < 0) {
				this.messageBox("查询集合医嘱错误");
				err("查询集合医嘱错误：" + result.getErrText());
				table.setParmValue(new TParm());
				return;
			}

			table.setParmValue(result);
		}
	}
	
	/**
	 * 表格数据双击事件
	 */
	public void onTableDoubleClicked(int row) {
		TParm data = table.getParmValue();
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}
		TParm selectedParm = data.getRow(selectedRow);
		this.setReturnValue(selectedParm);
		this.closeWindow();
	}
}
