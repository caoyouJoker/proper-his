package com.javahis.ui.inv;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.inv.INVsettlementTool;
import jdo.spc.IndAgentTool;
import jdo.spc.SPCSettleAccountsTool;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.ExportXmlUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资结算
 * </p>
 *
 * <p>
 * Description: 物资结算     
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author chenx 2013.07.19
 * @version 4.0
 */
public class INVAccountControl extends TControl {
	
	public TTable table;
	
	TParm  accountResult   ;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化查询区间
		this.setValue("START_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInitLast(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("END_DATE", rollDay);
		 
		setValue("REGION_CODE", Operator.getRegion());
		
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		table = this.getTable("TABLE");
		
		
	}
	/**
	 * 物资历史记录查询
	 */
	public  void  onHistoryQuery(){
		TParm  parmD = this.getAccountNoByDate() ;
		if(parmD.getErrCode()<0){
			return  ;
		}
		TTable  tableD = (TTable)this.getComponent("TABLED");
		tableD.setParmValue(parmD) ;
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("请选择供应商");
			return ;
		}
		TParm searchParm = getSearchParm();  //结算查询的条件
		//结算数据 result
		accountResult = INVsettlementTool.getInstance().getAccountData(searchParm) ;     
		
		
		TParm newParm = new TParm() ;
		 
		//采购总金额
		double sumTotAmt =  0 ;
		int count = accountResult.getCount() ;
		int i = 0 ;
		for(int j = 0;  j < count ; j++){
			TParm rowParm = accountResult.getRow(j);
			
			//标志是否在供应商对应的商品代码里，true是
			boolean b = false;
			String invCode = rowParm.getValue("INV_CODE") ;
			TParm sParm = new TParm();
			sParm.setData("SUP_CODE",supCode);
			sParm.setData("INV_CODE",invCode)  ;
			TParm invAgenTParm =  INVsettlementTool.getInstance().getAgentInvCode(sParm) ;
			if(invAgenTParm.getCount()<0){
				this.messageBox("供应商对应药品为空") ;
				return  ;
			}
				TParm indRowParm = invAgenTParm.getRow(0) ;
				String agtInvCode = indRowParm.getValue("INV_CODE");
				if(invCode.equals(agtInvCode)){
					b = true;
				
				}
			if(b){
				//物资代码
				newParm.setData("INV_CODE",i,invCode);
				//物资名称
				newParm.setData("INV_DESC",i,rowParm.getValue("INV_CHN_DESC"));
				//规格
				newParm.setData("DESCRIPTION",i,rowParm.getValue("DESCRIPTION"));
				//出库总量
				newParm.setData("QTY",i,rowParm.getValue("QTY"));
				//单位
				newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
				//采购单价
				newParm.setData("OWN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
				//采购金额
				newParm.setData("TOT_AMT",i,rowParm.getDouble("TOT_AMT"));
				sumTotAmt +=rowParm.getDouble("TOT_AMT") ;
				//部门名称
				newParm.setData("ORG_DESC",i,rowParm.getValue("ORG_DESC"));
				i++;
			}
		}
		//采购总金额
		setValue("SUM_TOT_AMT", StringTool.round(sumTotAmt,2));
		//合计笔数
		setValue("COUNT", count);
		table.setParmValue(newParm);
	}
   /**
    * 获取查询条件数据
    * @return
    */
	private TParm getSearchParm() {
		TParm searchParm = new TParm();
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
				+ startDate.substring(8, 10) + startDate.substring(11, 13)
				+ startDate.substring(14, 16) + startDate.substring(17, 19);
		searchParm.setData("START_DATE",startDate);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
				+ endDate.substring(8, 10) + endDate.substring(11, 13)
				+ endDate.substring(14, 16) + endDate.substring(17, 19);
		searchParm.setData("END_DATE",endDate);
		
		String invCode = getValueString("INV_CODE");
		searchParm.setData("INV_CODE",invCode);
		
		String orgCode = getValueString("ORG_CODE");
		searchParm.setData("ORG_CODE",orgCode);
		
		String supCode = getValueString("SUP_CODE");
		
		searchParm.setData("SUP_CODE",supCode);
		return searchParm;
	}

