package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/** 
 * 
 * <p>Title: 药库普药上架(入库)</p>
 *
 * <p>Description:TODO 药库普药上架</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCGenDrugPutUpControl  extends TControl {
	
	
	//未入库
	TTable table_N;
	
	// 已入库
	TTable table_Y;
	
	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	TParm parm1 = new TParm();
	int k = 0;
	
	//验收单号
	String  verifyinNo;
	
	//货箱电子标签
	String  contaninerTags ;
	
	//电子标签
	String  electraonicTags;
	
	String orgCode = "";
	
	
	/**
	 * 电子标签
	 */
	TParm labelParm = new TParm();
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// TFrame tFrame = (TFrame) getComponent("UI");
		// final TTextField mrField = (TTextField)
		// getComponent("DIANZIBIAOQIAN");
		// tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		// public void windowOpened(java.awt.event.WindowEvent evt) {
		// mrField.requestFocus();
		// }
		// });
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		 
	}

	
	public void onQuery(){
		
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		TParm parm = new TParm();
		parm.setData("VERIFYIN_NO",verifyinNo);
		parm.setData("CONTAINER_TAGS",contaninerTags);
		
		if(N_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","N");
		}
		if(Y_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","Y");
			
			String startDate = getValueString("START_DATE");
			if(startDate != null && !startDate.equals("")){
				startDate = startDate.substring(0,10);
				parm.setData("START_DATE",startDate);
			}
			String endDate = getValueString("END_DATE");
			if(endDate != null && !endDate.equals("")){
				endDate = endDate.substring(0,10);
				parm.setData("END_DATE",endDate);
			}
			
		}
		TParm result = SPCGenDrugPutUpTool.getInstance().onQuery(parm);
		//不是深拷呗的原因，重新查询一遍
		labelParm = SPCGenDrugPutUpTool.getInstance().onQuery(parm);
		this.setValue("VERIFYIN_NO", "");
		this.setValue("CONTAINER_TAGS", "");
		
		if(result.getCount() <  0 ){
			this.messageBox("没有查询到数据");
			if(N_PANEL.isShowing()){
				table_N.setParmValue(new TParm());
			}
			if(Y_PANEL.isShowing()){
				table_Y.setParmValue(new TParm());
			}
			return ;
		}
		
		if(N_PANEL.isShowing()){
			
			//TParm tableParm = table_N.getParmValue() ;
		//	result.addParm(tableParm);   
			table_N.setParmValue(result);
			table_Y.setParmValue(null);
			
			
			/**
			 * 去除重复合并
			 */
			
			int count = labelParm.getCount() ;
			for(int i = 0;i < count;i++) { 
				TParm rowParm = labelParm.getRow(i);
				String orderCode = rowParm.getValue("ORDER_CODE") ;
				 
				for(int j = i+1;j < count;j++){ 
					TParm rowParm1 = labelParm.getRow(j);
					String orderCode1 = rowParm1.getValue("ORDER_CODE") ;
					 
				   if(orderCode.equals(orderCode1)){ 
				   		 
					   labelParm.removeRow(j); 
					   j--;
					   count--;
				   }
				}
			} 
			labelParm.setCount(count);
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			//调用电子标签接口
			for(int i = 0 ; i < labelParm.getCount() ; i++ ){
				TParm rowParm = (TParm)labelParm.getRow(i);
				
				
				String orgCode = rowParm.getValue("ORG_CODE");
				String apRegion = getApRegion(orgCode);
				
				if(apRegion == null || apRegion.equals("")){
					this.messageBox("没有设置电子标签区域 "+orgCode);
					continue ;
				}
				
				
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				String orderDesc = rowParm.getValue("ORDER_DESC");
				String eletagCode = rowParm.getValue("ELETAG_CODE");
				String spec = rowParm.getValue("SPECIFICATION");
				if(null!= spec && spec.length()>11)  {
					spec = spec.substring(0, 11);
				}
				map.put("ProductName", orderDesc);
				map.put("SPECIFICATION", spec);
				map.put("TagNo", eletagCode);
				map.put("Light", 50);
				
				
				map.put("APRegion", apRegion);
				list.add(map);
								
			}
			try{
				String url = Constant.LABELDATA_URL ;
				LabelControl.getInstance().sendLabelDate(list, url);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("上架查询后调用电子标签失败");
			}
			
		}
		if(Y_PANEL.isShowing()){
			table_Y.setParmValue(result);
			table_N.setParmValue(null);
		}
		
	}



	/**
	 * 货箱单号、验收单号回车事件
	 * */
	public void onMonCHD() {
		onQuery();
		this.getTextField("ELECTRONIC_TAGS").grabFocus();
		return ;
	}

	/**
	 * 货位电子标签的回车事件
	 * */
	public void onClickByElectTags() {
		electraonicTags =  getValueString("ELECTRONIC_TAGS");
		if (electraonicTags.length() == 0)
	        return;
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		table_N.acceptText();
		//搜索IND_MATERIALLOC,得到ORDER_CODE
		TParm tabParm = table_N.getParmValue();		
		String labelNo = "";
		String productName = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		 
		int count = table_N.getParmValue().getCount();
		boolean flg = false;
		TParm eleParm = null;
		for (int i = 0; i < count; i++) {
			TParm rowParm = tabParm.getRow(i);		
			//判断是否找到
			if(electraonicTags.equals(rowParm.getData("ELETAG_CODE"))){				 
				productName = rowParm.getValue("ORDER_DESC");
				orgCode = rowParm.getValue("ORG_CODE");
				orderCode= rowParm.getValue("ORDER_CODE");
				
				TParm result = onSaveStock(i);
				if (result.getErrCode() < 0) {
					this.messageBox("保存失败");
					break ;
					
				}
				//找到移出一条  ，调用电子标签接口显示库存与闪烁
				
				table_N.removeRow(i);
				eleParm = new TParm();
				eleParm.setData("ELE_TAG",electraonicTags);
				eleParm.setData("ORDER_CODE",orderCode);
				eleParm.setData("ORG_CODE",orgCode);
				eleParm.setData("ELETAG_CODE",electraonicTags);
				labelNo = electraonicTags;
				/*TParm inParm = new TParm();
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("ORG_CODE",orgCode);
				inParm.setData("ELETAG_CODE",electraonicTags);
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
				spec = outParm.getValue("SPECIFICATION");*/
				flg = true;
				i--;
				}
			 
		}
		if(flg) {
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(eleParm);	
				if(outParm.getCount() >  0 ){
					spec = outParm.getValue("SPECIFICATION",0);
				}
				
				 
				String apRegion = getApRegion(orgCode);
				if(apRegion == null || apRegion.equals("")){
					this.messageBox("没有设置电子标签区域 "+orgCode);
					 return ;
				}
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				 
				if(null!= spec && spec.length()>11)  {
					spec = spec.substring(0, 11);
				}
				map.put("ProductName", productName);
				map.put("SPECIFICATION", spec+" "+outParm.getValue("QTY",0));
				map.put("TagNo", labelNo);
				map.put("Light", 0);
				map.put("APRegion",apRegion);
				list.add(map);
				try{
					String url = Constant.LABELDATA_URL ;
					LabelControl.getInstance().sendLabelDate(list, url);
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("上架入库后调用电子标签失败");
				}
				 

		}
		this.setValue("VERIFYIN_NO", verifyinNo);
		this.setValue("CONTAINER_TAGS", contaninerTags);
		this.clearValue("ELECTRONIC_TAGS");
		
		setValue("ELETAG_CODE", "");
		
		/**
		TParm tabNowParm = table_N.getParmValue();
		//判断是否全上架完成
		for(int k = 0 ; k < count ; k++ ){
			TParm rowParm = tabParm.getRow(k);
			String materiaLoc = rowParm.getValue("MATERIAL_LOC_CODE");
			if(!StringUtil.isNullString(materiaLoc)){
				materiaLoc = materiaLoc.substring(0,3);
			}
			boolean bo = true;
			for(int j = 0 ; j < tabNowParm.getCount() ; j++ ){
				TParm rowSecParm = tabNowParm.getRow(j);
				String materiaSec = rowSecParm.getValue("MATERIAL_LOC_CODE");
				if(!StringUtil.isNullString(materiaSec)){
					materiaSec = materiaSec.substring(0,3);
				}
				
				if(materiaLoc.equals(materiaSec)){
					bo = false;
					break ;
				}
			}
			
			//货位排标签灯灭
			if(bo){
				login();
				sendEleTag(materiaLoc+"000", materiaLoc+"000", "", "", 0);
			}
		}*/
		return;
	}
	
	public void onTPanlClick() {
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		onQuery();
		return;
	}
	
	
	/**
	 * 上架入库
	 * @param i  当前匹配上的数据行
	 */
	public TParm onSaveStock(int i){
		TParm parm = new TParm();
		
		TParm result = new TParm() ;
		//String org_code = "040101";
		parm.setData("IS_PUTAWAY","Y");
		parm.setData("PUTAWAY_USER",Operator.getID());
		//parm.setData("ORG_CODE", org_code);
		/*parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		parm.setData("PLAN_NO", getValue("PLAN_NO"));
		parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
		parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));*/
		
		parm.setData("PLAN_NO", "");
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("DESCRIPTION", "");
		// 审核人员
		parm.setData("CHECK_USER", Operator.getID());
		
		Timestamp date = SystemTool.getInstance().getDate();
		// 审核时间
		parm.setData("CHECK_DATE", date);
		// 验收量
		double qty = 0.00;
		// 赠送量
		double g_qty = 0.00;
		// 订购量
		double pur_qty = 0.00;
		// 库存量
		double stock_qty = 0.00;
		// 药品代码
		String order_code = "";
		// 出入库数量转换率
		TParm getTRA = new TParm();
		// 出入库效期批号者
		TParm getSEQ = new TParm();
		// 进货转换率
		double s_qty = 0.00;
		// 库存转换率
		double d_qty = 0.00;
		// 批号
		String batch_no = "";
		// 批次序号
		int batch_seq = 1;
		// 有效期
		String valid_date = "";
		// 更新或新增IND_STOCK的FLG->U:更新;I,新增
		String flg = "";
		// 零售价格
		double retail = 0.00;
		// 验收单价
		double verprice = 0.00;
		// 累计质量扣款金额
		double deduct_atm = 0.00;
		// 库存平均价
		double stock_price = 0.00;
		// 订购单号
		String pur_no = "";
		// 订购单号序号
		int pur_no_seq = 0;
		//周转箱 
		String spcBoxCode = "";

		// 得到IND_SYSPARM信息
		result = IndSysParmTool.getInstance().onQuery();
		
		
		parm.setData("REUPRICE_FLG", "Y");

		// 判断是否自动将最后一次验收入库单价维护至药品基本档中批发价
		parm.setData("UPDATE_TRADE_PRICE",  "Y" );

		// 细项信息
		String material_loc_code = "";

		// 入库单号集合
		// 药品代码集合
		TParm rowParm = table_N.getParmValue();
		String org_code=rowParm.getValue("ORG_CODE",i);  
		parm.setData("ORG_CODE", org_code);
		String verifyin_no = rowParm.getValue("VERIFYIN_NO",i);
		parm.setData("VERIFYIN", verifyin_no);
		order_code = rowParm.getValue("ORDER_CODE", i);
		parm.setData("ORDER_CODE", order_code);
		
		//供应商
		String supCode = rowParm.getValue("SUP_CODE",i) ;
		parm.setData("SUP_CODE", supCode);  
		// 出入库数量转换率                       
		getTRA = INDTool.getInstance().getTransunitByCode(order_code); 
		if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
			this.messageBox("药品" + order_code + "转换率错误");
			return getTRA;
		}
		// 进货转换率集合
		s_qty = getTRA.getDouble("STOCK_QTY", 0);
		parm.setData("STOCK_QTY", s_qty);
		// 库存转换率集合
		d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
		parm.setData("DOSAGE_QTY", d_qty);
		// 库存量
		stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
		parm.setData("QTY", stock_qty);  
		// 批号集合              
		//batch_no = dataStore.getItemString(i, "BATCH_NO");
		batch_no = rowParm.getValue("BATCH_NO",i);
		parm.setData("BATCH_NO", batch_no);
		// 有效期   
		valid_date = StringTool.getString(rowParm.getTimestamp("VALID_DATE",i),"yyyy/MM/dd");      
		parm.setData("VALID_DATE", valid_date);
		//20121128 liyh添加周转箱
		spcBoxCode = rowParm.getValue("SPC_BOX_BARCODE",i);
		parm.setData("SPC_BOX_BARCODE", spcBoxCode);
		// *************************************************************
		// luahi 2012-01-10 add 加入批次序号选取条件加入验收价格条件begin
		// *************************************************************
		// 出入库效期批号者 
		// getSEQ =
		// IndStockDTool.getInstance().onQueryStockBatchSeq(
		// org_code, order_code, batch_no, valid_date);
		// add by liyh 20120614 在IND_STOCK
		// 里查询药品验收价格时，应该是片/支的价格（最小单位的价格） start
		double verpriceD = rowParm.getDouble("VERIFYIN_PRICE",i) / d_qty ;
				
		verpriceD = StringTool.round(verpriceD, 4);
		String verpriceDStr = String.valueOf(verpriceD);
		 
 
		//供应商药品编码
		String supOrderCode = rowParm.getValue("SUP_ORDER_CODE",i);
		parm.setData("SUP_ORDER_CODE",supOrderCode);
		
		getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeqBy(org_code, order_code, batch_no, valid_date, verpriceDStr,supCode,supOrderCode);//
		
		// *************************************************************
		// luahi 2012-01-10 add 加入批次序号选取条件加入验收价格条件end
		// *************************************************************
		if (getSEQ.getErrCode() < 0) {
			this.messageBox("药品" + order_code + "批次序号错误");
			return getSEQ;
		}
		if (getSEQ.getCount("BATCH_SEQ") > 0) {
			flg = "U";
			// 该批次药品存在
			batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
		} else {
			flg = "I";
			// 该批次药品不存在,抓取该库存药品最大+1批次序号新增
			getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
			if (getSEQ.getErrCode() < 0) {
				this.messageBox("药品" + order_code + "批次序号错误");
				return getSEQ;
			}
			// 最大+1批次序号
			batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
			material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
		}
		// 出入库效期批号者集合
		parm.setData("BATCH_SEQ", batch_seq);
		// 料位
		parm.setData("MATERIAL_LOC_CODE", material_loc_code);
		// 更新或新增IND_STOCK的FLG集合
		parm.setData("UI_FLG", flg);
		// 验收量集合
		
		qty = rowParm.getDouble("VERIFYIN_QTY",i);
		
		parm.setData("VERIFYIN_QTY", qty);
		// 赠送量集合
		g_qty = rowParm.getDouble("GIFT_QTY",i);
		
		parm.setData("GIFT_QTY",g_qty);

		// 零售价格集合
		retail = rowParm.getDouble("RETAIL_PRICE",i);
		
		parm.setData("RETAIL_PRICE", retail);
		
		// 验收价格集合 修改验收价格的单位为配药单位
		double verifyinPrice = rowParm.getDouble("VERIFYIN_PRICE",i);
		//整合价格
		parm.setData("INVENT_PRICE",verifyinPrice);
		
		verprice = verifyinPrice/ d_qty ;
		
		
		parm.setData("VERIFYIN_PRICE", StringTool.round(verprice, 4));
		// zhangyong20091014 end
		// 验收序号集合
		int seq_no = rowParm.getInt("SEQ_NO",i);
		parm.setData("SEQ_NO", seq_no);
		// 累计质量扣款金额集合
		deduct_atm =rowParm.getDouble("QUALITY_DEDUCT_AMT",i); 		
		parm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
		// 订购单号集合
		pur_no = rowParm.getValue("PURORDER_NO",i);	
		parm.setData("PURORDER_NO", pur_no);
		// 订购单号序号集合
		pur_no_seq = rowParm.getInt("PURSEQ_NO",i);
		
		parm.setData("PURSEQ_NO", pur_no_seq);

	/*	TParm inparm = new TParm();
		inparm.setData("PURORDER_NO", pur_no);
		inparm.setData("SEQ_NO", pur_no_seq);
		result = IndPurorderMTool.getInstance().onQueryDone(inparm);
		if (result.getCount() == 0 || result.getErrCode() < 0) {
			this.messageBox("错误");
			return;
		}*/
		// 订购量集合		                        
		pur_qty = result.getDouble("PURORDER_QTY", 0);
		parm.setData("PURORDER_QTY",pur_qty);
		// 库存平均价集合
		stock_price = result.getDouble("STOCK_PRICE", 0);
		parm.setData("STOCK_PRICE", stock_price);
		
		// 累计验收数
		parm.setData("ACTUAL_QTY", result.getDouble("ACTUAL_QTY", i));
		// 状态
		String update_flg = "3";
		parm.setData("UPDATE_FLG",update_flg);
 
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());	
		parm.setData("VERIFYIN_NO", verifyin_no);
		 
		parm.setData("PRC",rowParm.getDouble("PRC",i));
		
		// 执行数据更新
		result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onUpdateSpc", parm);
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("E0001");
			return result;
		}   
		return result;
		//this.messageBox("P0001");
	}
	

	private String getApRegion( String orgCode) {
		TParm searchParm = new TParm () ;
		searchParm.setData("ORG_CODE",orgCode);
		TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
		String apRegion = "";
		if(resulTParm != null ){
			apRegion = resulTParm.getValue("AP_REGION");
		}
		return apRegion;
	}
	


	/**
	 * 清空操作
	 * */
	public void onClear() {
		String controlName = "VERIFYIN_NO;CONTAINER_TAGS;ELECTRONIC_TAGS";
		this.clearValue(controlName);
		this.getTextField("VERIFYIN_NO").setEditable(true);
		table_Y.removeRowAll();
		table_N.removeRowAll();
		this.getTextField("CONTAINER_TAGS").grabFocus();
	}
	
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}
	
}
