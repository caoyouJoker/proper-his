package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdo.ins.INSDownloadPayTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;



/**
 * 
 * <p>
 * Title: 下载支付信息
 * </p>
 * 
 * <p>
 * Description:下载支付信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.02.08
 * @version 1.0
 */
public class INSDownloadPayControl extends TControl {
	TParm data;
	/**
	 * 城职
	 */
	public static String CHENGZHI = "1";
	/**
	 * 城居
	 */
	public static String CHENGJU = "2";
	/**
	 * 人员类别 城职 城居
	 */
	String downloadType = "";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	private TParm regionParm;
    INSDriveBatches driverBatches = new INSDriveBatches(regionParm);
    TParm PARMDATA = new TParm();
	public void onInit() {
		super.onInit();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		onClear();
	}

	/**
	 * 增加对tab1TABLE的监听
	 * 
	 * @param row
	 *            int
	 */
	public void onTab1TableClicked(int row) {
		TTable table = (TTable) getComponent("tab1Table");
		row = table.getSelectedRow();
		if (row < 0)
			return;
		TParm data = (TParm) callFunction("UI|tTabbedPane_1|tPanel_1|tab1Table|getParmValue");
		((TTextField) getComponent("tab2OfferBatchNumber")).setValue(data
				.getValue("CODE", row)
				+ data.getValue("REPORT_CODE", row));
		this.setValue("tab3OfferBatchNumber", data.getValue("CODE", row)
				+ data.getValue("REPORT_CODE", row));
	}

	/**
	 * 增加对tab2TABLE的监听
	 * 
	 * @param row
	 *            int
	 */
	public void onTab2TableClicked(int row) {
		TTable table = (TTable) getComponent("tab2Table");
		row = table.getSelectedRow();
		if (row < 0)
			return;
		TParm data = (TParm) callFunction("UI|tTabbedPane_1|tPanel_2|tab2Table|getParmValue");

		((TTextField) getComponent("seqNumber")).setValue(data.getValue(
				"CONFIRM_NO", row));
		PARMDATA.setData("MR_NO",data.getValue("MR_NO", row));
		PARMDATA.setData("CASE_NO",data.getValue("CASE_NO", row));
		
	}

	/**
	 * 报盘批号下载
	 */
	public void onTab1Download() {
		((TTable) getComponent("tab1Table")).setParmValue(new TParm());
		if (StringUtil.isNullString(getText("startDate"))) {
			this.messageBox("请输入开始期号。");
			return;
		}
		if (StringUtil.isNullString(getText("endDate"))) {
			this.messageBox("请输入结束期号。");
			return;
		}
		if (StringUtil.isNullString((String) getValue("tab1ComboBox"))) {
			this.messageBox("请选择人员类别。");
			return;
		}
		TComboBox box = (TComboBox) getComponent("tab1ComboBox");
		TParm parm = new TParm();
		parm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		downloadType = box.getValue();
		String startDate = getText("startDate").replace("/", "")
				.substring(0, 6);
		String endDate = getText("endDate").replace("/", "").substring(0, 6);
		if (box.getValue().equals(CHENGZHI)) {
			parm.addData("PAYMENT_START_CODE", startDate);
			parm.addData("PAYMENT_END_CODE", endDate);
			parm.addData("PARM_COUNT", 3);
			parm.setData("PIPELINE", "DataDown_rs");
			parm.setData("PLOT_TYPE", "J");
			((TTable) getComponent("tab1Table")).setParmMap("REPORT_CODE");
		} else if (box.getValue().equals(CHENGJU)) {
			parm.addData("START_CODE", startDate);
			parm.addData("END_CODE", endDate);
			parm.addData("PARM_COUNT", 3);
			parm.setData("PIPELINE", "DataDown_czyd");
			parm.setData("PLOT_TYPE", "F");
			((TTable) getComponent("tab1Table")).setParmMap("CODE");
		}
		TParm data = InsManager.getInstance().safe(parm, "");
		data.setCount(data.getCount("REPORT_CODE"));
		((TTable) getComponent("tab1Table")).setParmValue(data);
	}

