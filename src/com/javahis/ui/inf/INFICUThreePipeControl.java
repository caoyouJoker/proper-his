package com.javahis.ui.inf;

import java.sql.Timestamp;

import jdo.inf.INFICUThreePipeTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:三管ICU统计报表
 * </p>
 * 
 * <p>
 * Description:三管ICU统计报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2017-3-27
 * @version JavaHis 1.0
 */
public class INFICUThreePipeControl extends TControl {

	private String startDate;

	private String endDate;

	private String dept;

	@Override
	public void onInit() {
		super.onInit();
		initPage();
	}

	private void initPage() {
		this.clearValue("DEPT_CODE;MR_NO;PAT_NAME;START_DATE;END_DATE");
		Timestamp time = SystemTool.getInstance().getDate();
		Timestamp endDate = Timestamp.valueOf(time.toString().substring(0, 10)
				+ " 23:59:59");
		Timestamp startDate = Timestamp.valueOf(StringTool.rollDate(time, -7)
				.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
		this.callFunction("UI|TABLE|setParmValue", new TParm());
	}

	/**
	 * 病案号事件
	 */
	public void onMrNo() {

		String mrno = this.getValueString("MR_NO");

		if (StringUtils.isEmpty(mrno)) {
			return;
		}
		mrno = PatTool.getInstance().checkMrno(mrno);
		this.setValue("MR_NO", mrno);

		String patName = PatTool.getInstance().getNameForMrno(mrno);

		this.setValue("PAT_NAME", patName);

	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm queryParm = new TParm();
		startDate = this.getValueString("START_DATE");
		if (!StringUtils.isEmpty(startDate)) {
			queryParm.setData("START_DATE", startDate.substring(0, 10)
					.replaceAll("-", "") + "000000");
			startDate = startDate.substring(0, 10).replaceAll("-", "/");
		} else {
			this.messageBox_("请输入开始时间");
			return;
		}

		endDate = this.getValueString("END_DATE");
		if (!StringUtils.isEmpty(endDate)) {
			queryParm.setData("END_DATE",
					endDate.substring(0, 10).replaceAll("-", "") + "235959");
			endDate = endDate.substring(0, 10).replaceAll("-", "/");
		} else {
			this.messageBox_("请输入结束时间");
			return;
		}

		String mrno = this.getValueString("MR_NO");
		if (!StringUtils.isEmpty(mrno)) {
			queryParm.setData("MR_NO", mrno);
		}

		dept = this.getValueString("DEPT_CODE");
		if (!StringUtils.isEmpty(dept)) {
			queryParm.setData("DEPT_CODE", dept);
			dept = this.getText("DEPT_CODE");
		} else {
			dept = "全部";
		}
		
		TParm parm = INFICUThreePipeTool.getInstance().getICUThreePipe(queryParm);
		if(parm == null || parm.getErrCode() < 0) {
			this.messageBox_("查询失败");
			this.getTTable("TABLE").setParmValue(new TParm());
			return;
		}
		if(parm.getCount("MR_NO") <= 0) {
			this.messageBox_("查无数据");
			this.getTTable("TABLE").setParmValue(new TParm());
			return;
		}
		
		String temp = "";
		
		Timestamp outTubAir = null;
		Timestamp outTubUreter = null;
		Timestamp outtubVein = null;
		
		for(int i = 0; i < parm.getCount("MR_NO"); i++) {
			//1.年龄处理
			parm.setData("BIRTH_DATE", i, StringUtil.showAge(parm.getTimestamp("BIRTH_DATE", i), parm.getTimestamp("IN_DATE", i))) ;
			
			//2.插管时间 （深静脉  + 15分钟）
			if(parm.getTimestamp("IN_TUBVEIN_TIME", i) != null) {
				parm.setData("IN_TUBVEIN_TIME", i, StringUtil.getNextTime(parm.getTimestamp("IN_TUBVEIN_TIME", i), 15L * 60L * 1000L));
			}
			
			//3.拔管时间处理 (从 yyyyMMddHHMMSS转化为Timestamp yyyy/MM/dd hh:ss)
			/*temp = parm.getValue("OUT_TUBAIR_TIME", i);
			if(StringUtils.isEmpty(temp)) {
				outTubAir = null;
			} else {
				outTubAir =Timestamp.valueOf(temp.substring(0, 4) + "-" + temp.substring(4, 6) + "-" + temp.substring(6, 8) + " " + temp.substring(8, 10) + ":" + temp.substring(10, 12) + ":00");
			}
			
			temp = parm.getValue("OUT_TUBURETER_TIME", i);
			if(StringUtils.isEmpty(temp)) {
				outTubUreter = null;
			} else {
				outTubUreter = Timestamp.valueOf(temp.substring(0, 4) + "-" + temp.substring(4, 6) + "-" + temp.substring(6, 8) + " " + temp.substring(8, 10) + ":" + temp.substring(10, 12) + ":00");
			}
			
			temp = parm.getValue("OUT_TUBVEIN_TIME", i);
			if(StringUtils.isEmpty(temp)) {
				outtubVein = null;
			} else {
				outtubVein = Timestamp.valueOf(temp.substring(0, 4) + "-" + temp.substring(4, 6) + "-" + temp.substring(6, 8) + " " + temp.substring(8, 10) + ":" + temp.substring(10, 12) + ":00");
			}
			
			parm.setData("OUT_TUBAIR_TIME", i, outTubAir);
			parm.setData("OUT_TUBURETER_TIME", i, outTubUreter);
			parm.setData("OUT_TUBVEIN_TIME", i, outtubVein);*/
			
			outTubAir = parm.getTimestamp("OUT_TUBAIR_TIME", i);
			outTubUreter = parm.getTimestamp("OUT_TUBURETER_TIME", i);
			outtubVein = parm.getTimestamp("OUT_TUBVEIN_TIME", i);
			
			//4.处理带管时间(拔管时间 - 插管时间 格式  XX分XX秒)
			parm.setData("CA_TUBAIR_TIME", i, StringUtil.getDiffInMinAndSec(parm.getTimestamp("IN_TUBAIR_TIME", i), outTubAir));
			parm.setData("CA_TUBVEIN_TIME", i, StringUtil.getDiffInMinAndSec(parm.getTimestamp("IN_TUBVEIN_TIME", i), outtubVein));
			
			//5.处理拔管后时间(出ICU时间 - 拔管 格式 XX分XX秒)
			parm.setData("PULL_TUBAIR_TIME", i , StringUtil.getDiffInMinAndSec(outTubAir, parm.getTimestamp("OUT_ICU_TIME", i)));
			parm.setData("PULL_TUBURETER_TIME", i , StringUtil.getDiffInMinAndSec(outTubUreter, parm.getTimestamp("OUT_ICU_TIME", i)));
			parm.setData("PULL_TUBVEIN_TIME", i , StringUtil.getDiffInMinAndSec(outtubVein, parm.getTimestamp("OUT_ICU_TIME", i)));
			
			//6.添加序号
			parm.setData("SEQ", i, (i + 1));
			
		}
		
		this.getTTable("TABLE").setParmValue(parm);
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = this.getTTable("TABLE");
		
		if(table.getRowCount() <= 0) {
			this.messageBox_("暂无打印数据");
			return;
		}
		
		TParm data = new TParm();
		
		//表头数据
		data.setData("TITLE", "TEXT", "ICU“三管”统计报表");
		data.setData("STAT_DATE", "TEXT", "统计时间：" + startDate + " ～ " + endDate);
		data.setData("DEPT", "TEXT", "统计科室：" + dept);
		
		
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
		for(int i = 0; i < tableParm.getCount("MR_NO"); i++) {
			parm.addData("SEQ", tableParm.getData("SEQ", i)); //序号
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("SEX_CODE", tableParm.getData("SEX_CODE", i));
			parm.addData("BIRTH_DATE", tableParm.getData("BIRTH_DATE", i));
			parm.addData("IN_OP_TIME", tableParm.getData("IN_OP_TIME", i));
			parm.addData("IN_TUBAIR_TIME", tableParm.getData("IN_TUBAIR_TIME", i));
			parm.addData("IN_TUBURETER_TIME", tableParm.getData("IN_TUBURETER_TIME", i));
			parm.addData("IN_TUBVEIN_TIME", tableParm.getData("IN_TUBVEIN_TIME", i));
			parm.addData("IN_ICU_TIME", tableParm.getData("IN_ICU_TIME", i));
			parm.addData("ACC_TUBAIR_TIME", tableParm.getData("ACC_TUBAIR_TIME", i));
			parm.addData("ACC_TUBURETER_TIME", tableParm.getData("ACC_TUBURETER_TIME", i));
			parm.addData("ACC_TUBVEIN_TIME", tableParm.getData("ACC_TUBVEIN_TIME", i));
			parm.addData("SIN_TUBAIR_TIME", tableParm.getData("SIN_TUBAIR_TIME", i));
			parm.addData("OUT_TUBAIR_TIME", tableParm.getData("OUT_TUBAIR_TIME", i));
			parm.addData("OUT_TUBURETER_TIME", tableParm.getData("OUT_TUBURETER_TIME", i));
			parm.addData("OUT_TUBVEIN_TIME", tableParm.getData("OUT_TUBVEIN_TIME", i));
			parm.addData("OUT_ICU_TIME", tableParm.getData("OUT_ICU_TIME", i));
			parm.addData("CA_TUBAIR_TIME", tableParm.getData("CA_TUBAIR_TIME", i));
			parm.addData("CA_TUBURETER_TIME", tableParm.getData("CA_TUBURETER_TIME", i));
			parm.addData("CA_TUBVEIN_TIME", tableParm.getData("CA_TUBVEIN_TIME", i));
			parm.addData("PULL_TUBAIR_TIME", tableParm.getData("PULL_TUBAIR_TIME", i));
			parm.addData("PULL_TUBURETER_TIME", tableParm.getData("PULL_TUBURETER_TIME", i));
			parm.addData("PULL_TUBVEIN_TIME", tableParm.getData("PULL_TUBVEIN_TIME", i));
			parm.addData("IS_MAC_INFECT", tableParm.getData("IS_MAC_INFECT", i));
			parm.addData("IS_YSNL_INFECT", tableParm.getData("IS_YSNL_INFECT", i));
			parm.addData("IS_DBGNL_INFECT", tableParm.getData("IS_DBGNL_INFECT", i));
			parm.addData("IS_DBGWZZ_INFECT", tableParm.getData("IS_DBGWZZ_INFECT", i));
			parm.addData("IS_XLGRLC_INFECT", tableParm.getData("IS_XLGRLC_INFECT", i));
			parm.addData("IS_XLGRSY_INFECT", tableParm.getData("IS_XLGRSY_INFECT", i));
			parm.addData("IS_XFY_INFECT", tableParm.getData("IS_XFY_INFECT", i));
			
		}
		
		parm.setCount(parm.getCount("MR_NO"));
		
		parm.addData("SYSTEM","COLUMNS","SEQ");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","SEX_CODE");
		parm.addData("SYSTEM","COLUMNS","BIRTH_DATE");
		parm.addData("SYSTEM","COLUMNS","IN_OP_TIME");
		parm.addData("SYSTEM","COLUMNS","IN_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","IN_TUBURETER_TIME");
		parm.addData("SYSTEM","COLUMNS","IN_TUBVEIN_TIME");
		parm.addData("SYSTEM","COLUMNS","IN_ICU_TIME");
		parm.addData("SYSTEM","COLUMNS","ACC_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","ACC_TUBURETER_TIME");
		parm.addData("SYSTEM","COLUMNS","ACC_TUBVEIN_TIME");
		parm.addData("SYSTEM","COLUMNS","SIN_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","OUT_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","OUT_TUBURETER_TIME");
		parm.addData("SYSTEM","COLUMNS","OUT_TUBVEIN_TIME");
		parm.addData("SYSTEM","COLUMNS","OUT_ICU_TIME");
		parm.addData("SYSTEM","COLUMNS","CA_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","CA_TUBURETER_TIME");
		parm.addData("SYSTEM","COLUMNS","CA_TUBVEIN_TIME");
		parm.addData("SYSTEM","COLUMNS","PULL_TUBAIR_TIME");
		parm.addData("SYSTEM","COLUMNS","PULL_TUBURETER_TIME");
		parm.addData("SYSTEM","COLUMNS","PULL_TUBVEIN_TIME");
		parm.addData("SYSTEM","COLUMNS","IS_MAC_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_YSNL_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_DBGNL_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_DBGWZZ_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_XLGRLC_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_XLGRSY_INFECT");
		parm.addData("SYSTEM","COLUMNS","IS_XFY_INFECT");
		
		
		data.setData("TABLE", parm.getData());
		
		data.setData("OPT_USER", "TEXT", "打印人：" + Operator.getName());
		data.setData("OPT_TIME", "TEXT", "打印时间：" + SystemTool.getInstance().getDate().toString().substring(0, 10).replaceAll("-", "/"));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\inf\\INFICUThreePipe.jhw", data);
	}

	/**
	 * 导出Excel
	 */
	public void onExcel() {
		TTable table = this.getTTable("TABLE");
		
		if(table.getRowCount() <= 0) {
			this.messageBox_("暂无导出Excel数据");
			return;
		}
		
		ExportExcelUtil.getInstance().exportExcel(table, "ICU三管统计报表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
	}
	
	/**
	 * 获取TTable
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	
	
}
