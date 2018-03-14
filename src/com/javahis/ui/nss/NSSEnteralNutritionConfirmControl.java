package com.javahis.ui.nss;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 肠内营养确认</p>
 *
 * <p>Description: 肠内营养确认</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.25
 * @version 1.0
 */
public class NSSEnteralNutritionConfirmControl extends TControl {
    public NSSEnteralNutritionConfirmControl() {
        super();
    }

    private TTable tableOrderM; // 营养师医嘱主项
    private TTable tableDSPNM; // 营养师医嘱展开主项
    private String unfoldEndDate; // 展开截止时间

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
		tableOrderM = getTable("TABLE_ORDERM");
		tableDSPNM = getTable("TABLE_DSPNM");
		
		clearValue("DR_DIET;ENC_DEPT_CODE;ENC_STATION_CODE;MR_NO;SELECT_ALL");
    	// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// 设定默认展开日期
    	this.setValue("CONFIRM_DATE_S", todayDate);
    	this.setValue("CONFIRM_DATE_E", todayDate);
    	// 设定默认查询日期
    	this.setValue("QUERY_DATE_S", todayDate);
    	this.setValue("QUERY_DATE_E", todayDate);
    	// 最大展开截止时间
    	unfoldEndDate = todayDate;
	}
	
    /**
     * 查询方法营养师医嘱主项
     */
    public void onQuery() {
    	TParm queryParm = new TParm();
    	queryParm.setData("ORDER_CODE", this.getValueString("DR_DIET"));
    	queryParm.setData("DEPT_CODE", this.getValueString("ENC_DEPT_CODE"));
    	queryParm.setData("STATION_CODE", this.getValueString("ENC_STATION_CODE"));
    	queryParm.setData("MR_NO", this.getValueString("MR_NO"));
    	// 只查询使用中的营养师医嘱
    	queryParm.setData("DIE_ORDER_STATUS", "Y");
    	// 只查询使用中的住院医师医嘱
    	queryParm.setData("DR_ORDER_STATUS", "Y");
		// 医嘱类型
		if (getRadioButton("RX_KIND_UD").isSelected()) {
			queryParm.setData("RX_KIND", "UD");
			callFunction("UI|CONFIRM_DATE_E|setEnabled", true);
		} else {
			queryParm.setData("RX_KIND", "ST");
			// 取得当前日期
			String todayDate = SystemTool.getInstance().getDate().toString()
					.substring(0, 10).replace('-', '/');
	    	this.setValue("CONFIRM_DATE_E", todayDate);
	    	// 最大展开截止时间
	    	unfoldEndDate = todayDate;
			callFunction("UI|CONFIRM_DATE_E|setEnabled", false);
		}
		queryParm.setData("NOW_DATE", SystemTool.getInstance().getDate()
				.toString().substring(0, 10).replaceAll("-", ""));
    	
    	// 查询营养师医嘱主项数据
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(
				queryParm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询营养师医嘱主项错误");
    		tableOrderM.setParmValue(new TParm());
    		tableDSPNM.setParmValue(new TParm());
    		clearValue("SELECT_ALL");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("查无营养师医嘱主项数据");
    		tableOrderM.setParmValue(new TParm());
    		tableDSPNM.setParmValue(new TParm());
    		clearValue("SELECT_ALL");
    		return;
    	} else {
    		tableOrderM.setParmValue(result);
    	}
    	
    	// 清空勾选注记
    	clearValue("SELECT_ALL");
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
     * 查询已确认展开的医嘱主项
     */
    public void onQueryConfirmedData() {
    	TParm parm = new TParm();
    	
    	if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
    		tableDSPNM.setParmValue(new TParm());
    		this.messageBox("请输入查询时间");
    		return;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		if (StringUtils.isNotEmpty(this.getValueString("DR_DIET").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("DR_DIET"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("ENC_DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("ENC_DEPT_CODE"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("ENC_STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("ENC_STATION_CODE"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}

		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENDspnM(parm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询营养师医嘱展开主项错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("查无营养师医嘱展开主项数据");
    		tableDSPNM.setParmValue(new TParm());
    		return;
    	} else {
    		tableDSPNM.setParmValue(result);
    	}
    	
    	for (int i = 0; i < tableDSPNM.getRowCount(); i++) {
			// 已取消的数据行背景颜色设置为黄色
			if (tableDSPNM.getParmValue().getBoolean("CANCEL_FLG", i)) {
				tableDSPNM.setRowColor(i, new Color(255, 255, 0));
			} else {
				// 否则保留原色
				tableDSPNM.removeRowColor(i);
			}
		}
    }
    
    /**
     * 	饮食医嘱改变事件
     */
    public void onDietChange() {
    	// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
    	String drDietCode = this.getValueString("DR_DIET");
    	
    	// 切换饮食医嘱后及时清空主表数据
    	tableOrderM.setParmValue(new TParm());
    	clearValue("SELECT_ALL");
    	
    	// 如果没有选择饮食医嘱，则默认只能展开当天
    	if (StringUtils.isEmpty(drDietCode)) {
    		// 设定默认展开日期
        	this.setValue("CONFIRM_DATE_E", todayDate);
        	// 最大展开截止时间
        	unfoldEndDate = todayDate;
    		return;
    	} else {
    		// 医嘱类型
			if (getRadioButton("RX_KIND_UD").isSelected()) {
				// 查询饮食种类字典
				TParm result = NSSEnteralNutritionTool.getInstance()
						.queryENCategory(drDietCode);

				if (result.getErrCode() < 0) {
					this.messageBox("查询饮食种类字典错误");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				if (result.getCount() <= 0) {
					this.messageBox("查无饮食种类字典数据");
					return;
				} else {
					int dayCount = result.getInt("VALID_PERIOD", 0) - 1;
					String dateStr = this.addDay(todayDate, dayCount);
					// 根据饮食种类的保质期来设定默认展开日期
					this.setValue("CONFIRM_DATE_E", dateStr);
					// 最大展开截止时间
					unfoldEndDate = dateStr;
				}
			}
    	}
    }
    
	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		if (tableOrderM.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableOrderM.getRowCount(); i++) {
			tableOrderM.setItem(i, "FLG", flg);
		}
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		// 强制失去编辑焦点
		if (tableOrderM.getTable().isEditing()) {
			tableOrderM.getTable().getCellEditor().stopCellEditing();
		}
		
		if (tableOrderM.getRowCount() <= 0) {
			this.messageBox("请选中要展开的医嘱主项");
			return;
		} else {
			TParm parm = tableOrderM.getParmValue();
			TParm queryParm = new TParm();
			TParm result = new TParm();
			boolean checkFlg = false;
			String userId = Operator.getID();
			String userIp = Operator.getIP();
			// 住院医生停用医嘱List
			List<String> admDcMrNoList = new ArrayList<String>();
			// 营养师停用医嘱List
			List<String> dieDcMrNoList = new ArrayList<String>();
			String message = "";
			// 成功展开数据条数
			int saveCount = 0;
			
			int count = parm.getCount();
			for (int i = count - 1; i > -1 ; i--) {
				if (parm.getBoolean("FLG", i)) {
					checkFlg = true;
				} else {
					parm.removeRow(i);
				}
			}
			
			if (!checkFlg) {
				this.messageBox("请选中要展开的医嘱主项");
				this.onQuery();
				return;
			} else {
				// 展开日期的间隔天数
				int unfoldDateCount = 0;
				
				// 医嘱类型
				if (getRadioButton("RX_KIND_UD").isSelected()) {
					// 计算展开日期的间隔天数
					unfoldDateCount = StringTool.getInt(DateUtil.getTwoDay(this
							.getValueString("CONFIRM_DATE_E").substring(0, 10)
							.replace('-', '/'), this.getValueString(
							"CONFIRM_DATE_S").substring(0, 10).replace('-', '/')));
				} else {
					unfoldDateCount = 0;
				}
				
				if (unfoldDateCount < 0) {
					this.messageBox("展开截止日期不能小于开始日期");
					return;
				}
				
				// 计算允许的最大截止日期和用户当前选择的截止日期相差天数
				int endDateCount = StringTool.getInt(DateUtil.getTwoDay(this
						.getValueString("CONFIRM_DATE_E").substring(0, 10)
						.replace('-', '/'), unfoldEndDate.substring(0, 10)
						.replace('-', '/')));
				
				if (endDateCount > 0) {
					this.messageBox("展开截止日期不能大于最大展开日期："+unfoldEndDate);
					return;
				}
				
				TParm saveParm = parm;
				count = saveParm.getCount();
				// 展开日期
				String unfoldDate = "";
				// 定制日期
				String orderDate = "";
				
				for (int i = 0; i <= unfoldDateCount; i++) {
					// 取得当前应展开日期
					unfoldDate = this.addDay(this.getValueString(
							"CONFIRM_DATE_S").substring(0, 10)
							.replace('-', '/'), i);
					
					for (int k = 0; k < count; k++) {
						queryParm = new TParm();
						queryParm.setData("CASE_NO", saveParm.getValue("CASE_NO", k));
						queryParm.setData("EN_ORDER_NO", saveParm.getValue("EN_ORDER_NO", k));
						queryParm.setData("EN_PREPARE_DATE", unfoldDate);
						// 未取消
						queryParm.setData("CANCEL_FLG", "N");
						
						// 验证该表中相同日期下是否存在未取消的相同定制单数据
						result = NSSEnteralNutritionTool.getInstance().queryENDspnM(
								queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("查询展开数据错误");
							err("ERR:" + result.getErrCode() + result.getErrText()
									+ result.getErrName());
							return;
						}
						
						// 如果已存在未取消数据则剔除,避免重复插入
						if (result.getCount() > 0) {
							// 应配制日期
							saveParm.setData("EXIST_FLG", k, true);
						} else {
							saveParm.setData("EXIST_FLG", k, false);
						}
						
						queryParm.setData("ORDER_NO", saveParm.getValue("ORDER_NO", k));
						queryParm.setData("ORDER_SEQ", saveParm.getValue("ORDER_SEQ", k));
						// 展开时实时查询数据库中该医嘱是否被停用，避免界面没有刷新展开无用数据
						result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
								queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("查询医嘱信息异常");
							err(result.getErrCode() + " " + result.getErrText());
							return;
						}
						
						// 停用注记
						saveParm.setData("DC_FLG", k, false);
						// 住院医生停用
						if (StringUtils.isNotEmpty(result
								.getValue("DC_DATE", 0))) {
							if (!admDcMrNoList.contains(saveParm.getValue(
									"MR_NO", k))) {
								admDcMrNoList.add(saveParm.getValue("MR_NO", k)
										+ ","
										+ saveParm.getValue("PAT_NAME", k));
							}
							// 停用注记不做展开
							saveParm.setData("DC_FLG", k, true);
						} else if (StringUtils.isNotEmpty(result.getValue(
								"EN_DC_DATE", 0))) {
							// 营养师停用
							if (!dieDcMrNoList.contains(saveParm.getValue(
									"MR_NO", k))) {
								dieDcMrNoList.add(saveParm.getValue("MR_NO", k)
										+ ","
										+ saveParm.getValue("PAT_NAME", k));
							}
							// 停用注记不做展开
							saveParm.setData("DC_FLG", k, true);
						}
						
						// 确认配制单号
						saveParm.setData("EN_PREPARE_NO", k, "");
						// 应配制日期
						saveParm.setData("EN_PREPARE_DATE", k, unfoldDate);
						// 累计使用量
						saveParm.setData("TOTAL_ACCU_QTY", k, 0);
						
						orderDate = saveParm.getValue("ORDER_DATE", k);
						if (null != orderDate && orderDate.length() > 19) {
							orderDate = orderDate.substring(0, 19);
						}
						// 定制日期
						saveParm.setData("ORDER_DATE", k, orderDate);
						// 确认人员
						saveParm.setData("CONFIRM_DR_CODE", k, userId);
						// 配制状态(0_未完成,1_已完成)
						saveParm.setData("PREPARE_STATUS", k, "0");
						// 配制人员
						saveParm.setData("PREPARE_DR_CODE", k, "");
						// 配制时间
						saveParm.setData("PREPARE_DATE", k, "");
						// 收费注记
						saveParm.setData("BILL_FLG", k, "N");
						// 执行注记
						saveParm.setData("EXEC_STATUS", k, "0");
						// 取消
						saveParm.setData("CANCEL_FLG", k, "N");
						// 操作人员
						saveParm.setData("OPT_USER", k, userId);
						// 操作时间
						saveParm.setData("OPT_TERM", k, userIp);
						// 序号
						saveParm.setData("SEQ", k, 0);
						// 执行注记(Y_已执行,N_未执行)
						saveParm.setData("EXEC_FLG", k, "N");
						// 执行人员
						saveParm.setData("EXEC_USER", k, "");
						// 执行时间
						saveParm.setData("EXEC_DATE", k, "");
						
						if (StringUtils.isEmpty(saveParm.getValue("DC_DATE", k))) {
							// 停用日期
							saveParm.setData("DC_DATE", k, "");
						}
						
						queryParm = new TParm();
						queryParm.setData("CASE_NO", saveParm.getValue("CASE_NO", k));
						// 每次展开数据时从ADM_INP表取最新的床号避免转床后床号错误
						result = ADMInpTool.getInstance().selectall(queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("查询最新床位号错误");
							err("ERR:" + result.getErrCode() + result.getErrText()
									+ result.getErrName());
						}
						
						if (result.getCount() > 0) {
							saveParm.setData("BED_NO", k, result.getValue("BED_NO", 0));
						}
					}
					
					// 执行保存操作
					result = TIOM_AppServer.executeAction(
							"action.nss.NSSEnteralNutritionAction",
							"onSaveNSSENDspnM", saveParm);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						this.messageBox("E0001");
						return;
					}
					
					// 计算累计展开数量
					saveCount = saveCount + result.getInt("SAVE_COUNT");
				}

				int admDcCount = admDcMrNoList.size();
				if (admDcCount > 0) {
					message = "";
					for (int m = 0; m < admDcCount; m++) {
						message = message + "病案号："
								+ admDcMrNoList.get(m).split(",")[0] + "，姓名："
								+ admDcMrNoList.get(m).split(",")[1] + "\n";
					}
					
					message = message + "因住院医生停用医嘱，数据未能展开";
					this.messageBox(message);
				}
				
				int dieDcCount = dieDcMrNoList.size();
				if (dieDcCount > 0) {
					message = "";
					for (int m = 0; m < dieDcCount; m++) {
						message = message + "病案号："
								+ dieDcMrNoList.get(m).split(",")[0] + "，姓名："
								+ dieDcMrNoList.get(m).split(",")[1] + "\n";
					}
					
					message = message + "因营养师停用配方，数据未能展开";
					this.messageBox(message);
				}
				
				if (saveCount > 0) {
					this.messageBox("P0001");
				} else if (saveCount == 0 && message.length() == 0) {
					this.messageBox("P0001");
				}
				
				// 刷新界面数据
				this.onQuery();
			}
		}
	}
	
    /**
     * 取消展开数据
     */
    public void onDelete() {
    	if (tableDSPNM.getSelectedRow() < 0) {
    		this.messageBox("请选中需要取消的展开数据");
    		return;
    	}
    	
		TParm queryParm = tableDSPNM.getParmValue().getRow(
				tableDSPNM.getSelectedRow());
		queryParm.setData("EN_PREPARE_DATE", queryParm.getValue("EN_PREPARE_DATE").substring(
				0, 10).replace('-', '/'));
    	// 使用选中的数据行查询数据验证该数据是否已经完成配制
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询营养师医嘱展开主项错误");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	// 如果已经完成配制，则不允许取消
    	if (StringUtils.equals("1", result.getValue("PREPARE_STATUS", 0))) {
    		this.messageBox("该展开数据已经完成配制不可取消");
    		return;
    	} else {
    		queryParm.setData("OPT_USER", Operator.getID());
    		queryParm.setData("OPT_TERM", Operator.getIP());
			result = NSSEnteralNutritionTool.getInstance()
					.updateENDspnMByCancel(queryParm);
			
			if (result.getErrCode() < 0) {
	    		this.messageBox("取消展开医嘱错误");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
	    	}
			
			this.messageBox("P0005");
			this.onQueryConfirmedData();
    	}
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
    	// 初始化页面控件数据
    	this.onInitPage();
    	tableOrderM.setParmValue(new TParm());
    	tableDSPNM.setParmValue(new TParm());
    }
    
	/**
	 * 根据指定日期计算指定天数后的日期
	 * 
     * @param date
     *            计算前日期
     * @param num
     *            计算间隔天数
     * @return 计算后日期(格式:yyyy/MM/dd)
	 */
	private String addDay(String date, int num) {
		return StringTool.getString(DateUtils.addDays(DateUtil.strToDate(date),
				num), "yyyy/MM/dd");
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
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
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