	/**
	 * 病患列表下载
	 */
	public void onTab2Download() {
		((TTable) getComponent("tab2Table")).setParmValue(new TParm());
		TParm parm = new TParm();
		if (StringUtil.isNullString(getText("tab2OfferBatchNumber"))) {
			this.messageBox("请选择报盘批号。");
			return;
		}
		// ======================== chenxi
		if (StringUtil.isNullString((String) getValue("tab2ComboBox"))) {
			this.messageBox("请选择下载状态");
			return;
		}
		// ====================== chenxi modify
		if (downloadType.equals(CHENGZHI)) {
			parm.addData("REPORT_CODE", getText("tab2OfferBatchNumber"));
			parm.addData("PARM_COUNT", 1);
			parm.setData("PIPELINE", "DataDown_rs");
			parm.setData("PLOT_TYPE", "L");
		} else if (downloadType.equals(CHENGJU)) {
			parm.addData("CODE", getText("tab2OfferBatchNumber"));
			parm.addData("PARM_COUNT", 1);
			parm.setData("PIPELINE", "DataDown_czyd");
			parm.setData("PLOT_TYPE", "H");
		}
		TParm data = InsManager.getInstance().safe(parm, "");
		// 解析医保结果集生成查询条件
		String whereSQL = getReportCode(data, "CONFIRM_NO");
		TParm serachTParm = new TParm();
		serachTParm.setData("CONFIRM_NO", whereSQL);
		// 查询数据
		TParm serachData = INSDownloadPayTool.getInstance().selectdata(
				serachTParm);
		if (serachData.getErrCode() < 0) {
			messageBox(serachData.getErrText());
			return;
		}
		// 封装查询结果集
		Map serachMap = packagDate(serachData, "CONFIRM_NO");
		// 合并医保本地结果集
		TParm showParm = mergeData(data, serachMap);
		if (showParm.getCount() == 0) {
			this.messageBox("查无数据");
		}
		((TTextField) getComponent("PAT_NUM")).setValue(showParm.getCount()
				+ "");// add by wanglong 20120911 增加病患数量信息
		if (downloadType.equals(CHENGZHI)) {
			((TTable) getComponent("tab2Table"))
					.setParmMap("CONFIRM_NO;SEAL_DATE;PAT_NAME;SEX_CODE;MR_NO;CHARGE_DATE;IN_STATUS");
		} else {
			((TTable) getComponent("tab2Table"))
					.setParmMap("CONFIRM_NO;CONFIRM_DATE;PAT_NAME;SEX_CODE;MR_NO;CHARGE_DATE;IN_STATUS");
		}
		((TTable) getComponent("tab2Table")).setParmValue(showParm);
	}

	/**
	 * 合并医保本地结果集
	 * 
	 * @param parm
	 * @param TParm
	 * @return
	 */
	private TParm mergeData(TParm insparm, Map serachMap) {
		TParm showParm = new TParm();
		String whereSql = "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		int count = 0;
		int insparmCount = ((List) insparm.getData("CONFIRM_NO")).size();
		String comBox = this.getValueString("tab2ComboBox");
		if (comBox.equals("1")) {
			whereSql = "AND A.IN_STATUS ='4' ";
		} else if (comBox.equals("2"))
			whereSql = "AND A.IN_STATUS <>'4' ";
		for (int i = 0; i < insparmCount; i++) {
			String confirmNo = (String) insparm.getData("CONFIRM_NO", i);
			Map rowMap = (Map) serachMap.get(confirmNo);
			showParm
					.addData("CONFIRM_DATE", insparm.getData("CONFIRM_DATE", i));
			// TParm result = new
			// TParm(TJDODBTool.getInstance().select("SELECT MR_NO,SEX_CODE,PAT_NAME FROM INS_IBS WHERE ADM_SEQ='"+confirmNo+"'"));
			TParm result = new TParm(
					TJDODBTool.getInstance().select(
									" SELECT A.MR_NO,A.SEX_CODE,A.PAT_NAME,B.CHARGE_DATE,A.IN_STATUS,A.CASE_NO"
											+ " FROM INS_ADM_CONFIRM A, BIL_IBS_RECPM B"
											+ " WHERE A.CASE_NO = B.CASE_NO(+)"
											+ " AND A.ADM_SEQ='" + confirmNo
											+ "'" + whereSql));

			if (result != null && result.getCount() > 0) {
				showParm.addData("CONFIRM_NO", confirmNo);
				showParm.addData("SEAL_DATE", insparm.getData("SEAL_DATE", i));
				showParm.addData("PAT_NAME", result.getValue("PAT_NAME", 0));
				showParm.addData("SEX_CODE", result.getValue("SEX_CODE", 0));
				showParm.addData("MR_NO", result.getValue("MR_NO", 0));
				showParm
						.addData("CHARGE_DATE",
								null != result.getValue("CHARGE_DATE", 0)
										&& result.getValue("CHARGE_DATE", 0)
												.length() > 0 ? result
										.getValue("CHARGE_DATE", 0).substring(
												0, 10) : "");
				showParm.addData("IN_STATUS", result.getValue("IN_STATUS", 0).equals("4") ? "已下载"
						: "未下载");
				showParm.addData("CASE_NO", result.getValue("CASE_NO", 0));
				count++;
			} else {
				if (comBox.equals("0")) {
					showParm.addData("CONFIRM_NO", confirmNo);
					showParm.addData("SEAL_DATE", insparm.getData("SEAL_DATE",
							i));
					showParm.addData("PAT_NAME", "");
					showParm.addData("SEX_CODE", "");
					showParm.addData("MR_NO", "");
					showParm.addData("CHARGE_DATE", "");
					showParm.addData("IN_STATUS", "错误");
					showParm.addData("CASE_NO", "");
					count++;
				}
			}
		}
		showParm.setCount(count);
		return showParm;
	}

