package com.javahis.ui.nss;

import java.util.ArrayList;
import java.util.List;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.DeptTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 肠内营养配制</p>
 *
 * <p>Description: 肠内营养配制</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.20
 * @version 1.0
 */
public class NSSEnteralNutritionPreparationControl extends TControl {
    public NSSEnteralNutritionPreparationControl() {
        super();
    }

    private TTable tableM;
    private TTable tableD;

    /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
		this.onInitPage();
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		
		// 配制主表数据单击事件
//		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
//				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
		// 配制主表数据双击事件
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableMDoubleClicked");
		
		// 配制主表勾选框勾选事件
		tableM.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableMCheckBoxClicked");
		
		// 控件初始化
		this.onInitControl();
	}
	
	/**
	 * 控件初始化
	 */
	public void onInitControl() {
		// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 设定默认展开日期
    	this.setValue("QUERY_DATE_S", todayDate);
    	this.setValue("QUERY_DATE_E", todayDate);
    	
    	clearValue("ENP_DEPT_CODE;ENP_STATION_CODE;MR_NO;EN_PREPARE_NO;SELECT_ALL");
    	
    	getRadioButton("STATUS_N").setSelected(true);
    	
    	tableM.setParmValue(new TParm());
    	tableD.setParmValue(new TParm());
	}

    /**
     * 查询方法
     */
    public void onQuery() {
    	clearValue("SELECT_ALL");
		tableM.setParmValue(new TParm());
		tableD.setParmValue(new TParm());
		
    	// 获取查询条件数据
    	TParm queryParm = this.getQueryParm();
    	
    	if (queryParm.getErrCode() < 0) {
    		this.messageBox(queryParm.getErrText());
    		return;
    	}
    	
    	TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("查询营养师展开配方错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("查无数据");
			tableM.setParmValue(new TParm());
			tableD.setParmValue(new TParm());
			return;
		}
		
        tableM.setParmValue(result);
    }
    
	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
			tableM.setParmValue(new TParm());
			parm.setErr(-1, "请输入查询时间");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		
		// 科室
		if (StringUtils.isNotEmpty(this.getValueString("ENP_DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("ENP_DEPT_CODE"));
		}
		// 病区
		if (StringUtils.isNotEmpty(this.getValueString("ENP_STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("ENP_STATION_CODE"));
		}
		// 配制单号
		if (StringUtils.isNotEmpty(this.getValueString("EN_PREPARE_NO").trim())) {
			parm.setData("EN_PREPARE_NO", this.getValueString("EN_PREPARE_NO"));
		}
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// 配制状态
		if (getRadioButton("STATUS_N").isSelected()) {
			parm.setData("PREPARE_STATUS", "0");
		} else {
			parm.setData("PREPARE_STATUS", "1");
		}
		// 未取消
		parm.setData("CANCEL_FLG", "N");
		
		return parm;
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
			//modify by huangtt 20160930 EMPI患者查重提示  start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
		            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
		    }
			//modify by huangtt 20160930 EMPI患者查重提示  end
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	/**
	 * 根据配制单号查询
	 */
	public void onQueryByPrepareNo() {
		this.onQuery();
		TParm parm = tableM.getParmValue();
		if (parm.getCount() > 0) {
			tableM.setSelectedRow(0);
			this.onTableMDoubleClicked(0);
		} else {
			this.setValue("EN_PREPARE_NO", "");
		}
	}
	
	/**
	 * 添加对tableM的选中监听事件
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableD.setParmValue(new TParm());
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderD(
				data.getRow(selectedRow));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询配方明细错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		tableD.setParmValue(result);
	}
	
	/**
	 * 添加对tableM双击的监听事件
	 * 
	 * @param row
	 */
	public void onTableMDoubleClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		TParm parm = data.getRow(selectedRow);
		
		TParm parameterParm = new TParm();
		// 配制状态
		if (getRadioButton("STATUS_N").isSelected()) {
			parameterParm.setData("COMPLETE_STATUS", "N");
		} else {
			parameterParm.setData("COMPLETE_STATUS", "Y");
		}
		parameterParm.setData("EN_PREPARE_NO", parm.getValue("EN_PREPARE_NO"));
		parameterParm.setData("EN_ORDER_NO", parm.getValue("EN_ORDER_NO"));
		parameterParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		parameterParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
		parameterParm.setData("BED_NO_DESC", parm.getValue("BED_NO_DESC"));
		
		TParm queryParm = new TParm();
		queryParm.setData("EN_PREPARE_NO", parm.getValue("EN_PREPARE_NO"));
		queryParm.setData("EN_ORDER_NO", parm.getValue("EN_ORDER_NO"));
		queryParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		queryParm.setData("CANCEL_FLG", "Y");

		// 查询营养师医嘱展开主项数据
		queryParm = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("查询展开配方错误");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText());
			return;
		}
		
		if (queryParm.getCount() > 0) {
			this.messageBox("该展开配方已被取消");
			this.onQuery();
			return;
		}
		
		parameterParm.addListener("addListener", this, "onRefresh");
		// 配制明细操作界面
		this.openDialog("%ROOT%\\config\\nss\\NSSEnteralNutritionFormula.x", parameterParm);
	}
	
	/**
	 * 配制完成后刷新界面数据
	 * 
	 * @param obj
	 */
	public void onRefresh(Object obj) {
		if (obj != null) {
			if (obj instanceof TParm) {
				this.onQuery();
			}
		}
	}
	
	/**
	 * 添加对tableM中勾选框勾选的监听事件
	 * 
	 * @param obj
	 */
	public void onTableMCheckBoxClicked(Object obj) {
		TParm parm = tableM.getParmValue();
		// 强制失去编辑焦点
		if (this.tableM.getTable().isEditing()) {
			this.tableM.getTable().getCellEditor().stopCellEditing();
		}
		
		int count = parm.getCount();
		// 定制单号
		String enOrderNoList = "";
		// 配制单号
		String enPrepareNoList = "";
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (enOrderNoList.length() == 0) {
					enOrderNoList = parm.getValue("EN_ORDER_NO", i);
					enPrepareNoList = parm.getValue("EN_PREPARE_NO", i);
				} else {
					// 拼接选中数据行的定制单号
					enOrderNoList = enOrderNoList + "','"
							+ parm.getValue("EN_ORDER_NO", i);
					// 拼接选中数据行的配制单号
					enPrepareNoList = enPrepareNoList + "','"
							+ parm.getValue("EN_PREPARE_NO", i);
				}
			}
		}
		
		// 无勾选数据
		if (enOrderNoList.length() > 0) {
			TParm queryParm = new TParm();
			queryParm.setData("EN_ORDER_NO", enOrderNoList);
			queryParm.setData("EN_PREPARE_NO", enPrepareNoList);
			// 统计配方明细总量
			TParm result = NSSEnteralNutritionTool.getInstance()
					.queryENOrderDDataAccount(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询配方明细总量错误");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
			}
			
			if (result.getCount() <= 0) {
				this.messageBox("查无配方明细总量数据");
				tableD.setParmValue(new TParm());
				return;
			} else {
				tableD.setParmValue(result);
			}
		} else {
			tableD.setParmValue(new TParm());
		}
	}
	
    /**
     * 清空方法
     */
    public void onClear() {
    	// 初始化页面控件数据
    	this.onInitControl();
    }
    
	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		if (tableM.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "FLG", flg);
		}
		
		this.onTableMCheckBoxClicked(tableM);
	}
	
	/**
	 * 打印备料清单
	 */
	public void onPrintReady() {
		TParm parm = tableD.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印数据
		TParm printData = new TParm();
		int count = parm.getCount();
		
		printParm.setData("TITLE", "TEXT", Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) + "肠内营养备料单");
		printParm.setData("PRINT_DATE", "TEXT", "制表日期:"+todayDate);
		
		for (int i = 0; i < count; i++) {
			printData.addData("FORMULA_CHN_DESC", parm.getValue("FORMULA_CHN_DESC", i));
			printData.addData("TOTAL_QTY", parm.getDouble("MEDI_QTY", i));
			printData.addData("TOTAL_UNIT", parm.getValue("MEDI_UNIT", i));
		}
		
		printData.setCount(count);
		
		printData.addData("SYSTEM", "COLUMNS", "FORMULA_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_QTY");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_UNIT");
		
		printParm.setData("TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENMaterialsReadyPrint", printParm);
	}
	
	/**
	 * 打印交接单
	 */
	public void onPrintHandOver() {
		TParm parm = tableM.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印的数据
		TParm printData = new TParm();
		
		// 将勾选的数据按照病区分组
		printData = this.sortByStationCode(parm);
		
		if (printData.getErrCode() < 0) {
			this.messageBox(printData.getErrText());
			return;
		}
		
		printParm.setData("TITLE", "TEXT", Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) + "肠内营养交接单");
		printParm.setData("PRINT_DATE", "TEXT", "打印日期:"+todayDate);
		
		printData.setCount(printData.getCount("MR_NO"));
		
		printData.addData("SYSTEM", "COLUMNS", "STATION_CODE");
		printData.addData("SYSTEM", "COLUMNS", "BED_NO");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		printData.addData("SYSTEM", "COLUMNS", "FREQ");
		printData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_QTY");
		
		printParm.setData("TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENHandOverPrint", printParm);
	}
	
	/**
	 * 打印条码标签
	 */
	public void onPrintENBarCode() {
		TParm parm = tableM.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		// 打印用Parm
		TParm printParm = new TParm();
		TParm queryParm = new TParm();
		TParm result = new TParm();
		int count = parm.getCount();
		int selectedCount = 0;
		// 标签显示日期
		String completeDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 补打标识
		boolean reprintFlg = false;
		// 打印循环次数
		int printCount = 0;
		// 医嘱备注
		String drNote = "";
		
		// 配制状态
		if (getRadioButton("STATUS_N").isSelected()) {
			reprintFlg = false;
		} else {
			reprintFlg = true;
		}
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				queryParm = tableM.getParmValue().getRow(i);
				// 查询医嘱信息中的备注
				result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
						queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("查询医嘱信息异常");
					err(result.getErrCode() + " " + result.getErrText());
					return;
				}
				
				if (result.getCount() > 0) {
					drNote = result.getValue("DR_NOTE", 0);
				}
				
				if (StringUtils.isNotEmpty(drNote.trim())) {
					drNote = "(" + drNote + ")";
				}
				
				printParm = new TParm();
				// 条码
				printParm.setData("BAR_CODE", "TEXT", parm.getValue("EN_PREPARE_NO", i));
				printParm.setData("DIET_DESC", "TEXT", parm.getValue("ORDER_DESC", i));
				printParm.setData("DR_NOTE", "TEXT", drNote);
				printParm.setData("BED_DESC", "TEXT", parm.getValue("BED_NO_DESC", i));
				printParm.setData("PAT_NAME", "TEXT", parm.getValue("PAT_NAME", i));
				printParm.setData("MR_NO", "TEXT", parm.getValue("MR_NO", i));
				printParm.setData("MEDI_QTY", "TEXT", parm.getInt(
						"LABEL_CONTENT", i)
						+ parm.getValue("LABEL_UNIT", i)
						+ "*"
						+ parm.getValue("LABEL_QTY", i));
				if (StringUtils.isNotEmpty(parm.getValue("PREPARE_DATE", i))) {
					completeDate = parm.getValue("PREPARE_DATE", i);
				}
				printParm.setData("COM_DATE", "TEXT", completeDate);
				printParm.setData("DEPT_DESC", "TEXT", DeptTool.getInstance()
						.getDescByCode(Operator.getDept()));
				
				// 如果是补打则只补打一次，否则按照计算出的标签个数自动循环打印
				if (reprintFlg) {
					printCount = 1;
				} else {
					printCount = parm.getInt("LABEL_QTY", i);
				}
				
				for (int k = 0; k < printCount; k++) {
					this.printENBarCode(printParm);
				}
				selectedCount++;
			}
		}
		
		if (selectedCount < 1) {
			this.messageBox("请勾选要打印的数据");
			return;
		}
	}
	
	/**
	 * 打印条码标签
	 */
	private void printENBarCode(TParm printParm) {
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENBarCodePrint.jhw", printParm, true);
	}
	
	/**
	 * 将勾选的数据按照病区分组
	 */
	private TParm sortByStationCode(TParm parm) {
		TParm sortParm = new TParm();
		int count = parm.getCount();
		// 病区List
		List<String> stationCodeList = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (!stationCodeList.contains(parm.getValue("STATION_CODE", i))) {
					stationCodeList.add(parm.getValue("STATION_CODE", i));
				}
			}
		}
		
		// 如果没有勾选的数据
		if (stationCodeList.size() == 0) {
			sortParm.setErr(-1, "请勾选要打印的数据");
			return sortParm;
		}
		
		int stationCodeListSize = stationCodeList.size();
		for (int j = 0; j < stationCodeListSize; j++) {
			for (int k = 0; k < count; k++) {
				if (parm.getBoolean("FLG", k)
						&& StringUtils.equals(stationCodeList.get(j), parm
								.getValue("STATION_CODE", k))) {
					sortParm.addData("STATION_CODE", parm.getValue(
							"STATION_CODE", k));
					sortParm.addData("BED_NO", parm.getValue("BED_NO_DESC", k));
					sortParm.addData("MR_NO", parm.getValue("MR_NO", k));
					sortParm.addData("PAT_NAME", parm.getValue("PAT_NAME", k));
					sortParm.addData("MEDI_QTY", parm.getDouble(
							"LABEL_CONTENT", k)
							+ parm.getValue("LABEL_UNIT", k)
							+ "*"
							+ parm.getValue("LABEL_QTY", k));
					sortParm.addData("FREQ", parm.getValue("FREQ_CODE", k));
					sortParm
							.addData("TAKE_DAYS", parm.getValue("TAKE_DAYS", k));
					sortParm.addData("TOTAL_QTY", parm.getValue("TOTAL_QTY", k)
							+ parm.getValue("TOTAL_UNIT", k));
				}
			}
			
			// 组装打印数据
			this.addPrintParmData(sortParm);
		}
		
		return sortParm;
	}
	
    /**
     * 组装打印数据
     *
     * @param parm
     *            TParm
     * @return
     */
	private void addPrintParmData(TParm parm) {
		parm.addData("STATION_CODE", "");
		parm.addData("BED_NO", "");
		parm.addData("MR_NO", "");
		parm.addData("PAT_NAME", "");
		parm.addData("MEDI_QTY", "");
		parm.addData("FREQ", "");
		parm.addData("TAKE_DAYS", "");
		parm.addData("TOTAL_QTY", "交接人员:");
		
		parm.addData("STATION_CODE", "");
		parm.addData("BED_NO", "");
		parm.addData("MR_NO", "");
		parm.addData("PAT_NAME", "");
		parm.addData("MEDI_QTY", "");
		parm.addData("FREQ", "");
		parm.addData("TAKE_DAYS", "");
		parm.addData("TOTAL_QTY", "");
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
	
	/**
	 * 得到getCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
