package com.javahis.ui.nss;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 肠内营养定制</p>
 *
 * <p>Description: 肠内营养定制</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.16
 * @version 1.0
 */
public class NSSEnteralNutritionOrderControl extends TControl {
    public NSSEnteralNutritionOrderControl() {
        super();
    }

    private TTable tablePat; // 病患信息table
    private TTable tableDrOrder; // 住院医生医嘱table
    private TTable tableDietitionOrder; // 营养师医嘱table
    private TTable tableFormula; // 配方table
    private TTable tableNutrition; // 营养成分table
    private Map<String, String> dietitionOrderMap; // 营养师医嘱主项Map

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
		tablePat = getTable("TABLE_PAT");
    	tableDrOrder = getTable("TABLE_DR_ORDER");
    	tableDietitionOrder = getTable("TABLE_DIETITION_ORDER");
    	tableFormula = getTable("TABLE_FORMULA");
    	tableNutrition = getTable("TABLE_NUTRITION");
    	
		// 病患信息表格数据点击事件
		this.callFunction("UI|TABLE_PAT|addEventListener", "TABLE_PAT->"
				+ TTableEvent.CLICKED, this, "onTablePatClicked");
		// 住院医生饮食医嘱表格数据点击事件
		this.callFunction("UI|TABLE_DR_ORDER|addEventListener", "TABLE_DR_ORDER->"
				+ TTableEvent.CLICKED, this, "onTableDrOrderClicked");
		// 营养师医嘱表格数据点击事件
		this.callFunction("UI|TABLE_DIETITION_ORDER|addEventListener", "TABLE_DIETITION_ORDER->"
				+ TTableEvent.CLICKED, this, "onTableDietitionOrderClicked");
        // 注册激发TableFormula弹出的事件
		getTable("TABLE_FORMULA").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onTableFormulaInput");
		// 修改配方数量时的调用事件
		this.addEventListener("TABLE_FORMULA->" + TTableEvent.CHANGE_VALUE,
				"onTableFormulaChangeValue");
		
		// 初始化病患基本信息数据
		this.onInitPatInfo();
		
		// 初始化营养师医嘱数据
		this.onInitENDietitionOrderInfo();
		
