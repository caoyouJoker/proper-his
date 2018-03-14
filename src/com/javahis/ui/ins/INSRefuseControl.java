package com.javahis.ui.ins;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 医保拒付表</p>
 *
 * <p>Description:   医保拒付表 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120209
 * @version 1.0
 */
public class INSRefuseControl extends TControl{
	
	TTable table;//取table
	private int radio = 1;//默认的选中的combobox
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable)this.getComponent("TABLE");//取table
    	setValue("SHEET", 1);
    }
    /**
     * combobox监听器
     */
    public void onChange(){
    	int sheet = getValueInt("SHEET");//取得sheet值
    	this.callFunction("UI|query|setEnabled", true);
		this.callFunction("UI|export|setEnabled", true);
    	if(sheet == 1){
    		this.getTTextFormat("START_YEAR").setEnabled(true);
    		this.getTTextFormat("END_YEAR").setEnabled(true);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(false);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(false);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("月份,100;人次,100;发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;" +
    				"拒付金额,100,double,#########0.00;" +
    				"拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;检查费,100,double,#########0.00;" +
    				"治疗费,100,double,#########0.00;手术费,100,double,#########0.00;" +
    				"床位费,100,double,#########0.00;材料费,100,double,#########0.00;" +
    				"其他费,100,double,#########0.00;全血费,100,double,#########0.00;" +
    				"成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,left;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right");
    		table.setParmMap(
    				"YE;COUNT(AA.ADM_SEQ);SUM(AA.TOTAL_AMT);SUM(AA.TOTAL_NHI_AMT);SUM(AA.REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 1;
    	}
    	if(sheet == 2){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("盘号,100;批次,100;发生金额,100,double,#########0.00;" +
    				"申报金额,100,double,#########0.00;拒付金额,100,,double,#########0.00;" +
    				"拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;检查费,100,double,#########0.00;" +
    				"治疗费,100,double,#########0.00;手术费,100,double,#########0.00;" +
    				"床位费,100,double,#########0.00;材料费,100,double,#########0.00;" +
    				"其他费,100,double,#########0.00;全血费,100,double,#########0.00;" +
    				"成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,left;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right");
    		table.setParmMap(
    				"NHI_NUM;COUNT(AA.ADM_SEQ);SUM(AA.TOTAL_AMT);SUM(AA.TOTAL_NHI_AMT);SUM(AA.REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 2;
    	}
    	if(sheet == 3){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(true);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("患者姓名,100;发生金额,100,double,#########0.00;" +
    				"申报金额,100,double,#########0.00;拒付金额,100,double,#########0.00;" +
    				"拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;检查费,100,double,#########0.00;" +
    				"治疗费,100,double,#########0.00;手术费,100,double,#########0.00;" +
    				"床位费,100,double,#########0.00;材料费,100,double,#########0.00;" +
    				"其他费,100,double,#########0.00;全血费,100,double,#########0.00;" +
    				"成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,left;5,right;6,right;" +
    				"7,right;8,right;9,right;10,right;11,right;12,right;13,right");
    		radio = 3;
    		this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
    	}
    	if(sheet == 4){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("患者姓名,100;发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;" +
    				"拒付金额,100,double,#########0.00;拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;" +
    				"检查费,100,double,#########0.00;治疗费,100,double,#########0.00;" +
    				"手术费,100,double,#########0.00;床位费,100,double,#########0.00;" +
    				"材料费,100,double,#########0.00;其他费,100,double,#########0.00;" +
    				"全血费,100,double,#########0.00;成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,left;5,right;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right");
    		table.setParmMap(
    				"PAT_NAME;SUM(A.TOTAL_AMT);SUM(A.TOTAL_NHI_AMT);SUM(REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 4;
    	}
    	if(sheet == 5){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("主管医师,100;人次,100;发生金额,100,double,#########0.00;" +
    				"申报金额,100,double,#########0.00;拒付金额,100,double,#########0.00;" +
    				"拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;" +
    				"检查费,100,double,#########0.00;治疗费,100,double,#########0.00;" +
    				"手术费,100,double,#########0.00;床位费,100,double,#########0.00;" +
    				"材料费,100,double,#########0.00;其他费,100,double,#########0.00;" +
    				"全血费,100,double,#########0.00;成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,left;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right");
    		table.setParmMap(
    				"USER_NAME;SUM(TT.COUNT);SUM(TT.TOTAL_AMT);SUM(TT.TOTAL_NHI_AMT);SUM(TT.REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 5;
    	}
    	if(sheet == 6){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(true);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("住院号,100;病患姓名,100;入院时间,100;出院时间,100;项目数量,100;" +
    				"项目单价,100,double,#########0.00;发生金额,100,double,#########0.00;" +
    				"申报金额,100,double,#########0.00;拒付金额,100,double,#########0.00;拒付原因,100");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,right;" +
					"7,right;8,right;9,right");
    		radio = 6;
    		this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
    	}
    	if(sheet == 7){
    		this.getTTextFormat("START_YEAR").setEnabled(true);
    		this.getTTextFormat("END_YEAR").setEnabled(true);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(false);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(false);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("月份,100;发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;" +
    				"拒付人次,100;拒付金额,100,double,#########0.00;拒付率(%),100,double,#########0.00;" +
    				"药品费,100,double,#########0.00;检查费,100,double,#########0.00;" +
    				"治疗费,100,double,#########0.00;手术费,100,double,#########0.00;" +
    				"床位费,100,double,#########0.00;材料费,100,double,#########0.00;" +
    				"其他费,100,double,#########0.00;全血费,100,double,#########0.00;" +
    				"成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,left;4,right;5,left;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right");
    		table.setParmMap(
    				"SUBSTR(B.YEAR_MON,5,2);SUM(A.TOTAL_AMT);SUM(A.TOTAL_NHI_AMT);COUNT(A.ADM_SEQ);SUM(REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 7;
    	}
    	if(sheet == 8){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("科室,100;人次,100;发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;" +
    				"拒付金额,100,double,#########0.00;拒付率(%),100,double,#########0.00;药品费,100,double,#########0.00;" +
    				"检查费,100,double,#########0.00;治疗费,100,double,#########0.00;" +
    				"手术费,100,double,#########0.00;床位费,100,double,#########0.00;" +
    				"材料费,100,double,#########0.00;其他费,100,double,#########0.00;" +
    				"全血费,100,double,#########0.00;成分血费,100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,left;6,right;" +
					"7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right");
    		table.setParmMap(
    				"DEPT_DESC;COUNT(AA.ADM_SEQ);SUM(AA.TOTAL_AMT);SUM(AA.TOTAL_NHI_AMT);SUM(AA.REFUSE_AMT);SPACE1;" +
    				"PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
    		radio = 8;
    	}
    	if(sheet == 9){
    		this.getTTextFormat("START_YEAR").setEnabled(false);
    		this.getTTextFormat("END_YEAR").setEnabled(false);
    		this.getTTextFormat("START_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("END_YEAR_MON").setEnabled(true);
    		this.getTTextFormat("DOCTOR").setEnabled(false);
    		this.getTTextField("PROJECT_CODE").setEnabled(false);
    		this.getTTextField("PROJECT_NAME").setEnabled(false);
    		table.setHeader("项目名称,100,NHI_ORD_CLASS_CODE;人次,100;发生金额,100,double,#########0.00;" +
    				"申报金额,100,double,#########0.00;拒付金额,100,double,#########0.00;拒付率(%),100,double,#########0.00");
    		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,left");
    		table.setParmMap("NHI_ORD_CLASS_CODE;COUNT(AA.ADM_SEQ);SUM(AA.TOTAL_AMT);SUM(AA.TOTAL_NHI_AMT);" +
    				"SUM(AA.REFUSE_AMT);SPACE1");
    		radio = 9;
    	}
    	onClear();
    }
    /**
     * 清空
     */
    public void onClear(){
    	clearValue("START_YEAR;END_YEAR;START_YEAR_MON;END_YEAR_MON;DOCTOR;PROJECT_CODE;" +
    			"PROJECT_NAME");
//    	setValue("SHEET", 1);
//    	onChange();
    }
    /**
     * 获取TTextFormat
     * @param tag
     * @return
     */
    private TTextFormat getTTextFormat(String tag){
    	TTextFormat textFormat = (TTextFormat) getComponent(tag);
    	return textFormat;
    }
    /**
     * 获取TTextField
     * @param tag
     * @return
     */
    private TTextField getTTextField(String tag){
    	TTextField textField = (TTextField) getComponent(tag);
    	return textField;
    }
    /**
     * 查询
     */
    public void onQuery(){
    	String startyear = getValueString("START_YEAR");
    	String endyear = getValueString("END_YEAR");
    	String startyearmon = getValueString("START_YEAR_MON");
    	String endyearmon = getValueString("END_YEAR_MON");
    	String doctor = getValueString("DOCTOR");
    	String projectcode = getValueString("PROJECT_CODE");
    	String projectname = getValueString("PROJECT_NAME");
    	String sql1 = "";
    	String sql2 = "";
    	TParm result = null;
    	if(radio == 1){
    		if("".equals(startyear)&&"".equals(endyear)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyear = startyear.substring(0, 4);
        	endyear = endyear.substring(0, 4);
        	result = query1(startyear,endyear);
    	}
    	if(radio == 2){
    		if("".equals(startyearmon)&&"".equals(endyearmon)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyearmon = startyearmon.substring(0, 4)+startyearmon.substring(5, 7);
    		endyearmon = endyearmon.substring(0, 4)+endyearmon.substring(5, 7);
    		result = query2(startyearmon,endyearmon);
    	}
    	if(radio == 8){
    		if("".equals(startyearmon)&&"".equals(endyearmon)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyearmon = startyearmon.substring(0, 4)+startyearmon.substring(5, 7);
    		endyearmon = endyearmon.substring(0, 4)+endyearmon.substring(5, 7);
    		result = query8(startyearmon,endyearmon);
    	}
    	if(radio == 4){
    		if("".equals(startyearmon)&&"".equals(endyearmon)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyearmon = startyearmon.substring(0, 4)+startyearmon.substring(5, 7);
    		endyearmon = endyearmon.substring(0, 4)+endyearmon.substring(5, 7);
    		result = query4(startyearmon,endyearmon);
    	}
    	if(radio == 7){
    		if("".equals(startyear)&&"".equals(endyear)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyear = startyear.substring(0, 4);
        	endyear = endyear.substring(0, 4);
    		result = query7(startyear,endyear);
    	}
    	if(radio == 9){
    		if("".equals(startyearmon)&&"".equals(endyearmon)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyearmon = startyearmon.substring(0, 4)+startyearmon.substring(5, 7);
    		endyearmon = endyearmon.substring(0, 4)+endyearmon.substring(5, 7);
    		result = query9(startyearmon,endyearmon);
    	}
    	if(radio == 5){
    		if("".equals(startyearmon)&&"".equals(endyearmon)){
    			messageBox("请输入查询条件");
    			return;
    		}
    		startyearmon = startyearmon.substring(0, 4)+startyearmon.substring(5, 7);
    		endyearmon = endyearmon.substring(0, 4)+endyearmon.substring(5, 7);
    		result = query5(startyearmon,endyearmon);
    	}
    	this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * 医保住院医疗费拒付金额汇总表
     * @param startyear
     * @param endyear
     * @return
     */
    private TParm query1(String startyear,String endyear){
    	String sql = 
    		"SELECT AA.YE, COUNT (AA.ADM_SEQ) , SUM (AA.TOTAL_AMT),SUM (AA.TOTAL_NHI_AMT), SUM (AA.REFUSE_AMT), '' AS SPACE1 " +
    		" FROM (SELECT   SUBSTR (B.YEAR_MON, 5, 2) YE, A.ADM_SEQ, " +
    		" SUM (A.TOTAL_AMT) TOTAL_AMT, " +
    		" SUM (A.TOTAL_NHI_AMT) TOTAL_NHI_AMT, " +
    		" SUM (REFUSE_AMT) REFUSE_AMT, ''  " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE SUBSTR (B.YEAR_MON, 1, 4) >= '"+startyear+"' " +
    		" AND SUBSTR (B.YEAR_MON, 1, 4) <= '"+endyear+"' " +
    		" GROUP BY A.ADM_SEQ, SUBSTR (YEAR_MON, 5, 2) " +
    		" HAVING SUM (REFUSE_AMT) <> 0) AA " +
    		" GROUP BY AA.YE";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"SELECT SUBSTR(B.YEAR_MON, 5, 2), SUM(A.REFUSE_AMT) " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE SUBSTR (B.YEAR_MON, 1, 4) >= '"+startyear+"' " +
    			" AND SUBSTR (B.YEAR_MON, 1, 4) <= '"+endyear+"' " +
    			" AND A.NHI_ORD_CLASS_CODE = '"+"0"+i+"' " +
    			" GROUP BY SUBSTR (YEAR_MON, 5, 2) " +
    			" HAVING SUM (REFUSE_AMT) <> 0";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("YE", result2.getData("SUBSTR(B.YEAR_MON,5,2)",j));
    				parm.addData("SUM(A.REFUSE_AMT)", result2.getData("SUM(A.REFUSE_AMT)",j));
				}
    		}
		}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(AA.REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(AA.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("YE", i).equals(parm.getData("YE", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("SUM(A.REFUSE_AMT)", j));
    			}	
			}
			
		}
    	return result1;
    }
    /**
     * 医保住院医疗费拒付金额统计表
     * @param startyearmon
     * @param endyearmon
     * @return
     */
    private TParm query2(String startyearmon,String endyearmon){
    	String sql = 
    		"SELECT AA.NHI_NUM, COUNT (AA.ADM_SEQ), SUM (AA.TOTAL_AMT), " +
    		" SUM (AA.TOTAL_NHI_AMT), SUM (AA.REFUSE_AMT), '' AS SPACE1 " +
    		" FROM (SELECT   B.NHI_NUM, A.ADM_SEQ, SUM (A.TOTAL_AMT) TOTAL_AMT, " +
    		" SUM (A.TOTAL_NHI_AMT) TOTAL_NHI_AMT, " +
    		" SUM (REFUSE_AMT) REFUSE_AMT, '' " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    		" GROUP BY A.ADM_SEQ, B.NHI_NUM " +
    		" HAVING SUM (REFUSE_AMT) <> 0) AA " +
    		" GROUP BY AA.NHI_NUM";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"SELECT   B.NHI_NUM, SUM(A.REFUSE_AMT) " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE B.YEAR_MON >= '"+startyearmon+"' " +
    			" AND B.YEAR_MON <= '"+endyearmon+"' " +
    			" AND A.NHI_ORD_CLASS_CODE = '0"+i+"' " +
    			" GROUP BY B.NHI_NUM, A.NHI_ORD_CLASS_CODE " +
    			" HAVING SUM (REFUSE_AMT) <> 0";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("NHI_NUM", result2.getData("NHI_NUM",j));
    				parm.addData("SUM(A.REFUSE_AMT)", result2.getData("SUM(A.REFUSE_AMT)",j));
				}
    		}
    	}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(AA.REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(AA.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("NHI_NUM", i).equals(parm.getData("NHI_NUM", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("SUM(A.REFUSE_AMT)", j));
    			}	
			}
			
		}
    	return result1;
    }
    /**
     * 科室医保住院医疗拒付金额统计表
     * @param startyearmon
     * @param endyearmon
     * @return
     */
    private TParm query8(String startyearmon,String endyearmon){
    	String sql = 
    		"SELECT   AA.DEPT_DESC, COUNT (AA.ADM_SEQ), SUM (AA.TOTAL_AMT), " +
    		" SUM (AA.TOTAL_NHI_AMT), SUM (AA.REFUSE_AMT), '' AS SPACE1 " +
    		" FROM (SELECT   B.DEPT_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) TOTAL_AMT, " +
    		" SUM (A.TOTAL_NHI_AMT) TOTAL_NHI_AMT, " +
    		" SUM (REFUSE_AMT) REFUSE_AMT, '' " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    		" GROUP BY A.ADM_SEQ, B.DEPT_DESC HAVING SUM (REFUSE_AMT) <> 0) AA " +
    		"  GROUP BY AA.DEPT_DESC";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"SELECT   B.DEPT_DESC, SUM (A.REFUSE_AMT) " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    			" AND A.NHI_ORD_CLASS_CODE = '0"+i+"' " +
    			" GROUP BY B.DEPT_DESC, A.NHI_ORD_CLASS_CODE " +
    			" HAVING SUM (REFUSE_AMT) <> 0";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("DEPT_DESC", result2.getData("DEPT_DESC",j));
    				parm.addData("SUM(A.REFUSE_AMT)", result2.getData("SUM(A.REFUSE_AMT)",j));
    			}
    		}
    	}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(AA.REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(AA.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("DEPT_DESC", i).equals(parm.getData("DEPT_DESC", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("SUM(A.REFUSE_AMT)", j));
    			}	
    		}
    		
    	}
    	return result1;
    }
    /**
     * 科室医保病人住院医疗费拒付金额一览表
     * @param startyearmon
     * @param endyearmon
     * @return
     */
    private TParm query4(String startyearmon,String endyearmon){
    	String sql = 
    		"SELECT   B.PAT_NAME, SUM (A.TOTAL_AMT), SUM (A.TOTAL_NHI_AMT), " +
    		" SUM (REFUSE_AMT), '' AS SPACE1 " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    		" GROUP BY B.PAT_NAME " +
    		" HAVING SUM (REFUSE_AMT) <> 0";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"SELECT   B.PAT_NAME, SUM (A.REFUSE_AMT) " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+
    			"' AND A.NHI_ORD_CLASS_CODE = '0"+i+"' " +
    			" GROUP BY B.PAT_NAME " +
    			" HAVING SUM (REFUSE_AMT) <> 0";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("PAT_NAME", result2.getData("PAT_NAME",j));
    				parm.addData("SUM(A.REFUSE_AMT)", result2.getData("SUM(A.REFUSE_AMT)",j));
    			}
    		}
    	}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(A.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("PAT_NAME", i).equals(parm.getData("PAT_NAME", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("SUM(A.REFUSE_AMT)", j));
    			}	
    		}
    		
    	}
    	return result1;
    }
    /**
     * 医保住院病人某项费用拒付情况统计表
     * @param startyear
     * @param endyear
     * @return
     */
    private TParm query7(String startyear,String endyear){
    	String sql = 
    		"SELECT SUBSTR (B.YEAR_MON, 5, 2), SUM (A.TOTAL_AMT), SUM (A.TOTAL_NHI_AMT), " +
    		" COUNT (A.ADM_SEQ), SUM (REFUSE_AMT), '' AS SPACE1 " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE SUBSTR (B.YEAR_MON, 1, 4) >= '"+startyear+"' AND SUBSTR (B.YEAR_MON, 1, 4) <= '"+endyear+"' " +
    		" GROUP BY SUBSTR (YEAR_MON, 5, 2) " +
    		" HAVING SUM (REFUSE_AMT) <> 0";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"  SELECT   SUBSTR(B.YEAR_MON, 5, 2), SUM (A.REFUSE_AMT) " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE SUBSTR (B.YEAR_MON, 1, 4) >= '"+startyear+"' " +
    			" AND SUBSTR (B.YEAR_MON, 1, 4) <= '"+endyear+"' " +
    			" AND A.NHI_ORD_CLASS_CODE = '0"+i+"' " +
    			" GROUP BY SUBSTR (YEAR_MON, 5, 2), A.NHI_ORD_CLASS_CODE " +
    			" HAVING SUM (REFUSE_AMT) <> 0";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("SUBSTR(B.YEAR_MON,5,2)", result2.getData("SUBSTR(B.YEAR_MON,5,2)",j));
    				parm.addData("SUM(A.REFUSE_AMT)", result2.getData("SUM(A.REFUSE_AMT)",j));
    			}
    		}
    	}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(A.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("SUBSTR(B.YEAR_MON,5,2)", i).equals(parm.getData("SUBSTR(B.YEAR_MON,5,2)", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("SUM(A.REFUSE_AMT)", j));
    			}	
    		}
    		
    	}
    	return result1;
    }
    /**
     * 医师拒付情况汇总表
     * @param startyearmon
     * @param endyearmon
     * @return
     */
    private TParm query5(String startyearmon,String endyearmon){
    	String sql = 
    		"SELECT TT.USER_NAME, SUM(TT.COUNT), SUM(TT.TOTAL_AMT), SUM(TT.TOTAL_NHI_AMT), " +
    		" SUM(TT.REFUSE_AMT), ''  AS SPACE1 " +
    		" FROM ( SELECT DD.USER_NAME, '1' AS COUNT, AA.TOTAL_AMT AS TOTAL_AMT," +
    		" AA.TOTAL_NHI_AMT AS TOTAL_NHI_AMT, AA.REFUSE_AMT AS REFUSE_AMT, ''  " +
    		" FROM " +
    		" (SELECT   B.CASE_NO AS CASE, SUM(A.TOTAL_AMT) AS TOTAL_AMT, SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT, " +
    		" SUM (REFUSE_AMT) AS REFUSE_AMT, '' " +
    		"  FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B ON A.ADM_SEQ = B.ADM_SEQ " +
    		" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    		" GROUP BY B.CASE_NO " +
    		" HAVING SUM (REFUSE_AMT) <> 0) AA, " +
    		" ADM_INP BB, SYS_OPERATOR DD " +
    		"  WHERE DD.USER_ID = BB.VS_DR_CODE  " +
    		" AND BB.CASE_NO = AA.CASE) TT  " +
    		" GROUP BY TT.USER_NAME";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	TParm result2 = null;
    	TParm parm = new TParm();//类型 月 费用
    	for (int i = 1; i < 10; i++) {
    		sql = 
    			"SELECT   DD.USER_NAME, AA.REFUSE_AMT" +
    			" FROM (SELECT   B.CASE_NO AS CASE, A.NHI_ORD_CLASS_CODE AS NHI_CODE, " +
    			" SUM (A.REFUSE_AMT) AS REFUSE_AMT " +
    			" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    			" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    			" WHERE B.YEAR_MON >= '"+startyearmon+"' " +
    			" AND B.YEAR_MON <= '"+endyearmon+"' " +
    			" AND A.NHI_ORD_CLASS_CODE = '0" + i + "' " +
    					" GROUP BY B.CASE_NO, A.NHI_ORD_CLASS_CODE " +
    					" HAVING SUM (REFUSE_AMT) <> 0) AA, " +
    					" ADM_INP BB, " +
    					"  SYS_OPERATOR DD " +
    					" WHERE DD.USER_ID = BB.VS_DR_CODE   " +
    					" AND BB.CASE_NO = AA.CASE " +
    					"GROUP BY DD.USER_NAME, AA.NHI_CODE, AA.REFUSE_AMT";
    		result2 = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result2.getCount()>0){
    			for (int j = 0; j < result2.getCount(); j++) {
    				parm.addData("TYPE", "PARM"+i);
    				parm.addData("USER_NAME", result2.getData("USER_NAME",j));
    				parm.addData("REFUSE_AMT", result2.getData("REFUSE_AMT",j));
    			}
    		}
    	}
    	for (int i = 0; i < result1.getCount(); i++) {
    		result1.addData("PARM1", 0.00);
    		result1.addData("PARM2", 0.00);
    		result1.addData("PARM3", 0.00);
    		result1.addData("PARM4", 0.00);
    		result1.addData("PARM5", 0.00);
    		result1.addData("PARM6", 0.00);
    		result1.addData("PARM7", 0.00);
    		result1.addData("PARM8", 0.00);
    		result1.addData("PARM9", 0.00);
    		double refuseAmt = result1.getDouble("SUM(TT.REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(TT.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		for (int j = 0; j < parm.getCount("TYPE"); j++) {
    			if(result1.getData("USER_NAME", i).equals(parm.getData("USER_NAME", j))){
    				result1.setData(parm.getData("TYPE", j).toString(),i, parm.getDouble("REFUSE_AMT", j));
    			}	
    		}
    		
    	}
    	return result1;
    }
    /**
     * 医保住院病人某项费用范围各单项目拒付明细表
     * @param startyearmon
     * @param endyearmon
     * @return
     */
    private TParm query9(String startyearmon,String endyearmon){
    	String sql = 
    		"SELECT   AA.NHI_ORD_CLASS_CODE, COUNT(AA.ADM_SEQ), SUM(AA.TOTAL_AMT), " +
    		" SUM(AA.TOTAL_NHI_AMT), SUM(AA.REFUSE_AMT), '' AS SPACE1" +
    		" FROM (SELECT   A.NHI_ORD_CLASS_CODE, A.ADM_SEQ, " +
    		" SUM (A.TOTAL_AMT) TOTAL_AMT,  SUM (A.TOTAL_NHI_AMT) TOTAL_NHI_AMT, " +
    		" SUM (REFUSE_AMT) REFUSE_AMT, '' " +
    		" FROM INS_IBSORDER_DOWNLOAD A LEFT JOIN INS_IBS B " +
    		" ON A.ADM_SEQ = B.ADM_SEQ AND A.REGION_CODE = B.REGION_CODE " +
    		" WHERE B.YEAR_MON >= '"+startyearmon+"' AND B.YEAR_MON <= '"+endyearmon+"' " +
    		" GROUP BY A.ADM_SEQ, A.NHI_ORD_CLASS_CODE " +
    		" HAVING SUM (REFUSE_AMT) <> 0) AA " +
    		" GROUP BY AA.NHI_ORD_CLASS_CODE";
    	TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result1.getErrCode()<0){
    		messageBox(result1.getErrText());
    		return result1;
    	}
    	if(result1.getCount()<0){
    		messageBox("查无数据");
    		return result1;
    	}
    	String nhiOrdClassCode = "";
    	for (int i = 0; i < result1.getCount(); i++) {
    		double refuseAmt = result1.getDouble("SUM(AA.REFUSE_AMT)", i);//拒付金额
    		double totalNhiAmt = result1.getDouble("SUM(AA.TOTAL_NHI_AMT)", i);//申请金额
    		double refuse = (double)refuseAmt/totalNhiAmt*100;//拒付率
    		result1.setData("SPACE1", i, refuse);
    		nhiOrdClassCode = result1.getData("NHI_ORD_CLASS_CODE", i).toString();
//    		[[id,name],[01,药品费],[02,检查费],[03,治疗费],[04,手术费],[05,床位费],[06,材料费],[07,其他费],[08,全血费],[09,成分血费]]
    		if(nhiOrdClassCode.equals("01")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"药品费");
    		}
    		if(nhiOrdClassCode.equals("02")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"检查费");
    		}
    		if(nhiOrdClassCode.equals("03")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"治疗费");
    		}
    		if(nhiOrdClassCode.equals("04")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"手术费");
    		}
    		if(nhiOrdClassCode.equals("05")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"床位费");
    		}
    		if(nhiOrdClassCode.equals("06")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"材料费");
    		}
    		if(nhiOrdClassCode.equals("07")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"其他费");
    		}
    		if(nhiOrdClassCode.equals("08")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"全血费");
    		}
    		if(nhiOrdClassCode.equals("09")){
    			result1.setData("NHI_ORD_CLASS_CODE", i,"成分血费");
    		}
    	}
    	return result1;
    }
    /**
     * 汇出Excel
     */
    public void onExport() {
    	table = (TTable)this.getComponent("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "医保拒付表");
    }

}
