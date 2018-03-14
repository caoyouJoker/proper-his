package com.javahis.ui.ins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.ins.InsManager;

import jdo.adm.ADMInpTool;
import jdo.ins.INSBlackListTool;
import jdo.ins.INSNoticeTool;
import jdo.reg.RegMethodTool;
import jdo.sys.Operator;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 诚信审核信息下载
 * </p>
 * 
 * <p>
 * Description:诚信审核信息下载
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
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSBlackListControl extends TControl {
	TParm data;
	TParm LocalTabledata;
	int selectRow = -1;
	/**
	 * 最大高度
	 */
	private static int maxHeight = 520;
	/**
	 * localTableDefaut默认Y
	 */
	private static int localTableDefautY = 306;
	/**
	 * localTableDefaut默认高度
	 */
	private static int localTableDefautHeight = 337;
	/**
	 * downdoadTableDefautHeight默认高度
	 */
	private static int downdoadTableDefautHeight = 254;
	/**
	 * localCheckBoxY默认Y
	 */
	private static int localCheckBoxDefautY = 281;
	/**
	 * 下载权限
	 */
	public static String INS_NOTICE_CONTROL_DOWNLOAD = "INS_NOTICE_CONTROL_DOWNLOAD";
	/**
	 * 查询权限
	 */
	public static String INS_NOTICE_CONTROL_SREACH = "INS_NOTICE_CONTROL_SREACH";
	/**
	 * 当前用户是否拥有下载权限
	 */
	boolean isPossessDownload = false;
	/**
	 * 当前保存是否覆盖
	 */
	boolean isAllowCover = false;
	/**
	 * 数据是否重复
	 */
	boolean isRepeat = false;
	/**
	 * DataDown_rs 中S方法行分隔符
	 */
	private String DATADOWN_RS_S_ROW_SPLIT = "";

	/**
	 * DataDown_rs 中S方法列分隔符
	 */
	private String DATADOWN_RS_S_COLUMN_SPLIT = "\\|";

	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	public void onInit() {
		super.onInit();

		setPossessDownload();
		onClear();

	}

	/**
	 * 设置当前登录用户权限
	 */
	private void setPossessDownload() {
		isPossessDownload = this.getPopedem("LEADER");
		// 默认权限 查询
		// isPossessDownload = false;
		// 下载权限
		isPossessDownload = true;

	}

	/**
	 * 增加对下载列表的监听change
	 * 
	 * @param row
	 *            int
	 */
	public void onTableRowChange() {
		// 选中行
		TTable table = ((TTable) getComponent("Table"));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		selectTableRow((String) tableParm.getData("DR_NAME", row),
				(String) tableParm.getData("BLIST_NO", row), "LocalTable");
	}

	/**
	 * 增加对本地数据库列表的监听change
	 * 
	 * @param row
	 *            int
	 */
	public void onLocalTableRowChange() {
		// 选中行
		TTable table = ((TTable) getComponent("LocalTable"));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		selectTableRow((String) tableParm.getData("DR_NAME", row),
				(String) tableParm.getData("BLIST_NO", row), "Table");
	}

	private void selectTableRow(String drName, String blistNo, String tableName) {
		TTable table = ((TTable) getComponent(tableName));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		int rowCount = table.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			String DR_NAME = (String) tableParm.getData("DR_NAME", rowIndex);
			String BLIST_NO = (String) tableParm.getData("BLIST_NO", rowIndex);
			if (DR_NAME.equals(drName) && BLIST_NO.equals(blistNo)) {
				table.setSelectedRow(rowIndex);
				return;
			}
		}
		// table.setSelectedRow(-1);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		TTable table = ((TTable) getComponent("Table"));
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm result = new TParm();
		int count = table.getRowCount();
		isRepeat = false;
		isAllowCover = false;
		TParm saveTParm = new TParm();
		int saveCount = 0;

		for (int i = 0; i < count; i++) {
			String drName = (String) tableParm.getData("DR_NAME", i);
			String blistNo = (String) tableParm.getData("BLIST_NO", i);
			boolean saveFlg = tableParm.getBoolean("SAVE_FLG", i);
			// 数据重复并且不与覆盖
			isAllowCover(saveFlg, drName, blistNo);
			if (isRepeat && !isAllowCover) {
				return;
			}
			if (saveFlg ) {

				saveTParm.addData("SEQ_NO", tableParm.getData("BLIST_NO", i));
				saveTParm.addData("DR_NAME", tableParm.getData("DOCTOR_NAME", i));
				saveTParm.addData("BLIST_NO", tableParm.getData("BLIST_NO", i));
				saveTParm.addData("VO_BDATE", tableParm.getData("VO_BDATE", i));
				saveTParm.addData("VO_SDATE", tableParm.getData("VO_SDATE", i));
				saveTParm.addData("ST_BDATE", tableParm.getData("ST_BDATE", i));
				saveTParm.addData("ST_SDATE", tableParm.getData("ST_SDATE", i));
				saveTParm.addData("ST_DESC", tableParm.getData("ST_DESC", i));
				saveTParm.addData("VO_DESC", tableParm.getData("VO_DESC", i));
				saveTParm.addData("BLIST_TYPE", tableParm.getData("BLIST_TYPE",
						i));
				saveTParm.addData("OPT_USER", Operator.getID());
				saveTParm.addData("OPT_TERM", Operator.getIP());
				saveCount++;

			}
		}
		saveTParm.setCount(saveCount);
		TParm actionParm = new TParm();
		actionParm.setData("saveTParm", saveTParm.getData());
		actionParm.setData("START_DATE", getText("START_DATE"));
		actionParm.setData("END_DATE", getText("END_DATE"));
		actionParm.setData("DR_NAME", getText("DR_NAME"));
		actionParm.setData("ISALLOWCOVER", isAllowCover);
		result = TIOM_AppServer.executeAction("action.ins.INSBlackListAction",
				"onSave", actionParm);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		this.messageBox("P0005");

		// onUpdate(tableParm);
	}

	/**
	 * 判断需要保存的数据是否与本地数据重复
	 */
	private boolean isAllowCover(boolean saveFlg, String drName, String blistNo) {
		if (!saveFlg) {
			return false;
		}
		TTable localTable = ((TTable) getComponent("LocalTable"));
		TParm tableParm = localTable.getParmValue();
		int rowCount = localTable.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			String DR_NAME = (String) tableParm.getData("DR_NAME", rowIndex);
			String BLIST_NO = (String) tableParm.getData("BLIST_NO", rowIndex);
			if (DR_NAME.equals(drName) && BLIST_NO.equals(blistNo)) {
				isRepeat = true;
				int infoValue = this.messageBox("系统提示", "当前保存数据与本地数据重复，是否覆盖？",
						2);
				if (infoValue == 0) {
					isAllowCover = true;
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TTable LocalTable = ((TTable) getComponent("LocalTable"));

		// tTable.setColumnHorizontalAlignmentData("0,left;1,left");
		// tTable.setColumnHorizontalAlignment(1, 0);
		clearValue("NOTICE_DESC");
		
		queryTParm.setData("START_DATE", getText("START_DATE"));
		queryTParm.setData("END_DATE", getText("END_DATE"));
		queryTParm.setData("DR_NAME", "%"+getText("DR_NAME")+"%");

		LocalTabledata = INSBlackListTool.getInstance().selectdata(queryTParm);
		// 判断错误值
		if (LocalTabledata.getErrCode() < 0) {
			messageBox(LocalTabledata.getErrText());
			return;
		}
		((TTable) getComponent("LocalTable")).setParmValue(LocalTabledata);
	}

	/**
	 * 全选
	 */
	public void saveCheckBox() {

		boolean selected = ((TCheckBox) getComponent("saveCheckBox"))
				.isSelected();
		String saveFlgVlaue = "N";
		if (selected) {
			saveFlgVlaue = "Y";
		}
		TTable table = ((TTable) getComponent("Table"));
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setItem(i, "SAVE_FLG", saveFlgVlaue);

		}
	}

	/**
	 * 调用接口 InsManager 中S方法
	 * 
	 * @param parm
	 * @return
	 */
	private TParm insManagerS(TParm parm) {
		parm.addData("START_DATE", getText("START_DATE"));
		parm.addData("END_DATE", getText("END_DATE"));
		//parm.addData("DR_NAME", getText("DR_NAME"));
		parm.addData("HOSP_NHI_NO", Operator.getRegion());
		parm.addData("PARM_COUNT", 3);

		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "S");
		TParm dataDown_rs_S =  InsManager.getInstance().safe(parm,"");
		//this.messageBox(dataDown_rs_S.getData()+"");
		//String dataDown_rs_S = "程序执行状态|程序执行信息|医院编码|王彦|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|禁止内容|警示内容|黑名单类型|黑名单顺序号程序执行状态|程序执行信息|医院编码|汤友林|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|禁止内容|警示内容|个人违规|1#";
		//dataDown_rs_S===={Data={TIMESTAMP=1, PROGRAM_STATE=1, HOSP_NHI_NO=109720.6, DOCTOR_NAME=0, PROGRAM_MESSAGE=成功!, BLIST_TPYE=1, ST_DESC=1, VO_DESC=1, BLIST_NO=1}}
		
		
//SAVE_FLG;DOCTOR_NAME;VO_BDATE;VO_SDATE;ST_BDATE;ST_SDATE;ST_DESC;VO_DESC;BLIST_TYPE
//		List columnNameList = new ArrayList();
//		columnNameList.add("PROGRAM_STATE");
//		columnNameList.add("PROGRAM_MESSAGE");
//		columnNameList.add("HOSP_NHI_NO");
//		columnNameList.add("DR_NAME");
//		columnNameList.add("VO_BDATE");
//		columnNameList.add("VO_SDATE");
//		columnNameList.add("ST_BDATE");
//		columnNameList.add("ST_SDATE");
//		columnNameList.add("ST_DESC");
//		columnNameList.add("VO_DESC");  
//		columnNameList.add("BLIST_TYPE");
//		columnNameList.add("BLIST_NO");
		//TParm result = dataDownToParm(dataDown_rs_S, columnNameList);
		// TParm result =

		return dataDown_rs_S;
	}

	/**
	 * 将DataDown_rs返回字串转化为TParm
	 * 
	 * @param parm
	 * @return
	 */
	private TParm dataDownToParm(String dataDown_rs_S,
			List<String> columnNameList) {
		TParm parm = new TParm();
		if (dataDown_rs_S == null || dataDown_rs_S.trim().equals("")) {
			return parm;
		}
		int columnNameListSize = columnNameList.size();
		String[] rows = dataDown_rs_S.split(DATADOWN_RS_S_ROW_SPLIT);
		for (int i = 0; i < rows.length; i++) {
			String[] columns = rows[i].split(DATADOWN_RS_S_COLUMN_SPLIT);
			if (columns.length == columnNameListSize) {

				parm.addData("SAVE_FLG", "Y");
				for (int j = 0; j < columnNameListSize; j++) {
					String value = columns[j];
					if (columnNameList.get(j).endsWith("DATE")
							&& value.length() >= 10) {
						value = value.substring(0, 10);
					}
					parm.addData(columnNameList.get(j), value);
				}

			}
		}

		return parm;
	}

	private TParm testInsManagerS(TParm parmSreach) {
		TParm parm = new TParm();
		for (int i = 1; i < 15; i++) {
			parm.addData("SAVE_FLG", "Y");
			parm.addData("DR_NAME", getText("DR_NAME"));
			parm.addData("VO_BDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("VO_SDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("ST_BDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("ST_SDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));

			parm.addData("BLIST_NO", "" + i);
			parm.addData("ST_DESC", "禁止内容" + i);
			parm.addData("VO_DESC", "警示内容" + i);
			parm.addData("BLIST_TYPE", "黑名单类型" + i);
		}

		return parm;
	}

	/**
	 * 显示所有列表
	 */
	private void allChecked() {
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(true);
		localTable.setY(localTableDefautY);
		localTable.setHeight(localTableDefautHeight - 92);
		localCheckBox.setY(localCheckBoxDefautY);

		downdoadTable.setHeight(downdoadTableDefautHeight);
		downdoadTable.setVisible(true);
		downloadCheckBox.setX(72);

		saveCheckBox.setVisible(true);

	}

	/**
	 * 隐藏本地列表
	 */
	private void hiddenLocalTable() {
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(false);
		localCheckBox.setY(localCheckBoxDefautY);

		downdoadTable.setHeight(maxHeight - 25);
		downdoadTable.setVisible(true);
		localCheckBox.setY(downdoadTable.getX() + downdoadTable.getHeight()
				+ 25);
		localCheckBox.setVisible(true);

	}

	/**
	 * 隐藏下载列表
	 */
	private void hiddenDownloadTable() {
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(true);
		localTable.setY(downdoadTable.getY() + 25);
		localTable.setHeight(maxHeight - 20);
		localCheckBox.setY(downdoadTable.getY() + 2);

		saveCheckBox.setVisible(false);
		downloadCheckBox.setX(saveCheckBox.getX());
		downdoadTable.setVisible(false);

	}

	/**
	 * 全部隐藏
	 */
	private void hiddenAll() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));

		localTable.setVisible(false);
		localCheckBox.setY(downloadCheckBox.getY() + 20);
		downdoadTable.setVisible(false);
		downloadCheckBox.setX(saveCheckBox.getX());

		saveCheckBox.setVisible(false);

	}

	/**
	 * 隐藏显示下载列表
	 */
	public void onShowTable() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));

		if (downloadCheckBox.isSelected() && localCheckBox.isSelected()) {
			allChecked();
		} else if (downloadCheckBox.isSelected()) {
			hiddenLocalTable();
		} else if (localCheckBox.isSelected()) {
			hiddenDownloadTable();
		} else {
			hiddenAll();
		}

	}

	/**
	 * 隐藏显示本地数据库信息列表
	 */
	public void onLocalCheckBoxClicked() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		if (localCheckBox.isSelected()) {
			// 显示 查询列表下移

			downdoadTable.setHeight(254);
			localTable.setVisible(true);
			if (downloadCheckBox.isSelected()) {
				localTable.setY(306);
				localTable.setHeight(337);
				localCheckBox.setY(281);
			} else {
				localTable.setY(downdoadTable.getY() + 20);
				localCheckBox.setY(downloadCheckBox.getY() + 2);
				localTable.setHeight(597);
			}

		} else {
			// 隐藏 查询列表上移
			localTable.setVisible(false);
			if (downloadCheckBox.isSelected()) {
				downdoadTable.setHeight(597);
				localCheckBox.setY(597 + 2);
			} else {
				localCheckBox.setY(downloadCheckBox.getY() + 2);
			}

			// localTable.setHeight(localTable.getHeight()+downdoadTable.getHeight());

		}

	}

	/**
	 * 下载
	 */
	public void onDownload() {
		TParm parm = new TParm();
		if(StringUtil.isNullString(getText("START_DATE"))){
			this.messageBox("请输入开始时间。");
			return ;
		}
		if(StringUtil.isNullString(getText("END_DATE"))){
			this.messageBox("请输入结束时间。");
			return ;
		}
		data = insManagerS(parm);
		
		// 判断错误值
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		// 封装查询结果集
		packagDate();
		((TTable) getComponent("Table")).setParmValue(data);
		((TCheckBox) getComponent("saveCheckBox")).setEnabled(true);
		((TCheckBox) getComponent("saveCheckBox")).setSelected(true);
		// 不可编辑
		((TTextFormat) getComponent("START_DATE")).setEnabled(false);
		((TTextFormat) getComponent("END_DATE")).setEnabled(false);
		((TextFormatSYSOperator) getComponent("DR_NAME")).setEnabled(false);
		onQuery();
	}
	/**
	 * 封装查询结果
	 * 
	 * @param parm
	 * @param columnName
	 * @return
	 */
	private void packagDate() {
		int parmCount = data.getCount("ST_SDATE");
		//String[] str=new String[parmCount];
		for (int i = 0; i < parmCount; i++) {
			//str[i]="Y";
			data.addData("SAVE_FLG", "Y");
			
		}
	//	data.addData("SAVE_FLG", str);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// clearValue("START_DATE;END_DATE");
		TTable tTable = ((TTable) getComponent("Table"));
		((TTable) getComponent("Table")).removeRowAll();
		this.setValue("START_DATE", DateUtil.getFirstDayOfMonth());
		this.setValue("END_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
		((TTextFormat) getComponent("START_DATE")).setEnabled(true);
		((TTextFormat) getComponent("END_DATE")).setEnabled(true);
		((TextFormatSYSOperator) getComponent("DR_NAME")).setEnabled(true);
		selectRow = -1;
		if (!isPossessDownload) {
			TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
			TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
			TTable localTable = ((TTable) getComponent("LocalTable"));

			// 菜单控制

			((TMenuItem) getComponent("save")).setVisible(false);
			((TMenuItem) getComponent("download")).setVisible(false);
			// 选择控件控制
			((TCheckBox) getComponent("saveCheckBox")).setVisible(false);
			downloadCheckBox.setVisible(false);
			downloadCheckBox.setSelected(false);
			localCheckBox.setVisible(false);
			localCheckBox.setSelected(true);
			onShowTable();
			localTable.setY(5);
		} else {
			tTable.setColumnHorizontalAlignmentData("1,left;6,left;7,left");
			// 菜单控制
			((TMenuItem) getComponent("query")).setVisible(false);
			((TCheckBox) getComponent("saveCheckBox")).setSelected(false);
			// 选择控件控制
			((TCheckBox) getComponent("saveCheckBox")).setEnabled(false);
		}

	}

	/**
	 * 
	 * 获取随机日期
	 * 
	 * @param beginDate
	 *            起始日期，格式为：yyyy-MM-dd
	 * 
	 * @param endDate
	 *            结束日期，格式为：yyyy-MM-dd
	 * 
	 * @return
	 */

	private static Date randomDate(String beginDate, String endDate) {

		try {

			Date start = format.parse(beginDate);// 构造开始日期

			Date end = format.parse(endDate);// 构造结束日期

			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。

			if (start.getTime() >= end.getTime()) {

				return null;

			}

			long date = random(start.getTime(), end.getTime());

			return new Date(date);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return null;

	}

	private static long random(long begin, long end) {

		long rtn = begin + (long) (Math.random() * (end - begin));

		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值

		if (rtn == begin || rtn == end) {

			return random(begin, end);

		}

		return rtn;

	}

	public static void main(String[] args) {
		INSBlackListControl ins = new INSBlackListControl();
		System.out.println(ins.insManagerS(new TParm()));
	}
}
