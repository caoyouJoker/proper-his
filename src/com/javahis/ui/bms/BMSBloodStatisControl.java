package com.javahis.ui.bms;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:用血统计报表
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx   20130308
 * @version 4.0
 */
public class BMSBloodStatisControl extends TControl{
	private TTable table ;
	private String date  ; //统计区间
	
	/**
	 * 初始化
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}
	/**
	 * 初始化界面
	 */
	public void onInitPage(){  
		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("S_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //开始时间
		this.setValue("E_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //结束时间
		table = (TTable)this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		if(!this.check()){
			return ;
		}
		String sdate = this.getValueString("S_DATE").substring(0,10).replace("-", "")+"000000" ;
		String edate = this.getValueString("E_DATE").substring(0, 10).replace("-", "")+"235959" ;
		StringBuffer sb  = new StringBuffer() ;
		String sql = "  SELECT  C.DEPT_CHN_DESC,D.STATION_DESC,E.USER_NAME,F.BLDCODE_DESC,sum(A.BLOOD_VOL) BLOOD_VOL," +
				    "   G.UNIT_CHN_DESC     FROM BMS_BLOOD A ,BMS_APPLYM B ,SYS_DEPT C,SYS_STATION D ," +
				    "   SYS_OPERATOR E ,BMS_BLDCODE F ,SYS_UNIT G WHERE      " +
				    "   A.APPLY_NO = B.APPLY_NO   AND A.DEPT_CODE = C.DEPT_CODE" +
				    "  AND A.STATION_CODE =D.STATION_CODE AND B.DR_CODE = E.USER_ID" +
				    "  AND A.BLD_CODE = F.BLD_CODE  AND F.UNIT_CODE =G.UNIT_CODE " +
				    "  AND A.STATE_CODE = '2'   AND   A.OUT_DATE BETWEEN TO_DATE('"+sdate+"', 'YYYYMMDDHH24MISS')   " +
				    "  AND TO_DATE('"+edate+"', 'YYYYMMDDHH24MISS') "  ;
		if(this.getValueString("DEPT_CODE").length()>0){
			sb.append(" AND A.DEPT_CODE = '"+getValueString("DEPT_CODE")+"' ") ;
		}
		if(this.getValueString("USER_NAME").length()>0){
			sb.append(" AND B.DR_CODE= '"+getValueString("USER_NAME")+"' ") ;
		}
		String whereSql = " GROUP BY C.DEPT_CHN_DESC,D.STATION_DESC,E.USER_NAME,F.BLDCODE_DESC,G.UNIT_CHN_DESC " +
				          "  ORDER  BY  C.DEPT_CHN_DESC  "  ;
		if(sb.length()>0)
			sql += sb.toString() +whereSql ;
		else sql += whereSql ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(parm.getErrCode()<0){
			this.messageBox("查无数据") ;
			return  ;
		}
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			this.onClear() ;
			return ;
		}
		table.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("S_DATE"),
		"yyyy/MM/dd ")
		+ " 至 "
		+ StringTool.getString((Timestamp) this.getValue("E_DATE"),
				"yyyy/MM/dd ");

	}
	/**
	 * 校验
	 * @return
	 */
	public boolean check(){
		
		if(this.getValueString("S_DATE")==null || this.getValueString("S_DATE").length()<0){
			this.messageBox("统计时间不能为空") ;
			return false ;
		}
		if(this.getValueString("E_DATE")==null || this.getValueString("E_DATE").length()<0){
			this.messageBox("统计时间不能为空") ;
			return false ;
		}
		return true ;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("DEPT_CODE;USER_NAME") ;
		table.removeRowAll() ;
	}
	
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("DEPT_CHN_DESC")<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//==DEPT_CHN_DESC;STATION_DESC;USER_NAME;BLDCODE_DESC;BLOOD_VOL;UNIT_CHN_DESC
		for(int i=0;i<tableParm.getCount("DEPT_CHN_DESC");i++){
			result.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i)); //赋值 
			result.addData("STATION_DESC", tableParm.getValue("STATION_DESC", i)); 
			result.addData("USER_NAME", tableParm.getValue("USER_NAME", i)); 
			result.addData("BLDCODE_DESC", tableParm.getValue("BLDCODE_DESC", i)); 
			result.addData("BLOOD_VOL", tableParm.getValue("BLOOD_VOL", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i)); 
		}
		result.setCount(tableParm.getCount("DEPT_CHN_DESC")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");//排序
		result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		result.addData("SYSTEM", "COLUMNS", "USER_NAME");
		result.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		result.addData("SYSTEM", "COLUMNS", "BLOOD_VOL");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//制表时间
		printParm.setData("TITLE", "TEXT","科室用血统计报表") ;
		printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\bms\\BMSBloodStatis.jhw", 
				printParm);
	}
	
	
	 /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount("DEPT_CHN_DESC") <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "科室用血统计报表");
    }	
}
