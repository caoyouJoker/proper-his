package com.javahis.ui.odi;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;












import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.StringTool;
import com.javahis.ui.opd.CDSSStationDrools;
import com.javahis.util.DateUtil;

import jdo.cdss.AdvicePojo;
import jdo.cdss.AllergyPojo;
import jdo.cdss.CDSSConfig;
import jdo.cdss.ErdPojo;
import jdo.cdss.ExaPojo;
import jdo.cdss.HisPojo;
import jdo.cdss.OrderPojo;
import jdo.odi.OdiDrugAllergy;
import jdo.odi.OdiObject;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

public class ODIStationDrools extends CDSSStationDrools{

	public ODIStationControl odiStationControl;

	private static final String URLDROOLS = "%ROOT%\\config\\drools\\ODOMainDeleteRx.x";
	private static final String URLCAL = "%ROOT%\\config\\drools\\ODICdssCal.x";


	public ODIStationDrools(ODIStationControl odiStationControl) {
		this.odiStationControl = odiStationControl;
	}

	/**
	 * 执行规则
	 * 
	 * @return
	 */
	public boolean fireRules(OdiObject odiObject, int i) {
		

		HisPojo hisPojo = gethispHisPojo(odiObject);
		
		TParm returnParm = new TParm();
		
		hisPojo.setAdmType("I");
		
		TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);
		
