package com.javahis.ui.ope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title:费用核算明细表
 * </p>
 *
 * <p>
 * Description:费用核算明细表
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
public class OPECostAccountListControl extends TControl{
	
	private TTable table;
	
	public OPECostAccountListControl(){
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
		String preday = getPreviousMonthDay(26);//上月26号
		String currday = getFirstDayOfMonth(25);//本月25号
		this.setValue("START_DATE", StringTool.getTimestamp(preday + "000000",
		"yyyyMMddHHmmss"));// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(currday + "235959",
		"yyyyMMddHHmmss"));// 结束时间
	}
	
	/**
	 * 查询方法
	 */
	public void onQuery(){
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss"); 	//开始时间
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss"); 	//结束时间
		String deptCode = getValueString("DEPT_CODE");						//科室
		if(startDate.length() == 0){
			messageBox("开始时间不正确!");
			return;
		}
		if(endDate.length() == 0){
			messageBox("结束时间不正确!");
			return;
		}
	
		String pattern ="yyyy-MM-dd hh:mm:ss";
		try {
			SimpleDateFormat sf = new SimpleDateFormat(pattern);
			 Date d1 = sf.parse(startDate);
			 Date d2 = sf.parse(endDate);
			 if(d1.getTime() > d2.getTime()){
				 messageBox("开始时间不能晚于结束时间!");
					return;
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT T.SEQ,C.DEPT_CHN_DESC AS DEPT_CODE_DESC,T.DEPT_CODE,T.BILL_DATE AS INDATE,D.PAT_NAME AS PNAME,")
			.append("T.MR_NO AS CASE_NO,T.OWN_PRICE,T.ORDER_CODE,T.ORDER_DESC,B.HEXP_CODE,")
			.append("T.AR_AMT ")
			.append(" FROM SPC_INV_RECORD T,IBS_ORDD B, SYS_DEPT C, SYS_PATINFO D")
			.append(" WHERE T.CASE_NO = B.CASE_NO AND T.CASE_NO_SEQ = B.CASE_NO_SEQ AND T.SEQ_NO = B.SEQ_NO")
			.append(" AND T.DEPT_CODE = C.DEPT_CODE AND T.MR_NO= D.MR_NO");
		
		if(deptCode.toString().length() > 0){//过滤条件：科室不为空
			sql.append(" AND T.DEPT_CODE = '" + deptCode + "'");
		}
		//开始、结束时间过滤
		sql.append(" AND T.BILL_DATE BETWEEN TO_DATE ('").append(startDate).append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(endDate).append("', 'YYYYMMDDHH24MISS')");
		sql.append(" ORDER BY T.DEPT_CODE,T.MR_NO,T.ORDER_CODE ");
		//打印sql
		//System.out.println("sql语句：：：："+sql.toString());
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString()));
		
		String hexpCode = "";
		String deptCode1 = "";
		String deptCode2 = "";
		String caseNo1 = "";
		String caseNo2 = "";
		String orderCode1 = "";
		String orderCode2 = "";
		double arAmt = 0.00;				//费用
		double material_price = 0.00;		//材料费
		double operstion_price = 0.00;		//手术费
		double drug_price = 0.00;			//药品费
		double all_material_price = 0.00;	//总计-材料费
		double all_operstion_price = 0.00;	//总计-手术费
		double all_drug_price = 0.00;		//总计-药品费
		TParm newparm = new TParm();
		int j = 0;
		for (int i = 0; i < returnParm.getCount(); i++) {
			hexpCode = returnParm.getValue("HEXP_CODE", i);
			deptCode1 = returnParm.getValue("DEPT_CODE", i);
			caseNo1 = returnParm.getValue("CASE_NO", i);
			orderCode1 = returnParm.getValue("ORDER_CODE", i);
			arAmt = returnParm.getDouble("AR_AMT", i);
			
			deptCode2 = returnParm.getValue("DEPT_CODE", i+1);
			caseNo2 = returnParm.getValue("CASE_NO", i+1);
			orderCode2 = returnParm.getValue("ORDER_CODE", i+1);
			if(deptCode1.equals(deptCode2)){
				if(caseNo1.equals(caseNo2)){
					if(orderCode1.equals(orderCode2)){//手术编号是否一样
						if(hexpCode.substring(0, 2).equals("2E")){//药品费
							drug_price += arAmt;
						}else if(hexpCode.equals("250")){//手术费
							operstion_price += arAmt;
						}else if(hexpCode.substring(0, 2).equals("2C")){//材料费
							material_price += arAmt;
						}
					}else {
						if(hexpCode.substring(0, 2).equals("2E")){//药品费
							drug_price += arAmt;
						}else if(hexpCode.equals("250")){//手术费
							operstion_price += arAmt;
						}else if(hexpCode.substring(0, 2).equals("2C")){//材料费
							material_price += arAmt;
						}
						newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	//
						newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
						newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					//
						newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					//
						newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
						newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
						newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
						newparm.setData("MATERIAL_PRICE", j, material_price);					//材料费
						newparm.setData("OPERATION_PRICE", j, operstion_price);					//手术费
						newparm.setData("DRUG_PRICE", j, drug_price);							//药品费
						newparm.setData("ALL_PRICE", j, material_price 
								+ operstion_price + drug_price);								//合计=材料费+手术费+药品费
						
						material_price = 0.00;	//材料费清0
						operstion_price = 0.00;	//手术费清0
						drug_price = 0.00;		//药品费清0
						
						j++;
					}
				}else {
					newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	
					newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
					newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					
					newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					
					newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
					newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
					newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
					newparm.setData("MATERIAL_PRICE", j, material_price);					//材料费
					newparm.setData("OPERATION_PRICE", j, operstion_price);					//手术费
					newparm.setData("DRUG_PRICE", j, drug_price);							//药品费
					newparm.setData("ALL_PRICE", j, material_price 
							+ operstion_price + drug_price);								//合计=材料费+手术费+药品费
					
					material_price = 0.00;//材料费清0
					operstion_price = 0.00;//手术费清0
					drug_price = 0.00;//药品费清0
					
					j++;
				}
			}else {
				if(hexpCode.substring(0, 2).equals("2E")){//药品费  ----
					drug_price += arAmt;
				}else if(hexpCode.equals("250")){//手术费
					operstion_price += arAmt;
				}else if(hexpCode.substring(0, 2).equals("2C")){//材料费
					material_price += arAmt;
				}
				newparm.setData("SEQ", j, returnParm.getValue("SEQ", i));	
				newparm.setData("DEPT_CODE_DESC", j, returnParm.getValue("DEPT_CODE_DESC", i));									
				newparm.setData("INDATE", j, returnParm.getValue("INDATE", i));					
				newparm.setData("PNAME", j, returnParm.getValue("PNAME", i));					
				newparm.setData("CASE_NO", j, returnParm.getValue("CASE_NO", i));	
				newparm.setData("OWN_PRICE", j, returnParm.getValue("OWN_PRICE", i));
				newparm.setData("ORDER_DESC", j, returnParm.getValue("ORDER_DESC", i));
				newparm.setData("MATERIAL_PRICE", j, material_price);					//材料费
				newparm.setData("OPERATION_PRICE", j, operstion_price);					//手术费
				newparm.setData("DRUG_PRICE", j, drug_price);							//药品费
				newparm.setData("ALL_PRICE", j, material_price 
						+ operstion_price + drug_price);								//合计=材料费+手术费+药品费
				
				material_price = 0.00;//材料费清0
				operstion_price = 0.00;//手术费清0
				drug_price = 0.00;//药品费清0
				
				j++;
			}
			
		}
		//合计算出费用
		for (int k = 0; k < newparm.getCount("SEQ"); k++) {
			if(k==0){
				double material_price_k = newparm.getDouble("MATERIAL_PRICE", k);
				double operstion_price_k = newparm.getDouble("OPERATION_PRICE", k);
				double drug_price_k = newparm.getDouble("DRUG_PRICE", k);
				all_material_price += material_price_k;
				all_operstion_price += operstion_price_k;
				all_drug_price += drug_price_k;
			}else{
				double material_price_k1 = newparm.getDouble("MATERIAL_PRICE", k);
				double operstion_price_k1 = newparm.getDouble("OPERATION_PRICE", k);
				double drug_price_k1 = newparm.getDouble("DRUG_PRICE", k);
				all_material_price += material_price_k1;
				all_operstion_price += operstion_price_k1;
				all_drug_price += drug_price_k1;
			}
		}
		//添加"合计"
		newparm.setData("SEQ", j, "合计:");
		newparm.setData("DEPT_CODE_DESC", j, "");									
		newparm.setData("INDATE", j, "");					
		newparm.setData("PNAME", j, "");					
		newparm.setData("CASE_NO", j, "");	
		newparm.setData("OWN_PRICE", j, "");
		newparm.setData("ORDER_DESC", j, "");
		newparm.setData("MATERIAL_PRICE", j, all_material_price);
		newparm.setData("OPERATION_PRICE", j, all_operstion_price);
		newparm.setData("DRUG_PRICE", j, all_drug_price);
		newparm.setData("ALL_PRICE", j, all_material_price+all_operstion_price+all_drug_price);
		
		if(newparm.getCount("SEQ") < 0){
			messageBox("查无数据！");
			TParm resultparm = new TParm();
			this.table.setParmValue(resultparm);
			return;
		}
		//将最后封装的TParm数据放到table控件中显示
		this.table.setParmValue(newparm);
		
	}
	
	/**
	 * 打印方法
	 */
	public void onPrint(){
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy-MM-dd");	//开始时间
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy-MM-dd");	//结束时间
//		TTextFormat turnPoint = (TTextFormat) getComponent("DEPT_CODE");
//		String deptCode = turnPoint.getText();	//科室
//		System.out.println("报表科室="+turnPoint.getText());
		String deptCode = getValueString("DEPT_CODE");				//科室
		System.out.println("999988---0=科室："+deptCode);
		if(deptCode.length() > 0){
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '" + deptCode + "'";
			TParm Parm = new TParm(TJDODBTool.getInstance().select(
					 sql.toString()));
			deptCode = Parm.getValue("DEPT_CHN_DESC", 0);
		}else{
			deptCode = "";
		}
		TTable table = getTable("TABLE");
		
		if(table.getRowCount() > 0){
			TParm tableParm = table.getParmValue();
			//out("tableParm.getCount():" + tableParm.getCount());
			//打印数据
			TParm data = new TParm();
			//表头数据
			data.setData("TITLE", "TEXT", Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
					"费用核算明细报表");
			data.setData("DEPT_CODE","TEXT", "科室:" + deptCode);
			data.setData("START_DATE", "TEXT", "开始时间：" + startDate);
			data.setData("END_DATE", "TEXT", "结束时间：" + endDate);
			//表格数据
			TParm parm = new TParm();
			
			if(tableParm.getCount("SEQ") <= 0){
				this.messageBox("无数据！");
			}else{
			//遍历表格中元素
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("SEQ", tableParm.getValue("SEQ", i));								//序号
				parm.addData("DEPT_CODE_DESC", tableParm.getValue("DEPT_CODE_DESC", i));		//科室
				String getdate = tableParm.getValue("INDATE", i);
				String c[] = getdate.split(" ");
//				String getdate = StringTool.getString(TypeTool
//						.getTimestamp(tableParm.getValue("INDATE", i)), "yyyy-MM-dd");
//				parm.addData("INDATE", getdate);												//日期
				parm.addData("INDATE", c[0]);													//日期
				parm.addData("PNAME", tableParm.getValue("PNAME", i));							//病人姓名
				parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));						//病案号
				parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));					//单价
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));				//手术名称
				parm.addData("MATERIAL_PRICE", tableParm.getValue("MATERIAL_PRICE", i));		//材料费
				parm.addData("OPERATION_PRICE", tableParm.getValue("OPERATION_PRICE", i));		//手术费
				parm.addData("DRUG_PRICE", tableParm.getValue("DRUG_PRICE", i));				//药品费
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));					//总计
				
			}
			
			//总行数
			parm.setCount(parm.getCount("SEQ"));
			parm.addData("SYSTEM", "COLUMNS", "SEQ");
			parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE_DESC");
			parm.addData("SYSTEM", "COLUMNS", "INDATE");
			parm.addData("SYSTEM", "COLUMNS", "PNAME");
			parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MATERIAL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OPERATION_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "DRUG_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			
			//将表格放到容器中
			data.setData("TABLE", parm.getData());
			//表尾数据
			data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
			
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\OPE\\OPECostAccountList.jhw", data);
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
    		ExportExcelUtil.getInstance().exportExcel(table_e, "费用核算明细表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
     }
    }
    /**
     *设置上个月哪一天（多少号）
     */
    public String getPreviousMonthDay(int no) {
    	String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// 设为当前月的n号
		lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号
		// lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天

		str = sdf.format(lastDate.getTime());
		return str;
    }
	// 获取当月第一天
	public static String getFirstDayOfMonth(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// 设为当前月的n号
		str = sdf.format(lastDate.getTime());
		return str;
	}
    /**
     * 清空方法
     */
    public void onClear() {//初始化时间
    	String preday = getPreviousMonthDay(26);	//上月26号
		String currday = getFirstDayOfMonth(25);	//本月25号
		this.setValue("START_DATE", StringTool.getTimestamp(preday + "000000",
		"yyyyMMddHHmmss"));							// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(currday + "235959",
		"yyyyMMddHHmmss"));							// 结束时间
		
		this.setValue("DEPT_CODE", "");				//科室
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