	/**
	 * table  的单击事件
	 */
	public void onTableClicked(int row ){
		if(row<0)  return ;
		this.onClear() ;
		TTable tableD = this.getTable("TABLED");
		row = tableD.getSelectedRow() ;
		for(int i=0;i<tableD.getRowCount();i++){
			tableD.setItem(i, "FLG", "N");
		}
		tableD.setItem(row, "FLG", "Y");
		String accountNo = tableD.getItemString(row, "ACCOUNT_NO") ;
		TParm result= INVsettlementTool.getInstance().getAccountData(accountNo) ;
		if(result.getCount()<0){
			this.messageBox("没有细项数据") ;
			return ;
		}
		double allMoney = 0.00 ;
		for(int i=0;i<result.getCount();i++){
			allMoney += result.getDouble("TOT_AMT", i) ;
		}
		//采购总金额
		setValue("SUM_TOT_AMT", StringTool.round(allMoney,2));
		//合计笔数
		setValue("COUNT", result.getCount());
		table.setParmValue(result) ;
	}
	/**
	 * 导出采购单为XML文件
	 */
	@SuppressWarnings("unchecked")
	public void onExportXml() {
		
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("请选择供应商");
			return ;
		}
		// 要导出来的数据
		TParm parm   = table.getParmValue() ;
		
		if (parm == null || parm.getCount("INV_CODE") < 0) {
			this.messageBox("没有导出的数据");
			return;
		}
		
		int count = parm.getCount("INV_CODE");

		TParm newParm = new TParm();
		 
