package com.javahis.ui.odi;


import java.util.ArrayList;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.javahis.ui.opd.CDSSStationDrools;
import jdo.cdss.AdvicePojo;
import jdo.cdss.CDSSConfig;
import jdo.cdss.HisPojo;
import jdo.cdss.OrderPojo;
import jdo.odi.OdiObject;



public class ODISingleExeDrools extends CDSSStationDrools{

	public ODISingleExeControl odiSingleExeControl;
	private static final String URL = "%ROOT%\\config\\drools\\ODISingleExeCdssUI.x";

	public ODISingleExeDrools(ODISingleExeControl odiSingleExeControl) {
		this.odiSingleExeControl = odiSingleExeControl;
	}	
	/**
	 * ÷¥––πÊ‘Ú
	 * 
	 * @return
	 */
	public boolean fireRules() {
	    HisPojo hisPojo = gethispHisPojo();		
		TParm returnParm = new TParm();		
		hisPojo.setAdmType("I");		
		TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);
//		System.out.println("inParm======"+inParm);
		returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
				"fireRule7", inParm);
//		System.out.println("returnParm======"+returnParm);
		hisPojo = sysUtil.parseTParmToHisPojo(returnParm);
//		System.out.println("hisPojo======1");
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();	
		if (advicePojos.size() == 0) {
			return false;
		}
//		System.out.println("hisPojo======2");
		TParm parm = new TParm();
		List<String> ids = new ArrayList<String>();
		for (AdvicePojo advicePojo : advicePojos) {
//			System.out.println("AdvicePojo======1");
			if(!ids.contains(advicePojo.getKnowladgeId())){
				parm.addData("ID", advicePojo.getKnowladgeId());
				parm.addData("ORDER_DESC", advicePojo.getOrderDesc());
				parm.addData("ADVICE", advicePojo.getAdviceText());
//				System.out.println("parm==="+parm);
				ids.add(advicePojo.getKnowladgeId());
			}
		}					
		this.odiSingleExeControl.openDialog(URL, parm);	
		return true;

	}

	private HisPojo gethispHisPojo() {		
		List<String> SingleExeOrderCode = CDSSConfig.getInstance().getSingleExeOrderCodes();
		TTable table = (TTable) odiSingleExeControl.getComponent("tableM");
		TParm Parm = table.getParmValue();
//		System.out.println("Parm=="+Parm);
		HisPojo hisPojo = new HisPojo();
		List<OrderPojo> orders = new ArrayList<OrderPojo>();
		for (int i = 0; i < Parm.getCount(); i++) {
		if(SingleExeOrderCode.contains(Parm.getValue("ORDER_CODE", i))){
			OrderPojo orderPojo = new OrderPojo();
			orderPojo.setId(sysUtil.generateShortUuid());
			orderPojo.setOrderCode(Parm.getValue("ORDER_CODE", i));
			orderPojo.setOrderDesc(Parm.getValue("ORDER_DESC", i));	
			orders.add(orderPojo);
		}			
	}
		hisPojo.setSingleExeOrderPojos(orders);		
		return hisPojo;
	}

	@Override
	public void onCdssCal(boolean flg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCkbLog(TParm updateParm) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean fireRules(OdiObject odiObject, int i) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean fireRulesOrder() {
		// TODO Auto-generated method stub
		return false;
	}
		
	
}
