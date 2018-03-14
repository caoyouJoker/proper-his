package action.spc;

import jdo.inw.InwForOdiTool;
import jdo.spc.INDTool;
import jdo.spc.SPCIndCabdspnTool;
import jdo.udd.UddChnCheckTool;  
import jdo.udd.UddRtnRgsTool;
import jdo.util.Medicine;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class SpcOdiAction extends TAction {

	// public TParm onSaveOdiDspnmd(TParm parm) {
	// TConnection conn = getConnection();
	// TParm mParm = parm.getParm("DSPN_M") ;
	// TParm dParm = parm.getParm("DSPN_D") ;
	//		 
	// TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
	// TParm result = new TParm();
	// String message = "";
	// for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {
	//            
	// TParm parmToIND = (TParm)trimedforIndpParmCheck.getRow(i);
	//            
	//           
	// Medicine subInd = new Medicine();
	// //System.out.println("parmToIND----:"+parmToIND);
	// if (!subInd.checkIndStockQty(parmToIND)) {
	// String orderDesc = trimedforIndpParmCheck.getValue("ORDER_DESC", i) ;
	// if(orderDesc == null || orderDesc.equals("")){
	// orderDesc = trimedforIndpParmCheck.getValue("HIS_ORDER_CODE",
	// i)+"HIS编码没有对应上";
	// }
	// message += (orderDesc+"扣库部门:"+ trimedforIndpParmCheck.getData("ORG_CODE",
	// i) + "\n");
	// }
	// }
	//		
	// if(message.length() != 0){
	// conn.rollback();
	// conn.close();
	// result.setErr( -1, message + "药品库存不足\n不可扣库！");
	// result.setErrCode(-1);
	// return result;
	// }
	// 	
	//		
	// TParm returnParm = new TParm() ;
	// for(int i = 0 ; i < mParm.getCount("CASE_NO") ; i++ ){
	// TParm mRowParm = (TParm)mParm.getRow(i);
	//			
	// String orderCode = mRowParm.getValue("ORDER_CODE") ;
	//			
	// TParm trimedforIndParm = new TParm();
	// trimedforIndParm.setData("ORG_CODE",mRowParm.getValue("EXEC_DEPT_CODE"));
	//			 
	// trimedforIndParm.setData("ORDER_CODE",orderCode);
	// trimedforIndParm.setData("DOSAGE_QTY",mRowParm.getDouble("DOSAGE_QTY"));
	// trimedforIndParm.setData("OPT_DATE", mRowParm.getTimestamp("OPT_DATE"));
	// trimedforIndParm.setData("OPT_USER",mRowParm.getValue("OPT_USER"));
	// trimedforIndParm.setData("OPT_TERM",mRowParm.getValue("OPT_TERM"));
	//            
	//            
	// trimedforIndParm.setData("SERVICE_LEVEL",mRowParm.getValue("SERVICE_LEVEL"));
	//			
	// String takemedOrg = mRowParm.getValue("TAKEMED_ORG");
	//			
	//			
	// boolean isBuckleStock = true; ;
	//			
	// if(takemedOrg.equals("1")){
	// TParm searchParm = new TParm();
	// searchParm.setData("ORDER_CODE",orderCode);
	//				
	// result = SPCIndCabdspnTool.getInstance().onQueryIsDrug(searchParm);
	//				
	// if(result.getCount() > 0 ){
	// isBuckleStock = false;
	// }else {
	// String isDrugOutUdd = getIsDrugOutUdd() ;
	// //System.out.println("isDrugOutUdd----:"+isDrugOutUdd);
	// if(isDrugOutUdd.equals("N")){
	// isBuckleStock = false;
	// }
	// }
	// }
	//			
	// if(isBuckleStock) {
	//				
	// returnParm =
	// INDTool.getInstance().reduceIndStockSpcBatch(trimedforIndParm,mRowParm.getValue("SERVICE_LEVEL"),conn);
	// if (returnParm.getErrCode() < 0) {
	// err("ERR:" + returnParm.getErrCode() + returnParm.getErrText()
	// + returnParm.getErrName());
	// String orderDesc = mRowParm.getValue("ORDER_DESC") ;
	//			          	 
	// result.setErr(-1,"扣库失败:"+orderDesc+" 扣库部门：\n"+mRowParm.getValue("EXEC_DEPT_CODE"));
	// result.setErrCode(-1);
	// conn.rollback();
	// conn.close();
	// return result;
	// }
	// }
	//			
	//	       	
	// result = InwForOdiTool.getInstance().insertOdiDspnm(mRowParm,conn);
	// if (result.getErrCode() < 0) {
	// err("ERR:" + result.getErrCode() + result.getErrText()
	// + result.getErrName());
	//	            
	// String msg =
	// "重复调配：CASE_NO="+mRowParm.getValue("CASE_NO")+":ORDER_NO="+mRowParm.getValue("ORDER_NO")+
	// "\n"+
	// ":ORDER_SEQ="+mRowParm.getValue("ORDER_SEQ")+":START_DTTM="+mRowParm.getValue("START_DTTM")+
	// "\n";
	// result.setErr(-1,msg);
	// conn.rollback();
	// conn.close();
	// return result;
	// }
	//	    
	// if(isBuckleStock){
	// TParm updateDispenseParm = new TParm();
	// updateDispenseParm.setData("CASE_NO",mRowParm.getValue("CASE_NO"));
	// updateDispenseParm.setData("ORDER_NO",mRowParm.getValue("ORDER_NO"));
	// updateDispenseParm.setData("ORDER_SEQ",mRowParm.getValue("ORDER_SEQ"));
	// updateDispenseParm.setData("START_DTTM",mRowParm.getValue("START_DTTM"));
	//		        
	//		       	
	//		       	
	// int count = returnParm.getCount("ORDER_CODE");
	// int var = 1;
	// for(int k = 0 ; k < count ;k++){
	// updateDispenseParm.setData("BATCH_SEQ"+var,returnParm.getValue("BATCH_SEQ",k));
	// updateDispenseParm.setData("VERIFYIN_PRICE"+var,returnParm.getValue("PRICE",k));
	// updateDispenseParm.setData("DISPENSE_QTY"+var,returnParm.getValue("QTY",k));
	// var++ ;
	// }
	// if(var <= 3 ){
	// while(var <=3 ){
	// updateDispenseParm.setData("BATCH_SEQ"+var,-1);
	// updateDispenseParm.setData("VERIFYIN_PRICE"+var,0);
	// updateDispenseParm.setData("DISPENSE_QTY"+var,0);
	// var++ ;
	// }
	// }
	//		       	
	// result
	// =UddChnCheckTool.getInstance().updateUDDDispenseDetail(updateDispenseParm,conn);
	//				
	// if (result.getErrCode() < 0) {
	// err("ERR:" + result.getErrCode() + result.getErrText()
	// + result.getErrName());
	// result.setErr(-1,"回写M表扣库信息失败："+mRowParm.getValue("CASE_NO")+":药品编码："+mRowParm.getValue("ORDER_CODE")
	// + "\n");
	// conn.rollback();
	// conn.close();
	// return result;
	// }
	// }
	//			
	// }
	//	
	//		
	// for(int j = 0 ; j < dParm.getCount("CASE_NO") ; j++ ){
	// TParm dRowParm = (TParm)dParm.getRow(j);
	//			
	// //System.out.println("----------dRowParm:"+dRowParm);
	// // 插入ODI_DSPND
	// result = InwForOdiTool.getInstance().insertOdiDspnd(dRowParm,conn);
	// //System.out.println("result----:"+result);
	// if (result.getErrCode() < 0) {
	// err("ERR:" + result.getErrCode() + result.getErrText()
	// + result.getErrName());
	// result.setErr(-1,"新增D表失败"+dRowParm.getValue("CASE_NO")
	// +":"+dRowParm.getValue("ORDER_CODE")+ "\n");
	// conn.rollback();
	// conn.close();
	// return result;
	// }
	// }
	//
	// conn.commit();
	// conn.close();
	//	    
	// return result;
	// }

	public TParm onSaveOdiDspnmd(TParm parm) {
		TConnection conn = getConnection();
		TParm mParm = parm.getParm("DSPN_M");
		TParm dParm = parm.getParm("DSPN_D");

		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result = new TParm();
		String message = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {

			TParm parmToIND = (TParm) trimedforIndpParmCheck.getRow(i);

			Medicine subInd = new Medicine();
			// System.out.println("parmToIND----:"+parmToIND);
			if (!subInd.checkIndStockQty(parmToIND)) {
				String orderDesc = trimedforIndpParmCheck.getValue(
						"ORDER_DESC", i);
				if (orderDesc == null || orderDesc.equals("")) {
					orderDesc = trimedforIndpParmCheck.getValue(
							"HIS_ORDER_CODE", i)
							+ "HIS编码没有对应上";
				}
				message += (orderDesc + "扣库部门:"
						+ trimedforIndpParmCheck.getData("ORG_CODE", i) + "\n");
			}
		}

		if (message.length() != 0) {
			conn.rollback();
			conn.close();
			result.setErr(-1, message + "药品库存不足\n不可扣库！");
			result.setErrCode(-1);
			return result;
		}

		TParm returnParm = new TParm();
		for (int i = 0; i < mParm.getCount("CASE_NO"); i++) {
			TParm mRowParm = (TParm) mParm.getRow(i);
			String orderCode = mRowParm.getValue("ORDER_CODE");

			TParm trimedforIndParm = new TParm();  
			trimedforIndParm.setData("ORG_CODE", mRowParm
					.getValue("EXEC_DEPT_CODE"));

			trimedforIndParm.setData("ORDER_CODE", orderCode);
			trimedforIndParm.setData("DOSAGE_QTY", mRowParm
					.getDouble("DOSAGE_QTY"));
			trimedforIndParm.setData("OPT_DATE", mRowParm
					.getTimestamp("OPT_DATE"));
			trimedforIndParm.setData("OPT_USER", mRowParm.getValue("OPT_USER"));
			trimedforIndParm.setData("OPT_TERM", mRowParm.getValue("OPT_TERM"));

			trimedforIndParm.setData("SERVICE_LEVEL", mRowParm
					.getValue("SERVICE_LEVEL"));  
			// 加入批号 同 服务等级模式
			if (mRowParm.getValue("BATCH_NO") != null||!"".equals(mRowParm.getValue("BATCH_NO"))) { 
			trimedforIndParm.setData("BATCH_NO", mRowParm.getValue("BATCH_NO"));  
			}
			String takemedOrg = mRowParm.getValue("TAKEMED_ORG");

			boolean isBuckleStock = true;
			;

			if (takemedOrg.equals("1")) {
				TParm searchParm = new TParm();
				searchParm.setData("ORDER_CODE", orderCode);

				result = SPCIndCabdspnTool.getInstance().onQueryIsDrug(
						searchParm);

				if (result.getCount() > 0) {  
					isBuckleStock = false;
				} else {
					String isDrugOutUdd = getIsDrugOutUdd();
					// ////System.out.println("isDrugOutUdd----:"+isDrugOutUdd);
					if (isDrugOutUdd.equals("N")) {
						isBuckleStock = false;
					}
				}
			}    
            
			if (isBuckleStock) {
				// fux modify 扣库修改 就批号判断取值 批号为空的走原有逻辑   不为空则走带批号的逻辑
				//if (!"null".equals(mRowParm.getValue("BATCH_NO").trim())) {   
				if(!"".equals(mRowParm.getValue("BATCH_NO").trim())){     
					returnParm = INDTool.getInstance().reduceIndStockSpcBatch(        
							trimedforIndParm,  
							mRowParm.getValue("SERVICE_LEVEL"), conn);      
				} else {  
					returnParm = INDTool.getInstance().reduceIndStockSpc(
							trimedforIndParm,
							mRowParm.getValue("SERVICE_LEVEL"), conn);    
				}
				if (returnParm.getErrCode() < 0) {
					err("ERR:" + returnParm.getErrCode()
							+ returnParm.getErrText() + returnParm.getErrName());
					String orderDesc = mRowParm.getValue("ORDER_DESC");

					result.setErr(-1, "扣库失败:" + orderDesc + " 扣库部门：\n"
							+ mRowParm.getValue("EXEC_DEPT_CODE"));
					result.setErrCode(-1);
					conn.rollback();
					conn.close();
					return result;
				}
			}

			result = InwForOdiTool.getInstance().insertOdiDspnm(mRowParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());

				String msg = "重复调配：CASE_NO=" + mRowParm.getValue("CASE_NO")
						+ ":ORDER_NO=" + mRowParm.getValue("ORDER_NO") + "\n"
						+ ":ORDER_SEQ=" + mRowParm.getValue("ORDER_SEQ")
						+ ":START_DTTM=" + mRowParm.getValue("START_DTTM")
						+ "\n";
				result.setErr(-1, msg);
				conn.rollback();
				conn.close();
				return result;
			}

			if (isBuckleStock) {
				TParm updateDispenseParm = new TParm();
				updateDispenseParm.setData("CASE_NO", mRowParm
						.getValue("CASE_NO"));
				updateDispenseParm.setData("ORDER_NO", mRowParm
						.getValue("ORDER_NO"));
				updateDispenseParm.setData("ORDER_SEQ", mRowParm
						.getValue("ORDER_SEQ"));
				updateDispenseParm.setData("START_DTTM", mRowParm
						.getValue("START_DTTM"));

				int count = returnParm.getCount("ORDER_CODE");
				int var = 1;
				for (int k = 0; k < count; k++) {
					updateDispenseParm.setData("BATCH_SEQ" + var, returnParm
							.getValue("BATCH_SEQ", k));
					updateDispenseParm.setData("VERIFYIN_PRICE" + var,
							returnParm.getValue("PRICE", k));
					updateDispenseParm.setData("DISPENSE_QTY" + var, returnParm
							.getValue("QTY", k));
					var++;
				}
				if (var <= 3) {
					while (var <= 3) {
						updateDispenseParm.setData("BATCH_SEQ" + var, -1);
						updateDispenseParm.setData("VERIFYIN_PRICE" + var, 0);
						updateDispenseParm.setData("DISPENSE_QTY" + var, 0);
						var++;
					}
				}

				result = UddChnCheckTool.getInstance().updateUDDDispenseDetail(
						updateDispenseParm, conn);

				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					result.setErr(-1, "回写M表扣库信息失败："
							+ mRowParm.getValue("CASE_NO") + ":药品编码："
							+ mRowParm.getValue("ORDER_CODE") + "\n");
					conn.rollback();
					conn.close();
					return result;
				}
			}

		}

		for (int j = 0; j < dParm.getCount("CASE_NO"); j++) {
			TParm dRowParm = (TParm) dParm.getRow(j);

			// ////System.out.println("----------dRowParm:"+dRowParm);
			// 插入ODI_DSPND
			result = InwForOdiTool.getInstance().insertOdiDspnd(dRowParm, conn);
			// ////System.out.println("result----:"+result);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				result.setErr(-1, "新增D表失败" + dRowParm.getValue("CASE_NO") + ":"
						+ dRowParm.getValue("ORDER_CODE") + "\n");
				conn.rollback();
				conn.close();
				return result;
			}
		}

		conn.commit();
		conn.close();

		return result;
	}

	public TParm onUpdateRtnCfm(TParm parm) {
		TParm result = new TParm();
		TConnection conn = getConnection();

		TParm mParm = parm.getParm("DSPN_M");
		TParm dParm = parm.getParm("DSPN_D");

		// 更新主项，并把主项要退的数据，新增到库房
		for (int k = 0; k < mParm.getCount("CASE_NO"); k++) {
			TParm mParmRow = (TParm) mParm.getRow(k);

			// ////System.out.println("mParmRow------------:"+mParmRow);
			String sql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO = '"
					+ mParmRow.getValue("CASE_NO") + "' AND ORDER_NO = '"
					+ mParmRow.getValue("ORDER_NO") + "' AND ORDER_SEQ = "
					+ mParmRow.getInt("ORDER_SEQ") + " AND START_DTTM = '"
					+ mParmRow.getValue("START_DTTM") + "'";
			TParm odi_dspnm = new TParm(TJDODBTool.getInstance().select(sql));
			if (odi_dspnm.getCount("PHA_RETN_CODE") > 0
					&& odi_dspnm.getData("PHA_RETN_CODE", 0) != null
					&& !"".equals(odi_dspnm.getValue("PHA_RETN_CODE", 0))) {
				////System.out.println("-----------------:写DSPNM表出错");
				// LogUtil.writerLogErr("-----------------:写DSPNM表出错");
				result.setErrCode(-1);
				result.setErrText("药品已经退药，请重新查询");

				return result;
			}

			double thisDosage = mParmRow.getDouble("RTN_DOSAGE_QTY")
					- mParmRow.getDouble("CANCEL_DOSAGE_QTY");
			// 增库
			TParm parmInd = new TParm();
			parmInd.setData("ORG_CODE", mParmRow.getValue("EXEC_DEPT_CODE"));
			parmInd.setData("ORDER_CODE", mParmRow.getValue("ORDER_CODE"));
			parmInd.setData("DOSAGE_QTY", thisDosage);
			parmInd.setData("OPT_USER", mParmRow.getValue("OPT_USER"));
			parmInd.setData("OPT_DATE", mParmRow.getTimestamp("OPT_DATE"));
			parmInd.setData("OPT_TERM", mParmRow.getValue("OPT_TERM"));

			// ////System.out.println("parmInd-----:"+parmInd);
			result = INDTool.getInstance().regressIndStock(parmInd,
					mParmRow.getValue("SERVICE_LEVEL"), conn);
			if (result.getErrCode() < 0) {
				////System.out.println("药品退药增库失败------:ORG_CODE");
				// LogUtil.writerLogErr("药品退药增库失败------:ORG_CODE="+mParmRow.getValue("EXEC_DEPT_CODE")+":ORDER_CODE="+mParmRow.getValue("ORDER_CODE")+":DOSAGE_QTY="+thisDosage);
				result.setErr(-1, "药品退药增库失败！");
				result.setErrCode(-1);
				conn.rollback();
				conn.close();
				return result;
			}

			String batchSeq = result.getValue("BATCH_SEQ");
			String verifyinPrice = result.getValue("VERIFYIN_PRICE");
			String dosageQty = result.getValue("DOSAGE_QTY");
			mParmRow.setData("BATCH_SEQ1", batchSeq);
			mParmRow.setData("VERIFYIN_PRICE1", verifyinPrice);
			mParmRow.setData("DISPENSE_QTY1", dosageQty);
			mParmRow.setData("BATCH_SEQ2", -1);
			mParmRow.setData("VERIFYIN_PRICE2", 0);
			mParmRow.setData("DISPENSE_QTY2", 0);
			mParmRow.setData("BATCH_SEQ3", -1);
			mParmRow.setData("VERIFYIN_PRICE3", 0);
			mParmRow.setData("DISPENSE_QTY3", 0);
			result = UddRtnRgsTool.getInstance().onInsertMRtn(mParmRow, conn);
			if (result.getErrCode() < 0) {
				////System.out.println("-----------------:写DSPNM表出错");
				// LogUtil.writerLogErr("-----------------:写DSPNM表出错");
				result.setErrCode(-1);
				result.setErrText("退药新增M表失败");
				conn.rollback();
				conn.close();
				return result;
			}

		}

		int c = dParm.getCount("CASE_NO");
		for (int b = 0; b < c; b++) {

			TParm parmRow = dParm.getRow(b);
			// ////System.out.println("dParmRow------------:"+parmRow);
			result = UddRtnRgsTool.getInstance().onInsertD(parmRow, conn);

			/**
			 * result = IBSOrderdTool.getInstance().checkIBSQutryForUDD(parmRow.
			 * getValue("CASE_NO"), parmRow.getValue("ORDER_CODE"),
			 * parmRow.getValue("EXEC_DEPT_CODE"));
			 */

			if (result.getErrCode() < 0) {
				// luhai add 2012-04-15 add rollback
				result.setErrCode(-1);
				////System.out.println("药品退药 插入ODI_DSPNMD表失败");
				result.setErrText("药品退药 插入ODI_DSPNMD表失败");
				// LogUtil.writerLogErr("药品退药 插入ODI_DSPNMD表失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}

		conn.commit();
		conn.close();
		return result;
	}

	public TParm onSaveIndCabdspn(TParm parm) {
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();

		for (int i = 0; i < count; i++) {
			TParm rowParm = parm.getRow(i);

			result = SPCIndCabdspnTool.getInstance().insert(rowParm, conn);

			if (result.getErrCode() < 0) {
				result.setErrCode(-1);
				////System.out.println("审核失败");
				result.setErrText("审核失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

	public TParm onDeleteIndCabdspn(TParm parm) {
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();

		for (int i = 0; i < count; i++) {
			TParm rowParm = parm.getRow(i);

			result = SPCIndCabdspnTool.getInstance().delete(rowParm, conn);

			if (result.getErrCode() < 0) {

				result.setErrCode(-1);
				////System.out.println("取消审核失败");
				result.setErrText("取消审核失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

	public TParm onCheckStockQty(TParm parm) {
		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result = new TParm();
		String message = "";
		String orgCode = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {

			TParm parmToIND = (TParm) trimedforIndpParmCheck.getRow(i);

			// IND接口类
			Medicine subInd = new Medicine();
			// ////System.out.println("parmToIND----:"+parmToIND);
			if (!subInd.checkIndStockQty(parmToIND)) {
				String orderDesc = trimedforIndpParmCheck.getValue(
						"ORDER_DESC", i);
				String spec = trimedforIndpParmCheck.getValue("SPECIFICATION",
						i);
				if (orderDesc == null || orderDesc.equals("")) {
					orderDesc = trimedforIndpParmCheck.getValue(
							"HIS_ORDER_CODE", i)
							+ "-HIS编码没有对应上";
				}
				orgCode = parmToIND.getValue("ORG_CODE");
				String orgDesc = getOrgDesc(orgCode);
				message += (orderDesc + "(" + spec + ")" + "-扣库部门：" + orgDesc + "\n");
			}
		}

		if (message.length() != 0) {

			result.setErr(-1, message + "药品库存不足，不可扣库，请补充库存！");
			result.setErrCode(-1);
			return result;
		}
		return result;
	}

	// fux modify 20150507
	// 按批号检核库存
	public TParm onCheckStockQtyBatch(TParm parm) {
		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result = new TParm();
		String message = "";
		String orgCode = "";
		String batchNo = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {

			TParm parmToIND = (TParm) trimedforIndpParmCheck.getRow(i);

			// IND接口类
			Medicine subInd = new Medicine();
			////System.out.println("parmToIND----:" + parmToIND);
			if (!subInd.checkIndStockQtyBatch(parmToIND)) {
				String orderDesc = trimedforIndpParmCheck.getValue(
						"ORDER_DESC", i);
				String spec = trimedforIndpParmCheck.getValue("SPECIFICATION",
						i);
				if (orderDesc == null || orderDesc.equals("")) {
					orderDesc = trimedforIndpParmCheck.getValue(
							"HIS_ORDER_CODE", i)
							+ "-HIS编码没有对应上";
				}
				orgCode = parmToIND.getValue("ORG_CODE");
				// fux modify 20150508
				batchNo = parmToIND.getValue("BATCH_NO");
				String orgDesc = getOrgDesc(orgCode);
				message += (orderDesc + "(" + spec + ")" + "-批号：" + batchNo
						+ "-扣库部门：" + orgDesc + "\n");
			}
		}

		if (message.length() != 0) {

			result.setErr(-1, message + "药品库存不足，不可扣库，请补充库存！");
			result.setErrCode(-1);
			return result;
		}
		return result;
	}

	// 查询批号
	public TParm onQueryBatch(TParm parm) {  
		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result = new TParm();
		String message = "";
		String orgCode = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {

			TParm parmToIND = (TParm) trimedforIndpParmCheck.getRow(i);

			// IND接口类
			//Medicine subInd = new Medicine();
			// ////System.out.println("parmToIND----:"+parmToIND);
			// 根据org_code order_code 传回 所有相关baotch_no
			
			String sql = " SELECT A.BATCH_NO FROM IND_STOCK A,PHA_BASE B " +
					" WHERE A.ORDER_CODE=B.ORDER_CODE  AND A.ORG_CODE='" + parmToIND.getValue("ORG_CODE") + "'" +
					" AND A.ORDER_CODE = '"+parmToIND.getValue("ORDER_CODE") +"' AND SYSDATE < A.VALID_DATE" +
					" AND B.ANTIBIOTIC_CODE IS NOT NULL "+          
                    " AND B.SKINTEST_FLG='Y'  " + 
					" AND A.STOCK_QTY>0 ORDER BY A.VALID_DATE ASC";  
			////System.out.println("sql:fux::"+sql);  
			TParm resultSql = new TParm(TJDODBTool.getInstance().select(sql));
			//result = subInd.onQueryBatchNo(parmToIND);
			result = resultSql;
			////System.out.println("result::onQueryBatchNo::"+result);    
			String orderDesc = trimedforIndpParmCheck.getValue("ORDER_DESC", i);
			String spec = trimedforIndpParmCheck.getValue("SPECIFICATION", i);
			// if(orderDesc == null || orderDesc.equals("")){  
			// orderDesc = trimedforIndpParmCheck.getValue("HIS_ORDER_CODE",
			// i)+"-HIS编码没有对应上";
			// }
			orgCode = parmToIND.getValue("ORG_CODE");    
			String orgDesc = getOrgDesc(orgCode);
			message += (orderDesc + "(" + spec + ")" + "-扣库部门：" + orgDesc + "\n");

		}
		if (result.getCount() <= 0) {
			result.setErr(-1, message + "无相关药品，请补充库存！");
			result.setErrCode(-1);
			return result;  
		}
		return result;
	}

	public String getIsDrugOutUdd() {
		String isDrugOutUdd = "Y";
		String sql = " SELECT A.IS_DRUG_OUT_UDD,A.TOXIC_LENGTH FROM IND_SYSPARM A  ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result != null && result.getCount() > 0) {
			TParm rowParm = result.getRow(0);
			isDrugOutUdd = rowParm.getValue("IS_DRUG_OUT_UDD");
		}
		return isDrugOutUdd;
	}

	private String getOrgDesc(String orgCode) {
		String orgDesc = "";
		String sql = " SELECT A.ORG_CHN_DESC FROM IND_ORG A WHERE A.ORG_CODE='"
				+ orgCode + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result != null && result.getCount() > 0) {
			TParm rowParm = result.getRow(0);
			orgDesc = rowParm.getValue("ORG_CHN_DESC");
		}
		if (orgDesc == null || orgDesc.equals("")) {
			orgDesc = orgCode;
		}
		return orgDesc;

	}

}
