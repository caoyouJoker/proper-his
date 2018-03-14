package com.javahis.ui.odi;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.erd.ERDCISVitalSignTool;
import jdo.odi.ODICISVitalSignTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 病区CIS体征监测数据
 * </p>
 * 
 * <p>
 * Description: 病区CIS体征监测数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2015.3.25
 * @version 1.0
 */
public class ODICISVitalSignControl extends TControl {
	
	private TTable table;
	
    public ODICISVitalSignControl() {
        super();
    }

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
		table = this.getTable("TABLE");
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", now);
		this.setValue("E_DATE", now);
		this.getTComboBox("QUERY_TYPE").setSelectedIndex(0);
	}
	
    /**
     * 查询病区CIS体征监测数据
     */
	public void onQuery() {
		table.setParmValue(new TParm());
		this.clearValue("TOTAL_COUNT;ACTUAL_TOTAL_COUNT");
		
		TParm queryParm = new TParm();
		String startDate = this.getValueString("S_DATE").substring(0, 10).replaceAll("-", "/") + " "
				+ this.getValueString("S_TIME");
		String endDate = this.getValueString("E_DATE").substring(0, 10).replaceAll("-", "/") + " "
				+ this.getValueString("E_TIME");
		
		String tempEndDate = endDate;
		String dataBase = "";
		String viewName = "";
		String queryType = "";
		TParm result = new TParm();
		String className = "";
		String methodName = "";
		// 从配置文件取得抓取CIS接口数据的分批查询时间
		long bathcPeriodTime = TypeTool.getLong(getProp().getString("CIS.ODIBatchPeriodTime"));
		
		try {
			Date sDate = StringTool.getDate(startDate + ":00",
					"yyyy/MM/dd HH:mm:ss");
			Date eDate = StringTool
					.getDate(endDate + ":00", "yyyy/MM/dd HH:mm:ss");
			
			if (null == sDate || null == eDate) {
				this.messageBox("日期格式错误");
				this.setValue("S_TIME", "00:00");
				this.setValue("E_TIME", "00:29");
			}
		} catch (Exception e) {
			this.messageBox("日期格式错误");
			this.setValue("S_TIME", "00:00");
			this.setValue("E_TIME", "00:29");
			return;
		}
		
		if (StringUtils.equals("0", getTComboBox("QUERY_TYPE").getSelectedID())) {
			dataBase = "javahisICU";
			viewName = "dbo.V_ICU_Vitalsigns";
			queryType = "ODI";
		} else if (StringUtils.equals("1", getTComboBox("QUERY_TYPE").getSelectedID())) {
			dataBase = "javahisCCU";
			viewName = "dbo.V_CCU_Vitalsigns";
			queryType = "ODI";
		} else if (StringUtils.equals("2", getTComboBox("QUERY_TYPE").getSelectedID())) {
			dataBase = "javahisWard";
			viewName = "dbo.V_Ward_Vitalsigns";
			queryType = "ODI";
		} else if (StringUtils.equals("3", getTComboBox("QUERY_TYPE").getSelectedID())) {
			dataBase = "javahisEMD";
			viewName = "dbo.V_EMD_Vitalsigns";
			queryType = "ERD";
			table.setHeader(table.getHeader().replaceAll("STATION_CODE", "CLINIC_CODE"));
			bathcPeriodTime = TypeTool.getLong(getProp().getString("CIS.ERDBatchPeriodTime"));
		} else {
			this.messageBox("请选择提取类别");
			return;
		}
		
		// 将毫秒转换为分钟
		bathcPeriodTime = bathcPeriodTime/60000;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		
		try {
			// 为避免时间间隔过长导致访问数据超时，采取以配置文件的分批间隔时间为分隔界限分批捞取数据的方式
			Date d1 = df.parse(startDate);
			Date d2 = df.parse(endDate);
			double diff = d2.getTime() - d1.getTime();
			int days = (int) Math.floor(diff / (1000 * 60 * bathcPeriodTime));
			if (days == 0) {
				days = 1;
			}
			Date date = new Date();
			TParm saveResult  = new TParm();
			TParm showParm = new TParm();
			TParm parm = new TParm();
			int count = 0;
			int actualTotalAccount = 0;

			for (int j = 1; j <= days; j++) {
				if (j == days) {
					date = DateUtils.addMinutes(StringTool.getDate(tempEndDate,
							"yyyy/MM/dd HH:mm"), TypeTool.getInt(-1));
					endDate = StringTool.getString(date, "yyyy/MM/dd HH:mm");
				} else {
					date = DateUtils.addMinutes(StringTool.getDate(startDate,
							"yyyy/MM/dd HH:mm"), TypeTool
							.getInt(bathcPeriodTime - 1));
					endDate = StringTool.getString(date, "yyyy/MM/dd HH:mm");
				}

				queryParm.setData("START_POOLING_TIME", startDate);
				queryParm.setData("END_POOLING_TIME", endDate);
				
				if (StringUtils.equals("ODI", queryType)) {
					className = "action.odi.ODICISVitalSignAction";
					methodName = "onInsertODICISVitalSignByManually";
					// 查询病区数据
					result = ODICISVitalSignTool.getInstance().queryODICISData(
							queryParm, viewName, dataBase);
				} else {
					className = "action.erd.ERDCISVitalSignAction";
					methodName = "onInsertERDCISVitalSignByManually";
					// 查询急诊数据
					result = ERDCISVitalSignTool.getInstance().queryERDCISData(
							queryParm);
				}
				
				if (result.getErrCode() < 0) {
					this.messageBox("查询错误:" + result.getErrText());
					return;
				}

				count = result.getCount("BED_NO");
				if (count > 0) {
					// 数据过滤处理:只取整分钟的数据，同一分钟若有不同秒数的数据，则取最靠近下一分钟的数据
					List<String> filterList = new ArrayList<String>();
					String keyStr = "";

					if (StringUtils.equals("ODI", queryType)) {
						for (int i = 0; i < count; i++) {
							keyStr = result.getValue("CASE_NO", i) + "_"
									+ result.getValue("MONITOR_TIME", i) + "_"
									+ result.getValue("MONITOR_ITEM_EN", i);
							if (!filterList.contains(keyStr)) {
								filterList.add(keyStr);
								result.setData("INSERT_FLG", i, "Y");
								actualTotalAccount++;
							} else {
								result.setData("INSERT_FLG", i, "N");
							}
						}
					} else {
						for (int i = 0; i < count; i++) {
							keyStr = result.getValue("BED_NO", i) + "_"
									+ result.getValue("MONITOR_TIME", i) + "_"
									+ result.getValue("MONITOR_ITEM_EN", i);

							if (!filterList.contains(keyStr)) {
								filterList.add(keyStr);
								result.setData("INSERT_FLG", i, "Y");
								actualTotalAccount++;
							} else {
								result.setData("INSERT_FLG", i, "N");
							}
						}
					}

					parm = new TParm();
					parm.setData("INSERT", result.getData());

					// 执行保存操作
					saveResult = TIOM_AppServer.executeAction(className,
							methodName, parm);

					if (saveResult.getErrCode() < 0) {
						err(saveResult.getErrCode() + " "
								+ saveResult.getErrText());
						this.messageBox("E0001");
						return;
					}

					// 如果执行成功
					if (saveResult.getBoolean("SUCCESS_FLG")) {
//						 this.messageBox("执行成功");
					} else {
						this.messageBox("执行失败:" + saveResult.getErrText());
						return;
					}
					
					if (showParm.getCount() > 0) {
						showParm.addParm(result);
					} else {
						showParm = result;
					}
					
					table.setParmValue(showParm);
					this.setValue("TOTAL_COUNT", this.getValueInt("TOTAL_COUNT")
							+ count);
				}
				
				// 执行完毕后将开始时间更新为本次截止时间
				date = DateUtils.addMinutes(StringTool.getDate(endDate,
						"yyyy/MM/dd HH:mm"), TypeTool.getInt(1));
				startDate = StringTool.getString(date, "yyyy/MM/dd HH:mm");
			}
			
			if (table.getParmValue() == null
					|| table.getParmValue().getCount() <= 0) {
				this.messageBox("查无数据");
				table.setParmValue(new TParm());
				this.setValue("TOTAL_COUNT", 0);
			} else {
				this.setValue("ACTUAL_TOTAL_COUNT", actualTotalAccount);
				this.messageBox("提取完毕");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
    /**
     * 清空方法
     */
	public void onCLear() {
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", now);
		this.setValue("E_DATE", now);
		this.getTComboBox("QUERY_TYPE").setSelectedIndex(0);
		this.clearValue("TOTAL_COUNT;ACTUAL_TOTAL_COUNT");
		table.setParmValue(new TParm());
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
     * 得到TComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getTComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
    
	/**
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
