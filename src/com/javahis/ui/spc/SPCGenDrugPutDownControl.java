package com.javahis.ui.spc;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jdo.ind.Constant;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.spc.SPCGenDrugPutDownTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;

import org.apache.http.message.BasicNameValuePair;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:药库普药下架control
 * </p>
 * 
 * <p>
 * Description: 药库普药下架control
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Yuanxm 20121016
 * @version 1.0
 */
public class SPCGenDrugPutDownControl extends TControl {
	TTable table_N;
	TTable table_Y;
	
	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	String boxEslId;// 周转箱
	TParm parm1 = new TParm();
	int k = 0;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.init();
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		
		/**
		 TPanel tPanel = (TPanel)getComponent();
	 
		 TFrame tFrame = (TFrame)tPanel.getParentComponent().getParentComponent() ;
			
			
		final TTextField dispenseNo = this.getTextField("DISPENSE_NO");
		 dispenseNo.setFocusable(true);
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				 dispenseNo.requestFocus(true);
			}
		});
		*/
	}
	
	public void onQuery(){
		String dispenseNo = (String)getValue("DISPENSE_NO");
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		
		if(!StringUtil.isNullString(dispenseNo)){
			TParm parm = new TParm();
			parm.setData("DISPENSE_NO",dispenseNo);
			
			if(N_PANEL.isShowing()){
				parm.setData("IS_BOXED","N");
			}else if(Y_PANEL.isShowing()){
				parm.setData("IS_BOXED","Y");
			}
			
			TParm result = SPCGenDrugPutDownTool.getInstance().onQuery(parm);
			if(result.getCount() <  0 ){
				if(N_PANEL.isShowing()){
					table_N.setParmValue(new TParm());
				}
				if(Y_PANEL.isShowing()){
					table_Y.setParmValue(new TParm());
				}
				return ;
			}
			
			String orgChnDesc = (String)result.getData( "ORG_CHN_DESC",0);
			setValue("ORG_CHN_DESC", orgChnDesc);
			this.getTextField("ORG_CHN_DESC").setEditable(false);
			this.getTextField("DISPENSE_NO").setEditable(false);
			
			if(N_PANEL.isShowing()){
				table_N.setParmValue(result);
			}else if(Y_PANEL.isShowing()){
				table_Y.setParmValue(result);
			}
		}
		
	}
	
	
	public void onAgain(){
		this.clearValue("BOX_ESL_ID");
		this.getTextField("BOX_ESL_ID").grabFocus();
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	

	/**
	 * 出库单的回车事件
	 * */
	public void onMonByCKD() {
				
		onQuery();
		this.getTextField("BOX_ESL_ID").grabFocus();
	}

	/**
	 * 周转箱的回车事件
	 * */
	public void onMonByZZX() {
		boxEslId = this.getValueString("BOX_ESL_ID");
		
		table_N = this.getTable("TABLE_N");
		table_N.acceptText();

		N_PANEL = (TPanel)getComponent("N_PANEL");
		if(N_PANEL.isShowing()) {
			String boxEslId = getValueString("BOX_ESL_ID");
			String dispenseNo = getValueString("DISPENSE_NO");
			if(StringUtil.isNullString(boxEslId)){
				this.messageBox("周转箱为空!");
				return ;
			}
			
			TParm parm  = table_N.getParmValue() ;
			int count = parm.getCount() ;
			if(count <= 0 ){
				return ;
			}
			
			//判断是否选中有没有要下架装箱的
			String orgChnDesc = (String)getValue("ORG_CHN_DESC");
			
			login();
			sendEleTag(boxEslId, orgChnDesc, dispenseNo+ "普药", "", 0);
			
			for(int i = 0 ; i < count; i++ ){
				TParm rowParm = parm.getRow(i);
				
				if (rowParm.getBoolean("CHOOSE")) {
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(rowParm);
					String spec = outParm.getValue("SPECIFICATION");
					EleTagControl.getInstance().login();
					EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), rowParm.getValue("ORDER_DESC"), spec, outParm.getValue("QTY",0), 0);
					table_N.removeRow(i);
				}
			}
			 
		}
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * 电子标签的回车事件
	 * */
	public void onMonByDianzbq() {
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		if(!Y_PANEL.isShowing()){
			String eletagCode=this.getValueString("ELETAG_CODE");
			boxEslId = this.getValueString("BOX_ESL_ID");
			String dispenseNo = (String)getValue("DISPENSE_NO");
			
			if(StringUtil.isNullString(eletagCode) || StringUtil.isNullString(boxEslId)){
				return;
			}
			table_N.acceptText();
			
			String labelNo = "";
			String productName = "";
			String orgCode = "";
			String orderCode = "";
			String spec = "";
			TParm parm = table_N.getParmValue();
			int count = table_N.getParmValue().getCount();
	
			boolean b = false;
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				String eleCode = rowParm.getValue("ELETAG_CODE");
				if(eletagCode.equals(eleCode)){
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("IS_BOXED","Y");
					labelNo = eletagCode;
					productName = rowParm.getValue("ORDER_DESC");
					orgCode = rowParm.getValue("ORG_CODE");
					orderCode= rowParm.getValue("ORDER_CODE");
					rowParm.setData("BOXED_USER",Operator.getID());
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					table_N.removeRow(i);
					b = true;
					break;
				}
			}
			
			//是的，调用电子标签接口显示库存与闪烁
			if(b){
				TParm inParm = new TParm();
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("ORG_CODE",orgCode);
				inParm.setData("ELETAG_CODE",eletagCode);
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
				spec = outParm.getValue("SPECIFICATION");
				EleTagControl.getInstance().login();
				EleTagControl.getInstance().sendEleTag(labelNo, productName, spec, outParm.getValue("QTY",0), 0);
				 
			}
			this.setValue("ELETAG_CODE", "");
			return;
		}
	}

	public void onChange() {
		 onQuery();
	}
	
	public void onSave(){
		
		table_N = this.getTable("TABLE_N");
		table_N.acceptText();

		N_PANEL = (TPanel)getComponent("N_PANEL");
		if(N_PANEL.isShowing()) {
			String boxEslId = getValueString("BOX_ESL_ID");
			String dispenseNo = getValueString("DISPENSE_NO");
			if(StringUtil.isNullString(boxEslId)){
				this.messageBox("周转箱为空!");
				return ;
			}
			
			TParm parm  = table_N.getParmValue() ;
			int count = parm.getCount() ;
			if(count <= 0 ){
				return ;
			}
			
			for(int i = 0 ; i < count; i++ ){
				TParm rowParm = parm.getRow(i);
				
				if (rowParm.getBoolean("CHOOSE")) {
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					 
					TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(rowParm);
					String spec = outParm.getValue("SPECIFICATION");
					EleTagControl.getInstance().login();
					EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), rowParm.getValue("ORDER_DESC"), spec, outParm.getValue("QTY",0), 0);
					table_N.removeRow(i);
				}
			}
			//onQuery();
		}
		
		
	}

	/**
	 * 清空操作
	 * */
	public void onClear() {
		String controlName = "DISPENSE_NO;ORG_CHN_DESC;BOX_ESL_ID;ELETAG_CODE";
		this.clearValue(controlName);
	 
		this.getTextField("ORG_CHN_DESC").setEditable(true);
		this.getTextField("DISPENSE_NO").setEditable(true); 
		this.getTextField("DISPENSE_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
	}
	
	public static void login(){
		ElectronicTagsInf eti = new ElectronicTagsImpl() ;
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			Constant.parameters.clear();
		}
		 
		Constant.parameters.add(0,new BasicNameValuePair("Token", (String)map.get("Token")));
		Constant.parameters.add(1,new BasicNameValuePair("UserId", (String)map.get("UserId")));
		Constant.parameters.add(2,new BasicNameValuePair("RoleId", (String)map.get("RoleId")));
	}
	
    /**
     * 更新电子标签
     * @param bastkId  电子标签ID
     * @param nameInfo 
     * @param spec
     * @param num
     * @param lightNum TODO
     * @return
     * @author liyh
     * @date 20120919
     */
	  public boolean sendEleTag(String bastkId,String nameInfo,String spec ,String num, int lightNum){
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if(null == uuid )
			uuid = UUID.randomUUID();
		
		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// 电子标签id
		m.put("LabelNo", bastkId);
		// 基站id
		m.put("StationID", "2");
		// 第一行 显示用户名和年龄
		m.put("ProductName", nameInfo);
		// 第二回 显示病案号 和性别
		m.put("Spec", spec + " " + num);
		// 第三行 电子标签二维条码含义
		 
		m.put("ShelfNo", bastkId);
	 
		// 闪烁次数
		m.put("Light", lightNum);
		// 是否亮灯：true:亮
		m.put("Enabled", true);
		
		Iterator it = m.entrySet().iterator();
		System.out.println("------------发药--电子标签更新内容---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// 调用电子标签接口
		Map<String, Object> map = eti.cargoUpdate(m);
		
		
		if(null != map){
			it = map.entrySet().iterator();
			 
	    	String status = (String) map.get("Status");
	    	if(null != status && "10000".equals(status)){//更新电子标签状态成功
	    		return true;
	    	}else{
	    		return false;
	    	}			
		}else{
			return false;
		}
		
		
    }
}
