package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import jdo.sta.StaGenMroDataTran;
import jdo.sta.StaUtilTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:医疗监管指标上传
 * </p>
 * 
 * <p>
 * Description:医疗监管指标上传
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
 * 
 * 
 */
public class StaGenMroDataControl extends TControl {

	public StaGenMroDataControl() {

	}

	private SimpleDateFormat dateFormate = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	// 表格
	private TTable table;

	// 查询标记（true 为连接中间表 false 为单表查询）
	private String flg;
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;

	// 初始化
	public void onInit() {
		super.onInit();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		initPage();
		// 排序监听
		// addListener(getTTable("TABLE"));
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		// 前一天日期
		Timestamp date = StringTool.rollDate(
				SystemTool.getInstance().getDate(), -1);
		String year = String.valueOf(date).substring(0, 4);
		String month = String.valueOf(date).substring(5, 7);
		String day = String.valueOf(date).substring(8, 10);
		Timestamp startDate = StringTool.getTimestamp(year + month + day
				+ "000000", "yyyyMMddHHmmss");
		Timestamp endDate = StringTool.getTimestamp(year + month + day
				+ "235959", "yyyyMMddHHmmss");
		this.setValue("S_DATE", startDate);
		this.setValue("E_DATE", endDate);
		this.setValue("REGION_CODE", Operator.getRegion());
		table = (TTable) this.getComponent("TABLE");
		this.callFunction("UI|TABLE|removeRowAll");
		selectType();
	}

	/**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 查询动作
	 */
	public void onQuery() {
		getFlg();
		onMQuery();
	}