		int  newCount = 0 ;
		/**
		 * 去除重复合并,界面上显示的是按照部门分类，发给国药的xml，要将同一类物资汇总
		 */
		for(int i = 0;i < count;i++) { 
			TParm rowParm = parm.getRow(i);
			String invCode = rowParm.getValue("INV_CODE") ;
			double dosageQty = rowParm.getDouble("QTY");
			for(int j = i+1;j < count;j++){ 
				TParm rowParmNew = parm.getRow(j);
				String invCodeNew = rowParmNew.getValue("INV_CODE") ;
				double dosageQtyNew = rowParmNew.getDouble("QTY") ;
			    if(invCode.equals(invCodeNew) ){ 
			   		dosageQty += dosageQtyNew ;
				    parm.removeRow(j); 
				    j-- ;
				    count--; 
			   }
			}
			
			newParm.setData("INV_CODE",i,invCode);
			newParm.setData("CSTCODE",i,rowParm.getValue("OWN_PRICE"));
			newParm.setData("INV_DESC",i,rowParm.getValue("INV_CHN_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("DESCRIPTION"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("DOSAGE_QTY",i,dosageQty);
			newCount++ ;
			 
		} 
		newParm.setCount(newCount);
		List list = new ArrayList();
		String msg = "";
		for (int i = 0; i < newParm.getCount(); i++) {
			TParm t =  newParm.getRow(i);	
				double dosageQty = t.getDouble("DOSAGE_QTY") ;
					if(dosageQty > 0 ){
						Map<String, String> map = new LinkedHashMap();
						map.put("cstcode",  t.getValue("CSTCODE"));//单价
						map.put("goods", t.getValue("INV_CODE") );   //物资哦编码
						map.put("goodname", t.getValue("INV_DESC")); //物资名称
						map.put("spec", t.getValue("SPECIFICATION"));//规格
						map.put("msunitno", t.getValue("DOSAGE_UNIT"));//单位
						map.put("billqty",dosageQty+"");//订购数量
						list.add(map);
					}

		}
		try {
			Document doc = ExportXmlUtil.createXml(list);
			ExportXmlUtil.exeSaveXml(doc, "物资结算.xml");
		} catch (Exception e) {
			System.out.println("错误信息=============="+e.toString());
		}
		
	}
	/**
	 * 保存
	 * 将界面的结算数据插入到account表中，流水号account_no 为请款单号
	 */
	public void onSave(){
		if( table.getRowCount() <= 0){
			this.messageBox("没有结算数据");
			return ;
		}
		
		String invCode = getValueString("INV_CODE");
		if(invCode != null &&  invCode.length() >  0 ){
			this.messageBox("结算不能输入物资编码!");
			return ;
		}
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("请选择供应商");
			return ;
		}
		
		String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
    	String startDate = getValueString("START_DATE");
    	 startDate = startDate.substring(0, 4)+ startDate.substring(5, 7) + startDate.substring(8, 10);
		TParm parm = this.getSearchParm() ;
		//查询区间段是否结算完成  true结算过
		TParm exits = checkSettleAccounts(parm) ;
		if(exits.getCount()>0){
			this.messageBox("本期已结算") ;
				return ;

		}
		if(this.messageBox("提示", "请确认是否结算?", 2) == 0){
		 
			int count = 0;
			TParm inParm = new TParm();
			String accountNo = SystemTool.getInstance().getNo("ALL", "INV", "INV_ACCOUNT", "No");
			for (int i = 0; i < accountResult.getCount(); i++) {
				TParm rowParm = accountResult.getRow(i);
				    inParm.setData("REGION_CODE",i,Operator.getRegion());
				    inParm.setData("ACCOUNT_NO",i,accountNo);
					inParm.setData("CLOSE_DATE",i,closeDate);
					inParm.setData("START_DATE",i,startDate);
					inParm.setData("ORG_CODE",i,rowParm.getValue("ORG_CODE"));
					inParm.setData("INV_CODE",i,rowParm.getValue("INV_CODE"));
					//出库总量
					inParm.setData("TOTAL_OUT_QTY",i,rowParm.getValue("QTY"));
					inParm.setData("TOTAL_UNIT_CODE",i,rowParm.getValue("UNIT_CODE"));
					//单价
					inParm.setData("VERIFYIN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
					//总金额
					inParm.setData("VERIFYIN_AMT",i,rowParm.getDouble("TOT_AMT"));
					//供应厂商
					inParm.setData("SUP_CODE",i,this.getValueString("SUP_CODE"));
					inParm.setData("OPT_USER",i,Operator.getID());
					inParm.setData("OPT_TERM",i,Operator.getIP());
					count++;
				}
			inParm.setCount(count) ;
			TParm result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
	                 "onSave", inParm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("结算失败");
				return;
			}
			this.messageBox("结算成功") ;
		}
	}
	/**
	 * 取消结算功能
	 */
	public void onCancleAccount(){
		 TTable table = this.getTable("TABLED");
		if(table.getRowCount()<0 || table==null){
			this.messageBox("没有结算数据") ;
			return  ;
		}
		TParm  parm  = new TParm() ;
		int count = 0;
		for(int i=0;i<table.getRowCount();i++){
			if(table.getItemData(i, "FLG").equals("Y")){
				count ++ ;
				parm.addData("ACCOUNT_NO", table.getItemString(i, "ACCOUNT_NO")) ;
			}
		}
		if(count==0){
			this.messageBox("选择取消数据") ;
			return  ;
		}
		
		parm.setCount(count) ;
    	TParm result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
                "onCancleAccountData", parm);
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("取消结算失败");
			return ;
		}
		this.messageBox("取消成功") ;
		this.onClear() ;
		table.removeRowAll() ;
		return   ;
	}
	/**
	 * 根据结算点获取结算的请款单号
	 * @return
	 */
	public TParm getAccountNoByDate(){
		TParm errParm = new TParm() ;
		errParm.setErrCode(-1) ;
		String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);	
    	//根据结算节点查询要取消结算的数据
    		TParm  parm = INVsettlementTool.getInstance().onCancleInvAccount(closeDate) ;
        	if(parm.getCount()<0){
        		this.messageBox("没有结算数据") ;      
        		return errParm ;
        	}
        	return parm ;
	}
	 /**
     * 汇出Excel
     */
    public void onExportXls() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "物资结算");
    }
    
    /**
     * 同步结算数据至HIS
     */
    public void onSynchronous(){
    	String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
		TParm closeParm = new TParm();
		closeParm.setData("CLOSE_DATE",closeDate);
		
		if(this.messageBox("提示", "请确认是否重送"+closeDate+"结算数据至HIS?", 2) == 0){
			TParm result = INVsettlementTool.getInstance().onQueryInvAccount(closeParm) ;
			if (result == null || result.getCount() < 0) {
				this.messageBox("没有要重送的结算数据");
				return ;
			}
			
			result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
	                "onSynchronous", result);   
			if (result == null || result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
		}
		this.messageBox("操作成功") ;
	}
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /**
     * 检测是否结算过
     * @return
     */
    public TParm checkSettleAccounts(TParm searchParm){
		TParm result = INVsettlementTool.getInstance().checkInvAccount(searchParm) ;
	     return  result ;
    }
    /**
     * 清空
     */
    public void onClear(){
    	 String clearStr = "INV_CODE;INV_DESC;ORG_CODE;SUM_TOT_AMT;COUNT;";
    	 this.clearValue(clearStr);
    	 table.removeRowAll() ;
    }
    /**
     * 打印
     */
    public void onPrint(){
		TParm result   = table.getParmValue() ;
		if (result == null || result.getCount("INV_CODE") < 0) {
			this.messageBox("没有打印数据");
			return;
		}
		
		 // 打印数据
        TParm date = new TParm();
        
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,19);
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,19);
        // 表头数据
        date.setData("TITLE", "物资结算单");
        date.setData("START_DATE",startDate);	
        date.setData("END_DATE",endDate);	
        
		TParm newParm = new TParm() ;
		 
		double sumTotAmt =  0 ;
		int count = result.getCount("INV_CODE") ;
		for(int i = 0;  i < count ; i++){
			TParm rowParm = result.getRow(i);
			newParm.setData("INV_CODE",i,rowParm.getValue("INV_CODE"));
			newParm.setData("INV_DESC",i,rowParm.getValue("INV_DESC"));
			newParm.setData("DESCRIPTION",i,rowParm.getValue("DESCRIPTION"));
			newParm.setData("OWN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
			newParm.setData("QTY",i,rowParm.getDouble("QTY"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("TOT_AMT",i,rowParm.getDouble("TOT_AMT"));
			sumTotAmt +=rowParm.getDouble("TOT_AMT");
			newParm.setData("ORG_DESC",i,rowParm.getValue("ORG_DESC"));
		}
		
		newParm.setCount(newParm.getCount("INV_CODE"));
		newParm.addData("SYSTEM", "COLUMNS", "INV_CODE");
		newParm.addData("SYSTEM", "COLUMNS", "INV_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		newParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		newParm.addData("SYSTEM", "COLUMNS", "QTY");
		newParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		newParm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		date.setData("TABLE", newParm.getData());
		
		date.setData("SUM_TOT_AMT",  df2.format(StringTool.round(sumTotAmt, 2)));//验收价格
		date.setData("OPT_USER",Operator.getName()); //Operator.getName()
		
		openPrintDialog("%ROOT%\\config\\prt\\inv\\INVAcconutPrint.jhw",
				date);
		
		
    }
    
    /**
     * 定位物资功能
     */
    public void onOrientationAction() {
        if ("".equals(this.getValueString("INV_CODE"))) {
            this.messageBox("请输入定位物资");
            return;
        }
        boolean flg = false;
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("INV_CODE");
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("INV_CODE"); i++) {
            if (order_code.equals(parm.getValue("INV_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("未找到定位物资");
        }
        else {
            table.setSelectedRow(row);
        }
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

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	
	
	/**
	 * 得到上个月
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
	
	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	@SuppressWarnings("deprecation")
	public Timestamp getDateForInitLast(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		String dayCycle = sysParm.getValue("DAY_CYCLE",0);
		int hours = Integer.parseInt( dayCycle.substring(0,2));
		result.setHours(hours);
		int minutes = Integer.parseInt(dayCycle.substring(2,4));
		result.setMinutes(minutes);
		int seconds = Integer.parseInt(dayCycle.substring(4,6));
		result.setSeconds(seconds);
		return result;
	}
	//数字格式化
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
}
