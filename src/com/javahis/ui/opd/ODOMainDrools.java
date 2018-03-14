package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

import jdo.odi.OdiObject;
import jdo.odo.Diagrec;
import jdo.odo.DrugAllergy;
import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.bil.BIL;
import jdo.cdss.AdvicePojo;
import jdo.cdss.AllergyPojo;
import jdo.cdss.CDSSConfig;
import jdo.cdss.ErdPojo;
import jdo.cdss.ExaPojo;
import jdo.cdss.HisPojo;
import jdo.cdss.OrderPojo;

public class ODOMainDrools extends CDSSStationDrools{

	public OdoMainControl odoMainControl;

	private static final String URLDROOLS = "%ROOT%\\config\\drools\\ODOMainDeleteRx.x";

	public ODOMainDrools(OdoMainControl odoMainControl) {
		this.odoMainControl = odoMainControl;
	}

	/**
	 * 执行规则
	 * 
	 * @return
	 */
	public boolean fireRules() {

		HisPojo hisPojo = gethispHisPojo(odoMainControl.reg,
				odoMainControl.pat, odoMainControl.odo);
		
		TParm returnParm = new TParm();
		
		TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);

//		hisPojo = CDSSClient.getInstance().fireRules1(hisPojo);
		returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
				"fireRule5", inParm);

		
		hisPojo = sysUtil.parseTParmToHisPojo(returnParm);

		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();

		if(advicePojos.size() == 0){
			return false;
		}
		
		TParm parm = new TParm();
		
		for (AdvicePojo advicePojo : advicePojos) {
			parm.addData("ID", advicePojo.getKnowladgeId());
			parm.addData("LEVEL", advicePojo.getLevel());
			parm.addData("ADVICE", advicePojo.getAdviceText());
			parm.addData("REMARK", "");
			parm.addData("ORDER_CODE", advicePojo.getOrderCode());
			parm.addData("ORDER_NO", advicePojo.getRxNo());
			parm.addData("ORDER_SEQ", advicePojo.getSeqNo());
		}
		
		TParm droolsLogBean = new TParm();
		droolsLogBean.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		droolsLogBean.setData("MR_NO", odoMainControl.odo.getMrNo());
		droolsLogBean.setData("ADM_TYPE", odoMainControl.reg.getAdmType());
		droolsLogBean.setData("LOG_PARM", parm.getData());
		
		Object obj = odoMainControl.openDialog(URLDROOLS, droolsLogBean);
		
		String objStr = (String)obj;
		
		if("Y".equals(objStr)){
			return false;
		}

		return true;
	}
	


	private HisPojo gethispHisPojo(Reg reg, Pat pat, ODO odo) {
		List<String> orderCodes = CDSSConfig.getInstance().getOrderCodes();
		List<String> orderCodesChestpain = CDSSConfig.getInstance().getOrderCodesChestpain();
		List<String> testItemCodes = CDSSConfig.getInstance().getTestItemCodes();

		Timestamp sysTs = SystemTool.getInstance().getDate();

		String sysDateStr = sysUtil.getDateStr(sysTs);

		String sysTimeStr = sysUtil.getTimeStr(sysTs);

		HisPojo hisPojo = new HisPojo();
		
		hisPojo.setAdmType(reg.getAdmType());

		hisPojo.setSex(pat.getSexString());

		hisPojo.setAge(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[0]));
		
		hisPojo.setAgeMonth(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[1]));
		
		hisPojo.setAgeDay(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[2]));
		
		Diagrec diagrec = odo.getDiagrec();
		String diagLastFilter = diagrec.getFilter();
		diagrec.setFilter("");
		diagrec.filter();
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < diagrec.rowCount(); i++) {
			sb.append("('");
			sb.append(diagrec.getItemString(i, "ICD_CODE"));
			sb.append("','");
			sb.append(diagrec.getItemString(i, "ICD_TYPE"));
			sb.append("')");
			sb.append(",");
		}
		sb.append("('','')");
		
		diagrec.setFilter(diagLastFilter);
		diagrec.filter();
		
		String sql = 
			" SELECT TAG_CODE" +
			" FROM SYS_DIAGNOSIS_TAGS " +
			" WHERE (ICD_CODE, ICD_TYPE) IN ("
			+ sb.toString() + ") ";
		
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
		
		
		if(pat.getLMPDate() != null){
			hisPojo.setLmpFlg("Y");
		}
		
		DrugAllergy drugAllergy = odo.getDrugAllergy();
		String daLastFilter = drugAllergy.getFilter();
		drugAllergy.setFilter("");
		drugAllergy.filter();
		
		List<AllergyPojo> allergyPojos = new ArrayList<AllergyPojo>();		
		AllergyPojo allergyPojo;
		for (int i = 0; i < drugAllergy.rowCount(); i++) {
			if(drugAllergy.getItemString(i, "DRUGORINGRD_CODE").length() > 0 || drugAllergy.getItemString(i, "ALLERGY_NOTE").length() > 0){							
				 if(!("N".equals(drugAllergy.getItemString(i, "DRUGORINGRD_CODE")) 
							&& "N".equals(drugAllergy.getItemString(i, "DRUG_TYPE")))
					){
					 allergyPojo = new AllergyPojo();
					 allergyPojo.setId(sysUtil.generateShortUuid());
					 allergyPojo.setDrugType(drugAllergy.getItemString(i, "DRUG_TYPE"));
					 allergyPojo.setDrugoringrdCode(drugAllergy.getItemString(i, "DRUGORINGRD_CODE"));
					 allergyPojos.add(allergyPojo);
					}
				
			}
		}
		
		for (int i = 0; i < drugAllergy.rowCount(); i++) {
			if(drugAllergy.getItemString(i, "DRUGORINGRD_CODE").length() > 0 || drugAllergy.getItemString(i, "ALLERGY_NOTE").length() > 0){
				if(!("N".equals(drugAllergy.getItemString(i, "DRUGORINGRD_CODE")) 
						&& "N".equals(drugAllergy.getItemString(i, "DRUG_TYPE")))
				){
					hisPojo.setAllergyFlg("Y");
					break;
				}
				
			}
		}
		drugAllergy.setFilter(daLastFilter);
		drugAllergy.filter();
		
		
		OrderPojo orderPojo;
		ExaPojo exaPojo;

		List<OrderPojo> orders = new ArrayList<OrderPojo>();
		List<ExaPojo> exaPojos = new ArrayList<ExaPojo>();
		
		boolean chestPainFlg = false;
		if(getChestPain(reg.caseNo())){
			chestPainFlg= true;
		}
		

		OpdOrder opdOrder = odo.getOpdOrder();
		TParm newParm = new TParm();
		StringBuilder cpOld = new StringBuilder(); //胸疼中心药品规则表
		StringBuilder cpNew = new StringBuilder(); //胸疼中心药品规则表
		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();
		sb = new StringBuilder();
		for (int i = 0; i < opdOrder.rowCount(); i++) {

			if (opdOrder.getItemString(i, "ORDER_CODE").length() > 0) {
				if(orderCodes.contains(opdOrder.getItemString(i, "ORDER_CODE"))){
					orderPojo = new OrderPojo();
					orderPojo.setId(sysUtil.generateShortUuid());
					orderPojo.setOrderCode(opdOrder.getItemString(i, "ORDER_CODE"));
					orderPojo.setMedQty(opdOrder.getItemDouble(i, "MEDI_QTY"));
					orderPojo.setUnit(opdOrder.getItemString(i, "MEDI_UNIT")
							.length() == 0 ? null : opdOrder.getItemString(i,
							"MEDI_UNIT"));
					orderPojo.setTakeDays(opdOrder.getItemInt(i, "TAKE_DAYS"));
					orderPojo.setFreqCycle(new Integer(getFreqCycle(opdOrder
							.getItemString(i, "FREQ_CODE"))));
					orderPojo.setFreqTimes(new Integer(getFreqFreqTimes(opdOrder
							.getItemString(i, "FREQ_CODE"))));
					orderPojo.setSysDateLong(sysUtil.parseDateStr2Long(sysDateStr));
					orderPojo.setSysTimeLong(sysUtil.parseTimeStr2Long(sysTimeStr));
					try {
						orderPojo.setOrderDateLong(sysUtil
								.parseDateStr2Long(sysUtil.getDateStr(opdOrder
										.getItemTimestamp(i, "ORDER_DATE"))));
						orderPojo.setOrderTimeLong(sysUtil
								.parseTimeStr2Long(sysUtil.getTimeStr(opdOrder
										.getItemTimestamp(i, "ORDER_DATE"))));
					} catch (Exception e) {
						// TODO: handle exception
					}
					orderPojo.setLiquidNo(opdOrder.getItemString(i, "LINK_NO"));
					orderPojo.setRxNo(opdOrder.getItemString(i, "RX_NO"));
					orderPojo.setSeqNo(opdOrder.getItemString(i, "SEQ_NO"));
					
//					TParm sysFeeParm = getSysFee(opdOrder.getItemString(i, "ORDER_CODE"));
//					orderPojo.setOptitemCode(sysFeeParm.getValue("OPTITEM_CODE"));
//					orderPojo.setTransHospCode(sysFeeParm.getValue("TRANS_HOSP_CODE"));
					
					orders.add(orderPojo);

					sb.append("'");
					sb.append(opdOrder.getItemString(i, "MED_APPLY_NO"));
					sb.append("'");
					sb.append(",");
				}
				//胸疼中心药品 规则
				if(chestPainFlg){

					if(orderCodesChestpain.contains(opdOrder.getItemString(i, "ORDER_CODE"))){
						
						System.out.println(opdOrder.getItemString(i, "#NEW#")+"====="+opdOrder.getItemString(i, "ORDER_CODE"));
						if(opdOrder.getItemString(i, "#NEW#").equals("Y")){
							newParm.addData("ORDER_CODE", opdOrder.getItemString(i, "ORDER_CODE"));
							newParm.addData("SYS_PHA_CLASS", getSysPhaClass(opdOrder.getItemString(i, "ORDER_CODE")));
						
							cpNew.append("'");
							cpNew.append(opdOrder.getItemString(i, "ORDER_CODE"));
							cpNew.append("'");
							cpNew.append(",");
						}else{
							cpOld.append("'");
							cpOld.append(opdOrder.getItemString(i, "ORDER_CODE"));
							cpOld.append("'");
							cpOld.append(",");
						}
					}
					
				}
				
			}
		}
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();
		sb.append("''");
		
		List<OrderPojo> chestPainOrders = new ArrayList<OrderPojo>();
		if(chestPainFlg){
//			System.out.println("newParm---"+newParm);
			if(newParm.getCount("ORDER_CODE") > 0){
				cpNew.append("''");
				cpOld.append("''");
				String cp = cpOld.toString()+","+cpNew.toString();
				System.out.println("order_code in ==="+cp.toString());
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
		
		for (OrderPojo o :chestPainOrders) {
			System.out.println(o.getOrderCode()+"=="+o.getSysPhaClass1()+"=="+o.getSysPhaClass2()+"==");
		}
		
		hisPojo.setChestpainOrderPojos(chestPainOrders);
		
		sql = "select * from MED_LIS_RPT where APPLICATION_NO in ("
				+ sb.toString() + ") ";

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

		hisPojo.setExaPojos(exaPojos);
		
		
		List<ErdPojo> erdPojos = new ArrayList<ErdPojo>();
		
//		ErdPojo erdPojo;	
//		sql = "  SELECT X.MONITOR_ITEM_EN, X.MONITOR_VALUE, X.MEASURE_UNIT" +
//				" FROM (  SELECT A.MONITOR_ITEM_EN, A.MONITOR_VALUE, B.MEASURE_UNIT" +
//				" FROM ERD_CISVITALSIGN A, CRP_VITAL_CONFIG B" +
//				" WHERE A.CASE_NO = '" + reg.caseNo() + "' AND B.MEASURE_CODE = A.MONITOR_ITEM_EN" +
//				" ORDER BY A.MONITOR_TIME DESC) X" +
//				" WHERE ROWNUM <= 1" +
//				" ORDER BY ROWNUM DESC";
//		TParm erdParm = new TParm(TJDODBTool.getInstance().select(sql));
//		for (int i = 0; i < erdParm.getCount(); i++) {
//			erdPojo = new ErdPojo();
//			erdPojo.setId(sysUtil.generateShortUuid());
//			erdPojo.setMonitorItemEn(erdParm.getValue("MONITOR_ITEM_EN",i));
//			erdPojo.setMonitorValue(erdParm.getDouble("MONITOR_VALUE", i));
//			erdPojo.setMeasureUnit(erdParm.getValue("MEASURE_UNIT", i));
//			erdPojos.add(erdPojo);
//		}
		hisPojo.setErdPojos(erdPojos);
		
//		sql="SELECT DRUG_TYPE,DRUGORINGRD_CODE FROM OPD_DRUGALLERGY " +
//				" WHERE MR_NO='"+ pat.getMrNo() +"'";
//		TParm aParm = new TParm(TJDODBTool.getInstance().select(sql));
//		for (int i = 0; i < aParm.getCount(); i++) {
//			 allergyPojo = new AllergyPojo();
//			 allergyPojo.setId(sysUtil.generateShortUuid());
//			 allergyPojo.setDrugType(aParm.getValue("DRUG_TYPE", i));
//			 allergyPojo.setDrugoringrdCode(aParm.getValue("DRUGORINGRD_CODE", i));
//			 allergyPojos.add(allergyPojo);
//		}
		hisPojo.setAllergyPojos(allergyPojos);

		return hisPojo;
	}
	
	private boolean getChestPain(String caseNo){
		String sql = "SELECT ENTER_ROUTE, PATH_KIND FROM REG_PATADM WHERE "
				+ " CASE_NO = '"+caseNo+"'"
				+ " AND ENTER_ROUTE='E02'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			return true;
		}
		return false;
	}
	
	private String getSysPhaClass(String orderCode,String sysPhaClass){
		String sql = "SELECT SYS_PHA_CLASS FROM SYS_FEE WHERE ORDER_CODE IN ("+orderCode+") AND SYS_PHA_CLASS='"+sysPhaClass+"'";
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
	
	private String getSysPhaClass(String orderCode){
		String sql = "SELECT SYS_PHA_CLASS FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String re = "";
		if(parm.getCount() > 0){
			re = parm.getValue("SYS_PHA_CLASS", 0);
		}
		return re;
	}


	@Override
	public boolean fireRules(OdiObject odiObject, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateCkbLog(TParm updateParm) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onCdssCal(boolean flg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean fireRulesOrder() {
		// TODO Auto-generated method stub
		HisPojo hisPojo = gethispHisPojoOrder(odoMainControl.reg,
				odoMainControl.pat, odoMainControl.odo);
		
		TParm returnParm = new TParm();
		
		TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);

//		hisPojo = CDSSClient.getInstance().fireRules1(hisPojo);
		returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
				"fireRule6", inParm);

		
		hisPojo = sysUtil.parseTParmToHisPojo(returnParm);

		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();

		if(advicePojos.size() == 0){
			return false;
		}
		
		TParm parm = new TParm();
		
		for (AdvicePojo advicePojo : advicePojos) {

			parm.addData("REMARK", advicePojo.getRemarks());
			parm.addData("ORDER_CODE", advicePojo.getOrderCode());
			parm.addData("MEDI_QTY", advicePojo.getMedQty());

		}
		
		System.out.println("CDSS-----"+parm); 
		
		String filter = odoMainControl.odo.getOpdOrder().getFilter();
		
		if(parm.getCount("ORDER_CODE") > 0){
			
	    	String rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
	    	System.out.println("cdss_---rxNo----"+rxNo);
	    	
//	    	odoMainControl.odo.getOpdOrder().setFilter("RX_NO='" + rxNo + "'");
//	    	odoMainControl.odo.getOpdOrder().filter();
	    	
	    	String[] ctz = new String[3];
	    	ctz[0] = odoMainControl.reg.getCtz1Code();
	    	ctz[1] = odoMainControl.reg.getCtz2Code();
	    	ctz[2] = odoMainControl.pat.getCtz3Code();
	    	
	    	for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {	
	    		System.out.println(i+"-----"+parm.getBoolean("REMARK", i));
				if(parm.getBoolean("REMARK", i)){
					if(getOrderCode(odoMainControl.reg.caseNo(),parm.getValue("ORDER_CODE", i)) > 0){
						continue;
					}
				}
				
				int row = odoMainControl.odo.getOpdOrder().newOrder("7", rxNo);
				
//				int row = -1;
//				if (!StringUtil.isNullString(odoMainControl.odo.getOpdOrder().getItemString(
//						odoMainControl.odo.getOpdOrder().rowCount() - 1, "ORDER_CODE"))) {
//					row = odoMainControl.odo.getOpdOrder().newOrder("7", rxNo);
//				} else {
//					row = odoMainControl.odo.getOpdOrder().rowCount() - 1;
//				}
				
				System.out.println("row============="+row);
				
				odoMainControl.odo.getOpdOrder().itemNow = true;
				odoMainControl.odo.getOpdOrder().sysFee.setFilter("ORDER_CODE='"
						+ parm.getValue("ORDER_CODE", i) + "'");
				odoMainControl.odo.getOpdOrder().sysFee.filter();
				TParm sysFeeParm = odoMainControl.odo.getOpdOrder().sysFee.getRowParm(0);
				String orderCode = sysFeeParm.getValue("ORDER_CODE");
				odoMainControl.odo.getOpdOrder().newOpOrder(rxNo, orderCode, ctz, row);
				
				// 判断模板传回的信息中是否有 执行科室
				// 如果有执行科室 那么用模板中的执行科室
				String execDept = "";
				 if (!StringUtil.isNullString(sysFeeParm
						.getValue("EXEC_DEPT_CODE"))) {
					execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
				} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
					execDept = Operator.getDept();
				}
				odoMainControl.odo.getOpdOrder().setItem(row, "EXEC_DEPT_CODE", execDept); // 执行科室
				odoMainControl.odo.getOpdOrder().setItem(row, "ORDER_DESC", sysFeeParm.getValue("ORDER_DESC")
						.replaceFirst(
								"(" + sysFeeParm.getValue("SPECIFICATION") + ")",
								"")); // 医嘱名称
				odoMainControl.odo.getOpdOrder().setItem(row, "CTZ1_CODE", ctz[0]);
				odoMainControl.odo.getOpdOrder().setItem(row, "CTZ2_CODE", ctz[1]);
				odoMainControl.odo.getOpdOrder().setItem(row, "CTZ3_CODE", ctz[2]);
				odoMainControl.odo.getOpdOrder().itemNow = false; // 开启总量计算
				odoMainControl.odo.getOpdOrder().setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
				odoMainControl.odo.getOpdOrder().setItem(row, "FREQ_CODE", "STAT");
				odoMainControl.odo.getOpdOrder().setItem(row, "DOSAGE_UNIT", odoMainControl.odo.getOpdOrder().getItemData(row, "MEDI_UNIT"));
				odoMainControl.odo.getOpdOrder().setItem(row, "NS_BLOOD_COLL_EXEC_FLG", "Y");
				odoMainControl.odo.getOpdOrder().setItem(row, "INSPAY_TYPE", "");
				odoMainControl.odo.getOpdOrder().setItem(row, "PHA_TYPE", "");
				odoMainControl.odo.getOpdOrder().setItem(row, "DOSE_TYPE", "");
				odoMainControl.odo.getOpdOrder().setItem(row, "LINKMAIN_FLG", "N");				
				odoMainControl.odo.getOpdOrder().setItem(row, "EXEC_DR_CODE", Operator.getID());
				
				odoMainControl.odo.getOpdOrder().setItem(row, "DISCOUNT_RATE", BIL.getOwnRate(ctz[0],
						ctz[1], ctz[2], sysFeeParm
								.getValue("CHARGE_HOSP_CODE"), sysFeeParm
								.getValue("ORDER_CODE")));
				
				odoMainControl.odo.getOpdOrder().setActive(row, true);
	
			}

		}
		
		odoMainControl.odo.getOpdOrder().setFilter(filter);
		odoMainControl.odo.getOpdOrder().filter();
		odoMainControl.odo.getOpdOrder().showDebug();
		
		TParm allParm = odoMainControl.odo.getOpdOrder().getBuffer(OpdOrder.PRIMARY);
		TParm insertParm = new TParm();
		for (int i = 0; i < allParm.getCount(); i++) {
			if(allParm.getValue("ORDER_CODE", i).length() != 0 && 
					allParm.getBoolean("#NEW#", i)){				
				insertParm.addRowData(allParm, i);
			}
			
			
		}
		
		System.out.println("insertParm----"+insertParm);
		
		return false;
	}
	
	private int getOrderCode(String caseNo,String ordercode){
		String sql = "SELECT CASE_NO FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' AND ORDER_CODE='"+ordercode+"'";
		System.out.println("sql----"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("count======="+parm.getCount());
		return parm.getCount();
	}
	
	private TParm getSysFee(String orderCode){
		String sql = "SELECT  OPTITEM_CODE,TRANS_HOSP_CODE,DEV_CODE FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			return parm.getRow(0);
		}
		
		TParm re = new TParm();
		re.setData("OPTITEM_CODE", "");
		re.setData("TRANS_HOSP_CODE", "");
		re.setData("DEV_CODE", "");
		return re;
		
	}
	
	
	private HisPojo gethispHisPojoOrder(Reg reg, Pat pat, ODO odo) {
		List<String> orderCodes = CDSSConfig.getInstance().getOrderCodeList();

		Timestamp sysTs = SystemTool.getInstance().getDate();

		String sysDateStr = sysUtil.getDateStr(sysTs);

		String sysTimeStr = sysUtil.getTimeStr(sysTs);

		HisPojo hisPojo = new HisPojo();
		
		hisPojo.setAdmType(reg.getAdmType());

		hisPojo.setSex(pat.getSexString());

		hisPojo.setAge(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[0]));
		
		hisPojo.setAgeMonth(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[1]));
		
		hisPojo.setAgeDay(Integer.valueOf(DateUtil.CountAgeByTimestamp(pat
				.getBirthday(), SystemTool.getInstance().getDate())[2]));

		List<String> diags = new ArrayList<String>();

		hisPojo.setDiags(diags);
		
		
		if(pat.getLMPDate() != null){
			hisPojo.setLmpFlg("Y");
		}
		
		
		
		List<AllergyPojo> allergyPojos = new ArrayList<AllergyPojo>();		
		
		
		OrderPojo orderPojo;

		List<OrderPojo> orders = new ArrayList<OrderPojo>();
		List<ExaPojo> exaPojos = new ArrayList<ExaPojo>();

		OpdOrder opdOrder = odo.getOpdOrder();

		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();
		
		TParm allParm = opdOrder.getBuffer(OpdOrder.PRIMARY);
		
		for (int i = 0; i < allParm.getCount(); i++) {
			if(allParm.getValue("ORDER_CODE", i).length() != 0 && 
					allParm.getBoolean("#NEW#", i)){
				
				if (opdOrder.getItemString(i, "ORDER_CODE").length() > 0) {
					if(orderCodes.contains(allParm.getValue("ORDER_CODE", i))){
						orderPojo = new OrderPojo();
						orderPojo.setId(sysUtil.generateShortUuid());
						orderPojo.setOrderCode(allParm.getValue("ORDER_CODE", i));
						orderPojo.setMedQty(allParm.getDouble("MEDI_QTY", i));
						orderPojo.setUnit(allParm.getValue("MEDI_UNIT", i)
								.length() == 0 ? null : allParm.getValue("MEDI_UNIT", i));
						orderPojo.setTakeDays(allParm.getInt("TAKE_DAYS", i));
						orderPojo.setFreqCycle(new Integer(getFreqCycle(allParm.getValue("FREQ_CODE", i))));
						orderPojo.setFreqTimes(new Integer(getFreqFreqTimes(allParm.getValue("FREQ_CODE", i))));
						orderPojo.setSysDateLong(sysUtil.parseDateStr2Long(sysDateStr));
						orderPojo.setSysTimeLong(sysUtil.parseTimeStr2Long(sysTimeStr));
						try {
							orderPojo.setOrderDateLong(sysUtil
									.parseDateStr2Long(sysUtil.getDateStr(allParm.getTimestamp("ORDER_DATE", i))));
							orderPojo.setOrderTimeLong(sysUtil
									.parseTimeStr2Long(sysUtil.getTimeStr(allParm.getTimestamp("ORDER_DATE", i))));
						} catch (Exception e) {
							// TODO: handle exception
						}
						orderPojo.setLiquidNo(allParm.getValue("LINK_NO", i));
						orderPojo.setRxNo(allParm.getValue("RX_NO", i));
						orderPojo.setSeqNo(allParm.getValue("SEQ_NO", i));
						
						TParm sysFeeParm = getSysFee(allParm.getValue("ORDER_CODE", i));
						orderPojo.setOptitemCode(sysFeeParm.getValue("OPTITEM_CODE"));
						orderPojo.setTransHospCode(sysFeeParm.getValue("TRANS_HOSP_CODE"));
						orderPojo.setDevCode(sysFeeParm.getValue("DEV_CODE"));
						
						orders.add(orderPojo);

					}
					
				}
				
				
			}

			
		}
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();


		hisPojo.setOrderPojos(orders);

		hisPojo.setExaPojos(exaPojos);
		
		
		List<ErdPojo> erdPojos = new ArrayList<ErdPojo>();

		hisPojo.setErdPojos(erdPojos);

		hisPojo.setAllergyPojos(allergyPojos);

		return hisPojo;
	}
	

}