	/**
	 * 中间查询过滤
	 */
	public void onMQuery() {
		// 出院开始日期
		Timestamp startDate = (Timestamp) getValue("S_DATE");
		// 出院结束日期
		Timestamp endDate = (Timestamp) getValue("E_DATE");
		if (startDate == null) {
			this.messageBox("开始日期不能为空");
			return;
		}
		if (endDate == null) {
			this.messageBox("结束日期不能为空");
			return;
		}
		// 病案号
		String mrNo = this.getValueString("MR_NO");
		// 入院科室
		String inDeptCode = this.getValueString("IN_DEPT_CODE");
		// 入院病区
		String inStationCode = this.getValueString("IN_STATION_CODE");
		// 出院科室
		String outDeptCode = this.getValueString("OUT_DEPT_CODE");
		// 出院科室
		String outStationCode = this.getValueString("OUT_STATION_CODE");
		// 得到院区
		String regionCode = this.getValueString("REGION_CODE");
		TParm parm = new TParm();
		parm.setData("S_DATE", StringTool
				.getString(startDate, "yyyyMMddHHmmss"));
		parm.setData("E_DATE", StringTool.getString(endDate, "yyyyMMddHHmmss"));
		parm.setData("MR_NO", mrNo);
		parm.setData("IN_DEPT_CODE", inDeptCode);
		parm.setData("IN_STATION_CODE", inStationCode);
		parm.setData("OUT_DEPT_CODE", outDeptCode);
		parm.setData("OUT_STATION_CODE", outStationCode);
		parm.setData("REGION_CODE", regionCode);
		String sql = this.getSql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("查询错误");
			this.callFunction("UI|TABLE|removeRowAll");
			this.setValue("SUM_TOT", "0");
			return;
		}
		if (result.getCount() <= 0) {
			this.callFunction("UI|TABLE|removeRowAll");
			this.setValue("SUM_TOT", "0");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("IN_COUNT", i, result.getInt("IN_COUNT", i));
			result.setData("REAL_STAY_DAYS", i, result.getInt("REAL_STAY_DAYS",
					i));
		}
		table.setParmValue(result);
		this.setValue("SUM_TOT", "" + result.getCount());
	}

	/**
	 * 得到条件
	 */
	public void getFlg() {
		// 首页未完成
		if (this.getTTRadioButton("TT").isSelected()) {
			flg = "N";
		}
		// 首页已完成/统计未完成
		if (this.getTTRadioButton("T0").isSelected()) {
			flg = "0";
		}
		// 统计已完成/未生成DBF
		if (this.getTTRadioButton("T1").isSelected()) {
			flg = "1";
		}
		// 生成DBF/未上传
		if (this.getTTRadioButton("T2").isSelected()) {
			flg = "2";
		}
		// 未上传/首页有更新
		if (this.getTTRadioButton("T3").isSelected()) {
			flg = "3";
		}
		// 已上传
		if (this.getTTRadioButton("T4").isSelected()) {
			flg = "4";
		}
	}

	/**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TRadioButton getTTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 生成DBF文件
	 */
	public void onCreateDbf() {
		// 出院开始日期
		Timestamp startDate = (Timestamp) getValue("S_DATE");
		// 出院结束日期
		Timestamp endDate = (Timestamp) getValue("E_DATE");
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		if (count <= 0) {
			this.messageBox("没有生成的数据");
			return;
		}
		TParm inparm = new TParm();
		int dataCount = 0;
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				inparm.addRowData(parm, i);
				dataCount++;
			}
		}
		if (dataCount <= 0) {
			this.messageBox("没有选中生成的数据");
			return;
		}
		inparm.setCount(dataCount);
		String strDate = StringTool.getString(startDate, "yyyyMMddHHmmss");
		String eDate = StringTool.getString(endDate, "yyyyMMddHHmmss");
		String DateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMddHHmmss");
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String GenFilePath = config.getString("LocalStaGenFilePath");
		String SendFilePath = config.getString("LocalStaSendFilePath");
		String SendFileServerPath = config.getString("ServerStaSendFilePath");
		File file = new File(GenFilePath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		File fileT = new File(SendFilePath);
		if (!fileT.isDirectory()) {
			fileT.mkdirs();
		} else {
			StaUtilTool.delAllFile(SendFilePath);// 清空文件夹
		}
		String fileNa = DateStr;
		TParm Dataparm = new TParm();
		Dataparm.setData("START_DATE", strDate);
		Dataparm.setData("END_DATE", eDate);
		Dataparm.setData("FILE_DATA", fileNa);
		Dataparm.setData("FILE_PATH", GenFilePath);
		Dataparm.setData("INPARM", inparm.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.sta.StaGenMroDataAction", "onCreateDBF", Dataparm);
		if (result.getErrCode() < 0) {
			this.messageBox("生成文件失败");
			return;
		}
		byte[] data = (byte[]) result.getData("BYTES");
		try {
			FileOutputStream stream = new FileOutputStream(GenFilePath + fileNa
					+ ".dbf");
			stream.write(data);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 压缩成 Zip
		File Sfile = new File(SendFilePath + fileNa + ".zip");
		if (Sfile.exists())
			Sfile.delete();
		if (StaUtilTool.fileToZip(GenFilePath, fileNa + ".dbf", SendFilePath,
				fileNa)) {
			byte[] zipdata = null;
			try {
				zipdata = FileTool.getByte(Sfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					SendFileServerPath + fileNa + ".zip", zipdata);
			Sfile.delete();
			this.messageBox("生成文件成功");
			SetTRadioButton(false, false, false, true, false, false);
			selectType();
			return;
		} else {
			this.messageBox("生成文件失败");
			SetTRadioButton(false, false, false, true, false, false);
			selectType();
			return;
		}
	}

	/**
	 * 发送文件
	 */
	public synchronized void onSendFile() {
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String ServerSendFilePath = config.getString("ServerStaSendFilePath");
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		if (count <= 0) {
			this.messageBox("没有生成的数据");
			return;
		}
		String DateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMddHHmmss");
		InputStream is = null;
		Map dataMap = new HashMap();
		int dataCount = 0;
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				TParm inparm = parm.getRow(i);
				String fileName = inparm.getValue("STA_DATE");
				if (fileName.equals(""))
					continue;
				else
					fileName = fileName.replace(":", "").replace("/", "")
							.replace(" ", "");
				if (dataMap.get(fileName) == null) {
					dataMap.put(fileName, fileName);
					// 获取token串
					TParm tokenParm = StaGenMroDataTran.getInstance()
							.getToken();
					if (!tokenParm.getValue("TOKEN").equals("")) {
						String token = tokenParm.getValue("TOKEN");
						byte[] data = TIOM_FileServer.readFile(TIOM_FileServer
								.getSocket(), ServerSendFilePath + fileName
								+ ".zip");
						if (data == null) {
							TIOM_FileServer.deleteFile(TIOM_FileServer
									.getSocket(), ServerSendFilePath
									+ fileName + ".zip");
							continue;
						}
						is = new ByteArrayInputStream(data);
						// 发送zip文件
						if (!token.equalsIgnoreCase("") || is != null) {
							boolean flg = StaGenMroDataTran.getInstance()
									.sendData(token, is);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
							if (flg) {
								TIOM_FileServer.deleteFile(TIOM_FileServer
										.getSocket(), ServerSendFilePath
										+ fileName + ".zip");
								TParm Dataparm = new TParm();
								// 查看返回信息
								String response = StaGenMroDataTran
										.getInstance().findResult(token);
								Dataparm.setData("ZIP_TIME", fileName);
								Dataparm.setData("SEND_TIME", DateStr);
								Dataparm.setData("RESPONSE", response);
								TParm result = TIOM_AppServer.executeAction(
										"action.sta.StaGenMroDataAction",
										"onSendDBF", Dataparm);
								if (result.getErrCode() < 0) {
									this.messageBox("发送失败");
									SetTRadioButton(false, false, false, false,
											false, true);
									selectType();
									return;
								}
							} else {
								SetTRadioButton(false, false, false, false,
										false, true);
								selectType();
								this.messageBox("发送失败");
								return;
							}
						}
					}else{
						String errorCode=tokenParm.getValue("ERRORCODE");
						String errMsg=tokenParm.getValue("ERRMSG");
						this.messageBox("医疗监管上传发送失败,<"+errorCode+">:<"+errMsg+">");
						return;
					}
				}
			}
		}
		this.messageBox("发送成功");
		SetTRadioButton(false, false, false, false, false, true);
		selectType();
	}

	/**
	 * 保存插入数据库
	 */
	public void onSave() {
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		if (count <= 0) {
			this.messageBox("没有保存的数据");
			return;
		}
		// System.out.println(parm);
		TParm inparm = new TParm();
		int dataCount = 0;
		for (int i = 0; i < count; i++) {
			if (table.getItemString(i, "FLG").equals("Y")) {
				inparm.addRowData(parm, i);
				dataCount++;
			}
		}
		// this.messageBox_(dataCount);
		if (dataCount <= 0) {
			this.messageBox("没有选中保存的数据");
			return;
		}
		inparm.setCount(dataCount);
		TParm result = TIOM_AppServer.executeAction(
				"action.sta.StaGenMroDataAction", "onSave", inparm);
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String dir = (new StringBuilder()).append(
				config.getString("StaGenFileLocalPath")).append("\\")
				.toString();
		if (result.getErrCode() < 0) {
			if (result.getErrCode() == -100) {
				int errcount = result.getInt("ERRORCOUNT");
				String errLog = result.getValue("ERRORLOG");
				this.messageBox_("插入失败，还有 " + errcount + " 人失败,详见前台" + dir
						+ "Log.txt");
				writeLocalFile(errLog, dir);
				SetTRadioButton(false, false, true, false, false, false);
				selectType();
			} else {
				this.messageBox("保存失败");
				return;
			}
		} else {
			this.messageBox("保存成功");
			SetTRadioButton(false, false, true, false, false, false);
			selectType();
		}

	}

	/*
	 * 设置TRadioButton
	 */
	private void SetTRadioButton(boolean flgT, boolean flg0, boolean flg1,
			boolean flg2, boolean flg3, boolean flg4) {
		this.getTTRadioButton("TT").setSelected(flgT);
		this.getTTRadioButton("T0").setSelected(flg0);
		this.getTTRadioButton("T1").setSelected(flg1);
		this.getTTRadioButton("T2").setSelected(flg2);
		this.getTTRadioButton("T3").setSelected(flg3);
		this.getTTRadioButton("T4").setSelected(flg4);
	}

	/**
	 * 摆药失败写到本地LOG
	 * 
	 * @param failedParm
	 *            TParm
	 * @param dir
	 *            String
	 * @return boolean
	 */
	private boolean writeLocalFile(String errlog, String dir) {
		if (errlog.length() < 0)
			return false;
		String fileName = "医疗监管Log"
				+ StringTool.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmm") + ".txt";
		try {
			FileTool.setString(dir + fileName, errlog.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 得到SQL
	 * 
	 * @param parm
	 * @return
	 */
	private String getSql(TParm parm) {
		String startDate = parm.getValue("S_DATE");
		// 出院结束日期
		String endDate = parm.getValue("E_DATE");
		// 病案号
		String mrNo = "";
		// 入院科室
		String inDeptCode = "";
		// 入院病区
		String inStationCode = "";
		// 出院科室
		String outDeptCode = "";
		// 出院科室
		String outStationCode = "";
		// 得到院区
		String regionCode = "";
		if (!parm.getValue("MR_NO").equals("")) {
			mrNo = " AND  A.MR_NO='" + parm.getValue("MR_NO") + "'";
		}
		if (!parm.getValue("IN_DEPT_CODE").equals("")) {
			inDeptCode = " AND  A.IN_DEPT='" + parm.getValue("IN_DEPT_CODE")
					+ "'";
		}
		if (!parm.getValue("IN_STATION_CODE").equals("")) {
			inStationCode = " AND  A.IN_STATION='"
					+ parm.getValue("IN_STATION_CODE") + "'";
		}
		if (!parm.getValue("OUT_DEPT_CODE").equals("")) {
			outDeptCode = " AND  A.OUT_DEPT='" + parm.getValue("OUT_DEPT_CODE")
					+ "'";
		}
		if (!parm.getValue("OUT_STATION_CODE").equals("")) {
			outStationCode = " AND  A.OUT_STATION='"
					+ parm.getValue("OUT_STATION_CODE") + "'";
		}
		if (!parm.getValue("REGION_CODE").equals("")) {
			regionCode = " AND  A.REGION_CODE='" + parm.getValue("REGION_CODE")
					+ "'";
		}
		String flgStr1 = "";
		String flgStr2 = "";
		String flgStr3 = "";
		if (flg.equals("N")) {// 首页未完成
			flgStr3 = " AND (A.ADMCHK_FLG='N' OR A.DIAGCHK_FLG='N' OR A.BILCHK_FLG='N' "
					+ " OR A.QTYCHK_FLG='N') AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}
		if (flg.equals("0")) {// 首页已完成统计未完成
			flgStr3 = " AND A.STA_FLG='0' "
					+ " AND A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG='Y' "
					+ " AND A.QTYCHK_FLG='Y' AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}
		if (flg.equals("1")) {// 统计已完成/未生成DBF
			flgStr1 = ",STA_MRO_DAILY D";
			flgStr2 = " A.MR_NO=D.P3 AND A.IN_COUNT=D.P2 AND ";
			flgStr3 = " AND A.STA_FLG='1'  AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}
		if (flg.equals("2")) {// 生成DBF/未上传
			flgStr1 = ",STA_MRO_DAILY D";
			flgStr2 = " A.MR_NO=D.P3 AND A.IN_COUNT=D.P2 AND ";
			flgStr3 = " AND A.STA_FLG='2'  AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}
		if (flg.equals("3")) {// 未上传/首页有更新
			flgStr1 = ",STA_MRO_DAILY D";
			flgStr2 = " A.MR_NO=D.P3 AND A.IN_COUNT=D.P2 AND ";
			flgStr3 = " AND A.STA_FLG='3'  AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}
		if (flg.equals("4")) {// 已上传
			flgStr1 = ",STA_MRO_DAILY D";
			flgStr2 = " A.MR_NO=D.P3 AND A.IN_COUNT=D.P2 AND ";
			flgStr3 = " AND A.STA_FLG='4'  AND A.IN_DEPT=C.DEPT_CODE AND C.DEPT_GRADE='3' AND C.CLASSIFY='0' AND C.STATISTICS_FLG='Y' ";
		}

		String sql = "SELECT 'Y' AS FLG,A.MR_NO,A.IPD_NO,A.PAT_NAME,TO_CHAR(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS') AS IN_DATE,TO_CHAR(A.OUT_DATE,'YYYY/MM/DD HH24:MI:SS') AS OUT_DATE,A.REAL_STAY_DAYS,"
				+ " A.IN_DEPT,A.IN_STATION,A.OUT_DEPT,A.OUT_STATION,A.CASE_NO,A.IN_COUNT,TO_CHAR(A.STA_DATE,'YYYY/MM/DD HH24:MI:SS') AS STA_DATE,TO_CHAR(A.STASEND_DATE,'YYYY/MM/DD HH24:MI:SS') AS STASEND_DATE"
				+ " FROM MRO_RECORD A ,SYS_DEPT C"
				+ flgStr1
				+ " WHERE "
				+ flgStr2
				+ " A.OUT_DATE BETWEEN TO_DATE('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ endDate
				+ "','YYYYMMDDHH24MISS')"
				+ mrNo
				+ inDeptCode
				+ inStationCode
				+ outDeptCode
				+ outStationCode
				+ regionCode
				+ flgStr3
				+ " ORDER BY A.OUT_DATE ASC";// 全部完成
		return sql;
	}

	/**
	 * 清空功能
	 */
	public void onClear() {
		this
				.clearValue("REGION_CODE;MR_NO;IN_DEPT_CODE;IN_STATION_CODE;OUT_DEPT_CODE;OUT_STATION_CODE;SUM_TOT;");
		initPage();
	}

	/**
	 * 病案号回车事件
	 */
	public void onQueryPatInfo() {
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		this.setValue("MR_NO", mrNo);
		onQuery();
	}

	/**
	 * 全选功能
	 */
	public void onAllCheck() {
		TParm parm = table.getParmValue();
		boolean flg = ((TCheckBox) getComponent("ALL_CHECK")).isSelected();
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", flg ? "Y" : "N");
		}

	}

	/**
	 * 选择类型动作
	 */
	public void selectType() {
		if (this.getTTRadioButton("TT").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", false);
			callFunction("UI|tButton_1|setEnabled", false);
			callFunction("UI|tButton_8|setEnabled", false);
		}
		if (this.getTTRadioButton("T0").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", true);
			callFunction("UI|tButton_1|setEnabled", false);
			callFunction("UI|tButton_8|setEnabled", false);
		}
		if (this.getTTRadioButton("T1").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", false);
			callFunction("UI|tButton_1|setEnabled", true);
			callFunction("UI|tButton_8|setEnabled", false);
		}
		if (this.getTTRadioButton("T2").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", false);
			callFunction("UI|tButton_1|setEnabled", false);
			callFunction("UI|tButton_8|setEnabled", true);
		}
		if (this.getTTRadioButton("T3").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", false);
			callFunction("UI|tButton_1|setEnabled", false);
			callFunction("UI|tButton_8|setEnabled", true);
		}
		if (this.getTTRadioButton("T4").isSelected()) {
			callFunction("UI|tButton_0|setEnabled", false);
			callFunction("UI|tButton_1|setEnabled", false);
			callFunction("UI|tButton_8|setEnabled", false);
		}
		onQuery();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "医疗监管指标上传明细");
		}
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				// 表格中parm值一致,
				// 1.取paramw值;
				getTTable("TABLE").acceptText();
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				//
				// System.out.println("tblColumnName===="+tblColumnName);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 查看日志
	 */
	public void onLog() {
		this.openDialog("%ROOT%\\config\\sta\\STA_MRO_LOGUI.x");
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
}
