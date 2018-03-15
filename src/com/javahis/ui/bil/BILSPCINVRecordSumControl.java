package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 物联网物资汇总查询报表</p>
 *
 * <p>Description: 物联网物资汇总查询报表</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2013</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author duzhw
 * @version 1.0
 */
public class BILSPCINVRecordSumControl extends TControl {
	
	
	TTable Table;
	//页面控件
	private TComboBox orgCombo;
	
	/**
	 * 初始化
	 * */
	 public void onInit() {
	        super.onInit();	 
	        initComponent();
	        initPage();
	 }
	 //初始化设置
	 private void initPage(){
		 	Table = (TTable) this.getComponent("Table");
		 	Timestamp now = StringTool.getTimestamp(new Date());
		 	//String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		 	this.setValue("START_DATE",
		 			now.toString().substring(0, 10).replace('-', '/') + " 00:00:00");// 开始时间
		 	this.setValue("END_DATE",
		 			now.toString().substring(0, 10).replace('-', '/') + " 23:59:59");// 结束时间
		 	
		 	//初始化门急住别-1住院
			orgCombo.setSelectedIndex(1);
		 	
		 	TParm parm = new TParm();
		    // 设置弹出菜单
	        getTextField("INV_CODE").setPopupMenuParameter("UD",
	            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
	            parm);
			// 定义接受返回值方法
	        getTextField("INV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
			
		}
	/**
	 * 得到TextField对象
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
	    TParm parm = (TParm) obj;
	    if (parm == null) {
	            return;
	    }
	    String order_code = parm.getValue("INV_CODE");
	      if (!StringUtil.isNullString(order_code))
	          getTextField("INV_CODE").setValue(order_code);
	      String order_desc = parm.getValue("INV_CHN_DESC");
	      if (!StringUtil.isNullString(order_desc))
	            getTextField("INV_DESC").setValue(order_desc);
	}
	 
   /**
	* 查询方法
	*/
	public void onQuery(){
		
		
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss"); 	//开始时间
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss"); 	//结束时间
		String mrNo = this.getValueString("MR_NO").trim();
		String invCode = this.getValueString("INV_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String classCode = this.getValueString("CLASS_CODE");
		
		if (mrNo.length() != 0) {

			this.setValue("MR_NO", PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO")));
			this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
					PatTool.getInstance().checkMrno(
							this.getValueString("MR_NO"))));
		}
		
//		if(mrNo.length() == 0){
//			messageBox("病案号不能为空!");
//			return;
//		}
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
		sql.append("SELECT   C.DEPT_CHN_DESC, A.INV_CODE, A.INV_DESC, A.OWN_PRICE, A.UNIT_CODE,")
			.append("SUM (A.QTY) AS ALLQTY, SUM (A.AR_AMT) AS ALLFEE, B.DESCRIPTION")
			.append(" FROM SPC_INV_RECORD A, INV_BASE B, JAVAHIS.SYS_DEPT C ")
			.append(" WHERE A.INV_CODE = B.INV_CODE AND A.EXE_DEPT_CODE = C.DEPT_CODE ")
			.append(" AND A.BILL_DATE BETWEEN TO_DATE('").append(startDate).append("','YYYYMMDDHH24MISS')  ")
			.append(" AND TO_DATE('").append(endDate).append("','YYYYMMDDHH24MISS')" );
		if(mrNo != null && !mrNo.equals("")){
			sql.append(" AND A.MR_NO = '").append(mrNo).append("' ");
		}
		if(invCode.trim().length() > 0){
			sql.append(" AND A.INV_CODE = '").append(invCode).append("' ");
		}
		if(deptCode.length() > 0){
			sql.append(" AND A.EXE_DEPT_CODE = '").append(deptCode).append("' ");
		}
		if (classCode != null && !classCode.equals("")){
			sql.append(" AND A.CLASS_CODE = '").append(classCode).append("' ");
		}
		
		sql.append(" GROUP BY C.DEPT_CHN_DESC,A.INV_CODE,A.INV_DESC,A.OWN_PRICE,A.UNIT_CODE,B.DESCRIPTION")
			.append(" ORDER BY A.INV_CODE, A.INV_DESC ");
		
		//打印输出sql语句
		//System.out.println("sql="+sql);
		
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		double sumFee = 0.00;
		double allSumFee = 0.00;
		int count = 0;
		int allCount = 0;
		for (int i = 0; i < returnParm.getCount(); i++) {
			sumFee = returnParm.getDouble("ALLFEE", i);
			count = returnParm.getInt("ALLQTY", i);
			allSumFee += sumFee;
			allCount += count;
			
			
		}
		
		//this.Table.setParmValue(returnParm);
		int j = returnParm.getCount() + 1;
		//System.out.println("j="+j);
		//金额汇总
		returnParm.setData("DEPT_CHN_DESC", j, "合计:");
		returnParm.setData("INV_CODE", j, "");
		returnParm.setData("INV_DESC", j, "");
		returnParm.setData("OWN_PRICE", j, "");
		returnParm.setData("UNIT_CODE", j, "");
		returnParm.setData("DESCRIPTION", j, "");
		returnParm.setData("ALLQTY", j, allCount);
		returnParm.setData("ALLFEE", j, allSumFee);
		//System.out.println("returnParm="+returnParm);
		
		
		if(returnParm.getCount("DEPT_CHN_DESC") < 0){
			messageBox("查无数据！");
			TParm resultparm = new TParm();
			this.Table.setParmValue(resultparm);
			return;
		}
//		//将最后封装的TParm数据放到table控件中显示
		this.Table.setParmValue(returnParm);
		
		
		
	}
	
	/**
     * 清空方法
     */
    public void onClear() {
    	//初始化时间
    	Timestamp now = StringTool.getTimestamp(new Date());
	 	this.setValue("START_DATE",
	 			now.toString().substring(0, 10).replace('-', '/') + " 00:00:00");// 开始时间
	 	this.setValue("END_DATE",
	 			now.toString().substring(0, 10).replace('-', '/') + " 23:59:59");// 结束时间
	 	this.clearValue("MR_NO;PAT_NAME;INV_CODE;INV_DESC;DEPT_CODE;CLASS_CODE");
	 	Table.removeRowAll();
    }
    /**
     * 导出EXCEL
     */
    public void onExcel() {
    	if(Table.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(Table, "物联网物资汇总表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
     }
    }
	/**
	  * 补齐病案号
	  * */
	 public void onMrNo(){
		 String mrNo =PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO")));
		 this.setValue("MR_NO", mrNo);
		 
		 //modify by huangtt 20160928 EMPI患者查重提示  start     
         Pat pat = Pat.onQueryByMrNo(mrNo);       
 		 if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
 	            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
 	          setValue("MR_NO", pat.getMrNo());
 	         
 	     }        
		 
//		 String patName = PatTool.getInstance().getNameForMrno(
//					PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
 		 
		 this.setValue("PAT_NAME",pat.getName());
		 
		//modify by huangtt 20160928 EMPI患者查重提示  end
	 }
	 /**
     *
     * 初始化页面控件便于程序调用
     */
    private void initComponent(){
    	orgCombo=(TComboBox)this.getComponent("TYPE");
    }
	

}
