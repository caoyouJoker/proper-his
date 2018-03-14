package com.javahis.ui.inf;

import java.awt.Color;
import java.sql.Timestamp;

import jdo.inf.INFSmsTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title:传染病自动提醒
 * </p>
 * 
 * <p>
 * Description:传染病自动提醒
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
 * @author wukai 2017-4-20
 * @version JavaHis 1.0
 */
public class INFSmsEarlyWarnControl extends TControl {
	
	/**
	 * 干预措施选项
	 */
	private String IVEN_OPTABLE = "IVEN_OPTABLE";
	
	/**
	 * 传染病人列表
	 */
	private String PAT_TABLE = "PAT_TABLE";
	
	/**
	 * 信息类别 （INF_SMS -> MES_TYPE）  11：传染病预警短信
	 */
	private String MES_TYPE = "11";
	
	private String startDate;

	private String endDate;

	private String dept;
	
	@Override
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化页面控件
	 */
	private void initPage() {
		this.clearValue("DEPT_CODE;STATION_CODE;STATE;MR_NO;PAT_NAME;START_DATE;END_DATE;INTERVENT_ID");
		Timestamp time = SystemTool.getInstance().getDate();
		Timestamp endDate = Timestamp.valueOf(time.toString().substring(0, 10) + " 23:59:59");
		Timestamp startDate = Timestamp.valueOf(StringTool.rollDate(time, -7).toString().substring(0, 10)+ " 00:00:00");
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
		this.getTTable(IVEN_OPTABLE).setParmValue(new TParm());
		this.getTTable(PAT_TABLE).setParmValue(new TParm());
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		
		String temp = "";
		
		TParm queryParm = new TParm();
		
		temp = this.getValueString("START_DATE");//开始时间
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("START_DATE", temp.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14));
			this.startDate = temp.replaceAll("-", "/").substring(0, 19);
		} else {
			this.messageBox_("请输入开始时间");
			this.grabFocus("START_DATE");
			this.startDate = "";
			return;
		}
		
		temp = this.getValueString("END_DATE");//结束时间
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("END_DATE", temp.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14));
			this.endDate = temp.replaceAll("-", "/").substring(0, 19);
		} else {
			this.messageBox_("请输入结束时间");
			this.grabFocus("END_DATE");
			this.endDate = "";
			return;
		}
		
		temp = this.getValueString("DEPT_CODE");//科室
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("DEPT_CODE", temp);
			this.dept = this.getText("DEPT_CODE");
		} else {
			this.dept = "";
		}
		
		temp = this.getValueString("STATION_CODE");//病区
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("STATION_CODE", temp);
		}
		
		temp = this.getValueString("STATE");//状态
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("STATE", temp);
		}
		
		temp = this.getValueString("MR_NO");//病案号
		if(!StringUtils.isEmpty(temp)) {
			queryParm.setData("MR_NO", temp);
		}
		
		queryParm.setData("MES_TYPE", MES_TYPE); //消息类型
		
		TTable patTable = this.getTTable(PAT_TABLE);
		TParm result = INFSmsTool.getInstance().selectInfWarnData(queryParm);
		if(result == null || result.getErrCode() < 0 || result.getCount() <= 0) {
			this.messageBox_("查无数据");
			patTable.setParmValue(new TParm());
			return;
		}
		patTable.setParmValue(result);
		for (int i = 0; i < result.getCount(); i++) {
			TParm p = result.getRow(i);
			// System.out.println("time=========:"+time);
			if (!p.getValue("STATE").equals("9")) {
				/** 淡蓝色 */
				patTable.setRowTextColor(i, new Color(0, 128, 255));
//				/** 蓝色 */
//				patTable.setRowTextColor(i, new Color(0, 0, 255));
//				/** 红色 */
//				patTable.setRowTextColor(i, new Color(255, 0, 0));
				
			} else {
				patTable.setRowTextColor(i, new Color(0, 0, 0));
			}
		}
		
	}
	
	/**
	 * 保存(只有更新)
	 */
	public void onSave() {
		TTable tablePat = this.getTTable(PAT_TABLE);
		int row = tablePat.getSelectedRow();
		if(row < 0) {
			this.messageBox_("请选择一名感染病患！");
			return;
		}
		
		String inventId = this.getValueString("INTERVENT_ID");
		if(StringUtils.isEmpty(inventId)) {
			this.messageBox_("请选择一条干预措施！");
			return;
		}
		
		Timestamp time = SystemTool.getInstance().getDate();
		
		String inventOps = "";
		
		TTable tableOp = this.getTTable(IVEN_OPTABLE);
		tableOp.acceptText();
		TParm parmOp = tableOp.getParmValue();
		for(int i = 0; i < parmOp.getCount("ID"); i++) {
			if(parmOp.getBoolean("FLG", i)) { 
				inventOps = inventOps + parmOp.getValue("ID", i) + ";";
			}
		}
		if(!StringUtils.isEmpty(inventOps)) {
			inventOps = inventOps.substring(0, inventOps.lastIndexOf(";"));
		}
		
		
		TParm patParm = tablePat.getParmValue();
		
		TParm saveParm = new TParm();
		
		saveParm.setData("MESSAGE_NO", patParm.getValue("MESSAGE_NO", row));
		
		saveParm.setData("HANDLE_USER", Operator.getID()); //处理用户
		
		saveParm.setData("HANDLE_DATE", time);  //处理时间
		
		saveParm.setData("INTERVENT_ID", inventId); //干预措施
		
		saveParm.setData("INTERVENT_OPTIONS", inventOps); //干预措施选项
		
		saveParm.setData("STATE", "9"); //处理完成
		
		saveParm.setData("OPT_USER", Operator.getID());
		
		saveParm.setData("OPT_DATE", time);
		
		saveParm.setData("OPT_TERM", Operator.getIP());
		
		TParm result = INFSmsTool.getInstance().updateInfWarnData(saveParm);
		if(result == null || result.getErrCode() < 0) {
			this.messageBox_("处理失败！");
		} else {
			this.messageBox_("处理成功！");
		}
		
		this.clearValue("INTERVENT_ID");
		tableOp.setParmValue(new TParm());
		onQuery();
	}
	
	/**
	 * 导出Excel
	 */
	public void onExcel() {
		TTable table = this.getTTable(PAT_TABLE);
		if(table.getRowCount() <= 0) {
			this.messageBox_("暂无导出Excel数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "感染预警统计");
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = this.getTTable(PAT_TABLE);
		if(table.getRowCount() <= 0) {
			this.messageBox_("暂无打印数据");
			return;
		}
		
		TParm data = new TParm();
		
		//表头数据
		data.setData("TITLE", "TEXT", "传染病自动提醒统计");
		data.setData("STAT_DATE", "TEXT", "统计时间：" + startDate + " ～ " + endDate);
		data.setData("DEPT", "TEXT", "统计科室：" + dept);
		
		
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
		for(int i = 0; i < tableParm.getCount("MR_NO"); i++) {
			parm.addData("ADM_TYPE", tableParm.getData("ADM_TYPE", i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("TESTITEM_DESC", tableParm.getData("TESTITEM_DESC", i));
			parm.addData("TEST_VALUE", tableParm.getData("TEST_VALUE", i));
			parm.addData("STATE", tableParm.getData("STATE", i));
			parm.addData("DEPT_CODE", tableParm.getData("DEPT_CODE", i));
			parm.addData("BILL_DOC_CODE", tableParm.getData("BILL_DOC_CODE", i));
			parm.addData("INF_DIRECTOR", tableParm.getData("INF_DIRECTOR", i));
			parm.addData("SEND_DATE", tableParm.getData("SEND_DATE", i));
			parm.addData("HANDLE_USER", tableParm.getData("HANDLE_USER", i));
			parm.addData("HANDLE_DATE", tableParm.getData("HANDLE_DATE", i));
			
		}
		
		parm.setCount(parm.getCount("MR_NO"));
		
		parm.addData("SYSTEM","COLUMNS","ADM_TYPE");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","TESTITEM_DESC");
		parm.addData("SYSTEM","COLUMNS","TEST_VALUE");
		parm.addData("SYSTEM","COLUMNS","STATE");
		parm.addData("SYSTEM","COLUMNS","DEPT_CODE");
		parm.addData("SYSTEM","COLUMNS","BILL_DOC_CODE");
		parm.addData("SYSTEM","COLUMNS","INF_DIRECTOR");
		parm.addData("SYSTEM","COLUMNS","SEND_DATE");
		parm.addData("SYSTEM","COLUMNS","HANDLE_USER");
		parm.addData("SYSTEM","COLUMNS","HANDLE_DATE");
		
		
		data.setData("TABLE", parm.getData());
		
		data.setData("OPT_USER", "TEXT", "打印人：" + Operator.getName());
		data.setData("OPT_TIME", "TEXT", "打印时间：" + SystemTool.getInstance().getDate().toString().substring(0, 10).replaceAll("-", "/"));
		
		this.openPrintDialog("%ROOT%\\config\\prt\\inf\\INFSmsEarlyWarn.jhw", data);
		
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		
		initPage();
		
	}
	
	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		this.clearValue("PAT_NAME");
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
	 * 干预措施选择事件
	 * 自动带入干预选项措施表格数据
	 */
	public void onInventSelected() {
		String inventId = this.getValueString("INTERVENT_ID");
		
		TTable table = this.getTTable(IVEN_OPTABLE);
		
		if(StringUtils.isEmpty(inventId)) {
			table.setParmValue(new TParm());
		} else {
			
			TParm parm = INFSmsTool.getInstance().getInfInventOptions(inventId);
			table.setParmValue(parm);
			
		}
		
		
	}
	
	/**
	 * 表格点击
	 * 自动带出干预措施和干预选项和病案号
	 */
	public void onClickPatTbl() {
		
		
		TTable table = this.getTTable(PAT_TABLE);
		this.setValue("MR_NO", "");
		this.setValue("PAT_NAME", "");
		int row = table.getSelectedRow();
		if(row < 0) {
			return;
		}
		
		TParm patParm = table.getParmValue();
		
		this.setValue("MR_NO", patParm.getValue("MR_NO", row));
		this.setValue("PAT_NAME", patParm.getValue("PAT_NAME", row));
		
		String inventId = patParm.getValue("INTERVENT_ID", row);
		
		this.setValue("INTERVENT_ID", inventId);
		
		String inventOps = patParm.getValue("INTERVENT_OPTIONS", row);
		
		TTable tableOps = this.getTTable(IVEN_OPTABLE);
		
		if(StringUtils.isEmpty(inventId)) {
			tableOps.setParmValue(new TParm());
		} else {
			TParm parmOps = INFSmsTool.getInstance().getInfInventOptions(inventId);
			for(String op : inventOps.split(";")) {
				for(int i = 0; i < parmOps.getCount("ID"); i++) {
					if(op.equals(parmOps.getValue("ID", i))) {
						parmOps.setData("FLG", i, "Y");
						break;
					}
				}
			}
			tableOps.setParmValue(parmOps);
			
		}
		
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
