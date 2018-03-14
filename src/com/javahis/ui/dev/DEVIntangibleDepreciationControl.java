package com.javahis.ui.dev;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.tiis.util.TiMath;
/**
 * <p>
 * Title:无形资产摊销计算表
 * </p>
 *
 * <p>
 * Description:无形资产摊销计算表
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p>
 *
 * @author fux 2013.09.23
 * @version 1.0
 */
public class DEVIntangibleDepreciationControl extends TControl{
	
	private TTable table;
	
	public DEVIntangibleDepreciationControl(){
		super();
	}
	/**
	 * 初始化方法
	 */ 
	public void onInit(){
		initPage();
	}
	private void initPage(){ 
		table = (TTable) this.getComponent("TABLE");
		/**  日期：默认当前年月 格式：YYYY/MM  **/
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
		"yyyyMM");
		this.setValue("Q_DATE", StringTool.getTimestamp(now, "yyyyMM"));// 查询日期
	}
	
	/**
	 * 查询方法
	 */
	public void onQuery(){ 
		
//		if(devKind.length() == 0){
//			messageBox("财务类别不能为空!");
//			return;
//		} 
		String qdate = StringTool.getString(TypeTool
				.getTimestamp(getValue("Q_DATE")), "yyyy-MM");
		if (qdate.length() == 0) {
			messageBox("日期不正确!"); 
			return;
		} 
		String yearAndMonth[] = qdate.split("-");
		//获取某年某月的第一天【年：yearAndMonth[0]  月：yearAndMonth[1]】
		String firstDayOfMonth = getFirstDayOfMonth(Integer.parseInt(yearAndMonth[0]),Integer.parseInt(yearAndMonth[1])-1);
		//获取某年某月的最后一天
		String lastDayOfMonth = getLastDayOfMonth(Integer.parseInt(yearAndMonth[0]),Integer.parseInt(yearAndMonth[1])-1);
		
		StringBuffer sql = new StringBuffer(); 
		//(MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM'), 'YYYY-MM'),TO_DATE(TO_CHAR(B.INWAREHOUSE_DATE,'YYYY-MM'), 'YYYY-MM'))+1)*12 AS USED_YEAR
		//+1 判断是否是同一天
		
		sql.append("SELECT T.INTANGIBLE_FLG,T.DEV_CODE ,T.DEV_CHN_DESC AS DEV_NAME,T.SPECIFICATION AS DEV_SPEC,")
				.append("		E.UNIT_CHN_DESC AS STOCK_UNIT,B.STOCK_QTY AS DEV_NUM,B.UNIT_PRICE,")
				.append("		C.DEPT_CHN_DESC AS USED_DEPT,") 
				.append("		TO_CHAR(B.INWAREHOUSE_DATE, 'YYYY/MM/DD') AS BUY_DATE,D.CHN_DESC AS DEV_KIND,12*T.DEP_DEADLINE AS DEP_YEAR,")
				.append("		MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM'), 'YYYY-MM'),TO_DATE(TO_CHAR(B.INWAREHOUSE_DATE,'YYYY-MM'), 'YYYY-MM')) AS USED_YEAR,")
				.append("		B.MDEP_PRICE AS DEP_PRICE_M,B.DEP_PRICE AS DEP_PRICE_ALL,B.CURR_PRICE AS DEP_PRICE_M_JZ")
				.append(" FROM DEV_BASE T,DEV_STOCKDD B, SYS_DEPT C, SYS_DICTIONARY D, SYS_UNIT E ")
				.append(" WHERE T.DEV_CODE = B.DEV_CODE AND T.INTANGIBLE_FLG ='Y'") 
				.append(" AND B.DEPT_CODE = C.DEPT_CODE ") 
				.append(" AND D.GROUP_ID='DEVKIND_CODE' AND T.DEVKIND_CODE=D.ID AND T.UNIT_CODE = E.UNIT_CODE");
		sql.append(" AND B.INWAREHOUSE_DATE BETWEEN TO_DATE ('").append(firstDayOfMonth+"000000")
				.append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(lastDayOfMonth+"235959").append("', 'YYYYMMDDHH24MISS')");
		String devKind = getValueString("DEV_KIND");	//财务类别
		messageBox("devKind"+devKind); 
		if(devKind.length() > 0){    
			sql.append(" AND T.FINAN_KIND = '").append(devKind).append("'");
		}    
		sql.append(" ORDER BY T.DEV_CODE");
		System.out.println("无形资产摊销计算 sql="+sql.toString()); 
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString())); 
		
		//添加合计
		double all_dep_price_m = 0.0;		//本月摊销额
		double all_dep_price_all = 0.0;		//累计摊销额
		double all_dep_price_m_jz = 0.0;	//本月摊销额(净值)
		double all_all_price = 0.0;			//总价 
		int all_num = 0;//总数量
		int j = 0;
		//TParm newParm = new TParm();
		for (int i = 0; i < returnParm.getCount(); i++) {
			int num = returnParm.getInt("DEV_NUM",i);					//数量
			double unitPrice = returnParm.getDouble("UNIT_PRICE",i);	//单价
			double all_price = unitPrice*num;							//总价=单价*数量
			BigDecimal bdi = new BigDecimal(all_price);
			bdi = bdi.setScale(1, 4);									//总价数值保留一位小数
			all_price = bdi.doubleValue();
			double dep_price_m = returnParm.getDouble("DEP_PRICE_M",i);
			double dep_price_all = returnParm.getDouble("DEP_PRICE_ALL",i);
			double dep_price_m_jz = returnParm.getDouble("DEP_PRICE_M_JZ",i);
			
			all_dep_price_m += dep_price_m; 
			all_dep_price_all += dep_price_all;
			all_dep_price_m_jz += dep_price_m_jz;
			all_num += num;
			all_all_price += all_price;
			returnParm.setData("ALL_PRICE",i,all_price);
			j++;
			
		}
		all_all_price = TiMath.round(all_all_price, 1);   
		all_dep_price_m = TiMath.round(all_dep_price_m, 1); 
		all_dep_price_all =  TiMath.round(all_dep_price_all, 1); 
		all_dep_price_m_jz = TiMath.round(all_dep_price_m_jz, 1); 
		//合计
		returnParm.setData("DEV_NAME",j, "合计:");
		returnParm.setData("DEV_SPEC", j, "");
		returnParm.setData("STOCK_UNIT", j,"");
		returnParm.setData("DEV_NUM", j, all_num);
		returnParm.setData("UNIT_PRICE", j, "");
		returnParm.setData("ALL_PRICE", j, all_all_price);
		returnParm.setData("USED_DEPT", j, "");
		returnParm.setData("BUY_DATE", j, "");
		returnParm.setData("PRO_CODE", j, "");
		returnParm.setData("DEV_KIND", j, "");
		returnParm.setData("DEP_YEAR", j, "");
		returnParm.setData("USED_YEAR", j, "");
		returnParm.setData("DEP_PRICE_M", j, all_dep_price_m);
		returnParm.setData("DEP_PRICE_ALL", j, all_dep_price_all);
		returnParm.setData("DEP_PRICE_M_JZ", j, all_dep_price_m_jz);
		if(returnParm.getCount() < 0){
			messageBox("查无数据！");
			TParm resultparm = new TParm();
			this.table.setParmValue(resultparm);
			return;
		}
		//将最后封装的TParm数据放到table控件中显示
		this.table.setParmValue(returnParm);
		
	}
	
	/**
	 * 打印方法
	 */
	public void onPrint(){
		TTextFormat turnPoint = (TTextFormat) getComponent("DEV_KIND");
		String devKind = turnPoint.getText();	//财务类别
		String date = StringTool.getString(TypeTool
				.getTimestamp(getValue("Q_DATE")), "yyyy-MM");
		
		TTable table = getTable("TABLE");
		if(table.getRowCount() > 0){
			TParm tableParm = table.getParmValue();
			//out("tableParm.getCount():" + tableParm.getCount());
			//打印数据
			TParm data = new TParm();
			//表头数据
			data.setData("TITLE", "TEXT", Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
					"固定资产折旧计算报表");
			data.setData("DEV_KIND","TEXT", "财务类别:"+ devKind);
			data.setData("Q_DATE", "TEXT", "日期：" + date);
			//表格数据
			TParm parm = new TParm();
			
			if(tableParm.getCount() <= 0){
				this.messageBox("查无数据！");
			}else{
			//遍历表格中元素
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("DEV_NAME", tableParm.getValue("DEV_NAME", i));				//名称
				parm.addData("DEV_SPEC", tableParm.getValue("DEV_SPEC", i));				//规格
				parm.addData("STOCK_UNIT", tableParm.getValue("STOCK_UNIT", i));			//单位
				parm.addData("DEV_NUM", tableParm.getValue("DEV_NUM", i));					//数量
				parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE", i));			//单价
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));				//总价
				parm.addData("USED_DEPT", tableParm.getValue("USED_DEPT", i));				//使用部门
				parm.addData("BUY_DATE", tableParm.getValue("BUY_DATE", i));				//购入日期
				parm.addData("PRO_CODE", tableParm.getValue("PRO_CODE", i));				//成本项目代码
				parm.addData("DEV_KIND", tableParm.getValue("DEV_KIND", i));				//类别
				parm.addData("DEP_YEAR", tableParm.getValue("DEP_YEAR", i));				//折旧年限(月)
				parm.addData("USED_YEAR", tableParm.getValue("USED_YEAR", i));				//已使用年限(月)
				parm.addData("DEP_PRICE_M", tableParm.getValue("DEP_PRICE_M", i));			//本月摊销额
				parm.addData("DEP_PRICE_ALL", tableParm.getValue("DEP_PRICE_ALL", i));		//累计摊销额
				parm.addData("DEP_PRICE_M_JZ", tableParm.getValue("DEP_PRICE_M_JZ", i));	//本月摊销额(净值)
				
			}
			
			//总行数
			parm.setCount(parm.getCount("DEV_NAME"));
			parm.addData("SYSTEM", "COLUMNS", "DEV_NAME");
			parm.addData("SYSTEM", "COLUMNS", "DEV_SPEC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NUM");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "USED_DEPT");
			parm.addData("SYSTEM", "COLUMNS", "BUY_DATE");
			parm.addData("SYSTEM", "COLUMNS", "PRO_CODE");
			parm.addData("SYSTEM", "COLUMNS", "DEV_KIND");
			parm.addData("SYSTEM", "COLUMNS", "DEP_YEAR");
			parm.addData("SYSTEM", "COLUMNS", "USED_YEAR");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_M");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_ALL");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_M_JZ");
			
			//将表格放到容器中
			data.setData("TABLE", parm.getData());
			//表尾数据
			data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
			
			//out日志输出data信息-用于调试
			//System.out.println("data=="+data);
			
			//调用打印方法 
			this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEVIntangibleDepreciation.jhw", data);
			}
 
		}else {
			this.messageBox("没有打印数据");
            return;
		}
		
	}
	
	/**
     * 导出EXCEL
     */
    public void onExport() {
    	
    	TTable table_e = getTable("TABLE");
    	if(table_e.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(table_e, "固定资产折旧计算表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
    	}
    	
    } 
    
    /**
     * 清空方法
     */
    public void onClear() {
    	initPage();
    	table.removeAll();
    	callFunction("UI|TABLE|removeRowAll");
    }
    
    //获取某年某月的最后一天
    public static String getLastDayOfMonth(int year, int month) {     
        Calendar cal = Calendar.getInstance();     
        cal.set(Calendar.YEAR, year);     
        cal.set(Calendar.MONTH, month);     
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE));  
       return  new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
    } 
    
    //获取某年某月的第一天
    public static String getFirstDayOfMonth(int year, int month) {     
        Calendar cal = Calendar.getInstance();     
        cal.set(Calendar.YEAR, year);     
        cal.set(Calendar.MONTH, month);  
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));  
       return   new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
    } 
    
	/**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称 
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