		switch (i) {
		case 1:
//			hisPojo = CDSSClient.getInstance().fireRules1(hisPojo);
			returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
					"fireRule1", inParm);
			break;
		case 2:
//			hisPojo = CDSSClient.getInstance().fireRules2(hisPojo);
			returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
					"fireRule2", inParm);
			break;
		}
		
		hisPojo = sysUtil.parseTParmToHisPojo(returnParm);

		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();

		if (advicePojos.size() == 0) {
			return false;
		}


		TParm parm = new TParm();

		List<String> ids = new ArrayList<String>();
		for (AdvicePojo advicePojo : advicePojos) {
			if(!ids.contains(advicePojo.getKnowladgeId())){
				parm.addData("ID", advicePojo.getKnowladgeId());
				parm.addData("LEVEL", advicePojo.getLevel());
				parm.addData("ADVICE", advicePojo.getAdviceText());
				parm.addData("REMARK", "");
				parm.addData("ORDER_CODE", advicePojo.getOrderCode());
				parm.addData("ORDER_NO", advicePojo.getRxNo());
				parm.addData("ORDER_SEQ", advicePojo.getSeqNo());
//				System.out.println("返回值==="+advicePojo.getRxNo()+"===="+advicePojo.getSeqNo());
				ids.add(advicePojo.getKnowladgeId());
			}
		}
		
		TParm droolsLogBean = new TParm();
		droolsLogBean.setData("CASE_NO", odiStationControl.getCaseNo());
		droolsLogBean.setData("MR_NO", odiStationControl.getMrNo());
		droolsLogBean.setData("ADM_TYPE", "I");
		droolsLogBean.setData("LOG_PARM", parm.getData());
		
		Object obj = odiStationControl.openDialog(URLDROOLS, droolsLogBean);

		String objStr = (String) obj;

		if ("Y".equals(objStr)) {
			return false;
		}

		return true;

	}

	private HisPojo gethispHisPojo(OdiObject odiObject) {
		List<String> orderCodesChestpain = CDSSConfig.getInstance().getOrderCodesChestpain();
		List<String> orderCodes = CDSSConfig.getInstance().getOrderCodes();
		List<String> testItemCodes = CDSSConfig.getInstance().getTestItemCodes();
		List<String> monitorItemEns =  CDSSConfig.getInstance().getMonitorItemEns();

		Pat pat = Pat.onQueryByMrNo(odiStationControl.getMrNo());

		Timestamp sysTs = SystemTool.getInstance().getDate();

		String sysDateStr = sysUtil.getDateStr(sysTs);

		String sysTimeStr = sysUtil.getTimeStr(sysTs);

		HisPojo hisPojo = new HisPojo();

		hisPojo.setSex(pat.getSexString());

		hisPojo.setAge(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[0]));
		
		hisPojo.setAgeMonth(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[1]));
		
		hisPojo.setAgeDay(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[2]));
		
		String sql = " SELECT NEW_BORN_FLG, WEIGHT FROM ADM_INP WHERE CASE_NO = '" + odiStationControl.getCaseNo() + "'";
		
		TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		hisPojo.setNewBornFlg(admParm.getValue("NEW_BORN_FLG", 0));
		
		
		hisPojo.setWeight(admParm.getDouble("WEIGHT", 0));
		
		
		
		sql = 
			" SELECT B.TAG_CODE" +
			" FROM ADM_INPDIAG A, SYS_DIAGNOSIS_TAGS B" +
			" WHERE A.ICD_TYPE = B.ICD_TYPE AND A.ICD_CODE = B.ICD_CODE " +
			" AND A.CASE_NO = '" + odiStationControl.getCaseNo() + "' ";
		
		TParm diagParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		List<String> diags = new ArrayList<String>();
		
		String diag;
		for (int i = 0; i < diagParm.getCount(); i++) {
			diag = diagParm.getValue("TAG_CODE", i);
			if(diag.length() > 0 && !diags.contains(diag)){
				diags.add(diag);
			}
		}
		
		hisPojo.setDiags(diags);
		
		sql = 
			" SELECT B.TAG_CODE TAG_CODE1, C.TAG_CODE TAG_CODE2" +
			" FROM OPE_OPBOOK A, SYS_DIAGNOSIS_TAGS B, SYS_DIAGNOSIS_TAGS C" +
			" WHERE     A.OP_CODE1 = B.ICD_TYPE" +
			" AND A.OP_CODE2 = C.ICD_CODE(+)" +
			" AND A.CASE_NO = '" + odiStationControl.getCaseNo() + "'";
		
		TParm opParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		List<String> operationDiags = new ArrayList<String>();
		
		String op1, op2;
		for (int i = 0; i < opParm.getCount(); i++) {
			op1 = opParm.getValue("TAG_CODE1", i);
			op2 = opParm.getValue("TAG_CODE2", i);
			if(op1.length() > 0 && !operationDiags.contains(op1)){
				operationDiags.add(op1);
			}
			if(op2.length() > 0 && !operationDiags.contains(op2)){
				operationDiags.add(op2);
			}
		}
		
		hisPojo.setOperationDiags(operationDiags);
		
		
		if(pat.getLMPDate() != null){
			hisPojo.setLmpFlg("Y");
		}
		
		
		OdiDrugAllergy odiDrugArrergy = odiStationControl.odiDrugArrergy;
		String daLastFilter = odiDrugArrergy.getFilter();
		odiDrugArrergy.setFilter("");
		odiDrugArrergy.filter();
		
		for (int i = 0; i < odiDrugArrergy.rowCount(); i++) {
			if(odiDrugArrergy.getItemString(i, "DRUGORINGRD_CODE").length() > 0 || odiDrugArrergy.getItemString(i, "ALLERGY_NOTE").length() > 0){
				
				if(!("N".equals(odiDrugArrergy.getItemString(i, "DRUGORINGRD_CODE")) 
						&& "N".equals(odiDrugArrergy.getItemString(i, "DRUG_TYPE")))
				){
					hisPojo.setAllergyFlg("Y");
					break;
				}
				
				
			}
		}
		odiDrugArrergy.setFilter(daLastFilter);
		odiDrugArrergy.filter();
		

		OrderPojo orderPojo;
		ExaPojo exaPojo;
		
		TParm newParm = new TParm();
		StringBuilder cpOld = new StringBuilder(); //胸疼中心药品规则表
		StringBuilder cpNew = new StringBuilder(); //胸疼中心药品规则表		
		boolean chestPainFlg = false;
		if(getChestPain(odiStationControl.getCaseNo())){
			chestPainFlg= true;
		}
		

		List<OrderPojo> orders = new ArrayList<OrderPojo>();
		List<OrderPojo> lastOrders = new ArrayList<OrderPojo>();
		List<ExaPojo> exaPojos = new ArrayList<ExaPojo>();

		String startDate = StringTool.getString((Timestamp) odiStationControl
				.getValue("START_DATEST"), "yyyy-MM-dd HH:mm:ss");
		String endDate = StringTool.getString((Timestamp) odiStationControl
				.getValue("END_DATEST"), "yyyy-MM-dd HH:mm:ss");

		sql = "SELECT ORDER_NO,ORDER_SEQ, LINK_NO, ORDER_CODE, MEDI_QTY, MEDI_UNIT, FREQ_CODE, TAKE_DAYS, MED_APPLY_NO, EFF_DATE FROM ODI_ORDER WHERE CASE_NO = '"
				+ odiStationControl.getCaseNo()
				+ "' AND RX_KIND = 'ST' AND (ORDER_DATE < TO_DATE ('"
				+ startDate
				+ "', 'YYYY-MM-DD HH24:MI:SS') OR ORDER_DATE > TO_DATE ('"
				+ endDate + "', 'YYYY-MM-DD HH24:MI:SS'))";

		TParm p = new TParm(TJDODBTool.getInstance().select(sql));