	/**
	 * 封装查询结果
	 * 
	 * @param parm
	 * @param columnName
	 * @return
	 */
	private Map packagDate(TParm parm, String columnName) {
		Map map = new HashMap();
		if (parm.getCount() < 1) {
			return map;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			Map rowMap = new HashMap();
			rowMap.put("CONFIRM_NO", parm.getData("CONFIRM_NO", i));
			rowMap.put("PAT_NAME", parm.getData("PAT_NAME", i));
			rowMap.put("SEX_CODE", parm.getData("SEX_CODE", i));
			rowMap.put("MR_NO", parm.getData("MR_NO", i));
			map.put(parm.getData(columnName, i), rowMap);
		}
		return map;
	}

	/**
	 * 获取数据库查询条件
	 * 
	 * @param parm
	 * @param columnName
	 * @return
	 */
	private String getReportCode(TParm parm, String columnName) {
		int parmCount = ((List) parm.getData(columnName)).size();
		if (parm.getData().size() < 1) {
			return "";
		}
		String whereSQL = "'";
		for (int i = 0; i < parmCount; i++) {
			// System.out.println("parm=" + parm);
			if (parmCount == 1) {
				whereSQL += parm.getData(columnName, i) + "',";
			} else if (i == 0) {
				whereSQL += parm.getData(columnName, i) + "',";

			} else if (i == parmCount - 1) {
				whereSQL += "'" + parm.getData(columnName, i) + "',";
			} else {
				whereSQL += "'" + parm.getData(columnName, i) + "',";
			}

		}
		return whereSQL.substring(0, whereSQL.length() - 1);
	}

	/**
	 * 个人下载
	 */
	public void onTab4DownloadButtonDownload() {
		((TTable) getComponent("tab3Table")).setParmValue(new TParm());
		if (StringUtil.isNullString(getText("seqNumber"))) {
			this.messageBox("请选择就诊序号。");
			return;
		}
		TParm data = new TParm();
		String sql = "";
		// ===============pangben 2012-6-13 start 修改
		// 城职
		if (downloadType.equals(CHENGZHI)) {
			data = tab4DownloadByCZ(getText("seqNumber"),PARMDATA);
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_rs' AND PLOT_TYPE='I' AND IN_OUT='OUT' ORDER BY ID";

		} else if (downloadType.equals(CHENGJU)) {
			// 城居
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_czyd' AND PLOT_TYPE='E' AND IN_OUT='OUT' ORDER BY ID";
			data = tab4DownloadByCJ(getText("seqNumber"),PARMDATA);
		}
		if (data.getErrCode() < 0) {
			this.messageBox("就诊序号:" + getText("seqNumber") + data.getErrText());
			((TTable) getComponent("tab4Table")).setParmValue(new TParm());
			return;
		}
		getTableValue(sql, "tab4Table", data);
		onTab2Download();
	}

