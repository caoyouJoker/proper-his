package com.javahis.ui.ibs;



import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;


/**
 * <p>
 * Title:跨年医保修改床位费
 * </p>
 * 
 * <p>
 * Description: 跨年医保修改床位费
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 */

public class IBSKnInsControl extends TControl {
	/**
	 * 医嘱TABLE
	 */
	private TTable tableKNINSINFO;// tableKNINSINFO数据
	private String  nowdate ="";
	public void onInit() { // 初始化程序
		super.onInit();
		tableKNINSINFO = (TTable) this.getComponent("KNINSINFO_TABLE");// tableKNINSINFO收费数据
		Timestamp sysDate = SystemTool.getInstance().getDate();
		nowdate = StringTool.getString(sysDate,"yyyyMMdd");//"20130101";
//		System.out.println("nowdate==="+nowdate);
		if(!nowdate.substring(4,8).equals("0101")){
		this.messageBox("非跨年第一天不能做跨年结算");
		callFunction("UI|save|setEnabled", false);// 保存操作
		callFunction("UI|query|setEnabled", false);// 查询操作
		}
		this.setValue("DATE",StringTool.rollDate(sysDate, -1));
	}
	/**
	 * 
	 * @param 获得KNINSINFO_TABLE数据
	 * @param 按钮
	 * @return
	 */
	public void onQuery(){
		//判断是否为空	
		if(checkQuery())
		   return;
//		String date = "20131226";
		String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "DATE")), "yyyyMMdd"); //拿到界面的时间
		//获得KNINSINFO_TABLE数据
		GetKnInsInf(date);	
	}
	//获得KNINSINFO_TABLE的数据
	public void GetKnInsInf(String date) {
		tableKNINSINFO.removeRowAll();	
		String sql =  
			" SELECT A.BED_NO,E.PAT_NAME,C.CTZ_DESC,A.CASE_NO ,D.ORDER_CODE,F.ORDER_CHN_DESC," +
			" TO_CHAR(F.BILL_DATE,'yyyy/MM/dd HH:mm:ss') AS BILL_DATE,F.CASE_NO_SEQ"+
		    " FROM SYS_BED A,ADM_INP B,SYS_CTZ C,SYS_BEDFEE D,SYS_PATINFO E,IBS_ORDD F"+
		    " WHERE A.MR_NO IS NOT NULL"+ 
		    " AND A.MR_NO = E.MR_NO"+
		    " AND A.CASE_NO = B.CASE_NO"+ 
		    " AND B.CTZ1_CODE = C.CTZ_CODE"+ 
		    " AND C.NHI_CTZ_FLG = 'Y'"+  
		    " AND A.CASE_NO = F.CASE_NO"+ 
		    " AND D.ORDER_CODE = F.ORDER_CODE"+ 
		    " AND D.BED_CLASS_CODE = A.BED_CLASS_CODE"+ 
		    " AND A.BED_OCCU_FLG = D.BED_OCCU_FLG"+ 
		    " AND TO_CHAR(F.BILL_DATE,'YYYYMMDD') ='"+date+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result===query"+result);	
		if (result.getErrCode() < 0) {
			this.messageBox("查询数据有问题");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			String Sdate = result.getValue("BILL_DATE", i).substring(0, 10);
			Sdate += " 23:59:59";
			result.setData("BILL_DATE", i, Sdate);
		}
		tableKNINSINFO.setParmValue(result);	
	}
	/**
	 * 保存操作
	 */
	public void onSave() {
		TParm result = new TParm();
		TParm result1 = new TParm();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String  nowdateupdate =StringTool.getString(sysDate,"yyyyMMdd");//"20130101";
		nowdateupdate = nowdateupdate+"000001";
		TParm Data =tableKNINSINFO.getParmValue();
//		System.out.println("Data==="+Data);
		if(Data==null){
		this.messageBox("无修改床位费数据，请先查询数据");	
		 return;	
		}		
		int count = Data.getCount();
		for (int i = 0; i < count; i++) {
			TParm tempParm = new TParm();
			tempParm = Data.getRow(i);			
		String 	billdate = tempParm.getValue("BILL_DATE").substring(0, 10).replace("/", "");
		String  caseno = tempParm.getValue("CASE_NO");
		String  ordercode = tempParm.getValue("ORDER_CODE");
		String  casenoseq = tempParm.getValue("CASE_NO_SEQ");
//		System.out.println("casenoseq==="+casenoseq);
		//修改IBS_ORDD表
		String sql = 
		" UPDATE IBS_ORDD SET KN_FLG  = 'Y',"+
		" BILL_DATE =  TO_DATE('"+nowdateupdate+"','YYYYMMDDHH24MISS')"+
		" WHERE CASE_NO  =  '"+caseno+"' "+
		" AND ORDER_CODE = '"+ordercode+"' "+
		" AND TO_CHAR(BILL_DATE,'YYYYMMDD') = '"+billdate+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql));// 修改床位费
		//修改IBS_ORDM表
		String sql1 = 
		" UPDATE IBS_ORDM SET BILL_DATE =  TO_DATE('"+nowdateupdate+"','YYYYMMDDHH24MISS')"+
		" WHERE CASE_NO  =  '"+caseno+"' "+
		" AND CASE_NO_SEQ = '"+casenoseq+"' ";
		result1 = new TParm(TJDODBTool.getInstance().update(sql1));// 修改床位费	
	}
		if (result.getErrCode() < 0||result1.getErrCode() < 0){ 
			this.messageBox("执行失败");
		 return;	
		}		   
		else 
			this.messageBox("修正床位费成功：\n"+
	                         "共计床位："+ count);			 

		 tableKNINSINFO.removeRowAll();		
}	

	//判断是否为空
	private boolean checkQuery(){
	   if(StringTool.getString(TCM_Transform.getTimestamp(getValue(
		"DATE")), "yyyyMMdd").length() == 0){
		messageBox("查询日期不能为空");
		return true;
	}
	return false;
 }
	/**
	 * 清空操作
	 */
	public void onClear() {
		tableKNINSINFO.removeRowAll();	
	}	
}
