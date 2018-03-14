package com.javahis.ui.sta;

import com.dongyang.control.*;
import jdo.sta.STAOPDLogTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import jdo.sta.STASQLTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

import jdo.sta.STATool;
import jdo.sta.STADeptListTool;
import jdo.sta.STAOpdDailyTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: 门急诊日志
 * </p>
 * 
 * <p>
 * Description: 门急诊日志
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-7-7
 * @version 1.0
 */
public class STAOPDLogControl extends TControl {
	private boolean STA_CONFIRM_FLG = false;// 记录是否提交
	private String DATA_StaDate = "";// 记录目前显示的数据的主键(日期)

	/**
	 * 初始化
	 */
    public void onInit() {
        super.init();
        this.initData();// 初始化数据
        this.onQuery();
    }

	/**
	 * 初始化数据
	 */
    public void initData() {
        // 获取昨天的日期
        Timestamp yestaday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("STA_DATE", yestaday);
        STA_CONFIRM_FLG = false;
        this.setValue("Submit", false);
    }
	

    /**
     * table数据绑定
     * @param STA_DATE
     * @param regionCode
     */
    public void tableBind(String STA_DATE, String regionCode) {
        TTable table = (TTable) this.getComponent("Table");
        // ========pangben modify 20110519 start 添加区域参数
        String sql = STASQLTool.getInstance().getSTAOPDLog(STA_DATE, regionCode);
        // ========pangben modify 20110519 stop
        TParm parm = new TParm();
        TParm dataParm = new TParm(this.getDBTool().select(sql));
        int OUTP_NUM_count = 0;// 门诊合计
        int ERD_NUM_count = 0;// 急诊合计
        int HRM_NUM_count = 0;// 健检合计
        int OTHER_NUM_count = 0;// 其他合计
        int GET_TIMES_count = 0;// 抢救合计
        int PROF_DR_count = 0;// 专家诊合计
        int COMM_DR_count = 0;// 普通诊合计
        int DR_HOURS_count = 0;// 工作小时合计
        int SUCCESS_TIMES_count = 0;// 抢救成功次数合计
        int OBS_NUM_count = 0;// 留观人次合计
        int ERD_DIED_NUM_count = 0;// 急诊死亡人数合计
        int OBS_DIED_NUM_count = 0;// 留观死亡人数合计
        int OPE_NUM_count = 0;// 手术人数合计
        int FIRST_NUM_count = 0;// 初诊人次合计
        int FURTHER_NUM_count = 0;// 复诊人次合计
        int APPT_NUM_count = 0;// 预约人次合计
        int ZR_DR_NUM_count = 0;// 主任副主任人次合计
        int ZZ_DR_NUM_count = 0;// 主治人次合计
        int ZY_DR_NUM_count = 0;// 住院人次合计
        int ZX_DR_NUM_count = 0;// 进修人次合计
        for (int i = 0; i < dataParm.getCount(); i++) {
            parm.addData("DEPT_CODE", dataParm.getValue("DEPT_CODE", i));
            parm.addData("OUTP_NUM", dataParm.getValue("OUTP_NUM", i));
            parm.addData("ERD_NUM", dataParm.getValue("ERD_NUM", i));
            parm.addData("HRM_NUM", dataParm.getValue("HRM_NUM", i));
            parm.addData("OTHER_NUM", dataParm.getValue("OTHER_NUM", i));
            parm.addData("GET_TIMES", dataParm.getValue("GET_TIMES", i));
            parm.addData("PROF_DR", dataParm.getValue("PROF_DR", i));
            parm.addData("COMM_DR", dataParm.getValue("COMM_DR", i));
            parm.addData("DR_HOURS", dataParm.getValue("DR_HOURS", i));
            parm.addData("SUCCESS_TIMES", dataParm.getValue("SUCCESS_TIMES", i));
            parm.addData("OBS_NUM", dataParm.getValue("OBS_NUM", i));
            parm.addData("ERD_DIED_NUM", dataParm.getValue("ERD_DIED_NUM", i));
            parm.addData("OBS_DIED_NUM", dataParm.getValue("OBS_DIED_NUM", i));
            parm.addData("OPE_NUM", dataParm.getValue("OPE_NUM", i));
            parm.addData("FIRST_NUM", dataParm.getValue("FIRST_NUM", i));
            parm.addData("FURTHER_NUM", dataParm.getValue("FURTHER_NUM", i));
            parm.addData("APPT_NUM", dataParm.getValue("APPT_NUM", i));
            parm.addData("ZR_DR_NUM", dataParm.getValue("ZR_DR_NUM", i));
            parm.addData("ZZ_DR_NUM", dataParm.getValue("ZZ_DR_NUM", i));
            parm.addData("ZY_DR_NUM", dataParm.getValue("ZY_DR_NUM", i));
            parm.addData("ZX_DR_NUM", dataParm.getValue("ZX_DR_NUM", i));
            OUTP_NUM_count += dataParm.getInt("OUTP_NUM", i);
            ERD_NUM_count += dataParm.getInt("ERD_NUM", i);
            HRM_NUM_count += dataParm.getInt("HRM_NUM", i);
            OTHER_NUM_count += dataParm.getInt("OTHER_NUM", i);
            GET_TIMES_count += dataParm.getInt("GET_TIMES", i);
            PROF_DR_count += dataParm.getInt("PROF_DR", i);
            COMM_DR_count += dataParm.getInt("COMM_DR", i);
            DR_HOURS_count += dataParm.getInt("DR_HOURS", i);
            SUCCESS_TIMES_count += dataParm.getInt("SUCCESS_TIMES", i);
            OBS_NUM_count += dataParm.getInt("OBS_NUM", i);
            ERD_DIED_NUM_count += dataParm.getInt("ERD_DIED_NUM", i);
            OBS_DIED_NUM_count += dataParm.getInt("OBS_DIED_NUM", i);
            OPE_NUM_count += dataParm.getInt("OPE_NUM", i);
            FIRST_NUM_count += dataParm.getInt("FIRST_NUM", i);
            FURTHER_NUM_count += dataParm.getInt("FURTHER_NUM", i);
            APPT_NUM_count += dataParm.getInt("APPT_NUM", i);
            ZR_DR_NUM_count += dataParm.getInt("ZR_DR_NUM", i);
            ZZ_DR_NUM_count += dataParm.getInt("ZZ_DR_NUM", i);
            ZY_DR_NUM_count += dataParm.getInt("ZY_DR_NUM", i);
            ZX_DR_NUM_count += dataParm.getInt("ZX_DR_NUM", i);
        }
        parm.addData("DEPT_CODE", "合计：");
        parm.addData("OUTP_NUM", OUTP_NUM_count);
        parm.addData("ERD_NUM", ERD_NUM_count);
        parm.addData("HRM_NUM", HRM_NUM_count);
        parm.addData("OTHER_NUM", OTHER_NUM_count);
        parm.addData("GET_TIMES", GET_TIMES_count);
        parm.addData("PROF_DR", PROF_DR_count);
        parm.addData("COMM_DR", COMM_DR_count);
        parm.addData("DR_HOURS", DR_HOURS_count);
        parm.addData("SUCCESS_TIMES", SUCCESS_TIMES_count);
        parm.addData("OBS_NUM", OBS_NUM_count);
        parm.addData("ERD_DIED_NUM", ERD_DIED_NUM_count);
        parm.addData("OBS_DIED_NUM", OBS_DIED_NUM_count);
        parm.addData("OPE_NUM", OPE_NUM_count);
        parm.addData("FIRST_NUM", FIRST_NUM_count);
        parm.addData("FURTHER_NUM", FURTHER_NUM_count);
        parm.addData("APPT_NUM", APPT_NUM_count);
        parm.addData("ZR_DR_NUM", ZR_DR_NUM_count);
        parm.addData("ZZ_DR_NUM", ZZ_DR_NUM_count);
        parm.addData("ZY_DR_NUM", ZY_DR_NUM_count);
        parm.addData("ZX_DR_NUM", ZX_DR_NUM_count);
        parm.setCount(parm.getCount("OUTP_NUM"));
        table.setParmValue(parm);
        // table.setSQL(sql);
        // table.retrieve();
        // table.setDSValue();
        DATA_StaDate = STA_DATE;
    }