		// 初始化营养成分表格数据
		this.onInitENNutritionTableInfo();
	}
	
	/**
	 * 初始化病患信息数据
	 */
	private void onInitPatInfo() {
		clearValue("ENO_STATION_CODE;ENO_DEPT_CODE;BED_NO_DESC;MR_NO;PAT_NAME;SEX_CODE;AGE;WEIGHT;HEIGHT;DR_DIET;EN_ORDER_NO");
		getRadioButton("ORDER_STATUS_N").setSelected(true);
	}
	
	/**
	 * 初始化营养师医嘱数据
	 */
	private void onInitENDietitionOrderInfo() {
		clearValue("MEDI_QTY;MEDI_UNIT;FREQ_CODE;TOTAL_QTY;TOTAL_UNIT;CONTAINER_CODE;LABEL_QTY;EN_ORDER_NO");
	}
	
	/**
	 * 初始化营养成分表格数据
	 */
	private void onInitENNutritionTableInfo() {
		TParm parm = new TParm();
		String[] nutritionArray = { "Ene,0kcal,'',KCl,0g", "Pro,0g,0%,NaCl,0g",
				"Fat,0g,0%,DF,0g", "Carb,0g,0%,Ca,0g", "N,0g,0%,VC,0g" };
		int count = nutritionArray.length;
		String[] dataArray = new String[3];
		for (int i = 0; i < count; i++) {
			dataArray = nutritionArray[i].split(",");
			parm.addData("NUTRITION_DESC_L", dataArray[0].replaceAll("'", ""));
			parm.addData("DATA_L", dataArray[1].replaceAll("'", ""));
			parm.addData("PROPORTION", dataArray[2].replaceAll("'", ""));
			parm.addData("NUTRITION_DESC_R", dataArray[3].replaceAll("'", ""));
			parm.addData("DATA_R", dataArray[4].replaceAll("'", ""));
		}
		
		tableNutrition.setParmValue(parm);
	}
	
    /**
     * 查询方法
     */
    public void onQuery() {
    	// 根据查询条件查询开立肠内营养饮食医嘱的病患信息
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENOrderPatInfo(this.getQueryParm());
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("查无数据");
        	// 初始化营养师医嘱数据
        	this.onInitENDietitionOrderInfo();
        	// 初始化营养成分表格数据
        	this.onInitENNutritionTableInfo();
        	
        	tablePat.setParmValue(new TParm());
    		tableDrOrder.setParmValue(new TParm());
    		tableDietitionOrder.setParmValue(new TParm());
    		tableFormula.setParmValue(new TParm());
    		return;
    	}
    	
    	// 对于未完成数据需要单独处理存在停用医嘱的数据
		if (getRadioButton("ORDER_STATUS_N").isSelected()) {
			TParm queryParm = new TParm();
	    	int count = result.getCount();
	    	for (int i = count - 1; i > -1; i--) {
	    		queryParm = new TParm();
	    		queryParm.setData("CASE_NO", result.getValue("CASE_NO", i));
	    		queryParm.setData("ORDER_NO", result.getValue("ORDER_NO", i));
	    		queryParm.setData("ORDER_SEQ", result.getInt("ORDER_SEQ", i));
	    		queryParm.setData("DIE_ORDER_STATUS", "Y");
	    		queryParm.setData("DR_ORDER_STATUS", "Y");
	    		
	    		// 查询当前病人是否存在使用中的营养师医嘱主项数据
	    		queryParm = NSSEnteralNutritionTool.getInstance().queryENOrderM(queryParm);
	    		
	    		if (queryParm.getErrCode() < 0) {
	    			this.messageBox("查询营养师医嘱主项数据错误");
	    			continue;
	    		}
	    		
	    		// 如果存在使用中的医嘱则校验该医嘱下是否存在配方
	    		if (queryParm.getCount() > 0) {
	    			queryParm = NSSEnteralNutritionTool.getInstance().queryENOrderD(queryParm.getRow(0));
	    			if (queryParm.getErrCode() < 0) {
	    				continue;
	    			}
	    			
	    			if (queryParm.getCount() > 0) {
	    				result.removeRow(i);
	    			}
	    		}
	    	}
		}
    	
    	if (result.getCount("CASE_NO") < 1) {
    		this.messageBox("查无数据");
    	}
    	
    	tablePat.setParmValue(result);
    	// 初始化营养师医嘱数据
    	this.onInitENDietitionOrderInfo();
    	// 初始化营养成分表格数据
    	this.onInitENNutritionTableInfo();
    	
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    }
    
    /**
     * 保存方法
     */
    public void onSave() {
    	// 数据校验
    	if (!this.validate()) {
    		return;
    	}
    	
    	// 根据营养师医嘱单号控件判断操作类型
    	String eNOrderNo = this.getValueString("EN_ORDER_NO");
    	
    	// 新增营养师医嘱主项
    	if (StringUtils.isEmpty(eNOrderNo)) {
    		// 获取保存数据
    		TParm saveParm = this.getSaveParmData();
    		
    		if (saveParm.getErrCode() < 0) {
    			this.messageBox(saveParm.getErrText());
    			return;
    		}
    		
    		// 保存营养师医嘱主项
    		TParm result = NSSEnteralNutritionTool.getInstance()
    				.insertNSSENOrderM(saveParm);
    		
    		if (result.getErrCode() < 0) {
    			this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
    		} else {
    			this.messageBox("P0001");
    			
    			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
    	} else {
    		// 更新注记
    		boolean updateFlg = false;
    		
        	// 当前选中的营养师医嘱主项
    		int selectedRow = tableDietitionOrder.getSelectedRow();
    		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
    		
    		// 如果营养师医嘱主项有改动
			if (!StringUtils.equals(this.getValueString("MEDI_QTY"),
					dietitionOrderMap.get("MEDI_QTY"))
					|| !StringUtils.equals(this.getValueString("MEDI_UNIT"),
							dietitionOrderMap.get("MEDI_UNIT"))
					|| !StringUtils.equals(this.getValueString("FREQ_CODE"),
							dietitionOrderMap.get("FREQ_CODE"))
					|| !StringUtils.equals(this.getValueString("TOTAL_QTY"),
							dietitionOrderMap.get("TOTAL_QTY"))
					|| !StringUtils.equals(this.getValueString("TOTAL_UNIT"),
							dietitionOrderMap.get("TOTAL_UNIT"))
					|| !StringUtils.equals(this
							.getValueString("CONTAINER_CODE"),
							dietitionOrderMap.get("CONTAINER_CODE"))) {
				
				data.setData("CANCEL_FLG", "N");
				data.setData("PREPARE_STATUS", "0");
        		// 更新之前先验证是否有已展开未取消且未配制的数据
        		TParm validateResult = NSSEnteralNutritionTool.getInstance().queryENDspnM(data);
        		
            	if (validateResult.getErrCode() < 0) {
            		this.messageBox("查询主项展开数据错误");
        			err("ERR:" + validateResult.getErrCode() + validateResult.getErrText());
        			return;
            	}
            	
            	if (validateResult.getCount() > 0) {
            		this.messageBox("该医嘱存在未进行配制的展开数据，请先取消已确认数据后再进行修改");
            		return;
            	}
				
				TParm updateParm = new TParm();
				updateParm.setData("CASE_NO", data.getValue("CASE_NO"));
				updateParm.setData("ORDER_NO", data.getValue("ORDER_NO"));
				updateParm.setData("ORDER_SEQ", data.getValue("ORDER_SEQ"));
				updateParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
				updateParm.setData("MEDI_QTY", this.getValueString("MEDI_QTY"));
				updateParm.setData("MEDI_UNIT", this.getValueInt("MEDI_UNIT"));
				updateParm.setData("FREQ_CODE", this.getValueString("FREQ_CODE"));
				
				TParm parm = new TParm();
				parm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
				parm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
				// 使用天数
				updateParm.setData("TAKE_DAYS", this.getTakeDays(parm));
				
				// 计算应打印标签个数
				TParm result = this.getLabelQty(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox(result.getErrText());
					return;
				}
				
				int labelQty = result.getInt("LABEL_QTY");
				// 标签个数计算
				updateParm.setData("LABEL_QTY", labelQty);
				// 每张标签含量
				double labelContent = this.getValueDouble("TOTAL_QTY") / labelQty;
				updateParm.setData("LABEL_CONTENT", labelContent);
				updateParm.setData("TOTAL_QTY", this.getValueInt("TOTAL_QTY"));
				updateParm.setData("TOTAL_UNIT", this.getValueString("TOTAL_UNIT"));
				updateParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
				updateParm.setData("OPT_USER", Operator.getID());
				updateParm.setData("OPT_TERM", Operator.getIP());
				
				// 更新营养师医嘱主项数据
				result = NSSEnteralNutritionTool.getInstance().updateENOrderM(updateParm);
				
	    		if (result.getErrCode() < 0) {
	    			this.messageBox("E0001");
	    			err("ERR:" + result.getErrCode() + result.getErrText());
	    			return;
	    		}
	    		
	    		updateFlg = true;
			}
    		
    		// 更新营养师医嘱细项
    		tableFormula.acceptText();
            TDataStore dataStore = tableFormula.getDataStore();
            
            // 如果配方明细有更新
            if (dataStore.getUpdateSQL().length > 0) {
            	// 选中的营养师医嘱主项
        		TParm selectedDietParm = tableDietitionOrder.getParmValue().getRow(tableDietitionOrder
        				.getSelectedRow());
        		selectedDietParm.setData("CANCEL_FLG", "N");
        		
        		// 更新之前先验证是否有已展开未取消数据
        		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(selectedDietParm);
        		
            	if (result.getErrCode() < 0) {
            		this.messageBox("查询主项展开数据错误");
        			err("ERR:" + result.getErrCode() + result.getErrText());
        			return;
            	}
            	
            	if (result.getCount() > 0) {
            		this.messageBox("该医嘱存在已展开未取消的数据，不可修改配方明细");
            		return;
            	}
            	
        		// 查询最大的SEQ
        		TParm parm = NSSEnteralNutritionTool.getInstance().queryENOrderD(data);
        		
        		int maxSeq = 0;
        		if (parm.getCount() > 0) {
        			maxSeq = parm.getInt("SEQ", parm.getCount() - 1);
        		}

                // 获得全部的新增行
                int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
                
    			for (int i = 0; i < newrows.length; i++) {
    				dataStore.setItem(newrows[i], "EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
    				dataStore.setItem(newrows[i], "MR_NO", data.getValue("MR_NO"));
    				dataStore.setItem(newrows[i], "SEQ", maxSeq + 1);
    				dataStore.setItem(newrows[i], "CASE_NO", data.getValue("CASE_NO"));
    				maxSeq++;
    			}
    			
    			if (!dataStore.update()) {
    				this.messageBox("E0001");
    				return;
    			} else {
    				updateFlg = true;
    			}
            }
            
			// 执行成功
			if (updateFlg) {
				TParm orderInfoParm = new TParm();
				orderInfoParm.setData("CASE_NO", data.getValue("CASE_NO"));
				orderInfoParm.setData("ORDER_NO", data.getValue("ORDER_NO"));
				orderInfoParm.setData("ORDER_SEQ", data.getValue("ORDER_SEQ"));
				orderInfoParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
				orderInfoParm.setData("ORDER_DEPT_CODE", Operator.getDept());
				orderInfoParm.setData("ORDER_DR_CODE", Operator.getID());
				
				// 定制数量或配方发生修改后更新开立人员信息
				TParm result = NSSEnteralNutritionTool.getInstance()
						.updateENOrderMOrderInfo(orderInfoParm);
				
				if (result.getErrCode() < 0) {
            		this.messageBox("更新操作人员信息数据错误");
        			err("ERR:" + result.getErrCode() + result.getErrText());
        			return;
            	}
            	
				this.messageBox("P0001");
			} else {
				this.messageBox("无修改数据");
				return;
			}
			
			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
			tableDietitionOrder.setSelectedRow(0);
			this.onTableDietitionOrderClicked(0);
    		
            return;
    	}
    }
    
    /**
     * 删除方法
     */
    public void onDelete() {
    	TParm parmDiet = tableDietitionOrder.getParmValue();
    	if (parmDiet == null || parmDiet.getCount() <= 0) {
    		this.messageBox("无删除数据");
    		return;
    	}
    	
    	if (tableDietitionOrder.getSelectedRow() < 0) {
    		this.messageBox("请选中要删除的营养师医嘱数据");
    		return;
    	}
    	
		// 强制失去编辑焦点
		if (tableDietitionOrder.getTable().isEditing()) {
			tableDietitionOrder.getTable().getCellEditor().stopCellEditing();
		}
		
		// 强制失去编辑焦点
		if (tableFormula.getTable().isEditing()) {
			tableFormula.getTable().getCellEditor().stopCellEditing();
		}
		
    	// 删除定制主项数据注记
    	boolean deleteTableDietFlg = false;
    	// 删除定制细项数据注记
    	boolean deleteTableFormulaFlg = false;
    	// 选中的营养师医嘱主项
		TParm selectedDietParm = parmDiet.getRow(tableDietitionOrder
				.getSelectedRow());
		selectedDietParm.setData("CANCEL_FLG", "N");
		
		// 更新之前先验证选中的营养师主项是否有已展开未取消数据
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(selectedDietParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询已展开医嘱数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() > 0) {
    		this.messageBox("该医嘱存在已展开并且未取消的数据，不可删除");
    		return;
    	}
		
    	for (int i = 0; i < parmDiet.getCount(); i++) {
    		if (parmDiet.getBoolean("FLG", i)) {
    			deleteTableDietFlg = true;
    			break;
    		}
    	}
    	
    	// 选中主项删除
    	if (deleteTableDietFlg) {
    		if (this.messageBox("删除", "确定是否删除该定制数据", 2) == 0) {
    			// 先删除主项下的所有细项
    			tableFormula.getDataStore().deleteRowAll();
    			
    			if (!tableFormula.getDataStore().update()) {
    				this.messageBox("E0001");
    				return;
    			}
    			
    			// 删除主项
				result = NSSEnteralNutritionTool.getInstance()
						.deleteNSSENOrderM(selectedDietParm);
				
		    	if (result.getErrCode() < 0) {
		    		this.messageBox("删除主项错误");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
		    	}
		    	
		    	this.messageBox("P0003");
				this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
    	} else {
    		int formulaCount = tableFormula.getDataStore().rowCount();
    		TParm formulaParm = tableFormula.getParmValue();
    		
    		// 如果没有选中主项数据，则查看是否选中了细项
    		for (int k = formulaCount - 1; k > -1; k--) {
				if (formulaParm.getBoolean("FLG", k)) {
					if (StringUtils.isNotEmpty(tableFormula.getDataStore().getItemString(k, "EN_ORDER_NO"))) {
						tableFormula.getDataStore().deleteRow(k);
						tableFormula.setDSValue();
					} else {
						tableFormula.removeRow(k);
						tableFormula.setDSValue();
					}
					deleteTableFormulaFlg = true;
				}
    		}
    		
			if (deleteTableFormulaFlg) {
				TParm sqlParm = new TParm();
				sqlParm.setData("DELETE_SQL", tableFormula.getDataStore().getUpdateSQL());
				// 执行删除操作
				result = TIOM_AppServer.executeAction(
						"action.nss.NSSEnteralNutritionAction",
						"deleteNSSENOrderD", sqlParm);
				
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					this.messageBox("E0005");
					return;
				}
				
				tableFormula.setDSValue();
				this.messageBox("P0003");
				// 根据配方明细表格中的值计算营养成分总量
				this.calculateTotalAmountOfNutrients();
			}
    		
    		// 主细项都没有勾选
    		if (!deleteTableDietFlg && !deleteTableFormulaFlg) {
    			this.messageBox("请勾选需要删除的数据");
    			return;
    		}
    	}
    	
    }
    
	/**
	 * 获取保存数据
	 * 
	 * @return
	 */
	private TParm getSaveParmData() {
		// 选中的住院医师医嘱
		TParm saveParm = tableDrOrder.getParmValue().getRow(
				tableDrOrder.getSelectedRow());
		
		// 取号原则
		saveParm.setData("EN_ORDER_NO", SystemTool.getInstance().getNo("ALL",
				"NSS", "EN_ORDER_NO", "EN_ORDER_NO"));
		saveParm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
		saveParm.setData("MEDI_UNIT", this.getValueString("MEDI_UNIT"));
		saveParm.setData("FREQ_CODE", this.getValueString("FREQ_CODE"));
		
		TParm parm = new TParm();
		parm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
		parm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
		
		// 计算使用天数
		saveParm.setData("TAKE_DAYS", this.getTakeDays(parm));
		saveParm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
		saveParm.setData("TOTAL_UNIT", this.getValueString("TOTAL_UNIT"));
		saveParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
		saveParm.setData("ORDER_DEPT_CODE", Operator.getDept());
		
		// 计算应打印标签个数
		TParm result = this.getLabelQty(parm);
		
		if (result.getErrCode() < 0) {
			return result;
		}
		
		// 标签个数
		int labelQty = result.getInt("LABEL_QTY");
		// 每张标签含量
		saveParm.setData("LABEL_QTY", labelQty);
		double labelContent = this.getValueDouble("TOTAL_QTY") / labelQty;
		saveParm.setData("LABEL_CONTENT", labelContent);
		saveParm.setData("ORDER_DEPT_CODE", Operator.getDept());
		saveParm.setData("ORDER_DR_CODE", Operator.getID());
		saveParm.setData("DC_DR_CODE", "");
		saveParm.setData("DC_DATE", "");
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		return saveParm;
	}
    
	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		// 饮食医嘱
		parm.setData("DR_DIET", getValueString("DR_DIET"));
		// 科室
		parm.setData("DEPT_CODE", getValueString("ENO_DEPT_CODE"));
		// 病区
		parm.setData("STATION_CODE", getValueString("ENO_STATION_CODE"));
		// 病案号
		parm.setData("MR_NO", getValueString("MR_NO"));
		// 完成状态
		if (getRadioButton("ORDER_STATUS_N").isSelected()) {
			parm.setData("ORDER_STATUS", "N");
		} else {
			parm.setData("ORDER_STATUS", "Y");
		}
		
		// 针对临时医嘱增加当前日期条件过滤，只在当天可以看到
		parm.setData("SYSDATE", SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace("-", ""));
		
		return parm;
	}
	
	/**
	 * 页面控件数据验证
	 */
	private boolean validate() {
		// 保存操作时需验证的内容
		// 用量
		if (this.getValueDouble("MEDI_QTY") == 0) {
			this.messageBox("请输入用量");
			return false;
		}
		// 用量单位
		if (StringUtils.isEmpty(this.getValueString("MEDI_UNIT"))) {
			this.messageBox("请输入用量单位");
			return false;
		}
		// 频次
		if (StringUtils.isEmpty(this.getValueString("FREQ_CODE"))) {
			this.messageBox("请输入频次");
			return false;
		}
		// 总量
		if (this.getValueDouble("TOTAL_QTY") == 0) {
			this.messageBox("请输入总量");
			return false;
		}
		// 总量单位
		if (StringUtils.isEmpty(this.getValueString("TOTAL_UNIT"))) {
			this.messageBox("请输入总量单位");
			return false;
		}
		// 容器
		if (StringUtils.isEmpty(this.getValueString("CONTAINER_CODE"))) {
			this.messageBox("请输入盛放容器");
			return false;
		}
		// 用量单位与总量单位必须一致
		if (!StringUtils.equals(this.getValueString("MEDI_UNIT"), this
				.getValueString("TOTAL_UNIT"))) {
			this.messageBox("用量单位与总量单位不一致");
			return false;
		}

		// 根据选择的容器查询对应的单位
		TParm result = NSSEnteralNutritionTool.getInstance().queryENContainer(
				this.getValueString("CONTAINER_CODE"));

		if (result.getErrCode() < 0) {
			this.messageBox("查询容器字典错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return false;
		}

		if (result.getCount() <= 0) {
			this.messageBox("查无容器字典数据");
			return false;
		}

		// 总量单位与容器单位必须一致
		if (!StringUtils.equals(this.getValueString("TOTAL_UNIT"), result
				.getValue("CAPACITY_UNIT", 0))) {
			this.messageBox("总量单位与容器规格单位不一致");
			return false;
		}
		
		tableFormula.acceptText();
        for (int i = 0; i < tableFormula.getRowCount(); i++) {
            if (!tableFormula.getDataStore().isActive(i)) {
                continue;
            }
            if (tableFormula.getItemDouble(i, "MEDI_QTY") <= 0) {
                this.messageBox("用量不能小于或等于0");
                return false;
            }
        }
		
		return true;
	}
	
    /**
     * 清空方法
     */
    public void onClear() {
    	// 初始化病患信息数据
    	this.onInitPatInfo();
    	// 初始化营养师医嘱数据
    	this.onInitENDietitionOrderInfo();
    	// 初始化营养成分表格数据
    	this.onInitENNutritionTableInfo();
    	
    	tablePat.setParmValue(new TParm());
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    }
    
	/**
	 * 添加对tablePat的监听事件
	 * 
	 * @param row
	 */
	public void onTablePatClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    	// 初始化营养师医嘱数据
    	this.onInitENDietitionOrderInfo();
    	// 初始化营养成分表格数据
    	this.onInitENNutritionTableInfo();
		
		int selectedRow = tablePat.getSelectedRow();
		TParm data = tablePat.getParmValue().getRow(selectedRow);
		// 向基本查询条件中上翻数据
		setValueForParm("ENO_DEPT_CODE;ENO_STATION_CODE;BED_NO_DESC;MR_NO;PAT_NAME;SEX_CODE;AGE;WEIGHT;HEIGHT", data);
		
		// 根据病患信息显示住院医生开立的饮食医嘱
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDrOrderInfo(data);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询住院医生医嘱错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("查无住院医生医嘱");
			return;
		}
		
		tableDrOrder.setParmValue(result);
	}
	
	/**
	 * 添加对tableDrOrder的监听事件
	 * 
	 * @param row
	 */
	public void onTableDrOrderClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
		
		int selectedRow = tableDrOrder.getSelectedRow();
		TParm data = tableDrOrder.getParmValue().getRow(selectedRow);
		
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			data.setData("DIE_ORDER_STATUS", "Y");
		} else {
			data.setData("DIE_ORDER_STATUS", "N");
		}
		
		// 根据选中的住院医生开立的饮食医嘱数据行查询对应的营养师医嘱主项数据
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(data);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询营养师医嘱主项错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		// 如果查到未停用营养师医嘱，则将数据带出
		if (result.getCount() > 0) {
	    	// 初始化营养师医嘱数据
	    	this.onInitENDietitionOrderInfo();
        	// 初始化营养成分表格数据
        	this.onInitENNutritionTableInfo();
	    	
			tableDietitionOrder.setParmValue(result);
			
			if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
				tableDietitionOrder.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
			} else {
				tableDietitionOrder.setLockColumns("all");
			}
		} else {
			if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
				// 初始化营养师医嘱数据
				this.onInitENDietitionOrderInfo();
	        	// 初始化营养成分表格数据
	        	this.onInitENNutritionTableInfo();

				tableDietitionOrder.setParmValue(new TParm());
				// 向营养师医嘱控件代入数据
				setValueForParm("MEDI_QTY;MEDI_UNIT;FREQ_CODE", data);
				this.setValue("TOTAL_QTY", data.getDouble("DOSAGE_QTY"));
				this.setValue("TOTAL_UNIT", data.getValue("DOSAGE_UNIT"));
				
				// 不论是长期还是临时，都根据用量与频次自动计算总量
				// 根据频次代码查询一天的执行次数
				result = NSSEnteralNutritionTool.getInstance().queryPhaFreq(
						this.getValueString("FREQ_CODE"));

				if (result.getErrCode() < 0) {
					this.messageBox("查询频次字典数据错误");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				if (result.getCount() <= 0) {
					this.messageBox("查无频次字典数据");
					return;
				}

				// 总量=用量*一天的次数
				double totalQty = data.getDouble("MEDI_QTY")
						* result.getDouble("FREQ_TIMES", 0);
				this.setValue("TOTAL_QTY", totalQty);
			}
		}
		
	}
	
	/**
	 * 添加对tableDietitionOrder的监听事件
	 * 
	 * @param row
	 */
	public void onTableDietitionOrderClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableFormula.setParmValue(new TParm());
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
		
		dietitionOrderMap = new HashMap<String, String>();
		dietitionOrderMap.put("MEDI_QTY", data.getValue("MEDI_QTY"));
		dietitionOrderMap.put("MEDI_UNIT", data.getValue("MEDI_UNIT"));
		dietitionOrderMap.put("FREQ_CODE", data.getValue("FREQ_CODE"));
		dietitionOrderMap.put("TOTAL_QTY", data.getValue("TOTAL_QTY"));
		dietitionOrderMap.put("TOTAL_UNIT", data.getValue("TOTAL_UNIT"));
		dietitionOrderMap.put("CONTAINER_CODE", data.getValue("CONTAINER_CODE"));
		
		// 向营养师医嘱控件代入数据
		setValueForParm("MEDI_QTY;MEDI_UNIT;FREQ_CODE;TOTAL_QTY;TOTAL_UNIT;CONTAINER_CODE;LABEL_QTY;EN_ORDER_NO", data);
		
		// 明细信息
		this.getTableFormulaInfo(data);
		// 明细表格增加一行
		this.addRow();
		
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			tableFormula.setLockColumns("3,4,5,6");
		} else {
			tableFormula.setLockColumns("all");
		}
		
		// 根据配方明细表格中的值计算营养成分总量
		this.calculateTotalAmountOfNutrients();
	}
	
    /**
     * 根据主项查询配方明细数据
     */
    private void getTableFormulaInfo(TParm parm) {
		// 明细信息
    	tableFormula.removeRowAll();
    	tableFormula.setSelectionMode(0);
		TDS tds = new TDS();
		String sql = NSSEnteralNutritionTool.getInstance().queryENOrderDSql(parm);
		tds.setSQL(sql);
		tds.retrieve();

		tableFormula.setDataStore(tds);
		tableFormula.setDSValue();
    }
	
    /**
     * 配方明细TABLE加入空行
     */
    private void addRow() {
		// 有未编辑行时返回
		if (!this.isNewRow()) {
			return;
		}
		int row = tableFormula.addRow();
		tableFormula.getDataStore().setActive(row, false);
    }
    
	/**
	 * 是否有未编辑行
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean flag = false;
		TParm parmBuff = tableFormula.getDataStore().getBuffer(
				tableFormula.getDataStore().PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			flag = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			flag = true;
		}
		return flag;
	}
	
    /**
     * 当TABLE创建编辑控件时
     *
     * @param com
     * @param row
     * @param column
     */
    public void onTableFormulaInput(Component com, int row, int column) {
        if (column != 1) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\nss\\NSSENFormulaPop.x"), new TParm());
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        // 当前选中的行数
        int selectedRow = tableFormula.getSelectedRow();
        tableFormula.acceptText();
        
        // 配方代码
        String fomulaCode = parm.getValue("FORMULA_CODE");
        // 配方中文名称
        String fomulaChnDesc = parm.getValue("FORMULA_CHN_DESC");
        // 配方用量
        double mediQty = parm.getDouble("MEDI_QTY");
        // 配方单位
        String mediUnit = parm.getValue("MEDI_UNIT");
        
        // 配方代码
        if (StringUtils.isNotEmpty(fomulaCode)) {
			tableFormula.getDataStore().setItem(selectedRow, "FORMULA_CODE", fomulaCode);
		}
		// 配方名称
		if (StringUtils.isNotEmpty(fomulaChnDesc)) {
			tableFormula.setItem(selectedRow, "FORMULA_CHN_DESC", fomulaChnDesc);
		}
		// 配方用量
		if (mediQty > 0) {
			tableFormula.setItem(selectedRow, "MEDI_QTY", mediQty);
		}
		// 配方单位
		if (StringUtils.isNotEmpty(mediUnit)) {
			tableFormula.setItem(selectedRow, "MEDI_UNIT", mediUnit);
		}
		
        tableFormula.setItem(selectedRow, "OPT_USER", Operator.getID());
        tableFormula.setItem(selectedRow, "OPT_DATE", SystemTool.getInstance()
				.getDate());
        tableFormula.setItem(selectedRow, "OPT_TERM", Operator.getIP());
        tableFormula.getDataStore().setActive(selectedRow, true);
		
        int count = tableFormula.getDataStore().rowCount();
        // 判断是否有重复数据
        for (int i = 0; i < count; i++) {
            if (i == selectedRow) {
                continue;
            }
            if (fomulaCode.equals(tableFormula.getDataStore().getItemData(i, "FORMULA_CODE"))) {
                this.messageBox("配方:【" + fomulaChnDesc + "】已存在");
                addRow();
                tableFormula.removeRow(selectedRow);
                return;
            }
        }
        
		// 增加新行
		this.addRow();
    }
    
	/**
	 * 修改配方数量时的调用事件
	 */
	public void onTableFormulaChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue())) {
			return;
		}
		// Table的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		if ("MEDI_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("用量不能小于或等于0");
				return;
			} else {
				node.getTable().getDataStore().setItem(node.getRow(),
						"MEDI_QTY", qty);
				// 根据配方明细表格中的值计算营养成分总量
				this.calculateTotalAmountOfNutrients();
			}
		}
	}
	
	/**
	 * 根据配方明细表格中的值计算营养成分总量
	 */
	private void calculateTotalAmountOfNutrients() {
	    TDataStore dataStore = tableFormula.getDataStore();
	    tableFormula.acceptText();
		TParm parm = new TParm();
		TParm result = new TParm();
		Map<String, Double> dataMap = this.getMapData();
		double qty = 0;
		
    	// 初始化营养成分表格数据
    	this.onInitENNutritionTableInfo();

		for (int i = 0; i < dataStore.rowCount(); i++) {
			if (dataStore.getItemDouble(i, "MEDI_QTY") > 0) {
				parm = new TParm();
				parm.setData("MEDI_QTY", dataStore.getItemDouble(i, "MEDI_QTY"));
				parm.setData("FORMULA_CODE", dataStore.getItemString(i,
						"FORMULA_CODE"));

				// 根据配方含量查询计算该配方下的营养成分含量
				result = NSSEnteralNutritionTool.getInstance()
						.queryNutritionContentQty(parm);

				if (result.getErrCode() < 0) {
					this.messageBox("计算营养成分含量错误");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				for (int k = 0; k < result.getCount(); k++) {
					qty = dataMap.get(result.getValue("NUTRITION_CHN_DESC", k));
					qty = qty + result.getDouble("CONTENT_QTY", k);
					dataMap.put(result.getValue("NUTRITION_CHN_DESC", k), qty);
				}
			}
		}

		this.setNutritionData(dataMap);
	}
	
	/**
	 * 设定营养成分含量以占比
	 */
	private void setNutritionData(Map<String, Double> dataMap) {
		tableNutrition.setItem(0, 1, this.countData(dataMap, 0, 1, "能量", "kcal"));
		tableNutrition.setItem(1, 1, this.countData(dataMap, 1, 1, "蛋白质", "g"));
		tableNutrition.setItem(2, 1, this.countData(dataMap, 2, 1, "脂肪", "g"));
		tableNutrition.setItem(3, 1, this.countData(dataMap, 3, 1, "碳水化合物", "g"));
		
		tableNutrition.setItem(0, 4, this.countData(dataMap, 0, 4, "钾", "g"));
		tableNutrition.setItem(1, 4, this.countData(dataMap, 1, 4, "钠", "g"));
		tableNutrition.setItem(2, 4, this.countData(dataMap, 2, 4, "膳食纤维", "g"));
		tableNutrition.setItem(3, 4, this.countData(dataMap, 3, 4, "钙", "g"));
		tableNutrition.setItem(4, 4, this.countData(dataMap, 4, 4, "VC", "g"));
		
		DecimalFormat df = new DecimalFormat("0.00");
		// 蛋白质含量
		double proteinQty = TypeTool.getDouble(tableNutrition.getItemString(1, 1)
				.replaceAll("g", ""));
		// 脂肪含量
		double fatQty = TypeTool.getDouble(tableNutrition.getItemString(2, 1)
				.replaceAll("g", ""));
		double NQty = proteinQty / 6.25;
		// 氮量=蛋白质/6.25
		tableNutrition.setItem(4, 1, df.format(NQty));
		
		// 总能量
		double energyQty = TypeTool.getDouble(tableNutrition.getItemString(0, 1)
				.replaceAll("kcal", ""));
		
		if (energyQty > 0) {
			// 蛋白％=蛋白质*4/能量*100
			tableNutrition.setItem(1, 2, df.format(proteinQty * 4 / energyQty * 100) + "%");
			// 脂肪％=脂肪*9/能量*100
			tableNutrition.setItem(2, 2, df.format(fatQty * 9 / energyQty * 100) + "%");
			// 碳水化合物％=100%-蛋白％-脂肪％
			tableNutrition.setItem(3, 2, df.format((1 - (proteinQty*4 + fatQty*9)/energyQty)*100) + "%");
		}
		
		if (proteinQty > 0) {
			// E/N=总能量/氮量
			tableNutrition.setItem(4, 2, df.format(energyQty / NQty));
		}
	}
	
	/**
	 * 根据公式计算含量
	 */
	private String countData(Map<String, Double> map,int row, int col, String key, String unit) {
		double data = map.get(key);
		if (StringUtils.equals("钾", key)) {
			data = data/1000*74.5/39;
		} else if (StringUtils.equals("钠", key)) {
			data = data/1000*58.5/23;
		} else if (StringUtils.equals("钙", key)) {
			data = data/1000;
		} else if (StringUtils.equals("VC", key)) {
			data = data/1000;
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(data) + unit;
	}
	
	/**
	 * 根据营养成分字典构造Map数据
	 */
	private Map<String, Double> getMapData() {
		Map<String, Double> dataMap = new HashMap<String, Double>();
		// 查询全部营养成分字典数据
		TParm result = NSSEnteralNutritionTool.getInstance().queryNSSNutrition();

		if (result.getErrCode() < 0) {
			this.messageBox("查询营养成分字典数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return dataMap;
		}

		if (result.getCount() <= 0) {
			this.messageBox("查无营养成分字典数据");
			return dataMap;
		}

		// 做好所有营养成分中文名称作为Key值的Map
		for (int i = 0; i < result.getCount(); i++) {
			if (!dataMap.containsKey(result.getValue("NUTRITION_CHN_DESC", i))) {
				dataMap.put(result.getValue("NUTRITION_CHN_DESC", i),
						new Double(0));
			}
		}
		
		return dataMap;
	}
    
	/**
	 * 添加对营养师医嘱停用切换的监听事件
	 */
	public void onChangeDieOrderStatus() {
		// 控件启用禁用切换
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			this.switchControlEnable(true);
		} else {
			this.switchControlEnable(false);
		}
		
    	// 初始化营养师医嘱数据
    	this.onInitENDietitionOrderInfo();
    	// 初始化营养成分表格数据
    	this.onInitENNutritionTableInfo();
		int selectedRow = tableDrOrder.getSelectedRow();
		this.onTableDrOrderClicked(selectedRow);
	}
	
	/**
	 * 停用按钮点击事件
	 */
	public void onDCButtonClick() {
		if (tableDietitionOrder == null
				|| tableDietitionOrder.getParmValue() == null) {
			this.messageBox("请先选中需要停用的营养师医嘱");
			return;
		}
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		if (selectedRow > -1) {
			TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
			data.setData("DC_DR_CODE", Operator.getID());
			data.setData("OPT_USER", Operator.getID());
			data.setData("OPT_TERM", Operator.getIP());
			
			// 停用选中的营养师医嘱
			TParm result = NSSEnteralNutritionTool.getInstance()
					.updateDCENOrderM(data);
			
			if (result.getErrCode() < 0) {
    			this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
    		} else {
    			this.messageBox("P0005");
    			
    			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
		} else {
			this.messageBox("请先选中需要停用的营养师医嘱");
			return;
		}
	}
	
	/**
	 * 控件禁用启用切换方法
	 */
	public void switchControlEnable(boolean flg) {
		this.callFunction("UI|DC_BUTTON|setEnabled", flg);
		this.callFunction("UI|MEDI_QTY|setEnabled", flg);
		this.callFunction("UI|MEDI_UNIT|setEnabled", flg);
		this.callFunction("UI|FREQ_CODE|setEnabled", flg);
		this.callFunction("UI|TOTAL_QTY|setEnabled", flg);
		this.callFunction("UI|TOTAL_UNIT|setEnabled", flg);
		this.callFunction("UI|CONTAINER_CODE|setEnabled", flg);
	}
	
	/**
	 * 计算天数
	 * 
	 * @return 天数
	 */
	private int getTakeDays(TParm parm) {
		int takeDays = 0;
		// 根据频次代码查询一天的执行次数
		TParm result = NSSEnteralNutritionTool.getInstance().queryPhaFreq(
				this.getValueString("FREQ_CODE"));
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询频次字典数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return takeDays;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("查无频次字典数据");
			return takeDays;
		}
		
		// 使用天数=总量/(用量*一天的次数)
		takeDays = (int) Math.ceil(parm.getDouble("TOTAL_QTY")
				/ (parm.getDouble("MEDI_QTY") * result.getDouble("FREQ_TIMES", 0)));
		
		return takeDays;
	}
	
	/**
	 * 计算应打印标签个数
	 * 
	 * @return 标签个数
	 */
	private TParm getLabelQty(TParm parm) {
		int labelQty = 0;
		TParm labelParm = new TParm();
		
		// 选中的住院医师医嘱
		TParm saveParm = tableDrOrder.getParmValue().getRow(
				tableDrOrder.getSelectedRow());
		// 查询饮食种类字典
		TParm categoryResult = NSSEnteralNutritionTool.getInstance()
				.queryENCategory(saveParm.getValue("ORDER_CODE"));
		
    	if (categoryResult.getErrCode() < 0) {
    		categoryResult.setErr(-1, "查询饮食种类字典错误");
			return categoryResult;
    	}
    	
    	if (categoryResult.getCount() <= 0) {
    		categoryResult.setErr(-1, "查无饮食种类字典数据");
    		return categoryResult;
    	}
		
		// 根据选择的容器查询对应的单位
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENContainer(this.getValueString("CONTAINER_CODE"));
		
    	if (result.getErrCode() < 0) {
    		result.setErr(-1, "查询容器字典错误");
			return result;
    	}
    	
    	if (result.getCount() <= 0) {
    		result.setErr(-1, "查无容器字典数据");
			return result;
    	}
    	
		// 按总量打印
		if (categoryResult.getBoolean("TOTAL_PRINT_FLG", 0)) {
			labelQty = 1;
		} else if (categoryResult.getBoolean("FREQ_PRINT_FLG", 0)) {
			// 标签个数 = 总量/用量 * 取整后(用量/容器容量)
			labelQty = ((int) Math.ceil(parm.getDouble("TOTAL_QTY")
					/ parm.getDouble("MEDI_QTY")))
					* ((int) Math.ceil(parm.getDouble("MEDI_QTY")
							/ result.getDouble("CAPACITY", 0)));
		} else {
			labelParm.setErr(-1, "该饮食种类未设定打印参数");
    		return labelParm;
		}
    	
		labelParm.setData("LABEL_QTY", labelQty);
		
		return labelParm;
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
	 * 查询指定病患本次入院后的所有定制配方记录
	 */
	public void onQueryOrderHistory() {
		if (tableDietitionOrder.getSelectedRow() < 0) {
			this.messageBox("请选中一行营养师医嘱");
			return;
		}
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
		
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", data.getValue("CASE_NO"));
		queryParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
		Object result = openDialog(
				"%ROOT%\\config\\nss\\NSSEnteralNutritionOrderHistory.x",
				queryParm);
		if (result != null) {
			if (result instanceof TParm) {
				TParm parm = (TParm) result;
				int count = parm.getCount();
				// 当前配方明细表中的现有数据行数
				int seq = tableFormula.getDataStore().rowCount();
				if (seq > 1) {
					// 如果本身有配方数据，则不允许再调用历史记录
					this.messageBox("请先清空配方数据再引用");
					return;
				}
				
				tableFormula.acceptText();
				for (int i = 0; i < count; i++) {
					if (parm.getBoolean("FLG", i)) {
						tableFormula.setSelectedRow(seq-1);
						this.popReturn("", parm.getRow(i));
						seq = seq + 1;
					}
				}
				
				// 根据配方明细表格中的值计算营养成分总量
				this.calculateTotalAmountOfNutrients();
			} else {
				this.messageBox("参数传回错误");
				return;
			}
		}
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
