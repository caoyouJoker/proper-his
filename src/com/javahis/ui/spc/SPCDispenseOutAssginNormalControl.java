package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:住院药房普药-非静配 配药control
 * </p>
 * 
 * <p>
 * Description: 住院药房普药-非静配 配药control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 2013 05 31
 * @version 1.0
 */
	public class SPCDispenseOutAssginNormalControl extends TControl {
		
		
		TTable table_N;
		TTable table_Y;
	
		/**开始时间*/
		TTextFormat start_date;
		
		/**结束时间*/
		TTextFormat end_date;
	
		/**统药单table*/
		TTable table_order;
		
		/**统药单号控件*/
		TTextField order_id;
		
		/**统药单号 查询控件*/
		TTextField order_id_query;	
		
		/**摆药核对控件*/
		TTextField box_check;
		
		/**单个清空控件*/
		TTextField box_chlear;
		
		/**病患人数*/
		TTextField pat_count;	
		
		/**统药单病区*/
		String intStationCode = "";
		
		
		/**初始化方法*/
		public void onInit() {
			super.init();
			table_N = this.getTable("TABLE_N");//病患table
			table_Y = this.getTable("TABLE_Y");//药品明细table
			
			start_date = (TTextFormat) getComponent("START_DATE");//开始时间
			end_date = (TTextFormat) getComponent("END_DATE");//结束时间
			table_order = this.getTable("TABLE_ORDER");//统药单table
			order_id = (TTextField) getComponent("INTGMED_NO");//统药单号  隐藏
			order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");//统药单查询控件
			
			box_check = (TTextField) getComponent("BOX_CHECK");//摆药核对
			box_chlear = (TTextField) getComponent("BOX_CLEAR");//单个清空
			pat_count = (TTextField) getComponent("PAT_COUNT");	//病患人数

			// 初始化查询区间
			Timestamp date = SystemTool.getInstance().getDate();        
	        this.setValue("START_DATE",date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
	        this.setValue("END_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	        onQuery();
	        		
		}

		/**查询统药单*/
		public void onQuery() {
			TParm parm = new TParm();
			
			parm.setData("START_DATE", start_date.getValue());
			parm.setData("END_DATE", end_date.getValue());
			parm.setData("STATION_ID",this.getValueString("STATION_ID"));
			parm.setData("INTGMED_NO",order_id_query.getValue());
			
			TParm result = SPCDispenseOutAssginNormalTool
											.getInstance().query(parm);
			table_order.setParmValue(result);		
		}
	
		/**table_order 病区统药单单击事件，根据统药单查询病区病患*/
		public void tableOrderClicked(){
        
	       int row_m = table_order.getSelectedRow();
	        if (row_m != -1) {
	        	
	        	TParm result = table_order.getParmValue();
				TParm parm = result.getRow(row_m);
								
	        	order_id.setValue(parm.getValue("INTGMED_NO"));
	        	intStationCode = parm.getValue("STATION_CODE");
	        	
	    		onIntgmEdNo();  //order_table单击事件,为病区病患表格赋值 		
	        }
	        
	        int patient_count = table_N.getParmValue().getCount();
	        pat_count.setValue(String.valueOf(patient_count));
		}
		
		
		/**根据统药单号查询该病区病患明细 */
		public void onIntgmEdNo() {
			String intgmedNo = (String) getValue("INTGMED_NO");//隐藏的统药单号控件，即order_teble选中的统药单
			
			if (!StringUtil.isNullString(intgmedNo)){
				TParm parm = new TParm();
								
				parm = SPCDispenseOutAssginNormalTool
								.getInstance().queryPatientByIntgmedNo(intgmedNo,intStationCode);	//根据统药单号查询该病区病人

				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;
				//INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
				
				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;INTGMED_NO;CASE_NO;STATION_DESC;BED_NO;STATION_CODE
				
				if (null != parm && parm.getCount() > 0) {
					table_N.setParmValue(parm);
					
					String stationDesc = (String) parm.getData("STATION_DESC", 0);	//病区
					setValue("STATION_DESC", stationDesc);	//回显病区（已经隐藏）
										
					this.getTextField("STATION_DESC").setEditable(false);
					this.getTextField("INTGMED_NO").setEditable(false);
					
					this.getTextField("BOX_ESL_ID").grabFocus();//焦点落在绑定电子标签控件
					table_N.setSelectedRow(0);//选中table第一行
					
				}
				
			}
		}
	
	/**刷药盒，绑定病患与电子标签（更新药盒与病人的绑定，没绑定的药盒置空）*/
	public void onEleTagCode() {
		
		// 判断是否选中一条记录		
		table_N.acceptText();
		TParm parm = table_N.getParmValue();	//病患table的数据
		int currentRow = table_N.getSelectedRow(); 	//病患table中，当前绑定的行
		
		if(currentRow < 0){
			this.messageBox("请先选择更换药盒的病患");
			return;
		}
		
		//新药盒电子标签
		String newEleTag = getValueString("BOX_ESL_ID");	//新药盒电子标签
		
		TTextField BOX_ESL_ID = (TTextField) getComponent("BOX_ESL_ID");	//药盒电子标签控件
		BOX_ESL_ID.select(0, newEleTag.length());
		
		if (StringUtil.isNullString(newEleTag)) return;		//如果药盒电子标签为空，结束	
		
					
		//当前选中行的信息
		TParm rowParm = parm.getRow(currentRow);//病患table选中行的parma
		
		//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;
		//INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
		
		String mrNo = rowParm.getValue("MR_NO");//病案号
		String bedNoDesc = rowParm.getValue("BED_NO_DESC");//床位
		String patName = rowParm.getValue("PAT_NAME");//病患名称
		
		String oldEleTag =  rowParm.getValue("BOX_ESL_ID");//原电子标签      新增时为空
		String oldApRegion = rowParm.getValue("AP_REGION");//选中行的电子标签区域（原标签 所属区域）  新增时为空
		
		String caseNo = rowParm.getValue("CASE_NO");//就诊号 当前所选记录 就诊号
		String orderNo = rowParm.getValue("INTGMED_NO");//统药单号		
		
		String stationDesc = rowParm.getValue("STATION_DESC");//病区
		String stationCode = rowParm.getValue("STATION_CODE");//病区号
		
		//如果新值与旧值相等  则直接返回
//		if(null != oldEleTag){
//			if(oldEleTag.equals(newEleTag)){
//				return ;
//			}
//		}else{	//oldValue is null
//			if(null == newEleTag || "".equals(newEleTag.trim())){
//				return ;
//			}
//		}
		
		//如果新电子标签在系统中不存在  系统自动添加  结束
		TParm result = SPCDispenseOutAssginNormalTool
										.getInstance().queryEleTagByTagCode(newEleTag);
		
		if(result.getErrCode() < 0){
			this.messageBox("根据电子标签号查询电子标签出错！");
			return ;
    	}
		
		String newApReagion = result.getValue("AP_REGION",0) ;
		
		if(result.getCount() != 1){
//			this.messageBox("该电子标签在系统中不存在，请先维护！");
//			return ;
			//如果该标签在系统中不存在，则插入，插入前先查出AP_REGION
			
			//查询AP_REGION
			TParm res = SPCDispenseOutAssginNormalTool
								.getInstance().queryApRegion();
			
			if(res.getErrCode() < 0){
				this.messageBox("查询住院药房区域代码出错！");
				return ;
	    	}
			
			String inPatApRegion = res.getValue("AP_REGION",0);
			
			if(null == inPatApRegion || "".equals(inPatApRegion.trim()) ){
				this.messageBox("没有查询到住院药房区域代码！");
				return ;
			}
			
			String optUser = Operator.getID();
			String optTerm = Operator.getIP();
			
			res = SPCDispenseOutAssginNormalTool
							.getInstance().insertBoxLable(newEleTag,inPatApRegion,optUser, optTerm);
			
			if(res.getErrCode() < 0){
				this.messageBox("IND_MEDBOX新增电子标签出错！");
				return ;
	    	}
			newApReagion = inPatApRegion;
			
    	}
		
//		String newApReagion = result.getValue("AP_REGION",0) ;
		
		
		//如果所选记录已经绑定药盒，先将原药盒置空  并调用接口刷新电子标签
		if(null != oldEleTag && !"".equals(oldEleTag.trim())){
			
			//IND_INPATBOX清空改标签对应的case_no  staiton_code
			result = SPCDispenseOutAssginNormalTool
								.getInstance().clearInPatBoxCaseNoByEletagCode(oldEleTag);
			
			if(result.getErrCode() < 0){
				this.messageBox("置空IND_INPATBOX原电子标签CASE_NO、STATION_CODE出错！");
				return;
	    	}
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().clearCaseNoByEletagCode(oldEleTag);
			
			if(result.getErrCode() < 0){
				this.messageBox("置空原电子标签CASE_NO出错！");
				return;
			}
			
			/**调用接口 置空原电子标签信息*/
//			freshLable("","",
//						oldEleTag,3,oldApRegion);//二代
			
//			freshLable("","","",oldEleTag,3);
			
		}

		
		//判断IND_INPATBOX表中是否存在该标签 如果存在则更新，如果不存在则insert
		result = SPCDispenseOutAssginNormalTool
						.getInstance().queryBoxByEletag(newEleTag);
		
		if(result.getErrCode() < 0){
			this.messageBox("根据ELETAG_CODE查询IND_INPATBOX出错！");
			return;
    	}
		
		if(result.getCount() > 0){
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().updateIndPatBoxCaseNoByEletagCode(newEleTag,caseNo,stationCode);

			if(result.getErrCode() < 0){
				this.messageBox("更新IND_INPATBOX电子标签CASE_NO、STATION_CODE出错！");
				return;
			}
			
		}else{
			
			String optUser = Operator.getID();
			String optTerm = Operator.getIP();
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().insertInPatBoxBox(newEleTag,caseNo,stationCode,optUser,optTerm);
			
			if(result.getErrCode() < 0){
				this.messageBox("新增IND_INPATBOX记录出错！");
				return;
			}
		}
		
		
		result = SPCDispenseOutAssginNormalTool
						.getInstance().updateCaseNoByEletagCode(newEleTag,caseNo);
		
		if(result.getErrCode() < 0){
			this.messageBox("更新电子标签CASE_NO出错！");
			return;
		}
		
		
		/**调用接口 更新电子标签显示信息*/	
		freshLable(patName,stationDesc+" "+bedNoDesc,
					newEleTag,3,newApReagion);//二代
		
//		freshLable(patName,stationDesc,bedNoDesc,newEleTag,3);
		
		
				
		table_N.acceptText();
		table_N.setItem(currentRow, "BOX_ESL_ID", newEleTag);	//则将当前电子标签与该病患绑定（只是填到界面表中）
		table_N.setItem(currentRow, "AP_REGION", newApReagion);	//
		
		tableOrderClicked();//刷新病患信息table
//		table_N.setSelectedRow(currentRow );//选中原来那行
		BOX_ESL_ID.select(0, newEleTag.length());//更换药盒控件获得焦点
		
		onTableMClicked();	//显示下一个病患的药品明细
		this.setValue("BOX_ESL_ID", "");
		
		if(table_N.getParmValue().getCount() - 1 > currentRow){
			table_N.setSelectedRow(currentRow + 1);
		}else{
			this.messageBox("改病区病患已经绑定完毕！");
			this.getTextField("BOX_CHECK").grabFocus();//焦点落在绑定电子标签控件
		}
		
		return;
	}

	
	
	/**单个清空药盒*/
	public void onBoxClear(){
		String eletagCode = getValueString("BOX_CLEAR");//药盒电子标签		
		box_chlear.select(0, eletagCode.length());

		if (StringUtil.isNullString(eletagCode)) return;	//如果药盒电子标签为空，结束
		
		
		//查询AP_REGION
		TParm res = SPCDispenseOutAssginNormalTool
							.getInstance().queryApRegion();
		
		if(res.getErrCode() < 0){
			this.messageBox("查询住院药房区域代码出错！");
			return ;
    	}
		
		String inPatApRegion = res.getValue("AP_REGION",0);
		
		if(null == inPatApRegion || "".equals(inPatApRegion.trim()) ){
			this.messageBox("没有查询到住院药房区域代码！");
			return ;
		}

		
		//IND_INPATBOX清空改标签对应的case_no  staiton_code
		TParm result = SPCDispenseOutAssginNormalTool
							.getInstance().clearInPatBoxCaseNoByEletagCode(eletagCode);
		
		if(result.getErrCode() < 0){
			this.messageBox("置空IND_INPATBOX原电子标签CASE_NO、STATION_CODE出错！");
			return;
    	}
		
		result = SPCDispenseOutAssginNormalTool
							.getInstance().clearCaseNoByEletagCode(eletagCode);
		
		if(result.getErrCode() < 0){
			this.messageBox("置空原电子标签CASE_NO出错！");
			return;
		}
		
				
		/**调用接口 置空原电子标签信息*/
		freshLable("","",
				eletagCode,3,inPatApRegion);//二代
		
//		freshLable("","","",eletagCode,3);
		
		box_chlear.setValue("");
	}
	
	
	
	/**摆药核对*/
	public void onBoxCheck(){
		
		String eletagCode = getValueString("BOX_CHECK");//药盒电子标签		
		box_check.select(0, eletagCode.length());

		if (StringUtil.isNullString(eletagCode)) return;	//如果药盒电子标签为空，结束		
		
		// 将对应药盒  "核对"  标记选中
		table_N.acceptText();
		TParm parm = table_N.getParmValue();//病患table的数据
		
		for(int i=0; i<parm.getCount(); i++){
			String boxId = parm.getValue("BOX_ESL_ID",i);
			
			if(eletagCode.equals(boxId)){
				table_N.setItem(i, "SELECT_FLG", true);
				table_N.setParmValue(parm);
				table_N.setSelectedRow(i);

				onTableMClicked();
				break;
			}
		}
	
		this.setValue("BOX_CHECK", "");
		this.getTextField("BOX_CHECK").grabFocus();//焦点落在
		return;
	}
	
	/** 病患信息主项表格(TABLE_N)单击事件*/
	public void onTableMClicked() {
		TTable table = getTable("table_N");

		int row = table.getSelectedRow();
		if (row != -1) {
			TParm result = table.getParmValue();
			TParm parm = result.getRow(row);
			String intgmedNo = parm.getValue("INTGMED_NO");
			String caseNO = parm.getValue("CASE_NO");

			TParm detailParm = new TParm(
					TJDODBTool.getInstance().select(
							SPCDispenseOutAssginNormalTool
										.getInstance().getOrderCodeInfoDetailByPation(intgmedNo, caseNO)));
			table_Y.setParmValue(detailParm);
		}
		this.getTextField("BOX_ESL_ID").grabFocus();//焦点落在绑定电子标签控件
	}
	
	
	/**清空所有药盒标签*/
	public void onBind(){
		
		if (this.messageBox("提示", "确定要清空病区所有药盒电子标签?", 2) == 0) {
            
        }else {
           return;
        }
		
		TParm result = SPCDispenseOutAssginNormalTool
								.getInstance().queryAllBox();
		for(int i=0; i < result.getCount(); i++){
			String eleTagCode = result.getValue("ELETAG_CODE",i);
			String apRegion = result.getValue("AP_REGION",i);
			String caseNo = result.getValue("CASE_NO",i);
			
			//清空标签case_no
			TParm res = SPCDispenseOutAssginNormalTool
								.getInstance().clearCaseNoByEletagCode(eleTagCode);
			
			if(res.getErrCode() < 0){
				this.messageBox("置空电子标签就诊号出错！  标签号：" + eleTagCode);
				continue;
	    	}
			
			//调用接口置空标签显示内容
			freshLable("","",
						eleTagCode,3,apRegion);//二代
//			freshLable("","","",eleTagCode,3);
		}
		
		tableOrderClicked();
	}
	
	/**清空操作*/
	public void onClear() {
		String controlName = "INTGMED_NO;STATION_DESC;BOX_ESL_ID";
		this.clearValue(controlName);

		this.getTextField("INTGMED_NO").setEditable(true);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		this.getTextField("INTGMED_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
		pat_count.setValue("");
	}
	
	/**调用接口 （一代） */	
	private void freshLable(String name,String stationDesc,String bedNoDesc,
							String eleTag,int lightNum){
		System.out.println("1代-----name:"+name+"-----stationDesc:"+stationDesc+"-----eleTag:"+eleTag+"-----lightNum:"+lightNum+"-----bedNoDesc:"+bedNoDesc);
        try{
        	EleTagControl.getInstance().login();
        }catch(Exception e){
        	this.messageBox("电子标签服务器登录失败,电子标签无法更新!");
        }
		
		try{
			EleTagControl.getInstance().sendEleTag(eleTag, name, stationDesc,bedNoDesc, lightNum);
							
		}catch(Exception e){	
			this.messageBox("更新电子标签显示信息时出错，标签号：" + eleTag);
			System.out.println("--绑定电子标签出错---EleTagControl.getInstance().sendEleTag----");
		}
		
	}

	/**调用接口 （二代） */	
	private void freshLable(String name,String spc,
							String eleTag,int lightNum,
							String apRegion){

		System.out.println("2代-----name:"+name+"-----spc:"+spc+"-----eleTag:"+eleTag+"-----lightNum:"+lightNum+"-----apRegion:"+apRegion);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("ProductName", name);
		map.put("SPECIFICATION",  spc);
		map.put("TagNo", eleTag);
		map.put("Light", lightNum);
		map.put("APRegion", apRegion);
		
		list.add(map);
		
		try{
			String url = Constant.LABELDATA_URL ;
			LabelControl.getInstance().sendLabelDate(list, url);
		}catch (Exception e) {
	    	System.out.println("调用电子标签服务失败");
	    	this.messageBox("调用电子标签服务失败");
		}
		
	}
	
	/**获得table*/
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}
	
	/**获得输入域*/
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
}
