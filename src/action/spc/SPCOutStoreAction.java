package action.spc;


import jdo.spc.INDTool;
import jdo.spc.INDSQL;
import jdo.spc.IndDispenseDTool;
import jdo.spc.IndDispenseMTool;
import jdo.spc.IndRequestDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCErOutCabinetTool;
import jdo.spc.SPCOutStoreMLTool;
import jdo.spc.SPCOutStoreTool;
import jdo.spc.SPCStationOutCabinetTool;
import jdo.spc.SPCToxicTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;


/**
 * 			
 * <p>Title: 麻精出库</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCOutStoreAction extends TAction {
	
	/**
	 * 补货任务-药房麻精出智能柜(麻精一支一支扫)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//保存D表
		result = SPCToxicTool.getInstance().onSaveToxicD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新containerD
		result = SPCContainerTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新DispenseD
		result = SPCOutStoreTool.getInstance().onUpdateDispenseD(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 补货任务-药房麻精出智能柜(扫容器一起出)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertBatch(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
				
 
		TParm toxicParm = parm.getParm("OUT_D");
		
		for(int i = 0 ; i < toxicParm.getCount("DISPENSE_NO") ; i++ ){
			
			TParm rowParm = (TParm)toxicParm.getRow(i);
			//保存D表
			result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			//更新containerD
			result = SPCContainerTool.getInstance().onUpdate(rowParm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//更新DispenseD
		result = SPCOutStoreTool.getInstance().onUpdateDispenseDNum(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-住院药房麻精出智能柜(一支一支扫出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertMl(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//保存D表
		result = SPCToxicTool.getInstance().onSaveToxicD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新containerD
		result = SPCContainerTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新ODI_DEPNM
		result = SPCOutStoreMLTool.getInstance().onUpdateOdiDspnm(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		result = SPCOutStoreMLTool.getInstance().onUpdateIndCabdspn(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		/**
		//更新库存--直接出库
		int batchSeq = parm.getInt("BATCH_SEQ");
		String orderCode = (String) parm.getValue("ORDER_CODE");
		int num = 1 ;
		String orgCode = parm.getValue("ORG_CODE");
		TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndStock(orgCode, orderCode, batchSeq + "")));
		double out_amt = StringTool.round(inParmSeq.getDouble(
				"STOCK_RETAIL_PRICE", 0)
				* num, 2);
		result = IndStockDTool.getInstance().onUpdateQtyRequestOut("EXM",
				orgCode, orderCode, batchSeq, num, out_amt,
				 parm.getValue("OPT_USER"),
				SystemTool.getInstance().getDate(),
				parm.getValue("OPT_TERM"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}*/
		conn.commit();
		conn.close();
		return result;
	}
	
	
	/**
	 * 临时医嘱/首日量-病区麻精出智能柜(一支一支扫出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertMlStation(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//保存D表
		result = SPCToxicTool.getInstance().onSaveToxicD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新containerD
		result = SPCContainerTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新ODI_DEPNM
		/**
		result = SPCOutStoreMLTool.getInstance().onUpdateOdiDspnm(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}*/
		
		result = SPCOutStoreMLTool.getInstance().onUpdateIndCabdspnNum(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		 
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-病区麻精出智能柜(整个容器出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertMlStationBatch(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		
		TParm dParm =  parm.getParm("OUT_D");
		for(int i = 0; i < dParm.getCount();i++){
			
			TParm rowParm = dParm.getRow(i);
			//保存D表
			result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			
			TParm cabdspnParm = new TParm();
			cabdspnParm.setData("START_DTTM", parm.getValue("START_DTTM"));
			cabdspnParm.setData("ORDER_NO", parm.getValue("ORDER_NO"));
			cabdspnParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			cabdspnParm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ"));
			cabdspnParm.setData("CONTAINER_ID",parm.getValue("CONTAINER_ID"));
			cabdspnParm.setData("TOXIC_ID",rowParm.getValue("TOXIC_ID"));
			cabdspnParm.setData("TAKEMED_USER",parm.getValue("TAKEMED_USER"));
			
			//
			result = SPCOutStoreMLTool.getInstance().onUpdateIndCabdspnNum(cabdspnParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			//更新containerD
			result = SPCContainerTool.getInstance().onUpdate(cabdspnParm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
		}
		
		result = SPCContainerTool.getInstance().onUpdateM(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
				 
		conn.commit();
		conn.close();
		return result;
	}
	
	
	
	
	/**
	 * 临时医嘱/首日量-住院药房麻精出智能柜(整个容器出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertMlBatch(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
			
		TParm toxicParm = parm.getParm("OUT_D");
		
		for(int i = 0 ; i < toxicParm.getCount("DISPENSE_NO") ; i++ ){
			
			TParm rowParm = (TParm)toxicParm.getRow(i);
			//保存D表
			result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			//更新containerD
			result = SPCContainerTool.getInstance().onUpdate(rowParm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//更新ODI_DEPNM
		result = SPCOutStoreMLTool.getInstance().onUpdateOdiDspnm(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		/**
		//更新库存--直接出库
		int batchSeq = parm.getInt("BATCH_SEQ");
		String orderCode = (String) parm.getValue("ORDER_CODE");
		int num = 1 ;
		String orgCode = parm.getValue("ORG_CODE");
		TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndStock(orgCode, orderCode, batchSeq + "")));
		double out_amt = StringTool.round(inParmSeq.getDouble(
				"STOCK_RETAIL_PRICE", 0)
				* num, 2);
		result = IndStockDTool.getInstance().onUpdateQtyRequestOut("EXM",
				orgCode, orderCode, batchSeq, num, out_amt,
				 parm.getValue("OPT_USER"),
				SystemTool.getInstance().getDate(),
				parm.getValue("OPT_TERM"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}*/
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-病区普药及麻精出智能柜 
	 * @param parm
	 * @return
	 */
	public TParm onInsertIndStock(TParm parm ){
		
		TParm result = new TParm();
		TParm orderParm = parm.getParm("OUT_ORDER");
		
		TParm checkParm = parm.getParm("CHECK_ORDER");
		int count = checkParm.getCount("ORDER_CODE");
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = checkParm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			double qty = rowParm.getDouble("ACTUAL_QTY");
			for(int j = i+1 ;j <count; j++  ){
				TParm rowParm2 = checkParm.getRow(j) ;
				String orderCode1 = rowParm2.getValue("ORDER_CODE");
				double qty1 = rowParm2.getDouble("ACTUAL_QTY");
				if(orderCode.equals(orderCode1)){
					 
					qty += qty1 ;
					checkParm.removeRow(j);
					j--;
					count--;
				}
			}
			checkParm.setData("ACTUAL_QTY",i,qty);
			
		}
		
		//检测库存是否够
		String orderCode = "";
		double actualQty = 0 ;
		TParm searchParm = new TParm();
		searchParm.setData("CABINET_ID",parm.getValue("CABINET_ID"));
		searchParm.setData("ORG_CODE",parm.getValue("ORG_CODE"));
		String msg = "";
		String orderDesc = "";
		for(int i = 0 ; i < checkParm.getCount("ORDER_CODE") ; i++){
			// 实际出库数量
			orderCode = checkParm.getValue("ORDER_CODE", i);
			actualQty = checkParm.getDouble("ACTUAL_QTY", i);
			orderDesc = checkParm.getValue("ORDER_DESC",i);
			searchParm.setData("ORDER_CODE",orderCode);
			TParm stockParm = IndStockDTool.getInstance().onQueryStockQTY(searchParm);
			if(stockParm.getCount() >  0 ){
				double qty = stockParm.getDouble("QTY",0);
				if(qty < actualQty ){
					
					msg += orderCode +" "+ orderDesc+" ;" ;
				}
			}else{
				msg += orderCode +" "+ orderDesc+" ; " ;

			}
		}
		
		if(msg != null && !msg.equals("")){
			result.setErrText(msg+"库存不足，不能出库!");
			result.setErrCode(-1);
			return result ;
		}
		TConnection conn = getConnection();
		//更新IND_STOCK
		 
		result = SPCStationOutCabinetTool.getInstance().onUpdateIndStock(orderParm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrText("扣库失败，不能出库!");
			result.setErrCode(-1);
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新IND_CABSTOCK
		/**
		result = SPCStationOutCabinetTool.getInstance().onUpdateIndCbnstock(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}*/
		
		//更新ODI_DSPNM表
		for(int i = 0 ; i < orderParm.getCount("ORDER_NO"); i++ ){
			TParm rowParm = (TParm)orderParm.getRow(i);
			result = SPCStationOutCabinetTool.getInstance().onUpdateOdiDspnm(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			//更新ODI_CABDSPN
			result = SPCStationOutCabinetTool.getInstance().onUpdateIndCabdspn(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-门急诊药房麻精出智能柜(一支一支扫出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertOutpatientOut(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
	
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				result.setErrCode(-1);
				result.setErrText("保存麻精交易M表失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		result = SPCToxicTool.getInstance().onSaveToxicD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrCode(-1);
			result.setErrText("保存麻精交易D表失败");
			conn.rollback();
			conn.close();
			return result;
		}
		
		result = SPCContainerTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrCode(-1);
			result.setErrText("更新容器表失败");
			conn.rollback();
			conn.close();
			return result;
		}
		
		result = IndRequestDTool.getInstance().onUpdateActualQty(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrCode(-1);
			result.setErrText("更新请领表失败");
			conn.rollback();
			conn.close();
			return result;
		}
		 
		TParm parmM = parm.getParm("OUT_M");
		String dispenseNo = parmM.getValue("DISPENSE_NO");
		result = new TParm(TJDODBTool.getInstance().select(INDSQL.getDispenseByDisNo(dispenseNo)));
		if(result.getCount() <=0 ) {
	        result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
	        if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				result.setErrCode(-1);
				result.setErrText("新增出库M表失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}
		 
        TParm parmD = parm.getParm("OUT_D");
        
        result = IndDispenseDTool.getInstance().onQueryDispenseDDrug(parmD);
        if(result.getCount() > 0 ){
        	result = IndDispenseDTool.getInstance().onUpdateDDrug(parmD, conn);
        	if (result.getErrCode() < 0) {
 				err("ERR:" + result.getErrCode() + result.getErrText()
 						+ result.getErrName());
 				result.setErrCode(-1);
				result.setErrText("更新出库D表失败");
 				conn.rollback();
 				conn.close();
 				return result;
 			}
        }else{
	        result = IndDispenseDTool.getInstance().onInsertD(parmD, conn);
	        if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				result.setErrCode(-1);
				result.setErrText("新增出库D表失败");
				conn.rollback();
				conn.close();
				return result;
			}
        }
        
        String orgCode= parmM.getValue("TO_ORG_CODE");
        String requestType = parmM.getValue("REQTYPE_CODE");
        
        TParm parmStock = new TParm();
        parmStock.addData("ORDER_CODE", parmD.getValue("ORDER_CODE"));
        parmStock.addData("ACTUAL_QTY", parmD.getDouble("ACTUAL_QTY"));
        parmStock.addData("OPT_USER", parmD.getValue("OPT_USER"));
        parmStock.addData("OPT_TERM", parmD.getValue("OPT_TERM"));
        parmStock.addData("OPT_DATE", parmD.getTimestamp("OPT_DATE"));
        //扣库
        result = INDTool.getInstance().onIndDispenseOutDrug(parmStock,orgCode,requestType,conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrCode(-1);
			result.setErrText("扣库失败");
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	
	
	/**
	 * 临时医嘱/首日量-门急诊药房麻精出智能柜(批量扫出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertOutpatientOutBatch(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
	
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		TParm toxicParmD = parm.getParm("OUT_TOXICID"); 
		
		for(int i = 0 ; i < toxicParmD.getCount("DISPENSE_NO") ; i++ ){
				
				TParm rowParm = (TParm)toxicParmD.getRow(i);
				//保存D表
				result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				
				//更新containerD
				result = SPCContainerTool.getInstance().onUpdate(rowParm,conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
		}
				
		result = IndRequestDTool.getInstance().onUpdateActualQty(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		 
		TParm parmM = parm.getParm("OUT_M");
		String dispenseNo = parmM.getValue("DISPENSE_NO");
		result = new TParm(TJDODBTool.getInstance().select(INDSQL.getDispenseByDisNo(dispenseNo)));
		if(result.getCount() <=0 ) {
	        result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
	        if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		 
        TParm parmD = parm.getParm("OUT_D");
        
        result = IndDispenseDTool.getInstance().onQueryDispenseDDrug(parmD);
        if(result.getCount() > 0 ){
        	result = IndDispenseDTool.getInstance().onUpdateDDrug(parmD, conn);
        	if (result.getErrCode() < 0) {
 				err("ERR:" + result.getErrCode() + result.getErrText()
 						+ result.getErrName());
 				conn.rollback();
 				conn.close();
 				return result;
 			}
        }else{
	        result = IndDispenseDTool.getInstance().onInsertD(parmD, conn);
	        if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
        }
        
        //扣库
        String orgCode= parmM.getValue("TO_ORG_CODE");
        String requestType = parmM.getValue("REQTYPE_CODE");
        
        TParm parmStock = new TParm();
        parmStock.addData("ORDER_CODE", parmD.getValue("ORDER_CODE"));
        parmStock.addData("ACTUAL_QTY", parmD.getDouble("ACTUAL_QTY"));
        parmStock.addData("OPT_USER", parmD.getValue("OPT_USER"));
        parmStock.addData("OPT_TERM", parmD.getValue("OPT_TERM"));
        parmStock.addData("OPT_DATE", parmD.getTimestamp("OPT_DATE"));
        //扣库
        result = INDTool.getInstance().onIndDispenseOutDrug(parmStock,orgCode,requestType,conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setErrCode(-1);
			result.setErrText("扣库失败");
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-急诊麻精出智能柜(一支一支扫出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertEr(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		//保存D表
		result = SPCToxicTool.getInstance().onSaveToxicD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新containerD
		result = SPCContainerTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		//更新OPD_ORDER
		result = SPCErOutCabinetTool.getInstance().onUpdateOpdorderNum(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		 
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 临时医嘱/首日量-急诊麻精出智能柜(整个容器出库)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertErBatch(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		//保存主表需要判断主表是否已经保存了,没有找到则新增
		result = SPCToxicTool.getInstance().onQueryToxicM(parm);
		if(result.getCount() <= 0){
			result = SPCToxicTool.getInstance().onSaveToxicM(parm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		
		TParm dParm =  parm.getParm("OUT_D");
		for(int i = 0; i < dParm.getCount();i++){
			
			TParm rowParm = dParm.getRow(i);
			//保存D表
			result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			
			TParm cabdspnParm = new TParm();
			cabdspnParm.setData("RX_NO", parm.getValue("RX_NO"));
			cabdspnParm.setData("SEQ_NO", parm.getValue("SEQ_NO"));
			cabdspnParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			cabdspnParm.setData("CONTAINER_ID",parm.getValue("CONTAINER_ID"));
			cabdspnParm.setData("TOXIC_ID",rowParm.getValue("TOXIC_ID"));
			
			//
			result = SPCErOutCabinetTool.getInstance().onUpdateOpdorderNum(cabdspnParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
		 
			//更新containerD
			result = SPCContainerTool.getInstance().onUpdate(cabdspnParm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
		}
		
		 
				 
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 手术间出库
	 * @param parm
	 * @return
	 */
	public TParm onSaveOperationRoom(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
	
		TParm parmM = parm.getParm("OUT_M");
		
		for(int i = 0 ; i < parmM.getCount("DISPENSE_NO") ; i++){
			TParm rowParm = parmM.getRow(i) ;
			result = SPCToxicTool.getInstance().onQueryToxicM(rowParm);
			if(result.getCount() <= 0){
				result = SPCToxicTool.getInstance().onSaveToxicM(rowParm,conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		
		TParm toxicParmD = parm.getParm("OUT_D"); 
		
		for(int i = 0 ; i < toxicParmD.getCount("DISPENSE_NO") ; i++ ){
			
			TParm rowParm = (TParm)toxicParmD.getRow(i);
			//保存D表
			result = SPCToxicTool.getInstance().onSaveToxicD(rowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			
			//更新containerD
			result = SPCContainerTool.getInstance().onUpdate(rowParm,conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result ;
	}
	

}
