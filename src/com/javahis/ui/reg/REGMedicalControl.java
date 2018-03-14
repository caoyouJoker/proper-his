package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Date;

import jdo.reg.REGMedicalTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 门/急诊挂号就诊人数统计报表</p>
 *
 * <p>Description: 差门/急诊挂号就诊人数统计类</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.7.05
 * @version 1.0
 */
	public class REGMedicalControl extends TControl {
		private TTable table;

		public void onInit() {
	
			initPage();
	}

	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	 /**
	 * 查询门/急诊挂号就诊人数
	 */
	public void onQuery() {
		if(getValueString("ADM_TYPE").length() == 0){ //验证门急诊查询类别
			messageBox("请选择门急诊");
			return;
		}
		table.removeRowAll();
		TParm parm = new TParm();
		 //接收查询时间段
//		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddhhmmss");
//		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddhhmmss");
		String sDate = getValueString("S_DATE");
		sDate = sDate.substring(0, 4)+sDate.substring(5, 7)+sDate.substring(8, 10)+sDate.substring(11, 13)+sDate.substring(14, 16)+sDate.substring(17, 19);
		String eDate = getValueString("E_DATE");
		eDate = eDate.substring(0, 4)+eDate.substring(5, 7)+eDate.substring(8, 10)+eDate.substring(11, 13)+eDate.substring(14, 16)+eDate.substring(17, 19);
		parm.setData("S_DATE", sDate);
		parm.setData("E_DATE", eDate);
        //验证接受门诊类别
		if (getValue("ADM_TYPE").toString().length() != 0
				&& getValue("ADM_TYPE") != null) {
			parm.setData("ADM_TYPE", getValue("ADM_TYPE"));
		}
        //验证接收时诊医生
		if (this.getValueString("REALDR_CODE").length() != 0
				&& this.getValueString("REALDR_CODE") != null) {
			parm.setData("REALDR_CODE", this.getValueString("REALDR_CODE"));
		}
		//验证接收时诊科室
		if (this.getValueString("REALDEPT_CODE").length() != 0
				&& this.getValueString("REALDEPT_CODE") != null) {
			parm.setData("REALDEPT_CODE", this.getValueString("REALDEPT_CODE"));
		}
		TParm result = new TParm();
		if(getValueString("ADM_TYPE").equals("E")){ //急诊查询
			result = REGMedicalTool.getInstance().selectdata(parm);
		}else{
			result = REGMedicalTool.getInstance().selectdataO(parm); //门诊查询
		}
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询出现问题");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			return;
		}
			int count = 0;//行数
			int index=0;//小计统计
			TParm resultParm=new TParm();
			String date="";//就诊时间
			String type="";//就诊状态:N 未就诊  T 暂存  Y 已完成
			String stype="";//门诊级别
			stype=result.getValue("ADM_TYPE",0);
			date=result.getValue("REG_DATE",0);
			type=result.getValue("SEE_DR_FLG",0);
			for (int i = 0; i < result.getCount("MR_NO"); i++) {
			
			if (date.equals(result.getValue("REG_DATE", i)) &&//累计根据时间、门诊类别和就诊状态 实现小计功能
					type.equals(result.getValue("SEE_DR_FLG", i))&& 
					stype.equals(result.getValue("ADM_TYPE", i))) {
				resultParm.setRowData(count, result, i);
				resultParm.setData("SEE_DR_FLG", count, onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));//就诊状态转换汉字
				count++;
				index++;
			}else{//不相同 添加小计
				resultParm.addData("REG_DATE", "就诊状态小计");
				resultParm.addData("ADM_TYPE","" );
				resultParm.addData("MR_NO", "");
				resultParm.addData("SESSION_CODE", "");
				resultParm.addData("ADM_TYPE", "");
				resultParm.addData("SEX_CODE", "");
				resultParm.addData("PAT_NAME", "");
				resultParm.addData("SEE_DR_FLG", "");
				resultParm.addData("REALDEPT_CODE", "");
				resultParm.addData("REALDR_CODE", index);
				count++;
				index=0;
				date=result.getValue("REG_DATE",i);
				type=result.getValue("SEE_DR_FLG",i);
				stype=result.getValue("ADM_TYPE",i);
				if (date.equals(result.getValue("REG_DATE", i)) &&//累计根据时间、门诊类别和就诊状态 实现小计功能
						type.equals(result.getValue("SEE_DR_FLG", i))&& 
						stype.equals(result.getValue("ADM_TYPE", i))) {
					resultParm.setRowData(count, result, i);
					resultParm.setData("SEE_DR_FLG", count, onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));//就诊状态转换汉字
					count++;
					index++;
				}
			}
			if (i==result.getCount()-1) {
				resultParm.addData("REG_DATE", "就诊状态小计");
				resultParm.addData("ADM_TYPE", result.getValue("ADM_TYPE", 0));
				resultParm.addData("MR_NO", "");
				resultParm.addData("SESSION_CODE", "");
				resultParm.addData("PAT_NAME", "");
				resultParm.addData("SEX_CODE", "");
				//resultParm.addData("SEE_DR_FLG",onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));
				resultParm.addData("SEE_DR_FLG","");
				resultParm.addData("REALDEPT_CODE", "");
				resultParm.addData("REALDR_CODE", index);
			}
		}
		resultParm.setCount(count);
		table.setParmValue(resultParm);

	}
	/**
	 *初始化
	 */
	private void initPage() {

		table = getTable("TABLE");
		//给查询时间段赋值
		 //Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        // Timestamp today = SystemTool.getInstance().getDate();
        //setValue("S_DATE", yesterday);
        //setValue("E_DATE", today);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("E_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("ADM_TYPE","O");
	}
	  /**
	 * 清空内容
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "S_DATE;E_DATE;REALDEPT_CODE;REALDR_CODE;ADM_TYPE";
		clearValue(clearString);
		initPage();//从新给查询时间段赋值
		table.removeRowAll();
	}
    /**
     * 打印
     */
	public void onPrint() {
		// REG_DATE;ADM_TYPE;MR_NO;PAT_NAME;SEX_CODE;SESSION_CODE;SEE_DR_FLG;REALDEPT_CODE;REALDR_CODE
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd HH:mm:ss");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd HH:mm:ss");
		table = (TTable) this.getComponent("TABLE");
		int row = table.getRowCount()-2;
		
		Timestamp today = SystemTool.getInstance().getDate();
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		if (table.getRowCount() > 0) {
			// 打印数据
			TParm date = new TParm();
			// 表头数据   
			date.setData("TITLE", "TEXT", "门/急诊挂号就诊人数统计报表");
			date.setData("REG_DATE","TEXT","统计时间:"+sDate+"--"+eDate);
			//如果选择科室查询则给报表表头赋值科室
			if (this.getValueString("REALDEPT_CODE").length() != 0
					&& this.getValueString("REALDEPT_CODE") != null) {
				date.setData("REALDEPT_CODE","TEXT", "时诊科室:"+tableParm.getValue("REALDEPT_CODE",0));
			}
			date.setData("USER","TEXT","打印人员:"+Operator.getName());
			// 表格数据
			
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("REG_DATE", tableParm.getValue("REG_DATE", i));
				parm.addData("SEX_CODE", tableParm.getValue("SEX_CODE", i));
				parm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE", i));
				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				parm.addData("SESSION_CODE", tableParm.getValue("SESSION_CODE",
						i));
				parm.addData("SEE_DR_FLG", tableParm.getValue("SEE_DR_FLG", i));
				parm.addData("REALDEPT_CODE", tableParm.getValue(
						"REALDEPT_CODE", i));
				parm.addData("REALDR_CODE", tableParm
						.getValue("REALDR_CODE", i));

			}
			
			parm.setCount(parm.getCount("REG_DATE"));
			parm.addData("SYSTEM", "COLUMNS", "REG_DATE");
			parm.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SESSION_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SEE_DR_FLG");
			parm.addData("SYSTEM", "COLUMNS", "REALDEPT_CODE");
			parm.addData("SYSTEM", "COLUMNS", "REALDR_CODE");
			date.setData("TABLE", parm.getData());
			date.setData("DATE","TEXT","打印时间:"+today);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\REG\\REGMedicalList.jhw", date);
		} else {
			this.messageBox("没有打印数据");
			return;
		}
	}

	/**
	 * 得到就诊状态中文
	 */
	public String onGetSeeDrType(String type) {
		String typeName = "";
		if (type.equals("N"))
			typeName = "未就诊";
		if ("T".endsWith(type)) {
			typeName = "暂存";
		}
		if ("Y".endsWith(type)) {
			typeName = "已完成";
		}
		return typeName;
	}
	
	/**
	 * 导出Excel
	 * add by huangtt 20141107
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "门/急诊挂号就诊人数统计报表");
	}
	
}