//		StringBuilder sb = new StringBuilder();
		
//		List<String> sbl = new ArrayList<String>();
		
		// 开始时间前的临时
		for (int i = 0; i < p.getCount(); i++) {
			if(orderCodes.contains(p.getValue("ORDER_CODE", i))){
				orderPojo = new OrderPojo();
				orderPojo.setId(sysUtil.generateShortUuid());
				orderPojo.setOrderCode(p.getValue("ORDER_CODE", i));
				orderPojo.setMedQty(p.getDouble("MEDI_QTY", i));
				orderPojo.setUnit(p.getValue("MEDI_UNIT", i).length() == 0 ? null
						: p.getValue("MEDI_UNIT", i));
				orderPojo.setTakeDays(p.getInt("TAKE_DAYS", i));
				orderPojo.setFreqCycle(new Integer(getFreqCycle(p.getValue(
						"FREQ_CODE", i))));
				orderPojo.setFreqTimes(new Integer(getFreqFreqTimes(p.getValue(
						"FREQ_CODE", i))));
				orderPojo.setSysDateLong(sysUtil.parseDateStr2Long(sysDateStr));
				orderPojo.setSysTimeLong(sysUtil.parseTimeStr2Long(sysTimeStr));
				orderPojo.setOrderDateLong(sysUtil.parseDateStr2Long(sysUtil
						.getDateStr(p.getTimestamp("EFF_DATE", i))));
				orderPojo.setOrderTimeLong(sysUtil.parseTimeStr2Long(sysUtil
						.getTimeStr(p.getTimestamp("EFF_DATE", i))));
				orderPojo.setLiquidNo(p.getValue("LINK_NO", i));
				orderPojo.setRxNo(p.getValue("ORDER_NO", i));  //add by huangtt 20150807
				orderPojo.setSeqNo(p.getValue("ORDER_SEQ", i));  //add by huangtt 20150807
				lastOrders.add(orderPojo);
			}
			
			if(chestPainFlg){
				if(orderCodesChestpain.contains(p.getValue("ORDER_CODE", i))){
					cpOld.append("'");
					cpOld.append(p.getValue("ORDER_CODE", i));
					cpOld.append("'");
					cpOld.append(",");
				}
			}
			
			
//			if(!sbl.contains(p.getValue("MED_APPLY_NO", i))){
//				sbl.add(p.getValue("MED_APPLY_NO", i));
//			}
			
//			sb.append("'");
//			sb.append(p.getValue("MED_APPLY_NO", i));
//			sb.append("'");
//			sb.append(" OR APPLICATION_NO = ");
			
		}

		// 开始时间后的临时和长期
		TDS ds = odiObject.getDS("ODI_ORDER");
		String lastFilter = ds.getFilter();
		ds.setFilter("");
		ds.filter();

		for (int i = 0; i < ds.rowCount(); i++) {
			if (ds.getItemString(i, "ORDER_CODE").length() > 0) {
				if(orderCodes.contains(ds.getItemString(i, "ORDER_CODE"))){
					orderPojo = new OrderPojo();
					orderPojo.setId(sysUtil.generateShortUuid());
					orderPojo.setOrderCode(ds.getItemString(i, "ORDER_CODE"));
					orderPojo.setMedQty(ds.getItemDouble(i, "MEDI_QTY"));
					orderPojo
							.setUnit(ds.getItemString(i, "MEDI_UNIT").length() == 0 ? null
									: ds.getItemString(i, "MEDI_UNIT"));
					orderPojo.setTakeDays(ds.getItemInt(i, "TAKE_DAYS"));
					orderPojo.setFreqCycle(new Integer(getFreqCycle(ds
							.getItemString(i, "FREQ_CODE"))));
					orderPojo.setFreqTimes(new Integer(getFreqFreqTimes(ds
							.getItemString(i, "FREQ_CODE"))));
					orderPojo.setSysDateLong(sysUtil.parseDateStr2Long(sysDateStr));
					orderPojo.setSysTimeLong(sysUtil.parseTimeStr2Long(sysTimeStr));
					orderPojo
							.setOrderDateLong(sysUtil
									.parseDateStr2Long(sysUtil
											.getDateStr(ds.getItemTimestamp(i,
													"EFF_DATE") == null ? SystemTool
													.getInstance().getDate()
													: ds.getItemTimestamp(i,
															"EFF_DATE"))));
					orderPojo
							.setOrderTimeLong(sysUtil
									.parseTimeStr2Long(sysUtil
											.getTimeStr(ds.getItemTimestamp(i,
													"EFF_DATE") == null ? SystemTool
													.getInstance().getDate()
													: ds.getItemTimestamp(i,
															"EFF_DATE"))));
					orderPojo.setLiquidNo(ds.getItemString(i, "LINK_NO"));
					orderPojo.setRxNo(ds.getItemString(i, "ORDER_NO"));  //add by huangtt 20150807
					orderPojo.setSeqNo(ds.getItemString(i, "ORDER_SEQ"));  //add by huangtt 20150807
					if("Y".equals(ds.getItemString(i, "#NEW#"))){
//						System.out.println(orderPojo.getOrderCode()+"=="+orderPojo.getRxNo()+"==="+orderPojo.getSeqNo()+"==="+orderPojo.getMedQty());
						orders.add(orderPojo);
					}else{
						lastOrders.add(orderPojo);
					}
				}
				
				if(chestPainFlg){
					if(orderCodesChestpain.contains(ds.getItemString(i, "ORDER_CODE"))){
						
						if(ds.getItemString(i, "#NEW#").equals("Y")){
							newParm.addData("ORDER_CODE", ds.getItemString(i, "ORDER_CODE"));
							newParm.addData("SYS_PHA_CLASS", getSysPhaClass(ds.getItemString(i, "ORDER_CODE")));
							cpNew.append("'");
							cpNew.append(ds.getItemString(i, "ORDER_CODE"));
							cpNew.append("'");
							cpNew.append(",");
						}else{
							cpOld.append("'");
							cpOld.append(ds.getItemString(i, "ORDER_CODE"));
							cpOld.append("'");
							cpOld.append(",");
						}
					}
				}
				
//				if(!sbl.contains(ds.getItemString(i, "MED_APPLY_NO"))){
//					sbl.add(ds.getItemString(i, "MED_APPLY_NO"));
//				}
				
//				sb.append("'");
//				sb.append(ds.getItemString(i, "MED_APPLY_NO"));
//				sb.append("'");
//				sb.append(" OR APPLICATION_NO = ");
			}
		}
		ds.setFilter(lastFilter);
		ds.filter();
		
		
		List<OrderPojo> chestPainOrders = new ArrayList<OrderPojo>();
		if(chestPainFlg){
//			System.out.println("newParm---"+newParm);
			if(newParm.getCount("ORDER_CODE") > 0){
				cpNew.append("''");
				cpOld.append("''");
				String cp = cpOld.toString()+","+cpNew.toString();
//				System.out.println("order_code in ==="+cp.toString());
				for (int j = 0; j < newParm.getCount("ORDER_CODE"); j++) {
					orderPojo = new OrderPojo();
					String sysPhaClass2 ="";
					orderPojo.setOrderCode(newParm.getValue("ORDER_CODE", j));
					orderPojo.setSysPhaClass1(newParm.getValue("SYS_PHA_CLASS", j));
					orderPojo.setSysPhaClass2(sysPhaClass2);	
				
					if(newParm.getValue("SYS_PHA_CLASS", j).equals("1")){
						sysPhaClass2 = getSysPhaClass(cp.toString(),"2");
						if(sysPhaClass2.length() > 0){
							
							if(getSysPhaClass(cpOld.toString(),"1").length() == 0){
								orderPojo.setSysPhaClass2(sysPhaClass2);
							}
							
							
						}else{
							sysPhaClass2 = getSysPhaClass(cp.toString(),"3");
							if(sysPhaClass2.length() > 0){
								if(getSysPhaClass(cpOld.toString(),"1").length() == 0){
									orderPojo.setSysPhaClass2(sysPhaClass2);
								}
								
							}
						}
						
					}else if(newParm.getValue("SYS_PHA_CLASS", j).equals("2")){
						sysPhaClass2 = getSysPhaClass(cp.toString(),"1");
						if(sysPhaClass2.length() > 0){
							if(getSysPhaClass(cpOld.toString(),"2").length() == 0){
								orderPojo.setSysPhaClass2(sysPhaClass2);
							}
							
							
						}
					}else if(newParm.getValue("SYS_PHA_CLASS", j).equals("3")){
						sysPhaClass2 = getSysPhaClass(cp.toString(),"1");
						if(sysPhaClass2.length() > 0){
							if(getSysPhaClass(cpOld.toString(),"3").length() == 0){
								orderPojo.setSysPhaClass2(sysPhaClass2);
							}
							
							
						}
					}
					
					
					if(orderPojo.getSysPhaClass1().length() > 0 && orderPojo.getSysPhaClass2().length() > 0){
						orderPojo.setOrderCode("");
						chestPainOrders.add(orderPojo);
						
						orderPojo = new OrderPojo();
						orderPojo.setOrderCode(newParm.getValue("ORDER_CODE", j));
						orderPojo.setSysPhaClass1(newParm.getValue("SYS_PHA_CLASS", j));
						orderPojo.setSysPhaClass2("");	
						
					}
					
					chestPainOrders.add(orderPojo);
					
				}
				
			}
		}
		
