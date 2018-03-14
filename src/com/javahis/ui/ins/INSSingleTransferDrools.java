package com.javahis.ui.ins;


import java.util.ArrayList;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.javahis.ui.opd.CDSSStationDrools;
import jdo.cdss.AdvicePojo;
import jdo.cdss.CDSSConfig;
import jdo.cdss.HisPojo;
import jdo.cdss.OrderPojo;
import jdo.odi.OdiObject;



public class INSSingleTransferDrools extends CDSSStationDrools{

	public INSSingleTransferControl insSingleTransferControl;
	public INSSingleTransferDrools(INSSingleTransferControl insSingleTransferControl) {
		this.insSingleTransferControl = insSingleTransferControl;
	}	
	/**
	 * ÷¥––πÊ‘Ú
	 * 
	 * @return
	 */
	public boolean fireRules() {
	    gethispHisPojo();							
		return true;

	}

	private void gethispHisPojo() {		
		List<String> SingleTransferCode = CDSSConfig.getInstance().getSingleTransferCodes();
		TTable table = (TTable) insSingleTransferControl.getComponent("table");
		TParm Parm = table.getParmValue();
//		System.out.println("Parm=="+Parm);
		TParm parmcaseno = new TParm();
		for (int i = 0; i < Parm.getCount(); i++) {
		String sql = 
				" SELECT  A.ORDER_CODE,A.ORDER_CHN_DESC,B.ICD_CODE" +
				" FROM IBS_ORDD A,ADM_INPDIAG B" +
				" WHERE A.CASE_NO = '"+Parm.getValue("CASE_NO",i)+"'" +
				" AND A.CASE_NO  = B.CASE_NO" +
				" AND A.ORDER_CODE IN('M1400014','M1400015','F0400026','F0400044')" +
				" AND B.IO_TYPE = 'O'" +
				" AND B.MAINDIAG_FLG = 'Y'";			
			TParm opParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(opParm.getCount()<=0)
				continue;
			HisPojo hisPojo = new HisPojo();
			List<OrderPojo> orders = new ArrayList<OrderPojo>();
		if(SingleTransferCode.contains(opParm.getValue("ORDER_CODE", 0))){
			OrderPojo orderPojo = new OrderPojo();
			orderPojo.setId(sysUtil.generateShortUuid());
			orderPojo.setOrderCode(opParm.getValue("ORDER_CODE", 0));
			orderPojo.setOrderDesc(opParm.getValue("ICD_CODE", 0));	
			orders.add(orderPojo);
			hisPojo.setSingleExeOrderPojos(orders);
			TParm returnParm = new TParm();		
			hisPojo.setAdmType("I");		
			TParm inParm = sysUtil.parseHisPojoToTParm(hisPojo);
//			System.out.println("inParm======"+inParm);
			returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
					"fireRule8", inParm);
//			System.out.println("returnParm======"+returnParm);
			hisPojo = sysUtil.parseTParmToHisPojo(returnParm);
//			System.out.println("hisPojo======1");
			List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();	
			if (advicePojos.size() == 0) {
				return;
			}
//			System.out.println("hisPojo======2");		
			for (AdvicePojo advicePojo : advicePojos) {
//				System.out.println("AdvicePojo======1");
				if(advicePojo.getAdviceText().equals("TRUE")){
				this.insSingleTransferControl.getSingleParm().
				addData("CASENO", Parm.getValue("CASE_NO",i));
				}
			}						
		}			
	}	
		return;
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
