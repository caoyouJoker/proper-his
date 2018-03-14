package com.javahis.ui.bil;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class BILInvoiceNoControl extends TControl{
	private TTable table;
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		//初始化时间
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE",
	             date.toString().substring(0, 10).
	             replace('-', '/') + " 00:00:00");
		this.setValue("END_DATE",
	             date.toString().substring(0, 10).replace('-', '/') +
	             " 23:59:59");
		table=(TTable)getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		String start=this.getValueString("START_DATE");
		String start_date=start.substring(0, 4)+start.substring(5,7)+start.substring(8,10)+start.substring(11,13)+start.substring(14,16)+start.substring(17,19);
		String end=this.getValueString("END_DATE");
		String end_date=end.substring(0, 4)+end.substring(5,7)+end.substring(8,10)+end.substring(11,13)+end.substring(14,16)+end.substring(17,19);
		TParm result=new TParm(TJDODBTool.getInstance().select(this.getSql(start_date,end_date)));
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有数据");
		}
		table.setParmValue(result);
	}
	
	/**
	 * 获得sql语句
	 * @return
	 */
	public String getSql(String start_date,String end_date){
		String sql="SELECT CASE WHEN recp_type = 'OPB' THEN '门诊' ELSE '挂号' END recp_type,"
         +" start_invno, "
         +" end_invno, "
         +" update_no, "
         +" user_name, "
         +" start_valid_date, "
         +" end_valid_date, "
         +" CASE "
         +"    WHEN status = 0 THEN '使用中' "
         +"    WHEN status = 1 THEN '领用' "
         +"    WHEN status = 2 THEN '交回' "
         +"    WHEN status = 3 THEN '确认交回' "
         +" END status "
         +" FROM bil_invoice a, sys_operator b "
         +" WHERE     a.cashier_code = b.user_id "
         +" AND START_VALID_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS') "
         +"                          AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') "
         +" AND RECP_TYPE <> 'IBS' "
         +" ORDER BY RECP_TYPE, START_VALID_DATE ";
		//System.out.println("sql:::"+sql);
		return sql;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("START_DATE;END_DATE");
		table.removeRowAll();
	}
	
	/**
     * 导出excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "票号管理");
    }
}