//		for (OrderPojo o :chestPainOrders) {
//			System.out.println(o.getOrderCode()+"=="+o.getSysPhaClass1()+"=="+o.getSysPhaClass2()+"==");
//		}
		
		hisPojo.setChestpainOrderPojos(chestPainOrders);
		

//		sb.append("''");
		
//		for (int j = 0; j < sbl.size(); j++) {
//			sb.append("'");
//			sb.append(sbl.get(j));
//			sb.append("'");
//			if(j < sbl.size() - 1){
//				sb.append(" OR APPLICATION_NO = ");
//			}
//		}
		
//		if(sbl.size() == 0){
//			sb = new StringBuilder();
//			sb.append("''");
//		}
		
		sql = 
			" SELECT B.TESTITEM_CODE, B.TEST_VALUE, B.TEST_UNIT" +
			" FROM MED_APPLY A, MED_LIS_RPT B" +
			" WHERE     A.APPLICATION_NO = B.APPLICATION_NO" +
			" AND A.CAT1_TYPE = B.CAT1_TYPE" +
			" AND A.ADM_TYPE = 'I'" +
			" AND A.CASE_NO = '" + odiStationControl.getCaseNo() + "'";
		
//		System.out.println(sql); 

		TParm p2 = new TParm(TJDODBTool.getInstance().select(sql));

		for (int j = 0; j < p2.getCount(); j++) {
			if(testItemCodes.contains(p2.getValue("TESTITEM_CODE", j))){
				exaPojo = new ExaPojo();
				exaPojo.setId(sysUtil.generateShortUuid());
				exaPojo.setTestitemCode(p2.getValue("TESTITEM_CODE", j));
				exaPojo.setTestValue(p2.getDouble("TEST_VALUE", j));
				exaPojo
						.setTestUnit(p2.getValue("TEST_UNIT", j).length() == 0 ? null
								: p2.getValue("TEST_UNIT", j));
				exaPojos.add(exaPojo);
			}
		}

		hisPojo.setOrderPojos(orders);
		
		hisPojo.setLastOrderPojos(lastOrders);

		hisPojo.setExaPojos(exaPojos);
		
		List<AllergyPojo> allergyPojos = new ArrayList<AllergyPojo>();
		List<ErdPojo> erdPojos = new ArrayList<ErdPojo>();
		AllergyPojo allergyPojo;
		ErdPojo erdPojo;
		
		for (int i = 0; i < monitorItemEns.size(); i++) {
			sql = "  SELECT X.MONITOR_ITEM_EN, X.MONITOR_VALUE, X.MEASURE_UNIT"
					+ " FROM (  SELECT A.MONITOR_ITEM_EN, A.MONITOR_VALUE, B.MEASURE_UNIT"
					+ " FROM ODI_CISVITALSIGN A, CRP_VITAL_CONFIG B"
					+ " WHERE A.CASE_NO = '" + odiStationControl.getCaseNo()+ "'" 
					+ " AND A.MONITOR_ITEM_EN = '"+monitorItemEns.get(i)+"'"
					+ " AND B.MEASURE_CODE = A.MONITOR_ITEM_EN"
					+ " ORDER BY A.MONITOR_TIME DESC) X" + " WHERE ROWNUM <= 1"
					+ " ORDER BY ROWNUM DESC";
			TParm erdParm = new TParm(TJDODBTool.getInstance().select(sql));
			for (int j = 0; j < erdParm.getCount(); j++) {
				erdPojo = new ErdPojo();
				erdPojo.setId(sysUtil.generateShortUuid());
				erdPojo.setMonitorItemEn(erdParm.getValue("MONITOR_ITEM_EN", j));
				erdPojo.setMonitorValue(erdParm.getDouble("MONITOR_VALUE", j));
				erdPojo.setMeasureUnit(erdParm.getValue("MEASURE_UNIT", j));
				erdPojos.add(erdPojo);
			}
		}
		
		
		hisPojo.setErdPojos(erdPojos);
		
		sql="SELECT DRUG_TYPE,DRUGORINGRD_CODE FROM OPD_DRUGALLERGY " +
				" WHERE MR_NO='"+odiStationControl.getMrNo()+"'";
		TParm aParm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < aParm.getCount(); i++) {
			if(!("N".equals(aParm.getValue("DRUGORINGRD_CODE",i)) 
					&& "N".equals(aParm.getValue("DRUG_TYPE",i)))
			){
				allergyPojo = new AllergyPojo();
				 allergyPojo.setId(sysUtil.generateShortUuid());
				 allergyPojo.setDrugType(aParm.getValue("DRUG_TYPE", i));
				 allergyPojo.setDrugoringrdCode(aParm.getValue("DRUGORINGRD_CODE", i));
				 allergyPojos.add(allergyPojo);
			}
			 
		}
		hisPojo.setAllergyPojos(allergyPojos);
		
		return hisPojo;
	}
	
	private String getSysPhaClass(String orderCode){
		String sql = "SELECT SYS_PHA_CLASS FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String re = "";
		if(parm.getCount() > 0){
			String classPha = parm.getValue("SYS_PHA_CLASS", 0);
			if(classPha.length() > 0){
				re = classPha.split(";")[0];
			}
		}
		return re;
	}
	
	private String getSysPhaClass(String orderCode,String sysPhaClass){
		String sql = "SELECT SYS_PHA_CLASS FROM SYS_FEE WHERE ORDER_CODE IN ("+orderCode+") AND SYS_PHA_CLASS='"+sysPhaClass+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String re = "";
		if(parm.getCount() > 0){
			re = sysPhaClass;
		}
		return re;
	}
	
	
	public void onCdssCal(boolean flg) {
//		List<String> orderCodes = CDSSConfig.getInstance().getOrderCodes();
		List<String> orderCodes = CDSSConfig.getInstance().getOrderCodesChilder();		
		TTabbedPane tab = (TTabbedPane) odiStationControl.getComponent("TABLEPANE");
		TTable table;
		int selectRow;
		String orderCode;
		TParm p;
		TTableNode node;
		
		TDS ds;
		
		switch (tab.getSelectedIndex()) {
		
		case 0:
			// 临时
			table = (TTable) odiStationControl.getComponent(ODIStationControl.TABLE1);
			ds = odiStationControl.odiObject.getDS("ODI_ORDER");
			selectRow = table.getSelectedRow();
			orderCode = ds.getItemString(selectRow, "ORDER_CODE");
			if(!orderCodes.contains(orderCode)){
				return;
			}
			p = onCdssCal(odiStationControl.odiObject, orderCode, flg);
			
			if(p == null){
				return;
			}
			
			node = new TTableNode();
			node.setTable(table);
			node.setColumn(table.getColumnIndex("MEDI_QTY"));
			node.setRow(selectRow);
			node.setValue(p.getDouble("MEDI_QTY"));
			odiStationControl.onChangeTableValueST(node);
			
//			if(p.getValue("FREQ_CODE").length() > 0){
//				node = new TTableNode();
//				node.setTable(table);
//				node.setColumn(table.getColumnIndex("FREQ_CODE"));
//				node.setRow(selectRow);
//				node.setValue(p.getValue("FREQ_CODE"));
//				odiStationControl.onChangeTableValueST(node);
//			}
			
			break;
		case 1:
			// 长期
			table = (TTable) odiStationControl.getComponent(ODIStationControl.TABLE2);
			ds = odiStationControl.odiObject.getDS("ODI_ORDER");
			selectRow = table.getSelectedRow();
			orderCode = ds.getItemString(selectRow, "ORDER_CODE");
			if(!orderCodes.contains(orderCode)){
				return;
			}
			p = onCdssCal(odiStationControl.odiObject, orderCode, flg);
			
			if(p == null){
				return;
			}
			
			node = new TTableNode();
			node.setTable(table);
			node.setColumn(table.getColumnIndex("MEDI_QTY"));
			node.setRow(selectRow);
			node.setValue(p.getDouble("MEDI_QTY"));
			table.setItem(selectRow, table.getColumnIndex("MEDI_QTY"), p.getDouble("MEDI_QTY"));
			odiStationControl.onChangeTableValueUD(node);
			
			if(p.getValue("FREQ_CODE").length() > 0){
				node = new TTableNode();
				node.setTable(table);
				node.setColumn(table.getColumnIndex("FREQ_CODE"));
				node.setRow(selectRow);
				node.setValue(p.getValue("FREQ_CODE"));
				table.setItem(selectRow, table.getColumnIndex("FREQ_CODE"), p.getValue("FREQ_CODE"));
				odiStationControl.onChangeTableValueUD(node);
			}
			
			break;
		case 2:
			// 出院带药
			table = (TTable) odiStationControl.getComponent(ODIStationControl.TABLE3);
			ds = odiStationControl.odiObject.getDS("ODI_ORDER");
			selectRow = table.getSelectedRow();
			orderCode = ds.getItemString(selectRow, "ORDER_CODE");
			if(!orderCodes.contains(orderCode)){
				return;
			}
			p = onCdssCal(odiStationControl.odiObject, orderCode, flg);
			
			if(p == null){
				return;
			}
			
			node = new TTableNode();
			node.setTable(table);
			node.setColumn(table.getColumnIndex("MEDI_QTY"));
			node.setRow(selectRow);
			node.setValue(p.getDouble("MEDI_QTY"));
			table.setItem(selectRow, table.getColumnIndex("MEDI_QTY"), p.getDouble("MEDI_QTY"));
			odiStationControl.onChangeTableValueDS(node);
			
			if(p.getValue("FREQ_CODE").length() > 0){
				node = new TTableNode();
				node.setTable(table);
				node.setColumn(table.getColumnIndex("FREQ_CODE"));
				node.setRow(selectRow);
				node.setValue(p.getValue("FREQ_CODE"));
				table.setItem(selectRow, table.getColumnIndex("FREQ_CODE"), p.getValue("FREQ_CODE"));
				odiStationControl.onChangeTableValueDS(node);
			}
			
			break;
		}
		
		
		
	}
	
	public TParm onCdssCal(OdiObject odiObject, String orderCode, boolean flg){		
		HisPojo hisPojo = gethispHisPojo(odiObject);
		if(hisPojo.getAge() > 14 && !"Y".equals(hisPojo.getNewBornFlg())){
			return null;
		}
		hisPojo.setAdmType("I");
		TParm returnParm = new TParm();		
		TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);		
