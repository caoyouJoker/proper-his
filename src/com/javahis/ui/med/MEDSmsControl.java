package com.javahis.ui.med;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
/**
 * 危急值管理
 * @author wangqing
 *
 */
public class MEDSmsControl extends TControl {

	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	// 记录表的选中行数
	int selectedRowIndex = -1;

	TTable table;

	private TParm data;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent(TABLE);
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// modified by WangQing 20170502
		this.onClear();
		onQuery();
	}

	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	public void onQueryNO() {
		String mrNo = getValueString("MR_NO");
		if (mrNo.length() > 0) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			setValue("MR_NO", mrNo);
			// modify by huangtt 20160929 EMPI患者查重提示 start
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("MR_NO", pat.getMrNo());// 病案号
			}
			// modify by huangtt 20160929 EMPI患者查重提示 end
			this.onQuery();
		}
	}

	public void onTableClicked(int row) {
		callFunction("UI|DELETE|setEnabled", true);
		if (row < 0) {
			return;
		} else {
			setValueForParm("SMS_CODE;MR_NO;HANDLE_OPINION", data, row);
			// modified by WangQing 20170502
			setValueForParm("DEPT_CODE;STATION_CODE;REPORT_DEPT_CODE;HANDLE_OPINION", data, row);
			this.setValue("SMS_STATE", data.getValue("STATE", row));
			selectedRowIndex = row;

			/**
			 * callFunction("UI|DEPT_CODE|setEnabled", new Object[] {
			 * Boolean.valueOf(false) });
			 * callFunction("UI|STATION_CODE|setEnabled", new Object[] {
			 * Boolean.valueOf(false) });
			 */
			return;
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {

		TParm selectCondition = getParmForTag(
				"DEPT_CODE;STATION_CODE;SMS_STATE;MR_NO;REPORT_DEPT_CODE", true);
		selectCondition.setData("BEGIN_TIME", getValue("BEGIN_TIME"));
		selectCondition.setData("END_TIME", getValue("END_TIME"));
//		System.out.println("--------selectCondition:"+selectCondition);
		data = MedSmsTool.getInstance().onQuery(selectCondition);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		} else {
//			callFunction("UI|Table|setParmValue", new Object[] { data });
			table.setParmValue(data);
			for (int i = 0; i < data.getCount(); i++) {
				TParm p = data.getRow(i);
				long time = getDiffTime(p.getValue("SEND_TIME"));
				// System.out.println("time=========:"+time);
				if (!p.getValue("STATE").equals("9")) {
					if (time > 0 && time < 31) {
						/** 淡蓝色 */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(0, 128, 255));
					}
					if (time > 30 && time < 41) {

						/** 蓝色 */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(0, 0, 255));
					}
					if (time > 41) {

						/** 红色 */
						this.getTable(TABLE).setRowTextColor(i,
								new Color(255, 0, 0));
					}
				} else {
					this.getTable(TABLE).setRowTextColor(i, new Color(0, 0, 0));
				}
			}
			return;
		}

	}

	/**
	 * 清空事件
	 */
	public void onClear() {
		this
		.clearValue("STATION_CODE;DEPT_CODE;BEGIN_TIME;END_TIME;MR_NO;HANDLE_OPINION;SMS_STATE;SMS_CODE;REPORT_DEPT_CODE");
		// modified by WangQing 20170502
		table.removeRowAll();
		// 初始化验收时间
				// 出库日期
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("BEGIN_TIME", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("DEPT_CODE", Operator.getDept());
	}

	/**
	 * 短信处理
	 */
	public void onSave() {
		TParm parm = getParmForTag("HANDLE_OPINION;SMS_CODE");
		if (parm.getValue("SMS_CODE") == null
				|| parm.getValue("SMS_CODE").equals("")) {
			this.messageBox("请选择危急值记录");
			return;
		}

		if (parm.getValue("HANDLE_OPINION") == null
				|| parm.getValue("HANDLE_OPINION").equals("")) {
			this.messageBox("请填写处理意见");
			return;
		}

		TParm result = MedSmsTool.getInstance().updateMedSms(parm);

		if (result.getErrCode() < 0) {
			this.messageBox("错误！", result.getErrText(), -1);
			return;
		} else {
			this.messageBox("危急值处理完成！");
			this.getTable("TABLE").setSelectedRow(selectedRowIndex);
		}
		onClear();
		onQuery();
	}

	public String isRole(String roleId) {
		/** 1: 医生 2:护士 */
		if ("ODO".equals(roleId) || "ODI".equals(roleId)
				|| "OIDR".equals(roleId)) {
			return "1";
		}
		if ("NWO".equals(roleId) || "NWE".equals(roleId)
				|| "NBW".equals(roleId) || "HREG".equals(roleId)
				|| "NWH".equals(roleId) || "NM".equals(roleId)
				|| "NWICU".equals(roleId)) {
			return "2";
		}
		return null;
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 两时间之差分钟(发送时间和当前时间)
	 * 
	 * @param 当前时间
	 * @return
	 */
	private long getDiffTime(String sendTime) {

		// String systemTime = DateUtil.getNowTime(TIME_FORMAT);
		String systemTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), TIME_FORMAT);
		Date begin = null;
		Date end = null;
		try {
			end = sdf.parse(systemTime);
			begin = sdf.parse(sendTime);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		/** 秒 **/
		long between = (end.getTime() - begin.getTime()) / 1000;

		/** 分钟 **/
		long minute = between / 60;
		return minute;
	}
	
	/**
	 * 导出Excel
	 */
	public void onExcel() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有数据！");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "危急值统计表");
	}

	/**
	 * 打印方法
	 */
	public void onPrint() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有需要打印的数据！");
			return;
		}
		String beginTime = getValueString("BEGIN_TIME").substring(0, getValueString("BEGIN_TIME").indexOf("."));
		String endTime = getValueString("END_TIME").substring(0, getValueString("END_TIME").indexOf("."));
		TParm parm = new TParm();
		parm.setData("START_TIME", beginTime);
		parm.setData("END_TIME", endTime);
		if(!"".equals(this.getValueString("DEPT_CODE")))
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		TParm printData = new TParm();
		TParm printParm = new TParm();
		printData = MedSmsTool.getInstance().getPrintData(parm);
		
		printData.setCount(printData.getCount("DEPT_CHN_DESC"));
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "ZLS");
		printData.addData("SYSTEM", "COLUMNS", "ASLS");
		printData.addData("SYSTEM", "COLUMNS", "KZRLS");
		printData.addData("SYSTEM", "COLUMNS", "KZGLS");
		printData.addData("SYSTEM", "COLUMNS", "ZGYZLS");
		printData.addData("SYSTEM", "COLUMNS", "ZYSLS");

		printParm.setData("TABLE", printData.getData());
		printParm.setData("USER", "TEXT", "制表人：" + Operator.getName());
		printParm.setData("REPORT_DATE", "TEXT", "统计时间：" + beginTime + " - " + endTime);
		printParm.setData("DATE", "TEXT", "制表时间："
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\MED\\MEDSmsReport.jhw",
				printParm);
	}
}
