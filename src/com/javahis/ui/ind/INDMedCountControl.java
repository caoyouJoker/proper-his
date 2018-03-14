package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
* <p>Title: </p>
*
* <p>Description: 住院药品统计报表</p>
*
* <p>Copyright: Copyright (c) 2009</p>
*
* <p>Company: javahis</p>
*
* @author lij
* @version 1.0
*/
public class INDMedCountControl extends TControl {
	TTable table;
	public void onInit() {
		super.onInit();
		initPage();
		table = (TTable)this.getComponent("TABLE");
	}
	private void initPage() {
		this.clearValue("ORDER_DATE_S;ORDER_DATE_E;BIRTH_DATE;DEPT_CODE;ORDER_CODE;BIRTH_FLG");
		//初始化查询起讫时
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		Timestamp birth_date = SystemTool.getInstance().getDate();
		this.setValue("ORDER_DATE_S", date);
		this.setValue("ORDER_DATE_E", end_day);
		this.setValue("DC_DATE_S", date);
		this.setValue("DC_DATE_E", end_day);
		this.setValue("BIRTH_DATE", birth_date);
		this.setValue("ORDER_DATE_FLG", true);
		this.setValue("DC_DATE_FLG", false);
		this.callFunction("UI|TABLE|setParmValue", new TParm());

	}
	/**
	 * 查询
	 */
	public void onQuery(){
		String deptCode = this.getValueString("DEPT_CODE");
		String orderCode = this.getValueString("ORDER_CODE");
		String startDate = this.getValueString("ORDER_DATE_S");//modify by wangjc 20171204 精确到时分秒查询
		String endDate = this.getValueString("ORDER_DATE_E");//modify by wangjc 20171204 精确到时分秒查询
		String dcDateS = this.getValueString("DC_DATE_S");//add by wangjc 20171204 增加医嘱停用时间
		String dcDateE = this.getValueString("DC_DATE_E");//add by wangjc 20171204 增加医嘱停用时间
		String birthDate = this.getValueString("BIRTH_DATE").substring(0, 10);
		String birthFlg = this.getValueString("BIRTH_FLG");
		String sql = "SELECT DISTINCT C.DEPT_CHN_DESC ,A.CASE_NO ,B.PAT_NAME ,B.MR_NO ,B.BIRTH_DATE ,A.ORDER_CODE ,"+
				" A.ORDER_DESC ,A.ORDER_DR_CODE ,A.ORDER_DATE ,A.DC_DATE ,A.DC_DR_CODE  "+
				" FROM ODI_ORDER A, SYS_PATINFO B,SYS_DEPT C "+
				" WHERE A.MR_NO=B.MR_NO "+
				" AND A.DEPT_CODE=C.DEPT_CODE "+
				" AND A.CAT1_TYPE = 'PHA' ";
		if(!"".equals(deptCode)){
			sql += " AND A.DEPT_CODE = '"+ deptCode +"' ";
		}
		if(!"".equals(orderCode)){
			sql += " AND A.ORDER_CODE = '"+ orderCode +"' ";
		}
		if(this.getValueBoolean("ORDER_DATE_FLG")){
			if(!"".equals(startDate) && !"".equals(endDate)){//modify by wangjc 20171204 精确到时分秒查询
				sql += " AND A.ORDER_DATE BETWEEN TO_DATE('"
						+ startDate.substring(0, 19)
						+ "','yyyy-MM-dd HH24:mi:ss') AND TO_DATE('"
						+ endDate.substring(0, 19)
						+ "','yyyy-MM-dd HH24:mi:ss') ";
			}
		}
		if(this.getValueBoolean("DC_DATE_FLG")){
			if(!"".equals(dcDateS) && !"".equals(dcDateE)){//add by wangjc 20171204 增加医嘱停用时间
				sql += " AND A.DC_DATE BETWEEN TO_DATE('"
						+ dcDateS.substring(0, 19)
						+ "','yyyy-MM-dd HH24:mi:ss') AND TO_DATE('"
						+ dcDateE.substring(0, 19)
						+ "','yyyy-MM-dd HH24:mi:ss') ";
			}
		}
		//出生日期
		if(!"".equals(birthDate)){
			if(">".equals(birthFlg)){
				sql += "AND B.BIRTH_DATE > TO_DATE('"+ birthDate +" 235959', 'yyyy-MM-dd HH24miss')";
			} else if("<".equals(birthFlg)){
				sql += "AND B.BIRTH_DATE < TO_DATE('"+ birthDate +" 000000', 'yyyy-MM-dd HH24miss')";
			} else if("=".equals(birthFlg)){
				sql += " AND B.BIRTH_DATE BETWEEN TO_DATE('"+ birthDate
						+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
						+ birthDate
						+ " 235959','yyyy-MM-dd HH24miss') ";
			} else if(">=".equals(birthFlg)){
				sql += "AND B.BIRTH_DATE >= TO_DATE('"+ birthDate +" 000000', 'yyyy-MM-dd HH24miss')";
			} else if("<=".equals(birthFlg)){
				sql += "AND B.BIRTH_DATE <= TO_DATE('"+ birthDate +" 235959', 'yyyy-MM-dd HH24miss')";
			}
		}
		sql += "ORDER BY A.ORDER_DATE";
//		System.out.println("sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("parm:"+parm);
		table.setParmValue(parm);
		if (table.getRowCount() < 1) {
			// 查无数据
			this.messageBox("查无数据");
		}
	}
	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有要汇出的数据");
			return;
		}

		ExportExcelUtil.getInstance()
				.exportExcel(table, "住院药品统计报表");

	}
	
	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
	/**
	 * 得到上个月
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
}
