package com.javahis.ui.reg;



import java.text.DecimalFormat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: 急诊检伤等级统计
 * Description:急诊检伤等级统计
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class REGErdLevelStatisticsControl extends TControl {
	private TTable table;
	DecimalFormat df1 = new DecimalFormat("##########0.00");
	DecimalFormat df = new DecimalFormat("##########0");
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}
    	if(this.getValue("ORDER_TYPE").equals("")){
    		this.messageBox("查询类型不能为空");
    		return true;
    	}	
	    return false; 
	}
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;   	
    	String sql ="";
    	String sql1 ="";
    	String sql2 ="";
    	String sqlall ="";
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//按检伤等级查询
    	if(this.getValue("ORDER_TYPE").equals("1")){
    	sql1 =" A.ERD_LEVEL,C.LEVEL_DESC,";
    	sql2 =" GROUP BY A.ERD_LEVEL,C.LEVEL_DESC"+
              " ORDER BY A.ERD_LEVEL,C.LEVEL_DESC";
    	}
    	//按收费员查询
    	else if(this.getValue("ORDER_TYPE").equals("2")){
    	sql1 =" B.CASH_CODE," +
    		  " A.ERD_LEVEL,C.LEVEL_DESC,D.USER_NAME,";
    	sql2 =" GROUP BY B.CASH_CODE,A.ERD_LEVEL,C.LEVEL_DESC,D.USER_NAME"+
              " ORDER BY B.CASH_CODE,A.ERD_LEVEL,C.LEVEL_DESC,D.USER_NAME";
    	}
    	//查询总人数
    	sqlall =" SELECT COUNT(A.CASE_NO) AS COUNT_ALL"+
    		" FROM REG_PATADM A,BIL_REG_RECP B"+
    		" WHERE A.ADM_TYPE = 'E'"+
    		" AND A.CASE_NO = B.CASE_NO"+
    		" AND B.AR_AMT>0"+
    		" AND A.ARRIVE_FLG ='Y'"+
    		" AND A.ERD_LEVEL IS NOT NULL"+ 
    		" AND B.CHARGE_DATE  BETWEEN"+
    		" TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
    		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sqlall));
        //查询其他数据
    	sql =" SELECT " +sql1+
            " COUNT(A.CASE_NO) AS LEVEL_COUNT"+
    	    " FROM REG_PATADM A,BIL_REG_RECP B,REG_ERD_LEVEL C,SYS_OPERATOR D"+
    	    " WHERE A.ADM_TYPE = 'E'"+
    	    " AND A.CASE_NO = B.CASE_NO"+
    	    " AND B.AR_AMT>0"+
    	    " AND A.ARRIVE_FLG ='Y'"+
    	    " AND A.ERD_LEVEL IS NOT NULL"+
    	    " AND A.ERD_LEVEL = C.LEVEL_CODE"+
    	    " AND B.CASH_CODE = D.USER_ID"+
    	    " AND B.CHARGE_DATE  BETWEEN"+
    	    " TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
    	    " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+sql2;   		
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}
		
		int count  = result.getCount();
		for (int i = 0; i < count; i++) {
			result.setData("COUNT_ALL", i, df.format(parm.getDouble("COUNT_ALL",0)));
			result.setData("SURPLUS_COUNT", i, df1.format((result.getDouble("LEVEL_COUNT",i)/
					 parm.getDouble("COUNT_ALL",0))*100)+"%");
			
		}		
    	((TTable) getComponent("TABLE")).setParmValue(result);
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	 String title ="急诊检伤等级统计表";			 
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	this.setValue("ORDER_TYPE","1");
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
 	    this.table.setHeader("检伤等级,100;挂号总人数,100;等级人数,100;等级所占比(%),100");
        this.table.setLockColumns("0,1,2,3");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right"); 
        this.table.setParmMap("LEVEL_DESC;COUNT_ALL;LEVEL_COUNT;SURPLUS_COUNT"); 	
    }
    /**
     * 查询类别选择事件
     */
    public void onSelect(){
    	if(this.getValue("ORDER_TYPE").equals("1")){
    	this.table.setHeader("检伤等级,100;挂号总人数,100;等级人数,100;等级所占比(%),100");
        this.table.setLockColumns("0,1,2,3");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right"); 
        this.table.setParmMap("LEVEL_DESC;COUNT_ALL;LEVEL_COUNT;SURPLUS_COUNT"); 	
    	}else{
    	this.table.setHeader("收费员,100;检伤等级,100;挂号总人数,100;" +
    			             "等级人数,100;等级所占比(%),100");
        this.table.setLockColumns("0,1,2,3,4");
        this.table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right"); 
        this.table.setParmMap("USER_NAME;LEVEL_DESC;COUNT_ALL;LEVEL_COUNT;SURPLUS_COUNT");	
    	}
    }
}