	/**
	 * 给表格赋值
	 * 
	 * @param sql
	 * @param tableName
	 */
	private void getTableValue(String sql, String tableName, TParm data) {
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		StringBuffer parmMap = new StringBuffer();
		StringBuffer header = new StringBuffer();
		for (int i = 2; i < selparm.getCount(); i++) {
			parmMap.append(selparm.getValue("COLUMN_NAME", i)).append(";");
			if (i == 2 || i == 7 || i == 13 || i == 14 || i == 15 || i == 16
					|| i == 17) {
				header.append(selparm.getValue("COLUMN_DESC", i)).append(
						",140;");
			} else {
				header.append(selparm.getValue("COLUMN_DESC", i)).append(
						",100;");
			}

		}
		String headerString = header.substring(0, header.toString()
				.lastIndexOf(";"));
		String parmMapString = parmMap.substring(0, parmMap.toString()
				.lastIndexOf(";"));
		((TTable) getComponent(tableName)).setParmMap(parmMapString);
		((TTable) getComponent(tableName)).setHeader(headerString);
		data.setCount(data.getCount("CONFIRM_NO"));
		((TTable) getComponent(tableName)).setParmValue(data);
	}

	/**
	 * 支付信息下载
	 */
	public void onTab3DownloadButtonPay() {
		String sql = null;
		TParm data = null;
		if (StringUtil.isNullString(getText("tab3OfferBatchNumber"))) {
			this.messageBox("请选择报盘批号。");
			return;
		}
		// 城职
		if (downloadType.equals(CHENGZHI)) {
			data = tab3DownloadByCZ();
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_rs' AND PLOT_TYPE='K' AND IN_OUT='OUT' ORDER BY ID";

		} else if (downloadType.equals(CHENGJU)) {
			// 城居
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_czyd' AND PLOT_TYPE='G' AND IN_OUT='OUT' ORDER BY ID";
			data = tab3DownLoadByCJ();
		}
		if (data.getErrCode() < 0) {
			((TTable) getComponent("tab3Table")).setParmValue(new TParm());
			this.messageBox("下载支付信息失败");
			return;
		}
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		StringBuffer parmMap = new StringBuffer();
		StringBuffer header = new StringBuffer();
		for (int i = 2; i < selparm.getCount(); i++) {
			parmMap.append(selparm.getValue("COLUMN_NAME", i)).append(";");
			if (i == 2 || i == 7 || i == 13 || i == 14 || i == 15 || i == 16
					|| i == 17) {
				header.append(selparm.getValue("COLUMN_DESC", i)).append(
						",150;");
			} else {
				header.append(selparm.getValue("COLUMN_DESC", i)).append(
						",120;");
			}

		}
		String headerString = header.substring(0, header.toString()
				.lastIndexOf(";"));
		String parmMapString = parmMap.substring(0, parmMap.toString()
				.lastIndexOf(";"));
		((TTable) getComponent("tab3Table")).setParmMap(parmMapString);
		((TTable) getComponent("tab3Table")).setHeader(headerString);
		data.setCount(data.getCount("REPORT_CODE"));
		((TTable) getComponent("tab3Table")).setParmValue(data);
	}

	/**
	 * 城居下载支付信息
	 * 
	 * @return
	 */
	private TParm tab3DownLoadByCJ() {
		// TParm actionParm=getDownLoadParm("");
		TParm DataDown_czyd_G = DataDown_czyd_G();
		// // 插入表INS_IBS_PAY
		if (DataDown_czyd_G.getErrCode() < 0) {
			this.messageBox("下载失败");
			return DataDown_czyd_G;
		}
		return DataDown_czyd_G;
	}