//		hisPojo = CDSSClient.getInstance().fireRules3(hisPojo);
		
		
		returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
				"fireRule3", inParm);

		hisPojo = sysUtil.parseTParmToHisPojo(returnParm);
		
		
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();

		if (advicePojos.size() == 0) {
			return null;
		}
		
		TDS ds = odiObject.getDS("ODI_ORDER");
		String lastFilter = ds.getFilter();
		ds.setFilter("");
		ds.filter();
		
		Map<String, String> orderMap = new HashMap<String, String>();

		for (int i = 0; i < ds.rowCount(); i++) {
			if (orderCode.equals(ds.getItemString(i, "ORDER_CODE"))) {
				orderMap.put(ds.getItemString(i, "ORDER_CODE"), ds.getItemString(i, "ORDER_DESC"));
			}
		}
		
		ds.setFilter(lastFilter);
		ds.filter();
		
		
		TParm parm = new TParm();
		
		List<String> ids = new ArrayList<String>();
		for (AdvicePojo advicePojo : advicePojos) {
			if(!ids.contains(advicePojo.getKnowladgeId())){
				if (orderCode.equals(advicePojo.getOrderCode())) {
					parm.addData("ID", advicePojo.getKnowladgeId());
					parm.addData("LEVEL", advicePojo.getLevel());
					parm.addData("ADVICE", advicePojo.getAdviceText());
					parm.addData("ORDER_CODE", advicePojo.getOrderCode());
					parm.addData("ORDER_DESC", orderMap.get(advicePojo.getOrderCode()));
					parm.addData("MEDI_QTY", advicePojo.getMedQty());
					parm.addData("UNIT_CODE", advicePojo.getUnit());
					parm.addData("FREQ_CODE", advicePojo.getFreqCode());
					parm.addData("REMARKS", advicePojo.getRemarks());
					ids.add(advicePojo.getKnowladgeId());
				}
			}
		}
		
		if(parm.getCount("ID")<=0){
			return null;
		}
		TParm p = null;
		//flg true表示住院医生站开立医嘱时自动带出最小量    false表示弹出选择框让医生自己选择
		if(flg){
			int row = 0;
			if(parm.getCount("ID") == 1){
				row = 0;
			}else{
				for (int i = 1; i < parm.getCount("ID"); i++) {
					if(parm.getDouble("MEDI_QTY", row) > parm.getDouble("MEDI_QTY", i)){
						row = i;
					}
				}
	
			}
			p = new TParm();
			p.setData("MEDI_QTY", StringTool.round(parm.getDouble("MEDI_QTY",row), 4));
			p.setData("UNIT_CODE", parm.getValue("UNIT",row));
			p.setData("FREQ_CODE", parm.getValue("FREQ_CODE",row));
		}else{
			TParm droolsLogBean = new TParm();
			droolsLogBean.setData("LOG_PARM", parm.getData());
			
			Object obj = odiStationControl.openDialog(URLCAL, droolsLogBean);

			if(obj != null && obj instanceof TParm){
				p = (TParm) obj;
			}
		}
		
		
		return p;
		
	}

	@Override
	public boolean fireRules() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateCkbLog(TParm updateParm) {
		Timestamp date = SystemTool.getInstance().getDate();
		String sDate = date.toString().substring(0, 10).replace("-", "").replace("/", "")+"000000";
		String eDate = date.toString().substring(0, date.toString().length()-2).replace("-", "").replace("/", "").replace(":", "").replace(" ", "");
		for (int i = 0; i < updateParm.getCount("ORDER_NO"); i++) {
			String orderNo=updateParm.getValue("MEDI_QTY", i)+updateParm.getValue("MEDI_UNIT", i)
			+new Integer(getFreqCycle(updateParm.getValue("FREQ_CODE", i)))
			+sysUtil.parseTimeStr2Long(sysUtil.getTimeStr(updateParm.getTimestamp("EFF_DATE", i))); 



			String sql = "UPDATE DSS_CKBLOG SET ORDER_NO='"+updateParm.getValue("ORDER_NO", i)+"'," +
			" ORDER_SEQ=" + updateParm.getValue("ORDER_SEQ", i) +
			" WHERE  DEPT_CODE = '"+Operator.getDept()+"'" +
			" AND DR_CODE = '"+Operator.getID()+"'" +
			" AND CASE_NO = '"+updateParm.getValue("CASE_NO", i)+"'" +
			" AND ORDER_NO='"+orderNo+"'" +
			" AND ORDER_CODE='"+updateParm.getValue("ORDER_CODE", i)+"'" +
			" AND IS_CONFIRM = 'Y'" +
			" AND ADM_TYPE = 'I'" +
			" AND LOG_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')";
			
//			System.out.println("update==="+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
			
		}
		
		
	}

	@Override
	public boolean fireRulesOrder() {
		// TODO Auto-generated method stub 
		return false;
	}
	
	private boolean getChestPain(String caseNo){
		String sql = "SELECT ENTER_ROUTE, PATH_KIND FROM REG_PATADM A,ADM_INP B,ADM_RESV C WHERE"
				+ " B.CASE_NO = '"+caseNo+"'"
				+ " AND B.CASE_NO=C.IN_CASE_NO"
				+ " AND C.OPD_CASE_NO = A.CASE_NO"
				+ " AND A.ENTER_ROUTE='E02'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			return true;
		}
		return false;
	}
	
	
}
