package com.javahis.ui.ins;

import java.text.DecimalFormat;
import java.util.Vector;

import jdo.ins.INSTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 医保考核报表</p>
 *
 * <p>Description:医保考核报表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedAssessmentReportControl extends TControl {
	
	private TRadioButton controlIndexStatistic ;//某年医保住院考核控制指标统计表
	
	private TRadioButton controlIndexFinish ;//科室医保住院考核控制指标完成情况统计表
	
	private TRadioButton controlIndexDetail ;//医保住院病人考核控制指标明细表
	
	private TTextFormat year ;
	
	private TTextFormat start1 ;
	
	private TTextFormat end1 ;
	
	private TTextFormat start2 ;
	
	private TTextFormat end2 ;
	
	private TTable tTable ;
	
	private int mark = 1 ;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		this.controlIndexStatistic = (TRadioButton) getComponent("CONTROLINDEXSTATISTIC") ;
		this.controlIndexFinish = (TRadioButton) getComponent("CONTROLINDEXFINISH") ;
		this.controlIndexDetail = (TRadioButton) getComponent("CONTROLINDEXDETAIL") ;
		
		this.year = (TTextFormat) getComponent("YEAR") ;
		this.start1 = (TTextFormat) getComponent("START1") ;
		this.end1 = (TTextFormat) getComponent("ENDDATE1") ;
		this.start2 = (TTextFormat) getComponent("START2") ;
		this.end2 = (TTextFormat) getComponent("ENDDATE2") ;
		
		this.tTable = (TTable) getComponent("TTABLE") ;
		
		this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		TParm parm = new TParm() ;
        if(this.mark == 1){//某年医保住院考核控制指标统计表
        	String year = this.getValueString("YEAR") ;
        	if("".equals(year)){
        		messageBox("请输入查询起年.") ;
        		return ;
        	}
        	String[] yearArray = year.split("-") ;
        	//所占比例
            String sql1 = "SELECT   AA.YEAR_MON, COUNT (AA.ADM_SEQ) AS PER,"+
						         "SUM (AA.TOTAL_AMT) / COUNT (AA.ADM_SEQ) AS TOTAL_AMT,"+
						         "SUM (TOTAL_NHI_AMT) / COUNT (AA.ADM_SEQ) AS TOTAL_NHI_AMT,"+
						         "SUM (AA.OWN_AMT) / COUNT (AA.ADM_SEQ) AS OWN_AMT,"+
						         "SUM (AA.APPLY_AMT) / COUNT (AA.ADM_SEQ) AS APPLY_AMT,"+
						         "SUM (AA.HOSP_APPLY_AMT) / COUNT (AA.ADM_SEQ) AS HOSP_APPLY_AMT,"+
						         "SUM (AA.INDAY) / COUNT (AA.ADM_SEQ) AS DAYS, SUM (AA.TOTAL_AMT) T_TOTAL_AMT"+
					      " FROM (" +
					            "SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
                                          "SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
                                          "SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
                                          " TO_CHAR (B.DS_DATE, 'YYYYMMDD')- TO_CHAR (B.IN_DATE, 'YYYYMMDD') AS INDAY"+
                                " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B"+
                                " WHERE A.REGION_CODE = B.REGION_CODE"+
                                       " AND A.ADM_SEQ = B.ADM_SEQ"+
                                       " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                       " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                       " GROUP BY B.YEAR_MON,B.IN_DATE,B.DS_DATE,A.ADM_SEQ,B.APPLY_AMT,HOSP_APPLY_AMT) AA"+
                          " GROUP BY AA.YEAR_MON"	 ;
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    		if (result1.getErrCode() < 0) {
    			messageBox(result1.getErrText());
    			return;
    		}
    		if (result1.getCount() <= 0) {
    			messageBox("查无数据");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		}    
    		//住院人次
    		String sql2 = "SELECT   AA.YEAR_MON, COUNT (AA.ADM_SEQ) AS ADM_SEQ_NUM"+
    					  " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, B.SOURCE_CODE"+
    					  			" FROM INS_IBSORDER_DOWNLOAD A,INS_IBS B"+
    					  			" WHERE A.REGION_CODE = B.REGION_CODE"+
    					  			" AND A.ADM_SEQ = B.ADM_SEQ"+
    					  			" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  			" AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
    					  			" AND B.SOURCE_CODE IN ('1', '2')"+
    					  			" GROUP BY B.YEAR_MON, A.ADM_SEQ, B.SOURCE_CODE) AA "+
    					 " GROUP BY AA.YEAR_MON" ;
    		 TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		 //药品金额合计
    		 String sql3 = " SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    		 			   " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
                                   " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B"+
                                   " WHERE A.REGION_CODE = B.REGION_CODE"+
                                   " AND A.ADM_SEQ = B.ADM_SEQ"+
                                   " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                   " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                   " AND A.NHI_ORD_CLASS_CODE = '01'"+
                                   " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA "+
                           " GROUP BY AA.YEAR_MON" ;
    		 TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));    	
    		 
    		 //贵重药品金额合计
    		 String sql4 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM" +
    		 			   " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT" +
                                "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B " +
                                " WHERE A.REGION_CODE = B.REGION_CODE " +
                                "  AND A.ADM_SEQ = B.ADM_SEQ " +
                                "  AND A.REGION_CODE = '"+Operator.getRegion()+"'" +
                                "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'" +
                                "  AND A.NHI_ORD_CLASS_CODE = '01'" +
                                "  AND A.PRICE >= 100" +
                                " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA" +
                           " GROUP BY AA.YEAR_MON" ;
    		 TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));  

    		 //材料费金额合计
    		 String sql5 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM"+
    		 				" FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
    		 					   "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
    		 					   " WHERE A.REGION_CODE = B.REGION_CODE "+
                                   "  AND A.ADM_SEQ = B.ADM_SEQ "+
                                   "  AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                   "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"' "+
                                   "  AND A.NHI_ORD_CLASS_CODE = '06'"+
                                   "  GROUP BY B.YEAR_MON, A.ADM_SEQ) AA"+
                           " GROUP BY AA.YEAR_MON" ;

    		 TParm result5 = new TParm(TJDODBTool.getInstance().select(sql5));  
    		 
    		 //高价材料费金额合计
    		 String sql6 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
                           " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
                                  "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
                                  " WHERE A.REGION_CODE = B.REGION_CODE "+
                                    "  AND A.ADM_SEQ = B.ADM_SEQ "+
                                    "  AND A.REGION_CODE = '"+Operator.getRegion()+"' "+
                                    "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                    "  AND A.NHI_ORD_CLASS_CODE = '06'"+
                                    "  AND A.PRICE >= 500"+
                                 "  GROUP BY B.YEAR_MON, A.ADM_SEQ) AA "+
                           " GROUP BY AA.YEAR_MON" ;

    		 TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6)); 
    		 
    		 //检查费金额合计
    		 String sql7 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
                           " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
                                    " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
                                    " WHERE A.REGION_CODE = B.REGION_CODE "+
                                     " AND A.ADM_SEQ = B.ADM_SEQ "+
                                     " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                     " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"' "+
                                     " AND A.NHI_ORD_CLASS_CODE = '02' "+
                                     " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA"+
                         " GROUP BY AA.YEAR_MON" ;

    		 TParm result7 = new TParm(TJDODBTool.getInstance().select(sql7)); 
    		 DecimalFormat df = new DecimalFormat("##########0.00"); 
    		 for (int i = 0; i < result1.getCount(); i++) {
    			 String yearMonth = result1.getValue("YEAR_MON", i) ;
    			 int per = result1.getInt("PER",i) ;//总人次
    			 int zyrc = 0 ;
    			 double ypje = 0 ;
    			 double gzhypje = 0 ;//贵重药品金额
    			 double clf = 0 ;//材料费
    			 double gjclf = 0 ;//高价材料费
    			 double jcf = 0 ;//检查费
    			 double tTotalAmt= result1.getDouble("T_TOTAL_AMT",i) ;//总额
    			 
    			 //处理住院人次
    			 for(int j = 0 ;i<result2.getCount() ;i++){
    				 if(result2.getValue("YEAR_MON",i).equals(yearMonth)){
    					 zyrc = result2.getInt("ADM_SEQ_NUM", i) ;
    					 break ;
    				 }
    			 }
    			 //药品金额
    			 for(int j = 0 ;i<result3.getCount() ;i++){
    				 if(result3.getValue("YEAR_MON",i).equals(yearMonth)){
    					 ypje = result3.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }   
    			 //贵重药品金额
    			 for(int j = 0 ;i<result4.getCount() ;i++){
    				 if(result4.getValue("YEAR_MON",i).equals(yearMonth)){
    					 gzhypje = result4.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //材料费
    			 for(int j = 0 ;i<result5.getCount() ;i++){
    				 if(result5.getValue("YEAR_MON",i).equals(yearMonth)){
    					 clf = result5.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }    			 
    			 //高价材料费
    			 for(int j = 0 ;i<result6.getCount() ;i++){
    				 if(result6.getValue("YEAR_MON",i).equals(yearMonth)){
    					 gjclf = result6.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }  
    			 //检查费
    			 for(int j = 0 ;i<result7.getCount() ;i++){
    				 if(result7.getValue("YEAR_MON",i).equals(yearMonth)){
    					 jcf = result7.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }   			 
    			 
    			 //处理治愈好转率
    			 double zyhzhlv = (per == 0) ? 0 : (per - zyrc)/per ;
    			 //药品费占医疗总费用比例
    			 double ypf = (tTotalAmt == 0) ? 0 : ypje/tTotalAmt ;
    			 //贵重药品占药品费比例
    			 double gzhyp = (tTotalAmt == 0) ? 0 : gzhypje/tTotalAmt ;
    			 //材料费占医疗费比例
    			 double clfbl = (tTotalAmt == 0) ? 0 : clf/tTotalAmt ;
    			 //高价材料费占医疗费比例
    			 double gjclfbl = (tTotalAmt == 0) ? 0 : gjclf/tTotalAmt ;
    			 //检查费占医疗费比例
    			 double jcfbl = (tTotalAmt == 0) ? 0 : jcf/tTotalAmt ;
    			 
    			 parm.addData("YEAR_MON", yearMonth.substring(0,4)) ;//月份
    			 parm.addData("ADM_SEQ_NUM", zyrc) ;//住院人次
    			 parm.addData("TOTAL_AMT", df.format(result1.getDouble("TOTAL_AMT", i))) ;//次均发生额
    			 parm.addData("TOTAL_NHI_AMT",df.format(result1.getDouble("TOTAL_NHI_AMT", i))) ;//次均申报额
    			 parm.addData("OWN_AMT", df.format(result1.getDouble("OWN_AMT", i))) ;//次均自负额
    			 parm.addData("APPLY_AMT",df.format(result1.getDouble("APPLY_AMT", i))) ;//次均统筹金额
    			 parm.addData("HOSP_APPLY_AMT", df.format(result1.getDouble("HOSP_APPLY_AMT", i))) ;//次均救助额
    			 parm.addData("DAYS", result1.getData("DAYS", i)) ;//次均住院天数.
    			 
    			 parm.addData("ZHYHZHLV", df.format(zyhzhlv)) ;//治愈好转率
    			 parm.addData("YPFYL", df.format(ypf)) ;//药品费占医疗总费用比例
    			 parm.addData("GZHYP", df.format(gzhyp)) ;//贵重药品占药品费比例
    			 parm.addData("CLFYP", df.format(clfbl)) ;//材料费占医疗费比例
    			 parm.addData("GJCLFBL", df.format(gjclfbl)) ;//高价材料费占医疗费比例
    			 parm.addData("JCFBL", df.format(jcfbl)) ;//检查费占医疗费比例 
			}
    		 
        }else if(this.mark == 2){//科室医保住院考核控制指标完成情况统计表
        	
        	String startDate = this.getValueString("START1") ;
        	String endDate = this.getValueString("ENDDATE1") ;
        	if("".equals(startDate)){
        		messageBox("请输入查询起日.") ;
        		return ;
        	}
        	if("".equals(endDate)){
        		messageBox("请输入查询讫日.") ;
        		return ;       		
        	}    
        	
        	String sql1 = "SELECT   AA.DEPT_CHN_DESC, COUNT (AA.ADM_SEQ) PER,"+
        							"SUM (AA.TOTAL_AMT) / COUNT (AA.ADM_SEQ) TOTAL_AMT,"+
        							"SUM (TOTAL_NHI_AMT) / COUNT (AA.ADM_SEQ) TOTAL_NHI_AMT,"+
        							"SUM (AA.OWN_AMT) / COUNT (AA.ADM_SEQ) OWN_AMT,"+
        							"SUM (AA.APPLY_AMT) / COUNT (AA.ADM_SEQ) APPLY_AMT,"+
        							"SUM (AA.HOSP_APPLY_AMT) / COUNT (AA.ADM_SEQ) HOSP_APPLY_AMT,"+
        							"SUM (AA.INDAY) / COUNT (AA.ADM_SEQ) AS DAYS, SUM (AA.TOTAL_AMT) T_TOTAL_AMT "+
        				 " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
        				 				"  SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
        				 				"  SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
        				 				"   TO_CHAR (B.DS_DATE, 'YYYYMMDD') - TO_CHAR (B.IN_DATE, 'YYYYMMDD') AS INDAY"+
        				 		" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,  SYS_DEPT D "+
        				 		" WHERE A.REGION_CODE = B.REGION_CODE "+
        				 			" AND A.ADM_SEQ = B.ADM_SEQ"+
        				 			" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
        				 			" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
        				 			" AND B.REGION_CODE = C.REGION_CODE"+
        				 			" AND B.CASE_NO = C.CASE_NO"+
        				 			" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
        				 		" GROUP BY D.DEPT_CHN_DESC,B.IN_DATE,B.DS_DATE,A.ADM_SEQ,B.APPLY_AMT,HOSP_APPLY_AMT) AA"+
        				 " GROUP BY AA.DEPT_CHN_DESC" ;
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    		if (result1.getErrCode() < 0) {
    			messageBox(result1.getErrText());
    			return;
    		}
    		if (result1.getCount() <= 0) {
    			messageBox("查无数据");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		} 
    		//住院人次
    		String sql2 = "SELECT   AA.DEPT_CHN_DESC, COUNT (AA.ADM_SEQ) AS ADM_SEQ_NUM"+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, B.SOURCE_CODE"+
    					  		 " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,  SYS_DEPT D"+
    					  		 " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  		 "  AND A.ADM_SEQ = B.ADM_SEQ"+
    					  		 "  AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  		 "  AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  		 "  AND B.REGION_CODE = C.REGION_CODE"+
    					  		 "  AND B.CASE_NO = C.CASE_NO"+
    					  		 "  AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  		 "  AND B.SOURCE_CODE IN ('1', '2')"+
    					  		 " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ, B.SOURCE_CODE) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		
    		//药品金额合计
    		String sql3 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  	      "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D"+
    					  	      " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  	        " AND A.ADM_SEQ = B.ADM_SEQ"+
    					  	        " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  	        " AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  	        " AND B.REGION_CODE = C.REGION_CODE"+
    					  	        " AND B.CASE_NO = C.CASE_NO"+
    					  	        " AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  	        " AND A.NHI_ORD_CLASS_CODE = '01'"+
    					  	      " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					 " GROUP BY AA.DEPT_CHN_DESC;" ;
    		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
    		
    		//贵重药品金额合计
    		String sql4 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM"+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  			" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D"+
    					  			" WHERE A.REGION_CODE = B.REGION_CODE"+
    					  			  " AND A.ADM_SEQ = B.ADM_SEQ"+
    					  			  " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  			  " AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  			  " AND B.REGION_CODE = C.REGION_CODE"+
    					  			  " AND B.CASE_NO = C.CASE_NO"+
    					  			  " AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  			  " AND A.NHI_ORD_CLASS_CODE = '01'"+
    					  			  " AND A.PRICE >= 100"+
    					  			" GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));
    		
    		//.材料费金额合计
    		String sql5 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  		  " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D"+
    					  		  " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  		  	" AND A.ADM_SEQ = B.ADM_SEQ"+
    					  		  	" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  		  	" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  		  	" AND B.REGION_CODE = C.REGION_CODE"+
    					  		  	" AND B.CASE_NO = C.CASE_NO"+
    					  		  	" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  		  	" AND A.NHI_ORD_CLASS_CODE = '06'"+
    					  		  " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result5 = new TParm(TJDODBTool.getInstance().select(sql5));
    		//高价材料费金额合计
    		String sql6 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM " +
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT" +
    					  		  " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D" +
    					  		  " WHERE A.REGION_CODE = B.REGION_CODE" +
    					  		  	" AND A.ADM_SEQ = B.ADM_SEQ" +
    					  		  	" AND A.REGION_CODE = '"+Operator.getRegion()+"'" +
    					  		  	" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'" +
    					  		  	" AND B.REGION_CODE = C.REGION_CODE" +
    					  		  	" AND B.CASE_NO = C.CASE_NO" +
    					  		  	" AND C.DS_DEPT_CODE = D.DEPT_CODE" +
    					  		  	" AND A.NHI_ORD_CLASS_CODE = '06'" +
    					  		  	" AND A.PRICE >= 500" +
    					  		 " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA" +
    					 " GROUP BY AA.DEPT_CHN_DESC" ;	
    		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
    		
    		//检查费金额合计
    		String sql7 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  "  FROM (   SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
    					  			"  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D "+
    					  			"  WHERE A.REGION_CODE = B.REGION_CODE "+
    					  				"  AND A.ADM_SEQ = B.ADM_SEQ "+
    					  				"  AND A.REGION_CODE = '"+Operator.getRegion()+"' "+
    					  				"  AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"' "+
    					  				"  AND B.REGION_CODE = C.REGION_CODE "+
    					  				"  AND B.CASE_NO = C.CASE_NO "+
    					  				"  AND C.DS_DEPT_CODE = D.DEPT_CODE "+
    					  				"  AND A.NHI_ORD_CLASS_CODE = '02'"+
    					  			" GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result7 = new TParm(TJDODBTool.getInstance().select(sql7));
    		DecimalFormat df = new DecimalFormat("##########0.00"); 
    		for (int i = 0; i < result1.getCount(); i++) {
    			 String deptChnDesc = result1.getValue("DEPT_CHN_DESC", i) ;
    			 
    			 int per = result1.getInt("PER",i) ;//总人次
    			 int zyrc = 0 ;
    			 
    			 double ypje = 0 ;
    			 double gzhypje = 0 ;//贵重药品金额
    			 double clf = 0 ;//材料费
    			 double gjclf = 0 ;//高价材料费
    			 double jcf = 0 ;//检查费
    			 double tTotalAmt= result1.getDouble("T_TOTAL_AMT",i) ;//总额    			 
    			 
    			 //处理住院人次
    			 for(int j = 0 ;i<result2.getCount() ;i++){
    				 if(result2.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 zyrc = result2.getInt("ADM_SEQ_NUM", i) ;
    					 break ;
    				 }
    			 }  
    			 //药品金额
    			 for(int j = 0 ;i<result3.getCount() ;i++){
    				 if(result3.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 ypje = result3.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //贵重药品金额
    			 for(int j = 0 ;i<result4.getCount() ;i++){
    				 if(result4.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 gzhypje = result4.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //材料费
    			 for(int j = 0 ;i<result5.getCount() ;i++){
    				 if(result5.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 clf = result5.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }
    			 //高价材料费
    			 for(int j = 0 ;i<result6.getCount() ;i++){
    				 if(result6.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 gjclf = result6.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }
    			 //检查费
    			 for(int j = 0 ;i<result7.getCount() ;i++){
    				 if(result7.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 jcf = result7.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }     			 
    			 
    			 //处理治愈好转率
    			 double zyhzhlv = (per == 0) ? 0 : (per - zyrc)/per ;
    			 //药品费占医疗总费用比例
    			 double ypf = (tTotalAmt == 0) ? 0 : ypje/tTotalAmt ;
    			 //贵重药品占药品费比例
    			 double gzhyp = (tTotalAmt == 0) ? 0 : gzhypje/tTotalAmt ;
    			 //材料费占医疗费比例
    			 double clfbl = (tTotalAmt == 0) ? 0 : clf/tTotalAmt ; 
    			 //高价材料费占医疗费比例
    			 double gjclfbl = (tTotalAmt == 0) ? 0 : gjclf/tTotalAmt ;  
    			 //检查费占医疗费比例
    			 double jcfbl = (tTotalAmt == 0) ? 0 : jcf/tTotalAmt ;    			 
    			 
    			 //DEPT_CHN_DESC;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL
    			 parm.addData("DEPT_CHN_DESC", result1.getValue("DEPT_CHN_DESC",i)) ;//科室
    			 parm.addData("ADM_SEQ_NUM", zyrc) ;//住院人次
    			 parm.addData("TOTAL_AMT", df.format(result1.getDouble("TOTAL_AMT", i))) ;//次均发生额
    			 parm.addData("TOTAL_NHI_AMT", df.format(result1.getDouble("TOTAL_NHI_AMT", i))) ;//次均申报额
    			 parm.addData("OWN_AMT", df.format(result1.getDouble("OWN_AMT", i))) ;//次均自负额
    			 parm.addData("APPLY_AMT",df.format(result1.getDouble("APPLY_AMT", i))) ;//次均统筹金额
    			 parm.addData("HOSP_APPLY_AMT", df.format(result1.getDouble("HOSP_APPLY_AMT", i))) ;//次均救助额
    			 parm.addData("DAYS", result1.getData("DAYS", i)) ;//次均住院天数.
    			 
    			 parm.addData("ZHYHZHLV", df.format(zyhzhlv)) ;//治愈好转率
    			 parm.addData("YPFYL", df.format(ypf)) ;//药品费占医疗总费用比例
    			 parm.addData("GZHYP", df.format(gzhyp)) ;//贵重药品占药品费比例
    			 parm.addData("CLFYP", df.format(clfbl)) ;//材料费占医疗费比例
    			 parm.addData("GJCLFBL", df.format(gjclfbl)) ;//高价材料费占医疗费比例
    			 parm.addData("JCFBL", df.format(jcfbl)) ;//检查费占医疗费比例 
    			 
    		}
    		
        }else if(this.mark == 3){//医保住院病人考核控制指标明细表
        	String startDate = this.getValueString("START2") ;
        	String endDate = this.getValueString("ENDDATE2") ;
        	if("".equals(startDate)){
        		messageBox("请输入查询起日.") ;
        		return ;
        	}
        	if("".equals(endDate)){
        		messageBox("请输入查询讫日.") ;
        		return ;       		
        	}
        	//PAT_NAME;CASE_NO;DEPT_CHN_DESC;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;INDAY INDAY;TOT_PHA_AMT;TOT_PHA_HPRICE;TOT_MATERIAL_AMT;TOT_MATERIAL_HPRICE;TOT_EXM_AMT
        	String sql = "SELECT   AA.PAT_NAME, AA.CASE_NO, AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) TOTAL_AMT,"+
        						"SUM (AA.TOTAL_NHI_AMT) TOTAL_NHI_AMT, SUM (AA.OWN_AMT) OWN_AMT,"+
        						"SUM (AA.APPLY_AMT) APPLY_AMT, SUM (AA.HOSP_APPLY_AMT) HOSP_APPLY_AMT,"+
        						"AA.INDAY INDAY, SUM (AA.TOT_PHA_AMT) TOT_PHA_AMT,"+
        						"SUM (AA.TOT_PHA_HPRICE) TOT_PHA_HPRICE,"+
        						"SUM (AA.TOT_MATERIAL_AMT) TOT_MATERIAL_AMT,"+
        						"SUM (AA.TOT_MATERIAL_HPRICE) TOT_MATERIAL_HPRICE,"+
        						"SUM (AA.TOT_EXM_AMT) TOT_EXM_AMT"+
        						" FROM (SELECT   B.PAT_NAME, B.CASE_NO, D.DEPT_CHN_DESC,"+
        									" SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
        									" SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
        									" SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
        									"  TO_CHAR (B.DS_DATE, 'YYYYMMDD') - TO_CHAR (B.IN_DATE, 'YYYMMDD') INDAY,"+
        									" CASE A.NHI_ORD_CLASS_CODE"+
        									"   WHEN '01'"+
        									"     THEN SUM (A.TOTAL_AMT)"+
        									"  ELSE 0"+
        									" END TOT_PHA_AMT,"+
        									" CASE"+
        									"   WHEN A.NHI_ORD_CLASS_CODE = '01'"+
        									"   AND A.PRICE >= 100"+
        									"     THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_PHA_HPRICE,"+
        									" CASE"+
        									"  WHEN A.NHI_ORD_CLASS_CODE = '06'"+
        									"    THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_MATERIAL_AMT,"+
        									" CASE"+
        									"   WHEN A.NHI_ORD_CLASS_CODE = '06'"+
        									"  AND A.PRICE >= 500"+
        									"   THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_MATERIAL_HPRICE,"+
        									" CASE"+
        									"  WHEN A.NHI_ORD_CLASS_CODE = '02'"+
        									"   THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_EXM_AMT"+
        							" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D"+
        							" WHERE A.REGION_CODE = B.REGION_CODE"+
        								" AND A.ADM_SEQ = B.ADM_SEQ"+
        								" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
        								" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
        								" AND B.REGION_CODE = C.REGION_CODE"+
        								" AND B.CASE_NO = C.CASE_NO"+
        								" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
        							" GROUP BY B.PAT_NAME, B.CASE_NO, D.DEPT_CHN_DESC, A.ADM_SEQ, B.IN_DATE, B.DS_DATE, B.APPLY_AMT,HOSP_APPLY_AMT,  A.NHI_ORD_CLASS_CODE, A.PRICE) AA"+
        				" GROUP BY AA.PAT_NAME, AA.INDAY, AA.DEPT_CHN_DESC, AA.CASE_NO" ;
        	parm = new TParm(TJDODBTool.getInstance().select(sql)); 
        	
    		if (parm.getErrCode() < 0) {
    			messageBox(parm.getErrText());
    			return;
    		}
    		if (parm.getCount() <= 0) {
    			messageBox("查无数据");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		}        	
        }	
        this.callFunction("UI|TTABLE|setParmValue",parm);
	}
	
	/**
	 * 
	 * 转换时间.
	 * @param date
	 * @return
	 */
	private String transferDate(String date){
		String[] dateArray = date.split(" ") ;
		String[] newDateArray = dateArray[0].split("-") ;
		return newDateArray[0]+newDateArray[1]+newDateArray[2] ;
	}	
	
	/**
	 * 清空
	 */
	public void onClear(){
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
		this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
		this.start1.setValue("") ;
		this.end1.setValue("") ;
		this.start2.setValue("") ;
		this.end2.setValue("") ;	

		this.year.setEnabled(true) ;
		this.start1.setEnabled(false) ;
		this.end1.setEnabled(false) ;
		this.start2.setEnabled(false) ;
		this.end2.setEnabled(false) ;
		this.mark = 1 ;
	}
	
	/**
	 * 汇出
	 */
	public void onExport(){
    	TTable table = (TTable)this.getComponent("TTABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        } 
        String title = null ;
        if(this.mark == 1){
        	title = "某年医保住院考核控制指标统计表" ;
        }else if(this.mark == 2){
        	title = "科室医保住院考核控制指标完成情况统计表" ;
        }else if(this.mark == 3){
        	title = "医保住院病人考核控制指标明细表" ;
        }
        ExportExcelUtil.getInstance().exportExcel(table, title);		
	}
	
	/**
	 * 单击单选钮.
	 */
	public void onRadioClick(){
		
		if(controlIndexStatistic.hasFocus()){//某年医保住院考核控制指标统计表
			this.year.setEnabled(true) ;
			this.start1.setEnabled(false) ;
			this.end1.setEnabled(false) ;
			this.start2.setEnabled(false) ;
			this.end2.setEnabled(false) ;
			this.mark = 1 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
			this.start1.setValue("") ;
			this.end1.setValue("") ;
			this.start2.setValue("") ;
			this.end2.setValue("") ;			
			this.tTable.setHeader("月份,120;住院人次,120;次均发生额,120,double,#########0.00;次均申报额,120,double,#########0.00;次均自付金额,120,double,#########0.00;次均统筹金额,120,double,#########0.00;次均救助金额,120,double,#########0.00;次均住院天数,120;治愈好转率,120,double,#########0.00;药品费占医疗总费用比例,150,double,#########0.00;贵重药品占药品费比例,150,double,#########0.00;材料费占医疗费比例,150,double,#########0.00;高价材料费占医疗费比例,150,double,#########0.00;检查费占医疗费比例,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
			this.tTable.setParmMap("YEAR_MON;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL") ;
		}else if(this.controlIndexFinish.hasFocus()){//科室医保住院考核控制指标完成情况统计表
			this.year.setEnabled(false) ;
			this.start1.setEnabled(true) ;
			this.end1.setEnabled(true) ;
			this.start2.setEnabled(false) ;
			this.end2.setEnabled(false) ;
			this.mark = 2 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue("") ;
			this.start2.setValue("") ;
			this.end2.setValue("") ;
			this.start1.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.end1.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.tTable.setHeader("科室,120;住院人次,120;次均发生额,120,double,#########0.00;次均申报额,120,double,#########0.00;次均自付金额,120,double,#########0.00;次均统筹金额,120,double,#########0.00;次均救助金额,120,double,#########0.00;次均住院天数,120;治愈好转率,120,double,#########0.00;药品费占医疗总费用比例,150,double,#########0.00;贵重药品占药品费比例,150,double,#########0.00;材料费占医疗费比例,150,double,#########0.00;高价材料费占医疗费比例,150,double,#########0.00;检查费占医疗费比例,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
			this.tTable.setParmMap("DEPT_CHN_DESC;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL") ;
		}else if(this.controlIndexDetail.hasFocus()){//医保住院病人考核控制指标明细表
			this.year.setEnabled(false) ;
			this.start1.setEnabled(false) ;
			this.end1.setEnabled(false) ;
			this.start2.setEnabled(true) ;
			this.end2.setEnabled(true) ;
			this.mark = 3 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue("") ;
			this.start1.setValue("") ;
			this.end1.setValue("") ;
			this.start2.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.end2.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			
			this.tTable.setHeader("姓名,120;住院号,120;科室,120;发生金额,120,double,#########0.00;申报金额,120,double,#########0.00;自付金额,120,double,#########0.00;统筹金额,120,double,#########0.00;救助金额,120,double,#########0.00;住院天数,120;药品费占医疗总费用比例,150,double,#########0.00;贵重药品占药品费比例,150,double,#########0.00;材料费占医疗费比例,150,double,#########0.00;高价材料费占医疗费比例,150,double,#########0.00;检查费占医疗费比例,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
		    this.tTable.setParmMap("PAT_NAME;CASE_NO;DEPT_CHN_DESC;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;INDAY INDAY;TOT_PHA_AMT;TOT_PHA_HPRICE;TOT_MATERIAL_AMT;TOT_MATERIAL_HPRICE;TOT_EXM_AMT") ;
		}
		
	}

	public TRadioButton getControlIndexStatistic() {
		return controlIndexStatistic;
	}

	public void setControlIndexStatistic(TRadioButton controlIndexStatistic) {
		this.controlIndexStatistic = controlIndexStatistic;
	}

	public TRadioButton getControlIndexFinish() {
		return controlIndexFinish;
	}

	public void setControlIndexFinish(TRadioButton controlIndexFinish) {
		this.controlIndexFinish = controlIndexFinish;
	}

	public TRadioButton getControlIndexDetail() {
		return controlIndexDetail;
	}

	public void setControlIndexDetail(TRadioButton controlIndexDetail) {
		this.controlIndexDetail = controlIndexDetail;
	}

	public TTextFormat getYear() {
		return year;
	}

	public void setYear(TTextFormat year) {
		this.year = year;
	}

	public TTextFormat getStart1() {
		return start1;
	}

	public void setStart1(TTextFormat start1) {
		this.start1 = start1;
	}

	public TTextFormat getEnd1() {
		return end1;
	}

	public void setEnd1(TTextFormat end1) {
		this.end1 = end1;
	}

	public TTextFormat getStart2() {
		return start2;
	}

	public void setStart2(TTextFormat start2) {
		this.start2 = start2;
	}

	public TTextFormat getEnd2() {
		return end2;
	}

	public void setEnd2(TTextFormat end2) {
		this.end2 = end2;
	}
	
	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}
}