	/**
	 * 获取城值支付信息
	 * 
	 * @return
	 */
	private TParm DataDown_czyd_G() {
		TParm parm = new TParm();
		TParm data = new TParm();
		// 支付信息下载接口
		parm.addData("HOSP_NHI_NO", regionParm.getData("NHI_NO", 0).toString());
		parm.addData("CODE", getText("tab2OfferBatchNumber"));
		parm.addData("PARM_COUNT", 2);
		parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "G");
		data = InsManager.getInstance().safe(parm, "");
		return data;
	}

	/**
	 * 获取城居支付信息
	 * 
	 * @return
	 */
	private TParm DataDown_rs_K() {
		TParm parm = new TParm();
		parm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		parm.addData("REPORT_CODE", getText("tab2OfferBatchNumber"));
		parm.addData("PARM_COUNT", 2);
		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "K");
		TParm data = InsManager.getInstance().safe(parm, "");
		return data;
	}

	/**
	 * 获取城职费用明细和拒付信息
	 * 
	 * @return
	 */
	private TParm detailCZ(String seqNumber) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "I");
		TParm data = InsManager.getInstance().safe(parm, "");
		return data;
	}

	/**
	 * 获取城居费用明细和拒付信息
	 * 
	 * @return
	 */
	private TParm detailCJ(String seqNumber) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "E");
		TParm data = InsManager.getInstance().safe(parm, "");
		return data;
	}

	/**
	 * tab3Data城职结算明细下载
	 */
	private TParm tab3DownloadByCZ() {
		TParm actionParm = getDownLoadParm("",new TParm());
		// 查询DataDown_rs_K
		TParm DataDown_rs_K = DataDown_rs_K();
		actionParm.setData("DataDown_rs_K", DataDown_rs_K.getData());
		// 插入表INS_IBS_PAY
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSDownloadPayAction", "saveDataPay", actionParm);
		if (result.getErrCode() < 0) {
			this.messageBox("下载失败");
			return result;
		}
		return DataDown_rs_K;
	}

	/**
	 * 入参共用
	 * 
	 * @param seqNumber
	 * @return
	 */
	private TParm getDownLoadParm(String seqNumber,TParm parmdata) {
		TParm actionParm = new TParm();
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("NHI_HOSP_CODE", hospital);
		actionParm.setData("ADM_SEQ", seqNumber);
		actionParm.setData("REPORT_CODE", getText("tab2OfferBatchNumber"));
		if (downloadType.equals(CHENGZHI)) {
			actionParm.setData("INS_TYPE", "01");// 城职	
		} else if (downloadType.equals(CHENGJU)) {
			actionParm.setData("INS_TYPE", "02");// 城乡	
		}
		actionParm.setData("MR_NO",parmdata.getValue("MR_NO"));
		actionParm.setData("CASE_NO",parmdata.getValue("CASE_NO"));
//		System.out.println("actionParm============"+actionParm);
		return actionParm;
	}

	/**
	 * 下载结算信息城职
	 * 
	 * @param seqNumber
	 * @return
	 */
	private TParm tab4DownloadByCZ(String seqNumber,TParm parmdata) {
		// 结算信息下载
		TParm actionParm = getCommDownload(seqNumber, "DataDown_sp", "G",parmdata);
		if (actionParm.getErrCode() < 0) {
			return actionParm;
		}
		TParm adm_Confirm = new TParm();
		adm_Confirm.setData("ADM_SEQ", seqNumber);
		actionParm.setData("adm_Confirm", adm_Confirm.getData());

		// I方法
		// 拒付信息下载
		TParm tab4Download_I_E = detailCZ(seqNumber);
		if (tab4Download_I_E.getErrCode() < 0) {
			return tab4Download_I_E;
		}
		actionParm.setData("tab4Download_I_E", tab4Download_I_E.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSDownloadPayAction", "saveData", actionParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return tab4Download_I_E;
	}

	/**
	 * 下载结算信息城居
	 * 
	 * @param seqNumber
	 * @return
	 */
	private TParm tab4DownloadByCJ(String seqNumber,TParm parmdata) {
		// 结算信息下载
		TParm actionParm = getCommDownload(seqNumber, "DataDown_czys", "J",parmdata);
		if (actionParm.getErrCode() < 0) {
			return actionParm;
		}
		// I方法
		// 拒付信息下载
		TParm tab4Download_I_E = detailCJ(seqNumber);
		if (tab4Download_I_E.getErrCode() < 0) {
			return tab4Download_I_E;
		}
		actionParm.setData("tab4Download_I_E", tab4Download_I_E.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSDownloadPayAction", "saveData", actionParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return tab4Download_I_E;
	}

	public TParm getCommDownload(String seqNumber, String pipeline,
			String plotType,TParm parmdata) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", pipeline);
		parm.setData("PLOT_TYPE", plotType);
		TParm tab4Download_G_J = InsManager.getInstance().safe(parm, "");
		if (tab4Download_G_J == null || tab4Download_G_J.getErrCode() < 0) {
			return tab4Download_G_J;
		}
		TParm actionParm = getDownLoadParm(seqNumber,parmdata);
		// ------------------------------------------
		actionParm.setData("tab4Download_G_J", tab4Download_G_J.getRow(0)
				.getData());

		TParm adm_Confirm = new TParm();
		adm_Confirm.setData("ADM_SEQ", seqNumber);
		actionParm.setData("adm_Confirm", adm_Confirm.getData());
		return actionParm;
	}

	/**
	 * tab3Data城居数据下载
	 * 
	 * @param seqNumber
	 * @param hospital
	 * @return
	 */
	private TParm tab3DownloadByCJ(String seqNumber, String hospital) {

		TParm parm = new TParm();
		TParm data = new TParm();
		// 支付信息下载接口
		parm.addData("HOSP_NHI_NO", hospital);
		parm.addData("CODE", seqNumber);
		parm.addData("PARM_COUNT", 2);
		parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "G");
		data = InsManager.getInstance().safe(parm, "");
		return data;
	}

	/**
	 * tab3Data城居全部数据下载
	 * 
	 * @param seqNumber
	 * @param hospital
	 * @return
	 */
	private TParm tab3DownloadByCJAll() {
		((TTable) getComponent("tab4Table")).setParmValue(new TParm());
		TParm tab2Data = (TParm) callFunction("UI|tTabbedPane_1|tPanel_2|tab2Table|getParmValue");
		TParm data = new TParm();
		for (int i = 0; i < this.getValueInt("COUNT"); i++) {
			TParm parmdata = new TParm();
			parmdata.setData("MR_NO",tab2Data.getValue("MR_NO", i));
			parmdata.setData("CASE_NO",tab2Data.getValue("CASE_NO", i));
			TParm rowData = tab4DownloadByCJ(tab2Data.getValue("CONFIRM_NO", i),parmdata);
			if (rowData.getErrCode() < 0) {
				this.messageBox("就诊序号:" + tab2Data.getValue("CONFIRM_NO", i)
						+ rowData.getErrText());
				break;
			}
			data.setData(rowData.getData());
			data.addParm(data);
		}
		return data;
	}

	/**
	 * 下载全部
	 */
	public void onTab4AllDownloadButtonDownload() {
		if (this.getValueInt("COUNT") == 0) {
			this.messageBox("请出入下载条数");
			return;
		}
		if (this.getValueInt("COUNT") > ((TTable) getComponent("tab2Table"))
				.getRowCount()) {
			this.messageBox("输入值过大");
			return;
		}
		((TTable) getComponent("tab4Table")).setParmValue(new TParm());
		TParm data = new TParm();
		String sql = null;
		// 城职
		if (downloadType.equals(CHENGZHI)) {
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_rs' AND PLOT_TYPE='I' AND IN_OUT='OUT' ORDER BY ID";
			//增加线程---------------------------------------------begin
//			 System.out.println("线程==========1");
			 /**
		     * 判断线程是否运行
		     */
		    if(driverBatches.isRun())
		    {
//		      System.out.println("线程==========2");
		      driverBatches.stop();
		      return;
		    }
//		     System.out.println("线程==========3");
		    //设置文件路径
		    String home = System.getProperty("user.home");
//		    System.out.println("线程==========3"+home);
		    //设置文件名
		    driverBatches.setFileName(home + "\\insdata");
//		    System.out.println("线程==========4");
		    //从界面上得到报盘批号
		    driverBatches.setPasselNo(getText("tab2OfferBatchNumber"));
//		    System.out.println("线程==========5");
		    TParm patList = new TParm();
		    TParm patNoI = new TParm();
		    patList.setData("TYPE","0");
		    patList.setData("COUNT" ,this.getValueInt("COUNT"));
//		    System.out.println("线程==========5"+patList);
		    for(int i = 0; i < this.getValueInt("COUNT"); i++){ 	
		    	TTable table =(TTable)this.getComponent("tab2Table");
		    	patNoI.addData("CONFIRM_NO",table.getParmValue().getValue("CONFIRM_NO",i));
		    	patNoI.addData("MR_NO",table.getParmValue().getValue("MR_NO",i));
		    	patNoI.addData("CASE_NO",table.getParmValue().getValue("CASE_NO",i));
		    } 
		    patList.setData("CONFIRM_NO",patNoI.getData("CONFIRM_NO"));
		    patList.setData("MR_NO",patNoI.getData("MR_NO"));
		    patList.setData("CASE_NO",patNoI.getData("CASE_NO"));
		    patList.setData("INS_TYPE", "01");
//      	    System.out.println("线程==========6"+patList);
		    driverBatches.setPatList(patList);
		    //将tab2Table的数据传入线程中
		    driverBatches.settab2Table((TTable) getComponent("tab2Table"));
		    //将PAT_NUM的数据传入线程中
		    driverBatches.setpatsum((TTextField) getComponent("PAT_NUM"));
		    //将tButton_3的数据传入线程中
		    driverBatches.setbutton((TButton) getComponent("tButton_3"));
		    //下载信息写文件并保存数据库
		    driverBatches.start();
		    callFunction("UI|tButton_3|setEnabled", false);//置灰
//		    System.out.println("线程==========50");
			//增加线程---------------------------------------------end   
//			data = tab3DownloadByCZAll();
		} else if (downloadType.equals(CHENGJU)) {
			// 城居
			sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE = 'DataDown_czyd' AND PLOT_TYPE='E' AND IN_OUT='OUT' ORDER BY ID";
			// 城居
			data = tab3DownloadByCJAll();
			if(data!=null){
			this.messageBox("下载成功!");
			}
			TParm parm=((TTable) getComponent("tab2Table")).getParmValue();
				
			removeMrTable(parm);		
		}

//		getTableValue(sql, "tab4Table", data);
	}

	/**
	 * tab3Data城职所有数据下载
	 */
	private TParm tab3DownloadByCZAll() {
		TParm data = new TParm();
		TParm tab2Data = ((TTable) getComponent("tab2Table")).getParmValue();		
		for (int i = 0; i < this.getValueInt("COUNT"); i++) {
			TParm parmdata = new TParm();
			parmdata.setData("MR_NO",tab2Data.getValue("MR_NO", i));
			parmdata.setData("CASE_NO",tab2Data.getValue("CASE_NO", i));
			TParm rowData = tab4DownloadByCZ(tab2Data.getValue("CONFIRM_NO", i),parmdata);
			if (rowData.getErrCode() < 0) {
				this.messageBox("就诊序号:" + tab2Data.getValue("CONFIRM_NO", i)
						+ rowData.getErrText());
				break;
			}
			data.setData(rowData.getData());
			data.addParm(data);

		}
		return data;
	}
	/**
	 * 移除病患列表数据
	 */
	private void removeMrTable(TParm parm){
		int row=this.getValueInt("COUNT");
		for (int i = 0; i < row; i++) {
			if (parm.getCount()>=1) {
				parm.removeRow(0);
			}
		}
		if(parm.getCount("CONFIRM_NO")>=row){
		}else{
			parm.setCount(0);
		}
		((TTable) getComponent("tab2Table")).setParmValue(parm);
		this.setValue("PAT_NUM", parm.getCount()+"");
	}
	/**
	 * 查询
	 */
	public void onQuery() {

	}

	/**
	 * 调用接口 InsManager 中 L方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insManagerL(TParm parm) {

		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "T");

		TParm result = null;
		// TParm result = InsManager.getInstance().safe(parm);

		return result;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// clearValue("START_DATE;END_DATE");
		this.setValue("COUNT", 10);
		this.setValue("startDate", SystemTool.getInstance().getDate());
		this.setValue("endDate", SystemTool.getInstance().getDate());
		
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = null;
		TTabbedPane tTabbedPane_1 = (TTabbedPane) getComponent("tTabbedPane_1");
		String name = "";
		switch (tTabbedPane_1.getSelectedIndex()) {
		case 0:
			table = (TTable) getComponent("tab1Table");
			name = "报盘批号";
			break;
		case 1:
			table = (TTable) getComponent("tab2Table");
			name = "病人列表";
			break;
		case 2:
			table = (TTable) getComponent("tab3Table");
			name = "支付信息下载";
			break;
		case 3:
			table = (TTable) getComponent("tab4Table");
			name = "结算明细下载";
			break;
		}
		// 得到UI对应控件对象的方法

		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, name);
	}

	public static void main(String[] args) {
		String sql = " SELECT A.REXP_CODE,SUM(A.WRT_OFF_AMT) "
				+ " FROM BIL_WRT_OFF A, BIL_RECPM B"
				+ " WHERE B.HOSP_AREA = ''" + " AND B.CASE_NO IN ()"
				+ " AND B.REFUND_FLG = 'N' " + " AND B.OFFRECEIPT_NO IS NULL "
				+ " AND A.HOSP_AREA = B.HOSP_AREA "
				+ " AND A.RECEIPT_NO = B.RECEIPT_NO " + " GROUP BY A.REXP_CODE";
		// System.out.println(sql);
	}
}
