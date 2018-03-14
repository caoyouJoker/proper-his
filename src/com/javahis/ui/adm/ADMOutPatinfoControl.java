package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;

public class ADMOutPatinfoControl extends TControl {
	TTable table;
	
	/**
     * 初始化
     */
	public void onInit(){
		super.onInit();
        table = (TTable)this.getComponent("TABLE");
        initPage();//初始化页面信息
	}
	
	/**
     * 初始化页面信息
     */
    public void initPage() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
    }
    
    /**
     * 清空
     */
    public void onClear(){
    	table.removeRowAll();
    	initPage();
    }
	
    /**
     * 查询
     */
    public void onQuery(){
    	String startDate;
    	String endDate;
    	
    	if (getValue("START_DATE") == null ||getValueString("START_DATE").equals("")){
    		messageBox("请选择开始时间!");
                return;
    	}
    	
        if (getValue("END_DATE") == null ||getValueString("END_DATE").equals("")){
        	messageBox("请选择结束时间!");
            return;
        }
        
        startDate = getValueString("START_DATE").substring(0, 19);
		endDate = getValueString("END_DATE").substring(0, 19);
		startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
		startDate.substring(8, 10) + startDate.substring(11, 13) +
		startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
		endDate.substring(8, 10) + endDate.substring(11, 13) +
		endDate.substring(14, 16) + endDate.substring(17, 19);
		
		String sql = "SELECT A.CASE_NO,A.OUT_DATE,F.STATION_DESC,A.PAT_NAME,"
			+ " CASE A.SEX WHEN '1' THEN '男' WHEN '2' THEN '女' END SEX,A.AGE,A.MR_NO,"
			+ " A.IN_DATE,B.USER_NAME,C.ICD_CHN_DESC,A.OP_CODE,D.OPT_CHN_DESC,E.POST_DESCRIPTION,A.TEL,A.CONT_TEL"
			+ " FROM MRO_RECORD A,SYS_OPERATOR B,SYS_DIAGNOSIS C,SYS_OPERATIONICD D,"
			+ " SYS_POSTCODE E,SYS_STATION F"
			+ " WHERE A.OUT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND "
			+ " TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" 
			+ " AND A.VS_DR_CODE = B.USER_ID AND A.IN_DIAG_CODE = C.ICD_CODE(+)"
			+ " AND A.OP_CODE = D.OPERATION_ICD(+) AND A.H_POSTNO=E.POST_CODE(+)"
			+ " AND A.OUT_STATION = F.STATION_CODE ORDER BY F.STATION_DESC,A.OUT_DATE ASC";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(parm.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
		
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
        
		table.setParmValue(parm);
        
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {
		String title = "客服部出院患者统计";
		if (table.getRowCount() > 0)   		
		ExportExcelUtil.getInstance().exportExcel(table,title);
	}

}
