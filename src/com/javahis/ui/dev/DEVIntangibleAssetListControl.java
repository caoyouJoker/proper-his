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
 * Title:无形资产明细
 * </p> 
 *
 * <p>
 * Description:无形资产明细
 * </p>  
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author duzhw 2013.08.16
 * @version 1.0
 */
public class DEVIntangibleAssetListControl extends TControl{
	
	private TTable table;
	
	public DEVIntangibleAssetListControl(){
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
		String devKind = getValueString("DEV_KIND");	//无形资产类别-资金来源
		String devFinan = getValueString("DEV_FINAN");	//财务类别
//		if(devKind.length() == 0){
//			messageBox("资金来源不能为空!");
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
		sql.append("SELECT T.DEV_CODE,T.DEV_CHN_DESC AS DEV_NAME,T.SPECIFICATION AS DEV_SPEC,")
				.append("		E.UNIT_CHN_DESC AS STOCK_UNIT,B.STOCK_QTY AS DEV_NUM,B.UNIT_PRICE,")
				.append("		TO_CHAR(B.INWAREHOUSE_DATE, 'YYYY/MM/DD') AS BUY_DATE,")
				.append("		B.DEP_PRICE AS ACC_AMOUNT,B.CURR_PRICE AS PRE_ASSET,D.COST_CENTER_ABS_DESC AS COST_CENTER,")
				.append("		C.CHN_DESC AS SOURCE_FUND")
				.append(" FROM DEV_BASE T,DEV_STOCKDD B, SYS_DICTIONARY C, SYS_COST_CENTER D, SYS_UNIT E ")
				.append(" WHERE T.DEV_CODE = B.DEV_CODE AND T.INTANGIBLE_FLG ='Y' ")
				.append(" AND C.ID=T.FUNDSOU_CODE AND C.GROUP_ID='FUNDSOU_CODE'")
				.append(" AND B.DEPT_CODE=D.COST_CENTER_CODE AND D.ACTIVE_FLG = 'Y' AND T.UNIT_CODE = E.UNIT_CODE");
		sql.append(" AND B.INWAREHOUSE_DATE BETWEEN TO_DATE ('").append(firstDayOfMonth+"000000")
				.append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(lastDayOfMonth+"235959").append("', 'YYYYMMDDHH24MISS')");
		if(devKind.length() > 0){
			sql.append(" AND T.FUNDSOU_CODE = '").append(devKind).append("'");
		} 		if(devFinan.length() > 0){
			sql.append(" AND T.FINAN_KIND = '").append(devFinan).append("'"); 
		} 
		System.out.println("无形资产明细 sql="+sql.toString());  
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString()));
		//添加合计 
		double all_all_price = 0.0;			//总价
		double all_dep_price_all = 0.0;		//累计摊销额总计
		double all_pre_price = 0.0;			//现值总计
		int all_num = 0;					//总数量
		int j = 0;
		//TParm newParm = new TParm();
		for (int i = 0; i < returnParm.getCount(); i++) {
			int num = returnParm.getInt("DEV_NUM",i);					//数量
			double unitPrice = returnParm.getDouble("UNIT_PRICE",i);	//单价
			double all_price = unitPrice*num;							//总价=单价*数量
			BigDecimal bdi = new BigDecimal(all_price);
			bdi = bdi.setScale(1, 4);									//总价数值保留一位小数
			all_price = bdi.doubleValue();
			double acc_dep = returnParm.getDouble("ACC_AMOUNT",i);
			double pre_price = returnParm.getDouble("PRE_ASSET",i);
			all_all_price += all_price;
			all_dep_price_all += acc_dep;
			all_pre_price += pre_price;
			all_num += num;
			returnParm.setData("ALL_PRICE",i,all_price);
			j++;
			
		} 
		all_all_price = TiMath.round(all_all_price, 1);  
		all_pre_price = TiMath.round(all_pre_price, 1); 
		all_dep_price_all =  TiMath.round(all_dep_price_all, 1);  
		//合计
		returnParm.setData("DEV_CODE",j, "合计:");
		returnParm.setData("DEV_NAME",j, "");
		returnParm.setData("DEV_SPEC", j, "");
		returnParm.setData("STOCK_UNIT", j,"");
		returnParm.setData("DEV_NUM", j, all_num);
		returnParm.setData("UNIT_PRICE", j, "");
		returnParm.setData("ALL_PRICE", j, all_all_price);
		returnParm.setData("BUY_DATE", j, "");
		returnParm.setData("ACC_AMOUNT", j, all_dep_price_all);
		returnParm.setData("PRE_ASSET", j, all_pre_price);
		returnParm.setData("COST_CENTER", j, "");
		returnParm.setData("SOURCE_FUND", j, "");
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
		String devKind = turnPoint.getText();	//资金来源
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
					"无形资产明细账");
			data.setData("DEV_KIND","TEXT", "资金来源：" + devKind);
			data.setData("Q_DATE", "TEXT", "日期：" + date);
			//表格数据
			TParm parm = new TParm();
			  
			if(tableParm.getCount() <= 0){
				this.messageBox("查无数据！");
			}else{
			//遍历表格中元素
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("DEV_CODE", tableParm.getValue("DEV_CODE", i));		//资产编码
				parm.addData("DEV_NAME", tableParm.getValue("DEV_NAME", i));		//名称
				parm.addData("DEV_SPEC", tableParm.getValue("DEV_SPEC", i));		//规格
				parm.addData("STOCK_UNIT", tableParm.getValue("STOCK_UNIT", i));	//单位
				parm.addData("DEV_NUM", tableParm.getValue("DEV_NUM", i));			//数量 
				parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE", i));	//单价
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));		//总价
				parm.addData("BUY_DATE", tableParm.getValue("BUY_DATE", i));		//购入日期
				parm.addData("ACC_AMOUNT", tableParm.getValue("ACC_AMOUNT", i));	//累计摊销额
				parm.addData("PRE_ASSET", tableParm.getValue("PRE_ASSET", i));		//资产现值
				parm.addData("COST_CENTER", tableParm.getValue("COST_CENTER", i));	//成本中心
				parm.addData("SOURCE_FUND", tableParm.getValue("SOURCE_FUND", i));	//资金来源
				
			}
			
			//总行数
			parm.setCount(parm.getCount("DEV_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NAME");
			parm.addData("SYSTEM", "COLUMNS", "DEV_SPEC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NUM");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "BUY_DATE");
			parm.addData("SYSTEM", "COLUMNS", "ACC_AMOUNT");
			parm.addData("SYSTEM", "COLUMNS", "PRE_ASSET");
			parm.addData("SYSTEM", "COLUMNS", "COST_CENTER");
			parm.addData("SYSTEM", "COLUMNS", "SOURCE_FUND");
			
			
			//将表格放到容器中
			data.setData("TABLE", parm.getData());
			//表尾数据
			data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
			
			//out日志输出data信息-用于调试
			//System.out.println("data=="+data);
			
			// 调用打印方法 
			this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEVIntangibleAssetList.jhw", data);
			}

		}else {
			this.messageBox("没有打印数据");
            return;
		}
		
	}
	/**
	 * 点击事件
	 *  
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
        row = table.getClickedRow(); 
        TParm rowParm = table.getParmValue().getRow(row);     
        //设置弹出菜单     
		if (rowParm.getValue("DEV_NAME").length() == 0){
			return;
		}   
		// 状态条显示       
		else{   
		callFunction(  
		        "UI|setSysStatus", 
		        rowParm.getValue("DEV_CODE") + " " 
		        + rowParm.getValue("DEV_NAME")
			    + " " + rowParm.getValue("DEV_SPEC")); 
		}   
	}
	
	/**
     * 导出EXCEL
     */
    public void onExport() {
    	
    	TTable table_e = getTable("TABLE");
    	if(table_e.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(table_e, "无形资产明细表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
    	}
    }
    
    public void onExcel(){ 
    	TTable tab = this.getTable("TABLE");
    	if(tab.getRowCount()<=0){
    		this.messageBox("无汇出数据");
    		return;
    	}
    	else{
    	    ExportExcelUtil.getInstance().exportExcel(tab, "无形资产明细");
    	}
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
    	initPage();
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