	/**
	 * 保存
	 */
	public void onSave() {
	    // 是否提交
        boolean submit = ((TCheckBox) this.getComponent("Submit")).isSelected();
        String STADATE = this.getText("STA_DATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("请选择日期");
            return;
        }
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE,
                                                       Operator.getRegion());
        if (submit && reFlg == 2) {//modify by wanglong 20140212
            // if (STA_CONFIRM_FLG) {
            this.messageBox_("数据已经提交，不能修改");
            return;
        }
		TTable table = (TTable) this.getComponent("Table");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		// TDataStore ds = table.getDataStore();
		String optUser = Operator.getID();
		String optIp = Operator.getIP();
		// 获取服务器时间
		Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
		String message = "修改成功！";// 提示语
		if (submit)
			message = "提交成功！";
		TParm parm = new TParm();
		TParm result = new TParm();
		for (int i = 0; i < tableParm.getCount("DEPT_CODE")-1; i++) {
			parm.setData("STA_DATE", STADATE);
			parm.setData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
			parm.setData("OUTP_NUM", tableParm.getInt("OUTP_NUM", i));
			parm.setData("ERD_NUM", tableParm.getInt("ERD_NUM", i));
			parm.setData("HRM_NUM", tableParm.getInt("HRM_NUM", i));
			parm.setData("OTHER_NUM", tableParm.getInt("OTHER_NUM", i));
			parm.setData("GET_TIMES", tableParm.getInt("GET_TIMES", i));
			parm.setData("PROF_DR", tableParm.getInt("PROF_DR", i));
			parm.setData("COMM_DR", tableParm.getInt("COMM_DR", i));
			parm.setData("DR_HOURS", tableParm.getInt("DR_HOURS", i));
			parm.setData("SUCCESS_TIMES", tableParm.getInt("SUCCESS_TIMES", i));
			parm.setData("OBS_NUM", tableParm.getInt("OBS_NUM", i));
			parm.setData("ERD_DIED_NUM", tableParm.getInt("ERD_DIED_NUM", i));
			parm.setData("OBS_DIED_NUM", tableParm.getInt("OBS_DIED_NUM", i));
			parm.setData("OPE_NUM", tableParm.getInt("OPE_NUM", i));
			parm.setData("FIRST_NUM", tableParm.getInt("FIRST_NUM", i));
			parm.setData("FURTHER_NUM", tableParm.getInt("FURTHER_NUM", i));
			parm.setData("APPT_NUM", tableParm.getInt("APPT_NUM", i));
			parm.setData("ZR_DR_NUM", tableParm.getInt("ZR_DR_NUM", i));
			parm.setData("ZZ_DR_NUM", tableParm.getInt("ZZ_DR_NUM", i));
			parm.setData("ZY_DR_NUM", tableParm.getInt("ZY_DR_NUM", i));
			parm.setData("ZX_DR_NUM", tableParm.getInt("ZX_DR_NUM", i));
			// 判断是否提交
			if (submit) {
				parm.setData("CONFIRM_FLG", "Y");
				parm.setData("CONFIRM_USER", optUser);
				parm.setData("CONFIRM_DATE", CONFIRM_DATE);
			} else {
				parm.setData("CONFIRM_FLG", "N");
				parm.setData("CONFIRM_USER", "");
				parm.setData("CONFIRM_DATE", "");
			}
			parm.setData("OPT_USER", optUser);
			parm.setData("OPT_TERM", optIp);
			parm.setData("OPT_DATE", CONFIRM_DATE);
			// ==============pangben modify 20110519 start
			parm.setData("REGION_CODE", Operator.getRegion());
			// ==============pangben modify 20110519 stop
			result = STAOPDLogTool.getInstance().update_STA_OPD_DAILY(parm);
		}
		if (result.getErrCode() < 0) {
			this.messageBox_("操作失败！");
		} else {
			this.messageBox_(message);
			if (submit)
				STA_CONFIRM_FLG = true;
		}

	}

	/**
	 * 导入指定日期的门诊信息
	 */
	public void onGenerate() {
		if (STA_CONFIRM_FLG) {
			this.messageBox_("数据已经提交，不可重新导入!");
			return;
		}
		String STADATE = this.getText("STA_DATE").replace("/", "");
		if (STADATE.trim().length() <= 0) {
			this.messageBox_("请选择日期！");
			return;
		}
		// 获取选定日期的前一天的日期
		Timestamp time = StringTool.rollDate(
				StringTool.getTimestamp(STADATE, "yyyyMMdd"), -1);
		String lastDay = StringTool.getString(time, "yyyyMMdd");
		TParm checkLastDay = new TParm();
		checkLastDay.setData("STA_DATE", lastDay);
		// ==========pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			checkLastDay.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110519 stop
		// 获取选定日期的前一天的数据
		TParm check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(
				checkLastDay);
		if (check.getCount("STA_DATE") <= 0) { // 如果前一天数据不存在，不允许导入，会影响数据准确度
			switch (this.messageBox("提示信息",
					StringTool.getString(time, "yyyy年MM月dd日")
							+ "的数据不存在\n导入新数据会影响准确度\n是否导入？", this.YES_NO_OPTION)) {
			case 0: // 生成
				break;
			case 1: // 不生成
				return;
			}
		}
		// 审核要导入的数据是否已经存在
		TParm checkDay = new TParm();
		checkDay.setData("STA_DATE", STADATE);
		// ==========pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			checkDay.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110519 stop
		check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(checkDay);
		if (check.getCount("STA_DATE") > 0) { // 如果数据存在，询问是否导入
			switch (this
					.messageBox("提示信息", "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
			case 0: // 生成
				break;
			case 1: // 不生成
				return;
			}
		}
		TParm sql = new TParm();
		sql.setData("ADMDATE", STADATE);
		sql.setData("OPT_USER", Operator.getID());
		sql.setData("OPT_TERM", Operator.getIP());
		// =================pangben modify 20110519 start 添加区域
		sql.setData("REGION_CODE", Operator.getRegion());
		// =================pangben modify 20110519 stop
		TParm dept = new TParm();
		// =========pangben modify 20110519 start 添加区域参数
		dept = STADeptListTool.getInstance()
				.selectOE_DEPT(Operator.getRegion());
		// =========pangben modify 20110519 stop
		if (dept.getCount() != 0) {
			TParm parm = new TParm();
			parm.setData("SQL", sql.getData());
			parm.setData("DEPT", dept.getData());
			TParm result = TIOM_AppServer.executeAction(
					"action.sta.STADailyAction", "insertSTA_OPD_DAILY", parm);
			if (result.getErrCode() < 0) {
				System.out.println("" + result);
				return;
			}
			this.messageBox_("门诊中间档导入成功！");
			// =========pangben modify 20110519
		} else
			this.messageBox_("没有需要导入的门诊中间档数据！");
		initData();
	}


    /**
     * 查询
     */
    public void onQuery() {
        String STADATE = this.getText("STA_DATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("请选择日期");
            return;
        }
        // ===========pangben modify 20110519 添加区域参数
        tableBind(STADATE, Operator.getRegion());
        // ===========pangben modify 20110519 添加区域参数
        STA_CONFIRM_FLG = false;
        this.setValue("Submit", false);
        // 检核数据状态
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE,
                                                       Operator.getRegion());
        if (reFlg == 2) {// 已经提交
            STA_CONFIRM_FLG = true;
            this.setValue("Submit", true);
        }
    }
    
	/**
	 * 打印
	 */
	public void onPrint() {
		if (DATA_StaDate.trim().length() <= 0) {// 没有选定统计日期
			return;
		}
		// =======pangben modify 20110519 start 添加区域参数
		TParm data = STAOPDLogTool.getInstance().getPrintData(DATA_StaDate,
				Operator.getRegion());
		// =======pangben modify 20110519 stop
		data.setCount(data.getCount("STA_DATE"));// 设置行数
		data.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		data.addData("SYSTEM", "COLUMNS", "OUTP_NUM");
		data.addData("SYSTEM", "COLUMNS", "ERD_NUM");
		data.addData("SYSTEM", "COLUMNS", "HRM_NUM");
		data.addData("SYSTEM", "COLUMNS", "OTHER_NUM");
		data.addData("SYSTEM", "COLUMNS", "GET_TIMES");
		data.addData("SYSTEM", "COLUMNS", "PROF_DR");
		data.addData("SYSTEM", "COLUMNS", "COMM_DR");
		data.addData("SYSTEM", "COLUMNS", "DR_HOURS");
		data.addData("SYSTEM", "COLUMNS", "SUCCESS_TIMES");
		data.addData("SYSTEM", "COLUMNS", "OBS_NUM");
		data.addData("SYSTEM", "COLUMNS", "ERD_DIED_NUM");
		data.addData("SYSTEM", "COLUMNS", "OBS_DIED_NUM");
		data.addData("SYSTEM", "COLUMNS", "OPE_NUM");
		data.addData("SYSTEM", "COLUMNS", "FIRST_NUM");
		data.addData("SYSTEM", "COLUMNS", "FURTHER_NUM");
		TParm printParm = new TParm();
		printParm.setData("Date", this.getText("STA_DATE").replaceAll("/", "-"));
		printParm.setData("T1", data.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STAOPDLog.jhw",
				printParm);
	}


    
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("STA_DATE;Submit");
        TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
        table.resetModify();
        this.initData();
    }
    

    /**
     * 单元格值改变事件
     */
    public void onChangeTableValue(){
        TTable table = (TTable) this.getComponent("Table");
        table.acceptText();
        TParm parm = new TParm();
        TParm dataParm = table.getParmValue();
        int OUTP_NUM_count = 0;// 门诊合计
        int ERD_NUM_count = 0;// 急诊合计
        int HRM_NUM_count = 0;// 健检合计
        int OTHER_NUM_count = 0;// 其他合计
        int GET_TIMES_count = 0;// 抢救合计
        int PROF_DR_count = 0;// 专家诊合计
        int COMM_DR_count = 0;// 普通诊合计
        int DR_HOURS_count = 0;// 工作小时合计
        int SUCCESS_TIMES_count = 0;// 抢救成功次数合计
        int OBS_NUM_count = 0;// 留观人次合计
        int ERD_DIED_NUM_count = 0;// 急诊死亡人数合计
        int OBS_DIED_NUM_count = 0;// 留观死亡人数合计
        int OPE_NUM_count = 0;// 手术人数合计
        int FIRST_NUM_count = 0;// 初诊人次合计
        int FURTHER_NUM_count = 0;// 复诊人次合计
        int APPT_NUM_count = 0;// 预约人次合计
        int ZR_DR_NUM_count = 0;// 主任副主任人次合计
        int ZZ_DR_NUM_count = 0;// 主治人次合计
        int ZY_DR_NUM_count = 0;// 住院人次合计
        int ZX_DR_NUM_count = 0;// 进修人次合计
        for (int i = 0; i < dataParm.getCount()-1; i++) {
            parm.addData("DEPT_CODE", dataParm.getValue("DEPT_CODE", i));
            parm.addData("OUTP_NUM", dataParm.getValue("OUTP_NUM", i));
            parm.addData("ERD_NUM", dataParm.getValue("ERD_NUM", i));
            parm.addData("HRM_NUM", dataParm.getValue("HRM_NUM", i));
            parm.addData("OTHER_NUM", dataParm.getValue("OTHER_NUM", i));
            parm.addData("GET_TIMES", dataParm.getValue("GET_TIMES", i));
            parm.addData("PROF_DR", dataParm.getValue("PROF_DR", i));
            parm.addData("COMM_DR", dataParm.getValue("COMM_DR", i));
            parm.addData("DR_HOURS", dataParm.getValue("DR_HOURS", i));
            parm.addData("SUCCESS_TIMES", dataParm.getValue("SUCCESS_TIMES", i));
            parm.addData("OBS_NUM", dataParm.getValue("OBS_NUM", i));
            parm.addData("ERD_DIED_NUM", dataParm.getValue("ERD_DIED_NUM", i));
            parm.addData("OBS_DIED_NUM", dataParm.getValue("OBS_DIED_NUM", i));
            parm.addData("OPE_NUM", dataParm.getValue("OPE_NUM", i));
            parm.addData("FIRST_NUM", dataParm.getValue("FIRST_NUM", i));
            parm.addData("FURTHER_NUM", dataParm.getValue("FURTHER_NUM", i));
            parm.addData("APPT_NUM", dataParm.getValue("APPT_NUM", i));
            parm.addData("ZR_DR_NUM", dataParm.getValue("ZR_DR_NUM", i));
            parm.addData("ZZ_DR_NUM", dataParm.getValue("ZZ_DR_NUM", i));
            parm.addData("ZY_DR_NUM", dataParm.getValue("ZY_DR_NUM", i));
            parm.addData("ZX_DR_NUM", dataParm.getValue("ZX_DR_NUM", i));
            OUTP_NUM_count += dataParm.getInt("OUTP_NUM", i);
            ERD_NUM_count += dataParm.getInt("ERD_NUM", i);
            HRM_NUM_count += dataParm.getInt("HRM_NUM", i);
            OTHER_NUM_count += dataParm.getInt("OTHER_NUM", i);
            GET_TIMES_count += dataParm.getInt("GET_TIMES", i);
            PROF_DR_count += dataParm.getInt("PROF_DR", i);
            COMM_DR_count += dataParm.getInt("COMM_DR", i);
            DR_HOURS_count += dataParm.getInt("DR_HOURS", i);
            SUCCESS_TIMES_count += dataParm.getInt("SUCCESS_TIMES", i);
            OBS_NUM_count += dataParm.getInt("OBS_NUM", i);
            ERD_DIED_NUM_count += dataParm.getInt("ERD_DIED_NUM", i);
            OBS_DIED_NUM_count += dataParm.getInt("OBS_DIED_NUM", i);
            OPE_NUM_count += dataParm.getInt("OPE_NUM", i);
            FIRST_NUM_count += dataParm.getInt("FIRST_NUM", i);
            FURTHER_NUM_count += dataParm.getInt("FURTHER_NUM", i);
            APPT_NUM_count += dataParm.getInt("APPT_NUM", i);
            ZR_DR_NUM_count += dataParm.getInt("ZR_DR_NUM", i);
            ZZ_DR_NUM_count += dataParm.getInt("ZZ_DR_NUM", i);
            ZY_DR_NUM_count += dataParm.getInt("ZY_DR_NUM", i);
            ZX_DR_NUM_count += dataParm.getInt("ZX_DR_NUM", i);
        }
        parm.addData("DEPT_CODE", "合计：");
        parm.addData("OUTP_NUM", OUTP_NUM_count);
        parm.addData("ERD_NUM", ERD_NUM_count);
        parm.addData("HRM_NUM", HRM_NUM_count);
        parm.addData("OTHER_NUM", OTHER_NUM_count);
        parm.addData("GET_TIMES", GET_TIMES_count);
        parm.addData("PROF_DR", PROF_DR_count);
        parm.addData("COMM_DR", COMM_DR_count);
        parm.addData("DR_HOURS", DR_HOURS_count);
        parm.addData("SUCCESS_TIMES", SUCCESS_TIMES_count);
        parm.addData("OBS_NUM", OBS_NUM_count);
        parm.addData("ERD_DIED_NUM", ERD_DIED_NUM_count);
        parm.addData("OBS_DIED_NUM", OBS_DIED_NUM_count);
        parm.addData("OPE_NUM", OPE_NUM_count);
        parm.addData("FIRST_NUM", FIRST_NUM_count);
        parm.addData("FURTHER_NUM", FURTHER_NUM_count);
        parm.addData("APPT_NUM", APPT_NUM_count);
        parm.addData("ZR_DR_NUM", ZR_DR_NUM_count);
        parm.addData("ZZ_DR_NUM", ZZ_DR_NUM_count);
        parm.addData("ZY_DR_NUM", ZY_DR_NUM_count);
        parm.addData("ZX_DR_NUM", ZX_DR_NUM_count);;
        parm.setCount(parm.getCount("OUTP_NUM"));
        table.setParmValue(parm);
    }
    
    /**
     * getDBTool 数据库工具实例
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
