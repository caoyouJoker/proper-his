package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 介入中心记账统计表
 * </p>
 * 
 * <p>
 * Description: 介入中心记账统计表
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c)2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 2015-11-5
 * @version 4.0
 */
public class BILOpeInComeQueryControl  extends TControl {
	private TTable table;
	public void onInit() { 
		super.onInit();
		table = (TTable) getComponent("TABLE");	
		initPage();
	}
	private void initPage(){
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')+ " 00:00:00");
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("OPE_TYPE", "3");
		table.removeRowAll();
	}
	/**
	 * 
	* @Title: onQuery
	* @Description: TODO(查询)
	* @author pangben
	* @throws
	 */
	public void onQuery(){
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		String where="";
		if (this.getValueString("DEPT_CODE").length()>0) {
			where=" AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"' ";
		}
		if (!this.getValueString("OPE_TYPE").equals("3")) {//全部
			where+=" AND B.STATE='"+this.getValueString("OPE_TYPE")+"' ";
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String sql="SELECT '' SEQ_NO,G.DEPT_CHN_DESC,TO_CHAR(B.OP_DATE,'YYYY/MM/DD') OP_DATE,D.PAT_NAME,CASE WHEN D.SEX_CODE='1' THEN '男' WHEN D.SEX_CODE='2' THEN '女' ELSE '未知' END SEX_CODE," +
				"FLOOR(MONTHS_BETWEEN(SYSDATE,D.BIRTH_DATE)/12) ||'岁' AS AGE,D.MR_NO,C.OPT_CHN_DESC,F.USER_NAME,'0.00' OTH_AMT,'0.00' OP_AMT,'0.00' PHA_AMT,'0.00' SUM_AMT ," +
				"A.CASE_NO " +
				"FROM ADM_INP A,OPE_OPBOOK B ,SYS_OPERATIONICD C,SYS_PATINFO D,SYS_OPERATOR F,SYS_DEPT G " +
				"WHERE A.CASE_NO=B.CASE_NO AND B.OP_CODE1=C.OPERATION_ICD(+) AND B.MAIN_SURGEON=F.USER_ID(+) "+
				"AND A.MR_NO=D.MR_NO AND A.DEPT_CODE=G.DEPT_CODE AND A.CANCEL_FLG<>'Y' " +
				"AND B.OP_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') " +where+
				"GROUP BY F.USER_NAME,C.OPT_CHN_DESC,G.DEPT_CHN_DESC," +
				"D.PAT_NAME,D.SEX_CODE,D.MR_NO,A.CASE_NO,B.OP_DATE,D.BIRTH_DATE ORDER BY G.DEPT_CHN_DESC,B.OP_DATE";
		//System.out.println("sql::::"+sql);
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		if (result.getErrCode()<0) {
			this.messageBox("查询出现问题");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			table.removeRowAll();
			return;
		}
		//sql="SELECT CAT1_TYPE ,SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='&' GROUP BY CAT1_TYPE";
		TParm totParm=null;
		double othAmt=0.00;
		double phaAmt=0.00;
		double opAmt=0.00;
		for (int i = 0; i < result.getCount(); i++) {
			totParm = new TParm( TJDODBTool.getInstance().select("SELECT CAT1_TYPE ,SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
					+result.getValue("CASE_NO",i)+"' GROUP BY CAT1_TYPE"));
			if (totParm.getErrCode()<0) {
				this.messageBox("查询出现问题");
				return;
			}
			result.setData("SEQ_NO",i,(i+1));
			if (totParm.getCount()>0) {
				for (int j = 0; j < totParm.getCount(); j++) {
					if (totParm.getValue("CAT1_TYPE",j).equals("OTH")
							||totParm.getValue("CAT1_TYPE",j).equals("TRT")) {
						othAmt+=totParm.getDouble("TOT_AMT",j);
					}
					else if (totParm.getValue("CAT1_TYPE",j).equals("PHA")) {
						phaAmt+=totParm.getDouble("TOT_AMT",j);
					}
					else{
						opAmt+=totParm.getDouble("TOT_AMT",j);
					}
				}
				result.setData("OTH_AMT",i,StringTool.round(othAmt, 2));
				result.setData("OP_AMT",i,StringTool.round(opAmt,2));
				result.setData("PHA_AMT",i,StringTool.round(phaAmt,2));
				result.setData("SUM_AMT",i,StringTool.round(othAmt+opAmt+phaAmt,2));
				othAmt=0.00;
				opAmt=0.00;
				phaAmt=0.00;
			}
		}
		table.setParmValue(result);
	}
	/**
	 * 
	* @Title: onPrint
	* @Description: TODO(打印)
	* @author pangben
	* @throws
	 */
	public void onPrint(){
		TParm tableParm=table.getParmValue();
		if (tableParm.getCount()<=0) {
			this.messageBox("没有需要打印的数据");
			return;
		}
		TParm printData=new TParm();
		String startDate=this.getValueString("START_DATE");
		String endDate=this.getValueString("END_DATE");
		String DATE = startDate.substring(0,startDate.lastIndexOf(".")) +"～" + endDate.substring(0,endDate.lastIndexOf("."));
        printData.setData("DATE","TEXT",DATE);
        printData.setData("TABLE",tableParm.getData());
        printData.setData("TITLE","TEXT","介入中心记账统计表");
        printData.setData("PRINT_DATE","TEXT",StringTool.getString(
        		SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
        printData.setData("OPT_NAME","TEXT",Operator.getName());
		this.openPrintDialog("%ROOT%\\config\\prt\\bil\\BILOpeInCome.jhw",printData);
	}
	/**
	 * 
	* @Title: onClear
	* @Description: TODO(清空)
	* @author pangben
	* @throws
	 */
	public void onClear(){
		initPage();
	}
	/**
	 * 
	* @Title: onExport
	* @Description: TODO(汇出)
	* @author pangben
	* @throws
	 */
	public void onExport(){
		// TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("无导出资料");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table,"介入手术收入统计表");
	}
}
